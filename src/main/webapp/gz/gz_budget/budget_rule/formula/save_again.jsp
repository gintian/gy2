<%@ page contentType="text/html; charset=UTF-8" language="java"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean"%>
<%@ taglib uri="/tags/struts-html" prefix="html"%>
<%@ taglib uri="/tags/struts-logic" prefix="logic"%>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms"%>
<%@ page import="com.hrms.struts.valueobject.UserView,com.hrms.struts.constant.WebConstant"%>
<%
	UserView userView=(UserView)session.getAttribute(WebConstant.userView);
	String hcmflag="";
	if(userView != null){
	  hcmflag=userView.getBosflag();
	}
%>
<link rel="stylesheet" href="/css/css1.css" type="text/css">
<hrms:themes />
<script language="JavaScript" src="/js/validate.js"></script>
<script language="JavaScript" src="/js/function.js"></script>
<script language="javascript">
function saveAgain(){
	var hashvo=new ParameterSet();
	var formulaname = "";
	if(document.getElementById("formulaname")!=null)
	{
		formulaname=document.getElementById("formulaname").value;
	}
	var formula_id=document.getElementById("formula_id").value;
	hashvo.setValue("formulaname",formulaname);
	hashvo.setValue("formula_id",formula_id);
	var request=new Request({method:'post',asynchronous:false,
     	onSuccess:showFieldList,functionId:'302001020499'},hashvo);
}
function showFieldList(outparamters){
	var base=outparamters.getValue("base");
	if(base!='no'){
		window.returnValue=base;
		window.close();	
	}else{
		alert("操作失败！");
	}
}
</script>
<html:form action="/gz/gz_budget/budget_rule/formula">
	<center>
	<%if("hl".equals(hcmflag)){ %>
	<table width="495px" border="0" align="center" style="margin-left:-3px;">
	<%}else{ %>
	<table width="495px" border="0" align="center" style="margin-left:-3px;margin-top:-5;">
	<%} %>
			<tr>
				<td align="center">
					<fieldset style="width: 100%; height: 70">
						<legend>
							<bean:message key="hmuster.label.expressions" /><bean:message key="jx.khplan.saveas" />
						</legend>
						<table width="80%" border="0" align="center">
							<hrms:extenditerate id="element" name="budgetformulaForm"
								property="budgetformulaForm.list" indexes="indexes"
								pagination="budgetformulaForm.pagination" pageCount="50"
								scope="session">
								<tr>
									<TD width="30%" align="right">
										<bean:message key="gz.budget.formula.budgetplan" />:
									</TD>
									<td width="70%" align="left">
										<input type="text" name="tab_name"
											value="<bean:write  name="element" property="tab_name" filter="true" />" style="width:250" class="inputtext" readonly>
									</td>
								</tr>
								<tr>
									<TD width="30%" align="right">
										<bean:message key="workdiary.message.formula.name" />:
									</TD>
									<td width="70%" align="left">
										<input type="text" name="formulaname" class="inputtext"
											value="<bean:write  name="element" property="formulaname" filter="true"/>" style="width:250">
																				<input type="hidden" name="formula_id"
											value="<bean:write  name="element" property="formula_id" filter="true"/>">
									</td>
								</tr>
							</hrms:extenditerate>
						</table>
					</fieldset>
						<table  width="80%" border="0" align="center">
								<tr>
									<td align="right">
										<input type="button" onclick="saveAgain();"
											value="<bean:message key='options.save'/>" Class="mybutton">
									</td>
									<td align="left">
										<input type="button"
											value="<bean:message key='button.close'/>"
											onclick="window.close();" Class="mybutton">
									</td>
								</tr>						
						</table>
				</td>
			</tr>
		</table>
	</center>
</html:form>
