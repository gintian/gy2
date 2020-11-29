<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean"%>
<%@ taglib uri="/tags/struts-html" prefix="html"%>
<%@ taglib uri="/tags/struts-logic" prefix="logic"%>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms"%>
<%@ page import="com.hjsj.hrms.actionform.gz.premium.premium_allocate.*"%>
<%
	MonthPremiumForm form=(MonthPremiumForm)session.getAttribute("monthPremiumForm");
	String khTableUrl = (String)form.getParamStr();	
%>
<html:form action="/gz/premium/premium_allocate/monthPremiumList">
	<table width="100%" border="0" cellspacing="0" cellpadding="0" align="center" height="100%">
		<tr><td height='2%'>&nbsp;</td></tr>
		<tr>
			<td>
				<hrms:tabset name="cardset" width="98%" height="99%" type="true"> 		
		  			<hrms:tab name="aa1" label="performance.kh.table" visible="true" url="<%=khTableUrl %>" >
	      			</hrms:tab>	
	      			<hrms:tab name="aa2" label="label.premium.distribute" visible="true" url="/gz/premium/premium_allocate/monthPremiumList.do?br_premium=link" >
	      			</hrms:tab>	
	   		   </hrms:tabset>
			</td>
		</tr>
	</table>
</html:form>
