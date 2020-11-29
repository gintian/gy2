<%@ page contentType="text/html; charset=UTF-8" language="java"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean"%>
<%@ taglib uri="/tags/struts-html" prefix="html"%>
<%@ taglib uri="/tags/struts-logic" prefix="logic"%>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms"%>
<link rel="stylesheet" href="/css/css0.css" type="text/css">
<script language="JavaScript">
function upload(){
    var fileEx = croPayMentForm.picturefile.value;
    if(fileEx == ""){
        alert("请选择excel文件!");
        return ;
    }
     if(!validateUploadFilePath(fileEx))
           return;
    var fileEx1=fileEx.substring(fileEx.lastIndexOf(".")+1).toLowerCase();
    if(fileEx1=="xls" || fileEx1=="xlsx"){
	    //document.croPayMentForm.action="/gz/gz_amount/gropayment.do?b_inexport=link&saveflag=save";
		document.croPayMentForm.submit(); 
     }else{
        alert("导入的文件必须为Excel格式!");
        return;
    }
}
</script>
<base id="mybase" target="_self">
<form name="croPayMentForm" method="post" action="/gz/gz_amount/gropayment.do?b_inexport=link&saveflag=save&isclose=link" enctype="multipart/form-data" >

<table width="490px;" border="0" style="margin-left:-3px;">
		<tr>
			<td align="center">
		提示：请用下载的Excel模板来导入数据！模板格式不允许修改！
<fieldset align="center" style="width:100%;">
	<legend>请选择导入文件</legend> 
	<table width="100%" border="0">
		<tr>
			<td align="center">&nbsp;</td>
		</tr>
		<tr>
			<td align="center">
				<html:file name="croPayMentForm" property="picturefile"
					styleClass="text6" size="30" />
			</td>
		</tr>
		<tr>
			<td align="center">&nbsp;</td>
		</tr>
		
	</table>
</fieldset>
</td>
</tr>
<tr>
			<td align="center">
				<input type="button" value="确定" class="mybutton" onclick="upload();">
				<input type="button" value="取消" class="mybutton" onclick="window.close();">
			</td>
		</tr>
</table>
</form>
<logic:equal name="croPayMentForm" property="checkClose" value="close">
<script language="JavaScript">
window.close();
returnValue="ok";
</script>
</logic:equal>
<logic:equal name="croPayMentForm" property="checkClose" value="alert">
<script language="JavaScript">
alert("${croPayMentForm.checkflag}");
</script>
</logic:equal>
