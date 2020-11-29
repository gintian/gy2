function replaceAll(str, sptr, sptr1)
{
	while (str.indexOf(sptr) >= 0)
	{
   		str = str.replace(sptr, sptr1);
	}
	return str;
}   
function validate_self(obj,aitemdesc)
{		
	var dd=true;
	var itemdesc="";
	if(aitemdesc==null||aitemdesc==undefined)
		itemdesc="日期";
	else 
		itemdesc=aitemdesc;

	if(trim(obj.value).length!=0)
	{						
		var myReg =/^(-?\d+)(\.\d+)?$/
		if(IsOverStrLength(obj.value,10))
		{
			alert(itemdesc+" 格式不正确,正确格式为yyyy-mm-dd ！");
			return false;
		}
		else
		{
			if(trim(obj.value).length!=10)
			{
				alert(itemdesc+" 格式不正确,正确格式为yyyy-mm-dd ！");
				return false;
			}
			var year=obj.value.substring(0,4);
			var month=obj.value.substring(5,7);
			var day=obj.value.substring(8,10);
			if(!myReg.test(year)||!myReg.test(month)||!myReg.test(day)) 
			{
				alert(itemdesc+" 格式不正确,正确格式为yyyy-mm-dd ！");
				return false;
			}
			if(year<1900||year>2100)
			{
				alert(itemdesc+" 年范围为1900~2100！");
				return false;
			}
							 	
			if(!isValidDate(day, month, year))
			{
				alert(itemdesc+"错误，无效时间！");
				return false;
			}
		}
	}
	return dd
}
 
function search_kh_data(obj)
{
	var timeInter = document.getElementById("timeInterval").value;
	if(timeInter=='3' && obj.id=='timeInterval')
	{
		//document.getElementById("editor1").style.display="block";	
		//document.getElementById("editor2").style.display="block";
		showElement('datepnl');
		document.getElementById("editor1").value='';
		document.getElementById("editor2").value='';
		return;
	}
	
	var startTime = document.getElementById("editor1").value;
	var endTime = document.getElementById("editor2").value;
	
	if(trim(startTime)!='')
    {	
    	 if(!validate_self(document.getElementById("editor1"),'起始日期'))
    	 		return false;    	
    }
    if(trim(endTime)!='')
    {
    	if(!validate_self(document.getElementById("editor2"),'结束日期'))
    	 		return false;
   	}	
	
	document.getElementById("startDate").value=startTime;	
	document.getElementById("endDate").value=endTime;	
	
    if(startTime!='' && endTime!='')
    	if(startTime>endTime)	
    	{
    		alert(KHPLAN_INFO1);
    	    return;
    	}	
    
    var query_status=examPlanForm.spStatus.value;
    var query_timefw=examPlanForm.timeInterval.value;
	document.examPlanForm.action="/performance/kh_plan/examPlanList.do?b_query=query&first=true";
	//&query_status="+query_status+"&query_timefw="+query_timefw;
	document.examPlanForm.submit();	
}
function edit(planId)
{
	var obj = document.getElementById("tbl-container");
	var scrollValue=obj.scrollLeft;
	var scrollTopValue = obj.scrollTop;
	document.examPlanForm.action="/performance/kh_plan/examPlanAdd.do?b_add=link&planId="+planId+"&scrollValue="+scrollValue+"&scrollTopValue="+scrollTopValue;
	document.examPlanForm.submit();	
}
function delHistory()
{
	var busitype = document.getElementById("busitype");
	var str="";
			for(var i=0;i<document.examPlanForm.elements.length;i++)
			{
				if(document.examPlanForm.elements[i].type=="checkbox")
				{					
					var ff = examPlanForm.elements[i].name.substring(0,18);						
					if(document.examPlanForm.elements[i].checked==true && ff=='setlistform.select')
					{
						var plan_id = document.examPlanForm.elements[i+1].value;
						var status = document.examPlanForm.elements[i+2].value;
						str+="/"+plan_id+":"+status;
					}
				}
			}
			if(str.length==0)
			{				
				if(busitype!=null && busitype.value=="1")				
					alert(PLEASE_EVALUASELPLAN);
				else					
					alert(PLEASE_SELPLAN);
					
				return;
			}
			else
			{
				if (confirm(KHPLAN_INFO2))
				{
					document.examPlanForm.paramStr.value=str.substring(1);
					document.examPlanForm.action="/performance/kh_plan/examPlanList.do?b_delhistory=link";
					document.examPlanForm.submit();	
				}
			}

	
}
function getTemplate(templId,planID,status)
{
    //如果是另存得来的考核计划，且另存时候选择了复制指标权限表，在起草状态也不能修改模板
	if(status=='0')
	{
		var hashvo=new ParameterSet();			
		hashvo.setValue("thePlan",planID);
		hashvo.setValue("templId",templId);
		var request=new Request({method:'post',asynchronous:false,onSuccess:getTemplate2,functionId:'9022000026'},hashvo);
	}else//非起草状态
	{
		var canedit='';
	//	if(status=='5')
	//		canedit='1';
	//	else
			canedit='0';
		getTemplate1(templId,planID,canedit);
	}
		
}
function getTemplate2(outparamters)
{
	var canedit=outparamters.getValue("canedit");	
	var planID=outparamters.getValue("thePlan");
	var templId=outparamters.getValue("templId");
	if(canedit=='1')//可以编辑模板
		getTemplate1(templId,planID,canedit);
	else if(canedit=='0')//不可以编辑模板
	{
		var busitype = document.getElementById("busitype");
		if(busitype!=null && busitype.value=="1")
			alert(NOTEDIT_KHTEMPLATE1);
		else
			alert(NOTEDIT_KHTEMPLATE);
		getTemplate1(templId,planID,canedit);
	}
}
function getTemplate1(templId,planID,canedit)
{	
	var subsys_id = "33";
  	var busitype = document.getElementById("busitype");
	if(busitype!=null && busitype.value=="1")
  		subsys_id = "35";
  	
	var method = $F('m_'+planID);	
	method=3//程序暂时改为考核模板显示所有，不受考核方法的制约，但是考核方法随着考核模板变动
	//method=1 显示非个性化项目的模板 method=2 显示个性化项目的模板 method=3 显示全部
	var theurl="/performance/kh_system/kh_template/kh_template_tree.do?b_query=link&subsys_id="+subsys_id+"&isVisible=2&planId="+planID+"&method="+method+"&isEdit="+canedit+"&templateid="+templId;
	if(canedit=='1')
		theurl="/performance/kh_system/kh_template/kh_template_tree.do?b_query=link&subsys_id="+subsys_id+"&isVisible=2&planId="+planID+"&method="+method+"&isEdit=1&templateid="+templId;
	else if(canedit=='0')
		theurl="/performance/kh_system/kh_template/init_kh_item.do?b_query=link&templateid="+templId+"&planId="+planID+"&subsys_id="+subsys_id+"&isVisible=2&method="+method+"&isEdit=0";
    //var iframe_url="/general/query/common/iframe_query.jsp?src="+theurl;//这里暂时不使用模板，因为模板设置了不显示滚动条，导致考核计划页面中点关联模板弹出的页面显示不全 chent 20160704

	t_planID=planID;
    if(window.showModalDialog){
        var return_vo= window.showModalDialog(theurl, 'template_win',
            "dialogWidth:800px; dialogHeight:610px;resizable:yes;center:yes;scroll:yes;status:no;minimize:yes;maximize:yes;");
        getTemplate_ok(return_vo);
    }else{
        var config = {
            width:800,
            height:610,
            type:'2',
            id:'template_win'
        };
        modalDialog.showModalDialogs(theurl,'template_win',config);
    }


}
function getTemplate_ok(return_vo) {
    if(return_vo==null)
        return;
    var template=return_vo.split(',');
    if(template!=null)
    {
        var templateId = template[1];
        document.examPlanForm.action="/performance/kh_plan/examPlanList.do?b_updateTemplate=link&planId="+t_planID+"&templateId="+templateId;
        document.examPlanForm.submit();
    }
}

function getPlanParameter(planId,objectType,status,templateId,gather_type)
{	
	if(templateId=='')
		templateId='isNull';
		
	var method = $F('m_'+planId);	
	var target_url="/performance/kh_plan/kh_params.do?b_query=link`paramOper=list`plan_id="+planId+"`status="+status+"`templateId="+templateId+'`object_type='+objectType+'`method='+method+'`gather_type='+gather_type;
 	var iframe_url="/general/query/common/iframe_query.jsp?src="+$URL.encode(target_url);
    if(window.showModalDialog){
        var height = 650;
        if (Ext.ieVersion>8)
            height = 660;
        var return_vo= window.showModalDialog(iframe_url, "kh_param_options",
            "dialogWidth:670px; dialogHeight:"+height+"px;resizable:no;center:yes;scroll:no;status:no");
        getPlanParameter_window_ok(return_vo);
    }else{
        var config = {
            width:670,
            height:650,
            type:'2',
			id:'resultFiled_win'
        };
        modalDialog.showModalDialogs(iframe_url,'resultFiled_win',config);
    }


}
function getPlanParameter_window_ok(return_vo) {

    if(!return_vo && return_vo!=null && return_vo!='undefined')
    {
        if(document.getElementById("busitype")!=null && document.getElementById("busitype").value=="1")
        {
            var byModelName = "";
            if(return_vo.byModel=='1') // 返回值包含byModel和requiredFieldStr modify by 刘蒙
                byModelName = "素质模板";
            document.getElementById('per_'+planId).innerHTML = byModelName;
        }
    }
}
function add()
{
	document.examPlanForm.action="/performance/kh_plan/examPlanAdd.do?b_add=link";
	document.examPlanForm.submit();	
}
function delqc()
{
	var busitype = document.getElementById("busitype");
	var str="";
			for(var i=0;i<document.examPlanForm.elements.length;i++)
			{
				if(document.examPlanForm.elements[i].type=="checkbox")
				{					
					var ff = examPlanForm.elements[i].name.substring(0,18);						
					if(document.examPlanForm.elements[i].checked==true && ff=='setlistform.select')
					{
						str+=document.examPlanForm.elements[i+1].value+"/";
						if(document.examPlanForm.elements[i+2].value!='0')
						{
							if(busitype!=null && busitype.value=="1")				
								alert("只能删除起草状态的评估计划！");
							else													
								alert(PERFORMANCE_PLAN_INFO);
							
							return;
						}
					}
				}
			}
			if(str.length==0)
			{				
				if(busitype!=null && busitype.value=="1")				
					alert(PLEASE_EVALUASELPLAN);
				else					
					alert(PLEASE_SELPLAN);
				
				return;
			}
			else
			{
				if (confirm(IS_DEL_Or_NOT_Plan))
				{
						document.examPlanForm.action="/performance/kh_plan/examPlanList.do?b_delete=link&deletestr="+str;
						document.examPlanForm.submit();	
				}
			}
}
function saveas()
{
	var busitype = document.getElementById("busitype");
	var p_status='';	
	var planId ="";  // 修改为可：同时另存多个考核计划； JinChunhai 2011.10.11
	var flagi=0;
	for(var i=0;i<document.examPlanForm.elements.length;i++)
   	{
		if(document.examPlanForm.elements[i].type=="checkbox")
		{
			var ff = examPlanForm.elements[i].name.substring(0,18);						
			if(document.examPlanForm.elements[i].checked==true && ff=='setlistform.select')
			{
				planId+=document.examPlanForm.elements[i+1].value+"/";
				p_status+=document.examPlanForm.elements[i+2].value+"/";
				flagi++;
				
/*				if(flagi==0)
				{
					planId = document.examPlanForm.elements[i+1].value;
				    p_status=document.examPlanForm.elements[i+2].value;
					flagi++;
				}else
				{
					alert(SELECT_ONE_PLAN+'!');
					return;
				}
*/				
			}
		}
	} 
	if(flagi==0)
	{		
		if(busitype!=null && busitype.value=="1")				
			alert(PLEASE_EVALUASELPLAN);
		else					
			alert(PLEASE_SELPLAN);
				
		return;
	}
	
 	var target_url="/performance/kh_plan/examPlanSaveAs.do?b_query=link`busitype="+busitype.value+"`planId="+planId+"`status="+p_status+"`type=nocopy";
 	var iframe_url="/general/query/common/iframe_query.jsp?src="+$URL.encode(target_url);


    if(window.showModalDialog){
        var return_vo= window.showModalDialog(iframe_url, "review_win",
            "dialogWidth:585px; dialogHeight:525px;resizable:no;center:yes;scroll:no;status:no");
        saveas_window_ok(return_vo);
    }else{
        var config = {
            width:585,
            height:525,
            type:'1',
			id:'review_win',
			title:'另存'
        };
        modalDialog.showModalDialogs(iframe_url,'review_win',config);
    }
}

function saveas_window_ok(return_vo) {

	if(!window.showModalDialog){
		Ext.getCmp("review_win").close();
	}

    if(return_vo) {
        if (return_vo.flag == "true") {
            document.examPlanForm.action = "/performance/kh_plan/examPlanList.do?b_query=link";
            document.examPlanForm.submit();
        }
    }
}
function appeal(mode)
{	
	var busitype = document.getElementById("busitype");
	var str="";
	for(var i=0;i<document.examPlanForm.elements.length;i++)
   {
		if(document.examPlanForm.elements[i].type=="checkbox")
		{
			var ff = examPlanForm.elements[i].name.substring(0,18);						
			if(document.examPlanForm.elements[i].checked==true && ff=='setlistform.select')
			{
				var p_status=document.examPlanForm.elements[i+2].value;
				if(p_status!='0')
				{
					alert(KHPLAN_INFO8);//只有起草状态的纪录才能报批
					return;
				}		
			
				str+=document.examPlanForm.elements[i+1].value+"/";
			}
		}
	}
	if(str.length==0)
	{		
		if(busitype!=null && busitype.value=="1")				
			alert(PLEASE_EVALUASELPLAN);
		else					
			alert(PLEASE_SELPLAN);
					
		return;
	}
	else
	{
		var info = "";
		if(mode=='1')
		   info=IS_SUBMIT;
		else
		   info=IS_SUBMIT2;
		if (confirm(info))
		{
			document.examPlanForm.action="/performance/kh_plan/examPlanList.do?b_baopi=link&baopistr="+str;
			document.examPlanForm.submit();	
		}
	}
}
function review()
{  
	var busitype = document.getElementById("busitype");
   /* var review_status=document.getElementById("review_status").value;
    if(review_status=='')
    {    	
		if(busitype!=null && busitype.value=="1")				
			alert(PLEASE_EVALUASELPLAN);
		else					
			alert(PLEASE_SELPLAN);
					
		return;    	
    }    	
	if(review_status!='1')
	{
		alert(KHPLAN_INFO3);
		return;
	}*/
	
	//为了支持同时审阅多个考核计划  加如下代码
	var planids="";
	var flagi=0;
	var planId ="";//多选的计划中的第一个
	for(var i=0;i<document.examPlanForm.elements.length;i++)
   	{
		if(document.examPlanForm.elements[i].type=="checkbox")
		{
			var ff = examPlanForm.elements[i].name.substring(0,18);						
			if(document.examPlanForm.elements[i].checked==true && ff=='setlistform.select')
			{
				var p_status=document.examPlanForm.elements[i+2].value;
				if(p_status!='1')
				{
					alert(KHPLAN_INFO3);
					return;
				}
				if(flagi==0)
				{
					planId = document.examPlanForm.elements[i+1].value;
					flagi++;
				}
				planids+=document.examPlanForm.elements[i+1].value+"/";
			}
		}
	} 
	if(flagi==0)
	{		
		if(busitype!=null && busitype.value=="1")				
			alert(PLEASE_EVALUASELPLAN);
		else					
			alert(PLEASE_SELPLAN);
					
		return;
	}
	//var planId = document.getElementById("planId").value;	
 	var target_url="/performance/kh_plan/examPlanReview.do?b_query=link`planId="+planId;
 	var iframe_url="/general/query/common/iframe_query.jsp?src="+$URL.encode(target_url);
    if(window.showModalDialog){
        var return_vo= window.showModalDialog(iframe_url, "review_win",
            "dialogWidth:410px; dialogHeight:280px;resizable:no;center:yes;scroll:no;status:no");
        review_window_ok(return_vo);
    }else{
        var config = {
            width:410,
            height:300,
            type:'1',
			title:'审阅',
			id:'review_win'
        };
        modalDialog.showModalDialogs(iframe_url,'review_win',config);
    }

}

function review_window_ok(return_vo) {
    if(return_vo) {
        if (return_vo.flag == "true") {

            var planids="";
            for(var i=0;i<document.examPlanForm.elements.length;i++)
            {
                if(document.examPlanForm.elements[i].type=="checkbox")
                {
                    var ff = examPlanForm.elements[i].name.substring(0,18);
                    if(document.examPlanForm.elements[i].checked==true && ff=='setlistform.select')
                    {
                        planids+=document.examPlanForm.elements[i+1].value+"/";
                    }
                }
            }

            var approve_result = return_vo.approve_result;
            document.getElementById("paramStr").value = return_vo.agree_idea;
            document.examPlanForm.action = "/performance/kh_plan/examPlanList.do?b_review=link&approve_result=" + approve_result + "&plan_ids=" + planids;
            document.examPlanForm.submit();
        }
    }
}

function issue()
{
	var busitype = document.getElementById("busitype");
/*
	var issue_status=document.getElementById("review_status").value;alert(issue_status);
    if(issue_status=='')
    {   	
		if(busitype!=null && busitype.value=="1")				
			alert(PLEASE_EVALUASELPLAN);
		else					
			alert(PLEASE_SELPLAN);
					
		return;
    }    	
	if(issue_status!='2')
	{
		alert(KHPLAN_INFO4);
		return;
	}*/
	//var planId = document.getElementById("planId").value;	
	var str="";
	for(var i=0;i<document.examPlanForm.elements.length;i++)
    {
		if(document.examPlanForm.elements[i].type=="checkbox")
		{
			var ff = examPlanForm.elements[i].name.substring(0,18);						
			if(document.examPlanForm.elements[i].checked==true && ff=='setlistform.select')
			{
				str+=document.examPlanForm.elements[i+1].value+"/";
				if(document.examPlanForm.elements[i+2].value!='2')
				{
					alert(KHPLAN_INFO4);
					return;
				}
			}
		}
	}

	if(str.length==0)
	{
		if(busitype!=null && busitype.value=="1")				
			alert(PLEASE_EVALUASELPLAN);
		else					
			alert(PLEASE_SELPLAN);
					
		return;
	}
	else
	{
		if (confirm(KHPLAN_INFO5))
		{ 
			document.examPlanForm.action="/performance/kh_plan/examPlanList.do?b_publish=link&planIds="+str;
			document.examPlanForm.submit();	
		}
	
	}	
}
function setMode(mode)
{
	var info=KHPLAN_INFO6;
	if(mode==0)
		info+="直批方式"
	else if(mode==1)
		info+="审批方式"
	info+=KHPLAN_INFO7;
	
	// 把用户设置的审批方式保存在cookie中  JinChunhai 2011.08.24
	if(document.cookie != "")
	{ 
		var Days = 36500; //此 cookie 将被保存 100 年
		var exp  = new Date();    //new Date("December 31, 9998");
		exp.setTime(exp.getTime() + Days*24*60*60*1000);
		document.cookie ="model="+ escape (mode) + ";expires=" + exp.toGMTString();
	}
		
//	if(confirm(info))
	{
		document.getElementById("model").value=mode;
		document.examPlanForm.action="/performance/kh_plan/examPlanList.do?b_set=link";
		document.examPlanForm.submit();	
	}
}
function showDateSelectBox(srcobj)
   {
	document.getElementById("date_panel").style.zIndex=9999;
   	if(document.getElementById("date_panel").style.display =="none"){
        date_desc=srcobj;
        showElement('date_panel');
        for(var i=0;i<document.examPlanForm.date_box.options.length;i++)
        {
            document.examPlanForm.date_box.options[i].selected=false;
        }
        var pos=getAbsPosition(srcobj);
        var panel=document.getElementById("date_panel");
        var btnPanel=srcobj;
        var panelHeight=panel.offsetHeight;
        var btnY=pos[1];
       if(btnY+srcobj.offsetHeight+panelHeight>document.body.offsetHeight){//计算按钮位置，按钮过于靠底部时显示下拉框放置于按钮上方显示
        	with($('date_panel'))
        	{
        		style.position="absolute";
        		style.left=pos[0]-1;
        		style.top=pos[1]-1-panelHeight;
        		style.width=80;
        	}
        }else{
        	with($('date_panel'))
        	{
        		style.position="absolute";
        		style.left=pos[0]-1;
        		style.top=pos[1]-1+srcobj.offsetHeight;
        		style.width=80;
        	}
        }
    }else{
   		hideElement('date_panel');
	}
   }
function setSelectValue()
{

  	for(var i=0;i<document.examPlanForm.date_box.options.length;i++)
  	{
  		if(document.examPlanForm.date_box.options[i].selected)
  		{
  			if(confirm(KHPLAN_INFO6+document.examPlanForm.date_box.options[i].text+KHPLAN_INFO7))  									
  				setMode(document.examPlanForm.date_box.options[i].value); 			
  			else
  				break;
  		}
  	}
    hideElement('date_panel');
}
function hideElement(id) {
    if(document.getElementById(id)) {
        document.getElementById(id).style.display = "none";
    }
}

function showElement(id) {
    if(document.getElementById(id)) {
        document.getElementById(id).style.display = "";
    }
}
   function setCvalue(nid,status)
   {
		document.getElementById("planId").value=nid;
		document.getElementById("review_status").value=status;	
	 	tr_bgcolor(nid);
	 	
	}
	function tr_bgcolor(nid){
		var tablevos=document.getElementsByTagName("input");
		for(var i=0;i<tablevos.length;i++){
		    //if(tablevos[i].type=="checkbox"){
		    	var cvalue = tablevos[i];
		    	var td = cvalue.parentNode.parentNode;
		    	td.style.backgroundColor = '';
			//}
			 if(tablevos[i].type=="checkbox")
			 	tablevos[i].value='1';
	    }
		var c = document.getElementById(nid);	
		var tr = c.parentNode.parentNode;	
		if(tr.style.backgroundColor!=''){
			tr.style.backgroundColor = '' ;
		}else{
			tr.style.backgroundColor = '#98C2E8' ;
		}
		tr.cells[0].style.background='FFF8D2';
		tr.cells[1].style.background='FFF8D2';
	}
//用于鼠标触发的某一行
var curObjTr= null;
var oldObjTr_c= "";
function tr_onclick_self(objTr,bgcolor)
{	
	if(curObjTr!=null)
	{
		curObjTr.style.background="";
		curObjTr.cells[0].style.background='#FFFFFF'; 
		curObjTr.cells[1].style.background='#FFFFFF'; 
		curObjTr.cells[2].style.background='#FFFFFF'; 
	}
	
	curObjTr=objTr;
	oldObjTr_c="FFF8D2";
	curObjTr.style.background='FFF8D2';		
	curObjTr.cells[0].style.background='FFF8D2';
	curObjTr.cells[1].style.background='FFF8D2';
	curObjTr.cells[2].style.background='FFF8D2';	
}
	function updateBigField(planid,status,fieldName)
	{
		var target_url="/performance/kh_plan/fieldDetail.do?b_queryBigFld=link`planID="+planid+"`fieldName="+fieldName+"`status="+status;
 		var iframe_url="/general/query/common/iframe_query.jsp?src="+$URL.encode(target_url);
        if(window.showModalDialog){
            var return_vo= window.showModalDialog(iframe_url, "review_win",
                "dialogWidth:310px; dialogHeight:330px;resizable:no;center:yes;scroll:no;status:no");
            updateBigField_window_ok(return_vo);
        }else{
            var config = {
                width:310,
                height:330,
                type:'1',
				id:'review_win',
				title:'计划说明'
            };
            modalDialog.showModalDialogs(iframe_url,'review_win',config);
        }
	}
	function updateBigField_window_ok(return_vo) {

        if (!window.showModalDialog) {
            Ext.getCmp('review_win').close();
        }

        if (return_vo) {
            if (return_vo.flag == "true") {
                document.getElementById("paramStr").value = return_vo.bigField;
                document.examPlanForm.action = "/performance/kh_plan/examPlanList.do?b_updateBigFld=link&planID=" + return_vo.planID + "&fieldName=" + return_vo.fieldName;
                document.examPlanForm.submit();
            }
        }

    }
	
function isNumber(obj)
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
  		alert(INPUT_NUMBER_VALUE+'!');
  		obj.value='';  
  		obj.focus();
  	}  	   
}
function changeMethod(method,planId)
{
	var hashvo=new ParameterSet();
	hashvo.setValue("planID",planId);
	hashvo.setValue("method",method);
	var request=new Request({method:'post',asynchronous:false,functionId:'9022000021'},hashvo);	
}
 function mincrease(obj_name) 
 {
      var objs =document.getElementsByName(obj_name);
  	  if(objs==null)
  		 return false;
  	  var obj=objs[0];
  	  if(parseInt(obj.value)>0)
		obj.value = (parseInt(obj.value)+1)+'';
}
 function msubtract(obj_name)
 {
      var objs =document.getElementsByName(obj_name);
  	  if(objs==null)
  		 return false;
  	  var obj=objs[0];
  	  if(parseInt(obj.value)>0)
		obj.value = (parseInt(obj.value)-1)+'';
}
//输入整数
function IsDigit2(obj) 
{
	if((event.keyCode >47) && (event.keyCode <= 57))
		return true;
	else
		return false;	
}
//可以输入正负整数
function IsDigit(obj) 
{
	 if ((event.keyCode >= 47) && (event.keyCode <= 57)) 
		return true;
	 if(event.keyCode == 45)
	 {
	 	var values=obj.value;
	 	if(values.length==0)
	 		return true;
	 }
		return false;
}
function changeA0000(theObj,plan_id)
{
	if(theObj.value!='')
	{
		if(parseInt(theObj.value)>999999)
		{
			alert('请输入小于等于999999的整数！');
			return;
		}
	}
	var hashvo=new ParameterSet();
	hashvo.setValue("planId",plan_id);
	hashvo.setValue("a0000",theObj.value==''?'null':theObj.value);
	hashvo.setValue("opt","13");
	var request=new Request({method:'post',asynchronous:false,functionId:'9023000003'},hashvo);			
}
function moveRecord(plan_id,move,obj)
{
	var timeInter = document.getElementById("timeInterval").value;
	if(timeInter=='3' && obj.id=='timeInterval')
	{
		document.getElementById("editor1").value='';
		document.getElementById("editor2").value='';
		return;
	}
	var startTime = document.getElementById("editor1").value;
	var endTime = document.getElementById("editor2").value;
	document.getElementById("startDate").value=startTime;	
	document.getElementById("endDate").value=endTime;	
	
	var obj = document.getElementById("tbl-container");
	var scrollValue=obj.scrollLeft;
	 var scrollTopValue = obj.scrollTop;
	
    if(startTime!='' && endTime!='')
    	if(startTime>endTime)	
    	{
    		alert(KHPLAN_INFO1);
    	    return;
    	}	
    
    var query_status=examPlanForm.spStatus.value;
    var query_timefw=examPlanForm.timeInterval.value;
	document.examPlanForm.action="/performance/kh_plan/examPlanList.do?b_query=move&a_planid="+plan_id+"&move="+move+"&query_status="+query_status+"&query_timefw="+query_timefw+"&scrollValue="+scrollValue+"&scrollTopValue="+scrollTopValue;
	document.examPlanForm.submit();	
}