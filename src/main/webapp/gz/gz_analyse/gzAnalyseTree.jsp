<%@ page contentType="text/xml; charset=UTF-8"%>
<%@ page language="java" %>
<%@ page import="com.hjsj.hrms.interfaces.gz.ItemTreeByXml"%>
<%@ page import="com.hjsj.hrms.interfaces.gz.GzAnalyseTree"%>
<%@ page import="com.hrms.struts.exception.*" %>
<%@ page import="com.hrms.struts.valueobject.UserView"%>
<%@ page import="com.hrms.struts.constant.WebConstant"%>

<%
    response.setContentType("text/xml;charset=UTF-8");
    UserView userView=(UserView)session.getAttribute(WebConstant.userView);
	String rsid = request.getParameter("rsid");
	String gz_module = request.getParameter("gz_module");
	if(rsid==null)
		rsid="";
	GzAnalyseTree gzAnalyseTree = new GzAnalyseTree(rsid,gz_module,userView);
	try
	{
	  String xmlc=gzAnalyseTree.outPutXmlStr();  //create xtree.js treeview.
	  //out.println(xmlc);
	  response.getWriter().write(xmlc);
	  response.getWriter().close();
	}
	catch(Exception e)
	{
      	    e.printStackTrace();
	}
	
%>
