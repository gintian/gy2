<%@ page contentType="text/html; charset=UTF-8" language="java"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
  <script language="javascript">
       parent.mil_menu.location.reload();
   </script>
<html:form action="/hire/zp_persondb/upenrollinfophoto" enctype="multipart/form-data" onsubmit="return validate()">
<table width="500" border="0" cellpadding="0" cellspacing="0" align="center" class="ftable">
 <html:hidden name="zppersondbForm" property="userbase"/> 
 <html:hidden name="zppersondbForm" property="actiontype" /> 
 <html:hidden name="zppersondbForm" property="a0100"/> 
 <html:hidden name="zppersondbForm" property="i9999"/> 
 <html:hidden name="zppersondbForm" property="filesort" value="F"/> 
    <br>
    <tr height="20">
       		<!--  <td width=10 valign="top" class="tableft"></td>
       		<td width=140 align=center class="tabcenter">&nbsp;<bean:message key="hire.zp_persondb.certificate"/>&nbsp;</td>
       		<td width=10 valign="top" class="tabright"></td>
       		<td valign="top" class="tabremain" width="500"></td>-->
       		<td width=140 align="left" colspan="2" class="TableRow">&nbsp;<bean:message key="hire.zp_persondb.certificate"/>&nbsp;</td>
       		             	      
    </tr> 
 <tr> 
            <td align="right"  nowrap><bean:message key="hire.zp_persondb.filetitle"/>&nbsp;</td>
            <td align="left"  nowrap ><html:text name="zppersondbForm" property="filetitle" styleClass="textborder" value=""/>
            </td>
         </tr>   
         <tr>
            <td align="right"  nowrap><bean:message key="hire.zp_persondb.certificatetitle"/>&nbsp;</td>
            <td align="left"  nowrap ><html:file name="zppersondbForm" property="picturefile" styleClass="textborder"/>
            </td>
         </tr>        
 <tr>
 <td align="center"  nowrap colspan="2" style="height:35px;">  
               <hrms:submit styleClass="mybutton"  property="b_upcertificate">
                    <bean:message key="button.ok"/>
	       </hrms:submit>  
  </td>
 </tr>    
 </table>
</html:form>
