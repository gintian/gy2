
<%@ page language="java" import="java.util.*" pageEncoding="GB18030"%>
<%
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
  <head>
    <base href="<%=basePath%>">
    
    <title>My JSP 'test_media.jsp' starting page</title>
    
    <meta http-equiv="pragma" content="no-cache">
    <meta http-equiv="cache-control" content="no-cache">
    <meta http-equiv="expires" content="0">
    <meta http-equiv="keywords" content="keyword1,keyword2,keyword3">
    <meta http-equiv="description" content="This is my page">
    
    <!--
    <link rel="stylesheet" type="text/css" href="styles.css">
    -->
	<script language="JavaScript" src="swfobject.js"></script>
	<script language="JavaScript" src="flowPlayer.js"></script>

  </head>
  
  <body>
    This is my JSP page. <br>
    <a href="aaa.WMV">play</a><br>
    <embed src="http://127.0.0.1:8888/courseware/01/1.mp3" loop="-1" width=300 height=140 balance="true" 
    showpositioncontrols="true" showtracker="true" showaudiocontrols="true" showcontrols="true" showstatusbar="true" 
    showdisplay="true" displaysize="0" volume="100" autosize="true" autostart="true" animationatstart="true" 
    transparentatstart="true">
    </embed>     
<br>

<object type="application/x-shockwave-flash" data="http://127.0.0.1:8888/test/FlowPlayerBlack.swf" width="320" height="240" id="FlowPlayer">
	<param name="allowScriptAccess" value="sameDomain" />
	<param name="movie" value="http://127.0.0.1:8888/test/FlowPlayerBlack.swf" />
	<param name="quality" value="high" />
	<param name="scale" value="noScale" />
	<param name="wmode" value="transparent" />
	<param name="allowNetworking" value="all" />
	<param name="flashvars" value="config={ 
		autoPlay: true, 
		loop: false, 
		initialScale: 'scale',
		playList: [
			{ url: 'http://127.0.0.1:8888/courseware/02/0206/12.flv' }
		],
		showPlayListButtons: true
		}" />
</object>    
  </body>
</html>
