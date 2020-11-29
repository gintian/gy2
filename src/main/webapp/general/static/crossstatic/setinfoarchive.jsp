<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean"%>
<%@ taglib uri="/tags/struts-html" prefix="html"%>
<%@ taglib uri="/tags/struts-logic" prefix="logic"%>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms"%>
<%@ page import="com.hrms.struts.valueobject.UserView"%>
<%@ page import="com.hrms.struts.constant.WebConstant"%>
<%@page import="com.hjsj.hrms.actionform.general.statics.SaveCrosstabForm"%>
<%@page import="java.util.ArrayList"%>
<%@ page import="com.hrms.frame.utility.DateStyle"%>
<%
	// 在标题栏显示当前用户和日期 2004-5-10 
	String userName = null;
	String css_url = "/css/css1.css";
	UserView userView = (UserView) session.getAttribute(WebConstant.userView);
	if (userView != null) {
		userName = userView.getUserFullName();
		css_url = userView.getCssurl();
		if (css_url == null || css_url.equals(""))
			css_url = "/css/css1.css";
	}
	SaveCrosstabForm form = (SaveCrosstabForm) session.getAttribute("saveCrosstabForm");
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
<script language="JavaScript" src="/module/utils/js/template.js"></script>
<script type="text/javascript">

	function submitSelect() {
		saveCrosstabForm.action="/general/deci/statics/savecrosstab.do?b_dialogsub=link"
		saveCrosstabForm.submit();
	}
	succ();
	function succ() {
		if ('1' == '${saveCrosstabForm.flag}') {
			/*
			var val = parent.dialogArguments;
			val.innerHTML = "${saveCrosstabForm.html}";
			top.close();
			*/
			//19/3/18 xus 浏览器兼容 谷歌点加号按钮 不弹窗bug
			var ainnerHTML = "${saveCrosstabForm.html}";
			if(window.showModalDialog){
				top.returnValue=ainnerHTML;
			}else{
				parent.parent.window.sformula_callbackfunc(ainnerHTML);
			}
			winClose();
		}
	}
	//19/3/18 xus 关闭窗口事件
	function winClose(){
		if(window.showModalDialog){
			top.close();
		}else{
			parent.parent.Ext.getCmp('sformula_showModalDialogs').close();
		}
	}
</script>
<hrms:themes />
<style type=text/css>
.fixedDiv5{
	height: expression(document.body.clientHeight-50);
	width: expression(document.body.clientWidth-10);
}
.TableRow_left{
	border-top: 0pt solid;
}
</style>
	<body id="htmlBody" scroll=no onload="setDivHeight();">
		<html:form action="/general/deci/statics/savecrosstab.do?b_showdialog=link">
			<div class="fixedDiv5" id="firstDiv" style="height:250px;">
				<table width="100%" border="0" cellspacing="0" align="center"
					cellpadding="0" class="ListTable" style="margin-top: 0;">
					<thead>
						<tr>
							<td align="center" class="TableRow_top" width="20%" nowrap>
								<input type="checkbox" name="selbox"
									onclick="batch_select(this,'setsaveCrosstabForm.select');"
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
					<hrms:extenditerate id="element" name="saveCrosstabForm"
						property="setsaveCrosstabForm.list" indexes="indexes"
						pagination="setsaveCrosstabForm.pagination" pageCount="<%=count%>"
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
								<hrms:checkmultibox name="saveCrosstabForm"
									property="setsaveCrosstabForm.select" value="true"
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
							onclick="winClose();">取消</html:button>
					</td>
				</tr>
			</table>
		</html:form>
	</body>
</html>
<script type="text/javascript">
var firstDiv = document.getElementById('firstDiv');
//19/3/27 xus 非兼容性视图浏览器兼容：多维统计-设置分类统计条件 页面没有滚动条bug
if(!getBrowseVersion()|| getBrowseVersion()==10){
	document.getElementById("htmlBody").scroll="yes";	
	document.getElementById("htmlBody").style.overflowY='auto';
	firstDiv.style.height = '';
}
function setDivHeight(){//针对ie9 下  div 会在下方显示，特殊处理 wangb bug 49032 20190619
	firstDiv.style.height = parseInt(firstDiv.offsetHeight)-1+'px';
}
</script>