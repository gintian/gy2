<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Insert title here</title>
</head>
<body>
<form action="http://192.192.100.6:8080/logon/logonService" method="post" name="form1">
	<input type="hidden"  name="QueryString" value="A55549C59C593B9C9860739805F58DCD5B4B7EC750E919BB987795BE0BB8F3362339E6BD2AF8D0C8" >

	<input type="hidden" name="flag" value="7" >
	<input type="hidden" name="user_ID" value="su" >	
	<input type="hidden" name="password" value="" >
	<input type="hidden" name="validatepwd" value="false" >
	
	<input type="submit" name="test" value="test">
</form>
<script language="JavaScript">
alert("sss");
function sub(flag)
{
 alert("SS");
 if(flag=="0")
 {
   document.form1.submit();
 }else{
   window.location.href = "http://127.0.0.1:8038/cas/logout";
 }
}
sub("0");
</script>

</body>
</html>