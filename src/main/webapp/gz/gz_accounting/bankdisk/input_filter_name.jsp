<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<script type="text/javascript">
<!--
function sub(type)
{
   var name = bankDiskForm.condname.value;
   var obj = new Object();
   obj.name= name;
   if(type=='0')
      returnValue = null;
   if(type=='1')
       returnValue=obj;
   window.close();
    
}
//-->
</script>
<html:form action="/gz/gz_accountingt/bankdisk/personFilter">
<table align="center" width="335px;">
  <tr>
     <td valign='top'>
        <fieldset align="center">
        <legend><bean:message key="general.inform.search.condname" /></legend>
	   <table width="100%" border="0" cellpadding="0" cellspacing="0" align="center">
            <tr>
             <td align="center" valign="top" colspan="4" style="border-collapse: collapse">
              <br>
              <br>
              <table border="0" cellpadding="0" cellspacing="0" class="DetailTable">
              <tr>
              <td>
               <input type="text" size="30" value="" name="condname" class="inputtext"/>
               </td>
               </tr>
               </table>
                <br>
               <br>
             </td>           
           </tr>          
        </table>
        </fieldset>
     </td>     
  </tr>
  <tr>
    <td align="center"> 
    <input type="button" name="btn" value='<bean:message key="button.ok"/>' onclick="sub('1')" class="mybutton">						          
      <input type="button" name="btnreturn" value='<bean:message key="button.cancel"/>' onclick="sub('0')" class="mybutton">						      
    </td>
  </tr>
</table>
</html:form>
<script type="text/javascript">
<!--
var paraArray=dialogArguments; 
<%if(request.getParameter("rename")!=null&&request.getParameter("rename").equals("1")){%>
var desc = paraArray[0];
bankDiskForm.condname.value=desc;
<%} else{%>
 var desc = paraArray[0];
 var oper = paraArray[1];
 var value= paraArray[2];
 bankDiskForm.condname.value=desc+" "+oper+" "+value;
<%}%>
//-->
</script>
