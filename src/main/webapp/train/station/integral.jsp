<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean"%>
<%@ taglib uri="/tags/struts-html" prefix="html"%>
<%@ taglib uri="/tags/struts-logic" prefix="logic"%>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms"%>
<script language="javascript">
function searchItem()
 {
    var hashvo=new ParameterSet(); 
    hashvo.setValue("flag","9"); 
 	var pars="emp_setid="+trainStationForm.emp_setid.value;
 	
   	var request=new Request({method:'post',asynchronous:false,parameters:pars,onSuccess:showSelectList,functionId:'2020020444'},hashvo);
 }
  
 function showSelectList(outparamters)
 {
 	var post_list=outparamters.getValue("post_list");
	AjaxBind.bind(trainStationForm.post_setid,post_list);
	AjaxBind.bind(trainStationForm.post_setxid,post_list);
	var fielditem_vo=eval("document.trainStationForm.post_setid");
	
 }
 
function saveset(){
	var hashvo=new ParameterSet(); 

	hashvo.setValue("emp_setid",document.getElementById("emp_setid").value);
	hashvo.setValue("post_setid",document.getElementById("post_setid").value);
	hashvo.setValue("post_setxid",document.getElementById("post_setxid").value);
	hashvo.setValue("reg_setid",document.getElementById("reg_setid").value);

   	var request=new Request({method:'post',onSuccess:saveOk,functionId:'2020020333'},hashvo);
}
function saveOk(outparamters){

var mess=outparamters.getValue("mess");
if(mess=="nook"){
	alert("可用已用积分指标不能重复!");
	}else if(mess=="ok"){
	alert("积分管理设置成功!");
			}

}

</script>
<center>
<html:form action="/train/station/integral">
	<br><br>
	<fieldset style="height: 115px;width: 510px;">
		<legend><bean:message key="train.setparam.integral.field" /></legend>
		<table width="100%" style="line-height: 30px;margin-top: 10px;margin-left: 10px;">
		
			<tr>
				<td width="150" align="right"><bean:message key="train.setparam.integral.subset"/></td>
				<td>
					<hrms:optioncollection name="trainStationForm" property="emp_list" collection="emplist"/> 
    	            <html:select name="trainStationForm" property="emp_setid" style="width:250px;" onchange="searchItem();">
						<html:options collection="emplist" property="dataValue" labelProperty="dataName" />
					</html:select>
				</td>
				</tr>
				<tr>
				<td width="150" align="right"><bean:message key="train.setparam.integral.using" /></td>
				<td>
					<hrms:optioncollection name="trainStationForm" property="post_list" collection="postlist"/> 
    	            <html:select name="trainStationForm" property="post_setid" style="width:250px;" >
						<html:options collection="postlist" property="dataValue" labelProperty="dataName" />
					</html:select>
				</td>
			</tr>
			<tr>
				<td width="150" align="right"><bean:message key="train.setparam.integral.used" /></td>
				<td>
					<hrms:optioncollection name="trainStationForm" property="post_list" collection="postlist"/> 
    	            <html:select name="trainStationForm" property="post_setxid" style="width:250px;" >
						<html:options collection="postlist" property="dataValue" labelProperty="dataName" />
					</html:select>
				</td>
			</tr>
			
			
		   </table>
	   </fieldset>
	   <br>
	
	  <fieldset style="height: 95px;width: 510px;">
		<legend><bean:message key="train.setparam.integral.table" /></legend>
		<table width="100%" style="line-height: 30px;margin-top: 10px;margin-left: 10px;">
		<tr>
				<td width="150" align="right"><bean:message key="sys.res.card" /></td>
				<td>
				<hrms:optioncollection name="trainStationForm" property="reg_list" collection="reglist"/> 
    	            <html:select name="trainStationForm" property="reg_setid" style="width:250px;" >
						<html:options collection="reglist" property="dataValue" labelProperty="dataName" />
					</html:select>  
				</td>
			</tr>
		</table>
    </fieldset>
	<br/><br/>
	<input type="button" class="mybutton" value='<bean:message key="button.ok"/>' onclick="saveset();"/>
</html:form>
</center>
<script></script>