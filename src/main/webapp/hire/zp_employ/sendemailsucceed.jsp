<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<html:form action="/hire/zp_employ/send_email">
<table width="70%" border="0" cellspacing="1"  align="center" cellpadding="1">
           <tr>
            <td align="center"  nowrap>
		<bean:message key="label.common.success"/>&nbsp;&nbsp;
            </td>            	        	        	        
           </tr>  
            <tr>
            <td align="center"  nowrap>
		<hrms:submit styleClass="mybutton" property="br_return">
            		<bean:message key="button.return"/>
	 	</hrms:submit>  
            </td>            	        	        	        
           </tr>    	    
</table>
</html:form>