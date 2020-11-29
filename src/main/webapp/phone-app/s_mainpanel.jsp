<%@ page contentType="text/html; charset=UTF-8"%>
<%@ page language="java" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>移动助手</title>

	 <link rel="stylesheet" href="../phone-app/jquery/css/jquery.mobile-1.0a2.min.css" type="text/css">
	 <script type="text/javascript" src="../phone-app/jquery/jquery-3.5.1.min.js"></script>
	 <script type="text/javascript" src="../phone-app/jquery/jquery.mobile-1.0a2.min.js"></script>	
  	 <style>
		.ui-icon-myicon {background: url(/phone-app/images/myicon.png) center top no-repeat !important;}	 
	 </style>
</head>
<body>
<html:form action="/phone-app/s_mainpanel">
<div data-role="page" data-fullscreen="true" id="mainbar">	
	<div data-role="header" data-position="fixed" data-position="inline">
		<a href="/phone-app/mainpanel.do?br_query=link" data-role="button" data-icon="forward" data-rel="dialog" data-transition="pop" rel="external">返回</a>
		<h1>${sphoneForm.name}</h1>
	</div>	
	<div data-role="content" style="margin-top: 40px">
		    <ul data-role="listview" data-inset="true">
		        <hrms:extmenu moduleid="${sphoneForm.moduleid}" mobile_app="true"/>	                
            </ul>
	</div>
</div>
</html:form>
</body>
</html>