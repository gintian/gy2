<%@ page contentType="text/html; charset=UTF-8" language="java"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<SCRIPT LANGUAGE=javascript src="./selectper.js"></SCRIPT>
<html:form action="/train/request/selectpre">
<table border="0" cellspacing="0"  align="left" cellpadding="0"><tr><td>
<div id="namePerson">
${courseTrainForm.tablestr}
</div>
</td></tr>
</table>
<input type="hidden" name="nameid">
</html:form>