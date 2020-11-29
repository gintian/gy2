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
<div class="PersonPicker-Main-Keyword" style="position:relative;">
	<input type="text" value="{search-input-text}<%= uniquenessStr %>" />
	<!-- <img src="/components/personPicker/image/clear.png" /> -->
</div>
<div class='PersonPicker-Main-Candidate' id="PersonPicker-Main-Candidate" style="display:none;">
	<ul></ul>
</div>

<div class="PersonPicker-Main-Organization" id="PersonPicker-Main-Organization" style="display:block;">
	<p id="PersonPicker-Main-Organization_NodeDiv1">组织架构</p>
	<div id="PersonPicker-Main-Organization_NodeDiv2"></div>
</div>
