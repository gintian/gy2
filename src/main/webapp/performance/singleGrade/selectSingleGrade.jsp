<%@ page contentType="text/html; charset=UTF-8"%>
<%@ page import="com.hrms.struts.constant.SystemConfig" %>
<%
	String clientname = SystemConfig.getPropertyValue("clientName");
	out.println("<script language=\"javascript\">");
	if("gs".equalsIgnoreCase(clientname)){
		out.println("window.location.href=\"/selfservice/performance/allPlansSingleGrade.do?b_query=link&fromModel=menu&model=0\"");
	}else{
		out.println("window.location.href=\"/selfservice/performance/singleGrade.do?b_query=link&fromModel=menu&model=0\"");
	}
	out.println("</script>");
%>
<html>
  <head>
  </head>
  <body>
  </body>
</html>
