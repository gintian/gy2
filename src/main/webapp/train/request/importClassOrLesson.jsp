<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean"%>
<%@ taglib uri="/tags/struts-html" prefix="html"%>
<%@ taglib uri="/tags/struts-logic" prefix="logic"%>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms"%>
<html>
	<script language='javascript'>
	function tabler31(){
		var height=window.top.document.body.clientHeight;
		window.parent.Ext.getCmp("iframe_body2").hide();
	}
	tabler31();
	function returnback(){
		courseTrainForm.action="/train/request/trainsData.do?b_query=link&model=1&a_code=${courseTrainForm.a_code}";
		courseTrainForm.submit();
	}
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
    		courseTrainForm.action="/train/request/trainsData.do?b_importdata=link";
  			courseTrainForm.submit();
    	}
    	else
    	{
    		alert("导入的文件必须为excel格式");
    	}
    }

  </script>
	<body>
		<form name="courseTrainForm" method="post" action="/train/request/trainsData.do" enctype="multipart/form-data">
			<br>
			<br>
			<br>
			<br>
			<table border="0" cellspacing="0" align="center" cellpadding="0">
			<tr>
					<td align="center">
							<font size=2>提示：请用下载的Excel模板来导入数据！模板格式不允许修改！</font>
						</td>
					</tr>
					<tr height="10"><td>&nbsp;</td></tr>
					<tr>
					<td >
				<fieldset style="width: 500px;">
				<legend>
					选择导入文件
				</legend>
				<table border="0" cellspacing="0" align="center" cellpadding="0">
					<tr>
						<td width="400">
							<Br>
							<Br>
							 &nbsp;文件
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
			<br>
			<table border="0" cellspacing="0" align="center" cellpadding="0" style="width:50%;">
					<tr>
						<td align="center">
							<input type="button" name="b_update" value="<bean:message key='menu.gz.import'/>" class="mybutton"
								onClick="imports()">
								<input type="button" name="b_update" value="<bean:message key='button.return'/>" class="mybutton"
								onClick="returnback();">
						</td>
					</tr>
			</table>
		</form>
	</body>
</html>
