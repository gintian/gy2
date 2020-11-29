<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean"%>
<%@ taglib uri="/tags/struts-html" prefix="html"%>
<%@ taglib uri="/tags/struts-logic" prefix="logic"%>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ page import="org.apache.commons.beanutils.LazyDynaBean" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Insert title here</title>
</head>
<body>
<form action="" name="staticStatementForm">
	<table width='600' align="left" border="0" cellspadding="0" cellspacing="0" class="mainbackground">
		<tr align="left">
			<td align="top" align=left>
				<div class="title" >
				</div>
			</td>
		</tr>
		<table width="100%" align="lsft" border="0" cellspadding="0" cellspacing="0" class="ListTable">
		<thead>
		<tr>
			<td align="center" class="TableRow" nowrap> 
				<input type="checkbox" name="selbox" onclick="batch_select(this,'staticStatementForm.select')" title='<bean:message key="label.query.selectall"/>'>
			</td>
			<td align="center" class="TableRow" nowrap>
			机构编码
			</td>
			
			<td align="center" class="TableRow" nowrap>
				机构名称
			</td>
			<td valign="top"><br></td><td valign="top"><br></td><td valign="top"><br></td><td align="center" class="TableRow" nowrap>
			排序
			</td>
		</tr>
		</thead>
		
		<% int i=0;%>
		<logic:iterate id="element" name="staticStatementForm" property="unitslist" indexId="index">
			<tr>
			<td align="center" class="RecordRow" nowrap>
				<hrms:checkmultibox name="staticStatementForm" property="staticStatementForm.select" value="true" indexes="indexes"/>
			</td>
			<td align="center" class="RecordRow" nowrap>
			<bean:write name="element" property="unitsid"/>
			</td>
			<td>
			<bean:write name="element" property="unitsname"/>
			</td>
			<td align="left" class="RecordRow" nowrap>
			&nbsp;<a href="javaScript:upItem()">
			<img src="../../images/up01.gif"  border=0></a> 
			&nbsp;<a href="javaScript:downItem()">
			<img src="../../images/down01.gif"  border=0></a> 
			</td>
			</tr>
		</logic:iterate>
		</table>
		
	</table>
	<table>
		</table>
	</form>
</body>
</html>