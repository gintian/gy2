<%@ page contentType="text/xml; charset=UTF-8"%>
<%@ page language="java" %>
<%@ page import="com.hjsj.hrms.interfaces.gz.Financial_voucherXml"%>
<%@ page import="com.hrms.struts.exception.*" %>
<%@ page import="com.hrms.struts.valueobject.UserView"%>
<%@ page import="com.hrms.struts.constant.WebConstant"%>
<%
    	response.setContentType("text/xml;charset=UTF-8");
	    String params=(String)request.getParameter("params");
	    String straction="/gz/voucher/searchvoucherdate.do";
     UserView userView=(UserView)session.getAttribute(WebConstant.userView);
	
	
 
	Financial_voucherXml  orgxml=new Financial_voucherXml (userView,params,straction,"mil_body");
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