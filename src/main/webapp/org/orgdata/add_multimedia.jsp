<%@ page contentType="text/html; charset=UTF-8" language="java"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>

<script language="javascript">
	function upload()
	{
		var fileEx = mInformForm.picturefile.value;
		 if(fileEx == "")
		 {
	    	alert(SELECT_FIELD+"!");
	    	return ;
	     }
	     
	    document.getElementById("filepath").value=fileEx;
		document.getElementById("i9999").value="";
		var flag = document.getElementById("filetype").value;
		if(flag=="")
		{	
			alert(SELECT_TYPE+"!");
			return ;
		}
		document.mInformForm.action="/general/inform/emp/view/savemultimedia.do?b_query2=link";
		mInformForm.target="_self";
		document.mInformForm.submit();
		alert('多媒体上传成功!');
		var thevo=new Object();
		thevo.flag="true";
		window.returnValue=thevo;
		window.close();
	}
	
	function cleartext()
	{
		mInformForm.filetitle.value='';
	}

	function return_to()
	{
		document.mInformForm.action="/general/inform/emp/view/opermultimedia.do?b_query=link&a0100=${mInformForm.a0100}&multimediaflag=${mInformForm.multimediaflag}&isvisible=${mInformForm.isvisible}";
		document.mInformForm.submit(); 
	}
	
	function   NoExec()   
  	{   
          if(event.keyCode==13||event.keyCode==222)   event.returnValue=false; 
          document.onkeypress=NoExec;     
  	} 
  	function closeWin() {
  		window.opener = null;
  		window.close();
  	}  
	
</script>
<form name="mInformForm" method="post" action="/general/inform/emp/view/opermultimedia" enctype="multipart/form-data" >
<html:hidden name="mInformForm" property="filepath"/>
<html:hidden name="mInformForm" property="i9999"/>
<html:hidden name="mInformForm" property="isvisible"/>
<table width=360 border="0" cellpadding="0" cellspacing="0" align="center" class="ftable">
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
             <td align="left"  nowrap ><html:text name="mInformForm" property="filetitle" styleClass="textborder text4" onkeydown="NoExec()" /></td>
          </tr>
         <tr>
             <td align="right"  nowrap ><bean:message key="conlumn.mediainfo.filename"/>&nbsp;</td>
             <td align="left"  nowrap ><html:file name="mInformForm" property="picturefile" styleClass="textborder text4"  /></td>
        
    </tr>
    <tr>
       <td  nowrap colspan="2" align="center" style="height:35px;">         
               <html:button styleClass="mybutton" property="b_next" onclick="upload();">
		      		<bean:message key="button.save"/>
			   </html:button>	  
		       <input type='button' value='<bean:message key='button.close' />'	class="mybutton" onclick='closeWin();'>	
        </td>
    </tr>    
  </table>
</form>
