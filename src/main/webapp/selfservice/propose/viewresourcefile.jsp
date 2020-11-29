<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>

<html:form action="/selfservice/propose/viewresourcefile">

      <table width="200" border="0" cellpadding="0" cellspacing="0" align="center"  style="margin-top:8px;">
          <tr height="20">
       		<!--  <td width=10 valign="top" class="tableft"></td>
       		<td width=130 align=center class="tabcenter">&nbsp;<bean:message key="conlumn.resource_list.descrption"/></td>
       		<td width=13 valign="top" class="tabright"></td>
       		<td valign="top" class="tabremain" width="700"></td>--> 
       		<td  align=center class="TableRow">&nbsp;<bean:message key="conlumn.resource_list.descrption"/></td>             	      
          </tr> 
          <tr>
            <td class="framestyle9">
               <br>
               <table width="650" border="0" cellpmoding="0" cellspacing="0"  class="DetailTable"  cellpadding="0" align="center">     
			<tr>
			<td>
			&nbsp;  
			   <bean:write  name="htmlFileListForm" property="htmlFileListvo.string(description)" filter="false"/>&nbsp;
			</td>
			</tr>
                 <tr>
                   <td height="10"></td>
                 </tr>
               </table>     
              </td>
          </tr>                                                      
          <tr>
            <td align="center">
              <table align=center">
              <tr>
		<td height="35">	 	
         	<hrms:submit styleClass="mybutton" property="br_return">
            		<bean:message key="button.return"/>
	 	</hrms:submit>  
	 	</td>
	 	</tr>
	 	</table>          
            </td>
          </tr>          
      </table>
</html:form>

