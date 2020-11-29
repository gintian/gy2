<%@ page contentType="text/html; charset=UTF-8" language="java"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean"%>
<%@ taglib uri="/tags/struts-html" prefix="html"%>
<%@ taglib uri="/tags/struts-logic" prefix="logic"%>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms"%>
<%@page import="com.hjsj.hrms.actionform.gz.gz_accounting.piecerate.PieceRateForm"%>
<%
	  PieceRateForm pieceRateForm = (PieceRateForm)session
					.getAttribute("pieceRateForm");
%>
<link rel="stylesheet" href="/css/css1.css" type="text/css">
<link rel="stylesheet"
	href="/gz/gz_budget/budget_rule/formula/budget_formula.css"
	type="text/css">
<script type="text/javascript"
	src="/gz/gz_accounting/piecerate/piecerate.js"></script>
<hrms:themes />
<html>	
  <body>	
<html:form action="/gz/gz_accounting/piecerate/search_piecerate">

		<fieldset  style="width: 590px; height: 85%" align="center" >
		<legend>设置</legend>
		<table width="100%" border="0" align="center" height="100%">

			<tr height="90%">
				<td align="center" >

					<div id="scroll_box2">
				<table width="90%" border="0" align="center" height="80%" class="ListTable1">
						<hrms:extenditerate id="element" name="pieceRateForm" property="pagelistform.list" indexes="indexes"  pagination="pagelistform.pagination" 
                                                                        pageCount="20000" scope="session">
                                                   
						<tr>
							<td  width="20%" align="center" class="RecordRow" nowrap>
								<bean:write  name="element" property="codeitemdesc" filter="true"/>
								<input type="hidden" name="codeitemid" value="<bean:write  name="element" property="codeitemid" filter="true"/>">
							</td>
							<td  width="80%" align="center" class="RecordRow" nowrap>
								<table>
									<tr>
										<td align="right" >
											作业票&nbsp;
										</td>
										<td >
											<hrms:optioncollection name="pieceRateForm" property="setlist" collection="list"/>
											<html:select name="element" property="jobtable" onchange="" style="width:190">
											<html:options collection="list" property="dataValue" labelProperty="dataName" />
											</html:select>
										</td>
									</tr>
									<tr>
										<td align="right" >
											考勤签到表&nbsp;
										</td>
										<td >
											<hrms:optioncollection name="pieceRateForm" property="setlist" collection="list"/>
											<html:select name="element" property="signtable" onchange="" style="width:190">
											<html:options collection="list" property="dataValue" labelProperty="dataName" />
											</html:select>
										</td>
									</tr>
								</table>
							</td>
						</tr>
						</hrms:extenditerate>
				</table>
				</div>
				
				</td>
			</tr>
			</table>
			</fieldset>	
			<table width="100%" border="0" align="center" height="10%">		
					<tr>
						<td align="right">
							<input type="button" value="保存" class="mybutton" onclick="saveSetUp();">
						</td>
						<td align="left">
							<input type="button" value="取消" class="mybutton" onclick="window.close()">
						</td>
					</tr>	
			</table>		
</html:form>
</body>
</html>
