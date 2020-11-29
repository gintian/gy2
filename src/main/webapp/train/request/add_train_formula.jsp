<%@ page contentType="text/html; charset=UTF-8" language="java"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean"%>
<%@ taglib uri="/tags/struts-html" prefix="html"%>
<%@ taglib uri="/tags/struts-logic" prefix="logic"%>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms"%>

<link rel="stylesheet" href="/css/css1.css" type="text/css">
<script language="JavaScript" src="/js/constant.js"></script>
<script language="JavaScript" src="/js/validate.js"></script>
<script language="JavaScript" src="/js/function.js"></script>
<script language="JavaScript" src="/gz/salary.js"></script>
<script type="text/javascript">
<!--
function save()
{
if(trainFormulaForm.trainFormulaName.value=='')
{
  alert(PLEASE_FILLFORMULANAME+"！");
  return;
}
trainFormulaForm.action="/train/request/trainsData/check.do?b_save=link&isClose=2";
trainFormulaForm.submit();
}
<%if(request.getParameter("isClose").equals("2"))
{
   %>
   var obj=new Object();
   obj.refresh="2";
   returnValue=obj;
   window.close();
   <%
}
%>
function closeWindow()
{
var obj=new Object();
   obj.refresh="1";
   returnValue=obj;
   window.close();
}
//-->
</script>
<hrms:themes/>
<html:form action="/train/request/trainsData/check.do?b_save=link">
<table width="490px" border="0" align="center" class="ListTable">
<thead>
<tr>
<td colspan="2" class="TableRow" align="left" nowrap>
<bean:message key="label.gz.shformula"/>
</td>
</tr>
</thead>
<tr>
 <td width="30%" align="right" class="RecordRow" nowrap>
<bean:message key="workdiary.message.formula.name"/>
 </td>
 <td width="70%"  class="RecordRow"  nowrap>
 <input type="text" name="trainFormulaName" class="text4" value="<bean:write name="trainFormulaForm" property="trainFormulaName"/>" size="41"/>
 </td>
</tr>
<tr>
 <td width="30%" align="right" valign="top" class="RecordRow" nowrap>
 <bean:message key="label.gz.shinformation"/>
 </td>
 <td width="70%"  class="RecordRow"  nowrap>
<textarea name="trainAlert" rows="8" cols="40"><bean:write name="trainFormulaForm" property="trainAlert"/></textarea>
 </td>
</tr>
<tr>
<td colspan="2" class="RecordRow" style="padding-top:3px;padding-bottom:3px;" align="center" nowrap>
<input type="button" name="ok" class="mybutton" onclick="save();" value="<bean:message key="button.ok"/>"/>
&nbsp;
<input type="button" name="can" class="mybutton" onclick="closeWindow();" value="<bean:message key="button.cancel"/>"/>
</td>
</tr>
<html:hidden name="trainFormulaForm" property="optType"/>
<html:hidden name="trainFormulaForm" property="trainFormulaId"/>
</table>
</html:form>