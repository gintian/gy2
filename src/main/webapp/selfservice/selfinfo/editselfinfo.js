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

function checkIDCard() {
	var flag = true;
	var idcardflag = "";
	var idcarditem = document.getElementById("idcardflag");
	if (idcarditem != null && idcarditem != undefined)
		idcardflag = idcarditem.value;

	var idcard = document.getElementById("idcard");
	var idcardvalue = "";
	if (idcard != null && idcard != undefined)
		idcardvalue = idcard.value;

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
	idCard = trim(idCard.replace(/ /g, "")); // 去掉字符串头尾空格
	if (idCard.length == 15) {
		return isValidityBrithBy15IdCard(idCard); // 进行15位身份证的验证
	} else if (idCard.length == 18) {
		var a_idCard = idCard.split(""); // 得到身份证数组
		if (isValidityBrithBy18IdCard(idCard)
				&& isTrueValidateCodeBy18IdCard(a_idCard)) { // 进行18位身份证的基本验证和第18位的验证
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
 * 
 * @param idCard15
 *            15位书身份证字符串
 * @return
 */
function isValidityBrithBy15IdCard(idCard15) {
	var reg = /^\d{15}$/;
	var year = idCard15.substring(6, 8);
	var month = idCard15.substring(8, 10);
	var day = idCard15.substring(10, 12);
	var temp_date = new Date(year, parseFloat(month) - 1, parseFloat(day));
	// 对于老身份证中的年龄则不需考虑千年虫问题而使用getYear()方法
	if (!reg.test(idCard15)||temp_date.getYear() != parseFloat(year)
			|| temp_date.getMonth() != parseFloat(month) - 1
			|| temp_date.getDate() != parseFloat(day)) {
		return false;
	} else {
		return true;
	}
}
/**
 * 验证18位数身份证号码中的生日是否是有效生日
 * 
 * @param idCard
 *            18位书身份证字符串
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
 * 
 * @param a_idCard
 *            身份证号码数组
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

function exeButtonAction(actionStr, target_str) {
	target_url = actionStr;
	window.open(target_url, target_str);
}

function checkValue(obj, itemlength, decimalwidth) {
	if (decimalwidth == '')
		return true;
	if (itemlength == '')
		return true;
	var t_len = obj.value;
	if (t_len != "") {
		var decimalw = parseInt(decimalwidth, 10);
		var itemlen = parseInt(itemlength, 10);
		var inde = t_len.indexOf(".");
		if (inde == -1) {
			if (t_len.length > itemlen) {
				alert(INTEGER_LENGTH_SET + itemlen + "," + PLEASE_UPDATE + "！");
				obj.focus();
				return false;
			}
		} else {
			var q_srt = t_len.substring(0, inde);
			var n_srt = t_len.substring(inde + 1);
			if (q_srt.length > itemlen) {
				alert(INTEGER_LENGTH_SET + itemlen + "," + PLEASE_UPDATE + "！");
				obj.focus();
				return false;
			} else if (n_srt.length > decimalw) {
				alert(DECIMAL_LENGTH_SET + decimalw + "," + PLEASE_UPDATE + "！");
				obj.focus();
				return false;
			}
		}
	}
}
function changinfor() {
	selfInfoForm.action = "/selfservice/selfinfo/inforchange.do?b_query=link";
	selfInfoForm.submit();
}
function IsDigit() {
	return event.keyCode == 46;
}
function change() {
	var list = document.getElementById("list");
	va = list.value;
	if (va == "A01") {
		selfInfoForm.action = "/selfservice/selfinfo/editselfinfo.do?b_edit=edit&i9999=I9999&actiontype=update&setname=A01&isAppEdite=1";
	} else {
		selfInfoForm.action = "/selfservice/selfinfo/searchselfdetailinfo.do?b_search=search&setname="
				+ va + "&flag=infoself&isAppEdite=1"
	}
	selfInfoForm.target = "mil_body";
	selfInfoForm.submit();
}

function checkLength(itemdesc, object, length) {
	var value = "";
	if (object)
		value = object.value;

	if (IsOverStrLength(value, length)) {
		var msg = ITEMVALUE_MORE_LENGTH;
		msg = msg.replace("{0}", length).replace("{1}", parseInt(length / 2));
		alert(itemdesc + msg);
		return;
	}
}

function prove() {
	if (!checkIDCard())
		return false;

	if (validate()) {
		if (confirm(APP_DATA_NOT_UPDATE + "?")) {
			selfInfoForm.action = "/selfservice/selfinfo/editselfinfo.do?b_appeal=link&buttonvalue=0";
			selfInfoForm.submit();
		} else {
			return;
		}
	}
}

function savesre() {
	if (!checkIDCard())
		return false;

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
	if (!checkIDCard())
		return false;

	var tablevos = document.getElementsByTagName("INPUT");
	if (validate()) {
		for (var i = 0; i < tablevos.length; i++) {
			tablevos[i].disabled = "";
		}
		selfInfoForm.action = "/selfservice/selfinfo/editselfinfo.do?b_save=link";
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
	var keyCode = event.keyCode ? event.keyCode : event.which ? event.which
			: event.charCode;
	return true;
}
function openappealpage() {
	var theurl = "/selfservice/selfinfo/showpersoninfo.do?b_query=link&a01001=${selfInfoForm.a0100}&pdbflag1=${selfInfoForm.userbase}&setprv=2";
	var retvalue = window
			.showModalDialog(
					theurl,
					true,
					"dialogWidth:800px; dialogHeight:1000px;resizable:no;center:yes;scroll:yes;status:no");
	if (retvalue != null) {
		window.parent.location.reload();
	}
}

function appealok() {
	alert(INFOR_APP_OK + '！');
	selfInfoForm.action = "/selfservice/selfinfo/editselfinfo.do?b_edit=edit&i9999=I9999&actiontype=update&setname=A01"
	selfInfoForm.submit();

}
function appeal(outparamters) {

	var cset = outparamters.getValue("cset");
	var citem = outparamters.getValue("citem");
	if (citem.length > 0) {
		alert(citem);
		window.close();
	} else {
		if (cset.length > 0) {
			if (confirm(cset)) {
				var pars = "a0100=${selfInfoForm.a0100}&pdbflag=${selfInfoForm.pdbflag}";
				var request = new Request({
					method : 'post',
					asynchronous : false,
					parameters : pars,
					onSuccess : appealok,
					functionId : '0201001098'
				});

			} else {
				window.close();
			}
		} else {
			var pars = "a0100=${selfInfoForm.a0100}&pdbflag=${selfInfoForm.pdbflag}";
			var request = new Request({
				method : 'post',
				asynchronous : false,
				parameters : pars,
				onSuccess : appealok,
				functionId : '0201001098'
			});
		}

	}
}

function getfirstfocuse() {
	var objsss = document.getElementsByTagName("input");

	for (var i = 0; i < objsss.length; i++) {
		var dobj = objsss[i];
		if (dobj.type == "text" && dobj.getAttribute("extra") == null) {
			dobj.focus();
			return;
		}
	}
}

function addDict(code, itemid, obj) {
	Element.hide('dict');
	var value = obj.value;
	if (value == "")
		return false;
	var dmobj;
	var vos = document.getElementsByName('dict_box');
	var dict_vo = vos[0];
	var isC = true;
	code_desc = obj;
	for (var i = dict_vo.options.length - 1; i >= 0; i--) {
		dict_vo.options.remove(i);
	}
	var no = new Option();
	no.value = "";
	no.text = "";
	dict_vo.options[0] = no;
	var r = 1;
	var vos;
	if (itemid.toLowerCase() == "e01a1" || itemid.toLowerCase() == "e0122")
		if (code == "UM")
			vos = document.getElementById('b0110');
		else if (code == "@K") {
			vos = document.getElementById('e0122');
			if (vos.value == '') {
				vos = document.getElementById('b0110');
			}
		}
	var code_value = "<%=codevalue%>";
	for (var i = 0; i < g_dm.length; i++) {
		dmobj = g_dm[i];
		if (code == "UM" || code == "@K" || code == "UN") {
			if (vos) {
				var b_value = vos.value;
				if (b_value == null || b_value == "") {
					b_value = ""
				}
				if ((code_value != "" || '<%=isAll %>' == 'all')
						&& code_value.length <= dmobj.ID.substring(2).length) {

					if ((dmobj.V.indexOf(value) != -1
							&& dmobj.ID.indexOf(code + code_value) == 0 && dmobj.ID
							.indexOf(code + b_value) == 0)
							|| (dmobj.ID.indexOf(code + value) == 0 && dmobj.ID
									.indexOf(code + b_value) == 0)) {
						if (dmobj.ID.substring(2).indexOf(code_value) == 0) {
							var hashvo = new ParameterSet();
							hashvo.setValue("a_code", dmobj.ID);
							var request = new Request({
								method : 'post',
								onSuccess : getBirthdayAge,
								functionId : '10200770001'
							}, hashvo);
							function getBirthdayAge(outparamters) {
								var parentdesc = outparamters
										.getValue("parentdesc");
								var no = new Option();
								no.value = dmobj.ID;
								no.text = (parentdesc.length > 0 ? parentdesc
										+ "/" : "")
										+ dmobj.V;
								dict_vo.options[r] = no;
								r++;
							}
						}
					}
				} else {
					if (code_value.length > 0
							&& ((dmobj.V.indexOf(value) != -1 && dmobj.ID
									.indexOf(code) == 0) || (dmobj.ID
									.indexOf(code + value) == 0))) {
						if (dmobj.ID.substring(2).indexOf(code_value) == 0) {
							var hashvo = new ParameterSet();
							hashvo.setValue("a_code", dmobj.ID);
							var request = new Request({
								method : 'post',
								onSuccess : getBirthdayAge,
								functionId : '10200770001'
							}, hashvo);
							function getBirthdayAge(outparamters) {
								var parentdesc = outparamters
										.getValue("parentdesc");
								var no = new Option();
								no.value = dmobj.ID;
								no.text = (parentdesc.length > 0 ? parentdesc
										+ "/" : "")
										+ dmobj.V;
								dict_vo.options[r] = no;
								r++;
							}
						}
					}
				}

			} else {
				if (code_value != ""
						&& code_value.length >= dmobj.ID.substring(2).length) {
					if ((dmobj.V.indexOf(value) != -1
							&& dmobj.ID.indexOf(code) == 0 && dmobj.ID
							.indexOf(code + b_value) == 0)
							|| (dmobj.ID.indexOf(code + value) == 0 && dmobj.ID
									.indexOf(code + b_value) == 0)) {
						if (code_value.indexOf(dmobj.ID.substring(2)) == 0) {
							var hashvo = new ParameterSet();
							hashvo.setValue("a_code", dmobj.ID);
							var request = new Request({
								method : 'post',
								onSuccess : getBirthdayAge,
								functionId : '10200770001'
							}, hashvo);
							function getBirthdayAge(outparamters) {
								var parentdesc = outparamters
										.getValue("parentdesc");
								var no = new Option();
								no.value = dmobj.ID;
								no.text = (parentdesc.length > 0 ? parentdesc
										+ "/" : "")
										+ dmobj.V;
								dict_vo.options[r] = no;
								r++;
							}
						}
					}
				} else {
					if ((dmobj.V.indexOf(value) != -1
							&& dmobj.ID.indexOf(code) == 0 && dmobj.ID
							.indexOf(code + b_value) == 0)
							|| (dmobj.ID.indexOf(code + value) == 0 && dmobj.ID
									.indexOf(code + b_value) == 0)) {
						var hashvo = new ParameterSet();
						hashvo.setValue("a_code", dmobj.ID);
						var request = new Request({
							method : 'post',
							onSuccess : getBirthdayAge,
							functionId : '10200770001'
						}, hashvo);
						function getBirthdayAge(outparamters) {
							var parentdesc = outparamters
									.getValue("parentdesc");
							var no = new Option();
							no.value = dmobj.ID;
							no.text = (parentdesc.length > 0 ? parentdesc + "/"
									: "")
									+ dmobj.V;
							dict_vo.options[r] = no;
							r++;
						}
					}
				}
			}

		} else {
			if ((dmobj.V.indexOf(value) != -1 && dmobj.ID.indexOf(code) == 0)
					|| (dmobj.ID.indexOf(code + value) == 0)) {

				var no = new Option();
				no.value = dmobj.ID;
				no.text = dmobj.V;
				dict_vo.options[r] = no;
				r++;
			}
		}
	}
	if (r == 1) {
		obj.value = "";
		Element.hide('dict');
		return false;
	}
	Element.show('dict');
	var pos = getAbsPosition(obj);
	with ($('dict')) {
		style.position = "absolute";
		style.posLeft = pos[0] - 1;
		style.posTop = pos[1] - 1 + obj.offsetHeight;
		style.width = (obj.offsetWidth < 150) ? 150 : obj.offsetWidth + 1;
	}
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
					var code_viewname = code_name.substring(0, code_name
							.indexOf("."));
					var view_vos = document.getElementsByName(code_viewname
							+ ".value");
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
	var keyCode = event.keyCode ? event.keyCode : event.which ? event.which
			: event.charCode;
	if (keyCode == 13) {
		setSelectCodeValue();
	}

}
function inputType2(obj, event) {
	var keyCode = event.keyCode ? event.keyCode : event.which ? event.which
			: event.charCode;
	if (keyCode == 40) {
		var vos = document.getElementsByName('dict');
		var vos1 = vos[0];
		if (vos1.style.display != "none") {
			var vos = document.getElementsByName('dict_box');
			var dict_vo = vos[0];
			dict_vo.focus();
		}
	}
	fieldcode(obj, 2);// tianye add 调用修改隐藏域内容 不知道为什么 没有执行onchange内的fieldcode()
	// 故在这里手动调用
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
Element.hide('dict');
function appEdite() {
	selfInfoForm.action = "/selfservice/selfinfo/appEditselfinfo.do?b_defend=link&isAppEdite=1";
	selfInfoForm.submit();
}
function appSave() {
	selfInfoForm.action = "/selfservice/selfinfo/editselfinfo.do?b_edit=edit&i9999=I9999&actiontype=update&setname=A01";
	selfInfoForm.target = "mil_body";
	selfInfoForm.submit();
}

function breturn(fieldsetid) {
	selfInfoForm.action = "/selfservice/selfinfo/appEditselfinfo.do?b_defendother=search&setname="
			+ fieldsetid + "&flag=infoself&isAppEdite=1";
	selfInfoForm.target = "mil_body";
	selfInfoForm.submit();
}

function appReturn() {
	selfInfoForm.action = "/selfservice/selfinfo/editselfinfo.do?b_edit=edit&i9999=I9999&actiontype=update&setname=A01";
	selfInfoForm.target = "mil_body";
	selfInfoForm.submit();
}
function savesDraft() {
	if (validate()) {
		selfInfoForm.action = "/selfservice/selfinfo/appEditselfinfo.do?b_save=save&isAppEdite=1";
		selfInfoForm.target = "mil_body";
		selfInfoForm.submit();
	}
}
function provesDraft() {
	if (validate()) {
		selfInfoForm.action = "/selfservice/selfinfo/appEditselfinfo.do?b_save=approve&isAppEdite=1";
		selfInfoForm.target = "mil_body";
		selfInfoForm.submit();
	}
}

function rendererFun() {
	// bug 34499 文本框显示出界面以外 wangb 20180208
	var form = document.getElementsByName('selfInfoForm')[0];
	form.style.marginTop = '4px';

	var valueInputsun = document.getElementsByName("<%=orgtemp%>");
	if (valueInputsun && valueInputsun.length > 0)
		document.getElementById("deptid").setAttribute("parentid",
				valueInputsun[0].value);

	var valueInputsum = document.getElementsByName("<%=postemp%>");
	if (valueInputsum && valueInputsum.length > 0) {
		if (valueInputsum[0].value)
			document.getElementById("jobid").setAttribute("parentid",
					valueInputsum[0].value);
		else
			document.getElementById("jobid").setAttribute("parentid",
					valueInputsun[0].value);
	}

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

	var dropdownCode = createDropDown("dropdownCode");
	var __t = dropdownCode;
	__t.type = "custom";
	__t.path = "/general/muster/select_code_tree.do";
	__t.readFields = "codeitemid";
	__t.cachable = true;
	__t.tag = "";
	_array_dropdown[_array_dropdown.length] = __t;
	initDropDown(__t);
}

function getchangeposun(outparamters) {
	var pretype = outparamters.getValue("pretype");
	var orgparentcode = outparamters.getValue("orgparentcode");
	var deptparentcode = outparamters.getValue("deptparentcode");
	var posparentcode = outparamters.getValue("posparentcode");
	AjaxBind.bind(selfInfoForm.orgparentcode, orgparentcode);
	AjaxBind.bind(selfInfoForm.deptparentcode, deptparentcode);
	AjaxBind.bind(selfInfoForm.posparentcode, posparentcode);
	document.getElementById("deptid").setAttribute("parentid", deptparentcode);
	document.getElementById("jobid").setAttribute("parentid", deptparentcode);
	var valueInputsun = document.getElementsByName(postemp);
	var dobjun = valueInputsun[0];
	dobjun.value = "";
	valueInputsun = document.getElementsByName(postempview);
	dobjun = valueInputsun[0];
	dobjun.value = "";
	var cc = document.getElementsByName(kktemp);
	var dobjkk = cc[0];
	dobjkk.value = "";
	cc = document.getElementsByName(kktempview);
	dobjkk = cc[0];
	dobjkk.value = "";

}
function getchangeposum(outparamters) {
	var pretype = outparamters.getValue("pretype");
	var orgparentcode = outparamters.getValue("orgparentcode");
	var orgvalue = outparamters.getValue("orgvalue");

	if (orgvalue != null && orgvalue.length > 0) {
		var orgvalueview = outparamters.getValue("orgviewvalue");
		var valueInputsun = document.getElementsByName(orgtemp);
		var dobjun = valueInputsun[0];
		dobjun.value = orgvalue;
		document.getElementById("deptid").setAttribute("parentid", orgvalue);
		valueInputsun = document.getElementsByName(orgtempview);
		dobjun = valueInputsun[0];
		dobjun.value = orgvalueview;
	}
	var valueInputskk = document.getElementsByName(kktemp);
	var dobjkk = valueInputskk[0];
	dobjkk.value = "";
	valueInputskk = document.getElementsByName(kktempview);
	dobjkk = valueInputskk[0];
	dobjkk.value = "";
	var deptparentcode = outparamters.getValue("deptparentcode");
	var posparentcode = outparamters.getValue("posparentcode");
	AjaxBind.bind(selfInfoForm.orgparentcode, orgparentcode);
	AjaxBind.bind(selfInfoForm.deptparentcode, deptparentcode);
	AjaxBind.bind(selfInfoForm.posparentcode, posparentcode);

}
function getchangeposkk(outparamters) {
	var pretype = outparamters.getValue("pretype");
	var orgparentcode = outparamters.getValue("orgparentcode");
	var deptparentcode = outparamters.getValue("deptparentcode");
	var posparentcode = outparamters.getValue("posparentcode");
	var orgvalue = outparamters.getValue("orgvalue");
	if (orgvalue != null && orgvalue.length > 0) {
		var orgvalueview = outparamters.getValue("orgviewvalue");
		var valueInputsun = document.getElementsByName(orgtemp);
		var dobjun = valueInputsun[0];
		dobjun.value = orgvalue;
		valueInputsun = document.getElementsByName(orgtempview);
		dobjun = valueInputsun[0];
		dobjun.value = orgvalueview;
	}
	var deptvalue = outparamters.getValue("deptvalue");
	if (deptvalue != null && deptvalue.length > 0) {
		var deptviewvalue = outparamters.getValue("deptviewvalue");
		if(postemp && postemp.length > 0) {
			var valueInputsum = document.getElementsByName(postemp);
			var dobjum = valueInputsum[0];
			dobjum.value = deptvalue;
			valueInputsum = document.getElementsByName(postempview);
			dobjum = valueInputsum[0];
			dobjum.value = deptviewvalue;
		}
	}
	AjaxBind.bind(selfInfoForm.orgparentcode, orgparentcode);

}

function changeOrg(pretype) {
	var value = "";
	var unIdInputs = document.getElementsByName(orgtempview);
	var valueInputsun = document.getElementsByName(orgtemp);
	if ('b0110' == pretype) {
		if (unIdInputs != null && unIdInputs != "undefined"
				&& unIdInputs.length > 0)
			value = unIdInputs[0].value;

		if (valueInputsun != null && valueInputsun != "undefined"
				&& valueInputsun.length > 0) {
			if (!value) {
				valueInputsun[0].value = "";
				document.getElementById("deptid").setAttribute("parentid", "");
				document.getElementById("jobid").setAttribute("parentid", "");
				changepos("2");
			}
		}

	} else if ('e0122' == pretype) {
		var umIdInputs = document.getElementsByName(postempview);
		var valueInputsum = document.getElementsByName(postemp);
		if (umIdInputs != null && umIdInputs != "undefined"
				&& umIdInputs.length > 0)
			value = umIdInputs[0].value;

		if (valueInputsum != null && valueInputsum != "undefined"
				&& valueInputsum.length > 0) {
			if (!value) {
				valueInputsum[0].value = "";
				valueInputsum[0].value = "";
				if (valueInputsun && valueInputsun.length > 0
						&& valueInputsun[0].value)
					document.getElementById("jobid").setAttribute("parentid",
							valueInputsun[0].value);
				else
					document.getElementById("jobid").setAttribute("parentid",
							"");

				changepos("1");
			}
		}

	} else if ('e01a1' == pretype) {
		var kkIdInputs = document.getElementsByName(kktempview);
		var valueInputskk = document.getElementsByName(kktemp);
		if (kkIdInputs != null && kkIdInputs != "undefined"
				&& kkIdInputs.length > 0)
			value = kkIdInputs[0].value;

		if (valueInputskk != null && valueInputskk != "undefined"
				&& valueInputskk.length > 0) {
			if (!value) {
				valueInputskk[0].value = "";
			}
		}
	}
}

function changepos(pretype) {
	if ('0' == pretype)
		pretype = '@K';

	if ('1' == pretype)
		pretype = 'UM';

	if ('2' == pretype)
		pretype = 'UN';

	var valueInputsun = document.getElementsByName(orgtemp);
	var dobjun = valueInputsun[0];
	var valueInputsum = document.getElementsByName(postemp);
	var dobjum = valueInputsum[0];
	var valueInputskk = document.getElementsByName(kktemp);
	var dobjkk = valueInputskk[0];
	var hashvo = new ParameterSet();
	hashvo.setValue("pretype", pretype);
	hashvo.setValue("orgparentcodestart", dobjun ? dobjun.value : "");
	hashvo.setValue("deptparentcodestart", dobjum ? dobjum.value : "");
	hashvo.setValue("posparentcodestart", dobjkk ? dobjkk.value : "");
	if (pretype == "UN") {
		var request = new Request({
			method : 'post',
			onSuccess : getchangeposun,
			functionId : '02010001012'
		}, hashvo);
	}
	if (pretype == "UM") {
		var request = new Request({
			method : 'post',
			onSuccess : getchangeposum,
			functionId : '02010001012'
		}, hashvo);
	}
	if (pretype == "@K") {
		var request = new Request({
			method : 'post',
			onSuccess : getchangeposkk,
			functionId : '02010001012'
		}, hashvo);
	}
}
function prove() {
	if (!checkIDCard())
		return false;

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
	//获取证件类型指标id
	var idType = document.getElementById("idType").value;
	//是否关联计算
	var cardflag = document.getElementById("cardflag").value;
	if("true"!=cardflag)
		return;
	//获取证件类型值
	var idTypeValue = "";
	if(idType&&document.getElementById(idType))
		idTypeValue = document.getElementById(idType).value;
	//身份证证件默认值
	var temp = document.getElementById("idTypeValue").value;
	//有默认身份证类型"1"or"01"跟选择的证件类型一致
	if(temp&&idTypeValue&&temp==idTypeValue||!idType){
		var hashvo = new ParameterSet();
		hashvo.setValue("idcardvalue", obj.value);
		var request = new Request({method : 'post', onSuccess : getBirthdayAge, functionId : '02010001013'}, hashvo);
	}
}

function getBirthdayAge(outparamters) {
	var birthdayvalue = outparamters.getValue("birthdayvalue");
	var agevalue = outparamters.getValue("agevalue");
	var axvalue = outparamters.getValue("axvalue");
	if (birthdayvalue != null) {

		var valueInputs = document.getElementsByName(birthdayfield);

		var dobj = valueInputs[0];
		if (dobj != null) {
			dobj.value = birthdayvalue;

		}
	}
	if (agevalue != null) {
		var valueInputs = document.getElementsByName(agefield);
		var dobj = valueInputs[0];
		if (dobj != null)
			dobj.value = agevalue;
	}
	if (axvalue != null) {
		var valueInputs = document.getElementsByName(axfield);
		var dobj = valueInputs[0];
		if (dobj != null)
			dobj.value = axvalue;
		if (axvalue == 1) {
			var valueInputs = document.getElementsByName(axviewfield);
			dobj = valueInputs[0];
			if (dobj != null)
				dobj.value = MAN_PERSON;
		} else if (axvalue == 2) {
			var valueInputs = document.getElementsByName(axviewfield);
			dobj = valueInputs[0];
			if (dobj != null)
				dobj.value = WOMAN_PERSON;
		}
	}

}

function showinfo(divId) {
	var fObj=document.getElementById(divId);
	if(fObj.style.display=='none') {
		fObj.style.display='block';
	}else {
		fObj.style.display='none';
	}
}