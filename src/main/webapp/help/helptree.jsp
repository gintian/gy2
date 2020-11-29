<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ page import="com.hrms.struts.valueobject.UserView"%>
<%@ page import="com.hrms.struts.constant.WebConstant"%>
<%@ page import="com.hjsj.hrms.businessobject.report.tt_organization.TTorganization"%>
<%@ page import="com.hrms.frame.dao.RecordVo"%>

<%
	String css_url="/css/css1.css";
	UserView userView=(UserView)session.getAttribute(WebConstant.userView);
	if(userView != null)
	{
		css_url=userView.getCssurl();
		if(css_url==null||css_url.equals(""))
		css_url="/css/css1.css";
	}
	
%>

<HTML>
<HEAD>
	<link href="<%=css_url%>" rel="stylesheet" type="text/css">
	<link href="/css/xtree.css" rel="stylesheet" type="text/css" >
	<SCRIPT LANGUAGE=javascript src="/js/xtree.js"></SCRIPT>
	<SCRIPT LANGUAGE=javascript src="/js/validate.js"></SCRIPT>     
	<SCRIPT LANGUAGE=javascript>
</SCRIPT>     
</HEAD>
<body   topmargin="0" leftmargin="5" marginheight="0" marginwidth="0">
	<table width="100%" align="left" border="0" cellpadding="0" cellspacing="0" class="mainbackground">
	<tr>  
		<td valign="top">
			<div id="treemenu"></div>
		</td>
	</tr>
</table>	

<BODY>
</HTML>


<SCRIPT LANGUAGE=javascript>

	var m_sXMLFile= "help_tree.jsp?params=-1";	

	var newwindow;
	var root=new xtreeItem("root","HRP帮助","#","mil_menu","HRP帮助","/images/book.gif",m_sXMLFile);

	root.setup(document.getElementById("treemenu"));

	if(newwindow!=null)
	{
		newwindow.focus();
	}
	if(parent.parent.myNewBody!=null)
	{
		parent.parent.myNewBody.cols="*,0"
	}

</SCRIPT>
		
