<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean"%>
<%@ taglib uri="/tags/struts-html" prefix="html"%>
<%@ taglib uri="/tags/struts-logic" prefix="logic"%>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms"%>  

<script type="text/javascript">
	
	function getTargetItems(elementName)
	{
		var items = document.getElementsByName(elementName);
		var itemStr='';
		for(var i=0;i<items.length;i++)
		{
			if(items[i].checked==true)
				itemStr+=','+items[i].value;
		}
		return itemStr;
	}
	
	function defineTargetItem()
	{	
		var plan_fields = getTargetItems("plan");
		var summarize_fields = getTargetItems("summarize");
		if(plan_fields!='')
			plan_fields = plan_fields.substring(1);
		if(summarize_fields!='')
			summarize_fields = summarize_fields.substring(1);
		nworkplanForm.plan_fields.value = plan_fields;							
		nworkplanForm.summarize_fields.value = summarize_fields;
		nworkplanForm.action="/performance/nworkplan/setParam.do?b_setValidFields=link&typeflag=&oper=close";
		nworkplanForm.submit();	
	}	
	
</script>
<html:form action="/performance/nworkplan/setParam">	 	
	<html:hidden name="nworkplanForm" property="plan_fields" styleId="plan_fields"/>	
	<html:hidden name="nworkplanForm" property="summarize_fields" styleId="summarize_fields"/>
	
	<table border="0" cellspacing="1" cellpadding="2" align="center" width="100%">
		<tr>
			<td height="10">
				
			</td>
		</tr>
		<tr>
			<td align="center">
				<fieldset style="width:90%" name="filterset">
					<legend>
					<logic:equal value="4" name="nworkplanForm" property="typeflag">
					年工作计划
					</logic:equal>
					<logic:equal value="3" name="nworkplanForm" property="typeflag">
					季工作计划
					</logic:equal>
					<logic:equal value="2" name="nworkplanForm" property="typeflag">
					月工作计划
					</logic:equal>
					<logic:equal value="1" name="nworkplanForm" property="typeflag">
					周工作计划
					</logic:equal>
					<logic:equal value="0" name="nworkplanForm" property="typeflag">
					日工作计划
					</logic:equal>
					</legend>
					<div style='height:190;width:100%; overflow: auto;'>
						<table width="90%" border="0" cellspacing="0" align="center" cellpadding="0" class="ListTable" id='targetTraceTable'>
							<logic:iterate id="element" name="nworkplanForm" property="planFieldsList">
								<tr>
									<td align="center" class="RecordRow" width="15%">
										<input name="plan" type="checkbox" value="<bean:write name="element" property="itemid" filter="true" />"
											<logic:notEqual name="element" property="selected" value="0">checked</logic:notEqual> 
										/>
									</td>
									<td align="left" class="RecordRow" nowrap>
										&nbsp;&nbsp;
										<bean:write name="element" property="itemdesc" filter="true" />
									</td>
								</tr>
							</logic:iterate>							
						</table>
					</div>
					
				</fieldset>
			</td>
		</tr>
		<tr>
			<td height="6">
			</td>
		</tr>
		<tr>
			<td align="center">
				<fieldset style="width:90%" name="filterset">
					<legend>
					<logic:equal value="4" name="nworkplanForm" property="typeflag">
					年工作总结
					</logic:equal>
					<logic:equal value="3" name="nworkplanForm" property="typeflag">
					季工作总结
					</logic:equal>
					<logic:equal value="2" name="nworkplanForm" property="typeflag">
					月工作总结
					</logic:equal>
					<logic:equal value="1" name="nworkplanForm" property="typeflag">
					周工作总结
					</logic:equal>
					<logic:equal value="0" name="nworkplanForm" property="typeflag">
					日工作总结
					</logic:equal>
					</legend>
				
					<div style='height:190;width:100%; overflow: auto;'>
						<table width="90%" border="0" cellspacing="0" align="center" cellpadding="0" class="ListTable" id='targetTraceTable'>
							<logic:iterate id="element" name="nworkplanForm" property="summarizeFieldList">
								<tr>
									<td align="center" class="RecordRow" width="15%">
										<input name="summarize" type="checkbox" value="<bean:write name="element" property="itemid" filter="true" />"
											<logic:notEqual name="element" property="selected" value="0">checked</logic:notEqual> />
									</td>
									<td align="left" class="RecordRow" nowrap>
										&nbsp;&nbsp;
										<bean:write name="element" property="itemdesc" filter="true" />
									</td>
								</tr>
							</logic:iterate>							
						</table>
					</div>
				</fieldset>
			</td>
		</tr>
	</table>
	<table border="0" cellspacing="1" cellpadding="2"  width="100%">
		<tr>
			<td align="center" style="height:35px">   
					<input type='button' class="mybutton" property="b_ok" onclick='defineTargetItem();' 
						value='<bean:message key="button.ok"/>'  />&nbsp;
					<input type='button' class="mybutton" property="b_cancel" onclick='window.close();' 
						value='<bean:message key="button.cancel"/>' />
			</td>
		</tr>
	</table>
</html:form>
<script>
	if("${param.oper}"=='close')
  	{
  		var thevo=new Object();
		thevo.flag="true";
		thevo.plan_fields=document.getElementById("plan_fields").value;
		thevo.summarize_fields=document.getElementById("summarize_fields").value;
		window.returnValue=thevo;
		window.close();
  	}
</script>