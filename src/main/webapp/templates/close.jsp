<%@ page language="java" import="java.util.*" pageEncoding="ISO-8859-1"%>
<%@ page import="javax.servlet.http.HttpSession" %>
<%
HttpSession ses = request.getSession();
ses.invalidate();
out.println("<script language=\"javascript\">");
out.println("window.opener=null;");
out.println("window.close();");
out.println("</script>");
%>


