<%@ page contentType="text/html; charset=UTF-8" language="java"%>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ page import="com.hrms.struts.valueobject.UserView"%>
<%@ page import="com.hrms.struts.constant.WebConstant,com.hjsj.hrms.businessobject.sys.SysParamBo"%>
<%
    UserView userView=(UserView)session.getAttribute(WebConstant.userView);
    String bosflag="";
    String themes="default";
    if(userView!=null){
    	bosflag = userView.getBosflag();
    	themes = SysParamBo.getSysParamValue("THEMES",userView.getUserName());  
    }
%>
<html>
<head>
<style  type="text/css" >
img{position:relative}
area {position:relative; display:block;}
</style>

<title></title>
<link href="/css/css1.css" rel="stylesheet" type="text/css">   
</head>
<body class="body_sec">
<%if("hcm".equals(bosflag)){ %>
<table width="80%" align="center" >
<tr>
    <td align="center"> <img src="hcm/<%=themes %>/law.jpg"  border="0" usemap="#Map"  hidefocus="true"> </td>
</tr>
</table>
<map name="Map">
        <area shape="poly" coords="291,360,340,318,323,295,315,265,316,235,327,210,338,191,292,146,266,180,250,213,247,244,251,280,262,314,273,337" href="/selfservice/lawbase/lawtext/law_maintenance0.do?b_init=link&basetype=1&returnvalue=dxt" alt="制度浏览" />
        <area shape="poly" coords="294,362,341,320,364,335,395,346,436,339,468,318,511,361,476,388,439,404,382,405,327,389" href="/selfservice/lawbase/law_maintenance0.do?b_init=link&basetype=5&returnvalue=dxt" alt="文档维护" />
        <area shape="poly" coords="472,316,515,359,543,318,560,256,556,218,540,180,515,144,470,193,495,235,491,281" href="/selfservice/lawbase/lawtext/law_maintenance0.do?b_init=link&basetype=5&returnvalue=dxt" alt="文档浏览" />
        <area shape="poly" coords="511,144,469,189,427,166,381,166,344,188,295,144,336,115,388,96,449,104,489,123" href="/selfservice/lawbase/law_maintenance0.do?b_init=link&basetype=1&returnvalue=dxt" alt="制度维护" />
</map>
<%} %>
</body>
</html>
