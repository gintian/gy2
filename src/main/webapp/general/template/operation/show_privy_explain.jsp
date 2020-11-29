<%@ page contentType="text/html; charset=UTF-8"%>

<%@ taglib uri="/tags/struts-bean" prefix="bean"%>
<%@ taglib uri="/tags/struts-html" prefix="html"%>
<%@ taglib uri="/tags/struts-logic" prefix="logic"%>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms"%>

<%
	String tabid="";
	String sp_flag="";
%>
<script type="text/javascript">
<!--
	function edit_inf()
	{
		abroadForm.action="/general/template/operation/show_privy_explain.do?b_edit=link";
		abroadForm.submit();
	}

		
//-->
</script>
<html:form action="/general/template/operation/show_privy_explain">
	<br>
	<br>
	<table width="80%" border="0" cellpadding="0" cellspacing="0" align="center" >		
		<tr class="trShallow">
		  <td align="center" valign="top" class="RecordRow">
                    <bean:write name="abroadForm" property="content" filter="false"/><br>
		 </td>
   		  			
		</tr>
		<tr class="trShallow">
		   <td align="center" style="height:35px">
		     <hrms:priv func_id="32100" module_id=""> 			   
	 		    <INPUT type="button" class="mybutton" onclick="edit_inf();" name="b_edit" value='<bean:message key="button.edit"/>'>
    		 </hrms:priv> 	 		    
	 	  </td>
   		</tr>		
	</table>
</html:form>
