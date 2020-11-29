
<%@ page contentType="text/html; charset=UTF-8" language="java"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ page import="com.hrms.struts.valueobject.UserView"%>
<%@ page import="com.hrms.struts.constant.WebConstant"%>

<%
UserView userView = (UserView) session.getAttribute(WebConstant.userView);
%>

<script language="JavaScript" src="/js/validate.js"></script>
<script language="JavaScript" src="/js/function.js"></script>
<hrms:themes />
<link href="/css/xtree.css" rel="stylesheet" type="text/css" >
<SCRIPT LANGUAGE=javascript src="/js/xtree.js"></SCRIPT> 
<hrms:themes />
<html:form action="/performance/workdiary/workdiary"> 
<%if((userView.getA0100()==null||userView.getA0100().trim().length()<1)&&userView.getStatus()==0){%>
 <%}else{ %>
  <table width="100%" border="0" cellspacing="1"  align="center" cellpadding="1" >
     <tr>
         <td align="left"> 
                  <hrms:orgtree flag="1" rootaction="1" rootPriv="0"  dbtype="1"  action="/performance/workdiary/workdiaryshow.do?b_query=link&a0100=&frommenu=1" target="mil_body"></hrms:orgtree>
         </td>
         </tr>           
 </table>
 <%} %>

</html:form>