<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ page import="com.hjsj.hrms.actionform.report.report_isApprove.Report_isApproveForm" %>
<%
	Report_isApproveForm report_isApproveForm = (Report_isApproveForm)session.getAttribute("report_isApproveForm");
%>

<HTML>
<HEAD>
<link href="/css/xtree.css" rel="stylesheet" type="text/css" > 
<hrms:themes></hrms:themes>
<script type="text/javascript">
	function ret(){
		var rdos = document.getElementsByTagName("input");
		var returnValue;
		for(var i=0; i<rdos.length; i++){
			if(rdos[i].type=="radio" && rdos[i].checked){
				returnValue = rdos[i].value;
				parent.returnValue = rdos[i].value;
			}
		}
		if(returnValue==null){
			alert("请选择审批人！！");
			return;
		}
		closeWin();
		
	}
	function closeWin(){
		if(parent.Ext.getCmp('appealWin'))
			parent.Ext.getCmp('appealWin').close();
		else
			window.close();
	}
</script>
</HEAD>
<body style="text-align: center;"> 
<br>
<table style="width: 100%;">
	<tr>
	<td align="center" valign="top">
	<fieldset style="width: 70%; height: 180px;" >
		<legend>
			审批人
		</legend>
		<table width="80%">
			<tr>
			<td align="center">
			<div >
			<table width="100%">
			<html:form action="/report/report_isApprove/reportIsApprove">
				<%int i = 1; %>					
				<hrms:extenditerate id="element" name="report_isApproveForm" property="report_isApproveForm.list" indexes="indexes" 
						pagination="report_isApproveForm.pagination" pageCount="20"
						scope="session">
					<%if(i%2!=0){ %>
					
						<tr>
						<td align="left">
						
						<input type="radio"  name="mainbody_id" value="<bean:write name="element" property="mainbody_id" filter="true"/>"/><bean:write name="element" property="a0101" filter="true"/>
						</td>
					<%}else{ %>
						<td align="left">
						<input type="radio"  name="mainbody_id" value="<bean:write name="element" property="mainbody_id" filter="true"/>"/><bean:write name="element" property="a0101" filter="true"/>
						</td>
						</tr>
					
					<%} i++;%>
				
				</hrms:extenditerate>
			</html:form>
			</table>
			</div>
			</td>
			</tr>
		</table>
	</fieldset>
	</td>
	</tr>
</table>
<table style="width: 100%; height: 20px;">
	<tr>
		<td align="center">
			<input type="button" class="mybutton" value='<bean:message key="button.ok"/>' onclick="ret();"/>&nbsp;
			<input type="button" class="mybutton" value='<bean:message key="button.cancel"/>' onclick="closeWin();"/>
		</td>
	</tr>
</table>

<BODY>
</HTML>


