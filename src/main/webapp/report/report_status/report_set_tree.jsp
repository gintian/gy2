<%@ page contentType="text/xml; charset=UTF-8"%>
<%@ page language="java" %>

<%@ page import="com.hjsj.hrms.interfaces.report.ReportSetByXml"%>
<%@ page import="com.hrms.struts.exception.*" %>
<%@ page import="com.hrms.struts.valueobject.UserView"%>
<%@ page import="com.hrms.struts.constant.WebConstant"%>

<%
    response.setContentType("text/xml;charset=UTF-8");
    UserView userView=(UserView)session.getAttribute(WebConstant.userView);
	String statusInfo = request.getParameter("statusInfo");
	String flag=request.getParameter("flag");
	ReportSetByXml reportSetByXml = new ReportSetByXml(statusInfo,flag);
	try
	{
	  String xmlc=reportSetByXml.outPutReportSetXml2();  //create xtree.js treeview.
	  response.getWriter().write(xmlc);
	  response.getWriter().close();	
	}
	catch(GeneralException e)
	{
      	    e.printStackTrace();
	}

%>