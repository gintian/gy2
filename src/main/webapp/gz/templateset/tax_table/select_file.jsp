<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<script type="text/javascript">
<!--
function ret()
{
   var dir=taxDetailTableForm.formfile.value;
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
   /* 模式窗体返回窗按钮问题 xiaoyun 2014-9-16 start */
   //taxDetailTableForm.action="/gz/templateset/tax_table/initTaxTable.do?b_upload=upload&opt=2";
   taxDetailTableForm.action="/gz/templateset/tax_table/initTaxTable.do?b_validate";
   /* 模式窗体返回窗按钮问题 xiaoyun 2014-9-16 end */
   taxDetailTableForm.submit();
}
	function goback()
	{
		taxDetailTableForm.action="/gz/templateset/tax_table/initTaxTable.do?b_init=init";
		taxDetailTableForm.submit();
	}
function initwindow()
{
	<%if(request.getParameter("b_upload")!=null&&request.getParameter("b_upload").equals("upload")){%>
		goback();
	<%}%>
}
</script>
<html:form action="/gz/templateset/tax_table/initTaxTable" enctype="multipart/form-data">
<br>
<br>
<table width="50%" border="0" cellspacing="1"  align="center" cellpadding="1">
<tr>
<td align="center" nowrap>
<fieldset align="center">
<legend><bean:message key="gz.select.taxname"/></legend>
<table width="100%" border="0" cellspacing="1"  align="center" cellpadding="1">
<tr>
<td align="center" nowrap>
&nbsp;
</td>
</tr>
<tr>
<td align="center" nowrap>
<input type="file" name="formfile" size="50" class="inputtext"/>
</td>
</tr>
<tr>
<td align="center" nowrap>
&nbsp;
</td>
</tr>
</table>
</fieldset>
</td>
</tr>
<tr>
<td align="center" style="padding-top:3px;" nowrap>
<input type="button" name="ok" value="<bean:message key="hire.jp.apply.upload"/>" onclick="ret();" class="mybutton"/>
<input type="reset" value="<bean:message key="button.clear"/>" class="mybutton">
<input type="button" name="b_update" value="<bean:message key="button.leave"/>" class="mybutton" onClick="goback()">	
</td>
</tr>
</table>
<script type="text/javascript">
initwindow();
</script>
</html:form>