
function setState(_boolean)
{
	for(var i=0;i<document.accountingForm.elements.length;i++)
	{
		 
		if(document.accountingForm.elements[i].type=='button')
			document.accountingForm.elements[i].disabled=_boolean;
	
	}
	
	 
}

function reject(a00z2,a00z3,salaryid)
{ 
		if(!confirm("您确定执行驳回操作吗？"))
			return;
		
		setState(true);	
		var arguments=new Array();
		arguments[0]="";
		arguments[1]="驳回原因";  
		var strurl="/gz/gz_accounting/rejectCause.jsp";
		var iframe_url="/general/query/common/iframe_query.jsp?src="+strurl; 
		var ss=window.showModalDialog(iframe_url,arguments,"dialogWidth=460px;dialogHeight=350px;resizable=yes;scroll=no;status=no;");  
		if(ss)
		{
				var waitInfo=eval("wait");	
				document.getElementById("wait_desc").innerHTML="正在进行驳回操作......";
				waitInfo.style.display="block";
			
			    document.accountingForm.rejectCause.value=ss[0];
			    document.accountingForm.bosdate.value=replaceAll(a00z2,"-","\.");
			    document.accountingForm.count.value=a00z3;
			    document.accountingForm.salaryid.value=salaryid;		
				document.accountingForm.action="/gz/gz_accounting/gz_sptable.do?b_appealCollect=appeal&opt=rejectAll";
				document.accountingForm.submit();
		}
		else
			setState(false);	
}

function appeal(_a00z2,_a00z3,_salaryid,_verify_ctrl,_isSendMessage,_isControl)
{
	isTotalControl=_isControl;
	verify_ctrl=_verify_ctrl;
	isSendMessage=_isSendMessage;
	a00z2=_a00z2;
	a00z3=_a00z3;
	salaryid=_salaryid;
	
	var selectID="";
	if(!confirm('您确定要执行报批操作？'))
	{
				return;
	}
	setState(true);	
			
	var str="";
	if(isTotalControl=='1')
			str+="、总额校验";
	if(verify_ctrl=='1')
			str+="、公式审核";
	if(isTotalControl=='1'||verify_ctrl=='1')
	{
			var waitInfo=eval("wait");	
			document.getElementById("wait_desc").innerHTML="正在进行 "+str.substring(1)+"";
			waitInfo.style.display="block";
		
	}	
	var hashvo=new ParameterSet();
	hashvo.setValue("salaryid",salaryid);
	hashvo.setValue("gz_module",gz_module); 
	hashvo.setValue("bosdate",replaceAll(a00z2,"-","\."));
	hashvo.setValue("count",a00z3); 
	hashvo.setValue("filterWhl","");
	hashvo.setValue("opt","appealAll"); 
	hashvo.setValue("userid",userid); 
	hashvo.setValue("selectID",selectID);
	var request=new Request({method:'post',asynchronous:true,onSuccess:appealSptotalControl,functionId:'3020111007'},hashvo);	
}


function appealSptotalControl(outparameters)
{

		var selectID="";
		var opt=outparameters.getValue("opt");
		var userid=outparameters.getValue("userid");
		var info=getDecodeStr(outparameters.getValue("info"));
		if(info=='success'||info=='')
		{
			if(verify_ctrl=='1')
			{
				 verifyFormula(selectID,opt,userid)
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
				else
					appealSpData(selectID,opt,userid);
			}
			else
			{
				if(isTotalControl=='1'||verify_ctrl=='1')
				{
					var waitInfo=eval("wait");	
					waitInfo.style.display="none";
				
				}	
				alert(info);
				 
				setState(false);
			
			}
		
		}
}




function confirmRecord(_a00z2,_a00z3,_salaryid,_verify_ctrl,_isSendMessage,_isControl)
{
	if(!confirm('您确定要批准本月本次的报批记录？'))
	{
					return;
	}
	isTotalControl=_isControl;
	verify_ctrl=_verify_ctrl;
	isSendMessage=_isSendMessage;
	a00z2=_a00z2;
	a00z3=_a00z3;
	salaryid=_salaryid;
	
	var hashvo=new ParameterSet();
	hashvo.setValue("salaryid",salaryid);
	hashvo.setValue("gz_module",gz_module); 
	hashvo.setValue("bosdate",replaceAll(a00z2,"-","\."));
	hashvo.setValue("count",a00z3); 
	hashvo.setValue("opt","confirmAll"); 
	hashvo.setValue("userid",userid); 
	hashvo.setValue("selectID",""); 
	hashvo.setValue("filterWhl","");
	setState(true);
	var waitInfo=eval("wait");	
	document.getElementById("wait_desc").innerHTML="正在进行批准操作......";
	waitInfo.style.display="block";
	var request=new Request({method:'post',asynchronous:true,onSuccess:confirmSptotalControl,functionId:'3020111007'},hashvo);
}



function confirmSptotalControl(outparameters)
{

		var selectID="";
		var operate=outparameters.getValue("opt");
		var userid=outparameters.getValue("userid");
		var info=getDecodeStr(outparameters.getValue("info"));
		if(info=='success'||info=='')
		{
			if(verify_ctrl=='1'&&operate=='confirmAll')
			{
				  verifyFormula(selectID,operate,userid)
			}
			else
			{
				var arguments=new Array();
				if(operate=='confirmAll') 
				{
					arguments[0]="同意，审批通过。";
					arguments[1]="批准意见";  
					arguments[2]="confirmAll";
					arguments[3]=isSendMessage;
					
			    }
			    else
				{
				   		arguments[0]="";
						arguments[1]="驳回原因";  
				}
			    if(operate=='appealAll')
			   	{ 
			   		 arguments[2]="appealAll";  
			    }
			    var strurl="/gz/gz_accounting/rejectCause.jsp";
			    var iframe_url="/general/query/common/iframe_query.jsp?src="+strurl; 
				var ss=window.showModalDialog(iframe_url,arguments,"dialogWidth=460px;dialogHeight=350px;resizable=yes;scroll=no;status=no;");  
			    if(ss)
			    {
				  
				    document.accountingForm.rejectCause.value=ss[0];
				    if(operate=='confirmAll'&&isSendMessage=='1')
				    	document.accountingForm.sendMen.value=ss[1];
				    document.accountingForm.bosdate.value=replaceAll(a00z2,"-","\.");
			    	document.accountingForm.count.value=a00z3;
			   		document.accountingForm.salaryid.value=salaryid;	
				    document.accountingForm.approveObject.value=userid;
					document.accountingForm.action="/gz/gz_accounting/gz_sptable.do?b_appealCollect=appeal&opt="+operate;
					document.accountingForm.submit();
				}
				else
				{
							setState(false);
							if(operate=='confirmAll')
							{
								var waitInfo=eval("wait");				
								waitInfo.style.display="none";
							}
				}
			
			}
		}
		else
		{
			
			var isOver=outparameters.getValue("isOver");
			if(isOver=='0')
			{
				alert(info);
				if(verify_ctrl=='1'&&operate=='confirmAll')
				{
					  verifyFormula(selectID,operate,userid)
				}
				else
				{
					var arguments=new Array();
					if(operate=='confirmAll') 
					{
						arguments[0]="同意，审批通过。";
						arguments[1]="批准意见";  
						arguments[2]="confirmAll";
						arguments[3]=isSendMessage;
						 
				    }else
				    {
				   		arguments[0]="";
						arguments[1]="驳回原因";  
				    }
				    
				    if(operate=='appealAll')
				   	{
				   		 arguments[0]="";
				   		 arguments[2]="appealAll";  
				    }
				    var strurl="/gz/gz_accounting/rejectCause.jsp";
				    var iframe_url="/general/query/common/iframe_query.jsp?src="+strurl; 
					var ss=window.showModalDialog(iframe_url,arguments,"dialogWidth=460px;dialogHeight=350px;resizable=yes;scroll=no;status=no;");  
				    if(ss)
				    {
					  
					    document.accountingForm.rejectCause.value=ss[0];
					    if(operate=='confirmAll'&&isSendMessage=='1')
					    	document.accountingForm.sendMen.value=ss[1];
					    document.accountingForm.bosdate.value=replaceAll(a00z2,"-","\.");
			   			document.accountingForm.count.value=a00z3;
			    		document.accountingForm.salaryid.value=salaryid;	
					    document.accountingForm.approveObject.value=userid;
						document.accountingForm.action="/gz/gz_accounting/gz_sptable.do?b_appealCollect=appeal&opt="+operate;
						document.accountingForm.submit();
					}
					else
					{
					
						 setState(false);
						 var waitInfo=eval("wait");	
		   	   			 waitInfo.style.display="none";
					}
				
				}
			}
			else
			{
				var waitInfo=eval("wait");	
		   	    waitInfo.style.display="none";
				alert(info);
				setState(false); 
			}
		
		}
}




function verifyFormula(selectID,opt,userid)
{
		 var a00z1=replaceAll(a00z2,"-","\.");
		 var a00z0=a00z3;
		 if(a00z1==''||a00z0=='')
		 {
		     alert(GZ_SELECT_BOSDATEANDCOUNT+"！");
		     return;
		 }
		 var hashvo=new ParameterSet();
		 hashvo.setValue("a_code","");
		 hashvo.setValue("condid","");
		 hashvo.setValue("salaryid",salaryid);
		 hashvo.setValue("type","1");
		 hashvo.setValue("a00z0",a00z0);
		 hashvo.setValue("a00z1",a00z1);
   		 hashvo.setValue("reportSQL","");
	     hashvo.setValue("selectID","");
	     hashvo.setValue("opt",opt);
	     hashvo.setValue("userid",userid);
	     var request=new Request({asynchronous:false,onSuccess:check_ok3,functionId:'3020070016'},hashvo);	
}



function check_ok3(outparameters)
{
  var msg=outparameters.getValue("msg");
  var selectID="";
  var opt=outparameters.getValue("opt");
  var userid=outparameters.getValue("userid");
  if(msg=='0')
  {
  	if(opt=='appeal'||opt=='appealAll')
 	   appealSpData(selectID,opt,userid);
 	if(opt=='confirm'||opt=='confirmAll')
 	    confirmSalary(selectID,opt,userid);
  }
  else if(msg=='no')
  {
  	if(opt=='appeal'||opt=='appealAll')
	     appealSpData(selectID,opt,userid);
	if(opt=='confirm'||opt=='confirmAll')
		 confirmSalary(selectID,opt,userid);
	
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
	  
		setState(false);
	}
	if(opt=='confirm'||opt=='confirmAll')
	{
	
		setState(false);
		
	
	}
     var filename=outparameters.getValue("fileName");
     var fieldName = getDecodeStr(filename);
     var win=open("/servlet/vfsservlet?fileid="+fieldName+"&fromjavafolder=true","excel");
  }
}	


function confirmSalary(selectID,operate,userid)
{
			var arguments=new Array();
			if(operate=='confirmAll') 
			{
				arguments[0]="同意，审批通过。";
				arguments[1]="批准意见";  
				if(operate=='confirmAll')
				{	
					arguments[2]="confirmAll";
					arguments[3]=isSendMessage;
				}  
		    }
		   
		    
		    var strurl="/gz/gz_accounting/rejectCause.jsp";
		    var iframe_url="/general/query/common/iframe_query.jsp?src="+strurl; 
			var ss=window.showModalDialog(iframe_url,arguments,"dialogWidth=460px;dialogHeight=350px;resizable=yes;scroll=no;status=no;");  
		    if(ss)
		    {
			    document.accountingForm.selectGzRecords.value="";
			    document.accountingForm.rejectCause.value=ss[0];
			    if(operate=='confirmAll'&&isSendMessage=='1')
			    	document.accountingForm.sendMen.value=ss[1];
			    document.accountingForm.approveObject.value=userid;
			    document.accountingForm.bosdate.value=replaceAll(a00z2,"-","\.");
			    document.accountingForm.count.value=a00z3;
			    document.accountingForm.salaryid.value=salaryid;	
				document.accountingForm.action="/gz/gz_accounting/gz_sptable.do?b_appealCollect=appeal&opt="+operate;
				document.accountingForm.submit();
			}
			else
			{
				if(operate=='confirmAll') 
				{
				 
					setState(true);
				
				}
			}
}

function appealSpData(selectID,opt,userid)
{
		
		if(isTotalControl=='1'||verify_ctrl=='1')
		{
					var waitInfo=eval("wait");	
					waitInfo.style.display="none";
				
		}	
		
		var returnValue= select_user_dialog("1",2,"0",salaryid);
		if(returnValue)
		{		
		    if(trim(returnValue.content).length==0)
			{
				alert("请选择审批用户!");
				setState(false);
				return;
			}	
			if(userid==returnValue.content)
			{
				alert("不能指定本人为审批对象！");
				setState(false);
				return;
			}
			var waitInfo=eval("wait");	
			document.getElementById("wait_desc").innerHTML="正在进行报批操作......";
			waitInfo.style.display="block";
			
			document.accountingForm.bosdate.value=replaceAll(a00z2,"-","\.");
			document.accountingForm.count.value=a00z3;
			document.accountingForm.salaryid.value=salaryid;	
			document.accountingForm.action="/gz/gz_accounting/gz_sptable.do?b_appealCollect=appeal&approveObject="+returnValue.content+"&opt="+opt;
			document.accountingForm.submit();
			
		}
		else
		{ 
			setState(false);
		}
		
}
	