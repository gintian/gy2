<%@ page contentType="text/html; charset=UTF-8" language="java"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean"%>
<%@ taglib uri="/tags/struts-html" prefix="html"%>
<%@ taglib uri="/tags/struts-logic" prefix="logic"%>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms"%>
<%@ page import="com.hjsj.hrms.actionform.train.resource.TrainResourceForm" %>
<script language="javascript" src="/train/resource/trainResc.js"></script>
<%
	TrainResourceForm form = (TrainResourceForm)session.getAttribute("trainResourceForm");
	int len = form.getFields().size();  
 %>
<html:form action="/train/resource/trainRescAdd">
	<input type="hidden" id="type" value="${param.type}">
	<input type="hidden" id="priFldValue" value="">
	<html:hidden name="trainResourceForm" property="primaryField"  styleId="priFld"/>
	<table width="90%" align="center" border="0" cellpadding="0"
		cellspacing="0">
		<tr>
			<td valign="top">
				<table width="100%" border="0" cellpadding="3" cellspacing="0"
					align="center" class="ListTable">
					<tr height="20">
						<td colspan="4" align="left" class="TableRow">	
							<bean:write name="trainResourceForm" property="recName" />
						</td>
					</tr>
					<tr class="trDeep">
					<%int i=0,j=0; %>
						<logic:iterate  id="element" name="trainResourceForm" property="fields" indexId="index">
						<%if(i==2){ %>
							<%if(j%2 == 0){%>
						</tr><tr class="trShallow">
						<%}else{%>
						</tr><tr class="trDeep">
						<%}i=0;j++;} %>
							<logic:notEqual name="element" property="itemtype" value="M">
								<td align="right" class="RecordRow" nowrap>
									<bean:write name="element" property="itemdesc" filter="true" />
								</td>
								<td align="left" class="RecordRow" nowrap>
									<logic:equal name="element" property="codesetid" value="0">
										<logic:notEqual name="element" property="itemtype" value="D">							
											<html:text maxlength="50" size="30" styleClass="textbox"
												name="trainResourceForm" styleId="${element.itemid}" property='<%="fields[" + index + "].value"%>' /> 								
										</logic:notEqual>	
											<logic:equal name="element" property="itemtype" value="D">	
												<input type="text" name='<%="fields[" + index + "].value"%>' maxlength="50" size="29"  id="${element.itemid}" extra="editor"  class="textbox"  style="font-size:10pt;text-align:left"
														dropDown="dropDownDate" value="${element.value}">																		
										   </logic:equal>	
									</logic:equal>		
									
									<logic:notEqual name="element" property="codesetid" value="0">
										 <logic:equal name="element" property="itemid" value="b0110">
										 	<html:hidden name="trainResourceForm" 	property='<%="fields[" + index + "].value"%>' onchange="fieldcode2(this)" />  
											<html:text maxlength="50" size="30" styleClass="textbox" 
													name="trainResourceForm" property='<%="fields[" + index + "].viewvalue"%>' onchange="fieldcode(this,2)"
													   />	 	
										 </logic:equal>									
										 <logic:notEqual name="element" property="itemid" value="b0110">
											<html:hidden name="trainResourceForm" property='<%="fields[" + index + "].value"%>' />  
											<html:text maxlength="50" size="30" styleClass="textbox" 
													name="trainResourceForm" property='<%="fields[" + index + "].viewvalue"%>' onchange="fieldcode(this,2)"
													   />
			 							</logic:notEqual>
									</logic:notEqual>									
											<%i++; %>
											
								</td>
								<%if(index<len-1) {	%>
								<logic:equal name="trainResourceForm" property='<%="fields[" + Integer.toString(index.intValue()+1) + "].itemtype"%>' value="M">
									<%if(i<2){ %>
									<td align="left" class="RecordRow" nowrap></td>
									<td align="left" class="RecordRow" nowrap></td>
									<%i++; }%>
									
								</logic:equal>
								<%} else if(index==len-1){%>
									<%if(i<2){ %>
									<td align="left" class="RecordRow" nowrap></td>
									<td align="left" class="RecordRow" nowrap></td>
									<%i++; }%>		
								<%} %>
							</logic:notEqual>
							<logic:equal name="element" property="itemtype" value="M">
								<td align="right" class="RecordRow" nowrap  valign="top" >
									<bean:write name="element" property="itemdesc" filter="true" />
								</td>
								<td align="left" class="RecordRow" nowrap  colspan="3">
									<html:textarea name="trainResourceForm"
										property='<%="fields[" + index + "].value"%>'
										cols="90" rows="6" styleClass="textboxMul"></html:textarea>
								</td>
								<%i=2; %>
							</logic:equal>
						</logic:iterate>
						</tr>				
		</table>

				<table width='100%' align='center'>
					<tr>
						<td align='left'>
							<input type="button" class="mybutton" value="<bean:message key='button.return'/>" onClick="freshMain();">  							
						</td>
					</tr>
				</table>
</html:form>
<script>
	var priFld = $F('priFld');
	var obj = $(priFld);
	var priFldValue=$('priFldValue');
	obj.readOnly="true";
	obj.className="textColorRead";
	priFldValue.value=obj.value;
</script>