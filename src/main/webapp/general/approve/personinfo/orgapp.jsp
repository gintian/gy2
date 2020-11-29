
<%@ page contentType="text/html; charset=UTF-8" language="java"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ page import="com.hrms.struts.valueobject.UserView"%>
<%@ page import="com.hrms.struts.constant.WebConstant"%>
<%@ page import="java.util.*"%>
<%@ page import="com.hjsj.hrms.actionform.general.approve.personinfo.ApprovePersonForm"%>
<hrms:themes></hrms:themes>
<script language="JavaScript" src="/js/validate.js"></script>
<script language="JavaScript" src="/js/function.js"></script>
<link href="/css/xtree.css" rel="stylesheet" type="text/css" >
<SCRIPT LANGUAGE=javascript src="/js/xtree.js"></SCRIPT> 
<html:form action="/general/approve/personinfo/orgsum"> 
<%
	ApprovePersonForm ap = (ApprovePersonForm) session.getAttribute("approvePersonForm");		
%>
 <table width="100%" border="0" cellspacing="1"  align="center" cellpadding="1" >
     <tr>
         <td align="left"> 
            <hrms:orgtree action="/general/approve/personinfo/setre.do?b_query=link&ff=b" target="mil_body" flag="1" nmodule="4" priv="1" showroot="false" dbpre="<%=ap.getPdbflag()%>" lv="1"/>
         	<SCRIPT LANGUAGE="javascript">
         	 var currnode=Global.selectedItem;
         	 var senode=currnode.childNodes[0];
         	 senode.select();
         	</SCRIPT>
         
         </td>
         </tr>           
 </table>
</html:form>