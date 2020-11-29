<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean"%>
<%@ taglib uri="/tags/struts-html" prefix="html"%>
<%@ taglib uri="/tags/struts-logic" prefix="logic"%>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms"%>
<html>
	<head>


	</head>

	<style>
.fixedtab 
{
	BORDER-BOTTOM: #94B6E6 1pt solid; 
    BORDER-LEFT: #94B6E6 1pt solid; 
    BORDER-RIGHT: #94B6E6 1pt solid; 
    BORDER-TOP: #94B6E6 1pt solid ; 	
}
</style>
	<script language="JavaScript" src="evaluation.js"></script>
	<script language='javascript'>

 
  
  

</script>

	<body>

		<html:form action="/performance/evaluation/performanceEvaluation">
			<html:hidden name="evaluationForm" property="khObjWhere2" />
			<table border="0" cellspacing="0" valign='top' align="center"
				cellpadding="0" width='100%' height='100%'>
				<tr>
					<td rowspan='2'>
						&nbsp;&nbsp;
					</td>
					<td colspan='2' valign='bottom'>
						&nbsp;&nbsp;
					</td>
				</tr>
				<tr>
					<td valign='top'>
						<html:textarea name="evaluationForm" property="expr"
							onclick="this.pos=document.selection.createRange();" cols="67"
							rows="10" styleId="shry"></html:textarea>
						<br>
						注:内容为空时自动使用等级设置中的绩效系数,根据考核等级确定
						<br>
						<br>


						<table border="0" cellspacing="0" valign='top' align="left"
							cellpadding="0" width='90%'>
							<tr>
								<td width="70%">
									<fieldset align="center" style="width:100%;">
										<legend>
											<bean:message key="gz.formula.operational.symbol" />
										</legend>
										<table>
											<tr>
												<td height="22">
													<Input type='button' value=' 1 ' class="mybutton"
														onclick='symbol(1)' />
													<Input type='button' value=' 2 ' class="mybutton"
														onclick='symbol(2)' />
													<Input type='button' value=' 3 ' class="mybutton"
														onclick='symbol(3)' />
													<Input type='button' value=' 4 ' class="mybutton"
														onclick='symbol(4)' />
													<Input type='button' value=' 5 ' class="mybutton"
														onclick='symbol(5)' />
													<Input type='button' value=' 6 ' class="mybutton"
														onclick='symbol(6)' />
													<Input type='button' value=' 7 ' class="mybutton"
														onclick='symbol(7)' />
													<Input type='button' value=' 8 ' class="mybutton"
														onclick='symbol(8)' />
													<Input type='button' value=' 9 ' class="mybutton"
														onclick='symbol(9)' />
													<Input type='button' value=' 0 ' class="mybutton"
														onclick='symbol(0)' />
												</td>
											</tr>
											<tr>
												<td height="22">
													<Input type='button' value=' . ' class="mybutton"
														onclick="symbol('.')" />
													<Input type='button' value=' + ' class="mybutton"
														onclick="symbol('+')" />
													<Input type='button' value=' - ' class="mybutton"
														onclick="symbol('-')" />
													<Input type='button' value=' * ' class="mybutton"
														onclick="symbol('*')" />
													<Input type='button' value=' / ' class="mybutton"
														onclick="symbol('/')" />
													<Input type='button' value=' ( ' class="mybutton"
														onclick="symbol('(')" />
													<Input type='button' value=' ) ' class="mybutton"
														onclick="symbol(')')" />
													<Input type='button' value=' > ' class="mybutton"
														onclick="symbol('>')" />
													<Input type='button' value=' < ' class="mybutton"
														onclick="symbol('<')" />
													<Input type='button' value=' = ' class="mybutton"
														onclick="symbol('=')" />
												</td>
											</tr>
											<tr>
												<td height="22">
													<Input type='button' value='< >' class="mybutton"
														onclick="symbol('<>')" />
													<Input type='button' value='且' class="mybutton"
														onclick="symbol('且')" />
													<Input type='button' value='或' class="mybutton"
														onclick="symbol('或')" />
													<Input type='button' value='如果' class="mybutton"
														onclick="symbol('如果')" />
													<Input type='button' value='那么' class="mybutton"
														onclick="symbol('那么')" />
													<Input type='button' value='否则' class="mybutton"
														onclick="symbol('否则')" />
													<Input type='button' value='结束' class="mybutton"
														onclick="symbol('结束')" />
													<Input type='button' value='分情况' class="mybutton"
														onclick="symbol('分情况')" />
												</td>
											</tr>
										</table>
									</fieldset>
								</td>
								<td>
									&nbsp;
								</td>
								<td>
									<fieldset align="center" style="width:100%">
										<legend>
											<bean:message key='jx.evalution.usePoints' />
										</legend>
										<table>
											<tr>
												<td height="80" valign="top">
													<select name='codeitem' onchange='setCode()'>
														<option value='[总分]'>
															总分&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
														</option>
														<option value="[所属部门]">
															所属部门
														</option>
														<option value="[对象类别]">
															对象类别
														</option>
													</select>



												</td>
										</table>
									</fieldset>
								</td>
							</tr>
						</table>




					</td>
					<td valign='top'>
						<input type='button' value='确   定'
							onclick="subFormula('${evaluationForm.planid}')" class="mybutton" />
						<Br>
						<Br>
						<input type='button' value='取   消' onclick="window.close()"
							class="mybutton" />
						<Br>
						<Br>
						<Br>
						<br>
						<input type='button' value='公式检查'
							onclick="checkFormula('${evaluationForm.planid}')"
							class="mybutton" />
						<br>
						<Br>
						<input type='button' value='向    导' class="mybutton"
							onclick="function_Wizard2('${evaluationForm.planid}','xishu');" />
						<br>
					</td>
				</tr>
			</table>
		</html:form>

	</body>
</html>
