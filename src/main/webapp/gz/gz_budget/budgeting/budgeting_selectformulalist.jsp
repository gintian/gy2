<%@ page contentType="text/html; charset=UTF-8" language="java"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean"%>
<%@ taglib uri="/tags/struts-html" prefix="html"%>
<%@ taglib uri="/tags/struts-logic" prefix="logic"%>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms"%>
<%@ page import="java.util.*"%>
<script language="JavaScript" src="/js/validate.js"></script>
<link rel="stylesheet" href="/css/css1.css" type="text/css">
<hrms:themes />
<%
	int i = 0;
%>

<script language='javascript'>
var curselectformulaid=0;
	function tr_bgcolor(formula_id) {
		var tablevos = document.getElementsByTagName("tr");
		for (var i = 0; i < tablevos.length; i++) {
				var cvalue = tablevos[i];
				cvalue.style.backgroundColor = "";
		}
		var c = document.getElementById(formula_id);
		if (c!=null) {
			var tr = c;
			if (tr.style.backgroundColor != "") {
				tr.style.backgroundColor = "";
			} else {
				tr.style.backgroundColor = "#FFF8D2";
			}
			
		}
	
	}
	function selformulavalid(formulaid)
	{
	  curselectformulaid=formulaid;
      tr_bgcolor(formulaid)
	}
	
	function ok()
	{
		if (curselectformulaid==0){		
			alert('请选择计算公式！');
			return;
		}
		
		var waitInfo=eval("wait");			
		waitInfo.style.display="block";
		document.getElementsByName("compute")[0].disabled=true;		
		 var hashvo=new ParameterSet();		
		 hashvo.setValue("flag","calc");
		 hashvo.setValue("formulaid",curselectformulaid);
		 var request=new Request({asynchronous:true,onSuccess:computeIsOk,functionId:'302001020206'},hashvo);			

	}
	
	function computeIsOk(outparamters)
	{
		var waitInfo=eval("wait");			
		waitInfo.style.display="none";
		var flag=outparamters.getValue("info");	
		var strerror=outparamters.getValue("error");	
		var retvo=new Object();	
		retvo.success=1;
		if(flag=="false") {
			retvo.success=2;
			retvo.strerror=strerror;
		}		
		
	    window.returnValue=retvo;
		window.close();
	}

	function selecttab(){
		var tab_id=document.getElementsByName("selectformula_tabid")[0].value;
		budgetingForm.action="/gz/gz_budget/budgeting/budgeting_table.do?b_calc=link";
		budgetingForm.submit();
	}

</script>

<html:form action="/gz/gz_budget/budgeting/budgeting_table">
	<br>
	<div id='wait'
		style='position: absolute; top: 120; left: 60; display: none;'>
		<table border="1" width="67%" cellspacing="0" cellpadding="4"
			class="table_style" height="87" align="center">
			<tr>
				<td class="td_style" height=24>
					<bean:message key="org.autostatic.mainp.calculation.wait" />
				</td>
			</tr>
			<tr>
				<td style="font-size: 12px; line-height: 200%" align=center>
					<marquee class="marquee_style" direction="right" width="300"
						scrollamount="5" scrolldelay="10">
						<table cellspacing="1" cellpadding="0">
							<tr height=8>
								<td bgcolor=#3399FF width=8></td>
								<td></td>
								<td bgcolor=#3399FF width=8></td>
								<td></td>
								<td bgcolor=#3399FF width=8></td>
								<td></td>
								<td bgcolor=#3399FF width=8></td>
								<td></td>
							</tr>
						</table>
					</marquee>
				</td>
			</tr>
		</table>
	</div>

	&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<bean:message key="gz.budget.budgeting.compute.info" />

	<br>
	<br>
	<table align="center" width="90%" height="80%">
		<tr>
			<td align="center">
				<fieldset align="left" style="width: 100%;">
					<legend>
						<bean:message key="label.gz.select.formula" />
					</legend>
					<table width="100%" border="0" cellspacing="0" align="center"
						cellpadding="0">
						<tr height="23">
							<td>
								&nbsp;<bean:message key="gz.budget.formula.budgetplan" />
								<hrms:optioncollection name="budgetingForm"
									property="selectformula_tablist" collection="list" />
								<html:select name="budgetingForm" property="selectformula_tabid"
									onchange="selecttab(this);" style="width:200">
									<html:option value="0">
										<bean:message key="label.all" />
									</html:option>
									<html:options collection="list" property="dataValue"
										labelProperty="dataName" />
								</html:select>
							</td>
						</tr>
						<tr>
							<td width="100%" align="center">
								<table  width="99%" border="0" cellspacing="0" cellpadding="0"
									class="ListTable">
									<tr>
										<td width="100%">
											<div id="scroll_box" style="height: 375px; overflow: auto; align: center;border-top: 1pt solid;" >
												<table width=100% border="0" class="ListTable">
													<thead>
														<tr class="fixedHeaderTr">
															<td align="center" class="TableRow" style="border-top: none;" nowrap width="60%">
																<bean:message key="gz.budget.formula.plantable" />
																&nbsp;
															</td>
															<td align="center" class="TableRow" style="border-top: none;" nowrap width="40%">
																<bean:message key="label.gz.formula" />
																&nbsp;
															</td>
														</tr>
													</thead>
													<hrms:extenditerate id="element" name="budgetingForm"
														property="formulalistform.list" indexes="indexes"
														pagination="formulalistform.pagination" pageCount="8000"
														scope="session">
														<bean:define id="formulaid" name="element"
															property="formulaid" />
														<%
															if (i % 2 == 0) {
														%>
														<tr class="trShallow" id="${formulaid}"
															onclick="selformulavalid(
										        ${formulaid});">
															<%
																} else {
															%>
														
														<tr class="trDeep" id="${formulaid}"
															onclick="selformulavalid(
										        ${formulaid });">
															<%
																}
																			i++;
															%>
															<td align="left" width="60%" class="RecordRow" nowrap>
																<bean:write name="element" property="tabname"
																	filter="true" />
																&nbsp;
															</td>
															<td align="left" width="40%" class="RecordRow" nowrap>
																<bean:write name="element" property="formulaname"
																	filter="true" />
																&nbsp;
															</td>
														</tr>
													</hrms:extenditerate>
												</table>
											</div>
										</td>
									</tr>
								</table>
							</td>
						</tr>

					</table>
				</fieldset>
			</td>
		</tr>
		<tr>
			<td>
				<table align="center">
					<tr>
						<td>
							<button name="compute" Class="mybutton" onclick="ok();">
								<bean:message key="button.computer" />
							</button>
							&nbsp;&nbsp;
							<button name="cancel" Class="mybutton" onclick="window.close();">
								<bean:message key="button.cancel" />
							</button>
						</td>
					</tr>
				</table>
			</td>
		</tr>
	</table>
</html:form>
<script language="javascript">

</script>


