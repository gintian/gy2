<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean"%>
<%@ taglib uri="/tags/struts-html" prefix="html"%>
<%@ taglib uri="/tags/struts-logic" prefix="logic"%>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms"%>
<html:form action="/performance/totalrank/totalrank">
<table width="90%" border="0" cellspacing="0" cellpadding="0" align="center" cellpadding="0">
	<tr><td>&nbsp;</td></tr>
	<tr><td>
	<fieldset align="center" style="width:100%;">
	<legend>统计时间范围</legend> 
	<table width="100%" border="0">
		<tr>
			<td>
				选择统计时间：
 				<html:select name="configParameterForm" property="timeitemid" style="width:120">
    			<html:optionsCollection property="timeFieldList" value="dataValue" label="dataName" />
 				</html:select> &nbsp;&nbsp;
 				从:&nbsp;<input type="text" name="fromScope"  value="${configParameterForm.fromScope}"  extra="editor" style="width:80px;font-size:10pt;text-align:left" dropDown="dropDownDate">
				&nbsp;到&nbsp;<input type="text" name="toScope" value="${configParameterForm.toScope}" extra="editor" style="width:80px;font-size:10pt;text-align:left" dropDown="dropDownDate"> 
			</td>
		</tr>
	</table>
	</fieldset>
	</td></tr>
	<tr><td align="center" height="60">
		<table width="150" align="center" border="0">
		<tr><td>
			<input type="button" class="mybutton" value='确定' onclick="returnTimes();">
		</td><td>
			<input type="button" class="mybutton" value='取消' onclick="window.close();">
		</td></tr>
		</table>
	</td></tr>
</table>
<script language="javascript">
function returnTimes(){ 
	var fromScope = document.getElementById("fromScope").value;
	var toScope = document.getElementById("toScope").value;
	var timeitemid = document.getElementById("timeitemid").value;
	if(timeitemid!=null&&timeitemid.length>0&&timeitemid!='no'){
		window.returnValue = timeitemid+"::"+fromScope+"::"+toScope;
		window.close();
	}else{
		alert("请选择统计指标");
		return false;
	}	
}
</script>
</html:form>
