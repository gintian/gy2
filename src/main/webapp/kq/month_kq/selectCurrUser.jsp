<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
%>
<link rel="stylesheet" href="/css/css1.css" type="text/css"><hrms:themes /> <!-- 7.0css -->
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
  <head>
    <base href="<%=basePath%>">
    
    <title>My JSP 'selectCurrUser.jsp' starting page</title>
    
	<meta http-equiv="pragma" content="no-cache">
	<meta http-equiv="cache-control" content="no-cache">
	<meta http-equiv="expires" content="0">    
	<meta http-equiv="keywords" content="keyword1,keyword2,keyword3">
	<meta http-equiv="description" content="This is my page">
	<!--
	<link rel="stylesheet" type="text/css" href="styles.css">
	-->

  </head>
  
  <body>
    <table  border="0" cellspacing="0" height="300" width="95%" align="center" cellpadding="0" class="ListTable" style="margin-left: 10px;margin-right: 10px;margin-top: 10px;">
		<tr>
			<td style="height: 10px;" class="TableRow" align="left">请选择当前审批人:<br></td>
		</tr>
		<logic:iterate id="element" name="monthKqForm" property="currUserList">
			<bean:define id="radioid" name="element" property="itemid"></bean:define>
			<tr>
				<td class="RecordRow">
					<input type="radio" value="${radioid}" name="radiobutton" style="margin-left:150px;"> <bean:write name="element" property="itemdesc"/>				
				</td>
			</tr>
		</logic:iterate>
		<tr>
			<td align="center">
				<input type="button" value="确定" class="mybutton" onclick="check();">&nbsp;
				<input type="button" value="关闭" class="mybutton" onclick="closes();">
			</td>
		</tr>
	</table>
  </body>
</html>

<script type="text/javascript">
	function check(){
		var radio = "";
		var radios = document.getElementsByName("radiobutton");
		for(var i = 0 ; i < radios.length ; i++){
			 if(radios[i].checked)
                radio = radios[i].value;
		}
		if("" == radio){
			alert("请选择当前审批人!");
			return;
		}else{
			returnValue = radio;
			window.close();
		}
	}
	function closes(){
		returnValue = "";
		window.close();
	}
</script>
