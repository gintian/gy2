
    var sub_page=1;
    
    function selectRow(planid,objectid,obj,template_id)
	{	
		if(plan_gather_type=='1')
			return;
		select_objectid=objectid;
		if(ori_obj)
		{
			ori_obj.className=ori_class;
		}
		ori_obj=obj.parentNode;
		ori_class=obj.parentNode.className;
		obj.parentNode.className='selectedBackGroud';		
		
		subHeight=parent.ril_body2.document.body.clientHeight-20;
		subWidth=parent.ril_body2.document.body.clientWidth-15;
		
		parent.ril_body2.location="/performance/implement/performanceImplement.do?br_mainbody=query&objectid="+select_objectid+"&template_id="+template_id+"&sub_page="+sub_page;
//		document.getElementById("desc").src="/performance/implement/performanceImplement.do?b_mainbody=query&objectid="+select_objectid+"&template_id="+template_id+"&opt=1";
		
	}
    
    
     function delBody(plan_id,templateid)
     {
     	document.frames.desc.delmainBody(plan_id,templateid,select_objectid)
     }
    
    
     //数据采集
     function dataGather()
     {
        implementForm.target="il_body";
     	implementForm.action="/performance/implement/dataGather.do?b_query=query&fromUrl=0&plan_id="+document.implementForm.planid.value;     
		implementForm.submit(); 
     }
     
  
     //手工选择考核对象
	 function handSelect(objectType,plan_b0110)
   	 { 
    /*
	   	var right_fields="";
	   	var infor="1";
	   	if(objectType=="1")//团队
	   		infor="3";  	
	  	else if(objectType=="2")//人员
	  		infor="1";
	  	else if(objectType=="3")//单位
	  		infor="2";
	  	else if(objectType=="4")//部门
	  		infor="4";
	  			
	   	var obj_value=handwork_selectObject3(infor,"usr",plan_b0110)
		alert(obj_value);
	  	if(obj_value.length>0)
	   	{
		   	for(var i=0;i<obj_value.length;i++)
		   	{
		   		right_fields+="/"+obj_value[i];		   		
		   	}	
		   	implementForm.str_sql.value=right_fields;	   	
		   	implementForm.action="/performance/implement/performanceImplement.do?b_selectObject=select&opt=handselect&right_fields="+right_fields.substring(1);     
		    implementForm.submit(); 
		 }
		 
		*/  

	var aplanid=document.implementForm.planid.value;
	var opt = 0;
	var infos=new Array();
	infos[0]=aplanid;
	infos[1]=opt;

    var strurl="/performance/handSel.do?b_query=link`planid="+aplanid+"`opt="+opt+"`callBackfunc=handSelect_ok";
	var iframe_url="/gz/gz_accounting/bankdisk/iframe_bank_disk.jsp?src="+$URL.encode(strurl);
    var width=610;
    var height=430;
    if (!window.showModalDialog){
        width=650;
		height=450;
        window.dialogArguments = infos;
    }else{
        //IE 非兼容模式
        if (getBrowseVersion() && !isCompatibleIE()){
            width = 630;
        }
    }
	var config = {
	    width:width,
        height:height,
        dialogArguments:infos,
        type:'2'
    }
    modalDialog.showModalDialogs(iframe_url,null, config,handSelect_ok);
 }
    function handSelect_ok(objList){
        var right_fields="";
        if(objList==null)
            return false;

        if(objList.length>0)
        {
            for(var i=0;i<objList.length;i++)
            {
                right_fields+="/"+objList[i];
            }
            implementForm.str_sql.value=right_fields;
            implementForm.action="/performance/implement/performanceImplement.do?b_selectObject=select&opt=handselect&right_fields="+right_fields.substring(1);
            implementForm.submit();
        }
    }
    
  
    //条件选择考核对象
	function conditionselect()
	{
		//zgd 2015-1-21 链接添加“`selecttype=general”，走通用查询
		var theurl="/performance/implement/kh_object/condition_select.do?b_query=link`db=Usr`selecttype=general`callbackfunc=conditionselect_ok2";
    	var iframe_url="/general/query/common/iframe_query.jsp?src="+$URL.encode(theurl);
    	var width = 560;
    	var height =480;
    	if(isIE6() || !window.showModalDialog){
            width = 570;
    	    height = 500;
    	}
    	var config = {
    	    width:width,
            height:height,
            type:'2'
        }
        modalDialog.showModalDialogs(iframe_url,'template_win',config,conditionselect_ok2);

	}
	function conditionselect_ok2(sql_str){
        if(sql_str!=null)
        {
            implementForm.str_sql.value=sql_str.sql;
            var delFlag=0;

            // 是否删除当前计划中已有的考核对象,已有考核对象时提示是否删除已有对象，否则不提示
            if(parseInt(khObjCount)>0){//这里的大于0这是标记是否需要删除已有的考核对象【39748】
                delFlag=1;
                if(window.Ext && !Ext.isIE && Ext.showConfirm){// 非自助模块条件选人时没有用到Ext，如果没有用到则使用confirm方式 chent 20180327 update
                    Ext.showConfirm(KH_IMPLEMENT_INF12, function(flag){
                        if(flag=="yes") {
                            if(parseInt(khObjCount)>0){//这里的大于0这是标记是否需要删除已有的考核对象【39748】
                                delFlag=1;
                            }
                            implementForm.action="/performance/implement/performanceImplement.do?b_condiSelObj=link&opt=conditionselect&delFlag="+delFlag;
                            implementForm.submit();
                        }
                    });
                } else {
                    var r = confirm(KH_IMPLEMENT_INF12);
                    if (r == true){
                        if(parseInt(khObjCount)>0)//这里的大于0这是标记是否需要删除已有的考核对象【39748】
                            delFlag=1;

                        implementForm.action="/performance/implement/performanceImplement.do?b_condiSelObj=link&opt=conditionselect&delFlag="+delFlag;
                        implementForm.submit();
                    }
                }
            }else{
                implementForm.action="/performance/implement/performanceImplement.do?b_condiSelObj=link&opt=conditionselect&delFlag="+delFlag;
                implementForm.submit();
            }

        }
    }
	//清除分数
	function valuedelete()
     {		
     	var theurl="/performance/implement/performanceImplement.do?b_eliminate=link`planid="+document.getElementById("planid").value;
     	var iframe_url="/general/query/common/iframe_query.jsp?src="+$URL.encode(theurl);
     	modalDialog.showModalDialogs(iframe_url,'glWin',{width:340,height:400,id:'valuedeleteWin',title:"清除分数"});
     }
    
    
    function changePlanID()
	{
		document.implementForm.action="/performance/implement/performanceImplement.do?b_query=query&operate=init0";
		document.implementForm.submit();
	
	}
   //暂停计划
   function pause()
	{
		if(implementForm.planid.value=='')
		{
			return;
		}
	
		if(confirm(P_I_INFO3+"?"))
		{
			document.implementForm.action="/performance/implement/performanceImplement.do?b_startPause=query&opt=pause&code="+orgCode;
			document.implementForm.submit();
		}
	}
	//移动记录
	function moveRecord(object_id,move,code,codeset)
	{
		var hashvo=new ParameterSet();
		hashvo.setValue("plan_id",implementForm.planid.value);
		hashvo.setValue("object_id",object_id);
		hashvo.setValue("move",move);
		hashvo.setValue("code",code);
		hashvo.setValue("codeset",codeset);
		hashvo.setValue("opt","12");
		var request=new Request({method:'post',asynchronous:false,onSuccess:moveRecordResult,functionId:'9023000003'},hashvo);
	}
	function moveRecordResult(outparamters)
	{	
		document.implementForm.action="/performance/implement/performanceImplement.do?b_query=query&operate=init&code="+orgCode;
		document.implementForm.submit();
	}
   //分发
   function distributePlan()
   {	
		if(confirm(KH_RELATION_INFO6))
		{
			var hashvo=new ParameterSet();
			hashvo.setValue("plan_id",implementForm.planid.value);
			var request=new Request({method:'post',asynchronous:false,onSuccess:distributePlan2,functionId:'90100140016'},hashvo);
		}
   }	
	
	function distributePlan2(outparamters)
	{
		var flag=outparamters.getValue("flag");
		var info=outparamters.getValue("info");
		var isSendEmail = outparamters.getValue("isSendEmail");
		var pending_system = outparamters.getValue("pending_system");  // 是否设置待办系统
		
		if(flag=='0')
			alert(getDecodeStr(info));
		else
		{
			//分发时给考核对象或团队负责人发送待办任务 2011.06.10 JinChunhai   由于改为推送到待办表 放开这里 zhaoxg add 
			//if(pending_system!=null && pending_system=='yes')
				disOrStartWaitTask('distribute','noApprove');
				
			var info_email='';
 		    if(khobjtype=='2')
 				info_email=KH_IMPLEMENT_INFO3;
 			else if(khobjtype!='2')
 				info_email=KH_IMPLEMENT_INFO9;		
		
			if(isSendEmail=='1')
			{
				if(confirm(info_email))
				{
					var hashvo=new ParameterSet();
					hashvo.setValue("plan_id",implementForm.planid.value);
					hashvo.setValue("oper",'distribute');
					var request=new Request({method:'post',asynchronous:false,onSuccess:distributeEmail,functionId:'9023000023'},hashvo);
				}else
				{
					document.implementForm.action="/performance/implement/performanceImplement.do?b_startPause=query&opt=distribute";
					document.implementForm.submit();
				}
			}else
			{
					document.implementForm.action="/performance/implement/performanceImplement.do?b_startPause=query&opt=distribute";
					document.implementForm.submit();
			}						    
		}		
	}
   
   	function distributeEmail(outparamters)
	{
		var isnull=outparamters.getValue("isnull");
		var name = outparamters.getValue("names");
		if(isnull=='yes'){
		    var win=open("/servlet/vfsservlet?fromjavafolder=true&fileid="+name,"txt");
			alert(KH_IMPLEMENT_INFO11);
			document.implementForm.action="/performance/implement/performanceImplement.do?b_startPause=query&opt=distribute";
			document.implementForm.submit();
		}else{
		    alert(KH_IMPLEMENT_INFO8);
		    document.implementForm.action="/performance/implement/performanceImplement.do?b_startPause=query&opt=distribute";
			document.implementForm.submit();
		}
	}



    //定义等级分类
    function defineDegree(planid,busitype)
    {
        var theurl="/performance/options/perDegreeList.do?b_query2=link`planid="+planid+"`busitype="+busitype;
        var iframe_url="/general/query/common/iframe_query.jsp?src="+$URL.encode(theurl);
        var width = window.screen.availWidth;
        var height = window.screen.availHeight;
        if (!window.showModalDialog){
            width=window.screen.availWidth-50;
            height=window.screen.availHeight-100;
        }
        var config = {
            width:width,
            height:height,
            type:'2'
        }
        modalDialog.showModalDialogs(iframe_url,"template_win",config,defineDegree_ok);
    }
    function defineDegree_ok(){
        document.implementForm.action="/performance/implement/performanceImplement.do?b_showGrade=link";
        document.implementForm.submit();
    }

   	
   //启动计划(打分)
	function startPlan2()
	{
		if(confirm(P_I_INFO1))
		{
			var theurl="/performance/implement/performanceImplement.do?b_showGrade=link`callBackFunc=changeDegreeCallBackfunc2";
	    	var iframe_url="/general/query/common/iframe_query.jsp?src="+$URL.encode(theurl);
	   		var config = {
	   		    width:470,
                height:320,
                type:'2'
            }
            modalDialog.showModalDialogs(iframe_url,'template_win',config,changeDegreeCallBackfunc2);
		}
	}
	function changeDegreeCallBackfunc2(degree){
        if(typeof(degree)!='undefined' && degree.length>0)
        {
            //启动时 360计划时给考核主体发送待办任务；目标计划时给考核对象或团队负责人发送待办任务 2011.06.10 JinChunhai
            disOrStartWaitTask('start','noApprove');

            document.implementForm.action="/performance/implement/performanceImplement.do?b_startPause=query&desc="+document.implementForm.date_box.value+"&opt=start&degree="+degree;
            document.implementForm.submit();
        }
    }
	//启动前的验证
	function testBeforeStart(startFlag)
	{
		var hashvo=new ParameterSet();
		hashvo.setValue("plan_id",implementForm.planid.value);
		hashvo.setValue("plan_status",implementForm.planStatus.value);
		hashvo.setValue("startFlag",startFlag);
		var request=new Request({method:'post',asynchronous:false,onSuccess:testBeforeStart2,functionId:'90100140017'},hashvo);
	}
	function testBeforeStart2(outparamters)
	{
	    window.tempObj = {};
        window.tempObj.logo = false;
		var isStart = false;
		var flag=outparamters.getValue("flag");
		var sporpfSeq=outparamters.getValue("sporpfSeq");
		var info=outparamters.getValue("info");
        window.tempObj.gather_type =outparamters.getValue("gather_type");  //0 网上 1 机读 2:网上+机读
        window.tempObj.startFlag =outparamters.getValue("startFlag");
        window.tempObj.isEmail =outparamters.getValue("isEmail");  // 是否设置发送邮件参数
        window.tempObj.isHaveTeamer =outparamters.getValue("isHaveTeamer"); // 是否设置团队负责人
        window.tempObj.pending_system = outparamters.getValue("pending_system"); // 是否设置待办系统
        window.tempObj.plan_ids = outparamters.getValue("plan_ids");  // 需全部启动的计划id
        window.tempObj.planId_s = outparamters.getValue("planId_s");  // 需启动发邮件的计划id
		var ainfo=getDecodeStr(info);
		var arguments=new Array();
		arguments[1]="有目标卡未被批准";
		arguments[0]=ainfo;
		arguments[2]=sporpfSeq;
		if(flag=='0')//由发布到启动的检查
		{
			alert(ainfo);
			isStart = false;
		}
		else if(flag=='2')//由发布到启动的检查 需要用弹出窗口来展示
		{
			var strurl="/performance/implement/performanceImplement.do?br_showInfo=link`callBackFunc=testBeforeStart2_canstartplan_ok";
	   	    var iframe_url="/general/query/common/iframe_query.jsp?src="+$URL.encode(strurl);
			if (!window.showModalDialog){
                window.dialogArguments=arguments;
            }
			var config = {
			    width:490,
                height:440,
                dialogArguments:arguments,
                type:'2'
            }
            modalDialog.showModalDialogs(iframe_url,null, config,testBeforeStart2_canstartplan_ok);
			// if(typeof(ss)!='undefined'&&ss==1)
		}else if(flag=='1') //flag=1是通过验证
			isStart= true;		
		else if(flag=='3')
		{
			if(confirm(ainfo))
				isStart= true;
			else
				isStart= false;
		}	
        if (isStart){
            testBeforeStart2_canstartplan();
        }
	}
	function testBeforeStart2_canstartplan_ok(return_vo){
        if(return_vo && return_vo.ok==1)
        {
            implementForm.noApproveTargetCanScore.value=return_vo.noApproveTargetCanScore;
            testBeforeStart2_canstartplan();

        }
    }
	function testBeforeStart2_canstartplan(){
        var theurl="/performance/implement/performanceImplement.do?b_showGrade=link`callBackFunc=testBeforeStart2_showGrade";
        var iframe_url="/general/query/common/iframe_query.jsp?src="+$URL.encode(theurl);
        var config = {
            width:470,
            height:300,
			title:'选择等级分类',
			id:'showGradeWin'
        }
        modalDialog.showModalDialogs(iframe_url,'template_win_new',config,testBeforeStart2_showGrade);

    }
    function testBeforeStart2_showGrade(degree){
        if(typeof(degree)!='undefined'&&degree.length>0)
        {
            //启动时 360计划时给考核主体发送待办任务；目标计划时给考核对象或团队负责人发送待办任务 2011.06.10 JinChunhai
            if(window.tempObj.pending_system!=null && window.tempObj.pending_system=='yes'
                && (window.tempObj.plan_ids!=null && window.tempObj.plan_ids.length>0
                    && window.tempObj.plan_ids!='undefined'))
                disOrStartWaitTask('start',implementForm.noApproveTargetCanScore.value,window.tempObj.startFlag);

            var info_email='';
            if(khmehtod=='1')
                info_email=KH_IMPLEMENT_INFO7;
            else if(khmehtod=='2' && khobjtype=='2')
                info_email="要发送电子邮件通知吗？";
            else if(khmehtod=='2' && khobjtype!='2' && window.tempObj.isHaveTeamer=='1')
                info_email="要发送电子邮件通知吗？";
            else if(khmehtod=='2' && khobjtype!='2' && window.tempObj.isHaveTeamer=='0')
                logo = true;
            if(window.tempObj.isEmail=='1' && (window.tempObj.planId_s!=null
                    && window.tempObj.planId_s.length>0 && window.tempObj.planId_s!='undefined'))
            {
                if(window.tempObj.startFlag!='result')//打分方式启动才发邮件 录入结果方式启动就不发邮件了
                {
                    if((window.tempObj.gather_type!=null && window.tempObj.gather_type.length>0) && (window.tempObj.gather_type=='1'))  // 360计划为机读时不发邮件 JinChunhai  2011.07.20
                    {
                        document.implementForm.action="/performance/implement/performanceImplement.do?b_startPause=query&desc="+window.tempObj.startFlag+"&opt=start&degree="+degree+"&code="+orgCode;
                        document.implementForm.submit();

                    }else
                    {
                        if(window.tempObj.logo)
                        {
                            document.implementForm.action="/performance/implement/performanceImplement.do?b_startPause=query&desc="+window.tempObj.startFlag+"&opt=start&degree="+degree+"&code="+orgCode;
                            document.implementForm.submit();
                        }else
                        {
                            if(confirm(info_email))
                            {
                                var hashvo=new ParameterSet();
                                hashvo.setValue("plan_id",implementForm.planid.value);
                                hashvo.setValue("oper",'start');
                                hashvo.setValue("startFlag",window.tempObj.startFlag);
                                hashvo.setValue("degree",degree);
                                hashvo.setValue("noApproveTargetCanScore",implementForm.noApproveTargetCanScore.value);
                                var request=new Request({method:'post',asynchronous:false,onSuccess:startEmail,functionId:'9023000023'},hashvo);
                            }else
                            {
                                document.implementForm.action="/performance/implement/performanceImplement.do?b_startPause=query&desc="+window.tempObj.startFlag+"&opt=start&degree="+degree+"&code="+orgCode;
                                document.implementForm.submit();
                            }
                        }
                    }
                }else
                {
                    document.implementForm.action="/performance/implement/performanceImplement.do?b_startPause=query&desc="+window.tempObj.startFlag+"&opt=start&degree="+degree+"&code="+orgCode;
                    document.implementForm.submit();
                }
            }else
            {
                document.implementForm.action="/performance/implement/performanceImplement.do?b_startPause=query&desc="+window.tempObj.startFlag+"&opt=start&degree="+degree+"&code="+orgCode;
                document.implementForm.submit();
            }
        }
    }

	function startEmail(outparamters)
	{
	    var isnull=outparamters.getValue("isnull");
		var resultFlag = outparamters.getValue("resultFlag");
		var startFlag = outparamters.getValue("startFlag");
		var degree = outparamters.getValue("degree");
		var name = outparamters.getValue("names");
		if(isnull=='yes'){
		    var win=open("/servlet/vfsservlet?fromjavafolder=true&fileid="+name,"txt");
			if(resultFlag=='1')
			{
				alert(KH_IMPLEMENT_INFO8);
				document.implementForm.action="/performance/implement/performanceImplement.do?b_startPause=query&desc="+startFlag+"&opt=start&degree="+degree+"&code="+orgCode;
				document.implementForm.submit();
			}
		}else{
		    if(resultFlag=='1')
			{
				alert(KH_IMPLEMENT_INFO8);
				document.implementForm.action="/performance/implement/performanceImplement.do?b_startPause=query&desc="+startFlag+"&opt=start&degree="+degree+"&code="+orgCode;
				document.implementForm.submit();
			}
		}
	}
	
	//启动计划
	function setSelectValue(planMethod,planStatus)
	{
		var startFlag = document.implementForm.date_box.value;
		document.implementForm.date_box.value='none';
		var desc=P_I_INFO1;
		if(startFlag=="result")
			desc=P_I_INFO2;
		
		if(confirm(desc))
		{
			if(planMethod=='2' && planStatus=='8' && startFlag=="result")
				alert(P_I_INF19);
			else
				testBeforeStart(startFlag);	
		}				
	}
	function startPlan(startFlag)
	{
		var desc=P_I_INFO1;
		if(startFlag=="result")
			desc=P_I_INFO2;
		if(confirm(desc))
		{
			if(khmehtod=='2' && planStatus=='8' && startFlag=="result")
				alert(P_I_INF19);
			else
				testBeforeStart(startFlag);
		}
	}
	//打分状态
	function showScoreState()
	{
		window.open('/performance/markStatus/markStatusList.do?b_search=link&model=0','_blank');
	}

//删除考核对象
function delObjects()
{
		var objs=eval("document.implementForm.objectIDs");
		var objectIDs="";
		if(objs)
		{
		
			if(objs.length)
			{
				for(var i=0;i<objs.length;i++)
				{
					if(objs[i].checked==true)
						objectIDs+="`"+objs[i].value;	
				}
			}
			else
			{
				if(objs.checked==true)
						objectIDs+="`"+objs.value;	
			}
		}
	
		if(objectIDs=="")
		{
				alert(PLEASESELOBJ);
				return;
		}
		if(confirm("确认删除所选考核对象？"))
		{
			document.implementForm.action="/performance/implement/performanceImplement.do?b_delobject=del&code="+orgCode;
			document.implementForm.submit();
		}

}
//批量设置考核对象类别
function batchSetObjType()
{
	/*
		var objs=eval("document.implementForm.objectIDs");
		var objectIDs="";
		if(objs)
		{		
			if(objs.length)
			{
				for(var i=0;i<objs.length;i++)
				{
					if(objs[i].checked==true)
						objectIDs+="`"+objs[i].value;	
				}
			}
			else
			{
				if(objs.checked==true)
						objectIDs+="`"+objs.value;	
			}
		}	
		if(objectIDs=="")
		{
			alert(SELECT_KHOBJ);
			return;
		}
		*/
		
		var target_url="/performance/implement/performanceImplement.do?br_showObjType=link";
 		var iframe_url="/general/query/common/iframe_query.jsp?src="+target_url;

        var width = 400;
        var height =200;
        if(isIE6() || !window.showModalDialog){
            width = 425;
            height = 220;
        }
        var config = {
            width:width,
            height:height,
            type:'2'
        }
    modalDialog.showModalDialogs(iframe_url,'',config,batchSetObjType_ok);
}
    function batchSetObjType_ok(return_vo){
        if(!return_vo)
            return false;
        if(return_vo.flag=="true")
        {
            document.implementForm.action="/performance/implement/performanceImplement.do?b_batchSetObjType=link&objTypeId="+return_vo.objTypeId+"&operate=init&code="+orgCode;
            document.implementForm.submit();
        }
    }
	//考核主体指标权限划分
	function pointpowerset(template_id)
	{
		var objs=eval("document.implementForm.objectIDs");
		var objectIDs="";
		if(objs)
		{
		
			if(objs.length)
			{
				for(var i=0;i<objs.length;i++)
				{
					if(objs[i].checked==true)
						objectIDs+="@"+objs[i].value;	
				}
			}
			else
			{
				if(objs.checked==true)
						objectIDs+="@"+objs.value;	
			}
		}
		if(objectIDs=="")
		{
				alert(SELECT_COLS);
				return;
		}
		var hashvo=new ParameterSet();
		hashvo.setValue("plan_id",implementForm.planid.value);
		hashvo.setValue("objectIDs",objectIDs);
		hashvo.setValue("template_id",template_id);
		var request=new Request({method:'post',asynchronous:false,onSuccess:pointpowerset2,functionId:'90100140015'},hashvo);
	}
	
	
	function pointpowerset2(outparamters)
	{
		var flag=outparamters.getValue("flag");
		//if(flag=='1')
		//{
		//	alert("所选考核对象没有设置主体!");
		//}
		//else
		//{
			var objectIDs=outparamters.getValue("objectIDs");
			var template_id = outparamters.getValue("template_id");
			var planid = implementForm.planid.value;
			var theurl="/performance/implement/kh_mainbody/pointpowerset.do?b_query=link`objIds="+objectIDs+"`plan_id="+planid+"`act=init";
	    	var iframe_url="/general/query/common/iframe_query.jsp?src="+$URL.encode(theurl);
	   		var resultVo= window.showModalDialog(iframe_url, 'pointpowerset_win', 
	      				"dialogWidth:550px; dialogHeight:500px;resizable:no;center:yes;scroll:yes;status:no");
	       parent.ril_body2.location="/performance/implement/performanceImplement.do?br_mainbody=query&objectid="+select_objectid+"&template_id="+template_id;
		//}
	}
	//考核主体指标权限划分修改
	function powerset(template_id,power_type)
	{
	    window.tempObj = {};
        window.tempObj.template_id =template_id;
		var objs=eval("document.implementForm.objectIDs");
		var objectIDs="";
		if(objs)
		{		
			if(objs.length)
			{
				for(var i=0;i<objs.length;i++)
				{
					if(objs[i].checked==true)
						objectIDs+="@"+objs[i].value;	
				}
			}
			else
			{
				if(objs.checked==true)
					objectIDs+="@"+objs.value;	
			}
		}

		var planid = implementForm.planid.value;
		var theurl="/performance/implement/kh_mainbody/powerset.do?b_query=link`objIds="+objectIDs+"`plan_id="+planid+"`act=init`power_type="+power_type;
	    var iframe_url="/general/query/common/iframe_query.jsp?src="+$URL.encode(theurl);
	   	var config = {
	   	    width:660,
            height:560,
            type:'2'
        }
	   	modalDialog.showModalDialogs(iframe_url,'powerset_win',config,powerset_ok)
	}
	function powerset_ok(){
        parent.ril_body2.location="/performance/implement/performanceImplement.do?br_mainbody=query&objectid="+select_objectid+"&template_id="+window.tempObj.template_id;
    }

//设置考核对象类型
function setObjType(object_id,plan_id,obj)
{
	var hashvo=new ParameterSet();
	hashvo.setValue("object_id",object_id);
	hashvo.setValue("plan_id",plan_id);
	hashvo.setValue("typeid",obj.value);
	hashvo.setValue("opt","1");
	var request=new Request({method:'post',asynchronous:false,onSuccess:setObjSuccess,functionId:'9023000003'},hashvo);
}
function setObjSuccess(parameters){
    document.implementForm.action="/performance/implement/performanceImplement.do?b_query=query";
	document.implementForm.submit();
}
//设置考核关系
function setKhRelation(object_id,plan_id,obj)
{	
	//if(confirm(KH_RELATION_INFO5)){
	var hashvo=new ParameterSet();
	hashvo.setValue("object_id",object_id);
	hashvo.setValue("plan_id",plan_id);
	hashvo.setValue("kh_relations",obj.value);
	hashvo.setValue("opt","7");
	var request=new Request({method:'post',asynchronous:false,functionId:'9023000003'},hashvo);
	//}
}


//批量设置考核对象类别
function batchSetObjKhRelations(plan_id)
{
		if (!confirm("批量设置考核关系将会清空对象的考核结果，确认继续吗？")) {
			return;
		}
		window.tempObj = {};
        window.tempObj.plan_id=plan_id;
		var target_url="/performance/implement/performanceImplement.do?br_showObjKhRelation=link";
 		var iframe_url="/general/query/common/iframe_query.jsp?src="+$URL.encode(target_url);

        var width = 400;
        var height =200;
        if(!window.showModalDialog){
            width = 425;
            height = 220;
        }
        var config = {
            width:width,
            height:height,
            type:'1',
            title:"批量设置考核关系",
            id:'batchSetObjKhRelationsWin'
        }
        modalDialog.showModalDialogs(iframe_url,'',config,batchSetObjKhRelations_ok);
}
function batchSetObjKhRelations_ok(return_vo){
    if(!return_vo)
        return false;
    if(return_vo.flag=="true")
    {
        if (!confirm("【注意】\n批量设置考核关系将会清空对象的考核结果，请慎重选择。\n确认继续吗？")) {
            return;
        }
        var hashvo=new ParameterSet();
        hashvo.setValue("plan_id",window.tempObj.plan_id);
        hashvo.setValue("kh_relations",return_vo.objKhRelation);
        hashvo.setValue("opt","32");
        var request=new Request({method:'post',asynchronous:false,onSuccess:refreshPage,functionId:'9023000003'},hashvo);
        //document.implementForm.action="/performance/implement/performanceImplement.do?b_batchSetObjType=link&objTypeId="+return_vo.objTypeId+"&operate=init&code="+orgCode;
        //document.implementForm.submit();
        window.tempObj = undefined;
    }
}
function refreshPage(outparameters)
{
	//window.location.href=window.location.href;
	document.implementForm.action="/performance/implement/performanceImplement.do?b_query=query&operate=init0";
	document.implementForm.submit();
}
//设置考核主体必打分
function setBodyMustScore(object_id,mainbody_id,plan_id,obj)
{
	var hashvo=new ParameterSet();
	hashvo.setValue("object_id",object_id);
	hashvo.setValue("plan_id",plan_id);
	hashvo.setValue("mainbody_id",mainbody_id);
	if(obj.checked)
		hashvo.setValue("fillctrl","1");
	else 
	    hashvo.setValue("fillctrl","0");
	hashvo.setValue("opt","2");
	var request=new Request({method:'post',asynchronous:false,functionId:'9023000003'},hashvo);
}
function selectall(obj)
{
	var temp=document.getElementsByName("must");
	var str="";
	for(var i=0;i<temp.length;i++){
		str+=temp[i].value+"|";
	}
	var hashvo=new ParameterSet();
	hashvo.setValue("str",str);
	if(obj.checked)
		hashvo.setValue("fillctrl","1");
	else 
	    hashvo.setValue("fillctrl","0");
	hashvo.setValue("opt","33");
	var request=new Request({method:'post',asynchronous:false,functionId:'9023000003'},hashvo);
}
//全选/全撤指标权限JinChunhai加
function selectAll(theFlag)
{
//	var objs=eval("document.implementForm.pointPriv");
	var temp=document.getElementsByTagName("input");
	var objs=document.getElementsByName("pointPriv");
  	var item_ids=new Array();
  	var j=0;
  	for(var i=0;i<temp.length;i++)
	{	
  	if(temp[i].type=="checkbox"&&temp[i].name.substring(0,9)=="pointPriv")
	{	
				if(theFlag=='1')
					temp[i].checked=true;

				else
					temp[i].checked=false;

				item_ids[j]=temp[i].value;	
				j++;
				
	}else if(temp[i].type=="checkbox"&&temp[i].name.substring(0,10)=="_pointPriv")
	{	
				if(theFlag=='1')
					temp[i].checked=true;

				else
					temp[i].checked=false;			
	}
	}
	var hashvo=new ParameterSet();
	hashvo.setValue("item_ids",item_ids);
	if(theFlag=='1')
		hashvo.setValue("value","1");
	else 
	    hashvo.setValue("value","0");
	hashvo.setValue("opt","3");
	var request=new Request({method:'post',asynchronous:false,functionId:'9023000003'},hashvo);
	
}
//设置指标权限
function setPointPriv(mainbodyid,objectid,pointid,planid,obj)
{
	var hashvo=new ParameterSet();
	hashvo.setValue("object_id",objectid);
	hashvo.setValue("plan_id",planid);
	hashvo.setValue("mainbody_id",mainbodyid);
	hashvo.setValue("pointid",pointid);
	if(obj.checked)
		hashvo.setValue("value","1");
	else 
	    hashvo.setValue("value","0");
	hashvo.setValue("opt","3");
	var request=new Request({method:'post',asynchronous:false,functionId:'9023000003'},hashvo);
	
}
function setAllPointPriv(obj,name)
{
	var temp=document.getElementsByName(name);
	if(temp.length=='0'){
		return;
	}
	var str="";
	for(var i=0;i<temp.length;i++){
		str+=temp[i].value+"|";
	}
	var hashvo=new ParameterSet();
	hashvo.setValue("str",str);

	if(obj.checked)
		hashvo.setValue("value","1");
	else 
	    hashvo.setValue("value","0");
	hashvo.setValue("opt","35");
	var request=new Request({method:'post',asynchronous:false,functionId:'9023000003'},hashvo);
	
}
//全选/全撤项目权限JinChunhai加
function batch_selectAll(theFlag)
{


	var temp=document.getElementsByTagName("input");
	var objs=document.getElementsByName("pointPriv");
  	var item_ids=new Array();
  	var j=0;
  	for(var i=0;i<temp.length;i++)
	{	
  	if(temp[i].type=="checkbox"&&temp[i].name.substring(0,8)=="itemPriv")
	{	
				if(theFlag=='1')
					temp[i].checked=true;

				else
					temp[i].checked=false;

				item_ids[j]=temp[i].value;	
				j++;
				
	}else  if(temp[i].type=="checkbox"&&temp[i].name.substring(0,9)=="_itemPriv")
	{	
				if(theFlag=='1')
					temp[i].checked=true;

				else
					temp[i].checked=false;				
	}
	}

	var hashvo=new ParameterSet();
	hashvo.setValue("item_ids",item_ids);
	if(theFlag=='1')
		hashvo.setValue("item_value","1");
	else 
	    hashvo.setValue("item_value","0");
	hashvo.setValue("opt","8");
	var request=new Request({method:'post',asynchronous:false,functionId:'9023000003'},hashvo);
	
}
//设置项目权限
function setItemPriv(objectid,mainbodyid,item_id,planid,obj)
{
	var hashvo=new ParameterSet();
	hashvo.setValue("object_id",objectid);
	hashvo.setValue("plan_id",planid);
	hashvo.setValue("body_id",mainbodyid);
	hashvo.setValue("item_id",item_id);
	if(obj.checked)
		hashvo.setValue("item_value","1");
	else 
	    hashvo.setValue("item_value","0");
	hashvo.setValue("opt","8");
	var request=new Request({method:'post',asynchronous:false,functionId:'9023000003'},hashvo);
	
}
function AllitemPriv(obj,name)
{
	var temp=document.getElementsByName(name);
	if(temp.length=="0"){
		return;
	}
	var str="";
	for(var i=0;i<temp.length;i++){
		str+=temp[i].value+"|";
	}
	var hashvo=new ParameterSet();
	hashvo.setValue("str",str);
	if(obj.checked)
		hashvo.setValue("item_value","1");
	else 
	    hashvo.setValue("item_value","0");
	hashvo.setValue("opt","34");
	var request=new Request({method:'post',asynchronous:false,functionId:'9023000003'},hashvo);
	
}
function recoverPrivAll(plan_id,objectId,power_type)
{
	if(objectId!='all')
		recoverPriv2(plan_id,objectId,power_type);
	else 
	{
		var objSel = $('object_id');
		for(i=0,j=0;i<objSel.options.length;i++)
  		{
  			var val = objSel.options[i].value;
  			if(val=='all')
  				continue;
  			else
  				recoverPriv2(plan_id,val,power_type);  			
  		}
	}
}
//恢复默认指标权限
function recoverPriv2(plan_id,objectId,power_type)
{
	//alert(plan_id+"--"+objectId);
	var hashvo=new ParameterSet();
	hashvo.setValue("object_id",objectId);
	hashvo.setValue("plan_id",plan_id);
	hashvo.setValue("power_type",power_type);
	hashvo.setValue("opt","4");
	var request=new Request({method:'post',asynchronous:false,onSuccess:recoverPrivOk2,functionId:'9023000003'},hashvo);
}
function recoverPrivOk2(outparamters)
{
   var power_type=outparamters.getValue("power_type");
   var objs;
   if(power_type=='point')
   	  objs=eval("document.implementForm.pointPriv");
   else if(power_type=='item')
   	  objs=eval("document.implementForm.itemPriv");
   if(objs)
   {
   		for(var i=0;i<objs.length;i++)
   			objs[i].checked=true;
   }
}
/*恢复默认指标/项目权限*/
function recoverPriv(plan_id,power_type)
{
	var objs = selObjs();
	if(objs=="")
	{
		alert(SELECT_KHOBJ);
		return;
	} 
	var hashvo=new ParameterSet();
	hashvo.setValue("objs",objs);
	hashvo.setValue("power_type",power_type);
	hashvo.setValue("plan_id",plan_id);
	hashvo.setValue("opt","4");
	var request=new Request({method:'post',asynchronous:false,onSuccess:recoverPrivOk,functionId:'9023000003'},hashvo);
}

function recoverPrivOk(outparamters)
{
  	//document.getElementById("desc").src=document.getElementById("desc").src;
  	parent.ril_body2.location=parent.ril_body2.location;
}

function delOk(outparamters)
{
	document.implementForm.action="/performance/implement/performanceImplement.do?b_mainbody=query&objectid="+outparamters.getValue("object_id")+"&template_id="+outparamters.getValue("templateid")+"&opt=1";
	document.implementForm.submit();
}

 //打分状态
 function showScoreStatus(plan_id,busitype)
 {
     var w=800;
     var h=600;
     var top= window.screen.availHeight-h>0?window.screen.availHeight-h:0;
     var left= window.screen.availWidth-w>0?window.screen.availWidth-w:0;
     top = top/2;
     left = left/2;
 		window.open("/performance/markStatus/markStatusList.do?b_search=link&consoleType=1&model=0&busitype="+busitype+"&checkPlanId="+plan_id,"newwin",
            "width="+w+",height="+h+",top="+top+",left="+left+",resizable=0,scrollbars=1,status=0,location=0,toolbar=0");
     //	document.implementForm.action="/performance/markStatus/markStatusList.do?b_search=link&model=0";
     //	document.implementForm.submit();
 }
     

function showDateSelectBox(srcobj)
{
      
      date_desc=srcobj;      
      Element.show('date_panel');   
      var pos=getAbsPosition(srcobj);
      if(pos[1]>50)
      	pos[1]=35;
	  with($('date_panel'))
	  {
	        style.position="absolute";
    		style.posLeft=pos[0]-1;
			style.posTop=pos[1]-1+srcobj.offsetHeight;
			style.width=110;
      }                 
      
  }
//设置动态主体权重
function setdynamainbodypropotion(planid)
{
	var theurl="/performance/implement/kh_mainbody/set_dyna_main_rank/setdynamainbodypropotion.do?b_ini=link`optString=3`planid="+planid;
    var iframe_url="/general/query/common/iframe_query.jsp?src="+$URL.encode(theurl);
   	var config = {
   	    width:750,
        height:450,
        type:'2'
    }
    modalDialog.showModalDialogs(iframe_url,null, config);
}
//目标卡制订
function target_card_set()
{
	var theurl="/performance/implement/performanceImplement.do?br_targetCardSet=link";
    var iframe_url="/general/query/common/iframe_query.jsp?src="+theurl;

    var width = window.screen.availWidth;
    var height = window.screen.availHeight-30;
    if (!window.showModalDialog){
        width=window.screen.availWidth-100;
        height=window.screen.availHeight-100;
    }
    var config = {
        width:width,
        height:height,
        type:'2'
    }
    modalDialog.showModalDialogs(iframe_url,null, config);
}
//设置动态指标权重
function setdynatargetpropotion()
{
	var theurl="/performance/implement/kh_object/set_dyna_target_rank/setdynatargetpropotion.do?b_ini=link`optString=4";
    var iframe_url="/general/query/common/iframe_query.jsp?src="+$URL.encode(theurl);
    var width = 750;
    var height = 450;
    var config = {
        width:width,
        height:height,
        type:'2'
    }
    modalDialog.showModalDialogs(iframe_url,null, config);
}
  
// 设置主体权重
function Weight()
{
 	var planid = implementForm.planid.value;
 	var weighturl="/performance/implement/performanceImplement.do?b_weight=link`planid="+planid;
 	var iframe_url="/general/query/common/iframe_query.jsp?src="+$URL.encode(weighturl);
 	var width = 500;
 	var height = 300;
    if(isIE6() || !window.showModalDialog){
        width = 520;
        height = 310;
    }
    var config = {
        width:width,
        height:height,
        type:'2'
    }
    modalDialog.showModalDialogs(iframe_url,'pointpowerset_win',config,Weight_ok);

}
   function Weight_ok(resultVo){
       if(resultVo!=null)
       {
           reflesh();
       }
   }
// 设置主体评分范围
function gradeScope()
{
  	var hashvo=new ParameterSet();
	hashvo.setValue("planid",implementForm.planid.value);
	hashvo.setValue("opt",'19');
	hashvo.setValue("planStatus",planStatus);
	var request=new Request({method:'post',onSuccess:isGradeScope,functionId:'9023000003'},hashvo);
}  
function isGradeScope(outparamters)
{
    var canSetDynaItem=outparamters.getValue("canSetDynaItem");  
    var planid=outparamters.getValue("planid");   
    var planStatus=outparamters.getValue("planStatus");   
    if(canSetDynaItem=='true')
	{
	 	var gradeurl="/performance/implement/performanceImplement.do?b_gradeScope=link`planid="+planid+"`planStatus="+planStatus;
 		var iframe_url="/general/query/common/iframe_query.jsp?src="+$URL.encode(gradeurl);
        var width = 500;
        var height = 300;
        var config = {
            width:width,
            height:height,
            type:'2'//open 方式打开
        };
        modalDialog.showModalDialogs(iframe_url,"pointpowerset_win",config,aotoMainBodySel_ok);
	}else
    	alert('请设置考核对象类别！');
} 

 //清除考核主体
 function cleanMainBody(templateID)
 {
 	var objs=eval("document.implementForm.objectIDs");
	var objectIDs="";
	if(objs)
	{
		if(objs.length)
		{
			for(var i=0;i<objs.length;i++)
			{
				if(objs[i].checked==true)
					objectIDs+="@"+objs[i].value;	
			}
		}
		else
		{
			if(objs.checked==true)
					objectIDs+="@"+objs.value;	
		}
	}
	if(objectIDs=="")
	{
			alert(SELECT_KHOBJ);
			return;
	}
	if(confirm("确认清除所选对象的主体信息吗?"))
	{
		parent.ril_body2.location="/performance/implement/performanceImplement.do?b_clearBody=link&operate=init&deletestr="+objectIDs+"&objectid="+select_objectid+"&template_id="+templateID+"&code="+orgCode;
		 //parent.ril_body2.location="/performance/implement/performanceImplement.do?br_mainbody=query&objectid="+select_objectid+"&template_id="+template_id;
		//document.implementForm.submit();
	}
 }
 //设置动态项目分值/权重
  function setdynaitem()
  {
  	var hashvo=new ParameterSet();
	hashvo.setValue("planid",implementForm.planid.value);
	hashvo.setValue("opt",'19');
	var request=new Request({method:'post',onSuccess:isCanSetDynaItem,functionId:'9023000003'},hashvo);
  }
  
 function isCanSetDynaItem(outparamters)
 {
    var canSetDynaItem=outparamters.getValue("canSetDynaItem");
    
    if(canSetDynaItem=='true')
	{
	 	var target_url="/performance/implement/kh_object/dynaitem.do?br_query=link";
 		var iframe_url="/general/query/common/iframe_query.jsp?src="+target_url;
		var config = {width:900,height:600,type:2};
		modalDialog.showModalDialogs(iframe_url,"",config);
	}else
    	alert('该考核计划没有设置考核对象或者考核对象没有设置考核对象类别!');
 }
  
  function mainBodySel(template_id)
  {
  	var objs=eval("document.implementForm.objectIDs");
	var objectIDs="";
	window.tempObj={};
      window.tempObj.template_id=template_id;
	if(objs)
	{
		if(objs.length)
		{
			for(var i=0;i<objs.length;i++)
			{
				if(objs[i].checked==true)
					objectIDs+=objs[i].value+"@";	
			}
		}
		else
		{
			if(objs.checked==true)
					objectIDs+=objs.value+"@";	
		}
	}
	if(objectIDs=="")
	{
			alert(SELECT_KHOBJ);
			return;
	}
  	var target_url="/performance/implement/kh_mainbody/mainbodySel.do?b_query=link`objIDs="+objectIDs;
 	var iframe_url="/general/query/common/iframe_query.jsp?src="+$URL.encode(target_url);
      var width = 800;
      var height = 600;
      var config = {
          width:width,
          height:height,
          type:'2'//open 方式打开
      };
      modalDialog.showModalDialogs(iframe_url,"mainBodyType",config,mainBodySel_ok);

  }
  function mainBodySel_ok(return_vo){
      if(!return_vo)
          return false;
      if(return_vo.flag=="true")
      {
          //alert(SElMAINBODYINFO);
          parent.frames['ril_body2'].location="/performance/implement/performanceImplement.do?br_mainbody=query&objectid="+select_objectid+"&template_id="+window.tempObj.template_id;
          window.tempObj=undefined;
      }
  }
    function aotoMainBodySel()
  {
    
  	var target_url="/performance/implement/kh_mainbody/aotoMainbodySel.do?b_query=link`callBackFunc=aotoMainBodySel_ok";
 	var iframe_url="/general/query/common/iframe_query.jsp?src="+$URL.encode(target_url);

 	var width = 800;
 	var height = 600;
 	var config = {
        width:width,
        height:height,
        type:'2'//open 方式打开
    };
 	modalDialog.showModalDialogs(iframe_url,"mainBodyType",config,aotoMainBodySel_ok);
  }
    function aotoMainBodySel_ok(return_vo){
        if(!return_vo)
            return false;
        if(return_vo.flag=="true")
        {
            document.implementForm.action="/performance/implement/performanceImplement.do?b_query=query&operate=init&code="+orgCode;
            document.implementForm.submit();
        }
    }
  function searchKhMainBody()
  {
  	implementForm.action="/performance/implement/kh_mainbody/mainBodyList.do?b_query=link&code="+$F('bodyType');
	implementForm.submit();	
  }
  function delMainBody()
  {
  	var khObj = $F('khObject');
  	var mainBodyIDs="";
  	var tablevos=document.getElementsByTagName("input");
  	var flag=false;
	for(var i=0;i<tablevos.length;i++)  
	{
	   if(tablevos[i].type=="checkbox" && tablevos[i].checked==true && tablevos[i].name!='selbox')	    
	 	{
	    	var theVal = tablevos[i].value;	    	
	    	var temp = theVal.split(':');  
	    	if(temp[2]=='5')
	    		flag=true;
	    	else
	    		mainBodyIDs +=theVal+"@";	
	    }
   	}	
   	if(mainBodyIDs=='' && flag==false)
   	{
   		alert(P_I_INFO4+'!');
   		return;
   	}else if(mainBodyIDs=='' && flag)
   	{
   		alert(NOTDELSELF);
   		return;
   	}
	if(confirm(Del_MAINBODYS))
	{	
		implementForm.paramStr.value=mainBodyIDs;
		implementForm.action="/performance/implement/kh_mainbody/mainBodyList.do?b_del=link&code="+$F('bodyType')+"&khObj="+khObj+"&delFlag=1";
		implementForm.submit();
		if(flag)
			alert(NOTDELSELF);
	}	
  }
  function myClose(theFlag,delFlag)
  {
	if(theFlag=='1' || delFlag=='1')
	{
		var thevo=new Object();
		thevo.flag="true";
		if(window.showModalDialog){
            parent.parent.window.returnValue=thevo;
        }else{
		    if(parent.parent.opener.mainBodySel_ok) {
				parent.parent.opener.mainBodySel_ok(thevo);
            }
        }
	}
    window.open("","_top").close();
 }
 function myCancel(theFlag,delFlag)
 {
	if(theFlag=='1' || delFlag=='1')
	{
		var thevo=new Object();
		thevo.flag="true";
		window.returnValue=thevo;
	}
	window.close();
 }
//条件选人
 function condiSelPeop()
 {	
 	var accordByDepartmentFlag =1;
 	if(implementForm.object_type.value!='2')
 		accordByDepartmentFlag =0; 	
 	
 	if($F('bodyType')=='all' || $F('bodyType')=='')
 	{
 		alert(SELETE_MAINBODYTYPE);
 		return;
 	}	
	var theurl="/performance/implement/kh_object/condition_select.do?b_query=link`db=Usr`accordByDepartmentFlag="+accordByDepartmentFlag+"`callbackfunc=conditionselect_ok";
	var iframe_url="/general/query/common/iframe_query.jsp?src="+$URL.encode(theurl);
    
    var config = {
        width:550,
        height:450,
        type:'2'
    }
   modalDialog.showModalDialogs(iframe_url,"template_win",config,conditionselect_ok);   
 }
 function conditionselect_ok(thevo){
   	 	if(thevo.flag=="true") {
	         var sql_str = thevo.sql;
	         var accordByDepartment = thevo.accordByDepartment;
	         if(sql_str!=null && sql_str!='')
	         {
	             implementForm.str_sql.value=sql_str;
	             implementForm.action="/performance/implement/kh_mainbody/mainBodyList.do?b_SelEmp=link&code="+$F('bodyType')+"&flag=1&selType=2&accordByDepartment="+accordByDepartment;     
	             implementForm.submit(); 
	         }
     } 
 }
//手工选人
 function handSelPeop(objectType)
 {   
  	if($F('bodyType')=='all' || $F('bodyType')=='')
 	{
 		alert(SELETE_MAINBODYTYPE);
 		return;
 	}	
 	/*	 	
	 var right_fields="";
	 var infor="1";
	// if(objectType=="1")
	   //	infor="3";  	
	 var obj_value=handwork_selectObject(infor,"usr")
	 if(obj_value.length>0)
	 {
		 for(var i=0;i<obj_value.length;i++)		  
		   	right_fields+=",'"+obj_value[i]+"'";
		 if(right_fields!=null && right_fields!='')
		 {   	
			 implementForm.str_sql.value=right_fields.substring(1);	
			 implementForm.action="/performance/implement/kh_mainbody/mainBodyList.do?b_SelEmp=link&code="+$F('bodyType')+"&flag=1";        
		   	 implementForm.submit(); 		 
		 }
     }
     
	
	var right_fields="";	
	var aplanid=document.implementForm.planid.value;
	var busitype=document.implementForm.busitype.value;
	var opt = 1;
	var infos=new Array();
	infos[0]=aplanid;
	infos[1]=opt;

    var strurl="/performance/handSel.do?b_query=link`planid="+aplanid+"`opt="+opt+"`busitype="+busitype;
	var iframe_url="/gz/gz_accounting/bankdisk/iframe_bank_disk.jsp?src="+strurl; 
	var objList=window.showModalDialog(iframe_url,infos,"dialogWidth=620px;dialogHeight=480px;resizable=no;scroll=no;status=no;");
    if(objList==null)
		return false;	

	if(objList.length>0)
	{
		for(var i=0;i<objList.length;i++)		   	
		    right_fields+=",'"+objList[i]+"'";		   		
		   		
		implementForm.str_sql.value=right_fields.substring(1);   	
		implementForm.action="/performance/implement/kh_mainbody/mainBodyList.do?b_SelEmp=link&code="+$F('bodyType')+"&flag=1&selType=1";        
		implementForm.submit(); 
	}*/
	new PersonPicker({
        titleText:"选择人员",
        isZoom:false,//控制选人控件不自动缩放
       	nbases:"Usr",//考核主体只能选择在职人员库的人员
        callback:function(persons){
            var right_fields="";
            for(var i=0;i<persons.length;i++){
                right_fields+=","+persons[i].id;
            }
            if(right_fields.length>0){
                implementForm.str_sql.value=right_fields.substring(1);  
                
                implementForm.action="/performance/implement/kh_mainbody/mainBodyList.do?b_SelEmp=link&code="+$F('bodyType')+"&flag=1&selType=1";        
                implementForm.submit(); 
            }
        }
    },this).open();
 }
 function alertInfo(theFlag)
 { 
 	if(theFlag=='1')
 	  alert(MAINBODY_SET);
 }
 function selOneObj()
 {
 	var objs=eval("document.implementForm.objectIDs");
	var objectID="";
	var count = 0;
	if(objs)	
	{	
		if(objs.length)
		{
			for(var i=0;i<objs.length;i++)
			{
				if(objs[i].checked==true)
				{
					objectID=objs[i].value;	
					count++;
				}					
			}
		}
		else
		{	
			if(objs.checked==true)
			{
				objectID=objs.value;	
				count++;
			}
		}
	}
		
	var thevo=new Object();
	thevo.count=count;
	thevo.objectID=objectID;
	return thevo;
 }
 //复制考核主体
 function copyKhMainBody()
 { 
 	var obj = selOneObj();	
	if(obj.count==1)
	{
		 var hashvo=new ParameterSet();     
    	hashvo.setValue("object_copy", obj.objectID);
    	hashvo.setValue("plan_id",document.implementForm.planid.value);
   		hashvo.setValue("opt",'14');
   		var request=new Request({method:'post',onSuccess:isHaveBodys,functionId:'9023000003'},hashvo);
	
	}
	else	
		alert(SELECT_ONE);		
 }
 function isHaveBodys(outparamters)
{
    var info=outparamters.getValue("info");
    var object_copy=outparamters.getValue("object_copy");
    if(info.length==0)
	{
	 	implementForm.action="/performance/implement/performanceImplement.do?b_copyBody=link&objectID="+object_copy;
		implementForm.submit();	
	}else
    	alert(info);
}
  //粘贴考核主体(可以给多个考核对象粘贴考核主体)
 function pasteKhMainBody()
 {
 	var objs=eval("document.implementForm.objectIDs");
	var objectIDs="";
	if(objs)
	{
		if(objs.length)
		{
			for(var i=0;i<objs.length;i++)
			{
				if(objs[i].checked==true)
					objectIDs+=objs[i].value+"@";	
			}
		}
		else
		{
			if(objs.checked==true)
					objectIDs+=objs.value+"@";	
		}
	}
	if(objectIDs=="")
	{
			alert(SELECT_KHOBJ);
			return;
	} 
	implementForm.action="/performance/implement/performanceImplement.do?b_pasteBody=link&objectIDs="+objectIDs;
	implementForm.submit();	
 }
  function searchKhMainBody2(plan_id,power_type)
 {	
	implementForm.action="/performance/implement/kh_mainbody/powerset.do?b_query=link&act=changeObj&plan_id="+plan_id+"&power_type="+power_type;
	implementForm.submit();	
 }
 //排序
 var code_taxis = "";
 function taxis(code){
 	var taxisurl="/performance/implement/performanceImplement.do?b_taxis=link";
 	var iframe_url="/general/query/common/iframe_query.jsp?src="+taxisurl;
     var width = 600;
     var height =370;
     code_taxis = code;
     if(isIE6() || !window.showModalDialog){
         width = 620;
         height = 390;
     }
     var config = {
         width:width,
         height:height,
         type:'2'
     }
    modalDialog.showModalDialogs(iframe_url,"pointpowerset_win",config,taxis_ok);

 	
 }
 function taxis_ok(resultVo){
     if(typeof(resultVo)!='undefined'&&resultVo.length>0)
     {

         if(resultVo=="no")
         {
             document.implementForm.orderSql.value="";
             document.implementForm.action="/performance/implement/performanceImplement.do?b_query=query&operate=init&code="+code_taxis;
             document.implementForm.submit();
         }
         else
         {
             document.implementForm.orderSql.value=resultVo;
             document.implementForm.action="/performance/implement/performanceImplement.do?b_query=query&operate=init&code="+code_taxis;
             document.implementForm.submit();
         }
     }
 }
 function synchronizeObjs(code)
 {
 	if(confirm(KH_IMPLEMENT_INF16))
 	{
 		document.implementForm.action="/performance/implement/performanceImplement.do?b_synchronizeObjs=link&operate=init&code="+code;
		document.implementForm.submit();
 	}
 }
//引入标准考核关系主体
function importKhRela(templateID)
{
	var objs=eval("document.implementForm.objectIDs");
	var objectIDs="";
	if(objs)
	{
		if(objs.length)
		{
			for(var i=0;i<objs.length;i++)
			{
				if(objs[i].checked==true)
					objectIDs+=objs[i].value+"@";	
			}
		}
		else
		{
			if(objs.checked==true)
					objectIDs+=objs.value+"@";	
		}
	}
	if(objectIDs=="")
	{
		alert(SELECT_KHOBJ);
		return;
	}
	if(confirm(KH_IMPLEMENT_INF10))
		parent.ril_body2.location="/performance/implement/performanceImplement.do?b_importkhrela=link&operate=init&objectid="+select_objectid+"&template_id="+templateID+"&objectIDs="+objectIDs;
}
//计划数据维护
function planDataWH(plan_id)
{	
	var hashvo=new ParameterSet();
	hashvo.setValue("plan_id",plan_id);
	var request=new Request({method:'post',asynchronous:false,onSuccess:planDataWHOk,functionId:'9023000022'},hashvo);
}

function planDataWHOk(outparamters)
{
	var resultFlag = outparamters.getValue("resultFlag");
	if(resultFlag=='1')
		alert(KH_IMPLEMENT_INFO1);
	else if(resultFlag=='0')
		alert(KH_IMPLEMENT_INFO2);
}
function selObjs()
{
	var objs=eval("document.implementForm.objectIDs");
	var objectIDs="";
	if(objs)
	{
		if(objs.length)
		{
			for(var i=0;i<objs.length;i++)
			{
				if(objs[i].checked==true)
					objectIDs+=objs[i].value+"@";	
			}
		}
		else
		{
			if(objs.checked==true)
					objectIDs+=objs.value+"@";	
		}
	}
	return objectIDs;
}
function batchCreateTarget(planID)
{
	
	var objs=eval("document.implementForm.objectIDs");
	var objectIDs="";
	if(objs)
	{
	
		if(objs.length)
		{
			for(var i=0;i<objs.length;i++)
			{
				if(objs[i].checked==true)
					objectIDs+="`"+objs[i].value;	
			}
		}
		else
		{
			if(objs.checked==true)
					objectIDs+="`"+objs.value;	
		}
	}

	var info = '';
	if(objectIDs=="") {
		info = KH_IMPLEMENT_INFO4;  
	}else {
		info = "确认对选中考核对象批量生成目标卡？";
	}
	
	if(confirm(info)) {
		var hashvo=new ParameterSet();
		hashvo.setValue("plan_id",planID);
		hashvo.setValue("objectIDs", objectIDs);
		var request = new Request({
					method : 'post',
					asynchronous : false,
					onSuccess : batchCreateTargetOk,
					functionId : '9023000024'
				}, hashvo);
	}  
	
}
function batchCreateTargetOk(outparamters)
{
	var msg = outparamters.getValue("msg");
	if(msg != undefined && msg != ""){
		alert(msg);
		return ;
	}
	
	var resultFlag = outparamters.getValue("resultFlag");
	if(resultFlag=='1'){
		alert(KH_IMPLEMENT_INFO5);
	}else if(resultFlag=='0')
		alert(KH_IMPLEMENT_INFO6);
}
function setBodySeq(object_id,mainbody_id,plan_id,obj)
{
	if(trim(obj.value).length>0)
	{
		if(! /^[1-9]\d*$/.test(trim(obj.value)))
		{
			 alert("顺序号为正整数!");
			 obj.value="";
		}
		 
	}
	var hashvo=new ParameterSet();
	hashvo.setValue("object_id",object_id);
	hashvo.setValue("plan_id",plan_id);
	hashvo.setValue("mainbody_id",mainbody_id);
	hashvo.setValue("seq",obj.value);
	hashvo.setValue("opt","9");
	var request=new Request({method:'post',asynchronous:false,functionId:'9023000003'},hashvo);
}
function setBodySp_seq(object_id,mainbody_id,plan_id,obj)
{
	if(trim(obj.value).length>0)
	{
		if(! /^[1-9]\d*$/.test(trim(obj.value)))
		{
			 alert("顺序号为正整数!");
			 obj.value="";
		}
		 
	}
	var hashvo=new ParameterSet();
	hashvo.setValue("object_id",object_id);
	hashvo.setValue("plan_id",plan_id);
	hashvo.setValue("mainbody_id",mainbody_id);
	hashvo.setValue("sp_seq",obj.value);
	hashvo.setValue("opt","31");
	var request=new Request({method:'post',asynchronous:false,functionId:'9023000003'},hashvo);
}
function exportObjs(plan_id,code,codeset,isDistribute)
{
	var hashvo=new ParameterSet();
	hashvo.setValue("plan_id",plan_id);
	hashvo.setValue("code",code);
	hashvo.setValue("codeset",codeset);
	hashvo.setValue("isDistribute",isDistribute);
	hashvo.setValue("orderSql",getEncodeStr(implementForm.orderSql.value));
	hashvo.setValue("queryA0100",getEncodeStr(implementForm.queryA0100.value));
	var request=new Request({method:'post',asynchronous:false,onSuccess:showfile,functionId:'9023000026'},hashvo);
}
function dataSynchronism(plan_id,code,codeset,isDistribute)
{
if(confirm('您确认要同步考核数据吗？'))  
	  	{									 				 			
	var hashvo=new ParameterSet();
	hashvo.setValue("plan_id",plan_id);
	var request=new Request({method:'post',asynchronous:false,onSuccess:showrefresh,functionId:'9023000027'},hashvo);
	}
}
function showrefresh(outparamters)
{

var A0101s=outparamters.getValue("a0101s");
if(A0101s !=''){
alert(A0101s)
}
alert("考核数据同步成功！")	
document.implementForm.action="/performance/implement/performanceImplement.do?b_query=query&operate=init0";
document.implementForm.submit();

}

function showfile(outparamters)
{		
	var outName=outparamters.getValue("outName");
//	outName=getDecodeStr(outName);
//	var name=outName.substring(0,outName.length-1)+".xls";
//	name=getEncodeStr(name);
	//zhangh 2020-4-7 下载改为使用VFS
	var outName=outparamters.getValue("outName");
	outName = decode(outName);
	var win=open("/servlet/vfsservlet?fromjavafolder=true&fileid="+outName);
}

//分发或启动时 360计划时给考核主体发送待办任务；目标计划时给考核对象或团队负责人发送待办任务 2011.06.10 JinChunhai
function disOrStartWaitTask(obj,approveTarget,startFlag)
{	
//	if(confirm(KH_RELATION_INFO6))
	{
		var hashvo=new ParameterSet();
		hashvo.setValue("plan_id",implementForm.planid.value);
		hashvo.setValue("oper",obj);
		hashvo.setValue("noApproveTargetCanScore",approveTarget);
		
		if(startFlag != undefined && startFlag !=null ){
			hashvo.setValue("startFlag",startFlag);
		}
		var request=new Request({method:'post',asynchronous:false,functionId:'9023000286'},hashvo);
//		var request=new Request({method:'post',asynchronous:false,onSuccess:showMessage,functionId:'9023000286'},hashvo);
	}
}
function showMessage(outparamters)
{
	var resultFlag = outparamters.getValue("resultFlag");
	if(resultFlag=='1')
		alert("发送成功！");
	else
		alert("发送失败！");
}

//展现 "评价关系明细及权重" 页面 2011.09.13 JinChunhai
function showDetailRankPage()
{
	implementForm.action="/performance/implement/performanceImplement/evaluateRelationDetailRank.do?b_query=link";
	implementForm.target="il_body";
   	implementForm.submit();	
}



/***************************************************  批量分发或启动计划  JinChunhai 2011.10.10 *************************************************************************/

function showSelectBox(srcobj)
{
	if(document.getElementById('date_panel').style.display==''){
        Element.hide('date_panel');
        return;
    }

	var flagi=0;
	var method = '1';  
	// 启动方式 method中没有2(即没有目标计划)：可以打分方式启动也可以录入结果方式启动  mothed中有2：只可以打分方式启动 
	for(var i=0;i<document.performancePlanForm.elements.length;i++)
   	{
		if(document.performancePlanForm.elements[i].type=="checkbox")
		{
			var ff = performancePlanForm.elements[i].name.substring(0,18);
			if(document.performancePlanForm.elements[i].checked==true && ff=='setlistform.select')
			{
				flagi++;
				if(document.performancePlanForm.elements[i+2].value=='2')
					method = '2';
			}
		}
	}
	if(flagi==0)
	{
		alert(PLEASE_SELPLAN);
		return;
	}

	date_desc=srcobj;
 	if(method == '2')
 	{
 		setSelectOptionValue('1');
 	/*
 		Element.show('date_panel2');
	    for(var i=0;i<document.performancePlanForm.date_box2.options.length;i++)
	  	{
	  	  	document.performancePlanForm.date_box2.options[i].selected=false;
	  	}
	    var pos=getAbsPosition(srcobj);
		with($('date_panel2'))
		{
			style.position="absolute";
	    	style.posLeft=pos[0]-1;
			style.posTop=pos[1]-1+srcobj.offsetHeight;
			style.width=110;
	    }
	*/

 	}else
 	{
	    Element.show('date_panel');
	    for(var i=0;i<document.performancePlanForm.date_box.options.length;i++)
	  	{
	  	  	document.performancePlanForm.date_box.options[i].selected=false;
	  	}
	    var pos=getAbsPosition(srcobj);
		with($('date_panel'))
		{
			style.position="absolute";
	    	style.left=(pos[0]-1)+"px";
			style.top=(pos[1]-1+srcobj.offsetHeight)+"px";
			style.width=110+"px";
	    } 
    }                  
}
function setSelectOptionValue(obj)
{
	if(obj=='2')
	{
		Element.hide('date_panel');  		
	  	for(var i=0;i<document.performancePlanForm.date_box.options.length;i++)
	  	{
	  		if(document.performancePlanForm.date_box.options[i].selected)
	  		{
	  			if(confirm('您确认要以'+document.performancePlanForm.date_box.options[i].text+'方式启动所选计划吗？'))  
	  			{	
	  				distributeORstart('start',document.performancePlanForm.date_box.options[i].value);								 				 	
	  			}		
	  			else
	  				break;
	  		}
	  	}
	}else
	{	
	  	if(confirm('您确认要以启动(打分)方式启动所选计划吗？'))  
	  	{	
	  		distributeORstart('start','0');								 				 	
	  	}			
	  	
	/*  	
	  	Element.hide('date_panel2');  		
	  	for(var i=0;i<document.performancePlanForm.date_box2.options.length;i++)
	  	{
	  		if(document.performancePlanForm.date_box2.options[i].selected)
	  		{
	  			if(confirm('您确认要以'+document.performancePlanForm.date_box2.options[i].text+'方式启动所选计划吗？'))  
	  			{	
	  				distributeORstart('start',document.performancePlanForm.date_box2.options[i].value);								 				 	
	  			}		
	  			else
	  				break;
	  		}
	  	}
	*/  	
	  	
	}	
}

// 批量分发或启动计划
function distributeORstart(type,mode)
{	
	var plan_ids = "";  
	var flagi=0;
	for(var i=0;i<document.performancePlanForm.elements.length;i++)
   	{
		if(document.performancePlanForm.elements[i].type=="checkbox")
		{
			var ff = performancePlanForm.elements[i].name.substring(0,18);						
			if(document.performancePlanForm.elements[i].checked==true && ff=='setlistform.select')
			{
				plan_ids+=document.performancePlanForm.elements[i+1].value+"/";
				flagi++;								
			}
		}
	}
	if(flagi==0)
	{
		alert(PLEASE_SELPLAN);
		return;
	}
	
	if(type!=null && type=='distribute' && mode!=null && mode=='no')
	{
		if(confirm("您确认要分发所选计划吗？"))
		{
			var hashvo=new ParameterSet();
			hashvo.setValue("plan_ids",plan_ids);
			hashvo.setValue("logo",type);
			hashvo.setValue("mode",mode); // 启动方式 0：打分方式 1：录入结果方式
			var request=new Request({method:'post',asynchronous:false,onSuccess:distriORstartSuc,functionId:'9022000288'},hashvo);
		}	
	}else
	{
		var hashvo=new ParameterSet();
		hashvo.setValue("plan_ids",plan_ids);
		hashvo.setValue("logo",type);
		hashvo.setValue("mode",mode); // 启动方式 0：打分方式 1：录入结果方式
		var request=new Request({method:'post',asynchronous:false,onSuccess:distriORstartSuc,functionId:'9022000288'},hashvo);
	}		  		
}
function distriORstartSuc(outparamters)
{
	var flag = outparamters.getValue("flag");
	var info = outparamters.getValue("info");
	var plan_ids = outparamters.getValue("plan_ids"); // 符合分发或启动条件的考核计划号
	var logo = outparamters.getValue("logo");
	var count = outparamters.getValue("count");
	var mode = outparamters.getValue("mode"); // 启动方式 score：打分方式 result：录入结果方式
		
	if(flag=='0')
	{
		if(count=='0') // 符合条件的计划一个都没有
		{
			alert(getDecodeStr(info));
			return;
		}
		else  // 有符合条件的计划也有不符合条件的计划
		{
			if(confirm(getDecodeStr(info)))
			{
				if(logo!=null && logo.length>0 && logo=='distribute') //分发
					distriKhPlan(plan_ids);
				else // 启动
					validateBeforeStart(plan_ids,mode);
			}
		}
	}
	else  // 选择的计划都符合条件
	{
		if(logo!=null && logo.length>0 && logo=='distribute') //分发
			distriKhPlan(plan_ids);
		else // 启动
			validateBeforeStart(plan_ids,mode);
	}						
}

/***************************  批量分发计划  *****************************************/
function distriKhPlan(plan_ids)
{			
	var hashvo=new ParameterSet();
	hashvo.setValue("plan_id",plan_ids);
	hashvo.setValue("signLogo",'batchDistribute');
	var request=new Request({method:'post',asynchronous:false,onSuccess:distriKhPlan2,functionId:'90100140016'},hashvo);		
}	
	
function distriKhPlan2(outparamters)
{
	var flag=outparamters.getValue("flag");
	var info=outparamters.getValue("info");
	var isSendEmail = outparamters.getValue("isSendEmail");
	var plan_ids = outparamters.getValue("plan_ids"); // 符合分发或启动条件的考核计划号
	var pending_system = outparamters.getValue("pending_system");  // 是否设置待办系统		
		
	if(flag=='0')
		alert(getDecodeStr(info));
	else
	{		
		//分发时给考核对象或团队负责人发送待办任务 2011.06.10 JinChunhai
//		if(pending_system!=null && pending_system=='yes')
			batchDisOrStartWaitTask(plan_ids,'distribute','batchDisOrStart','noApprove');
				
		var info_email='要向考核对象或团队负责人发送电子邮件通知吗？';						
		if(isSendEmail=='1')
		{
			if(confirm(info_email))
			{
				var hashvo=new ParameterSet();
				hashvo.setValue("plan_id",plan_ids);
				hashvo.setValue("oper",'distribute');
				hashvo.setValue("signLogo",'batchDisOrStart');
				var request=new Request({method:'post',asynchronous:false,onSuccess:distriKhplanEmail,functionId:'9023000023'},hashvo);
			}else
			{
				document.performancePlanForm.action="/performance/kh_plan/performPlanList.do?b_startPause=query&opt=distribute&signLogo=batchDisOrStart&plan_ids="+plan_ids;
				document.performancePlanForm.submit();
			}
		}else
		{
			document.performancePlanForm.action="/performance/kh_plan/performPlanList.do?b_startPause=query&opt=distribute&signLogo=batchDisOrStart&plan_ids="+plan_ids;
			document.performancePlanForm.submit();						
		}						    
	
//		window.location.href = "/performance/kh_plan/performPlanList.do?b_query=link&jxmodul=1&returnflag=menu";	
	}	
}
   
function distriKhplanEmail(outparamters)
{
	var resultFlag = outparamters.getValue("resultFlag");
	var plan_ids = outparamters.getValue("plan_ids");
	if(resultFlag=='1')
	{
		alert(KH_IMPLEMENT_INFO8);
		document.performancePlanForm.action="/performance/kh_plan/performPlanList.do?b_startPause=query&opt=distribute&signLogo=batchDisOrStart&plan_ids="+plan_ids;
		document.performancePlanForm.submit();				
	}
	
//	window.location.href = "/performance/kh_plan/performPlanList.do?b_query=link&jxmodul=1&returnflag=menu";
}
/***************************  批量分发计划 END *****************************************/


/***************************  批量启动计划  *****************************************/

//启动前的验证
function validateBeforeStart(plan_ids,startFlag)
{
	var hashvo=new ParameterSet();
	hashvo.setValue("plan_id",plan_ids);
	hashvo.setValue("signLogo",'batchStart');
	hashvo.setValue("startFlag",startFlag);
	var request=new Request({method:'post',asynchronous:false,onSuccess:validateBeforeStart2,functionId:'90100140017'},hashvo);
}
function validateBeforeStart2(outparamters)
{
    window.implObj = {};
    window.implObj.noApprTarget = "false";
	var isStart = false;
	var flag=outparamters.getValue("flag");
	var sporpfSeq=outparamters.getValue("sporpfSeq");
	var info=outparamters.getValue("info");
    window.implObj.startFlag =outparamters.getValue("startFlag");
    window.implObj.isEmail =outparamters.getValue("isEmail");  // 是否设置发送邮件参数
    window.implObj.plan_ids =outparamters.getValue("plan_ids");  // 需全部启动的计划id
    window.implObj.planId_s =outparamters.getValue("planId_s");  // 需启动发邮件的计划id
    window.implObj.pending_system = outparamters.getValue("pending_system");  // 是否设置待办系统
		
	var ainfo=getDecodeStr(info);
	var arguments=new Array();
	arguments[1]="有目标卡未被批准";
	arguments[0]=ainfo;
	arguments[2]=sporpfSeq;
	if(flag=='0')//由发布到启动的检查
	{
		alert(ainfo);
		isStart = false;
	}
	else if(flag=='2')//由发布到启动的检查 需要用弹出窗口来展示
	{
		var strurl="/performance/implement/performanceImplement.do?br_showInfo=link`callBackFunc=implement_showInfo_callfunc";
	   	var iframe_url="/general/query/common/iframe_query.jsp?src="+$URL.encode(strurl);
        var dialogWidth='490';
        var dialogHeight='440';
        if(!window.showModalDialog){
            dialogWidth = "500";
            dialogHeight='445';
            window.dialogArguments = arguments;
        }
        var config = {
            width:dialogWidth,
            height:dialogHeight,
            dialogArguments:arguments,
            type:'2'
        }
        modalDialog.showModalDialogs(iframe_url,null, config,implement_showInfo_callfunc);
        return;
	}else if(flag=='1') //flag=1是通过验证
	{
		isStart= true;	
			
	}else if(flag=='3')
	{
		if(confirm(ainfo))
			isStart= true;
		else
			isStart= false;
	}	
	if(isStart)	
	{
        implement_isStart();
	}	
}
function implement_showInfo_callfunc(return_vo){
    if(return_vo!=null && return_vo.ok==1)
    {

        if((return_vo.noApproveTargetCanScore)==true)
            window.implObj.noApprTarget="true";
        else
            window.implObj.noApprTarget="false";
        implement_isStart();
    }
}
function implement_isStart(){
    var theurl="/performance/implement/performanceImplement.do?b_showGrade=link`callBackFunc=changeDegreeCallBackfunc";
    var iframe_url="/general/query/common/iframe_query.jsp?src="+$URL.encode(theurl);
    var dialogWidth='470';
    var dialogHeight='300';
    if(!window.showModalDialog){
        dialogWidth = "460";
        dialogHeight='300';
    }
    var config = {
        width:dialogWidth,
        height:dialogHeight,
        type:'2'
    }
    modalDialog.showModalDialogs(iframe_url,'template_win', config,changeDegreeCallBackfunc);
}
function changeDegreeCallBackfunc(degree){
    if(typeof(degree)!='undefined' && degree.length>0)
    {
        //因为全局变量会在窗口关闭的时候消失，所以这里需要吧全局变量另存一下，用于下面代码的使用。
        var implObj = window.implObj;
        //启动时 360计划时给考核主体发送待办任务；目标计划时给考核对象或团队负责人发送待办任务 2011.06.10 JinChunhai
// 			if(pending_system!=null && pending_system=='yes' && (plan_ids!=null && plan_ids.length>0 && plan_ids!='undefined'))
        batchDisOrStartWaitTask(window.implObj.plan_ids,'start','batchDisOrStart',implObj.noApprTarget);

        if(implObj.isEmail=='1' && (implObj.planId_s!=null && implObj.planId_s.length>0 && implObj.planId_s!='undefined'))
        {
            Ext.showConfirm("要发送电子邮件通知吗？",function (btflag) {
                if(btflag=="yes")
                {
                    var hashvo=new ParameterSet();
                    hashvo.setValue("plan_id",implObj.planId_s);
                    hashvo.setValue("khPlan_ids",implObj.plan_ids); // 需全部启动的计划id
                    hashvo.setValue("oper",'start');
                    hashvo.setValue("startFlag",implObj.startFlag);
                    hashvo.setValue("degree",degree);
                    hashvo.setValue("noApproveTargetCanScore",implObj.noApprTarget);
                    hashvo.setValue("signLogo",'batchDisOrStart');
                    var request=new Request({method:'post',asynchronous:false,onSuccess:startSendEmail,functionId:'9023000023'},hashvo);

                }else
                {
                    document.performancePlanForm.action="/performance/kh_plan/performPlanList.do?b_startPause=query&opt=start&signLogo=batchDisOrStart&desc="+implObj.startFlag+"&degree="+degree+"&plan_ids="+implObj.plan_ids+"&noApproveTargetCanScore="+implObj.noApprTarget;
                    document.performancePlanForm.submit();
                }
            })

        }else
        {
            document.performancePlanForm.action="/performance/kh_plan/performPlanList.do?b_startPause=query&opt=start&signLogo=batchDisOrStart&desc="+implObj.startFlag+"&degree="+degree+"&plan_ids="+implObj.plan_ids+"&noApproveTargetCanScore="+implObj.noApprTarget;
            document.performancePlanForm.submit();
        }
    }
}
function startSendEmail(outparamters)
{   
    var isnull=outparamters.getValue("isnull");
	var name = outparamters.getValue("names");
	var startFlag = outparamters.getValue("startFlag");
	var degree = outparamters.getValue("degree");
	var plan_ids = outparamters.getValue("khPlan_ids");
	if(isnull=='yes'){
	    var win=open("/servlet/vfsservlet?fromjavafolder=true&fileid="+name,"txt");
		alert(KH_IMPLEMENT_INFO11);
		document.performancePlanForm.action="/performance/kh_plan/performPlanList.do?b_startPause=query&opt=start&signLogo=batchDisOrStart&desc="+startFlag+"&degree="+degree+"&plan_ids="+plan_ids;
		document.performancePlanForm.submit();
	}else{
	    alert(KH_IMPLEMENT_INFO8);
		document.performancePlanForm.action="/performance/kh_plan/performPlanList.do?b_startPause=query&opt=start&signLogo=batchDisOrStart&desc="+startFlag+"&degree="+degree+"&plan_ids="+plan_ids;
		document.performancePlanForm.submit();
	}
	
//	window.location.href = "/performance/kh_plan/performPlanList.do?b_query=link&jxmodul=1&returnflag=menu";
	
}

/***************************  批量启动计划 END *****************************************/

// 批量分发或启动时 360计划时给考核主体发送待办任务；目标计划时给考核对象或团队负责人发送待办任务 2011.06.10 JinChunhai
function batchDisOrStartWaitTask(plan_ids,obj,signLogo,approveTarget)
{	
//	if(confirm(KH_RELATION_INFO6))
	{
		var hashvo=new ParameterSet();
		hashvo.setValue("plan_id",plan_ids);
		hashvo.setValue("oper",obj);
		hashvo.setValue("signLogo",signLogo);
		hashvo.setValue("noApproveTargetCanScore",approveTarget);
		var request=new Request({method:'post',asynchronous:false,functionId:'9023000286'},hashvo);
	}
}





