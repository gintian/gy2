package com.hjsj.hrms.utils.components.funcmenu;

import com.hjsj.hrms.businessobject.general.template.TemplateTableParamBo;
import com.hjsj.hrms.businessobject.sys.bos.menu.MenuMainBo;
import com.hjsj.hrms.businessobject.sys.report.Sys_Oth_Parameter;
import com.hjsj.hrms.businessobject.workplan.WorkPlanUtil;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.frame.utility.AdminDb;
import com.hrms.hjsj.sys.EncryptLockClient;
import com.hrms.hjsj.sys.VersionControl;
import com.hrms.struts.constant.SystemConfig;
import com.hrms.struts.constant.WebConstant;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.valueobject.UserView;
import org.apache.commons.lang.StringUtils;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.xpath.XPath;
import org.mortbay.util.ajax.JSON;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class SearchMenuServlet extends HttpServlet{

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		this.doPost(req, resp);
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		
		String menu_id = req.getParameter("node");
		if("root".equals(menu_id))
			menu_id="";
		
		List child = null;
		try{
		if(menu_id==null||menu_id.length()==0){
			child = loadMainMenu(req);
		}else
			child = loadNextMenu(req,menu_id);
		}catch(Exception e){
			e.printStackTrace();
		}
		
		HashMap map  = new HashMap();
		map.put("children", child);
		String ss = JSON.toString(map);
		resp.setCharacterEncoding("utf-8");
		resp.getWriter().write(ss);
	}
	
	private ArrayList loadMainMenu(HttpServletRequest req) throws JDOMException{
		Document doc=getDocument();
		
		ArrayList list = new ArrayList();
		if(doc==null)
			return list;
		
        Element rootnode=doc.getRootElement();
        List menus=rootnode.getChildren();
        XPath xPath = XPath.newInstance("/hrp_menu/menu[@id='21']");
	    	Element ele = (Element) xPath.selectSingleNode(doc);
	    	boolean leadershow = false;
		if(ele!=null&&"false".equals(ele.getAttributeValue("menuhide"))){
			leadershow=true;
		}
		
		for(int i=0;i<menus.size();i++)
        {
	        	Element element=(Element)menus.get(i);
	        	
	        	String menuhide = element.getAttributeValue("menuhide");
	        	if("false".equalsIgnoreCase(menuhide))
	        		continue;
	        	String func_id=element.getAttributeValue("func_id");
	        	String mod_id=element.getAttributeValue("mod_id");
	        	String id=element.getAttributeValue("id");
	        	if(!haveFuncPriv(func_id,mod_id,req))
	        	{
	        		continue;
	        	}
	        	
	        	/**左边主菜单区域排除top快捷菜单项项,移动应用平台,网络学院*/
	        	//如果显示了领导桌面就不显示领导决策
	        	//不显示云菜单
	        	if("04".equalsIgnoreCase(id) || "9999".equalsIgnoreCase(id)/*||id.equalsIgnoreCase("21")*/|| "50".equalsIgnoreCase(id)|| "55".equalsIgnoreCase(id)||(!leadershow&&"11".equals(id)))
	        		continue;
	        	
	        	String name = element.getAttributeValue("name");
	        	String url = element.getAttributeValue("url");
	        	String target  = element.getAttributeValue("target");
	        	String desc = element.getAttributeValue("desc");
			desc = desc==null?"":desc;
			String qicon = element.getAttributeValue("qicon");
			qicon = qicon==null?"default.png":qicon;
			
	        	if("326".equals(func_id)) {
	        	    //zxj 20160612 标准版绩效考核只开放360评价，名称改为考核评价
	        	    EncryptLockClient lock=(EncryptLockClient)req.getSession().getServletContext().getAttribute("lock");
	        	    if (lock.getVersion_flag() == 0) {
	        	    			name = ResourceFactory.getProperty("jx.khplan.param3.title6.std");
	                }	        	    
	        	}
	        
	        		HashMap map = new HashMap();
				
				map.put("id",id);
				map.put("menuid",id);
				map.put("funcid",func_id);
				map.put("name",name);
				map.put("desc",desc);
				map.put("qicon",qicon);
				map.put("url",url);
				map.put("target",target);
				map.put("bevalidate","0");
				List child = element.getChildren();
				if(child.size()==0)
					map.put("leaf", true);
				
				list.add(map);
        }
		
		return list;
	}
	
	private ArrayList loadNextMenu(HttpServletRequest req,String menu_id) throws JDOMException{
		UserView userView=(UserView) req.getSession().getAttribute(WebConstant.userView);
		Document doc=getDocument();
		ArrayList list = new ArrayList();
		if(doc==null)
			return list;
        Element rootnode=doc.getRootElement(); 
        if(menu_id!=null && !"".equals(menu_id)){
        		XPath xPath = XPath.newInstance("/hrp_menu//menu[@id='"+menu_id+"']");
        		rootnode = (Element) xPath.selectSingleNode(doc);
		}
        
        List menus=rootnode.getChildren();
		
        for(int i=0;i<menus.size();i++)
        {
	        	Element element=(Element)menus.get(i);
	        	
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
	        	if(!haveFuncPriv(func_id,mod_id,req)){
	        		continue;
	        	}
	        	
	        	if("0KR020302".equals(func_id)
        				||"0KR020502".equals(func_id)){
	        		//如果有负责部门，则定位到部门上。
    					Connection conn = null;
					try {
						conn = (Connection) AdminDb.getConnection();
						WorkPlanUtil workPlanUtil = new WorkPlanUtil(conn, userView);
	        	            ArrayList deptlist =workPlanUtil.getDeptList(userView.getDbname(), 
	        	                    userView.getA0100());
		        			if(haveFuncPriv(func_id,null,req) && deptlist.size()>0){
		        				
		        			}else{
		        				continue;
		        			}
					} catch (GeneralException e) {
						e.printStackTrace();
					}finally{
						PubFunc.closeDbObj(conn);
					}
	        	}
	        		
	        	if("030101".equalsIgnoreCase(func_id)&& "20030101".equalsIgnoreCase(id))
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
	        	    		url="/workbench/browse/showphoto.do?b_search=link&action=showphotodata.do&target=nil_body&userbase=usr&flag=noself&isUserEmploy=0";
	        		else
	        		    url="/workbench/browse/showinfo.do?b_search=link&action=showinfodata.do&target=nil_body&userbase=&flag=noself&isUserEmploy=0&isphotoview=";
	        	}
	        	
	        		String name = element.getAttributeValue("name");
		        	String target  = element.getAttributeValue("target");
		        	String desc = element.getAttributeValue("desc");
					desc = desc==null?"":desc;
					String qicon = element.getAttributeValue("qicon");
					qicon = qicon==null || qicon.length()<1?"default.png":qicon;
					String bevalidate = element.getAttributeValue("validate");
					bevalidate = "true".equals(bevalidate)?"1":"0";
					String validatetype = SystemConfig.getPropertyValue("validateType");
					validatetype = validatetype==null || validatetype.length()<1?"1":validatetype;
					
        		 	HashMap map = new HashMap();
					map.put("id",id);
					map.put("menuid",id);
					map.put("funcid",func_id);
					map.put("name",name);
					map.put("desc",desc);
					map.put("qicon",qicon);
					map.put("url",url);
					map.put("target",target);
					map.put("bevalidate",bevalidate);
					map.put("validatetype",validatetype);
					
					List child = element.getChildren();
					if(child.size()==0){
						map.put("leaf", true);
						if(url==null || url.length()<1)
			        			continue;
					}
					
					list.add(map);
        }
		return list;
	}
	
	private boolean haveFuncPriv(String function_id,String module_id,HttpServletRequest req)
	  {
	      boolean bfunc=true,bmodule=true;
	      /**
	       * 在这里进行权限分析
	       */
	       /**版本功能控制*/
	      VersionControl ver_ctrl=new VersionControl();	
	      UserView userview=(UserView) req.getSession().getAttribute(WebConstant.userView);	      
          EncryptLockClient lock=(EncryptLockClient)req.getSession().getServletContext().getAttribute("lock");
	      ver_ctrl.setVer(lock.getVersion());
	      
	    //xus 是否判断功能菜单权限参数
		  String isCheckPriv = req.getParameter("isCheckPriv");
		  if("false".equals(isCheckPriv)){
			  if(!(function_id==null|| "".equals(function_id)))
		        {	      
		      	  String[] funcs =StringUtils.split(function_id,","); 
		      	  for(int i=0;i<funcs.length;i++)
		      	  {
		      		  bfunc=ver_ctrl.searchFunctionId(funcs[i],userview.hasTheFunction(funcs[i]))&&haveVersionFunc(funcs[i], lock.getVersion_flag(),req);
		      		  if(bfunc)
		      			  break;
		      	  }   
		       }
			  return bfunc;
		  }
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
      		  bfunc=ver_ctrl.searchFunctionId(funcs[i],userview.hasTheFunction(funcs[i]))&&haveVersionFunc(funcs[i], lock.getVersion_flag(),req);
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
    private boolean haveVersionFunc(String funcid,int ver_s,HttpServletRequest req)
    {
    	UserView userview=(UserView) req.getSession().getAttribute(WebConstant.userView);	  
    	return PubFunc.haveVersionFunc(userview, funcid, ver_s);
    }
	
	private  Document getDocument(){
		MenuMainBo bo = new MenuMainBo();
		Document doc =null;
		try {
			doc =  bo.getDocument();
		} catch (GeneralException e) {
			e.printStackTrace();
		}
		return doc;
	}

	
}
