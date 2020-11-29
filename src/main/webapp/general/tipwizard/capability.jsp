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
    <td align="center"> <img src="hcm/<%=themes %>/capability.jpg"  border="0" usemap="#Map"> </td>
</tr>
</table>
<map name="Map">
 
  <area shape="rect" coords="2,100,111,131" href="/performance/options/checkBodyObjectList.do?b_query=link&bodyType=0&returnflag=dxt&modelflag=capability" alt="主体类别">
  
  <area shape="rect" coords="1,164,110,198" href="/performance/options/checkBodyObjectList.do?b_query=link&bodyType=1&returnflag=dxt&modelflag=capability" alt="对象类别">
 
  <area shape="rect" coords="3,228,111,261" href="/performance/options/perKnowList.do?b_query=link&returnflag=dxt&modelflag=capability" alt="了解程度">
 
   <area shape="rect" coords="1,292,110,326" href="/performance/options/perDegreeList.do?b_query=link&amp;busitype=1&amp;&returnflag=dxt&modelflag=capability" alt="等级分类">
 
 <area shape="rect" coords="2,356,110,392" href="/performance/options/perParamList.do?b_query=link&returnflag=dxt&modelflag=capability" alt="评语模板">

  <area shape="rect" coords="3,422,112,453" href="/performance/options/configParameter.do?b_query=link&busitype=1&returnflag=dxt&returnvalue=dxt&modelflag=capability" alt="配置参数">
 
   <area shape="rect" coords="231,103,340,135" href="/performance/kh_system/kh_field/kh_field_tree.do?b_query=link&amp;subsys_id=35&amp;returnflag=dxt&modelflag=capability" alt="素质指标">
 
  <area shape="rect" coords="230,211,340,245" href="/competencymodal/postseq_commodal/post_modal_tree.do?b_tree=tree&amp;object_type=2&amp;historyType=1&returnflag=dxt&returnvalue=dxt" alt="岗位序列素质模型">

  <area shape="rect" coords="357,270,466,302" href="/competencymodal/postseq_commodal/post_modal_tree.do?b_tree=tree&amp;object_type=3&amp;historyType=1&returnflag=dxt&returnvalue=dxt" target="il_body" alt="岗位素质模型">

  <area shape="rect" coords="356,362,465,396" href="/performance/kh_plan/performPlanList.do?b_query=link&amp;busitype=1&amp;jxmodul=1&amp;returnflag=dxt" alt="评估实施">
 
  <area shape="rect" coords="203,398,312,431" href="/performance/kh_plan/khplanorgtree.do?b_query=link&amp;busitype=1&amp;flow_flag=0&amp;gz_module=0&amp;returnflag=dxt" alt="评估计划">
 
  <area shape="rect" coords="478,104,586,137" href="/performance/kh_system/kh_template/kh_template_tree.do?b_query=link&amp;subsys_id=35&amp;isVisible=1&amp;method=0&amp;templateId=-1&amp;returnflag=dxt" alt="测评量表">
 
  <area shape="rect" coords="475,210,585,244" href="/competencymodal/postseq_commodal/post_modal_tree.do?b_tree=tree&amp;object_type=1&amp;historyType=1&returnflag=dxt&returnvalue=dxt" alt="职务序列素质模型">

  <area shape="rect" coords="695,423,805,457" href="/performance/perAnalyse.do?b_personStation0=query0&amp;busitype=1&amp;returnflag=dxt" alt="岗位分析" />
  
  <area shape="rect" coords="696,100,806,130" href="/performance/perAnalyse.do?br_query=link&amp;busitype=1&amp;returnflag=dxt" alt="结果分析" />
  
  <area shape="rect" coords="356,437,465,470" href="/performance/kh_plan/performPlanList.do?b_query=link&amp;busitype=1&amp;jxmodul=3&amp;returnflag=dxt" alt="数据采集" />
  
  <area shape="rect" coords="511,398,619,430" href="/performance/kh_plan/performPlanList.do?b_query=link&amp;busitype=1&amp;jxmodul=2&amp;returnflag=dxt" alt="评估打算" />
</map>
<%} %>
</body>
</html>
