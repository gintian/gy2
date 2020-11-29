<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean"%>
<%@ taglib uri="/tags/struts-html" prefix="html"%>
<%@ taglib uri="/tags/struts-logic" prefix="logic"%>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms"%>
<html>

	<head>

	</head>
	<script type="text/javascript">
	function goback()
	{
		document.achievementTaskForm.action="/performance/achivement/achivementTask.do?b_search=query&target_id="+"${achievementTaskForm.target_id}";
		document.achievementTaskForm.target="mil_body";
		document.achievementTaskForm.submit();
	}
	function imports()
	{
		var filetem=achievementTaskForm.file.value;
		if(filetem=='')
		{
			alert("请选择需导入的文件!");
        	return ;
		}
		
		// 防止上传漏洞
		var isRightPath = validateUploadFilePath(filetem);
		if(!isRightPath)	
			return;
		
		flag=true;
		var temp=filetem;
		while(flag)
		{
			temp=temp.substring(temp.indexOf(".")+1);
			if(temp.indexOf(".")==-1)
			{
				flag=false;
			}
		}
		if(temp.toLowerCase()=='xls'||temp.toLowerCase()=='xlsx')
		{
			document.achievementTaskForm.action="/performance/achivement/achivementTask.do?b_getTemplData=get&target_id="+"${achievementTaskForm.target_id}";
			document.achievementTaskForm.submit();
		}else
		{
			alert("导入的文件必须为Excel格式");
		}
	}
	</script>
	<body>
		<form name="achievementTaskForm" method="post"
			action="/performance/achivement/achivementTask.do" enctype="multipart/form-data">
			<br>
			<br>
			<table border="0" cellspacing="0" align="center" cellpadding="0" style="width:50%;">
					<tr>
						<td width="400" align="center">
							<font size=2>说明：请用下载的Excel模板来导入数据！模板格式不允许修改！</font>
						</td>
					</tr>
			</table>   
			<fieldset align="center" style="width:700px;margin:0 auto;">
				<legend>
					选择导入文件
				</legend>
				<table border="0" cellspacing="0" align="center" cellpadding="0">
					<tr>
						<td width="400">
							<Br>
							文件
							<input type="file" name="file"  size="40" class="inputtext">
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

			<table border="0" cellspacing="5" align="center" cellpadding="0">
					<tr>
						<td align="center">
							<input type="button" name="b_update" value="上传" class="mybutton" onclick="imports()"/>
							<input type="button" name="b_update" value="返回" class="mybutton" onclick="goback()"/>
						</td>
					</tr>
			</table>         				     
		</form>
	</body>

<script>
<% if(request.getParameter("b_getTemplData")!=null){%>
	if('${achievementTaskForm.errorname}'=='')
	{
		alert('成功导入${achievementTaskForm.okcount}条！');
			goback();
	}else
	{
			var outName="${achievementTaskForm.errorname}";
		//	outName=getDecodeStr(outName);
		//	var name=outName.substring(0,outName.length-1)+".xls";
		//	name=getEncodeStr(name);
  			window.location.target="_blank";
			window.location.href = "/servlet/DisplayOleContent?filename="+outName;
			window.setTimeout("goback()",1000);
			
	}
<%}%>
</script>
</html>