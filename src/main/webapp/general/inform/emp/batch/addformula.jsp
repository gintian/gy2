<%@ page contentType="text/html; charset=UTF-8" language="java"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean"%>
<%@ taglib uri="/tags/struts-html" prefix="html"%>
<%@ taglib uri="/tags/struts-logic" prefix="logic"%>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms"%>

<script language="JavaScript" src="./batch.js"></script>
<style type="text/css"> 
#itemtable {
    border: 1px solid #eee;
    height: 255px;    
    width: 210px;            
    overflow: auto;            
    margin: 1em 1;
}
</style>
<hrms:themes></hrms:themes>
<html:form action="/general/inform/emp/batch/addformula">
<table border="0" align="center" width="390px;" style="margin-top:-28px;margin-left:-6px;">
	<tr>
		<td width="100%">
			<table border="0" align="center">
				<tr><td>&nbsp;</td></tr>
				<tr>
					<td style="padding-top: 5px;">
						<html:select name="indBatchHandForm" property="fieldsetid" onchange="addchange('${indBatchHandForm.infor}');" style="width:388">
			 				<html:optionsCollection property="fieldsetlist"  value="dataValue" label="dataName" />
						</html:select>
					</td>
				</tr>
				<tr>
					<td>
						<select name="itemid_arr"  multiple="multiple" onDblClick="addItemOk();"  style="width:388;height:250;font-size:9pt">
             			</select>
					</td>
				</tr>
			</table>
		</td>

	</tr>
	<tr>
		<td height="20" align="center">
			<input type="button"  value="<bean:message key='button.ok'/>" onclick="addItemOk();"  Class="mybutton">
			<input type="button"  value="<bean:message key='button.close'/>" onclick="top.close();" Class="mybutton">
		</td>
	</tr>
</table>
</html:form>
<script language="JavaScript">
addchange('${indBatchHandForm.infor}');
</script>
