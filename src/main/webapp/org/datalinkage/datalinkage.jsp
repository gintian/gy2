<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<html>
<head>
<link href="../../css/css1.css" rel="stylesheet" type="text/css">
<SCRIPT LANGUAGE=javascript src="/js/validate.js"></SCRIPT>
</head>
<script language="javaScript">
	function download(){
		test.action="/org/datalinkage/datalinkage.do?b_test=test";
		test.submit();
	}
	
	function toWord(){
		var un ="su";
		var dp = "usr";
		var cadreids = "00000005,00000006";
		cadrermtoword(un,dp,cadreids);
	}
	
	function userPopedom(){
		window.open("/system/options/userpopedom.do?b_query=link",
		'glWin','toolbar=no,location=no,directories=no,status=no,menubar=no,scrollbars=yes,top=0,left=220,resizable=no,width=800,height=600');
	}
	
</script>
<body>
	<FORM name="test" method="post" >
		<INPUT type="button" value="下载测试" onClick="download()" class="mybutton">
		<input type="button" value="Word测试A" onClick="toWord()"  class="mybutton">
		<input type="button" value="用户权限测试" onClick="userPopedom()"  class="mybutton">
	</FORM>
</body>
</html> 