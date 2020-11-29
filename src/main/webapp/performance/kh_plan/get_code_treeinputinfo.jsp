
<%@ page contentType="text/xml; charset=UTF-8"%>
<%@ page language="java" %>

<%@ page import="com.hjsj.hrms.interfaces.performance.OrgPersonByXml"%>
<%@ page import="com.hrms.struts.exception.*" %>
<%@ page import="com.hrms.struts.valueobject.UserView"%>
<%@ page import="com.hrms.struts.constant.WebConstant"%>
<%@ page import="com.hrms.frame.utility.DateStyle"%>

<%
	UserView userView=(UserView)session.getAttribute(WebConstant.userView);
    response.setContentType("text/xml;charset=UTF-8");
	String codesetid=(String)request.getParameter("codesetid");
	String codeitemid=(String)request.getParameter("codeitemid");
	String isfirstnode=(String)request.getParameter("isfirstnode");
	OrgPersonByXml codexml=new OrgPersonByXml(codesetid,codeitemid,isfirstnode,userView);
	try
	{
	  String xmlc=codexml.outCodeTree();  //create xtree.js treeview.
	  //out.println(xmlc);
	  response.getWriter().write(xmlc);
	  response.getWriter().close();		  
	}
	catch(GeneralException ee)
	{
      	    ee.printStackTrace();
	}

%>