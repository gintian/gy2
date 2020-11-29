<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<jsp:useBean id="appraiseselfForm" scope="session" class="com.hjsj.hrms.actionform.performance.AppraiseselfForm"/>
<%
	if(appraiseselfForm.getMessage().equals(""))
	{
	}
	else
	{
	%>
	<script>
	alert('<%=appraiseselfForm.getMessage()%>');
	</script>
	<%
	appraiseselfForm.setMessage("");
	appraiseselfForm.messageClear();
	}
%>
<script>

JavaScript:history.back();
</script>