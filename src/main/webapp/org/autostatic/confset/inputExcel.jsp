<%@ page contentType="text/html; charset=UTF-8" language="java"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean"%>
<%@ taglib uri="/tags/struts-html" prefix="html"%>
<%@ taglib uri="/tags/struts-logic" prefix="logic"%>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms"%>
<link rel="stylesheet" href="/css/css0.css" type="text/css">
<script language="JavaScript">
function upload(){
    var fileEx = subsetConfsetForm.picturefile.value;
    if(fileEx == ""){
        alert("请选择excel文件!");
        return ;
    }
    var fileEx1=fileEx.substring(fileEx.lastIndexOf(".")+1).toLowerCase();
    if(fileEx1=="xlsx"||fileEx1=="xls"){
		document.subsetConfsetForm.submit(); 
     }else{
        alert("您输入的excel格式不正确!");
        return;
    }
}
</script>
<base id="mybase" target="_self">
<form name="subsetConfsetForm" method="post" action="/org/autostatic/confset/datascan.do?b_inExport=link&saveflag=save&tablename=${subsetConfsetForm.tablename}" enctype="multipart/form-data" >
<table width="100%" border="0" style="margin-top: 20px">
		<tr>
			<td align="center">
<fieldset align="center" style="width:80%;">
	<legend>导入数据</legend> 
	<table width="100%" border="0">
		<tr>
			<td align="center">&nbsp;</td>
		</tr>
		<tr>
			<td align="center">
				<html:file name="subsetConfsetForm" property="picturefile"
					styleClass="text6" size="20" />
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
<logic:equal name="subsetConfsetForm" property="checkClose" value="close">
<script language="JavaScript">
window.close();
returnValue="ok";
</script>
</logic:equal>
<logic:equal name="subsetConfsetForm" property="checkClose" value="alert">
<script language="JavaScript">
alert("${subsetConfsetForm.checkflag}");
</script>
</logic:equal>
