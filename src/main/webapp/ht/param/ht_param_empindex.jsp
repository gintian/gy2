<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean"%>
<%@ taglib uri="/tags/struts-html" prefix="html"%>
<%@ taglib uri="/tags/struts-logic" prefix="logic"%>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms"%>
<script language="JavaScript" src="ht_param.js"></script>
<style>
.tableLoca{
	position:relative;
	/*background-image:url(../../images/listtableheaderm.jpg);*/
	top:0;
	left:0.0;
	height:20;
	width:expression(document.body.clientWidth);
	/*background-color:#DEEAF5;
	background-color:#F4F7F7;*/
}
</style>
<html:form action="/ht/param/ht_param_empindex">
	<Table style="padding-bottom: 5px;">	
		<tr><td class="tableLoca">
		<input type="button"  value="<bean:message key='button.save'/>" onclick="saveEmpIndex();" Class="mybutton">
		<input type="button"  value="<bean:message key='button.all.select'/>" onclick="checkAll();" Class="mybutton">
		<input type="button"  value="<bean:message key='button.all.reset'/>" onclick="clearAll();" Class="mybutton">
		</td></tr>
	</TABLE>
	<div class="myfixedDiv common_border_color" style="height:expression(document.body.clientHeight-50);overflow:auto;border: 1px solid;">
	<table border=0 width="100%"  class="ListTableF" style="border: none;">
		<tr>
			<td>
				<table width="100%" border="0" cellpmoding="0" cellspacing="0"
						cellpadding="0" align="center">
						<logic:iterate id="element" name="contractParamForm" property="empIndex" >
							<tr>							
								<td width="100%">
									<input name="emp" type="checkbox" 
											value="<bean:write name="element" property="itemid" filter="true" />"
											<logic:notEqual name="element" property="indexsel"
											value="0">checked</logic:notEqual> />
									<bean:write name="element" property="itemdesc" filter="true" />
								</td>						
							</tr>
						</logic:iterate>
				</table>
			</td>
		</tr>
	</table>
	</div>
	<html:hidden name="contractParamForm" property="paramStr"/>
</html:form>
