<%@ page contentType="text/html; charset=UTF-8" language="java"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ page import=" com.hjsj.hrms.actionform.report.edit_report.PictureReportForm,java.io.File"
				 %>
<%@ page import="com.hjsj.hrms.utils.PubFunc"%>				 
<%
//session里注册图片
PictureReportForm pictureReportForm = (PictureReportForm)session.getAttribute("pictureReportForm");
String	photofile = "";
if(pictureReportForm!=null&&pictureReportForm.getPhotofile()!=null){
  File tempFile =   new File(System.getProperty("java.io.tmpdir")+pictureReportForm.getPhotofile());   
		    	com.hjsj.hrms.servlet.ServletUtilities.registerPhotoForDeletion(tempFile, session);
	photofile = PubFunc.encrypt(pictureReportForm.getPhotofile());   	
		    	}
 %>
<script type="text/javascript" src="/general/sys/hjaxmanage.js"></script>
<script type="text/javascript">
<!--
	function getPohtoInfo()
	{
		var img=document.getElementById("photoid");
		if(img==null)
			return;
		var filePath = img.src;
		if(!validateUploadFilePath(filePath)){
			return;
		}
	    window.returnValue=img.src;
	    window.close();			
	}
	
	
	
	
	 function validateSize()
       {
          var f_obj=document.getElementsByName("picturefile");
          if(f_obj)
          {
             var value=f_obj[0].value;            
             var photoEx=value.substring(value.lastIndexOf(".")); 
             photoEx=photoEx.toLowerCase();
             if(photoEx!=".gif"&&photoEx!=".jpg"&&photoEx!=".bmp"&&photoEx!=".jpeg"){ 
                   alert("上传文件类型不正确！仅限于.gif .jpg .bmp .jpeg文件");
                   return false;
             }
             if(!AxManager.setup(null, 'FileView', 0, 0))
  				return false;
             var  obj=document.getElementById('FileView'); 
             if (obj != null)
             {
                var facSize=obj.GetFileSize(value);                  
                var  photo_maxsize="${pictureReportForm.photo_maxsize}"   
                if(parseInt(photo_maxsize,10)>0&&parseInt(photo_maxsize,10)<parseInt(facSize,10)/1024)
                {  
                   
                   alert("上传文件大小超过管理员定义大小，请修正！上传文件上限"+photo_maxsize+"KB");
                   return false;
                }     
             }
          }
       }
	
	
	
	
	
	
	
</script>	
<base id="mybase" target="_self">
<html:form action="/report/edit_report/pictureReport" enctype="multipart/form-data" onsubmit="return validateSize()"  >
<table width="100%" border="0" cellspacing="1"  align="center" cellpadding="1" class="ListTable">
 <br>
    <tr height="20">
       		<td class="TableRow"><bean:message key="conlumn.info.phototitle"/></td>
    </tr> 
 <tr>
  <td class="framestyle9" height="40">
      <table>
      	<logic:notEqual name="pictureReportForm" property="photofile" value="">
         <tr>
            <td align="right"  nowrap>上传照片</td>
            <td align="left"  nowrap >
           		<img id="photoid" src="/servlet/DisplayOleContent?filename=<%=photofile %>" width="80" border="0" height="120">  
            </td>
       </tr>  
       </logic:notEqual>     
         <tr>
            <td align="right"  nowrap><bean:message key="conlumn.info.photolabel"/>&nbsp;</td>
            <td align="left"  nowrap >
             <html:file name="pictureReportForm" property="picturefile" styleClass="text6"/>  </td>
       </tr>        
      </table>
   </td>  
  </tr>
</table> 
 <table align="center"> 
 <tr> 
<td align="center"  nowrap >  
  <br>
      &nbsp;&nbsp;
                <hrms:submit styleClass="mybutton"   property="b_save" onclick="document.pictureReportForm.target='_self';validate('R','picturefile','上传照片');return document.returnValue;">
                    <bean:message key="lable.fileup"/>
	            </hrms:submit>  
      &nbsp;            
	 	        <button class="mybutton" onclick="getPohtoInfo();">
            		<bean:message key="button.ok"/>
	 	        </button>		            
      &nbsp;            
	 	        <button class="mybutton" onclick="window.close();">
            		<bean:message key="button.close"/>
	 	        </button>		       
  </td>
 </tr>     
 </table>
</html:form>

