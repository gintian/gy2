<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<script type="text/javascript" src="/competencymodal/person_post_matching/postMatching.js"></script>
<script type="text/javascript" src="/anychart/js/AnyChart.js"></script>
<table width="100%" border="0" cellspacing="0"  align="center" cellpadding="0" class="ListTable">
<tr>
<td width="70%" valign="top" align="left" id='chart1' >
<hrms:chart name="personPostMatchingForm" title="" scope="session" xangle="45" 	 numDecimals="2"  legends="dataMap" data="" width="600" height="550" chart_type="41"   labelIsPercent="0"   chartParameter="chartParam" chartpnl="chart1">
</hrms:chart>
</td>
</tr>
</table>