<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean"%>
<%@ taglib uri="/tags/struts-html" prefix="html"%>
<%@ taglib uri="/tags/struts-logic" prefix="logic"%>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms"%>
<%@ page import="com.hjsj.hrms.actionform.selfinfomation.SelfInfoForm" %>
<html>
	<head>

	</head>
	<hrms:themes></hrms:themes>
	<script language='javascript'>
  </script>
	<body>
	  <table width="500" border="0" cellpadding="0" cellspacing="0" align="center" class="ftable">
	          <tr height="20">
	       		<td align="left" class="TableRow">&nbsp;<bean:message key="label.information"/>&nbsp;</td>
	          </tr> 
	          <tr >
	    	    <td align="left" valign="middle" nowrap style="height:120">${ selfInfoForm.info}</td>
	          </tr> 
	  </table>
	  <table width="500" border="0" cellpadding="0" cellspacing="0" align="center">
	  	<tr>
         <td align="center" style="height:35">
 			<input type="button" name="btnreturn" value="返回" onclick="goBack();" class="mybutton">
         </td>
       </tr> 
	  </table> 
	</body>
</html>
<script language='javascript'>
	function goBack() {
		window.location.href= "/workbench/info/showinfodata.do?b_batchPOMimportbefore=link&batchImportType=${selfInfoForm.batchImportType}";
	}
</script>
