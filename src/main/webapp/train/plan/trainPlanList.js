var rId;
function checkTime(times){
	var result=times.match(/^(\d{1,4})(-|\/)(\d{1,2})\2(\d{1,2})$/);
	if(result==null)
		return false;
	var d= new Date(result[1], result[3]-1, result[4]);
	return (d.getFullYear()==result[1]&&(d.getMonth()+1)==result[3]&&d.getDate());
}
function timeCheck(obj){
	if(!checkTime(obj.value)){
		obj.value='';
	}
}
function sub(codeSet,codeID){
	trainMovementForm.action="/train/plan/searchCreatPlanList.do?b_query=query&codeset="+codeSet+"&code="+codeID+"&model=2";
	trainMovementForm.submit();
}
function display(state,timeFlag){
	trainMovementForm.action="/train/plan/searchCreatPlanList.do?b_query=link&model=1";
	trainMovementForm.submit();
}
function timeFlagChange(obj,codeSet,codeID){
	var timeflag = obj.value;
	if(timeflag=='5'){
		toggles("viewtime");
		toggles("viewtime1");
		return false;
	}else{
		document.getElementById("startTime").value="";
		document.getElementById("endTime").value="";
		sub(codeSet,codeID);
	}
}
function setPageFormat(username){
	var param_vo=oageoptions_selete("3",username);
}
function append(codeSet,codeID){
	var hashvo=new ParameterSet();
	hashvo.setValue("a_code",codeSet+codeID);	
	hashvo.setValue("model","3");	
	var request=new Request({method:'post',asynchronous:false,onSuccess:setAppendRecord,functionId:'2020020212'},hashvo);
}
function setAppendRecord(outparamters){
	rId=outparamters.getValue("r3101");
	var tablename,table,dataset,record;
	tablename="tabler31";
	table=$(tablename);
	dataset=table.getDataset(); 
	record=dataset.getCurrent(); 
	dataset.insertRecord("begin");	
	record=dataset.getCurrent();
	record.setValue("r3101",outparamters.getValue("r3101")); 
	record.setValue("b0110",outparamters.getValue("b0110")); 
	record.setValue("e0122",outparamters.getValue("e0122")); 
	record.setValue("r3127",outparamters.getValue("r3127"));  
	record.setValue("r3130",outparamters.getValue("r3130"));
	if(record.getString("r3117"))
		record.setValue("r3117","活动说明");
	record.setState("modify");
	var r3118 = outparamters.getValue("r3118");
	if(r3118!=null&&r3118.length>8) 	
		record.setValue("r3118",outparamters.getValue("r3118"));   	 	

}	
//输出 EXCEL OR PDF
function showfile(outparamters){
	var outName=outparamters.getValue("outName");
	var flag=outparamters.getValue("flag");
	window.location.target="_blank";
	window.location.href="/servlet/vfsservlet?fromjavafolder=true&fileid="+outName;
}
/*
*flag 1:pdf  2:excel
*/
function executeOutFile(flag,tlname,fieldSize){
	var tablename,table,size,fieldWidths,whl_sql;       
	tablename="table"+tlname;
	table=$(tablename);
	size=fieldSize;
	for(var i=1;i<size+2;i++){
		var width=table.getColWidth(table,i);
		if(width!=null)
			fieldWidths+="/"+width;
	}
	var hashvo=new ParameterSet();
	hashvo.setValue("fieldWidths",fieldWidths.substring(10));
	hashvo.setValue("tablename",tlname);
	var In_paramters="flag="+flag;  
	var request=new Request({method:'post',asynchronous:false,parameters:In_paramters,onSuccess:showfile,functionId:'0521010011'},hashvo);
}
function goback(){
	window.open("/templates/menu/train_menu.do?b_query=link&module=2","i_body")
}

function doExamint(opt,tlname){
	var tablename="table"+tlname;
	table=$(tablename);
	dataset=table.getDataset();
	var record=dataset.getFirstRecord();
	var selectID="";	
	var num=0;	
	var noNum=0;
	while (record){
		if (record.getValue("select")){							
			num++;
			if(record.getValue("r3127")=='04'||record.getValue("r3127")=='05'||record.getValue("r3127")=='06'){
				selectID=record.getValue("r3101");
			}else
				noNum++;	    
		}
		record=record.getNextRecord();
	}  	
	
	if(num>1){
		alert(SELECT_ONE_PLAN_ACTIVE);
		return;
	}
	
	if(noNum>0){
		alert(VIEW_PLAN_SYS_RESULT);
		return;
	}
	
	if(selectID == null || selectID.length < 1){
		alert(SELECT_TRAIN_CLASS);
		return;
	}
	
	var hashvo=new ParameterSet();
	hashvo.setValue("opt",opt);
	var In_paramters="r3101="+selectID;  
	var request=new Request({method:'post',asynchronous:false,parameters:In_paramters,onSuccess:returndoExamint,functionId:'2020020118'},hashvo);
	
}
function returndoExamint(outparamters){
	var id=outparamters.getValue("mdid");
	var sql="/selfservice/infomanager/askinv/searchendview.do?b_query=link&id="+id+"&f=1";
	window.open(sql,"");
}
function returnlookResult(outparamters){
	var r3101=outparamters.getValue("r3101");
	var templateid=outparamters.getValue("templateid");
	document.trainMovementForm.action="/train/evaluatingStencil.do?b_analyse=link&type=direct&r3101="+r3101+"&templateid="+templateid;
	document.trainMovementForm.target="_blank";
	document.trainMovementForm.submit();
}
// 1: 活动评估结果   2:教师评估结果
function lookResult(opt,tlname){
	var tablename="table"+tlname;
	table=$(tablename);
	dataset=table.getDataset();
	var record=dataset.getFirstRecord();
	var selectID="";	
	var num=0;	
	var noNum=0;
	while (record){
		if (record.getValue("select")){							
			num++;
			if(record.getValue("r3127")=='04'||record.getValue("r3127")=='05'
				||record.getValue("r3127")=='06'){
				selectID=record.getValue("r3101");
			}else
				noNum++;	    
		}
		record=record.getNextRecord();
	}  	
	
	if(num>1){
		alert(SELECT_ONE_PLAN_ACTIVE);
		return;
	}
	
	if(noNum>0){
		alert(VIEW_PLAN_SYS_RESULT);
		return;
	}
	
	if(selectID == null || selectID.length < 1){
		alert(SELECT_TRAIN_CLASS);
		return;
	}
	
	var hashvo=new ParameterSet();
	hashvo.setValue("opt",opt);
	var In_paramters="r3101="+selectID;  
	var request=new Request({method:'post',asynchronous:false,parameters:In_paramters,onSuccess:returnlookResult,functionId:'2020020118'},hashvo);
	
}

function display(var1,var2){
	if(var1=='time'){
		document.trainMovementForm.timeFlag.value=var2;
		if(var2=='5'){
			var var3=selectTimeArea();
			if(var3.length>=1){
				document.trainMovementForm.startTime.value=var3[0];
				document.trainMovementForm.endTime.value=var3[1];
			}
		}
	}else if(var1=='state'){
		document.trainMovementForm.stateFlag.value=var2;
	}
	document.trainMovementForm.action="/train/plan/searchCreatPlanList.do?b_query=link&model=2&operate=link";
	document.trainMovementForm.submit();
}
function editPlan(){
	var planID="";
	planID=document.trainMovementForm.trainPlanID.value;
	if(planID!=0&&planID!=-1){
		document.trainMovementForm.action="/train/plan/searchCreatPlanList.do?b_edit=link&planid="+planID+"&operator=edit";
		document.trainMovementForm.submit();
	}
}
//查询
function query(){
	var hashvo=new ParameterSet();
	hashvo.setValue("opt","query");
	var In_paramters="tableName=R31"; 	
	var request=new Request({method:'post',asynchronous:false,parameters:In_paramters,onSuccess:ReturnQuery,functionId:'3000000104'},hashvo);			
}
function ReturnQuery(outparamters){
	var fields_temp=outparamters.getValue("fields");		
	var fields=new Array();
	for(var i=0;i<fields_temp.length;i++){
		////数组[ 0:列名 1:列描述  2:列的类型 3:如果为代码型,则为代码值,否则为空 ]
		var a_field=fields_temp[i].split("<@>");
		fields[i]=a_field
	}
	var extendSql=generalQuery("R31",fields);
	if(extendSql){
		document.trainMovementForm.extendSql.value=extendSql;
		trainMovementForm.action='/train/plan/searchCreatPlanList.do?b_query=link&model=2&operate=link';
		trainMovementForm.submit();
	}
}
function searchInform(){
	var thecodeurl ="/train/traincourse/generalsearch.do?b_query=link&fieldsetid=r31"; 
	var return_vo= window.showModalDialog(thecodeurl, "", 
              	"dialogWidth:700px; dialogHeight:410px;resizable:no;center:yes;scroll:no;status:no");
    if(return_vo!=null){
    	document.trainMovementForm.extendSql.value=return_vo;
    	trainMovementForm.action='/train/plan/searchCreatPlanList.do?b_query=link&model=2&operate=link';
    	trainMovementForm.submit();
    }
}
//排序
function taxis(){
	var hashvo=new ParameterSet();
	hashvo.setValue("opt","taxis");
	var In_paramters="tableName=R31"; 	
	var request=new Request({method:'post',asynchronous:false,parameters:In_paramters,onSuccess:ReturnTaxis,functionId:'3000000104'},hashvo);			
}
function ReturnTaxis(outparamters){
	var fields_temp=outparamters.getValue("fields");		
	var fields=new Array();
	for(var i=0;i<fields_temp.length;i++){
		////数组[ 0:列名 1:列描述  2:列的类型 3:如果为代码型,则为代码值,否则为空 ]
		var a_field=fields_temp[i].split("<@>");
		fields[i]=a_field
	}
	var orderSql=taxisDialog("R31",fields);
	if(orderSql){
		document.trainMovementForm.orderSql.value=orderSql;
		trainMovementForm.action='/train/plan/searchCreatPlanList.do?b_query=link&model=2&operate=link';
		trainMovementForm.submit();
	}
}
//issuePlan('${trainMovementForm.tablename}');
function issuePlan(tlname){
	var tablename,table,dataset,record;
	tablename="table"+tlname;
	table=$(tablename);
	
	dataset=table.getDataset();
	record=dataset.getFirstRecord();
	var selectID="";	
	var isUsed=0;	
	var noNum=0;
	while (record) {
		if (record.getValue("select")){							
			if(record.getValue("r3127")=='01'||record.getValue("r3127")=='07'){
				isUsed=1;
				noNum++;
			}
		else if(record.getValue("r3127")=='03')
			selectID+="^"+record.getValue("r3101");	
		}
		record=record.getNextRecord();
	}  	
	if(noNum>0){
		alert(PLAN_APP_SYS_FILTER);
	}
	document.trainMovementForm.action="/train/plan/searchCreatPlanList.do?b_issue=link&selectIDs="+selectID;
	document.trainMovementForm.submit();
}
//培训计划添加 add
function addPlan(a_code)
{
	var initValue='';
	var readonlyFilds='';
	var hideFilds ='';
	var hidepics = '';
	
	var date = new Date(); 
	var currentYear=date.getFullYear();
	var currentMonth=date.getMonth()+1;
	var currentDay=date.getDate();
	var now = currentYear+'-'+currentMonth+'-'+currentDay;
	initValue='r2509:03,r2512:01,r2503:'+currentYear+',r2508:'+now;//计划审批 初始值
	
	readonlyFilds='b0110,e0122,r2504,r2505,r2509,r2512,';//只读字段
	hideFilds ='';//隐藏字段r3111,r3112,
    hidepics = 'imgr2509,imgr2512,';//需要隐藏的字段旁边的图片	
	isUnUmRela='true';
	
	var theurl="/train/traincourse/traindataAdd.do?b_query=link`fieldset=r25`a_code="+a_code+'`initValue='+getEncodeStr(initValue)+'`readonlyFilds='+readonlyFilds+'`hideFilds='+hideFilds+'`hidepics='+hidepics+'`isUnUmRela='+isUnUmRela;
    var iframe_url="/general/query/common/iframe_query.jsp?src="+$URL.encode(theurl);
   	var return_vo= window.showModalDialog(iframe_url, 'addPlan_win', 
      				"dialogWidth:800px; dialogHeight:400px;resizable:no;center:yes;scroll:yes;status:no");	
    if(return_vo!=null){
    	document.trainMovementForm.action="/train/plan/searchCreatPlanList.do?b_query=link&model=2"
    	document.trainMovementForm.submit();
    }
}

function adjustPlan(tlname){
	var tablename,table,dataset,record;
	tablename="table"+tlname;
	table=$(tablename);
	dataset=table.getDataset();
	record=dataset.getFirstRecord();
	var selectID="";	
	var isUsed=0;	
	var noNum=0;
	var selectedID="";
	var selid=0;
	while (record) {
		if (record.getValue("select")){						
			if(record.getValue("r3127")!='03'){
				isUsed=1;
				noNum++;
			}else if(record.getValue("r3127")=='03'){
				selectID+="#"+record.getValue("r3101");	
				selectedID+="*"+record.getValue("r3101");	
			}
			selid++;
		}
		record=record.getNextRecord();
	}
	if(selid<1){
		alert(SELECT_TRAIN_CLASS);
	}  	
	if(noNum>0){
		alert(PLAN_SELECT_SYS_FILTER);
	}else if(selectID.length>0){
		var planID=selectTrainPlan(selectedID);
		if(planID&&planID!=null){
			document.trainMovementForm.trainPlanID.value=planID;
			document.trainMovementForm.selectIDs.value=selectID;
			document.trainMovementForm.action="/train/plan/searchCreatPlanList.do?b_insertPlan=link";
			document.trainMovementForm.submit();
		}       		
	}
}
