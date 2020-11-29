<%@ page contentType="text/xml; charset=UTF-8"%>
<%@ page language="java" %>
<%@ page import="com.hjsj.hrms.interfaces.kq.ItemDirectoryByXml"%>
<%@ page import="com.hrms.struts.exception.*" %>
<%
    	response.setContentType("text/xml;charset=UTF-8");
	    String params=(String)request.getParameter("params");
	    String straction="/kq/options/kq_item_details.do";

	
	
 
	ItemDirectoryByXml  orgxml=new ItemDirectoryByXml (params,straction,"mil_body");
	try
	{
	  String xmlc=orgxml.outTree();  //create xtree.js treeview.
	  //out.println(xmlc);
	  response.getWriter().write(xmlc);
	  response.getWriter().close();
	}
	catch(Exception ee)
	{
      	    ee.printStackTrace();
	}
%>