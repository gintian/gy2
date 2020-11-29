<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean"%>
<%@ taglib uri="/tags/struts-html" prefix="html"%>
<%@ taglib uri="/tags/struts-logic" prefix="logic"%>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms"%>
<link rel="stylesheet" href="/css/css1.css" type="text/css">
<script type="text/javascript">
<!--
function checkTime(i)
{
	if (i<10) {
		i="0" + i;
	}
  	return i;
}
function reloop(){         
	var today=new Date();
	var y=today.getFullYear();
	var mm=today.getMonth()+1;
	var d=today.getDate();
	var h=today.getHours();
	var m=today.getMinutes();
	var s=today.getSeconds();
	mm=checkTime(mm);
	d=checkTime(d);
	m=checkTime(m);
	s=checkTime(s);

	var tx=y+"-"+mm+"-"+d+" "+h+":"+m+":"+s;
	document.getElementById("dateTime").value = tx;
	setTimeout("reloop()",1000);
}
function getUserByCard(obj){
	var card_num = obj.value;
	if(card_num == null || card_num == ""){
		closee();
	}else{
		var hashvo = new ParameterSet();
		var classplan = document.getElementById("classplan").value;
		var into = document.getElementById("into");
		
		// 选中未排成班的人员刷卡时直接进库时，输入卡号查询人员范围按管理范围过滤
		if (into && into.checked == true) {
			hashvo.setValue("into","1");
		} else {
			hashvo.setValue("into","0");
		}
		
		hashvo.setValue("card_num",card_num);
		hashvo.setValue("classplan",classplan);
		showSelectBox(obj);
		var request=new Request({method:'post',onSuccess:showSelectUser,functionId:'2020020240'},hashvo);
	}
}
function onEnter(e){
	
	e = window.event || e;
	var k = e.keyCode || e.which;
	if(k==220){
		event.keyCode=0;
		event.returnValue=false;
	}
		if (k == 13) {
		save();
		closee();
		var cardObj = document.getElementById("card_num");
		cardObj.value="";
	}
}
function showSelectUser(outparamters){
	var empList=outparamters.getValue("empList");		
	if(empList!=null){
		AjaxBind.bind($('user_box'),empList);
	}
}
function closee(){
	Element.hide('UserPnl');
}
function showSelectBox(srcobj){
	Element.show('UserPnl');   
	var pos=getAbsPosition(srcobj);
	with($('UserPnl')){
		style.position="absolute";
		style.posLeft=pos[0];
		style.posTop=pos[1]-1+srcobj.offsetHeight;
		style.width=(srcobj.offsetWidth<150)?150:srcobj.offsetWidth;
	}                 
}  
function setSelectValue(){
	var obj=$('user_box');
	var cardObj = document.getElementById("card_num");
	var card_num = "";
	for(var i = 0; i < obj.options.length; i++){
    	if(obj.options[i].selected){
			card_num=obj.options[i].value;
		}
	}
	cardObj.value=card_num;
	closee();
	save();
	cardObj.focus();
}

function save(){
	var hashvo = new ParameterSet();
	var courseplan = document.getElementById("courseplan").value;
	var card_num = document.getElementById("card_num").value;
	var into = document.getElementById("into");
	if (into && into.checked == true) {
		hashvo.setValue("into","1");
	} else {
		hashvo.setValue("into","0");
	}
	hashvo.setValue("courseplan",courseplan);
	hashvo.setValue("classplan",window.dialogArguments);
	hashvo.setValue("card_num",card_num);
	hashvo.setValue("regFlag",${trainAtteForm.regFlag});
	var request=new Request({method:'post',onSuccess:addReg,functionId:'2020020241'},hashvo);
}

function addReg(outparamters){
	var isinto = outparamters.getValue("isinto");
	if ("0" == isinto) {
		var u_name = outparamters.getValue("u_name");
		var nowTime = outparamters.getValue("nowTime");
		var reg_state = outparamters.getValue("reg_state");
		var state = "";
		if(reg_state == '0'){
			state = "正常";
		}else if(reg_state == '1'){
			state = "迟到";
		}else if(reg_state == '2'){
			state = "早退";
		}else {
			state = "";
		}
		document.getElementById("u_name").innerText = u_name;
		document.getElementById("nowTime").innerText = nowTime;
		document.getElementById("reg_state").innerText = state;
	} else {
		var a0101 = outparamters.getValue("a0101");
		var classname = outparamters.getValue("classname");
		if (confirm(a0101+"未排进“"+classname+"”培训班，是否把"+a0101+"增加到“"+classname+"”培训班？")) {
			var hashvo = new ParameterSet();
			var courseplan = document.getElementById("courseplan").value;
			var card_num = document.getElementById("card_num").value;
			hashvo.setValue("into","1");
			hashvo.setValue("courseplan",courseplan);
			hashvo.setValue("classplan",window.dialogArguments);
			hashvo.setValue("card_num",card_num);
			hashvo.setValue("regFlag",${trainAtteForm.regFlag});
			var request=new Request({method:'post',onSuccess:addReg,functionId:'2020020241'},hashvo);
		}
	}
}
//-->
</script>
<hrms:themes />
<html:form action="/train/attendance/registration">
	<table width="340px" align="center" border="0" cellpadding="0"
		cellspacing="0" class="ListTable">
		<tr>
			<td class="TableRow" nowrap="nowrap" align="center" colspan="2">
				<bean:write name="trainAtteForm" property="courseplanName" />
				<html:hidden name="trainAtteForm" property="courseplan" />
				<html:hidden name="trainAtteForm" property="classplan" />
			</td>
		</tr>
		<tr>
			<td class="RecordRow" align="center" colspan="2">
				<input type="text" id="dateTime" style="border: 0" size="20"
					readonly="readonly" disabled="disabled" />
			</td>
		</tr>
		<tr>
			<td class="TableRow" align="center" colspan="2">
				<!-- 请刷卡 -->
				<bean:message key="train.b_plan.reg.card"/>
			</td>
		</tr>
		<tr>
			<td class="RecordRow" align="center" colspan="2">
				<html:text name="trainAtteForm" property="card_num" size="16" styleClass="text4"
					onkeyup="getUserByCard(this);" onkeydown="onEnter(event);" tabindex="1" />
			</td>
		</tr>
		<tr>
			<td class="RecordRow" align="center">
				<!-- 姓&nbsp;&nbsp;&nbsp;&nbsp;名 -->
				<bean:message key="train.b_plan.reg.name"/>
			</td>
			<td id="u_name" class="RecordRow" align="left" width="60%">
				&nbsp;&nbsp;
			</td>
		</tr>
		<tr>
			<td class="RecordRow" align="center">
				<!-- 刷卡时间 -->
				<bean:message key="train.b_plan.reg.time"/>
			</td>
			<td id="nowTime" class="RecordRow" align="left" width="60%">
			</td>
		</tr>
		<tr>
			<td class="RecordRow" align="center">
				<!-- 状&nbsp;&nbsp;&nbsp;&nbsp;态 -->
				<bean:message key="train.b_plan.reg.state"/>
			</td>
			<td id="reg_state" class="RecordRow" align="left" width="60%">
			</td>
		</tr>
		<tr>
			<td colspan="2" align="center" class="RecordRow">
				<input name="into" type="checkbox" id="into" value="1" onfocus="abc()"><bean:message key="train.b_plan.reg.intodatabase"/>
			</td>
		</tr>
		<tr>
			<td align="center" colspan="2" style="padding-top: 5px;">
				<center>
					<input type="button" name="b_cls" value='关闭' class="mybutton"
						onclick="window.close();" onfocus="abc()"/>
				</center>
			</td>
		</tr>
	</table>
</html:form>
<div id="UserPnl" style="border-style: nono">
	<select name="user_box" multiple="multiple" size="6" style="width: 125"
		ondblclick="setSelectValue();">
	</select>
</div>
<script language="javascript">
reloop();
Element.hide('UserPnl');
function abc(){
	var cardObj = document.getElementById("card_num");
	cardObj.value="";
	cardObj.focus();
}
</script>