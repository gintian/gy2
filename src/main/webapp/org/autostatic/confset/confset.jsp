<%@ page contentType="text/html; charset=UTF-8" language="java"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>

<hrms:themes></hrms:themes>
<script language="JavaScript" src="/js/validate.js"></script>
<script language="JavaScript" src="/js/function.js"></script>

<body >
<table width="500" height="360" border="0" align="center">
      	<tr><td height="80">&nbsp;</td></tr>
      	<tr>
      		<td>
      			<fieldset align="center" style="width:60%;">
      			<legend><bean:message key='org.autostatic.mainp.system.settings'/></legend>
      			<table width="100%" height="140" align="center" border="0">
      				<tr>
      					<td>
							<a href="/org/autostatic/confset/subsetconfset.do?b_query=link&init=first"><img src="/images/pos.gif" border="0">
							<bean:message key='org.autostatic.mainp.set.change.item'/></a>
						</td>
						<td>
							<a href="/org/autostatic/mainp/project.do?b_query=link&flag=0"><img src="/images/public_info.gif" border="0">
							<bean:message key='org.autostatic.confset.datascan.setproject'/></a>
						</td>
      				</tr>
      				<tr>
      					<td>&nbsp;</td>
						<td>&nbsp;</td>
      				</tr>
      			</table>
      			</fieldset>
      		</td>
      	</tr>
      	<tr>
      		<td height="80" align="center">
      			<button name="return" class="mybutton" onclick="backSet();" style="width:45" style="font-size:10pt">
      			<bean:message key='reportcheck.return'/></button>
    		</td>
    	</tr>
</table>
<script language="javascript"> 
function backSet(){
	document.location.href="/org/autostatic/confset/datasynchro.do?b_init=link";
}	
</script>
</body>


    