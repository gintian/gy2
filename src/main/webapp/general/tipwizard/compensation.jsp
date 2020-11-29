<%@ page contentType="text/html; charset=UTF-8" language="java"%>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ page import="com.hrms.struts.valueobject.UserView"%>
<%@ page import="com.hrms.struts.constant.WebConstant,com.hjsj.hrms.businessobject.sys.SysParamBo"%>
<%
   UserView userView =(UserView)session.getAttribute(WebConstant.userView);
   int versionFlag = 0;
   int ver=userView.getVersion(); //锁版本
   String bosflag = "";
    String themes="default";
		if (userView != null){
			versionFlag = userView.getVersion_flag(); // 1:专业版 0:标准版
			bosflag = userView.getBosflag();
			themes = SysParamBo.getSysParamValue("THEMES",userView.getUserName());
		}
 %>
<html>
<head>
<html>
<head>
<title></title>
   <link href="/css/css1.css" rel="stylesheet" type="text/css">
</head>
<body class="body_sec">
<%if("hcm".equals(bosflag)){ %>
<table width="80%" align="center" >
<tr>
    <td align="center"> <img src="hcm/<%=themes %>/compensation.jpg" border="0" usemap="#Map"> </td>
</tr>
</table>
<map name="Map">
  <hrms:priv func_id="32408">
  <% if(ver<70){ %>
  <area shape="rect" coords="97,118,207,150" href="/gz/templateset/gz_templatelist.do?returnvalue=dxt&b_query=link0&gz_module=0" alt="薪资类别">
  <% }else{ %>
  <area shape="rect" coords="97,118,207,150" href="/module/gz/salarytype/SalaryType.html?b_query=link&imodule=0&returnvalue=1" alt="薪资类别">
  <% } %>
 </hrms:priv>
<hrms:priv func_id="32409">
<%--    <area shape="rect" coords="99,169,208,202" href="/gz/templateset/tax_table/initTaxTable.do?returnflag=dxt&b_init=init" alt="税率表">--%>
    <area shape="rect" coords="99,169,208,202" href="/module/gz/templateset/taxtable/TaxTableSet.html?b_query=link" alt="税率表">
</hrms:priv>
<hrms:priv func_id="32410">
<%--    <area shape="rect" coords="97,222,207,254" href="/gz/templateset/standard/standardPackage.do?returnflag=dxt&b_query=init" alt="薪资标准">--%>
    <area shape="rect" coords="97,222,207,254" href="/module/gz/templateset/standard/Standard.html" alt="薪资标准">
</hrms:priv>
<%if(versionFlag==1){ %>
<hrms:priv func_id="32411">
  <area shape="rect" coords="98,273,208,307" href="/gz/gz_amount/init_parameter_config.do?returnflag=dxt&b_query=link&opt=init" alt="薪资总额参数">
  </hrms:priv>
  <%} %>
<hrms:priv func_id="32412">
  <area shape="rect" coords="98,326,208,360" href="/org/gzdatamaint/gzdatamaint.do?returnflag=dxt&b_addsubclass=link&tagname=1&infor=2&gzflag=2" alt="相关子集">
  </hrms:priv>
<hrms:priv func_id="32413">
  <area shape="rect" coords="99,380,207,411" href="/org/gzdatamaint/gz_org_tree.do?returnflag=dxt&b_query=link&infor=2&gzflag=2" alt="基础数据">
</hrms:priv>

<hrms:priv func_id="32401">
 <% if(ver<70){ %>
  <area shape="rect" coords="369,115,478,149" href="/general/template/search_bs_tree.do?b_query=link&amp;type=2&amp;res_flag=8" alt="薪资变动">
<% }else{ %>
  <area shape="rect" coords="369,115,478,149" href="/module/template/templatenavigation/TemplateNavigation.html?b_query=link&sys_type=1&module_id=2" alt="薪资变动">
 <% } %>
</hrms:priv>

<hrms:priv func_id="32402">
<% if(ver<70){ %>
  <area shape="rect" coords="368,199,479,233" href="/gz/gz_accounting/gz_set_list.do?returnvalue=dxt&b_query=link&flow_flag=0&gz_module=0" alt="薪资发放">
<% }else{ %>
  <area shape="rect" coords="368,199,479,233" href="/module/gz/salarytemplate/SalaryTemplate.html?b_query=link&viewtype=0&imodule=0" alt="薪资发放">
<% } %>

</hrms:priv>
<hrms:priv func_id="32403">
   <% if(ver<70){ %>
  <area shape="rect" coords="368,249,478,282" href="/gz/gz_accounting/gz_sp_setlist.do?returnvalue=dxt&b_query=link&flow_flag=1&gz_module=0" alt="薪资审批">
  <% }else{ %>
   <area shape="rect" coords="368,249,478,282" href="/module/gz/salarytemplate/SalaryTemplate.html?b_query=link&viewtype=1&imodule=0" alt="薪资审批">
  <% } %>
</hrms:priv>
<hrms:priv func_id="32404">
 <% if(ver<70){ %>
  <area shape="rect" coords="369,298,478,331" href="/gz/gz_accounting/tax/gz_tax_org_tree.do?returnvalue=dxt&b_query=link&is_back=not" alt="所得税管理">
   <% }else{ %>
   <area shape="rect" coords="369,298,478,331" href="/module/gz/tax/SearchTax.html" alt="所得税管理">
   <% } %>
</hrms:priv>
<%if(versionFlag==1){ %>
<hrms:priv func_id="32405">
  <area shape="rect" coords="368,378,478,412" href="/gz/gz_amount/gz_gross_tree.do?returnflag=dxt&b_query=link" alt="薪资总额">
</hrms:priv>
<%} %>
<hrms:priv func_id="324071">

  <% if(ver<70){ %>
  	<area shape="rect" coords="615,172,723,204" href="/gz/gz_analyse/gzAnalyseList.do?returnflag=dxt&b_query=link&gz_module=0" alt="分析表">
  <% }else{ %>
   	<area shape="rect" coords="615,172,723,204" href="/module/gz/analysistables/analysistable/AnalysisTables.html?imodule=0" alt="分析表">
  <% } %>
 </hrms:priv>
 <%if(versionFlag==1){ %>
<hrms:priv func_id="324073">
  <area shape="rect" coords="615,273,725,306" href="/gz/gz_analyse/gz_fare/fare_analyse_orgtree.do?b_query=query&opt=init&type=0" alt="发放进展表">
</hrms:priv>
<%} %>
<hrms:priv func_id="324072">
  <area shape="rect" coords="616,224,724,255" href="/gz/gz_analyse/gzAnalyseChart.do?returnvalue=dxt&br_query=link" alt="分析图">
 </hrms:priv>
<%--    <area shape="rect" coords="616,322,724,353" href="/gz/gz_analyse/historydata/salary_set_list.do?b_query=query&gz_module=0&returnflag=dxt" alt="薪资历史数据" />--%>
    <area shape="rect" coords="616,322,724,353" href="/module/gz/analyse/historydata/SalaryHistoryData.html?b_query=link" alt="薪资数据" />
</map>
<%}else{ %>
<table width="80%" align="center" >
<tr>
    <td align="center"> <img src="compensation.gif" border="0" usemap="#Map"> </td>
</tr>
</table>
<map name="Map">
  <hrms:priv func_id="32408">
  <% if(ver<70){ %>
  <area shape="rect" coords="29,76,112,102" href="/gz/templateset/gz_templatelist.do?returnvalue=dxt&b_query=link0&gz_module=0" alt="薪资类别">
  <% }else{ %>
  <area shape="rect" coords="29,76,112,102" href="/module/gz/salarytype/SalaryType.html?b_query=link&imodule=0&returnvalue=1" alt="薪资类别">
  <% } %>
 </hrms:priv>
<hrms:priv func_id="32409">
<%--    <area shape="rect" coords="33,120,114,147" href="/gz/templateset/tax_table/initTaxTable.do?returnflag=dxt&b_init=init" alt="税率表">--%>
    <area shape="rect" coords="33,120,114,147" href="/module/gz/templateset/taxtable/TaxTableSet.html?b_query=link" alt="税率表">
</hrms:priv>
<hrms:priv func_id="32410">
<%--    <area shape="rect" coords="33,172,111,195" href="/gz/templateset/standard/standardPackage.do?returnflag=dxt&b_query=init" alt="薪资标准">--%>
    <area shape="rect" coords="33,172,111,195" href="/module/gz/templateset/standard/Standard.html" alt="薪资标准">
</hrms:priv>
<%if(versionFlag==1){ %>
<hrms:priv func_id="32411">
  <area shape="rect" coords="28,220,108,245" href="/gz/gz_amount/init_parameter_config.do?returnflag=dxt&b_query=link&opt=init" alt="薪资总额参数">
  </hrms:priv>
  <%} %>
<hrms:priv func_id="32412">
  <area shape="rect" coords="32,262,105,289" href="/org/gzdatamaint/gzdatamaint.do?returnflag=dxt&b_addsubclass=link&tagname=1&infor=2&gzflag=2" alt="相关子集">
  </hrms:priv>
<hrms:priv func_id="32413">
  <area shape="rect" coords="30,309,106,338" href="/org/gzdatamaint/gz_org_tree.do?returnflag=dxt&b_query=link&infor=2&gzflag=2" alt="基础数据">
</hrms:priv>

<hrms:priv func_id="32401">


   <% if(ver<70){ %>
   <area shape="rect" coords="264,33,344,62" href="/general/template/search_bs_tree.do?b_query=link&amp;type=2&amp;res_flag=8" alt="薪资变动">
<% }else{ %>
 <area shape="rect" coords="264,33,344,62" href="/module/template/templatenavigation/TemplateNavigation.html?b_query=link&sys_type=1&module_id=2" alt="薪资变动">
 <% } %>

</hrms:priv>

<hrms:priv func_id="32402">

  <% if(ver<70){ %>
  <area shape="rect" coords="263,133,337,156" href="/gz/gz_accounting/gz_set_list.do?returnvalue=dxt&b_query=link&flow_flag=0&gz_module=0" alt="薪资发放">
<% }else{ %>
  <area shape="rect" coords="263,133,337,156" href="/module/gz/salarytemplate/SalaryTemplate.html?b_query=link&viewtype=0&imodule=0" alt="薪资发放">
<% } %>
</hrms:priv>
<hrms:priv func_id="32403">
   <% if(ver<70){ %>
  <area shape="rect" coords="269,178,340,208" href="/gz/gz_accounting/gz_sp_setlist.do?returnvalue=dxt&b_query=link&flow_flag=1&gz_module=0" alt="薪资审批">
  <% }else{ %>
  <area shape="rect" coords="269,178,340,208" href="/module/gz/salarytemplate/SalaryTemplate.html?b_query=link&viewtype=1&imodule=0" alt="薪资审批">
  <% } %>

</hrms:priv>
<hrms:priv func_id="32404">

  <% if(ver<70){ %>
   <area shape="rect" coords="263,225,338,252" href="/gz/gz_accounting/tax/gz_tax_org_tree.do?returnvalue=dxt&b_query=link&is_back=not" alt="所得税管理">
   <% }else{ %>
   <area shape="rect" coords="263,225,338,252" href="/module/gz/tax/SearchTax.html" alt="所得税管理">
   <% } %>


</hrms:priv>
<%if(versionFlag==1){ %>
<hrms:priv func_id="32405">
  <area shape="rect" coords="266,321,340,349" href="/gz/gz_amount/gz_gross_tree.do?returnflag=dxt&b_query=link" alt="总额管理">
</hrms:priv>
<%} %>
<hrms:priv func_id="324071">
  <% if(ver<70){ %>
  	<area shape="rect" coords="500,132,572,158" href="/gz/gz_analyse/gzAnalyseList.do?returnflag=dxt&b_query=link&gz_module=0" alt="分析表">
  <% }else{ %>
   	<area shape="rect" coords="500,132,572,158" href="/module/gz/analysistables/analysistable/AnalysisTables.html?imodule=0" alt="分析表">
  <% } %>
 </hrms:priv>
 <%if(versionFlag==1){ %>
<hrms:priv func_id="324073">
  <area shape="rect" coords="491,225,568,256" href="/gz/gz_analyse/gz_fare/fare_analyse_orgtree.do?b_query=query&opt=init&type=0" alt="发放进展">
</hrms:priv>
<%} %>
<hrms:priv func_id="324072">
  <area shape="rect" coords="494,177,571,208" href="/gz/gz_analyse/gzAnalyseChart.do?returnvalue=dxt&br_query=link" alt="分析图">
 </hrms:priv>
</map>
<%} %>
</body>
</html>
