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
<title></title>
   <link href="/css/css1.css" rel="stylesheet" type="text/css">
</head>
<body class="body_sec">
<%if("hcm".equals(bosflag)){ %>
<table width="80%" align="center" >
<tr>
    <td align="center"> <img src="hcm/<%=themes %>/retain.jpg"  border="0" usemap="#Map"> 
      <map name="Map">
                <hrms:priv func_id="31011"> 
        <area shape="rect" coords="46,40,156,74" href="/hire/demandPlan/positionDemand/positionDemandTree.do?returnflag=dxt&br_query1=query" alt="需求报批">
        </hrms:priv> <hrms:priv func_id="31012"> 
        <area shape="rect" coords="46,127,156,162" href="/hire/demandPlan/positionDemand/positionDemandTree.do?returnflag=dxt&br_query2=query" alt="需求审批">
        </hrms:priv> <hrms:priv func_id="31014"> 
        <area shape="rect" coords="46,214,156,247" href="/hire/demandPlan/positionDemand/positionDemandTree.do?returnflag=dxt&br_query3=query" alt="审核查询">
        </hrms:priv> <hrms:priv func_id="31031"> 
        <area shape="rect" coords="46,347,154,379" href="/hire/interviewEvaluating/interviewExamine.do?returnflag=dxt&br_query=link" alt="面试考核">
        </hrms:priv> <hrms:priv func_id="31032"> 
        <area shape="rect" coords="246,346,355,378" href="/hire/interviewEvaluating/interviewAnnounce.do?returnflag=dxt&br_query=link" alt="面试通知">
        </hrms:priv> <hrms:priv func_id="31031"> 
        <area shape="rect" coords="455,348,564,380" href="/hire/interviewEvaluating/interviewArrange.do?returnflag=dxt&br_query=link" alt="面试安排">
        </hrms:priv> <hrms:priv func_id="31024"> 
        <area shape="rect" coords="454,241,565,272" href="/hire/employActualize/employResume.do?returnflag=dxt&b_query=link&z0301=-1&personType=1&operate=init" alt="人才库">
        </hrms:priv> <hrms:priv func_id="3102"> 
        <area shape="rect" coords="454,184,563,217" href="/hire/employActualize/employResume.do?returnflag=dxt&b_query=link&z0301=-1&personType=4&operate=init" alt="我的收藏夹">
        </hrms:priv> <hrms:priv func_id="31023"> 
        <area shape="rect" coords="455,127,564,158" href="/hire/employActualize/employResume.do?returnflag=dxt&b_query=link&z0301=-1&personType=0&operate=init" alt="应聘简历">
        </hrms:priv> <hrms:priv func_id="31022"> 
        <area shape="rect" coords="455,69,564,104" href="/hire/employActualize/employPosition.do?returnflag=dxt&b_query=link&operate=init" alt="招聘岗位">
        </hrms:priv> <hrms:priv func_id="31013"> 
        <area shape="rect" coords="246,128,355,160" href="/hire/demandPlan/engagePlan.do?returnflag=dxt&b_query=query" alt="招聘计划">
        </hrms:priv> <hrms:priv func_id="31061"> 
        <area shape="rect" coords="662,69,772,101" href="/hire/zp_options/stat/showstatestat.do?returnflag=dxt&b_query=link" alt="按岗位">
        </hrms:priv> <hrms:priv func_id="31062"> 
        <area shape="rect" coords="662,127,771,159" href="/hire/zp_options/stat/statestat/showstateresult.do?returnflag=dxt&b_query=link&init=1" alt="按简历状态">
        </hrms:priv> <hrms:priv func_id="31063"> 
        <area shape="rect" coords="661,187,772,220" href="/hire/zp_options/stat/itemstat/showstatresult.do?returnflag=dxt&b_query=link&init=1&pos=menu" alt="按类别">
        </hrms:priv> <hrms:priv func_id="31041"> 
        <area shape="rect" coords="46,433,154,465" href="/hire/employSummarise/personnelEmploy.do?returnflag=dxt&br_query=link" alt="员工录用">
        </hrms:priv> <hrms:priv func_id="31042"> 
        <area shape="rect" coords="246,432,355,465" href="/hire/employSummarise/hireSummarise.do?returnflag=dxt&b_query=link&operate=init" alt="招聘总结">
    </hrms:priv> </map></td>
</tr>
</table>
<%}else{ %>
<table width="80%" align="center" >
<tr>
    <td align="center"> <img src="retain.gif"  border="0" usemap="#Map"> 
      <map name="Map">
        <hrms:priv func_id="31011"> 
        <area shape="rect" coords="21,25,97,57" href="/hire/demandPlan/positionDemand/positionDemandTree.do?returnflag=dxt&br_query1=query" alt="需求报批">
        </hrms:priv> <hrms:priv func_id="31012"> 
        <area shape="rect" coords="17,109,98,142" href="/hire/demandPlan/positionDemand/positionDemandTree.do?returnflag=dxt&br_query2=query" alt="需求审批">
        </hrms:priv> <hrms:priv func_id="31014"> 
        <area shape="rect" coords="16,189,97,219" href="/hire/demandPlan/positionDemand/positionDemandTree.do?returnflag=dxt&br_query3=query" alt="审核查询">
        </hrms:priv> <hrms:priv func_id="31031"> 
        <area shape="rect" coords="21,308,103,342" href="/hire/interviewEvaluating/interviewExamine.do?returnflag=dxt&br_query=link" alt="面试考核">
        </hrms:priv> <hrms:priv func_id="31032"> 
        <area shape="rect" coords="189,316,259,349" href="/hire/interviewEvaluating/interviewAnnounce.do?returnflag=dxt&br_query=link" alt="面试通知">
        </hrms:priv> <hrms:priv func_id="31031"> 
        <area shape="rect" coords="373,321,440,347" href="/hire/interviewEvaluating/interviewArrange.do?returnflag=dxt&br_query=link" alt="面试安排">
        </hrms:priv> <hrms:priv func_id="31024"> 
        <area shape="rect" coords="378,212,455,237" href="/hire/employActualize/employResume.do?returnflag=dxt&b_query=link&z0301=-1&personType=1&operate=init" alt="人才库">
        </hrms:priv> <hrms:priv func_id="3102"> 
        <area shape="rect" coords="375,168,451,197" href="/hire/employActualize/employResume.do?returnflag=dxt&b_query=link&z0301=-1&personType=4&operate=init" alt="我的收藏夹">
        </hrms:priv> <hrms:priv func_id="31023"> 
        <area shape="rect" coords="375,115,445,147" href="/hire/employActualize/employResume.do?returnflag=dxt&b_query=link&z0301=-1&personType=0&operate=init" alt="应聘简历">
        </hrms:priv> <hrms:priv func_id="31022"> 
        <area shape="rect" coords="379,70,445,98" href="/hire/employActualize/employPosition.do?returnflag=dxt&b_query=link&operate=init" alt="招聘岗位">
        </hrms:priv> <hrms:priv func_id="31013"> 
        <area shape="rect" coords="178,104,256,142" href="/hire/demandPlan/engagePlan.do?returnflag=dxt&b_query=query" alt="招聘计划">
        </hrms:priv> <hrms:priv func_id="31061"> 
        <area shape="rect" coords="610,73,683,100" href="/hire/zp_options/stat/showstatestat.do?returnflag=dxt&b_query=link" alt="按岗位">
        </hrms:priv> <hrms:priv func_id="31062"> 
        <area shape="rect" coords="606,119,687,149" href="/hire/zp_options/stat/statestat/showstateresult.do?returnflag=dxt&b_query=link&init=1" alt="按简历状态">
        </hrms:priv> <hrms:priv func_id="31063"> 
        <area shape="rect" coords="606,162,685,196" href="/hire/zp_options/stat/itemstat/showstatresult.do?returnflag=dxt&b_query=link&init=1&pos=menu" alt="按类别">
        </hrms:priv> <hrms:priv func_id="31041"> 
        <area shape="rect" coords="15,399,97,433" href="/hire/employSummarise/personnelEmploy.do?returnflag=dxt&br_query=link" alt="员工录用">
        </hrms:priv> <hrms:priv func_id="31042"> 
        <area shape="rect" coords="188,400,270,434" href="/hire/employSummarise/hireSummarise.do?returnflag=dxt&b_query=link&operate=init" alt="招聘总结">
        </hrms:priv> </map></td>
</tr>
</table>
<%} %>
</body>
</html>
