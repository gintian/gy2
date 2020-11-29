<%@ page contentType="text/html; charset=UTF-8" language="java"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ page import="com.hrms.struts.valueobject.UserView"%>
<%@ page import="com.hrms.struts.constant.WebConstant"%>
<%
    // 在标题栏显示当前用户和日期 2004-5-10 
    String userName = null;
    String css_url="/css/css1.css";
	UserView userView=(UserView)session.getAttribute(WebConstant.userView);
	if(userView != null){
	  css_url=userView.getCssurl();
	  if(css_url==null||css_url.equals(""))
	  	 css_url="/css/css1.css";
	}
	String pageRow = request.getParameter("pageRow");
%>		     
<script LANGUAGE=javascript src="/js/xtree.js"></script> 
<link rel="stylesheet" href="<%=css_url%>" type="text/css">
<link href="/css/xtree.css" rel="stylesheet" type="text/css">
<hrms:themes />
<html>
<head></head>
<body>
<form name="setpages" >
<table width="100%" border="0" cellspacing="1"  align="center" cellpadding="1" >             
   <tr>        
       <td width="80%">
       		<fieldset align="center" style="width:80%;">
			<legend>每页行数</legend> 
			<%if(pageRow.equals("0")){%>
       		<table width="100%" border="0" cellspacing="1"  align="center" cellpadding="1" >
       			<tr>
       				<td ><input type="radio" name="pagerows" value="0" checked  onclick="setPageRow(0)">自动计算</td>
       				<td>&nbsp;</td>
       			</tr>
       			<tr>
       				<td><input type="radio" name="pagerows" value="1"  onclick="setPageRow(1)">用户指定</td>
       				<td><div id="viewpages" style="display:none"  ><input type="text" id="pagevalue" name="pagevalue" size="10" value="<%=pageRow%>"></div></td>
       			</tr>
       		</table>
       		<%}else{%>
       		<table width="100%" border="0" cellspacing="1"  align="center" cellpadding="1" >
       			<tr>
       				<td ><input type="radio" name="pagerows" value="0"  onclick="setPageRow(0)">自动计算</td>
       				<td>&nbsp;</td>
       			</tr>
       			<tr>
       				<td><input type="radio" name="pagerows" value="1" checked  onclick="setPageRow(1)">用户指定</td>
       				<td><div id="viewpages"><input type="text" id="pagevalue" name="pagevalue" onkeypress="event.returnValue=IsDigit();" size="10" value="<%=pageRow%>"></div></td>
       			</tr>
       		</table>
       		<%}%>
       		</fieldset>
       </td>
       <td width="15%" align="center">
       		<table width="100%" border="0" cellspacing="1"  align="center" cellpadding="1" >
       			<tr>
       				<td height="30" valign="center"><input type="button" name="button1" value="确定" onclick="setPageRowOk();" class="mybutton"></td>
       			</tr>
       			<tr>
       				<td><input type="button" name="button2" value="取消" onclick="closeWindow();" class="mybutton"></td>
       			</tr>
       		</table>
       </td>
   </tr>            
</table>
<input type="hidden" id="pagevalues" name="pagevalues" value="1">
</form>
<script LANGUAGE="javascript">
function toggles(targetId){
	if (document.getElementById(targetId)){
		target = document.getElementById(targetId);
		target.style.display = "block";
	}
} 
function hides(targetId){
	if (document.getElementById(targetId)){
		target = document.getElementById(targetId);
		target.style.display = "none";
	}
}
function setPageRow(pageview){
	if(pageview==0){
		hides("viewpages");
		document.getElementById("pagevalues").value=0;
	}else{
		toggles("viewpages");
		document.getElementById("pagevalues").value=1;
	}
}
function setPageRowOk(){
	var pagevalues = document.getElementById("pagevalues").value;
	var pageRowId=parent.Ext.getCmp('pageRowId');
	if(pagevalues==1){
		var values = document.getElementById("pagevalue").value;
		parent.returnValue=values;//给父窗口设置全局变量 
		pageRowId.close();
		
	}else {
		parent.returnValue="0";
		pageRowId.close();
	}
}
function closeWindow(){
var pageRowId=parent.Ext.getCmp('pageRowId');
pageRowId.close();
}

function IsDigit() 
{ 
    return ((event.keyCode >= 47) && (event.keyCode <= 57)); 
}
</script> 
</body>
</html>
