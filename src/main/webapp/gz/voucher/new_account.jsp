<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ page import="com.hjsj.hrms.actionform.gz.voucher.VoucherForm,java.util.*,com.hrms.hjsj.sys.VersionControl"%>
<%
	VoucherForm voucherForm=(VoucherForm)session.getAttribute("financial_voucherForm"); 
%>

<script type="text/javascript">

function ret()
{
   var dir=financial_voucherForm.formfile.value;
   if(trim(dir).length==0)
   {
       alert("请选择要导入的文件");
       return;
   }
    if(!validateUploadFilePath(dir))
           return;
   var index=dir.lastIndexOf(".");
   if(dir.substring(index)!=".xls" && dir.substring(index)!=".xlsx")
   {
       alert("所选择的文件扩展名应为[.xls]或者[.xlsx]");
       return;
   }
   financial_voucherForm.action="/gz/voucher/financial_voucher.do?b_upload=upload&opt=2";
   financial_voucherForm.submit();
}
function fanhui(){
    financial_voucherForm.action="/gz/voucher/financial_voucher.do?b_account=link";
    financial_voucherForm.target="mil_body";        	
	financial_voucherForm.submit();
}
function new_account()
{
	var accname=financial_voucherForm.accname.value;
	var accid=financial_voucherForm.accid.value;
	var accgrade=financial_voucherForm.accgrade.value;
	if(accid==""){
	   alert("科目编号不允许为空");
	   return false;
	}
	if(!accname){
		alert("科目名称不允许为空");
		return false;
	}
	if(!checkIsNum(accgrade)){
	   alert("科目级别只能输入数值");
	   return false;
	}
	var hashvo=new ParameterSet();
  	hashvo.setValue("accid",accid);
  	hashvo.setValue("accname",accname);
  	hashvo.setValue("accgrade",accgrade);
  	hashvo.setValue("opt","new");
	var request=new Request({method:'post',asynchronous:false,onSuccess:fanhui,functionId:'3020073004'},hashvo);
}
function checkIsNum(value)//验证是否是数字
{
    return /^-?\d+(\.\d+)?$/.test(value);
}
function new_account1()
{
	var accname=financial_voucherForm.accname.value;
	var accid=financial_voucherForm.accid.value;
	var accgrade=financial_voucherForm.accgrade.value;
	if(accid==""){
       alert("科目编号不允许为空");
       return false;
    }
    if(!checkIsNum(accgrade)){
       alert("科目级别只能输入数值");
       return false;
    }
	var hashvo=new ParameterSet();
  	hashvo.setValue("accid",accid);
  	hashvo.setValue("accname",accname);
  	hashvo.setValue("accgrade",accgrade);
  	hashvo.setValue("opt","update");
  	hashvo.setValue("i_id","${financial_voucherForm.i_id}");
	var request=new Request({method:'post',asynchronous:false,onSuccess:fanhui,functionId:'3020073004'},hashvo);
}
function NewAndGo()
{
    var accname=financial_voucherForm.accname.value;
    var accid=financial_voucherForm.accid.value;
    var accgrade=financial_voucherForm.accgrade.value;
    if(accid==""){
       alert("科目编号不允许为空");
       return false;
    }
    if(!checkIsNum(accgrade)){
       alert("科目级别只能输入数值");
       return false;
    }
    financial_voucherForm.action="/gz/voucher/financial_voucher.do?b_new=link&opt=new";
    financial_voucherForm.submit();
}
</script>
<html:form action="/gz/voucher/financial_voucher" >

<table width="30%" border="0" cellspacing="1"  align="center" cellpadding="1" style="margin-top:60px;">
<tr>
<td align="center" nowrap>
<fieldset align="center">
<legend>会计科目</legend>
<table width="30%" border="0" cellspacing="1"  align="center" cellpadding="1">
<tr>
<td align="center" nowrap>
科目编号&nbsp;
</td>
<td align="center" nowrap>
<input type="text" name="accid" value="${financial_voucherForm.accid}" size="40" class="inputtext"/>
</td>
</tr>

<tr>
<td align="center" nowrap>
科目名称&nbsp;
</td>
<td align="center" nowrap>
<input type="text" name="accname" value="${financial_voucherForm.accname}" size="40" class="inputtext"/>
</td>
</tr>

<tr>
<td align="center" nowrap>
科目级别&nbsp;
</td>
<td align="center" nowrap>
<input type="text" name="accgrade" value="${financial_voucherForm.accgrade}" size="40" class="inputtext"/>
</td>
</tr>

</table>
</fieldset>
</td>
</tr>
<tr>
<td align="center" style="padding-top:3px;" nowrap>

<%if(voucherForm.getFlag().equals("1")){ %>
<input type="button" name="ok" value="保存" onclick="new_account();" class="mybutton"/>
<input type="button" name="okandgo" value="保存&继续" onclick="NewAndGo();" class="mybutton"/>
<%}else if(voucherForm.getFlag().equals("2")){ %>
<input type="button" name="ok" value="保存" onclick="new_account1();" class="mybutton"/>
<%} %>
<input type="button" name="cancel" value="返回" onclick="fanhui();" class="mybutton"/>
</td>
</tr>
</table>
<script type="text/javascript">

</script>
</html:form>