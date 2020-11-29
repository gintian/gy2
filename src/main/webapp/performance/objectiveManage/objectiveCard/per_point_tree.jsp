<%@ page contentType="text/xml; charset=UTF-8"%>
<%@ page language="java" %>

<%@ page import="com.hjsj.hrms.interfaces.performance.PerPointByTree"%>
<%@ page import="com.hrms.struts.exception.*" %>
<%@ page import="com.hrms.struts.valueobject.UserView"%>
<%@ page import="com.hrms.struts.constant.WebConstant"%>

<%
        response.setContentType("text/xml;charset=UTF-8");
  
	String objectid = request.getParameter("objectid");
	String objectType = request.getParameter("objectType");
	String flag=request.getParameter("flag");
	String planid=request.getParameter("planid");
	String id=request.getParameter("id");
	UserView userView=(UserView)session.getAttribute(WebConstant.userView);
	PerPointByTree perPointByTree = new PerPointByTree(objectid,objectType,flag,planid,id,userView);
	try
	{
	  String xmlc=perPointByTree.outPut_Xml();  //create xtree.js treeview.
	  //out.println(xmlc);
	  response.getWriter().write(xmlc);
	  response.getWriter().close();
	}
	catch(GeneralException e)
	{
      	    e.printStackTrace();
	}

%>