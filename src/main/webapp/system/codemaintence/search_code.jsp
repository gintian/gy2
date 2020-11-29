<%@ page contentType="text/html; charset=UTF-8" language="java"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean"%>
<%@ taglib uri="/tags/struts-html" prefix="html"%>
<%@ taglib uri="/tags/struts-logic" prefix="logic"%>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms"%>
<%@ page import="com.hrms.struts.valueobject.UserView"%>
<%@ page import="com.hrms.struts.constant.WebConstant"%>
<link rel="stylesheet" href="/css/css1.css" type="text/css">
<script language="JavaScript" src="/js/validate.js"></script>
<script language="JavaScript" src="/js/function.js"></script>

<script type="text/javascript" language="javascript">

<!--
 function onsearch(){
   var codesetid=codeMaintenceForm.seltree.value;
   var ret=new Object();
   ret.codesetid=codesetid;
   window.returnValue=ret;
   window.close();
 }
//-->
</script>
<html:form action="/system/codemaintence/search_code">
	<br>
	<table width="240" border="0" cellspacing="0" align="center" cellpadding="0">
		<tr>
			<td>
			
				<TABLE border="0" cellspacing="0" cellpadding="0">
					<tr height="20">
						<!--  <td width=10 valign="top" class="tableft"></td>
						<td width=130 align=center class="tabcenter">
							&nbsp;
							<bean:message key="button.query" />
							&nbsp;
						</td>
						<td width=10 valign="top" class="tabright"></td>
						<td valign="top" class="tabremain" width="150"></td>-->
						<td align=center class="TableRow">
							&nbsp;
							<bean:message key="button.query" />
							&nbsp;
						</td>
					</tr>
				</TABLE>
			</td>
		</tr>
		<tr>
			<td align="center" class="framestyle" colspan="8" height="100px" width="300px" nowrap>
			<br/>
				<bean:write name="codeMaintenceForm" property="selstr" filter="false" />
				<br/>
				<br/>
				
			</td>
		</tr>
		<tr>
		<td align="center"><br/>
		<html:button styleClass="mybutton" property="br_return" onclick="onsearch();">
					<bean:message key="button.query" />
				</html:button>
				<html:button styleClass="mybutton" property="br_return" onclick="window.close();">
					<bean:message key="button.close" />
				</html:button>
		</td>
		</tr>
	</table>
	<html:hidden name="codeMaintenceForm" property="cflag" />
</html:form>
