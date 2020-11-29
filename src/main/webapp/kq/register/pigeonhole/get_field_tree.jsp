
<%@ page contentType="text/xml; charset=UTF-8"%>
<%@ page language="java" %>

<%@ page import="com.hjsj.hrms.interfaces.kq.CreateFieldBySetXml"%>
<%@ page import="com.hrms.struts.exception.*" %>


<%
    response.setContentType("text/xml;charset=UTF-8");
	String setname=(String)request.getParameter("setname");
	String intype=(String)request.getParameter("intype");
	
	CreateFieldBySetXml fieldxml=new CreateFieldBySetXml(setname,intype);
	try
	{
	  String xmlc=fieldxml.outFieldByTree();  //create xtree.js treeview.
	  //out.println(xmlc);
	  response.getWriter().write(xmlc);
	  response.getWriter().close();;
	}
	catch(Exception ee)
	{
      	    ee.printStackTrace();
	}

%>