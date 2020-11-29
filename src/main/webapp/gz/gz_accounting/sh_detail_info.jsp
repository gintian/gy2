<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms"%>
<script type="text/javascript">
<!--
function outExcel(filename)
{
if(filename&&filename.length>0)
{
  var fieldName = getDecodeStr(filename);
  var win=open("/servlet/vfsservlet?fileid="+fieldName+"&fromjavafolder=true","excel");
 }
}
//-->
</script>
<html:form action="/gz/gz_accounting/sh_formula">
<table width="95%"  border="0" align="center" class="ListTable">
<tr>
<td width="100%" align="center"><strong><bean:message key="label.gz.shresult"/></strong></td>
</tr>
<tr>
<td align="center" width="100%" valign="bottom">
<div style="align:left;border:0px solid #eee;height:365px;width:380px;overflow:auto;margin:1em 1;">
<table width="100%" border="0" class="ListTable">
<% int j=1; %>
<logic:iterate id="element" name="accountingForm" property="shPersonList" indexId="index" offset="0">
<tr>
<td colspan="4" align="left" class="RecordRow" bgcolor="#00ffff"><%=j%>：<bean:write name="element" property="name"/><br><bean:message key="workdiary.message.message"/>：<bean:write name="element" property="information"/></td>
</tr>
<tr>
<td class="TableRow" align="center" ><bean:message key="recidx.label"/></td><td class="TableRow"  align="center" >
<bean:message key="hire.employActualize.name"/></td><td class="TableRow"  align="center" ><bean:message key="lable.hiremanage.org_id"/>
</td><td class="TableRow"  align="center" ><bean:message key="lable.hiremanage.dept_id"/></td>
</tr>
<%int i=1; %>
<logic:iterate id="data" name="element" property="subList">
<tr>
<td align="center" class="RecordRow"><%=i%></td>
<td align="center" class="RecordRow"><bean:write name="data" property="a0101"/></td>
<td align="center" class="RecordRow"><bean:write name="data" property="b0110"/></td>
<td align="center" class="RecordRow"><bean:write name="data" property="e0122"/></td>
</tr>
<%i++; %>
</logic:iterate>
<% j++; %>
</logic:iterate>
</table>
</div>
</td>
</tr>
<tr>
<td valign="bottom" align="center">
<input type="button" class="mybutton" value=" <bean:message key="goabroad.collect.educe.excel"/>" name="ex" onclick="outExcel('${accountingForm.fileName}');"/>
&nbsp;
<input type="button" class="mybutton" value="   <bean:message key="button.cancel"/>   " name="can" onclick="window.close();"/>
</td>
</tr>
</table>
</html:form>