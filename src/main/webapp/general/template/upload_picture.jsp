<%@ page contentType="text/html; charset=UTF-8" language="java"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<script type="text/javascript">
<!--
	function getPohtoInfo()
	{
		var img=document.getElementById("photoid");
		if(img==null)
			return;
	    window.returnValue=img.src;
	    window.close();			
	}
	
	
    function save(obj)
    {
        
        if (obj.disbled==true ) {
          return ;
        }
        
        if (!validate('R','picturefile','上传照片')) {
         return ;
        }
        obj.disabled = true;
        templateForm.action="/general/template/upload_picture.do?b_save=link";
        templateForm.enctype="multipart/form-data";
        templateForm.submit();
    }
    
    	
	
	
	
	
	
	
	
	
</script>	
<script language="JavaScript" src="template.js"></script>
<base id="mybase" target="_self">
<html:form action="/general/template/upload_picture" enctype="multipart/form-data" onsubmit="return validateSize()"  >
<logic:equal name="templateForm" property="photofile" value="">
<br>
</logic:equal>     
<table><tr><td>&nbsp;</td><td width='100%'>

<table width="100%" border="0" cellspacing="0"  align="center" cellpadding="0" class="ListTable">
 <thead>
    <tr height="20">
       		<td class="TableRow" >&nbsp;<bean:message key="conlumn.info.phototitle"/></td>
    </tr> 
 </thead>
 <tr>
  <td class="framestyle" height="40">
      <table>
      	<logic:notEqual name="templateForm" property="photofile" value="">
         <tr>
            <td align="right"  nowrap>&nbsp;上传照片</td>
            <td align="left"  nowrap >
           		<img id="photoid" src="/servlet/DisplayOleContent?filename=<bean:write name="templateForm" property="photofile" filter="true"/>" width="80" border="0" height="120">  
            </td>
       </tr>  
       </logic:notEqual>     
         <tr>
            <td align="right"  nowrap>&nbsp;<bean:message key="conlumn.info.photolabel"/>&nbsp;</td>
            <td align="left"  nowrap >
             <html:file name="templateForm" property="picturefile" styleClass="text6"/>  </td>
       </tr>        
      </table>
   </td>  
  </tr>
</table> 
 <table align="center"> 
 <tr> 
<td align="center"  nowrap>  
      &nbsp;&nbsp;

	            <button class="mybutton" onclick="save(this);" style="margin-top:5px;" >
                    <bean:message key="lable.fileup"/>
                </button> 
      &nbsp;            
	 	        <button class="mybutton" onclick="getPohtoInfo();" style="margin-top:5px;" >
            		<bean:message key="button.ok"/>
	 	        </button>		            
      &nbsp;            
	 	        <button class="mybutton" onclick="window.close();" style="margin-top:5px;" >
            		<bean:message key="button.close"/>
	 	        </button>		       
  </td>
 </tr>     
 </table>
</td></tr></table>
 
</html:form>

