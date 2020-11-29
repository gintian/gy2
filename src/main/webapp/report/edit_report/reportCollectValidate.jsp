<%@ page contentType="text/html; charset=UTF-8"%>

<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>

	<SCRIPT LANGUAGE=javascript src="/js/validate.js"></SCRIPT>     
	<SCRIPT LANGUAGE=javascript src="/js/function.js"></SCRIPT> 
	<SCRIPT LANGUAGE=javascript>

	var n=-1;
	var info=parent.info;
	
	
	function closeWindow()
	{
		 var valWin = parent.Ext.getCmp('collectValidate');
			if(valWin)
				valWin.close();
			else
				window.close();	
	
	}
	
	
	</script>
<HEAD>
<link href="/css/css1.css" rel="stylesheet" type="text/css">
<hrms:themes></hrms:themes>
</HEAD>

<body bgcolor="#F7FAFF"   >
<form name='f1'>
		<table  width="100%"  height="100%" border="0" cellpmoding="0" cellspacing="0"  class="DetailTable"  cellpadding="0">   
 
		        <tr>  
		         <td width="95%" height="100%" align='center' >
						&nbsp;&nbsp;<TEXTAREA   name='area' rows='12' cols='57' >
						</TEXTAREA>
						<br>
						<INPUT type='button' value=' <bean:message key="button.cancel"/> ' class='mybutton' onclick='closeWindow()' style="margin-top: 2px;" >
		         </td>
		         </tr>

		 </table>


</form>
<script language='javascript'>
	f1.area.value=info;

</script>
</body>
