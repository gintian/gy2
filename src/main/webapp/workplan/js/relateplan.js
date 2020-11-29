var wpm =wpm || {
    plan_type:"1",
    period_type:"1",
    period_year:"2013",
    period_month:"1",
    period_week:"1"
}

function setJsVar() {
    var box = document.getElementById("plantype");
    if (box!=null){ 
        wpm.plan_type=box.value;
    }   
    var box = document.getElementById("periodtype");
    if (box!=null){ 
        wpm.period_type=box.value;
    }   
    var box = document.getElementById("periodyear");
    if (box!=null){ 
        wpm.period_year=box.value;
    }   
    var box = document.getElementById("periodmonth");
    if (box!=null){ 
        wpm.period_month=box.value;
        if (wpm.period_type=="4"){//保存本次月份
            wpm.old_period_month=wpm.period_month;
        }
    }   
    var box = document.getElementById("periodweek");
    if (box!=null){ 
        wpm.period_week=box.value;
    }  
 
}   
/*初始化关联计划界面*/
function initRelateForm() {
    setJsVar();
    initRelatePlan();
}   

/*加载关联计划*/
function initRelatePlan()
{
    var hashvo = new HashMap();
    hashvo.put("oprType","initPlan");
    hashvo.put("planType", wpm.plan_type);
    hashvo.put("periodType", wpm.period_type);
    hashvo.put("periodYear", wpm.period_year);
    hashvo.put("periodMonth", wpm.period_month);
    hashvo.put("periodWeek", wpm.period_week);
    Rpc( {functionId : '9028000705',success: initRelatePlan_ok}, hashvo);
}

function initRelatePlan_ok(response)
{
    var map = Ext.JSON.decode(response.responseText);
    var setlist = map.setlist;
    var table=document.getElementById("a_table");
    if (setlist!=null && setlist.length>0){
	   for(var i = setlist.length-1 ; i >=0 ;i--){
	        var rowLength=table.rows.length;
	        var list = setlist[i];
	        var trNode=table.insertRow(1);  
	        trNode.id="tr_"+list.plan_id;
	        //trNode.onclick=tronclick;
	        Ext.get(trNode.id).on("click",function(){tronclick(this);});
	        for(var j=0;j<5;j++){
	            var tdNode=trNode.insertCell(j);            
	            tdNode.className="RecordRow";
	            var text="";
	            tdNode.align="left";
	            if(j==0){
	               tdNode.align="center";
	               text=document.createTextNode(list.plan_id);
	               
	            } 
	            else if(j==1){
	               text=document.createTextNode(list.name);
	            } 
	            else if(j==2){
	               text=document.createTextNode(list.period_desc);
	            } 
	            else if(j==3){
	               text=document.createTextNode(list.status_desc);
	            } 
	            else if(j==4){
	               text=document.createTextNode(list.object_type);
	            } 
	           
	            tdNode.appendChild(text);
	    
	        }
	        
	    }
    
    }
 
}

 /* 新增计划 */
 function addPlan()
 {
	 var thecodeurl ="/workplan/relate_plan.do?b_addplan=link&oprType="
        +"&plantype="+wpm.plan_type
        +"&periodtype="+wpm.period_type+"&periodyear="+wpm.period_year
        +"&periodmonth="+wpm.period_month+"&periodweek="+wpm.period_week; 
	 Ext.create("Ext.window.Window",{
    	id:'addplan',
    	width:400,
    	height:210,
    	title:'创建新计划',
    	resizable:'no',
    	modal:true,
    	autoScroll:false,
    	autoShow:true,
    	autoDestory:true,
    	html:"<iframe style='background-color:#ffffff' frameborder='0' SCROLLING=NO height='100%' width='100%' src='"+thecodeurl+"'></iframe>",
    	listeners:{
    		'close':function(){
    			if(window.returnValue && window.returnValue.success=="1")  {
    	        	//开始关联
    				initAddedRelatePlan(returnValue.plan_id);
    	        }
    		}
    	}
    })	
 }
 
 
 /*加载刚刚新增的计划*/
function initAddedRelatePlan(plan_id)
{
    var hashvo = new HashMap();
    hashvo.put("oprType","initAddedPlan");
    hashvo.put("planId", plan_id);
    Rpc( {functionId : '9028000705',async: false,success: initRelatePlan_ok}, hashvo);
    var objtr=document.getElementById("tr_"+plan_id);
    if (objtr!=null){//定位刚新增的计划
	    tronclick(objtr);
    }
}

/*
初始计划界面

*/
function initAddForm() {
    setJsVar();
    initNewPlan();
}   


/*初始化新增计划界面*/
function initNewPlan()
{
    var hashvo = new HashMap();
    hashvo.put("oprType","initPlan");
    hashvo.put("planType", wpm.plan_type);
    hashvo.put("periodType", wpm.period_type);
    hashvo.put("periodYear", wpm.period_year);
    hashvo.put("periodMonth", wpm.period_month);
    hashvo.put("periodWeek", wpm.period_week);
    Rpc( {functionId : '9028000706',success: showNewPlanInfo}, hashvo);
}

function showNewPlanInfo(response)
{
    var map = Ext.JSON.decode(response.responseText);
    var plan_name = map.plan_name;
    
    var box = document.getElementById("planname");
    if (box!=null){ 
       box.value=plan_name;
    }  
}


/*保存新增计划*/
function saveNewPlan()
{
    var hashvo = new HashMap();
    hashvo.put("oprType","savePlan");
    hashvo.put("planType", wpm.plan_type);
    hashvo.put("periodType", wpm.period_type);
    hashvo.put("periodYear", wpm.period_year);
    hashvo.put("periodMonth", wpm.period_month);
    hashvo.put("periodWeek", wpm.period_week);
    var plan_name="";
    var box = document.getElementById("planname");
    if (box!=null){ 
       plan_name=box.value;
    }  
    var template_id="";
    var box = document.getElementById("template_id");
    if (box!=null){ 
       template_id=box.value;
    }  
    hashvo.put("planName", plan_name);
    hashvo.put("templateId", template_id);
    
    Rpc( {functionId : '9028000706',success: saveNewPlan_ok}, hashvo);
}

function saveNewPlan_ok(response)
{
        var retvo=new Object(); 
        retvo.success=1;
	    var map = Ext.JSON.decode(response.responseText);
        retvo.plan_id=map.plan_id;        
        parent.returnValue=retvo;
        if(parent.Ext && parent.Ext.getCmp("addplan"))
        	parent.Ext.getCmp("addplan").close();
}




