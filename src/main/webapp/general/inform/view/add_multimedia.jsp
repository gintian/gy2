<%@ page contentType="text/html; charset=UTF-8" language="java"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>

<script type="text/javascript" src="/general/sys/hjaxmanage.js"></script>
<script language="javascript">
	function upload()
	{
		var fileEx = mInformForm.picturefile.value;
		 if(fileEx == "")
		 {
	    	alert(SELECT_FIELD+"!");
	    	
	    	return ;
	     }
	     if(!validateUploadFilePath(fileEx)){
	     	return;
	     }
	     if(getBrowseVersion()){
	     	if(!AxManager.setup(null, 'FileView', 0, 0))
  				return false;
  			var  obj=document.getElementById('FileView'); 
         	if (obj != null)
         	{
                var facSize=obj.GetFileSize(fileEx);
                if(facSize==-1)
                {
				     alert(FIELD_NOT_EXIST);
				     return;                
                }   
  			}
  			else
  		   		return;
  		   	document.getElementsByName("filepath")[0].value=fileEx;
			document.getElementsByName("i9999")[0].value="";
			var flag = document.getElementsByName("filetype")[0].value;
			if(flag=="")
			{	
				alert(SELECT_TYPE+"!");
				return ;
			}
	     }else{ //非IE浏览器判断插件对象是否存在 不校验  wangb 20180126
	     	if(typeof(AxManager) == "undefined")
  				return false;
  			
			document.getElementsByName("filepath")[0].value=fileEx;
			document.getElementsByName("i9999")[0].value="";
	     }
		document.mInformForm.action="/general/inform/emp/view/savemultimedia.do?b_query=link";
		document.mInformForm.submit();
		document.getElementById('wcommit').disabled=true;
	}
	
	function cleartext()
	{
		mInformForm.filetitle.value='';
		//重置时，清空所选的文件
		var isIE = /msie/i.test(navigator.userAgent) && !window.opera;   
		if (isIE)
	 		document.getElementsByName("picturefile")[0].createTextRange().execCommand("delete");
		else
			document.getElementsByName("picturefile")[0].value="";
			
	}

	function return_to()
	{
		//返回时清空文件名
		mInformForm.filetitle.value='';
		document.mInformForm.action="/general/inform/emp/view/opermultimedia.do?b_query=link&a0100=${mInformForm.a0100}&multimediaflag=${mInformForm.multimediaflag}&isvisible=${mInformForm.isvisible}";
		document.mInformForm.submit(); 
	}
	
	function   NoExec()   
  	{   
          if(event.keyCode==13||event.keyCode==222)   event.returnValue=false; 
          document.onkeypress=NoExec;     
  	}  
  	
  	/* add by wangchaoqun on 2014-9-12 对上传文件名进行验证，过滤非法文件 */
  	function getFilename(){
  		var titleOBJ=document.getElementsByName("filetitle");
		if(titleOBJ[0].value.length==0){//判断标题行有无信息，没有，则将上传文件名作为标题传出  zhaogd 2013-12-12
			var fileOBJ=document.getElementsByName("picturefile");
			var filename = fileOBJ[0].value;
			if(!validateUploadFilePath(filename))
				return;
			
			var endlenght = filename.lastIndexOf("\.");
			var startlenght = filename.lastIndexOf("\\");
			var file = filename.substring(startlenght+1,endlenght);
			titleOBJ[0].value=file;
		}else{
			return;
		}
  	}
	
</script>
<form name="mInformForm" method="post" action="/general/inform/emp/view/opermultimedia" enctype="multipart/form-data" >
<html:hidden name="mInformForm" property="filepath"/>
<html:hidden name="mInformForm" property="i9999"/>
<html:hidden name="mInformForm" property="isvisible"/>
<table width=360 border="0" cellpadding="0" cellspacing="0" align="center" class="ftable">
    <br>
    <tr height="20">
       		<!-- td width=10 valign="top" class="tableft"></td>
       		<td width=140 align=center class="tabcenter">&nbsp;<bean:message key="conlumn.mediainfo.titleinfo"/>&nbsp;</td>
       		<td width=10 valign="top" class="tabright"></td>
       		<td valign="top" class="tabremain" width="300"></td>  -->
       		<td align="left" colspan="2" class="TableRow">&nbsp;<bean:message key="conlumn.mediainfo.titleinfo"/>&nbsp;</td>           	             	                              	      
    </tr> 
    <tr>

            <td align="right"  nowrap><bean:message key="general.mediainfo.type"/>&nbsp;</td>
            <td align="left"  nowrap >
	           
            	 <hrms:optioncollection name="mInformForm" property="fileTypeList" collection="list" />
	             <html:select name="mInformForm" property="filetype" size="1" value="${mInformForm.multimediaflag}">
	             <html:options collection="list" property="dataValue" labelProperty="dataName"/>
	             </html:select>

            </td>
         </tr>
          <tr>
             <td align="right"  nowrap ><bean:message key="general.mediainfo.title"/>&nbsp;</td>
             <td align="left"  nowrap ><html:text name="mInformForm" property="filetitle" styleClass="textColorWrite" onkeydown="NoExec()" /></td>
          </tr>
         <tr>
             <td align="right"  nowrap ><bean:message key="conlumn.mediainfo.filename"/>&nbsp;</td>
             <td align="left"  nowrap ><html:file name="mInformForm" property="picturefile" styleClass="textColorWrite" onkeydown="return false" onkeyup="return false" onchange="getFilename()"/>
             
             </td>  
    </tr>
    <tr>
       <td  nowrap colspan="2" align="center" style="height:35px;">
           
               <html:button styleClass="mybutton" styleId="wcommit" property="b_next" onclick="upload();">
		      		<bean:message key="button.save"/>
			   </html:button>
			   <html:button styleClass="mybutton" property="b_clear" onclick="cleartext();">
		      		<bean:message key="button.clear"/>
			   </html:button>	  
			   <html:button styleClass="mybutton" property="b_return" onclick="return_to();">
		      		<bean:message key="button.return"/>
			   </html:button>	        
        </td>
    </tr>    
  </table>
</form>
<script>
if(!getBrowseVersion() || getBrowseVersion() == 10){//兼容非IE浏览器 样式问题  bug 34742 wangb 20180208
	var picturefile = document.getElementsByName('picturefile')[0]; //下载文本框 按钮显示错位
	picturefile.style.height ='26px';
	picturefile.style.paddingTop ='1px';
	var userAgent = navigator.userAgent; //取得浏览器的userAgent字符串 
	var isFF = userAgent.indexOf("Firefox") > -1; 
	if(isFF){//火狐浏览器 单独处理 下载框 样式   
		picturefile.style.paddingBottom ='27px';
		picturefile.style.marginBottom ='3px';
		picturefile.style.marginTop ='3px';
	}
}
</script>
