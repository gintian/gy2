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
             if(value == ""){
             	   alert("请选择照片");
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
             if(AxManager.setup(null, 'FileView', 0, 0)){
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
          //tianye add 
          var s = document.getElementsByName('b_save')[0];
          s.disabled= true;//按钮失效后不能提交，必须写下面两句话
          selfInfoForm.action="/workbench/info/upphotoinfo.do?b_save=link&flag=save&fileurl="+$URL.encode(value);
          selfInfoForm.enctype="multipart/form-data";
          //selfInfoForm.submit();//切忌提交，会报错的
       }
       
	function deletephoto(){
		if(!confirm("确定要清除照片吗?"))
      		return false;
   		selfInfoForm.action="/workbench/info/upphotoinfo.do?b_save=link&flag=reset";
       	selfInfoForm.submit();
	}
       
       function chan() {
       	if(top.document.frames['mil_menu']) {
       		if (top.document.frames['mil_menu'].document) {
       			var photo = top.document.frames['mil_menu'].document.getElementById("photo");
       			if (photo) {
       				if (document.getElementsByName("picturefile")[0])
       				photo.src = document.getElementsByName("picturefile")[0].value;
       			}
       		}
       	}
       }
   </script>
<html:form action="/workbench/info/upphotoinfo" enctype="multipart/form-data" onsubmit="return validate()">
<table width="500" border="0" cellpadding="0" cellspacing="0" align="center" style="margin-top:10px;">
 <html:hidden name="selfInfoForm" property="userbase"/> 
 <html:hidden name="selfInfoForm" property="actiontype" /> 
 <html:hidden name="selfInfoForm" property="a0100"/> 
 <html:hidden name="selfInfoForm" property="i9999"/> 
    <tr height="20">
       		<!-- td width=10 valign="top" class="tableft"></td>
       		<td width=140 align=center class="tabcenter"></td>
       		<td width=10 valign="top" class="tabright"></td>
       		<td valign="top" class="tabremain" width="500"></td>   -->
       		
       		<td align="left" colspan="4" class="TableRow"><bean:message key="conlumn.info.phototitle"/></td>            	      
    </tr> 
 <tr> 
  <td colspan="4" class="framestyle3" height="60">
      <table>
         <tr>
            <td align="right" width="100px" nowrap><bean:message key="conlumn.info.photolabel"/></td>
            <td align="left"  nowrap ><html:file name="selfInfoForm" property="picturefile" style="height:25px;" styleClass="text6"/>
            </td>
         </tr>
         <tr><td align="right" width="100px" nowrap><bean:message key="label.description"/></td><td align="left"  nowrap>：照片格式扩展名支持".bmp",".jpg",".jpeg"。</td></tr>        
      </table>
     </td>  
    </tr>
 <tr>
 <td align="center"  nowrap colspan="4" style="height:35px">  
 <logic:notEqual name="selfInfoForm" property="a0100" value="su">
                <logic:notEqual name="selfInfoForm" property="a0100" value="A0100">
           <hrms:priv func_id="260604,0304019">  
                <html:button styleClass="mybutton" property="b_clean" onclick="deletephoto();">
					<bean:message key="conlumn.info.cleanphoto"/>
			   </html:button>
               <hrms:submit styleClass="mybutton"  property="b_save" onclick="document.selfInfoForm.target='_self';chan();return document.returnValue;">
                    <bean:message key="button.ok"/>
	       </hrms:submit>  
	    </hrms:priv> 
	    </logic:notEqual>
	      </logic:notEqual>
  </td>
 </tr>    
 </table>
</html:form>
