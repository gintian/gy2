<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>

<%@ page import="com.hrms.struts.valueobject.UserView"%>
<%@ page import="com.hrms.struts.constant.WebConstant"%>
<%@ page import="com.hrms.frame.utility.DateStyle"%>
<%@page import="com.hrms.hjsj.sys.EncryptLockClient"%>
<%
    // 在标题栏显示当前用户和日期 2004-5-10 
    String userName = null;
    String css_url="/css/css1.css";    
	UserView userView=(UserView)session.getAttribute(WebConstant.userView);
	if(userView != null){
	  userName = userView.getUserId();
    if(css_url==null||css_url.equals(""))
 	  css_url="/css/css1.css";
  
	}
	String date = DateStyle.getSystemDate().getDateString();
    EncryptLockClient lock=(EncryptLockClient)pageContext.getServletContext().getAttribute("lock");
    int ver=50;//lock.getVersion();		
%>
<html>
<head>
<title><bean:message key="welcome.title"/>　<bean:message key="label.mail.username"/>：<%=userView.getUserFullName()%>　<bean:message key="workdiary.message.today"/>：<%=date%></title>
   <link href="<%=css_url%>" rel="stylesheet" type="text/css">
   <%if(ver>40){%>
	    <link rel="stylesheet" type="text/css" href="/ext/resources/css/ext-all.css" />

	    <link rel="stylesheet" type="text/css" href="/ext/resources/css/slate.css" />
	    <script type="text/javascript" src="/ext/adapter/ext/ext-base.js"></script>
	    <script type="text/javascript" src="/ext/ext-all.js"></script>      
   <%}%>     
</head>
<%if(ver<=40){%>
<frameset name="myBody" cols="160,*" bordercolor='#C0DEF6' framespacing="3">
  <frame src="<hrms:insert parameter="HtmlMenu" />" frameborder="yes" name="il_menu" scrolling="auto" >
  <frame src="<hrms:insert parameter="HtmlBody" />" frameborder="yes" name="il_body" scrolling="auto" >
</frameset>
<%}%>
  <%if(ver>40){%>
 <script type="text/javascript">
	Ext.onReady(function() { 
		Ext.QuickTips.init(); 
		var viewport = new Ext.Viewport({ 
		id:'simplevp' 
		,layout:'border' 
		,items:[
		{ 
	     margins:'-1 0 -1 -1',	
		 region:'west' 
		,width:200 
		,html:'<iframe src="<hrms:insert parameter="HtmlMenu" />" name="il_menu" id="center_iframe" scrolling="no" width="100%" height="100%" frameborder="0"></iframe> ' 
		,collapsible:false
		,split:true 
		,collapseMode:'mini' 
		},
		{ 
		 region:'center' 
	    ,margins:'-1 -1 -1 0'			 
		,html:'<iframe src="<hrms:insert parameter="HtmlBody" />" name="il_body" id="center_iframe" scrolling="auto" width="100%" height="100%" frameborder="0"></iframe> ' 
		,border:true 
		}] 
		}); 
	});  
</script>
 <%}%> 
</html>