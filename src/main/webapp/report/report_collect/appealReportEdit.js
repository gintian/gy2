
function openWin(config){
    Ext.create("Ext.window.Window",{
    	id:config.id,
    	width:config.width,
    	height:config.height,
    	title:config.title,
    	resizable:false,
    	autoScroll:false,
    	modal:true,
    	renderTo:Ext.getBody(),
    	html:"<iframe style='background-color:#ffffff' frameborder='0' SCROLLING=NO height='100%' width='100%' src='"+config.theurl+"'></iframe>"
	    }).show();	
}
function change()
	{
		editReportForm.action="/report/report_collect/reportOrgCollecttree.do?b_query=link&a_code="+unitcode+"&operateObject=2";
		editReportForm.submit();
		
	}
		
		
	function go_left(ite){
	    var temp_str = ite.name;
	    var post = temp_str.indexOf("_");
	    var temp_str1 = temp_str.substring(post+1);
	    if(temp_str1!=0)
	    {
	    	var next_item;
	    	var next_item1=temp_str1-1;
	    	var next_item = "document.editReportForm."+temp_str.substring(0,post+1);
	    	next_item1 = next_item + next_item1;
	    	var new_object=eval(next_item1);	    	
	    	if(new_object!=null&&new_object.type!='hidden')
	    		new_object.focus();
	    }
	  }
	  
	function go_right(ite){
	    var temp_str = ite.name;
	    var post = temp_str.indexOf("_");
	    var temp_str1 = temp_str.substring(post+1);
	    var next_item;
	    var next_item1=parseInt(temp_str1)+1;
	    var next_item = "document.editReportForm."+temp_str.substring(0,post+1);
	    next_item1 = next_item + next_item1;
	    var new_object=eval(next_item1);
	    if(new_object!=null&&new_object.type!='hidden')
	    	new_object.focus();
	 
	  }
	  
	  
	function go_up(ite){
	    var temp_str = ite.name;
	    var post = temp_str.indexOf("_");
	    var temp_str1 = temp_str.substring(1,post);
	    var next_item1=parseInt(temp_str1)-1;
	    var next_item = "document.editReportForm.a"+next_item1+temp_str.substring(post);
	    var new_object=eval(next_item);
	    if(new_object!=null&&new_object.type!='hidden')
	    	new_object.focus();

	  }
	  
	  
	function go_down(ite){
	   var temp_str = ite.name;
	    var post = temp_str.indexOf("_");
	    var temp_str1 = temp_str.substring(1,post);
	    var next_item1=parseInt(temp_str1)+1;
	    var next_item = "document.editReportForm.a"+next_item1+temp_str.substring(post);
	    var new_object=eval(next_item);
	    if(new_object!=null&&new_object.type!='hidden')
	    	new_object.focus();

	  }
	  
	  
	  
	  function check_data(iteName,npercent)
	  {
	  	var a_object=eval("document.editReportForm."+iteName);
	  	
	  	if(a_object.value!='')
	  	{
		  	if(!checkNUM3(a_object,15,npercent))
		  	{
		  		a_object.value="";
		  		a_object.focus();
		  		return;
		  	}
	  	}
	 
	  }
	  
	  function check_data2(iteName,intlen,npercent)
	  {
	  	var a_object=eval("document.editReportForm."+iteName);
	  	
	  	if(a_object.value!='')
	  	{
		  	if(!checkNUM3(a_object,intlen,npercent))
		  	{
		  		a_object.value="";
		  		a_object.focus();
		  	}
	  	}
	  
	  }
	  
	  
	   //检验参数中是否有必填项
	  function isMustFill()
	  {
	 
	 	 var para_arr=param_str.split("/");
	 	
	  	 for(var i=0;i<para_arr.length;i++)
	  	 {
	  	 	var temp=para_arr[i].split("#");
	  	 	if(temp[2]==1)
	  	 	{
	  	 		var a_object;
	  	 		if(temp[1]==REPORT_CODE)
	  	 			a_object=document.getElementsByName(temp[0]+".value");
	  	 		else
	  	 			a_object=eval("document.editReportForm."+temp[0]);
				
				if(a_object.value==""||a_object.value==" ")
	  	 		{
	  	 				alert(PARAM_NO_VALUE+"！");
	  	 				return false;
	  	 		}
	  	 	}
	  	 }
	  	 return true;
	  }
	  
	  
	    
	    
	  //得到参数值
	  function get_ParamValues()
	  {
	  	var values="";
	  	if(param_str!=''&&param_str!=' ')
	  	{
		  	var param=param_str.split("/");
		 	for(var i=0;i<param.length;i++)
		  	{
		  		
		  		var temp=param[i];
		  		var temp_arr=temp.split("#");
		  		var a_object;
		  		if(temp_arr[1]==REPORT_CODE)
		  		{	  			
		  			a_object=$(temp_arr[0]+".value");		  			
		  		}
		  		else
		  		{
		  			a_object=eval("document.editReportForm."+temp_arr[0]);  			
		  		}
		  		if(a_object.value!=''||a_object.value!=' ')
		  		{	
		  				if(a_object.value.replace(/[^\x00-\xff]/g,"**").length>temp_arr[4]*1)
		  				{
		  					if(temp_arr[1]!=REPORT_TEXT)
		  					{	 
		  					alert(a_object.value+" "+OVERDEFINELENGTH+temp_arr[4]+"，"+DONOTOPERATER);
		  					return false;
		  					}
		  				}
		  				if(a_object.value.indexOf("#")!=-1)
		  				{
		  				    alert("参数值不支持 # 符号!");
		  				    return false;
		  				}
		  				values+="/"+temp_arr[0]+"#"+a_object.value+"#"+temp_arr[3]+"#"+temp_arr[1];
		  		}	
		  	}
		}
	  	return values;
	  }	
	  
	  function returnInfo(outparamters)
	  {
	  	var info=outparamters.getValue("info");
	  	if(info==1)
	  	{
				alert(SAVESUCCESS+"！");
				
		}
		else if(info==0)
				alert(SAVEFAILED+"！");
		
	  }
	  
	  //处理归档操作返回的信息
	  function return_pigeonhole(outparamters)
	  {
	  	var info=outparamters.getValue("info");
	  	if(info==1)
	  	{
				alert(COLLECTSUCCESS+"！");
		}
		else if(info==2)
	  	{
				alert(REPORT_INFO21+"！");
		}
		else if(info==3)
	  	{
				alert(REPORT_INFO22+"！");
		}
		else if(info==4)
	  	{
				alert(COLLECTERROR+"！");
		}else if(info==9){
				alert("归档类型要与该表取数类型一致");
		}
		
	  }
	  
	  //处理上报操作返回的信息
	  function returnInfo_appeal(outparamters)
	  {
	  //1:上报成功 2：进行表内、表间校验，不正确 3：报表保存错误，上报不成功 4：刚有人上报成功，不再给与上报 5:上报不成功
	    var info=outparamters.getValue("info");
	  	if(info==1)
	  	{
				alert(APPEALSUCCESS+"！");
				var appealButton=eval("document.editReportForm.b_appeal");
				appealButton.style.display="none";
				parent.mil_menu.document.location.reload();
				
		}
		else if(info==2)
				alert(REPORT_INFO23+" ！");
	    else if(info==3)
				alert(REPORT_INFO24+"！");
		else if(info==4)
				alert(REPORT_INFO25+"！");
		else if(info==5)
				alert(APPEALFAILED+"！");
		
	  }
	  
	  
	  
	  //报表上报——2
	  function appeal_2(outparamters)
	  {
	  	var info=outparamters.getValue("info");
	  	if(info==0){
	  		alert(SAVEVALUEERROR+"！");		
	  		return;
	  	}
	   editReportForm.action="/report/edit_report/editReport.do?b_initAppeal=appeal&tabid="+tabid+"&status="+status;
	   editReportForm.submit();
	
	  }
	  
	   //报表上报——1
	  function appeal_1()
	  {
	  	if(!isMustFill())
		  			return;
			  	var hashvo=new ParameterSet();
			    hashvo.setValue("tabid",tabid); 
			   	hashvo.setValue("rows",rows);
			   	hashvo.setValue("cols",cols);
			   	hashvo.setValue("unitcode",unitcode);
			   	hashvo.setValue("operateObject",operateObject);
			    var new_result=""; //new Array();
			    for(var i=0;i<rows;i++)
			    {
			    	var a_values='';
			    	for(var j=0;j<cols;j++)
			    	{
			    		var a_object=eval("document.editReportForm.a"+i+"_"+j);
			    		if(a_object.value==''||a_object.value==' ')
			    		{
			    			a_values+='/0';
			    		}
			    		else
			    			a_values+='/'+a_object.value;
			    	}
			     //   new_result[i]=a_values.substring(1);
			    	new_result+="`"+a_values.substring(1);
			    }
			    hashvo.setValue("results",new_result);
			    if(get_ParamValues()==false&&param_str!=''&&param_str!=' ')
			    	return;
			    hashvo.setValue("param",getEncodeStr(get_ParamValues().substring(1)));
			   	var In_paramters="flag=1"; 	
				var request=new Request({method:'post',asynchronous:false,parameters:In_paramters,onSuccess:appeal_2,functionId:'03020000002'},hashvo);			
	  }
	  
	   //保存
	  function save()
	  {
	  	if(!isMustFill())
	  		return;
	  	var hashvo=new ParameterSet();
	    hashvo.setValue("tabid",tabid); 
	   	hashvo.setValue("rows",rows);
	   	hashvo.setValue("cols",cols);
	   	hashvo.setValue("unitcode",unitcode);
	    var new_result=""; //new Array();
	    for(var i=0;i<rows;i++)
	    {
	    	var a_values='';
	    	for(var j=0;j<cols;j++)
	    	{
	    		var a_object=eval("document.editReportForm.a"+i+"_"+j);
	    		if(a_object.value==''||a_object.value==' ')
	    		{
	    			a_values+='/0';
	    		}
	    		else
	    			a_values+='/'+a_object.value;
	    	}
	        // new_result[i]=a_values.substring(1);
	    	new_result+="`"+a_values.substring(1);
	    }
	    hashvo.setValue("results",new_result);
	    if(get_ParamValues()==false&&param_str!=''&&param_str!=' ')
			    	return;
	    hashvo.setValue("param",getEncodeStr(get_ParamValues().substring(1)));
	    hashvo.setValue("operateObject",operateObject);
	   	var In_paramters="flag=1"; 		
		var request=new Request({method:'post',asynchronous:false,parameters:In_paramters,onSuccess:returnInfo,functionId:'03020000002'},hashvo);

	  }
	  
	   function returnInfo2(outparamters)
	   {
	    	var info=outparamters.getValue("info");	    	
			var str=document.location.href;
	    	document.location=str;
		
	   }
	  
	  //清零
	  function clears()
	  {
	  
	  	if(!confirm(ENTERCLEARDATA+"？"))
	  		return;
	 	 	var hashvo=new ParameterSet();
	    hashvo.setValue("tabid",tabid); 
	   	hashvo.setValue("rows",rows);
	   	hashvo.setValue("cols",cols);
	   	hashvo.setValue("unitcode",unitcode);
	    var new_result=""; //new Array();
	    for(var i=0;i<rows;i++)
	    {
	    	var a_values='';
	    	for(var j=0;j<cols;j++)
	    	{
	    		var a_object=eval("document.editReportForm.a"+i+"_"+j);
	    		a_values+='/0';
	    		
	    	}
	      //  new_result[i]=a_values.substring(1);
	    	new_result+="`"+a_values.substring(1);
	    }
	    hashvo.setValue("results",new_result);
	     if(get_ParamValues()==false&&param_str!=''&&param_str!=' ')
			    	return;
	    hashvo.setValue("param",getEncodeStr(get_ParamValues().substring(1)));
	    hashvo.setValue("operateObject",operateObject);
	   	var In_paramters="flag=1"; 		
		var request=new Request({method:'post',asynchronous:false,parameters:In_paramters,onSuccess:returnInfo2,functionId:'03020000002'},hashvo);
	   	
	  }
	
		//归档
	  	var pinfos=new Array();
	  	var year_value=new Array();
		function pigeonhole()
		{
			year_value=new Array();
			if(!confirm(CONFIRMCOLLECTDATA+"?"))
				return;
			pinfos[0]=unitcode;
			pinfos[1]=tabid;
			pinfos[2]=selfType
			pinfos[3]="2";  //1：编辑没上报表 2：编辑上报后的表
			//var thecodeurl="/report/edit_report/selectYear.jsp"; 
			var thecodeurl="/report/edit_report/editReport.do?b_pigeonhole=query&tabid="+tabid; 
			
			var config = {
					width:450,
					height:350,
					title:'',
					theurl:thecodeurl,
					id:'pigeonhole'
				}
			openWin(config);
			Ext.getCmp("pigeonhole").addListener('close',function(){validateIsPigeonhole()});
		}
		//校验是否已有数据
		function validateIsPigeonhole(){
			if(year_value==undefined||year_value.length==0||!isMustFill())
			{			
				return;
			}else{
				var hashvo=new ParameterSet();
			    hashvo.setValue("tabid",tabid); 
			    hashvo.setValue("reportType",year_value[0]);
				hashvo.setValue("year",year_value[1]);
				if(year_value[0]>2)
				   hashvo.setValue("count",year_value[2]);
			    if(year_value[0]==6)
				   hashvo.setValue("week",year_value[3]);
				 if(year_value[4]==null){
				   	hashvo.setValue("auto_archive","null");
				 }else{
				   	hashvo.setValue("auto_archive",year_value[4]);
				 }
			  	hashvo.setValue("operateObject",'2');	
			  	var In_paramters="flag=1"; 		
		        hashvo.setValue("appealUnitcode",unitcode);
				var request=new Request({method:'post',asynchronous:false,parameters:In_paramters,onSuccess:pigeonhole2,functionId:'03020000014'},hashvo);
			}
		}
		
		function pigeonhole2(outparamters)
		{
				var isData=outparamters.getValue("isData");
				if(isData=='yes')
				{
					if(!confirm(REPORT_INFO26+"?"))
						return;
				}
		
				var hashvo=new ParameterSet();
			    hashvo.setValue("tabid",tabid); 
			   	hashvo.setValue("rows",rows);
			   	hashvo.setValue("cols",cols);
			   	hashvo.setValue("selfType",selfType);
			   	hashvo.setValue("reportType",outparamters.getValue("reportType"));
			   	selfType=outparamters.getValue("reportType");	
			   	hashvo.setValue("year",outparamters.getValue("year"));
			   	if(outparamters.getValue("reportType")>2)
			   		hashvo.setValue("count",outparamters.getValue("count"));
			   	if(outparamters.getValue("reportType")==6)
			   		hashvo.setValue("week",outparamters.getValue("week"));
			    var new_result=new Array();
			    for(var i=0;i<rows;i++)
			    {
			    	var a_values='';
			    	for(var j=0;j<cols;j++)
			    	{
			    		var a_object=eval("document.editReportForm.a"+i+"_"+j);
			    		if(a_object.value==''||a_object.value==' ')
			    		{
			    			a_values+='/0';
			    		}
			    		else
			    			a_values+='/'+a_object.value;
			    	}
			        new_result[i]=a_values.substring(1);
			    }
			    hashvo.setValue("results",new_result);
			     if(get_ParamValues()==false&&param_str!=''&&param_str!=' ')
			    	return;
			    hashvo.setValue("param",getEncodeStr(get_ParamValues().substring(1)));
			    hashvo.setValue("operateObject",'2');
			    hashvo.setValue("appealUnitcode",unitcode);
			   	var In_paramters="flag=1"; 		
				var request=new Request({method:'post',asynchronous:false,parameters:In_paramters,onSuccess:return_pigeonhole,functionId:'03020000004'},hashvo);
		}
		
		//总计算
		function totalCount(outparamters)
		{
			 editReportForm.action="/report/edit_report/editReport.do?b_initFormula=initFormula&flag=c&status="+status;
			 editReportForm.submit();
		}
		
		
		//表内计算  
		function reportInnerCount(outparamters)
		{				
			   editReportForm.action="/report/edit_report/editReport.do?b_initFormula=initFormula&flag=a&status="+status;
			   editReportForm.submit();
		}
		
		//表间计算
		function reportCount(outparamters)
		{			
			   editReportForm.action="/report/edit_report/editReport.do?b_initFormula=initFormula&flag=b&status="+status;
			   editReportForm.submit();
		}
		
		//表计算
		// flag:1 表内  2：表间  3:总计算
		function reportsCount(flag)
		{
				if(!isMustFill())
		  			return;
			  	var hashvo=new ParameterSet();
			    hashvo.setValue("tabid",tabid); 
			   	hashvo.setValue("rows",rows);
			   	hashvo.setValue("cols",cols);
			    var new_result=""; //new Array();
			    for(var i=0;i<rows;i++)
			    {
			    	var a_values='';
			    	for(var j=0;j<cols;j++)
			    	{
			    		var a_object=eval("document.editReportForm.a"+i+"_"+j);
			    		if(a_object.value==''||a_object.value==' ')
			    		{
			    			a_values+='/0';
			    		}
			    		else
			    			a_values+='/'+a_object.value;
			    	}
			       // new_result[i]=a_values.substring(1);
			    	new_result+="`"+a_values.substring(1);
			    }
			    hashvo.setValue("results",new_result);
			     if(get_ParamValues()==false&&param_str!=''&&param_str!=' ')
			    	return;
			    hashvo.setValue("param",getEncodeStr(get_ParamValues().substring(1)));
			    hashvo.setValue("operateObject",operateObject);
			    hashvo.setValue("unitcode",unitcode); 
			   	var In_paramters="flag=1"; 	
			   	if(flag==1)	
					var request=new Request({method:'post',asynchronous:false,parameters:In_paramters,onSuccess:reportInnerCount,functionId:'03020000002'},hashvo);
				else if(flag==2)
					var request=new Request({method:'post',asynchronous:false,parameters:In_paramters,onSuccess:reportCount,functionId:'03020000002'},hashvo);
				else if(flag==3)
					var request=new Request({method:'post',asynchronous:false,parameters:In_paramters,onSuccess:totalCount,functionId:'03020000002'},hashvo);
		}	
		
		
		
		
		
		function executeGridObjectArray_2(n)
		{
			var text_2array=new Array(rows);			
			for(var a=0;a<rows;a++)
			{
				var cols_ob=new Array(cols);
				for(var b=0;b<cols;b++)
				{	
						if(n==1)			
							cols_ob[b]=eval("aa"+a+"_"+b);    	//生成表格的2维数组对象
						else
						{
							var a_object=eval("document.editReportForm.a"+a+"_"+b);
				    		if(a_object.value==''||a_object.value==' ')
				    		{
				    			cols_ob[b]=0;
				    		}
				    		else
				    			cols_ob[b]=a_object.value;
						}	
				}
				text_2array[a]=cols_ob;
			}
			return text_2array;
		}
		
		
		//处理表内（即时）校验结果
		var infos=new Array();
		function returnInfo_promptlyValidate(outparamters)
	  	{
			var info=outparamters.getValue("info");				
			infos[0]=info;
			infos[1]= executeGridObjectArray_2(1);			
			var thecodeurl="/report/edit_report/promptlyValidate.jsp";
			
			var config = {
					width:370,
					height:260,
					title:'表内校验',
					theurl:thecodeurl,
					id:'promptlyValidatWin'
				}
			openWin(config);
					
		}
		
		
		//处理表间校验结果
		var reportValInfo=new Array();
		function returnInfo_reportValidate(outparamters)
		{
			reportValInfo=outparamters.getValue("info");								
			var thecodeurl="/report/edit_report/reportValidate.jsp";
			
			var config = {
					width:370,
					height:260,
					title:'表间校验',
					theurl:thecodeurl,
					id:'reportValidate'
				}
			openWin(config);
		}
		
		
		//表内即时校验
		function promptlyValidate()
		{
				nums=-1;
			   var hashvo=new ParameterSet();
			    hashvo.setValue("tabid",tabid); 
			   	hashvo.setValue("rows",rows);
			   	hashvo.setValue("cols",cols);
			   	hashvo.setValue("unitcode",unitcode);
			    var new_result=new Array();
			    for(var i=0;i<rows;i++)
			    {
			    	var a_values='';
			    	for(var j=0;j<cols;j++)
			    	{
			    		var a_object=eval("document.editReportForm.a"+i+"_"+j);
			    		if(a_object.value==''||a_object.value==' ')
			    		{
			    			a_values+='/0';
			    		}
			    		else
			    			a_values+='/'+a_object.value;
			    	}
			        new_result[i]=a_values.substring(1);
			    }
			    hashvo.setValue("results",new_result);		
			    hashvo.setValue("operateObject","2")		  
			   	var In_paramters="flag=1"; 		
				var request=new Request({method:'post',asynchronous:false,parameters:In_paramters,onSuccess:returnInfo_promptlyValidate,functionId:'03020000005'},hashvo);
			   	  
		}
		
		var info=new Array();
		function returnInfo_collectValidate(outparamters)
		{
			info=getDecodeStr(outparamters.getValue("info"));								
			var thecodeurl="/report/edit_report/reportCollectValidate.jsp";
			
			var config = {
					width:480,
					height:300,
					title:'汇总校验',
					theurl:thecodeurl,
					id:'collectValidate'
				}
			openWin(config);
		
		}
		
		
		//汇总校验
		function reportCollectValidate()
		{
				var hashvo=new ParameterSet();
			    hashvo.setValue("tabid",tabid); 
			   	hashvo.setValue("rows",rows);
			   	hashvo.setValue("cols",cols);
			   	hashvo.setValue("unitcode",unitcode);
			    var new_result=new Array();
			    for(var i=0;i<rows;i++)
			    {
			    	var a_values='';
			    	for(var j=0;j<cols;j++)
			    	{
			    		var a_object=eval("document.editReportForm.a"+i+"_"+j);
			    		if(a_object.value==''||a_object.value==' ')
			    		{
			    			a_values+='/0';
			    		}
			    		else
			    			a_values+='/'+a_object.value;
			    	}
			        new_result[i]=a_values.substring(1);
			    }
			    hashvo.setValue("results",new_result);
			    hashvo.setValue("operateObject","2");		 
			   	var In_paramters="flag=1"; 		
				var request=new Request({method:'post',asynchronous:false,parameters:In_paramters,onSuccess:returnInfo_collectValidate,functionId:'03020000016'},hashvo);
		
		}
		
		//表间校验
		function reportValidate()
		{
			 
			 var hashvo=new ParameterSet();
			    hashvo.setValue("tabid",tabid); 
			   	hashvo.setValue("rows",rows);
			   	hashvo.setValue("cols",cols);
			   	hashvo.setValue("unitcode",unitcode);
			    var new_result=new Array();
			    for(var i=0;i<rows;i++)
			    {
			    	var a_values='';
			    	for(var j=0;j<cols;j++)
			    	{
			    		var a_object=eval("document.editReportForm.a"+i+"_"+j);
			    		if(a_object.value==''||a_object.value==' ')
			    		{
			    			a_values+='/0';
			    		}
			    		else
			    			a_values+='/'+a_object.value;
			    	}
			        new_result[i]=a_values.substring(1);
			    }
			    hashvo.setValue("results",new_result);
			    hashvo.setValue("operateObject","2");		 
			   	var In_paramters="flag=1"; 		
				var request=new Request({method:'post',asynchronous:false,parameters:In_paramters,onSuccess:returnInfo_reportValidate,functionId:'03020000006'},hashvo);
		}	  
	  
	  
	  //参数设置
	  function parameterSet()
	  {
	  	editReportForm.action="/report/edit_report/parameter.do?br_send=send&encryptParam=" + encryptParam;
	  	editReportForm.submit();
	  }
	  
	  //封存
	  function freezeReport()
	  {
	 		var hashvo=new ParameterSet();
		    hashvo.setValue("tabid",tabid); 
			hashvo.setValue("unitcode",unitcode); 
			var In_paramters="type=1";
			var request=new Request({method:'post',asynchronous:false,parameters:In_paramters,onSuccess:releaseInfo,functionId:'03030000012'},hashvo);
	   	
	  }
	  
	  //打回
	  function reportGoBack()
	  {
	  		var info='';
			var thecodeurl="/report/report_collect/reportOrgCollecttree.do?b_writeDesc=description`unitcode="+unitcode+"`selfUnitcode="+selfUnitcode+"`flag=1";			
			var iframe_url="/general/query/common/iframe_query.jsp?src="+thecodeurl;
			var win= window.showModalDialog(iframe_url,info, 
		        "dialogWidth:430px; dialogHeight:360px;resizable:no;center:yes;scroll:yes;status:no");
	  		if(win)
	  		{
	  			var str=document.location.href;
	    		document.location=str;
	    	}
	  }
	 function batchreportGoBack(){
	 	var thecodeurl="/report/report_collect/reportOrgCollecttree.do?b_selr=init&unitcode="+unitcode+"&tabid="+tabid+"&selfUnitcode="+selfUnitcode+"&tsort=''";
	 	var config = {
				width:600,
				height:420,
				title:'批量驳回',
				theurl:thecodeurl,
				id:'reportApprove'
			}
		openWin(config);
	 	Ext.getCmp("reportApprove")
		.addListener('close',
		function(){
	        if(win1=='b'){
	   		 	 var str=document.location.href;
	   		 	 document.location=str;
	        }
		});
	 }
	  function releaseInfo(outparamters)
	  {		
	  		//window.location.reload(); 
	  	    var str=document.location.href;
	    	document.location=str;
	  		//	desc.innerHTML="封存";

	  }
	  
	   //直属汇总
	  function subUnitCollect()
	  {
	  	var hashvo=new ParameterSet();
		hashvo.setValue("tabid",tabid); 
	  	hashvo.setValue("unitcode",unitcode); 
	  	var In_paramters="type=1";
	  	var request=new Request({method:'post',asynchronous:false,parameters:In_paramters,onSuccess:showOrGo,functionId:'03030000210'},hashvo);
	  }
	  var returnValue = "";
	  function showOrGo(outparamters){
	  	var unitcodes=outparamters.getValue("subunitcodes");
	  	var tabid1=outparamters.getValue("tabid");
	  	if(unitcodes.length==0){
		  	editReportForm.action="/report/report_collect/reportOrgCollecttree.do?b_subUnitCollect=collect";
		  	editReportForm.submit();
	  	}else{
	  		var thecodeurl="/report/report_collect/reportOrgCollecttree.do?b_show=init&unitcodes="+unitcodes+"&tabid="+tabid1;
		    var config = {
					width:390,
					height:330,
					title:'直属汇总',
					theurl:thecodeurl,
					id:'subcollect'
				}
			openWin(config);
			Ext.getCmp("subcollect").addListener('close',
				function(){
					if(returnValue=='go'){
						editReportForm.action="/report/report_collect/reportOrgCollecttree.do?b_subUnitCollect=collect";
						editReportForm.submit();
					}
				});
	  		}
	  }
	  
	  var nums='-1,';
	  //选中行或列
	  function selectRowOrColumn(info)
	  {
	  		//   a1   a:列  b:行
	  		//clearSelected(0);
	  		if(info.substring(0,1)=='a')
	  		{
	  			if(nums.indexOf(',b')!=-1)
	  			{
	  				alert("不能同时选择横纵列!");
	  				return;
	  			}
	  			for(var i=0;i<rows;i++)
	  			{
	  				var a_object=eval("aa"+i+"_"+info.substring(1));
	  				a_object.style.background="#2D86E8";
	  			    //	  a_object.style.border='thin solid blue'
	  			}	
	  		
	  		}
	  		else
	  		{	
	  			if(nums.indexOf(',a')!=-1)
	  			{
	  				alert("不能同时选择横纵列!");
	  				return;
	  			}
	  			for(var i=0;i<cols;i++)
	  			{
	  				var a_object=eval("aa"+info.substring(1)+"_"+i);
	  				a_object.style.background="#2D86E8";
	  				// a_object.style.border='thin solid blue'
	  			}	
	  		}
	  		if(nums.indexOf(','+info+',')==-1)
		  		nums+=info+',';
		  	
	  }
	  
	  
	  //清除前一步所选的行或列的颜色
	  function clearSelected(info)
	  {
	  		if(nums.indexOf(','+info+',')!=-1)
	  		{
			  	if(info.substring(0,1)=='a')
			  	{
		  			for(var i=0;i<rows;i++)
		  			{
		  				var a_object=eval("aa"+i+"_"+info.substring(1));
		  				a_object.style.background="#ffffff";
		  			//	a_object.style.border='1px solid #000000';
		  			}	
			  	}
			  	else
			  	{
	  				for(var i=0;i<cols;i++)
		  			{
		  				var a_object=eval("aa"+info.substring(1)+"_"+i);
		  				a_object.style.background="#ffffff";
		  				//a_object.style.border='1px solid #000000';
		  			}	
			  	}
		  	}
		  	if(nums!='-1,')
	  		{
	  			nums=nums.replace(info+",",""); 	
	  		}
	  		//if(n=1)				//????
	  		//	nums=-1;
	  		
	  }
	  

	  
	   //生成综合表
	  function productIntegrateTable()
	  {
	  	if(nums=='-1,')
	  	{
	  		alert(REPORT_INFO38+"!");
	  		return;
	  	}
	  	editReportForm.action="/report/report_collect/IntegrateTable.do?b_selectTableTerm=search&cols="+cols+"&unitcode="+selfUnitcode+"&tabid="+tabid+"&nums="+nums;
     	editReportForm.submit();
	  }
	  
			//自动计算
			function autoAccount(iteName,npercent)
			{
				var a_object=eval("document.editReportForm."+iteName);
				if(!checkNUM(a_object,15,npercent))
					return;
				else
				{
					if(a_object.value!=''&&a_object.value.length>0)
						pageResult[iteName.substring(1,iteName.indexOf("_"))][iteName.substring(iteName.indexOf("_")+1)]=a_object.value;
					else
						pageResult[iteName.substring(1,iteName.indexOf("_"))][iteName.substring(iteName.indexOf("_")+1)]=0;
				}
					
				
				var a_result=new Array();
				for(var a=0;a<rows;a++)
				{
					var row_str="";
					for(var b=0;b<cols;b++)
					{
						row_str+="/"+pageResult[a][b];
					}
					a_result[a]=row_str.substring(1);
				}	
				
				var hashvo=new ParameterSet();
				hashvo.setValue("tabid",tabid);
				hashvo.setValue("rows",rows);
				hashvo.setValue("cols",cols);
			    hashvo.setValue("pageResult",a_result); 
				hashvo.setValue("gridName",iteName);
				var In_paramters="flag=1"; 		
				var request=new Request({method:'post',asynchronous:false,parameters:In_paramters,onSuccess:returnInfo_autoCalculate,functionId:'03020000010'},hashvo);

			}
			
			//处理自动计算返回值
			function returnInfo_autoCalculate(outparamters)
			{
				 var info=outparamters.getValue("info");
				// alert(info);
				 if(info.length!=0)
				 {						
					 if(info.indexOf("/")==-1)
					 {
					 	if(info.indexOf("#")==-1)
					 	{
						 	var temp=info.split(",");
						 	var a_object=eval("document.editReportForm.a"+temp[0]+"_"+temp[1]);
						 	a_object.value=temp[2];
						 	pageResult[temp[0]][temp[1]]=temp[2];
						 }
						 else
						 {
						 	alert(info.substring(1));
						 }
						 
						 
					 }
					 else
					 {
					 	var gridValue=info.split("/");
					 	for(var a=0;a<gridValue.length;a++)
					 	{
					 		if(gridValue[a].indexOf("#")==-1)
					 		{
							 	var temp=gridValue[a].split(",");
							 	var a_object=eval("document.editReportForm.a"+temp[0]+"_"+temp[1]);
							 	a_object.value=temp[2];
							 	pageResult[temp[0]][temp[1]]=temp[2];
							 }
							  else
							 {
							 	alert(gridValue[a].substring(1));
							 }
							 
					 	}
					 }		
				}
			}


			function checkNUM(NUM,len1,len2)
			{
				

			    var i,j,strTemp;
			    var str1,str2;
			    var n=0;
			    strTemp="-0123456789.";
			    if ( NUM.value.length== 0)
			    {
			        return true;
			    }   
			    
			    var myReg =/^(-?\d+)(\.\d+)?$/
				if(myReg.test(NUM.value)) 
					return true;
				else
 				 	return false;
			 
			    if(NUM.value.indexOf(".")!=-1)
			    {
			     	str1 = NUM.value.substr(0,NUM.value.indexOf("."));
			     	str2 = NUM.value.substr(NUM.value.indexOf(".")+1,NUM.value.length);
			     	
			     	if(str1.length>len1)
			     	  {			     
			     	  	return false;
			     	  }
			        if(str2.length>len2)
			        {			     
			     	  	return false;
			        }
			    }
			    else
			    {
			    	str1 = NUM.value;
			    	if(str1.length>len1)
			     	  {		   
			     	  	return false;
			     	  }
			    }   
			    //说明是数字
			    return true;
			}


		
function checkNUM3(NUM,len1,len2)
{
    var i,j,strTemp;
    var str1,str2;
    var n=0;
   
  
    strTemp="-0123456789.";
    if ( NUM.value.length== 0)
    {
        return true;
    }    
   
    var myReg =/^(-?\d+)(\.\d+)?$/
    if(!myReg.test(NUM.value)) 
	{
 					alert(REPORT_INFO39+"！")
 				 	return false;
   
   	}

    if(NUM.value.indexOf(".")!=-1)
    {
     	str1 = NUM.value.substr(0,NUM.value.indexOf("."));
     	str2 = NUM.value.substr(NUM.value.indexOf(".")+1,NUM.value.length);
     	
     	if(str1.length>len1)
     	  {
     	  	alert(REPORT_INFO17+len1);
     	  	return false;
     	  }
        if(str2.length>len2)
        {
        	alert(REPORT_INFO18+len2);
     	  	return false;
        }
    }
    else
    {
    	str1 = NUM.value;
    	if(str1.length>len1)
     	  {
     	  	alert(REPORT_INFO17+len1);
     	  	return false;
     	  }
    }   
    //说明是数字
    return true;
}
			
function upload_picture(tabid,gridno,pathname){
   
	if(pathname==null)
	{
		alert(NOT_HAVE_RECORD);
		return;
	}    
   pathname=getEncodeStr(pathname);
    var thecodeurl ="/report/edit_report/pictureReport.do?b_query=link`tabid="+tabid+"`gridno="+gridno+"`pathname="+pathname+"`tablename=tpage"; 
	var iframe_url="/general/query/common/iframe_query.jsp?src="+thecodeurl;     
    var return_vo= window.showModalDialog(iframe_url, "", 
              "dialogWidth:500px; dialogHeight:320px;resizable:no;center:yes;scroll:yes;status:no");      
	//dataset.flushData();
	if(return_vo)
	{

		var img=document.getElementById(""+tabid+"_"+gridno);
		if(img==null)
			return;
			img.src=return_vo;
		pathname = return_vo.substring(return_vo.indexOf("filename=")+9);
		img.onclick =  function(){ //wangcq 2015-1-5 点击事件重新传值
            upload_picture(tabid,gridno,pathname); 
        };
			
	}
}			

		function appeal_4(){

		  	if(!isMustFill())
		  			return;
			  	var hashvo=new ParameterSet();
			    hashvo.setValue("tabid",tabid); 
			   	hashvo.setValue("rows",rows);
			   	hashvo.setValue("cols",cols);
			   	hashvo.setValue("operateObject","1")
			    var new_result=""; //new Array();
			   
			    for(var i=0;i<rows;i++)
			    {
			    	var a_values='';
			    	for(var j=0;j<cols;j++)
			    	{
			    		var a_object=eval("document.editReportForm.a"+i+"_"+j);
			    		if(a_object.value==''||a_object.value==' ')
			    		{
			    			a_values+='/0';
			    		}
			    		else
			    			a_values+='/'+a_object.value;
			    	}
			       // new_result[i]=a_values.substring(1);
			        new_result+="`"+a_values.substring(1);
			    }
			    
			    hashvo.setValue("results",new_result);
			    if(get_ParamValues()==false&&param_str!=''&&param_str!=' ')
			    	return;
			    hashvo.setValue("param",getEncodeStr(get_ParamValues().substring(1)));
			   	var In_paramters="flag=1"; 	
			 
				var request=new Request({method:'post',asynchronous:false,parameters:In_paramters,onSuccess:appeal_init,functionId:'03020000002'},hashvo);		
		}
		function appeal_init(outparamters){
			var info=outparamters.getValue("info");
	  	
	  		if(info==0){
	  			alert(SAVEVALUEERROR+"！");		
	  			return;
	  		}
			var user = $URL.encode(getEncodeStr(username));
	   		editReportForm.action="/report/edit_report/editReport.do?b_initAppeal=appeal&tabid="+tabid+"&status="+status+"&isApproveflag="+isApproveflag+"&username="+user;
	   		editReportForm.submit();
		}
	  function appeal_3()
	  {	  
	  		var info='';
			var thecodeurl="/report/report_isApprove/reportIsApprove.do?b_isApprove=link`username="+$URL.encode(username);
			var iframe_url="/general/query/common/iframe_query.jsp?src="+thecodeurl; 
			var win= window.showModalDialog(iframe_url,info, 
		        "dialogWidth:430px; dialogHeight:240px;resizable:no;center:yes;scroll:yes;status:no");
		    if(win!=null){
		    	if(confirm("确定要报批吗？")){
		    			var hashvo=new ParameterSet();
						hashvo.setValue("mainbody_id",win);
						hashvo.setValue("tabid",tabid);
						hashvo.setValue("unitcode1",unitcode1);
						var request=new Request({method:'post',asynchronous:false,onSuccess:appeal_5,functionId:'03020000098'},hashvo);	
		    	}
		    }
	  }
	  function appeal_5(){
	  		 var href="/report/report_collect/reportOrgCollecttree.do?b_query=link&a_code="+unitcode1+"&tabid="+tabid;
	  		 window.location=href;
	  }
	  function returnApprove(flag){
			var thecodeurl="/report/report_isApprove/reportIsApprove.do?b_return=link&tabid="+tabid+"&unitcode1="+unitcode1+"&flag="+flag;	
			var config = {
					width:600,
					height:620,
					title:'',
					theurl:thecodeurl,
					id:'reportApprove'
				}
			openWin(config);
			Ext.getCmp("reportApprove")
			.addListener('close',
			function(){
		        if(win1){
	        		var hashvo=new ParameterSet();
					hashvo.setValue("content1",win1);
					hashvo.setValue("flag",flag);
					hashvo.setValue("tabid",tabid);
					hashvo.setValue("unitcode1",unitcode1);					
					var request=new Request({method:'post',asynchronous:false,onSuccess:appeal_5,functionId:'03020000096'},hashvo);	
		        }
			});
	  }
	  function shenpi(flag){
			var thecodeurl="/report/report_isApprove/reportIsApprove.do?b_spAndbh=link&flag="+flag+"&encryptParam=" + encryptParam4;	
			var config = {
					width:600,
					height:400,
					title:'',
					theurl:thecodeurl,
					id:'reportIsApprove'
				}
			openWin(config);
	  }
	  function bohui(flag){
			var thecodeurl="/report/report_isApprove/reportIsApprove.do?b_spAndbh=link&flag="+flag+"&encryptParam=" + encryptParam4;	
			var config = {
					width:600,
					height:400,
					title:'',
					theurl:thecodeurl,
					id:'reportIsApprove'
				}
			openWin(config);
	  }
	  function goback1(){
	  	  	 var href="/templates/index/portal.do?b_query=link";
	  		 window.location=href;
	  }
	  function approve(){//批准
	  		var hashvo=new ParameterSet();
	  		hashvo.setValue("tabids",tabid);
			hashvo.setValue("operateObject","2");
			hashvo.setValue("unitcode",unitcode1);
			hashvo.setValue("appealUnitcode",unitcode1);
	
			var In_paramters="flag=1"; 
			var request=new Request({method:'post',asynchronous:true,parameters:In_paramters,onSuccess:approve2,functionId:'03020000009'},hashvo);
	  }
	  var win1;
	  function approve2(outparamters){//批准
	  		var returninfo=outparamters.getValue("returnInfo");
			var tabid_str=outparamters.getValue("tabid_str");
			var operateObject=outparamters.getValue("operateObject");
			if(!operateObject)
			     operateObject="1";
			if(returninfo!="success")
			{
			if(returninfo=='failed1')
			{
				var errorInfo=getDecodeStr(outparamters.getValue("errorInfo"));
				alert(errorInfo);
			}
			else if(returninfo=='failed2')
			{
				var errorInfo=getDecodeStr(outparamters.getValue("errorInfo"));
				alert("\r\n校验错误,不予上报!\r\n"+errorInfo);
			}
			else
				alert(REPORT_INFO31);
			return; 
			}
				if(tabid_str.length==0)
			{
				alert(REPORT_INFO11+"！");
				return;
			}

			var thecodeurl="/report/report_isApprove/reportIsApprove.do?b_return=link&tabid="+tabid+"&unitcode1="+unitcode1+"&flag=2";	
			var config = {
					width:600,
					height:620,
					title:'',
					theurl:thecodeurl,
					id:'reportApprove'
				}
			openWin(config);
			Ext.getCmp("reportApprove")
			.addListener('close',
			function(){
				if(win1){	        		
					var hashvo=new ParameterSet();
					hashvo.setValue("content1",win1);
					hashvo.setValue("tabids",tabid);
					hashvo.setValue("operateObject",operateObject);
					hashvo.setValue("appealUnitcode",unitcode1);
					hashvo.setValue("changStatus","1");
					var In_paramters="flag=1"; 
					var request=new Request({method:'post',asynchronous:false,parameters:In_paramters,onSuccess:approve3,functionId:'03020000003'},hashvo);
				}
			});
	  }
	  function approve3(outparamters){
	  		var info=outparamters.getValue("info");
			var operateObject="1";
			alert(info);
	
		  	var href="/report/report_collect/reportOrgCollecttree.do?b_query=link&a_code="+unitcode1+"&tabid="+tabid;
	  		window.location=href;
	  }
//-->