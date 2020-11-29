<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean"%>
<%@ taglib uri="/tags/struts-html" prefix="html"%>
<%@ taglib uri="/tags/struts-logic" prefix="logic"%>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms"%>
<%@ page import="com.hrms.struts.valueobject.UserView"%>
<%@ page import="com.hrms.struts.constant.WebConstant"%>
<script language="JavaScript" src="../../js/validate.js"></script>
<script language="JavaScript" src="../../js/function.js"></script>

<%// 在标题栏显示当前用户和日期 2004-5-10 
			String userName = null;
			String css_url = "/css/css1.css";
			UserView userView = (UserView) session
					.getAttribute(WebConstant.userView);
			if (userView != null) {
				css_url = userView.getCssurl();
				if (css_url == null || css_url.equals(""))
					css_url = "/css/css1.css";
			}
%>
<link rel="stylesheet" href="<%=css_url%>" type="text/css">
<link rel="stylesheet" type="text/css" href="/ajax/skin.css"></link>
<link href="/css/xtree.css" rel="stylesheet" type="text/css">
<hrms:themes></hrms:themes>
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
<script type="text/javascript" language="javascript">
<!--
function getinfo(){
	/**
	*防止前缀选相同选项出现重复值
	*许硕16/09/13
	**/
	var sprefix;
	if(parent.Ext)
		sprefix=parent.Ext.getCmp('id_factory').sprefix;
	else
		sprefix=window.dialogArguments;
	var reobject=new Object();
	var selvalue=document.getElementById("sel").value;
	var a=-1;
	/*当sprefix 值为空时 不支持indexOf方法页面报错 加上非空验证  29768 wangb 20170727*/
	if(sprefix != "")
		a=sprefix.indexOf(selvalue);
	if(a==-1){
		reobject.sel=selvalue; 
		if(parent.Ext)
			parent.Ext.getCmp('id_factory').reobject=reobject;
		else
			top.returnValue= reobject;
	}
	if(parent.Ext){
		parent.Ext.getCmp('id_factory').close();
		return;
	}
	top.close();
	
}
function reback(){
	if(parent.Ext){
		parent.Ext.getCmp('id_factory').close();
		return;
	}
	window.close();
}
//-->
</script>
<form>
<table align="center" width="290" height="60"  cellpadding="0" cellspacing="0">
	<tr>
		<td colspan="6">
			<table align="center">
				<tr>
					<td>
						<select id="sel" onchange="getinfo();" style="width:180">
							<OPTION value="">
								<bean:message key="label.select" />
							</OPTION>
							<OPTION value="#yyyy#">
								<bean:message key="id_factory.current" />
								<bean:message key="datestyle.year" />
							</OPTION>
							<OPTION value="#yyyy-mm#">
								<bean:message key="id_factory.current" />
								<bean:message key="datestyle.year" />
								-
								<bean:message key="datestyle.month" />
							</OPTION>
							<OPTION value="#yyyy.mm#">
								<bean:message key="id_factory.current" />
								<bean:message key="datestyle.year" />
								.
								<bean:message key="datestyle.month" />
							</OPTION>
							<OPTION value="#yyyy-mm-dd#">
								<bean:message key="id_factory.current" />
								<bean:message key="datestyle.year" />
								-
								<bean:message key="datestyle.month" />
								-
								<bean:message key="datestyle.day" />
							</OPTION>
							<OPTION value="#yyyy.mm.dd#">
								<bean:message key="id_factory.current" />
								<bean:message key="datestyle.year" />
								.
								<bean:message key="datestyle.month" />
								.
								<bean:message key="datestyle.day" />
							</OPTION>
						</select>
					</td>
				</tr>
			</table>
		</td>
	</tr>
</table>
<table width="100%">
	<tr>
		<td align="center" height="25px;">
			<INPUT type="button" value='<bean:message key="button.cancel"/>' onclick="reback();" class="mybutton">
		</td>
	</tr>
</table>
</form>
<script>
 if(parent.Ext)
 	document.getElementById('sel').value=parent.Ext.getCmp('id_factory').sprefix;
 else
 	document.getElementById('sel').value=window.dialogArguments;
</script>