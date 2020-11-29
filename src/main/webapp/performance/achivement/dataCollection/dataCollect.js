function changePlan()
{	//alert(thePlan);
	var objstr='';
	var paramStr = $('paramStr');	
	if(thePointType!="0")	//加减分指标的保存
	{
		var scoreStr='';
		var dfScore='';
		
		var objArray=objs.split(",");
		for(var i=0;i<objArray.length;i++)
  		{
  			var objId = objArray[i];
  			if(objId=='')
  				break;
			var theObj = document.getElementById(objId);
			if(theObj!=null)
			{
				objstr+=objId+'<@>';	
				var df_value = $F(objId+'_df');
				if(df_value=='')
					df_value='0';
				dfScore+=objId+'='+df_value+'<@>';	
				var itemsArray=items.split(",");
				for(var j=0;j<itemsArray.length;j++)
  				{
  					var item = itemsArray[j];
  					if(item=='')
  						break;
					var score_value=$F(objId+'_'+item);
					if(score_value=='')
						score_value='0';
					scoreStr+=objId+'_'+item+'='+score_value+'<@>';
				}
			}
		}
		paramStr.value=objstr+"&"+scoreStr+"&"+dfScore;		
	}
	else if (thePointType=="0" && theRule=="0")	//基本指标录分规则
	{
		var fzScores = '';
		var objArray=objs.split(",");
		for(var i=0;i<objArray.length;i++)
  		{
  			var objId = objArray[i];
  			if(objId=='')
  				break;
			var theObj = document.getElementById(objId);
			if(theObj!=null)
			{
				fzScores+=objId+'='+getVal(objId+'_fz')+'<@>';
				objstr+=objId+'<@>';
			}
		}
		paramStr.value=objstr+"&"+fzScores;				
	}
	else if (thePointType=="0" && theRule!="0")	//基本指标非录分规则
	{
		var dfValue ='';
		var standardVal='';
		var basicVal='';
		var praticalVal='';
		var addVal='';
		var deducVal='';
		
		var objArray=objs.split(",");
		for(var i=0;i<objArray.length;i++)
  		{
  			var objId = objArray[i];
  			if(objId=='')
  				break;
			var theObj = document.getElementById(objId);
			if(theObj!=null)
			{	
				objstr+=objId+'<@>';
				dfValue+=objId+'_df='+getVal(objId+'_df')+'<@>';			
				standardVal+=objId+'_standard='+getVal(objId+'_standard')+'<@>';
				basicVal+=objId+'_basic='+getVal(objId+'_basic')+'<@>';
				praticalVal+=objId+'_pratical='+getVal(objId+'_pratical')+'<@>';
				addVal+=objId+'_add='+getVal(objId+'_add')+'<@>';
				deducVal+=objId+'_deduc='+getVal(objId+'_deduc')+'<@>';			
			}
		}	
		paramStr.value=objstr+"&"+standardVal+"&"+praticalVal+"&"+basicVal+"&"+addVal+"&"+deducVal+"&"+dfValue;	
	}	
	/*
	dataCollectForm.action="/performance/achivement/dataCollection/dataCollect.do?b_save=link&planId="+thePlan;
	dataCollectForm.submit();*/
	//alert(thePlan+'--'+thePoint+'--'+paramStr+'--'+theRule+'--'+thePointType);
	var hashvo=new ParameterSet();			
	hashvo.setValue("planId",thePlan);
	hashvo.setValue("point",thePoint);
	hashvo.setValue("paramStr",paramStr.value);
	hashvo.setValue("rule",theRule);
	hashvo.setValue("pointype",thePointType);
	var request=new Request({method:'post',asynchronous:false,functionId:'9020020309'},hashvo);			
}
function changePoint(pointID)
{	
	//alert(dataCollectForm.planId.value+'--'+thePlan+'--'+planid+'--'+pointID);
	//先执行保存操作
	var objstr='';
	var paramStr = $('paramStr');	
	if(thePointType!="0")	//加减分指标的保存
	{
		var scoreStr='';
		var dfScore='';
		
		var objArray=objs.split(",");
		for(var i=0;i<objArray.length;i++)
  		{
  			var objId = objArray[i];
  			if(objId=='')
  				break;
			var theObj = document.getElementById(objId);
			if(theObj!=null)
			{
				objstr+=objId+'<@>';	
				var df_value = $F(objId+'_df');
				if(df_value=='')
					df_value='0';
				dfScore+=objId+'='+df_value+'<@>';	
				var itemsArray=items.split(",");
				for(var j=0;j<itemsArray.length;j++)
  				{
  					var item = itemsArray[j];
  					if(item=='')
  						break;
					var score_value=$F(objId+'_'+item);
					if(score_value=='')
						score_value='0';
					scoreStr+=objId+'_'+item+'='+score_value+'<@>';
				}
			}
		}
		paramStr.value=objstr+"&"+scoreStr+"&"+dfScore;		
	}
	else if (thePointType=="0" && theRule=="0")	//基本指标录分规则
	{
		var fzScores = '';
		var objArray=objs.split(",");
		for(var i=0;i<objArray.length;i++)
  		{
  			var objId = objArray[i];
  			if(objId=='')
  				break;
			var theObj = document.getElementById(objId);
			if(theObj!=null)
			{
				fzScores+=objId+'='+getVal(objId+'_fz')+'<@>';
				objstr+=objId+'<@>';
			}
		}
		paramStr.value=objstr+"&"+fzScores;				
	}
	else if (thePointType=="0" && theRule!="0")	//基本指标非录分规则
	{
		var dfValue ='';
		var standardVal='';
		var basicVal='';
		var praticalVal='';
		var addVal='';
		var deducVal='';
		
		var objArray=objs.split(",");
		for(var i=0;i<objArray.length;i++)
  		{
  			var objId = objArray[i];
  			if(objId=='')
  				break;
			var theObj = document.getElementById(objId);
			if(theObj!=null)
			{	
				objstr+=objId+'<@>';
				dfValue+=objId+'_df='+getVal(objId+'_df')+'<@>';			
				standardVal+=objId+'_standard='+getVal(objId+'_standard')+'<@>';
				basicVal+=objId+'_basic='+getVal(objId+'_basic')+'<@>';
				praticalVal+=objId+'_pratical='+getVal(objId+'_pratical')+'<@>';
				addVal+=objId+'_add='+getVal(objId+'_add')+'<@>';
				deducVal+=objId+'_deduc='+getVal(objId+'_deduc')+'<@>';			
			}
		}	
		paramStr.value=objstr+"&"+standardVal+"&"+praticalVal+"&"+basicVal+"&"+addVal+"&"+deducVal+"&"+dfValue;	
	}
	var oldPoint=thePoint;
	var newPoint=pointID;
	dataCollectForm.action="/performance/achivement/dataCollection/dataCollect.do?b_save2=link&planId="+thePlan+"&oldPoint="+oldPoint+"&newPoint="+newPoint;
	dataCollectForm.submit();	
}
//输入数值型(实际值和加减指标的业务值不能为负)
function IsDigit(obj) 
{
	if((event.keyCode >= 46) && (event.keyCode <= 57) && event.keyCode!=47)
	{
		var values=obj.value;
		if((event.keyCode == 46) && (values.indexOf(".")!=-1))//有两个.
			return false;
		if((event.keyCode == 46) && (values.length==0))//首位是.
			return false;	
		return true;
	}
		return false;	
}
//输入数值型(录入方式下的分值可以为负)
function IsDigit2(obj) 
{
	if((event.keyCode >= 45) && (event.keyCode <= 57) && event.keyCode!=47)
	{
		var values=obj.value;
		if((event.keyCode == 46) && (values.indexOf(".")!=-1))//有两个.
			return false;
		if((event.keyCode == 46) && (values.length==0))//首位是.
			return false;	
		return true;
	}
		return false;	
}
function importExcel(planid,pointID)
{
	var target_url="/performance/achivement/dataCollection/importExcel.do?br_import=link";
 	var iframe_url="/general/query/common/iframe_query.jsp?src="+target_url;
  	var uploadObj =  Ext.create("SYSF.FileUpLoad",{
	renderTo:Ext.getBody(),
	emptyText:"请输入文件路径或选择文件",
	upLoadType:1,
	fileExt:"*.xlsx;*.xls;",
	success:function(list){
		var obj = list[0];
		var hashvo=new HashMap();     
		hashvo.put("filename",obj.filename);
		hashvo.put("path",obj.path);
		hashvo.put("planId",planId);
		hashvo.put("determine",determine);
		var functionId = isShowTargetTrace=="1"?'9020020312':'9020020304';
		Rpc({functionId:functionId,async:false,success:function(res){// 
			Ext.getCmp("importWin").close();
			var data = Ext.decode(res.responseText);
			if(data.error=="0"){
				Ext.showAlert("导入成功!",function(){
	    			dataCollectForm.action="/performance/achivement/dataCollection/dataCollect.do?b_query=link&planId="+planid+"&pointID="+pointID;
					dataCollectForm.submit();
				});
			}else{
				Ext.showAlert(data.message);
			}
		}},hashvo);
	}
});
	Ext.widget("window",{
		id:'importWin',
		title: '导入数据',
        modal:true,
        border:false,
        resizable:false,
        width:380,
			height: 120,
        items:[{
           xtype: 'panel',
           border:false,
    	   layout:{  
             	type:'vbox',  
             	padding:'15 0 0 30', //上，左，下，右 
             	pack:'center',  
              	align:'middle'  
            },
           items:[uploadObj]
       }]
    }).show(); 
}
function getVal(theID)
{
	var theVal = $F(theID);
	if(theVal=='')
		theVal = '0';
	return theVal;	
}
function isNumberz(obj)
{
  		var checkOK = "0123456789.";
  		if(obj.value=='')
  			obj.value='0';
 		var checkStr = obj.value;
  		var allValid = true;
  		var decPoints = 0;
  		var allNum = "";
  		if (checkStr=="")
  			return false;
  		for (i = 0;  i < checkStr.length;  i++)
	    {
    		ch = checkStr.charAt(i);
    		for (j = 0;  j < checkOK.length;  j++)
     	    if (ch == checkOK.charAt(j))
       			 break;
    		if (j == checkOK.length)
   		    {
  			   allValid = false;
   			   break;
  		    }
    		if (ch == ".")
    		{
     			 allNum += ".";
     			 decPoints++;
  			 }
    	  else if (ch != ",")
      		allNum += ch;
  		}
  	if (decPoints > 1 || !allValid) 
  	{
  		alert(INPUT_NUMBER_VALUE);
  		obj.value=''; 
  		return false; 
  	}  
  	return true;	   
}
function isNumberf(obj)
{
  		var checkOK = "-0123456789.";
 		var checkStr = obj.value;
  		var allValid = true;
  		var decPoints = 0;
  		var allNum = "";
  		if (checkStr=="")
  			return;
  		var count = 0;
  		var theIndex = 0;
  		for (i = 0;  i < checkStr.length;  i++)
	    {
    		ch = checkStr.charAt(i);
    		if(ch=='-')
    		{
    			count=count+1;
    			theIndex=i+1;
    		}
    		for (j = 0;  j < checkOK.length;  j++)
     	    if (ch == checkOK.charAt(j))
       			 break;
    		if (j == checkOK.length)
   		    {
  			   allValid = false;
   			   break;
  		    }
    		if (ch == ".")
    		{
     			 allNum += ".";
     			 decPoints++;
  			 }
    	  else if (ch != ",")
      		allNum += ch;
  		}
  	if(count>1 || (count==1 && theIndex>1))
  			allValid=false;
  	if (decPoints > 1 || !allValid) 
  	{
  		alert(INPUT_NUMBER_VALUE);
  		obj.value='';  
  	}  	   
}
function testVal(theVal,obj)
{
	var thisVal = obj.value;
	if(thisVal=='')
		return;
	if(theVal!='')
	{	
		if((parseFloat(theVal)>0  && (parseFloat(theVal)<parseFloat(thisVal) || 0>parseFloat(thisVal))) || (parseFloat(theVal)<0  && (parseFloat(thisVal)>0 || parseFloat(thisVal)<parseFloat(theVal))))
		{
			alert(INPUT_RIGHT_VALUE);	
			obj.value='';
			obj.focus();
			obj.parentNode.parentNode.fireEvent("onClick");
		}	
	}		
}
//基本型指标的光标上下移动
function goMove(obj,theFlag,theDirect)
{
	var objId = obj.id.substr(0,obj.id.indexOf('_'));
	var objArray=objs.split(",");
	for(var i=0;i<objArray.length;i++)
  	{
  		if(objArray[i]==objId)
  		{  		   
  		   var temp=objArray[i+theDirect];
  		   var new_obj=document.getElementById(temp+theFlag);
  		   if(new_obj==null)
  		   	return;
  		   new_obj.focus();
  		   new_obj.parentNode.parentNode.fireEvent("onClick");
  		   break;
  		}
  	}
}
//加扣分指标的光标上下移动
function goUpDown(obj,theDirect)
{
	var objId = obj.id.substr(0,obj.id.indexOf('_'));
	var itemId = obj.id.substr(obj.id.indexOf('_')+1,obj.id.length);

	var objArray=objs.split(",");
	for(var i=0;i<objArray.length;i++)
  	{
  		if(objArray[i]==objId)
  		{  		   
  		   var temp=objArray[i+theDirect];
  		   var new_obj=document.getElementById(temp+'_'+itemId);
  		   if(new_obj==null)
  		   	return;
  		   new_obj.focus();
  		   new_obj.parentNode.parentNode.fireEvent("onClick");
  		   break;
  		}
  	}
}
//加扣分指标的光标左右移动
function goLeftRight(obj,theDirect)
{
	var objId = obj.id.substr(0,obj.id.indexOf('_'));
	var itemId = obj.id.substr(obj.id.indexOf('_')+1,obj.id.length);

	var itemsArray=items.split(",");
	for(var i=0;i<itemsArray.length;i++)
  	{
  		if(itemsArray[i]==itemId)
  		{  		   
  		   var temp=itemsArray[i+theDirect];
  		   var new_obj=document.getElementById(objId+'_'+temp);
  		   if(new_obj==null)
  		   	return;
  		   new_obj.focus();
  		   break;
  		}
  	}
}
//基本分计算(非排名)
function basicCalcu(obj)
{
	if(theRule=="3")	
		return;
	if(obj.value=='')
		obj.value='0';
	
	var objId = obj.id.substr(0,obj.id.indexOf('_'));
	var hashvo=new ParameterSet();
	hashvo.setValue("pratical",obj.value);
	hashvo.setValue("basic",getVal(objId+'_basic'));
	hashvo.setValue("standard",getVal(objId+'_standard'));
	hashvo.setValue("objectId",objId);
	hashvo.setValue("planId",thePlan);
	hashvo.setValue("point",thePoint);
    var request=new Request({method:'post',asynchronous:false,onSuccess:returnbasicCalcu,functionId:'9020020305'},hashvo);			
}
function returnbasicCalcu(outparamters)
{
	var objectId=outparamters.getValue("objectId");
	document.getElementById(objectId+'_df').value=outparamters.getValue("objDF");
	document.getElementById(objectId+'_add').value=outparamters.getValue("objAdd");
	document.getElementById(objectId+'_deduc').value=outparamters.getValue("objRedu");
}
//加扣分指标计算得分
function getDF(obj)
{
	var scoreStr='';
	if(obj.value=='')	
		obj.value='0';
		
	var objId = obj.id.substr(0,obj.id.indexOf('_'));
	var itemsArray=items.split(",");
	for(var i=0;i<itemsArray.length;i++)
  	{
		var item = itemsArray[i];
		if(item=='')
			break;
		var score_value=$F(objId+'_'+item);
		if(score_value=='')
			score_value='0';
		scoreStr+=objId+'_'+item+'='+score_value+'<@>';
	}

	var hashvo=new ParameterSet();
	hashvo.setValue("scoreStr",scoreStr);
	hashvo.setValue("objectId",objId);
	hashvo.setValue("planId",thePlan);
	hashvo.setValue("point",thePoint);
	var request=new Request({method:'post',asynchronous:false,onSuccess:returnDF,functionId:'9020020303'},hashvo);			
}
function returnDF(outparamters)
{	
	var objectId=outparamters.getValue("objectId");
	var score=outparamters.getValue("objDF");
	var cz=outparamters.getValue("cz");
	var objDF=$(objectId+'_df');	
	objDF.value=score;
    objDF=$(objectId+'_cz');	
	objDF.value=cz;
}
function save()
{
	var objstr='';
	var paramStr = $('paramStr');	
	if(thePointType!="0")	//加减分指标的保存
	{
		var scoreStr='';
		var dfScore='';
		
		var objArray=objs.split(",");
		for(var i=0;i<objArray.length;i++)
  		{
  			var objId = objArray[i];
  			if(objId=='')
  				break;
			var theObj = document.getElementById(objId);
			if(theObj!=null)
			{
				objstr+=objId+'<@>';	
				var df_value = $F(objId+'_df');
				if(df_value=='')
					df_value='0';
				dfScore+=objId+'='+df_value+'<@>';	
				var itemsArray=items.split(",");
				for(var j=0;j<itemsArray.length;j++)
  				{
  					var item = itemsArray[j];
  					if(item=='')
  						break;
					var score_value=$F(objId+'_'+item);
					if(score_value=='')
						score_value='0';
					scoreStr+=objId+'_'+item+'='+score_value+'<@>';
				}
			}
		}
		paramStr.value=objstr+"&"+scoreStr+"&"+dfScore;		
	}
	else if (thePointType=="0" && theRule=="0")	//基本指标录分规则
	{
		var fzScores = '';
		var objArray=objs.split(",");
		for(var i=0;i<objArray.length;i++)
  		{
  			var objId = objArray[i];
  			if(objId=='')
  				break;
			var theObj = document.getElementById(objId);
			if(theObj!=null)
			{
				fzScores+=objId+'='+getVal(objId+'_fz')+'<@>';
				objstr+=objId+'<@>';
			}
		}
		paramStr.value=objstr+"&"+fzScores;				
	}
	else if (thePointType=="0" && theRule!="0")	//基本指标非录分规则
	{
		var dfValue ='';
		var standardVal='';
		var basicVal='';
		var praticalVal='';
		var addVal='';
		var deducVal='';
		
		var objArray=objs.split(",");
		for(var i=0;i<objArray.length;i++)
  		{
  			var objId = objArray[i];
  			if(objId=='')
  				break;
			var theObj = document.getElementById(objId);
			if(theObj!=null)
			{	
				objstr+=objId+'<@>';
				dfValue+=objId+'_df='+getVal(objId+'_df')+'<@>';			
				standardVal+=objId+'_standard='+getVal(objId+'_standard')+'<@>';
				basicVal+=objId+'_basic='+getVal(objId+'_basic')+'<@>';
				praticalVal+=objId+'_pratical='+getVal(objId+'_pratical')+'<@>';
				addVal+=objId+'_add='+getVal(objId+'_add')+'<@>';
				deducVal+=objId+'_deduc='+getVal(objId+'_deduc')+'<@>';			
			}
		}	
		paramStr.value=objstr+"&"+standardVal+"&"+praticalVal+"&"+basicVal+"&"+addVal+"&"+deducVal+"&"+dfValue;	
	}	
	/*dataCollectForm.action="/performance/achivement/dataCollection/dataCollect.do?b_save=link&planId="+thePlan+"&pointID="+thePoint;
	dataCollectForm.submit();*/	
	var hashvo=new ParameterSet();			
	hashvo.setValue("planId",thePlan);
	hashvo.setValue("point",thePoint);
	hashvo.setValue("paramStr",paramStr.value);
	hashvo.setValue("rule",theRule);
	hashvo.setValue("pointype",thePointType);
	var request=new Request({method:'post',asynchronous:false,onSuccess:refrshTable,functionId:'9020020309'},hashvo);	
	
}
function refrshTable(){
	dataCollectForm.action="/performance/achivement/dataCollection/dataCollect.do?b_query=link&planId="+thePlan+"&pointID="+thePoint;
	dataCollectForm.submit();
}
function exportExcel()
{
	var hashvo=new ParameterSet();
	hashvo.setValue("planID",thePlan);
	var request=new Request({method:'post',asynchronous:false,onSuccess:showfile,functionId:'9020020306'},hashvo);
}
function showfile(outparamters)
{
	var outName=outparamters.getValue("outName");
//	outName=getDecodeStr(outName);
//	var name=outName.substring(0,outName.length-1)+".xls";	
	//xus 20/4/30 vfs改造
	var win=open("/servlet/vfsservlet?fromjavafolder=true&fileid="+outName,"excel");	
}
function downloadTemplate()
{
	var determine = $('determine');	
	var hashvo=new ParameterSet();
	hashvo.setValue("planID",thePlan);
	hashvo.setValue("determine",determine.value);
	var request=new Request({method:'post',asynchronous:false,onSuccess:showfile,functionId:'9020020307'},hashvo);
}

// 计算
function computeScore()
{
	var isReadOnly = $('isReadOnly');	
	if(isReadOnly.value==1)
	{
		Ext.showAlert("当前计划为评估状态不允许计算！");
		return;
	}
	var newPoint=document.getElementsByName("point");
	var pointObject=newPoint[0];
	var point="";
	for(var i=0;i<pointObject.options.length;i++){
		if(pointObject.options[i].selected){
			point=pointObject.options[i].value;
		}
	}
	dataCollectForm.action="/performance/achivement/dataCollection/dataCollect.do?b_compute=link&planId="+thePlan+"&point="+point; //+"&pointID="+thePoint;
	dataCollectForm.submit();
	window.location.href = window.location.href;
}

