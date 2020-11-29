<%@ page contentType="text/html; charset=UTF-8"%>

<%@ taglib uri="/tags/struts-bean" prefix="bean"%>
<%@ taglib uri="/tags/struts-html" prefix="html"%>
<%@ taglib uri="/tags/struts-logic" prefix="logic"%>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms"%>
<%@ page import="com.hrms.struts.valueobject.UserView"%>
<%@ page import="com.hrms.struts.constant.WebConstant"%>
<html>
  <head>
   

  </head>
  <script language='javascript'>
	  <logic:equal name="editReportForm" property="isSubNode" value="true">
	  		window.location="/report/report_collect/reportOrgCollecttree.do?br_init=init";
	  
	  </logic:equal>
  	  <logic:equal name="editReportForm" property="isSubNode" value="false">
		  window.location="/report/report_collect/reportOrgCollecttree.do?b_lookInfo=look2&unitcode=${editReportForm.unitcode}&reportSet=-1&status=-2";
	  
	  </logic:equal>
  
  </script>
  <hrms:themes />
  <body>
  </body>
</html>
