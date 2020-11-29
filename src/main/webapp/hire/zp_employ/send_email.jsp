<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>

<script language="JavaScript">
function getFileValue(xname){
	var InString=xname.value;
	zpEmployForm.filename.value = InString;
}
</script>

<html:form action="/hire/zp_employ/send_email" enctype="multipart/form-data">
    <table align="center" width="600" border="0" cellpadding="0" cellspacing="0" align="center">
        <tr height="20">
       		<!-- <td width=10 valign="top" class="tableft"></td>
       		<td width=110 align=center class="tabcenter">&nbsp;<bean:message key="label.zp_employ.sendmail"/></td>
       		<td width=10 valign="top" class="tabright"></td>
       		<td valign="top" class="tabremain" width="680"></td>  --> 
       		<td colspan="4" align=center class="TableRow">&nbsp;<bean:message key="label.zp_employ.sendmail"/></td>            	      
          </tr> 
           <tr>
            <td colspan="4" class="framestyle9">
               <table border="0" cellpmoding="0" cellspacing="0"  class="DetailTable"  cellpadding="0">    
		<tr class="list3">
		   <td align="left" nowrap valign="middle" width="50"><bean:message key="label.zp_employ.to"/></td>
		   <td align="left" nowrap><html:text name="zpEmployForm" property="toName" size="71"/></td>
	       </tr>
 	     </table>     
           </td>
        </tr>
        
        <tr>
            <td colspan="4" class="framestyle9">
               <table border="0" cellpmoding="0" cellspacing="0"  class="DetailTable"  cellpadding="0">    
		<tr class="list3">
		   <td align="left" nowrap valign="middle" width="50"><bean:message key="label.zp_employ.from"/></td>
		   <td align="left" nowrap><html:text name="zpEmployForm" property="fromName" size="71"/></td>
	       </tr>
 	     </table>     
           </td>
        </tr>
        
        <tr>
            <td colspan="4" class="framestyle9">
               <table border="0" cellpmoding="0" cellspacing="0"  class="DetailTable"  cellpadding="0">    
		<tr class="list3">
		   <td align="left" nowrap valign="middle" width="50"><bean:message key="conlumn.board.topic"/></td>
		   <td align="left" nowrap><html:text name="zpEmployForm" property="topic" size="71"/></td>
	       </tr>
 	     </table>     
           </td>
        </tr>
     
        <tr>
           <td colspan="4" class="framestyle9">
               <table border="0" cellpmoding="0" cellspacing="0"  class="DetailTable"  cellpadding="0">         
     		  <tr class="list3">
			<td align="left" nowrap valign="top" width="50"><bean:message key="conlumn.board.content"/></td><td align="left" nowrap><html:textarea name="zpEmployForm" property="content" cols="70" rows="15"/>
			</td>
		   </tr>
	       </table>     
          </td>
     </tr>
     
      <tr>
          <td colspan="4" class="framestyle9">
               <table border="0" cellpmoding="0" cellspacing="0"  class="DetailTable"  cellpadding="0">        
		  <tr class="list3">
			<td align="left" nowrap valign="top" width="50"><bean:message key="label.zp_employ.uploadfile"/></td>
			<td align="left" nowrap>
			   <html:hidden name="zpEmployForm" property="filename" styleClass="text6"/>
			   <html:file name="zpEmployForm" property="file"/></td>
		  </tr>
	       </table>     
            </td>
     </tr>

<tr>
       <td colspan="4" class="framestyle9" align="center">
          <table border="0" cellpmoding="0" cellspacing="0"  class="DetailTable"  cellpadding="0">       
           <tr>
             <td colspan="2" align="center">
                <hrms:submit styleClass="mybutton" property="b_send" onclick="getFileValue(zpEmployForm.file);">
            		<bean:message key="label.zp_employ.send"/>
	 	</hrms:submit>
	 	<html:reset styleClass="mybutton" property="reset">
	 	       <bean:message key="button.clear"/>
	 	</html:reset>
	 	<hrms:submit styleClass="mybutton" property="br_return">
            		<bean:message key="button.return"/>
	 	   </hrms:submit>	 	
            </td></tr>
        </table>     
     </td>
   </tr>
<table>
</td></tr></table>


</html:form>

