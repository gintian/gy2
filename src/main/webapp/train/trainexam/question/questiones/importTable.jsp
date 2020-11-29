<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean"%>
<%@ taglib uri="/tags/struts-html" prefix="html"%>
<%@ taglib uri="/tags/struts-logic" prefix="logic"%>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms"%>
<style>
.divstyle {
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
</style>
<html>
	<head>

	</head>
	<script language='javascript'>
    function imports()
    {
    	 var fileEx = planTrainForm.file.value;
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
    		document.planTrainForm.action="/train/trainexam/question/import.do?b_importdata=link";
  			document.planTrainForm.submit();
    	}
    	else
    	{
    		alert("导入的文件必须为excel格式");
    	}
    }
    

  </script>
  <hrms:themes></hrms:themes>
	<body>
		<form name="planTrainForm" method="post"
			action="/train/trainexam/question/import.do?b_importdata=link"  enctype="multipart/form-data">
			<div class="divstyle tableRow" style="padding-top: 10px;" ><b>导入考试试题</b></div>
			<div style="width: 488px;height: 380px;border: 1pt solid;border-top: none; " class="common_border_color">
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
				<fieldset style="width: 400px;">
				<legend>
					选择导入文件
				</legend>
				<table border="0" cellspacing="0" align="center" cellpadding="0">
					<tr>
						<td width="90%" align="center">
							<Br>
							&nbsp;文件
							<input type="file" name="file" size="40" class="text6">&nbsp;
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
