<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ page import="com.hjsj.hrms.utils.PubFunc"%>
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
  //参数加密处理，防止高位字符导致请求被掐断 guodd 2019-06-28
  if(sSourceURL.indexOf("?")>0){
	  sSourceURL = sSourceURL.substring(0, sSourceURL.indexOf("?")+1)+"encryptParam="+PubFunc.encrypt(sSourceURL.substring(sSourceURL.indexOf("?")+1));
  }	
%>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title></title>

</head>
<hrms:themes/>
<body>
<iframe name="childFrame" id="childFrame" frameBorder="0" style="border:0;" height="100%" width="99%" src="<%=sSourceURL%>"></iframe>
</body>
</html> 