<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean"%>
<%@ page import="com.hrms.struts.valueobject.UserView"%>
<%@ page import="com.hrms.struts.constant.WebConstant"%>
<%@ page import="com.hrms.frame.utility.DateStyle"%>
<%
    // 在标题栏显示当前用户和日期 2004-5-10 
    String userName = null;
    String css_url="/css/css1.css";
	UserView userView=(UserView)session.getAttribute(WebConstant.userView);
	if(userView != null)
	{
	  userName = userView.getUserFullName();

	}
	String date = DateStyle.getSystemDate().getDateString();
	String busiid=(String)request.getParameter("busiid");
%>

<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>　<bean:message key="hire.zp_persondb.username"/>：<%=userName%>　<bean:message key="workdiary.message.today"/>：<%=date%></title>
</head>
<link rel="stylesheet" href="<%=css_url%>" type="text/css">
<hrms:themes />
<body>
    <iframe id="iframe_user" name="iframe_user" width="100%" height="100%" 
       src="/gz/gz_accounting/piecerate/search_piecerate_formula.do?b_query=link&busiid=<%=busiid%>"></iframe>
  </body>
</html>
