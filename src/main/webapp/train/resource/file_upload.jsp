<%@ page contentType="text/html; charset=UTF-8" language="java"%>
<%@page import="com.hjsj.hrms.utils.PubFunc"%>
<%@page import="com.hrms.frame.codec.SafeCode"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean"%>
<%@ taglib uri="/tags/struts-html" prefix="html"%>
<%@ taglib uri="/tags/struts-logic" prefix="logic"%>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms"%>
<style type="text/css"> 
.scroll_box {
    height: 180px;    
    width: 100%;            
    overflow: auto;            
   	BORDER-BOTTOM: #94B6E6 1pt solid; 
    BORDER-LEFT: #94B6E6 1pt solid; 
    BORDER-RIGHT: #94B6E6 1pt solid; 
    BORDER-TOP: #94B6E6 1pt solid ; 
}
</style>
<script language="javascript" src="/js/common.js"></script>
<script language="javascript" src="/js/constant.js"></script>
<script language="JavaScript" src="/js/function.js"></script>
<%int i = 0;%>
<hrms:themes/>
<html:form action="/train/resource/file_upload" enctype="multipart/form-data">
	<table width="90%" border="0" cellspacing="0" align="center"
		cellpadding="0" class="" style="margin-top: 5px;">
		<tr>
			<td>
				<div class="scroll_box common_border_color" style="width: 691px;">
					<table width="100%" border="0" cellspacing="0" align="center"
						cellpadding="0">
						<tr class="fixedHeaderTr">
						  <logic:notEqual value="0" name="trainFileForm" property="myself">
							<td align="center" class="TableRow noleft" width="30" style="border-top: none;" nowrap>
								<input type="checkbox" name="checkall" value="checkall" onclick="checkboxAll(this);">
							</td>
						  </logic:notEqual>
							<td align="center" class="TableRow noleft" style="border-top: none;" nowrap>
								附件名称
							</td>
							<td align="center" class="TableRow noleft" style="border-top: none;" nowrap>
								创建人
							</td>
							<td align="center" class="TableRow noleft noright" style="border-top: none;" nowrap>
								创建日期
							</td>
							<td align="center" width="60" class="TableRow noright" style="border-top: none;" nowrap>
								<bean:message key="conlumn.infopick.educate.downfile"/>
							</td>
						</tr>
						
						<hrms:paginationdb id="element" name="trainFileForm"
							sql_str="trainFileForm.filesql" table="" where_str="trainFileForm.strwhere"
							columns="trainFileForm.columns" page_id="pagination"
							pagerows="${trainFileForm.pagerows}"
							order_by="order by fileid">
							<bean:define id="nid" name="element" property="fileid" />
							<bean:define id="name" name="element" property="name" />
							<bean:define id="ext" name="element" property="ext" />
							<bean:define id="nid" name="element" property="fileid" />
							<bean:define id="url" name="element" property="url" />
							<%
							String fid = SafeCode.encode(PubFunc.encrypt(nid.toString()));
							String fname = SafeCode.encode(PubFunc.encrypt(name.toString()));
							String fpath = SafeCode.encode(PubFunc.encrypt(url.toString()+nid.toString()+ext.toString()));
							if (i % 2 == 0){%>
							<tr class="trShallow">
							<%} else{%>
							<tr class="trDeep">
							<%}i++;%>
							<logic:notEqual value="0" name="trainFileForm" property="myself">
								<td align="center" class="RecordRow noleft" style="border-top: none;" nowrap>
									<input type="checkbox" name="<%=fid %>" value="<%=fid %>"/>
								</td>
							</logic:notEqual>
								<logic:equal value="0" name="trainFileForm" property="myself">
									<td id="newName<%=fid %>" align="left" class="RecordRow noleft" style="border-top: none;" nowrap>
									
										<bean:write name="element" property="name" filter="true"/>
									</td>
								</logic:equal>
								<logic:notEqual value="0" name="trainFileForm" property="myself">
									<td id="newName<%=fid %>" align="left" class="RecordRow noleft" style="color:rgb(0,0,255);border-top: none;" nowrap onclick="editFileName('<%=fid %>','newName<%=fid %>')" style="cursor: hand;" title="单击鼠标可以改变附件名称" >
									
										<bean:write name="element" property="name" filter="true"/>
									</td>
								</logic:notEqual>
								<td align="center" class="RecordRow noleft" style="border-top: none;" nowrap>
									<bean:write name="element" property="create_user" filter="true" />
								</td>
								<td align="center" class="RecordRow noleft noright" style="border-top: none;" nowrap>
									<bean:write name="element" property="create_time" filter="true" />
								</td>
								<td align="center" class="RecordRow noright" style="border-top: none;" nowrap>
								<logic:notEqual value="0" name="trainFileForm" property="myself">
								    <logic:equal value="0" name="trainFileForm" property="type">
								      <hrms:priv func_id="3230512" module_id="">
										<span style="cursor:hand;color:#0000FF" onclick="uploadFile('<%=fpath %>','${url}','<%=fid %>')">
										<bean:message key="conlumn.infopick.educate.downfile"/></span>
									  </hrms:priv>
									</logic:equal>
								    <logic:equal value="1" name="trainFileForm" property="type">
								      <hrms:priv func_id="3230207" module_id="">
										<span style="cursor:hand;color:#0000FF" onclick="uploadFile('<%=fpath %>','${url}','<%=fid %>')">
										<bean:message key="conlumn.infopick.educate.downfile"/></span>
									  </hrms:priv>
									</logic:equal>
								</logic:notEqual>
								<logic:equal value="0" name="trainFileForm" property="myself">
									<span style="cursor:hand;color:#0000FF" onclick="uploadFile('<%=fpath %>','${url}','<%=fid %>')">
									<bean:message key="conlumn.infopick.educate.downfile"/></span>
								</logic:equal>
								</td>
							</tr>
						</hrms:paginationdb>
					</table>
				</div>
			</td>
		</tr>
		<tr>
			<td>
				<table width="100%" class="RecordRowP" align="center">
					<tr>
						<td valign="bottom" class="tdFontcolor">
							<hrms:paginationtag name="trainFileForm"
								pagerows="${trainFileForm.pagerows}" property="pagination"
								scope="page" refresh="true"></hrms:paginationtag>
						</td>
						<td align="right" nowrap class="tdFontcolor">
							<p align="right">
								<hrms:paginationdblink name="trainFileForm"
									property="pagination" nameId="trainFileForm" scope="page">
								</hrms:paginationdblink>
						</td>
					</tr>
				</table>
			</td>
		</tr>
		<tr>
			<td>
			<logic:notEqual value="0" name="trainFileForm" property="myself">
			<logic:equal value="0" name="trainFileForm" property="type">
			 	<hrms:priv func_id="3230508" module_id="">
				<table width="100%" class="RecordRowP" align="center">
					<tr>
						<td class="tdFontcolor" width="100" align="right">
							文件名称&nbsp;
						</td>
						<td class="tdFontcolor" align="left">
							<input type="text" id="uploadfilename" onchange="checkFilename(this);" name="filename" Class="text6" size="20"/>
						</td>
					</tr>
					<tr>
						<td class="tdFontcolor" width="100" align="right">
							上传文件路径&nbsp;
						</td>
						<td  nowrap class="tdFontcolor" align="left">
							<html:file name="trainFileForm" property="picturefile" styleId="picturefile" onkeydown="event.returnValue=false;" onchange="submitUpload();"  styleClass="text6" size="20"/>
						</td>
					</tr>
				</table>
				</hrms:priv>
				</logic:equal>
				<logic:equal value="1" name="trainFileForm" property="type">
			 	<hrms:priv func_id="3230206" module_id="">
				<table width="100%" class="RecordRowP" align="center">
					<tr>
						<td class="tdFontcolor" width="100" align="right">
							文件名称&nbsp;
						</td>
						<td class="tdFontcolor" align="left">
							<input type="text" id="uploadfilename" onchange="checkFilename(this);" name="filename" Class="text6" size="20"/>
						</td>
					</tr>
					<tr>
						<td class="tdFontcolor" width="100" align="right" align="left">
							上传文件路径&nbsp;
						</td>
						<td  nowrap class="tdFontcolor">
							<html:file name="trainFileForm" property="picturefile" styleId="picturefile" onkeydown="event.returnValue=false;" onchange="submitUpload();"  styleClass="text6" size="20"/>
						</td>
					</tr>
				</table>
				</hrms:priv>
				</logic:equal>
				</logic:notEqual>
			</td>
		</tr>
		<tr>
			<td align="center" style="padding-top: 5px;">
			<logic:notEqual value="0" name="trainFileForm" property="myself">
			<logic:equal value="0" name="trainFileForm" property="type">
		     <hrms:priv func_id="3230509" module_id="">
				<input type="button" id="deleteButton" value="删除" class="mybutton" onclick="deleteFile()"/>
			 </hrms:priv>
			 </logic:equal>
			 <logic:equal value="1" name="trainFileForm" property="type">
		     <hrms:priv func_id="3230208" module_id="">
				<input type="button" id="deleteButton" value="删除" class="mybutton" onclick="deleteFile()"/>
			 </hrms:priv>
			 </logic:equal>
			 </logic:notEqual>
				<input type="button" id="returnButton" value="关闭" onclick="goback();" class="mybutton"/>
			</td>
		</tr>
	</table>
	<div id="wait" style='position:absolute;top:142;left:120;display:none;width:500px;heigth:250px'>
 
		<table border="1" width="50%" cellspacing="0" cellpadding="4" class="table_style" height="100" align="center">
			<tr>
			
				<td class="td_style" height=24 id="hlw">
					请稍候，正在上传...
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
<script language="javascript">

function goback(){
	<%if(i>0){%>
	window.returnValue="${trainFileForm.itemid}";
	<%}%>
	window.close();
}
function submitUpload(){

		var picturefile = document.getElementById("picturefile");
		if(!validateUploadFilePath(picturefile.value)){
			picturefile.outerHTML=picturefile.outerHTML;
			return;
		}

		var fileSize = 0;
		var isIE = /msie/i.test(navigator.userAgent) && !window.opera;    
		var fileSize = 0;          
		if (isIE && !picturefile.files) {  
			try {
		        var fileSystem = new ActiveXObject("Scripting.FileSystemObject");         
		        var file = fileSystem.GetFile (picturefile.value);  
		        fileSize = file.Size;
			} catch(e) {
				fileSize = 1024;
			}
		} else {     
		   fileSize = picturefile.files[0].size;      
		} 
			  
		var size = fileSize / 1024;
		if(size>100*1024){
		    alert(TRAIN_RESOURCE_FILE_SIZE);
			picturefile.outerHTML=picturefile.outerHTML;
			return;
		}
		document.getElementById('wait').style.display='block';
		document.getElementById("deleteButton").disabled="disabled";
		document.getElementById("returnButton").disabled="disabled";
		trainFileForm.action="/train/resource/file_upload.do?b_query=link&flag=load&r0701=${trainFileForm.itemid}&type=${trainFileForm.type}";
		trainFileForm.target="_self";
		trainFileForm.method="post";
		trainFileForm.enctype="multipart/form-data";
		trainFileForm.submit();
		picturefile.disabled="disabled";
}
function uploadFile(names,url,fileid){
	if(url=="no"){
		var hashvo=new ParameterSet();
		hashvo.setValue("check","outfile");
		hashvo.setValue("fileid",fileid);
		hashvo.setValue("flag","54");
		var request=new Request({method:'post',asynchronous:false,onSuccess:showFieldList,functionId:'2020030021'},hashvo);
	}else
		window.open("/media/resources/"+names);
}	
function showFieldList(outparamters){
	var outName=outparamters.getValue("outname");
	if(outName!=null&&outName.length>1)
		window.open("/servlet/vfsservlet?fileid="+outName);
}
function deleteFile(){
	var checkvalues = "";
	//if(!confirm("确定删除?"))
	//	return false;
	var tablevoss=document.getElementsByTagName("input");
	for(var i=0;i<tablevoss.length;i++){
		if(tablevoss[i].type=="checkbox"&&tablevoss[i].checked){
			if(tablevoss[i].value!="checkall"){
				checkvalues+=tablevoss[i].value+",";
			}
      	 }
   	}
   	if(checkvalues.length>0){
   		if(!confirm("确定删除?"))
			return false;
   	}else{
   		alert("请选择要删除的附件！");
   		return false;
   	}
   		
   	var urlstr = "/train/resource/file_upload.do?b_query=link&flag=delete&r0701=${trainFileForm.itemid}&type=${trainFileForm.type}&checkvalue=";
   	urlstr+=checkvalues;
   	trainFileForm.action=urlstr;
   	trainFileForm.target="_self";
	trainFileForm.method="post";
	trainFileForm.submit();  
}
document.body.onbeforeunload=function(){ 
	 if(event.clientY<0) {//双击文件关闭选取文件的窗口时 event.clientX为负数故将event.clientX<0的判断去掉    
          goback(); 
     }   
}


function editFileName(fileid,newNameId){
	var filename = document.getElementById(newNameId).innerText;
	var thecodeurl="/train/resource/file_upload.do?br_edit=link&fileid="+fileid;
	var return_vo= window.showModalDialog(thecodeurl, "", 
              	"dialogWidth:400px; dialogHeight:150px;resizable:no;center:yes;scroll:no;status:no");
	if(return_vo!=null){
		document.getElementById(newNameId).innerHTML=return_vo;
		
	}
    
}

</script>
