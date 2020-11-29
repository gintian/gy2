<%@ page contentType="text/html; charset=UTF-8" language="java"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<script type="text/javascript">
<!--
function sub()
{
  var allt=document.getElementsByName("sel");
  if(allt==null||allt.length==0)
  {
     alert("请选择登记表!");
     return;
  }
  var id="";
  for(var i=0;i<allt.length;i++)
  {
    if(allt[i].checked)
    {
       id=allt[i].value;
       break;
    }
  }
  if(id=="")
  {
     alert("请选择登记表!");
     return;
  }
  window.returnValue=id;
  window.close();
}
function winclo()
{
  window.retrunValue=null;
  window.close();
}
//-->
</script>
<html:form action="/performance/interview/search_interview_list">
<table width="90%" border="0" cellspacing="0"  align="center" cellpadding="0">
<tr>
<td align='center'>
<fieldset>
<legend>选择打印登记表</legend>
 <div style="overflow:auto;width:470px;height:250px;" >
 <table width="100%" border="0" cellspacing="0"  align="center" cellpadding="0" class="ListTable">
 <thead>
 <tr>
 <td class="TableRow" align='center'>
 选择
 </td>
 <td class="TableRow" align='center'>
 登记表
 </td>
 </tr>
 </thead>
 <logic:iterate id="element" name="performanceInterviewForm" property="tabList" offset="0">
 <tr>
 <td align="center" class="RecordRow">
 <input type="radio" name="sel" value="<bean:write name="element" property="id"/>"/>
 </td>
 <td align="left" class="RecordRow">
 <bean:write name="element" property="name"/>
 </td>
 </tr>
 </logic:iterate>
 </table>
 </div>
</fieldset>
</td>
</tr>
 <tr>
 <td>
 <input type="button" class="mybutton" name="o" value="<bean:message key="button.ok"/>" onclick="sub();"/>
 &nbsp;&nbsp;
  <input type="button" class="mybutton" name="c" value="<bean:message key="button.cancel"/>" onclick="winclo();"/>
 </td>
 </tr>
</table>
</html:form>