<%@ page contentType="text/xml; charset=UTF-8"%>
<%@ page language="java" %>

<%@ page import="com.hjsj.hrms.interfaces.gz.ItemTreeByXml"%>
<%@ page import="com.hrms.struts.exception.*" %>


<%
        response.setContentType("text/xml;charset=UTF-8");
  
	String flag = request.getParameter("flag");
	String id = request.getParameter("id");
	String type=request.getParameter("type");

	
	ItemTreeByXml ItemTreeByXml = new ItemTreeByXml(flag,id,type);
	try
	{
	  String xmlc=ItemTreeByXml.outPutXml();  //create xtree.js treeview.
	  //out.println(xmlc);
	  response.getWriter().write(xmlc);
	  response.getWriter().close();
	}
	catch(GeneralException e)
	{
      	    e.printStackTrace();
	}

%>