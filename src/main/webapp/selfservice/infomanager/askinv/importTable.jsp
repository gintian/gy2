<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean"%>
<%@ taglib uri="/tags/struts-html" prefix="html"%>
<%@ taglib uri="/tags/struts-logic" prefix="logic"%>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms"%>
<html>
	<head>

	</head>
	<hrms:themes></hrms:themes>
	<script language='javascript'>
    function imports()
    {
    	 var fileEx = topicForm.file.value;
        if(fileEx == ""){
        	alert("请选择需导入的文件!");
        	return ;
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
    		//planTrainForm.target="nil_body";
    		document.topicForm.action="/selfservice/infomanager/askinv/import.do?b_importdata=link";
  			document.topicForm.submit();
    	}
    	else
    	{
    		alert("导入的文件必须为excel格式");
    	}
    }
    

  </script>
	<body>
		<form name="topicForm" method="post"
			action="/train/trainexam/question/import.do?b_importdata=link" enctype="multipart/form-data">
			<div style="width: 490px;height: 30px;BORDER-BOTTOM: none; BORDER-LEFT: #C4D8EE 1pt solid; BORDER-RIGHT: #C4D8EE 1pt solid; BORDER-TOP: #C4D8EE 1pt solid;background-color: #F4F7F7;valign:middle;text-align: left;margin-top: 5px;" class="common_border_color common_background_color">
			<table>
			<tr>
			<td style="height:30px;margin-left:5px;" valign="middle">导入问卷调查</td>
			</tr>
			</table>
			</div>
			<div style="width: 490px;height: 150px;border: #C4D8EE 1pt solid; " class="common_border_color">
			<br>
			<table border="0" cellspacing="0" align="center" cellpadding="0">
			<tr>
					<td align="center">
							<font-size=2>提示：请用下载的Excel模板来导入数据！模板格式不允许修改！</font-size>
						</td>
					</tr>
					
					<tr>
					<td >
				<fieldset style="margin:5,0,5,0;">
				<legend>
					选择导入文件
				</legend>
				<table border="0" cellspacing="0" align="center" cellpadding="0">
					<tr>
						<td width="400" style="padding-left:5px;">
							<Br>
							文件
							<input type="file" name="file" size="40" class="text6">
							<br>
							<br>
						</td>
					</tr>
					
				</table>
			</fieldset>
						</td>
					</tr>
			</table>
			</div>
			<table border="0" cellspacing="0" align="center" cellpadding="0" style="width:50%;">
					<tr>
						<td align="center" height="35px;">
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
