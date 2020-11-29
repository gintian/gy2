<%@ page contentType="text/html; charset=UTF-8"%>
<%@ page import="com.hrms.struts.valueobject.UserView"%>
<%@ page import="com.hrms.struts.constant.WebConstant"%>
<%@ page import="com.hrms.frame.utility.DateStyle"%>
<%
    String css_url="/css/css1.css";
	// 在标题栏显示当前用户和日期 2004-5-10 
    String userName = null;
	UserView userView=(UserView)session.getAttribute(WebConstant.userView);
	if(userView != null)
	{
	  userName = userView.getUserFullName();
	  css_url=userView.getCssurl();
	  if(css_url==null||css_url.equals(""))
	  	css_url="/css/css1.css";
	}
	String date = DateStyle.getSystemDate().getDateString();
	String state=(String)request.getParameter("state");
	String nflag=(String)request.getParameter("nflag");	
%>
<HTML>
<HEAD>
<TITLE>用户名：<%=userName%>　当前日期：<%=date%>
</TITLE>
   <link href="<%=css_url%>" rel="stylesheet" type="text/css">
   <link href="/css/xtree.css" rel="stylesheet" type="text/css" >
</HEAD>
<body style="margin-top:-2px;margin-left:-2px">
    <iframe id="iframe_user" name="iframe_user" width="100%" height="570" src="/gz/tempvar/viewtempvar.do?b_query=link&state=<%=state%>&type=3&nflag=<%=nflag%>"></iframe>
  </body>
</html>