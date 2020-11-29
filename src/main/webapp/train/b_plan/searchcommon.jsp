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
		}else{
			document.getElementById("id").value="";
		}
	}else{
		document.getElementById("id").value="";
	}
}
function delCond(){
	var ids = document.getElementById("id").value;
	if(ids==null||ids.length<1){
		alert("<bean:message key='train.b_plan.selectsearch'/>");
		return;
	}
	if(confirm("<bean:message key='id_factory.delconfirm'/>")){
		var type = "${planTrainForm.type}";
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
		AjaxBind.bind(planTrainForm.titleid,titlelist);
	}
}
function searchCond(){
	var ids = document.getElementById("id").value;
	if(ids==null||ids.length<1){
		alert("<bean:message key='train.b_plan.selectsearch'/>");
		return;
	}
	
	var hashvo=new ParameterSet();
	hashvo.setValue("id",ids);
	var request=new Request({method:'post',asynchronous:false,onSuccess:checksearchSave,functionId:'2020050010'},hashvo);	
}
function checksearchSave(outparamters){
	var searchstr=outparamters.getValue("searchstr");
	window.returnValue=searchstr;
	window.close();
}
function editCond(){
	var tablename = "${planTrainForm.tablename}";
	var type = "${planTrainForm.type}";
	var ids = document.getElementById("id").value;
	if(ids==null||ids.length<1){
		alert("<bean:message key='train.b_plan.selectsearch'/>");
		return;
	}
	var thecodeurl="/train/b_plan/gmsearch.do?b_query=link&type="+type+"&tablename="+tablename+"&id="+ids;
    var  return_vo= window.showModalDialog(thecodeurl, "", 
              "dialogWidth:700px; dialogHeight:430px;resizable:no;center:yes;scroll:no;status:no");
    if(return_vo!=null){
    	window.returnValue=return_vo;
		window.close();
    }
}
function selectIndex(){
	var titleidarr= document.getElementsByName("titleid");
	var titleidarr_vo = titleidarr[0];
	if(titleidarr_vo.options.length>0){
		titleidarr_vo.options[0].selected=true;
	}
	var obj = document.getElementById("titleid");
	change(obj);
}
function closeWin(){
	//returnValue="aaa";
	window.close();
}
</script>
<html:form action="/train/b_plan/gmsearch">
<input type="hidden" name="id">
<table width="100%" height="240" border="0">
  <tr>
    <td height="240" width="80%" valign="top">
		<table width="100%" border="0">
        <tr> 
          <td height="100%" valign="top">
          	<hrms:optioncollection name="planTrainForm" property="titlelist" collection="list" />
          	<html:select name="planTrainForm" property="titleid" multiple="multiple" onchange="change(this);" style="height:220px;width:100%;font-size:9pt">
				<html:options collection="list" property="dataValue" labelProperty="dataName"/>
			</html:select>
          </td>
        </tr>
      </table>
	</td>
	<td valign="bottom">
		<table width="100%" border="0">
			<tr> 
          		<td>&nbsp;</td>
        	</tr>
        	<tr> 
          		<td height="30" align="center"><input type="button" name="button1" onclick="searchCond();" value="<bean:message key='button.query'/>" Class="mybutton"></td>
          	</tr>
        	<tr> 
          		<td height="30" align="center"><input type="button" name="button2" onclick="editCond();" value="<bean:message key='label.edit.user'/>" Class="mybutton"></td>
          	</tr>
          	<tr> 
          		<td height="30" align="center"><input type="button" name="button2" onclick="delCond();" value="<bean:message key='button.delete'/>" Class="mybutton"></td>
          	</tr>
        	<tr> 
          		<td height="30" align="center"><input type="button" name="button４" onclick="closeWin();" value="<bean:message key='button.close'/>" Class="mybutton"></td>
        	</tr>
        	<tr> 
          		<td height="30" align="center">&nbsp;</td>
        	</tr>
      </table>
	</td>
  </tr>
</table>
</html:form>
<script language="JavaScript">
selectIndex();
</script>