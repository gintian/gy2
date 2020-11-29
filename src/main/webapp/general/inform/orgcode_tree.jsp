<%@ page contentType="text/xml; charset=UTF-8"%>
<%@ page language="java" %>
<%@ page import="com.hjsj.hrms.interfaces.general.CreateCodeTreeXML"%>
<%@ page import="com.hrms.struts.exception.*" %>

<%
    response.setContentType("text/xml;charset=UTF-8");
    String codesetid = request.getParameter("codesetid");
    String codeitemid=(String)request.getParameter("codeitemid");
    String parentid=(String)request.getParameter("parentid");

	CreateCodeTreeXML codexml=new CreateCodeTreeXML();
	try{
	  	String xmlc = codexml.outCodeOrgTree(codesetid,codeitemid,parentid);  //create xtree.js treeview.
	  	//out.println(xmlc);
	  	 response.getWriter().write(xmlc);
	    response.getWriter().close();
	}catch(GeneralException ee){
      	ee.printStackTrace();
	}
%>