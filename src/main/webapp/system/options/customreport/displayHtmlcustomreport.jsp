<%@ page contentType="text/html; charset=UTF-8" language="java"%>
<%@page import="java.util.HashMap"%>
<%@page import="java.net.URLEncoder"%>
<%@page import="java.net.URLDecoder"%>
<%@page import="com.hrms.frame.codec.SafeCode"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean"%>
<%@ taglib uri="/tags/struts-html" prefix="html"%>
<%@ taglib uri="/tags/struts-logic" prefix="logic"%>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms"%>
<%@ page import="com.hrms.struts.valueobject.UserView"%>
<%@ page import="com.hrms.struts.constant.WebConstant"%>
<%@ page import="com.hrms.frame.utility.DateStyle"%>
<%@ page import="com.hrms.struts.constant.SystemConfig"%>
<%
    // 在标题栏显示当前用户和日期 2004-5-10 
    String userName = null;
    String css_url="/css/css1.css";
	UserView userView=(UserView)session.getAttribute(WebConstant.userView);
	if(userView != null)
	{
	  userName = userView.getUserFullName();
	  css_url=userView.getCssurl();
	  if(css_url==null||css_url.equals(""))
	  	css_url="/css/css1.css";
	}
	String date = DateStyle.getSystemDate().getDateString();
	int flag=1;
	String webserver=SystemConfig.getPropertyValue("webserver");
	if(webserver.equalsIgnoreCase("websphere"))
		flag=2;
		

	
%>
<html>
	<head>
		<style>
		<link rel="stylesheet" href="/css/css1.css" type="text/css">
		<script language="javascript" src="/ajax/editor.js"></script>
		</style>
	</head>
	<body style="margin:0px;padding:0px;">
<%//request.getAttribute("html") %>
<%	String filename = (String) request.getAttribute("filename");
	//filename = URLEncoder.encode(filename);
%>
<iframe scrolling="yes"  id="fram" frameborder="0" style="border: 0px;margin:0px;padding:0px;" src="html/<%=filename %>" width="100%" height="100%"></iframe>
	</body>
	<script type="text/javascript">	
	</script>
</html>