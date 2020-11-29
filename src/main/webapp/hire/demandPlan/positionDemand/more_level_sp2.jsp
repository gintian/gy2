<%@ page contentType="text/html; charset=UTF-8" language="java"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<script type="text/javascript">
<!--
function sub()
{
   objecttype=$F('roleid');
   if(objecttype=="#")
	 {
	   alert("请选择派单对象!");
	   return;
	 }
	 var obj = new Object();
	 obj.objecttype=objecttype;
	 var title=$('user_').value;
	 var content=$('user_h').value;
	 if(title==''||content=='')
	 {
	   alert("请选择派单对象!");
	   return;
	 }
	 var assignType = $('assignType').value;
	 obj.title=title;
	 obj.content=content;
	 obj.assignObjTye = assignType;
	 returnValue=obj;
	 window.close();
}
function selectobject()
	{
	 var objecttype=$F('roleid');
	 if(objecttype=="#")
	 {
	   $('user_').value='';
	   $('user_h').value='';
	   return;
	 }
	 var flag=0;
	  if(objecttype=="1") 
	 {
	   flag=1;
	 }
	 if(objecttype=="1")
	 {
     	 var return_vo=select_org_emp_dialog(1,2,0,0);   
		 if(return_vo)
		 {
	 		$('user_').value=return_vo.title;
	 		$('user_h').value=return_vo.content;
	 	}	
	 }
	 else if(objecttype=="4")
	 {
	 	var return_vo=select_user_dialog('1','2');
	 	if(return_vo)
	 	{
	 		$('user_').value=return_vo.title;
	 		$('user_h').value=return_vo.content;	 		
	 	}
	 }
	 $('type').value=objecttype;
	}
	function winClose()
	{
	  returnValue=null;
	  window.close();
	  
	}
//-->
</script>
<html:form action="/hire/demandPlan/positionDemand/positionDemandTree">
<table align="center">
<tr>
<td align="center">
<fieldset>
<legend>选择派单对象</legend>
<table align="center" border="0">
<tr>
<td >&nbsp;&nbsp;</td>
<td >&nbsp;&nbsp;</td>
<td >&nbsp;&nbsp;</td>
</tr>
<tr>
<td >&nbsp;&nbsp;</td>
<td align="right">人员类别</td>
 <td  align="left">

            				 <html:select name="positionDemandForm" property="roleid" size="1" onchange="selectobject();">
               					<option value="#" selected="selected"><bean:message key="label.select" /></option>
                                <html:optionsCollection property="rolelist" value="codeitem" label="codename"/>
				        	 </html:select>
				<INPUT type="text" id="user_" value="" class="TEXT9" size="20" maxlength="200">
                <INPUT type="hidden" id="user_h" value=""  size=30>	
                <INPUT type="hidden" id="type" value=""  size=30>	
</td>
</tr>
<tr>
<td >&nbsp;&nbsp;</td>
<td align="right">派给</td>
<td  align="left">

            				 <html:select name="positionDemandForm" property="assignType" size="1">
                              <html:optionsCollection property="assignTypeList" value="dataValue"
									label="dataName" />
				        	 </html:select>
</td>
</tr>
<tr>
<td >&nbsp;&nbsp;</td>
<td >&nbsp;&nbsp;</td>
</tr>
<tr>
<td >&nbsp;&nbsp;</td>
<td >&nbsp;&nbsp;</td>
</tr>
</table>
</fieldset>
</td>
</tr>
<tr>
<td align="center">
<input type="button" class="mybutton" name="o" value="<bean:message key="button.ok"/>" onclick="sub();"/>
&nbsp;&nbsp;
<input type="button" class="mybutton" name="c" value="<bean:message key="button.close"/>" onclick="winClose();"/>

</td>
</tr>
</table>
</html:form>