<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<script language="JavaScript" src="/js/popcalendar.js"></script>
<script language="JavaScript" src="/js/function.js"></script>
<script language="JavaScript" src="/js/validate.js"></script>
<script language="JavaScript" src="/js/constant.js"></script>
<script language="javascript" src="/ajax/common.js"></script>
<SCRIPT Language="JavaScript">dateFormat='yyyy.mm.dd'</SCRIPT>
<%@ page import="com.hjsj.hrms.actionform.hire.employNetPortal.EmployPortalForm,
			     org.apache.commons.beanutils.LazyDynaBean,
			     java.util.*"%>
<LINK 
href="/css/hireNetStyle.css" type=text/css rel=stylesheet>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">

<html:form action="/hire/hireNetPortal/search_zp_position"> 
<html>
  <head>
  
  </head>
  
  <body>
   
   
   
   
   <TABLE cellSpacing=0 cellPadding=0 width="100%" border=0>
  <TBODY>
  <TR>
    <TD background="">

                		<hrms:ole name="employPortalForm" dbpre="${employPortalForm.dbName}" a0100="a0100" scope="session" />
      </TD></TR></TBODY></TABLE>          	
   
  </body>
</html>
</html:form>
