<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean"%>
<%@ taglib uri="/tags/struts-html" prefix="html"%>
<%@ taglib uri="/tags/struts-logic" prefix="logic"%>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms"%>
<html>
	<head>

	</head>
	<script language='javascript'>
	function goback()
	{
		bonusForm.action='/gz/bonus/inform.do?b_query=link&a_code=${bonusForm.a_code}';
		bonusForm.submit();
	}
    function imports()
    {
    	var fileEx = bonusForm.file.value;
        if(fileEx == ""){
        	alert("请选择需导入的文件!");
        	return ;
        }
       if(!validateUploadFilePath(fileEx))
           return;
       	flag=true;
		var temp=fileEx;
		while(flag)
    	{
	    	temp=temp.substring(temp.indexOf(".")+1)
	    	if(temp.indexOf(".")==-1)
	    		flag=false;
    	}
    	if(temp.toLowerCase()=='xls' || temp.toLowerCase()=='xlsx')
    	{
    		bonusForm.action='/gz/bonus/inform.do?b_getTemplData=link&a_code=${bonusForm.a_code}';
			bonusForm.submit();
    	}
    	else
    	{
    		alert("导入的文件必须为excel格式");
    	}
    }

  </script>
	<body>
		<form name="bonusForm" method="post" action="/gz/bonus/inform.do"
			enctype="multipart/form-data">
			<br>
			<br>
			<fieldset align="center" style="width:50%;">
				<legend>
					选择导入文件
				</legend>
				<table border="0" cellspacing="0" align="center" cellpadding="0">
					<tr>
						<td width="400">
							<Br>
							文件
							<input type="file" name="file" size="40">
							<br>
							<br>

						</td>
					</tr>
					<tr>
						<td>
							&nbsp;
						</td>
					</tr>
					<tr>
						<td align="center">
							<input type="button" name="b_update" value="上传" class="mybutton"
								onClick="imports()">
							<input type="button" name="b_update" value="返回" class="mybutton"
								onClick="goback()">
						</td>
					</tr>
				</table>
			</fieldset>
			<p>
			<table border="0" cellspacing="0" align="center" cellpadding="0" style="width:50%;">
					<tr>
						<td width="400">
							<font size=2>注：请用下载的Excel模板来导入数据！模板格式不允许修改！</font>
						</td>
					</tr>
			</table>
		</form>
	</body>
</html>
