<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@page import="com.hjsj.hrms.utils.PubFunc"%>
<html>
<head>
<title>
</title>
</head>
<%String url = request.getParameter("url"); 
url = PubFunc.hireKeyWord_filter_reback(url);



 if(url.toLowerCase().indexOf("script")!=-1) //20170717 dengcan
  		throw new Exception("error page!");
  
	  //此处存在安全问题，如果src是危险网站，会泄漏信息。此处判断如果是重定向的连接，
	  //判断域名端口是否一致，否则是引入外部连接，禁止访问 guodd 2016-12-19
	  if(url.toLowerCase().indexOf("http")!=-1 || url.indexOf("HTTP")!=-1){
		  StringBuffer reUrl = request.getRequestURL();
		  String local = reUrl.substring(0, reUrl.indexOf("/train"));
		  if(!url.toLowerCase().startsWith(local.toLowerCase()))
			  throw new Exception("error page!");
		  
	  }


%>
<body style="padding:0px;margin:0px;overflow-y: hidden">
<iframe frameborder="0" name="childFrame" id="childFrame" height="100%" width="100%" src="<%=url %>"></iframe>
</body>
</html> 