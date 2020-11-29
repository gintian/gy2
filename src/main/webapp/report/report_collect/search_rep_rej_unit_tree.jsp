<%@ page contentType="text/xml; charset=UTF-8"%>
<%@ page language="java" %>

<%@ page import="com.hjsj.hrms.interfaces.report.GetUnitBytabidByXml"%>
<%@ page import="com.hrms.struts.exception.*" %>
<%
   
	response.setContentType("text/xml;charset=UTF-8");
	String unitcode = request.getParameter("unitcode");
	String selfunitcode=request.getParameter("selfunitcode");
	String tsort=request.getParameter("tsort");
	String init=request.getParameter("init");
	GetUnitBytabidByXml wqe = new GetUnitBytabidByXml(selfunitcode,unitcode,tsort,init);
	
	try
	{
	 //create xtree.js treeview.
	
	 String xmls= wqe.outTreeByxml();
	 response.getWriter().write(xmls);
	 response.getWriter().close();	
	}
	catch(Exception e)
	{
      	    e.printStackTrace();
	}

%>