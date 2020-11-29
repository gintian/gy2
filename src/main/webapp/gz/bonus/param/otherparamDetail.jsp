<%@ page contentType="text/html; charset=UTF-8" language="java"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean"%>
<%@ taglib uri="/tags/struts-html" prefix="html"%>
<%@ taglib uri="/tags/struts-logic" prefix="logic"%>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms"%>
<%@ page import="com.hjsj.hrms.actionform.gz.bonus.BonusParamForm"%>
<%
		BonusParamForm bonusParamForm=(BonusParamForm)request.getAttribute("bonusParamForm");	
		String codeLen = (String)bonusParamForm.getCodeLen();
%>
<link rel="stylesheet" href="/css/css1.css" type="text/css">
<hrms:themes />
<script language="JavaScript" src="param.js"></script>
<body onbeforeunload="qxFunc();">
<html:form action="/gz/bonus/param/otherparam">
	<fieldset align="center" style="width:390px;">
		<table border="0" cellspacing="0" align="center" cellpadding="2">			   
			<tr>
				<td align="right" width="35%" nowrap valign="left">
					<bean:message key='conlumn.codeitemid.caption' />
				</td>
				<td align="left" width="65%" nowrap valign="left">
					<html:text name="bonusParamForm" styleClass="inputtext" styleId="codeitemid" onkeyup="checkNuNS(this)" property="codeitemVo.string(codeitemid)" maxlength="<%=codeLen %>"/>
				</td>  
			</tr> 		
			<tr>
				<td align="right" width="35%" nowrap valign="left">
					<bean:message key='conlumn.codeitemid.caption' /><bean:message key='kq.item.name' />
				</td>
				<td align="left" width="65%" nowrap valign="left">
					<html:text name="bonusParamForm" styleClass="inputtext" styleId="name" property="codeitemVo.string(codeitemdesc)"/>
				</td>  
			</tr> 
		</table>
	</fieldset>
	<table border="0" cellspacing="0" align="center" cellpadding="2">		
			<tr>
				<td align="center" height="35">						
						<script>
							if(document.getElementById('codeitemid').value=='')
							{
								document.write('<input type=\"button\" value=\"保存\" onclick=\"beforSave(0);\" Class=\"mybutton\">&nbsp');
								document.write('<input type=\"button\" value=\"保存&继续\" onclick=\"beforSave(2);\" Class=\"mybutton\">');
							}else
							{
								document.write('<input type=\"button\" value=\"保存\" onclick=\"beforSave(1);\" Class=\"mybutton\">');
							}
						</script>
						<input type="button" class="mybutton" value="<bean:message key='button.cancel'/>" onClick="qxFunc();">  				
				</td>
			</tr>
		</table>
			<html:hidden name="bonusParamForm" property="paramStr"/>
	<html:hidden name="bonusParamForm" property="menuid"/>
</html:form>
<script>
	if(document.getElementById('codeitemid').value!='')
		document.getElementById('codeitemid').disabled=true;
</script>
</body>