var code_desc;
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
					var view_vos = document.getElementsByName(code_viewname + ".value");
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
	obj.focus();
}

function reloadMenu(a0100, setname, actiontype) {
	if (actiontype == "update")
		return;
	if (a0100 != null && a0100 != "" && a0100 != "A0100" && a0100 != "a0100" && a0100 != "su") {
		if (setname == "A01")
			parent.mil_menu.location.reload();
	}
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
function trun() {
	parent.parent.menupnl.toggleCollapse(true);
}

function exeButtonAction(actionStr, target_str) {
	target_url = actionStr;
	window.open(target_url, target_str);
}
function exeButtonAction2(actionStr) {
	parent.location.href = actionStr;
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
	if(temp&&idTypeValue&&temp==idTypeValue||!idType || !idTypeValue){
		var hashvo = new ParameterSet();
		obj.value = trim(obj.value.replace(/ /g, ""));
	hashvo.setValue("idcardvalue", obj.value);
		var request = new Request({method : 'post', onSuccess : getBirthdayAge, functionId : '02010001013'}, hashvo);
	}
}

function CalculateWorkDate(obj) {
	var hashvo = new ParameterSet();
	hashvo.setValue("workdatevalue", obj.value);
	var request = new Request({method : 'post', onSuccess : getWorkAge,	functionId : '02010001014'}, hashvo);
}

function CalculatePostAge(obj) {
	var hashvo = new ParameterSet();
	hashvo.setValue("postdatevalue", obj.value);
	var request = new Request({method : 'post', onSuccess : getPostAge,	functionId : '02010001015'}, hashvo);
}
function proves() {
	if (validate()) {
		if (confirm("一旦报批数据，将不能进行修改，是否报批?")) {
			selfInfoForm.action = "/selfservice/selfinfo/editselfinfo.do?b_appeals=link";
			selfInfoForm.submit();
		} else {
			return;
		}
	}
}
function savesub() {
	if(document.getElementById('idcard'))
	  	calculatebirthday(document.getElementById('idcard'));
	if (validate()) {
		if (document.getElementById("saveid"))
			document.getElementById("saveid").disabled = true;

		if (document.getElementById("savereturnid"))
			document.getElementById("savereturnid").disabled = true;

		selfInfoForm.action = "/workbench/info/editselfinfo.do?b_savesub=link";
		selfInfoForm.submit();
	} else {
		return;
	}
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
				alert("整数位长度超过定义" + itemlen + ",请修改！");
				obj.focus();
				return false;
			}
		} else {
			var q_srt = t_len.substring(0, inde);
			var n_srt = t_len.substring(inde + 1);
			if (q_srt.length > itemlen) {
				alert("整数位长度超过定义" + itemlen + ",请修改！");
				obj.focus();
				return false;
			} else if (n_srt.length > decimalw) {
				alert("小数位长度超过定义" + decimalw + ",请修改！");
				obj.focus();
				return false;
			}
		}
	}
}
function savere() {
	if (validate()) {
		document.getElementsByName("savasss")[0].disabled = true;
		document.getElementsByName("savass")[0].disabled = true;
		selfInfoForm.action = "/workbench/info/editselfinfo.do?b_savesubre=link";
		selfInfoForm.submit();
	} else {
		return;
	}
}
function check() {
	var item = document.getElementById("a0101");
	if (!item)
		return;

	var value = item.value;
	var itemvalue = "";
	for (var i = 0; i < value.length; i++) {
		var index = value.substring(i, i + 1);
		if (index.charCodeAt(0) > 255) {
			itemvalue += index;
		} else if ((/^[a-zA-Z0-9]+$/).test(index)) {
			itemvalue += index;
		} else if (index == "(" || index == ")") {
			itemvalue += index;
		} else if (index == " ") {
			itemvalue += index;
		}
	}
	item.value = itemvalue;
}
function check(item) {
	if (!item)
		return;

	var value = item.value;
	if (!value)
		return;

	var itemvalue = "";
	for (var i = 0; i < value.length; i++) {
		var index = value.substring(i, i + 1);
		if (!(/^[\u00a0]+$/).test(index))
			itemvalue += index;
	}
	item.value = itemvalue;
}
//加权因子
var Wi = [ 7, 9, 10, 5, 8, 4, 2, 1, 6, 3, 7, 9, 10, 5, 8, 4, 2, 1 ]; 
//身份证验证位值.10代表X
var ValideCode = [ 1, 0, 10, 9, 8, 7, 6, 5, 4, 3, 2 ]; 
function IdCardValidate(idCard) {
	// 去掉字符串头尾空格
	idCard = trim(idCard.replace(/ /g, "")); 
	if (idCard.length == 15) {
		// 进行15位身份证的验证
		return isValidityBrithBy15IdCard(idCard); 
	} else if (idCard.length == 18) {
		// 得到身份证数组
		var a_idCard = idCard.split(""); 
		// 进行18位身份证的基本验证和第18位的验证
		if (isValidityBrithBy18IdCard(idCard) && isTrueValidateCodeBy18IdCard(a_idCard)) { 
			return true;
		} else {
			return false;
		}
	} else {
		return false;
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
	// 声明加权求和变量
	var sum = 0; 
	if (a_idCard[17].toLowerCase() == 'x') {
		// 将最后位为x的验证码替换为10方便后续操作
		a_idCard[17] = 10; 
	}
	for (var i = 0; i < 17; i++) {
		// 加权求和
		sum += Wi[i] * a_idCard[i]; 
	}
	// 得到验证码所位置
	valCodePosition = sum % 11; 
	if (a_idCard[17] == ValideCode[valCodePosition]) {
		return true;
	} else {
		return false;
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
// 去掉字符串头尾空格
function trim(str) {
	return str.replace(/(^\s*)|(\s*$)/g, "");
}
function addDict(code, itemid, obj, codeitemid) {
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

	for (var i = 0; i < g_dm.length; i++) {
		dmobj = g_dm[i];

		if (code == "UM" || code == "@K" || code == "UN") {
			if (codeitemid) {
				var flag = false;
				var id = dmobj.ID;
				var codeitemids = codeitemid.split("`");
				for (var a = 0; a < codeitemids.length; a++) {
					if (!codeitemids[a])
						continue;

					if (id.substring(2).indexOf(codeitemids[a].substring(2)) == 0) {
						flag = true;
						break;
					}
				}
				if (!flag)
					continue;
			}
			if (vos) {
				var b_value = vos.value;
				if (b_value == null || b_value == "") {
					b_value = ""
				}
				if ((code_value != "" || isAall == 'all')
						&& code_value.length <= dmobj.ID.substring(2).length) {

					if ((dmobj.V.indexOf(value) != -1 && dmobj.ID.indexOf(code + code_value) == 0 
							&& dmobj.ID.indexOf(code + b_value) == 0)
							|| (dmobj.ID.indexOf(code + value) == 0 && dmobj.ID.indexOf(code + b_value) == 0)) {
						if (dmobj.ID.substring(2).indexOf(code_value) == 0) {
							var hashvo = new ParameterSet();
							hashvo.setValue("a_code", dmobj.ID);
							var request = new Request({method : 'post', onSuccess : getBirthdayAge,	functionId : '10200770001'}, hashvo);
							function getBirthdayAge(outparamters) {
								var parentdesc = outparamters.getValue("parentdesc");
								var no = new Option();
								no.value = dmobj.ID;
								no.text = (parentdesc.length > 0 ? parentdesc + "/" : "") + dmobj.V;
								dict_vo.options[r] = no;
								r++;
							}
						}
					}
				} else {
					if (code_value.length > 0 && ((dmobj.V.indexOf(value) != -1 
							&& dmobj.ID.indexOf(code) == 0) || (dmobj.ID.indexOf(code + value) == 0))) {
						if (dmobj.ID.substring(2).indexOf(code_value) == 0) {
							var hashvo = new ParameterSet();
							hashvo.setValue("a_code", dmobj.ID);
							var request = new Request({method : 'post', onSuccess : getBirthdayAge,	functionId : '10200770001'}, hashvo);
							function getBirthdayAge(outparamters) {
								var parentdesc = outparamters.getValue("parentdesc");
								var no = new Option();
								no.value = dmobj.ID;
								no.text = (parentdesc.length > 0 ? parentdesc + "/" : "")	+ dmobj.V;
								dict_vo.options[r] = no;
								r++;
							}
						}
					}
				}
			} else {
				if ("UM" == code) {
					if (code_value != ""
							&& code_value.length >= dmobj.ID.substring(2).length) {
						if ((dmobj.V.indexOf(value) != -1 && (dmobj.ID.indexOf(code) == 0 || dmobj.ID.indexOf('UN') == 0))
								|| (dmobj.ID.indexOf(code + value) == 0 && (dmobj.ID.indexOf(code) == 0 
										|| dmobj.ID.indexOf('UN') == 0))) {
							if (code_value.indexOf(dmobj.ID.substring(2)) == 0) {
								var no = new Option();
								no.value = dmobj.ID;
								no.text = dmobj.V;
								dict_vo.options[r] = no;
								r++;
							}
						}
					} else {
						if ((dmobj.V.indexOf(value) != -1 && (dmobj.ID.indexOf(code) == 0 || dmobj.ID.indexOf('UN') == 0))
								|| (dmobj.ID.indexOf(code + value) == 0 && (dmobj.ID.indexOf(code) == 0 
										|| dmobj.ID.indexOf('UN') == 0))) {
							var no = new Option();
							no.value = dmobj.ID;
							no.text = dmobj.V;
							dict_vo.options[r] = no;
							r++;
						}
					}
				} else {
					if (code_value != "" && code_value.length >= dmobj.ID.substring(2).length) {
						if ((dmobj.V.indexOf(value) != -1 && dmobj.ID.indexOf(code) == 0)
								|| (dmobj.ID.indexOf(code + value) == 0 && dmobj.ID.indexOf(code) == 0)) {
							if (code_value.indexOf(dmobj.ID.substring(2)) == 0) {
								var hashvo = new ParameterSet();
								hashvo.setValue("a_code", dmobj.ID);
								var request = new Request({method : 'post', onSuccess : getBirthdayAge, functionId : '10200770001'}, hashvo);
								function getBirthdayAge(outparamters) {
									var parentdesc = outparamters.getValue("parentdesc");
									var no = new Option();
									no.value = dmobj.ID;
									no.text = (parentdesc.length > 0 ? parentdesc + "/" : "") + dmobj.V;
									dict_vo.options[r] = no;
									r++;
								}
							}
						}
					} else {
						if ((dmobj.V.indexOf(value) != -1 && dmobj.ID.indexOf(code) == 0)
								|| (dmobj.ID.indexOf(code + value) == 0 && dmobj.ID.indexOf(code) == 0)) {
							var hashvo = new ParameterSet();
							hashvo.setValue("a_code", dmobj.ID);
							var request = new Request({method : 'post', onSuccess : getBirthdayAge, functionId : '10200770001'}, hashvo);
							function getBirthdayAge(outparamters) {
								var parentdesc = outparamters.getValue("parentdesc");
								var no = new Option();
								no.value = dmobj.ID;
								no.text = (parentdesc.length > 0 ? parentdesc + "/" : "")	+ dmobj.V;
								dict_vo.options[r] = no;
								r++;
							}
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
function getchangeposun(outparamters) {
	var pretype = outparamters.getValue("pretype");
	var orgparentcode = outparamters.getValue("orgparentcode");

	var deptparentcode = outparamters.getValue("deptparentcode");
	var posparentcode = outparamters.getValue("posparentcode");
	AjaxBind.bind(selfInfoForm.orgparentcode, orgparentcode);
	AjaxBind.bind(selfInfoForm.deptparentcode, deptparentcode);
	AjaxBind.bind(selfInfoForm.posparentcode, posparentcode);
	if (document.getElementById("deptId"))
		document.getElementById("deptId").setAttribute("parentid", deptparentcode);

	if (document.getElementById("jobId"))
		document.getElementById("jobId").setAttribute("parentid", deptparentcode);

	var valueInputsun = document.getElementsByName(postemp);
	var dobjun = valueInputsun[0];
	if(dobjun)
		dobjun.value = "";
		
	valueInputsun = document.getElementsByName(postempview);
	dobjun = valueInputsun[0];
	if(dobjun)
		dobjun.value = "";
	
	var cc = document.getElementsByName(kktemp);
	var dobjkk;
	if (cc.length > 0) {
		dobjkk = cc[0];
		dobjkk.value = "";
		cc = document.getElementsByName(kktempview);
		dobjkk = cc[0];
		dobjkk.value = "";
	}
}
function getchangeposum(outparamters) {
	var pretype = outparamters.getValue("pretype");
	var orgparentcode = outparamters.getValue("orgparentcode");
	var orgvalue = outparamters.getValue("orgvalue");
	if (orgvalue != null && orgvalue.length > 0 && orgtemp && orgtemp.length > 0) {
		var orgvalueview = outparamters.getValue("orgviewvalue");
		var valueInputsun = document.getElementsByName(orgtemp);
		var dobjun = valueInputsun[0];
		dobjun.value = orgvalue;
		if (document.getElementById("deptId"))
		    document.getElementById("deptId").setAttribute("parentid", orgvalue);
		valueInputsun = document.getElementsByName(orgtempview);
		dobjun = valueInputsun[0];
		dobjun.value = orgvalueview;
	}
	
	if (kktemp && kktemp.length > 0) {
		var valueInputskk = document.getElementsByName(kktemp);
		var dobjkk;
		if (valueInputskk.length > 0) {
			dobjkk = valueInputskk[0];
			dobjkk.value = "";
			valueInputskk = document.getElementsByName(kktempview);
			dobjkk = valueInputskk[0];
			dobjkk.value = "";
		}
	}
	
	var deptparentcode = outparamters.getValue("deptparentcode");
	var posparentcode = outparamters.getValue("posparentcode");
	AjaxBind.bind(selfInfoForm.orgparentcode, orgparentcode);
	if (deptparentcode != null && deptparentcode.length > 0)
		AjaxBind.bind(selfInfoForm.deptparentcode, deptparentcode);
	AjaxBind.bind(selfInfoForm.posparentcode, posparentcode);

}
function getchangeposkk(outparamters) {
	var pretype = outparamters.getValue("pretype");
	var orgparentcode = outparamters.getValue("orgparentcode");
	var deptparentcode = outparamters.getValue("deptparentcode");
	var posparentcode = outparamters.getValue("posparentcode");
	var orgvalue = outparamters.getValue("orgvalue");
	if (orgvalue != null && orgvalue.length > 0 && orgtemp && orgtemp.length > 0) {
		var orgvalueview = outparamters.getValue("orgviewvalue");
		var valueInputsun = document.getElementsByName(orgtemp);
		var dobjun = valueInputsun[0];
		dobjun.value = orgvalue;
		valueInputsun = document.getElementsByName(orgtempview);
		dobjun = valueInputsun[0];
		dobjun.value = orgvalueview;
	}
	
	if (postemp && postemp.length > 0) {
		var deptvalue = outparamters.getValue("deptvalue");
		var deptviewvalue = outparamters.getValue("deptviewvalue");
		var dobjum = document.getElementsByName(postemp)[0];
		dobjum.value = deptvalue;
		dobjum = document.getElementsByName(postempview)[0];
		dobjum.value = deptviewvalue;
		AjaxBind.bind(selfInfoForm.orgparentcode, orgparentcode);
	}
}
function changeOrg(pretype) {
	var value = "";
	var unIdInputs = document.getElementsByName(orgtempview);
	var valueInputsun = document.getElementsByName(orgtemp);
	if ('b0110' == pretype) {
		if (unIdInputs != null && unIdInputs != "undefined" && unIdInputs.length > 0)
			value = unIdInputs[0].value;
		if (valueInputsun != null && valueInputsun != "undefined" && valueInputsun.length > 0) {
			if (!value) {
				valueInputsun[0].value = "";
				if (document.getElementById("deptId"))
					document.getElementById("deptId").setAttribute("parentid", "");

				if (document.getElementById("jobId"))
					document.getElementById("jobId").setAttribute("parentid", "");
				changepos("2");
			}
		}
	} else if ('e0122' == pretype) {
		var umIdInputs = document.getElementsByName(postempview);
		var valueInputsum = document.getElementsByName(postemp);
		if (umIdInputs != null && umIdInputs != "undefined" && umIdInputs.length > 0)
			value = umIdInputs[0].value;
		if (valueInputsum != null && valueInputsum != "undefined" && valueInputsum.length > 0) {
			if (!value) {
				valueInputsum[0].value = "";
				if (document.getElementById("jobId")) {
					if (valueInputsun && valueInputsun.length > 0 && valueInputsun[0].value)
						document.getElementById("jobId").setAttribute("parentid", valueInputsun[0].value);
					else
						document.getElementById("jobId").setAttribute("parentid", "");
				}
				changepos("1");
			}
		}
	} else if ('e01a1' == pretype) {
		var kkIdInputs = document.getElementsByName(kktempview);
		var valueInputskk = document.getElementsByName(kktemp);
		if (kkIdInputs != null && kkIdInputs != "undefined" && kkIdInputs.length > 0)
			value = kkIdInputs[0].value;

		if (valueInputskk != null && valueInputskk != "undefined" && valueInputskk.length > 0) {
			if (!value) {
				valueInputskk[0].value = "";
			}
		}
	}
}
function changepos(pretype) {
	if ('0' == pretype) {
		pretype = '@K';
	}
	if ('1' == pretype) {
		pretype = 'UM';
	}
	if ('2' == pretype) {
		pretype = 'UN';
	}
	var valueInputsun = document.getElementsByName(orgtemp);
	var dobjun;
	var dobjum;
	var dobjkk;
	if (valueInputsun != null && valueInputsun != "undefined" && valueInputsun.length > 0)
		dobjun = valueInputsun[0];
	var valueInputsum = document.getElementsByName(postemp);
	if (valueInputsum != null && valueInputsum != "undefined" && valueInputsum.length > 0)
		dobjum = valueInputsum[0];
	var valueInputskk = document.getElementsByName(kktemp);
	if (valueInputskk != null && valueInputskk != "undefined" && valueInputskk.length > 0)
		dobjkk = valueInputskk[0];
	var hashvo = new ParameterSet();
	if (pretype != null)
		hashvo.setValue("pretype", pretype);
	
	hashvo.setValue("orgparentcodestart", dobjun ? dobjun.value : "");
	hashvo.setValue("deptparentcodestart", dobjum ? dobjum.value : "");
	hashvo.setValue("posparentcodestart", dobjkk ? dobjkk.value : "");

	if (pretype == "UN") {
		var request = new Request({method : 'post', onSuccess : getchangeposun, functionId : '02010001012'}, hashvo);
	}
	if (pretype == "UM") {
		var request = new Request({method : 'post', onSuccess : getchangeposum, functionId : '02010001012'}, hashvo);
	}
	if (pretype == "@K") {
		var request = new Request({method : 'post', onSuccess : getchangeposkk, functionId : '02010001012'}, hashvo);
	}
}
function save(){
	document.getElementsByName("savass")[0].disabled = true;
	var idcardflag = "";
	var idcarditem = document.getElementById("idcardflag");
	//获取证件类型指标id
	var idType = document.getElementById("idType").value;
	//获取证件类型值
	var idTypeValue = "";
	if(idType&&document.getElementById(idType))
		idTypeValue = document.getElementById(idType).value;
	//身份证证件默认值
	var temp = document.getElementById("idTypeValue").value;
	if(idcarditem!=null&&idcarditem!=undefined)
		idcardflag = idcarditem.value;
	var idcard = document.getElementById("idcard");
	var idcardvalue = "";
	if(idcard!=null&&idcard!=undefined){
		idcardvalue = idcard.value;
		idcardvalue = trim(idcardvalue.replace(/ /g, ""));
		idcard.value = idcardvalue;
	}
	var idcardDesc = document.getElementById("idcardDesc");
	var descValue = "身份证号";
	if (idcardDesc)
		descValue = idcardDesc.value;
	
	var flag = IdCardValidate(idcardvalue);
	if(idcardflag=="true"){
		//有默认身份证类型"1"or"01"跟选择的证件类型一致
		if(temp&&idTypeValue&&temp==idTypeValue||!idType)
		if(!flag){
			alert(descValue + "格式错误，请重新填写！");
			document.getElementsByName("savass")[0].disabled =false;
			return;
		}
	} else {
		if(idcardvalue!=""){
			if(temp&&idTypeValue&&temp==idTypeValue||!idType)
			if(!flag){
				alert(descValue + "格式错误，请重新填写！");
				document.getElementsByName("savass")[0].disabled =false;
				return;
			}
		}
	}
	if(validate()){
		if(returnValue == "75")
			selfInfoForm.target='il_body';
		else if(returnValue == "73")
			selfInfoForm.target='i_body';
		else 
			selfInfoForm.target='nil_body';
		
		selfInfoForm.action="/workbench/info/editselfinfo.do?b_save_new=link";
		selfInfoForm.submit();
	}else{
		document.getElementsByName("savass")[0].disabled =false;
		return;
	}
}
function change() {
 	var list = document.getElementById("list");
 	va = list.value;
 	if (va == "A01") {
 		selfInfoForm.action = "/workbench/info/editselfinfo.do?b_edit=edit&i9999=I9999&actiontype=update&setname=A01&isAppEdite=1";
 	} else if(va == "A00"){
 		selfInfoForm.action = "/workbench/media/searchmediainfolist.do?b_search=link&setname="+va+"&setprv=2&flag=notself&returnvalue=" + returnValue + "&userbase=" + nbase + "&isAppEdite=1";
 	}else {
 	
 	selfInfoForm.action = "/workbench/info/searchselfdetailinfo.do?b_searchsort=search&setname="+va+"&flag=noself&isAppEdite=1";
 	}
 	selfInfoForm.target="mil_body";
	selfInfoForm.submit();
} 
function changetitle(obj) {
	var hashvo = new ParameterSet();
	hashvo.setValue("codeitemid", obj.value);
	hashvo.setValue("uplevel", uplevel);
	var request = new Request({method : 'post', onSuccess : changetitlevalue, functionId : '02010001016'}, hashvo);
	function changetitlevalue(outparamters) {
		var name = outparamters.getValue("name");
		var targetobj = document.getElementsByName(obj.name.replace('.value', '.viewvalue'))[0];
		targetobj.title = name;
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
				dobj.value = "男";
		} else if (axvalue == 2) {
			var valueInputs = document.getElementsByName(axviewfield);
			dobj = valueInputs[0];
			if (dobj != null)
				dobj.value = "女";
		}
	}
}
function getWorkAge(outparamters) {
	var workagevalue = outparamters.getValue("workagevalue");
	if (workagevalue != null) {
		var valueInputs = document.getElementsByName(workagefield);
		var dobj = valueInputs[0];
		if (dobj != null) {
			dobj.value = workagevalue;
			if (workdatefield == startpostfield) {
				valueInputs = document.getElementsByName(postagefield);
				if(valueInputs && valueInputs.length > 0) {
					dobj = valueInputs[0];
					dobj.value = workagevalue;
				}
			}
		}
	}
}
function getPostAge(outparamters) {
	var postagevalue = outparamters.getValue("postagevalue");
	if (postagevalue != null) {
		var valueInputs = document.getElementsByName(postagefield);
		var dobj = valueInputs[0];
		if (dobj != null) {
			dobj.value = postagevalue;
			if (workdatefield == startpostfield) {
				valueInputs = document.getElementsByName(workagefield);
				dobj = valueInputs[0];
				dobj.value = workagevalue;
			}
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
