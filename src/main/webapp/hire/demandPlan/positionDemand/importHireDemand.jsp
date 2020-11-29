<%@ page contentType="text/html; charset=UTF-8" language="java"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ page import="com.hrms.struts.valueobject.UserView"%>
<%@ page import="com.hrms.struts.constant.WebConstant"%>
<%@ page import="com.hjsj.hrms.actionform.hire.demandPlan.PositionDemandForm"%>
<html>
<head>
 <%
 UserView userView=(UserView)session.getAttribute(WebConstant.userView);
	String flag=request.getParameter("flag");

	%>
<script language="JavaScript" src="/js/validate.js"></script>
<script language="JavaScript" src="/js/function.js"></script>
<script language='javascript'>
	function importHireDemand(){
		var filepath=document.positionDemandForm.file.value;
		if(filepath.length==0)
		{
			alert("请选择附件!");
			return;
		}else{
			if(filepath.substring(filepath.lastIndexOf(".")+1,filepath.length)!="zip"){
				alert("请用导出的zip压缩包来导入数据！");
				return;
			}
		}
	   var isRightPath = validateUploadFilePath(filepath);
	   if(!isRightPath)	
			return;
		document.getElementById("b_update").disabled="true";
		document.positionDemandForm.action="/hire/demandPlan/positionDemand/positionDemandTree.do?b_importdemandzip=link&isok=ok&flag="+<%=flag%>;
		///document.positionDemandForm.target="mil_body"
		document.positionDemandForm.submit();

	}
function detailReport(infor,logTextName){
	var name=document.positionDemandForm.logTextName.value;
	if(logTextName!=null&&logTextName.length>0){
		name = decode(name);
		var win=open("/servlet/vfsservlet?fromjavafolder=true&fileid="+name);
	}

}

function goback(){
	///window.history.back();
	if("1"==<%=flag%>){
		document.positionDemandForm.action="/hire/demandPlan/positionDemand/positionDemandTree.do?b_query=1";
	}else if("2"==<%=flag%>){
		document.positionDemandForm.action="/hire/demandPlan/positionDemand/positionDemandTree.do?b_query2=2";
	}
	document.positionDemandForm.target="mil_body";
	document.positionDemandForm.submit();
}

</script>
<TITLE>e-HR</TITLE>
</head>
<hrms:themes></hrms:themes>
<%
String bosflag= userView.getBosflag();//得到系统的版本号
if(bosflag!=null&&bosflag.equalsIgnoreCase("hcm")){
%>
    <link href="/hire/css/layout.css" rel="stylesheet" type="text/css">
<%
}
%>
<BODY>
<%
if(bosflag!=null&&!bosflag.equalsIgnoreCase("hcm")){
%>
 <br>
<% 
}
%> 
<base id="mybase" target="_self">

<html:form action="/hire/demandPlan/positionDemand/positionDemandTree"  enctype="multipart/form-data" >
<%
if(bosflag!=null&&!bosflag.equalsIgnoreCase("hcm")){
%>
 <br><br>
<% 
}
%> 			 
			 			<table  width='50%' align='center' class="rightsmallTable" ><tr><td  align='left'>
							<font size=2  >说明：请用导出的zip压缩包来导入数据!</font>
						</td></tr>
						</table>
						 <br>
<%
if(bosflag!=null&&!bosflag.equalsIgnoreCase("hcm")){
%>
<br>
<% 
}
%>  
			<fieldset align="center" style="width:50%;">
				<legend>
					选择导入文件
				</legend>
				<table border="0" cellspacing="0" align="center" cellpadding="0">
					<tr>
						<td width="400">
							<Br>
							文件
							<input type="file" name="file" size="40" class="text4">
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
<%
if(bosflag!=null&&!bosflag.equalsIgnoreCase("hcm")){//保留原有的页面布局 v6.x
%>
<p>
<% 
}
%>  
			<table border="0" cellspacing="0" align="center" cellpadding="0" style="width:50%;" class="containButtonTable">
						<tr>
						<td align="center">
							<input type="button" name="b_update" id="b_update" value="上传" class="mybutton"
								onClick="importHireDemand();">
							<input type="button" name="b_update2" value="返回" class="mybutton"
								onClick="goback()">
						</td>
					</tr>
					<tr>
						<td width="400">
							&nbsp;<input type="hidden" name="logTextName" value="${positionDemandForm.logTextName}">
						</td>
					</tr>
					
			</table>


</html:form>
</BODY>
<script type="text/javascript">

 <%
    PositionDemandForm positionDemandForm=(PositionDemandForm)session.getAttribute("positionDemandForm");
	String logTextName=positionDemandForm.getLogTextName();
	String infor=positionDemandForm.getInfor();
	if(request.getParameter("isok")!=null&&request.getParameter("isok").equals("ok")&&infor!=null&&!infor.equals("")){
		
%>

	 	detailReport('<%=infor%>','<%=logTextName%>');

<%		
	
	}
	positionDemandForm.setLogTextName("");
	positionDemandForm.setInfor("");
%>
</script>
</html>