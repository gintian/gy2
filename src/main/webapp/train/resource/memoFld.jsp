<%@ page contentType="text/html; charset=UTF-8" language="java"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean"%>
<%@ taglib uri="/tags/struts-html" prefix="html"%>
<%@ taglib uri="/tags/struts-logic" prefix="logic"%>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms"%>
<script language="javascript" src="/train/resource/trainResc.js"></script>
<script language="javascript">
	if("${param.oper}"=="close")
	{
		 var thevo=new Object();
       	 thevo.flag="true";
       	 window.returnValue=thevo;
		 window.close();
	}
</script>
<style>
.textColorWrite{
	width: 100%;
	height: 100%;
	border: none;
	padding: 5px;
}
</style>
<html:form action="/train/resource/memoFld">
	<input type='hidden' id='type' value="${param.type}">
	<input type='hidden' id='priFld' value="${param.priFld}">
	<input type='hidden' id='memoFldName' value="${param.memoFldName}">
	<input type='hidden' id='classid' value="${trainResourceForm.classid}">
	<input type='hidden' id='dbname' value="${param.dbname}"/>
	
	<bean:define id="itemdesc" name="trainResourceForm" property="itemdesc"/>
	<table border="0" cellspacing="0" width="380px;" align="center" cellpadding="0" class="ListTableF">
		<TR>
		<TD class="TableRow">
		 <%=itemdesc %>
		</TD>
		</TR>
		<tr>

			<td align="left" valign="top" style="padding: 0; height: 260px;" nowrap="nowrap">
			<html:textarea name="trainResourceForm" styleClass="textColorWrite" styleId="memoFld"
				property="memoFld" cols="45" rows="15"></html:textarea>
			</td>
		</tr>
	</table>
	<table border="0" cellspacing="0" align="center" cellpadding="2"
		width="50%">
		<tr>
		<logic:equal value="1" name="trainResourceForm" property="flag">
			<td align="center" style="padding-top: 10px;">
				<input type="button" class="mybutton"
					value="&nbsp;<bean:message key='button.close' />&nbsp;"
					onClick="window.close();">
			</td>
		</logic:equal>
		<logic:notEqual value="1" name="trainResourceForm" property="flag">
			<td align="center" style="padding: 0;padding-top: 10px;">
				<input type="button" class="mybutton" id="okButton"
					value="&nbsp;<bean:message key='button.ok' />&nbsp;"
					onClick="updateMemoFild();" />
				<input type="button" class="mybutton"
					value="&nbsp;<bean:message key='button.cancel' />&nbsp;"
					onClick="window.close();">
			</td>
		</logic:notEqual>
		</tr>
	</table>
</html:form>


