var isTargetTemplate = "false";
function replaceAll(str, sptr, sptr1)
{
	while (str.indexOf(sptr) >= 0)
	{
   		str = str.replace(sptr, sptr1);
	}
	return str;
}

function getCycle()
{
	var cycle = document.getElementById('cycle').value;
	var theyear= document.getElementById('theyear').value;
	var thequarter = document.getElementById('thequarter').value;
	var themonth = document.getElementById('themonth').value;
	var start_date= document.getElementById('start_date').value;
	var end_date = document.getElementById('end_date').value;
	
	if(cycle==KHPLAN_YEAR)
		cycle='0';
	else if(cycle==KHPLAN_HALFYEAR)
		cycle='1';
	else if(cycle==KHPLAN_QUARTER)
		cycle='2';
	else if(cycle==KHPLAN_MONTH)
		cycle='3';	
	else if(cycle==KHPLAN_INDEFINETIME)
		cycle='7';		
	
	var target_url="/performance/kh_plan/examPlanAdd.do?br_cycle=link`cycle="+cycle+"`theyear="+theyear+"`thequarter="+thequarter+"`themonth="+themonth+"`start_date="+start_date+"`end_date="+end_date+"`status="+$F('status');
 	var iframe_url="/general/query/common/iframe_query.jsp?src="+$URL.encode(target_url);

    if(window.showModalDialog){
        var return_vo= window.showModalDialog(iframe_url, "cycle_win",
            "dialogWidth:320px; dialogHeight:300px;resizable:no;center:yes;scroll:no;status:no");
        getCycle_Ok(return_vo);
    }else{
        var config = {
            width:350,
            height:250,
            type:'1',
            title:'考核计划周期设置',
            id:'cycle_win'
        };
        modalDialog.showModalDialogs(iframe_url,'cycle_win',config);

    }

}

function getCycle_Ok(return_vo) {
	if(Ext.getCmp('cycle_win')){
        Ext.getCmp('cycle_win').close();
	}

    if(!return_vo || $F('status')!='0')
        return false;
    if(return_vo.flag=="true")
    {
        document.getElementById('cycle').value=return_vo.cycle;
        document.getElementById('theyear').value=return_vo.theyear;
        document.getElementById('themonth').value=return_vo.themonth;
        document.getElementById('start_date').value=return_vo.start_date;
        document.getElementById('end_date').value=return_vo.end_date;
        if(return_vo.cycle=='1')
            document.getElementById('thequarter').value=return_vo.thehalfyear;
        else if(return_vo.cycle=='2')
            document.getElementById('thequarter').value=return_vo.thequarter;

        setkhtimeqj();
        setCycle();
    }
}

function getTemplate()
{
	var busitype = document.getElementById('planBusitype');
	var subsys_id = '33';
	if(busitype!=null && busitype.value=='1')
  		subsys_id = '35';

	var plan_id = $F('plan_id');
	var status = $F('status');
	var templId=$F('template_id');
	if(status=='0')
	{
		var hashvo=new ParameterSet();
		hashvo.setValue("thePlan",plan_id);
		hashvo.setValue("templId",templId);
		var request=new Request({method:'post',asynchronous:false,onSuccess:getTemplate2,functionId:'9022000026'},hashvo);
	}else
	{
		var method = '1';
		if(busitype!=null && busitype.value=='0')
		{
		    if(examPlanForm.method[1].checked==true)
		    	method = '2';
	    }
	    method=3//程序暂时改为考核模板显示所有，不受考核方法的制约，但是考核方法随着考核模板变动
		 //method=1 显示非个性化项目的模板 method=2 显示个性化项目的模板 method=3 显示全部
		var theurl="/performance/kh_system/kh_template/init_kh_item.do?b_query=link`templateid="+templId+"`subsys_id="+subsys_id+"`isVisible=2`method="+method+"`isEdit=0";
      	var iframe_url="/general/query/common/iframe_query.jsp?src="+$URL.encode(theurl);

        if(window.showModalDialog){
            var return_vo= window.showModalDialog(iframe_url, 'template_win',
                "dialogWidth:800px; dialogHeight:610px;resizable:yes;center:yes;scroll:no;status:no;minimize:yes;maximize:yes;");
        }else{
            var config = {
                width:800,
                height:610,
                type:'2',
                title:'关联模板',
                id:'template_win'
            };
            modalDialog.showModalDialogs(iframe_url,'template_win',config);
        }
	}
}
function getTemplate2(outparamters)
{
	var canedit=outparamters.getValue("canedit");	
	var planID=outparamters.getValue("thePlan");
	var templId=outparamters.getValue("templId");
	var astatus=outparamters.getValue("status");
	if(canedit=='1')//可以编辑模板
		getTemplate1();
	else if(canedit=='0')//不可以编辑模板
	{
		var busitype = document.getElementById('planBusitype');
		var subsys_id = '33'; 	
		if(busitype!=null && busitype.value=='1')
	  		subsys_id = '35';
		
		if(astatus=='0')//如果是另存得来的考核计划，且另存时候选择了复制指标权限表，在起草状态也不能修改模板
			alert(NOTEDIT_KHTEMPLATE);
	    templId=$F('template_id');
	    var method = '1';
	    if(busitype!=null && busitype.value=='0')
		{
		    if(examPlanForm.method[1].checked==true)
		    	method = '2';	
	    }
	    method=3//程序暂时改为考核模板显示所有，不受考核方法的制约，但是考核方法随着考核模板变动
		 //method=1 显示非个性化项目的模板 method=2 显示个性化项目的模板 method=3 显示全部
		var theurl="/performance/kh_system/kh_template/init_kh_item.do?b_query=link`templateid="+templId+"`subsys_id="+subsys_id+"`isVisible=2`method="+method+"`isEdit=0";
      	var iframe_url="/general/query/common/iframe_query.jsp?src="+$URL.encode(theurl);

        if(window.showModalDialog){
            window.showModalDialog(iframe_url, 'template_win',
                "dialogWidth:800px; dialogHeight:600px;resizable:no;center:yes;scroll:no;status:no");
        }else{
            var config = {
                width:800,
                height:600,
                type:'2',
                title:'关联模板',
                id:'template_win'
            };
            modalDialog.showModalDialogs(iframe_url,'template_win',config);
        }
	}
}
function getTemplate1()
{
	var busitype = document.getElementById('planBusitype');
	var subsys_id = '33'; 	
	if(busitype!=null && busitype.value=='1')
  		subsys_id = '35';
	
	var templId=$F('template_id');
	var method = '1';
	if(busitype!=null && busitype.value=='0')
	{
		if(examPlanForm.method[1] && examPlanForm.method[1].checked==true)
			method = '2';	
	}
	 method=3//程序暂时改为考核模板显示所有，不受考核方法的制约，但是考核方法随着考核模板变动
	 //method=1 显示非个性化项目的模板 method=2 显示个性化项目的模板 method=3 显示全部
	//viewtype=1 考核计划 新建选择模板
	  var theurl="/performance/kh_system/kh_template/kh_template_tree.do?b_query=link`subsys_id="+subsys_id+"`isVisible=2`method="+method+"`isEdit=1`templateid="+templId+"`viewtype=2";
      var iframe_url="/general/query/common/iframe_query.jsp?src="+$URL.encode(theurl);
    var height = 600;
    if (!window.showModalDialog){
        height = 700;
    }

    var config = {
        width:800,
        height:height,
        type:'2',
        title:'关联模板',
        id:'template_win'
    };
    modalDialog.showModalDialogs(iframe_url,'template_win',config,getTemplate_ok);
}

function getTemplate_ok(return_vo) {
    if(return_vo==null)
        return;
    var template=return_vo.split(',');
    if(template!=null && $F('status')=='0')
    {
        document.getElementById('template_id').value=template[1];
        document.getElementById('templateName').value=template[0];

        var hashvo=new ParameterSet();
        hashvo.setValue("template_id",template[1]);
        var plan_id = $F('plan_id');
        hashvo.setValue("plan_id",plan_id);
        var request=new Request({method:'post',asynchronous:false,onSuccess:beforeUpdateMethod,functionId:'9022000023'},hashvo);
    }
}

// updateMethod页面刷新时会调用，但是无需清空requiredFieldStr add by 刘蒙
function beforeUpdateMethod(outparamters) {
	// 切换模板后，清空requiredFieldStr隐藏域的值
	var requiredFieldStr = document.getElementById('requiredFieldStr');
	requiredFieldStr.value = "";
	isTargetTemplate = outparamters.getValue("isTargetTemplate");
	updateMethod(outparamters);
}
function updateMethod(outparamters)
{
	var flag=outparamters.getValue("flag");
	var busitype = document.getElementById('planBusitype');
	//用于判断是否是个性化模板
	if(busitype!=null && busitype.value=='0')
	{
		if(flag=='1')//个性化模板对应目标管理的考核方法
		{
			document.getElementById('methodflag').value="2";
			if(examPlanForm.method[1]){
				examPlanForm.method[1].click();
			}
//			examPlanForm.method[1].checked=true;
//			examPlanForm.method[1].disabled=false;//个性化模板只能用于目标计划
//			examPlanForm.method[0].disabled=true;
		}
		else
		{
			document.getElementById('methodflag').value="1";
			if(examPlanForm.method[0]){
				examPlanForm.method[0].click();
			}
//			examPlanForm.method[0].disabled=false;
//			examPlanForm.method[0].checked=true;
//			examPlanForm.method[1].disabled=false;//而非个性化模板既可用于360又可用于目标计划
		}
	}
}
function checkTemplateType(){
	     var template_id=document.getElementById("template_id").value;
	     if(template_id=="")
	     return;
	     var hashvo=new ParameterSet();
		 hashvo.setValue("template_id",template_id);
		 // 【2331】确定考核方法用 add by 刘蒙
		 var plan_id = $F('plan_id');
		 hashvo.setValue("plan_id",plan_id);
		 var request=new Request({method:'post',asynchronous:false,onSuccess:updateMethod,functionId:'9022000023'},hashvo);	
}
function setMehod()
{/* 修改模板 考核方法相应调整 但是允许用户自己再修改考核方法 所以注释了此处的代码
	if(document.getElementById('methodflag').value=="1")
		examPlanForm.method[0].checked=true;
	if(document.getElementById('methodflag').value=="2")
		examPlanForm.method[1].checked=true;	*/
	var a=document.getElementsByName("examPlanVo.string(method)");//考核方法
	if(isTargetTemplate=="true"){
		if(a[0].checked){
			alert("个性化模板只能用于目标计划！")
			a[1].checked=true;
			return;
		}
	}
	var b=document.getElementsByName("examPlanVo.string(gather_type)");//考核类型
	if(a[1].checked){
		Element.hide("hide_radio");
		b[0].checked=true;	
	}else{
		Element.show("hide_radio");
	}
}
/**
 * 判断当前浏览器是否为ie6
 * 返回boolean 可直接用于判断 
 * @returns {Boolean}
 */
function isIE6() 
{ 
	if(navigator.appName == "Microsoft Internet Explorer") 
	{ 
		if(navigator.userAgent.indexOf("MSIE 6.0")>0) 
		{ 
			return true;
		}else{
			return false;
		}
	}else{
		return false;
	}
}
function getParam()
{
	var busitype=document.getElementById('planBusitype');
	var planId=document.getElementById('plan_id').value;
	var status = document.getElementById('status').value;
	var templateId = document.getElementById('template_id').value;

	var object_type=2;		
	if(busitype!=null && busitype.value=='0')
	{		
		if (examPlanForm.object_type[0].checked==true)
			object_type=2;	
		else if (examPlanForm.object_type[1].checked==true)
			object_type=1;			
		else if (examPlanForm.object_type[2].checked==true)
			object_type=3;	
		else if (examPlanForm.object_type[3].checked==true)
			object_type=4;	
	}	
			
	var gather_type=0;
	for (var i=0;i<examPlanForm.gather_type.length;i++) 
	{
		if (examPlanForm.gather_type[i].checked==true)
			gather_type=i;	//0-网上 1-机读 2-网上+机读			
	}	

	var method = '1';
	if(busitype!=null && busitype.value=='0')
	{
		if(examPlanForm.method[1]&&examPlanForm.method[1].checked==true)
			method = '2';
	}
	
	var requiredFieldStr = document.getElementById("requiredFieldStr");
	
	if(templateId=='')
		templateId='isNull';
	var target_url="/performance/kh_plan/kh_params.do?b_query=link`paramOper=detail`plan_id="+planId+"`status="+status+"`templateId="+templateId+'`object_type='+object_type+'`method='+method+'`gather_type='+gather_type;
	if (requiredFieldStr) {
		target_url += "`requiredFieldStr=" + requiredFieldStr.value;
	}
 	var iframe_url="/general/query/common/iframe_query.jsp?src="+$URL.encode(target_url);

    if(window.showModalDialog){
        var return_vo= window.showModalDialog(iframe_url, "kh_param_options",
            "dialogWidth:730px; dialogHeight:700px;resizable:no;center:yes;scroll:no;status:no");
        getParam_ok(return_vo);
    }else{
        var config = {
            width:700,
            height:680,
            type:'2',
            id:'template_win'
        };
        modalDialog.showModalDialogs(iframe_url,'template_win',config);
    }

}
function getParam_ok(return_vo) {
    if (!return_vo) {
        return;
    }
    var requiredFieldStr = document.getElementById("requiredFieldStr");
    if (requiredFieldStr) { // 清空requiredFieldStr隐藏域 add by 刘蒙
        requiredFieldStr.value = return_vo.requiredFieldStr; // 将计划明细页的隐藏域requiredFieldStr置为参数页选择的指标 add by 刘蒙
    }
    if("1"==return_vo.byModel){///按岗位素质模型测评
        document.getElementsByName("gather_type")[1].disabled=true;
    }
    if("0"==return_vo.byModel){
        document.getElementsByName("gather_type")[1].disabled=false;
    }

}
/*
function getParam()
{
	var hashvo=new ParameterSet();			
	hashvo.setValue("thePlan",document.getElementById('plan_id').value);
	var request=new Request({method:'post',asynchronous:false,onSuccess:isExistPlan,functionId:'9022000022'},hashvo);
}
function isExistPlan(outparamters)
{
	var flag=outparamters.getValue("flag");	
	if(flag=='1')//新增设置参数
		alert('请先保存计划再设置计划参数！');
	else if(flag=='0')//编辑设置参数
		editParam();
}
function editParam()
{
	var planId=document.getElementById('plan_id').value;
	var status = document.getElementById('status').value;
	var templateId = document.getElementById('template_id').value;

	var object_type=2;
	for (var i=0;i<examPlanForm.object_type.length;i++) 
	{
		if (examPlanForm.object_type[i].checked==true)
			object_type=i;	//0-团队 1-人员			
	}	
	var gather_type=0;
	for (var i=0;i<examPlanForm.gather_type.length;i++) 
	{
		if (examPlanForm.gather_type[i].checked==true)
			gather_type=i;	//0-网上 1-机读 2-网上+机读				
	}	

	var method = '1';
	if(examPlanForm.method[1].checked==true)
		method = '2';
	
	if(templateId=='')
		templateId='isNull';
	var target_url="/performance/kh_plan/kh_params.do?b_query=link`paramOper=detail`plan_id="+planId+"`status="+status+"`templateId="+templateId+'`object_type='+object_type+'`method='+method+'`gather_type='+gather_type;
 	var iframe_url="/general/query/common/iframe_query.jsp?src="+target_url;
	var return_vo= window.showModalDialog(iframe_url, "kh_param_options", 
	              "dialogWidth:660px; dialogHeight:630px;resizable:no;center:yes;scroll:no;status:yes");	
	if(!return_vo)
		return false;	   
	if(return_vo.flag=="true")
	{
	   //用于考核主体类别中选中的分类
		document.getElementById('selectedStr').value=return_vo.selectedStr;
	  if(method=='1'){
		document.getElementById('dataGatherMode').value=return_vo.dataGatherMode;		
	  	document.getElementById('scaleToDegreeRule').value=return_vo.scaleToDegreeRule;
	  	document.getElementById('degreeShowType').value=return_vo.degreeShowType;
		document.getElementById('fineRestrict').checked=return_vo.fineRestrict;
		document.getElementById('fineMax').value=return_vo.fineMax;
		document.getElementById('badlyRestrict').checked=return_vo.badlyRestrict;
		document.getElementById('badlyMax').value=return_vo.badlyMax;
		document.getElementById('sameResultsOption').value=return_vo.sameResultsOption;
		document.getElementById('noCanSaveDegrees').value=return_vo.noCanSaveDegrees;
		document.getElementById('blankScoreOption').value=return_vo.blankScoreOption;
		document.getElementById('scoreWay').value=return_vo.scoreWay;	
		document.getElementById('mutiScoreGradeCtl').checked=return_vo.mutiScoreGradeCtl;
		document.getElementById('mitiScoreMergeSelfEval').checked=return_vo.mitiScoreMergeSelfEval;
		document.getElementById('checkGradeRange').value=return_vo.checkGradeRange;
		document.getElementById('blankScoreUseDegree').value=return_vo.blankScoreUseDegree;
		
		if(return_vo.fine_attributes!='')
			document.getElementById('fine_attributes').value=return_vo.fine_attributes;
		if(return_vo.badly_attributes!='')
			document.getElementById('badly_attributes').value=return_vo.badly_attributes;	
				
		document.getElementById('showIndicatorDesc').checked=return_vo.showIndicatorDesc;
		document.getElementById('showOneMark').checked=return_vo.showOneMark;
		document.getElementById('idioSummary').checked=return_vo.idioSummary;
		document.getElementById('showTotalScoreSort').checked=return_vo.showTotalScoreSort;
		document.getElementById('isShowSubmittedPlan').checked=return_vo.isShowSubmittedPlan;
		document.getElementById('showNoMarking').checked=return_vo.showNoMarking;
		document.getElementById('isEntireysub').checked=return_vo.isEntireysub;
		document.getElementById('scoreBySumup').checked=return_vo.scoreBySumup;
		document.getElementById('isShowSubmittedScores').checked=return_vo.isShowSubmittedScores;
		document.getElementById('selfScoreInDirectLeader').value=return_vo.selfScoreInDirectLeader;	
		document.getElementById('scoreNumPerPage').value=return_vo.scoreNumPerPage;
		document.getElementById('isShowOrder').checked=return_vo.isShowOrder;
		document.getElementById('autoCalcTotalScoreAndOrder').checked=return_vo.autoCalcTotalScoreAndOrder;
		document.getElementById('perSet').value=return_vo.perSet;
		document.getElementById('perSetShowMode').value=return_vo.perSetShowMode;
		document.getElementById('perSetStatMode').value=return_vo.perSetStatMode;
		document.getElementById('statCustomMode').checked=return_vo.statCustomMode;
		document.getElementById('statEndDate').value=replaceAll(return_vo.statEndDate,'-','.');
	    document.getElementById('statStartDate').value=replaceAll(return_vo.statStartDate,'-','.');
	    document.getElementById('noteIdioGoal').checked=return_vo.noteIdioGoal;
	    document.getElementById('nodeKnowDegree').checked=return_vo.nodeKnowDegree;
	    document.getElementById('performanceType').value=return_vo.performanceType;
	    document.getElementById('selfEvalNotScore').checked=return_vo.selfEvalNotScore;
	    document.getElementById('showIndicatorRole').checked=return_vo.showIndicatorRole;
		document.getElementById('showIndicatorDegree').checked=return_vo.showIndicatorDegree;
		document.getElementById('showIndicatorContent').checked=return_vo.showIndicatorContent;	
		document.getElementById('relatingTargetCard').checked=return_vo.relatingTargetCard;	
		document.getElementById('showDeductionCause').checked=return_vo.showDeductionCause;	
		document.getElementById('mustFillCause').checked=return_vo.mustFillCause;
		document.getElementById('canSaveAllObjsScoreSame').checked=return_vo.canSaveAllObjsScoreSame;	
		document.getElementById('showSumRow').checked=return_vo.showSumRow;	
	    document.getElementById('evalOutLimitStdScore').checked=return_vo.evalOutLimitStdScore;
		}else if(method=='2')
		{
			document.getElementById('keyEventEnabled').checked=return_vo.keyEventEnabled;
			document.getElementById('idioSummary').checked=return_vo.idioSummary;
			document.getElementById('isEntireysub').checked=return_vo.isEntireysub;
			document.getElementById('isShowSubmittedScores').checked=return_vo.isShowSubmittedScores;
			document.getElementById('publicPointCannotEdit').checked=return_vo.publicPointCannotEdit;
			document.getElementById('selfScoreInDirectLeader').value=return_vo.selfScoreInDirectLeader;	
			document.getElementById('dataGatherMode').value=return_vo.dataGatherMode;	
			document.getElementById('scaleToDegreeRule').value=return_vo.scaleToDegreeRule;				
			document.getElementById('targetMakeSeries').value=return_vo.targetMakeSeries;	
			document.getElementById('taskCanSign').checked=return_vo.taskCanSign;
			document.getElementById('taskNeedReview').checked=return_vo.taskNeedReview;
			document.getElementById('taskAdjustNeedNew').checked=return_vo.taskAdjustNeedNew;	
			document.getElementById('targetAppMode').value=return_vo.targetAppMode;	
			document.getElementById('targetAllowAdjustAfterApprove').checked=return_vo.targetAllowAdjustAfterApprove;		
			document.getElementById('allowLeadAdjustCard').checked=return_vo.allowLeadAdjustCard;	
			document.getElementById('allowSeeLowerGrade').checked=return_vo.allowSeeLowerGrade;	
			document.getElementById('dataGatherMode').value=return_vo.dataGatherMode;	
			document.getElementById('showDeductionCause').checked=return_vo.showDeductionCause;		
			document.getElementById('mustFillCause').checked=return_vo.mustFillCause;
			document.getElementById('targetTraceEnabled').checked=return_vo.targetTraceEnabled;	
			document.getElementById('evalCanNewPoint').checked=return_vo.evalCanNewPoint;	
			document.getElementById('targetTraceItem').value=return_vo.targetTraceItem;	
			document.getElementById('targetCollectItem').value=return_vo.targetCollectItem;	
			document.getElementById('noShowTargetAdjustHistory').checked=return_vo.noShowTargetAdjustHistory;	
			document.getElementById('allowLeaderTrace').checked=return_vo.allowLeaderTrace;	
			document.getElementById('blankScoreOption').value=return_vo.blankScoreOption;	
			document.getElementById('blankScoreUseDegree').value=return_vo.blankScoreUseDegree;
		    document.getElementById('evalOutLimitStdScore').checked=return_vo.evalOutLimitStdScore;
		    document.getElementById('processNoVerifyAllScore').checked=return_vo.processNoVerifyAllScore;	
		    document.getElementById('scoreWay').value=return_vo.scoreWay;			
		    document.getElementById('isLimitPointValue').checked=return_vo.isLimitPointValue;
		}		
		document.getElementById('wholeEval').checked=return_vo.wholeEval;
		document.getElementById('mustFillWholeEval').checked=return_vo.mustFillWholeEval;
		document.getElementById('showAppraiseExplain').checked=return_vo.showAppraiseExplain;
		document.getElementById('gatiShowDegree').checked=return_vo.gatiShowDegree;
		document.getElementById('descriptiveWholeEval').checked=return_vo.descriptiveWholeEval;		
		document.getElementById('showBackTables').value=return_vo.showBackTables;		
	} 
}
*/
	function save()
	{	
		var planName = document.getElementById('name').value;
		if(trimStr(planName)=='')
		{
			alert(KHPLAN_NAMENONULL);
			return;
		}		
		var approve_result = document.getElementById('approve_result').value;
		if(approve_result==KHPLAN_AGREE)
			document.getElementById('approve_result').value='1';
		if(approve_result==KHPLAN_NOAGREE)
			document.getElementById('approve_result').value='0';
		
		var cycle = document.getElementById('cycle').value;
		if(cycle==KHPLAN_YEAR)	
			document.getElementById('cycle').value='0';
		if(cycle==KHPLAN_HALFYEAR)	
			document.getElementById('cycle').value='1';
		if(cycle==KHPLAN_QUARTER)	
			document.getElementById('cycle').value='2';	
		if(cycle==KHPLAN_MONTH)	
			document.getElementById('cycle').value='3';	
		if(cycle==KHPLAN_INDEFINETIME)	
			document.getElementById('cycle').value='7';	
	
		document.examPlanForm.action="/performance/kh_plan/examPlanAdd.do?b_save=link";
		document.examPlanForm.submit();	
	}
	function goback()
	{
		var planId=document.getElementById('plan_id').value;
		document.examPlanForm.action="/performance/kh_plan/examPlanList.do?b_query=query&currentPlan="+planId;
		document.examPlanForm.submit();	
	}
	function setkhtimeqj()
	{
		var cycle = document.getElementById('cycle').value;
		var theyear= document.getElementById('theyear').value;
		var thequarter = document.getElementById('thequarter').value;
		var themonth = document.getElementById('themonth').value;
		var start_date= document.getElementById('start_date').value;
		var end_date = document.getElementById('end_date').value;
		if(cycle=='0')
			document.getElementById('khtimeqj').value=theyear+GZ_ANLAYSE_SETINFOR_YEAR;			
		if(cycle=='1')	
		{
			var thehalfyear= document.getElementById('thequarter').value;
			if(thehalfyear=='1')
				document.getElementById('khtimeqj').value=theyear+GZ_ANLAYSE_SETINFOR_YEAR+' '+ACHIEVEMENT_UPYEAR;
			else if(thehalfyear=='2')
				document.getElementById('khtimeqj').value=theyear+GZ_ANLAYSE_SETINFOR_YEAR+' '+ACHIEVEMENT_DOWNYEAR;
		}	
		if(cycle=='2')	
		{			
			if(thequarter=='01')
				document.getElementById('khtimeqj').value=theyear+GZ_ANLAYSE_SETINFOR_YEAR+' '+KHPLAN_ONEQUARTER;
		    if(thequarter=='02')
				document.getElementById('khtimeqj').value=theyear+GZ_ANLAYSE_SETINFOR_YEAR+' '+KHPLAN_TWOQUARTER;
			if(thequarter=='03')
				document.getElementById('khtimeqj').value=theyear+GZ_ANLAYSE_SETINFOR_YEAR+' '+KHPLAN_THREEQUARTER;
			if(thequarter=='04')
				document.getElementById('khtimeqj').value=theyear+GZ_ANLAYSE_SETINFOR_YEAR+' '+KHPLAN_FOREQUARTER;
		}
		if(cycle=='3')	
		{			
			if(themonth=='01')
				document.getElementById('khtimeqj').value=theyear+GZ_ANLAYSE_SETINFOR_YEAR+' '+KHPLAN_JANUARY;
			else if(themonth=='02')
				document.getElementById('khtimeqj').value=theyear+GZ_ANLAYSE_SETINFOR_YEAR+' '+KHPLAN_FEBRUARY;
			else if(themonth=='03')
				document.getElementById('khtimeqj').value=theyear+GZ_ANLAYSE_SETINFOR_YEAR+' '+KHPLAN_MARCH;
			else if(themonth=='04')
				document.getElementById('khtimeqj').value=theyear+GZ_ANLAYSE_SETINFOR_YEAR+' '+KHPLAN_APRIL;
			else if(themonth=='05')
				document.getElementById('khtimeqj').value=theyear+GZ_ANLAYSE_SETINFOR_YEAR+' '+KHPLAN_MAY;
			else if(themonth=='06')
				document.getElementById('khtimeqj').value=theyear+GZ_ANLAYSE_SETINFOR_YEAR+' '+KHPLAN_JUNE;
			else if(themonth=='07')
				document.getElementById('khtimeqj').value=theyear+GZ_ANLAYSE_SETINFOR_YEAR+' '+KHPLAN_JULY;
			else if(themonth=='08')
				document.getElementById('khtimeqj').value=theyear+GZ_ANLAYSE_SETINFOR_YEAR+' '+KHPLAN_AUGUEST;
			else if(themonth=='09')
				document.getElementById('khtimeqj').value=theyear+GZ_ANLAYSE_SETINFOR_YEAR+' '+KHPLAN_SEPTEMBER;
			else if(themonth=='10')
				document.getElementById('khtimeqj').value=theyear+GZ_ANLAYSE_SETINFOR_YEAR+' '+KHPLAN_OCTOBER;
			else if(themonth=='11')
				document.getElementById('khtimeqj').value=theyear+GZ_ANLAYSE_SETINFOR_YEAR+' '+KHPLAN_NOVEMBER;
			else if(themonth=='12')
				document.getElementById('khtimeqj').value=theyear+GZ_ANLAYSE_SETINFOR_YEAR+' '+KHPLAN_DECEMBER;		
		}
		if(cycle=='7')			
			document.getElementById('khtimeqj').value=replaceAll(start_date,'.','-')+'－'+replaceAll(end_date,'.','-');
	}
	function setCycle()
	{
		var cycle = document.getElementById('cycle').value;
		if(cycle=='0')	 
			document.getElementById('cycle').value=KHPLAN_YEAR;
		if(cycle=='1')	
			document.getElementById('cycle').value=KHPLAN_HALFYEAR;
		if(cycle=='2')	
			document.getElementById('cycle').value=KHPLAN_QUARTER;	
		if(cycle=='3')	
			document.getElementById('cycle').value=KHPLAN_MONTH;	
		if(cycle=='7')	
			document.getElementById('cycle').value=KHPLAN_INDEFINETIME;		
	}