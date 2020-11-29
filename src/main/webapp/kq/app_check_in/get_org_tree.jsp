<%@ page contentType="text/xml; charset=UTF-8"%>
<%@ page language="java" %>
<%@ page import="com.hjsj.hrms.interfaces.kq.CreateTree"%>
<%@ page import="com.hrms.struts.exception.*" %>
<%
    	response.setContentType("text/xml;charset=UTF-8");
	String params=(String)request.getParameter("params");
	String flag=request.getParameter("flag");
	String straction="/kq/app_check_in/manuselect.do";
	CreateTree orgxml=new CreateTree(params,straction,"mil_body",flag);
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



	

