<%@ page contentType="text/html; charset=UTF-8"%>
<%@page import="com.hjsj.hrms.utils.PubFunc"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%
String frompath = request.getParameter("frompath");
frompath = PubFunc.keyWord_reback(frompath);
%>
<script type="text/javascript">
//<!--
function reback(){
	window.location.href="<%=frompath %>";
}
//-->
</script>
<table width="70%" border="0" cellspacing="1"  align="center" cellpadding="1">
           <tr>
            <td align="center"  nowrap>
		<bean:message key="label.common.success"/>&nbsp;&nbsp;
            </td>            	        	        	        
           </tr>  
            <tr>
            <td align="center"  nowrap>
         <%if(frompath!=null){ %>
            <hrms:submit styleClass="mybutton" property="b_other" onclick="reback()">
                    <bean:message key="button.return"/>
        </hrms:submit> 
         <%}else{ %>
		<hrms:submit styleClass="mybutton" property="b_other" onclick="history.back()">
            		<bean:message key="button.return"/>
	 	</hrms:submit>  
	 	<%} %>
            </td>            	        	        	        
           </tr>    	    
</table>


