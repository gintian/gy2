<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%
   int i = 0;
%>

<html:form action="/hire/resource_plan/resource_plan_view">
      <br>
      <br>
      <table width="700" border="0" cellpadding="0" cellspacing="0" align="center">
          <tr>
            <td colspan="4">
               <table width="80%" border="0" cellspacing="1"  align="center" cellpadding="1" bordercolor = "#223377"> 
                <tr>
		 	<td height="20" align="center" class="TableRow" nowrap colspan="4"><bean:message key="hire.manpower.plan"/>(<bean:write  name="resourcePlanForm" property="zpplanvo.string(plan_id)" filter="true"/>)</td>
		 </tr> 
                  <tr class="trDeep1">
                     <!--<td align="right" nowrap valign="center"><bean:message key="lable.resource_plan.plan_id"/></td>
                     <td align="left"  nowrap valign="center">
                          <bean:write  name="resourcePlanForm" property="zpplanvo.string(plan_id)" filter="true"/>
                      </td>-->
                     
                     <td align="right" nowrap valign="center"><bean:message key="lable.resource_plan.name"/></td>
                     <td align="left"  nowrap valign="center">
                        <bean:write  name="resourcePlanForm" property="zpplanvo.string(name)" filter="true"/>
                      </td>
                     <td align="right" nowrap valign="center"><bean:message key="lable.resource_plan.org_id"/></td>
                     <td align="left"  nowrap valign="center">
                          <bean:write  name="resourcePlanForm" property="org_id_value" filter="true"/>
                      </td>
                   </tr>
                   <tr class="trDeep1">
                     <td align="right" nowrap valign="center"><bean:message key="lable.resource_plan.run_date"/></td>
                     <td align="left"  nowrap valign="center">
                         <bean:write  name="resourcePlanForm" property="zpplanvo.string(run_date)" filter="true"/>
                      </td>
                     <td align="right" nowrap valign="center"><bean:message key="lable.hiremanage.create_date"/></td>
                     <td align="left"  nowrap valign="center">
                          <bean:write  name="resourcePlanForm" property="zpplanvo.string(create_date)" filter="true"/>
                      </td>     
                   </tr>  
                   <tr class="trDeep1">
                      
                     <td align="right" nowrap valign="center"><bean:message key="lable.hiremanage.staff_id"/></td>
                     <td align="left"  nowrap valign="center">
                          <bean:write  name="resourcePlanForm" property="zpplanvo.string(staff_id)" filter="true"/>
                      </td>
                     <td align="right" nowrap valign="center"><bean:message key="lable.hiremanage.status"/></td>
                     <td align="left"  nowrap valign="center" >
                            <logic:equal name="resourcePlanForm" property="zpplanvo.string(status)" value="01">
                              <bean:message key="label.hiremanage.status1"/>&nbsp;
                          </logic:equal>
                          <logic:equal name="resourcePlanForm" property="zpplanvo.string(status)" value="02">
                              <bean:message key="label.hiremanage.status2"/>&nbsp;
                          </logic:equal>
                          <logic:equal name="resourcePlanForm" property="zpplanvo.string(status)" value="03">
                              <bean:message key="label.hiremanage.status3"/>&nbsp;
                          </logic:equal>
                          <logic:equal name="resourcePlanForm" property="zpplanvo.string(status)" value="04">
                              <bean:message key="label.hiremanage.status4"/>&nbsp;
                          </logic:equal>
                          <logic:equal name="resourcePlanForm" property="zpplanvo.string(status)" value="05">
                              <bean:message key="label.hiremanage.status5"/>&nbsp;
                          </logic:equal>
                          <logic:equal name="resourcePlanForm" property="zpplanvo.string(status)" value="06">
                              <bean:message key="label.hiremanage.status6"/>&nbsp;
                          </logic:equal>
                      </td>   
                   </tr>                    
                    <tr class="trShallow1">
                     <td align="right" nowrap valign="center"><bean:message key="lable.resource_plan.description"/></td>
                     <td align="left"  nowrap valign="center" colspan="3">
                          <bean:write  name="resourcePlanForm" property="zpplanvo.string(description)" filter="true"/>
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
		<bean:message key="lable.resource_plan.dept_id"/>&nbsp;
	    </td>
            <td align="center" class="TableRow" nowrap>
		<bean:message key="lable.resource_plan.pos_id"/>&nbsp;
	    </td>
	    <td align="center" class="TableRow" nowrap>
		<bean:message key="lable.hiremanage.amount"/>&nbsp;
	    </td>
	    <td align="center" class="TableRow" nowrap>
		<bean:message key="lable.resource_plan.type"/>&nbsp;
	    </td>
            <td align="center" class="TableRow" nowrap>
		<bean:message key="lable.hiremanage.reason"/>&nbsp;
	    </td>       	
	    </td> 	    		        	        	        
           </tr>
   	  </thead>
   	  <hrms:extenditerate id="element" name="resourcePlanForm" property="zpplanDetailsForm.list" indexes="indexes"  pagination="zpplanDetailsForm.pagination" pageCount="10" scope="session">
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
               <logic:equal name="element" property="string(type)" value="01">
                    <bean:message key="label.hiremanage.type1"/>&nbsp;
               </logic:equal>
               <logic:notEqual name="element" property="string(type)" value="01">
                      <bean:message key="label.hiremanage.type2"/>&nbsp;
               </logic:notEqual> 
	    </td> 	                
            <td align="left" class="RecordRow" nowrap width="100" style="word-break:break-all">
                    <bean:write  name="element" property="string(reason)" filter="false"/>&nbsp;
	    </td>	   	    
               		        	        	        
          </tr>
        </hrms:extenditerate>
   	    
     </table>
     <table  width="70%" align="center">
		<tr>
		    <td valign="bottom" class="tdFontcolor"><bean:message key="label.page.serial"/>
					<bean:write name="resourcePlanForm" property="zpplanDetailsForm.pagination.current" filter="true" />
				<bean:message key="label.page.sum"/>
					<bean:write name="resourcePlanForm" property="zpplanDetailsForm.pagination.count" filter="true" />
				<bean:message key="label.page.row"/>
					<bean:write name="resourcePlanForm" property="zpplanDetailsForm.pagination.pages" filter="true" />
					<bean:message key="label.page.page"/>
			</td>
	               <td  align="right" nowrap class="tdFontcolor">
		          <p align="right"><hrms:paginationlink name="resourcePlanForm" property="zpplanDetailsForm.pagination"
				nameId="zpplanDetailsForm" propertyId="zpplanDetailsProperty">
				</hrms:paginationlink>
			</td>
		</tr>
    </table>
       
    </tr>             
        
 </table>
</html:form>
