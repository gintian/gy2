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
      var valueInputs=document.getElementsByName("zpjobvo.string(start_date)");
      var dobj=valueInputs[0];      
      if(checkDate(dobj) == false)
      {
	 dobj.focus();
	 return false;
      }
      var valueInputs0=document.getElementsByName("zpjobvo.string(end_date)");
      var dobj0=valueInputs0[0];      
      if(checkDate(dobj0) == false)
      {
	 dobj0.focus();
	 return false;
      }
      return true;
 }
</script>

<%
   int i = 0;
%>

<html:form action="/hire/zp_job/add_zp_job">
      <br>
      <br>
    <fieldset align="center" style="width:90%;">
      <table width="700" border="0" cellpadding="0" cellspacing="0" align="center">
          <tr>
            <td colspan="4">
               <table width="75%" border="0" cellspacing="1"  align="center" cellpadding="1" bordercolor = "#223377"> 
                <tr>
		 	<td height="20" align="center" class="TableRow" nowrap colspan="4"><bean:message key="hire.activity.activity"/>(<bean:write name="zpjobForm" property="zpjobvo.string(zp_job_id)" />)</td>
		 </tr> 
		  <tr>
		    <td><html:hidden name="zpjobForm" property="zp_job_id_value" styleClass="text6"/></td>
		 </tr>
                 
                 <tr class="trDeep1">
                     <!--<td align="right" nowrap valign="center"><bean:message key="label.zp_job.zp_job_id"/></td>
                     <td align="left"  nowrap valign="center">
                          <html:text name="zpjobForm" property="zpjobvo.string(zp_job_id)" disabled = "true"  styleClass="text6"/>
                     </td> -->
                     <td align="right" nowrap valign="center"><bean:message key="label.zp_job.name"/></td>
                     <td align="left"  nowrap valign="center">
                         <html:text name="zpjobForm" property="zpjobvo.string(name)" styleClass="text6"/>
                     </td> 
                      <td align="right" nowrap valign="center"><bean:message key="label.zp_job.plan"/></td>
                     <logic:equal name="zpjobForm" property="managepriv" value="UM">
                     <td align="left"  nowrap valign="center">
                          <hrms:importgeneraldata showColumn="name" valueColumn="plan_id" flag="false"  paraValue=""
                  			sql="select plan_id,name from zp_plan where status='05' and plan_id in (select plan_id from zp_plan_details where dept_id = '${zpjobForm.manageprivvalue}') " collection="list" scope="page"/> 
            				<html:select name="zpjobForm" property="zpjobvo.string(plan_id)" size="1"> 
            				<html:options collection="list" property="dataValue" labelProperty="dataName"/> 
            				</html:select>

                     </td> 
                     </logic:equal>
                      <logic:notEqual name="zpjobForm" property="managepriv" value="UM">
                      <logic:equal name="zpjobForm" property="managepriv" value="UN">
                     <td align="left"  nowrap valign="center">
                          <hrms:importgeneraldata showColumn="name" valueColumn="plan_id" flag="false"  paraValue="05"
                  			sql="select plan_id,name from zp_plan where status=? and org_id like '${zpjobForm.manageprivvalue}%'" collection="list" scope="page"/> 
            				<html:select name="zpjobForm" property="zpjobvo.string(plan_id)" size="1"> 
            				<html:options collection="list" property="dataValue" labelProperty="dataName"/> 
            				</html:select>

                     </td> 
                     </logic:equal>
                     <logic:notEqual name="zpjobForm" property="managepriv" value="UN">
                     <td align="left"  nowrap valign="center">
                          <hrms:importgeneraldata showColumn="name" valueColumn="plan_id" flag="false"  paraValue="05"
                  			sql="select plan_id,name from zp_plan where status=? " collection="list" scope="page"/> 
            				<html:select name="zpjobForm" property="zpjobvo.string(plan_id)" size="1"> 
            				<html:options collection="list" property="dataValue" labelProperty="dataName"/> 
            				</html:select>

                     </td> 
                     </logic:notEqual>
                     </logic:notEqual>
                 </tr>
                  <tr class="trDeep1">
                    
                     <td align="right" nowrap valign="center"><bean:message key="label.zp_job.resource_id"/></td>
                     <td align="left"  nowrap valign="center">
                          <hrms:importgeneraldata showColumn="name" valueColumn="resource_id" flag="true"  paraValue="1"
                  			sql="select resource_id,name from zp_resource where 1=? " collection="list" scope="page"/> 
            				<html:select name="zpjobForm" property="zpjobvo.string(resource_id)" size="1"> 
            				<html:options collection="list" property="dataValue" labelProperty="dataName"/> 
            				</html:select>
                     </td> 
                     <td align="right" nowrap valign="center"><bean:message key="lable.zp_plan.start_date"/></td>
                     <td align="left"  nowrap valign="center">
                         <html:text name="zpjobForm" property="zpjobvo.string(start_date)" styleClass="text6"/>
                     </td>
                 </tr>
               <tr class="trShallow1">
                    
                     <td align="right" nowrap valign="center"><bean:message key="lable.zp_plan.end_date"/></td>
                     <td align="left"  nowrap valign="center">
                         <html:text name="zpjobForm" property="zpjobvo.string(end_date)" styleClass="text6"/>
                     </td>
                     <td align="right" nowrap valign="center"><bean:message key="lable.zp_plan.staff_id"/></td>
                     <td align="left"  nowrap valign="center" colspan="3">
                         <html:text name="zpjobForm" property="zpjobvo.string(principal)" styleClass="text6"/>
                     </td>     
                 </tr>                 
                 <tr class="trShallow1">
                        <td align="right" nowrap valign="top"><bean:message key="label.zp_job.attendee"/></td>
                     <td align="left"  nowrap valign="center" colspan="3">
                          <html:textarea name="zpjobForm" property="zpjobvo.string(attendee)" cols="61" rows="3" styleClass="text6"/>
                      </td>
                 </tr> 
              <tr>
              <td align="center"  nowrap colspan="4">                  	
	 	     <hrms:submit styleClass="mybutton" property="b_save" onclick="document.zpjobForm.target='_self';validate('R','zpjobvo.string(name)','活动名称','R','zpjobvo.string(start_date)','开始日期','R','zpjobvo.string(end_date)','结束日期','R','zpjobvo.string(principal)','负责人','R','zpjobvo.string(attendee)','参加人','R','zpjobvo.string(resource_id)','招聘资源');return (document.returnValue && validate1() && ifqrbc());"><bean:message key="button.save"/></hrms:submit>
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
          <table width="65%" border="0" cellspacing="1"  align="center" cellpadding="1" class="ListTable">
   	  <thead>
           <tr>
            <td align="center" class="TableRow" nowrap>
		 <bean:message key="column.select"/>&nbsp;
            </td>         
            <td align="center" class="TableRow" nowrap>
		<bean:message key="label.zp_job.detailname"/>&nbsp;
	    </td>
            <td align="center" class="TableRow" nowrap>
		<bean:message key="label.zp_job.charge"/>&nbsp;
	    </td>
            <td align="center" class="TableRow" nowrap>
		<bean:message key="label.edit"/>            	
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
            <td align="center" class="RecordRow" nowrap>
	   	 <hrms:checkmultibox name="zpjobForm" property="zpjobDetailsForm.select" value="true" indexes="indexes"/>&nbsp;
	    </td>           
         
            <td align="left" class="RecordRow" nowrap>
                    <bean:write  name="element" property="string(detailname)" filter="true"/>&nbsp;
	    </td>
            <td align="right" class="RecordRow" nowrap>
                    <bean:write  name="element" property="string(charge)" filter="true"/>&nbsp;
	    </td>
	   <td align="center" class="RecordRow" nowrap>
	     <logic:equal name="zpjobForm" property="flag" value="1">
	        <logic:equal name="zpjobForm" property="flag_mid" value="1">
		   <a href="/hire/zp_job/add_zp_job_detail.do?b_query=link&a_id=<bean:write name="element" property="string(zp_job_id)" filter="true"/>&detail_id=<bean:write name="element" property="string(detail_id)" filter="true"/>"><img src="/images/edit.gif" border=0></a>             	
	        </logic:equal>
	     </logic:equal>
	     <logic:notEqual name="zpjobForm" property="flag" value="1">
		   <a href="/hire/zp_job/add_zp_job_detail.do?b_query=link&a_id=<bean:write name="element" property="string(zp_job_id)" filter="true"/>&detail_id=<bean:write name="element" property="string(detail_id)" filter="true"/>"><img src="/images/edit.gif" border=0></a>             	
	     </logic:notEqual>
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
     <table  width="70%" align="center">
          <tr>
            <td align="center">
            <logic:equal name="zpjobForm" property="flag" value="1">
              <logic:equal name="zpjobForm" property="flag_mid" value="0">
         	<hrms:submit styleClass="mybutton" property="b_detail_add" disabled = "true">
            		<bean:message key="button.insert"/>
	 	</hrms:submit>
	 	<hrms:submit styleClass="mybutton" property="b_detail_delete" disabled = "true">
            		<bean:message key="button.delete"/>
	 	</hrms:submit>
	 	
	    </logic:equal>
	 	<logic:notEqual name="zpjobForm" property="flag_mid" value="0">
         	   <hrms:submit styleClass="mybutton" property="b_detail_add">
            		<bean:message key="button.insert"/>
	 	   </hrms:submit>
	 	<hrms:submit styleClass="mybutton" property="b_detail_delete" onclick="return ifdel()">
            		<bean:message key="button.delete"/>
	 	</hrms:submit>
	 	
	      </logic:notEqual>	
	    </logic:equal> 
	   <logic:notEqual name="zpjobForm" property="flag" value="1">
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
 </table>

  <table  width="70%" align="center">
        <tr>
              <td align="center"  nowrap colspan="4">
              <br>
              <br>
                       <logic:equal name="zpjobForm" property="flag" value="1">
                         <logic:equal name="zpjobForm" property="flag_mid" value="0">
         	           <hrms:submit styleClass="mybutton" property="b_submit" disabled = "true">
            		     <bean:message key="lable.welcomeinv.sumbit"/>
	 	           </hrms:submit>
	                 </logic:equal>
	 	         <logic:notEqual name="zpjobForm" property="flag_mid" value="0">
         	             <hrms:submit styleClass="mybutton" property="b_submit" onclick = "return ifqrtj()">
            		         <bean:message key="lable.welcomeinv.sumbit"/>
	 	             </hrms:submit>
	                 </logic:notEqual>	
	              </logic:equal> 
	              <logic:notEqual name="zpjobForm" property="flag" value="1">
         	          <hrms:submit styleClass="mybutton" property="b_submit" onclick = "return ifqrtj()">
            		     <bean:message key="lable.welcomeinv.sumbit"/>
	 	          </hrms:submit>
	              </logic:notEqual>		 	    
	 	     <hrms:submit styleClass="mybutton" property="b_return"><bean:message key="button.return"/></hrms:submit>    
             </td>
         </tr> 
   </table>
</html:form>
