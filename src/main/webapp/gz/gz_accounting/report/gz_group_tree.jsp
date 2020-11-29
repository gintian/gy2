
<%@ page contentType="text/xml; charset=UTF-8"%>
<%@ page language="java" %>
<%@ page import="com.hjsj.hrms.interfaces.gz.ItemTreeByXml"%>
<%@ page import="com.hjsj.hrms.interfaces.gz.GzGroupScopeTree"%>
<%@ page import="com.hrms.struts.exception.*" %>


<%
    response.setContentType("text/xml;charset=UTF-8");
  
	String codesetid = request.getParameter("codesetid");
	String codeitemid = request.getParameter("codeitemid");

	
	GzGroupScopeTree gzGroupScopeTree = new GzGroupScopeTree(codesetid,codeitemid);
	try
	{
	  String xmlc=gzGroupScopeTree.outPutXml();  //create xtree.js treeview.
	  //out.println(xmlc);
	  response.getWriter().write(xmlc);
	  response.getWriter().close();
	}
	catch(GeneralException e)
	{
      	    e.printStackTrace();
	}
	
%>
