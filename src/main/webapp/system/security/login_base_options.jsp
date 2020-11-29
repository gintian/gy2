<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>


<html:form action="/system/security/login_base_options">
  <table width="500" border="0" cellpadding="0" cellspacing="0" align="center" class="ftable" style="margin-top:6px;">
          <tr height="20">
       		<td align="left" class="TableRow" ><bean:message key="label.loginbase.options"/>&nbsp;</td>
          </tr> 
          <logic:iterate id="element" name="loginBaseForm"  property="dblist" indexId="index"> 
                      <tr>
                	  <td align="left" nowrap class="tdFontcolor" >
                	  	<logic:equal name="loginBaseForm" property='<%="selectedlist["+index+"]"%>' value="1">
                	      	  <input type="checkbox" name="dbArr" value="${element.dataValue}" checked="true"><bean:write name="element" property="dataName"/>
                                </logic:equal>    
                	  	<logic:notEqual name="loginBaseForm" property='<%="selectedlist["+index+"]"%>' value="1">
                	      	  <input type="checkbox" name="dbArr" value="${element.dataValue}"><bean:write name="element" property="dataName"/>
                                </logic:notEqual>                                               	      	
                          </td>
                      </tr>
           </logic:iterate>                                   
          <tr >
            <td align="center" style="height:35">
               <hrms:submit styleClass="mybutton"  property="b_save">
                    <bean:message key="button.save"/>
	       </hrms:submit>
      
               <html:reset styleClass="mybutton">
                    <bean:message key="button.clear"/>
	       </html:reset> 	
            </td>
          </tr>  
  </table>
 
</html:form>
