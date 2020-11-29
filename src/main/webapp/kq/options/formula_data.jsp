<%@ page contentType="text/html; charset=UTF-8" language="java"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<link rel="stylesheet" href="/css/css1.css" type="text/css"><hrms:themes /> <!-- 7.0css -->

<html:form action="/kq/options/formula_data">
<br>
<br>
  <fieldset align="center" style="width:60%;">
  	<legend ><bean:message key="kq.item.formercode"/></legend>
  <table width="150" border="0" cellpadding="0" cellspacing="0" align="center">
      	<html:hidden name="kqItemForm" property="items"/>
         <tr class="list3">
            <html:select name="kqItemForm" property="sdata_src" size="1" >
              <html:optionsCollection  property="klist" value="dataValue" label="dataName"/>
             </html:select> 
            </tr>              
        <tr class="list3">
        <td align="center" colspan="2">&nbsp;
          </td>
        </tr>                                                      
       <tr class="list3">
         <td align="center" colspan="2">
         	<hrms:submit styleClass="mybutton" property="b_ok" >
            <bean:message key="kq.formula.true"/>
	        </hrms:submit>
	      </td>
	      <td>
	       <input type="button" name="br_return" value="<bean:message key="button.return"/>" class="mybutton" onclick="history.back();">     
        </td>
       </tr>          
    </table>
  </fieldset>
</html:form>