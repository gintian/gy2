<%@ page contentType="text/html; charset=UTF-8"%>
<%@page import="com.hjsj.hrms.utils.PubFunc"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@page import="com.hrms.struts.constant.SystemConfig"%>
<%@page import="org.apache.axis.encoding.Base64"%>
<%@page import="java.text.SimpleDateFormat"%>
<%@page import="java.util.Date"%>
<%String sSourceURL = (String)request.getParameter("src");
//url特殊字符还原处理 xiaoyun 2014-9-5 start
sSourceURL = PubFunc.hireKeyWord_filter_reback(sSourceURL);
//url特殊字符还原处理 xiaoyun 2014-9-5 end
sSourceURL=sSourceURL.replaceAll("`","&");
%>
<html>
<head>
<title>
</title>
</head>
<%
if (sSourceURL.indexOf("appfwd=1")>0){
	String etoken = sSourceURL.substring(sSourceURL.indexOf("etoken")+7);
	//System.out.println(etoken);
	String strtime = "";
	byte[] sourcearr = Base64.decode(etoken);
	try {
		String strs = new String(sourcearr,"UTF-8");
		//System.out.println(strs);
		strtime = strs.substring(strs.indexOf(",")+1);
		//System.out.println(strtime);
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmm");
		Date ktime = sdf.parse(strtime);  //链接时间
		Date ntime = new Date();        //当前时间
		Date stime = new Date(ktime.getTime()+10*60*1000);  //链接有效时间
		if (stime.before(ntime)){
			request.getSession().setAttribute("errMsg","链接已过期！");
			response.sendRedirect("/templates/info/failure_06.jsp");
			return;
		}
	} catch (Exception e) {
		e.printStackTrace();
	}
	
	
	
	
	
 	/*String sso_validateurl=SystemConfig.getPropertyValue("sso_validateurl");//单点登录来源地址
 	//sso_validateurl = "http://127.0.0.1";
	if(sso_validateurl !=null && sso_validateurl.length()>0){
		String referer = request.getHeader("referer");
		if(referer == null || referer.length()==0){
			request.getSession().setAttribute("errMsg","您无权访问此资源！"+referer);
			response.sendRedirect("/templates/info/failure_06.jsp");	
			return;
		}
		boolean bool = false;
		String[] validateurl = sso_validateurl.split(";");
		for (int i = 0; i < validateurl.length; i++) {
			if(referer.startsWith(validateurl[i])){
				bool = true;
				break;
			}
		}
		if(!bool){
			request.getSession().setAttribute("errMsg","您无权访问此资源！" + referer );
			response.sendRedirect("/templates/info/failure_06.jsp");	
			return;
		}
		
	}*/
}


	 if(sSourceURL.toLowerCase().indexOf("script")!=-1) //20170717 dengcan
  		throw new Exception("error page!");
  
	  //此处存在安全问题，如果src是危险网站，会泄漏信息。此处判断如果是重定向的连接，
	  //判断域名端口是否一致，否则是引入外部连接，禁止访问 guodd 2016-12-19
	  if(sSourceURL.toLowerCase().indexOf("http")!=-1 || sSourceURL.indexOf("HTTP")!=-1){
		  StringBuffer reUrl = request.getRequestURL();
		  String local = reUrl.substring(0, reUrl.indexOf("/train"));
		  if(!sSourceURL.toLowerCase().startsWith(local.toLowerCase()))
			  throw new Exception("error page!");
		  
	  }
 	
 %>
<body style="padding:0px;margin:0px;overflow-y: hidden">
<iframe frameborder="0" name="childFrame" id="childFrame" height="100%" width="100%" src="<%=sSourceURL%>"></iframe>
</body>
</html> 