
<%@ page contentType="text/html; charset=UTF-8" language="java"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean"%>
<%@ taglib uri="/tags/struts-html" prefix="html"%>
<%@ taglib uri="/tags/struts-logic" prefix="logic"%>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms"%>
<%@ page import="com.hrms.struts.valueobject.UserView"%>
<%@ page import="com.hrms.struts.constant.WebConstant"%>

<hrms:themes></hrms:themes>
<script language="JavaScript" src="/js/validate.js"></script>
<script language="JavaScript" src="/js/function.js"></script>
<script language="JavaScript" src="/module/utils/js/template.js"></script>
<script language="JavaScript" src="/js/popcalendar3.js"></script>
<script type="text/javascript" language="javascript">
function update(){
	if(exportForm.jobclass.value.length<1){
		alert('<bean:message key="sys.export.inputjobclass"/>');
		return;
	}
	if(exportForm.description.value.length<1){
		alert('<bean:message key="sys.export.inputdescription"/>');
		return;
	}
	if(exportForm.description.value.length>100){
		alert('<bean:message key="sys.export.errordescription"/>');
		return;
	}
	if(exportForm.job_time.value.length>100){
		alert('<bean:message key="sys.export.inputjob_time"/>');
		return;
	}
	var trigger= document.exportForm.trigger.value;
	if(trigger!=null&&trigger.length!=0&&trigger==0){
	var time = exportForm.job_time.value.split("|");
		if(time.length!=4){
			alert('<bean:message key="sys.export.errorjob_time"/>');
			return;
		}
	}
	if(exportForm.status.checked){
		exportForm.status_flag.value="1";
	}else{
		exportForm.status_flag.value="0";
	}
	exportForm.action="/system/export/searchclass_info.do?b_edit=link";
	exportForm.submit();
}
function edittime()
{
	var info= document.exportForm.trigger.value;
	
	var info2= document.exportForm.job_time.value;
	var arr=new Array(info,info2);
	var info1= "/system/export/edittime.jsp";
	var dw=680,dh=600,dl=(screen.width-dw)/2;dt=(screen.height-dh)/2;

	//兼容浏览器 wangbs 20190319
    Ext.create("Ext.window.Window",{
		id:"dateSetWin",
		title:'设置',
		width:dw,
		height:dh,
		arr:arr,
		resizable:false,
		modal:true,
		autoScroll:false,
		autoShow:true,
		autoDestroy:true,
		renderTo:Ext.getBody(),
		html:"<iframe style='background-color:#ffffff' frameborder='0' SCROLLING=NO height='100%' width='100%' src='"+info1+"'></iframe>",
		listeners:{
		    close:function(){
                if(this.return_vo){
                    document.exportForm.job_time.value = this.return_vo[1];
                    document.exportForm.trigger.value = this.return_vo[0];
                }
			}
		}
    });
}
</script>
<html:form action="/system/export/editclass_info">
<%--底部按钮不居中 wangbs 20190319--%>
<table width="650px" border="0" cellspacing="0" align="center" cellpadding="0">
	<tr>
		<td><html:hidden styleId="job_id" name="exportForm" property="jobsvo.int(job_id)"/></td>
		<td><html:hidden styleId="trigger_flag" name="exportForm" property="jobsvo.int(trigger_flag)"/></td>
	</tr>
	<tr>
		<td ><html:hidden name="exportForm" property="status_flag"/></td>
	</tr>
	<tr>
		<td colspan="2">
			<%--为兼容浏览器 宽度加px wangbs 20190319--%>
		<fieldset align="center" style="width:650px;">
<legend>作业类修改</legend>
<table width="100%" border="0" cellspacing="1"  cellpadding="1">
	<tr>
		<td width="15%" align="right" style="padding-right:10px;"><bean:message key="sys.export.jobclass"/></td>
		<td><html:text styleId="jobclass" name="exportForm" property="jobsvo.string(jobclass)" size="41" maxlength="100" readonly="true" styleClass="text4 common_border_color" style="width:500px;"></html:text>
		</td>
	</tr>
	<tr>
		<td valign="top" align="right" style="padding-right:10px;"><bean:message key="sys.export.description"/></td>
		<td><html:textarea styleId = "description" name="exportForm" property="jobsvo.string(description)" cols="40" rows="8" style="width:500px;"></html:textarea>
		</td>
	</tr>
	<tr>
		<td valign="top" align="right" style="padding-right:10px;"><bean:message key="sys.export.jobparam"/></td>
		<td><html:textarea styleId = "jobparam" name="exportForm" property="jobsvo.string(job_param)" cols="40" rows="8" style="width:500px;"></html:textarea>
		</td>
	</tr>
	<tr>
		<td align="right" style="padding-right:10px;"><bean:message key="sys.export.job_time"/></td>
		<td><html:text styleId = "job_time" name="exportForm" property="jobsvo.string(job_time)"  size="35" maxlength="50" readonly="true" styleClass="text4 common_border_color" style="width:461px;"></html:text>
		<html:button style="position:absolute;" styleClass="mybutton" property="b_edit" onclick="edittime();"><bean:message key="button.orgmapset"/></html:button >
		</td>
	</tr>
	<tr>
		<td align="right" style="padding-right:10px;"><bean:message key="sys.export.status"/></td>
		<td>
		   <html:checkbox styleId = "status" name="exportForm" property="jobsvo.int(status)" value="1"><bean:message key="label.zp_resource.status1"/></html:checkbox>
		</td>
	</tr>
	<tr>
		<td>&nbsp;</td>
		<td>&nbsp;</td>
	</tr>
	
</table>
</fieldset>
		</td>
	</tr>
	<tr>
		<td colspan="2" align="center" height="35px;"><html:button  styleClass="mybutton" property="b_edit" onclick="update();"><bean:message key="label.edit"/></html:button >
		<html:button  styleClass="mybutton" property="b_retrun" onclick="history.back();"><bean:message key="button.return"/></html:button >
		<input type=hidden name='trigger' value="${exportForm.trigger}">
		</td>
	</tr>
</table>
</html:form>
