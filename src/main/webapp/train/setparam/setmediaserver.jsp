<%@ page contentType="text/html; charset=UTF-8"%>
<%@ page import="com.hjsj.hrms.utils.Office2Swf" %>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
 

<body style="margin: 0px;" >
<html:form action="/train/setparam/setmediaserver" styleId="form1">&nbsp; <br>
<fieldset align="center" style="width:90%;">
 <legend ><bean:message key="train.setparam.mediaserver.serverparams"/></legend>	
 <table width="100%" align="center"> 
   <tr> 
   <td  valign="top">
     <table  width="100%"  border="0" cellpmoding="0" cellspacing="0"  class="DetailTable"  cellpadding="0" valign="top">   
      
	 <tr>
	  <td style="padding-top:10px;">
	    <fieldset align="center" style="width:95%;">
    	      <legend><bean:message key="train.setparam.mediaserver.mediaserver"/></legend>
		<table width="100%"  border="0" cellpmoding="0" cellspacing="0"  class="DetailTable"  cellpadding="0">    
		      <tr>
				<td width="50" align="right" nowrap valign="middle" height="30" ><bean:message key="train.setparam.mediaserver.mediaserver.type"/>&nbsp;</td>
               	<td align="left"  nowrap valign="middle">
                         	&nbsp;<html:select name="setParamForm" property="mediaServerType" onchange="serverTypeChange();" styleId="mediaServerType">
                         		<html:option value=""></html:option>
                         		<html:option value="red5">red5</html:option>
                         		<html:option value="microsoft">microsoft</html:option>
                         		<html:option value="HTTP">HTTP</html:option>
                         	</html:select>   
                         <br></td>
                          <td align="right"  nowrap>
								&nbsp;
							</td>
							<td align="left" nowrap height="30" valign="middle">
							&nbsp;
							</td>
                </tr> 
                        
                        <tr>
                           <td width="50" align="right" nowrap valign="middle" height="30" ><bean:message key="train.setparam.mediaserver.mediaserver.address"/>&nbsp;</td>
                           <td align="left"  nowrap valign="middle">
	                        	&nbsp;<html:text name="setParamForm" styleClass="TEXT4" property="mediaServerAddress" styleId="ipAddress"></html:text>  
                           </td>
                          <td align="right"  nowrap>
								<bean:message key="train.setparam.mediaserver.mediaserver.port"/>&nbsp;
							</td>
							<td align="left" nowrap valign="middle">
							&nbsp;<html:text name="setParamForm" styleClass="TEXT4"  property="mediaServerPort"></html:text>&nbsp;&nbsp;
							</td>
                        </tr>
                        
                        <tr style="display: none;" id="pubRootTr">
                           <td width="50" align="right" nowrap valign="middle" height="30" >发布点&nbsp;</td>
                           <td align="left"  nowrap valign="middle" colspan="3">
	                        	&nbsp;<html:text name="setParamForm" styleClass="TEXT4"  property="mediaServerPubRoot"></html:text>  
                           </td>
                          
                        </tr>
                        
                     
                                      	
		 </table>
	      </fieldset>
	     </td>
        </tr>
        
          <tr>
         <td style="padding-top:10px;">
                  
         <fieldset align="center" style="width:95%;">
             <legend><bean:message key="train.setparam.mediaserver.ftpserver"/></legend>
               <table width="100%"  border="0" cellpmoding="0" cellspacing="0"  class="DetailTable"  cellpadding="0">                   	

		      <tr>
				<td align="right" width="50" nowrap><bean:message key="train.setparam.mediaserver.ftpserver.address"/>&nbsp; </td>
               	<td align="left"  nowrap valign="middle" height="30" >
                            	&nbsp;<html:text name="setParamForm" styleClass="TEXT4"  property="ftpServerAddress"></html:text>    
                            </td>
                            <td align="right"  nowrap>
								<bean:message key="train.setparam.mediaserver.ftpserver.port"/>&nbsp;
							</td>
							<td align="left" nowrap valign="middle">
							&nbsp;<html:text name="setParamForm" styleClass="TEXT4"  property="ftpServerPort"></html:text>  
							</td>
                        </tr> 
                        
                        <tr>
                           <td align="right" nowrap><bean:message key="train.setparam.mediaserver.ftpserver.username"/>&nbsp;</td>
                           <td align="left"  nowrap height="30" valign="middle">
	                        	&nbsp;<html:text name="setParamForm"  styleClass="TEXT4" property="ftpServerUserName"></html:text>  
                           </td>
                          <td align="right"  nowrap>
								<bean:message key="train.setparam.mediaserver.ftpserver.pwd"/>&nbsp;
							</td>
							<td align="left" nowrap height="30" valign="middle">
							&nbsp;<html:password name="setParamForm" styleClass="TEXT4"  property="ftpServerPwd" size="21"></html:password>  
							</td>
                        </tr>
                        
                                      	
		 </table>
            </fieldset> 
           </td>
          </tr>
        <tr>
         <td style="padding-top:10px;">
                   
         <fieldset align="center" style="width:95%;">
             <legend ><bean:message key="train.setparam.mediaserver.filepath"/></legend>
               <table width="100%"  border="0" cellpmoding="0" cellspacing="0"  class="DetailTable"  cellpadding="0">                   	
		 <tr>
		  <td width="100%" height="30" align="left">
		 		&nbsp;&nbsp;<bean:message key="train.setparam.mediaserver.filepath.desc"/>
		  </td>
		 </tr>
		 
		 <tr>
		  <td width="100%" height="30" align="left" nowrap>
		 		&nbsp;&nbsp; <html:text name="setParamForm" styleClass="TEXT4"  property="filePath" size="83"></html:text>  
		  </td>
		 </tr>
		 
		 <tr>
		  <td width="100%" height="30" align="left" nowrap>
		  		&nbsp;&nbsp;&nbsp;<bean:message key="train.setparam.mediaserver.filepath.filesize"/>
		 		<html:text name="setParamForm" styleClass="TEXT4" style="width:50px;"  property="fileSize"></html:text> 
		 		<bean:message key="train.setparam.mediaserver.filepath.limit"/> 
		  </td>
		 </tr>
		  
	        </table>
            </fieldset> 
           </td>
          </tr>
    
         <!--  <tr>
         <td>
         <br>           
         <fieldset align="center" style="width:95%;">
             <legend ><bean:message key="train.setparam.mediaserver.openofficeserverset"/></legend>
	        
	        
	        <table width="100%"  border="0" cellpmoding="0" cellspacing="0"  class="DetailTable"  cellpadding="0">                   	

		      <tr>
				<td align="right" width="50" nowrap><bean:message key="train.setparam.mediaserver.openofficeserveradd"/>&nbsp; </td>
               	<td align="left"  nowrap valign="middle" height="30" >
                            	&nbsp;<html:text name="setParamForm" property="openOfficeAdd"></html:text>    
                            </td>
                            <td align="right"  nowrap>
								<bean:message key="train.setparam.mediaserver.openofficeserverport"/>&nbsp;
							</td>
							<td align="left" nowrap valign="middle">
							&nbsp;<html:text name="setParamForm" property="openOfficePort"></html:text>  
							</td>
                        </tr> 
                        
                                      	
		 </table>
	        
	        
            </fieldset> 
           </td>
          </tr>--> 
          
       <tr>
	     <td >
	     	
	      	&nbsp;<!-- &nbsp;
	      	<logic:equal name="setParamForm" property="isDownload" value="1">
	      	<input type="checkbox" name="isDownload_check" id="isDownload_check" value="1" checked="checked" onclick="checkClick()" > <bean:message key="train.setparam.mediaserver.download"/>
	      	
	      	</logic:equal>
	      	<logic:notEqual name="setParamForm" property="isDownload" value="1">
	      	<input type="checkbox" name="isDownload_check" id="isDownload_check" value="1" onclick="checkClick()"> <bean:message key="train.setparam.mediaserver.download"/> 
	      	</logic:notEqual>
	      	<html:hidden name="setParamForm" property="isDownload" styleId="isDownload" value=""/> -->
	     </td>
	   </tr>
      
	
      </table>
    </td>
    </tr>
    
  </table>
  
   
    </fieldset> 
    
     <table width="50%" align="center" valign="top" style="margin-top:10px;">
      <tr>
        <td align="center">        
            
             <input type="button" name="btnreturn" value='<bean:message key="button.ok"/>' class="mybutton" onclick="saveParam();">
  	         
        </td>
      </tr>
    </table>     	
</html:form>
<script type="text/javascript">
	function saveParam(){
		var mediaServerType = document.getElementById("mediaServerType");
		if (mediaServerType) {	
			if (mediaServerType.value == "microsoft" || mediaServerType.value== "red5") {
				var ipAddress = document.getElementById("ipAddress");
				if (ipAddress) {
					if (ipAddress.value == "") {
						alert("设置失败！流媒体服务器地址不能为空！");
						return;
					}
				}
			}
		}
		var form = document.getElementById("form1");
		//checkClick();
		form.action = "/train/setparam/setmediaserver.do?b_query=link&opt=save";
		form.submit();
	}
	
	function checkClick() {
		var chek = document.getElementById("isDownload_check");
		if (chek.checked) {
			document.getElementById("isDownload").value = "1";
		} else {
			document.getElementById("isDownload").value = "0";
		}
	}
	document.body.onload=function a(){
		if("save"=="<%=request.getParameter("opt") %>"){
			alert("流媒体服务器设置成功！");
		
		}
	}
	
	function serverTypeChange(){
		var mediaServerType = document.getElementById("mediaServerType");
		if ("microsoft" == mediaServerType.value.toLowerCase()) {
			document.getElementById("pubRootTr").style.display = "";
		} else {
			document.getElementById("pubRootTr").style.display = "none";
		}
	}
	serverTypeChange();
</script>
