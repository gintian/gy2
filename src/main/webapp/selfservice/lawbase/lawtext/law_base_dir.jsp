<%@ page contentType="text/html; charset=UTF-8"%>
<%@ page import="com.hjsj.hrms.utils.PubFunc"%>
<%@ page import="com.hrms.struts.constant.WebConstant"%>
<%@ page import="com.hrms.struts.valueobject.UserView"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<jsp:useBean id="welcomeForm" class="com.hjsj.hrms.actionform.welcome.WelcomeForm" scope="session" />
<script language="javascript">
    basetype = "<bean:write name="lawbaseForm" property="basetype" />";
	var caption = "";
	if (basetype == 1) {
	    caption = "<bean:message key="menu.rule"/>";
	}
	if (basetype == 4) {
	    caption = "<bean:message key="law_maintenance.peoplecode"/>";
	}
	if (basetype == 5) {
	    caption = "<bean:message key="law_maintenance.file"/>";
	}
   function exeAdd(addStr)
   {
       target_url=addStr;
       window.open(target_url, 'il_body'); 
   }
</script>
<%String css_url = "/css/css1.css";
			UserView userView = (UserView) session
					.getAttribute(WebConstant.userView);
			if (userView != null) {
				css_url = userView.getCssurl();
				if (css_url == null || css_url.equals(""))
					css_url = "/css/css1.css";
			}

			%>

<HTML>
	<HEAD>
		<TITLE></TITLE>
		<link href="<%=css_url%>" rel="stylesheet" type="text/css">
		<link href="/css/xtree.css" rel="stylesheet" type="text/css">
		<SCRIPT LANGUAGE=javascript src="/js/xtree.js"></SCRIPT>
		<SCRIPT LANGUAGE=javascript src="/js/validate.js"></SCRIPT>
		<SCRIPT LANGUAGE=javascript>
      	
   </SCRIPT>
	</HEAD>
	<body topmargin="0" leftmargin="5" marginheight="0" marginwidth="0">
		<table width="100%" align="left" border="0" cellpadding="0" cellspacing="0" class="mainbackground">
			<!-- <tr align="left">
				<td valign="top">
					&nbsp;&nbsp;&nbsp;&nbsp;
					 <IMG src="/images/lawadd.gif" onclick="exeAdd('http://127.0.0.1:8080/selfservice/lawbase/lawtext/law_term_query.jsp?a_base_id=<%=request.getParameter("a_base_id")%>')">   
					&nbsp;
					<input type="image" name="b_order" src="/images/sort.gif" alt="全文检索" onclick="adjust_order();">
					&nbsp;
				</td>
			</tr>-->
			<tr>
				<td valign="top">
					<div id="treemenu"></div>
				</td>
			</tr>
		</table>
	</BODY>
</HTML>


<SCRIPT LANGUAGE=javascript>

var m_sXMLFile	= "/selfservice/lawbase/lawtext/get_lawbase_strut_tree.jsp?params=<%=PubFunc.encrypt("base_id=up_base_id")%>&basetype=<bean:write name="lawbaseForm" property="basetype" filter="true"/>";
var newwindow;
var root=new xtreeItem("root",caption,"/selfservice/lawbase/lawtext/law_maintenance.do?b_query=link&a_base_id=","mil_body",caption,"/images/add_all.gif",m_sXMLFile);
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
