<%@ page contentType="text/xml; charset=UTF-8"%>
<%@ page language="java" %>
<%@ page import="com.hjsj.hrms.interfaces.kq.ClassDirectoryByXml"%>
<%@ page import="com.hrms.struts.exception.*" %>
<%@ page import="com.hrms.struts.valueobject.UserView"%>
<%@ page import="com.hrms.struts.constant.WebConstant"%>
<%
    	response.setContentType("text/xml;charset=UTF-8");
	 String params=(String)request.getParameter("params");
	  UserView userView=(UserView)session.getAttribute(WebConstant.userView);
	ClassDirectoryByXml  orgxml=new ClassDirectoryByXml (userView,params,"","");
	
	try
	{
	  orgxml.setImage("/images/overview_obj.gif");
	  String xmlc=orgxml.outTree();  //create xtree.js treeview.
	  //out.println(xmlc);
	  response.getWriter().write(xmlc);
	  response.getWriter().close();;
	}
	catch(Exception ee)
	{
      	    ee.printStackTrace();
	}
%>
