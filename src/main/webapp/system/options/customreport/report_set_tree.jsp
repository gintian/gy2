<%@ page contentType="text/xml; charset=UTF-8"%>
<%@ page language="java" %>

<%@ page import="com.hjsj.hrms.transaction.sys.options.customreport.ReportSetByXml"%>
<%@ page import="com.hrms.struts.exception.*" %>
<%@ page import="com.hrms.struts.valueobject.UserView"%>
<%@ page import="com.hrms.struts.constant.WebConstant"%>

<%
        response.setContentType("text/xml;charset=UTF-8");
    UserView userView=(UserView)session.getAttribute(WebConstant.userView);
	String flag = request.getParameter("flag");
	String codeid = request.getParameter("codeid");
	String userName = request.getParameter("userName");
	String operate=request.getParameter("operate");
	ReportSetByXml reportSetByXml = new ReportSetByXml(flag,codeid,userName,userView,operate);
	try
	{
	  String xmlc=reportSetByXml.outPutReportSetXml();  //create xtree.js treeview.
	  //out.println(xmlc);
	  response.getWriter().write(xmlc);
	  response.getWriter().close();;
	}
	catch(GeneralException e)
	{
      	    e.printStackTrace();
	}

%>
