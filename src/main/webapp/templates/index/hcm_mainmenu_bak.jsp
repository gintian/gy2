<%@ page import="com.hrms.struts.constant.SystemConfig" %>
<%	
String isturn=SystemConfig.getPropertyValue("Menutogglecollapse");
//主题皮肤
String themes = "default";
%>
<html>
    <%@ page contentType="text/html; charset=UTF-8"%>
    <%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>   
    <%@ taglib uri="/tags/struts-html" prefix="html" %>
	<head>
		<title>Simple Tasks</title>
		
		<link href="/css/hcm/themes/<%=themes %>/layout.css" rel="stylesheet" type="text/css" />
		<!--[if lte IE 6]>
		<script type="text/javascript" src="js/PNG.js"></script>
		<script>PNG.fix('.png');</script>
		<![endif]-->
		<script type="text/javascript" src="/phone-app/jquery/jquery-3.5.1.min.js">
	    	    
<style type="text/css">
</style>
</head>
<body>
<script type="text/javascript">

</script>

    <span class="shadow png"></span>
    <div class="leftbar">
        <h1 class="tit01"><a href="javascript:void(0);" class="fr"><img src="/images/hcm/themes/<%=themes %>/icon/icon8.png" class="png"/></a>员工管理</h1>
        <ul class="list01">
            <li>
                <a href="javascript:void(0);"><img src="/images/hcm/themes/<%=themes %>/icon/icon10.png"/>查询浏览</a>
                <ul>
                    <li><a href="javascript:void(0);"><img src="/images/hcm/themes/<%=themes %>/icon/icon10.png"/>信息浏览</a>
	                    <ul>
	                        <li><a href="javascript:void(0);"><img src="/images/hcm/themes/<%=themes %>/icon/icon10.png"/>历史时点</a>
		                        <ul>
		                            <li><a href="javascript:void(0);"><img src="/images/hcm/themes/<%=themes %>/icon/icon10.png"/>历史时点</a></li>
		                        </ul>
	                        </li>
	                    </ul>
                    </li>
                    <li><a href="javascript:void(0);"><img src="/images/hcm/themes/<%=themes %>/icon/icon10.png"/>快速查询</a></li>
                    <li><a href="javascript:void(0);" class="current"><img src="/images/hcm/themes/<%=themes %>/icon/icon10.png"/>常用查询</a></li>
                    <li><a href="javascript:void(0);"><img src="/images/hcm/themes/<%=themes %>/icon/icon10.png"/>简单查询</a></li>
                    <li><a href="javascript:void(0);"><img src="/images/hcm/themes/<%=themes %>/icon/icon10.png"/>通用查询</a></li>
                    <li><a href="javascript:void(0);"><img src="/images/hcm/themes/<%=themes %>/icon/icon10.png"/>复杂查询</a></li>
                    <li><a href="javascript:void(0);"><img src="/images/hcm/themes/<%=themes %>/icon/icon10.png"/>历史查询</a></li>
                </ul>
            </li>
            <li><a href="javascript:void(0);"><img src="/images/hcm/themes/<%=themes %>/icon/icon9.png"/>统计分析</a></li>
            <li><a href="javascript:void(0);"><img src="/images/hcm/themes/<%=themes %>/icon/icon9.png"/>花名册</a></li>
            <li><a href="javascript:void(0);"><img src="/images/hcm/themes/<%=themes %>/icon/icon9.png"/>信息维护</a></li>
        </ul>
    </div> 
</body>
</html>