function checkTime(times){
 	var result=times.match(/^(\d{1,4})(-|\/)(\d{1,2})\2(\d{1,2})$/);
 	if(result==null) return false;
 	var d= new Date(result[1], result[3]-1, result[4]);
 	return (d.getFullYear()==result[1]&&(d.getMonth()+1)==result[3]&&d.getDate());
}
function timeCheck(obj){
	if(!checkTime(obj.value)){
		obj.value='';
	}
}
function validate_self(obj,aitemdesc)  //zhangcq 2016-4-25 时间校验
{	
	var dd=true;
	var itemdesc="";
	if(aitemdesc==null||aitemdesc==undefined)
		itemdesc="日期";
	else 
		itemdesc=aitemdesc;

	if(trim(obj.value).length!=0)
	{						
		var myReg =/^(-?\d+)(\.\d+)?$/
		if(IsOverStrLength(obj.value,10))
		{
			alert(itemdesc+" 格式不正确,正确格式为yyyy-mm-dd ！");
			return false;
		}
		else
		{
			if(trim(obj.value).length!=10)
			{
				alert(itemdesc+" 格式不正确,正确格式为yyyy-mm-dd ！");
				return false;
			}
			var year=obj.value.substring(0,4);
			var month=obj.value.substring(5,7);
			var day=obj.value.substring(8,10);
			var midLine1=obj.value.substring(4,5);
			var midLine2=obj.value.substring(7,8);
			if(!myReg.test(year)||!myReg.test(month)||!myReg.test(day)) 
			{
				alert(itemdesc+" 格式不正确,正确格式为yyyy-mm-dd ！");
				return false;
			}
			if(year<1900||year>2100)
			{
				alert(itemdesc+" 年范围为1900~2100！");
				return false;
			}
							 	
			if(!isValidDate(day, month, year))
			{
				alert(itemdesc+"错误，无效时间！");
				return false;
			}
			if(midLine1!="-" || midLine2!="-"){
                     alert(itemdesc+"格式不正确,正确格式为yyyy-mm-dd ！");
				return false;
			}
		}
	}
	return dd
}
function timeFlagChange(obj,model,a_code){
	var timeflag = obj.value;
	if(timeflag=='04'){
		toggles("viewtime");
		toggles("viewtime1");
		return false;
	}else{
		document.getElementById("startime").value="";
		document.getElementById("endtime").value="";
		changesReload(model,a_code);
	}
}
function changesReload(model,a_code){
	var startTime = document.getElementById("editor1").value;//zhangcq 2016-4-25 时间校验
	var endTime = document.getElementById("editor2").value;
	
	if(trim(startTime)!='')
    {	
    	 if(!validate_self(document.getElementById("editor1"),'起始日期'))
    	 		return false;    	
    }
    if(trim(endTime)!='')
    {
    	if(!validate_self(document.getElementById("editor2"),'结束日期'))
    	 		return false;
   	}	
	
	
    if(startTime!='' && endTime!='')
    	if(startTime>endTime)	
    	{
    		alert("起始时间不能大于结束时间");
    	    return;
    	}	
	planTrainForm.action="/train/b_plan/planTrain.do?b_query=link&model="+model+"&a_code="+a_code;
	planTrainForm.submit();  	
}
function searchGeneral(searchstr,a_code,model){
	document.getElementById("searchstr").value=searchstr;
    changesReload(model,a_code);
}
function searchInform(model,a_code,type){
	var thecodeurl ="/train/b_plan/gmsearch.do?b_query=link&tablename=r25&&type="+type; 
	var return_vo= window.showModalDialog(thecodeurl, "", 
              	"dialogWidth:700px; dialogHeight:430px;resizable:no;center:yes;scroll:no;status:no");
    if(return_vo!=null){
    	document.getElementById("searchstr").value=return_vo;
    }
    changesReload(model,a_code);
}
function searchComm(type,model,a_code){
	var thecodeurl ="/train/b_plan/gmsearch.do?b_comm=link&type="+type; 
	var return_vo= window.showModalDialog(thecodeurl, "", 
              	"dialogWidth:400px; dialogHeight:300px;resizable:no;center:yes;scroll:no;status:no");
    if(return_vo!=null){
    	document.getElementById("searchstr").value=return_vo;
    }
    changesReload(model,a_code);
}
function append(model,a_code){
	var hashvo=new ParameterSet();
	hashvo.setValue("a_code",a_code);	
	hashvo.setValue("model",model);	
	var request=new Request({method:'post',asynchronous:false,onSuccess:setAppendRecord,functionId:'2020050004'},hashvo);
}
function setAppendRecord(outparamters){
	var tablename,table,dataset,record;
	tablename="tabler25";
	table=$(tablename);
	dataset=table.getDataset(); 
	record=dataset.getCurrent();  
	dataset.insertRecord("end");	
	record=dataset.getCurrent();
    record.setValue("r2501",outparamters.getValue("r2501")); 
    record.setValue("b0110",outparamters.getValue("b0110")); 
	record.setValue("e0122",outparamters.getValue("e0122")); 
    record.setValue("r2509",outparamters.getValue("r2509"));  
    record.setValue("r2502",outparamters.getValue("r2502"));  	
    record.setValue("r2508",outparamters.getValue("r2508")); 
    record.setValue("r2503",outparamters.getValue("r2503")); 
    record.setValue("model",outparamters.getValue("model"));  	 	
}
function sortRecord(model,a_code){
	var thecodeurl ="/train/traincourse/traindata.do?b_sort=link&model="+model+"&fieldsetid=r25"; 
	var return_vo= window.showModalDialog(thecodeurl, "", 
              	"dialogWidth:350px; dialogHeight:350px;resizable:no;center:yes;scroll:no;status:no");
    if(return_vo!=null){
    	changesReload(model,a_code);
    }
}
function setPageFormat(username){
   	var param_vo=oageoptions_selete("3",username);
}
//培训计划添加
function addPlan(model,a_code)
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
	if(model=='1'){
		initValue='r2509:01,r2512:02,r2503:'+currentYear+',r2508:'+now;//计划制定 初始值
		hideFilds ='r2512,r2513,';//隐藏字段r3111,r3112,
	}else if(model=='2'){
		initValue='r2509:03,r2512:01,r2503:'+currentYear+',r2508:'+now;//计划审批 初始值
		hideFilds ='r2512,';//隐藏字段r3111,r3112,
	}
	
	readonlyFilds='b0110,e0122,r2504,r2505,r2509,';//只读字段
	//hideFilds ='';//隐藏字段r3111,r3112,
    hidepics = 'imgr2509,';//需要隐藏的字段旁边的图片	
	isUnUmRela='true';
	
	var theurl="/train/traincourse/traindataAdd.do?b_query=link`fieldset=r25`a_code="+a_code+'`initValue='+getEncodeStr(initValue)+'`readonlyFilds='+readonlyFilds+'`hideFilds='+hideFilds+'`hidepics='+hidepics+'`isUnUmRela='+isUnUmRela;
    var iframe_url="/general/query/common/iframe_query.jsp?src="+$URL.encode(theurl);
   	var return_vo= window.showModalDialog(iframe_url, 'addPlan_win', 
      				"dialogWidth:800px; dialogHeight:400px;resizable:no;center:yes;scroll:yes;status:no");	
         		
   if(return_vo==null)
		return false;	   
   if(return_vo.flag=="true")
	{   
		planTrainForm.action="/train/b_plan/planTrain.do?b_query=link&a_code="+a_code+"&model="+model;
		planTrainForm.submit();	
	}     					
}

