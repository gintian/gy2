<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<script type="text/javascript">
<!--
function sub(){
var myReg =/^(-?\d+)(\.\d+)?$/
var val=moneyStyleForm.nitemid.value;
var name=moneyStyleForm.cname.value;
if(name==null||trim(name)==''){
alert("请输入货币面值的名称!");
return;
}
if(val==null||trim(val).length==0)
{
alert("请输入货币面值!");
return;
}
if(!myReg.test(val)){
alert("货币面值请输入数字!");
return;
}
 var hashVo=new ParameterSet();
 hashVo.setValue("nstyleid","${moneyStyleForm.nstyleid}");
 hashVo.setValue("nitemid",val);
 hashVo.setValue("before","${moneyStyleForm.nitemid}");
 hashVo.setValue("isVisable","${moneyStyleForm.isVisable}");
  var request=new Request({method:'post',asynchronous:false,onSuccess:resubmit,functionId:'3020040011'},hashVo);			
}

function saveAndCon(){
var myReg =/^(-?\d+)(\.\d+)?$/
var val=moneyStyleForm.nitemid.value;
var name=moneyStyleForm.cname.value;
if(name==null||trim(name)==''){
alert("请输入货币面值的名称!");
return;
}
if(val==null||trim(val).length==0)
{
alert("请输入货币面值!");
return;
}
if(!myReg.test(val)){
alert("货币面值请输入数字");
return;
}
var hashVo=new ParameterSet();
 hashVo.setValue("nstyleid","${moneyStyleForm.nstyleid}");
 hashVo.setValue("nitemid",val);
  hashVo.setValue("before","${moneyStyleForm.nitemid}");
  hashVo.setValue("isVisable","${moneyStyleForm.isVisable}");
  var request=new Request({method:'post',asynchronous:false,onSuccess:resubmitSAndC,functionId:'3020040011'},hashVo);			

}


function ret(){
moneyStyleForm.action="/gz/templateset/moneystyle/initMoneyStyleDetail.do?b_init=init";
moneyStyleForm.submit();
}
function resubmit(outparameters){
var flag=outparameters.getValue("flag");
var nitemid=outparameters.getValue("nitemid");
if(parseInt(flag)==1){
alert("面值为 ["+nitemid+"] 货币项目已经存在，不可重复");
return;
}

moneyStyleForm.action="/gz/templateset/moneystyle/newMoneyStyleDetail.do?b_save=save";
moneyStyleForm.submit();
}
function resubmitSAndC(outparameters){
var flag=outparameters.getValue("flag");
var nitemid=outparameters.getValue("nitemid");
if(parseInt(flag)==1){
alert("面值为 ["+nitemid+"] 货币项目已经存在，不可重复");
return;
}
moneyStyleForm.action="/gz/templateset/moneystyle/newMoneyStyleDetail.do?b_con=con";
moneyStyleForm.submit();
}
//-->
</script>
<html:form action="/gz/templateset/moneystyle/newMoneyStyleDetail">
<br>
<table width="60%" border="0" cellspacing="0"  align="center" cellpadding="0" class="ListTable">
<thead>
<tr>
<logic:equal name="moneyStyleForm" property="isVisable" value="new">
<td align="left" class="TableRow" colspan="2" nowrap>增加货币面值
</td>
</logic:equal>
<logic:equal name="moneyStyleForm" property="isVisable" value="edit">
<td align="left" class="TableRow" colspan="2" nowrap>维护货币面值
</td>
</logic:equal>
</tr>
</thead>
<tr ><td align="right" class="RecordRow" width="40%">名称&nbsp;</td><td align="left" class="RecordRow" width="60%">
&nbsp;<input type='text' name='cname' value='<bean:write name="moneyStyleForm" property="cname" />' maxlength="5" class="inputtext"/></td>
</tr>
<tr><td align="right" class="RecordRow" width="40%">面值&nbsp;</td><td align="left" class="RecordRow" width="60%">
&nbsp;<input type='text' name='nitemid' value='<bean:write name="moneyStyleForm" property="nitemid" />' maxlength='4' class="inputtext"/>
</td></tr> 
<tr>
<td align="center" colspan="2" style="padding-top:3px;">
<input type='button' class='mybutton' name='b_save' value='<bean:message key="button.save"/>' onclick='sub();'/>
<logic:equal name="moneyStyleForm" property="isVisable" value="new">
<input type='button' class='mybutton' name='savecon' value='保存&继续' onclick='saveAndCon();'/>
</logic:equal>
<input type='button' class='mybutton' name='clo' value='<bean:message key="button.return"/>' onclick='ret();'/>
<input type='hidden' name='nstyleid'  value='<bean:write name="moneyStyleForm" property="nstyleid" />'/>
<input type='hidden' name='beforenitemid' value='<bean:write name="moneyStyleForm" property="nitemid" />'/>
</td>
</tr>

</table>

</html:form>