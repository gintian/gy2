<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>

<script language="JavaScript" src="/js/validate.js"></script>
<script language="JavaScript" src="/js/function.js"></script>

<%
   int i = 0;
%>

<html:form action="/hire/staffreq/add_pos">
      <br>
      <br>
      <table width="700" border="0" cellpadding="0" cellspacing="0" align="center">
          <tr>
            <td colspan="4">
               <table width="70%" border="0" cellspacing="1"  align="center" cellpadding="1" bordercolor = "#223377"> 
                <tr>
		 	<td height="20" align="center" class="TableRow" nowrap colspan="4"><bean:messge key="hire.new.position"/></td>
		 </tr> 
		 <tr>
		      <td><html:hidden name="hireManageForm" property="gather_id_value" styleClass="text6"/></td> 
		 </tr>
                  <tr class="trShallow1">
                    
                     <td align="right" nowrap valign="center"><bean:message key="lable.hiremanage.pos_name"/></td>
                     <td align="left"  nowrap valign="center">
                       <logic:equal name="hireManageForm" property="flag_pos" value="1">
                           <html:hidden name="hireManageForm" property="gatherPosvo.string(pos_id)" styleClass="text6"/>
                           <html:hidden name="hireManageForm" property="posparentcode"/>
                          <html:text name="hireManageForm" property="pos_id_value" readonly="true" styleClass="text6"/>
                          <img src="/images/code.gif" onclick='javascript:openInputCodeDialogOrg_1("@K","pos_id_value","gatherPosvo.string(pos_id)",hireManageForm.posparentcode.value);'/>
                       </logic:equal>
                       <logic:notEqual name="hireManageForm" property="flag_pos" value="1">
                          <html:text name="hireManageForm" property="pos_id_value" disabled = "true" styleClass="text6"/>
                       </logic:notEqual>
                      </td>
                      <td align="right"  nowrap valign="center"><bean:message key="lable.hiremanage.subamount"/></td>
                     <td align="left"  nowrap valign="center">
                          <html:text name="hireManageForm" property="gatherPosvo.string(amount)" styleClass="text6"/>
                      </td>
                    
                   </tr>
                   <tr class="trDeep1">
                   
                     <td align="right" nowrap valign="top"><bean:message key="lable.hiremanage.gather_type"/></td>
                     <td align="left"  nowrap valign="center" colspan="3">
                        <html:select name="hireManageForm" property="gatherPosvo.string(type)">
                           <html:option value="01"><bean:message key="lable.resource_plan.type1"/></html:option>
                           <html:option value="02"><bean:message key="lable.resource_plan.type2"/></html:option>
                        </html:select>   
                      </td>  
                          
                   </tr> 
                   <tr class="trShallow1">
                        <td align="right" nowrap valign="top"><bean:message key="lable.hiremanage.reason"/></td>
                     <td align="left"  nowrap valign="center" colspan="3">
                          <html:textarea name="hireManageForm" property="gatherPosvo.string(reason)" cols="60" rows="5" styleClass="text6"/>
                      </td>
                   </tr> 
                   
           <tr>
              <td align="center"  nowrap colspan="4">
	 	     <hrms:submit styleClass="mybutton" property="b_save" onclick="document.hireManageForm.target='_self';validate('R','pos_id_value','岗位名称','RI','gatherPosvo.string(amount)','数量','R','gatherPosvo.string(reason)','理由');return (document.returnValue && ifqrbc());"><bean:message key="button.save"/></hrms:submit>
	 	      <hrms:submit styleClass="mybutton" property="br_return"><bean:message key="button.return"/></hrms:submit>    
             </td>
         </tr>                                                        
          
          </table>     
        </td>
      </tr>               
        
 </table>
</html:form>
