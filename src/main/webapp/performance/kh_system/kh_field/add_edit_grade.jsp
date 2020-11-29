<%@ page contentType="text/html; charset=UTF-8" language="java"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<script type="text/javascript">
<!--
function check(saveandcontinue)
{

   var grade_id = khFieldForm.grade_id.value;
     if(trim(grade_id)==''||trim(grade_id).length<=0)
     {
       alert("标度代码不能为空!");
       return;
    }
   var chc = /^[A-Za-z0-9]+$/;
   if(!chc.test(grade_id))
   {
       alert("标度代码包含非法字符!");
       return;
   }
   var gradedesc=khFieldForm.gradedesc.value;
   if(gradedesc==null||trim(gradedesc).length<=0)
   {
    alert("标度内容不能为空!");
    return;
    }
    var gradevalue =khFieldForm.gradevalue.value;
     var checkFloat = /^[+-]?\d+(\.\d+)?$/;
    //if(!checkFloat.test(dd[i].value)) 
    if(!checkFloat.test(gradevalue))
    {
       alert("标度比例请输入数字!");
       return;
    }
    if(parseFloat(gradevalue)>1||parseFloat(gradevalue)<0)
    {
        alert("标度比例应该介于 [0] 和 [1] 之间!");
         return;
    }
    var top_value=khFieldForm.top_value.value;
    if(top_value!=null&&trim(top_value).length>0){
        if(!checkFloat.test(top_value))
        {
          alert("上限值请输入数字!");
          return;
        }
    }
    var bottom_value=khFieldForm.bottom_value.value;
    if(bottom_value!=null&&trim(bottom_value).length>0){
        if(!checkFloat.test(bottom_value))
        {
          alert("下限值请输入数字!");
          return;
        }
    }
     var hashvo=new ParameterSet();
     hashvo.setValue("subsys_id",khFieldForm.subsys_id.value);
     hashvo.setValue("grade_id",grade_id);
     hashvo.setValue("type",khFieldForm.type.value);
     hashvo.setValue("hiddenid",khFieldForm.hiddenGradeid.value);
     hashvo.setValue("saveandcontinue",saveandcontinue);
     var request=new Request({asynchronous:false,onSuccess:check_ok,functionId:'9021001010'},hashvo);
}
function check_ok(outparameters)
{
    var msg=outparameters.getValue("msg");
    var saveandcontinue=outparameters.getValue("saveandcontinue");
    if(msg=="1")
    {
        khFieldForm.action = "/performance/kh_system/kh_field/init_grade_template.do?b_save=save&isClose="+saveandcontinue;
        khFieldForm.submit();
    }
    else
    {
    alert(msg);
    return;
    }
}
function isClose(isClose)
{
   if(isClose =="1")
   {
      var obj= new Object();
      obj.isclose=isClose;
       obj.refresh = "2";
       parent.window.returnValue=obj;
       if(window.showModalDialog) {
           parent.window.close();
       }else{
           parent.window.opener.window.addOrEdit_OK(obj);
           window.open("about:blank","_top").close();
       }
   }
}
 function closee(refresh)
{
   var obj = new Object();
   obj.refresh = refresh;
    parent.window.returnValue=obj;
    if(window.showModalDialog) {
        parent.window.close();
    }else{
        parent.window.opener.window.addOrEdit_OK(obj);
        window.open("about:blank","_top").close();
    }
}
//-->
</script>
<html:form action="/performance/kh_system/kh_field/init_grade_template">
<table width="484" style="margin-left:3px;margin-top:5px;" border="0" cellspacing="0"  align="left" cellpadding="0">
<tr height="35">
			<td align='left' class='TableRow_lrt'>
				<logic:equal name="khFieldForm" property="type" value="1"><bean:message key="kh.field.bdnew"/></logic:equal>
<logic:equal name="khFieldForm" property="type" value="2"><bean:message key="kh.field.bdedit"/></logic:equal>
			</td>
		</tr>
		<tr>
			<td align="center" colspan="4" class="framestyle" height="120px">
<table width="100%" border="0" cellspacing="0"  align="center" cellpadding="5" class="DetailTable">
<colgroup>
	<col width="80">
</colgroup>
<tr height="35">
<td align="right">
<bean:message key="kh.field.code"/>
</td>
<td>
<html:text name="khFieldForm" property="grade_id" size="30" maxlength="1" styleClass="inputtext"/>
</td>
</tr>
<tr height="35">
<td align="right">
<bean:message key="kh.field.content"/>
</td>
<td>
<html:textarea name="khFieldForm" property="gradedesc" rows="6" cols="29"></html:textarea>
</td>
</tr>
<tr height="35">
<td align="right">
<bean:message key="kh.field.scale"/>
</td>
<td>
<html:text name="khFieldForm" property="gradevalue" size="30" maxlength="10" styleClass="inputtext"/>
</td>
</tr>
<tr height="35">
<td align="right">
<bean:message key="kh.field.topv"/>
</td>
<td>
<html:text name="khFieldForm" property="top_value" size="30" maxlength="10" styleClass="inputtext"/>
</td>
</tr>
<tr height="35">
<td align="right">
<bean:message key="kh.field.bottomv"/>
</td>
<td>
<html:text name="khFieldForm" property="bottom_value" size="30" maxlength="10" styleClass="inputtext"/>
</td>
</tr>
</table>
</td>
</tr>
<tr>
<td align="center" colspan="4">
<html:hidden name="khFieldForm" property="subsys_id"/>
<html:hidden name="khFieldForm" property="type"/>
<html:hidden name="khFieldForm" property="hiddenGradeid"/>
<table width="100%" border="0" cellspacing="0"  align="center" cellpadding="0" style="margin-top:7px;">
<tr>
<td align="center">
<input type="button" class="mybutton" onclick="check('1');" name="sav" value="<bean:message key="button.save"/>"/>
<logic:equal value="1" name="khFieldForm" property="type">
<input type="button" class="mybutton" onclick="check('2');" name="sAndC" value="<bean:message key="button.savereturn"/>"/>
</logic:equal>
<input type="button" class="mybutton" onclick="closee('${khFieldForm.isrefresh}');" name="clo" value="<bean:message key="button.cancel"/>"/>
</td>
</tr>
</table>
</td>
</tr>
</table>
</html:form>
<script type="text/javascript">
<!--
isClose("${khFieldForm.isClose}");
//-->
</script>