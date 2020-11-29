<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<script type="text/javascript">
<!--
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
   /* 安全问题 文件上传 财务凭证定义-设置-导入 xiaoyun 2014-9-16 start */
   //financial_voucherForm.action="/gz/voucher/financial_voucher.do?b_upload=upload&opt=2";
   financial_voucherForm.action="/gz/voucher/financial_voucher.do?b_upload=upload&opt=2&isclose=link";
   /* 安全问题 文件上传 财务凭证定义-设置-导入 xiaoyun 2014-9-16 end */
   financial_voucherForm.submit();
}
function initwindow()
{
   <%if(request.getParameter("opt")!=null&&request.getParameter("opt").equals("0")){%>
     
   <%}
   else if(request.getParameter("opt")!=null&&request.getParameter("opt").equals("2")){
   %>
      var info="${financial_voucherForm.info}";
      var returnInfo="${financial_voucherForm.returnInfo}";
      if(info=="0"){
      var obj = new Object();
      obj.fresh="1";
      returnValue=obj;
      window.close();
      }
      else
      {
          alert(returnInfo);
      }
  <%}%>
}
function windowClose()
{
   var obj = new Object();
   obj.fresh="0";
   returnValue=obj;
   window.close();
}
//-->
</script>
<html:form action="/gz/voucher/financial_voucher" enctype="multipart/form-data">

<table width="390px" border="0" cellspacing="1"  align="center" cellpadding="1" style="margin-top:10px;">
<tr>
<td align="center" nowrap>
<fieldset align="center">
<legend>请选择要导入的会计科目表文件</legend>
<table width="100%" border="0" cellspacing="1"  align="center" cellpadding="1">
<tr>
<td align="center" nowrap>
&nbsp;
</td>
</tr>
<tr>
<td align="center" nowrap>
<input type="file" name="formfile" size="30" class="inputtext"/>
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
<input type="button" name="ok" value="确定" onclick="ret();" class="mybutton"/>
<input type="button" name="cancel" value="取消" onclick="windowClose();" class="mybutton"/>
</td>
</tr>
</table>
<script type="text/javascript">
<!--
initwindow();
//-->
</script>
</html:form>