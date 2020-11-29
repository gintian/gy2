<%@ page contentType="text/xml; charset=UTF-8"%>
<%@ page language="java" %>

<%@ page import="com.hjsj.hrms.interfaces.help.HRPHelpByXml"%>
<%@ page import="com.hrms.struts.exception.*" %>
<%
    response.setContentType("text/xml;charset=UTF-8");

	String params = request.getParameter("params");

	HRPHelpByXml helpxml = new HRPHelpByXml(params,"/help/hrphelp.do" , "mil_body");

    String xmlc=helpxml.outPutHRPHelpXml();  //create xtree.js treeview.
	//out.println(xmlc);
	response.getWriter().write(xmlc);
	  response.getWriter().close();
%>