<%@ page contentType="text/html; charset=UTF-8" language="java"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<script type="text/javascript" src="/general/sys/hjaxmanage.js"></script>
<script language="javascript">
       parent.mil_menu.location.reload();
       function validate()
       {
          var f_obj=document.getElementsByName("picturefile");
          if(f_obj)
          {
             var value=f_obj[0].value; 
             //【5208】我的信息，信息维护，上传照片，如果未选择文件，提示信息不正确。 jingq add 2014.11.24
             if(value==""){
            	 alert("请选择上传的照片！");
            	 return false;
             }
             var photoEx=value.substring(value.lastIndexOf(".")); 
             photoEx=photoEx.toLowerCase();
             /*if(photoEx!=".gif"&&photoEx!=".jpg"&&photoEx!=".bmp"&&photoEx!=".jpeg"){ 
                   alert("上传文件类型不正确！仅限于.gif .jpg .bmp .jpeg文件")*/
             if(photoEx!=".jpg"&&photoEx!=".bmp"&&photoEx!=".jpeg"){ //缺陷2087 zgd 2014-7-11 照片上传不支持jif格式。
                   alert("上传文件类型不正确！仅限于.jpg .bmp .jpeg文件");
                   return false;
             }
             if(!window.ActiveXObject){
            	 return true;
             }
             if(!AxManager.setup(null, 'FileView', 0, 0))
			  	return false;
             var  obj=document.getElementById('FileView'); 
             if (obj != null)
             {
                var facSize=obj.GetFileSize(value);                  
                var  photo_maxsize="${selfInfoForm.photo_maxsize}"   
                if(parseInt(photo_maxsize,10)>0&&parseInt(photo_maxsize,10)<parseInt(facSize,10)/1024)
                {  
                   
                   alert("上传文件大小超过管理员定义大小，请修正！上传文件上限"+photo_maxsize+"KB");
                   return false;
                }     
             }
          }
       }
   </script>
   <hrms:themes />
<html:form action="/selfservice/selfinfo/upphotoinfo" enctype="multipart/form-data" onsubmit="return validate()">
<table width="500" border="0" cellspacing="0"  align="center" cellpadding="0" style="margin-top:10px;">
 <html:hidden name="selfInfoForm" property="userbase"/> 
 <html:hidden name="selfInfoForm" property="actiontype" /> 
 <html:hidden name="selfInfoForm" property="a0100"/> 
 <html:hidden name="selfInfoForm" property="i9999"/>  
    <tr height="20">
       		<!--  <td width=10 valign="top" class="tableft"></td>
       		<td width=140 align=center class="tabcenter">&nbsp;<bean:message key="conlumn.info.phototitle"/>&nbsp;</td>
       		<td width=10 valign="top" class="tabright"></td>
       		<td valign="top" class="tabremain" width="500"></td> --> 
       		<td align=center class="TableRow"><bean:message key="conlumn.info.phototitle"/></td>            	      
    </tr> 
 <tr>
  <td  class="framestyle9" height="60">
      <table>
         <tr>
            <td align="right" width="100px" nowrap><bean:message key="conlumn.info.photolabel"/></td>
            <td align="left" nowrap ><html:file name="selfInfoForm" style="height:25px" property="picturefile" styleClass="text6"/>
            </td>
      	</tr>
       	<tr><td align="right" width="100px" nowrap><bean:message key="label.description"/></td><td align="left"  nowrap>：照片格式扩展名支持".bmp",".jpg",".jpeg"。</td></tr>        
      </table>
   </td>  
  </tr>
 <tr> 
<td align="center"  nowrap height="35">  
           
           <hrms:priv func_id="01030117">  
               <hrms:submit styleClass="mybutton"  property="b_save" onclick="document.selfInfoForm.target='_self';return document.returnValue;">
                    <bean:message key="button.ok"/>
                     </hrms:submit>  
            </hrms:priv>
	   
  </td>
 </tr>     
 </table>
</html:form>
