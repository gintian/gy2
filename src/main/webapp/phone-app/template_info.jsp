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
<meta name="viewport" content="width=device-width">
<title></title>
     <link rel="stylesheet" href="/phone-app/css/default.css" type="text/css">	
<script type="text/javascript">
	function checkNumber(obj,event){//分页标签输入的跳转页面只能是数字
		try{
	    	if (event.keyCode<48 || event.keyCode>57) 
	    	{
	          	  event.returnValue=false;
	    	}
		}catch(e){
	    	alert(e.description);
		}
	}
</script>
</head>
<body>
<table width="100%" align="center" border="0" cellpadding="0" cellspacing="0">
  <tr>  
    <td valign="top">
       <hrms:insert parameter="HtmlBody" />
    </td>
  </tr>
</table>
</body>
</html>