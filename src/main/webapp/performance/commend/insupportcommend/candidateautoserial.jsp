<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<script type="text/javascript">
<!--
function serial(){
	var autonum=$F("autoNumber");
    parent.window.returnValue=autonum;
    parent.window.close();
}
//-->
</script>
<table border="0" cellpadding="0" cellspacing="0" width="90%" align="center" style="margin-top: 15px;border-collapse: collapse;">
	<tr>
		<td class="RecordRow" height="25" bgcolor="#F4F7F7">&nbsp;<b>人员自动编号</b></td>
	</tr>
	<tr>
		<td class="RecordRow" width="100%">
			<table border="0" cellpadding="0" cellspacing="0" style="margin: 15 5 10 5;">
				<tr>
					<td colspan="2" valign="top" height="40">
						人员编号指标：&nbsp;
						<select name="autoNumber">
							<logic:iterate id="auto" name="inSupportCommendForm" property="autolist">
								<option value="${auto[0] }`${auto[2] }">${auto[1] }</option>
							</logic:iterate>
						</select>
					</td>
				</tr>
				<tr>
					<td width="37" valign="top" style="line-height: 21px;">说明：</td>
					<td valign="top" style="line-height: 20px;">根据候选人顺序，从1开始编号，编号长度由指标长度确定，前面补0。已有的编号会被覆盖。</td>
				</tr>
			</table>
		</td>
	</tr>
	<tr>
		<td class="RecordRow" align="center" style="padding: 5px;">
			<input type="button" value="确定" class="mybutton" onclick="serial();"/>&nbsp;
			<input type="button" value="关闭" class="mybutton" onclick="javascript:window.close();"/>
		</td>
	</tr>
</table>