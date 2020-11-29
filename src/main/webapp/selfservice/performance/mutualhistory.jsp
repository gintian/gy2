<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<jsp:useBean id="appraiseMutualForm" scope="session" class="com.hjsj.hrms.actionform.performance.AppraiseMutualForm"/>
<%
	if(appraiseMutualForm.getMessage().equals(""))
	{
	}
	else
	{
	%>
	<script>
	alert('<%=appraiseMutualForm.getMessage()%>');
	</script>
	<%
	appraiseMutualForm.setMessage("");
	appraiseMutualForm.messageClear();
	}
%>
<script>

JavaScript:history.back();
</script>