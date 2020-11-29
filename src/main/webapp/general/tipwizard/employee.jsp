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
</head>
<body>
<%if("hcm".equals(bosflag)){ %>
<table width="80%" align="center" >
<tr>
    <td align="center"> <img src="hcm/<%=themes %>/employee.jpg"  border="0" usemap="#Map"> </td>
</tr>
</table>
<map name="Map">
  <hrms:priv func_id="26011"> 
  <area shape="rect" coords="96,110,205,142" href="/workbench/browse/showinfo.do?returns=dxt&b_search=link&action=showinfodata.do&target=nil_body&userbase=&flag=noself&isUserEmploy=1" alt="信息浏览">
 </hrms:priv>
<hrms:priv func_id="26010"> 
  <area shape="rect" coords="96,216,205,246" href="/workbench/query/query_interface.do?returnvalue=dxt&b_query=link&a_inforkind=1&home=1" alt="快速查询">
</hrms:priv>
<hrms:priv func_id="2601002"> 
  <area shape="rect" coords="96,266,204,300" href="/workbench/query/hquery_interface.do?returnvalue=dxt&a_query=1&b_query=link&a_inforkind=1&home=1" alt="简单查询">
</hrms:priv>
<hrms:priv func_id="2601003"> 
  <area shape="rect" coords="95,321,204,351" href="/workbench/query/hquery_interface.do?returnvalue=dxt&a_query=2&b_query=link&a_inforkind=1&home=1" alt="通用查询">
</hrms:priv>
<hrms:priv func_id="26010"> 
  <area shape="rect" coords="97,161,205,194" href="/workbench/query/query_interface.do?returnvalue=dxt&b_gquery=link&type=1&home=dxt" alt="常用查询">
 </hrms:priv>
<hrms:priv func_id="26032"> 
  <area shape="rect" coords="96,408,206,439" href="/general/muster/hmuster/searchHroster.do?returnflag=dxt&b_search=link&nFlag=3&a_inforkind=1&result=0" alt="高级花名册">
</hrms:priv>
<hrms:priv func_id="2604"> 
<!--   <area shape="rect" coords="365,408,474,442" href="/general/card/searchcard.do?returnvalue=dxt&b_query=link&home=2&inforkind=1&result=0" alt="登记表">-->
 <area shape="rect" coords="365,408,474,442" href="/module/card/cardCommonSearch.jsp?inforkind=1&callbackfunc=dxt" alt="登记表">
 </hrms:priv>
<hrms:priv func_id="26062"> 
  <area shape="rect" coords="365,319,476,352" href="/templates/menu/busi_m_menu.do?b_query2=link&module=11&cs_module=10" alt="表格录入">
</hrms:priv>
<hrms:priv func_id="26060">                        
  <area shape="rect" coords="366,215,475,248" href="/workbench/info/showinfo.do?returnvalue=dxt&returnvalue1=dxt&b_searchsort=link&amp;action=showinfodata.do&amp;isUserEmploy=1&amp;target=nil_body&amp;flag=noself" alt="记录录入">
</hrms:priv>
<hrms:priv func_id="26000"> 
  <area shape="rect" coords="365,111,475,143" href="/workbench/info/addinfo/add.do?returnvalue=dxt&b_add=add&a0100=A0100&i9999=I9999&actiontype=new&setname=A01&tolastpageflag=yes&flag=notself" alt="快速录入">
 </hrms:priv>
<hrms:priv func_id="26020"> 
  <area shape="rect" coords="611,161,721,194" href="/general/static/select_field.do?returnvalue=dxt&b_query=link&a_inforkind=1" alt="简单统计">
 </hrms:priv>
<hrms:priv func_id="26021"> 
  <area shape="rect" coords="613,214,722,247" href="/general/static/select_static_fields.do?returnvalue=dxt&b_query=link&a_inforkind=1" alt="通用统计">
</hrms:priv>
<hrms:priv func_id="26022"> 
  <area shape="rect" coords="612,266,724,300" href="/general/static/two_dim_static.do?returnvalue=dxt&b_query=link&a_inforkind=1" alt="二维统计">
</hrms:priv>
<hrms:priv func_id="26023"> 
  <area shape="rect" coords="612,109,723,143" href="/general/static/commonstatic/statshow.do?returnvalue=dxt&b_ini=link&infokind=1&home=6" alt="常用统计">
</hrms:priv>
<hrms:priv func_id="26024"> 
  <area shape="rect" coords="612,320,723,353" href="/general/static/singlestatic/single_static.do?returnvalue=dxt&b_query=link&a_inforkind=1" alt="单项统计">
</hrms:priv>
<hrms:priv func_id="26031"> 
  <area shape="rect" coords="613,408,722,441" href="/module/muster/mustermanage/MusterManage.html" alt="常用花名册">
</hrms:priv>
</map>
<%}else{ %>
<table width="80%" align="center" >
<tr>
    <td align="center"> <img src="employee.gif"  border="0" usemap="#Map"> </td>
</tr>
</table>
<map name="Map">
  <hrms:priv func_id="26011"> 
  <area shape="rect" coords="26,71,110,96" href="/workbench/browse/showinfo.do?returns=dxt&b_search=link&action=showinfodata.do&target=nil_body&userbase=&flag=noself&isUserEmploy=1" alt="信息浏览">
 </hrms:priv>
<hrms:priv func_id="26010"> 
  <area shape="rect" coords="29,163,111,191" href="/workbench/query/query_interface.do?returnvalue=dxt&b_query=link&a_inforkind=1&home=1" alt="快速查询">
</hrms:priv>
<hrms:priv func_id="2601002"> 
  <area shape="rect" coords="32,213,111,238" href="/workbench/query/hquery_interface.do?returnvalue=dxt&a_query=1&b_query=link&a_inforkind=1&home=1" alt="简单查询">
</hrms:priv>
<hrms:priv func_id="2601003"> 
  <area shape="rect" coords="26,256,107,287" href="/workbench/query/hquery_interface.do?returnvalue=dxt&a_query=2&b_query=link&a_inforkind=1&home=1" alt="通用查询">
</hrms:priv>
<hrms:priv func_id="26010"> 
  <area shape="rect" coords="31,116,108,145" href="/workbench/query/query_interface.do?returnvalue=dxt&b_gquery=link&type=1&home=dxt" alt="常用查询">
 </hrms:priv>
<hrms:priv func_id="26032"> 
  <area shape="rect" coords="25,351,107,381" href="/general/muster/hmuster/searchHroster.do?returnflag=dxt&b_search=link&nFlag=3&a_inforkind=1&result=0" alt="高级花名册">
</hrms:priv>
<hrms:priv func_id="2604"> 
  <area shape="rect" coords="258,351,338,387" href="/module/card/cardCommonSearch.jsp?inforkind=1&callbackfunc=dxt" alt="登记表">
</hrms:priv>
<hrms:priv func_id="26062"> 
  <area shape="rect" coords="264,238,339,266" href="/templates/menu/busi_m_menu.do?b_query2=link&module=11&cs_module=10" alt="表格录入">
</hrms:priv>
<hrms:priv func_id="26060">                        
  <area shape="rect" coords="267,156,341,185" href="/workbench/info/showinfo.do?returnvalue=dxt&returnvalue1=dxt&b_searchsort=link&amp;action=showinfodata.do&amp;isUserEmploy=1&amp;target=nil_body&amp;flag=noself" alt="记录录入">
</hrms:priv>
<hrms:priv func_id="26000"> 
  <area shape="rect" coords="269,82,341,111" href="/workbench/info/addinfo/add.do?returnvalue=dxt&b_add=add&a0100=A0100&i9999=I9999&actiontype=new&setname=A01&tolastpageflag=yes&flag=notself" alt="快速录入">
 </hrms:priv>
<hrms:priv func_id="26020"> 
  <area shape="rect" coords="498,114,572,145" href="/general/static/select_field.do?returnvalue=dxt&b_query=link&a_inforkind=1" alt="简单统计">
 </hrms:priv>
<hrms:priv func_id="26021"> 
  <area shape="rect" coords="499,162,573,188" href="/general/static/select_static_fields.do?returnvalue=dxt&b_query=link&a_inforkind=1" alt="通用统计">
</hrms:priv>
<hrms:priv func_id="26022"> 
  <area shape="rect" coords="490,210,569,239" href="/general/static/two_dim_static.do?returnvalue=dxt&b_query=link&a_inforkind=1" alt="二维统计">
</hrms:priv>
<hrms:priv func_id="26023"> 
  <area shape="rect" coords="495,72,570,98" href="/general/static/commonstatic/statshow.do?returnvalue=dxt&b_ini=link&infokind=1&home=6" alt="常用统计">
</hrms:priv>
<hrms:priv func_id="26024"> 
  <area shape="rect" coords="491,256,569,285" href="/general/static/singlestatic/single_static.do?returnvalue=dxt&b_query=link&a_inforkind=1" alt="单项统计">
</hrms:priv>
<hrms:priv func_id="26031"> 
  <area shape="rect" coords="495,353,572,385" href="/general/muster/hmuster/searchroster.do?returnflag=dxt&b_search=link&a_inforkind=1&result=" alt="常用花名册">
</hrms:priv>
</map>
<%} %>
</body>
</html>
