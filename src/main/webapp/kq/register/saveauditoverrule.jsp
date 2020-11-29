<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%
  String sb = (String)request.getParameter("sb");
%>
<script language="javascript">
   function rule()
   {
      browseRegisterForm.action="/kq/register/audit_registerdata.do?b_overrule=link";
      browseRegisterForm.target="mil_body";
      browseRegisterForm.submit();
      window.close();
   }
   function submitb()
   {
     <%if(sb!=null&&sb.equals("new"))
     {%>
       saveover();
     <%}else
     {%>
        rule();
     <%}%>
   }
   function saveover()
   {
         var o_obj=document.getElementById('overrule');
         var tx=o_obj.value;
         var thevo=new Object();
	     thevo.save="1";
		 thevo.text=tx;
		 window.returnValue=thevo;
		 window.close();  
   }
 </script>
<html:form action="/kq/register/audit_registerdata">


<table align="center" width="100%" valign='middle' height="150">
  <!-- <br>
  <br>
  <br>
  <br> -->
  <tr>
     <td valign='middle'>
         <table border="0" width="100%" cellspacing="0" cellpadding="4" style="border-collapse: collapse"  height="150" align="center" class="ftable">
           <tr>
             <td bgcolor="#f4f7f7" height=24  align="center" class="TableRow">驳回意见信息</td>
           </tr>
           <tr>
          <!--  <td align="center">colspan="2"
               意见:
             </td>-->
             <td align="center" valign="middle" style="padding-top:8px;padding-bottom:8px;">
              <html:textarea cols="70" rows="10" name="browseRegisterForm"  property="overrule">              
              </html:textarea>  
             
             </td>
           </tr>
        </table>
     </td>     
  </tr>
  <tr>
    <td align="center" style="height:35px;">
      <input type="button" name="btnreturn" value='<bean:message key="button.ok"/>' onclick="javascript:submitb();" class="mybutton">		
      
      <input type="button" name="btnreturn" value='<bean:message key="button.close"/>' onclick="window.close();" class="mybutton">						      
    </td>
  </tr>
</table>
</html:form>