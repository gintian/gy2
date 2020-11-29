<%@ page language="java" import="java.util.*" pageEncoding="GB2312"%>
<%@page import="com.hrms.frame.codec.SafeCode"%>
<%@page import="com.hrms.struts.valueobject.UserView"%>
<%@page import="com.hrms.struts.constant.WebConstant"%>
<%@page import="com.hjsj.hrms.businessobject.sys.SysParamBo"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean"%>
<%@ taglib uri="/tags/struts-html" prefix="html"%>
<%@ taglib uri="/tags/struts-logic" prefix="logic"%>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms"%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
  <head>
    
    <title>My JSP 'showscorm.jsp' starting page</title>
    
	<meta http-equiv="pragma" content="no-cache">
	<meta http-equiv="cache-control" content="no-cache">
	<meta http-equiv="expires" content="0">    
	<meta http-equiv="keywords" content="keyword1,keyword2,keyword3">
	<meta http-equiv="description" content="This is my page">
	<!--
	<link rel="stylesheet" type="text/css" href="styles.css">
	-->

  </head>
 <link rel="stylesheet" type="text/css" href="/css/css1.css"></link> 
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
<script type="text/javascript" src="/train/resource/course/scorm/scormapi.js"></script>
<script language="javascript" src="/js/constant.js"></script>
  <script type="text/javascript">
  	var currentNum = <bean:write name='courseLessonForm' property='currentNum'/>;
  	var sco = <bean:write name='courseLessonForm' property='scoId'/>;
  	if(sco != null && sco != '')
  		currentNum = sco;
		
  	var courseInfo = eval(getDecodeStr("<bean:write name='courseLessonForm' property='courseInfo'/>"));
  	var maxNum = <bean:write name='courseLessonForm' property='maxNum'/>;
  	var findAPITries = 0;
  	var scormapi = null;
  	function initBt() {
  		if (currentNum == 1 && currentNum != maxNum ) {
  			document.getElementById("up").style.display = "none";
  			document.getElementById("dow").style.display = "";
  		} else if (currentNum == 1 && currentNum == maxNum ){
  			document.getElementById("up").style.display = "none";
  			document.getElementById("dow").style.display = "none";
  		} else if (currentNum > 1 && currentNum == maxNum ){
  			document.getElementById("up").style.display = "";
  			document.getElementById("dow").style.display = "none";
  		} else if (currentNum > 1 && currentNum != maxNum ){
  			document.getElementById("up").style.display = "";
  			document.getElementById("dow").style.display = "";
  		}
  	}
  	function selectSco(type) {
  		if (type == 1) {// 上一节
  			currentNum--;
  		} else if (type == 2){ // 下一节
  			currentNum++;
  		}
  		initBt();
  		if (!scormapi) {
  			scormapi = getAPI();
  		}
  		scormapi.setScoId(courseInfo[currentNum -1][0]);
  		document.getElementById("childFrame").src = courseInfo[currentNum -1][1];
  		
  	}
  	
  	
function findAPI(win)
{
   while ((win.API == null) && (win.parent != null) && (win.parent != win))
   {
      findAPITries++;
      // Note: 7 is an arbitrary number, but should be more than sufficient
      if (findAPITries > 7) 
      {
         // alert("Error finding API -- too deeply nested.");
         return null;
      }
      
      win = win.parent;
   }
   return win.API;
}

function getAPI()
{
   var theAPI = findAPI(window);
   if ((theAPI == null) && (window.opener != null) && (typeof(window.opener) != "undefined"))
   {
      theAPI = findAPI(window.opener);
   }
   if (theAPI == null)
   {
       alert("Unable to find an API adapter");
   }
   return theAPI;
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
<hrms:themes/>
<%
UserView userView=(UserView)session.getAttribute(WebConstant.userView);
if("hcm".equals(userView.getBosflag())){ 
	String themes=SysParamBo.getSysParamValue("THEMES",userView.getUserName());
%>
  <script language="javascript">
    hcm_tabset_root="/images/hcm/themes/<%=themes %>/content/";
  </script>
  <%} %>
  <body style="padding:0px;margin:0px;overflow-y: hidden; ">
  <logic:equal value="0"  name='courseLessonForm' property='exist'>
<bean:define id="src" name='courseLessonForm' property='src'></bean:define>
	<table border="0" cellpadding="0" cellspacing="0" align="center">
		<tr>
			<td height="35">
				<button id="up" name="but" value="" onclick="selectSco(1)"  class="mybutton">上一节</button>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<button id="dow" name="but" value="" onclick="selectSco(2)"  class="mybutton">下一节</button>
			</td>
		</tr>
	</table>
	<iframe style="margin-left: 20px;" frameborder="0" name="childFrame" id="childFrame" height="100%" width="100%" src="" scrolling="auto"></iframe>
</logic:equal>

<logic:equal value="1"  name='courseLessonForm' property='exist'>
	<table  border="0" cellpadding="0" cellspacing="0" align="center" style="margin-top:20px">
		<tr>
			<td  class="TableRow" align="left" >
				<bean:message key='sys.import.alertMessage' />
			</td>
		</tr>
		<tr>
			<td  width="300px" class="TableRow"  align="left" style="border-top:0px ;background-color: white;line-height:120px;">
				<bean:message key='train.ilearning.scormInexistence' />
			</td>
		</tr>
	</table>
</logic:equal>

<logic:equal value="2"  name='courseLessonForm' property='exist'>
	<table border="0" align="center" >
		<tr>
			<td  class="TableRow" align="left" >
				<bean:message key='sys.import.alertMessage' />
			</td>
		</tr>
		<tr>
			<td  width="300px" class="TableRow"  align="left" style="border-top:0px ;background-color: white;line-height:120px;">
		   		<bean:message key='train.ilearning.noScorm' />
			</td>
		</tr>
	</table>
</logic:equal>
</body>
</html>
<script type="text/javascript">
<!--

function initdocu() {
	initBt();
	if (!scormapi) {
		scormapi = getAPI();
	}
	scormapi.setScoId(courseInfo[currentNum -1][0]);
	scormapi.setR5100("<bean:write name='courseLessonForm' property='r5100'/>");
	scormapi.setR5000("<bean:write name='courseLessonForm' property='r5000'/>");
  	document.getElementById("childFrame").src = courseInfo[currentNum -1][1];
}
initdocu();
//-->
</script>
