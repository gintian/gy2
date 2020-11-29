<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean"%>
<%@ taglib uri="/tags/struts-html" prefix="html"%>
<%@ taglib uri="/tags/struts-logic" prefix="logic"%>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms"%>
<%@ page import="com.hjsj.hrms.actionform.hire.demandPlan.HireOrderForm "%>
<script language="javascript" src="/js/dict.js"></script>
<script language="JavaScript" src="/js/validateDate.js"></script>
<script language="JavaScript" src="/general/inform/emp_main.js"></script>
<script language="javascript"
	src="/hire/demandPlan/hireOrder/hireorder.js"></script>
<% 
		HireOrderForm hireOrderForm=(HireOrderForm)session.getAttribute("hireOrderForm");
		String endFlag = hireOrderForm.getEndFlag();
		boolean flag = false;
		if(endFlag.equals("1"))
			flag = true;
%>
<html:form action="/hire/demandPlan/hireOrder">
		<hrms:priv func_id="310153,0A083">
	<input type='button'
		value='&nbsp;<bean:message key='button.save' />&nbsp;'
		class="mybutton" onclick='editSave()'>
		</hrms:priv>
	<input type='button'
		value='&nbsp;<bean:message key='button.close' />&nbsp;'
		class="mybutton" onclick='window.close();'>
	<div class="fixedDiv2" style="margin-top:3px;border-top:none;">
		<table width="100%" cellpadding="0" cellspacing="0"
			align="center" class="ListTableF" style="border:none;">
			<%
			int i = 0, j = 0;
			%>
			<tr class="trShallow1" style="border:none;">
				<logic:iterate id="element" name="hireOrderForm"
					property="fieldslist" indexId="index">
					<td align="right" class="RecordRow" nowrap style="border-left:none;">
						<bean:write name="element" property="itemdesc" filter="true" />
					</td>
					<td align="left" class="RecordRow" nowrap>
					<logic:equal name="element" property="itemtype" value="A">
						<logic:equal name="element" property="codesetid" value="0">
							<logic:equal name="element" property="readonly" value="true">
								<html:text maxlength="${element.itemlength}" size="25"
										styleClass="textbox complex_border_color" name="hireOrderForm"
										styleId="${element.itemid}"
										property='<%="fieldslist[" + index + "].value"%>' readonly="true"/>													
							</logic:equal>
							<logic:equal name="element" property="readonly" value="false">
								<html:text maxlength="${element.itemlength}" size="25"
										styleClass="textbox complex_border_color" name="hireOrderForm"
										styleId="${element.itemid}"
										property='<%="fieldslist[" + index + "].value"%>' readonly="<%=flag %>"/>							
							</logic:equal>
						</logic:equal>							
							<logic:notEqual name="element" property="codesetid" value="">
								<logic:notEqual name="element" property="codesetid" value="0">
									<logic:equal name="element" property="itemid" value="z0404">
										<html:hidden name="hireOrderForm"
											property='<%="fieldslist[" + index + "].value"%>'
											styleId='z0404_value' onchange="changepos('UN',this)" />
										<html:text maxlength="${element.itemlength}" size="25"
											styleClass="textbox complex_border_color" styleId="z0404" name="hireOrderForm"
											property='<%="fieldslist[" + index + "].viewvalue"%>'
											onchange="fieldcode(this,2)" readonly="true" />
									</logic:equal>
									<logic:equal name="element" property="itemid" value="z0405">
										<html:hidden name="hireOrderForm"
											property='<%="fieldslist[" + index + "].value"%>'
											styleId='z0405_value' onchange="changepos('UM',this)" />
										<html:text maxlength="${element.itemlength}" size="25"
											styleClass="textbox complex_border_color" styleId="z0405" name="hireOrderForm"
											property='<%="fieldslist[" + index + "].viewvalue"%>'
											onchange="fieldcode(this,2)" readonly="true" />
									</logic:equal>
									<logic:equal name="element" property="itemid" value="z0403">
										<html:hidden name="hireOrderForm"
											property='<%="fieldslist[" + index + "].value"%>'
											styleId='z0403_value' onchange="changepos('@k',this)" />
										<html:text maxlength="${element.itemlength}" size="25"
											styleClass="textbox complex_border_color" styleId="e01a1" name="hireOrderForm"
											property='<%="fieldslist[" + index + "].viewvalue"%>'
											onchange="fieldcode(this,2)" readonly="true" />
									</logic:equal>
									<logic:notEqual name="element" property="itemid" value="z0403">
										<logic:notEqual name="element" property="itemid" value="z0404">
											<logic:notEqual name="element" property="itemid"
												value="z0405">
												<html:hidden name="hireOrderForm"
													property='<%="fieldslist[" + index + "].value"%>'
													styleId="${element.itemid}_value" />
												<html:text maxlength="${element.itemlength}" size="25"
													styleClass="textbox complex_border_color" name="hireOrderForm"
													property='<%="fieldslist[" + index + "].viewvalue"%>'
													onchange="fieldcode(this,2)" styleId="${element.itemid}"
													readonly="true" />
													<logic:equal name="hireOrderForm" property="endFlag" value="0">
												<img id='img${element.itemid}' src="/images/code.gif"
													onclick='javascript:openInputCodeDialog("${element.codesetid}","<%="fieldslist[" + index + "].viewvalue"%>");'
													align="absmiddle" />&nbsp;</logic:equal>
									</logic:notEqual>
										</logic:notEqual>
									</logic:notEqual>
								</logic:notEqual>
							</logic:notEqual>
						</logic:equal>
						<logic:equal name="element" property="itemtype" value="N">
							<logic:equal name="element" property="decimalwidth" value="0">
								<html:text maxlength="${element.itemlength}" size="25"
									styleClass="textbox complex_border_color"
									onkeypress="event.returnValue=IsDigit2(this);"
									onblur='isNumber(this);' name="hireOrderForm"
									styleId="${element.itemid}"
									property='<%="fieldslist[" + index + "].value"%>' readonly="<%=flag %>"/>
							</logic:equal>
							<logic:notEqual name="element" property="decimalwidth" value="0">
								<html:text maxlength="${element.itemlength}" size="25"
									styleClass="textbox complex_border_color"
									onkeypress="event.returnValue=IsDigit(this);"
									onblur='isNumber(this);' name="hireOrderForm"
									styleId="${element.itemid}"
									property='<%="fieldslist[" + index + "].value"%>' readonly="<%=flag %>"/>
							</logic:notEqual>
						</logic:equal>
						<logic:equal name="element" property="itemtype" value="D">
								<logic:equal name="element" property="itemid" value="z0402">
										<input type="text" name='<%="fieldslist[" + index + "].value"%>'
								maxlength="${element.itemlength}" size="25"
								id="${element.itemid}" extra="editor" class="m_input complex_border_color"
								style="font-size:10pt;text-align:left" dropDown="dropDownDate"
								value="${element.value}" readonly=true
								onchange=" if(!validate(this,'${element.itemdesc}')) { this.value='';}"
								onblur="if(!validate(this,'${element.itemdesc}')) { this.value='';}">
								</logic:equal>
								<logic:notEqual name="element" property="itemid" value="z0402">
									<logic:equal name="hireOrderForm" property="endFlag" value="1">
												<input type="text" name='<%="fieldslist[" + index + "].value"%>'
								maxlength="${element.itemlength}" size="25"
								id="${element.itemid}" extra="editor" class="m_input complex_border_color"
								style="font-size:10pt;text-align:left" dropDown="dropDownDate"
								value="${element.value}" readonly=true
								onchange=" if(!validate(this,'${element.itemdesc}')) { this.value='';}"
								onblur="if(!validate(this,'${element.itemdesc}')) { this.value='';}">
									</logic:equal>	
									<logic:equal name="hireOrderForm" property="endFlag" value="0">
									<input type="text" name='<%="fieldslist[" + index + "].value"%>'
								maxlength="${element.itemlength}" size="25"
								id="${element.itemid}" extra="editor" class="m_input complex_border_color"
								style="font-size:10pt;text-align:left" dropDown="dropDownDate"
								value="${element.value}" 
								onchange=" if(!validate(this,'${element.itemdesc}')) { this.value='';}"
								onblur="if(!validate(this,'${element.itemdesc}')) { this.value='';}">	
									</logic:equal>					
								</logic:notEqual>							
						</logic:equal>
						<logic:equal name="element" property="itemtype" value="M">
							<html:textarea name="hireOrderForm" styleId="${element.itemid}"
								property='<%="fieldslist[" + index + "].value"%>' cols="50"
								rows="8" styleClass="textboxMul" readonly="<%=flag %>"></html:textarea>
						</logic:equal>
					</td>
<%
							    						
						   if (++j % 2==0)
						    {
						    	if (i % 2 == 0)
								{
				%>			
		</tr>  
		<tr class="trDeep1">
				<%
								} else{
				%>
			</tr>  
		<tr class="trShallow1">
			<%
								}
								i++;			
					    }
			%>
			</logic:iterate>
				<%   if (j % 2!=0){	%>				
		<td class="RecordRow" nowrap align="right">
		
		</td>
		<td class="RecordRow" nowrap>
			
		</td>			
		<%} %>
		</tr>
	</table>
	</div>
</html:form>
