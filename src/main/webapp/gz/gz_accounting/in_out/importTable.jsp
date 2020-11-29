<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean"%>
<%@ taglib uri="/tags/struts-html" prefix="html"%>
<%@ taglib uri="/tags/struts-logic" prefix="logic"%>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms"%>
<html>
	<head>

	</head>
	<script language='javascript'>
	var urlStr = "&returnFlag=${accountingForm.returnFlag}&theyear=${accountingForm.theyear}&themonth=${accountingForm.themonth}&orgcode=${accountingForm.operOrg}";
	function goback()
	{
		var oper = '<%=request.getParameter("oper")%>';	
		if(oper=='sp')  
			 // document.accountingForm.action="/gz/gz_accounting/gz_sp_orgtree.do?b_query=link&ori=0&salaryid=${accountingForm.salaryid}";
			document.accountingForm.action="/gz/gz_accounting/gz_sptable.do?b_query=link&import="+accountingForm.itemid.value+"&ori=0&salaryid=${accountingForm.salaryid}";
		else if (oper=='fafang')
			document.accountingForm.action="/gz/gz_accounting/gz_table.do?b_query=link&import="+accountingForm.itemid.value+"&salaryid=${accountingForm.salaryid}";
		else
		{
			//document.accountingForm.target="il_body";
			//document.accountingForm.action="/gz/gz_accounting/gz_org_tree.do?b_query=link&import="+accountingForm.itemid.value+"&salaryid=${accountingForm.salaryid}";
			document.accountingForm.action="/gz/gz_accounting/gz_table.do?b_query=link&import="+accountingForm.itemid.value+"&salaryid=${accountingForm.salaryid}";
		}
	   		 
		document.accountingForm.submit();
	}
	
	
	
    
    function imports()
    {
    	 var fileEx = accountingForm.file.value;
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
    		var oper = '<%=request.getParameter("oper")%>';
    		if('<%=request.getParameter("importTempl")%>'=='null')
    		{
    			document.accountingForm.action="/gz/gz_accounting/in_out.do?b_getImportData=get&import="+accountingForm.itemid.value+"&salaryid=<%=request.getParameter("salaryid")%>";
  				document.accountingForm.submit();
    		}
			else if(oper=='fafang')
			{
				//document.accountingForm.target="il_body";
				document.accountingForm.action="/gz/gz_accounting/in_out.do?b_getTemplData=get&importTempl=1&oper=fafang&import="+accountingForm.itemid.value+"&salaryid=<%=request.getParameter("salaryid")%>"+urlStr;
  				document.accountingForm.submit();
			} 
			else if(oper=='sp')
			{
				//document.accountingForm.target="il_body";
				document.accountingForm.action="/gz/gz_accounting/in_out.do?b_getTemplData2=get&importTempl=1&oper=sp&import="+accountingForm.itemid.value+"&salaryid=<%=request.getParameter("salaryid")%>";
  				document.accountingForm.submit();
			}    		
    	}
    	else
    	{
    		alert("导入的文件必须为excel格式");
    	}
    }
  </script>
	<body>
		<form name="accountingForm" method="post"
			action="/gz/gz_accounting/in_out.do" enctype="multipart/form-data">

			<%if(request.getParameter("oper")!=null && (request.getParameter("oper").equals("sp") || request.getParameter("oper").equals("fafang"))) {%>
          	
			<table border="0" cellspacing="0" align="center" cellpadding="0" style="width:700px;margin-top:60px;">
					<tr>
						<td align="center">
							<font size=2>说明：请用下载的Excel模板来导入数据！模板格式不允许修改！</font>
						</td>
					</tr>
			</table>
          <%} %>  
			<fieldset align="center" style="width:700px;">
				<legend>
					选择导入文件
				</legend>
				<table border="0" cellspacing="0" align="center" cellpadding="0">
					<tr>
						<td width="400">
							<Br>
							文件
							<input type="file" name="file"  size="40" class="complex_border_color">
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
			<table border="0" cellspacing="0" align="center" cellpadding="0" height="35px;">
					<tr>
						<td align="center">
							<input type="button" name="b_update" value="上传" class="mybutton"
								onClick="imports()">
							<input type="button" name="b_update" value="返回" class="mybutton"
								onClick="goback()">
						</td>
					</tr>
				</table>
          <html:hidden name="accountingForm" property="proright_str"/> 
          <html:hidden name="accountingForm"  property="itemid"/>                 
		</form>
	</body>
</html>
<script>
		<%if(request.getParameter("b_getTemplData")!=null || request.getParameter("b_getTemplData2")!=null){%>
		if('${accountingForm.errorFileName}'=='')
		{		
			alert('成功导入${accountingForm.okCount}条！');
			goback();
		}else
		{
			var outName='${accountingForm.errorFileName}';
			var fieldName = getDecodeStr(outName);
			var win=open("/servlet/vfsservlet?fileid="+fieldName+"&fromjavafolder=true","excel");
		}
	<%}%>
</script>