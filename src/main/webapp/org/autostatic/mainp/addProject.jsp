<%@ page contentType="text/html; charset=UTF-8" language="java"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean"%>
<%@ taglib uri="/tags/struts-html" prefix="html"%>
<%@ taglib uri="/tags/struts-logic" prefix="logic"%>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms"%>
<hrms:themes></hrms:themes>
<script language="JavaScript" src="/js/validate.js"></script>
<script language="JavaScript" src="/js/function.js"></script>
<script language="JavaScript" src="/js/popcalendar3.js"></script>
<script language='javascript'>
function viewSubmit(){
	var fielditemid = document.getElementById("fieldname").value;
	if(fielditemid==''){
		alert("<bean:message key='org.autostatic.mainp.select.project'/>");
		return;
	}
	window.returnValue=fielditemid;
	window.close();
}
function selectProject(obj){
	var targetsetid=obj.value;
    if(targetsetid==null||targetsetid.length<1){
		return;
	}
	var in_paramters="fieldname=4-"+targetsetid;
   	var request=new Request({method:'post',asynchronous:false,parameters:in_paramters,onSuccess:showFieldList,functionId:'1602010219'});
}
function showFieldList(outparamters){
	var usedlist=outparamters.getValue("usedlist");
	AjaxBind.bind(projectForm.targetitemid,usedlist);
}
function targetOk(){
	var fielditemid = document.getElementById("fieldname").value;
	var targetsetid = document.getElementById("targetsetid").value;
	var targetitemid = document.getElementById("targetitemid").value;
	if(targetitemid==null||targetitemid.length<1){
		alert("请设置目标指标!");
		return ;
	}
	var targetsetdesc = document.getElementById("targetsetid").options[window.document.getElementById("targetsetid").selectedIndex].text;

	var targetitemdesc = document.getElementById("targetitemid").options[window.document.getElementById("targetitemid").selectedIndex].text;

	window.returnValue=fielditemid+"::"+targetsetid+"::"+targetsetdesc+"::"+targetitemid+"::"+targetitemdesc;
	window.close();
}
</script>

<base id="mybase" target="_self">
<html:form action="/org/autostatic/mainp/addProject">
<table width="100%" border="0" align="center">
  <tr> 
    <td width="10%" height="149">&nbsp;</td>
    <td width="80%">
    <fieldset align="center" style="width:100%;">
      <legend><bean:message key="org.maip.add.project"/></legend>
    	<table width="100%" height="79" border="0">
        <tr> 
          <td width="30%" align="right" height="37"><bean:message key="conlumn.investigate_item.name"/></td>
          <td width="70%">
			<html:select name="projectForm" property="fieldname" style="width:160">
			 	<html:optionsCollection property="usedlist" value="dataValue" label="dataName" />
			</html:select>
		  </td>
        </tr>
        <logic:equal name="projectForm" property="checkfalg" value="1">
        <tr> 
          <td width="30%" align="right" height="37">目标子集</td>
          <td width="70%">
			<html:select name="projectForm" property="targetsetid" onchange="selectProject(this);" style="width:160;">
			 	<html:optionsCollection property="targetsetlist" value="dataValue" label="dataName" />
			</html:select>
		  </td>
        </tr>
        <tr> 
          <td width="30%" align="right" height="37">目标指标</td>
          <td width="70%">
			<select name="targetitemid" style="width:160;">
             </select>
		  </td>
        </tr>
        <tr> 
          <td width="30%"><input type="hidden" name="type" value="${projectForm.type}"></td>
        </tr>
        </logic:equal>
         <logic:notEqual name="projectForm" property="checkfalg" value="1">
        <tr> 
          <td width="30%"><input type="hidden" name="type" value="${projectForm.type}"></td>
        </tr>
        </logic:notEqual>
      </table>
      </fieldset></td>
    <td width="10%">&nbsp;</td>
  </tr>
</table>

<logic:equal name="projectForm" property="checkfalg" value="1">
<center>
<input name="newproject" type="button" id="newproject2" value='<bean:message key="button.ok"/>' onclick="targetOk();" Class="mybutton">
<input name="closeproject" type="button" id="newproject2" value='<bean:message key="button.close"/>' onclick="window.close()" Class="mybutton">
</center>
</logic:equal>
<logic:notEqual name="projectForm" property="checkfalg" value="1">
<center>
<input name="newproject" type="button" id="newproject2" value='<bean:message key="button.ok"/>' onclick="viewSubmit();" Class="mybutton">
<input name="closeproject" type="button" id="newproject2" value='<bean:message key="button.close"/>' onclick="window.close()" Class="mybutton">
</center>
</logic:notEqual>
</html:form>


