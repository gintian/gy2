/**
 * 
 */
package com.hjsj.hrms.servlet.sys;

import com.hjsj.hrms.businessobject.kq.KqParameter;
import com.hjsj.hrms.interfaces.sys.CreateOrganizationXml;
import com.hrms.struts.constant.WebConstant;
import com.hrms.struts.valueobject.UserView;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * <p>Title:</p>
 * <p>Description:加载组织机构树和人员列表</p> 
 * <p>Company:hjsj</p> 
 * create time at:Jun 23, 20061:56:01 PM
 * @author chenmengqing
 * @version 4.0
 */
public class LoadOrgEmployServlet extends HttpServlet {
	protected void doGet(HttpServletRequest arg0, HttpServletResponse arg1) throws ServletException, IOException {
		doPost(arg0, arg1);
	}

	
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		//田野添加关系映射定义时查询对应人员库的人员标记为approvalRelationDefine
		String approvalRelationDefine = (String)req.getParameter("approvalRelationDefine");
		String params=(String)req.getParameter("params");
		params=params.replaceAll("％3D", "="); //20140910 dengcan  params会包含=符号，经过过滤器需替换回来，否则报错 
		/* 【6300】工具箱：审批关系，指定审批主体时，手工选人窗口，左侧机构树都带有超链接
		 * opt=9，请求为工具箱，指定审批主体，手工选人，需要清空session中的action
		 * jingq add 2014.12.25
		 */
		String opt = req.getParameter("opt");
		if("9".equals(opt)){
			req.getSession().setAttribute("SYS_LOAD_ORG_ACTION", "");
		}
		String action=(String)req.getSession().getAttribute("SYS_LOAD_ORG_ACTION");
		String target=(String)req.getParameter("target");
		String flag=(String)req.getParameter("flag");
		String dbtype=(String)req.getParameter("dbtype");
		String id=(String)req.getParameter("id");		
		String priv=(String)req.getParameter("priv");
		priv=priv!=null&&priv.trim().length()>0?priv:"1";
		
		String loadtype=(String)req.getParameter("loadtype");
		loadtype=loadtype!=null&&loadtype.trim().length()>0?loadtype:"0";
		
		String isfilter=(String)req.getParameter("isfilter");
		String dbpre=(String)req.getParameter("dbpre");
		String showDbName=req.getParameter("showDbName")!=null?"1":"0";
		String dbvalue = (String)req.getParameter("dbvalue");
		if(req.getParameter("showDb")!=null)
			showDbName=req.getParameter("showDb");
		
		/**是否加载虚拟节点,如果参数未定义，则不加载，等于加载虚组织节点*/
		String lv=(String)req.getParameter("lv");
		lv=lv!=null&&lv.trim().length()>0?lv:"0";
			
		/**是否为虚拟机构节点*/		
		String vg=(String)req.getParameter("vg");
		vg=vg!=null&&vg.trim().length()>0?vg:"0";

		String privtype=(String)req.getParameter("privtype");
		
		 // 考勤组织机构树是否显示岗位
		boolean isPost = true;
		
    	KqParameter para = new KqParameter();
    	if ("kq".equalsIgnoreCase(privtype)&&"1".equalsIgnoreCase(para.getKq_orgView_post())) {
    		isPost = false;
    	} else {
    		isPost = true;
    	}
		String viewunit=(String)req.getParameter("viewunit");
		String ctrlviewunit=req.getParameter("ctrlviewunit")==null?"0":req.getParameter("ctrlviewunit");
		viewunit=viewunit!=null&&viewunit.trim().length()>0?viewunit:"0";
		String nmodule=(String)req.getParameter("nmodule");
		String ctrlmodule=(String)req.getParameter("ctrlmodule");
		String nextlevel=(String)req.getParameter("nextlevel");
		nextlevel=nextlevel!=null&&nextlevel.trim().length()>0?nextlevel:"0";
		
		/** 只列本人所在单位节点 */
		String showSelfNode=(String)req.getParameter("showSelfNode");
		showSelfNode=showSelfNode!=null&&showSelfNode.trim().length()>0?showSelfNode:"0";
		/**是否只显示自己所在部门内人员*/
		String isShowSelfDepts=(String)req.getParameter("isShowSelfDepts");
		isShowSelfDepts=isShowSelfDepts!=null&&isShowSelfDepts.trim().length()>0?isShowSelfDepts:"0";
		
		String chitemid=(String)req.getParameter("chitemid");
		chitemid=chitemid!=null&&chitemid.trim().length()>0?chitemid:"";
		
		String orgcode=(String)req.getParameter("orgcode");
		orgcode=orgcode!=null&&orgcode.trim().length()>0?orgcode:"";
		String umlayer = req.getParameter("umlayer");
		umlayer = umlayer!=null&&umlayer.trim().length()>0?umlayer:"0";
		String cascadingctrl=req.getParameter("cascadingctrl");
		String parent_id=req.getParameter("parent_id");
		String isAddAction=req.getParameter("isAddAction");
		UserView userview=(UserView) req.getSession().getAttribute(WebConstant.userView);		
		CreateOrganizationXml orgxml=new CreateOrganizationXml(params,action,target,flag,dbtype,priv,isfilter,(String) req.getSession().getAttribute("SYS_FILTER_FACTOR"),isPost,(String) req.getSession().getAttribute("SUPPORT_VARIABLE_SQL"),(String) req.getSession().getAttribute("MODEL_STRING"));
		orgxml.setLoadtype(loadtype);
		orgxml.setShowDbName(showDbName);
		orgxml.setPrivtype(privtype);
		orgxml.setIsShowSelfDepts(isShowSelfDepts);
		orgxml.setDbvalue(dbvalue);
		if(userview.getUserId().equals(userview.getA0100()))
		{
			orgxml.setShowSelfNode(showSelfNode);
		}
		else
			orgxml.setShowSelfNode("0");
		orgxml.setChitemid(chitemid);
		orgxml.setOrgcode(orgcode);
		orgxml.setIsAddAction(isAddAction);
		String first=(String)req.getParameter("first");
		if(first==null|| "".equalsIgnoreCase(first))
			orgxml.setBfirst(false);
		else
			orgxml.setBfirst(true);
		if("1".equals(vg))
			orgxml.setBorg(true);
		if("1".equals(lv))
			orgxml.setBloadvorg(true);
		try
		{
			String xmlc="";
			//田野添加关系映射定义时查询对应人员库的人员标记为approvalRelationDefine
		  	if(null!=approvalRelationDefine&&"1".equals(approvalRelationDefine)){
		  		xmlc=orgxml.outOrgEmployTree(userview,id,dbpre,viewunit,ctrlviewunit,nextlevel,umlayer,nmodule,ctrlmodule,cascadingctrl,parent_id,approvalRelationDefine);
		  	}
		  	else{
		  		xmlc=orgxml.outOrgEmployTree(userview,id,dbpre,viewunit,ctrlviewunit,nextlevel,umlayer,nmodule,ctrlmodule,cascadingctrl,parent_id); 
		  	}
		  resp.setContentType("text/xml;charset=UTF-8");
		  resp.getWriter().println(xmlc);   
		}
		catch(Exception ee)
		{
	      ee.printStackTrace();
		}
		
	}

}
