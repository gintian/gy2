<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean"%>
<%@ taglib uri="/tags/struts-html" prefix="html"%>
<%@ taglib uri="/tags/struts-logic" prefix="logic"%>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms"%>

<html:form action="/selfservice/propose/replyconsulant">
	<table width="500" border="0" cellpadding="0" cellspacing="0"
		class="ftable" align="center" style="margin-top: 6px;">
		<tr height="20">
			<td align="left" colspan="2" class="TableRow">
				<bean:message key="lable.consult.replay" />
				&nbsp;
			</td>
		</tr>

		<tr class="list3">
			<td align="right" nowrap valign="top">
				<bean:message key="column.submit.consult" />
			</td>
			<td align="left" nowrap>
				<html:textarea name="consulantForm"
					property="consulantvo.string(ccontent)" cols="80" rows="12"
					readonly="true" />
			</td>
		</tr>

		<tr class="list3">
			<td align="right" nowrap valign="top">
				<bean:message key="column.reply.content" />
			</td>
			<td align="left" nowrap>
				<html:textarea name="consulantForm"
					property="consulantvo.string(rcontent)" cols="80" rows="12" />
			</td>
		</tr>

		<tr class="list3">
			<td align="center" colspan="2" style="height: 35px;">
				<hrms:submit styleClass="mybutton" property="b_save"
					onclick="document.consulantForm.target='_self';validate('R','consulantvo.string(rcontent)','答复内容');return (document.returnValue && ifqrbc());">
					<bean:message key="button.save" />
				</hrms:submit>
				<hrms:submit styleClass="mybutton" property="br_return">
					<bean:message key="button.return" />
				</hrms:submit>
			</td>
		</tr>
	</table>
	<script language='javascript'>
		if(!getBrowseVersion()){//兼容非IE 浏览器  wangb 20171127
			var textarea = document.getElementsByTagName('textarea');//调整 文本域 高度 大小
			for(var i = 0 ; i < textarea.length ; i++){
				textarea[i].setAttribute('rows','10');
				textarea[i].style.marginTop ='2px'; // bug 34326 wangb 20180130 文本域边框和table边框重叠 
				textarea[i].style.marginBottom ='2px'; // bug 34326 wangb 20180130 文本域边框和table边框重叠
				textarea[i].style.resize = 'none'; //文本域可以拉伸   添加禁止拉伸样式 resize bug 34943 wangb 20180226
			}
		}
	</script>
</html:form>
