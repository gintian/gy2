<%@ page contentType="text/html; charset=UTF-8" language="java"%>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ page import="com.hrms.struts.valueobject.UserView"%>
<%@ page import="com.hrms.struts.constant.WebConstant,com.hjsj.hrms.businessobject.sys.SysParamBo"%>
<%
    UserView userView=(UserView)session.getAttribute(WebConstant.userView);
    String bosflag="";
    int ver=userView.getVersion(); //锁版本
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
    <td align="center"> <img src="hcm/<%=themes %>/insurance.jpg"  border="0" usemap="#Map"> </td>
</tr>
</table>
<map name="Map">
   <hrms:priv func_id="32505"> 
   <% if(ver<70){ %>
  <area shape="rect" coords="104,140,214,172" href="/gz/templateset/gz_templatelist.do?returnvalue=dxt&b_query=link0&gz_module=1" alt="险种类别">
   <% }else{ %>
  <area shape="rect" coords="104,140,214,172" href="/module/gz/salarytype/SalaryType.html?b_query=link&imodule=1&returnvalue=1" alt="险种类别">
 	<% } %>
 </hrms:priv>
<hrms:priv func_id="32506"> 
  <area shape="rect" coords="105,244,214,274" href="/org/gzdatamaint/gzdatamaint.do?returnflag=dxt&b_addsubclass=link&tagname=1&infor=2&gzflag=3" alt="相关子集">
 </hrms:priv>
<hrms:priv func_id="32507"> 
  <area shape="rect" coords="105,346,213,377" href="/org/gzdatamaint/gz_org_tree.do?returnflag=dxt&b_query=link&infor=2&gzflag=3" alt="基础数据">
 </hrms:priv>
<hrms:priv func_id="32501"> 
  <% if(ver<70){ %>
  <area shape="rect" coords="386,159,494,191" href="/general/template/search_bs_tree.do?returnvalue=dxt&b_query=link&type=8&res_flag=17" alt="保险核定">
 <% }else{ %>
 
 <area shape="rect" coords="386,159,494,191" href="/module/template/templatenavigation/TemplateNavigation.html?b_query=link&sys_type=1&module_id=4" alt="保险核定">
 <% } %>
 </hrms:priv>
<hrms:priv func_id="32502"> 
 <% if(ver<70){ %>
  <area shape="rect" coords="385,264,494,297" href="/gz/gz_accounting/gz_set_list.do?returnvalue=dxt&b_query=link&flow_flag=0&gz_module=1" alt="缴费核算">
  <% }else{ %>
  <area shape="rect" coords="385,264,494,297" href="/module/gz/salarytemplate/SalaryTemplate.html?b_query=link&viewtype=0&imodule=1" alt="缴费核算">
  <% } %>
 
 </hrms:priv>
<hrms:priv func_id="32503"> 
<% if(ver<70){ %>
  <area shape="rect" coords="385,320,495,353" href="/gz/gz_accounting/gz_sp_setlist.do?returnvalue=dxt&b_query=link&flow_flag=1&gz_module=1" alt="缴费审批">
<% }else{ %>
  <area shape="rect" coords="385,320,495,353" href="/module/gz/salarytemplate/SalaryTemplate.html?b_query=link&viewtype=1&imodule=1" alt="缴费审批">
<% } %>
</hrms:priv>
<hrms:priv func_id="32504">   
	<% if(ver<70){ %>
	  	<area shape="rect" coords="611,321,720,355" href="/gz/gz_analyse/gzAnalyseList.do?returnflag=dxt&b_query=link&gz_module=1" alt="保险分析表">
	<% }else{ %>
		<area shape="rect" coords="611,321,720,355" href="/module/gz/analysistables/analysistable/AnalysisTables.html?imodule=1" alt="保险分析表">
	<% } %>
 </hrms:priv>
</map>
<%}else{ %>
<table width="80%" align="center" >
<tr>
    <td align="center"> <img src="insurance.gif"  border="0" usemap="#Map"> </td>
</tr>
</table>
<map name="Map">
  <hrms:priv func_id="32505"> 
 
    <% if(ver<70){ %>
   <area shape="rect" coords="32,70,109,100" href="/gz/templateset/gz_templatelist.do?returnvalue=dxt&b_query=link0&gz_module=1" alt="险种类别">
   <% }else{ %>
    <area shape="rect" coords="32,70,109,100" href="/module/gz/salarytype/SalaryType.html?b_query=link&imodule=1&returnvalue=1" alt="险种类别"> 
   <% } %>
 
 
 </hrms:priv>
<hrms:priv func_id="32506"> 
  <area shape="rect" coords="26,144,109,173" href="/org/gzdatamaint/gzdatamaint.do?returnflag=dxt&b_addsubclass=link&tagname=1&infor=2&gzflag=3" alt="相关子集">
 </hrms:priv>
<hrms:priv func_id="32507"> 
  <area shape="rect" coords="26,217,104,247" href="/org/gzdatamaint/gz_org_tree.do?returnflag=dxt&b_query=link&infor=2&gzflag=3" alt="基础数据">
 </hrms:priv>
<hrms:priv func_id="32501">  
 <% if(ver<70){ %>
  <area shape="rect" coords="267,27,346,58" href="/general/template/search_bs_tree.do?returnvalue=dxt&b_query=link&type=8&res_flag=17" alt="保险业务">
 <% }else{ %>
  <area shape="rect" coords="267,27,346,58" href="/module/template/templatenavigation/TemplateNavigation.html?b_query=link&sys_type=1&module_id=4" alt="保险业务"> 
 <% } %> 
 </hrms:priv>
<hrms:priv func_id="32502"> 
  <% if(ver<70){ %>
  <area shape="rect" coords="261,162,342,192" href="/gz/gz_accounting/gz_set_list.do?returnvalue=dxt&b_query=link&flow_flag=0&gz_module=1" alt="缴费核算">
  <% }else{ %>
  <area shape="rect" coords="261,162,342,192" href="/module/gz/salarytemplate/SalaryTemplate.html?b_query=link&viewtype=0&imodule=1" alt="缴费核算">
  <% } %>
  
  
 </hrms:priv>
<hrms:priv func_id="32503"> 
<% if(ver<70){ %>
  <area shape="rect" coords="262,223,345,252" href="/gz/gz_accounting/gz_sp_setlist.do?returnvalue=dxt&b_query=link&flow_flag=1&gz_module=1" alt="缴费审批">
<% }else{ %>
  <area shape="rect" coords="262,223,345,252" href="/module/gz/salarytemplate/SalaryTemplate.html?b_query=link&viewtype=1&imodule=1" alt="缴费审批">  
<% } %>

  
 </hrms:priv>
<hrms:priv func_id="32504">   
	<% if(ver<70){ %>
	  	<area shape="rect" coords="490,193,572,223" href="/gz/gz_analyse/gzAnalyseList.do?returnflag=dxt&b_query=link&gz_module=1" alt="保险分析表">
	<% }else{ %>
		<area shape="rect" coords="490,193,572,223" href="/module/gz/analysistables/analysistable/AnalysisTables.html?imodule=1" alt="保险分析表">
	<% } %>
 </hrms:priv>
</map>
<%} %>
</body>
</html>
