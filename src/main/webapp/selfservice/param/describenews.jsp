<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean"%>
<%@ taglib uri="/tags/struts-html" prefix="html"%>
<%@ taglib uri="/tags/struts-logic" prefix="logic"%>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms"%>

<html:form action="/selfservice/param/otherparam">
<table border="0" align="center" width="100%" height="100%" cellspacing="0" cellpadding="0">
<tr>
 <td width="100%" height="80%" valign="top">
  <table border="0" align="center" width="500">
			<tr>
				<td align="center">
						<table border="0" align="center">
							<tr>
								<td valign="top">
									<html:textarea name="otherParamForm" property="staff_info"   cols="60" rows="10" style=""/>
								</td>
							</tr>
							<tr>
								<td>
									&nbsp;&nbsp;指标集
									<hrms:optioncollection name="otherParamForm" property="setlist" collection="list" />
									<html:select name="otherParamForm" property="setid" style="width:300px;" size="1" onchange="changeFieldSet();">
										<html:options collection="list" property="dataValue" labelProperty="dataName" />
									</html:select>
								</td>
							</tr>
							<tr>
								<td>
									&nbsp;&nbsp;指标项
									<hrms:optioncollection name="otherParamForm" property="itemlist" collection="list" />
									<html:select name="otherParamForm" property="fieldItemId" style="width:300px;" size="1" onchange="changeFieldItem();">
										<html:options collection="list" property="dataValue" labelProperty="dataName" />
									</html:select>
								</td>
							</tr>
						</table>
				</td>
			</tr>
			<tr>
			<td align="center" height="30px;">
	    		<input type="button" name="Submit" value="&nbsp;确&nbsp;&nbsp;定&nbsp;" class="mybutton" onClick="save();">
				<input type="button" name="bto" value="&nbsp;取&nbsp;&nbsp;消&nbsp;" class="mybutton" onclick="winclose()">
			</td>
			</tr>
		</table>
 </td>
 </tr>
 </table>
</html:form>
<script language="JavaScript">
function save()
{
  otherParamForm.action = "/selfservice/param/otherparam.do?b_describenews_save=link";
  otherParamForm.submit();
  winclose();
}
function changeFieldSet(){
	var v = otherParamForm.setid.value;
	var hashvo=new ParameterSet();
	hashvo.setValue("setid",v);
	var In_paramters="flag=1";
	var request=new Request({method:'post',asynchronous:false,parameters:In_paramters,onSuccess:resultChangeFieldSet,functionId:'1010020308'},hashvo);					
}
function resultChangeFieldSet(outparamters){
  	var fielditemlist=outparamters.getValue("itemList");
	AjaxBind.bind(otherParamForm.fieldItemId,fielditemlist);
}
function changeFieldItem(){
	var m = document.otherParamForm.fieldItemId.value;
	if(m.length>0)//【8405】系统管理，设置明星员工的描述信息  jingq add 2015.03.31
		insertTxt(m);
}
function insertTxt(strtxt){
    if(strtxt==null)
   	 	return ;
  	var expr_editor=otherParamForm.staff_info;
    expr_editor.focus();
	var element = document.selection;
	if (element&&element!=null) 
	{
	  var rge = element.createRange();
	  if (rge!=null)	
	  	  rge.text="["+strtxt+"]";
	}else{
		var word = expr_editor.value;
		var _length=strtxt.length;
		var startP = expr_editor.selectionStart;
		var endP = expr_editor.selectionEnd;
		var ddd=word.substring(0,startP)+strtxt+word.substring(endP);
    	expr_editor.value=ddd;
		expr_editor.setSelectionRange(startP+_length,startP+_length); 
	}
}
function winclose(){
	if(parent.parent.Ext)
		parent.parent.Ext.getCmp('person_desc').close();
	else
		window.close();
}
</script>
