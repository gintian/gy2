<%@ page contentType="text/html; charset=UTF-8" language="java"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
  <script language="javascript">
       parent.mil_menu.location.reload();
   </script>
<html:form action="/hire/zp_persondb/upenrollinfophoto" enctype="multipart/form-data" onsubmit="return validate()">
<table width="500" border="0" cellpadding="0" cellspacing="0" align="center">
 <html:hidden name="zppersondbForm" property="userbase"/> 
 <html:hidden name="zppersondbForm" property="actiontype" /> 
 <html:hidden name="zppersondbForm" property="a0100"/> 
 <html:hidden name="zppersondbForm" property="i9999"/> 
 <html:hidden name="zppersondbForm" property="filesort" value="p"/> 
    <br>
    <tr height="20">
       		<!--  <td width=10 valign="top" class="tableft"></td>
       		<td width=140 align=center class="tabcenter">&nbsp;<bean:message key="conlumn.info.phototitle"/>&nbsp;</td>
       		<td width=10 valign="top" class="tabright"></td>
       		<td valign="top" class="tabremain" width="500"></td> --> 
       		<td  align=center class="TableRow">&nbsp;<bean:message key="conlumn.info.phototitle"/>&nbsp;</td>            	      
    </tr> 
 <tr> 
  <td class="framestyle9" height="40">
      <table>
         <tr>
            <td align="right"  nowrap><bean:message key="conlumn.info.photolabel"/>&nbsp;</td>
            <td align="left"  nowrap ><html:file name="zppersondbForm" property="picturefile" styleClass="text6"/>
            </td>
         </tr>        
      </table>
     </td>  
    </tr>
 <tr>
 <td align="center"  nowrap style="height:35px;">  
               <hrms:submit styleClass="mybutton"  property="b_up">
                    <bean:message key="button.ok"/>
	       </hrms:submit>  
  </td>
 </tr>    
 </table>
</html:form>
