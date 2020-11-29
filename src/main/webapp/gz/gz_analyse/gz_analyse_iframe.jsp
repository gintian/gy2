<%@ page contentType="text/html; charset=UTF-8"%>
<%@page import="com.hjsj.hrms.utils.PubFunc"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%
  String sSourceURL = ((String)request.getParameter("src")).replaceAll("／", "/").replaceAll("？", "?").replaceAll("＝", "=")  ;  //20140901  dengcan
  sSourceURL=sSourceURL.replaceAll("`","&");
%>
<html>
<head>
<title>
工资报表
</title>
</head>

<body>
<iframe name="childFrame" id="childFrame" height="100%" width="100%" src="<%=sSourceURL%>"></iframe>
</body>
<script type="text/javascript">
var paraArray=dialogArguments; 
 var parent = paraArray[0];
 var child = paraArray[1];
 window.document.title=parent+"--->>"+child;
</script>
</html> 