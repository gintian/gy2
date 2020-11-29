<%@page import="com.hrms.hjsj.sys.EncryptLockClient"%>
<%@ page contentType="text/xml; charset=UTF-8"%>
<%@ page language="java" %>

<%@ page import="com.hjsj.hrms.interfaces.sys.bos.AchivementMainTree"%>
<%@ page import="com.hrms.struts.exception.*,org.jdom.Document,
com.hjsj.hrms.actionform.sys.bos.func.FunctionMainForm" %>

<%
        response.setContentType("text/xml;charset=UTF-8");
  
	String opt = request.getParameter("opt");
	String codeid = request.getParameter("codeid");
	String parent_id = request.getParameter("function_id");
	//节点 的ctrl_ver属性值 用于与parent_id 联合 定位节点 guodd 2018-09-07
	String ctrl_ver = request.getParameter("ctrl_ver");
	//String parent_name = request.getParam
			
	//加载功能权限树时根据 锁版本过滤 guodd 2018-09-07
	EncryptLockClient lock = (EncryptLockClient)session.getServletContext().getAttribute("lock");
	
	AchivementMainTree achivementMainTree = new AchivementMainTree(opt,codeid,parent_id);
	achivementMainTree.setLockVersion(lock.getVersion()+"");
	if(ctrl_ver!=null && ctrl_ver.length()>0)
		achivementMainTree.setCtrl_ver(ctrl_ver);
	try
	{
		FunctionMainForm functionMainForm=(FunctionMainForm)session.getAttribute("functionMainForm"); 
		Document doc=functionMainForm.getFunction_dom(); 
	  String xmlc=achivementMainTree.outPut_Xml(doc);
	  //out.println(xmlc);
	   response.getWriter().write(xmlc);
	    response.getWriter().close();
	}
	catch(GeneralException e)
	{
      	    e.printStackTrace();
	}

%>