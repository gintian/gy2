<%@ page contentType="text/html; charset=UTF-8" language="java"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<script type="text/javascript" src="/general/tipwizard/returnT.js"></script>
<html>
<body>
<html:form action="/gz/gz_analyse/gzAnalyseChart" >
<table width="100%" align="center" border="0" cellpadding="0" cellspacing="0">
<tr>
<td align="center">
<br>
<br>
<br>
<%
if(request.getParameter("what")!=null&&request.getParameter("what").equals("tabid"))
{
 %>
 <p>
   <bean:message key="no.chart.foryou"/>
 </p>
 <% }%>
 <%
if(request.getParameter("what")!=null&&request.getParameter("what").equals("pre"))
{
 %>
 <p>
   <bean:message key="no.pre.foryou"/>
 </p>
 <% }%>
</td>
</tr>
<tr>
<td align="left">
<hrms:tipwizardbutton flag="compensation" target="il_body" formname="sysForm"/> 
</td>
</tr>
</table>
</html:form>
</body>
</html>