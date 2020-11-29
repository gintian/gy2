<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>

<%@ page import="com.hrms.struts.valueobject.UserView"%>
<%@ page import="com.hrms.struts.constant.WebConstant"%>
<%@ page import="com.hrms.frame.utility.DateStyle"%>
<%@page import="com.hrms.hjsj.sys.EncryptLockClient"%>
<%@page import="com.hrms.hjsj.sys.EncryptLockClient,com.hjsj.hrms.businessobject.sys.SysParamBo"%>
<%
//在标题栏显示当前用户和日期 2004-5-10 
String userName = null;
String css_url="/css/css1.css";
String bosflag = "";
UserView userView=(UserView)session.getAttribute(WebConstant.userView);
if(userView != null){
  userName = userView.getUserFullName();
if(css_url==null||css_url.equals(""))
	  css_url="/css/css1.css";
bosflag = userView.getBosflag();
}
String date = DateStyle.getSystemDate().getDateString();
EncryptLockClient lock=(EncryptLockClient)pageContext.getServletContext().getAttribute("lock");
int ver=50;//lock.getVersion();	
String bborder="true";
if("hcm".equals(bosflag)){
    bborder = "false";
}
String themes="default";
if(userView != null){
    /*xuj added at 2014-4-18 for hcm themes*/
    themes=SysParamBo.getSysParamValue("THEMES",userView.getUserName());
}

%>
<html>
<head>
<title><bean:message key="welcome.title"/>　<bean:message key="label.mail.username"/>：<%=userView.getUserFullName()%>　<bean:message key="workdiary.message.today"/>：<%=date%></title>
   <link href="<%=css_url%>" rel="stylesheet" type="text/css">
   
   <%if(ver>40){%>
	    <link rel="stylesheet" type="text/css" href="/ext/resources/css/ext-all.css" />

	    <script type="text/javascript" src="/js/constant.js"></script>
	    <script type="text/javascript" src="/ext/adapter/ext/ext-base.js"></script>
	    <script type="text/javascript" src="/ext/ext-all.js"></script>      
   <%}%>  
   <%if("hcm".equals(bosflag)){ %>
   <link href="/css/hcm/themes/<%=themes %>/content.css" rel="stylesheet" type="text/css" />
  
  <%} %>      
</head>
<%if(ver<=40){%>
<frameset name="myBody" cols="160,*" bordercolor='#C0DEF6' framespacing="3">
  <frame src="<hrms:insert parameter="HtmlMenu" />" frameborder="yes" name="il_menu" scrolling="auto" >
  <frame src="<hrms:insert parameter="HtmlBody" />" frameborder="yes" name="il_new_body" scrolling="auto" >
</frameset>
<%}%>
  <%if(ver>40){%>
 <script type="text/javascript">
	Ext.onReady(function() {
		Ext.define("Diy.resizer.Splitter",{override:"Ext.resizer.Splitter",size:1,cls:'mysplitter'});
		Ext.QuickTips.init(); 
		var viewport = new Ext.Viewport({ 
		id:'simplevp' 
		,layout:'border' 
		,items:[
		{ 
			header:false,
	     margins:'-1 0 -1 0',	
		 region:'west' 
		,width:200 
		,html:'<iframe src="<hrms:insert parameter="HtmlMenu" />" name="il_menu" id="center_iframe" scrolling="auto" width="100%" height="100%" frameborder="0"></iframe> ' 
		,collapsible:false
		,split:true 
		,collapseMode:'mini'
		,border:<%=bborder%>
		},
		{ 
		 region:'center' 
	    ,margins:'-1 -1 -1 0'			 
		,html:'<iframe src="<hrms:insert parameter="HtmlBody" />" name="il_new_body" id="center_iframe" scrolling="auto" width="100%" height="100%" frameborder="0"></iframe> ' 
		,border:<%=bborder%>
		}] 
		}); 
	});  
</script>
 <%}%> 
</html>