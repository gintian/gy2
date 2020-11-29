<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean"%>
<%@ taglib uri="/tags/struts-html" prefix="html"%>
<%@ taglib uri="/tags/struts-logic" prefix="logic"%>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms"%>
<%@ page import="com.hjsj.hrms.actionform.report.actuarial_report.fill_cycle.ReportCycleForm"%>
<%
ReportCycleForm reportCycleForm=(ReportCycleForm)session.getAttribute("reportCycleForm");
	String report_id = reportCycleForm.getReport_id();
	//System.out.println("selfUnitcode:"+selfUnitcode+"unitcode:"+unitcode+"flag:"+flag+"opt:"+opt+"from_model:"+from_model);
	
 %>
<html>
	<head>

	</head>
	<script language='javascript'>
    function goback()
    {
	   document.reportCycleForm.action="/report/actuarial_report/fill_cycle.do?b_query2=lisk"
	   document.reportCycleForm.submit();
    }
    function imports(Report_id)
    {
    	var fileEx = reportCycleForm.file.value;
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
             if(Report_id.indexOf("U02")!=-1)
   			{
   				if (document.reportCycleForm.selbox.checked){
   				document.reportCycleForm.addother.value="1";
   				} else{
   				document.reportCycleForm.addother.value="0";
   				}
     		 	document.reportCycleForm.action="/report/actuarial_report/fill_cycle.do?b_importData=get&id=${reportCycleForm.id}&unitcode=${reportCycleForm.unitcode}&report_id=${reportCycleForm.report_id}"
  	        	document.reportCycleForm.submit();  
  			 }else if(Report_id=="U03")
  			 {
   			 document.reportCycleForm.action="/report/actuarial_report/fill_cycle.do?b_importData03=get&id=${reportCycleForm.id}&unitcode=${reportCycleForm.unitcode}&report_id=${reportCycleForm.report_id}"
  	        document.reportCycleForm.submit();  
  			 }else if(Report_id=="U04")
  			 {
   			 document.reportCycleForm.action="/report/actuarial_report/fill_cycle.do?b_importData04=get&id=${reportCycleForm.id}&unitcode=${reportCycleForm.unitcode}&report_id=${reportCycleForm.report_id}"
  		     document.reportCycleForm.submit();  
   			}
    	   		
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
  </script>
  <hrms:themes />
	<body>
		<form name="reportCycleForm" method="post"
			action="/report/actuarial_report/fill_cycle.do"
			enctype="multipart/form-data">
			<html:hidden styleId="report_id" name="reportCycleForm"
				property="report_id" />
				<html:hidden styleId="addother" name="reportCycleForm"
				property="addother" />
				
			<br>
			<br>
				
			<table border="0" cellspacing="0" align="center" cellpadding="0"
				style="width: 50%;">
				<tr>
					<td width="400">
						<font size=2>说明：请用下载的Excel模板来导入数据！模板格式不允许修改！</font>
					</td>
				</tr>
			</table>
			<p>
			<fieldset align="center" style="width: 50%;">
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
						

						</td>
					</tr>
						<tr>
						<%if(report_id.indexOf("U02")!=-1){ %>
						<td align="left">
           				 是否追加<input type="checkbox" name="selbox">
						</td>
						<%} %>
						 <br>	
					</tr>
					
					<tr>
						<td align="center">
							<input type="button" name="b_update" value="上传" class="mybutton"
								onClick="imports('${reportCycleForm.report_id}')">
							<input type="button" name="b_update" value="返回" class="mybutton"
								onClick="goback()">
						</td>
					</tr>
					
			</table>
			</fieldset>
		

		</form>
	</body>
</html>
<div id='wait' style='position: absolute; top: 200; left: 250;'>
	<table border="1" width="400" cellspacing="0" cellpadding="4"
		 class="table_style" height="87"
		align="center">
		<tr>
			<td class="td_style" 
				height=24>
				正在处理数据请稍候....
			</td>
		</tr>
		<tr>
			<td style="font-size: 12px; line-height: 200%" align=center>
				<marquee class="marquee_style" direction="right"
					width="300" scrollamount="5" scrolldelay="10" >
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