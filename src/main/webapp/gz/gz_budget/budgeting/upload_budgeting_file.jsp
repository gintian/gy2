<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<html>
<script language="javascript" src="/gz/gz_budget/budgeting/budgeting.js"></script>
<script type="text/javascript">
<!--
function upload()
    {
      if(confirm("确定导入数据吗？导入的数据将覆盖原有数据。")){
    	 var fileEx = budgetingForm.templateFile.value;
        if(fileEx == ""){
        	alert(GZ_TAX_INFO1);
        	return ;
        }
         if(!validateUploadFilePath(fileEx))
           return;
        jinduo();
   		document.budgetingForm.action="/gz/gz_budget/budgeting/budgeting_table.do?b_upload=validate&tab_id="+document.budgetingForm.tab_id.value;
  		document.budgetingForm.submit();
     }else{
       return;
     }
    }
function goback(){
    document.budgetingForm.action="/gz/gz_budget/budgeting/budgeting_table.do?b_open=open&tab_id="+document.budgetingForm.tab_id.value;
  	document.budgetingForm.submit();
}
function jinduo(){
	var x=document.body.clientWidth/2-300;
    var y=document.body.clientHeight/2-125;
	var waitInfo;
	waitInfo=eval("wait");
	waitInfo.style.top=y;
	waitInfo.style.left=x;
	waitInfo.style.display="block";
}
//-->
</script>
<body>
<html:form action="/gz/gz_budget/budgeting/budgeting_table" enctype="multipart/form-data">
<br>
<br>
<br>
<br>
	<table border="0" cellspacing="0" align="center" cellpadding="0" style="width:40%;">
	<tr><td width="400"><font size=2>提示：请用下载的Excel模板来导入数据！模板格式不允许修改！</font></td></tr>
	<tr height="10"><td>&nbsp;</td></tr>
	</table>
<html:hidden property="tab_id"/>
	<center>
		<fieldset style="width:40%;">
				<legend>
					选择导入文件
				</legend>
				<table border="0" cellspacing="0" align="center" cellpadding="0">
					<tr>
						<td width="400">
							<Br>
							文件
							<input type="file" name="templateFile"  size="40">
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
		</center>
			<p> 
			<p> 
			<table border="0" cellspacing="0" align="center" cellpadding="0">
					<tr>
						<td align="center">
							<input type="button" name="b_update" value="上传" class="mybutton" onClick="upload();">
							<input type="button" name="b_update" value="返回" class="mybutton" onClick="goback();">
						</td>
					</tr>
			</table>

<div   id="wait" style='position:absolute;top:285;left:120;display:none;width:500px;heigth:250px'>
 
		<table border="1" width="50%" cellspacing="0" cellpadding="4" class="table_style" height="100" align="center">
			<tr>
			
				<td class="td_style" height=24 id="hlw">
					请稍候，正在导入数据...
				</td>
			</tr>
			<tr>
				<td style="font-size:12px;line-height:200%" align=center>
					<marquee class="marquee_style" direction="right" width="400" scrollamount="5" scrolldelay="10">
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
		    <iframe src="javascript:false" style="position:absolute; visibility:inherit; top:0px; left:0px;width:285px;height:120px;z-index:-1;filter='progid:DXImageTransform.Microsoft.Alpha(style=0,opacity=0)';">
		    </iframe>	
	</div>
</html:form>
</body>
</html>