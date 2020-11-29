function autoSave(theObj,object_id)
{
	var hashvo=new ParameterSet();
	hashvo.setValue("planid",evaluationForm.planid.value);
	hashvo.setValue("opt",'29');
	hashvo.setValue("theValue",getEncodeStr(theObj.value));
	hashvo.setValue("object_id",object_id);
	var request=new Request({method:'post',asynchronous:false,functionId:'9023000003'},hashvo);	
}
//绩效面谈
function perInterview(interViewType)
{
	/*
	var strurl="/performance/interview/search_interview_list.do?b_init=init&opt=1&plan_id="+evaluationForm.planid.value+'&type=1&khObjWhere='+getEncodeStr(evaluationForm.khObjWhere.value);
	var iframe_url="/general/query/common/iframe_query.jsp?src="+strurl; 
	//var ss=window.showModalDialog(iframe_url,arguments,"dialogWidth=700px;dialogHeight=500px;resizable=yes;scroll=no;status=no;");  
	window.open(strurl,"_ad","top=0,left=5,height="+(window.screen.height-70)+",width="+(window.screen.width-20));
	*/
	if(selectObjectId==undefined)
	{
		alert(INPUT_VALIDATE_INFO11);
		return;
	}

	var str="";
	if(interViewType=='1')//文字方式
 		 str="/performance/interview/search_interview_list.do?b_edit=edit`id=-1`plan_id="+evaluationForm.planid.value+"`objectid="+selectObjectId+"`isClose=0`body=1`oper=0";
 	else if(interViewType=='0')//模板方式
 		str="/performance/kh_result/kh_result_interview.do?b_interview=link`object_id="+selectObjectId+"`planid="+evaluationForm.planid.value;
  	var iframe_url="/general/query/common/iframe_query.jsp?src="+$URL.encode(str);
  	//var values= window.showModalDialog(iframe_url,window, 
	//	        "dialogWidth:650px; dialogHeight:570px;resizable:yes;center:yes;scroll:no;status:no");	
  	var config = {
	    width:650,
	    height:570,
	    type:'1',
	    id:'perInterview_win'
	}

	modalDialog.showModalDialogs(iframe_url,"perInterview_win",config,"");
}
//评估表结构设置
function setTableStructure()
{
	var target_url="/performance/evaluation/performanceEvaluation.do?br_setTableStructure=link";
 	var iframe_url="/general/query/common/iframe_query.jsp?src="+target_url;
 	/*var return_vo= window.showModalDialog(iframe_url, "", 
			"dialogWidth:650px; dialogHeight:420px;resizable:no;center:yes;scroll:no;status:no");	*/
  	
//	var return_vo= window.showModalDialog(iframe_url, "", 
//			"dialogWidth:650px; dialogHeight:420px;resizable:yes;center:yes;scroll:yes;status:yes;minimize:yes;maximize:yes;");	
 	
    var config = {
        width:650,
        height:460,
        type:'1',
        id:'setTableStructure_win',
        title:'评估表结构设置'
    }
    
    modalDialog.showModalDialogs(iframe_url,"setTableStructure_win",config,setTableStructure_ok); 
    
}

function setTableStructure_ok(thevo) {
	if(thevo=="ok") {
		//此处暂时做延时处理，修改表结构（字段修改）如果没有完成，便刷新页面，就会产生要查的字段还没有生成，从而后台报字段无效的错误  haosl 2018-9-1
		setTimeout(function(){
			document.evaluationForm.action="/performance/evaluation/performanceEvaluation.do?b_query=query&noEditResult=true&operate=init&code="+document.evaluationForm.code.value;
			document.evaluationForm.submit();
		},2000);
	} 
}
/**
 * 导出打分明细
 */
function exportScoreDetails(){
	var planId = document.evaluationForm.planid.value;
	var hashvo=new ParameterSet();
	hashvo.setValue("planId",planId);
	var request=new Request({method:'post',asynchronous:false,onSuccess:function (outparamters)
	{
		var filename=outparamters.getValue("filename");
		window.location.target="_blank";
		//xus 20/4/30 vfs改造
		window.location.href = "/servlet/vfsservlet?fromjavafolder=true&fileid="+filename;
	},functionId:'9028000630'},hashvo);
	
}

//用于鼠标触发的某一行
var curObjTr= null;
var oldObjTr_c= "";
function tr_onclick_self(objTr,bgcolor)
{
	if(curObjTr!=null)
	{
		curObjTr.style.backgroundColor='#FFFFFF'; //oldObjTr_c;
		for(var j=0;j<curObjTr.cells.length;j++){
			curObjTr.cells[j].style.backgroundColor='#FFFFFF'; //oldObjTr_c;
		}
//		curObjTr.cells[0].style.background='#FFFFFF'; //oldObjTr_c;
//		curObjTr.cells[1].style.background='#FFFFFF'; //oldObjTr_c;
//		curObjTr.cells[2].style.background='#FFFFFF'; //oldObjTr_c;
//		curObjTr.cells[3].style.background='#FFFFFF'; //oldObjTr_c;
//		curObjTr.cells[4].style.background='#FFFFFF'; //oldObjTr_c;
//		curObjTr.cells[5].style.background='#FFFFFF'; //oldObjTr_c;
	}
	ori_obj=objTr;
	curObjTr=objTr;
	oldObjTr_c='none';
	curObjTr.style.backgroundColor='#FFF8D2';
	for(var i=0;i<objTr.cells.length;i++){
		objTr.cells[i].style.backgroundColor='#FFF8D2';
	}
//	objTr.cells[0].style.background='FFF8D2';
//	objTr.cells[1].style.background='FFF8D2';
//	objTr.cells[2].style.background='FFF8D2';
//	objTr.cells[3].style.background='FFF8D2';
//	objTr.cells[4].style.background='FFF8D2';
//	objTr.cells[5].style.background='FFF8D2';
	selectObjectId=objTr.id;
	selectObjectId_extra=objTr.getAttribute("id_s");
	
}




//自动保存录入分值
function autoValue(obj, EvalOutLimitStdScore)
{
	if(obj.value.length>0)
  	{	
  		if(!checkIsNum2(obj.value))
  		{
  			obj.value='';
  			obj.focus();
  			alert(INPUT_NUMBER_VALUE+"!");
  			return;
  		}
  		if(EvalOutLimitStdScore == 'False'){
		  	var temps=obj.name.split("/");
		  	if((temps[3]*1>0 && (obj.value>temps[3]*1 || obj.value*1<0)) || (temps[3]*1<0 && (obj.value*1<temps[3]*1 || obj.value*1>0)))
		  	{
		  		alert(INPUT_VALIDATE_INFO1);
		  		obj.value='';
		  		obj.focus();
		  		return;
		  	}
  		}
	  	var hashvo=new ParameterSet();
		hashvo.setValue("value",obj.value);
		hashvo.setValue("nameDesc",obj.name);
		var request=new Request({method:'post',asynchronous:false,onSuccess:reSave,functionId:'9024000015'},hashvo);
	}
}

function reSave(outparamters)
{
	
}
	function testRange(object_id,plan_id,pointid,maxScore,obj, EvalOutLimitStdScore)
	{	
		if(obj.value.length>0)
  		{	
  			if(!checkIsNum2(obj.value))
  			{
  				obj.value='0';
  				obj.focus();
  				alert(INPUT_NUMBER_VALUE+"!");
  				return;
  			}
  			if(EvalOutLimitStdScore == 'False'){
		  		if((maxScore*1>0 && (obj.value*1>maxScore*1 || obj.value*1<0)) || (maxScore*1<0 && (obj.value*1<maxScore*1 || obj.value*1>0)))
		  		{
		  			alert(INPUT_VALIDATE_INFO1);
		  			obj.value='0';
		  			obj.focus();
		  			return;
		  		}
  			}
		}
		var hashvo=new ParameterSet();
		hashvo.setValue("value",obj.value);
		hashvo.setValue("nameDesc",plan_id+'/'+object_id+'/'+pointid);
		var request=new Request({method:'post',asynchronous:false,onSuccess:reSave,functionId:'9024000015'},hashvo);	
	}	
//手工打分
function editScore()
{
 		document.evaluationForm.action="/performance/evaluation/performanceEvaluation.do?b_query=query&startEditScore=1&operate=init";
	    document.evaluationForm.submit();
}


 // 导出excel
function exportExcel(code,computeFashion,bodyid,pointResult)
{
	if(document.evaluationForm.planid.value=='')
		return;
	var showaband="";
	var showbenbu="";
	var showmethod="";
	var a0100="";
	var tempalteid="";
	if(computeFashion!=null&&computeFashion==6){
		a0100=document.getElementById("a0100").value;
		if(a0100!=null&&a0100.indexOf("`")!=-1){
		
		}else{
			alert("请选择正确的考核对象！");
			return;
		}
		var tt=document.getElementsByName("showdd");
		var showaband="";
		var showbenbu="";
		var showmethod="";
		for(var i=0;i<tt.length;i++){
			if(tt[i].checked){
				var value=tt[i].value;
				switch (value){
					case '1':
						showaband=1;
						break;
					case '2':
						 showbenbu=1;
						 break;
					case '3':
						 showmethod=2;
						 break;
					default :;
				}
					
			}
		}
		templateid=document.getElementById("templateid").value;
	}
	var hashvo=new ParameterSet();
	hashvo.setValue("busitype",document.evaluationForm.busitype.value);
	hashvo.setValue("planid",document.evaluationForm.planid.value);
	hashvo.setValue("showDetails",document.evaluationForm.showDetails.value);
	hashvo.setValue("object_type",document.evaluationForm.object_type.value);
	hashvo.setValue("code",code);
	hashvo.setValue("computeFashion",computeFashion);
	hashvo.setValue("bodyid",bodyid);
	hashvo.setValue("pointResult",pointResult);
	hashvo.setValue("khObjWhere2",getEncodeStr(document.evaluationForm.khObjWhere2.value));
	hashvo.setValue("order_str",getEncodeStr(document.evaluationForm.order_str.value));
	if(computeFashion!=null&&computeFashion==6){
		hashvo.setValue("showaband",showaband);
		hashvo.setValue("showmethod",showmethod);
		
		hashvo.setValue("a0100",a0100);
		hashvo.setValue("showbenbu",showbenbu);
		hashvo.setValue("templateid",templateid);
	}
	var request=new Request({method:'post',asynchronous:false,onSuccess:outIsOk,functionId:'9024000011'},hashvo);
	
}
 // 输出开放式意见明细表
function exportOpenOpinionExcel(code,computeFashion,bodyid,pointResult)
{
	if(document.evaluationForm.planid.value=='')
		return;

	var hashvo=new ParameterSet();
	hashvo.setValue("busitype",document.evaluationForm.busitype.value);
	hashvo.setValue("planid",document.evaluationForm.planid.value);
	hashvo.setValue("code",code);
	hashvo.setValue("computeFashion",computeFashion);
	hashvo.setValue("bodyid",bodyid);
	hashvo.setValue("pointResult",pointResult);
	hashvo.setValue("khObjWhere2",getEncodeStr(document.evaluationForm.khObjWhere2.value));
	hashvo.setValue("order_str",getEncodeStr(document.evaluationForm.order_str.value));
	var request=new Request({method:'post',asynchronous:false,onSuccess:outIsOk,functionId:'9024000030'},hashvo);
	
}
function outIsOk(outparamters)
{

	//zhangh 2020-4-7 下载改为使用VFS
	var outName=outparamters.getValue("filename");
	outName = decode(outName);
	var win=open("/servlet/vfsservlet?fromjavafolder=true&fileid="+outName);
/*
	var filename=outparamters.getValue("filename");
	window.open("/servlet/DisplayOleContent?filename="+filename+".xls","xls");*/
}

function changePlanID()
{
	document.evaluationForm.action="/performance/evaluation/performanceEvaluation.do?b_query=query&operate=init0";
	document.evaluationForm.submit();

}

// 展现 1：评语  2：总结
function showRemark(opt)
{
	if(selectObjectId==undefined)
	{
		alert(INPUT_VALIDATE_INFO11);
		return;
	}
	var strurl="/performance/evaluation/performanceEvaluation.do?b_showRemark=query`opt="+opt+'`object_id='+selectObjectId;
	var iframe_url="/general/query/common/iframe_query.jsp?src="+$URL.encode(strurl);
	var height = opt == 1 ? 580 : 700;
	if(isIE6()){
		height += 20;
	}
    //var ss=window.showModalDialog(iframe_url,arguments,"dialogWidth=710px;dialogHeight=" + height + "px;resizable=yes;scroll=no;status=no;");  
    var config = {
	    width:710,
	    height:height,
	    type:'1',
	    id:'showRemark_win'
	}

	modalDialog.showModalDialogs(iframe_url,"showRemark_win",config,"");
}

function changeFashion()
{
	var ul="/performance/evaluation/performanceEvaluation.do?b_query=query&operate=init0";
	if(document.getElementById("a0100")){
		a0100=document.getElementById("a0100").value;
		ul+="&find="+$URL.encode(a0100);
	}
	document.evaluationForm.action=ul;
	evaluationForm.order_str.value=''; // 计算方式改变，清空排序字符串 by 刘蒙
	document.evaluationForm.submit();
}

function doforcheck(){
	var tt=document.getElementsByName("showdd");
	var showaband="";
	var showbenbu="";
	var showmethod="";
	for(var i=0;i<tt.length;i++){
		if(tt[i].checked){
			var value=tt[i].value;
			switch (value){
				case '1':
					showaband=1;
					break;
				case '2':
					 showbenbu=1;
					 break;
				case '3':
					 showmethod=2;
					 break;
				default :;
			}
				
		}
	}
	var a0100=document.getElementById("a0100").value;
	document.evaluationForm.action="/performance/evaluation/performanceEvaluation.do?b_query=query&operate=init2&&find="+$URL.encode(a0100)+"&showaband="+showaband+"&showbenbu="+showbenbu+"&showmethod="+showmethod;
	document.evaluationForm.submit();
}

function hre(flag){
	var tt=document.getElementsByName("showdd");
		var showaband="";
		var showbenbu="";
		var showmethod="";
		for(var i=0;i<tt.length;i++){
			if(tt[i].checked){
				var value=tt[i].value;
				switch (value){
					case '1':
						showaband=1;
						break;
					case '2':
						 showbenbu=1;
						 break;
					case'3':
						 showmethod=2;
						 break;
					default :;
				}					
			}
		}
	if(flag==0){
		document.evaluationForm.action="/performance/evaluation/performanceEvaluation.do?b_query=query&operate=init2&find=first&showaband="+showaband+"&showbenbu="+showbenbu+"&showmethod="+showmethod;
		document.evaluationForm.submit();
	}
	if(flag==1){
		var up=document.getElementById("upa0100").value;
		document.evaluationForm.action="/performance/evaluation/performanceEvaluation.do?b_query=query&operate=init2&find="+$URL.encode(up)+"&showaband="+showaband+"&showbenbu="+showbenbu+"&showmethod="+showmethod;
		document.evaluationForm.submit();
	}
	if(flag==2){
		var next=document.getElementById("nexta0100").value;
		document.evaluationForm.action="/performance/evaluation/performanceEvaluation.do?b_query=query&operate=init2&find="+$URL.encode(next)+"&showaband="+showaband+"&showbenbu="+showbenbu+"&showmethod="+showmethod;
		document.evaluationForm.submit();
	}
	if(flag==3){
		document.evaluationForm.action="/performance/evaluation/performanceEvaluation.do?b_query=query&operate=init2&find=end"+"&showaband="+showaband+"&showbenbu="+showbenbu+"&showmethod="+showmethod;
		document.evaluationForm.submit();
	}
}
function setPlanScope(scope)
{
	document.evaluationForm.plan_scope.value=scope;
	document.evaluationForm.action="/performance/evaluation/performanceEvaluation.do?b_query=query&operate=init0&changeScope=1";
	document.evaluationForm.submit();
}


function setPointResult(result)
{
	document.evaluationForm.pointResult.value=result;
	document.evaluationForm.action="/performance/evaluation/performanceEvaluation.do?b_query=query&operate=init0";
	document.evaluationForm.submit();
}


//定义计算规则
function defineRule(planStatus)
{
	var strurl="/performance/evaluation/calculate.do?b_showRule=show`planid="+document.evaluationForm.planid.value+"`planStatus="+planStatus+"`callbackFunc=refresh_ok";
	var iframe_url="/general/query/common/iframe_query.jsp?src="+$URL.encode(strurl);
	/*if(isIE6()){
		var ss=window.showModalDialog(iframe_url,arguments,"dialogWidth=590px;dialogHeight=430px;resizable=no;scroll=no;status=no;");  
	}else{
		var ss=window.showModalDialog(iframe_url,arguments,"dialogWidth=570px;dialogHeight=410px;resizable=no;scroll=no;status=no;");  
	}*/
	var config = {
	    width:590,
	    height:430,
	    type:'1',
        title:'计算规则',
	    id:'defineRule_win'
	}
	modalDialog.showModalDialogs(iframe_url,"defineRule_win",config,refresh_ok);
}

function refresh_ok(ss){
	if(ss!=null && (ss.flag=="true" || ss =='ok'))
	{
		document.evaluationForm.action="/performance/evaluation/performanceEvaluation.do?b_query=query&operate=init";
	    document.evaluationForm.submit();
	}
}

var rulefanwei_id = "";
var rulefanwei_planStatus = "";
function rulefanwei(id,planStatus){
	var validate=document.getElementById('wer');
	var tem="false";
	if(validate.checked){
		var tem="true";
	}else{
	
	}
	var strurl="/performance/evaluation/calculate.do?b_definerange=show`planid="+id+"`isvalidate="+tem;
    var iframe_url="/general/query/common/iframe_query.jsp?src="+$URL.encode(strurl);
	//var ss=window.showModalDialog(iframe_url,arguments,"dialogWidth=600px;dialogHeight=400px;resizable=yes;scroll=no;status=no;");  
    rulefanwei_id = id;
    rulefanwei_planStatus = planStatus;
	var config = {
	    width:600,
	    height:400,
	    type:'2'
	}

	modalDialog.showModalDialogs(iframe_url,"rulefanwei_win",config,rulefanwei_ok);

}

function rulefanwei_ok(ss) {
	if(ss!=null&&ss.ok=='ok'){
		document.calcRuleForm.isvalidate.value=ss.IsValidate;
		document.calcRuleForm.action="/performance/evaluation/calculate.do?b_showRule="+showRule+"&planid="+rulefanwei_id+"&opt=2"+"&planStatus="+rulefanwei_planStatus+"&isvalidate="+ss.isvalidate+"&operate=select";
		document.calcRuleForm.submit();
	}
}

//按新标度重算 1
function repeatCompute()
{	
	if(confirm(INPUT_VALIDATE_INFO2+"?"))
	{
		var hashvo=new ParameterSet();
		hashvo.setValue("plan_id",document.evaluationForm.planid.value);
		var request=new Request({method:'post',asynchronous:false,onSuccess:reCompute,functionId:'9024000002'},hashvo);
	}
}

//按新标度重算 2
function reCompute(outparamters)
{
    var info=outparamters.getValue("info");
	if(info.length>0)
	{
		ainfo=getDecodeStr(info);
		var arguments=new Array();
		arguments[0]=ainfo;
		var strurl="/performance/evaluation/showInfo.jsp";
	    var iframe_url="/general/query/common/iframe_query.jsp?src="+strurl; 
		var ss=window.showModalDialog(iframe_url,arguments,"dialogWidth=460px;dialogHeight=350px;resizable=yes;scroll=no;status=no;");  
	    
	 }
	 else
	 {
	 	
	 	var hashvo=new ParameterSet();
		hashvo.setValue("plan_id",document.evaluationForm.planid.value);
		var request=new Request({method:'post',asynchronous:false,onSuccess:compute,functionId:'9024000012'},hashvo);
	    
	 }
}
//按新标度重算 3
function compute(outparamters)
{
		var strurl="/performance/evaluation/calculate.do?b_showRule=cal`planid="+document.evaluationForm.planid.value;
	    var iframe_url="/general/query/common/iframe_query.jsp?src="+$URL.encode(strurl);
		var ss=window.showModalDialog(iframe_url,arguments,"dialogWidth=520px;dialogHeight=280px;resizable=yes;scroll=no;status=no;");  
		document.evaluationForm.action="/performance/evaluation/performanceEvaluation.do?b_query=query&operate=init0";
	    document.evaluationForm.submit();
}

function jgfk(){
	var hashvo=new ParameterSet();
	hashvo.setValue("planid",document.evaluationForm.planid.value);
	hashvo.setValue("opt",'37');
	var request=new Request({method:'post',asynchronous:false,onSuccess:jgfkResult,functionId:'9023000003'},hashvo);
}
function jgfkResult(outparameters){
	var flag =outparameters.getValue("flag");
	if(flag=="1"){
		alert("考核结果发布成功！");
		document.getElementById("jg").style.display = "none";//绩效评估点击结果反馈成功后隐藏结果反馈按钮
	}
}



//计算校验
function computeValidate()
{
	var hashvo=new ParameterSet();
	hashvo.setValue("planid",document.evaluationForm.planid.value);
	hashvo.setValue("opt",'25');
	var request=new Request({method:'post',asynchronous:false,onSuccess:testResult,functionId:'9023000003'},hashvo);			
}
function testResult(outparameters)
{
   var flag =outparameters.getValue("flag");
   var handScore =outparameters.getValue("handScore");//1 启动录入结果
   if(flag=='1')
   {
   		if(handScore=='1')
   		{
   			strurl="/performance/evaluation/calculate.do?b_showRule=cal`planid="+document.evaluationForm.planid.value+"`code="+document.evaluationForm.code.value+"`callbackFunc=testResult_ok";
			iframe_url="/general/query/common/iframe_query.jsp?src="+$URL.encode(strurl);
			/*if(isIE6()){
				return_vo=window.showModalDialog(iframe_url,arguments,"dialogWidth=570px;dialogHeight=400px;resizable=no;scroll=no;status=no;");  
			}else{
				return_vo=window.showModalDialog(iframe_url,arguments,"dialogWidth=570px;dialogHeight=395px;resizable=no;scroll=no;status=no;");  
			}*/
			
			var config = {
			    width:580,
			    height:450,
			    type:'1',
			    id:'defineRule_win',
                title:'计算'

			}

			modalDialog.showModalDialogs(iframe_url,"defineRule_win",config,testResult_ok);
			
   		}else
   		{  		
	   		var strurl="/performance/evaluation/performanceEvaluation.do?b_calcuValidate=link`planid="+document.evaluationForm.planid.value+"`validateOper=1`callbackFunc=testResult_first_ok";
			var iframe_url="/general/query/common/iframe_query.jsp?src="+$URL.encode(strurl);
			//var return_vo=window.showModalDialog(iframe_url,arguments,"dialogWidth=700px;dialogHeight=530px;resizable=yes;scroll=no;status=no;");  
			var config = {
			    width:710,
			    height:560,
			    type:'1',
			    id:'testResult_first_win',
                title:'计算'
			}
			modalDialog.showModalDialogs(iframe_url,"testResult_first_win",config,testResult_first_ok);
		}
   }else
   		alert(KH_IMPLEMENT_INF13);
}

function close_cal() {
	Ext.getCmp("testResult_first_win").close();
}

function testResult_ok(return_vo){
	if(return_vo==null)
		 return false;	   
	 	if(return_vo.flag=="true") 
	{
			document.evaluationForm.action="/performance/evaluation/performanceEvaluation.do?b_query=query&operate=init0";
		document.evaluationForm.submit();
	}
}

function testResult_first_ok(return_vo){
	if(return_vo==null)
		return false;	
	if(return_vo.flag=="true")
	{
		strurl="/performance/evaluation/calculate.do?b_showRule=cal`planid="+document.evaluationForm.planid.value+"`code="+document.evaluationForm.code.value+"`callbackFunc=testResult_ok";
		iframe_url="/general/query/common/iframe_query.jsp?src="+$URL.encode(strurl);
		/*if(isIE6()){
			return_vo=window.showModalDialog(iframe_url,arguments,"dialogWidth=590px;dialogHeight=420px;resizable=no;scroll=no;status=no;");  
		}else{
			return_vo=window.showModalDialog(iframe_url,arguments,"dialogWidth=590px;dialogHeight=410px;resizable=no;scroll=no;status=no;");  
		}*/
		var config = {
		    width:590,
		    height:450,
		    type:'1',
		    id:'defineRule_win'
		}

		modalDialog.showModalDialogs(iframe_url,"defineRule_win",config,testResult_ok);
	}
}
/*
function computeValidate()
{
	var hashvo=new ParameterSet();
	hashvo.setValue("plan_id",document.evaluationForm.planid.value);
	var request=new Request({method:'post',asynchronous:false,onSuccess:returnIsOk,functionId:'9024000002'},hashvo);

}



function returnIsOk(outparamters)
{
	var info=outparamters.getValue("info");
	alert(info);
	if(info.length>0)
	{
		ainfo=getDecodeStr(info);
		var arguments=new Array();
		arguments[0]=ainfo;
		arguments[1]="是否继续计算";
		var strurl="/performance/evaluation/showInfo.jsp";
	    var iframe_url="/general/query/common/iframe_query.jsp?src="+strurl; 
		var ss=window.showModalDialog(iframe_url,arguments,"dialogWidth=460px;dialogHeight=350px;resizable=yes;scroll=no;status=no;");  
	    if(typeof(ss)!='undefined'&&ss=='1')
		{
		
			checkPerDegree();
		}
	}
	else
	{
		checkPerDegree();
	
	}
}

//检查等级分类
function checkPerDegree()
{
	var hashvo=new ParameterSet();
	hashvo.setValue("plan_id",document.evaluationForm.planid.value);
	var request=new Request({method:'post',asynchronous:false,onSuccess:returnCheck,functionId:'9026003013'},hashvo);
}

function returnCheck(outparamters)
{
	var info=getDecodeStr(outparamters.getValue("checkResult"));
	if(info.indexOf("错误")!=-1||info.indexOf("警告")!=-1||info.indexOf("提示")!=-1)
	{	
		var arguments=new Array();
		arguments[0]=info;
		arguments[1]="等级结果检查";
		var strurl="/performance/evaluation/showInfo.jsp";
	    var iframe_url="/general/query/common/iframe_query.jsp?src="+strurl; 
		var ss=window.showModalDialog(iframe_url,arguments,"dialogWidth=460px;dialogHeight=350px;resizable=yes;scroll=no;status=no;");  
	    if(typeof(ss)!='undefined'&&ss=='1')
	    {
			alert(KH_EVALUATION_INFO4);
		}
	    
	}
	else
	{
		var strurl="/performance/evaluation/calculate.do?b_showRule=show`planid="+document.evaluationForm.planid.value;
		var iframe_url="/general/query/common/iframe_query.jsp?src="+strurl; 
	    var return_vo=window.showModalDialog(iframe_url,arguments,"dialogWidth=520px;dialogHeight=280px;resizable=yes;scroll=no;status=no;");  
	    if(return_vo==null)
			 return false;	   
	   if(return_vo.flag=="true") 
		document.evaluationForm.action="/performance/evaluation/performanceEvaluation.do?b_query=query&operate=init0";
		document.evaluationForm.submit();
	}
	
}
*/


var ori_obj;
var selectObjectId;
var selectObjectId_extra="init";
var correctScore_code = "";
var inputexcel_code = "";

// 选中行
function selectRow(obj)
{
	
	if(ori_obj)
	{
		for(var i=2;i<ori_obj.cells.length;i++)
		{
			ori_obj.cells[i].bgColor='#FFFFFF';
		}
	}
	var rowObj=obj.parentNode;
	selectObjectId=rowObj.id;
	var n = rowObj.cells.length;
	for(var i=2;i<n;i++)
	{
		rowObj.cells[i].bgColor='#98C2E8';
	}
	ori_obj=rowObj;
	
}

//输出简报
function briefingOut()
{
	var hashvo=new ParameterSet();
	hashvo.setValue("planid",document.evaluationForm.planid.value);
	var request=new Request({method:'post',asynchronous:false,onSuccess:outFile,functionId:'9024000010'},hashvo);

}

function outFile(outparamters)
{
	var outName=outparamters.getValue("briefingName");
	outName = decode(outName);
	var win=open("/servlet/vfsservlet?fromjavafolder=true&fileid="+outName);
}

//修正分值
function correctScore(code)
{
	if(ori_obj)
	{
		correctScore_code = code;
		var strurl="/performance/evaluation/performanceEvaluation.do?b_showCorrectScore=show`objectid="+ori_obj.id+"`planid="+document.evaluationForm.planid.value;
	    var iframe_url="/general/query/common/iframe_query.jsp?src="+$URL.encode(strurl);
		//var ss=window.showModalDialog(iframe_url,arguments,"dialogWidth=500px;dialogHeight=280px;resizable=yes;scroll=no;status=no;");
	    var config = {
            width:500,
            height:280,
            type:'1',
            id:'correctScore_win'
        }
        
        modalDialog.showModalDialogs(iframe_url,"correctScore_win",config,correctScore_ok);
	}
	else
	{
		alert(INPUT_VALIDATE_INFO3+"!");
	}
}

function correctScore_ok(retvo) {
	if(retvo!=null)
	{
		document.evaluationForm.action="/performance/evaluation/performanceEvaluation.do?b_query=query&operate=init0&code="+correctScore_code;
		document.evaluationForm.submit();
	}
}

function inputexcel(code){
	var strurl="/performance/evaluation/performanceEvaluation.do?br_show=show`objectid="+ori_obj.id+"`planid="+document.evaluationForm.planid.value+"`object_type="+document.evaluationForm.object_type.value;
	var iframe_url="/general/query/common/iframe_query.jsp?src="+$URL.encode(strurl);
	inputexcel_code = code;
	//var ss=window.showModalDialog(iframe_url,arguments,"dialogWidth=500px;dialogHeight=200px;resizable=yes;scroll=no;status=no;");
	var config = {
        width:500,
        height:220,
        type:'2'
    }
    
    modalDialog.showModalDialogs(iframe_url,"inputexcel_win",config,inputexcel_ok);
}
function inputexcel_ok(retvo) {
	if(retvo!=null)
	{
		document.evaluationForm.action="/performance/evaluation/performanceEvaluation.do?b_query=query&operate=init0&code="+inputexcel_code;
		document.evaluationForm.submit();
	}
}
var dialogArguments = new Array();

// opt 1:调高等级  2。调低等级
function adjustGrade(opt)
{
	if(selectObjectId)
	{
		//var hashvo=new ParameterSet();
		//hashvo.setValue("planid",document.evaluationForm.planid.value);
		//hashvo.setValue("opt",opt);
		//hashvo.setValue("object_id",selectObjectId);
		//var request=new Request({method:'post',asynchronous:false,onSuccess:returnAdjust,functionId:'9024000009'},hashvo);
		dialogArguments[0]="0";
		dialogArguments[1]="请填写等级调整原因:";
		dialogArguments[2]=document.evaluationForm.planid.value;
		dialogArguments[3]=opt;
		dialogArguments[4]=selectObjectId;
		
		var target_url="/performance/evaluation/performanceEvaluation.do?b_adjustGrade=link`planid="+document.evaluationForm.planid.value+"`objectid="+selectObjectId;
 		var iframe_url="/general/query/common/iframe_query.jsp?src="+$URL.encode(target_url);
		//var return_vo= window.showModalDialog(iframe_url, arguments, 
	    //          "dialogWidth:390px; dialogHeight:380px;resizable:no;center:yes;scroll:no;status:no");	
	    var title = opt==1?"提高等级":"降低等级";
		var config = {
            width:390,
            height:380,
            type:'1',
            id:'adjustGrade_win',
            title:title,
            dialogArguments:dialogArguments
        }
		modalDialog.showModalDialogs(iframe_url,dialogArguments, config, adjustGrade_ok);
	}
	else
		alert(INPUT_VALIDATE_INFO4);
}

function adjustGrade_ok(return_vo){
	if(return_vo && return_vo.flag=="true")
    {
    		var info = return_vo.info;
    		//info="&nbsp;"+info;///zzk 加空格 提高降低等级内容对齐   
    		if(info.indexOf("!")!=-1)
				alert(info);
			else
			{
				var n = ori_obj.cells.length;
				for(var i=n-1;i>=0;i--)
				{
				    //自定义属性不能用xxx.xx，应该用getAttribute haosl 20190514
					if(ori_obj.cells[i].getAttribute('name')=='exx_object')
					{
						ori_obj.cells[i].innerHTML=info.substring(info.lastIndexOf("&")+1,info.length);
					}
					if(ori_obj.cells[i].getAttribute('name')=='desc')
					{
						ori_obj.cells[i].innerHTML=info.substring(0,info.lastIndexOf("&"));
					} 
				}
			}
    		dialogArguments = new Array();
    }
}
function QueryRemark(a0101,objectid)
{
  	var arguments=new Array();
	arguments[0]="1";
	arguments[1]=a0101+"－备注:";
	var target_url="/performance/evaluation/performanceEvaluation.do?b_adjustGrade=link`planid="+document.evaluationForm.planid.value+"`objectid="+objectid;
 	var iframe_url="/general/query/common/iframe_query.jsp?src="+$URL.encode(target_url);
	/*var return_vo= window.showModalDialog(iframe_url, arguments, 
	              "dialogWidth:390px; dialogHeight:360px;resizable:no;center:yes;scroll:no;status:no");*/
	
	var config = {
	    width:400,
	    height:380,
	    type:'1',
	    dialogArguments:arguments,
	    id:"adjustGrade_win"
	}
	if(!window.showModalDialog)
		window.dialogArguments = arguments;

	modalDialog.showModalDialogs(iframe_url,"adjustGrade_win",config,"");

}
function QueryRemarkField(a0101,objectid,fieldid)
{
  	var arguments=new Array();
  	arguments[0]=a0101;
	var target_url="/performance/evaluation/performanceEvaluation.do?b_searchRemarkField=link`planid="+document.evaluationForm.planid.value+"`objectid="+objectid+"`fieldid="+fieldid;
 	var iframe_url="/general/query/common/iframe_query.jsp?src="+$URL.encode(target_url);
	var return_vo= window.showModalDialog(iframe_url, arguments, 
	              "dialogWidth:390px; dialogHeight:360px;resizable:no;center:yes;scroll:no;status:no");

}
/*
function returnAdjust(outparamters)
{
	var info=getDecodeStr(outparamters.getValue("info"));
	alert(info);
	if(info.indexOf("!")!=-1)
		alert(info);
	else
	{
		var n = ori_obj.cells.length;
		for(var i=n-1;i>=0;i--)
		{
			if(ori_obj.cells[i].name=='desc')
			{
				ori_obj.cells[i].innerHTML=info;
				break;
			}
		}
	}
}
*/
var importexpre_code = "";
//引入
function importexpre(code)
{	
	var strurl="/performance/evaluation/set_import.do?b_search=link`busitype="+evaluationForm.busitype.value+"`planid="+document.evaluationForm.planid.value+"`flag=plan";
	var iframe_url="/general/query/common/iframe_query.jsp?src="+$URL.encode(strurl);
	importexpre_code = code;
	//var ss=window.showModalDialog(iframe_url,null,"dialogWidth=850px;dialogHeight=730px;resizable=yes;scroll=no;status=no;");  
	var config = {
        width:850,
        height:530,
        type:'1',
        id:'importexpre_win'
    }
    
    modalDialog.showModalDialogs(iframe_url,"importexpre_win",config,importexpre_ok);
}

function importexpre_ok(retvo) {
	if(retvo=='ok')
	{
		document.evaluationForm.action="/performance/evaluation/performanceEvaluation.do?b_query=query&operate=init&code="+importexpre_code;
		document.evaluationForm.submit();
	}
}
//将下面table中选择的列放入list
function choiceidAll(id)
{
	if(id=='up')
	{
		downlist.length = 0;
		var tabup=document.getElementById("up");
		for(var i=1;i<tabup.rows.length;i++)
		{	
			var chklist,objname,i,typeanme;
      		chklist=document.getElementsByTagName('INPUT');
     	    if(!chklist)
       			 return;
			for(j=0;j<chklist.length;j++)
	 		{
	   			typeanme=chklist[j].type.toLowerCase();
	     		if(typeanme!="checkbox")
	       			 continue;	  
	    		 objname=chklist[j].name;
	    		
	     		if(!objname.match(tabup.rows[i].id))
	       			 continue;
	       		choiceiddown(chklist[j]);
	  		}	  		
		}
	}else if(id=='down')
	{
		uplist.length = 0;
		var tabdown=document.getElementById("down");
		for(var i=1;i<tabdown.rows.length;i++)
		{			
		  	var chklist,objname,i,typeanme;
      		chklist=document.getElementsByTagName('INPUT');
     	    if(!chklist)
       			 return;
			for(j=0;j<chklist.length;j++)
	 		{
	   			typeanme=chklist[j].type.toLowerCase();
	     		if(typeanme!="checkbox")
	       			 continue;	  
	    		 objname=chklist[j].name;	    		
	     		if(!objname.match(tabdown.rows[i].id))
	       			 continue;
	       		choiceidup(chklist[j]);
	  		}			
		}
	}
}
function choiceidup(obj)
{
	var id = obj.parentElement.parentElement.id;
	for(i=0;i<uplist.length;i++)
	{
		if(uplist[i]==id)
			uplist[i]="aaa";
	}
	if(obj.checked==true)		
		uplist.push(id);
	taxis(uplist,"up");
}

function choiceiddown(obj)
{
	var id = obj.parentElement.parentElement.id;
	for(i=0;i<downlist.length;i++)
	{
		if(downlist[i]==id)
			downlist[i]="aaa";
	}
	if(obj.checked==true)
	{
		selPlans.push(obj.value);
		downlist.push(id);
	}else
	{
		for(var i=0;i<selPlans.length;i++)
		{
			if(obj.value==selPlans[i])
			{
				selPlans=selPlans.slice(0,i).concat(selPlans.slice(i+1,selPlans.length)); 
				break;
			}				
		}
				
	}	
		
	taxis(downlist,"down");
}
//向上移动
function up()
{		
	var index = 0;
	var tab_down=document.getElementById("down");
	for(i=0;i<uplist.length;i++)
	{
		if(uplist[i]!='aaa')
		{	
			var obj_tr = document.getElementById(uplist[i]);
			var tabup=document.getElementById("up");
			var newRow=tabup.insertRow(tabup.rows.length);	
			
			for(k=0;k<tab_down.rows.length;k++)		
			{
				if(uplist[i]==tab_down.rows[k].id)
				{
					obj_tr=tab_down.rows[k];
					break;
				}				 	
			}
			
			insertrow(obj_tr,newRow,"up");	
			var tabdown=document.getElementById("down");
			tabdown.deleteRow(uplist[i].substring(5)-index);
			index = index+1;
		}
		
	}

	var tabup=document.getElementById("up");
	for(i=0;i<tabup.rows.length;i++)
	{	
		tabup.rows[i].id = "up_"+(i);
	}
	var tabdown=document.getElementById("down");
	for(i=0;i<tabdown.rows.length;i++)
	{
		tabdown.rows[i].id = "down_"+(i);
	}
	uplist.length = 0;
}
//从上面的table中移动到下面的table
function down()
{
	var index = 0;
	var tab_up=document.getElementById("up");
	for(i=0;i<downlist.length;i++)
	{
		if(downlist[i]!='aaa')
		{	
			var obj_tr = document.getElementById(downlist[i]);
			var tabup=document.getElementById("down");
			var newRow=tabup.insertRow(tabup.rows.length);
			
			for(k=0;k<tab_up.rows.length;k++)		
			{
				if(downlist[i]==tab_up.rows[k].id)
				{
					obj_tr=tab_up.rows[k];
					break;
				}				 	
			}
			
			insertrow(obj_tr,newRow,"down");
			
			for(var j=0;j<selPlans.length;j++)
		    {
				if(parseInt(obj_tr.cells[1].innerHTML)==parseInt(selPlans[j]))
				{
					selPlans=selPlans.slice(0,j).concat(selPlans.slice(j+1,selPlans.length)); 
					break;
			 	}				
			}			
			
			var tabdown=document.getElementById("up");
			tabdown.deleteRow(downlist[i].substring(3)-index);
			index = index+1;
		}
	}
	var tabdown=document.getElementById("down");
	for(i=0;i<tabdown.rows.length;i++)
	{
		tabdown.rows[i].id = "down_"+(i);
	}
	var tabup=document.getElementById("up");
	for(i=0;i<tabup.rows.length;i++)
	{
		tabup.rows[i].id = "up_"+(i);
	}
	downlist.length = 0;
}
//增加新的一行
function insertrow(obj_tr,newRow,type)
{
	var tabstr = "";
	newRow.className = "trDeep";
	myNewCell=newRow.insertCell(0);
	myNewCell.className = "RecordRow_right";
	myNewCell.align="center";
	if(type=='up'){
		var tabup=document.getElementById("up");			
		tabstr="<input type=\"checkbox\" name=\"up_"+(tabup.rows.length-1)+"\"  onclick=\"choiceiddown(this)\" value=\""+obj_tr.cells[1].innerHTML+"\"/>";
	
	}
	else if(type=='down'){
		var tabdown=document.getElementById("down");			
		tabstr="<input type=\"checkbox\" name=\"down_"+(tabdown.rows.length-1)+"\"  onclick=\"choiceidup(this)\" value=\""+obj_tr.cells[1].innerHTML+"\"/>";
	}
	
	myNewCell.innerHTML = tabstr;
	myNewCell=newRow.insertCell(1);
	myNewCell.className = "RecordRow";
	myNewCell.innerHTML = obj_tr.cells[1].innerHTML;
	myNewCell.align="center";
	myNewCell=newRow.insertCell(2);
	myNewCell.className = "RecordRow";
	myNewCell.innerHTML = obj_tr.cells[2].innerHTML;
	myNewCell=newRow.insertCell(3);
	myNewCell.className = "RecordRow";
	myNewCell.innerHTML = obj_tr.cells[3].innerHTML;
	myNewCell=newRow.insertCell(4);
	myNewCell.className = "RecordRow";
	myNewCell.innerHTML = obj_tr.cells[4].innerHTML;
	myNewCell=newRow.insertCell(5);
	myNewCell.className = "RecordRow_left";
	myNewCell.innerHTML = obj_tr.cells[5].innerHTML;
}
//为数组排序
function taxis(list,type)
{

	for(i=0;i<list.length-1;i++)
	{
		for(j=i+1;j<list.length;j++)
		{
			if(list[i]!='aaa')
			{
				var temp;
				if(type=='up'){
					temp = 5;
				}
				else if(type=='down'){
					temp = 3;
				}
				if(parseInt(list[i].substring(temp))>parseInt(list[j].substring(temp)))
				{
					var a =list[i];
					list[i] = list[j];
					list[j]=a;
				}
			}
		}
	}
}  
//考核系数公式
function examineConfigExpr()
{
	var strurl="/performance/evaluation/performanceEvaluation.do?b_examModulus=link";
	var iframe_url="/general/query/common/iframe_query.jsp?src="+strurl; 
	var ss=window.showModalDialog(iframe_url,window,"dialogWidth=650px;dialogHeight=370px;resizable=yes;scroll=yes;status=no;");  
	if(ss&&ss=='ok')
	{
		document.evaluationForm.action="/performance/evaluation/performanceEvaluation.do?b_query=query&operate=init";
	    document.evaluationForm.submit();
	}

}
//等级公式
function gradeFormula()
{
	var strurl="/performance/evaluation/performanceEvaluation.do?b_gradeFormula=link";
	var iframe_url="/general/query/common/iframe_query.jsp?src="+strurl; 
	var return_vo=window.showModalDialog(iframe_url,window,"dialogWidth=580px;dialogHeight=300px;resizable=yes;scroll=yes;status=no;");  
	if(return_vo==null)
		return false;	   
	if(return_vo.flag=="true") 
	{
		document.evaluationForm.action="/performance/evaluation/performanceEvaluation.do?b_query=query&operate=init";
	    document.evaluationForm.submit();
	}

}
//总分公式
function countExpr()
{
	var strurl="/performance/evaluation/expressions.do?b_query=link`planid="+document.evaluationForm.planid.value;
	var iframe_url="/general/query/common/iframe_query.jsp?src="+$URL.encode(strurl);
	var ss=window.showModalDialog(iframe_url,window,"dialogWidth=600px;dialogHeight=390px;resizable=yes;scroll=yes;status=no;");  
}
//计算公式
function computFormula(planStatus)
{
	var strurl="/performance/evaluation/performanceEvaluation.do?b_computFormula=link`planStatus="+planStatus;
	var iframe_url="/general/query/common/iframe_query.jsp?src="+$URL.encode(strurl);
	/*if(isIE6()){
		var ss=window.showModalDialog(iframe_url,window,"dialogWidth=800px;dialogHeight=470px;resizable=no;scroll=no;status=no;"); 
	}else{
		var ss=window.showModalDialog(iframe_url,window,"dialogWidth=800px;dialogHeight=440px;resizable=no;scroll=no;status=no;"); 
	}*/
	var config = {
	    width:800,
	    height:470,
	    type:'1',
        title:'计算公式',
	    id:'computFormula_win'
	}

	modalDialog.showModalDialogs(iframe_url,"computFormula_win",config,refresh_ok);
}
// 考核等级
function setKhDegree()
{	
	var strurl="/performance/options/perDegreeList.do?b_query2=link`planid="+evaluationForm.planid.value+"`busitype="+evaluationForm.busitype.value;
	var iframe_url="/general/query/common/iframe_query.jsp?src="+$URL.encode(strurl);
	//var ss=window.showModalDialog(iframe_url,arguments,"dialogWidth:800px; dialogHeight:500px;resizable:yes;center:yes;scroll:yes;status:no;minimize:yes;maximize:yes;");
	var config = {
	    width:800,
	    height:500,
	    type:'1',
        title:'考核等级',
	    id:"setKhDegree_win"
	}

	modalDialog.showModalDialogs(iframe_url,"setKhDegree_win",config,importexpre_ok);
}
//简单查询
function simpleQuery()
{
	var theurl="/performance/implement/kh_object/condition_select.do?b_query=link`db=Usr";
    var iframe_url="/general/query/common/iframe_query.jsp?src="+$URL.encode(theurl);
    /*if(isIE6()){
    	var sql_str= window.showModalDialog(iframe_url, 'template_win', 
			"dialogWidth:570px; dialogHeight:440px;resizable:no;center:yes;scroll:no;status:no");
    }else{
    	var sql_str= window.showModalDialog(iframe_url, 'template_win', 
			"dialogWidth:550px; dialogHeight:410px;resizable:no;center:yes;scroll:no;status:no");
    }*/
   	
    var config = {
	    width:570,
	    height:440,
	    type:'2'
	}

	modalDialog.showModalDialogs(iframe_url,"simpleQuery_win",config,conditionselect_ok);
}

function conditionselect_ok(sql_str) {
	if(sql_str!=null)
	{
		
		evaluationForm.objStr_temp.value=sql_str.sql;
		if (document.cookie != "")
		{ 
			var Days = 30; //此 cookie 将被保存 30 天
		    var exp  = new Date();    //new Date("December 31, 9998");
		    exp.setTime(exp.getTime() + Days*24*60*60*1000);
		    document.cookie ="objStr="+ escape (sql_str.sql) + ";expires=" + exp.toGMTString();
		}  
		evaluationForm.isDispAll.value="false";
		document.evaluationForm.action="/performance/evaluation/performanceEvaluation.do?b_query=query&operate=init1";
	    document.evaluationForm.submit();
	}
}
	
	
//显示结果
function showResultData(isAll)
{
	if(isAll=='true')
		evaluationForm.objStr.value='';
	else
	{
		if (document.cookie != "")
		{
		 	var arr = document.cookie.match(new RegExp("(^| )objStr=([^;]*)(;|$)"));
	    	 if(arr != null)
	    	 {
	      		 evaluationForm.objStr.value=unescape(arr[2]);
	     
	    	 }
		}
	}
	evaluationForm.isDispAll.value=isAll;
	document.evaluationForm.action="/performance/evaluation/performanceEvaluation.do?b_query=query&operate=init";
	document.evaluationForm.submit();
}

//显示对象详情
function showDetails()
{	
   if(evaluationForm.showDetails.value==""||evaluationForm.showDetails.value=="false"){
		evaluationForm.showDetails.value="true";
	}else{
		evaluationForm.showDetails.value="false";
	}

	document.evaluationForm.action="/performance/evaluation/performanceEvaluation.do?b_query=query&operate=init";
	document.evaluationForm.submit();
}
var sort_code = "";
//排序
function sort(code,computeFashion)
{
	var planid=evaluationForm.planid.value;
	var theurl="/performance/evaluation/performanceEvaluation.do?br_sort=link`planid="+planid+"`computeFashion="+computeFashion;
    var iframe_url="/general/query/common/iframe_query.jsp?src="+$URL.encode(theurl);
    /*if(isIE6()){
    	var order_str= window.showModalDialog(iframe_url, 'template_win',
			"dialogWidth:670px; dialogHeight:430px;resizable:no;center:yes;scroll:no;status:no");
    }else{
    	var order_str= window.showModalDialog(iframe_url, 'template_win',
			"dialogWidth:650px; dialogHeight:400px;resizable:no;center:yes;scroll:no;status:no");
    }*/
    sort_code = code;
    var config = {
	    width:650,
	    height:430,
	    type:'1',
        title:'指标排序',
	    id:'sort_win'
	}

	modalDialog.showModalDialogs(iframe_url,"sort_win",config,sort_ok);
}
function sort_ok(order_str) {
	if(order_str!=null && order_str!='')
	{
	
		evaluationForm.order_str.value=' '+order_str;
		document.evaluationForm.action="/performance/evaluation/performanceEvaluation.do?b_query=query&operate=init&code="+sort_code;
	    document.evaluationForm.submit();
	}
}
//同步对象顺序
function synchronizeObjs(code)
{
	document.evaluationForm.action="/performance/evaluation/performanceEvaluation.do?b_synchronizeObjs=link&operate=init&code="+code;
	document.evaluationForm.submit();
}
//手工选择
function handSel()
{
		/*
	var planid=evaluationForm.planid.value;
	var theurl="/performance/evaluation/performanceEvaluation.do?br_handSel=link`planid="+planid;
    var iframe_url="/general/query/common/iframe_query.jsp?src="+theurl;
   	var sql_str= window.showModalDialog(iframe_url, 'template_win', 
      				"dialogWidth:550px; dialogHeight:450px;resizable:no;center:yes;scroll:yes;status:yes");

	alert(sql_str);

	if(sql_str!=null && sql_str!='')
	{
		evaluationForm.objStr.value=sql_str;
		if (document.cookie != "")
		{
			var Days = 30; //此 cookie 将被保存 30 天
		    var exp  = new Date();    //new Date("December 31, 9998");
		    exp.setTime(exp.getTime() + Days*24*60*60*1000);
		    document.cookie ="objStr="+ escape (sql_str) + ";expires=" + exp.toGMTString();
		 }
		evaluationForm.isDispAll.value="false";
		document.evaluationForm.action="/performance/evaluation/performanceEvaluation.do?b_query=query&operate=init";
	    document.evaluationForm.submit();
	}
	
	var aplanid=document.evaluationForm.planid.value;
	var opt = 2;
	var infos=new Array();
	infos[0]=aplanid;
	infos[1]=opt;

    var strurl="/performance/handSel.do?b_query=link`planid="+aplanid+"`opt="+opt;
	var iframe_url="/gz/gz_accounting/bankdisk/iframe_bank_disk.jsp?src="+strurl; 
	var flag=window.showModalDialog(iframe_url,infos,"dialogWidth=600px;dialogHeight=480px;resizable=yes;scroll=no;status=no;");  
	if(flag=="1")
	{

	}*/
	var right_fields='';
	var aplanid=evaluationForm.planid.value;
	var opt = 2;
	var infos=new Array();
	infos[0]=aplanid;
	infos[1]=opt;
	infos[2]=aplanid;
	
    var strurl="/performance/handSel.do?b_query=link`planid="+aplanid+"`opt="+opt+"`callBackfunc=select_ok";
	var iframe_url="/gz/gz_accounting/bankdisk/iframe_bank_disk.jsp?src="+$URL.encode(strurl);
	
	//var objList=window.showModalDialog(iframe_url,infos,"dialogWidth=610px;dialogHeight=480px;resizable=yes;scroll=no;status=no;");  
	var config = {
	    width:650,
	    height:480,
	    type:'1',
	    dialogArguments:infos,
	    id:"handSel_win"
	}
	if(!window.showModalDialog)
		window.dialogArguments = infos;
	
	modalDialog.showModalDialogs(iframe_url,"handSel_win",config,select_ok);
}
function selectWinClose(){
	Ext.getCmp("handSel_win").close();
}
function select_ok(objList){
	var right_fields = "";
	if(objList==null)
		return false;	

	if(objList.length>0)
	{
		for(var i=0;i<objList.length;i++)		   	
		    right_fields+=",'"+objList[i]+"'";		   		
		   		
		evaluationForm.objStr.value=right_fields.substring(1);
		if (document.cookie != "")
		{
			var Days = 30; //此 cookie 将被保存 30 天
		    var exp  = new Date();    //new Date("December 31, 9998");
		    exp.setTime(exp.getTime() + Days*24*60*60*1000);
		    document.cookie ="objStr="+ escape (right_fields.substring(1)) + ";expires=" + exp.toGMTString();
		 }
		evaluationForm.isDispAll.value="false";
		document.evaluationForm.action="/performance/evaluation/performanceEvaluation.do?b_query=query&operate=init";
	    document.evaluationForm.submit();   		
	}
}
//结果归档
function resultFiled()
{
	var planid=evaluationForm.planid.value;
	//检查是否计算
	var hashvo=new ParameterSet();
 	hashvo.setValue("planID",planid);
	var request=new Request({asynchronous:false,
	onSuccess:resultFiled1,functionId:'9024001008'},hashvo);
	
}
function resultFiled1(outparamters)
{
	var isExist=outparamters.getValue("isExist");
	var type=outparamters.getValue("type");
	if(isExist=='true')
	{	
		var info=outparamters.getValue("info");
		if(type=='1')
		{	
			alert(info);
			return;
		}else
		{
			if(!confirm(info))
				return;
		}	
	}

	resultFiled3();	
	/*
	var planid=evaluationForm.planid.value;
	//if(confirm(INPUT_VALIDATE_INFO9+'！\n'+INPUT_VALIDATE_INFO10+'?'))
	//{
		var hashvo=new ParameterSet();
 		hashvo.setValue("planID",planid);
		var request=new Request({asynchronous:false,
				onSuccess:resultFiled2,functionId:'9024001004'},hashvo);
	//}	*/
}
function resultFiled3()
{
	var planid=evaluationForm.planid.value;
	var busitype=evaluationForm.busitype.value;
	var theurl="/performance/evaluation/dealWithBusiness/resultFiled.do?b_query=link`dispBt=all`planid="+planid+"`busitype="+busitype;
    var iframe_url="/general/query/common/iframe_query.jsp?src="+$URL.encode(theurl);
   	//var return_vo= window.showModalDialog(iframe_url, 'resultFiled_win', 
    //  				"dialogWidth:700px; dialogHeight:480px;resizable:no;center:yes;scroll:yes;status:no");
   	
   	var config = {
	    width:750,
	    height:580,
	    type:'1',
	    id:'resultFiled3_win'
	}

	modalDialog.showModalDialogs(iframe_url,"resultFiled3_win",config,resultFiled3_ok);
}

function resultFiled3_ok(return_vo) {
	if(!return_vo)
		return false;	   
	if(return_vo.flag=="true")
	{
		if(return_vo.oper==2 || return_vo.oper==3)//归档成功
		{
			alert(INPUT_VALIDATE_INFO8+'！'); 
	  	 	evaluationForm.action="/performance/evaluation/performanceEvaluation.do?b_query=query&operate=init&code="+evaluationForm.code.value;
			evaluationForm.submit();
		}			
	}
}
function resultFiled2(outparamters)
{
	var isExist=outparamters.getValue("isExist");
	if(isExist=='true')
	{
		var objs=outparamters.getValue("objs");
		if(confirm(INPUT_VALIDATE_INFO6+':\n'+objs+'\n'+INPUT_VALIDATE_INFO7+'?'))
		{
			resultFiled3();    
		}
	}else if(isExist=='false')
		resultFiled3();	
}
//生成评语 
function generateRemark()
{
	var planid=evaluationForm.planid.value;
	var theurl="/performance/evaluation/dealWithBusiness/generateRemark.do?b_query=link`planid="+planid;
    var iframe_url="/general/query/common/iframe_query.jsp?src="+$URL.encode(theurl);
   	//var return_vo= window.showModalDialog(iframe_url, 'template_win', 
    //  				"dialogWidth:580px; dialogHeight:500px;resizable:no;center:yes;scroll:yes;status:no");
   	var config = {
	    width:580,
	    height:500,
	    type:'2'
	}

	modalDialog.showModalDialogs(iframe_url,"template_win",config,generateRemark_ok);
}

function generateRemark_ok(return_vo) {
	if(!return_vo)
		return false;
	
	if(return_vo.flag=="true")
	{
		alert(INPUT_VALIDATE_INF286); 	
		var objs=return_vo.objids;
		var order=return_vo.order;

		//在此刷新主页面，只出现objs指定的对象排序规则为order指定
		evaluationForm.order_str.value=order;
		//evaluationForm.objStr.value=objs;
		evaluationForm.action="/performance/evaluation/performanceEvaluation.do?b_query=query&operate=init&code="+evaluationForm.code.value;
		evaluationForm.submit();
	}
}

//结果分析
function resultAnalyse()
{

	//evaluationForm.target="il_body";
	//evaluationForm.action="/performance/perAnalyse.do?br_query=link&plan_id="+evaluationForm.planid.value+"&busitype="+evaluationForm.busitype.value;
    	// evaluationForm.submit();
    	 
    	 var strurl="/performance/perAnalyse.do?br_query=link&fromModule=evaluation&plan_id="+evaluationForm.planid.value+"&busitype="+evaluationForm.busitype.value;
	//var iframe_url="/general/query/common/iframe_query.jsp?src="+strurl; 
	//var ss=window.showModalDialog(iframe_url,arguments,"dialogWidth=700px;dialogHeight=500px;resizable=yes;scroll=no;status=no;");  
	window.open(strurl,"_ad","top=0,left=5,height="+(window.screen.height-70)+",width="+(window.screen.width-20));
}

//统一打分
function rate()
{	
	window.parent.location=evaluationForm.action="/performance/evaluation/performanceEvaluation.do?br_uniteScore=link";
    

}
//票数计算
function voteCompute(code)
{
	var planid=evaluationForm.planid.value;
	var hashvo=new ParameterSet();
 	hashvo.setValue("planid",planid);
 	hashvo.setValue("code",code);
 	hashvo.setValue("testTable","1");
	var request=new Request({asynchronous:false,onSuccess:voteCompute2,functionId:'9024000019'},hashvo);
	
}
var code_voteComputer2 = '';
function voteCompute2(outparamters)
{
	var isExist=outparamters.getValue("isExist");
	if(isExist=='0')
	{
		alert(KH_EVALUATION_INFO);
		return;
	}
	code_voteComputer2=outparamters.getValue("code");
	var planid=evaluationForm.planid.value;
	var theurl="/performance/evaluation/performanceEvaluation.do?b_votecalcu=link`planid="+planid;
    var iframe_url="/general/query/common/iframe_query.jsp?src="+$URL.encode(theurl);
    //var return_vo= window.showModalDialog(iframe_url, 'template_win', 
	//	"dialogWidth:400px; dialogHeight:300px;resizable:no;center:yes;scroll:yes;status:no");
    
    var config = {
	    width:420,
	    height:300,
	    type:'1',
	    id:"voteCompute2_win"
	}

	modalDialog.showModalDialogs(iframe_url,"voteCompute2_win",config,voteCompute2_ok);
	
}

function voteCompute2_ok(return_vo){
	if(!return_vo)
		return false;	   
	if(return_vo.flag=="true")
	{	
		document.evaluationForm.action="/performance/evaluation/performanceEvaluation.do?b_query=query&operate=init&code="+code_voteComputer2;
	    document.evaluationForm.submit();
	}
}

function voteCompute2_close(){
	Ext.getCmp("voteCompute2_win").close();
}
function voteStatis(code)
{
	if(confirm(KH_EVALUATION_INFO2))
	{
		document.evaluationForm.action="/performance/evaluation/performanceEvaluation.do?b_query=query&voteStatis=1&operate=init&code="+code;
		document.evaluationForm.submit();	
	}
	
}
//打印登记表
function printcard()
{
	var planid=evaluationForm.planid.value;
	var templateid = evaluationForm.templateid.value;
//	//"/general/card/searchcard.do?b_query=link`home=2`inforkind=5`result=0`temp_id="+templateid+"`plan_id="+planid;
	var theurl="/module/card/cardCommonSearch.jsp?inforkind=5`temp_id="+templateid+"`plan_id="+planid;
    var iframe_url="/general/query/common/iframe_query.jsp?src="+$URL.encode(theurl);
	var return_vo= window.open(iframe_url, 'template_win', 
      			"width="+window.screen.width+",height="+(window.screen.height-100)+",top=0,left=0,resizable=yes,scrollbars=yes,status=no");
}
//打印高级花名册
function printHighroster()
{
	var planid=evaluationForm.planid.value;

}
//发送考核反馈表
function sendBackTables()
{
	var _backTables=evaluationForm.showBackTables.value;
	if(_backTables=='')
	{
		if(evaluationForm.busitype.value!=null && evaluationForm.busitype.value=='1')
			alert(SEND_BACKTABLES_COMPET);
		else
			alert(SEND_BACKTABLES_INFO);
		return;
	}
	
	var hashvo=new ParameterSet();
	hashvo.setValue("plan_id",evaluationForm.planid.value);
	hashvo.setValue("backTables",_backTables);
	hashvo.setValue("oper","test");
	var request=new Request({method:'post',asynchronous:true,onSuccess:testSend,functionId:'9023000025'},hashvo);
		
}
function testSend(outparamters)
{	
	var resultFlag = outparamters.getValue("resultFlag");
	var errorInfo = outparamters.getValue("errorInfo");
	if(resultFlag=='0')
	{
		alert(getDecodeStr(errorInfo));
	}
	else if(resultFlag=='1')
	{
		if(confirm(SEND_BACKTABLES_INFO2))
		{
			jinduo();
			var hashvo=new ParameterSet();
			hashvo.setValue("plan_id",evaluationForm.planid.value);
			hashvo.setValue("backTables",evaluationForm.showBackTables.value);
			hashvo.setValue("oper","sendBackTables");
			var request=new Request({method:'post',asynchronous:true,onSuccess:sendEmail,functionId:'9023000025'},hashvo);
		}
	}
}
function sendEmail(outparamters)
{	
	var resultFlag = outparamters.getValue("resultFlag");
	var errorInfo = outparamters.getValue("errorInfo");
	var waitInfo=eval("wait");	
	if((resultFlag=='0'))
	{
		waitInfo.style.display="none";
		alert(getDecodeStr(errorInfo));
	}else if(resultFlag=='1')
	{
		waitInfo.style.display="none";
		alert(KH_IMPLEMENT_INFO8);
	}else if(resultFlag=='2')
	{
		waitInfo.style.display="none";
		alert(KH_IMPLEMENT_INF17);//"邮件发送失败,可能的原因:\r\n  1.邮件服务器设置错误.\r\n  2.请检查网络连接.";
	}else
	{
		waitInfo.style.display="none";
		alert('输入信息不正确！');
	}
}
function return_bt()
{
	evaluationForm.action="/performance/kh_plan/performPlanList.do?b_query=return&currentPlanid="+evaluationForm.planid.value;
	evaluationForm.target="il_body";
	evaluationForm.submit();
}
function jinduo()
{	
	var waitInfo1;
	waitInfo1=eval("wait");
	waitInfo1.style.display="block";
}
//批量导出绩效报告
function batchExportReport()
{
	var strurl="/performance/evaluation/performanceEvaluation.do?br_jxReportInfo=link";
	var iframe_url="/general/query/common/iframe_query.jsp?src="+strurl; 
//	var return_vo=window.showModalDialog(iframe_url,arguments,"dialogWidth=550px;dialogHeight=500px;resizable=yes;scroll=no;status=no;");
    var config = {
        width:550,
        height:500,
        type:'2'
    }
    modalDialog.showModalDialogs(iframe_url,"batchExportReport",config,batchExportReport_ok);

}

function batchExportReport_ok(return_vo){
    if(return_vo!=null && return_vo.ok==1)
    {
       var strurl="/performance/evaluation/performanceEvaluation.do?br_batchExportReport=link";
       var iframe_url="/general/query/common/iframe_query.jsp?src="+strurl;
       // return_vo=window.showModalDialog(iframe_url,arguments,"dialogWidth=460px;dialogHeight=180px;resizable=no;scroll=no;status=no;");
        var config = {
            width:460,
            height:180,
            type:'2'
        }
        modalDialog.showModalDialogs(iframe_url,"batchExportReport_ok",config);
    }
}

// 能力素质模型推送考核对象考核不合格的指标关联的课程:所有对象所有指标同时推送
function sendLessons()
{		
	var hashvo=new ParameterSet();
	hashvo.setValue("hjsoft","multiplePoint");
	hashvo.setValue("plan_id",evaluationForm.planid.value);
//	hashvo.setValue("khObjScope",getEncodeStr(document.evaluationForm.khObjWhere2.value));
//	hashvo.setValue("backTables",_backTables);
//	hashvo.setValue("oper","test");
	var request=new Request({method:'post',asynchronous:true,onSuccess:propellingSuc,functionId:'9023000297'},hashvo);		
}
function propellingSuc(outparamters)
{	
	var flag = outparamters.getValue("flag");
	if(flag=='ok')
		alert("推送成功！");
	else if(flag=='hjsoft')
		alert("课程已存在，不允许重复推送！");
	else if(flag=='nook')
		alert("没有要推送的课程！");
	else
		alert("推送失败！");	
}
//批量导入
function batchImport(code)
{
 			inputexcel(code);

}
//下载修正分值的批量导入模板
function exportTemplate()
{
	var plan_id=document.evaluationForm.planid.value;
	var object_type=document.evaluationForm.object_type.value;
	var hashvo=new ParameterSet();
	hashvo.setValue("plan_id",plan_id);
	hashvo.setValue("object_type",object_type);
	var request=new Request({method:'post',asynchronous:false,onSuccess:outIsOk,functionId:'9024000298'},hashvo);
}
//右键单击鼠标显示菜单。郭峰添加  现在已经不用了
function showMenu()
	{
		 var obj=document.getElementById('menu_');
		 if(obj!=null)
		 {
		 	obj.style.display="block";
		 	obj.style.position="absolute";
		 	obj.style.posLeft=event.clientX;	
		 	obj.style.posTop=event.clientY;
		 	document.getElementById("menu_").focus();
		 }
	}
	//隐藏右键弹出的菜单 郭峰 现在已经不用了
	function hiddenElement()
	{
   		 setTimeout("closeMenu()",500);
		
	}
	//让菜单消失  郭峰 现在已经不用了
	function closeMenu()
	{
		 var obj=document.getElementById('menu_');
		 if(obj!=null)
		 {
		 	obj.style.display="none";
		 }
	}
	//处理得分明细
	function scoreDetail()
	{
		//document.evaluationForm.action="/performance/evaluation/performanceEvaluation.do?b_scoreDetail=link&object_id="+selectObjectId+"&showWays=0";
		//document.evaluationForm.submit();
		//alert("fdsfdfdsf"+selectObjectId_extra);
		if(selectObjectId_extra=="init")
		{
			alert("请选择考核对象");
			return;
		}
		window.open("/performance/evaluation/performanceEvaluation.do?b_scoreDetail=link&recheckObjectid="+selectObjectId_extra+"&showWays=0");
	}
	//显示卡片
	function showCard()
	{
		if(selectObjectId_extra=="init")
		{
			alert("请选择考核对象");
			return;
		}
		//window.open("/performance/evaluation/performanceEvaluation.do?b_showCard=link&cardObject_id="+selectObjectId_extra+"&scoreExplainFlag=0&fromjs=1");
		strurl="/performance/evaluation/performanceEvaluation.do?b_showCard=link`cardObject_id="+selectObjectId_extra+"`scoreExplainFlag=0`fromjs=1";
		iframe_url="/general/query/common/iframe_query.jsp?src="+$URL.encode(strurl);
		/*if(isIE6()){
			window.showModalDialog(iframe_url,arguments,"dialogWidth=810px;dialogHeight=620px;resizable=no;scroll=no;status=no;"); 
		}else{
			window.showModalDialog(iframe_url,arguments,"dialogWidth=800px;dialogHeight=600px;resizable=no;scroll=no;status=no;"); 
		}*/
		var config = {
		    width:810,
		    height:640,
		    type:'1',
		    id:'showCard_win'
		}

		modalDialog.showModalDialogs(iframe_url,"showCard_win",config,"");
	}
	function showCardDetail()
	{
		if(selectObjectId_extra=="init")
		{
			alert("请选择考核对象");
			return;
		}
		strurl="/performance/evaluation/performanceEvaluation.do?b_showCardDetail=link&cardObject_id="+selectObjectId_extra+"&scoreExplainFlag=0&fromjs=1";
		evaluationForm.action=strurl;
		evaluationForm.target="ril_body2";
        evaluationForm.submit();  
	}
	//总体评价
	function totalEvaluate()
	{
		if(selectObjectId_extra=="init")
		{
			alert("请选择考核对象");
			return;
		}
		//window.open("/performance/evaluation/performanceEvaluation.do?b_showCard=link&cardObject_id="+selectObjectId_extra+"&scoreExplainFlag=0&fromjs=1");
		strurl="/performance/evaluation/performanceEvaluation.do?b_totalEvaluate=link`totalevaluateObject="+selectObjectId_extra+"`fromjs=1";
		iframe_url="/general/query/common/iframe_query.jsp?src="+$URL.encode(strurl);
		//window.showModalDialog(iframe_url,"","dialogWidth=800px;dialogHeight=600px;resizable=no;scroll=no;status=no;");  
		
		var config = {
		    width:810,
		    height:600,
		    type:'1',
		    id:'totalEvaluate_win'
		}

		modalDialog.showModalDialogs(iframe_url,"totalEvaluate_win",config,"");
	}