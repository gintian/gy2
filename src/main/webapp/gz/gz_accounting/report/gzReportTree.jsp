
<%@ page contentType="text/xml; charset=UTF-8"%>
<%@ page language="java" %>
<%@ page import="com.hjsj.hrms.interfaces.gz.ItemTreeByXml"%>
<%@ page import="com.hjsj.hrms.interfaces.gz.GzReportTree"%>
<%@ page import="com.hrms.struts.valueobject.UserView"%>
<%@ page import="com.hrms.struts.constant.WebConstant"%> 
<%@ page import="com.hrms.struts.exception.*" %>


<%
    response.setContentType("text/xml;charset=UTF-8");
    UserView userView=(UserView)session.getAttribute(WebConstant.userView);
	String flag = request.getParameter("flag");
	String rsid = request.getParameter("rsid");
	String salaryid=request.getParameter("salaryid");
	if(rsid==null)
		rsid="";

	
	GzReportTree gzReportTree = new GzReportTree(flag,rsid,salaryid,userView);
	try
	{
	  String xmlc=gzReportTree.outPutXml();  //create xtree.js treeview.
	  //out.println(xmlc);
	  response.getWriter().write(xmlc);
	  response.getWriter().close();
	}
	catch(GeneralException e)
	{
      	    e.printStackTrace();
	}
	
%>
