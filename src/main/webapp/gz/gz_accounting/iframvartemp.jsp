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
	String state=(String)request.getParameter("state");
	String fieldsetid =(String) request.getParameter("fieldsetid");
%>
<HTML>
<HEAD>
<TITLE>
</TITLE>
   <link href="<%=css_url%>" rel="stylesheet" type="text/css">
   <link href="/css/xtree.css" rel="stylesheet" type="text/css" >
   <hrms:themes />
</HEAD>
<body>
	<%if(!state.equals("-1")){ %>
	<%
		if("-2".equals(state)){
	%>
		<iframe id="iframe_user" name="iframe_user" width="100%" height="480" src="/gz/tempvar/viewtempvar.do?b_query=link&state=<%=fieldsetid%>&type=5&nflag=5&showflag=1"></iframe>
	<%
		}else{
	%>
    <iframe id="iframe_user" name="iframe_user" width="100%" height="480" src="/gz/tempvar/viewtempvar.do?b_query=link&state=<%=state%>&type=2&nflag=0"></iframe>
    <%}}else{ %>
    <iframe id="iframe_user" name="iframe_user" width="100%" height="480" src="/gz/tempvar/viewtempvar.do?b_query=link&state=<%=state%>&type=1&nflag=4"></iframe>
    <%} %>
  </body>
</html>
