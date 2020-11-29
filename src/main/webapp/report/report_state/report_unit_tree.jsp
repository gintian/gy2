<%@ page contentType="text/xml; charset=UTF-8"%>
<%@ page language="java"%>

<%@ page import="com.hjsj.hrms.interfaces.report.ReportUnitByXml"%>
<%@ page import="com.hrms.struts.exception.*"%>
<%
    response.setContentType("text/xml;charset=UTF-8");

	String params = request.getParameter("params");

	ReportUnitByXml reportunitxml = new ReportUnitByXml(params,"/report/report_state/reportstatepanel.do" , "mil_body","2","");
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
