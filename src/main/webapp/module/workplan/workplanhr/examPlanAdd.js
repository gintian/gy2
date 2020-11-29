function replaceAll(str, sptr, sptr1)
{
	while (str.indexOf(sptr) >= 0)
	{
   		str = str.replace(sptr, sptr1);
	}
	return str;
}

function getCycle()
{
	var cycle = document.getElementById('cycle').value;
	var theyear= document.getElementById('theyear').value;
	var thequarter = document.getElementById('thequarter').value;
	var themonth = document.getElementById('themonth').value;
	var start_date= document.getElementById('start_date').value;
	var end_date = document.getElementById('end_date').value;
	
	if(cycle==KHPLAN_YEAR)
		cycle='0';
	else if(cycle==KHPLAN_HALFYEAR)
		cycle='1';
	else if(cycle==KHPLAN_QUARTER)
		cycle='2';
	else if(cycle==KHPLAN_MONTH)
		cycle='3';	
	else if(cycle==KHPLAN_INDEFINETIME)
		cycle='7';		
	
	var target_url="/performance/kh_plan/examPlanAdd.do?br_cycle=link`cycle="+cycle+"`theyear="+theyear+"`thequarter="+thequarter+"`themonth="+themonth+"`start_date="+start_date+"`end_date="+end_date+"`status="+$F('status');
 	var iframe_url="/general/query/common/iframe_query.jsp?src="+target_url;
	var return_vo= window.showModalDialog(iframe_url, "cycle_win",  
	              "dialogWidth:320px; dialogHeight:300px;resizable:no;center:yes;scroll:no;status:no"); 
	if(!return_vo || $F('status')!='0')
	    return false;	   
    if(return_vo.flag=="true")  
    {     
		document.getElementById('cycle').value=return_vo.cycle;
		document.getElementById('theyear').value=return_vo.theyear;
		document.getElementById('themonth').value=return_vo.themonth;
		document.getElementById('start_date').value=return_vo.start_date;
		document.getElementById('end_date').value=return_vo.end_date;
		if(return_vo.cycle=='1')
			document.getElementById('thequarter').value=return_vo.thehalfyear;
		else if(return_vo.cycle=='2')
			document.getElementById('thequarter').value=return_vo.thequarter;
			
		setkhtimeqj();
		setCycle();		
	} 
}
function getTemplate()
{
	var busitype = document.getElementById('planBusitype');
	var subsys_id = '33'; 	
	if(busitype!=null && busitype.value=='1')
  		subsys_id = '35';
	var plan_id = $F('plan_id');
	var status = $F('status');
	var templId=$F('template_id');
	if(status=='0')
	{
		var hashvo=new ParameterSet();			
		hashvo.setValue("thePlan",plan_id);
		hashvo.setValue("templId",templId);
		var request=new Request({method:'post',asynchronous:false,onSuccess:getTemplate2,functionId:'9022000026'},hashvo);
	}else
	{
		var method = '1';
		if(busitype!=null && busitype.value=='0')
		{
		    if(examPlanForm.method[1].checked==true)
		    	method = '2';	
	    }
	    method=3//程序暂时改为考核模板显示所有，不受考核方法的制约，但是考核方法随着考核模板变动
		 //method=1 显示非个性化项目的模板 method=2 显示个性化项目的模板 method=3 显示全部
		var theurl="/performance/kh_system/kh_template/init_kh_item.do?b_query=link`templateid="+templId+"`subsys_id="+subsys_id+"`isVisible=2`method="+method+"`isEdit=0";
      	var iframe_url="/general/query/common/iframe_query.jsp?src="+$URL.encode(theurl);
      	var height = parent.parent.document.body.offsetHeight;
        var width = parent.parent.document.body.offsetWidth;
        height = height<700?height*0.8:600;
        width = width<900?width*.08:800;
        parent.parent.onresize = function(){
      	    var h = parent.parent.document.body.offsetHeight;
            var w = parent.parent.document.body.offsetWidth;
            h = h<700?h*0.8:600;
            w = w<900?w*0.8:800;
      	  var templateWin = parent.parent.Ext.getCmp("template_win");
      	  if(templateWin){
      		  templateWin.setWidth(w);
      		  templateWin.setHeight(h);
      	  }
        }
        parent.parent.Ext.create("Ext.window.Window",{
          	id:'template_win',
          	width:width,
          	height:height,
         	title:'关联模板',
         	resizable:'yes',
         	modal:true,
         	autoScroll:false,
         	autoShow:true,
         	autoDestory:true,
         	html:"<iframe style='background-color:#ffffff' frameborder='0' SCROLLING=NO height='100%' width='100%' src='"+iframe_url+"'></iframe>"
         })	
	}
}
function getTemplate2(outparamters)
{
	var canedit=outparamters.getValue("canedit");	
	var planID=outparamters.getValue("thePlan");
	var templId=outparamters.getValue("templId");
	var astatus=outparamters.getValue("status");
	if(canedit=='1')//可以编辑模板
		getTemplate1();
	else if(canedit=='0')//不可以编辑模板
	{
		var busitype = document.getElementById('planBusitype');
		var subsys_id = '33'; 	
		if(busitype!=null && busitype.value=='1')
	  		subsys_id = '35';
		
		if(astatus=='0')//如果是另存得来的考核计划，且另存时候选择了复制指标权限表，在起草状态也不能修改模板
			alert(NOTEDIT_KHTEMPLATE);
	    templId=$F('template_id');
	    var method = '1';
	    if(busitype!=null && busitype.value=='0')
		{
		    if(examPlanForm.method[1].checked==true)
		    	method = '2';	
	    }
	    method=3//程序暂时改为考核模板显示所有，不受考核方法的制约，但是考核方法随着考核模板变动
		 //method=1 显示非个性化项目的模板 method=2 显示个性化项目的模板 method=3 显示全部
		var theurl="/performance/kh_system/kh_template/init_kh_item.do?b_query=link`templateid="+templId+"`subsys_id="+subsys_id+"`isVisible=2`method="+method+"`isEdit=0";
      	var iframe_url="/general/query/common/iframe_query.jsp?src="+$URL.encode(theurl);
      	var height = parent.parent.document.body.offsetHeight;
        var width = parent.parent.document.body.offsetWidth;
        height = height<700?height*0.8:600;
        width = width<900?width*.08:800;
        parent.parent.onresize = function(){
      	    var h = parent.parent.document.body.offsetHeight;
            var w = parent.parent.document.body.offsetWidth;
            h = h<700?h*0.8:600;
            w = w<900?w*0.8:800;
      	  var templateWin = parent.parent.Ext.getCmp("template_win");
      	  if(templateWin){
      		  templateWin.setWidth(w);
      		  templateWin.setHeight(h);
      	  }
        }
        parent.parent.Ext.create("Ext.window.Window",{
          	id:'template_win',
          	width:width,
          	height:height,
          	title:'关联模板',
          	resizable:'yes',
          	modal:true,
          	autoScroll:false,
          	autoShow:true,
          	autoDestory:true,
          	html:"<iframe style='background-color:#ffffff' frameborder='0' SCROLLING=NO height='100%' width='100%' src='"+iframe_url+"'></iframe>"
          });
	}
}
function getTemplate1()
{
	var busitype = document.getElementById('planBusitype');
	var subsys_id = '33'; 	
	if(busitype!=null && busitype.value=='1')
  		subsys_id = '35';
	
	var templId=$F('template_id');
	var method = '1';
	if(busitype!=null && busitype.value=='0')
	{
		if(examPlanForm.method[1] && examPlanForm.method[1].checked==true)
			method = '2';	
	}
	 method=3//程序暂时改为考核模板显示所有，不受考核方法的制约，但是考核方法随着考核模板变动
	 //method=1 显示非个性化项目的模板 method=2 显示个性化项目的模板 method=3 显示全部
	  var theurl="/performance/kh_system/kh_template/kh_template_tree.do?b_query=link`subsys_id="+subsys_id+"`isVisible=2`method="+method+"`isEdit=1`templateid="+templId;
      var iframe_url="/general/query/common/iframe_query.jsp?src="+$URL.encode(theurl);
      var height = parent.parent.document.body.offsetHeight;
      var width = parent.parent.document.body.offsetWidth;
      height = height<700?height*0.8:600;
      width = width<900?width*.08:800;
      parent.parent.onresize = function(){
    	  var h = parent.parent.document.body.offsetHeight;
          var w = parent.parent.document.body.offsetWidth;
          h = h<700?h*0.8:600;
          w = w<900?w*0.8:800;
    	  var templateWin = parent.parent.Ext.getCmp("template_win");
    	  if(templateWin){
    		  templateWin.setWidth(w);
    		  templateWin.setHeight(h);
    	  }
      }
      parent.parent.Ext.create("Ext.window.Window",{
        	id:'template_win',
        	width:width,
        	height:height,
        	title:'关联模板',
        	resizable:'yes',
        	modal:true,
        	autoScroll:false,
        	autoShow:true,
        	autoDestory:true,
        	html:"<iframe style='background-color:#ffffff' frameborder='0' SCROLLING=NO height='100%' width='100%' src='"+iframe_url+"'></iframe>",
        	listeners:{
        		'close':function(){
        			var return_vo = parent.parent.return_vo_template;
        			 if(return_vo==null){
        			      	return;
        			 }
        			 var template=return_vo.split(',');      
				     if(template!=null && $F('status')=='0')
				     {
				      	 document.getElementById('template_id').value=template[1];
				      	 document.getElementById('templateName').value=template[0];
				      	 var hashvo=new ParameterSet();
				      	 hashvo.setValue("template_id",template[1]);
				      	 var plan_id = $F('plan_id');
				      	 hashvo.setValue("plan_id",plan_id);
				      	 var request=new Request({method:'post',asynchronous:false,onSuccess:beforeUpdateMethod,functionId:'9022000023'},hashvo);	
				      }   
        		}
        	}
        });
}
// updateMethod页面刷新时会调用，但是无需清空requiredFieldStr add by 刘蒙
function beforeUpdateMethod(outparamters) {
	// 切换模板后，清空requiredFieldStr隐藏域的值
	var requiredFieldStr = document.getElementById('requiredFieldStr');
	requiredFieldStr.value = "";
	updateMethod(outparamters);
}
function updateMethod(outparamters)
{
	var flag=outparamters.getValue("flag");	
	var busitype = document.getElementById('planBusitype');
	
	if(busitype!=null && busitype.value=='0')
	{
		if(flag=='1')//个性化模板对应目标管理的考核方法
		{
			document.getElementById('methodflag').value="2";
			if(examPlanForm.method[1]){
				examPlanForm.method[1].click();
			}
//			examPlanForm.method[1].checked=true;
//			examPlanForm.method[1].disabled=false;//个性化模板只能用于目标计划
//			examPlanForm.method[0].disabled=true;
		}
		else
		{
			document.getElementById('methodflag').value="1";
			if(examPlanForm.method[0]){
				examPlanForm.method[0].click();
			}
//			examPlanForm.method[0].disabled=false;
//			examPlanForm.method[0].checked=true;
//			examPlanForm.method[1].disabled=false;//而非个性化模板既可用于360又可用于目标计划
		}
	}
}
function checkTemplateType(){
	     var template_id=document.getElementById("template_id").value;
	     if(template_id=="")
	     return;
	     var hashvo=new ParameterSet();
		 hashvo.setValue("template_id",template_id);
		 // 【2331】确定考核方法用 add by 刘蒙
		 var plan_id = $F('plan_id');
		 hashvo.setValue("plan_id",plan_id);
		 var request=new Request({method:'post',asynchronous:false,onSuccess:updateMethod,functionId:'9022000023'},hashvo);	
}
function setMehod()
{/* 修改模板 考核方法相应调整 但是允许用户自己再修改考核方法 所以注释了此处的代码
	if(document.getElementById('methodflag').value=="1")
		examPlanForm.method[0].checked=true;
	if(document.getElementById('methodflag').value=="2")
		examPlanForm.method[1].checked=true;	*/
	var a=document.getElementsByName("examPlanVo.string(method)");//考核方法
	var b=document.getElementsByName("examPlanVo.string(gather_type)");//考核类型
	if(a[1].checked){
		Element.hide("hide_radio");
		b[0].checked=true;	
	}else{
		Element.show("hide_radio");
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
function getParam()
{
	var busitype=document.getElementById('planBusitype');
	var planId=document.getElementById('plan_id').value;
	var status = document.getElementById('status').value;
	var templateId = document.getElementById('template_id').value;

	var object_type=2;		
	if(busitype!=null && busitype.value=='0')
	{		
		if (examPlanForm.object_type[0].checked==true)
			object_type=2;	
		else if (examPlanForm.object_type[1].checked==true)
			object_type=1;			
		else if (examPlanForm.object_type[2].checked==true)
			object_type=3;	
		else if (examPlanForm.object_type[3].checked==true)
			object_type=4;	
	}	
			
	var gather_type=0;
	for (var i=0;i<examPlanForm.gather_type.length;i++) 
	{
		if (examPlanForm.gather_type[i].checked==true)
			gather_type=i;	//0-网上 1-机读 2-网上+机读			
	}	

	var method = '1';
	if(busitype!=null && busitype.value=='0')
	{
		//兼容高校程序：306评估，新建计划的时候，计划参数按钮点击之后没有反应。 haosl 2017-11-4
		if(examPlanForm.method[1] && examPlanForm.method[1].checked==true)
			method = '2';
	}
	
	var requiredFieldStr = document.getElementById("requiredFieldStr");
	
	if(templateId=='')
		templateId='isNull';
	var target_url="/performance/kh_plan/kh_params.do?b_query=link`paramOper=detail`plan_id="+planId+"`status="+status+"`templateId="+templateId+'`object_type='+object_type+'`method='+method+'`gather_type='+gather_type;
	if (requiredFieldStr) {
		target_url += "`requiredFieldStr=" + requiredFieldStr.value;
	}
 	var iframe_url="/general/query/common/iframe_query.jsp?src="+$URL.encode(target_url);
 	if(isIE6()){
 		var return_vo= window.showModalDialog(iframe_url, "kh_param_options", 
 		"dialogWidth:660px; dialogHeight:680px;resizable:no;center:yes;scroll:no;status:no");
 	}else{
 		var return_vo= window.showModalDialog(iframe_url, "kh_param_options", 
        "dialogWidth:640px; dialogHeight:630px;resizable:no;center:yes;scroll:no;status:no");
 	}
	if (!return_vo) {
		return;
	}
	
	if (requiredFieldStr) { // 清空requiredFieldStr隐藏域 add by 刘蒙
		requiredFieldStr.value = return_vo.requiredFieldStr; // 将计划明细页的隐藏域requiredFieldStr置为参数页选择的指标 add by 刘蒙
	}
	if("1"==return_vo.byModel){///按岗位素质模型测评
		document.getElementsByName("gather_type")[1].disabled=true;
	}
	if("0"==return_vo.byModel){
		document.getElementsByName("gather_type")[1].disabled=false;
	}	
}
	function save()
	{	
		var planName = document.getElementById('name').value;
		if(trimStr(planName)=='')
		{
			alert(KHPLAN_NAMENONULL);
			return;
		}		
		var approve_result = document.getElementById('approve_result').value;
		if(approve_result==KHPLAN_AGREE)
			document.getElementById('approve_result').value='1';
		if(approve_result==KHPLAN_NOAGREE)
			document.getElementById('approve_result').value='0';
		
		var cycle = document.getElementById('cycle').value;
		if(cycle==KHPLAN_YEAR)	
			document.getElementById('cycle').value='0';
		if(cycle==KHPLAN_HALFYEAR)	
			document.getElementById('cycle').value='1';
		if(cycle==KHPLAN_QUARTER)	
			document.getElementById('cycle').value='2';	
		if(cycle==KHPLAN_MONTH)	
			document.getElementById('cycle').value='3';	
		if(cycle==KHPLAN_INDEFINETIME)	
			document.getElementById('cycle').value='7';	
	
		document.examPlanForm.action="/performance/kh_plan/examPlanAdd.do?b_save=link";
		document.examPlanForm.submit();	
	}
	function goback()
	{
		var planId=document.getElementById('plan_id').value;
		document.examPlanForm.action="/performance/kh_plan/examPlanList.do?b_query=query&currentPlan="+planId;
		document.examPlanForm.submit();	
	}
	function setkhtimeqj()
	{
		var cycle = document.getElementById('cycle').value;
		var theyear= document.getElementById('theyear').value;
		var thequarter = document.getElementById('thequarter').value;
		var themonth = document.getElementById('themonth').value;
		var start_date= document.getElementById('start_date').value;
		var end_date = document.getElementById('end_date').value;
		if(cycle=='0')				
			document.getElementById('khtimeqj').value=theyear+GZ_ANLAYSE_SETINFOR_YEAR;			
		if(cycle=='1')	
		{
			var thehalfyear= document.getElementById('thequarter').value;
			if(thehalfyear=='1')
				document.getElementById('khtimeqj').value=theyear+GZ_ANLAYSE_SETINFOR_YEAR+' '+ACHIEVEMENT_UPYEAR;
			else if(thehalfyear=='2')
				document.getElementById('khtimeqj').value=theyear+GZ_ANLAYSE_SETINFOR_YEAR+' '+ACHIEVEMENT_DOWNYEAR;
		}	
		if(cycle=='2')	
		{			
			if(thequarter=='01')
				document.getElementById('khtimeqj').value=theyear+GZ_ANLAYSE_SETINFOR_YEAR+' '+KHPLAN_ONEQUARTER;
		    if(thequarter=='02')
				document.getElementById('khtimeqj').value=theyear+GZ_ANLAYSE_SETINFOR_YEAR+' '+KHPLAN_TWOQUARTER;
			if(thequarter=='03')
				document.getElementById('khtimeqj').value=theyear+GZ_ANLAYSE_SETINFOR_YEAR+' '+KHPLAN_THREEQUARTER;
			if(thequarter=='04')
				document.getElementById('khtimeqj').value=theyear+GZ_ANLAYSE_SETINFOR_YEAR+' '+KHPLAN_FOREQUARTER;
		}
		if(cycle=='3')	
		{			
			if(themonth=='01')
				document.getElementById('khtimeqj').value=theyear+GZ_ANLAYSE_SETINFOR_YEAR+' '+KHPLAN_JANUARY;
			else if(themonth=='02')
				document.getElementById('khtimeqj').value=theyear+GZ_ANLAYSE_SETINFOR_YEAR+' '+KHPLAN_FEBRUARY;
			else if(themonth=='03')
				document.getElementById('khtimeqj').value=theyear+GZ_ANLAYSE_SETINFOR_YEAR+' '+KHPLAN_MARCH;
			else if(themonth=='04')
				document.getElementById('khtimeqj').value=theyear+GZ_ANLAYSE_SETINFOR_YEAR+' '+KHPLAN_APRIL;
			else if(themonth=='05')
				document.getElementById('khtimeqj').value=theyear+GZ_ANLAYSE_SETINFOR_YEAR+' '+KHPLAN_MAY;
			else if(themonth=='06')
				document.getElementById('khtimeqj').value=theyear+GZ_ANLAYSE_SETINFOR_YEAR+' '+KHPLAN_JUNE;
			else if(themonth=='07')
				document.getElementById('khtimeqj').value=theyear+GZ_ANLAYSE_SETINFOR_YEAR+' '+KHPLAN_JULY;
			else if(themonth=='08')
				document.getElementById('khtimeqj').value=theyear+GZ_ANLAYSE_SETINFOR_YEAR+' '+KHPLAN_AUGUEST;
			else if(themonth=='09')
				document.getElementById('khtimeqj').value=theyear+GZ_ANLAYSE_SETINFOR_YEAR+' '+KHPLAN_SEPTEMBER;
			else if(themonth=='10')
				document.getElementById('khtimeqj').value=theyear+GZ_ANLAYSE_SETINFOR_YEAR+' '+KHPLAN_OCTOBER;
			else if(themonth=='11')
				document.getElementById('khtimeqj').value=theyear+GZ_ANLAYSE_SETINFOR_YEAR+' '+KHPLAN_NOVEMBER;
			else if(themonth=='12')
				document.getElementById('khtimeqj').value=theyear+GZ_ANLAYSE_SETINFOR_YEAR+' '+KHPLAN_DECEMBER;		
		}
		if(cycle=='7')			
			document.getElementById('khtimeqj').value=replaceAll(start_date,'-','.')+'－'+replaceAll(end_date,'-','.');
	}
	function setCycle()
	{
		var cycle = document.getElementById('cycle').value;
		if(cycle=='0')	 
			document.getElementById('cycle').value=KHPLAN_YEAR;
		if(cycle=='1')	
			document.getElementById('cycle').value=KHPLAN_HALFYEAR;
		if(cycle=='2')	
			document.getElementById('cycle').value=KHPLAN_QUARTER;	
		if(cycle=='3')	
			document.getElementById('cycle').value=KHPLAN_MONTH;	
		if(cycle=='7')	
			document.getElementById('cycle').value=KHPLAN_INDEFINETIME;		
	}