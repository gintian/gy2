<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean"%>
<%@ taglib uri="/tags/struts-html" prefix="html"%>
<%@ taglib uri="/tags/struts-logic" prefix="logic"%>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms"%>
<%@ page import="com.hrms.struts.valueobject.UserView"%>
<%@ page import="com.hrms.struts.constant.WebConstant"%>
<%@page import="com.hjsj.hrms.actionform.stat.InfoSetupForm"%>
<%@page import="java.util.ArrayList"%>
<%@ page import="com.hrms.frame.utility.DateStyle"%>
<%
	// 在标题栏显示当前用户和日期 2004-5-10 
	String userName = null;
	String css_url = "/css/css1.css";
	UserView userView = (UserView) session
			.getAttribute(WebConstant.userView);
	if (userView != null) {
		userName = userView.getUserFullName();
		css_url = userView.getCssurl();
		if (css_url == null || css_url.equals(""))
			css_url = "/css/css1.css";
	}
	String date = DateStyle.getSystemDate().getDateString();
	InfoSetupForm form = (InfoSetupForm) session
			.getAttribute("infoSetupForm");
	ArrayList list = form.getCondList();
	String bosflag = "";
	if (userView != null) {
		bosflag = userView.getBosflag();
	}
%>
<html>
	<head>
		<link rel="stylesheet" type="text/css" href="/ajax/skin.css"></link>
		<link rel="stylesheet" type="text/css" href="/css/css1.css"></link>
	</head>
		<script language="JavaScript" src="/js/validate.js"></script>
		<script language="JavaScript" src="/js/function.js"></script>

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
		<script language="javascript" src="/js/validate.js"></script>
		<script language="javascript" src="/js/constant.js"></script>

		<script type="text/javascript">
<!--
	function submitSelect() {
		infoSetupForm.action="/general/static/commonstatic/statshowsetup.do?b_dialogsub=link"
		infoSetupForm.submit();
	}
	succ();
	function succ() {
		if ('1' == '${infoSetupForm.flag}') {
			if(getBrowseVersion()){
				var val = parent.dialogArguments;
				val.innerHTML = "${infoSetupForm.html}";
			}else{//非IE浏览器 获取父页面 元素  wangb 20180127
				var val = parent.opener.document.getElementById("conddiv");
				val.innerHTML = "${infoSetupForm.html}";
			}
			top.close();
		}
	}
//-->
</script>
		<hrms:themes />
	<STYLE type=text/css>
.fixedDiv5{
	height: expression(document.body.clientHeight-50);
	width: expression(document.body.clientWidth-10);
}
.TableRow_left{
	BORDER-TOP: 0pt solid;
}
</STYLE>
	<body scroll=no>
		<html:form
			action="/general/static/commonstatic/setstatshowsetup.do?b_showdialog=link">
			<div class="fixedDiv5">
				<table width="100%" border="0" cellspacing="0" align="center"
					cellpadding="0" class="ListTable" style="margin-top: 0;">
					<thead>
						<tr>
							<td align="center" class="TableRow_top" width="20%" nowrap>
								<input type="checkbox" name="selbox"
									onclick="batch_select(this,'setinfoSetupForm.select');"
									title='<bean:message key="label.query.selectall"/>'>
							</td>
							<td align="center" class="TableRow_left" width="80%" nowrap>
								名称
							</td>
						</tr>
					</thead>
					<%
						int i = 0;
							if (list != null && list.size() != 0) {
								String count = String.valueOf(list.size());
					%>
					<hrms:extenditerate id="element" name="infoSetupForm"
						property="setinfoSetupForm.list" indexes="indexes"
						pagination="setinfoSetupForm.pagination" pageCount="<%=count%>"
						scope="session">

						<%
							if (i % 2 == 0) {
						%>
						<tr class="trShallow">
							<%
								} else {
							%>
						
						<tr class="trDeep">
							<%
								}
												i++;
							%>
							<td align="center" class="RecordRow_right" nowrap width="20%">
								<hrms:checkmultibox name="infoSetupForm"
									property="setinfoSetupForm.select" value="true"
									indexes="indexes" />
							</td>
							<td align="left" class="RecordRow_left" nowrap width="80%">
								<bean:write name="element" property="string(name)" filter="true" />
							</td>
						</tr>
					</hrms:extenditerate>
					<%
						}
					%>
				</table>
			</div>
			<table width="70%" border="0" cellspacing="0" align="center"
				cellpadding="0">
				<tr><td height="5px"></td></tr>
				<tr>
					<td align="center" nowrap>
						<html:button property="b_save" styleClass="mybutton"
							onclick="submitSelect();"><bean:message
								key='button.ok' /></html:button>
						<html:button property="b_close" styleClass="mybutton"
							onclick="top.close();">取消</html:button>
					</td>
				</tr>
			</table>
		</html:form>
	</body>
		<script>
	$('unitview').value="";
	$('anyunit').value="";

if(!getBrowseVersion() || getBrowseVersion() == 10){//兼容非IE浏览器 样式  wangb 20180206  bug 34609  and 处理ie11 不加兼容视图样式  wangb 20190307
	var fixedDiv5 = document.getElementsByClassName('fixedDiv5')[0]; //调整table高度
	fixedDiv5.style.height = '240px';
	var iframes = parent.document.getElementById('childFrame'); //调整整体 iframe 高度
	iframes.style.height = '300px';
}
</script>
</html>