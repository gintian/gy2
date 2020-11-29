<%@ page contentType="text/html; charset=UTF-8" language="java"%>
<%@page
	import="com.hjsj.hrms.actionform.train.trainexam.exam.TrainExamStudentForm"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean"%>
<%@ taglib uri="/tags/struts-html" prefix="html"%>
<%@ taglib uri="/tags/struts-logic" prefix="logic"%>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms"%>
<%@ page import="com.hjsj.hrms.valueobject.common.FieldItemView"%>

<script language="JavaScript" src="/js/validateDate.js"></script>
<!-- script type="text/javascript" src="/js/validate.js">
</script -->
<script language="javascript">
function save0(){
	trainExamStudentForm.action = "/train/trainexam/exam/student.do?b_save=save";
	trainExamStudentForm.submit();
}
function returnback0(){
	trainExamStudentForm.action = "/train/trainexam/exam/student.do?b_query=return";
	trainExamStudentForm.submit();
}
</script>
<%
    TrainExamStudentForm form = (TrainExamStudentForm) session
					.getAttribute("trainExamStudentForm");
			int len = form.getFieldlist().size();
%>
<style>
body {
	padding-top: 5px;
	text-align: center;
}
</style>
<html:form action="/train/trainexam/exam/student">
	<table width="96%" align="center" border="0" cellpadding="0"
		cellspacing="0">
		<tr>
			<td valign="top">
				<table width="100%" border="0" cellpadding="3" cellspacing="0"
					align="center" class="ListTable">
					<tr height="20">
						<td colspan="4" align="left" valign="bottom" class="TableRow">
							&nbsp;&nbsp;考试成绩&nbsp;
						</td>
					</tr>
					<tr class="trDeep">
						<%
						    int i = 0, j = 0;
						%>
						<logic:iterate id="element" name="trainExamStudentForm"
							property="fieldlist" indexId="index">
							<%
							    if (i == 2) {
													if (j % 2 == 0) {
							%>
						
					</tr>
					<tr class="trShallow">
						<%
						    } else {
						%>
					</tr>
					<tr class="trDeep">
						<%
						    }
												i = 0;
												j++;
											}
											FieldItemView abean = (FieldItemView) pageContext
													.getAttribute("element");
											if (!"r5400".equals(abean.getItemid())
													&& !"nbase".equals(abean.getItemid())
													&& !"a0100".equals(abean.getItemid())) {
						%>
						<logic:notEqual name="element" property="itemtype" value="M">
							<td align="right" class="RecordRow" nowrap>
								<bean:write name="element" property="itemdesc" filter="true" />
							</td>
							<td align="left" class="RecordRow" nowrap>
								<logic:equal name="element" property="codesetid" value="0">
									<logic:notEqual name="element" property="itemtype" value="D">
										<logic:equal name="element" property="itemtype" value="N">
											<!--  不能修改分数
											<logic:equal name="element" property="itemid" value="r5501">
												<html:text maxlength="50" size="30" styleClass="textbox"
														onkeypress="event.returnValue=IsDigit2(this);"
														onblur='isNumber(this);' name="trainExamStudentForm"
														styleId="${element.itemid}"
														property='<%="fieldlist[" + index
														+ "].value"%>' style="border:1px solid #DBDFE6;"/>
											</logic:equal>
											<logic:equal name="element" property="itemid" value="r5503">
												<html:text maxlength="50" size="30" styleClass="textbox"
														onkeypress="event.returnValue=IsDigit2(this);"
														onblur='isNumber(this);' name="trainExamStudentForm"
														styleId="${element.itemid}"
														property='<%="fieldlist["
																	+ index
																	+ "].value"%>' style="border:1px solid #DBDFE6;" />
											</logic:equal>
											-->
											<logic:equal name="element" property="itemid" value="r5513">
												<logic:empty name="element" property="value">
													<input type="text" size="30" readonly="true"
														class="textColorRead" />
												</logic:empty>
												<logic:equal value="-1" name="element" property="value">
													<input type="text" size="30" readonly="true"
														class="textColorRead" value="未考" />
												</logic:equal>
												<logic:equal value="0" name="element" property="value">
													<input type="text" size="30" readonly="true"
														class="textColorRead" value="正考" />
												</logic:equal>
												<logic:equal value="1" name="element" property="value">
													<input type="text" size="30" readonly="true"
														class="textColorRead" value="已考" />
												</logic:equal>
											</logic:equal>
											<logic:equal name="element" property="itemid" value="r5515">
												<logic:empty name="element" property="value">
													<input type="text" size="30" class="textColorRead" />
												</logic:empty>
												<logic:equal value="-1" name="element" property="value">
													<input type="text" size="30" readonly="true"
														class="textColorRead" value="未阅" />
												</logic:equal>
												<logic:equal value="0" name="element" property="value">
													<input type="text" size="30" readonly="true"
														class="textColorRead" value="正阅" />
												</logic:equal>
												<logic:equal value="1" name="element" property="value">
													<input type="text" size="30" readonly="true"
														class="textColorRead" value="已阅" />
												</logic:equal>
												<logic:equal value="2" name="element" property="value">
													<input type="text" size="30" readonly="true"
														class="textColorRead" value="发布" />
												</logic:equal>
											</logic:equal>
											<!--
											<logic:notEqual name="element" property="itemid" value="r5501">
											<logic:notEqual name="element" property="itemid" value="r5503">
											</logic:notEqual>
											</logic:notEqual>
											-->
											<logic:notEqual name="element" property="itemid"
												value="r5513">
												<logic:notEqual name="element" property="itemid"
													value="r5515">
													<logic:equal name="element" property="decimalwidth"
														value="0">
														<html:text maxlength="50" size="30"
															styleClass="textColorRead" name="trainExamStudentForm"
															styleId="${element.itemid}"
															property='<%="fieldlist["
															+ index + "].value"%>'
															readonly="true" />
													</logic:equal>
													<logic:notEqual name="element" property="decimalwidth"
														value="0">
														<html:text maxlength="50" size="30"
															styleClass="textColorRead" name="trainExamStudentForm"
															styleId="${element.itemid}"
															property='<%="fieldlist["
															+ index + "].value"%>'
															readonly="true" />
													</logic:notEqual>
												</logic:notEqual>
											</logic:notEqual>

										</logic:equal>
										<logic:notEqual name="element" property="itemtype" value="N">
											<html:text maxlength="50" size="30"
												styleClass="textColorRead" name="trainExamStudentForm"
												styleId="${element.itemid}"
												property='<%="fieldlist[" + index
													+ "].value"%>'
												readonly="true" />
										</logic:notEqual>
									</logic:notEqual>
									<logic:equal name="element" property="itemtype" value="D">
										<input type="text"
											name='<%="fieldlist[" + index
													+ "].value"%>' maxlength="50"
											size="30" id="${element.itemid}" class="textColorRead"
											value="${element.value}" readonly="true">
									</logic:equal>
								</logic:equal>

								<logic:notEqual name="element" property="codesetid" value="0">
									<logic:equal name="element" property="itemid" value="b0110">
										<html:hidden name="trainExamStudentForm"
											property='<%="fieldlist[" + index
												+ "].value"%>' />
										<html:text maxlength="50" size="30" styleClass="textColorRead"
											name="trainExamStudentForm"
											property='<%="fieldlist[" + index
												+ "].viewvalue"%>'
											readonly="true" />
									</logic:equal>
									<logic:notEqual name="element" property="itemid" value="b0110">
										<html:hidden name="trainExamStudentForm"
											property='<%="fieldlist[" + index
												+ "].value"%>' />
										<html:text maxlength="50" size="30" styleClass="textColorRead"
											name="trainExamStudentForm"
											property='<%="fieldlist[" + index
												+ "].viewvalue"%>'
											readonly="true" />
									</logic:notEqual>
								</logic:notEqual>
								<%
								    i++;
								%>
							</td>
							<%
							    if (index < len - 1) {
							%>
							<logic:equal name="trainExamStudentForm"
								property='<%="fieldlist["
												+ Integer.toString(index
														.intValue() + 1)
												+ "].itemtype"%>'
								value="M">
								<%
								    if (i < 2) {
								%>
								<td align="left" class="RecordRow" nowrap></td>
								<td align="left" class="RecordRow" nowrap></td>
								<%
								    i++;
																			}
								%>

							</logic:equal>
							<%
							    } else if (index == len - 1) {
							%>
							<%
							    if (i < 2) {
							%>
							<td align="left" class="RecordRow" nowrap></td>
							<td align="left" class="RecordRow" nowrap></td>
							<%
							    i++;
																	}
							%>
							<%
							    }
							%>
						</logic:notEqual>
						<logic:equal name="element" property="itemtype" value="M">
							<td align="right" class="RecordRow" nowrap valign="top">
								<bean:write name="element" property="itemdesc" filter="true" />
							</td>
							<td align="left" class="RecordRow" nowrap colspan="3">
								<html:textarea name="trainExamStudentForm"
									property='<%="fieldlist[" + index
											+ "].value"%>'
									cols="90" rows="6" styleClass="textboxMul common_border_color"></html:textarea>
							</td>
							<%
							    i = 2;
							%>
						</logic:equal>
						<%
						    }
						%>
						</logic:iterate>

					</tr>
				</table>
				<table width='100%' align='center' cellpadding="0" cellspacing="0">
					<tr>
						<td align='left' style="padding-top: 5px;">
							<logic:notEqual name="trainExamStudentForm" property="planStatus"
								value="04">
								<input type='button' value='<bean:message key='button.save' />'
									class="mybutton" onclick="save0();">
							</logic:notEqual>
							<input type="button" class="mybutton"
								value="<bean:message key='button.return'/>"
								onClick="returnback0();">
						</td>
					</tr>
				</table>
				</html:form>
				<script>
	//输入整数
function IsDigit2(obj) {
	if ((event.keyCode > 47) && (event.keyCode <= 57)) {
		return true;
	} else {
		return false;
	}
}
function isNumber(obj) {
	var checkOK = "-0123456789.";
	var checkStr = obj.value;
	var allValid = true;
	var decPoints = 0;
	var allNum = "";
	if (checkStr == "") {
		return;
	}
	var count = 0;
	var theIndex = 0;
	for (i = 0; i < checkStr.length; i++) {
		ch = checkStr.charAt(i);
		if (ch == "-") {
			count = count + 1;
			theIndex = i + 1;
		}
		for (j = 0; j < checkOK.length; j++) {
			if (ch == checkOK.charAt(j)) {
				break;
			}
		}
		if (j == checkOK.length) {
			allValid = false;
			break;
		}
		if (ch == ".") {
			allNum += ".";
			decPoints++;
		} else {
			if (ch != ",") {
				allNum += ch;
			}
		}
	}
	if (count > 1 || (count == 1 && theIndex > 1)) {
		allValid = false;
	}
	if (decPoints > 1 || !allValid) {
		alert("\u8bf7\u8f93\u5165\u6570\u503c\u7c7b\u578b\u7684\u503c\uff01");
		obj.value = "";
		obj.focus();
	}
}
</script>