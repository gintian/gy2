<%@ page contentType="text/xml; charset=UTF-8"%>
<%@ page language="java" %>

<%@ page import="com.hjsj.hrms.interfaces.sys.bos.MenuMainTree,com.hrms.hjsj.sys.EncryptLockClient"%>
<%@ page import="com.hrms.struts.exception.*,org.jdom.Document,
com.hjsj.hrms.actionform.sys.bos.menu.MenuMainForm,com.hrms.struts.valueobject.UserView" %>

<%
        response.setContentType("text/xml;charset=UTF-8");
        EncryptLockClient lock=(EncryptLockClient)session.getServletContext().getAttribute("lock");
  UserView userView = (UserView)request.getSession().getAttribute("userView");
	String opt = request.getParameter("opt");
	String codeid = request.getParameter("codeid");
	String parent_id = request.getParameter("menu_id"); 
	//String parent_name = request.getParam
	MenuMainTree menuMainTree = new MenuMainTree(opt,codeid,parent_id,userView);
	try
	{
	menuMainTree.setLock(lock);
		MenuMainForm menuMainForm=(MenuMainForm)session.getAttribute("menuMainForm"); 
		Document doc=menuMainForm.getMenu_dom(); 
		//System.out.println("展现doc"+doc);
	  String xmlc=menuMainTree.outPut_Xml(doc);
	  //out.println(xmlc);
	   response.getWriter().write(xmlc);
	    response.getWriter().close();
	}
	catch(GeneralException e)
	{
      	    e.printStackTrace();
	}

%>