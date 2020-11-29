<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean"%>
<%@ taglib uri="/tags/struts-html" prefix="html"%>
<%@ taglib uri="/tags/struts-logic" prefix="logic"%>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms"%>
<script language="javascript" src="/js/validate.js"></script>
<html>
	<head>

	</head>
	<script language='javascript'>
    function imports(holidayType)
    {
    	var fileEx = FeastForm.file.value;
        if(fileEx == ""){
        	alert("请选择需要导入的文件!");
        	return ;
        }
       	if(!validateUploadFilePath(fileEx)){
       		alert("请选择正确的文件上传!");
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
    		var r=confirm("导入时会覆盖当前数据,您确定导入吗？");

    		if(r==false) return;
    		var waitInfo=eval("wait");	//显示进度条
		    waitInfo.style.display="block";
		    
			document.FeastForm.action="/kq/feast_manage/hols_manager.do?b_importdata=link&tablename=Q17&status=afterImport&holidayType="+holidayType;
  			document.FeastForm.submit();
    	}
    	else
    	{
    		alert("导入的文件必须为excel格式");
    	}
    }
    
  </script>
  <% String status=request.getParameter("status");%>
  
	<body>
	<%
	  String holidayType=request.getParameter("holidayType");
    %>
	 
		<form name="FeastForm" method="post"
			action="/kq/feast_manage/hols_manager.do?b_importdata=link" enctype="multipart/form-data">
			<div  style="width: 488px;height: 22px;BORDER-BOTTOM: none; BORDER-LEFT: #C4D8EE 1pt solid; BORDER-RIGHT: #C4D8EE 1pt solid; BORDER-TOP: #C4D8EE 1pt solid;text-align: center;padding-top: 4px;"
					class="TableRow common_background_color ">
			    <b>导入数据</b>
			</div>
			<div style="width: 488px;height: 380px;border: #C4D8EE 1pt solid; " class="common_border_color">
			<br>

			<table border="0" cellspacing="0" align="center" cellpadding="0">
			<tr>
					<td align="center">
							<font-size=2>提示：请使用下载的Excel模板来导入数据！模板格式不允许修改！</font-size>
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
						<td width="400" style="text-align: center;vertical-align:middle">
							<Br>
							<span style="vertical-align:middle">文件</span>
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
								onClick="imports('<%=holidayType %>')">
								<input type="button" name="b_update" value="<bean:message key='button.cancel'/>" class="mybutton"
								onClick="window.close();">
						</td>
					</tr>
			</table>
			
		</form>
		

	</body>
</html>

<%-- 进度条 --%>
<div id='wait' style='position:absolute;top:200;left:50;display:none;text-align: center;display: none;'>
	<table border="1" width="400" cellspacing="0" cellpadding="4"
		class="table_style" height="87" align="center">
		<tr>

			<td class="td_style common_background_color" height=24>
				<bean:message key="classdata.getdata.wiat" />
			</td>

		</tr>
		<tr>
			<td style="font-size: 12px; line-height: 200%" align=center>
				<marquee class="marquee_style" direction="right" width="300"
					scrollamount="5" scrolldelay="10">
					<table cellspacing="1" cellpadding="0">
						<tr height=8>
							<td bgcolor=#3399FF width=8></td>
							<td></td>
							<td bgcolor=#3399FF width=8></td>
							<td></td>
							<td bgcolor=#3399FF width=8></td>
							<td></td>
							<td bgcolor=#3399FF width=8></td>
							<td></td>
						</tr>
					</table>
				</marquee>
			</td>
		</tr>
	</table>
</div>

<script type="text/javascript">
    function closeWindow(status){
        if("afterImport"==status){
            <logic:notEmpty name="feastForm" property="importMsg">
              var importMsg = "<bean:write name="feastForm" property="importMsg"/>"
              alert(importMsg);
            </logic:notEmpty> 
            window.returnValue=true;
            window.top.close();         
        }
    }

    closeWindow("<%=status %>");
</script>