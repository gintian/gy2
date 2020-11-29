

	
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
	  	if(a_object.value!=''&&a_object.value.length>0)
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
	  	if(a_object.value!=''&&a_object.value.length>0)
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
				if(a_object!=null&&((a_object.value!=undefined&&trim(a_object.value)=="")||(a_object.length>0&&a_object[0].value=="")))
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
		  				if(a_object.length>1){
		  				alert(PARAMETERDEFINEREPEAT+","+DONOTOPERATER+"!");
		  				return false;
		  				}
		  				if(a_object.value.replace(/[^\x00-\xff]/g,"**").length>temp_arr[4]*1)
		  				{
		  					if(temp_arr[1]!=REPORT_TEXT)
		  					{	 
		  					alert(a_object.value+" "+OVERDEFINELENGTH+temp_arr[4]+"，"+DONOTOPERATER);
		  					return false;
		  					}
		  					
		  				}
		  				if(a_object.value.indexOf("\'")!=-1||a_object.value.indexOf("\"")!=-1)
		  				{
		  					alert("参数值不支持 \' 和 \" 符号!");
		  					return false;
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
	  	var obj2=outparamters.getValue("obj1");
	  	if(info==1)
	  	{
				alert(SAVESUCCESS+"！");
				//parent.mil_menu.document.location.reload();
				if(obj2=="1"){
	  			var user = $URL.encode(getEncodeStr(username1));
	  			var href="/report/edit_report/reportSettree.do?b_query=link&username="+user+"&code="+tabid+"&obj1="+obj2;
	  			window.location=href;
	  			}else{
				parent.mil_menu.updateState();
				}
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
			   	hashvo.setValue("operateObject","1")
			   	hashvo.setValue("username",username);
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
	   	hashvo.setValue("operateObject","1");
	   	hashvo.setValue("scopeid",scopeid);
	   	hashvo.setValue("username",username);
	   	hashvo.setValue("obj1",obj1);
	    var new_result=""; //new Array();
	    for(var i=0;i<rows;i++)
	    {
	    	var a_values='';
	    	for(var j=0;j<cols;j++)
	    	{
	    		var a_object=eval("document.editReportForm.a"+i+"_"+j);
	    		if(a_object==undefined||a_object.value==''||a_object.value==' ')
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
	    hashvo.setValue("scopeid",scopeid);
	    if(get_ParamValues()==false&&param_str!=''&&param_str!=' ')
			    	return;
	    hashvo.setValue("param",getEncodeStr(get_ParamValues().substring(1)));
	   	var In_paramters="flag=1"; 	
		var request=new Request({method:'post',asynchronous:false,parameters:In_paramters,onSuccess:returnInfoSave,functionId:'03020000002'},hashvo);
	   	

	  }
	   function returnInfo2(outparamters)
	   {
	    	var info=outparamters.getValue("info");  	
	    	var str=document.location.href;
	    	document.location=str;

	   }
	  
	  //清零
	  function clear()
	  {
	  	if(!confirm(ENTERCLEARDATA+"？"))
	  		return;
	  
	 	 var hashvo=new ParameterSet();
	    hashvo.setValue("tabid",tabid); 
	   	hashvo.setValue("rows",rows);
	   	hashvo.setValue("cols",cols);
	   	hashvo.setValue("operateObject","1");
	   	hashvo.setValue("scopeid",scopeid);
	   	hashvo.setValue("username",username);
	   	hashvo.setValue("obj1",obj1);
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
	   	var In_paramters="flag=1"; 		
		var request=new Request({method:'post',asynchronous:false,parameters:In_paramters,onSuccess:returnInfo2,functionId:'03020000002'},hashvo);
	   	

	  }
	  
	
		//归档
	  	var pinfos=new Array();
	  	var year_value=new Array();
		function pigeonhole()
		{
			year_value=new Array();
			if(use_scope_cond=="1"&&scopeid=="0"){
			alert("请你先进行自动取数的操作！");
			return;
			}
			if(!confirm(CONFIRMCOLLECTDATA+"?"))
				return;
			pinfos[0]=selfUnitcode;
			pinfos[1]=tabid;
			pinfos[2]=selfType;
			pinfos[3]="1";  //1：编辑没上报表 2：编辑上报后的表
			pinfos[4]='${editReportForm.auto_archive}';
			var thecodeurl="/report/edit_report/editReport.do?b_pigeonhole=query&tabid="+tabid; 
			var config = {
					width:450,
					height:350,
					title:'报表数据归档',
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
				hashvo.setValue("operateObject",'1');	
				hashvo.setValue("scopeid",scopeid);	
				hashvo.setValue("username",username1);	
				var In_paramters="flag=1"; 		
				
				var request=new Request({method:'post',asynchronous:false,parameters:In_paramters,onSuccess:pigeonhole2,functionId:'03020000014'},hashvo);
			}
		  
		}
		
		function pigeonhole2(outparamters)
		{
				var isData=outparamters.getValue("isData");
				
				if(selfType!=outparamters.getValue("reportType")){
						if(!confirm(CONFIRMPIGHOLE)){
							return;
						}
				}else{
						if(isData=='yes')
					{
						if(!confirm(REPORT_INFO26+"?"))
							return;
					}
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
			    hashvo.setValue("operateObject",'1');
			    hashvo.setValue("scopeid",scopeid);
			   	var In_paramters="flag=1"; 		
				var request=new Request({method:'post',asynchronous:false,parameters:In_paramters,onSuccess:return_pigeonhole,functionId:'03020000004'},hashvo);
			   	
		}
		
		
		//总计算
		function totalCount(outparamters)
		{
			 editReportForm.action="/report/edit_report/editReport.do?b_initFormula=initFormula&flag=c&status="+status+"&username="+$URL.encode(username);
			 editReportForm.submit();
		}
		
		//表内计算 
		function reportInnerCount(outparamters)
		{		
			   editReportForm.action="/report/edit_report/editReport.do?b_initFormula=initFormula&flag=a&status="+status+"&username="+$URL.encode(username);
			   editReportForm.submit();
		}
		
		//表间计算
		function reportCount(outparamters)
		{		
			   editReportForm.action="/report/edit_report/editReport.do?b_initFormula=initFormula&flag=b&status="+status+"&username="+$URL.encode(username);
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
			   	hashvo.setValue("operateObject","1");
			   	hashvo.setValue("scopeid",scopeid);
			   	hashvo.setValue("username",username);
			   	hashvo.setValue("obj1",obj1);
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
		
		var infos=new Array();
		//处理表内（即时）校验结果
		function returnInfo_promptlyValidate(outparamters)
	  	{
			var info=outparamters.getValue("info");				
			infos[0]=info;
			infos[1]= executeGridObjectArray_2(1);			
			var config = {
				width:370,
				height:260,
				title:'表内校验',
				theurl:"/report/edit_report/promptlyValidate.jsp",
				id:'promptlyValidatWin'
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
		    	modal:true,
		    	renderTo:Ext.getBody(),
		    	html:"<iframe style='background-color:#ffffff' frameborder='0' SCROLLING=NO height='100%' width='100%' src='"+config.theurl+"'></iframe>"
	 	    }).show();	
		}
		
		
		//处理表间校验结果
		var reportValInfo =new Array();
		function returnInfo_reportValidate(outparamters)
		{
			reportValInfo=outparamters.getValue("info");								
			var config = {
					width:370,
					height:260,
					title:'表间校验',
					theurl:"/report/edit_report/reportValidate.jsp",
					id:'reportValidate'
				}
			openWin(config);
		}
		
		
		function returnInfo_collectValidate(outparamters)
		{
			var info=getDecodeStr(outparamters.getValue("info"));								
			var thecodeurl="/report/edit_report/reportCollectValidate.jsp";
            var iframe_url="/general/query/common/iframe_query.jsp?src="+thecodeurl; 
			var win= window.showModalDialog(iframe_url,info, 
		        "dialogWidth:480px; dialogHeight:300px;resizable:no;center:yes;scroll:yes;status:no");	
		
		}
		
		//汇总校验
		function reportCollectValidate()
		{
				var hashvo=new ParameterSet();
			    hashvo.setValue("tabid",tabid); 
			   	hashvo.setValue("rows",rows);
			   	hashvo.setValue("cols",cols);
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
			    hashvo.setValue("operateObject","1");		 
			   	var In_paramters="flag=1"; 		
				var request=new Request({method:'post',asynchronous:false,parameters:In_paramters,onSuccess:returnInfo_collectValidate,functionId:'03020000016'},hashvo);
		
		}
		
		
		//表内即时校验
		function promptlyValidate()
		{
				
			   var hashvo=new ParameterSet();
			    hashvo.setValue("tabid",tabid); 
			   	hashvo.setValue("rows",rows);
			   	hashvo.setValue("cols",cols);
			   	hashvo.setValue("username",username);
			   	hashvo.setValue("obj1",obj1);
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
			    hashvo.setValue("operateObject","1");				
			   	var In_paramters="flag=1"; 		
				var request=new Request({method:'post',asynchronous:false,parameters:In_paramters,onSuccess:returnInfo_promptlyValidate,functionId:'03020000005'},hashvo);
			   	  
		}
		
		//表间校验
		function reportValidate()
		{
			 var hashvo=new ParameterSet();
			    hashvo.setValue("tabid",tabid); 
			   	hashvo.setValue("rows",rows);
			   	hashvo.setValue("cols",cols);
			   	hashvo.setValue("username",username);
			   	hashvo.setValue("obj1",obj1);
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
			    hashvo.setValue("operateObject","1");		 
			   	var In_paramters="flag=1"; 		
				var request=new Request({method:'post',asynchronous:false,parameters:In_paramters,onSuccess:returnInfo_reportValidate,functionId:'03020000006'},hashvo);
		}	  
	  
	  
	  //参数设置
	  function parameterSet2()
	  {
	  	editReportForm.action="/report/edit_report/parameter.do?br_send=query&encryptParam=" + encryptParam1;
	  	
	  	editReportForm.submit();
	  }
	  
	  
	  //查看打回说明
	  function description()
	  {	  
	  		var info='';
			var thecodeurl="/report/report_collect/reportOrgCollecttree.do?b_lookDesc=description`bopt=1";	
			var iframe_url="/general/query/common/iframe_query.jsp?src="+thecodeurl; 
			var win= window.showModalDialog(iframe_url,info, 
		        "dialogWidth:430px; dialogHeight:360px;resizable:no;center:yes;scroll:yes;status:no");

	  }

	  //设置反查标记
	function setReverseID(name)
	{
	showCellSelection(false);
//	 startCell=null;  
//	 endCell=null;
		var a_td;
		if(reverseFlag!=''&&reverseFlag!=' ')
		{
		    a_td=eval("a"+reverseFlag);
			a_td.style.border='1px solid #000000';
			
			var startRow=parseInt(reverseFlag.substring(1,reverseFlag.indexOf("_")));
			a_td.style.borderRightWidth=CellArray[startRow];
		}
		reverseFlag=name;
		a_td=eval("a"+name);
		a_td.style.border='2px solid green'
	}
	
	
		
	//反查
	function revertData()
	{
		
		if(reverseFlag==''||reverseFlag==' ')
		{
			alert(REPORT_INFO27+"！");
			return;
		}
		var gridVo=eval("document.editReportForm."+reverseFlag);	
		if(gridVo.value==''||gridVo.value==' ')
			return;		
		var info='';
	//	var strurl="/report/edit_report/editReport.do?b_reverseFind=find`pageNum=1`gridName=" +reverseFlag+ "`tabid="+tabid+"`count="+gridVo.value;		 
	//	var iframe_url="/general/query/common/iframe_query.jsp?src="+strurl;
		newwindow=window.open('/report/edit_report/editReport.do?b_reverseFind=find&gridName=' +reverseFlag+ "&count="+gridVo.value+"&encryptParam="+encryptParam,'glWin',
		'toolbar=no,location=no,directories=no,status=no,menubar=no,scrollbars=yes,top=170,left=220,width=530,height=600,resizable=no');
	//	var year_value= window.showModelessDialog(iframe_url, info, "dialogWidth:400px; dialogHeight:300px;resizable:yes;center:yes;scroll:yes;status:yes");			
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
				var row_str="";
				for(var a=0;a<rows;a++)
				{
					row_str="";
					for(var b=0;b<cols;b++)
					{
						row_str+="/"+pageResult[a][b];
						
					}
					
				   a_result[a]=row_str.substring(1);
				}	
				
				var ahashvo=new ParameterSet()
				ahashvo.setValue("tabid",tabid);
				ahashvo.setValue("rows",rows);
				ahashvo.setValue("cols",cols);			    
			    ahashvo.setValue("pageResult",a_result); 
				ahashvo.setValue("gridName",iteName);
				var In_paramters="flag=1"; 	
				var request=new Request({method:'post',asynchronous:false,parameters:In_paramters,onSuccess:returnInfo_autoCalculate,functionId:'03020000010'},ahashvo);

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

						 	pageResult[temp[0]][temp[1]]=temp[2]*1;

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

							 	pageResult[temp[0]][temp[1]].value=temp[2];

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
				if(myReg.test(NUM.value)){ 
					//return true;
					}
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
 					alert(FORMATERROR+"！")
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
		img.onclick =  function(){ //wangcq 2014-12-27 点击事件重新传值
            upload_picture(tabid,gridno,pathname); 
        }; 
	}
}			
function setStaticStatement(){
	if(getBrowseVersion()==0){
		alert("请使用IE浏览器使用此功能！");
		return;
	}
		  var thecodeurl ="/report/edit_report/editReport/staticStatement.do?b_setStatic=init`scopeid=0"; 
	var iframe_url="/general/query/common/iframe_query.jsp?src="+thecodeurl; 
		var return_vo= window.showModalDialog(iframe_url, "", 
              "dialogWidth:"+screen.width/1.5+"px; dialogHeight:"+screen.height/1.5+"px;resizable:yes;center:yes;scroll:yes;status:no");
		while(return_vo!=null&&return_vo!=""){
		thecodeurl="/report/edit_report/editReport/staticStatement.do?b_setStatic=init`scopeid="+return_vo; 
		iframe_url="/general/query/common/iframe_query.jsp?src="+thecodeurl; 
		var return_vo= window.showModalDialog(iframe_url, "", 
              "dialogWidth:"+screen.width/1.5+"px; dialogHeight:"+screen.height/1.5+"px;resizable:yes;center:yes;scroll:yes;status:no");
		}
		if(!return_vo){
	  	var href = window.location.href;
  		window.location.href=href;
		}
		
}
function autoGetNum(){
	var hashvo=new ParameterSet();
				var In_paramters="flag=1"; 	
				var request=new Request({method:'post',asynchronous:false,parameters:In_paramters,onSuccess:autoGetNumresult,functionId:'03020000073'},hashvo);
}
function autoGetNumresult(outparamters){
var info=outparamters.getValue("info");  	
if(info=="fail"){
alert("权限范围下的统计口径不存在!");
return;
}
var thecodeurl ="/report/edit_report/editReport/staticStatement.do?b_select=init`tabid="+tabid+"`scopeid=0"; 
	var iframe_url="/general/query/common/iframe_query.jsp?src="+thecodeurl; 
		var return_vo= window.showModalDialog(iframe_url, "", 
              "dialogWidth:"+screen.width/1.5+"px; dialogHeight:"+screen.height/2+"px;resizable:yes;center:yes;scroll:yes;status:no");
	
	while(return_vo!=null&&!return_vo==""){
	 thecodeurl ="/report/edit_report/editReport/staticStatement.do?b_select=init`tabid="+tabid+"`scopeid="+return_vo; 
	 iframe_url="/general/query/common/iframe_query.jsp?src="+thecodeurl; 
		var return_vo= window.showModalDialog(iframe_url, "", 
              "dialogWidth:"+screen.width/1.5+"px; dialogHeight:"+screen.height/2+"px;resizable:yes;center:yes;scroll:yes;status:no");
		}
		if(return_vo!=null&&return_vo==""){
		window.location.reload();
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
			   	hashvo.setValue("username",getEncodeStr(username1));
	   			hashvo.setValue("obj1",obj1);
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
			 	if(obj1=="1"){
			 		var request=new Request({method:'post',asynchronous:false,parameters:In_paramters,onSuccess:app_1,functionId:'03020000002'},hashvo);	
			 	}else{
			 		var request=new Request({method:'post',asynchronous:false,parameters:In_paramters,onSuccess:app_11,functionId:'03020000002'},hashvo);	
			 	}
					
		}
		function app_11(){//报批校验下
	  		var hashvo=new ParameterSet();
	  		hashvo.setValue("tabids",tabid);
			hashvo.setValue("operateObject","1");
			hashvo.setValue("unitcode",unitcode1);
			hashvo.setValue("appealUnitcode",unitcode1);
			hashvo.setValue("username",getEncodeStr(username1));
			var In_paramters="flag=1"; 
			var request=new Request({method:'post',asynchronous:true,parameters:In_paramters,onSuccess:appeal_init,functionId:'03020000009'},hashvo);
	  }
	  function appeal_init(outparamters)
	  {	  
	  	  	var returninfo=outparamters.getValue("returnInfo");
			var tabid_str=outparamters.getValue("tabid_str");
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
				alert("\r\n校验错误,不予报批!\r\n"+errorInfo);
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

	   		editReportForm.action="/report/edit_report/editReport.do?b_initAppeal=appeal&tabid="+tabid+"&status="+status+"&isApproveflag="+isApproveflag+"&username="+$URL.encode(username);
	   		editReportForm.submit();
		}
		function app_1(){//报批校验下
	  		var hashvo=new ParameterSet();
	  		hashvo.setValue("tabids",tabid);
			hashvo.setValue("operateObject","1");
			hashvo.setValue("unitcode",unitcode1);
			hashvo.setValue("appealUnitcode",unitcode1);
			hashvo.setValue("username",getEncodeStr(username1));
			var In_paramters="flag=1"; 
			var request=new Request({method:'post',asynchronous:true,parameters:In_paramters,onSuccess:appeal_3,functionId:'03020000009'},hashvo);
	  }
		var returnValue;
	  function appeal_3(outparamters)
	  {	  
	  	  	var returninfo=outparamters.getValue("returnInfo");
			var tabid_str=outparamters.getValue("tabid_str");
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
				alert("\r\n校验错误,不予报批!\r\n"+errorInfo);
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
			
			
	  		var info='';
			var thecodeurl="/report/report_isApprove/reportIsApprove.do?b_isApprove=link&username="+$URL.encode(username);
			
			var config = {
					width:420,
					height:300,
					title:'报批',
					theurl:thecodeurl,
					id:'appealWin'
				}
				openWin(config);
			Ext.getCmp("appealWin").addListener('close',function(){
				if(returnValue){
					if(confirm("确定要报批吗？")){
		    			var hashvo=new ParameterSet();
						hashvo.setValue("mainbody_id",returnValue);
						hashvo.setValue("tabid",tabid);
						hashvo.setValue("unitcode1",unitcode1);
						var request=new Request({method:'post',asynchronous:false,onSuccess:appeal_5,functionId:'03020000098'},hashvo);	
					}
				}
			});
	  }
	  function appeal_5(){
	  			if(obj1=="1"){
	  				var user = $URL.encode(getEncodeStr(username1));
	  				var href="/report/edit_report/reportSettree.do?b_query=link&username="+user+"&code="+tabid+"&obj1="+obj1;
	  				window.location=href;
	  			}else if(obj1=="2"){
	  				var href = ""+parent.mil_menu.document.location;
					if(href.indexOf(".jsp?")<0){
					href = href+"?selectuid="+tabid;
					}else{
					href = href.substring(0,href.indexOf(".jsp?")+5)+"selectuid="+tabid;
					}
					parent.mil_menu.document.location=href;	
	  			}	 
	  }
	  function returnApprove(flag){
	  		var info='';
			var thecodeurl="/report/report_isApprove/reportIsApprove.do?b_return=link&tabid="+tabid+"&unitcode1="+unitcode1+"&flag="+flag;	
			var config = {
					width:600,
					height:620,
					title:'驳回',
					theurl:thecodeurl,
					id:'reportApprove'
				}
			openWin(config);
			Ext.getCmp("reportApprove")
			.addListener('close',
			function(){
				if(win1!=null){
	        		var hashvo=new ParameterSet();
					hashvo.setValue("content1",win1);
					hashvo.setValue("flag",flag);
					hashvo.setValue("tabid",tabid);
					hashvo.setValue("unitcode1",unitcode1);					
					var request=new Request({method:'post',asynchronous:true,onSuccess:appeal_5,functionId:'03020000096'},hashvo);	
				}
			});
	  }
	  function shenpi(flag){
	  		var info='';
			var thecodeurl="/report/report_isApprove/reportIsApprove.do?b_spAndbh=link&flag="+flag+"&encryptParam="+encryptParam4;	
			var config = {
					width:600,
					height:360,
					title:'审批意见',
					theurl:thecodeurl,
					id:'reportIsApprove'
				}
			openWin(config);
	  }
	  function bohui(flag){
	  	  	var info='';
			var thecodeurl="/report/report_isApprove/reportIsApprove.do?b_spAndbh=link&flag="+flag+"&encryptParam="+encryptParam4;	
			var config = {
					width:600,
					height:364,
					title:'驳回原因',
					theurl:thecodeurl,
					id:'reportIsApprove'
				}
			openWin(config);
	  }
	  function goback1(backflag){
		  var href="/templates/index/hcm_portal.do?b_query=link";
		  if(backflag=='1')
			  href="/general/template/matterList.do?b_query=link";
		  backflag = "";
	      window.location=href;
	  }
	  function approve(){//批准
	  		var hashvo=new ParameterSet();
	  		hashvo.setValue("tabids",tabid);
			hashvo.setValue("operateObject","1");
			hashvo.setValue("unitcode",unitcode1);
			hashvo.setValue("appealUnitcode",unitcode1);
			hashvo.setValue("username",getEncodeStr(username1));
			var In_paramters="flag=1"; 
			var request=new Request({method:'post',asynchronous:true,parameters:In_paramters,onSuccess:approve2,functionId:'03020000009'},hashvo);
	  }
	  var win1;
	  function approve2(outparamters){//批准
	  		var returninfo=outparamters.getValue("returnInfo");
			var tabid_str=outparamters.getValue("tabid_str");
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
					title:'批准',
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
					hashvo.setValue("operateObject","1");
					hashvo.setValue("appealUnitcode",unitcode1);
					hashvo.setValue("username",getEncodeStr(username1));
					hashvo.setValue("changStatus","1");
					var In_paramters="flag=1"; 
					var request=new Request({method:'post',asynchronous:true,parameters:In_paramters,onSuccess:approve3,functionId:'03020000003'},hashvo);	
				}
			});
	  }
	  function approve3(outparamters){
	  		var info=outparamters.getValue("info");
			var operateObject="1";
			alert(info);
	  			if(obj1=="1"){
	  				var user = $URL.encode(getEncodeStr(username1));
	  				var href="/report/edit_report/reportSettree.do?b_query=link&username="+user+"&code="+tabid+"&obj1="+obj1;
	  				window.location=href;
	  			}else if(obj1=="2"){
	  				var href = ""+parent.mil_menu.document.location;
					if(href.indexOf(".jsp?")<0){
					href = href+"?selectuid="+tabid;
					}else{
					href = href.substring(0,href.indexOf(".jsp?")+5)+"selectuid="+tabid;
					}
					parent.mil_menu.document.location=href;	
	  			}
	  }
	  function returnInfoSave(outparamters)
	   {
	    	var info=outparamters.getValue("info");  	
	    	var str=document.location.href;
	    	document.location=str;

	   }
//-->