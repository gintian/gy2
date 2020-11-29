<%@ page contentType="text/html; charset=UTF-8"%>
<%@page import="com.hjsj.hrms.actionform.kq.machine.KqCardDataForm"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean"%>
<%@ taglib uri="/tags/struts-html" prefix="html"%>
<%@ taglib uri="/tags/struts-logic" prefix="logic"%>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms"%>
<script language="javascript" src="/js/validate.js"></script>
<html>
	<head>

	</head>
	<script language='javascript'>
    function imports()
    {
    	var fileEx = KqCardDataForm.file.value;
        if(fileEx == ""){
        	alert("请选择需要导入的文件!");
        	return ;
        }
       if(!validateUploadFilePath(fileEx)){
	       	alert("请上传正确的文件!");
	    	return ;
        }
       	flag=true;
		var temp=fileEx;
		while(flag)
    	{
	    	temp=temp.substring(temp.indexOf(".")+1);
	    	if(temp.indexOf(".")==-1)
	    		flag=false;
    	}
    	if(temp.toLowerCase()=='xls' || temp.toLowerCase()=='xlsx' || temp.toLowerCase()=='et')
    	{
    	var r=(confirm("确定导入数据？"));

    		if(r==false) 
        		return;

			document.KqCardDataForm.action="/kq/machine/search_card_data.do?b_impcard=link";
  			document.KqCardDataForm.submit();
  			document.getElementById("wait").style.display = "";

    	}
    	else
    	{
    		alert("导入的文件必须为excel格式");
    	}
    } 
    function turnBack(){
		window.location.href = "/kq/machine/search_card_data.do?b_search=link";
    }
  </script>
	<body>
		<form name="KqCardDataForm" method="post"
			action="/kq/machine/search_card_data.do?b_impcard=link" enctype="multipart/form-data">
			<br>

			<table border="0" cellspacing="0" align="center" cellpadding="0">
					<tr height="10"><td>&nbsp;</td></tr>
					<tr>
					<td >
				<fieldset>
				<legend>
					选择导入文件
				</legend>
				<table border="0" cellspacing="0" align="center" cellpadding="0">
					<tr>
						<td width="400" align="center">
							<Br>
							文件
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
			</table>
			<br>
			<table border="0" cellspacing="0" align="center" cellpadding="0" style="width:50%;">
					<tr>
						<td align="center">
							<input type="button" name="b_update" value="<bean:message key='menu.gz.import'/>" class="mybutton"
								onClick="imports()">
								<input type="button" name="b_update" value="<bean:message key='button.return'/>" class="mybutton"
								onClick="turnBack();">
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

<script type="text/javascript">
    function showMess(){
        <logic:notEmpty name="kqCardDataForm" property="error_message">
        	var error_message = "<bean:write name="kqCardDataForm" property="error_message"/>"
        	document.getElementById("wait").style.display = "none";
        	alert(error_message);   
	        document.KqCardDataForm.action="/kq/machine/search_card_data.do?b_search=link";
			document.KqCardDataForm.submit();
        </logic:notEmpty>
        <%
        	KqCardDataForm kqCardDataForm = (KqCardDataForm)session.getAttribute("kqCardDataForm");
       		kqCardDataForm.setError_message("");
        %> 
    }
    showMess();
</script>