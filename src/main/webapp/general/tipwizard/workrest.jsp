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
    <td align="center"> <img src="hcm/<%=themes %>/workrest.jpg"  border="0" usemap="#Map"> 
      <map name="Map">
        <hrms:priv func_id="27010"> 
        <area shape="rect" coords="18,89,127,121" href="/kq/app_check_in/all_app.do?returnvalue=dxt&b_query=link&action=all_app_data.do&target=mil_body&viewPost=kq&table=Q11" alt="加班申请">
        </hrms:priv> <hrms:priv func_id="27011"> 
        <area shape="rect" coords="19,169,127,201" href="/kq/app_check_in/all_app.do?returnvalue=dxt&b_query=link&action=all_app_data.do&target=mil_body&viewPost=kq&table=Q15" alt="请假申请">
        </hrms:priv> <hrms:priv func_id="27012"> 
        <area shape="rect" coords="19,248,129,280" href="/kq/app_check_in/all_app.do?returnvalue=dxt&b_query=link&action=all_app_data.do&target=mil_body&viewPost=kq&table=Q13" alt="公出申请">
        </hrms:priv> <hrms:priv func_id="27013"> 
        <area shape="rect" coords="20,327,127,361" href="/kq/app_check_in/exchange_class/exchange.do?returnvalue=dxt&b_search=link&action=exchangedata.do&target=mil_body&viewPost=kq" alt="调班申请">
        </hrms:priv> <hrms:priv func_id="27014"> 
        <area shape="rect" coords="19,407,129,440" href="/kq/app_check_in/redeploy_rest/redeploy.do?returnvalue=dxt&b_search=link&action=redeploydata.do&target=mil_body&viewPost=kq" alt="调休申请">
        </hrms:priv> <hrms:priv func_id="2702025"> 
        <area shape="rect" coords="250,286,358,319" href="/kq/register/search_register.do?returnvalue=dxt&b_search=link&action=search_registerdata.do&target=mil_body&viewPost=kq&flag=noself" alt="月末处理">
        </hrms:priv> <hrms:priv func_id="27020"> 
        <area shape="rect" coords="250,207,358,240" href="/kq/register/daily_register.do?returnvalue=dxt&b_search=link&action=daily_registerdata.do&target=mil_body&viewPost=kq&flag=noself" alt="员工明细数据">
        </hrms:priv> <hrms:priv func_id="2704"> 
        <!-- 
        <area shape="rect" coords="249,89,359,121" href="/kq/feast_manage/manager.do?returnvalue=dxt&b_search=link&action=hols_manager.do&target=mil_body&flag=noself&viewPost=kq&kind=2" alt="假期管理">
         -->
        <area shape="rect" coords="249,89,359,121" href="/module/kq/holiday/Holiday.html" alt="假期管理">
        </hrms:priv> <hrms:priv func_id="2708"> 
        <area shape="rect" coords="447,89,556,121" href="/kq/options/manager/usermanager.do?returnvalue=dxt&b_search=link&action=usermanagerdata.do&target=mil_body&viewPost=kq&flag=noself&menu=1" alt="人员基本信息">
        </hrms:priv> <hrms:priv func_id="27070"> 
        <area shape="rect" coords="448,169,558,202" href="/kq/team/array/search_array.do?returnvalue=dxt&b_query=link&action=search_array_data.do&target=mil_body&viewPost=kq" alt="排班管理">
        </hrms:priv> <hrms:priv func_id="27062"> 
        <area shape="rect" coords="449,248,558,280" href="/kq/machine/analyse/data_analyse.do?returnvalue=dxt&b_query=link&action=data_analyse_data.do&target=mil_body&viewPost=kq" alt="数据处理">
        </hrms:priv> <hrms:priv func_id="27060"> 
        <area shape="rect" coords="449,327,556,361" href="/kq/machine/search_card.do?returnvalue=dxt&b_query=link&action=search_card_data.do&target=mil_body&viewPost=kq" alt="刷卡数据">
        </hrms:priv> <hrms:priv func_id="27061"> 
        <area shape="rect" coords="448,407,557,440" href="/kq/machine/kq_rule.do?returnvalue=dxt&b_query=link&action=kq_rule_data.do&target=mil_body" alt="文件规则">
        </hrms:priv> <hrms:priv func_id="27030"> 
        <area shape="rect" coords="672,89,782,119" href="/kq/options/struts/select_parameter.do?returnvalue=dxt&b_query=link" alt="结构参数">
        </hrms:priv> <hrms:priv func_id="27031"> 
        <area shape="rect" coords="672,152,782,185" href="/kq/options/duration_detail.do?returnvalue=dxt" alt="考勤期间">
        </hrms:priv> <hrms:priv func_id="27032"> 
        <area shape="rect" coords="673,215,781,247" href="/kq/options/search_feast.do?returnvalue=dxt&b_query=link" alt="节假日">
        </hrms:priv> <hrms:priv func_id="27034"> 
        <area shape="rect" coords="671,280,782,312" href="/kq/options/search_rest.do?returnvalue=dxt&b_query=link&mege=4" alt="公休日">
        </hrms:priv> <hrms:priv func_id="27033"> 
        <area shape="rect" coords="672,342,783,375" href="/kq/options/kq_item_detail.do?returnvalue=dxt" alt="考勤规则">
        </hrms:priv> <hrms:priv func_id="27038"> 
        <area shape="rect" coords="672,407,779,439" href="/kq/options/class/kq_class.do?returnvalue=dxt" alt="基本班次">
    </hrms:priv>  </map></td>
</tr>
</table>
<%}else{ %>
<table width="80%" align="center" >
<tr>
    <td align="center"> <img src="workrest.gif"  border="0" usemap="#Map"> 
      <map name="Map">
        <hrms:priv func_id="27010"> 
        <area shape="rect" coords="32,64,115,97" href="/kq/app_check_in/all_app.do?returnvalue=dxt&b_query=link&action=all_app_data.do&target=mil_body&viewPost=kq&table=Q11" alt="加班申请">
        </hrms:priv> <hrms:priv func_id="27011"> 
        <area shape="rect" coords="35,120,108,152" href="/kq/app_check_in/all_app.do?returnvalue=dxt&b_query=link&action=all_app_data.do&target=mil_body&viewPost=kq&table=Q15" alt="请假申请">
        </hrms:priv> <hrms:priv func_id="27012"> 
        <area shape="rect" coords="40,183,116,216" href="/kq/app_check_in/all_app.do?returnvalue=dxt&b_query=link&action=all_app_data.do&target=mil_body&viewPost=kq&table=Q13" alt="公出申请">
        </hrms:priv> <hrms:priv func_id="27013"> 
        <area shape="rect" coords="38,239,109,268" href="/kq/app_check_in/exchange_class/exchange.do?returnvalue=dxt&b_search=link&action=exchangedata.do&target=mil_body&viewPost=kq" alt="调班申请">
        </hrms:priv> <hrms:priv func_id="27014"> 
        <area shape="rect" coords="32,305,104,332" href="/kq/app_check_in/redeploy_rest/redeploy.do?returnvalue=dxt&b_search=link&action=redeploydata.do&target=mil_body&viewPost=kq" alt="调休申请">
        </hrms:priv> <hrms:priv func_id="2702025"> 
        <area shape="rect" coords="269,236,341,267" href="/kq/register/search_register.do?returnvalue=dxt&b_search=link&action=search_registerdata.do&target=mil_body&viewPost=kq&flag=noself" alt="月末处理">
        </hrms:priv> <hrms:priv func_id="27020"> 
        <area shape="rect" coords="260,167,337,196" href="/kq/register/daily_register.do?returnvalue=dxt&b_search=link&action=daily_registerdata.do&target=mil_body&viewPost=kq&flag=noself" alt="员工明细数据">
        </hrms:priv> <hrms:priv func_id="2704"> 
        <area shape="rect" coords="262,29,337,59" href="/kq/feast_manage/manager.do?returnvalue=dxt&b_search=link&action=hols_manager.do&target=mil_body&flag=noself&viewPost=kq&kind=2" alt="假期管理">
        </hrms:priv> <hrms:priv func_id="2708"> 
        <area shape="rect" coords="481,25,561,58" href="/kq/options/manager/usermanager.do?returnvalue=dxt&b_search=link&action=usermanagerdata.do&target=mil_body&viewPost=kq&flag=noself&menu=1" alt="人员基本信息">
        </hrms:priv> <hrms:priv func_id="27070"> 
        <area shape="rect" coords="484,103,563,131" href="/kq/team/array/search_array.do?returnvalue=dxt&b_query=link&action=search_array_data.do&target=mil_body&viewPost=kq&privtype=kq" alt="排班管理">
        </hrms:priv> <hrms:priv func_id="27062"> 
        <area shape="rect" coords="485,190,559,218" href="/kq/machine/analyse/data_analyse.do?returnvalue=dxt&b_query=link&action=data_analyse_data.do&target=mil_body&viewPost=kq" alt="数据处理">
        </hrms:priv> <hrms:priv func_id="27060"> 
        <area shape="rect" coords="478,259,552,290" href="/kq/machine/search_card.do?returnvalue=dxt&b_query=link&action=search_card_data.do&target=mil_body&viewPost=kq" alt="刷卡数据">
        </hrms:priv> <hrms:priv func_id="27061"> 
        <area shape="rect" coords="482,348,562,376" href="/kq/machine/kq_rule.do?returnvalue=dxt&b_query=link&action=kq_rule_data.do&target=mil_body" alt="文件规则">
        </hrms:priv> <hrms:priv func_id="27030"> 
        <area shape="rect" coords="699,69,789,103" href="/kq/options/struts/select_parameter.do?returnvalue=dxt&b_query=link" alt="结构参数">
        </hrms:priv> <hrms:priv func_id="27031"> 
        <area shape="rect" coords="699,115,787,146" href="/kq/options/duration_detail.do?returnvalue=dxt" alt="考勤期间">
        </hrms:priv> <hrms:priv func_id="27032"> 
        <area shape="rect" coords="701,162,787,199" href="/kq/options/search_feast.do?returnvalue=dxt&b_query=link" alt="节假日">
        </hrms:priv> <hrms:priv func_id="27034"> 
        <area shape="rect" coords="700,215,792,242" href="/kq/options/search_rest.do?returnvalue=dxt&b_query=link&mege=4" alt="公休日">
        </hrms:priv> <hrms:priv func_id="27033"> 
        <area shape="rect" coords="708,262,789,288" href="/kq/options/kq_item_detail.do?returnvalue=dxt" alt="考勤规则">
        </hrms:priv> <hrms:priv func_id="27038"> 
        <area shape="rect" coords="694,306,783,342" href="/kq/options/class/kq_class.do?returnvalue=dxt" alt="基本班次">
        </hrms:priv> </map></td>
</tr>
</table>
<%} %>
</body>
</html>
