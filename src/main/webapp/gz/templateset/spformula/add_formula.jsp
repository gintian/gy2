<%@ page contentType="text/html; charset=UTF-8" language="java"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean"%>
<%@ taglib uri="/tags/struts-html" prefix="html"%>
<%@ taglib uri="/tags/struts-logic" prefix="logic"%>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms"%>

<link rel="stylesheet" href="/css/css1.css" type="text/css">
<hrms:themes />
<script language="JavaScript" src="/js/constant.js"></script>
<script language="JavaScript" src="/js/validate.js"></script>
<script language="JavaScript" src="/js/function.js"></script>
<script language="JavaScript" src="/gz/salary.js"></script>
<script type="text/javascript">
<!--
function save()
{
if(gztemplateSetForm.spFormulaName.value=='')
{
  alert(PLEASE_FILLFORMULANAME+"！");
  return;
}
   gztemplateSetForm.action="/gz/templateset/spformula/sp_formula.do?b_save=save&isClose=2";
   gztemplateSetForm.submit();
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
<html:form action="/gz/templateset/spformula/sp_formula.do?b_save=save&isClose=2">
<table width="485px;" border="0" align="center" class="ListTable">

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
 <html:text property="spFormulaName" name="gztemplateSetForm" size="41" styleClass="inputtext"></html:text>
 </td>
</tr>
<tr>
 <td width="30%" align="right" valign="top" class="RecordRow" nowrap>
 <bean:message key="label.gz.shinformation"/>
 </td>
 <td width="70%"  class="RecordRow"  nowrap>
<html:textarea property="spAlert" name="gztemplateSetForm" cols="40" rows="8"></html:textarea>
 </td>
</tr>
<tr>
<td colspan="2" class="RecordRow" style="padding-top:3px;padding-bottom:3px;" align="center" nowrap>
<input type="button" name="ok" class="mybutton" onclick="save();" value="<bean:message key="button.ok"/>"/>
<input type="button" name="can" class="mybutton" onclick="closeWindow();" value="<bean:message key="button.cancel"/>"/>
</td>
</tr>
<html:hidden name="gztemplateSetForm" property="salaryid"/>
<html:hidden name="gztemplateSetForm" property="optType"/>
<html:hidden name="gztemplateSetForm" property="spFormulaId"/>
</table>
</html:form>