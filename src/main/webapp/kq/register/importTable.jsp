<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean"%>
<%@ taglib uri="/tags/struts-html" prefix="html"%>
<%@ taglib uri="/tags/struts-logic" prefix="logic"%>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms"%>
<script language="javascript" src="/js/validate.js"></script>
<hrms:themes />
<html>
	<head>

	</head>
	<script language='javascript'>
	function back()
	{
		window.location.href = "/kq/register/dailyregister.jsp";
	}
	
    function imports()
    {
    	 var fileEx = dailyRegisterForm.file.value;
        if(fileEx == ""){
        	alert("请选择需导入的文件!");
        	return ;
        }
       if(!validateUploadFilePath(fileEx)){
			alert("请选择正确的文件！");
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
    		var waitInfo=eval("wait");	
	  		waitInfo.style.display="block";
    		 dailyRegisterForm.action="/kq/register/daily_register.do?b_importTemplate<%=request.getParameter("tablename")%>=like&tablename=<%=request.getParameter("tablename")%>";
    		 dailyRegisterForm.submit(); 		
    	}
    	else
    	{
    		alert("导入的文件必须为excel格式");
    	}
    }

  </script>
	<body>
		<form name="dailyRegisterForm" method="post"
			action="/kq/register/daily_register.do" enctype="multipart/form-data">
			<br />
			<br />
			<table border="0" cellspacing="0" align="center" cellpadding="0" style="margin-top: 50px">
				
				<tr>
					<td>
						<br/>
					</td>
				</tr>
				<tr>
					<td align="center" width="">
						<fieldset style="width: 100%;">
							<legend>
								选择导入文件
							</legend>
							<table border="0" cellspacing="0" align="center" cellpadding="0">
								<tr>
									<td width="400">
										<Br>
										&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;文件
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
				<tr>
					<td align="left">
							<br>
							<font-size=2>提示：请用下载的Excel模板来导入数据！模板格式不允许修改！</font-size>
						</td>
					</tr>
				
				<tr>
					<td align="center">
					<br>
						<input type="button" name="b_update" value="上传" class="mybutton"
							onClick="imports()">
						<input type="button" name="b_update" value="返回" class="mybutton"
							onClick="back();">
					</td>
				</tr>
			</table>

		</form>
	</body>
</html>
<div id='wait' style='text-align: center;display: none;'>
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
