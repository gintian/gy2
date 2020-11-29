<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms"%>
<SCRIPT LANGUAGE="javascript">
function saveTypeValue(){
	var typename=document.getElementById("typename").value;
	if(typename==null||typename.length<1){
		alert(INPUT_ROSTER_TYPE_NAME);
		return false;
	}
    var hashvo=new ParameterSet();
	hashvo.setValue("typename",typename);	
	hashvo.setValue("chkflag","addtype");
	hashvo.setValue("a_inforkind","${musterForm.infor_Flag}");
	var request=new Request({method:'post',asynchronous:false,onSuccess:showTypeList,functionId:'0520000003'},hashvo);	
}	
function showTypeList(outparamters){	
	var typelist=outparamters.getValue("typelist");
	if(typelist!=null&&typelist.length>0){
		AjaxBind.bind(musterForm.mustertype,typelist);
		document.getElementById("typename").value="";
	}
}
function updateTypeValue(){
	var typename=document.getElementById("typename").value;
	var styleid=document.getElementById("mustertype").value;
	if(typename==null||typename.length<1){
		alert(INPUT_ROSTER_TYPE_NAME);
		return false;
	}
    var hashvo=new ParameterSet();
	hashvo.setValue("typename",typename);
	hashvo.setValue("styleid",styleid);		
	hashvo.setValue("chkflag","alert");
	hashvo.setValue("a_inforkind","${musterForm.infor_Flag}");
	var request=new Request({method:'post',asynchronous:false,onSuccess:showTypeList,functionId:'0520000003'},hashvo);	
}
function delTypeValue(){
	if(!confirm(DEL_INFO)){
		return false;
	}
	var vos= document.getElementsByName("mustertype");
	if(vos==null){
  		return false;
  	}
  	var left_vo=vos[0];
	var styleid="";

	if(left_vo!=null&&left_vo.length>0){
		for(i=0;i<left_vo.options.length;i++){
			if(left_vo.options[i].selected){
        		var item=left_vo.options[i].value;
				var arr = item.split(":");
				if(arr.length==2){
					styleid+=arr[0]+",";
				}
			}
		}
	}else{
		alert(SELECT_ROSTER_TYPE+"!");
		return false;
	}
	if(styleid==null||styleid.length<1){
		alert(SELECT_ROSTER_TYPE+"!");
		return false;
	}
	
    var hashvo=new ParameterSet();
	hashvo.setValue("styleid",styleid);		
	hashvo.setValue("chkflag","delete");
	hashvo.setValue("a_inforkind","${musterForm.infor_Flag}");
	var request=new Request({method:'post',asynchronous:false,onSuccess:showTypeList,functionId:'0520000003'},hashvo);	
}
function changeType(obj){
	var item = obj.value;
	if(item!=null&&item.length>0){
		var arr = item.split(":");
		if(arr.length==2){
			document.getElementById("typename").value=arr[1];
		}
	}
}
function checkChar(){ 
	var k = window.event.keyCode;
	if ( k==39||k==47||k==34){
		return false;
	}
}   
function reflesh(){
	self.parent.mil_menu.location ="/general/muster/hmuster/rostertree.jsp";
}
</SCRIPT>
<html:form action="/general/muster/opertype">
<table width="90%" align="center" height="300" border="0" cellspacing="0" cellpadding="0" style="width:expression(document.body.clientWidth-10);">
  <tr>
    <td height="300">
    	<fieldset align="center" style="width:100%;">
    	<legend><bean:message key="muster.label.type"/></legend>
		<table width="90%" border="0" cellspacing="0" cellpadding="0" align="center" style="width:expression(document.body.clientWidth-20);margin:5px;">
		<logic:notEqual name="musterForm" property="chkflag" value="del">
		<tr>
			<td height="35" align="left">
				<bean:message key="muster.label.type"/>&nbsp;&nbsp;<input type="text" size="41" maxlength="24" onkeypress="event.returnValue=checkChar();" id="typename" name="typename" class="text4">
			</td>
			<td align="right">
				<input type="button" value="<bean:message key='kq.emp.button.save'/>" class="mybutton" onclick="saveTypeValue();" style="margin-right:5px;margin-top:2px">
			</td>
		</tr>
		</logic:notEqual>
		<logic:equal name="musterForm" property="chkflag" value="del">
		<tr><td colspan="2"><input type="hidden" id="typename" name="typename"></td></tr>
		</logic:equal>
        <tr> 
          <td height="100%" valign="top" colspan="2">
          	<hrms:optioncollection name="musterForm" property="typelist" collection="list" />
          	<html:select name="musterForm" property="mustertype" multiple="multiple"  style="height:260px;width:100%;font-size:9pt">
				<html:options collection="list" property="dataValue" labelProperty="dataName"/>
			</html:select>
          </td>
        </tr>
      </table>
      </fieldset>
	</td>
  </tr>
</table>
<table width="80%" height="35px" align="center" border="0" cellspacing="0" cellpadding="0">
<tr>
<td align="center">
	<logic:equal name="musterForm" property="chkflag" value="del">
		<input type="button" value="<bean:message key='kq.emp.change.emp.leave'/>" onclick="delTypeValue();" class="mybutton">
		&nbsp;
	</logic:equal>
	<input type="button" value="<bean:message key='lable.welcomeboard.close'/>" onclick="window.close();" class="mybutton"></td>
</tr>
</table>
</html:form>
<script>
	if(!getBrowseVersion() || getBrowseVersion() == 10){//非ie兼容视图 样式修改 wangb 20190308
		var table1 = document.getElementsByName('musterForm')[0].getElementsByTagName('table')[0];
		table1.setAttribute('align','left');
		var fieldset = document.getElementsByTagName('fieldset')[0];
		fieldset.setAttribute('align','left');
		var table2 = fieldset.getElementsByTagName('table')[0];
		table2.setAttribute('width','100%');
	}
</script>