<%@ page contentType="text/html; charset=UTF-8"%>
<%@page import="com.hrms.frame.codec.SafeCode"%>
<%@page import="com.hjsj.hrms.utils.PubFunc"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean"%>
<%@ taglib uri="/tags/struts-html" prefix="html"%>
<%@ taglib uri="/tags/struts-logic" prefix="logic"%>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms"%>
<script language="JavaScript" src="/js/meizzDate.js"></script>
<script language="JavaScript" src="/js/popcalendar.js"></script>
<script language="javascript" src="/ajax/format.js"></script>
<link rel="stylesheet" href="/css/css1.css" type="text/css">
<style>
<!--
.divTable {
	border: 1px solid #C4D8EE;
	overflow-y: scroll;
	height: 250px;
}

.m_frameborder {
	border-left: 1px inset;
	border-top: 1px inset;
	border-right: 1px inset;
	border-bottom: 1px inset;
	width: 36px;
	height: 18px;
	background-color: transparent;
	overflow: hidden;
	text-align: right;
	font-family: "Tahoma";
	font-size: 6px;
}

.m_arrow {
	float: left;
	margin-top: 1px;
	padding-top: 1px;
	width: 16px;
	height: 9px;
	font-family: "Webdings";
	font-size: 7px;
	line-height: 2px;
	padding-left: 2px;
	cursor: default;
}

.m_input {
	width: 13px;
	height: 15px;
	border: 0px solid black;
	font-family: "Tahoma";
	font-size: 9px;
	text-align: right;
}
-->
</style>
<script type="text/javascript">
<!--
function reOk(outparamters){
	var isOk=outparamters.getValue("isOk");
	if(isOk == "1"){
		alert("补签成功！");
		window.returnValue = 1
		//window.close();
	}
}
function setFocusObj(obj,time_vv) {
	if(time_vv=='24'){
		if(obj.value>=24||obj.value==""){
			alert("请输入0到23！");
			obj.value='00';
			return false;
		}
	}
	if(time_vv=='60'){
		if(obj.value>=60||obj.value==""){
			alert("请输入0到59!");
			obj.value = '00';
			return false;
		}
	}
	this.fObj = obj;
	time_r=time_vv;		
}

function checkValid(obj,time_vv){
	if(time_vv=='24'){
		if(obj.value>=24){
			alert("时钟只能在0到23");
			obj.value='00';
			return false;
		}
	}
	if(time_vv=='60'){
		if(obj.value>=60){
			alert("分钟数只能在0到59!");
			obj.value = '00';
			return false;
		}
	}
}


function IsInputTimeValue() {	     
	event.cancelBubble = true;
	var fObj=this.fObj;		
	if (!fObj) return;		
	var cmd = event.srcElement.innerText=="5"?true:false;
	if(fObj.value==""||fObj.value.lenght<=0)
		fObj.value="0";
	var i = parseInt(fObj.value,10);		
	var radix=parseInt(time_r,10)-1;				
	if (i==radix&&cmd) {
		i = 0;
	} else if (i==0&&!cmd) {
		i = radix;
	} else {
		cmd?i++:i--;
	}	
	if(i==0){
		fObj.value = "00"
	}else if(i<10&&i>0){
		fObj.value="0"+i;
	}else{
		fObj.value = i;
	}			
	fObj.select();
}
function selAll(){
	var flag = document.getElementById("all").checked;
	var len=document.trainAtteForm.elements.length;
	for (var i = 0;i < len;i++){
		if (document.trainAtteForm.elements[i].type == "checkbox"){
			document.trainAtteForm.elements[i].checked = flag;
		}
	}
}
function IsDigit() { 
	return ((event.keyCode >= 46) && (event.keyCode <= 57)); 
} 
function validate(){
	if(!isDate(document.trainAtteForm.nowDate.value,"yyyy-MM-dd")){
		alert("签到时间格式不正确,请输入正确的时间格式！\nyyyy-MM-dd");
        return false;
	}
	var tag = false;
	var selectBox =  document.trainAtteForm.elements;
	var len=selectBox.length;
	for (var i = 0;i < len;i++){
		if (selectBox[i].type == "checkbox"){
			if(selectBox[i].checked == true && selectBox[i].id != "all"){
				tag = true;
			}
		}
	}
	if(tag != true){
		alert("请选择刷卡人员！");
		return false;
	}
	var retReason = document.trainAtteForm.retReason.value;
	if(retReason == null ||　retReason == ""){
		alert("补刷原因未填写！");
		return false;
	}
	return true;
}

function save(){
	if(validate()){
		var usrid = document.getElementsByName("usrid");
		var usrArray = new Array();
		for(var i = 0;i < usrid.length;i++){
			if(usrid[i].checked == true){
				usrArray.push(usrid[i].value);
			}
		}
		var hashvo = new ParameterSet();
		var nowDate =    document.trainAtteForm.nowDate.value;
		var regFlag =    document.getElementsByName("regFlag");// 1：签到2：签退标记
		var regFlagValue = 0;
		for(var j = 0;j < regFlag.length;j++){
			if(regFlag[j].checked == true){
				regFlagValue = regFlag[j].value;
			}
		}
		var courseplan = document.trainAtteForm.courseplan.value;// 培训课程编号
		var nowHours =   document.getElementById("nowHours").value;
		var nowMinutes = document.getElementById("nowMinutes").value;
		var retReason =  document.trainAtteForm.retReason.value;
		hashvo.setValue("nowDate",nowDate);
		hashvo.setValue("regFlag",regFlagValue);
		hashvo.setValue("courseplan",courseplan);
		hashvo.setValue("nowHours",nowHours);
		hashvo.setValue("nowMinutes",nowMinutes);
		hashvo.setValue("retReason",getEncodeStr(retReason));
		hashvo.setValue("usrid",usrArray);
		//trainAtteForm.action="/train/attendance/pageregistration.do?b_addRet=link";
		//trainAtteForm.submit();
		var request=new Request({method:'post',onSuccess:reOk,functionId:'2020020244'},hashvo);
	}
}
function IsDigit1(){
	return ((event.keyCode != 39) && (event.keyCode != 34)); 
}
function wclose(){
	if(window.returnValue != 1){
		window.returnValue = 0;
	}
	window.close();
}
//-->
</script>
<hrms:themes />
<html:form action="/train/attendance/pageregistration">
	<html:hidden name="trainAtteForm" property="courseplan" />
	<html:hidden name="trainAtteForm" property="classplan" />
	<table width="490" align="center" cellpadding="0" cellspacing="0"
		border="0">
		<tr>
			<td align="left" class="TableRow">
				补刷
			</td>
		</tr>
		<tr>
			<td>
				<div class="divTable common_border_color" style="border-top: 0px;">
					<table width="100%" cellpadding="0" cellspacing="0">
						<tr>
							<th class="TableRow noleft" width="35px" style="border-top: none;" nowrap="nowrap" align="center">
								序号
							</th>
							<th class="TableRow noleft" width="210px" style="border-top: none;" nowrap="nowrap" align="center">
								学员所在部门
							</th>
							<th class="TableRow noleft" width="55px" style="border-top: none;" nowrap="nowrap" align="center">
								<!-- 姓名：-->
								<bean:message key="hire.employActualize.name"></bean:message>
							</th>
							<th class="TableRow noleft" width="120px" style="border-top: none;" nowrap="nowrap" align="center">
								卡号
							</th>
							<th class="TableRow noleft" width="35px" style="border-top: none;" nowrap="nowrap" align="center">
								<input type="checkbox" id="all" value="true" onclick="selAll()" />
							</th>
						</tr>
						<%
							int i = 1;
						%>
						<hrms:paginationdb id="element"
							pagerows="${trainAtteForm.pagerows}" name="trainAtteForm"
							sql_str="trainAtteForm.sql_str" table=""
							where_str="trainAtteForm.cond_str" order_by=""
							columns="trainAtteForm.columns" page_id="pagination"
							indexes="indexes">
							<tr>
								<td class="RecordRow noleft" width="35px" style="border-top: none;" align="center">
									<%=i%>
									<%
										i++;
									%>
								</td>
								<td class="RecordRow noleft" width="210px" style="border-top: none;" align="left">
									
									<hrms:codetoname codeid="UM" name="element" codevalue="e0122"
										codeitem="e0122" scope="page"
										uplevel="${trainAtteForm.uplevel}" />
									<bean:write name="e0122" property="codename" />
									
								</td>
								<td class="RecordRow noleft" width="55px" style="border-top: none;" align="left">
									
									<bean:write name="element" property="a0101" filter="true" />
									
								</td>
								<td class="RecordRow noleft" width="120px" style="border-top: none;" align="left">
									
									<bean:write name="element" property="${trainAtteForm.card_no}"
										filter="true" />
									
								</td>
								<td class="RecordRow noleft" width="35px" style="border-top: none;" align="center">
								<bean:define id="dbase" name="element" property="nbase"/>
								<bean:define id="a0100s" name="element" property="a0100"/>
								<%
								String nbase = SafeCode.encode(PubFunc.encrypt(dbase.toString()));
								String a0100 = SafeCode.encode(PubFunc.encrypt(a0100s.toString()));
								%>
									<input type="checkbox" name="usrid" value="<%=nbase %>`<%=a0100 %>">
								</td>
							</tr>
						</hrms:paginationdb>
						<tr>
							<td colspan="5">
								<table border="0" cellspacing="0" align="center"
									class="ListTableF" style="border-top: none;" cellpadding="0"
									width="100%">
									<tr height="30px">
										<td width="60%" valign="bottom" align="left" height="25"
											nowrap>
											&nbsp;
											<hrms:paginationtag name="trainAtteForm"
												pagerows="${trainAtteForm.pagerows}" property="pagination"
												scope="page" refresh="true"></hrms:paginationtag>
										</td>
										<td width="40%" valign="bottom" align="right" nowrap>
											<hrms:paginationdblink name="trainAtteForm"
												property="pagination" nameId="trainAtteForm" scope="page">
											</hrms:paginationdblink>
											&nbsp;
										</td>
									</tr>
								</table>
							</td>
						</tr>

					</table>
				</div>
			</td>
		</tr>
		<tr>
			<td>
				<br />
			</td>
		</tr>
		<tr>
			<td>
				<div class="RecordRow" style=" width: 100%">
					<table width="100%">
						<tr>
							<td align="right">
							<bean:message key="train.b_plan.reg.type"/>：
							</td>
							<td>
								<html:radio name="trainAtteForm" property="regFlag" value="3"><!-- 补签到 --><bean:message key="train.b_plan.ret.on"/></html:radio>
								<html:radio name="trainAtteForm" property="regFlag" value="4"><!-- 补签退 --><bean:message key="train.b_plan.ret.off"/></html:radio>
							</td>
						</tr>
						<tr>
							<td align="right">
								<!-- 补刷时间 --><bean:message key="train.b_plan.ret.time"/>：
							</td>
							<td align="left" nowrap="nowrap">
								<table>
									<tr>
										<td>
											<input type="text" name="nowDate" class="textColorWrite" size="12" value="${trainAtteForm.nowDate}" extra="editor" style="width:100px;font-size:10pt;text-align:left" id="nowDate" dropDown="dropDownDate">
										</td>
										<td>
											<table border="0" cellspacing="0" cellpadding="0">
												<tr>
													<td width="40" nowrap style="background-color: #FFFFFF";>
														<div class="m_frameborder common_border_color">
															<input type="text" class="m_input" maxlength="2"
																name="nowHours" id="nowHours"
																value="${trainAtteForm.nowHours}"
																onfocus="setFocusObj(this,24);"
																onblur="checkValid(this,24);"
																onkeypress="event.returnValue=IsDigit();">
															<font color="#000000"><strong>:</strong> </font>
															<input type="text" class="m_input" maxlength="2"
																name="nowMinutes" id="nowHours"
																value="${trainAtteForm.nowMinutes}"
																onfocus="setFocusObj(this,60);"
																onblur="checkValid(this,60);"
																onkeypress="event.returnValue=IsDigit();">
														</div>
													</td>
													<td>
														<table border="0" cellspacing="2" cellpadding="0">
															<tr>
																<td>
																	<button id="0_up" class="m_arrow"
																		onmouseup="IsInputTimeValue();">
																		5
																	</button>
																</td>
															</tr>
															<tr>
																<td>
																	<button id="0_down" class="m_arrow"
																		onmouseup="IsInputTimeValue();">
																		6
																	</button>
																</td>
															</tr>
														</table>
													</td>
												</tr>
											</table>
										</td>
									</tr>
								</table>
							</td>
						</tr>
						<tr>
							<td align="right">
								<!-- 补刷原因 --><bean:message key="train.b_plan.ret.reason"/>：
							</td>
							<td align="left" valign="middle" nowrap="nowrap">
								<html:textarea name="trainAtteForm" property="retReason"
									cols="35" rows="3" onkeypress="event.returnValue=IsDigit1();"></html:textarea>
							</td>
						</tr>
					</table>
				</div>
			</td>
		</tr>
		<tr>
			<td width="93%" style="padding-top: 5px;">
				<center>
					<input type="button" name="b_addRet" value='确定' onclick="save()" class="mybutton" />
					<input type="button" name="b_cls" value='关闭' class="mybutton"
						onclick="wclose();" />
				</center>
			</td>
		</tr>
	</table>
</html:form>
