<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@page import="java.util.*,org.apache.commons.beanutils.LazyDynaBean" %>
<%
	int i=0;
	System.out.println("functionMainWelcome.jsp");
%>

<html:form action="/system/bos/func/functionMain">
<br>
<table width="70%" border="0" cellspacing="0"  align="center" cellpadding="0" class="ListTable">
<tr><td>
	<button extra="button" id="clo1" onclick="newTarget()"  allowPushDown="false" down="false"><bean:message key="kq.emp.button.add"/></button>
    
</td>
</tr>
</table>
 
</html:form>


