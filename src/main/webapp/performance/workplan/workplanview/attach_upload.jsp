<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<SCRIPT LANGUAGE=javascript src="/performance/workplan/workplanview/workplanview.js"></SCRIPT>

<html:form action="/performance/workplan/workplanview/workplan_view_list" enctype="multipart/form-data" >
<html:hidden name="workPlanViewForm" property="p0100"/>
<table width="85%" border="0" cellspacing="0"  align="left" cellpadding="0" class="ListTable">
	<tr style="position:relative;top:expression(this.offsetParent.scrollTop);">
		<td class="TableRow" align="center">
			附件名称
		</td>
		<td class="TableRow" align="center">
			下载
		</td>
		<logic:equal value="1" name="workPlanViewForm" property="optPlan">
		<td class="TableRow" align="center">
			操作
		</td>
		</logic:equal>
	</tr>
	
	<logic:iterate id="attach" name="workPlanViewForm" property="attachList" indexId="attachIndex" offset="0">
	<tr>
		<td align="center" class="RecordRow">
 			<a href="/servlet/performance/fileDownLoad?opt=workView&p0100=${workPlanViewForm.p0100}&file_id=<bean:write name="attach" property="file_id"/>" target='_blank'> <bean:write name="attach" property="name"/></a>
		</td>
		<td align="center" class="RecordRow">
 			<a href="/servlet/performance/fileDownLoad?opt=workView&p0100=${workPlanViewForm.p0100}&file_id=<bean:write name="attach" property="file_id"/>" target='_blank'> <img src='/images/detail.gif' border=0 /></a>
		</td>
		<logic:equal value="1" name="workPlanViewForm" property="optPlan">
		<td align="center" class="RecordRow">
 			<a href="/performance/workplan/workplanview/workplan_view_list.do?b_saveattach=save&opt=2&file_id=<bean:write name="attach" property="file_id"/>">删除</a>
		</td>
		</logic:equal>
	</tr>
	</logic:iterate>
	
	<logic:equal value="1" name="workPlanViewForm" property="optPlan">
	<tr>
		<td align="left" class="RecordRow" colspan="3">
			<table><tr><td>
				文&nbsp;件&nbsp;名&nbsp;称：<input type="text" name="fileName" maxlength="250" size="40"/>
					</td>
				</tr>
				<tr>
					<td>
						上传文件路径:<input type="file" name="formFile" size="30" onchange='upload()' onkeydown= "if(event.keyCode==13) this.fireEvent('onchange');" />
					</td>
				</tr>
			</table>
		</td>
	</tr>
	</logic:equal>
</table>
</html:form>