<%@ page contentType="text/html; charset=UTF-8"%>
<%@page import="com.hjsj.hrms.utils.OperateDate"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean"%>
<%@ taglib uri="/tags/struts-html" prefix="html"%>
<%@ taglib uri="/tags/struts-logic" prefix="logic"%>
<%@ page import="java.util.*"%>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms"%>
<%@ page import="com.hjsj.hrms.actionform.kq.kqself.KqSelfForm"%>
<%@page import="com.hrms.hjsj.sys.FieldItem"%>
<%@page import="com.hrms.hjsj.sys.DataDictionary"%>
<%@ page import="java.util.HashMap"%>
<script language="javascript" src="/ajax/format.js"></script>
<script language="JavaScript" src="/js/validate.js"></script>
<script language="JavaScript" src="/js/function.js"></script>
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
	float: left;
	margin-top: 1px;
	<!--[if gte IE 9]>padding-top: 1px;<![endif]-->
	<!--[if lt IE 8]>paddint-top:0px;<![endif]-->
	width: 16px;
	height: 8px;
	font-family: "Webdings";
	font-size: 7px;
	line-height: 2px;
	padding-left: 2px;
	cursor: default;
}

.m_input {
	width: 15px;
	height: 20px;
	border: 0px solid black;
	font-family: "Tahoma";
	font-size: 9px;
	text-align: center;
	padding-top:3px;
}
</style>
<%
	KqSelfForm kqselfForm = (KqSelfForm) session
			.getAttribute("kqselfForm");
	HashMap taskmap = kqselfForm.getTaskMap();
	String id = kqselfForm.getId();
    String kqempcal = kqselfForm.getKqempcal().toString();
	//pageContext.setAttribute("parammap", taskmap);
%>
<script language="javascript">
var kqempcal = "<%=kqempcal%>";

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
      return ((event.keyCode >= 45) && (event.keyCode <= 57)); 
    }
    function printT(id)
    {
       var tab_name="${kqselfForm.table}";       
       var win=window.open("/servlet/OutputKqTemplateDataServlet?tab_name="+tab_name+"&id="+id,"_blank");
    }
	function validate(){
		var tag;
		if(document.kqselfForm.sels.value == ""){
		<logic:equal name="kqselfForm" property="table" value="Q11">
			alert("请选择加班类型!");
		</logic:equal>
		<logic:equal name="kqselfForm" property="table" value="Q13">
			alert("请选择公出类型!");
		</logic:equal>
		<logic:equal name="kqselfForm" property="table" value="Q15">
			alert("请选择请假类型!");
		</logic:equal>
			tag = false;
			return tag;
		}
		
        var objs = document.all.item("app_way");
        var b_flag = "0";
		for(var i = 0;i<objs.length;i++){
			if(objs[i].checked==true){
				b_flag = objs[i].value;
			}
		}
		if (b_flag=="0"){
			var date_count = document.kqselfForm.date_count.value.replace(/[^0-9]+/, ''); 
			document.kqselfForm.date_count.value = date_count;
			if (date_count == "" || date_count == "0" || date_count == "00"){
				alert("申请天数未填写！");
                tag=false;
                return tag;
			}
			if(!isDate(document.kqselfForm.start_d.value,"yyyy-MM-dd"))
	         {
	                 alert("开始时间格式不正确,请输入正确的时间格式！\nyyyy-MM-dd");
	                 tag=false;
	                 return tag;
	        }
		}else if(b_flag=="1"){
			var time_count = document.kqselfForm.time_count.value;//.replace(/[^0-9]+/, '');
			document.kqselfForm.time_count.value = time_count;
			if (time_count == "" || time_count == "0" || time_count == "00"){
				alert("申请时间未填写！");
                tag=false;
                return tag;
            }
			var start_date = document.getElementById("start_d").value;
			var start_time_h = document.kqselfForm.start_time_h.value;
			var start_time_m = document.kqselfForm.start_time_m.value;
			if(!isDate(start_date+" "+start_time_h+":"+start_time_m,"yyyy-MM-dd HH:mm")){
                alert("开始时间格式不正确,请输入正确的时间格式！\nyyyy-MM-dd HH:mm");
                tag=false;
                return tag;
         	}
		}else if(b_flag=="2"){
			if(!isDate(document.kqselfForm.scope_start_time.value,"yyyy-MM-dd HH:mm")){
                alert("开始时间格式不正确,请输入正确的时间格式！\nyyyy-MM-dd HH:mm");
                tag=false;
                return tag;
         	}
         	if(!isDate(document.kqselfForm.scope_end_time.value,"yyyy-MM-dd HH:mm")){
        		alert("结束时间格式不正确,请输入正确的时间格式！\nyyyy-MM-dd HH:mm");
        		tag=false;
            	return tag;
         	}
		}
		<logic:equal name="kqselfForm" property="table" value="Q11">
		<%
			if(!hiddenQ1104)
			{
		%>
			if (document.kqselfForm.class_id.value == "#"){
				var objs = document.all.item("app_way");
				if(b_flag=="0"){
					alert("请选择参考班次！");
	            	tag=false;
	            	return tag;
	            }
			}
		<%
			}
		%>
		</logic:equal>
		if(trimStr(document.kqselfForm.app_reason.value) == ""){
		<logic:equal name="kqselfForm" property="table" value="Q11">
			alert("请填写加班事由！");
		</logic:equal>
		<logic:equal name="kqselfForm" property="table" value="Q13">
			alert("请填写公出事由！");
		</logic:equal>
		<logic:equal name="kqselfForm" property="table" value="Q15">
			alert("请填写请假事由！");
		</logic:equal>
            tag=false;
            return tag;
        }
		return true;
	}
	
	function isValidate(){
		var tag;
		if(document.getElementById("sels").value == ""){
			tag=false;
			return tag;
		}
		
		var objs = document.all.item("app_way");
        var b_flag = "0";
		for(var i = 0;i<objs.length;i++){
			if(objs[i].checked==true){
				b_flag = objs[i].value;
			}
		}
		if (b_flag=="0"){
			var date_count = document.kqselfForm.date_count.value.replace(/[^0-9]+/, ''); 
			document.kqselfForm.date_count.value = date_count;
			if (date_count == "" || date_count == "0" || date_count == "00"){
                tag=false;
                return tag;
			}
			if(!isDate(document.getElementById("start_d").value,"yyyy-MM-dd"))
	         {
	                 tag=false;
	                 return tag;
	         }
		}else if(b_flag=="1"){
			var time_count = document.kqselfForm.time_count.value;//.replace(/[^0-9]+/, '');
			document.kqselfForm.time_count.value = time_count;
			if (time_count == "" || time_count == "0" || time_count == "00"){
                tag=false;
                return tag;
            }
			var start_date = document.getElementById("start_d").value;
			var start_time_h = document.kqselfForm.start_time_h.value;
			var start_time_m = document.kqselfForm.start_time_m.value;
			if(!isDate(start_date+" "+start_time_h+":"+start_time_m,"yyyy-MM-dd HH:mm")){
                tag=false;
                return tag;
         	}
		}else if(b_flag=="2"){
			if(!isDate(document.kqselfForm.scope_start_time.value,"yyyy-MM-dd HH:mm")){
                tag=false;
                return tag;
         	}
			if(!isDate(document.kqselfForm.scope_end_time.value,"yyyy-MM-dd HH:mm")){
                tag=false;
                return tag;
         	}
         	
		}
		<logic:equal name="kqselfForm" property="table" value="Q11">
		<%
			if(!hiddenQ1104)
			{
		%>
			if (document.getElementById("class_id").value == "#"){
				var objs = document.all.item("app_way");
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
		var objs = document.all.item("app_way");
		var tr1 = document.all.item("startime");
		var tr2 = document.getElementById("datecount");
		<logic:equal name="kqselfForm" property="table" value="Q11">
		var tr3 = document.getElementById("class");
		</logic:equal>
		var tr4 = document.all.item("time_scope");
		var tr5 = document.all.item("time_noscope");
		var b_flag = "0";
		for(var i = 0;i<objs.length;i++){
			if(objs[i].checked==true){
				b_flag = objs[i].value;
			}
		}
		if (b_flag == "0"){
			
			for(var i =0;i<tr1.length;i++){
    			tr1[i].style.display="none";
    		}
    		tr2.style.display="";
    		<logic:equal name="kqselfForm" property="table" value="Q11">
    		<%
				if(hiddenQ1104)
				{
			%>
				tr3.style.display="none";
			<%
				}
			%>

    		</logic:equal>
    		for(var i =0;i<tr4.length;i++){
    			tr4[i].style.display="none";
    		}
    		for(var i =0;i<tr5.length;i++){
    			tr5[i].style.display="";
    		}
		} else if (b_flag == "1"){
			for(var i =0;i<tr1.length;i++){
    			tr1[i].style.display="";
    		}
    		getStartTime();
    		
    		tr2.style.display="none";
    		<logic:equal name="kqselfForm" property="table" value="Q11">
    		tr3.style.display="none";
    		</logic:equal>
    		for(var i =0;i<tr4.length;i++){
    			tr4[i].style.display="none";
    		}
    		for(var i =0;i<tr5.length;i++){
    			tr5[i].style.display="";
    		}
		} else if(b_flag == "2"){
			for(var i =0;i<tr1.length;i++){
    			tr1[i].style.display="none";
    		}
    		tr2.style.display="none";
    		<logic:equal name="kqselfForm" property="table" value="Q11">
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
	 	kqselfForm.action="/kq/kqself/search_kqself.do?b_query=link";
		kqselfForm.target="il_body";
        kqselfForm.submit();
     }
     function saveq(sub_flag)
     {
     	var appReaCode; 
     	var IftoRest;
    	<logic:equal name="kqselfForm" property="table" value="Q11">
     		   appReaCode = $F('appReaCode');

     		  <logic:notEmpty name="kqselfForm" property="isExistIftoRest">
     		  IftoRest = $F('IftoRest');
	     		 if(IftoRest == ""){
	                	alert("请选择是否调休！");
	            		return false;
	             }
           	  </logic:notEmpty>
     	</logic:equal>
     	
     	if(validate()){
        	kqselfForm.action="/kq/kqself/addkqAppSelf.do?b_save=link&sub_flag="+sub_flag+"&appReaCode="+appReaCode+"&IftoRest="+IftoRest;
    		kqselfForm.target="il_body";
    		setEndDate('1');
			
    	}
	}
    
    function setEndDate(sub_flag)
    {
    	<logic:equal name="kqselfForm" property="table" value="Q15">
    	if($F('sels')==""){
    		document.getElementById("njcxts").innerText = "";
			return;
        	}
    	</logic:equal>
    	if(isValidate()){
    		var radios = document.all.item("app_way");
        	var b_flag = "0";
			for(var i=0;i<radios.length;i++){
				if(radios[i].checked==true){
					b_flag = radios[i].value;
				}
			}
    		var obj = new ParameterSet();
			obj.setValue("sels",document.getElementById("sels").value);
			obj.setValue("app_way",b_flag);
			obj.setValue("start_d",document.getElementById("start_d").value);
			obj.setValue("start_time_h",document.getElementById("start_time_h").value);
			obj.setValue("start_time_m",document.getElementById("start_time_m").value);
			obj.setValue("date_count",document.getElementById("date_count").value);
			obj.setValue("time_count",document.getElementById("time_count").value);
			<logic:equal name="kqselfForm" property="table" value="Q11">
				obj.setValue("class_id",document.getElementById("class_id").value);
			</logic:equal>
			obj.setValue("table",document.getElementById("table").value);
			obj.setValue("scope_start_time",document.getElementById("scope_start_time").value);
			obj.setValue("scope_end_time",document.getElementById("scope_end_time").value);
			obj.setValue("reflag",sub_flag);
			if(sub_flag == 1){
	    		var request=new Request({method:'post',onSuccess:getEndDate,functionId:'1510020018'},obj);
	    	} else {
	    		var request=new Request({method:'post',onSuccess:getEndDate1,functionId:'1510020018'},obj);
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
		var start_d;
		var radios = document.all.item("app_way");
        var b_flag = "0";
		for(var i = 0;i<radios.length;i++){
			if(radios[i].checked==true){
				b_flag = radios[i].value;
			}
		}
		if(b_flag != "2"){
	       	if(b_flag == "0"){
	       		count = "申请天数：" + document.getElementById("date_count").value + "天";
	       		start_d = "开始时间：" + outparamters.getValue("startDate");
	       	} else if(b_flag == "1"){
	           	var warnMsg = outparamters.getValue("warnMessage");
	       		if (typeof(warnMsg)!="undefined" && warnMsg != ""){
	           		if(window.confirm(warnMsg)){
		        		count = "申请时长：" + document.getElementById("time_count").value + "小时";
		        		start_d = "开始时间：" + outparamters.getValue("startDate");
	           		}else
	               		return false;
	               }else{
	               	count = "申请时长：" + document.getElementById("time_count").value + "小时";
	        		start_d = "开始时间：" + outparamters.getValue("startDate");
	               }
	       	}
	       	
	       	var end_date = "结束时间：" + outparamters.getValue("endDate");
	       	if(window.confirm(count+"\n" + start_d + "\n" + end_date)){
	   			document.getElementById("end_d").value = outparamters.getValue("endDate");
	   			if(kqempcal == "1"){
		   			var appReaCode; 
		   	        var IftoRest;
		   	        <logic:equal name="kqselfForm" property="table" value="Q11">
		   	               appReaCode = $F('appReaCode');
	
		   	              <logic:notEmpty name="kqselfForm" property="isExistIftoRest">
		   	              IftoRest = $F('IftoRest');
		   	                 if(IftoRest == ""){
		   	                        alert("请选择是否调休！");
		   	                        return false;
		   	                 }
		   	              </logic:notEmpty>
		   	        </logic:equal>
		   	        
		   			var radios = document.all.item("app_way");
		            var b_flag = "0";
		            for(var i=0;i<radios.length;i++){
		                if(radios[i].checked==true){
		                    b_flag = radios[i].value;
		                }
		            }
	   				var obj = new ParameterSet();
	   	            obj.setValue("sels",document.getElementById("sels").value);
	   	            obj.setValue("app_way",b_flag);
	   	            obj.setValue("appReaCode",appReaCode);
	   	            obj.setValue("IftoRest",IftoRest);
	   	            obj.setValue("start_d",document.getElementById("start_d").value);
	   	            obj.setValue("start_time_h",document.getElementById("start_time_h").value);
	   	            obj.setValue("start_time_m",document.getElementById("start_time_m").value);
	   	            obj.setValue("date_count",document.getElementById("date_count").value);
	   	            obj.setValue("time_count",document.getElementById("time_count").value);
	   	            <logic:equal name="kqselfForm" property="table" value="Q11">
	   	                obj.setValue("class_id",document.getElementById("class_id").value);
	   	            </logic:equal>
	   	            obj.setValue("table",document.getElementById("table").value);
	   	            obj.setValue("scope_start_time",document.getElementById("scope_start_time").value);
	   	            obj.setValue("scope_end_time",document.getElementById("scope_end_time").value);
	   	            obj.setValue("end_d",document.getElementById("end_d").value);
	   	            obj.setValue("app_reason",document.getElementById("app_reason").value);
	   	            obj.setValue("sub_flag","1");
	   				var request=new Request({method:'post',onSuccess:parent.empcal_me.viewClose,functionId:'1510020019'},obj);
	   			}else{
	   			    kqselfForm.submit();
	   			}
	       	}
		} 
		else {
			kqselfForm.submit();
		}
			
    }
    
    function getEndDate1(outparamters){

    	if (outparamters.getValue("err_message") != ""){
    		alert(outparamters.getValue("err_message"));
    		return false;
    	}
    	if (outparamters.getValue("pro_message") != ""){
    		if(!window.confirm(outparamters.getValue("pro_message"))){
    			return false;
    		}
        }
    	document.getElementById("end_d").value = outparamters.getValue("endDate");
    	<logic:equal name="kqselfForm" property="table"  value="Q15">
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
    	var objs = document.all.item("app_way");
        var b_flag = "0";
		for(var i = 0;i<objs.length;i++){
			if(objs[i].checked==true){
				b_flag = objs[i].value;
			}
		}
		var obj = new ParameterSet();
		if(b_flag == "1"){
    		if(!isDate(document.kqselfForm.start_d.value,"yyyy-MM-dd"))
        	{
        		return false;
        	}
    		obj.setValue("start_d",document.getElementById("start_d").value);
    	}else if(b_flag == "2"){
    		if(!isDate(document.kqselfForm.scope_start_time.value,"yyyy-MM-dd HH:mm"))
        	{
        		return false;
        	}
        	var scope_s_t = document.kqselfForm.scope_start_time.value.substring(0,10);
        	if(isGetTimeFlag == scope_s_t){
        		return false;
        	}
        	obj.setValue("start_d",scope_s_t);
    	}
    	
		var objs = document.all.item("app_way");
		if(document.kqselfForm.table.value!="Q11"){
			if (b_flag != "0"){
				var request=new Request({method:'post',onSuccess:setStartTime,functionId:'1510020026'},obj);
			}
		}
    }

	function setStartTime(outparamters){
		var objs = document.all.item("app_way");
        var b_flag = "0";
		for(var i = 0;i<objs.length;i++){
			if(objs[i].checked==true){
				b_flag = objs[i].value;
			}
		}
		var start_h = outparamters.getValue("start_time_h");
		var start_m = outparamters.getValue("start_time_m");
		if(b_flag == "1"){
			document.kqselfForm.start_time_h.value = start_h;
			document.kqselfForm.start_time_m.value = start_m;
		}else if(b_flag == "2"){
			var scope_s_t = document.kqselfForm.scope_start_time.value;
        	if(isDate(scope_s_t,"yyyy-MM-dd HH:mm")){
        		isGetTimeFlag = scope_s_t.substring(0,10);
        		scope_s_t = scope_s_t.substring(0,11) + start_h + ":" + start_m;
        		document.kqselfForm.scope_start_time.value = scope_s_t;
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
		var objs = document.all.item("app_way");
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
        //29356 时间为空直接返回
        if(start_h.length==0 || start_m.length==0 || end_h.length==0 || end_m.length==0){
        	//alert("该班次的开始或结束时间为空，请选择其他班次！");
            return;
        }     
        var class_id=outparamters.getValue("class_id");
        if(class_id != "#" && class_id != "" && b_flag == 2){
        	var scope_s_t = document.kqselfForm.scope_start_time.value;
        	if(isDate(scope_s_t,"yyyy-MM-dd HH:mm")){
        		scope_s_t = scope_s_t.substring(0,11) + start_h + ":" + start_m;
        		document.kqselfForm.scope_start_time.value = scope_s_t;
        	}
        	var scope_e_t = document.kqselfForm.scope_end_time.value;
        	if(isDate(scope_e_t,"yyyy-MM-dd HH:mm")){
        		scope_e_t = scope_e_t.substring(0,11) + end_h + ":" + end_m;
        		document.kqselfForm.scope_end_time.value = scope_e_t;
        	}
        }
	}
	
	function isshow(){
		var sels=$F('sels');
		var table = $F("table");
		var hashvo=new ParameterSet();	
		hashvo.setValue("sels",sels);
		hashvo.setValue("start_d",$F("start_d"));
		if(table == "Q15"){
			var request=new Request({method:'post',asynchronous:false,onSuccess:showornot,functionId:'1510020028'},hashvo);
		}
	}
	
	function showornot(outparamters){
		var isshow = outparamters.getValue("isshow");
		if(isshow == "1"){
			document.getElementById('div1').style.display = 'block';
			document.getElementById("usableTime").innerText = outparamters.getValue("usableTime");
		}else{
			document.getElementById('div1').style.display = 'none';
		}
	}
	
	function showdetails(){
		var usableTime = document.getElementById("usableTime").innerText;
		var target_url = "/kq/kqself/addkqAppSelf.do?b_showdetails=link&usableTime="+usableTime+"&start_d="+$F("start_d");
		window.showModalDialog(target_url,1,"dialogWidth:700px; dialogHeight:500px;resizable:no;center:yes;scroll:yes;status:no");
	}
</script>
<script language="javascript">
   	var outObject;
   	var weeks="";
   	var feasts ="";
   	var turn_dates="";
   	var week_dates="";
   	
    function getKqCalendarVar()
   	{
     	var request=new Request({method:'post',asynchronous:false,onSuccess:setkqcalendar,functionId:'15388800008'});
   	}
   	function setkqcalendar(outparamters)
   	{
      	weeks=outparamters.getValue("weeks");  
      	feasts=outparamters.getValue("feasts"); 
      	turn_dates=outparamters.getValue("turn_dates"); 
      	week_dates=outparamters.getValue("week_dates");  
   	}
	function IsDigit() 
    { 
       return ((event.keyCode >= 48) && (event.keyCode <= 57)); 
    }
    function IsDigits(obj){
		if(checkIsNum(obj.value))
			setEndDate(0);
		else{
			alert(PLEASE_INPUT_CORRECT_DIGIT);
			document.kqselfForm.time_count.value = '';
		}
    }
    //重置 
    function resetEx(form)
    {
		form.reset();
		init();
    }
</script>
<body onload="init();">
<html:form action="/kq/kqself/addkqAppSelf.do">
    <logic:notEqual name="kqselfForm" property="kqempcal" value="1">
	   <br>
	</logic:notEqual>
	<html:hidden name="kqselfForm" property="table" styleId="table"/>
	<logic:notEqual name="kqselfForm" property="kqempcal" value="1">
       <br>
    </logic:notEqual>
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
					<tr id="wangmj">
						<td align="right" nowrap="nowrap">
							<logic:equal name="kqselfForm" property="table" value="Q11">
								<bean:message key="kq.class.worktype" />
							</logic:equal>
							<logic:equal name="kqselfForm" property="table" value="Q15">
								<bean:message key="kq.class.leavetype" />
							</logic:equal>
							<logic:equal name="kqselfForm" property="table" value="Q13">
								<bean:message key="kq.class.evectiontype" />
							</logic:equal>
						</td>
						<td nowrap="nowrap">
							<table>
								<tr>
									<td>
										<div>
											<html:select name="kqselfForm" property="sels" styleId="sels" size="1"
												disabled="false" onchange="setEndDate(0);isshow();">
												<html:optionsCollection name="kqselfForm" property="selist"
													value="dataValue" label="dataName" />
											</html:select>
											<logic:equal name="kqselfForm" property="table" value="Q15">
												&nbsp;<font id="njcxts"></font>
											</logic:equal>
										</div>
									
									</td>
									<td>
										<div id="div1" style="display:none">
										<logic:equal value="Q15" property="table" name="kqselfForm">
											<a href="#" style="text-decoration:underline" onclick="showdetails();">
											(可调休时数<font id="usableTime"></font>小时)
											</a>
										</logic:equal>
										</div>
									</td>
								</tr>
							</table>
						</td>
					</tr>
					<tr>
						<td align="right" nowrap="nowrap">
							<bean:message key="kq.class.applyscope" />
						</td>
						<td>
							<html:radio name="kqselfForm" property="app_way" value="0"
								onclick="init();setEndDate('0');"></html:radio>
							<bean:message key="kq.shift.relief.day" />
							<html:radio name="kqselfForm" property="app_way" value="1"
								onclick="init();setEndDate('0');"></html:radio>
							<bean:message key="kq.class.hour" />
							<html:radio name="kqselfForm" property="app_way" value="2"
								onclick="init();setEndDate('0');"></html:radio>
							<bean:message key="kq.time.space" />
						</td>
					</tr>
					<tr>
					<logic:equal name="kqselfForm" property="table" value="Q11">
						<logic:notEqual name="kqselfForm" property="dert_itemid"
							value="">
							<td align="right" nowrap="nowrap">
								休息扣除
								<html:hidden name="kqselfForm" property="dert_itemid"
									styleClass="text" />
							</td>
							<td nowrap>
								<table>
									<tr>
										<td>
											<html:text styleClass="inputtext" name="kqselfForm" styleId='dert_value'
												property="dert_value" size="4" value=""
												onkeypress="event.returnValue=IsDigitNegative();" />
											&nbsp;
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
						</td>
						<td>

							<!--  
							<html:text name="kqselfForm" property="start_d" styleId='z1'
								size="20" maxlength="20"
								onclick='getKqCalendarVar();popUpCalendar(this,this,weeks,feasts,turn_dates,week_dates,false,false);'
								styleClass="TEXT4" />
							-->
							<input type="text" name="start_d"  value="${kqselfForm.start_d }"
								size="16" maxlength="10" id="start_d"
								onclick='getKqCalendarVar();popUpCalendar(this,this,weeks,feasts,turn_dates,week_dates,false,false);'
								style="text-align: right" class="TEXT4"
								onpropertychange="getStartTime();setEndDate(0);isshow();" />
							(日期格式：<%
								out.print(OperateDate.dateToStr(new Date(), "yyyy-MM-dd"));
							%>)
						</td>
					</tr>
					<tr id="startime" style="display: none;">
						<td align="right" nowrap="nowrap">
							<bean:message key="kq.strut.start" />
						</td>
						<td>
							<table border="0" cellspacing="0" align="left" valign="bottom"
								cellpadding="0">
								<tr>
									<td width="40" nowrap style="background-color: #FFFFFF";>
										<div class="m_frameborder inputtext">
											<table border="0" cellpadding="0" cellspacing="0"><tr><td valign="middle">
											<html:text styleClass="m_input" maxlength="2"
												name="kqselfForm" property="start_time_h" 
												styleId="start_time_h"
												onfocus="setFocusObj(this,24);"
												onkeypress="event.returnValue=IsDigit();"
												onchange="setEndDate(0);" />
												</td><td valign="top">
											<strong>:</strong>
											</td><td valign="middle">
											<html:text styleClass="m_input" maxlength="2"
												name="kqselfForm" property="start_time_m"
												styleId="start_time_m"
												onfocus="setFocusObj(this,60);"
												onkeypress="event.returnValue=IsDigit();"
												onchange="setEndDate(0);" />
												</td></tr></table>
										</div>
									</td>
									<td>
										<table border="0" cellspacing="2" cellpadding="0"><tr><td>
											<button id="0_up" class="m_arrow"
												onmouseup="IsInputTimeValue();setEndDate(0);">
												5
											</button>
											</td></tr><tr><td>
											<button id="0_down" class="m_arrow"
												onmouseup="IsInputTimeValue();setEndDate(0);">
												6
											</button>
										</td></tr></table>
									</td>
								</tr>
							</table>
						</td>
					</tr>
					<tr id="datecount">
						<td align="right" nowrap="nowrap">
							<bean:message key="kq.class.applyday" />
						</td>
						<td>
							<html:text property="date_count" styleId="date_count" maxlength="3" size="10"
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
						</td>
						<td>
							<html:text property="time_count" styleId="time_count" maxlength="4" size="10"
								styleClass="TEXT4" style="text-align:right"
								onkeypress="event.returnValue=IsDigitNegative();"
								onchange="IsDigits(this);"></html:text>
							<bean:message key="kq.class.hour" />
						</td>
					</tr>
					<tr id="time_noscope">
						<td align="right" nowrap="nowrap">
							<bean:message key="lable.zp_plan.end_date" />
							<br />
						</td>
						<td>
							<html:text name="kqselfForm" property="end_d" styleId='end_d'
								readonly="true" size="20" maxlength="20" styleClass="TEXT4"
								style="background-color:#F8F8F8;" />
						</td>
					</tr>
					<tr id="time_scope" style="display: none;">
						<td align="right" nowrap="nowrap">
							<bean:message key="kq.deration_details.start" />
						</td>
						<td>
							<input type="text" name="scope_start_time"
								value="${kqselfForm.scope_start_time }" size="16" maxlength="16"
								id="scope_start_time"
								onclick='getKqCalendarVar();popUpCalendar(this,this,weeks,feasts,turn_dates,week_dates,true,false);'
								onpropertychange="getStartTime();" class="TEXT4" />
							(日期格式：<%
								out.print(OperateDate.dateToStr(new Date(),
													"yyyy-MM-dd HH:mm"));
							%>)
						</td>
					</tr>
					<tr id="time_scope" style="display: none;">
						<td align="right" nowrap="nowrap">
							<bean:message key="lable.zp_plan.end_date" />
						</td>
						<td>
							<input type="text" name="scope_end_time" size="16" maxlength="16"
								id="scope_end_time"  value="${kqselfForm.scope_end_time }"
								onclick='getKqCalendarVar();popUpCalendar(this,this,weeks,feasts,turn_dates,week_dates,true,false);'
							 class="TEXT4" />
						</td>
					</tr>
					<logic:equal name="kqselfForm" property="table" value="Q11">
					<tr id="class">
						<td align="right" nowrap="nowrap">
							<bean:message key="kq.class.re.class" />
						</td>
						<td>
							<html:select name="kqselfForm" property="class_id" styleId="class_id" size="1"
								disabled="false" onchange="setEndDate(0);changeClassID(this);">
								<html:optionsCollection name="kqselfForm" property="class_list"
									value="dataValue" label="dataName" />
							</html:select>
						</td>
					</tr>
					</logic:equal>
					<logic:equal name="kqselfForm" property="table" value="Q11">
					<logic:notEmpty name="kqselfForm" property="appReaCodesetid">
					<tr>
						<td align="right" nowrap="nowrap">
							加班原因
						</td>
						<td align="left">
							<input type="text" name="appReaCodedesc" id="appReaCodedesc"
									size="20" readonly="readonly" disabled="disabled"/>
								<input type="hidden" name="appReaCode" id="appReaCode"/>
								<img src="/images/code.gif"
									onclick='javascript:openInputCodeDialogText("${kqselfForm.appReaCodesetid}","appReaCodedesc","appReaCode");' />
						</td>
					</tr>
					</logic:notEmpty>
					<logic:notEmpty name="kqselfForm" property="isExistIftoRest">
						<tr>
							<td>
								是否调休
							</td>
							<td align="left">
								<html:select property="IftoRest" name="kqselfForm" value="" size="1">
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
							<logic:equal name="kqselfForm" property="table" value="Q11">
								<bean:message key="kq.class.overtimesake" />
							</logic:equal>
							<logic:equal name="kqselfForm" property="table" value="Q15">
								<bean:message key="kq.class.leavesake" />
							</logic:equal>
							<logic:equal name="kqselfForm" property="table" value="Q13">
								<bean:message key="kq.class.evectionsake" />
							</logic:equal>
						</td>
						<td>
							<html:textarea name="kqselfForm" property="app_reason" cols="35"
								rows="4" styleClass="text5"></html:textarea>
						</td>
					</tr>
					<tr>
						<td height="10"></td>
					</tr>
				</table>
			</td>
		</tr>
		<tr class="list3">
			<td align="center" colspan="4" style="height: 35px;" nowrap="nowrap">
				<table align="center">
					<tr>
						<td style="height: 35px;">
							<logic:equal name="kqselfForm" property="table" value="Q15">
								<hrms:priv func_id="0B221">
									<html:button styleClass="mybutton" property="b_save"
										onclick="return saveq('08');">
										<bean:message key="button.report" />
									</html:button>
								</hrms:priv>
								<hrms:priv func_id="0B222">
									<html:button styleClass="mybutton" property="b_save"
										onclick="return saveq('02');">
										<bean:message key="button.appeal" />
									</html:button>
								</hrms:priv>
							</logic:equal>
							<logic:equal name="kqselfForm" property="table" value="Q11">
								<hrms:priv func_id="0B211">
									<html:button styleClass="mybutton" property="b_save"
										onclick="return saveq('08');">
										<bean:message key="button.report" />
									</html:button>
								</hrms:priv>
								<hrms:priv func_id="0B212">
									<html:button styleClass="mybutton" property="b_save"
										onclick="return saveq('02');">
										<bean:message key="button.appeal" />
									</html:button>
								</hrms:priv>
							</logic:equal>
							<logic:equal name="kqselfForm" property="table" value="Q13">
								<!--<hrms:submit styleClass="mybutton" property="b_save">
            		<bean:message key="button.save"/>
	 	           </hrms:submit>-->
								<hrms:priv func_id="0B231">
									<html:button styleClass="mybutton" property="b_save"
										onclick="return saveq('08');">
										<bean:message key="button.report" />
									</html:button>
								</hrms:priv>
								<hrms:priv func_id="0B232">
									<html:button styleClass="mybutton" property="b_save"
										onclick="return saveq('02');">
										<bean:message key="button.appeal" />
									</html:button>
								</hrms:priv>
							</logic:equal>
							<logic:notEqual name="kqselfForm" property="id" value="">
								<logic:equal name="kqselfForm" property="isTemplate" value="1">
									<input type="button" name="btnreturn" value='打印'
										onclick="printT('<%=id%>');" class="mybutton">
								</logic:equal>
							</logic:notEqual>
							<input type="button" class="mybutton" onclick="resetEx(kqselfForm);" value="<bean:message key="button.clear"/>"  /> 
							<%--<hrms:fixflowbutton name="加班" url="${kqselfForm.taskurl}"
								parammap="<%=taskmap%>" formname="kqselfForm" js_flag="1" />--%>
							<logic:notEqual name="kqselfForm" property="kqempcal" value="1">	
								<input type="button" name="btnreturn"
									value='<bean:message key="kq.emp.button.return"/>'
									onclick="history.back();" class="mybutton">
							</logic:notEqual>
						</td>
					</tr>
				</table>
			</td>
		</tr>
	</table>
</html:form>
</body>