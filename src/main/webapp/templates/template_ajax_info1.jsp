<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title></title>
</head>
<script type="text/javascript" src="/js/hjsjUrlEncode.js"></script>
<body style="margin:0 0 0 0">
<table width="100%" height="90%" align="center" border="0" cellpadding="0" cellspacing="0">
  <tr>  
    <td valign="top" style="overflow:auto">
       <hrms:insert parameter="HtmlBody" />
    </td>
  </tr>
</table>
</body>
<script language="javascript">
	//解决IE文本框自带历史记录问题  jingq add 2014.12.31
	var inputs = document.getElementsByTagName("input");
	for ( var i = 0; i < inputs.length; i++) {
		if(inputs[i].getAttribute("type")=="text"){
			inputs[i].setAttribute("autocomplete","off");
		}
	}
</script>
</html>