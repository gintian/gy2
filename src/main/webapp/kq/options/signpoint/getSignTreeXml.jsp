<%@page import="com.hjsj.hrms.transaction.kq.options.sign_point.GetKqSignPointTreeXml"%>
<%@ page contentType="text/xml; charset=UTF-8"%>
<%@ page language="java" %>
<%@ page import="com.hrms.struts.exception.*" %>
<%@ page import="com.hrms.struts.valueobject.UserView"%>
<%@ page import="com.hrms.struts.constant.WebConstant"%>
<%@ page import="com.hjsj.hrms.utils.PubFunc"%>

<%
   response.setContentType("text/xml;charset=UTF-8");
   String city=(String)request.getParameter("city"); //考勤点分类
   if(city != null)
     city = PubFunc.decryption(city);
   
   UserView userView=(UserView)session.getAttribute(WebConstant.userView);
   GetKqSignPointTreeXml codeTree = new GetKqSignPointTreeXml(city,userView);
   
   String treeXml = codeTree.getTreeXml();
   response.getWriter().write(treeXml);
   response.getWriter().close();

%>