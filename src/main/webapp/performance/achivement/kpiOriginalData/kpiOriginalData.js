	// 刷新事件对象类型
	function refreshTree(obj)
	{
		document.kpiOriginalDataForm.objecType.value = obj.value;
		kpiOriginalDataForm.action="/performance/achivement/kpiOriginalData/orgTree.do?b_query=link&action=kpiOriginalDataList.do&treetype=duty&objecType="+obj.value;
		kpiOriginalDataForm.target="il_body";
		kpiOriginalDataForm.submit();
	}
	
	// 按 考核周期查询
	function searchCycle()
	{
		kpiOriginalDataForm.action="/performance/achivement/kpiOriginalData/kpiOriginalDataList.do?b_query=search&refreshKey=changeCycle&refreshData=yesOk";
		kpiOriginalDataForm.submit();
	}
		
	// 按 年度/季度/月度 查询
	function search()
	{
		kpiOriginalDataForm.action="/performance/achivement/kpiOriginalData/kpiOriginalDataList.do?b_query=search&refreshKey=saveKey&refreshData=yesOk";
		kpiOriginalDataForm.submit();
	}
	
	// 按 年度/季度/月度 查询
	function checkSearch()
	{
		kpiOriginalDataForm.action="/performance/achivement/kpiOriginalData/kpiOriginalDataList.do?b_query=checkSearch&refreshKey=saveKey&refreshData=yesOk";
		kpiOriginalDataForm.submit();
	}
			
	//保存业绩任务书标准值
	function saveDataValue()
	{
		var str=document.kpiOriginalDataForm.object_ids.value;
  		var s_str="";
  		var arr = str.split(",");
  		for(var i=0;i<arr.length;i++)
  		{
    		if(arr[i]==null||trim(arr[i]).length==0)
     			continue;
     		if(document.getElementById("s_"+arr[i])!=null)
     		{
         		s_str+=","+arr[i]+"/"+document.getElementById("s_"+arr[i]).value;
     		}   		
  		}
 		
  		var hashvo=new ParameterSet();
  		hashvo.setValue("s_str",s_str);		
  		var request=new Request({asynchronous:false,onSuccess:save_ok,functionId:'9020020406'},hashvo); 
	}
	function save_ok(outparameters)
	{    		
   		var msg = getDecodeStr(outparameters.getValue("msg"));
   		if(msg=='ok')
   		{
     		kpiOriginalDataForm.action="/performance/achivement/kpiOriginalData/kpiOriginalDataList.do?b_query=checkSearch&refreshData=yesOk&freshPage=false";
			kpiOriginalDataForm.submit();
   		}else
   		{
      		alert(msg);
      		return;
   		}  
	}
		
	// 删除KPI指标得分记录	
	function delDataValue()
	{
		
		var str="";
		for(var i=0;i<document.kpiOriginalDataForm.elements.length;i++)
		{
			if(document.kpiOriginalDataForm.elements[i].type=="checkbox")
			{					
				var ff = kpiOriginalDataForm.elements[i].name.substring(0,18);						
				if(document.kpiOriginalDataForm.elements[i].checked==true && ff=='setlistform.select')
				{
					str+=document.kpiOriginalDataForm.elements[i+1].value+"/";
				}
			}
		}
		if(str.length==0)
		{
			alert("请选择记录，再进行操作！");
			return;
		}
		else
		{		
			if (confirm(IS_DEL_NOT))
			{
//				document.kpiOriginalDataForm.action="/performance/achivement/kpiOriginalData/kpiOriginalDataList.do?b_delete=del&deletestr="+str;
//				document.kpiOriginalDataForm.submit();	
								
				var hashvo=new ParameterSet();
		  		hashvo.setValue("deletestr",str);		
		  		var request=new Request({asynchronous:false,onSuccess:delDataValue_ok,functionId:'9020020404'},hashvo); 												
			}
		}		
	}
	function delDataValue_ok(outparameters)
	{    		
   		var msg = getDecodeStr(outparameters.getValue("msg"));
   		if(msg=='have03')
   			alert("生效的记录不允许删除！请先退回再删除！");
     	kpiOriginalDataForm.action="/performance/achivement/kpiOriginalData/kpiOriginalDataList.do?b_query=checkSearch&refreshData=yesOk&freshPage=false";
		kpiOriginalDataForm.submit();
   		  
	}
	
			
	//可以输入正负整数
	function IsDigit_(evt) 
	{
		var evt = window.event||evt;
        var keynum = evt.keyCode||evt.which;
        var num = String.fromCharCode(keynum);
        var numtest = /^-?\d+(\\.\\d+)?$/;//正负整数或者小数
        if(keynum==8 || keynum==13 || keynum==46) 
        {
            return true;
        }
      return numtest.test(num); 
	}
	
	// 生效/退回 KPI记录
	function comBackData(obj)
	{
		var str="";
		for(var i=0;i<document.kpiOriginalDataForm.elements.length;i++)
		{
			if(document.kpiOriginalDataForm.elements[i].type=="checkbox")
			{					
				var ff = kpiOriginalDataForm.elements[i].name.substring(0,18);						
				if(document.kpiOriginalDataForm.elements[i].checked==true && ff=='setlistform.select')
				{
					str+=document.kpiOriginalDataForm.elements[i+1].value+"/";
				}
			}
		}
		if(str.length==0)
		{
			alert("请选择记录，再进行操作！");
			return;
		}
		else
		{			
			if(obj=='back')
			{				
				if(confirm("您确认退回吗？"))
				{
					document.kpiOriginalDataForm.action="/performance/achivement/kpiOriginalData/kpiOriginalDataList.do?b_compareBack=link&freshPage=false&opt="+obj+"&comparestr="+str;
					document.kpiOriginalDataForm.submit();	
				}
			}else
			{
				document.kpiOriginalDataForm.action="/performance/achivement/kpiOriginalData/kpiOriginalDataList.do?b_compareBack=link&freshPage=false&opt="+obj+"&comparestr="+str;
				document.kpiOriginalDataForm.submit();
			}
		}
	}

	
/***************************************************************  KPI指标维护  *******************************************************************************/	


// 查询KPI指标	
function searchKpiTarget()
{
	document.kpiOriginalDataForm.action="/performance/achivement/kpiOriginalData/kpiTargetAssertList.do?b_query=search";
	document.kpiOriginalDataForm.submit();	
}
	
// 新增KPI指标	
function addTarget()
{
	document.kpiOriginalDataForm.action="/performance/achivement/kpiOriginalData/kpiTargetAssertList.do?b_insert=add";
	document.kpiOriginalDataForm.submit();	
}	
// 编辑KPI指标	
function kpiTargetEdit(item_id)
{
	document.kpiOriginalDataForm.action="/performance/achivement/kpiOriginalData/kpiTargetAssertList.do?b_insert=add&item_id="+item_id;
	document.kpiOriginalDataForm.submit();	
}	
// 撤销/删除KPI指标	
function delAboKpiTarget(obj)
{
	
	var str="";
	for(var i=0;i<document.kpiOriginalDataForm.elements.length;i++)
	{
		if(document.kpiOriginalDataForm.elements[i].type=="checkbox")
		{					
			var ff = kpiOriginalDataForm.elements[i].name.substring(0,18);						
			if(document.kpiOriginalDataForm.elements[i].checked==true && ff=='setlistform.select')
			{
				str+=document.kpiOriginalDataForm.elements[i+1].value+"/";
			}
		}
	}
	if(str.length==0)
	{
		alert("请选择记录，再进行操作！");
		return;
	}
	else
	{
		var info;
		if(obj=='del')
		{
//			info = "删除所选指标，将同时删除引用该指标的人员和\n组织单元的所有相关记录，确认删除吗？"
			var hashvo=new ParameterSet();
			hashvo.setValue("opt",obj);
		  	hashvo.setValue("deletestr",str);		
		  	var request=new Request({asynchronous:false,onSuccess:delKpiTarget_ok,functionId:'9020020411'},hashvo);			
			
		}else
		{
			info = "确认撤销KPI指标？";
			if (confirm(info))
			{
				document.kpiOriginalDataForm.action="/performance/achivement/kpiOriginalData/kpiTargetAssertList.do?b_delete=del&opt="+obj+"&deletestr="+str;
				document.kpiOriginalDataForm.submit();					
			}
		}
	}		
}
function delKpiTarget_ok(outparameters)
{    		
   	var msg = getDecodeStr(outparameters.getValue("msg"));
   	var obj=outparameters.getValue("opt");  
   	var str=outparameters.getValue("delStr");  
   	
   	var info="";
   	if(msg=='useded')
   		info = "指标已被使用，强制删除会将关联它的KPI原始数据也删除！确定删除吗？";
   	else
   		info = "确认删除吗？";		
	if (confirm(info))
	{
		document.kpiOriginalDataForm.action="/performance/achivement/kpiOriginalData/kpiTargetAssertList.do?b_delete=del&opt="+obj+"&deletestr="+str;
		document.kpiOriginalDataForm.submit();					
	}  		  
}

// 保存KPI指标
function saveKpiTarget()
{
	var targetName = document.getElementById('itemdesc').value;
//	var item_type_desc = document.getElementById('item_type_desc').value;

/*	
	var hidcategories=$F('hidKpiItemType');
   	if(hidcategories.indexOf("\‘")>-1||hidcategories.indexOf("\”")>-1||hidcategories.indexOf("\'")>-1||hidcategories.indexOf("\"")>-1)
  	{	
       	alert("分类名称不能包含\’或\"或\’或\”");
    	return false;
  	}
*/	
	var hidKpiItemType = document.getElementById('hidKpiItemTypeSign').value;
	
	var unitB0110 = document.getElementById('b0110').value;
	if(targetName==null || targetName=='undefined' || trimStr(targetName)=='')
	{
		alert("指标名称不能为空！");
		return;
	}
	
//	if(item_type_desc==null || item_type_desc=='undefined' || trimStr(item_type_desc)=='')
//	{
//		alert("指标类别不能为空！");
//		return;
//	}
	if(hidKpiItemType==null || hidKpiItemType=='undefined' || trimStr(hidKpiItemType)=='')
	{
		alert("指标类别不能为空！");
		return;
	}
	document.kpiOriginalDataForm.hidKpiItemType.value = hidKpiItemType;
			
	var start_date = document.getElementById('start_date').value;
	var end_date = document.getElementById('end_date').value;
	//防止手工输入的时候起始时间和结束时间有点有横杆，这里全部替换比较
    if(start_date!='' && end_date!='')
    	if(replaceAll(start_date,"-",".")>replaceAll(end_date,"-","."))	
    	{
    		alert("有效日期起应该小于有效日期止！");
    	    return;
    	}   	
/*    		
	if(unitB0110==null || unitB0110=='undefined' || trimStr(unitB0110)=='')
	{
		alert("归属单位不能为空！");
		return;
	}
*/	
	document.kpiOriginalDataForm.action="/performance/achivement/kpiOriginalData/kpiTargetAssertList.do?b_save=save";
	document.kpiOriginalDataForm.submit();	
}
	
// 返回KPI指标维护页面	
function goback()
{
	document.kpiOriginalDataForm.action="/performance/achivement/kpiOriginalData/kpiTargetAssertList.do?b_query=search";
	document.kpiOriginalDataForm.submit();	
}	

	
/***************************************************************  KPI指标维护  *******************************************************************************/

	
/*	
	function replaceAll(str, sptr, sptr1)
	{
		while (str.indexOf(sptr) >= 0)
		{
   			str = str.replace(sptr, sptr1);
		}
		return str;
	}

*/
		
	