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
	  css_url=userView.getCssurl();
	  if(css_url==null||css_url.equals(""))
	  	css_url="/css/css1.css";
	}
	String date = DateStyle.getSystemDate().getDateString();
	
%>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>　用户名：<%=userName%>　当前日期：<%=date%>　</title>
<script language="JavaScript" src="/js/validate.js"></script>
<script language="JavaScript">
function pf_ChangeFocus() 
{
   key = window.event.keyCode;

   if ( key==0xD && event.srcElement.tagName!='TEXTAREA'&& event.srcElement.type!='file') /*0xD*/
   {
   	window.event.keyCode=9;
   }
}
function pf_return(form,element) 
{
	document.forms[form].elements[element].focus();
	return false;
}
</script>
   <link href="<%=css_url%>" rel="stylesheet" type="text/css" id="skin">
   <script type="text/javascript" src="/js/hjsjUrlEncode.js"></script>
</head>
<body onKeyDown="return pf_ChangeFocus();" >
<table width="100%" align="center" border="0" cellpadding="0" cellspacing="0" class="mainbackground">
  <tr align="center">  
    <td valign="top" height="30">
       <hrms:insert parameter="HtmlBanner" />
    </td>
  </tr>
  <tr>  
    <td valign="top">
       <hrms:insert parameter="HtmlBody"/>
    </td>
  </tr>
</table>
</body>
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