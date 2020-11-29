<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<html>
  <head>
    
    <title>测试api</title>
    
	<meta http-equiv="pragma" content="no-cache">
	<meta http-equiv="cache-control" content="no-cache">
	<meta http-equiv="expires" content="0">    
	<meta http-equiv="keywords" content="keyword1,keyword2,keyword3">
	<meta http-equiv="description" content="This is my page">
	<!--
	<link rel="stylesheet" type="text/css" href="styles.css">
	-->

  </head>
  <script type="text/javascript" src="/train/resource/course/scorm/scormapi.js"></script>
<link rel="stylesheet" type="text/css" href="/ajax/skin.css"></link>
<script language="javascript" src="/ajax/constant.js"></script>
<script language="javascript" src="/ajax/basic.js"></script>
<script language="javascript" src="/ajax/common.js"></script>
<script language="javascript" src="/ajax/control.js"></script>
<script language="javascript" src="/ajax/dataset.js"></script>
<script language="javascript" src="/ajax/editor.js"></script>
<script language="javascript" src="/ajax/dropdown.js"></script>
<script language="javascript" src="/ajax/table.js"></script>
<script language="javascript" src="/ajax/menu.js"></script>
<script language="javascript" src="/ajax/tree.js"></script>
<script language="javascript" src="/ajax/pagepilot.js"></script>
<script language="javascript" src="/ajax/command.js"></script>
<script language="javascript" src="/ajax/format.js"></script>
<script language="javascript" src="/js/validate.js"></script>

<script language="javascript" src="/js/constant.js"></script>
  <script type="text/javascript">
  	function test() {
	  	var api = new SCOAPI();
	  	api.init("123213");
	  	alert(api.servletURL);
  	}
  	function init() {
  		API = new SCOAPI();
  		API.debug = false;
  	}
  </script>
  <script language="javascript">
    var _checkBrowser=true;
	var _disableSystemContextMenu=false;
	var _processEnterAsTab=true;
	var _showDialogOnLoadingData=true;
	var _enableClientDebug=true;
	var _theme_root="/ajax/images";
	var _application_root="";
	var __viewInstanceId="968";
	var ViewProperties=new ParameterSet();
	var webserver="";
	
</script>
  <body onload="init()">
    <input type="button" value="测试" onclick="test();"/><br>
    <a href="/Courses/test3/LS0603/course01/common/htm/sco01.htm" target="fra">第一节</a><br>
    <a href="/Courses/test3/LS0603/course01/common/htm/sco02.htm" target="fra">第二节</a><br>
    <a href="/Courses/test3/LS0603/course01/common/htm/sco03.htm" target="fra">第三节</a><br>
    <a href="/Courses/test3/LS0603/course01/common/htm/sco04.htm" target="fra">第四节</a><br>
    <a href="/Courses/test3/LS0603/course01/common/htm/sco05.htm" target="fra">第五节</a><br>
    <a href="/Courses/test3/LS0603/course01/common/htm/sco06.htm" target="fra">第六节</a><br>
    <a href="/Courses/test3/LS0603/course01/common/htm/sco07.htm" target="fra">第七节</a><br>
    <a href="/Courses/test3/LS0603/course01/common/htm/sco08.htm" target="fra">第八节</a><br>
    <a href="/Courses/test3/LS0604/default.htm" target="fra">第九节</a><br>
    <iframe name="fra" width="100%" height="100%" frameborder="0" ></iframe>
  </body>
</html>
