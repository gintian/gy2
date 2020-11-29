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
<frameset cols="250,*"  bordercolor='#DCEFFE' framespacing="2" >
  <frame src="<hrms:insert parameter="HtmlMenu" />" frameborder="1"  name="mil_menu" scrolling="auto" >
  <frame src="<hrms:insert parameter="HtmlBody" />" frameborder="1"  name="mil_body" scrolling="auto" >
</frameset>
 <%}%>
  <%if(ver>40){%>
 <script type="text/javascript">
    var menupnl;
	Ext.onReady(function() {
	 <%if(!"true".equals(framedegrade)){%>
		Ext.define("Diy.resizer.Splitter",{override:"Ext.resizer.Splitter",size:1,cls:'mysplitter'});
		<%}%>
         menupnl = new Ext.Panel({
         header:false,
	     margins:'-1 0 -1 -1',	
		 region:'west' 
		,width:188 
        ,minSize:188
        ,maxSize:188		
		,html:'<iframe src="<hrms:insert parameter="HtmlMenu" />" name="mil_menu" id="center_iframe" scrolling="no" width="100%" height="100%" frameborder="0"></iframe> ' 
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
		,items:[menupnl,
		{ 
		header:false,
		 region:'center' 
	    ,margins:'-1 -1 -1 0'			 
		,html:'<iframe src="<hrms:insert parameter="HtmlBody" />" name="mil_body" id="center_iframe" scrolling="auto" width="100%" height="100%" frameborder="0"></iframe> ' 
			,border:<%=bborder%>
		}] 
		}); 
		
	});  
</script>
 <%}%> 
<iframe id="ifam" src="/pos/police/desc.jsp" scrolling="no" frameborder="0" style="display: none;position: absolute;z-index: 9999; margin:0px;padding:0px;background-color:#FFFF99; "></iframe>
 
 <script type="text/javascript">
	function getElementPos(elementId) {
		var ua = navigator.userAgent.toLowerCase();
		var isOpera = (ua.indexOf('opera') != -1);
		var isIE = (ua.indexOf('msie') != -1 && !isOpera); // not opera spoof
		var el = elementId;
 		if(el.parentNode === null || el.style.display == 'none') {
  			return false;
 		}      
		var parent = null;
 		var pos = [];     
 		var box;     
 		if(el.getBoundingClientRect){//IE         
  			box = el.getBoundingClientRect();
  			var scrollTop = Math.max(document.documentElement.scrollTop, document.body.scrollTop);
  			var scrollLeft = Math.max(document.documentElement.scrollLeft, document.body.scrollLeft);
  			return {x:box.left + scrollLeft, y:box.top + scrollTop};
 		}else if(document.getBoxObjectFor){    // gecko
  			box = document.getBoxObjectFor(el); 
  			var borderLeft = (el.style.borderLeftWidth)?parseInt(el.style.borderLeftWidth):0; 
  			var borderTop = (el.style.borderTopWidth)?parseInt(el.style.borderTopWidth):0; 
  			pos = [box.x - borderLeft, box.y - borderTop];
 		} else {    // safari & opera
  			pos = [el.offsetLeft, el.offsetTop];  
  			parent = el.offsetParent;     
  			if (parent != el) { 
   				while (parent) {  
    				pos[0] += parent.offsetLeft; 
    				pos[1] += parent.offsetTop; 
    				parent = parent.offsetParent;
   				}  
  			}   
  			if (ua.indexOf('opera') != -1 || ( ua.indexOf('safari') != -1 && el.style.position == 'absolute' )) { 
   				pos[0] -= document.body.offsetLeft;
   				pos[1] -= document.body.offsetTop;         
  			}    
 		}              
 		if (el.parentNode) { 
    		parent = el.parentNode;
   		} else {
    		parent = null;
   		}
 		while (parent && parent.tagName != 'BODY' && parent.tagName != 'HTML') { // account for any scrolled ancestors
  			pos[0] -= parent.scrollLeft;
  			pos[1] -= parent.scrollTop;
  			if (parent.parentNode) {
   				parent = parent.parentNode;
 			} else {
   				parent = null;
  			}
 		}
 		return {x:pos[0], y:pos[1]};
	}
 	
 	function getMouseXY(el,str) {
 		desc = document.getElementById("ifam");
// 		var scrollTop = Math.max(document.documentElement.scrollTop, document.body.scrollTop);
//  	var scrollLeft = Math.max(document.documentElement.scrollLeft, document.body.scrollLeft);
 		poss = getElementPos(el);
// 		wx = el.clientLeft + el.clientWidth + scrollLeft +5;
//		wy = el.clientTop + scrollTop;
		document.all['ifam'].contentWindow.document.all['desc'].innerText=str;   
// 		desc.innerText = str;
 		desc.style.top = poss.y;
 		desc.style.left = poss.x + el.offsetWidth + 5;
 		desc.style.width=document.all['ifam'].contentWindow.document.all['desc'].style.width; 		
 		desc.style.display = "block";
 		 desc.style.height=document.all['ifam'].contentWindow.document.all['desc'].clientHeight;		
 	}
 	function hiddenDesc() {
 		desc = document.getElementById("ifam");
 		desc.style.display = "none";
 	}
 </script>
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