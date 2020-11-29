<%@ page contentType="text/xml; charset=UTF-8"%>
<%@ page language="java" %>

<%@ page import="com.hjsj.hrms.interfaces.performance.SelectPointTree"%>
<%@ page import="com.hrms.struts.exception.*" %>
<%@ page import="com.hrms.struts.valueobject.UserView"%>
<%@ page import="com.hrms.struts.constant.WebConstant"%>

<%
        response.setContentType("text/xml;charset=UTF-8");
  
	String templateID = request.getParameter("templateID");
	String flag = request.getParameter("flag");
	String pointsetid=request.getParameter("pointsetid");
	String subsys_id=request.getParameter("subsys_id");
	String object_type=request.getParameter("object_type");
	String object_id=request.getParameter("object_id");
	UserView userView=(UserView)session.getAttribute(WebConstant.userView);
	SelectPointTree perPointByTree = new SelectPointTree(templateID,pointsetid,flag,subsys_id,userView,object_type,object_id);
	try
	{
	  String xmlc=perPointByTree.GetTreeXMLString();  //create xtree.js treeview.
	  //out.println(xmlc);
	  response.getWriter().write(xmlc);
	  response.getWriter().close();
	}
	catch(GeneralException e)
	{
      	    e.printStackTrace();
	}

%>