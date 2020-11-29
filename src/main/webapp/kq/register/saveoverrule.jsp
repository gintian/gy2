<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%String flag = (String)request.getParameter("flag");
  String sb = (String)request.getParameter("sb");
%>
<script language="javascript">
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
   function rule()
   {
    
      <%if(flag!=null&&flag.equals("day"))
      {%>
         dailyRegisterForm.action="/kq/register/daily_registerdata.do?b_rule=link";
         dailyRegisterForm.target="mil_body";
         dailyRegisterForm.submit();
      <%}else
      {%>
         dailyRegisterForm.action="/kq/register/select_collectdata.do?b_overrule=link";
         dailyRegisterForm.target="mil_body";
         dailyRegisterForm.submit();
      <%}%>
      window.close();
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
<html:form action="/kq/register/select_collectdata">


<table align="center" width="100%" valign='middle'>
  <tr>
     <td valign='middle'>
         <table border="0" width="100%" cellspacing="0" cellpadding="4" style="border-collapse: collapse" bgcolor="#F7FAFF" align="center" class="ftable">
           <tr>
             <td style="font-size:14px;" height=24  align="center" class="TableRow">意见信息</td>
           </tr>
           <tr>
             <td align="center" valign="middle" style="padding-top:8px;padding-bottom:8px;">
              <html:textarea cols="70" rows="10" name="dailyRegisterForm" styleId='overrule' property="overrule" value="">              
              </html:textarea>  
             </td>
           </tr>
        </table>
     </td>     
  </tr>
  <tr>
    <td align="center">     
      <input type="button" name="btnreturn" value='<bean:message key="button.save"/>' onclick="submitb();" class="mybutton">						      
      <input type="button" name="btnreturn" value='<bean:message key="button.close"/>' onclick="window.close();" class="mybutton">						      
    </td>
  </tr>
</table>
</html:form>