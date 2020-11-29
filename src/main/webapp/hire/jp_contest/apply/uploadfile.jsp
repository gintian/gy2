<%@ page contentType="text/html; charset=UTF-8" language="java"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>

<script language="javascript">
	function upload()
	{
		 var fileEx = jingpinForm.uploadfile.value;
		 if(fileEx == "")
		 {
	    	alert(SELECT_FIELD+"!");
	    	return ;
	     }
	     /**
	      var fso=new ActiveXObject("Scripting.FileSystemObject");   
  		  if(!fso.FileExists(fileEx))
		  {
		     alert("文件不存在");
		     return;
		  } 
		  */   
	    document.getElementById("filepath").value=fileEx;
		jingpinForm.action="/hire/jp_contest/apply/saveuploadfile.do?b_query=link&up=1";
		jingpinForm.submit();
		
	}
	
	
	<%
	if(request.getParameter("up")!=null&&request.getParameter("up").equals("1"))
	{
		out.println("window.returnValue = 'refresh';");
		out.println("window.close();");
	}
	%>
	
	function cleartext()
	{
		jingpinForm.filetitle.value='';
	}

	
</script>
<base target=_self>
<form name="jingpinForm" method="post" action="/hire/jp_contest/apply/saveuploadfile.do" enctype="multipart/form-data" >
<html:hidden name="jingpinForm" property="filepath"/>
<table width=360 border="0" cellpadding="0" cellspacing="0" align="center">
    <br>
    <br>
    <tr height="20">
       		<!--  <td width=10 valign="top" class="tableft"></td>
       		<td width=140 align=center class="tabcenter">&nbsp;<bean:message key="hire.jp.apply.datum"/>&nbsp;</td>
       		<td width=10 valign="top" class="tabright"></td>
       		<td valign="top" class="tabremain" width="300"></td> -->
       		<td  align=center class="TableRow">&nbsp;<bean:message key="hire.jp.apply.datum"/>&nbsp;</td>             	      
    </tr> 
    <tr>
    <td class="framestyle9">
      <table>       
          <tr>
             <td align="right"  nowrap ><bean:message key="conlumn.mediainfo.info_title"/>&nbsp;</td>
             <td align="left"  nowrap ><html:text name="jingpinForm" property="filetitle" styleClass="text6" /></td>
          </tr>
         <tr>
             <td align="right"  nowrap ><bean:message key="conlumn.mediainfo.filename"/>&nbsp;</td>
             <td align="left"  nowrap ><html:file name="jingpinForm" property="uploadfile" styleClass="text6"/></td>
         </tr>        
      </table>
     </td>  
    </tr>
    <tr>
       <td  nowrap align="center" sytle="height:35px;">
        <br>
           
               <html:button styleClass="mybutton" property="b_next" onclick="upload();">
		      		<bean:message key="button.save"/>
			   </html:button>	  
			   <html:button styleClass="mybutton" property="b_clear" onclick="cleartext();">
		      		<bean:message key="button.clear"/>
			   </html:button>	  
			   <html:button styleClass="mybutton" property="b_return" onclick="window.close();">
		      		<bean:message key="button.cancel"/>
			   </html:button>	        
        </td>
    </tr>    
  </table>
</form>
