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
    zpreleasePosForm.action="/hire/zp_release_pos/add_zp_pos.do?b_org=link&pretype=UM";
    zpreleasePosForm.submit();
   }
</script>

<%
   int i = 0;
%>

<html:form action="/hire/zp_release_pos/add_zp_pos">
      <br>
      <br>
      <table width="700" border="0" cellpadding="0" cellspacing="0" align="center">
          <tr>
            <td colspan="4">
               <table width="70%" border="0" cellspacing="1"  align="center" cellpadding="1" bordercolor = "#223377"> 
                <tr>
		 	<td height="20" align="center" class="TableRow" nowrap colspan="4"><bean:message key="label.zp_filter.pos"/></td>
		 </tr> 
                  <tr class="trShallow1">
                      
                     <td align="right" nowrap valign="center"><bean:message key="lable.hiremanage.dept_id"/></td>
                     <td align="left"  nowrap valign="center">
                           <html:hidden name="zpreleasePosForm" property="zpreleasePosvo.string(dept_id)" styleClass="text6"/>
                          <html:hidden name="zpreleasePosForm" property="deptparentcode"/>
                          <html:text name="zpreleasePosForm" property="dept_id_value" styleClass="text6" onchange="change_pos();"/>
                          <img src="/images/code.gif" onclick='javascript:openInputCodeDialogOrg_1("UM","dept_id_value","zpreleasePosvo.string(dept_id)",zpreleasePosForm.deptparentcode.value);'/>
                      </td>
                      <td align="right"  nowrap valign="center"><bean:message key="lable.hiremanage.pos_name"/></td>
                     <td align="left"  nowrap valign="center">
                           <html:hidden name="zpreleasePosForm" property="zpreleasePosvo.string(pos_id)" styleClass="text6"/>
                          <html:hidden name="zpreleasePosForm" property="posparentcode"/>
                          <html:text name="zpreleasePosForm" property="pos_id_value" styleClass="text6"/>
                          <img src="/images/code.gif" onclick='javascript:openInputCodeDialogOrg_1("@K","pos_id_value","zpreleasePosvo.string(pos_id)",zpreleasePosForm.posparentcode.value);'/>
                      </td>
                    
                   </tr>
                   <tr class="trDeep1">
                     <td align="right" nowrap valign="center"><bean:message key="lable.hiremanage.amount"/></td>
                     <td align="left"  nowrap valign="center">
                          <html:text name="zpreleasePosForm" property="zpreleasePosvo.string(amount)" styleClass="text6"/>
                     </td> 
                     <td align="right" nowrap valign="top"><bean:message key="lable.zp_plan_detail.status"/></td>
                     <td align="left"  nowrap valign="center">
                          <html:select name="zpreleasePosForm" property="zpreleasePosvo.string(status)">
                           <html:option value="0"><bean:message key="lable.zp_plan_detail.status0"/></html:option>
                           <html:option value="1"><bean:message key="lable.zp_plan_detail.status1"/></html:option>
                        </html:select> 
                      </td>  
                          
                   </tr> 
                   <tr class="trShallow1">
                        <td align="right" nowrap valign="top"><bean:message key="lable.zp_plan_detail.domain"/></td>
                     <td align="left"  nowrap valign="center" colspan="3">
                          <html:textarea name="zpreleasePosForm" property="zpreleasePosvo.string(domain)" cols="61" rows="7" styleClass="text6"/>
                      </td>
                   </tr> 
                   
           <tr>
              <td align="center"  nowrap colspan="4">
	 	     <hrms:submit styleClass="mybutton" property="b_save" onclick="document.zpreleasePosForm.target='_self';validate('R','dept_id_value','部门名称','R','pos_id_value','岗位名称','RI','zpreleasePosvo.string(amount)','所需数量','R','zpreleasePosvo.string(domain)','工作地点');return (document.returnValue && ifqrbc());"><bean:message key="button.save"/></hrms:submit>
	 	      <hrms:submit styleClass="mybutton" property="br_return"><bean:message key="button.return"/></hrms:submit>    
             </td>
         </tr>                                                        
          
          </table>     
        </td>
      </tr>               
        
 </table>
</html:form>
