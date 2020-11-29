	
	var flag=1;
	function giveValue(value)
	{
	   flag=value;
	}
	//显示员工日志
	 function showWordDiary(a0100,startDate,endDate)
	 {
	 		  var _width=window.screen.width-200;
	 		 var _url="/performance/workdiary/workdiaryshow.do?b_query=link&timeflag=1&plan_id="+plan_id+"&logo=1&a0100="+a0100+"&start_date="+startDate+"&end_date="+endDate;
			 window.open(_url,null,"width="+_width+",height=700,top=50,left=50,resizable=no,status=no,toolbar=no,scrollbars=yes,menubar=no,location=no"); 
	 }
	
	//  "评分细则"按钮的弹出界面 
	function scoreManual()
	{
	 	var target_url="/performance/objectiveManage/objectiveCard.do?b_scoreManual=link`sort_id=1";
	 	var iframe_url="/general/query/common/iframe_query.jsp?src="+$URL.encode(target_url);
		var return_vo= window.showModalDialog(iframe_url, "", 
				"dialogWidth:600px; dialogHeight:520px;resizable:no;center:yes;scroll:yes;status:no");
	}
	//报批
	function appealTracePoint(opt)
	{
				var hashvo=new ParameterSet();
				if(opt=='02')
				{
					if(!confirm("您确认"+KH_PLAN_ASSIGN+"跟踪指标吗？"))
						return;
				}
				if(opt=='03')
				{
					if(!confirm("您确认"+KH_PLAN_TRANSACT+"跟踪指标吗？"))
						return;
				}

				if(opt!='07') {

                    hashvo.setValue("object_id", object_id);
                    hashvo.setValue("plan_id", plan_id);
                    hashvo.setValue("body_id", body_id);
                    hashvo.setValue("model", model);
                    hashvo.setValue("opt", opt);
                    var request = new Request({
                        method: 'post',
                        asynchronous: false,
                        onSuccess: appealTracePointOk,
                        functionId: '9028000622'
                    }, hashvo);
                }

				if(opt=='07')
				{
                    var rejectCauseWin=Ext.create("Ext.window.Window", {
                        id: 'rejectCauseWin',
                        width: 460,
                        height: 350,
                        title: '退回原因',
                        layout: 'fit',
                        border: false,
                        resizable: false,
                        modal: true,
                        autoScroll: false,
                        renderTo: Ext.getBody(),
                        items: [{
                            xtype: 'textarea',
							id:'rejectCause_text'
                        }],
                        buttons: [{xtype:'tbfill'},{
                            xtype: 'button',
                            text: '确定',
                            handler: function() {
                            	var cause=Ext.getCmp('rejectCause_text').getValue();
                                hashvo.setValue("object_id",object_id);
                                hashvo.setValue("plan_id",plan_id);
                                hashvo.setValue("body_id",body_id);
                                hashvo.setValue("model",model);
                                hashvo.setValue("opt",opt);
                                hashvo.setValue("reject_cause",getEncodeStr(cause));
                                rejectCauseWin.close();
                                var request=new Request({method:'post',asynchronous:false,onSuccess:appealTracePointOk,functionId:'9028000622'},hashvo);
                            }

                        },
						{
							xtype: 'button',
							text: '取消',
                            handler: function() {
                                rejectCauseWin.close();
                            }
						},{xtype:'tbfill'}
                        ]
                    }).show();
				}

	}

	function appealTracePointOk(outparamters)
	{
		var info = outparamters.getValue("info");
		info = info.replace(/<br>/g,"\r\n");
		alert(info);
		document.objectCardForm.action="/performance/objectiveManage/objectiveCard.do?b_query=query&model="+model+"&opt="+opt+"&planid="+mdplan_id+"&object_id="+mdobject_id+"&body_id="+body_id;;
    	document.objectCardForm.submit();
	}


	// 引入上期目标卡
	function importPreCard(object_id,plan_id)
	{
		var hashvo=new ParameterSet();
		hashvo.setValue("object_id",object_id);
		hashvo.setValue("plan_id",plan_id);
		hashvo.setValue("opt","0");
		var request=new Request({method:'post',asynchronous:false,onSuccess:importPreCard2,functionId:'9028000621'},hashvo);
	}
	
	function importPreCard2(outparamters)
	{
		var object_id=outparamters.getValue("object_id") ;
		var plan_id=outparamters.getValue("plan_id")
		var info=getDecodeStr(outparamters.getValue("info"));
		if(info=='1')
		{
			if(confirm("您确定要引入上期"+KH_OBJECTIVE_LABLE+"吗？此操作会将当前"+KH_OBJECTIVE_LABLE+"中的数据清除!"))
			{
				if(confirm('为避免误操作带来严重后果，请再次确认!'))
				{
					var hashvo=new ParameterSet();
					hashvo.setValue("object_id",object_id);
					hashvo.setValue("plan_id",plan_id);
					hashvo.setValue("opt","1");
					var request=new Request({method:'post',asynchronous:false,onSuccess:importSuccess,functionId:'9028000621'},hashvo);
				}
			}
		}
		else
			alert("上期"+KH_OBJECTIVE_LABLE+"无合适的数据引入!");
	}


	//确认目标卡
	function confirmCard()
	{
		var hashvo=new ParameterSet();
		hashvo.setValue("operator","6");
		hashvo.setValue("pendingCode",pendingCode);
		var request=new Request({method:'post',asynchronous:false,onSuccess:confirmOk,functionId:'9028000608'},hashvo);
	}
	
	function confirmOk()
	{
		window.close();
	}

	var p0400_scoreReason = "";
	function scoreReason(plan_id,object_id,mainbody_id,p0400,opt)
    {
		p0400_scoreReason = p0400;
    	var strurl="/selfservice/performance/singleGrade.do?b_initScoreCause=query`type=1`plan_id="+plan_id+"`opt="+opt+"`objectid="+object_id+"`userID="+mainbody_id+"`point_id="+p0400;
		var iframe_url="/general/query/common/iframe_query.jsp?src="+$URL.encode(strurl);
		//var reject_cause=window.showModalDialog(iframe_url,arguments,"dialogWidth=460px;dialogHeight=350px;resizable=yes;scroll=no;status=no;");  
		
		if(!window.showModalDialog)
			window.dialogArguments = arguments;
		
		var config = {
		    width:480,
		    height:350,
		    type:'1',
		    id:"scoreReasonWin",
		    dialogArguments:arguments
		}
		modalDialog.showModalDialogs(iframe_url,"",config,scoreReason_ok);
    }
	
	function scoreReason_ok(reject_cause) {
		if(reject_cause!=null && reject_cause!='undefined')  // 评分说明可以制为空 并且也刷新 JinChunhai 2011.12.08
		{
			var all_r=reject_cause;
			if(reject_cause.length>50)
			{
				reject_cause=reject_cause.substring(0,50)+"......";
				reject_cause=replaceAll(reject_cause,"<br>","\r\n");
				reject_cause=replaceAll(reject_cause," ","&nbsp;");
				document.getElementById('r_'+p0400_scoreReason).innerHTML=reject_cause;
			}
			else
			{
				reject_cause=replaceAll(reject_cause,"<br>","\r\n");
				reject_cause=replaceAll(reject_cause," ","&nbsp;");
				document.getElementById('r_'+p0400_scoreReason).innerHTML=reject_cause;
			}
			document.getElementById('r_'+p0400_scoreReason).title=all_r;
		}
	}

 	function hidden()
   {
   		Element.hide('date_panel');
   }
   
   
   function showDateSelectBox(srcobj,point_id)
   {
      
      var pos=getAbsPosition(srcobj);
      var hashvo=new ParameterSet();
	  hashvo.setValue("pos0",pos[0]);
	  hashvo.setValue("pos1",pos[1]);
      hashvo.setValue("srcobj_width",srcobj.offsetWidth);
      hashvo.setValue("srcobj_height",srcobj.offsetHeight);      
      var In_paramters="point_id="+point_id; 		
	  var request=new Request({method:'post',asynchronous:false,parameters:In_paramters,onSuccess:returnInfo2,functionId:'90100160009'},hashvo);
   }
   
   
   //驳回
   function reject(body_id,mainbodyid,object_id,planid) {
	  //haosl 本人不参与评分流程时，直接领导退回校验是否可以退回目标卡 start
	  var hashvo=new ParameterSet();
      hashvo.setValue("planid",planid);
      hashvo.setValue("object_id",object_id);      
      hashvo.setValue("body_id",body_id);		
      
      var isCanReject = true;
      
	  var request=new Request({method:'post',asynchronous:false,onSuccess:function(outparamters){
		  var rejectObjList = outparamters.getValue("rejectObjList");
		  if(rejectObjList.length==0){
			  isCanReject = false;
		  }
	  },functionId:'9028000625'},hashvo);
	  
	  	if(!isCanReject){
	  		alert("没有要退回的人员！");
	  		return;
	  	}
	  //haosl 本人不参与评分流程时，直接领导退回校验是否可以退回目标卡 end
   		window.planid = planid;
   		window.mainbodyid = mainbodyid;
       if(flag==1)
       {
   		 if(!confirm(OBJECTCARDINFO14+"？"))
 		 {
 			 return;
 		 }
 	   }
 		else
 		{
 		    if(!confirm("确认"+KH_PLAN_BACK+"当前"+KH_OBJECTIVE_LABLE+"吗？"))
 		    {
 			   return;
 		    }
 		}
 		var info='';
	    if(flag==1)
	    	info=OBJECTCARDINFO15; 
	    else
	        info=KH_PLAN_BACK+"原因";
	        
	        
	    var strurl="/performance/objectiveManage/objectiveCard.do?b_reject=search`isMustFill=1`info="+info;   //"/gz/gz_accounting/rejectCause.jsp?isMustFill=1";
	    var iframe_url="/general/query/common/iframe_query.jsp?src="+$URL.encode(strurl);
	    //var reject_cause=window.showModalDialog(iframe_url,arguments,"dialogWidth=460px;dialogHeight=350px;resizable=yes;scroll=no;status=no;");  

	    // 多浏览器兼容showModalDialog改为Ext.window形式 chent 20171226 add
	    Ext.create("Ext.window.Window",{
	    	id:'reject_win',
        	width:460,
        	height:355,
        	title:'退回',
        	resizable:false,
        	modal:true,
        	autoScroll:false,
        	renderTo:Ext.getBody(),
        	html:"<iframe style='background-color:#ffffff' frameborder='0' SCROLLING=NO height='100%' width='100%' src='"+iframe_url+"'></iframe>"
        }).show();
		
   }
   function reject_ok(reject_cause){
   		if(!reject_cause) {
 			return;
 		}
 		
 		reject_win_close();
 		var isEmail="0";
	 	if(creatCard_mail=='true')
	 	{
	 		if(document.objectCardForm.isSendEmail)
	 		{
	 		   if(document.objectCardForm.isSendEmail.checked)
	 		     	isEmail="1";
	 	    }
	 	    
	 	}
 		var hashvo=new ParameterSet();
 		hashvo.setValue("isEmail",isEmail);
		hashvo.setValue("operator","4");
		hashvo.setValue("object_id",object_id);
		hashvo.setValue("planid",planid);
		hashvo.setValue("body_id",body_id);
		hashvo.setValue("mainbodyid",mainbodyid);
		hashvo.setValue("reject_cause",getEncodeStr(reject_cause[0]));
		hashvo.setValue("rejectObj",reject_cause[1]);
		hashvo.setValue("model",model);
		hashvo.setValue("pendingCode",pendingCode);
		var request=new Request({method:'post',asynchronous:false,onSuccess:returnOk,functionId:'9028000608'},hashvo);
   }
   function reject_win_close(){
        Ext.getCmp('reject_win').close();
   }
   /** ??????×÷????????·??????·?????÷??????
    * @param agreeBtn ????°???????×÷?á???ó??????°???
    * @param plan_id ??????
    * @param boject_id ???ó±à??
    * @param mainbody_id ?÷??±à??(?±?°???§)
    * @author ????
    */
	function agree(plan_id, object_id) {
		var src = "/performance/objectiveManage/objectiveCard/agreeCardPage.jsp";
		var iframe_url = "/general/query/common/iframe_query.jsp?src=" + src;
		// var opinion = window.showModalDialog(iframe_url, undefined,
		// 		"dialogWidth=460px;dialogHeight=350px;resizable=yes;scroll=no;status=no;");
		window.tempObj = {};
       window.tempObj.plan_id=plan_id;
       window.tempObj.object_id=object_id;
       var config = {
           width:460,
           height:350,
           type:'2'
       };
		modalDialog.showModalDialogs(iframe_url,'agreewin',config,agreeOK_);

	}
	function agreeOK_(opinion){
        if (!opinion) return;
        var hashvo=new ParameterSet();
        hashvo.setValue("plan_id", window.tempObj.plan_id);
        hashvo.setValue("object_id", window.tempObj.object_id);
        if (document.objectCardForm.whole_id) {
            hashvo.setValue("whole_id", document.objectCardForm.whole_id.value);
        }
        if (document.objectCardForm.wholeEvalScoreId) {
            hashvo.setValue("wholeEvalScoreId", document.objectCardForm.wholeEvalScoreId.value);
        }
        hashvo.setValue("opinion", getEncodeStr(opinion));
        var request=new Request({asynchronous:false,onSuccess:agreeOK,functionId:'9028000628'},hashvo);
    }
	/**
	 * ?????á???ó????×÷,?????°?????±???°?????±°???
	 * @param outparamters ???ó???????ú????????
	 * @author ????
	 */
	function agreeOK(data) {
		var returnBtn = document.getElementById("returnBtn");
		if (returnBtn) {
			returnBtn.style.display = "none";
		}
		window.tempObj = undefined;
		document.getElementById("agreeBtn").style.display = "none";
	}
   
   //目标卡批准
   function approveCard(body_id,mainbodyid,object_id,planid)
   {
   		if(mainbodyid==object_id)
	    {
	   		if(!confirm(OBJECTCARDINFO3+"?"))
	 		{
	 			return;
	 		}
	 	}
	 	else
	 	{
	 	  if(flag==1)
	 	  {
		 	if(!confirm(OBJECTCARDINFO16+"?"))
		 	{
		 		return;
		 	}
		  }
		  else
		  {
		     if(!confirm("您确定"+KH_PLAN_TRANSACT+"当前"+KH_OBJECTIVE_LABLE+"吗?"))
		 	 {
		 		return;
		 	 }
		  }
		}
	 	var isEmail="0";
	 	if(creatCard_mail=='true')
	 	{
	 		if(document.objectCardForm.isSendEmail)
	 		{
	 		   if(document.objectCardForm.isSendEmail.checked)
	 			  isEmail="1";
	 	    }
	 	}
	 	
	 	
	 	var infos=new Array();
		for(var i=0;i<document.objectCardForm.elements.length;i++)
		{
		 		if(document.objectCardForm.elements[i].type=='text')
		 		{
		 			var name=document.objectCardForm.elements[i].name;
		 			var temp=name.split("~")
		 			if(status=='0')
		 			{
		 				if(name.indexOf("~p0413")!=-1)
		 				{
		 					infos[infos.length]=temp[0]+"/"+document.objectCardForm.elements[i].value;
		 				}
		 			}
		 			else if(status=='1')
		 			{
		 				if(name.indexOf("~p0415")!=-1)
		 				{
		 					infos[infos.length]=temp[0]+"/"+document.objectCardForm.elements[i].value;
		 				}
		 			}
		 		}
		 }
  		var hashvo=new ParameterSet();
  		if(infos.length>0)
  		{
  			hashvo.setValue("valueList",infos);
  		}
  		hashvo.setValue("isEmail",isEmail);
		hashvo.setValue("operator","3");
		hashvo.setValue("object_id",object_id);
		hashvo.setValue("planid",planid);
		hashvo.setValue("body_id",body_id);
		hashvo.setValue("mainbodyid",mainbodyid);
		hashvo.setValue("model",model);
		hashvo.setValue("pendingCode",pendingCode);
		var request=new Request({method:'post',asynchronous:false,onSuccess:returnOk,functionId:'9028000608'},hashvo);
   }
   
   //目标卡报批
   function	 appealCard(event,body_id,mainbodyid,object_id,planid)
   {
   		window.myevent = event || window.event;
	    var hashvo=new ParameterSet();
	    
	    
	    if(mainbodyid==object_id||un_functionary==mainbodyid)
	    {
	    	
	 		var infos=new Array();
		 	for(var i=0;i<document.objectCardForm.elements.length;i++)
		 	{
		 		if(document.objectCardForm.elements[i].type=='text')
		 		{
		 			var name=document.objectCardForm.elements[i].name;
		 			var temp=name.split("~")
		 			if(status=='0')
		 			{
		 				if(name.indexOf("~p0413")!=-1)
		 				{
		 					infos[infos.length]=temp[0]+"/"+document.objectCardForm.elements[i].value;
		 				}
		 			}
		 			else if(status=='1')
		 			{
		 				if(name.indexOf("~p0415")!=-1)
		 				{
		 					infos[infos.length]=temp[0]+"/"+document.objectCardForm.elements[i].value;
		 				}
		 			}
		 		}
		 	}
	 		hashvo.setValue("model",model);
			hashvo.setValue("body_id",body_id);
			hashvo.setValue("valueList",infos);
			hashvo.setValue("reject_cause","");
			hashvo.setValue("operator","1");
			hashvo.setValue("object_id",object_id);
			hashvo.setValue("planid",planid);
			hashvo.setValue("pendingCode",pendingCode);
			var request=new Request({method:'post',asynchronous:false,onSuccess:returnSaveOk,functionId:'9028000608'},hashvo);
	 	}
	 	else
	 	{
			hashvo.setValue("object_id",object_id);
			hashvo.setValue("planid",planid);
			hashvo.setValue("body_id",body_id);
			hashvo.setValue("mainbodyid",mainbodyid);
   			var request=new Request({method:'post',asynchronous:false,onSuccess:appealCard2,functionId:'9028000619'},hashvo);
   		}
   }
   
   function returnSaveOk(outparamters)
   {
   		var info=getDecodeStr(outparamters.getValue("info"));
   		if(info.length!=0)
		{	
		
			if(flag==1)
			   alert(info+"\r\n"+OBJECTCARDINFO17+"!");//交办失败//普天
			else
			   alert(info+"\r\n"+KH_PLAN_ASSIGN+"失败!");
		}
		else
		{
			var hashvo=new ParameterSet();
			var object_id=outparamters.getValue("object_id");
			var planid=outparamters.getValue("planid");
			var body_id=outparamters.getValue("body_id");
			var mainbodyid=outparamters.getValue("mainbodyid");
			hashvo.setValue("object_id",object_id);
			hashvo.setValue("planid",planid);
			hashvo.setValue("body_id",body_id);
			hashvo.setValue("mainbodyid",mainbodyid);
   			var request=new Request({method:'post',asynchronous:false,onSuccess:appealCard2,functionId:'9028000619'},hashvo);
		}
   }
   
   
    function hiddenElement()
	{
   		 setTimeout("closeMenu()",300);
		
	}
	function closeMenu()
	{
		 var obj=document.getElementById('menu_');
		 obj.style.display="none";
		 document.getElementById("appeal").disabled=false;
	}
	
	function enterObj(mainbodyid,object_id,planid,body_id)
	{
		var objs=document.getElementsByName("appealObject");
		var n=0;
		for(var i=0;i<objs.length;i++)
		{
			if(objs[i].checked)
			{
				n++;
				appealCard3(mainbodyid,object_id,planid,objs[i].value,body_id);
			}
		}
		if(n==0)
		{
		   if(flag==1)
		    	alert(OBJECTCARDINFO18+"!");
		    else
		        alert("请选择"+KH_PLAN_ASSIGN+"对象!");
			return;
		}
	}
	function enterObj2(object_id,planid)
	{
		var objs=document.getElementsByName("appealObject2");
		var n=0;
		for(var i=0;i<objs.length;i++)
		{
			if(objs[i].checked)
			{
				n++;
				
				if(confirm("确定引入上级"+KH_OBJECTIVE_LABLE+"吗？此操作会将当前"+KH_OBJECTIVE_LABLE+"中的数据清除！"))
				{
				
				}else
					return;
				
				importLeaderTarget2(planid,object_id, objs[i].value)
			}
		}
		if(n==0)
			alert("请选择上级!");
	}
	function importLeaderTarget2(planid,pastObjId, copyObjId)
	{		
		var hashvo=new ParameterSet();
		hashvo.setValue("planid",planid);
		hashvo.setValue("pastObjId",pastObjId);
		hashvo.setValue("copyObjId",copyObjId);
		hashvo.setValue("opt",'28');
		var request=new Request({method:'post',asynchronous:false,onSuccess:refreshPage,functionId:'9023000003'},hashvo);		 			
	}
	function refreshPage(outparameters)
	{   
		goback();
	}
	function   appealCard2(outparamters)
   {
   
   		 var info=outparamters.getValue("info");
   		 if(info=='ok')
   		{
   			 alert("该考核对象没有审批主体，将直接进行批准操作！");
   			 var object_id=outparamters.getValue("object_id")
   	   		 var planid=outparamters.getValue("planid")
   	  		 var body_id=outparamters.getValue("body_id")
   	  		 var mainbodyid=outparamters.getValue("mainbodyid")
   			 approveCard(body_id,mainbodyid,object_id,planid);	 
   			return;
   		}
   		 if(info.length>0)
   		 {
   		 	alert(info);
   		 	return;
   		 }
   
   		 document.getElementById("appeal").disabled=true;
   		 
   		 var object_id=outparamters.getValue("object_id")
   		 var planid=outparamters.getValue("planid")
  		 var body_id=outparamters.getValue("body_id")
  		 var mainbodyid=outparamters.getValue("mainbodyid")
  		 var appealObjectStr=outparamters.getValue("appealObjectStr");
  		 if(appealObjectStr.indexOf("&#&")!=-1)
  		 {
  		 
  		   if(flag==1)
  		   {
  		 	  if(!confirm(OBJECTCARDINFO19+"?"))
	 		 {
	 			 document.getElementById("appeal").disabled=false;
	 			 return;
	 		 }
	 	   }
	 	   else
	 	   {
	 	      if(!confirm("您确认执行"+KH_PLAN_ASSIGN+"操作吗?"))
	 		 {
	 			 document.getElementById("appeal").disabled=false;
	 			 return;
	 		 }
	 	   }
  		 	var temps=appealObjectStr.split("&#&");
  		 	var str="<table>";
  		 	if(flag==1)
  		     	str+="<tr><td colspan='3' align='left' >"+OBJECTCARDINFO20+":</td></tr>";
  		     else
  		        str+="<tr><td colspan='3' align='left' >"+KH_PLAN_ASSIGN+"对象:</td></tr>";
  		 	for(var i=0;i<temps.length;i++)
  		 	{
				var temp=temps[i];
				var a_temp=temp.split("^");
				if(i!=0)
					str+="<tr><td colspan='3' ><hr style=\"border:0;background-color:#C4D8EE;height:1px;\" class=\"complex_border_color\"></td></tr>";
				str+="<tr><td><input type='radio' value='"+a_temp[4]+"' ";
			//	if(i==0)
			//		str+=" checked ";
				str+="  name='appealObject' /></td>";
				str+="<td  nowrap  align='left' >单位:"+a_temp[0]+"<br>部门:"+a_temp[1]+"<br>职位:"+a_temp[2]+"</td><td nowrap align='right' >&nbsp;"+a_temp[3]+"</td>";
				str+="</tr>";
	
			}
			
			str+="<tr><td colspan='3' align='right'  ><hr style=\"border:0;background-color:#C4D8EE;height:1px;\" class=\"complex_border_color\"><input type='button' value='确定' onclick='enterObj(\""+mainbodyid+"\",\""+object_id+"\",\""+planid+"\",\""+body_id+"\")'  class='mybutton'  >&nbsp;<input type='button' value='取消' class='mybutton' onclick='closeMenu()'  > </td></tr>";
			
  		 	str+="</table>";
  		 	var obj=document.getElementById("menu_");
  		 	obj.innerHTML=str;
			obj.style.display="block";
			obj.style.position="absolute";
			 var _top = myevent.clientY || myevent.offsetY || myevent.pageY;
			 var _left = myevent.clientX || myevent.offsetX || myevent.pageX;
			obj.style.left=_left-15;
		    obj.style.top=_top-65*temps.length-70;
  		 }
  		 else
  		 {
  		 	var a_temp=appealObjectStr.split("^");
  		 	if(flag==1)
  		 	{
  		    	if(!confirm("您确认将"+KH_OBJECTIVE_LABLE+"报["+a_temp[3]+"]"+KH_PLAN_TRANSACT_1+"?"))//您确认将考核表交["+a_temp[3]+"]办理?
	 	    	{
	 	    		document.getElementById("appeal").disabled=false;
	 	    		return;
	 	    	}
	 	    }else
	 	    {
	 	       if(!confirm("您确认将"+KH_OBJECTIVE_LABLE+"报给["+a_temp[3]+"]"+KH_PLAN_TRANSACT_1+"?"))//您确认将考核表交["+a_temp[3]+"]办理?
	 	    	{
	 	    		document.getElementById("appeal").disabled=false;
	 	    		return;
	 	    	}
	 	    }
  		 	
  		 	appealCard3(mainbodyid,object_id,planid,a_temp[4],body_id);
  		 }
  		 
   }
   function importLeaderTargetCard(object_id,planid)
   {
   		var appealObjectStr = objectCardForm.appealObjectStr.value;
   		
		var plantype='emp';
 		var temps=appealObjectStr.split("&#&");
 		if(temps.length==1)
 		{
 		
 			if(confirm("确定引入上级"+KH_OBJECTIVE_LABLE+"吗？此操作会将当前"+KH_OBJECTIVE_LABLE+"中的数据清除！"))
			{
			
			}else
				return;
 		
 			var temp=temps[0].split("/");
			var a_temp=temp[0].split("^");
			plantype=a_temp[0];
			if(plantype=='emp')
			{
				if(confirm("您确认将引入["+a_temp[4]+"]的"+KH_OBJECTIVE_LABLE+"吗?"))
 					importLeaderTarget2(planid,object_id, a_temp[5]+"/"+temp[2]);
			}else if(plantype=='team')
			{
				if(confirm("您确认将引入["+a_temp[2]+"]的"+KH_OBJECTIVE_LABLE+"吗?"))
 					importLeaderTarget2(planid,object_id, a_temp[1]+"/"+temp[2]);
			}
 		}else
 		{
 			var str="<table>";
 			str+="<tr><td colspan='3' align='left' >请选择:</td></tr>";
  		 	for(var i=0;i<temps.length;i++)
  		 	{
				var temp=temps[i].split("/");
				var a_temp=temp[0].split("^");
			    plantype=a_temp[0];
				if(plantype=='emp')
				{
					if(i!=0)
						str+="<tr><td colspan='3' ><hr></td></tr>";
					str+="<tr><td><input type='radio' value='"+a_temp[5]+"/"+temp[2]+"' ";
					str+="  name='appealObject2' /></td>";
					str+="<td  nowrap  align='left' >单位:"+a_temp[1]+"<br>部门:"+a_temp[2]+"<br>职位:"+a_temp[3]+"</td><td nowrap align='right' >&nbsp;"+a_temp[4]+"</td>";
					str+="</tr>";
				}else if(plantype=='team')
				{
 					if(i!=0)
						str+="<tr><td  ><hr></td></tr>";
					str+="<tr><td nowrap><input type='radio' value='"+a_temp[1]+"/"+temp[2]+"'";
					str+="  name='appealObject2' />&nbsp;"+a_temp[2]+"("+a_temp[3]+")</td>";
					str+="</tr>";
				}
			}
			
			str+="<tr><td colspan='3' align='right'  ><hr><input type='button' value='确定' onclick='enterObj2(\""+object_id+"\",\""+planid+"\")'  class='mybutton'  >&nbsp;<input type='button' value='取消' class='mybutton' onclick='closeMenu()'  > </td></tr>";
			
  		 	str+="</table>";
  		 	var obj=document.getElementById("menu_");
  		 	obj.innerHTML=str;
			obj.style.display="block";
			obj.style.position="absolute";
			obj.style.posLeft=event.clientX-15;	
			if(plantype=='emp')
		    	obj.style.posTop=event.clientY-65*temps.length-70;
		    else
		    	obj.style.posTop=event.clientY-35*temps.length-70;
		    	
 		}
   }
   //查看上级目标卡
   function lookLeaderTargetCard(object_id,planid)
   {
   		var appealObjectStr = objectCardForm.appealObjectStr.value;
 		var temps=appealObjectStr.split("&#&");
 		var plantype='emp';
 		if(temps.length==1)
 		{
 			var temp=temps[0].split("/");
			var a_temp=temp[0].split("^");	
		    plantype=a_temp[0];
			if(plantype=='emp')
			{
				lookLeaderTarget2(planid,object_id, a_temp[5]+"/"+temp[2]);
			}else if(plantype=='team')
			{
				lookLeaderTarget2(planid,object_id, a_temp[1]+"/"+temp[2]);
			}		
 		}else
 		{
 			var str="<table>";
  		    str+="<tr><td colspan='3' align='left' >请选择:</td></tr>";
  		 	for(var i=0;i<temps.length;i++)
  		 	{
				var temp=temps[i].split("/");
				var a_temp=temp[0].split("^");
			    plantype=a_temp[0];
				if(plantype=='emp')
				{
					if(i!=0)
						str+="<tr><td colspan='3' ><hr></td></tr>";
					str+="<tr><td><input type='radio' value='"+a_temp[5]+"/"+temp[2]+"' ";
					str+="  name='lookappealObject2' /></td>";
					str+="<td  nowrap  align='left' >单位:"+a_temp[1]+"<br>部门:"+a_temp[2]+"<br>职位:"+a_temp[3]+"</td><td nowrap align='right' >&nbsp;"+a_temp[4]+"</td>";
					str+="</tr>";
				}else if(plantype=='team')
				{
 					if(i!=0)
						str+="<tr><td  ><hr></td></tr>";
					str+="<tr><td nowrap><input type='radio' value='"+a_temp[1]+"/"+temp[2]+"'";
					str+="  name='lookappealObject2' />&nbsp;"+a_temp[2]+"("+a_temp[3]+")</td>";
					str+="</tr>";
				}	
			}			
			str+="<tr><td colspan='3' align='right'  ><hr><input type='button' value='确定' onclick='lookenterObj2(\""+object_id+"\",\""+planid+"\");closeMenu();'  class='mybutton'  >&nbsp;<input type='button' value='取消' class='mybutton' onclick='closeMenu()'  > </td></tr>";
			
  		 	str+="</table>";
  		 	var obj=document.getElementById("menu_");
  		 	obj.innerHTML=str;
			obj.style.display="block";
			obj.style.position="absolute";
			obj.style.posLeft=event.clientX-15;	
			if(plantype=='emp')
		    	obj.style.posTop=event.clientY-65*temps.length-70;
		    else
		    	obj.style.posTop=event.clientY-35*temps.length-70;	
 		}
   }
    function lookenterObj2(object_id,planid)
	{
		var objs=document.getElementsByName("lookappealObject2");
		var n=0;
		for(var i=0;i<objs.length;i++)
		{
			if(objs[i].checked)
			{
				n++;
				lookLeaderTarget2(planid,object_id, objs[i].value)
			}
		}
		if(n==0)
			alert("请选择上级!");
	}
	function lookLeaderTarget2(planid,pastObjId, copyObjId)
	{				
		var temp=copyObjId.split("/")
		var target_url="/performance/objectiveManage/copyObjectiveCard.do?b_query=query`zglt=0`entranceType=0`fromflag=rz`body_id=1`model=6`opt=0`planid="+temp[1]+"`object_id="+temp[0];
 		var iframe_url="/general/query/common/iframe_query.jsp?src="+$URL.encode(target_url);
		var return_vo= window.showModalDialog(iframe_url, "", 
			"dialogWidth:"+(window.screen.width*0.8)+"px; dialogHeight:"+(window.screen.height*0.7)+"px;resizable:no;center:yes;scroll:yes;status:no");	
	
//		dialogWidth:450px; dialogHeight:180px;
//		"dialogWidth:"+(((window.screen.availWidth)*7)/10)+"; dialogHeight:"+(((window.screen.availHeight)*7)/10)+";
//	    document.objectCardForm.action="/performance/objectiveManage/objectiveCard.do?b_query=query&zglt=0&returnflag=il_body&entranceType=0&body_id=1&model=6&opt=0&planid="+planid+"&object_id="+copyObjId;
//	    document.objectCardForm.submit();
	    		 			
	}
	
   function appealCard3(mainbodyid,object_id,planid,appealObject_id,body_id)
   {
  	    var hashvo=new ParameterSet();
   		var infos=new Array();
	 	for(var i=0;i<document.objectCardForm.elements.length;i++)
	 	{
	 		if(document.objectCardForm.elements[i].type=='text')
	 		{
	 			var name=document.objectCardForm.elements[i].name;
	 			var temp=name.split("~")
	 			if(status=='0')
	 			{
	 				if(name.indexOf("~p0413")!=-1)
	 				{
	 					infos[infos.length]=temp[0]+"/"+document.objectCardForm.elements[i].value;
	 				}
	 			}
	 			else if(status=='1')
	 			{
	 				if(name.indexOf("~p0415")!=-1)
	 				{
	 					infos[infos.length]=temp[0]+"/"+document.objectCardForm.elements[i].value;
	 				}
	 			}
	 		}
	 	}
	 	var isEmail="0";
	 	if(creatCard_mail=='true')
	 	{
	 		if(document.objectCardForm.isSendEmail)
	 		{
	 		  if(document.objectCardForm.isSendEmail.checked)
	 			 isEmail="1";
	 		}
	 	}
	 	
	 	hashvo.setValue("model",model);
		hashvo.setValue("valueList",infos);
		hashvo.setValue("operator","2");
		hashvo.setValue("object_id",object_id);
		hashvo.setValue("planid",planid);
		hashvo.setValue("appealObject_id",appealObject_id);
		hashvo.setValue("body_id",body_id);
		hashvo.setValue("url_p",url_p);
		hashvo.setValue("pendingCode",pendingCode);
		hashvo.setValue("isEmail",isEmail);
		var request=new Request({method:'post',asynchronous:false,onSuccess:returnOk,functionId:'9028000608'},hashvo);
   
   }
   // 总结回顾
   function review(P0400,isRead,height)
   {
   		// var arguments=new Array();
	    var strurl="/performance/objectiveManage/objectiveCard.do?b_review=review`txid=-1`isRead="+isRead+"`p0400="+P0400;
	    var iframe_url="/general/query/common/iframe_query.jsp?src="+$URL.encode(strurl);
	    if (window.showModalDialog){
            window.showModalDialog(iframe_url,arguments,"dialogWidth=720px;dialogHeight=750px;resizable=yes;scroll=no;status=no;");
            if(isRead != '1'){//如果是只读的，就不刷新页面了
                goback();
            }
            return;
        }
       if(typeof window.Ext == 'undefined'){
           insertFile("/ext/ext6/resources/ext-theme.css","css", function(){
               insertFile("/ext/ext6/ext-all.js","js",function () {
                   // 多浏览器兼容showModalDialog改为Ext.window形式 chent 20171226 add
                   Ext.create("Ext.window.Window",{
                       id:'review_win',
                       width:720,
                       height:height || 680,
                       title:'信息回顾',
                       resizable:false,
                       modal:true,
                       autoScroll:true,
                       renderTo:Ext.getBody(),
                       html:"<iframe style='background-color:#ffffff' frameborder='0' SCROLLING=NO height='99%' width='100%' src='"+iframe_url+"'></iframe>",
                       listeners:{
                           'close':function(){
                               if(isRead != '1'){//如果是只读的，就不刷新页面了
                                   goback();
                               }
                           }
                       }
                   }).show();
               });
           });

       }else{
           // 多浏览器兼容showModalDialog改为Ext.window形式 chent 20171226 add
           Ext.create("Ext.window.Window",{
               id:'review_win',
               width:720,
               height:680,
               title:'信息回顾',
               resizable:false,
               modal:true,
               autoScroll:true,
               renderTo:Ext.getBody(),
               html:"<iframe style='background-color:#ffffff' frameborder='0' SCROLLING=NO height='99%' width='100%' src='"+iframe_url+"'></iframe>",
               listeners:{
                   'close':function(){
                       if(isRead != '1'){//如果是只读的，就不刷新页面了
                           goback();
                       }
                   }
               }
           }).show();
       }
   }
   //签批
   function sign_point(p0400) {
	window.p0400 = p0400;
	var arguments = new Array();
	arguments[0] = "";
	arguments[1] = "签批意见";
	var strurl = "/gz/gz_accounting/rejectCause.jsp?fromflag=1";
	var iframe_url = "/general/query/common/iframe_query.jsp?src=" + strurl;
	// 多浏览器兼容showModalDialog改为Ext.window形式 chent 20171226 add
	if (/msie/i.test(navigator.userAgent)) {
		var reject_cause = window.showModalDialog(iframe_url, arguments, "dialogWidth=460px;dialogHeight=350px;resizable=yes;scroll=no;status=no;");
		sign_point_ok(reject_cause);
	} else {
		function openWin() {
			Ext.create("Ext.window.Window", {
				id : 'sign_point_win',
				width : 480,
				height : 290,
				title : '签批意见',
				resizable : false,
				modal : true,
				autoScroll : true,
				renderTo : Ext.getBody(),
				html : "<iframe style='background-color:#ffffff' frameborder='0' SCROLLING=NO height='100%' width='100%' src='" + iframe_url + "'></iframe>"
			}).show();
		}

		if (typeof window.Ext == 'undefined') {
			insertFile("/ext/ext6/resources/ext-theme.css", "css", function() {
						insertFile("/ext/ext6/ext-all.js", "js", openWin);
					});
		} else {
			openWin();
		}
	}
	return;
}
   function sign_point_ok(reject_cause) {
		if (reject_cause) {
			var hashvo = new ParameterSet();
	
			hashvo.setValue("p0400", p0400);
		hashvo.setValue("opinion", getEncodeStr(reject_cause[0]));
		var request = new Request({
					method : 'post',
					asynchronous : false,
					onSuccess : returnSign,
					functionId : '9028000616'
				}, hashvo);
		}
	}
	function sign_pointWinClose(){
		Ext.getCmp('sign_point_win').close();
	}
  
   
   
   
   function returnInfo2(outparamters)
	{
	
		  Element.show('date_panel');   
	      var pos0=outparamters.getValue("pos0")*1;
	      var pos1=outparamters.getValue("pos1")*1;
		  var srcobj_width=outparamters.getValue("srcobj_width")*1;
		  var srcobj_height=outparamters.getValue("srcobj_height")*1;
         var op=eval('date_panel');

		var fieldlist=outparamters.getValue("dataList");	
		var dataHtml=getDecodeStr(outparamters.getValue("dataHtml"));	
		dataHtml=replaceAll(dataHtml,"#@#","<br>");		
		date_panel.innerHTML=dataHtml;
//不知道这个iframe有什么用 取消掉没啥问题 zhanghua 2017-9-27
//		date_panel.innerHTML=date_panel.innerHTML+"<iframe src=\"javascript:false\" style=\"position:absolute; visibility:inherit; top:0px; left:0px;"
//  			 				+"width:"+op.offsetWidth+"; height:"+op.offsetHeight+"; " 					    	
//  			 				+"z-index:-1;filter='progid:DXImageTransform.Microsoft.Alpha(style=0,opacity=0)';\"></iframe>";   				
//         with($('date_panel'))
//		  {
//		        style.position="absolute";
//		        style.posLeft=15;	
//		     
//			    if(window.document.body.offsetHeight<(pos1+srcobj_height+20+op.offsetHeight))
//			    {
//			    	 
//			    	style.posTop=pos1-op.offsetHeight-20;			    	
//			    }
//			    else
//			    {
//			    	 
//			    	 style.posTop=pos1-10+srcobj_height;  
//			    }
//	      }  
		
		var panel=$('date_panel');
		panel.style.position="absolute";
		panel.style.left=15;
		panel.style.zIndex=200;
		
		if(window.document.body.offsetHeight<(pos1+srcobj_height+20+op.offsetHeight))
		{
			panel.style.top=pos1-op.offsetHeight-20;			    	
		}
		else
		{
			panel.style.top=pos1-10+srcobj_height;  
		}
		
	}
   
   
   
   
   
   
   
   
   
   
   
  
   function showDateSelectBox2(srcobj,point_id)
   {
     
      date_desc=srcobj;
      Element.show('date_panel');   
      var pos=getAbsPosition(srcobj);
      window.screen.availWidth
	  with($('date_panel'))
	  {
	        style.position="absolute";
	        
	        style.left=window.screen.availWidth/2-350;
	        style.top=pos[1]-15+srcobj.offsetHeight;  
      }  
      var In_paramters="point_id="+point_id; 		
	 
	  var dataHtml="";
	  dataHtml+="<table width='500' border='0' cellspacing='0' bgColor='#FFFFFF'  align='center' cellpadding=0' class='ListTable'   > ";
	  dataHtml+="<thead><tr> <td  width='60'  align='center' class='TableRow' nowrap >"+P_STANDPOINT+"</td>";
	  dataHtml+="<td width='260' align='center' class='TableRow' nowrap > &nbsp;&nbsp;&nbsp;"+P_POINTDESC+"&nbsp;&nbsp;&nbsp;</td>";			
	  dataHtml+="<td width='60' align='center' class='TableRow' nowrap > &nbsp;&nbsp;&nbsp;"+PERCENT+"&nbsp;&nbsp;&nbsp;</td>";			
	  dataHtml+="<td width='60' align='center' class='TableRow' nowrap > &nbsp;&nbsp;"+P_UVALUE+"&nbsp;&nbsp;</td>";
	  dataHtml+="<td width='60' align='center' class='TableRow' nowrap >&nbsp;&nbsp;"+P_BVALUE+"&nbsp;&nbsp;</td> </tr>  </thead>";
	  if(typeof(point_grade)=="undefined")
	  {
	  	alert(P_FRESHDATA2+"!");
	  	return;
	  }
	  var gradeList=point_grade['p'+point_id];
	  for(var i=0;i<gradeList.length;i++)
	  {
	       var temp=gradeList[i];
	       
	  		dataHtml+="<tr";
			if(i%2==0)
				dataHtml+=" background-color: #FFFFFF; ";
			else
				dataHtml+=" class='trDeep' ";
			
			if(temp.subsys_id!="undefined" && temp.subsys_id=='35')
			{
				if(typeof(per_competencedegree[temp.gradecode])=="undefined")
				{
					alert(P_NOSAME+"!");
					return;
				}
			}else
			{
				if(typeof(per_standdegree[temp.gradecode])=="undefined")
				{
					alert(P_NOSAME+"!");
					return;
				}
			}
			
			if(temp.subsys_id!="undefined" && temp.subsys_id=='35')			
				dataHtml+="><td align='left' class='RecordRow'  nowrap >"+per_competencedegree[temp.gradecode].gradedesc+"</td>";
			else
				dataHtml+="><td align='left' class='RecordRow'  nowrap >"+per_standdegree[temp.gradecode].gradedesc+"</td>";
			dataHtml+="<td align='left'  class='RecordRow' >"+temp.gradedesc+"</td>";
			dataHtml+="<td  align='left' class='RecordRow' nowrap >"+temp.gradevalue+"</td>";
			dataHtml+="<td  align='left' class='RecordRow' nowrap >"+temp.top_value+"</td>";
			dataHtml+="<td  align='left' class='RecordRow' nowrap >"+temp.bottom_value+"</td></tr>";
	  
	  }
	  dataHtml+="</table>";	
	 
	 
	  dataHtml=replaceAll(dataHtml,"#@#","<br>");	
	  date_panel.innerHTML=dataHtml;
	  date_panel.innerHTML=date_panel.innerHTML+"<iframe src=\"javascript:false\" style=\"position:absolute; visibility:inherit; top:0px; left:0px;"
   			 				+"width:"+date_panel.offsetWidth+"; height:"+date_panel.offsetHeight+"; " 					    	
   			 				+"z-index:-1;filter='progid:DXImageTransform.Microsoft.Alpha(style=0,opacity=0)';\"></iframe>";
   	  // 增加zIndex，否则谷歌下显示不出来 chent 20171226 add
   	  date_panel.style.zIndex = 10;
	  var pos=getAbsPosition(srcobj);
	  var pos0=pos[0];
	  var pos1=pos[1];
	  var srcobj_width=srcobj.offsetWidth;
	  var srcobj_height=srcobj.offsetHeight;
      var op=eval('date_panel');	
       with($('date_panel'))
		  {
		        style.position="absolute";
		        style.left=15;	
		     
			    if(window.document.body.offsetHeight<(pos[1]+srcobj.offsetHeight+20+op.offsetHeight))
			    {
			    	 
			    	style.top=pos[1]-op.offsetHeight-20+'px';			    	
			    }
			    else
			    {
			    	 
			    	 style.top=pos[1]-10+srcobj.offsetHeight+'px';  
			    }
	      }  
   }
 
 
 
 
 
 
var keyDownFunction = function(obj)
{
	if(event.keyCode==13)
        window.event.keyCode=40;
	if(event.keyCode==40)
	{
		var isFind=false;
		for(var i=0;i<document.objectCardForm.elements.length;i++)
		{
			if(document.objectCardForm.elements[i].type=='text')
			{
				if(isFind)
				{
					document.objectCardForm.elements[i].focus();
					break;
				}  
				if(document.objectCardForm.elements[i]==obj)
				{
					isFind=true;
				}
			}
		}
	}
	
	if(event.keyCode==38)
	{
		var index=0;
		for(var i=0;i<document.objectCardForm.elements.length;i++)
		{
			if(document.objectCardForm.elements[i].type=='text')
			{
				if(document.objectCardForm.elements[i]==obj)
				{
					index=i;
					break;
				}
			}
		}
		if(index>0)
			document.objectCardForm.elements[index-1].focus();
			
	}
}
 
 
 //引入绩效指标
 function importPoint(object_id,plan_objectType,planid)
 {	
 	if(!old_itemid||old_itemid.length==0)
 	{
 		alert(QXZGXHXMMC+"!");
 		return;
 	}
 	importPerPoint(object_id,plan_objectType,planid);
 }
 
 //报批调整后的目标卡
 function appearAdjustCard(operator,opt,status,object_id,planid)
 {
 	var reject_cause="";
 	var hashvo=new ParameterSet();
 	if(operator==2)
 	{
 		if(!confirm(OBJECTCARDINFO13+"?"))
 		{
 			return;
 		}
 	}
 	if(operator==3)
 	{
 	  if(flag==1)
 	  {
 		if(!confirm(OBJECTCARDINFO21+"?"))
 		{
 			return;
 		}
 	  }
 	  else
 	  {
 	      if(!confirm("您确认"+KH_PLAN_TRANSACT+"当前"+KH_OBJECTIVE_LABLE+"吗?"))
 		{
 			return;
 		}
 	  }
 	}
 	if(operator==4)
 	{
 	 if(flag==1)
 	 {
 		if(!confirm(OBJECTCARDINFO22+"?"))
 		{
 			return;
 		}
 	 }
 	 else
 	 {
 	    if(!confirm("您确认"+KH_PLAN_BACK+"当前"+KH_OBJECTIVE_LABLE+"吗?"))
 		{
 			return;
 		}
 	 }
 		
 		var arguments=new Array();
	    arguments[0]="";
		arguments[1]=OBJECTCARDINFO15; 
	    var strurl="/gz/gz_accounting/rejectCause.jsp";
	    var iframe_url="/general/query/common/iframe_query.jsp?src="+strurl; 
	    var reject_cause=window.showModalDialog(iframe_url,arguments,"dialogWidth=460px;dialogHeight=350px;resizable=yes;scroll=no;status=no;");  
		if(!reject_cause)
		{
 			return;
 		}
 		
 	}
	 	var infos=new Array();
	 	for(var i=0;i<document.objectCardForm.elements.length;i++)
	 	{
	 		if(document.objectCardForm.elements[i].type=='text')
	 		{
	 			var name=document.objectCardForm.elements[i].name;
	 			var temp=name.split("~")
	 			if(status=='0')
	 			{
	 				if(name.indexOf("~p0413")!=-1)
	 				{
	 					infos[infos.length]=temp[0]+"/"+document.objectCardForm.elements[i].value;
	 				}
	 			}
	 			else if(status=='1')
	 			{
	 				if(name.indexOf("~p0415")!=-1)
	 				{
	 					infos[infos.length]=temp[0]+"/"+document.objectCardForm.elements[i].value;
	 				}
	 			}
	 		}
	 	}
	 	
		hashvo.setValue("opt",opt);
		hashvo.setValue("status",status);
		hashvo.setValue("valueList",infos);
	hashvo.setValue("reject_cause",getEncodeStr(reject_cause[0]));
	hashvo.setValue("operator",operator);
	hashvo.setValue("object_id",object_id);
	hashvo.setValue("planid",planid);
	hashvo.setValue("model",model);
	hashvo.setValue("pendingCode",pendingCode);
	var request=new Request({method:'post',asynchronous:false,onSuccess:returnOk,functionId:'9028000608'},hashvo);
 }
 
 
 
 
 
 //保存 目标任务的权限或分值 和 上报目标卡  和 批准目标卡  、驳回
 function saveTaskValue(operator,body_id,status,object_id,planid)
 {
    var reject_cause="";
 	var hashvo=new ParameterSet();
 	if(operator==3)
 	{
 		if(!confirm(OBJECTCARDINFO3+"？"))
 		{
 			return;
 		}
 	}
 	
 	if(operator!=4)
 	{
	 	var infos=new Array();
	 	for(var i=0;i<document.objectCardForm.elements.length;i++)
	 	{
	 		if(document.objectCardForm.elements[i].type=='text')
	 		{
	 			var name=document.objectCardForm.elements[i].name;
	 			var temp=name.split("~")
	 			if(status=='0')
	 			{
	 				if(name.indexOf("~p0413")!=-1)
	 				{
	 					infos[infos.length]=temp[0]+"/"+document.objectCardForm.elements[i].value;
	 				}
	 			}
	 			else if(status=='1')
	 			{
	 				if(name.indexOf("~p0415")!=-1)
	 				{
	 					infos[infos.length]=temp[0]+"/"+document.objectCardForm.elements[i].value;
	 				}
	 			}
	 		}
	 	}
		hashvo.setValue("body_id",body_id);
		hashvo.setValue("status",status);
		hashvo.setValue("valueList",infos);
	}
	hashvo.setValue("model",model);
	hashvo.setValue("reject_cause",getEncodeStr(reject_cause));
	hashvo.setValue("operator",operator);
	hashvo.setValue("object_id",object_id);
	hashvo.setValue("planid",planid);
	hashvo.setValue("pendingCode",pendingCode);
	var request=new Request({method:'post',asynchronous:false,onSuccess:returnOk,functionId:'9028000608'},hashvo);
 	
 }




 //1:保存 或 2:提交
  function subScore(flag,object_id,planid,body_id,model,saveType)
  {
  	if(flag==2)
  	{
  		if(!confirm(OBJECTCARDINFO4+"?"))
			return;
  	}
  	
  	if(perPointNoGrade=='1')
  	{
  		var n_ids=noGradeItem.split(",");
		var desc="";
		for(var i=0;i<n_ids.length;i++)
		{
			if(trim(n_ids[i]).length!=0)
			{
				desc+="\n\r."+per_pointArray[n_ids[i]].pointname;
			}
		}
		if(desc.length>0)
		{
			alert(desc+"\n\r "+OBJECTCARDINFO5+"！");
			return;
  		}
  	}
  	
  	var values=new Array();
    for(var i=0;i<document.objectCardForm.elements.length;i++)
  	{
  		var obj_name=document.objectCardForm.elements[i].name;
  		if(obj_name.length>2&&(obj_name.substring(0,2)=='p_'||obj_name.substring(0,2)=='s_'))
  		{
  			if(document.objectCardForm.elements[i].value==''||trim(document.objectCardForm.elements[i].value).length==0)
  			{
  				if((flag==2||flag==8)&&isEntireysub=='true'&&!document.objectCardForm.elements[i].disabled)
  				{
  					alert(P_I_INFO6+"!");
  					return;
  				}
  				else
  					values[values.length]=obj_name+":null";
  			}
  			else
  			{
  					values[values.length]=obj_name+":"+document.objectCardForm.elements[i].value;
  			}
  		}
  	}
  	if(eval("document.objectCardForm.whole_id")!=null)
  		values[values.length]="whole_id:"+document.objectCardForm.whole_id.value;
  	if(eval("document.objectCardForm.know_id")!=null)
  		values[values.length]="know_id:"+document.objectCardForm.know_id.value;
  	
  	var isEmail="0";
  	if(flag=='2')
  	{
	 	if(evaluateCard_mail=='true')
	 	{
	 		if(currentlevel<targetMakeSeries)
	 		{
	 			if(document.objectCardForm.isSendEmail&&document.objectCardForm.isSendEmail.checked)
	 				isEmail="1";
	 		}
	 	}
  	
  	}
  	

  	
  	var hashvo=new ParameterSet();
  	hashvo.setValue("isEmail",isEmail);
	hashvo.setValue("object_id",object_id);
	hashvo.setValue("planid",planid);
	hashvo.setValue("body_id",body_id);
	hashvo.setValue("model",model);
	hashvo.setValue("valueList",values);
	hashvo.setValue("flag",flag);
	hashvo.setValue("pendingCode",pendingCode);
	hashvo.setValue("isShowHistoryTask",document.objectCardForm.isShowHistoryTask.value);
	hashvo.setValue("saveType", saveType);
	if(document.getElementById("wholeEvalScoreId")!=null)
		hashvo.setValue("wholeEvalScore", document.objectCardForm.wholeEvalScore.value);
	var request=new Request({method:'post',asynchronous:false,onSuccess:returnSubOk,functionId:'9028000609'},hashvo);
  }



 //总体评价说明
function showWindow(plan_id,object_id,mainbody_id,body_id)
{
   
	var win=open("/performance/markStatus/markStatusList.do?b_edit3=edit&planID="+plan_id+"&body_id="+body_id+"&objectID="+object_id+"&mainbodyID="+mainbody_id,"info","width=510,height=420");
}

 //总体评价说明2(只读)
function showWindow2(plan_id,object_id,mainbody_id,body_id)
{
   var win=window.open("/performance/markStatus/markStatusList.do?b_edit3=edit&opt=read&planID="+plan_id+"&body_id="+body_id+"&objectID="+object_id+"&mainbodyID="+mainbody_id,null,"width=530,height=450,top=50,left=50,resizable=no,status=no,toolbar=no,scrollbars=yes,menubar=no,location=no");
	//var win=open("/performance/markStatus/markStatusList.do?b_edit3=edit&opt=read&planID="+plan_id+"&body_id="+body_id+"&objectID="+object_id+"&mainbodyID="+mainbody_id,"info","width=700,height=420");
}


 function showComment(model_opt,plan_id,object_id)
  {
  	window.open("/performance/objectiveManage/objectiveCard.do?b_searchComment=link&_plan_id_o="+plan_id+"&_plan_id="+plan_id+"&model_opt="+model_opt,"_zz","height=700, width=700, top=0, left=200, toolbar=no,menubar=no,scrollbars=yes,resizable=no,location=no, status=no")
  }
  
  function showInfo(planid)
  {
	var win=open("/servlet/performance/markStatus/showIndexInfo?plan_id="+planid,"info");
	
  }
function collectData(a_code,planid,object_id,model,opt,body_id)
{
 var strurl="/performance/achivement/dataCollection/dataCollect.do?b_query3=link`a_code="+a_code+"`planId="+planid;
   var iframe_url="/general/query/common/iframe_query.jsp?src="+$URL.encode(strurl);
   var returnV=window.showModalDialog(iframe_url,arguments,"dialogWidth=700px;dialogHeight=440px;resizable=yes;scroll=no;status=no;");  
  if(returnV)
  {
      document.objectCardForm.action="/performance/objectiveManage/objectiveCard.do?b_query=query&model="+model+"&opt="+opt+"&planid="+mdplan_id+"&object_id="+mdobject_id+"&body_id="+body_id;;
      document.objectCardForm.submit();
  }
}
//新建加扣分指标
function newEvalCanNewPointPoint()
{
  if(!old_itemid||old_itemid.length==0)
 	{
 		alert(QXZGXHXMMC+"!")
 		return;
 	}
  document.objectCardForm.action="/performance/objectiveManage/objectiveCard.do?b_showTaskDesc=show&itemtype=1&operator=new&itemid="+old_itemid+"&a_p0400="+old_p0400;
  document.objectCardForm.submit();
}
function editEvalCanNewPoint(p0400)
{
	document.objectCardForm.action="/performance/objectiveManage/objectiveCard.do?b_showTaskDesc=show&itemtype=1&operator=edit&p0400="+p0400;
 	document.objectCardForm.submit();
}
//引用职责指标
function importPositionField(plan_id,object_id,model,opt,body_id) {
	window.plan_id = plan_id;
	window.object_id = object_id;
	window.model = model;
	window.opt = opt;
	window.body_id = body_id;
	
   if(!old_itemid||old_itemid.length==0)
 	{
 		alert(QXZGXHXMMC+"!")
 		return;
 	}
   var strurl="/performance/objectiveManage/import_position_field_list.do?b_query=link`importType=position`model="+model+"`body_id="+body_id+"`item_id="+old_itemid+"`plan_id="+plan_id+"`object_id="+object_id+"`p0400="+old_p0400;
   var iframe_url="/general/query/common/iframe_query.jsp?src="+$URL.encode(strurl);
   //var returnV=window.showModalDialog(iframe_url,arguments,"dialogWidth=860px;dialogHeight=400px;resizable=yes;scroll=no;status=no;");  
   
   // 多浏览器兼容showModalDialog改为Ext.window形式 chent 20171226 add
    Ext.create("Ext.window.Window",{
    	id:'importpositionfield_win',
    	width:860,
    	height:500,
    	title:'引用职责指标',
    	resizable:false,
    	modal:true,
    	autoScroll:true,
    	renderTo:Ext.getBody(),
    	html:"<iframe style='background-color:#ffffff' frameborder='0' SCROLLING=NO height='99%' width='100%' src='"+iframe_url+"'></iframe>"
    }).show();	
}
function importPositionField_ok(refresh){
	if(object.refresh==2)
	{
		document.objectCardForm.action="/performance/objectiveManage/objectiveCard.do?b_query=query&model="+model+"&opt="+opt+"&planid="+mdplan_id+"&object_id="+mdobject_id+"&body_id="+body_id;;
		document.objectCardForm.submit();
    }
    return ;
}
function importpositionfieldWinClose(){
	Ext.getCmp('importpositionfield_win').close();
}
function importDeptField(plan_id,object_id,model,opt,body_id){
	 if(!old_itemid||old_itemid.length==0)
 	{
 		alert(QXZGXHXMMC+"!")
 		return;
 	}
   var strurl="/performance/objectiveManage/import_position_field_list.do?b_query=link`importType=dept`model="+model+"`body_id="+body_id+"`item_id="+old_itemid+"`plan_id="+plan_id+"`object_id="+object_id+"`p0400="+old_p0400;
   var iframe_url="/general/query/common/iframe_query.jsp?src="+$URL.encode(strurl);
   var returnV=window.showModalDialog(iframe_url,arguments,"dialogWidth=860px;dialogHeight=400px;resizable=yes;scroll=no;status=no;");  
   if(returnV)
   {
      var object = new Object();
      object.refresh=returnV.refresh;
      if(object.refresh==2)
      {
         document.objectCardForm.action="/performance/objectiveManage/objectiveCard.do?b_query=query&model="+model+"&opt="+opt+"&planid="+mdplan_id+"&object_id="+mdobject_id+"&body_id="+body_id;;
         document.objectCardForm.submit();
      }
   }
}
function copyScore(plan_id,object_id,model,opt,body_id)
{
    document.objectCardForm.action="/performance/objectiveManage/objectiveCard.do?b_query=query&isCopy=true&model="+model+"&opt="+opt+"&planid="+mdplan_id+"&object_id="+mdobject_id+"&body_id="+body_id;;
    document.objectCardForm.submit();
}
function newPoint()
 {
 
 	document.objectCardForm.action="/performance/objectiveManage/objectiveCard.do?b_showTaskDesc=show&itemtype=0&operator=new&itemid="+old_itemid+"&a_p0400="+old_p0400;
 	document.objectCardForm.submit();
 }
 
 //编辑目标任务
function editPoint(p0400,itemtype)
{
	document.objectCardForm.action="/performance/objectiveManage/objectiveCard.do?b_showTaskDesc=show&itemtype="+itemtype+"&operator=edit&p0400="+p0400;
 	document.objectCardForm.submit();
}

 //编辑目标任务2
function editPoint2(p0400)
{
	document.objectCardForm.action="/performance/objectiveManage/objectiveCard.do?b_showTaskDesc2=show&opt="+opt+"&operator=edit&p0400="+p0400;
 	document.objectCardForm.submit();
}

 //编辑目标任务3  已报批或已批并且是目标卡代制订  JinChunhai 2013.03.19
function editPoint3(p0400,itemtype)
{
	document.objectCardForm.action="/performance/objectiveManage/objectiveCard.do?b_showTaskDesc=show&itemtype="+itemtype+"&operator=edit&editCardSp=edit&p0400="+p0400;
 	document.objectCardForm.submit();
}

//删除目标任务
function delPoint(p0400)
{
	if(confirm(OBJECTCARDINFO6+"?"))
	{
		document.objectCardForm.action="/performance/objectiveManage/objectiveCard.do?b_delTask=del&p0400="+p0400;
 		document.objectCardForm.submit();
 	}
}


 
 //选择个性化项目
 function selectItem(itemid,obj)
 { 
 	if(old_itemid&&old_itemid.length>0)
 	{
 		old_obj.className=old_class; //'RecordRow_self_locked';
	 	//old_obj.style.border='1px solid #94B6E6';
 	}
 	old_class=obj.className;
 	if(old_class.indexOf('last')!=-1)
 		obj.className='RecordRow_self_locked_last_selected  RecordRow_Right';
 	else
	 	obj.className='RecordRow_self_locked_selected  RecordRow_Right';
 //	obj.style.border='2px dashed #94B6E6';
 	obj.style.borderWidth="0 1 1 0px;"
 	old_obj=obj;
 	old_itemid=itemid;
 	old_p0400="";
 	
 }
 
 
 //选择个性化项目
 function selectPoint(itemid,p0400,obj)
 {
 	if(old_itemid&&old_itemid.length>0)
 	{
 	//	old_obj.style.border='1px solid #94B6E6';
 		old_obj.className=old_class; //'RecordRow_self_locked';
 	}
 	old_class=obj.className;
 	if(old_class.indexOf('last')!=-1)
 		obj.className='RecordRow_self_locked_last_selected RecordRow_Right';
 	else
	 	obj.className='RecordRow_self_locked_selected RecordRow_Right';
 //	obj.style.border='2px dashed #94B6E6';
 	obj.style.borderWidth="0 1 1 0px;"
 	old_obj=obj;
 	old_itemid=itemid;
 	old_p0400=p0400;
 }
 
 
   /* 打开工作任务界面的链接 有工作计划任务推送过来的任务P08 wangrd 20141127*/ 
  function openP08Task(event,p0400,p0800){
     var returnurl="/performance/objectiveManage/objectiveCard.do?b_query=query"
    +"&opt="+opt;
    returnurl= getEncodeStr(returnurl); 
    //alert(returnurl) ;      
   
    var hashvo = new ParameterSet();
    hashvo.setValue("oprType", "getP08TaskInfo");
    hashvo.setValue("p0800", p0800);
    hashvo.setValue("p0400", p0400);
    var request=new Request({method:'post',asynchronous:false,
          onSuccess: function(outparamters) {              
                p0800 = outparamters.getValue("p0800");
               var p0700 = outparamters.getValue("p0700");
               var p0723 = outparamters.getValue("p0723");
               var objectid = outparamters.getValue("objectid");
               
			    var url="/workplan/plan_task.do?br_task=link&fromFlag=planEval&p0800="
			            +p0800+"&p0700="+p0700
			            +"&p0723="+p0723
			            +"&objectid="+objectid
			            +"&performance="+"1"
			            +"&returnurl="+returnurl    
			            +"";
//			    location.href =url;
			    var taskFrame = document.getElementById("ptaskFrame");
			    var iframe_task = window.frames["piframe_task"];
			    iframe_task.location.href = url;
               // 页面滚动的高度
			    var QUIRKS = document.compatMode == "BackCompat" ? true : false; // 怪异模式(BackCompat)
			    var BODY = QUIRKS ? document.body : document.documentElement;
			    var scrollTop = BODY.scrollTop;
			
			    // 定位左箭头
			    var e = event || window.event;
			    var _top = e.clientY || e.offsetY || e.pageY;
			    taskFrame.style.top = "0px";
			    taskFrame.style.display = "block";
               
            }, functionId:'9028000704'},hashvo);   

 }
 
 
 //取消选择的个性化项目
 function clearItem(itemid,obj)
 {
 	//obj.style.border='1px solid #94B6E6';
 	obj.className=old_class; //'RecordRow_self_locked';
 	old_itemid="";
 	old_obj=null;
 
 }
 
 //取消选择的个性化项目
 function clearPoint(obj)
 {
 //	obj.style.border='1px solid #94B6E6';
 	obj.className=old_class; //'RecordRow_self_locked';
 	old_itemid="";
 	old_p0400="";
 	old_obj=null;
 
 }
 
 //引入绩效指标
 function importPerPoint(objectid,objectType,planid)
 {
 	
 	var infos=new Array();
	infos[0]=objectid;
	infos[1]=objectType;
	infos[2]=planid;
	var thecodeurl="/performance/objectiveManage/objectiveCard.do?br_selectpoint=query`objectid="+objectid+"`objectType="+objectType+"`planid="+planid; 
	var iframe_url="/general/query/common/iframe_query.jsp?src="+$URL.encode(thecodeurl);
//	var points= window.showModalDialog(iframe_url, infos, 
//		        "dialogWidth:430px; dialogHeight:380px;resizable:no;center:yes;scroll:no;status:no");			
		
	// 多浏览器兼容showModalDialog改为Ext.window形式 chent 20171226 add
    Ext.create("Ext.window.Window",{
    	id:'perpoint_win',
    	width:440,
    	height:380,
    	title:'引用绩效指标',
    	resizable:false,
    	modal:true,
    	autoScroll:true,
    	renderTo:Ext.getBody(),
    	html:"<iframe style='background-color:#ffffff' frameborder='0' SCROLLING=NO height='99%' width='100%' src='"+iframe_url+"'></iframe>"
    }).show();		        

 }
 function importPerPoint_ok(points){
 	if(points == undefined) {			
		return;
	}
	if(points.length>0) {
 		document.objectCardForm.importPoint_value.value=points;
 		document.objectCardForm.action="/performance/objectiveManage/objectiveCard.do?b_importPoint=import&itemid="+old_itemid+"&a_p0400="+old_p0400;
 		document.objectCardForm.submit();
 	}
 }
 function importPerPoint_closeWin(){
 	Ext.getCmp('perpoint_win').close();
 }
 function validateNum(obj,isNull)
 {
 	if(obj.value.length==0)  //dengcan 2009123
 		return;
 	if(obj.value.length>0)
 	{
	 		if(!(/^-?\d+(\.\d+)?$/.test(obj.value)))  //   obj.value.search("^-?\\d+$")!=0)
	 		{
	 			alert("请输入数值型数据!");
				obj.value="";
				obj.focus();
				return;
	 		
	 		}
 	}
 }
 
 //校验输入框里的值是否为数字类型
 function validateValue(obj,isNull)
 {
 	// if(isNull!=null&&isNull=='null'&&obj.value.length==0)
 	 if(obj.value.length==0)  //dengcan 2009123
 		return;
 	if(scoreflag==4)
 	{
 		if(obj.value.length>0)
 		{
	 		if(!(/^-?\d+(\.\d+)?$/.test(obj.value)))  //   obj.value.search("^-?\\d+$")!=0)
	 		{
	 			alert("请输入数值型数据!");
				obj.value="";
				obj.focus();
				return;
	 		
	 		}
 		}
 	
 	}
 	else
 	{
 		 var flag=false;
 		 flag=checkIsNum2(obj.value);
 		 /*
 		 if(EvalOutLimitStdScore=='true')
 		 	flag=checkIsNum2(obj.value);
 		 else
 			flag=checkIsNum(obj.value);*/
		 if(!flag)
		 {
		 		if(scoreflag=='2')
		 		{
			 		if(grade_template_id_str.toLowerCase().indexOf(','+obj.value.toLowerCase()+",")==-1)
			 		{
						alert("输入的值不在标度或分值范围内!");
						obj.value="";
						obj.focus();
						return;
					}
				}
				else
				{
						alert(OBJECTCARDINFO7+"!");
						obj.value=0;
						obj.focus();
						return;
				}
		 }
 	}
 }
 
/* function split_str(string,words_per_line) 
 {
 	var output_string = string.substring(0,1); //取出i=0时的字，避免for循环里换行时多次判断i是否为0
 	for(var i=1;i<string.length;i++) {
 		if(i%words_per_line == 0) {
 			output_string += "<br/>";
 		}
 		output_string += string.substring(i,i+1);
 	}
 	return output_string;
 }*/
 var title_value = ''; 
 function title_show(td) 
 {
 	var div=document.getElementById("title_show");
 	div.style.zIndex = 20;
 	title_value = td.getAttribute("tip") || ""; // 描述由title改为tip lium 2012-12-11
 	div.style.left = (td.offsetLeft-10)+"px"; //设置title_show在页面中的位置。
 	div.style.top = (td.offsetTop+20)+"px";
 	if(title_value.length>20) {
 		div.style.width=200 +"px";
 	}
 	else {
 		div.style.width = title_value.length*10 +"px";
 	} 
 	if(title_value=='' || title_value.length==0){
 		div.style.display = "none";
 	}
 	//var words_per_line = 100; //每行字数
 	//var title = split_str(td.title,words_per_line); //按每行14个字显示标题内容。
 	else{
	 	div.innerHTML = title_value;
	 	div.style.display = '';
 	}
 }
 function title_back(td) 
 {
	/* 描述由title改为tip,因此无需写回该属性 lium 2012-12-11
 	td.title = title_value;
 	*/
	 var div=document.getElementById("title_show"); 
 	div.style.display = "none";
 }
 function returnSubOk(outparamters)
 {
	var info=getDecodeStr(outparamters.getValue("info"));
	var flag=outparamters.getValue("flag");  //1:保存 或 2:提交 8完成
	var score=outparamters.getValue("score");
	var ascore=outparamters.getValue("ascore");
	var saveType=outparamters.getValue("saveType");
	var haveFormula=outparamters.getValue("haveFormula");
	var totalAppValue=outparamters.getValue("totalAppValue");
	if(info.length>0)
	{
		alert(info);
	}	
	else
	{
		if(document.getElementById("totalScore"))
			document.getElementById("totalScore").innerHTML=score;
		   //先放开 wangrd 20150720 bug10735
	    if(document.getElementById("selfTotalScore"))
			document.getElementById("selfTotalScore").innerHTML=ascore;
		if(haveFormula!=null && haveFormula=='true')
		{		
			if(eval("document.objectCardForm.whole_id")!=null)
			{
  				document.objectCardForm.whole_id.value=totalAppValue;
  			}
		}					
	    if(saveType=="1")
	    	return;
		if(flag=='1')
		{
			alert(Template_SAVESUCCESS+"!");
			//var copyButton = document.getElementById("copy");
			//if(copyButton)
			  // copyButton.style.display="none";
		}
		else if(flag=='2'||flag=='8')
		{
		     if(flag=='8')
		     {
		        alert(SCORE_SUBSUCCESS+"!"); 
		     }
		     else
		    	alert(SUBSUCCESS+"!");
		    var clientName=outparamters.getValue("clientName");
		    if(clientName=='zglt')
		    {
		       go_back();
		       return;
		    }
			document.getElementById('aa').style.display="none";
			document.getElementById('bb').style.display="block";
			var opt='0';
			if(flag=='8')
			   opt='2';
			var planid=outparamters.getValue("planid");
			var body_id=outparamters.getValue("body_id");
			//var model=outparamters.getValue("model");
			var object_id=outparamters.getValue("object_id");
			var rurl = document.getElementById("returnURL")==null?"":document.getElementById("returnURL").value;
			if(rurl==null||rurl=='')
			{
		    	document.objectCardForm.action="/performance/objectiveManage/objectiveCard.do?b_query=query&body_id="+body_id+"&model="+model+"&opt="+opt+"&planid="+mdplan_id+"&object_id="+mdobject_id;
		    	document.objectCardForm.submit();
		    }
		    else
	    		go_back();
		}
	}
 }
 function displayIMG(obj1,obj2)//2013.11.09 pjf
 {
    var tab=document.getElementById(obj1);
    var ig=document.getElementById(obj2);
    if(tab.style.display=='none')
    {
       tab.style.display='block';
       ig.src='/images/expand_pm.gif';
    }
    else
    {
       tab.style.display='none';
       ig.src='/images/collapse_pm.gif';
    }
 }
  function displayIMG1(obj,obj1,obj2)//2013.11.09 pjf
 {
    var tab=document.getElementById(obj1);
    var ig=document.getElementById(obj2);
    var str=document.getElementsByName("type");
    if(str){
    	for(var i=0;i<str.length;i++){
    		if(str[i].checked&&str[i].value==obj){
    			
    		}else if(str[i].checked&&str[i].value!=obj){
    			return;
    		}
    	}
    }
    if(tab.style.display=='none')
    {
       tab.style.display='block';
       ig.src='/images/expand_pm.gif';
    }
    else
    {
       tab.style.display='none';
       ig.src='/images/collapse_pm.gif';
    }
 }
function checkPerStion(srcobj,plan_id,p0400,extJsonWidth)
{
	var extJsWidth = extJsonWidth*1;
	var pos = getAbsPosition(srcobj);
	var pos0 = pos[0]*1;
	var pos1 = pos[1]*1;		
	var srcobj_width = srcobj.offsetWidth*1;
	var srcobj_height = srcobj.offsetHeight*1;
		
	//document.main.location="/performance/objectiveManage/objectiveCard.do?b_extJsTree=query&plan_id="+plan_id+"&p0400="+p0400;
	window.frames["iframe_main"].location.href="/performance/objectiveManage/objectiveCard.do?b_extJsTree=query&plan_id="+plan_id+"&p0400="+p0400;
	Element.show('extJs_tree');
	var extJs = eval('extJs_tree');
	with($('extJs_tree'))
	{
		style.position="absolute";	      
	    style.left=extJsWidth+srcobj.offsetLeft+10;			     
	    style.zIndex = 10;
		if(window.document.body.offsetHeight<(pos1+srcobj_height+20+extJs.offsetHeight))
		{			    	 
			style.top=pos1-extJs.offsetHeight+30;			    	
		}
		else
		{			    	 
			style.top=pos1-10+srcobj_height;  
		}	    	      	      
    }
}

function hiddenExtJs()
{
	Element.hide('extJs_tree');
}
function showSetBox(obj,p0400,p0401,context){
	a_num=0;
	var tempArray;
	if(typeof(point_grade)=="undefined")
	{
		alert(P_FRESHDATA+"!");
		return;
	}
	if(p0401=='per_standdegree')
		tempArray=per_standdegree;		
	else
		tempArray=point_grade['p'+p0401];
	if(p0401!='per_standdegree'&&typeof(point_grade['p'+p0401])=="undefined"){
		alert(P_FRESHDATA+"!");
		return;
	}
	if(p0401=='per_standdegree'||(tempArray[0].pointkind=="0"&&scoreflag=="1")) //下拉框
	{
	  Element.show('options');
      var pos=getAbsPosition(obj);
      window.screen.availWidth
	  with($('options'))
	  {
        style.position="absolute";
        if(typeof(window.addEventListener)=="function")
        {
//        	style.left=pos[0];
//        	style.top=pos[1];  
			if(document.body.clientWidth-pos[0]<350)
        		style.right=document.body.clientWidth-pos[0]-obj.offsetWidth;
        	else
        		style.left=pos[0];
        	style.top=pos[1]; 
        	
        }
        else
        {
//        	style.posLeft=pos[0];
//        	style.posTop=pos[1];  
			if(document.body.clientWidth-pos[0]<350)
        		style.posRight=document.body.clientWidth-pos[0]-obj.offsetWidth;
        	else
        		style.posLeft=pos[0];	
        	style.posTop=pos[1]; 
        }
      }
	  var inner="<table id='selectOption'       class='table_class' bgColor='#ffffff' >";
	  inner+="<tr   "; 
	  inner+=" onclick='setValue(\""+p0400+"\",\"null\",\"\")'  ><td style='cursor:pointer;' class='table_td' >&nbsp;";
	  inner+="</td></tr>";

	  for(var key in tempArray){
//	  for(var i=0;i<tempArray.length;i++)
//	  {
	  	inner+="<tr ";
  		if(tempArray[key].gradecode==context)
  		{
  			inner+="  bgColor='#FFF8D2'  ";
  		}
		if(p0401=='per_standdegree')
	  	{
	  		if(tempArray[key].grade_template_id){
	  			inner+=" onclick='setValue(\""+p0400+"\",\""+tempArray[key].grade_template_id.toUpperCase()+"\",\""+tempArray[key].gradedesc+"\")'  ><td  style='cursor:pointer' class='table_td'  >&nbsp;&nbsp;&nbsp;";
	  			inner+=tempArray[key].gradedesc
	  		}
	  	}else{
	  		if(tempArray[i].subsys_id!="undefined" && tempArray[i].subsys_id=='35')
	  		{
	  			if(tempArray[key].gradecode){
		  			inner+=" onclick='setValue(\""+p0400+"\",\""+tempArray[key].gradecode.toUpperCase()+"\",\""+per_competencedegree[tempArray[key].gradecode].gradedesc+"\")'  ><td  style='cursor:pointer' class='table_td'  >&nbsp;&nbsp;&nbsp;";
		  			inner+=tempArray[key].gradedesc
		  		}
			}
			else
			{
				if(tempArray[key].gradecode){
					inner+=" onclick='setValue(\""+p0400+"\",\""+tempArray[key].gradecode.toUpperCase()+"\",\""+per_standdegree[tempArray[key].gradecode].gradedesc+"\")'  ><td  style='cursor:pointer' class='table_td'  >&nbsp;&nbsp;&nbsp;";
	 				inner+=tempArray[key].gradedesc
	 			}
			}
	  	}
	  	inner+="&nbsp;&nbsp;&nbsp;</td></tr>";
	  }
	  inner+="</table>";	
	  options.innerHTML=inner;
	  options.innerHTML=options.innerHTML+"<iframe src=\"javascript:false\" style=\"position:absolute; visibility:inherit; top:0px; left:0px;"
 			 				+"width:"+options.offsetWidth+"; height:"+options.offsetHeight+"; " 					    	
 			 				+"z-index:-1;filter='progid:DXImageTransform.Microsoft.Alpha(style=0,opacity=0)';\"></iframe>";
	}
}
function setValue(p0400,value,valuename)
{
	document.getElementsByName("p_"+p0400)[0].value=value;
	document.getElementById("pp"+p0400).innerHTML=valuename;
	Element.hide('options');
}
function closeDiv()
{
	Element.hide('options');
}