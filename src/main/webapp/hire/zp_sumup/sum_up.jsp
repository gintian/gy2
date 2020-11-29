<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ page import="com.hrms.frame.dao.RecordVo"%>
<%
   int i = 0;
%>
<script language="JavaScript" src="/js/validate.js"></script>
<script language="JavaScript" src="/js/function.js"></script>
<script language="javascript">
   function job_change()
   {
      zpSumupForm.action="/hire/zp_sumup/sum_up.do?b_query=link";
      zpSumupForm.submit();
   }  
   function jobplan_change()
   {
      zpSumupForm.action="/hire/zp_sumup/sum_up.do?b_plan=link";
      zpSumupForm.submit();
   }                     
  function validates()
  {
    var tag=true;        
        <hrms:extenditerate id="element" name="zpSumupForm" property="zpSumupDetailsForm.list" indexes="indexes"  pagination="zpSumupDetailsForm.pagination" pageCount="20" scope="session">
        <% 	RecordVo vot=(RecordVo)element;
                String txtName="txt"+vot.getString("detail_id");
                String txtValue=vot.getString("realcharge");
        %>
          var valueInputs=document.getElementsByName("<%=txtName%>");
             var dobj=valueInputs[0];
             tag=checkNUM2(dobj,10,5) &&  tag ;  
              if(tag==false)
	      {
	        dobj.focus();
	        return false;
	      }	 
     </hrms:extenditerate>     
     return tag;   
  }
</script>

<html:form action="/hire/zp_sumup/sum_up">
      <br>
      <br>
      <table width="800" border="0" cellpadding="0" cellspacing="0" align="center">
          <tr>
            <td colspan="4">
               <table width="75%" border="0" cellspacing="1"  align="center" cellpadding="1" bordercolor = "#223377"> 
                <tr>
		 	<td height="20" align="center" class="TableRow" nowrap colspan="4"><bean:message key="hire.hire.summary"/></td>
		 </tr> 
                 <tr class="trDeep1">
                     <td align="right" nowrap valign="center"><bean:message key="label.zp_job.plan"/></td>
                     <td align="left"  nowrap valign="center">
                     <hrms:importgeneraldata showColumn="name" valueColumn="plan_id" flag="true"  paraValue="05"
                  			sql="select plan_id,name from zp_plan where status=? " collection="list" scope="page"/> 
            				<html:select name="zpSumupForm" property="plan_id_value" size="1" onchange="jobplan_change();"> 
            				   <html:option value=""><bean:message key="label.select.dot"/></html:option>
            				   <html:options collection="list" property="dataValue" labelProperty="dataName"/> 
            				</html:select>

                     </td> 
                     <td align="right" nowrap valign="center"><bean:message key="label.zp_sumup.name"/></td>
                     <td align="left"  nowrap valign="center">
                     <hrms:importgeneraldata showColumn="name" valueColumn="zp_job_id" flag="true"  paraValue="zpSumupForm.plan_id_value"
                  			sql="select zp_job_id,name from zp_job where plan_id=? and status = '05'" collection="list" scope="page"/> 
            				<html:select name="zpSumupForm" property="zpSumupvo.string(zp_job_id)" size="1"  onchange="job_change();"> 
            				   <html:option value=""><bean:message key="label.select.dot"/></html:option>
            				   <html:options collection="list" property="dataValue" labelProperty="dataName"/> 
            				</html:select>
                     </td>
                 </tr>
                  <tr class="trDeep1">            
                     <td align="right" nowrap valign="center"><bean:message key="lable.zp_plan.real_invite_amount"/></td>
                     <td align="left"  nowrap valign="center">
                          <html:text name="zpSumupForm" property="zpSumupvo.string(real_invite_amount)" styleClass="text6"/>
                     </td>
                     <td align="right" nowrap valign="center"><bean:message key="label.zp_job.resource_id"/></td>
                     <td align="left"  nowrap valign="center">
                          <bean:write  name="zpSumupForm" property="resource_id_name" filter="true"/>&nbsp;
                     </td>       
                 </tr>
                 <tr class="trShallow1">
                     <td align="right" nowrap valign="top"><bean:message key="label.zp_sumup.description"/></td>
                     <td align="left"  nowrap valign="center" colspan="3">
                         <html:textarea name="zpSumupForm" property="zpSumupvo.string(description)" cols="61" rows="8" styleClass="text6"/>
                     </td>
                 </tr> 
                 <tr class="trDeep1">
                         <td align="right" nowrap valign="top"><bean:message key="label.zp_sumup.fee"/></td>
                         <td align="left"  nowrap valign="center" colspan="3">
                         <table width="100%" border="0" cellspacing="1"  align="center" cellpadding="1" class="ListTable">
                         <thead>
                         <tr>
                            <td align="center" class="TableRow" nowrap>
		                <bean:message key="label.zp_job.detailname"/>&nbsp;
                            </td>         
                            <td align="center" class="TableRow" nowrap>
		                <bean:message key="label.zp_job.charge"/>&nbsp;
	                    </td>
                            <td align="center" class="TableRow" nowrap>
		                 <bean:message key="label.zp_sumup.fee"/>&nbsp;
	                    </td>	    		        	        	        
                        </tr>
                        </thead>
                        <hrms:extenditerate id="element" name="zpSumupForm" property="zpSumupDetailsForm.list" indexes="indexes"  pagination="zpSumupDetailsForm.pagination" pageCount="20" scope="session">
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
                             <td align="left" class="RecordRow" nowrap>
                            <%
                            	RecordVo vot=(RecordVo)element;
                            	String txtName="txt"+vot.getString("detail_id");
                            	String txtValue=vot.getString("realcharge");
                            %>
                            <input type="text" name="<%=txtName%>" value="<%=txtValue%>" class="text6">
                             </td> 	        	        	        
                         </tr>
                    </hrms:extenditerate>
                  </table>
                  <table  width="75%" align="center">
		 <tr>
		    <td valign="bottom" class="tdFontcolor"><bean:message key="label.page.serial"/>
					<bean:write name="zpSumupForm" property="zpSumupDetailsForm.pagination.current" filter="true" />
					<bean:message key="label.page.sum"/>
					<bean:write name="zpSumupForm" property="zpSumupDetailsForm.pagination.count" filter="true" />
				<bean:message key="label.page.row"/>
					<bean:write name="zpSumupForm" property="zpSumupDetailsForm.pagination.pages" filter="true" />
					<bean:message key="label.page.page"/>
			</td>
	               <td  align="right" nowrap class="tdFontcolor">
		          <p align="right"><hrms:paginationlink name="zpSumupForm" property="zpSumupDetailsForm.pagination"
				nameId="zpSumupDetailsForm" propertyId="zpSumupDetailsProperty">
				</hrms:paginationlink>
			</td>
		</tr>
              </table>
                </td>
              </tr> 
          </table>     
        </td>
      </tr> 
      </table>          
     <table  width="70%" align="center">
          <tr>
            <td align="center">
         	<hrms:submit styleClass="mybutton" property="b_save" onclick="document.zpSumupForm.target='_self';validate('R','zpSumupvo.string(zp_job_id)','招聘活动','R','zpSumupvo.plan_id_value','招聘计划','RI','zpSumupvo.string(real_invite_amount)','实际招聘人数','R','zpSumupvo.string(description)','活动评估');return (document.returnValue && validates() && ifqrbc());">
            		<bean:message key="button.save"/>
	 	</hrms:submit>
	 	<html:reset styleClass="mybutton">
                    <bean:message key="button.clear"/>
	       </html:reset> 
	 	<hrms:submit styleClass="mybutton" property="b_end" onclick="return ifjsho();">
            		<bean:message key="label.zp_sumup.status"/>
	 	</hrms:submit>
	 	<hrms:submit styleClass="mybutton" property="b_endplan" onclick="return ifjsjh();">
            		<bean:message key="label.zp_sumup.endplan"/>
	 	</hrms:submit>	    
            </td>
          </tr>          
    </table>
</html:form>

