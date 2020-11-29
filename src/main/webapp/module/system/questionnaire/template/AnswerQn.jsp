<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>

<!DOCTYPE HTML>
<html>
	<head>
		<title>问卷答题</title>

		<meta http-equiv="keywords" content="keyword1,keyword2,keyword3">
		<meta http-equiv="description" content="this is my page">
		<meta http-equiv="X-UA-Compatible" content="IE=9; IE=8; IE=7">
		<!-- <meta http-equiv="content-type" content="text/html; charset=UTF-8"> -->

	</head>
	<!--<link rel="stylesheet" type="text/css" href="./styles.css">-->
	<script language="JavaScript" src="../../../../module/utils/js/template.js"></script>
	<!--  <script language="javascript" src="http://pv.sohu.com/cityjson"></script>-->
	<script language='JavaScript' src='../../../../module/system/questionnaire/GlobalVariable.js'></script>
	<script src="../../../../components/querybox/QueryBox.js"></script>
	<script language='JavaScript'
		src='../../../../components/tableFactory/tableFactory.js'></script>
	<script type="text/javascript" src='../../../../ext/ext6/charts.js'></script>
	<script type="text/javascript" src='../../../../module/system/questionnaire/template/BrowserInfo.js'></script>
	<script type='text/javascript'
		src='../../../../module/utils/js/resource_zh_CN.js'></script>
	<script type='text/javascript'
		src='../../../../module/system/questionnaire/questionnaire_resource_zh_CN.js'></script>
	<link rel="stylesheet"
		href="../../../../ext/ext6/resources/ext-theme.css" type="text/css" />
	<script>
	<%
		String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {  
            if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {  
                ip = request.getHeader("Proxy-Client-IP");
            }  
            if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {  
                ip = request.getHeader("WL-Proxy-Client-IP");
            }  
            if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {  
                ip = request.getHeader("HTTP_CLIENT_IP"); 
            }  
            if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {  
                ip = request.getHeader("HTTP_X_FORWARDED_FOR");
            }  
            if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {  
                ip = request.getRemoteAddr(); 
            }  
        } else if (ip.length() > 15) {  
            String[] ips = ip.split(",");  
            for (int index = 0; index < ips.length; index++) {  
                String strIp = (String) ips[index];  
                if (!("unknown".equalsIgnoreCase(strIp))) {  
                    ip = strIp;  
                    break;  
                }  
            }  
        }  
        ip =ip.equals("0:0:0:0:0:0:0:1")?"127.0.0.1":ip;
        
        StringBuffer paramStr = new StringBuffer("{");
        //xus 20/5/16 【60678 】VFS+UTF-8+达梦：问卷调查/收集配置，勾选不登录不允许答题，复制问卷链接，去另一个没有用户登录系统的浏览器，进入链接后，输入账号密码，提示网页不存在
        Iterator ite = request.getParameterMap().keySet().iterator();
        while(ite.hasNext()){
        	   String key = ite.next().toString();
        	   String value = request.getParameter(key);
           paramStr.append(key).append(":'").append(value).append("',");
        }
        paramStr.deleteCharAt(paramStr.length()-1);
        paramStr.append("}");
        
	%>
	if((navigator.platform.indexOf("Win")!=0&&navigator.platform.indexOf("Mac")!=0)&&browser.versions.mobile){
		var href = window.location.href;
		href=href.replace(/template/,"mobile");
		href=href.replace(/AnswerQn/,"index");
		href=href.replace(/suerveyid/,"planid");
		window.location.href=''+href;
	}else{
	Ext.Loader.setConfig({
	enabled: true,
	paths: {
		'QuestionnairePlan': '../../../../module/system/questionnaire/plan',
		'QuestionnaireTemplate': '../../../../module/system/questionnaire/template',
		'QuestionnaireRecovery': '../../../../module/system/questionnaire/recovery',
		'QuestionnaireAnalysis': '../../../../module/system/questionnaire/analysis',
		'EHR.extWidget.field':'../../../../components/extWidget/field',
		'SYSF':'../../../../components/fileupload'
	}
});
var cip = "<%=ip%>";//returnCitySN["cip"];
//var suerveyid = "${param.suerveyid}";
Ext.onReady(function(){
	
	var configObj = <%=paramStr%>;
	configObj.cip = cip;
	Ext.require("QuestionnaireTemplate.PreviewTemplate",function(){
		  var re = Ext.create("QuestionnaireTemplate.PreviewTemplate",configObj/*{suerveyid:suerveyid,cip:cip}*/);
		  var s = Ext.widget('viewport',{
		     layout:'fit',
		        items:{
		           xtype:'container',
		           autoScroll:true,
		           layout: {
			           	type : 'vbox',
						align : 'center'
	               },
	               items:re
		        }
		  });
		 
	});
});
}
</script>
	<body>
	</body>
</html>
