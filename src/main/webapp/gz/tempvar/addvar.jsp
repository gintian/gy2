<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean"%>
<%@ taglib uri="/tags/struts-html" prefix="html"%>
<%@ taglib uri="/tags/struts-logic" prefix="logic"%>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms"%>
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
	String type=(String)request.getParameter("type");
	String cstate=(String)request.getParameter("cstate");
	String nflag=(String)request.getParameter("nflag");
	String showflag =(String) request.getParameter("showflag");
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
     <iframe  frameborder="0"  scrolling="none" id="iframe_user" name="iframe_user" width="100%" height="100%" src="/gz/tempvar/addtempvar.do?b_query=link&type=<%=type%>&cstate=<%=cstate%>&nflag=<%=nflag%>&showflag=<%=showflag%>"></iframe>
<BODY>
</HTML>


