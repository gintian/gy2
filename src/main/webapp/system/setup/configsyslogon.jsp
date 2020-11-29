<%@ page contentType="text/html; charset=UTF-8" language="java"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ page import="com.hrms.struts.valueobject.UserView"%>
<%@ page import="com.hrms.struts.constant.WebConstant"%>
<link rel="stylesheet" href="/css/css1.css" type="text/css">
<script language="JavaScript" src="/js/validate.js"></script>
<script language="JavaScript" src="/js/function.js"></script>
 <%
      int ti = session.getMaxInactiveInterval();
    %>
<script type="text/javascript">
<!--
	function valdate(){
	var username=configsysForm.username;
	var password=configsysForm.password;
	if(username==null||username.length<1)
	 alert("fdsfd");
	}
//-->
</script>
<html:form action="/system/setup/configsyslogon"> 
<table width="100%" align="center">
<tr>
<td>
<br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/>
<table width="30%" align="center">
<tr>
<td align="center"><bean:message key="label.mail.username"/>:
</td>
<TD align="center">
<input class="shuoming"  type="text" name="username" size='20'/>
</td>
</tr>
<tr>
<td align="center"><bean:message key="config.sys.info.password"/>:
</td>
<td align="center">
<input class="shuoming" type="password" name="password" size='20'/>
</td>
</tr>
<tr>
<td  colspan="2" align="center">
<hrms:submit styleClass="mybutton" property="b_logon" onclick="valdate();">
						<bean:message key="config.sys.setup.logon" />
					</hrms:submit>&nbsp;
<BUTTON class="mybutton" onclick='window.close();'>
<bean:message key="config.sys.setup.logonout"/>
</BUTTON>
</td>
</tr>
</table>
</td>
</tr>
</table>


</html:form>

  	 


    