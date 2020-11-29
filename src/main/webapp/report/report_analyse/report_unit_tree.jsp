<%@ page contentType="text/xml; charset=UTF-8"%>
<%@ page language="java" %>

<%@ page import="com.hjsj.hrms.interfaces.report.ReportUnitByXml"%>
<%@ page import="com.hrms.struts.exception.*" %>
<%
    response.setContentType("text/xml;charset=UTF-8");

	String params = request.getParameter("params");
	String backdate = request.getParameter("backdate");
	ReportUnitByXml reportunitxml = new ReportUnitByXml(params,"/report/report_analyse/reportanalyse.do" , "ril_body1","1",backdate);
	try
	{
	  String xmlc=reportunitxml.outPutReportUnitXml();  //create xtree.js treeview.
	  response.getWriter().write(xmlc);
	  response.getWriter().close();	

	}
	catch(GeneralException e)
	{
      	    e.printStackTrace();
	}
%>