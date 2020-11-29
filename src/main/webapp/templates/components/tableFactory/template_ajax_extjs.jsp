<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>

<html>
<head>
<meta http-equiv="X-UA-Compatible" content="IE=9; IE=8; IE=7" >
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">

<script type='text/javascript' src='../../../ext/ext6/ext-all.js'></script>
<script type='text/javascript' src='../../../ext/ext6/locale-zh_CN.js' ></script>
<script type='text/javascript' src='../../../ext/ext6/ext-additional.js' ></script>
<link rel='stylesheet' href='../../../ext/ext6/resources/ext-theme.css' type='text/css' />
<script type="text/javascript" src="../../ext/rpc_command.js"></script>
<script type='text/javascript' src='/ajax/basic.js'></script>
<script type='text/javascript' src='/js/validate.js'></script>
<!-- 兼容旧程序功能  wangb 20171117 -->
<script language="javascript" src="/ajax/common.js"></script>
<script type="text/javascript" src="/js/hjsjUrlEncode.js"></script>
</head>
<body>
<table width="100%" height="100%" align="center" border="0" cellpadding="0" cellspacing="0">
  <tr>  
    <td valign="top" style="overflow:auto" >
       <hrms:insert parameter="HtmlBody" />
    </td>
  </tr>
</table>

</body>

</html>