<%@ page contentType="text/html; charset=UTF-8" language="java"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ page import="com.hrms.struts.valueobject.UserView"%>
<%@ page import="com.hrms.struts.constant.WebConstant"%>
<script language="JavaScript" src="/js/validate.js"></script>
<script language="JavaScript" src="/js/function.js"></script>
<hrms:themes></hrms:themes>
<link href="/css/xtree.css" rel="stylesheet" type="text/css" >
<SCRIPT LANGUAGE=javascript src="/js/xtree.js"></SCRIPT> 
<html:form action="/general/approve/personinfo/orgsum"> 
 <table width="100%" border="0" cellspacing="1"  align="center" cellpadding="1" >
     <tr>
         <td align="left"> 
        	<hrms:orgtree action="/general/approve/personinfo/showstatret.do?b_query=link" target="mil_body" flag="0" showroot="false" loadtype="1" rootaction="1" nmodule="4"/>		
         </td>
         </tr>           
 </table>
</html:form>
<script LANGUAGE="javascript">
	root.openURL();
</script>