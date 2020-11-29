<%@ page contentType="text/html; charset=UTF-8" language="java"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean"%>
<%@ taglib uri="/tags/struts-html" prefix="html"%>
<%@ taglib uri="/tags/struts-logic" prefix="logic"%>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms"%>
<script type="text/javascript" src="/general/sys/hjaxmanage.js"></script>
<script language="JavaScript">

function upload(){
    var fileEx = mInformForm.picturefile.value;
    if(fileEx == ""){
        alert(SELECT_PIC_FIELD+"!");
        return ;
    }
    var fileEx1=fileEx.substring(fileEx.lastIndexOf(".")+1).toLowerCase();
    if(fileEx1=="jpg" || fileEx1=="bmp" || fileEx1=="jpeg"||fileEx1=="png"){
        document.getElementById("type").value = "upload";
	 	document.getElementById("a0100").value = "${mInformForm.a0100}";
		mInformForm.submit(); 	
     }else{
        alert(SELECT_PIC_FIELD);
        return;
    }
    if(!AxManager.setup(null, 'FileView', 0, 0))
      return false;
    var obj=document.getElementById('FileView'); 
    if (obj != null){
        var facSize=obj.GetFileSize(fileEx);                
        var photo_maxsize="${mInformForm.photo_maxsize}";
        if(parseInt(photo_maxsize,10)>0&&parseInt(photo_maxsize,10)<parseInt(facSize,10)/1024){  
            alert("上传文件大小超过管理员定义大小，请修正！上传文件上限"+photo_maxsize+"KB");
            return false;
        }     
    }  						
	window.close();	
}
function setPhoto2()
{
	document.getElementById("picturefile").click();
}
</script>
<form name="mInformForm" method="post" action="/general/inform/emp/view/displaypicture.do?b_save2=link"  enctype="multipart/form-data" >
	<html:hidden name="mInformForm" property="a0100"/>
	<html:hidden name="mInformForm" property="type"/>
	<br>
<table width="350" border="0" cellpadding="0" cellspacing="0" align="center">
    <tr height="20">
       		<!--  <td width=10 valign="top" class="tableft"></td>
       		<td width=140 align=center class="tabcenter">&nbsp;<bean:message key="conlumn.info.phototitle"/>&nbsp;</td>
       		<td width=10 valign="top" class="tabright"></td>
       		<td valign="top" class="tabremain" width="350"></td>  -->   
       		<td align=center class="TableRow">&nbsp;<bean:message key="conlumn.info.phototitle"/>&nbsp;</td>         	      
    </tr> 
 <tr> 
  <td class="framestyle9" height="40">
      <table>
         <tr>
            <td align="right"  nowrap><bean:message key="conlumn.info.photolabel"/>&nbsp;</td>
            <td align="left"  nowrap ><html:file name="mInformForm" property="picturefile" styleClass="text6"/>
            </td>
         </tr>        
      </table>
     </td>  
    </tr>
 <tr>
 <td align="center"  nowrap style="height:35px;">  
  <br>
   	<input type="button" onclick="upload();" Class="mybutton" value="<bean:message key="button.ok"/>">  
  </td>
 </tr>    
 </table>
</form>
