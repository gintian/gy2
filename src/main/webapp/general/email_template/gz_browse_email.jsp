<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<script type="text/javascript">
<!--
function returnBack()
{ 
   gzEmailForm.action="/general/email_template/gz_send_email.do?b_init=init3";
   gzEmailForm.submit();
}
//-->
</script>
<html:form action="/general/email_template/gz_browse_email">
<br>
<table width='80%' border="0" cellspacing="1"  align="left" cellpadding="1">
<tr>
<td width="30%" align="right" nowrap>
当前人员:
</td>
<td align="left" width="70%" nowrap>
<STRONG><bean:write name="gzEmailForm" property="a0101"/></STRONG>
</td>
</tr>


<tr>
<td width="30%" align="right" nowrap>
发&nbsp;送&nbsp;到:
</td>
<td align="left" width="70%" nowrap>
<html:text name="gzEmailForm" property="address" size="100" disabled="true"></html:text>
</td>
</tr>
<tr>
<td width="30%" align="right" nowrap>
主&nbsp;&nbsp;&nbsp;&nbsp;题:
</td>
<td align="left" width="70%" nowrap>
<html:text name="gzEmailForm" property="subject" size="100" disabled="true"></html:text>
</td>
</tr>
<tr>
<td width="30%" align="right" valign="top" height="200">
主&nbsp;&nbsp;&nbsp;&nbsp;体:
</td>

<td width="70%" align="left">

<html:textarea name="gzEmailForm" property="content" cols="100" rows="30" readonly="true"/>
</td>
</tr>
<logic:notEqual value="0" property="attachSize" name="gzEmailForm">
<tr>
<td width="30%" align="right" valign="top" nowrap>
附&nbsp;&nbsp;&nbsp;&nbsp;件:
</td>
<td width="70%" align="left" nowrap>
<table width='100%' border="0" cellspacing="1"  align="left" cellpadding="1" class="ListTable">
<% int i=0;%>
<logic:iterate name="gzEmailForm" id="element" property="attachlist" offset="0">
<tr>
<td align="center" class="RecordRow" nowrap>
<%=i+1%>
</td>
<td class="RecordRow" align="left" nowrap>
&nbsp;<bean:write name="element" property="filename"/>&nbsp;
</td>
</tr>
<% i++;%>
</logic:iterate>
</table>
</td>
</tr>
</logic:notEqual>
<tr>
<td align="center" colspan="2" nowrap>
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<input type="button" class="mybutton" name="ret" value="返回" onclick="returnBack();" align="center"/>
</td>
</tr>
</table>
</html:form>