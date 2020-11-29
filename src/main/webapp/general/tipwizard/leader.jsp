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
    <td align="center"> <img src="hcm/<%=themes %>/leader.jpg"  border="0" usemap="#Map"> </td>
</tr>
</table>
<map name="Map">
 
  <area shape="rect" coords="147,91,256,122" href="/general/deci/leader/leaderframe.do?returnvalue=dxt&b_query=link&param=1" alt="领导班子">
  
  <area shape="rect" coords="427,91,536,125" href="/general/static/commonstatic/statshow.do?returnvalue=leaderdxt&b_ini=link&infokind=1&home=leaderdxt" alt="人员统计">
 
  <area shape="rect" coords="148,282,256,315" href="/general/inform/org/searchorgbrowse.do?returnvalue=leaderdxt&b_query=link&droit=0" alt="机构信息">
 
   <area shape="rect" coords="426,415,535,449" href="/general/deci/browser/much/much_field_analyse.do?br_query=link" alt="多指标分析">
 
   <area shape="rect" coords="427,344,534,378" href="/general/deci/statics/loademploymakeupanalyse.do?returnvalue=leaderdxt&b_search=link" alt="人员结构分析">
   
   <area shape="rect" coords="149,155,258,186" href="/general/deci/leader/leaderframe.do?returnvalue=dxt&b_query=link&param=2" alt="后备干部">
  
  <area shape="rect" coords="566,91,675,125" href="/general/static/commonstatic/statshow.do?returnvalue=dxt&b_ini=link&infokind=2&home=leaderdxt" alt="单位统计">
 
  <area shape="rect" coords="149,348,257,381" href="/general/inform/org/map/searchorgmap.do?returnvalue=leaderdxt&b_search=link" alt="机构图">
  
  <area shape="rect" coords="567,345,674,379" href="/general/deci/browser/single/single_field_analyse.do?br_query=link" alt="单指标分析">
   
   <area shape="rect" coords="149,219,258,250" href="/workbench/browse/showinfo.do?returns=leaderdxt&b_search=link&action=showinfodata.do&target=nil_body&userbase=&flag=noself&isphotoview=" alt="人员信息">
  
  <area shape="rect" coords="426,164,535,198" href="/general/static/commonstatic/statshow.do?returnvalue=dxt&b_ini=link&infokind=3&home=leaderdxt" alt="岗位统计">
 
  <area shape="rect" coords="149,410,257,443" href="/general/inform/synthesisbrowse.do?b_dbname=link" alt="综合信息">
  
  <area shape="rect" coords="567,415,674,449" href="/report/report_analyse/reportunittree.do" alt="报表分析">
  
</map>
<%} %>
</body>
</html>
