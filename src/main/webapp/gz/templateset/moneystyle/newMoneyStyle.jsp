<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<script type="text/javascript">
<!--
function sub(){
if(moneyStyleForm.cname.value==null||trim(moneyStyleForm.cname.value).length==0)
{
    alert("请输入币种名称!");
    return;
}
if(moneyStyleForm.ctoken.value==null||trim(moneyStyleForm.ctoken.value).length==0)
{
    alert("请输入币种符号!");
    return;
}
if(moneyStyleForm.cunit.value==null||trim(moneyStyleForm.cunit.value).length==0)
{
    alert("请输入币种单位!");
    return;
}
if(moneyStyleForm.nratio.value==null||trim(moneyStyleForm.nratio.value).length==0)
{
    alert("请输入币种汇率!");
    return;
}
var myReg =/^(-?\d+)(\.\d+)?(e|E)?((-|\+)?\d+)?$/
var val=moneyStyleForm.nratio.value;
if((val!=null||val!='')&&!myReg.test(val)){
alert("汇率输入有误!");
return;
}

moneyStyleForm.action="/gz/templateset/moneystyle/newMoneyStyle.do?b_save=save";
moneyStyleForm.submit();
window.close();
}
function saveAndCon(){
if(moneyStyleForm.cname.value==null||trim(moneyStyleForm.cname.value).length==0)
{
    alert("请输入币种名称!");
    return;
}
if(moneyStyleForm.ctoken.value==null||trim(moneyStyleForm.ctoken.value).length==0)
{
    alert("请输入币种符号!");
    return;
}
if(moneyStyleForm.cunit.value==null||trim(moneyStyleForm.cunit.value).length==0)
{
    alert("请输入币种单位!");
    return;
}
if(moneyStyleForm.nratio.value==null||trim(moneyStyleForm.nratio.value).length==0)
{
    alert("请输入币种汇率!");
    return;
}
var myReg =/^(-?\d+)(\.\d+)?(e|E)?((-|\+)?\d+)?$/
var val=moneyStyleForm.nratio.value;
if((val!=null||val!='')&&!myReg.test(val)){
alert("汇率输入有误");
return;
}
moneyStyleForm.action="/gz/templateset/moneystyle/newMoneyStyle.do?b_con=con";
moneyStyleForm.submit();
}
function ret(){
moneyStyleForm.action="/gz/templateset/moneystyle/initMoneyStyle.do?b_init=init";
moneyStyleForm.submit();
}
//-->
</script>
<html:form action="/gz/templateset/moneystyle/newMoneyStyle">
<br>
<table width="60%" border="0" cellspacing="0"  align="center" cellpadding="0" class="ListTable">
<thead>
<tr>
<logic:equal name="moneyStyleForm" property="isVisable" value="new">
<td align="left" class="TableRow" colspan="2" nowrap>新增币种
</td>
</logic:equal>
<logic:equal name="moneyStyleForm" property="isVisable" value="edit">
<td align="left" class="TableRow" colspan="2" nowrap>币种维护
</td>
</logic:equal>
</tr>
</thead>
<tr ><td align="right" class="RecordRow" width="40%">名称&nbsp;</td>
<td align="left" class="RecordRow" width="60%">
&nbsp;<input type='text' name='cname' value='<bean:write name="moneyStyleForm" property="cname" />' class="inputtext"/></td>
</tr>
<tr><td align='right' class="RecordRow" width="40%">符号&nbsp;</td><td align="left" class="RecordRow" width="60%">
&nbsp;<input type='text' name='ctoken' value='<bean:write name="moneyStyleForm" property="ctoken" />' class="inputtext"/>
</td></tr> 
<tr><td align='right' class="RecordRow" width="40%">单位&nbsp;</td><td align="left" class="RecordRow" width="60%">
&nbsp;<input type='text' name='cunit' value='<bean:write name="moneyStyleForm" property="cunit" />' class="inputtext"/>
</td></tr> 
<tr><td align='right' class="RecordRow" width="40%">汇率&nbsp;</td><td align="left" class="RecordRow" width="60%">
&nbsp;<input type='text' name='nratio' value='<bean:write name="moneyStyleForm" property="nratio" />' maxlength='4' class="inputtext"/>
</td></tr> 
<tr>
<td align="center" colspan="2" style="padding-top:3px;">
<input type='button' class='mybutton' name='b_save' value='<bean:message key="button.save"/>' onclick='sub();'/>
<logic:equal name="moneyStyleForm" property="isVisable" value="new">
<input type='button' class='mybutton' name='savecon' value='保存&继续' onclick='saveAndCon();'/>
</logic:equal>
<input type='button' class='mybutton' name='clo' value='<bean:message key="button.return"/>' onclick='ret();'/>
<input type='hidden' name='nstyleid'  value='<bean:write name="moneyStyleForm" property="nstyleid" />'/>
</td>
</tr>

</table>

</html:form>