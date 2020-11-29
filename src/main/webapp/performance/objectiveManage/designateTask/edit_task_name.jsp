<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean"%>
<%@ taglib uri="/tags/struts-logic" prefix="logic"%>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms"%>
<html>
<SCRIPT LANGUAGE="javascript" src="/performance/objectiveManage/designateTask/designate.js"></SCRIPT>
<SCRIPT LANGUAGE="javascript" src="/module/utils/js/template.js"></SCRIPT>
<script type="text/javascript">
<%if(request.getParameter("isclose")!=null&&request.getParameter("isclose").equals("1")){%>
	if(window.showModalDialog){
		window.returnValue="1";
	}else{
		parent.parent.designateTask_ok("1");
	}
	closeExtWin();
<%}%>
</script>
<body>
<html:form action="/performance/objectiveManage/designateTask"> <br>

<table width="300" border="0" align="center" cellpadding="0" cellspacing="0" class="ListTable">
<tr><td align="left" class="TableRow" >${designateTaskForm.itemdesc}:</td></tr>
<tr><td align="center" class="RecordRow" align="center"><input type="text" name="p0407" size="50" value="${designateTaskForm.p0407}"/></td></tr>
<tr><td align="center">
<br>
<html:hidden name="designateTaskForm" property="taskid"/>
<input type="button" name="" value="<bean:message key="button.ok"/>" onclick="saveP0407();" class="mybutton"/>
<input type="button" name="" value="<bean:message key="button.close"/>" onclick="closeExtWin();" class="mybutton"/>
</td></tr>

</table>
</html:form>
</body>
</html>