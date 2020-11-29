<%@ page contentType="text/html; charset=UTF-8" language="java"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean"%>
<%@ taglib uri="/tags/struts-html" prefix="html"%>
<%@ taglib uri="/tags/struts-logic" prefix="logic"%>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms"%>
<%@ page import="com.hrms.struts.valueobject.UserView"%>
<%@ page import="com.hrms.struts.constant.WebConstant"%>
<script language="JavaScript">
function PreviewPhotoatwidth(photo_file,img_object,imgwidth){
	var fileext=photo_file.value.substring(photo_file.value.lastIndexOf("."),photo_file.value.length);
    fileext=fileext.toLowerCase();
    if ((fileext!='.jpg')&&(fileext!='.gif')&&(fileext!='.jpeg')&&(fileext!='.png')&&(fileext!='.bmp')){
    	alert(SORRY_OS_PIC_UPLOAD+"！");
        photo_file.focus();
    }else{
        img_object.src=photo_file.value;
       	if (img_object.width>imgwidth){
            img_object.width=imgwidth;
        }
   }
}
function resizeImage(){
	var imagearr =  document.getElementsByTagName("img") ;
	var clientWidth = document.body.clientWidth; 
	var clientHeight = document.body.clientHeight; 
	for(var i=0;i<imagearr.length;i++){
		imagearr[i].width = clientWidth;
		imagearr[i].height = clientHeight;
    }
}
function minSize(){
	var clientHeight = document.body.clientHeight;
	var clientWidth = document.body.clientWidth;  
	if(clientHeight>120&&clientWidth>85)
	{		
		window.resizeBy(-(25*(85/120)),-25); 
		resizeImage();
	}
}
function maxSize(){
	window.resizeBy(25*(85/120),25);
	resizeImage();
}
function upload()
    {
    	var fileEx = mInformForm.picturefile.value;
    	 if(fileEx == ""){
        	alert(SELECT_PIC_FIELD+"!");
        	return ;
        }
        if(fileEx.substring(fileEx.length-3)=="jpg" || fileEx.substring(fileEx.length-3)=="bmp" 
        	|| fileEx.substring(fileEx.length-4)=="jpeg" )
        {
        	document.getElementById("type").value = "upload";
	 		document.getElementById("a0100").value = "${mInformForm.a0100}";
	        document.mInformForm.action="/general/inform/emp/view/operpicture.do?b_query=link";
			document.mInformForm.submit(); 
        }else
        {
        	alert(SELECT_PIC_FIELD);
        	return;
        }
}
function bclear(){		
	document.getElementById("type").value = "clear";
 	document.getElementById("a0100").value = "${mInformForm.a0100}";
    document.mInformForm.action="/general/inform/emp/view/operpicture.do?b_save=link";
	document.mInformForm.submit(); 
}
function openFile() { 
	document.getElementById("picturefile").click();
} 

</script>
<form name="mInformForm" method="post" action="/general/inform/view/operpicture.do?b_save=link" enctype="multipart/form-data" >
	<fieldset align="center" style="width:180;">
	<legend><bean:message key="workdiary.message.set.pic"/></legend> 
	<table width="100%" align="center" border="0" cellspacing="0" cellpadding="0">
		<tr>
			<td align="center" id="picture" >
				<hrms:ole name="mInformForm" dbpre="mInformForm.dbname" a0100="a0100" ids="imgpicture" scope="session" height="150" width="100"/>
			</td>
		</tr>
		<tr>
			<td align="center" id="uploadbotton" >
			<input type='button'   value="<bean:message key='workdiary.message.preview'/>" onclick="openFile();" Class="mybutton" size="6"/>
			<input type='button'   value="<bean:message key='options.save'/>"  name='save' Class="mybutton" onclick="upload()"  />
			</td>
		</tr>
	</table>
	</fieldset>
	<div style="display:none">
		<html:file name="mInformForm" property="picturefile" onchange="PreviewPhotoatwidth(this,imgpicture,100)" styleClass="text6" size="6"/>
	</div>
	<html:hidden name="mInformForm" property="a0100"/>
</form>
