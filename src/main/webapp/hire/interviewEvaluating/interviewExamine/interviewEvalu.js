
function delEval(a0100) {
	var i9999s = "";
	var tablevos = document.getElementsByTagName("input");
	for (var i = 0; i < tablevos.length; i++) {
		if (tablevos[i].type == "checkbox" && tablevos[i].checked == true && tablevos[i].name != "selbox") {
			var theVal = tablevos[i].value;
			i9999s += "@" + theVal;
		}
	}
	if (i9999s.length == 0) {
		alert(CHOISE_DELETE_NOT);
		return;
	} else {
		if (confirm(CONFIRMATION_DEL)) {
			interviewExamineForm.action = "/hire/interviewEvaluating/interviewExamine.do?b_delEvalu=link&i9999s=" + i9999s.substring(1) + "&a0100=" + a0100;
			interviewExamineForm.submit();
		}
	}
}
function editEval(userFld, dateFld, a0100, dbname, subset, i9999) {
	var setname = dbname + subset;
	var theurl = "/general/inform/empsubset_add.do?b_query=link`a0100=" + a0100 + "`dbname=" + dbname + "`subset=" + subset + "`i9999=" + i9999 + "`userFld=" + userFld + "`dateFld=" + dateFld + "`oper=zp`readonlyFilds="+userFld+","+dateFld;
	var iframe_url = "/general/query/common/iframe_query.jsp?src=" + theurl;
	var returnVo = window.showModalDialog(iframe_url, "empsubset_win", "dialogWidth:600px; dialogHeight:400px;resizable:no;center:yes;scroll:yes;status:no");
	if (returnVo) {
	  var obj= new Object();
	  obj.flag=returnVo.flag;
    	if (obj.flag == "true") {
    		window.setTimeout("fresh('"+a0100+"')",300); 
    	}
	}
}
////为了防止刷新没数据做的尝试 zzk
function fresh(a0100){
    interviewExamineForm.action = "/hire/interviewEvaluating/interviewExamine.do?b_evalu=link&a0100=" + a0100;
   	interviewExamineForm.submit();
}
