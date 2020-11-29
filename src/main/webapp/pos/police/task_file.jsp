<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>

<SCRIPT LANGUAGE=javascript src="/js/xtree.js"></SCRIPT>

<script language="javascript">

function showtaskword()
{
	policeForm.action = "/pos/police/work_file?flag=${policeForm.flag}&type=${policeForm.type}";
	policeForm.submit();
}
 function returnTO()
  {
     window.location="/templates/attestation/police/wizard.do?br_work_wizard=link";
  }
</script>
<html:form action="/pos/police/task_file">
<br>
<br>
<br>

<logic:equal name="policeForm" property="issave" value="no">
		<h1 align="center">您没有相关文件</h1>
		<table  width="90%" align="center" >
        <tr>
       <td align="center">
         <logic:equal value="wizard" name="policeForm" property="returnvalue">
           <input type='button' name='b_save' value='返回' onclick='returnTO();' class='mybutton'>
         </logic:equal>
      
       </td>
       </tr>
     </table>
</logic:equal>
</html:form>
<logic:equal name="policeForm" property="issave" value="yes">
<script language="javascript">
showtaskword();
</script>
</logic:equal>