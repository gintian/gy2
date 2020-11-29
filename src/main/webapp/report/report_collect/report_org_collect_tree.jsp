<%@ page contentType="text/xml; charset=UTF-8"%>
<%@ page language="java" %>

<%@ page import="com.hjsj.hrms.interfaces.report.ReportCollectUnitByXml"%>
<%@ page import="com.hrms.struts.exception.*" %>
<%
   
	response.setContentType("text/xml;charset=UTF-8");
	String params = request.getParameter("params");
	String isAction=request.getParameter("isAction");
	String cycle_id=request.getParameter("cycle_id");
	if(isAction==null)
		isAction="1";
	ReportCollectUnitByXml reportCollectUnitByXml = new ReportCollectUnitByXml(params,isAction);
	if(isAction.equals("2"))
		reportCollectUnitByXml.setCycle_id(cycle_id);
	
	try
	{
	  String xmlc=reportCollectUnitByXml.outPutReportUnitXml();  //create xtree.js treeview.
	  response.getWriter().write(xmlc);
	  response.getWriter().close();	
	}
	catch(GeneralException e)
	{
      	    e.printStackTrace();
	}

%>