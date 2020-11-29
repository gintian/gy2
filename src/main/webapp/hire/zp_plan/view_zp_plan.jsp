<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%
   int i = 0;
%>

<html:form action="/hire/zp_plan/view_zp_plan">
      <br>
      <br>
      <table width="700" border="0" cellpadding="0" cellspacing="0" align="center">
          <tr>
            <td colspan="4">
               <table width="80%" border="0" cellspacing="1"  align="center" cellpadding="1" bordercolor = "#223377"> 
                <tr>
		 	<td height="20" align="center" class="TableRow" nowrap colspan="4"><bean:message key="label.zp_job.plan"/></td>
		 </tr> 
		  <tr>
		    <td><html:hidden name="zpplanForm" property="plan_id_value" styleClass="text6"/></td>
		 </tr>
                  <tr class="trDeep1">
                     <td align="right" nowrap valign="center"><bean:message key="lable.zp_plan.plan_id"/></td>
                     <td align="left"  nowrap valign="center">
                          <bean:write  name="zpplanForm" property="zpplanvo.string(plan_id)" filter="true"/>
                     </td> 
                     <td align="right" nowrap valign="center"><bean:message key="lable.zp_plan.name"/></td>
                     <td align="left"  nowrap valign="center">
                         <bean:write  name="zpplanForm" property="zpplanvo.string(name)" filter="true"/>
                     </td> 
                 </tr>
                 <tr class="trShallow1">
                     <td align="right" nowrap valign="center"><bean:message key="lable.resource_plan.org_id"/></td>
                     <td align="left"  nowrap valign="center">
                          <bean:write  name="zpplanForm" property="org_id_value" filter="true"/>
                     </td>
                     <td align="right" nowrap valign="center"><bean:message key="lable.zp_plan.dept_id"/></td>
                     <td align="left"  nowrap valign="center">
                         <bean:write  name="zpplanForm" property="dept_id_value" filter="true"/>
                     </td>
               </tr>
               <tr class="trDeep1">
                     <td align="right" nowrap valign="center"><bean:message key="lable.zp_plan.staff_id"/></td>
                     <td align="left"  nowrap valign="center">
                          <bean:write  name="zpplanForm" property="zpplanvo.string(staff_id)" filter="true"/>
                     </td>
                     <td align="right" nowrap valign="center"><bean:message key="lable.zp_plan.budget_fee"/></td>
                     <td align="left"  nowrap valign="center">
                         <bean:write  name="zpplanForm" property="zpplanvo.string(budget_fee)" filter="true"/>
                     </td>
                          
                 </tr> 
                 <tr class="trShallow1">
                     <td align="right" nowrap valign="center"><bean:message key="lable.zp_plan.start_date"/></td>
                     <td align="left"  nowrap valign="center">
                        <bean:write  name="zpplanForm" property="zpplanvo.string(start_date)" filter="true"/>
                     </td>
                     <td align="right" nowrap valign="center"><bean:message key="lable.zp_plan.end_date"/></td>
                     <td align="left"  nowrap valign="center">
                         <bean:write  name="zpplanForm" property="zpplanvo.string(end_date)" filter="true"/>
                     </td>
                          
                 </tr>
                 <tr class="trDeep1">
                     <td align="right" nowrap valign="center"><bean:message key="lable.zp_plan.plan_invite_amount"/></td>
                     <td align="left"  nowrap valign="center">
                         <bean:write  name="zpplanForm" property="zpplanvo.string(plan_invite_amount)" filter="true"/>
                     </td>
                     <td align="right" nowrap valign="center"><bean:message key="lable.zp_plan.domain"/></td>
                     <td align="left"  nowrap valign="center">
                         <bean:write  name="zpplanForm" property="zpplanvo.string(domain)" filter="true"/>
                     </td>
                          
                 </tr>    
                   <tr class="trShallow1">
                     <td align="right" nowrap valign="center"><bean:message key="lable.zp_plan.zp_object"/></td>
                     <td align="left"  nowrap valign="center">
                           <logic:equal name="zpplanForm" property="zpplanvo.string(zp_object)" value="01">
                              <bean:message key="lable.zp_plan.zp_object0"/>&nbsp;
                           </logic:equal> 
                           <logic:equal name="zpplanForm" property="zpplanvo.string(zp_object)" value="02">
                              <bean:message key="lable.zp_plan.zp_object1"/>&nbsp;
                           </logic:equal>   
                           <logic:equal name="zpplanForm" property="zpplanvo.string(zp_object)" value="03">
                              <bean:message key="lable.zp_plan.zp_object2"/>&nbsp;
                           </logic:equal>                       
                      </td>
                     <td align="right" nowrap valign="center"><bean:message key="lable.zp_plan.status"/></td>
                     <td align="left"  nowrap valign="center">
                           <logic:equal name="zpplanForm" property="zpplanvo.string(status)" value="01">
                              <bean:message key="label.hiremanage.status1"/>&nbsp;
                          </logic:equal>
                          <logic:equal name="zpplanForm" property="zpplanvo.string(status)" value="02">
                              <bean:message key="label.hiremanage.status2"/>&nbsp;
                          </logic:equal>
                          <logic:equal name="zpplanForm" property="zpplanvo.string(status)" value="03">
                              <bean:message key="label.hiremanage.status3"/>&nbsp;
                          </logic:equal>
                          <logic:equal name="zpplanForm" property="zpplanvo.string(status)" value="04">
                              <bean:message key="label.hiremanage.status4"/>&nbsp;
                          </logic:equal>
                          <logic:equal name="zpplanForm" property="zpplanvo.string(status)" value="05">
                              <bean:message key="label.hiremanage.status5"/>&nbsp;
                          </logic:equal>
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
		<bean:message key="lable.zp_plan_detail.dept_id"/>&nbsp;
	    </td>
            <td align="center" class="TableRow" nowrap>
		<bean:message key="lable.zp_plan_detail.pos_id"/>&nbsp;
	    </td>
	    <td align="center" class="TableRow" nowrap>
		<bean:message key="lable.zp_plan_detail.amount"/>&nbsp;
	    </td>
            <td align="center" class="TableRow" nowrap>
		<bean:message key="lable.zp_plan_detail.status"/>&nbsp;
	    </td>
	     <td align="center" class="TableRow" nowrap>
		<bean:message key="lable.hiremanage.gather_id"/>&nbsp;
	    </td>
	     <td align="center" class="TableRow" nowrap>
		<bean:message key="lable.zp_plan_detail.domain"/>&nbsp;
	    </td>	    		        	        	        
           </tr>
   	  </thead>
   	  <hrms:extenditerate id="element" name="zpplanForm" property="zpplanDetailsForm.list" indexes="indexes"  pagination="zpplanDetailsForm.pagination" pageCount="10" scope="session">
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
                  <hrms:codetoname codeid="UM" name="element" codevalue="string(dept_id)" codeitem="codeitem" scope="page"/>  	      
                  <bean:write name="codeitem" property="codename" />&nbsp;
	    </td>
            <td align="left" class="RecordRow" nowrap>
                   <hrms:codetoname codeid="@K" name="element" codevalue="string(pos_id)" codeitem="codeitem" scope="page"/>  	      
                   <bean:write name="codeitem" property="codename" />&nbsp;
	    </td>
            <td align="right" class="RecordRow" nowrap>
                    <bean:write  name="element" property="string(amount)" filter="true"/>&nbsp;
	    </td> 	                
            <td align="left" class="RecordRow" nowrap>
                    <logic:equal name="element" property="string(status)" value="0">
                    <bean:message key="lable.zp_plan_detail.status0"/>&nbsp;
              </logic:equal>
               <logic:notEqual name="element" property="string(status)" value="0">
                      <bean:message key="lable.zp_plan_detail.status1"/>&nbsp;
                 </logic:notEqual>  
	    </td>
	     <td align="right" class="RecordRow" nowrap>
                    <bean:write  name="element" property="string(gather_id)" filter="true"/>&nbsp;
	    </td>
	    <td align="left" class="RecordRow" nowrap>
                    <bean:write  name="element" property="string(domain)" filter="true"/>&nbsp;
	    </td>	   	    
               		        	        	        
          </tr>
        </hrms:extenditerate>
   	    
     </table>
     <table  width="70%" align="center">
		<tr>
		    <td valign="bottom" class="tdFontcolor"><bean:message key="label.page.serial"/>
					<bean:write name="zpplanForm" property="zpplanDetailsForm.pagination.current" filter="true" />
					<bean:message key="label.page.sum"/>
					<bean:write name="zpplanForm" property="zpplanDetailsForm.pagination.count" filter="true" />
					<bean:message key="label.page.row"/>
					<bean:write name="zpplanForm" property="zpplanDetailsForm.pagination.pages" filter="true" />
					<bean:message key="label.page.page"/>
			</td>
	               <td  align="right" nowrap class="tdFontcolor">
		          <p align="right"><hrms:paginationlink name="zpplanForm" property="zpplanDetailsForm.pagination"
				nameId="zpplanDetailsForm" propertyId="zpplanDetailsProperty">
				</hrms:paginationlink>
			</td>
		</tr>
    </table>
       
    </tr>             
 </table>
</html:form>
