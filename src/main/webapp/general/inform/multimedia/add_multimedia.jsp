<%@ page contentType="text/html; charset=UTF-8" language="java"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ page import="com.hrms.struts.valueobject.UserView"%>
<%@ page import="com.hrms.struts.constant.WebConstant"%>
<%
	UserView userView = (UserView) session.getAttribute(WebConstant.userView);
	String bosflag="";
	if(userView!=null){
		bosflag = userView.getBosflag();
	}
%>
<style type="text/css">  
#txt{  
	height:24px;      
	position:relative 
}  

#txt p{  
	position:absolute;  
	bottom:0px;  
	padding:0px;  
	margin:0px 
}  
</style>
<script language="javascript" src="/js/function.js"></script> 
<script language="javascript">
	function upload(savetype,editflag)
	{	
		var filetype = document.getElementsByName("filetype")[0].value;
		if(filetype=="") {	
			alert(SELECT_TYPE+"!");
			return ;
		}
		var filetitle = document.getElementsByName("filetitle")[0].value;
		if(!filetitle) {
			alert(TITLE_ISNOT_EMPTY + "！");
			return;
		}
		
		var fileEx = multiMediaFileForm.picturefile.value;
		var fileName = "";
		
		if(fileEx.lastIndexOf("/") > -1)
			fileName = fileEx.substring(fileEx.lastIndexOf("/") + 1);
		else
			fileName = fileEx.substring(fileEx.lastIndexOf("\\") + 1);
		
		if(fileName && IsOverStrLength(fileName, 100)) {
			alert("上传文件的名称不能超过100个字符或50个汉字！");
			return;
		}
		
		document.getElementsByName("filepath")[0].value=fileEx;
		var flag=false;
		var fileSystem;
		try{
		 fileSystem= new ActiveXObject("Scripting.FileSystemObject");
		 
		}catch(e){
			flag=true;
		}
		if(!flag){
		if (fileEx !=""){		
			
			var picturefile = document.getElementsByName("filepath")[0];			
			if(!validateUploadFilePath(picturefile.value))
			{
				picturefile.outerHTML=picturefile.outerHTML;
				return;
			}
			var file = fileSystem.GetFile(picturefile.value);
			var fileSize = file.Size;
	        if(fileSize==-1)
            {
			     alert(FIELD_NOT_EXIST);
			     return;                
            }   
             
			var hashvo=new ParameterSet();        
		    hashvo.setValue("savetype", savetype);
		    hashvo.setValue("editflag", editflag);
		    hashvo.setValue("filetitle", filetitle);
		    hashvo.setValue("fileSize", fileSize);
		    hashvo.setValue("type", 'validfile');
		    var request=new Request({method:'post',onSuccess:validFilesizeOk,functionId:'1010090026'},hashvo);
			return;
		}
		else {
			if (editflag!='true'){			
		    	alert(SELECT_FIELD+"!");		    	
		    	return ;			
			}     
		 }
	    }
	    
	    var picturefile1 = document.getElementsByName("filepath")[0];			
			if(!validateUploadFilePath(picturefile1.value))
			{
				picturefile1.outerHTML=picturefile1.outerHTML;
				return;
			}
	    
		if (savetype=='1'){		
			document.multiMediaFileForm.action="/general/inform/multimedia/savemultimedia.do?b_save=link";
		}
		else {
			document.multiMediaFileForm.action="/general/inform/multimedia/savemultimedia.do?b_savere=link";
		
		}
		document.multiMediaFileForm.submit();
		document.getElementById('wcommit').disabled=true;
		document.getElementById('wcommit2').disabled=true;
		

	}
	
	function validFilesizeOk(outparamters){		
			var rowid=parseInt(outparamters.getValue("rowid"));
			var savetype=outparamters.getValue("savetype");
			var editflag=outparamters.getValue("editflag");
			var infomsg=outparamters.getValue("infomsg");
			if ((infomsg!=null) &&(infomsg!="")){
			  alert(infomsg);
			  return;
			}		
		
			if (savetype=='1'){		
				document.multiMediaFileForm.action="/general/inform/multimedia/savemultimedia.do?b_save=link";
	
			}
			else {
				document.multiMediaFileForm.action="/general/inform/multimedia/savemultimedia.do?b_savere=link";
			
			}
			document.multiMediaFileForm.submit();
			document.getElementById('wcommit').disabled=true;
			document.getElementById('wcommit2').disabled=true;
	}
	
	
	function cleartext()
	{
		multiMediaFileForm.filetitle.value='';
	}

	function return_to()
	{
		document.multiMediaFileForm.action="/general/inform/multimedia/opermultimedia.do?b_query=link&a0100=${multiMediaFileForm.a0100}&multimediaflag=${multiMediaFileForm.multimediaflag}&isvisible=${multiMediaFileForm.isvisible}";
		document.multiMediaFileForm.submit(); 
	}
	
	function   NoExec()   
  	{   
          if(event.keyCode==13||event.keyCode==222)   event.returnValue=false; 
          document.onkeypress=NoExec;     
  	}  
  	
	function getFileName(){
		var titleOBJ=document.getElementsByName("filetitle");
		if(titleOBJ[0].value.length==0){//判断标题行有无信息，没有，则将上传文件名作为标题传出  zhaogd 2013-12-12
			var fileOBJ=document.getElementsByName("picturefile");
			var filename = fileOBJ[0].value;
			var endlenght = filename.lastIndexOf("\.");
			var startlenght = filename.lastIndexOf("\\");
			var file = filename.substring(startlenght+1,endlenght);
			titleOBJ[0].value=file;
		}else{
			return;
		}
	}
	
</script>
<form name="multiMediaFileForm" method="post" action="/general/inform/multimedia/opermultimedia" enctype="multipart/form-data" >
<html:hidden name="multiMediaFileForm" property="filepath"/>
<html:hidden name="multiMediaFileForm" property="isvisible"/>

<%if("hcm".equals(bosflag)){ %>
	<table width="100%" border="0" cellpadding="0" cellspacing="0" align="center" class="ftable">
<%}else{ %>
	<table width="100%" style="margin-top: 10px" border="0" cellpadding="0" cellspacing="0" align="center" class="ftable">
<%} %>
	<tr height="20">
		<td align="left" colspan="2" class="TableRow"><bean:message key="conlumn.mediainfo.titleinfo"/></td>           	             	                              	      
	</tr> 
	<tr>
		<td align="right"  nowrap><bean:message key="general.mediainfo.type"/>&nbsp;</td>
		<td align="left"  nowrap >
			<hrms:optioncollection name="multiMediaFileForm" property="fileTypeList" collection="list" />
			<html:select name="multiMediaFileForm" property="filetype" size="1" value="${multiMediaFileForm.filetype}">
				<html:options collection="list" property="dataValue" labelProperty="dataName"/>
			</html:select> 
		</td>
	</tr>
	<tr>
		<td align="right"  nowrap ><bean:message key="general.mediainfo.title"/>&nbsp;</td>
		<td align="left"  nowrap ><html:text name="multiMediaFileForm" property="filetitle" style="width:300px" maxlength="100" styleClass="text4" /></td>
	</tr>
	<tr>
		<td align="right"  nowrap ><bean:message key="kq.set.card.explain"/>&nbsp;</td>
		<td align="left" >
			<html:textarea name="multiMediaFileForm" property="decription" cols="80" rows="20" style="width:300px;height:140px" styleId="shry">
			</html:textarea>	
		</td>
	</tr>
	<tr>
		<td align="right"  nowrap ><bean:message key="conlumn.mediainfo.filename"/>&nbsp;</td>
		<td align="left"  nowrap ><html:file name="multiMediaFileForm" property="picturefile" onchange="getFileName()" style="width:300px" styleClass="text6" onkeydown="return false" onkeyup="return false" /><br>
			<div id="txt">
				<p>
					提示：设置本站为信任站点才可以保存文件。
				</p>
			</div>
	           
		</td>  
	</tr>
</table>
<table width="100%" border="0" cellpadding="0" cellspacing="0" align="center">
	<tr>
		<td  nowrap colspan="2" align="center" style="height:35px;">
			<html:button styleClass="mybutton" styleId="wcommit" property="b_save" onclick="upload('1','${multiMediaFileForm.editFlag}');">
				<bean:message key="button.save"/>
			</html:button>
			<html:button styleClass="mybutton"  styleId="wcommit2" property="b_savere" onclick="upload('2','${multiMediaFileForm.editFlag}');">
				<bean:message key="button.savereturn"/>
			</html:button>	  
			<html:button styleClass="mybutton" property="b_return" onclick="return_to();">
				<bean:message key="button.return"/>
			</html:button>	        
		</td>
	</tr>
</table>
</form>
