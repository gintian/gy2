<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<html>
<html>
  <head>
  </head>
  
  <body>
  <html:form action="/performance/showkhresult/showDirectionAnalyse">
  <table align='center'>
  <tr><td align='center' >  
  	<hrms:chart name="directionAnalyseForm" title="" isneedsum="false" scope="session" legends="dataMap" data=""  width="650" height="400"  chart_type="11" >
	</hrms:chart>
  </td></tr>
  </table>
  </html:form>
  </body>
</html>
