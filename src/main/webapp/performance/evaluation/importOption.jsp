<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<STYLE type=text/css>
.div2
{
 overflow:auto; 
 width: 100%;height: 160px;
 line-height:15px; 
 border-width:1px; 
 border-style: groove;
 border-width :thin ;
 scrollbar-base-color:#C3D3FD; 
 scrollbar-face-color:none;
 scrollbar-arrow-color:none;
 scrollbar-track-color:#ffffff;
 scrollbar-3dlight-color:#ffffff;
 scrollbar-darkshadow-color:#ffffff;
 scrollbar-highlight-color:none;
 scrollbar-shadow-color:#e5c8e5"
 SCROLLBAR-DARKSHADOW-COLOR: #ffffff;
 BORDER-BOTTOM: #ffccff 1px dotted;
}
</STYLE>
<script type="text/javascript">
function selAll(theObj)
{	
	var bodyids = document.getElementsByName('bodyId');
	for(var i=0;i<bodyids.length;i++)
	{
		if(theObj.checked==true)
			bodyids[i].checked=true;
		else
			bodyids[i].checked=false;	
	}
}
function selectAll()
{
	var objs = document.getElementsByTagName('input');
	for(var i=0;i<objs.length;i++)
	{
		if(objs[i].type=='checkbox')
		{
			if(objs[i].checked==false)
				objs[i].checked=true;			
		}			
	}
}
function defaultSet()
{
	var objs = document.getElementsByTagName('input');
	for(var i=0;i<objs.length;i++)
	{
		if(objs[i].type=='checkbox')
		{
			if(objs[i].checked==true)
				objs[i].checked=false;
		}			
	}
	document.getElementById('isScore').checked=true;
}
function setOk()
{
	var returnVal='';
	if(document.getElementById('isScore').checked==true)
		returnVal+='Score`';
	if(document.getElementById('isGrpAvg').checked==true)
		returnVal+='Avg`';	
	if(document.getElementById('isXiShu').checked==true)
		returnVal+='XiShu`';
	if(document.getElementById('isorder').checked==true)
		returnVal+='Order`';		
	if(document.getElementById('isGrade').checked==true)
		returnVal+='Grade`';
	var bodyids = document.getElementsByName('bodyId');
	for(var i=0;i<bodyids.length;i++)
	{
		if(bodyids[i].checked==true)
			returnVal+='Body'+bodyids[i].value+'`';	
	}	
	var thevo=new Object();
	thevo.menus=returnVal;
	window.returnValue=thevo;
	window.close();
}
</script>
<html:form action="/performance/evaluation/calculate">
	<table width="90%" border="0" cellspacing="1"  align="center" cellpadding="2" class="ListTable">
		<tr height='30'>
			<td>
				<bean:message key="jx.import.plandata" />
			</td>
		</tr>			
		<tr>
			<td class="RecordRow">
				<table width="100%">
					<tr>
						<td  nowrap>
							<html:checkbox  styleId="isScore" name="calcRuleForm" property="isScore" value="1"/><bean:message key="jx.datacol.result" />
						</td>
						<td>
							<html:checkbox  styleId="isScore" name="calcRuleForm" property="isGrpAvg" value="1"/><bean:message key="jx.import.plandata.average" />
						</td>
					</tr>
					<tr>
						<td  nowrap>
							<html:checkbox  styleId="isGrade" name="calcRuleForm" property="isGrade" value="1"/><bean:message key="jx.param.degreepro" />
						</td>
						<td>
							<html:checkbox  styleId="isorder" name="calcRuleForm" property="isorder" value="1"/><bean:message key="jx.import.plandata.order" />
						</td>
					</tr>
						<tr>
						<td  nowrap>
							<html:checkbox  styleId="isXiShu" name="calcRuleForm" property="isXiShu" value="1"/><bean:message key="jx.import.plandata.xishu" />
						</td>
						<td>
							
						</td>
					</tr>
				</table>
			</td>
		</tr>
		<tr>
			<td class="RecordRow">
				<fieldset style="width:100%">
					<legend>
						<input id='a' type="checkbox" onclick='selAll(this)'><bean:message key="jx.import.plandata.grade" />
					</legend>
				 <div class="div2">
				   <table width="100%" border="0" cellpadding="0" cellspacing="0" align="center"  class="ListTableF">
					        <logic:iterate id="element" name="calcRuleForm"	property="bodyTypeList">
									<tr>
										<td align="center" class="RecordRow" nowrap width="12%">
											<input name="bodyId" type="checkbox" id="<bean:write name="element" property="name" filter="true" />"
												value="<bean:write name="element" property="body_id" filter="true" />" 
												<logic:notEqual name="element" property="selected" value="0">checked</logic:notEqual> />
										</td>
										<td align="left" class="RecordRow" nowrap>
											&nbsp;<bean:write name="element" property="name" filter="true" />
										</td>
									</tr>
								</logic:iterate>
				  </table>
				</div>
			</td>
		</tr>
		<tr height='30'>
			<td align="center">
				<input type="button" name="cancel" value="<bean:message key="button.all.select"/>" class="mybutton" onclick="selectAll();">    		
				<input type="button" name="cancel" value="<bean:message key="button.default"/>" class="mybutton" onclick="defaultSet();">    		
				<input type="button" name="cancel" value="<bean:message key="button.ok"/>" class="mybutton" onclick="setOk();">
				<input type="button" name="cancel" value="<bean:message key="button.cancel"/>" class="mybutton" onclick="window.close();">    		
			</td>
		</tr>
	</table>
</html:form>
