<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean"%>
<%@ taglib uri="/tags/struts-html" prefix="html"%>
<%@ taglib uri="/tags/struts-logic" prefix="logic"%>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms"%>
<script language="javascript"><!--
function setKh(flag){
   	document.location= "/performance/totalrank/setfield.do?b_set=link&flag="+flag;
}	 
--></script>
<html:form action="/performance/totalrank/setfield">
<br><br>
<table width="60%" border="0" cellspacing="0" cellpadding="0" align="center" class="ListTableF">
	<tr>
		<td colspan="3" class="TableRow">业务子集设置</td>
	</tr>
	<tr>
		<td class="RecordRow" width="100">&nbsp;考核结果子集</td>
		<td class="RecordRow">&nbsp;${configParameterForm.kh_setdesc}</td>
		<td class="RecordRow" width="40" align="center">
			<input type="button" value="设置" class="mybutton" onclick="setKh('kh_set');">
		</td>
	</tr>
	<tr>
		<td class="RecordRow">&nbsp;工作业绩子集</td>
		<td class="RecordRow">&nbsp;${configParameterForm.kh_set_lookdesc}</td>
		<td class="RecordRow" align="center">
		<input type="button" value="设置" class="mybutton" onclick="setKh('kh_set_look');">
		</td>
	</tr>
</table>
</html:form>
