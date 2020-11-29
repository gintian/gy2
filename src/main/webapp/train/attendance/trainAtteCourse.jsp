<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean"%>
<%@ taglib uri="/tags/struts-html" prefix="html"%>
<%@ taglib uri="/tags/struts-logic" prefix="logic"%>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms"%>
<script language="JavaScript" src="/js/function.js"></script>
<style type="text/css">
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
</style>
<script type="text/javascript">
<!--
	function checkTime(times){
	 	var result=times.match(/^(\d{1,4})(-|\/)(\d{1,2})\2(\d{1,2})$/);
	 	if(result==null) return false;
	 	var d= new Date(result[1], result[3]-1, result[4]);
	 	return (d.getFullYear()==result[1]&&(d.getMonth()+1)==result[3]&&d.getDate());
	}
	function timeCheck(obj){
	if(!checkTime(obj.value)){
		obj.value='';
	}
	}
 function save(){
 		var start_date=document.getElementById("start_date").value;
 		if(start_date==null||start_date.length<10){
 			alert("请选择起始日期！");
 			return;
 		}
 		var minute = document.getElementById("minute");
 		if(!checkNUM1(minute,"每课时分钟数"))
 			return;

 		if(minute.value<1){
 			alert("请录入每课时分钟数！");
 			return;
 		}

		var begin_time_h = document.getElementById("begin_time_h").value;
		if(begin_time_h.length==0 || begin_time_h>23){
			alert("上课时间小时格式输入错误!");
			return;
		}
		var begin_time_m = document.getElementById("begin_time_m").value;
		if(begin_time_m.length==0 || begin_time_m>59){
            alert("上课时间分钟格式输入错误!");
            return;
        }
		var end_time_h = document.getElementById("end_time_h").value;
		if(end_time_h.length==0 || end_time_h>23){
            alert("下课时间小时格式输入错误!");
            return;
        }
		var end_time_m = document.getElementById("end_time_m").value;
		if(end_time_m.length==0 || end_time_m>59){
            alert("下课时间分钟格式输入错误!");
            return;
        }
 		var bc="0",ec="0",hd="0",jj="0";
 		var begin_card=document.getElementById("begin_card");
 		if(begin_card.checked)
			bc="1";
		var end_card=document.getElementById("end_card");
		if(end_card.checked)
			ec="1";
		if("${trainAtteForm.id}"=="0"){
			var holiday=document.getElementById("holiday");
			if(holiday.checked)
				hd="1";
			var feast=document.getElementById("feast");
			if(feast.checked)
				jj="1";
		}
		var begin_time = begin_time_h +":"+ begin_time_m;
		var end_time = end_time_h +":"+ end_time_m;
 		var hashvo=new ParameterSet(); 
		hashvo.setValue("id","${trainAtteForm.id}");
		hashvo.setValue("r4101","${trainAtteForm.r4101}");
		hashvo.setValue("start_date",start_date); 
		hashvo.setValue("stop_date",document.getElementById("stop_date").value);
		hashvo.setValue("begin_time",begin_time);
		hashvo.setValue("end_time",end_time);
		hashvo.setValue("minute",minute.value);
		hashvo.setValue("begin_card",bc);
		hashvo.setValue("end_card",ec);
		hashvo.setValue("holiday",hd);
		hashvo.setValue("feast",jj);
    	var request=new Request({method:'post',onSuccess:showOk,functionId:'2020020236'},hashvo);
 }
 function showOk(outparamters){
 	if(outparamters.getValue("mess")=="success"){
 		returnValue=outparamters.getValue("mess");
 		window.close();
 	}else
 		alert(outparamters.getValue("mess"));
 }
//-->
</script>
<div class="fixedDiv3">
<html:form action="/train/attendance/trainAtteCourse.do?b_edit=link">
<table width="100%" align="center" border="0" cellpadding="0" cellspacing="0" style="margin-bottom: 10px;" class="ListTableF">
	<tr>
		<td class="TableRow" align="center">排课</td>
	</tr>
	<tr>
		<td class="RecordRow">
			<table width="93%" align="center" border="0" style="padding: 4px;" cellpadding="0" cellspacing="0">
				<tr>
					<td width="30%" align="right">起始日期:</td>
					<td>
						<logic:equal value="0" name="trainAtteForm" property="id">
							<input type="text" name="start_date" class="textColorWrite" extra="editor" value="${trainAtteForm.start_date}" onblur="timeCheck(this);" style="width:130px;font-size:10pt;text-align:left" dropDown="dropDownDate">
						</logic:equal>
						<logic:notEqual value="0" name="trainAtteForm" property="id">
							<input type="text" name="start_date" class="textColorWrite"  value="${trainAtteForm.start_date}" style="width:130px;font-size:10pt;text-align:left" disabled>
						</logic:notEqual>
					</td>
				</tr>
				<tr>
					<td align="right">终止日期:</td>
					<td>
						<logic:equal value="0" name="trainAtteForm" property="id">
							<input type="text" name="stop_date" class="textColorWrite"  extra="editor" value="${trainAtteForm.stop_date}" onblur="timeCheck(this);" style="width:130px;font-size:10pt;text-align:left" dropDown="dropDownDate">
						</logic:equal>
						<logic:notEqual value="0" name="trainAtteForm" property="id">
							<html:text name="trainAtteForm" styleClass="textColorWrite"  property="stop_date" disabled="true" style="width:130px;font-size:10pt;text-align:left"/>
						</logic:notEqual>
					</td>
				</tr>
				<tr>
					<td align="right">上课时间:</td>
					<td>
						<table border="0" cellspacing="0" align="left" valign="bottom" cellpadding="0">
							<tr>
								<td width="40" nowrap style="background-color: #FFFFFF;">
									<div class="m_frameborder">
										<input type="text" class="m_input" maxlength="2" name="begin_time_h" 
											onfocus="setFocusObj(this,24,1);"
											onblur="checkValid(this,24);"
											onkeypress="event.returnValue=IsDigit();" value="00"/>
										<span
											style="color: #000000; border: 0px; vertical-align: middle; height: 22px"><strong>:</strong>
										</span>
										<input type="text" class="m_input" maxlength="2" name="begin_time_m" 
											onfocus="setFocusObj(this,60,1);"
											onblur="checkValid(this,60);"
											onkeypress="event.returnValue=IsDigit();" value="00" />
									</div>
								</td>
								<td>
									<div style="float: inherit; height: 20px; width: 18px;">
										<button id="0_up" class="m_arrow"
											onmouseup="IsInputTimeValue(1);"
											style="float: left; margin-top: 1px; padding-top: 1px;">
											5
										</button>

										<button id="0_down" class="m_arrow"
											onmouseup="IsInputTimeValue(1);"
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
					<td align="right">下课时间:</td>
					<td>
						<table border="0" cellspacing="0" align="left" valign="bottom" cellpadding="0">
							<tr>
								<td width="40" nowrap style="background-color: #FFFFFF";>
									<div class="m_frameborder">
										<input type="text" class="m_input" maxlength="2" name="end_time_h" 
											onfocus="setFocusObj(this,24,2);"
											onblur="checkValid(this,24);"
											onkeypress="event.returnValue=IsDigit();" value="00"/>
										<span
											style="color: #000000; border: 0px; vertical-align: middle; height: 22px"><strong>:</strong>
										</span>
										<input type="text" class="m_input" maxlength="2" name="end_time_m" 
											onfocus="setFocusObj(this,60,2);"
											onblur="checkValid(this,60);"
											onkeypress="event.returnValue=IsDigit();" value="00" />
									</div>
								</td>
								<td>
									<div style="float: inherit; height: 20px; width: 18px;">
										<button id="0_up" class="m_arrow"
											onmouseup="IsInputTimeValue(2);"
											style="float: left; margin-top: 1px; padding-top: 1px;">
											5
										</button>

										<button id="0_down" class="m_arrow"
											onmouseup="IsInputTimeValue(2);"
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
					<td align="right">每课时分钟数:</td>
					<td><html:text name="trainAtteForm" styleClass="textColorWrite" property="minute"
								   onkeypress="event.returnValue=IsDigit();"
								   style="width:40px;font-size:10pt;text-align:right;"
								   maxlength="3"/></td>
				</tr>
				<tr>
					<td align="right">上课需签到:</td>
					<td><html:checkbox name="trainAtteForm" property="begin_card" value="1"/>
					　　下课需签退:<html:checkbox name="trainAtteForm" property="end_card" value="1"/></td>
				</tr>
				<logic:equal value="0" name="trainAtteForm" property="id">
					<tr>
						<td align="right">公休日顺延:</td>
						<td><input type="checkbox" name="holiday" value="1"/>
						　　节假日顺延:<input type="checkbox" name="feast" value="1"/></td>
					</tr>
				</logic:equal>
			</table>
		</td>
	</tr>
</table>
<center>
<input type="button" name="b_save" value='确定' class="mybutton" onclick="save();"/>&nbsp;
<input type="button" name="b_cls" value='关闭' class="mybutton" onclick="window.close();"/>
</center>
</html:form>
</div>
<script type="text/javascript">
var timeObj;
function setFocusObj(obj,time_vv,timeObjs) 
{	
	if(time_vv=='24'){
		if(obj.value>=24){
			alert("请输入0到23！");
			obj.value='00';
			return false;
		}
	}
	if(time_vv=='60'){
		if(obj.value>=60){
			alert("请输入0到59!");
			obj.value = '00';
			return false;
		}
	}
	this.fObj = obj;
	time_r=time_vv;
	timeObj = timeObjs;
}

function checkValid(obj,time_vv){
	if(time_vv=='24'){
		if(obj.value>=24||obj.value==""){
			alert("时钟只能在0到23");
			obj.value='00';
			return false;
		}
	}
	if(time_vv=='60'){
		if(obj.value>=60||obj.value==""){
			alert("分钟数只能在0到59!");
			obj.value = '00';
			return false;
		}
	}
}

function IsDigit() 
{ 
   return ((event.keyCode >= 47) && (event.keyCode <= 57)); 
}
function IsInputTimeValue(obj) 
{	     
	if(timeObj != obj)
		return;
   	event.cancelBubble = true;//取消事件冒泡
   	var fObj=this.fObj;		
   	if (!fObj) return;	 // 如果 	fObj 为 null  or undifined，直接返回
   	var cmd = event.srcElement.innerText=="5"?true:false; //触发事件的控件（可能是文本框）的 innerText 如果等于5，cmd等于true
   	if(fObj.value==""||fObj.value.lenght<=0)
fObj.value="0";
   	var i = parseInt(fObj.value,10);		//转为10进制的Int类型
   	var radix=parseInt(time_r,10)-1;	//同上			
   	if (i==radix&&cmd) {
       	i = 0;
   	} else if (i==0&&!cmd) {
		i = radix;
   	} else {
		cmd?i++:i--;
   	}	
   	if(i==0)
   	{
	fObj.value = "00"//给控件 fObj赋值
   	}else if(i<10&&i>0)
   	{
	fObj.value="0"+i;
   	}else{
	fObj.value = i;
   	}			
   	fObj.select();//选中fObj中的文本
} 

var b_time="${trainAtteForm.begin_time}";
if(b_time!=null&&b_time.length==5){
	document.getElementById("begin_time_h").value=b_time.substring(0,2);
	document.getElementById("begin_time_m").value=b_time.substring(3);
}
var e_time="${trainAtteForm.end_time}";
if(e_time!=null&&e_time.length==5){
	document.getElementById("end_time_h").value=e_time.substring(0,2);
	document.getElementById("end_time_m").value=e_time.substring(3);
}
</script>