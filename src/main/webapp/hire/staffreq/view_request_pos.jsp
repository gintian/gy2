<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%
   int i = 0;
%>

<html:form action="/hire/staffreq/view_request_pos">
      <br>
      <br>
      <table width="700" border="0" cellpadding="0" cellspacing="0" align="center">
          <tr>
            <td colspan="4">
               <table width="80%" border="0" cellspacing="1"  align="center" cellpadding="1" bordercolor = "#223377"> 
                <tr>
		 	<td height="20" align="center" class="TableRow" nowrap colspan="4"><ben:message key="hire.requirement.unit"/>(<bean:write  name="hireManageForm" property="zpgathervo.string(gather_id)" filter="true"/>)</td>
		 </tr> 
                  <tr class="trDeep1">
                     <td align="right" nowrap valign="center"><bean:message key="lable.hiremanage.org_id"/></td>
                     <td align="left"  nowrap valign="center">
                          <bean:write  name="hireManageForm" property="org_id_value" filter="true"/>
                      </td>
                     
                     <td align="right" nowrap valign="center"><bean:message key="lable.hiremanage.dept_id"/></td>
                     <td align="left"  nowrap valign="center">
                        <bean:write  name="hireManageForm" property="dept_id_value" filter="true"/>
                      </td>
                   </tr>
                   <tr class="trDeep1">
                    <!-- <td align="right" nowrap valign="center"><bean:message key="lable.hiremanage.gather_id"/></td>
                     <td align="left"  nowrap valign="center">
                          <bean:write  name="hireManageForm" property="zpgathervo.string(gather_id)" filter="true"/>
                      </td>  -->
                     <td align="right" nowrap valign="center"><bean:message key="lable.hiremanage.valid_date"/></td>
                     <td align="left"  nowrap valign="center">
                         <bean:write  name="hireManageForm" property="zpgathervo.string(valid_date)" filter="true"/>
                      </td>
                      <td align="right" nowrap valign="center"><bean:message key="lable.hiremanage.create_date"/></td>
                      <td align="left"  nowrap valign="center">
                          <bean:write  name="hireManageForm" property="zpgathervo.string(create_date)" filter="true"/>
                      </td>     
                   </tr>  
                   <tr class="trDeep1">                      
                     <td align="right" nowrap valign="center"><bean:message key="lable.hiremanage.staff_id"/></td>
                     <td align="left"  nowrap valign="center">
                          <bean:write  name="hireManageForm" property="zpgathervo.string(staff_id)" filter="true"/>
                      </td>
                     <td align="right" nowrap valign="center"><bean:message key="lable.hiremanage.gather_type"/></td>
                     <td align="left"  nowrap valign="center">
                         <logic:equal name="hireManageForm" property="zpgathervo.string(gather_type)" value="0">
                              <bean:message key="label.hiremanage.gather_type0"/>&nbsp;
                          </logic:equal>
                          <logic:notEqual name="hireManageForm" property="zpgathervo.string(gather_type)" value="0">
                               <bean:message key="label.hiremanage.gather_type1"/>&nbsp;
                          </logic:notEqual>
                      </td>     
                   </tr>  
                   <tr class="trDeep1">                     
                     <td align="right" nowrap valign="center"><bean:message key="lable.hiremanage.status"/></td>
                     <td align="left"  nowrap valign="center">
                            <logic:equal name="hireManageForm" property="zpgathervo.string(status)" value="01">
                              <bean:message key="label.hiremanage.status1"/>&nbsp;
                          </logic:equal>
                          <logic:equal name="hireManageForm" property="zpgathervo.string(status)" value="02">
                              <bean:message key="label.hiremanage.status2"/>&nbsp;
                          </logic:equal>
                          <logic:equal name="hireManageForm" property="zpgathervo.string(status)" value="03">
                              <bean:message key="label.hiremanage.status3"/>&nbsp;
                          </logic:equal>
                          <logic:equal name="hireManageForm" property="zpgathervo.string(status)" value="04">
                              <bean:message key="label.hiremanage.status4"/>&nbsp;
                          </logic:equal>
                          <logic:equal name="hireManageForm" property="zpgathervo.string(status)" value="05">
                              <bean:message key="label.hiremanage.status5"/>&nbsp;
                          </logic:equal>
                           <logic:equal name="hireManageForm" property="zpgathervo.string(status)" value="06">
                              <bean:message key="label.hiremanage.status6"/>&nbsp;
                          </logic:equal>
                      </td>
                      <td align="right" nowrap valign="center"></td>
                      <td align="left"  nowrap valign="center">                          
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
		<bean:message key="lable.hiremanage.pos_id"/>&nbsp;
	    </td>
            <td align="center" class="TableRow" nowrap>
		<bean:message key="lable.hiremanage.amount"/>&nbsp;
	    </td>
	    <td align="center" class="TableRow" nowrap>
		<bean:message key="lable.hiremanage.type"/>&nbsp;
	    </td>
            <td align="center" class="TableRow" nowrap>
		<bean:message key="lable.hiremanage.reason"/>&nbsp;
	    </td>       	
	    </td> 	    		        	        	        
           </tr>
   	  </thead>
   	  <hrms:extenditerate id="element" name="hireManageForm" property="gatherPosForm.list" indexes="indexes"  pagination="gatherPosForm.pagination" pageCount="10" scope="session">
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
					<bean:write name="hireManageForm" property="gatherPosForm.pagination.current" filter="true" />
				<bean:message key="label.page.sum"/>
					<bean:write name="hireManageForm" property="gatherPosForm.pagination.count" filter="true" />
				<bean:message key="label.page.row"/>
					<bean:write name="hireManageForm" property="gatherPosForm.pagination.pages" filter="true" />
					<bean:message key="label.page.page"/>
			</td>
	               <td  align="right" nowrap class="tdFontcolor">
		          <p align="right"><hrms:paginationlink name="hireManageForm" property="gatherPosForm.pagination"
				nameId="gatherPosForm" propertyId="gatherPosProperty">
				</hrms:paginationlink>
			</td>
		</tr>
    </table>
       
    </tr>             
        
 </table>
</html:form>
