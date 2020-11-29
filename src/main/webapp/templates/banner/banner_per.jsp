<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<script language="JavaScript1.2">
  
  function isclose()
  {
  	if(confirm("确定要注销吗？"))
  	{
   		var url = "/templates/index/perLogon.jsp";
		newwin=window.open(url,"_parent","toolbar=no,location=no,directories=0,status=no,menubar=no,scrollbars=no,resizable=no","true");
		//window.opener=null;//不会出现提示信息
   		//parent.window.close();	
  	}
  }
</script>

<link rel="stylesheet" href="/css/css1.css" type="text/css">
<body topmargin="0" bottommargin="0" marginheight="0" style="margin:0 0 0 0">
<table width="998" height="125" border="0" align="center" cellpadding="0" cellspacing="0">
  <tr>
	<td background="/images/06233_01.jpg" width="100%" height="15" valign="top" align="left" style="background-repeat: repeat;background-position: center bottom;"> 
	<table width="100%" border="0" cellspacing="0" cellpadding="0">
  <tr>
    <td width="85%">&nbsp;</td>
    <td width="13%">
        <hrms:priv func_id="000101,3010" module_id=""> 						
							<hrms:link href="/system/security/resetup_password.do" target="
							_blank" ><img src="/images/keylock.gif" border=0 title="<bean:message key="label.mail.password"/>"></hrms:link>
    		  			</hrms:priv> 
	<input name="reenter" type="image" src="/images/reenter.gif" title="<bean:message key="label.banner.relogin"/>" onclick="isclose();">					
	<input name="b_exit" type="image" src="/images/exit.gif" title="退出" onclick="parent.window.close();">
	</td>
    <td width="2%">&nbsp;
	
	</td>
  </tr>
</table>
</td>
</tr>
<tr>
    <td background="/images/062_01.jpg" width="100%" height="110" valign="top" align="left" style="background-repeat: no-repeat"> 
      <object classid="clsid:D27CDB6E-AE6D-11cf-96B8-444553540000" codebase="http://download.macromedia.com/pub/shockwave/cabs/flash/swflash.cab#version=6,0,29,0" width="998" height="100">
        <param name="movie" value="/images/HEAD.swf">
        <param name="quality" value="high">
        <embed src="/images/HEAD.swf" quality="high" pluginspage="http://www.macromedia.com/go/getflashplayer" type="application/x-shockwave-flash" width="998" height="100"></embed></object></td>
	</tr>
	<tr>
	<td background="/images/062_022.jpg" width="100%" height="10" valign="top" align="left" style="background-repeat: repeat"> 
	</td>
	</tr>
</table>
</body>