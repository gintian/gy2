function toggles(targetId){
	if (document.getElementById(targetId)){
		target = document.getElementById(targetId);
		target.style.display = "block";
	}
} 
function hides(targetId){
	if (document.getElementById(targetId)){
		target = document.getElementById(targetId);
		target.style.display = "none";
	}
}

function selectYear(obj){
	var s_year=obj.value;
	budgetExecRateForm.action="/gz/gz_budget/budget_execrate.do?b_init=resubmityear";
	budgetExecRateForm.target='_parent';
	budgetExecRateForm.submit();
}
function selectMonth(obj){
	var s_month=obj.value;
	budgetExecRateForm.action="/gz/gz_budget/budget_execrate.do?b_query=resubmitmonth";
	budgetExecRateForm.target='_self';
	budgetExecRateForm.submit();
}

function downloadTemplate(tabid,flag){
	 var hashvo=new ParameterSet();
     hashvo.setValue("tab_id",tabid);
     hashvo.setValue("modelflag","downSingle");
     hashvo.setValue("B0110",budgetExecRateForm.b0110.value);
     hashvo.setValue("budgetyear",budgetExecRateForm.budgetYear.value);
     hashvo.setValue("budgetmonth",budgetExecRateForm.budgetMonth.value);
     var request=new Request({asynchronous:false,onSuccess:download,functionId:'302001020227'},hashvo);	

}
function download(outparameters){
	var filename=outparameters.getValue("fileName");
	if (filename=="undefined")  return;
	filename=getDecodeStr(filename);
    var win=open("/servlet/vfsservlet?fileid="+filename+"&fromjavafolder=true","excel");
}

function imports(tab_id){
	budgetExecRateForm.action="/gz/gz_budget/budget_execrate.do?br_selectfile=open&tab_id="+tab_id;
	budgetExecRateForm.submit();
}

function expbatch(tabid){
	 var hashvo=new ParameterSet();
     hashvo.setValue("modelflag","expBatch");
     hashvo.setValue("B0110",budgetExecRateForm.b0110.value);
     hashvo.setValue("budgetyear",budgetExecRateForm.budgetYear.value);
     hashvo.setValue("budgetmonth",budgetExecRateForm.budgetMonth.value);
     var request=new Request({asynchronous:false,onSuccess:download,functionId:'302001020227'},hashvo);	

}
function statistics(tabid,str){
	if(confirm(str))
    {
    	var hashvo=new ParameterSet();
	    hashvo.setValue("tab_id",tabid);
	    hashvo.setValue("modelflag","Statistics");
	    hashvo.setValue("B0110",budgetExecRateForm.b0110.value);
	    hashvo.setValue("budgetyear",budgetExecRateForm.budgetYear.value);
	    hashvo.setValue("budgetmonth",budgetExecRateForm.budgetMonth.value);
	    var request=new Request({asynchronous:false,onSuccess:statistics_ok,functionId:'302001020227'},hashvo);	
    }
}
function statistics_ok(outparameters){
	var isactualdata=outparameters.getValue("isactualdata");
	var tab_id=outparameters.getValue("tab_id");
	if (isactualdata=="false")  return;
	budgetExecRateForm.action="/gz/gz_budget/budget_execrate.do?b_query=query&tab_id="+tab_id;
	budgetExecRateForm.submit();
}