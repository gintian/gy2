
//	Written by szk, 2013/09/03
function focusTest(id)
	{
		var focusItem = document.getElementById(id);
		focusItem.focus();
		focusItem.select();
		/***************
		var r =focusItem.createTextRange(); 
		r.moveStart('character',focusItem.value.length); 
		r.collapse(true); 
		r.select();
		光标在文本后  */

	}

function minute(name,fName,hh_mm) {         
	this.name = name;	
	this.fName = fName || "m_input";
	this.timer = null;
	this.fObj = null;
	var hh="";
	var mm="";
	var new_hh="00";
	var new_mm="00";
	if(hh_mm!=null)
	{
		
                var r = hh_mm.split(":");                
                if(r.length>1)
                {                 
                   hh=r[0];
                   mm=r[1];
                } 
               
	}   
	var new_hh=hh;
	var new_mm=mm;  
	this.toString = function() {
		var objDate = new Date();
		var sMinute_Common = "class=\"m_input\" maxlength=\"2\" name=\""+this.fName+"\" onfocus=\""+this.name+".setFocusObj(this)\" onblur=\""+this.name+".setTime(this)\"  onkeypress=\"if (!/[0-9]/.test(String.fromCharCode(event.keyCode)))event.keyCode=0\" onpaste=\"return false\" ondragenter=\"return false\" style=\"ime-mode:disabled\"";
		var sButton_Common = "class=\"m_arrow\" onfocus=\"this.blur()\" onmouseup=\""+this.name+".controlTime()\" "
		var str = "";
		str += "<table border=\"0\"  cellspacing=\"0\" cellpadding=\"0\" >"
		str += "<tr>"
		str += "<td>"
		str += "<div class=\"m_frameborder\">"
		str += "<input radix=\"24\" id=\"hh_t\" onkeyup=\""+this.name+".prevent(this)\"  value=\""+hh+"\"  "+sMinute_Common+">:"
		str += "<input radix=\"60\" id=\"mm_t\" onkeyup=\""+this.name+".move(this)\" value=\""+mm+"\"  "+sMinute_Common+">"		
		str += "</div>"
		str += "</td>"
		str += "<td>"
		str += "<table border=\"0\" cellspacing=\"2\" cellpadding=\"0\">"
		str += "<tr><td><button id=\""+this.fName+"_up\" "+sButton_Common+">5</button></td></tr>"
		str += "<tr><td><button id=\""+this.fName+"_down\" "+sButton_Common+">6</button></td></tr>"
		str += "</table>"
		str += "</td>"
		str += "</tr>"
		str += "</table>" 

		return str;		
	}
	this.play = function() {
		this.timer = setInterval(this.name+".playback()",1000);
	}
	this.formatTime = function(sTime) {
		sTime = ("0"+sTime);		
		return sTime.substr(sTime.length-2);
	}
	this.playback = function() {		
		var objDate = new Date();
		var arrDate = [hh,mm];
		var objMinute = document.getElementsByName(this.fName);
		for (var i=0;i<objMinute.length;i++)
		{
		   objMinute[i].value = this.formatTime(arrDate[i]);
		}
	}
	//min的keyup
	 this.move = function(obj)
	 {
	  var keycode = event.keyCode; 
	  this.setFocusObj(obj);
	  var value = parseInt(obj.value,10);
		var radix = parseInt(obj.radix,10)-1;
		if (obj.value>radix||obj.value<0) {
			obj.value = obj.value.substr(0,1);
		}
		if(keycode == 37&&this.getCursorPos(obj)==0&&obj.value.length==2){
			
			focusTest("hh_t");}
	 }
	//hour的keyup
	this.prevent = function(obj) {		
		clearInterval(this.timer);
		this.setFocusObj(obj);
		 var keycode = event.keyCode; 
		 //96-105 小键盘数字 47-58大键盘
		if((keycode<=105 &&keycode>=96 )||(keycode<=58 &&keycode>=47 )){
	
		var value = parseInt(obj.value,10);
		var radix = parseInt(obj.radix,10)-1;
		if (obj.value>radix||obj.value<0) {
			obj.value = obj.value.substr(0,1);
		}
		var conut=obj.value.length;
		if(conut == 2 || (obj.value!=1 &&obj.value!=0  &&obj.value!=2 ) )
		{
		focusTest("mm_t");
		}
		}
		if(keycode == 39){

			if(this.getCursorPos(obj)==2||obj.value==1 ||obj.value==0  ||obj.value==2 ){
				focusTest("mm_t");
			}
		
		}
	}
	//判断光标位置
	 this.getCursorPos = function(obj)
	 {
  var rngSel = document.selection.createRange();//建立选择域
  var rngTxt = obj.createTextRange();//建立文本域
  var flag = rngSel.getBookmark();//用选择域建立书签
  rngTxt.collapse();//瓦解文本域到开始位,以便使标志位移动
  rngTxt.moveToBookmark(flag);//使文本域移动到书签位
  rngTxt.moveStart('character',-obj.value.length);//获得文本域左侧文本
  str = rngTxt.text.replace(/\r\n/g,'');//替换回车换行符
  return(str.length);//返回文本域文本长度
}
	 
	this.controlTime = function(cmd) {	     
		event.cancelBubble = true;
		if (!this.fObj) return;
		clearInterval(this.timer);
		var cmd = event.srcElement.innerText=="5"?true:false;
		var t_value=this.fObj.value;
		if(t_value==null||t_value.length<=0)
		  t_value="0";
		var i = parseInt(t_value,10);
		var radix = parseInt(this.fObj.radix,10)-1;		
		if (i==radix&&cmd) {
			i = 0;
		} else if (i==0&&!cmd) {
			i = radix;
		} else {
			cmd?i++:i--;
		}
		this.fObj.value = this.formatTime(i);
		if(radix==23)
		{
		  new_hh=this.fObj.value;	
		}
		if(radix==59)
		{
		  new_mm=this.fObj.value;	
		}
		
		this.fObj.select();
	}
	var obj2="";
	this.setTime = function(obj)
	{
		var f_Time=this.formatTime(obj.value);
	
		 var objMinute = document.getElementsByName(this.fName);
		if(f_Time=="0"){
	       
		//  if( objMinute[0].value=="" ||  objMinute[0].value=="0"  )
			  obj.value="";
			//  else
			//  obj.value="00"; 
			  }
	        else
		 { obj.value = this.formatTime(obj.value);}
		
		
		if(obj.radix==24)
		{
			obj2=obj.value;
//			if(obj.value=="00" || obj.value =="01" || obj.value =="02"){
	//			focusTest("mm_t");	}
		}
		if(obj.radix==60&&obj2==""&&obj.value!="")
		{
		alert("请输入小时！");
			focusTest("hh_t");
		}
	
		
	}
		var hhh="1";
	this.setFocusObj = function(obj) {
			
		//eval(this.fName+"_up").disabled = eval(this.fName+"_down").disabled = false;
	
		if(obj.radix==24)
		{
		hhh=obj.value;
		
		}

		if(obj.radix==60&&obj.value==""&&hhh!="")
		{
		obj.value="00";
		obj.select();
		}
		this.fObj = obj;
	}
	this.getTime = function() {
		var arrTime = new Array(1);	
		var namelen = document.getElementsByName(this.fName).length;
		for (var i=0;i<namelen;i++) {
			arrTime[i] = document.getElementsByName(this.fName)[i].value;
			//IE浏览器下该方法会把同样的id也引过来，在页面会有个隐藏的id为this.fName的input，所以IE下除去 时、分两个input外还有一个，而Google edge等则只有这两个
			if(namelen==3){
				if(i==1){
					new_hh=document.getElementsByName(this.fName)[i].value;
				}
				if(i==2){
					new_mm=document.getElementsByName(this.fName)[i].value;
				}
			}else if(namelen==2){
				if(i==0){
					new_hh=document.getElementsByName(this.fName)[i].value;
				}
				if(i==1){
					new_mm=document.getElementsByName(this.fName)[i].value;
				}
			}
		}	
		return arrTime;
	}
	this.getNewTime = function() 
	{
		this.getTime();
		var arrTime="";				
		if(new_hh.length<=0&&new_mm.length<=0)
		{
			arrTime ="";
		}else
		{
			arrTime = new_hh+":"+new_mm;
	        }

		return arrTime;
	}	
}
