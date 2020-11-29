<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<% 
	String reportid = request.getParameter("reportid");
%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">

<html>
	<head>
		<title>报表</title>
		<meta http-equiv="X-UA-Compatible" content="IE=9; IE=8; IE=7">
		<meta http-equiv="content-type" content="text/html; charset=UTF-8">
	</head>
	
	<script language="JavaScript" src="../../../module/utils/js/template.js"></script>
	<script language="JavaScript" src="./DataView.js"></script>
	<link rel="stylesheet" href="../../../ext/ext6/resources/ext-theme.css" type="text/css" />
	
	<script type="text/javascript">
		Ext.onReady(function(){
			var reportid = '<%=reportid%>';
			
			if(reportid.length<1){
				alert("请检查参数！");
				return;
			}
			Ext.create("EHR.dataview.DataView",{
				reportid:reportid
			});
		});
	</script>
	<body>
	</body>
</html>


