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
      var tag=true;    
      var valueInputs=document.getElementsByName("zpplanvo.string(run_date)");
      var dobj=valueInputs[0];
      tag= checkDate(dobj) && tag;      
      if(tag==false)
      {
	 dobj.focus();
	 return false;
      }
      return true;
 }
</script>

<%
   int i = 0;
%>

<html:form action="/hire/resource_plan/resource_plan">
      <br>
      <br>
     <fieldset align="center" style="width:90%;">
      <table width="700" border="0" cellpadding="0" cellspacing="0" align="center">
          <tr>
            <td colspan="4">
               <table width="80%" border="0" cellspacing="1"  align="center" cellpadding="1" bordercolor = "#223377"> 
                <tr>
		 	<td height="20" align="center" class="TableRow" nowrap colspan="4"><bean:message key="hire.manpower.plan"/>(<bean:write name="resourcePlanForm" property="zpplanvo.string(plan_id)" />)</td>
		 </tr> 
		  <tr>
		    <td><html:hidden name="resourcePlanForm" property="plan_id_value" styleClass="text6"/></td>
		 </tr>
                  <tr class="trDeep1">
                     <!--<td align="right" nowrap valign="center"><bean:message key="lable.resource_plan.plan_id"/></td>
                     <td align="left"  nowrap valign="center">
                          <html:text name="resourcePlanForm" property="zpplanvo.string(plan_id)" disabled = "true"  styleClass="text6"/>
                     </td> -->
                     <td align="right" nowrap valign="center"><bean:message key="lable.resource_plan.name"/></td>
                     <td align="left"  nowrap valign="center">
                         <html:text name="resourcePlanForm" property="zpplanvo.string(name)" styleClass="text6" maxlength="50"/>
                     </td> 
                      <td align="right" nowrap valign="center"><bean:message key="lable.resource_plan.org_id"/></td>
                     <logic:equal name="resourcePlanForm" property="managepriv" value="UM">
                     <td align="left"  nowrap valign="center">
                          <html:hidden name="resourcePlanForm" property="zpplanvo.string(org_id)" styleClass="text6"/>
                          <html:hidden name="resourcePlanForm" property="orgparentcode"/> 
                          <html:text name="resourcePlanForm" property="org_id_value" readonly="true" styleClass="text6"/>
                          <img src="/images/code.gif"/>
                     </td>
                     </logic:equal>
                     <logic:notEqual name="resourcePlanForm" property="managepriv" value="UM">
                     <logic:notEqual name="resourcePlanForm" property="managepriv" value="UN">
                     <td align="left"  nowrap valign="center">
                          <html:hidden name="resourcePlanForm" property="zpplanvo.string(org_id)" styleClass="text6"/>
                          <html:hidden name="resourcePlanForm" property="orgparentcode"/> 
                          <html:text name="resourcePlanForm" property="org_id_value" readonly="true" styleClass="text6"/>
                          <img src="/images/code.gif"/>
                     </td>
                     </logic:notEqual>
                     <logic:equal name="resourcePlanForm" property="managepriv" value="UN">
                     <td align="left"  nowrap valign="center">
                          <html:hidden name="resourcePlanForm" property="zpplanvo.string(org_id)" styleClass="text6"/>
                          <html:hidden name="resourcePlanForm" property="orgparentcode"/> 
                          <html:text name="resourcePlanForm" property="org_id_value" readonly="true" styleClass="text6"/>
                          <img src="/images/code.gif" onclick='javascript:openInputCodeDialogOrg_1("UN","org_id_value","zpplanvo.string(org_id)",resourcePlanForm.orgparentcode.value);'/>
                     </td>
                     </logic:equal>
                     </logic:notEqual>
                 </tr>
                 <tr class="trDeep1">         
                     <td align="right" nowrap valign="center"><bean:message key="lable.resource_plan.run_date"/></td>
                     <td align="left"  nowrap valign="center">
                         <html:text name="resourcePlanForm" property="zpplanvo.string(run_date)" styleClass="text6"/>
                     </td>  
                     <td align="right" nowrap valign="center"></td>
                     <td align="left"  nowrap valign="center"></td>      
                 </tr>  
                <tr class="trShallow1">
                        <td align="right" nowrap valign="top"><bean:message key="lable.resource_plan.description"/></td>
                     <td align="left"  nowrap valign="center" colspan="3">
                          <html:textarea name="resourcePlanForm" property="zpplanvo.string(description)" cols="61" rows="3" styleClass="text6"/>
                      </td>
              </tr> 
              <tr>
              <td align="center"  nowrap colspan="4">
             	
	 	     <hrms:submit styleClass="mybutton" property="b_save" onclick="document.resourcePlanForm.target='_self';validate('R','zpplanvo.string(name)','规划名称','R','org_id_value','所属单位','R','zpplanvo.string(run_date)','执行日期','R','zpplanvo.string(description)','内容说明');return (document.returnValue && validate1() && ifqrbc());"><bean:message key="button.save"/></hrms:submit>
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
            <td align="center" class="TableRow" nowrap>
		<bean:message key="label.edit"/>            	
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
            <td align="center" class="RecordRow" nowrap>
	   	 <hrms:checkmultibox name="resourcePlanForm" property="zpplanDetailsForm.select" value="true" indexes="indexes"/>&nbsp;
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
	   <td align="center" class="RecordRow" nowrap>
	     <logic:equal name="resourcePlanForm" property="flag" value="1">
	        <logic:equal name="resourcePlanForm" property="flag_mid" value="1">
		   <a href="/hire/resource_plan/add_plan_pos.do?b_query=link&a_id=<bean:write name="element" property="string(plan_id)" filter="true"/>&key_id=<bean:write name="element" property="string(key_id)" filter="true"/>"><img src="/images/edit.gif" border=0></a>             	
	        </logic:equal>
	     </logic:equal>
	     <logic:notEqual name="resourcePlanForm" property="flag" value="1">
		   <a href="/hire/resource_plan/add_plan_pos.do?b_query=link&a_id=<bean:write name="element" property="string(plan_id)" filter="true"/>&key_id=<bean:write name="element" property="string(key_id)" filter="true"/>"><img src="/images/edit.gif" border=0></a>             	
	     </logic:notEqual>
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
     <table  width="70%" align="center">
          <tr>
            <td align="center">
            <logic:equal name="resourcePlanForm" property="flag" value="1">
              <logic:equal name="resourcePlanForm" property="flag_mid" value="0">
                <hrms:submit styleClass="mybutton" property="b_request" disabled = "true">
            		<bean:message key="lable.resource_plan.request"/>
	 	</hrms:submit>
         	<hrms:submit styleClass="mybutton" property="b_detail_add" disabled = "true">
            		<bean:message key="button.insert"/>
	 	</hrms:submit>
	 	<hrms:submit styleClass="mybutton" property="b_detail_delete" disabled = "true">
            		<bean:message key="button.delete"/>
	 	</hrms:submit>	 	
	     </logic:equal>
	 	<logic:notEqual name="resourcePlanForm" property="flag_mid" value="0">
	 	<hrms:submit styleClass="mybutton" property="b_request" onclick="return ifyosq();">
            		<bean:message key="lable.resource_plan.request"/>
	 	</hrms:submit>
         	<hrms:submit styleClass="mybutton" property="b_detail_add">
            		<bean:message key="button.insert"/>
	 	</hrms:submit>
	 	<hrms:submit styleClass="mybutton" property="b_detail_delete">
            		<bean:message key="button.delete"/>
	 	</hrms:submit>
	 	
	      </logic:notEqual>	
	    </logic:equal> 
	   <logic:notEqual name="resourcePlanForm" property="flag" value="1">
	        <hrms:submit styleClass="mybutton" property="b_request" onclick="return ifyosq();">
            		<bean:message key="lable.resource_plan.request"/>
	 	</hrms:submit>
         	<hrms:submit styleClass="mybutton" property="b_detail_add" >
            		<bean:message key="button.insert"/>
	 	</hrms:submit>
	 	<hrms:submit styleClass="mybutton" property="b_detail_delete">
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
           <logic:equal name="resourcePlanForm" property="flag" value="1">
              <logic:equal name="resourcePlanForm" property="flag_mid" value="0">
         	<hrms:submit styleClass="mybutton" property="b_submit" disabled = "true">
            		<bean:message key="lable.welcomeinv.sumbit"/>
	 	</hrms:submit>
	    </logic:equal>
	 	<logic:notEqual name="resourcePlanForm" property="flag_mid" value="0">
         	<hrms:submit styleClass="mybutton" property="b_submit" disabled = "false">
            		<bean:message key="lable.welcomeinv.sumbit"/>
	 	</hrms:submit>
	      </logic:notEqual>	
	    </logic:equal> 
	   <logic:notEqual name="resourcePlanForm" property="flag" value="1">
         	  <hrms:submit styleClass="mybutton" property="b_submit" disabled = "false">
            		<bean:message key="lable.welcomeinv.sumbit"/>
	 	 </hrms:submit>
	    </logic:notEqual>	
	        <hrms:submit styleClass="mybutton" property="b_return"><bean:message key="button.return"/></hrms:submit>    
       </td>
    </tr>            
 </table>
</html:form>
