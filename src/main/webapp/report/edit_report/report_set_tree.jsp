<%@ page contentType="text/xml; charset=UTF-8"%>
<%@ page language="java" %>

<%@ page import="com.hjsj.hrms.interfaces.report.ReportSetByXml"%>
<%@ page import="com.hrms.struts.exception.*" %>
<%@ page import="com.hrms.struts.valueobject.UserView"%>
<%@ page import="com.hrms.struts.constant.WebConstant"%>
<%@ page import="com.hrms.frame.utility.AdminDb"%>
<%@ page import="java.sql.Connection"%>
<%
    response.setContentType("text/xml;charset=UTF-8");
    request.setCharacterEncoding("UTF-8");
    UserView userView=(UserView)session.getAttribute(WebConstant.userView);
	String flag = request.getParameter("flag");
	String codeid = request.getParameter("codeid");
	String userName = request.getParameter("userName");
	String operate=request.getParameter("operate");
	Connection con=(Connection)AdminDb.getConnection();
	ReportSetByXml reportSetByXml = new ReportSetByXml(flag,codeid,userName,userView,operate);
	try
	{
	  String xmlc=reportSetByXml.outPutReportSetXml();  //create xtree.js treeview.
	  response.getWriter().write(xmlc);
	  response.getWriter().close();	

	}
	catch(GeneralException e)
	{
      	    e.printStackTrace();
	}finally {
		con.close();
	}


%>