<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<html>
<LINK href="/css/employNetStyle.css" type=text/css rel=stylesheet>
<LINK href="/css/main.css" type=text/css rel=stylesheet>
<LINK href="/css/nav.css" type=text/css rel=stylesheet>
 <script language="JavaScript" src="/hire/employNetPortal/employNetPortal.js"></script>
<body  bgcolor="#92D2EE">
<html:form action="/hire/employNetPortal/search_zp_position">
<br>
<table width="95%" border="0" cellspacing="0"  align="center" cellpadding="0" class="hj_zhaopin_list_tab_title">
<tr>
              <th style="font-size:12px;font-weight:bold;" colspan='2'><font class='FontStyle'><strong>请输入在本站的注册邮箱，系统将把您的密码发送到该邮箱</strong></font></th>
              </tr>
<tr>
<td id="desc" style='TEXT-ALIGN:right' height="30" width="40%">
<font class='FontStyle'>请输入注册邮箱:&nbsp;&nbsp;</font>
</td>
<td align="left"  height="30" width="60%" style="padding-left:6px;">

<input id="zzee" class='s_input' type="text" name="ZE" value="" size="30"/>
</td>
</tr>
<tr>
<td style="TEXT-ALIGN:center" colspan="2">
<table>
<tr>
<td style="TEXT-ALIGN:center">
<input type="button" style="font-family:verdana;border:darkgray 1px solid;font-size:9pt;cursor:hand;height:22px;background-color:#f2f2f2" name="oo" value="<bean:message key="button.ok"/>" onclick='sendmail();'/>
&nbsp;<input type="button" style="font-family:verdana;border:darkgray 1px solid;font-size:9pt;cursor:hand;height:22px;background-color:#f2f2f2" name="cc" value="<bean:message key="button.close"/>" onclick="window.close();"/>
</td>
</tr>
</table>
</td>
</tr>
</table>
</html:form>
</body>
</html>