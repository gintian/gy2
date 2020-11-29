<%@ page contentType="text/html; charset=UTF-8" language="java"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ page import="java.util.ArrayList,java.util.HashMap,com.hjsj.hrms.actionform.hire.zp_option.stat.weekly.WeeklyStmtForm" %>
<style type="text/css">
<!--
.list_tb{width:100%;_width:96%;border-collapse:collapse; clear:both}
.list_tb a:visited{color:#660066; text-decoration:underline}
.list_tb a:hover{color:#FE6700;text-decoration:none}
.list_tb th,.list_tb .th td{
	background-position : center left;
	font-size: 12px;  
	BORDER-BOTTOM: #C4D8EE 0pt solid; 
	BORDER-LEFT: #C4D8EE 0pt solid; 
	BORDER-RIGHT: #C4D8EE 0pt solid; 
	BORDER-TOP: #C4D8EE 0pt solid;
	height:22px;
	font-weight: bold;
	background-color:#f4f7f7;	
	/*
	color:#336699;
	*/
	valign:middle;
}
.list_tb .th2 td{background:#FEFFD7;height:25px;line-height:25px}
.list_tb td{border-bottom:1px solid #DEE0EB;height:30px;line-height:16px;padding:2px 4px 2px 5px}
.list_tb a{margin-right:5px}
.list_tb th.chk{padding:0 4px 0 4px;width:20px; vertical-align:middle}
.list_tb th.icon{width:35px}
.list_tb th.time{width:75px}
.list_tb td.chk{text-align:center;width:20px;}
.list_tb td.icon{text-align:center}
.list_tb th.num{text-align:right;padding-right:5px}
.list_tb td.num{text-align:right;padding-right:5px}
.list_tb td.total{font-weight:bolder}
.con{
	color:#000;
	font-family: "宋体", Verdana;
	font-weight: bold;
	font-size: 14px
}
.weekday{
	color:#000;
	font-family: "宋体", Verdana;
	font-weight: bold;
	font-size: 13px
}
-->
</style>
<script language="javascript" src="/js/validate.js"></script>
<%
 WeeklyStmtForm weeklyStmtForm = (WeeklyStmtForm)session.getAttribute("weeklyStmtForm");
 ArrayList list = weeklyStmtForm.getAllList();

 %>
<html:form action="/hire/zp_option/stat/weekly/weeklyday">
<table width="96%" border="0" cellspacing="0" cellpadding="0" align="center" style="border-bottom: #C0C0C0 1px solid;">
	<tr>
		<td>&nbsp;</td>
	</tr>
	<tr>
		<td class="con">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<bean:message key="workdiary.message.job.statistics"/></td>
	</tr>
	<tr>
		<td>&nbsp;</td>
	</tr>
	<tr>
		<td>&nbsp;</td>
	</tr>
	<tr class='trShallow'> 
		<td class="weekday" height="25">
			&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<bean:write name="weeklyStmtForm" property="theweekday" filter="false" /><bean:message key="workdiary.message.data.statistics"/>
		</td>
	</tr>
</table>
<table width="96%" border="0" cellspacing="0" cellpadding="0" align="center" >
<tr>
<td align="center" id='pnl_0'>
<hrms:chart name="weeklyStmtForm" title="浏览者，申请者每日统计分析" scope="session" legends="dayApp" data=""  width="900" height="400" chart_type="" chartpnl='pnl_0'>
</hrms:chart>
</td>
</tr>
</table>
<table  align="center" class="list_tb">
	<tr>
		<th nowrap="nowrap" class="common_background_color" width="48%"><bean:write name="weeklyStmtForm" property="theweekday" filter="false" /></th>
		<th nowrap="nowrap" class="common_background_color" width="6%"><bean:message key="columns.archive.day"/></th>
		<th nowrap="nowrap" class="common_background_color" width="6%"><bean:message key="workdiary.message.one"/></th>
		<th nowrap="nowrap" class="common_background_color" width="6%"><bean:message key="workdiary.message.tow"/></th>
		<th nowrap="nowrap" class="common_background_color" width="6%"><bean:message key="workdiary.message.three"/></th>
		<th nowrap="nowrap" class="common_background_color" width="6%"><bean:message key="workdiary.message.four"/></th>
		<th nowrap="nowrap" class="common_background_color" width="6%"><bean:message key="workdiary.message.five"/></th>
		<th nowrap="nowrap" class="common_background_color" width="6%"><bean:message key="workdiary.message.six"/></th>
		<th nowrap="nowrap" class="common_background_color" width="10%"><bean:message key="planar.stat.total"/></th>
	</tr>
	<tr class='trShallow'>
		<td class="common_border_color"><bean:message key="hire.zp_option.weekly.Viewed"/></td>
		<td align="right" class="common_border_color"><bean:write name="weeklyStmtForm" property="view_sunday" filter="false" />&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</td>
		<td align="right" class="common_border_color"><bean:write name="weeklyStmtForm" property="view_monday" filter="false" />&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</td>
		<td align="right" class="common_border_color"><bean:write name="weeklyStmtForm" property="view_tuesday" filter="false" />&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</td>
		<td align="right" class="common_border_color"><bean:write name="weeklyStmtForm" property="view_wednesday" filter="false" />&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</td>
		<td align="right" class="common_border_color"><bean:write name="weeklyStmtForm" property="view_thursday" filter="false" />&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</td>
		<td align="right" class="common_border_color"><bean:write name="weeklyStmtForm" property="view_friday" filter="false" />&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</td>
		<td align="right" class="common_border_color"><bean:write name="weeklyStmtForm" property="view_saturday" filter="false" />&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</td>
		<td align="right" class="common_border_color"><bean:write name="weeklyStmtForm" property="sum_view" filter="false" />&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</td>
	 </tr>
	 <tr class='trShallow'>
		<td class="common_border_color"><bean:message key="hire.zp_option.weekly.apped"/></td>
		<td align="right" class="common_border_color"><bean:write name="weeklyStmtForm" property="app_sunday" filter="false" />&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</td>
		<td align="right" class="common_border_color"><bean:write name="weeklyStmtForm" property="app_monday" filter="false" />&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</td>
		<td align="right" class="common_border_color"><bean:write name="weeklyStmtForm" property="app_tuesday" filter="false" />&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</td>
		<td align="right" class="common_border_color"><bean:write name="weeklyStmtForm" property="app_wednesday" filter="false" />&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</td>
		<td align="right" class="common_border_color"><bean:write name="weeklyStmtForm" property="app_thursday" filter="false" />&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</td>
		<td align="right" class="common_border_color"><bean:write name="weeklyStmtForm" property="app_friday" filter="false" />&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</td>
		<td align="right" class="common_border_color"><bean:write name="weeklyStmtForm" property="app_saturday" filter="false" />&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</td>
		<td align="right" class="common_border_color"><bean:write name="weeklyStmtForm" property="sum_app" filter="false" />&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</td>
	 </tr>
</table>
<table width="80%" border="0" cellspacing="0" cellpadding="0" align="center">
<tr><td>&nbsp;</td></tr>
<tr>
  <td align="center">
    <html:button property="button1" styleClass="mybutton" onclick="javascript:history.go(-1);"><bean:message key="button.return"/></html:button>
  </td>
</tr>
</table>
</html:form>
