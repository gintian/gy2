<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%
double screenWidth=Double.parseDouble(request.getParameter("screenWidth")==null?"75":request.getParameter("screenWidth"));
if(screenWidth>1024)
{
        screenWidth=125;
}
else
   screenWidth=80;
 %>
<html:form action="/performance/kh_result/kh_result_reviews">
<br>	
	<br>
	   <table width="90%" border="0" cellpadding="0" cellspacing="0" align="center">
	   <tr>
	   <td align="left" width="570">
      <table width="570" border="0" cellpadding="0" cellspacing="0" align="center">
          <tr height="20">
       		<!--  <td width=10 valign="top" class="tableft"></td>
       		<td width=130 align=center class="tabcenter">&nbsp;<font style="font-weight:bold" size='2'><bean:message key="lable.performance.reviews"/></font>&nbsp;</td>
       		<td width=10 valign="top" class="tabright"></td>
       		<td valign="top" class="tabremain" width="500"></td>-->  
<td align=center class="TableRow">&nbsp;<font style="font-weight:bold" size='2'><bean:message key="lable.performance.reviews"/></font>&nbsp;</td>           	      
          </tr> 
          <tr>
            <td  class="framestyle9">
            
               <table  width="100%"  border="0" cellpmoding="0" cellspacing="0"  class="DetailTable"  cellpadding="0">   	 		 		
		 		<tr>		 			
		 			<td width="90%"  align='center' >
		 			<br>
		 			<html:textarea property="reviews" name="khResultForm"  rows='30' cols="<%=String.valueOf(screenWidth)%>" readonly="true"></html:textarea>
		 			</td>		 		
		 		</tr>				                                                   
      </table>
</td>
</tr>
</table>
</html:form>