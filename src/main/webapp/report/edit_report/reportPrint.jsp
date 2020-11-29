<%@ page contentType="text/html; charset=UTF-8"%>

<%@ taglib uri="/tags/struts-bean" prefix="bean"%>
<%@ taglib uri="/tags/struts-html" prefix="html"%>
<%@ taglib uri="/tags/struts-logic" prefix="logic"%>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms"%>
<%@ page import="com.hrms.struts.valueobject.UserView"%>
<%@ page import="com.hrms.struts.constant.WebConstant"%>
<%@ page import="com.hjsj.hrms.businessobject.report.ReportPrint"%>
<%@ page import="java.util.*"%>
<%@ page import="java.io.*"%>
<%@ page import="com.hjsj.hrms.businessobject.report.TnameBo"%>
<%@ page import="java.sql.Connection"%>
<script>
   var win=open("/servlet/DisplayOleContent?filename="+"${reportListForm.path}","_self");
   //newwindow=window.open('${reportListForm.path}','glWin','toolbar=no,location=no,directories=no,status=no,menubar=no,scrollbars=yes,top=0,left=220,resizable=yes,width=800,height=800');
</script>