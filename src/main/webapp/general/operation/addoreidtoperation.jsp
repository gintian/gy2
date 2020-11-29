
<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean"%>
<%@ taglib uri="/tags/struts-html" prefix="html"%>
<%@ taglib uri="/tags/struts-logic" prefix="logic"%>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms"%>

<script type="text/javascript">
<!--
	function getCodesetInfo()
	{
       var operationvo=new Object();	
       operationvo.operationid=$F('operationVo.string(operationid)');
       
       operationvo.operationname=$F('operationVo.string(operationname)');
       if(operationvo.operationname.length<1){
       	alert('<bean:message key="operation.message.operationname"/>');
       	return;
       }
       operationvo.operationcode=$F('operationVo.string(operationcode)');
       
       operationvo.statid=$F('operationVo.string(static)');
       //if(codesetvo instanceof Object) 
       //  alert("hello");
	   window.returnValue=operationvo;
	   window.close();		
	}

//-->
</script>
<html:form action="/general/operation/addoreidtoperation">
	<br>
	<br>
	<table width="400" border="0" cellpadding="0" cellspacing="0" align="center">
		<tr height="20">
			<!-- <td width=10 valign="top" class="tableft"></td>
			<td width=130 align=center class="tabcenter">
			<logic:equal value="" name="operationForm" property="operationVo.string(operationname)">
			<bean:message key="operation.addoperation"/>
			</logic:equal>	
			<logic:notEqual value="" name="operationForm" property="operationVo.string(operationname)">
			<bean:message key="operation.updateoperation"/>
			</logic:notEqual>		
				&nbsp;
			</td>
			<td width=10 valign="top" class="tabright"></td>
			<td valign="top" class="tabremain" width="350"></td> -->
			<td  align=center class="TableRow">
			<logic:equal value="" name="operationForm" property="operationVo.string(operationname)">
			<bean:message key="operation.addoperation"/>
			</logic:equal>	
			<logic:notEqual value="" name="operationForm" property="operationVo.string(operationname)">
			<bean:message key="operation.updateoperation"/>
			</logic:notEqual>		
				&nbsp;
			</td>
		</tr>
		<tr>
			<td  class="framestyle9">
			<br/>
				<table border="0" cellpmoding="0" cellspacing="2" class="DetailTable" cellpadding="0">
					<tr class="list3">
						<td align="right" nowrap>
							<bean:message key="operation.operationcode"/>
							:
						</td>
						<td align="left" nowrap>
						
						<html:text name="operationForm" property="operationVo.string(operationcode)" maxlength="20" size="20" styleClass="text"  disabled="true"></html:text>
						</td>
						</tr>
						<tr>
						<td align="right" nowrap>
							<bean:message key="operation.operationname"/>
							:
						</td>
						<td align="left" nowrap>
							<html:text name="operationForm" property="operationVo.string(operationname)" maxlength="20" size="20" styleClass="text"></html:text>
						</td>
					</tr>
					<tr>
						<td align="right" nowrap>
							<bean:message key="operation.static"/>
							:
						</td>
						<td align="left" nowrap>
							<html:select name="operationForm" property="operationVo.string(static)">
							<html:option value="1">
							<bean:message key="label.module.rsyw"/>
							</html:option>
							<html:option value="2">
							<bean:message key="label.module.sama"/>
							</html:option>
							<html:option value="3">
							<bean:message key="operation.static.jingxian"/>
							</html:option>
							<html:option value="8">
							<bean:message key="operation.static.baoxian"/>
							</html:option>
							</html:select>
						</td>
					</tr>
							<html:hidden name="operationForm" property="operationVo.string(operationid)"/>
				</table>
				<br/>
			</td>
		</tr>
		<tr class="list3">
			<td align="center" style="height:35px;">
							<html:button styleClass="mybutton" property="b_save" onclick="getCodesetInfo();">
					<bean:message key="button.ok"/>
				</html:button>
			
			
				<html:button styleClass="mybutton" property="br_return" onclick="window.close();">
					<bean:message key="button.close" />
				</html:button>
			</td>
		</tr>
	</table>
</html:form>
<script type="text/javascript">
<!--
	if(window.dialogArguments)
	{
		Element.readonly('codesetvo.string(codesetid)');
	}
//-->
</script>
