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
	  userName = userView.getUserId();
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
   <script language="JavaScript" src="/js/validate.js"></script>
   <script language="JavaScript" src="/js/constant.js"></script>
<!-- 引入Ext 框架 -->
	   <hrms:linkExtJs frameDegradeId="framedegrade"/>
   <%if("hcm".equals(bosflag)){ %>
   <link href="/css/hcm/themes/<%=themes %>/content.css" rel="stylesheet" type="text/css" />
  
  <%} %>   
</head>
<%if(ver<=40){%>
<frameset rows="50%,50%" bordercolor='#DCEFFE' name="forum"  framespacing="3">
  <frame src="<hrms:insert parameter="HtmlupTree" />" frameborder="1" name="a" scrolling="auto" >
  <frame src="<hrms:insert parameter="HtmldownTree" />" frameborder="1" name="b" scrolling="auto" >
</frameset>
 <%}%>
  <%if(ver>40){%>
 <script type="text/javascript">
 	var menupnl;
	Ext.onReady(function() { 
		<%if(!"true".equals(framedegrade)){%>
		Ext.define("Diy.resizer.Splitter",{override:"Ext.resizer.Splitter",size:1,cls:'myUDsplitter'});
		<%}%>
		menupnl = new Ext.Panel({ 
		 margins:'-1 -1 -1 -1'
		,region:'south' 
		,width:200 
		,html:'<iframe src="<hrms:insert parameter="HtmldownTree" />" name="a" id="center_iframe" scrolling="auto" width="100%" height="100%" frameborder="0"></iframe> ' 
		,collapsible:false
		,split:true 
	    ,height:300 
		,collapseMode:'mini' 
			,border:<%=bborder%>
		});
		
		Ext.QuickTips.init(); 
		var viewport = new Ext.Viewport({ 
		id:'simplevp' 
		,layout:'border' 
		,items:[
		{ 
	     region:'center' 
	    ,margins:'-1 -1 -1 -1'	
		,html:'<iframe src="<hrms:insert parameter="HtmlupTree" />" name="b" id="center_iframeb" scrolling="auto" width="100%" height="100%" frameborder="0"></iframe> '
			,border:<%=bborder%>
		},menupnl
		] 
		}); 
		<%if(ver>=70){%>
		var b =  Ext.query(".myUDsplitter");
		
		var clicker = b[0].firstChild;
		if(clicker){
			clicker.className = "myUpClicker";
			clicker.setAttribute("coll","false");
			clicker.onclick = function(event){
		}
			var el;
			el = window.event?window.event.srcElement:event.target;
				if(el){
					
					var coll = el.getAttribute("coll");
					if(coll=="true"){
						el.className = "myUpClicker";
						el.setAttribute("coll","false");
					}else{
						el.className = "myDownClicker";
						el.setAttribute("coll","true");
					}
				}
			};
			<%}%>
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