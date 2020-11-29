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
	trainCourseForm.action="/train/traincourse/traindata.do?b_query=link&model="+model+"&a_code="+a_code;
	trainCourseForm.submit();  	
}
function searchInform(model,a_code){
	var thecodeurl ="/train/traincourse/generalsearch.do?b_query=link&fieldsetid=r31&model="+model; 
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
	var thecodeurl ="/train/traincourse/traindata.do?b_sort=link&model="+model+"&fieldsetid=r31&sortype=1"; 
	var return_vo= window.showModalDialog(thecodeurl, "", 
              	"dialogWidth:350px; dialogHeight:350px;resizable:no;center:yes;scroll:no;status:no");
    if(return_vo!=null){
    	changesReload(model,a_code);
    }
}
function setPageFormat(username){
   	var param_vo=oageoptions_selete("3",username);
}
//添加培训班
function add(a_code,model)
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

	if(model=='1')
		initValue='r3130:新培训班,r3127:01,r3118:'+now;//初始值
	else if(model=='2')
		initValue='r3130:新培训班,r3127:03,r3118:'+now;//初始值
	
	readonlyFilds='b0110,e0122,r3104,r3103,r3120,r3121,r3126,r3127,r3128,r3125,';//只读字段
	hideFilds ='';//隐藏字段 r3111,r3112,
    hidepics = 'imgr3127,';//需要隐藏的字段旁边的图片	
	isUnUmRela = 'true';
	
	var theurl="/train/traincourse/traindataAdd.do?b_query=link`fieldset=r31`a_code="+a_code+'`initValue='+getEncodeStr(initValue)+'`readonlyFilds='+readonlyFilds+'`hideFilds='+hideFilds+'`hidepics='+hidepics+'`isUnUmRela='+isUnUmRela;
    var iframe_url="/general/query/common/iframe_query.jsp?src="+$URL.encode(theurl);
   	var return_vo= window.showModalDialog(iframe_url, 'trainClass_win1', 
      				"dialogWidth:760px; dialogHeight:650px;resizable:no;center:yes;scroll:yes;status:no");		
   if(!return_vo)
	    return false;	   
   if(return_vo.flag=="true")
	{   
		trainCourseForm.action="/train/traincourse/traindata.do?b_query=link&a_code="+a_code+"&model="+model;
		trainCourseForm.submit();	
	}   
}
//添加某培训计划的培训班
function addRelaClass(b0110,e0122,model,r2501)
{
	var initValue='';
	var readonlyFilds='';
	var hideFilds ='';
	var hidepics = '';
	var a_code='';
    var isUnUmRela = '';
    
	var date = new Date(); 
	var currentYear=date.getFullYear();
	var currentMonth=date.getMonth()+1;
	var currentDay=date.getDate();
	var now = currentYear+'-'+currentMonth+'-'+currentDay;

	if(model=='1')
		initValue='r3127:03,r3118:'+now+',r3125:'+r2501;//初始值
	else if(model=='2')
		initValue='r3127:03,r3118:'+now+',r3125:'+r2501;//初始值
		
	if(e0122=='' && b0110!='')
		a_code='UN'+b0110;
	else if(e0122!='' && b0110!='')
		a_code='UM'+e0122;

	readonlyFilds='b0110,e0122,r3104,r3103,r3120,r3121,r3126,r3127,r3128,r3125,';//只读字段
	hideFilds ='';//隐藏字段 r3111,r3112,
    hidepics = 'imgr3127,';//需要隐藏的字段旁边的图片	
	isUnUmRela = 'true';
	var theurl="/train/traincourse/traindataAdd.do?b_query=link`fieldset=r31`a_code="+a_code+'`initValue='+getEncodeStr(initValue)+'`readonlyFilds='+readonlyFilds+'`hideFilds='+hideFilds+'`hidepics='+hidepics+'`isUnUmRela='+isUnUmRela;
    var iframe_url="/general/query/common/iframe_query.jsp?src="+theurl;
   	var return_vo= window.showModalDialog(iframe_url, 'trainClass_win2', 
      				"dialogWidth:700px; dialogHeight:620px;resizable:no;center:yes;scroll:yes;status:no");	
   if(!return_vo)
		return;	   
   if(return_vo.flag=="true")
	{   
		trainCourseForm.action="/train/traincourse/traindata.do?b_train=link&r2501="+r2501+"&model="+model+"&b0110="+b0110+"&e0122="+e0122;
		trainCourseForm.submit();	
	}    				
}
//编辑培训计划对应的培训班
function editRelaClass(priFldValue,model,r2501,b0110,e0122)
{
	var initValue='';
	var readonlyFilds='';
	var hideFilds ='';
	var hidepics = '';
	var a_code='';
    var isUnUmRela = 'true';

	readonlyFilds='b0110,e0122,r3104,r3103,r3120,r3121,r3126,r3127,r3128,r3125,';//只读字段
	hideFilds ='';//隐藏字段 r3111,r3112,
    hidepics = 'imgr3127,';//需要隐藏的字段旁边的图片	
	
	var theurl="/train/traincourse/traindataAdd.do?b_query=link`fieldset=r31`a_code="+a_code+'`initValue='+initValue+'`readonlyFilds='+readonlyFilds+'`hideFilds='+hideFilds+'`hidepics='+hidepics+'`isUnUmRela='+isUnUmRela+'`priFldValue='+priFldValue;
    var iframe_url="/general/query/common/iframe_query.jsp?src="+theurl;
   	var return_vo= window.showModalDialog(iframe_url, 'trainClass_win2', 
      				"dialogWidth:700px; dialogHeight:620px;resizable:no;center:yes;scroll:yes;status:no");	
   if(!return_vo)
		return;	   
   if(return_vo.flag=="true")
	{   
		trainCourseForm.action="/train/traincourse/traindata.do?b_train=link&r2501="+r2501+"&model="+model+"&b0110="+b0110+"&e0122="+e0122;
		trainCourseForm.submit();	
	}    				
}
function returnFirst(){
   	self.parent.location= "/general/tipwizard/tipwizard.do?br_train=link";
}

function trainBoards(flag){
	
	var table,dataset,ids="";	
    table=$("tabler31");
    dataset=table.getDataset();	
	var record=dataset.getFirstRecord();
	while (record) {
		if (record.getValue("select")) {
			var classid=record.getValue("R3101");
			
			ids=ids+classid+",";
		}
		record=record.getNextRecord();
	}
	if(ids==null||ids.length<1){
		alert(SELECT_TRAIN_CLASS);
		return;
	}
	if(flag=='1'){
		if (!confirm(TRAIN_CALSS_APPROVAL))
			return;
	}else if(flag=='2') {
		if (!confirm(TRAIN_CALSS_APPROVE))
			return;
	}
	var hashvo=new ParameterSet();
	hashvo.setValue("ids",ids);
	hashvo.setValue("vflag",flag);
	var request=new Request({method:'post',asynchronous:false,onSuccess:check_oks,functionId:'202003003306'},hashvo);
}
function check_oks(outparameters)
{
  var msg=outparameters.getValue("msg");
 
  if(msg=='no'||msg=='0')
  {
	  var ids=outparameters.getValue("ids");
	  var vflag=outparameters.getValue("vflag");
	  var hashvo=new ParameterSet();
	  hashvo.setValue("ids",ids);
	  if(vflag=='1')
	  		var request=new Request({method:'post',asynchronous:false,onSuccess:Success,functionId:'2020020219'},hashvo);
	  if(vflag=='2')
		  	var request=new Request({method:'post',asynchronous:false,onSuccess:Success,functionId:'2020020217'},hashvo);
	  function Success(outparameters)
		{
		  var flag=outparameters.getValue("msg");
		  if(flag=='true')
		  {
		    alert(TRAIN_CLASS_SUCCESS);
		    trainCourseForm.action="/train/traincourse/traindata.do?b_query=link&model="+vflag+"&encryptParam="+encryptParam;
		    trainCourseForm.submit();
		    return;
		  }
		}
  }
  else{
	  var filename=outparameters.getValue("fileName");
      if(filename==null)
      	return;
      window.location.target="mil_body";
		window.location.href = "/servlet/vfsservlet?fromjavafolder=true&fileid="+filename;
  }
}