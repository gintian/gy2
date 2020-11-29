<%@ page contentType="text/xml; charset=UTF-8"%>
<%@ page language="java" %>
<%@ page import="com.hjsj.hrms.interfaces.performance.ScoreSortPointTree"%>

<%
    response.setContentType("text/xml;charset=UTF-8");
	String itemHasChild = request.getParameter("itemHasChild");
	String code = request.getParameter("code");
	String planId = request.getParameter("planID");
	String computeFashion = request.getParameter("computeFashion");
	String busitype = request.getParameter("busitype");
  
	if(code==null)
		code="";
	ScoreSortPointTree tree = new ScoreSortPointTree(planId,code,itemHasChild,computeFashion,busitype);
	try 
	{
	  String xmlc=tree.outPutXmlStr();  //create xtree.js treeview.
	 // out.println(xmlc);
	  response.getWriter().write(xmlc);
	  response.getWriter().close(); 
	}
	catch(Exception e)
	{
      	    e.printStackTrace();
	}
	
%>