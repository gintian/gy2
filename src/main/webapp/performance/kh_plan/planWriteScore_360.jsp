<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean"%>
<%@ taglib uri="/tags/struts-html" prefix="html"%>
<%@ taglib uri="/tags/struts-logic" prefix="logic"%>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms"%>
<%@ page import="java.util.*,
				 com.hrms.struts.valueobject.UserView,
				 com.hrms.struts.constant.WebConstant,
				 org.apache.commons.beanutils.LazyDynaBean,
				 com.hjsj.hrms.actionform.performance.kh_plan.ExamPlanForm"%>
				 
<script>
	
</script>
<html:form action="/performance/kh_plan/kh_params" style="margin:0;padding:0;">
<div id="requireFieldDiv" style="margin-top:0px;margin-left:2px;width:98%;">
	<fieldset style="height:240px;">
		<legend style="font-size:11pt;">必填指标</legend>
		<%
			String tplId = request.getParameter("tplId");
			if (tplId == null || tplId.equals("") || tplId.equals("isNull")) {
		%>
		<p style="text-align:center; vlign:middle;"><bean:message key='jx.khplan.param2.specifyKHTemplate' /></p>
		<%
			} else {
				int i = 0;
		%>
		<%
				String requiredFieldStr = request.getParameter("requiredFieldStr");
	// 			requiredFieldStr = requiredFieldStr == null || requiredFieldStr.equals("") ?
	// 					((ExamPlanForm) session.getAttribute("examPlanForm")).getRequiredFieldStr() : requiredFieldStr;
			%>
		<div class="" style="overflow:auto;overflow-x:hidden;height:200px;position:absolute;top:39px;left:15px;">
			<table width="464" border="0" cellspacing="0" align="center" cellpadding="0" class="ListTable complex_border_color">
				<logic:iterate id="element" name="examPlanForm" property="requiredField">
					<tr>
						<td align="center" style="border-left:0;border-right:0;border-top:0;" class="RecordRow" nowrap>
						<%
							LazyDynaBean bean = (LazyDynaBean) pageContext.getAttribute("element");
							String point_id = (String) bean.get("point_id");
							String pointname = (String) bean.get("pointname");
							
							String checked = "checked";
							if (requiredFieldStr != null && !requiredFieldStr.equals("")) { // 必填指标为空，表示初次制定必选参数
								checked = requiredFieldStr.indexOf(point_id) < 0 ? "" : checked;
							}
						%>
							<input type="checkbox" <%=checked %> name="requiredField" value="<%=point_id %>" />
						<td align="left" class="RecordRow" style="border-left:0;border-right:0;border-top:0;" nowrap>&nbsp;&nbsp;<%=pointname %></td>
					</tr>
					<%i++; %>
				</logic:iterate>
			</table>
		</div>
		<%
			}
		%>
	</fieldset>
</div>
<div id="degreeDiv" style="margin-left:2px;margin-top:10px;width:98%;">
	<fieldset style="padding-left:10px;">
		<legend style="font-size:11pt;">必填规则</legend>
		<div id="tbl-container" style='height: 90px; width: 93%; align: center;margin-bottom:10px;'>
			<Br>
			<table border="0" cellspacing="0" align="center" cellpadding="0" align="center" style="width: 97%;">
				<tr align="center">
					<td id="rankTarget_name1">
						<html:checkbox styleId="upIsValid" name="examPlanForm" property="upIsValid" value="1" onclick="isCheckDegree();" /> <bean:message key='jx.khplan.param2.upperScore' /> 
						<html:select name="examPlanForm" property="upDegreeId" size="1" styleId="upDegreeId" style="width:150px" onchange="activeExcludeDegree()">
							<html:option value=""></html:option>
							<html:optionsCollection property="grade_template" value="dataValue" label="dataName" />
						</html:select>
						 &nbsp; <bean:message key='jx.khplan.param2.gradeShowMustFill' />
					</td>
				</tr>
				<tr><td>&nbsp;</td></tr>
				<tr align="center">
					<td id="rankTarget_name2">
						<html:checkbox styleId="downIsValid" name="examPlanForm" property="downIsValid" value="1" onclick="isCheckDegree();" /> <bean:message key='jx.khplan.param2.lowerScore' /> 
						<html:select name="examPlanForm" property="downDegreeId" size="1" styleId="downDegreeId" style="width:150px" onchange="activeExcludeDegree()">
							<html:option value=""></html:option>
							<html:optionsCollection property="grade_template" value="dataValue" label="dataName" />
						</html:select>
						 &nbsp; <bean:message key='jx.khplan.param2.gradeShowMustFill' />
					</td>
				</tr>
				<tr><td>&nbsp;</td></tr>
				<tr align="center">
					<logic:equal name="examPlanForm" property="upDegreeId" value="">
						<logic:equal name="examPlanForm" property="downDegreeId" value="">
							<bean:define id="excludeDegreeDisabled" value="true"></bean:define>
						</logic:equal>
					</logic:equal>
					<td id="rankTarget_name3">
						&nbsp;&nbsp;<bean:message key='jx.khplan.param2.evaluateAs' />&nbsp;
						<html:select name="examPlanForm" property="excludeDegree" size="1" styleId="excludeDegree" style="width:150px" disabled="${excludeDegreeDisabled }">
							<html:option value=""></html:option>
							<html:optionsCollection property="grade_template" value="dataValue" label="dataName" />
						</html:select> &nbsp; <bean:message key='jx.khplan.param2.gradeShowNonRequired' />
					</td>
				</tr>
			</table>
		</div>
		<br /> &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
		<bean:message key='jx.khplan.param2.gradeShowHighorUnder' />
	</fieldset>
</div>

<table border="0" cellspacing="0" align="center" cellpadding="0">
	<tr align="center">
		<td style="padding-top:5px;">
			<input type="button" value="<bean:message key='button.ok' />" id="b_ok" class="mybutton" onclick="saveRankTarget();" /> &nbsp; 
			<input type="button" value="<bean:message key='button.cancel' />" id="b_cansal" class="mybutton" onclick="closewindow();" />
		</td>
	</tr>
</table>
	
<script>
	var $ = function(sId) {
		return document.getElementById(sId);
	};
	
	var upIsValid = $("upIsValid");
	var downIsValid = $("downIsValid");
	var upDegreeId = $("upDegreeId");
	var downDegreeId = $("downDegreeId");
	var excludeDegree = $("excludeDegree");
	
	// 复写原先的如下两个函数
	isCheckDegree = function() {
		if (upIsValid.checked) {
			upDegreeId.disabled = false;
		} else {
			upDegreeId.disabled = true;
			upDegreeId.value = "";
		}
	
		if (downIsValid.checked) {
			downDegreeId.disabled = false;
		} else {
			downDegreeId.disabled = true;
			downDegreeId.value = "";
		}
		
		activeExcludeDegree();
	};
	
	saveRankTarget = function() {
		var thevo = new Object();
		thevo.upIsValid = upIsValid.checked;
		thevo.downIsValid = downIsValid.checked;
		thevo.upDegreeId = upDegreeId.value;
		thevo.downDegreeId = downDegreeId.value;
		thevo.excludeDegree = excludeDegree.value;
		
		var requiredField = document.getElementsByName("requiredField");
		var requiredFieldStr = "";
		for (var i = 0; i < requiredField.length; i++) {
			if (requiredField[i].checked) {
				requiredFieldStr += requiredField[i].value + ",";
			}
		}
		if (requiredFieldStr === "") {
			alert("请至少选择一个必填指标。");
			return;
		}
		thevo.requiredFieldStr = requiredFieldStr;
		
		thevo.flag = "true";

        if(window.showModalDialog) {
            parent.window.returnValue = thevo;
            parent.window.close();
        }else{
            parent.parent.window.opener.window.mustWriteScore_ok(thevo);
            window.open("about:blank","_top").close();
        }
	}
    function closewindow()
    {
        if(window.showModalDialog) {
            parent.window.close();
        }else{
            window.open("about:blank","_top").close();
        }
    }
	var activeExcludeDegree = function() {
		if ((upIsValid.checked && upDegreeId.value !== "") || (downIsValid.checked && downDegreeId.value !== "")) {
			excludeDegree.disabled = false;
		} else {
			excludeDegree.disabled = true;
			excludeDegree.value = "";
		}
	};

	isCheckDegree();

	var theStatus = '${examPlanForm.status}';
	if (theStatus == '5' || theStatus == '0')
		document.getElementById("b_ok").disabled = false;
	else
		document.getElementById("b_ok").disabled = true;
</script>
</html:form>
