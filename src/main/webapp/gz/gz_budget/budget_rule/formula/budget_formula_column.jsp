<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean"%>
<%@ taglib uri="/tags/struts-html" prefix="html"%>
<%@ taglib uri="/tags/struts-logic" prefix="logic"%>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms"%>
<link rel="stylesheet" href="/css/css1.css" type="text/css">
<link rel="stylesheet"
	href="/gz/gz_budget/budget_rule/formula/budget_formula.css"
	type="text/css">
<hrms:themes />
<script language="JavaScript"
	src="/gz/gz_budget/budget_rule/formula/budget_formula.js"></script>
<body>
	<html:form action="/gz/gz_budget/budget_rule/formula">
		<table border=0 width="97%"  align="center">
		<tr>
		<td>
			<fieldset  style="width: 99%" >
				<legend>
					<bean:message key="workdiary.message.formula.name" />
				</legend>
				<table border=0 width="100%"  height="100">
					<tr>
						<td style="width: 70%;">
							<fieldset align="center" style="width: 100%;">
								<legend>
									<bean:message key="gz.budget.formula.colItem" />
								</legend>
								<div class="columndiv" >
								<table width="100%" border="0" cellspacing="0" 
									cellpadding="0" align="center">
									<hrms:extenditerate id="element" name="budgetformulaForm" property="budgetformulaForm.list" indexes="indexes"  pagination="budgetformulaForm.pagination" pageCount="800" scope="session">
									<tr >
										
											<td width="20" align="right">
												<logic:equal name="element" property="flag" value="0">
													<hrms:checkmultibox name="budgetformulaForm" property="budgetformulaForm.select" value="false" indexes="indexes"/>
												</logic:equal>
												<logic:notEqual name="element" property="flag" value="0">
													<hrms:checkmultibox name="budgetformulaForm" property="budgetformulaForm.select" value="true" indexes="indexes"/>
												</logic:notEqual>
												<input type="hidden" name="aaa" value="<bean:write  name="element" property="itemid" filter="true"/>"> 
												<input type="hidden" name="bbb" value="<bean:write  name="element" property="formula_id" filter="true"/>">
											</td>
											<td width="100" align="left">
												<bean:write  name="element" property="itemdesc" filter="true"/>&nbsp;
											</td>
										
									</tr>
									</hrms:extenditerate>
								</table>
								</div>
							</fieldset>
						</td>


						<td style="width: 30%;" align="center">
							<input type="button" value="<bean:message key='button.ok'/>"
								onclick="saveColumn();" Class="mybutton">
							<br><br>
							<input type="button"
								value="<bean:message key='button.all.select'/>"
								onclick="checkAll();" Class="mybutton">
							<br><br>
							<input type="button"
								value="<bean:message key='button.all.reset'/>"
								onclick="clearAll();" Class="mybutton">
							<br><br>
							<input type="button"
								value="<bean:message key='button.cancel'/>"
								onclick="window.close();" Class="mybutton">
						</td>
					</tr>
				</table>
			</fieldset>
		</td>
		</tr>
		</table>
	</html:form>
</body>
