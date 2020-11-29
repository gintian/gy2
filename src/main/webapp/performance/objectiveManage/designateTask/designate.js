function newDesignate(p0400,p0407,fromflag,p0401)
{
   document.getElementById("p7").value=getDecodeStr(p0407);
   document.getElementById("p4").value=p0400;
   document.designateTaskForm.action="/performance/objectiveManage/designateTask.do?b_add=add&fromflag="+fromflag+"&p0401="+p0401;
   document.designateTaskForm.submit();
}

function editDesignnate(taskid,opt)
{
   
       var thecodeurl="/performance/objectiveManage/designateTask.do?b_edit=edit`opt="+opt+"`task_id="+taskid; 
	   var iframe_url="/general/query/common/iframe_query.jsp?src="+$URL.encode(thecodeurl);
	   var config = {
			    width:400,
			    height:180,
			    type:'1',
			    id:'designateTask_win'
			}
	   modalDialog.showModalDialogs(iframe_url,"designateTask_win",config,designateTask_ok);
}
function designateTask_ok(returnValue){
	if(returnValue)
	{
	     var planid=document.getElementsByName("plan_id")[0].value;
	     var objectid=document.getElementsByName("objectid")[0].value;
	     document.designateTaskForm.action="/performance/objectiveManage/designateTask.do?b_init=link&plan_id="+planid+"&objectid="+objectid;
         document.designateTaskForm.submit(); 
    }
}
function deleteTask(type,taskid,p0400,group_id)
{
   if(confirm("确认删除下达任务？\r\n只能删除考核计划为分发状态，考核对象目标卡状态为起草或"+KH_PLAN_BACK+"的下达任务！"))
   {
      var hashvo=new ParameterSet();
      hashvo.setValue("type",type);
	  hashvo.setValue("taskid",taskid);
	  hashvo.setValue("p0400",p0400);
	  hashvo.setValue("group_id",group_id);
	  var request=new Request({method:'post',asynchronous:false,onSuccess:deleteOK,functionId:'90100170025'},hashvo);
       
   }
}
function deleteOK(outparameters)
{
  
     alert("删除成功(只删除了考核计划为分发状态，考核对象目标卡状态为起草或"+KH_PLAN_BACK+"的下达任务)！");
     var planid=document.getElementsByName("plan_id")[0].value;
     var objectid=document.getElementsByName("objectid")[0].value;
     document.designateTaskForm.action="/performance/objectiveManage/designateTask.do?b_init=link&plan_id="+planid+"&objectid="+objectid;
     document.designateTaskForm.submit(); 
}
function saveP0407()
{
   document.designateTaskForm.action="/performance/objectiveManage/designateTask.do?b_save=save&isclose=1";
   document.designateTaskForm.submit();
}
function closeExtWin(){
	if(window.showModalDialog){
		window.close();
	}else if(Ext.getCmp("designateTask_win")){
		Ext.getCmp("designateTask_win").close();
	}else if(parent.parent.Ext.getCmp("designateTask_win")){
		parent.parent.Ext.getCmp("designateTask_win").close();
	}
}
function newDesignateTT(task_type,p0400,fromflag,p0401,p0407,type,task_id,group_id)
{
   var qz="0";
   if(document.getElementById("qz")!=null && document.getElementById("qz").checked)
      qz="1";
    var thecodeurl="/performance/objectiveManage/select_object.do?b_init=init`task_type="+task_type+"`type="+type+"`task_id="+task_id+"`group_id="+group_id+"`p0407="+p0407+"`opt=init`p0400="+p0400+"`qz="+qz+"`fromflag="+fromflag+"`p0401="+p0401; 
	var iframe_url="/general/query/common/iframe_query.jsp?src="+$URL.encode(thecodeurl);
	var config = {
		width:500,
        height:600,
        title:'选择主办人',
        id:'newDesignateTTWin'
    }
   
  	modalDialog.showModalDialogs(iframe_url,null,config,newDesignateTT_callback);
}
function newDesignateTT_callback(returnValue){
	if(returnValue)
	{
		var planid=document.getElementsByName("plan_id")[0].value;
	    var objectid=document.getElementsByName("objectid")[0].value;
		document.designateTaskForm.action="/performance/objectiveManage/designateTask.do?b_init=link&plan_id="+planid+"&objectid="+objectid;
        document.designateTaskForm.submit(); 
   }
}
function newDesignate2(task_type,p0400,p0407,type,task_id,group_id)
{	
	//此处无奈只能采用全局变量给下面的回调方法用了
	var hashvo=new ParameterSet();
    hashvo.setValue("type",type);
	hashvo.setValue("taskid",task_id);
	hashvo.setValue("p0400",p0400);
	hashvo.setValue("group_id",group_id);
	hashvo.setValue("task_type",task_type);
	hashvo.setValue("p0407",p0407);
	window.newDesignate2_obj=hashvo;
     var theurl="/system/logonuser/org_tree.do?flag=1`showDb=1`selecttype=1`dbtype=1"+
                "`priv=1`isfilter=0`loadtype=1`chitemid=`orgcode=`dbpre=USR`showroot=true`callbackFunc=newDesignate2_callback";
                
         var iframe_url="/general/query/common/iframe_query.jsp?src="+$URL.encode(theurl);
         var width = 300;
         var height = 440;
         if(window.showModalDialog){
            width=310;
            height=400;
         }
	 	var config = {
			width:width,
	        height:height,
	        dialogArguments:1,
	        title:'组织机构',
	        id:'select_org_dialog1_win'
		 }
	    if(!window.showModalDialog){
	  		window.dialogArguments = 1;
	    }
	   
	modalDialog.showModalDialogs(iframe_url,null,config,newDesignate2_callback);
   
		
}
function newDesignate2_callback(return_vo){
    var ids="";  
	if(return_vo)
	{
	 	ids=return_vo.title+"/"+return_vo.content;
	}else{
	     return;
	}
	if(ids==""||ids=="/")
	{
	    alert("请选择协办人！");
	    return;
	}
	window.newDesignate2_obj.setValue("hiddenStr",ids);
	var request=new Request({method:'post',asynchronous:false,onSuccess:saveOK,functionId:'90100170026'},window.newDesignate2_obj);
}
function saveOK(outparameter)
{
	//在此处清掉全局变量
	if(window.newDesignate2_obj)
		window.newDesignate2_obj = null;
   var planid=document.getElementsByName("plan_id")[0].value;
   var objectid=document.getElementsByName("objectid")[0].value;
   document.designateTaskForm.action="/performance/objectiveManage/designateTask.do?b_init=link&plan_id="+planid+"&objectid="+objectid;
   document.designateTaskForm.submit(); 
}
function selectAllRecord(obj)
{
   var arr=document.getElementsByName("selids");
   for(var i=0;i<arr.length;i++)
   {
      if(obj.checked==true)
         arr[i].checked=true;
      else
         arr[i].checked=false;
   }
}
function selectOk()
{
   var arr=document.getElementsByName("selids");
   var ids='';
   for(var i=0;i<arr.length;i++)
   {
      var t="0";
      if(arr[i].checked==true)
         t="1";
      ids+="/"+arr[i].value+"`"+t;
   }
   if(ids!='')
   {
      ids=ids.substring(1);
      document.getElementById("hstr").value=ids;
   }else{
      alert("请选择考核对象！");
      return;
   }
    var to_itemid = document.getElementsByName("to_itemid")[0].value;
    if (!to_itemid){
        alert("请选择项目分类！");
        return;
    }
     document.designateTaskForm.action="/performance/objectiveManage/select_object.do?b_save=save&isClose=1";
     document.designateTaskForm.submit(); 
}
function deleteSinglePepole(taskid,p0400,group_id,a0101)
{
   if(confirm("确定收回【"+a0101+"】的下达任务？")){
    var hashvo=new ParameterSet();
	hashvo.setValue("taskid",taskid);
	hashvo.setValue("p0400",p0400);
	hashvo.setValue("group_id",group_id);
	var request=new Request({method:'post',asynchronous:false,onSuccess:saveOK,functionId:'90100170027'},hashvo);
   }
}