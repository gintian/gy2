<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean"%>
<%@ taglib uri="/tags/struts-logic" prefix="logic"%>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms"%>
<script type="text/javascript" src="/general/sys/hjaxmanage.js"></script>
<script type="text/javascript">
<!--
<%if(request.getParameter("isClose")!=null&&request.getParameter("isClose").equals("1")){%>
   returnValue = "1";
   window.close();
<%}%>
function uploadG(){
      var filePath = document.innerEmployPortalForm.attachFile.value;
      var type = document.getElementById("type").value;
      //修改时，可以只改文件名
      if(type=='0')
      {
         if(trim(filePath).length==0)
         {
            alert("请选择文件！");
            return;
         }
      }
      
      if(trim(filePath).length>0){
		 if(!AxManager.setup(null, 'FileView', 0, 0))
		  	return false;      
         var  obj=document.getElementById('FileView'); 
         if (obj != null)
         {
            var facSize=obj.GetFileSize(filePath); 
            if(parseInt(facSize)==-1)   
            {
               alert("文件不存在，请输入正确的文件路径！");
               return;
            }              
            var  photo_maxsize="10240";   
            if(parseInt(photo_maxsize,10)>0&&parseInt(photo_maxsize,10)<parseInt(facSize,10)/1024)
            {  
                   
                alert("上传文件请控制在10M以下！");
                return;
            } 
          }    
     }

     if (!validateUploadFilePath(filePath))
         return;
     
    document.innerEmployPortalForm.action="/hire/innerEmployNetPortal/initInnerEmployPos.do?b_save=link&isClose=1";
    document.innerEmployPortalForm.target="_self";
	document.innerEmployPortalForm.submit();
}
//-->
</script>
 <base id="mybase" target="_self">
 <form name="innerEmployPortalForm" method="post" action="/hire/innerEmployNetPortal/initInnerEmployPos" enctype="multipart/form-data"> <br>
 <table width="80%" border="0" cellpadding="0" cellspacing="0" align="center" class="ListTable">
 <html:hidden name="innerEmployPortalForm" property="zpkA0100"/>
 <html:hidden name="innerEmployPortalForm" property="zpDbName"/>
 <html:hidden name="innerEmployPortalForm" property="i9999"/>
  <html:hidden name="innerEmployPortalForm" property="type"/>
<tr>
<td colspan="2" class="TableRow">
 上传简历附件
</td>
</tr>
<tr>
<td align="right" class="RecordRow">
 文件名称：
</td>
<td align="left" class="RecordRow">
<html:text property="fileName" name="innerEmployPortalForm" size="30" styleClass="TEXT4"></html:text>
</td>
</tr>
<tr>
<td align="right" class="RecordRow">
上传文件：
</td>
<td align="left" class="RecordRow">
<input type="file" name="attachFile" size="30" class="TEXT4"/>
</td>
</tr>
<tr><td colspan="2" align="center" style="padding-top:3px">
<input  type="button" name="n" value="<bean:message key="button.ok"/>" class="mybutton" onclick="uploadG();"/>
<input  type="button" name="d" value="<bean:message key="button.close"/>" class="mybutton" onclick="window.close();"/>
</td></tr>
 </table>
 </form>
