<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean"%>
<%@ taglib uri="/tags/struts-html" prefix="html"%>
<%@ taglib uri="/tags/struts-logic" prefix="logic"%>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms"%>
<div class="splitpage" style="margin-top: 7px;">
	<table width="80%" border="0" cellspacing="1" align="center" cellpadding="1">
		<bean:write name="reportListForm" property="reportExprAnalyseResult" filter="false" />
	</table>
</div>
<center>
	<input type="button" name="b_add" value="<bean:message key="field_result.return"/>" class="mybutton"
	 onClick="history.back();" style='margin-top: 4px;'>
</center>
