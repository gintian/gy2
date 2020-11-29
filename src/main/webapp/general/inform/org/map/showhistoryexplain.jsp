<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>

<html:form action="/general/inform/org/map/searchhistoryorgmaps">
 <table  width="90%"  border="0" cellpmoding="0" cellspacing="0"  class="DetailTable"  cellpadding="0" align="center" valign="middle" >   
     <tr height="20">
       		                <!-- td width=1 valign="top" class="tableft1"></td>
       		               <td width=130 align=center class="tabcenter">归档说明</td>   
       		               <td width=10 valign="top" class="tabright"></td>
       		               <td valign="top" class="tabremain" width="500"></td>    -->
       		               <td align="left" colspan="1" class="TableRow">&nbsp;归档说明&nbsp;</td>     		           	      
     </tr>                                         
     <tr>
       <td colspan="4"  class="framestyle3">
          <table border="0" width="100%" cellspacing="0" cellpadding="4"  style="border-collapse: collapse"  height="270" align="center">
           <tr>
            <td height="5" colspan="3">             
            </td> 
           </tr>
           <tr> 
            <td width="10">
             &nbsp;
            </td>          
             <td align="left" valign="top">
               <bean:write  name="orgMapForm" property="description" filter="false"/>
             </td>
             <td width="10">
              &nbsp;
            </td>   
           </tr>
        </table>          
     </td>     
  </tr>
  <tr>
    <td align="center" colspan="4" style="padding-top: 10px;">
      <input type="button" name="btnreturn" value='<bean:message key="button.close"/>' onclick="window.close();" class="mybutton">						      
    </td>
  </tr>
</table>
</html:form>