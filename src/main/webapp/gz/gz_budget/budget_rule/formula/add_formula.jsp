<%@ page contentType="text/html; charset=UTF-8" language="java"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean"%>
<%@ taglib uri="/tags/struts-html" prefix="html"%>
<%@ taglib uri="/tags/struts-logic" prefix="logic"%>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms"%>

<link rel="stylesheet" href="/css/css1.css" type="text/css">
<%@ page import="com.hrms.struts.valueobject.UserView,com.hrms.struts.constant.WebConstant"%>
<%
	UserView userView=(UserView)session.getAttribute(WebConstant.userView);
	String hcmflag="";
	if(userView != null){
	  hcmflag=userView.getBosflag();
	}
%>

<hrms:themes />
<script language="JavaScript" src="/js/validate.js"></script>
<script language="JavaScript" src="/js/function.js"></script>
<script language="JavaScript"
	src="/gz/gz_budget/budget_rule/formula/budget_formula.js"></script>
<script language="javascript">
function saveProject(mode,formulaid){
	var hashvo=new ParameterSet();
	var tab_id = document.getElementById("tab_id").value;
	var formulaname = "";
	if(document.getElementById("formulaname")!=null)
	{
		formulaname=document.getElementById("formulaname").value;
	}
	if (formulaname==""){
	   alert('公式名称不能为空!');
	   return;
	}
	if(tab_id==null||tab_id==undefined||tab_id==''){
		alert(SELECT_COND_PROJECT+"!");
		return false;
	}	
	hashvo.setValue("tab_id",tab_id);
	hashvo.setValue("formulaname",formulaname);
	hashvo.setValue("mode",mode);
	hashvo.setValue("formulaid",formulaid);

	var request=new Request({method:'post',asynchronous:false,
     	onSuccess:showFieldList,functionId:'302001020491'},hashvo);
}
function showFieldList(outparamters){
	var hashvo=new ParameterSet();
	var tab_id = document.getElementById("tab_id").value;
	var base=outparamters.getValue("base");
	var formula_id=outparamters.getValue("formula_id");
	if(tab_id=="1"&&base!='no'){
		window.returnValue=base;
		var itemid1=document.getElementsByName("itemid1")[0].value;
		hashvo.setValue("colrange",itemid1);
		hashvo.setValue("formula_id",formula_id);	
		var request=new Request({asynchronous:false,onSuccess:window.close,functionId:'302001020498'},hashvo);		
		
	}else if(tab_id=="2"&&base!='no'){
		window.returnValue=base;
		var itemid=document.getElementsByName("itemid")[0].value;
		hashvo.setValue("colrange",itemid);
		hashvo.setValue("formula_id",formula_id);	
		var request=new Request({asynchronous:false,onSuccess:window.close,functionId:'302001020498'},hashvo);
	}else if(base!='no'){
		window.returnValue=base;
		window.close();	
	}else{
		alert("操作失败！");
	}
}

function on(obj){	
	document.budgetformulaForm.formulaname.value = obj.options[obj.selectedIndex].text;	
}

function en(obj){	
	document.budgetformulaForm.formulaname.value = obj.options[obj.selectedIndex].text;	
}
</script>
<hrms:themes />
<html:form action="/gz/gz_budget/budget_rule/formula">
<bean:define id="mode" name="budgetformulaForm" property="addmode" />
	<center>
	<%if("hl".equals(hcmflag)){ %>
	<table width="495px" border="0" align="center" style="margin-left:-3px;">
	<%}else{ %>
	<table width="495px" border="0" align="center" style="margin-left:-3px;margin-top:-5;">
	<%} %>
		
			<tr>
				<td align="center">
					<fieldset style="width: 100%; height: 80">
						<legend>
							<bean:message key="gz.budget.formula.newbudgetformula" />
						</legend>
						<table width="80%" border="0" align="center">

							<tr>
								<td width="30%" align="right">
									<bean:message key="gz.budget.formula.budgetplan" />&nbsp;
								</td>
								<td width="70%" align="left">
									<hrms:optioncollection name="budgetformulaForm" property="list"
										collection="list" />
									<html:select name="budgetformulaForm" property="tab_id"
										onchange="selectzhibiao(this,l1,l2);" style="width:250">
										<html:optionsCollection property="list" value="dataValue"
											label="dataName" />
									</html:select>
								</td>
							</tr>
							<tr>
								<td width="30%" align="right">
									<bean:message key="workdiary.message.formula.name" />&nbsp;
								</td>
								<td width="70%">
									<input type="text" name="formulaname" style="width:250" class="inputtext">
								</td>
							</tr>
						</table>
						<div id="zhibiao" style="display: none">
							<table width="80%" border="0" align="center">
								<tr>

									<TD width="30%" align="right">
										<bean:message key="gz.budget.formula.colItem" />&nbsp;
									</TD>
									<TD width="70%">
										<html:select name="budgetformulaForm" property="itemid"
											onchange="on(this)" style="width:250">
											<html:optionsCollection property="list1" value="dataValue"
												label="dataName" />
										</html:select>
									</TD>
								</tr>
							</table>
						</div>

						<div id="zhibiao1" style="display: none">
							<table width="80%" border="0" align="center">
								<tr>

									<TD width="30%" align="right">
										<bean:message key="gz.budget.formula.colItem" />&nbsp;
									</TD>
									<td width="70%">
										<html:select name="budgetformulaForm" property="itemid1"
											onchange="en(this)" style="width:250">
											<html:optionsCollection property="list2" value="dataValue"
												label="dataName" />
										</html:select>
									</td>
								</tr>
							</table>
						</div>
	<br>
					</fieldset>
				</td>
			</tr>
			<tr>
				<td>
						<table width="80%" border="0" align="center">
							<tr>
								<td align="right">
									<input type="button" onclick="saveProject('${mode}','${budgetformulaForm.addcurformulaid}');"
										value="<bean:message key='options.save'/>" Class="mybutton"> 
								</td>
								<td  align="left">
									<input type="button" value="<bean:message key='button.close'/>"
										onclick="window.close();" Class="mybutton"> &nbsp;
								</td>
							</tr>
						</table>
				</td>
			</tr>
		</table>
	</center>
	<script language="javascript">
var tab_id = '${budgetformulaForm.tab_id}';
var l1 = '${budgetformulaForm.l1}';
var l2 = '${budgetformulaForm.l2}';
Add_selectTab(tab_id,l1,l2);
</script>
</html:form>
