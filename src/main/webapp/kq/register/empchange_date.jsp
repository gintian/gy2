<%@ page contentType="text/html; charset=UTF-8" language="java"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<link rel="stylesheet" href="/css/css1.css" type="text/css"><hrms:themes /> <!-- 7.0css -->

<html:form action="/kq/register/empchange">
<br>
<br>

<table align="center">
<tr>
  <td align="center" >
  <fieldset align="center" style="width:60%;">
  	<legend >修改时间</legend>
     <table width="150" border="0" cellpadding="0" cellspacing="0" align="center">
      	<tr class="list3">
            ${empChangeForm.workcalendar}
            </tr>              
        <tr class="list3">
        <td align="center" colspan="2">&nbsp;
          </td>
        </tr>                                                      
         <tr class="list3">
          <td align="center" colspan="2">
	    </td>
	     <td>
	     </td>
       </tr>          
    </table>
  </fieldset>
  </td>
</tr>
<tr>
  <td align="center" >
            <logic:equal name="empChangeForm" property="changestatus" value="1">
                   <hrms:submit styleClass="mybutton" property="b_ok_add" >
                      <bean:message key="kq.formula.true"/>
                   </hrms:submit>
            </logic:equal>
            <logic:equal name="empChangeForm" property="changestatus" value="0">
                   <hrms:submit styleClass="mybutton" property="b_ok_leave" >
                      <bean:message key="kq.formula.true"/>
                   </hrms:submit>
            </logic:equal>	
            <input type="button" name="br_return" value="<bean:message key="button.return"/>" class="mybutton" onclick="history.back();">     
  </td>
</tr>
</table>
 
</html:form>