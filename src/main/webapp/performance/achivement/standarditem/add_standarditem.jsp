<%@ page contentType="text/html; charset=UTF-8" language="java"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<script type="text/javascript">
<!--
function saveItem()
{
    var ruletype=standardItemForm.ruletype.value;
    var desc=document.getElementById("desc").value;
    if(ruletype=='0')
    {
       var score=document.getElementById("itemscore").value;
       var topvalue=document.getElementById("topv").value;
       var bottomvalue=document.getElementById("bottomv").value;
       var myReg =/^(-?\d+)(\.\d+)?$/
    }
    if(desc==null||trim(desc)=='')
    {
        alert("请输入项目名称！");
        return;
    }
    if(ruletype=='0')
    {
    if(score==null||trim(score)=='')
    {
       alert("请输入标准分值！");
       return;
    }
    else
    {
     if(!myReg.test(score))
     {
         alert("标准分值请输入数字！");
         return;
     }
    }
    if(topvalue!=null&&trim(topvalue)!='')
    {
          if(!myReg.test(topvalue))
          {
              alert("上限值请输入数字！");
              return;
          }
    }
     if(bottomvalue!=null&&trim(bottomvalue)!='')
    {
          if(!myReg.test(bottomvalue))
          {
              alert("下限值请输入数字！");
              return;
          }
    }
    }
    var obj = new Object();
   
    obj.desc=desc;
    if(ruletype=='0')
    {
    obj.score=score;
    obj.topv=topvalue;
    obj.bottomv=bottomvalue;
    }
    obj.ruletype=ruletype;
    if(window.showModalDialog){
        parent.window.returnValue=obj;
    }else{
        if(parent.opener.addStandardItem_ok){
            parent.opener.addStandardItem_ok(obj);
        }
    }

    parent.window.close();
}
//-->
</script>
<html:form action="/performance/achivement/standarditem/search_standarditem_list">

<table width="100%" border="0" cellspacing="0"  align="center" cellpadding="0">
<tr>
<td>
<fieldset align="center">
<legend>
<logic:equal value="0" name="standardItemForm" property="type"><bean:message key="kh.field.addproject"/></logic:equal>
<logic:equal value="1" name="standardItemForm" property="type"><bean:message key="kh.field.editproject"/></logic:equal>
</legend>
<table width="100%" border="0" cellspacing="0"  align="center" cellpadding="0">
<tr style="line-height: 30px;">
  <td align="right" width="20%">
 <bean:message key="kh.field.projectname"/>:
  </td>
  <td align="left">
  &nbsp;<html:text styleId="desc" property="itemdesc" name="standardItemForm" size="40" styleClass="inputtext"></html:text>
  </td>
</tr>
<logic:equal value="0" name="standardItemForm" property="ruletype">

<tr style="line-height: 30px;">
<td align="right"><bean:message key="kh.field.topvalue"/>
</td>
<td align="left">
&nbsp;<html:text styleId="topv" property="top_value" name="standardItemForm" size="40" styleClass="inputtext"></html:text>
</td>
</tr>

<tr style="line-height: 30px;">
<td align="right"><bean:message key="kh.field.bottomvalue"/>
</td>
<td align="left">
&nbsp;<html:text styleId="bottomv" property="bottom_value" name="standardItemForm" size="40" styleClass="inputtext"></html:text>
</td>
</tr>

<tr style="line-height: 30px;">
<td align="right"><bean:message key="kh.field.standardvalue"/>
</td>
<td align="left">
&nbsp;<html:text styleId="itemscore" property="score" name="standardItemForm" size="40" styleClass="inputtext"></html:text>
</td>
</tr>

</logic:equal>

<tr>
<td colspan="2">&nbsp;&nbsp;
<html:hidden name="standardItemForm" property="ruletype"/>
</td>
</tr>
</table>
</fieldset>
</td>
</tr>
</table>
<table width="100%">
	<tr>
 <td  align="center">
  <input type="button" name="save" class="mybutton" value="<bean:message key="button.ok"/>" onclick="saveItem();"/>
  <input type="button" name="clos" class="mybutton" value="<bean:message key="button.cancel"/>" onclick="parent.window.close();"/>
 </td>
</tr>
</table>
</html:form>