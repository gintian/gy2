<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";

%>
<link rel="stylesheet" href="/css/css1.css" type="text/css">

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">

<html>
  <head>
    <base href="<%=basePath%>">
    
    
	<meta http-equiv="pragma" content="no-cache">
	<meta http-equiv="cache-control" content="no-cache">
	<meta http-equiv="expires" content="0">    
	<meta http-equiv="keywords" content="keyword1,keyword2,keyword3">
	<meta http-equiv="description" content="This is my page">
	<!--
	<link rel="stylesheet" type="text/css" href="styles.css">
	-->

  </head>
  	<script language="javascript">
		function enter(){
			var obj=document.getElementsByName("s");
			for(var i=0;i<obj.length;i++){
				if(obj[i].checked==true){
					value=obj[i].value;
				}
			}
    		window.returnValue=value;
    		window.close();
		}
		function closeok(){
			returnValue="2";
			window.close();
		}
	</script>
	<hrms:themes />
  <body>
    <TABLE width="290" border="0" cellspacing="0" align="center" cellpadding="0" style="margin-top:-15px;margin-left:-3px;">
    <tr>
    <td>&nbsp;</td>
    </tr>
    	<tr>
    	<td align="center">
		<fieldset style="width:100%">
			<legend>
					引入上期总额
			</legend>
			<br>
  <TABLE border="0" cellspacing="0" align="center" cellpadding="0">
 	<TR>
		<TD align="center">包含下级机构：&nbsp;<INPUT TYPE="radio" NAME="s" value="1" checked>是&nbsp;&nbsp;&nbsp;<INPUT TYPE="radio" NAME="s" value="0">否</TD>
  	</TR>
  </TABLE>
      	<br>


  </fieldset>
  
  </td>
  </tr>
  <tr>
  <td>

    <TABLE border="0" cellspacing="0" align="center" cellpadding="0">
  	<TR>
		<TD align="center" height="35px;"><INPUT TYPE="button" VALUE="确定" ONCLICK="enter()" class="mybutton">
		<INPUT TYPE="button" VALUE="取消" ONCLICK="closeok()" class="mybutton"></TD>
  	</TR>
  </TABLE>
  </td>
  </tr>
    </TABLE>
  </body>
<script language="javascript">
</script>
</html>