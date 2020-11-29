<%@ page contentType="text/html; charset=UTF-8" language="java"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ page import="com.hrms.struts.valueobject.UserView"%>
<%@ page import="com.hrms.struts.constant.WebConstant"%>
<script type="text/javascript">
<!--
function sub()
{
   objecttype=$F('roleid');
   if(objecttype=="#")
	 {
	   alert("请选择报送对象!");
	   return;
	 }
	 var obj = new Object();
	 obj.objecttype=objecttype;
	 var title=$('user_').value;
	 var content=$('user_h').value;
	 if(title==''||content=='')
	 {
	   alert("请选择报送对象!");
	   return;
	 }
	 obj.title=title;
	 obj.content=content;
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
     	 var return_vo=select_org_emp_byname_dialog(1,2,2,0);   
		 if(return_vo)
		 {
	 		$('user_').value=return_vo.title;
	 		$('user_h').value=return_vo.content;
	 	 }else{
	 		 document.all.roleid.options[0].selected=true;///不选择报送对象时默认选中第一项，使onchange事件明晰
	 	}	
	 }
	 else if(objecttype=="4")
	 {
	 	var return_vo=select_user_dialog('1','2');
	 	if(return_vo)
	 	{
	 		$('user_').value=return_vo.title;
	 		$('user_h').value=return_vo.content;	 		
	 	}else{
	 		 document.all.roleid.options[0].selected=true;///不选择报送对象时默认选中第一项，使onchange事件明晰
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
<%
    UserView userView = (UserView) session.getAttribute(WebConstant.userView);
    String bosflag= userView.getBosflag();//得到系统的版本号
    if(bosflag!=null&&bosflag.equalsIgnoreCase("hcm")){
%>
    <link href="/hire/css/layout.css" rel="stylesheet" type="text/css">
<%
}
%>
<table align="center" width="400px" class="bpTableCon">
<tr>
<td align="center">
<fieldset width="99%">
<legend>选择报送对象</legend>
<table align="center">
<tr>
<td >&nbsp;&nbsp;</td>
<td >&nbsp;&nbsp;</td>
</tr>
<tr>
<td >&nbsp;&nbsp;</td>
 <td width="100%" align="left">
<bean:message key="rsbd.task.selectobject"/>
            				 <html:select name="positionDemandForm" property="roleid" size="1" onchange="selectobject();">
               					<option value="#" selected="selected"><bean:message key="label.select" /></option>
                                <html:optionsCollection property="rolelist" value="codeitem" label="codename"/>
				        	 </html:select>
				<br>
				<br>
				<INPUT type="text" id="user_" value="" class="TEXT9" size="40" maxlength="200">
                <INPUT type="hidden" id="user_h" value=""  size=30>	
                <INPUT type="hidden" id="type" value=""  size=30>	
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
<td align="center" height="35px;">
<input type="button" class="mybutton" name="o" value="<bean:message key="button.ok"/>" onclick="sub();"/>
&nbsp;&nbsp;
<input type="button" class="mybutton" name="c" value="<bean:message key="button.close"/>" onclick="winClose();"/>

</td>
</tr>
</table>
</html:form>