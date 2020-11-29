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
    <td align="center"> <img src="hcm/<%=themes %>/ct.jpg"  border="0" usemap="#Map"  hidefocus="true"> </td>
</tr>
</table>

<map name="Map" id="Map">
  <hrms:priv func_id="33001"> 
  	  <% if(ver<70){ %>
      <area shape="poly" coords="312,264,311,252,313,237,317,220,329,202,342,186,367,170,391,166,410,162,422,163,434,166,469,113,459,108,453,106,439,102,427,99,410,99,392,99,369,101,347,107,323,119,290,145,276,163,261,188,254,212,248,241,249,269,257,293,264,315,316,276" 
        href="/general/template/search_bs_tree.do?b_query=link&type=21&res_flag=7&module=15&dht=htbl" alt="合同办理" />
      <% }else{ %>  
      <area shape="poly" coords="312,264,311,252,313,237,317,220,329,202,342,186,367,170,391,166,410,162,422,163,434,166,469,113,459,108,453,106,439,102,427,99,410,99,392,99,369,101,347,107,323,119,290,145,276,163,261,188,254,212,248,241,249,269,257,293,264,315,316,276" 
        href="/module/template/templatenavigation/TemplateNavigation.html?b_query=link&sys_type=1&module_id=3" alt="合同办理" />
      <% } %>
  </hrms:priv>
  <hrms:priv func_id="33003">
      <area shape="poly" coords="265,318,317,280,338,315,350,327,371,339,391,345,417,343,437,340,462,325,506,370,503,368,466,396,426,406,380,407,344,394,321,382,297,363,279,343" 
        href="/ht/ctstatic/ctanalysis.do?b_tree=link&returnvalue=dxt" alt="统计分析" />
  </hrms:priv>
  <hrms:priv func_id="33002">
      <area shape="poly" coords="487,288,478,304,464,322,509,364,526,343,539,325,552,294,555,273,557,253,558,242,554,220,549,197,538,177,520,151,493,127,471,115,438,169,470,189,482,203,492,225,496,244,495,264" 
        href="/ht/inform/data_table.do?b_tree=link&returnvalue=dxt" alt="合同台账" />
  </hrms:priv>
  <hrms:priv func_id="33004">
      <area shape="poly" coords="319,232,317,243,314,254,322,284,334,307,353,324,372,337,404,342,433,337,463,319,480,296,491,272,491,234,476,200,463,188,435,169,407,165,374,171,360,177,345,189,333,201,321,221" 
        href="/ht/param/ht_param_menu.do?br_query=link&returnvalue=dxt" alt="基础设置" />
  </hrms:priv>
</map>
<%} %>
</body>
</html>
