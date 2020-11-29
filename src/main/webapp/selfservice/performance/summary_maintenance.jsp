<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>

<html:form action="/selfservice/performance/summary_maintenance">
<br>	
<br>
      <table width="500" border="0" cellpadding="0" cellspacing="0" align="center">
          <tr height="20">
       		<!--  <td width=10 valign="top" class="tableft"></td>
       		<td width=130 align=center class="tabcenter">&nbsp;<bean:message key="lable.performance.summary"/>&nbsp;</td>
       		<td width=10 valign="top" class="tabright"></td>
       		<td valign="top" class="tabremain" width="500"></td> -->
       		<td align=center class="TableRow">&nbsp;<bean:message key="lable.performance.summary"/>&nbsp;</td>             	      
          </tr> 
          <tr>
            <td class="framestyle9">
               <table border="0" cellpmoding="0" cellspacing="0"  class="DetailTable"  cellpadding="0">     
                      <tr class="list3">
                	  <td align="left" nowrap >
                 	     <html:textarea name="appraiseselfForm" property="summary" cols="80" rows="40"/>
                          </td>
                      </tr>
                    
                 </table>     
              </td>
          </tr>
                                                    
          <tr class="list3">
            <td align="center" style="height:35px;">
         	<hrms:submit styleClass="mybutton" property="b_save" >
            		<bean:message key="button.save"/>
	 	</hrms:submit>
         	<hrms:submit styleClass="mybutton" property="b_return">
            		<bean:message key="button.return"/>
	 	</hrms:submit>            
            </td>
          </tr>          
      </table>
</html:form>
