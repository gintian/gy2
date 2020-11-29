<%@ page contentType="text/html; charset=UTF-8"%>
<%@ page import="com.hjsj.hrms.utils.PubFunc"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
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
  
  if(sSourceURL.toLowerCase().indexOf("script")!=-1) //20170717 dengcan
  		throw new Exception("error page!");
  
  //此处存在安全问题，如果src是危险网站，会泄漏信息。此处判断如果是重定向的连接，
  //判断域名端口是否一致，否则是引入外部连接，禁止访问 guodd 2016-12-19
  if(sSourceURL.toLowerCase().indexOf("http")!=-1 || sSourceURL.indexOf("HTTP")!=-1){
	  StringBuffer reUrl = request.getRequestURL();
	  String local = reUrl.substring(0, reUrl.indexOf("/gz"));
	  if(!sSourceURL.toLowerCase().startsWith(local.toLowerCase()))
		  throw new Exception("error page!");
	  
  }
    //参数加密处理，防止高位字符导致请求被掐断 haosl 2019-06-28
    if(sSourceURL.indexOf("?")>0){
        sSourceURL = sSourceURL.substring(0, sSourceURL.indexOf("?")+1)+"encryptParam="+PubFunc.encrypt(sSourceURL.substring(sSourceURL.indexOf("?")+1));
    }
  
%>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>　用户名：<%=userName%>　当前日期：<%=date%></title>
</head>
<body>
<iframe name="childFrame" id="childFrame" height="100%" width="100%" src="<%=sSourceURL%>" frameborder="0" style="margin-left:0px;"></iframe>
</body>
</html> 