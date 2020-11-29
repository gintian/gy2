function checkTime(times){
 	var result=times.match(/^(\d{1,4})(-|\/)(\d{1,2})\2(\d{1,2})$/);
 	if(result==null) return false;
 	var d= new Date(result[1], result[3]-1, result[4]);
 	return (d.getFullYear()==result[1]&&(d.getMonth()+1)==result[3]&&d.getDate());
	
}
function timeCheck(obj){
	/*if(!checkTime(obj.value)){
		obj.value='';
	}*/  
		
	             
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
	var startTime = document.getElementById("editor1").value;
	var endTime = document.getElementById("editor2").value;
	
	if(trim(startTime)!='')
    {	
    	 if(!validate_self(document.getElementById("editor1"),'起始日期'))//zhangcq 2016-4-25 时间校验
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
	courseTrainForm.action="/train/request/trainsData.do?b_query=link&vflag=0&model="+model+"&a_code="+a_code;
	courseTrainForm.submit();  	
}
function searchInform(model,a_code){
	var thecodeurl ="/train/traincourse/generalsearch.do?b_query=link&fieldsetid=r31"; 
	var return_vo= window.showModalDialog(thecodeurl, "", 
              	"dialogWidth:700px; dialogHeight:410px;resizable:no;center:yes;scroll:no;status:no");
    if(return_vo!=null){
    	document.getElementById("searchstr").value=return_vo;
    	changesReload(model,a_code);
    }
}
function append(model,a_code){
	var hashvo=new ParameterSet();
	hashvo.setValue("a_code",a_code);	
	hashvo.setValue("model",model);	
	var request=new Request({method:'post',asynchronous:false,onSuccess:setAppendRecord,functionId:'2020020212'},hashvo);
}
function setAppendRecord(outparamters){
	var tablename,table,dataset,record;
	tablename="tabler31";
	table=$(tablename);
	dataset=table.getDataset(); 
	record=dataset.getCurrent();  
	dataset.insertRecord("end");	
	record=dataset.getCurrent();
    record.setValue("r3101",outparamters.getValue("r3101")); 
    record.setValue("b0110",outparamters.getValue("b0110")); 
	record.setValue("e0122",outparamters.getValue("e0122")); 
    record.setValue("r3127",outparamters.getValue("r3127"));  
    record.setValue("r3130",outparamters.getValue("r3130"));  	
    record.setValue("r3118",outparamters.getValue("r3118")); 
    record.setValue("model",outparamters.getValue("model"));  	 	
}
function sortRecord(model,a_code){
	var thecodeurl ="/train/traincourse/traindata.do?b_sort=link&model="+model+"&fieldsetid=r31"; 
	var return_vo= window.showModalDialog(thecodeurl, "", 
              	"dialogWidth:350px; dialogHeight:350px;resizable:no;center:yes;scroll:no;status:no");
    if(return_vo!=null){
    	changesReload(model,a_code);
    }
}
function setPageFormat(username){
   	var param_vo=oageoptions_selete("3",username);
}
//培训归档
function resultFiled(model,a_code)
{
	var table,dataset,record;	
    table=$("tabler31");
    dataset=table.getDataset();	
    record=dataset.getCurrent();

    if(!record)
		return;
		
	var classid=record.getValue("R3101");
	var status=record.getValue("R3127");
	if(status!='04')
	{
		alert(PUBLISHED_TRAIN_ARCHIVING+'！');
		return;
	}

	var hashvo=new ParameterSet();
	hashvo.setValue("classid",classid);
	hashvo.setValue("a_code",a_code);
	hashvo.setValue("model",model);	
	hashvo.setValue("msg","0");	
	var request=new Request({method:'post',asynchronous:false,onSuccess:traincount,functionId:'2020040012'},hashvo);
}
function traincount(outparamters){
	var person=outparamters.getValue("person");
	if(person==null||person==""||person=="0"){
		alert("培训班中没有学员时，不能归档！");
		return;
	}
	var model=outparamters.getValue("model");
	var a_code=outparamters.getValue("a_code");
	var classid=outparamters.getValue("classid");
	var theurl="/train/request/resultFiled.do?b_query=link`type=1`id="+classid;
    var iframe_url="/general/query/common/iframe_query.jsp?src="+$URL.encode(theurl);
   	var return_vo= window.showModalDialog(iframe_url, 'resultFiled_win', 
      				"dialogWidth:550px; dialogHeight:480px;resizable:no;center:yes;scroll:false;status:no");		
    if(!return_vo)
		return false;	   
    if(return_vo.flag=="true")
    {          
	 	courseTrainForm.action="/train/request/trainsData.do?b_query=link&a_code="+a_code+"&model="+model;
	 	courseTrainForm.submit();	
    } 
}

//培训教师归档
function teacherArchive()
{
	var id = getCurClassId();
    if (null==id || ""==id)
    {
      alert("请选择培训班！");
      return;
    }
    
	var hashvo=new ParameterSet();
	hashvo.setValue("classid",id);
	var request=new Request({method:'post',asynchronous:false,onSuccess:teacherCount,functionId:'2020040014'},hashvo);
}

function teacherCount(outparamters)
{
	var tCount = outparamters.getValue("tCount");
	//没有内部教师
	if(tCount==null||tCount==""||tCount=="0"){
		return;
	}
	
	var classid=outparamters.getValue("classid");
	var theurl="/train/request/resultFiled.do?b_query=link`type=2`id="+classid;
    var iframe_url="/general/query/common/iframe_query.jsp?src="+$URL.encode(theurl);

    
   	var return_vo= window.showModalDialog(iframe_url, 'teacherFiled_win', 
      				"dialogWidth:550px; dialogHeight:480px;resizable:no;center:yes;scroll:false;status:no");		
   if(!return_vo)
		return false;		
}

function trainArchive(model,a_code) 
{ 
	var id = getCurClassId();
    if (null==id || ""==id)
    {
      alert("请选择培训班！");
      return;
    }
    //只有已发布的培训班允许归档
	var status = getCurClassStatus();
	if(status!='04')
	{
		alert(PUBLISHED_TRAIN_ARCHIVING+'！');
		return;
	}
	
	teacherArchive();
	resultFiled(model,a_code);	
}

//添加培训班
function add(model,a_code,isAutoHour)
{
	var initValue='';
	var readonlyFilds='';
	var hideFilds ='';
	var hidepics = '';
    var isUnUmRela = '';
    
	var date = new Date(); 
	var currentYear=date.getFullYear();
	var currentMonth=date.getMonth()+1;
	var currentDay=date.getDate();
	var now = currentYear+'-'+currentMonth+'-'+currentDay;

	initValue='r3130:新培训班,r3127:03,r3118:'+now;//初始值	
	readonlyFilds='b0110,e0122,r3104,r3103,r3120,r3121,r3126,r3127,r3128,r3125,';//只读字段
	hideFilds ='';//隐藏字段 r3111,r3112,
    hidepics = 'imgr3127,';//需要隐藏的字段旁边的图片	
	isUnUmRela='true';

	var theurl="/train/traincourse/traindataAdd.do?b_query=link`fieldset=r31`a_code="+a_code+'`initValue='+getEncodeStr(initValue)+'`readonlyFilds='+readonlyFilds+'`hideFilds='+hideFilds+'`hidepics='+hidepics+'`isUnUmRela='+isUnUmRela+'`isAutoHour='+isAutoHour;
    var iframe_url="/general/query/common/iframe_query.jsp?src="+$URL.encode(theurl);
   	var return_vo= window.showModalDialog(iframe_url, 'trainClass_win1', 
      				"dialogWidth:800px; dialogHeight:650px;resizable:no;center:yes;scroll:yes;status:no");		
       				
   if(!return_vo)
		return false;	   
   if(return_vo.flag=="true")
	{   
		courseTrainForm.action="/train/request/trainsData.do?b_query=link&a_code="+a_code+"&model="+model;
		courseTrainForm.submit();	
	}    				
}
//添加课程
function addCourse(classid,r3127,r3115,r3116)
{
	var initValue='';
	var readonlyFilds='';
	var hideFilds ='';
	var hidepics = '';
	var hideSaveFlds='';
	var isUnUmRela = '';
	
	initValue='r4108:'+r3115+',r4110:'+r3116+',';//初始值	
	//readonlyFilds='r4105,r4106,r4114,r4103,';//只读字段
	hideFilds ='r4103,r4104,';//隐藏字段 r3111,r3112,
    hidepics = '';//需要隐藏的字段旁边的图片	imgr3127,
	hideSaveFlds = 'r4103:'+classid+',';//需要不在页面显示但是还要保存的字段和值，在此设置了就不用在initValue中设置了,但是还要在 hideFilds中设置
	isUnUmRela='false';
	
	var theurl='/train/traincourse/traindataAdd.do?b_query=link`fieldset=r41`addCourse=add`initValue='+initValue+'`readonlyFilds='+readonlyFilds+'`hideFilds='+hideFilds+'`hidepics='+hidepics+'`hideSaveFlds='+hideSaveFlds+'`isUnUmRela='+isUnUmRela;
    var iframe_url="/general/query/common/iframe_query.jsp?src="+$URL.encode(theurl);
   	var return_vo= window.showModalDialog(iframe_url, 'trainCourse_win', 
      				"dialogWidth:770px; dialogHeight:300px;resizable:no;center:yes;scroll:yes;status:no");		
       				
   if(!return_vo)
		return false;	   
   if(return_vo.flag=="true")
   {   
		courseTrainForm.action="/train/request/trainCourse.do?b_query=link&r3127="+r3127+"&r3101="+classid+"&r3115="+r3115+"&r3116="+r3116+"&flag=2";
		courseTrainForm.submit();	
   }
}
//编辑课程
function editCourse(priFldValue,classid,r3127,r3115,r3116)
{
	var initValue='';
	var readonlyFilds='';
	var hideFilds ='';
	var hidepics = '';
	
	initValue='';//初始值	
	//readonlyFilds='r4105,r4106,r4114,r4103,';//只读字段
	hideFilds ='r4103,r4104,';//隐藏字段 r3111,r3112,
    hidepics = '';//需要隐藏的字段旁边的图片	imgr3127, 
    
    var theurl='/train/traincourse/traindataAdd.do?b_query=link`fieldset=r41`initValue='+initValue+'`readonlyFilds='+readonlyFilds+'`hideFilds='+hideFilds+'`hidepics='+hidepics+'`priFldValue='+priFldValue+'`classid='+classid;
    var iframe_url="/general/query/common/iframe_query.jsp?src="+$URL.encode(theurl);
   	var return_vo= window.showModalDialog(iframe_url, 'trainCourse_win', 
      				"dialogWidth:700px; dialogHeight:300px;resizable:no;center:yes;scroll:yes;status:no");		    				
   if(!return_vo)
		return;	   
   if(return_vo.flag=="true")
   {   
		courseTrainForm.action="/train/request/trainCourse.do?b_query=link&r3127="+r3127+"&r3101="+classid+"&r3115="+r3115+"&r3116="+r3116+"&flag=2";
		courseTrainForm.submit();	
   }
}
//推送课程
function pushCourse(classid,r5000,basePath){
	if(!classid||!r5000){
		alert("操作失败！");
		return false;
	}
	var hashvo=new ParameterSet();
	hashvo.setValue("classid",classid);	
	hashvo.setValue("r5000",r5000);	
	hashvo.setValue("basepath", basePath);
	var request=new Request({method:'post',asynchronous:false,onSuccess:pushinfo,functionId:'2020040013'},hashvo);
}
function pushinfo(outparamters){
	if(outparamters){
		var flag = outparamters.getValue("flag")
		if("ok"==flag)
			alert("推送成功！");
		else if("error" == flag)
			alert("推送失败！");
		else
			alert(flag);
	}
}
//添加资源评估
function addResPg(classid,r3127)
{
	var initValue='';
	var readonlyFilds='';
	var hideFilds ='';
	var hidepics = '';
	var hideSaveFlds='';
	
	initValue='';//初始值	
	readonlyFilds='r3702,r3703,';//只读字段
	hideFilds ='r3703,';//隐藏字段 r3111,r3112,
    hidepics = '';//需要隐藏的字段旁边的图片	imgr3127,
	hideSaveFlds = 'r3703:'+classid+',';//需要不在页面显示但是还要保存的字段和值
	
	var theurl='/train/traincourse/traindataAdd.do?b_query=link`fieldset=r37`initValue='+initValue+'`readonlyFilds='+readonlyFilds+'`hideFilds='+hideFilds+'`hidepics='+hidepics+'`hideSaveFlds='+hideSaveFlds+'`classid='+classid;
    var iframe_url="/general/query/common/iframe_query.jsp?src="+$URL.encode(theurl);
   	var return_vo= window.showModalDialog(iframe_url, 'trainCourse_win', 
      				"dialogWidth:770px; dialogHeight:270px;resizable:no;center:yes;scroll:yes;status:no");		
       				
   if(!return_vo)
		return false;	   
   if(return_vo.flag=="true")
   {   
		courseTrainForm.action="/train/request/trainStu.do?b_query=link&r3127="+r3127+"&r3101="+classid+"&flag=4";
		courseTrainForm.submit();	
   }
}
//编辑资源评估
function editResPg(priFldValue,classid,r3127)
{
	var initValue='';
	var readonlyFilds='';
	var hideFilds ='';
	var hidepics = '';
	
	initValue='';//初始值	
	readonlyFilds='r3702,r3703,';//只读字段
	hideFilds ='r3703,';//隐藏字段 r3111,r3112,
    hidepics = '';//需要隐藏的字段旁边的图片	imgr3127,
    
    var theurl='/train/traincourse/traindataAdd.do?b_query=link`fieldset=r37`initValue='+initValue+'`readonlyFilds='+readonlyFilds+'`hideFilds='+hideFilds+'`hidepics='+hidepics+'`priFldValue='+priFldValue+'`classid='+classid;
    var iframe_url="/general/query/common/iframe_query.jsp?src="+$URL.encode(theurl);
   	var return_vo= window.showModalDialog(iframe_url, 'trainResPg_win', 
      				"dialogWidth:800px; dialogHeight:270px;resizable:no;center:yes;scroll:yes;status:no");		    				
   if(!return_vo)
		return;	   
   if(return_vo.flag=="true")
   {   
		courseTrainForm.action="/train/request/trainStu.do?b_query=link&r3127="+r3127+"&r3101="+classid+"&flag=4";
		courseTrainForm.submit();	
   }
}
function numParseDate(num)
{
	if(num=='')
		return num;
	var date = new Date();
    date.setTime(num);
    var currentYear=date.getFullYear();
	var currentMonth=date.getMonth()+1;
	var currentDay=date.getDate();
	var strDate = currentYear+'-'+currentMonth+'-'+currentDay;
	return strDate;
}
//设置标准学时
function setStudeyHour(model,a_code)
{
    var theurl='/train/request/trainsData.do?b_queryHour=link';
    var iframe_url="/general/query/common/iframe_query.jsp?src="+theurl;
   	var return_vo= window.showModalDialog(iframe_url, 'setStudeyHour', 
      				"dialogWidth:300px; dialogHeight:180px;resizable:no;center:yes;scroll:yes;status:no");
   if(!return_vo)
		return;	   
   if(return_vo.flag=="true")
   {  	   
	 courseTrainForm.action="/train/request/trainsData.do?b_updateHour=link&theHour="+return_vo.theHour+"&model="+model+"&a_code="+a_code;
	 courseTrainForm.submit();	
   } 
}
//自动计算学时
function autoCalcuStuHour(model,a_code,flag)
{
	document.getElementById("isAutoHour").value=flag;
	changesReload(model,a_code);	
}
//培训效果评估表
function trainEval()
{
	var table,dataset,record;	
    table=$("tabler31");
    dataset=table.getDataset();	
    record=dataset.getCurrent();

    if(!record)
		return;
	var classid=record.getValue("R3101");
	var theurl='/train/request/trainEffectEval.do?b_query=link`classid='+classid;
    var iframe_url="/general/query/common/iframe_query.jsp?src="+$URL.encode(theurl);
   	var return_vo= window.showModalDialog(iframe_url, 'setStudeyHour', 
      				"dialogWidth:700px; dialogHeight:360px;resizable:no;center:yes;scroll:yes;status:no");
     if(return_vo){
     	window.location.href=window.location.href;
     }

}
//费用分摊
function costShare()
{
	var table,dataset,record;	
    table=$("tabler31");
    dataset=table.getDataset();	
    record=dataset.getCurrent();
    if(!record)
		return;
	var classid=record.getValue("R3101");
	var hashvo=new ParameterSet();
	hashvo.setValue("classid",classid);	
	var request=new Request({method:'post',asynchronous:false,onSuccess:refreshStu,functionId:'2020030035'},hashvo);
}
function refreshStu(outparamters)
{
 	var table=$("tabler31");
	var dataset=table.getDataset();	
   	var record=dataset.getCurrent();
   	if(!record)
	    return;
    var r3101=record.getValue("r3101");
    var r3127=record.getValue("r3127");    
    var r3115=record.getValue("r3115");  //培训班开始时间
    var r3116=record.getValue("r3116");  //培训班结束时间 
  	parent.frames['ril_body2'].location="/train/request/trainsData.do?b_menu=link&r3101="+r3101+"&r3127="+r3127+"&r3115="+numParseDate(r3115)+"&r3116="+numParseDate(r3116)+"&sub_page=3";
}
//培训资源评估备注字段
function editMemoFild(priFld,memoFldName,classid,r3127)
{
	var target_url="/train/resource/memoFld.do?b_query=link`type=8`priFld="+priFld+"`memoFldName="+memoFldName;
 	var iframe_url="/general/query/common/iframe_query.jsp?src="+$URL.encode(target_url);
	var return_vo= window.showModalDialog(iframe_url, "memoFld_win", 
	              "dialogWidth:390px; dialogHeight:360px;resizable:no;center:yes;scroll:no;status:no");	
	if(!return_vo)
	   	return;	   
   	if(return_vo.flag=="true")
   {
		courseTrainForm.action="/train/request/trainStu.do?b_query=link&r3127="+r3127+"&r3101="+classid+"&flag=4";
		courseTrainForm.submit();	
   } 	
}
    
//begin 培训效果调查、评估相关方法
function getCurClassId()
{
	var tablename,table,dataset;		
	tablename="tabler31";
	table=$(tablename);
	  
	var dataset=table.getDataset(); 
	var record=dataset.getCurrent();
	if(record==null)
	   return "";
	  
	return record.getValue("r3101");
}

//得到选中培训班的状态
function getCurClassStatus()
{
	var tablename,table,dataset;		
	tablename="tabler31";
	table=$(tablename);
	  
	var dataset=table.getDataset(); 
	var record=dataset.getCurrent();
	if(record==null)
	   return "";
	  
	return record.getValue("R3127");
}

var Questionnaire = function(outparamters)
{   
	var id = "";
	if(null != outparamters)
	{
		id = outparamters.getValue("mdquesJob");		
	}
	
	 if(""==id)
	 {
	    alert("此培训班未设置培训效果调查问卷！");
	    return;
	 }
	 
	 var theurl = "/train/request/viewQuestionnareChart.jsp?id="+id;   
	 var iframe_url="/general/query/common/iframe_query.jsp?src="+$URL.encode(theurl);
	 window.showModalDialog(iframe_url, "memoFld_win", 
	             "dialogWidth:1000px; dialogHeight:800px;resizable:yes;center:yes;scroll:yes;status:no");
};

//关闭问卷分析
function questionnaireTemplateWinClose(){
	var questionnaireTemplateWin = Ext.getCmp('questionnaireTemplateWin');
	if(questionnaireTemplateWin){
		questionnaireTemplateWin.close();
	}
}

function getEvalSet(callfunc)
{
	var id = getCurClassId();
}
cell=null;
isShowQusestionImg=function(outparamters){
		var id = "";
		if(null != outparamters)
		{
			id = outparamters.getValue("quesJob");		
		}
		 if(""==id)
		 {
		 	cell.innerHTML="";
		 }else
		 	cell.innerHTML="<img src=\"/images/view.gif\" border=\"0\" onclick=\"showQuestionnaire();\" style=\"cursor:hand;\">";
};

function getEvalSet(callfunc,r3101,_cell)
{
	var id =null;
	if(r3101)
		id=r3101;
	else
		id = getCurClassId();
	cell=_cell;

    if (null==id || ""==id)
    {
      alert("请选择培训班！");
      return;
    }
    //请求获取问卷调查编号
    var hashvo=new ParameterSet();
    hashvo.setValue("classid",id);    
    var request=new Request({asynchronous:false,onSuccess:callfunc,functionId:'2020030033'},hashvo);
    
    
}

function showQuestionnaire()
{
   getEvalSet(Questionnaire);
} 

var KhResult = function(outparamters)
{   
  var classid = getCurClassId();
	if(null != outparamters)
	{
		templetid = outparamters.getValue("temJob");		
	}
	
  if(""==templetid)
  {
     alert("此培训班未设置教师培训评估模板！");
     return;
  }
  var target_url = "/train/evaluatingStencil.do?b_analyse=link&r3101=" + classid + "&templateid=" + templetid;    
  window.showModalDialog(target_url, "memoFld_win", 
              "dialogWidth:1200px; dialogHeight:1000px;resizable:yes;center:yes;scroll:yes;status:no");
};

isShowKhResultImg = function(outparamters)
{   
	if(null != outparamters)
	{
		templetid = outparamters.getValue("temJob");		
	}
	
  if(""==templetid)
  {
     cell.innerHTML="";
  }else{
  	cell.innerHTML="<img src=\"/images/view.gif\" border=\"0\" onclick=\"showKhResult();\" style=\"cursor:hand;\">";
  }
};

function showKhResult()
{ 
	getEvalSet(KhResult);
}
//end 培训效果调查、评估相关方法

function batchInOut(){
	courseTrainForm.action="/train/request/trainsData.do?b_batchinout=link&student=";
	courseTrainForm.submit();
}

function fileup(){
	courseTrainForm.action="/train/request/trainsData.do?br_selectfile=link";
	courseTrainForm.submit();
}
//批量修改
function batchedit(model,a_code) {
	thecodeurl = "/train/request/trainsData.do?b_batchedit=link";
	var iframe_url = "/general/query/common/iframe_query.jsp?src=" + $URL.encode(thecodeurl);
	var dw = 520, dh = 500, dl = (screen.width - dw) / 2;
	dt = (screen.height - dh) / 2;
	var return_vo=window.showModalDialog(iframe_url, 1, "dialogLeft:" + dl + "px;dialogTop:" + dt 
			+ "px;dialogWidth:570px; dialogHeight:500px;resizable:no;center:yes;scroll:yes;status:no;scrollbars:yes");
	if(return_vo=="true")
		changesReload(model,a_code);
}

