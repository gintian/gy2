<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>

<script language="javascript">
  
  function valide()
	{  
         appForm.action="/kq/app_check_in/choose_or.do?b_save=link";         
         appForm.submit();       
         appForm.action="/kq/app_check_in/all_app_data.do?b_search=link";
         appForm.target="mil_body";
         appForm.submit();
         window.close();
        
	}
	

</script>
<html:form  action="/kq/app_check_in/choose_or">
<br>
 <logic:equal name="appForm" property="sign" value="4"> 
   <fieldset align="center" style="width:50%;">
     <legend ><bean:message key="general.inform.org.historyorgdesc"/></legend>
       <table width="230" border="0" cellpadding="0"  cellspacing="0" align="center">
	       <tr>
		      <td>
            <font color=#ff000><bean:write name="appForm" property="message"/></font>
          </td>
        </tr>
      </table>
    </fieldset>
 </logic:equal>
<fieldset align="center" style="width:50%;">
 <legend ><bean:message key="kq.approve.idea"/></legend>
  <table width="250" border="0" cellpadding="0"  cellspacing="0" align="center">
   
      <tr class="list3">	 
         <td align="center" class="TableRow" nowrap >
         <logic:equal name="appForm" property="audit_flag" value="1">
           <bean:message key="kq.register.overrule"/>
         </logic:equal>  
         <logic:equal name="appForm" property="audit_flag" value="2">
           <bean:message key="kq.registr.argue"/>
         </logic:equal>  
         </td>
         </tr> 
         <tr class="list3">
            <td>
            	<html:textarea name="appForm" property="result"   cols="50" rows="20" style="height:110px;width:100%;font-size:9pt"/>
            	<html:hidden name="appForm" property="audit_flag" styleClass="text"/>
             </td>
            </tr>
            <logic:equal name="appForm" property="audit_flag" value="1">
              <tr class="list3">
            	<td>
                <bean:message key="conlumn.board.approveoperation"/>
            	<html:radio name="appForm" property="radio" value="01"  /><bean:message key="label.agree"/>&nbsp;&nbsp;&nbsp;
            	<html:radio name="appForm" property="radio" value="02"  /><bean:message key="label.nagree"/>&nbsp;&nbsp;&nbsp;
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