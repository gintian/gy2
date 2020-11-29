<%@ page contentType="text/html; charset=UTF-8" language="java"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean"%>
<%@ taglib uri="/tags/struts-html" prefix="html"%>
<%@ taglib uri="/tags/struts-logic" prefix="logic"%>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms"%>
<script language="JavaScript" src="/js/validate.js"></script>
<script language="javascript" src="monthPremium.js"></script>
<link rel="stylesheet" href="/css/css1.css" type="text/css">
<style>
.myfixedDiv
{ 
	overflow:auto; 
	height:260;
	BORDER-BOTTOM: #94B6E6 1pt solid; 
    BORDER-LEFT: #94B6E6 1pt solid; 
    BORDER-RIGHT: #94B6E6 1pt solid; 
    BORDER-TOP: #94B6E6 1pt solid ; 
}
</style>
<hrms:themes />
<html:form action="/gz/premium/premium_allocate/monthPremiumList">
	<br>
	<table width="80%" border="0" cellspacing="0" align="center"
		cellpadding="0">
		<tr>
			<td>
				<div class="myfixedDiv">
					<table width="100%" border="0" cellspacing="0" align="center"
						cellpadding="0" class="ListTableF">
						<tr class="fixedHeaderTr">
							<td align="center" class="TableRow" nowrap width="15%">
								<input type="checkbox" name="selbox"
									onclick="batch_select(this, 'b0110');">
							</td>
							<td align="center" class="TableRow" nowrap>
								<bean:message key="org.performance.unorum" />
							</td>
						</tr>
						<logic:iterate id="element" name="monthPremiumForm"
							property="orgChilds">
							<tr>
								<td align="center" class="RecordRow" nowrap width="15%">
									<input name="b0110" type="checkbox"
										value="<bean:write name="element" property="itemid" filter="true" />" />
								</td>
								<td align="left" class="RecordRow" nowrap>
									&nbsp;&nbsp;
									<bean:write name="element" property="itemdesc" filter="true" />
								</td>
							</tr>
						</logic:iterate>
					</table>
				</div>
			</td>
		</tr>
	</table>
	<table align="center">
		<tr>
			<td>
				<button name="new" Class="mybutton"
					onclick='generateData2("${monthPremiumForm.year}","${monthPremiumForm.month}","${monthPremiumForm.operOrg}","${monthPremiumForm.orgsubset}");'>
					<bean:message key="button.ok" />
				</button>
				&nbsp;&nbsp;&nbsp;&nbsp;
				<button name="cancel" Class="mybutton" onclick="window.close();">
					<bean:message key="button.cancel" />
				</button>
			</td>
		</tr>
	</table>
</html:form>



