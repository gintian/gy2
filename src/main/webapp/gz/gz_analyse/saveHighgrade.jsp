<%@ page language="java" import="java.util.*" pageEncoding="utf-8"%>
<%
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
String name=request.getParameter("name");
String str="1";
if(name.indexOf("鍏变韩")!=-1){
	str="0";
}

if(name.equals("undefined")){
	name="";
}
name=name.split("\\(")[0];
%>
<link rel="stylesheet" href="/css/css1.css" type="text/css">
<hrms:themes />
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
  <head>
    <base href="<%=basePath%>">
    
    <title>鏂规淇濆瓨</title>
    
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
			var name=document.getElementById("savename").value;  
			if(name.length=="0"){
				alert("鏂规鍚嶄笉鑳戒负绌猴紒");
				return;
			}
			var value="";
			var obj=document.getElementsByName("s");
			for(var i=0;i<obj.length;i++){
				if(obj[i].checked==true){
					value=obj[i].value;
				}
			}
			var re=new Array();
    		re[0]=name;
    		re[1]=value;
    		window.returnValue=re;
    		window.close();
		}
		function closeok(){
			var re=new Array();
    		re[0]="ssss";
			returnValue=re;
			window.close();
		}
	</script>
  <body>
  <br>
   <br>
  <TABLE width="280" border="0" cellspacing="0" align="center" cellpadding="0">
  	<TR>
		<TD align="right">鏂规鍚嶇О锛�</TD>
		<TD align="left"><INPUT TYPE="text" NAME="" id="savename" value="<%=name %>"></TD>
 	 </TR>
  </TABLE>
      	<br>

  <TABLE width="280" border="0" cellspacing="0" align="center" cellpadding="0">
 	<TR>
		<TD align="center"><INPUT TYPE="radio" NAME="s" value="1" checked>绉佹湁&nbsp;&nbsp;&nbsp;<INPUT TYPE="radio" NAME="s" value="0">鍏变韩</TD>
  	</TR>
  </TABLE>
      	<br>

  <TABLE width="280" border="0" cellspacing="0" align="center" cellpadding="0">
  	<TR>
		<TD align="center"><INPUT TYPE="button" VALUE="纭畾" ONCLICK="enter()" class="mybutton">&nbsp;&nbsp;&nbsp;<INPUT TYPE="button" VALUE="鍙栨秷" ONCLICK="closeok()" class="mybutton"></TD>
  	</TR>
  </TABLE>
  </body>
<script language="javascript">
var temp=document.getElementsByName("s");
for(var i=0;i<temp.length;i++){
	if(temp[i].value==<%=str%>){
		temp[i].checked=true;
	}
}
</script>
</html>

