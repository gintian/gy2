<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%
String infor_type=request.getParameter("infor_type");
String objectid=request.getParameter("objectid");
String basepre=request.getParameter("basepre");
String attachmenttype=request.getParameter("attachmenttype");
%>

<script language='javascript'>
///关闭
function goback()
{
<% 
if(request.getParameter("b_save")!=null&&request.getParameter("b_save").equals("save")){//为了让返回值有值
%>
	var obj = new Object();
	obj.name = "name";
	window.returnValue = obj;
	window.close();
<%}else{%>
	window.close();
	<%} %>
}
///上传
function imports(infor_type,objectid,basepre,attachmenttype)
{
	var fileEx = templateForm.filecontent.value;
	if(fileEx == "")
	{
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
	document.getElementById("b_update").disabled="true";
	document.templateForm.action="/general/template/upload_attachment.do?b_save=save&infor_type="+infor_type+"&objectid="+objectid+"&basepre="+basepre+"&attachmenttype="+attachmenttype;
	document.templateForm.submit();
}

<% 
if(request.getParameter("b_save")!=null&&request.getParameter("b_save").equals("save")){//为了让返回值有值
%>
	var obj = new Object();
	obj.name = "name";
	window.returnValue = obj;
  	window.close();  	
<%} %>

</script>

		<form action="/general/template/upload_attachment.do" name="templateForm" method="post" enctype="multipart/form-data">

			
			<fieldset align="center" style="width:540px;height:110px;margin:10px,2Px,10px,2px">
				<legend>
					选择导入文件
				</legend>
				<table border="0" cellspacing="0" align="center" cellpadding="0" width="100%" style="margin-top: 3%;">
			
					<%
						if("1".equals(attachmenttype)){//只有个人附件才选择多媒体分类
					%>
					<tr>
						<td align="right" width="20%" style="padding-right:5px">
							多媒体分类&nbsp;
						</td>
						<td align="left" width="80%">
							<hrms:optioncollection name="templateForm" property="mediasortList" collection="list" />
							<html:select name="templateForm" property="mediasortid" size="1" style="width:150px;">
								<html:options collection="list"  property="dataValue" labelProperty="dataName"/>
							</html:select>
						</td>
					</tr>
		
					<%		
						}
					%>
					
					<tr>
						<td align="right" width="20%" style="padding-right:5px">
							文件
						</td>
						<td align="left" width="80%">
							<input type="file" name="filecontent" size="30" class="TEXT4">
						</td>
					</tr>
				
				
				</table>
			</fieldset>
			
          	
          	
			<table border="0" cellspacing="0" align="center" cellpadding="0" style="width:540px;margin-top:10px">
				<tr>
					<td align="center">
						<input type="button" name="b_update" id="b_update" value="上传" class="mybutton" onClick="imports('<%=infor_type %>','<%=objectid %>','<%=basepre %>','<%=attachmenttype %>')">
						<input type="button" name="b_update2" value="关闭" class="mybutton" onClick="goback()">
					</td>
				</tr>
			</table>
		</form>

