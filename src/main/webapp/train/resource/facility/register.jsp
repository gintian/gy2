<%@ page contentType="text/html; charset=UTF-8" language="java"%>
<%@page import="com.hrms.hjsj.sys.DataDictionary"%>
<%@page import="com.hrms.hjsj.sys.FieldItem"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean"%>
<%@ taglib uri="/tags/struts-html" prefix="html"%>
<%@ taglib uri="/tags/struts-logic" prefix="logic"%>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms"%>
<script language="JavaScript" src="/js/validateDate.js"></script>
<script type="text/javascript" src="exchange.js"></script>
<style>
<!--
.m_arrow {
	width: 18px;
	height: 12px;
	font-family: "Webdings";
	font-size: 7px;
	line-height: 0px;
	padding-left: 2px;
	cursor: default;
}
.datestyle{
    width: 122px;
    height:19px;
    border: 1pt solid #60A2BD;
}
.textarea{
    width: 222px;
    border: 1pt solid #60A2BD; 
}
-->
</style>
<script language="javascript">
	function save() {
		
		if(!$('a0100').value)
		{
			alert("请选择人员！");
			$('a0101').focus();
			return;
		}
		
		if(!$('a0101').value){
			alert("姓名不能为空！");
			$('a0101').focus();
			return;
		}
		
		<logic:equal value="out" name="facilityInfoForm" property="state">
		var resNum = $('number').value;
		if((null==resNum)||(""==resNum)||"0"==resNum)
		{
			alert("请输入数量！");
			$('number').focus();
			return;
		}
		else if(0 > parseInt(resNum))
		{
			alert("数量不能为负数，请重新输入！");
			$('number').focus();
			return; 
		}
		</logic:equal>		
		
		var strdate = $('strdate').value;
		if((null==strdate)||(""==strdate))
		{
			alert("请输入日期！");
			$('strdate').focus();
			return;
		}

		var declare = $("declare").value;
		if((null!=declare)||(""!=declare))
		{
			<% 
			FieldItem fieldItem = DataDictionary.getFieldItem("r5911", "r59");
	        int length = fieldItem.getItemlength();
			%>
			var len = lenStat(declare);
			if(len > <%=length%>){
				alert(TRAIN_ROOM_MORE_LENGTH1+<%=length%>+TRAIN_ROOM_MORE_LENGTH2+<%=length%>/2+TRAIN_ROOM_MORE_LENGTH3);
				return;
			}
		}
		
		var hashvo=new ParameterSet();
		hashvo.setValue("fieldId","${facilityInfoForm.fieldId}");
		hashvo.setValue("state","${facilityInfoForm.state}");
		hashvo.setValue("r5900","${facilityInfoForm.r5900}");
		hashvo.setValue("a0100",$('a0100').value);
		hashvo.setValue("strdate",$("strdate").value);
		if($("number"))
			hashvo.setValue("number",$("number").value);
		hashvo.setValue("declare",getEncodeStr($("declare").value));
		var request=new Request({method:'post',asynchronous:false,onSuccess:colsewin,functionId:'2020030111'},hashvo);
	}
	function colsewin(outparamters)
	{
		var flag=outparamters.getValue("flag");		
		if("ok"==flag){
			returnValue="ok";
			window.close();
		}else if("error"==flag){
			alert("操作失败！");
		}else{
			alert(flag);
		}
	}

	function lenStat(str) {
		var len = 0;
        for (var i = 0; i < str.length; i++) {
            var c = str.charCodeAt(i);
            //单字节加1 
            if ((c >= 0x0001 && c <= 0x007e) || (0xff60 <= c && c <= 0xff9f)) {
                len++;
            }
            else {
                len += 2;
            }
        }
        return len;

	}
	
</script>
<html:form action="/train/resource/facility/facilityinfo">
<logic:equal value="out" name="facilityInfoForm" property="state">
	<bean:define id="tmp" value="借出" />
</logic:equal>
<logic:equal value="in" name="facilityInfoForm" property="state">
	<bean:define id="tmp" value="归还" />
</logic:equal>
	<table width="390px" border="0" cellspacing="0" align="center" cellpadding="0" class="ListTableF" style="margin-left: -5px;margin-top: 3px;">
		<tr>
			<td colspan="2" class="TableRow" align="center">
				${tmp }登记
			</td>
		</tr>
		<tr>
			<td align="right" class="RecordRow" width="60px">
				${tmp }项
			</td>
			<td class="RecordRow">
				<bean:write name="facilityInfoForm" property="fieldName" />
			</td>
		</tr>
		<tr>
			<td align="right" class="RecordRow">
				${tmp }人
			</td>
			<td class="RecordRow">
				<input type="text" class="text4" name="a0101" id="a0101" onkeyup="getUserByName(this);" style="width: 222px;height:19px;">
				<input type="hidden" name="a0100" id="a0100" />
			</td>
		</tr>
		<tr>
			<td align="right" class="RecordRow">
				${tmp }日期
			</td>
			<td class="RecordRow">
				<input type="text" class="datestyle common_border_color" name="strdate" id="strdate" extra="editor" dropDown="dropDownDate" onchange=" if(!validate(this,'日期')) {this.focus(); this.value=''; }" value="${facilityInfoForm.strdate }"/>
			</td>
		</tr>
		<logic:equal value="out" name="facilityInfoForm" property="state">
		  <tr>
			<td align="right" class="RecordRow">
				借出数量
			</td>
			<td class="RecordRow">
				<table border="0" cellspacing="0" align="left" valign="bottom" cellpadding="0">
					<tr>
						<td width="30" nowrap style="background-color: #FFFFFF";>
							<input type="text" class="text4" style="width: 30px;height: 20px;text-align: right;" maxlength="8" name="number" id="number" onkeypress="event.returnValue=IsDigit();" value="1"/>
						</td>
						<td>
							<div style="float: inherit; height: 25px; width: 18px;">
								<button id="0_up" class="m_arrow"
									onmouseup="IsInputTimeValue();"
									style="float: left; margin-top: 1px; padding-top: 1px;">
									5
								</button>

								<button id="0_down" class="m_arrow"
									onmouseup="IsInputTimeValue();"
									style="float: left; margin-top: 1px; padding-top: 1px;">
									6
								</button>
							</div>
						</td>
					</tr>
				</table>
			</td>
		  </tr>
		</logic:equal>
		<tr>
			<td align="right" valign="top" class="RecordRow" style="padding-top: 5px;">
				说明
			</td>
			<td class="RecordRow">
				<textarea rows="4" cols="30" name="declare" id="declare" class="textarea common_border_color"></textarea>
			</td>
		</tr>
		</table>
		<table cellpadding="0" cellspacing="0" border="0" align="center">
		<tr style="padding-top: 15px;">
			<td colspan="2" align="center">
				<input type="button" value="确定" class="mybutton" onclick="save();"/>
				<input type="button" value="关闭" class="mybutton" onclick="javascript:window.close();"/>
			</td>
		</tr>
	</table>
</html:form>
<div id="UserPnl" style="border-style: nono">
	<select name="user_box" multiple="multiple" size="5" style="width: 222"
		ondblclick="setSelectValue();">
	</select>
</div>
<script>
	Element.hide('UserPnl');
	if($('number')){
		var fObj = $('number');
		time_r = "${facilityInfoForm.number }";
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
	function getUserByName(obj){
		$("a0100").value = "";
		var _v = obj.value;
		if(_v == null || _v == ""){
			Element.hide('UserPnl');
		}else{
			
			var hashvo = new ParameterSet();
			hashvo.setValue("state","query");
			
			hashvo.setValue("value",getEncodeStr(_v));
			showSelectBox(obj);
			var request=new Request({method:'post',onSuccess:showSelectUser,functionId:'2020030111'},hashvo);
		}
	}
	function showSelectUser(outparamters){
		var empList=outparamters.getValue("empList");		
		if(empList!=null){
			AjaxBind.bind($('user_box'),empList);
		}
	}
	function setSelectValue(){
		var obj=$('user_box');
		var a0100 = document.getElementById("a0100");
		var a0101 = document.getElementById("a0101");
		var _a1 = "";
		var _a2 = "";
		for(var i = 0; i < obj.options.length; i++){
	    	if(obj.options[i].selected){
				_a1=obj.options[i].value;
				_a2=obj.options[i].text;
			}
		}
		a0100.value=_a1;
		a0101.value=_a2;
		Element.hide('UserPnl');
		a0101.focus();
	}
	function IsDigit() 
	{ 
	   return ((event.keyCode >= 47) && (event.keyCode <= 57)); 
	}
	function IsInputTimeValue() 
	{	     
	   	event.cancelBubble = true;
	   	var fObj=this.fObj;		
	   	if (!fObj) return;		
	   	var cmd = event.srcElement.innerText=="5"?true:false;
	   	if(fObj.value==""||fObj.value.lenght<=0||fObj.value=="-")
			fObj.value="1";

	   	var i = 0;
		try {
	   	  i = parseInt(fObj.value,10);	
		} catch(e) {
			i = 1;
		}

		if (isNaN(i))
			i = 1;
		
	   	var radix=parseInt(time_r,10);		
	   	if (radix<0)
		   	radix = 1;
	   	
	   	if (i>=radix&&cmd) {
	       	i = radix;
	   	} else if (i<=1&&!cmd) {
			i = 1;
	   	} else {
			cmd?i++:i--;
	   	}

	   	if (i<=0)
		   	i = 1;
	   	
		fObj.value = i;		
	   	fObj.select();
	} 
</script>