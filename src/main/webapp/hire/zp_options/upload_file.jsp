<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>


<html:form action="/hire/zp_options/upload_file" enctype="multipart/form-data">
    <table align="center" width="500" border="0" cellpadding="0" cellspacing="0" align="center">
        <tr height="20">
       		<!--  <td width=10 valign="top" class="tableft"></td>
       		<td width=110 align=center class="tabcenter">&nbsp;<bean:message key="label.zp_options.test_question"/></td>
       		<td width=10 valign="top" class="tabright"></td>
       		<td valign="top" class="tabremain" width="680"></td>-->              	      
          </tr> 
 
          <tr>
            <td  class="framestyle9">
               <table border="0" cellpmoding="0" cellspacing="0"  class="DetailTable"  cellpadding="0">    
		<tr class="list3">
		   <td align="left" nowrap valign="middle" width="50"><bean:message key="column.name"/></td>
		   <td align="left" nowrap><html:text name="infoMlrframeForm" property="testQuestionvo.string(name)"/></td>
	       </tr>
 	     </table>     
           </td>
        </tr>
     
        <tr>
           <td  class="framestyle9">
               <table border="0" cellpmoding="0" cellspacing="0"  class="DetailTable"  cellpadding="0">         
     		  <tr class="list3">
			<td align="left" nowrap valign="top" width="50"><bean:message key="conlumn.resource_list.descrption"/></td><td align="left" nowrap><html:textarea name="infoMlrframeForm" property="testQuestionvo.string(description)" cols="50" rows="10"/>
			</td>
		   </tr>
	       </table>     
          </td>
     </tr>
     
      <tr>
          <td class="framestyle9">
               <table border="0" cellpmoding="0" cellspacing="0"  class="DetailTable"  cellpadding="0">        
		  <tr class="list3">
			<td align="left" nowrap valign="top" width="50"><bean:message key="label.zp_options.upload_file"/></td>
			<td align="left" nowrap> <html:file name="infoMlrframeForm" property="file"/></td>
		  </tr>
	       </table>     
            </td>
     </tr>

<tr>
       <td  class="framestyle9" align="center">
          <table border="0" cellpmoding="0" cellspacing="0"  class="DetailTable"  cellpadding="0">       
           <tr>
             <td colspan="2" align="center">
                <hrms:submit styleClass="mybutton" property="b_save" onclick="document.infoMlrframeForm.target='_self';validate('R','testQuestionvo.string(name)','名称','R','testQuestionvo.string(description)','描述','R','file','上传资料');return (document.returnValue && ifqrbc());">
            		<bean:message key="button.save"/>
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

