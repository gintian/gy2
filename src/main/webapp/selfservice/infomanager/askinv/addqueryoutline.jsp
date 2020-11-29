<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ page import="java.util.*"%>


<html:form action="/selfservice/infomanager/askinv/addoutline">
      <table width="500" border="0" cellpadding="0" cellspacing="0" align="center">
          <tr height="20">
       		<!--  <td width=10 valign="top" class="tableft"></td>
       		<td width=130 align=center class="tabcenter">&nbsp;<bean:message key="lable.investigate_point.repair"/>&nbsp;</td>
       		<td width=10 valign="top" class="tabright"></td>
       		<td valign="top" class="tabremain" width="500"></td>-->   
       		<td align=center class="TableRow">&nbsp;<bean:message key="lable.investigate_point.repair"/>&nbsp;</td>           	      
          </tr> 
          <tr>
            <td class="framestyle9">
               <table border="0" cellpmoding="0" cellspacing="0"  class="DetailTable"  cellpadding="0"> 
                     <tr class="list3"><td align="right" nowrap valign="top"><bean:message key="conlumn.investigate_item.name"/>:</td>
                     <td>
                      <bean:write name="outlineForm" property="itemName" filter="true"/>&nbsp;
                     
                     </td> </tr>   
                      <tr class="list3">
                	      <td align="right" nowrap valign="top"> <bean:message key="conlumn.investigate_point.name"/>:</td>
                	      <td align="left"  nowrap>
                	      	<html:text name="outlineForm" property="outlinevo.string(name)"/>
                          </td>
                      </tr> 
                       <tr>
                       <td align="right" nowrap valign="top"> <bean:message key="conlumn.investigate_point.status"/>:</td>
                      <td align="left"  nowrap>
                      <html:radio name="outlineForm" property="outlinevo.string(status)" value="1"/><bean:message key="datestyle.yes"/>
                      <html:radio name="outlineForm" property="outlinevo.string(status)" value="0"/><bean:message key="datesytle.no"/>   
                     </td>
                      </tr>
                      
                 </table>     
              </td>
          </tr>
                                                                      
          <tr class="list3">
            <td align="center" style="height:35px;">
               <table><tr><td>
         	<hrms:submit styleClass="mybutton" property="b_save" onclick="document.outlineForm.target='_self';validate( 'R','outlinevo.string(name)','要点名称');return (document.returnValue && ifqrbc());">
            		<bean:message key="button.save"/>
	 	</hrms:submit>
	 	</td>
	 	<td>
		<html:reset styleClass="mybutton" property="reset"><bean:message key="button.clear"/></html:reset>
		</td>
		<td>	 	
         	<hrms:submit styleClass="mybutton" property="b_return">
            		<bean:message key="button.return"/>
	 	</hrms:submit>  
	 	</td>
	 	</tr>
	 	</table>          
            </td>
          </tr>          
      </table>
</html:form>
