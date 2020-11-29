<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ page import="com.hrms.hjsj.sys.ResourceFactory" %>
<script language="JavaScript" src="/js/validate.js"></script>
<script language="JavaScript" src="/js/function.js"></script>
<script language="javascript">
  function change_pos()
   {
    resourcePlanForm.action="/hire/resource_plan/add_plan_pos.do?b_org=link&pretype=UM";
    resourcePlanForm.submit();
   }
</script>

<%
   int i = 0;
%>

<html:form action="/hire/resource_plan/add_plan_pos" onsubmit="return validate()">
      <br>
      <br>
      <table width="700" border="0" cellpadding="0" cellspacing="0" align="center">
          <tr>
            <td colspan="4">
               <table width="70%" border="0" cellspacing="1"  align="center" cellpadding="1" bordercolor = "#223377"> 
                <tr>
		 	<td height="20" align="center" class="TableRow" nowrap colspan="4"><bean:message key="hire.plan.position"/></td>
		 </tr> 
		 <tr>
		    <td><html:hidden name="resourcePlanForm" property="plan_id_value" styleClass="text6"/></td>
		 </tr>
                  <tr class="trShallow1">
                      
                     <td align="right" nowrap valign="center"><bean:message key="lable.resource_plan.dept_id"/></td>
                     <logic:equal name="resourcePlanForm" property="managepriv" value="UM">
                     <td align="left"  nowrap valign="center">
                          <html:hidden name="resourcePlanForm" property="zpplanDetailsvo.string(dept_id)" styleClass="text6"/>
                          <html:hidden name="resourcePlanForm" property="deptparentcode"/>
                          <html:text name="resourcePlanForm" property="dept_id_value" styleClass="text6" onchange="change_pos();"/>
                          <img src="/images/code.gif"/>
                      </td>
                      </logic:equal>
                      <logic:notEqual name="resourcePlanForm" property="managepriv" value="UM">
                     <td align="left"  nowrap valign="center">
                          <html:hidden name="resourcePlanForm" property="zpplanDetailsvo.string(dept_id)" styleClass="text6"/>
                          <html:hidden name="resourcePlanForm" property="deptparentcode"/>
                          <html:text name="resourcePlanForm" property="dept_id_value" styleClass="text6" onchange="change_pos();"/>
                          <img src="/images/code.gif" onclick='javascript:openInputCodeDialogOrg_1("UM","dept_id_value","zpplanDetailsvo.string(dept_id)",resourcePlanForm.deptparentcode.value);'/>
                      </td>
                      </logic:notEqual>
                      <td align="right"  nowrap valign="center"><bean:message key="lable.hiremanage.pos_name"/></td>
                     <td align="left"  nowrap valign="center">
                           <html:hidden name="resourcePlanForm" property="zpplanDetailsvo.string(pos_id)" styleClass="text6"/>
                          <html:hidden name="resourcePlanForm" property="posparentcode"/>
                          <html:text name="resourcePlanForm" property="pos_id_value" styleClass="text6"/>
                          <img src="/images/code.gif" onclick='javascript:openInputCodeDialogOrg_1("@K","pos_id_value","zpplanDetailsvo.string(pos_id)",resourcePlanForm.posparentcode.value);'/>
                      </td>
                    
                   </tr>
                   <tr class="trDeep1">
                     <td align="right" nowrap valign="center"><bean:message key="lable.hiremanage.amount"/></td>
                     <td align="left"  nowrap valign="center">
                          <html:text name="resourcePlanForm" property="zpplanDetailsvo.string(amount)" styleClass="text6"/>
                     </td> 
                     <td align="right" nowrap valign="top"><bean:message key="lable.resource_plan.type"/></td>
                     <td align="left"  nowrap valign="center" colspan="3">
                        <html:select name="resourcePlanForm" property="zpplanDetailsvo.string(type)">
                           <html:option value="01"><bean:message key="lable.resource_plan.type1"/></html:option>
                           <html:option value="02"><bean:message key="lable.resource_plan.type2"/></html:option>
                        </html:select> 
                      </td>  
                          
                   </tr> 
                   <tr class="trShallow1">
                        <td align="right" nowrap valign="top"><bean:message key="lable.hiremanage.reason"/></td>
                     <td align="left"  nowrap valign="center" colspan="3">
                          <html:textarea name="resourcePlanForm" property="zpplanDetailsvo.string(reason)" cols="60" rows="10" styleClass="text6"/>
                      </td>
                   </tr> 
                   
           <tr>
              <td align="center"  nowrap colspan="4">
	 	     <hrms:submit styleClass="mybutton" property="b_save" onclick="document.resourcePlanForm.target='_self';validate('R','dept_id_value','<%=ResourceFactory.getProperty("lable.resource_plan.dept_id")%>','R','pos_id_value','<%=ResourceFactory.getProperty("lable.hiremanage.pos_name")%>','RI','zpplanDetailsvo.string(amount)','<%=ResourceFactory.getProperty("lable.hiremanage.amount")%>','R','zpplanDetailsvo.string(reason)','<%=ResourceFactory.getProperty("lable.hiremanage.reason")%>');return (document.returnValue && ifqrbc());"><bean:message key="button.save"/></hrms:submit>
	 	      <hrms:submit styleClass="mybutton" property="br_return"><bean:message key="button.return"/></hrms:submit>    
             </td>
         </tr>                                                        
          
          </table>     
        </td>
      </tr>               
        
 </table>
</html:form>
