<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>

<script language="javascript">
   
   function saveover()
   {
         if(!confirm("确定要保存该信息吗？"))
            return false;
         var o_obj=document.getElementById('overrule');
         var tx=o_obj.value;
         var thevo=new Object();
	     thevo.save="1";
		 thevo.text=tx;
		 window.returnValue=thevo;
		 window.close();  
   }
 </script>
<html:form action="/selfservice/welcome/welcome">

<table align="center" width="490">
  <tr>
     <td valign='top'>
         <table width="100%" cellspacing="0" cellpadding="0" height="230" align="center" class="ftable">
           <tr>
             <td class="TableRow" style="font-size:14px;" height=24 align="left">回复内容</td>
           </tr>
           <tr>
           
             <td align="left" valign="middle" height="230px;">              
               <textarea name="overrule" id="overrule" cols="75" style="height:225px;"></textarea>
             </td>
           </tr>
        </table>
     </td>     
  </tr>
  <tr>
    <td align="center" height="35px;">     
      <input type="button" name="btnreturn" value='<bean:message key="button.save"/>' onclick="saveover();" class="mybutton">						      
      <input type="button" name="btnreturn" value='<bean:message key="button.close"/>' onclick="window.close();" class="mybutton">						      
    </td>
  </tr>
</table>
</html:form>