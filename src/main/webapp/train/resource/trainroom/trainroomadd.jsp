<%@ page contentType="text/html; charset=UTF-8" language="java"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean"%>
<%@ taglib uri="/tags/struts-html" prefix="html"%>
<%@ taglib uri="/tags/struts-logic" prefix="logic"%>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms"%>
<script language="JavaScript" src="/js/validateDate.js"></script>
<style>
<!--
.m_frameborder {
	border-left: 1px inset #D4D0C8;
	border-top: 1px inset #D4D0C8;
	border-right: 1px inset #D4D0C8;
	border-bottom: 1px inset #D4D0C8;
	width: 40px;
	height: 20px;
	background-color: transparent;
	overflow: hidden;
	text-align: right;
	font-family: "Tahoma";
	font-size: 6px;
}

.m_arrow {
	width: 16px;
	height: 8px;
	font-family: "Webdings";
	font-size: 7px;
	line-height: 0px;
	padding-left: 2px;
	cursor: default;
}

.m_input {
	width: 15px;
	height: 20px;
	border: 0px solid black;
	font-family: "Tahoma";
	font-size: 9px;
	text-align: right;
}
-->
</style>
<script language="javascript">
	function save() {
		var hashvo=new ParameterSet();
		hashvo.setValue("state","save");
		hashvo.setValue("fieldId","${facilityInfoForm.fieldId}");
		hashvo.setValue("strdate","${facilityInfoForm.strdate}");
		hashvo.setValue("begin_time",$("begin_time_h").value+":"+$("begin_time_m").value);
		hashvo.setValue("end_time",$("end_time_h").value+":"+$("end_time_m").value);
		hashvo.setValue("declare",getEncodeStr($("declare").value));
		var request=new Request({method:'post',asynchronous:false,onSuccess:closewin,functionId:'2020030113'},hashvo);
	}
	function closewin(outparamters)
	{
		var flag=outparamters.getValue("flag");		
		if("ok"==flag){
			returnValue=outparamters.getValue("value");
			window.close();
		}else{
			alert(flag);
		}
	}
</script>
<html:form action="/train/resource/trainroom/selftrainroom">
<div class="fixedDiv3 ">
	<table width="100%" border="0" cellspacing="0" align="center" cellpadding="0" class="ListTableF">
		<tr>
			<td colspan="2" class="TableRow" align="center">
				培训场所使用申请
			</td>
		</tr>
		<tr>
			<td align="right" class="RecordRow">
				培训场所：
			</td>
			<td class="RecordRow">
				<bean:write name="facilityInfoForm" property="fieldName" />
			</td>
		</tr>
		<tr>
			<td align="right" class="RecordRow">
				申请日期：
			</td>
			<td class="RecordRow">
				<bean:write name="facilityInfoForm" property="strdate" />
			</td>
		</tr>
	  <tr>
		<td align="right" class="RecordRow">
			开始时间：
		</td>
		<td class="RecordRow">
			<table border="0" cellspacing="0" align="left" valign="bottom" cellpadding="0">
				<tr>
					<td width="30" nowrap style="background-color: #FFFFFF";>
						<div class="m_frameborder">
							<input type="text" class="m_input" maxlength="2" name="begin_time_h" 
								onfocus="setFocusObjStart(this,24);"
								onkeypress="event.returnValue=IsDigit();" value="00"/>
							<span
								style="color: #000000; border: 0px; vertical-align: middle; height: 22px"><strong>:</strong>
							</span>
							<input type="text" class="m_input" maxlength="2" name="begin_time_m" 
								onfocus="setFocusObjStart(this,60);"
								onkeypress="event.returnValue=IsDigit();" value="00" />
						</div>
					</td>
					<td>
						<div style="float: inherit; height: 20px; width: 18px;">
							<button id="0_up" class="m_arrow"
								onmouseup="IsInputTimeValueStart();"
								style="float: left; margin-top: 1px; padding-top: 1px;">
								5
							</button>

							<button id="0_down" class="m_arrow"
								onmouseup="IsInputTimeValueStart();"
								style="float: left; margin-top: 1px; padding-top: 1px;">
								6
							</button>
						</div>
					</td>
				</tr>
			</table>
		</td>
	  </tr>
	   <tr>
		<td align="right" class="RecordRow">
			结束时间：
		</td>
		<td class="RecordRow">
			<table border="0" cellspacing="0" align="left" valign="bottom" cellpadding="0">
				<tr>
					<td width="30" nowrap style="background-color: #FFFFFF";>
						<div class="m_frameborder">
							<input type="text" class="m_input" maxlength="2" name="end_time_h" 
								onfocus="setFocusObjEnd(this,24);"
								onkeypress="event.returnValue=IsDigit();" value="00"/>
							<span
								style="color: #000000; border: 0px; vertical-align: middle; height: 22px"><strong>:</strong>
							</span>
							<input type="text" class="m_input" maxlength="2" name="end_time_m" 
								onfocus="setFocusObjEnd(this,60);"
								onkeypress="event.returnValue=IsDigit();" value="00" />
						</div>
					</td>
					<td>
						<div style="float: inherit; height: 20px; width: 18px;">
							<button id="0_up" class="m_arrow"
								onmouseup="IsInputTimeValueEnd();"
								style="float: left; margin-top: 1px; padding-top: 1px;">
								5
							</button>

							<button id="0_down" class="m_arrow"
								onmouseup="IsInputTimeValueEnd();"
								style="float: left; margin-top: 1px; padding-top: 1px;">
								6
							</button>
						</div>
					</td>
				</tr>
			</table>
		</td>
	  </tr>
		<tr>
			<td align="right" valign="top" class="RecordRow" style="padding-top: 5px;">
				事&nbsp;&nbsp;由：
			</td>
			<td class="RecordRow">
				<textarea rows="3" cols="30" name="declare" id="declare" style="width: 100%;height: 80px;" class="RecordRow"></textarea>
			</td>
		</tr>
	</table>
	<table align="center" style="margin-top: 5px;">
		<tr>
			<td align="center">
				<input type="button" value="报批" class="mybutton" onclick="save();"/>
				<input type="button" value="关闭" class="mybutton" onclick="javascript:window.close();"/>
			</td>
		</tr>
	</table>
	</div>
</html:form>
<script>
	function setFocusObjEnd(obj,time_vv) 
	{		
		this.fObjEnd = obj;
		time_rEnd=time_vv;
	}
	function setFocusObjStart(obj,time_vv) 
	{		
		this.fObjStart = obj;
		time_rStart=time_vv;
	}
	function IsDigit() 
	{ 
	   return ((event.keyCode >= 47) && (event.keyCode <= 57)); 
	}
	function IsInputTimeValueEnd() 
	{	     
	   	event.cancelBubble = true;
	   	var fObjEnd=this.fObjEnd;		
	   	if (!fObjEnd) return;		
	   	var cmd = event.srcElement.innerText=="5"?true:false;
	   	if(fObjEnd.value==""||fObjEnd.value.lenght<=0)
		fObjEnd.value="0";
	   	var i = parseInt(fObjEnd.value,10);		
	   	var radix=parseInt(time_rEnd,10)-1;				
	   	if (i==radix&&cmd) {
	       	i = 0;
	   	} else if (i==0&&!cmd) {
			i = radix;
	   	} else {
			cmd?i++:i--;
	   	}	
	   	if(i==0)
	   	{
		fObjEnd.value = "00"
	   	}else if(i<10&&i>0)
	   	{
		fObjEnd.value="0"+i;
	   	}else{
		fObjEnd.value = i;
	   	}			
	   	fObjEnd.select();
	} 
	function IsInputTimeValueStart() 
	{	     
	   	event.cancelBubble = true;
	   	var fObjStart=this.fObjStart;		
	   	if (!fObjStart) return;		
	   	var cmd = event.srcElement.innerText=="5"?true:false;
	   	if(fObjStart.value==""||fObjStart.value.lenght<=0)
	   		fObjStart.value="0";
	   	var i = parseInt(fObjStart.value,10);		
	   	var radix=parseInt(time_rStart,10)-1;				
	   	if (i==radix&&cmd) {
	       	i = 0;
	   	} else if (i==0&&!cmd) {
			i = radix;
	   	} else {
			cmd?i++:i--;
	   	}	
	   	if(i==0)
	   	{
	   		fObjStart.value = "00"
	   	}else if(i<10&&i>0)
	   	{
	   		fObjStart.value="0"+i;
	   	}else{
	   		fObjStart.value = i;
	   	}			
	   	fObjStart.select();
	} 
	
	var b_time="${facilityInfoForm.startdate}";
	if(b_time!=null&&b_time.length==5){
		document.getElementById("begin_time_h").value=b_time.substring(0,2);
		document.getElementById("begin_time_m").value=b_time.substring(3);
	}
	var e_time="${facilityInfoForm.enddate}";
	if(e_time!=null&&e_time.length==5){
		document.getElementById("end_time_h").value=e_time.substring(0,2);
		document.getElementById("end_time_m").value=e_time.substring(3);
	}
</script>