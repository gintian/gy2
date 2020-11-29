<%@page import="com.hjsj.hrms.utils.PubFunc"%>
<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%
    String path = PubFunc.decrypt(request.getParameter("center_url"));
%>
<html>
<head>
<meta http-equiv="X-UA-Compatible" content="IE=9; IE=8; IE=7" >
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">

</head>
<frameset name="myBody"  border="0" frameborder="no" >
  <frame  src="<%=path %>" frameborder="no" name="il_body" scrolling="auto" noresize>
</frameset>
</html>