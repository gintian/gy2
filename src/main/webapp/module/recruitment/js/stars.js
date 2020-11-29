var _score; //分数 
var starli;
var obj; //显示星的对象
var b_onmouseover = "false";
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

//清空
function cleanstar()
{
   	for(var i=0 ;i < 10; i++){
   		if(i%2 == 0)
   		{	if(starli[i]==null)
   				return;
   			starli[i].className="starleft";
   		}
   		else
   		{
   			if(starli[i]==null)
   				return;
   			starli[i].className="starright";
   		}
    }
   	_score.value=-1;
}

//单击--修改为单击之后打分,onmouseover事件失效,再次单击可以重新打分
function submitstar(sobj,isok)
{
	if(b_onmouseover == "false"){
		if(isok =='ok'){
	 	for(var i=1 ;i <= 10; i++){
	 		starli[i-1].onmouseover = "null";
	 	}
		}
	 	sobj.parentNode.onmouseout = "return false";
	 	showstar(_score.value,sobj,0); 
	 	b_onmouseover = "true";
	 	return;
	}
	if(b_onmouseover == "true"){
		 Ext.getDom('score').value="-1";
		initstar('starlist');
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
//初始化 星星
function initstar(id)
{
	b_onmouseover = "false";
	_score = Ext.getDom("score");
	if((typeof id=='string')&&id.constructor==String){
		obj=document.getElementById(id);
	}
	else if((typeof id=='object'))
	{
		obj=id;
	}
	obj.innerHTML = "";
	if(_score.value >= 0){
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
	}
	else
	{
		obj.innerHTML =  " <li onmouseover=\"showstar(1,this,0);\" onclick=\"submitstar(this,'ok')\" title=\"0.5分\" class=\"starleft\"></li>"
			   +" <li onmouseover=\"showstar(2,this,0);\" onclick=\"submitstar(this,'ok')\" title=\"1分\" class=\"starright\"></li>"
			   +" <li onmouseover=\"showstar(3,this,0);\" onclick=\"submitstar(this,'ok')\" title=\"1.5分\" class=\"starleft\"></li>"
			   +" <li onmouseover=\"showstar(4,this,0);\" onclick=\"submitstar(this,'ok')\" title=\"2分\" class=\"starright\"></li>"
			   +" <li onmouseover=\"showstar(5,this,0);\" onclick=\"submitstar(this,'ok')\" title=\"2.5分\" class=\"starleft\"></li>"
			   +" <li onmouseover=\"showstar(6,this,0);\" onclick=\"submitstar(this,'ok')\" title=\"3分\" class=\"starright\"></li>"
			   +" <li onmouseover=\"showstar(7,this,0);\" onclick=\"submitstar(this,'ok')\" title=\"3.5分\" class=\"starleft\"></li>"
			   +" <li onmouseover=\"showstar(8,this,0);\" onclick=\"submitstar(this,'ok')\" title=\"4分\" class=\"starright\"></li>"
			   +" <li onmouseover=\"showstar(9,this,0);\" onclick=\"submitstar(this,'ok')\" title=\"4.5分\" class=\"starleft\"></li>"
			   +" <li onmouseover=\"showstar(10,this,0);\" onclick=\"submitstar(this,'ok')\" title=\"5分\" class=\"starright\"></li>";
			obj.onmouseout = function()   
		    {   
				cleanstar();         
			    };   
			_score.value=-1;
		
	}  
	 
}
Ext.onReady(function(){
	//初始化静态星星控件
	$("div[showStart]").each(function(i){
		
		var startScore = $(this).find(".startScore").text()
		var str = '';
		//左半部分
        for (var i = 0; i < startScore; i++) {
			if(i%2==0){ 
				str += '<LI class="starleft starlefton"></LI> ';
			}else{
				str += '<LI class="starright starrighton"></LI>';
			}
        }
		//右半部分
        for (var j = startScore; j < 10; j++) {
			if(j%2==0){ 
				str += '<LI class=starleft></LI> ';
			}else{
				str += '<LI class=starright></LI>';
			}
        }
       $(this).find(".showStartScore").append(str);
	});
})
