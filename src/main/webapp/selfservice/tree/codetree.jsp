<%@ page contentType="text/html; charset=UTF-8" language="java"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>

<SCRIPT LANGUAGE=javascript src="/js/xtree.js"></SCRIPT>
<script language="javascript">
function paste(text,codesetid,fielname)
{	
   parent.selfInfoForm.fieldvalue[fielname].value=text;
   parent.selfInfoForm.fieldcode[fielname].value=codesetid;
   parent.innerframe.style.display='none'; 
}
  </script>
<html:form action="/common/tree/loadtree">
<table>
 <tr>
  <td id="treemenu">   
      <SCRIPT LANGUAGE=javascript>    
        <bean:write name="treeForm" property="treeCode" filter="false"/>
      </SCRIPT>    
  </td>
 </tr>   
</table>
</html:form>
