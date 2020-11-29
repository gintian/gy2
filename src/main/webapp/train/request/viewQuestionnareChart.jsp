<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ page import="com.hjsj.hrms.actionform.welcome.WelcomeForm,com.hjsj.hrms.actionform.askinv.EndViewForm,java.util.ArrayList,com.hjsj.hrms.utils.PubFunc"%>
<%@ page import="java.util.*"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="X-UA-Compatible" content="IE=9; IE=8;">
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<script type="text/javascript" src="/module/utils/js/template.js"></script>
<script type="text/javascript" src="/module/system/questionnaire/questionnaire_resource_zh_CN.js"></script>
<script>

var id='<%=(PubFunc.decryption(request.getParameter("id")))%>';
var ids = id.split(":");
var planId = ids[0];
var qnId = ids[1];

Ext.Loader.setConfig({
	enabled: true,
	paths: {
		'QuestionnaireAnalysis': rootPath+'/module/system/questionnaire/analysis',
	}
});

Ext.onReady(function(){
	Ext.require('QuestionnaireAnalysis.ChartAnalysis',function(){
		Ext.create("QuestionnaireAnalysis.ChartAnalysis",{qnId:qnId,planId:planId,renderTo:document.body});
	});
});
</script>
</head>
<body>

</body>
</html>