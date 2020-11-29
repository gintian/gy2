<%@ page contentType="text/html; charset=UTF-8" language="java"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean"%>
<%@ taglib uri="/tags/struts-html" prefix="html"%>
<%@ taglib uri="/tags/struts-logic" prefix="logic"%>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms"%>

<link rel="stylesheet" href="/css/css1.css" type="text/css">
<link rel="stylesheet"
	href="/gz/gz_budget/budget_rule/formula/budget_formula.css"
	type="text/css">
<hrms:themes />
<script language="JavaScript" src="/js/constant.js"></script>
<script language="JavaScript" src="/js/validate.js"></script>
<script language="JavaScript" src="/js/function.js"></script>
<script language="JavaScript"  src="/gz/gz_budget/budget_rule/formula/budget_formula.js"></script>
<%@ page
	import="com.hjsj.hrms.actionform.gz.gz_budget.budget_rule.formula.BudgetFormulaForm"%>
<%
	BudgetFormulaForm budgetformulaForm = (BudgetFormulaForm) session
			.getAttribute("budgetformulaForm");
%>

<html:form action="/gz/gz_budget/budget_rule/formula">
	<bean:define id="tab_id" name='budgetformulaForm' property='tab_id'/>

	<table width="98%" height="80%" border="0" align="center" >
		<tr>
			<td >
				<html:hidden name="budgetformulaForm" property="formula_id" />
				<%
					int i = 0;
				%>
				<fieldset style="width: 100%;">
					<legend>
						<bean:message key="gz.premium.countformula" />
					</legend>
					<table width="99%" border="0" align="center">
						<tr>
						<td valign="middle">
							&nbsp;&nbsp;&nbsp;<bean:message key="gz.budget.formula.budgetplan" />
							<hrms:optioncollection name="budgetformulaForm"
								property="tablist" collection="list" />
							<html:select name="budgetformulaForm" property="tab_id"	onchange="selectformula(this);" style="width:300;vertical-align:middle;">
								<html:option value="0">
									<bean:message key="label.all" />
								</html:option>
								<html:options collection="list" property="dataValue"
									labelProperty="dataName" />
							</html:select>
							<td>
						</tr>

						<tr>
							<td width="60%" height="100%" align="center">
								<fieldset align="left" style="height:410px">
									<legend>
										<bean:message key="gz.formula.list.table" />
									</legend>
									<table width="100%" height="100%" border="0">
										<tr>
											<td align="center" valign="top">
												<div id="scroll_box" style="width: 100%">
													<table width="100%" border="0" class="ListTable">
														<tr class="fixedHeaderTr">
															<td width="5%" class="TableRow_right common_background_color common_border_color" align="center" style="border-top:0px;">
																<input type="checkbox" name="quanxuan" onclick="batch_select(this,'budgetformulaForm.select');">
															</td>
															<td width="37%" class="TableRow" align="center" style="border-top:0px;">
																<bean:message key="gz.budget.formula.plantable" />
															</td>
															<td width="30%" class="TableRow" align="center" style="border-top:0px;">
																<bean:message key="workdiary.message.formula.name" />
															</td>
															<td width="12%" class="TableRow" align="center" style="border-top:0px;">
																<bean:message key="gz.budget.formula.formulacategories" />
															</td>
															<td width="8%" class="TableRow" align="center" style="border-top:0px;">
																<bean:message key="gz.budget.formula.scope" />
															</td>
															<td width="8%" class="TableRow_left common_background_color common_border_color" align="center" style="border-top:0px;">
																<bean:message key="label.order" />
															</td>
														</tr>

														<hrms:extenditerate id="element" name="budgetformulaForm"
															property="budgetformulaForm.list" indexes="indexes"
															pagination="budgetformulaForm.pagination" pageCount="2000"
															scope="session">
															<bean:define id="formula_id" name="element" property="formula_id" />
															<%															   
																if (i % 2 == 0) {
															%>															
															<tr class="trShallow" onclick="checkformula(${formula_id} );">
																<%
																	} else {
																%>
															
															<tr class="trDeep" onclick="checkformula(${formula_id} );">
																<%
																	}
																	i++;
																%>
																<td align="center" class="RecordRow_right common_border_color" nowrap>
																	<hrms:checkmultibox name="budgetformulaForm"
																		property="budgetformulaForm.select" value="true"
																		indexes="indexes" />
																	
																	<input type="hidden" name="ids"
																		value="<bean:write  name="element" property="formula_id" filter="true"/>">
																	<input type="hidden"
																		name="<bean:write  name="element" property="formula_id" filter="true"/>"
																		value="<bean:write  name="element" property="formula_id" filter="true"/>">
																</td>
																<td align="left" class="RecordRow"
																	nowrap>
																	&nbsp;
																	<bean:write name="element" property="tab_name"
																		filter="true" />
																	&nbsp;
																</td>
																<td align="left" class="RecordRow"
																	nowrap>
																	&nbsp;
																	<bean:write name="element" property="formulaname"
																		filter="true" />
																	&nbsp;
																</td>
																<td class="RecordRow"
																	nowrap>
																		<html:select name="element" property="formulatype"
																			onchange="setformulatype(this);" style="width:80">
																			<html:option value="1">
																				<bean:message key="gz.budget.formula.inputitems" />
																			</html:option>
																			<html:option value="2">
																				<bean:message key="gz.budget.formula.calculateditem" />
																			</html:option>
																			<html:option value="3">
																				<bean:message key="gz.budget.formula.importentry" />
																			</html:option>
																		</html:select>

																</td>
																<td align="center" class="RecordRow"
																	nowrap>
																	<logic:equal name="element" property="isColumn"
																		value="1">
																		<a
																			href="javascript:openColumn('<bean:write name="element" property="formula_id" filter="true"/>')" /><img
																				src="/images/edit.gif" border=0> </a>
																	</logic:equal>
																</td>
																<td align="center" class="RecordRow"
																	nowrap style="border-right:0px;">
																	<logic:equal name="element" property="number" value="1">
                													&nbsp;&nbsp;
                													</logic:equal>
																	<logic:notEqual name="element" property="number"
																		value="1">
																		<img src="/images/up01.gif" border=0
																			style="cursor: hand"
																			onclick="javaScript:moveRecord('<bean:write name="element" property="formula_id" filter="true"/>','<bean:write name="element" property="seq" filter="true"/>','up')">
																	</logic:notEqual>

																	<logic:notEqual name="element" property="number"
																		value="${budgetformulaForm.count}">
																		<img src="/images/down01.gif" border=0
																			style="cursor: hand"
																			onclick="javaScript:moveRecord('<bean:write name="element" property="formula_id" filter="true"/>','<bean:write name="element" property="seq" filter="true"/>','down')">
																	</logic:notEqual>
																	<logic:equal name="element" property="number"
																		value="${budgetformulaForm.count}">
                													&nbsp;&nbsp;
               	 													</logic:equal>
																</td>
														</hrms:extenditerate>
													</table>
												</div>
											</td>
										</tr>
										<tr>
											<td height="35px;" align="center" valign="top">
												<input type="button"
													value="<bean:message key='button.new.add'/>"
													onclick="add_formula();" Class="mybutton">
												<input type="button"
													value="<bean:message key="button.new.insert"/>"
													onclick="insert_formula();" Class="mybutton">
												<input type="button"
													value="<bean:message key="button.update"/>"
													onclick="modifyname();" Class="mybutton">
												<input type="button"
													value="<bean:message key='button.delete'/>"
													onclick="del_formula(${budgetformulaForm.tab_id});" Class="mybutton">
												<input type="button"
													value="另存为"
													onclick="othersave();" Class="mybutton">
												<span id="backdef"> 
											     	<input type="button"
													  value="<bean:message key="button.leave"/>"
													  onclick="returnbudgetdef()" Class="mybutton">
												</span>		
											</td>
										</tr>
									</table>
								</fieldset>
							</td>
							<td width="40%" height="100%"  valign="top">
								<fieldset  style="height:479px"> <!-- modify by xiaoyun 2014-8-28 -->
									<legend>
										<bean:message key="kq.wizard.expre" />
									</legend>
									<div class="rightdiv" style="height: 459px;">
									<table border="0" align="left" width ="99%" style="margin-top: -4px;"><!-- modify by xiaoyun 2014-9-13 -->
										    <tr  id="formularowtype"  height="28" > 
											     <td >
													<table width="100%" border="0">
														<tr align="left">
															<td width="15%">
															<bean:message key="gz.budget.formula.formulacategories"/>：	
															</td>
												     		<td  align="center" class="RecordRow" >
		                                                       <input type="radio" name="rowColMode" value="1" onclick="checkrowcolflag(1)" checked> <bean:message key="edit_report.rowFormula"/>
		                                                       &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
		                                                       &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
		                                                       <input type="radio" name="rowColMode" value="2" onclick="checkrowcolflag(2)" >  <bean:message key="edit_report.columnFormula"/>
		       												</td>
														</tr>
													</table>	
												 </td>
											 </tr>		
											 <tr>
												<td>
													<table width="100%" border="0">
														<tr>
															<td id="lblformulacontent"  width="15%" align="left" valign="top">
															<bean:message key="hmuster.label.expressions"/><bean:message key="report.conter"/>：	
															</td>						
															<td  width="85%" align="left">
																<html:textarea name="budgetformulaForm"  
																	property="formuladcrp" style="width:100%;height:150px"
																	styleId="shry"></html:textarea>	
															</td>
														</tr>											
													</table>	
												 </td>
											 </tr>		
											 <tr id="trbtns">
												<td>
													<table width="100%" border="0">												
														<tr>
															<td height="21" colspan="4" align="right">
																<input type="button" id="btn_wizard"	value='<bean:message key="kq.formula.function"/>'
																	onclick="function_wizard('formuladcrp');"
																	Class="mybutton">
																<input type="button" id="btn_calc"
																		value='<bean:message key="gz.formula.calculation.conditions"/>'
																		onclick="openRow();" Class="mybutton">		
																<input type="button" id="btn_tj"
																		value="<bean:message key="makeupanalyse.stat"/>"
																		onclick="setcond();" Class="mybutton">																																						
																<input   type="button" id="btn_save"
																	value="<bean:message key="org.maip.formula.preservation"/>"
																	onclick="saveformulacontent();" Class="mybutton">
															</td>
														</tr>
													</table>	
												 </td>
											 </tr>		
											 <tr id="trreference">
												<td>
													<table width="100%" border="0">	
														<tr>
															<td width="60%" align="center">
																<fieldset align="center" style="width: 100%;height:175px">
																	<legend>
																		<bean:message key="org.maip.reference.projects" />
																	</legend>
																	<table width="100%" border="0" height="100">
																		<tr>
																			<td>
																				<span id="setview">
																					<table width="100%" border="0">
																						<tr>
																							<td height="13">
																								<span id="setvalue1"> <bean:message
																										key="infor.label.setlist" /> </span>
																								<span id="setvalue2" style="display: none">
																									<bean:message
																										key="gz.budget.budget_examination.budgetTable" />
																								</span>
																							</td>
																							<td height="13">
																								<select name="fieldsetlist"
																									onchange="changeset();"
																									style="width: 170; font-size: 9pt">
																								</select>
																							</td>
																						</tr>
																					</table> </span>
																			</td>
																		</tr>
																		<tr>
																			<td>
																				<span id="rowview">
																					<table width="100%" border="0">
																						<tr>
																							<td>
																								<bean:message key="gz.budget.formula.rowproject" />
																							</td>
																							<td>
																								<select name="fieldrowlist"
																									onchange="changerow();"
																									style="width: 170; font-size: 9pt">
																								</select>
																							</td>
																						</tr>
																					</table> </span>
																			</td>
																		</tr>
																		<tr>
																			<td>
																				<span id="colview">
																					<table width="100%" border="0">
																						<tr>
																							<td height="13">
																								<span id="colvalue1"> <bean:message
																										key="gz.budget.formula.colItem" /> </span>
																								<span id="colvalue2" style="display: none">
																									<bean:message
																										key="gz.budget.formula.colproject" /> </span>
																							</td>
																							<td height="13">
																								<select name="fieldcollist"
																									onchange="changecol();"
																									style="width: 170; font-size: 9pt">
																								</select>
																							</td>
																						</tr>
																					</table> </span>
																			</td>
																		</tr>
																		<tr>
																			<td>
																				<span id="codeview" style="display: none">
																					<table width="100%" border="0">
																						<tr>
																							<td>
																								<bean:message key="gz.budget.formula.codeItem" />
																							</td>
																							<td height="13">
																								<select name="fieldcodelist"
																									onchange="changecode();"
																									style="width: 170; font-size: 9pt">
																								</select>
																							</td>
																						</tr>
																					</table> </span>
																			</td>
																		</tr>
																		<tr>
																			<td>
																				<span id="sptablecell" style="display: none">
																					<table width="100%" border="0">
																						<tr >
																									
																							<td align="left" width="50%" height="30">	
																							&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;	&nbsp;																					
																								<input type="checkbox" name="chktablecell" onclick="checktablecell(this)">
																								单元格
																							</td>
																							<td id ="tdbtn_ok" width="50%" align="left" height="30">		
																								<input   type="button" id="btn_ok"
																									value="<bean:message key="button.affirm"/>"
																									onclick="savetablecell()" Class="mybutton">
																							</td>					
																						
																						</tr>
																					</table> </span>
																			</td>
																		</tr>
																	</table>
																</fieldset>
															</td>
															<td width="40%">
																<fieldset align="center" style="width: 100%;height:175px">
																	<legend>
																		<bean:message key="org.maip.reference.projects" />
																	</legend>
																	<table width="80%" border="0" align="center">
																		<tr id="trReference1">
																			<td>
																				<table width="100%" border="0">
																					<tr >
																						<td>
																							<input type="button" value="0"
																								onclick="symbol('formuladcrp',0);" class="btn2 common_btn_bg">
																						</td>
																						<td>
																							<input type="button" value="1"
																								onclick="symbol('formuladcrp',1);" class="btn2 common_btn_bg">
																						</td>
																						<td>
																							<input type="button" value="2"
																								onclick="symbol('formuladcrp',2);" class="btn2 common_btn_bg">
																						</td>
																						<td>
																							<input type="button" value="3"
																								onclick="symbol('formuladcrp',3);" class="btn2 common_btn_bg">
																						</td>
																						<td>
																							<input type="button" value="4"
																								onclick="symbol('formuladcrp',4);" class="btn2 common_btn_bg">
																						</td>
																						<td>
																							<input type="button" value="("
																								onclick="symbol('formuladcrp','(');"
																								class="btn2 common_btn_bg">
																						</td>
																						<td colspan="2">
																							<input type="button"
																								value="<bean:message key='gz.formula.if'/>"
																								onclick="symbol('formuladcrp','<bean:message key='gz.formula.if'/>');"
																								class="btn3 common_btn_bg">
																						</td>
																					</tr>
																					<tr>
																						<td>
																							<input type="button" value="5"
																								onclick="symbol('formuladcrp',5);" class="btn2 common_btn_bg">
																						</td>
																						<td>
																							<input type="button" value="6"
																								onclick="symbol('formuladcrp',6);" class="btn2 common_btn_bg">
																						</td>
																						<td>
																							<input type="button" value="7"
																								onclick="symbol('formuladcrp',7);" class="btn2 common_btn_bg">
																						</td>
																						<td>
																							<input type="button" value="8"
																								onclick="symbol('formuladcrp',8);" class="btn2 common_btn_bg">
																						</td>
																						<td>
																							<input type="button" value="9"
																								onclick="symbol('formuladcrp',9);" class="btn2 common_btn_bg">
																						</td>
																						<td>
																							<input type="button" value=")"
																								onclick="symbol('formuladcrp',')');"
																								class="btn2 common_btn_bg">
																						</td>
																						<td colspan="2">
																							<input type="button"
																								value="<bean:message key='gz.formula.else'/>"
																								onclick="symbol('formuladcrp','<bean:message key='gz.formula.else'/>');"
																								class="btn3 common_btn_bg">
																						</td>
																					</tr>
																					<tr>
																						<td>
																							<input type="button" value="+"
																								onclick="symbol('formuladcrp','+');"
																								class="btn2 common_btn_bg">
																						</td>
																						<td>
																							<input type="button" value="-"
																								onclick="symbol('formuladcrp','-');"
																								class="btn2 common_btn_bg">
																						</td>
																						<td>
																							<input type="button" value="*"
																								onclick="symbol('formuladcrp','*');"
																								class="btn2 common_btn_bg">
																						</td>
																						<td>
																							<input type="button" value="/"
																								onclick="symbol('formuladcrp','/');"
																								class="btn2 common_btn_bg">
																						</td>
																						<td>
																							<input type="button" value="\"
																								onclick="symbol('formuladcrp','\\');"
																								class="btn2 common_btn_bg">
																						</td>
																						<td>
																							<input type="button" value="%"
																								onclick="symbol('formuladcrp','%');"
																								class="btn2 common_btn_bg">
																						</td>
																						<td>
																							<input type="button"
																								value="<bean:message key='general.mess.and'/>"
																								onclick="symbol('formuladcrp','<bean:message key='general.mess.and'/>');"
																								class="btn1 common_btn_bg">
																						</td>
																						<td>
																							<input type="button"
																								value="<bean:message key='general.mess.or'/>"
																								onclick="symbol('formuladcrp','<bean:message key='general.mess.or'/>');"
																								class="btn1 common_btn_bg">
																						</td>
																					</tr>
																					<tr>
																						<td>
																							<input type="button" value="="
																								onclick="symbol('formuladcrp','=');"
																								class="btn2 common_btn_bg">
																						</td>
																						<td>
																							<input type="button" value="&gt;"
																								onclick="symbol('formuladcrp','&gt;');"
																								class="btn2 common_btn_bg">
																						</td>
																						<td>
																							<input type="button" value="&lt;"
																								onclick="symbol('formuladcrp','&lt;');"
																								class="btn2 common_btn_bg">
																						</td>
																						<td>
																							<input type="button" value="&lt;&gt;"
																								onclick="symbol('formuladcrp','&lt;&gt;');"
																								class="btn1 common_btn_bg">
																						</td>
																						<td>
																							<input type="button" value="&lt;="
																								onclick="symbol('formuladcrp','&lt;=');"
																								class="btn1 common_btn_bg">
																						</td>
																						<td>
																							<input type="button" value="&gt;="
																								onclick="symbol('formuladcrp','&gt;=');"
																								class="btn1 common_btn_bg">
																						</td>
																						<td>
																							<input type="button" value="~"
																								onclick="symbol('formuladcrp','~');"
																								class="btn2 common_btn_bg">
																						</td>
																						<td>
																							<input type="button"
																								value="<bean:message key='kq.wizard.not'/>"
																								onclick="symbol('formuladcrp','<bean:message key='kq.wizard.not'/>');"
																								class="btn1 common_btn_bg">
																						</td>
																					</tr>
																				</table>
																			</td>
																		</tr>
																		
																		
																		
																		<tr id="trReference2" style="display:none">
																			<td>
																				<table width="100%" border="0" align="center">
																					<tr >
																						<td>
																							<input type="button" value="0"
																								onclick="symbol('formuladcrp',0);" class="btn2 common_btn_bg">
																						</td>
																						<td>
																							<input type="button" value="1"
																								onclick="symbol('formuladcrp',1);" class="btn2 common_btn_bg">
																						</td>
																						<td>
																							<input type="button" value="2"
																								onclick="symbol('formuladcrp',2);" class="btn2 common_btn_bg">
																						</td>
																						<td>
																							<input type="button" value="3"
																								onclick="symbol('formuladcrp',3);" class="btn2 common_btn_bg">
																						</td>
																						<td>
																							<input type="button" value="4"
																								onclick="symbol('formuladcrp',4);" class="btn2 common_btn_bg">
																						</td>
																						<td>
																							<input type="button" value="("
																								onclick="symbol('formuladcrp','(');"
																								class="btn2 common_btn_bg">
																						</td>						
																						<td>
																							<input type="button" value="."
																								onclick="symbol('formuladcrp','.');"
																								class="btn2 common_btn_bg">
																						</td>						
																					</tr>
																					<tr>
																						<td>
																							<input type="button" value="5"
																								onclick="symbol('formuladcrp',5);" class="btn2 common_btn_bg">
																						</td>
																						<td>
																							<input type="button" value="6"
																								onclick="symbol('formuladcrp',6);" class="btn2 common_btn_bg">
																						</td>
																						<td>
																							<input type="button" value="7"
																								onclick="symbol('formuladcrp',7);" class="btn2 common_btn_bg">
																						</td>
																						<td>
																							<input type="button" value="8"
																								onclick="symbol('formuladcrp',8);" class="btn2 common_btn_bg">
																						</td>
																						<td>
																							<input type="button" value="9"
																								onclick="symbol('formuladcrp',9);" class="btn2 common_btn_bg">
																						</td>
																						<td>
																							<input type="button" value=")"
																								onclick="symbol('formuladcrp',')');"
																								class="btn2 common_btn_bg">
																						</td>
																						<td>
																							<input type="button" value=":"
																								onclick="symbol('formuladcrp',':');"
																								class="btn2 common_btn_bg">
																						</td>
																					</tr>
																					<tr>
																						<td>
																							<input type="button" value="+"
																								onclick="symbol('formuladcrp','+');"
																								class="btn2 common_btn_bg">
																						</td>
																						<td>
																							<input type="button" value="-"
																								onclick="symbol('formuladcrp','-');"
																								class="btn2 common_btn_bg">
																						</td>
																						<td>
																							<input type="button" value="*"
																								onclick="symbol('formuladcrp','*');"
																								class="btn2 common_btn_bg">
																						</td>
																						<td>
																							<input type="button" value="/"
																								onclick="symbol('formuladcrp','/');"
																								class="btn2 common_btn_bg">
																						</td>
																						<td>
																							<input type="button" value="\"
																								onclick="symbol('formuladcrp','\\');"
																								class="btn2 common_btn_bg">
																						</td>
																						<td>
																							<input type="button" value="%"
																								onclick="symbol('formuladcrp','%');"
																								class="btn2 common_btn_bg">
																						</td>			
																					</tr>
	
																				</table>
																			</td>
																		</tr>
																		
																	</table>
																</fieldset>
															</td>
														</tr>
													</table>												
												</td>
										</tr>
									</table> 
								</div>
								</fieldset>	
							</td>
						</tr>
					</table>
				</fieldset>
			</td>
		</tr>
	</table>
<script language="javascript">
	var formula_id = '${budgetformulaForm.formula_id}';
	defCheck(formula_id);
	var btnreturnvisible = '${budgetformulaForm.btnreturnvisible}';
	
	if (btnreturnvisible=="true")
	 toggles1("backdef")
	else
	  hides("backdef")
	


</script>

</html:form>