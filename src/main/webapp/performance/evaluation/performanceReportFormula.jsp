<%@ page contentType="text/html; charset=UTF-8" language="java"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean"%>
<%@ taglib uri="/tags/struts-html" prefix="html"%>
<%@ taglib uri="/tags/struts-logic" prefix="logic"%>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms"%>
<%@ page import="java.util.*,				 
				 com.hrms.struts.taglib.CommonData,
				 com.hrms.hjsj.sys.DataDictionary,
				 com.hrms.hjsj.sys.FieldItem,
				 com.hrms.struts.valueobject.UserView,
				 com.hrms.struts.constant.WebConstant" %>
				 
<hrms:themes />
<script language="JavaScript" src="/js/validate.js"></script>
<script language="JavaScript" src="/js/function.js"></script>
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
.mybutton{
  padding:0 5px 0 5px;
    margin-right:4px;
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
	    	symbol2(obj.options[i].value);
		}
	}
}
function symbol2(cal)
{
	var formula = document.getElementById("formula")
    formula.focus();
    if (document.selection) {
        //ie8 非标准状态下面
        var rge = window.document.selection.createRange();
        if (rge!=null)
            rge.text=cal;
    }else if(formula.setSelectionRange)
    {
        var rangeStart=formula.selectionStart;
        var rangeEnd=formula.selectionEnd;
        var temp1=formula.value.substring(0,rangeStart);
        var temp2=formula.value.substring(rangeEnd);
        formula.value=temp1+cal+temp2;
        formula.setSelectionRange(rangeStart+1,rangeEnd+1);
    }
}
function saveExpr()
{
	var theFormula = document.getElementById("formula").value;
	if(ltrim(rtrim(theFormula))=='')
	{
		alert('请设定表达式！');
		return;
	}
	setCookie("plansReportFormulaVal",theFormula);
	var thevo=new Object();
	thevo.ok=1;
	thevo.formulaVal = theFormula;
	if (window.showModalDialog){
        window.returnValue=thevo;
    }else{
		parent.window.opener.formulaDef_m_ok(thevo);
    }
	parent.window.close();
}
function function_Wizard()
{
	var thecodeurl ="/org/funwd/function_Wizard.do?b_query=link&flag=2&checktemp=jixiaoguanli&callBackFunc=function_Wizard_ok";
//    var return_vo= window.showModalDialog(thecodeurl, "",
//              "dialogWidth:400px; dialogHeight:400px;resizable:no;center:yes;scroll:yes;status:no");
    var config = {
        width:400,
        height:400,
        type:'2'
    }
    modalDialog.showModalDialogs(thecodeurl,'tempwin',config,function_Wizard_ok)
}
function function_Wizard_ok(return_vo) {
    if(return_vo!=null)
    {
        symbol2(return_vo);
    }else{
        return ;
    }
}
function checkFormula()
{
	
	var hashvo=new ParameterSet();
	hashvo.setValue("planid",'${evaluationForm.planid}');
	hashvo.setValue("type",'PerformanceReport_nameFormula');
	hashvo.setValue("formula",getEncodeStr(document.getElementById('formula').value));
	var request=new Request({method:'post',onSuccess:resultCheckFormula,functionId:'9024000026'},hashvo);	
}  
function resultCheckFormula(outparamters)
{
  	var info = outparamters.getValue("errorInfo");
  	info = getDecodeStr(info);
	if(info=="ok"||info.length==0)
		 saveExpr();
	else
		alert(info);
}

// 公式检查
function formulaCheck()
{
	
	var hashvo=new ParameterSet();
	hashvo.setValue("planid",'${evaluationForm.planid}');
	hashvo.setValue("type",'PerformanceReport_nameFormula');
	hashvo.setValue("formula",getEncodeStr(document.getElementById('formula').value));
	var request=new Request({method:'post',onSuccess:resultFormulaCheck,functionId:'9024000026'},hashvo);	
}  
function resultFormulaCheck(outparamters)
{
  	var info = outparamters.getValue("errorInfo");
  	info = getDecodeStr(info);
	if(info=="ok"||info.length==0)
		alert("公式通过检查！");
	else
		alert(info);
}

</script>
<html:form action="/performance/evaluation/performanceEvaluation">

	<table width="100%"  height="250" border="0" align="center">
		<tr>
			<td>
				<table width="100%" height="250" border="0" align="center">
					<tr>
						<td width="85%" align="center">
							<table border="0" align="center">
								<tr>
									<td>
										<table width="100%" border="0">
											<tr>
												<td colspan="2" align="left">
													<bean:message key="kq.wizard.expre" />
													<br>
													<textarea id="formula" style='width:100%' rows="10"></textarea>												
												</td>
											</tr>
											<tr>												
												<td width="65%">
													<fieldset align="center">
														<legend>
															<bean:message key="gz.formula.operational.symbol" />
														</legend>
														<table width="100%" border="0">
															<tr>
																<td height="60">
																	<table width="100%" border="0">
																		<tr>
																			<td>
																				<input type="button" value=" 0 "
																					onclick="symbol2(0);" class="mybutton">
																			
																				<input type="button" value=" 1 "
																					onclick="symbol2(1);" class="mybutton">
																			
																				<input type="button" value=" 2 "
																					onclick="symbol2(2);" class="mybutton">
																		
																				<input type="button" value=" 3 "
																					onclick="symbol2(3);" class="mybutton">
																		
																				<input type="button" value=" 4 "
																					onclick="symbol2(4);" class="mybutton">
																					
																				<input type="button" value=" + " style="width: 28px;"
																					onclick="symbol2('+');" class="mybutton">
																			
																				<input type="button" value=" - "
																					onclick="symbol2('-');" class="mybutton">
																					
																				<input type="button" value=" ( "
																					onclick="symbol2('(');" class="mybutton">
																			</td>
																		</tr>
																		<tr>
																			<td>
																				<input type="button" value=" 5 "
																					onclick="symbol2(5);" class="mybutton">
																		
																				<input type="button" value=" 6 "
																					onclick="symbol2(6);" class="mybutton">
																			
																				<input type="button" value=" 7 "
																					onclick="symbol2(7);" class="mybutton">
																		
																				<input type="button" value=" 8 "
																					onclick="symbol2(8);" class="mybutton">
																		
																				<input type="button" value=" 9 "
																					onclick="symbol2(9);" class="mybutton">
																					
																				<input type="button" value=" * "
																					onclick="symbol2('*');" style="width: 28px;" class="mybutton">
																					
																				<input type="button" value=" / "
																					onclick="symbol2('/');" class="mybutton">																						
																					
																				<input type="button" value=" ) "
																					onclick="symbol2(')');" class="mybutton">
																			</td>
																		</tr>																		
																	</table>
																</td>
															</tr>
														</table>
												</td>
												<td width="35%" align="center" >
													<fieldset align="center">
														<legend>
															<bean:message key='org.maip.reference.projects' />
														</legend>
														<table width="100%" border="0" height="60">
															<tr height="10">
																<td valign="top">
																	<table width="100%" border="0">
																		<tr>
																			<td>
																					<%
																	         			FieldItem fielditem = DataDictionary.getFieldItem("E0122");
																	         		%>
																					<select name="nameindex" style="width:130" size="1" onchange="addrelate('formula',this);">
																						<option value="" selected="selected"></option>
																						<option value="计划名称">计划名称</option>
  																						<option value=<bean:message key="b0110.label"/>><bean:message key="b0110.label"/></option>
  																						<option value=<%=fielditem.getItemdesc()%>><%=fielditem.getItemdesc()%></option>
 						    															<option value=<bean:message key="e01a1.label"/>><bean:message key="e01a1.label"/></option>
  																						<option value="姓名">姓名</option>
  																						<option value="人员编号">人员编号</option>
																					</select>																				
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
						<td width="15%" >
							<table border="0" align="center">
								<tr height="40">
									<td align="center">
										<input type='button' value='向    导' class="mybutton" onclick="function_Wizard();"/>
										&nbsp;&nbsp;&nbsp;
									</td>
								</tr>												
								<tr height="40">
									<td align="center">
										<input type="button" value="公式检查"
											class="mybutton" onclick="formulaCheck();">
										&nbsp;&nbsp;&nbsp;
									</td>
								</tr>
							
								<tr height="40">
									<td align="center">
	
										&nbsp;&nbsp;&nbsp;
									</td>
								</tr>
								<tr height="40">
									<td align="center">
										<input type="button" name="save"
											value="&nbsp;&nbsp;<bean:message key="button.ok"/>&nbsp;&nbsp;"
											class="mybutton" onclick="checkFormula();">
										&nbsp;&nbsp;&nbsp;
									</td>
								</tr>
								<tr height="40">
									<td align="center">
										<input type="button" name="cancel"
											value="&nbsp;&nbsp;<bean:message key="button.cancel"/>&nbsp;&nbsp;"
											class="mybutton" onclick="parent.window.close()">
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
<script>
	var formulaVal =  getCookie('plansReportFormulaVal');
	if(formulaVal!=null)
		document.getElementById('formula').value=formulaVal;
</script>