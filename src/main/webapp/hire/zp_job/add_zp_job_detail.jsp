<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>

<script language="JavaScript" src="/js/validate.js"></script>
<script language="JavaScript" src="/js/function.js"></script>

<%
   int i = 0;
%>

<html:form action="/hire/zp_job/add_zp_job_detail">
      <br>
      <br>
      <table width="700" border="0" cellpadding="0" cellspacing="0" align="center">
          <tr>
            <td colspan="4">
               <table width="70%" border="0" cellspacing="1"  align="center" cellpadding="1" bordercolor = "#223377"> 
                <tr>
		 	<td height="20" align="center" class="TableRow" nowrap colspan="4"><bean:message key="hire.activity.cost"/></td>
		 </tr> 
		 <tr>
		    <td><html:hidden name="zpjobForm" property="zp_job_id_value" styleClass="text6"/></td>
		 </tr>
                   <tr class="trDeep1">
                     <td align="right" nowrap valign="center"><bean:message key="label.zp_job.detailname"/></td>
                     <td align="left"  nowrap valign="center">
                          <html:text name="zpjobForm" property="zpjobDetailsvo.string(detailname)" styleClass="text6"/>
                     </td>  
                     <td align="right" nowrap valign="center"><bean:message key="label.zp_job.charge"/></td>
                     <td align="left"  nowrap valign="center">
                          <html:text name="zpjobForm" property="zpjobDetailsvo.string(charge)" styleClass="text6"/>
                     </td>      
                   </tr> 
                   
           <tr>
              <td align="center"  nowrap colspan="4">
	 	     <hrms:submit styleClass="mybutton" property="b_save" onclick="document.zpjobForm.target='_self';validate('R','zpjobDetailsvo.string(detailname)','费用项目','RF','zpjobDetailsvo.string(charge)','项目预算');return (document.returnValue && ifqrbc());"><bean:message key="button.save"/></hrms:submit>
	 	      <hrms:submit styleClass="mybutton" property="br_return"><bean:message key="button.return"/></hrms:submit>    
             </td>
         </tr>                                                        
          
          </table>     
        </td>
      </tr>               
        
 </table>
</html:form>
