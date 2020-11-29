<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>

<SCRIPT LANGUAGE=javascript src="/js/xtree.js"></SCRIPT>
<SCRIPT LANGUAGE=javascript src="/js/validate.js"></SCRIPT>
<%@ page import="java.util.*,
				com.hjsj.hrms.actionform.pos.PosRoleInfoForm" %>
<script language="javascript">
function showword()
{
	posRoleInfoForm.action = "/pos/roleinfo/pos_priv_post?usertable=k00&usernumber=${posRoleInfoForm.usernumber}&i9999=${posRoleInfoForm.i9999}";
	posRoleInfoForm.submit();
}
function showtaskword()
{
	posRoleInfoForm.action = "/pos/roleinfo/pos_priv_post?usertable=${posRoleInfoForm.usertable}&usernumber=${posRoleInfoForm.usernumber}&i9999=${posRoleInfoForm.i9999}";
	posRoleInfoForm.submit();
}
</script>
<html:form action="/pos/roleinfo/pos_priv_info">
<br>
<br>
<br>
<logic:equal name="posRoleInfoForm" property="usertable" value="k00">
	<logic:equal name="posRoleInfoForm" property="i9999" value="no">
		<h1 align="center">您没有相关职位说明书</h1>
	</logic:equal>
</logic:equal>
<logic:notEqual name="posRoleInfoForm" property="usertable" value="k00">
	<logic:equal name="posRoleInfoForm" property="i9999" value="no">
		<h1 align="center">您没有相关任务书</h1>
	</logic:equal>
</logic:notEqual>
</html:form>
<script language="javascript">
<%
	PosRoleInfoForm pri = (PosRoleInfoForm)session.getAttribute("posRoleInfoForm");
	if(pri.getI9999()!=null&&!pri.getI9999().equalsIgnoreCase("no")&&pri.getUsertable().equalsIgnoreCase("k00")){
%>
showword();
<%
	}else if(pri.getI9999()!=null&&!pri.getI9999().equalsIgnoreCase("no")&&!pri.getUsertable().equalsIgnoreCase("k00")){
%>
showtaskword();
<%
	}
%>
</script>