
function selectBody(obj)
{
	var value_str=obj.value;
	if(obj.checked)
	{
		document.getElementById("grade_"+value_str).disabled=false;
		document.getElementById("seq_"+value_str).disabled=false;
		document.getElementById("grade_"+value_str).checked=true;
		// radio联动 by 刘蒙
		document.getElementsByName("opt_"+value_str)[1].disabled=false;
		document.getElementsByName("opt_"+value_str)[1].checked=true;
		document.getElementsByName("opt_"+value_str)[2].disabled=false;
	}
	else
	{
		document.getElementById("grade_"+value_str).disabled=true;
		document.getElementById("seq_"+value_str).disabled=true;
		document.getElementById("grade_"+value_str).checked=false;
		document.getElementById("seq_"+value_str).value="";
		// radio联动 by 刘蒙
		document.getElementsByName("opt_"+value_str)[0].value="0"; // 设置opt(hidden)为0
		//document.getElementsByName("opt_"+value_str)[1].checked=true; // opt0置为选中
		document.getElementsByName("opt_"+value_str)[1].checked=false;
		document.getElementsByName("opt_"+value_str)[2].checked=false;
		document.getElementsByName("opt_"+value_str)[1].disabled=true;
		document.getElementsByName("opt_"+value_str)[2].disabled=true;
	}

}

function setGradeValue(obj)
{
	var bodyId = obj.id.substring(obj.id.indexOf("_") + 1); // grade_xxxx --> xxxx
	if(!obj.checked) {
		document.getElementById("seq_" + bodyId).value = ""; // 清空顺序号
		// radio联动 by 刘蒙
		document.getElementsByName("opt_" + bodyId)[0].value = "0"; // opt值置为0（打分）
		document.getElementsByName("opt_" + bodyId)[1].disabled = true;
		/*
		 * 【2991】计划参数选择考核主体不评分，应该默认将后面的内容（打分或者确认）置空。
		 * 当再次勾选主体打分时，默认为打分操作  jingq upd
		 * 2015.01.15
		 */
		document.getElementsByName("opt_" + bodyId)[1].checked = false;
		document.getElementsByName("opt_" + bodyId)[2].checked = false;
		document.getElementsByName("opt_" + bodyId)[2].disabled = true;
	} else {
		document.getElementsByName("opt_" + bodyId)[1].disabled = false;
		document.getElementsByName("opt_" + bodyId)[2].disabled = false;
		document.getElementsByName("opt_" + bodyId)[1].checked = true;
	}
}
		
function valiSeqData(obj)
{
	if(trim(obj.value).length>0)
	{
		if(! /^[1-9]\d*$/.test(trim(obj.value)))
		{
			 alert("评分顺序号为正整数!");
			 obj.value="";
		}
		 
	}
}

function inputFile()
{
	var isBrowse= document.getElementById('isBrowse').value;
	if(isBrowse=='1')
	{
		if (confirm(KHPLAN_ERRORINFO1))
		{	
			var target_url="/performance/kh_plan/kh_params_file.do?br_query=link`plan_id="+planId;
 			var iframe_url="/general/query/common/iframe_query.jsp?src="+$URL.encode(target_url);

            var config = {
                width:450,
                height:180,
                type:'2'
            };
            modalDialog.showModalDialogs(iframe_url,'kh_param_file',config,inputFile_window_ok);
		}
	}
	else
	{
		var target_url="/performance/kh_plan/kh_params_file.do?br_query=link`plan_id="+planId;
 		var iframe_url="/general/query/common/iframe_query.jsp?src="+$URL.encode(target_url);

        var config = {
            width:450,
            height:200,
            type:'2',
            id:'kh_param_file'
        };
        modalDialog.showModalDialogs(iframe_url,'kh_param_file',config,inputFile_window_ok);
	}
}
function inputFile_window_ok(return_vo) {
    if(!return_vo)
        return false;
    if(return_vo.flag=="true")
    {
        showElement('browse');
        document.getElementById('isBrowse').value='1';
    }
}

function showMenRefDept(planId,status,templateId)
{	
	var target_url ="/performance/kh_plan/kh_params.do?b_search=link`plan_id="+planId+"`status="+status+"`templateId="+templateId;
	var iframe_url="/general/query/common/iframe_query.jsp?src="+$URL.encode(target_url);

    if(window.showModalDialog){
        var return_vo= window.showModalDialog(iframe_url, "",
            "dialogWidth:450px; dialogHeight:140px;resizable:no;center:yes;scroll:no;status:no");
    }else{
        var config = {
            width:450,
            height:140,
            type:'2'
        };
        modalDialog.showModalDialogs(iframe_url,'',config);
    }
}
function outputFile()
{
//	document.downLoadForm.action="/servlet/performance/kh_plan/fileDownLoad";
//	document.downLoadForm.target="_blank";
//	document.downLoadForm.submit();
	
	window.location.target="_blank";
	window.location.href = "/servlet/performance/kh_plan/fileDownLoad?plan_id="+planId;
}
function replaceAll(str, sptr, sptr1)
{
	while (str.indexOf(sptr) >= 0)
	{
   		str = str.replace(sptr, sptr1);
	}
	return str;
}
function partRescrit(templateId,type)
{
	if(templateId=='' || templateId=='isNull')
	{
		alert(KHPLAN_ERRORINFO2);
		return;
	}

	var wholeEval = document.getElementById('wholeEval').checked;
	var target_url="/performance/kh_plan/param_partRestrict.do?b_query=link`type="+type+"`wholeEval="+wholeEval+"`plan_id="+planId+"`templId="+templateId+"`theStatus="+theStatus;
 	var iframe_url="/general/query/common/iframe_query.jsp?src="+$URL.encode(target_url);

    if(window.showModalDialog){
        var return_vo= window.showModalDialog(iframe_url, "mainbody_win",
            "dialogWidth:450px; dialogHeight:450px;resizable:no;center:yes;scroll:no;status:no");
    }else{
        var config = {
            width:550,
            height:450,
            type:'2',
            id:'partRescrit_win'
        };
        modalDialog.showModalDialogs(iframe_url,'partRescrit_win',config);
    }
}

function defineIndex()
{
	var arguments=new Array();
	arguments[0]=document.getElementById('allowLeaderTrace').checked;
	arguments[1]=theStatus;  
	var attach=document.getElementById('taskSupportAttach').checked;
	var isCheck="0";
	if(attach)
	   isCheck="1";
	var target_url="/performance/kh_plan/kh_params.do?b_defTargetItem=link`oper=init`plan_id="+planId+"`isCheck="+isCheck;

 	var iframe_url="/general/query/common/iframe_query.jsp?src="+$URL.encode(target_url);
 	var iTop = (window.screen.height-500)/2; //获得窗口的垂直位置;
    var iLeft = (window.screen.width-460)/2;  //获得窗口的水平位置;
    if(window.showModalDialog){
        var return_vo= window.showModalDialog(iframe_url, arguments,
            "dialogWidth:580px; dialogHeight:580px;resizable:no;center:yes;scroll:no;status:no");
        defineIndex_window_ok(return_vo);
    }else{
        window.dialogArguments=arguments;
        var config = {
            width:580,
            height:580,
            type:'2',
            id:'defineIndex'
        };
        modalDialog.showModalDialogs(iframe_url,'defineIndex',config);
    }

}



function hideElement(id) {
    if(document.getElementById(id)) {
        document.getElementById(id).style.display = "none";
    }
}

function showElement(id) {
	if(document.getElementById(id)) {
        document.getElementById(id).style.display = "";
    }
}

function defineIndex_window_ok(return_vo) {
    if(!return_vo)
        return false;

    // 标准分值不选中时，参数"评分得分不受标准分限制"必须为选中状态，且不可操作
    // 同时processNoVerifyAllScore取消选中,不可操作 add by 刘蒙
    var limitScore = document.getElementById("evalOutLimitStdScore");
    var noVerify = document.getElementById("processNoVerifyAllScore");
    if (return_vo.targetDefineItem) {
        if (return_vo.targetDefineItem.indexOf("P0413") < 0) {
            limitScore.checked = true;
            limitScore.onclick = function() {
                alert("目标卡指标中没有选择[标准分值]，不可取消。");
                return false;
            };
            noVerify.checked = false;
            noVerify.onclick = function() {
                alert("目标卡指标中没有选择[标准分值]，不可选择。");
                return false;
            };
        } else {
            limitScore.onclick = null;
            noVerify.onclick = null;
        }
    }

    if(return_vo.flag=="true")
    {
        examPlanForm.targetCalcItem.value=return_vo.targetCalcItem;
        examPlanForm.targetTraceItem.value=return_vo.targetTraceItem;
        examPlanForm.targetCollectItem.value=return_vo.targetCollectItem;
        examPlanForm.allowLeaderTrace.checked=return_vo.allowLeaderTrace;
        examPlanForm.targetDefineItem.value=return_vo.targetDefineItem;
        examPlanForm.targetMustFillItem.value=return_vo.targetMustFillItem;
        examPlanForm.targetUsePrevious.value=return_vo.targetUsePrevious;
        examPlanForm.taskNameDesc.value=getDecodeStr(return_vo.taskNameDesc);
    }
}





function saveParam()
{
	// 自动计算参数需要配合显示排名和显示总分参数使用 lium
	var _auto = document.getElementById("autoCalcTotalScoreAndOrder"); // 自动计算总分，排名
	var _order = document.getElementById("isShowOrder"); // 显示排名
	var _score = document.getElementById("showTotalScoreSort"); // 显示总分
	var isAllowed = true; // 是否允许保存(通过以下校验)
	if (_auto && _auto.checked) {
		if (_order) {
			if (_score) {
				isAllowed = !_order.checked && !_score.checked ? false : true;
			} else {
				isAllowed = _order.checked ? true : false;
			}
		} else {
			isAllowed = _score && !_score.checked ? false : true;
		}
	}
	if (!isAllowed) {
		alert("\"显示排名\"或者\"显示总分\"参数至少选中一个\"自动计算总分，排名\"参数才有效");
		return;
	}
	
	if(theStatus!='0' && theStatus!='5')//非起草和暂停的状态直接关闭窗口，不做保存操作
	{	window.close(); return;}//由于关闭窗口后代码还在继续走所以在此加个return
		
	//目标管理中，如果主体类别中设置了团队负责人，打分途径就只能选择网上打分	,人员的也只能网上打分 所以目标计划打分途径应该默认为网上打分
	//目标管理计划 考核对象类别不是人员的可以不设置团队负责人
	/*
	if(theMethod =='2')
	{
		var isTeamLeaderSel="0";
		for (var i=0;i<examPlanForm.bodyId.length;i++) 
		{
			if (examPlanForm.bodyId[i].checked && examPlanForm.bodyId[i].value=='-1' && isTeamLeaderSel=="0")
			{
				isTeamLeaderSel="1";break;
			}				
		}		
	    if(isTeamLeaderSel=="0" && theObjectType!='2')
		{
				alert(KHPLAN_ERRORINFO7);
				return;
		}
	}*/
	//验证目标卡制订支持的等级在主体类别中是否选择了
	var selectedStr = '';
	var a = new Array('1', '0', '-1','-2');  
	var b = new Array('上级', '上上级', '第三级领导','第四级领导'); 
	if(theMethod =='2' && document.getElementById('targetMakeSeries')!=null && (document.getElementById('spByBodySeq')!=null && document.getElementById('spByBodySeq').checked==false))
	{
		var isTeamLeaderSel="0";
		for (var i=0;i<examPlanForm.bodyId.length;i++) 
		{
			if (examPlanForm.bodyId[i].checked && examPlanForm.bodyId[i].value=='-1' && isTeamLeaderSel=="0")
			{
				isTeamLeaderSel="1";break;
			}				
		}	
		
		if(theObjectType=='2' || (theObjectType!='2' && isTeamLeaderSel=='1'))
		{
			var targetMakeSeries = document.getElementById('targetMakeSeries').value;	
			for (var i=0;i<examPlanForm.bodyId.length;i++) 
			{
				if (examPlanForm.bodyId[i].checked)
					selectedStr=selectedStr+examPlanForm.level[i].value+','; 
			}
			selectedStr=","+selectedStr;
			var info="";	
			if(targetMakeSeries=='1')
			{
				if(selectedStr.indexOf(","+a[0]+",")==-1)
					info+=b[0]+'、';
			}
			else if(targetMakeSeries=='2')
			{
				for(var i=0;i<2;i++)
				{
					if(selectedStr.indexOf(","+a[i]+",")==-1)
						info+=b[i]+'、';
				}
			}
			else if(targetMakeSeries=='3')
			{
				for(var i=0;i<3;i++)
				{
					if(selectedStr.indexOf(","+a[i]+",")==-1)
						info+=b[i]+'、';
				}
			}
			else if(targetMakeSeries=='4')
			{
				for(var i=0;i<4;i++)
				{					
					if(selectedStr.indexOf(","+a[i]+",")==-1)
						info+=b[i]+'、';				
				}				
			}
			if(info!='')
			{
				info=info.substring(0,info.length-1);
				info='审批中'+info+'没有设置相应主体类别！';
				alert(info);
				return;
			}	
		}
		
	}

	//按考核主体先后顺序进行审批，加上设置主题类别的判断
	
	if(theMethod =='2' && (document.getElementById('spByBodySeq')!=null && document.getElementById('spByBodySeq').checked==true))
	{
		var isTeamLeaderSel="0";
		for (var i=0;i<examPlanForm.bodyId.length;i++) 
		{
			if (examPlanForm.bodyId[i].checked && examPlanForm.bodyId[i].value=='-1' && isTeamLeaderSel=="0")
			{
				isTeamLeaderSel="1";break;
			}				
		}	
		
		if(theObjectType=='2' || (theObjectType!='2' && isTeamLeaderSel=='1'))
		{
			var targetMakeSeries = document.getElementById('targetMakeSeries').value;
			var bool=true;	
			for (var i=0;i<examPlanForm.bodyId.length;i++) 
			{
				if (examPlanForm.bodyId[i].checked)
					bool=false;
			}
			
			if(bool){
				alert("请设置主体类别!");
				return;
			}
			/*selectedStr=","+selectedStr;
			var info="";	
			if(selectedStr.indexOf(","+a[0]+",")==-1)
					info+=b[0]+'、';
			if(info!='')
			{
				info=info.substring(0,info.length-1);
				info='审批中'+info+'没有设置相应主体类别！';
				alert(info);
				return;
			}	
			*/
		}	
	}

	/*
	var hashvo=new ParameterSet();			
	hashvo.setValue("thePlan",planId);
	var request=new Request({method:'post',asynchronous:false,onSuccess:selSave,functionId:'9022000022'},hashvo);*/
	editSave();
}
function selSave(outparamters)
{
	var flag=outparamters.getValue("flag");	
	if(flag=='1')//新增设置参数
		addSave();
	else if(flag=='0')//编辑设置参数
		editSave();
}
function editSave()
{
	var selectedStr='';
	if(theStatus=='5' || theStatus=='0')
	{
		var bodys = khbodySel.split(',');
		var bodyCns = khbodyCn.split(',');	
		for(var j=0;j<bodys.length;j++)
		{	if(bodys[j]=='') continue;
			var exist=0;
			for (var i=0;i<examPlanForm.bodyId.length;i++) 
			{
				if (examPlanForm.bodyId[i].checked && bodys[j]==examPlanForm.bodyId[i].value)
					 exist=1;
			}

			if(exist==0)
			{
				if(confirm(DEL_BODY_INFO1+bodyCns[j].substring(0,bodyCns[j].indexOf('('))+DEL_BODY_INFO2))
				{
					var hashvo=new ParameterSet();			
					hashvo.setValue("plan_id",planId);
					hashvo.setValue("body_id",bodys[j]);
					var request=new Request({method:'post',asynchronous:false,functionId:'9022000025'},hashvo);
				}else
					selectedStr=selectedStr+bodys[j]+','; 
			}
		}
	}
	dafen_beforeSave();
	if(theMethod =='1')
	{		
		if(theGatherType != '1')
		{
			bs_beforeSave();	
		}    
	}
	
	var str = "";
	for (var i=0;i<examPlanForm.bodyId.length;i++) 
	{
		if (examPlanForm.bodyId[i].checked)
		{
			var _str=examPlanForm.bodyId[i].value;
			 
			if(theMethod =='2')
			{
				var _id=examPlanForm.bodyId[i].value; 
				if(document.getElementById("grade_"+_id).checked)
				{
					_str += "/0"; // isgrade
					_str += "/" + trim(document.getElementById("seq_"+_id).value); // grade_seq
					var optValue = trim(document.getElementsByName("opt_"+_id)[0].value);
					_str += "/" + (optValue || "0"); // opt
					
					if(trim(document.getElementById("seq_"+_id).value).length>8)
						str = "评分顺序输入数据长度过长！";
				}
				else
					_str=_str+"/1//0";
			 } 
			selectedStr=selectedStr+_str+',';
		}
	}
	if(str!="" && str.length>0)
	{
		alert(str);
		return;
	}
	
	var blind=document.getElementById("blind").value;
	if(blind>100)
	{
		alert("评价盲点百分比不能大于100%！");
		return;
	}

	var eva=document.getElementsByName("eva");
	var e_str="";
	if(eva)
	{
	   for(var i=0;i<eva.length;i++)
	   {
	      if(eva[i].checked)
	      {
	        e_str+=","+eva[i].value;
	      }
	   }
	}
	if(trim(e_str)!="")
	{
		document.examPlanForm.evaluate_str.value=e_str.substring(1);
	}else{
		document.examPlanForm.evaluate_str.value="NO";
	}
	var paramStr = "&status="+theStatus+"&method="+theMethod+"&gather_type="+theGatherType+"&object_type="+theObjectType+"&templateId="+theTemplateId;
	document.examPlanForm.bodyTypeIds.value=selectedStr;
	document.examPlanForm.action="/performance/kh_plan/examPlanList.do?b_saveparam=link&plan_id="+planId+paramStr;
	document.examPlanForm.submit();	
}
function addSave()
{  
	var selectedStr='';
	for (var i=0;i<examPlanForm.bodyId.length;i++) 
	{
		if (examPlanForm.bodyId[i].checked)
			selectedStr=selectedStr+examPlanForm.bodyId[i].value+','; 
	}			
	var thevo=new Object();
	thevo.flag="true";		
	thevo.selectedStr=selectedStr;
	thevo.method=theMethod ;	
	dafen_beforeSave();	
	if(theMethod =='1')
	{		
		bs_beforeSave();	
		//打分控制参数
		thevo.dataGatherMode=$F('dataGatherMode');
		thevo.scaleToDegreeRule=$F('scaleToDegreeRule');
		thevo.degreeShowType=$F('degreeShowType');				
		thevo.fineRestrict=document.getElementById('fineRestrict').checked;					
		thevo.fineMax=document.getElementById('fineMax').value;							
		thevo.badlyRestrict=document.getElementById('badlyRestrict').checked;				
		thevo.badlyMax=document.getElementById('badlyMax').value;		
		thevo.sameResultsOption=$F('sameResultsOption');
		thevo.blankScoreOption=$F('blankScoreOption');
		thevo.scoreWay=document.getElementById('scoreWay').value;
		thevo.noCanSaveDegrees=document.getElementById('noCanSaveDegrees').value;
		thevo.fine_attributes=document.getElementById('fine_attributes').value;
		thevo.badly_attributes=document.getElementById('badly_attributes').value;
		thevo.blankScoreUseDegree=document.getElementById('blankScoreUseDegree').value;
		
		//BS参数
		thevo.showIndicatorDesc=document.getElementById('showIndicatorDesc').checked;
		thevo.showOneMark=document.getElementById('showOneMark').checked;	
		thevo.idioSummary=document.getElementById('idioSummary').checked;
		thevo.showTotalScoreSort=document.getElementById('showTotalScoreSort').checked;
		thevo.isShowSubmittedPlan=document.getElementById('isShowSubmittedPlan').checked;		
		thevo.showNoMarking=document.getElementById('showNoMarking').checked;		
		thevo.isEntireysub=document.getElementById('isEntireysub').checked;
		thevo.scoreBySumup=document.getElementById('scoreBySumup').checked;
		thevo.isShowSubmittedScores=document.getElementById('isShowSubmittedScores').checked;
		thevo.selfScoreInDirectLeader=document.getElementById('selfScoreInDirectLeader').value;
		thevo.scoreNumPerPage=document.getElementById('scoreNumPerPage').value;
		thevo.isShowOrder=document.getElementById('isShowOrder').checked;
		thevo.mutiScoreGradeCtl=document.getElementById('mutiScoreGradeCtl').checked;
		thevo.mitiScoreMergeSelfEval=document.getElementById('mitiScoreMergeSelfEval').checked;
		thevo.checkGradeRange=document.getElementById('checkGradeRange').value;
		thevo.autoCalcTotalScoreAndOrder=document.getElementById('autoCalcTotalScoreAndOrder').checked;
		thevo.perSet=document.getElementById('perSet').value;
		thevo.perSetShowMode=document.getElementById('perSetShowMode').value;
		thevo.perSetStatMode=document.getElementById('perSetStatMode').value;
		thevo.statCustomMode=document.getElementById('statCustomMode').checked;
		thevo.statEndDate=document.getElementById('editor2').value;
		thevo.statStartDate=document.getElementById('editor1').value;
		thevo.noteIdioGoal=document.getElementById('noteIdioGoal').checked;		
		thevo.selfEvalNotScore=document.getElementById('selfEvalNotScore').checked;
		thevo.showIndicatorRole=document.getElementById('showIndicatorRole').checked;		
		thevo.showIndicatorDegree=document.getElementById('showIndicatorDegree').checked;
		thevo.showIndicatorContent=document.getElementById('showIndicatorContent').checked;
	//	thevo.relatingTargetCard=document.getElementById('relatingTargetCard').checked;
		thevo.relatingTargetCard=$F('relatingTargetCard');
		thevo.showDeductionCause=document.getElementById('showDeductionCause').checked;
		thevo.mustFillCause=document.getElementById('mustFillCause').checked;
		thevo.canSaveAllObjsScoreSame=document.getElementById('canSaveAllObjsScoreSame').checked;
		thevo.showSumRow=document.getElementById('showSumRow').checked;
		thevo.evalOutLimitStdScore=document.getElementById('evalOutLimitStdScore').checked;
	}else if(theMethod =='2')
	{
		//目标管理
		thevo.keyEventEnabled=document.getElementById('keyEventEnabled').checked;	
		thevo.idioSummary=document.getElementById('idioSummary').checked;
		thevo.isEntireysub=document.getElementById('isEntireysub').checked;
		thevo.isShowSubmittedScores=document.getElementById('isShowSubmittedScores').checked;
		thevo.publicPointCannotEdit=document.getElementById('publicPointCannotEdit').checked;
		thevo.selfScoreInDirectLeader=document.getElementById('selfScoreInDirectLeader').value;
		thevo.dataGatherMode=$F('dataGatherMode');
		thevo.degreeShowType=$F('degreeShowType');	
		thevo.scaleToDegreeRule=$F('scaleToDegreeRule');	
		thevo.targetMakeSeries=$F('targetMakeSeries');
		thevo.taskCanSign=document.getElementById('taskCanSign').checked;	
		thevo.taskAdjustNeedNew=document.getElementById('taskAdjustNeedNew').checked;
		thevo.taskNeedReview=document.getElementById('taskNeedReview').checked;	
		thevo.targetAppMode=$F('targetAppMode');
		thevo.targetAllowAdjustAfterApprove=document.getElementById('targetAllowAdjustAfterApprove').checked;		
		thevo.allowLeadAdjustCard=document.getElementById('allowLeadAdjustCard').checked;
		thevo.allowSeeLowerGrade=document.getElementById('allowSeeLowerGrade').checked;	
		thevo.dataGatherMode=$F('dataGatherMode');
		thevo.showDeductionCause=document.getElementById('showDeductionCause').checked;	
		thevo.mustFillCause=document.getElementById('mustFillCause').checked;	
		thevo.targetTraceEnabled=document.getElementById('targetTraceEnabled').checked;
		thevo.evalCanNewPoint=document.getElementById('evalCanNewPoint').checked;		
		thevo.targetTraceItem=document.getElementById('targetTraceItem').value;
		thevo.targetCollectItem=document.getElementById('targetCollectItem').value;
		thevo.targetCalcItem=document.getElementById('targetCalcItem').value;
		thevo.noShowTargetAdjustHistory=document.getElementById('noShowTargetAdjustHistory').checked;
		thevo.allowLeaderTrace=document.getElementById('allowLeaderTrace').checked;
		thevo.blankScoreOption=$F('blankScoreOption');
		thevo.blankScoreUseDegree=document.getElementById('blankScoreUseDegree').value;
		thevo.evalOutLimitStdScore=document.getElementById('evalOutLimitStdScore').checked;
		thevo.processNoVerifyAllScore=document.getElementById('processNoVerifyAllScore').checked;
		thevo.isLimitPointValue=document.getElementById('isLimitPointValue').checked;
		thevo.showLeaderEval=document.getElementById('showLeaderEval').checked;
		thevo.scoreWay=document.getElementById('scoreWay').value;
	}
	//其它参数
	thevo.wholeEval=document.getElementById('wholeEval').checked;	
	thevo.mustFillWholeEval=document.getElementById('mustFillWholeEval').checked;	
	thevo.descriptiveWholeEval=document.getElementById('descriptiveWholeEval').checked;
	thevo.showAppraiseExplain=document.getElementById('showAppraiseExplain').checked;
	thevo.gatiShowDegree=document.getElementById('gatiShowDegree').checked;
	thevo.showBackTables=document.getElementById('showBackTables').value;
	if(theMethod =='1')
	{
		thevo.nodeKnowDegree=document.getElementById('nodeKnowDegree').checked;
		thevo.performanceType=document.getElementById('performanceType').value;
	}		
	window.returnValue=thevo;
	window.close();	
}
function dispTime()
{
	var dataFilter = document.getElementById('perSetStatMode').value;
	if(dataFilter=='9')
		showElement('timeduan');
	else
	{
		hideElement('timeduan');
//		document.getElementById('editor1').value='';
//		document.getElementById('editor2').value='';
	}
}
function readOnlySet()
{	
	if(theStatus!='0' && theStatus!='5')//起草和暂停的状态可以编辑参数设置
	{				
		for(var i=0;i<document.examPlanForm.elements.length;i++)
		{				
			//if(document.examPlanForm.elements[i].name!='blankScoreOption' && document.examPlanForm.elements[i].name!='blankScoreOptionset' && document.examPlanForm.elements[i].type!='button')				
			if(document.examPlanForm.elements[i].name!='filterset' && document.examPlanForm.elements[i].name!='blankScoreOptionset')
				document.examPlanForm.elements[i].disabled=true;		
		}
		document.getElementById('cancel').disabled=false;
		if(document.getElementById("busitype")!=null && document.getElementById("busitype").value=="0" && document.getElementById('b_menRefDeptTmpl')!=null)	
			document.getElementById("b_menRefDeptTmpl").disabled=false;
		if(document.getElementById('mustWriteButton')!=null)	
			document.getElementById("mustWriteButton").disabled=false;
		if(document.getElementById('bodyDefine')!=null)
			document.getElementById('bodyDefine').disabled=false;

		if(document.getElementById('b_isBrowse')!=null)
			document.getElementById('b_isBrowse').disabled=false;			
		if(document.getElementById('know_button')!=null)
			document.getElementById('know_button').disabled=false;
		if(document.getElementById('message_button')!=null)
			document.getElementById('message_button').disabled=false;	
		if(document.getElementById('defineIndex_bt')!=null)
			document.getElementById('defineIndex_bt').disabled=false;	
		if(document.getElementById('b_showBack')!=null)
			document.getElementById('b_showBack').disabled=false;	
		if(document.getElementById('sameResultDef_bt')!=null)
			document.getElementById('sameResultDef_bt').disabled=false;	
		if(document.getElementById('calbutton')!=null)
			document.getElementById('calbutton').disabled=false;	
		if(document.getElementById('probutton')!=null)
			document.getElementById('probutton').disabled=false;
				
		if(document.getElementById('button1')!=null)
		{
			if(document.forms[0].nohigh[2].checked)
				document.getElementById('button1').disabled=false;	
		}
		if(document.getElementById('button2')!=null)
		{
			if(document.forms[0].nolow[2].checked)
				document.getElementById('button2').disabled=false;	
		}	
				
		document.getElementById('ok').disabled=true;
	}
	if(theStatus=='5' && document.getElementById('scoreWay')!=null)	
		document.getElementById('scoreWay').disabled=true;
	
}
function dafen_onLoad()
{
	if($F('dataGatherMode')=="2")
		 showElement('datepnl');
	else
		 hideElement('datepnl');
	    
	if($F('dataGatherMode')=="1")
		 showElement('datepn2');
	else
		 hideElement('datepn2');    
	 
	if($F('dataGatherMode')=="4")
		 showElement('datepn3');
	else
		 hideElement('datepn3');   
	    
	setTheHihtestValue();	
	
	if(theGatherType=='1')
	{
	 	document.getElementById("gather_type_1").style.display='none';
	}

	if($F('blankScoreOption')=="2")
		 document.getElementById("blankScoreUseDegree").disabled=false;
	else
	{
		 document.getElementById("blankScoreUseDegree").disabled=true;
		 document.getElementById("blankScoreUseDegree").value='A';
	}
	 radioSelect2();
	 if(document.getElementById("busitype")!=null && document.getElementById("busitype").value=="0")
	 	showWarnOpt('warnOpt2');
	othermessageButton();
	otherShowEvalClass();
	changePerformanceType();
}
function setJw(theObj)
{
	if($F('blankScoreOption')=="2")
		 document.getElementById("blankScoreUseDegree").disabled=false;
	else
		 document.getElementById("blankScoreUseDegree").disabled=true;
}
function setTheHihtestValue()
{
	selectRestrict('low');
    selectRestrict('high');
	    
	var onhigh=document.getElementById("fineRestrict").checked;
	var fineMax = document.getElementById("fineMax").value;	
	if (onhigh==true &&  fineMax!='' && parseFloat(fineMax)<1 && parseFloat(fineMax)>0)
	{
		document.getElementById("bili1").value=parseFloat(fineMax)*100;
		examPlanForm.nohigh[0].checked=true;
	}else if(onhigh==true && fineMax!='' && parseFloat(fineMax)>=0)
	{
		document.getElementById("value1").value=fineMax;
		examPlanForm.nohigh[1].checked=true			
	}else if (theMethod =='1' && onhigh==true &&  fineMax=='-1')
		examPlanForm.nohigh[2].checked=true;
		
	var badlyMax = document.getElementById("badlyMax").value;
	var onlow=document.getElementById("badlyRestrict").checked;
	if (onlow==true && badlyMax!='' && parseFloat(badlyMax)<1 && parseFloat(badlyMax)>0)
	{
		document.getElementById("bili2").value=parseFloat(badlyMax)*100;
		examPlanForm.nolow[0].checked=true;
	}else if(onlow==true && badlyMax!='' && parseFloat(badlyMax)>=0)
	{
		document.getElementById("value2").value=badlyMax;
		examPlanForm.nolow[1].checked=true			
	}else if (theMethod =='1' && onlow==true && badlyMax=='-1')
		examPlanForm.nolow[2].checked=true;
	radioSelect();
	
//	if (theMethod =='2')
	{
		selectScoreNumLess();	
		if (sameAllScoreValue!='' && parseFloat(sameAllScoreValue)<=1 && parseFloat(sameAllScoreValue)>0)
		{
			document.getElementById("biliObj").value=parseFloat(sameAllScoreValue)*100;
			examPlanForm.noScoreObj[0].checked=true;
		}else if(sameAllScoreValue!='' && parseFloat(sameAllScoreValue)>=2)
		{
			document.getElementById("valueObj").value=sameAllScoreValue;
			examPlanForm.noScoreObj[1].checked=true			
		}
		radioSelectScoreNum();
	}
}
function selectScoreNumLess()
{	
	var noScoreObj=false;
	if(sameAllScoreValue!='' && sameAllScoreValue!='0')
	{
		document.getElementById("sameAllScoreNumLess").checked=true;
		noScoreObj=true;
	}else
		document.getElementById("sameAllScoreNumLess").checked=false;
			
	if(noScoreObj==true)
	{
		for (var i=0;i<examPlanForm.noScoreObj.length;i++) 
		{
			if (examPlanForm.noScoreObj[i].disabled==true)
				examPlanForm.noScoreObj[i].disabled=false;				
		}
	}
	else
	{
		for (var i=0;i<examPlanForm.noScoreObj.length;i++) 
		{
			if (examPlanForm.noScoreObj[i].disabled==false)
				examPlanForm.noScoreObj[i].disabled=true;				
		}
	}		
}
function selectScoreNum_Less()
{		
	var noScoreObj=document.getElementById("sameAllScoreNumLess").checked;		
	if(noScoreObj==true)
	{
		for (var i=0;i<examPlanForm.noScoreObj.length;i++) 
		{
			if (examPlanForm.noScoreObj[i].disabled==true)
				examPlanForm.noScoreObj[i].disabled=false;				
		}
	}
	else
	{
		for (var i=0;i<examPlanForm.noScoreObj.length;i++) 
		{
			if (examPlanForm.noScoreObj[i].disabled==false)
				examPlanForm.noScoreObj[i].disabled=true;				
		}
	}		
}
function selectRestrict(type)
{
	if(type=='high')
	{
		var onhigh=document.getElementById("fineRestrict").checked;
		if(onhigh==true)
		{
			for (var i=0;i<examPlanForm.nohigh.length;i++) 
			{
				if (examPlanForm.nohigh[i].disabled==true)
					examPlanForm.nohigh[i].disabled=false;				
			}
		}
		else
		{
			for (var i=0;i<examPlanForm.nohigh.length;i++) 
			{
				if (examPlanForm.nohigh[i].disabled==false)
					examPlanForm.nohigh[i].disabled=true;				
			}
		}
	}
	if(type=='low')
	{
		var onlow=document.getElementById("badlyRestrict").checked;
		if(onlow==true)
		{
			for (var i=0;i<examPlanForm.nolow.length;i++) 
			{
				if (examPlanForm.nolow[i].disabled==true)
					 examPlanForm.nolow[i].disabled=false;				
			}
		}
		else
		{
			for (var i=0;i<examPlanForm.nolow.length;i++) 
			{
				if (examPlanForm.nolow[i].disabled==false)
				 	 examPlanForm.nolow[i].disabled=true;				
			}
		}
	}	
}
function radioSelect()
{
	document.getElementById('bili1').disabled=true;
	document.getElementById('value1').disabled=true;
	if(document.getElementById('button1')!=null)
		document.getElementById('button1').disabled=true;
	document.getElementById('bili2').disabled=true;
	document.getElementById('value2').disabled=true;
	if(document.getElementById('button2')!=null)
		document.getElementById('button2').disabled=true;
	
	if(examPlanForm.nohigh[0].checked==true)
		document.getElementById('bili1').disabled=false;
	if(examPlanForm.nohigh[1].checked==true)
		document.getElementById('value1').disabled=false;
	if(theMethod =='1' && examPlanForm.nohigh[2].checked==true)
		document.getElementById('button1').disabled=false;	
		
	if(examPlanForm.nolow[0].checked==true)
		document.getElementById('bili2').disabled=false;
	if(examPlanForm.nolow[1].checked==true)
		document.getElementById('value2').disabled=false;
	if(theMethod =='1' && examPlanForm.nolow[2].checked==true)
		document.getElementById('button2').disabled=false;				
}
function radioSelectScoreNum()
{
	document.getElementById('biliObj').disabled=true;
	document.getElementById('valueObj').disabled=true;
		
	if(examPlanForm.noScoreObj[0].checked==true)
		document.getElementById('biliObj').disabled=false;
	if(examPlanForm.noScoreObj[1].checked==true)
		document.getElementById('valueObj').disabled=false;					
}
function radioSelect2()
{
	if(examPlanForm.sameResultsOption[0].checked==true)
		document.getElementById('sameResultDef_bt').disabled=true;
	if(examPlanForm.sameResultsOption[1].checked==true)
		document.getElementById('sameResultDef_bt').disabled=true;
	if(examPlanForm.sameResultsOption[2].checked==true)
		document.getElementById('sameResultDef_bt').disabled=false;	
}
function sameResultDefine()
{
	var theObj=document.getElementById('noCanSaveDegrees');
	var target_url="/performance/kh_plan/kh_params.do?b_noCanSaveDegrees=link`degrees="+theObj.value+'`plan_id='+planId+'`templateId='+theTemplateId+'`planStatus='+theStatus;
 	var iframe_url="/general/query/common/iframe_query.jsp?src="+$URL.encode(target_url);

    if(window.showModalDialog){
        var return_vo= window.showModalDialog(iframe_url, "",
            "dialogWidth:500px; dialogHeight:300px;resizable:no;center:yes;scroll:no;status:no");
        sameResultDefine_ok(return_vo);
    }else{
        var config = {
            width:500,
            height:300,
            type:'2',
            id:'sameResultDefine'
        };
        modalDialog.showModalDialogs(iframe_url,'sameResultDefine',config);
    }

}

function sameResultDefine_ok(return_vo) {
    if(!return_vo)
        return;
    else if(return_vo.flag=="true")
    {
        var theObj=document.getElementById('noCanSaveDegrees');
        theObj.value = return_vo.degrees;
    }
}
function checkValue(id)
{	
	var value = document.getElementById(id).value;
	if(id=='value1' || id=='value2' || id=='valueObj')
	{
		if(!isNum(value))
		{
			alert(KHPLAN_ERRORINFONUMBER);
			document.getElementById(id).focus();
			return;
		}
		else if(id=='valueObj' && parseFloat(value)<2)
		{
			alert("请输入大于等于2的数值！");
			document.getElementById(id).focus();
			return;
		}
	}
	if(id=='bili1' || id=='bili2' || id=='biliObj')
	{
		if(!isNum(value))
		{
			alert(KHPLAN_ERRORINFONUMBER);
			document.getElementById(id).focus();
			return;
		}
		else if(parseFloat(value)>100)
		{
			alert(KHPLAN_ERRORINFO4);
			document.getElementById(id).focus();
			return;
		}
	}	
}
	
function dafen_beforeSave()
{		
	if(examPlanForm.nohigh[0].checked==true)
		document.getElementById('fineMax').value=parseFloat(document.getElementById('bili1').value)/100;
	if(examPlanForm.nohigh[1].checked==true)
		document.getElementById('fineMax').value=document.getElementById('value1').value;
	if(theMethod =='1' && examPlanForm.nohigh[2].checked==true)
	{
		document.getElementById('fineMax').value='-1';
	}	
		
	if(examPlanForm.nolow[0].checked==true)
		document.getElementById('badlyMax').value=parseFloat(document.getElementById('bili2').value)/100;
	if(examPlanForm.nolow[1].checked==true)
		document.getElementById('badlyMax').value=document.getElementById('value2').value;
	if(theMethod =='1' && examPlanForm.nolow[2].checked==true)
	{
		document.getElementById('badlyMax').value='-1';
	}
		
	var onhigh=document.getElementById("fineRestrict").checked;
	if(onhigh==true && examPlanForm.nohigh[0].checked==false && examPlanForm.nohigh[1].checked==false && theMethod =='1' && examPlanForm.nohigh[2].checked==false)
		document.getElementById('fineMax').value='999';
	if(onhigh==true && examPlanForm.nohigh[0].checked==false && examPlanForm.nohigh[1].checked==false && theMethod =='2')
		document.getElementById('fineMax').value='999';
			
	var onlow=document.getElementById("badlyRestrict").checked;
	if(onlow==true && examPlanForm.nolow[0].checked==false && examPlanForm.nolow[1].checked==false  && theMethod =='1' && examPlanForm.nolow[2].checked==false)
		document.getElementById('badlyMax').value='999';
	if(onlow==true && examPlanForm.nolow[0].checked==false && examPlanForm.nolow[1].checked==false  && theMethod =='2')
		document.getElementById('badlyMax').value='999';	
		
	
//	if(theMethod =='2')
	{
		if(examPlanForm.noScoreObj[0].checked==true)
			document.getElementById('sameAllScoreNumLess').value=parseFloat(document.getElementById('biliObj').value)/100;
		if(examPlanForm.noScoreObj[1].checked==true)
			document.getElementById('sameAllScoreNumLess').value=document.getElementById('valueObj').value;
		if(document.getElementById("sameAllScoreNumLess").checked==false)
			document.getElementById('sameScoreNumLessValue').value="0";	
		else
			document.getElementById('sameScoreNumLessValue').value="1";			
	}	
				
}
function selNum()
{	
	var flag = document.getElementById('num').checked;
	if(flag==true)
	{		
		examPlanForm.scoreNumPerPage.disabled=false;
		document.getElementById('0_up').disabled=false;
		document.getElementById('0_down').disabled=false;					
	}
	else
	{	
		examPlanForm.scoreNumPerPage.disabled=true;
		document.getElementById('0_up').disabled=true;
		document.getElementById('0_down').disabled=true;	
	}		
}
function showWarnOpt(obj)
{	
	var flag = document.getElementById(obj).checked;
	if(flag==true)
	{		
		if(obj=='warnOpt1')
		{
			examPlanForm.delayTime1.disabled=false;
			document.getElementById('delayTime1_up').disabled=false;
			document.getElementById('delayTime1_down').disabled=false;
			document.getElementById('roleScope1Desc').disabled=false;
			document.getElementById('roleScope1image').disabled=false;
		}else
		{
			examPlanForm.delayTime2.disabled=false;
			document.getElementById('delayTime2_up').disabled=false;
			document.getElementById('delayTime2_down').disabled=false;
			document.getElementById('roleScope2Desc').disabled=false;
			document.getElementById('roleScope2image').disabled=false;
		}					
	}
	else
	{					
		if(obj=='warnOpt1')
		{
			examPlanForm.delayTime1.disabled=true;
			document.getElementById('delayTime1_up').disabled=true;
			document.getElementById('delayTime1_down').disabled=true;
			document.getElementById('roleScope1Desc').disabled=true;
			document.getElementById('roleScope1image').disabled=true;
		}else
		{
			examPlanForm.delayTime2.disabled=true;
			document.getElementById('delayTime2_up').disabled=true;
			document.getElementById('delayTime2_down').disabled=true;
			document.getElementById('roleScope2Desc').disabled=true;
			document.getElementById('roleScope2image').disabled=true;
		}
	}		
}
function showDetail()
{	
	if(document.getElementById("busitype")!=null && document.getElementById("busitype").value=="0")
	{	
		var jixiao = false;
		if(document.getElementById("performanceDate")!=null)
			jixiao = document.getElementById("performanceDate").checked;
			
		if(jixiao==true)
		{
			showElement('jxsub');
			document.getElementById("jxdata").style.display='';
		}	
		else
		{
			hideElement('jxsub');
			document.getElementById("jxdata").style.display='none';
		}
	}	
}
function bs_onLoad(perset)
{	
	if(examPlanForm.scoreNumPerPage.value!='0')
		document.getElementById('num').checked=true;	
	else if(examPlanForm.scoreNumPerPage.value=='0')
		examPlanForm.scoreNumPerPage.value='8';	
	selNum();
	
	if(document.getElementById("busitype")!=null && document.getElementById("busitype").value=="0")
	{
		/*
		if(perset=="")	
		{	
			document.getElementById("jixiao").checked=false;
		}
		else
			document.getElementById("jixiao").checked=true;
		*/	
		showDetail();	
	    
		var dispWay=document.getElementById("perSetShowMode").value;
		if(dispWay=='2')
		{
		   document.getElementById("detailItem").checked=false;
		   document.getElementById("sumItem").checked=true;
	    }
		else if(dispWay=='3')
		{
		    document.getElementById("detailItem").checked=true;
		    document.getElementById("sumItem").checked=true;
		}
		else if(dispWay=='1')
	    {
		    document.getElementById("detailItem").checked=true;
		    document.getElementById("sumItem").checked=false;
		}else if(dispWay=='0')
		{
		    document.getElementById("detailItem").checked=false;
		    document.getElementById("sumItem").checked=false;
		}   
		var param_start = document.getElementById("statStartDateSt").value;
	    var param_end = document.getElementById("statEndDateEn").value; 
	
		document.getElementById('editor2').value=replaceAll(param_end,'.','-');
		document.getElementById('editor1').value=replaceAll(param_start,'.','-');
		dispTime();	
	}
	   
	var isBrowse = document.getElementById('isBrowse').value;
	if(isBrowse!='1')
	   	hideElement('browse');
	   	
	if(document.getElementById("mutiScoreGradeCtl")!=null)
	{   	
		var theFlag = document.getElementById("mutiScoreGradeCtl").checked;
		if(theFlag == true)
		   	document.getElementById("checkGradeRange").disabled=false;
		else
		    document.getElementById("checkGradeRange").disabled=true;
	}
	
	var showTargetDesc = document.getElementById("showIndicatorDesc").checked;
	if(showTargetDesc == true)
	   	document.getElementById("diaoru").disabled=false;
	else
	    document.getElementById("diaoru").disabled=true;
	  
	if(document.getElementById("busitype")!=null && document.getElementById("busitype").value=="0") 
	{   
		setTargetCard($('noteIdioGoal'));
		setPerforReport($('idioSummary'));
	}
	setMustFillCause();	
	setMustWriteButton();	
	importFormula();  //加载页面时进行判断  pjf 2014.01.03
}
function bs_beforeSave()
{
	if(document.getElementById("busitype")!=null && document.getElementById("busitype").value=="0")    
	{
		var detailItem;
		if(document.getElementById("detailItem")!=null)
			detailItem = document.getElementById("detailItem").checked;
		var sumItem;
		if(document.getElementById("sumItem")!=null)
			sumItem = document.getElementById("sumItem").checked;
		if(detailItem==true && sumItem==true)
			document.getElementById("perSetShowMode").value='3';		
		else if(detailItem==false && sumItem==true)
			document.getElementById("perSetShowMode").value='2';
		else if(detailItem==true && sumItem==false)
			document.getElementById("perSetShowMode").value='1';
		else if(detailItem==false && sumItem==false)
			document.getElementById("perSetShowMode").value='0';			
		
		/*
		var jixiao = document.getElementById("jixiao").checked;						
		if(jixiao!=null && jixiao==false)
		{			
			document.examPlanForm.perSet.value='';
			document.examPlanForm.perSetShowMode.value='0';	
			//document.getElementById("perSet").value='';	
			//document.getElementById("perSetShowMode").value='0';
		}
		*/
	}
	var flag;
	if(document.getElementById("num")!=null)
		flag = document.getElementById('num').checked;
	if(flag==false)
		examPlanForm.scoreNumPerPage.value='0';	
}
function otherParam_onLoad()
{
	if($F('nodeKnowDegree')=="1")
	  showElement('know_span');
	else
	  hideElement('know_span');	    
	
}
function dispButton()
{
	var flag = document.getElementById('nodeKnowDegree').checked;
	if(flag==true)
		showElement('know_span');
    else
    	hideElement('know_span');
}
function messageButton()
{
	var flag = document.getElementById('showBasicInfo').checked;
	if(flag==true)
		showElement('messagetwo');
    else
    	hideElement('messagetwo');
}
function othermessageButton()
{
	if($F('showBasicInfo')=="1")
	  showElement('messagetwo');
	else
	  hideElement('messagetwo');	    
	
}
function showEvalClass()
{
	var flag1 = document.getElementById('wholeEval').checked;
	var flag2 = document.getElementById('descriptiveWholeEval').checked;
	
	if(flag1==true)
		showElement('eval');
    else
    	hideElement('eval');
    if(flag1==false&&flag2==false){
    	hideElement('showEval');
    }else{
    	showElement('showEval');
    }	
}
function showEvalClass2()
{
	var flag1 = document.getElementById('wholeEval').checked;
	var flag2 = document.getElementById('descriptiveWholeEval').checked;
	if(flag1==false&&flag2==false){
    	hideElement('showEval');
    }else{
    	showElement('showEval');
    }
}
function otherShowEvalClass()
{
     
	if($F('wholeEval')=="1")
	  showElement('eval');
	else
	  hideElement('eval');	    
	
}
function knowDegree()
{
	var target_url="/performance/options/perKnowList.do?b_query2=link";
 	var iframe_url="/general/query/common/iframe_query.jsp?src="+$URL.encode(target_url);

    if(window.showModalDialog){
        var return_vo= window.showModalDialog(iframe_url, "perKnow_win",
            "dialogWidth:500px; dialogHeight:420px;resizable:no;center:yes;scroll:no;status:no");
    }else{
        var config = {
            width:500,
            height:420,
            type:'2',
            id:'perKnow_win'
        };
        modalDialog.showModalDialogs(iframe_url,'perKnow_win',config);
    }
}
function message()
{   var BasicInfoItem=document.getElementById('basicInfoItem');
	var lockMGradeColumn=document.getElementById('lockMGradeColumn');
	var target_url="/performance/kh_plan/person_message.do?b_query=link`Item="+BasicInfoItem.value+"`lockMGradeColumn="+lockMGradeColumn.value+"`object_type="+theObjectType;
 	var iframe_url="/general/query/common/iframe_query.jsp?src="+$URL.encode(target_url);

    if(window.showModalDialog){
        var return_vo= window.showModalDialog(iframe_url, "perKnow_win",
            "dialogWidth:465px; dialogHeight:420px;resizable:no;center:yes;scroll:no;status:no");
        message_window_ok(return_vo);
    }else{
        var config = {
            width:465,
            height:420,
            type:'2',
            id:'perKnow_win'
        };
        modalDialog.showModalDialogs(iframe_url,'perKnow_win',config);
    }

}

function message_window_ok(return_vo) {
    if(!return_vo)
        return;
    else if(return_vo.flag=="true")
    {
        var BasicInfoItem=document.getElementById('basicInfoItem');
        var lockMGradeColumn=document.getElementById('lockMGradeColumn');
        BasicInfoItem.value = return_vo.degrees;
        lockMGradeColumn.value=return_vo.lockMGradeColumn;
    }
}
//强制分布考核主体类别控制
function mainbodyGradeCtl()
{
	var selectedStr='';//获得主体类别选中的考核主体类别
	for (var i=0;i<examPlanForm.bodyId.length;i++) 
	{
		if (examPlanForm.bodyId[i].checked)
		{
			var _str=examPlanForm.bodyId[i].value;
			selectedStr=selectedStr+_str+','; 
		}
	}
	var mainbodyGradeCtl = document.getElementById('mainbodybodyid');
	var allmainbodyGradeCtl = document.getElementById('allmainbodybody');
	var target_url="/performance/kh_plan/mainbodyGradeCtl.do?b_query=link`allmainbodyGradeCtl="+allmainbodyGradeCtl.value+"`mainbodyGradeCtl="+mainbodyGradeCtl.value+"`bodyids="+selectedStr+"`plan_id="+planId+"`object_type1="+theObjectType;
 	var iframe_url="/general/query/common/iframe_query.jsp?src="+$URL.encode(target_url);
 	var iTop = (window.screen.height-500)/2; //获得窗口的垂直位置;
    var iLeft = (window.screen.width-460)/2;  //获得窗口的水平位置;
    if(window.showModalDialog){
        var return_vo= window.showModalDialog(iframe_url, "perKnow_win",
            "dialogWidth:360px; dialogHeight:270px;resizable:no;center:yes;scroll:no;status:no");
        mainbodyGradeCtl_window_ok(return_vo);
    }else{
        var config = {
            width:360,
            height:270,
            type:'2'
        };
        modalDialog.showModalDialogs(iframe_url,'perKnow_win',config);
    }

}
function mainbodyGradeCtl_window_ok(return_vo) {
    if (!return_vo)
        return;
    else if (return_vo.flag == "true") {
        var mainbodyGradeCtl = document.getElementById('mainbodybodyid');
        var allmainbodyGradeCtl = document.getElementById('allmainbodybody');
        mainbodyGradeCtl.value = return_vo.bodyids;
        allmainbodyGradeCtl.value = return_vo.allbodyids;
    }
}

function bodyTypeDef()
{
    window.tempOjb = {};
    window.tempOjb.selectedStr='';
	for (var i=0;i<examPlanForm.bodyId.length;i++) 
	{
		if (examPlanForm.bodyId[i].checked)
		{
			var _str=examPlanForm.bodyId[i].value;
			 
			if(theMethod =='2')
			{
				var _id=examPlanForm.bodyId[i].value; 
				if(document.getElementById("grade_"+_id).checked)
				{
					_str=_str+"/0";
					_str=_str+"/"+trim(document.getElementById("seq_"+_id).value);
				}
				else
					_str=_str+"/1/";
			 }
            window.tempOjb.selectedStr=window.tempOjb.selectedStr+_str+',';
		}
	}
	var target_url="/performance/options/checkBodyObjectList.do?b_query2=link`bodyType=0`busitype="+document.getElementById("busitype").value;
 	var iframe_url="/general/query/common/iframe_query.jsp?src="+$URL.encode(target_url);
 	var iTop = (window.screen.height-500)/2; //获得窗口的垂直位置;
    var iLeft = (window.screen.width-460)/2;  //获得窗口的水平位置;
    if(window.showModalDialog){
        var return_vo= window.showModalDialog(iframe_url, "mainbody_win",
            "dialogWidth:600px; dialogHeight:450px;resizable:no;center:yes;scroll:no;status:no");
        bodyTypeDef_ok(return_vo);
    }else{
        var config = {
            width:600,
            height:450,
            type:'2',
            id:'mainbody_win'
        };
        modalDialog.showModalDialogs(iframe_url,'mainbody_win',config);
    }
}

function bodyTypeDef_ok(return_vo) {
    if(return_vo==null || return_vo.flag=="false")
        return;
    else if(return_vo.flag=="true")
    {
        document.examPlanForm.action="/performance/kh_plan/kh_params.do?b_mainbody=link&status="+theStatus+"&plan_id="+planId+"&method="+theMethod+"&gather_type="+theGatherType+"&object_type="+theObjectType+"&templateId="+theTemplateId+"&bodyids="+window.tempOjb.selectedStr;
        window.tempOjb = undefined;
        document.examPlanForm.submit();
    }


}


function selMult(theObj)
{	
	if(theObj.checked==true){
		showElement("checkGradeRange");
		showElement('MainbodyGradeCtlSpan');
		document.getElementById("checkGradeRange").disabled=false;
		document.getElementById("MainbodyGradeCtl").disabled=false;
	}
	else{
		hideElement('MainbodyGradeCtlSpan');
		hideElement("checkGradeRange");
	}
}
function selMult()
{	
	if(document.getElementById("mutiScoreGradeCtl")==null)
		return;
	if(document.getElementById("mutiScoreGradeCtl").checked==true){
		showElement("checkGradeRange");
		showElement('MainbodyGradeCtlSpan');
		if(theStatus!='0' && theStatus!='5'){
			document.getElementById("checkGradeRange").disabled=true;
			document.getElementById("MainbodyGradeCtl").disabled=false;
		} else{
			document.getElementById("checkGradeRange").disabled=false;
			document.getElementById("MainbodyGradeCtl").disabled=false;
		}
	}
	else{
		
		hideElement("checkGradeRange");
		hideElement('MainbodyGradeCtlSpan');
	}
}

function showTargetDesc(theObj)
{	
	if(theObj.checked==true){
		showElement("diaoru");
		document.getElementById("diaoru").disabled=false;
	}	
	else{
		hideElement("diaoru");
		
	}	
}

function showTargetDesc()
{	
	if(document.getElementById("showIndicatorDesc")==null)
		return;
	if(document.getElementById("showIndicatorDesc").checked==true){
		showElement("diaoru");
		document.getElementById("diaoru").disabled=false;
	}	
	else{
		hideElement("diaoru");
		
	}	
}
function mbgl_onload()
{	
	var isBrowse = document.getElementById('isBrowse').value;
	if(isBrowse!='1')
	   	hideElement('browse');
	if($F('dataGatherMode')=="2")
		 showElement('datepnl');
	else
		 hideElement('datepnl');
	    
	if($F('dataGatherMode')=="1")
		 showElement('datepn2');
	else
		 hideElement('datepn2');	
	
	if($F('dataGatherMode')=="4")
		 showElement('datepn3');
	else
		 hideElement('datepn3'); 
		 
//	if(theObjectType=='1')//考核对象为人员 打分途径置灰不可选	
//	{
//		document.getElementById("scoreWay").value='1';
//		document.getElementById("scoreWay").disabled=true;
//	}
	var targetTraceEnabled = document.getElementById('targetTraceEnabled');
	if(targetTraceEnabled!=null && !targetTraceEnabled.checked)
		//document.getElementById("defineIndex_bt").style.display='none';
		hideElement('defineIndex_span');
    var dutyRuleid = document.getElementById('dutyRuleid');
	if(dutyRuleid!=null && !dutyRuleid.checked)
		//document.getElementById("defineIndex_bt").style.display='none';
		hideElement('dutyRuleidSpan');

	if(theGatherType=='1')
	{
	 	document.getElementById("gather_type_1").style.display='none';
	 	//document.getElementById("gather_type_2").style.display='none';
	}

	if($F('blankScoreOption')=="2")
		 document.getElementById("blankScoreUseDegree").disabled=false;
	else
	{
		 document.getElementById("blankScoreUseDegree").disabled=true;
		 document.getElementById("blankScoreUseDegree").value='A';
	}	
	setMustFillCause();
	setMustWriteButton();
	showWarnOpt('warnOpt1');
	showWarnOpt('warnOpt2');
	changeAppMode();
	selectSpByBodySeq();
	otherShowEvalClass();
}
function setMustFillCause()
{
	if(document.getElementById("showDeductionCause").checked==true){
		 document.getElementById("mustFillCause").disabled=false;
		 showElement('ScoreIntroductuios')
    }
	else
	{
		document.getElementById("mustFillCause").disabled=true;	
		document.getElementById("mustFillCause").checked=false;	
		hideElement('mustWriteButton');
		hideElement('ScoreIntroductuios')
	}
}
function importFormula()
{
	if(document.getElementById("showTotalScoreSort").checked==true){
		 document.getElementById("batchScoreImportFormula").disabled=false;
		 showElement('showImportFormula');
    }
	else
	{
		document.getElementById("batchScoreImportFormula").disabled=true;	
		document.getElementById("batchScoreImportFormula").checked=false;	
		hideElement('showImportFormula');
	}
}
// //选择员工日志类型
// function selectEmpRecordType(showdayweekmonth)
// {
// 	var showEmployeeRecord = document.getElementById("showEmployeeRecord");
// 	if(showEmployeeRecord !=null) {
// 		if(document.getElementById("showEmployeeRecord").checked==true){
// 			 document.getElementById("ShowDay").disabled=false;
// 			 document.getElementById("ShowWeek").disabled=false;
// 			 document.getElementById("ShowMonth").disabled=false;
// 			 document.getElementById("ShowDay").checked=true;
// 			 document.getElementById("ShowWeek").checked=true;
// 			 document.getElementById("ShowMonth").checked=true;
// 			 showElement('RecordIntroductuios')
// 	   }
// 		else
// 		{
// 			document.getElementById("ShowDay").disabled=true;
// 			document.getElementById("ShowDay").checked=false;
// 			document.getElementById("ShowWeek").disabled=true;
// 			document.getElementById("ShowWeek").checked=false;
// 			document.getElementById("ShowMonth").disabled=true;
// 			document.getElementById("ShowMonth").checked=false;
// 			hideElement('RecordIntroductuios')
// 		}
// 	}
// }

function selectEmpRecord(showdayweekmonth)
{
	var showEmployeeRecord = document.getElementById("showEmployeeRecord");
	if(showEmployeeRecord !=null) {
		if(document.getElementById("showEmployeeRecord").checked==true){
			//2013.11.13 pjf begin
			if(theStatus=='0' || theStatus=='5'){ //起草或打分状态才可编辑
				 document.getElementById("ShowDay").disabled=false;
				 document.getElementById("ShowWeek").disabled=false;
				 document.getElementById("ShowMonth").disabled=false;
			} else{
				 document.getElementById("ShowDay").disabled=true;
				 document.getElementById("ShowWeek").disabled=true;
				 document.getElementById("ShowMonth").disabled=true;
			}
			//2013.11.13 pjf end
			 if(showdayweekmonth=="True") {
				 document.getElementById("ShowDay").checked=true;
				 document.getElementById("ShowWeek").checked=true;
				 document.getElementById("ShowMonth").checked=true;
			 }
			 showElement('RecordIntroductuios')
	   }
		else
		{
			document.getElementById("ShowDay").disabled=true;	
			document.getElementById("ShowDay").checked=false;	
			document.getElementById("ShowWeek").disabled=true;	
			document.getElementById("ShowWeek").checked=false;	
			document.getElementById("ShowMonth").disabled=true;	
			document.getElementById("ShowMonth").checked=false;	
			hideElement('RecordIntroductuios')
		}
	}
}

function setMustWriteButton()
{
	if(document.getElementById("mustFillCause").checked==true)
		showElement('mustWriteButton');
	else			
		hideElement('mustWriteButton');					
}
function setTargetTraceEnabled(theObj)
{
	if(theObj!=null)
	{
		if(theObj.checked==false)    
			//document.getElementById("defineIndex_bt").style.display='none';	
			hideElement('defineIndex_span'); 
		else
			//document.getElementById("defineIndex_bt").style.display='block';
			showElement('defineIndex_span'); 
	}	
}
/**JinChunhai begin*/
function setAllowResult()
{	
	if(document.getElementById("busitype")!=null && document.getElementById("busitype").value=="0")
	{
		if(document.getElementById("allowAdjustEvalResult")!=null && document.getElementById("allowAdjustEvalResult").checked==true)
		{
			if(document.getElementById("adjustEvalRange")!=null)
				document.getElementById("adjustEvalRange").disabled=false;	
			if(document.getElementById("adjustEvalRange1")!=null)		
				document.getElementById("adjustEvalRange1").disabled=false;				
			document.getElementById("adjustEvalDegreeType").disabled=false;
			document.getElementById("adjustEvalDegreeType1").disabled=false;
			document.getElementById("adjustEvalDegreeNum").disabled=false;
			document.getElementById("adjustEvalDegreeNums").disabled=false;
		}else{
			if(document.getElementById("adjustEvalRange")!=null)
				document.getElementById("adjustEvalRange").disabled=true;
			if(document.getElementById("adjustEvalRange1")!=null)
				document.getElementById("adjustEvalRange1").disabled=true;			
			document.getElementById("adjustEvalDegreeType").disabled=true;
			document.getElementById("adjustEvalDegreeType1").disabled=true;
			document.getElementById("adjustEvalDegreeNum").disabled=true;
			document.getElementById("adjustEvalDegreeNums").disabled=true;
		}
	}	
}
function setResult()
{
	if(document.getElementById("busitype")!=null && document.getElementById("busitype").value=="0")
	{
		if(document.getElementById("dataGatherModes").checked==false)
		{		
			document.getElementById("allowAdjustEvalResults").disabled=false;
			document.getElementById("adjustEvals").disabled=false;
			document.getElementById("calcMenScoreRefDepts").disabled=false;
			if(theMethod=="2")	
				document.getElementById("showGrpOrders").disabled=false;			
		}
		else
		{
			document.getElementById("allowAdjustEvalResults").disabled=true;			
			document.getElementById("adjustEvals").disabled=true;	
			document.getElementById("calcMenScoreRefDepts").disabled=true;	
			if(theMethod=="2")
				document.getElementById("showGrpOrders").disabled=true;		
		}	
	}
}
function setHide()
{
	if(document.getElementById("busitype")!=null && document.getElementById("busitype").value=="0")
	{
		if(theGatherType != '1')
		{
			if(document.getElementById("dataGatherModes").checked==false)
			{			
				//showElement('pgxx');	
				showElement('keyEventCanNewPoint');
				showElement('allowAdjustEvalResults');
				if(theObjectType == '2')
				{
					showElement('calcMenScoreRefDepts');
				}else{
					hideElement('calcMenScoreRefDepts');		
				}			
				showElement('scoreFromGrpOrders');
			}				
			else
			{
				//hideElement('pgxx');
				hideElement('allowAdjustEvalResults');
				hideElement('adjustEvals');
				hideElement('calcMenScoreRefDepts');
				hideElement('scoreFromGrpOrders');
			}		
		}else
			hideElement('pgxx');
	}					
}
function showObjsFromCard()
{
	if(document.getElementById("bodysFromCard").checked==true)
	{		
		if(document.getElementById("objsFromCard").checked=false)
			document.getElementById("objsFromCard").checked=true;
		else
			document.getElementById("objsFromCard").checked=true;					
	}
}
//控制总体评价计算公式    pjf   2013.01.07
function changewholeEvalMode(){
	if(document.examPlanForm.wholeEvalMode.value==1)
	{
		hideElement('wholeEvalFormula');
	}else{
		showElement('wholeEvalFormula');
	}
}
//控制描述性评议项
function changePerformanceType(){
	if(document.examPlanForm.performanceType&&document.examPlanForm.performanceType.value==1)
	{
		showElement('probutton');
	}else{
		hideElement('probutton');
	}
}
function changeParams()
{	
	if(document.examPlanForm.readerType.value==1)
	{
		hideElement('bodysFromCards');
		hideElement('objsFromCards');
	}else{
		showElement('bodysFromCards');
		showElement('objsFromCards');
	}
}
function changeAppMode()
{	
	if(document.examPlanForm.targetAppMode.value==0)
	{	
		showElement('mainbodySpByBodySeq');
	}else
	{
		hideElement('mainbodySpByBodySeq');
		document.getElementById("targetMakeSeries").disabled=false;
		document.getElementById("spByBodySeq").checked=false;
	}	
}
function selectSpByBodySeq()
{
	if(document.getElementById("spByBodySeq").checked)	
		document.getElementById("targetMakeSeries").disabled=true;	
	else	
		document.getElementById("targetMakeSeries").disabled=false;	
}
function showORhide()
{	
	if(document.getElementById("dataGatherMode").checked==true)
	{
		showElement('pointEvalTypes');		
	}else
	{
		hideElement('pointEvalTypes');
	}
}

function setBodysFromCard()
{
	if(document.getElementById("bodysFromCard").checked==false)
	{		
		document.getElementById("select").disabled=false;		
	}
	else
	{
		document.getElementById("select").disabled=true;
		document.getElementById("objsFromCard").checked=true;					
	}	
}
function mincreasep(obj_name)
{
  	var objs = document.getElementsByName(obj_name);
  	if(objs==null)
  		return false;
  	var obj=objs[0];
  	if(parseInt(obj.value)>=0&&parseInt(obj.value)<30)
  	{
		obj.value = (parseInt(obj.value)+1).toFixed(0);
	}
}
function msubtractp(obj_name)
{
  	var objs = document.getElementsByName(obj_name);	
  	if(objs==null)
  		return false;
  	var obj=objs[0];
  	if(parseInt(obj.value)>0&&parseInt(obj.value)<=30)
  	{
		obj.value = (parseInt(obj.value)-1).toFixed(0);
	}
}
function checkEval()
{
	if(document.getElementById("busitype")!=null && document.getElementById("busitype").value=="0")
	{
		if(document.getElementById("dataGatherMode").checked==true && (document.getElementById("adjustEvalRange")!=null && document.getElementById("adjustEvalRange").checked==true))
		{
			showElement('adjustEvals');
		}
		else
		{
			hideElement('adjustEvals');				
		}
	}
}
function checkType()
{
	if(document.getElementById("busitype")!=null && document.getElementById("busitype").value=="0")
	{
		if(document.getElementById("adjustEvalDegreeType1").checked==true)
		{
			showElement('adjustEvalGradeStep');
		}
		else
		{
			hideElement('adjustEvalGradeStep');				
		}
	}
}
function IsDigitme(obj) 
{
	if((((event.keyCode >47) && (event.keyCode <= 57)) || (event.keyCode == 46)))
		return true;
	else
		return false;	
}
function keydown()   
{ 
    if((event.keyCode <48||event.keyCode> 57)||event.shiftKey == true)
    { 
    	if(event.keyCode != 190||event.shiftKey == true)   
        { 
        	event.keyCode = 0; 
             window.event.returnValue = false; 
        }else{ 
        	for(var i=0; i <adjustEvalGradeStep.value.length; i++)
        	{ 
            	if(adjustEvalGradeStep.value.substring(i,i+1) == ".")   
            	{ 
                	event.keyCode = 0; 
                     window.event.returnValue = false;                  
                } 
            } 
        } 
    }   
} 
window.document.onkeydown=keydown;

/** end  */
function testNum(obj)
{
	if(obj.value!='' && !isNum(obj.value))
	{ 
		alert(KHPLAN_ERRORINFO5);
		obj.value='8';
		obj.focus();
	}
	else
	{
		if(parseInt(obj.value)>100)
			obj.value='100';
		else if(parseInt(obj.value)<1)
			obj.value='1';
	}
}
function mincrease(obj_name,theMax) 
{
      var objs =document.getElementsByName(obj_name);      
  	  if(objs==null)
  		 return false;
  	  var obj=objs[0];
  	  if(parseInt(obj.value)<theMax)
		obj.value = (parseInt(obj.value)+1)+'';
}
function msubtract(obj_name,theMin) 
{
      var objs =document.getElementsByName(obj_name);      
  	  if(objs==null)
  		 return false;
  	  var obj=objs[0];
  	  if(parseInt(obj.value)>theMin)
		obj.value = (parseInt(obj.value)-1)+'';
}
function IsDigit2(obj) 
{
	if((event.keyCode >47) && (event.keyCode <= 57))
		return true;
	else
		return false;	
}
function setTargetCard(obj)
{
	if(document.getElementById("busitype")!=null && document.getElementById("busitype").value=="0")
	{
		if(obj.checked==true){
			showElement("show_target");
			document.getElementById('relatingTargetCard').disabled=false;
		}
		else
		{
			hideElement("show_target");
			document.getElementById('relatingTargetCard').disabled=true;
		//	document.getElementById('relatingTargetCard').checked=false;
		}
	}
}
function setPerforReport(obj)
{
	if(document.getElementById("busitype")!=null && document.getElementById("busitype").value=="0")
	{
		if(obj.checked==true){
		    showElement("jx.show");
		    showElement("UploadFile.show");
		    showElement("affixUploadFile");
			document.getElementById('scoreBySumup').disabled=false;
		}
		else
		{
			hideElement("jx.show");
			hideElement("UploadFile.show");
		    hideElement("affixUploadFile");
			document.getElementById('scoreBySumup').disabled=true;
			document.getElementById('scoreBySumup').checked=false;
		}
	}
}
function setPerforReport1()
{		
	if(document.getElementById("idioSummary")!=null){
		if(document.getElementById("idioSummary").checked==true){
		    showElement("UploadFile.show");
		    showElement("affixUploadFile");
		    if(document.getElementById("scoreBySumup")!=null)
		    document.getElementById("scoreBySumup").disabled=false;
		}
		else
		{
			hideElement("UploadFile.show");
			hideElement("affixUploadFile");
		    if(document.getElementById("scoreBySumup")!=null){
		    	document.getElementById("scoreBySumup").checked=false;
		    	document.getElementById("scoreBySumup").disabled=true;
		    }

		}
	}

}
function showBackTableSet()
{	
	if(theTemplateId=='isNull')
	{
		alert(KH_TEMPLATE_SET);
		return;
	}
	
	var cards=document.getElementById('showBackTables').value;
	var target_url="/performance/kh_plan/kh_params.do?b_setBackTable=link`cards="+cards+'`plan_id='+planId+'`templateId='+theTemplateId+'`planStatus='+theStatus;
 	var iframe_url="/general/query/common/iframe_query.jsp?src="+$URL.encode(target_url);
    if(window.showModalDialog){
        var return_vo= window.showModalDialog(iframe_url, "",
            "dialogWidth:425px; dialogHeight:380px;resizable:no;center:yes;scroll:no;status:no");
        showBackTableSet_window_ok(return_vo);
    }else{
        var config = {
            width:425,
            height:380,
            type:'2'
        };
        modalDialog.showModalDialogs(iframe_url,'',config);
    }

}
function showBackTableSet_window_ok(return_vo) {
    if(!return_vo)
        return;
    else if(return_vo.flag=="true")
    {
        document.getElementById('showBackTables').value = return_vo.cardIds;
    }
}

// 启用评分说明 必填设置
function mustWriteScore()
{
	var upIsValid = document.getElementById("upIsValid");
	var downIsValid = document.getElementById("downIsValid");	
	var upDegreeId = document.getElementById("upDegreeId");
	var downDegreeId = document.getElementById("downDegreeId");		
	var excludeDegree = document.getElementById("excludeDegree");		
	var requiredFieldStr = document.getElementById("requiredFieldStr");		
	var method = document.getElementById("planMethod");		
	
	var target_url ="/performance/kh_plan/kh_params.do?b_mustWrite=link`plan_id="+planId+"`upIsValid="+upIsValid.value+"`downIsValid="+downIsValid.value+"`upDegreeId="+upDegreeId.value+"`downDegreeId="+downDegreeId.value+"`status="+theStatus;
	target_url += "`method=" + method.value; // 计划类别 add by 刘蒙
	target_url += "`tplId="+document.getElementById("planTpl").value; // 计划模板id - 必填指标用 add by 刘蒙
	var dialogwidth = "450";
	var dialogHeight = "290";
	if (method.value === "1") {
		target_url+="`excludeDegree="+excludeDegree.value; // 无需评分说明   add by 刘蒙
		target_url+="`requiredFieldStr="+requiredFieldStr.value; // 必填指标   add by 刘蒙
		dialogwidth = "530";
		dialogHeight = "550";
	}
	var iframe_url="/general/query/common/iframe_query.jsp?src="+$URL.encode(target_url);
    var config = {
        width:dialogwidth,
        height:dialogHeight,
        type:'2'
    };
    modalDialog.showModalDialogs(iframe_url,'',config,mustWriteScore_ok);
}

function mustWriteScore_ok(return_vo) {
    if(!return_vo)
        return;
    else if(return_vo.flag=="true")
    {
        var method = document.getElementById("planMethod");
        //IE 下需要重新获取下下面这几个元素对象。否则汇报未定义的错误 haosl 2019-6-24
        var upIsValid = document.getElementById("upIsValid");
        var downIsValid = document.getElementById("downIsValid");
        var upDegreeId = document.getElementById("upDegreeId");
        var downDegreeId = document.getElementById("downDegreeId");
        var excludeDegree = document.getElementById("excludeDegree");
        var requiredFieldStr = document.getElementById("requiredFieldStr");
        upIsValid.value = return_vo.upIsValid;
        downIsValid.value = return_vo.downIsValid;
        upDegreeId.value = return_vo.upDegreeId;
        downDegreeId.value = return_vo.downDegreeId;
        if (method.value === "1") {
            excludeDegree.value = return_vo.excludeDegree;
            requiredFieldStr.value = return_vo.requiredFieldStr;
        }
    }
}

// 选择预警角色
function selectRoles(obj)
{
	var select_id="";
	if(obj=="roleScope1") //角色 
		select_id = document.getElementById("roleScope1").value;
	else
		select_id = document.getElementById("roleScope2").value;	
	if(select_id!=null && select_id.length>0 && select_id.indexOf("RL")==-1)
    {
		var matters = select_id.split(",");
	    var roleName = "";
	    for (var j = 0; j < matters.length; j++)
	    {
	    	roleName += ",RL"+matters[j];			    				
	    }
	    select_id = roleName.toString().substring(1);
    }	
					
	var t_url="/system/warn/config_maintenance.do?br_domain=link&role_id="+select_id;	   


    if(window.showModalDialog){
        var return_vo= window.showModalDialog(t_url,'rr',
            "dialogWidth:340px; dialogHeight:400px;resizable:no;center:yes;scroll:yes;status:no;scrollbars:yes");
        selectRoles_window_ok(return_vo,obj);
    }else{
        var win = Ext.create('Ext.window.Window',{
            id:'select_role_emp',
            title:'选择角色',
            width:340,
            height:420,
            resizable:'no',
            modal:true,
            autoScoll:false,
            autoShow:true,
            autoDestroy:true,
            html:'<iframe style="background-color:#fff" frameborder="0" SCROLLING=NO height="100%" width="100%" src="'+t_url+'"></iframe>',
            renderTo:Ext.getBody(),
            listeners:{
                'close':function(){
                    if(this.return_vo && this.return_vo.flag=="true")
                    {
                        selectRoles_window_ok(this.return_vo,obj);
                    }
                }
            }
        });
    }
}
function selectRoles_window_ok(return_vo,obj) {
    if(!return_vo)
        return false;
    else
    {
        if(return_vo.flag=="true")
        {
            if(obj=="roleScope1")
            {
                document.getElementById("roleScope1").value = return_vo.content.substring(0,return_vo.content.length-1);
                document.getElementById("roleScope1Desc").value = return_vo.title;
            }else
            {
                document.getElementById("roleScope2").value = return_vo.content.substring(0,return_vo.content.length-1);
                document.getElementById("roleScope2Desc").value = return_vo.title;
            }
        }
    }
}

function calformula(templateId,status){
   var totalAppFormula = document.getElementById("totalAppFormula").value;
   var t_url="/performance/kh_plan/kh_params.do?b_calformula=link`templateId="+templateId+"`status="+status;	   
   var iframe_url="/general/query/common/iframe_query.jsp?src="+$URL.encode(t_url);
   var iTop = (window.screen.height-500)/2; //获得窗口的垂直位置;
   var iLeft = (window.screen.width-460)/2;  //获得窗口的水平位置;
   var arguments = totalAppFormula;
    totalAppFormula_arguments=totalAppFormula;
    var width = 650;
    //非兼容模式增加宽度
    if( !isCompatibleIE()){
        width = 710;
    }
    if(window.showModalDialog){
        var return_vo= window.showModalDialog(iframe_url, arguments,
            "dialogWidth:"+width+"px; dialogHeight:460px;resizable:no;center:yes;scroll:no;status:no");
        calformula_window_ok(return_vo);
    }else{
        var config = {
            width:width,
            height:460,
            type:'2'
        };
        modalDialog.showModalDialogs(iframe_url,'',config);
    }
}
function calformula_window_ok(return_vo) {
    if(return_vo!=null){
        document.getElementById("totalAppFormula").value = return_vo;
    }
}
function proAppraise()
{
	var target_url="/performance/kh_plan/proAppraise.do?b_query=link";
 	var iframe_url="/general/query/common/iframe_query.jsp?src="+$URL.encode(target_url);

    if(window.showModalDialog){
        var return_vo= window.showModalDialog(iframe_url, "proAppraise",
            "dialogWidth:600px; dialogHeight:500px;resizable:no;center:yes;scroll:no;status:no");
    }else{
        var config = {
            width:600,
            height:500,
            type:'2',
            id:'proAppraise'
        };
        modalDialog.showModalDialogs(iframe_url,'proAppraise',config);

    }
}
function showRadioSpan()
{
    
	var value = eval("examPlanForm.pointEvalType").value;
    	
	if(value=="1"){
		showElement('radiospan');
		showElement('mutiScoreOnePageOnePoint');
	}
    else{
    	hideElement('radiospan');
    	hideElement('mutiScoreOnePageOnePoint');
    }	
}
//让 "显示已自评目标卡" 这个选项出现
function showYP()
{
	var obj=document.getElementById("ypShow");
	if(obj!=null)
	{
		obj.style.display="";
	}
}
//让 "显示已自评目标卡" 这个选项隐藏
function hideYP()
{
	var obj=document.getElementById("ypShow");
	if(obj!=null)
	{
		obj.style.display="none";
	}
}
var modelstatus = "";
function getStatus(template_id){
	 	var hashvo=new ParameterSet();
		hashvo.setValue("template_id",template_id);
  			var request=new Request({asynchronous:false,onSuccess:getStatus_ok,functionId:'9022000293'},hashvo);	
}
function getStatus_ok(outparameters){
	modelstatus=outparameters.getValue("status");
}
function _changeModel(){
		var tempobj=document.getElementById("byModel");
		if(theTemplateId=="isNull"&&tempobj.checked){
			alert("设置“按岗位素质模型测评”参数需使用权重模板，请选择权重模板！");
			tempobj.checked=false;
			return;
		}else{
			getStatus(theTemplateId);
			if(modelstatus=="0"&&tempobj.checked){
				alert("设置“按岗位素质模型测评”参数需使用权重模板，请选择权重模板！");
				tempobj.checked=false;
				return;
			}
		}
}
///点击  “按岗位素质模型测评” 这个复选框
function changeModel()
		{
			var tempobj=document.getElementById("byModel");
			if(theGatherType=='1'){  //机读就不用考虑自助参数选项卡  zzk 2014/1/22
				if(tempobj!=null){
					if((document.getElementById("busitype")!=null && document.getElementById("busitype").value=="1")&&tempobj.checked){
						document.getElementById("jiduParams").style.display="none";
					}else{
						document.getElementById("jiduParams").style.display="block";
					}
				}
				return;
			}
			if(tempobj!=null)
			{
				if((document.getElementById("busitype")!=null && document.getElementById("busitype").value=="1")||tempobj.checked)
				{
					///如果勾选“按岗位素质模型测评”，那么就不要显示多人考评四个字
					var bymodel1=document.getElementById("bymodel1");
					bymodel1.style.display="none";
					var bymodel3=document.getElementById("bymodel3");
					bymodel3.style.display="none";
					var bymodel2=document.getElementById("bymodel2");
					bymodel2.style.display="";
					var bymodel4=document.getElementById("bymodel4");
					bymodel4.style.display="";
				
					///让多人考评隐藏
					var mulevalObj=document.getElementById("muleval");
					if(mulevalObj!=null)
					{
						mulevalObj.style.display="none";
					}
					var mulevalFieldsetObj=document.getElementById("mulevalFieldset");
					if(mulevalFieldsetObj!=null)
					{
						mulevalFieldsetObj.style.display="none";
					}
					///如果没有被选中的，就默认让单人考评选中
					var isSelected=0;//是否有选中的
					var singleObj=document.getElementById("singleRadio");
					if(singleObj!=null)
					{
						if(singleObj.checked)
						{
							isSelected="1";
						}
					}
					if(isSelected==0)
					{
						var nullObj=document.getElementById("nullRadio");
						if(nullObj!=null)
						{
							if(nullObj.checked)
							{
								isSelected="1";
							}
						}
					}
					if(isSelected==0)
					{
						var singleRadioObj=document.getElementById("singleRadio");
						if(singleRadioObj!=null)
						{
							singleRadioObj.checked="true";
							
						}
					}
				}
				else///如果没有被选中
				{
					///如果没有勾选“按岗位素质模型测评”，那么就要显示多人考评四个字
				 	var bymodel2=document.getElementById("bymodel2");
				 	bymodel2.style.display="none";
					var bymodel4=document.getElementById("bymodel4");
					bymodel4.style.display="none";
					var bymodel1=document.getElementById("bymodel1");
				 	bymodel1.style.display="";
					var bymodel3=document.getElementById("bymodel3");
					bymodel3.style.display="";
					
					var mulevalObj=document.getElementById("muleval");
					var mulevalFieldsetObj=document.getElementById("mulevalFieldset");
					if(mulevalObj!=null)
					{
						mulevalObj.style.display="";
					}
					if(mulevalFieldsetObj!=null)
					{
						mulevalFieldsetObj.style.display="";
					}
					
				}
			}
		}


