/**
 *liweichao
 */

//新增奖品
function exchangeadd(r5701) {
	exchangeForm.action = "/train/exchange/exchangemanage.do?b_add=link&r5701="+r5701;
	exchangeForm.submit();
}
//保存奖品
function exchangesave() {
	exchangeForm.action = "/train/exchange/exchangemanage.do?b_save=link";
	exchangeForm.submit();
}
//保存&继续
function exchangesavecontinue() {
	exchangeForm.action = "/train/exchange/exchangemanage.do?b_savecontinue=link";
	exchangeForm.submit();
}
//删除奖品
function exchangedelete() {
	var sel = "";
	var sels = document.getElementsByName("r5701");
	for (var i = 0; i < sels.length; i++) {
		if (sels[i].checked) {
			sel += sels[i].value + ",";
		}
	}
	if (sel != "" && sel.length > 0) {
		if (sel != null && confirm("\u786e\u8ba4\u8981\u5220\u9664\u5417\uff1f")) {//确认要删除吗？
			var hashvo = new ParameterSet();
			hashvo.setValue("sel", sel.substring(0, sel.length - 1));
			var request = new Request({method:"post", asynchronous:false, onSuccess:exchange, functionId:"2020082004"}, hashvo);
		}
	} else {
		alert("\u8bf7\u9009\u62e9\u8981\u5220\u9664\u7684\u8bb0\u5f55\uff01");//请选择要删除的记录！
		return null;
	}
}
//修改奖品状态  发布 暂停 批量发布 批量暂停
function exchangestatus(r5713) {
	var sel = "";
	var sels = document.getElementsByName("r5701");
	var fb = document.getElementsByName("names");
	for (var i = 0; i < sels.length; i++) {
		if (sels[i].checked) {
			if("04" == r5713){//如果是发布
				if(fb[i].value == "04"){
					alert("该奖品已发布,不能重复发布!");
					return null;
				}else{
					sel += sels[i].value + ",";
				}
			}else if("09" == r5713){
				if(fb[i].value != "04"){//如果是暂停
					alert("请选择已发布的奖品进行暂停!");
					return null;
				}else{					
					sel += sels[i].value + ",";
				}
			}
		}
	}
	if("09" == r5713){		
		if (sel != "" && sel.length > 0) {
			if (sel != null && confirm("确认要暂停吗?")) {//暂停操作
				var hashvo = new ParameterSet();
				hashvo.setValue("sel", sel.substring(0, sel.length - 1));
				hashvo.setValue("r5713",r5713);
				var request = new Request({method:"post", asynchronous:false, onSuccess:exchange, functionId:"2020082005"}, hashvo);
			}
		} else {
			alert("请选择要暂停的奖品!");//请选择要删除的记录！
			return null;
		}
	}else if("04" == r5713){
		if (sel != "" && sel.length > 0) {
			if (sel != null && confirm("确认要发布吗?")) {//发布操作
				var hashvo = new ParameterSet();
				hashvo.setValue("r5713",r5713);
				hashvo.setValue("sel", sel.substring(0, sel.length - 1));
				var request = new Request({method:"post", asynchronous:false, onSuccess:exchange, functionId:"2020082005"}, hashvo);
			}
		} else {
			alert("请选择要发布的奖品!");//请选择要删除的记录！
			return null;
		}
	}
	//exchangeForm.action = "/train/exchange/exchangemanage.do?b_status=link&r5701=" + r5701 + "&status=" + r5713;
	//exchangeForm.submit();
}
//奖品管理
function exchange() {
	exchangeForm.action = "/train/exchange/exchangemanage.do?b_query=link";
	exchangeForm.submit();
}
//返回
function exchange1(r5713) {
	exchangeForm.action = "/train/exchange/exchangemanage.do?b_query=link&r5713=" + r5713;
	exchangeForm.submit();
}
//奖品兑换详情
function exchangeinfo(r5701, r5703) {
	exchangeForm.action = "/train/exchange/exchangeinfo.do?b_query=link&r5701=" + r5701 + "&r5703=" + getEncodeStr(r5703);
	exchangeForm.submit();
}
//奖品兑换记录
function record() {
	exchangeForm.action = "/train/exchange/exchangerecord.do?b_query=link";
	exchangeForm.submit();
}
//Excel导入奖品
function excelimport() {
	window.location.href = "/train/exchange/exchangeimport.jsp";
}
//生成Excel导入奖品模版
function excelTemplate() {
	var request = new Request({method:"post", asynchronous:false, onSuccess:showExportInfo, functionId:"2020082006"}, null);
}
function showExportInfo(outparamters) {
	if (outparamters) {
		var name = outparamters.getValue("filename");
		if (name) {
			window.location.target = "_blank";
			window.location.href = "/servlet/vfsservlet?fromjavafolder=true&fileid=" + name;
		}
	}
}
function outContent(r5701,field){
	var hashvo=new ParameterSet();
	hashvo.setValue("r5701",r5701);
	hashvo.setValue("field",field);
	var request=new Request({method:'post',asynchronous:false,onSuccess:viewContent,functionId:'2020082009'},hashvo);
}
function viewContent(outparamters){
	if(outparamters){
		var content=outparamters.getValue("content");
		config.FontSize='10pt';//hint提示信息中的字体大小
		Tip(getDecodeStr(content),STICKY,true);
	}
}
//奖品兑换
function integral() {
	exchangeForm.action = "/train/exchange/exchangeintegral.do?b_query=link&counts=0";
	exchangeForm.submit();
}
//div居中显示
 function doThis(){
	 var a = document.getElementById("wait");
	 a.style.left=(document.body.clientWidth/2-a.clientWidth/2)+"px";
	 a.style.top=(document.body.scrollTop+document.body.clientHeight/2-a.clientHeight/2)+"px";
}
//奖品兑换-添加
var counts = "1";
function integraladd(r5701,r5707) {
	var hashvo = new ParameterSet();
	
	//hashvo.setValue("counts",counts);
	hashvo.setValue("r5701", r5701);
	var request = new Request({method:"post", asynchronous:false, onSuccess:integraladdinfo, functionId:"2020082012"}, hashvo);
	//div();
	doThis();
	exchangeForm.action = "/train/exchange/exchangeintegral.do?b_query=link&counts="+counts;
	exchangeForm.submit();
}
//奖品兑换-批量添加
	var count = 0 ;
function integralbatch() {
	var sel = "";

	var sels = document.getElementsByName("r5701");
	for (var i = 0; i < sels.length; i++) {
		if (sels[i].checked) {
			sel += sels[i].value + ",";
			count++;
		}
	}
	if (sel != "" && sel.length > 0) {
		var hashvo = new ParameterSet();
		hashvo.setValue("counts",count);
		hashvo.setValue("r5701", sel.substring(0, sel.length - 1));
		var request = new Request({method:"post", asynchronous:false, onSuccess:integraladdinfo, functionId:"2020082012"}, hashvo);
		//div();
		exchangeForm.action = "/train/exchange/exchangeintegral.do?b_query=link&counts="+count;
		exchangeForm.submit();
	} else {
		alert("\u8BF7\u9009\u62E9\u8981\u6DFB\u52A0\u7684\u5956\u54C1\uFF01");//请选择要添加的奖品！
		return null;
	}
}
function integraladdinfo(outparamters){
	var msg = outparamters.getValue("msg");
	if(msg != null && msg != "undefind"){		
		alert(msg);
		counts = 0;
		count = 0;
		//document.getElementById("wait").style.display="none"; 
	}
	//if(outparamters){
	//var counts = "";
	//counts = outparamters.getValue("counts");
	//	if("ok"==outparamters.getValue("mess"))
	//		alert("\u6DFB\u52A0\u6210\u529F\uFF01");//添加成功！	
	//}	
}
//奖品兑换-结算
function account() {
	exchangeForm.action = "/train/exchange/exchangeintegral.do?b_account=link";
	exchangeForm.submit();
}
//奖品兑换-编辑
function integraledit(flag,id,r5701,count) {
	if(flag=="del"&&!window.confirm("\u786e\u8ba4\u8981\u5220\u9664\u5417\uff1f"))//确认要删除吗？
		return
	
	if(flag=="change"&&(count==""||count<0))
		return;
	exchangeForm.action = "/train/exchange/exchangeintegral.do?b_edit=link&flag="+flag+"&id="+id+"&r5701="+r5701+"&count="+count;
	exchangeForm.submit();
}
//奖品兑换-提交订单
function integralsubmit() {
	if(window.confirm("\u786E\u8BA4\u8981\u5151\u6362\u5217\u8868\u4E2D\u7684\u5956\u54C1\u5417\uFF1F"))//确认要兑换列表中的奖品吗？
	var request = new Request({method:"post", asynchronous:false, onSuccess:submitinfo, functionId:"2020082015"}, null);
}
function submitinfo(outparamters){
	if(outparamters){
		if("ok"==outparamters.getValue("mess")){
			alert("\u5151\u6362\u6210\u529F\uFF01");//兑换成功！
			integral();
		}else{
			alert(getDecodeStr(outparamters.getValue("mess")));
		}
	}
}
//输入浮点数值型
function IsDigit(obj) {
	if ((event.keyCode >= 46) && (event.keyCode <= 57) && event.keyCode != 47) {
		var values = obj.value;
		if ((event.keyCode == 46) && (values.indexOf(".") != -1)) {//有两个.
			return false;
		}
		if ((event.keyCode == 46) && (values.length == 0)) {//首位是.
			return false;
		}
		return true;
	}
	return false;
}
function validatefilepath(filename) {
	var mediapath = document.getElementsByName(filename)[0].value;
	if (mediapath.length < 3) {
		alert("\u8bf7\u9009\u62e9\u8981\u4e0a\u4f20\u7684Excel\u6587\u4ef6\uff01");//请选择要上传的Excel文件！
		return false;
	} else {
		if (validateUploadFilePath(mediapath)) {
			if (mediapath.indexOf(".") == -1 || (mediapath.substring(mediapath.indexOf(".")).toLowerCase() != ".xls" && mediapath.substring(mediapath.indexOf(".")).toLowerCase() != ".xlsx")) {
				alert("\u8bf7\u9009\u62e9Excel\u6587\u4ef6\u8fdb\u884c\u4e0a\u4f20\uff01");//请选择Excel文件进行上传！
				return false;
			}
		}
	}
	return true;
}
	//输入整数
function IsDigit2(obj) {
	if ((event.keyCode > 47) && (event.keyCode <= 57)) {
		return true;
	} else {
		return false;
	}
}
function isNumber(obj) {
	var checkOK = "-0123456789.";
	var checkStr = obj.value;
	var allValid = true;
	var decPoints = 0;
	var allNum = "";
	if (checkStr == "") {
		return;
	}
	var count = 0;
	var theIndex = 0;
	for (i = 0; i < checkStr.length; i++) {
		ch = checkStr.charAt(i);
		if (ch == "-") {
			count = count + 1;
			theIndex = i + 1;
		}
		for (j = 0; j < checkOK.length; j++) {
			if (ch == checkOK.charAt(j)) {
				break;
			}
		}
		if (j == checkOK.length) {
			allValid = false;
			break;
		}
		if (ch == ".") {
			allNum += ".";
			decPoints++;
		} else {
			if (ch != ",") {
				allNum += ch;
			}
		}
	}
	if (count > 1 || (count == 1 && theIndex > 1)) {
		allValid = false;
	}
	if (decPoints > 1 || !allValid) {
		alert("\u8bf7\u8f93\u5165\u6570\u503c\u7c7b\u578b\u7684\u503c\uff01");//请输入数值类型的值！
		obj.value = "";
		obj.focus();
	}
}

