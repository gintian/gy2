function mincrease(obj_name,theMax,state) 
{
	var obj =document.getElementById(obj_name);      
  	if(parseInt(obj.value)<theMax){
		obj.value = (parseInt(obj.value)+1)+'';
		if(state!=null && state=='2'){
		   document.forms[0].action = '/performance/nworkplan/searchMonthWorkplan.do?b_query=link&init=1';
		   document.forms[0].submit();
		}else if(state=='1'){
		  
		}
  	}
}
function msubtract(obj_name,theMin,state) 
{
    var obj =document.getElementById(obj_name);      
  	if(parseInt(obj.value)>theMin){
		obj.value = (parseInt(obj.value)-1)+'';
  	    if(state!=null && state=='2'){
		   document.forms[0].action = '/performance/nworkplan/searchMonthWorkplan.do?b_query=link&init=1';
		   document.forms[0].submit();
		}else if(state=='1'){
		  
		}
  	}
}
function IsDigit() 
{ 
    return ((event.keyCode >= 47) && (event.keyCode <= 57)); 
}
function deleteworkplan(p0100,record_num,state){
	if(confirm("确认删除吗?")){
		if(state!=null && state=='2'){
		   document.forms[0].action = "/performance/nworkplan/searchMonthWorkplan.do?b_delete=link&p0100="+p0100+"&record_num="+record_num;
	       document.forms[0].submit();
		}else if(state=='1'){
		  document.forms[0].action = "/performance/nworkplan/week/searchWeekWorkplan.do?b_delete=link&p0100="+p0100+"&record_num="+record_num;
	      document.forms[0].submit();
		}
	}
}
function moveRecord(p0100,record_num,seq,moveflag,log_type,state){
	document.getElementById("hyperlinkP0100").value = p0100;
	document.getElementById("hyperlinkRecord").value = record_num;
    if(state!=null && state=='2'){
       document.forms[0].action = "/performance/nworkplan/searchMonthWorkplan.do?b_query=link&init=move&p0100="+p0100+"&record_num="+record_num+"&seq="+seq+"&moveflag="+moveflag+"&log_type="+log_type;
       document.forms[0].submit();
	}else if(state=='1'){
	  document.forms[0].action = "/performance/nworkplan/week/searchWeekWorkplan.do?b_query=link&init=move&p0100="+p0100+"&record_num="+record_num+"&seq="+seq+"&moveflag="+moveflag+"&log_type="+log_type;
       document.forms[0].submit();
	}
}
function outContent(content){
	config.FontSize='10pt';//hint提示信息中的字体大小
	Tip(getDecodeStr(content),STICKY,true);
}
function appiary(planDataSize,summarizeDataSize,sp_relation,personPage,p0100,state)
{	
    if(planDataSize=='0'&&summarizeDataSize=='0'){
        alert("请填写计划或总结之后再执行报批操作!");
        return false;
    }
	var hashvo = new ParameterSet();
	hashvo.setValue("sp_relation",sp_relation);
	hashvo.setValue("personPage",personPage);
	hashvo.setValue("p0100",p0100);
	hashvo.setValue("state",state);
	if(personPage=='1'){
//		if(confirm("确认报批吗?")){
//			if(state!=null && state=='2'){
//	          document.forms[0].action = "/performance/nworkplan/searchMonthWorkplan.do?b_appiary=link&personPage="+personPage;
//		      document.forms[0].submit();
//			}else if(state=='1'){
//				
//			}
//	    }
		if(confirm("确认报批吗?")){
			var request=new Request({method:'post',asynchronous:false,onSuccess:appiarySuccess,functionId:'302001020641'},hashvo);
		}	
	}else if(personPage=='0'){
		var request=new Request({method:'post',asynchronous:false,onSuccess:getSuperiorUser,functionId:'302001020639'},hashvo);
	}
}
function appiarySuccess(outparamters){
	var state = outparamters.getValue("state");
	if(state!=null && state=='2'){
	   window.location.href = '/performance/nworkplan/searchMonthWorkplan.do?b_query=link&init=1';
	}else if(state=='1'){
	  
	}
}
function getSuperiorUser(outparamters)
{
	var curr_user = "";
	var state = outparamters.getValue("state");
	var personPage = outparamters.getValue("personPage");
	var p0100 = outparamters.getValue("p0100");
	if(outparamters.getValue("outname").length==1)
	{
		curr_user = (outparamters.getValue("outname")[0].split(":"))[0];
		getA0101(curr_user,state,personPage,p0100);
		
	}else if(outparamters.getValue("outname").length>1)
	{
		var thecodeurl="/performance/workdiary/cat.jsp?outname="+outparamters.getValue("outname");
    	var return_vo= window.showModalDialog(thecodeurl, "_blank", 
              "dialogHeight:220px;dialogWidth:330px;center:yes;help:no;resizable:yes;status:no;scroll:no;");
       	if(return_vo!=null && return_vo.length>=0)
       	{
       		curr_user = return_vo;
       		getA0101(curr_user,state,personPage,p0100);
       	}
	}else
	{
		selectCurrUser(state,personPage,p0100);
	}
}
function selectCurrUser(state,personPage,p0100)
{
        //var return_vo=select_org_emp_dialog(1,2,1,0,0,1);
        var return_vo = select_org_emp_dialog2_jh("1","2","1","0","0","1","");
        if(return_vo){
            var curr_user = return_vo.content;
	        if(curr_user==""){
	          alert(PERFORMANCE_WORKPLAN_WORKPLANTEAM_INFO16);
	          return;
	        } 
	        getA0101(curr_user,state,personPage,p0100);
        }
}
//组织机构树如果显示人员，则先显示人员库
function select_org_emp_dialog2_jh(flag,selecttype,dbtype,priv,isfilter,loadtype,generalmessage)
{													//("1","1","0","1","0","1",generalmessage); 
	 var showSelfNode=0;
	 if(dbtype!=1)
	 	dbtype=0;
	 if(priv!=0)
	    priv=1;
     var theurl="/system/logonuser/org_employ_tree.do?b_query=link`flag="+flag+"`showSelfNode="+showSelfNode+"`showDb=1`tabid="+2+"`selecttype="+selecttype+"`dbtype="+dbtype+
                "`priv="+priv + "`isfilter=" + isfilter+"`loadtype="+loadtype+"`generalmessage="+"可以输入“姓名”，“拼音简码”进行查询";
      var iframe_url="/general/query/common/iframe_query.jsp?src="+$URL.encode(theurl);
      
     var return_vo= window.showModalDialog(iframe_url,1, 
        "dialogWidth:300px; dialogHeight:450px;resizable:no;center:yes;scroll:yes;status:no");
	 return return_vo;
}
function select_org_emp_dialog2_jh(flag,selecttype,dbtype,priv,isfilter,loadtype,generalmessage)
{													//("1","1","0","1","0","1",generalmessage); 
	 var showSelfNode=0;
	 if(dbtype!=1)
	 	dbtype=0;
	 if(priv!=0)
	    priv=1;
     var theurl="/system/logonuser/org_employ_tree.do?b_query=link`flag="+flag+"`showSelfNode="+showSelfNode+"`showDb=1`tabid="+2+"`selecttype="+selecttype+"`dbtype="+dbtype+
                "`priv="+priv + "`isfilter=" + isfilter+"`loadtype="+loadtype+"`generalmessage="+"可以输入“姓名”，“拼音简码”进行查询";
      var iframe_url="/general/query/common/iframe_query.jsp?src="+$URL.encode(theurl);
      
     var return_vo= window.showModalDialog(iframe_url,1, 
        "dialogWidth:300px; dialogHeight:450px;resizable:no;center:yes;scroll:yes;status:no");
	 return return_vo;
}
function show_org_tree_workplan(){
      var theurl="/performance/nworkplan/searchMonthWorkplan.do?br_tree=link";
      var iframe_url="/general/query/common/iframe_query.jsp?src="+theurl;  
      
     var return_vo= window.showModalDialog(iframe_url,1, 
        "dialogWidth:300px; dialogHeight:450px;resizable:no;center:yes;scroll:yes;status:no");
	 return return_vo;
}
function getA0101(curr_user,state,personPage,p0100){
     var hashvo = new ParameterSet();
     hashvo.setValue("curr_user",getEncodeStr(curr_user));
     hashvo.setValue("state",state);
     hashvo.setValue("personPage",personPage);
     hashvo.setValue("p0100",p0100);
     var request=new Request({method:'post',asynchronous:false,onSuccess:return_ok,functionId:'302001020640'},hashvo);
}
function return_ok(outparamters){
     var a0101 = outparamters.getValue("a0101");
     var curr_user = outparamters.getValue("curr_user");
     var state = outparamters.getValue("state");
     var personPage = outparamters.getValue("personPage");
     var p0100 = outparamters.getValue("p0100");
     curr_user = getDecodeStr(curr_user);
	 var hashvo = new ParameterSet();
     hashvo.setValue("curr_user",curr_user);		
     hashvo.setValue("personPage",personPage);		
     hashvo.setValue("p0100",p0100);		
     hashvo.setValue("state",state);		
     
       if(confirm(PERFORMANCE_WORKPLAN_WORKPLANVIEW_CONFIRM01+a0101+PERFORMANCE_WORKPLAN_WORKPLANVIEW_CONFIRM02)){
//          document.forms[0].action = "/performance/nworkplan/searchMonthWorkplan.do?b_appiary=link&curr_user="+curr_user;
//	      document.forms[0].submit();
	      var request=new Request({method:'post',asynchronous:false,onSuccess:appiarySuccess,functionId:'302001020641'},hashvo);
       }
       else
	   {
		return;
	   }
}
function goback(returnurl){
     window.location.href=returnurl;
}
function exportWeekWorkPlan(p0100,summarizeTime,summarizeFields,planFields){
      var hashvo = new ParameterSet();
	  hashvo.setValue("p0100",p0100);
	  hashvo.setValue("summarizeTime",summarizeTime);
	  hashvo.setValue("summarizeFields",summarizeFields);
	  hashvo.setValue("planFields",planFields);
	  var request=new Request({method:'post',asynchronous:false,onSuccess:exportWeekWorkPlanSuccess,functionId:'302001020648'},hashvo);
}
function exportWeekWorkPlanSuccess(outparamters){
	var outName=outparamters.getValue("outName");
	outName=getDecodeStr(outName);
	var name=outName.substring(0,outName.length-1)+".xls";
	name=getEncodeStr(name);
	window.location.target="_blank";
	window.location.href = "/servlet/vfsservlet?fromjavafolder=true&fileid="+name;
}
function exportMonthWorkPlan(p0100,currentYear,currentMonth,nextYear,nextMonth){
	var hashvo = new ParameterSet();
	hashvo.setValue("p0100",p0100); 
	hashvo.setValue("currentYear",currentYear); 
	hashvo.setValue("currentMonth",currentMonth); 
	hashvo.setValue("nextYear",nextYear); 
	hashvo.setValue("nextMonth",nextMonth); 
	var request=new Request({method:'post',asynchronous:false,onSuccess:exportWeekWorkPlanSuccess,functionId:'302001020680'},hashvo);
}
function searchMonthWorkInfo(personPage,state,isChuZhang,belong_type){
    var backurl = window.location.href;
    queryMonthWorkPlanForm.personPage.value = personPage;
    queryMonthWorkPlanForm.isChuZhang.value =  isChuZhang;
    queryMonthWorkPlanForm.backurl.value =  backurl;
    queryMonthWorkPlanForm.state.value =  state;
    queryMonthWorkPlanForm.belong_type.value =  belong_type;
    queryMonthWorkPlanForm.action = '/performance/nworkplan/queryMonthWorkPlan.do?b_query=link&init=init';
	queryMonthWorkPlanForm.submit();
}
function showperson(){
    var return_vo =show_org_tree_workplan();
     if(return_vo){
        var person = return_vo.content;
        if(person==""){
          alert("请选择人员");
          return;
        } 
        var hashvo = new ParameterSet();
	     hashvo.setValue("person",getEncodeStr(person));
	     var request=new Request({method:'post',asynchronous:false,onSuccess:showperson_ok,functionId:'302001020640'},hashvo);
    }
}
function showperson_ok(outparamters){
    var a0101 = outparamters.getValue("a0101");
    if(a0101!=''){
        document.getElementById("principal").value=a0101;
    }
}

