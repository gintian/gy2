<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean"%>
<%@ taglib uri="/tags/struts-html" prefix="html"%>
<%@ taglib uri="/tags/struts-logic" prefix="logic"%>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms"%>
<script language="JavaScript" src="formula.js"></script>

<script language="JavaScript" src="/js/validate.js"></script>
<script language="JavaScript" src="/js/function.js"></script>
<%@ page import="java.util.*,
				 com.hjsj.hrms.actionform.performance.evaluation.EvaluationForm,
				 org.apache.commons.beanutils.LazyDynaBean,
				 com.hrms.struts.taglib.CommonData,
				 com.hrms.struts.valueobject.UserView,
				 com.hrms.struts.constant.WebConstant" %>
<% 
	UserView userView=(UserView)session.getAttribute(WebConstant.userView);
	EvaluationForm myForm=(EvaluationForm)session.getAttribute("evaluationForm");	
	String busiType = myForm.getBusitype(); // 业务分类字段 =0(绩效考核); =1(能力素质)
	ArrayList exprrelatelist = myForm.getExprrelatelist();
	String gradeFormula = myForm.getGradeFormula();
			
%>
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

.fuctionbutton{
    width:70px;
}
#scroll_box {
    border: 1px solid #eee;
    height: 280px;    
    width: 270px;            
    overflow: auto;            
    margin: 1em 1;
}
</style>
<hrms:themes />
<script language="javascript">
	<%		
		if(request.getParameter("isReCalcu")!=null && request.getParameter("isReCalcu").equalsIgnoreCase("ok"))
		{
	%>				
			close("ok");
	<%
		}else if(request.getParameter("isReCalcu")!=null && request.getParameter("isReCalcu").equalsIgnoreCase("no")){			
	%>				
			close("no");
	<%	
		}	
	%>	
	
	function close(value) {
		if(window.showModalDialog){
            parent.window.returnValue=value;
            parent.window.close();
		}else {
			parent.parent.refresh_ok(value);
			var win = parent.parent.Ext.getCmp('computFormula_win');
	   		if(win) {
	    		win.close();
	   		}
		}
	}
	/**根据总分公式、纠偏总分公式下拉框选择情况来判断公式展现属性  **/
	
	function changeScoreFormula(obj){
		if(document.getElementById("scoreFormula01")==null||document.getElementById("scoreFormula02")==null)
			return;
		if(obj.value=="01"){
			document.getElementById("scoreFormula01").style.display="block";
			document.getElementById("scoreFormula02").style.display="none";
		}else if(obj.value="02"){
			document.getElementById("scoreFormula01").style.display="none";
			document.getElementById("scoreFormula02").style.display="block";
		}
	}
	/**根据是否勾选‘对总分进行纠偏’ 控制 总分公式、纠偏总分公式的展现**/
	function deviationTotalScore(){
		if(document.getElementById("deviationScore")==null)
		return;
		if(document.getElementById("deviationScore").checked==true){
			document.getElementById("scoreFormula").style.display="block";
			document.evaluationForm.totalScoreFormulaType.value='01';

		}else{
			document.getElementById("scoreFormula").style.display="none";
			document.getElementById("scoreFormula01").style.display="block";
			document.getElementById("scoreFormula02").style.display="none";
		}
	}

</script>
<html:form action="/performance/evaluation/performanceEvaluation">
	<html:hidden name="evaluationForm" property="khObjWhere2" />
	<html:hidden name="evaluationForm" property="planid" />
	<html:hidden name="evaluationForm" property="busitype" />

	<hrms:tabset name="cardset" width="780" height="400" type="false">
	
	<%	// 区分 绩效管理和能力素质 模块的功能授权					    
		if(busiType==null || busiType.trim().length()<=0 || !busiType.equals("1")){						  	 
	%>
	<hrms:priv func_id="326040501"> 
		<hrms:tab name="menu1" label="jx.evalution.countExpr" visible="true"><!-- 总分计算公式 -->
			<table width="100%" height="300" border="0" align="center">
				<tr>
					<td>
						<table width="100%" height="300" border="0">
							<tr>
								<td width="85%" align="center">
									<table border="0" align="center">
										<tr>
											<td valign='top'>
												<table width="100%" border="0">
													
														<tr>
															<td colspan="2" align="left">
																<div id="scoreFormula" >
																	<html:select name="evaluationForm" property="totalScoreFormulaType" onchange="changeScoreFormula(this);"  >
																		<option value="01">总分公式</option>
																		<option value="02">总分纠偏公式</option>
																	</html:select>
																</div>
															</td>
														</tr>
													
													<tr>
														<td colspan="2" align="center">
														<div id="scoreFormula01" style="text-align:left;"><!-- 总分公式 -->
															<html:textarea name="evaluationForm" property="formula"
																styleId="total_formula" style="width:100%;" cols="100" rows="8">
															</html:textarea>
															</div>
														<div id="scoreFormula02" style="text-align:left;"><!-- 总分纠偏公式 -->
															<html:textarea name="evaluationForm" property="scoreDeviationFormula"
																styleId="total_deviation_formula" style="width:100%;" cols="100" rows="8">
															</html:textarea>
														</div>
														<html:hidden name="evaluationForm" property="scoreDeviationFormula" styleId="total_deviation_formula"/>	
														</td>
													</tr>
													<tr>
														<td colspan="2" align="left">
														<html:checkbox styleId="deviationScore" name="evaluationForm" onclick="deviationTotalScore();"
														property="deviationScore" value="1" />对总分进行纠偏
														</td>
													</tr>
													<tr>
														<td width="50%">
															<fieldset align="center">
																<legend>
																	<bean:message key="gz.formula.operational.symbol" />
																</legend>
																<table width="100%" border="0" cellspacing="0" cellpadding="0">
																	<tr>
																		<td height="100"><!-- 【6570】绩效管理/绩效评估/设置/计算公式，应该和查看总分计算公式的时候保持一致   jingq upd 2015.01.09 -->
																			<table style="margin-left:5px;">
																				<tr>
																					<td height="22">
																						<input type="button" value="0"
																							onclick="symbol2('0');" style="width:8%;" class="smallbutton">

																						<input type="button" value="1"
																							onclick="symbol2('1');" style="width:8%;" class="smallbutton">

																						<input type="button" value="2"
																							onclick="symbol2('2');" style="width:8%;" class="smallbutton">

																						<input type="button" value="3"
																							onclick="symbol2('3');" style="width:8%;" class="smallbutton">

																						<input type="button" value="4"
																							onclick="symbol2('4');" style="width:8%;" class="smallbutton">

																						<input type="button" value="5"
																							onclick="symbol2('5');" style="width:8%;" class="smallbutton">

																						<input type="button" value="6"
																							onclick="symbol2('6');" style="width:8%;" class="smallbutton">

																						<input type="button" value="7"
																							onclick="symbol2('7');" style="width:8%;" class="smallbutton">

																						<input type="button" value="8"
																							onclick="symbol2('8');" style="width:8%;" class="smallbutton">

																						<input type="button" value="9"
																							onclick="symbol2('9');" style="width:8%;" class="smallbutton">

																					</td>
																				</tr>
																				<tr>
																					<td height="22">
																						<input type="button" value="."
																							onclick="symbol2('.');" style="width:8%;" class="smallbutton">

																						<input type="button" value="("
																							onclick="symbol2('(');" style="width:8%;" class="smallbutton">

																						<input type="button" value=")"
																							onclick="symbol2(')');" style="width:8%;" class="smallbutton">

																						<input type="button" value="+"
																							onclick="symbol2('+');" style="width:8%;" class="smallbutton">

																						<input type="button" value="-"
																							onclick="symbol2('-');" style="width:8%;" class="smallbutton">

																						<input type="button" value="*"
																							onclick="symbol2('*');" style="width:8%;" class="smallbutton">

																						<input type="button" value="/"
																							onclick="symbol2('/');" style="width:8%;" class="smallbutton">

																						<input type="button" value='>'
																							onclick="symbol2('>');" style="width:8%;" class="smallbutton">

																						<input type="button" value='<'
																						 		onclick="symbol2('<');" style="width:8%;" class="smallbutton">
																						

																						<input type="button" value="="
																							onclick="symbol2('=');" style="width:8%;" class="smallbutton">
																					</td>
																				</tr>
																				<tr>
																					<td height="22">
																						<input type="button" value='<>'
																							onclick="symbol2('<>');" style="width:9%" class="smallbutton">

																						<input type="button" value="且"
																							onclick="symbol2('且');" style="width:8%;" class="smallbutton">

																						<input type="button" value="或"
																							onclick="symbol2('或');" style="width:8%;" class="smallbutton">

																						<input type="button" value="如果"
																							onclick="symbol2('如果');" style="width:12%;" class="smallbutton">

																						<input type="button" value="那么"
																							onclick="symbol2('那么');" style="width:12%;" class="smallbutton">
																					
																						<input type="button" value="否则"
																							onclick="symbol2('否则');" style="width:12%;" class="smallbutton">

																						<input type="button" value="结束"
																							onclick="symbol2('结束');" style="width:12%;" class="smallbutton">

																						<input type="button" value="分情况"
																							onclick="symbol2('分情况');" style="width:17%;" class="smallbutton">
																					</td>
																				</tr>
																			</table>
																		</td>
																	</tr>
																</table>
														</td>
														<td align="center" width="50%">
															<fieldset align="center">
																<legend>
																	<bean:message key='jx.evalution.usePoints' />
																</legend>
																<table width="100%" border="0" height="100">
																	<tr height="10">
																		<td valign="top">
																			<table width="100%" border="0">
																				<tr>
																					<td>																					
																						<html:select name="evaluationForm"
																							property="expression" size="1"
																							onchange="addrelate('formula',this);" style="width:260px">
																							<html:option value="[本次得分]">本次得分</html:option>
																							<html:option value="[所属部门]">所属部门</html:option>
																							<html:option value="[对象类别]">对象类别</html:option>
																							<html:optionsCollection property="exprrelatelist" value="dataValue" label="dataName" />
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
								<td width="15%">
									<table border="0" align="center">
										<tr height="40">
											<td align="left">
												<input type="button" id="b_ok" name="save"
													value="确      定"
													class="smallbutton fuctionbutton" onclick="subFormula()" 
													<% if("7".equals(request.getParameter("planStatus"))){
													%>
														disabled
													<%
													}
													%>
												>
												&nbsp;&nbsp;&nbsp;
											</td>
										</tr>
										<tr height="40">
											<td align="left">
												<input type="button" name="cancel"
													value="取      消"
													class="smallbutton fuctionbutton" onclick="window.close()">
												&nbsp;&nbsp;&nbsp;
											</td>
										</tr>
										<tr height="40">
											<td align="left">
												<input type="button" name="zbpm"
													value="<bean:message key="performance.workdiary.check.zbpm"/>"
													class="smallbutton fuctionbutton" onclick="show_pmzb('${evaluationForm.planid}');">
												&nbsp;&nbsp;&nbsp;
											</td>
										</tr>
										<tr height="40">
											<td align="left">
												<input type="button" name="check"
													value="<bean:message key="performance.workdiary.check.formula"/>"
													class="smallbutton fuctionbutton" onclick="checkFormula_total()">
												&nbsp;&nbsp;&nbsp;
											</td>
										</tr>
										<tr height="40">
											<td align="left">
												<input type="button" name="relate"
													value="<bean:message key="jx.evaluation.associateplan"/>"
													class="smallbutton fuctionbutton" onclick="importexpre1()">
												&nbsp;&nbsp;&nbsp;
											</td>
										</tr>
										<tr height="40">
											<td align="left">
												<input type='button' value='向      导' class="smallbutton fuctionbutton"
													onclick="function_Wizard2('${evaluationForm.planid}','sum');" />
												&nbsp;&nbsp;&nbsp;
											</td>
										</tr>
										<tr height="40">
											<td align="left">

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
		</hrms:tab>
	</hrms:priv>
	
	
	<hrms:priv func_id="326040502"> 
		<hrms:tab name="menu2" label="jx.evalution.examineConfigExpr"
			visible="true">
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
						<html:textarea name="evaluationForm" property="expr" cols="83"
							rows="10" styleId="xishu_formula"></html:textarea>
						<br>
						注:内容为空时自动使用等级设置中的绩效系数,根据考核等级确定
						<br>
						<br>


						<table border="0" cellspacing="0" valign='top' align="left"
							cellpadding="0" width='93%'>
							<tr>
								<td width="60%">
									<fieldset align="center">
										<legend>
											<bean:message key="gz.formula.operational.symbol" />
										</legend>
										<table style="margin-left:10px;">
											<tr>
												<td height="22">
													<Input type='button' value='1' style="width:8%;" class="smallbutton"
														onclick="symbol('1')" />
													<Input type='button' value='2' style="width:8%;" class="smallbutton"
														onclick="symbol('2')" />
													<Input type='button' value='3' style="width:8%;" class="smallbutton"
														onclick="symbol('3')" />
													<Input type='button' value='4' style="width:8%;" class="smallbutton"
														onclick="symbol('4')" />
													<Input type='button' value='5' style="width:8%;" class="smallbutton"
														onclick="symbol('5')" />
													<Input type='button' value='6' style="width:8%;" class="smallbutton"
														onclick="symbol('6')" />
													<Input type='button' value='7' style="width:8%;" class="smallbutton"
														onclick="symbol('7')" />
													<Input type='button' value='8' style="width:8%;" class="smallbutton"
														onclick="symbol('8')" />
													<Input type='button' value='9' style="width:8%;" class="smallbutton"
														onclick="symbol('9')" />
													<Input type='button' value='0' style="width:8%;" class="smallbutton"
														onclick="symbol('0')" />
												</td>
											</tr>
											<tr>
												<td height="22">
													<Input type='button' value='.' style="width:8%;" class="smallbutton"
														onclick="symbol('.')" />
													<Input type='button' value='+' style="width:8%;" class="smallbutton"
														onclick="symbol('+')" />
													<Input type='button' value='-' style="width:8%;" class="smallbutton"
														onclick="symbol('-')" />
													<Input type='button' value='*' style="width:8%;" class="smallbutton"
														onclick="symbol('*')" />
													<Input type='button' value='/' style="width:8%;" class="smallbutton"
														onclick="symbol('/')" />
													<Input type='button' value='(' style="width:8%;" class="smallbutton"
														onclick="symbol('(')" />
													<Input type='button' value=')' style="width:8%;" class="smallbutton"
														onclick="symbol(')')" />
													<Input type='button' value='>' style="width:8%;" class="smallbutton"
														onclick="symbol('>')" />
													<Input type='button' value='<' style="width:8%;" class="smallbutton"
														onclick="symbol('<')" />
													<Input type='button' value='=' style="width:8%;" class="smallbutton"
														onclick="symbol('=')" />
												</td>
											</tr>
											<tr>
												<td height="22">
													<Input type='button' value='< >' style="width:9%" class="smallbutton"
														onclick="symbol('<>')" />
													<Input type='button' value='且' style="width:8%;" class="smallbutton"
														onclick="symbol('且')" />
													<Input type='button' value='或' style="width:8%;" class="smallbutton"
														onclick="symbol('或')" />
													<Input type='button' value='如果' style="width:12%;" class="smallbutton"
														onclick="symbol('如果')" />
													<Input type='button' value='那么' style="width:12%;" class="smallbutton"
														onclick="symbol('那么')" />
													<Input type='button' value='否则' style="width:12%;" class="smallbutton"
														onclick="symbol('否则')" />
													<Input type='button' value='结束' style="width:12%;" class="smallbutton"
														onclick="symbol('结束')" />
													<Input type='button' value='分情况' style="width:17%;" class="smallbutton"
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
													<select name='codeitem' onchange='setCode()' style="width:260px">												
														<option value="[所属部门]">
															所属部门
														</option>
														<option value="[对象类别]">
															对象类别
														</option>
														<option value='[总分]'>
															总分&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
														</option>
														<option value="[排名]">
															排名
														</option>
														<option value="[组内对象数]">
															组内对象数
														</option>
														<option value="[部门排名]">
															部门排名
														</option>
														<option value="[部门人数]">
															部门人数
														</option>
														<%for(int i=0;i<exprrelatelist.size();i++) {
															CommonData data = (CommonData)exprrelatelist.get(i);
															String dataname = data.getDataName();
															String datavalue = data.getDataValue();
														%>
															<option value="<%=datavalue %>">
															<%=dataname %>
														</option>
														<%}%>
													</select>
												</td>
										</table>
									</fieldset>
								</td>
							</tr>
						</table>
					</td>
					<td valign='top'>
						<input type='button' id="b_ok" value='确      定' name="save2"
							onclick="subFormula()" 
							<% if("7".equals(request.getParameter("planStatus"))){
							%>
								disabled
							<%
							}
							%>
							class="smallbutton fuctionbutton" />
						<Br>
						<Br>
						<input type='button' value='取      消' onclick="window.close()"
							class="smallbutton fuctionbutton" />
						<Br>
						<Br>
						<input type='button' value='排名指标'
							onclick="show_pmzb('${evaluationForm.planid}');"
							class="smallbutton fuctionbutton" />
						<Br>
						<Br>
						<input type='button' value='公式检查'
							onclick="checkFormula_xishu()"
							class="smallbutton fuctionbutton" />
						<Br>
						<Br>
						<input type='button' value='向      导' class="smallbutton fuctionbutton"
							onclick="function_Wizard2('${evaluationForm.planid}','xishu');" />
						<br>
					</td>
				</tr>
			</table>
		</hrms:tab>
	</hrms:priv>
	
	
	<hrms:priv func_id="326040503"> 
		<hrms:tab name="menu3" label="jx.param.degreepro" visible="true">
			<table width="100%" border="0">
				<tr>
					<td width="85%">
						<fieldset align="center">
							<table width="100%" border="0">
								<tr>
									<td colspan="3">
										<html:radio styleId="gradeFormula0" name="evaluationForm" property="gradeFormula"
											value="0" onclick="setDis(this);changeParams();" />
										<bean:message key='jx.evalution.GradeFormula.default' />
									</td>
								</tr>								
								<tr>
									<td id="gradeFormula_2">									
										<html:radio styleId="gradeFormula2" name="evaluationForm" property="gradeFormula"
											value="2" onclick="setDis(this);changeParams();" />
										<bean:message key='jx.evalution.GradeFormula.formula' />									
									</td>
									<td>																			
									<% if(gradeFormula.equals("3")){ %>
										<html:radio styleId="gradeFormula3" name="evaluationForm" property="gradeFormula"
											value="3" onclick="setDis(this);changeParams();" />
										<bean:message key='jx.evalution.GradeFormula.formula' />
									<% } %>
																		
									<span id="gjsjformula" >
										<html:checkbox styleId="gjsjformula_c" name="evaluationForm"
															property="gjsjformula" value="1" />
										<bean:message key="jx.evalution.GradeFormula.gjsjformula" />
									</span>
									</td>
								</tr>
								<tr>
									<td colspan="2">
										<html:textarea name="evaluationForm" property="customizeGrade"
											styleId="custom_formula" cols="65" rows="8">
										</html:textarea>
									</td>
								</tr>
								<tr>
									<td colspan="2">
										<html:radio styleId="gradeFormula1" name="evaluationForm" property="gradeFormula"
											value="1" onclick="setDis(this);changeParams();" />
										<bean:message key='jx.evalution.GradeFormula.useprocedure' />
									</td>
								</tr>
							
							
							<%--
								<tr>
									<td colspan="3">
										<html:checkbox styleId="gradeFormula0" name="evaluationForm" property="gradeFormula"
											value="1" onclick="setDis(this);changeParams();" />
										<bean:message key='jx.evalution.GradeFormula.default' />
									</td>
								</tr>								
								<tr>
									<td id="gradeFormula_2">									
										<html:checkbox styleId="gradeFormula2" name="evaluationForm" property="gradeFormula"
											value="1" onclick="setDis(this);changeParams();" />
										<bean:message key='jx.evalution.GradeFormula.formula' />									
									</td>
									
								</tr>
								<tr>
									<td colspan="2">
										<html:textarea name="evaluationForm" property="customizeGrade"
											styleId="custom_formula" cols="65" rows="8">
										</html:textarea>
									</td>
								</tr>
								<tr>
									<td colspan="2">
										<html:checkbox styleId="gradeFormula1" name="evaluationForm" property="gradeFormula"
											value="1" onclick="setDis(this);changeParams();" />
										<bean:message key='jx.evalution.GradeFormula.useprocedure' />
									</td>
								</tr>
							--%>
								
								
								<tr id="ccgc">
									<td align="center" colspan="2">
										<table width="100%" border="0">
											<tr>
												<td>
													&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
													<bean:message
														key='jx.evalution.GradeFormula.inputProcedureName' />
													:
													<html:text name="evaluationForm" property="procedureName"
														size='30' styleId="procedureName"></html:text>
												</td>
											</tr>

											<tr>
												<td>
													&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
													<bean:message key='jx.evalution.GradeFormula.Introductions' />
													<br>
													&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
													<bean:message
														key='jx.evalution.GradeFormula.Introductions1' />
													<br>
													&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
													<bean:message
														key='jx.evalution.GradeFormula.Introductions2' />
													<br>
													&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
													<bean:message
														key='jx.evalution.GradeFormula.Introductions3' />
													<br>
													&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
													<bean:message
														key='jx.evalution.GradeFormula.Introductions4' />
													<br>
												</td>
											</tr>
										</table>
									</td>
								</tr>
							</table>
						</fieldset>
					</td>					
					<td width="15%" align="center">
					
						<input type='button' id="b_ok" value='确      定' name="save3"
							onclick="subFormula()"
							<% if("7".equals(request.getParameter("planStatus"))){
							%>
								disabled
							<%
							}
							%>
							class="smallbutton fuctionbutton" />
						<Br>
						<Br>
						<input type='button' value='取      消' onclick="window.close()"
							class="smallbutton fuctionbutton" />
						<Br>
						<Br>
						<input id="pmzb_check" type='button' value='排名指标'
							onclick="show_pmzb('${evaluationForm.planid}');"
							class="smallbutton fuctionbutton" />
						<Br>
						<Br>
						<input id="custom_formula_check" type='button' value='公式检查'
							onclick="checkFormula_custom();"
							class="smallbutton fuctionbutton" />
						<Br>
						<Br>
						<input id="custom_xd_look" type='button' value='向      导' class="smallbutton fuctionbutton"
							onclick="function_Wizard2('${evaluationForm.planid}','custom');" />
						<Br>
					</td>
				</tr>
				<tr id="zdygs">
					<td>
						<table border="0" cellspacing="0" valign='top' align="left"
							cellpadding="0" width='100%'>
							<tr>
								<td width="60%">
									<fieldset align="center">
										<legend>
											<bean:message key="gz.formula.operational.symbol" />
										</legend>
										<table style="margin-left:10px;">
											<tr>
												<td height="22">
													<Input type='button' value='1' style="width:8%;" class="smallbutton"
														onclick="symbol3('1')" />
													<Input type='button' value='2' style="width:8%;" class="smallbutton"
														onclick="symbol3('2')" />
													<Input type='button' value='3' style="width:8%;" class="smallbutton"
														onclick="symbol3('3')" />
													<Input type='button' value='4' style="width:8%;" class="smallbutton"
														onclick="symbol3('4')" />
													<Input type='button' value='5' style="width:8%;" class="smallbutton"
														onclick="symbol3('5')" />
													<Input type='button' value='6' style="width:8%;" class="smallbutton"
														onclick="symbol3('6')" />
													<Input type='button' value='7' style="width:8%;" class="smallbutton"
														onclick="symbol3('7')" />
													<Input type='button' value='8' style="width:8%;" class="smallbutton"
														onclick="symbol3('8')" />
													<Input type='button' value='9' style="width:8%;" class="smallbutton"
														onclick="symbol3('9')" />
													<Input type='button' value='0' style="width:8%;" class="smallbutton"
														onclick="symbol3('0')" />
												</td>
											</tr>
											<tr>
												<td height="22">
													<Input type='button' value='.' style="width:8%;" class="smallbutton"
														onclick="symbol3('.')" />
													<Input type='button' value='+' style="width:8%;" class="smallbutton"
														onclick="symbol3('+')" />
													<Input type='button' value='-' style="width:8%;" class="smallbutton"
														onclick="symbol3('-')" />
													<Input type='button' value='*' style="width:8%;" class="smallbutton"
														onclick="symbol3('*')" />
													<Input type='button' value='/' style="width:8%;" class="smallbutton"
														onclick="symbol3('/')" />
													<Input type='button' value='(' style="width:8%;" class="smallbutton"
														onclick="symbol3('(')" />
													<Input type='button' value=')' style="width:8%;" class="smallbutton"
														onclick="symbol3(')')" />
													<Input type='button' value='>' style="width:8%;" class="smallbutton"
														onclick="symbol3('>')" />
													<Input type='button' value='<' style="width:8%;" class="smallbutton"
														onclick="symbol3('<')" />
													<Input type='button' value='=' style="width:8%;" class="smallbutton"
														onclick="symbol3('=')" />
												</td>
											</tr>
											<tr>
												<td height="22">
													<Input type='button' value='< >' style="width:9%" class="smallbutton"
														onclick="symbol3('<>')" />
													<Input type='button' value='且' style="width:8%;" class="smallbutton"
														onclick="symbol3('且')" />
													<Input type='button' value='或' style="width:8%;" class="smallbutton"
														onclick="symbol3('或')" />
													<Input type='button' value='如果' style="width:12%;" class="smallbutton"
														onclick="symbol3('如果')" />
													<Input type='button' value='那么' style="width:12%;" class="smallbutton"
														onclick="symbol3('那么')" />
													<Input type='button' value='否则' style="width:12%;" class="smallbutton"
														onclick="symbol3('否则')" />
													<Input type='button' value='结束' style="width:12%;" class="smallbutton"
														onclick="symbol3('结束')" />
													<Input type='button' value='分情况' style="width:17%;" class="smallbutton"
														onclick="symbol3('分情况')" />
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
													<select name='codeitems' onchange='setCodes()' style="width:260px">														
														<option value="[所属部门]">
															所属部门
														</option>
														<option value="[对象类别]">
															对象类别
														</option>
														<option value='[总分]'>
															总分&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
														</option>
														<option value="[排名]">
															排名
														</option>
														<option value="[组内对象数]">
															组内对象数
														</option>
														<option value="[部门排名]">
															部门排名
														</option>
														<option value="[部门人数]">
															部门人数
														</option>									
														<option value="[等级]">
															等级
														</option>
														<%for(int i=0;i<exprrelatelist.size();i++) {
															CommonData data = (CommonData)exprrelatelist.get(i);
															String dataname = data.getDataName();
															String datavalue = data.getDataValue();
														%>
															<option value="<%=datavalue %>">
															<%=dataname %>
														</option>
														<%}%>
													</select>
												</td>
										</table>
									</fieldset>
								</td>
							</tr>
						</table>
					</td>
				</tr>
			</table>
		</hrms:tab>
	</hrms:priv>
	
	<%}else{%>
	<hrms:priv func_id="3603030701"> 
		<hrms:tab name="menu1" label="jx.evalution.countExpr" visible="true">
			<table width="100%" height="300" border="0" align="center">
				<tr>
					<td>
						<table width="100%" height="300" border="0">
							<tr>
								<td width="85%" align="center">
									<table border="0" align="center">
										<tr>
											<td valign='top'>
												<table width="100%" border="0">
													<tr>
														<td colspan="2" align="center">
															<html:textarea name="evaluationForm" property="formula"
																styleId="total_formula" style="width:640px;" rows="10"></html:textarea>
														</td>
													</tr>
													<tr>
														<td width="50%">
															<fieldset align="center"><!-- style="width:310px"-->
																<legend>
																	<bean:message key="gz.formula.operational.symbol" />
																</legend>
																<!-- xus 19/12/31 【56773】v77能力素质：素质评估/评估计算设置计算公式页面边框重合 -->
																<table width="100%" border="0" ><!-- style="margin-left:10px;" -->
																	<tr>
																		<td height="100">
																			<table width="100%" border="0">
																				<tr>
																					<td height="22">
																						<input type="button" value="0" style="width:8%;" 
																							onclick="symbol2('0');" class="smallbutton">

																						<input type="button" value="1" style="width:8%;" 
																							onclick="symbol2('1');" class="smallbutton">

																						<input type="button" value="2" style="width:8%;" 
																							onclick="symbol2('2');" class="smallbutton">

																						<input type="button" value="3" style="width:8%;" 
																							onclick="symbol2('3');" class="smallbutton">

																						<input type="button" value="4" style="width:8%;" 
																							onclick="symbol2('4');" class="smallbutton">

																						<input type="button" value="5" style="width:8%;" 
																							onclick="symbol2('5');" class="smallbutton">

																						<input type="button" value="6" style="width:8%;" 
																							onclick="symbol2('6');" class="smallbutton">

																						<input type="button" value="7" style="width:8%;" 
																							onclick="symbol2('7');" class="smallbutton">

																						<input type="button" value="8" style="width:8%;" 
																							onclick="symbol2('8');" class="smallbutton">

																						<input type="button" value="9" style="width:8%;" 
																							onclick="symbol2('9');" class="smallbutton">

																					</td>
																				</tr>
																				<tr>
																					<td height="22">
																						<input type="button" value="." style="width:8%;" 
																							onclick="symbol2('.');" class="smallbutton">

																						<input type="button" value="(" style="width:8%;" 
																							onclick="symbol2('(');" class="smallbutton">

																						<input type="button" value=")" style="width:8%;" 
																							onclick="symbol2(')');" class="smallbutton">

																						<input type="button" value="+" style="width:8%;" 
																							onclick="symbol2('+');" class="smallbutton">

																						<input type="button" value="-" style="width:8%;" 
																							onclick="symbol2('-');" class="smallbutton">

																						<input type="button" value="*" style="width:8%;" 
																							onclick="symbol2('*');" class="smallbutton">

																						<input type="button" value="/" style="width:8%;" 
																							onclick="symbol2('/');" class="smallbutton">

																						<input type="button" value='>' style="width:8%;" 
																							onclick="symbol2('>');" class="smallbutton">

																						<input type="button" value="<" style="width:8%;" 
																						 		onclick="symbol2('<');" class="smallbutton">
																						

																						<input type="button" value=" = "
																							onclick="symbol2('=');" class="smallbutton">
																					</td>
																				</tr>
																				<tr>
																					<td height="22">
																						<input type="button" value="< >" style="width:9%" 
																							onclick="symbol2('<>');" class="smallbutton">

																						<input type="button" value="且" style="width:8%;" 
																							onclick="symbol2('且');" class="smallbutton">

																						<input type="button" value="或" style="width:8%;" 
																							onclick="symbol2('或');" class="smallbutton">

																						<input type="button" value="如果" style="width:12%;" 
																							onclick="symbol2('如果');" class="smallbutton">

																						<input type="button" value="那么" style="width:12%;" 
																							onclick="symbol2('那么');" class="smallbutton">
																					
																						<input type="button" value="否则" style="width:12%;" 
																							onclick="symbol2('否则');" class="smallbutton">

																						<input type="button" value="结束" style="width:12%;" 
																							onclick="symbol2('结束');" class="smallbutton">

																						<input type="button" value="分情况" style="width:17%;" 
																							onclick="symbol2('分情况');" class="smallbutton">
																					</td>
																				</tr>
																			</table>
																		</td>
																	</tr>
																</table>
															</fieldset>
														</td>
														<td align="center" width="50%">
															<fieldset align="center" ><!--style="width:100%"-->
																<legend>
																	<bean:message key='jx.evalution.usePoints' />
																</legend>
																<table width="100%" border="0" height="100">
																	<tr height="10">
																		<td valign="top">
																			<table width="100%" border="0">
																				<tr>
																					<td>																					
																						<html:select name="evaluationForm"
																							property="expression" size="1"
																							onchange="addrelate('formula',this);" style="width:260px">
																							<html:option value="[本次得分]">本次得分</html:option>
																							<html:option value="[所属部门]">所属部门</html:option>
																							<html:option value="[对象类别]">对象类别</html:option>
																							<html:optionsCollection property="exprrelatelist" value="dataValue" label="dataName" />
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
								<td width="15%">
									<table border="0" align="center">
										<tr height="40">
											<td align="center">
												<input style="width:60px" type="button" id="b_ok" name="save"
													value="确      定"
													class="smallbutton" onclick="subFormula()"
													<% if("7".equals(request.getParameter("planStatus"))){
													%>
														disabled
													<%
													}
													%>
													>
												&nbsp;&nbsp;&nbsp;
											</td>
										</tr>
										<tr height="40">
											<td align="center">
												<input style="width:60px" type="button" name="cancel"
													value="取      消"
													class="smallbutton" onclick="window.close()">
												&nbsp;&nbsp;&nbsp;
											</td>
										</tr>
										<tr height="40">
											<td align="center">
												<input style="width:60px" type="button" name="zbpm"
													value="<bean:message key="performance.workdiary.check.zbpm"/>"
													class="smallbutton" onclick="show_pmzb('${evaluationForm.planid}');">
												&nbsp;&nbsp;&nbsp;
											</td>
										</tr>
										<tr height="40">
											<td align="center">
												<input style="width:60px" type="button" name="check"
													value="<bean:message key="performance.workdiary.check.formula"/>"
													class="smallbutton" onclick="checkFormula_total()">
												&nbsp;&nbsp;&nbsp;
											</td>
										</tr>
										

										<%--<tr height="40">
											<td align="center">
												<input type="button" name="relate"
													value="<bean:message key="jx.evaluation.associateplan"/>"
													class="smallbutton" onclick="importexpre1()">
												&nbsp;&nbsp;&nbsp;
											</td>
										</tr>--%>

										
										<tr height="40">
											<td align="center">
												<input style="width:60px" type='button' value='向      导' class="smallbutton"
													onclick="function_Wizard2('${evaluationForm.planid}','sum');" />
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
		</hrms:tab>
	</hrms:priv>
	
	<%if(false){ // 暂时隐藏%>
	<hrms:priv func_id="3603030702"> 
		<hrms:tab name="menu2" label="jx.evalution.examineConfigExpr"
			visible="true">
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
							onclick="this.pos=document.selection.createRange();" cols="83"
							rows="10" styleId="xishu_formula"></html:textarea>
						<br>
						注:内容为空时自动使用等级设置中的绩效系数,根据考核等级确定
						<br>
						<br>


						<table border="0" cellspacing="0" valign='top' align="left"
							cellpadding="0" width='90%'>
							<tr>
								<td width="60%">
									<fieldset align="center" style="width:100%;">
										<legend>
											<bean:message key="gz.formula.operational.symbol" />
										</legend>
										<table style="margin-left:10px;">
											<tr>
												<td height="22">
													<Input type='button' value='1' style="width:8%;" class="smallbutton"
														onclick="symbol('1')" />
													<Input type='button' value='2' style="width:8%;" class="smallbutton"
														onclick="symbol('2')" />
													<Input type='button' value='3' style="width:8%;" class="smallbutton"
														onclick="symbol('3')" />
													<Input type='button' value='4' style="width:8%;" class="smallbutton"
														onclick="symbol('4')" />
													<Input type='button' value='5' style="width:8%;" class="smallbutton"
														onclick="symbol('5')" />
													<Input type='button' value='6' style="width:8%;" class="smallbutton"
														onclick="symbol('6')" />
													<Input type='button' value='7' style="width:8%;" class="smallbutton"
														onclick="symbol('7')" />
													<Input type='button' value='8' style="width:8%;" class="smallbutton"
														onclick="symbol('8')" />
													<Input type='button' value='9' style="width:8%;" class="smallbutton"
														onclick="symbol('9')" />
													<Input type='button' value='0' style="width:8%;" class="smallbutton"
														onclick="symbol('0')" />
												</td>
											</tr>
											<tr>
												<td height="22">
													<Input type='button' value='.' style="width:8%;" class="smallbutton"
														onclick="symbol('.')" />
													<Input type='button' value='+' style="width:8%;" class="smallbutton"
														onclick="symbol('+')" />
													<Input type='button' value='-' style="width:8%;" class="smallbutton"
														onclick="symbol('-')" />
													<Input type='button' value='*' style="width:8%;" class="smallbutton"
														onclick="symbol('*')" />
													<Input type='button' value='/' style="width:8%;" class="smallbutton"
														onclick="symbol('/')" />
													<Input type='button' value='(' style="width:8%;" class="smallbutton"
														onclick="symbol('(')" />
													<Input type='button' value=')' style="width:8%;" class="smallbutton"
														onclick="symbol(')')" />
													<Input type='button' value='>' style="width:8%;" class="smallbutton"
														onclick="symbol('>')" />
													<Input type='button' value='<' style="width:8%;" class="smallbutton"
														onclick="symbol('<')" />
													<Input type='button' value='=' style="width:8%;" class="smallbutton"
														onclick="symbol('=')" />
												</td>
											</tr>
											<tr>
												<td height="22">
													<Input type='button' value='< >' style="width:9%" class="smallbutton"
														onclick="symbol('<>')" />
													<Input type='button' value='且' style="width:8%;" class="smallbutton"
														onclick="symbol('且')" />
													<Input type='button' value='或' style="width:8%;" class="smallbutton"
														onclick="symbol('或')" />
													<Input type='button' value='如果' style="width:12%;" class="smallbutton"
														onclick="symbol('如果')" />
													<Input type='button' value='那么' style="width:12%;" class="smallbutton"
														onclick="symbol('那么')" />
													<Input type='button' value='否则' style="width:12%;" class="smallbutton"
														onclick="symbol('否则')" />
													<Input type='button' value='结束' style="width:12%;" class="smallbutton"
														onclick="symbol('结束')" />
													<Input type='button' value='分情况' style="width:17%;" class="smallbutton"
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
													<select name='codeitem' onchange='setCode()' style="width:260px">												
														<option value="[所属部门]">
															所属部门
														</option>
														<option value="[对象类别]">
															对象类别
														</option>
														<option value='[总分]'>
															总分&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
														</option>
														<option value="[排名]">
															排名
														</option>
														<option value="[组内对象数]">
															组内对象数
														</option>
														<option value="[部门排名]">
															部门排名
														</option>
														<option value="[部门人数]">
															部门人数
														</option>
														<%for(int i=0;i<exprrelatelist.size();i++) {
															CommonData data = (CommonData)exprrelatelist.get(i);
															String dataname = data.getDataName();
															String datavalue = data.getDataValue();
														%>
															<option value="<%=datavalue %>">
															<%=dataname %>
														</option>
														<%}%>
													</select>
												</td>
										</table>
									</fieldset>
								</td>
							</tr>
						</table>
					</td>
					<td valign='top'>
						<input type='button' id="b_ok" value='确      定' name="save2"
							onclick="subFormula()"
							<% if("7".equals(request.getParameter("planStatus"))){
							%>
								disabled
							<%
							}
							%>
							class="smallbutton" />
						<Br>
						<Br>
						<input type='button' value='取      消' onclick="window.close()"
							class="smallbutton" />
						<Br>

						<br>
						<input type='button' value='排名指标'
							onclick="show_pmzb('${evaluationForm.planid}');"
							class="smallbutton" />
						<Br>
						<Br>
						<input type='button' value='公式检查'
							onclick="checkFormula_xishu()"
							class="smallbutton" />
						<Br>
						<Br>
						<input type='button' value='向      导' class="smallbutton"
							onclick="function_Wizard2('${evaluationForm.planid}','xishu');" />
						<br>
					</td>
				</tr>
			</table>
		</hrms:tab>
	</hrms:priv>
	<%}%>
	
	<hrms:priv func_id="3603030703"> 
		<hrms:tab name="menu3" label="jx.param.degreepro" visible="true">
			<table width="100%" border="0">
				<tr>
					<td width="85%">
						<fieldset align="center">
							<table width="100%" border="0">
								<tr>
									<td colspan="3">
										<html:radio styleId="gradeFormula0" name="evaluationForm" property="gradeFormula"
											value="0" onclick="setDis(this);changeParams();" />
										<bean:message key='jx.evalution.GradeFormula.default' />
									</td>
								</tr>								
								<tr>
									<td id="gradeFormula_2">									
										<html:radio styleId="gradeFormula2" name="evaluationForm" property="gradeFormula"
											value="2" onclick="setDis(this);changeParams();" />
										<bean:message key='jx.evalution.GradeFormula.formula' />									
									</td>
									<td>																			
									<% if(gradeFormula.equals("3")){ %>
										<html:radio styleId="gradeFormula3" name="evaluationForm" property="gradeFormula"
											value="3" onclick="setDis(this);changeParams();" />
										<bean:message key='jx.evalution.GradeFormula.formula' />
									<% } %>
																		
									<span id="gjsjformula" >
										<html:checkbox styleId="gjsjformula_c" name="evaluationForm"
															property="gjsjformula" value="1" />
										<bean:message key="jx.evalution.GradeFormula.gjsjformula" />
									</span>
									</td>
								</tr>
								<tr>
									<td colspan="2">
										<html:textarea name="evaluationForm" property="customizeGrade"
											styleId="custom_formula" cols="65" rows="8">
										</html:textarea>
									</td>
								</tr>
								<tr>
									<td colspan="2">
										<html:radio styleId="gradeFormula1" name="evaluationForm" property="gradeFormula"
											value="1" onclick="setDis(this);changeParams();" />
										<bean:message key='jx.evalution.GradeFormula.useprocedure' />
									</td>
								</tr>
								<tr id="ccgc">
									<td align="center" colspan="2">
										<table width="100%" border="0">
											<tr>
												<td>
													&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
													<bean:message
														key='jx.evalution.GradeFormula.inputProcedureName' />
													:
													<html:text name="evaluationForm" property="procedureName"
														size='30' styleId="procedureName"></html:text>
												</td>
											</tr>

											<tr>
												<td>
													&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
													<bean:message key='jx.evalution.GradeFormula.Introductions' />
													<br>
													&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
													<bean:message
														key='jx.evalution.GradeFormula.Introductions1' />
													<br>
													&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
													<bean:message
														key='jx.evalution.GradeFormula.Introductions2' />
													<br>
													&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
													<bean:message
														key='jx.evalution.GradeFormula.Introductions3' />
													<br>
													&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
													<bean:message
														key='jx.evalution.GradeFormula.Introductions4' />
													<br>
												</td>
											</tr>
										</table>
									</td>
								</tr>
							</table>
						</fieldset>
					</td>					
					<td width="15%" align="center">
					
						<input type='button' id="b_ok" value='确      定' name="save3"
							onclick="subFormula()"
							<% if("7".equals(request.getParameter("planStatus"))){
							%>
								disabled
							<%
							}
							%>
							class="smallbutton" />
						<Br>
						<Br>
						<input type='button' value='取      消' onclick="window.close()"
							class="smallbutton" />
						<Br>

						<Br>
						<input id="pmzb_check" type='button' value='排名指标'
							onclick="show_pmzb('${evaluationForm.planid}');"
							class="smallbutton" />
						<Br>
						<Br>
						<input id="custom_formula_check" type='button' value='公式检查'
							onclick="checkFormula_custom();"
							class="smallbutton" />
						<Br>
						<Br>
						<input id="custom_xd_look" type='button' value='向      导' class="smallbutton"
							onclick="function_Wizard2('${evaluationForm.planid}','custom');" />
						<Br>
					</td>
				</tr>
				<tr id="zdygs">
					<td>
						<table border="0" cellspacing="0" valign='top' align="left"
							cellpadding="0" width='100%'>
							<tr>
								<td width="60%">
									<fieldset align="center">
										<legend>
											<bean:message key="gz.formula.operational.symbol" />
										</legend>
										<table style="margin-left:10px;">
											<tr>
												<td height="22">
													<Input type='button' value='1' style="width:8%;" class="smallbutton"
														onclick="symbol3('1')" />
													<Input type='button' value='2' style="width:8%;" class="smallbutton"
														onclick="symbol3('2')" />
													<Input type='button' value='3' style="width:8%;" class="smallbutton"
														onclick="symbol3('3')" />
													<Input type='button' value='4' style="width:8%;" class="smallbutton"
														onclick="symbol3('4')" />
													<Input type='button' value='5' style="width:8%;" class="smallbutton"
														onclick="symbol3('5')" />
													<Input type='button' value='6' style="width:8%;" class="smallbutton"
														onclick="symbol3('6')" />
													<Input type='button' value='7' style="width:8%;" class="smallbutton"
														onclick="symbol3('7')" />
													<Input type='button' value='8' style="width:8%;" class="smallbutton"
														onclick="symbol3('8')" />
													<Input type='button' value='9' style="width:8%;" class="smallbutton"
														onclick="symbol3('9')" />
													<Input type='button' value='0' style="width:8%;" class="smallbutton"
														onclick="symbol3('0')" />
												</td>
											</tr>
											<tr>
												<td height="22">
													<Input type='button' value='.' style="width:8%;" class="smallbutton"
														onclick="symbol3('.')" />
													<Input type='button' value='+' style="width:8%;" class="smallbutton"
														onclick="symbol3('+')" />
													<Input type='button' value='-' style="width:8%;" class="smallbutton"
														onclick="symbol3('-')" />
													<Input type='button' value='*' style="width:8%;" class="smallbutton"
														onclick="symbol3('*')" />
													<Input type='button' value='/' style="width:8%;" class="smallbutton"
														onclick="symbol3('/')" />
													<Input type='button' value='(' style="width:8%;" class="smallbutton"
														onclick="symbol3('(')" />
													<Input type='button' value=')' style="width:8%;" class="smallbutton"
														onclick="symbol3(')')" />
													<Input type='button' value='>' style="width:8%;" class="smallbutton"
														onclick="symbol3('>')" />
													<Input type='button' value='<' style="width:8%;" class="smallbutton"
														onclick="symbol3('<')" />
													<Input type='button' value='=' style="width:8%;" class="smallbutton"
														onclick="symbol3('=')" />
												</td>
											</tr>
											<tr>
												<td height="22">
													<Input type='button' value='< >' style="width:9%" class="smallbutton"
														onclick="symbol3('<>')" />
													<Input type='button' value='且' style="width:8%;" class="smallbutton"
														onclick="symbol3('且')" />
													<Input type='button' value='或' style="width:8%;" class="smallbutton"
														onclick="symbol3('或')" />
													<Input type='button' value='如果' style="width:12%;" class="smallbutton"
														onclick="symbol3('如果')" />
													<Input type='button' value='那么' style="width:12%;" class="smallbutton"
														onclick="symbol3('那么')" />
													<Input type='button' value='否则' style="width:12%;" class="smallbutton"
														onclick="symbol3('否则')" />
													<Input type='button' value='结束' style="width:12%;" class="smallbutton"
														onclick="symbol3('结束')" />
													<Input type='button' value='分情况' style="width:17%;" class="smallbutton"
														onclick="symbol3('分情况')" />
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
													<select name='codeitems' onchange='setCodes()' style="width:260px">														
														<option value="[所属部门]">
															所属部门
														</option>
														<option value="[对象类别]">
															对象类别
														</option>
														<option value='[总分]'>
															总分&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
														</option>
														<option value="[排名]">
															排名
														</option>
														<option value="[组内对象数]">
															组内对象数
														</option>
														<option value="[部门排名]">
															部门排名
														</option>
														<option value="[部门人数]">
															部门人数
														</option>									
														<option value="[等级]">
															等级
														</option>
														<%for(int i=0;i<exprrelatelist.size();i++) {
															CommonData data = (CommonData)exprrelatelist.get(i);
															String dataname = data.getDataName();
															String datavalue = data.getDataValue();
														%>
															<option value="<%=datavalue %>">
															<%=dataname %>
														</option>
														<%}%>
													</select>
												</td>
										</table>
									</fieldset>
								</td>
							</tr>
						</table>
					</td>
				</tr>
			</table>
		</hrms:tab>
	</hrms:priv>	
	<%}%>
	
	</hrms:tabset>
	
	<%
	// 区分 绩效管理和能力素质 模块的功能授权					    
	if(busiType==null || busiType.trim().length()<=0 || !busiType.equals("1"))
	{
		if(userView.hasTheFunction("326040501")==false){ 
	%>  
		<html:hidden name="evaluationForm" property="formula" styleId="total_formula"/>	
	<%
		}if(userView.hasTheFunction("326040502")==false){ 
	%>
		<html:hidden name="evaluationForm" property="expr" styleId="xishu_formula"/>				
	<%
		}if(userView.hasTheFunction("326040503")==false){ 
	%>		
		<html:hidden name="evaluationForm" property="gradeFormula"/>
		<html:hidden name="evaluationForm" property="procedureName"	styleId="procedureName"/>
		<html:hidden name="evaluationForm" property="customizeGrade" styleId="custom_formula"/>
	<%
		}
	}else
	{  
		if(userView.hasTheFunction("3603030701")==false){ 
	%>  
		<html:hidden name="evaluationForm" property="formula" styleId="total_formula"/>	
	<%
		}if(userView.hasTheFunction("3603030702")==false){ 
	%>
		<html:hidden name="evaluationForm" property="expr" styleId="xishu_formula"/>				
	<%
		}if(userView.hasTheFunction("3603030703")==false){ 
	%>		
		<html:hidden name="evaluationForm" property="gradeFormula"/>
		<html:hidden name="evaluationForm" property="procedureName"	styleId="procedureName"/>
		<html:hidden name="evaluationForm" property="customizeGrade" styleId="custom_formula"/>		
	<%
		}
	}
	%>
	
<div id='wait' style='position:absolute;top:160;left:250;display:none;'>
  <table border="1" width="300" cellspacing="0" cellpadding="4" class="table_style" height="87" align="center">
           <tr>
             <td id="wait_desc" class="td_style" height=24>正在计算，请稍候....</td>
           </tr>
           <tr>
             <td style="font-size:12px;line-height:200%" align=center>
               <marquee class="marquee_style" direction="right" width="260" scrollamount="5" scrolldelay="10" >
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
		
</html:form>
<script>
//	if($F('gradeFormula')=="0")
//		evaluationForm.procedureName.disabled=true;
	deviationTotalScore();
	var obj=new Object();
	obj.value="01"
	changeScoreFormula(obj);
	var formulaType = "${evaluationForm.gradeFormula}";
	if(formulaType==3)	
	{
		if(document.getElementById("gjsjformula_c")!=null)
			document.getElementById("gjsjformula_c").checked=true;
		Element.hide('gradeFormula_2');
	}
	else if(document.getElementById("gjsjformula_c")!=null)
		document.getElementById("gjsjformula_c").checked=false;
			
	if(document.getElementById("gradeFormula0")!=null && document.getElementById("gradeFormula0").checked==true)
	{
		if(document.getElementById("gjsjformula_c")!=null)
			document.getElementById("gjsjformula_c").checked=false;
		document.getElementById('gjsjformula').style.display = 'none';
		document.getElementById('custom_formula').style.display = 'none';
		document.getElementById('zdygs').style.display = 'none';
		document.getElementById('ccgc').style.display = '';
		document.getElementById('custom_formula_check').style.display = 'none';
		document.getElementById('custom_xd_look').style.display = 'none';
		
	}else if(document.getElementById("gradeFormula1")!=null && document.getElementById("gradeFormula1").checked==true)
	{
		if(document.getElementById("gjsjformula_c")!=null)
			document.getElementById("gjsjformula_c").checked=false;
		
		document.getElementById('gjsjformula').style.display = 'none';
		document.getElementById('custom_formula').style.display = 'none';
		document.getElementById('zdygs').style.display = 'none';
		document.getElementById('ccgc').style.display = '';
		document.getElementById('custom_formula_check').style.display = 'none';
		document.getElementById('custom_xd_look').style.display = 'none';
	}else if((document.getElementById("gradeFormula2")!=null && document.getElementById("gradeFormula2").checked==true) || (document.getElementById("gradeFormula3")!=null && document.getElementById("gradeFormula3").checked==true))
	{
		document.getElementById('gjsjformula').style.display = '';
		document.getElementById('custom_formula').style.display = '';
		document.getElementById('zdygs').style.display = '';
		document.getElementById('ccgc').style.display = 'none';
		document.getElementById('custom_formula_check').style.display = '';
		document.getElementById('custom_xd_look').style.display = '';
	}	
			
</script>
<script type="text/javascript">
var aa=document.getElementsByTagName("input");
for(var i=0;i<aa.length;i++){
	if(aa[i].type=="text"){
		aa[i].className="inputtext";
	}
}
</script>