<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean"%>
<%@ taglib uri="/tags/struts-html" prefix="html"%>
<%@ taglib uri="/tags/struts-logic" prefix="logic"%>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms"%>
<script language="JavaScript" src="param.js"></script>


<script language="JavaScript">
function onchanges(obj){
//var hashvo=new ParameterSet();
	var fieldsetid = obj.value;
	//hashvo.setValue("fieldsetid",fieldsetid);
	if(fieldsetid==""){
	fieldsetid="-1";
	}
	document.location="/gz/premium/param.do?b_query=link&fieldsetid="+fieldsetid;
	//var request=new Request({method:'post',asynchronous:false,functionId:'3020131001'},hashvo);
	
}
function savePremiumParam(){

var dbs = document.getElementsByName('db');
	var dbstr='';
	if(dbs)
	{		
		if(dbs.length)
		{
				for(var i=0;i<dbs.length;i++)
				{
					if(dbs[i].checked==true)
						dbstr+=','+dbs[i].value;	
				}
		}
		else
		{
			if(dbs.checked==true)
				dbstr+=','+dbs.value;	
		}
	}
	//premiumParamForm.paramStr.value=dbstr.substring(1);
	


var hashvo=new ParameterSet();
hashvo.setValue("setid",document.premiumParamForm.setid.value);
hashvo.setValue("dist_field",document.premiumParamForm.dist_field.value);
hashvo.setValue("rep_field",document.premiumParamForm.rep_field.value);
hashvo.setValue("keep_save_field",document.premiumParamForm.keep_save_field.value);
hashvo.setValue("bonus_sum_field",document.premiumParamForm.bonus_sum_field.value);
hashvo.setValue("dist_sum_field",document.premiumParamForm.dist_sum_field.value);
hashvo.setValue("surplus_field",document.premiumParamForm.surplus_field.value);
hashvo.setValue("cardid",document.premiumParamForm.cardid.value);
hashvo.setValue("salaryid",document.premiumParamForm.salaryid.value);
hashvo.setValue("checkUn_field",document.premiumParamForm.checkUn_field.value);
hashvo.setValue("stat_dbpre",dbstr.substring(1));
var request=new Request({method:'post',asynchronous:false,onSuccess:subSuccess,functionId:'3020131002'},hashvo);
//reportCycleForm.action = "/report/actuarial_report/fill_cycle.do?b_editsave2=editsave";
//	 	reportCycleForm.submit();
}
function subSuccess(outparamters){
alert(SAVESUCCESS);
return;
}
function query(){
window.location.href="";
}

</script>
<html:form action="/gz/premium/param" styleId="premium_param">
<table border=0 width="100%" align="center" class="ListTableF">
	<tr>
		<td>
			<fieldset style="width:100%">
				<legend>
					<bean:message key="sys.label.param" />
				</legend>
				<table  border="0" cellpmoding="0" cellspacing="0"
					cellpadding="0">
						<tr>
							<td align="left">
								&nbsp;<bean:message key="gz.premium.bonusassignset" />&nbsp;&nbsp;
							</td>
							<td>
								 <html:select name="premiumParamForm" property="setid"  style="width:200" onchange="onchanges(this);">
                          			 <html:optionsCollection property="setidList" value="dataValue" label="dataName"/>
                     			 </html:select>
                     			<bean:message key="gz.premium.unitmonthset" />
                     			 
                     			 <hrms:submit styleClass="mybutton" property="br_query">
										<bean:message key="hmuster.label.expressions" />
								</hrms:submit>
								
                     		</td>
                     		
						</tr>	
							<tr>
							<td align="left">&nbsp;
							</td>
							<td>
							</td>
						</tr>
						<tr>
							<td align="left">
								&nbsp;<bean:message key="gz.premium.belowidentifiertarget" />&nbsp;&nbsp;
							</td>
							<td>
								 <html:select name="premiumParamForm" property="dist_field"  style="width:200">
                          			 <html:optionsCollection property="dist_fieldList" value="dataValue" label="dataName"/>
                     			 </html:select>
                     			 	<bean:message key="gz.premium.relativecode45" />
							</td>
						</tr>
							<tr>
							<td align="left">&nbsp;
							</td>
							<td>
							</td>
						</tr>
						<tr>
							<td align="left">
							&nbsp;<bean:message key="gz.premium.upidentifiertarget" />&nbsp;&nbsp;
							</td>
							<td>
								 <html:select name="premiumParamForm" property="rep_field"  style="width:200">
                          			 <html:optionsCollection property="rep_fieldList" value="dataValue" label="dataName"/>
                     			 </html:select>
                     			 <bean:message key="gz.premium.relativecode45" />
							</td>
						</tr>
								<tr>
							<td align="left">&nbsp;
							</td>
							<td>
							</td>
						</tr>
						<tr>
							<td align="left">
								&nbsp;<bean:message key="gz.premium.bonustotaltarget" />&nbsp;&nbsp;
							</td>
							<td>
								 <html:select name="premiumParamForm" property="bonus_sum_field"  style="width:200">
                          			 <html:optionsCollection property="bonus_sum_fieldList" value="dataValue" label="dataName"/>
                     			 </html:select>
							</td>
						</tr>	
							<tr>
							<td align="left">&nbsp;
							</td>
							<td>
							</td>
						</tr>
						<tr>
							<td align="left">
								&nbsp;<bean:message key="gz.premium.belowtotaltarget" />&nbsp;&nbsp;
							</td>
							<td>
								 <html:select name="premiumParamForm" property="dist_sum_field"  style="width:200">
                          			 <html:optionsCollection property="dist_sum_fieldList" value="dataValue" label="dataName"/>
                     			 </html:select>
							</td>
						</tr>	
							<tr>
							<td align="left">&nbsp;
							</td>
							<td>
							</td>
						</tr>
						<tr>
							<td align="left">
								&nbsp;<bean:message key="gz.premium.bonusbalancetarget" />&nbsp;&nbsp;
							</td>
							<td>
								 <html:select name="premiumParamForm" property="surplus_field"  style="width:200">
                          			 <html:optionsCollection property="surplus_fieldList" value="dataValue" label="dataName"/>
                     			 </html:select>
							</td>
						</tr>	
							<tr>
							<td align="left">&nbsp;
							</td>
							<td>
							</td>
						</tr>
						<tr>
							<td align="left">
								&nbsp;<bean:message key="gz.premium.saveidentifiertarget" />&nbsp;&nbsp;
							</td>
							<td>
								 <html:select name="premiumParamForm" property="keep_save_field"  style="width:200">
                          			 <html:optionsCollection property="keep_save_fieldList" value="dataValue" label="dataName"/>
                     			 </html:select>
                     			 <bean:message key="gz.premium.relativecode45" />
							</td>
						</tr>
							<tr>
							<td align="left">&nbsp;
							</td>
							<td>
							</td>
						</tr>
						<tr>
							<td align="left">
								&nbsp;<bean:message key="premium.checkun.index" />&nbsp;&nbsp;
							</td>
							<td>
								 <html:select name="premiumParamForm" property="checkUn_field"  style="width:200">
                          			 <html:optionsCollection property="keep_save_fieldList" value="dataValue" label="dataName"/>
                     			 </html:select>
                     			 <bean:message key="gz.premium.relativecode45" />
							</td>
						</tr>		
							<tr>
							<td align="left">&nbsp;
							</td>
							<td>
							</td>
						</tr>
						<tr>
							<td align="left">
								&nbsp;<bean:message key="gz.premium.belowbonusnounce" />&nbsp;&nbsp;
							</td>
							<td>
								 <html:select name="premiumParamForm" property="cardid"  style="width:200">
                          			 <html:optionsCollection property="cardidList" value="dataValue" label="dataName"/>
                     			 </html:select>
							</td>
						</tr>	
						<tr>
							<td align="left">&nbsp;
							</td>
							<td>
							</td>
						</tr>
						<tr>
							<td align="left">
								&nbsp;<bean:message key="gz.premium.personmonthsalary" />&nbsp;&nbsp;
							</td>
							<td>
								 <html:select name="premiumParamForm" property="salaryid"  style="width:200">
                          			 <html:optionsCollection property="salaryidList" value="dataValue" label="dataName"/>
                     			 </html:select>
							</td>
						</tr>	
						<tr>
							<td align="left">&nbsp;
							</td>
							<td>
							</td>
						</tr>
						</table>
						<table  border="0" cellpmoding="0" cellspacing="0"
					cellpadding="0">
						<tr>
							<td valign="top">
								<table  border="0" cellpmoding="0"  cellspacing="0"
					cellpadding="0"><tr>
					<td>
								&nbsp;<bean:message key="gz.premium.countpersonbasearea" />&nbsp;&nbsp;
								</td>
								</tr>
								</table>
							</td>
							<td align="left">
							<table  border="0" cellpmoding="0" cellspacing="0"
					cellpadding="0">
					<%int i=0; %>
									<logic:iterate id="element" name="premiumParamForm"
						property="stat_dbpre">
							<%if(i%2==0){ %>
							<tr>
						<td>
								<input name="db" type="checkbox"
									value="<bean:write name="element" property="pre" filter="true" />"
									<logic:notEqual name="element" property="dbsel"
											value="0">checked</logic:notEqual> />
								<bean:write name="element" property="dbname" filter="true" />
							</td>
							<%}else{ %>
								<td>
								<input name="db" type="checkbox"
									value="<bean:write name="element" property="pre" filter="true" />"
									<logic:notEqual name="element" property="dbsel"
											value="0">checked</logic:notEqual> />
								<bean:write name="element" property="dbname" filter="true" />
							</td>
							<%}i++; %>
						
					</logic:iterate>
					<%if(i%2!=0) {%>
					<td></td>
					</tr>
					<%} %>
								</table>
							</td>
						</tr>	
				</table>
			</fieldset>
			<center>
				<input type="button"  value="<bean:message key='button.save'/>" onclick="savePremiumParam();" align="middle" Class="mybutton">	
			</center>
		</td>
	</tr>	
	<tr>
		<td  nowrap  align="center">
			&nbsp;
		</td>
	</tr>
</table>
</html:form>