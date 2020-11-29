function getSelect(){
		var b0110s = "";
		var selects = document.getElementsByTagName("input");
		for(var i=0;i<selects.length;i++){
			if(selects[i].type=="checkbox"){
				if(selects[i].checked){
					//alert(selects[i].value)
					b0110s+=selects[i].value+",";
				}
			}
		}
		return b0110s;
	}
function appeal(userid,opt,salaryid,gz_module,tablename)
{
		

        var table=$("tablegz_sp_report");
        var dataset=table.getDataset();
	    var record=dataset.getFirstRecord();
	    //where_sql: (select null from XXX where XXXX.a00z0=salaryhistory.a00z0 and  XXXX.a00z1=salaryhistory.a00z1 XXXX.a0100=salaryhistory.a0100 and lower(XXXX.nbase)=lower(salaryhistory.nbase) and XXXXXX )
	    var basesql = "select null from "+tablename+" where "+tablename+".a00z0=salaryhistory.a00z0 and "+tablename+".a00z1=salaryhistory.a00z1 and "+tablename+".a0100=salaryhistory.a0100 and lower("+tablename+".nbase)=lower(salaryhistory.nbase) and(1=2";
	    var selectID="";
	    selectID = basesql;
	    if(opt=='appeal')
	    {
		    var num=0;	
		    var noNum=0;
			while (record) 
			{
				if (record.getValue("select"))
				{							
							if((record.getValue("sp_flag")=='02'||record.getValue("sp_flag")=='07')&&record.getValue("b0110").indexOf("sum")==-1)
							{
								num++;
							    selectID+=" or org like '"+record.getValue("b0110")+"%'";
							}
							else if(record.getValue("b0110").indexOf("sum")==-1)
								noNum++;	    
				}
				record=record.getNextRecord();
			}  	
			
			if(num==0)
			{
				alert("没有选择可报批的记录！");
				return;
			}
			if(noNum>0)
			{
				alert("您不能报批已批的记录！");
				return;
			}
		}
		var hashvo=new ParameterSet();
		
		hashvo.setValue("salaryid",salaryid);
		hashvo.setValue("gz_module",gz_module); 
	
		hashvo.setValue("bosdate",collectForm.bosdate.value);
		hashvo.setValue("count",collectForm.count.value); 
		
		hashvo.setValue("opt",opt); 
		hashvo.setValue("opt2","appeal_group");
		hashvo.setValue("userid",userid); 
		hashvo.setValue("selectID",selectID+")"); 
		
		
		
		disabledButton(true);
		var str="";
		if(isTotalControl=='1')
			str+="、总额校验";
		if(verify_ctrl=='1')
			str+="、数据审核";
		if(isTotalControl=='1'||verify_ctrl=='1')
		{
			var waitInfo=eval("wait");	
			document.getElementById("wait_desc").innerHTML="正在进行 "+str.substring(1)+",请稍候...";
			waitInfo.style.display="block";
		
		}	
		
		
		var request=new Request({method:'post',asynchronous:true,onSuccess:appealSptotalControl,functionId:'3020111007'},hashvo);
			
		/*
		var returnValue= select_user_dialog("1",2,"0");
		if(returnValue)
		{			
			if(userid==returnValue.content)
			{
				alert("不能指定本人为审批对象！");
				return;
			}
			document.accountingForm.selectGzRecords.value=selectID.substring(1);
			document.accountingForm.action="/gz/gz_accounting/gz_sptable.do?b_appeal=appeal&approveObject="+returnValue.content+"&opt="+opt;
			document.accountingForm.submit();
			
		}*/
}
function appealSpData(selectID,opt,userid)
{
		
		var returnValue=new Object();
 		if(sp_actor_str.length>0)
		{	
			var temps=getDecodeStr(sp_actor_str).split("`"); 
			if(temps.length==1)
			{ 
				returnValue.content=temps[0].split("##")[2];
			}
			else if(temps.length>1)
			{
				var arguments=new Array();
				arguments[0]=sp_actor_str;
				var strurl="/gz/gz_accounting/gz_table.do?br_selectUser=select";   //"/gz/gz_accounting/rejectCause.jsp?isMustFill=1";
	   			var iframe_url="/general/query/common/iframe_query.jsp?src="+strurl; 
	    		_returnValue=window.showModalDialog(iframe_url,arguments,"dialogWidth=400px;dialogHeight=350px;resizable=yes;scroll=no;status=no;");  
	    		if(_returnValue!=null && _returnValue!='undefined') 
	    		{
	    
		    		returnValue.content=_returnValue;
	    		
	    		}
	    		else
	    		{
		    		if(verify_ctrl=='1'||isTotalControl=='1')
					{
									var waitInfo=eval("wait");	
									waitInfo.style.display="none";
					}
				 
					disabledButton(false);
	    			return;
	    		}
			}
		}
		else  
			returnValue= select_user_dialog("1",2,"0");
		if(returnValue)
		{			
			if(userid==returnValue.content)
			{
				alert("不能指定本人为审批对象！");
				disabledButton(false);
				return;
			}
			if(opt=='appeal')
				document.collectForm.selectGzRecords.value=selectID;
			document.collectForm.action="/gz/gz_accounting/gz_collect_table.do?b_appeal=appeal&approveObject="+returnValue.content+"&opt="+opt;
			document.collectForm.submit();
			
		}
		else
		{
			var waitInfo=eval("wait");	
			waitInfo.style.display="none";
			disabledButton(false);
		}
		
}


function confirmData(selectID,opt,userid)
{
	var arguments=new Array();
	arguments[0]="同意，审批通过。";
	arguments[1]="批准意见"; 
	arguments[2]="confirmAll";
	arguments[3]=isSendMessage;
	
	var waitInfo=eval("wait");	
	waitInfo.style.display="none"; 
	
	var a00z2=collectForm.bosdate.value;
	var a00z3=collectForm.count.value;
	
	var strurl="/gz/gz_accounting/gz_sptable.do?b_confirm=confirm`operate=confirmGroup`selectID="+getEncodeStr(selectID);
	strurl+="`a00z2="+a00z2+"`a00z3="+a00z3+"`salaryid="+salaryid;
	var iframe_url="/general/query/common/iframe_query.jsp?src="+strurl; 
	var ss=window.showModalDialog(iframe_url,arguments,"dialogWidth=460px;dialogHeight=350px;resizable=yes;scroll=no;status=no;");  
	 
	if(ss)
	{
		    	disabledButton(true);
		    	
			    document.collectForm.selectGzRecords.value=selectID;
			    document.collectForm.rejectCause.value=ss[0];
			    if((opt=='confirm'||opt=='confirmAll')&&isSendMessage=='1')
			    	document.collectForm.sendMen.value=ss[1]; 
				document.collectForm.action="/gz/gz_accounting/gz_collect_table.do?b_appeal=appeal&approveObject="+userid+"&opt="+opt;
				document.collectForm.submit();
   } 
   else
   {
   			disabledButton(false);
   }
	 
}


function appealSptotalControl(outparameters)
{

		var selectID=outparameters.getValue("selectID");
		var opt=outparameters.getValue("opt");
		var userid=outparameters.getValue("userid");
		var info=getDecodeStr(outparameters.getValue("info"));
		if(info=='success')
		{
			if(verify_ctrl=='1')
			{
				verifyFormula(selectID,opt,userid)
			}
			else if(opt=='confirm')
			{
				confirmData(selectID,opt,userid)
			}
			else
				appealSpData(selectID,opt,userid);
		}
		else
		{
			
			var isOver=outparameters.getValue("isOver");
			if(isOver=='0')
			{
				alert(info);
				if(verify_ctrl=='1')
				{
				 	verifyFormula(selectID,opt,userid)
				}
				else if(opt=='confirm')
				{
					confirmData(selectID,opt,userid)
				}
				else
					appealSpData(selectID,opt,userid);
			}
			else
			{
			//	disabledButton(false);
			//	alert(info);
			
			 	var ctrlType = outparameters.getValue("ctrlType");
			    var alertInfo =getDecodeStr(outparameters.getValue("alertInfo")); 
			    if(ctrlType=='1')
			    {
			     
		    		if(isTotalControl=='1'||verify_ctrl=='1')
			    	{
			      		var waitInfo=eval("wait");	
			    		waitInfo.style.display="none";
				
			    	}	 
			     	disabledButton(false);
			     	alert(info);
			    }else
			    {
			        if(confirm(alertInfo))
			        {
			            if(verify_ctrl=='1')
			    	    {
			     	     	verifyFormula(selectID,opt,userid)
			        	}
			        	else if(opt=='confirm')
						{
							confirmData(selectID,opt,userid)
			
						}
			        	else
			    	    	appealSpData(selectID,opt,userid);
			        }else
			        {
			         
			           if(isTotalControl=='1'||verify_ctrl=='1')
			    	   {
			      		var waitInfo=eval("wait");	
			    		waitInfo.style.display="none";
				
			    	    }	 
			         	disabledButton(false);
			        }
			    }
			
			
			}
		
		}
}


function verifyFormula(selectID,opt,userid)
{
		 var a00z1=collectForm.bosdate.value;
		 var a00z0=collectForm.count.value;
		    
		 var hashvo=new ParameterSet();
		 hashvo.setValue("a_code","");
		 hashvo.setValue("condid","all");
		 hashvo.setValue("salaryid",salaryid);
		 hashvo.setValue("type","1");
		 hashvo.setValue("a00z0",a00z0);
		 hashvo.setValue("a00z1",a00z1);
	    
   		 hashvo.setValue("reportSQL","");
	     hashvo.setValue("selectID",selectID);
	     hashvo.setValue("opt",opt);
	     hashvo.setValue("opt2","sh_group");
	     hashvo.setValue("userid",userid);
	     var request=new Request({asynchronous:false,onSuccess:check_ok3,functionId:'3020070016'},hashvo);	
}



function check_ok3(outparameters)
{
 
  var msg=outparameters.getValue("msg");
  var selectID=outparameters.getValue("selectID");
  var opt=outparameters.getValue("opt");
  var userid=outparameters.getValue("userid");
   
  if(msg=='0')
  {
  	if(opt=='appeal'||opt=='appealAll')
 	   appealSpData(selectID,opt,userid);
 	if(opt=='confirm'||opt=='confirmAll') 
 	    confirmData(selectID,opt,userid);
  }
  else if(msg=='no')
  {
  	if(opt=='appeal'||opt=='appealAll')
	     appealSpData(selectID,opt,userid);
	if(opt=='confirm'||opt=='confirmAll')
		 confirmData(selectID,opt,userid);
	
  }
  else{
  	if(opt=='appeal'||opt=='appealAll')
  	{
  		if(isTotalControl=='1'||verify_ctrl=='1')
		{
					var waitInfo=eval("wait");	
					waitInfo.style.display="none";
				
		}	
  		 alert("审核不通过，不允许上报!");
    }
    if(opt=='confirm'||opt=='confirmAll')
  		 alert("审核不通过，不允许批准!");
  	if(opt=='appeal'||opt=='appealAll')
  	{	 
	  
		disabledButton(false);
	}
	if(opt=='confirm'||opt=='confirmAll')
	{
	
		disabledButton(false);
		
	
	}
	
	var waitInfo=eval("wait");	
	waitInfo.style.display="none";
	
     var filename=outparameters.getValue("fileName");
     var fieldName = getDecodeStr("");
     var win=open("/servlet/vfsservlet?fileid="+fieldName+"&fromjavafolder=true","excel");
  }
}	

function disabledButton(flag)
{

		var objs=document.getElementsByName("button2");
	    if(objs)
	    {
	  			for(var i=0;i<objs.length;i++)
					objs[i].disabled=flag;	    
	    }
	    bjs=document.getElementsByName("button1");
	    if(objs)
	    {
	  			bjs[0].disabled=flag;	    
	    }

}





/** 工资审批（批准 驳回） */
function optSalary(operate,userid,tablename)
{
		

		var basesql = "select null from "+tablename+" where "+tablename+".a00z0=salaryhistory.a00z0 and "+tablename+".a00z1=salaryhistory.a00z1 and "+tablename+".a0100=salaryhistory.a0100 and lower("+tablename+".nbase)=lower(salaryhistory.nbase) and (1=2";
		var tablename="tablegz_sp_report";
        table=$(tablename);
        dataset=table.getDataset();
	    var record=dataset.getFirstRecord();
	    var selectID="";
	    selectID = basesql;	
	    var num=0;	
	    var noNum=0;
		var desc="";
		if(operate=='confirm'||operate=='confirmAll')
			desc="批准"
		else if(operate=='reject')
			desc="驳回";
			
		if(operate!='confirmAll')
		{	
			while (record) 
			{
				if (record.getValue("select"))
				{							
							
							if((record.getValue("sp_flag")=='02'||record.getValue("sp_flag")=='07')&&record.getValue("b0110").indexOf("sum")==-1)
							{
								num++;
							     selectID+=" or org like '"+record.getValue("b0110")+"%'";
							}
							else if(record.getValue("b0110").indexOf("sum")==-1)
								noNum++;	    
				}
				record=record.getNextRecord();
			}  	
			if(num==0)
			{
				alert("没有选择可"+desc+"的记录！");
				return;
			}
			if(noNum>0)
			{
				alert("您选择了不可"+desc+"的记录！");
				return;
			
			}
		}
		
		var arguments=new Array();
		if(operate=='confirm'||operate=='confirmAll') 
		{
		/*
			arguments[0]="同意，审批通过。";
			arguments[1]="批准意见";  
			if(operate=='confirmAll')
			{	
				arguments[2]="confirmAll";
				arguments[3]=isSendMessage;
			}  
		*/
		
			var hashvo=new ParameterSet(); 
			hashvo.setValue("salaryid",salaryid);
			hashvo.setValue("gz_module",gz_module); 
		
			hashvo.setValue("bosdate",collectForm.bosdate.value);
			hashvo.setValue("count",collectForm.count.value); 
			
			hashvo.setValue("opt",operate); 
			hashvo.setValue("opt2","appeal_group");
			hashvo.setValue("userid",userid); 
			hashvo.setValue("selectID",selectID+")"); 
			 
			disabledButton(true);
			var str="";
			if(isTotalControl=='1')
				str+="、总额校验";
			if(verify_ctrl=='1')
				str+="、数据审核";
			if(isTotalControl=='1'||verify_ctrl=='1')
			{
				var waitInfo=eval("wait");	
				document.getElementById("wait_desc").innerHTML="正在进行 "+str.substring(1)+",请稍候...";
				waitInfo.style.display="block";
			
			}	 
			var request=new Request({method:'post',asynchronous:true,onSuccess:appealSptotalControl,functionId:'3020111007'},hashvo);
			
	    }
	    else
	    {
	   		arguments[0]="";
			arguments[1]="驳回原因";  
			
			
			var strurl="/gz/gz_accounting/rejectCause.jsp";
		    var iframe_url="/general/query/common/iframe_query.jsp?src="+strurl; 
			var ss=window.showModalDialog(iframe_url,arguments,"dialogWidth=460px;dialogHeight=350px;resizable=yes;scroll=no;status=no;");  
		    if(ss)
		    {
		    	disabledButton(true);
		    	
			    document.collectForm.selectGzRecords.value=selectID+")";
			    document.collectForm.rejectCause.value=ss[0];
			    if(operate=='confirmAll'&&isSendMessage=='1')
			    	document.accountingForm.sendMen.value=ss[1];
			   // document.collectForm.approveObject.value=userid;
				document.collectForm.action="/gz/gz_accounting/gz_collect_table.do?b_appeal=appeal&approveObject="+userid+"&opt="+operate;
				document.collectForm.submit();
			} 
	    }
	   
	    
	   
}