<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ page import="com.hjsj.hrms.actionform.performance.evaluation.EvaluationForm" %>

<%
EvaluationForm evaluationForm = (EvaluationForm)session.getAttribute("evaluationForm");
String remarkFieldName = evaluationForm.getRemarkFieldName();
%>
<script language='javascript'>
  	var info=dialogArguments;
</script>

	<table border="0" cellspacing="0" align="center" cellpadding="2">
		<tr>
			<td nowrap>
			<script language='javascript'>
  				document.write(info[0]);
			</script>
			
    -----<%=remarkFieldName %>  显示：
			</td>
		</tr>
		
		<tr>
			<td align="center" nowrap>
					<html:textarea name="evaluationForm" styleId="remarkFieldValue" property="remarkFieldValue" cols="45" rows="15"></html:textarea>
			</td>
		</tr>
			
			
	</table>
		
		<table border="0" cellspacing="0" align="center" cellpadding="2" width="50%" >
			<tr>
				<td align="center">
					<input type="button" class="mybutton" value="&nbsp;<bean:message key='button.close' />&nbsp;" onClick="window.close();">
				</td>
			</tr>
		</table>
