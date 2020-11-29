<%@ page contentType="text/html; charset=UTF-8" language="java"%>
<%@page import="com.hjsj.hrms.utils.PubFunc"%>
<%@page import="com.hrms.frame.codec.SafeCode"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ page import="com.hrms.struts.valueobject.UserView"%>
<%@ page import="com.hrms.struts.constant.WebConstant"%>
<%@ page import="com.hjsj.hrms.actionform.hire.employActualize.EmployResumeForm"%>
<%@ page import="java.io.File"%>
<html>
<head>
<script language="JavaScript" src="/js/validate.js"></script>
<script language="JavaScript" src="/js/function.js"></script>
<style type="text/css">
    .RecordRow_top {
	border: inset 1px #94B6E6;	
	BORDER-BOTTOM: #94B6E6 0pt solid; 
	BORDER-LEFT: #94B6E6 0pt solid; 
	BORDER-RIGHT: #94B6E6 0pt solid; 
	BORDER-TOP: #94B6E6 1pt solid;
	font-size: 12px;

	}	
</style>
<script language='javascript'>
	/******定义简历导入方案*****/
	function defineResumeImportScheme(){
		document.employResumeForm.action="/hire/employActualize/employResumeImport.do?b_defineScheme=link";
		document.employResumeForm.submit();
	}
	
	/********开始简历导入*********/
	function startResumeImport(){
		var filepath=document.employResumeForm.zipFile.value;
		if(filepath.length==0)
		{
			alert("请选择附件!");
			return;
		}else{
			if(filepath.substring(filepath.lastIndexOf(".")+1,filepath.length)!="zip"){
				alert("请用zip压缩包来导入数据！");
				return;
			}
		}
	   var isRightPath = validateUploadFilePath(filepath);//防止上传漏洞 
	   if(!isRightPath)	
			return;
		document.getElementById("b_update").disabled="true";
		employResumeForm.action="/hire/employActualize/employResumeImport.do?b_update=link&isok=ok";
		///document.positionDemandForm.target="mil_body"
		employResumeForm.submit();

	}
	
	function detailReport(logTextName){
	var name=logTextName;
	alert(name);
}

function download(){
<%
	String tempdir=System.getProperty("java.io.tmpdir");
	String[] patharr = tempdir.split("\\\\");
	String path = "";
	for(int i=0;i<patharr.length;i++){
	path +=patharr[i]+"\\\\";
	}
	path+="ResumeImportLog.txt";
	File file=new File(path);
	String filename=SafeCode.encode(PubFunc.encrypt("ResumeImportLog.txt"));
	if(file.exists()){
%>
		var win=open("/servlet/vfsservlet?fromjavafolder=true&fileid="+ decode(<%=filename %>));
<%
	}else{
%>
		alert("日志文件不存在!");
<%
}
%>


}

function deletelog(){
	employResumeForm.action="/hire/employActualize/employResumeImport.do?b_delete=link";
		
	employResumeForm.submit();

}
</script>
</head>
<BODY><br>
<base id="mybase" target="_self">
<script type="text/javascript">

 <%
    EmployResumeForm employResumeForm=(EmployResumeForm)session.getAttribute("employResumeForm");
	String logTextName=employResumeForm.getLog();

	if(request.getParameter("isok")!=null&&request.getParameter("isok").equals("ok")){

%>
	detailReport('<%=logTextName%>');
<%		
	
	}
	employResumeForm.setLog("");

%>
</script>
<hrms:themes></hrms:themes>
<html:form action="/hire/employActualize/employResumeImport"  enctype="multipart/form-data" >

			<br>
			<br>
				 
			 			<table  width='50%' align='center'  >
				 			<tr>
					 			<td  align='left'>
									<font size=2  ><bean:message key="zp.resumeImport.importFormate"/></font>
								</td>
							</tr>
						</table>
				 
			<br>
			<br>
			<fieldset align="center" style="width:50%;">
				<legend>
					<bean:message key="zp.resumeImport.importFile"/>
				</legend>
				<table border="0" cellspacing="0" align="center" cellpadding="0">
					<tr>
						<td width="500">
							<Br>
							<bean:message key="zp.resumeImport.zipFile"/>
							<input type="file" name="zipFile" size="40" class="TEXT4">
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
			<table border="0" cellspacing="0" align="center" cellpadding="0" style="width:50%; margin-top:5px;">
						<tr>
						<td  nowrap align='center'>
						&nbsp;
						<hrms:priv func_id="3102501">
						<input type="button" class="mybutton"  value="<bean:message key="zp.resumeImport.defineResumeImportScheme"/>" onClick="defineResumeImportScheme();" >
						</hrms:priv>			
						<hrms:priv func_id="3102502">
							<input type="button" name="b_update" id="b_update" value="<bean:message key="zp.resumeImport.startImport"/>" class="mybutton"
								onClick="startResumeImport();">
						</hrms:priv>
						<hrms:priv func_id="3102503">
						<input type="button" class="mybutton"  value="<bean:message key="zp.resumeImport.downloadLog"/>" onClick="download();" >
						</hrms:priv>
						<hrms:priv func_id="3102504">
						<input type="button" class="mybutton"  value="<bean:message key="zp.resumeImport.deleteLog"/>" onClick="deletelog();" >
						</hrms:priv>
						</td>
					</tr>
					<tr>
						<td width="400">
							&nbsp;<input type="hidden" name="logTextName" value="${EmployResumeForm.log}">
						</td>
					</tr>
			</table>
</html:form>
</BODY>

</html>