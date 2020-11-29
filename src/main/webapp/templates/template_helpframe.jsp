<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>

<%@ page import="com.hrms.struts.valueobject.UserView"%>
<%@ page import="com.hrms.struts.constant.WebConstant"%>
<%@ page import="com.hrms.frame.utility.DateStyle"%>
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
%>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>HRPWEB帮助</title>
   <link href="<%=css_url%>" rel="stylesheet" type="text/css">
</head>

<frameset rows="33,*" name="myBody" framespacing="0" frameborder="0" border="0">
	<frame src="<hrms:insert parameter="HtmlTop" />"  name="mil_top"  scrolling="no" noresize  >
	<frameset cols="200,*" border="2" frameborder="1" name="ril_body" framespacing="4">
		<frame src="<hrms:insert parameter="HtmlMenu" />" frameborder="2" name="mil_menu" scrolling="auto" >
		<frame src="<hrms:insert parameter="HtmlBody" />" frameborder="2" name="mil_body" scrolling="auto" >
	</frameset>
</frameset>
<script language="javascript">
	//解决IE文本框自带历史记录问题  jingq add 2014.12.31
	var inputs = document.getElementsByTagName("input");
	for ( var i = 0; i < inputs.length; i++) {
		if(inputs[i].getAttribute("type")=="text"){
			inputs[i].setAttribute("autocomplete","off");
		}
	}
</script>
</html>