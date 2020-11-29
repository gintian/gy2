var _score = Ext.getDom('score'); //分数
var starli;
var obj; //显示星的对象
var b_onmouseover = "false";
var scoreNumb = -1;
// 选星显示 
function showstar(num,obj1,type)
{
	if(type == 1){
	starli =obj.getElementsByTagName("li"); 
	}
	else
	{
		starli=obj1.parentNode.getElementsByTagName("li"); 
	}
	for(var i=0 ;i < 10; i++){
   		if(i%2 == 0)
   		{	if(i+1>num)
   			{
   			starli[i].className="starleft";
   			}else
   			{
   			starli[i].className="starleft starlefton";
   			}
   		}
   		else
   		{
   			if(i+1>num)
   			{
   				starli[i].className="starright";
   			}else
   			{
   				starli[i].className="starright starrighton";
   			}
   		}
    }
   	_score.value=num;
}

function getStarNum(num,obj1,type)
{
	if(type == 1){
	starli =obj.getElementsByTagName("li"); 
	}
	else
	{
		starli=obj1.parentNode.getElementsByTagName("li"); 
	}
	
   	_score.value=num;
   	//alert(num);
}

//清空
function cleanstar()
{
   	for(var i=0 ;i < 10; i++){
   		if(i%2 == 0)
   		{	
   			starli[i].className="starleft";
   		}
   		else
   		{
   			starli[i].className="starright";
   		}
    }
   	_score.value=-1;
}

//单击--修改为单击之后打分,onmouseover事件失效,再次单击可以重新打分wusy
function submitstar(sobj,isok)
{
	if(b_onmouseover == "false"){
		//var starlist = Ext.query("#starlist li");
		if(isok =='ok'){
	 	for(var i=1 ;i <= 10; i++){
	 		starli[i-1].onmouseover = "null";
	 	}
		}
	 	sobj.parentNode.onmouseout = "return false";
	 	showstar(_score.value,sobj,0); 
	 	b_onmouseover = "true";
	 	scoreNumb = sobj.getAttribute('num');//获取重新打分的分数
	 	clickScore = true;//计划页面弹出打分窗口后,想重新打分用到的的变量
	 	return;
	}
	if(b_onmouseover == "true"){
		Ext.getDom('score').value="-1";
		//打分是工作计划和工作总结都调用的,所以要区分是计划还是总结,evaluation-score是工作计划页面中的一个元素id
		var plan = document.getElementById("evaluation-score");
		if(plan){
			initstar('evaluation-score');
		}else{
			var pingjia = document.getElementById("showpingjia");
			if(pingjia){
				pingjia.style.display="inline";
			}
			var starShow1 = document.getElementById("starshow1");
			if(starShow1){
			    starShow1.style.display="none";
			}
			
//			if(document.getElementById('starshow1')){//注释掉，starShow1在上面刚刚判断如果存在的话就不显示，这句又开始初始化，造成星星不会显示正确的值 chent 20160324
//			 	initstar('starshow1');
//			}else 
			
			if(document.getElementById('starId')){
			 	initstar('starId','-1');
			}else if(document.getElementById('starlist')){
				initstar('starlist');
			}else if(document.getElementById('newScore')){
				initstar('newScore','-1');
			}
//			document.getElementById("showpingjia").style.display="inline";
//		    document.getElementById("starshow1").style.display="none";
//			initstar('starlist');
//		 	initstar('starshow1');
		}
	    b_onmouseover = "false";
	    return;
		
	}
}
//重新打分 
function restar(restar)
{
	_score.value = -1;
	initstar(restar.parentNode.id);
}
function biubiu(thisEle){
	var win = Ext.getCmp('scoreWin');//计划页面打分窗口
	if(win){	
		initstar('starId', -1);
	}else{
		var num = thisEle.getAttribute('num');
		var taskId = thisEle.parentNode.getAttribute('taskId');
		var taskName = thisEle.parentNode.getAttribute('taskName');
		var planId = thisEle.parentNode.getAttribute('planId');
		var objectid = thisEle.parentNode.getAttribute('objectid');
		var p0723 = thisEle.parentNode.getAttribute('p0723');
		var role = thisEle.parentNode.getAttribute('role');
		if(typeof(editScore) == 'function'){
			editScore(planId, p0723, objectid, taskId, taskName, num,role);
		}
		clickScore = false;	
	}
}


//初始化 星星
function initstar2(id, num)
{
	//alert(typeof(num)!="undefined");
	clickScore = true;
	if(typeof(num)!="undefined"){
		num = parseInt(num);
		_score = {};
		_score.value = num;
		//重新打分,需要将b_onmouseover置为false,调用submitstar时才会重新打分
		if(num==-1){
			b_onmouseover = "false";
		}
	}
	if((typeof id=='string')&&id.constructor==String){
		obj=document.getElementById(id);
	}
	else if((typeof id=='object'))
	{
		obj=id;
	}
	if(obj){//排除obj可能不存在的情况
	
	}else{
		return;
	}
	obj.innerHTML = "";
	obj.innerHTML =  " <li onmouseover=\"showstar(1,this,0);\" onclick=\"submitstar(this,'ok')\" class=\"starleft\" num=\"1\"></li>"
		   +" <li onmouseover=\"showstar(2,this,0);\" onclick=\"submitstar(this,'ok')\" class=\"starright\" num=\"2\"></li>"
		   +" <li onmouseover=\"showstar(3,this,0);\" onclick=\"submitstar(this,'ok')\" class=\"starleft\" num=\"3\"></li>"
		   +" <li onmouseover=\"showstar(4,this,0);\" onclick=\"submitstar(this,'ok')\" class=\"starright\" num=\"4\"></li>"
		   +" <li onmouseover=\"showstar(5,this,0);\" onclick=\"submitstar(this,'ok')\" class=\"starleft\" num=\"5\"></li>"
		   +" <li onmouseover=\"showstar(6,this,0);\" onclick=\"submitstar(this,'ok')\" class=\"starright\" num=\"6\"></li>"
		   +" <li onmouseover=\"showstar(7,this,0);\" onclick=\"submitstar(this,'ok')\" class=\"starleft\" num=\"7\"></li>"
		   +" <li onmouseover=\"showstar(8,this,0);\" onclick=\"submitstar(this,'ok')\" class=\"starright\" num=\"8\"></li>"
		   +" <li onmouseover=\"showstar(9,this,0);\" onclick=\"submitstar(this,'ok')\" class=\"starleft\" num=\"9\"></li>"
		   +" <li onmouseover=\"showstar(10,this,0);\" onclick=\"submitstar(this,'ok')\" class=\"starright\" num=\"10\"></li>";
	showstar(_score.value,obj,1); 
}

//初始化 星星
function initstar(id, num)
{
	//alert(typeof(num)!="undefined");
	clickScore = true;
	if(typeof(num)!="undefined"){
		num = parseInt(num);
		_score = {};
		_score.value = num;
		//重新打分,需要将b_onmouseover置为false,调用submitstar时才会重新打分
		if(num==-1){
			b_onmouseover = "false";
		}
	}
	if((typeof id=='string')&&id.constructor==String){
		obj=document.getElementById(id);
	}
	else if((typeof id=='object'))
	{
		obj=id;
	}
	if(obj){//排除obj可能不存在的情况
	
	}else{
		return;
	}
	obj.innerHTML = "";
	if(_score.value >= 0){
//		obj.innerHTML = " <li  class=\"starleft\"></li>"
//					   +" <li  class=\"starright\"></li>"
//					   +" <li  class=\"starleft\"></li>"
//					   +" <li  class=\"starright\"></li>"
//					   +" <li  class=\"starleft\"></li>"
//					   +" <li   class=\"starright\"></li>"
//					   +" <li   class=\"starleft\"></li>"
//					   +" <li   class=\"starright\"></li>"
//					   +" <li   class=\"starleft\"></li>"
//					   +" <li   class=\"starright\"></li>";
		for (var i = 0; i < 5; i++) {
			var left = document.createElement("li");
			left.className = 'starleft';
			var right = document.createElement("li");
			right.className = 'starright';
			obj.appendChild(left);
			obj.appendChild(document.createTextNode(" "));
			obj.appendChild(right);
			obj.appendChild(document.createTextNode(" "));
		}
		
	    starli =obj.getElementsByTagName("li"); 
	    obj.onmouseout = "return false";
	    showstar(_score.value,obj,1); 
			
	    //为适应工作计划页面上级评分列中需求(点击后可评分,点击不同的星星弹出框会对应出现对应的打分)
	    for(var k=1; k<=starli.length; k++){
		   starli[k-1].setAttribute('num',k);
		   starli[k-1].setAttribute('onclick',"biubiu(this)");
	    }
	}
	else
	{
		obj.innerHTML =  " <li onmouseover=\"showstar(1,this,0);\" onclick=\"submitstar(this,'ok')\" class=\"starleft\" num=\"1\"></li>"
			   +" <li onmouseover=\"showstar(2,this,0);\" onclick=\"submitstar(this,'ok')\" class=\"starright\" num=\"2\"></li>"
			   +" <li onmouseover=\"showstar(3,this,0);\" onclick=\"submitstar(this,'ok')\" class=\"starleft\" num=\"3\"></li>"
			   +" <li onmouseover=\"showstar(4,this,0);\" onclick=\"submitstar(this,'ok')\" class=\"starright\" num=\"4\"></li>"
			   +" <li onmouseover=\"showstar(5,this,0);\" onclick=\"submitstar(this,'ok')\" class=\"starleft\" num=\"5\"></li>"
			   +" <li onmouseover=\"showstar(6,this,0);\" onclick=\"submitstar(this,'ok')\" class=\"starright\" num=\"6\"></li>"
			   +" <li onmouseover=\"showstar(7,this,0);\" onclick=\"submitstar(this,'ok')\" class=\"starleft\" num=\"7\"></li>"
			   +" <li onmouseover=\"showstar(8,this,0);\" onclick=\"submitstar(this,'ok')\" class=\"starright\" num=\"8\"></li>"
			   +" <li onmouseover=\"showstar(9,this,0);\" onclick=\"submitstar(this,'ok')\" class=\"starleft\" num=\"9\"></li>"
			   +" <li onmouseover=\"showstar(10,this,0);\" onclick=\"submitstar(this,'ok')\" class=\"starright\" num=\"10\"></li>";
			  // +" <li title=\"重新打分\" class=\"restar\" onclick=\"restar(this)\"></li>";
			obj.onmouseout = function()   
		    {   
				cleanstar();         
			    };   
			_score.value=-1;
		
	}
}
