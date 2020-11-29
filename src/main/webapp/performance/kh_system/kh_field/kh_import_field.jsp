<%@ page contentType="text/html; charset=UTF-8" language="java"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms"%>
<script type="text/javascript">
function subok()
{
	var path=khFieldForm.fieldfile.value;		
	
	if(path==null || trim(path).length<=0)
	{
  		alert("请选择需要导入的文件!");
  		return;
	}
	
	// 防止上传漏洞
	var isRightPath = validateUploadFilePath(path);
	if(!isRightPath)	
		return;
	
 	var obj=document.getElementById('FileView'); 
  	if (obj != null)
  	{
       	obj.SetFileName(path);
       	var facSize=obj.GetFileSize(); 
       	if(parseInt(facSize)==-1)   
       	{
           	alert("文件不存在，请输入正确的文件路径！");
           	return;
       	}  
	}       
	var waitInfo=eval("wait");			
  	waitInfo.style.display="block";
   	document.getElementById("ko").disabled=true;
   	document.getElementById("olc").disabled=true;
   	khFieldForm.action="/performance/kh_system/kh_field/kh_field_tree.do?b_import2=import2&count=2";
  	khFieldForm.submit();
}

function backFlag()
{
    self.parent.location="/performance/kh_system/kh_field/kh_field_tree.do?b_query=link&subsys_id="+khFieldForm.subsys_id.value;
}
</script>
<html:form action="/performance/kh_system/kh_field/kh_field_tree" enctype="multipart/form-data" method="post">

	<div id="wait" style='position:absolute;top:100;left:375;display:none;'>
 
		<table border="1" width="50%" cellspacing="0" cellpadding="4" class="table_style" height="135" align="center">
			<tr>
			
				<td class="td_style" id='wait_desc' height=24>
					正在分析选择的文件...
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
 	<table width="50%" border="0" cellspacing="0" style="margin-top:50px;" align="center" cellpadding="0">
		<tr height="20">
			<td align='left' class='TableRow_lrt'>
				<bean:message key="kh.field.selectfieldfile"/>
			</td>
		</tr>
		<tr>
			<td width="100%" align="center" class="framestyle" height="100px">
			
				<table width="100%" border="0" cellspacing="0"  align="center" cellpadding="0">
					<tr>
						<td align="center" width="100%" colspan="4">
							<html:file name="khFieldForm" property="fieldfile" style="height:28px;" styleClass="inputtext" accept="zip" size="40"/>
						</td>
					</tr>
				</table>
			</td>
		</tr>
		<tr>
			<td colspan="4" align="center" style="padding-top:7px;">
				<input type="button" name="ok" id="ko" value="<bean:message key="button.ok"/>" class="mybutton" onclick="subok();"/>
				<input type="button" name="clo" id="olc"  value="<bean:message key="button.return"/>" class="mybutton" onclick="backFlag();"/>
			</td>
		</tr>
	</table>
<html:hidden name="khFieldForm" property="subsys_id"/>
</html:form>

