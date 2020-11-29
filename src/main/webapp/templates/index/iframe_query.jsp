<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@page import="com.hjsj.hrms.utils.PubFunc"%>
<%//url特殊字符还原处理 dengcan 2014-9-26 end 
  String sSourceURL = ((String)request.getParameter("src")).replaceAll("／", "/").replaceAll("？", "?").replaceAll("＝", "=")  ;  //20140901  dengcan
  sSourceURL=sSourceURL.replaceAll("`","&");
  
  //haosl sSourceURL.toLowerCase().indexOf("script")这句话会误杀一些链接，比如传参fieldName=descript，会被拦截掉，先暂时这么改
  if((sSourceURL.toLowerCase().indexOf("script")!=-1 && sSourceURL.toLowerCase().indexOf("<")!=-1)
     || sSourceURL.toLowerCase().indexOf("javascript:")!=-1) //20170717 dengcan
      throw new Exception("error page!");
  
  //此处存在安全问题，如果src是危险网站，会泄漏信息。此处判断如果是重定向的连接，
  //判断域名端口是否一致，否则是引入外部连接，禁止访问 guodd 2017-11-28
  if(sSourceURL.toLowerCase().indexOf("http")!=-1 || sSourceURL.indexOf("HTTP")!=-1){
		  throw new Exception("error page!");
	  
  }
  
  if(sSourceURL.trim().startsWith("//"))
		throw new Exception("error page!");
%>
<html>
<head><title>获取密码</title>
</head>
<body>
<iframe name="childFrame" id="childFrame" frameborder="0" width="99%" height="98%" src="<%=sSourceURL%>"></iframe>
</body>
</html> 