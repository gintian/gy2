<%@ page contentType="text/html; charset=UTF-8" language="java"%>
<%@page import="com.hrms.struts.valueobject.UserView"%>
<%@page import="com.hrms.struts.constant.WebConstant"%>
<%@page import="com.hrms.frame.utility.DateStyle"%>
<%@page import="com.hrms.struts.constant.SystemConfig"%>
<%@page import="com.hrms.frame.dbstruct.DbWizard"%>
<%@page import="com.hrms.frame.utility.AdminDb"%>
<%@page import="java.sql.Connection"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<link rel="stylesheet" href="/css/css1.css" type="text/css">
<link rel="stylesheet" type="text/css" href="/ajax/skin.css"></link>
<script language="javascript" src="/ajax/constant.js"></script>
<script language="javascript" src="/ajax/basic.js"></script>
<script language="javascript" src="/ajax/common.js"></script>
<script language="javascript" src="/ajax/control.js"></script>
<script language="javascript" src="/ajax/dataset.js"></script>
<script language="javascript" src="/ajax/editor.js"></script>
<script language="javascript" src="/ajax/dropdown.js"></script>
<script language="javascript" src="/ajax/table.js"></script>
<script language="javascript" src="/ajax/menu.js"></script>
<script language="javascript" src="/ajax/tree.js"></script>
<script language="javascript" src="/ajax/pagepilot.js"></script>
<script language="javascript" src="/ajax/command.js"></script>
<script language="javascript" src="/ajax/format.js"></script>
<script language="JavaScript" src="/js/validate.js"></script>
<script language="JavaScript" src="/js/function.js"></script>
<%
	//在标题栏显示当前用户和日期 2004-5-10 
	String userName = null;
	String css_url="/css/css1.css";
	UserView userView=(UserView)session.getAttribute(WebConstant.userView);
	if(userView != null)
	{
	  userName = userView.getUserFullName();
	  css_url=userView.getCssurl();
	  if(css_url==null||css_url.equals(""))
	  	css_url="/css/css1.css";
	}
	String date = DateStyle.getSystemDate().getDateString();
	int flag=1;
	String webserver=SystemConfig.getPropertyValue("webserver");
	if(webserver.equalsIgnoreCase("websphere"))
		flag=2;
	
	String emporg = request.getParameter("emporg");
	String unique_ids = request.getParameter("unique_ids");
	String table = "";
	if("1".equals(emporg))
		table = "t_hr_view";
	else if("2".equals(emporg))
		table = "t_org_view";
	else if("3".equals(emporg))
		table = "t_post_view";
	boolean isShow = true;
	Connection conn = null;
	try{
		conn = AdminDb.getConnection();
		DbWizard dbw = new DbWizard(conn);
		if(table.length()<5 || !dbw.isExistTable(table,false))
			isShow = false;
	}catch(Exception e){
		e.printStackTrace();
	}finally{
		if(conn!=null)
			conn.close();
	}
%>
<hrms:themes></hrms:themes>
<script type="text/javascript">
	var _checkBrowser=true;
	var _disableSystemContextMenu=false;
	var _processEnterAsTab=true;
	var _showDialogOnLoadingData=true;
	var _enableClientDebug=true;
	var _theme_root="/ajax/images";
	var _application_root="";
	var __viewInstanceId="968";
	var ViewProperties=new ParameterSet();
	var webserver=<%=flag%>;
	
	
	function editSync(){
		/*var ids = "";
		var sels = document.getElementsByName("selbox");
		for(var i=0;sels!=null&&i<sels.length;i++){
			if(sels[i].checked)
				ids += sels[i].value + ",";
		}*/
		var radio = "";
		var radios = document.getElementsByName("radiobutton");
		for(var i = 0;i<radios.length;i++){
            if(radios[i].checked)
                radio = radios[i].value;
        }
		if(radio==0){
			var unique_ids = "<%=unique_ids%>";
			var unique_id=unique_ids.replace(",,", ",");
			if(unique_id.length<2){
				alert("请勾选要操作的记录!");
				return;
			}
		}
		if(!confirm("确认要修改吗？"))
			return;
		var hashvo=new ParameterSet();
		hashvo.setValue("ids","KAFKA,");// 默认修改KAFKA 20201016 wangcy
		hashvo.setValue("starflag",document.getElementById("starflag").value);
		hashvo.setValue("emporg","<%=table %>");
		if(radio==0)
			   hashvo.setValue("unique_ids","<%=unique_ids %>");
		var request=new Request({method:'post',asynchronous:true,onSuccess:isOK,functionId:'1010100124'},hashvo);

	}
	
	function isOK(outparamters){
		var isflag = outparamters.getValue("check");
		winclose(true);
		//if(isflag&&isflag=="ok"){
			//window.close();
		//}
	}
	//关闭弹窗方法  wangb 20190320
	function winclose(return_vo){
		if(parent.Ext && parent.Ext.getCmp('syncstata1')){
			var win = parent.Ext.getCmp('syncstata1');
			win.return_vo =return_vo;
			win.close();
			return;
		}
		window.close();
	}
</script>

<html:form action="/sys/export/SearchEmpSync.do?b_syncstata=link">
<table  border="0" cellspacing="0" height="300" width="390" align="center" cellpadding="0" class="ListTable" style="margin-top:10px;">
	<tr>
		<td style="height: 30px;" class="TableRow" align="left">修改同步状态</td>
	</tr>
	<tr>
		<td class="RecordRow"  style="border-bottom:0;">
			<div style="width: 100%;height: 240px;overflow: auto;">
				<table width="95%" border="0" cellpadding="0" cellspacing="0" style="padding: 5px;margin-top: 3px;margin-left: 10px;">
					<tr>
						<td>
							<select name="starflag" id="starflag">
								<option value="1">新增</option>
								<option value="2">修改</option>
								<option value="3">删除</option>
								<option value="0">已同步</option>
							</select>
						</td>
					</tr>
					<%--去除勾选外部系统20201016 wangcy--%>
					<%--<%if(isShow){ %>
					<hrms:paginationdb id="element" name="outsyncFrom"
						sql_str="select sys_id,sys_name" where_str="from t_sys_outsync where state=1"
						columns="sys_id,sys_name" page_id="pagination" pagerows="10"
						indexes="indexes">
						<tr>
							<td>
								<input type="checkbox" name="selbox"
								value='<bean:write name="element" property="sys_id" />'>&nbsp;
								<bean:write name="element" property="sys_name" />
							</td>
						</tr>
					</hrms:paginationdb>
					<%} %>--%>
				</table>
			</div>
		</td>
	</tr>
	<tr>
	   <td style="height: 30px;" class="RecordRow" align="center" style="border-top:0;">
	       <input type="radio" name="radiobutton" value="0" checked>已选记录&nbsp;&nbsp;&nbsp;&nbsp;
	       <input type="radio" name="radiobutton" value="1">全部记录
	   </td>
	</tr>
	<tr>
		<td style="height: 35px;" class="RecordRow" align="center">
			<input type="button" value="确定" class="mybutton" onclick="editSync();"/>&nbsp;&nbsp;&nbsp;&nbsp;
			<input type="button" value="关闭" class="mybutton" onclick="winclose(false);"/>
		</td>
	</tr>
</table>
</html:form>