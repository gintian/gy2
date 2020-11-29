<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean"%>
<%@ taglib uri="/tags/struts-html" prefix="html"%>
<%@ taglib uri="/tags/struts-logic" prefix="logic"%>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms"%>
<script language="JavaScript" src="param.js"></script>
<html:form action="/gz/bonus/param/baseparam">
<table border=0 width="100%" class="ListTableF">
	<tr>
		<td>
			<fieldset style="width:100%">
				<legend>
					<bean:message key="label.select" />
				</legend>
				<table width="100%" border="0" cellpmoding="0" cellspacing="0"
					cellpadding="0" align="center">
					<logic:iterate id="element" name="bonusParamForm"
						property="nbase">
						<tr>
							<td width="70%">
								<input name="db" type="checkbox"
									value="<bean:write name="element" property="pre" filter="true" />"
									<logic:notEqual name="element" property="dbsel"
											value="0">checked</logic:notEqual> />
								<bean:write name="element" property="dbname" filter="true" />
							</td>
						</tr>
					</logic:iterate>
				</table>
			</fieldset>
		</td>
	</tr>
	<tr>
		<td>
			<fieldset style="width:100%;">
				<legend>
					<bean:message key="label.select" />
				</legend>
				<table  border="0" cellpmoding="0" cellspacing="0"
					cellpadding="0">
						<tr>
							<td align="right">
								<bean:message key="gz.bonus.subset" />&nbsp;
							</td>
							<td>
								 <html:select name="bonusParamForm" property="bonusSet" size="1" style="width:200">
                          			 <html:optionsCollection property="bonusSetList" value="dataValue" label="dataName"/>
                     			 </html:select>
                     			 （关联子集需对应的指标或代码：处理状态（51），奖金项目（49），奖金审批单位（50），金额，业务日期，进工资总额标识（45），进工资标识（45））
							</td>
						</tr>	
						<tr>
							<td align="right">
								<bean:message key="gz.bonus.jobnum" />&nbsp;
							</td>
							<td>
								 <html:select name="bonusParamForm" property="jobnum" size="1" style="width:200">
                          			 <html:optionsCollection property="jobnumList" value="dataValue" label="dataName"/>
                     			 </html:select>
							</td>
						</tr>	
				</table>
			</fieldset>
			<center>
				<input type="button"  value="<bean:message key='button.save'/>" onclick="saveBaseParam();" Class="mybutton" style="margin-top: -3px;margin-bottom: 1px;">
			</center>
		</td>
	</tr>
	
</table>
	<html:hidden name="bonusParamForm" property="paramStr"/>
	<html:hidden name="bonusParamForm" property="menuid"/>
</html:form>