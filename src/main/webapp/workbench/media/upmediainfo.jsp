<%@ page contentType="text/html; charset=UTF-8" language="java"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<script type="text/javascript" src="/general/sys/hjaxmanage.js"></script>
<script language="javascript">
 function ValidateValue(textbox) { 
	 var IllegalString = "?*:\"<>\\/|";
     var textboxvalue = textbox.value; 
     var index= textboxvalue.length-1; 
     var s = textbox.value.charAt(index); 
     if(IllegalString.indexOf(s)>=0) { 
           s = textboxvalue.substring(0,index); 
           textbox.value = s; 
     } 
 } 
                
 function validatefilepath(){
 	var mediapath=document.multMediaForm.mediafile.value;
 	return validateUploadFilePath(mediapath);
 }
 
 function wsubmit(){
 	var	ftitle = document.getElementsByName("filetitle");
 	var	IllegalString = "?*:\"<>\\/|";
 	var ft="";
 	var filesort=document.getElementsByName('filesort');
 	if(filesort.length==0 || filesort[0].value.length==0){
 		alert("没有多媒体分类权限，不能上传！");
 		return;
 	}else{
 		var fileobj=document.getElementsByName("mediafile");
 		var file = fileobj[0].value;
 		var ft="";
 		var	ftitle = document.getElementsByName("filetitle")[0].value;
 		for(var i=0;i<ftitle.length;i++){
 			ft=ftitle.substring(i,i+1);
 			if(IllegalString.indexOf(ft)>=0){
 				alert("标题中包含特殊字符，请修改标题后，再重新上传。");
 			 	return;
 			}
 		}
 		
 		var flag=false;
 		try{
 		 var fileSystem= new ActiveXObject("Scripting.FileSystemObject");
 		}catch(e){
 		  flag=true;
 		}
 		if(!flag){
	 		if(!AxManager.setup(null, 'FileView', 0, 0)){
	 			multMediaForm.action = "/workbench/media/upmediainfo.do?b_save=link";
	 			multMediaForm.submit();
	  			return false; 
	 		}
	 		var  obj=document.getElementById('FileView'); 
	        if (obj != null)
	        {
	           var facSize=obj.GetFileSize(file);
		 	   var multimedia_maxsize="${multMediaForm.multimedia_maxsize}";
	           if(parseInt(multimedia_maxsize,10)>0&&parseInt(multimedia_maxsize,10)<parseInt(facSize,10)/1024)
	           {  
	              alert("上传文件大小超过管理员定义大小，请修正！上传文件上限"+multimedia_maxsize+"KB");
	              return false;
	           }
	           //判断新增的文件大小，如果为0给出提示（如果这里不给判断，会导致新增文件的类型错乱）
	           if(parseInt(facSize,10)==0)
	           {
	           		alert("上传文件无内容，请重新选择文件！");
	           		return false;
	           }   
	        }
 		}
 		
        multMediaForm.action = "/workbench/media/upmediainfo.do?b_save=link";
		multMediaForm.submit();
	}
	document.getElementById("butonid").disabled=true;
}
function getFileName(){
	var titleOBJ=document.getElementsByName("filetitle");
	if(titleOBJ[0].value.length==0){//判断标题行有无信息，没有，则将上传文件名作为标题传出  zhaogd 2013-12-12
		var fileOBJ=document.getElementsByName("mediafile");
		var filename = fileOBJ[0].value;
		var endlenght = filename.lastIndexOf("\.");
		var startlenght = filename.lastIndexOf("\\");
		var file = filename.substring(startlenght+1,endlenght);
		var	IllegalString = "?*:\"<>\\/|";
 		var ft = "";
 		for(var i = 0; i < file.length; i++){
 			ft = file.substring(i, i + 1);
 			if(IllegalString.indexOf(ft) >= 0){
 				file = replaceAll(file, ft, "");
 			}
 		}
 		
		titleOBJ[0].value=file;
	}else{
		return;
	}
}
 
</script>
<hrms:themes />
<html:form action="/workbench/media/upmediainfo" enctype="multipart/form-data">
 <html:hidden name="multMediaForm" property="userbase"/> 
 <html:hidden name="multMediaForm" property="a0100"/> 

<table width="500" border="0" cellpadding="0" cellspacing="0" align="center" class="ftable">
    <br>
    <br>
    <tr height="20">
       		<td width=140 colspan="2" align="left" class="TableRow"><bean:message key="conlumn.mediainfo.titleinfo"/></td>             	      
    </tr> 
    <tr>
            <td align="right"  nowrap><bean:message key="conlumn.mediainfo.info_title"/>
            </td>
            <td align="left"  nowrap >
            
            	<hrms:optioncollection name="multMediaForm" property="fileTypeList" collection="list" />
	             <html:select name="multMediaForm" property="filesort">
	             <html:options collection="list" property="dataValue" labelProperty="dataName"/>
	             </html:select>
                                                        
            </td>
         </tr>
          <tr>
             <td align="right"  nowrap ><bean:message key="general.mediainfo.title"/></td>
             <td align="left"  nowrap ><html:text name="multMediaForm" property="filetitle" styleClass="text4"  value="" onkeyup="ValidateValue(this)"/></td>
          </tr>
         <tr>
             <td align="right"  nowrap ><bean:message key="conlumn.mediainfo.filename"/></td>
             <td align="left"  nowrap ><html:file name="multMediaForm" property="mediafile" styleClass="text4" onchange="getFileName();"/></td>
 
    </tr>
    <tr>
       <td  nowrap colspan="2" style="height:35px;" align="center"> 
	       <button onclick="document.multMediaForm.target='_self';validate('R','filetitle','标题','R','mediafile','上传文件');if(document.returnValue&&validatefilepath())wsubmit();" class="mybutton" id="butonid">
	      	<bean:message key="button.save"/>
	      </button>	
	        <hrms:submit styleClass="mybutton"  property="b_clear">
                    <bean:message key="button.clear"/>
	       </hrms:submit>    
	        <hrms:submit styleClass="mybutton"  property="b_return">
                    <bean:message key="button.return"/>
	       </hrms:submit>      
        </td>
    </tr>    
  </table>
</html:form>
