
function hrbreturn(flag, target, fr01) {
	var obj = eval("document." + fr01);
	var objs = obj.elements;
	var len = objs.length;
	for (var i = 0; i < len; i = i + 1) {
		if (objs[i].type == "checkbox") {
			objs[i].checked = false;
		}
	}
	var url = "";
	if (flag == "org") {
		url = "/general/tipwizard/tipwizard.do?br_orginfo=link";
	} else if (flag == "emp") {
		url = "/general/tipwizard/tipwizard.do?br_employee=link";
	} else if (flag == "workrest") {
		url = "/general/tipwizard/tipwizard.do?br_workrest=link";
	} else if (flag == "insurance") {
		url = "/general/tipwizard/tipwizard.do?br_Insurance=link";
	} else if (flag == "compensation") {//薪资
		url = "/general/tipwizard/tipwizard.do?br_compensation=link";
	} else if (flag == "report") {//报表
		url = "/general/tipwizard/tipwizard.do?br_report=link";
	} else if (flag == "train") {//培训
		url = "/general/tipwizard/tipwizard.do?br_train=link";
	} else if (flag == "performance") {//绩效
		url = "/general/tipwizard/tipwizard.do?br_performance=link";
	} else if (flag == "retain") {//招聘
		url = "/general/tipwizard/tipwizard.do?br_retain=link";
	} else if (flag == "retain") {
		url = "/general/tipwizard/tipwizard.do?br_retain=link";
	} else if (flag == "dtgh") { //党团管理
		url = "/general/tipwizard/tipwizard.do?br_dtgh=link";
	} else if (flag == "leader") { //领导班子
		url = "/general/tipwizard/tipwizard.do?br_leader=link"
	}else if (flag == "capability") { //能力素质
		url = "/general/tipwizard/tipwizard.do?br_capability=link";
	} else if (flag == "law") { //文档管理
		url = "/general/tipwizard/tipwizard.do?br_law=link";
	} else if (flag == "contract") { //合同管理
		url = "/general/tipwizard/tipwizard.do?br_ct=link";
	} else if (flag == "selfinfo") { //zizhufuwu
		url = "/general/tipwizard/tipwizard.do?br_selfinfo=link";
	}
	
	if (url === "") {
		alert("\u8fd4\u56de\u53c2\u6570\u4e0d\u6b63\u786e\uff0c\u8fd4\u56de\u529f\u80fd\u65e0\u6548\uff01");
		return false;
	}
	if (target == "1") {
		document.location = url;
	} else if (target == "2") {
		self.parent.location = url;
    } else if (target == "3") {
		self.parent.parent.location = url;
	} else {
		if (target === "") {
			target = "il_body";
		}
		obj.action = url;
		obj.target = target;
		obj.submit();
	}
}