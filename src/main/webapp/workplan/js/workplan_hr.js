var Const_QuickQueryHint="请输入姓名/拼音简称/部门/email";
var workplan= workplan||{};
workplan.main=workplan.main || {}
workplan.main.global=workplan.main.global || {

}
workplan.main.hr=workplan.main.hr || {
    plan_type:"1",
    view_type:"1",
    period_type:"",
    period_year:"2013",
    period_month:"1",
    period_week:"1",
    week_num:4,//本期间周个数
    old_period_month:"1",
    curjsp:"", //区分哪个jsp调用的， selfplan teamplan 因为有共有变量及共用函数
    submit_type:"",//查看类型 空：所有，0：未提交 1已提交 2 已批准
    pageSize:"",
    sumPage:"",
    sumCount:"",
    curPage:"1",  
    query_type:"",
    query_text:"",    
    selNode:null,
    grid_width:0,
    b_result:false,//函数执行结果
    store:{},
    defaultQueryFields:"",
    saveQuery:false
}
 var wpm=workplan.main.hr;
 
function setJsVar() {
    var box = document.getElementById("plantype");
    if (box!=null){ 
        wpm.plan_type=box.value;
    }   
    var box = document.getElementById("viewtype");
    if (box!=null){ 
        wpm.view_type=box.value;
    }   
    var box = document.getElementById("periodtype");
    if (box!=null){ 
        wpm.period_type=box.value;
    }   
    var box = document.getElementById("periodyear");
    if (box!=null){ 
        wpm.period_year=box.value;
    }   
    var box = document.getElementById("periodmonth");
    if (box!=null){ 
        wpm.period_month=box.value;
        if (wpm.period_type=="4"){//保存本次月份
            wpm.old_period_month=wpm.period_month;
        }
    }   
    var box = document.getElementById("periodweek");
    if (box!=null){ 
        wpm.period_week=box.value;
    }   
    var box = document.getElementById("submittype");
    if (box!=null){ 
        wpm.submit_type=box.value;
    }   
    var box = document.getElementById("curpage");
    if (box!=null){ 
        wpm.curPage=box.value;
    }   
    var box = document.getElementById("pagesize");
    if (box!=null){ 
        wpm.pageSize=box.value;
    }   
    var box = document.getElementById("querytype");
    if (box!=null){ 
        wpm.query_type=box.value;
    }   
    var box = document.getElementById("querytext");
    if (box!=null){ 
        wpm.query_text=box.value;
    }   
    
}  

//隐藏，显示
function display(id, targetId) {
	var source = document.getElementById(id);
	var dp = source.style.display;
	if (dp == "none") {
		if (Ext.isDefined(targetId)) {
			var target = document.getElementById(targetId);
			var left = target.offsetLeft;
			var top = target.offsetTop;
			while (target = target.offsetParent) {
				left += target.offsetLeft;
				top += target.offsetTop;
			}

			source.style.position = "absolute";
				source.style.left = left + "px";
				source.style.top = top + 25 + "px";
			
		}
		source.style.display = "block";
	} else {
		source.style.display = "none";
	}

}

function statusvalue(plan,value)
{
	document.getElementById('plantype1').value=value;
	Ext.getDom('plan_status').value = plan;
}

/*
切换期间类型
*/
function selectPeriodType(periodvalue) {
    var selobj=document.getElementById(periodvalue);    
    var periodTypeName =selobj.innerHTML; 
    changePeriodParam(true,periodvalue,wpm.period_year,wpm.old_period_month,wpm.period_week);
    if (wpm.b_result){   
        //设置期间类型描述 
        setPeriodTypeName(periodTypeName);  
        setPeriodName(wpm.period_year,wpm.period_month);         
        displayWeekLabel();
        doCommonQuery();//执行公共查询    haosl 20170602
        //loadPlanByAjax(); 
    }
    
}
/*
切换期间时,去后台获取一些默认信息 如定位到当前月。
*/
function changePeriodParam(bLocationCurWeek,periodtype,periodyear,periodmonth,periodweek) {
    var locationCurWeek="false";
    if (bLocationCurWeek) locationCurWeek="true";
    var hashvo = new ParameterSet();
    hashvo.setValue("oprType", "checkIsCanReadPlan");
    hashvo.setValue("locationCurWeek", locationCurWeek);
    hashvo.setValue("periodType", periodtype);
    hashvo.setValue("periodYear", periodyear);
    hashvo.setValue("periodMonth", periodmonth);
    hashvo.setValue("periodWeek", periodweek);
    hashvo.setValue("curjsp", wpm.curjsp);
    hashvo.setValue("deptLeader", wpm.deptleader);
    hashvo.setValue("p0723", wpm.p0723);     
    wpm.b_result=false;  
    var request=new Request({method:'post',asynchronous:false,
          onSuccess:changePeriodParam_ok,functionId:'9028000704'},hashvo);
}
function changePeriodParam_ok(outparamters) {
    hideDropdownBox();
    var strinfo = outparamters.getValue("info");
    if (strinfo=="true"){
        wpm.b_result=true;        
        var periodtype = outparamters.getValue("periodType");
        document.getElementById("periodtype").value=periodtype;    
        wpm.period_type=periodtype;   
        var periodyear = outparamters.getValue("periodYear");
        document.getElementById("periodyear").value=periodyear;    
        wpm.period_year=periodyear;   
        var periodmonth = outparamters.getValue("periodMonth");
        document.getElementById("periodmonth").value=periodmonth;    
        wpm.period_month=periodmonth;   
        var periodweek = outparamters.getValue("periodWeek");
        document.getElementById("periodweek").value=periodweek;    
        wpm.period_week=periodweek;   
        var weeknum = outparamters.getValue("weekNum"); 
        wpm.week_num=weeknum;   
        if (wpm.period_type=="4"){//保存本次月份
            wpm.old_period_month=wpm.period_month;
        }
    }
    else {
        alert("你没有权限查看当前期间的计划");
    }
}

/*
切换年度，
*/
function selectPeriodYear(periodvalue) {   
    var bLocationCurWeek=false; 
    if (wpm.period_type=="1" ){//年、半年、季度
        ;
    }   
    else if (wpm.period_type=="2" || wpm.period_type=="3"){
        bLocationCurWeek=true;
    }
    else {
        return;
    } 
    var selobj=document.getElementById(periodvalue);    
    var periodname =selobj.innerHTML; 
    
    changePeriodParam(bLocationCurWeek,wpm.period_type,periodvalue,wpm.period_month,wpm.period_week);
    if (wpm.b_result){    
        setPeriodName(wpm.period_year,wpm.period_month);    
        var curmenuvalue = document.getElementById("periodyear");   
        curmenuvalue.value = periodvalue;   
        wpm.period_year=periodvalue+"";    
        displayWeekLabel();   
        //loadPlanByAjax(); 
        doCommonQuery();//执行公共查询    haosl 20170602
    }
}
/*
选择月份，只有月、周计划才可选
*/
function selectPeriodMonth(periodmonth) {
    var bLocationCurWeek=false; 
    if(wpm.period_type=="4"  ){//月、周
       bLocationCurWeek=false ;
    }
    else if(wpm.period_type=="5" ){//月、周
       bLocationCurWeek=true ;
    }
    else {
        return;
    }
    var periodyear=Ext.getDom("myeartitle").innerHTML;    
    changePeriodParam(bLocationCurWeek,wpm.period_type,periodyear,periodmonth,wpm.period_week);
    if (wpm.b_result){  
        setPeriodName(periodyear,periodmonth);
        displayWeekLabel();
        //loadPlanByAjax();
        doCommonQuery();//执行公共查询    haosl 20170602
    }
}
/*
选择右侧季度、半年、周标签事件
*/
function selectPeriodWeek(periodvalue) {
    if(wpm.period_type=="2" || wpm.period_type=="3"){//半年、季度
        changePeriodParam(false,wpm.period_type,wpm.period_year,periodvalue,wpm.period_week);
    }
    else if(wpm.period_type=="5"){//周
        changePeriodParam(false,wpm.period_type,wpm.period_year,wpm.period_month,periodvalue);
    }
    else {
        return;
    }
    
    if (wpm.b_result){  
//        loadPlanByAjax(); 
    	doCommonQuery();//执行公共查询    haosl 20170602
        checkedWeek(periodvalue);
    }
}

/**
初始界面
*/
function initHRform() {
    wpm.curjsp="hrplan";
    initDefault();
    setJsVar();
    if("2"==Ext.getDom('plantype').value){
    	hideCommonQuery();   
    }else{
    	Ext.getDom('quickQueryText').style.marginLeft="15px";
    }
    quickQueryTextBlur(Ext.getDom("quickQueryText"));
    initHrPlan();             
}
/**
初始计划
*/
function initHrPlan() {
    var hashvo = new ParameterSet();
    hashvo.setValue("oprType", "getPlanInfo");
    hashvo.setValue("planType", wpm.plan_type);
    hashvo.setValue("periodType", wpm.period_type);
    hashvo.setValue("periodYear", wpm.period_year);
    hashvo.setValue("periodMonth", wpm.period_month);
    hashvo.setValue("periodWeek", wpm.period_week);
    hashvo.setValue("viewType", wpm.view_type);
    hashvo.setValue("queryType", wpm.query_type);
    hashvo.setValue("queryText", getEncodeStr(wpm.query_text));
    var request=new Request({method:'post',asynchronous:false,
          onSuccess:init_ok,functionId:'9028000704'},hashvo);   
}

function init_ok(outparamters) {
    var strinfo = outparamters.getValue("info");
    wpm.defaultQueryFields = outparamters.getValue("defaultQuery");//默认指标
    wpm.saveQuery = outparamters.getValue("saveQuery");//默认指标
    strinfo=getDecodeStr(strinfo);
    if (strinfo=="false"){
       var url ="/workplan/work_plan.do?br_query=link&type=1&objectid="                 
                   +"";
       location.href =url;  
       return;
    }   
    var obj =Ext.decode(strinfo);    
    var planobj=obj.planinfo;
    
    // 填报期间范围权限 chent 20170112 start
	var plan_cycle_function = outparamters.getValue("plan_cycle_function");
	plan_cycle_function = getDecodeStr(plan_cycle_function);
	wpm.cycle_function = Ext.decode(plan_cycle_function);
	for(var p in wpm.cycle_function){
		var obj = wpm.cycle_function[p];
		if(obj.p1){//半年计划隐藏“上半年”、“下半年”
			var cycle = ',' + obj.p1.cycle + ',';
			for(var i=1; i<=2; i++){
				if(cycle.indexOf(','+i+',') == -1) Ext.getDom('halfyear'+(i)).style.display = 'none';
			}
		} else if(obj.p2){//季度计划隐藏“第一季度”、“第二季度”。。
			var cycle = ',' + obj.p2.cycle + ',';
			for(var i=1; i<=4; i++){
				if(cycle.indexOf(','+i+',') == -1) Ext.getDom('quarter'+(i)).style.display = 'none';
			}
		}
	}
	// 填报期间范围权限 chent 20170112 end
	
//--公用信息
    setJspVarAndJsVar(planobj.public_info);  

//--计划信息
    displayPlanInfo(planobj.detail_info);
}


/**
刷新计划
*/
function loadPlanByAjax() { 
	planfontblue();
    wpm.curPage="1";
    var hashvo = new ParameterSet();
    hashvo.setValue("oprType", "refreshPlanInfo");
    hashvo.setValue("planType", wpm.plan_type);
    hashvo.setValue("periodType", wpm.period_type);
    hashvo.setValue("periodYear", wpm.period_year);
    hashvo.setValue("periodMonth", wpm.period_month);    
    hashvo.setValue("periodWeek", wpm.period_week);    
    hashvo.setValue("viewType", wpm.view_type);
    hashvo.setValue("queryType", wpm.query_type);
    hashvo.setValue("queryText", getEncodeStr(wpm.query_text));

    var request=new Request({method:'post',asynchronous:true,
          onSuccess:refreshPlan_ok,functionId:'9028000704'},hashvo);
}


function refreshPlan_ok(outparamters) {
    var strinfo = outparamters.getValue("info");
    strinfo=getDecodeStr(strinfo);  
    var obj =Ext.decode(strinfo);   
    var planobj=obj.planinfo;
//--计划信息
    displayPlanInfo(planobj.detail_info);
}

/**
显示计划信息
*/
function displayPlanInfo(planobj) {
    displayTeamInfoTitle(planobj.teaminfo_title) ;
    displayView(wpm.view_type);

}
/**
显示计划列表信息
*/
function getDetailList() {
    var hashvo = new ParameterSet();
    hashvo.setValue("oprType", "getDeatilList");
    hashvo.setValue("planType", wpm.plan_type);
    hashvo.setValue("viewType", wpm.view_type);
    hashvo.setValue("periodType", wpm.period_type);
    hashvo.setValue("periodYear", wpm.period_year);
    hashvo.setValue("periodMonth", wpm.period_month);
    hashvo.setValue("periodWeek", wpm.period_week);
    hashvo.setValue("submitType", wpm.submit_type);
    hashvo.setValue("curPage", wpm.curPage);
    hashvo.setValue("pageSize", wpm.pageSize);
    hashvo.setValue("queryType", wpm.query_type);
    hashvo.setValue("queryText", getEncodeStr(wpm.query_text));
    
    if (wpm.view_type=="1"){
        var request=new Request({method:'post',asynchronous:false,
          onSuccess:displayPlanDetail,functionId:'9028000704'},hashvo);  
    }
    else { //------获取任务信息       
           var request=new Request({method:'post',asynchronous:false,
          onSuccess:displayTaskList,functionId:'9028000704'},hashvo);  
    }
}
/**
显示计划列表表头信息
*/
function displayTeamInfoTitle(teaminfo) {
	//<span class='hj-hr-teaminfotitle'></span>
     var strtitle="<b>提交情况：</b>"
            +" &nbsp;应报：<a style='color:#1B4A98 ;cursor:pointer;'  onclick='displayTeamByType(\"\",this);' >"
            +teaminfo.sum_count+"人</a>&nbsp;&nbsp;&nbsp;&nbsp; "
            +"未报：<a style='color:#1B4A98 ;cursor:pointer;' onclick='displayTeamByType(\""+"0"+"\",this);' >"
            +teaminfo.unsubmit_count+"人</a> &nbsp;&nbsp;&nbsp;&nbsp;"
		    +"已报：<a style='color:#1B4A98 ;cursor:pointer;' onclick='displayTeamByType(\""+"1"+"\",this);' >"
		    +teaminfo.submit_count+"人</a>&nbsp;&nbsp;&nbsp;&nbsp; "
		    +"未批：<a style='color:#1B4A98 ;cursor:pointer;' onclick='displayTeamByType(\""+"3"+"\",this);' >"
            +(teaminfo.submit_count-teaminfo.approve_count)+"人</a>&nbsp;&nbsp;&nbsp;&nbsp; "
            +"已批：<a style='color:#1B4A98 ;cursor:pointer;' onclick='displayTeamByType(\""+"2"+"\",this);' >"
            +teaminfo.approve_count+"人</a>&nbsp;&nbsp;&nbsp;&nbsp; "
            +"已变更：<a style='color:#1B4A98 ;cursor:pointer;' onclick='displayTeamByType(\""+"4"+"\",this);' >"
            +teaminfo.change_count+"人</a>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; ";
              
    strtitle=strtitle+"";         
    var seltitle=document.getElementById("teaminfotitle"); 
    seltitle.innerHTML=strtitle;
}

//显示计划列表表头信息样式修改  lis 20160629
function checkedTeamInfo(obj) {
	var el = [];
	el = Ext.query("#teaminfotitle a");
	for (var i = 0; i < el.length; i++) {
		el[i].style.background = "";
	}

	obj.style.background = "#EEEEEE";
}

/** 
提醒写、批准计划 submit_type：0:写 1：批准
*/
function remindHrTeam(objectid,submit_type) { 
    if (submit_type=="2") {//关联
        relatePlanSingle(objectid);
        return;
    }
    else if (submit_type=="3") {//更新
        updateTargetCard(objectid);
        return;
    }
    var hashvo = new ParameterSet();
    hashvo.setValue("oprType", "remindTeam"); 
    hashvo.setValue("planType", wpm.plan_type); 
    hashvo.setValue("periodType", wpm.period_type);
    hashvo.setValue("periodYear", wpm.period_year);
    hashvo.setValue("periodMonth", wpm.period_month);   
    hashvo.setValue("periodWeek", wpm.period_week);   
    hashvo.setValue("objectId", objectid);
    hashvo.setValue("submitType", submit_type);
    hashvo.setValue("queryType", wpm.query_type);
    hashvo.setValue("queryText", getEncodeStr(wpm.query_text));

    var request=new Request({method:'post',asynchronous:true,
          onSuccess:remindTeam_ok,functionId:'9028000704'},hashvo);  
}

function remindTeam_ok(outparamters) {
    var strinfo = outparamters.getValue("info");
    strinfo=getDecodeStr(strinfo); 
    //alert("邮件已发送!");  
}  


function relatePlan_ok(outparamters) {
    var strinfo = outparamters.getValue("info");
    strinfo=getDecodeStr(strinfo); 

}   
/**
按批准、未提交等查询记录
*/
function displayTeamByType(submit_type,obj){
    wpm.submit_type=submit_type;
    wpm.curPage="1";
    getDetailList();
    checkedTeamInfo(obj)
}

/**
跳转首页、末页 page=-1标识末页 1标识 第一页
*/
function paginationAciton(page){
    wpm.curPage=page;
    getDetailList();
}

/**
上下翻页
*/
function upDownPage(page){
    if (wpm.curPage=="1") {
     if (page==-1){//首页不能向上翻页
            return;
        }
    }
  
    var iPage=Number(wpm.curPage);
    iPage=iPage+parseInt(page);
    paginationAciton(iPage);
}
//翻页
function changepagenum(num) {
	if (num == 0) {//直接到首页
		 wpm.curPage = 1;
	} else if (num == 2) {//直接到末页
		 wpm.curPage = -1;
	} else {//上一页下一页
		 wpm.curPage =  wpm.curPage + num;
	}
	getDetailList();
}

/*设置页签的颜色*/
function pageturn(curPage){
	if (curPage == 1)// 第一页时隐藏‘首页’‘上一页’
	{
		Ext.query("[name=pageup]")[0].innerHTML = '首页';
		Ext.query("[name=pageup]")[1].innerHTML = '上页'	
		if(sumPage==1){
			Ext.query("[name=pagedown]")[0].innerHTML = '下页';
			Ext.query("[name=pagedown]")[1].innerHTML = '末页';
		}
	}
	else {
		Ext.query("[name=pageup]")[0].innerHTML = "<a  href='javascript:changepagenum(0)'>首页</a>";
		Ext.query("[name=pageup]")[1].innerHTML = "<a  href='javascript:changepagenum(-1)''>上页</a>";
	}
	if (curPage == sumPage||sumPage==0)// 末页以及页面无记录时时隐藏‘末页’‘下一页’
	{
		Ext.query("[name=pagedown]")[0].innerHTML = '下页';
		Ext.query("[name=pagedown]")[1].innerHTML = '末页';
	}
	else {
		Ext.query("[name=pagedown]")[0].innerHTML = "<a  href='javascript:changepagenum(1)'>下页</a>";
		Ext.query("[name=pagedown]")[1].innerHTML = "<a  href='javascript:changepagenum(2)'>末页</a>";
	}
}

/**
跳转任意页
*/
function goPage(){
   var page=Ext.getDom("input_curpage").value;    
   var iPage=Ext.num(page,-1);
    if (iPage<0 ) {
	    alert("页数请输入数字！");
	    return;
	} 
    paginationAciton(iPage);
}


function loadPeoplePlan(objectid,dept_leader,p0723,fromflag){
    var returnurl ="/workplan/work_track.do?b_HRquery=link&type="
            +wpm.plan_type+"&viewtype="+wpm.view_type
             +"&periodtype="+wpm.period_type+"&periodyear="+wpm.period_year
             +"&periodmonth="+wpm.period_month+"&periodweek="+wpm.period_week
           +"&curpage="+wpm.curPage 
           +"&submittype="+wpm.submit_type 
           +"&querytype="+wpm.query_type 
           +"&querytext="+wpm.query_text 
            ;
    returnurl= getEncodeStr(returnurl); 

    var url ="/workplan/work_plan.do?br_query=link&type=1&objectid="+objectid
             +"&p0723="+p0723
             +"&periodtype="+wpm.period_type
             +"&periodyear="+wpm.period_year+"&periodmonth="+wpm.period_month
             +"&periodweek="+wpm.period_week
             +"&deptleader="+dept_leader
             +"&fromflag="+fromflag
             +"&returnurl="+returnurl
             ; 
      location.href =url; 
}

/**
显示页信息
*/
function displayTeamInfoPage(teaminfo) {
    //alert(teaminfo.cur_page);
    wpm.curPage=teaminfo.cur_page;
    sumPage=teaminfo.sum_page;
    sumCount=teaminfo.sum_count;
    Ext.getDom("span_curpage").innerHTML=teaminfo.cur_page;
    Ext.getDom("span_sumcount").innerHTML=teaminfo.sum_count;
    Ext.getDom("span_sumpage").innerHTML=teaminfo.sum_page;
    
}
/**
显示计划列表明细
*/
function displayPlanDetail(outparamters) {
    var strinfo = outparamters.getValue("info");
    strinfo=getDecodeStr(strinfo);
    desplayPlanDetailNext(strinfo, false);
}

function desplayPlanDetailNext(strinfo, bool){
	var obj =Ext.decode(strinfo);    
    var planobj=obj.planinfo;
    var teaminfo=planobj.teaminfo_detail;
    if(bool){
    	displayTeamInfoTitle(planobj.detail_info.teaminfo_title);
    }
    var plan_grid = document.getElementById("plan_grid");
    var task_grid = document.getElementById("taskgrid");
    plan_grid.style.display="block";
    task_grid.style.display="none";

    var worklist= teaminfo.detail_list;
    displayTeamInfoPage(teaminfo);
    var strhtml=""; 
    for (var i=0;i<worklist.length;i++){
      var fontgrey="fontgrey(this);";
      if (worklist[i].submittype=="2"){
        fontgrey="";
      }
      
      if(wpm.plan_type=="1"){
          strhtml =strhtml
                +"<tr>"                
                +"<td width=\"15%\" class=\"hj-wzm-tdzb-td\">";
           if (worklist[i].visbleBox=="true" ){      
                strhtml =strhtml+"<input type='checkbox' name='checkbox1' id="
                    +worklist[i].objectid+" > ";
           }
           else {
                strhtml =strhtml+"&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; ";
           }
                    
          strhtml =strhtml+"<a  style='color:#549fe3;' href=\"javascript:void(0)\" onclick='loadPeoplePlan(\""+worklist[i].objectid
                    +"\",\"\",\""+worklist[i].p0723+"\",\"hr\")'>"
                    +"<img class=\"img-circle\" src='"+ worklist[i].imagepath+"'/> &nbsp;"+worklist[i].name
                +"</a>" ;   
          		if(strHaveCreatePlanPri_p)
          		{
	                if(worklist[i].name.length==2)
	                	strhtml+="&nbsp;&nbsp;&nbsp;";
	                strhtml+="&nbsp;&nbsp; &nbsp;&nbsp;<img style='cursor:pointer' title='制定计划'    onclick='loadPeoplePlan(\""+worklist[i].objectid +"\",\"\",\""+worklist[i].p0723+"\",\"hr_create\")'  src='/images/new_module/dealto.gif'/> ";
          		}
                strhtml+="</td>"               
                +"<td width=\"20%\">"
                +""+worklist[i].dept_desc+""               
                +"</td>"
                +"<td width=\"10%\">"
                +worklist[i].appstatus               
                +"</td>"
                +"<td width=\"10%\">"
                +worklist[i].total_rank               
                +"</td>"
                +"<td width=\"10%\">"
                +"<a style='color:#549fe3;cursor:pointer;' "
                    +" onclick='"+fontgrey+"remindHrTeam(\""+worklist[i].objectid+"\",\""+worklist[i].submittype+"\")'>"
                    +worklist[i].remind+"</a>"               
                +"</td>"                
                +"</tr>"   
        }
        else {
          strhtml =strhtml
                +"<tr>"
                +"<td width=\"15%\" class=\"hj-wzm-tdzb-td\">";
           if (worklist[i].visbleBox=="true" ){      
                strhtml =strhtml+"<input type='checkbox' name='checkbox1' id="
                    +worklist[i].e0122+" > ";
           }
           else {
                strhtml =strhtml+"&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; ";
           }
                    
          strhtml =strhtml+"<a style='color:#549fe3;'  href=\"javascript:void(0)\" onclick='loadPeoplePlan(\""+worklist[i].e0122+"\",\""+worklist[i].objectid
                    +"\",\""+worklist[i].p0723+"\",\"hr\")'>"
                    +"<img class=\"img-circle\" src='"+ worklist[i].imagepath+"'/>&nbsp;"+worklist[i].name
                +"</a>" ;  
      			if(strHaveCreatePlanPri_u)
      			{
      				if(worklist[i].name.length==0)
      					strhtml+="&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;";
      				else if(worklist[i].name.length==2)
      					strhtml+="&nbsp;&nbsp;&nbsp;";
      				strhtml+="&nbsp;&nbsp; &nbsp;&nbsp;<img style='cursor:pointer'  title='制定计划'  onclick='loadPeoplePlan(\""+worklist[i].e0122+"\",\""+worklist[i].objectid +"\",\""+worklist[i].p0723+"\",\"hr_create\")'  src='/images/new_module/dealto.gif'/> ";
      			}
      			strhtml +="</td>"
                +"<td width=\"12%\">"
                +""+worklist[i].e0122desc+"&nbsp;工作计划"               
                +"</td>"
                +"<td width=\"10%\">"
                +""+worklist[i].appstatus+""               
                +"</td>"
                +"<td width=\"10%\">"
                +""+worklist[i].total_rank               
                +"</td>"
                +"<td width=\"13%\">"
                +"<a style='color:#549fe3;cursor:pointer;' "
                    +" onclick='"+fontgrey+"remindHrTeam(\""+worklist[i].e0122
                    +"\",\""+worklist[i].submittype+"\")'>"+worklist[i].remind+"</a>"               
                +"</td>"                
                +"</tr>"   
        
        }
    }
    strhtml="<table width=\"100%\" border=\"0\">"+strhtml+"</table>";

    var selobj=document.getElementById("plan_grid");  
    selobj.innerHTML=strhtml;
    pageturn(wpm.curPage);
}

/**
显示任务列表明细
*/
  function displayTaskList(outparamters)
 {
    var plan_grid = document.getElementById("plan_grid");
    var task_grid = document.getElementById("taskgrid");
    plan_grid.style.display="none";
    task_grid.style.display="block";
    var strinfo = outparamters.getValue("info");
    strinfo=getDecodeStr(strinfo);
    displayTaskListNext(strinfo);
 }
  
  function displayTaskListNext(strinfo){
	  var info=Ext.decode(strinfo);
	    var _tableData=info.dataJson; 
	    var _fields=info.dataModel; 
	    _columns=info.panelColumns; 
	    displayTeamInfoPage(info.pageInfo);
	    
	    if (wpm.grid_width<=0)
	        wpm.grid_width=getTreeGridWidth(true);

	    Ext.define('Task', {
	        extend: 'Ext.data.TreeModel',
	        fields: _fields
	    });

	    var store = Ext.create('Ext.data.TreeStore', {
	        model: 'Task',
	        root:_tableData
	    });

	    Ext.destroy(g_tree); 
	    g_tree = Ext.create('Ext.tree.Panel', {
	        id:'gridView',       
	        width: wpm.grid_width,
	        height: 400,
	        renderTo:'taskgrid',
	        collapsible: true,
	        useArrows: true,
	        rootVisible: false,
	        store: store,
	        header:false,
	        multiSelect: false, 
	        forceFit:true, 
	        stripeRows:true,
	        columns:_columns 
	    });
	    wpm.store=store;
	    // store.getRootNode().expand(true,true);
	    pageturn(wpm.curPage);
  }
 /**
切换视图
*/
 function displayView(viewtype) {        
    wpm.view_type=viewtype;
    var a_planView = Ext.getDom("a_planView");
    var a_taskView = Ext.getDom("a_taskView");
    if ( wpm.view_type=="1"){
        a_planView.className="hj-wzm-or-a";
        a_taskView.className="";
    }
    else {
        a_planView.className="";
        a_taskView.className="hj-wzm-or-a";
    }
    getDetailList();
}

 /* 任务名称添加链接 */
 function addLink(value,cell,record,rowIndex,columnIndex,store){          
    setCellTaskNameCss(cell);
    if(record.get("p0700")==""){        
        value="<span style='color:#549fe3;font-size:14px;cursor:pointer; ' title='"+value+"'  onclick='loadPeoplePlan(\""
            +record.get("objectid")+"\",\""+"\",\""+record.get("p0723")   
            +"\",\"hr\")' > "
	        +" <img   width='24' height='24' src='"+record.get("img_path")+"' />"
	        +"&nbsp;"+value;    
        value=  value+" </span>";  
        
    }
    else {
    
	    value="<span style='color:#549fe3;cursor:pointer' title='"+value+"' onclick='openTask(event,\""
	        +record.get("p0700")+"\",\""+record.get("p0800")+"\",\""+record.get("objectid")+"\",\""+record.get("p0723")   
	        +"\")' > "+value;    
	    if(record.get("p0833")==1){//新增
	        value=  value+" &nbsp;<img   width='10' height='10' src='/workplan/image/is_add.png' />";
	    }
	    else if(record.get("p0833")==2){//取消
	        value=  value+" &nbsp;<img   width='10' height='10' src='/workplan/image/is_cancel.png' />";
	    }
	    else if(record.get("p0833")==3){//变更
	        value=  value+" &nbsp;<img   width='10' height='10' src='/workplan/image/is_alter.png' />";
	    }    
	   
	    value=  value+" </span>";  
    
    }
    
    return value;
 }
 /**
打开任务界面
*/
   function openTask(event,p0700,p0800,objectid,p0723){
    //从任务编辑界面 返回计划界面的url
    var returnurl ="/workplan/work_track.do?b_HRquery=link&type="
            +wpm.plan_type+"&viewtype="+wpm.view_type
             +"&periodtype="+wpm.period_type+"&periodyear="+wpm.period_year
             +"&periodmonth="+wpm.period_month+"&periodweek="+wpm.period_week
           +"&curpage="+wpm.curPage 
           +"&submittype="+wpm.submit_type 
           +"&querytype="+wpm.query_type 
           +"&querytext="+wpm.query_text 
            ;
    returnurl= getEncodeStr(returnurl); 

    var url="/workplan/plan_task.do?br_task=link&p0700="+p0700+"&p0800="+p0800+"&objectid="+objectid+"&p0723="+p0723
            +"&returnurl="+returnurl;   
     
    // 任务改由弹出小窗口代替打开新页面
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
/**
回车键 一键查询
*/
 function quickQueryTextOnEnter()         
 { 
     if(event.keyCode == 13)
     {
        quickQuery();  
        return false;
     } 
 }
/**
一键查询
*/
 function quickQuery()         
 { 
    var obj=Ext.getDom("quickQueryText");
    var querytext=obj.value;
    wpm.query_type="1";
    if ((querytext.length>=3) && (querytext.substr(0,3)=="请输入")){
        querytext="";
        wpm.query_type="";
    } 
    wpm.query_text=querytext;
    loadPlanByAjax();
 }
 
/**
一键查询
*/
 function commonQuery()         
 { 
    wpm.query_type="2";
    wpm.query_text=getcommonQueryText();
    loadPlanByAjax();
 }
/**
一键查询获取焦点
*/ 
function quickQueryTextFocus(obj){ 
    if (obj.value==Const_QuickQueryHint){
        obj.value=""; 
        obj.style.color="#000";
    
    } 
}
/**
一键查询失去焦点
*/
function quickQueryTextBlur(obj){ 
   if (obj.value==""){
       obj.value=Const_QuickQueryHint;  
       obj.style.color="#ccc";       
   }
}

/**
显示普通查询界面
*/
function displayCommonQuery(){ 
	Ext.getDom("down_img").style.display="none";
	Ext.getDom("up_img").style.display="block";
	if("2"==Ext.getDom('plantype').value){
		Ext.getDom("commonQueryDiv").style.display="block";
	}
	Ext.getDom("dividerLineDiv").style.display="none";

}
/**
隐藏普通查询界面
*/
function hideCommonQuery(){ 
    Ext.getDom("down_img").style.display="block";
    Ext.getDom("up_img").style.display="none";
    if("2"==Ext.getDom('plantype').value){
    	Ext.getDom("commonQueryDiv").style.display="none";
    }
    Ext.getDom("dividerLineDiv").style.display="block";
}
/**
重置查询界面
*/
function resetQuery()
{
    var sels = Ext.query(".hj-zm-bumen");
    statusvalue('','全部计划');
    for(i=0;i<sels.length;i++)
    {
        sels[i].value="";
        if (sels[i].name=="query_dept.viewvalue"){
         Ext.getDom("query_dept.value").value="";
        }
    }
    wpm.query_type="";
    loadPlanByAjax();
    
}
/**
获取普通查询条件
*/
function getcommonQueryText()
{
    var str="";
    var sels = Ext.query(".hj-zm-bumen");
    for(i=0;i<sels.length;i++)
    {
        if (sels[i].value==""){
          continue;
        }
        if (sels[i].name=="query_dept.viewvalue"){
          str=str+"`"+"e0122="+Ext.getDom("query_dept.value").value;
        }
        else {
            str=str+"`"+sels[i].name+"="+sels[i].value;
        }
    }
    return str;
}

 /* 动态获取grid宽度 */
 function getTreeGridWidth(bInit)
 {
    var gridObj=document.getElementById("taskgrid");
    var grid_width=0;
    if (bInit){
        grid_width=gridObj.offsetWidth; //IE11有问题第一次gridObj.offsetWidth为0
        grid_width=gridObj.offsetWidth; 
    }
    else {
        grid_width=gridObj.offsetWidth
    }
    return grid_width;
 }
  /* 获取选中的人员*/
 function getRowItemids(){
    var itemids="";
    var itemid_arr= document.getElementsByName("checkbox1");
    if(itemid_arr==null){
        return "";
    }else{
        for(var i=0;i<itemid_arr.length;i++){
            if(itemid_arr[i].checked){
                var itemid =itemid_arr[i].id;
                itemids =itemids+","+itemid
             }
        }
   }
    return itemids;
}
 /* 全部关联计划*/
function relatePlanAll() {
    if (wpm.period_type==5){
      alert("周计划不能关联！");
      return;
    }
    var b=false;
    var itemids=getRowItemids();
    var objecttype="人员";
    if (wpm.plan_type=="2"){
        objecttype="部门";
    }
    if (itemids==""){
        //判断是否有已批准的计划，如果都未批准则无需关联
	    var hashvo = new HashMap();
	    hashvo.put("oprType","checkIsApproved");
	    hashvo.put("planType", wpm.plan_type);
	    hashvo.put("periodType", wpm.period_type);
	    hashvo.put("periodYear", wpm.period_year);
	    hashvo.put("periodMonth", wpm.period_month);
	    hashvo.put("periodWeek", wpm.period_week);
	    hashvo.put("queryType", wpm.query_type);
	    hashvo.put("queryText", getEncodeStr(wpm.query_text));
	    Rpc( {functionId : '9028000704',async: false,
	        success: function(response) {
	                var map = Ext.JSON.decode(response.responseText);
	                var info = map.info;
	                if (info=="false"){
	                    b=false;
	                }
	                else {
	                   b=true;
	                }
	            }
	        }, hashvo);
	
	    if (!b){
	       alert('当前查询列表没有已批准的计划，无需关联！');
	       return;
	    }
	    
	    b=false;  
        if (confirm('是否要为所有'+objecttype+'关联考核计划？')){
          b=true;  
        }
    }
    else {
        if (confirm('是否要为当前选择'+objecttype+'关联考核计划？')){
          b=true;  
        }
    }
    if (!b){
        return ;
    }
   selectPlan(itemids);
}    
 
 /**
为选中的人关联考核计划
*/
function relatePlanSingle(objectid) {
    if (wpm.period_type==5){
      alert("周计划不能关联！");
      return;
    }
    selectPlan(objectid);
}


/**
打开关联考核计划界面
*/
function selectPlan(objectids) {
    var thecodeurl ="/workplan/relate_plan.do?b_query=link&oprtype=''"
        +"&plantype="+wpm.plan_type
        +"&periodtype="+wpm.period_type+"&periodyear="+wpm.period_year
             +"&periodmonth="+wpm.period_month+"&periodweek="+wpm.period_week; 
    //location.href=thecodeurl;
   //return;         
   // /*  
   
    var retvo= window.showModalDialog(thecodeurl, "", 
              "dialogWidth:800px; dialogHeight:380px;resizable:no;center:yes;scroll:yes;status:no;location:no");  
    if (retvo==null) return;                            
    
    if(retvo.success=="1")  {
      //alert(retvo.plan_id);
      //开始关联
      afterSelectPlan(retvo.plan_id,objectids);
    }
   
}
/**
选中关联计划后 开始关联
*/
function afterSelectPlan(plan_id,objectids) { 
    /*     
    Ext.Msg.show({
         title:'关联考核计划',
         msg:'正在关联，请等待...',
         width:300,
         progress:true,
         progressText:'progress...',
         closable:false,
         animEl:'btnProgress'
     });
     
        var myFun=function(i){
         return function(){
             if(i==11){
                 Ext.MessageBox.hide();//隐藏窗口
             }else{
                 Ext.MessageBox.updateProgress(i/10,100*(i/10)+"%");
             }
         }
     }
     for(var i=1;i<=11;++i){
         //setTimeout(myFun(i),;
     }
     
     */
     
    checkPlan(plan_id,objectids);
}
/**
检查当前选中的计划是否可以关联
*/
function checkPlan(plan_id,objectids) { 
    var hashvo = new HashMap();
    //检查主体类别
    var b=true;
    hashvo.put("oprType","checkSuperBodyType");
    hashvo.put("planType", wpm.plan_type); 
    hashvo.put("periodType", wpm.period_type);
    hashvo.put("periodYear", wpm.period_year);
    hashvo.put("periodMonth", wpm.period_month);   
    hashvo.put("periodWeek", wpm.period_week);   
    hashvo.put("objectIds", objectids);
    hashvo.put("queryType", wpm.query_type);
    hashvo.put("queryText", getEncodeStr(wpm.query_text));
    hashvo.put("planId", plan_id);
    Rpc( {functionId : '9028000704',async: false,
        success: function(response) {
                var map = Ext.JSON.decode(response.responseText);
		        var info = map.info;
		        if (info=="false"){
		            if (!confirm('此计划没有设置'+ map.bodySet+'主体类别，是否关联？')){
		              b=false;  
		            }
		        }
            } 
        }, hashvo);

	if (!b){
	   return;
	}
	
	//检查是所选人员是否已关联过
    var reRelalte="false";    //已关联的人是否重新关联
    hashvo.put("oprType","checkIsRelated");
    hashvo.put("planType", wpm.plan_type); 
    hashvo.put("periodType", wpm.period_type);
    hashvo.put("periodYear", wpm.period_year);
    hashvo.put("periodMonth", wpm.period_month);   
    hashvo.put("periodWeek", wpm.period_week);   
    hashvo.put("objectIds", objectids);
    hashvo.put("queryType", wpm.query_type);
    hashvo.put("queryText", getEncodeStr(wpm.query_text));
    hashvo.put("planId", plan_id);
    Rpc( {functionId : '9028000704',async: false,
         success: function(response) {
			        var map = Ext.JSON.decode(response.responseText);
			        var info = map.info;
			        var name = map.objectName;
			        var count = map.objectCount;
			        if (info=="false"){
			            if (count==""){
			                if (confirm(name+"已经关联考核计划，是否继续关联新考核计划中？")){
			                  reRelalte="true" 
			                }
			            }
			            else {
			                if (confirm(name+"等"+count+"人已经关联考核计划，是否继续关联新考核计划中？")){
			                  reRelalte="true" 
			                }
			            }
			        }
                }  
            }, hashvo);
 
 
    //任务模板需要修改 但不能修改
    hashvo.put("oprType","checkTemplate");
    hashvo.put("planType", wpm.plan_type); 
    hashvo.put("periodType", wpm.period_type);
    hashvo.put("periodYear", wpm.period_year);
    hashvo.put("periodMonth", wpm.period_month);   
    hashvo.put("periodWeek", wpm.period_week);   
    hashvo.put("objectIds", objectids);
    hashvo.put("queryType", wpm.query_type);
    hashvo.put("queryText", getEncodeStr(wpm.query_text));
    hashvo.put("planId", plan_id);
    hashvo.put("reRelalte", reRelalte);
    Rpc( {functionId : '9028000704',async: false,
             success: function(response) {
			        var map = Ext.JSON.decode(response.responseText);
			        var info = map.info;
			        var task_desc = map.taskDesc;
			        if (info=="false"){
			            alert('任务分类['+task_desc+']在考核模板中不存在，且此模板已经被其他考核计划使用，不能关联！');
			            b=false;
			        }
                }
             }, hashvo);

    if (!b){
       return;
    }
    
    //开始关联
    hashvo.put("oprType","relatePlan");
    hashvo.put("planType", wpm.plan_type); 
    hashvo.put("periodType", wpm.period_type);
    hashvo.put("periodYear", wpm.period_year);
    hashvo.put("periodMonth", wpm.period_month);   
    hashvo.put("periodWeek", wpm.period_week);   
    hashvo.put("objectIds", objectids);
    hashvo.put("queryType", wpm.query_type);
    hashvo.put("queryText", getEncodeStr(wpm.query_text));
    hashvo.put("planId", plan_id);
    hashvo.put("reRelalte", reRelalte);
    Rpc( {functionId : '9028000704',async: false,
             success: function(response) {
                var map = Ext.JSON.decode(response.responseText);
                var info = map.info;
                if (info=="true"){	//刷新
                   alert('已关联到考核计划！');             
	               getDetailList();
	               
	               // 关联计划后验证权重之和 lium
	               checkTotalRankOrScore(plan_id, objectids);
                }
            }  }, hashvo);
}

// 关联计划后检查分值或权重之和 lium
function checkTotalRankOrScore(planId, objectIds) {
	var hashvo = new ParameterSet();
	hashvo.setValue("planId", planId || "");
	hashvo.setValue("objectIds", objectIds.split(","));
	new Request({
		asynchronous: true,
		onSuccess: _callback,
		functionId: '9028000740'
	}, hashvo);
}
function _callback(data) {
	var up = data.getValue("up") || [];
	var down = data.getValue("down") || [];
	var status = data.getValue("status") || ""; // 查询计划模板的类型(per_template.status): '0' = 分值, '1' = 权重
	var object_type = data.getValue("object_type") || ""; // per_plan.object_type(1=团队, 2=人员)
	var tplType = status == "0" ? "标准分值" : "权重";
	var objType = object_type == "2" ? "人" : "部门";//1:团队(对单位|部门)2:人员3:单位（新加一种类型,仅对单位）4:部门
	
	// 没有超出额度的人
	if ((up.length + down.length) == 0) {return;}
	var content = "<div id='CheckTotalRankOrScore' style='height:100%;background-color:#FFF;padding:5px;word-break:break-all;word-wrap:break-word;overflow:auto;'>";
	if (down.length > 0) {
		content += tplType + "不足100%的" + objType + "有：<br/>&nbsp;&nbsp;" + down.join("、") + "<br/>";
	}
	if (up.length > 0) {
		content += tplType + "超过100%的" + objType + "有：<br/>&nbsp;&nbsp;" + up.join("、") + "<br/>";
	}
	content += "</div>";
	
	var win = Ext.create('Ext.window.Window', {
	    title: '目标卡校验',
	    height: 420,
	    width: 400,
	    layout: 'fit',
	    html: content,
	    buttons: [ {
			text: "保存",
			handler: function () { // 导出总分或权重超限的对象(人员或部门)名单
				var content = document.getElementById("CheckTotalRankOrScore").innerHTML;
				var s = content.replace(/<br *\/?>/gi, "\r\n");
				s = s.replace(/&nbsp;/gi, " ");
				
				var hashvo = new ParameterSet();
				hashvo.setValue("content", s);
				hashvo.setValue("status", status);
				hashvo.setValue("object_type", object_type);
				new Request({
					asynchronous : true,
					onSuccess : function(data) {
						var fileName = data.getValue("fileName") || "";
						if (fileName) {
							location.href = "/servlet/vfsservlet?fromjavafolder=true&fileid=" + fileName;
						}
						win.hide();
					},
					functionId : '9028000741'
				}, hashvo);
			}
		}, {
			text: "关闭",
			handler: function() {win.hide();}
		} ],
		closeAction: 'close'
	});
	win.show();
}

/**
 * 更新目标卡
 */
function updateTargetCard(objectid) { 
    var hashvo = new HashMap();
    hashvo.put("oprType","updateTargetCard");
    hashvo.put("planType", wpm.plan_type); 
    hashvo.put("periodType", wpm.period_type);
    hashvo.put("periodYear", wpm.period_year);
    hashvo.put("periodMonth", wpm.period_month);   
    hashvo.put("periodWeek", wpm.period_week);   
    hashvo.put("objectId", objectid);
    Rpc( {functionId : '9028000704',async: false,
             success: function(response) {
                var map = Ext.JSON.decode(response.responseText);
                if (map.info=="true"){
                   // alert('已更新到目标卡！');
                    
                	// 关联计划后验证权重之和 lium
                    var planId = map.planId || "";
                    checkTotalRankOrScore(planId, objectid);
                }
                else if (map.info=="editTemplate"){
                    var task_desc = map.taskDesc;
                    alert('任务分类['+task_desc+']在考核模板中不存在，且此模板已经被其他考核计划使用，不能关联！');
                }
                else {
                    if (map.desc!=null){
                        alert(map.desc);
                    }
                
                }
       
            }  }, hashvo);
}

/**
 * 更新目标卡
 */
function batchUpdateTargetCard() { 	
    var itemids=getRowItemids();
    var objecttype="人员";
    if (wpm.plan_type=="2"){
        objecttype="部门";
    }
    var b=false;
    if (itemids==""){
        if (confirm('是否要为所有已关联计划的'+objecttype+'更新目标卡？')){
          b=true;  
        }
    }
    else {
        if (confirm('是否要为当前选择'+objecttype+'更新目标卡？')){
          b=true;  
        }
    }
    if (!b){
        return ;
    }
	
    //开始
    var hashvo = new HashMap();
    hashvo.put("oprType","batchUpdateTargetCard");
    hashvo.put("planType", wpm.plan_type); 
    hashvo.put("periodType", wpm.period_type);
    hashvo.put("periodYear", wpm.period_year);
    hashvo.put("periodMonth", wpm.period_month);   
    hashvo.put("periodWeek", wpm.period_week);   
    hashvo.put("objectIds", itemids);
    hashvo.put("queryType", wpm.query_type);
    hashvo.put("queryText", getEncodeStr(wpm.query_text));
    Rpc( {functionId : '9028000704',async: false,
             success: function(response) {
                var map = Ext.JSON.decode(response.responseText);
                alert('已更新到目标卡！');  
                if (map.planIds!=","){                	
                    checkTotalRankOrScore(map.planIds, itemids);
                }               
            }  }, hashvo);
}

/** 链接灰色 */
function fontgrey(obj)
{
	obj.style.color="#838383";
}

/**
 * 链接蓝色
 * */
function planfontblue()
{
	var cccc = Ext.query(".hj-hr-teaminfobtn-left a");
	for ( var irow = 0; irow < cccc.length; irow++) {
		cccc[irow].style.color="#549FE3";
	}
}
/**
 * haosl 20170315 update 
 * 执行公共查询
 * @returns
 */
function doCommonQuery(){
	var wp_commonQuery = Ext.getCmp("commonQueryId");
	if(wp_commonQuery){
		wp_commonQuery.executeDoQuery();
	}
}
 

