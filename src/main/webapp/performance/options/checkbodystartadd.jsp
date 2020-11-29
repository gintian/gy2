<%@ page contentType="text/html; charset=UTF-8"%>
<%@ page import="com.hrms.struts.valueobject.UserView"%>
<%@ page import="com.hrms.struts.constant.WebConstant"%>
<%@ page import="com.hrms.frame.utility.DateStyle"%>
<%
    String css_url="/css/css1.css";
	UserView userView=(UserView)session.getAttribute(WebConstant.userView);
	if(userView != null)
	{
	  css_url=userView.getCssurl();
	  if(css_url==null||css_url.equals(""))
	   css_url="/css/css1.css";
	}
	String bodyType=(String)request.getParameter("bodyType");
	String aa="";
%>
<HTML>
<HEAD>
<TITLE>
</TITLE>
<hrms:themes />
   <link href="/css/xtree.css" rel="stylesheet" type="text/css" >
</HEAD>
<body>
     <iframe id="iframe_user" name="iframe_user" width="100%" height="280" FRAMEBORDER="0" src="/performance/options/checkBodyObjectAdd.do?b_add=link&bodyType=<%=bodyType%>"></iframe>
</BODY>
</HTML>


