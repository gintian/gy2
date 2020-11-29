<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
  <head>
    <base href="<%=basePath%>">
    
    <title>My JSP 'kqorgtree.jsp' starting page</title>
    
	<meta http-equiv="pragma" content="no-cache">
	<meta http-equiv="cache-control" content="no-cache">
	<meta http-equiv="expires" content="0">    
	<meta http-equiv="keywords" content="keyword1,keyword2,keyword3">
	<meta http-equiv="description" content="This is my page">
	<!--
	<link rel="stylesheet" type="text/css" href="styles.css">
	-->
	<script LANGUAGE=javascript src="/js/xtree.js"></script> 
  </head>
  
  <body>
  <table border="0" cellspacing="0"  align="left" cellpadding="0" class="mainbackground" >
   	<tr>
   		<td class="RecordRow">
    		<div id="treemenu"></div>
    	</td>
    </tr>
  </table>
  </body>
  <script type="text/javascript">
  	 var m_sXMLFile= "/kq/options/item_list.jsp?params=codeitemid%3Dparentid";
 	 var root=new xtreeItem("27","考勤项目","/kq/options/kq_item_details.do?b_query=link&codeitemid=","mil_body","考勤项目","/images/table.gif",m_sXMLFile);
 	 root.setup(document.getElementById("treemenu"));
  </script>
</html>
