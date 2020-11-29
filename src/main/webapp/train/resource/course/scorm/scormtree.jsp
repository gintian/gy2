<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@page import="com.hrms.frame.codec.SafeCode"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean"%>
<%@ taglib uri="/tags/struts-html" prefix="html"%>
<%@ taglib uri="/tags/struts-logic" prefix="logic"%>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms"%>
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
	  <link href="/css/css1.css" rel="stylesheet" type="text/css">
<link href="/css/xtree.css" rel="stylesheet" type="text/css">
<link rel="stylesheet" type="text/css" href="/ajax/skin.css"></link>
  </head>

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
</script>
<SCRIPT LANGUAGE=javascript src="/js/xtree.js"></SCRIPT>
<SCRIPT LANGUAGE=javascript src="/js/validate.js"></SCRIPT>
  
<body style="margin-left:0px;margin-top:0px">
<table width="600" align="left" border="0" cellpadding="0" cellspacing="0" class="mainbackground">
	<tr align="left">
		<td valign="top" align="left">
		<logic:equal value="1" name="courseLessonForm" property="isLearn">
		<div class="toolbar" style="padding-left:5px;">
			<img border="0" width="16" height="16" src="/ext/resources/images/default/grid/refresh.gif" alt="刷新" onclick="reflash();" align="middle" style="margin-top:2px;vertical-align:middle; "/> 
		  
       
		</div>
		</logic:equal>
		</td>
	</tr>
	<tr>
		<td valign="top">
			<div id="treemenu"></div>
<SCRIPT LANGUAGE=javascript>
	Global.showroot=false;
	<logic:equal value="0" name="courseLessonForm" property="isLearn">
	var m_sXMLFile	= "/train/resource/course/get_code_tree.jsp?codesetid=40000&codeitemid=&r5100=<bean:write name='courseLessonForm' property='r5100'/>&classes=<bean:write name='courseLessonForm' property='classes'/>&r5000=<bean:write name='courseLessonForm' property='r5000'/>";	
	</logic:equal>
	<logic:equal value="1" name="courseLessonForm" property="isLearn">
	var m_sXMLFile	= "/train/resource/course/get_code_tree.jsp?codesetid=40001&codeitemid=&r5100=<bean:write name='courseLessonForm' property='r5100'/>&classes=<bean:write name='courseLessonForm' property='classes'/>&r5000=<bean:write name='courseLessonForm' property='r5000'/>";	
	</logic:equal>
	var root=new xtreeItem("root","课件目录","javascript:;","mil_body","课件目录","/images/add_all.gif",m_sXMLFile);

	root.setup(document.getElementById("treemenu"));
	
</SCRIPT>
		</td>
	</tr>
</table>


</body>
</html>
<script type="text/javascript">
	root.openURL();
	root.expand2level();
	function aa() {
	
			root.JsItem.style.height = "600px";;
	
	}
	function reflash() {
		var currnode=Global.selectedItem;
		if(!currnode) {
			currnode = root;
		}
		
		if(currnode.load){
					while(currnode.childNodes.length){
						//alert(currnode.childNodes[0].uid);
						currnode.childNodes[0].remove();
					}
					currnode.load=true;
					currnode.loadChildren();
					currnode.reload(1);
				}
	}
	
</script>

