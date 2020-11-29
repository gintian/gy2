<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<script language="javascript">
function goback()
{
	  document.optionsForm.action="/system/options/query_template.do?b_query=link";
	  document.optionsForm.submit();  
}
</script>

<html:form action="/system/options/query_template">
  <br>
  <br>  
  <table width="500" border="0" cellpadding="0" cellspacing="0" align="center" class="ftable">
          <tr height="20">
       		<td align=center class="TableRow">&nbsp;<bean:message key="label.information"/>&nbsp;</td>
          	      
          </tr> 
                    <tr>
              	      <td align="left" nowrap style="height:70px"><bean:message key="label.save.success"/>！</td>
                    </tr> 
                    <tr class="list3" >
                      <td align="center" style="height:35px">
              		<input type="button" name="btnreturn" value="返回" onclick="goback();" class="mybutton">
                      </td>
                    </tr>   

  </table>
 
</html:form>
