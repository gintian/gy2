<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>

<html:form action="/system/security/viewrole">
	<table width="500" border="0" cellpadding="0" cellspacing="0" align="center" class="ftable" style="margin-top:6px;">
          <tr height="20" >
       		<td align="left"  colspan="2" class="TableRow">角色维护&nbsp;</td>
          </tr> 
            
                      <tr class="list3">
                	      <td align="right" nowrap valign="middle"><bean:message key="column.name"/></td>
                	      <td align="left" nowrap >
                	      	<html:text name="sysForm" property="rolevo.string(role_name)" size="20" maxlength="30" styleClass="text" style="width:300px;"/>    	      
                          </td>
                      </tr>
                      <tr class="list3">
                	      <td align="right" nowrap valign="middle"><bean:message key="label.role.property"/></td>
                	      <td align="left"  nowrap>
                              	<html:select name="sysForm" property="rolevo.string(role_property)" style="width:300px;">
                 		   <html:optionsCollection property="propertylist" value="dataValue" label="dataName"/>                              	   
                             	 </html:select>
                              </td>
                      </tr> 
                                          
                      <tr class="list3">
                	      <td align="right" nowrap valign="top"><bean:message key="column.desc"/></td>
                	      <td align="left"  nowrap>
                	      	<html:textarea name="sysForm" property="rolevo.string(role_desc)" cols="50" rows="5"/>
                          </td>
                      </tr> 

          <tr class="list3">
            <td align="center" colspan="2" style="height:35">
         	<hrms:submit styleClass="mybutton" property="b_save" onclick="document.sysForm.target='_self';validate('R','rolevo.string(role_name)','名称');return (document.returnValue);">
            		<bean:message key="button.save"/>
	 	</hrms:submit>
         	<hrms:submit styleClass="mybutton" property="br_return">
            		<bean:message key="button.return"/>
	 	</hrms:submit>            
            </td>
          </tr>          
      </table>
</html:form>
