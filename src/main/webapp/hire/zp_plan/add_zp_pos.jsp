<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>

<script language="JavaScript" src="/js/validate.js"></script>
<script language="JavaScript" src="/js/function.js"></script>
<script language="javascript">
  function change_pos()
   {
    zpplanForm.action="/hire/zp_plan/add_zp_pos.do?b_org=link&pretype=UM";
    zpplanForm.submit();
   }
</script>

<%
   int i = 0;
%>

<html:form action="/hire/zp_plan/add_zp_pos">
      <br>
      <br>
      <table width="700" border="0" cellpadding="0" cellspacing="0" align="center">
          <tr>
            <td colspan="4">
               <table width="70%" border="0" cellspacing="1"  align="center" cellpadding="1" bordercolor = "#223377"> 
                <tr>
		 	<td height="20" align="center" class="TableRow" nowrap colspan="4"><bean:message key="label.zp_filter.pos"/></td>
		 </tr> 
		 <tr>
		    <td><html:hidden name="zpplanForm" property="plan_id_value" styleClass="text6"/></td>
		 </tr>
                  <tr class="trShallow1">
                      
                     <td align="right" nowrap valign="center"><bean:message key="lable.hiremanage.dept_id"/></td>
                     <logic:equal name="zpplanForm" property="managepriv" value="UM">
                     <td align="left"  nowrap valign="center">
                           <html:hidden name="zpplanForm" property="zpplanDetailsvo.string(dept_id)" styleClass="text6"/>
                           <html:hidden name="zpplanForm" property="deptparentcode"/>
                          <html:text name="zpplanForm" property="dept_pos_id_value" readonly="true" styleClass="text6" onchange="change_pos();"/>
                          <img src="/images/code.gif"/>
                      </td>
                      </logic:equal>
                      <logic:notEqual name="zpplanForm" property="managepriv" value="UM">
                     <td align="left"  nowrap valign="center">
                           <html:hidden name="zpplanForm" property="zpplanDetailsvo.string(dept_id)" styleClass="text6"/>
                           <html:hidden name="zpplanForm" property="deptparentcode"/>
                          <html:text name="zpplanForm" property="dept_pos_id_value" readonly="true" styleClass="text6" onchange="change_pos();"/>
                          <img src="/images/code.gif" onclick='javascript:openInputCodeDialogOrg_1("UM","dept_pos_id_value","zpplanDetailsvo.string(dept_id)",zpplanForm.deptparentcode.value);'/>
                      </td>
                      </logic:notEqual>
                      <td align="right"  nowrap valign="center"><bean:message key="lable.hiremanage.pos_name"/></td>
                     <td align="left"  nowrap valign="center">
                           <html:hidden name="zpplanForm" property="zpplanDetailsvo.string(pos_id)" styleClass="text6"/>
                           <html:hidden name="zpplanForm" property="posparentcode"/>
                          <html:text name="zpplanForm" property="pos_id_value" readonly="true" styleClass="text6"/>
                          <img src="/images/code.gif" onclick='javascript:openInputCodeDialogOrg_1("@K","pos_id_value","zpplanDetailsvo.string(pos_id)",zpplanForm.posparentcode.value);'/>
                      </td>
                    
                   </tr>
                   <tr class="trDeep1">
                     <td align="right" nowrap valign="center"><bean:message key="lable.hiremanage.amount"/></td>
                     <td align="left"  nowrap valign="center">
                          <html:text name="zpplanForm" property="zpplanDetailsvo.string(amount)" styleClass="text6" maxlength="8"/>
                     </td> 
                     <td align="right" nowrap valign="top"><bean:message key="lable.zp_plan_detail.status"/></td>
                     <td align="left"  nowrap valign="center">
                        <html:select name="zpplanForm" property="zpplanDetailsvo.string(status)">
                           <html:option value="0"><bean:message key="lable.zp_plan_detail.status0"/></html:option>
                           <html:option value="1"><bean:message key="lable.zp_plan_detail.status1"/></html:option>
                        </html:select>      
                      </td>  
                          
                   </tr> 
                   <tr class="trShallow1">
                        <td align="right" nowrap valign="top"><bean:message key="lable.zp_plan_detail.domain"/></td>
                     <td align="left"  nowrap valign="center" colspan="3">
                          <html:textarea name="zpplanForm" property="zpplanDetailsvo.string(domain)" cols="61" rows="7" styleClass="text6"/>
                      </td>
                   </tr> 
                   
           <tr>
              <td align="center"  nowrap colspan="4">
	 	     <hrms:submit styleClass="mybutton" property="b_save" onclick="document.zpplanForm.target='_self';validate('R','dept_pos_id_value','部门名称','R','pos_id_value','岗位名称','RI','zpplanDetailsvo.string(amount)','所需数量','R','zpplanDetailsvo.string(domain)','工作地点');return (document.returnValue && ifqrbc());"><bean:message key="button.save"/></hrms:submit>
	 	      <hrms:submit styleClass="mybutton" property="br_return"><bean:message key="button.return"/></hrms:submit>    
             </td>
         </tr>                                                        
          
          </table>     
        </td>
      </tr>               
        
 </table>
</html:form>
