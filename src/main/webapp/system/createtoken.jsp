<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<%@page import="com.hrms.frame.codec.SafeCode"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title></title>
<%
	String userid=request.getParameter("userid");
	if(userid==null)
		userid="";
	userid=SafeCode.encode(userid);
	String pwd=request.getParameter("password");
	if(pwd==null)
		pwd="";
	pwd=SafeCode.encode(pwd);	
	Cookie cookie=null;
    cookie=new Cookie("RecordName",userid);
    cookie.setPath("/");
    response.addCookie(cookie);
    cookie=new Cookie("RecordPwd",pwd);
    cookie.setPath("/");
    response.addCookie(cookie);

    String portal=request.getParameter("portal");
    if(portal!=null)
    	portal=portal.replaceAll("`","&");
%>
</head>
<body>
<script type="text/javascript">
  <%if(!(portal==null||portal.length()==0)){%>
   	window.location.href="<%=portal%>";
  <%}%>
</script>   	
</body>
</html>