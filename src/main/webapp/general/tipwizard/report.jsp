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
    <td align="center"> <img src="hcm/<%=themes %>/report.jpg"  border="0" usemap="#Map"> 
      <map name="Map">
        <hrms:priv func_id="29011"> 
        <area shape="rect" coords="106,100,216,132" href="/report/auto_fill_report/options.do?returnvalue=dxt&b_query=link" alt="取数范围">
        </hrms:priv> <hrms:priv func_id="29010"> 
        <area shape="rect" coords="106,181,216,214" href="/report/auto_fill_report/reportlist.do?returnvalue=dxt&b_query=link&sortId=-1&print=5&checkFlag=0" alt="提取数据">
        </hrms:priv> <hrms:priv func_id="2902"> 
        <area shape="rect" coords="105,263,216,295" href="/report/edit_report/reportSettree.do?returnvalue=dxt" alt="编辑报表">
        </hrms:priv> <hrms:priv func_id="29052"> 
        <area shape="rect" coords="106,370,216,403" href="/report/report_state/reportunittree.do?b_init=init&returnvalue=dxt" alt="按组织机构查询">
        </hrms:priv> <hrms:priv func_id="29053"> 
        <area shape="rect" coords="106,414,216,445" href="/report/report_status.do?returnvalue=dxt&b_query=query&opt=init" alt="按表分类查询">
        </hrms:priv> <hrms:priv func_id="29034"> 
        <area shape="rect" coords="375,363,485,396" href="/report/report_pigeonhole/reportBatchPigeonhole.do?returnvalue=dxt&b_int=int" alt="报表归档">
        </hrms:priv> <hrms:priv func_id="29032"> 
        <area shape="rect" coords="375,308,483,339" href="/report/report_collect/reportOrgCollecttree.do?returnvalue=dxt&b_init=int" alt="编辑报表">
        </hrms:priv> <hrms:priv func_id="29031"> 
        <area shape="rect" coords="376,250,485,283" href="/report/edit_collect/reportCollect.do?returnvalue=dxt&b_initCollect=link&sortid=@" alt="报表汇总">
        </hrms:priv> <hrms:priv func_id="29033"> 
        <area shape="rect" coords="376,193,485,226" href="/report/edit_report/sendReceiveView.do?returnvalue=dxt&b_query=b_query" alt="表式收发">
        </hrms:priv> <hrms:priv func_id="29030"> 
        <area shape="rect" coords="375,135,484,167" href="/report/edit_report/receive_report/receive_report.jsp?returnvalue=dxt" alt="接收报盘">
        </hrms:priv> <hrms:priv func_id="29050"> 
        <area shape="rect" coords="605,135,713,167" href="/report/org_maintenance/reportunittree.do?returnvalue=dxt" alt="填报单位">
        </hrms:priv> <hrms:priv func_id="29040"> 
        <area shape="rect" coords="604,190,715,223" href="/report/report_analyse/reportunittree.do?returnvalue=dxt" alt="报表分析">
    </hrms:priv> </map></td>
</tr>
</table>
<%}else{ %>
<table width="80%" align="center" >
<tr>
    <td align="center"> <img src="report.gif"  border="0" usemap="#Map"> 
      <map name="Map">
        <hrms:priv func_id="29011"> 
        <area shape="rect" coords="33,69,104,103" href="/report/auto_fill_report/options.do?returnvalue=dxt&b_query=link" alt="取数范围">
        </hrms:priv> <hrms:priv func_id="29010"> 
        <area shape="rect" coords="27,142,99,171" href="/report/auto_fill_report/reportlist.do?returnvalue=dxt&b_query=link&sortId=-1&print=5&checkFlag=0" alt="提取数据">
        </hrms:priv> <hrms:priv func_id="2902"> 
        <area shape="rect" coords="27,226,107,251" href="/report/edit_report/reportSettree.do?returnvalue=dxt" alt="编辑报表">
        </hrms:priv> <hrms:priv func_id="29052"> 
        <area shape="rect" coords="33,338,100,365" href="/report/report_state/reportunittree.do?b_init=init&returnvalue=dxt" alt="按组织机构查询">
        </hrms:priv> <hrms:priv func_id="29053"> 
        <area shape="rect" coords="29,385,96,412" href="/report/report_status.do?returnvalue=dxt&b_query=query&opt=init" alt="按表分类查询">
        </hrms:priv> <hrms:priv func_id="29034"> 
        <area shape="rect" coords="268,304,333,332" href="/report/report_pigeonhole/reportBatchPigeonhole.do?returnvalue=dxt&b_int=int" alt="报表归档">
        </hrms:priv> <hrms:priv func_id="29032"> 
        <area shape="rect" coords="263,241,331,271" href="/report/report_collect/reportOrgCollecttree.do?returnvalue=dxt&b_init=int" alt="编辑报表">
        </hrms:priv> <hrms:priv func_id="29031"> 
        <area shape="rect" coords="267,184,337,215" href="/report/edit_collect/reportCollect.do?returnvalue=dxt&b_initCollect=link&sortid=@" alt="报表汇总">
        </hrms:priv> <hrms:priv func_id="29033"> 
        <area shape="rect" coords="269,128,333,158" href="/report/edit_report/sendReceiveView.do?returnvalue=dxt&b_query=b_query" alt="表式收发">
        </hrms:priv> <hrms:priv func_id="29030"> 
        <area shape="rect" coords="268,67,337,96" href="/report/edit_report/receive_report/receive_report.jsp?returnvalue=dxt" alt="接收报盘">
        </hrms:priv> <hrms:priv func_id="29050"> 
        <area shape="rect" coords="497,34,567,66" href="/report/org_maintenance/reportunittree.do?returnvalue=dxt" alt="填报单位">
        </hrms:priv> <hrms:priv func_id="29040"> 
        <area shape="rect" coords="489,90,563,123" href="/report/report_analyse/reportunittree.do?returnvalue=dxt" alt="报表分析">
        </hrms:priv> </map></td>
</tr>
</table>
<%} %>
</body>
</html>
