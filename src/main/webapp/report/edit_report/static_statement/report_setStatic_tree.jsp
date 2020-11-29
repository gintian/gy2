<%@ page contentType="text/xml; charset=UTF-8"%>
<%@ page language="java" %>

<%@ page import="com.hjsj.hrms.interfaces.report.ReportSetStaticByXml"%>
<%@ page import="com.hrms.struts.exception.*" %>
<%@ page import="com.hrms.struts.valueobject.UserView"%>
<%@ page import="com.hrms.struts.constant.WebConstant"%>

<%
        response.setContentType("text/xml;charset=UTF-8");
    UserView userView=(UserView)session.getAttribute(WebConstant.userView);
	String flag = request.getParameter("flag");
	String codeid = request.getParameter("codeid");
	ReportSetStaticByXml reportSetStaticByXml = new ReportSetStaticByXml(flag,codeid,userView);
	try
	{
	  String xmlc=reportSetStaticByXml.outPutReportSetXml();  //create xtree.js treeview.
	 	response.getWriter().write(xmlc);
	    response.getWriter().close();
	}
	catch(GeneralException e)
	{
      	    e.printStackTrace();
	}

%>