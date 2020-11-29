<%@ page contentType="text/html; charset=UTF-8" language="java"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
				 				 
<html>
<head>
<link href="/performance/solarterms/solarterms.css" rel="stylesheet" type="text/css">
<script language="JavaScript" src="/performance/solarterms/solarterms.js"></script>
</head>

<body>
	<html:form action="/performance/solarterms/specialtask">
	 <logic:equal name="solarTermsForm" property="showType" value="0">
	 	<div style="float:left;margin-top:10px;">
	        <a href="/performance/solarterms/solarterms.do?b_search=link&amp;frompage=1&amp;showType=0"><img src="/images/select.gif" alt="返回" border=0></a>
	     </div>
	    <div class="epm-li-yue-all">
	    	
	        <div class="epm-li-two-bottom">
	            ${solarTermsForm.taskHtml}
	        </div>
	    </div>
	 </logic:equal>
	 <logic:equal name="solarTermsForm" property="showType" value="1">
	 	<div style="float:left;margin-top:10px;">
	        <a href="/performance/solarterms/solarterms.do?b_search=link&amp;frompage=1&amp;showType=1"><img src="/images/select.gif" alt="返回" border=0></a>
	    </div>
	 	<div class="epm-li-yue-all">
	 		${solarTermsForm.taskHtml}
	 	</div>
	 </logic:equal>
   </html:form>
</body>
</html>