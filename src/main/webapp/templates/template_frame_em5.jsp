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
	if(userView != null){
	  userName = userView.getUserFullName();
    if(css_url==null||css_url.equals(""))
 	  css_url="/css/css1.css";
  
	}
	String date = DateStyle.getSystemDate().getDateString();

%>
<html>
<head>
<title>用户名：<%=userName%>　当前日期：<%=date%></title>
   <link href="<%=css_url%>" rel="stylesheet" type="text/css">
   <script language="javascript" src="/js/validate.js"></script>   
<script language="javascript" src="/js/dict.js"></script>     
</head>
<script language="javascript">
<hrms:sysinfo></hrms:sysinfo>
<hrms:sysmessage></hrms:sysmessage>
</script>
<frameset rows="66,*,21" border="0" frameborder="no">
  <frame src="<hrms:insert parameter="HtmlTopIndex" />" frameborder="no" name="i_top" scrolling="no" noresize>
  <frame src="<hrms:insert parameter="HtmlBody"/>" frameborder="no" name="i_body" scrolling="auto" >
  <frame src="<hrms:insert parameter="HtmlFooter"/>" frameborder="no" name="i_footer" scrolling="no" >  
  
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