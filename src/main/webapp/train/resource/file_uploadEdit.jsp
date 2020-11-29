<%@ page contentType="text/html; charset=utf-8"%>

<%@ taglib uri="/tags/struts-bean" prefix="bean"%>
<%@ taglib uri="/tags/struts-html" prefix="html"%>
<%@ taglib uri="/tags/struts-logic" prefix="logic"%>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms"%>
<script language="JavaScript" src="/js/popcalendar.js"></script>
<script language="javascript" src="/ajax/format.js"></script>
<script language="JavaScript" src="/js/function.js"></script>
<link rel="stylesheet" href="/css/css1.css" type="text/css">
<style>
<!--
.divTable {
	border: 1px solid #C4D8EE;
}
.TableRow1 {
	background-position : center left;
	font-size: 12px;  
	BORDER-BOTTOM: #C4D8EE 1pt solid; 
	BORDER-LEFT: #C4D8EE 0pt solid; 
	BORDER-RIGHT: #C4D8EE 0pt solid; 
	BORDER-TOP: #C4D8EE 0pt solid;
	height:22px;
	font-weight: bold;
	background-color:#f4f7f7;	
	/*
	color:#336699;
	*/
	valign:middle;
}
-->
</style>
<script type="text/javascript">
<!--
function reOk(outparamters){
	var isOk=outparamters.getValue("isOk");
	var newName=outparamters.getValue("newName");
	if(isOk == "1"){
		alert("修改成功！");
		window.returnValue =newName;
		
		window.close();
	}
}

function save(fileid){
		var hashvo = new ParameterSet();
		var filename=document.getElementById("uploadFileName").value;
		if(IsOverStrLength(filename, 200))
		  {
			  alert(TRAIN_RESOURCE_FILENAME_LENGTH);
			  return false;
		  }

		hashvo.setValue("fileid",fileid);
		hashvo.setValue("filename",filename);
		var request=new Request({method:'post',onSuccess:reOk,functionId:'2020012580'},hashvo);
	
}

function wclose(){
	
	window.close();
}

</script>
<%
   String fileid = request.getParameter("fileid");
   String filename=request.getParameter("filename");
 
%>
<hrms:themes></hrms:themes>
<html:form action="/train/resource/file_upload">
 <table width="100%" align="center" cellpadding="0" cellspacing="0" border="0">
   <tr><td>
      <div class="divTable common_border_color" style="width: 96%" style="margin-left: 12px; margin-top: 8px;">
		<table width="100%" align="center" cellpadding="0" cellspacing="0" border="0">
			<tr>
				<td class="TableRow" style="border-left: none;border-top: none;border-right: none;" colspan="2" nowrap="nowrap" align="center">
					修改附件名称
				</td>
			</tr>
			<tr><td>&nbsp;</td></tr>
			<tr>
				<td>
					<table width="96%" align="center" cellpadding="0" cellspacing="0" border="0">
						<tr>
							<td align="right" class="common_border_color"> 附件名称 &nbsp;&nbsp; </td>
							<td align="left" class="common_border_color">
								<html:text maxlength="200" styleId="uploadFileName" onchange="checkFilename(this);" property="filename" name="trainFileForm" value="<%=filename%>" size="40" styleClass="textColorWrite"/>
							</td>
						</tr>
					</table>
				</td>
			</tr>
			<tr><td>&nbsp;</td></tr>
		</table>
	</div>
  </td></tr>
  <tr><td>
    <table width="96%" align="center" cellpadding="0" cellspacing="0" border="0"
		style="margin-left: 12px; margin-top: 8px;">
		<tr><td align="center">
	 		<input type="button" name="b_modify" value='确定' onclick="save('<%=fileid%>')" class="mybutton" />
			&nbsp;&nbsp;
	 		<input type="button" name="b_cls" value='关闭' class="mybutton" onclick="wclose();" />
	   </td></tr>
    </table>
  </td></tr>
</table>
</html:form>
