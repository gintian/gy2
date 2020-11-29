<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>

<script language="JavaScript" src="/js/validate.js"></script>
<script language="JavaScript" src="/js/function.js"></script>
<script language="javascript">
 function validate1()
  {   
      var valueInputs=document.getElementsByName("zpplanvo.string(start_date)");
      var dobj=valueInputs[0];     
      if(checkDate(dobj) == false)
      {
	 dobj.focus();
	 return false;
      }
      var valueInputs0=document.getElementsByName("zpplanvo.string(end_date)");
      var dobj0=valueInputs0[0];     
      if(checkDate(dobj0) == false)
      {
	 dobj0.focus();
	 return false;
      }
     
      return true;
 }
 function change_pos()
   {
    zpplanForm.action="/hire/zp_plan/add_zp_plan.do?b_org=link&pretype=UN";
    zpplanForm.submit();
   }
</script>

<%
   int i = 0;
%>

<html:form action="/hire/zp_plan/add_zp_plan">
      <br>
      <br>
     <fieldset align="center" style="width:90%;">
      <table width="700" border="0" cellpadding="0" cellspacing="0" align="center">
          <tr>
            <td colspan="4">
               <table width="80%" border="0" cellspacing="1"  align="center" cellpadding="1" bordercolor = "#223377"> 
                <tr>
		 	<td height="20" align="center" class="TableRow" nowrap colspan="4"><bean:message key="label.zp_job.plan"/>( <bean:write name="zpplanForm" property="zpplanvo.string(plan_id)" />)</td>
		 </tr> 
		  <tr>
		    <td><html:hidden name="zpplanForm" property="plan_id_value" styleClass="text6"/></td>
		 </tr>
                  <tr class="trDeep1">
                    <!-- <td align="right" nowrap valign="center"><bean:message key="lable.zp_plan.plan_id"/></td>
                     <td align="left"  nowrap valign="center">
                          <html:text name="zpplanForm" property="zpplanvo.string(plan_id)" disabled = "true"  styleClass="text6"/>
                     </td> -->
                     <td align="right" nowrap valign="center"><bean:message key="lable.zp_plan.name"/></td>
                     <td align="left"  nowrap valign="center">
                         <html:text name="zpplanForm" property="zpplanvo.string(name)" styleClass="text6" maxlength="50"/>
                     </td> 
                     <td align="right" nowrap valign="center"><bean:message key="lable.resource_plan.org_id"/></td>
                     <logic:equal name="zpplanForm" property="managepriv" value="UM">
                     <td align="left"  nowrap valign="center">
                          <html:hidden name="zpplanForm" property="zpplanvo.string(org_id)" styleClass="text6"/>
                          <html:hidden name="zpplanForm" property="orgparentcode"/> 
                          <html:text name="zpplanForm" property="org_id_value" readonly="true" styleClass="text6" onchange="change_pos();"/>
                          <img src="/images/code.gif"/>
                     </td>
                     </logic:equal>
                     <logic:notEqual name="zpplanForm" property="managepriv" value="UM">
                     <logic:notEqual name="zpplanForm" property="managepriv" value="UN">
                     <td align="left"  nowrap valign="center">
                          <html:hidden name="zpplanForm" property="zpplanvo.string(org_id)" styleClass="text6"/>
                          <html:hidden name="zpplanForm" property="orgparentcode"/> 
                          <html:text name="zpplanForm" property="org_id_value" readonly="true" styleClass="text6" onchange="change_pos();"/>
                          <img src="/images/code.gif"/>
                     </td>
                     </logic:notEqual>
                     <logic:equal name="zpplanForm" property="managepriv" value="UN">
                     <td align="left"  nowrap valign="center">
                          <html:hidden name="zpplanForm" property="zpplanvo.string(org_id)" styleClass="text6"/>
                          <html:hidden name="zpplanForm" property="orgparentcode"/> 
                          <html:text name="zpplanForm" property="org_id_value" readonly="true" styleClass="text6" onchange="change_pos();"/>
                          <img src="/images/code.gif" onclick='javascript:openInputCodeDialogOrg_1("UN","org_id_value","zpplanvo.string(org_id)",zpplanForm.orgparentcode.value);'/>
                     </td>
                     </logic:equal>
                     </logic:notEqual>
                 </tr>
                 <tr class="trShallow1">
                     
                     <td align="right" nowrap valign="center"><bean:message key="lable.zp_plan.dept_id"/></td>
                     <td align="left"  nowrap valign="center">
                          <html:hidden name="zpplanForm" property="zpplanvo.string(dept_id)" styleClass="text6"/>
                          <html:hidden name="zpplanForm" property="deptparentcode"/> 
                          <html:text name="zpplanForm" property="dept_id_value" readonly="true" styleClass="text6"/>
                          <img src="/images/code.gif" onclick='javascript:openInputCodeDialogOrg_1("UM","dept_id_value","zpplanvo.string(dept_id)",zpplanForm.deptparentcode.value);'/>
                     </td>
                     <td align="right" nowrap valign="center"><bean:message key="lable.zp_plan.staff_id"/></td>
                     <td align="left"  nowrap valign="center">
                         <html:text name="zpplanForm" property="zpplanvo.string(staff_id)" styleClass="text6"/>
                     </td>
               </tr>
               <tr class="trDeep1">
                    
                     <td align="right" nowrap valign="center"><bean:message key="lable.zp_plan.budget_fee"/></td>
                     <td align="left"  nowrap valign="center">
                         <html:text name="zpplanForm" property="zpplanvo.string(budget_fee)" styleClass="text6"/>
                     </td>
                     <td align="right" nowrap valign="center"><bean:message key="lable.zp_plan.start_date"/></td>
                     <td align="left"  nowrap valign="center">
                         <html:text name="zpplanForm" property="zpplanvo.string(start_date)" styleClass="text6"/>
                     </td>    
                 </tr> 
                 <tr class="trShallow1">
                     
                     <td align="right" nowrap valign="center"><bean:message key="lable.zp_plan.end_date"/></td>
                     <td align="left"  nowrap valign="center">
                         <html:text name="zpplanForm" property="zpplanvo.string(end_date)" styleClass="text6"/>
                     </td>
                     <td align="right" nowrap valign="center"><bean:message key="lable.zp_plan.plan_invite_amount"/></td>
                     <td align="left"  nowrap valign="center">
                         <html:text name="zpplanForm" property="zpplanvo.string(plan_invite_amount)" styleClass="text6"/>
                     </td>     
                 </tr>
                 <tr class="trDeep1">
                    
                     <td align="right" nowrap valign="center"><bean:message key="lable.zp_plan.domain"/></td>
                     <td align="left"  nowrap valign="center">
                         <html:text name="zpplanForm" property="zpplanvo.string(domain)" styleClass="text6"/>
                     </td>
                     <td align="right" nowrap valign="center"><bean:message key="lable.zp_plan.zp_object"/></td>
                     <td align="left"  nowrap valign="center" colspan="3">
                        <html:select name="zpplanForm" property="zpplanvo.string(zp_object)">
                           <html:option value="01"><bean:message key="lable.zp_plan.zp_object0"/></html:option>
                           <html:option value="02"><bean:message key="lable.zp_plan.zp_object1"/></html:option>
                           <html:option value="03"><bean:message key="lable.zp_plan.zp_object2"/></html:option>
                        </html:select>      
                     </td>     
                 </tr>   
                     
              <tr>
              <td align="center"  nowrap colspan="4">
                   
	 	     <hrms:submit styleClass="mybutton" property="b_save" onclick="document.zpplanForm.target='_self';validate('R','zpplanvo.string(name)','计划名称','R','org_id_value','所属单位','R','dept_id_value','负责部门','R','zpplanvo.string(staff_id)','负责人','RF','zpplanvo.string(budget_fee)','预算费用','R','zpplanvo.string(start_date)','开始日期','R','zpplanvo.string(end_date)','结束日期','RI','zpplanvo.string(plan_invite_amount)','计划招聘人数','R','zpplanvo.string(domain)','招聘地区');return (document.returnValue && validate1()&& ifqrbc());"><bean:message key="button.save"/></hrms:submit>
	 	     <html:reset styleClass="mybutton" property="reset"><bean:message key="button.clear"/></html:reset>
	 	    
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
		 <bean:message key="column.select"/>&nbsp;
            </td>         
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
            <td align="center" class="TableRow" nowrap>
		<bean:message key="label.edit"/>            	
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
            <td align="center" class="RecordRow" nowrap>
	   	 <hrms:checkmultibox name="zpplanForm" property="zpplanDetailsForm.select" value="true" indexes="indexes"/>&nbsp;
	    </td>           
         
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
	    <td align="left" class="RecordRow" nowrap width="100" style="word-break:break-all">
                    <bean:write  name="element" property="string(domain)" filter="false"/>&nbsp;
	    </td>
	   <td align="center" class="RecordRow" nowrap>
	     <logic:equal name="zpplanForm" property="flag" value="1">
	        <logic:equal name="zpplanForm" property="flag_mid" value="1">
		   <a href="/hire/zp_plan/add_zp_pos.do?b_query=link&plan_id_value=<bean:write name="element" property="string(plan_id)" filter="true"/>&details_id=<bean:write name="element" property="string(details_id)" filter="true"/>"><img src="/images/edit.gif" border=0></a>             	
	        </logic:equal>
	     </logic:equal>
	     <logic:notEqual name="zpplanForm" property="flag" value="1">
		   <a href="/hire/zp_plan/add_zp_pos.do?b_query=link&plan_id_value=<bean:write name="element" property="string(plan_id)" filter="true"/>&details_id=<bean:write name="element" property="string(details_id)" filter="true"/>&gather_id=<bean:write name="element" property="string(gather_id)" filter="true"/>"><img src="/images/edit.gif" border=0></a>             	
	     </logic:notEqual>
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
     <table  width="70%" align="center">
          <tr>
            <td align="center">
            <logic:equal name="zpplanForm" property="flag" value="1">
              <logic:equal name="zpplanForm" property="flag_mid" value="0">
              <hrms:submit styleClass="mybutton" property="b_request" disabled = "true">
            		<bean:message key="lable.zp_plan.gather_id"/>
	 	</hrms:submit>
	 	<hrms:submit styleClass="mybutton" property="b_pos" disabled = "true">
            		<bean:message key="lable.zp_plan.short_pos"/>
	 	</hrms:submit>
         	<hrms:submit styleClass="mybutton" property="b_detail_add" disabled = "true">
            		<bean:message key="button.insert"/>
	 	</hrms:submit>
	 	<hrms:submit styleClass="mybutton" property="b_detail_delete" disabled = "true">
            		<bean:message key="button.delete"/>
	 	</hrms:submit>
	 	
	    </logic:equal>
	 	<logic:notEqual name="zpplanForm" property="flag_mid" value="0">
	 	<hrms:submit styleClass="mybutton" property="b_request" onclick="return ifygxq();">
            		<bean:message key="lable.zp_plan.gather_id"/>
	 	</hrms:submit>
	 	<hrms:submit styleClass="mybutton" property="b_pos">
            		<bean:message key="lable.zp_plan.short_pos"/>
	 	</hrms:submit>
         	   <hrms:submit styleClass="mybutton" property="b_detail_add">
            		<bean:message key="button.insert"/>
	 	   </hrms:submit>
	 	<hrms:submit styleClass="mybutton" property="b_detail_delete" onclick="return ifdel()">
            		<bean:message key="button.delete"/>
	 	</hrms:submit>
	 	
	      </logic:notEqual>	
	    </logic:equal> 
	   <logic:notEqual name="zpplanForm" property="flag" value="1">
	   <hrms:submit styleClass="mybutton" property="b_request" onclick="return ifygxq();">
            		<bean:message key="lable.zp_plan.gather_id"/>
	 	</hrms:submit>
	 	<hrms:submit styleClass="mybutton" property="b_pos">
            		<bean:message key="lable.zp_plan.short_pos"/>
	 	</hrms:submit>
         	   <hrms:submit styleClass="mybutton" property="b_detail_add">
            		<bean:message key="button.insert"/>
	 	   </hrms:submit>
	 	<hrms:submit styleClass="mybutton" property="b_detail_delete" onclick="return ifdel()">
            		<bean:message key="button.delete"/>
	 	</hrms:submit>
	 	
	    </logic:notEqual>		
         	
        
            </td>
          </tr>          
    </table>
   </fieldset>   
    </tr>   
    <tr>
       <td width="100%" align="center">
       <br>
       <br>
                  <logic:equal name="zpplanForm" property="flag" value="1">
                      <logic:equal name="zpplanForm" property="flag_mid" value="0">
                       <hrms:submit styleClass="mybutton" property="b_release" disabled = "true"><bean:message key="button.release"/></hrms:submit>
                      </logic:equal>
                      <logic:notEqual name="zpplanForm" property="flag_mid" value="0">
                       <hrms:submit styleClass="mybutton" property="b_release"  disabled = "false" onclick = "return ifrelease()"><bean:message key="button.release"/></hrms:submit>
                      </logic:notEqual>
                    </logic:equal>
                    <logic:notEqual name="zpplanForm" property="flag" value="1">
                             <hrms:submit styleClass="mybutton" property="b_release" disabled = "false"><bean:message key="button.release"/></hrms:submit>
                    </logic:notEqual>
	         <hrms:submit styleClass="mybutton" property="b_return"><bean:message key="button.return"/></hrms:submit>     
       </td>
    </tr>                
 </table>
</html:form>
