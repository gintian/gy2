function batchCompute(unitcode)
{
		var thecodeurl="/report/edit_collect/reportCollect.do?b_batchCompute=query&unitcode="+unitcode;
		/*var objectlist= window.showModalDialog(thecodeurl,"", 
		        "dialogWidth:650px; dialogHeight:530px;resizable:no;center:yes;scroll:yes;status:no");*/
		
		var config = {
				width:680,
				height:550,
				title:'表内表间计算',
				theurl:thecodeurl,
				id:'editCollect'
			}
		openWin(config);
}


function selectAll(name)
{
	var a_select=eval("document.reportCollectForm."+name);
	for(var a=0;a<a_select.options.length;a++)
	{
		a_select.options[a].selected=true;
	}
	var a_select2=eval("document.reportCollectForm.unitCode");
	for(var a=0;a<a_select2.options.length;a++)
	{
		a_select2.options[a].selected=true;
	}
	
}


//返回选择的结果集
function getSelectInfos(name)
{
	var values=new Array();
	var i=0;
	var a_select=eval("document.reportCollectForm."+name);
	for(var a=0;a<a_select.options.length;a++)
	{
		if(a_select.options[a].selected==true)
		{
			var a_value=a_select.options[a].value;
			 while(a_value.indexOf("\r\n")!=-1) 
		     { 
		    	a_value=a_value.replace('\r\n','');
		     }
			values[i++]=a_value
		}
	}
	return values;
}

//返回选择的结果集
function getSelectBmInfos(name)
{
	var values=new Array();
	var i=0;
	var a_select=eval("document.reportCollectForm."+name);
	for(var a=0;a<a_select.options.length;a++)
	{
		if(a_select.options[a].selected==true)
		{
			var a_value=a_select.options[a].value;
			while(a_value.indexOf("\r\n")!=-1) 
		    { 
		    	a_value=a_value.replace('\r\n','');
		    }
			values[i++]=getEncodeStr(a_value)
		}
	}
	return values;
}


function collect()
{
	var hashvo=new ParameterSet();
	var unitcodeArray=new Array();
	var operate="";
	document.getElementsByName("b_update2")[0].disabled=true;
	for(var i=0;i<document.reportCollectForm.operater.length;i++)
	{
		if(document.reportCollectForm.operater[i].checked==true)
			operate=document.reportCollectForm.operater[i].value;
	}
	if(operate==1)
	{
	
		var a_select=eval("document.reportCollectForm.unitCode");
		if(a_select.options.length==0){
			alert(REPORT_INFO67+"！");
		document.getElementsByName("b_update2")[0].disabled=false;
		return ;
		}
		unitcodeArray=getSelectInfos("unitCode");
		if(unitcodeArray.length==0)
		{
			alert(REPORT_INFO37+"！");
		document.getElementsByName("b_update2")[0].disabled=false;
			return;
		}
	}
	else if(operate==4)
	{
	
		var a_select=eval("document.reportCollectForm.unitCode");
		if(a_select.options.length==0){
		alert(REPORT_INFO67+"！");
		document.getElementsByName("b_update2")[0].disabled=false;
		return ;
		}
		
	}
	else if(operate==5)
	{
		selectAll("unitCode");
		unitcodeArray=getSelectInfos("unitCode");
		var a_select=eval("document.reportCollectForm.unitCode");
		if(a_select.options.length==0){
			alert(REPORT_INFO67+"！");
		document.getElementsByName("b_update2")[0].disabled=false;
		return ;
		}
	}
	var tabArray=getSelectBmInfos("tabid");
	if(tabArray.length==0)
	{	
		alert(REPORT_INFO42+"！");
		document.getElementsByName("b_update2")[0].disabled=false;
		return;
	}
	if(operate==1||operate==5)  //直属单位汇总 
	{
		hashvo.setValue("unitcodeArray",unitcodeArray);
		hashvo.setValue("tabArray",tabArray);		
		var In_paramters="operate="+operate; 				
		var request=new Request({method:'post',asynchronous:false,parameters:In_paramters,onSuccess:returnValidateInfo,functionId:'03030000003'},hashvo);
		
	}
	else if(operate==3||operate==7||operate==4)	//所有基层单位|逐层汇总|直属汇总-基层汇总比较
	{
		hashvo.setValue("tabArray",tabArray);
		hashvo.setValue("unitcode",unitcode);		
		var In_paramters="operate="+operate; 				
		var request=new Request({method:'post',asynchronous:false,parameters:In_paramters,onSuccess:returnValidateInfo,functionId:'03030000003'},hashvo);
		
	}
	else if(operate==2||operate==6) //简单条件基层汇总||复杂条件基层汇总
	{
		if(unitcodelist.length==0)
		{
			alert(REPORT_INFO43);
			document.getElementsByName("b_update2")[0].disabled=false;
			return;
		}
		hashvo.setValue("unitcodeArray",unitcodelist);
		hashvo.setValue("tabArray",tabArray);		
		var In_paramters="operate="+operate; 			
		var request=new Request({method:'post',asynchronous:false,parameters:In_paramters,onSuccess:returnValidateInfo,functionId:'03030000003'},hashvo);
	}
}

//校验上报信息
var info=new Array();
function returnValidateInfo(outparamters)
{
	var isPopedom=outparamters.getValue("isPopedom");
	if(isPopedom=='0')
	{
		alert(REPORT_INFO44);
		return;
	
	}
	
	
	var returnInfo=outparamters.getValue("returnInfo");
	var operate=outparamters.getValue("operate");
	var arr=new Array();
	for(var a=0;a<returnInfo.length;a++)
		arr[a]=returnInfo[a];
	info[0]=arr;
	if(operate==1||operate==5)
		info[1]=getSelectInfos("unitCode")
	else if(operate==2||operate==6||operate==4)
		info[1]=outparamters.getValue("unitcodeArray");
	else if(operate==3||operate==7)  //所有基层单位汇总|逐层汇总
	{
		info[1]="";
	}
	info[2]=getSelectInfos("tabid")
	info[3]=unitcode;
	info[4]=operate;
	var thecodeurl="/report/edit_collect/reportCollect.do?br_collectValidate=validate";
	/* 将scroll由yes设置为auto xiaoyun 2014-6-26 start 
	var objectlist= window.showModelessDialog(thecodeurl,info, 
		        "dialogWidth:600px; dialogHeight:350px;resizable:no;center:yes;scroll:auto;status:no");*/
	/* 将scroll由yes设置为auto xiaoyun 2014-6-26 end */
	document.getElementsByName("b_update2")[0].disabled=false;
	var config = {
			width:600,
			height:350,
			title:'汇总',
			theurl:thecodeurl,
			id:'editCollect'
		}
	openWin(config);
}

function openWin(config){
    Ext.create("Ext.window.Window",{
    	id:config.id,
    	width:config.width,
    	height:config.height,
    	title:config.title,
    	resizable:false,
    	autoScroll:false,
    	padding:'0 30',
    	modal:true,
    	renderTo:Ext.getBody(),
    	html:"<iframe style='background-color:#ffffff' frameborder='0' SCROLLING=NO height='100%' width='100%' src='"+config.theurl+"'></iframe>",
    	listeners:{
    		'close':function(){
    			var unitcodeList=new Array();	
    			if(objectlist&&objectlist.length>0)
    			{
    				for(var i=0;i<objectlist.length;i++)
    					unitcodeList[i]=objectlist[i];
    				unitcodelist=unitcodeList;
    			}
    			else
    			{
    				unitcodelist=new Array();
    				return;
    			}
    		}
    	}
	    }).show();	
}

//简单条件基层汇总
var objectlist= new Array();
function simpleCollect()
{	
	var a_tabArray=getSelectInfos("tabid");
	if(a_tabArray.length==0)
	{
		alert(REPORT_INFO45+"！");
		return;
	}
	

	var sortid_str='';
	for(var i=0;i<a_tabArray.length;i++)
	{
		var temp=a_tabArray[i];
		var temp_str=temp.split('§');
		sortid_str+=','+temp_str[2];
	}	
	var thecodeurl="/report/edit_collect/reportCollect.do?b_simpleCondition=query&unitcode="+unitcode+"&sortid_str="+sortid_str.substring(1);
	
	var config = {
			width:420,
			height:450,
			title:'设置汇总条件',
			theurl:thecodeurl,
			id:'editCollect'
		}
	openWin(config);
}


function complexCollect()
{

	var a_tabArray=getSelectInfos("tabid");
	if(a_tabArray.length==0)
	{
		alert(REPORT_INFO46+"！");
		return;
	}
	var sortid_str='';
	
	for(var a=0;a<a_tabArray.length;a++)
	{
		var temp=a_tabArray[a];
		var temp_str=temp.split('§');
		sortid_str+=','+temp_str[2];
	}

	var thecodeurl="/report/edit_collect/reportCollect.do?b_complexCondition=query&unitcode="+unitcode+"&sortid_str="+sortid_str.substring(1);
	var config = {
			width:550,
			height:460,
			title:'设置汇总条件',
			theurl:thecodeurl,
			id:'editCollect'
		}
	openWin(config);
}