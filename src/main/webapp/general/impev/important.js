function trim(str){ //删除左右两端的空格
　　return str.replace(/(^\s*)|(\s*$)/g, "");
}
function saveImpev() {
	var p0600 = $F("p0600");
	oEditor = FCKeditorAPI.GetInstance("FCKeditor1");
	if (oEditor.EditorDocument == null) {
		alert("\u7f16\u8f91\u5668\u6b63\u5728\u52a0\u8f7d\u4e2d\uff01\u8bf7\u91cd\u65b0\u70b9\u51fb\u6309\u94ae!");
		return;
	}
	document.getElementsByName("content")[0].value = oEditor.GetXHTML(true);
	importantEvForm.action = "/general/impev/importantev.do?b_save=link&p0600=" + p0600;
	importantEvForm.submit();	
}
function saveandsubmit() {
	var p0600 = $F("p0600");
	oEditor = FCKeditorAPI.GetInstance("FCKeditor1");
	if (oEditor.EditorDocument == null) {
		alert("\u7f16\u8f91\u5668\u6b63\u5728\u52a0\u8f7d\u4e2d\uff01\u8bf7\u91cd\u65b0\u70b9\u51fb\u6309\u94ae!");
		return;
	}
	document.getElementsByName("content")[0].value = oEditor.GetXHTML(true);
	if($F("content")!=null&&trim($F("content")).length>0){
		importantEvForm.action = "/general/impev/importantev.do?b_save=link&p0609=1&p0600=" + p0600;
		importantEvForm.submit();
	}else{
		alert("很抱歉，您不能提交空的重要信息报告！！！");
	}
}
function back() {
	importantEvForm.action = "/general/impev/importantev.do?b_query=link";
	importantEvForm.submit();
}
function back1() {
	//impEvCommentForm.action = "/general/impev/importantev.do?b_query=link";
	//impEvCommentForm.submit();
	history.go(-1);
}
function back2(flag,a_code) {
	if(flag=="1"){
		impEvCommentForm.action = "/performance/workdiary/workdiaryshow.do?b_query=link&a_code="+a_code;
		impEvCommentForm.submit();
	}else{
		impEvCommentForm.action = "/general/impev/importantev.do?b_search=link&a_code="+a_code;
		impEvCommentForm.submit();
	}
}
function deliver(flag) {
	if ($F("content") != null && trim($F("content")).length > 0) {
		if(flag=="1"){
			impEvCommentForm.action = "/general/impev/importantevcomment.do?b_evwork=link&flag="+flag+"&a_code=${impEvCommentForm.a_code}";
			impEvCommentForm.submit();
		}else{
			impEvCommentForm.action = "/general/impev/importantevcomment.do?b_deliver=link&flag="+flag+"&a_code=${impEvCommentForm.a_code}";
			impEvCommentForm.submit();
		}
	} else {
		alert("\u5f88\u62b1\u6b49\uff0c\u60a8\u4e0d\u80fd\u53d1\u8868\u7a7a\u8bc4\u8bba\uff01\uff01\uff01");
	}
}

