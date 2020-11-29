<%@ page language="java" import="java.util.*" pageEncoding="ISO-8859-1"%>
<%@ page import="com.hrms.struts.valueobject.UserView"%>
<%@ page import="com.hrms.struts.constant.WebConstant"%>
<%
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";

UserView userView = (UserView) session.getAttribute(WebConstant.userView); 
String repair_reflag = (String)userView.getHm().get("repair_reflag");
if (repair_reflag != null && !repair_reflag.equalsIgnoreCase("")){
    userView.getHm().remove("repair_reflag");
}
%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
  <head>
    <base href="<%=basePath%>">
    
    <title>My JSP 'handlershift.jsp' starting page</title>
    
	<meta http-equiv="pragma" content="no-cache">
	<meta http-equiv="cache-control" content="no-cache">
	<meta http-equiv="expires" content="0">    
	<meta http-equiv="keywords" content="keyword1,keyword2,keyword3">
	<meta http-equiv="description" content="This is my page">
	<!--
	<link rel="stylesheet" type="text/css" href="styles.css">
	-->

  </head>
  
  <body>
   <% if (repair_reflag != null && !repair_reflag.equalsIgnoreCase("")) {%>
   <msg><%=repair_reflag %><amsg>
   <% } %>
  </body>
  <% if (repair_reflag == null || repair_reflag.equalsIgnoreCase("")) {%>
  <script>
  	window.dialogArguments.location.href=window.dialogArguments.document.location.href;
  	window.close();
  </script>
  <% } %>
</html>
