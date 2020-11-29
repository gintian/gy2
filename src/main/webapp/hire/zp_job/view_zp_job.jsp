<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%
   int i = 0;
%>

<html:form action="/hire/zp_job/view_zp_job">
      <br>
      <br>
      <table width="700" border="0" cellpadding="0" cellspacing="0" align="center">
          <tr>
            <td colspan="4">
               <table width="80%" border="0" cellspacing="1"  align="center" cellpadding="1" bordercolor = "#223377"> 
                <tr>
		 	<td height="20" align="center" class="TableRow" nowrap colspan="4"><bean:message key="hire.activity.activity"/></td>
		 </tr> 
		  <tr>
		    <td><html:hidden name="zpjobForm" property="zp_job_id_value" styleClass="text6"/></td>
		 </tr>
                  <tr class="trDeep1">
                     <td align="right" nowrap valign="center"><bean:message key="label.zp_job.zp_job_id"/></td>
                     <td align="left"  nowrap valign="center">
                          <bean:write  name="zpjobForm" property="zpjobvo.string(zp_job_id)" filter="true"/>
                     </td> 
                     <td align="right" nowrap valign="center"><bean:message key="label.zp_job.name"/></td>
                     <td align="left"  nowrap valign="center">
                         <bean:write  name="zpjobForm" property="zpjobvo.string(name)" filter="true"/>
                     </td> 
                 </tr>
               <tr class="trDeep1">
                     <td align="right" nowrap valign="center"><bean:message key="lable.zp_plan.start_date"/></td>
                     <td align="left"  nowrap valign="center">
                         <bean:write  name="zpjobForm" property="zpjobvo.string(start_date)" filter="true"/>
                     </td>
                     <td align="right" nowrap valign="center"><bean:message key="lable.zp_plan.end_date"/></td>
                     <td align="left"  nowrap valign="center">
                         <bean:write  name="zpjobForm" property="zpjobvo.string(end_date)" filter="true"/>
                     </td>
                          
                 </tr> 
                 <tr class="trShallow1">
                     <td align="right" nowrap valign="center"><bean:message key="lable.zp_plan.staff_id"/></td>
                     <td align="left"  nowrap valign="center">
                         <bean:write  name="zpjobForm" property="zpjobvo.string(principal)" filter="true"/>
                     </td>
                     <td align="right" nowrap valign="center"><bean:message key="lable.zp_plan.status"/></td>
                     <td align="left"  nowrap valign="center">
                           <logic:equal name="zpjobForm" property="zpjobvo.string(status)" value="01">
                              <bean:message key="label.hiremanage.status1"/>&nbsp;
                          </logic:equal>
                          <logic:equal name="zpjobForm" property="zpjobvo.string(status)" value="02">
                              <bean:message key="label.hiremanage.status2"/>&nbsp;
                          </logic:equal>
                          <logic:equal name="zpjobForm" property="zpjobvo.string(status)" value="03">
                              <bean:message key="label.hiremanage.status3"/>&nbsp;
                          </logic:equal>
                          <logic:equal name="zpjobForm" property="zpjobvo.string(status)" value="04">
                              <bean:message key="label.hiremanage.status4"/>&nbsp;
                          </logic:equal>
                          <logic:equal name="zpjobForm" property="zpjobvo.string(status)" value="05">
                              <bean:message key="label.hiremanage.status5"/>&nbsp;
                          </logic:equal>
                          <logic:equal name="zpjobForm" property="zpjobvo.string(status)" value="06">
                              <bean:message key="label.hiremanage.status6"/>&nbsp;
                          </logic:equal>
                      </td>
                          
                 </tr>
                 <tr class="trShallow1">
                        <td align="right" nowrap valign="top"><bean:message key="label.zp_job.attendee"/></td>
                       <td align="left"  nowrap valign="center" colspan="4">
                           <bean:write  name="zpjobForm" property="zpjobvo.string(attendee)" filter="true"/>
                       </td>
                 </tr> 
              <tr>
              <td align="center"  nowrap colspan="4">
	 	     <hrms:submit styleClass="mybutton" property="br_return"><bean:message key="button.return"/></hrms:submit>    
             </td>
         </tr>                                                        
          
          </table>     
        </td>
      </tr>         
       <tr class="list3">
            <td align="center" colspan="2">
		&nbsp           
            </td>
          </tr>   

          <tr>
          <table width="70%" border="0" cellspacing="1"  align="center" cellpadding="1" class="ListTable">
   	  <thead>
           <tr>        
            <td align="center" class="TableRow" nowrap>
		<bean:message key="label.zp_job.detailname"/>&nbsp;
	    </td>
            <td align="center" class="TableRow" nowrap>
		<bean:message key="label.zp_job.charge"/>&nbsp;
	    </td>	    		        	        	        
           </tr>
   	  </thead>
   	  <hrms:extenditerate id="element" name="zpjobForm" property="zpjobDetailsForm.list" indexes="indexes"  pagination="zpjobDetailsForm.pagination" pageCount="10" scope="session">
              <%
          if(i%2==0)
          {
          %>
          <tr class="trShallow">
          <%}
          else
          {%>
          <tr class="trDeep">
          <%
          }
          i++;          
          %>            
         
            <td align="left" class="RecordRow" nowrap>
                    <bean:write  name="element" property="string(detailname)" filter="true"/>&nbsp;
	    </td>
            <td align="right" class="RecordRow" nowrap>
                    <bean:write  name="element" property="string(charge)" filter="true"/>&nbsp;
	    </td>	   	    
               		        	        	        
          </tr>
        </hrms:extenditerate>
   	    
     </table>
    <table  width="70%" align="center">
		<tr>
		    <td valign="bottom" class="tdFontcolor"><bean:message key="label.page.serial"/>
					<bean:write name="zpjobForm" property="zpjobDetailsForm.pagination.current" filter="true" />
					<bean:message key="label.page.sum"/>
					<bean:write name="zpjobForm" property="zpjobDetailsForm.pagination.count" filter="true" />
				<bean:message key="label.page.row"/>
					<bean:write name="zpjobForm" property="zpjobDetailsForm.pagination.pages" filter="true" />
					<bean:message key="label.page.page"/>
			</td>
	               <td  align="right" nowrap class="tdFontcolor">
		          <p align="right"><hrms:paginationlink name="zpjobForm" property="zpjobDetailsForm.pagination"
				nameId="zpjobDetailsForm" propertyId="zpjobDetailsProperty">
				</hrms:paginationlink>
			</td>
		</tr>
    </table>
       
    </tr>             
 </table>
</html:form>
