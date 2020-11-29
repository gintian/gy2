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

<html:form action="/hire/zp_interview/record_result">
      <br>
      <br>
      <table width="700" border="0" cellpadding="0" cellspacing="0" align="center">
          <tr>
            <td colspan="4">
               <table width="70%" border="0" cellspacing="1"  align="center" cellpadding="1" class="ListTable"> 
                <tr>
		 	<td align="center" class="TableRow" nowrap><bean:message key="hire.interviewing.person"/></td>
		 	<td align="center" class="TableRow" nowrap><bean:message key="hire.interviewing.record"/></td>
		 </tr> 
		  <hrms:extenditerate id="element" name="zpInterviewForm" property="zpProcessLogForm.list" indexes="indexes"  pagination="zpProcessLogForm.pagination" pageCount="10" scope="session">
                    <tr class="trShallow">
                       <td align="left" class="RecordRow" nowrap >
                          <bean:write  name="element" property="string(staff_name)" filter="true"/>
                       </td>
                       <td align="left" class="RecordRow" nowrap >
                          <bean:write  name="element" property="string(description)" filter="false"/>
                       </td>
                     </tr>   
                </hrms:extenditerate>
                   <tr class="trDeep">
                        <td align="left" valign="top" class="RecordRow" nowrap><bean:message key="label.zp_interview.description"/></td>
                     <td align="left" class="RecordRow" nowrap >
                          <html:textarea name="zpInterviewForm" property="description" cols="61" rows="7" styleClass="text6"/>
                      </td>
                   </tr> 
                    </table>     
        </td>
      </tr>               
        
 </table>
 <table  width="70%" align="center">
                   
           <tr>
              <td align="center"  nowrap colspan="4">
	 	     <hrms:submit styleClass="mybutton" property="b_ok" onclick="document.zpInterviewForm.target='_self';validate('R','zpProcessLogvo.string(description)','您的评语');return (document.returnValue && ifqrbc());"><bean:message key="button.ok"/></hrms:submit>
	 	      <hrms:submit styleClass="mybutton" property="br_return"><bean:message key="button.return"/></hrms:submit>    
             </td>
         </tr>                                                        
          
  </table>       
</html:form>
