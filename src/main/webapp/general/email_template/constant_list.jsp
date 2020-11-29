<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<script language="Javascript" src="/gz/salary.js"></script>
<script type="text/javascript">
<!--
function closeWindow()
{
   parent.parent.window.getConstant_ok(null);
}
function selectOK()
{
  var ids='';
  var obj=document.getElementById("cid");
  for(var i=0;i<obj.options.length;i++)
  {
    if(obj.options[i].selected)
    {
       ids=obj.options[i].text;
       break;
    }
  }
  if(ids=='')
  {
    alert("请选择常量！");
    return;
  }
    var objvalue=new Object();
    objvalue.txt=ids;
    parent.parent.window.getConstant_ok(objvalue);
//  returnValue=objvalue;
//  window.close();
}
//-->
</script>
<html:form action="/general/email_template/constant_list">
<table width='290' border="0" cellspacing="0"  align="center" cellpadding="0">

<tr>
<td align="center" colspan="2" nowrap>
  <html:select name="gzEmailForm" size="15" styleId="cid" property="constantID"  ondblclick="selectOK();" style="height:200px;width:280px;font-size:9pt">
		              <html:optionsCollection property="constantList" value="dataValue" label="dataName"/>
		        </html:select>	
</td>
</tr>
</table>
<table width="100%">
	<TR>
	<td align="center" colspan="2" nowrap height="35px;">
	<input type="button" name="ok" value="<bean:message key="button.ok"/>" class="mybutton" onclick="selectOK();"/>
	<input type="button" name="clos" value="<bean:message key="button.cancel"/>" class="mybutton" onclick="closeWindow();"/>
	</td>
	</TR>
</table>
</html:form>