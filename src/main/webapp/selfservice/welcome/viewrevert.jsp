<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>

<script language="javascript">
   

 </script>
<html:form action="/selfservice/welcome/welcome">

<table align="center" width="95%" valign='middle'height="150">
  <tr>
     <td valign='middle'>
         <table border="1" width="100%" cellspacing="0" cellpadding="4"   height="280" align="center">
           <tr>
             <td class="TableRow" style="font-size:14px;" height=24 align="center">回复内容</td>
           </tr>
           <tr>
           
             <td align="left" valign="top" valign="middle">   
             <bean:write name="welcomeForm" property="opinion" />           
          
             </td>
           </tr>
        </table>
     </td>     
  </tr>
  <tr>
    <td align="center">     
     						      
      <input type="button" name="btnreturn" value='<bean:message key="button.close"/>' onclick="window.close();" class="mybutton">						      
    </td>
  </tr>
</table>
</html:form>