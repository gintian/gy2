<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<script language="JavaScript">
function change(obj){
	var item = obj.value;
	if(item!=null&&item.length>0){
		var arr = item.split(":");
		if(arr.length==2){
			document.getElementById("id").value=arr[0];
		}
	}
}
function delCond(){
	var optionCount = document.getElementById("titleid").options.length;
	var ids = "";
	if(optionCount>0){
		ids = document.getElementById("id").value;
	}
	//var ids = document.getElementById("id").value;
	var type = "${searchInformForm.type}";
	if(ids==null||ids.length<1){
		alert("请选择查询条件!");
		return;
	}
	if(confirm("确认删除？")){
		var hashvo=new ParameterSet();
		hashvo.setValue("id",ids);
		hashvo.setValue("type",type);
		hashvo.setValue("flag","delete");
		var request=new Request({method:'post',asynchronous:false,onSuccess:checkdelSave,functionId:'3020110076'},hashvo);	
		selectIndex();
	}
}
function checkdelSave(outparamters){
	var check=outparamters.getValue("check");
	if(check=='ok'){
		var titlelist=outparamters.getValue("titlelist");
		AjaxBind.bind(searchInformForm.titleid,titlelist);	
	}
}
function searchCond(){
	var ids = document.getElementById("id").value;
	if(ids==null||ids.length<1){
		alert("请选择查询条件!");
		return;
	}
	
	var hashvo=new ParameterSet();
	hashvo.setValue("id",ids);
	hashvo.setValue("a_code","${searchInformForm.a_code}");
	hashvo.setValue("tablename","${searchInformForm.tablename}");
	hashvo.setValue("type","${searchInformForm.type}");
	hashvo.setValue("flag","search");
	var request=new Request({method:'post',asynchronous:false,onSuccess:checksearchSave,functionId:'3020110076'},hashvo);	
}
function checksearchSave(outparamters){
	var check=outparamters.getValue("check");
	if(check=='ok'){
		var obj = new Object();
		obj.isExistField=outparamters.getValue("isExistField");
		obj.wherestr=outparamters.getValue("wherestr");
		obj.wheresql=outparamters.getValue("wheresql");
		window.returnValue=obj;
		//window.returnValue='ok';
		window.close();
	}else{
		alert(check);
	}
}
function editCond(){
	var a_code = "${searchInformForm.a_code}";
	var tablename = "${searchInformForm.tablename}";
	var type = "${searchInformForm.type}";
	var ids = document.getElementById("id").value;
	if(ids==null||ids.length<1){
		alert("请选择查询条件!");
		return;
	}
	var thecodeurl="/general/inform/search/gmsearch.do?b_query=link&type="+type+"&a_code="+a_code+"&tablename="+tablename+"&id="+ids;
    var  return_vo= window.showModalDialog(thecodeurl, "", 
              "dialogWidth:700px; dialogHeight:430px;resizable:no;center:yes;scroll:no;status:no;");
    if(return_vo!=null&&return_vo!='ok'&&return_vo.length<10){
   		var hashvo=new ParameterSet();
		hashvo.setValue("id",ids);
		hashvo.setValue("a_code",a_code);
		hashvo.setValue("tablename",tablename);
		hashvo.setValue("type",type);
		hashvo.setValue("flag","alertname");
		var request=new Request({method:'post',asynchronous:false,onSuccess:checkEditSave,functionId:'3020110076'},hashvo);
		document.getElementById("titleid").value=return_vo;
	}else if(return_vo!=null&&return_vo=='ok'){
		window.returnValue='ok';
		window.close();
	}else if(return_vo!=null&&return_vo.length>10){
		window.returnValue='ok';
		window.close();
	}
}
function checkEditSave(outparamters){
	var check=outparamters.getValue("check");
	var returnvalue=outparamters.getValue("returnvalue");
	if(check=='ok'){
		alert("保存成功!");
		var titlelist=outparamters.getValue("titlelist");
		AjaxBind.bind(searchInformForm.titleid,titlelist);
	}
}
function selectIndex(){
	var titleidarr= document.getElementsByName("titleid");
	var titleidarr_vo = titleidarr[0];
	if(titleidarr_vo.options.length>0){
		titleidarr_vo.options[0].selected=true;
		var obj = document.getElementById("titleid");
		change(obj);
	}
}
function closeWin(){
	returnValue="aaa";
	window.close();
}
</script>
<html:form action="/general/inform/search/searchcommon">
<input type="hidden" name="id">
<table width="100%" height="240" border="0" cellspacing="0" align="center" cellpadding="0">
  <tr>
    <td height="240" width="90%" valign="top">
		<table width="100%" border="0" cellspacing="0" align="center" cellpadding="0">
        <tr> 
          <td height="100%" valign="top">
          	<hrms:optioncollection name="searchInformForm" property="titlelist" collection="list" />
          	<html:select name="searchInformForm" property="titleid" multiple="multiple" onchange="change(this);" style="height:220px;width:100%;font-size:9pt" styleId="titleid">
				<html:options collection="list" property="dataValue" labelProperty="dataName"/>
			</html:select>
          </td>
        </tr>
      </table>
	</td>
	<td valign="top">
		<table width="100%" border="0" cellspacing="0" align="center" cellpadding="0">
        	<tr> 
          		<td height="45" align="center" valign="top"><input type="button" name="button1" onclick="searchCond();" value="<bean:message key='button.query'/>" Class="mybutton"></td>
          	</tr>
          	<hrms:priv func_id="2601005,0303011">
        	<tr> 
          		<td height="45" align="center" valign="top"><input type="button" name="button2" onclick="editCond();" value="<bean:message key='label.edit.user'/>" Class="mybutton"></td>
          	</tr>
          	</hrms:priv>
          	 <hrms:priv func_id="2601001,0303010">
          	<tr> 
          		<td height="45" align="center" valign="top"><input type="button" name="button2" onclick="delCond();" value="<bean:message key='button.delete'/>" Class="mybutton"></td>
          	</tr>
          	</hrms:priv>
        	<tr> 
          		<td height="45" align="center" valign="top"><input type="button" name="button４" onclick="closeWin();" value="<bean:message key='button.close'/>" Class="mybutton"></td>
        	</tr>
      </table>
	</td>
  </tr>
</table>
</html:form>
<script language="JavaScript">
selectIndex();
</script>