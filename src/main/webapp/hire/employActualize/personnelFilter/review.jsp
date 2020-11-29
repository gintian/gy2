<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<script language='javascript'>
	function save()
	{
		if(employActualizeForm.summary.value.length>120)
		{
			alert(COMMENT_IS_NOT_LONGER+"！");
			return;
		}
		employActualizeForm.action="/hire/employActualize/personnelFilter/personnelFilterTree.do?b_saveSummary=save"
		employActualizeForm.submit();
	}
	
	function goback()
	{
		employActualizeForm.action="/hire/employActualize/personnelFilter/personnelFilterTree.do?br_back=save"
		employActualizeForm.submit();
	
	}
	


</script>


<html:form action="/hire/employActualize/personnelFilter/personnelFilterTree">
<br>	
<br>
      <table width="500" border="0" cellpadding="0" cellspacing="0" align="center">
          <tr height="20">
       		<!--  <td width=10 valign="top" class="tableft"></td>
       		<td width=130 align=center class="tabcenter"><bean:message key="hire.employActualize.personnelFilter.comment"/></td>
       		<td width=10 valign="top" class="tabright"></td>
       		<td valign="top" class="tabremain" width="500"></td> -->  
       		<td align=center class="TableRow"><bean:message key="hire.employActualize.personnelFilter.comment"/></td>           	      
          </tr> 
          <tr>
            <td class="framestyle9">
               <table border="0" cellpmoding="0" cellspacing="0"  class="DetailTable"  cellpadding="0">     
                      <tr class="list3">
                	  <td align="left" nowrap >
                 	     <html:textarea name="employActualizeForm" property="summary" cols="80" rows="30"/>
                          </td>
                      </tr>
                    
                 </table>     
              </td>
          </tr>                                                  
          <tr class="list3">
            <td align="center" style="height:35px;">
       
	 	<input type="button" name="b_add53" value="<bean:message key="button.save"/>" class="mybutton" onClick="save()">  
	 	<input type="button" name="b_add53" value="<bean:message key="button.return"/>" class="mybutton" onClick="goback()">
         	      
            </td>
          </tr>          
      </table>
</html:form>
