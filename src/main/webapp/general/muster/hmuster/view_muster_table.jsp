<%@ page contentType="text/html; charset=UTF-8"%>

<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<hrms:themes></hrms:themes>
<link rel="stylesheet" href="/css/css1_brokenline.css" type="text/css">
<style>

.ListTable_self {
    BACKGROUND-COLOR: #F7FAFF;
    BORDER-BOTTOM: medium none; 
    BORDER-COLLAPSE: collapse; 
    BORDER-LEFT: medium none; 
    BORDER-RIGHT: medium none; 
    BORDER-TOP: medium none;    
}
</style>
<html:form action="/general/muster/hmuster/select_muster_name" >
 <table>
     <tr>
       <td align="center">      
<!--<div  style="position:relative;width:${hmusterForm.divWidth};height:${hmusterForm.divHeight};background-color:#FFFFFF;border:4px solid #878886;border-top-width:1px;border-left-width:1px;border-left-style:solid;border-top-style:solid;margin-right: auto; margin-left: auto;">
-->
<table border="0" style="position:absolute;top:0;left:0;" cellspacing="0" align="left" valign="top" cellpadding="0">
<tr>
<td align="center" valign="top" width="100%">
${hmusterForm.tableTitleTop}
${hmusterForm.tableHeader}
${hmusterForm.tableBody}
${hmusterForm.tableTitleBottom}
</td>
</tr >
</table >
<!--  
</div>
-->
</td>
</tr>
</table>
</html:form>