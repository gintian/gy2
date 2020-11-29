<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean"%>
<%@ taglib uri="/tags/struts-html" prefix="html"%>
<%@ taglib uri="/tags/struts-logic" prefix="logic"%>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms"%>

<html:form action="/train/resource/trainRescList">
<script language="javascript">
function saveRecord(){
	var hashvo = new ParameterSet();
	var codedesc = document.getElementById("codeitemdesc").value;
	if(codedesc==""){
		alert("名称不能为空！");
		return false;
	}
    hashvo.setValue("setid","${trainResourceForm.codesetid}");
    hashvo.setValue("flag","${trainResourceForm.checkflag}");
    hashvo.setValue("itemid","${trainResourceForm.codeitemid}");
    hashvo.setValue("codeitemdesc",getEncodeStr(codedesc));
    var request=new Request({asynchronous:false,onSuccess:add_code_ok,functionId:'2020030019'},hashvo); 
}
function add_code_ok(outparamters){
	var check=outparamters.getValue("check");
	if(check=="yes"){
		var itemid=outparamters.getValue("itemid");
		var codeitemdesc=outparamters.getValue("codeitemdesc");
	 	var beanvalue = new Object();
	 	beanvalue.id = itemid;
		beanvalue.desc = codeitemdesc;
		window.returnValue = beanvalue;
		window.close();
	}else{
		alert("您输入的类型名称重名了,请重新输入!");
		return false;
	}
}
function IsDigitStr() {
	if(event.keyCode==34||event.keyCode==39){
		return false;
	}
}	
function checkData(obj) {
	var CheckData = /<|>|'|;|"|'/;
	if ( CheckData.test(obj.value) ) {
        alert("用户名包含非法字符，请不要使用特殊字符！");
        obj.value="";   
   	}
} 
</script>
	<table width=70% border="0" cellpadding="0" cellspacing="0"
		align="center">
		<tr height="20">
			<td>&nbsp;</td>
		</tr>
		<tr>
			<td>
			<fieldset align="center" style="width:100%;height:90px;">
			<legend>${trainResourceForm.codetitle}</legend>
					<table border="0" cellpmoding="0" cellspacing="0"
						align="center" cellpadding="0" width="100%">
						<tr height="20">
							<td>&nbsp;</td>
						</tr>
						<tr>
							<td align="center" nowrap>
								<bean:message key="column.law_base.name" />&nbsp;
								<html:text name="trainResourceForm" styleClass="textColorWrite" property="codeitemdesc" onblur="checkData(this);" size="30" onkeypress="event.returnValue=IsDigitStr();"/>
							</td>
						</tr>
					</table>
				</fieldset>
			</td>
		</tr>
		<tr>
			<td align="center" style="padding-top: 10px;">
				<button Class="mybutton" name="b_save" onclick="saveRecord();">
					<bean:message key="button.ok" />
				</button>&nbsp;
				<html:button styleClass="mybutton" property="cancel"
					onclick="window.close();">
					<bean:message key="button.cancel" />
				</html:button>
			</td>
		</tr>
	</table>
</html:form>
