<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<jsp:useBean id="welcomeForm" class="com.hjsj.hrms.actionform.welcome.WelcomeForm" scope="session"/>
<style type="text/css">
<!--
.unnamed1 {
	border-bottom-width: 1px;
	border-bottom-style: solid;
	border-bottom-color: #578FEC;
}
-->
</style>
<hrms:themes />
<html>
<body>
<html:form action="/selfservice/welcome/welcome">
<%
	if(welcomeForm.getSuccessmsg().equals(""))
	{
	}
	else
	{
	%>
	
	<script language="JavaScript">
		alert('<%=welcomeForm.getSuccessmsg()%>');
	</script>
	<%
		welcomeForm.setSuccessmsg("");
		welcomeForm.clearSuccessmsg();
	}
	%>
	<script language="JavaScript">
	welcomeForm.action="/selfservice/welcome/refresh_right.do?b_return=refresh&homePageHotId=${welcomeForm.homePageHotId}&enteryType=${welcomeForm.enteryType}&home=${welcomeForm.home}";
	welcomeForm.submit();
	</script>
</html:form>
</body>
</html>


