<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ page import="com.hjsj.hrms.actionform.general.email_template.GzEmailForm,java.util.ArrayList"%>
<script language="Javascript" src="/gz/salary.js"></script>
<script type="text/javascript">
<!--
function changeFieldSet(){
	var v = gzEmailForm.fieldsetid.value;
  	var hashvo=new ParameterSet(); 	
    hashvo.setValue("fieldsetid",v);
   	var In_paramters="flag=1"; 	
    var request=new Request({method:'post',asynchronous:false,
		     parameters:In_paramters,onSuccess:resultChangeFieldSet,functionId:'0202030010'},hashvo);					

    
  }
  function resultChangeFieldSet(outparamters){
  	var fielditemlist=outparamters.getValue("itemlist");
	AjaxBind.bind(gzEmailForm.itemid,fielditemlist);
  }
  function email_select_field_ok()
  {
      var value="";
      var text="";
      var num=0;
      var obj=$("itemid");
     for(var i=0;i<obj.options.length;i++)
     {
         if(obj.options[i].selected)
         {
            value=obj.options[i].value;
            text=obj.options[i].text;
            num++;
            break;
         }
     }
     if(num==0)
     {
        alert("请选择用于发送邮件的指标");
        return;      
     }
     var objlist = new Object();
     objlist.value=value;
     objlist.text=text;

	 //兼容Ext window wangbs 20190319
     winClose(objlist);
     // returnValue=objlist;
     // window.close();
  }
function winClose(objlist){
    if(parent.parent.emailSelectReturn){
        parent.parent.emailSelectReturn(objlist);
	}else{
        window.close();
	}
}
//-->
</script>
<html:form action="/general/email_template/select_email_field">
<table width='290' border="0" cellspacing="0"  align="center" cellpadding="0">
<tr>
<td align="center">
<fieldset align="center">
<LEGEND>设置邮件指标</LEGEND>
<table width='100%' border="0" cellspacing="0"  align="center" cellpadding="0">
<tr>

<td align="center" nowrap>
<html:select name="gzEmailForm" property="fieldsetid" size="1" onchange="changeFieldSet();" style="width:240px;">
			<html:optionsCollection property="fieldsetlist" value="dataValue" label="dataName"/>
		    </html:select>
	 </td>
</tr>
		   <tr>
		   <td align="center" nowrap>
		     <html:select name="gzEmailForm" size="15" property="itemid" style="width:240px;height:280px;" ondblclick="email_select_field_ok();">
		              <html:optionsCollection property="itemlist" value="dataValue" label="dataName"/>
		        </html:select>
		        </td>
		        </tr>
</td>
</tr>
</table>
</fieldset>
</td>
</tr>
<tr>
<td align="center" nowrap height="35px;">
<input type="button" name="ok" class="mybutton" value="确定" onclick="email_select_field_ok();"/>
<input type="button" name="col" class="mybutton" value="关闭" onclick="winClose();"/>

</td>
</tr>
</table>
</html:form>