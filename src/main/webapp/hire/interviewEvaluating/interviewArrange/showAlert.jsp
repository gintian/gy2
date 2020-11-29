<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<html>
<script language='javascript'>
	var infos=dialogArguments;
	function back()
	{
		window.close();
	}
</script>
<body>
<hrms:themes></hrms:themes>
<html:form action="/hire/employActualize/employResume/batchSendMail">
<table width="100%" height='100%' align="center" border="0" cellpadding="0" cellspacing="0">
		<tr>
			<td align='left' valign='middle'class="TableRow" nowrap >
				系统未设置招聘短信接收业务参数,请到系统管理/通信平台/短信接口参数/设置短信业务接口如图增加
			</td>
		</tr>
		<tr>
			<td align='left' valign='middle'class="TableRow" nowrap >
				招聘业务参数,"业务类"请填写图中"描述"框中的内容。
			</td>
		</tr>
		<tr>
		<td align='center' valign='middle' class="TableRow" nowrap>
			<img src="/images/messagealert.jpg">
		</td>
		</tr>
		<tr>
		<td  align='center' class="TableRow" >
		<input type="button" class="mybutton" value="关 闭" onclick="back();">
		</td>
		</tr>
</table>
</html:form>
</body>

</html>