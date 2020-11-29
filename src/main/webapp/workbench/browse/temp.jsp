<%@ page language="java" import="java.util.*" pageEncoding="ISO-8859-1"%>
<%@ page import="com.hjsj.hrms.utils.PubFunc"%>
<%
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
  <head>
    <base href="<%=basePath%>">
    
    <title>My JSP 'temp.jsp' starting page</title>
    
	<meta http-equiv="pragma" content="no-cache">
	<meta http-equiv="cache-control" content="no-cache">
	<meta http-equiv="expires" content="0">    
	<meta http-equiv="keywords" content="keyword1,keyword2,keyword3">
	<meta http-equiv="description" content="This is my page">
	<!--
	<link rel="stylesheet" type="text/css" href="styles.css">
	-->

  </head>
  <hrms:themes />
  <body>
    <%
    String url = PubFunc.hireKeyWord_filter_reback(request.getParameter("url"));
  

    //姝ゅ瀛樺湪瀹夊叏闂锛屽鏋渟rc鏄嵄闄╃綉绔欙紝浼氭硠婕忎俊鎭�傛澶勫垽鏂鏋滄槸閲嶅畾鍚戠殑杩炴帴锛�
    //鍒ゆ柇鍩熷悕绔彛鏄惁涓�鑷达紝鍚﹀垯鏄紩鍏ュ閮ㄨ繛鎺ワ紝绂佹璁块棶 guodd 2016-12-19
    if(url.toLowerCase().indexOf("http")!=-1 || url.indexOf("HTTP")!=-1){
  	  StringBuffer reUrl = request.getRequestURL();
  	  String local = reUrl.substring(0, reUrl.indexOf("/workbench"));
  	  System.out.println(local+"   "+url);
  	  if(!url.toLowerCase().startsWith(local.toLowerCase()))
  		  throw new Exception("error page!");
  	  
    }
    
    if(url.trim().indexOf("/workbench/media/showmediainfo")!=0)
    {
    	throw new Exception("error page!");
    }
     
    
    
    
    
    
    if(url.indexOf("encryptParam")!=-1){
    	response.sendRedirect(url);
    }else{
    	response.sendRedirect(url+"&encryptParam="+PubFunc.encrypt("&usernumber="+request.getParameter("usernumber")+"&i9999="+request.getParameter("i9999")));
    }
     %>
  </body>
</html>
