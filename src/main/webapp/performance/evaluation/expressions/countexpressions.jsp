<%@ page contentType="text/html; charset=UTF-8" language="java"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean"%>
<%@ taglib uri="/tags/struts-html" prefix="html"%>
<%@ taglib uri="/tags/struts-logic" prefix="logic"%>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms"%>

<hrms:themes />
<script language="JavaScript" src="/js/validate.js"></script>
<script language="JavaScript" src="/js/function.js"></script>
<script language="JavaScript" src="/performance/evaluation/evaluation.js"></script>
<style type="text/css"> 
.btn1 {
 BORDER-RIGHT: #7b9ebd 1px solid;
 PADDING-RIGHT: 0px;
 BORDER-TOP: #7b9ebd 1px solid; 
 PADDING-LEFT: 0px; FONT-SIZE: 12px; 
 BORDER-LEFT: #7b9ebd 1px solid; 
 CURSOR: hand; 
 COLOR: black; 
 PADDING-TOP: 1px; 
 PADDING-BOTTOM: 0px;
 BORDER-BOTTOM: #7b9ebd 1px solid;
 border:#0042A0 1px solid;
 background-image:url(/images/button.jpg);	
}
.btn2 {
 BORDER-RIGHT: #7b9ebd 1px solid;
 PADDING-RIGHT: 3px; 
 BORDER-TOP: #7b9ebd 1px solid; 
 PADDING-LEFT: 3px; FONT-SIZE: 12px; 
 BORDER-LEFT: #7b9ebd 1px solid; 
 CURSOR: hand; 
 COLOR: black; 
 PADDING-TOP: 1px; 
 PADDING-BOTTOM: 0px;
 BORDER-BOTTOM: #7b9ebd 1px solid;
 border:#0042A0 1px solid;
 background-image:url(/images/button.jpg);	
}
.btn3 {
BORDER-RIGHT: #7b9ebd 1px solid;
 PADDING-RIGHT: 2px;
 BORDER-TOP: #7b9ebd 1px solid; 
 PADDING-LEFT: 2px; FONT-SIZE: 12px; 
 BORDER-LEFT: #7b9ebd 1px solid; 
 CURSOR: hand; 
 COLOR: black; 
 PADDING-TOP: 1px; 
 PADDING-BOTTOM: 0px;
 BORDER-BOTTOM: #7b9ebd 1px solid;
 border:#0042A0 1px solid;
 background-image:url(/images/button.jpg);	
}
#scroll_box {
    border: 1px solid #eee;
    height: 280px;    
    width: 270px;            
    overflow: auto;            
    margin: 1em 1;
}
</style>

<script language="javascript">
function addrelate(name,obj)
{
	var no = new Option();
	for(i=0;i<obj.options.length;i++)
	{
		if(obj.options[i].selected)
		{
	    	no.value=obj.options[i].value;
	    	no.text=obj.options[i].text;
		}
	}
	document.getElementById(name).focus();
	var element = document.selection;
	if (element!=null) {
		var rge = element.createRange();
		if (rge!=null)	
			rge.text=no.value;
	}
}
function saveExpr()
{
	checkFormula('save');
}
function checkFormula(temp)
{
	var hashvo=new ParameterSet();
	hashvo.setValue("temp",temp);
	hashvo.setValue("planid",${evaluationForm.planid});
	hashvo.setValue("formula",getEncodeStr($F("formula")));
	hashvo.setValue("khObjWhere2",getEncodeStr(document.evaluationForm.khObjWhere2.value));
	var request=new Request({method:'post',onSuccess:showresult,functionId:'9024003102'},hashvo);
}
function showresult(outparamters)
{
	var mess = outparamters.getValue("mess");
	var temp = outparamters.getValue("temp");
	var fsql = outparamters.getValue("fsql");
	if(mess=='ok'){
		if(temp=="check"){
			alert(FORMULA_OK);
		}
		else{
			var isReCalcu='no';
			if(confirm(IS_RECALCU_FORMULA))
				isReCalcu='ok';		

			evaluationForm.action="/performance/evaluation/expressions.do?b_save=link&fsql="+getEncodeStr(getDecodeStr(fsql))+"&isReCalcu="+isReCalcu;
			evaluationForm.submit();
			window.returnValue=isReCalcu;
			window.close();
			window.dialogArguments.window.location = window.dialogArguments.window.location
		}
	}
	else{
		alert(getDecodeStr(mess));
		return;
	}
}
function importexpre1()
{
	var strurl="/performance/evaluation/set_import.do?b_search=link`busitype="+${evaluationForm.busitype}+"`planid="+${evaluationForm.planid}+"`flag=expr";
	var iframe_url="/general/query/common/iframe_query.jsp?src="+$URL.encode(strurl);
	var ss=window.showModalDialog(iframe_url,window,"dialogWidth=630px;dialogHeight=350px;resizable=yes;scroll=yes;status=no;");  
	//var return_vo= window.open(iframe_url, 'newwindow', 
    //    "height=350, width=600, top=250,left=200, toolbar=no, menubar=no, scrollbars=yes, resizable=yes,location=no, status=yes");
}
</script>
<html:form action="/performance/evaluation/expressions">
	<html:hidden name="evaluationForm" property="khObjWhere2"/>
	<table width="90%" height="300" border="0" align="center">
		<tr>
			<td>
				<table width="100%" height="300" border="0" align="center">
					<tr>
						<td width="70%" align="center">
							<table border="0" align="center">
								<tr>
									<td>
										<table width="100%" border="0">
											<tr>
												<td colspan="2" align="center">
													<html:textarea name="evaluationForm" property="formula" styleId="formula"
														cols="53" rows="10" styleId="shry"></html:textarea>
												</td>
											</tr>
											<tr>
												
												<td width="60%">
													<fieldset align="center" style="width:100%;">
														<legend>
															<bean:message key="gz.formula.operational.symbol" />
														</legend>
														<table width="100%" border="0">
															<tr>
																<td height="100">
																	<table width="100%" border="0">
																		<tr>
																			<td>
																				<input type="button" value="0"
																					onclick="symbol2(0);" class="btn2">
																			
																				<input type="button" value="1"
																					onclick="symbol2(1);" class="btn2">
																			
																				<input type="button" value="2"
																					onclick="symbol2(2);" class="btn2">
																		
																				<input type="button" value="3"
																					onclick="symbol2(3);" class="btn2">
																		
																				<input type="button" value="4"
																					onclick="symbol2(4);" class="btn2">
																		
																				<input type="button" value="5"
																					onclick="symbol2(5);" class="btn2">
																		
																				<input type="button" value="6"
																					onclick="symbol2(6);" class="btn2">
																			
																				<input type="button" value="7"
																					onclick="symbol2(7);" class="btn2">
																		
																				<input type="button" value="8"
																					onclick="symbol2(8);" class="btn2">
																		
																				<input type="button" value="9"
																					onclick="symbol2(9);" class="btn2">
																			
																			</td>
																		</tr>
																		<tr>
																			<td>
																				<input type="button" value="."
																					onclick="symbol2('.');" class="btn2">
																			
																				<input type="button" value="("
																					onclick="symbol2('(');" class="btn2">
																			
																				<input type="button" value=")"
																					onclick="symbol2(')');" class="btn2">
																			
																				<input type="button" value="+"
																					onclick="symbol2('+');" class="btn2">
																			
																				<input type="button" value="-"
																					onclick="symbol2('-');" class="btn2">
																		
																				<input type="button" value="*"
																					onclick="symbol2('*');" class="btn2">
																			
																				<input type="button" value="/"
																					onclick="symbol2('/');" class="btn2">
																		
																				<input type="button" value='>'
																					onclick="symbol2('>');" class="btn2">
																			
																				<input type="button" value='<'
																					onclick="symbol2('<');" class="btn2">
																		
																				<input type="button" value="="
																					onclick="symbol2('=');" class="btn2">
																			</td>
																		</tr>
																		<tr>
																			<td>
																				<input type="button" value="< >"
																					onclick="symbol2('<>');" class="btn2">
																		
																				<input type="button" value="且"
																					onclick="symbol2('且');" class="btn2">
																			
																				<input type="button" value="或"
																					onclick="symbol2('或');" class="btn2">
																			
																				<input type="button" value="如果"
																					onclick="symbol2('如果');" class="btn2">
																		
																				<input type="button" value="那么"
																					onclick="symbol2('那么');" class="btn2">
																					</td>
																		</tr>
																			<tr>
																			<td>
																				<input type="button" value="否则"
																					onclick="symbol2('否则');" class="btn2">
																		
																				<input type="button" value="结束"
																					onclick="symbol2('结束');" class="btn2">
																		
																				<input type="button" value="分情况"
																					onclick="symbol2('分情况');" class="btn2">
																			</td>
																		</tr>
																	</table>
																</td>
															</tr>
														</table>
												</td>
												<td width="40%" align="center" >
													<fieldset align="center" style="width:100%">
														<legend>
															<bean:message key='jx.evalution.usePoints' />
														</legend>
														<table width="100%" border="0" height="100">
															<tr height="10">
																<td valign="top">
																	<table width="100%" border="0">
																		<tr>
																			<td>
																				<hrms:optioncollection name="evaluationForm"
																					property="exprrelatelist" collection="list" />
																				<html:select name="evaluationForm" property="expression"
																					size="1" onchange="addrelate('formula',this);">
																					<html:option value="[本次得分]">本次得分</html:option>
																					<html:option value="[所属部门]">所属部门</html:option>
																					<html:option value="[对象类别]">对象类别</html:option>
																					<html:options collection="list"
																						property="dataValue" labelProperty="dataName" />
																				</html:select>
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

						</td>
						<td width="30%" >
							<table border="0" align="center">
								<tr height="40">
									<td align="center">
										<input type="button" name="save"
											value="&nbsp;&nbsp;<bean:message key="button.ok"/>&nbsp;&nbsp;"
											class="mybutton" onclick="saveExpr()">
										&nbsp;&nbsp;&nbsp;
									</td>
								</tr>
								<tr height="40">
									<td align="center">
										<input type="button" name="cancel"
											value="&nbsp;&nbsp;<bean:message key="button.cancel"/>&nbsp;&nbsp;"
											class="mybutton" onclick="window.close()">
										&nbsp;&nbsp;&nbsp;
									</td>
								</tr>
								<tr height="40">
									<td align="center">
										<input type="button" name="check"
											value="<bean:message key="performance.workdiary.check.formula"/>"
											class="mybutton" onclick="checkFormula('check')">
										&nbsp;&nbsp;&nbsp;
									</td>
								</tr>
								<tr height="40">
									<td align="center">
										<input type="button" name="relate"
											value="<bean:message key="jx.evaluation.associateplan"/>"
											class="mybutton" onclick="importexpre1()">
										&nbsp;&nbsp;&nbsp;
									</td>
								</tr>
								<tr height="40">
									<td align="center">
										<input type='button' value='向    导' class="mybutton" onclick="function_Wizard2('${evaluationForm.planid}','sum');"/>
										&nbsp;&nbsp;&nbsp;
									</td>
								</tr>
								<tr height="40">
									<td align="center">
	
										&nbsp;&nbsp;&nbsp;
									</td>
								</tr>
								
							</table>
						</td>
					</tr>
				</table>
		
			</td>
		</tr>
	</table>
</html:form>
