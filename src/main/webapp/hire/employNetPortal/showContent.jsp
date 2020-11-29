 <%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<html>
<head>

</head>
<body>
<html:form action="/hire/employNetPortal/search_zp_position"> 
<table width='90%'  align='center'>
<tr><td align='center' >
	
		${employPortalForm.info}
	
</td></tr>
</table>
</html:form>	
</body>

</html>