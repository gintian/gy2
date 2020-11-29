<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean"%>
<%@ taglib uri="/tags/struts-html" prefix="html"%>
<%@ taglib uri="/tags/struts-logic" prefix="logic"%>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms"%>
<style>
.divstyle{
	width: 488px;
	height: 22px;
	BORDER-BOTTOM: none; 
	BORDER-LEFT: #C4D8EE 1pt solid; 
	BORDER-RIGHT: #C4D8EE 1pt solid; 
	BORDER-TOP: #C4D8EE 1pt solid;
	background-color: #F4F7F7;
	text-align: center;
	padding-top: 4px;
}
.divstyle1{
	width: 488px;
	height: 380px;
	border: #C4D8EE 1pt solid; 
}
</style>
<html>
	<head>

	</head>
	<script language='javascript'>
    function imports()
    {
    	var fileEx = courseTrainForm.file.value;
        if(fileEx == ""){
        	alert("请选择需导入的文件!");
        	return ;
        }

        if(!validateUploadFilePath(fileEx)){
            alert(ERROR_FILE_UPLOADFAIL);
            return;
        }
            
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
    		document.courseTrainForm.action="/train/request/import.do?b_importdata=link&r3101=${courseTrainForm.r3101}";
  			document.courseTrainForm.submit();
    	}
    	else
    	{
    		alert("导入的文件必须为excel格式");
    	}
    }
    

  </script>
  <hrms:themes/>
	<body>
		<form name="courseTrainForm" method="post"
			action="/train/request/import.do?b_importdata=link" enctype="multipart/form-data">
			<div class="divstyle common_background_color common_border_color" style="margin-top: 10px;"><b>导入培训学员</b></div>
			<div class="divstyle1 common_border_color">
			<br>
			<br>
			<br>
			<br>
			<br>
			<br>
			<table border="0" cellspacing="0" align="center" cellpadding="0">
			<tr>
					<td align="center">
							<font-size=2>提示：请用下载的Excel模板来导入数据！模板格式不允许修改！</font-size>
						</td>
					</tr>
					<tr height="10"><td>&nbsp;</td></tr>
					<tr>
					<td >
				<fieldset>
				<legend>
					选择导入文件
				</legend>
				<table border="0" cellspacing="0" align="center" cellpadding="0">
					<tr>
						<td width="410">
							<Br>
							&nbsp;&nbsp;文件
							<input type="file" name="file" size="40" class="text6">
							<br>
							<br>
						</td>
					</tr>
					<tr>
						<td>
							&nbsp;
						</td>
					</tr>
				</table>
			</fieldset>
						</td>
					</tr>
			</table>
			</div>
			<br>
			<table border="0" cellspacing="0" align="center" cellpadding="0" style="width:50%;">
					<tr>
						<td align="center">
							<input type="button" name="b_update" value="<bean:message key='menu.gz.import'/>" class="mybutton"
								onClick="imports()">
								<input type="button" name="b_update" value="<bean:message key='button.cancel'/>" class="mybutton"
								onClick="window.close();">
						</td>
					</tr>
			</table>
			
		</form>
		
	</body>
</html>
