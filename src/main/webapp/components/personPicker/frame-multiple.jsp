<%@page import="com.hjsj.hrms.utils.PubFunc"%>
<%@page import="com.hrms.frame.utility.AdminCode"%>
<%@page import="java.sql.Connection"%>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="java.util.*,
				com.hrms.frame.utility.AdminDb,
				com.hjsj.hrms.utils.*,
				java.sql.Connection,
				com.hjsj.hrms.businessobject.sys.report.Sys_Oth_Parameter,
				com.hrms.hjsj.sys.DataDictionary" %>
<%@ page import="com.hrms.hjsj.sys.FieldItem" %>
<%
	//查询唯一性指标配置
	String uniquenessStr = "";
	Connection conn = null;
	try{
		conn = AdminDb.getConnection();
		Sys_Oth_Parameter sysbo=new Sys_Oth_Parameter(conn);
		String uniquenessvalid = sysbo.getCHKValue(Sys_Oth_Parameter.CHK_UNIQUENESS,"0","valid");//是否定义唯一性指标 0：没定义
		String onlyname = sysbo.getCHKValue(Sys_Oth_Parameter.CHK_UNIQUENESS,"0","name");//唯一性指标值
		if(!"0".equals(uniquenessvalid)&&onlyname!=null&&onlyname.length()>0){
			FieldItem item = DataDictionary.getFieldItem(onlyname);
			if(item != null){
				String uniqueness = DataDictionary.getFieldItem(onlyname).getItemdesc();
				uniquenessStr="{uniqueness-value}"+uniqueness+"{uniqueness-value}";
			}
		}
	}finally{
		PubFunc.closeDbObj(conn);
	}
%>
<table border="0" cellpadding="0" cellspacing="0">
	<colgroup>
		<col width="300" />
		<col width="260" />
	</colgroup>
	<!--<tr>
		<td style="height:32px;border-bottom:1px solid #ccc;">
			<span id="titleText" style="margin-left:11px"><strong></strong></span>
		</td>
		<td style="height:32px;border-bottom:1px solid #ccc;"> </td>
	</tr>-->
	<tr>
		<td style="width:300px;height:340px;">
			<div class="PersonPicker-Main" id="PersonPicker-Main" multiple="true">
				<!-- 关键字 -->
				<div class="PersonPicker-Main-Keyword" style="position:relative;">
					<input type="text" value="{search-input-text}<%= uniquenessStr %>" />
					<!-- <img src="/components/personPicker/image/clear.png" /> -->
				</div>
				
				<!-- 候选人(查询) -->
				<div class='PersonPicker-Main-Candidate' id="PersonPicker-Main-Candidate" style="display:none;"><ul></ul></div>
				
				<!-- 组织架构 -->
				<div class="PersonPicker-Main-Organization" id="PersonPicker-Main-Organization" style="display:block;">
					<p id="PersonPicker-Main-Organization_NodeDiv1">组织架构</p>
						<div id="PersonPicker-Main-Organization_NodeDiv2"></div>
				</div>
			</div>
		</td>
		<td style="position:relative;">
			<!-- 分割线 -->
			<div style="border-left:1px solid #ccc;height:292px;bottom:0px;left:0px;position:absolute;"></div>
			<div style="width:242px;height:340px;margin-left:10px;">
				<p style="height:46px;position:relative;">
					<span style="position:absolute;bottom:5px;bottom:3px\9;left:0px;color:#A1A1A1;">已选</span>
					<a style="position:absolute;bottom:5px;bottom:3px\9;right:0px;" id="clearChecked" class="AnchorBtn">清空已选</a>
				</p>
				<div class="PersonPicker-Multiple-CheckedPerson">
					<table style="border-collapse:collapse;color:#666;width:100%;font-size:12px;"><tbody></tbody></table>
				</div>
			</div>
		</td>
	</tr>
	<tr>
		<td colspan="2">
			<!-- 分割线 -->
			<div style="border-top:1px solid #ccc;width:540px;height:0px;margin-left:10px;margin-top:10px;"></div>
			<a class="PersonPicker-Multiple-AddBtn AnchorBtn" style="margin-bottom:10px;"></a>
		</td>
	</tr>
</table>
<!--<div style="position:absolute;right:23px;top:10px">
	<a>
		<img id="PersonPicker-Multiple-OnClose" src="/components/tableFactory/tableGrid-theme/images/tools/tool-sprites.gif" title="关闭"></img>
	</a>
</div>-->
