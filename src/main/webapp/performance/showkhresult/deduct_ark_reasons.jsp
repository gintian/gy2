<%@ page contentType="text/html; charset=UTF-8" language="java"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean"%>
<%@ taglib uri="/tags/struts-html" prefix="html"%>
<%@ taglib uri="/tags/struts-logic" prefix="logic"%>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms"%>
<%@ page import="java.util.ArrayList"%>
<html:form action="/performance/showkhresult/show_kh">
			<table  width="80%" border="0" cellspacing="0" align="center" cellpadding="0" class="ListTable">
<thead>
<tr>
<td class="TableRow" align="center" width="20%">
<bean:message key="kjg.title.indexname"/> 
</td>
<td class="TableRow" align="center" width="80%">
<bean:message key="lable.performance.DeductMark"/>
</td>
</tr>
</thead>
<% int i=0; %> 
<logic:iterate id="element" property="deductMarkReasonsList" name="showKhResultForm" indexId="index">
 <%if(i%2==0){ %>
 <tr class="trShallow">
  <%} else { %>
<tr class="trDeep">
<%} %>
<td class="RecordRow" align="center">
<bean:write name="element" property="pointname"/>
</td>
<td class="RecordRow" align="left">
<bean:write name="element" property="reason"/>
</td>
</tr>
<%i++; %>
</logic:iterate>
<tr>
 <td colspan="2" align="center">
 <input type="button" name="clo" class="mybutton" value="<bean:message key="button.close"/>" onclick="window.close();"/>
 </td>
</tr>
</table>	
</html:form>
