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
body{overfolw：auto;}
</style>
<body>
<html:form action="/ht/param/ht_param_db">
<table style="padding-bottom: 5px">
<TR>
	<TD>
		<input type="button"  value="<bean:message key='button.save'/>" onclick="saveDB();" Class="mybutton">
		<input type="button"  value="<bean:message key='button.all.select'/>" onclick="checkAll();" Class="mybutton">
		<input type="button"  value="<bean:message key='button.all.reset'/>" onclick="clearAll();" Class="mybutton">
	</TD>
</TR>
</table>
	<div class="myfixedDiv" style="width: 100%">
	<table border=0 width="100%"  class="ListTableF">
		<tr>
			<td>
				<table width="100%" border="0" cellpmoding="0" cellspacing="0"
						cellpadding="0" align="center">
						<logic:iterate id="element" name="contractParamForm" property="nbase" >
							<tr>							
								<td width="70%">
								<input name="db" type="checkbox" 
											value="<bean:write name="element" property="pre" filter="true" />"
											<logic:notEqual name="element" property="dbsel"
											value="0">checked</logic:notEqual> />
									<bean:write name="element" property="dbname" filter="true" />									
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
</body>
