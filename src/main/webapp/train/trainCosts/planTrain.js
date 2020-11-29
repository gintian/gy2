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
function timeFlagChange(obj,model,a_code){
	var timeflag = obj.value;
	if(timeflag=='04'){
		toggles("viewtime");
		return false;
	}else{
		document.getElementById("startime").value="";
		document.getElementById("endtime").value="";
		changesReload(model,a_code);
	}
}
function changesReload(model,a_code){
	planTrainForm.action="/train/trainCosts/trainData.do?b_query=link&model="+model+"&a_code="+a_code;
	planTrainForm.submit();  	
}
function setPageFormat(username){
   	var param_vo=oageoptions_selete("3",username);
}
//给某计划添加培训费用明细
function addRelaCost(b0110,e0122,r2501)
{
	var initValue='';
	var readonlyFilds='';
	var hideFilds ='';
	var hidepics = '';  
	var a_code='';
    var isUnUmRela = '';
    var hideSaveFlds='';
    
	var date = new Date(); 
	var currentYear=date.getFullYear();
	var currentMonth=date.getMonth()+1;
	var currentDay=date.getDate();
	var now = currentYear+'-'+currentMonth+'-'+currentDay;
	
	initValue='r4511:'+now+',';//初始值
	hideSaveFlds = 'r4502:'+r2501+',';//需要不在页面显示但是还要保存的字段和值，在此设置了就不用在initValue中设置了,但是还要在 hideFilds中设置
	if(e0122=='' && b0110!='')
		a_code='UN'+b0110;
	else if(e0122!='' && b0110!='')
		a_code='UM'+e0122;

	readonlyFilds='b0110,e0122,';//只读字段
	hideFilds ='r4502,';//隐藏字段 r3111,r3112,
    hidepics = 'imgb0110,imge0122,';//需要隐藏的字段旁边的图片	imgr3127,
	isUnUmRela = 'true';
	var theurl="/train/traincourse/traindataAdd.do?b_query=link`fieldset=r45`a_code="+a_code+'`initValue='+getEncodeStr(initValue)+'`readonlyFilds='+readonlyFilds+'`hideFilds='+hideFilds+'`hidepics='+hidepics+'`isUnUmRela='+isUnUmRela+'`hideSaveFlds='+hideSaveFlds;
    var iframe_url="/general/query/common/iframe_query.jsp?src="+$URL.encode(theurl);
   	var return_vo= window.showModalDialog(iframe_url, 'trainClass_win2', 
      				"dialogWidth:700px; dialogHeight:500px;resizable:no;center:yes;scroll:yes;status:no");	
   if(!return_vo)
		return;	   
   if(return_vo.flag=="true")
	{   
		trainCostsForm.action="/train/trainCosts/trainCosts.do?b_query=link&r2501="+r2501+"&b0110="+b0110+"&e0122="+e0122+"&flag=1";
		trainCostsForm.submit();	
	}    				
}
//编辑培训计划对应的培训费用明细
function editRelaCost(priFldValue,r2501,b0110,e0122)
{
	var readonlyFilds='';
	var hideFilds ='';
	var hidepics = '';
    var isUnUmRela = 'true';

	readonlyFilds='b0110,e0122,';//只读字段
	hideFilds ='r4502,';//隐藏字段 r3111,r3112,
    hidepics = 'imgb0110,imge0122,';//需要隐藏的字段旁边的图片	imgr3127,
	
	var theurl='/train/traincourse/traindataAdd.do?b_query=link`fieldset=r45`readonlyFilds='+readonlyFilds+'`hideFilds='+hideFilds+'`hidepics='+hidepics+'`isUnUmRela='+isUnUmRela+'`priFldValue='+priFldValue;
    var iframe_url="/general/query/common/iframe_query.jsp?src="+$URL.encode(theurl);
   	var return_vo= window.showModalDialog(iframe_url, 'trainClass_win2', 
      				"dialogWidth:700px; dialogHeight:500px;resizable:no;center:yes;scroll:yes;status:no");	
   if(!return_vo)
		return;	   
   if(return_vo.flag=="true")
	{   
		trainCostsForm.action="/train/trainCosts/trainCosts.do?b_query=link&r2501="+r2501+"&b0110="+b0110+"&e0122="+e0122+"&flag=1";
		trainCostsForm.submit();	
	}    				
}
//培训费用明细计算
function calculate()
{
	var thecodeurl="/train/trainCosts/costCalcu.do?br_query=link";
   	var iframe_url="/general/query/common/iframe_query.jsp?src="+thecodeurl;
    var return_vo= window.showModalDialog(iframe_url, "", 
              	"dialogWidth:400px; dialogHeight:400px;resizable:no;center:yes;scroll:yes;status:no");
}
//培训费用排序
function costSort()
{
	var thecodeurl="/train/trainCosts/costCalcu.do?b_sort=link";
   	var iframe_url="/general/query/common/iframe_query.jsp?src="+thecodeurl;
    var return_vo= window.showModalDialog(iframe_url, "", 
              	"dialogWidth:340px; dialogHeight:380px;resizable:no;center:yes;scroll:yes;status:no");
   if(!return_vo)
		return;	   
   if(return_vo.flag=="true")
	{   
		var sortStr = return_vo.sortStr;
		trainCostsForm.action="/train/trainCosts/costCalcu.do?b_saveSort=link&sortStr="+sortStr;
		trainCostsForm.submit();	
	} 
}
function setFlag(itemid)
{
	alert(itemid);
	var item = $(itemid);
	var value = '0';
	if(item.checked==true)
		value='1';
	//var hashvo=new ParameterSet();
	//hashvo.setValue("itemid",itemid);	
	//hashvo.setValue("value",value);	
	//var request=new Request({method:'post',asynchronous:false,functionId:'2020020212'},hashvo);
}


