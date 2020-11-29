<%@ page contentType="text/html; charset=UTF-8" language="java"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ page import="com.hrms.struts.constant.SystemConfig" %>
<%
   String tt4CssName="ttNomal4";
   String tt3CssName="ttNomal3";
   if(SystemConfig.getPropertyValue("clientName").equalsIgnoreCase("zglt"))
   {
      tt4CssName="tt4";
      tt3CssName="tt3";
   }
 %>
<html:form action="/performance/objectiveManage/myObjective/my_objective_list">

 <table width="570px" border="0" cellpadding="0" cellspacing="0" align="center" style="padding:0px 5px 0px 0px;">

	   <tr>
	   <td  >
      <table width="100%" border="0" cellpadding="0" cellspacing="0" align="center">
          <tr height="20">
       		<td  align=center class="TableRow">&nbsp;<font  class="<%=tt4CssName%>"><logic:equal value="100" property="opt" name="myObjectiveForm">退回原因</logic:equal><logic:equal value="1" property="opt" name="myObjectiveForm"><bean:message key="info.appleal.state10"/>原因</logic:equal><logic:equal value="2" property="opt" name="myObjectiveForm">填表说明</logic:equal></font>&nbsp;</td>             	      
          </tr> 
          <tr>
            <td class="framestyle9">
            
               <table  width="100%"  border="0" cellpmoding="0" cellspacing="0"  class="DetailTable"  cellpadding="0" style="padding:0px 5px 0px 0px;">   	 		 		
		 		<tr>		 			
		 			<td width="90%"  align='center' >
		 		       <font class="<%=tt3CssName%>">
		 				<html:textarea property="rejectreason" name="myObjectiveForm"  rows='19' cols='80' style="margin:2px 0 2px 0"></html:textarea>
		               </font>
		 			</td>		 		
		 		</tr>				                                                   
      </table>
</td>
</tr>
</table>
</td>
</tr>
<tr>
<td align="center" style="height:35px;">
<input type="button" class="mybutton" name="oo" value="<bean:message key="button.close"/>" onclick="closeWin();"/>
</td>
</tr>
</table>
<script>
function closeWin(){
	if(parent && parent.parent && parent.parent.Ext && parent.parent.searchReject_close){
		parent.parent.searchReject_close();
	} else {
		parent.window.close();
	}
}
</script>
</html:form>