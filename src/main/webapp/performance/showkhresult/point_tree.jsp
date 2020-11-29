<%@ page contentType="text/xml; charset=UTF-8"%>
<%@ page language="java" %>

<%@ page import="com.hjsj.hrms.interfaces.performance.PointTreeByXml"%>
<%@ page import="com.hrms.struts.exception.*" %>


<%
        response.setContentType("text/xml;charset=UTF-8");
  
	String flag = request.getParameter("flag");
	String id = request.getParameter("id");
	String isItem=request.getParameter("isItem");
	String template_id=request.getParameter("template_id");
	
	
	PointTreeByXml pointTreeByXml = new PointTreeByXml(flag,id,isItem,template_id);
	try
	{
	  String xmlc=pointTreeByXml.outPutOrgXml();  //create xtree.js treeview.
	  //out.println(xmlc);
	  response.getWriter().write(xmlc);
	  response.getWriter().close(); 
	}
	catch(GeneralException e)
	{
      	    e.printStackTrace();
	}

%>