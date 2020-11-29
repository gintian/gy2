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
<!-- 引入Ext 框架 -->
	    <hrms:linkExtJs frameDegradeId="framedegrade"/> 
   <%if("hcm".equals(bosflag)){ %>
   <link href="/css/hcm/themes/<%=themes %>/content.css" rel="stylesheet" type="text/css" />
  
  <%} %> 
</head>
<%if(ver<=40){%>
<frameset name='forum' cols="200,9,*" border="0" frameborder="no" bordercolor='#C0DEF6' >
  <frame src="<hrms:insert parameter="HtmlMenu" />" frameborder="1" name="mil_menu" scrolling="auto" noresize>
  <FRAME name=toogle marginWidth=0 marginHeight=0 src=/templates/but.jsp noResize scrolling="no">
  <frameset rows="*,150"     border="0" frameborder="no"   scrolling="auto"  name="ril_body"   bordercolor='#C0DEF6' framespacing="2" >
  	  <frame src="<hrms:insert parameter="HtmlBody1" />" frameborder="1" name="ril_body1" scrolling="auto" >
 	  <frame src="<hrms:insert parameter="HtmlBody2" />" frameborder="1" name="ril_body2" scrolling="auto" >
 </frameset>
</frameset>
 <%}%>
 <%if(ver>40){%>
 <script type="text/javascript">
 	var viewport;
	Ext.onReady(function() { 
		<%if(!"true".equals(framedegrade)){%>
		Ext.define("Diy.resizer.Splitter",{override:"Ext.resizer.Splitter",size:1,cls:'mysplitter'});
		<%}%>
		Ext.QuickTips.init(); 
		 viewport = new Ext.Viewport({ 
		 header:false,
		id:'simplevp' 
		,layout:'border' 
		,items:[
		{ 
		header:false,
	     margins:'-1 0 -1 -1',	
		 region:'west' 
		,width:200 
		,html:'<iframe src="<hrms:insert parameter="HtmlMenu" />" name="mil_menu" id="center_iframe" scrolling="auto" width="100%" height="100%" frameborder="0"></iframe> ' 
		,collapsible:false
		,split:true 
		,collapseMode:'mini' 
		,border:<%=bborder%>
		},
		
		{ 
		header:false,
         region:'center',
         layout:'border',
		 margins:'-1 -1 -1 0'
		 ,border:<%=bborder%>,
		 items:[
		    {	
		    header:false,	
			 region:'center' 
		    ,margins:'-1 -1 0 -1'	
			,html:'<iframe src="<hrms:insert parameter="HtmlBody1" />" name="ril_body1" id="center_iframe" scrolling="auto" width="100%" height="100%" frameborder="0"></iframe> '
			,split:true 
			,collapsible:false
			,collapseMode:'mini' 							 
				,border:<%=bborder%>
			},
		    {		
		    header:false,
			 region:'south' 
			 ,id :'iframe_body2'
		    ,margins:'-1 -1 -1 -1'	
			,html:'<iframe src="<hrms:insert parameter="HtmlBody2" />" name="ril_body2" id="center_iframe" scrolling="auto" width="100%" height="100%" frameborder="0"></iframe> '
			,split:true 				
			,collapsible:false
			,collapseMode:'mini' 				 
			,border:<%=bborder%>
			}			
		 ] 
		}
		] 
		}); 
		 
		 <%if(!"true".equals(framedegrade)){%>
		 //窗体之间分隔条样式重定义
		var b =  Ext.query(".mysplitter");
		var splitter1 = Ext.getCmp(b[1].id);
		splitter1.addCls('myUDsplitter');
		var clicker = b[1].firstChild;
		clicker.className = "myUpClicker";
		clicker.setAttribute("coll","false");
		clicker.onclick = function(event){
			var el;
			el = window.event?window.event.srcElement:event.target;
				if(el){
					
					var coll = el.getAttribute("coll");
					if(coll=="true"){
						el.className = "myUpClicker";
						el.setAttribute("coll","false");
						splitter1.setSize(undefined,9);
					}else{
						el.className = "myDownClicker";
						el.setAttribute("coll","true");
						splitter1.setSize(undefined,9);
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