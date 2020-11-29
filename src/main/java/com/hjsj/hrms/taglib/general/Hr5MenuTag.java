/**
 * 
 */
package com.hjsj.hrms.taglib.general;

import com.hjsj.hrms.businessobject.general.template.TemplateTableParamBo;
import com.hjsj.hrms.businessobject.performance.singleGrade.DirectUpperPosBo;
import com.hjsj.hrms.businessobject.sys.ConstantXml;
import com.hjsj.hrms.businessobject.sys.report.Sys_Oth_Parameter;
import com.hjsj.hrms.businessobject.workplan.WorkPlanUtil;
import com.hjsj.hrms.module.recruitment.parameter.businessobject.ParameterXMLBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.frame.utility.AdminDb;
import com.hrms.hjsj.sys.EncryptLockClient;
import com.hrms.hjsj.sys.IResourceConstant;
import com.hrms.hjsj.sys.VersionControl;
import com.hrms.struts.constant.SystemConfig;
import com.hrms.struts.constant.WebConstant;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.valueobject.UserView;
import org.apache.commons.lang.StringUtils;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.xpath.XPath;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.BodyTagSupport;
import java.io.FileInputStream;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * <p>Title:HJ-ehr5.0主菜单标签</p>
 * <p>Description:</p>
 * <p>Company:hjsj</p>
 * <p>create time:Jun 10, 2008:4:00:33 PM</p>
 * @author chenmengqing
 * @version 1.0
 * 
 */
public class Hr5MenuTag extends BodyTagSupport {
	
	/**
	 * 输出js变量名称
	 */
	private String name;
	private String fromnode;
	public Hr5MenuTag() {
        super();
	}
	
	  private boolean haveFuncPriv(String function_id,String module_id)
	  {
	      boolean bfunc=true,bmodule=true;
	      
	      if("27015".equalsIgnoreCase(function_id)|| "0C348".equalsIgnoreCase(function_id)) //如果没有在考情参数中设置 请假、公出、调休业务模板，不出现业务办理菜单
	      {
	    	  Connection connection=null;
	    	  try
	    	  {
	    		  connection = (Connection) AdminDb.getConnection();
		    	  TemplateTableParamBo tp=new TemplateTableParamBo(connection); 
				  if(!tp.isDefineKqParam())
					  return false;
				  
	    	  }
	    	  catch(Exception e)
	    	  {
	    		  
	    	  }
	    	  finally
	    	  {
	    		  try
	    		  {
	    			  if(connection!=null)
	    				  connection.close();
	    		  }
	    		  catch(Exception ee)
	    		  {
	    			  ee.printStackTrace();
	    		  }
	    	  }	
	      }
	      
	      /**
	       * 在这里进行权限分析
	       */
	       /**版本功能控制*/
	      VersionControl ver_ctrl=new VersionControl();	
	      UserView userview=(UserView) pageContext.getSession().getAttribute(WebConstant.userView);	      
          EncryptLockClient lock=(EncryptLockClient)pageContext.getServletContext().getAttribute("lock");
	      ver_ctrl.setVer(lock.getVersion());

          if(!(module_id==null|| "".equals(module_id)))
          {
        	String[] modules =StringUtils.split(module_id,",");
            for(int i=0;i<modules.length;i++)
            {
            	module_id=modules[i];
            	bmodule=lock.isBmodule(Integer.parseInt(module_id),userview.getUserName());
            	if(bmodule)
            		break;
            }

          }	
          
          if("9A0".equals(function_id)){//仅针对c+b，自助模块就不用显示工具箱
        	  bmodule=lock.isBmodule(11,userview.getUserName());
          }
	      
          if(!(function_id==null|| "".equals(function_id)))
          {	      
        	  String[] funcs =StringUtils.split(function_id,","); 
        	  for(int i=0;i<funcs.length;i++)
        	  {
        		  bfunc=ver_ctrl.searchFunctionId(funcs[i],userview.hasTheFunction(funcs[i]))&&haveVersionFunc(funcs[i], lock.getVersion_flag());
        		  if(bfunc)
        			  break;
        	  }   
         }
		 return (bfunc&bmodule);
	  }	
	  
	  /**
	     * 标准版、专业版功能区分
	     * @param funcid
	     * @param ver_s =1专业版 =0标准版
	     * @return
	     */
	    private boolean haveVersionFunc(String funcid,int ver_s)
	    {
	    	UserView userview=(UserView) pageContext.getSession().getAttribute(WebConstant.userView);	  
	    	return PubFunc.haveVersionFunc(userview, funcid, ver_s);
	    }
	/**
	 * 输出子节点的内容
	 * 	<menu id="0101" url="" param="" icon="" func_id="" target="">ssss</menu>
	 * @param parent
	 * @return
	 */
	private String outChildJs(Element parent,int layer)
	{
		StringBuffer buf=new StringBuffer();
	    UserView userview=(UserView) pageContext.getSession().getAttribute(WebConstant.userView);
	    if(fromnode!=null && !"".equals(fromnode)){
	    	
	    }
	    String func_id_p=parent.getAttributeValue("func_id");
		buf.append("{");
		buf.append("text:'");
		buf.append(parent.getAttributeValue("name"));
		buf.append("' ");
		String tmp=parent.getAttributeValue("icon");
		
		String id=parent.getAttributeValue("id");//0501
		if(!(tmp==null||tmp.length()==0))
		{
			buf.append(",icon:'");
			buf.append(tmp);
			buf.append("'");
		}else{
			 if(fromnode!=null && "20".equals(fromnode)&&layer==1){//epm绩效平台，导航菜单上显示图片
				 buf.append(",icon:'");
				 buf.append("menuicon.gif");
				 buf.append("'");
			  }
		}
		
    	/**超链上补参数*/
    	if("010101".equalsIgnoreCase(func_id_p) || "010201".equalsIgnoreCase(func_id_p) || "010301".equalsIgnoreCase(func_id_p))
    	{
    		tmp=parent.getAttributeValue("url");
    		tmp=tmp+"&amp;userbase="+userview.getDbname();
    		parent.setAttribute("url", tmp);
    	}	
    	
		tmp=parent.getAttributeValue("url");
	if("030101".equalsIgnoreCase(func_id_p)&& "20030101".equalsIgnoreCase(id))
	{
		Connection connection=null;
		String browse_photo="";
		try
		{
		   connection = (Connection) AdminDb.getConnection();
		   Sys_Oth_Parameter sysbo=new Sys_Oth_Parameter(connection);
		   browse_photo=sysbo.getValue(Sys_Oth_Parameter.BROWSE_PHOTO);	
		   browse_photo=browse_photo!=null&&browse_photo.length()>0?browse_photo:"0";//0默认为表格信息，1照片显示	
		}catch(Exception e)
		{
		}finally
		{		
		    try
		    {
			  if(connection!=null)
			      connection.close();
		    } catch (SQLException e)
		    {
			e.printStackTrace();			
		    }	
		}
		if(browse_photo!=null&& "1".equals(browse_photo))
	    		tmp="/workbench/browse/showphoto.do?b_search=link&amp;action=showphotodata.do&amp;target=nil_body&amp;userbase=usr&amp;flag=noself&amp;isUserEmploy=0";
		else
		        tmp="/workbench/browse/showinfo.do?b_search=link&amp;action=showinfodata.do&amp;target=nil_body&amp;userbase=&amp;flag=noself&amp;isUserEmploy=0&amp;isphotoview=";
	}
	if("060601".equalsIgnoreCase(func_id_p)&&("20110403".equalsIgnoreCase(id)|| "030401".equalsIgnoreCase(id)))
	{
	    DirectUpperPosBo bo=new DirectUpperPosBo();
	    String flag=bo.getGradeFashion("0");
	    if("1".equals(flag))
		tmp="/selfservice/performance/batchGrade.do?b_query=link&amp;model=0&amp;linkType=1&amp;returnflag=menu";
	    else
		tmp="/selfservice/performance/batchGrade.do?b_tileFrame=link&amp;model=0&amp;linkType=1&amp;planContext=all&amp;returnflag=menu";
	}
	/* zxj 20160613 考勤自助不区分标准版专业版
	 //----- 郑文龙
	if(func_id_p.equalsIgnoreCase("0B")&&id.equalsIgnoreCase("2008"))
	{
		int flag= userview.getVersion_flag();
	    if(flag == 0)
	    	tmp="";
	}*/
	String url_to_use = "";
	String target_to_use = "";
	String validateFlag = "0";
	String validate = parent.getAttributeValue("validate");
	String validateHide = "";
	if(!(validate==null||validate.length()==0) && "true".equalsIgnoreCase(validate))
	{
		validateFlag = "1";
	}
	if(!(tmp==null||tmp.length()==0))
	{
		//将url链接参数加密为一个参数encryptParam  xuj add 2014-9-2  start
		int index = tmp.indexOf("&");
		if(index>-1){
			String allurl = tmp.substring(0,index);
			String allparam = tmp.substring(index);
			tmp=allurl+"&encryptParam="+PubFunc.encrypt(allparam);
		}
		//将url链接参数加密为一个参数encryParam  xuj add 2014-9-2  end
		
		if("1".equals(validateFlag)){
			url_to_use = tmp;//保存连接
		}else{
			buf.append(",href:'");
			buf.append(tmp);
			buf.append("'");
		}
		
	}	
				
		//人员管理设置业务日期特殊处理
		if("260".equalsIgnoreCase(func_id_p)&& "0306".equalsIgnoreCase(id))
		{
			String js="var thecodeurl ='/gz/gz_accounting/setapp_date.do?b_query=link'; var return_vo= window.showModalDialog(thecodeurl, '', 'dialogWidth:400px; dialogHeight:340px;resizable:no;center:yes;scroll:yes;status:yes');";
			buf.append(",listeners:{click:function (node,checked){"+js+"}}");
		}
		tmp=parent.getAttributeValue("target");
		if(!(tmp==null||tmp.length()==0))
		{
			//xuj add 业务平台支持显示领导桌面模块 由于领导桌面使用的target不是il_body不适合业务平台框架显示，特殊转换一下
			if(id.startsWith("21")){
				tmp="il_body";
			}
			if("1".equals(validateFlag)){
				target_to_use = tmp;//保存target
			}else{
				buf.append(",hrefTarget:'");
				buf.append(tmp);
				buf.append("'");
			}
		}
		EncryptLockClient lockclient = (EncryptLockClient)this.pageContext.getServletContext().getAttribute("lock");
        int version = lockclient.getVersion();
		tmp=parent.getAttributeValue("hide");
		if(!(tmp==null||tmp.length()==0))
		{
		  //2015-12-04  chenxg  add  v70以上版本模块二次认证不起作用
			if("1".equals(validateFlag) && 70 > version){
				validateHide = tmp;
			}else{
				buf.append(",hide:");
				buf.append(tmp);
				buf.append("");
			}
		}
		
		/**应用模式*/
		tmp=parent.getAttributeValue("app");
		if(!(tmp==null||tmp.length()==0))
		{
			buf.append(",app:");
			buf.append(tmp);
			buf.append("");
		}	
		
		/**CS应用业务模块号*/
		tmp=parent.getAttributeValue("mod_id");
		if(!(tmp==null||tmp.length()==0))
		{
			/**自助服务平台0,1,2,3,4...*/
			if(tmp.indexOf(",")==-1)
			{
				buf.append(",mod_id:");
				buf.append(tmp);
				buf.append("");
			}
		}		
		
		//子节点
		List list=parent.getChildren();
		//zxj 20170721 招聘设置-参数设置的子菜单作为paramMenu方式使用，不用显示在左侧菜单中
        if("3110601".equals(func_id_p)|| list.size()==0)
		{
			buf.append(",leaf:true");
		}
		else
		{
			boolean bflag=false;
			StringBuffer childjs=new StringBuffer();
			for(int i=0;i<list.size();i++)
			{
				Element child=(Element)list.get(i);
	        	String func_id=child.getAttributeValue("func_id");
	        	String mod_id=child.getAttributeValue("mod_id");
	        	String ceo_id=child.getAttributeValue("id");
	        	String menuhide=child.getAttributeValue("menuhide");
	        	String url=child.getAttributeValue("url");
	        	
	        	if(!PubFunc.hasPriMenu(func_id,url,userview)) //根据锁版本号控制人事异动or薪资的新旧程序
	        		continue;
	        	
	        	//如果是考核计划，并且只有最后两个菜单权限，考核计划不显示
	    	    boolean khPlanFlag = true;
	    		if(SystemConfig.getPropertyValue("clientName")!=null&& "hkyh".equalsIgnoreCase(SystemConfig.getPropertyValue("clientName").trim())){
	    			if("32602".equals(func_id)){//2013.11.11 pjf
	    				khPlanFlag = false;
	    				StringBuffer sb = userview.getFuncpriv();
	    				String[] tempArray = sb.toString().split(",");
	    				for(int p=0 ; p<tempArray.length ; p++){
	    					if("3260201".equals(tempArray[p]) || "3260202".equals(tempArray[p]) || "3260203".equals(tempArray[p]) || "3260204".equals(tempArray[p]) || "3260205".equals(tempArray[p]) || "3260206".equals(tempArray[p])){
	    					     // todo
	    						khPlanFlag = true;
	    					}
	    				}
	    				if(userview.isAdmin())
	    					khPlanFlag = true;
	    			}
	    		}
	    		if(!khPlanFlag)
	    			continue;
	    		if("false".equalsIgnoreCase(menuhide))
	        		continue;
	    		/******************当不启动简历解析服务时，主菜单栏不显示简历解析栏  zzk2013/11/14*************************/
	    		if("04024".equals(ceo_id)){
	    			Connection connection=null;
	    			try {
	    				connection = (Connection) AdminDb.getConnection();
	    				ParameterXMLBo parameterXMLBo=new ParameterXMLBo(connection);
	    				HashMap map=parameterXMLBo.getAttributeValues();
	    				if(map!=null&&map.get("startResumeAnalysis")!=null)
	    				{
	    					String startResumeAnalysis=(String)map.get("startResumeAnalysis");
	    					HashMap resumeAnalysisMap=(HashMap) map.get("resumeAnalysisMap");
	    					if(!"1".equals(startResumeAnalysis))
	    						continue;
	    					if(resumeAnalysisMap==null||resumeAnalysisMap.get("resumeAnalysisName")==null||resumeAnalysisMap.get("resumeAnalysisName").toString().trim().length()==0)
	    						continue;;
	    				}else{
	    					continue;
	    				}
	    			} catch (GeneralException e) {
	    				// TODO Auto-generated catch block
	    				e.printStackTrace();
	    			}finally{
	    				if(connection!=null)
							try {
								connection.close();
							} catch (SQLException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
	    			}

	    		}
	    		
	        	if("01030106".equals(func_id))
	        	{
	        		String inputchinfor="";
	        		String approveflag="";
	        		Connection connection=null;
	        		try
	        		{
	        		   connection = (Connection) AdminDb.getConnection();
	        		   Sys_Oth_Parameter sysbo=new Sys_Oth_Parameter(connection);
	        		   inputchinfor=sysbo.getValue(Sys_Oth_Parameter.INPUTCHINFOR);
	        		   approveflag=sysbo.getValue(Sys_Oth_Parameter.APPROVE_FLAG);
	        		}catch(Exception e)
	        		{
	        		}finally
	        		{
	        		  if(connection!=null)
				    try
				    {
					connection.close();
				    } catch (SQLException e)
				    {
					// TODO Auto-generated catch block
					e.printStackTrace();
				    }	
	        		}
	        		inputchinfor=inputchinfor!=null&&inputchinfor.trim().length()>0?inputchinfor:"1";
	  			    approveflag=approveflag!=null&&approveflag.trim().length()>0?approveflag:"1";
	  						  
	  			    if("1".equals(inputchinfor)&& "1".equals(approveflag)&&!"su".equalsIgnoreCase(userview.getUserName()))
	  			    {
	  			       if(haveFuncPriv(func_id,mod_id))
	  			      {
	  	        		bflag=true;
	  	        		if(childjs.length()>0)
	  	        			childjs.append(",");
	  	        		childjs.append(outChildJs(child,++layer));
	  			      }
	  			    }
	        	}else if("27020".equals(func_id))//员工明细数据,判断如果没有日明细主要业务则将跳转到月汇总页面中,考勤休假
	        	{
	        		VersionControl ver_ctrl=new VersionControl();		      	              
	                EncryptLockClient lock=(EncryptLockClient)pageContext.getServletContext().getAttribute("lock");
	      	        ver_ctrl.setVer(lock.getVersion());
	      	        String[] funcs =StringUtils.split("2702011,2702010,2702020,2702021,2702024,2702032",","); //人员变动比对，生成日考勤明细表，统计，算，个人业务处理，浏览日明细
	      	        boolean bfunc=false;
	        	    for(int r=0;r<funcs.length;r++)
	        	    {
	        		  bfunc=ver_ctrl.searchFunctionId(funcs[r],userview.hasTheFunction(funcs[r]));
	        		  if(bfunc)
	        			  break;
	        	    } 
	        	    if(!bfunc&&ver_ctrl.searchFunctionId("2702030",userview.hasTheFunction("2702030")))//浏览权限中有浏览月汇总
	        	    {
	        	    	child.setAttribute("url","/kq/register/select_collect.do?b_search=link&amp;action=select_collectdata.do&amp;target=mil_body&amp;flag=noself&amp;returnvalue=");
	  	    	    }
	        	    
	        	    if(haveFuncPriv(func_id,mod_id))
		        	{
		        		bflag=true;
		        		if(childjs.length()>0)
		        			childjs.append(",");
		        		childjs.append(outChildJs(child,++layer));
		        	}
	        	    
	        	}else if("0C31".equals(func_id))//员工明细数据,判断如果没有日明细主要业务则将跳转到月汇总页面中,部门考勤
	        	{
	        		VersionControl ver_ctrl=new VersionControl();		      	              
	                EncryptLockClient lock=(EncryptLockClient)pageContext.getServletContext().getAttribute("lock");
	      	        ver_ctrl.setVer(lock.getVersion());
	      	        String[] funcs =StringUtils.split("0C3105,0C3100,0C3120,0C3121,0C3123,0C3113",","); 
	      	        boolean bfunc=false;
	        	    for(int r=0;r<funcs.length;r++)
	        	    {
	        		  bfunc=ver_ctrl.searchFunctionId(funcs[r],userview.hasTheFunction(funcs[r]));
	        		  if(bfunc)
	        			  break;
	        	    } 
	        	    if(!bfunc&&ver_ctrl.searchFunctionId("0C3110",userview.hasTheFunction("0C3110")))//浏览权限中有浏览月汇总
	        	    {
	        	    	child.setAttribute("url","/kq/register/select_collect.do?b_search=link&amp;action=select_collectdata.do&amp;target=mil_body&amp;flag=noself&amp;returnvalue=");
	  	        	}
	        	    if(haveFuncPriv(func_id,mod_id))
		        	{
		        		bflag=true;
		        		if(childjs.length()>0)
		        			childjs.append(",");
		        		childjs.append(outChildJs(child,++layer));
		        	}
	        	}else
	        	{
	        		if("3800505".equals(func_id)){
	        			HashMap map = new HashMap();
		        		if(haveFuncPriv("3800501", null)||haveFuncPriv("3800502", null)
		        				||haveFuncPriv("3800503", null)||haveFuncPriv("3800504", null)){
		        			Connection conn=null;
		        			try{
		        				conn = (Connection) AdminDb.getConnection();
		        				ConstantXml constantXml = new ConstantXml(conn, "JOBTITLE_CONFIG","params");
		        				List listEl = constantXml.getAllChildren("//templates");
		        				Element template = null;
		        				for (int j = 0; j < listEl.size(); j++) {   
		        					template = (Element) listEl.get(j); //循环依次得到子节点
		        					map.put(template.getAttributeValue("type"), template.getAttributeValue("template_id"));
		     		    	    }
		        			}catch(Exception e){
		        				e.printStackTrace();
		        			}finally{		
		        			    try
		        			    {
		        				  if(conn!=null)
		        					  conn.close();
		        			    } catch (SQLException e){
		        			    	e.printStackTrace();			
		        			    }	
		        			}
		        		}
		        		if(haveFuncPriv("3800501",null)&&StringUtils.isNotEmpty((String)map.get("3"))){
	        				String HaveTemplateid = isHaveTemplateid((String)map.get("3"));
	        				//外语免试备案
	        				if(!"".equals(HaveTemplateid))
	        					childjs.append(outVirtualChildJs(child, ++layer,ResourceFactory.getProperty("zc.label.mianshiPn"),HaveTemplateid));
	        			}
	        			if(haveFuncPriv("3800502",null)&&StringUtils.isNotEmpty((String)map.get("4"))){
	        				String HaveTemplateid = isHaveTemplateid((String)map.get("4"));
	        				if(!"".equals(HaveTemplateid)){
	        					if(childjs.length()>0)
				        			childjs.append(",");
		        				//破格申请审批
	        					childjs.append(outVirtualChildJs(child, ++layer,ResourceFactory.getProperty("zc.label.pogePn"),HaveTemplateid));
	        				}
	        				
	        			}
	        			if(haveFuncPriv("3800511",null)&&StringUtils.isNotEmpty((String)map.get("8"))){
	        				String HaveTemplateid = isHaveTemplateid((String)map.get("8"));
	        				if(!"".equals(HaveTemplateid)){
		        				if(childjs.length()>0)
				        			childjs.append(",");
		        				//预报名申请
		        				childjs.append(outVirtualChildJs(child, ++layer,ResourceFactory.getProperty("zc.label.baomingPn"),HaveTemplateid));
	        				}
	        			}
	        			if(haveFuncPriv("3800504",null)&&StringUtils.isNotEmpty((String)map.get("6"))){
	        				String HaveTemplateid = isHaveTemplateid((String)map.get("6"));
	        				if(!"".equals(HaveTemplateid)){
		        				if(childjs.length()>0)
				        			childjs.append(",");
		        				//材料审查
		        				childjs.append(outVirtualChildJs(child, ++layer,ResourceFactory.getProperty("zc.label.shenbaoCaiPn"),HaveTemplateid));
	        				}
	        			}
	        			if(haveFuncPriv("3800503",null)&&StringUtils.isNotEmpty((String)map.get("5"))){
	        				String HaveTemplateid = isHaveTemplateid((String)map.get("5"));
	        				if(!"".equals(HaveTemplateid)){
		        				if(childjs.length()>0)
				        			childjs.append(",");
		        				//论文送审
		        				childjs.append(outVirtualChildJs(child, ++layer,ResourceFactory.getProperty("zc.label.lunwenSongPn"),HaveTemplateid));
	        				}
	        			}
	        		}else if("0KR020502".equals(func_id)
	        				||"0KR020302".equals(func_id)){//部门计划和部门总结，如果有权限但不是部门领导同样不显示菜单
	        			Connection conn = null;
						try {
							conn = (Connection) AdminDb.getConnection();
							WorkPlanUtil workPlanUtil = new WorkPlanUtil(conn, userview);
	        	            ArrayList deptlist =workPlanUtil.getDeptList(userview.getDbname(), 
	        	                    userview.getA0100());
		        			if(haveFuncPriv(func_id,null) && deptlist.size()>0){
		        				if(childjs.length()>0)
				        			childjs.append(",");
				        		childjs.append(outChildJs(child,++layer));
		        			}else{
		        				continue;
		        			}
						} catch (GeneralException e) {
							e.printStackTrace();
						}finally{
							PubFunc.closeDbObj(conn);
						}
       				
	        		}
	        	    if(haveFuncPriv(func_id,mod_id))
		        	{
		        		bflag=true;
		        		if("0KR020502".equals(func_id)
		        				||"0KR020302".equals(func_id))//部门计划，部门总结跳过
		        			continue;
		        		else{
			        		if(childjs.length()>0)
			        			childjs.append(",");
			        		childjs.append(outChildJs(child,++layer));
		        		}
		        	}
	        	    if("38002".equals(func_id)){
		        		HashMap map = new HashMap();
		        		if(haveFuncPriv("38003", null)||haveFuncPriv("38004", null)){
		        			Connection conn=null;
		        			try{
		        				conn = (Connection) AdminDb.getConnection();
		        				ConstantXml constantXml = new ConstantXml(conn, "JOBTITLE_CONFIG","params");
		        				List listEl = constantXml.getAllChildren("//templates");
		        				Element template = null;
		        				for (int j = 0; j < listEl.size(); j++) {   
		        					template = (Element) listEl.get(j); //循环依次得到子节点
		        					map.put(template.getAttributeValue("type"), template.getAttributeValue("template_id"));
		     		    	    }
		        			}catch(Exception e){
		        				e.printStackTrace();
		        			}finally{		
		        			    try
		        			    {
		        				  if(conn!=null)
		        					  conn.close();
		        			    } catch (SQLException e){
		        			    	e.printStackTrace();			
		        			    }	
		        			}
		        		}
		        		if(haveFuncPriv("38003",null)&&StringUtils.isNotEmpty((String)map.get("1"))){
		        			String HaveTemplateid = isHaveTemplateid((String)map.get("1"));
	        				if(!"".equals(HaveTemplateid)){
	        					childjs.append(",");
	        					//职称认定
			        			childjs.append(outVirtualChildJs(child,1,ResourceFactory.getProperty("zc.label.zhichengPn"),HaveTemplateid));
			        		}
		        		}
		        		if(haveFuncPriv("38004",null)&&StringUtils.isNotEmpty((String)map.get("2"))){
		        			String HaveTemplateid = isHaveTemplateid((String)map.get("2"));
	        				if(!"".equals(HaveTemplateid)){
	        					childjs.append(",");
	        					//考试认定
			        			childjs.append(outVirtualChildJs(child,1,ResourceFactory.getProperty("zc.label.kaoshiPn"),HaveTemplateid));
			        		}
		        		}
		        	}
	        	}	        					
			}//for i loop end.
			
			if(bflag)
			{
				buf.append(",children:[");
				buf.append(childjs);
				buf.append("]");
			}
		}
		
		if("1".equals(validateFlag))
		{
			String validateType = "1";//=1校验密码，=2校验验证码，=1，2两者都验证。默认校验密码
			if(SystemConfig.getPropertyValue("validateType")!=null&&SystemConfig.getPropertyValue("validateType").length()>0)
				validateType=SystemConfig.getPropertyValue("validateType");
			//2015-12-04  chenxg  add  v70以上版本模块二次认证不起作用
//			if(70 > version){
//    			if(validateType.equals("1")){
//    			    String js2="var isCollapse=\""+validateHide+"\";var thecodeurl ='/general/sys/validate/validatePassword.jsp'; var return_vo= window.showModalDialog(thecodeurl, '', 'dialogWidth:470px; dialogHeight:200px;resizable:no;center:yes;scroll:yes;status:yes');";
//    			    js2+="if(return_vo!=null){ var content=return_vo.content;var inputcode=return_vo.inputcode;if(content==inputcode){if(isCollapse=='true')accordion.toggleCollapse(false);document."+target_to_use+".location='"+url_to_use+"';}else{alert(\"密码不正确!\");}}";
//    			    buf.append(",listeners:{click:function (node,checked){"+js2+"}}");
//    			}else if(validateType.equals("2")){
//    			    String js2="var isCollapse=\""+validateHide+"\";var thecodeurl ='/general/sys/validate/secondValidate.do?b_init=link'; var return_vo= window.showModalDialog(thecodeurl, '', 'dialogWidth:470px; dialogHeight:280px;resizable:no;center:yes;scroll:yes;status:yes');";
//    			    js2+="if(return_vo!=null){ var content=return_vo.content;var inputcode=return_vo.inputcode;if(content==inputcode){if(isCollapse=='true')accordion.toggleCollapse(false);document."+target_to_use+".location='"+url_to_use+"'}else{alert(\"校验码不正确!\");}}";
//    			    buf.append(",listeners:{click:function (node,checked){"+js2+"}}");
//    			}else if(validateType.equals("1,2")){
//    			    String js1="var isCollapse=\""+validateHide+"\";var thecodeurl ='/general/sys/validate/validatePassword.jsp'; var return_vo= window.showModalDialog(thecodeurl, '', 'dialogWidth:470px; dialogHeight:200px;resizable:no;center:yes;scroll:yes;status:yes');";
//    			    js1+="if(return_vo!=null){ var content=return_vo.content;var inputcode=return_vo.inputcode;if(content==inputcode){if(isCollapse=='true')accordion.toggleCollapse(false);document."+target_to_use+".location='"+url_to_use+"'}else{alert(\"密码不正确!\");}}";
//    			    String js2="var thecodeurl ='/general/sys/validate/secondValidate.do?b_init=link'; var return_vo= window.showModalDialog(thecodeurl, '', 'dialogWidth:470px; dialogHeight:280px;resizable:no;center:yes;scroll:yes;status:yes');";
//    			    js2+="if(return_vo!=null){ var content=return_vo.content;var inputcode=return_vo.inputcode;if(content==inputcode){"+js1+"}else{alert(\"校验码不正确!\");}}";
//    			    buf.append(",listeners:{click:function (node,checked){"+js2+"}}");
//    			}
//			} else {
                if(validateType != null && validateType.length() > 0)
                    buf.append(",validateType:'"+validateType+"',urlToUse:'"+url_to_use+"',targetToUse:'"+target_to_use+"'");
//			}
		}
		
		buf.append("}");
		return buf.toString();
	}
	/**
	 * 判断是否有模板的权限
	 * @param tabid
	 * @return
	 */
	private String isHaveTemplateid(String tabid)
	{
		UserView userView=(UserView) pageContext.getSession().getAttribute(WebConstant.userView);
		String tabidarr [] = tabid.split(",");
		String isHavetabid = "";
		for(String tab_id :tabidarr){
			boolean b=false;
	        if (userView.isHaveResource(IResourceConstant.RSBD, tab_id)){
	        	b=true;
	        }	
	        else if (userView.isHaveResource(IResourceConstant.GZBD, tab_id)){
	        	b=true;
	        }        
	        else if (userView.isHaveResource(IResourceConstant.INS_BD, tab_id)){
	        	b=true;
	        }        
	        else if (userView.isHaveResource(IResourceConstant.PSORGANS, tab_id)){
	        	b=true;
	        }
	        else if (userView.isHaveResource(IResourceConstant.PSORGANS_FG, tab_id)){
	        	b=true;
	        }
	        else if (userView.isHaveResource(IResourceConstant.PSORGANS_GX, tab_id)){
	        	b=true;
	        }
	        else if (userView.isHaveResource(IResourceConstant.PSORGANS_JCG, tab_id)){
	        	b=true;
	        }
	        else if (userView.isHaveResource(IResourceConstant.ORG_BD, tab_id)){
	        	b=true;
	        }
	        else if (userView.isHaveResource(IResourceConstant.POS_BD, tab_id)){
	        	b=true;
	        }
	        if(b==true){
	        	isHavetabid+=tab_id+",";
	        }
		}
		if(!"".equals(isHavetabid)){
			isHavetabid = isHavetabid.substring(0,isHavetabid.length()-1);
		}
        return isHavetabid;
	}
	/**
	 * 输出虚拟子节点
	 * @param parent
	 * @param layer
	 * @param name 显示文本
	 * @param tabid 模板号
	 * @return
	 */
	private String outVirtualChildJs(Element parent,int layer,String name,String tabid){
		StringBuffer buf=new StringBuffer();
		buf.append("{");
		buf.append("text:'");
		buf.append(name);
		buf.append("' ");
		
		String tmp=parent.getAttributeValue("icon");
		if(!(tmp==null||tmp.length()==0)){
			buf.append(",icon:'");
			buf.append(tmp);
			buf.append("'");
		}	
    	
		if(!"".equals(tabid)){
			buf.append(",href:'");
			if(tabid.indexOf(",")!=-1){
				//buf.append("/general/template/search_module.do?b_query=link");
				//buf.append("&encryptParam="+PubFunc.encrypt("&operationname=^804c^79f0^8bc4^5ba1&staticid=37&returnflag=noback&res_flag=38&template_ids="+tabid));
				buf.append("/module/template/templatenavigation/TemplateForm.html?b_query=link&module_id=11&approve_flag=1&sys_type=1&tab_id="+tabid);
			}else {
				//buf.append("/general/template/edit_form.do?b_query=link");
				//buf.append("&encryptParam="+PubFunc.encrypt("&returnflag=noback&business_model=0&businessModel=0&sp_flag=1&ins_id=0&tabid="+tabid));
				buf.append("/module/template/templatenavigation/TemplateForm.html?b_query=link&approve_flag=1&module_id=11&tab_id="+tabid);
			}
			buf.append("'");
		}	
		
		tmp=parent.getAttributeValue("target");
		if(!(tmp==null||tmp.length()==0)){
			buf.append(",hrefTarget:'");
			buf.append(tmp);
			buf.append("'");
		}
		
		tmp=parent.getAttributeValue("hide");
		if(!(tmp==null||tmp.length()==0)){
			buf.append(",hide:");
			buf.append(tmp);
			buf.append("");
		}
		
		buf.append(",leaf:true");

		buf.append("}");
		return buf.toString();
	}
	
	private String getPath() {
		String classPath = "";
		try
		{
			classPath = this.getClass().getResource("").toString();
			classPath=java.net.URLDecoder.decode(classPath,"utf-8"); 	
			//classPath = this.getClass().getResource("").toURI().getPath();
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}

		if (classPath.indexOf("hrpweb3.jar") != -1) 
		{
			int beginIndex=-1,endIndex=-1;
			/**weblogic,环境布署时*/
			if(classPath.startsWith("zip:"))
			{
				beginIndex = classPath.indexOf("zip:") + 4;
				endIndex = classPath.lastIndexOf("hrpweb3.jar") + 11;
				classPath = classPath.substring(beginIndex, endIndex);				
			}
			/* cmq added at 20121010 for jboss eap6,但取不到实际的绝对路径
			else if(classPath.startsWith("vfs:"))//jboss Eap6
			{
				//vfs:/D:/EAP-6.0.0.GA/jboss-eap-6.0/bin/content/hrms.war/WEB-INF/lib/hrpweb3.jar/com/hjsj/hrms/taglib/general/			
				beginIndex = classPath.indexOf("vfs:") + 5;
				endIndex = classPath.lastIndexOf("hrpweb3.jar") + 11;
				classPath = classPath.substring(beginIndex, endIndex);
			}
			*/
			else
			{
				Properties props=System.getProperties(); //系统属性
				String sysname = props.getProperty("os.name");
				if(sysname.startsWith("Win")){
					beginIndex = classPath.indexOf("/") + 1;
					endIndex = classPath.lastIndexOf("hrpweb3.jar") + 11;
					classPath = classPath.substring(beginIndex, endIndex);
					}else{
						beginIndex = classPath.indexOf("/") ;
						endIndex = classPath.lastIndexOf("hrpweb3.jar") + 11;
						classPath = classPath.substring(beginIndex, endIndex);
					}
			}
		} 
		else 
		{
			Properties props=System.getProperties(); //系统属性
			String sysname = props.getProperty("os.name");
			//zhaoxj 20150514 开发环境下，windows平台需要去掉路径开头的“/”，其它如OSX，linux等不能去掉
			int beginIndex = classPath.indexOf("/");
			if(sysname.startsWith("Win")){
				beginIndex++;
			}
			
			if(classPath.indexOf("taglib")!=-1){
				int endIndex = classPath.lastIndexOf("taglib")-1;
				String mixpath = "/constant/menu.xml/";
				classPath = classPath.substring(beginIndex, endIndex) + mixpath;
			}
		}

		return classPath;
	}


	private  Document getDocument()throws GeneralException {
		String file = this.getPath();
		Document doc = null;
		String EntryName = "com/hjsj/hrms/constant/menu.xml";
		InputStream in = null;
		JarFile jf = null;
		try 
		{

			/**cmq added for jboss eap6*/
		    String webserver=SystemConfig.getProperty("webserver");
		    if("jboss".equalsIgnoreCase(webserver)|| "inforsuite".equalsIgnoreCase(webserver))
		    {
		    	in=this.getClass().getResourceAsStream("/com/hjsj/hrms/constant/menu.xml");
		    }
		    else
		    {
				if(file.indexOf("hrpweb3.jar")!=-1)
				{
					jf = new JarFile(file);
					Enumeration es = jf.entries();
					while (es.hasMoreElements()) 
					{
						JarEntry je = (JarEntry) es.nextElement();
						if (je.getName().equals(EntryName)) 
						{
							in = jf.getInputStream(je);
							break;
						}

					}
				}		    	
		    }
			if(in==null)
			{
				in = new FileInputStream("/" + file);
			}
			if(in==null)
				throw new GeneralException("NOT FOUND menu.xml FILE");
			doc = PubFunc.generateDom(in);
		}  catch (Exception e) {
			throw GeneralExceptionHandler.Handle(e);
		}finally{
			PubFunc.closeIoResource(in);
			PubFunc.closeIoResource(jf);
			PubFunc.closeIoResource(in);
		}

		return doc;

	}
	
	 
	
	/**
	 * 生成主菜单列表,xml->json
	 * @return
	 */
	private String outMenuJs()
	{
		StringBuffer buf=new StringBuffer();
		try
		{
			Document doc=getDocument();
			
	        Element rootnode=doc.getRootElement();
	        if(fromnode!=null && !"".equals(fromnode)){
	        	XPath xPath = XPath.newInstance("/hrp_menu/menu[@id='"+fromnode+"']");
	        	rootnode = (Element) xPath.selectSingleNode(doc);
    		}
	        List list=rootnode.getChildren();
    		buf.append("[");
    		UserView userView=(UserView) pageContext.getSession().getAttribute(WebConstant.userView);
    		//add xuj 现在领导桌面id=21的节点支持在业务平台显示，但如果领导显示了（menuhide<>false时）这时领导决策模块
    		//就不要显示啦，故要在遍历显示领导决策模块时先要判断出领导桌面是否会显示
    		XPath xPath = XPath.newInstance("/hrp_menu/menu[@id='21']");
        	Element ele = (Element) xPath.selectSingleNode(doc);
        	boolean leadershow = false;
    		if(ele!=null&&"false".equals(ele.getAttributeValue("menuhide"))){
    			leadershow=true;
    		}
	        for(int i=0;i<list.size();i++)
	        {
	        	Element element=(Element)list.get(i);
	        	
	        	String menuhide = element.getAttributeValue("menuhide");
	        	if("false".equalsIgnoreCase(menuhide))
	        		continue;
	        	String func_id=element.getAttributeValue("func_id");
	        	String mod_id=element.getAttributeValue("mod_id");	   
	        	String id=element.getAttributeValue("id");
	        	String url=element.getAttributeValue("url");
	        	
	        	if(!PubFunc.hasPriMenu(func_id,url,userView)) //根据锁版本号控制人事异动or薪资的新旧程序
	        		continue;
	        	if("326".equals(func_id)) {
                    //zxj 20160612 标准版绩效考核只开放360评价，名称改为考核评价
                    EncryptLockClient lock=(EncryptLockClient)pageContext.getServletContext().getAttribute("lock");
                    if (lock.getVersion_flag() == 0) {
                        element.setAttribute("name", ResourceFactory.getProperty("jx.khplan.param3.title6.std")); 
                    }                   
                }
	        	
	        	/**左边主菜单区域排除top快捷菜单项项,移动应用平台*/
	        	//如果显示了领导桌面就不显示领导决策
	        	if("9999".equalsIgnoreCase(id)||/*id.equalsIgnoreCase("21")||*/"50".equalsIgnoreCase(id)||(!leadershow&&"11".equals(id)))
	        		continue;
	        	List children = element.getChildren();
	        	boolean haveChildrenPriv=false;
	        	for(int n=children.size()-1;n>=0;n--){
	        		Element el=(Element)children.get(n);
	        		String elfunc_id=el.getAttributeValue("func_id");
		        	String elmod_id=el.getAttributeValue("mod_id");
		        	if(haveFuncPriv(elfunc_id,elmod_id)){
		        		haveChildrenPriv=true;
		        		break;
		        	}
	        	}
	        String be_link = element.getAttributeValue("be_link");
	        be_link = "true".equals(be_link)?be_link:"false";
	        haveChildrenPriv = haveChildrenPriv==false&& "true".equals(be_link)?true:haveChildrenPriv;
	        	if(haveChildrenPriv&&haveFuncPriv(func_id,mod_id))
	        	{
	        		if(buf.length()>1)
		        		buf.append(",");
	        		buf.append(outChildJs(element,1));
	        	}
	        	
	        	
	        }  //for i loop end.
    		buf.append("]");
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
		return buf.toString();
	}
	
	
	public int doStartTag() throws JspException {
        try
        {   
          //2015-12-04  chenxg  add  v70以上版本模块二次认证不起作用
            //声明树节点中的属性
            StringBuffer fields = new StringBuffer();
            //节点名称
            fields.append("[{ name: 'text', type: 'string', defaultValue: null },"); 
            fields.append("{ name: 'hrefTarget', type: 'string', defaultValue: null },");  
            //是否进行模块的二次认证   2：根据电话发送校验码进行认证  1：输入登录口令进行验证  1,2：两层认证
            fields.append("{ name: 'validateType', type: 'string', defaultValue: null },");    
            //二次认证后需要跳转的链接
            fields.append("{ name: 'urlToUse', type: 'string', defaultValue: 0 },"); 
            //跳转链接，需要二次认证时值为空
            fields.append("{ name: 'href', type: 'string', defaultValue: null },");    
            fields.append("{ name: 'leaf', type: 'bool', defaultValue: false },"); 
            fields.append("{ name: 'hide', type: 'bool', defaultValue: false },"); 
            //链接跳转的区域  二次认证时才需要赋值，其他不用赋值
            fields.append("{ name: 'targetToUse', type: 'string', defaultValue: null },"); 
            //模块编号
            fields.append("{ name: 'mod_id', type: 'string', defaultValue: null }]");    
            
        	StringBuffer buf=new StringBuffer();
        	String js=outMenuJs();
        	buf.append("<script language=\"javascript\">");
        	buf.append("var fields =");
            buf.append(fields);
            buf.append(";var ");
        	buf.append(this.name);
			buf.append("=");
			buf.append(js);
			buf.append(";");
			buf.append("</script>");
			
        	
	        pageContext.getOut().println(buf.toString());
//	        System.out.println("js="+buf.toString());
	        return EVAL_BODY_BUFFERED;   
        }
        catch(Exception ex)
        {
        	ex.printStackTrace();
        	return SKIP_BODY;
        }
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getFromnode() {
		return fromnode;
	}

	public void setFromnode(String fromnode) {
		this.fromnode = fromnode;
	}

}
