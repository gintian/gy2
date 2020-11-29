<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean"%>
<%@ taglib uri="/tags/struts-html" prefix="html"%>
<%@ taglib uri="/tags/struts-logic" prefix="logic"%>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms"%>
<%@ page import="com.hrms.struts.constant.SystemConfig"%>
<html>
<head>
<SCRIPT LANGUAGE=javascript src="/js/constant.js"></SCRIPT>
<script language='javascript'>
function enter() {
	var opn = document.getElementById("opinion");
	if (trim(opn.value).length == 0) {
		alert(GZ_ACCOUNTING_PLEASEFILL + "<bean:message key="label.performance.opinion"/>!");
		return;
	}
	if (window.showModalDialog){
        parent.window.returnValue = opn.value;
    }else{
        parent.opener.agreeOK_(opn.value);
    }
	closeWin();
}
function closeWin(){
	if(parent.close){
		parent.close();
	}else{
		window.close();
	}
}
</script>
<script language="javascript" src="/js/validate.js"></script>
<hrms:themes />
</head>

<body>
<table>
	<tr><td><bean:message key="label.performance.opinion"/>: </td></tr>
	<tr>
		<td><textarea id='opinion' style="width:440;height:250;"></textarea></td>
	</tr>
	<tr>
		<td colspan='2' valign='top' align='center'>&nbsp;&nbsp;&nbsp;
			<input type='button' class="mybutton" onclick='enter()'
				value='<bean:message key="kq.formula.true"/>' /> 
			<input type='button' class="mybutton" onclick='closeWin()'
				value='<bean:message key="lable.content_channel.cancel"/>' />
		</td>
	</tr>
</table>
</body>
</html>