// 常量
// 年 "1"; 半年 "2"; 季度 "3"; 月 "4"; 周 "5";
// 全局变量
var workplan = workplan || {};
workplan.main = workplan.main || {}
workplan.main.global = workplan.main.global || {
	period_type : "1",
	period_year : "2013",
	period_month : "1",
	period_week : "1",
	week_num : 4,// 本期间周个数
	old_period_month : "1",
	object_id : "",
	concerned_bteam : "false",// 当前显示的是团队成员
	concerned_cur_page : 1,// 当前页
	sub_object_id : "",// 当前显示的团队上级 团队成员：人员 岗位 下属部门：都是岗位
	sub_person_flag : "true",// 人力地图 上级是否人员标记，有可能是岗位 查看团队成员时用
	deptleader : "",// 部门负责人
	p0700 : "", // 计划id
	p0723 : "",// 个人计划 部门计划
	p0725 : "1",// 类型
	plan_design : "1",// 1 计划制定 2 计划跟踪
	btn_publish_visible : false,// 发布按钮是否显示
	my_direct_sub_people : false,// 是否是我的直接下级的计划
	myplan : true,
	subplan : false,
	curjsp : "selfplan", // 区分哪个jsp调用的， selfplan teamplan 因为有共有变量及共用函数
	fromflag : "",// 是否业务用户
	task_path : "",
	return_url : "",//
	super_concerned_objs : [],
	teaminfo : {},
	bAddChildTask : false,
	selNode : null,
	hasSumRow : true,
	grid_width : 0,
	b_result : false,// 函数执行结果
	b_loaded : false,// 页面加载完毕
	evaluationField : "",// 用于存储addGridScore()方法渲染时的id和score
	store : {},
	cTeam_needSee : false // 计划关注人穿透查看下级中点击"返回上级"按钮的一个参数,控制 needSeeSub
}
var wpm = workplan.main.global;
wpm.showSubTask = "0";//是否显示下属任务 =0 不显示 =1 显示
var g_tree = null;
var _columns;
var _role;// 登陆人和计划所有者之间的上下级关系,本人登录返回0,直接上级返回1,上级(不是直接上级)返回2,其他关系返回-1
var selected_p0800 = 0; // 已选中的任务号
var g_w_tabledata = null; //
var g_w_field = null; //
var g_w_column = null; //
var defaultName = null;// 计划制订页面添加任务栏中负责人(默认显示)
var needRefresh = "yes";// 是否需要刷新人力地图

function getElementLeft(element) {
	var actualLeft = element.offsetLeft;
	var current = element.offsetParent;
	while (current !== null) {
		actualLeft += current.offsetLeft;
		current = current.offsetParent;
	}
	return actualLeft;
}
function getElementTop(element) {
	var actualTop = element.offsetTop;
	var current = element.offsetParent;
	while (current !== null) {
		actualTop += current.offsetTop;
		current = current.offsetParent;
	}
	return actualTop;
}

function removeElement(_element) {
	var _parentElement = _element.parentNode;
	if (_parentElement) {
		_parentElement.removeChild(_element);
	}
}
function GetRequest() {
	var url = location.search; // 获取url中"?"符后的字串
	var theRequest = new Object();
	if (url.indexOf("?") != -1) {
		var str = url.substr(1);

		strs = str.split("&");
		for (var i = 0; i < strs.length; i++) {
			theRequest[strs[i].split("=")[0]] = (strs[i].split("=")[1]);
		}
	}
	return theRequest;
}

function hideDropdownBox() {
	var box = document.getElementById("dropdownBox");
	box.style.display = "none";
}

// 加减年
function yearchange(va) {
	var year = Ext.getDom('myeartitle');
	year.innerHTML = Number(year.innerHTML) + va;
}

/*
 * 下拉选择我的部门、团队成员等人力地图类型
 */
function dropdownAttentionMenu() {
	var hashvo = new ParameterSet();
	hashvo.setValue("oprType", "dropdownHummanMapTypeList");
	hashvo.setValue("periodType", wpm.period_type);
	hashvo.setValue("periodYear", wpm.period_year);
	hashvo.setValue("periodMonth", wpm.period_month);
	hashvo.setValue("periodWeek", wpm.period_week);
	hashvo.setValue("p0723", wpm.p0723);
	hashvo.setValue("objectId", wpm.object_id);
	hashvo.setValue("planType",wpm.planType);//haosl planType 为workplan_main.jsp中定义的全局变量
	var request = new Request({
				method : 'post',
				asynchronous : true,
				onSuccess : dropdownHummanMapType_ok,
				functionId : '9028000702'
			}, hashvo);
}

function dropdownHummanMapType_ok(outparamters) {
	var strplan = outparamters.getValue("info");
	strplan = getDecodeStr(strplan);
	// var planobj = eval("("+strplan+")");
	var planobj = Ext.decode(strplan);

	var strhtml = "";
	strhtml = "";
	var worklist = planobj.typelist;
	for (var i = 0; i < worklist.length; i++) {
		strhtml = strhtml + '<li> <a id="' + worklist[i].type_id
				+ '" onclick="' + 'selectHummanMapType(' + worklist[i].type_id
				+ ')' + '" href="javascript:void(0)" >' + worklist[i].type_name
				+ '</a></li>';

	}
	strhtml = "<ul>" + strhtml + "</ul>"
	dispalyDropdownBox("4", strhtml);
}

// 加载下拉列表
function dispalyDropdownBox(type, strhtml) {

	if (type == "7") { // 下拉显示月份
		// 填报期间范围权限 chent 20170112 start
		if(wpm.period_type == "4") {//月计划隐藏月份
			var validmonths = ',';
			var key = 'p3';
			for(var p in wpm.cycle_function){
				var obj = wpm.cycle_function[p];
				if(obj[key]){
					validmonths += obj[key].cycle+',';
				}
			}
			
			for(var i=1; i<=12; i++){
				if(validmonths.indexOf(','+i+',') == -1){
					Ext.getDom('li'+i).style.display = 'none';
				}
			}
		} else {
			for(var i=1; i<=12; i++){
				Ext.getDom('li'+i).style.display = 'block';
			}
		}
		// 填报期间范围权限 chent 20170112 end
		var box = document.getElementById("monthlist");
		var parentobj = document.getElementById("plantype_div");
		var left = getElementLeft(parentobj) + 10;
		var top = getElementTop(parentobj) + parentobj.offsetHeight;
		top = top + "px";
		left = left + "px";
		box.style.top = top;
		box.style.left = left;
		box.style.display = "block";
		var j = Number(wpm.period_month) - 1;
		var em = Ext.query("#months a");
		for (var i = 0; i < em.length; i++) {
			em[i].style.backgroundColor = "";
			em[i].style.color = "";
		}
		em[j].style.backgroundColor = "#549FE3";
		em[j].style.color = "#ffffff";
		return;
	}
	var box = document.getElementById("dropdownBox");
	box.style.display = "none";
	box.innerHTML = strhtml;
	box.style.display = "block";

	if (type == "1") {
		var parentobj = document.getElementById("plantype_div");
		var left = getElementLeft(parentobj);// +120;
		var top = getElementTop(parentobj) + parentobj.offsetHeight;
		top = top + "px";
		left = left + "px";
		box.style.top = top;
		box.style.left = left;
		box.style.width = "100px";
	} else if (type == "2") {// 下拉显示年份
		var parentobj = document.getElementById("plantype_div");
		var left = getElementLeft(parentobj) + 80;
		var top = getElementTop(parentobj) + parentobj.offsetHeight;
		top = top + "px";
		left = left + "px";
		box.style.top = top;
		box.style.left = left;
		box.style.width = "100px";
	} else if (type == "3") {
		var parentobj = document.getElementById("planscopename");
		var left = getElementLeft(parentobj);
		var top = getElementTop(parentobj) + parentobj.offsetHeight;
		top = top + "px";
		left = left + "px";
		box.style.top = top;
		box.style.left = left;
		box.style.width = "100px";
	} else if (type == "4") {
		var parentobj = document.getElementById("concerneddivx");
		var left = getElementLeft(parentobj);
		var top = getElementTop(parentobj) + parentobj.offsetHeight;
		top = top + "px";
		left = left + "px";
		box.style.top = top;
		box.style.left = left;
		box.style.width = "153px";
	} else if (type == "5") {
		var parentobj = document.getElementById("plantitle");
		var left = getElementLeft(parentobj);
		var top = getElementTop(parentobj) + parentobj.offsetHeight;
		top = top + "px";
		left = left + "px";
		box.style.top = top;
		box.style.left = left;
		box.style.width = "140px";
	} else if (type == "6") { // 工作总结 add by 刘蒙
		var parentobj = document.getElementById("workReports");
		var left = getElementLeft(parentobj);
		var top = getElementTop(parentobj) + parentobj.offsetHeight;
		top = top + "px";
		left = left + "px";
		box.style.top = top;
		box.style.left = left;
		box.style.width = "88px";
	} else if (type == "8") { // 复制上期任务、导出任务
		var parentobj = document.getElementById("a_addMenu");
		var left = getElementLeft(parentobj);
		var top = getElementTop(parentobj) + parentobj.offsetHeight;
		top = top + "px";
		left = left + "px";
		box.style.top = top;
		box.style.left = left;
		box.style.width = "150px";
	}
}
// ---显示对象
function displayObj(objname) {
	var nameobj = Ext.getDom(objname);
	if (nameobj != null) {
		nameobj.style.display = "block";
	}
}
// ---隐藏对象
function hideObj(objname) {
	var nameobj = Ext.getDom(objname);
	if (nameobj != null) {
		nameobj.style.display = "none";
	}
}
// ---设置计划类型
function setPeriodTypeName(namevalue) {
	var nameobj = document.getElementById("periodtypename");
	nameobj.innerHTML = namevalue
			+ '&nbsp;<img dropdownName="dropdownBox" onclick="dropdownPeriodType()" src="/workplan/image/jiantou.png"/>';
}
// ---设置计划期间
function setPeriodName(periodyear, periodmonth) {
	var monthDesc = "";
	if (wpm.period_type == "4" || wpm.period_type == "5") {
		monthDesc = periodmonth + "月";
		if (Number(periodmonth) < 10) {
			monthDesc = "&nbsp;" + monthDesc;
		}
	}

	monthDesc = periodyear + "年" + monthDesc;
	var nameobj = document.getElementById("periodname");
	nameobj.innerHTML = monthDesc
			+ '&nbsp;<img dropdownName="both" src="/workplan/image/jiantou.png"/>';

}

// ----设置计划标题
function setPlanTitle(namevalue) {
	var nameobj = document.getElementById("plantitle");
	if (nameobj != null)
		nameobj.innerHTML = namevalue;
}
// -----设置关注人标题
function setConcernedTitle(namevalue) {
	var nameobj = document.getElementById("concernedtitle");
	if (nameobj != null)
		nameobj.innerHTML = namevalue;
}
// -----设置计划状态
function setPlanStatus(status, statusdesc) {
	// 是否显示发布按钮
	var nameobj = document.getElementById("btnPublish");
	if (status == "" || status == "0" || status == "3") {// 显示发布按钮
		nameobj.style.display = "block";
		wpm.btn_publish_visible = true;
	} else {
		nameobj.style.display = "none";
		wpm.btn_publish_visible = false;
	}
	// 发布状态
	var nameobj = document.getElementById("planstatus");
	nameobj.innerHTML = statusdesc;
	// 是否显示批准、退回按钮
	var approveobj = document.getElementById("planapprove");
	var rejectobj = document.getElementById("planreject");
	var div_planstatus = document.getElementById("div_planstatus");
	// 登录人是上级或指派的审批人，且计划是报批状态时，显示批准按钮  chent modify 20171218 
	if ((wpm.my_direct_sub_people || wpm.is_p0733) && status == "1") {
		approveobj.style.display = "block";
		rejectobj.style.display = "block";
		div_planstatus.style.display = "none";
	} else {
		approveobj.style.display = "none";
		rejectobj.style.display = "none";
		div_planstatus.style.display = "block";
	}
}
/**
 * 显示人力地图类型下拉箭头
 */
function displayConcernedImg(dispaly_concerned_img) {
	var obj = document.getElementById("concernedimg");
	var objtitle = document.getElementById("concernedtitle");
	if (dispaly_concerned_img == "true") {

		obj.style.display = "inline";
		objtitle.style.cursor = "pointer";
		Ext.get("concernedtitle").on("onclick", dropdownAttentionMenu);
	} else {
		obj.style.display = "none";
		objtitle.style.cursor = "";
		Ext.get("concernedtitle").un("onclick", dropdownAttentionMenu);
	}
}
function displayTeamListImg(dispaly_concerned_img) {
	var obj = document.getElementById("teamlistimg");
	if (obj != null) {
		if (dispaly_concerned_img == "true") {
			obj.style.display = "inline";
		} else {
			obj.style.display = "none";
		}
	}
}
/*
 * 返回上一页面
 */
function returnLast() {
	var box = document.getElementById("returnurl");
	if (box != null) {
		url = getDecodeStr(box.value);
		location.href = url;
	}

}

function initDefault() {
	// 隐藏选人框
	var box = document.getElementById("selectPerson");
	if (box != null) {
		box.style.display = "none";
	}
	var box = document.getElementById("dropdownBox");
	if (box != null) {
		box.style.display = "none";
	}
	var box = document.getElementById("returnurl");
	if ((box != null) && (box.value != "")) {
		var box = document.getElementById("btn_return");
		if (box != null) {
			box.style.display = "block";
		}
	}
	//“下属计划”按钮状态设置  start 
	var hideSubTask = Ext.getDom("hideSubTask");
	var showSubtask = Ext.getDom("showSubtask");
	if(wpm.showSubTask == "1"){
		if(showSubtask)
			showSubtask.style.display = "none";
		if(hideSubTask)
			hideSubTask.style.display = "inline";
	}else{
		if(showSubtask)
			showSubtask.style.display = "inline";
		if(hideSubTask)
			hideSubTask.style.display = "none";
	}
	//“下属计划”按钮状态设置  end
}
/**
 * 从jsp中获取默认公用变量
 */
function setJsVar() {
	var box = document.getElementById("periodtype");
	if (box != null) {
		wpm.period_type = box.value;
	}
	var box = document.getElementById("periodyear");
	if (box != null) {
		wpm.period_year = box.value;
	}
	var box = document.getElementById("periodmonth");
	if (box != null) {
		wpm.period_month = box.value;
		if (wpm.period_type == "4") {// 保存本次月份
			wpm.old_period_month = wpm.period_month;
		}
	}
	var box = document.getElementById("periodweek");
	if (box != null) {
		wpm.period_week = box.value;
	}
	var box = document.getElementById("fromflag");
	if (box != null) {
		wpm.from_flag = box.value;
	}
	var box = document.getElementById("task_path");
	if (box != null) {
		if (box.value != "")
			wpm.task_path = getDecodeStr(box.value);
	}
	var box = document.getElementById("returnurl");
	if (box != null) {
		wpm.return_url = box.value;
	}
	var box = document.getElementById("p0723");
	if (box != null) {
		wpm.p0723 = box.value;
	}
	var box = document.getElementById("p0700");
	if (box != null) {
		wpm.p0700 = box.value;
	}
	var box = document.getElementById("objectid");
	if (box != null) {
		wpm.object_id = box.value;
	}
	var box = document.getElementById("deptleader");
	if (box != null) {
		wpm.deptleader = box.value;
	}
	var box = document.getElementById("plandesign");
	if (box != null) {
		wpm.plan_design = "1";
		// 废掉计划跟踪,直接传1
		// wpm.plan_design=box.value;
	}
	var box = document.getElementById("subobjectid");
	if (box != null) {
		wpm.sub_object_id = box.value;
	}
	var box = document.getElementById("subpersonflag");
	if (box != null) {
		wpm.sub_person_flag = box.value;
	}
	var box = document.getElementById("concerned_bteam");
	if (box != null) {
		wpm.concerned_bteam = box.value;
	}
	var box = document.getElementById("concerned_cur_page");
	if (box != null) {
		wpm.concerned_cur_page = box.value;
	}
	var box = document.getElementById("superconcernedjson");
	if (box != null) {
		var info = box.value;
		if (info != "") {
			info = getDecodeStr(info);
			for (var i = 0; i < info.length; i++) {
				if (info.indexOf("\＂") > 0) {
					info = info.replace("\＂", "\"");
				}
			}
			// var obj = eval("("+info+")");
			var obj = Ext.decode(info);
			wpm.super_concerned_objs = obj.superinfo;
		}
	}
}

// 初始js jsp变量
function setJspVarAndJsVar(planobj) {
	// 计划期间类型
	var periodtype = planobj.period_type;
	divobj = document.getElementById("periodtype");
	divobj.value = periodtype;
	wpm.period_type = periodtype;

	// 计划期间年
	var year = planobj.period_year;
	divobj = document.getElementById("periodyear");
	divobj.value = year;
	wpm.period_year = year;
	// 计划期间月
	var period_month = planobj.period_month;
	divobj = document.getElementById("periodmonth");
	divobj.value = period_month;
	wpm.period_month = period_month;
	// 计划期间周
	var period_week = planobj.period_week;
	divobj = document.getElementById("periodweek");
	divobj.value = period_week;
	wpm.period_week = period_week;

	wpm.old_period_month = wpm.period_month;
	displayWeekLabel();

	// 计划期间类型描述
	var period_type_name = planobj.period_type_name;
	setPeriodTypeName(period_type_name);
	// 计划期间描述
	setPeriodName(wpm.period_year, wpm.period_month);
}
/**
 * 初始界面
 */
function initform(curjsp) {
	wpm.curjsp = curjsp;
	
	//从缓存中取出  haosl start
	var curUsername = document.getElementById("curUsername");
	//团队成员页面无需此处操作   haosl 2017年12月22日
	if(curUsername){
		var name = getEncodeStr(curUsername.value);
		var cookieValue = getCookie("workplan_subtask_"+name);
		if(!Ext.isEmpty(cookieValue))
			wpm.showSubTask = cookieValue;//是否显示下属任务 =0 不显示 =1 显示
	}
	//从缓存中取出  haosl end
	wpm.isMobileBrowser = isMobileBrowser();//是否是移动端访问
	initDefault();

	setJsVar();

	initPlan();
}

function initPlan() {
	var hashvo = new ParameterSet();
	hashvo.setValue("oprType", "getPlanInfo");
	hashvo.setValue("p0723", wpm.p0723);
	hashvo.setValue("periodType", wpm.period_type);
	if ((wpm.curjsp == "selfplan") && (wpm.period_type == "")) {// 默认进来
																// 从cookie里面取上次使用的计划类型
		var strCookie = document.cookie;
		var arrCookie = strCookie.split(";");
		var valuecookie = "";
		curUsername = document.getElementById("curUsername");
		if (curUsername != null) {
			var name = getEncodeStr(curUsername.value);
			for (var i = 0; i < arrCookie.length; i++) {
				var arr = arrCookie[i].split("=");
				if (arr[0].indexOf("workplan_" + name) > -1) {
					valuecookie = arr[1];
					break;
				}
			}
		}
		if (valuecookie != "") {
			hashvo.setValue("cookie_periodType", valuecookie);
		}
	}
	hashvo.setValue("periodYear", wpm.period_year);
	hashvo.setValue("periodMonth", wpm.period_month);
	hashvo.setValue("periodWeek", wpm.period_week);
	hashvo.setValue("objectId", wpm.object_id);
	hashvo.setValue("p0700", wpm.p0700);

	hashvo.setValue("deptLeader", wpm.deptleader);
	hashvo.setValue("curjsp", wpm.curjsp);

	hashvo.setValue("concerned_bteam", wpm.concerned_bteam);
	hashvo.setValue("subobjectid", wpm.sub_object_id);
	hashvo.setValue("subpersonflag", wpm.sub_person_flag);
	hashvo.setValue("concerned_cur_page", wpm.concerned_cur_page);
	hashvo.setValue("fromflag", wpm.from_flag);
	hashvo.setValue("planType", wpm.planType);
	
	var box = document.getElementById("needcheck");
	if (box != null) {
		hashvo.setValue("needcheck", box.value);
	}
	var request = new Request({
				method : 'post',
				asynchronous : false,
				onSuccess : init_ok,
				onFailure : faileFun,
				functionId : '9028000702'
			}, hashvo);

}

function init_ok(outparamters) {
	var strinfo = outparamters.getValue("info");
	strinfo = getDecodeStr(strinfo);
	if (strinfo == "false") {
		var url = "/workplan/work_plan.do?br_query=link&type="+wpm.planType+"&objectid=" + "";
		location.href = url;
		return;
	}
	
	// 填报期间范围权限 chent 20170112 start
	var plan_cycle_function = outparamters.getValue("plan_cycle_function");
	plan_cycle_function = getDecodeStr(plan_cycle_function);
	wpm.cycle_function = Ext.decode(plan_cycle_function);
	var display = false;
	for(var p in wpm.cycle_function){
		var obj = wpm.cycle_function[p];
		if(obj.p1){//半年计划隐藏“上半年”、“下半年”
			var cycle = ',' + obj.p1.cycle + ',';
			for(var i=1; i<=2; i++){
				if(cycle.indexOf(','+i+',') == -1){
					var halfyear = Ext.getDom('halfyear'+(i));
					if(halfyear)
						halfyear.style.display = 'none';
				}
			}
		} else if(obj.p2){//季度计划隐藏“第一季度”、“第二季度”。。
			var cycle = ',' + obj.p2.cycle + ',';
			for(var i=1; i<=4; i++){
				if(cycle.indexOf(','+i+',') == -1){ 
					var quarter = Ext.getDom('quarter'+(i));
					if(quarter)
						quarter.style.display = 'none';
				}
			}
		}
		if(obj.s4)//周报启用
			display = true;
		if(obj.s3)//月报启用
			display = true;
		if(obj.s2)//季度总结启用
			display = true;
		if(obj.s1)//半年总结启用
			display = true;
		if(obj.s0)//年度总结启用
			display = true;
	}
	// 填报期间范围权限 chent 20170112 end
	var parentobj = document.getElementById("workReports");
		if(parentobj){
			if(!display){
				parentobj.style.display='none';
			}else{
				parentobj.style.display='inline';
			}
		}
	// 个人填报  自己的  期间范围权限 linbz 
	var personCF = outparamters.getValue("person_cycle_function");
	personCF = getDecodeStr(personCF);
	//当业务用户从监控键入页面时，是没有填报范围的
	if(!Ext.isEmpty(personCF)){
		wpm.person_cycle_function = Ext.decode(personCF);
	}
	// 个人填报  部门的 期间范围权限 linbz
	var orgCF = outparamters.getValue("org_cycle_function");
	orgCF = getDecodeStr(orgCF);
	if(!Ext.isEmpty(orgCF)){
		wpm.org_cycle_function = Ext.decode(orgCF);
	}
    setPriv();

	var obj = Ext.decode(strinfo);
	var planobj = obj.planinfo;
	// 给object_id赋值 lis 20160323 start
	if(wpm.object_id){
		var box = document.getElementById("objectid");
		if (box != null) {
			wpm.object_id = planobj.object_id;
			box.value = planobj.object_id;
		}
	}
	// end
	
	//移动端屏蔽一些功能按钮
	if(wpm.isMobileBrowser){
		Ext.getDom("a_daochu").style.display='none';
		Ext.getDom("a_addMenu").style.display='none';
		Ext.getDom("a_deltask").style.display='none';
		Ext.getDom("a_addfollower").style.display='none';
		var schemeSetting = Ext.getDom("schemeSetting");
		if(schemeSetting)
			schemeSetting.style.display='none';
		Ext.getDom("showSubtask").style.display='none';
		Ext.getDom("hideSubTask").style.display='none';
	}
	var selobj = null;
	// --显示默认负责人
	defaultName = outparamters.getValue("defaultName");
	
	// --公用信息
	setJspVarAndJsVar(planobj);
	// --当右侧人力地图没有数据时，收起人力地图  haosl 20170615
	displayHumanMap(planobj);
	// --本月周数
	wpm.week_num = Number(planobj.week_num);
	displayWeekLabel();
	// --计划信息
	displayPlanInfo(planobj);
}
function faileFun(outparamters) {
	wpm.nopriv = "1";// 非自助用户不能使用该功能。
}
/**
 * 显示人力地图
 */
function displayHumanMap(planobj) {
	// 是否显示人力地图
	// var display_div=human_map.display_human_map=="true";
	if (wpm.from_flag == "email" || wpm.from_flag == "hr"|| wpm.from_flag == "hr_create") {
		var selobj = document.getElementById("rightDiv");
		selobj.style.width = "0px";
		selobj.style.display = "none";
		var selobj = document.getElementById("showRightDiv");
		selobj.style.display = "none";
		var selobj = document.getElementById("leftDiv");
		selobj.style.marginRight = "20px";
		var selobj = document.getElementById("plantype_div");
		selobj.style.display = "none";
		if (wpm.from_flag == "email") {
			var selobj = document.getElementById("one_div");
			// selobj.style.display="none";
			selobj.style.height = "5px";
		}
		return;
	}

	// 我的照片
	var my_image = planobj.my_image;
	selobj = document.getElementById("my_image");
	selobj.style.display='inline';
	selobj.src = my_image;
	// 我的姓名
	// var my_name=planobj.my_name;
	// selobj=document.getElementById("my_name");
	// selobj.title=planobj.my_fullname;
	// selobj.innerHTML=my_name;
	// 显示人力地图
	displayConcerneders(planobj.human_map);
	// email 监控页面进入计划的时候不应该显示人力地图相关内容 haosl 2017-11-22
	if(planobj.human_map && planobj.human_map.concerneders && planobj.human_map.concerneders.length==0)
		hideRightDiv();
	else
		showRightDiv();
}
/**
 * 显示计划信息
 */
function displayPlanInfo(planobj) {
	var objectid = planobj.object_id;
	selobj = document.getElementById("objectid");
	selobj.value = objectid;
	wpm.g_objectid = objectid;

	var myplan = planobj.is_myplan;
	selobj = document.getElementById("ismyplan");
	selobj.value = myplan;

	wpm.myplan = (myplan === "true");
	// 废掉计划跟踪页面
	// if (!wpm.myplan){
	// wpm.plan_design="2";
	// }
	// else {
	if (wpm.plan_design == "") {
		wpm.plan_design = "1";
	}
	// }

	// 计划类型
	var p0723 = planobj.p0723;
	var box = document.getElementById("p0723");
	if (box != null) {
		box.value = p0723;
	}
	wpm.p0723 = p0723;
	// 计划期间
	wpm.p0725 = planobj.p0725;
	if (wpm.curjsp == "selfplan") {
		// 计划人照片
		var plan_owner_image = planobj.plan_owner_image;
		img = document.getElementById("plan_owner_image");
		img.style.display = "inline";
		img.src = plan_owner_image;

		// 计划id
		var p0700 = planobj.p0700;
		var box = document.getElementById("p0700");
		if (box != null) {
			box.value = p0700;
			wpm.p0700 = p0700;
		}

		// 所有者
		var object_id = planobj.object_id;
		divobj = document.getElementById("objectid");
		divobj.value = object_id;
		wpm.object_id = object_id;
		// 部门负责人
		var dept_leader = planobj.dept_leader;
		divobj = document.getElementById("deptleader");
		divobj.value = dept_leader;
		wpm.deptleader = dept_leader;

		// ------是否是我的下级
		if (planobj.is_subplan == "true") {
			wpm.subplan = true;
		} else {
			wpm.subplan = false;
		}
		// ------是否是我的直接下级
		if (planobj.direct_sub_people == "true") {
			wpm.my_direct_sub_people = true;
		} else {
			wpm.my_direct_sub_people = false;
		}
		// ------当前登录人是否是计划指派的审批人 chent add 20171218
		if (planobj.is_p0733 == "true") {
			wpm.is_p0733 = true;
		} else {
			wpm.is_p0733 = false;
		}
		// 计划状态
		var plan_status = planobj.plan_status;
		var plan_status_desc = planobj.plan_status_desc;
		setPlanStatus(plan_status, plan_status_desc);
		// 计划可见范围
		var plan_scope_desc = planobj.plan_scope_desc;
		setPlanScopeName(plan_scope_desc);

		// 计划标题
		var plan_title = planobj.plan_title;
		setPlanTitle(plan_title);
		initMessageContentlist("1", wpm.p0700);

		// ------关注我的
		displayFollower(planobj.follower);
		var lbl = document.getElementById("lbl_follower");
		lbl.innerHTML = "关注" + planobj.follow_owner + "计划的：";
		// ------获取任务信息
		getPlanTaskList();
		// setTimeout('getPlanTaskList()', 500);

	} else {
		// 计划标题
		var plan_title = planobj.plan_title;
		setPlanTitle(plan_title);
		wpm.teaminfo = planobj.teaminfo;
		displayTeamInfo("", planobj.teaminfo);

	}
	//填报权限复制，用于计划详情页判断权限
	var validFlag = privCheck("false");
	wpm.personCF_ = validFlag=="1"?"false":"true";
	wpm.isCanFill_ = validFlag=="2"?"false":"true";
}

/**
 * 删除关注人
 */
function delFollower(objectid) {
	var hashvo = new ParameterSet();
	hashvo.setValue("oprType", "delFollower");
	hashvo.setValue("plan_id", wpm.p0700);
	hashvo.setValue("followerId", objectid);
	var request = new Request({
				method : 'post',
				asynchronous : true,
				onSuccess : delFollower_ok,
				functionId : '9028000702'
			}, hashvo);

}

function delFollower_ok(outparamters) {
	var objectid = outparamters.getValue("followerId");
	var info = outparamters.getValue("info");
	if (info == "true") {
		var delNode = document.getElementById("follow_" + objectid);
		var followerNode = delNode.parentNode.parentNode;
		document.getElementById("followerdiv").removeChild(followerNode);
	}

}
/**
 * 显示关注人
 */
function displayFollower(worklist) {
	var strhtml = "";
	for (var i = 0; i < worklist.length; i++) {
		strhtml = strhtml + getDisplayFollowerItem(worklist[i]);
	}
	var selobj = document.getElementById("followerdiv");
	selobj.innerHTML = strhtml;

}

function photoshow(obj) {
	obj.getElementsByTagName("div")[0].style.display = 'block';

}

function photohide(obj) {
	obj.getElementsByTagName("div")[0].style.display = 'none';

}

function getDisplayFollowerItem(item) {
	var html = "<dl   style='margin-right:20px;margin-bottom:8px' "
	if (wpm.myplan || wpm.subplan) {
		html += "onmouseout='photohide(this)' onmouseover='photoshow(this)'"
	}
	html += ">"
	html += "<dt title='" + item.fullname + "'>";
	html += '<img class="img-circle" src="' + item.imagepath + '" />';
	html += '<div style="z-index:666;position:absolute;margin-top:-40px;width:20px;display:none;height:20px;margin-left:26px;cursor:pointer;"';
	html += ' id="' + "follow_" + item.objectid + '" onclick='
			+ "'delFollower(\"" + item.objectid + '"' + ")'>"
	html += '<img style="width:20px;height:20px;" src="/workplan/image/remove.png">';
	html += '</div>';
	html += '</dt>';
	html += '<dd style="font-size:11px;">' + item.name + '</dd>';
	html += "</dl>";
	return html;
}

function loadMyPlan() {
	wpm.plan_design = "1";
	wpm.deptleader='';
	needRefresh = "no";
	refreshPlan("")
	needRefresh = "yes";
}

/*
 * 选择右侧周、季度、半年标签后 改变选中样式
 */
function checkedWeek(periodvalue) {
	var el = [];
	if (wpm.period_type == "2") {// 半年
		el = Ext.query("#div_halfyears a");
	} else if (wpm.period_type == "3") {// 季度
		el = Ext.query("#div_quaters a");
	} else if (wpm.period_type == "5") {// 月
		el = Ext.query("#div_weeks a");
	}
	for (var i = 0; i < el.length; i++) {
		el[i].className = "";
	}

	var j = 0;
	j = Number(periodvalue) - 1;
	el[j].className = "hj-wzm-or-a";
}

/*
 * 是否显示右侧周、半年、季度标签
 */
function displayWeekLabel() {
	// 显示周、季度、半年的标签 并选中标签
	hideObj('div_halfyears');
	hideObj('div_quaters');
	hideObj('div_weeks');

	if (wpm.period_type == '1') {// 年

	} else if (wpm.period_type == '2') {// 半年
		displayObj("div_halfyears");
		checkedWeek(wpm.period_month);
	} else if (wpm.period_type == '3') {// 季度
		displayObj("div_quaters");
		checkedWeek(wpm.period_month);
	} else if (wpm.period_type == '4') {// 月

	} else if (wpm.period_type == '5') {// 周
		displayObj("div_weeks");
		if (wpm.week_num == 5) {
			Ext.get("fiveweek").show();
			Ext.getDom("div_weeks").style.width = "300px";
		} else {
			Ext.get("fiveweek").hide();
			Ext.getDom("div_weeks").style.width = "240px";
		}
		checkedWeek(wpm.period_week);
	}

}
/**
 * 获取cookie
 * 
 * @param {}
 *            name
 * @return {}
 */
function getCookie(name) 
{ 
    var arr,reg=new RegExp("(^| )"+name+"=([^;]*)(;|$)");
 
    if(arr=document.cookie.match(reg))
 
        return unescape(arr[2]); 
    else 
        return null; 
} 


/**
 * 删除cookie
 * 
 * @param {}
 *            name
 */
function delCookie(name) 
{ 
    var exp = new Date(); 
    exp.setTime(exp.getTime() - 1); 
    var cval=getCookie(name);
    if(cval!=null) 
        document.cookie= name + "="+cval+";expires="+exp.toGMTString(); 
} 

/*
 * 切换期间类型
 */
function selectPeriodType(periodvalue) {
	var selobj = document.getElementById(periodvalue);
	var periodTypeName = selobj.innerHTML;
	changePeriodParam(true, periodvalue, wpm.period_year, wpm.old_period_month,
			wpm.period_week);
	if (wpm.b_result) {
		// 设置期间类型描述
		if (wpm.myplan) {// 放入cookie
			curUsername = document.getElementById("curUsername");
			if (curUsername != null) {
				var name = getEncodeStr(curUsername.value);
				delCookie("workplan_" + name);
				// 存cookie需要设置有效期
				var Days = 30;
				var exp = new Date();
				exp.setTime(exp.getTime() + Days * 24 * 60 * 60 * 1000 * 6);
				document.cookie = "workplan_" + name + "=" + periodvalue
						+ ";expires=" + exp.toGMTString();
			}
		}
		setPeriodTypeName(periodTypeName);
		setPeriodName(wpm.period_year, wpm.period_month);// 设置计划期间
		displayWeekLabel();// 显示右侧季度标签
		loadPlanByAjax(wpm.object_id, "true");
	}

}

/*
 * 切换年度，
 */
function selectPeriodYear(periodvalue) {
	var bLocationCurWeek = false;
	if (wpm.period_type == "1") {// 年、半年、季度
		;
	} else if (wpm.period_type == "2" || wpm.period_type == "3") {
		bLocationCurWeek = true;
	} else {
		return;
	}
	var selobj = document.getElementById(periodvalue);
	var periodname = selobj.innerHTML;

	changePeriodParam(bLocationCurWeek, wpm.period_type, periodvalue,
			wpm.period_month, wpm.period_week);
	if (wpm.b_result) {
		setPeriodName(wpm.period_year, wpm.period_month);
		var curmenuvalue = document.getElementById("periodyear");
		curmenuvalue.value = periodvalue;
		wpm.period_year = periodvalue + "";
		displayWeekLabel();
		loadPlanByAjax(wpm.object_id, "true");
	}
}
/*
 * 选择月份，只有月、周计划才可选
 */
function selectPeriodMonth(periodmonth) {
	var bLocationCurWeek = false;
	if (wpm.period_type == "4") {// 月、周
		bLocationCurWeek = false;
	} else if (wpm.period_type == "5") {// 月、周
		bLocationCurWeek = true;
	} else {
		return;
	}
	var periodyear = Ext.getDom("myeartitle").innerHTML;
	changePeriodParam(bLocationCurWeek, wpm.period_type, periodyear,
			periodmonth, wpm.period_week);
	if (wpm.b_result) {
		setPeriodName(periodyear, periodmonth);
		displayWeekLabel();
		loadPlanByAjax(wpm.object_id, "true");
	}
}
/*
 * 选择右侧季度、半年、周标签事件
 */
function selectPeriodWeek(periodvalue) {
	basic.global.logonOut();
	if (wpm.period_type == "2" || wpm.period_type == "3") {// 半年、季度
		changePeriodParam(false, wpm.period_type, wpm.period_year, periodvalue,
				wpm.period_week);
	} else if (wpm.period_type == "5") {// 周
		changePeriodParam(false, wpm.period_type, wpm.period_year,
				wpm.period_month, periodvalue);
	} else {
		return;
	}

	if (wpm.b_result) {

		loadPlanByAjax(wpm.object_id, "true");
		checkedWeek(periodvalue);

	}
}

/*
 * 检查是否有查看所选期间的权限 bchangetype：true
 * 是否需要定位当前期间，如果选当前年份，自动定位当前月、季度、半年，如果选当前月份，自动定位当前周 periodtype:期间类型
 * periodyear:期间年
 */
function changePeriodParam(bLocationCurWeek, periodtype, periodyear,
		periodmonth, periodweek) {	
	var locationCurWeek = "false";
	if (bLocationCurWeek)
		locationCurWeek = "true";
	var hashvo = new ParameterSet();
	hashvo.setValue("oprType", "checkIsCanReadPlan");
	hashvo.setValue("locationCurWeek", locationCurWeek);
	hashvo.setValue("periodType", periodtype);
	hashvo.setValue("periodYear", periodyear);
	hashvo.setValue("periodMonth", periodmonth);
	hashvo.setValue("periodWeek", periodweek);
	hashvo.setValue("objectId", wpm.object_id);
	hashvo.setValue("curjsp", wpm.curjsp);
	hashvo.setValue("deptLeader", wpm.deptleader);
	hashvo.setValue("p0723", wpm.p0723);
	hashvo.setValue("fromflag", wpm.from_flag);
	wpm.b_result = false;
	var request = new Request({
				method : 'post',
				asynchronous : false,
				onSuccess : changePeriodParam_ok,
				functionId : '9028000702'
			}, hashvo);
}

/*
 * 
 */
function changePeriodParam_ok(outparamters) {
	hideDropdownBox();
	var strinfo = outparamters.getValue("info");
	if (strinfo == "true") {
		wpm.b_result = true;
		var periodtype = outparamters.getValue("periodType");
		document.getElementById("periodtype").value = periodtype;
		wpm.period_type = periodtype;
		var periodyear = outparamters.getValue("periodYear");
		document.getElementById("periodyear").value = periodyear;
		wpm.period_year = periodyear;
		var periodmonth = outparamters.getValue("periodMonth");
		document.getElementById("periodmonth").value = periodmonth;
		wpm.period_month = periodmonth;
		var periodweek = outparamters.getValue("periodWeek");
		document.getElementById("periodweek").value = periodweek;
		wpm.period_week = periodweek;
		var weeknum = outparamters.getValue("weekNum");
		wpm.week_num = weeknum;
		if (wpm.period_type == "4") {// 保存本次月份
			wpm.old_period_month = wpm.period_month;
		}
	} else {
		Ext.Msg.alert("提示信息", "你没有权限查看当前期间的计划");
	}
}
/*
 * 返回我的团队列表，
 */
function backMyTeam() {
	var url = "/workplan/work_track.do?br_query=link&objectid="
			+ "&subobjectid=" + wpm.sub_object_id + "&subpersonflag="
			+ wpm.sub_person_flag + "&periodtype=" + wpm.period_type
			+ "&periodyear=" + wpm.period_year + "&periodmonth="
			+ wpm.period_month + "&periodweek=" + wpm.period_week
			+ "&deptleader=" + wpm.deptleader + "&concerned_bteam="
			+ wpm.concerned_bteam + "&concerned_cur_page="
			+ wpm.concerned_cur_page + '&superconcernedjson='
			+ $URL.encode(get_super_concerned_json()) + "&type="+wpm.planType;
	location.href = url;
}

function refreshPlan(objectid) {
	loadPlan(objectid, "false");
}

/*
 * objectid:计划所有者 bRefHummanMap:是否刷新人力地图导航栏
 */
function loadPlan(objectid, bRefHummanMap) {
	if (wpm.curjsp == "teamplan") {
		var url = "/workplan/work_plan.do?br_query=link&objectid="
				+ objectid + "&p0723=" + wpm.p0723 + "&subobjectid="
				+ wpm.sub_object_id + "&subpersonflag=" + wpm.sub_person_flag
				+ "&periodtype=" + wpm.period_type + "&periodyear="
				+ wpm.period_year + "&periodmonth=" + wpm.period_month
				+ "&periodweek=" + wpm.period_week + "&deptleader="
				+ wpm.deptleader + "&concerned_bteam=" + wpm.concerned_bteam
				+ "&concerned_cur_page=" + wpm.concerned_cur_page
				+ '&superconcernedjson=' + $URL.encode(get_super_concerned_json()) +"&type="+wpm.planType;
		location.href = url;
		return;
	}
	loadPlanByAjax(objectid, bRefHummanMap);
}
/*
 * objectid:计划所有者 bRefHummanMap:是否刷新人力地图导航栏
 */
function loadPlanByAjax(objectid, bRefHummanMap) {
	var hashvo = new ParameterSet();
	hashvo.setValue("needRefresh", needRefresh);
	hashvo.setValue("oprType", "refreshPlanInfo");
	hashvo.setValue("periodType", wpm.period_type);
	hashvo.setValue("bRefHummanMap", bRefHummanMap);
	hashvo.setValue("periodYear", wpm.period_year);
	hashvo.setValue("periodMonth", wpm.period_month);
	hashvo.setValue("periodWeek", wpm.period_week);
	hashvo.setValue("objectId", objectid);
	hashvo.setValue("curjsp", wpm.curjsp);
	hashvo.setValue("deptLeader", wpm.deptleader);
	hashvo.setValue("p0723", wpm.p0723);
	hashvo.setValue("subobjectid", wpm.sub_object_id);
	hashvo.setValue("subpersonflag", wpm.sub_person_flag);
	hashvo.setValue("cur_page", wpm.concerned_cur_page);
	hashvo.setValue("concerned_bteam", wpm.concerned_bteam);
	hashvo.setValue("planType", wpm.planType);//haosl 20161128
	var request = new Request({
				method : 'post',
				asynchronous : true,
				onSuccess : refreshPlan_ok,
				functionId : '9028000702'
			}, hashvo);
}

function refreshPlan_ok(outparamters) {

	// 调用处理登录超时的方法
	basic.global.logonOut();
	var strinfo = outparamters.getValue("info");
	strinfo = getDecodeStr(strinfo);
	// var obj = eval("("+strinfo+")");
	var obj = Ext.decode(strinfo);
	// ------获取计划信息
	wpm.task_path = "";
	// --显示默认负责人
	defaultName = outparamters.getValue("defaultName");
	var contentObj = document.getElementById("content-add-member");
	if (contentObj) {
		contentObj.innerHTML = '';
		var item = {
			id : '',
			name : defaultName
		};
		var strhtml = getDisplayParticipantItem(item);
		contentObj.innerHTML = strhtml;
	}

	var planobj = obj.planinfo;
	displayPlanInfo(planobj);
	// 刷新期间时 需更新人力地图
	var bRefHummanMap = outparamters.getValue("bRefHummanMap");
	if (bRefHummanMap != null && bRefHummanMap == "true") {
		displayConcerneders(planobj.human_map);
	}
	
	// 个人填报  自己的  期间范围权限 linbz 
	var personCF = outparamters.getValue("person_cycle_function");
	personCF = getDecodeStr(personCF);
	//当业务用户从监控键入页面时，是没有填报范围的
	if(!Ext.isEmpty(personCF)){
		wpm.person_cycle_function = Ext.decode(personCF);
	}
	// 个人填报  部门的 期间范围权限 linbz
	var orgCF = outparamters.getValue("org_cycle_function");
	orgCF = getDecodeStr(orgCF);
	if(!Ext.isEmpty(orgCF)){
		wpm.org_cycle_function = Ext.decode(orgCF);
	}
    setPriv();
}

function add_super_concerned(objectid, curpage, flag) {
	var bfind = false;
	if (wpm.super_concerned_objs.length > 0) {
		for (var i = 0; i < wpm.super_concerned_objs.length; i++) {
			var obj = wpm.super_concerned_objs[i];
			if (obj.objectid == objectid) {
				obj.curpage = curpage;
				obj.flag = flag;
				bfind = true;
				break;
			}
		}
	}
	if (!bfind) {
		var obj = new Object();
		obj.objectid = objectid;
		obj.curpage = curpage;
		obj.flag = flag;
		wpm.super_concerned_objs.push(obj);
	}
	var selobj = document.getElementById("backSuperDiv");

	if (wpm.super_concerned_objs.length > 1) {
		selobj.style.display = "block";
	} else {
		selobj.style.display = "none";
	}
}

function del_last_super_concerned(objectid, curpage) {
	if (wpm.super_concerned_objs.length > 1) {
		wpm.super_concerned_objs.pop();
	}
}

function backSuper() {
	del_last_super_concerned();
	if (wpm.super_concerned_objs.length > 0) {
		if(wpm.concerned_bteam=="4" && wpm.super_concerned_objs.length==1){
			wpm.cTeam_needSee = false;
			selectHummanMapType(4);
		}else{
		var obj = wpm.super_concerned_objs[wpm.super_concerned_objs.length - 1];
		clickSubPeople(obj.objectid, obj.curpage, obj.flag);
		}
		
	}
}

function get_super_concerned_json() {
	var str = "";
	for (var i = 0; i < wpm.super_concerned_objs.length; i++) {
		var obj = wpm.super_concerned_objs[i];
		var stritem = "{" + '"objectid":"' + obj.objectid + '"' + ","
				+ '"curpage":"' + obj.curpage + '"' + "," + '"flag":"'
				+ obj.flag + '"' + "}";
		if (str == "") {
			str = stritem;
		} else {
			str = str + "," + stritem
		}
	}
	str = "{\"" + "superinfo\"" + ":[" + str + "]}";
	str = getEncodeStr(str);
	return str;
}
function displayConcerneders(human_map) {
	var selobj = document.getElementById("backSuperDiv");
	// 是否显示人力地图下拉图片
	if (wpm.curjsp == "selfplan" && human_map.display_dropdown_img != "") {
		var display_img = human_map.display_dropdown_img;
		displayConcernedImg(display_img);
	}
	// 是否显示切换团队列表图片
	if (human_map.display_team_list_img != null) {
		var display_img = human_map.display_team_list_img;
		displayTeamListImg(display_img);
	}
	wpm.concerned_bteam = human_map.concerned_bteam;
	wpm.concerned_cur_page = human_map.concerned_cur_page;
	var concerned_title = human_map.concerned_title;
	var add_super_flag = false;
	if (wpm.concerned_bteam == "2" || wpm.concerned_bteam == "3") {
		add_super_flag = true;
		if(wpm.concerned_bteam == "2")
			wpm.planType="person";
		else
			wpm.planType="org";
		wpm.sub_object_id = human_map.concerned_objectid;
		wpm.sub_person_flag = human_map.isperson;
		add_super_concerned(wpm.sub_object_id, wpm.concerned_cur_page,
				human_map.isperson);
		if (wpm.concerned_bteam == "2")
			concerned_title = concerned_title;
		else
			concerned_title = concerned_title;
		concerned_title = concerned_title;

	} else {
		if(wpm.concerned_bteam == "1")
			wpm.planType="org";
		selobj.style.display = "none";
		concerned_title = concerned_title;
		if(wpm.concerned_bteam == "4"){
			concerned_title = "我关注的";
			wpm.planType="person";
		}
	}
	setConcernedTitle(concerned_title);
	var worklist = human_map.concerneders;
	var teamPageNun = 1;
	if (worklist.length > 0) {
		teamPageNun = parseInt(worklist[0].totalPageNum);
	}
	var strhtml = "";
	var xxiaEle = document.getElementById("xxiajpg");
	if (worklist.length < 8 || teamPageNun == wpm.concerned_cur_page) {
		if (xxiaEle) {
			xxiaEle.style.display = "none";
		}
	} else {
		if (xxiaEle) {
			xxiaEle.style.display = "inline";
			xxiaEle.style.margin = "0 0 0 55px";
		}
	}
	var justOnce = false;
	for (var i = 0; i < worklist.length; i++) {
		if (wpm.concerned_bteam == "3") { // 下属部门
			strhtml = strhtml + "<dl><dt>";
			if (worklist[i].isleader == "true") {
				strhtml = strhtml
						+ "<a  href=\"javascript:void(0)\" onclick='clickTeamDept(\""
						+ worklist[i].objectid + "\",\"" + worklist[i].e0122
						+ "\",\"" + worklist[i].p0723 + "\",\""+wpm.planType+"\")' ";
			} else {
				strhtml = strhtml + "<a"
			}
			strhtml = strhtml + " onmouseover='hintMsg(\""
					+ worklist[i].hintinfo + "\")'  onmouseout='tt_HideInit()'"
					+ ">"
			strhtml = strhtml + "<img class=\"img-circle\" src='"
					+ worklist[i].imagepath + "'/></a></dt>";
			// strhtml =strhtml +"<dd>"+worklist[i].name+"</dd>"
			strhtml = strhtml + "<dd>" + worklist[i].e0122desc + "</dd>";
			if (worklist[i].subpeople != "") {
				strhtml = strhtml
						+ "<dd>查看 <a href=\"javascript:void(0)\" onclick='clickSubPeople(\""
						+ worklist[i].e01a1 + "\",\"1\",\"true\")'>"
						+ worklist[i].subpeople + "</a></dd>"
			}
		} else if (wpm.concerned_bteam=="2" || worklist[i].clientName=='HJSJ') {// 团队成员
			if(worklist[i].cTeam=='true'){
				wpm.cTeam_needSee = true;
				selobj.style.display = "block";
			}
			if(worklist[i].clientName=='HJSJ' && !add_super_flag && !justOnce){
				justOnce = true;
				wpm.sub_object_id = human_map.concerned_objectid;
				wpm.sub_person_flag = human_map.isperson;
				add_super_concerned(wpm.sub_object_id, wpm.concerned_cur_page,
				human_map.isperson);
			}
			if (worklist[i].flag == "true") {
				if(worklist[i].canDelete == "true"){
					strhtml = strhtml + "<dl   ";
					if (worklist[i].canDelete == "true") {
						strhtml += "onmouseout='photohide(this)' onmouseover='photoshow(this)'"
					}
					strhtml += ">";
					strhtml = strhtml + "<dt><a ";
					strhtml = strhtml + " onmouseover='hintMsg(\""
							+ worklist[i].hintinfo + "\")'  onmouseout='tt_HideInit()'"
					strhtml = strhtml
							+ " href=\"javascript:void(0)\" onclick='clickConcerneders(\""
							+ worklist[i].objectid + "\",\"" + worklist[i].dept_leader
							+ "\",\"" + worklist[i].p0723
							+ "\",\"" + wpm.planType
							+ "\")'><img class=\"img-circle\" src='"
							+ worklist[i].imagepath + "'/></a>";
					strhtml += '<div style="z-index:666;position:absolute;margin-top:-60px;width:20px;height:20px;display:none;margin-left:90px;cursor:pointer;border:none;background:none;"';
					strhtml += ' id="' + "follow_" + worklist[i].objectid
							+ '" onclick=' + "'delConcerneder(\""
							+ worklist[i].objectid + "\",\"" + worklist[i].p0723
							+ "\")'>"
					strhtml += '<img style="width:20px;height:20px;border-style:none;border-width:0;" src="/workplan/image/remove.png">';
					strhtml += '</div>';
		
					strhtml += "</dt>"
					strhtml = strhtml + "<dd>" + worklist[i].name + "</dd>"
				}else{
						strhtml = strhtml
						+ "<dl><dt><a onmouseover='hintMsg(\""
						+ worklist[i].hintinfo
						+ "\")'  onmouseout='tt_HideInit()'  href=\"javascript:void(0)\" onclick='clickTeamPeople(\""
						+ worklist[i].objectid + "\",\"" + worklist[i].p0723
						+ "\")'><img class=\"img-circle\" src='"
						+ worklist[i].imagepath + "'/></a></dt>";
						strhtml = strhtml + "<dd>" + worklist[i].name + "</dd>"
				}
				if (worklist[i].subpeople != "") {
					strhtml = strhtml
							+ "<dd>查看 <a href=\"javascript:void(0)\" onclick='clickSubPeople(\""
							+ worklist[i].objectid + "\",\"1\",\""
							+ worklist[i].flag + "\",\"yes\")'>"
							+ worklist[i].subpeople + "</a></dd>"
				}
			} else {
				strhtml = strhtml
						+ "<dl><dt><a onmouseover='hintMsg(\""
						+ worklist[i].hintinfo
						+ "\")'  onmouseout='tt_HideInit()'  href=\"javascript:void(0)\"'><img class=\"img-circle\" src='"
						+ worklist[i].imagepath + "'/></a></dt>";
				strhtml = strhtml + "<dd>" + worklist[i].name + "</dd>"
				if (worklist[i].subpeople != "") {
					strhtml = strhtml
							+ "<dd>查看 <a href=\"javascript:void(0)\" onclick='clickSubPeople(\""
							+ worklist[i].objectid + "\",\"1\",\""
							+ worklist[i].flag + "\")'>"
							+ worklist[i].subpeople + "</a></dd>"
				}
			}

		} else if (wpm.concerned_bteam == "1") {
			strhtml = strhtml + "<dl><dt><a ";
			if (wpm.concerned_bteam == "4") {
				strhtml = strhtml + " onmouseover='hintMsg(\""
						+ worklist[i].hintinfo
						+ "\")'  onmouseout='tt_HideInit()'"
			} else {
				strhtml = strhtml + " onmouseover='tt_HideInit()'"
			}
			strhtml = strhtml
					+ " href=\"javascript:void(0)\" onclick='clickConcerneders(\""
					+ worklist[i].objectid + "\",\"" + worklist[i].dept_leader
					+ "\",\"" + worklist[i].p0723
					+ "\",\""+wpm.planType+"\")'><img class=\"img-circle\" src='"//haosl 20161129
					+ worklist[i].imagepath + "'/></a></dt>"
			strhtml = strhtml + "<dd>" + worklist[i].name + "</dd>"
		} else if (wpm.concerned_bteam == "4" && worklist[i].clientName!='HJSJ') {
			strhtml = strhtml + "<dl   ";
			if (worklist[i].canDelete == "true") {
				strhtml += "onmouseout='photohide(this)' onmouseover='photoshow(this)'"
			}
			strhtml += ">";
			strhtml = strhtml + "<dt><a ";
			strhtml = strhtml + " onmouseover='hintMsg(\""
					+ worklist[i].hintinfo + "\")'  onmouseout='tt_HideInit()'"
			strhtml = strhtml
					+ " href=\"javascript:void(0)\" onclick='clickConcerneders(\""
					+ worklist[i].objectid + "\",\"" + worklist[i].dept_leader
					+ "\",\"" + worklist[i].p0723
					+ "\",\""+wpm.planType+"\")'><img class=\"img-circle\" src='"
					+ worklist[i].imagepath + "'/></a>";
			strhtml += '<div style="z-index:666;position:absolute;margin-top:-60px;width:20px;height:20px;display:none;margin-left:90px;cursor:pointer;border:none;background:none;"';
			strhtml += ' id="' + "follow_" + worklist[i].objectid
					+ '" onclick=' + "'delConcerneder(\""
					+ worklist[i].objectid + "\",\"" + worklist[i].p0723
					+ "\")'>"
			strhtml += '<img style="width:20px;height:20px;border-style:none;border-width:0;" src="/workplan/image/remove.png">';
			strhtml += '</div>';

			strhtml += "</dt>"
			strhtml = strhtml + "<dd>" + worklist[i].name + "</dd>"
		}

		if (wpm.concerned_bteam == "4" && worklist[i].clientName!='HJSJ') {
			if(worklist[i].plan_type_desc){
				strhtml = strhtml + "<dd>" + worklist[i].plan_type_desc + "</dd>"
			}
		}

		strhtml = strhtml + "</dl>";
	}
	var selobj = document.getElementById("concernedersdiv");
	selobj.innerHTML = strhtml;
	if (worklist.length < 4)
		selobj.style.height = "400px"
	else
		selobj.style.height = "";
}

/**
 * 删除我关注的 function delConcerneder(objectid,p0723) { if (!confirm('确认不关注了吗?')){
 * return; } var hashvo = new ParameterSet(); hashvo.setValue("oprType",
 * "delMeConcerneder"); hashvo.setValue("periodType", wpm.period_type);
 * hashvo.setValue("periodYear", wpm.period_year);
 * hashvo.setValue("periodMonth", wpm.period_month);
 * hashvo.setValue("periodWeek", wpm.period_week); hashvo.setValue("objectId",
 * objectid); hashvo.setValue("p0723", p0723);
 * 
 * var request=new Request({method:'post',asynchronous:true,
 * onSuccess:delFollower_ok,functionId:'9028000702'},hashvo); function
 * delFollower_ok(outparamters) { //var objectid =
 * outparamters.getValue("followerId");
 * refreshConcerneders(wpm.concerned_cur_page); } }
 */

function delConcerneder(objectid, p0723) {
	Ext.Msg.confirm('提示信息', '确定不关注了吗？', function(btn) {
				if (btn == 'yes') {
					var hashvo = new ParameterSet();
					hashvo.setValue("oprType", "delMeConcerneder");
					hashvo.setValue("periodType", wpm.period_type);
					hashvo.setValue("periodYear", wpm.period_year);
					hashvo.setValue("periodMonth", wpm.period_month);
					hashvo.setValue("periodWeek", wpm.period_week);
					hashvo.setValue("objectId", objectid);
					hashvo.setValue("p0723", p0723);

					var request = new Request({
								method : 'post',
								asynchronous : true,
								onSuccess : delFollower_ok,
								functionId : '9028000702'
							}, hashvo);
					function delFollower_ok(outparamters) {
						// var objectid = outparamters.getValue("followerId");
						refreshConcerneders(wpm.concerned_cur_page);

					}
				} else {

				}

			}, this);
}

function clickConcerneders(objectid, dept_leader, p0723,planType) {
	wpm.deptleader = dept_leader;
	wpm.p0723 = p0723;
	wpm.planType = planType;
	needRefresh = "no";
	refreshPlan(objectid);
	needRefresh = "yes";
}

function clickTeamPeople(objectid, p0723) {
	wpm.p0723 = p0723;
	needRefresh = "no";
	refreshPlan(objectid);
	needRefresh = "yes";
}
function clickTeamDept(dept_leader, e0122, p0723,planType) {
	wpm.p0723 = p0723;
	wpm.planType = planType;
	wpm.deptleader = dept_leader;
	needRefresh = "no";
	refreshPlan(e0122);
	needRefresh = "yes";
}

function displayTeamByType(submittype) {
	displayTeamInfo(submittype, wpm.teaminfo);
}

function displayTeamInfo(submittype, teaminfo) {
	var worklist = teaminfo.detail_list;
	var strhtml = "";
	for (var i = 0; i < worklist.length; i++) {
		if (submittype != "") {
			if (submittype == "1") {// 已报批的人员=已报批+已批准
				if (worklist[i].submittype != "3"
						&& worklist[i].submittype != "2") {
					continue
				};
			} else if(submittype == "4"){// 已变更 chent 20160415
				if (worklist[i].changeflag != "1"){
					continue; 
				}
			}else {
				if (worklist[i].submittype != submittype) {
					continue
				};
			}
		}
		if (wpm.concerned_bteam == "2") {
			strhtml = strhtml
					+ "<tr>"
					+ "<td width=\"15%\" class=\"hj-wzm-tdzb-td\">"
					+ "<a href=\"javascript:void(0)\"  onclick='clickConcerneders(\""
					+ worklist[i].objectid + "\",\"\",\"" + worklist[i].p0723
					+"\",\""+wpm.planType
					+ "\")'>" + "<img class=\"img-circle\" src='"
					+ worklist[i].imagepath + "'/> &nbsp;" + worklist[i].name
					+ "</a>" + "</td>" + "<td width=\"20%\">" + ""
					+ worklist[i].dept_desc + "" + "</td>"
					+ "<td width=\"15%\">" + worklist[i].appstatus + "</td>"
					+ "<td width=\"15%\">" + "<a "
					+ " style='cursor:pointer;' onclick='fontgrey(this);remindTeam(\""
					+ worklist[i].objectid + "\",\"\",\"" + worklist[i].p0723
					+ "\")'>" + worklist[i].remind + "</a>" + "</td>" + "</tr>"
		} else {
			strhtml = strhtml
					+ "<tr>"
					+ "<td width=\"15%\" class=\"hj-wzm-tdzb-td\">"
					+ "<a href=\"javascript:void(0)\" onclick='clickConcerneders(\""
					+ worklist[i].e0122 + "\",\"" + worklist[i].objectid
					+ "\",\"" + worklist[i].p0723
					+"\",\""+wpm.planType
					+"\")'>"
					+ "<img class=\"img-circle\" src='" + worklist[i].imagepath
					+ "'/>&nbsp;" + worklist[i].name + "</a>" + "</td>"
					+ "<td width=\"15%\">" + "" + worklist[i].e0122desc
					+ "&nbsp;部门工作计划" + "</td>" + "<td width=\"15%\">" + ""
					+ worklist[i].appstatus + "" + "</td>"
					+ "<td width=\"15%\">" + "<a "
					+ " style='cursor:pointer;' onclick='fontgrey(this);remindTeam(\""
					+ worklist[i].e0122 + "\",\"" + worklist[i].objectid
					+ "\",\"" + worklist[i].p0723 + "\")'>"
					+ worklist[i].remind + "</a>" + "</td>" + "</tr>"

		}
	}
	strhtml = "<table width=\"100%\" border=\"0\">" + strhtml + "</table>";
	var strtitle = "<p><b>提交情况：</b>" + ""
			+ "&nbsp;应报：<a href='javascript:displayTeamByType(\"\");' >"
			+ teaminfo.sum_count + "人</a>&nbsp;&nbsp;&nbsp;&nbsp; "
			+ "未报：<a href='javascript:displayTeamByType(\"" + "0" + "\");' >"
			+ teaminfo.unsubmit_count + "人</a>&nbsp;&nbsp;&nbsp;&nbsp; "
			+ "已报：<a href='javascript:displayTeamByType(\"" + "1" + "\");' >"
			+ teaminfo.submit_count + "人</a>&nbsp;&nbsp;&nbsp;&nbsp; "
			+ "未批：<a href='javascript:displayTeamByType(\"" + "3" + "\");' >"
			+ teaminfo.unapprove_count + "人</a>&nbsp;&nbsp;&nbsp;&nbsp; "
			+ "已批：<a href='javascript:displayTeamByType(\"" + "2" + "\");' >"
			+ teaminfo.approve_count + "人</a>&nbsp;&nbsp;&nbsp;&nbsp; "
			+ "已变更：<a href='javascript:displayTeamByType(\"" + "4" + "\");' >"
			+ teaminfo.change_count + "人</a>&nbsp;&nbsp;&nbsp;&nbsp; "
	if (teaminfo.unsubmit_count > 0) {
		strtitle = strtitle + "<a "
				+ " style='cursor:pointer;' onclick='fontgrey(this);remindTeam(\"\",\"\",\"\")'>"
				+ "提醒大家写计划</a>";
	}
	strtitle = strtitle + "</p>";
	strhtml = strtitle + strhtml;
	var selobj = document.getElementById("teaminfodiv");
	selobj.innerHTML = strhtml;
}
/**
 * 提醒写计划
 */
function remindTeam(objectid, dept_leader, p0723) {
	var hashvo = new ParameterSet();
	hashvo.setValue("oprType", "remindTeam");
	hashvo.setValue("periodType", wpm.period_type);
	hashvo.setValue("periodYear", wpm.period_year);
	hashvo.setValue("periodMonth", wpm.period_month);
	hashvo.setValue("periodWeek", wpm.period_week);
	hashvo.setValue("objectId", objectid);
	hashvo.setValue("p0723", p0723);
	hashvo.setValue("concerned_bteam", wpm.concerned_bteam);
	hashvo.setValue("deptLeader", dept_leader);
	if (objectid == "") {
		hashvo.setValue("p0723", wpm.p0723);
		hashvo.setValue("subobjectid", wpm.sub_object_id);
		hashvo.setValue("subpersonflag", wpm.sub_person_flag);
	}

	var request = new Request({
				method : 'post',
				asynchronous : true,
				onSuccess : remindTeam_ok,
				functionId : '9028000702'
			}, hashvo);
}

function remindTeam_ok(outparamters) {
	var strinfo = outparamters.getValue("info");
	strinfo = getDecodeStr(strinfo);
}

/**
 * 获取下属人员
 */
function clickSubPeople(objectid, curpage, flag, needSeeSub) {
	var hashvo = new ParameterSet();
	if(wpm.cTeam_needSee===true){
		needSeeSub = "yes";
		wpm.cTeam_needSee = false;
	}
	hashvo.setValue("oprType", "getConcerneders");
	hashvo.setValue("periodType", wpm.period_type);
	hashvo.setValue("periodYear", wpm.period_year);
	hashvo.setValue("periodMonth", wpm.period_month);
	hashvo.setValue("periodWeek", wpm.period_week);
	hashvo.setValue("curjsp", wpm.curjsp);
	hashvo.setValue("subobjectid", objectid);
	hashvo.setValue("subpersonflag", flag);
	hashvo.setValue("concerned_cur_page", curpage);
	hashvo.setValue("concerned_bteam", wpm.concerned_bteam);
	hashvo.setValue("needSeeSub", needSeeSub);
	hashvo.setValue("planType",wpm.planType);
	var request = new Request({
				method : 'post',
				asynchronous : true,
				onSuccess : clickSubPeople_ok,
				functionId : '9028000702'
			}, hashvo);
}

function clickSubPeople_ok(outparamters) {
	var strinfo = outparamters.getValue("info");
	strinfo = getDecodeStr(strinfo);
	// var obj = eval("("+strinfo+")");
	var obj = Ext.decode(strinfo);
	var planobj = obj.planinfo;
	displayConcerneders(planobj.human_map);
	if (wpm.concerned_bteam == "1" || wpm.concerned_bteam == "2"
			|| wpm.concerned_bteam == "3") {
		if (planobj.human_map.team_plan_title != null) {
			setPlanTitle(planobj.human_map.team_plan_title);
		}
		// 显示主页面
		if (wpm.curjsp == "teamplan") {
			wpm.teaminfo = planobj.teaminfo;
			displayTeamInfo("", planobj.teaminfo);
		}
	}
}

/*
 * 下拉选择复制上期任务、导出列表
 */
function dropdownAddMenuList() {
	//引导入组件
        Ext.Loader.setConfig({
            enabled: true,
            paths: {
                'SYSF':'../../../components/fileupload'
            }
        });
        Ext.require('SYSF.FileUpLoad');
	var hashvo = new ParameterSet();
	hashvo.setValue("oprType", "dropdownAddMenuList");
	hashvo.setValue("fromflag",wpm.from_flag);
	var request = new Request({
				method : 'post',
				asynchronous : true,
				onSuccess : function(outparamters) {
					var info = outparamters.getValue("info");
					info = getDecodeStr(info);
					var planobj = Ext.decode(info);
					var strhtml = "";
					var worklist = planobj.periodlist;
					for (var i = 0; i < worklist.length; i++) {
						strhtml = strhtml + '<li> <a id="'
								+ worklist[i].menu_id
								+ '" onclick="selectAddMenu(\''
								+ worklist[i].menu_id + '\')'
								+ '" href="javascript:void(0)">'
								+ worklist[i].menu_name + '</a> </li>';
					}
					strhtml = "<ul>" + strhtml + "</ul>"
					dispalyDropdownBox("8", strhtml);
				},
				functionId : '9028000702'
			}, hashvo);
}
/*
 * linbz 优化复制任务
 * */
// ---设置计划类型
function setPeriodTypeNameCopyTask(namevalue) {
    var nameobj = document.getElementById("typeTitleid");
    nameobj.innerHTML = namevalue;
}
// ---设置计划期间
function setPeriodNameCopyTask(periodyear, periodmonth) {
    var monthDesc = "";
    if (wpm.period_type == "4" || wpm.period_type == "5") {
        monthDesc = periodmonth + "月";
        if (Number(periodmonth) < 10) {
            monthDesc = "&nbsp;" + monthDesc;
        }
    }

    monthDesc = periodyear + "年" + monthDesc;
    var nameobj = document.getElementById("periodname");
    nameobj.innerHTML = monthDesc
            + '&nbsp;<img dropdownName="both" src="/workplan/image/jiantou.png"/>';

}
/*
 * 复制任务 
 */
//复制任务回调窗口
function copyPirorTasks_ok(outparamters){
	
	var parammap = getAroundPeriod(0);
	periodCascadeMain(outparamters, wpm.period_type, parammap.year, parammap.month, parammap.week);
	    
}

function periodCascadeMain(outparamters, period_type, period_year, period_month, period_week){
		var window1 = Ext.getCmp('cypewin');
	    if(window1)
	        window1.close(); 
        //校验是否选中        
        var selModel = g_tree.getSelectionModel();
    	var records = selModel.getSelection();
    	if(records.length>0)
    		wpm.selNode = records[0];
    	else 
    		wpm.selNode = null;
    	
        var parentid = "";
        var parentTitle = "";
        var partens = "";
    
        if (wpm.selNode != null) {
            parentid = wpm.selNode.get('p0800');
            parentTitle = wpm.selNode.get('p0801');
            partens = wpm.selNode.get('p0801');
        }
        if(!Ext.isEmpty(parentTitle) && parentTitle.length>30){
            parentTitle = parentTitle.substring(0, 30)+"...";
        }
        
        var _tableData = Ext.decode(getDecodeStr(outparamters
                .getValue("dataJson")));
        // 上期全部任务 chent 20160415
        var _tableDataAll = Ext.decode(getDecodeStr(outparamters
                .getValue("dataJsonAll")));
        var _fields = Ext.decode(getDecodeStr(outparamters
                .getValue("dataModel")));
        var _columns = Ext.decode(getDecodeStr(outparamters
                .getValue("panelColumns")));
        Ext.define('unFinishTaskList', {
                    extend : 'Ext.data.TreeModel',
                    fields : _fields
                });
        var store1 = Ext.create('Ext.data.Store', {
                    model : 'unFinishTaskList',
                    data : _tableData
                });
        var storeAll = Ext.create('Ext.data.Store', {
                    model : 'unFinishTaskList',
                    data : _tableDataAll
                });
        
        var gridnotfinish = Ext.create("Ext.grid.Panel", {
            store : store1,
            id : 'gridnotfinish',
            width : 766, // 窗口宽度
            height : 400, // 窗口高度
            forceFit:true,
            hidden:true,
            //region : "center",
            columns : _columns,
            // mode的取值：multi,simple,single，默认为多选multi（需要按住ctrl键选中），simple多评选，single
            selModel : Ext.create(
                    'Ext.selection.CheckboxModel', {
                        mode : 'simple'
                    })
        });
        var gridAll = Ext.create("Ext.grid.Panel", {
            store : storeAll,
            id : 'gridall',
            width : 764, // 窗口宽度
            height : 350, // 窗口高度
//            forceFit:true,
            //region : "center",
            columns : _columns,
            // mode的取值：multi,simple,single，默认为多选multi（需要按住ctrl键选中），simple多评选，single
            selModel : Ext.create(
                    'Ext.selection.CheckboxModel', {
                        mode : 'simple'
                    })
        });
        
       /* //底部去掉 两个单选
        var checkBox = Ext.widget({
            xtype: 'fieldcontainer',
            defaultType: 'checkboxfield',
            layout:'hbox',
            items: [
                {
                    boxLabel  : '仅复制未完成任务',
                    inputValue: '1',
                    margin:'0 30 0 0',
                    id:'radio1',
                    handler:function(check,v){
                        if(v){
                            gridAll.hide();
                            gridnotfinish.show();
                        }else{
                            gridAll.show();
                            gridnotfinish.hide();
                        }
                    }
                }, {
                    boxLabel  : '复制任务进度和完成情况',
                    inputValue: '2',
                    id:'isCopyInfoCheckBox'
                }
            ]
        });*/
        var cycleFunction;
        if(wpm.planType=="person")
        	cycleFunction = wpm.person_cycle_function;
    	else if(wpm.planType=="org")
    		cycleFunction = wpm.org_cycle_function;
        
        var obj = new Object();
        obj.type="0";
        obj.Periodyear = period_year;//
        obj.Periodmonth = period_month;//
        obj.Periodweek = period_week;//
        obj.Periodtype = period_type;
        obj.params =  cycleFunction;
        obj.success = periodCallback;
        
        var PeriodCascade = Ext.create("PeriodCascadeUL.PeriodCascade",obj);
       var selector = PeriodCascade.getSelector();
        var tbar1 = new Ext.Toolbar({//Ext.create("Ext.panel.Panel", { 
                    id:'toolbar1',
                    height:25,
                    padding:'0 0 0 5',
                    border:false,
                    items:[
                    {
                        xtype:'label',
                        id:'titlep',
                        hidden:Ext.isEmpty(parentTitle)?true:false,
                        html:"<div onmouseover='mOver(this)' onmouseout='mOut(this)'>" +
                        		"父任务：<span title='"+partens+"' >"+parentTitle+"</span>" +
                        		"<img id='delid' width='14px' height='14px' onclick='deleParent(\"titlep\");' " +
                        		"src='/workplan/image/remove.png' style='display:none;cursor:pointer;position:relative !important; top:-2px !important; ' />" +
                        		"</div>"
                    },
                      "->",
                    {
                       xtype:'panel',
                       border:false,
                       height:25,
                       autoWidth : true,
                       items:[selector]
                    }]
                }); 
                
            var container = Ext.create('Ext.container.Container', {
            scrollable:'y',
            border: true,
            items: [tbar1, gridAll]//, gridnotfinish
        });
          
        window1 = new Ext.Window({
            id : 'cypewin',
            closeToolText : '',
            title : "任务选择", // 窗口标题
            width : 776, // 窗口宽度
            height : 460, // 窗口高度
//            layout : "fit",// 布局
            constrain : true, // 防止窗口超出浏览器窗口,保证不会越过浏览器边界
            buttonAlign : "center", // 按钮显示的位置
            modal : true, // 模式窗口，弹出窗口后屏蔽掉其他组建
            resizable : false, // 是否可以调整窗口大小，默认TRUE。
    //            plain : true,// 将窗口变为半透明状态。
            items : [container],
            listeners:{
            	close:function(){
            		if(PeriodCascade){
            			var win = PeriodCascade.getDropDownWin()
            			if(win)
            				win.close();
            		}
            	}
            
            },
            bbar: [
              { 
                  xtype: 'button', 
                  margin:'0 0 0 320',
                  text: '确定',
                  handler : function() {    
    //                                var isNotAll = Ext.getCmp('radio1').getValue();
                        var listall = gridAll.getSelectionModel().getSelection();
                        
                        var p0800ids = "";
                        var selectlist = comp(listall);
                        for(var i = 0;i<selectlist.length;i++){
                            var sel = selectlist[i];
                            p0800ids = p0800ids + ","+ sel.get('p0800');
                        }
                        //如果都未选中则默认全部复制
                        if (Ext.isEmpty(p0800ids)) {
                            for(var i = 0;i<listall.length;i++){
                                var sel = listall[i];
                                p0800ids = p0800ids + ","+ sel.get('p0800');
                            }
                        }
                        if (!Ext.isEmpty(p0800ids)) {
                        	//linbz  现优化为全部复制，默认为是1
//                          var isCopyInfo = "1";
//                          if(Ext.getCmp('isCopyInfoCheckBox').getValue()){//是否复制任务进度、完成情况 chent//linbz  默认为是1
//                              isCopyInfo = "1";
//                          }
                        	//如果父任务隐藏，则设置为空，就是复制到一级任务
                            if(Ext.getCmp('titlep').hidden){
                            	parentid = "";
                            }
                            copyPirorTask(p0800ids, _tableData, parentid, "");
    						window1.close();
                        } else {
                            Ext.Msg.alert('提示信息', '请选择需要复制的任务！');
                        }
                        
                }},
                {
                    xtype: 'button', 
                    margin:'0 10 0 10',
                    text : "取消",
                    handler : function() {
                        window1.close();
                    }
                }
            ]
        });
        window1.show();
}
function deleParent(titleid){
	Ext.getCmp(titleid).setHidden(true);
}
function mOver(){
	var delid = document.getElementById("delid");
	if (delid) {
		delid.style.display = "inline";
	}
}
function mOut(){
	var delid = document.getElementById("delid");
	if (delid) {
		delid.style.display = "none";
	}
}
/**
 * PeriodCascadeUL.PeriodCascade组件回调函数
 * @param {} typeflag 切换类型标识，=0不是，=1是，与填写类型一致时则定位到上一期,其他类型则定位到当前计划，
 * @param {} period_type
 * @param {} period_year
 * @param {} period_month
 * @param {} period_week
 */
function periodCallback(typeflag, period_type, period_year, period_month, period_week){
	//引期间类型对象
    Ext.Loader.setConfig({
        enabled: true,
        paths: {
            'PeriodCascadeUL': '/module/workplan/utlis'
            
        }
    });
                
    Ext.require('PeriodCascadeUL.PeriodCascade');
    var yearback = "";
    var monthback = "";
    var weekback = "";
    if(typeflag == '1'){
    	//复制计划窗口的期间类型和工作计划页面的期间类型一致时，定位上一期的计划
    	if(wpm.period_type == period_type){
    		var parammap = getAroundPeriod(0);
    		yearback = parammap.year;
    	    monthback = parammap.month;
    	    weekback = parammap.week;
    	}else{
    		//不一致时，需要在后台定位当前时间，这里就不需要传递时间了
//    		yearback = wpm.period_year;
//    	    monthback = wpm.period_month;
//    	    weekback = wpm.period_week;
    	}
    }else{
    	yearback = period_year;
	    monthback = period_month;
	    weekback = period_week;
    }
	var hashvo = new ParameterSet();
        hashvo.setValue("oprType", "selectUnFinishTask");
        hashvo.setValue("objectId", wpm.object_id);
        hashvo.setValue("periodType", period_type);
        hashvo.setValue("periodYear", yearback);
        hashvo.setValue("periodMonth", monthback);
        hashvo.setValue("periodWeek", weekback);
        hashvo.setValue("p0723", wpm.p0723);

        var request = new Request({
            method : 'post',
            asynchronous : true,
            onSuccess : function(outparamters){
            	if(Ext.isEmpty(yearback)&& Ext.isEmpty(monthback) && Ext.isEmpty(monthback)){
            		yearback = outparamters.getValue("periodYear");
            		monthback = outparamters.getValue("periodMonth");
            		weekback = outparamters.getValue("periodWeek");
            	}
            	periodCascadeMain(outparamters, period_type, yearback, monthback, weekback);
            },
            functionId : '9028000707'
        }, hashvo);
}

/**
 * 获取前后期间参数  
 * @param {} flag  =0上一期间， =1获取下一期间
 * 年1，半年2，季度3，月4，周5，
 * @return {}
 */
function getAroundPeriod(flag){
	
	//参数控制
	var cyclemap =  new HashMap();
	cyclemap.put("halfyears","");
    cyclemap.put("quaters","");
    cyclemap.put("months","");
    
    var cycleFunction;
    if(wpm.planType=="person")
    	cycleFunction = wpm.person_cycle_function;
	else if(wpm.planType=="org")
		cycleFunction = wpm.org_cycle_function;
    
    for(var p in cycleFunction){
        var obj = cycleFunction[p];
        if(obj['p1'] != undefined ){
        	
            cyclemap.put("halfyears",obj['p1'].cycle);
        }else if(obj['p2'] != undefined ){
        	
            cyclemap.put("quaters",obj['p2'].cycle);
        }else if(obj['p3'] != undefined ){
        	
            cyclemap.put("months",obj['p3'].cycle);
        }else{
            continue;
        }
    }
    var halfyearlist = cyclemap.halfyears.split(",");
    var quaterlist = cyclemap.quaters.split(",");
    var monthlist = cyclemap.months.split(",");
        
	var parammap =  new HashMap();
	parammap.put("year","");
    parammap.put("month","");
    parammap.put("week","");
	if("0" == flag){
	   if("1" == wpm.period_type){// 年1
            parammap.put("year",Math.round(wpm.period_year)-1);
            parammap.put("month",wpm.period_month);
            parammap.put("week",wpm.period_week);
            
       }else if("2" == wpm.period_type){//半年2
       	    if("1" == wpm.period_month){
           	    parammap.put("year",Math.round(wpm.period_year)-1);
           	    if((","+cyclemap.halfyears+",").indexOf(','+2+",") != -1)
       	            parammap.put("month","2");
       	        else if((","+cyclemap.halfyears+",").indexOf(','+1+",") != -1)
       	            parammap.put("month","1");
       	    }else{
       	        parammap.put("year",wpm.period_year);
                parammap.put("month","1");
                if((","+cyclemap.halfyears+",").indexOf(','+1+",") != -1)
                    parammap.put("month","1");
                else if((","+cyclemap.halfyears+",").indexOf(','+2+",") != -1)
                    parammap.put("month","2");
       	    }
            parammap.put("week",wpm.period_week);
            
       }else if("3" == wpm.period_type){//季度3
       	    if(quaterlist[0] == wpm.period_month){
                parammap.put("year",Math.round(wpm.period_year)-1);
                parammap.put("month",quaterlist[quaterlist.length-1]);
            }else{
                parammap.put("year",wpm.period_year);
                var val = "";
                for(var j in quaterlist){
                    if(wpm.period_month==quaterlist[j] && j!=0){
                        val = quaterlist[j-1];
                    }
                }
                parammap.put("month",val);
            }
            parammap.put("week",wpm.period_week);
            
       }else if("4" == wpm.period_type){//月4
       	    if(monthlist[0] == wpm.period_month){
                parammap.put("year",Math.round(wpm.period_year)-1);
                parammap.put("month",monthlist[monthlist.length-1]);
            }else{
                parammap.put("year",wpm.period_year);
                var val = "";
                for(var j in monthlist){
                    if(wpm.period_month==monthlist[j] && j!=0){
                        val = monthlist[j-1];
                    }
                }
                parammap.put("month",val);
            }
            parammap.put("week",wpm.period_week);
       
       }else if("5" == wpm.period_type){//周5，
            if("1" == wpm.period_week){
            	
            	 if("1" == wpm.period_month){
                    parammap.put("year",Math.round(wpm.period_year)-1);
                    parammap.put("month","12");
                    var weeks = getWeeks(Math.round(wpm.period_year)-1, monthlist[monthlist.length-1]);
                    parammap.put("week",weeks);
                }else{
                    parammap.put("year",wpm.period_year);
                    parammap.put("month",Math.round(wpm.period_month)-1);
                    var weeks = getWeeks(wpm.period_year, Math.round(wpm.period_month)-1);
                    parammap.put("week",weeks);
                }
            }else{
                parammap.put("year",wpm.period_year);
                parammap.put("month",wpm.period_month);
                parammap.put("week",Math.round(wpm.period_week)-1);
            }
       }
	}
//	console.log(parammap);
	return parammap;

}
/**
    * 获取该月份有几周（从第一个周一起）
    * @param {} year
    * @param {} month
    * @return {}
    */
function getWeeks(year,month){
	var weeknum=4;
	var map = new HashMap();
	map.put("periodYear",year)
	map.put("periodMonth",month)
	map.put("oprType","getWeekNum")
	Rpc({functionId:'9028000702',async:false,success:function(form){
		 var result = Ext.decode(form.responseText); 
		 weeknum = result.weeknum;
	}},map);
	return weeknum;
}
/**
 * 复制任务
 * @param {} menu_id
 */
function selectAddMenu(menu_id) {
	if (menu_id == "copyPirorTask") {// 复制任务
		// 填报范围校验 chent 20180329 add
		if(privCheck()!="0") {
			return ;
		}
		
		//获取上一期间参数
        var parammap = getAroundPeriod(0);
		var hashvo = new ParameterSet();
		hashvo.setValue("oprType", "selectUnFinishTask");
		hashvo.setValue("objectId", wpm.object_id);
		hashvo.setValue("periodType", wpm.period_type);
		hashvo.setValue("periodYear", parammap.year);//wpm.period_year
		hashvo.setValue("periodMonth", parammap.month);//wpm.period_month
		hashvo.setValue("periodWeek", parammap.week);// wpm.period_week
		hashvo.setValue("p0723", wpm.p0723);
		//引期间类型组件
		Ext.Loader.setConfig({
            enabled: true,
            paths: {
                'PeriodCascadeUL': '/module/workplan/utlis'
            }
        });
        Ext.require('PeriodCascadeUL.PeriodCascade');
        
		var request = new Request({
			method : 'post',
			asynchronous : true,
			onSuccess : copyPirorTasks_ok,
			functionId : '9028000707'
		}, hashvo);

	} else if (menu_id == "exportTask") {// 导出任务列表
		var hashvo = new ParameterSet();
		hashvo.setValue("oprType", "exportExcel");
		hashvo.setValue("periodType", wpm.period_type);
		hashvo.setValue("periodYear", wpm.period_year);
		hashvo.setValue("periodMonth", wpm.period_month);
		hashvo.setValue("periodWeek", wpm.period_week);
		hashvo.setValue("exportSubTask", wpm.showSubTask);
		if (wpm.p0700 == "") {
			Ext.Msg.alert("提示信息", "当前计划没有任务列表");
			return;
		}
		hashvo.setValue("p0700", wpm.p0700);
		hashvo.setValue("objectId", wpm.object_id);
		hashvo.setValue("p0723", wpm.p0723);
		var request = new Request({
					method : 'post',
					asynchronous : true,
					onSuccess : showfile,
					functionId : '9028000702'
				}, hashvo);
	}else if (menu_id == "importTask") {// 导入任务
		// 填报范围校验 chent 20180329 add 
		if(privCheck()!="0") {
			return ;
		}
		importTask();
	}
}
/**
 * 导入任务
 */
function importTask(){
    var win = Ext.create('Ext.window.Window',{
            closeToolText : '',
            title:'导入',
            id:'imputeId',
            width:300,
            height:180,  
            resizable: false,  
            modal: true,
            border:false,
            bodyStyle: 'background:#ffffff;',
            layout: {
                type: 'vbox',
//              align: 'left'
                padding:'0 0 0 50'
            },
            items:[{
                xtype:'container',
                margin: '24 0 0 0',
                layout:'hbox',
                items:[{
                    xtype:"label",
                    margin: '0 24 0 0',
                    width:120,
                    text:"1、下载模板文件"
                },{
                    xtype:"button",
                    text:"下载",
                    handler:function(){
                        var map = new HashMap();
                            map.put("periodType", wpm.period_type);
                            map.put("periodYear", wpm.period_year);
                            //wpm.period_month=null时，后台拿到的是jsonObject类型的null  haosl 2018-3-14
                            map.put("periodMonth",Ext.isEmpty(wpm.period_month)?"":wpm.period_month);
                            map.put("periodWeek",Ext.isEmpty(wpm.period_week)?"":wpm.period_week);
                            map.put("p0700", wpm.p0700);
                            map.put("objectId", wpm.object_id);
                            map.put("p0723", wpm.p0723);
                            Rpc({functionId:'WP10000005',success:function(form,action){
                                var result = Ext.decode(form.responseText); 
                                if(result.succeed){
									//zhangh 2020-3-5 下载改为使用VFS
									var outName = result.fileName
									outName = decode(outName);
									var win=open("/servlet/vfsservlet?fromjavafolder=true&fileid="+outName);
                                    }else{
                                        Ext.MessageBox.show({
                                            title : "提示信息",  
                                            msg : result.message, 
                                            icon: Ext.MessageBox.INFO  
                                        })
                                    }
                             }},map);
                    }
                }]},{
                    xtype:'container',
                    margin: '24 0 0 0',
                    layout:'hbox',
                    items:[{
                        xtype:"label",
                        width:120,
                        margin: '0 24 0 0',
                        html:"2、请选择导入文件"
                },{
                    xtype:"button",
                    width:40,
                    height:22,
                    text:"浏览"
                }]},
                {
                    xtype:'box',
                    border:false,
                    width:40,
                    height:22,
                    margin: '-22 0 0 150',
                    style:{
                        background:'',
                        borderColor:'#c5c5c5',
                        borderStyle:'dashed'
                    },
                    listeners:{
                        render:function(){
                            var uploadBox = this;
                            Ext.widget("fileupload",{
                            	//zhangh 2020-3-5 调用统一的上传组件，增加vfs参数
								isTempFile:true,
								VfsModules:VfsModulesEnum.MB,
								VfsFiletype:VfsFiletypeEnum.doc,
								VfsCategory:VfsCategoryEnum.other,
								CategoryGuidKey:'',
                                upLoadType:3,
                                height:22,width:40,
                                buttonText:'',
                                fileExt:"*.xls;*.xlsx",//添加对上传文件类型控制
                                renderTo:uploadBox.id,
                                success:function(list){
                                    var obj = list[0];
                                    var map1 = new HashMap();
                                    map1.put("filename",obj.filename);
                                    map1.put("path",obj.fileid);
                                    map1.put("p0700", wpm.p0700);
                                    map1.put("objectId", wpm.object_id);
                                    map1.put("p0723", wpm.p0723);
                                    map1.put("periodType", wpm.period_type);
                                    map1.put("periodYear", wpm.period_year);
                                    map1.put("periodMonth", wpm.period_month);
                                    map1.put("periodWeek",Ext.isEmpty(wpm.period_week)?"":wpm.period_week);
                                    //增加滚动条
                                    Ext.MessageBox.wait("正在执行导入计划任务操作，请稍候...", "等待");
                                    Rpc({functionId:'WP10000006',success:function(data){
                                        var result = Ext.decode(data.responseText);
                                        var msglist = result.importMsg;
                                        if(wpm.p0700==""){
                                            wpm.p0700 = result.p0700;
                                        }
                                        var msgg = "";
                                        if(msglist != ""){
                                            for(var i=0;i<msglist.length;i++){
                                                var msg = msglist[i];
                                                msgg += msg+"<br>";
                                            }
                                            msgg = msgg;
                                            Ext.showAlert(""+msgg+"", function(){
                                            	Ext.MessageBox.close();
                                                win.close();
                                                getPlanTaskList();
                                                // 本人的计划内是否包含需要报批的任务 
                                    			var planstatusVal = document.getElementById("planstatus").innerHTML;
                                    			// 为重新按钮加限制,只有计划在已批准的情况下,重新发布按钮才会出现
                                    			if (planstatusVal == "已批准") {
                                    				document.getElementById("planstatus").innerHTML = "已变更";
                                    				document.getElementById("btnRepublish").style.display = "inline-block";
                                    			}
                                    			if (!wpm.myplan) {
                                    				document.getElementById("btnRepublish").style.display = "none";
                                    			}
                                            });
                                        }
                                    },scope:this},map1);    
                                },
                                callBackScope:'',
                                savePath:''
                            });
                        }
                    }
                },
                {
                    xtype:'container',
                    margin: '20 0 0 -10',
                    layout:'hbox',
                    items:[{
                        xtype:"label",
                        html:"注意：每次导入之前请下载最新模板文件！"
                        }]
                }]
        });
        win.show();
}

/**
 * 
 * @param {} selectlist
 * @return {}
 */
function comp(selectlist){
	for (var i = 0; i < selectlist.length-1; i++) {
		for(var j = 0;j<selectlist.length-1-i;j++){
			var ssel = selectlist[j];
			var lsel = selectlist[j+1];
			if(ssel.data._level>lsel.data._level){
				selectlist[j] = lsel;
				selectlist[j+1] = ssel;
			}
		}
	}
	return selectlist;
}
function showfile(outparamters) {
	//zhangh 2020-3-3 下载改为使用VFS
	var outName=outparamters.getValue("outName");
	outName = decode(outName);
	var win=open("/servlet/vfsservlet?fromjavafolder=true&fileid="+outName);

}
function resizeEvent(ct, column, width) {
	
	if(Ext.isIE && column.dataIndex == 'p0801'){//任务名称链接的渲染在ie下特殊处理 chent 20171009
		wpm.p0801ColumnWidth = width;
		
		var treeStore = g_tree.getStore();
		var records = treeStore.data.items;
		for(var i=0; i<records.length; i++){
			var record = records[i];
			var p0801Link = Ext.getDom('p0801_'+record.data.p0800);
			if(p0801Link){
				p0801Link.style.width = getP0801ClomnDisplayWidth(record)+'px';
			}
			
		}
	}
	var hashvo = new ParameterSet();
	hashvo.setValue("dataIndex", column.dataIndex);
	hashvo.setValue("newWidth", width);
	var submoduleid = "workPlan_position_0001";// 栏目设置唯一id
	/*
	 * 废掉计划跟踪页面 if(wpm.plan_design == "2"){ submoduleid =
	 * "workPlan_position_0002"; }
	 */
	hashvo.setValue("submoduleid", submoduleid);
	hashvo.setValue("is_share", "0");// 是否公有 0:否 1:是
	var request = new Request({
				method : 'post',
				asynchronous : true,
				onSuccess : '',
				functionId : '9028000813'
			}, hashvo);
	return false;// 为了解决bug16119:OKR工作计划，新增任务，列表界面设置权重，权重值输入后不切换单元格，然后去拖动调整列宽，调整后权重丢失.
	// return false 好像就不会重新渲染了
}

/* 保存所选择复制的上期任务 */
function copyPirorTask(task_ids, _tableData, parentid, isCopyInfo) {
	Ext.MessageBox.wait("正在复制任务，请稍候...", "等待",{renderTo:'cypewin'});
	var scopeobj = document.getElementById("planscope");
	var planscope = scopeobj.value;
	var hashvo = new ParameterSet();
	hashvo.setValue("oprType", "copyPirorTask");
	hashvo.setValue("p0723", wpm.p0723);
	hashvo.setValue("periodType", wpm.period_type);
	hashvo.setValue("periodYear", wpm.period_year);
	hashvo.setValue("periodMonth", wpm.period_month);
	hashvo.setValue("periodWeek", wpm.period_week);
	hashvo.setValue("objectId", wpm.object_id);
	hashvo.setValue("plan_scope", planscope);// 新增计划时使用
	hashvo.setValue("task_ids", task_ids);
//	hashvo.setValue("isCopyInfo", isCopyInfo);
	// hashvo.setValue("tableData", _tableData);
	hashvo.setValue("task_parentid", parentid);
	var request = new Request({
				method : 'post',
				asynchronous : true,
				onSuccess : isHave,
				functionId : '9028000702'
			}, hashvo);
	function isHave(outparamters) {
		if (outparamters) {
			var info = outparamters.getValue("info");
			info = getDecodeStr(info);
			var obj = Ext.decode(info);
			if (wpm.p0700 == "") {
				wpm.p0700 = obj.p0700;
				setPlanStatus("0", "起草中");
			}
			getPlanTaskList();
		}
		Ext.MessageBox.close();
	}
}

/** 未完成任务名称样式 * */
function addUnfinishedTaskNameCss(value, cell, record, rowIndex, columnIndex,
		store) {
	cell.style = "padding-left:0;border: inset 1px  #EEEDED;"
			+ "BORDER-BOTTOM:  #EEEDED 0pt solid;"
			+ "BORDER-LEFT:  #EEEDED 0pt solid;"
			+ "BORDER-RIGHT:  #EEEDED 0pt solid;"
			+ "BORDER-TOP:  #EEEDED 0pt solid;";
	if (value.length > 0) {
		//任务名称当中有换行的换，页面会报错，此处解密  haosl 2018-3-13
		value=getDecodeStr(value);
		value = "&nbsp;&nbsp;" + value;
		var _level = record.get("_level");
		for (var i = 1; i < _level; i++) {
			value = "&nbsp;&nbsp;&nbsp;&nbsp;" + value;
		}
	}
	return value;
}

/**
 * 选择人力地图视图
 */
function selectHummanMapType(menu_id) {
	wpm.concerned_cur_page = 1;
	var xshangEle = document.getElementById("xshangjpg");
	if (xshangEle) {
		xshangEle.style.display = "none";
	}
	hideDropdownBox();
	wpm.concerned_bteam = menu_id;
	wpm.super_concerned_objs = [];
	wpm.sub_object_id = "";
	wpm.sub_person_flag = "true";
	if (wpm.concerned_bteam == "1") {// 定位第一个我的部门
		var hashvo = new ParameterSet();
		hashvo.setValue("oprType", "getMyFirstDept");
		hashvo.setValue("planType",wpm.planType);
		var request = new Request({
					method : 'post',
					asynchronous : true,
					onSuccess : getMyFirstDept_ok,
					functionId : '9028000702'
				}, hashvo);
	} else if (wpm.concerned_bteam == "2" || wpm.concerned_bteam == "3") {
		if (wpm.curjsp == "teamplan") {
			clickSubPeople(wpm.sub_object_id, 1, wpm.sub_person_flag);
		} else {
			backMyTeam();
		}
	} else {
		clickSubPeople(wpm.sub_object_id, 1, wpm.sub_person_flag);
	}
}

function getMyFirstDept_ok(outparamters) {
	var objectid = outparamters.getValue("objectid");
	objectid = getDecodeStr(objectid);
	if (objectid != "") {
		var dept_leader = outparamters.getValue("dept_leader");
		dept_leader = getDecodeStr(dept_leader);
		var p0723 = outparamters.getValue("p0723");
		p0723 = getDecodeStr(p0723);
		wpm.object_id = objectid;
		wpm.deptleader = dept_leader;
		wpm.p0723 = p0723;
		loadPlan(objectid, "true");
	}
}

/**
 * 上翻页
 */
function upConcerneders() {
	if (wpm.concerned_cur_page <= 2) {
		var xshangEle = document.getElementById("xshangjpg");
		if (xshangEle) {
			xshangEle.style.display = "none";
		}
	}
	refreshConcerneders(--wpm.concerned_cur_page);
}
/**
 * 下翻页
 */
function downConcerneders() {
	var xshangEle = document.getElementById("xshangjpg");
	if (xshangEle) {
		xshangEle.style.display = "inline";
		xshangEle.style.align = "center";
		xshangEle.style.margin = "0 0 0 55px";
	}
	refreshConcerneders(++wpm.concerned_cur_page);
}

function refreshConcerneders(cur_page) {
	var objectid = document.getElementById("objectid").value;
	var hashvo = new ParameterSet();
	hashvo.setValue("oprType", "getConcerneders");
	hashvo.setValue("objectId", wpm.object_id);
	hashvo.setValue("periodType", wpm.period_type);
	hashvo.setValue("periodYear", wpm.period_year);
	hashvo.setValue("periodMonth", wpm.period_month);
	hashvo.setValue("periodWeek", wpm.period_week);

	hashvo.setValue("curjsp", wpm.curjsp);
	hashvo.setValue("concerned_cur_page", cur_page);
	hashvo.setValue("concerned_bteam", wpm.concerned_bteam);
	hashvo.setValue("subobjectid", wpm.sub_object_id);
	hashvo.setValue("subpersonflag", wpm.sub_person_flag);
	hashvo.setValue("deptLeader", wpm.deptleader);
	var request = new Request({
				method : 'post',
				asynchronous : false,
				onSuccess : refreshConcerneders_ok,
				functionId : '9028000702'
			}, hashvo);
}

function refreshConcerneders_ok(outparamters) {
	var strinfo = outparamters.getValue("info");
	strinfo = getDecodeStr(strinfo);
	var obj = Ext.decode(strinfo);
	var planobj = obj.planinfo;
	var maps = planobj.human_map;
	if ("" == maps.concerneders) {
		if (wpm.concerned_cur_page > 1) {
			--wpm.concerned_cur_page;
		}
		// return;
	}
	displayConcerneders(planobj.human_map);
}

function hintMsg(content) {
	config.FontSize = '10pt';
	config.FontColor = '#51504E';
	// config.Shadow=true;
	// config.BgImg="/workplan/image/huifu.jpg";
	config.BgColor = "#FFFFFF";
	Tip(content, STICKY, true);
}
/**
 * 选择期间类型
 */
function dropdownPeriodType(hr) {
	var hashvo = new ParameterSet();
	divobj = document.getElementById("periodtype");
	var periodtype = divobj.value;
	hashvo.setValue("oprType", "dropdownPeriodType");
	hashvo.setValue("ishr", hr);
	var request = new Request({
				method : 'post',
				asynchronous : true,
				onSuccess : dropdownPeriodType_ok,
				functionId : '9028000702'
			}, hashvo);
}

function dropdownPeriodType_ok(outparamters) {
	var info = outparamters.getValue("info");
	info = getDecodeStr(info);
	var planobj = Ext.decode(info);

	var strhtml = "";
	strhtml = "";
	var worklist = planobj.periodlist;
	for (var i = 0; i < worklist.length; i++) {
	
		// 填报期间范围权限 chent 20170112 start
		var id = worklist[i].period_id;
		var key = '';
		if(id == 1) key='p0'; 
		if(id == 2) key='p1'; 
		if(id == 3) key='p2'; 
		if(id == 4) key='p3'; 
		if(id == 5) key='p4'; 
		var isOpen = false;
		for(var p in wpm.cycle_function){
			var obj = wpm.cycle_function[p];
			if(obj[key] != undefined){
				isOpen = true;//启用
			}
		}
		if(!isOpen) continue ;
		// 填报期间范围权限 chent 20170112 end
		
		strhtml = strhtml + '<li> <a id="' + id
				+ '" onclick="' + 'selectPeriodType(' + id
				+ ')' + '" href="javascript:void(0)">'
				+ worklist[i].period_name + '</a> </li>';

	}
	if(strhtml.length>0){
		strhtml = "<ul>" + strhtml + "</ul>"
		dispalyDropdownBox("1", strhtml);
	}
}
/**
 * 选择年
 */
function dropdownPeriodYear(hr) {
	hideDropdownBox();
	if (wpm.period_type == "4" || wpm.period_type == "5") {// 月、周
		var year = Ext.getDom('myeartitle');
		year.innerHTML = wpm.period_year;
		dispalyDropdownBox("7", '');
		return;
	}

	var hashvo = new ParameterSet();
	divobj = document.getElementById("periodtype");
	var periodtype = divobj.value;
	hashvo.setValue("oprType", "dropdownPeriodList");
	hashvo.setValue("periodType", periodtype);
	hashvo.setValue("ishr", hr);
	var request = new Request({
				method : 'post',
				asynchronous : true,
				onSuccess : dropdownPeriodYear_ok,
				functionId : '9028000702'
			}, hashvo);
}

function dropdownPeriodYear_ok(outparamters) {
	var info = outparamters.getValue("info");
	info = getDecodeStr(info);
	// var planobj = eval("("+info+")");
	var planobj = Ext.decode(info);

	var strhtml = "";
	strhtml = "";
	var worklist = planobj.periodlist;
	for (var i = 0; i < worklist.length; i++) {
		strhtml = strhtml + '<li> <a id="' + worklist[i].period_id
				+ '" onclick="' + 'selectPeriodYear(' + worklist[i].period_id
				+ ')' + '" href="javascript:void(0)">'
				+ worklist[i].period_name + '</a> </li>';

	}
	if(strhtml.length>0){
		strhtml = "<ul>" + strhtml + "</ul>"
		dispalyDropdownBox("2", strhtml);
	}
	
}
/**
 * 计划制定视图
 */
function planDesign() {
	// 刷新表格
	wpm.plan_design = "1";
	getPlanTaskList();
}
/**
 * 计划跟踪视图
 */
function planTrace() {
	// 刷新表格
	wpm.plan_design = "1";// "2";
	getPlanTaskList();
}
/**
 * 下拉可见范围菜单
 */
function dropdownPlanScope() {
	if (!(wpm.myplan || wpm.subplan)) {
		return false;

	}
	var box = document.getElementById("dropdownBox");

	var hashvo = new ParameterSet();
	hashvo.setValue("oprType", "dropdownPlanScope");
	var request = new Request({
				method : 'post',
				asynchronous : true,
				onSuccess : dropdownPlanScope_ok,
				functionId : '9028000702'
			}, hashvo);

}

function dropdownPlanScope_ok(outparamters) {
	var strplan = outparamters.getValue("info");
	strplan = getDecodeStr(strplan);
	// var planobj = eval("("+strplan+")");
	var planobj = Ext.decode(strplan);

	var strhtml = "";
	strhtml = "";
	var worklist = planobj.planscopelist;
	for (var i = 0; i < worklist.length; i++) {
		strhtml = strhtml + '<li> <a id="' + worklist[i].scope_id
				+ '" onclick="' + 'selectPlanScope(' + worklist[i].scope_id
				+ ')' + '" href="javascript:void(0)" >'
				+ worklist[i].scope_name + '</a></li>';

	}
	strhtml = "<ul>" + strhtml + "</ul>"

	dispalyDropdownBox("3", strhtml);
}
/**
 * 选择可见范围
 */
function selectPlanScope(planscope) {
	var planscopename = document.getElementById("planscopename").innerHTML;// 计划未定制,不能修改可见范围wusy
	var selobj = document.getElementById(planscope);
	var selvalue = selobj.innerHTML;
	setPlanScopeName(selvalue);

	hideDropdownBox();
	if (wpm.p0700 != '') {
		var hashvo = new ParameterSet();
		hashvo.setValue("oprType", "updatePlanScope");
		hashvo.setValue("plan_scope", planscope);
		hashvo.setValue("plan_id", wpm.p0700);
		var request = new Request({
					method : 'post',
					asynchronous : true,
					functionId : '9028000702'
				}, hashvo);
	} else {
		document.getElementById("planscopename").innerHTML = planscopename;// 计划未定制,不能修改可见范围wusy
		Ext.showAlert("当前计划还未制订,不能设置可见范围!");
	}
	var curmenuvalue = document.getElementById("planscope");
	curmenuvalue.value = planscope;
}

function setPlanScopeName(namevalue) {
	var nameobj = document.getElementById("planscopename");
	nameobj.innerHTML = '<img src="/workplan/image/suo.jpg" />' + namevalue
			+ '&nbsp; <img id="img_scope" src="/workplan/image/jiantou.png" />';
}
/**
 * 任务名称等获取焦点
 */
function taskFocus(obj) {
	var objclassName = obj.className;
	if (objclassName == "hj-wzm-four-rwmc") {
		if (obj.value == "任务名称" || obj.value == "创建任务" || obj.value == "创建子任务")
			obj.value = "";
	} else if (objclassName == "hj-wzm-four-rwms") {
		if (obj.value == "任务描述")
			obj.value = "";
	} else if (objclassName == "hj-wzm-four-cyr") {
		if (obj.value == "负责人")
			obj.value = "";
	} else if (objclassName == "hj-wzm-four-qc") {
		if (obj.value == "权重(0-100)")
			obj.value = "";
	} else if (objclassName == "hj-wzm-four-sjap") {
		if (obj.value == "开始日期" || obj.value == "结束日期")
			obj.value = "";
	}
}
/**
 * 任务名称等失去焦点
 */
function taskBlur(obj) {
	var objclassName = obj.className;
	if (objclassName == "hj-wzm-four-rwmc") {
		if (obj.value == "") {
			setTaskNameDefaultValue();
		}

	} else if (objclassName == "hj-wzm-four-rwms") {
		if (obj.value == "")
			obj.value = "任务描述";
	} else if (objclassName == "hj-wzm-four-cyr") {
		if (obj.value == "")
			obj.value = "负责人";
	} else if (objclassName == "hj-wzm-four-qc") {
		if (obj.value == "")
			obj.value = "权重(0-100)";
	}
	if (obj.id == "task_startdate") {
		if (obj.value == "")
			obj.value = "开始日期";
	}
	if (obj.id == "task_enddate") {
		if (obj.value == "")
			obj.value = "结束日期";
	}
}

/**
 * 新增任务
 */
function addTask() {
	// 填报范围校验 chent 20180329 add
	if(privCheck()!="0") {
		return ;
	}
	
	var selModel = g_tree.getSelectionModel();
	var records = selModel.getSelection();
	if(records.length>0)
		wpm.selNode = records[0];
	
	else wpm.selNode = null;
	var parentid = "";
	var p0723 = "";
	var seq = "";
	var othertask = "0";// 穿透任务标志；  默认为正常任务，此处的othertask必须有值，不然不能更新合计权重  haosl 2017-11-22
	if (wpm.selNode != null) {
		parentid = wpm.selNode.get('p0800');
		othertask = wpm.selNode.get('othertask');
		p0723 = wpm.selNode.get('p0723');
		if (parentid == "") {
			wpm.selNode = wpm.store.getRootNode();
		} else {
			seq = wpm.selNode.get('seq');
		}
	} else {
		p0723 = wpm.p0723;
		wpm.selNode = wpm.store.getRootNode();
	}
	var count = wpm.selNode.childNodes.length + 1;
	if (seq != "")
		seq = seq + "." + count;
	else
		seq = count;
	if (wpm.hasSumRow && (wpm.selNode === wpm.store.getRootNode())) {
		seq = count - 1;
	}

	var obj = document.getElementById("task_name");
	var task_name = obj.value;
	// var obj=document.getElementById("task_desc");
	// var task_desc=obj.value;
	var task_desc = ""
	var obj = document.getElementById("store-add-member");
	var task_cyr = obj.value;
	if (task_cyr == "undefined")
		task_cyr = "";
	var obj = document.getElementById("task_rank");
	var task_rank = obj.value;
	var obj = document.getElementById("task_startdate");
	var task_startdate = obj.value;
	var obj = document.getElementById("task_enddate");
	var task_enddate = obj.value;
	if (task_name == '任务名称' || task_name.replace(/(^\s*)|(\s*$)/g, '').length == 0 || task_name == "创建任务"
			|| task_name == "创建子任务") {
		Ext.Msg.alert("提示信息", "请输入任务名称！");
		return;
	}
	task_name = checkTaskName(task_name);// 替换编码时可能造成报错的字符 chent 20150916
	if (task_desc == '任务描述') {
		task_desc = "";
	}
	if (task_rank == '权重(0-100)') {
		task_rank = "";
	}
	if (task_startdate == '开始日期') {
		task_startdate = "";
	}
	if (task_enddate == '结束日期') {
		task_enddate = "";
	}
	// 加验证日期格式 global.js
	if (!workPlanCheckDate(task_startdate) || !workPlanCheckDate(task_enddate)) {
		return;
	}
	if (task_rank != "") {
		var rank = Ext.num(task_rank, -1);
		if ((rank > 100) || (rank < 0)) {
			Ext.Msg.alert("提示信息", "权重必须为数值型,范围为0-100");
			return;
		}
		var checkfloat = rank.toString().split(".");
		if (checkfloat[1]) {
			Ext.Msg.alert("提示信息", "权重只能输入整数");
			return;
		}
		task_rank = rank;
	}

	var object_id = "";
	if(othertask == "1"){
		object_id = wpm.selNode.get('objectid');
	}else{
		object_id = wpm.object_id;
	}
	// 检查权重是否超过100% 检查上级是否是考核任务
	var b = true;
	var hashvo = new ParameterSet();
	hashvo.setValue("oprType", "checkNewTask");
	hashvo.setValue("p0723", p0723);
	hashvo.setValue("periodType", wpm.period_type);
	hashvo.setValue("periodYear", wpm.period_year);
	hashvo.setValue("periodMonth", wpm.period_month);
	hashvo.setValue("periodWeek", wpm.period_week);
	hashvo.setValue("objectId", object_id);
	hashvo.setValue("task_rank", task_rank);
	hashvo.setValue("task_parentid", parentid);
	hashvo.setValue("othertask", othertask+"");// 父级是否是穿透任务
	var request = new Request({
				method : 'post',
				asynchronous : false,
				onSuccess : function(outparamters) {
					var info = outparamters.getValue("info");
					if("cannot_add" == info){
						Ext.Msg.alert('提示信息',"当前任务负责人的计划未发布，不能新增子任务！");
						b = false;
					}else if (info != "") {
						if (!confirm(info)) {
							b = false;
						}
					}
				},
				functionId : '9028000702'
			}, hashvo);
	if (!b) {
		return;
	}

	// 新增任务
	var scopeobj = document.getElementById("planscope");
	var planscope = scopeobj.value;
	
	var map = new HashMap();
	map.put("oprType", "addTask");
	map.put("p0723", p0723);
	map.put("periodType", wpm.period_type);
	map.put("periodYear", wpm.period_year);
	map.put("periodMonth", wpm.period_month);
	map.put("periodWeek", wpm.period_week);
	map.put("objectId", object_id);
	map.put("plan_scope", planscope);// 新增计划时使用
	// 任务名称、描述、负责人、权重、时间安排
	map.put("task_name", task_name);
	map.put("task_desc", task_desc);
	map.put("task_cyr", task_cyr);
	map.put("task_rank", task_rank+"");
	map.put("task_enddate", task_enddate);
	map.put("task_startdate", task_startdate);

	map.put("task_parentid", parentid+"");
	map.put("task_seq", seq+"");
	map.put("othertask", othertask+"");// 父级是否是穿透任务

	Rpc({functionId:'9028000702',async:false,success:addTask_ok},map);
}

function addTask_ok(response) {
	var result = Ext.decode(response.responseText); 
	if(result.succeed){
		var rowinfo =  Ext.decode(getDecodeStr(result.rowinfo));
		if (wpm.p0700 == "") {
			wpm.p0700 = result.p0700;
			setPlanStatus("0", "起草中");
		}
		getPlanAndBtnStatus();// 获得计划状态和按钮状态
		
		if (wpm.hasSumRow && (wpm.selNode === wpm.store.getRootNode())) {// 根节点
			wpm.selNode.insertChild(wpm.selNode.childNodes.length-1, rowinfo);
			//getPlanTaskList();
			updateRelatedRank(wpm.selNode.childNodes[wpm.selNode.childNodes.length - 2],result.clearIDs);
		} else {
			//不管父任务是否展开，都需要插入子任务  haosl 2017-12-1
			wpm.selNode.insertChild(wpm.selNode.childNodes.length,rowinfo);
			updateRelatedRank(wpm.selNode.childNodes[wpm.selNode.childNodes.length - 1],result.clearIDs);
//			if (!wpm.selNode.isExpanded()) {// 没有展开则要展开
//				var path = wpm.selNode.getPath('id'); 
//				g_tree.expandPath(path);  // 展开节点
//			}
			/*else{// 已经展开
				if(wpm.selNode.get('othertask') == "0"){// 如果当前节点是正常节点，则新增子任务时要删除掉穿透任务
					if(wpm.selNode.childNodes.length>0 && wpm.selNode.childNodes[0].get('othertask') == "1"){// 有子任务且是穿透任务
						wpm.selNode.removeAll();
					}
				}
			}*/
		}
		//这么修改是因为，当计划状态为未制定时，新增任务之后添加子任务，不能显示子任务。 haosl 2018-3-20
		if (!wpm.selNode.isExpanded()) {
			wpm.selNode.set("leaf", false);
			wpm.selNode.expand(true, true);
		}
		// 更改合计权重
		if(result.othertask == "0")//父级任务是非穿透任务才计算合计权重 lis 20160623
			updateSumRank(result.sum_rank);

		var obj1 = document.getElementById("task_name");
		document.getElementById("task_rank").value="权重(0-100)";
		document.getElementById("store-add-member").value="";
		obj1.value = "";
		obj1.focus();
		if (!wpm.myplan) {
			document.getElementById("btnPublish").style.display = "none";
			document.getElementById("btnRepublish").style.display = "none";
		}
	}else{
		Ext.Msg.alert('提示信息',result.message);
	}	
}
/**
 * 获取选中的任务id
 */
function setDeletedIds() {
	var to_deleted_ids = "";
	var selected = g_tree.getSelectionModel().getSelection();
	Ext.each(selected,function(rec,index){
		var p0800 = rec.get("p0800");
		if(!p0800 ||p0800 == "")
			return;
		to_deleted_ids = to_deleted_ids + "," + p0800;
	});
	return to_deleted_ids;
}

// 获取删除节点id lis-20160305
function selectDeletedIds() {
	var selected = g_tree.getSelectionModel().getSelection();
	Ext.each(selected,function(rec,index){
		var p0800 = rec.get("p0800");
		var othertask = rec.get("othertask");
		if(!p0800 ||p0800 == "")
			return;
		to_deleted_ids = to_deleted_ids + "," + p0800 + "_" + othertask;
	});
	return to_deleted_ids;
}

/**
 * 删除任务
 */
function deleteRow(curnode) {
	var selectRecords = g_tree.getSelectionModel().getSelection();
	if (curnode.childNodes.length > 0) {
		var cnt = curnode.childNodes.length - 1;
		for (var i = cnt; i >= 0; i--) {
			var node = curnode.childNodes[i];
			var ischeck = false;
			for(var j in selectRecords){
				if(node==selectRecords[j]){
					ischeck = true;
					break;
				}
			}
			var p0800 = node.get("p0800");
			if (p0800 == "") {
				continue;
			}
			if (ischeck) {
				curnode.removeChild(node,true);
				// 如果没有子任务，则刷新当前父节点
				if(curnode.childNodes.length==0){
					var opions = {node:curnode};// 进行封装
					wpm.store.load(opions);// 局部加载
				}
				var selModel = g_tree.getSelectionModel();// 刷新完后，判断是否还有选中行
				if(selModel.getSelection().length == 0){// 没有选中行
					var task_name_obj = document.getElementById("task_name");
					task_name_obj.value = "创建任务";
				}
			} else {
				if (node.childNodes.length > 0) {
					deleteRow(node);
				}
			}
		}
	}
}
// ====fuj
// 删除任务==========================================================================================================
/**
 * 进入删除操作的逻辑
 */
function delTask() {
	to_deleted_ids = "";
	to_deleted_ids = selectDeletedIds();
	if (to_deleted_ids == "") {
		Ext.Msg.alert("提示信息", "请选择要删除的任务！");
		return;
	}
	var temp = checkSubTask(to_deleted_ids);
	var flag = temp.split(",")[0];
	var info = temp.split(",")[1];
	if (flag == "true" && info == "") {
		Ext.MessageBox.show({
					title : "删除任务",
					msg : "该任务有子任务，是否同时删除子任务?",
					buttons : Ext.Msg.YESNOCANCEL,
					fn : function(e) {
						if (e == "yes") {
							delTask_WithChild1();
						} else if (e == "no") {
							delTask_WithoutChild();
						} else {
							return;
						}
					},
					width : 350,
					icon : Ext.MessageBox.INFO
				});

	} else if (flag == "false" && info == "") {
		delTask_WithChild();
	} else {
		return;
	}
}
/**
 * 删除任务前：判断是否有子任务的
 */
function checkSubTask(to_deleted_ids) {
	var hashvo = new ParameterSet();
	var temp = "";
	hashvo.setValue("oprType", "checkSubTask");
	hashvo.setValue("plan_id", wpm.p0700);
	hashvo.setValue("task_ids", to_deleted_ids);
	hashvo.setValue("p0723", wpm.p0723);
	hashvo.setValue("objectid", wpm.object_id);
	var request = new Request({
				method : 'post',
				asynchronous : false,
				onSuccess : isHave,
				functionId : '9028000702'
			}, hashvo);
	function isHave(outparamters) {
		if (outparamters) {
			var info = outparamters.getValue("info");
			info = getDecodeStr(info);
			if (info != "") {
				Ext.Msg.alert("提示信息", info);
			}
			temp = outparamters.getValue("flag") + ',' + info;// 这样处理可以保证方法返回值是布尔类型的
		}
	}
	return temp;
}
/**
 * 删除任务：当前任务的子任务一并删除
 */
function delTask_WithChild() {
	to_deleted_ids = "";
	to_deleted_ids = selectDeletedIds(wpm.store.getRootNode());
	if (to_deleted_ids != "") {
		/*
		 * if (confirm('确认要删除吗?')){ delTask_WithChild1(); }
		 */
		// 兼容火狐wusy
		Ext.MessageBox.confirm('提示信息', '确认要删除吗？', function(btn) {
					if (btn == 'yes') {
						delTask_WithChild1();
					} else {
						return;
					}
				});

	} else {
		Ext.Msg.alert("提示信息", "请选择要删除的任务！");
	}

}
/**
 * 删除任务：当前任务的子任务一并删除(上面的删除方法就是封装的本方法)
 */
function delTask_WithChild1() {
	var hashvo = new ParameterSet();
	hashvo.setValue("oprType", "delTask");
	hashvo.setValue("deltype", "withChild");
	hashvo.setValue("plan_id", wpm.p0700);
	hashvo.setValue("task_ids", to_deleted_ids);
	var request = new Request({
				method : 'post',
				asynchronous : false,
				onSuccess : delTask_ok1,
				functionId : '9028000702'
			}, hashvo);
}
/**
 * 删除任务：当前任务的子任务不删除
 */
function delTask_WithoutChild() {
	var hashvo = new ParameterSet();
	hashvo.setValue("oprType", "delTask");
	hashvo.setValue("deltype", "withoutChild");
	hashvo.setValue("plan_id", wpm.p0700);
	hashvo.setValue("task_ids", to_deleted_ids);
	var request = new Request({
				method : 'post',
				asynchronous : false,
				onSuccess : delTask_ok,
				functionId : '9028000702'
			}, hashvo);
}

function delTask_ok1(outparamters) {
	var info = outparamters.getValue("info");
	info = getDecodeStr(info);
	if (info != "") {
		Ext.Msg.alert('提示信息', info);
	} else {
		// 删除单个或全部下级的时候不刷新列表
		deleteRow(wpm.store.getRootNode());
		resetSeq(wpm.store.getRootNode(), "");
		// 更改合计权重
		var sum_rank = outparamters.getValue("sum_rank");
		updateSumRank(sum_rank);
		wpm.store.load();//复制全部删除后选中合计行  haosl add 2018-1-17
	}
}
function delTask_ok(outparamters) {
	var info = outparamters.getValue("info"); 
	info = getDecodeStr(info);
	if (info != "") {
		Ext.Msg.alert('提示信息', info);
	} else {
		// 不删除下级的时候刷新列表:重新加载
		getPlanTaskList();
	}
}
// ====fuj
// 删除任务==========================================================================================================
/**
 * 发布计划
 */
function publishPlan() {
	// 填报范围校验 chent 20180329 add
	if(privCheck()!="0") {
		return ;
	}
	
	if (wpm.p0700 != '') {
		// 兼容火狐wusy
		Ext.Msg.confirm('提示信息', '确定要发布计划吗？', function(btn) {
					if (btn == 'yes') {
						var hashvo = new ParameterSet();
						hashvo.setValue("oprType", "publishPlan");
						hashvo.setValue("plan_id", wpm.p0700);
						hashvo.setValue("planType",wpm.planType);
						var request = new Request({
									method : 'post',
									asynchronous : false,
									onSuccess : publishPlan_ok,
									functionId : '9028000702'
								}, hashvo);
					} else {
						return;
					}
				});

	} else {
		Ext.Msg.alert('提示信息', '计划还未制订,不能发布！');
	}
}

function publishPlan_ok(outparamters) {
	var info = outparamters.getValue("info");
	if (info == "2") {
		setPlanStatus("2", "已批准");
	} else if (info == "1"){
		setPlanStatus("1", "已发布");
	} else {
        info = getDecodeStr(info);
		Ext.showAlert(info+"<br/>");//IE下提示多的话，最后一行被覆盖了  haosl 2018-3-22
		return ;
	}
	initMessageContentlist("1", wpm.p0700);
}
/**
 * 批准计划 function approvePlan() { if (wpm.p0700!=''){ if (!confirm("确定要批准吗？")){
 * return; } var hashvo = new ParameterSet(); hashvo.setValue("oprType",
 * "approvePlan"); hashvo.setValue("plan_id", wpm.p0700); var request=new
 * Request({method:'post',asynchronous:false,
 * onSuccess:approvePlan_ok,functionId:'9028000702'},hashvo); } }
 */

/*
 * extjs实现confirm，解决手机批准提示框显示服务器ip问题
 */
function approvePlan() {
	Ext.Msg.confirm('提示信息', '确定要批准吗？', function(btn) {
				if (btn == 'yes') {
					var hashvo = new ParameterSet();
					hashvo.setValue("oprType", "approvePlan");
					hashvo.setValue("plan_id", wpm.p0700);
					var request = new Request({
								method : 'post',
								asynchronous : false,
								onSuccess : approvePlan_ok,
								functionId : '9028000702'
							}, hashvo);
				} else {

				}

			}, this);
}

function approvePlan_ok(outparamters) {
	setPlanStatus("2", "已批准");
	initMessageContentlist("1", wpm.p0700);
}

/**
 * 退回计划
 */
function rejectPlan() {
	if (wpm.p0700 != '') {
		Ext.MessageBox.prompt({
					title : "退回工作计划",
					msg : "原因：",
					width : 300,
					prompt : true,
					multiline : true,
					defaultTextHeight : 90,
					fn : function(id, msg) {
						if (id == "yes") {
							var hashvo = new ParameterSet();
							hashvo.setValue("oprType", "rejectPlan");
							hashvo.setValue("plan_id", wpm.p0700);
							hashvo.setValue("rejectInfo", getEncodeStr(msg));
							var request = new Request({
										method : 'post',
										asynchronous : false,
										onSuccess : rejectPlan_ok,
										functionId : '9028000702'
									}, hashvo);
						}
					},
					buttons : Ext.Msg.YESNO
				});

	}
}

function rejectPlan_ok(outparamters) {
	setPlanStatus("3", "已退回");
	refreshPlan(wpm.object_id);
	initMessageContentlist("1", wpm.p0700);
}

function dropdownMember(inputtext) {
	var data = [];
	var items = basic.global.directors;
	for (var i = 0; i < 3; i++) {
		data[data.length] = items[Math.floor(Math.random() * items.length)];
	}

	return data;
}

/**
 * 添加关注人
 */
/*
 * function addFollower(){ if (wpm.p0700==""){
 * Ext.Msg.alert("提示","此计划还未制订，不能添加关注人!"); return; } var promptBox =
 * document.getElementById(basic.prompt.promptBoxId); var cyrobj=
 * document.getElementById("addFollower"); promptBox.callerNode = cyrobj; //
 * 提示框的发起者 basic.util.lowerLeft(promptBox,
 * basic.util.getAbsoluteLocation(cyrobj)); // 将提示框定位到弹出提示框元素左下角
 * basic.prompt.openPrompt({ afterPickUp : function(id) { var hashvo = new
 * ParameterSet(); hashvo.setValue("oprType", "addFollower");
 * hashvo.setValue("plan_id", wpm.p0700); hashvo.setValue("followerId", id); var
 * request=new Request({method:'post',asynchronous:false,
 * onSuccess:search_ok,functionId:'9028000702'},hashvo);
 * 
 * function search_ok(outparamters) { var info = outparamters.getValue("info");
 * info=getDecodeStr(info); var obj = eval("("+info+")") ; obj =obj.follower;
 * var strhtml=""; for (var i=0;i<obj.length;i++){ strhtml
 * =strhtml+getDisplayFollowerItem(obj[i]); } if (strhtml!=""){
 * //alert(strhtml); var selobj=document.getElementById("followerdiv");
 * selobj.innerHTML=selobj.innerHTML+strhtml; } } }, search : function(keyword) {
 * var data = [];
 * 
 * var hashvo = new ParameterSet(); hashvo.setValue("oprType",
 * "dropdownFollower"); hashvo.setValue("plan_id", wpm.p0700);
 * hashvo.setValue("keyword", keyword);
 * 
 * var hashvo = new HashMap(); hashvo.put("oprType","dropdownFollower");
 * hashvo.put("plan_id",wpm.p0700); hashvo.put("keyword",keyword); Rpc(
 * {functionId : '9028000702',async: false,success : search_ok}, hashvo);
 * 
 * 
 * var request=new Request({method:'post',asynchronous:false,
 * onSuccess:search_ok,functionId:'9028000702'},hashvo);
 * 
 * function search_ok(outparamters) {
 * 
 * var info = outparamters.getValue("info"); info=getDecodeStr(info); // var obj =
 * eval("("+info+")") ; var obj =Ext.decode(info); data=obj.person_list;
 * 
 * 
 * var map = Ext.JSON.decode(outparamters.responseText); var info
 * =getDecodeStr(map.info); // var obj = eval("("+info+")") ; var obj
 * =Ext.decode(info); data=obj.person_list; } return data; } }) }
 */

function getDisplayParticipantItem(item) {
	var strhtml = "";
	strhtml += "<div class='director' onmouseover='basic.util.showDelSpan(this)' onmouseleave='basic.util.hideDelSpan(this)'>";
	strhtml += "<div class='director-name'>" + item.name + "</div>";
	strhtml += "<div class='director-del' itemId='" + item.id
			+ "' onclick='delParticipant(this)'>×</div>";
	strhtml += "</div>";

	return strhtml;
}

function delParticipant(del) {
	var storeNode = document.getElementById("store-add-member");
	var contentNode = document.getElementById("content-add-member");
	var items = storeNode.value.split(",");
	var index = basic.global.indexOf(items, del.getAttribute("itemId"));
	if (index > -1) {
		items.splice(index, 1);
	}
	storeNode.value = items.join(",");
	contentNode.removeChild(del.parentNode);
};
/**
 * 添加参与人
 */
/*
 * function addCyr(e){ e = e || window.event; var promptBox =
 * document.getElementById(basic.prompt.promptBoxId); var cyrobj=
 * document.getElementById("add-member"); promptBox.callerNode = cyrobj;
 * basic.util.lowerLeft(promptBox, basic.util.getAbsoluteLocation(cyrobj)); //
 * 将提示框定位到弹出提示框元素左下角 basic.prompt.openPrompt({ afterPickUp : function(id) { var
 * hashvo = new ParameterSet(); hashvo.setValue("oprType", "addParticipant");
 * hashvo.setValue("plan_id", wpm.p0700); hashvo.setValue("usrId", id); var
 * request=new Request({method:'post',asynchronous:false,
 * onSuccess:add_ok,functionId:'9028000702'},hashvo);
 * 
 * function add_ok(outparamters) { var info = outparamters.getValue("info");
 * info=getDecodeStr(info); var obj = eval("("+info+")") ; obj =obj.participant;
 * var strhtml=""; for (var i=0;i<obj.length;i++){ strhtml
 * =getDisplayParticipantItem(obj[i]); if (strhtml!=""){ var storeObj =
 * document.getElementById("store-add-member"); var
 * contentObj=document.getElementById("content-add-member");
 * 
 * var items = storeObj.value.split(","); var index =
 * basic.global.indexOf(items, obj[i].id); if (index > -1) {
 * Ext.Msg.alert("提示","不能重复添加参与人！") return; } if (contentObj.innerHTML=="负责人"){
 * contentObj.innerHTML=""; } var modnum=items.length%5; if ((items.length>1) &&
 * (modnum==1)){ //contentObj.innerHTML=contentObj.innerHTML+"</p>"
 * //contentObj.style.height=contentObj.style.height+25; }
 * contentObj.innerHTML=contentObj.innerHTML+strhtml;
 * 
 * contentObj.innerHTML=strhtml; // 隐藏域
 * //storeObj.value=storeObj.value+","+obj[i].id; storeObj.value=obj[i].id; } } } },
 * search : function(keyword) { var data = []; var hashvo = new ParameterSet();
 * var storeObj=document.getElementById("store-add-member"); var usrId
 * =storeObj.value; hashvo.setValue("oprType", "dropdownParticipant");
 * hashvo.setValue("usrId", usrId); hashvo.setValue("keyword", keyword); var
 * request=new Request({method:'post',asynchronous:false,
 * onSuccess:search_ok,functionId:'9028000702'},hashvo);
 * 
 * function search_ok(outparamters) { var info = outparamters.getValue("info");
 * info=getDecodeStr(info); var obj = eval("("+info+")") ; data=obj.person_list; }
 * return data; } }) // 取消冒泡 if (e.stopPropagation) { e.stopPropagation(); }
 * else { e.cancelBubble = true; } }
 */

/** 判断是否满足拖拽排序条件 */
function isCanRemoveRecord(ori_seq, to_seq) {
	if (ori_seq.indexOf('\.') == -1 && to_seq.indexOf('\.') == -1)
		return true;
	else if (ori_seq.indexOf('\.') != -1 && to_seq.indexOf('\.') != -1) {

		if (ori_seq.substring(0, ori_seq.lastIndexOf('\.')) == to_seq
				.substring(0, to_seq.lastIndexOf('\.')))
			return true;
		else
			return false;
	}
	return false;
}

// 拖拽调整任务顺序
function removeRecord(node, data, model, dropPosition, dropHandlers) {
	/*
	 * 废掉计划跟踪页面 if (wpm.plan_design=="2"){ return false; }
	 */
	var othertask = data.records[0].get("othertask");
	if(othertask == "1"){// 如果是穿透任务，不能拖动 lis 20160322
		return false;
	}
	ori_seq = data.records[0].get("seq");
	to_seq = model.get('seq');
	if (ori_seq == "" || to_seq == "") {
		return false;
	}
	var parentNode = model.parentNode;
	if (isCanRemoveRecord(ori_seq, to_seq)) {
		// dropHandlers.processDrop();
		for (var i = 0; i < parentNode.childNodes.length; i++) {
			var node = parentNode.childNodes[i];
			var seq = node.get('seq');
			if (seq == to_seq) {
				if (dropPosition == 'before')
					parentNode.insertChild(i, data.records[0]);
				else
					parentNode.insertChild(i + 1, data.records[0]);

				var hashvo = new HashMap();
				hashvo.put("dropPosition", dropPosition);
				hashvo.put("to_p0800", model.get('p0800'));
				hashvo.put("ori_p0800", data.records[0].get('p0800'));
				hashvo.put("p0700", wpm.p0700);
				hashvo.put("p0723", wpm.p0723);
				hashvo.put("object_id", wpm.object_id);
				Rpc({
							functionId : '9028000703',
							success : adjustSeqOK
						}, hashvo);

			}
		}
		dropHandlers.cancelDrop();
	} else
		dropHandlers.cancelDrop();
}

function adjustSeqOK(response) {

	resetSeq(wpm.store.getRootNode(), "");

}

// 重新生成任务序号
function resetSeq(node, seq_value) {

	for (var i = 0; i < node.childNodes.length; i++) {
		var _value = i + 1;
		if (seq_value.length > 0) {
			_value = seq_value + "." + (i + 1);
		}
		_value = _value + "";
		if (node.childNodes[i].get('seq') != "")
			node.childNodes[i].set('seq', _value);
		if (node.childNodes[i].childNodes.length > 0) {
			resetSeq(node.childNodes[i], _value);
		}
	}
}

// 计划沟通
function refreshMessageContentlist(obj) {
	basic.global.every(Ext.query(".hj-wzm-six-top-a"), function(node) {
				basic.global.removeClass(node, "hj-wzm-six-top-a");
			});
	obj.className += " hj-wzm-six-top-a";

	initMessageContentlist("1", wpm.p0700);
}

function reLoadData(outparamters) {
	var _tableData = outparamters.getValue("dataJson");
	// store对象
	// _tableData.children[0].superiorEvaluation = 2;
	var _fields = Ext.decode(getDecodeStr(outparamters.getValue("dataModel")));
	_columns = Ext.decode(getDecodeStr(outparamters.getValue("panelColumns")));
	_role = outparamters.getValue("role");
	g_w_tabledata = _tableData; //
	g_w_field = _fields; //
	g_w_column = _columns; // 
	// displayTaskList(_tableData,_fields,_columns);
	// getPlanAndBtnStatus();// 获得计划状态和按钮状态
}

/**
 * 刷新store
 */
function reloadStore() {
	var hashvo = new ParameterSet();
	hashvo.setValue("p0700", wpm.p0700);
	hashvo.setValue("p0723", wpm.p0723);
	hashvo.setValue("object_id", wpm.object_id);
	hashvo.setValue("p0725", wpm.p0725);
	hashvo.setValue("p0727", wpm.period_year);
	hashvo.setValue("p0729", wpm.period_month);
	hashvo.setValue("p0731", wpm.period_week);
	hashvo.setValue("showType", "1");
	hashvo.setValue("opt", "1");
	hashvo.setValue("exportSubTask",wpm.showSubTask);
	var request = new Request({
				method : 'post',
				asynchronous : false,
				onSuccess : reLoadData,
				functionId : '9028000701'
			}, hashvo);

}

/**
 * 获取任务列表
 */
function getPlanTaskList() {
	wpm.bAddChildTask = false;
	setTaskNameDefaultValue();
	// var a_plandesign = Ext.getDom("a_plandesign");
	// var a_plantrace = Ext.getDom("a_plantrace");
	// var div_plandesign = Ext.getDom("div_plandesign");
	var obj_editask = document.getElementById("editask");
	var a_deltask = Ext.getDom("a_deltask");
	var a_addMenu = Ext.getDom("a_addMenu");
	var a_addfollower = Ext.getDom("a_addfollower");
	var btnPublish = Ext.getDom("btnPublish");
	var a_planscopename = Ext.getDom("planscopename");
	var img_scope = Ext.getDom("img_scope");

	// aaaaaaaaaaaa 想换成不是自己的计划或不是下级计划(我关注的)
	if (!wpm.myplan && !wpm.subplan) {
		obj_editask.style.display = "none";
		a_deltask.style.display = "none";
		a_addMenu.style.display = "none";
		btnPublish.style.display = "none";
		if (wpm.myplan || wpm.subplan) {
			img_scope.style.display = "inline";
			a_planscopename.style.cursor = "pointer";
			Ext.get("planscopename").on("click", dropdownPlanScope);
		} else {
			img_scope.style.display = "none";
			a_planscopename.style.cursor = "";
			Ext.get("planscopename").un("click", dropdownPlanScope);
		}
		if (wpm.subplan) {
			//移动端访问是屏蔽按钮
			if(wpm.btn_publish_visible)
				a_addfollower.style.display = "block";
		} else {
			a_addfollower.style.display = "none";
		}
	} else {
		obj_editask.style.display = "block";
		//移动端访问是屏蔽按钮
		if(!wpm.isMobileBrowser){
			a_deltask.style.display = "block";
			a_addMenu.style.display = "block";
			a_addfollower.style.display = "block";
		}
		img_scope.style.display = "inline";
		a_planscopename.style.cursor = "pointer";
		Ext.get("planscopename").on("click", dropdownPlanScope);
		// 是否显示发布按钮
		if (wpm.btn_publish_visible)
			btnPublish.style.display = "block";
		else
			btnPublish.style.display = "none";
	}
	// 是否显示计划跟踪、计划制订按钮
	// if (!wpm.myplan){
	// //div_plandesign.style.display="none";
	// //a_plandesign.style.display="none";
	// //a_plantrace.style.display="none";
	// }
	// else {
	// //div_plandesign.style.display="block";
	// //a_plandesign.style.display="block";
	// //a_plantrace.style.display="block";
	// }
	// 跟踪、制订按钮的样式
	// if ( wpm.plan_design=="1"){
	// a_plandesign.className="hj-wzm-or-a";
	// a_plantrace.className="";
	// }
	// else {
	// a_plandesign.className="";
	// a_plantrace.className="hj-wzm-or-a";
	// }

	// if (wpm.from_flag=="email"|| wpm.from_flag=="hr"){
	// //div_plandesign.style.display="none";
	// //a_plandesign.style.display="none";
	// //a_plantrace.style.display="none";
	// }
	if (!wpm.myplan) {
		document.getElementById("btnPublish").style.display = "none";
		document.getElementById("btnRepublish").style.display = "none";
	}
	// 显示任务列表
	var hashvo = new ParameterSet();
	hashvo.setValue("p0700", wpm.p0700);
	hashvo.setValue("p0723", wpm.p0723);
	hashvo.setValue("object_id", wpm.object_id);
	hashvo.setValue("p0725", wpm.p0725);
	hashvo.setValue("p0727", wpm.period_year);
	hashvo.setValue("p0729", wpm.period_month);
	hashvo.setValue("p0731", wpm.period_week);
	// 废掉计划跟踪,直接传1
	hashvo.setValue("showType", "1");
	hashvo.setValue("exportSubTask",wpm.showSubTask);
	// hashvo.setValue("showType",wpm.plan_design); ////任务列表视图类型 1：计划制定 2：计划跟踪
	// var myDate = new Date();
	// var mytime=myDate.getSeconds();
	
	
	var request = new Request({
				method : 'post',
				asynchronous : false,
				onSuccess : loadData,
				functionId : '9028000701'
			}, hashvo);

}

/**
 * 加载任务列表数据
 */
function loadData(outparamters) {
	// var _tableData =
	// Ext.decode(getDecodeStr(outparamters.getValue("dataJson")));
	var _tableData = "";
	// store对象
	// _tableData.children[0].superiorEvaluation = 2;
	var _fields = Ext.decode(getDecodeStr(outparamters.getValue("dataModel")));
	_columns = Ext.decode(getDecodeStr(outparamters.getValue("panelColumns")));
	var p0801ColumnWidth = Ext.decode(outparamters.getValue("p0801ColumnWidth"));
	wpm.p0801ColumnWidth = p0801ColumnWidth;
	_role = outparamters.getValue("role");
	g_w_tabledata = _tableData; //
	g_w_field = _fields; //
	g_w_column = _columns; // 
	displayTaskList(_tableData, _fields, _columns);
	getPlanAndBtnStatus();// 获得计划状态和按钮状态
}

/** 重新发布或批准 lium */
function transitBatch(action) {
	var s = action == "publish" ? "重新发布" : "批准";
	
	if(action == "publish"){
		if(privCheck()!="0")
			return;
	}
	
	if (wpm.p0700 != '') {
		Ext.MessageBox.confirm('提示信息', '确定要' + s + '计划吗？', function(btn) {
			if (btn == 'yes') {
				var hashvo = new ParameterSet();
				hashvo.setValue("p0700", wpm.p0700);
				hashvo.setValue("action", action);
				hashvo.setValue("planType",wpm.planType);
				new Request({
					method : 'post',
					asynchronous : false,
					onSuccess : function(data) {
						if(action == "publish"){
							var info =  data.getValue("info");
							if(!Ext.isEmpty(info)){
								//提示信息显示不全的修改
								Ext.showAlert(info+"<br/>");
								//Ext.Msg.alert('提示信息', info);
								return ;
							}
						}
						getPlanTaskList();
						var isHaveDirectSuper = data
								.getValue("isHaveDirectSuper");
						document.getElementById("btnRepublish").style.display = "none";
						document.getElementById("btnApprove").style.display = "none";
						if (action == "publish" && isHaveDirectSuper=="true") {
							document.getElementById("planstatus").innerHTML = "已变更";
						} else {
							document.getElementById("planstatus").innerHTML = "已批准";
						}
						initMessageContentlist("1", wpm.p0700);
					},
					functionId : '9028000777'
				}, hashvo);
			}
		});
	} else {
		Ext.Msg.alert('提示信息', '计划还未制订,不能发布！');
	}
}

function secondeLoadData() {
	if (g_w_tabledata != null)
		displayTaskList(g_w_tabledata, g_w_field, g_w_column);
}

// 展开/关闭树节点时渲染星星
function showScore() {
	if (wpm.evaluationField.length > 0) {
		var ss = wpm.evaluationField.split(",");
		for (var i = 0; i < ss.length - 1; i++) {
			var dd = ss[i].split(":");
			var id = dd[0];
			var val = dd[1];
			initstar(id, val);
		}
	}
//	wpm.evaluationField = "";
}

/**
 * 加载任务列表数据
 */
function displayTaskList(_tableData, _fields, _columns) {
	var grid_width = getTreeGridWidth(true);
	if (grid_width > 0) {
		// if (wpm.grid_width<=0){
		wpm.grid_width = grid_width;
		// }
	}

	Ext.define('Task', {
				extend : 'Ext.data.TreeModel',
				fields : _fields
			});
	/*
	 * var store = Ext.create('Ext.data.TreeStore', { model : 'Task', root :
	 * _tableData, listeners : { 'datachanged': showScore } });
	 */
	
	var store = Ext.create('Ext.data.TreeStore', {
				proxy:{
			    	type: 'transaction',
	        		functionId:'9028000701',
			        extraParams:{
		        		p0700:wpm.p0700,
		        		p0723:wpm.p0723,
		        		object_id:wpm.object_id,
		        		p0725:wpm.p0725,
		        		p0727:wpm.period_year,
		        		p0729:wpm.period_month,
		        		p0731:wpm.period_week,
		        		exportSubTask:wpm.showSubTask,
		        		showType:"1",
		        		opt:"1"
			        },
			        reader: {
			            type: 'json',
			            root: 'dataJson'         	
			        }
			    },
			    fields : _fields,
				filters: [
				    function(item) {
				    	var selModel = g_tree.getSelectionModel();
						if(!!!wpm.selNode)
							wpm.selNode = selModel.getLastSelected();// 获得当前选中节点
				    	if(wpm.showSubTask=="0")
				    		return item.data.othertask == 0;
				    	else 
				    		return true;
				    }
				],
				listeners : {
					'datachanged': showScore,
					'load':function(store){
						resetSeq(wpm.store.getRootNode(), "");//重新生成序号 20160920				
						showScore();
					}
				},
				autoLoad:true
			});
	
	var bEnableDrag = true;
	// aaaaaaaaaaaaa 没有计划跟踪,直接注释掉应该可以
	/*
	 * if (wpm.plan_design=="2"){ bEnableDrag=false }
	 */
	
	// 穿透任务颜色
	if(!!!Ext.util.CSS.getRule(".x-grid-row-selected-other .x-grid-td"))
		Ext.util.CSS.createStyleSheet(".x-grid-row-selected-other .x-grid-td{background-color: #F5F5F5 !important}","otherId");
	if(!!!Ext.util.CSS.getRule(".x-grid-row-selected-mouseenter .x-grid-td"))
		Ext.util.CSS.createStyleSheet(".x-grid-row-selected-mouseenter .x-grid-td{background-color: rgb(255, 248, 210)}","mouseonId");
	if(!!!Ext.util.CSS.getRule(".x-grid-row-selected-itemclick .x-grid-td"))
		Ext.util.CSS.createStyleSheet(".x-grid-row-selected-itemclick .x-grid-td{background-color: rgb(255, 248, 210)}","clickId");
	//没有创建过的样式才创建，否则ie下报错 haosl add 2017-12-19
	if(!!!Ext.util.CSS.getRule(".getRowClassLock")){
		Ext.util.CSS.createStyleSheet(".getRowClassLock {pointer-events: none;}");
		Ext.util.CSS.createStyleSheet(".getRowClassLock td div div{display:none;}");
		Ext.util.CSS.createStyleSheet(".getRowClassLock td{border-bottom:0px;}");
	}
	Ext.destroy(g_tree);
	g_tree = Ext.create('Ext.tree.Panel', {
				id : 'gridView',
				width : wpm.grid_width,
				//height : 460,
				selModel:"checkboxmodel",
				renderTo : 'taskgrid',
				collapsible : true,
				useArrows : true,
				rootVisible : false,
				store : store,
				listeners : {
					'beforeitemmouseenter':function(view,record,el,index){
						//移除合计行效果 haosl 2017-07-20
						if(record.get("p0801")=="合计")
							return false;
					},
					'afteritemexpand' : showScore,
					'afteritemcollapse' : showScore,
					'columnresize' : resizeEvent,
					'itemmouseenter':function(view,record,el,index){
						if(record.get('othertask') == "1"){
							var rowCss = el.rows[0].className;
							if(rowCss.indexOf('x-grid-row-selected-itemclick') < 0){
								view.removeRowCls(index,'x-grid-row-selected-other');
								view.addRowCls(index,'x-grid-row-selected-mouseenter');
							}
						}
					},
					'itemmouseleave':function(view,record,el,index){// 鼠标离开行
						if(record.get('othertask') == "1"){
							var rowCss = el.rows[0].className;
							if(rowCss.indexOf('x-grid-row-selected-itemclick') < 0){// 不是选中行
								view.removeRowCls(index,'x-grid-row-selected-mouseenter');
								view.addRowCls(index,'x-grid-row-selected-other');
							}
						}
					},
					'beforeitemclick':function(view,record,el,index){
						//移除合计行效果 haosl 2017-07-20
						if(record.get("p0801")=='合计'){
						   //合计行被单击时取消“创建子任务”的任务提示  haosl  2018-2-7
						   setTaskNameDefaultValue(record);
						   return false;
						}
					},
					'select':function(row,record,index){
						wpm.rowIndex = index;
						rowCssChange(g_tree.getView(),record,index);
					}
				},
				viewConfig : {
					getRowClass: function(record,rowIndex,rowParams,store){// 改变行颜色
						if(record.data.p0801=='合计'){
							return "getRowClassLock";
						}
						if(record.data.othertask==1){
							return 'x-grid-row-selected-other';
						}else{
							return '';
						} 
					},
					plugins : {
						ptype : 'gridviewdragdrop',
						dragText : '拖拽记录完成排序',
						enableDrag : bEnableDrag
					},
					listeners : {
						beforedrop : removeRecord,
						itemclick : selectRow
					}
				},

				plugins : [Ext.create('Ext.grid.plugin.CellEditing', {
							listeners : {
								beforeedit : beforeEdit,
								edit:showScore,
								validateedit :validateEdit
							}
						})],
				header : false,
				hideHeaders : false,
				multiSelect : false,
				// forceFit:true,
				stripeRows : true,// 隔行换色
				columns : _columns
			});

	// g_tree.expandAll();
	// store.getRootNode().expand(true,true);
	wpm.store = store;
	if (wpm.task_path != "") {
		var temppath = wpm.task_path;
		var path = '';
		while (temppath.lastIndexOf(".") > 0) {
			var j = temppath.lastIndexOf(".");
			var str = temppath.substring(0, j)
			path = str + "/" + path;
			temppath = str;
		}
		if (path != "") {
			g_tree.expandPath("//" + path, "seq");
		}
	}
	// initMessageContentlist("1",wpm.p0700);
	// var rootnode=store.getRootNode();
	// store.getRootNode().appendChild({seq:'1',p0800:1,objectid:'usr00000009',type:1,expanded:
	// true,p0801:'1',p0803:'1',rank:0.0,timearrange:'',leaf:true});
	// showScore();
}
// 检查是否有编辑权限
function beforeEdit(editor, e) {
	basic.global.logonOut();
	// 检查是否有权限编辑
	if (e.record.get("p0800") == "") {
		e.cancel = true;
		return;
	}
	//如果是穿透任务，不可编辑 chent 20160505
	if(e.record.data.othertask == 1) {
		
		var hashvo = new ParameterSet();
		hashvo.setValue("oprType", "checkothertaskversion");//穿透任务编辑时，判断是否有权限
		hashvo.setValue("p0700", e.record.get("p0700"));
		hashvo.setValue("p0800", e.record.get("p0800"));
		hashvo.setValue("objectid", e.record.get("objectid"));
		hashvo.setValue("p0723", e.record.get("p0723"));
		hashvo.setValue("field", e.field);
		hashvo.setValue("othertask", e.record.get("othertask"));
		hashvo.setValue("subplan", wpm.subplan+"");
	
		var request = new Request({
			method : 'post',
			asynchronous : false,
			onSuccess : function(outparamters) {
				var b = true;
				var info = outparamters.getValue('info');
				if (info == "false") {
					e.cancel = true;
					return;
				}
			},
			functionId : '9028000702'
		}, hashvo);
	}
	// 大文本类型,需要转码
	if (e.column.field.xtype == "bigtextfield") {
		e.value = getDecodeStr(e.value);
	}
	var hashvo = new ParameterSet();
	hashvo.setValue("oprType", "checkUpdateP0835");
	hashvo.setValue("p0700", e.record.get("p0700"));
	hashvo.setValue("p0800", e.record.get("p0800"));
	hashvo.setValue("objectid", e.record.get("objectid"));
	hashvo.setValue("p0723", e.record.get("p0723"));
	hashvo.setValue("field", e.field);
	hashvo.setValue("othertask", e.record.get("othertask"));
	hashvo.setValue("subplan", wpm.subplan+"");
	hashvo.setValue("fromflag", wpm.from_flag);

	var request = new Request({
				method : 'post',
				asynchronous : false,
				onSuccess : function(outparamters) {
					var b = true;
					var info = outparamters.getValue(e.field);
					if (info == "false") {

						e.cancel = true;
						return;
					}
				},
				functionId : '9028000702'
			}, hashvo);
}
// 更新计划页面数据
function validateEdit(editor, e) {
	if (e.field == "rank") {// 编辑权重
		if (e.record.get("p0800") == "") {
			e.cancel = true;
			return;
		}
		if (e.value != "") {
			var rank = Ext.num(e.value, -1);
			if ((rank > 100) || (rank < 0)) {
				e.cancel = true;
				return;
			}
			if(rank == 0){//权重不可输入0 lis 21060628
				e.cancel = true;
				return;
			}
			var checkfloat = rank.toString().split(".");
			if (checkfloat[1]) {
				e.cancel = true;
				return;
			}
			var othertask = e.record.get("othertask");// 1：当前任务是穿透任务
			// 检查权重之和大于100及上下级是否有权重
			var hashvo = new ParameterSet();
			hashvo.setValue("oprType", "checkUpdateTask");
			hashvo.setValue("p0700", e.record.get("p0700"));
			hashvo.setValue("p0800", e.record.get("p0800"));
			hashvo.setValue("objectid", e.record.get("objectid"));
			hashvo.setValue("p0723", e.record.get("p0723"));
			hashvo.setValue("othertask", othertask);
			hashvo.setValue("field", e.field);
			hashvo.setValue("oldvalue", e.originalValue);
			hashvo.setValue("value", rank);

			var request = new Request({
				method : 'post',
				asynchronous : false,
				onSuccess : function(outparamters) {
					var b = true;
					var info = outparamters.getValue("info");
					if (info == "p0809false") {
						Ext.Msg.alert("提示信息", "该任务已取消,不能调整权重");
						e.cancel = true;
						return;
					}
					if (info != "" && othertask == "0") {
						// 提示信息太多，提示框增加宽度 chent 20160321
						Ext.MessageBox.confirm({
						 	title : "提示信息", 
						 	message : info,
						 	buttons: Ext.Msg.YESNO,
						 	icon: Ext.Msg.QUESTION,
						 	width:270,
							fn:function(optional) {
								if (optional == 'no') {
									e.record.data[e.field] = e.originalValue;
									e.record.commit();
									return;
								} else {
									afterEdit(editor, e);
								}
							}
						});
					} else {
						afterEdit(editor, e);
					}
				},
				functionId : '9028000702'
			}, hashvo);

		} else {
			afterEdit(editor, e);
		}
	} else if (e.field == "p0835") {// 编辑完成进度
		if (e.record.get("p0800") == "") {
			e.cancel = true;
			return;
		}
		// 计划进度加限制,首先保证输入的是数字wusy
		var reg = new RegExp("^(0|[1-9][0-9]*)$");
		if (!reg.test(e.value)) {
			e.cancel = true;
			return;
		}
		if (e.record.get("p0835") == "") {
			e.cancel = true;
			return;
		}
		if (e.value != "") {
			var p0835 = Ext.num(e.value, -1);
			if ((p0835 > 100) || (p0835 < 0)) {
				e.cancel = true;
				return;
			}
		} else {
			e.cancel = true;
			return;
		}
		// 检查进度
		var hashvo = new ParameterSet();
		hashvo.setValue("oprType", "checkUpdateP0835");
		hashvo.setValue("p0700", e.record.get("p0700"));
		hashvo.setValue("p0800", e.record.get("p0800"));
		hashvo.setValue("objectid", e.record.get("objectid"));
		hashvo.setValue("p0723", e.record.get("p0723"));
		hashvo.setValue("othertask", e.record.get("othertask"));
		hashvo.setValue("field", e.field);
		hashvo.setValue("oldvalue", e.originalValue);
		hashvo.setValue("value", p0835);

		var request = new Request({
					method : 'post',
					asynchronous : false,
					onSuccess : function(outparamters) {
						var b = true;
						var info = outparamters.getValue("info");
						if (info == "p0809false") {
							Ext.Msg.alert("提示信息", "该任务已取消,不能调整完成进度");
							e.cancel = true;
							return;
						}
						afterEdit(editor, e);
						// getPlanTaskList();// 刷新工作计划 //删除掉，见下面 chent 20160321						
						e.record.set("p0835", p0835);// 不刷新列表，直接更新store值
					},
					functionId : '9028000702'
				}, hashvo);

	}
	// 计划页面更新任务名p0801
	else if (e.field == "p0801") {// 编辑任务名称
		if (e.record.get("p0800") == "") {
			e.cancel = true;
			return;
		}
		// 为啥取length是0?擦擦擦, js没有trim()方法...
		var taskName = e.value;
		if (taskName.replace(/(^\s*)|(\s*$)/g, '').length == 0) {
			Ext.Msg.alert("提示", "请输入任务名称！");
			e.cancel = true;
			return;
		}
		taskName = checkTaskName(taskName);
		afterEdit(editor, e, taskName);

	} else if (e.field == "p0817") {// 编辑计划工时
		if (e.record.get("p0800") == "") {
			e.cancel = true;
			return;
		}
		if (e.value == "") {
			e.value = 0;
		}
		// 计划进度加限制,首先保证输入的是数字wusy
		var reg = new RegExp("^(0|[1-9][0-9]*)$");
		if (!reg.test(e.value)) {
			e.cancel = true;
			Ext.Msg.alert("提示", "请输入数字");
			return;
		}
		if (e.value.length > 8) {
			e.cancel = true;
			return;
		}
		afterEdit(editor, e);
	} else if (e.field == "p0803" || e.field == "p0841" || e.field == "p0837") {// 任务描述、评价标准、进度说明
		if (e.record.get("p0800") == "") {
			e.cancel = true;
			return;
		}
		afterEdit(editor, e);
	} else if (e.field == "p0813") {// 开始时间
		if (e.record.get("p0800") == "") {
			e.cancel = true;
			return;
		}
		afterEdit(editor, e);
	} else if (e.field == "p0815") {// 结束时间
		if (e.record.get("p0800") == "") {
			e.cancel = true;
			return;
		}
		afterEdit(editor, e);
	} else if (e.field == "p0823") {// 任务分类
		if (e.record.get("p0800") == "") {
			e.cancel = true;
			return;
		}
		afterEdit(editor, e);
	} else if (e.field == "p0825") {// 任务来源
		if (e.value != "") {
			var rank = Ext.num(e.value, -1);
			if ((rank > 100) || (rank < 0)) {
				e.cancel = true;
				return;
			}
			var checkfloat = rank.toString().split(".");
			if (checkfloat[1]) {
				e.cancel = true;
				return;
			}
		}
		afterEdit(editor, e);
	} else {
		if (e.record.get("p0800") == "") {
			e.cancel = true;
			return;
		}
		afterEdit(editor, e);
	}
}

/* 更新权重 */
function afterEdit(editor, e, taskName) {
	if (e.field == "rank") {
		var rank = e.value;
		function validateEdit_ok(outparamters) {
			updateRelatedRank(e.record,outparamters.getValue("clearIDs"));
			var p0833 = outparamters.getValue("p0833");
			if (p0833 == "3") {
				e.record.set("p0833", "3");
				e.record.set("p0801", e.record.get("p0801"));
			}
			
			if(e.record.get('othertask') == "0"){// 是正常任务时才更新合计权重
				// 更改合计权重
				var sum_rank = outparamters.getValue("sum_rank");
				updateSumRank(sum_rank);
			}

			// 本人的计划内是否包含需要报批的任务 lium
			var planstatusVal = document.getElementById("planstatus").innerHTML;
			// 为重新按钮加限制,只有计划在已批准的情况下,重新发布按钮才会出现 wusy
			if (planstatusVal == "已批准") {
				document.getElementById("planstatus").innerHTML = "已变更";
				document.getElementById("btnRepublish").style.display = "inline-block";
			}
			if (!wpm.myplan) {
				document.getElementById("btnRepublish").style.display = "none";
			}
			//去掉开头为0的权重值 haosl 2017-01-22
			if(rank != "")
				e.record.set("rank", parseInt(rank,10));
		}
		// 更新
		var hashvo = new ParameterSet();
		hashvo.setValue("oprType", "updateTask");
		hashvo.setValue("p0700", e.record.get("p0700"));
		hashvo.setValue("p0800", e.record.get("p0800"));
		hashvo.setValue("objectid", e.record.get("objectid"));
		hashvo.setValue("p0723", e.record.get("p0723"));
		hashvo.setValue("othertask", e.record.get("othertask"));
		hashvo.setValue("field", e.field);
		hashvo.setValue("oldvalue", e.originalValue);
		hashvo.setValue("value", rank);
		if (e.originalValue == rank) {
			return;
		}
		var request = new Request({
					method : 'post',
					asynchronous : true,
					onSuccess : validateEdit_ok,
					functionId : '9028000702'
				}, hashvo);

	} else if (e.field == "p0835") {
		saveTaskField(editor, e);
		// updateSchedule(editor, e);
	} else if (e.field == "p0801") {
		updateTaskName(editor, e, taskName);
	} else if (e.field == "p0817") {
		saveTaskField(editor, e);
	} else if (e.field == "p0803" || e.field == "p0841" || e.field == "p0837") {
		saveTaskField(editor, e);
	} else if (e.field == "p0813") {
		saveTaskField(editor, e);
	} else if (e.field == "p0815") {
		saveTaskField(editor, e);
	} else {
		saveTaskField(editor, e)
	}
	// showScore();
}

function saveTaskField(editor, e) {
	var hashvo = new ParameterSet();
	hashvo.setValue("oprType", "editTaskfields");
	hashvo.setValue("p0800", e.record.get("p0800"));
	hashvo.setValue("field", e.field);
	var value = e.value;
	if (e.column.field.xtype == "bigtextfield") {
		value = getEncodeStr(value);
	}
	hashvo.setValue("value", value);
	var request = new Request({
				method : 'post',
				asynchronous : false,
				onSuccess : function(outparamters) {
					var info = outparamters.getValue("info");
					if ("success" != info) {
						if(info.length>0){
							Ext.Msg.alert("提示信息", info);
						}
						e.cancel = true;
						return;
					}
					refreshtreepanel();  //haosl 2017-07-15  刷新表格的高度
				},
				functionId : '9028000702'
			}, hashvo);
}

/* 更新任务名 */
function updateTaskName(editor, e, taskName) {
	var hashvo = new ParameterSet();
	hashvo.setValue("oprType", "editTaskfields");
	hashvo.setValue("field", e.field);
	hashvo.setValue("p0800", e.record.get("p0800"));
	hashvo.setValue("taskName",getEncodeStr(taskName));
	var request = new Request({
				method : 'post',
				asynchronous : false,
				onSuccess : function(outparamters) {
					var info = outparamters.getValue("info");
					 if("repeat" == info){
						Ext.Msg.alert('提示信息',"已存在同名任务,不能保存！");
						e.cancel = true;
						return;
					}else if ("success" != info) {
						e.cancel = true;
						return;
					}
					 refreshtreepanel();  //haosl 2017-07-15  刷新表格的高度
				},
				functionId : '9028000702'
			}, hashvo);
}
/* 更改任务进度 */
// 原名为updateRank,改为updateSchedule贴切些
// function updateSchedule(editor, e){
// var p0835=e.value;
// var hashvo = new ParameterSet();
// hashvo.setValue("oprType", "editTaskProgress");
// hashvo.setValue("p0800", e.record.get("p0800"));
// hashvo.setValue("value", p0835);
// var request=new
// Request({method:'post',asynchronous:false,functionId:'9028000702'},hashvo);
// }
/* 更改合计权重 */
function updateSumRank(sum_rank) {
	if (wpm.store.getRootNode().childNodes.length > 0) {
		var node = wpm.store.getRootNode().childNodes[wpm.store.getRootNode().childNodes.length
				- 1];
		if (node.get("p0800") == "") {
			node.set("rank", sum_rank);
		}
	}
}

/* 更新上下级权重 */
function updateRelatedRank(node,clearIDs) {
	if(node)//lis 20160630
		if ((node.get("rank") != "") && (node.get("rank") != "0")) {
			resetChildRank(node,clearIDs);
			resetParentRank(node,clearIDs);
		}
}

// 将下级任务权重置为空;
function resetChildRank(node,clearIDs) {
	if(node.get('othertask') == "0"){// 当前任务不是穿透任务
		for (var i = 0; i < node.childNodes.length; i++) {
				if(node.childNodes[i].get('othertask') == "0"){// 子任务不是穿透任务时才会清空子任务权重
					node.childNodes[i].set('rank', "");
					if (node.childNodes[i].childNodes.length > 0) {
						resetChildRank(node.childNodes[i]);
					}
				}
		}
	}else{// 当前任务是穿透任务，其子任务肯定都是穿透任务
		for (var i = 0; i < node.childNodes.length; i++) {
			var childNode = node.childNodes[i];
			if(clearIDs){
				if(clearIDs.indexOf(","+childNode.get('id')+",") >= 0){
					node.childNodes[i].set('rank', "");
				}
			}
			if (childNode.childNodes.length > 0) {
				resetChildRank(node.childNodes[i],clearIDs);
			}
		}
	}
}

// 将上级任务权重置为空;
function resetParentRank(node,clearIDs) {
	if(node.get('othertask') == "0"){// 当前任务不是穿透任务，则其父任务肯定也不是穿透任务
		var parentNode = node.parentNode;
		while (parentNode != null) {
			parentNode.set('rank', "");
			parentNode = parentNode.parentNode;
		}
	}else{// 当前任务如果是穿透任务
		var parentNode = node.parentNode;
			while (parentNode != null) {
				if(parentNode.get('othertask') == "1"){// 如果父任务是穿透任务
					if(clearIDs){
						if(clearIDs.indexOf(","+parentNode.get('id')+",") >= 0){
							parentNode.set('rank', "");
							parentNode = parentNode.parentNode;
						}else
							parentNode = null;
					}else
						parentNode = null;
				}else{
					parentNode = null;
				}
			}
	}
}
/* 撤销/选中 行 */
function selectRow(obj, record, item, index, e, eOpts) {
	var d = new Ext.util.DelayedTask(function() {
				var selModel = g_tree.getSelectionModel();
				
				if (selModel.getLastSelected() != null) {
					var p0800 = selModel.getLastSelected().get('p0800');
					// wpm.task_path=selModel.getLastSelected().parentNode.getPath("seq");
					if (selected_p0800 == 0) {
						selected_p0800 = p0800;
						wpm.bAddChildTask = true;
					} else if (selected_p0800 == p0800) {
						selected_p0800 = 0;
//						g_tree.getSelectionModel().deselectAll(true);
//						g_tree.getSelectionModel().clearSelections();
						//linbz 撤销选中操作
						selModel.deselect(index, true);
						wpm.bAddChildTask = false;

					} else {
						selected_p0800 = p0800;
						wpm.bAddChildTask = true;
					}
					if (p0800 == "") {
//						g_tree.getSelectionModel().deselectAll(true);
//						g_tree.getSelectionModel().clearSelections();
						wpm.bAddChildTask = false;
					}
				}
				setTaskNameDefaultValue(record);
			});
	d.delay(200);

}

function dbClickRow(obj, record, item, index, e, eOpts) {

}
/* 任务名称提示字符串 */
function setTaskNameDefaultValue(record) {
	var task_name_obj = document.getElementById("task_name");
	if (task_name_obj == null)
		return;
	
	var othertask = "";
	var principalName = "";
	if(record){
		othertask = record.get('othertask');
		principalName = record.get('principal');
	}
	if (wpm.bAddChildTask && record && record.get('p0801')!="合计") {
		if("1" == othertask){
			// 穿透任务时子任务负责人中默认是当前任务负责人
		    var contentObj=document.getElementById("content-add-member");
		    var item={id:'',name:principalName};
		    var strhtml= getDisplayParticipantItem(item);
		    contentObj.innerHTML=strhtml;
		}else{
			// 任务负责人是默认本人
		    var contentObj=document.getElementById("content-add-member");
		    var item={id:'',name:defaultName};
		    var strhtml= getDisplayParticipantItem(item);
		    contentObj.innerHTML=strhtml;
		}
		task_name_obj.value = "创建子任务";
	} else {
		// 任务负责人中默认本人
	    var contentObj=document.getElementById("content-add-member");
	    var item={id:'',name:defaultName};
	    var strhtml= getDisplayParticipantItem(item);
	    contentObj.innerHTML=strhtml;
	    
		task_name_obj.value = "创建任务";
	}
}

/* 加样式 */
function setCellCss(cell) {
	//cell.style = "float:left;height:30px;padding-top:0px;padding-bottom:0px;line-height:30px;";
	//font-size:14px;上级评价渲染小星星的时候，font-size 会影响星星的显示   haosl  20180604
	if(cell.column.dataIndex=="superiorEvaluation"){
		cell.style = "float:left;padding:5px;white-space: normal;word-wrap: break-word;line-height:20px;";
	}else{
		cell.style = "font-size:14px;float:left;padding:5px;white-space: normal;word-wrap: break-word;line-height:20px;";
	}
}
/* 加样式 （最后一列） */
function setCellCssForEnd(cell) {
	//height:100%; haosl delete 最后一列无法换行
	//font-size:14px;上级评价渲染小星星的时候，font-size 会影响星星的显示   haosl  20180604
	if(cell.column.dataIndex=="superiorEvaluation"){
		cell.style = "float:left;padding:5px;white-space: normal;word-wrap: break-word;"
	}else{
		cell.style = "font-size:14px;float:left;padding:5px;white-space: normal;word-wrap: break-word;"
	}
	//设置最后一行的边框线  haosl  2017-9-30
	cell.tdStyle = "BORDER-RIGHT:  #C5C5C5 1pt solid;";
}
/* 任务名称加样式 */
function setCellTaskNameCss(cell) {
	//cell.style = "font-size:14px;float:left;padding:5px;white-space: normal;word-wrap: break-word;";
	//text-overflow:clip !important;此处因为任务名称做过兼容ie的操作导致ie11以下的版本任务名称后面都会有省略号，故去掉省略号  haosl 2018-2-7
	cell.style = "font-size:14px;white-space: normal;padding:5px;text-overflow:clip !important;";
}
/* 任务名称加样式（最后一列） */
function setCellTaskNameCssForEnd(cell) {
	cell.style = "font-size:14px;float:left;padding:5px;white-space: normal;word-wrap: break-word;"
		//设置最后一行的边框线  haosl  2017-9-30
		cell.tdStyle = "BORDER-RIGHT:  #C5C5C5 1pt solid;";
}

function startTimeOutFn(rowIndex) {
	document.getElementById("startImgage" + rowIndex).style.display = "none";
	document.getElementById("startTimeSubmit" + rowIndex).style.display = "none";
}

function endTimeOutFn(rowIndex) {
	document.getElementById("endImgage" + rowIndex).style.display = "none";
	document.getElementById("endTimeSubmit" + rowIndex).style.display = "none";
}

function startTimeOverFn(rowIndex, p0700, p0800, objectid, p0723) {
	if (p0800.length != 0) {
		var str = validateAuthority(p0700, p0800, objectid, p0723, "p0813");// 验证能否修改开始时间权限
	} else {
		return;
	}
	if (str == "true") {
		document.getElementById("startImgage" + rowIndex).style.display = "inline";
		document.getElementById("startTimeSubmit" + rowIndex).style.display = "inline";
	}
}

function endTimeOverFn(rowIndex, p0700, p0800, objectid, p0723) {
	if (p0800.length != 0) {
		var str = validateAuthority(p0700, p0800, objectid, p0723, "p0815");// 验证能否修改结束时间权限
	} else {
		return;
	}
	if (str == "true") {
		document.getElementById("endImgage" + rowIndex).style.display = "inline";
		document.getElementById("endTimeSubmit" + rowIndex).style.display = "inline";
	}
}

/* 计划页面修改任务结束时间 */
function editTaskEndTime(value, cell, record, rowIndex, columnIndex, store) {
	setCellCss(cell);
	var ap0800 = record.get("p0800");
	var ap0700 = record.get("p0700");
	var aobjectid = record.get("objectid");
	var ap0723 = record.get("p0723");
	var time = value == null ? "" : parseInt(value);
	if (ap0800.length == 0) {
		return time;
	}
	var displayTime = "";
	if (time > 0) {
		var times = new Date(parseInt(time));
		displayTime = parseInt(times.getFullYear()) + "."
				+ (parseInt(times.getMonth(),10) + 1) + "."
				+ parseInt(times.getDate(),10);
	}
	return displayTime;
	// var eles = "<div
	// onmouseover=endTimeOverFn('"+rowIndex+"','"+ap0700+"','"+ap0800+"','"+aobjectid+"','"+ap0723+"')
	// onmouseout=endTimeOutFn('"+rowIndex+"')>";
	// eles += "<input type='text'
	// style='border-left:0px;border-top:0px;border-right:0px;border-bottom:1px;width:80px;'
	// name='endTimeabc"+rowIndex+"' id='task_enddate"+rowIndex+"'
	// value='"+displayTime+"'>";
	// eles += "<div style='position:absolute;z-index:5555; display:inline;'>"
	// eles += "<img src='/workplan/image/workplantime.bmp' width='15px'
	// height='15px' style='display:none;' plugin='datetimeselector'
	// inputname='endTimeabc"+rowIndex+"' id='"+"endImgage"+rowIndex+"'
	// format='Y.m.d'/>";
	// eles += "<span>&nbsp;&nbsp;</span>";
	// eles += "<input type='button' value='保存' style='height:20px; width:40px;
	// display:none;' id='endTimeSubmit"+rowIndex+"'
	// onclick=saveEndTime('"+ap0800+"','"+displayTime+"','"+"task_enddate"+rowIndex+"')>";
	// eles += "</div>";
	// eles += "</div>";

}
// 最后一列
function editTaskEndTimeLast(value, cell, record, rowIndex, columnIndex, store) {
	setCellCssForEnd(cell);
	var ap0800 = record.get("p0800");
	var ap0700 = record.get("p0700");
	var aobjectid = record.get("objectid");
	var ap0723 = record.get("p0723");
	var time = value == null ? "" : parseInt(value);
	if (ap0800.length == 0) {
		return time;
	}
	var displayTime = "";
	if (time > 0) {
		var times = new Date(parseInt(time));
		displayTime = parseInt(times.getFullYear()) + "."
				+ (parseInt(times.getMonth(),10) + 1) + "."
				+ parseInt(times.getDate(),10);
	}
	return displayTime;
}

/* 计划页面修改任务开始时间 */
function editTaskStartTimeLast(value, cell, record, rowIndex, columnIndex,
		store) {
	setCellCssForEnd(cell);
	var ap0800 = record.get("p0800");
	var time = value == null ? "" : value;
	if (ap0800.length == 0) {
		return time;
	}
	var displayTime = "";
	if (time > 0) {
		var times = new Date(parseInt(time));
		displayTime = parseInt(times.getFullYear()) + "."
				+ (parseInt(times.getMonth(),10) + 1) + "."
				+ parseInt(times.getDate(),10);
	}
	return displayTime;
}

function editTaskStartTime(value, cell, record, rowIndex, columnIndex, store) {
	setCellCss(cell);
	var ap0800 = record.get("p0800");
	var time = value == null ? "" : value;
	if(time!=""){
		time = parseInt(time);
	}
	if (ap0800.length == 0) {
		return "";
	}
	var displayTime = "";
	if (time > 0) {
		var times = new Date(time);
		displayTime = parseInt(times.getFullYear()) + "."
				+ (parseInt(times.getMonth(),10) + 1) + "."
				+ parseInt(times.getDate(),10);
	}
	if(time.length==0){
		return "";
	}
	return displayTime;
}

/* 加样式 */
function addGridCss(value, cell, record, rowIndex, columnIndex, store) {
	setCellCss(cell);
	return value;
}

function addGridCssa(value, cell, record, rowIndex, columnIndex, store) {
	setCellCss(cell);
	if (value == "0") {
		return "";
	}
	return value;
}

function addGridCssP0823(value, cell, record, rowIndex, columnIndex, store) {
	setCellCss(cell);
	if (value && value.indexOf('`') > -1) {
		value = value.split('`')[1];
	}
	return value;
}

function addGridCssP0823end(value, cell, record, rowIndex, columnIndex, store) {
	setCellCssForEnd(cell);
	if (value && value.indexOf('`') > -1) {
		value = value.split('`')[1];
	}
	return value;
}
/* 加样式 (最后一列) */
function addGridCssEnd(value, cell, record, rowIndex, columnIndex, store) {
	setCellCssForEnd(cell);
	return value;
}

// 提交后重新算分
function reloadScore(planId, p0723, objectid, p0800) {
	var scoreWin = Ext.getCmp('scoreWin');
	if(scoreWin)
		scoreWin.close();
	var hashvo = new ParameterSet();
	hashvo.setValue("commandStr", "reloadScore");
	hashvo.setValue("p0700", planId);
	hashvo.setValue("p0723", p0723);
	hashvo.setValue("objectid", objectid);
	hashvo.setValue("p0800", p0800);
	// 查出平均分并重新渲染,刷新store
	var request = new Request({
				method : 'post',
				asynchronous : false,
				onSuccess : function(outparamters) {
					var sNum = outparamters.getValue("reloadScore");
					//lis 20160627 start
					var selModel = g_tree.getSelectionModel();
					if(!!!wpm.selNode)
						wpm.selNode = selModel.getLastSelected();// 获得当前选中节点
					// 星星重新渲染无需重新赋值，这样会导致前一次的评分结果因为store的变化而消失 chent 20180324 delete start	
					/*wpm.selNode.set("superiorEvaluation",sNum);
					var p0801 = wpm.selNode.get('p0801');
					wpm.selNode.set("p0801",p0801+" ");
					wpm.selNode.set("p0801",p0801);//重新赋值跟原来的不一样才会重新渲染,生成新的超链接*/
					// 星星重新渲染无需重新赋值，这样会导致前一次的评分结果因为store的变化而消失 chent 20180324 delete end
					//lis 20160627 end
					initstar('evaluation' + p0800, sNum);
					reloadStore();
				},
				functionId : '9028000765'
			}, hashvo);
}

var clickScore = true;// 计划页面弹出打分窗口后,想重新打分用的的变量
function editScore(planId, p0723, objectid, p0800, taskName, num,role) {
	if (role != '1') {// 直接上级,可以在计划页面直接评分
		return;
	}
	// 点击查询上级评价(searchEval)中的星星会调用该方法,禁掉
	var winSearch = Ext.getCmp('displayScoreWin');
	if(winSearch){
		return false;
	}
	// ------------------------------------
	var superEvaluations = [];// 上级评价结合
	var description = "";// 最近一条上级评价
	var isDisplay = false;// checkbox是否hidden
	var marginStr = "";// 发布按钮布局
	var hashvo = new ParameterSet();
	hashvo.setValue("p0700", planId);
	hashvo.setValue("myP0700", wpm.p0700);
	hashvo.setValue("p0800", p0800);
	hashvo.setValue("p0723", p0723);
	hashvo.setValue("objectid", objectid);
	new Request({
				asynchronous : false,
				onSuccess : function(data) {
					var evaluations = data.getValue("visible") || [];
					// 上级评价集合
					var se = 0;
					superEvaluations = [];
					var who = data.getValue("who");// who指当前登录用户和被查看人的关系
													// self:查看的自己, super:上级,
													// director:任务负责人,
													// member:任务成员/关注人
					if (evaluations.length > 0) {
						for (var i = 0; i < evaluations.length; i++) {
							// 获取上级评价集合
							if (evaluations[i].role=='superior') {
								superEvaluations[se] = evaluations[i];
								se++;
							}
						}
					}
					if (superEvaluations.length >= 1) {
						description = superEvaluations[0].editDescription;
						description = decode(description);
						isDisplay = false;
						marginStr = "0 0 0 102";
					} else {
						isDisplay = true;
						marginStr = "0 0 0 203";
					}
				},
				functionId : '9028000764'
			}, hashvo);

	// ------------------------------------
	var id = "evaluation" + p0800;
	var xPosition = Ext.fly(id).parent().parent().getX();
	var yPosition = Ext.fly(id).parent().parent().getY();

	// 所属组织
	var container = Ext.create('Ext.container.Container', {
		layout : {
			type : 'hbox',
			anchor : '90%'
		},
		margin : '0',
		// width:300,
		border : false,
		items : [{
					xtype : 'checkboxfield',
					id : 'newEvaluation',
					boxLabel : '添加新评价记录',
					labelAlign : 'left',
					hidden : isDisplay,
					name : 'isNew',
					inputValue : true
				}, {
					xtype : 'button',
					id : 'xxvv',
					text : '发布',
					labelAlign : 'right',
					width : 40,
					margin : marginStr,
					handler : function(o) {
						// basic.biz.publishEvaluation('plan');
						var evaluationText = Ext.getCmp('description')
								.getValue();// 评价内容
						var isNewEvaluation = Ext.getCmp('newEvaluation').checked;// 复选框
						var commandStr = 'updateEvaluation';
						if (isNewEvaluation) {
							commandStr = 'addEvaluation';
						}
						if(isDisplay){// bug 16151
							commandStr = 'addEvaluation';
						}
						var map = new HashMap();
						map.put("commandStr", commandStr);
						map.put("p0700", planId);
						map.put("p0723", p0723);
						map.put("objectid", objectid);
						map.put("p0800", p0800);
						num = clickScore ? scoreNumb : num;
						map.put("score", num);// 分数
						map.put("description", getEncodeStr(evaluationText));// 评价内容
						map.put("isNewEvaluation", isNewEvaluation);// 是否为新评价
						Rpc({
									functionId : '9028000765',
									async : false,
									success : function() {
										reloadScore(planId, p0723, objectid,
												p0800);
									},
									scope : this
								}, map);
					}
				}]
	});
	var formPanel = Ext.create('Ext.form.Panel', {
				bodyPadding : '10',
				margin : '0 auto',
				border : false,
				width : 300,
				// layout: 'anchor',
				items : [{
							xtype : 'panel',
							border : false,
							layout : 'hbox',
							items : [{
										xtype : 'label',
										text : '打分:'
									}, {
										xtype : 'container',
										html : '<span id="starId"></span>',
										margin : '0 0 0 20'
									}],
							margin : '0 0 10 0'
						}, {
							xtype : 'container',
							border : false,
							layout : 'hbox',
							items : [{
										xtype : 'label',
										text : '评语:'
							}, {
										xtype :'textareafield',// .x-form-text-wrap-default
																// ext-theme.css中的属性会多出一条线
										id : 'description',
										border: false,
										value : description,
										width : 200,
										grow : true,
										growMin : 100,
										margin : '0 0 0 20'
							}
							],
							margin : '0 0 10 0'
							/*
							 * xtype : 'textareafield', id : 'description',
							 * fieldLabel : '评语', labelAlign : 'left',
							 * labelWidth : 30, value : description, width :
							 * 300, margin : '0 0 20 0', grow : true
							 * //autoScroll : false, //minHeight : 50
							 * 
							 */
						}, container],
				renderTo : Ext.getBody()
			});
	// 初始化星星(打分控件)
	initstar('starId', num);
	// 弹出打分框
	Ext.create('Ext.window.Window', {
				id : 'scoreWin',
				closeToolText:'',
				x : xPosition,
				y : yPosition + 30,
				title : "对任务" + "\"" + taskName + "\"" + "的评价",
				layout : 'fit',
				width : 300,
				modal : true,
				minHeight : 200,
				autoScroll : true,
				border : false,
				items : [formPanel]
			}).show();

	// return false;
}

/**
 * 关闭
 */
function colseEvalWin() {

}

/**
 * 查询每条任务的上级评价
 * 
 * @param {}
 *            thisEle
 */
function searchEval(thisEle) {
	var role = thisEle.getAttribute('role');
	if(role!='1' && role!='0'){
		return;
	}
	var win = Ext.getCmp('displayScoreWin');
	if (win) {
		win.close();
	}
	var taskName = thisEle.getAttribute('taskName');
	var p0700 = thisEle.getAttribute('planId');
	var p0723 = thisEle.getAttribute('p0723');
	var p0800 = thisEle.getAttribute('taskId');
	var objectid = thisEle.getAttribute('objectid');
	var hashvo = new ParameterSet();
	hashvo.setValue("p0700", p0700);
	hashvo.setValue("myP0700", wpm.p0700);
	hashvo.setValue("p0800", p0800);
	hashvo.setValue("p0723", p0723);
	hashvo.setValue("objectid", objectid);
	new Request({
		asynchronous : false,
		onSuccess : function(data) {
			// var win = Ext.getCmp('displayScoreWin');
			// if(win){
			// win.close();
			// }
			var evaluations = data.getValue("visible") || [];
			// 上级评价集合
			var se = 0;
			var superEvaluations = [];
			var who = data.getValue("who");// who指当前登录用户和被查看人的关系 self:查看的自己,
											// super:上级, director:任务负责人,
											// member:任务成员/关注人
			if (evaluations.length > 0) {
				for (var i = 0; i < evaluations.length; i++) {
					// 获取上级评价集合
					if (evaluations[i].role == 'superior'  && evaluations[i].direcSuper==='true') {// 上级且是直接上级
						superEvaluations[se] = evaluations[i];
						se++;
					}
				}
			}
			// 得到集合遍历显示
			if (!((who == 'super' || who == 'self') && superEvaluations.length > 0)) {
				return;
			}
			// *****************************************************************************
			var id = "search" + p0800;
			var xPosition = Ext.fly(id).getX();
			var yPosition = Ext.fly(id).getY();
			var dwidth = Ext.fly(id).getWidth();
			// ------------------
			var basicContainer = Ext.create('Ext.container.Container', {
						id : "basicContainer",
						layout : {
							type : 'vbox'
							// align:'center'
						},
						width : 250,
						height : 50,
						// autoscroll: true,
						overflowX : 'hidden',
						overflowY : 'auto',//竖向滚动条自动
						renderTo : Ext.getBody(),
						border : 1,
						style : {
							borderColor : '#ffffff',
							borderStyle : 'solid',
							borderWidth : '0px'
						},
						// defaults: {
						// labelWidth: 80,
						// // 隐式创建容器通过指定的xtype
						// xtype: 'datefield',
						// flex: 1,
						// style: {
						// padding: '10px'
						// }
						// },
						items : []
					});

			// 遍历得到的集合
			for (var i = 0; i < superEvaluations.length; i++) {
				var date = superEvaluations[i].date;
				var score = superEvaluations[i].score;
				var description = superEvaluations[i].description;
				var evalContain = Ext.widget('container', {
					id : 'evalContain' + i,
					border : false,
					bodyPadding : 10,
					width : 191,
					minHeight : 50,
					// style: 'word-break: break-all; word-wrap:break-word;',
					layout : {
						type : 'vbox'
						// align: 'center'
					},
					items : [{
								xtype : 'label',
								text : date + '发布的评价:'
							}, {
								xtype : 'container',
								html : '<span id=' + '"dStar' + i + '"></span>'

							}, {
								xtype : 'container',
								width : 200,
								html : '<div style="table-layout:fixed;word-wrap:break-word;">'
										+ description + '</div>'
								// style: 'table-layout:
								// fixed;word-wrap:break-word;',
								// text: description
							}, {
								xtype : 'container',
								html : '<br />'
							}]
				});
				basicContainer.add(evalContain);
				initstar('dStar' + i, score);
			}

			// 显示打分历史列表窗口
			Ext.create('Ext.window.Window', {
						id : 'displayScoreWin',
						closeToolText : '',
						x : xPosition-250,
						y : yPosition+25,
						modal : false,
//						title : "对任务" + "\"" + taskName + "\"" + "的评价",
						title:"任务评价",//haosl 20160829
						layout : 'fit',
						width : 250,
						height : 200,
						resizable : false, // 不能通过拖拽改变窗口大小
						overflowX : 'hidden',// 横向滚动条隐藏
						overflowY : 'auto', // 竖向滚动条自动
						// autoScroll:true, //不能与overflowX/overflowY同时出现
						border : false,
						listeners: {
					        click: {
					            element: 'el', // bind to the underlying el
												// property on the panel
					            fn: function(){ return false; }
					        }
					        
						},
						items : [basicContainer]
					}).show();
			// *****************************************************************************

		},
		functionId : '9028000764'
	}, hashvo);
}

/* 上级评分 */
function addGridScore(value, cell, record, rowIndex, columnIndex, store) {
	setCellCssForEnd(cell);
// if(rowIndex==0){
// }
	var ap0800 = record.get("p0800");
	if (ap0800 == "") {
		return "";
	} else {
		if (value == "null" || value=="") {// 这种情况的value是字符串"null"
			value = 0;
		}
	}

	var ap0700 = record.get("p0700");
	var aobjectid = record.get("objectid");
	var ap0723 = record.get("p0723");
	var taskName = record.get("p0801");
	var role = record.get("role"); // 上下级关系 lis 20160322
	var prin = value;
	// var elems = "<div style='height:30px;width:100%;'
	// onmouseout=prinMouseOut('"+ap0800+"')
	// onmouseover=prinMouseOver('"+ap0700+"','"+ap0800+"','"+aobjectid+"','"+ap0723+"','"+ap0800+"')>"
	// elems += "<span id='prin"+ap0800+"'> "+prin+" </span>";
	// if(record.get("p0800").length != 0){
	// elems += "<div id='director-"+ap0800+"'
	// onclick=changePrin('"+ap0800+"','"+ap0800+"')
	// style='background-color:#FFF8D2;position:absolute;z-index:5555;height:30px;padding-top:0px;padding-bottom:0px;line-height:30px;cursor:pointer;display:none;'><font
	// style='color:#549fe3'>&nbsp;&nbsp;转给他人负责&nbsp;&nbsp;</font></div>"
	// }
	// elems += "</div>";onclick=editScore('"+ap0800+"','"+taskName+"')
	var elems = "<div style='height:30px;width:70%; float:left;' id='evaluation" + ap0800
			+ "' taskId='"
			+ ap0800
			+ "' p0723='"
			+ ap0723
			+ "' role='"
			+ role
			+ "' planId='"
			+ ap0700
			+ "' taskName='"
			+ taskName
			+ "' objectid='" + aobjectid + "'>";
	elems += "</div>";
	elems += "<div onmouseover='searchEval(this)' style='height:30px;width:25%; float:right;' taskName='"
			+ taskName
			+ "' id='search"
			+ ap0800
			+ "' taskId='"
			+ ap0800
			+ "' p0723='"
			+ ap0723
			+ "' role='"
			+ role
			+ "' planId='"
			+ ap0700
			+ "' objectid='"
			+ aobjectid + "'></div>";
	id = 'evaluation' + ap0800;
	wpm.evaluationField += id + ":" + value + ",";
	return elems;
}

function addGridCssEnda(value, cell, record, rowIndex, columnIndex, store) {
	setCellCssForEnd(cell);
	if (value == "0") {
		return "";
	}
	return value;
}
/* 时间安排样式加样式 */
function addTimeArrageCss(value, cell, record, rowIndex, columnIndex, store) {
	cell.style = "padding-left:0;border: inset 1px  #EEEDED;padding: 8px 6px 4px;"
			+ "BORDER-BOTTOM:  #EEEDED 0pt solid;"
			+ "BORDER-LEFT:  #EEEDED 0pt solid;"
			+ "BORDER-RIGHT:  #C5C5C5 0pt solid;"
			+ "BORDER-TOP:  #EEEDED 0pt solid;";
	return value;
}
/* 时间安排样式加样式 最后一行*/
function addTimeArrageCssForEnd(value, cell, record, rowIndex, columnIndex, store) {
	cell.style = "padding-left:0;border: inset 1px  #EEEDED;padding: 8px 6px 4px;"
			+ "BORDER-BOTTOM:  #EEEDED 0pt solid;"
			+ "BORDER-LEFT:  #EEEDED 0pt solid;"
			+ "BORDER-RIGHT:  #C5C5C5 0pt solid;"
			+ "BORDER-TOP:  #EEEDED 0pt solid;";
	//设置最后一行的边框线  haosl  2017-9-30
	cell.tdStyle = "BORDER-RIGHT:  #C5C5C5 1pt solid;";
	return value;
}

/* 选择框样式 */
function addCheckBoxGridCss(value, cell, record, rowIndex, columnIndex, store) {
	setCellCss(cell);
	if (record.get("p0800") == "") {
		return "";
	} else {
		return (new Ext.grid.column.CheckColumn).renderer(value);
	}
	// return value;
}
/* 
 * 跳转任务界面
 * 
 * personCF 校验是否有权限填写,
 * isCanFill 是否在填报期限的范围内
 *  */
function openTask(event, p0700, p0800, objectid, p0723, seq,othertask,recordId,superiorEvaluation,personCF,isCanFill,period_type) { // 添加事件对象，方便任务界面左侧的箭头定位
    //如果打开了选人窗口，要关闭掉选人
    var win =  Ext.getCmp('person_picker_single_view') || Ext.getCmp('person_picker_multiple_view');
    if (win) {
        win.close();
    }
   ;


	// lium
	// 从任务编辑界面 返回计划界面的url
	var periodtype = document.getElementById("periodtype").value;
	var periodyear = document.getElementById("periodyear").value;
	var periodmonth = document.getElementById("periodmonth").value;
	var returnurl = "/workplan/work_plan.do?br_query=link&p0700=" + wpm.p0700
			+ "&subobjectid=" + wpm.sub_object_id
			+ "&subpersonflag="
			+ wpm.sub_person_flag
			+ "&deptleader="
			+ wpm.deptleader
			+ "&plandesign="
			+ "1"// wpm.plan_design//废掉计划跟踪直接传1
			+ "&concerned_bteam=" + wpm.concerned_bteam
			+ "&concerned_cur_page=" + wpm.concerned_cur_page
			+ '&superconcernedjson=' + get_super_concerned_json()
			+ '&fromflag=' + wpm.from_flag + '&task_path=' + getEncodeStr(seq)
			+ '&performance=' + '0'
			+ '&returnurl=' + wpm.return_url;

	returnurl = getEncodeStr(returnurl);
	wpm.task_path = seq;
	var url = "/workplan/plan_task.do?br_task=link&p0700=" + p0700 + "&p0800=" + p0800 + "&myP0700=" + wpm.p0700 + "&recordId="+ recordId+ '&fromflag=' + wpm.from_flag
			+ "&objectid=" + objectid + "&p0723=" + p0723 + "&othertask=" + othertask + "&concerned_bteam=" + wpm.concerned_bteam + "&superiorEvaluation="+ superiorEvaluation
			+ "&returnurl=" + returnurl
			+"&personCF="+personCF
			+"&isCanFill="+isCanFill
			+"&period_type="+period_type;

	// 任务改由弹出小窗口代替打开新页面 lium
	var taskFrame = document.getElementById("taskFrame");
	var leftArrow = document.getElementById("leftArrow");
	var iframe_task = window.frames["iframe_task"];
	iframe_task.location.href = url;

	// 页面滚动的高度
	var QUIRKS = document.compatMode == "BackCompat" ? true : false; // 怪异模式(BackCompat)
	var BODY = QUIRKS ? document.body : document.documentElement;
	var scrollTop = BODY.scrollTop;

	// 定位左箭头
	var e = event || window.event;
	var _top = e.clientY || e.offsetY || e.pageY;
	leftArrow.style.top = (_top + scrollTop - 7) + "px";
	leftArrow.style.display = "block";
	taskFrame.style.top = "0px";
	taskFrame.style.display = "block";
}

/* 工作进度 */
function drawWarning(value, cell, record, rowIndex, columnIndex, store) {
	if (wpm.curjsp == "111hrplan") {
		cell.style = "padding-left:0;border: inset 1px  #EEEDED;BORDER-BOTTOM:  #EEEDED 0pt solid;BORDER-LEFT:  #EEEDED 0pt solid;BORDER-RIGHT:  #EEEDED 1pt solid;BORDER-TOP:  #EEEDED 0pt solid;";
	} else {
		setCellCss(cell);
	}

	var color = record.get("taskprogresscolor");
	if (color != "" && value != "") {
		var colorimg = "<img   width='15' height='15' src='/workplan/image/"
				+ color + ".png' />";
		var space = '';
		if(value.length == 1){
			space = '&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;';
		} else if(value.length == 2){
			space = '&nbsp;&nbsp;&nbsp;';
			
		} else if(value.length == 3){
			space = '&nbsp;';
		}
		value = colorimg + space + value + "%";
	}
	return value;

}
/**
 * 个人计划监控的工作分类的值处理
 * 
 * haosl 20160913
 */
function p0823(value){
	if (value && value.indexOf('`') > -1) {
		value = value.split('`')[1];
	}
	return value;
}
/* 工作进度 （最后一列） */
function drawWarningEnd(value, cell, record, rowIndex, columnIndex, store) {
	if (wpm.curjsp == "111hrplan") {
		cell.style = "padding-left:0;border: inset 1px  #EEEDED;BORDER-BOTTOM:  #EEEDED 0pt solid;BORDER-LEFT:  #EEEDED 0pt solid;BORDER-RIGHT:  #EEEDED 1pt solid;BORDER-TOP:  #EEEDED 0pt solid;"
		//设置最后一行的边框线  haosl  2017-9-30
		cell.tdStyle = "BORDER-RIGHT:  #C5C5C5 1pt solid;";
	} else {
		setCellCssForEnd(cell);
	}

	var color = record.get("taskprogresscolor");
	if (color != "" && value != "") {
		var colorimg = "<img   width='15' height='15' src='/workplan/image/"
				+ color + ".png' />";
		var space = '';
		if(value.length == 1){
			space = '&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;';
		} else if(value.length == 2){
			space = '&nbsp;&nbsp;&nbsp;';
			
		} else if(value.length == 3){
			space = '&nbsp;';
		}
		value = colorimg + space + value + "%";
	}
	return value;

}
/* 工作进度 */
/*
 * function drawWarningJd(value,cell,record,rowIndex,columnIndex,store){
 * cell.style="height:30px;"; var temps=value.split("_"); value="<img
 * width='15' height='15' src='/workplan/image/"+temps[0]+".png' />";
 * if(temps[1].length>0) value+="&nbsp;&nbsp;&nbsp;"+temps[1]+"%"; return value; }
 */

/* 任务名称添加链接 */
function addLink() {return p0801AddLink(arguments, false);}
function addLinkForEnd() {return p0801AddLink(arguments, true);}
function p0801AddLink(arg, isEnd){
	var value = arg[0];
	var cell = arg[1];
	var record = arg[2];
	// 获得当前任务的报批状态
	var p0811 = record.get("p0811");
	p0811 = parseInt(p0811);
	var approvalStatus = "";
	if (p0811 == "02") {
		approvalStatus = "已报批"
	} else {
		approvalStatus = "未报批"
	}
	if(isEnd){
		setCellTaskNameCssForEnd(cell);
	}else {
		setCellTaskNameCss(cell);
	}
	//还原特殊字符  haosl 2017-07-10
	if(!Ext.isEmpty(value)){
		value = value.replace(/</g,'&lt;').replace(/\n/g,'<br>').replace(/ /g,'&nbsp;');
 	}
	if (record.get("p0700") == "") {
		value = "<span style='padding-left:16px;float:left;font-size:14px;color:#000;font-size:14px;' title='" + value
				+ "'  > " + value + " </span>";
	}else {
		var style = 'float:left;font-size:14px;color:#549fe3;cursor:pointer;';
		if(Ext.isIE){//任务名称链接的渲染在ie下特殊处理 chent 20171009
			style += 'width:'+getP0801ClomnDisplayWidth(record)+'px;';
		}
		var clickFunc = "openTask(event,\"" + record.get("p0700") + "\",\"" + record.get("p0800") + "\",\""
		+ record.get("objectid") + "\",\"" + record.get("p0723")+ "\",\"" + record.get("seq") + "\",\"" + record.get("othertask") + "\",\"" 
		+ record.id + "\",\"" + record.get("superiorEvaluation") + "\",\"" 
		+ wpm.personCF_ + "\",\"" + wpm.isCanFill_ + "\",\"" + wpm.period_type + "\")";
		if(strHaveShowDetailPri == 'false'){// 没有查看任务详细权限 chent 20160413
			style = '';
			clickFunc = '';
		}
		value = "<span id='p0801_"+record.get("p0800")+"' style='"+style+"' onclick='"+clickFunc+"' > " + value + "</span>";
		if (record.get("p0833") == 1) {// 新增
			value = value + " &nbsp;<img   width='10' height='10' title='新增（"
					+ approvalStatus + "）' src='/workplan/image/is_add.png' />";
		} else if (record.get("p0833") == 2) {// 取消
			if (record.get("p0809") == "5") {// 已取消 5
				value = value
						+ " &nbsp;<img   width='10' height='10' title='取消' src='/workplan/image/is_cancel.png' />";
			} else {
				value = value
						+ " &nbsp;<img   width='10' height='10' title='取消（"
						+ approvalStatus
						+ "）' src='/workplan/image/is_cancel.png' />";
			}
		} else if (record.get("p0833") == 3) {// 变更
			value = value + " &nbsp;<img   width='10' height='10' title='已变更（"
					+ approvalStatus
					+ "）' src='/workplan/image/is_alter.png' />";
		}

		// value= value+" </span>";
	}
	return value;
}
/** 绘甘特图 */
function drawGrid_right(value, cell, record, rowIndex, columnIndex, store) {

	//cell.style = "border: inset 1px  #EEEDED;BORDER-BOTTOM:  #EEEDED 0pt solid;BORDER-LEFT:  #EEEDED 0pt solid;BORDER-RIGHT:  #EEEDED 1pt solid;BORDER-TOP:  #EEEDED 0pt solid;";
	if (value == 1) {
		value = "<span ><img   width='50' height='9' src='/images/epm_w_07.gif' /></span>";
	} else
		value = "";
	return value;
}
/** 绘甘特图 */
function drawGrid_left_right(value, cell, record, rowIndex, columnIndex, store) {

	//cell.style = "height:30px;padding-left:0;border: inset 1px  #EEEDED;BORDER-BOTTOM:  #EEEDED 0pt solid;BORDER-LEFT:  #EEEDED 1pt solid;BORDER-RIGHT:  #EEEDED 1pt solid;BORDER-TOP:  #EEEDED 0pt solid;";
	if (value == 1) {
		value = "<span ><img   width='50' height='9' src='/images/epm_w_07.gif' /></span>";
	} else
		value = "";
	return value;
}

function prinMouseOut(rowIndex) {
	if (prinMouse && (prinId == ("director-" + rowIndex))) {
		return false;
	}
	try {
		if (prinMouse) {
			clearTimeout(prinMouse);
		}
		var zzDiv = document.getElementById("director-" + rowIndex);
		if(zzDiv)
			zzDiv.style.display = "none";
		//重置prinID 否则容易选完人后，出不来“转给他人负责” haosl 2019年5月18日
        prinId = "";
	} catch (err) {
	}

}

// 验证权限(计划页面能否修改负责人)
function validateAuthority(p0700, p0800, objectid, p0723, str) {
	basic.global.logonOut();
	// 检查是否有权限编辑
	var hashvo = new ParameterSet();
	hashvo.setValue("oprType", "checkUpdateP0835");
	hashvo.setValue("p0700", p0700);
	hashvo.setValue("p0800", p0800);
	hashvo.setValue("objectid", objectid);
	hashvo.setValue("p0723", p0723);
	hashvo.setValue("field", str);
	//haosl add 需要增加fromflag参数，判断是否是hr_create 工作计划制定
	hashvo.setValue("fromflag", wpm.from_flag);
	var info;
	var request = new Request({
				method : 'post',
				asynchronous : false,
				onSuccess : function(outparamters) {
					var b = true;
					info = outparamters.getValue(str);
				},
				functionId : '9028000702'
			}, hashvo);
			
	return info;
}

var prinMouse = null; // setTimeout方法的变量名
var prinId = "";// 用于区分onmouseover和onmouseout事件监听的是否是同一个元素,如果是同一个元素,return false
function prinMouseOver(p0700, p0800, objectid, p0723, rowIndex) {
	if (prinId == ("director-" + rowIndex)) {
		return false;
	}
	if(document.getElementById(prinId)){//如果存在，则肯定是上一个的，隐藏 lis 20160625 
		document.getElementById(prinId).style.display = "none";
	}
	prinMouse = setTimeout(function() {
				if (p0800.length != 0) {
					var str = validateAuthority(p0700, p0800, objectid, p0723,
							"DIRECTOR");// 验证能否修改负责人权限
				} else {
					return;
				}
				if (str == "true") {
					var zzDiv = document.getElementById("director-" + rowIndex);
					if(zzDiv)
						zzDiv.style.display = "inline";
					prinId = "director-" + rowIndex;
				}
			}, 300);
}

function changePrin(p0800, rowIndex) {
	if (prinMouse) {
		clearTimeout(prinMouse);
	}
	var p0700 = wpm.p0700;
	var p0723 = Ext.getDom("p0723").value;
	var objectid = Ext.getDom("objectid").value;
	var except = [];
	var btn = document.getElementById("director-" + rowIndex);
	var hashvo = new ParameterSet();
	hashvo.setValue("condition", "2");
	hashvo.setValue("p0905", "1");// 1、负责人
	hashvo.setValue("type", "2");// 1、计划 2、任务 3、工作总结
	hashvo.setValue("id", p0800);
	new Request({
				asynchronous : false,
				onSuccess : function(data) {
					except = data.getValue("except");
				},
				functionId : '9028000769'
			}, hashvo);
	var picker = new PersonPicker({
				multiple : false,
				text : "选择任务负责人",
				deprecate : except,
				titleText : "选择任务负责人",
				header:true,//选人控件在列表上不能触发点击事件，故将选人控件的关闭按钮放开 chent
				isPrivExpression:false,//不启用高级权限
				callback : function(c) {
					var staffId = c.id;
					basic.util.saveStaff(staffId, btn, p0800, p0700, p0723,
							objectid, rowIndex);
				}
			}, btn);
	picker.open();
}

/* 重绘参与人 haosl 20160923 */
function rendercipant(value){
	var html = "";
	if(value){
		var partiArr = value.split("、");
		if(partiArr.length<2&&partiArr[0].length>0){
			var partiName = partiArr[0].split(":")[1]//参与人名称
			html+="<span title = "+partiName+">"+partiName+"</span>";
			return html;
		}else{
			var partiNames = "";
			for(var i = 0;i<partiArr.length && partiArr[i].length>0;i++){
				if(i==partiArr.length-1)
					partiNames+=partiArr[i].split(":")[1]
				else
					partiNames+=partiArr[i].split(":")[1]+"、";
			}
			html+="<span title = "+partiNames+">"+partiNames+"</span>";
		}
		return html;
	}
	return value;
}

/* 重绘负责人 */
function addPrincipalValue(value, cell, record, rowIndex, columnIndex, store) {
	setCellCss(cell);
	if(value == "null"){
		return "";
	}
	var ap0800 = record.get("p0800");
	var ap0700 = record.get("p0700");
	var aobjectid = record.get("objectid");
	var ap0723 = record.get("p0723");
	var prin = value;
	var elems = "<div style='height:100%;width:100%;' onmouseout=prinMouseOut('"
			+ ap0800
			+ "') onmouseover=prinMouseOver('"
			+ ap0700
			+ "','"
			+ ap0800
			+ "','"
			+ aobjectid
			+ "','"
			+ ap0723
			+ "','"
			+ ap0800
			+ "')>"
	elems += "<span id='prin" + ap0800 + "'> " + prin + " </span>";
	if (record.get("p0800").length != 0) {
		elems += "<div title='转给他人负责' id='director-"	//haosl	 20160713
				+ ap0800
				+ "' onclick=changePrin('"
				+ ap0800
				+ "','"
				+ ap0800
				+ "') style='background-color:#FFF8D2;position:absolute;z-index:5555;padding-top:0px;padding-bottom:0px;cursor:pointer;display:none;'><font style='color:#549fe3'>&nbsp;&nbsp;转给他人负责&nbsp;&nbsp;</font></div>"
	}
	elems += "</div>";
	return elems;
	/* value title的value后面有个)，导致报IE11非兼容模式InvalidCharacterError错 */
}
/* 重绘负责人(最后一列) */
function addPrincipalValueEnd(value, cell, record, rowIndex, columnIndex, store) {
	setCellCssForEnd(cell);
	value = "<span  title='" + value + "' > " + value + " </span>";
	return value;
	/* value title的value后面有个)，导致报IE11非兼容模式InvalidCharacterError错 */
}

/* 重绘权重单元格内容 */
function addRankValue(value, cell, record, rowIndex, columnIndex, store) {
	setCellCss(cell);
	if(record.get('p0801')=="合计"){
		return value+"%";	//合计列返回原值（如果是0的话返回0%）haosl 20160829
	}
	if (value == "")
		return value;
	if(value==0 || record.get('othertask') == "1")//穿透任务的权重不显示 lis 20160623  //haosl 20160829 	如果权重为0 显示为空
		return "";
	return value + "%";
}
/* 重绘权重单元格内容(最后一列) */
function addRankValueEnd(value, cell, record, rowIndex, columnIndex, store) {
	setCellCssForEnd(cell);

	if (value == "")
		return value;
	return value + "%";
}

function changeMember(p0800, rowIndex,p0700,p0723,objectid) {
	var btn = document.getElementById("member-" + rowIndex);
	var staffids = "";
	// 获取需排除人员
	var except = [];
	var hashvo = new ParameterSet();
	hashvo.setValue("condition", "3");// 3：任务详情界面选任务成员
	hashvo.setValue("type", "2");// 1、计划 2、任务 3、工作总结
	hashvo.setValue("id", p0800);
	new Request({
				asynchronous : false,
				onSuccess : function(data) {
					except = data.getValue("except");
				},
				functionId : '9028000769'
			}, hashvo);
	var picker = new PersonPicker({
				multiple : true,
				text : "添加任务成员",
				deprecate : except,
				titleText : "选择任务成员",
				isPrivExpression:false,//不启用高级权限
				callback : function(c) {
					for (var i = 0; i < c.length; i++) {
						var staffId = c[i].id;
						staffids += staffId + "`";//后台使用`分割，将-改为`  haosl  2018-3-2
						basic.util.saveStaff(staffId, btn, p0800, p0700, p0723,
								objectid, rowIndex);
					}
					basic.biz.sendstaffids(staffids);
					// basic.util.saveStaff(staffId,btn,p0800,p0700,p0723,objectid,rowIndex);
				}
			}, btn);
	picker.open();

}

function addPartMouseOut(id) {
	document.getElementById("member-" + id).style.display = "none";
}

function addPartMouseOVer(p0700, p0800, objectid, p0723, rowIndex) {
	if (p0800.length != 0) {
		var str = validateAuthority(p0700, p0800, objectid, p0723, "MEMBER");// 验证能否修改任务成员权限
		if (str == "true") {
			document.getElementById("member-" + rowIndex).style.display = "inline";
		}
	} else {
		return;
	}

}

/* 任务成员单元格内容 */
function addParticipant(value, cell, record, rowIndex, columnIndex, store) {
	var ap0800 = record.get("p0800");
	var ap0700 = record.get("p0700");
	var aobjectid = record.get("objectid");
	var ap0723 = record.get("p0723");
	value = value == null ? "" : value;
	if (ap0800.length == 0) {
		return value;
	}
	var html = '';
	setCellCss(cell);
	// if(value != "" && value != null){
	html += "<div id='partic" + ap0800			
			//修改任务成员列根据列宽自动显示人员，宽度不够加省略号。haosl
			+ "' style='height:30px; width:100%;white-space:nowrap; overflow:hidden;text-overflow:ellipsis;' onmouseout=addPartMouseOut('"
			+ ap0800 + "') onmouseover=addPartMouseOVer('" + ap0700 + "','"
			+ ap0800 + "','" + aobjectid + "','" + ap0723 + "','" + ap0800
			+ "')>";
	html += "<span id='particspan" + ap0800 + "'>";
	if (value.length > 0) {
		var s = value.split("、");
		for (var i = 0; i < s.length; i++) {
			var v = s[i].split(":")[1];
			html += v;
			if (i != (s.length - 1)) {
				html += "、";
			}
		}
	}
	html += "</span>";
	html += "<div id='member-"
			+ ap0800
			+ "' onclick=changeMember('"
			+ ap0800
			+ "','"
			+ ap0800
			+ "','"+ap0700+"','"+ap0723+"','"+aobjectid+"') style='background-color:#FFF8D2;position:absolute;z-index:5555;padding-top:0px;padding-bottom:0px;cursor:pointer;display:none;'><font style='color:#549fe3'>添加任务成员</font></div>"
	html += "</div>";
	// }
	return html;
}
/* 任务成员单元格内容(最后一列) */
function addParticipantEnd(value, cell, record, rowIndex, columnIndex, store) {
	var html = addParticipant(value, cell, record, rowIndex, columnIndex, store);
	setCellCssForEnd(cell);
	return html;
	
}
function addGridCssMemo(value, cell, record, rowIndex, columnIndex, store) {
	setCellCss(cell);
	if (value == "")
		return value;
	var vl = getDecodeStr(value);
	if(!Ext.isEmpty(vl)){
		vl = vl.replace(/</g,'&lt;').replace(/\n/g,'<br>').replace(/ /g,'&nbsp;');
 	}
	//var title = vl.replace(/<br>/g, '\n');// 提示时需要改成\n
	value = "<span> " + vl + " </span>";
	return value;
}
function addGridCssMemoEnd(value, cell, record, rowIndex, columnIndex, store) {
	setCellCssForEnd(cell);
	if (value == "")
		return value;
	var vl = getDecodeStr(value);
	//var title = vl.replace(/<br>/g, '\n');// 提示时需要改成\n
	if(!Ext.isEmpty(vl)){
		vl = vl.replace(/</g,'&lt;').replace(/\n/g,'<br>').replace(/ /g,'&nbsp;');
 	}
	value = "<span> " + vl + " </span>";
	return value;
}

/* 动态获取grid宽度 */
function getTreeGridWidth(bInit) {
	var gridObj = document.getElementById("taskgrid");
	var grid_width = 0;
	if (bInit) {
		grid_width = gridObj.offsetWidth;// -12;
	} else {
		grid_width = gridObj.offsetWidth;
	}
	return grid_width;
}
// 链接灰色
function fontgrey(obj) {
	obj.style.color = "#838383";
}
/*
 * objectid:计划所有者 bRefHummanMap:是否刷新人力地图导航栏
 */
function isNeedToRefresh(p0700,p0800,p0723,recordId) {
	var hashvo = new ParameterSet();
	hashvo.setValue("p0700", p0700);
	hashvo.setValue("p0723", p0723);
	hashvo.setValue("p0800", p0800);
	hashvo.setValue("showType", "1");
	hashvo.setValue("recordId", recordId);
	hashvo.setValue("exportSubTask",wpm.showSubTask);
	var request = new Request({
				method : 'post',
				asynchronous : false,
				onSuccess : refresh_ok,
				functionId : '9028000708'
			}, hashvo);
}
function refresh_ok(outparamters) {
	var refreshFlg = outparamters.getValue("isRefreshWorkPlan");// 工作计划页面变更flg
	var recordId = outparamters.getValue("recordId");//当前数据行recordid
	if (refreshFlg != null && refreshFlg == "true") {
		var info = outparamters.getValue("info");
		var rowData = outparamters.getValue("rowData");// 当前节点数据
		var record = g_tree.getStore().getById(recordId);
		if(record){
			if(record.data.othertask == "1"){//是穿透任务修改时刷新父节点 lis 20160627
				var opions = {node:record.parentNode,callback:function(records){
					if(wpm.rowIndex)
						rowCssChange(g_tree.getView(),record,wpm.rowIndex);
				}};// 进行封装
				wpm.store.load(opions);// 局部加载
			}else{
				Ext.each(rowData,function(obj){
					if(obj.dataName!="seq")//不需要设置序号 haosl 20160920
						record.set(obj.dataName,obj.dataValue);
				})
			}
		}
	}
}
/**
 * 获得计划状态和按钮状态
 */
function getPlanAndBtnStatus() {
	// 显示任务列表
	var hashvo = new ParameterSet();
	hashvo.setValue("p0700", wpm.p0700);
	hashvo.setValue("p0723", wpm.p0723);
	hashvo.setValue("object_id", wpm.object_id);
	hashvo.setValue("p0725", wpm.p0725);
	hashvo.setValue("p0727", wpm.period_year);
	hashvo.setValue("p0729", wpm.period_month);
	hashvo.setValue("p0731", wpm.period_week);
	// 废掉计划跟踪,直接传1
	hashvo.setValue("showType", "1");
	// hashvo.setValue("showType",wpm.plan_design); ////任务列表视图类型 1：计划制定 2：计划跟踪

	var request = new Request({
				method : 'post',
				asynchronous : false,
				onSuccess : loadDataforStatusAndBtn,
				functionId : '9028000709'
			}, hashvo);

}

/**
 * 加载任务列表数据
 */
function loadDataforStatusAndBtn(outparamters) {
	// 本人的计划内是否包含需要报批的任务
	var needToRepublish = Ext.decode(outparamters.getValue("needToRepublish"));
	var planChangeStatus = getDecodeStr(outparamters
			.getValue("planChangeStatus"));
	var btnRep = document.getElementById("btnRepublish");
	if (needToRepublish == "true" || needToRepublish == true) {
		btnRep.style.display = "inline-block";
	} else {
		btnRep.style.display = "none";
	}

	if (planChangeStatus != "") {
		document.getElementById("planstatus").innerHTML = planChangeStatus;
	}

	// 下属的计划内是否包含需要批准的任务
	var needToApprove = Ext.decode(outparamters.getValue("needToApprove"));
	var btnApp = document.getElementById("btnApprove");
	if (needToApprove == "true" || needToApprove == true) {
		btnApp.style.display = "inline-block";
		// 任务状态改为待批准,表示计划下有任务需要批准,计划状态不变
		document.getElementById("planstatus").innerHTML = "已变更";
	} else {
		btnApp.style.display = "none";
	}
}

/**
 * 栏目设置按钮事件
 */
function schemeSetting(op) {
	if (wpm.nopriv == "1") {
		return;
	}
	var showPublicPlan = false;
	if (op[1])
		showPublicPlan = true;
	if (!op[2])
		op[2] = 'Y:P08';
	// if(wpm.plan_design == "1"){
	op[0] = 'workPlan_position_0001';
	// }
	// aaaaaaaaaaaaaaa 直接注释掉
	/*
	 * else if(wpm.plan_design == "2"){ op[0] = 'workPlan_position_0002'; }
	 */

	// Ext.Loader.setPath('EHR.tableFactory.plugins',
	// "/components/tableFactory/plugins");
	// 加载自定义类
// Ext.Loader.loadScript({
// url : '/components/tableFactory/customs/ext_custom.js'
// });
	Ext.require("EHR.tableFactory.plugins.SchemeSetting", function() {
				var window = new EHR.tableFactory.plugins.SchemeSetting({
							subModuleId : op[0],
							viewConfig:{publicPlan:showPublicPlan,merge:false,sum:false,pageSize:false},// pageSize:false隐藏栏目设置的每页条数
							itemKeyFunctionId:"",//栏目设置变更，新增配置  lis 20160625 
							schemeItemKey : op[2],
							showPageSize : false,
							closeAction : closeSettingWindow
						});
			});

	// var settingUrl =
	// "/components/tableFactory/gridSetting/gridSetting.jsp?tablekey="+op[0]+"&showPublicPlan="+showPublicPlan+"&schemeItemKey="+op[2];
	// Ext.widget("window",{
	// modal:true,
	// id:'settingWindow',
	// height:600,
	// title:'栏目设置',
	// resizable:false,
	// width:800,
	// html:"<iframe frameBorder=0 height='100%' width='100%'
	// src='"+settingUrl+"'></iframe>"
	// }).show();
};

function closeSettingWindow() {
	// window.location.reload();
	// if(wpm.plan_design == "1"){
	planDesign();
	// }
	// aaaaaaaaaaaa 直接注掉
	/*
	 * else if(wpm.plan_design == "2"){ planTrace(); }
	 */
}
function checkTaskName(str) {
	var tmp = str.replace(/\%/g, "％");
	var tmp1 = tmp.replace(/\^/g, "＾");
	var tmp2 = tmp1.replace(/\&/g, "＆");
	var tmp3 = tmp2.replace(/\\/g, "＼");

	return tmp3;
}

// 隐藏右侧人力地图
function hideRightDiv() {
	document.getElementById("rightDiv").style.display = "none";
	if(document.getElementById("leftDiv"))
		document.getElementById("leftDiv").style.marginRight = "20px";
	if(document.getElementById("showRightDiv"))
	document.getElementById("showRightDiv").style.display = "inline";
}
// 显示右侧人力地图
function showRightDiv() {
	document.getElementById("rightDiv").style.display = "inline";
	if(document.getElementById("leftDiv"))
		document.getElementById("leftDiv").style.marginRight = "173px";//陈总提 左右两边边距应保持一致 20170406
	if(document.getElementById("showRightDiv"))
		document.getElementById("showRightDiv").style.display = "none";
}

//行样式改变 lis 20160627
function rowCssChange(view,record,index){
		for(var i=0; i<view.getNodes().length;i++){// 清除其他选中样式
			var node = view.getNode(i);
			var rowCss = node.rows[0].className;
			var recordtemp = view.getRecord(node);
			if(rowCss.indexOf('x-grid-row-selected-itemclick') >= 0){// 是选中行，则清除选中样式
				if(recordtemp.get('othertask') == "1"){// 如果是穿透任务则添加穿透样式
					view.addRowCls(i,'x-grid-row-selected-other');
				}
				view.removeRowCls(i,'x-grid-row-selected-itemclick');
			}
		}
		//移除合计行选中样式  haosl 2017-07-20
		var node = view.getNode(view.getNodes().length-1);
		node.className = node.className.replace("x-grid-item-selected","");
		if(record.get('othertask') == "1"){
			view.removeRowCls(index,'x-grid-row-selected-other');
			view.removeRowCls(index,'x-grid-row-selected-mouseenter');
			view.addRowCls(index,'x-grid-row-selected-itemclick');
		}
}
/**
 * 刷新表格视图
 * @returns
 */
function refreshtreepanel(){
	var task = new Ext.util.DelayedTask(function(){
		var grid = Ext.getCmp("gridView");
		if(grid)
			grid.view.refresh();
		showScore();
	});
	task.delay(100);
}
function getP0801ClomnDisplayWidth(record){
	var depth = record.getDepth();// 节点处于第几层级
	return (wpm.p0801ColumnWidth-16*2-(depth-1)*16-12);
}
/**
 * 是否显示下属任务（即穿透任务）
 * @param isShow
 * @returns
 */
function showOrHideSubTask(isShow){
	var hideSubTask = Ext.getDom("hideSubTask");
	var showSubtask = Ext.getDom("showSubtask");
	if(isShow){
		if(showSubtask)
			showSubtask.style.display = "none";
		if(hideSubTask)
			hideSubTask.style.display = "inline";
		wpm.showSubTask = "1";
	}else{
		if(showSubtask)
			showSubtask.style.display = "inline";
		if(hideSubTask)
			hideSubTask.style.display = "none";
		wpm.showSubTask = "0";
	}
	var curUsername = document.getElementById("curUsername");
	if (curUsername != null) {
		var name = getEncodeStr(curUsername.value);
		delCookie("workplan_subtask_" + name);
		// 存cookie需要设置有效期
		var Days = 30;
		var exp = new Date();
		exp.setTime(exp.getTime() + Days * 24 * 60 * 60 * 1000 * 6);
		document.cookie = "workplan_subtask_" + name + "=" + wpm.showSubTask
				+ ";expires=" + exp.toGMTString();
	}
	//刷新计划列表
	getPlanTaskList();
}
// 填报范围校验 chent 20180329
/**
 * return =0 校验通过 =1 没有计划权限 =2 不在填报期限内
 * @param {} isShowALert
 * @return {Boolean}
 */
function privCheck(isShowALert){
	var flag = true;
	
	var cycleFunction;
    var str = "";
    if(wpm.planType=="person"){
    	cycleFunction = wpm.cycleFunction_person;
    	str+="个人";
    }else if(wpm.planType=="org") {
        cycleFunction = wpm.cycleFunction_org;
        str+="部门";
    }
		
    // 若本人填写权限明细中没有该类型的权限，则不允许复制
    // 如果来自工作计划监控页面的编辑按钮，不控制权限 chent 20171125 add
    //cycleFunction得做非空判断。 haosl 2019-5-20
    if(cycleFunction && cycleFunction.indexOf(','+wpm.period_type+",") == -1 && wpm.from_flag != "hr_create"){
    	if (wpm.period_type == '1') {// 年
    		str += "年计划";
		} else if (wpm.period_type == '2') {// 半年
			str += "半年计划";
		} else if (wpm.period_type == '3') {// 季度
			str += "季度计划";
		} else if (wpm.period_type == '4') {// 月
			str += "月计划";
		} else if (wpm.period_type == '5') {// 周
			str += "周计划";
		}
		if(isShowALert!="false")
    		Ext.Msg.alert('提示信息', '您没有填写'+str+'的权限！');
    	return "1";
    }
    //填报期限控制，不在允许期限内不允许填报计划
    var key = "";
    switch(wpm.period_type){
    	case "1"://年
    		key="p0";
    		break;
    	case "2"://半年
    		key="p1";
    		break;
    	case "3"://季度
    		key="p2";
    		break;
    	case "4"://月
    		key="p3";
    		break;
    	case "5"://周
    		key="p4";
    		break;
    	
    }
	var validPre = "";
	var validNow = "";
	for(var p in wpm.cycle_function){
		var obj = wpm.cycle_function[p];
		if(obj[key]){
			validPre = obj[key].pre;
			validNow = obj[key].now;
			break;
		}
	}
	var map = new HashMap();
	map.put("periodType",wpm.period_type);
	map.put("periodYear",wpm.period_year);
	map.put("periodMonth",wpm.period_month);
	map.put("periodWeek",wpm.period_week);
	map.put("validPre",validPre);
	map.put("validNow",validNow);
	map.put("oprType","validPreNow");
	Rpc({functionId:'9028000702',async:false,success:function(form){
		 var result = Ext.decode(form.responseText);
		 flag = result.fillPlan;
		 if(!result.succeed){
			 Ext.showAlert(result.message);
			 return;
		 }
		 if(!flag && isShowALert!="false"){
		 	Ext.Msg.alert('提示信息', '计划不在填报期限内,不能填报！');
		 }
	}},map);
	if(!flag)
   	   return "2";
   	else
 	   return "0";
}
// 设置填报范围变量 chent 20180329
function setPriv(){
	// 个人计划填报范围整理
	wpm.cycleFunction_person = ",";
    for(var p in wpm.person_cycle_function){
        var obj = wpm.person_cycle_function[p];
        if(obj['p0'] != undefined ){// 年
        	wpm.cycleFunction_person += "1,";
        }else if(obj['p1'] != undefined ){// 半年
        	wpm.cycleFunction_person += "2,";
        }else if(obj['p2'] != undefined ){// 季度
        	wpm.cycleFunction_person += "3,";
        }else if(obj['p3'] != undefined ){// 月
        	wpm.cycleFunction_person += "4,";
        }else if(obj['p4'] != undefined ){// 周
        	wpm.cycleFunction_person += "5,";
        }else{
            continue;
        }
    }
    
    // 部门计划填报范围整理
    wpm.cycleFunction_org = ",";
    for(var p in wpm.org_cycle_function){
        var obj = wpm.org_cycle_function[p];
        if(obj['p0'] != undefined ){// 年
        	wpm.cycleFunction_org += "1,";
        }else if(obj['p1'] != undefined ){// 半年
        	wpm.cycleFunction_org += "2,";
        }else if(obj['p2'] != undefined ){// 季度
        	wpm.cycleFunction_org += "3,";
        }else if(obj['p3'] != undefined ){// 月
        	wpm.cycleFunction_org += "4,";
        }else if(obj['p4'] != undefined ){// 周
        	wpm.cycleFunction_org += "5,";
        }else{
            continue;
        }
    }
}

/**
 * 移动终端浏览器版本信息
 * @return true = 移动端 | false = pc端
 */
function isMobileBrowser(){
	var isMobileBrowser = false;
       var u = navigator.userAgent.toLowerCase();  
	   isMobileBrowser = u.match(/android/i) == "android" //android
    	  || u.match(/ucweb/i) == "ucweb" //uc 
          || u.match(/rv:1.2.3.4/i) == "rv:1.2.3.4" //uc7
          || u.match(/midp/i) == "midp" //  
          || !!u.match(/\(i[^;]+;( U;)? CPU.+Mac OS X/) //ios终端  
          || u.match(/windows ce/i) == "windows ce" //
          || u.match(/iphone os/i) == "iphone os" //
          || u.match(/ipad/i) == "ipad" //是否iPad  
          || u.match(/windows mobile/i) == "windows mobile" //
          || u.match(/MicroMessenger/i)=="MicroMessenger"//是否是微信打开
    	return isMobileBrowser;
    }