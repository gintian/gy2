
<%@ page contentType="text/xml; charset=UTF-8"%>
<%@ page language="java" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>

<%@ page import="com.hrms.frame.utility.MenuXmlNode"%>
<%@ page import="com.hrms.struts.exception.*" %>


<%
    	response.setContentType("text/xml;charset=UTF-8");
	String parentid=(String)request.getParameter("parentId");
	MenuXmlNode menuxml=new MenuXmlNode(parentid);
	try
	{
		//String xmlc=menuxml.getChildNode();//create XMLSelTree.js　treeview
		String xmlc=menuxml.outXTreeMenu();  //create xtree.js treeview.
		//if(xmlc!=null||xmlc.equals(""))
		//System.out.println("---------->"+xmlc);		
		//out.println("ssss");
		//xmlc="<?xml version=\"1.0\" encoding=\"gb2312\"?><root><car dd=\"我\"></car></root>";
		//System.out.println("---------->"+xmlc);
		out.println(xmlc);
		//response.write(xmlc);

	}
	catch(GeneralException ee)
	{
      	    ee.printStackTrace();
	}

%>




