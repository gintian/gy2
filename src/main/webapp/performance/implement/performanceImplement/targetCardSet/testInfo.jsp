<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ page import="com.hjsj.hrms.actionform.performance.implement.ImplementForm" %>

<html>
  <head>
  <title><bean:message key="jx.implement.target_card_set.testResult"/></title>
   <script type="text/javascript">
       function closeWin() {
           if (window.showModalDialog) {
               window.close();
           } else {
               parent.parent.Ext.getCmp("targetCardCheck").close();
           }
       }
   </script>
  </head>  
<hrms:themes />
  <html:form action="/performance/implement/performanceImplement/targetCardSet">
		<table width="98%" border="0" align="center">
			<tr>
				<td align="left">
					   <bean:message key="jx.implement.target_card_set.testResult"/>
				</td>
			</tr>	
			<tr>
				<td align="left">
					<html:textarea name="implementForm" property="targetCardTestStr" rows="18" cols="75"></html:textarea>
				</td>
			</tr>
				<tr>
				<td align="center" style="margin-top:5px">
					<input type="button" class="myButton" value='<bean:message key="button.close"/>' onclick="closeWin();"/>
				</td>
			</tr>			
		</table>  
  </html:form>
</html>