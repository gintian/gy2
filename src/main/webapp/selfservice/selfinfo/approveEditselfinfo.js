function appEdite() {
	selfInfoForm.action = "/selfservice/selfinfo/appEditselfinfo.do?b_edit=link&isAppEdite=1";
	selfInfoForm.submit();
}
function appSave() {
	selfInfoForm.action = "/selfservice/selfinfo/editselfinfo.do?b_edit=edit&i9999=I9999&actiontype=update&setname=A01";
	selfInfoForm.target = "mil_body";
	selfInfoForm.submit();
}

function breturn() {
	selfInfoForm.action = "/selfservice/selfinfo/editselfinfo.do?b_return=link&isAppEdite=1";
	selfInfoForm.target = "mil_body";
	selfInfoForm.submit();
}

function appReturn() {
	selfInfoForm.action = "/selfservice/selfinfo/editselfinfo.do?b_edit=edit&i9999=I9999&actiontype=update&setname=A01";
	selfInfoForm.target = "mil_body";
	selfInfoForm.submit();
}

function setSelectCodeValue() {
	if (code_desc) {
		var vos = document.getElementsByName('dict_box');
		var dict_vo = vos[0];
		var isC = true;
		for (var i = 0; i < dict_vo.options.length; i++) {
			if (dict_vo.options[i].selected) {
				code_desc.value = dict_vo[i].text;
				var code_name = code_desc.name;
				if (code_name != "") {
					var code_viewname = code_name.substring(0, code_name.indexOf("."));
					var view_vos = document.getElementsByName(code_viewname	+ ".value");
					var view_vo = view_vos[0];
					if (dict_vo[i].value != null)
						view_vo.value = dict_vo[i].value.substring(2);
					view_vo.fireEvent("onchange");
				}
			}
		}
		Element.hide('dict');
		event.srcElement.releaseCapture();
	}
}
function inputType(obj, event) {
	var keyCode = event.keyCode ? event.keyCode : event.which ? event.which : event.charCode;
	if (keyCode == 13) {
		setSelectCodeValue();
	}

}
function inputType2(obj, event) {
	var keyCode = event.keyCode ? event.keyCode : event.which ? event.which : event.charCode;
	if (keyCode == 40) {
		var vos = document.getElementsByName('dict');
		var vos1 = vos[0];
		if (vos1.style.display != "none") {
			var vos = document.getElementsByName('dict_box');
			var dict_vo = vos[0];
			dict_vo.focus();
		}
	}
}
function styleDisplay(obj) {
	var obj_name = obj.name;
	if (code_desc) {
		var code_name = code_desc.name;
		if (code_name != obj_name) {
			Element.hide('dict');
		}
	}
}
function checkDict(code, obj) {
	var code_name = obj.name;
	var code_viewname = code_name.substring(0, code_name.indexOf("."));
	var view_vos = document.getElementsByName(code_viewname + ".value");
	var view_vo = view_vos[0];
	if (view_vo == null || view_vo == "") {
		obj.value = "";
		return false;
	}
	var isC = false;
	for (var i = 0; i < g_dm.length; i++) {
		dmobj = g_dm[i];
		if (dmobj.ID == (code + view_vo)) {
			isC = true;
			break;
		}
	}
	if (!isC) {
		obj.value = "";
		return false;
	}
}

function changeLowerLevel(curentId, childId) {// add by xiegh bug36384
	document.getElementById(childId).setAttribute("parentid", document.getElementsByName(curentId)[0].value);
}

function saves() {
	if (validate()) {
		selfInfoForm.action = "/selfservice/selfinfo/editselfinfo.do?b_saves=link&isAppEdite=1";
		selfInfoForm.submit();
	} else {
		return;
	}
}

function savesre() {
	if (validate()) {
		selfInfoForm.action = "/selfservice/selfinfo/editselfinfo.do?b_savere=link&isAppEdite=1";
		selfInfoForm.submit();
	} else {
		return;
	}
}
function diswrite(o) {

	o.disabled = "true";
}
function writeable() {
	var tablevos = document.getElementsByTagName("INPUT");
	if (validate()) {
		for (var i = 0; i < tablevos.length; i++) {
			tablevos[i].disabled = "";
		}
		selfInfoForm.action = "/selfservice/selfinfo/editselfinfo.do?b_save=link";
		selfInfoForm.submit();
	}
}

function writeabless() {
	var tablevos = document.getElementsByTagName("INPUT");
	if (validate()) {
		for (var i = 0; i < tablevos.length; i++) {
			tablevos[i].disabled = "";
		}

		var appSaves = document.getElementsByName("appSaves");
		if (appSaves)
			appSaves[0].disabled = true;

		selfInfoForm.action = "/selfservice/selfinfo/appEditselfinfo.do?b_saveself=link&isAppEdite=1";
		selfInfoForm.submit();
	}
}

function capp() {
	if (confirm(APPLIC_OK + '？')) {
		selfInfoForm.action = "/selfservice/selfinfo/editselfinfo.do?b_app=link";
		selfInfoForm.submit();
	} else {
		return;
	}
}

function handleEnter(field, event) {
	var keyCode = event.keyCode ? event.keyCode : event.which ? event.which : event.charCode;
	alert(keyCode);
	return true;
}

function proves() {
	if (validate()) {
		if (confirm(APP_DATA_NOT_UPDATE + "?")) {
			selfInfoForm.action = "/selfservice/selfinfo/editselfinfo.do?b_appeals=link&isAppEdite=1";
			selfInfoForm.submit();
		} else {
			return;
		}
	}
}

function CalculatePostAge(obj) {
	var hashvo = new ParameterSet();
	hashvo.setValue("postdatevalue", obj.value);
	var request = new Request({
		method : 'post',
		onSuccess : getPostAge,
		functionId : '02010001015'
	}, hashvo);
}

function CalculateWorkDate(obj) {
	var hashvo = new ParameterSet();
	hashvo.setValue("workdatevalue", obj.value);
	var request = new Request({
		method : 'post',
		onSuccess : getWorkAge,
		functionId : '02010001014'
	}, hashvo);
}

function prove() {
	if (validate()) {
		if (confirm(APP_DATA_NOT_UPDATE + "?")) {
			selfInfoForm.action = "/selfservice/selfinfo/editselfinfo.do?b_appeal=link&buttonvalue=0";
			selfInfoForm.submit();
		} else {
			return;
		}
	}
}

function calculatebirthday(obj) {
	var hashvo = new ParameterSet();
	obj.value = trim(obj.value.replace(/ /g, ""));
	hashvo.setValue("idcardvalue", obj.value);
	var request = new Request({
		method : 'post',
		onSuccess : getBirthdayAge,
		functionId : '02010001013'
	}, hashvo);
}

function checkIDCard() {
	var flag = true;
	var idcardflag = "";
	var idcarditem = document.getElementById("idcardflag");
	if (idcarditem != null && idcarditem != undefined)
		idcardflag = idcarditem.value;
	var idcard = document.getElementById("idcard");
	var idcardvalue = "";
	if (idcard != null && idcard != undefined){
		idcardvalue = idcard.value;
		idcardvalue = trim(idcardvalue.replace(/ /g, ""));
		idcard.value = idcardvalue;
	}

	var idcardDesc = document.getElementById("idcardDesc");
	var descValue = "身份证号";
	if (idcardDesc)
		descValue = idcardDesc.value;

	if (idcardflag == "true") {
		var flag = IdCardValidate(idcardvalue);
		if (!flag) {
			alert(descValue + "格式错误，请重新填写！");
			flag = false;
		}
	} else {
		if (idcardvalue != "") {
			var flag = IdCardValidate(idcardvalue);
			if (!flag) {
				alert(descValue + "格式错误，请重新填写！");
				flag = false;
			}
		}
	}
	return flag;
}

var Wi = [ 7, 9, 10, 5, 8, 4, 2, 1, 6, 3, 7, 9, 10, 5, 8, 4, 2, 1 ]; // 加权因子   
var ValideCode = [ 1, 0, 10, 9, 8, 7, 6, 5, 4, 3, 2 ]; // 身份证验证位值.10代表X   
function IdCardValidate(idCard) {
	idCard = trim(idCard.replace(/ /g, "")); //去掉字符串头尾空格                     
	if (idCard.length == 15) {
		return isValidityBrithBy15IdCard(idCard); //进行15位身份证的验证    
	} else if (idCard.length == 18) {
		var a_idCard = idCard.split(""); // 得到身份证数组   
		if (isValidityBrithBy18IdCard(idCard)
				&& isTrueValidateCodeBy18IdCard(a_idCard)) { //进行18位身份证的基本验证和第18位的验证
			return true;
		} else {
			return false;
		}
	} else {
		return false;
	}
}

/**  
 * 验证15位数身份证号码中的生日是否是有效生日  
 * @param idCard15 15位书身份证字符串  
 * @return  
 */
function isValidityBrithBy15IdCard(idCard15) {
	var year = idCard15.substring(6, 8);
	var month = idCard15.substring(8, 10);
	var day = idCard15.substring(10, 12);
	var temp_date = new Date(year, parseFloat(month) - 1, parseFloat(day));
	// 对于老身份证中的年龄则不需考虑千年虫问题而使用getYear()方法   
	if (temp_date.getYear() != parseFloat(year)
			|| temp_date.getMonth() != parseFloat(month) - 1
			|| temp_date.getDate() != parseFloat(day)) {
		return false;
	} else {
		return true;
	}
}
/**  
 * 验证18位数身份证号码中的生日是否是有效生日  
 * @param idCard 18位书身份证字符串  
 * @return  
 */
function isValidityBrithBy18IdCard(idCard18) {
	var year = idCard18.substring(6, 10);
	var month = idCard18.substring(10, 12);
	var day = idCard18.substring(12, 14);
	var temp_date = new Date(year, parseFloat(month) - 1, parseFloat(day));
	// 这里用getFullYear()获取年份，避免千年虫问题   
	if (temp_date.getFullYear() != parseFloat(year)
			|| temp_date.getMonth() != parseFloat(month) - 1
			|| temp_date.getDate() != parseFloat(day)) {
		return false;
	} else {
		return true;
	}
}
/**  
 * 判断身份证号码为18位时最后的验证位是否正确  
 * @param a_idCard 身份证号码数组  
 * @return  
 */
function isTrueValidateCodeBy18IdCard(a_idCard) {
	var sum = 0; // 声明加权求和变量   
	if (a_idCard[17].toLowerCase() == 'x') {
		a_idCard[17] = 10; // 将最后位为x的验证码替换为10方便后续操作   
	}
	for (var i = 0; i < 17; i++) {
		sum += Wi[i] * a_idCard[i]; // 加权求和   
	}
	valCodePosition = sum % 11; // 得到验证码所位置   
	if (a_idCard[17] == ValideCode[valCodePosition]) {
		return true;
	} else {
		return false;
	}
} 	

function change() {
	var list = document.getElementById("list");
	var flag = false;
	flag = dataIsChange();
	va = list.value;
	if (va == "A01") {
		selfInfoForm.action = "/selfservice/selfinfo/appEditselfinfo.do?b_defend=link&isAppEdite=1";
	} else if (va == "A00") {
		selfInfoForm.action = "/workbench/media/searchmediainfolist.do?b_appsearch=link&setname=A00&setprv=2&flag=self&returnvalue=3&isUserEmploy=0&button=0";
	} else {
		if (flag) {
			var flag = confirm("信息已修改请保存数据，否则可能会导致数据丢失。确认跳转？");
			if (flag)
				selfInfoForm.action = "/selfservice/selfinfo/appEditselfinfo.do?b_defendother=search&setname="
						+ va + "&flag=infoself&isAppEdite=1"
			else {
				document.getElementById('list').selectedIndex = 0;
				return;
			}
		} else {
			selfInfoForm.action = "/selfservice/selfinfo/appEditselfinfo.do?b_defendother=search&setname="
					+ va + "&flag=infoself&isAppEdite=1"
		}
	}
	selfInfoForm.target = "mil_body";
	selfInfoForm.submit();
}

function checkValue(obj, itemlength, decimalwidth, desc) {
	checkNUM2(obj, itemlength, decimalwidth, desc);
}
function changinfor() {
	selfInfoForm.action = "/selfservice/selfinfo/inforchange.do?b_query=link";
	selfInfoForm.submit();
}
function IsDigit() {
	return event.keyCode == 46;
}

function checkValue(obj, itemlength, decimalwidth, desc) {
	checkNUM2(obj, itemlength, decimalwidth, desc);
}
function changinfor() {
	selfInfoForm.action = "/selfservice/selfinfo/inforchange.do?b_query=link";
	selfInfoForm.submit();
}
function IsDigit() {
	return event.keyCode == 46;
}
function setSelectValue() {
	if (date_desc) {
		date_desc.value = $F('date_box');
		Element.hide('date_panel');
		event.srcElement.releaseCapture();
	}
}
function showDateSelectBox(srcobj) {
	date_desc = srcobj;
	Element.show('date_panel');
	var pos = getAbsPosition(srcobj);
	with ($('date_panel')) {
		style.position = "absolute";
		style.posLeft = pos[0] - 1;
		style.posTop = pos[1] - 1 + srcobj.offsetHeight;
		style.width = (srcobj.offsetWidth < 150) ? 150 : srcobj.offsetWidth + 1;
	}
}

function initDate() {
	var dropDownDate = createDropDown("dropDownDate");
	var __t = dropDownDate;
	__t.type = "date";
	__t.tag = "";
	_array_dropdown[_array_dropdown.length] = __t;
	initDropDown(__t);

	var dropDownList = createDropDown("dropDownList");
	var __t = dropDownList;
	__t.type = "list";
	__t.tag = "";
	_array_dropdown[_array_dropdown.length] = __t;
	initDropDown(__t);
}
