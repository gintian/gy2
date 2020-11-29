<%@ page contentType="text/html; charset=UTF-8" language="java"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<html:form action="/train/trainCosts/trainCosts">
<table border="0">
<tr>
	<td>
	<hrms:chart name="trainCostsForm" title="费用比例图" scope="session" legends="setlist" data=""  
		width="700" height="720" chart_type="5">
	</hrms:chart>
	</td>
</tr>
</table>
</html:form>