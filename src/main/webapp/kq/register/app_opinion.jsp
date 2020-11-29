<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<script language="javascript">
   function rule()
   {
      var fObj=document.getElementById("overrule");
      var overrule=fObj.value;
      var thevo=new Object();
      thevo.flag="true";
	  thevo.overrule=overrule;
      window.returnValue=thevo;
      window.close();
   }   
 </script>
<html:form action="/kq/register/select_collectdata">
<table align="center" width="100%" valign='middle'>
    
  <tr>
     <td valign='middle'>
         <table border="0" width="100%" cellspacing="0" cellpadding="4" style="border-collapse: collapse" height="150" align="center" class="ftable">
           <tr>
             <td class="TableRow" height=24 align="center">审核意见信息</td>
           </tr>
           <tr>
             <td align="center" valign="middle" style="padding-top:8px;padding-bottom:8px;">
              <html:textarea cols="70" rows="10" name="dailyRegisterForm"  property="overrule" styleId="overrule">              
              </html:textarea>  
             </td>
           </tr>
        </table>
     </td>     
  </tr>
  <tr>
    <td align="center" style="height:35px;">     
      <input type="button" name="btnreturn" value='<bean:message key="button.ok"/>' onclick="rule();" class="mybutton">						      
      <input type="button" name="btnreturn" value='<bean:message key="button.close"/>' onclick="window.close();" class="mybutton">						      
    </td>
  </tr>
</table>
</html:form>