<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ page import="com.hjsj.hrms.actionform.sys.cms.ChannelForm,com.hrms.hjsj.sys.EncryptLockClient,
					com.hrms.struts.constant.WebConstant,com.hrms.struts.valueobject.UserView" %>
					<%@ page import="com.hrms.struts.valueobject.UserView"%>
<%@ page import="com.hrms.struts.constant.WebConstant"%>
<%
    UserView userView=(UserView)session.getAttribute(WebConstant.userView);
    String bosflag= userView.getBosflag();//得到系统的版本号
    
%>  
<%
ChannelForm channelForm=(ChannelForm)session.getAttribute("channelForm");
String projectpath=channelForm.getProjectPath();
    boolean isFive=false;
    EncryptLockClient lockclient=(EncryptLockClient)session.getServletContext().getAttribute("lock");
    if(lockclient!=null){
       if(lockclient.getVersion()>=50)
           isFive=true;
   }
 %>
 
<script type="text/javascript" src="../../ext/ext-all.js" ></script>
 <script language="JavaScript" src="/js/validate.js"></script>
<script type="text/javascript" src="/general/sys/hjaxmanage.js"></script>
<script type="text/javascript">
<!--
function subok()
{
   var filePath = document.channelForm.logofile.value;
   if(trim(filePath).length!=0)
	{
			 if(!validateUploadFilePath(filePath)){//文件上传漏洞
					alert("上传文件为不符合要求！请选择正确的文件上传！");
					return;
				}   
			var extendFile=trim(filePath.substring(filePath.indexOf(".")+1,filePath.length));
			while(extendFile.indexOf(".")!=-1)
			{
			      extendFile=trim(extendFile.substring(extendFile.indexOf(".")+1,extendFile.length));
			}
			if(extendFile.toLowerCase()!='jpg')
			{
				alert("上传文件1应为.jpg格式!");
				return;
			}
			if(!AxManager.setup(null, 'FileView', 0, 0))
  				return false;			
			var  obj=document.getElementById('FileView'); 
             if (obj != null)
             {
                var facSize=obj.GetFileSize(filePath); 
                if(parseInt(facSize)==-1)   
                {
                   alert("文件1不存在，请输入正确的文件路径！");
                   return;
                }              
             }
		}else{
		  alert("文件1请选择文件！");
		  return;
		}
		var onefilePath = document.channelForm.oneFile.value;
        if(trim(onefilePath).length!=0)
	   {
	   			if(!validateUploadFilePath(onefilePath)){//文件上传漏洞
					alert("上传文件为不符合要求！请选择正确的文件上传！");
					return;
				}   
			var extendFile=trim(onefilePath.substring(onefilePath.indexOf(".")+1,onefilePath.length));
			while(extendFile.indexOf(".")!=-1)
			{
			      extendFile=trim(extendFile.substring(extendFile.indexOf(".")+1,extendFile.length));
			}
			if(extendFile.toLowerCase()!='jpg')
			{
				alert("上传文件3应为.jpg格式!");
				return;
			}
			if(!AxManager.setup(null, 'FileView', 0, 0))
  				return false;			
			var  obj=document.getElementById('FileView'); 
             if (obj != null)
             {
                var facSize=obj.GetFileSize(onefilePath); 
                if(parseInt(facSize)==-1)   
                {
                   alert("文件3不存在，请输入正确的文件路径！");
                   return;
                }              
             }
		}else{
		   alert("文件3请选择文件！");
		   return;
		}
		var twofilePath = document.channelForm.twoFile.value;
        if(trim(twofilePath).length!=0)
	   {
	 		  if(!validateUploadFilePath(twofilePath)){//文件上传漏洞
					alert("上传文件为不符合要求！请选择正确的文件上传！");
					return;
				}  
			var extendFile=trim(twofilePath.substring(twofilePath.indexOf(".")+1,twofilePath.length));
			while(extendFile.indexOf(".")!=-1)
			{
			      extendFile=trim(extendFile.substring(extendFile.indexOf(".")+1,extendFile.length));
			}
			if(extendFile.toLowerCase()!='jpg')
			{
				alert("上传文件2应为.jpg格式!");
				return;
			}
			if(!AxManager.setup(null, 'FileView', 0, 0))
  				return false;			
			var  obj=document.getElementById('FileView'); 
             if (obj != null)
             {
                var facSize=obj.GetFileSize(twofilePath); 
                if(parseInt(facSize)==-1)   
                {
                   alert("文件2不存在，请输入正确的文件路径！");
                   return;
                }              
             }
		}else{
		  alert("文件2请选择文件！");
		  return;
		}
   document.getElementById("pathvalue").value="<%=projectpath%>";
   channelForm.action="/sys/cms/uploadLogo.do?b_upload=upload&isClose=2&type=0";
   channelForm.submit();
}
function subokF()
{
   var filePath = document.channelForm.logofile.value;
   if(trim(filePath).length!=0)
	{
	   if(!validateUploadFilePath(filePath)){//文件上传漏洞
			alert("上传文件为不符合要求！请选择正确的文件上传！");
			return;
		}   
		var extendFile=trim(filePath.substring(filePath.indexOf(".")+1,filePath.length));
		while(extendFile.indexOf(".")!=-1)
		{
		      extendFile=trim(extendFile.substring(extendFile.indexOf(".")+1,extendFile.length));
		}
		if(extendFile.toLowerCase()!='gif'&&extendFile.toLowerCase()!='swf')
		{
			alert("上传文件1应为.gif或swf格式!");
			return;
		}
		if(!AxManager.setup(null, 'FileView', 0, 0)&&!Ext.isChrome)
					return false;			
		var  obj=document.getElementById('FileView'); 
        if (obj != null)
        {
           var facSize=obj.GetFileSize(filePath); 
           if(parseInt(facSize)==-1)   
           {
              alert("文件1不存在，请输入正确的文件路径！");
              return;
           }              
        }
		var oobj=document.getElementById("two");
		for(var i=0;i<oobj.options.length;i++)
		{
		   if(oobj.options[i].selected)
		   {
		       if(oobj.options[i].value=='-1')
		       {
		           alert("文件1请选择文件类型！");
		           return;
		       }
		   }
		}
	}else{
		   alert("文件1请选择文件！");
		   return;
	}
	var twofilePath = document.channelForm.twoFile.value;
    if(trim(twofilePath).length!=0)
    {
 		if(!validateUploadFilePath(twofilePath)){//文件上传漏洞
			alert("上传文件为不符合要求！请选择正确的文件上传！");
			return;
		}   
		var extendFile=trim(twofilePath.substring(twofilePath.indexOf(".")+1,twofilePath.length));
		while(extendFile.indexOf(".")!=-1)
		{
		      extendFile=trim(extendFile.substring(extendFile.indexOf(".")+1,extendFile.length));
		}
		if(extendFile.toLowerCase()!='gif'&&extendFile.toLowerCase()!='swf')
		{
			alert("上传文件2应为.gif或swf格式!");
			return;
		}
		if(!AxManager.setup(null, 'FileView', 0, 0)&&!Ext.isChrome)
 				return false;			
		var  obj=document.getElementById('FileView'); 
            if (obj != null)
            {
               var facSize=obj.GetFileSize(twofilePath); 
               if(parseInt(facSize)==-1)   
               {
                  alert("文件2不存在，请输入正确的文件路径！");
                  return;
               }              
            }
            var oobj=document.getElementById("one");
    	for(var i=0;i<oobj.options.length;i++)
    	{
      	   if(oobj.options[i].selected)
	      {
	       if(oobj.options[i].value=='-1')
	       {
	           alert("文件2请选择文件类型！");
	           return;
	       }
     	   }
    	}
	}
	else{
	  alert("文件2请选择文件！");
	  return;
	}
   document.getElementById("pathvalue").value="<%=projectpath%>";
   channelForm.action="/sys/cms/uploadLogo.do?b_upload=upload&isClose=2&type=1";
   channelForm.submit();
}

function winColse(){
	 var browserName=navigator.appName;
	    if (browserName=="Netscape") {      
	        window.parent.parent.close();     
	    } else {    
	        window.close();    
	    }  
}
<%
  if(request.getParameter("isClose")!=null&&request.getParameter("isClose").equals("2"))
  {
     out.print("alert('文件上传成功！');");
     out.print("if(Ext.isChrome){");
     out.print("window.parent.parent.close(); ");
     out.print("}else{");
     out.print("window.close();");
     out.print("}");
  }
    int versionFlag = 1;
	if (userView != null)
		versionFlag = userView.getVersion_flag(); // 1:专业版 0:标准版
%>
//-->
</script>
<html:form action="/sys/cms/uploadLogo" enctype="multipart/form-data" method="post">
<%
    if(bosflag!=null&&!bosflag.equals("hcm")){
%>
<Br>
<%
}
%>
<%if(isFive&&versionFlag==1){ %>
 <table width="720" border="0" cellspacing="0"  align="center" cellpadding="0" class="ListTable" style="margin-top:10px;"> 
<tr height="20">
			<td class="TableRow_lrt" align="left" colspan="3">
			<input type="hidden" name="path" value="" id="pathvalue"/>
				<bean:message key="kh.field.selectfieldfile"/>(可上传GIF图片或FLASH文件)
			</td>
		</tr>
		<tr>
		<td class="RecordRow"  colspan='3' nowrap>
		<div style='overflow:auto;width:630px;height:150px;'>
		<table>
		<tr><td>
		<img src="/images/zp_upload_demo.gif" border="0">
		</td>
		</tr>
		</table>
		</div>
		</td>
		</tr>
		<tr>
		<td align="right" class="RecordRow" nowrap> 文件1(hire_header):</td>
		<td align="left" class="RecordRow" >&nbsp;文件类型：<select name="lfType" id="two"><option value="-1">请选择...</option><option value="0">GIF</option><option value="1">SWF</option></select>
		</td>
		<td align="left" class="RecordRow" nowrap><html:file name="channelForm" property="logofile" size="30" styleClass="TEXT4"/>
		<font color='red'>(1000*124)</font>
		</td>
		
		</tr>
		<tr>
		<td align="right" class="RecordRow" nowrap> 文件2(zp_homepage_bck):</td>
		<td align="left" class="RecordRow">&nbsp;文件类型：<select name="hbType" id="one"><option value="-1">请选择...</option><option value="0">GIF</option><option value="1">SWF</option></select>
		</td>
		<td align="left" class="RecordRow" nowrap><html:file name="channelForm" property="twoFile" size="30" styleClass="TEXT4"/>
		<font color='red'>(1000*505)</font>
		</td>
		
		</tr>
			<tr>
			<td colspan="3" align="center" class="RecordRow" style="padding-top:3px;padding-bottom:3">
			<input type="button" name="ok" value="<bean:message key="button.ok"/>" class="mybutton" onclick="subokF();"/>
			<input type="button" name="clo" value="<bean:message key="button.close"/>" class="mybutton" onclick="winColse()"/>
			</td>
			</tr>
			</table>


<%}else{ %>
 <table width="720" border="0" cellspacing="0"  align="center" cellpadding="0" class="ListTable" style="margin-top:10px;">
<tr height="20">
			<td class="TableRow_lrt" align="left" colspan="2"><input type="hidden" name="path" value="" id="pathvalue"/>
				<bean:message key="kh.field.selectfieldfile"/>
			</td>
		</tr>
		<tr>
		<td class="RecordRow"  colspan='2' nowrap>
		<img src="/images/demologo.png" height="100" width="600">
		</td>
		</tr>
		<tr>
		<td align="right" class="RecordRow" nowrap> 文件1（header_logo.jpg）:</td><td align="left" class="RecordRow" nowrap><html:file name="channelForm" property="logofile" accept="jpg" size="40"/><font color='red'>(166*157)</font></td>
		</tr>
		<tr>
		<td align="right" class="RecordRow" nowrap> 文件2（header_center.jpg）:</td><td align="left" class="RecordRow" nowrap><html:file name="channelForm" property="twoFile" accept="jpg" size="40"/><font color='red'>(4*157)</font></td>
		</tr>
		<tr>
		<td align="right" class="RecordRow" nowrap> 文件3（header_right.jpg）:</td><td align="left" class="RecordRow" nowrap><html:file name="channelForm" property="oneFile" accept="jpg" size="40"/><font color='red'>(801*157)</font></td>
		</tr>
		
			<tr>
			<td colspan="2" align="center" class="RecordRow" style="padding-top:3px;padding-bottom:3">
			<input type="button" name="ok" value="<bean:message key="button.ok"/>" class="mybutton" onclick="subok();"/>
			<input type="button" name="clo" value="<bean:message key="button.close"/>" class="mybutton" onclick="window.close();"/>
			</td>
			</tr>
			</table>
<%} %>
</html:form>
