<%@ page contentType="text/html; charset=UTF-8"%>
<%@ page import="com.hrms.struts.valueobject.UserView"%>
<%@ page import="com.hrms.struts.constant.WebConstant"%>
<%@ page import="com.hrms.frame.utility.DateStyle"%>
<%@page import="com.hjsj.hrms.utils.PubFunc"%>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%
	String css_url = "/css/css1.css";
	UserView userView = (UserView) session
			.getAttribute(WebConstant.userView);

	if (userView != null) {
		css_url = userView.getCssurl();
		if (css_url == null || css_url.equals(""))
			css_url = "/css/css1.css";
	}
	String encryptParam = PubFunc.encrypt("codesetid=55");
%>


<HTML>
<HEAD>
<TITLE></TITLE>
<link href="<%=css_url%>" rel="stylesheet" type="text/css">
<link href="/css/xtree.css" rel="stylesheet" type="text/css">
<SCRIPT LANGUAGE=javascript src="/js/xtree.js"></SCRIPT>
<SCRIPT LANGUAGE=javascript src="/js/validate.js"></SCRIPT>
</HEAD>

<body>
<table width="600" align="left" border="0" cellpadding="0" cellspacing="0" class="mainbackground">
	<tr>
		<td valign="top">
			<div id="treemenu"></div>
		</td>
	</tr>
</table>
</BODY>
</HTML>
<SCRIPT LANGUAGE=javascript>
	var m_sXMLFile	= "/train/hierarchy/get_code_tree.jsp?encryptParam=<%=encryptParam%>";	
	var root=new xtreeItem("root","培训课程分类","/train/hierarchy.do?b_query=link&a_code=","mil_body","培训课程分类","/images/add_all.gif",m_sXMLFile);
	root.setup(document.getElementById("treemenu"));
</SCRIPT>
<script>
	root.openURL();
</script>
