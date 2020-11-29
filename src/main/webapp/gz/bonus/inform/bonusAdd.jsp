<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<script language="javascript" src="/gz/bonus/inform/bonus.js"></script>
<%@ page import="com.hjsj.hrms.actionform.gz.bonus.BonusForm,com.hjsj.hrms.valueobject.common.FieldItemView" %>
<%
	BonusForm form = (BonusForm)session.getAttribute("bonusForm");
	int len = form.getFieldInfoList().size();  
 %>
<html:form action="/gz/bonus/inform">
<table width="96%" align="center" border="0" cellpadding="0"
		cellspacing="0">
		<tr>
			<td valign="top">			
				<table width="100%" border="0" cellpadding="3" cellspacing="0"
					align="center" class="ListTableF">
					<tr height="20">
						<td colspan="4" align="left" valigh="bottom" class="TableRow" >	
							&nbsp;&nbsp;奖金信息&nbsp;
						</td>
					</tr>
					<tr class="trDeep">
					<logic:notEqual name="bonusForm" property="jobnumFld" value="">					
						<td align="right" class="RecordRow" nowrap>
							<bean:message key='kq.emp.gono'/>
						</td>
						<td align="left" class="RecordRow" nowrap >
							<input type='hidden' id='a0100'>
							<input type="text" name="gh" value="" class="textbox" maxlength="50" size="30" id="gh" onkeyup='queryGH(0);showSelectBox(this);'>	
						</td>
						<td align="right" class="RecordRow" nowrap>
							<bean:message key='hire.employActualize.name'/>
						</td>
						<td align="left" class="RecordRow" nowrap>
							<input type="text" name="a0101" id='a0101' maxlength="50" size="30" onkeyup='queryGH(1);showSelectBox(this);'>	
						</td>
					</logic:notEqual>
					<logic:equal name="bonusForm" property="jobnumFld" value="">
						<td align="right" class="RecordRow" nowrap>
							<bean:message key='hire.employActualize.name'/>
						</td>
						<td align="left" class="RecordRow" nowrap colspan="3">
							<input type='hidden' id='a0100'>
							<input type="text" name="gh" value="" class="textbox" maxlength="50" size="30" id="gh" onkeyup='queryGH(0);showSelectBox(this);'>	
							<input type="text" name="a0101" id='a0101'  class="textColorRead" maxlength="50" size="30"  readonly="readonly" style="display:none">	
						</td>
					</logic:equal>
					
					</tr>
					<tr class="trShallow">
						<td align="right" class="RecordRow" nowrap>
							<bean:message key='label.commend.unit'/>
						</td>
						<td align="left" class="RecordRow" nowrap>
							<input type="text" name="b0100" id='b0110' class="textColorRead" readonly="readonly" maxlength="50" size="30">	
						</td>
						<td align="right" class="RecordRow" nowrap>
							<bean:message key='label.commend.um'/>
						</td>
						<td align="left" class="RecordRow" nowrap>
							<input type="text" name="e0122" id="e0122" class="textColorRead" maxlength="50" size="30"  readonly="readonly">	
						</td>
					</tr>
				<tr class="trDeep">
					<%int i=0,j=0; %>
						<logic:iterate  id="element" name="bonusForm" property="fieldInfoList" indexId="index">
						<%						
						if(i==2){ %>
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
										   <logic:equal name="element" property="itemtype" value="N">	
												<logic:equal name="element" property="decimalwidth" value="0">
													<html:text maxlength="50" size="30" styleClass="textbox" onkeypress="event.returnValue=IsDigit2(this);" onblur='isNumber(this);'
																name="bonusForm" styleId="${element.itemid}" property='<%="fieldInfoList[" + index + "].value"%>' /> 								
												</logic:equal>
												<logic:notEqual name="element" property="decimalwidth" value="0">
													<html:text maxlength="50" size="30" styleClass="textbox" onkeypress="event.returnValue=IsDigit(this);" onblur='isNumber(this);'
																name="bonusForm" styleId="${element.itemid}" property='<%="fieldInfoList[" + index + "].value"%>' /> 								
												</logic:notEqual>
												  
											</logic:equal>	
											<logic:notEqual name="element" property="itemtype" value="N">	
												<logic:equal name="element" property="itemid" value="CreateUserName">	
													<html:text maxlength="50" size="30" styleClass="textbox"
														name="bonusForm" styleId="${element.itemid}" property='<%="fieldInfoList[" + index + "].value"%>' 													
															readonly="true"  styleClass="textColorRead" /> 								
												</logic:equal>
												<logic:notEqual name="element" property="itemid" value="CreateUserName">	
													<html:text maxlength="50" size="30" styleClass="textbox"
														name="bonusForm" styleId="${element.itemid}" property='<%="fieldInfoList[" + index + "].value"%>' /> 								
												</logic:notEqual>
											</logic:notEqual>
										</logic:notEqual>	
											<logic:equal name="element" property="itemtype" value="D">	
												<input type="text" name='<%="fieldInfoList[" + index + "].value"%>' maxlength="50" size="29"  id="${element.itemid}" extra="editor"  class="textbox"  style="font-size:10pt;text-align:left"
														dropDown="dropDownDate" value="${element.value}" onchange=" if(!validate(this,'${element.itemdesc}')) {this.focus(); this.value=''; }">	
														 
										   </logic:equal>	
									</logic:equal>		
									
									<logic:notEqual name="element" property="codesetid" value="0">
										 <logic:equal name="element" property="itemdesc" value="处理状态">
										 	<html:hidden name="bonusForm" property='<%="fieldInfoList[" + index + "].value"%>' />  
											<html:text maxlength="50" size="30" styleClass="textbox" 
													name="bonusForm" property='<%="fieldInfoList[" + index + "].viewvalue"%>' onchange="fieldcode(this,2)"
													  readonly="true"  styleClass ="textColorRead"/>
										 </logic:equal>									
										 <logic:notEqual name="element" property="itemdesc" value="处理状态">
											<html:hidden name="bonusForm" property='<%="fieldInfoList[" + index + "].value"%>' />  
											<html:text maxlength="50" size="30" styleClass="textbox" 
													name="bonusForm" property='<%="fieldInfoList[" + index + "].viewvalue"%>' onchange="fieldcode(this,2)"
													   />
			 <img src="/images/code.gif" onclick='javascript:openInputCodeDialog("${element.codesetid}","<%="fieldInfoList[" + index + "].viewvalue"%>");' />&nbsp;

			 							</logic:notEqual>
									</logic:notEqual>									
											<%i++; %>
											
								</td>
								<%if(index<len-1) {	%>
								<logic:equal name="bonusForm" property='<%="fieldInfoList[" + Integer.toString(index.intValue()+1) + "].itemtype"%>' value="M">
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
									<html:textarea name="bonusForm"
										property='<%="fieldInfoList[" + index + "].value"%>'
										cols="90" rows="6" styleClass="textboxMul"></html:textarea>
								</td>
								<%i=2; %>
							</logic:equal>							
						</logic:iterate>
						</tr>				
		</table>

				<table width='100%' align='center'>
					<tr>
						<td></td>
					</tr>
					<tr>
						<td align='left'>
							<input type='button' value='<bean:message key='button.save' />'	class="mybutton" onclick="saveAdd('saveClose');">
							&nbsp;
		
							<input type="button" value="<bean:message key='button.save'/>&<bean:message key='edit_report.continue'/>" onclick="saveAdd('saveContinue');" Class="mybutton">
							&nbsp;
		
							<input type="button" class="mybutton" value="<bean:message key='button.return'/>" onClick="freshMain();">  							
						</td>
					</tr>
				</table>
	 <div id="a0101_pnl" style="border-style:nono">
  		<select name="a0101_box" multiple="multiple" size="10" class="dropdown_frame"  ondblclick='setSelectValue()'>    
   	    </select>
     </div>  
	<html:hidden name="bonusForm" property="a_code"/>
</html:form>
<script language="javascript">
   Element.hide('a0101_pnl');
</script>