<%@ page contentType="text/html; charset=UTF-8" language="java"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean"%>
<%@ taglib uri="/tags/struts-html" prefix="html"%>
<%@ taglib uri="/tags/struts-logic" prefix="logic"%>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms"%>
<style type="text/css"> 
.scroll_box {
    height: 200px;    
    width: 100%;            
    overflow: auto;            
   	BORDER-BOTTOM: #94B6E6 1pt solid; 
    BORDER-LEFT: #94B6E6 1pt solid; 
    BORDER-RIGHT: #94B6E6 1pt solid; 
    BORDER-TOP: #94B6E6 1pt solid ; 
}
</style>
<script language="javascript" src="/js/common.js"></script>
<%int i = 0;%>
<html:form action="/org/orgdata/orgdata" enctype="multipart/form-data">
	<table width="80%" border="0" cellspacing="0" align="center" cellpadding="0">
		<tr><td>&nbsp;</td></tr>
		<tr>
			<td>
				<fieldset align="center" style="width:100%;height:100;">
				<legend>上传文件</legend> 
				<table width="100%" align="center">
					<tr><td colspan="2">&nbsp;</td></tr>
					<tr>
						<td width="100">
							上传文件路径：
						</td>
						<td  nowrap>
							<html:file name="orgDataForm" property="picturefile" onkeydown="event.returnValue=false;"  styleClass="text6" size="20"/>
						</td>
					</tr>
				</table>
				</fieldset>
			</td>
		</tr>
		<tr><td>&nbsp;</td></tr>
		<tr>
			<td align="center">
				<input type="button" value="确定" onclick="submitUpload();" class="mybutton"/>
				<input type="button" value="关闭" onclick="window.close();" class="mybutton"/>
			</td>
		</tr>
	</table>
</html:form>
<script language="javascript">
function submitUpload(){
	if(!confirm("确定上传?"))
		return false;
	var picturefile = document.getElementById("picturefile").value;
	if(picturefile==null||picturefile.length<1){
		alert("请输入上传文件的路径!");
		document.getElementById("picturefile").focus();
		return false;
	}
	orgDataForm.action="/org/orgdata/orgdata.do?b_load=link&flag=load&b0110=${param.b0110}&i9999=${param.i9999}&infor=${param.infor}";
	orgDataForm.target="_self";
	orgDataForm.method="post";
	orgDataForm.enctype="multipart/form-data";
	orgDataForm.submit();  
}
</script>
