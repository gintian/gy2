<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ page import="com.hrms.struts.valueobject.UserView"%>
<%@ page import="com.hrms.struts.constant.WebConstant"%>
<%@ page import="com.hrms.frame.utility.DateStyle"%>
<%@page import="com.hjsj.hrms.utils.PubFunc"%>
<%
    // 在标题栏显示当前用户和日期 2004-5-10 
    String userName = null;
    String css_url="/css/css1.css";
	UserView userView=(UserView)session.getAttribute(WebConstant.userView);
	if(userView != null)
	{
	  userName = userView.getUserFullName();
	  css_url=userView.getCssurl();
	  if(css_url==null||css_url.equals(""))
	  	css_url="/css/css1.css";
	}
	String date = DateStyle.getSystemDate().getDateString();
%>
<%
//url特殊字符还原处理 dengcan 2014-9-26 end 
  String sSourceURL = ((String)request.getParameter("src")).replaceAll("／", "/").replaceAll("？", "?").replaceAll("＝", "=")  ;  //20140901  dengcan
  sSourceURL=sSourceURL.replaceAll("`","&");
%>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>凭证定义</title>
</head>
<body>
<iframe name="childFrame" id="childFrame" height="100%" width="100%" src="<%=sSourceURL%>"></iframe>
</body>
</html> 