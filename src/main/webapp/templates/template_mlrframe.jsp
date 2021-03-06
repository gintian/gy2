<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>

<%@ page import="com.hrms.struts.valueobject.UserView"%>
<%@ page import="com.hrms.struts.constant.WebConstant"%>
<%@ page import="com.hrms.frame.utility.DateStyle"%>
<%@page import="com.hrms.hjsj.sys.EncryptLockClient,com.hjsj.hrms.businessobject.sys.SysParamBo"%>
<%
    // 在标题栏显示当前用户和日期 2004-5-10 
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
    int ver=lock.getVersion();	
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
<title>人力资源信息管理系统　用户名：<%=userName%>　当前日期：<%=date%></title>
   <link href="<%=css_url%>" rel="stylesheet" type="text/css">
<!-- 引入Ext 框架 -->
	    <hrms:linkExtJs frameDegradeId="framedegrade"/>
   <%if("hcm".equals(bosflag)){ %>
   <link href="/css/hcm/themes/<%=themes %>/content.css" rel="stylesheet" type="text/css" />
  
  <%} %>  
</head>
<%if(ver<=40){%>
<frameset cols="250,*"  bordercolor='#DCEFFE' framespacing="2" >
  <frame src="<hrms:insert parameter="HtmlMenu" />" frameborder="1"  name="mil_menu" scrolling="auto" >
  <frame src="<hrms:insert parameter="HtmlBody" />" frameborder="1"  name="mil_body" scrolling="auto" >
</frameset>
 <%}%>
  <%if(ver>40){%>
 <script type="text/javascript">
 	var menupnl;
    var htmlmenu='<iframe src="<hrms:insert parameter="HtmlMenu" />" name="mil_menu" id="center_iframe" scrolling="auto" width="100%" height="100%" frameborder="0"></iframe> ';
    var htmlbody='<iframe src="<hrms:insert parameter="HtmlBody" />" name="mil_body" id="center_iframe" scrolling="auto" width="100%" height="100%" frameborder="0"></iframe> ';
    if(navigator.userAgent.match(/iPad|iPhone/i)) 
	{
		htmlmenu='<div id="iframebox">'+htmlmenu+'</div>';
		htmlbody='<div id="iframebox">'+htmlbody+'</div>';
	}
 	
	Ext.onReady(function() {
		<%if(!"true".equals(framedegrade)){%>
		Ext.define("Diy.resizer.Splitter",{override:"Ext.resizer.Splitter",size:1,cls:'mysplitter'});
		<%}%>
		menupnl=new Ext.Panel({ 
		header:false,
	     margins:'-1 0 -1 -1',	
		 region:'west' 
		,width:180 
		,html:htmlmenu 
		,collapsible:false
		,split:true 
		,collapseMode:'mini' 
			,border:<%=bborder%>
		}); 
		Ext.QuickTips.init(); 
		var viewport = new Ext.Viewport({ 
		header:false,
		id:'simplevp' 
		,layout:'border' 
		,items:[
		menupnl,
		{ 
		header:false,
		 region:'center' 
	    ,margins:'-1 -1 -1 0'			 
		,html:htmlbody 
		,border:<%=bborder%>
		}] 
		}); 
		
	});  
</script>
 <%}%> 
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