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
		document.templateListForm.action="/general/template/templatelist.do?b_query=link&tabid=${templateListForm.tabid}";
		document.templateListForm.submit();
		
	}
    
    function imports()
    {
    	 var fileEx = templateListForm.file.value;
        if(fileEx == ""){
        	alert("请选择需导入的文件!");
        	return ;
        }
       // 防止上传漏洞
		var isRightPath = validateUploadFilePath(fileEx);
		if(!isRightPath)	
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
    				document.getElementById("b_update").disabled="true";
    				document.templateListForm.action="/general/template/templatelist.do?b_getImportData=get&tabid=<%=request.getParameter("tabid")%>";
  					document.templateListForm.submit();
  				
    	}
    	else
    	{
    		alert("导入的文件必须为excel格式");
    	}
    	//var outName='${templateListForm.errorFileName}';
            //alert(outName);
			/* 薪资发放-导入数据 出现空白页面 xiaoyun 2014-9-22 start */
			//outName=getDecodeStr(outName);
			//var name=outName.substring(0,outName.length-1)+".xls";
			//name=getEncodeStr(name);
			/* 薪资发放-导入数据 出现空白页面 xiaoyun 2014-9-22 end */
		//var win=open("/servlet/DisplayOleContent?filename="+outName,"excel");
    }

  </script>
	<body>
		<form name="templateListForm" method="post"
			action="/general/template/templatelist.do" enctype="multipart/form-data">
			<br>
			<br>
				 
			 			<table  width='50%' align='center'  ><tr><td  align='left'>
							<font size=2  >说明：请用下载的Excel模板来导入数据！模板格式不允许修改！</font>
						</td></tr></table>
				 
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
							<input type="file" name="file" size="40" class="TEXT4">
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
          	<p>
			<table border="0" cellspacing="0" align="center" cellpadding="0" style="width:50%;">
						<tr>
						<td align="center">
							<input type="button" name="b_update" id="b_update" value="上传" class="mybutton"
								onClick="imports()">
							<input type="button" name="b_update2" value="返回" class="mybutton"
								onClick="goback()">
						</td>
					</tr>
					<tr>
						<td width="400">
							&nbsp;
						</td>
					</tr>
					
			</table>
		</form>
	</body>
</html>
<script>
var oName='${templateListForm.onlyname}';
		<%if(request.getParameter("b_getImportData")!=null){%>
			var outName='${templateListForm.errorFileName}';
			var updateCount='${templateListForm.updateCount}';
			/* 薪资发放-导入数据 出现空白页面 xiaoyun 2014-9-22 start */
			//outName=getDecodeStr(outName);
			//var name=outName.substring(0,outName.length-1)+".xls";
			//name=getEncodeStr(name);
			/* 薪资发放-导入数据 出现空白页面 xiaoyun 2014-9-22 end */
			if(oName!="no"&&outName!=""){//有唯一标识并且有错误提示 为
				var win=open("/servlet/DisplayOleContent?filename="+outName,"excel");
		    }else{
		    	alert("成功导入"+updateCount+"条数据。");
		    	document.templateListForm.action="/general/template/templatelist.do?b_query=init&tabid=<%=request.getParameter("tabid")%>";
  				document.templateListForm.submit();
		    }
		    
	<%}%>
	
</script>
