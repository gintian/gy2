<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<link href="../../../../css/css1.css" rel="stylesheet" type="text/css">
<logic:notEqual name="muchFieldAnalyseForm" property="analyseType" value="1">
<logic:notEqual name="muchFieldAnalyseForm" property="chartFlag" value="no">
	<table align="center" width="100%" height="100%">
	<tr>
		<td align="center" nowrap colspan="5">
			<hrms:chart name="muchFieldAnalyseForm" title="${muchFieldAnalyseForm.chartTitle}" 
			scope="session" legends="chartMap" data="" width="800" height="400" chart_type="${muchFieldAnalyseForm.chartType}"
			chartParameter="chartParameter" isneedsum="false">
			</hrms:chart>
		</td>
	</tr>
	</table>
</logic:notEqual>
</logic:notEqual>

<logic:notEqual name="muchFieldAnalyseForm" property="analyseType" value="2">
<logic:notEqual name="muchFieldAnalyseForm" property="chartFlag" value="no">
	<table align="center" width="100%" height="100%">
	<tr>
		<td align="center" nowrap colspan="5">
			<hrms:chart name="muchFieldAnalyseForm" title="${muchFieldAnalyseForm.chartTitle}" 
			scope="session" legends="chartMap" data="" width="800" height="400" chart_type="${muchFieldAnalyseForm.chartType}"
			chartParameter="chartParameter" isneedsum="false">
			</hrms:chart>
		</td>
	</tr>
	</table>
</logic:notEqual>
</logic:notEqual>


