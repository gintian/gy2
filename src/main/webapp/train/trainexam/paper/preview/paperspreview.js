var testFinished = 0;
var isNext = 0;
function stopDefault( e ) {    
 // Prevent the default browser action (W3C)    
 if ( e && e.preventDefault )    
     e.preventDefault();    
    // A shortcut for stoping the browser action in IE    
   else    
       window.event.returnValue = false;    
   return false;    
}  

function beforeFinished(event) {
	if (testFinished != 1) {
		event.returnValue="您确定要退出考试吗？退出后系统将自动交卷!";
	}
	
}
function noFinished(flag,paper_id,r5300,event) {
	if (flag != '2') {
		// 保存
		var map = saveAnswer(flag,paper_id,r5300);
		if (flag == '2') {
			map["paperState"] = "1";
			window.opener.reflash();
		}
		if("0" == document.getElementById("typeid").value)
			document.getElementById("saveid").disabled=true;

		var submit = document.getElementById("submitid");
		if(submit != null)
			submit.disabled=true;

		Rpc({functionId:'2020030183',success:noFinishedSubmitSaveSucc},map);
	} else {
		beforeunloadfunc(flag,paper_id,r5300)
	}
		
}
function noFinishedSubmitSaveSucc(response) {
	var value=response.responseText;
	var map=Ext.decode(value);
	if (map.flag == "2") {
		window.opener.location.href="/train/resource/myexam.do?b_query=link";
		//window.opener.reflash();
	}
}

function beforeunloadfunc(flag,paper_id,r5300){
			// 保存
			var map = saveAnswer2(flag,paper_id,r5300);
			if (flag == '2') {
				//map["paperState"] = "1";
				var parameter = map._getParameter("paperState");
				if (!parameter) {
					parameter = map._addParameter("paperState");
				}
				if (parameter){
					parameter.value = "1";
				}
			}
		
		
      		var request=new Request({method:'post',asynchronous:false,onSuccess:beforeunloadsuccfunc,functionId:'2020030183'},map);
		
			window.opener.location.href="/train/resource/myexam.do?b_query=link";
		}
		
		function beforeunloadsuccfunc(outparamters){
		}

//返回
function returnqURL(url){
	papersPreviewForm.action=url;
	papersPreviewForm.submit();
}
function verify(r5300){//校验
	var hashvo=new ParameterSet();
    hashvo.setValue("r5300",r5300);
    var request=new Request({method:'post',onSuccess:addtypeok,functionId:'2020070009'},hashvo);
}
function addtypeok(outparamters){
	var flag=outparamters.getValue("flag"); 
	if("ok"==flag){
		alert("校验成功！");
	}else if("no"==flag)
		alert(getDecodeStr(outparamters.getValue("mess")));
}
var blurTimes = 0;
function paperPaging(current,state,flag,paper_id,r5300,r5000,times){
	testFinished = 1;
	isNext=1;
	blurTimes=times;
	var map = saveAnswer(flag,paper_id,r5300);
	map["current"] = "" + current;
	map["state"] = "" + state;
	map["r5000"] = "" + r5000;
	Rpc({functionId:'2020030183',success:netxUpSaveSucc},map);
	
}

function netxUpSaveSucc(response) {
	var value=response.responseText;
	var map=Ext.decode(value);
	if (map.biaozhi == "ok") {
		papersPreviewForm.action="/train/trainexam/paper/preview/paperspreview.do?b_paging=link&state="+map.state+"&current="+map.current+"&r5300="+map.r5300+"&paper_id="+map.paper_id+"&r5000="+map.r5000+"&blurTimes="+blurTimes;
		papersPreviewForm.submit();
	} else {
		alert("保存失败！");
	}
}

// 考试交卷后 返回
function returnToMyTest(r5000) {
	
		var form1 = document.getElementById("form1");
		form1.action="/train/trainexam/exam/mytest/mytest.do?b_querry=link&lessonId="+r5000;
		form1.submit();	
}

function bodyUnload(r5000,flag,paper_id,r5300) {
	if (confirm("您确定要退出考试吗？退出后系统将自动交卷！")) {
	}
}

// 考试未交卷就点返回
function returnSelfExam(r5000,flag,paper_id,r5300) {
	Rpc({functionId:'2020030183',success:forcSaveSucc},saveAnswer(flag,paper_id,r5300));

function forcSaveSucc(response) {
	var value=response.responseText;
	var map=Ext.decode(value);
	var form1 = document.getElementById("form1");
	if (map.flag == '5') {
		form1.action="/train/trainexam/exam/mytest/mytest.do?b_querry=link&lessonId="+r5000;
	} else if (map.flag == '2') {
		form1.action=document.getElementById("tomodify").value;
	}
	if (map.r5513 == "1") {
		form1.submit();
	} else {
		if (confirm("您尚未交卷，是否要返回？ 如果返回，系统将自动交卷")) {
			//form1.submit();
			returnSubmitPaper(flag,paper_id,r5300);
		} 
	}
	
}
}

function returnSubmitPaper(flag,paper_id,r5300) {
	var map = saveAnswer(flag,paper_id,r5300);
	if (flag == '2') {
		map["paperState"] = "1";
	}
	Rpc({functionId:'2020030183',success:returnSucc},map);
}
function returnSucc (response) {
	var value=response.responseText;
	var map=Ext.decode(value);
	if (map.biaozhi == "ok") {
		var form1 = document.getElementById("form1");
		form1.action = document.getElementById("tomodify").value;
		form1.submit();
	} else {
		alert("答案保存失败！");
	}
}


function daojishi(divId, seconds, type, actionMethod) {
 	var only = 0;
	if (seconds > 0) {
		only = seconds - 1;
		
	} else {
		eval(actionMethod);
		return;
	}
	
	setTimeout("daojishi('"+divId+"',"+only+",'"+type+"','"+actionMethod+"')", 1000);

	var divObj = document.getElementById(divId);
	
	if(divObj == null)
		return only;
	
	if (type == "3" ) {
		divObj.innerHTML = "剩余" + handlerNum(only) +"秒";
		
	} else if (type == "2") {
		if (only % 60 != 0) {
			divObj.innerHTML = "剩余"+handlerNum(parseInt(only/60)) +"分钟"+handlerNum(parseInt(only%60))+"秒";
		} else {
			divObj.innerHTML = "剩余"+handlerNum(parseInt(only/60)) +"分钟";
		}
		
		
	} else if (type == "1") {
		divObj.innerHTML = "剩余"+handlerNum(parseInt(only/60))+"分"+handlerNum(only%60)+"秒";
	}

}

function daojishi2(divId, seconds, type, actionMethod) {
 	var only = 0;
	if (seconds > 0) {
		only = seconds - 1;
		
		
	} else {
		
		eval(actionMethod);
		return;
	}
	
	//setTimeout("daojishi2('"+divId+"',"+only+",'"+type+"','"+actionMethod+"')", 1000);

	var divObj = document.getElementById(divId);
	
	if(divObj == null)
		return only;
	
	if (type == "3" ) {
		divObj.innerHTML = "剩余" + handlerNum(only) +"秒";
		
	} else if (type == "2") {
		if (only % 60 != 0) {
			divObj.innerHTML = "剩余"+handlerNum(parseInt(only/60)) +"分钟"+handlerNum(parseInt(only%60))+"秒";
		} else {
			divObj.innerHTML = "剩余"+handlerNum(parseInt(only/60)) +"分钟";
		}
		
		
	} else if (type == "1") {
		divObj.innerHTML = "剩余"+handlerNum(parseInt(only/60))+"分"+handlerNum(only%60)+"秒";
	}
	
	return only;
}

/**
*搜集答案
**/
function collectAnswer(src,targ,type) {
	var srcObj = document.getElementById(src);
	var targObj = document.getElementById(targ);
	if ("radio" == type) {
		if (srcObj.checked == true) {
			targObj.value = srcObj.value;
		}
	} else {
		if (srcObj.checked == true) {
			targObj.value = targObj.value + srcObj.value + ",";
		} else {
			targObj.value = targObj.value.replace(srcObj.value +",","");
		}
	}
}

//保存答案
function saveAnswer(flag,paper_id,r5300) {
	var inputObjs = document.getElementsByTagName("input");
	var map = new HashMap();
	// 客观题答案
	var s_answer = "";
	var tempName = "";
	for (i = 0; i < inputObjs.length; i++) {
		var inputObj = inputObjs[i];
		var inputId = inputObj.id;
		var index = inputId.indexOf("_"+flag+"_answer");
		if (index != -1 && "_"+flag+"_answer" == inputId.substr(index)){
			if (inputObj.type == "hidden") {
				s_answer += inputObj.name + ":" + inputObj.value + ";";
			}  else if (inputObj.type == "text") {
				if (tempName.indexOf(inputObj.name+",") == -1) {
					tempName +=inputObj.name + ",";
				}
			}
		}		
	}
	
	var names = tempName.split(",");
	for (i = 0; i < names.length; i++) {
		var objs = document.getElementsByName(names[i]);
		var str = "";
		for (j = 0; j < objs.length; j++) {
			str += trimStr(objs[j].value) + "@,@";
		}
		map.put(names[i], getEncodeStr(str));
	}
	
	// 客观题答案
	map.put("s_answer",s_answer);
	
	// 考试编号
	map.put("paper_id", paper_id);
	// 试卷编号
	map.put("r5300",r5300);
	// flag
	map.put("flag",flag);
	//主观题答案
	var textareaObjs = document.getElementsByTagName("textarea");
	for (i = 0; i < textareaObjs.length; i++) {
		var textareaObj = textareaObjs[i];
		var textareaId = textareaObj.id;
		var index = textareaId.indexOf("_"+flag+"_answer");
		if (index != -1){
			map.put(textareaObj.name,getEncodeStr(trimStr(textareaObj.value)));
		}
	}
	
	return map;
	//Rpc({functionId:'2020030170',success:saveSucc},map);
}

//保存答案
function saveAnswer2(flag,paper_id,r5300) {
	var inputObjs = document.getElementsByTagName("input");
	//var map = new HashMap();
	var map=new ParameterSet()
	// 客观题答案
	var s_answer = "";
	var tempName = "";
	for (i = 0; i < inputObjs.length; i++) {
		var inputObj = inputObjs[i];
		var inputId = inputObj.id;
		var index = inputId.indexOf("_"+flag+"_answer");
		if (index != -1 && "_"+flag+"_answer" == inputId.substr(index)){
			if (inputObj.type == "hidden") {
				s_answer += inputObj.name + ":" + inputObj.value + ";";
			}  else if (inputObj.type == "text") {
				if (tempName.indexOf(inputObj.name+",") == -1) {
					tempName +=inputObj.name + ",";
				}
			}
		}		
	}
	
	var names = tempName.split(",");
	for (i = 0; i < names.length; i++) {
		var objs = document.getElementsByName(names[i]);
		var str = "";
		for (j = 0; j < objs.length; j++) {
			str += trimStr(objs[j].value) + "@,@";
		}
		map.setValue(names[i], str);
	}
	
	
	
	
	// 客观题答案
	map.setValue("s_answer",s_answer);
	
	// 考试编号
	map.setValue("paper_id", paper_id);
	// 试卷编号
	map.setValue("r5300",r5300);
	// flag
	map.setValue("flag",flag);
	
	//map.setValue("paperState","1");
	//主观题答案
	var textareaObjs = document.getElementsByTagName("textarea");
	for (i = 0; i < textareaObjs.length; i++) {
		var textareaObj = textareaObjs[i];
		var textareaId = textareaObj.id;
		var index = textareaId.indexOf("_"+flag+"_answer");
		if (index != -1){
			map.setValue(textareaObj.name,getEncodeStr(trimStr(textareaObj.value)));
		}
	}
	
	return map;
	//Rpc({functionId:'2020030170',success:saveSucc},map);
}


// 保存得分
function savePerScore(flag,paper_id,r5300,a0100,nbase) {
	var inputObjs = document.getElementsByTagName("input");
	var map = new HashMap();
	map.put("flag",flag);
	map.put("paper_id",paper_id);
	map.put("r5300",r5300);
	map.put("a0100",a0100);
	map.put("nbase",nbase);
	for (i = 0; i < inputObjs.length; i++) {
		var inputObj = inputObjs[i];
		var inputName = inputObj.name;
		if ("score_" == inputName.substring(0,6)){
			if (trimStr(inputObj.value).length > 0 && !isNaN(trimStr(inputObj.value))) {
				map.put(inputName,trimStr(inputObj.value));
			}
				
		}
	}
	
	Rpc({functionId:'2020030187',success:savePerScoreSucc},map);
}

function savePerScore2(flag,paper_id,r5300,a0100,nbase) {
	var inputObjs = document.getElementsByTagName("input");
	var map = new HashMap();
	map.put("flag",flag);
	map.put("paper_id",paper_id);
	map.put("r5300",r5300);
	map.put("a0100",a0100);
	map.put("nbase",nbase);
	map.put("issubmit","1");
	for (i = 0; i < inputObjs.length; i++) {
		var inputObj = inputObjs[i];
		var inputName = inputObj.name;
		if ("score_" == inputName.substring(0,6)){
			if (trimStr(inputObj.value).length > 0 && !isNaN(trimStr(inputObj.value))) {
				map.put(inputName,trimStr(inputObj.value));
			}
				
		}
	}
	
	Rpc({functionId:'2020030187',success:savePerScoreSucc2},map);
}

// 保存得分
function saveScore(flag,paper_id,r5300) {
	var inputObjs = document.getElementsByTagName("input");
	var map = new HashMap();
	map.put("flag",flag);
	map.put("paper_id",paper_id);
	map.put("r5300",r5300);
	for (i = 0; i < inputObjs.length; i++) {
		var inputObj = inputObjs[i];
		var inputName = inputObj.name;
		if ("score_" == inputName.substring(0,6)){
			if (trimStr(inputObj.value).length > 0 && !isNaN(trimStr(inputObj.value))) {
				map.put(inputName,trimStr(inputObj.value));
			}
				
		}
	}
	
	Rpc({functionId:'2020030187',success:saveScoreSucc},map);
}

function savePerScoreSucc(response) {
	var value=response.responseText;
	var map=Ext.decode(value);
	if (map.biaozhi == "ok") {
		alert("保存成功!");
	} else {
		alert("保存失败!");
	}
}

function savePerScoreSucc2(response) {
	var value=response.responseText;
	var map=Ext.decode(value);
	if (map.biaozhi == "ok") {
		var form1 = document.getElementById("form1");
		form1.action = "/train/trainexam/exam/student.do?b_query=return&planid=" + map.paper_id;
		form1.submit();
	} else {
		alert("保存失败!");
	}
}

function saveScoreSucc(response) {
	var value=response.responseText;
	var map=Ext.decode(value);
	if (map.biaozhi == "ok") {
		alert("保存成功!");
	} else {
		alert("保存失败!");
	}
}

function saveAll(flag,paper_id,r5300) {
	if("0" == document.getElementById("typeid").value)
		document.getElementById("saveid").disabled=true;
	else{
		document.getElementById("nextid").disabled=true;
		document.getElementById("upid").disabled=true;
	}
		
	document.getElementById("submitid").disabled=true;
	var map = saveAnswer(flag,paper_id,r5300);
	Rpc({functionId:'2020030183',success:saveSucc},map);
}
// 交卷
function submitAnswer(flag,paper_id,r5300,r5000,submit) {
	if (flag != '2') {
		testFinished = 1;
	}
	// 保存
	var map = saveAnswer(flag,paper_id,r5300);
	map.put("r5000", r5000);
	map.put("submit", submit);
	if (flag == '2') {
		map["paperState"] = "1";
		sAlert("您确定要交卷吗？",2,"rpcsub('"+flag+"','"+paper_id+"','"+r5300+"')");
	} else {	
		Rpc({functionId:'2020030183',success:submitSaveSucc},map);
	}
	
	
}

function rpcsub(flag,paper_id,r5300) {
	testFinished = 1;
	if("0" == document.getElementById("typeid").value)
		document.getElementById("saveid").disabled=true;
	else{
		document.getElementById("upid").disabled=true;
	}
	document.getElementById("submitid").disabled=true;
	var map = saveAnswer(flag,paper_id,r5300);
	map["paperState"] = "1";
	Rpc({functionId:'2020030183',success:submitSaveSucc},map);
}

function submitSaveSucc (response) {
	var value=response.responseText;
	var map2=Ext.decode(value);
	// 查询是否有没填的答案
	var map = new HashMap();
	map.put("flag",map2.flag);
	map.put("paper_id",map2.paper_id);
	map.put("r5300",map2.r5300);
	Rpc({functionId:'2020030186',success:checkSucc},map);
}
function checkSucc(response) {
	var value=response.responseText;
	var map2=Ext.decode(value);
	var form1 = document.getElementById("form1");
	form1.action = document.getElementById("tomodify").value;
		if (map2.flag == "2") {
			/**if(map2.biaozhi == "ok") {
				if(confirm("您确定要交卷吗？")) {					
					form1.submit();
				} 
			} else {
				if (confirm("您有未完成的试题，确实要交卷吗？")){
					form1.submit();
				}
			}**/
			reflashPare();
		} else {
			if(map2.biaozhi == "ok") {
				if(confirm("您确定要交卷吗？")) {					
					form1.submit();
				} else {
					if("0" == document.getElementById("typeid").value)
						document.getElementById("saveid").disabled=false;
					else{
						document.getElementById("upid").disabled=false;
					}
					
					document.getElementById("submitid").disabled=false;
				}
			} else {
				if (confirm("您有未完成的试题，确实要交卷吗？")){
					form1.submit();
				} else {
					if("0" == document.getElementById("typeid").value)
						document.getElementById("saveid").disabled=false;
					else{
						var up = document.getElementById("upid");
						if(up != null)
							up.disabled=false;
					}
					
					var submit = document.getElementById("submitid");
					if(submit != null)
						submit.disabled=false;
				}
			}
		}
}

function saveSucc(response) {
	var value=response.responseText;
	var map2=Ext.decode(value);
	if("0" == document.getElementById("typeid").value)
		document.getElementById("saveid").disabled=false;
	else{
		document.getElementById("nextid").disabled=false;
		document.getElementById("upid").disabled=false;
	}
	
	document.getElementById("submitid").disabled=false;
	if(map2.biaozhi == "ok") {
		if (map2.flag == "2") {
			sAlert("试卷保存成功！");
		} else {		
			alert("试卷保存成功！");
		}
	} else if(map2.biaozhi == "no") {
		if (map2.flag == "2") {
			sAlert("试卷保存失败！");
		} else {		
			alert("试卷保存失败！");
		}
	}else{
		sAlert(map2.biaozhi);
	}
}
function submitSucc(response) {
	var value=response.responseText;
	var map2=Ext.decode(value);
	if(map2.succeed) {
		Rpc({functionId:'2020030184',success:submitSucc2},saveAnswer(flag,paper_id,r5300));
	}
}



function handlerNum(num) {
	if (num > 9) {
		return "" + num ;
	} else {
		return "0" + num ;
	}
}

/**
*时间到，强制交卷
**/
function forcSubmitPaper(flag,paper_id,r5300) {
	var map = saveAnswer(flag,paper_id,r5300);
	if (flag == '2') {
		map["paperState"] = "1";
	}
	Rpc({functionId:'2020030183',success:forcSucc},map);
}
function forcSucc (response) {
	var value=response.responseText;
	var map=Ext.decode(value);
	if (map.biaozhi == "ok") {
		if (map.flag == 2) {
			alert("考试时间到，系统已自动提交试卷!");
			testFinished = 1;
			reflashPare();
		} else {
			var form1 = document.getElementById("form1");
			form1.action = document.getElementById("tomodify").value;
			
			form1.submit();
		}
	} else {
		alert("答案保存失败！");
	}
}

function T(instr){
		var divObj = document.createElement("div");
    	divObj.innerHTML = instr;
		var outstr = divObj.innerText;
		return outstr;
	}

/**
*校验得分
**/	
function valueChange (obj,socre){
	var value = obj.value;
	if (isNaN(value)) {
		alert("得分只能为数字！");
		obj.value="0";
		return;
	}
	
	if (parseFloat(trimStr(value)) > parseFloat(socre)) {
		alert("所填分数不能大于本题分数！");
		obj.value="0";
		return;
	}
	
	if (parseFloat(trimStr(value)) < 0) {
		alert("所填分数不能小于0！");
		obj.value="0";
		return;
	}
	
	var totalScore = document.getElementById("totalScoreId");
	var inputObjs = document.getElementsByTagName("input");
	var total = 0;
	if (totalScore) {
		for (i = 0; i < inputObjs.length; i++) {
			var inputObj = inputObjs[i];
			var inputName = inputObj.name;
			if ("score_" == inputName.substring(0,6)){
				if (trimStr(inputObj.value).length > 0 && !isNaN(trimStr(inputObj.value))) {
					total += parseFloat(inputObj.value);
				}
					
			}
		}
		totalScore.innerHTML = "<font color='red' size='3'>" + total + "</font>";
		
	}
}

function returnToMyExam(url){
	var form1 = document.getElementById("form1");
	form1.action=url;
	form1.submit();
}



	// 屏蔽右键
	if (window.Event)    
		document.captureEvents(Event.MOUSEUP);    
	function nocontextmenu() {    
		event.cancelBubble = true;
		event.returnValue = false;
		return false;
	} 
	   
	function norightclick(e){    
		if (window.Event){
			if (e.which == 2 || e.which == 3)
				return false;
		} else if(event.button == 2 || event.button == 3){
				event.cancelBubble = true;
				event.returnValue = false;
				return false; 
		}    
	}    
	document.oncontextmenu = nocontextmenu; // for IE5+    
	document.onmousedown = norightclick; // for all others
	
	
	document.onkeydown=function() {    
		if ((window.event.keyCode==116)|| //屏蔽 F5    
				(window.event.keyCode==122)|| //屏蔽 F11    
				(window.event.shiftKey && window.event.keyCode==121)|| //shift+F10    
				(window.event.ctrlKey && window.event.keyCode==82)||// ctrl+R
				(window.event.keyCode==8 && window.event.srcElement.tagName !='INPUT' && window.event.srcElement.tagName !='TEXTAREA')//backspace
				) {   
			window.event.keyCode=0;    
			window.event.returnValue=false;    

     	}     

  		if ((window.event.altKey)&&(window.event.keyCode==115)) {     
         	//屏蔽Alt+F4    

         	window.showModelessDialog("about:blank","","dialogWidth:1px;dialogheight:1px");    

         	return false;    

     	}      
	}
	
	
	function sAlert(str,type,opt){
		this.returnValue = false;
		// type 1为普通提示框，2为选择提示框
	 	if (!type) {
	 		type = 1;
	 	} 

        var msgw,msgh; 
        msgw=300;// 提示窗口的宽度
        msgh=100;// 提示窗口的高度
        titleheight=16 // 提示窗口标题高度
         
        var sWidth,sHeight; 
        sWidth=document.body.clientWidth;// screen.width;
        sHeight=document.body.clientHeight + document.body.scrollTop;// screen.height;

        var bgObj;
        if (document.getElementById("bgDiv")) {
        	bgObj = document.getElementById("bgDiv");
        } else {
       		bgObj = document.createElement("div"); 
        }
        bgObj.setAttribute('id','bgDiv'); 
        bgObj.style.position="absolute"; 
        bgObj.style.top="0"; 
        bgObj.style.background="#cccccc"; 
        bgObj.style.filter="progid:DXImageTransform.Microsoft.Alpha(style=3,opacity=25,finishOpacity=75"; 
        bgObj.style.opacity="0.6"; 
        bgObj.style.left="0"; 
        bgObj.style.width=sWidth + "px"; 
        bgObj.style.height=sHeight + "px"; 
        bgObj.style.zIndex = "10000"; 
        document.body.appendChild(bgObj); 
         
        var msgObj; 
        if (document.getElementById("msgDiv")) {
        	msgObj = document.getElementById("msgDiv");
        	if (document.getElementById("bt1")) {
        		msgObj.removeChild(document.getElementById("bt1"));
        	}
        	if (document.getElementById("bt2")) {
        		msgObj.removeChild(document.getElementById("bt2"));
        	}
        } else {
       		msgObj = document.createElement("div"); 
        }
        msgObj.setAttribute("id","msgDiv"); 
        msgObj.setAttribute("align","left"); 
        msgObj.style.background="white";
        msgObj.className="complex_border_color";
        msgObj.style.position = "absolute"; 
        msgObj.style.left = "50%"; 
        msgObj.style.top = "50%"; 
        msgObj.style.font="12px/1.6em Verdana, Geneva, Arial, Helvetica, sans-serif"; 
        msgObj.style.marginLeft = "-150px" ; 
        msgObj.style.marginTop = -75+document.body.scrollTop+"px"; 
        msgObj.style.width = msgw + "px"; 
        msgObj.style.height = msgh + "px"; 
        msgObj.style.textAlign = "left"; 
        msgObj.style.lineHeight ="25px"; 
        msgObj.style.zIndex = "10001";

       var title;
        if (document.getElementById("msgTitle")) {
        	title = document.getElementById("msgTitle");
        } else {
       		title = document.createElement("h4"); 
        }
       title.setAttribute("id","msgTitle"); 
       title.setAttribute("align","right"); 
       title.style.margin="0"; 
       title.style.padding="2px"; 
       title.style.filter="progid:DXImageTransform.Microsoft.Alpha(startX=20, startY=20, finishX=100, finishY=100,style=1,opacity=75,finishOpacity=100);"; 
       title.style.opacity="0.75"; 
       title.className="divborder common_border_color common_background_color";
       title.style.height= titleheight + "px"; 
       title.style.font="12px Verdana, Geneva, Arial, Helvetica, sans-serif"; 
       title.style.color="white"; 
       title.style.cursor="pointer";
	   
	   var clo = document.createElement("img");
	    if (document.getElementById("closeimg")) {
        	clo = document.getElementById("closeimg");
        } else {
       		clo = document.createElement("img"); 
        }
         clo.setAttribute("id","closeimg"); 
		clo.src="/images/close2.gif";
       clo.border=0;
       clo.onclick=function(){ 
            document.body.removeChild(bgObj); 
            document.getElementById("msgDiv").removeChild(title); 
            document.body.removeChild(msgObj);
            if (document.getElementById("bt1")) {
            	document.body.removeChild(document.getElementById("bt1"));
            } 
            if (document.getElementById("bt2")) {
            	document.body.removeChild(document.getElementById("bt2"));
            } 
       } 
		title.appendChild(clo);
       document.body.appendChild(msgObj); 
      document.getElementById("msgDiv").appendChild(title); 
       var txt=document.createElement("div"); 
       if (document.getElementById("msgTxt")) {
        	txt = document.getElementById("msgTxt");
        } else {
       		txt = document.createElement("div"); 
        }
       txt.style.margin="1em 10" 
       txt.setAttribute("id","msgTxt"); 
       txt.innerHTML=str; 
       document.getElementById("msgDiv").appendChild(txt); 

	   if (type == 2 || type == 3) {
			var bt1,bt2;
			if (document.getElementById("bt1")) {
        		bt1 = document.getElementById("bt1");
			} else {
       			bt1 = document.createElement("button"); 
			}
			
			bt1.setAttribute("id","bt1"); 
			bt1.name="bt1"
			bt1.value="确定";
			bt1.className="myButton";
			bt1.style.marginLeft="103px";
			bt1.style.marginBottom="10px";
			bt1.onclick = function () {
			
			}
			if (document.getElementById("bt2")) {
        		bt2 = document.getElementById("bt2");
			} else {
       			bt2 = document.createElement("button"); 
			}
			bt1.onclick = function() {
				document.body.removeChild(bgObj); 
				document.getElementById("msgDiv").removeChild(title); 
				document.body.removeChild(msgObj);
				eval(opt);
			}
			msgObj.appendChild(bt1);
			if(type == 2) {
				bt2.value="取消";
				bt2.className="myButton";
				bt2.setAttribute("id","bt2"); 
				bt2.name="bt2"
				bt2.onclick=function(){ 
					document.body.removeChild(bgObj); 
					document.getElementById("msgDiv").removeChild(title); 
					document.body.removeChild(msgObj);
				}
				bt2.style.marginLeft="20px";
				bt2.style.marginBottom="10px";
				msgObj.appendChild(bt2);
			
			}

	   }
    } 
	function printer(obj){
	try{
	
		var RegWsh = new ActiveXObject("WScript.Shell");
		var hkey_key="header";
		var hkey_root="HKEY_CURRENT_USER";
		var hkey_path="\\Software\\Microsoft\\Internet Explorer\\PageSetup\\";
		RegWsh.RegWrite(hkey_root+hkey_path+hkey_key,"&w&b页码　&p/&P");
		hkey_key="footer";
          	RegWsh.RegWrite(hkey_root+hkey_path+hkey_key,"");
          	document.getElementById(obj).style.display='none';
    }catch(e){}
          
	}
	function exportWord(r5300,imgurl){
		var url = "/train/trainexam/paper/preview/paperspreview.do?br_select=link&r5300="+$URL.encode(r5300)+"&imgurl="+$URL.encode(imgurl);
		var outName = window.showModalDialog(url, 'mytree_win', 
			"dialogWidth:300px; dialogHeight:230px;resizable:no;center:yes;scroll:yes;status:no");
		
		if(outName) {
			window.location.target="_blank";
			window.location.href = "/servlet/vfsservlet?fromjavafolder=true&fileid="+outName;
		}
	} 
	
	function goPortal(tar) {
		if(tar=="hl")//6.0首页
			document.location="/templates/index/portal.do?b_query=link";
		else if(tar=="hcm")//7.0首页
			document.location="/templates/index/hcm_portal.do?b_query=link";
		
	}
	