/******************  dengcan start *****************/
//薪资发放  重新导入
var temp_fullname;//获取fullname——》a0101——》username  zhaoxg add 2013-10-24
var temp_msg;//是否满足重发要求
function reImport_ff(salaryid,gz_module)
{
 
	var thecodeurl="/gz/gz_accounting/batchimport.do?b_query=link`fromModel=ff`salaryid="+salaryid+"`gz_module="+gz_module; 
	var iframe_url="/general/query/common/iframe_query.jsp?src="+$URL.encode(thecodeurl);
	var retvo= window.showModalDialog(iframe_url, null, 
		        "dialogWidth:530px; dialogHeight:500px;resizable:no;center:yes;scroll:no;status:no");		
  	if(retvo==null)
  	 	return ;		        
	if(retvo.success=="1")
	{
	    var salaryid=retvo.salaryid;
	    // signSaveEmpfilterSql,gby?í????±ê?????????????????????ó????????
		accountingForm.action="/gz/gz_accounting/gz_table.do?b_query=link&salaryid="+salaryid+urlStr+"&signSaveEmpfilterSql=1";;
		accountingForm.submit();  	
	}
}

//薪资审批  重新导入
function reImport_sp(salaryid,gz_module)
{
	
	var ym=document.accountingForm.bosdate.value;
	var count=document.accountingForm.count.value;
	var thecodeurl="/gz/gz_accounting/batchimport.do?b_query=link`fromModel=sp`opt=sp`ym="+ym+"`count="+count+"`salaryid="+salaryid+"`gz_module="+gz_module; 
	var iframe_url="/general/query/common/iframe_query.jsp?src="+$URL.encode(thecodeurl);
	var retvo= window.showModalDialog(iframe_url, null, 
		       "dialogWidth:530px; dialogHeight:500px;resizable:no;center:yes;scroll:no;status:no");		
  	if(retvo==null)
  	 	return ;		        
	if(retvo.success=="1")
	{
	    var salaryid=retvo.salaryid; 	
		accountingForm.action="/gz/gz_accounting/gz_sptable.do?b_query=link&salaryid="+salaryid;
	    accountingForm.submit();  
	}

}




function compare(salaryid,gz_module,flow_flag)
{
	      var arguments=new Array();     
	     var strurl="/gz/gz_accounting/datachangecompare.do?b_query=link&salaryid="+salaryid+"&gz_module="+gz_module+"&flow_flag="+flow_flag+"&isVisible=0";
		// var iframe_url="/general/query/common/iframe_query.jsp?src="+strurl; 
		 // var flag=window.showModalDialog(iframe_url,arguments,"dialogWidth=700px;dialogHeight=650px;resizable=yes;scroll=no;status=no;");  
		  var iWidth=800;                           //弹出窗口的宽度;
         var iHeight=600;                        //弹出窗口的高度;
         var iTop = (window.screen.availHeight-30-iHeight)/2;        //获得窗口的垂直位置;
        var iLeft = (window.screen.availWidth-10-iWidth)/2;           //获得窗口的水平位置;
        window.open(strurl,"_blank",
       "width="+iWidth+",height="+iHeight+",left="+iLeft+",top="+iTop+",toolbar=no,location=no,directories==1,status=no,menubar=no,scrollbars=0,resizable=no,z-look=1'");
		 
}


//个别计算
	function validateFormula(salaryid)
	{
		var tablename="table"+gztablename;
	    table=$(tablename);
	    dataset=table.getDataset();
		  var record=dataset.getFirstRecord();
		  var selectID="";	
		  var num=0;	
		  var noNum=0;
		  while (record) 
		  {
				if (record.getValue("select"))
				{			
						num++;
						selectID+="#"+record.getValue("a0100")+"/"+record.getValue("nbase")+"/"+record.getValue("a00z0")+"/"+record.getValue("a00z1");
							   
				}
				record=record.getNextRecord();
		 }  	
			
		 if(num==0)
		 {
				alert(GZ_ACCOUNTING_INFO5+"！");
				return;
		 }
			
		 document.accountingForm.selectGzRecords.value=selectID.substring(1);
		
		 var thecodeurl="/gz/gz_accounting/formulalist.do?b_query=link`flag=0`salaryid="+salaryid;
		 var iframe_url="/general/query/common/iframe_query.jsp?src="+$URL.encode(thecodeurl);
		 if(isIE6() ){
		 		 var retvo= window.showModalDialog(iframe_url, null, 
					        "dialogWidth:450px; dialogHeight:480px;resizable:no;center:yes;scroll:no;status:no");
		 }else{
		 		 var retvo= window.showModalDialog(iframe_url, null, 
					        "dialogWidth:420px; dialogHeight:450px;resizable:no;center:yes;scroll:no;status:no");
		 }
			
		// if(retvo!="1")
		//		return;
		if(retvo)
		{
			var itemids="";
			for(var i=0;i<retvo.length;i++)	
				itemids+=","+retvo[i];
				setState0(true);
			var waitInfo=eval("wait");
			document.getElementById("wait_desc").innerHTML="正在计算,请稍候...";			
			waitInfo.style.display="block";
	
			document.accountingForm.action="/gz/gz_accounting/gz_table.do?b_personalCompute=appeal&itemids="+itemids+urlStr;
			document.accountingForm.submit();
		}
		// var In_parameters="salaryid="+salaryid;
		// var request=new Request({method:'post',asynchronous:false,parameters:In_parameters,onSuccess:validate_ok,functionId:'3020070115'});			
	}
	
	
//输出 EXCEL OR xml
    function showfile(outparamters)
	{
		var fileName=outparamters.getValue("fileName");
		fileName = getDecodeStr(fileName);
		var flag=outparamters.getValue("flag");
		if(flag==2){
			var win=open("/servlet/vfsservlet?fileid="+fileName+"&fromjavafolder=true","xml");
		}
		else
		{
			var win=open("/servlet/vfsservlet?fileid="+fileName+"&fromjavafolder=true","excel");
		}
		
	}
	
	function exportTable(salaryid)
	{
		    var arguments=new Array();     
		    var strurl="/gz/gz_accounting/in_out.do?b_setFormat=link&a_code="+a_code;
		    
		    var flag=window.showModalDialog(strurl,arguments,"dialogWidth=380px;dialogHeight=440px;resizable=yes;scroll=no;status=no;");  
		    if(flag){
		    	flag = getDecodeStr(flag);
			    var win=open("/servlet/vfsservlet?fileid="+flag+"&fromjavafolder=true","xml");
		    }
		    
		    
	}
	//审批导出excel
		function exportTable(salaryid,sp)
	{
		    var arguments=new Array();     
		    var strurl="/gz/gz_accounting/in_out.do?b_setFormat=link`a_code="+a_code+"`sp="+sp;
		    var iframe_url="/general/query/common/iframe_query.jsp?src="+$URL.encode(strurl);
		    
		    var flag=window.showModalDialog(iframe_url,arguments,"dialogWidth=380px;dialogHeight=440px;resizable=yes;scroll=no;status=no;");  
		    if(flag){
		    	flag = getDecodeStr(flag);
		    	window.location.target="_blank";
				window.location.href = "/servlet/vfsservlet?fileid="+flag+"&fromjavafolder=true";
		    }
	}
	
	//报审  1:驳回 2：报审
	function report(opt)
	{
	
		var desc="您确定要报审吗";
		if(opt=='1')
		{
			  desc="您确定要驳回吗";
			  var tablename="table"+gztablename;
		   	  table=$(tablename);
		   	  dataset=table.getDataset();
			  var record=dataset.getFirstRecord();
			  var selectID="";	
			  var num=0;	
			  var noNum=0;
			  while (record) 
			  {
					if (record.getValue("select"))
					{			
						   if(appflag=='false')
						   {
							   if(record.getValue("sp_flag2")=='02')
							   {
									num++;
									selectID+="#"+record.getValue("a0100")+"/"+record.getValue("nbase")+"/"+record.getValue("a00z0")+"/"+record.getValue("a00z1");
								}
						   }
						   else
						   {
							   if(record.getValue("sp_flag2")=='02'&&(record.getValue("sp_flag")=='01'||record.getValue("sp_flag")=='07'))
							   {
									num++;
									selectID+="#"+record.getValue("a0100")+"/"+record.getValue("nbase")+"/"+record.getValue("a00z0")+"/"+record.getValue("a00z1");
								}
						   }
								   
					}
					record=record.getNextRecord();
			 }  	
				
			 if(num==0)
			 {
					alert("请选择可以驳回的记录！");
					return;
			 }
			 document.accountingForm.selectGzRecords.value=selectID.substring(1);
		}	
		if(confirm(desc+"？"))
		{
			if(opt=='1')
			{
				var arguments=new Array();
				arguments[0]="";
				arguments[1]="驳回原因";
			    var strurl="/gz/gz_accounting/rejectCause.jsp";
			    var iframe_url="/general/query/common/iframe_query.jsp?src="+strurl; 
				var ss=window.showModalDialog(iframe_url,arguments,"dialogWidth=460px;dialogHeight=350px;resizable=yes;scroll=no;status=no;");  
			    if(ss)
			    {
				    document.accountingForm.rejectCause.value=ss[0];
					document.accountingForm.action="/gz/gz_accounting/gz_table.do?b_report=appeal&opt="+opt+urlStr;
					document.accountingForm.submit();
				}
			}
			else
			{
				if(verify_ctrl=='1'){//进行审核公式控制,lis
			        var waitInfo=eval("wait");      
			            waitInfo.style.display='block';
			            document.getElementById("wait_desc").innerHTML="正在进行审核操作,请稍候...";
			            setState0(true);
			        
			       var hashvo=new ParameterSet();
			       hashvo.setValue("a_code","UN");
			       hashvo.setValue("condid","all");
			       hashvo.setValue("salaryid",salaryid);
			       hashvo.setValue("type","0");
			       hashvo.setValue("opt",opt);
			       var request=new Request({asynchronous:false,onSuccess:checked_ok,functionId:'3020070016'},hashvo);   
			    }else{
			    	document.accountingForm.action="/gz/gz_accounting/gz_table.do?b_report=appeal&opt="+opt+urlStr;
					document.accountingForm.submit();
			    }
			}
		
		}
	}

	function checked_ok(outparameters)
	{
	  var waitInfo=eval("wait");    
	      waitInfo.style.display="none";
	      setState0(false)
	  var msg=outparameters.getValue("msg");
	  var opt=outparameters.getValue("opt");
	  if(msg=='0' || msg=='no')
	  {
		  document.accountingForm.action="/gz/gz_accounting/gz_table.do?b_report=appeal&opt="+opt+urlStr;
		  document.accountingForm.submit();
	  }
	  else{
	     var filename=outparameters.getValue("fileName");
	     filename = getDecodeStr(filename);
	 	 var win=open("/servlet/vfsservlet?fileid="+filename+"&fromjavafolder=true","excel");
	  }
	}
	
	function setState0(_boolean)
	{
		var bt=document.getElementById("buttonappeal");
		if(bt)
			bt.disabled=_boolean;
		bt=document.getElementById("buttondelselected");
		if(bt)
			bt.disabled=_boolean;
		bt=document.getElementById("buttonreject");
		if(bt)
			bt.disabled=_boolean;	
		bt=document.getElementById("buttonsh_formula");
		if(bt)
			bt.disabled=_boolean;	
		bt=document.getElementById("buttoncompute");
		if(bt)
			bt.disabled=_boolean;	
		bt=document.getElementById("buttonpersonalcompute");
		if(bt)
			bt.disabled=_boolean;	
			
		bt=document.getElementById("buttontable");
		if(bt)
				bt.disabled=_boolean;
		bt=document.getElementById("buttontable2");
		if(bt)
				bt.disabled=_boolean;
		bt=document.getElementById("buttonreject");
		if(bt)
				bt.disabled=_boolean;
				
		bt=document.getElementById("buttonrejectAll");
		if(bt)
				bt.disabled=_boolean;
		bt=document.getElementById("buttonappeal1");
		if(bt)
			bt.disabled=_boolean;
		bt=document.getElementById("buttonappeal2");
		if(bt)
			bt.disabled=_boolean;
			
			
		bt=document.getElementById("buttondelselected");  //删除
		if(bt)
			bt.disabled=_boolean;
			
		bt=document.getElementById("buttonsavedata"); //保存
		if(bt)
			bt.disabled=_boolean;
		bt=document.getElementById("buttoncompute");  //计算
		if(bt)
			bt.disabled=_boolean;
			
		bt=document.getElementById("buttonsh_formula"); //审核
		if(bt)
			bt.disabled=_boolean;
		bt=document.getElementById("buttonsendmail");
		if(bt)
			bt.disabled=_boolean;	
		bt=document.getElementById("buttonreimport");
		if(bt)
			bt.disabled=_boolean;
		bt=document.getElementById("buttongoback1");
		if(bt)
			bt.disabled=_boolean;	
			
	    var menuitem=getMenuItem("deletem3");
		if(menuitem)
			menuitem.enabled=!_boolean;	
			
			
	}

	function  appealTotalControl(auserid,aisAppealData,salaryid)
	{
		if(isEdit())
  		{
  			alert("数据已被更改，请执行保存操作!");
  			return;
  		}
	 //   var bt=document.getElementById("buttonappeal");
	//	bt.disabled=true;
		setState0(true);
		var hashvo=new ParameterSet();
		hashvo.setValue("salaryid",salaryid);
		hashvo.setValue("userid",auserid); 
		hashvo.setValue("isAppealData",aisAppealData); 
		
		
		var str="";
		if(isTotalControl=='1')
			str+="、总额校验";
		if(verify_ctrl=='1')
			str+="、数据审核";
		if(isTotalControl=='1'||verify_ctrl=='1')
		{
			var waitInfo=eval("wait");	
			document.getElementById("wait_desc").innerHTML="正在进行"+str.substring(1)+",请稍候...";
					
			waitInfo.style.display="block";
		
		} 
		var request=new Request({method:'post',asynchronous:true,onSuccess:appealTotalControl2,functionId:'3020070116'},hashvo);
	}
	
	function appealTotalControl2(outparamters)
	{
		var userid=outparamters.getValue("userid");
		var isAppealData=outparamters.getValue("isAppealData");
		
		var info=getDecodeStr(outparamters.getValue("info"));
		var ctrlType = outparamters.getValue("ctrlType");
		if(info=='success')
		{
			if(verify_ctrl=='1')
				verifyFormula(salaryid,isAppealData,userid)
			else
				appeal0(userid,isAppealData);
				
			return;	
		}
		else
		{
			var isOver=outparamters.getValue("isOver");    
			if((typeof(isOver)=='undefined')&&info=="")
			{
				var waitInfo=eval("wait");	
				waitInfo.style.display="none"; 
				setState0(false);
			}
			else
			{
				
				if(isOver=='0')
				{
					if(info.length>0)
						alert(info);
					if(info.indexOf("没有批复")!=-1)
					{
						if(isTotalControl=='1')
						{
							var waitInfo=eval("wait");	
							waitInfo.style.display="none";
						}
					//	var bt=document.getElementById("buttonappeal");
					//	bt.disabled=false;
						setState0(false);
					}
					else
					{
						if(verify_ctrl=='1')
							verifyFormula(salaryid,isAppealData,userid)
						else
							appeal0(userid,isAppealData);
					}
				}
				else
				{
				   if(ctrlType=='1')//强制控制
				   {
					  if(isTotalControl=='1')
					  {
					    	var waitInfo=eval("wait");	
					     	waitInfo.style.display="none";
				      }
					  alert(info);
					  setState0(false);
			       }else{
			           var alertInfo=getDecodeStr(outparamters.getValue("alertInfo"));
			          if(confirm(alertInfo))
			          {
			             if(verify_ctrl=='1')
							verifyFormula(salaryid,isAppealData,userid)
						else
							appeal0(userid,isAppealData);
			          }else{
			              if(isTotalControl=='1')
					     {
					    	var waitInfo=eval("wait");	
					     	waitInfo.style.display="none";
				         }
				    	  setState0(false);
			          }
			       }
				}
			}
		
		}
	}
	
	
	
	
function check_ok2(outparameters)
{
  var msg=outparameters.getValue("msg");
  var aisAppealData=outparameters.getValue("aisAppealData");
  var auserid=outparameters.getValue("auserid");
  if(msg=='0')
  {
    appeal0(auserid,aisAppealData);
  }
  else if(msg=='no')
  {
     appeal0(auserid,aisAppealData); 
  }
  else{
  	if(verify_ctrl=='1')
	 {
					var waitInfo=eval("wait");	
					waitInfo.style.display="none";
	 }
  
  	 alert("审核不通过，不允许操作!");
  	 
	 setState0(false)
     var filename=outparameters.getValue("fileName");
	 filename = getDecodeStr(filename);
 	 var win=open("/servlet/vfsservlet?fileid="+filename+"&fromjavafolder=true","excel");
  }
}
	
	
	
	
	function appeal0(auserid,aisAppealData)
	{
		
		var userid=auserid
		var isAppealData=aisAppealData;
		if(isAppealData=='0')
		{
			if(verify_ctrl=='1'||isTotalControl=='1')
			{
							var waitInfo=eval("wait");	
							waitInfo.style.display="none";
			}
			alert(GZ_ACCOUNTING_INFO6+"!");
		//	var bt=document.getElementById("buttonappeal");
		//	bt.disabled=false;
			setState0(false);
			return;
		}
		if(isNotSpFlag2Records=='1')
		{
			if(verify_ctrl=='1'||isTotalControl=='1')
			{
							var waitInfo=eval("wait");	
							waitInfo.style.display="none";
			}
		//	alert("有未报审的记录!");
		//	setState0(false)
		//	return;
			if(!confirm("有未报审的记录,您是否继续报批?"))
			{
				setState0(false);
		  		return;
			}
		
		
		}
		
		var waitInfo=eval("wait");	
		waitInfo.style.display="none";
		

		
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
				 
					setState0(false);
	    			return;
	    		}
			}
		}
		else  
			returnValue= select_user_dialog("1",2,"0",salaryid); 
		if(returnValue)
		{		
			if(trim(returnValue.content).length==0)
			{
				if(verify_ctrl=='1'||isTotalControl=='1')
				{
								var waitInfo=eval("wait");	
								waitInfo.style.display="none";
				}
				alert("请选择审批用户!");
			//	var bt=document.getElementById("buttonappeal");
			//	bt.disabled=false;
				setState0(false);
				return;
			}
			if(userid==returnValue.content)
			{
				if(verify_ctrl=='1'||isTotalControl=='1')
				{
								var waitInfo=eval("wait");	
								waitInfo.style.display="none";
				}
				alert(GZ_ACCOUNTING_INFO7+"！");
			//	var bt=document.getElementById("buttonappeal");
			//	bt.disabled=false;
				setState0(false);
				return;
			}
			getName(returnValue.content)
			if(!confirm("确定要报批给"+temp_fullname+"?"))//赵旭光  add 搜房网 报批前确认  2013-10-23
			{
				setState0(false);
		  		return;
			}
			var waitInfo=eval("wait");	
			document.getElementById("wait_desc").innerHTML="正在进行上报操作,请稍候...";
			waitInfo.style.display="block";
			document.accountingForm.approveObject.value=returnValue.content;
			document.accountingForm.action="/gz/gz_accounting/gz_table.do?b_appeal=appeal";
			document.accountingForm.submit();
			
		}
		else
		{
			if(verify_ctrl=='1'||isTotalControl=='1')
			{
							var waitInfo=eval("wait");	
							waitInfo.style.display="none";
			}
		//	var bt=document.getElementById("buttonappeal");
		//	bt.disabled=false;
			setState0(false);
		}
	} 
	function getName(usrname){
		 	var hashvo=new ParameterSet();
			hashvo.setValue("usrname",usrname);
   			var request=new Request({asynchronous:false,onSuccess:getName_ok,functionId:'3020070088'},hashvo);	
	}
	function getName_ok(outparameters){
		temp_fullname=outparameters.getValue("name");
	}
	function go_back(gzmodul)
	{
		accountingForm.target="il_body";
		accountingForm.action="/gz/gz_accounting/gz_set_list.do?b_query=link&flow_flag=0&gz_module="+gzmodul;
		accountingForm.submit();
	}
	function go_back2(theyear,themonth,orgCode)
	{	
		accountingForm.target="il_body";
		accountingForm.action="/gz/premium/premium_allocate/monthPremiumList.do?b_query=link&theYear="+theyear+"&theMonth="+themonth+"&orgcode="+orgCode;
		accountingForm.submit();
	}	
	
		
	function personalcompute()
	{
		accountingForm.action="/gz/gz_accounting/gz_table.do?b_compute=compute";
		accountingForm.submit();
	}
	
	function change_condlist_ok(outparameters)
	{
		  var filterList = outparameters.getValue("filterCondList");
		  AjaxBind.bind(accountingForm.condid,filterList); 
		  var obj=$("condid"); 
		  if(obj.options.length==2)
		  {
		    accountingForm.action="/gz/gz_accounting/gz_table.do?b_query=link&salaryid="+salaryid+urlStr;
		    accountingForm.submit();
		  }
		  else
		  {
		     for(var i=0;i<obj.options.length;i++)
		     {
		        if(obj.options[i].value==prv_filter_id)
		        {
		            obj.options[i].selected=true;
		            return;
		        }
		     }
		    accountingForm.action="/gz/gz_accounting/gz_table.do?b_query=link&salaryid="+salaryid+urlStr;
		    accountingForm.submit();
		  }     
	}
	
	function change_itemlist_ok(outparameters)
	{
		  var filterList = outparameters.getValue("itemfilterlist");
		  var model=outparameters.getValue("model");
		  var salaryid=outparameters.getValue("salaryid");
		  AjaxBind.bind(accountingForm.itemid,filterList); 
		  var obj=$("itemid"); 
		  for(var i=0;i<obj.options.length;i++)
		  {
		      obj.options[i].text=getDecodeStr(obj.options[i].text);
		  }
		  if(obj.options.length==2)
		  {
		    if(model=='1')
		    {
		       accountingForm.action="/gz/gz_accounting/gz_sptable.do?b_query=link&salaryid="+salaryid;
		    }
		    else
		    {
		         accountingForm.action="/gz/gz_accounting/gz_table.do?b_query=link&salaryid="+salaryid+urlStr;
		    }
		    accountingForm.submit();
		  }
		  else
		  {
		     for(var i=0;i<obj.options.length;i++)
		     {
		        if(obj.options[i].value==prv_project_id)
		        {
		            obj.options[i].selected=true;
		            return;
		        }
		     }
		    if(model=='1')
		    {
		       accountingForm.action="/gz/gz_accounting/gz_sptable.do?b_query=link&salaryid="+salaryid;
		    }
		    else
		    {
		         accountingForm.action="/gz/gz_accounting/gz_table.do?b_query=link&salaryid="+salaryid+urlStr;
		    }
			accountingForm.submit();
		  }     
	}
	function changeItemidList(salaryid,model)
	{
		 var hashVo=new ParameterSet();
		 hashVo.setValue("salaryid",salaryid);
		 hashVo.setValue("model",model);
		 var In_parameters="opt=1";
		 var request=new Request({method:'post',asynchronous:false,parameters:In_parameters,onSuccess:change_itemlist_ok,functionId:'3020070203'},hashVo);			
		    
	}
	
	function bankdisk_changeCondList(salaryid)
	{
		 var hashVo=new ParameterSet();
		 hashVo.setValue("isclose","2");
		 hashVo.setValue("salaryid",salaryid);
		 //var In_parameters="opt=1";
		 var request=new Request({method:'post',asynchronous:false,onSuccess:change_condlist_ok,functionId:'3020100017'},hashVo);			
		    
	}
/****************   lilinbing start  ************************/
function symbol(editor,strexpr){
    var expr_editor = document.getElementById(editor);
    expr_editor.focus();
    var element;
    if(document.selection){
        element = document.selection;
        if (element!=null) {
            var rge = element.createRange();
            if (rge!=null)
                rge.text=strexpr;
        }
	}else{
    	//插入公式 浏览器兼容  wangbs 20190320
        element = window.getSelection();
        var start =expr_editor.selectionStart;
        expr_editor.value = expr_editor.value.substring(0,start)+strexpr+expr_editor.value.substring(start,expr_editor.value.length);
        expr_editor.setSelectionRange(start+strexpr.length,start+strexpr.length);
	}
}

function function_Wizard(salaryid,formula){
	//兼容浏览器 wangbs 20190320
    var thecodeurl ="/org/autostatic/mainp/function_Wizard.do?b_query=link&salaryid="+salaryid
    				+"&tableid=&checktemp=salary&mode=xzgl_jsgs";

    var iTop = (window.screen.height-30-305)/2; //获得窗口的垂直位置;
    var iLeft = (window.screen.width-10-470)/2;  //获得窗口的水平位置;
    window.open(thecodeurl,'','height=430, width=400,top='+iTop+',left='+iLeft+',toolbar=no,menubar=no,scrollbars=no,resizable=no,location=no,status=no');
}
function condiTions(formula,id){
	formula = $URL.encode(formula);
    var thecodeurl ="/gz/formula/calculating_conditions.do?b_query=link&conditions="+formula+"&id="+id; 
    var return_vo= window.showModalDialog(thecodeurl, "", 
              "dialogWidth:520px; dialogHeight:430px;resizable:no;center:yes;scroll:no;status:no");      
    return return_vo;  
}
function setSpchange(entry_type){
 	var thecodeurl ="/gz/gz_accounting/set_change_sp.do?b_query=link&flag=alert&entry_type="+entry_type; 
 	if(isIE6()){
 	    var return_vo= window.showModalDialog(thecodeurl, "", 
              "dialogWidth:520px; dialogHeight:430px;resizable:no;center:yes;scroll:yes;status:no");
 	}else{
 	    var return_vo= window.showModalDialog(thecodeurl, "", 
              "dialogWidth:500px; dialogHeight:430px;resizable:no;center:yes;scroll:yes;status:no");
 	}
}
function setChangeMore(outparameters){
     var msg = outparameters.getValue("msg");
     var salaryid=outparameters.getValue("salaryid");
     var gz_module=outparameters.getValue("gz_module");
     var flow_flag=outparameters.getValue("flow_flag");
     if(msg=='1')
     {
          alert(GZ_ACCOUNTING_IFNO8+"！");
          return;
     }
	if(document.getElementById("condid").value=='new'){
		alert(GZ_ACCOUNTING_INFO9+"!");
		return;
	}
	if(document.getElementById("bosdate").value==''){
		alert(GZ_ACCOUNTING_INFO10+"!");
		return;
	}
	if(document.getElementById("count").value==''){
		alert(GZ_ACCOUNTING_INFO11+"!");
		return;
	}
	var thecodeurl ="/gz/gz_accounting/changesmore.do?b_query=link";
	//var thecodeurl="/gz/gz_accounting/datachangecompare.do?b_query=link`salaryid="+salaryid+"`gz_module="+gz_module+"`flow_flag="+flow_flag+"`isVisible=1";
	//var iframe_url="/general/query/common/iframe_query.jsp?src="+thecodeurl;
    //var return_vo= window.showModalDialog(iframe_url,"", 
              //"dialogWidth:750px; dialogHeight:550px;resizable:no;center:yes;scroll:yes;status:yes");
     var iWidth=800;                           //弹出窗口的宽度;
    /* 薪资审批-数据比对 去掉滚动条 xiaoyun 2014-9-26 start */ 
    var iHeight=620;                        //弹出窗口的高度;
    /* 薪资审批-数据比对 去掉滚动条 xiaoyun 2014-9-26 end */
    var iTop = (window.screen.availHeight-30-iHeight)/2;        //获得窗口的垂直位置;
    var iLeft = (window.screen.availWidth-10-iWidth)/2;           //获得窗口的水平位置;
    window.open(thecodeurl,"_blank",
    "width="+iWidth+",height="+iHeight+",left="+iLeft+",top="+iTop+",toolbar=no,location=no,directories==1,status=no,menubar=no,scrollbars=1,resizable=no,z-look=1'");
    
}
function checkHasCompareField(salaryid,gz_module,flow_flag)
{
    var hashVo=new ParameterSet();
    hashVo.setValue("salaryid",salaryid);
    hashVo.setValue("gz_module",gz_module);
    hashVo.setValue("flow_flag",flow_flag);
    var In_parameters="opt=1";
    var request=new Request({method:'post',asynchronous:false,parameters:In_parameters,onSuccess:setChangeMore,functionId:'3020110046'},hashVo);			
}
function condFormula(salaryid){
 	var thecodeurl ="/gz/gz_accounting/iframformula.jsp?salaryid="+salaryid;
 	if(isIE6()){
 	 	var return_vo= window.showModalDialog(thecodeurl,"windows1", 
              "dialogWidth:820px; dialogHeight:520px;resizable:no;center:yes;scroll:no;status:no");
 	}else{
 	 	var return_vo= window.showModalDialog(thecodeurl,"windows1", 
              "dialogWidth:800px; dialogHeight:520px;resizable:no;center:yes;scroll:no;status:no");
 	}
}
/**
 * 判断当前浏览器是否为ie6
 * 返回boolean 可直接用于判断 
 * @returns {Boolean}
 */
function isIE6() 
{ 
	if(navigator.appName == "Microsoft Internet Explorer") 
	{ 
		if(navigator.userAgent.indexOf("MSIE 6.0")>0) 
		{ 
			return true;
		}else{
			return false;
		}
	}else{
		return false;
	}
}
function gzPayrollViewHide(salaryid,flag){
 	var checkview = hireView(salaryid,flag);
 	if(checkview.length>0){
 		accountingForm.action="/gz/gz_accounting/gz_table.do?b_query=link&salaryid="+salaryid+urlStr;
		accountingForm.submit();
 	} 
}
function gzSpHide(salaryid,flag){
 	var checkview = hireView(salaryid,flag);
 	if(checkview.length>0){
 		accountingForm.action="/gz/gz_accounting/gz_sptable.do?b_query=link&salaryid="+salaryid;
		accountingForm.submit();
 	} 
}
function gzPayrollSort(salaryid){
	var checksort = setSorting(salaryid);
 	if(checksort.length>0){
 		accountingForm.action="/gz/gz_accounting/gz_table.do?b_query=link&salaryid="+salaryid+urlStr;
		accountingForm.submit();
 	} 
}
/**
排序
itemid 指标id
itemdesc 指标中文名称
dbname  所要排序的数据库表的名称
where 条件
orderby 顺序显示
**/
function setSorting(salaryid){
 	var thecodeurl ="/gz/gz_amount/tax/sorting.do?b_query=link&salaryid="+salaryid;	
 	if(isIE6() ){
 	    var return_vo= window.showModalDialog(thecodeurl, "", 
              "dialogWidth:390px; dialogHeight:430px;resizable:no;center:yes;scroll:yes;status:no");
 	}else{
 	    var return_vo= window.showModalDialog(thecodeurl, "", 
              "dialogWidth:360px; dialogHeight:400px;resizable:no;center:yes;scroll:yes;status:no");
 	}

    if(return_vo!=null){
    	return return_vo;
    }else{
    	return "";
    }
}
/**
itemid 指标id
itemdesc 指标中文名称
checkview 检测是否隐藏
dbname  所要设置显示/隐藏的数据库表的名称
where 条件
orderby 顺序显示
**/
function hireView(salaryid,flag){
 	var thecodeurl ="/gz/gz_amount/tax/gz_viewHide.do?b_query=link&salaryid="+salaryid+"&flag="+flag;
 				
    var return_vo= window.showModalDialog(thecodeurl, "", 
              "dialogWidth:365px; dialogHeight:400px;resizable:no;center:yes;scroll:no;status:no"); // modify by xiaoyun 薪资发放/显示/显示隐藏指标：页面中缺线 2014-10-14
    if(return_vo!=null){
    	return return_vo;
    }else{
    	return "";
    }
}

/****************   lilinbing end  ************************/

/**????????*/


function open_tax_mx()
{
	var theurl="/gz/gz_accounting/tax/gz_tax_org_tree.do?br_link=link";
	window.open(theurl,"_blank","");
}
/**????????*/
var _tempDesc="";
function chginfo_bd(salaryid,gz_module)
{
   var hashvo=new ParameterSet();
	hashvo.setValue("salaryid",salaryid);
	hashvo.setValue("gz_module",gz_module); 
	hashvo.setValue("fromflag","1"); 
	var itemid=document.accountingForm.itemid.value;
    var fieldstr=document.accountingForm.proright_str.value;
    hashvo.setValue("filterid",itemid);
    hashvo.setValue("fieldstr",getEncodeStr(fieldstr));
    
    var waitInfo=eval("wait");	
    _tempDesc=document.getElementById("wait_desc").innerHTML;
	document.getElementById("wait_desc").innerHTML="正在变动比对,请稍候......";		
	waitInfo.style.display="block";
	var menuitem=getMenuItem("m1_chg");
  	if(menuitem)
  			menuitem.enabled=false;
	
	
	var request=new Request({method:'post',asynchronous:true,onSuccess:openInfoWindow,functionId:'3020072010'},hashvo);
	
}
function openInfoWindow(outparameters)
{

	var waitInfo=eval("wait");	
	document.getElementById("wait_desc").innerHTML=_tempDesc;		
	waitInfo.style.display="none";
	var menuitem=getMenuItem("m1_chg");
  	if(menuitem)
  			menuitem.enabled=true;

    var add=outparameters.getValue("add");
	var del=outparameters.getValue("del");
	var info=outparameters.getValue("info");
	var stop=outparameters.getValue("stop");
	var salaryid=outparameters.getValue("salaryid");
	var gz_module=outparameters.getValue("gz_module");
	var fromflag=outparameters.getValue("fromflag");
	var cname=getDecodeStr(outparameters.getValue("cname"));
	if(add=='0' && del=='0' && info=='0' && stop=='0')
	{
	    alert(salaryid+"."+cname+"中没有新增，减少，或信息有变化的人员！");
	    return;
	}
	var itemid=outparameters.getValue("filterid");
	
    var fieldstr=outparameters.getValue("fieldstr");
   var url="/gz/gz_accounting/change_list.do?b_query=link`add="+add+"`del="+del+"`info="+info+"`stop="+stop+"`fromflag="+fromflag+"`gz_module="+gz_module+"`salaryid="+salaryid+"`filterid="+itemid+"`fieldstr="+fieldstr;
   var iframe_url="/general/query/common/iframe_query.jsp?src="+$URL.encode(url);
   var obj=window.showModalDialog(iframe_url,null,"dialogWidth="+(window.screen.width-40)+"px;dialogHeight=650px;resizable:yes;center:yes;scroll:no;status:no");   
   if(obj!=null&&obj==1)
   {  
	   document.accountingForm.target="il_body";
	   document.accountingForm.action="/gz/gz_accounting/gz_org_tree.do?b_query=link&salaryid="+salaryid+urlStr;
	   document.accountingForm.submit();
	}
}

function searchdata(salaryid)
{
		accountingForm.action="/gz/gz_accounting/gz_sptable.do?b_query=link&zjjt=1&salaryid="+salaryid;
		accountingForm.submit();    		
}
function search_gz_data_byitem(salaryid,setobj,model)
{
   var itemid;
   for(i=0;i<setobj.options.length;i++)
   {
      if(setobj.options[i].selected)
      {
    	itemid=setobj.options[i].value;
    	break;
      }   
   }	
   if(itemid!="new")
   {
	    document.getElementById("proright_str").value="";
	    if(model=='1')
	    {
	       accountingForm.action="/gz/gz_accounting/gz_sptable.do?b_query=link&salaryid="+salaryid;
	    }
	    else
	    {
	    	accountingForm.action="/gz/gz_accounting/gz_table.do?b_query=link&salaryid="+salaryid;
	    }
		accountingForm.submit();    		
   }	
   else
   {
   		var thecodeurl ="/gz/gz_accounting/gzprofilter.do?b_query=link&opt=2&salaryid="+salaryid;
    	var return_vo= window.showModalDialog(thecodeurl, "", 
              "dialogWidth:480px; dialogHeight:400px;resizable:no;center:yes;scroll:yes;status:no");
   		if(return_vo!=null)
   		{
   			document.getElementById("proright_str").value=return_vo[0];
   			var obj=document.getElementById("itemid");
   			obj.options[obj.options.length-1].value=return_vo[1];   
            obj.options[obj.options.length-1].text=getDecodeStr(return_vo[2]);  
   			obj.options[obj.options.length-1].selected=true;
   			
			if(model=='1')
	        {
	          accountingForm.action="/gz/gz_accounting/gz_sptable.do?b_query=link&salaryid="+salaryid;
	        }
	        else
	        {
	    	   accountingForm.action="/gz/gz_accounting/gz_table.do?b_query=link&salaryid="+salaryid;
	        }
  	    	accountingForm.submit();
   		}else
   		{
   			changeItemidList(salaryid,model);
   		}		
   }
}

	function search_gz_spdata(salaryid)
	{
		accountingForm.action="/gz/gz_accounting/gz_sptable.do?b_query=link&salaryid="+salaryid;
		accountingForm.submit();
	}

	function search_gz_data_bycond(salaryid,setobj,tableName)
	{
       var itemid,i;
	   for(i=0;i<setobj.options.length;i++)
	   {
	      if(setobj.options[i].selected)
	      {
	    	itemid=setobj.options[i].value;
	    	break;
	      }   
	   }	
	   if(itemid!="new")
	   {
	   		document.getElementById("empfiltersql").value="";
			accountingForm.action="/gz/gz_accounting/gz_table.do?b_query=link&salaryid="+salaryid;
			accountingForm.submit();    		
	   }	
	   else
	   {
			sql_str=bankdisk_personFilter(salaryid,tableName,1);
			if(sql_str==null||trim(sql_str).length==0)
			{
				bankdisk_changeCondList(salaryid);
			}else
			{
			    accountingForm.cond_id_str.value=cond_id_str;
				document.getElementById("empfiltersql").value=sql_str;
			 	accountingForm.action="/gz/gz_accounting/gz_table.do?b_query=link&salaryid="+salaryid;
				accountingForm.submit(); 
			}
		  		
	   }
	}
function search_gz_spdata_bycond(salaryid,setobj,tableName)
{
   var itemid,i;
   for(i=0;i<setobj.options.length;i++)
   {
      if(setobj.options[i].selected)
      {
    	itemid=setobj.options[i].value;
    	break;
      }   
   }

    if(itemid!="new")
   {
	   	document.getElementById("empfiltersql").value="";
		accountingForm.action="/gz/gz_accounting/gz_sptable.do?b_query=link&salaryid="+salaryid;
		accountingForm.submit();    		
   }	
   else
   {
		sql_str=bankdisk_personFilter(salaryid,tableName,1);
		if(sql_str==null||trim(sql_str).length==0)
		{
			bankdisk_changeCondList(salaryid);
		}else
		{   
		    accountingForm.cond_id_str.value=cond_id_str;
		  	document.getElementById("empfiltersql").value=sql_str;
		 	accountingForm.action="/gz/gz_accounting/gz_sptable.do?b_query=link&salaryid="+salaryid;
			accountingForm.submit(); 	
		}
   }	
}
/**????????????????*/
function setformulavalid(obj,itemid,salaryid)
{
	var flag;
	if(obj.checked)
	  flag="1";
	else
	  flag="0";
    var hashvo=new ParameterSet();
    hashvo.setValue("itemid",itemid);
	hashvo.setValue("salaryid",salaryid);
	hashvo.setValue("flag",flag);	
	hashvo.setValue("batch","0");   
   	var request=new Request({asynchronous:false,functionId:'3020070102'},hashvo); 
}
function batch_set_valid(flag,salaryid)
{
	var name;
	if(flag==1)
		Element.allselect('chk');
	else
		Element.unallselect('chk');
    var hashvo=new ParameterSet();
	hashvo.setValue("salaryid",salaryid);
	hashvo.setValue("flag",flag);	   
	hashvo.setValue("batch","1");   
   	var request=new Request({asynchronous:false,functionId:'3020070102'},hashvo);	
}


/**????????????????*/
function gzspcompute(salaryid,strYm,strC,condid)
{
//	alert(document.getElementsByName("compute")[0])
	

	var itemids=new Array();
	var temps=document.getElementsByName("chk");
	for(var i=0;i<temps.length;i++)
	{
		if(temps[i].checked)
		{
			itemids[itemids.length]=temps[i].value;
		}
	}
	if(itemids.length==0)
	{
		alert("请选择计算公式!");
		return;
	}
	
	var waitInfo=eval("wait");			
	waitInfo.style.display="block";
	
	document.getElementsByName("compute")[0].disabled=true;
	var hashvo=new ParameterSet();
	
	hashvo.setValue("itemids",itemids);
	hashvo.setValue("salaryid",salaryid);
	hashvo.setValue("strYm",strYm);
	hashvo.setValue("strC",strC);
	hashvo.setValue("condid",condid);
	hashvo.setValue("reportSql",document.batchForm.reportSql.value);
	hashvo.setValue("gz_module",document.batchForm.gz_module.value);
   	var request=new Request({method:'post',asynchronous:true,onSuccess:computespIsOk,functionId:'3020070119'},hashvo);
	
}
/**????????????????...*/
function computespIsOk(outparamters)
{
	var waitInfo=eval("wait");			
	waitInfo.style.display="none";
	var salaryid=outparamters.getValue("salaryid");
	var flag=outparamters.getValue("succeed");

	if(flag=="false")
		return;
	var retvo=new Object();	
	retvo.success="1";
	retvo.salaryid=salaryid;
    window.returnValue=retvo;
	window.close();
}








/**????????????????*/
function gzcompute(salaryid)
{
//	alert(document.getElementsByName("compute")[0])
	
	var itemids=new Array();
	var temps=document.getElementsByName("chk");
	for(var i=0;i<temps.length;i++)
	{
		if(temps[i].checked)
		{
			itemids[itemids.length]=temps[i].value;
		}
	}
	if(itemids.length==0)
	{
		alert("请选择计算公式!");
		return;
	}
	
	var waitInfo=eval("wait");			
	waitInfo.style.display="block";
	document.getElementsByName("compute")[0].disabled=true;
	var hashvo=new ParameterSet();
	
	hashvo.setValue("itemids",itemids);
	hashvo.setValue("salaryid",salaryid);
	hashvo.setValue("isPremium",isPremium);
/**		var num = 0;
        timer = setInterval(function(){ 
				var vo=new ParameterSet();
				vo.setValue("keyid","gzcompute");
				var request=new Request({method:'post',asynchronous:true,onSuccess:function(outparamters) {
					var info=outparamters.getValue("info");
					num++;
					if(info=="1"){
						clearInterval(timer);
						var retvo=new Object();	
						retvo.success="1";
						retvo.salaryid=salaryid;
					    window.returnValue=retvo;
						window.close();
					}
					if(num==10){
						clearInterval(timer);
					}
				},functionId:'9999999999'},vo)
      	},1000*60*3);//定时三分钟*/
   	var request=new Request({method:'post',asynchronous:true,onSuccess:computeIsOk,functionId:'3020070103'},hashvo);
	
}

function computeIsOk2(outparamters)
{
	clearInterval(timer);
	var waitInfo=eval("wait");			
	waitInfo.style.display="none";
	var salaryid=outparamters.getValue("salaryid");
	var flag=outparamters.getValue("succeed");

	if(flag=="false")
		return;
	var retvo=new Object();	
	retvo.success="1";
	retvo.salaryid=salaryid;
    window.returnValue=retvo;
	window.close();
}

/**????????????????...*/
function computeIsOk(outparamters)
{
	var waitInfo=eval("wait");			
	waitInfo.style.display="none";
	var salaryid=outparamters.getValue("salaryid");
	var flag=outparamters.getValue("succeed");

	if(flag=="false")
		return;
	var retvo=new Object();	
	retvo.success="1";
	retvo.salaryid=salaryid;
    window.returnValue=retvo;
	window.close();
}
/**????????????*/
function get_formula(salaryid)
{
	if(isEdit())
	{
			alert("数据已被更改，请执行保存操作!");
			return;
	}
	var thecodeurl="/gz/gz_accounting/formulalist.do?b_query=link`salaryid="+salaryid; 
	var iframe_url="/general/query/common/iframe_query.jsp?src="+$URL.encode(thecodeurl);
	if(isIE6() ){
		var retvo= window.showModalDialog(iframe_url, null, 
			        "dialogWidth:430px; dialogHeight:480px;resizable:no;center:yes;scroll:no;status:no");
	}else{
		var retvo= window.showModalDialog(iframe_url, null, 
		        "dialogWidth:410px; dialogHeight:460px;resizable:no;center:yes;scroll:no;status:no");
	}	        
  	 if(retvo==null)
  	 	return ;		        
	if(retvo.success=="1")
	{
	    var salaryid=retvo.salaryid;
		accountingForm.action="/gz/gz_accounting/gz_table.do?b_query=link&salaryid="+salaryid+urlStr;
		accountingForm.submit();  	
	}
}


function get_sp_formula(salaryid)
{
	var strYm=document.accountingForm.bosdate.value;
	var strC=document.accountingForm.count.value;
    var condid=document.accountingForm.condid.value;
	var thecodeurl="/gz/gz_accounting/formulalist.do?b_query=link`condid="+condid+"`strC="+strC+"`strYm="+strYm+"`module=sp`salaryid="+salaryid; 
	var iframe_url="/general/query/common/iframe_query.jsp?src="+$URL.encode(thecodeurl);
	var retvo= window.showModalDialog(iframe_url, null, 
		        "dialogWidth:410px; dialogHeight:460px;resizable:no;center:yes;scroll:no;status:no");	
  	 if(retvo==null)
  	 	return ;		        
	if(retvo.success=="1")
	{
	    var salaryid=retvo.salaryid;
		accountingForm.action="/gz/gz_accounting/gz_sptable.do?b_query=link&salaryid="+salaryid;
	    accountingForm.submit();  	
	}
}



function gzOk()
{

    var itemids=new Array();
	var temps=document.getElementsByName("chk");
	for(var i=0;i<temps.length;i++)
	{
		if(temps[i].checked)
		{
			itemids[itemids.length]=temps[i].value;
		}
	}
	if(itemids.length==0)
	{
		alert("请选择计算公式!");
		return;
	}	
	returnValue=itemids;
	window.close();
}


function run_batch_update(salaryid,whl)
{
	var itemid=$F('itemid');
	var formula=$F('formula');
	if((itemid.toLowerCase()=='a00z0'||itemid.toLowerCase()=='a00z1')&&trim(formula).length==0)
	{
		alert(GZ_ACCOUNTING_INFO12+"!");
		return;
	}
	if(formula.length==0)
	{
	  formula="NULL";
//	  return;
	}
	formula=getEncodeStr(formula);
	var cond=$F('cond');
	//cond=getEncodeStr(cond);
	var hashvo=new ParameterSet();

	hashvo.setValue("salaryid",salaryid);
	hashvo.setValue("itemid",itemid);		
	hashvo.setValue("formula",formula);		
	hashvo.setValue("cond",cond);	
	hashvo.setValue("whl",whl);	
   	var request=new Request({method:'post',asynchronous:true,onSuccess:computeIsOk,functionId:'3020070107'},hashvo);

}


function run_batch_import_history(salaryid,ym,_count)
{
   
	
	var type=$F('importtype');
	var items=$F('chk');
	if(items.length==0)
	{
	  alert("请选择需引入的薪资项目!");
	  return;
	}
	var hashvo=new ParameterSet();

	hashvo.setValue("salaryid",salaryid);
	hashvo.setValue("items",items);		
	hashvo.setValue("importtype",type.toString());	
	hashvo.setValue("ym",ym);
	hashvo.setValue("_count",_count);
	if(type=='4')
	{
		hashvo.setValue("year",document.batchForm.year.value);
		hashvo.setValue("month",document.batchForm.month.value);
		hashvo.setValue("count",document.batchForm.count.value);
	}
	
	var waitInfo=eval("wait");			
	waitInfo.style.display="block";
	var bt=$('import');
	bt.disabled=true;
	
	
  	var request=new Request({method:'post',asynchronous:true,onSuccess:computeIsOk,functionId:'3020070105'},hashvo);

}






function run_batch_import(salaryid)
{
   
	
	var type=$F('importtype');
	var items=$F('chk');
	if(items.length==0)
	{
	  alert("请选择需引入的薪资项目!");
	  return;
	}
	var hashvo=new ParameterSet();

	hashvo.setValue("salaryid",salaryid);
	hashvo.setValue("items",items);		
	hashvo.setValue("importtype",type.toString());	
	
	if(type=='4')
	{
		hashvo.setValue("year",document.batchForm.year.value);
		hashvo.setValue("month",document.batchForm.month.value);
		hashvo.setValue("count",document.batchForm.count.value);
	}
	
	var waitInfo=eval("wait");			
	waitInfo.style.display="block";
	var bt=$('import');
	bt.disabled=true;
	
	
  	var request=new Request({method:'post',asynchronous:true,onSuccess:computeIsOk,functionId:'3020070105'},hashvo);

}



function batch_import_history(salaryid,gz_module)
{
	var ym=document.accountingForm.bosdate.value;
	var count=document.accountingForm.count.value;
	var thecodeurl="/gz/gz_accounting/batchimport.do?b_query=link`opt=sp`ym="+ym+"`count="+count+"`salaryid="+salaryid+"`gz_module="+gz_module; 
	var iframe_url="/general/query/common/iframe_query.jsp?src="+$URL.encode(thecodeurl);
	var retvo= window.showModalDialog(iframe_url, null, 
		         "dialogWidth:530px; dialogHeight:500px;resizable:no;center:yes;scroll:no;status:no");				
  	if(retvo==null)
  	 	return ;		        
	if(retvo.success=="1")
	{
	    var salaryid=retvo.salaryid; 	
		accountingForm.action="/gz/gz_accounting/gz_sptable.do?b_query=link&salaryid="+salaryid;
	    accountingForm.submit();  
	}
}



function batch_import(salaryid,gz_module)
{
	var thecodeurl="/gz/gz_accounting/batchimport.do?b_query=link`salaryid="+salaryid+"`gz_module="+gz_module; 
	var iframe_url="/general/query/common/iframe_query.jsp?src="+$URL.encode(thecodeurl);
	var retvo= window.showModalDialog(iframe_url, null, 
		        "dialogWidth:530px; dialogHeight:500px;resizable:no;center:yes;scroll:no;status:no");			
  	if(retvo==null)
  	 	return ;		        
	if(retvo.success=="1")
	{
	    var salaryid=retvo.salaryid;
		accountingForm.action="/gz/gz_accounting/gz_table.do?b_query=link&salaryid="+salaryid+urlStr;
		accountingForm.submit();  	
	}
}
function allSel(obj)
{
   if(obj.checked)
   {
     batch_set_check(1);
   }
   else
   {
     batch_set_check(0);
   }
}

function set_batch_update(condname,salaryid)
{
	var cond_str=condiTions(document.getElementById(condname).value,salaryid);
	if(cond_str)
     	document.getElementById(condname).value=cond_str;
}
function batch_update(salaryid,gz_module)
{
	var thecodeurl="/gz/gz_accounting/batchupdate.do?b_query=link`gz_module="+gz_module+"`salaryid="+salaryid; 
	var iframe_url="/general/query/common/iframe_query.jsp?src="+$URL.encode(thecodeurl);
	var obj=eval("document.accountingForm.filterWhl");
	
	var retvo= window.showModalDialog(iframe_url, obj.value, 
		        "dialogWidth:420px; dialogHeight:490px;resizable:no;center:yes;scroll:no;status:no");			
  	 if(retvo==null)
  	 	return ;		        
	if(retvo.success=="1")
	{
	    var salaryid=retvo.salaryid;
		accountingForm.action="/gz/gz_accounting/gz_table.do?b_query=link&salaryid="+salaryid+urlStr;
		accountingForm.submit();  	
	}
}
/*
set gz_item state (all,unall)
*/
function batch_set_check(flag)
{
	var name;
	if(flag==1)
		Element.allselect('chk');
	else
		Element.unallselect('chk');
}

function create_gz_table(salaryid)
{
	var thecodeurl="/gz/gz_accounting/input_gz_date.do?b_query=link`salaryid="+salaryid; 
	var iframe_url="/general/query/common/iframe_query.jsp?src="+$URL.encode(thecodeurl);
	//alert(thecodeurl);
	var retvo= window.showModalDialog(iframe_url, null, 
		        "dialogWidth:403px; dialogHeight:150px;resizable:no;center:yes;scroll:no;status:no");			
  	 if(retvo==null||retvo.salaryid==null)
  	 	return ;		        
	if(retvo.success=="1")
	{
	    var salaryid=retvo.salaryid;
	    var ff_bosdate=retvo.ff_bosdate;
		accountingForm.action="/gz/gz_accounting/gz_table.do?b_query=link&ff_bosdate="+ff_bosdate+"&salaryid="+salaryid+urlStr;
		accountingForm.submit();  	
	}
}

/**??????????????*/
function gzTableIsOk(outparamters)
{
	var waitInfo=eval("wait");			
	waitInfo.style.display="none";
	var salaryid=outparamters.getValue("salaryid");
	var ff_bosdate=outparamters.getValue("ff_bosdate");
	var retvo=new Object();	
	retvo.success="1";
	retvo.salaryid=salaryid;
	retvo.ff_bosdate=ff_bosdate;
    window.returnValue=retvo;
	window.close();
}
/**????????????????????*/

function isDigit(s)   
{   
		var patrn=/^[0-9]{1,20}$/;   
		if (!patrn.exec(s)) return false  
			return true  
}  

function submit_gz_table(they,themon,salaryid,gz_module)
{
	var year=they.value;
	var month=themon.value;
	
	if(!isDigit(year)||!isDigit(month))
	{
		alert("日期格式不正确！");
		return;
	}
	if(year*1<=1900||year*1>=2100)
	{
		alert("年要大于1900,小于2100!");
		return;
	}
	if(month*1<1||month*1>12)
	{
		alert("月要大于等于1,小于等于12!");
		return;
	}
	
	var obj=eval("document.accountingForm.finalDate");
	if(trim(obj.value).length>0)
	{
		var temps=obj.value.split("-");
		if(temps[0]*1<year*1)
		{
			if(year*1-temps[0]*1>1||!(temps[1]*1==12&&month*1==1))
			{
				if(!confirm("业务日期超前,在"+temps[0]+"."+temps[1]+"~"+year+"."+month+"之间的业务日期未做业务，您是否确定?"))
				{
						return;
				}
			}
		}
		else if(temps[0]*1==year*1)
		{
			if(month*1-temps[1]*1>1)
			{
				if(!confirm("业务日期超前,在"+temps[0]+"."+temps[1]+"~"+year+"."+month+"之间的业务日期未做业务，您是否确定?"))
				{
						return;
				}
			}
		}
	}
	var waitInfo=eval("wait");
	waitInfo.style.display="block";
				
	var hashvo=new ParameterSet();
	hashvo.setValue("year",year);
	hashvo.setValue("month",month);	
	hashvo.setValue("salaryid",salaryid);	
	hashvo.setValue("gz_module",gz_module);	
   	var request=new Request({method:'post',asynchronous:true,onSuccess:gzTableIsOk,functionId:'3020070005'},hashvo);
	
}

/** 返回数据集的提交方式  */
function submitGzType()
{
	var sets=eval("document.batchForm.setid");
	var types=eval("document.batchForm.type");
	var retvo="";
	for(var i=0;i<sets.length;i++)
	{
		retvo+="#"+sets[i].value+"/"+types[i].value;
	}
	window.returnValue=retvo;
	window.close();
}


function setapp_date()
{
 	var thecodeurl ="/gz/gz_accounting/setapp_date.do?b_query=link"; 
 	if(isIE6() ){
 	    var return_vo= window.showModalDialog(thecodeurl, "", 
              "dialogWidth:430px; dialogHeight:340px;resizable:no;center:yes;scroll:yes;status:no");
 	}else{
 	    var return_vo= window.showModalDialog(thecodeurl, "", 
              "dialogWidth:397px; dialogHeight:340px;resizable:no;center:yes;scroll:yes;status:no");
 	}
}

function reloadIsOk(outparamters)
{
	var waitInfo=eval("wait");			
	waitInfo.style.display="none";
	var salaryid=outparamters.getValue("salaryid");
	var flag=outparamters.getValue("succeed");
	if(flag=="false")
		return;	
	var retvo=new Object();	
	retvo.success="1";
	retvo.salaryid=salaryid;
	retvo.ff_bosdate=outparamters.getValue("ff_bosdate");
    window.returnValue=retvo;
	window.close();
}

function reloadIsOk2(outparamters)
{
	var waitInfo=eval("wait");			
	waitInfo.style.display="none";
	var salaryid=outparamters.getValue("salaryid");
	var gz_module=outparamters.getValue("gz_module");
	var flag=outparamters.getValue("succeed");
	
	var bt=$('ok');
	if(bt)
		bt.disabled=false;
	
	if(flag=="false")
		return;	
	if(flag=="true"&&gz_module!=1)
		alert("提交成功!");	
	if(flag=="true"&&gz_module==1)
		alert("提交成功!");	
			
		
	var retvo=new Object();	
	retvo.success="1";
	retvo.salaryid=salaryid;
    window.returnValue=retvo;
	window.close();
}


/***flag is gz's data submit flag*/
function reloadGzData(they,them,thec,salaryid,flag)
{
	var year=they.value;
	var month=them.value;
	var count=	thec.value;
 /*   if(flag=="false")
    {
    	if(!confirm('数据未提交,重置业务日期后,未提交的数据将丢失!是否继续？'))
    		return;
    }   */		
    
    if(!isDigit(year)||!isDigit(month))
	{
		alert("日期格式不正确！");
		return;
	}
     if(!isDigit(count))
	{
		alert("次数格式不正确！");
		return;
	}	
	var hashvo=new ParameterSet();
	hashvo.setValue("year",year);
	hashvo.setValue("month",month);	
	hashvo.setValue("count",count);		
	hashvo.setValue("salaryid",salaryid);		
   	var request=new Request({method:'post',asynchronous:true,onSuccess:reloadIsOk,functionId:'3020070109'},hashvo);
}


//审批确认判断是否超过总额控制
function validateOverTotalControl2(salaryid,gz_module,bosdate,count)
{
	
	if(subNoShowUpdateFashion=='0')
	{
		var waitInfo=eval("wait");		
		
		document.getElementById("wait_desc").innerHTML="正在提交数据......";	
		waitInfo.style.display="block";
		var bt=$('ok');
		bt.disabled=true;
	}
	if(subNoShowUpdateFashion=='1')
	{
		var waitInfo=eval("wait");	
		document.getElementById("wait_desc").innerHTML="正在提交数据......";		
		waitInfo.style.display="block";
	}
	var hashvo=new ParameterSet();
	hashvo.setValue("salaryid",salaryid);
	hashvo.setValue("gz_module",gz_module); 
	hashvo.setValue("bosdate",bosdate);
	hashvo.setValue("count",count); 
	if(subNoShowUpdateFashion=='0')
		hashvo.setValue("filterWhl",document.batchForm.reportSql.value);
	else
		hashvo.setValue("filterWhl",document.accountingForm.reportSql.value);
	var request=new Request({method:'post',asynchronous:true,onSuccess:totalControl2,functionId:'3020111007'},hashvo);
}
//判断是否超过总额控制
function validateOverTotalControl(salaryid,gz_module)
{
	
	if(subNoShowUpdateFashion!=1)
	{
		var waitInfo=eval("wait");			
		document.getElementById("wait_desc").innerHTML="正在提交数据......";
		waitInfo.style.display="block";
		
		var bt=$('ok');
		bt.disabled=true;
	}
	if(subNoShowUpdateFashion==1)
	{
		
		var waitInfo=eval("wait");	
		document.getElementById("wait_desc").innerHTML="正在提交数据......";		
		waitInfo.style.display="block";
	}
	var hashvo=new ParameterSet();
	hashvo.setValue("salaryid",salaryid);
	hashvo.setValue("gz_module",gz_module); 
	var request=new Request({method:'post',asynchronous:true,onSuccess:totalControl,functionId:'3020070116'},hashvo);
}


function totalControl2(outparamters)
{
	var salaryid=outparamters.getValue("salaryid");
	var gz_module=outparamters.getValue("gz_module");
	var bosdate=outparamters.getValue("bosdate");
	var count=outparamters.getValue("count");
	var info=getDecodeStr(outparamters.getValue("info"));
	if(typeof(salaryid)=='undefined'&&typeof(gz_module)=='undefined'&&typeof(bosdate)=='undefined'){
			setState(false);
			var waitInfo=eval("wait");				
			waitInfo.style.display="none";
			return;
	}
	if(info=='success')
		submit_gz_type2(salaryid,gz_module,bosdate,count);
	else
	{
	//	if(subNoShowUpdateFashion=='0')
		{
			var waitInfo=eval("wait");			
			waitInfo.style.display="none";
		}
		var isOver=outparamters.getValue("isOver"); 
		if(isOver=='0')
		{
			alert(info);
		//	if(subNoShowUpdateFashion=='0')
			{
				var waitInfo=eval("wait");			
				waitInfo.style.display="block";
			}
			submit_gz_type2(salaryid,gz_module,bosdate,count);
		}
		else
		{
		  //  this.getFormHM().put("alertInfo", SafeCode.encode(alertInfo)); 
			var ctrlType=outparamters.getValue("ctrlType");
			if(ctrlType=='1')
			{
	    		alert(info);
	    		return;
	        }else{
	           var alertInfo = getDecodeStr(outparamters.getValue("alertInfo"));
	           if(confirm(alertInfo))
	           {
		      	  {
				    var waitInfo=eval("wait");			
				    waitInfo.style.display="block";
			      }
			      submit_gz_type2(salaryid,gz_module,bosdate,count);
	           }else
	           {
	              return;
	           }
	        }
		}
	
	}
}



function totalControl(outparamters)
{
	var salaryid=outparamters.getValue("salaryid");
	var gz_module=outparamters.getValue("gz_module");
	var info=getDecodeStr(outparamters.getValue("info"));
	
	if(info=='success')
		submit_gz_type(salaryid,gz_module);
	else
	{
	//	if(subNoShowUpdateFashion!='1')
		{
			var waitInfo=eval("wait");			
			waitInfo.style.display="none";
		}
		var isOver=outparamters.getValue("isOver");
		var ctrlType = outparamters.getValue("ctrlType");
		var alertInfo = getDecodeStr(outparamters.getValue("alertInfo"));
		if(isOver=='0')
		{
			if(info.length>0)
				alert(info);
			var waitInfo=eval("wait");			
			waitInfo.style.display="block";
			submit_gz_type(salaryid,gz_module);
		}
		else
		{
		   if(ctrlType=='1')
		   {
			    var bt=document.getElementById("buttonsubmit");
			    if(bt)	
				   bt.disabled=false;
			    alert(info);
		   }else{
		       if(confirm(alertInfo))
		       {
		          var waitInfo=eval("wait");			
			      waitInfo.style.display="block";
		       	  submit_gz_type(salaryid,gz_module);
		       }
		       else
		       {
		       		 
		       		var bt=$('ok');
					if(bt)
						bt.disabled=false;
		          return;
		       }
		   }
		}
	
	}
}


function submitgztype2(salaryid,gz_module,bosdate,count,verify_ctrl)
{
		//if(confirm(GZ_ACCOUNTING_INFO1+"(y/n)"))
		if(verify_ctrl=='1')
		{
		   var a00z1=count;
		   var a00z0=bosdate;
		   if(a00z1==''||a00z0=='')
		   {
		     alert(GZ_SELECT_BOSDATEANDCOUNT+"！");
		     return;
		   }
		   var hashvo=new ParameterSet();
		   hashvo.setValue("a_code","");
		   hashvo.setValue("condid","all");
		   
		   var sql = document.batchForm.reportSql.value;
   		   hashvo.setValue("reportSQL",sql);
		   
		   hashvo.setValue("salaryid",salaryid);
		   hashvo.setValue("type","1");
		   hashvo.setValue("a00z0",count);
		   hashvo.setValue("a00z1",bosdate);
	     
		   hashvo.setValue("gz_module",gz_module);
		   hashvo.setValue("bosdate",bosdate);
		   hashvo.setValue("count",count);
		   var request=new Request({asynchronous:false,onSuccess:check_ok4,functionId:'3020070016'},hashvo);	
		}
		else
		{
			validateOverTotalControl2(salaryid,gz_module,bosdate,count);
		}
}

function check_ok4(outparameters)
{
  var msg=outparameters.getValue("msg");
  var gz_module=outparameters.getValue("gz_module");
  var bosdate=outparameters.getValue("bosdate");
  var count=outparameters.getValue("count");
  var salaryid=outparameters.getValue("salaryid");
  if(msg=='0')
  {
    validateOverTotalControl2(salaryid,gz_module,bosdate,count);
  }
  else if(msg=='no')
  {
     validateOverTotalControl2(salaryid,gz_module,bosdate,count);
  }
  else{
  	 alert("审核不通过，不允许操作!");
     var filename=outparameters.getValue("fileName");
     filename = getDecodeStr(filename);
 	 var win=open("/servlet/vfsservlet?fileid="+filename+"&fromjavafolder=true","excel");
  }
}	


function submit_gz_type2(salaryid,gz_module,bosdate,count)
{
	var hashvo=new ParameterSet();
	if(subNoShowUpdateFashion=='0')
	{
		var setid=$F('setid');
		var type=$F('type');
		
		hashvo.setValue("setid",setid);
		hashvo.setValue("type",type);	
	}
	hashvo.setValue("gz_module",gz_module);
	if(subNoShowUpdateFashion=='0')
	{
		var item_str="";
		for(var i=0;i<itemArray.length;i++)
		{
			item_str+="/"+itemArray[i];
		}
		var type_str="";
		for(var i=0;i<typeArray.length;i++)
		{
			type_str+="/"+typeArray[i];
		}
		hashvo.setValue("items",item_str);
		hashvo.setValue("uptypes",type_str);
		hashvo.setValue("filterWhl",document.batchForm.filterWhl.value);
	}
	else
	{
		hashvo.setValue("filterWhl",document.accountingForm.filterWhl.value);
	}
	hashvo.setValue("subNoShowUpdateFashion",subNoShowUpdateFashion);
	hashvo.setValue("salaryid",salaryid); 
	hashvo.setValue("bosdate",bosdate);
	hashvo.setValue("count",count);
	if(subNoShowUpdateFashion=='0')
   		var request=new Request({method:'post',asynchronous:true,onSuccess:reloadIsOk2,functionId:'3020111006'},hashvo);
   	else
   		var request=new Request({method:'post',asynchronous:true,onSuccess:reloadIsOk4,functionId:'3020111006'},hashvo);
}

function reloadIsOk4(outparamters)
{
	
	var salaryid=outparamters.getValue("salaryid");
	var gz_module=outparamters.getValue("gz_module");
	var flag=outparamters.getValue("succeed");
	
	var waitInfo=eval("wait");			
	waitInfo.style.display="none";
	
	var bt=$('ok');
	if(bt)
		bt.disabled=false;
	
	if(flag=="false")
		alert("提交失败!");	
	if(flag=="true"&&gz_module!=1)
		alert("提交成功!");	
	if(flag=="true"&&gz_module==1)
		alert("提交成功!");	
			
//	var url=document.location.href;
//	document.location=url;	
	accountingForm.action="/gz/gz_accounting/gz_sptable.do?b_query=link&salaryid="+salaryid;
	accountingForm.submit();
}


function submit_gz_type(salaryid,gz_module)
{
	
	var hashvo=new ParameterSet();
	if(subNoShowUpdateFashion=='0')
	{
		var setid=$F('setid');
		var type=$F('type');
		
		hashvo.setValue("setid",setid);
		hashvo.setValue("type",type);	
	}
	hashvo.setValue("gz_module",gz_module);
	
	if(subNoShowUpdateFashion=='0')
	{
		var item_str="";
		for(var i=0;i<itemArray.length;i++)
		{
			item_str+="/"+itemArray[i];
		}
		var type_str="";
		for(var i=0;i<typeArray.length;i++)
		{
			type_str+="/"+typeArray[i];
		}
		hashvo.setValue("items",item_str);
		hashvo.setValue("uptypes",type_str);
		hashvo.setValue("filterWhl",document.batchForm.filterWhl.value);
		hashvo.setValue("isRedo","0");
	}
	else
	{
		hashvo.setValue("isRedo",isRedo);
		hashvo.setValue("filterWhl",document.accountingForm.filterWhl.value);
	}
	
	hashvo.setValue("subNoShowUpdateFashion",subNoShowUpdateFashion);
	hashvo.setValue("salaryid",salaryid); 
	if(subNoShowUpdateFashion=='0')
   		var request=new Request({method:'post',asynchronous:true,onSuccess:reloadIsOk2,functionId:'3020070112'},hashvo);
   	else
   		var request=new Request({method:'post',asynchronous:true,onSuccess:reloadIsOk3,functionId:'3020070112'},hashvo);
}



function reloadIsOk3(outparamters)
{
	
	var salaryid=outparamters.getValue("salaryid");
	var gz_module=outparamters.getValue("gz_module");
	var flag=outparamters.getValue("succeed");
	
	var waitInfo=eval("wait");			
	waitInfo.style.display="none";
	
	if(flag=="false")
		alert("确认失败!");	
	if(flag=="true"&&gz_module!=1)
		alert("确认成功!");	
	if(flag=="true"&&gz_module==1)
		alert("确认成功!");	
		
	accountingForm.action="/gz/gz_accounting/gz_table.do?b_query=link&salaryid="+salaryid;
	accountingForm.submit();		
		
}


function open_submit_dialog2(salaryid,gz_module)
{
	var bosdate=document.accountingForm.bosdate.value;
	var count=document.accountingForm.count.value;
	
	var menuitem=getMenuItem("gz4mitem2");
  	if(menuitem)
  			menuitem.enabled=false;
	
	if(subNoShowUpdateFashion=='0')
	{
	 	var thecodeurl ="/gz/gz_accounting/submit_data.do?b_query=link&count="+count+"&bosdate="+bosdate+"&gz_module="+gz_module+"&salaryid="+salaryid+"&type=2";  	
	    var retvo= window.showModalDialog(thecodeurl,"", 
	              "dialogWidth:500px; dialogHeight:500px;resizable:no;center:yes;scroll:yes;status:no");
	  	 if(retvo==null)
	  	 {
	  	 	var menuitem=getMenuItem("gz4mitem2");
		  	if(menuitem)
		  			menuitem.enabled=true;
	  	 	return ;
	  	 }         
		if(retvo.success=="1")
		{
			//var url=document.location.href;
			//document.location=url;
			accountingForm.action="/gz/gz_accounting/gz_sptable.do?b_query=link&salaryid="+salaryid;
			accountingForm.submit();	
		}	
	}
	else
	{
		
		if(verify_ctrl=='1')
		{
		   var a00z1=count;
		   var a00z0=bosdate;
		   if(a00z1==''||a00z0=='')
		   {
		     alert(GZ_SELECT_BOSDATEANDCOUNT+"！");
		     return;
		   }
		   var hashvo=new ParameterSet();
		   hashvo.setValue("a_code","");
		   hashvo.setValue("condid","all");
		   var sql = accountingForm.reportSql.value;
   		   hashvo.setValue("reportSQL",sql);
		   hashvo.setValue("salaryid",salaryid);
		   hashvo.setValue("type","1");
		   hashvo.setValue("a00z0",count);
		   hashvo.setValue("a00z1",bosdate);
	     
		   hashvo.setValue("gz_module",gz_module);
		   hashvo.setValue("bosdate",bosdate);
		   hashvo.setValue("count",count);
		   hashvo.setValue("subNoShowUpdateFashion",subNoShowUpdateFashion);
		   var request=new Request({asynchronous:false,onSuccess:check_ok4,functionId:'3020070016'},hashvo);	
		}
		else
		{
			validateOverTotalControl2(salaryid,gz_module,bosdate,count);
		}
	
	}              
	
}
//项目合计 JinChunhai
function itemsSum(salaryid)
{
	var thecodeurl ="/gz/gz_accounting/itemsum.do?b_query=link&salaryid="+salaryid;  	
	var retvo= window.showModalDialog(thecodeurl,"","dialogWidth:650px; dialogHeight:500px;resizable:no;center:yes;scroll:yes;status:no");
}
//项目合计  审批
function itemsSum(salaryid,sp)
{
	var thecodeurl ="/gz/gz_accounting/itemsum.do?b_query=link&salaryid="+salaryid+"&sp="+sp;  	
	var retvo= window.showModalDialog(thecodeurl,"","dialogWidth:650px; dialogHeight:500px;resizable:no;center:yes;scroll:yes;status:no");
}
function open_submit_dialog(salaryid,gz_module)
{
	if(isNotSpFlag2Records=='1')
	{
		if(!confirm("有未报审的记录,是否进行薪资确认!"))
			return;
	}

	if(subNoShowUpdateFashion=='0')
	{
		var menuitem=getMenuItem("mitem44");
	  	if(menuitem) 
	  		menuitem.enabled=false;
	  	var bt=document.getElementById("buttonsubmit");
		if(bt)
			bt.disabled=true;	
	  		
	  	 	     
	
	 	var thecodeurl ="/gz/gz_accounting/submit_data.do?b_query=link&gz_module="+gz_module+"&salaryid="+salaryid+"&type=1";  	
	    var retvo= window.showModalDialog(thecodeurl,"", 
	              "dialogWidth:500px; dialogHeight:500px;resizable:no;center:yes;scroll:yes;status:no");
	  	 if(retvo==null)
	  	 {	
	  		if(menuitem)
	  			menuitem.enabled=true;
	  	 	if(bt)
				bt.disabled=false;	  
	  	 	return ;  
	  	 }
		if(retvo.success=="1")
		{
		    	var salaryid=retvo.salaryid;
				accountingForm.action="/gz/gz_accounting/gz_table.do?b_query=link&salaryid="+salaryid+urlStr;
				accountingForm.submit();	
		}	              
	}
	else  //不显示提交方式窗口
	{
	
		var bt=document.getElementById("buttonsubmit");
		if(bt)	
			bt.disabled=true;
				
				
		if(verify_ctrl=='1')  //检查审核公式
		{
			 var hashvo=new ParameterSet();
		     hashvo.setValue("a_code","");
		     hashvo.setValue("condid",accountingForm.condid.value);
		     hashvo.setValue("salaryid",salaryid);
		     hashvo.setValue("type","0");
		     var request=new Request({asynchronous:false,onSuccess:check_verify,functionId:'3020070016'},hashvo);	
		}
		else
		{
			if(isHistory=='1')
			{
				if(confirm(GZ_ACCOUNTING_INFO1+"(y/n)"))
				{
					validateOverTotalControl(salaryid,gz_module);
				}
				else
				{
					var bt=document.getElementById("buttonsubmit");
					if(bt)	
						bt.disabled=false;
				
				}
			}
			else
			{
				validateOverTotalControl(salaryid,gz_module);
			}
		}
	}
}



function check_verify(outparameters)
{
  var msg=outparameters.getValue("msg");
  if(msg=='0'||msg=='no')
  {
     	
	   if(isHistory=='1')
	   {
			if(confirm(GZ_ACCOUNTING_INFO1+"(y/n)"))
			{
				
				validateOverTotalControl(salaryid,gz_module);
			}
			else
			{
				 var bt=document.getElementById("buttonsubmit");
				 if(bt)	
					bt.disabled=false;
			}
	   }
	   else
	   {
	  
			validateOverTotalControl(salaryid,gz_module);
	   }
  }
  else{
  	 var bt=document.getElementById("buttonsubmit");
	 if(bt)	
		bt.disabled=false;
  
  	 alert("审核不通过,不允许操作!");
     var filename=outparameters.getValue("fileName");
	 /* 公式，在薪资表中有不符合审核公式的记录时，点击【提交】按钮，系统会提示审核不通过，并生成审核报告，但是生成的审核报告显示空白，后台报错 xiaoyun start */
     //var name=filename.substring(0,filename.length-1)+".xls";
	 /* 公式，在薪资表中有不符合审核公式的记录时，点击【提交】按钮，系统会提示审核不通过，并生成审核报告，但是生成的审核报告显示空白，后台报错 xiaoyun end */
     filename = getDecodeStr(filename);
 	 var win=open("/servlet/vfsservlet?fileid="+filename+"&fromjavafolder=true","excel");
  }
}



function reset_gz_date(salaryid,flag)
{
	if(flag=="false")
	{
		if(!confirm('数据未提交,重置业务日期后,未提交的数据将丢失!是否继续？'))
			return;
	}
			
	var thecodeurl ="/gz/gz_accounting/reset_gz_date.do?b_query=link&opt=reset"; 
    var retvo= window.showModalDialog(thecodeurl, "", 
	              "dialogWidth:500px; dialogHeight:230px;resizable:no;center:yes;scroll:no;status:no");
	if(retvo==null)
	  	 	return ;  
	  	 	            
	if(retvo.success=="1")
	{
		    var salaryid=retvo.salaryid;
		    var ff_bosdate=retvo.ff_bosdate;
		    var ext_str="";
		    if(ff_bosdate!=null)
		    	ext_str="&ff_bosdate="+ff_bosdate;
			accountingForm.action="/gz/gz_accounting/gz_table.do?b_query=link"+ext_str+"&salaryid="+salaryid+urlStr;
			accountingForm.submit();  	
	}	  
}

/***flag is gz's data submit flag*/
function reDoGz(salaryid,flag)
{
//	if(!confirm('您确定执行重发最后一次操作吗？'))
//    		return;
    isReGz(salaryid);
    var temp=temp_msg;
    if(temp=="1"){
    	alert("您还没有提交过薪资，不需要重发！");
    	return;
    }else if(temp=="2"){
    	alert("薪资业务处于审批环节，不允许重发！请您先处理完本期的薪资，再进行薪资重发！");
    	return;
    }		
    if(flag=="false")
    {
    	if(!confirm('数据未提交,重发后未提交的数据将丢失!是否继续？'))
    		return;
    }   
    var thecodeurl ="/gz/gz_accounting/gz_table.do?b_queryRf=link"; 
    var retvo= window.showModalDialog(thecodeurl, "", 
	              "dialogWidth:500px; dialogHeight:230px;resizable:no;center:yes;scroll:yes;status:no");
	
    if(retvo==null)
	  	 	return ; 
    accountingForm.action="/gz/gz_accounting/gz_table.do?b_query=link&salaryid="+salaryid+urlStr;
	accountingForm.submit(); 	 
}
function isReGz(salaryid){
	var hashvo=new ParameterSet();
	hashvo.setValue("salaryid",salaryid); 
	var request=new Request({method:'post',asynchronous:false,onSuccess:isReGzOk,functionId:'3020070089'},hashvo);

}
function isReGzOk(outparamters){
	temp_msg=outparamters.getValue("msg");
}
//工资重发 
function submit_app_date(salaryid)
{
	var bosdate=document.accountingForm.bosdate.value;
	var count=document.accountingForm.count.value;
	if(bosdate.length==0)
	{
		alert("请选择需重发的业务日期!");
		window.close();
	}
	var hashvo=new ParameterSet();
	hashvo.setValue("salaryid",salaryid); 
	hashvo.setValue("bosdate",bosdate);	
	hashvo.setValue("count",count);	
	var request=new Request({method:'post',asynchronous:true,onSuccess:reExtendIsOk,functionId:'3020070110'},hashvo);
}


function reExtendIsOk(outparamters)
{
	window.returnValue="111";
	window.close();
	/*
	var salaryid=outparamters.getValue("salaryid");
	var flag=outparamters.getValue("succeed");
	if(flag=="false")
		return;	
	accountingForm.action="/gz/gz_accounting/gz_table.do?b_query=link&salaryid="+salaryid+urlStr;
	accountingForm.submit(); 	
	*/
	 
}

function reSetisSuccee(outparamters)
{
	var salaryid=outparamters.getValue("salaryid");
	var flag=outparamters.getValue("succeed");
	if(flag=="false")
		return;
	var retvo=new Object();	
	retvo.success="1";
	retvo.salaryid=salaryid;
	//retvo.ym=outparamters.getValue("ym");
	//retvo.count=outparamters.getValue("count");
    window.returnValue=retvo;
	window.close();	
}

function run_rset_date(year,month,count,salaryid)
{
	var hashvo=new ParameterSet();
	hashvo.setValue("year",year);
	hashvo.setValue("month",month);	
	hashvo.setValue("count",count);	
	hashvo.setValue("salaryid",salaryid);		
  	var request=new Request({method:'post',asynchronous:true,onSuccess:reSetisSuccee,functionId:'3020070109'},hashvo);
}


/************************************************************************************
*BankDisk method begin lizhenwei                                                    *
*************************************************************************************/
var sql_str="";
function bankdisk_bankDisk(a_code,tableName,salaryid)
{
 var condid=accountingForm.condid.value;
 var height=window.screen.height-85;
 var Actual_Version=browserinfo();
 if (Actual_Version!=null&&Actual_Version.length>0&&Actual_Version=='7.0') {
				  	
	   height=height-25;
 }
//window.open("/gz/gz_accountingt/bankdisk/initBankDisk.do?b_init=init&opt=init&code="+a_code+"&tableName="+tableName+"&salaryid="+salaryid+"&condid=all&s="+getEncodeStr(accountingForm.filterWhl.value),
//"_blank","");
var model = document.getElementById("gm").value;
var strurl="/gz/gz_accountingt/bankdisk/initBankDisk.do?b_init=init`model="+model+"`condid="+condid+"`opt=init`o=1`code="+a_code+"`tableName="+tableName+"`salaryid="+salaryid+"`s="+getEncodeStr(accountingForm.filterWhl.value);
 var iframe_url="/gz/gz_accounting/bankdisk/iframe_bank_disk.jsp?src="+$URL.encode(strurl);
var flag=window.showModalDialog(iframe_url,null,"dialogWidth="+window.screen.width+"px;dialogHeight="+window.screen.height+"px;resizable=yes;scroll=no;status=no;");  

}
 function browserinfo(){
        var Browser_Name=navigator.appName;
        var Browser_Version=parseFloat(navigator.appVersion);
        var Browser_Agent=navigator.userAgent;
        
        var Actual_Version;
        var is_IE=(Browser_Name=="Microsoft Internet Explorer");
        if(is_IE){
            var Version_Start=Browser_Agent.indexOf("MSIE");
            var Version_End=Browser_Agent.indexOf(";",Version_Start);
            Actual_Version=Browser_Agent.substring(Version_Start+5,Version_End)
        }
       return Actual_Version;
    }
function bankdisk_add(code,bank_id,tableName,salaryid)
{
	var priv;
		var rd=document.getElementsByName("attributeflag");
			var rd1=document.getElementsByName("attributeflag1");
				if(rd1[0].checked==true){
					priv='0';
				}
				if(rd[0].checked==true){
					priv='1';
				}
				var name=document.getElementsByName("bank_name")[0];
	if(opt!=null&&trim(opt).length!=0&&opt=='add'){
		if(dmlbank_id!=null&&trim(dmlbank_id).length!=0){
			
		}else{
			
			
				if(rd1[0].checked==true){
					priv='0';
				}
				if(rd[0].checked==true){
					priv='1';
				}
				if(validate(priv,name.value,salaryid,code,tableName)){
					 var hashVo=new ParameterSet();
					 hashVo.setValue("salaryid",salaryid);
					 hashVo.setValue("tableName",tableName);
					 hashVo.setValue("code",code);
					 hashVo.setValue("bank_name",getEncodeStr(name.value));
					 hashVo.setValue("priv",priv);
					 var In_parameters="opt=1";
					 var request=new Request({method:'post',asynchronous:false,parameters:In_parameters,onSuccess:bankdisk_addBank_dmlok,functionId:'3020100003'},hashVo);
				}else{
					return;
				}
				
			
		}
		var theURL="/gz/gz_accountingt/bankdisk/selectBankItem.do?b_init=query"+"`bank_id="+dmlbank_id+"`code="+code+"`tableName="+tableName+"`salaryid="+salaryid+"`bankname="+getEncodeStr(name.value);;
	}else{
		
		var theURL="/gz/gz_accountingt/bankdisk/selectBankItem.do?b_init=query"+"`bank_id="+bank_id+"`code="+code+"`tableName="+tableName+"`salaryid="+salaryid+"`priv="+priv+"`bankname="+getEncodeStr(name.value);
	}
	var iframe_url="/gz/gz_accounting/bankdisk/iframe_bank_disk.jsp?src="+$URL.encode(theURL);
	var objlist =window.showModalDialog(iframe_url,null,"dialogWidth=400px;dialogHeight=450px;scroll:no;resizable=yes;status=no;");  
	if(objlist==null)
	return;
	var obj=new Object();
	obj.rightField=objlist.rightField;
	bankDiskForm.rightFields.value=obj.rightField;
	var str=PLEASE_INPUT_BANKNAME;
	bankDiskForm.action="/gz/gz_accountingt/bankdisk/addBankItem.do?b_init=init&opt=fin&inputname="+str+"&priv="+priv+"&bankname="+getEncodeStr(name.value);
	bankDiskForm.submit();

}
	function bankdisk_delTabRows(bank_id){
		if(dmlbank_id==null||trim(dmlbank_id).length==0){
				alert("请先增加银行待发数据标志！");
				return;
			}
		var tab=document.getElementById("dataTable");
		var itemidArray=document.getElementsByName("itemidArray");
		var itemids="";
		for(var i=0;i<itemidArray.length;)
		{
		if(itemidArray[i].checked)
		{
		itemids+=","+itemidArray[i].value;
		tab.deleteRow(i+1);
		}
		else{
		i++;
		}
	}
	if(trim(itemids).length<=0)
	return;
	var hashVo=new ParameterSet();
	hashVo.setValue("bank_id",bank_id);
	 hashVo.setValue("itemids",itemids.substring(1));
	 var In_parameters="opt=1";
	 var request=new Request({method:'post',asynchronous:false,parameters:In_parameters,onSuccess:bankdisk_delete_ok,functionId:'3020100013'},hashVo);			
	}
	function bankdisk_delete_ok(outparameters)
	{
	var str=PLEASE_INPUT_BANKNAME;
	bankDiskForm.action="/gz/gz_accountingt/bankdisk/editBankTemplate.do?b_edit=edit&opt=del&inputname="+str+"&bank_id="+dmlbank_id;
	bankDiskForm.submit();
	}
function bankdisk_addInput(code,bank_id,tableName,salaryid)
{
	if(opt!=null&&trim(opt).length!=0&&opt=='add'){
		if(dmlbank_id!=null&&trim(dmlbank_id).length!=0){
			
		}else{
			var name=document.getElementsByName("bank_name")[0];
			var rd=document.getElementsByName("attributeflag");
			var rd1=document.getElementsByName("attributeflag1");
			var priv;
				if(rd1[0].checked==true){
					priv='0';
				}
				if(rd[0].checked==true){
					priv='1';
				}
				if(validate(priv,name.value,salaryid,code,tableName)){
					 var hashVo=new ParameterSet();
					 hashVo.setValue("salaryid",salaryid);
					 hashVo.setValue("tableName",tableName);
					 hashVo.setValue("code",code);
					 hashVo.setValue("bank_name",getEncodeStr(name.value));
					 hashVo.setValue("priv",priv);
					 var In_parameters="opt=1";
					 var request=new Request({method:'post',asynchronous:false,parameters:In_parameters,onSuccess:bankdisk_addBank_dmlok,functionId:'3020100003'},hashVo);
				}else{
					return;
				}
				
			
		}
	}
	var table = window.document.getElementById("inputTable");
	var n = table.rows[0].cells.length;
	var tab=table.rows[0].insertCell(n);
	tab.innerHTML="<input type='text' name='bankFormat' value='' size='20' class='inputtext'>";
	}
function delInput(){
	var table = window.document.getElementById("inputTable");
	var n = table.rows[0].cells.length;
	if(n==0)
	return;
	table.rows[0].deleteCell(n-1);
}
function bankdisk_sub()
{
	var name=document.getElementsByName("bank_name")[0];
	var rd=document.getElementsByName("attributeflag");
	var rd1=document.getElementsByName("attributeflag1");
	var priv;
	if(rd1[0].checked==true){
			priv='0';
	}
	if(rd[0].checked==true){
		priv='1';
	}
	
	if(opt!=null&&trim(opt).length!=0&&opt=='add'){
		var salaryid=document.getElementsByName("salaryid")[0].value;
		var code=document.getElementsByName("code")[0].value;
		var tableName=document.getElementsByName("tableName")[0].value;
		if(dmlbank_id!=null&&trim(dmlbank_id).length!=0){
			
		}else{
			if(validate(priv,name.value,salaryid,code,tableName)){
					 var hashVo=new ParameterSet();
					 hashVo.setValue("salaryid",salaryid);
					 hashVo.setValue("tableName",tableName);
					 hashVo.setValue("code",code);
					 hashVo.setValue("bank_name",getEncodeStr(name.value));
					 hashVo.setValue("priv",priv);
					 var In_parameters="opt=1";
					 var request=new Request({method:'post',asynchronous:false,parameters:In_parameters,onSuccess:bankdisk_addBank_dmlok,functionId:'3020100003'},hashVo);
				}else{
					return;
				}
		}
	}
	var arr=document.getElementsByName("bankFormat");
	var val="";
	for( var i=0;i<arr.length;i++)
	{
	if(trim(arr[i].value).length>0)
	{
	val+="`"+arr[i].value;
	}
	}
	
	bankDiskForm.bankFormatValue.value=val.substring(1);
	bankDiskForm.action="/gz/gz_accountingt/bankdisk/saveBankTemplate.do?b_save=save"+"&priv="+priv+"&bankname="+getEncodeStr(name.value);
	bankDiskForm.submit();

}
function initBankFormat(obj,format)
{
if(format=='')
  return;
var formatArr=format.split("`");
var tab=document.getElementById(obj);
var n = tab.rows[0].cells.length;
for(var i=0;i<formatArr.length;i++)
{
if(formatArr.length<=n)
{
tab.rows[0].cells[i].innerHTML="<input type='text' name='bankFormat' value='"+formatArr[i]+"' size='20'  class='inputtext'>";
}
else{
if(i<n){
tab.rows[0].cells[i].innerHTML="<input type='text' name='bankFormat' value='"+formatArr[i]+"' size='20' class='inputtext'>";
}
else{
var table=tab.rows[0].insertCell(i);
table.innerHTML="<input type='text' name='bankFormat' value='"+formatArr[i]+"' size='20' class='inputtext'>";
}
}
}
}
function bankdisk_change(str,salaryid,code,tableName)
{
 var bank_id=bankDiskForm.bank_id.value;
 if(bank_id=='#')
 {
    bankdisk_addBank(PLEASE_INPUT_BANKNAME,salaryid,code,tableName);
 }
 else if(bank_id=='*')
 {
     return;
 }
 else
 {
bankDiskForm.action="/gz/gz_accountingt/bankdisk/initBankDisk.do?b_change=change";
bankDiskForm.submit();
}
}
function bankdisk_del(str,bank_id,username){
if(bank_id=='*')
{
  alert("系统还未建立代发银行模板!");
  return;
}
if(bank_id!='*'&&issuper!='true'&&username!=usrName)
{
  alert("不能删除非本人建立的银行报盘!");
  return;
}
var bank="";
var id="";
var banks=bankDiskForm.bank_id.options.length;
for(var i=0;i<banks;i++)
{
if(bankDiskForm.bank_id.options[i].selected)
{
bank=bankDiskForm.bank_id.options[i].text;
id=bankDiskForm.bank_id.options[i].value;
break;
}
}
if(id=='*')
{
    return;
}
if(confirm(str+" ["+bank+"]")){

bankDiskForm.action="/gz/gz_accountingt/bankdisk/deleteBankTemplate.do?b_delete=delete";
bankDiskForm.submit();
}
}
function bankdisk_edit(salaryid,bank_id,code,tableName)
{
if(bank_id=='*')
{
  alert("系统还未建立代发银行模板");
  return;
}
var theURL="/gz/gz_accountingt/bankdisk/editBankTemplate.do?b_edit=edit"+"`bank_id="+bank_id+"`code="+code+"`tableName="+tableName+"`salaryid="+salaryid+"`inputname="+str+"`opt=edit";
var iframe_url="/gz/gz_accounting/bankdisk/iframe_bank_disk.jsp?src="+$URL.encode(theURL);
var objlist =window.showModalDialog(iframe_url,null,"dialogWidth=800px;dialogHeight=550px;resizable=yes;status=no;");
if(objlist==null)
return;
var obj = new Object();
obj.isclose=objlist.isclose;
if(parseInt(obj.isclose)==1)
{
bankDiskForm.action="/gz/gz_accountingt/bankdisk/initBankDisk.do?b_init=init&opt=add&code="+code+"&tableName="+tableName+"&salaryid="+salaryid+"&model="+bankDiskForm.model.value+"&count="+bankDiskForm.boscount.value+"&bosdate="+bankDiskForm.bosdate.value;
bankDiskForm.submit();
 } 
}
function bankdisk_edit1(salaryid,bank_id,code,tableName,username)
{
if(bank_id=='*')
{
  alert("系统还未建立代发银行模板");
  return;
}
if(bank_id!='*'&&issuper!='true'&&username!=usrName)
{
  alert("不能修改非本人建立的银行报盘!");
  return;
}
var str=PLEASE_INPUT_BANKNAME;
var theURL="/gz/gz_accountingt/bankdisk/editBankTemplate.do?b_edit=edit"+"`bank_id="+bank_id+"`code="+code+"`tableName="+tableName+"`salaryid="+salaryid+"`opt=init`inputname="+str;
var iframe_url="/gz/gz_accounting/bankdisk/iframe_bank_disk.jsp?src="+$URL.encode(theURL);
var objlist =window.showModalDialog(iframe_url,null,"dialogWidth=800px;dialogHeight=560px;resizable=yes;status=no;"); // modify by xiaoyun (dialogHeight 550->560)
if(objlist==null)
return;
var obj = new Object();
obj.isclose=objlist.isclose;
if(parseInt(obj.isclose)==1)
{
bankDiskForm.action="/gz/gz_accountingt/bankdisk/initBankDisk.do?b_init=init&opt=add&code="+code+"&tableName="+tableName+"&salaryid="+salaryid+"&model="+bankDiskForm.model.value+"&count="+bankDiskForm.boscount.value+"&bosdate="+bankDiskForm.bosdate.value;
bankDiskForm.submit();
 } 
}
function bankdisk_addBank(str,salaryid,code,tableName){
var o=$("bank_id");
var tabb=document.getElementById("tabb");
//var newName=prompt(str+":","");
//var thecodeurl ="/gz/gz_accountingt/bankdisk/initBankDisk.do?br_get=link&inputname="+str;
   //	var retval= window.showModalDialog(thecodeurl, "", 
  //          "dialogWidth:380px; dialogHeight:150px;resizable:no;center:yes;scroll:yes;status:yes");
  //   var newName;
  //   var priv;
  //  if(retval!=null){
  //  	newName=retval[0];
  //  	priv=retval[1];
  //  }
	var theURL="/gz/gz_accountingt/bankdisk/editBankTemplate.do?b_edit=edit"+"`bank_id=`code="+code+"`tableName="+tableName+"`salaryid="+salaryid+"`inputname="+str+"`opt=add";
	var iframe_url="/gz/gz_accounting/bankdisk/iframe_bank_disk.jsp?src="+$URL.encode(theURL);
	//【6169】薪资管理：银行报盘，新增银行报盘页面问题。   jingq upd 2014.12.22
	var objlist =window.showModalDialog(iframe_url,null,"dialogWidth=800px;dialogHeight=570px;resizable=yes;status=no;");  
	if(objlist==null)
	return;
	var obj = new Object();
	obj.isclose=objlist.isclose;
	var bankid= objlist.bank_id;
	if(parseInt(obj.isclose)==1)
	{
	bankDiskForm.action="/gz/gz_accountingt/bankdisk/initBankDisk.do?b_init=init&opt=add&code="+code+"&tableName="+tableName+"&salaryid="+salaryid+"&model="+bankDiskForm.model.value+"&count="+bankDiskForm.boscount.value+"&bosdate="+bankDiskForm.bosdate.value+"&bank_id="+bankid;
	bankDiskForm.submit();
 } 			
}
 function bankdisk_addBank_ok(outparameters)
 {
     var salaryid=outparameters.getValue("salaryid");
     var bank_id=outparameters.getValue("bank_id");
     var code=outparameters.getValue("code");
     var tableName=outparameters.getValue("tableName");
     bankdisk_edit(salaryid,bank_id,code,tableName);
 }





function bankdisk_disk(salaryid,code,tableName,bank_id,size,str)
{
if(bank_id=='*')
{
alert("系统还未建立代发银行模板");
return;
}
if(parseInt(size)==0)
{
alert(str);
return;
}

var theURL="/gz/gz_accountingt/bankdisk/selectFileType.do?br_select=select";
var popwin= window.showModalDialog(theURL, null, 
        "dialogWidth:300px; dialogHeight:250px;resizable:no;center:yes;scroll:no;status:no");
        if(popwin==null)
        return;
var obj=new Object();
obj.val=popwin.val;
var before  = document.getElementById("before").value;
var ids=document.getElementById("filterSql").value;
 var hashVo=new ParameterSet();
 hashVo.setValue("fileType",obj.val);
 hashVo.setValue("salaryid",salaryid);
 hashVo.setValue("tableName",tableName);
 hashVo.setValue("bank_id",bank_id);
 hashVo.setValue("code",code);
 hashVo.setValue("ids",ids);
 hashVo.setValue("before",before);
 hashVo.setValue("model",bankDiskForm.model.value);
 hashVo.setValue("boscount",bankDiskForm.boscount.value);
 hashVo.setValue("bosdate",bankDiskForm.bosdate.value);
 var In_parameters="opt=1";
 var request=new Request({method:'post',asynchronous:false,parameters:In_parameters,onSuccess:bankdisk_showfile,functionId:'3020100009'},hashVo);			
}
function bankdisk_showfile(outparamters){
var outName=outparamters.getValue("outName");
outName = getDecodeStr(outName);
/* 安全问题 文件下载 薪资发放-银行报盘 xiaoyun 2014-9-23 start */
var fileType=outparamters.getValue("fileType");
if(fileType=="3")
{
var win=open("/servlet/vfsservlet?fileid="+outName+"&fromjavafolder=true","excel");
}
else
{
var win=open("/servlet/vfsservlet?fileid="+outName+"&fromjavafolder=true","txt");
}
/* 安全问题 文件下载 薪资发放-银行报盘 xiaoyun 2014-9-23 end */
}

function bankdisk_choose(str)
{
    var rightFiledIDs="";
	//	var rightFieldNames="";
   var rightFields=$('right_fields')
		if(rightFields.options.length==0)
		{
			 return;
	    	
		}
		for(var i=0;i<rightFields.options.length;i++)
		{
			rightFiledIDs+=","+rightFields.options[i].value;
		}
bankDiskForm.rightFields.value=rightFiledIDs.substring(1);
bankDiskForm.action="/gz/gz_accountingt/bankdisk/personFilter.do?b_query=query&model="+model;
bankDiskForm.submit();
}
function bankdisk_allSelected()
{
var ids = document.getElementsByName("ids");
for(var i=0;i<ids.length;i++)
{
ids[i].checked=true;
}
}
function bankdisk_allCleaned()
{
var ids = document.getElementsByName("ids");
for(var i=0;i<ids.length;i++)
{
ids[i].checked=false;
}
}
function bankdisk_selectOk()
{
var selectedIds="";
var ids = document.getElementsByName("ids");
for(var i=0;i<ids.length;i++)
{
if(ids[i].checked)
{
selectedIds+=","+ids[i].value;
}
}
var obj=new Object();
obj.id=selectedIds.substring(1);
returnValue=obj;
window.close();
}
function bankdisk_selectBankitem()
	{
	    var itemidArray = document.getElementsByName("itemidArray");
	    var ids="";
	    for(var i=0;i<itemidArray.length;i++)
	    {
	       if(itemidArray[i].checked)
	       {
	       ids+=","+itemidArray[i].value;
	       }
	    }
	    if(trim(ids).length<=0)
	    {
	       returnValue=null;
	       window.close();
	    }
		var obj=new Object();
		obj.rightField=ids.substring(1);
		returnValue=obj;
		window.close();
}
		
function bankdisk_selectFileType()
{
var fileT=document.getElementsByName("fileType");
var val="";
for(var i=0;i<fileT.length;i++)
{
if(fileT[i].checked)
{
val=fileT[i].value;
}
}
var obj= new Object();
obj.val=val;
returnValue=obj
window.close();
}


var cond_id_str="";
function bankdisk_filtertype(salaryid,setobj,tableName,model)
{
var condid="";

for(var i=0;i<setobj.length;i++)
{
  if(setobj[i].selected)
  {
  condid=setobj[i].value;
  cond_id_str=setobj[i].value;
  break;
  }
}
if(condid=="new")//????
{
sql_str=bankdisk_personFilter(salaryid,tableName,1);
if(sql_str==null||trim(sql_str).length==0)
{
return;
}
}

if(condid=="all")
{
   sql_str="";
}
if(condid!="new"&&condid!="all"){//?????????sql?
var hashVo=new ParameterSet();
 hashVo.setValue("condid",condid);
 hashVo.setValue("salaryid",salaryid);
 hashVo.setValue("tableName",tableName);
 hashVo.setValue("model",model);
 var In_parameters="opt=1";
 var request=new Request({method:'post',asynchronous:false,parameters:In_parameters,onSuccess:bankdisk_returnSql,functionId:'3020100016'},hashVo);			
}

}
function bankdisk_returnSql(outparameters)
{ 
  
sql_str=outparameters.getValue("sql");
prv_filterCondId=outparameters.getValue("condid");
}
function bankdisk_personFilter(salaryid,tableName,type){
var model=document.getElementById("gm").value;
var theURL="/gz/gz_accountingt/bankdisk/personFilter.do?b_select=select"+"`salaryid="+salaryid+"`tableName="+tableName+"`model="+model;
var iframe_url="/gz/gz_accounting/bankdisk/iframe_bank_disk.jsp?src="+$URL.encode(theURL);
if(isIE6()){
var objlist =window.showModalDialog(iframe_url,null,"dialogWidth=630px;dialogHeight=400px;resizable=no;status=no;;center:yes;scroll:no"); 
}else{
var objlist =window.showModalDialog(iframe_url,null,"dialogWidth=600px;dialogHeight=400px;resizable=no;status=no;;center:yes;scroll:no"); 
}
 
var obj=new Object();
if(objlist==null)
{
obj.sql="";
}else{
  obj.isclose=objlist.isclose;
  obj.sql=objlist.sql;
  obj.condid=objlist.condid;
  cond_id_str=obj.condid;
  if(parseInt(type)==1)
  {
  if(parseInt(obj.isclose)==2)//点取消按钮返回
  {
     bankdisk_changeCondList(salaryid);
  }
  }
}
return obj.sql;
}
function getPersonFilterSql(bank_id,salaryid,tableName,code,size,str)
{
if(bank_id=='*')
{
  alert("系统还未建立代发银行模板");
  return;
}
if(parseInt(size)==0)
{
alert(str);
return;
}

var sql = bankdisk_personFilter(salaryid,tableName,1);
if(sql==null||trim(sql).length==0)
return;
bankDiskForm.filterSql.value=sql;
bankDiskForm.action="/gz/gz_accountingt/bankdisk/initBankDisk.do?b_init=init&opt=add&tableName="+tableName+"&code="+code+"&salaryid="+salaryid+"&bank_id="+bank_id+"&condid="+cond_id_str+"&model="+bankDiskForm.model.value+"&count="+bankDiskForm.boscount.value+"&bosdate="+bankDiskForm.bosdate.value;
bankDiskForm.submit();
}





function bankdisk_filterPersonMethod(bank_id,salaryid,tableName,setobj,code,size,str,model)
{
var tabb=document.getElementById("tabb");
var o=$("filterCondId");
if(bank_id=='*')
{
  alert("系统还未建立代发银行模板");
  for(var i=0;i<o.options.length;i++)
     {
        if(o.options[i].value=='all')
        {
           o.options[i].selected=true;
           tabb.focus();
           break;
        }
     }
  return;
}
if(parseInt(size)==0)
{
alert(NO_DATA_CONTENT);
return;
}



var condid="";

for(var i=0;i<setobj.length;i++)
{
  if(setobj[i].selected)
  {
  condid=setobj[i].value;
  
      //cond_id_str=setobj[i].value;
     
      break;
  }
  }

 bankdisk_filtertype(salaryid,setobj,tableName,model);
 if(trim(sql_str).length==0&&condid!="all")
 {
    
      bankdisk_changeCondList(salaryid);
 }
 else
 {
 
bankDiskForm.filterSql.value=sql_str;
bankDiskForm.action="/gz/gz_accountingt/bankdisk/initBankDisk.do?b_init=init&opt=add&tableName="+tableName+"&code="+code+"&salaryid="+salaryid+"&bank_id="+bank_id+"&condid="+cond_id_str+"&model="+bankDiskForm.model.value+"&count="+bankDiskForm.boscount.value+"&bosdate="+bankDiskForm.bosdate.value;
bankDiskForm.submit();
}
}

/************************************************************
*BankDisk method end lizhenwei                              *
************************************************************/	
		
/************************************************************
*CashList method begin lizhenwei                            *
************************************************************/
function cashlist_cashList(salaryid,priv)
{
     var condid=accountingForm.condid.value;
     var height=window.screen.height-85;
     var Actual_Version=browserinfo();
     if (Actual_Version!=null&&Actual_Version.length>0&&Actual_Version=='7.0') {
				  	
	    height=height-25;
     }
var strurl="/gz/gz_accounting/cash/cash_list_org_tree.do?b_init=init`priv="+priv+"`opt=init`condid="+condid+"`salaryid="+salaryid+"`before="+getEncodeStr(accountingForm.filterWhl.value);
 var iframe_url="/gz/gz_accounting/bankdisk/iframe_bank_disk.jsp?src="+$URL.encode(strurl);
var flag=window.showModalDialog(iframe_url,null,"dialogWidth="+window.screen.width+"px;dialogHeight="+window.screen.height+"px;resizable=yes;scroll=no;status=no;");  
parent.mil_menu.location=parent.mil_menu.location+"?b_opt=1";    
	//window.open("/gz/gz_accounting/cash/cash_list_org_tree.do?b_init=init&priv="+priv+"&opt=init&condid="+condid+"&salaryid="+salaryid+"&before="+getEncodeStr(accountingForm.filterWhl.value),
	//"_blank","height="+window.screen.height+"px,width="+window.screen.width+"px,toolbar=yes,menubar=yes,scrollbars=no, resizable=yes,location=yes, status=yes");
}
function cashlist_configMoney(moneyid)
{
var theURL="/gz/gz_accounting/cash/getMoneyItemList.do?b_query=link`moneyid="+moneyid;
var iframe_url="/gz/gz_accounting/cash/iframe_cash_list.jsp?src="+$URL.encode(theURL);
if(isIE6() ){
var objlist =window.showModalDialog(iframe_url,null,"dialogWidth=395px;dialogHeight=490px;resizable=yes;status=no;");
}else{
var objlist =window.showModalDialog(iframe_url,null,"dialogWidth=355px;dialogHeight=430px;resizable=yes;status=no;");
}

if(objlist==null)
return;
if(objlist.ids==null)
return;
var obj = new Object();
obj.ids=objlist.ids;
cashListForm.moneyitemids.value=obj.ids;
cashListForm.action="/gz/gz_accounting/cash/initCashList.do?b_config=config";
cashListForm.submit();
}
function cashlist_changeItem()
{
cashListForm.action="/gz/gz_accounting/cash/initCashList.do?b_change=change";
cashListForm.submit();
}

function cashlist_exportExcel(code,salaryid,tableName,nmoneyid,itemid)
{
 var hashVo=new ParameterSet();
 var before = document.getElementById("before").value;
 var sql=document.getElementById("filterSql").value;
 hashVo.setValue("salaryid",salaryid);
 hashVo.setValue("tableName",tableName);
 hashVo.setValue("code",code);
 hashVo.setValue("sql",sql);
 hashVo.setValue("nmoneyid",nmoneyid);
 hashVo.setValue("itemid",itemid);
 hashVo.setValue("before",before);
 var In_parameters="opt=1";
 var request=new Request({method:'post',asynchronous:false,parameters:In_parameters,onSuccess:cashlist_showfile,functionId:'3020110005'},hashVo);			
 }
function cashlist_showfile(outparamters){
var outName=outparamters.getValue("outName");
outName = getDecodeStr(outName);
var win=open("/servlet/vfsservlet?fileid="+outName+"&fromjavafolder=true","excel");
}

 function cashlist_selectMoneyitem()
{
   var obj=document.getElementsByName("itemidArray");
   var ids="";
   for(var i=0;i<obj.length;i++)
   {
   if(obj[i].checked)
   {
   ids += "-"+obj[i].value;
   }
   }
  var setobj = new Object();
  setobj.ids=ids.substring(1);
  returnValue=setobj;
  window.close();
}


/*************************************************************
*CashList method end lizhenwei                               *
*************************************************************/

/************************************************************
*Gz EmailTemplate method lizhenwei                          *
************************************************************/
function searchTemplate(salaryid,code,input_type,order_by)
{
     var height=window.screen.height-85;
     var Actual_Version=browserinfo();
     if (Actual_Version!=null&&Actual_Version.length>0&&Actual_Version=='7.0') {
				  	
	    height=height-25;
     }
var strurl="/general/email_template/gz_email_orgtree.do?b_init=init`salaryid="+salaryid+"`order_by="+order_by+"`beforeSql="+getEncodeStr(accountingForm.filterWhl.value)+"`filterId="+accountingForm.condid.value;
 var iframe_url="/gz/gz_accounting/bankdisk/iframe_bank_disk.jsp?src="+$URL.encode(strurl);
var flag=window.showModalDialog(iframe_url,null,"dialogWidth="+window.screen.width+"px;dialogHeight="+window.screen.height+"px;resizable=yes;scroll=no;status=no;");  
      parent.mil_menu.location=parent.mil_menu.location+"?b_opt=1"+urlStr;
     //var theURL="/general/email_template/gz_email_orgtree.do?b_init=init&salaryid="+salaryid+"&order_by="+order_by+"&beforeSql="+getEncodeStr(accountingForm.filterWhl.value)+"&filterId="+accountingForm.condid.value;
     //window.open(theURL,"_blank","height="+window.screen.height+"px,width="+window.screen.width+"px,toolbar=yes,menubar=yes,scrollbars=no, resizable=yes,location=yes, status=yes");
    // window.close();
}
function addEmailTemplate()
{
var theURL="/general/email_template/addEmailTemplate.do?b_init=init`opt=edit`templateId=first`type=0`nmodule=2";
var url="/general/email_template/iframe_gz_email.jsp?src="+theURL;
if(isIE6() ){
	var objlist =window.showModalDialog(url,null,"dialogWidth=800px;dialogHeight=700px;resizable=no;status=no;scroll:no;"); 
}else{
	var objlist =window.showModalDialog(url,null,"dialogWidth=750px;dialogHeight=700px;resizable=no;status=no;scroll:no;"); 
}
}
/*range参数针对IE某些情况定位错误添加，一般情况下使用无需传入，方法会自动区分浏览器获取。*/
function insertTxt(type,str,obj,id,range)
{
   if(str==null)
   {
       return;
   }
   if(parseInt(type)==1)
   {
       var strtxt="$"+id+":"+str+"$";
    }
    if(parseInt(type)==2)
    {
       var strtxt=" "+str+" ";
    }
    if(parseInt(type)==3)
    {
       var strtxt="#"+id+":"+str+"#";
    }
    if(parseInt(type)==4)
    {
       var strtxt=str;
    }
    if(parseInt(type)==5)
    {
       var strtxt='"'+str+'"';
    }
   var expr_editor=$(obj);
   expr_editor.focus();
    var element;
    //52443 如果有传入range对象，直接使用 guodd 2019-09-24
    if(range){
    	range.text=strtxt;
    
    }else if(document.selection){
        element = document.selection;
        if (element!=null) {
            var rge = element.createRange();
            if (rge!=null)
                rge.text=strtxt;
        }
    }else{
        //插入公式 浏览器兼容  wangbs 20190320
        element = window.getSelection().toString();
        var start = expr_editor.selectionStart;
        var end = expr_editor.selectionEnd;
        expr_editor.value = expr_editor.value.substring(0,start)+strtxt+expr_editor.value.substring(end,expr_editor.value.length);
        expr_editor.setSelectionRange(start+strtxt.length,start+strtxt.length);
    }
}

function changeFieldSet(){
	var v = gzEmailForm.formulafieldsetid.value;
  	var hashvo=new ParameterSet(); 	
    hashvo.setValue("fieldsetid",v);
    hashvo.setValue("nmodule",gzEmailForm.nmodule.value);
   	var In_paramters="flag=1"; 	
    var request=new Request({method:'post',asynchronous:false,
		     parameters:In_paramters,onSuccess:resultChangeFieldSet,functionId:'0202030005'},hashvo);					

    
  }
  function resultChangeFieldSet(outparamters){
  	var fielditemlist=outparamters.getValue("itemlist");
  	var obj=document.getElementById("t");
  	if(obj!=null)
  	 {
  	     obj.style.display="block";
  	 }
	AjaxBind.bind(gzEmailForm.itemid,fielditemlist);
  }
  
  
  function changeFormulaFieldSet(){
	var v = gzEmailForm.formulafieldsetid.value;
	var nm=gzEmailForm.nmodule.value;
  	var hashvo=new ParameterSet(); 	
    hashvo.setValue("formulafieldsetid",v);
    hashvo.setValue("nmodule",nm);
   	var In_paramters="flag=1"; 	
    var request=new Request({method:'post',asynchronous:false,
		     parameters:In_paramters,onSuccess:resultFormulaChangeFieldSet,functionId:'0202030006'},hashvo);					

    
  }
   function resultFormulaChangeFieldSet(outparamters){
  	var fielditemlist=outparamters.getValue("itemlist");
  	var obj=document.getElementById("t");
  	if(obj!=null)
  	 {
	   //兼容非ie浏览器样式  wangbs 20190320
  	     obj.style.display="";
  	 }
	AjaxBind.bind(gzEmailForm.itemid,fielditemlist);
  }
  
  
   function IsDigit() 
        { 
           return ((event.keyCode >= 46) && (event.keyCode <= 57)); 
        } 
 function IsInputValue(textid,type) {	     
		event.cancelBubble = true;
		var fObj=document.getElementById(textid);		
		if(fObj.disabled==true)
		  return false;		
		if (!fObj) return;
				
		var cmd =event.srcElement.innerText=="5"?true:false;
		var i = parseInt(fObj.value,10);
		if(parseInt(type)==1)//Integer
		{
	       var radix = 15;		
		   if(i==radix&&cmd)
		   {
			   i=radix;
		   }
		   else if(!cmd&&i==1) 
		   {
		       i=1;
		   }
		   else
		   {
			   cmd?i++:i--;
		   }
	    }else if(parseInt(type)==2)//Decimal
	    {
	            cmd?i++:i--;
	    }
	    else if(parseInt(type)==3)//char
	    {
	       if(!cmd&&i==1)
	          i=1;
	       else
	         cmd?i++:i--;
	         
	    }		
		fObj.value = i;
		fObj.select();
} 
function email_search_cond(templateid)
{
  if(templateid==null||trim(templateid).length<=0)
  {
      alert("系统未定义邮件模板");
      return;
  }
var id=gzEmailForm.queryvalue.value;
   gzEmailForm.action="/general/email_template/gz_send_email.do?b_search=query&timeid="+id;
   gzEmailForm.submit();
}

function email_delete_person(id)
{
 if(id=="")
 {
     alert("请选择邮件模板！");
     return;
 }
   var obj=document.getElementsByName("selectpersonid");
   if(!obj)
   {
         return;
   }
   var select="";
   var num=0;
   for(var i=0;i<obj.length;i++)
   { 
      if(obj[i].checked)
      {
          select+="`"+obj[i].value;
          num++;
      }
   }
   if(num==0)
   { 
      alert("请选择要删除的记录");
      return;
   }
   if(ifdel())
   {
	   jinduo('sclsjl');
      gzEmailForm.selectid.value=select.substring(1);
      gzEmailForm.action="/general/email_template/gz_send_email.do?b_delete=delete";
      gzEmailForm.submit();
   }
   else
   {
      return;
   }
   
}

function gzEmail_filterPersonMethod(salaryid,setobj,tableName)
{
   var condid="";

   for(var i=0;i<setobj.length;i++)
   {
      if(setobj[i].selected)
      {
        condid=setobj[i].value;
        break;
      }
  }
   
   bankdisk_filtertype(salaryid,setobj,tableName,"0");
   if(trim(sql_str).length==0&&condid!="all")
   {
    
      gzEmail_changeCondList(salaryid);
   }
   else
   {
      gzEmailForm.beforeSql.value=sql_str;
      var id="";
      var obj=document.getElementById("t");
      for(var i=0;i<obj.options.length;i++)
      {
         if(obj.options[i].selected)
         {
           id=obj.options[i].value;
           break;
         }
      }
     gzEmailForm.action="/general/email_template/gz_send_email.do?b_query=query&timeid="+id+"&filterId="+cond_id_str;
     gzEmailForm.submit();
   }
}

/************************************************************
*Gz EmailTemplate method lizhenwei end                      *
************************************************************/

/****************   dengcan start  ************************/

function importTable(salaryid)
{
	document.accountingForm.action="/gz/gz_accounting/in_out.do?br_import=init&salaryid="+salaryid+urlStr;
  	document.accountingForm.submit();
}

/** 工资审批（多级上报） */
function appeal(userid,opt,salaryid,gz_module)
{
		
		
		var tablename="tableSalaryHistory";
        table=$(tablename);
        dataset=table.getDataset();
	    var record=dataset.getFirstRecord();
	    var selectID="";
	    if(opt=='appeal')
	    {
		    var num=0;	
		    var noNum=0;
			while (record) 
			{
				if (record.getValue("select"))
				{							
							if(record.getValue("sp_flag")=='02'||record.getValue("sp_flag")=='07')
							{
								num++;
							     selectID+="#"+record.getValue("a0100")+"/"+record.getValue("nbase")+"/"+record.getValue("a00z0")+"/"+record.getValue("a00z1")+"/"+record.getValue("userflag");
							}
							else
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
				alert("没有选择可报批的记录！");
				return;
			}
		}
		else if(opt=='appealAll')
		{
			if(!confirm('您确定要报批当前筛选条件下已报批或驳回的记录？'))
			{
				return;
			}
		}
		/*
		var bt=document.getElementById("buttonappeal1");
		if(bt)
			bt.disabled=true;
		
		bt=document.getElementById("buttonappeal2");
		if(bt)
			bt.disabled=true;
		*/
		setState(true);	
			
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
			
	//	var obj=eval("appeal2");
	//	obj.disabled=true;
		
		var hashvo=new ParameterSet();
		
		hashvo.setValue("salaryid",salaryid);
		hashvo.setValue("gz_module",gz_module); 
	
		hashvo.setValue("bosdate",accountingForm.bosdate.value);
		hashvo.setValue("count",accountingForm.count.value); 
		hashvo.setValue("filterWhl",document.accountingForm.reportSql.value);
		hashvo.setValue("opt",opt); 
		hashvo.setValue("userid",userid); 
		hashvo.setValue("selectID",selectID); 
		
		
		
		
		
		
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
		
		if(isTotalControl=='1'||verify_ctrl=='1')
		{
					var waitInfo=eval("wait");	
					waitInfo.style.display="none";
				
		}	
		
		
	//	var returnValue= select_user_dialog("1",2,"0",salaryid);
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
	   			var iframe_url="/general/query/common/iframe_query.jsp?src="+$URL.encode(strurl);
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
				 
					setState0(false);
	    			return;
	    		}
			}
		}
		else  
			returnValue= select_user_dialog("1",2,"0",salaryid); 
		if(returnValue)
		{		
		    if(trim(returnValue.content).length==0)
			{
				alert("请选择审批用户!");
				/*
				var bt=document.getElementById("buttonappeal1");
				if(bt)
					bt.disabled=false;
				bt=document.getElementById("buttonappeal2");
				if(bt)
					bt.disabled=false;
				*/
				setState(false);
				
				return;
			}	
			if(userid==returnValue.content)
			{
				alert("不能指定本人为审批对象！");
				/*
				var bt=document.getElementById("buttonappeal1");
				if(bt)
					bt.disabled=false;
				bt=document.getElementById("buttonappeal2");
				if(bt)
					bt.disabled=false;
				*/
				setState(false);
				
				return;
			}
			getName(returnValue.content)
			if(!confirm("确定要报批给"+temp_fullname+"?"))//赵旭光  add 搜房网 报批前确认  2013-10-23
			{
				setState0(false);
		  		return;
			}
			if(opt=='appeal')
				document.accountingForm.selectGzRecords.value=selectID.substring(1);
				
			var waitInfo=eval("wait");	
			document.getElementById("wait_desc").innerHTML="正在进行 报批,请稍候...";
			waitInfo.style.display="block";
				
			document.accountingForm.action="/gz/gz_accounting/gz_sptable.do?b_appeal=appeal&approveObject="+returnValue.content+"&opt="+opt;
			document.accountingForm.submit();
			
		}
		else
		{
		/*
			var bt=document.getElementById("buttonappeal1");
			if(bt)
				bt.disabled=false;
			bt=document.getElementById("buttonappeal2");
			if(bt)
				bt.disabled=false;
		*/
			setState(false);
		}
		
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
  		 alert("审核不通过，不允许操作!");
    }
    if(opt=='confirm'||opt=='confirmAll')
  		 alert("审核不通过，不允许操作!");
  	if(opt=='appeal'||opt=='appealAll')
  	{	 
	  
		setState(false);
	}
	if(opt=='confirm'||opt=='confirmAll')
	{
	
		setState(false);
		
	
	}
     var filename=outparameters.getValue("fileName");
     filename = getDecodeStr(filename);
     var win=open("/servlet/vfsservlet?fileid="+filename+"&fromjavafolder=true","excel");
  }
}	


function confirmSalary(selectID,operate,userid)
{
			var arguments=new Array();
			if(operate=='confirm'||operate=='confirmAll') 
			{
				arguments[0]="同意，审批通过。";
				arguments[1]="批准意见";  
			//	if(operate=='confirmAll')
				{	
					arguments[2]="confirmAll";
					arguments[3]=isSendMessage;
				}  
		    }
		   
		    
		    var strurl="/gz/gz_accounting/gz_sptable.do?b_confirm=confirm`operate="+operate+"`selectID="+getEncodeStr(selectID);
		    var iframe_url="/general/query/common/iframe_query.jsp?src="+$URL.encode(strurl);
			var ss=window.showModalDialog(iframe_url,arguments,"dialogWidth=460px;dialogHeight=350px;resizable=yes;scroll=no;status=no;");  
		    if(ss)
		    {
			    document.accountingForm.selectGzRecords.value=selectID.substring(1);
			    document.accountingForm.rejectCause.value=ss[0];
			    if((operate=='confirm'||operate=='confirmAll')&&isSendMessage=='1')
			    	document.accountingForm.sendMen.value=ss[1];
			    document.accountingForm.approveObject.value=userid;
				document.accountingForm.action="/gz/gz_accounting/gz_sptable.do?b_appeal=appeal&opt="+operate;
				document.accountingForm.submit();
			}
			else
			{
				if(operate=='confirm'||operate=='confirmAll') 
				{
				/*
					var bt=document.getElementById("buttontable");
					if(bt)
						bt.disabled=true;
					
					bt=document.getElementById("buttontable2");
					if(bt)
						bt.disabled=true;
				*/
					//setState(true);
					setState(false);
					var waitInfo=eval("wait");				
					waitInfo.style.display="none";
							
				
				}
			}
}

	
		
function appealSptotalControl(outparameters)
{

		var selectID=outparameters.getValue("selectID");
		var opt=outparameters.getValue("opt");
		var userid=outparameters.getValue("userid");
		var info=getDecodeStr(outparameters.getValue("info"));
			if(typeof(selectID)=='undefined'&&typeof(opt)=='undefined'&&typeof(userid)=='undefined'){
			setState(false);
			var waitInfo=eval("wait");				
			waitInfo.style.display="none";
			return;
			}
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
			    var ctrlType = outparameters.getValue("ctrlType");
			    var alertInfo =getDecodeStr(outparameters.getValue("alertInfo")); 
			    if(ctrlType=='1')
			    {
		    		if(isTotalControl=='1'||verify_ctrl=='1')
			    	{
			      		var waitInfo=eval("wait");	
			    		waitInfo.style.display="none";
				
			    	}	
			    	alert(info);
			     	setState(false);
			    }else
			    {
			        if(confirm(alertInfo))
			        {
			            if(verify_ctrl=='1')
			    	    {
			     	     	verifyFormula(selectID,opt,userid)
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
			         	setState(false);
			        }
			    }
			
			}
		
		}
}


function setState(_boolean)
{
		var bt=document.getElementById("buttontable");
		if(bt)
				bt.disabled=_boolean;
		bt=document.getElementById("buttontable2");
		if(bt)
				bt.disabled=_boolean;
		bt=document.getElementById("buttonreject");
		if(bt)
				bt.disabled=_boolean;
				
		bt=document.getElementById("buttonrejectAll");
		if(bt)
				bt.disabled=_boolean;
		bt=document.getElementById("buttonappeal1");
		if(bt)
			bt.disabled=_boolean;
		bt=document.getElementById("buttonappeal2");
		if(bt)
			bt.disabled=_boolean;
			
			
		bt=document.getElementById("buttondelselected");  //删除
		if(bt)
			bt.disabled=_boolean;
			
		bt=document.getElementById("buttonsavedata"); //保存
		if(bt)
			bt.disabled=_boolean;
		bt=document.getElementById("buttoncompute");  //计算
		if(bt)
			bt.disabled=_boolean;
			
		bt=document.getElementById("buttonsh_formula"); //审核
		if(bt)
			bt.disabled=_boolean;
		
		
		var menuitem=getMenuItem("m0");
  		if(menuitem)
  			menuitem.enabled=!_boolean;
		menuitem=getMenuItem("m1");
  		if(menuitem)
  			menuitem.enabled=!_boolean;	
  		menuitem=getMenuItem("m2");
  		if(menuitem)
  			menuitem.enabled=!_boolean;	
  		menuitem=getMenuItem("m3");
  		if(menuitem)
  			menuitem.enabled=!_boolean;	
  		menuitem=getMenuItem("m4");
  		if(menuitem)
  			menuitem.enabled=!_boolean;	
  		menuitem=getMenuItem("gz4mitem2");
  		if(menuitem)
  			menuitem.enabled=!_boolean;	
	
		if(!_boolean)
		{
			var waitInfo=eval("wait");	
			if(waitInfo)		
				waitInfo.style.display="none";
		}

}



/** 工资审批（批准 驳回） */
function optSalary(operate,userid)
{
		
		var tablename="tableSalaryHistory";
        table=$(tablename);
        dataset=table.getDataset();
	    var record=dataset.getFirstRecord();
	    var selectID="";	
	    var num=0;	
	    var noNum=0;
		var desc="";
		if(operate=='confirm'||operate=='confirmAll')
			desc="批准"
		else if(operate=='reject'||operate=='rejectAll')
			desc="驳回";
			
		if(operate!='confirmAll'&&operate!='rejectAll')
		{	
			while (record) 
			{
				if (record.getValue("select"))
				{							
							
							if(record.getValue("sp_flag")=='02'||record.getValue("sp_flag")=='07')
							{
								num++;
							     selectID+="#"+record.getValue("a0100")+"/"+record.getValue("nbase")+"/"+record.getValue("a00z0")+"/"+record.getValue("a00z1")+"/"+record.getValue("userflag");
							}
							else
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
				alert("没有选择可"+desc+"的记录！");
				return;
			
			}
		}
		else
		{
			if(operate=='confirmAll')
			{
				if(!confirm('您确定要批准本月本次当前筛选条件下的报批记录？'))
				{
					return;
				}
			}
			if(operate=='rejectAll')
			{
				if(!confirm('您确定要驳回本月本次当前筛选条件下的报批记录？'))
				{
					return;
				}
			}
		}
		
		if(operate=='confirm'||operate=='confirmAll')
		{
			var hashvo=new ParameterSet();
			hashvo.setValue("salaryid",salaryid);
			hashvo.setValue("gz_module",gz_module); 
			hashvo.setValue("bosdate",accountingForm.bosdate.value);
			hashvo.setValue("count",accountingForm.count.value); 
			hashvo.setValue("opt",operate); 
			hashvo.setValue("userid",userid); 
			hashvo.setValue("selectID",selectID); 
			hashvo.setValue("filterWhl",document.accountingForm.reportSql.value);
			
			/*
			var bt=document.getElementById("buttontable");
			if(bt)
				bt.disabled=true;
			
			bt=document.getElementById("buttontable2");
			if(bt)
				bt.disabled=true;
			*/
			setState(true);
			
			var waitInfo=eval("wait");	
			document.getElementById("wait_desc").innerHTML="正在进行批准操作......";
			waitInfo.style.display="block";
			
			
			var request=new Request({method:'post',asynchronous:true,onSuccess:confirmSptotalControl,functionId:'3020111007'},hashvo);
		}
		else
		{
			/*
			var bt=document.getElementById("buttonreject");
			if(bt)
					bt.disabled=true;
				
			bt=document.getElementById("buttonrejectAll");
			if(bt)
					bt.disabled=true;
			*/
			setState(true);
			
			var arguments=new Array();
		   	arguments[0]="";
			arguments[1]="驳回原因";  
		    var strurl="/gz/gz_accounting/rejectCause.jsp";
		    var iframe_url="/general/query/common/iframe_query.jsp?src="+$URL.encode(strurl);
			var ss=window.showModalDialog(iframe_url,arguments,"dialogWidth=460px;dialogHeight=350px;resizable=yes;scroll=no;status=no;");  
		    if(ss)
		    {
			    document.accountingForm.selectGzRecords.value=selectID.substring(1);
			    document.accountingForm.rejectCause.value=ss[0];
			//  if(operate=='confirmAll'&&isSendMessage=='1')
			//    	document.accountingForm.sendMen.value=ss[1];
			    	
			  
			    
			    document.accountingForm.approveObject.value=userid;
				document.accountingForm.action="/gz/gz_accounting/gz_sptable.do?b_appeal=appeal&opt="+operate;
				document.accountingForm.submit();
			}
			else
			{
			/*	var bt=document.getElementById("buttonreject");
				if(bt)
						bt.disabled=false;
					
				bt=document.getElementById("buttonrejectAll");
				if(bt)
						bt.disabled=false;
						*/
					setState(false);
			}
		
		}
		
		/*
		
		if(verify_ctrl=='1'&&(operate=='confirm'||operate=='confirmAll'))
		{
				 verifyFormula(selectID,operate,userid)
	    }
		else
		{
			var arguments=new Array();
			if(operate=='confirm'||operate=='confirmAll') 
			{
				arguments[0]="同意，审批通过。";
				arguments[1]="批准意见";  
				if(operate=='confirmAll')
				{	
					arguments[2]="confirmAll";
					arguments[3]=isSendMessage;
				}  
		    }
		    else
		    {
		   		arguments[0]="";
				arguments[1]="驳回原因";  
		    }
		    if(operate=='appealAll')
		   		 arguments[2]="appealAll";  
		    
		    var strurl="/gz/gz_accounting/rejectCause.jsp";
		    var iframe_url="/general/query/common/iframe_query.jsp?src="+strurl; 
			var ss=window.showModalDialog(iframe_url,arguments,"dialogWidth=460px;dialogHeight=350px;resizable=yes;scroll=no;status=no;");  
		    if(ss)
		    {
			    document.accountingForm.selectGzRecords.value=selectID.substring(1);
			    document.accountingForm.rejectCause.value=ss[0];
			    if(operate=='confirmAll'&&isSendMessage=='1')
			    	document.accountingForm.sendMen.value=ss[1];
			    
			    document.accountingForm.approveObject.value=userid;
				document.accountingForm.action="/gz/gz_accounting/gz_sptable.do?b_appeal=appeal&opt="+operate;
				document.accountingForm.submit();
			}
		
		}
		*/
		
}



function confirmSptotalControl(outparameters)
{

		var selectID=outparameters.getValue("selectID");
		var operate=outparameters.getValue("opt");
		var userid=outparameters.getValue("userid");
		var info=getDecodeStr(outparameters.getValue("info"));
		if(typeof(selectID)=='undefined'&&typeof(operate)=='undefined'&&typeof(userid)=='undefined'){
			setState(false);
			var waitInfo=eval("wait");				
			waitInfo.style.display="none";
			return;
		}
		if(info=='success'||info=='')
		{
			if(verify_ctrl=='1'&&(operate=='confirm'||operate=='confirmAll'))
			{
				  verifyFormula(selectID,operate,userid)
			}
			else
			{
				var arguments=new Array();
				if(operate=='confirm'||operate=='confirmAll') 
				{
					arguments[0]="同意，审批通过。";
					arguments[1]="批准意见";  
			//		if(operate=='confirmAll')
					{	
						arguments[2]="confirmAll";
						arguments[3]=isSendMessage;
					}  
			    }
			    else
			    {
			   		arguments[0]="";
					arguments[1]="驳回原因";  
			    }
			    if(operate=='appealAll')
			   		 arguments[2]="appealAll";  
			    
			    var strurl="/gz/gz_accounting/gz_sptable.do?b_confirm=confirm`operate="+operate+"`selectID="+getEncodeStr(selectID);
			    var iframe_url="/general/query/common/iframe_query.jsp?src="+$URL.encode(strurl);
				var ss=window.showModalDialog(iframe_url,arguments,"dialogWidth=460px;dialogHeight=350px;resizable=yes;scroll=no;status=no;");  
			    if(ss)
			    {
				    document.accountingForm.selectGzRecords.value=selectID.substring(1);
				    document.accountingForm.rejectCause.value=ss[0];
			 	    if((operate=='confirm'||operate=='confirmAll')&&isSendMessage=='1')
				    	document.accountingForm.sendMen.value=ss[1];
				    
				    document.accountingForm.approveObject.value=userid;
					document.accountingForm.action="/gz/gz_accounting/gz_sptable.do?b_appeal=appeal&opt="+operate;
					document.accountingForm.submit();
				}
				else
				{
						/*	var bt=document.getElementById("buttontable");
							if(bt)
								bt.disabled=false;
							
							bt=document.getElementById("buttontable2");
							if(bt)
								bt.disabled=false;*/
							setState(false);
							if(operate=='confirm'||operate=='confirmAll')
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
			var ctrlType = outparameters.getValue("ctrlType");
			
			var flagn=0;
			if(isOver=='1'&&ctrlType!='1')
			{
				  var alertInfo=getDecodeStr(outparameters.getValue("alertInfo"));
		          if(confirm(alertInfo))
		          {
				    isOver='0';
				    flagn=1;
				  }
				  else
				  {
				 	setState(false);
				    return;
				  }
			}
			
			
			
			
			if(isOver=='0')
			{
				if(flagn==0)
					alert(info);
				if(verify_ctrl=='1'&&(operate=='confirm'||operate=='confirmAll'))
				{
					  verifyFormula(selectID,operate,userid)
				}
				else
				{
					var arguments=new Array();
					if(operate=='confirm'||operate=='confirmAll') 
					{
						arguments[0]="同意，审批通过。";
						arguments[1]="批准意见";  
				//		if(operate=='confirmAll')
						{	
							arguments[2]="confirmAll";
							arguments[3]=isSendMessage;
						}  
				    }
				    else
				    {
				   		arguments[0]="";
						arguments[1]="驳回原因";  
				    }
				    if(operate=='appealAll')
				   		 arguments[2]="appealAll";  
				    
				    var strurl="/gz/gz_accounting/gz_sptable.do?b_confirm=confirm`operate="+operate+"`selectID="+getEncodeStr(selectID);
				    var iframe_url="/general/query/common/iframe_query.jsp?src="+$URL.encode(strurl);
					var ss=window.showModalDialog(iframe_url,arguments,"dialogWidth=460px;dialogHeight=350px;resizable=yes;scroll=no;status=no;");  
				    if(ss)
				    {
					    document.accountingForm.selectGzRecords.value=selectID.substring(1);
					    document.accountingForm.rejectCause.value=ss[0];
					    if((operate=='confirm'||operate=='confirmAll')&&isSendMessage=='1')
					    	document.accountingForm.sendMen.value=ss[1];
					    
					    document.accountingForm.approveObject.value=userid;
						document.accountingForm.action="/gz/gz_accounting/gz_sptable.do?b_appeal=appeal&opt="+operate;
						document.accountingForm.submit();
					}
					else
					{
					/*
						if(operate=='confirm'||operate=='confirmAll') 
						{
							var bt=document.getElementById("buttontable");
							if(bt)
								bt.disabled=false;
							
							bt=document.getElementById("buttontable2");
							if(bt)
								bt.disabled=false;
					
						}
						*/
						setState(false);
					}
				
				}
			}
			else
			{
				alert(info);
				/*
				var bt=document.getElementById("buttontable");
				if(bt)
					bt.disabled=false;
				
				bt=document.getElementById("buttontable2");
				if(bt)
					bt.disabled=false;
					*/
				setState(false);
			}
		
		}
}





/** （工资审核）提交 */
function sub(salaryid)
{
		var tablename="tableSalaryHistory";
        table=$(tablename);
        dataset=table.getDataset();  
		
	//	alert("如果工资数据已被改动，请先执行保存操作!")
		if(confirm("如果数据已被更改，请先保存，否则修改的数据可能会丢失！是否继续？"))
		{
			var selectID="";	
		    var num=0;	
		    var nonum=0;
			var record=dataset.getFirstRecord();
			while (record) 
			{
				
				if (record.getValue("select"))
				{							
							
							if(record.getValue("sp_flag")=='06')
							{
							    // selectID=selectID+"#"+record.getValue("a0100")+"/"+record.getValue("nbase");
							     selectID+="#"+record.getValue("a0100")+"/"+record.getValue("nbase")+"/"+record.getValue("a00z0")+"/"+record.getValue("a00z1");
							     num++;
							}
							else
								nonum++;
				}
				record=record.getNextRecord();
			}  	
			if(num==0||nonum>0)
			{
				alert("请选择审批结束的记录!");
				return;
			}
			
		//	alert(selectID);
		//	return;
			var gzRecordVo=eval("document.accountingForm.selectGzRecords");
			gzRecordVo.value=selectID.substring(1);
			var bosdate=document.accountingForm.bosdate.value;
	        var count=document.accountingForm.count.value;
	
	/*
			var thecodeurl ="/gz/gz_accounting/submit_data.do?b_query=link&count="+count+"&bosdate="+bosdate+"&salaryid="+salaryid;  	
		    var retvo= window.showModalDialog(thecodeurl,"", 
		              "dialogWidth:500px; dialogHeight:500px;resizable:no;center:yes;scroll:yes;status:yes");
		  	if(retvo==null)
		  	 	return ;   
		  	 	
	*/
		  	document.accountingForm.action="/gz/gz_accounting/gz_sptable.do?b_sub=sub";
			document.accountingForm.submit();
		}
}

/*****************dengcan end*****************************************/


/*****************fengxibin start*****************************************/

function syncgzspemp(salaryid,bosdate)
{
	var hashvo=new ParameterSet();
	hashvo.setValue("salaryid",salaryid);
	hashvo.setValue("bosdate",bosdate);
	var request=new Request({method:'post',asynchronous:true,onSuccess:show_gz_sptable,functionId:'3020111009'},hashvo);
}
function show_gz_sptable(outparameters)
{
    var salaryid=outparameters.getValue("salaryid");
	accountingForm.action="/gz/gz_accounting/gz_sptable.do?b_query=link&salaryid="+salaryid;
	accountingForm.submit();  
}
function syncgzemp(salaryid)
{
	var hashvo=new ParameterSet();
	hashvo.setValue("salaryid",salaryid);
	var request=new Request({method:'post',asynchronous:true,onSuccess:show_gz_table,functionId:'3020070303'},hashvo);
}
function show_gz_table(outparameters)
{
    var salaryid=outparameters.getValue("salaryid");
    document.getElementById("order_by").value=outparameters.getValue("order_by");
	accountingForm.action="/gz/gz_accounting/gz_table.do?b_query=link&salaryid="+salaryid+urlStr;
	accountingForm.submit();  
}
function to_project_filter(salaryid)
	{
		//var thecodeurl ="/gz/gz_accounting/gzprofilter.do?b_query=link&salaryid="+salaryid;
    	//var return_vo= window.showModalDialog(thecodeurl, "", 
           //   "dialogWidth:480px; dialogHeight:400px;resizable:no;center:yes;scroll:yes;status:yes");
        //if(return_vo!=null)
   		//{
   			
	   	//	document.getElementById("proright_str").value=return_vo[0];
	   	//	if(return_vo[1].length>0&&return_vo[2].length>0)
	   	//	{
	   			//var obj=document.getElementById("itemid");
	   		//	obj.options[obj.options.length-1].value=return_vo[1];   
	         //   obj.options[obj.options.length-1].text=return_vo[2];  
	   		//	obj.options[obj.options.length-1].selected=true;
	   	//	}
		//	accountingForm.action="/gz/gz_accounting/gz_table.do?b_query=link&salaryid="+salaryid;
  	    //	accountingForm.submit();
   		//}else
   		//{
   		//	changeItemidList(salaryid);
   		//}
   		var thecodeurl ="/gz/gz_accounting/gzprofilter.do?b_delete=link`salaryid="+salaryid; 
		var iframe_url="/general/query/common/iframe_query.jsp?src="+$URL.encode(thecodeurl);
    	var vo= window.showModalDialog(iframe_url, "", 
              "dialogWidth:450px; dialogHeight:400px;resizable:no;center:yes;scroll:no;status:no");
        if(vo)
        {
            changeItemidList(salaryid,'0');
            var obj=document.getElementById("projectFilter");
            for(var i=0;i<obj.options.length;i++)
            {
               if(obj.options[i].value==vo)
               {
                    obj.options[i].selected=true;
               }
            }
           // accountingForm.itemid.value=vo;
            accountingForm.action="/gz/gz_accounting/gz_table.do?b_query=link&salaryid="+salaryid+urlStr;
  	    	accountingForm.submit();
        }
        else
        {
           changeItemidList(salaryid,'0');
        }      
	}
function to_person_filter(salaryid,tableName)
	{
		sql_str=bankdisk_personFilter(salaryid,tableName,1);
			if(sql_str==null||trim(sql_str).length==0)
			return;
			accountingForm.cond_id_str.value=cond_id_str;
		  	document.getElementById("empfiltersql").value=sql_str;
		 	accountingForm.action="/gz/gz_accounting/gz_table.do?b_query=link&salaryid="+salaryid+urlStr;
			accountingForm.submit(); 	
	}
function to_sort_emp(salaryid)
	{
		var thecodeurl ="/gz/sort/sorting.do?b_query=link&salaryid="+salaryid+"&flag=0";
		var return_vo= window.showModalDialog(thecodeurl, "", 
              "dialogWidth:510px; dialogHeight:470px;resizable:no;center:yes;scroll:yes;status:no");
				
    	if(return_vo!=null){
			return_vo=return_vo!='not'?return_vo:"";
			document.getElementById("sort_table_detail").value=return_vo;
			accountingForm.action="/gz/gz_accounting/gz_table.do?b_query=link&salaryid="+salaryid;
  	    	accountingForm.submit();
		}
	}
	function to_sort_emp1(salaryid)
	{
		var thecodeurl ="/gz/sort/sorting.do?b_query=link&salaryid="+salaryid+"&flag=xuj&xuj=xuj";
		var return_vo= window.showModalDialog(thecodeurl, "", 
              "dialogWidth:555px; dialogHeight:380px;resizable:no;center:yes;scroll:yes;status:no");// modify by xiaoyun 510-		
    	if(return_vo!=null){
    		return_vo=return_vo!='not'?return_vo:"";
    		var hashvo=new ParameterSet();
			hashvo.setValue("return_vo",return_vo);
			hashvo.setValue("salaryid",salaryid);
    		var request=new Request({method:'post',asynchronous:false,onSuccess:tempsort,functionId:'3020071040'},hashvo);
		}
	}
	function tempsort(outparamters){
			var return_vo=outparamters.getValue("return_vo");
			var salaryid=outparamters.getValue("salaryid");
			//alert(return_vo+"  "+salaryid);
			document.getElementById("sort_table_detail").value=return_vo;
			accountingForm.action="/gz/gz_accounting/gz_table.do?b_query=link&salaryid="+salaryid+urlStr;
  	    	accountingForm.submit();
	}
	//zgd 2015-1-21 add 薪资审批增加人员排序 start
	function to_sort_emp2(salaryid){
		var thecodeurl ="/gz/sort/sorting.do?b_query=link&salaryid="+salaryid+"&flag=xuj&xuj=xuj";
		var return_vo= window.showModalDialog(thecodeurl, "", 
              "dialogWidth:555px; dialogHeight:380px;resizable:no;center:yes;scroll:yes;status:no");	
    	if(return_vo!=null){
    		return_vo=return_vo!='not'?return_vo:"";
    		var hashvo=new ParameterSet();
			hashvo.setValue("return_vo",return_vo);
			hashvo.setValue("salaryid",salaryid);
    		var request=new Request({method:'post',asynchronous:false,onSuccess:tempsort2,functionId:'3020071040'},hashvo);
		}
	}
	function tempsort2(outparamters){
			var return_vo=outparamters.getValue("return_vo");
			var salaryid=outparamters.getValue("salaryid");
			//alert(return_vo+"  "+salaryid);
			document.getElementById("sort_table_approval").value=return_vo;
			accountingForm.action="/gz/gz_accounting/gz_sptable.do?b_query=link&salaryid="+salaryid;
  	    	accountingForm.submit();
	}
	//zgd 2015-1-21 add 薪资审批增加人员排序 end
function to_gz_person_filter(salaryid,tableName)
	{
		sql_str=bankdisk_personFilter(salaryid,tableName,1);
		if(sql_str==null||trim(sql_str).length==0)
		{
		    bankdisk_changeCondList(salaryid);
		}
		else
		{
		accountingForm.cond_id_str.value=cond_id_str;
	  	document.getElementById("empfiltersql").value=sql_str;
	 	accountingForm.action="/gz/gz_accounting/gz_table.do?b_query=link&salaryid="+salaryid+urlStr;
		accountingForm.submit(); 	
		}
	}	
function to_gzsp_person_filter(salaryid,tableName)
	{
		sql_str=bankdisk_personFilter(salaryid,tableName,1);
		if(sql_str==null||trim(sql_str).length==0)
		{
		    bankdisk_changeCondList(salaryid);
		}
		else
		{
		accountingForm.cond_id_str.value=cond_id_str;
	  	document.getElementById("empfiltersql").value=sql_str;
	 	accountingForm.action="/gz/gz_accounting/gz_sptable.do?b_query=link&salaryid="+salaryid;
		accountingForm.submit(); 
		}	
	}
function show_tax_mx(salaryid)
{
	accountingForm.action="/gz/gz_accounting/tax/gz_tax_org_tree.do?b_query=link&is_back=back&salaryid="+salaryid+urlStr;
	accountingForm.target="il_body";
	accountingForm.submit(); 	
	
}
function return_gz(salaryid)
{
	taxTableForm.action="/gz/gz_accounting/gz_org_tree.do?b_query=link&salaryid="+salaryid+urlParams;
	taxTableForm.target="il_body";
	taxTableForm.submit(); 	
}
function to_querycondition()
	{
		var theURL="/gz/gz_accounting/tax/querycondition.do?b_query=link";
		if(isIE6() ){
				var return_vo =window.showModalDialog(theURL,"",
			"dialogWidth=480px;dialogHeight=420px;resizable=yes;scroll:yes;center:yes;status=no;");  
		}else{
				var return_vo =window.showModalDialog(theURL,"",
			"dialogWidth=450px;dialogHeight=420px;resizable=yes;scroll:yes;center:yes;status=no;");  
		}
		if(return_vo!=null){
			if(return_vo!="cancel")
			{
				document.getElementById("condtionsql").value = return_vo;
				taxTableForm.action="/gz/gz_accounting/tax/search_tax_table.do?b_query=link&init=search"+urlParams;
	   			taxTableForm.submit();  
			}
		}
		
	}	
var gsetname;
function new_record(tablename,a_code)
{
	gsetname=tablename
	var hashvo=new ParameterSet();
	hashvo.setValue("tablename",tablename);
	hashvo.setValue("a_code",a_code);	
	var request=new Request({method:'post',asynchronous:false,onSuccess:setNewRecord,functionId:'3020092101'},hashvo);
	
}	
function setNewRecord(outparamters)
{
	var tablename,table,dataset,preno,bmainset;
    tablename="table"+gsetname;
    table=$(tablename);
    dataset=table.getDataset();
    record=dataset.getCurrent();
	
	dataset.insertRecord("before");	
    record=dataset.getCurrent();
    record.setValue("declare_tax",outparamters.getValue("declare_tax")); 
	record.setValue("tax_date",outparamters.getValue("tax_date")); 
    record.setValue("tax_max_id",outparamters.getValue("tax_max_id"));
    record.setValue("B0110",outparamters.getValue("b0110"));
    record.setValue("E0122",outparamters.getValue("e0122"));
}	
/*****************fengxibin end*****************************************/

/* 手工引入人员 */
function hand_importMen(tablename,nbase,salaryid,isSalaryManager)
{
	var infos=new Array();
	infos[0]=tablename;
	infos[1]=nbase;
	infos[2]=isSalaryManager;
	var strurl="/gz/gz_accounting/gz_table.do?br_handImportMen=query";
	var iframe_url="/gz/gz_accounting/bankdisk/iframe_bank_disk.jsp?src="+$URL.encode(strurl);
	var flag=window.showModalDialog(iframe_url,infos,"dialogWidth=700px;dialogHeight=450px;resizable=yes;scroll=no;status=no;");  
	if(flag=="1")
	{
		accountingForm.action="/gz/gz_accounting/gz_table.do?b_query=link&salaryid="+salaryid+urlStr;
		accountingForm.submit();
	}
}

/*  引入单位\部门变动人员 */
function importChangeMen(salaryid,isSalaryManager)
{
	
	var hashvo=new ParameterSet();
	hashvo.setValue("salaryid",salaryid);
	hashvo.setValue("isSalaryManager",isSalaryManager);
	var request=new Request({method:'post',asynchronous:true,onSuccess:importChangeMen2,functionId:'3020071014'},hashvo);
}

//设置单位部门变动子集
function setGzParam(salaryid)
{
	
	var infos="";
	var strurl="/gz/gz_accounting/importMen.do?b_queryParam=query`salaryid="+salaryid;
	var iframe_url="/gz/gz_accounting/bankdisk/iframe_bank_disk.jsp?src="+$URL.encode(strurl);
	var flag=window.showModalDialog(iframe_url,infos,"dialogWidth=390px;dialogHeight=370px;resizable=yes;scroll=no;status=no;");  
	if(flag)
	{
    	if(flag=="1")
    	{
    		document.importPersonnelForm.action="/gz/gz_accounting/importMen.do?b_query=query&salaryid="+salaryid;
	    	document.importPersonnelForm.submit();
     	}	
     }	
}

/** 设置单位部门变动子集/取得指标html */
function getItemHtml()
{
	document.importPersonnelForm.action="/gz/gz_accounting/importMen.do?b_changeset=set";
  	document.importPersonnelForm.submit();

}
/*  引入单位\部门变动人员 */
function importChangeMen2(outparamters)
{
	var salaryid=outparamters.getValue("salaryid");
	var isExist=outparamters.getValue("isExist");
	var isSalaryManager=outparamters.getValue("isSalaryManager");
	var infos="";
	if(isExist=='1')
	{
		var strurl="/gz/gz_accounting/importMen.do?b_query=query`salaryid="+salaryid+"`isSalaryManager="+isSalaryManager;
		var iframe_url="/gz/gz_accounting/bankdisk/iframe_bank_disk.jsp?src="+$URL.encode(strurl);
		var flag=window.showModalDialog(iframe_url,infos,"dialogWidth=790px;dialogHeight=480px;resizable=yes;scroll=no;status=no;");  
		if(flag=="1")
		{
			accountingForm.action="/gz/gz_accounting/gz_table.do?b_query=link&salaryid="+salaryid+urlStr;
			accountingForm.submit();
		}
	
	}
	else
	{
	   var infos="";
		var strurl="/gz/gz_accounting/importMen.do?b_queryParam=query`salaryid="+salaryid+"`isSalaryManager="+isSalaryManager;
		var iframe_url="/gz/gz_accounting/bankdisk/iframe_bank_disk.jsp?src="+$URL.encode(strurl);
		var flag=window.showModalDialog(iframe_url,infos,"dialogWidth=390px;dialogHeight=370px;resizable=yes;scroll=no;status=no;");  
		if(flag=="1")
		{
			var strurl="/gz/gz_accounting/importMen.do?b_query=query`salaryid="+salaryid+"`isSalaryManager="+isSalaryManager;
			var iframe_url="/gz/gz_accounting/bankdisk/iframe_bank_disk.jsp?src="+$URL.encode(strurl);
			var flag=window.showModalDialog(iframe_url,infos,"dialogWidth=850px;dialogHeight=480px;resizable=yes;scroll=no;status=no;");  
			if(flag=="1")
			{
				accountingForm.action="/gz/gz_accounting/gz_table.do?b_query=link&salaryid="+salaryid+urlStr;
				accountingForm.submit();
			}
	
		}
	}

}

function lockcolumn(setname)
{
	var tablename,table;
	tablename="table"+setname;
    table=$(tablename);
    var col=table.getActiveCellIndex();
    table.lockColumn(table,col);
}
//导出摸板
function downLoadTemp(salaryid)
{	
	var hashvo=new ParameterSet();	
	hashvo.setValue("salaryid",salaryid);
	hashvo.setValue("itemid",$F('projectFilter'));
	hashvo.setValue("proright_str",accountingForm.proright_str.value);  
	hashvo.setValue("sqlStr",getEncodeStr(accountingForm.sql.value));
	var request=new Request({method:'post',asynchronous:false,onSuccess:showfile1,functionId:'3020071020'},hashvo);
}
function showfile1(outparamters)
{
	var outName=outparamters.getValue("outName");
	outName=getDecodeStr(outName);
	window.location.target="_blank";
	window.location.href = "/servlet/vfsservlet?fileid="+outName+"&fromjavafolder=true";
}
//导入模板数据
function exportTempData(salaryid,oper)
{
	document.accountingForm.action="/gz/gz_accounting/in_out.do?br_import=init&salaryid="+salaryid+"&importTempl=1&oper="+oper;
  	document.accountingForm.submit();
}

function searchdata1()
{
	historyDataForm.action='/gz/gz_analyse/historydata/browse.do?b_query=link&a_code='+a_code;
	historyDataForm.submit();
}

/**
 * 薪资历史分析切换项目过滤
 * @param salaryid
 * @param setobj
 */
function history_projectFilter(salaryid,setobj) {

    var itemid,i;
    for(i=0;i<setobj.options.length;i++)
    {
        if(setobj.options[i].selected)
        {
            itemid=setobj.options[i].value;
            break;
        }
    }
    // for(i=0;i<salary_id.length;i++){
     //    salaryid+=salaryids[i]+",";
	// }

    if(itemid!="new")
    {
        document.getElementById("empfiltersql").value="";
        historyDataForm.action='/gz/gz_analyse/historydata/browse.do?b_query=link&a_code='+a_code;
        historyDataForm.submit();
    }else {

        var thecodeurl = "/gz/gz_accounting/gzprofilter.do?b_query=link&opt=2&model=history&salaryid=" + salaryid;
        var return_vo = window.showModalDialog(thecodeurl, "",
            "dialogWidth:480px; dialogHeight:400px;resizable:no;center:yes;scroll:yes;status:no");
        if (return_vo != null) {
            document.getElementById("proright_str").value = return_vo[0];
            var obj = document.getElementById("itemid");
            obj.options[obj.options.length - 1].value = return_vo[1];
            obj.options[obj.options.length - 1].text = getDecodeStr(return_vo[2]);
            obj.options[obj.options.length - 1].selected = true;
            historyDataForm.action = '/gz/gz_analyse/historydata/browse.do?b_query=link&a_code=' + a_code;
            historyDataForm.submit();
        } else {
            changeItemidList_history(salaryid, "history");
        }
    }
}


/**
 * 薪资历史分析切换项目过滤 点击退出时
 * @param salaryid
 * @param model
 */
function changeItemidList_history(salaryid,model)
{
    var hashVo=new ParameterSet();
    hashVo.setValue("salaryid",salaryid);
    hashVo.setValue("model",model);
    var In_parameters="opt=1";
    var request=new Request({method:'post',asynchronous:false,parameters:In_parameters,onSuccess:change_itemlist_ok_history,functionId:'3020070203'},hashVo);

}

/**
 * 重新加载下拉列表
 * @param outparameters
 */
function change_itemlist_ok_history(outparameters)
{
    var filterList = outparameters.getValue("itemfilterlist");
    var model=outparameters.getValue("model");
    var salaryid=outparameters.getValue("salaryid");
    AjaxBind.bind(historyDataForm.itemid,filterList);
    var obj=$("itemid");
    for(var i=0;i<obj.options.length;i++)
    {
        obj.options[i].text=getDecodeStr(obj.options[i].text);
    }
    if(obj.options.length==2)
    {
        historyDataForm.action='/gz/gz_analyse/historydata/browse.do?b_query=link&a_code='+a_code;
        historyDataForm.submit();
    }
    else
    {

        for(var i=0;i<obj.options.length;i++)
        {
            if(obj.options[i].value==prv_project_id)
            {
                obj.options[i].selected=true;
                return;
            }
        }
        historyDataForm.action='/gz/gz_analyse/historydata/browse.do?b_query=link&a_code='+a_code;
        historyDataForm.submit();
    }
}

/**
 * 薪资历史分析 点击项目过滤按钮
 * @param salaryid
 */
function to_project_filter_history(salaryid)
{
    var thecodeurl ="/gz/gz_accounting/gzprofilter.do?b_delete=link`model=history`salaryid="+salaryid;
    var iframe_url="/general/query/common/iframe_query.jsp?src="+$URL.encode(thecodeurl);
    var vo= window.showModalDialog(iframe_url, "",
        "dialogWidth:450px; dialogHeight:400px;resizable:no;center:yes;scroll:no;status:no");
    if(vo)
    {
        changeItemidList_history(salaryid,'history');
        var obj=document.getElementById("projectFilter");
        for(var i=0;i<obj.options.length;i++)
        {
            if(obj.options[i].value==vo)
            {
                obj.options[i].selected=true;
            }
        }
        // accountingForm.itemid.value=vo;
        historyDataForm.action='/gz/gz_analyse/historydata/browse.do?b_query=link&a_code='+a_code;
        historyDataForm.submit();
    }
    else
    {
        changeItemidList_history(salaryid,'history');
    }
}

/**
 * 薪资历史分析 人员过滤
 * @param salaryid
 * @param setobj
 */
function search_gz_data_bycond_history(salaryid,setobj) {
    var itemid = setobj, i, tableName = "salaryarchive";
    if (itemid != 'new') {
        for (i = 0; i < setobj.options.length; i++) {
            if (setobj.options[i].selected) {
                itemid = setobj.options[i].value;
                break;
            }
        }
    }
    if(itemid!="new")
    {
        historyDataForm.cond_id_str.value=itemid;
        document.getElementById("empfiltersql").value="";
        historyDataForm.action='/gz/gz_analyse/historydata/browse.do?b_query=link&a_code='+a_code;
        historyDataForm.submit();
    }
    else
    {
        sql_str=history_personFilter(salaryid,tableName,1);

        if(sql_str==null||trim(sql_str).length==0)
        {
            history_changeCondList(salaryid);
        }else
        {
            historyDataForm.cond_id_str.value=cond_id_str;
            document.getElementById("empfiltersql").value=sql_str;
            historyDataForm.action='/gz/gz_analyse/historydata/browse.do?b_query=link&a_code='+a_code;
            historyDataForm.submit();
        }

    }
}

function history_personFilter(salaryid,tableName,type) {
    var model ="history";

    var theURL = "/gz/gz_accountingt/bankdisk/personFilter.do?b_select=select" + "`salaryid=" + salaryid + "`tableName=" + tableName + "`model=" + model;
    var iframe_url = "/gz/gz_accounting/bankdisk/iframe_bank_disk.jsp?src=" +  $URL.encode(theURL);
    var objlist = window.showModalDialog(iframe_url, null, "dialogWidth=600px;dialogHeight=400px;resizable=yes;status=no;");
    var obj = new Object();
    if (objlist == null) {
        obj.sql = "";
    } else {
        obj.isclose = objlist.isclose;
        obj.sql = objlist.sql;
        obj.condid = objlist.condid;
        cond_id_str = obj.condid;
        if (parseInt(type) == 1) {
            if (parseInt(obj.isclose) == 2)//点取消按钮返回
            {
                history_changeCondList(salaryid);
            }
        }
    }
    return obj.sql;
}

/**
 * 薪资历史分析 获取下拉列表数据
 * @param salaryid
 */
function history_changeCondList(salaryid)
{
    var hashVo=new ParameterSet();
    hashVo.setValue("isclose","2");
    hashVo.setValue("salaryid",salaryid);
    hashVo.setValue("model","history");
    //var In_parameters="opt=1";
    var request=new Request({method:'post',asynchronous:false,onSuccess:change_condlist_ok_history,functionId:'3020100017'},hashVo);
}

/**
 * 薪资历史分析 重新加载人员过滤下拉列表
 * @param outparameters
 */
function change_condlist_ok_history(outparameters)
{
    var filterList = outparameters.getValue("filterCondList");
    AjaxBind.bind(historyDataForm.condid,filterList);
    var obj=$("condid");

    if(obj.options.length==2)
    {
        historyDataForm.action='/gz/gz_analyse/historydata/browse.do?b_query=link&a_code='+a_code;
        historyDataForm.submit();
    }
    else
    {
        for(var i=0;i<obj.options.length;i++)
        {
            if(obj.options[i].value==prv_filter_id)
            {
                obj.options[i].selected=true;
                return;
            }
        }
        historyDataForm.action='/gz/gz_analyse/historydata/browse.do?b_query=link&a_code='+a_code;
        historyDataForm.submit();
    }
}



//薪资归档导出Excel
function exportGDData()
{	
	var hashvo=new ParameterSet();	
	hashvo.setValue("fieldStr",getEncodeStr(historyDataForm.fieldStr.value));
	hashvo.setValue("sqlStr",getEncodeStr(historyDataForm.sql.value));
	var request=new Request({method:'post',asynchronous:false,onSuccess:showfile1,functionId:'3020071028'},hashvo);
}
//引入奖金
function importBonus()
{	
	accountingForm.action='/gz/gz_accounting/gz_table.do?b_importBonus=link&a_code='+a_code+urlStr;
	accountingForm.submit();
}
//引入计件
function importPiece()
{	if(confirm("确定要引入计件薪资吗？")){
		accountingForm.action='/gz/gz_accounting/gz_table.do?b_importPiece=link&a_code='+a_code+urlStr;
		accountingForm.submit();
   }

}
function delete_cond(salaryid)
{
   var theUrl="/gz/gz_accounting/bankdisk/delete_filter_cond.do?b_query=query`model="+model+"`salaryid="+salaryid;
   var url="/gz/gz_accounting/bankdisk/iframe_bank_disk.jsp?src="+$URL.encode(theUrl);
   var objlist =window.showModalDialog(url,null,"dialogWidth=320px;dialogHeight=400px;resizable=yes;status=no;");  
   if(objlist==null)
   {
      return;
   }
   var obj= new Object();
   obj.ids=objlist.ids;
   obj.type=objlist.type;
   if(parseInt(obj.type)==2)//open
   {
       bankDiskForm.action="/gz/gz_accounting/bankdisk/delete_filter_cond.do?b_open=open&model="+model+"&salaryid="+salaryid+"&condid="+obj.ids;
       bankDiskForm.submit();
   }			
 }
 function filterSort(salaryid)
{
   var thecodeurl ="/gz/gz_accounting/bankdisk/delete_filter_cond.do?b_query=query`salaryid="+salaryid; 
   var iframe_url="/general/query/common/iframe_query.jsp?src="+$URL.encode(thecodeurl);
   var vo= window.showModalDialog(iframe_url, "", 
              "dialogWidth:300px; dialogHeight:360px;resizable:no;center:yes;scroll:yes;status:no");
   if(vo!=null&&vo=='1')
   {
      bankDiskForm.action="/gz/gz_accounting/bankdisk/delete_filter_cond.do?b_query=query&salaryid="+salaryid;
      bankDiskForm.tagert="_self";
      bankDiskForm.submit();
   }
}
/******xieguiquan start ******/
function showstate(){
	var thecodeurl ="/gz/gz_accounting/gz_showapprove.do?b_query=link";
 				
    var return_vo= window.showModalDialog(thecodeurl, "", 
              "dialogWidth:360px; dialogHeight:350px;resizable:no;center:yes;scroll:yes;status:no");
   

}
function showprocess(frommodel,_tablename)
{
	var tablename="table"+_tablename;
	    table=$(tablename);
	    dataset=table.getDataset();
		 var record2= dataset.getCurrent();
		 if(record2==null||record2.getValue("a0100")==null)
		 return;
		 var selectID=salaryid+"/"+record2.getValue("a0100")+"/"+record2.getValue("nbase")+"/"+record2.getValue("a00z0")+"/"+record2.getValue("a00z1");
		 var appprocess =getEncodeStr(selectID);
		 var thecodeurl ="/gz/gz_accounting/gz_showapprove.do?b_search=link&frommodel="+frommodel+"&appprocess="+appprocess;
		   var return_vo= window.showModalDialog(thecodeurl, "", 
              "dialogWidth:360px; dialogHeight:350px;resizable:no;center:yes;scroll:yes;status:no");
 				
    
}
/******xieguiquan end ******/
/******dml start*********/
function validate(priv,newName,salaryid,code,tableName){			     
			if(newName==null)
		  	{
			  	 alert("请输入银行报盘名称！");
			  	 return false;
		    }
		     
		    if(IsOverStrLength(newName,28))
			{ 
			    alert("银行名称输入过长！");
			    
			      return false;
			}
			  if(trim(newName).length==0)
			  {
			      alert("银行名称不能为空,请输入正确的银行名称!");
			     
			      return false;
			  }

			  return true;
}
	function bankdisk_addBank_dmlok(outparameters){
		 var retval=new Array(); 
		 var salaryid=outparameters.getValue("salaryid");
	     var bank_id=outparameters.getValue("bank_id");
	 
	     var code=outparameters.getValue("code");
	     var tableName=outparameters.getValue("tableName");
	     var bann=document.getElementsByName("bank_id")[0];
	     bann.value=bank_id;
	   	dmlbank_id=bank_id;
	}
/******dml end*********/
	

/**obj中的值+1*/
function inc_count(obj){
		
		var value=obj.value;
		if(value.substring(0,1)=="0")
		   value=value.substring(1,value.length);
	   	value=getInt(value);      	   
		value = value+1;
		if(value>12)
		  value=12;
		obj.value = value;
}

/**obj中的值减-1*/
function dec_count(obj){
		    var value=obj.value;
			if(value.substring(0,1)=="0")
		   	   value=value.substring(1,value.length);	
		   	value=getInt(value);       
			value = value-1;
			if(value<=0)
				value=1;
			obj.value = value;
}
		
	
