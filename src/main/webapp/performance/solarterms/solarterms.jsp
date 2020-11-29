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
	<html:form action="/performance/solarterms/solarterms">
	<logic:equal name="solarTermsForm" property="showType" value="0">
		<div class="epm-li-yue-all">
	    	<div class="epm-li-yue-top">
	    		<div style="float:left;margin-top:10px;margin-left:5px;">
				<hrms:optioncollection name="solarTermsForm" property="yearlist" collection="list" />
						<html:select name="solarTermsForm" property="year" size="1" onchange="changeOption(0);">
							<html:options collection="list" property="dataValue" labelProperty="dataName"/>
				</html:select>
				</div>
				<div style="float:left;margin-left:10px;margin-top:10px;">
				<hrms:optioncollection name="solarTermsForm" property="departoptionslist" collection="list" />
						<html:select name="solarTermsForm" property="depart" size="1" onchange="changeOption(0);">
							<html:options collection="list" property="dataValue" labelProperty="dataName"/>
				</html:select>	
				</div>	
	        	<h2 style="float:left;margin-left:20%;">${solarTermsForm.departdesc}常规重点工作月历</h2>
	        	<div style="float:right;margin-top:10px;">
	        	<a href="/performance/solarterms/solarterms.do?b_search=link&amp;frompage=0&amp;showType=1"><img src="/images/add_del.gif" border=0></a>
	        	</div>
	        </div>
        	<div class="epm-li-yue-bottom">
        		<div class="epm-li-bottom-logo">
 					${solarTermsForm.indexHtml}
            	</div>
        	</div>
    	</div>
	</logic:equal>
	<logic:equal name="solarTermsForm" property="showType" value="1">
		<div class="epm-li-yue-all">
        <div class="epm-li-yue-three-bottom">
        	<div class="epm-li-three-bottom-logo">
        		<div style="float:left;margin-top:10px;margin-left:5px;">
				<hrms:optioncollection name="solarTermsForm" property="yearlist" collection="list" />
						<html:select name="solarTermsForm" property="year" size="1" onchange="changeOption(1);">
							<html:options collection="list" property="dataValue" labelProperty="dataName"/>
				</html:select>
				</div>
				<div style="float:left;margin-left:10px;margin-top:10px;">
				<hrms:optioncollection name="solarTermsForm" property="departoptionslist" collection="list" />
						<html:select name="solarTermsForm" property="depart" size="1" onchange="changeOption(1);">
							<html:options collection="list" property="dataValue" labelProperty="dataName"/>
				</html:select>	
				</div>	
	        	<div style="float:right;margin-top:10px;">
	        	<a href="/performance/solarterms/solarterms.do?b_search=link&amp;frompage=0&amp;showType=0"><img src="/images/add_del.gif" border=0></a>
	        	</div>
            	${solarTermsForm.indexHtml}
            </div>
        </div>
    </div>
	</logic:equal>
   </html:form>
</body>
</html>
