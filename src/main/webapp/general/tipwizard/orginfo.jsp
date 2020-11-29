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
    <td align="center"> <img src="hcm/<%=themes %>/orginfo.jpg"  border="0" usemap="#Map"> </td>
</tr>
</table>
<map name="Map">
<hrms:priv func_id="2310">
  <hrms:priv func_id="23011"> 
  <area shape="rect" coords="108,140,217,171" href="/general/inform/org/searchorgbrowse.do?returnvalue=dxt&b_query=link&droit=&busiPriv=1" alt="单位信息">
</hrms:priv>
</hrms:priv>
<hrms:priv func_id="2311">
<hrms:priv func_id="25011">  
  <area shape="rect" coords="107,198,216,232" href="/general/inform/pos/searchorgbrowse.do?returnvalue=dxt&b_query=link" alt="岗位信息">
</hrms:priv>
</hrms:priv>
<hrms:priv func_id="2310">
<hrms:priv func_id="23051">  
  <area shape="rect" coords="108,259,216,292" href="/general/inform/org/map/searchOrgTree.do?b_search=link&backdate=&amp;returnvalue=dxt&busiPriv=1" alt="机构图">
</hrms:priv>
</hrms:priv>
<hrms:priv func_id="2311"> 
<hrms:priv func_id="2504"> 
   <area shape="rect" coords="108,317,217,351" href="/module/card/cardCommonSearch.jsp?inforkind=4&callbackfunc=dxt" alt="岗位说明书">
</hrms:priv>
</hrms:priv>
<hrms:priv func_id="2311">
<hrms:priv func_id="231101"> 
<hrms:priv func_id="23110108"> 
   <area shape="rect" coords="109,376,216,410" href="/pos/posreport/get_relation_tree.do?b_search=link&openwin=1&returnvalue=dxt&yfiles=1" alt="岗位汇报关系图">
</hrms:priv>
</hrms:priv>
</hrms:priv>
<hrms:priv func_id="23064"> 
  <area shape="rect" coords="374,378,483,409" href="/org/orgpre/get_org_tree.do?returnvalue=dxt&b_query=link&infor=2&unit_type=3" alt="编制管理">
</hrms:priv>
<hrms:priv func_id="2314"> 
<hrms:priv func_id="23062"> 
   <area shape="rect" coords="608,378,717,410" href="/pos/posparameter/ps_parameter.do?returnvalue=dxt&b_search_unit=link" alt="编制参数设置">
</hrms:priv>
</hrms:priv>
<hrms:priv func_id="23061"> 
  <area shape="rect" coords="374,284,484,318" href="/org/autostatic/confset/datasynchro.do?returnvalue=dxt&b_init=link" alt="数据联动">
</hrms:priv>
<hrms:priv func_id="2311">
<hrms:priv func_id="231101"> 
  <area shape="rect" coords="374,189,483,221" href="/workbench/dutyinfo/searchdutyinfo.do?b_search=link&returnvalue1=dxt&backdate=&action=searchdutyinfodata.do&treetype=vorg&kind=2&target=nil_body&loadtype=1" target="il_body" alt="岗位信息">
</hrms:priv>
</hrms:priv>
<hrms:priv func_id="2313">
<hrms:priv func_id="23056"> 
  <area shape="rect" coords="607,248,716,282" href="/pos/posbusiness/searchposbusinesstree.do?b_query=link&first=4&returnvalue=dxt&param=PS_LEVEL_CODE" alt="职务级别设置">
</hrms:priv>
<hrms:priv func_id="23057"> 
  <area shape="rect" coords="607,193,716,226" href="/pos/posbusiness/searchposbusinesstree.do?b_query=link&first=4&returnvalue=dxt&param=PS_CODE" alt="职务体系设置">
</hrms:priv>
<hrms:priv func_id="23050"> 
  <area shape="rect" coords="607,139,715,172" href="/org/orginfo/searchorgtree.do?returnvalue=dxt&b_query=link&code=" alt="机构编码">
</hrms:priv>
</hrms:priv>
<hrms:priv func_id="2310"> 
<hrms:priv func_id="230600"> 
  <area shape="rect" coords="374,139,484,173" href="/workbench/orginfo/searchorginfo.do?b_search=link&returnvalue1=dxt&backdate=&action=searchorginfodata.do&treetype=vorg&kind=2&loadtype=1&target=nil_body&leader=org&busiPriv=1" alt="单位信息">
</hrms:priv>
</hrms:priv>
</map>
<%}else{ %>
<table width="80%" align="center" >
<tr>
    <td align="center"> <img src="orginfo.gif"  border="0" usemap="#Map"> </td>
</tr>
</table>
<map name="Map">
<hrms:priv func_id="2310">
  <hrms:priv func_id="23011"> 
  <area shape="rect" coords="22,71,111,100" href="/general/inform/org/searchorgbrowse.do?returnvalue=dxt&b_query=link&droit=&busiPriv=1" alt="单位信息">
</hrms:priv>
</hrms:priv>
<hrms:priv func_id="2311">
<hrms:priv func_id="25011">  
  <area shape="rect" coords="24,133,103,162" href="/general/inform/pos/searchorgbrowse.do?returnvalue=dxt&b_query=link" alt="岗位信息">
</hrms:priv>
</hrms:priv>
<hrms:priv func_id="2310">
<hrms:priv func_id="23051">  
  <area shape="rect" coords="28,193,112,222" href="/general/inform/org/map/searchOrgTree.do?b_search=link&amp;backdate=&amp;returnvalue=dxt&busiPriv=1" alt="机构图">
</hrms:priv>
</hrms:priv>
<hrms:priv func_id="2311"> 
<hrms:priv func_id="2504">
  <area shape="rect" coords="27,250,105,275" href="/module/card/cardCommonSearch.jsp?inforkind=4&callbackfunc=dxt" alt="岗位说明书">
</hrms:priv>
</hrms:priv>
<hrms:priv func_id="2311">
<hrms:priv func_id="231101"> 
<hrms:priv func_id="23110108"> 
  <area shape="rect" coords="29,310,105,330" href="/pos/posreport/get_relation_tree.do?b_search=link&openwin=1&returnvalue=dxt&yfiles=1" alt="岗位汇报关系图">
</hrms:priv>
</hrms:priv>
</hrms:priv>
<hrms:priv func_id="23064"> 
  <area shape="rect" coords="265,318,338,341" href="/org/orgpre/get_org_tree.do?returnvalue=dxt&b_query=link&infor=2&unit_type=3" alt="编制管理">
</hrms:priv>
<hrms:priv func_id="2314"> 
<hrms:priv func_id="23062"> 
  <area shape="rect" coords="492,322,582,342" href="/pos/posparameter/ps_parameter.do?returnvalue=dxt&b_search_unit=link" alt="编制参数设置">
</hrms:priv>
</hrms:priv>
<hrms:priv func_id="23061"> 
  <area shape="rect" coords="262,231,335,256" href="/org/autostatic/confset/datasynchro.do?returnvalue=dxt&b_init=link" alt="数据联动">
</hrms:priv>
<hrms:priv func_id="2311">
<hrms:priv func_id="231101"> 
  <area shape="rect" coords="263,123,333,145" href="/workbench/dutyinfo/searchdutyinfo.do?b_search=link&returnvalue1=dxt&backdate=&action=searchdutyinfodata.do&treetype=vorg&kind=2&target=nil_body&loadtype=1" target="il_body" alt="岗位设置">
</hrms:priv>
</hrms:priv>
<hrms:priv func_id="2313">
<hrms:priv func_id="23056"> 
  <area shape="rect" coords="500,165,582,193" href="/pos/posbusiness/searchposbusinesstree.do?b_query=link&first=4&returnvalue=dxt&param=PS_LEVEL_CODE" alt="职务级别设置">
</hrms:priv>
<hrms:priv func_id="23057"> 
  <area shape="rect" coords="494,121,576,149" href="/pos/posbusiness/searchposbusinesstree.do?b_query=link&first=4&returnvalue=dxt&param=PS_CODE" alt="职务体系设置">
</hrms:priv>
<hrms:priv func_id="23050"> 
  <area shape="rect" coords="506,75,572,94" href="/org/orginfo/searchorgtree.do?returnvalue=dxt&b_query=link&code=" alt="机构编码">
</hrms:priv>
</hrms:priv>
<hrms:priv func_id="2310"> 
<hrms:priv func_id="230600"> 
  <area shape="rect" coords="258,71,338,91" href="/workbench/orginfo/searchorginfo.do?b_search=link&returnvalue1=dxt&backdate=&action=searchorginfodata.do&treetype=vorg&kind=2&loadtype=1&target=nil_body&leader=org&busiPriv=1" alt="单位信息">
</hrms:priv>
</hrms:priv>
</map>
<%} %>
</body>
</html>
