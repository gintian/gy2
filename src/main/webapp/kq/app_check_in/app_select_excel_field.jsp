<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean"%>
<%@ taglib uri="/tags/struts-html" prefix="html"%>
<%@ taglib uri="/tags/struts-logic" prefix="logic"%>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms"%>
<link rel="stylesheet" href="/css/css1.css" type="text/css">
<script language="javascript" src="/js/validate.js"></script>
<hrms:themes /> <!-- 7.0css -->
<script type="text/javascript">
<!--
function fini(){
	var vo = document.getElementsByName("right_fields"); 
	var right_vo=vo[0];
	if(vo == null || right_vo.options.length == 0){
		alert("请选择导出的指标！")
	}else{
		var filedList = new Array()
		for(var i=0;i<right_vo.options.length;i++){
			filedList.push(right_vo.options[i].value);
		}
		window.returnValue = filedList.join(",");
		window.close();
	}
}
//-->
</script>
<html:form action="/kq/app_check_in/all_app_data">
	<div class="fixedDiv2" style="height: 100%;border: none">
	<table width="100%" align="center" cellpadding="0" cellspacing="0" class="ListTable"  >
		<tr>
			<th class="TableRow">
				<bean:message key="label.query.selectfield"/>
			</th>
		</tr>
		<tr>
			<td width="100%" class="RecordRow">
				<table width="100%">
					<tr>
						<td width="50%" align="left" colspan="2">
							<bean:message key="selfservice.query.queryfield"/>
						</td>
						<td width="50%" align="left"  colspan="2">
							<bean:message key="static.ytarget"/>
						</td>
					</tr>
					<tr>
						<td width="42%">
							<html:select name="appForm" property="left_fields" multiple="true" style="height:220px;width:100%;font-size:9pt" ondblclick="additem2('left_fields','right_fields');">
                           		<html:optionsCollection property="selectFieldList" value="dataValue" label="dataName"/>
                      		</html:select>
						</td>
						<td align="center" width="8%">
							<input type="button" class="mybutton" name="dd" value="<bean:message key='button.setfield.addfield'/>"  onclick="additem2('left_fields','right_fields');">
							<br><br/>
							<input type="button" class="mybutton" name="dd" value="<bean:message key='button.setfield.delfield'/>" onclick="removeitem('right_fields');">
						</td>
						<td width="42%">
							<html:select name="appForm" property="right_fields" multiple="true" style="height:220px;width:100%;font-size:9pt" ondblclick="removeitem('right_fields');">
                        		<html:optionsCollection property="excelFieldList"  value="dataValue" label="dataName"/>
                     		</html:select>
						</td>
						<td align="center" width="8%">
							<input type="button" class="mybutton" name="dd" value="<bean:message key='button.previous'/>"  onclick="upItem($('right_fields'));">
							<br><br/>
							<input type="button" class="mybutton" name="dd" value="<bean:message key='button.next'/>" onclick="downItem($('right_fields'));">
						</td>
					</tr>
				</table>
			</td>
		</tr>
	<logic:equal name="appForm" property="flag" value="1">
		<tr>
		<td align="left">
		<font color="red">提示：${appForm.desc}、起始时间、终止时间、申请类型为必选指标，请勿删除！</font>
		</td>
		</tr>
	</logic:equal>
		<tr>
			<td align="center" valign="middle" nowrap height="35">
				<input type="button" class="mybutton" name="dd" value="<bean:message key='button.ok'/>" onclick="fini()">
				<input type="button" class="mybutton" name="dd" value="<bean:message key='button.close'/>" onclick="window.close()">
			</td>
		</tr>
	</table>
	</div>
</html:form>