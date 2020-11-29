<%@ page contentType="text/html; charset=UTF-8"%>
<%@page import="com.hrms.frame.codec.SafeCode"%>
<%@page import="com.hjsj.hrms.utils.PubFunc"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean"%>
<%@ taglib uri="/tags/struts-html" prefix="html"%>
<%@ taglib uri="/tags/struts-logic" prefix="logic"%>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms"%>
<script language="javascript" src="/js/dict.js"></script>
<script language="JavaScript" src="/js/validateDate.js"></script>
<script type="text/javascript" src="../../../js/hjsjUrlEncode.js"></script>
<style>
body {
	text-align: center;
	padding-left: 5px;
}

.notop {
	border-top: none;
}
</style>
<html:form action="/train/resource/trainroom/trainroom">
	<%
	    int i = 0;
	%>
	<table border="0" cellpadding="0" cellspacing="0">
		<tr>
			<td height="35">
				<font size="4" style="font-weight: bold;">${facilityInfoForm.fieldName}申请明细:</font>
			</td>
		</tr>
		<tr>
			<td>
				<div class="fixedDiv2">
					<table width="100%" border="0" cellspacing="0" align="center" cellpadding="0">
						<thead>
							<tr class="fixedHeaderTr">
								<td align="center" class="TableRow noleft notop" style="width: 50px;" nowrap>
									&nbsp;<input type="checkbox" name="selall" onclick="batch_select(this,'sel');" title="全选" />&nbsp;
								</td>
								<logic:iterate id="element1" name="facilityInfoForm" property="itemList">
									<td align="center" class="TableRow noleft notop" nowrap>
										<bean:write name="element1" property="itemdesc" filter="false" />
									</td>
								</logic:iterate>
							</tr>
						</thead>
						<hrms:paginationdb id="element" name="facilityInfoForm" sql_str="facilityInfoForm.strsql" table=""
							where_str="facilityInfoForm.strwhere" columns="facilityInfoForm.columns" page_id="pagination"
							pagerows="${facilityInfoForm.pagerows}" order_by="facilityInfoForm.order_by">
							<bean:define id="fieldid" name="element" property="r1001"/>
							<bean:define id="dbname" name="element" property="nbase"/>
							<bean:define id="personid" name="element" property="a0100"/>
							<%
							String r1001 = SafeCode.encode(PubFunc.encrypt(fieldid.toString()));
							String nbase = SafeCode.encode(PubFunc.encrypt(dbname.toString()));
							String a0100 = SafeCode.encode(PubFunc.encrypt(personid.toString()));
							    if (i % 2 == 0) {
							%>
							<tr class="trShallow" onmouseover="javascript:tr_onclick(this,'');">
								<%
								    } else {
								%>

								<tr class="trDeep" onmouseover="javascript:tr_onclick(this,'');">
									<%
									    }
														i++;
									%><!-- 【6782】培训管理：su在查看培训场所申请信息的时候复选框没有对齐  jingq upd 2015.01.20 -->
									<td class="RecordRow noleft notop" width="50px;" align="center" nowrap>&nbsp;
										<logic:notEqual value="03" name="element" property="r6111">
											<logic:equal value="1" name="element" property="flag">
												<input type="checkbox" name="sel"
													value='<%=r1001 %>`<%=nbase %>`<%=a0100 %>`<bean:write name="element" property="r6101"/>`<bean:write name="element" property="r6103"/>' />
											</logic:equal>
										</logic:notEqual>&nbsp;
									</td>
									<logic:iterate id="element1" name="facilityInfoForm"
										property="itemList">
										<bean:define id="tmpitemid" name="element1" property="itemid" />
										<logic:equal name="element1" property="itemtype" value="D">
											<td class="RecordRow noleft notop" align="center" nowrap>
												&nbsp;
												<bean:write name="element" property="${tmpitemid }" />
												&nbsp;
											</td>
										</logic:equal>
										<logic:equal name="element1" property="itemtype" value="N">
											<td class="RecordRow noleft notop" align="right" nowrap>
												&nbsp;
												<bean:write name="element" property="${tmpitemid }" />
												&nbsp;
											</td>
										</logic:equal>
										<logic:equal name="element1" property="itemtype" value="A">
											<logic:notEqual name="element1" property="codesetid"
												value="0">
												<bean:define id="codesetid" name="element1"
													property="codesetid" />
												<logic:equal value="UN" name="codesetid">
													<td class="RecordRow noleft notop" nowrap>
														<hrms:codetoname codeid="UN" name="element"
															codevalue="${tmpitemid }" codeitem="codeitem"
															scope="page" />
														&nbsp;
														<bean:write name="codeitem" property="codename" />
													</td>
												</logic:equal>
												<logic:equal value="UM" name="codesetid">
													<td class="RecordRow noleft notop" nowrap>
														<hrms:codetoname codeid="UM" name="element"
															uplevel="${facilityInfoForm.uplevel }"
															codevalue="${tmpitemid }" codeitem="codeitem"
															scope="page" />
														&nbsp;
														<bean:write name="codeitem" property="codename" />
													</td>
												</logic:equal>
												<logic:notEqual value="UN" name="codesetid">
													<logic:notEqual value="UM" name="codesetid">
														<td class="RecordRow noleft notop" align="center" nowrap>
															<hrms:codetoname codeid="${codesetid }" name="element"
																codevalue="${tmpitemid }" codeitem="codeitem"
																scope="page" />
															&nbsp;
															<bean:write name="codeitem" property="codename" />
														</td>
													</logic:notEqual>
												</logic:notEqual>
											</logic:notEqual>
										</logic:equal>
										<logic:notEqual name="element1" property="itemtype" value="D">
											<logic:notEqual name="element1" property="itemtype" value="N">
												<logic:equal name="element1" property="codesetid" value="0">
												<logic:notEqual name="element1" property="itemid" value="r6105">
													<td class="RecordRow noleft notop" nowrap>
														&nbsp;
														<bean:write name="element" property="${tmpitemid }" />
														&nbsp;
													</td>
												</logic:notEqual>
												<logic:equal name="element1" property="itemid" value="r6105">
													<td class="RecordRow noleft notop" title="<bean:write name="element" property="${tmpitemid }" />" nowrap>
														<div STYLE="width: 200px; overflow:hidden;white-space: nowrap;text-overflow:ellipsis">
														<span>&nbsp;
														<bean:write name="element" property="${tmpitemid }" />
														&nbsp;</span>
														</div>
													</td>
												</logic:equal>
												</logic:equal>
											</logic:notEqual>
										</logic:notEqual>
									</logic:iterate>
								</tr>
						</hrms:paginationdb>
					</table>
				</div>
			</td>
		</tr>
		<tr>
			<td>
				<table width="100%" class="RecordRowP" align="center">
					<tr>
						<td valign="bottom" class="tdFontcolor">
							<hrms:paginationtag name="facilityInfoForm"
								pagerows="${facilityInfoForm.pagerows}" property="pagination"
								scope="page" refresh="true"></hrms:paginationtag>
						</td>
						<td align="right" nowrap class="tdFontcolor">
							<hrms:paginationdblink name="facilityInfoForm"
								property="pagination" nameId="facilityInfoForm" scope="page">
							</hrms:paginationdblink>
						</td>
					</tr>
				</table>
			</td>
		</tr>
		<tr>
			<td style="padding-top: 5px;" align="left">
				<hrms:priv func_id="323030401" module_id="">
					<input type="button" class="mybutton" value="批准"
						onclick="edit('app')" />
				</hrms:priv>
				<hrms:priv func_id="323030402" module_id="">
					<input type="button" class="mybutton" value="驳回"
						onclick=
	edit('ovr');
/>
				</hrms:priv>
				<input type="button" class="mybutton"
					value="<bean:message key='button.return'/>" onclick=
	returnstr();;
>
				&nbsp;
			</td>
		</tr>
	</table>
</html:form>
<script>
	function returnstr() {
		facilityInfoForm.action = "/train/resource/trainroom/selftrainroom.do?b_query=return";
		facilityInfoForm.submit();
	}
	function edit(state) {
		var str = "";
		for ( var i = 0; i < document.facilityInfoForm.elements.length; i++) {
			if (document.facilityInfoForm.elements[i].type == "checkbox"
					&& document.facilityInfoForm.elements[i].name != 'selall') {
				if (document.facilityInfoForm.elements[i].checked == true) {
					str += "," + document.facilityInfoForm.elements[i].value;
				}
			}
		}
		if (str.length == 0) {
			alert("请选择要操作的记录！");
			return;
		}

		var return_vo = idea();
		if (!return_vo)
			return;
		var hashvo = new ParameterSet();
		hashvo.setValue("state", state);
		hashvo.setValue("str", str.substring(1));
		hashvo.setValue("declare", return_vo);
		var request = new Request( {
			method : 'post',
			asynchronous : false,
			onSuccess : search,
			functionId : '2020030115'
		}, hashvo);
	}

	function idea() {
		var thecodeurl = "/train/resource/trainroom/trainroomidea.jsp";
		var return_vo = window
				.showModalDialog(
						thecodeurl,
						"",
						"dialogWidth:400px; dialogHeight:310px;resizable:no;center:yes;scroll:no;status:no");
		return return_vo;
	}
	function search(outparamters) {
		var msg = outparamters.getValue("msg");
		if (msg != null && "" != msg) {
			alert(msg);
		} else {
			var fieldName = "${facilityInfoForm.fieldName }";
			fieldName = $URL.encode(getEncodeStr(fieldName));
			facilityInfoForm.action = "/train/resource/trainroom/trainroom.do?b_query=link&&fieldName="
					+ fieldName;
			facilityInfoForm.submit();
		}
	}
</script>