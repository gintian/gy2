<%@ page contentType="text/html; charset=UTF-8" language="java"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<link href="/css/css1.css" rel="stylesheet" type="text/css">
	<title></title>
	<script language="JavaScript">
	function closedialog()
	{
	   returnValue=document.selfInfoForm.ordernum.value;
	   window.close();	   
	}
	</script>
<html:form action="/workbench/info/searchselfdetailinfo">
     <br>
        <table width="100%" border="0" cellpadding="0" cellspacing="0" align="center">
            <tr>
                 <td align="center"  nowrap valign="center">
            	    <bean:message key="label.serialnumber"/><input type="text" name="ordernum" size="20">
                 </td>
              </tr>  
          </table>       
     <table  width="100%" align="center">
          <tr>
            <td align="center">
               <br>
         	  <input type="button" name="b_ok" value="<bean:message key="button.ok"/>" class="mybutton" onclick="closedialog()">
	          <input type="button" name="br_return" value="<bean:message key="button.cancel"/>" class="mybutton" onclick="window.close();">
            </td>
          </tr>          
    </table>
</html:form >