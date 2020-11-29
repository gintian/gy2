<%@ page contentType="text/html; charset=UTF-8"%>
<%@page import="com.hrms.hjsj.sys.FieldItem"%>
<%@page import="com.hrms.hjsj.sys.DataDictionary"%>
<%@page import="com.hjsj.hrms.utils.OperateDate"%>
<%@ page import="java.util.*"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean"%>
<%@ taglib uri="/tags/struts-html" prefix="html"%>
<%@ taglib uri="/tags/struts-logic" prefix="logic"%>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms"%>
<script language="javascript" src="/ajax/format.js"></script>
<script language="JavaScript" src="/js/validate.js"></script>
<script language="JavaScript" src="/js/function.js"></script>
<script language="JavaScript" src="/js/popcalendar.js"></script>
<script language="JavaScript" src="/kq/kq.js"></script>
<script LANGUAGE=javascript src="/js/xtree.js"></script>
<%
	boolean hiddenQ1104 = false;
	FieldItem fieldItem = DataDictionary.getFieldItem("q1104");
	if(fieldItem != null){
		String state = fieldItem.getState();
		if("0".equals(state))
			hiddenQ1104 = true;
	}
%>
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
<script language="javascript">
	function validate(){
		var tag;
		if(document.appForm.mess.value == ""){
		<logic:equal name="appForm" property="table" value="Q11">
			alert("请选择加班类型!");
		</logic:equal>
		<logic:equal name="appForm" property="table" value="Q13">
			alert("请选择公出类型!");
		</logic:equal>
		<logic:equal name="appForm" property="table" value="Q15">
			alert("请选择请假类型!");
		</logic:equal>
			tag=false;
			return tag;
		}
		
        var objs = document.all.item("radio");
        var b_flag = "0";
		for(var i = 0;i<objs.length;i++){
			if(objs[i].checked==true){
				b_flag = objs[i].value;
			}
		}
		if (b_flag=="0"){
			
			var date_count = document.appForm.date_count.value.replace(/[^0-9]+/, ''); 
			document.appForm.date_count.value = date_count;
			if (date_count == "" || date_count == "0" || date_count == "00"){
				alert("申请天数未填写！");
                tag=false;
                return tag;
			}
			if(!isDate(document.appForm.app_start_date.value,"yyyy-MM-dd"))
	         {
	                 alert("开始时间格式不正确,请输入正确的时间格式！\nyyyy-MM-dd");
	                 tag=false;
	                 return tag;
	        }
		}else if(b_flag=="1"){
			var hr_count = document.appForm.hr_count.value;//.replace(/[^0-9]+/, '');
			//document.appForm.hr_count.value = hr_count;
			if (hr_count == "" || hr_count == "0" || hr_count == "00"){
				alert("申请时间未填写！");
                tag=false;
                return tag;
			}
			var start_date = document.getElementById("app_start_date").value;
			var start_time_h = document.appForm.start_time_h.value;
			var start_time_m = document.appForm.start_time_m.value;
			if(!isDate(start_date+" "+start_time_h+":"+start_time_m,"yyyy-MM-dd HH:mm")){
                alert("开始时间格式不正确,请输入正确的时间格式！\nyyyy-MM-dd HH:mm");
                tag=false;
                return tag;
         	}
		}else if(b_flag=="2"){
			if(!isDate(document.appForm.scope_start_time.value,"yyyy-MM-dd HH:mm")){
                alert("开始时间格式不正确,请输入正确的时间格式！\nyyyy-MM-dd HH:mm");
                tag=false;
                return tag;
         	}
         	if(!isDate(document.appForm.scope_end_time.value,"yyyy-MM-dd HH:mm")){
        		alert("结束时间格式不正确,请输入正确的时间格式！\nyyyy-MM-dd HH:mm");
        		tag=false;
            	return tag;
         	}
		}
		<logic:equal name="appForm" property="table" value="Q11">
		<%
			if(!hiddenQ1104)
			{
		%>
			if (document.appForm.mess1.value == "#"){
				if(b_flag == "0"){
					alert("请选择参考班次！");
	            	tag=false;
	            	return tag;
	            }
			}
		<%
			}
		%>
		</logic:equal>
		if(trim(document.appForm.message.value) == ""){
		<logic:equal name="appForm" property="table" value="Q11">
			alert("请填写加班事由！");
		</logic:equal>
		<logic:equal name="appForm" property="table" value="Q13">
			alert("请填写公出事由！");
		</logic:equal>
		<logic:equal name="appForm" property="table" value="Q15">
			alert("请填写请假事由！");
		</logic:equal>
            tag=false;
            return tag;
        }
		return true;
	}
	
	function isValidate(){
		var tag;
		if(document.appForm.mess.value == ""){
			tag=false;
			return tag;
		}
		
        var objs = document.all.item("radio");
        var b_flag = "0";
		for(var i = 0;i<objs.length;i++){
			if(objs[i].checked==true){
				b_flag = objs[i].value;
			}
		}
		if (b_flag=="0"){
			if(!isDate(document.appForm.app_start_date.value,"yyyy-MM-dd")){
	        	tag=false;
				return tag;
	        }
			var date_count = document.appForm.date_count.value.replace(/[^0-9]+/, ''); 
			document.appForm.date_count.value = date_count;
			if (date_count == "" || date_count == "0" || date_count == "00"){
                tag=false;
                return tag;
			}
		}else if(b_flag=="1"){
			var hr_count = document.appForm.hr_count.value;//.replace(/[^0-9]+/, '');
			//document.appForm.hr_count.value = hr_count;
			if (hr_count == "" || hr_count == "0" || hr_count == "00"){
                tag=false;
                return tag;
			}
			var start_date = document.getElementById("app_start_date").value;
			var start_time_h = document.appForm.start_time_h.value;
			var start_time_m = document.appForm.start_time_m.value;
			if(!isDate(start_date+" "+start_time_h+":"+start_time_m,"yyyy-MM-dd HH:mm")){
                tag=false;
                return tag;
         	}
		}else if(b_flag=="2"){
			if(!isDate(document.appForm.scope_start_time.value,"yyyy-MM-dd HH:mm")){
                tag=false;
                return tag;
         	}
			if(!isDate(document.appForm.scope_end_time.value,"yyyy-MM-dd HH:mm")){
                tag=false;
                return tag;
         	}
		}
		<logic:equal name="appForm" property="table" value="Q11">
		<%
			if(!hiddenQ1104)
			{
		%>
			if (document.appForm.mess1.value == "#"){
				if(b_flag=="0"){
	            	tag=false;
	            	return tag;
	            }
			}
		<%
			}
		%>
		</logic:equal>
		return true;
	}
	
	function init(){
		var b_flag = "0";
		var objs = document.all.item("radio");
		for(var i = 0;i<objs.length;i++){
			if(objs[i].checked==true){
				b_flag = objs[i].value;
			}
		}
		var tr1 = document.all.item("startime");
		var tr2 = document.getElementById("datecount");
		<logic:equal name="appForm" property="table" value="Q11">
			var tr3 = document.getElementById("class");
		</logic:equal>
		var tr4 = document.all.item("time_scope");
		var tr5 = document.all.item("time_noscope");

		if(b_flag=="0"){
			for(var i =0;i<tr1.length;i++){
    			tr1[i].style.display="none";
    		}
    		for(var i =0;i<tr4.length;i++){
    			tr4[i].style.display="none";
    		}
    		for(var i =0;i<tr5.length;i++){
    			tr5[i].style.display="";
    		}
    		tr2.style.display="";
    		<logic:equal name="appForm" property="table" value="Q11">
    		<%
				if(hiddenQ1104)
				{
			%>
				tr3.style.display="none";
			<%
				} else {
			%>
    			tr3.style.display="";
    		<% } %>	
			</logic:equal>
		} else if (b_flag=="1"){
			for(var i =0;i<tr1.length;i++){
    			tr1[i].style.display="";
    		}
    		for(var i =0;i<tr4.length;i++){
    			tr4[i].style.display="none";
    		}
    		for(var i =0;i<tr5.length;i++){
    			tr5[i].style.display="";
    		}
    		
    		getStartTime();
    		tr2.style.display="none";
    		<logic:equal name="appForm" property="table" value="Q11">
    			tr3.style.display="none";
    		</logic:equal>
		} else if(b_flag=="2"){
			for(var i =0;i<tr1.length;i++){
    			tr1[i].style.display="none";
    		}
    		tr2.style.display="none";
    		<logic:equal name="appForm" property="table" value="Q11">
    		<%
				if(hiddenQ1104)
				{
			%>
				tr3.style.display="none";
			<%
				} else {
			%>
			tr3.style.display="block";
			<% } %>	
    		</logic:equal>
    		for(var i =0;i<tr4.length;i++){
    			tr4[i].style.display="";
    		}
    		for(var i =0;i<tr5.length;i++){
    			tr5[i].style.display="none";
    		}
    		getStartTime();
		}
	}
	
	function setFocusObj(obj,time_vv) 
    {		
		this.fObj = obj;
		time_r=time_vv;
    }
	
	function IsInputTimeValue() 
    {	     
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
       	if(i==0)
       	{
	  		fObj.value = "00"
       	}else if(i<10&&i>0)
       	{
	  		fObj.value="0"+i;
       	}else{
	  		fObj.value = i;
       	}			
       	fObj.select();
    } 
	 
     function backs()
     {
	 	//appForm.action="/kq/app_check_in/manuselect.do?b_query=link";
        //appForm.submit();
    	 window.history.go(-1);
     }
     function backq()
     {
	  	//appForm.action="/kq/app_check_in/querycon.do?b_simplequery=link";
       // appForm.submit();
    	 window.history.go(-1);
     }
     function saveq(sub_flag)
     {
      	var appReaCode; 
      	var IftoRest;
    	<logic:equal name="appForm" property="table" value="Q11">
     		   appReaCode = $F('appReaCode');
     		  <logic:notEmpty name="appForm" property="isExistIftoRest">
     		     IftoRest = $F('IftoRest');
	     		 if(IftoRest == ""){
	                	alert("请选择是否调休！");
	            		return false;
	             }
              </logic:notEmpty>
     	</logic:equal>
     	if(validate()){
        	appForm.action="/kq/app_check_in/add_kqapp.do?b_save=link&sub_flag="+sub_flag+"&appReaCode="+appReaCode+"&IftoRest="+IftoRest;
    		setEndDate('1');
    	}
	}
    
    function setEndDate(sub_flag)
    {
    	<logic:equal name="appForm" property="table" value="Q15">
    	if($F('mess')==""){
    		document.getElementById("njcxts").innerText = "";
			return;
        	}
    	</logic:equal>
    	if(isValidate()){
    	    var radios = document.all.item("radio");
        	var b_flag = "0";
			for(var i = 0;i<radios.length;i++){
				if(radios[i].checked==true){
					b_flag = radios[i].value;
				}
			}

    		var obj = new ParameterSet();
			obj.setValue("mess",document.appForm.mess.value);
			obj.setValue("radio",b_flag);
			obj.setValue("start_date",document.getElementById("app_start_date").value);
			obj.setValue("start_time_h",document.appForm.start_time_h.value);
			obj.setValue("start_time_m",document.appForm.start_time_m.value);
			obj.setValue("date_count",document.appForm.date_count.value);
			obj.setValue("hr_count",document.appForm.hr_count.value);
			<logic:equal name="appForm" property="table" value="Q11">
				obj.setValue("mess1",document.appForm.mess1.value);
			</logic:equal>
			obj.setValue("infoStr",document.appForm.infoStr.value);
			obj.setValue("table",document.appForm.table.value);
			obj.setValue("dbpre",document.appForm.dbpre.value);
			obj.setValue("scope_start_time",document.appForm.scope_start_time.value);
			obj.setValue("scope_end_time",document.appForm.scope_end_time.value);
			obj.setValue("reflag",sub_flag);
			if(sub_flag == 1){
	    		var request=new Request({method:'post',onSuccess:getEndDate,functionId:'1510010113'},obj);
	    	} else {
	    		var request=new Request({method:'post',onSuccess:getEndDate1,functionId:'1510010113'},obj);
	    	}
    	}
    }
    function getEndDate(outparamters)
    {
    	if (outparamters.getValue("err_message") != ""){
    		alert(outparamters.getValue("err_message"));
    		return false;
    	}
    	if (outparamters.getValue("pro_message") != ""){
    		if(!window.confirm(outparamters.getValue("pro_message"))){
    			return false;
    		}
        }
        var count;
		var app_start_date;
		var radios = document.all.item("radio");
        var b_flag = "0";
		for(var i = 0;i<radios.length;i++){
			if(radios[i].checked==true){
				b_flag = radios[i].value;
			}
		}
		if(b_flag != "2"){
        	if(b_flag=="0"){
        		count = "申请天数：" + document.appForm.date_count.value + "天";
        		app_start_date = "开始时间：" + outparamters.getValue("startDate");
        	} else if(b_flag=="1"){
        		count = "申请时长：" + document.appForm.hr_count.value + "小时";
        		app_start_date = "开始时间：" + outparamters.getValue("startDate");
        	}
        	var app_end_date = "终止时间：" + outparamters.getValue("endDate");
        	if(window.confirm(count+"\n" + app_start_date + "\n" + app_end_date)){
    			document.appForm.app_end_date.value = outparamters.getValue("endDate");
        		appForm.target="il_body";
        		appForm.submit();
        	}
        }else{
            appForm.target="il_body";
        	appForm.submit();
        }
    }
    function getEndDate1(outparamters){
    	document.appForm.app_end_date.value = outparamters.getValue("endDate");
    	<logic:equal name="appForm" property="table" value="Q15">
    		var rdx = outparamters.getValue("rdx_message");
    		if(rdx != ""){
    			document.getElementById("njcxts").innerText = outparamters.getValue("njcxts");
    			alert(rdx);
    		} else {
    			document.getElementById("njcxts").innerText = outparamters.getValue("njcxts");
    		}
    	</logic:equal>
    }
    
    var isGetTimeFlag;
    
    function getStartTime()
    {
    	var objs = document.all.item("radio");
        var b_flag = "0";
		for(var i = 0;i<objs.length;i++){
			if(objs[i].checked==true){
				b_flag = objs[i].value;
			}
		}
		var obj = new ParameterSet();
		if(b_flag == "1"){
    		if(!isDate(document.appForm.app_start_date.value,"yyyy-MM-dd"))
        	{
    			 
        		return false;
        	}
    		obj.setValue("start_date",document.getElementById("app_start_date").value);
    	}else if(b_flag == "2"){
    		if(!isDate(document.appForm.scope_start_time.value,"yyyy-MM-dd HH:mm"))
        	{
    			
        		return false;
        	}
        	var scope_s_t = document.appForm.scope_start_time.value;
        	scope_s_t = scope_s_t.substring(0,10);
        	if(isGetTimeFlag == scope_s_t){
        		return false;
        	}
        	obj.setValue("start_date",scope_s_t);
    	}
    	obj.setValue("infoStr",document.appForm.infoStr.value);
    	obj.setValue("dbpre",document.appForm.dbpre.value);
		if(document.appForm.table.value!="Q11"){
			if (b_flag != "0"){
				var request=new Request({method:'post',onSuccess:setStartTime,functionId:'1510010114'},obj);
			}
		}
    }
    
	function setStartTime(outparamters){
		var objs = document.all.item("radio");
        var b_flag = "0";
		for(var i = 0;i<objs.length;i++){
			if(objs[i].checked==true){
				b_flag = objs[i].value;
			}
		}
		var start_h = outparamters.getValue("start_time_h");
		var start_m = outparamters.getValue("start_time_m");
		if(b_flag == "1"){
			document.appForm.start_time_h.value = start_h;
			document.appForm.start_time_m.value = start_m;
		}else if(b_flag == "2"){
			var scope_s_t = document.appForm.scope_start_time.value;
        	if(isDate(scope_s_t,"yyyy-MM-dd HH:mm")){
        		isGetTimeFlag = scope_s_t.substring(0,10);
        		scope_s_t = scope_s_t.substring(0,11) + start_h + ":" + start_m;
        		document.appForm.scope_start_time.value = scope_s_t;
        	}
		}
	}
	
	function changeClassID(obj){
		var class_id=obj.value;
		var hashvo=new ParameterSet();
       	hashvo.setValue("class_id",class_id);	
       	var request=new Request({method:'post',asynchronous:false,onSuccess:setTiem,functionId:'1510030082'},hashvo);
	}
	function setTiem(outparamters){
		var objs = document.all.item("radio");
        var b_flag = "0";
		for(var i = 0;i<objs.length;i++){
			if(objs[i].checked==true){
				b_flag = objs[i].value;
			}
		}
	    var start_h=outparamters.getValue("start_h");
        var start_m=outparamters.getValue("start_m");
        var end_h=outparamters.getValue("end_h");
        var end_m=outparamters.getValue("end_m");     
        var class_id=outparamters.getValue("class_id");
        if(class_id != "#" && class_id != "" && b_flag == 2){
        	var scope_s_t = document.appForm.scope_start_time.value;
        	if(isDate(scope_s_t,"yyyy-MM-dd HH:mm")){
        		scope_s_t = scope_s_t.substring(0,11) + start_h + ":" + start_m;
        		document.appForm.scope_start_time.value = scope_s_t;
        	}
        	var scope_e_t = document.appForm.scope_end_time.value;
        	if(isDate(scope_e_t,"yyyy-MM-dd HH:mm")){
        		scope_e_t = scope_e_t.substring(0,11) + end_h + ":" + end_m;
        		document.appForm.scope_end_time.value = scope_e_t;
        	}
        }
	}
	
	   function IsInputValue(textid) {	     
		event.cancelBubble = true;
		var fObj=document.getElementById(textid);		
		if(fObj.disabled==true)
		  return false;		
		if (!fObj) return;
		if(fObj.value=="")
		  fObj.value="0";		
		var cmd = event.srcElement.innerText=="5"?true:false;
		var i = parseInt(fObj.value,10);
		var radix = 200-1;		
		if(textid=="dert_value")
		{
		   cmd?i++:i--;
		}else
		{
		   if (i==radix&&cmd) {
			  i = 0;
		   } else if (i==0&&!cmd) {
			i = radix;
		   } else {
			cmd?i++:i--;
		   }
		}
		fObj.value = i;
		fObj.select();
	}
	
	  function IsDigitNegative()
    {
      return ((event.keyCode >= 46) && (event.keyCode <= 57)); 
    }
</script>
<script language="javascript">
   	var outObject;
   	var weeks="";
   	var feasts ="";
   	var turn_dates="";
   	var week_dates="";
	function IsDigit() 
    { 
       return ((event.keyCode >= 48) && (event.keyCode <= 57)); 
    }
    function IsDigits(obj)
    {
    	if(checkIsNum(obj.value))
    		setEndDate(0);
		else{
			alert(PLEASE_INPUT_CORRECT_DIGIT);
			document.appForm.hr_count.value = '';
		}
    }
    function resetEx(form)
    {
		form.reset();
		init();
    }
</script>
<body onload="init();">
<hrms:themes /> <!-- 7.0css -->
<html:form action="/kq/app_check_in/add_kqapp.do">
	<br>
	<html:hidden name="appForm" property="infoStr" />
	<html:hidden name="appForm" property="table" />
	<html:hidden name="appForm" property="dbpre" />
	<br>
	<table width="400" border="0" cellpadding="1" cellspacing="0"
		align="center">
		<tr height="20">
			<td colspan="4" align="center" class="TableRow">
				<bean:message key="lable.overtime" />
			</td>
		</tr>
		<tr>
			<td colspan="4" class="framestyle9">
				<table border="0" cellpadding="0" cellspacing="5"
					class="DetailTable" cellpadding="0" style="">
					<tr>
						<td height="10"></td>
					</tr>
					<tr>
						<td align="right" nowrap="nowrap">
							<logic:equal name="appForm" property="table" value="Q11">
								<bean:message key="kq.class.worktype" /> :
							</logic:equal>
							<logic:equal name="appForm" property="table" value="Q15">
								<bean:message key="kq.class.leavetype" /> :
							</logic:equal>
							<logic:equal name="appForm" property="table" value="Q13">
								<bean:message key="kq.class.evectiontype" /> :
							</logic:equal>
						</td>
						<td>
							<html:select name="appForm" property="mess" size="1"
								disabled="false" onchange="setEndDate(0);">
								<html:optionsCollection property="salist" value="dataValue"
									label="dataName" />
							</html:select>
							<logic:equal name="appForm" property="table" value="Q15">
								&nbsp;<font id="njcxts"></font>
							</logic:equal>
						</td>
					</tr>
					<tr>
						<td align="right" nowrap="nowrap">
							<bean:message key="kq.class.applyscope" />
							:
						</td>
						<td>
							<html:radio name="appForm" property="radio" value="0"
								onclick="init();setEndDate('0');"></html:radio>
							<bean:message key="kq.shift.relief.day" />
							<html:radio name="appForm" property="radio" value="1"
								onclick="init();setEndDate('0');"></html:radio>
							<bean:message key="kq.class.hour" />
							<html:radio name="appForm" property="radio" value="2"
								onclick="init();setEndDate('0');"></html:radio>
							<bean:message key="kq.time.space" />
						</td>
					</tr>
					<tr>
					<logic:equal name="appForm" property="table" value="Q11">
						<logic:notEqual name="appForm" property="dert_itemid" value="">
							<td align="right" nowrap="nowrap">
								休息扣除 :
								<html:hidden name="appForm" property="dert_itemid"
									styleClass="text" />
							</td>
							<td>
								<table>
									<tr>
										<td>
											<html:text name="appForm" styleId='dert_value'
												property="dert_value" size="4" value=""
												onkeypress="event.returnValue=IsDigitNegative();" />
										</td>
										<td>
											<table border="0" cellspacing="2" cellpadding="0">
												<tr>
													<td>
														<button id="1_up" class="m_arrow"
															onmouseup="IsInputValue('dert_value');" 
															style="float: left; margin-top: 1px; padding-top: 1px;">
															5
														</button>
													</td>
												</tr>
												<tr>
													<td>
														<button id="1_down" class="m_arrow"
															onmouseup="IsInputValue('dert_value');"
															style="float: left; margin-top: 1px; padding-top: 1px;">
															6
														</button>
													</td>
												</tr>
											</table>
										</td>
										<td>
											分钟 &nbsp;&nbsp;
											<font color="red">(加班中间休息时长)</font>
										</td>
									</tr>
								</table>
							</td>
						</logic:notEqual>
					</logic:equal>
					</tr>
					<tr id="time_noscope">
						<td align="right" nowrap="nowrap">
							<bean:message key="kq.deration_details.start" />
							:
						</td>
						<td>

							<!--  
							<html:text name="appForm" property="app_start_date" styleId='z1'
								size="20" maxlength="20"
								onclick='getKqCalendarVar();popUpCalendar(this,this,weeks,feasts,turn_dates,week_dates,false,false);'
								styleClass="TEXT4" />
							-->
							<input type="text" name="app_start_date"
								value="${appForm.app_start_date }" size="16" maxlength="10"
								id="app_start_date"
								onclick='getKqCalendarVar();popUpCalendar(this,this,weeks,feasts,turn_dates,week_dates,false,false);'
								style="text-align: right" class="TEXT4"
								onpropertychange="getStartTime();setEndDate(0);" />
							(日期格式：<%
								out.print(OperateDate.dateToStr(new Date(), "yyyy-MM-dd"));
							%>)
						</td>
					</tr>
					<tr id="startime" style="display: none;">
						<td align="right" nowrap="nowrap">
							<bean:message key="kq.strut.start" />
							:
						</td>
						<td>
							<table border="0" cellspacing="0" align="left" valign="bottom"
								cellpadding="0">
								<tr>
									<td width="40" nowrap style="background-color: #FFFFFF">
										<div class="m_frameborder inputtext" align="left">
											<table border="0" cellpadding="0" cellspacing="0"><tr><td valign="middle">
											<html:text styleClass="m_input" maxlength="2" name="appForm" style="font-size:13;"
												property="start_time_h" onfocus="setFocusObj(this,24);"
												onkeypress="event.returnValue=IsDigit();"
												onchange="setEndDate(0);" />
												</td><td valign="top" style="padding-top:1px;">
											<span
												style="color: #000000; border: 0px;"><strong>:</strong>
											</span>
											</td><td valign="middle">
											<html:text styleClass="m_input" maxlength="2" name="appForm" style="font-size:13;"
												property="start_time_m" onfocus="setFocusObj(this,60);"
												onkeypress="event.returnValue=IsDigit();"
												onchange="setEndDate(0);" />
												</td></tr></table>
										</div>
									</td>
									<td>
										<div style="float: inherit; height: 20px; width: 18px;margin-left:2px;">
											<button id="0_up" class="m_arrow"
												onmouseup="IsInputTimeValue();setEndDate(0);"
												style="float: left;padding-left:1px;">
												5
											</button>

											<button id="0_down" class="m_arrow"
												onmouseup="IsInputTimeValue();setEndDate(0);"
												style="float: left; margin-top: 2px;padding-left:1px;">
												6
											</button>
										</div>
									</td>
								</tr>
							</table>
						</td>
					</tr>
					<tr id="datecount">
						<td align="right" nowrap="nowrap">
							<bean:message key="kq.class.applyday" />
							:
						</td>
						<td>
							<html:text property="date_count" maxlength="3" size="10"
								styleClass="TEXT4" style="text-align:right"
								onkeypress="event.returnValue=IsDigit();"
								onchange="setEndDate(0);"></html:text>
							(
							<bean:message key="kq.rest.day" />
							)
						</td>
					</tr>
					<tr id="startime" style="display: none;">
						<td align="right" nowrap="nowrap">
							<bean:message key="kq.class.applytime" />
							:
						</td>
						<td>
							<html:text property="hr_count" maxlength="4" size="10"
								styleClass="TEXT4" style="text-align:right"
								onkeypress="event.returnValue=IsDigitNegative();"
								onchange="IsDigits(this);"></html:text>
							<bean:message key="kq.class.hour" />
						</td>
					</tr>
					<tr id="time_noscope">
						<td align="right" nowrap="nowrap">
							<bean:message key="kq.deration_details.endtime" />
							:
							<br />
						</td>
						<td>
							<html:text name="appForm" property="app_end_date" styleId='z3'
								readonly="true" size="20" maxlength="20" styleClass="TEXT4"
								style="background-color:#F8F8F8;" />
						</td>
					</tr>
					<tr id="time_scope" style="display: none;">
						<td align="right" nowrap="nowrap">
							<bean:message key="kq.strut.start" />
							:
							<br />
						</td>
						<td>
							<input type="text" name="scope_start_time"
								value="${appForm.scope_start_time }" size="16" maxlength="16"
								id="scope_start_time"
								onclick='getKqCalendarVar();popUpCalendar(this,this,weeks,feasts,turn_dates,week_dates,true,false);'
								onpropertychange="getStartTime();"
								class="TEXT4" />
							(日期格式：<%
								out
											.print(OperateDate.dateToStr(new Date(),
													"yyyy-MM-dd HH:mm"));
							%>)
						</td>
					</tr>
					<tr id="time_scope" style="display: none;">
						<td align="right" nowrap="nowrap">
							<bean:message key="kq.deration_details.endtime" />
							:
							<br />
						</td>
						<td>
							<input type="text" name="scope_end_time" size="16" maxlength="16" 
								id="scope_end_time" value="${appForm.scope_end_time }"
								onclick="getKqCalendarVar();popUpCalendar(this,this,weeks,feasts,turn_dates,week_dates,true,false);" 
								class="TEXT4" />
						</td>
					</tr>
					<logic:equal name="appForm" property="table" value="Q11">
					<logic:notEmpty name="appForm" property="appReaCodesetid">
					<tr>
						<td width="60">
							加班原因 :
						</td>
						<td align="left">
							<input type="text" name="appReaCodedesc" id="appReaCodedesc"
									size="20" readonly="readonly" disabled="disabled"/>
								<input type="hidden" name="appReaCode" id="appReaCode"/>
								<img src="/images/code.gif"
									onclick='javascript:openInputCodeDialogText("${appForm.appReaCodesetid}","appReaCodedesc","appReaCode");' />
						</td>
					</tr>
					</logic:notEmpty>
					<logic:notEmpty name="appForm" property="isExistIftoRest">
						<tr>
							<td width="60">
								是否调休
							</td>
							<td align="left">
								<html:select property="IftoRest" name="appForm" value="" size="1">
									<html:option value=""></html:option>
									<html:option value="1">是</html:option>
									<html:option value="2">否</html:option>
								</html:select>
							</td>
						</tr>
					</logic:notEmpty>
					</logic:equal>
					<tr>
						<td align="right" nowrap="nowrap">
							<logic:equal name="appForm" property="table" value="Q11">
								<bean:message key="kq.class.overtimesake" /> :
							</logic:equal>
							<logic:equal name="appForm" property="table" value="Q15">
								<bean:message key="kq.class.leavesake" /> :
							</logic:equal>
							<logic:equal name="appForm" property="table" value="Q13">
								<bean:message key="kq.class.evectionsake" /> :
							</logic:equal>
						</td>
						<td>
							<html:textarea name="appForm" property="message" cols="35"
								rows="4" styleClass="text5"></html:textarea>
						</td>
					</tr>
					<logic:equal name="appForm" property="table" value="Q11">
						<tr id="class">
							<td align="right" nowrap="nowrap">
								<bean:message key="kq.class.re.class" />
								:
							</td>
							<td>
								<html:select name="appForm" property="mess1" size="1"
									disabled="false" onchange="setEndDate(0);changeClassID(this);">
									<html:optionsCollection property="class_list" value="dataValue"
										label="dataName" />
								</html:select>
							</td>
						</tr>
					</logic:equal>
					<tr>
						<td height="10"></td>
					</tr>
				</table>
			</td>
		</tr>
		<tr class="list3">
			<td align="center" colspan="4" style="height: 35px;" nowrap="nowrap">
				<logic:equal name="appForm" property="table" value="Q11">
					<hrms:priv func_id="0C341a,27010a">
						<input type="button" class="mybutton" name="dd"
							value='<bean:message key="button.report"/>'
							onclick="saveq('08');">
					</hrms:priv>
					<hrms:priv func_id="0C341b,27010b">
						<input type="button" class="mybutton" name="dd"
							value='<bean:message key="button.appeal"/>'
							onclick="saveq('02');">
					</hrms:priv>
					<hrms:priv func_id="270102,0C3412">
						<input type="button" class="mybutton" name="dd"
							value='<bean:message key="button.approve"/>'
							onclick="saveq('03');">
					</hrms:priv>
				</logic:equal>
				<logic:equal name="appForm" property="table" value="Q13">
					<hrms:priv func_id="0C343a,27012a">
						<input type="button" class="mybutton" name="dd"
							value='<bean:message key="button.report"/>'
							onclick="saveq('08');">
					</hrms:priv>
					<hrms:priv func_id="0C343b,27012b">
						<input type="button" class="mybutton" name="dd"
							value='<bean:message key="button.appeal"/>'
							onclick="saveq('02');">
					</hrms:priv>
					<hrms:priv func_id="270122,0C3432">
						<input type="button" class="mybutton" name="dd"
							value='<bean:message key="button.approve"/>'
							onclick="saveq('03');">
					</hrms:priv>
				</logic:equal>
				<logic:equal name="appForm" property="table" value="Q15">
					<hrms:priv func_id="0C342a,27011a">
						<input type="button" class="mybutton" name="dd"
							value='<bean:message key="button.report"/>'
							onclick="saveq('08');">
					</hrms:priv>
					<hrms:priv func_id="0C342b,27011b">
						<input type="button" class="mybutton" name="dd"
							value='<bean:message key="button.appeal"/>'
							onclick="saveq('02');">
					</hrms:priv>
					<hrms:priv func_id="270112,0C3422">
						<input type="button" class="mybutton" name="dd"
							value='<bean:message key="button.approve"/>'
							onclick="saveq('03');">
					</hrms:priv>
				</logic:equal>

				<!--   <html:reset styleClass="mybutton" property="reset" onclick="">
					<bean:message key="button.clear" />
				</html:reset>
				-->
				
				<input type="button" class="mybutton" onclick="resetEx(appForm);" value="<bean:message key="button.clear"/>"  /> 
				
				<logic:equal name="appForm" property="selectflag" value="0">
					<!--<hrms:submit styleClass="mybutton" property="br_return" onclick="validate('1')">
            	    <bean:message key="button.return"/>
	     	    </hrms:submit>
	     	    <input type="button" class="mybutton"  name="dd" value="<bean:message key="button.save"/>" onclick="saveq();">   
	     	    -->
					<input type="button" class="mybutton" name="dd"
						value="<bean:message key="button.return"/>" onclick="backs();">
				</logic:equal>
				<logic:equal name="appForm" property="selectflag" value="1">
					<input type="button" class="mybutton" name="dd"
						value="<bean:message key="button.return"/>" onclick="backq();">
				</logic:equal>
			</td>
		</tr>
	</table>
</html:form>
</body>
