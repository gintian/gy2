<%@ page contentType="text/html; charset=UTF-8"%>
<%@page import="com.hjsj.hrms.utils.PubFunc"%>
<%
String sPeopleId="",sMsg="",sRanDom="",sMd5Result="";
sPeopleId = request.getParameter("account");
sRanDom=request.getParameter("random");
sMd5Result=request.getParameter("md5Result");
String flag="";
if(sPeopleId==null||sPeopleId.length()<=0||sRanDom==null||sRanDom.length()<=0||sMd5Result==null||sMd5Result.length()<=0)
{
   sMsg="您还没有登录网站，请点击网站上的登录按钮登录！";
   flag="2";
}else
{
   String encryptResult=PubFunc.getMD5Encrypt(sPeopleId,sRanDom,"");
   if(encryptResult!=null&&encryptResult.equals(sMd5Result))
   {
      sMsg="";
      flag="1";
   }else
   {
      sMsg="姓名代码不存在或登录口令不正确！";
      flag="2";
   }
}

%>
<html>
<HEAD>
<META NAME="GENERATOR" Content="Microsoft Visual Studio 6.0">
</HEAD>
<BODY >

</BODY>
<form name="form1" id="form1"  action="/logon/logonService" method="post">
	<input type="hidden" name="flag" value="14" >
	<input type="hidden" name="validatepwd" value="false" >
	<input type="hidden" name="user_ID" value="<%=sPeopleId%>" >	
	<input type="hidden" name="password" value="" >
</form>
</HTML>
<script language="javascript">
function sub(flag)
{
   if(flag=="2")
   {
      alert("<%=sMsg%>");
      window.location.href = "/hrms/templates/index/employLogon.jsp"	
   }else
   {
      document.form1.submit();
   }
}
sub("<%=flag%>");
</script>

