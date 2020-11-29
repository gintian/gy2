<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<script language="JavaScript" src="/js/popcalendar.js"></script>
<script language="JavaScript" src="/js/function.js"></script>
<script language="JavaScript" src="/js/validate.js"></script>
<script language="JavaScript" src="/js/constant.js"></script>
<link href="/css/css1.css" rel="stylesheet" type="text/css">
<hrms:themes />
<script language="Javascript" src="/gz/salary.js"></script>
<script type="text/javascript">
<!--
function sendemail_deleteTemplate(str1,str2)
{
var id="";
var text="";
var num=0;
var setobj=$('templateId');
if(setobj.options.length==0)
return;
for(var i=0;i<setobj.options.length;i++)
{
  if(setobj.options[i].selected)
  {
     num++;
     id+=","+setobj.options[i].value;
     text+="["+setobj.options[i].text+"] ";
  }
}
if(num==0)
{
  alert(str1);
  return;
}
if(confirm(str2+":"+text)){
sendEmailForm.id.value=id.substring(1);
sendEmailForm.action="/gz/gz_accounting/gz_emailtemplate_list.do?b_delete=delete";
sendEmailForm.submit();

}else
{
return;
}

}

function sendemail_sub(type,str,code,salaryid)
{
var obj=new Object();
if(parseInt(type)==1)//新增
{
   obj.type=type;
   obj.id="";
}else{//修改或发送
var id="";
var num=0;
var setobj=$('templateId');
if(setobj.options.length==0)
return;
for(var i=0;i<setobj.options.length;i++)
{
  if(setobj.options[i].selected)
  {
     num++;
     id=setobj.options[i].value;
  }
}
if(num==0)
{
  alert(str);
  return;
} 
obj.type=type;
obj.id=id;
}
returnValue=obj;
window.close();
}
function email_select_template_ok(salaryid,code,count)
{
var id="";
var num=0;
var setobj=$('templateId');
if(setobj.options.length==0)
return;
for(var i=0;i<setobj.options.length;i++)
{
  if(setobj.options[i].selected)
  {
     num++;
     id=setobj.options[i].value;
  }
}
if(num==0)
{
  alert("请选择模板");
  return;
}
  if(parseInt(count)==0)
  {
     var hashvo=new ParameterSet(); 	
     hashvo.setValue("id",id);
     hashvo.setValue("code",code);
     hashvo.setValue("salaryid",salaryid);
   	 var In_paramters="flag=1"; 	
     var request=new Request({method:'post',asynchronous:false,
     parameters:In_paramters,onSuccess:gzemail_is_reexport,functionId:'0202030013'},hashvo); 
  }
  else
  {
     var obj= new Object();
     obj.id=id;
     obj.salaryid=salaryid;
     obj.isno="2";
     returnValue=obj;
     window.close();
     
     
  }
}
function gzemail_is_reexport(outparameters)
{
 var code=outparameters.getValue("code");
 var flag=outparameters.getValue("flag");
 var id=outparameters.getValue("id");
 var salaryid=outparameters.getValue("salaryid");
 var isno="";
 if(parseInt(flag)==1)
 {
    if(confirm("是否重新导入数据"))
    {
       isno="1";
    }
    else
    {
       isno="2";
    }
 }
 else
 { 
    isno="1";
 }
 var obj = new Object();
 obj.id=id;
 obj.isno=isno;
 obj.salaryid=salaryid;
 returnValue=obj;
 window.close();
}

//-->
</script>
<base id="mybase" target="_self">
<html:form action="/gz/gz_accounting/gz_emailtemplate_list">
<table width='80%' border="0" cellspacing="1"  align="center" cellpadding="1">
<tr>
<td>
<fieldset align="center">
<legend>选择模板</legend>
<table width='100%' border="0" cellspacing="1"  align="center" cellpadding="1">
<thead>
<tr>
<td align="left" class="TableRow" nowrap>
选择邮件模板
</td>
</tr>
</thead>
<tr>
<td align="center">
 <html:select name="sendEmailForm" size="10" property="templateId" style="height:250px;width:100%;font-size:9pt">
		              <html:optionsCollection property="templateList" value="dataValue" label="dataName"/>
		        </html:select>	
</tr>
</table>
</fieldset>
</td>
</tr>
<tr>
<td>
<input type="hidden" name="id" value=""/>
<table width='100%' border="0" cellspacing="1"  align="center" cellpadding="1">
<tr>
<logic:equal name="sendEmailForm" property="input_type" value="1">
<td align="center" nowrap>
<input type="button" name="new" value="新增" class="mybutton" onclick="sendemail_sub('1','','${sendEmailForm.code}','${sendEmailForm.salaryid}');"/>
<input type="button" name="edit" value="编辑" class="mybutton" onclick="sendemail_sub('2','请选择要修改的模板','${sendEmailForm.code}','${sendEmailForm.salaryid}');"/>
<input type="button" name="delete" value="删除" class="mybutton" onclick="sendemail_deleteTemplate('请选择要删除的模板','确认删除吗');"/>
<input type="button" name="send" value="确定" class="mybutton" onclick="sendemail_sub('3','请选择邮件模板','${sendEmailForm.code}','${sendEmailForm.salaryid}');"/>
<input type="button" name="clo" value="关闭" class="mybutton" onclick="window.close();"/>
</td>
</logic:equal>
<logic:equal name="sendEmailForm" property="input_type" value="2">
<td align="center" nowrap>
<input type="button" name="ok" value="确定" class="mybutton" onclick="email_select_template_ok('${sendEmailForm.salaryid}','${sendEmailForm.code}','${sendEmailForm.num}');"/>
<input type="button" name='clo' value='关闭' class="mybutton" onclick='window.close();'/>
</td>
</logic:equal>
</tr>
</table>
</td>
</tr>
</table>
</html:form>