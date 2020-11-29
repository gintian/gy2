<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<script language="Javascript" src="/gz/salary.js"></script>
  <style>
  	.RecordRow_self {
	border: inset 0px #94B6E6;
	BORDER-BOTTOM: #94B6E6 0pt solid; 
	BORDER-LEFT: #94B6E6 0pt solid; 
	BORDER-RIGHT: #94B6E6 0pt solid; 
	BORDER-TOP: #94B6E6 0pt solid;
	font-size: 12px;
	text-align:center;
	} 
  </style>
<script type="text/javascript" src="/general/sys/hjaxmanage.js"></script>
<script type="text/javascript">
<!--
function gzemail_addattachok(type)
{
 
  var path=gzEmailForm.file.value;
  if(path==null||trim(path).length==0)
  {
     alert("请选择文件后再确定");
     return;
  }
  //var fso=new ActiveXObject("Scripting.FileSystemObject");   
  //if(!fso.FileExists(path))
 // {
   //  alert("文件不存在");
   //  return;
 // }    
  //gzEmailForm.path.value=gzEmailForm.file.value;
  if(validateUploadFilePath(path)!=true){
  		return false;
  }
  //兼容谷歌 不校验 wangbs 20190321
  if(getBrowseVersion()){
      if(!AxManager.setup(null, 'FileView', 0, 0))
          return false;
      var  obj=document.getElementById('FileView');
      if (obj != null)
      {
          var facSize=obj.GetFileSize(path);
          if(parseInt(facSize)==-1)
          {
              alert("文件不存在，请输入正确的文件路径！");
              return;
          }
      }
  }
  document.getElementsByName('ok')[0].disabled="disabled";
  document.getElementsByName('oas')[0].disabled="disabled";
  gzEmailForm.action="/general/email_template/save_gzemail_attach.do?b_save=save&type="+type;
  gzEmailForm.submit();
}
function attach_is_close(param)
{
   if(parseInt(param)==1)
   {
   	  winClose();
   }
   if(parseInt(param)==2)
   {
      
   }
}
function delete_attach(id,templateId)
{
  if(ifdel())
  {
 gzEmailForm.action="/general/email_template/takeboat_attach.do?b_delete=delete&id="+id+"&templateId="+templateId;
 gzEmailForm.submit();
  }
  else
  {
  return;
  }

}
function winClose(){
    if(parent.parent.selectAttachReturn){
        parent.parent.selectAttachReturn();
    }else{
    	window.close();
    }
}
//-->
</script>
<html:form action="/general/email_template/takeboat_attach" enctype="multipart/form-data" method="post">
<table width='440' height="300" cellspacing="0"  align="center" cellpadding="0" class="complex_border_color">
<tr><td class="common_border_color" height="100%">
<table width='100%' border="0" cellspacing="0"  align="center" cellpadding="0" style="border-collapse:collapse;">
<tr height="20px">
<td align="center" colspan="2" nowrap valign="top" style="padding-top:5px;">
单击"浏览"选择文件,或在下面的框中键入文件的路径
</td>
</tr>
<tr height="20px">
<td align="right" nowrap width="70px;">
查找文件
</td>
<td align="left" nowrap>
<input type="file" name="file" size='35' style="margin-left:5px;" class="text4"/>
<html:hidden name="gzEmailForm" property="id"/>
<input type="hidden" name="path" value=""/>
</td>
</tr>
</table>
</td></tr>
<tr><td align="left" valign="top" height="30">
<table width="430px;" border="0" cellspacing="0"  align="center" cellpadding="0" class="ListTable" style="margin-top:5px;border-collapse:collapse;">
<THEAD>
<tr height="20px"> 
<td align="center" class="TableRow" nowrap style="width:280px;border-bottom:none;">
附件
</td>
<td align="center" class="TableRow" nowrap style="width:82px;border-bottom:none;">
大小
</td>
<td align="center" class="TableRow" nowrap style="border-bottom:none;">
删除
</td>
</tr>
</THEAD>
</table>
</td></tr>
<tr><td align="left" valign="top" height="230" style="padding-left:4px;">
<div style="height:220;width:430;overflow-y:auto;position: absolute;border:1px solid #C4D8EE;" class="common_border_color">
<table width='100%' border="0" cellspacing="0"  align="center" cellpadding="0" style="border-collapse:collapse;margin-top:-1;">
<logic:iterate id="element" name="gzEmailForm" property="attachlist" offset="0">
<tr height="20px">
<logic:equal name="element" property="istotal" value="no">
<td align="left" class="RecordRow" style="width:280px;border-left:none;">
</logic:equal>
<logic:equal name="element" property="istotal" value="yes">
<td align="right" class="RecordRow" style="width:280px;border-left:none;">
</logic:equal>
<bean:write name="element" property="fileName"/>
</td>
<td align="right" class="RecordRow" style="width:82px;">
<bean:write name="element" property="fileLength"/>
</td>
<td align="center" class="RecordRow" style="border-right:none;">
<logic:equal name="element" property="istotal" value="no">
<IMG src="/images/delete.gif" border="0" style="cursor:hand" alt="删除附件" onclick="delete_attach('<bean:write name="element" property="id"/>','<bean:write name="element" property="templateId"/>');"> 
</logic:equal>
</td>
</tr>
</logic:iterate>
</table>
</div>
</td></tr></table>
<table width="440">
	<TR height="35px">
<td colspan="2" align="center" nowrap>
<input type="button" class="mybutton" name="ok" value="<bean:message key="button.ok"/>" onclick="gzemail_addattachok('1');"/>
&nbsp;
<input type="button" class="mybutton" name="oas" value="确定并附加另一个" onclick="gzemail_addattachok('2');"/>
&nbsp;
<input type="button" class="mybutton" name="can" value="<bean:message key="button.cancel"/>" onclick="winClose();"/>

</td>
</TR>
</table>
</html:form>
<script type="text/javascript">
<!--
attach_is_close("${gzEmailForm.isok}");
//-->
var file = document.getElementsByName('file')[0];
if(getBrowseVersion()){
	file.style.lineHeight='18px';
}else{
	file.style.height='24px';
}
</script>