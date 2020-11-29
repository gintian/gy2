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
    <td align="center"> <img src="hcm/<%=themes %>/performance.jpg"  border="0" usemap="#Map"> 
      <map name="Map">
        <hrms:priv func_id="3260601"> 
        <area shape="rect" coords="105,77,216,110" href="/performance/options/checkBodyObjectList.do?returnvalue=dxt&b_query=link&bodyType=0&returnflag=dxt&modelflag=performance" alt="主体类别">
        </hrms:priv> <hrms:priv func_id="3260602"> 
        <area shape="rect" coords="105,135,215,167" href="/performance/options/checkBodyObjectList.do?returnvalue=dxt&b_query=link&bodyType=1&returnflag=dxt&modelflag=performance" alt="对象类别">
        </hrms:priv> <hrms:priv func_id="3260603"> 
        <area shape="rect" coords="105,190,216,224" href="/performance/options/perKnowList.do?returnvalue=dxt&b_query=link&returnflag=dxt&modelflag=performance" alt="了解程度">
        </hrms:priv> <hrms:priv func_id="3260604"> 
        <area shape="rect" coords="105,246,215,277" href="/performance/options/perDegreeList.do?returnvalue=dxt&b_query=link&busitype=0&returnflag=dxt&modelflag=performance" alt="等级分类">
        </hrms:priv> <hrms:priv func_id="3260607"> 
        <area shape="rect" coords="105,359,216,393" href="/performance/options/kh_relation.do?returnvalue=dxt&br_int=link&returnflag=dxt&modelflag=performance" alt="考核关系">
        </hrms:priv> <hrms:priv func_id="3260605"> 
        <area shape="rect" coords="106,303,214,335" href="/performance/options/perParamList.do?returnvalue=dxt&b_query=link&returnflag=dxt&modelflag=performance" alt="评语模板">
        </hrms:priv> <hrms:priv func_id="3260606"> 
        <area shape="rect" coords="106,415,215,448" href="/performance/options/configParameter.do?b_query=link&busitype=0&returnflag=dxt&modelflag=performance" alt="配置参数">
        </hrms:priv> <hrms:priv func_id="32602"> 
        <area shape="rect" coords="375,243,485,277" href="/performance/kh_plan/khplanorgtree.do?returnvalue=dxt&b_query=link&busitype=0&flow_flag=0&gz_module=0&returnflag=dxt&modelflag=performance" alt="考核计划">
        </hrms:priv> <hrms:priv func_id="3260102"> 
        <area shape="rect" coords="375,160,485,193" href="/performance/kh_system/kh_template/kh_template_tree.do?returnflag=dxt&b_query=link&subsys_id=33&isVisible=1&method=0&templateId=-1&returnflag=dxt&modelflag=performance" alt="考核模板">
        </hrms:priv> <hrms:priv func_id="3260101"> 
        <area shape="rect" coords="374,78,485,111" href="/performance/kh_system/kh_field/kh_field_tree.do?returnflag=dxt&b_query=link&subsys_id=33&returnflag=dxt&modelflag=performance" alt="考核指标">
        </hrms:priv> <hrms:priv func_id="3260301"> 
        <area shape="rect" coords="605,217,715,250" href="/performance/kh_plan/performPlanList.do?returnflag=dxt&b_query=link&busitype=0&jxmodul=1&returnflag=dxt&modelflag=performance" alt="考核实施">
        </hrms:priv> <hrms:priv func_id="3260302"> 
        <area shape="rect" coords="605,273,713,306" href="/performance/kh_plan/performPlanList.do?returnvalue=dxt&b_query=link&busitype=0&jxmodul=3&returnflag=dxt&modelflag=performance" alt="数据采集">
        </hrms:priv> <hrms:priv func_id="32604"> 
        <area shape="rect" coords="604,355,714,386" href="/performance/kh_plan/performPlanList.do?returnvalue=dxt&b_query=link&busitype=0&jxmodul=2&returnflag=dxt&modelflag=performance" alt="绩效评估">
        </hrms:priv> <hrms:priv func_id="32605"> 
        <area shape="rect" coords="605,437,714,469" href="/performance/perAnalyse.do?returnvalue=dxt&br_query=link&busitype=0&returnflag=dxt&modelflag=performance" alt="绩效分析">
    </hrms:priv> </map></td>
</tr>
</table>
<%}else{ %>
<table width="80%" align="center" >
<tr>
    <td align="center"> <img src="performance.gif"  border="0" usemap="#Map"> 
      <map name="Map">
        <hrms:priv func_id="3260601"> 
        <area shape="rect" coords="32,60,115,91" href="/performance/options/checkBodyObjectList.do?returnvalue=dxt&b_query=link&bodyType=0&returnflag=dxt&modelflag=performance" alt="主体类别">
        </hrms:priv> <hrms:priv func_id="3260602"> 
        <area shape="rect" coords="25,113,107,137" href="/performance/options/checkBodyObjectList.do?returnvalue=dxt&b_query=link&bodyType=1&returnflag=dxt&modelflag=performance" alt="对象类别">
        </hrms:priv> <hrms:priv func_id="3260603"> 
        <area shape="rect" coords="32,156,110,188" href="/performance/options/perKnowList.do?returnvalue=dxt&b_query=link&returnflag=dxt&modelflag=performance" alt="了解程度">
        </hrms:priv> <hrms:priv func_id="3260604"> 
        <area shape="rect" coords="28,204,100,234" href="/performance/options/perDegreeList.do?returnvalue=dxt&b_query=link&busitype=0&returnflag=dxt&modelflag=performance" alt="等级分类">
        </hrms:priv> <hrms:priv func_id="3260607"> 
        <area shape="rect" coords="22,297,108,327" href="/performance/options/kh_relation.do?returnvalue=dxt&br_int=link&returnflag=dxt&modelflag=performance" alt="考核关系">
        </hrms:priv> <hrms:priv func_id="3260605"> 
        <area shape="rect" coords="26,251,112,281" href="/performance/options/perParamList.do?returnvalue=dxt&b_query=link&returnflag=dxt&modelflag=performance" alt="评语模板">
        </hrms:priv> <hrms:priv func_id="3260606"> 
        <area shape="rect" coords="29,344,105,374" href="/performance/options/configParameter.do?b_query=link&busitype=0&returnflag=dxt&modelflag=performance" alt="配置参数">
        </hrms:priv> <hrms:priv func_id="32602"> 
        <area shape="rect" coords="250,183,326,213" href="/performance/kh_plan/khplanorgtree.do?returnvalue=dxt&b_query=link&busitype=0&flow_flag=0&gz_module=0&returnflag=dxt&modelflag=performance" alt="考核计划">
        </hrms:priv> <hrms:priv func_id="3260102"> 
        <area shape="rect" coords="253,101,322,137" href="/performance/kh_system/kh_template/kh_template_tree.do?returnflag=dxt&b_query=link&subsys_id=33&isVisible=1&method=0&templateId=-1&returnflag=dxt&modelflag=performance" alt="考核模板">
        </hrms:priv> <hrms:priv func_id="3260101"> 
        <area shape="rect" coords="256,29,326,59" href="/performance/kh_system/kh_field/kh_field_tree.do?returnflag=dxt&b_query=link&subsys_id=33&returnflag=dxt&modelflag=performance" alt="考核指标">
        </hrms:priv> <hrms:priv func_id="3260301"> 
        <area shape="rect" coords="481,123,554,148" href="/performance/kh_plan/performPlanList.do?returnflag=dxt&b_query=link&busitype=0&jxmodul=1&returnflag=dxt&modelflag=performance" alt="考核实施">
        </hrms:priv> <hrms:priv func_id="3260302"> 
        <area shape="rect" coords="478,169,551,199" href="/performance/kh_plan/performPlanList.do?returnvalue=dxt&b_query=link&busitype=0&jxmodul=3&returnflag=dxt&modelflag=performance" alt="数据采集">
        </hrms:priv> <hrms:priv func_id="32604"> 
        <area shape="rect" coords="475,266,554,297" href="/performance/kh_plan/performPlanList.do?returnvalue=dxt&b_query=link&busitype=0&jxmodul=2&returnflag=dxt&modelflag=performance" alt="绩效评估">
        </hrms:priv> <hrms:priv func_id="32605"> 
        <area shape="rect" coords="479,345,549,375" href="/performance/perAnalyse.do?returnvalue=dxt&br_query=link&busitype=0&returnflag=dxt&modelflag=performance" alt="绩效分析">
        </hrms:priv> </map></td>
</tr>
</table>
<%} %>
</body>
</html>
