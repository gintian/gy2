<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean"%>
<%@ taglib uri="/tags/struts-html" prefix="html"%>
<%@ taglib uri="/tags/struts-logic" prefix="logic"%>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms"%>
<link rel="stylesheet" type="text/css" href="/ajax/skin.css">
<script language="javascript" src="/ajax/constant.js"></script>
<script language="javascript" src="/ajax/basic.js"></script>
<script language="javascript" src="/ajax/common.js"></script>
<script language="javascript" src="/ajax/control.js"></script>
<script language="javascript" src="/ajax/dataset.js"></script>
<script language="javascript" src="/ajax/editor.js"></script>
<script language="javascript" src="/ajax/dropdown.js"></script>
<script language="javascript" src="/ajax/format.js"></script>
<!-- popcalendar.js xuj add 2013-7-29 解决extra="editor" dropDown="dropDownDate"使用统一日期控件 -->
<script type="text/javascript" src="/js/popcalendar.js"></script>
<script language="JavaScript" src="/kq/kq.js"></script>
<script language="javascript">
	function countdata() {
		var count_type;
		for ( var i = 0; i < document.dailyRegisterForm.count_type.length; i++) {
			if (document.dailyRegisterForm.count_type[i].checked) {
				count_type = document.dailyRegisterForm.count_type[i].value;
			}
		}

		if (count_type == 1 || count_type == 0 || count_type == 2) {
			if (count_type == 2) {
				var count_start = document.dailyRegisterForm.count_start.value;
				var count_end = document.dailyRegisterForm.count_end.value;
				if (count_start == "") {
					alert("请选择计算开始时间！");
					return;
				} else if (count_end == "") {
					alert("请选择计算结束时间！");
					return;
				} else {
					//dailyRegisterForm.action="/kq/register/count_register.do?br_count=link";
					//dailyRegisterForm.target="mil_body";
					//dailyRegisterForm.submit();
					//统计的开始时间
					if(!isDate(count_start,"yyyy-MM-dd")){
						alert("请输入正确的开始日期yyyy-MM-dd！");
						return;
					}
					if(!isDate(count_end,"yyyy-MM-dd")){
						alert("请输入正确的结束日期yyyy-MM-dd！");
						return;
					}
					
					var arr = count_start.split("-");
					var starttime = new Date(arr[0], parseInt(arr[1] - 1), arr[2]);
					var starttimes = starttime.getTime();
					//统计的结束时间
					var arrs = count_end.split("-");
					var endtime = new Date(arrs[0], parseInt(arrs[1] - 1), arrs[2]);
					var endtimes = endtime.getTime();
					if (starttimes > endtimes) {
						alert(KQ_DAILY_COUNT_NOTMORNTHAN_ENDTIME);
						return;
					}
					var thevo = new Object();
					thevo.count_start = count_start;
					thevo.count_end = count_end;
					thevo.count_type = count_type;
					window.returnValue = thevo;
					window.close();
				}
			} else {
				//alert("123");
				//dailyRegisterForm.action="/kq/register/count_register.do?br_count=link";
				// dailyRegisterForm.target="mil_body";
				//dailyRegisterForm.submit();
				//window.close();
				var thevo = new Object();
				thevo.count_start = "";
				thevo.count_end = "";
				thevo.count_type = count_type;
				window.returnValue = thevo;
				window.close();
			}
		} else {
			alert("请选择计算方式！");
			return;
		}

	}
</script>
<script language="javascript">
	var _checkBrowser = true;
	var _disableSystemContextMenu = false;
	var _processEnterAsTab = true;
	var _showDialogOnLoadingData = true;
	var _enableClientDebug = true;
	var _theme_root = "/ajax/images";
	var _application_root = "";
	var __viewInstanceId = "968";
	var ViewProperties = new ParameterSet();
</script>
<html:form action="/kq/register/count_register">
	<div class="fixedDiv3">
	<table width="100%" border="0" cellspacing="0" class="DetailTable"
		cellpadding="0" align="center">
		<tr height="20">
			<td align=center class="TableRow">
				<bean:message key="kq.countdate.width" />
			</td>
		</tr>
		<tr>
			<td width="100%" valign="middle" class="framestyle9">
				<br>
				<table width="100%" border="0" cellspacing="0" class="DetailTable"
					cellpadding="0">
					<tr>
						<td width="100%" valign="middle" colspan="4">
							<table width="100%" border="0" cellspacing="0"
								class="DetailTable" cellpadding="0">
								<tr>
									<td width="100%" height="50">
										<table>
											<tr>
												<td>
													&nbsp;
													<html:radio name="dailyRegisterForm" property="count_type"
														value="2" />
													&nbsp;
													<bean:message key="kq.datewidth" />
													&nbsp;
												</td>
												<td>
													<input type="text" name="dailyRegisterForm" size="12"
														value="${dailyRegisterForm.count_start}"
														 class="inputtext" style="width: 100px; font-size: 10pt; text-align: left"
														id="count_start"
														onclick="getKqCalendarVar();popUpCalendar(this, this, weeks, feasts, turn_dates, week_dates, false);">
													~
													<input type="text" name="dailyRegisterForm" size="12"
														value="${dailyRegisterForm.count_end}"
														 class="inputtext" style="width: 100px; font-size: 10pt; text-align: left"
														id="count_end"
														onclick="getKqCalendarVar();popUpCalendar(this, this, weeks, feasts, turn_dates, week_dates, false);">
												</td>
												<tr>
										</table>
									</td>
								</tr>
								<tr>
									<td width="100%" height="50">
										<table>
											<tr>
												<td>
													&nbsp;
													<html:radio name="dailyRegisterForm" property="count_type"
														value="0" />
													&nbsp;
													<bean:message key="kq.curdate" />
													&nbsp;
												</td>
												<td>
												</td>
												<tr>
										</table>
									</td>
								</tr>

								<tr>
									<td width="100%" height="50">
										<table>
											<tr>
												<td>
													&nbsp;
													<html:radio name="dailyRegisterForm" property="count_type"
														value="1" />
													&nbsp;
													<bean:message key="kq.session" />
													&nbsp;
													<html:hidden name="dailyRegisterForm"
														property="count_duration" styleClass="text" />
													<html:hidden name="dailyRegisterForm"
														property="kq_duration" styleClass="text" />
												</td>
												<td>

												</td>
												<tr>
										</table>

									</td>
								</tr>
							</table>
						</td>
					</tr>
				</table>
			</td>
		</tr>
	</table>
	<table align="center">
		<tr>
			<td height="40" align="center">
				<input type="button" name="btnreturn"
					value='<bean:message key="button.ok"/>'
					onclick="countdata();" class="mybutton">
				<input type="button" name="btnreturn"
					value='<bean:message key="kq.register.kqduration.cancel"/>'
					onclick="window.close();" class="mybutton">
			</td>
		</tr>
	</table>
	</div>
</html:form>
<script language="javascript">
	var dropDownDate = createDropDown("dropDownDate");
	var __t = dropDownDate;
	__t.type = "date";
	__t.tag = "";
	_array_dropdown[_array_dropdown.length] = __t;
	initDropDown(__t);
</script>
<script language="javascript">
	initDocument();
</script>