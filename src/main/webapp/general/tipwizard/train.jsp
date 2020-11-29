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
    <td align="center"> <img src="hcm/<%=themes %>/train.jpg"  border="0" usemap="#Map"> </td>
</tr>
</table>
<map name="Map">
  <hrms:priv func_id="32310"> 
  <area shape="rect" coords="110,82,219,114" href="/train/traincourse/org_tree.do?returnvalue=dxt&b_query=link&model=1&returnflag=dxt" alt="需求采集">
</hrms:priv>
<hrms:priv func_id="32311"> 
  <area shape="rect" coords="109,168,219,201" href="/train/traincourse/org_tree.do?returnvalue=dxt&b_query=link&model=2&returnflag=dxt" alt="需求审批">
</hrms:priv>
<hrms:priv func_id="32360"> 
  <area shape="rect" coords="110,284,219,318" href="/train/report/orgTree.do?returnvalue=dxt&b_query=link&type=1&returnflag=dxt" alt="单位部门报表">
</hrms:priv>
<hrms:priv func_id="32361"> 
  <area shape="rect" coords="110,343,219,377" href="/train/report/orgTree.do?returnvalue=dxt&b_query=link&type=2&returnflag=dxt" alt="培训类别报表">
</hrms:priv>
<hrms:priv func_id="32362"> 
  <area shape="rect" coords="110,403,218,435" href="/train/report/orgTree.do?returnvalue=dxt&b_query=link&type=3&returnflag=dxt" alt="学员培训报表">
</hrms:priv>
<hrms:priv func_id="3235"> 
  <area shape="rect" coords="355,433,463,464" href="/train/trainCosts/trainCosts.do?returnvalue=dxt&b_org=link&model=1&returnflag=dxt" alt="培训费用">
</hrms:priv>
<hrms:priv func_id="32331"> 
  <area shape="rect" coords="354,343,464,375" href="/general/inform/org_tree.do?returnvalue=dxt&b_query=link&inforflag=2&returnflag=dxt" alt="外部培训">
</hrms:priv>
<hrms:priv func_id="3233"> 
  <area shape="rect" coords="354,284,463,317" href="/train/request/trainsData.do?returnvalue=dxt&b_org=link&model=1&returnflag=dxt" alt="培训班">
</hrms:priv>
<hrms:priv func_id="32321"> 
  <area shape="rect" coords="355,170,464,201" href="/train/b_plan/planTrain.do?returnvalue=dxt&b_org=link&model=2&returnflag=dxt" alt="计划审批">
 </hrms:priv>
<hrms:priv func_id="32320"> 
  <area shape="rect" coords="353,82,463,114" href="/train/b_plan/planTrain.do?returnvalue=dxt&b_org=link&model=1&returnflag=dxt" alt="计划制订">
</hrms:priv>
<hrms:priv func_id="32300"> 
  <area shape="rect" coords="600,82,709,113" href="/train/resource/trainPro.do?returnvalue=dxt&br_query=link&returnflag=dxt" alt="培训类别">
</hrms:priv>
<hrms:priv func_id="32301"> 
  <area shape="rect" coords="599,137,709,169" href="/train/resource/trainRescList.do?returnvalue=dxt&b_query=link&type=1&returnflag=dxt" alt="培训机构">
</hrms:priv>
<hrms:priv func_id="32302"> 
  <area shape="rect" coords="599,192,708,224" href="/train/resource/trainRescList.do?returnvalue=dxt&b_query=link&type=2&returnflag=dxt" alt="培训教师">
</hrms:priv>
<hrms:priv func_id="32303">   
  <area shape="rect" coords="599,248,709,281" href="/train/resource/trainRescList.do?returnvalue=dxt&b_query=link&type=3&returnflag=dxt" alt="培训场所">
 </hrms:priv>
  <hrms:priv func_id="32304"> 
  <area shape="rect" coords="598,303,708,335" href="/train/resource/trainRescList.do?returnvalue=dxt&b_query=link&type=4&returnflag=dxt" alt="培训设施">
 </hrms:priv>
<hrms:priv func_id="32306C" module_id="39"> 
  <area shape="rect" coords="598,357,707,390" href="/train/resource/course.do?returnvalue=dxt&b_tree=link&returnflag=dxt" alt="培训课程">
</hrms:priv>
</map>
<%}else{ %>
<table width="80%" align="center" >
<tr>
    <td align="center"> <img src="train.gif"  border="0" usemap="#Map"> </td>
</tr>
</table>
<map name="Map">
  <hrms:priv func_id="32310"> 
  <area shape="rect" coords="34,71,107,100" href="/train/traincourse/org_tree.do?returnvalue=dxt&b_query=link&model=1&returnflag=dxt" alt="需求采集">
</hrms:priv>
<hrms:priv func_id="32311"> 
  <area shape="rect" coords="45,144,117,175" href="/train/traincourse/org_tree.do?returnvalue=dxt&b_query=link&model=2&returnflag=dxt" alt="需求审批">
</hrms:priv>
<hrms:priv func_id="32360"> 
  <area shape="rect" coords="25,291,109,322" href="/train/report/orgTree.do?returnvalue=dxt&b_query=link&type=1&returnflag=dxt" alt="单位部门报表">
</hrms:priv>
<hrms:priv func_id="32361"> 
  <area shape="rect" coords="23,340,112,376" href="/train/report/orgTree.do?returnvalue=dxt&b_query=link&type=2&returnflag=dxt" alt="培训类别报表">
</hrms:priv>
<hrms:priv func_id="32362"> 
  <area shape="rect" coords="24,385,105,419" href="/train/report/orgTree.do?returnvalue=dxt&b_query=link&type=3&returnflag=dxt" alt="学员培训报表">
</hrms:priv>
<hrms:priv func_id="3235"> 
  <area shape="rect" coords="268,415,349,449" href="/train/trainCosts/trainCosts.do?returnvalue=dxt&b_org=link&model=1&returnflag=dxt" alt="培训费用">
</hrms:priv>
<hrms:priv func_id="32331"> 
  <area shape="rect" coords="265,329,346,359" href="/general/inform/org_tree.do?returnvalue=dxt&b_query=link&inforflag=2&returnflag=dxt" alt="外部培训">
</hrms:priv>
<hrms:priv func_id="3233"> 
  <area shape="rect" coords="263,279,336,313" href="/train/request/trainsData.do?returnvalue=dxt&b_org=link&model=1&returnflag=dxt" alt="培训班">
</hrms:priv>
<hrms:priv func_id="32321"> 
  <area shape="rect" coords="266,142,341,174" href="/train/b_plan/planTrain.do?returnvalue=dxt&b_org=link&model=2&returnflag=dxt" alt="计划审批">
 </hrms:priv>
<hrms:priv func_id="32320"> 
  <area shape="rect" coords="263,73,339,100" href="/train/b_plan/planTrain.do?returnvalue=dxt&b_org=link&model=1&returnflag=dxt" alt="计划制订">
</hrms:priv>
<hrms:priv func_id="32300"> 
  <area shape="rect" coords="500,69,576,97" href="/train/resource/trainPro.do?returnvalue=dxt&br_query=link&returnflag=dxt" alt="培训类别">
</hrms:priv>
<hrms:priv func_id="32301"> 
  <area shape="rect" coords="503,116,574,145" href="/train/resource/trainRescList.do?returnvalue=dxt&b_query=link&type=1&returnflag=dxt" alt="培训机构">
</hrms:priv>
<hrms:priv func_id="32302"> 
  <area shape="rect" coords="505,162,578,191" href="/train/resource/trainRescList.do?returnvalue=dxt&b_query=link&type=2&returnflag=dxt" alt="培训教师">
</hrms:priv>
<hrms:priv func_id="32303">   
  <area shape="rect" coords="503,208,578,241" href="/train/resource/trainRescList.do?returnvalue=dxt&b_query=link&type=3&returnflag=dxt" alt="培训场所">
 </hrms:priv>
  <hrms:priv func_id="32304"> 
  <area shape="rect" coords="504,252,578,284" href="/train/resource/trainRescList.do?returnvalue=dxt&b_query=link&type=4&returnflag=dxt" alt="培训设施">
 </hrms:priv>
<hrms:priv func_id="32306C" module_id="39"> 
  <area shape="rect" coords="503,300,577,332" href="/train/resource/course.do?returnvalue=dxt&b_tree=link&returnflag=dxt" alt="培训课程">
</hrms:priv>
</map>
<%} %>
</body>
</html>
