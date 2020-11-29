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
	 editReport_actuaialForm.action="/report/actuarial_report/edit_report/searcheportU02List.do?b_query=link&opt=1&unitcode=${editReport_actuaialForm.unitcode}&id=${editReport_actuaialForm.id}&report_id=${editReport_actuaialForm.report_id}";
      editReport_actuaialForm.submit();
    }
    function imports()
    {
    	var fileEx = editReport_actuaialForm.file.value;
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
    	if(temp.toLowerCase()=='xls'||temp.toLowerCase()=='xlsx')
    	{
    	    var waitInfo=eval("wait");	   
            waitInfo.style.display="block";
    	    document.editReport_actuaialForm.action="/report/actuarial_report/edit_report/editreportU02List.do?b_importData2=get&id=${editReport_actuaialForm.id}&unitcode=${editReport_actuaialForm.unitcode}&report_id=${editReport_actuaialForm.report_id}"
  	        document.editReport_actuaialForm.submit();  		
    	}
    	else
    	{
    		alert("导入的文件必须为excel格式");
    	}
    }
function MusterInitData()
{
	var waitInfo=eval("wait");	
	waitInfo.style.display="none";
}
function reject(){
 document.editReport_actuaialForm.action="/report/actuarial_report/edit_report/editreportU02List.do?b_importsubmit2=reject"
   document.editReport_actuaialForm.submit();
}
  </script>
  <hrms:themes />
	<body>
		<form name="editReport_actuaialForm" method="post"
			action="/report/actuarial_report/edit_report/editreportU02List.do" enctype="multipart/form-data">
			<br>
			<br>
		
			<table border="0" cellspacing="0" align="center" cellpadding="0" style="width:50%;">
					<tr>
						<td width="400">
							<font size=2>说明：请用下载的Excel模板来导入数据！模板格式不允许修改！</font>
						</td>
					</tr>
			</table>
				<p>
			<fieldset align="center" style="width:50%;">
				<legend>
					选择导入文件
				</legend>
				<table border="0" cellspacing="0" align="center" cellpadding="0">
					<tr>
						<td width="400">
							<Br>
							文件
							<input type="file" name="file" size="40">
							<br>
							<br>

						</td>
					</tr>
					<tr>
						<td>
							&nbsp;
						</td>
					</tr>
					<tr>
						<td align="center">
							<input type="button" name="b_update" value="上传" class="mybutton"
								onClick="imports()">
							<input type="button" name="b_update" value="返回" class="mybutton"
								onClick="goback()">
						</td>
					</tr>
				</table>
			</fieldset>
         	
                 <br>
			<br>
			<fieldset align="center" style="width:50%;">
				<legend>
					直接驳回
				</legend>
				<table border="0" cellspacing="0" align="center" cellpadding="0">
					
					<tr>
						<td valign='top' >驳回描述:<br><html:textarea  name="editReport_actuaialForm"  rows='10' cols='50%' property="description" />	
 					</td>
					</tr>
					<tr>
						<td align="center">
							<input type="button" name="b_update" value="确定" class="mybutton"
								onClick="reject()">
							
						</td>
					</tr>
				</table>
			</fieldset> 
		</form>
	</body>
</html>
<div id='wait' style='position:absolute;top:200;left:250;display:none;'>
  <table border="1" width="400" cellspacing="0" cellpadding="4"  class="table_style" height="87" align="center">
           <tr>
             <td  class="td_style"  height=24>正在处理数据请稍候....</td>
           </tr>
           <tr>
             <td style="font-size:12px;line-height:200%" align=center>
               <marquee class="marquee_style"  direction="right" width="300" scrollamount="5" scrolldelay="10">
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
<script language="javascript">
 MusterInitData();
</script>