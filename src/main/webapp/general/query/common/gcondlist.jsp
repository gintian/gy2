<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>

<base id="mybase" target="_self">
<html:form action="/general/query/common/gcondlist">
  <br>
  <br>
  <br>  
  <table width="500" border="0" cellpadding="0" cellspacing="0" align="center">
          <tr height="20">
       		<!--  <td width=10 valign="top" class="tableft"></td>
       		<td width=130 align=center class="tabcenter"><bean:message key="label.query.gcond"/></td>
       		<td width=10 valign="top" class="tabright"></td>
       		<td valign="top" class="tabremain" width="500"></td>  --> 
       		<td align=center class="TableRow"><bean:message key="label.query.gcond"/></td>           	      
          </tr> 
          <tr>
            <td class="framestyle9">
               <br>
               <table border="0" cellpmoding="0" cellspacing="0"  class="DetailTable"  cellpadding="0" >    
                      <tr>
                	      <td align="left" nowrap class="tdFontcolor">
				<bean:message key="column.name"/>:
				<html:text   name="commonQueryForm" property="condname" maxlength="20" style="width:210px;"/>             	      
                              </td>
                      </tr>                    
                      <tr><td height="10"></td></tr>                      
                      <tr>    
                          <td align="left" class="tdFontcolor" nowrap >    
                            <html:select name="commonQueryForm" property="keyid" size="1" multiple="false"  style="height:209px;width:250px;">
                              <html:optionsCollection property="condlist" value="id" label="name"/>
                            </html:select>                            
                          </td>                             
                       </tr>                            

                       <tr>
                       <td height="10" ></td>
                       
                       </tr>              
	       </table>	            	
            </td>
          </tr>           
          <tr class="list3">
            <td align="center" style="height:35px;">
            
               <hrms:submit styleClass="mybutton"  property="b_save" onclick="validate('R','condname','条件名称');return document.returnValue;">
                    <bean:message key="button.save"/>
	       </hrms:submit>
               <hrms:submit styleClass="mybutton"  property="br_return">
                    <bean:message key="button.return"/>
	       </hrms:submit>	
	
            </td>
          </tr>  
  </table>
 
</html:form>
