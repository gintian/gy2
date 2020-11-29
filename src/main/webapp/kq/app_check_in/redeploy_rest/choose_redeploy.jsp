<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>

<script language="javascript">
  
  function valide()
{  
             
         redeployRestForm.action="/kq/app_check_in/redeploy_rest/redeploydata.do?b_change=link";
         redeployRestForm.target="mil_body";
         redeployRestForm.submit();
         window.close();
        
}
	

</script>
<html:form  action="/kq/app_check_in/redeploy_rest/redeploydata">
<br>
<fieldset align="center" style="width:50%;">
 <legend ><bean:message key="kq.approve.idea"/></legend>
  <table width="250" border="0" cellpadding="0"  cellspacing="0" align="center">
   
      <tr class="list3">	 
         <td align="center" class="TableRow" nowrap >
         <logic:equal name="redeployRestForm" property="audit_flag" value="1">
           <bean:message key="kq.register.overrule"/>
         </logic:equal>  
         <logic:equal name="redeployRestForm" property="audit_flag" value="2">
           <bean:message key="kq.registr.argue"/>
         </logic:equal>  
         </td>
         </tr> 
         <tr class="list3">
            <td>
            	<html:textarea name="redeployRestForm" property="result"   cols="50" rows="20" style="height:110px;width:100%;font-size:9pt"/>
            	<html:hidden name="redeployRestForm" property="audit_flag" styleClass="text"/>
             </td>
            </tr>
            <logic:equal name="redeployRestForm" property="audit_flag" value="1">
              <tr class="list3">
            	<td>
                <bean:message key="conlumn.board.approveoperation"/>
            	<html:radio name="redeployRestForm" property="radio" value="01"  /><bean:message key="label.agree"/>&nbsp;&nbsp;&nbsp;
            	<html:radio name="redeployRestForm" property="radio" value="02"  /><bean:message key="label.nagree"/>&nbsp;&nbsp;&nbsp;
             </td>
            </tr>  
            </logic:equal>             
   </table>        
    </fieldset>
    <br>
       <br>
    <table width="200" border="0" cellpmoding="0" cellspacing="0"  align="center"   cellpadding="0">                                                 
        <tr class="list3">
        <td align="center" colspan="2">
         <input type="button"  value="<bean:message key="button.save"/>" class="mybutton" onclick="valide()">         
         <input type="reset"  value="<bean:message key="button.clear"/>" class="mybutton">
         <input type="button"  value="<bean:message key="button.return"/>" class="mybutton" onclick="window.close();"> 
        </td>
       </tr>          
      </table>
     
</html:form>
