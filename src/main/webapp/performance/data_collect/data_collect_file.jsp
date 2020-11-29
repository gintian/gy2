<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<script type="text/javascript">
var info=dialogArguments; 
<!--
function ret()
{
   var dir=data_collectForm.formfile.value;
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
   document.getElementById("fieldsetid").value=info[0];
   data_collectForm.action="/performance/data_collect/data_collect.do?b_upload=upload&opt=2";
   data_collectForm.submit();
}
function initwindow()
{
   <%if(request.getParameter("opt")!=null&&request.getParameter("opt").equals("0")){%>
     
   <%}
   else if(request.getParameter("opt")!=null&&request.getParameter("opt").equals("2")){
   %>
      var info="${data_collectForm.info}";
      var returnInfo="${data_collectForm.returnInfo}";
      if(info=="0"){
      var obj = new Object();
      obj.fresh="1";
          parent.window.returnValue=obj;
          parent.window.close();

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
    parent.window.returnValue=obj;
   window.close();
}
//-->
</script>
<html:form action="/performance/data_collect/data_collect" enctype="multipart/form-data">
<br>
<br/>
<br/>
<br/>
<table width="80%" border="0" cellspacing="1"  align="center" cellpadding="1">
<tr>
<td align="center" nowrap>
<fieldset align="center">
<legend>请选择要导入的文件</legend>
<table width="100%" border="0" cellspacing="1"  align="center" cellpadding="1">
<tr>
<td align="center" nowrap>
&nbsp;
</td>
</tr>
<tr>
<td align="center" nowrap>
<input type="file" name="formfile" size="30"/>
<input type="hidden" name="fieldsetid" value=""/>
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