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
		var planTargets = getTargetItems("planTargets");
		var summTargets = getTargetItems("summTargets");
		if(planTargets!='')
			planTargets = planTargets.substring(1);
		if(summTargets!='')
			summTargets = summTargets.substring(1);
		nworkplanForm.planTarget.value = planTargets;							
		nworkplanForm.summTarget.value = summTargets;					
		nworkplanForm.action='/performance/nworkplan/setParam.do?b_dailyTargetSet=link&oper=close';
		nworkplanForm.submit();	
	}	
	
</script>
<html:form action="/performance/nworkplan/setParam">	 	
	<html:hidden name="nworkplanForm" property="planTarget" styleId="planTarget"/>	
	<html:hidden name="nworkplanForm" property="summTarget" styleId="summTarget"/>
	
	<table border="0" cellspacing="1" cellpadding="2" align="center" width="100%">
		<tr>
			<td height="10">
				
			</td>
		</tr>
		<tr>
			<td align="center">
				<fieldset style="width:90%" name="filterset">
					<legend>
					工作计划指标
					</legend>
					<div style='height:190;width:100%; overflow: auto;'>
						<table width="90%" border="0" cellspacing="0" align="center" cellpadding="0" class="ListTable" id='targetTraceTable'>
							<logic:iterate id="element" name="nworkplanForm" property="plantargetList">
								<tr>
									<td align="center" class="RecordRow" width="15%">
										<input name="planTargets" type="checkbox" value="<bean:write name="element" property="itemid" filter="true" />"
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
					工作总结指标
					</legend>
				
					<div style='height:190;width:100%; overflow: auto;'>
						<table width="90%" border="0" cellspacing="0" align="center" cellpadding="0" class="ListTable" id='targetTraceTable'>
							<logic:iterate id="element" name="nworkplanForm" property="summtargetList">
								<tr>
									<td align="center" class="RecordRow" width="15%">
										<input name="summTargets" type="checkbox" value="<bean:write name="element" property="itemid" filter="true" />"
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
		thevo.planTarget=nworkplanForm.planTarget.value;
		thevo.summTarget=nworkplanForm.summTarget.value;		
		window.returnValue=thevo;
		window.close();
  	}
</script>