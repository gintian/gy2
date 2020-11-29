<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>

<%@ page import="com.hrms.struts.valueobject.UserView"%>
<%@ page import="com.hrms.struts.constant.WebConstant"%>
<%@ page import="com.hrms.frame.utility.DateStyle"%>
<%@ page import="com.hrms.struts.constant.SystemConfig"%>
<%@ page import="org.apache.commons.lang.StringUtils" %>
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
	String value=SystemConfig.getPropertyValue("display_employee_info");
	boolean bvalue=false;
    if(value.length()==0||value.equalsIgnoreCase("true"))
    {
    	bvalue=true;
    }   
    String title=SystemConfig.getPropertyValue("frame_index_title");
	if(title!=null&&title.length()>0)
	{ 
	   title+="&nbsp;";
	} 
%>
<html>
<head>

<title><%=title%></title>

   <link href="<%=css_url%>" rel="stylesheet" type="text/css">
	<script type="text/javascript" src="/components/screenfull/screenfull.js"></script>
	<script type="text/javascript" src="/components/screenfull/browser-polyfill.js"></script>
	<link rel="icon" href="favicon.ico" type="image/x-icon" />
	<link rel="shortcut icon" href="favicon.ico" type="image/x-icon"/>
	<link rel="Bookmark" HREF="favicon.ico"/>
   <script language="javascript" src="/js/validate.js"></script>   
<!--script language="javascript" src="/js/dict.js"></script-->     
</head>
<script language="javascript">
	childEle = undefined;
	//默认是非全屏状态
	isFullScreen = false;
	themes = undefined;
	function fullScreen(ele,color){
		childEle = ele;
		themes = color;
		//IE低版本
		if(window.screenfull ==undefined){
			iefull();
			//判断当前是否是全屏模式
			if(isFullScreen){
				childEle.firstChild.title = "退出全屏";
				childEle.firstChild.style.background = "url(/images/hcm/themes/"+themes+"/nav/little_nav.png) no-repeat -0px -90px ";
			}else{
				childEle.firstChild.title = "全屏";
				childEle.firstChild.style.background = "url(/images/hcm/themes/"+themes+"/nav/little_nav.png) no-repeat -18px -72px ";
			}
		}else{
			screenfull.toggle();
			screenfull.on('change',onchange);
		}
	}
	function onchange(){
		isFullScreen = !isFullScreen;
		//判断当前是否是全屏模式
		if(document.fullscreen||document.MSFullscreenChange||isFullScreen){
			childEle.firstChild.title = "退出全屏";
			childEle.firstChild.style.background = "url(/images/hcm/themes/"+themes+"/nav/little_nav.png) no-repeat -0px -90px ";
		}else{
			childEle.firstChild.title = "全屏";
			childEle.firstChild.style.background = "url(/images/hcm/themes/"+themes+"/nav/little_nav.png) no-repeat -18px -72px ";
		}
	}
	//IE低版本进入和退出全屏
	function iefull() {
		isFullScreen = !isFullScreen;
		if (typeof window.ActiveXObject != "undefined") {
			//模拟f11键，使浏览器全屏
			var wscript = new ActiveXObject("WScript.Shell");
			if (wscript != null) {
				wscript.SendKeys("{F11}");
			}
		}
	}
	window.onresize = function() {
		if(window.screenfull ==undefined){
			//+ 5 是因为body有边框，导致全屏时网页的高度和浏览器的高度不完全一致
			if(window.screen.height > document.body.clientHeight + 5){
				isFullScreen = false;
				childEle.firstChild.title = "全屏";
				childEle.firstChild.style.background = "url(/images/hcm/themes/"+themes+"/nav/little_nav.png) no-repeat -18px -72px ";
			}else{
				isFullScreen = true;
				childEle.firstChild.title = "退出全屏";
				childEle.firstChild.style.background = "url(/images/hcm/themes/"+themes+"/nav/little_nav.png) no-repeat -0px -90px ";
			}
		}
	}
<hrms:sysinfo></hrms:sysinfo>
</script>
<frameset rows="*" border="0" frameborder="no" style="border:none">
  <frame src="<hrms:insert parameter="HtmlTopIndex" />" frameborder="no" name="i_top" scrolling="no" noresize>
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