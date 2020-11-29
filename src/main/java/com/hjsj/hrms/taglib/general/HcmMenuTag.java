/**
 * 
 */
package com.hjsj.hrms.taglib.general;

import com.hjsj.hrms.businessobject.general.template.TemplateTableParamBo;
import com.hjsj.hrms.businessobject.performance.singleGrade.DirectUpperPosBo;
import com.hjsj.hrms.businessobject.sys.ConstantXml;
import com.hjsj.hrms.businessobject.sys.report.Sys_Oth_Parameter;
import com.hjsj.hrms.module.recruitment.parameter.businessobject.ParameterXMLBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.utility.AdminCode;
import com.hrms.frame.utility.AdminDb;
import com.hrms.hjsj.sys.*;
import com.hrms.struts.constant.SystemConfig;
import com.hrms.struts.constant.WebConstant;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.valueobject.UserView;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Category;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.xpath.XPath;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.BodyTagSupport;
import javax.sql.RowSet;
import java.io.FileInputStream;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * <p>Title: HcmMenuTag </p>
 * <p>Description: v7版导航菜单</p>
 * <p>Company: hjsj</p>
 * <p>create time  2014-3-17 上午10:18:40</p>
 * @author xuj
 * @version 1.0
 */
public class HcmMenuTag extends BodyTagSupport {
	
	private static Category log = Category.getInstance(HcmMenuTag.class.getName());
	/**
	 * menu.xml文件中menu节点的id属性值。
	 * 对于一级菜单menu_id不用传递，程序根据判断menu_id为空认为要加载一级导航菜单按钮，
	 * 当menu_id值不为空程序根据menu_id值加载相应一级menu节点下的二级即二级以下导航菜单
	 */
	private String menu_id;
	/**
	 * 对一级菜单有效，定义页头最多显示多少个一级菜单按钮后需要采用填出框方式显示更多一级菜单按钮
	 * 弃用此属性，用处不大
	 */
	private String max_menu;
	/**
	 * 对一级菜单有效，标示点击一级导航菜单刷新二级菜单区域的target
	 * 可弃用此属性，用处不大
	 */
	private String target;
	/**
	 * 输出js变量名称
	 */
	private String name;
	
	/**
	 * 主题
	 */
	private String themes="default";
	
	/**
	 * 当前用户一级菜单个数
	 */
	private int menuNum = 0;
	/**
	 * 强制显示一级菜单
	 */
	private boolean showRoot = false;
	/**
	 * 当前用户一级菜单名称
	 */
	private String menuName="";
	private String modId="-1";
	private String allname="";	
	private String centerurl="";
	private String centertarget="il_body";
	
	/**
	 * 只有一级节点直接点击打开连接标记 guodd 2016-05-25
	 */
	private String be_link="";
	
	/**
	 * 菜单按钮类型：默认空|toolbar|menuitem
	 */
	private String menutype;
	
	private String center_target="mil_body";
	
	/*使用menuitem时第一个节点的连接，用于默认显示在内容区*/
	private String first_url="";

	public HcmMenuTag() {
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
	
	private String outChildJs(Element parent,int layer)
	{
		StringBuffer buf=new StringBuffer();
	    UserView userview=(UserView) pageContext.getSession().getAttribute(WebConstant.userView);

	    String func_id_p=parent.getAttributeValue("func_id");
		buf.append("{");
		buf.append("text:'");
		// 职称评审模块菜单要自定义显示
		if("3800202".equals(func_id_p)){//聘委会成员
			buf.append(ResourceFactory.getProperty("zc.menu.committeeshowtext") + "成员");
		}else if("3800203".equals(func_id_p)){//学科组成员
			buf.append(ResourceFactory.getProperty("zc.menu.subjectsshowtext") + "成员");
		} else {
			buf.append(parent.getAttributeValue("name"));
		}
		buf.append("' ");
		if(layer==1&&this.menuNum==0){
			/**默认展开第一个节点导致这个节点菜单不触发expand事件而无法进行并发点数控制。
			 * 此处注释掉，通过前端js实现展开第一个节点 
			 * guodd 2018-08-02
			 */
			//buf.append(",expanded:");
			//buf.append("true");
		}
		String tmp=parent.getAttributeValue("icon");
		
		String id=parent.getAttributeValue("id");//0501
		if(!(tmp==null||tmp.length()==0))
		{
			buf.append(",icon:'");
			buf.append(tmp);
			buf.append("'");
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
		/** 当链接中有custommenu=1参数时，替换一下。用于走交易类时判断是否走priv_funcid权限判断 
		 * 原因:客户通过咱们的文档将某个统计功能配置为自助的单独菜单，用于实现不给授权人员统计权限但是要让用户看见某个特定的报表。
		 * 但是用户点击进去不，因为功能交易类配置了priv_funcid。
		 * 解决方案：配置的连接中添加固定参数：custommenu=1。为了安全，此处将值替换为betrue。在BusinessProcess类中判断custommenu=betrue时，不进行priv_funcid校验
		 * guodd 2018-08-02*/
		tmp = tmp.replace("custommenu=1","custommenu=betrue");
		
		//将url链接参数加密为一个参数encryptParam  xuj add 2014-9-2  start
		int index = tmp.indexOf("&");
		/**
		 * 如果是配置的外部链接，参数不加密 guodd 2019-08-28
		 */
		if(index>-1  && !(tmp.startsWith("http://") || tmp.startsWith("https://"))){
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
			if("menuitem".equals(menutype)&&this.first_url.length()==0&&!tmp.startsWith("javascript:")){
				first_url = tmp;
			}
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
		
		tmp=parent.getAttributeValue("hide");
		if(!(tmp==null||tmp.length()==0))
		{
		  //2015-12-04  chenxg  add  v70以上版本模块二次认证不起作用
//			if(validateFlag.equals("1")){
//				validateHide = tmp;
//			}else{
				buf.append(",hide:");
				buf.append(tmp);
				buf.append("");
//			}
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
		if("3110601".equals(func_id_p) || list.size()==0)
		{
			buf.append(",leaf:true");
		}
		//wangb 20190130 我的薪酬-应用参数设置的子菜单作为paramMenu方式使用，不用显示在左侧菜单中
		else if("32418".equals(func_id_p) || list.size()==0)
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
	    				e.printStackTrace();
	    			}finally{
	    				if(connection!=null)
							try {
								connection.close();
							} catch (SQLException e) {
								e.printStackTrace();
							}
	    			}

	    		}
	    		
	    		/* zxj 20150119 V70及以上版本都使用新版机构图和汇报关系图，menu.xml中已改为新链接，此处不用再判断
	    		//zxj 20140605 HCM不显示旧组织机构图菜单
	    		//自助-机构信息-机构图
                if ("050102".equals(func_id)) {
                    child.setAttribute("url", "/general/inform/org/map/searchOrgTree.do?b_search=link&amp;backdate=&amp;returnvalue=");
                }
                
                //自助-机构信息-历史机构图
                if ("050103".equals(func_id)) {
                    child.setAttribute("url", "/general/inform/org/map/searchhistoryOrgTree.do?b_search=link&amp;backdate=&amp;returnvalue=");
                }
                
                //业务-组织机构-单位管理-机构图,领导决策-信息浏览-机构图
                if ("23051".equals(func_id) || "32202".equals(func_id)) {
                    child.setAttribute("url", "/general/inform/org/map/searchOrgTree.do?b_search=link&amp;backdate=&amp;returnvalue=&amp;busiPriv=1");
                }
	    		
                //业务-组织机构-单位管理-历史机构图
                if ("23052".equals(func_id)) {
                    child.setAttribute("url", "/general/inform/org/map/searchhistoryOrgTree.do?b_search=link&amp;busiPriv=1");
                }
                //业务-组织机构-单位管理-汇报关系图
                if("230521".equals(func_id)){
                	child.setAttribute("url","/general/sprelationmap/select_report_tree.do?b_init=link&amp;relationType=1");
                }
                */
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
					e.printStackTrace();
				    }	
	        		}
	        		inputchinfor=inputchinfor!=null&&inputchinfor.trim().length()>0?inputchinfor:"1";
	  			    approveflag=approveflag!=null&&approveflag.trim().length()>0?approveflag:"1";
	  						  
	  			    if("1".equals(inputchinfor)&& "1".equals(approveflag))
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
	      	        String[] funcs =StringUtils.split("2702011,2702010,2702020,2702021,2702024,2702032",","); //人员变动比对，生成日考勤明细表，统计，計算，个人业务处理，浏览日明细
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
	      	        String[] funcs =StringUtils.split("0C3105,0C3100,0C3120,0C3121,0C3123,0C3113",","); //人员变动比对，生成日考勤明细表，统计，計算，个人业务处理，浏览日明细
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
		        		if(haveFuncPriv("3800501", null)
		        				||haveFuncPriv("3800502", null)
		        				||haveFuncPriv("3800503", null)
		        				||haveFuncPriv("3800504", null)
		        				||haveFuncPriv("3800511", null)){
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
	        				if(!"".equals(HaveTemplateid)) {
	        					bflag=true;
	        					childjs.append(outVirtualChildJs(child, ++layer,ResourceFactory.getProperty("zc.label.mianshiPn"),HaveTemplateid));
	        				}
	        			}
	        			if(haveFuncPriv("3800502",null)&&StringUtils.isNotEmpty((String)map.get("4"))){
	        				String HaveTemplateid = isHaveTemplateid((String)map.get("4"));
	        				if(!"".equals(HaveTemplateid)){
	        					bflag=true;
	        					if(childjs.length()>0)
				        			childjs.append(",");
	        					//破格申请审批
		        				childjs.append(outVirtualChildJs(child, ++layer,ResourceFactory.getProperty("zc.label.pogePn"),HaveTemplateid));
	        				}
	        				
	        			}
	        			if(haveFuncPriv("3800511",null)&&StringUtils.isNotEmpty((String)map.get("8"))){
	        				String HaveTemplateid = isHaveTemplateid((String)map.get("8"));
	        				if(!"".equals(HaveTemplateid)){
	        					bflag=true;
		        				if(childjs.length()>0)
				        			childjs.append(",");
		        				//预报名申请
		        				childjs.append(outVirtualChildJs(child, ++layer,ResourceFactory.getProperty("zc.label.baomingPn"),HaveTemplateid));
	        				}
	        			}
	        			if(haveFuncPriv("3800503",null)&&StringUtils.isNotEmpty((String)map.get("6"))){
	        				String HaveTemplateid = isHaveTemplateid((String)map.get("6"));
	        				if(!"".equals(HaveTemplateid)){
	        					bflag=true;
		        				if(childjs.length()>0)
				        			childjs.append(",");
		        				//材料审查
		        				childjs.append(outVirtualChildJs(child, ++layer,ResourceFactory.getProperty("zc.label.shenbaoCaiPn"),HaveTemplateid));
	        				}
	        			}
	        			if(haveFuncPriv("3800504",null)&&StringUtils.isNotEmpty((String)map.get("5"))){
	        				String HaveTemplateid = isHaveTemplateid((String)map.get("5"));
	        				if(!"".equals(HaveTemplateid)){
	        					bflag=true;
		        				if(childjs.length()>0)
				        			childjs.append(",");
		        				//论文送审
		        				childjs.append(outVirtualChildJs(child, ++layer,ResourceFactory.getProperty("zc.label.lunwenSongPn"),HaveTemplateid));
	        				}
	        			}
	        		}else if("0KR020302".equals(func_id)
	        				||"0KR020502".equals(func_id)){//部门计划、部门总结，如果有权限但不是部门领导同样不显示菜单
	        			 //如果有负责部门，则定位到部门上。
        				Connection conn = null;
						try {
							conn = (Connection) AdminDb.getConnection();
							// “部门计划”“部门总结”菜单只做权限控制，避免分管的情况看不到菜单 chent modify 20180125
							/*WorkPlanUtil workPlanUtil = new WorkPlanUtil(conn, userview);
	        	            ArrayList deptlist =workPlanUtil.getDeptList(userview.getDbname(), 
	        	                    userview.getA0100());*/
		        			if(haveFuncPriv(func_id,null)/* && deptlist.size()>0*/){
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
		        		/*if(childjs.length()>0)
	        			childjs.append(",");
	        			childjs.append(outChildJs(child,++layer));*/
		        		//HCM系统管理资源下载界面不显示SVG View  jingq add 2014.11.14
		        		String bosflag = userview.getBosflag();
		        		if("30051".equals(func_id)&&"hcm".equals(bosflag)
		        				||"0KR020302".equals(func_id)
		        				||"0KR020502".equals(func_id)){
		        			continue;
		        		} else {
			        		if(childjs.length()>0)
			        			childjs.append(",");
			        		childjs.append(outChildJs(child,++layer));
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
			if(validateType != null && validateType.length() > 0)
			    buf.append(",validateType:'"+validateType+"',urlToUse:'"+url_to_use+"',targetToUse:'"+target_to_use+"'");
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
	 * @param name
	 * @param tabid
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
			//if(tabid.indexOf(",")!=-1){
				//buf.append("/general/template/search_module.do?b_query=link");
				//buf.append("&encryptParam="+PubFunc.encrypt("&operationname=^804c^79f0^8bc4^5ba1&staticid=37&returnflag=noback&res_flag=38&template_ids="+tabid));
				buf.append("/module/template/templatenavigation/TemplateForm.html?b_query=link&module_id=11&approve_flag=1&sys_type=1&tab_id="+tabid);//hej update 20180726 bug39150 
			/*}else {
				//buf.append("/general/template/edit_form.do?b_query=link");
				//buf.append("&encryptParam="+PubFunc.encrypt("&returnflag=noback&business_model=0&businessModel=0&sp_flag=1&ins_id=0&tabid="+tabid));
				//单个模板不应该走批量审批 approve_flag = 0 才对  haosl update 2018年6月13日
				buf.append("/module/template/templatenavigation/TemplateForm.html?b_query=link&approve_flag=0&module_id=11&tab_id="+tabid);
			}*/
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
	
	/**
	 * 输出子节点的内容
	 * 
	 * <li><a href="javascript:void(0);"><img class="png" src="/images/hcm/themes/<%=themes %>/nav/nav1.png"/>组织结构</a></li>
     * <li class="line"></li>
     *         
     * <li><a href="javascript:void(0);" onclick="showhidden('menuid','none');"><img class="png" src="/images/hcm/themes/<%=themes %>/nav/nav1.png"/>组织结构</a></li>
     *                 
	 * @param parent
	 * @return
	 */
	private void outChildHTML(Element parent,int layer,StringBuffer bufMenu,StringBuffer menuMore)
	{
	    UserView userview=(UserView) pageContext.getSession().getAttribute(WebConstant.userView);

	    String func_id_p=parent.getAttributeValue("func_id");
	    String target = parent.getAttributeValue("target");
	    String menu_name= parent.getAttributeValue("name");
	    //System.out.println("menu_name"+menu_name);
	    log.debug("menu_name"+menu_name);
	    String name = PubFunc.splitString(menu_name, 15);
	    //System.out.println("name"+name);
	    log.debug("name"+name);
	    if(menu_name.equals(name)){
	    	menu_name="";
	    }
	    
		String icon=parent.getAttributeValue("iconv7");
		String mod_id=parent.getAttributeValue("mod_id");
	    
	    icon = icon==null||icon.length()==0?"nav_sys.png":icon;
	    
		String id=parent.getAttributeValue("id");//0501

		String url = "";
    	/**超链上补参数*/
    	if("010101".equalsIgnoreCase(func_id_p) || "010201".equalsIgnoreCase(func_id_p) || "010301".equalsIgnoreCase(func_id_p))
    	{
    		url=parent.getAttributeValue("url");
    		url=url+"&amp;userbase="+userview.getDbname();
    		parent.setAttribute("url", url);
    	}	
    	
    	url=parent.getAttributeValue("url");
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
				url="/workbench/browse/showphoto.do?b_search=link&amp;action=showphotodata.do&amp;target=nil_body&amp;userbase=usr&amp;flag=noself&amp;isUserEmploy=0";
			else
				url="/workbench/browse/showinfo.do?b_search=link&amp;action=showinfodata.do&amp;target=nil_body&amp;userbase=&amp;flag=noself&amp;isUserEmploy=0&amp;isphotoview=";
		}
		if("060601".equalsIgnoreCase(func_id_p)&&("20110403".equalsIgnoreCase(id)|| "030401".equalsIgnoreCase(id)))
		{
		    DirectUpperPosBo bo=new DirectUpperPosBo();
		    String flag=bo.getGradeFashion("0");
		    if("1".equals(flag))
		    	url="/selfservice/performance/batchGrade.do?b_query=link&amp;model=0&amp;linkType=1&amp;returnflag=menu";
		    else
		    	url="/selfservice/performance/batchGrade.do?b_tileFrame=link&amp;model=0&amp;linkType=1&amp;planContext=all&amp;returnflag=menu";
		}
		
		//将url链接参数加密为一个参数encryptParam  xuj add 2014-9-2  start
		url = PubFunc.encrypt(url);
		//将url链接参数加密为一个参数encryParam  xuj add 2014-9-2  end
		
		String[] pos = new String[]{"0","0"};
		if("nav.png".equals(icon)){
			String position = parent.getAttributeValue("position");
    			pos = position.split(",");
		}
		
		bufMenu.append("<li>");
	    bufMenu.append("<span onmouseover=\"this.className='menu_span_hover';\" onmouseout=\"this.className='';\" title=\""+menu_name+"\" onclick=\"menuTree('"+id+"','"+this.target+"','"+url+"','"+target+"','"+name+"','"+((mod_id==null||mod_id.indexOf(",")!=-1)?"-1":mod_id)+"','"+menu_name+"',"+be_link+",this);\">");
	    	bufMenu.append("<div style=\"width:32px;height:32px;margin:0 auto;background:url(/images/hcm/themes/"+themes+"/nav/"+icon+") no-repeat "+pos[0]+"px "+pos[1]+"px;\"></div>");////使用div + 背景图片定位
	    bufMenu.append(name);
	    bufMenu.append("</span>");
	    bufMenu.append("</li>");
	    bufMenu.append("<li id=\"menuli"+menuNum+"\" class=\"line\"></li>");
	    
	    menuMore.append("<li>");
	    menuMore.append("<span onmouseover=\"this.className='menu_span_hover';\" onmouseout=\"this.className='';\" title=\""+menu_name+"\"  onclick=\"showhidden('menuid','none');menuTree('"+id+"','"+this.target+"','"+url+"','"+target+"','"+name+"','"+((mod_id==null||mod_id.indexOf(",")!=-1)?"-1":mod_id)+"','"+menu_name+"',"+be_link+",this);\">");
	    	menuMore.append("<div style=\"width:32px;height:32px;margin:0 auto;background:url(/images/hcm/themes/"+themes+"/nav/"+icon+") no-repeat "+pos[0]+"px "+pos[1]+"px;\"></div>");//使用div + 背景图片定位
	    menuMore.append(name);
	    menuMore.append("</span>");
	    menuMore.append("</li>");
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
				in = new FileInputStream(file);
			}
			if(in==null)
				throw new GeneralException("NOT FOUND menu.xml FILE");
			doc = PubFunc.generateDom(in);
		}  catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}finally{
			PubFunc.closeIoResource(in);
			PubFunc.closeIoResource(jf);
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
	        if(menu_id!=null && !"".equals(menu_id)){
	        	XPath xPath = XPath.newInstance("/hrp_menu/menu[@id='"+menu_id+"']");
	        	rootnode = (Element) xPath.selectSingleNode(doc);
    		}
	        if("menuitem".equals(menutype)){//考虑到工具条等类似menu按钮，可能会menu下的menu，根据实际情况暂最多支持menu下三级
	        	if(rootnode==null){
	        		XPath xPath = XPath.newInstance("/hrp_menu/menu/menu[@id='"+menu_id+"']");
		        	rootnode = (Element) xPath.selectSingleNode(doc);
		        	if(rootnode==null){
		        		xPath = XPath.newInstance("/hrp_menu/menu/menu/menu[@id='"+menu_id+"']");
			        	rootnode = (Element) xPath.selectSingleNode(doc);
		        	}
	        	}
	        }
	        if(rootnode==null)
	        	return "[]";
	        List list=rootnode.getChildren();
    		buf.append("[");
    		
    		//判断是否有头像设置、代理设置按钮
    		UserView userView=(UserView) pageContext.getSession().getAttribute(WebConstant.userView);
			int status = 0;
			status=userView.getStatus();
			boolean bself=true;
		    if(status!=4)
		    {
		        String a0100=userView.getA0100();
		    	if(a0100==null||a0100.length()==0)
		    	{
		    		bself=false;
		    	}
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
	        	/**左边主菜单区域排除top快捷菜单项项,移动应用平台,网络学院*/
	        	if("9999".equalsIgnoreCase(id)/*||id.equalsIgnoreCase("21")*/|| "50".equalsIgnoreCase(id)|| "55".equalsIgnoreCase(id))
	        		continue;
	        	List children = element.getChildren();
	        	/*boolean haveChildrenPriv=false;
	        	for(int n=children.size()-1;n>=0;n--){
	        		Element el=(Element)children.get(n);
	        		String elfunc_id=el.getAttributeValue("func_id");
		        	String elmod_id=el.getAttributeValue("mod_id");
		        	if(haveFuncPriv(elfunc_id,elmod_id)){
		        		haveChildrenPriv=true;
		        		break;
		        	}
	        	}*/
	        	//在节点是评委会的时候，判断是否有职称认定权限和考试认定权限，，如果有加上，这里就不用判断了
	        	if("38003".equals(func_id) || "38004".equals(func_id)){
	        		continue;
	        	}
	        	if(/*haveChildrenPriv&&*/haveFuncPriv(func_id,mod_id))
	        	{
	        		if("menuitem".equals(menutype)){
	        			if("900301".equals(id)){//纯业务用户无头像设置功能
			        		if(bself){
			        			if(buf.length()>1)
					        		buf.append(",");
				        		buf.append(outChildJs(element,1));
				        		menuNum++;
			        		}
	        			}else if("900303".equals(id)){//非自助用户无代理设置
	        				if(status==4){
	        					if(buf.length()>1)
					        		buf.append(",");
				        		buf.append(outChildJs(element,1));
				        		menuNum++;
	        				}
	        			}else{
	        				if(buf.length()>1)
				        		buf.append(",");
			        		buf.append(outChildJs(element,1));
			        		menuNum++;
	        			}
	        		}else{
	        			if(buf.length()>1)
			        		buf.append(",");
		        		buf.append(outChildJs(element,1));
		        		menuNum++;
	        		}
	        	}
	        	//在节点是评委会的时候，判断是否有职称认定权限和考试认定权限，，如果有加上，这样上面对考试认定和职称认定权限continue
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
        					if(buf.length()>1)
        						buf.append(",");
				        	//职称认定
			        		buf.append(outVirtualChildJs(element,1,ResourceFactory.getProperty("zc.label.zhichengPn"),HaveTemplateid));
			        		menuNum++;
		        		}
	        		}
	        		if(haveFuncPriv("38004",null)&&StringUtils.isNotEmpty((String)map.get("2"))){
	        			String HaveTemplateid = isHaveTemplateid((String)map.get("2"));
        				if(!"".equals(HaveTemplateid)){
        					if(buf.length()>1)
        						buf.append(",");
		        			//考试认定
			        		buf.append(outVirtualChildJs(element,1,ResourceFactory.getProperty("zc.label.kaoshiPn"),HaveTemplateid));
			        		menuNum++;
		        		}
	        		}
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
	
	/**
	 * 根据菜单编号取菜单名称
	 * @Title: getMenuName   
	 * @Description: 根据菜单编号取菜单名称  
	 * @param menuId 菜单编号
	 * @return
	 */
    private String getMenuName(String menuId)
    {
        String menuName = "";
        if (menuId == null || "".equals(menuId))
            return menuName;
            
        try
        {
            Document doc=getDocument();
            
            Element rootnode=doc.getRootElement();
            
            //考虑到menu.xml中菜单级数有限，这里最多取到第三层菜单，以后如有需求，可考虑循环所有节点查找
            XPath xPath = XPath.newInstance("/hrp_menu/menu[@id='"+menu_id+"']");
            rootnode = (Element) xPath.selectSingleNode(doc);
            if (rootnode == null) {
                xPath = XPath.newInstance("/hrp_menu/menu/menu[@id='"+menu_id+"']");
                rootnode = (Element) xPath.selectSingleNode(doc);
                
                if (rootnode == null) {
                    xPath = XPath.newInstance("/hrp_menu/menu/menu/menu[@id='"+menu_id+"']");
                    rootnode = (Element) xPath.selectSingleNode(doc);
                }
            }
              
            if (rootnode != null)
                menuName = rootnode.getAttributeValue("name").toString();
        }
        catch(Exception ex)
        {
            ex.printStackTrace();
        }
        return menuName;
    }
    
	/*
	  <!-- 导航菜单 -->
      <div class="navArea">
          <div class="midnav">
          <ul class="nav">
              <li><a href="javascript:void(0);"><img class="png" src="/images/hcm/themes/<%=themes %>/nav/nav1.png"/>组织结构</a></li>
              <li class="line"></li>
              <li><a href="javascript:void(0);"><img class="png" src="/images/hcm/themes/<%=themes %>/nav/nav2.png"/>能力素质</a></li>
              <li class="line"></li>
              <li><a href="javascript:void(0);"><img class="png" src="/images/hcm/themes/<%=themes %>/nav/nav3.png"/>员工管理</a></li>
              <li class="line"></li>
              <li><a href="javascript:void(0);"><img class="png" src="/images/hcm/themes/<%=themes %>/nav/nav4.png"/>招聘管理</a></li>
              <li class="line"></li>
              <li><a href="javascript:void(0);"><img class="png" src="/images/hcm/themes/<%=themes %>/nav/nav5.png"/>人事异动</a></li>
              <li class="line"></li>
              <li><a href="javascript:void(0);"><img class="png" src="/images/hcm/themes/<%=themes %>/nav/nav1.png"/>组织结构</a></li>
              <li class="line"></li>
              <li><a href="javascript:void(0);"><img class="png" src="/images/hcm/themes/<%=themes %>/nav/nav2.png"/>能力素质</a></li>
              <li class="line"></li>
              <li><a href="javascript:void(0);"><img class="png" src="/images/hcm/themes/<%=themes %>/nav/nav3.png"/>员工管理</a></li>
              <li class="line"></li>
              <li><a href="javascript:void(0);"><img class="png" src="/images/hcm/themes/<%=themes %>/nav/nav4.png"/>招聘管理</a></li>
              <li class="line"></li>
              <li><a href="javascript:void(0);"><img class="png" src="/images/hcm/themes/<%=themes %>/nav/nav5.png"/>人事异动</a></li>
              <li class="line"></li>
              <li><a href="javascript:void(0);"><img class="png" src="/images/hcm/themes/<%=themes %>/nav/nav3.png"/>员工管理</a></li>
              <li class="line"></li>
              <li><a href="javascript:void(0);"><img class="png" src="/images/hcm/themes/<%=themes %>/nav/nav4.png"/>招聘管理</a></li>
              <li class="line"></li>
              <li><a href="javascript:void(0);"><img class="png" src="/images/hcm/themes/<%=themes %>/nav/nav5.png"/>人事异动</a></li>
              <li class="line"></li>
          </ul>
          <a id="showmoreid" href="javascript:void(0);"  onclick="showhidden('menuid','block');" class="open" style="display:none"><img src="/images/hcm/themes/<%=themes %>/icon/icon7.png" /></a>
          </div>
      </div>
 </div>
</div>

<!-- 导航菜单更多弹出层 -->
<div class="navAreaopen" id="menuid">
<div class="opnav">
          <a href="javascript:void(0);"  onclick="showhidden('menuid','none');" class="close"><img src="/images/hcm/themes/<%=themes %>/icon/icon7_hover.png" /></a>
          <ul>
              <li><a href="javascript:void(0);" onclick="showhidden('menuid','none');"><img class="png" src="/images/hcm/themes/<%=themes %>/nav/nav1.png"/>组织结构</a></li>
              <li><a href="javascript:void(0);" onclick="showhidden('menuid','none');"><img class="png" src="/images/hcm/themes/<%=themes %>/nav/nav2.png"/>能力素质</a></li>
              <li><a href="javascript:void(0);" onclick="showhidden('menuid','none');"><img class="png" src="/images/hcm/themes/<%=themes %>/nav/nav3.png"/>员工管理</a></li>
              <li><a href="javascript:void(0);" onclick="showhidden('menuid','none');"><img class="png" src="/images/hcm/themes/<%=themes %>/nav/nav4.png"/>招聘管理</a></li>
              <li><a href="javascript:void(0);" onclick="showhidden('menuid','none');"><img class="png" src="/images/hcm/themes/<%=themes %>/nav/nav5.png"/>人事异动</a></li>
              <li><a href="javascript:void(0);" onclick="showhidden('menuid','none');"><img class="png" src="/images/hcm/themes/<%=themes %>/nav/nav1.png"/>组织结构</a></li>
              <li><a href="javascript:void(0);" onclick="showhidden('menuid','none');"><img class="png" src="/images/hcm/themes/<%=themes %>/nav/nav2.png"/>能力素质</a></li>
              <li><a href="javascript:void(0);" onclick="showhidden('menuid','none');"><img class="png" src="/images/hcm/themes/<%=themes %>/nav/nav3.png"/>员工管理</a></li>
              <li><a href="javascript:void(0);" onclick="showhidden('menuid','none');"><img class="png" src="/images/hcm/themes/<%=themes %>/nav/nav4.png"/>招聘管理</a></li>
              <li><a href="javascript:void(0);" onclick="showhidden('menuid','none');"><img class="png" src="/images/hcm/themes/<%=themes %>/nav/nav5.png"/>人事异动</a></li>
              <li><a href="javascript:void(0);" onclick="showhidden('menuid','none');"><img class="png" src="/images/hcm/themes/<%=themes %>/nav/nav3.png"/>员工管理</a></li>
              <li><a href="javascript:void(0);" onclick="showhidden('menuid','none');"><img class="png" src="/images/hcm/themes/<%=themes %>/nav/nav4.png"/>招聘管理</a></li>
              <li><a href="javascript:void(0);" onclick="showhidden('menuid','none');"><img class="png" src="/images/hcm/themes/<%=themes %>/nav/nav5.png"/>人事异动</a></li>
              <li><a href="javascript:void(0);" onclick="showhidden('menuid','none');"><img class="png" src="/images/hcm/themes/<%=themes %>/nav/nav4.png"/>招聘管理</a></li>
              <li><a href="javascript:void(0);" onclick="showhidden('menuid','none');"><img class="png" src="/images/hcm/themes/<%=themes %>/nav/nav5.png"/>人事异动</a></li>
              <li><a href="javascript:void(0);" onclick="showhidden('menuid','none');"><img class="png" src="/images/hcm/themes/<%=themes %>/nav/nav3.png"/>员工管理</a></li>
              <li><a href="javascript:void(0);" onclick="showhidden('menuid','none');"><img class="png" src="/images/hcm/themes/<%=themes %>/nav/nav4.png"/>招聘管理</a></li>
          </ul>
</div>
</div>  
	 */
	private String outMenuHTML()
	{
		StringBuffer bufMenu=new StringBuffer();
		StringBuffer menuMore = new StringBuffer();
		try
		{
			UserView userview=(UserView) pageContext.getSession().getAttribute(WebConstant.userView);	      
			
			bufMenu.append("<div class=\"navArea\">");
			if("Trident".equals(userview.getBrower())){
				bufMenu.append("<div class=\"midnav\" style=\"width:443px;margin-left:-221;\">");
			}else{
				bufMenu.append("<div class=\"midnav\" style=\"width:443px;margin:0 auto;\">");
			}
			bufMenu.append("<ul class=\"nav\">");
			
			menuMore.append("<div class=\"navAreaopen\" id=\"menuid\">");
			menuMore.append("<iframe class=\"opIframe\" frameborder=\"0\" src=\"javascript:void(0);\"> </iframe>");
			menuMore.append("<div class=\"opnav\">");
//			menuMore.append("<a href=\"javascript:void(0);\"  onclick=\"showhidden('menuid','none');\" class=\"close\"><img src=\"/images/hcm/themes/"+themes+"/icon/icon7_hover.png\" /></a>");
			menuMore.append("<ul>");
			
			Document doc=getDocument();
			
	        Element rootnode=doc.getRootElement();
	        List list=rootnode.getChildren();
	        
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
	        	String allname = element.getAttributeValue("name");
	        	
	        	if("326".equals(func_id)) {
	        	    //zxj 20160612 标准版绩效考核只开放360评价，名称改为考核评价
	        	    EncryptLockClient lock=(EncryptLockClient)pageContext.getServletContext().getAttribute("lock");
	        	    if (lock.getVersion_flag() == 0) {
	        	        allname = ResourceFactory.getProperty("jx.khplan.param3.title6.std");
	                    element.setAttribute("name", allname); 
                    }	        	    
	        	}
	        	
	        	String menu_name=PubFunc.splitString(allname, 18);
	        	String center_url  = element.getAttributeValue("url");
	        	String center_target=element.getAttributeValue("target");
	        	this.be_link = element.getAttributeValue("be_link");
	        	this.be_link = "true".equals(this.be_link)?this.be_link:"false";
	        	if(allname.equals(menu_name)){
	        		allname = "";
	        	}
	        	/**左边主菜单区域排除top快捷菜单项项,移动应用平台,网络学院*/
	        	//如果显示了领导桌面就不显示领导决策
	        	if("9999".equalsIgnoreCase(id)/*||id.equalsIgnoreCase("21")*/|| "50".equalsIgnoreCase(id)|| "55".equalsIgnoreCase(id)||(!leadershow&&"11".equals(id)))
	        		continue;
	        	
	        	/*如果be_link=true，不用判断下级权限。只判断本级权限即可 guodd 2016-05-25*/
	        	if("true".equals(be_link) && haveFuncPriv(func_id,mod_id)){
	        		
	        		/** 云菜单显示权限*/
		        	if("04".equals(id)){
		        		//没有配置云参数不加载
		        		if(!isHrCloud()){
		        			continue;
		        		}
		        		//自助用户登录或关联了自助的业务用户登录
		        		if((userview.getStatus()!=4) &&
		        				( userview.getStatus()!=0 || userview.getA0100()==null || "".equals(userview.getA0100())) ){
		        			continue;
		        		}
		        		//当前用户在人员视图中存在，且状态必须是[已同步]或[修改]
		        		if(!hrViewExist(userview)){
		        			continue;
		        		}
		        	}
	        		
	        		 outChildHTML(element,1,bufMenu,menuMore);
	        		 if(menuNum==0){
	        			 centerurl = PubFunc.encrypt(center_url);
	        			 showRoot = true;//强制显示一级菜单，防止只有一个的时候隐藏
	        		 }
	        		 menuNum++;
	        	     continue;	
	        	}
	        	
	        	List children = element.getChildren();
	        	boolean haveChildrenPriv=false;
	        	String url = "";
	        	for(int n=children.size()-1;n>=0;n--){
	        		Element el=(Element)children.get(n);
	        		String elfunc_id=el.getAttributeValue("func_id");
		        	String elmod_id=el.getAttributeValue("mod_id");
		        	String eid=element.getAttributeValue("id");
		        	if(haveFuncPriv(elfunc_id,elmod_id)){
		        		haveChildrenPriv=true;
		        		break;
		        	}
	        	}
	        	
	        	/**只有证照管理模块权限，但没有子菜单时，需要显示门户。此处特殊处理   guodd 2019-05-28*/
	        	if("400".equals(func_id) && !haveChildrenPriv && haveFuncPriv(func_id,mod_id) ) {
	        		//be_link等于true时，点击模块时将调用没有菜单树的template模板
	        		this.be_link = "true";
	        		outChildHTML(element,1,bufMenu,menuMore);
	        		 if(menuNum==0){
	        			 centerurl = PubFunc.encrypt(center_url);
	        			 showRoot = true;//强制显示一级菜单，防止只有一个的时候隐藏
	        		 }
	        		 menuNum++;
	        	     continue;	
	        	}
	        	
	        	if(haveChildrenPriv&&haveFuncPriv(func_id,mod_id))
	        	{
	        		if(menuNum==0){
	        			menu_id=id;
	        			menuName=menu_name;
	        			modId = mod_id;
	        			this.allname=allname;
	        			centerurl = PubFunc.encrypt(center_url);
	        			centertarget=center_target;
	        		}
	        		menuNum++;
	        		/***************无导航图默认展开第一个有权限的菜单*************/
	        		url = element.getAttributeValue("url");
	        		if(url.indexOf("br_blank=link")!=-1){
	        			url = this.findUrl(element);
	        		}
	        		if(url.length()>0)
	        			element.setAttribute("url", url);
	        		/******************************************/
	        		outChildHTML(element,1,bufMenu,menuMore);
	        	}
	        	
	        	
	        }  //for i loop end.
	        bufMenu.append("</ul>");
	        bufMenu.append("<a id=\"showmoreid\" href=\"javascript:void(0);\"  onmouseover=\"showhidden('menuid','block');\" class=\"open\" style=\"display:block\"><img src=\"/images/hcm/themes/"+themes+"/icon/icon7.png\" /></a>");
	        bufMenu.append("</div>");
	        bufMenu.append("</div>");
	        bufMenu.append("</div>");
	        bufMenu.append("</div>");
	        
	        menuMore.append("</ul>");
	        menuMore.append("</div>");
	        menuMore.append("</div>"); 
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
		return bufMenu.append(menuMore).toString();
	}
	
	/**
	 * 判断数据视图中是否存在此账号
	 * @param userview
	 * @return
	 */
	private boolean hrViewExist(UserView userview) {
		Connection conn = null;
		RowSet rs = null;
		boolean flag = false;
		String sql = " select A0100 from t_hr_view where A0100 = ? and hrcloud in ('0','2') ";
		ArrayList values = new ArrayList();
		values.add(userview.getA0100());
		try{
			conn = AdminDb.getConnection();
			ContentDAO dao = new ContentDAO(conn);
			rs = dao.search(sql,values);
			if(rs.next()){
				flag = true;
			}
		}catch (Exception e) {
		}finally {
			PubFunc.closeDbObj(rs);
			PubFunc.closeDbObj(conn);
		}
		
		return flag;
	}

	/**
	 * 判断是否配置云参数
	 * @return
	 */
	private boolean isHrCloud(){
		Connection conn = null;
		boolean flag = true;
		try{
			RecordVo recordVo = ConstantParamter.getConstantVo("HRCLOUD_CONFIG");
			conn = AdminDb.getConnection();
			//操作类型，加载云平台参数
			if (recordVo != null) {
				String str = recordVo.getString("str_value");
				if(str==null||"".equals(str)){
					flag = false;
				}
				JSONObject json = JSONObject.fromObject(str);
				if(json.get("appId") == null||json.get("tenantId") == null||json.get("appSecret") == null){
					flag = false;
				}
			}else{
				flag = false;
			}
		}catch (Exception e) {
			flag = false;
			e.printStackTrace();
		}finally{
			PubFunc.closeDbObj(conn);
		}
		return flag;
	}
	private void toolbarHTML(String menu_id,StringBuffer htmlStr){
		UserView userView=(UserView) pageContext.getSession().getAttribute(WebConstant.userView);
		try{	
			//判断是否需要签到签退按钮
			int status = 0;
			status=userView.getStatus();
			boolean bself=true;
		    if(status!=4)
		    {
		        String a0100=userView.getA0100();
		    	if(a0100==null||a0100.length()==0)
		    	{
		    		bself=false;
		    	}
		    }
		    
			
			//工具条层
			htmlStr.append("<div class=\"litnav\">");//"style=\"float:right;border:1 red solid;width:400px;\"
			htmlStr.append("<ul class=\"function\">");//style=\"height:28px;\"
			
			//工具条下拉层
			StringBuffer hidebar = new StringBuffer();
	        hidebar.append("<div class='list06' id='navid'><ul>");
	        hidebar.append("<li><a href='####' onclick=\"javascript:showhidden('navid','none');\">");
	        hidebar.append("<img class=\"png\" src=\"/images/hcm/themes/"+themes+"/icon/icon23_hover.png\" style='margin-left:6px'/>");
	        hidebar.append("</a></li>");
			Document doc=getDocument();
			
			//查询工具条数据
	        Element rootnode=doc.getRootElement();
	        if(menu_id!=null && !"".equals(menu_id)){
	        	XPath xPath = XPath.newInstance("/hrp_menu/menu[@id='"+menu_id+"']");
	        	rootnode = (Element) xPath.selectSingleNode(doc);
			}
	        if(rootnode==null || "false".equals(rootnode.getAttribute("menuhide"))){
	        	return;
	        }
	        
	        //查询是要显示退出按钮
	        boolean exitbFlag = false;
	        XPath xpath = XPath.newInstance("menu[@id='9006']");
	        Element exitNode = (Element)xpath.selectSingleNode(rootnode);
	        if(exitNode !=null){
	        	String func_id=exitNode.getAttributeValue("func_id");
	        	String mod_id=exitNode.getAttributeValue("mod_id");	   
	        	if(haveFuncPriv(func_id,mod_id))
	        		exitbFlag = true;
	        }
	        	
	        List list=rootnode.getChildren();
	        if(list.size()==0)
	        	return;
	        
	        StringBuffer temHtml = new StringBuffer();
	        //显示的按钮个数 
	        int k=exitbFlag?1:0;
	        while(list.size()>0){
	        	
	        	Element element=(Element)list.get(0);
	        	list.remove(0);
	        	
	        	String func_id=element.getAttributeValue("func_id");
	        	String mod_id=element.getAttributeValue("mod_id");	   
	        	String id=element.getAttributeValue("id");
	        	String menuhide = element.getAttributeValue("menuhide");
	        	//签到签退按钮
	        	if(("9004".equals(id) || "9005".equals(id)) && !bself)
	        		continue;
	        	
	        	if(haveFuncPriv(func_id,mod_id) && !"false".equals(menuhide))
	        	{
	        		//如果退出按钮，先跳过。因为退出按钮 要总是在最后
	        		if("9006".equals(id)){
	        			continue;
	        		}
	        		k++;
	        		if(k>6) {
	        			if(k == 7) {
	        				icon2Html(element,temHtml,themes);
	        			}
	        			
	        			icon2Html(element,hidebar,themes);
	        		} else
	        		    icon2Html(element,htmlStr,themes);
	        		
	        		
	        	}
	        }
	       
	        if(k == 7) {
        		htmlStr.append(temHtml);
        	}
	        //退出按钮总是在可见位置
	        if(exitbFlag){
	        	icon2Html(exitNode,htmlStr,themes);
	        }
	        
	        //如果按钮超过六个添加下拉按钮
	        if(k>7){
	        	htmlStr.append("<a href=\"###\" onclick=\"javascript:showhidden('navid','block');\" style=\"margin-top:20px;cursor:hand;\">");
	    		htmlStr.append("<li style=\"padding-top:10px;padding-bottom:3px;\">");
	    		htmlStr.append("<img  class=\"png\"  src=\"/images/hcm/themes/"+themes+"/icon/icon23.png\"  />");
	    		htmlStr.append("</li>");
	    		htmlStr.append("</a>");
	        }
	        
	        htmlStr.append("</ul>");
	        titleFunction(htmlStr,userView,themes);
	        htmlStr.append("</div>");
	        
	        
	        hidebar.append("</ul>");
	        hidebar.append("</div>");
	        
	        if(k>7){
	            htmlStr.append(hidebar);
	        }else if(k<8){
	        	htmlStr.append("<script>$('.function').css(\"marginLeft\","+(7-k)*30+")</script>");
	        }
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	//输出头部功能（搜索框还是欢迎文字）
	private void titleFunction(StringBuffer htmlStr,UserView userView,String userthemes){
		
		Connection conn = null;
		try{
			String convenient_search=SystemConfig.getPropertyValue("convenient_search");
		
		  //搜索框
			if("1".equals(convenient_search)){ 
				String generalmessage ="可以输入\"姓名\"";
				String selectmessage ="请输入\"姓名\"";
				if("1".equals(convenient_search)){
						conn = AdminDb.getConnection();
						Sys_Oth_Parameter sysbo=new Sys_Oth_Parameter(conn);
								 String onlyname = sysbo.getCHKValue(Sys_Oth_Parameter.CHK_UNIQUENESS,"0","name");
								 FieldItem item = DataDictionary.getFieldItem(onlyname);
									if (item != null&&!"a0101".equalsIgnoreCase(onlyname)&&!"0".equals(userView.analyseFieldPriv(item.getItemid()))) {
										generalmessage+=",\""+item.getItemdesc()+"\"";
										selectmessage+=",\""+item.getItemdesc()+"\"";
									}
									String pinyin_field = sysbo.getValue(Sys_Oth_Parameter.PINYIN_FIELD_SEARCH);
									item  = DataDictionary.getFieldItem(pinyin_field.toLowerCase());
									if (!(pinyin_field == null|| "".equals(pinyin_field) || "#".equals(pinyin_field)||item==null|| "0".equals(item.getUseflag()))&&!"a0101".equalsIgnoreCase(pinyin_field)&&!"0".equals(userView.analyseFieldPriv(item.getItemid()))){
										generalmessage+=",\""+item.getItemdesc()+"\"";
										selectmessage+=",\""+item.getItemdesc()+"\"";
									}
									generalmessage+="进行查询";
									selectmessage+="进行查询";
				}
				
		        htmlStr.append("<div class=\"serarea\">");
		        htmlStr.append("<input type=\"text\" id=\"selectname\" class=\"search\" title='"+generalmessage+"' onkeypress=\"queryperson(event);\" />");
		        htmlStr.append("<script>selectmessage = '"+selectmessage+"'</script>");
		        htmlStr.append("</div>");
			}else{//欢迎字幕
				 //欢迎提示是否显示人员机构信息 xuj add 2013-11-26
			    StringBuffer textstr=new StringBuffer();
			    String value=SystemConfig.getPropertyValue("display_employee_info");
			    boolean bvalue=false;
			    if(value.length()==0|| "true".equalsIgnoreCase(value))
			    {
			    	bvalue=true;
			    } 
			    
			    String sys_name=SystemConfig.getPropertyValue("sys_name");
			    if(sys_name.length()==0)
			    {
			    	sys_name=SystemConfig.getPropertyValue("frame_index_title");
			    }
			    
			    String display_field_info = SystemConfig.getPropertyValue("display_field_info").toLowerCase();
			    if(SystemConfig.isScrollWelcome()&&bvalue)
			    {
			    	if(display_field_info.length()==0){
					    String orgid=userView.getUserOrgId();
					    String deptid=userView.getUserDeptId();
					    String posid=userView.getUserPosId();
					    textstr.append("&nbsp;");
					    textstr.append(AdminCode.getCodeName("UN",orgid));
					    textstr.append("&nbsp;");
					    textstr.append(AdminCode.getCodeName("UM",deptid));
					    textstr.append("&nbsp;");
					    textstr.append(AdminCode.getCodeName("@K",posid));
			    	}else{
			    		if(display_field_info.indexOf("b0110")!=-1){
			    			String orgid=userView.getUserOrgId();
			    			textstr.append("&nbsp;");
			    			textstr.append(AdminCode.getCodeName("UN",orgid));
			    		}else if(display_field_info.indexOf("e0122")!=-1){
			    			String deptid=userView.getUserDeptId();
			    			textstr.append("&nbsp;");
			    			textstr.append(AdminCode.getCodeName("UM",deptid));
			    		}else if(display_field_info.indexOf("e01a1")!=-1){
			    			String posid=userView.getUserPosId();
			    			textstr.append("&nbsp;");
			    			textstr.append(AdminCode.getCodeName("@K",posid));
			    		}
			    	}
			    	textstr.append("&nbsp;");
			    	textstr.append(userView.getUserFullName());
			    	textstr.append("&nbsp;");
			    }
				
				if(SystemConfig.isScrollWelcome()){
					htmlStr.append("<div class=\"cserarea\"><h1>");
					htmlStr.append("<div style=\"margin-left: 110px;\" >欢迎 "+textstr.toString().trim()+"登录</div>");
					htmlStr.append("</h1></div>");
			   	}
		   	}
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			PubFunc.closeDbObj(conn);
		}
		
	}
	
	//将element转成<li>html
	private void icon2Html(Element ele,StringBuffer htmlStr,String themes){
		
		String name = ele.getAttributeValue("name");
    	String iconv7 = ele.getAttributeValue("iconv7");
		String url = ele.getAttributeValue("url");
		url = url==null || url.trim().length()<1?"####":url;
		//js标识
		boolean function  = false;
		//以javascript 开头
		if(url.length()>11 && "javascript:".equalsIgnoreCase(url.substring(0, 11)))
			function = true;
		String target = ele.getAttributeValue("target");
		htmlStr.append("<li >");
		htmlStr.append("<a style=\"cursor:pointer;\" ");
		//如果设置了js方法，就不走链接了
		if(function){
			htmlStr.append(" href=\"####\" ");
			htmlStr.append(" onclick=\""+url+"\" " );
		}else{
			//将url链接参数加密为一个参数encryptParam  xuj add 2014-9-2  start
			int index = url.indexOf("&");
			if(index>-1){
				String allurl = url.substring(0,index);
				String allparam = url.substring(index);
				url = allurl+"&encryptParam="+PubFunc.encrypt(allparam);
			}
			//将url链接参数加密为一个参数encryParam  xuj add 2014-9-2  end
		    htmlStr.append(" href=\""+url+"\" target=\""+target+"\" ");
		}
		
		htmlStr.append(" >");
		
//		if(iconv7!=null && iconv7.length()>0)
//			htmlStr.append("<img class=\"png\" title=\""+name+"\" alt=\""+name+"\" src=\"/images/hcm/themes/"+themes+"/nav/"+iconv7+"\"/>");
		if(iconv7!=null && iconv7.length()>0 && !"little_nav.png".equalsIgnoreCase(iconv7))//不是大图 显示默认图片 wangb 20170815
			htmlStr.append("<div title=\""+name+"\" style=\"width:18px;height:18px;background:url(/images/hcm/themes/"+themes+"/nav/"+iconv7+") no-repeat 0px 0px;\"></div>");
		else if(iconv7!=null && iconv7.length()>0 && "little_nav.png".equalsIgnoreCase(iconv7)){//替换为大图 需要图片定位 wang 20170815
			String position = ele.getAttributeValue("position");
			String[] pos = position.split(",");
			htmlStr.append("<div title=\""+name+"\" style=\"width:18px;height:18px;background:url(/images/hcm/themes/"+themes+"/nav/"+iconv7+") no-repeat "+pos[0]+"px "+pos[1]+"px;\"></div>");
		}else{
			
			//如果没有图片显示字。长度为4byte 如果有汉字处理一下
			char[] t ={' ',' ',' ',' '};
			int size=0;
			for(int i=0;i<name.toCharArray().length;i++){
				size ++;
				int k = (int)name.charAt(i);
				if(k<=0||k>=126)
					size++;
				if(size>4)
					break;
				t[i] = name.charAt(i);
			}
			String text = new String(t).trim();
		  htmlStr.append("<span style=\"width:25px;margin:0 -3 0 -4px;cursor:pointer;text-align: center;\" title=\""+name+"\" >"+text+"</span>");
		}
		htmlStr.append("</a>");
		htmlStr.append("</li>");
		
	}
	
	
	public int doStartTag() throws JspException {
        try
        {   //2015-12-04  chenxg  add  v70以上版本模块二次认证不起作用
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
        	
        	if("toolbar".equals(menutype)){
        		//调用toolbar生成html方法
        		toolbarHTML(menu_id,buf);
        		
        	}else if("menuitem".equals(menutype)){
        		this.first_url="";
        		//生成二级按钮（此处二级按钮不要和二级菜单按钮混淆，两个概念）
        		menuitemHTML(menu_id,buf);
        	}else{
	        	//一级导航菜单
	        	menuNum=0;
	        	showRoot = false;
	        	if(this.menu_id==null||this.menu_id.length()==0){
	        		String html=outMenuHTML();
	        		/*changeStart 
	        		 * 如果没有任何菜单权限，不显示左侧菜单栏，直接显示面板界面
	        		 * guodd 2017-11-09
	        		 * */
	        		if(menuNum==0){
	        			showRoot=true;
	        		}
	        		if(showRoot){
	        			centerurl = PubFunc.encrypt("/templates/index/hcm_portal.do?b_query=link");
	        		}
	        		/*changeEnd*/
	        		buf.append(html);
	        		buf.append("\n");
	        		buf.append("<script language=\"javascript\">");
	        		buf.append("var menuNum");
					buf.append("=");
					buf.append(menuNum);
					buf.append(";");
					buf.append("var showRoot = ");
					buf.append(showRoot);
					buf.append(";");
					buf.append("var menu_id");
					buf.append("=");
					buf.append("'"+(menu_id.length()==0?"00":menu_id)+"'");
					buf.append(";");
					buf.append("var menu_name");
					buf.append("=");
					buf.append("'"+(menu_id.length()==0?" ":menuName)+"'");
					buf.append(";");
					buf.append("var mod_id");
					buf.append("=");
					buf.append("'"+(menu_id.length()==0?"-1":((modId==null||modId.indexOf(",")!=-1)?"-1":modId))+"'");
					buf.append(";");
					buf.append("var allname");
					buf.append("=");
					buf.append("'"+(menu_id.length()==0?"":allname)+"'");
					buf.append(";");
					buf.append("var centerurl");
					buf.append("=");
					buf.append("'"+(centerurl.length()==0?"":centerurl)+"'");
					buf.append(";");
					buf.append("var centertarget");
					buf.append("=");
					buf.append("'"+(centertarget.length()==0?"":centertarget)+"'");
					buf.append(";");
					buf.append("</script>");
	        	}else{//二级导航菜单，还采用extjs生成，优化器css样式
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
	        	}
        	}
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
	
	private void menuitemHTML(String menu_id,StringBuffer buf){
		if(menu_id!=null&&menu_id.length()>0){
			buf.append("<link rel=\"stylesheet\" type=\"text/css\" href=\"../../ext/resources/css/ext-all.css\" /><script type=\"text/javascript\" src=\"../../ext/ext-all.js\"></script><script type=\"text/javascript\" src=\"../../ext/rpc_command.js\"></script>");
			buf.append("<link rel=\"stylesheet\" type=\"text/css\" href=\"/css/hcm/themes/"+themes+"/menu.css\" />");
			buf.append("<link rel=\"stylesheet\" type=\"text/css\" href=\"/css/hcm/themes/"+themes+"/content.css\" />");
			buf.append("<style type=\"text/css\">.x-panel-header{border-width:0 1px 0 0;}" +
					".x-panel-body{border-width:0 1px 0 0;}" +
					".x-panel-default{border-width:0 1px 0 0;}" +
					".x-tree-node a span, .x-dd-drag-ghost a span{padding-right:20px;}" +
					".x-tree-node{text-align:right;border-bottom:0;}" +
					".x-grid-cell-inner-treecolumn{border-bottom:0;}" +
					".x-tree-icon-leaf{background-image:none !important;}" +
					".x-grid-cell-inner-treecolumn{float:right; padding-right:18px;}" +
					".x-grid-row-focused .x-grid-td{border-bottom:none}" +
					".x-grid-row-before-focused .x-grid-td{border-bottom:none}" +
					".x-grid-row-over .x-grid-td{border-bottom:none}" +
					".x-panel-header-text-container-default{line-height:0}" +
					//".x-grid-body .x-grid-table-focused-first{border-bottom:1px dashed rgb(222, 228, 235)}" +
					"</style>");
			
		//读取menu.xml配置文件相应menu节点,按extjs生成Tree二级按钮列表
			String js=outMenuJs();
			//二级菜单设置二次加密字体变大，特殊处理    wangbo 2020-02-10  bug 56275
			JSONArray jsonArray = JSONArray.fromObject(js);
			for(int i = 0 ; i < jsonArray.size() ; i++) {
				JSONObject jsonObject = (JSONObject) jsonArray.get(i);
				String validateType = (String) jsonObject.get("validateType");
				if(validateType != null && validateType.trim().length() >0) {
					jsonObject.put("text", "<span style='font-size:12px;'>"+jsonObject.getString("text")+"<span>");
					break;
				}
			}
			js = jsonArray.toString();
			//zxj 20150630 menuName不再写死为“个人设置”，改为从menu.xml中读取
			String menuName = getMenuName(menu_id);
        	buf.append("<script language=\"javascript\">");
        	buf.append("var ");
        	buf.append(this.name);
			buf.append("=");
			buf.append(js);
			buf.append(";");
			buf.append("</script>");
			
			buf.append("<script language=\"javascript\">");
			//zxj 20150713 指定EXT空图片名，否则会访问ext自带的图片，如不能访问互相网则导致显示带X的空图片
			buf.append("Ext.BLANK_IMAGE_URL='/images/s.gif';");
        	buf.append("var viewport;");
			buf.append("var curr_module,curr_pnl,curr_node;");
			buf.append("var il_body='<iframe src=\"\" name=\""+center_target+"\" id=\"center_iframe\" scrolling=\"auto\" width=\"100%\" height=\"100%\" frameborder=\"0\"></iframe> ';");
			buf.append("if(navigator.userAgent.match(/iPad|iPhone/i)) ");
			buf.append("il_body='<div id=\"iframebox\">'+il_body+'</div>';");
			buf.append("Ext.onReady(function(){");
			buf.append("var hcmcenter = new Ext.Panel({title: '',border:false,region:'center',html: il_body,cls:'empty'});");
			buf.append("window.treeStore = Ext.create('Ext.data.TreeStore', {root: {text: '根节点',expanded: true,children:"+this.name+"}});");
			buf.append("var hcmtree = Ext.create('Ext.tree.Panel', {useArrows: false,region:'west',title: '<h1 style=\"text-align:center;font-color:#fff;\">" + menuName + "</h1>',width:130,margins:'0 0 0 0',minSize: 130, maxSize: 130, border:true,autoScroll:true,collapsible: false,collapseMode:'mini', split:false,layoutConfig:{animate:true},store: treeStore, rootVisible: false });");
			buf.append("hcmtree.on(\"click\",function(node,event){});");
			//二级按钮，控制二次密码验证添加 beforeitemclick事件，同时在validate.js 里补充具体的实现方法入口validateClick 方法  wangb 2019-12-07 bug 55918
			buf.append("hcmtree.on(\"beforeitemclick\",function(view, record, item, index, e,obj){if(validateClick){validateClick(view, record, item, index, e,obj)}else{ return true;}});");
			buf.append("hcmtree.on(\"expandnode\",function(node){if(node.attributes[\"mod_id\"]!=undefined){curr_pnl=this;curr_node=node;var map = new HashMap();map.put(\"module\",node.attributes[\"mod_id\"]);map.put(\"auth_lock\",\"true\");Rpc({functionId:'1010010206',success:authorize},map);} });");
			buf.append("viewport = new Ext.Viewport({layout:'border',items:[hcmtree,hcmcenter]});");
			buf.append("var node = hcmtree.getRootNode();");
			buf.append("var firstNode = node.childNodes[0];");
			buf.append("hcmtree.getSelectionModel().select(node.childNodes[0]);");
			buf.append("if(!firstNode.raw.validateType){window.open(\""+this.first_url+"\",\""+center_target+"\");}else{if(\"1\"==firstNode.raw.validateType){check(firstNode.raw.targetToUse,firstNode.raw.urlToUse);}else if(\"2\"==firstNode.raw.validateType || \"1,2\"==firstNode.raw.validateType){smscheck(firstNode.raw.targetToUse,firstNode.raw.urlToUse,(\"1,2\"==firstNode.raw.validateType));}};");
			buf.append("}); ");
			buf.append("function getModuleNode(name){var node,mobj;for(var i=0;i<modulemenu.length;i++){var mobj=modulemenu[i];if(mobj.text==name){node=mobj;break;}}return node; }");
			buf.append("function handle(pnl){var name=pnl.id;var title=pnl.title;var node=getModuleNode(name);curr_module=node;curr_pnl=pnl;if(node.mod_id){var map = new HashMap();map.put(\"module\",node.mod_id);map.put(\"auth_lock\",\"true\");Rpc({functionId:'1010010206',success:authorize},map); }} ");
			buf.append("function authorize(response){var value=response.responseText;var map=Ext.util.JSON.decode(value);if(map.succeed==false){alert(map.message); if(curr_pnl.id=='自助服务'||curr_pnl.id=='绩效自助'){curr_node.collapse(true);}else{ curr_pnl.collapse(true);}if(map.message==\"会话超时,请重新登录!\"){var newwin=window.open(window.location,\"_top\",\"toolbar=no,location=0,directories=0,fullscreen=0,status=no,menubar=no,scrollbars=no,resizable=yes\");window.opener=null;self.close();} }else{var href,target;if(curr_module){href=curr_module.href;target=curr_module.hrefTarget;if(href){window.open(href,target);}      }  }}");
			buf.append("</script>");
		}
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getMenu_id() {
		return menu_id;
	}

	public void setMenu_id(String menu_id) {
		this.menu_id = menu_id;
	}

	public String getMax_menu() {
		return max_menu;
	}

	public void setMax_menu(String max_menu) {
		this.max_menu = max_menu;
	}

	public String getTarget() {
		return target;
	}

	public void setTarget(String target) {
		this.target = target;
	}

	public String getThemes() {
		return themes;
	}

	public void setThemes(String themes) {
		this.themes = themes;
	}
	
	public String getMenutype() {
		return menutype;
	}

	public void setMenutype(String menutype) {
		this.menutype = menutype;
	}
	
	public String getCenter_target() {
		return center_target;
	}

	public void setCenter_target(String center_target) {
		this.center_target = center_target;
	}

	
	/**
	 * 获取功能权限范围下第一个功能菜单链接
	 * @param el
	 * @return
	 */
	private String findUrl(Element el){
		String url ="";
		String elfunc_id=el.getAttributeValue("func_id");
	    	String elmod_id=el.getAttributeValue("mod_id");
	    	if(haveFuncPriv(elfunc_id,elmod_id)){
	    		url = el.getAttributeValue("url");
			if(url.length()>0&&url.indexOf("br_blank=link")==-1){
				//顶级菜单默认打开第一个子节点连接，将url链接参数加密为一个参数encryptParam，跟点击实际子菜单加密保持一致 guodd 2018-03-02
				int index = url.indexOf("&");
				if(index>-1){
					String allurl = url.substring(0,index);
					String allparam = url.substring(index);
					url = allurl+"&encryptParam="+PubFunc.encrypt(allparam);
				}
				return url.replaceAll("&", "`");
			}else{
				List childel = el.getChildren();
				UserView userview=(UserView) pageContext.getSession().getAttribute(WebConstant.userView);	      
				for(int i=0;i<childel.size();i++){
					Element element = (Element)childel.get(i);
					
					String func_id=element.getAttributeValue("func_id"); 
		        	String _url=element.getAttributeValue("url"); 
		        	if(!PubFunc.hasPriMenu(func_id,_url,userview)) //根据锁版本号控制人事异动or薪资的新旧程序
		        		continue;
					
					
					url = this.findUrl(element);
					if(url.length()>0&&url.indexOf("br_blank=link")==-1)
						break;
				}
			}
		}
		return url.replaceAll("&", "`");
	}
	
}
