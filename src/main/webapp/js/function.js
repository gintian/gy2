function IsOverStrLength(str,len)
{
   return str.replace(/[^\x00-\xff]/g,"**").length>len
   
}

function IsOverStrLength2(str,len)
{
   return str.length>len
   
}

function trim(s){ 
  return s.replace(/^\s+|\s+$/, ''); 
} 
function ShowLayer(Layer){

      
  if (Layer.style.display == 'none') {    
    Layer.style.display = '';
	moveLayerToMouseLoc(Layer,-100,10);
  }
  else{
    Layer.style.display = 'none';
  }
}
//把指定层移到鼠标位置


function moveLayerToMouseLoc(theLayer, offsetH, offsetV)
{
    var obj;
    if (document.layers)  //NS
    {
      document.onMouseMove = getMouseLoc;
      obj = document.layers[theLayer];
      obj.left = mLoc.x +offsetH;
      obj.top  = mLoc.y +offsetV;
    }
    else if (document.all)//IE
    {
      getMouseLoc();
//	  theLayer.style.left = mLoc.x - theLayer.style.width +offsetH;
	  theLayer.style.left = mLoc.x +offsetH;
	  theLayer.style.top = mLoc.y +offsetV;
	  
    }
}
// get mouse location
function Point(x,y) {  this.x = x; this.y = y; }
mLoc = new Point(-500,-500);
function getMouseLoc(e)
{
  if(!document.all)  //NS
  {
    mLoc.x = e.pageX;
    mLoc.y = e.pageY;
  }
  else               //IE
  {
    mLoc.x = event.x + document.body.scrollLeft;
    mLoc.y = event.y + document.body.scrollTop;
  }
  return true;
}

//NS init:
if(document.layers){ document.captureEvents(Event.MOUSEMOVE); document.onMouseMove = getMouseLoc; }
//把指定层移到鼠标位置 －－－－结束
function checkDate(datestr,desc) {
	if(!datestr)
		return true;
	
	var dateValue = datestr.value;
	if(!dateValue)
		return true;
	
	dateValue = replaceAll(dateValue, "-", ".");
	dateValue = replaceAll(dateValue, "/", ".");
	dateValue = replaceAll(dateValue, "\\", ".");

	var timeValue = "";
	if(dateValue.length > 11){
		timeValue = dateValue.split(" ")[1];
		dateValue = dateValue.split(" ")[0];
	}
	
  if(!desc){
	  desc="";
  }else{
	  desc+=":";
  }
  if(datestr!="" && dateValue.length==4)
  {
     var reg = new RegExp("^(\\d{4})$");
     if(!reg.exec(dateValue)){
        alert(desc+"请输入正确的日期格式！（比如:YYYY或者YYYY.MM等）");
	datestr.focus();
	datestr.select();
	return false;
     }
     else
     {
     	if(dateValue<1753)
     	{
          alert(desc+"输入了不合法的日期！");
     	  return false
     	}
     	else
     	return true;
     }
  }
  if(datestr!="" && (dateValue.length==6 || dateValue.length==7))
  {
     var reg = new RegExp("^(\\d{4})[.](\\d{1,2})$");
     if(!reg.exec(dateValue)){
        alert(desc+"请输入正确的日期格式！（比如:YYYY或者YYYY.MM等）");
	datestr.focus();
	datestr.select();
	return false;
     }
     else
     {
     	var mm=dateValue.substr(dateValue.indexOf(".")+1,dateValue.length);
     	/*月份为0的时候报错，把等于0的情况删掉    xpy 2013 4 15*/
     	if(mm>12 || mm<1)
     	{
     	  alert(desc+"请输入正确的日期格式！（比如:YYYY或者YYYY.MM等）");
	  datestr.focus();
	  datestr.select();
	  return false;	
     	}
     	if(dateValue.substr(0,4)<1753)
     	{
          alert(desc+"输入了不合法日期！");
     	  return false
     	}
     	return true;
     }
  } 
 
  var reg = new RegExp("^(\\d{4})[.](\\d{1,2})[.](\\d{1,2})$");
  if(!reg.exec(dateValue)){
     alert(desc+"请输入正确的日期格式！（比如:YYYY或者YYYY.MM等）");
	datestr.focus();
	datestr.select();
	return false;
  }
   var lthdatestr;
   if (dateValue != "")
        lthdatestr= dateValue.length ;
   else
   {
   	return true;
	
        lthdatestr=0;
    }
    var tmpy="";
    var tmpm="";
    var tmpd="";
 
    var status=0;  
    for (i=0;i<lthdatestr;i++)
    {
        if (dateValue.charAt(i)== '.')
        {
            status++;
        }
        if (status>2)
        {

            alert(desc+"日期格式不正确，正确格式为yyyy.mm.dd！");
            return false;
        }
        if ((status==0) && (dateValue.charAt(i)!='.'))
        {
            tmpy=tmpy+dateValue.charAt(i)
        }
        if ((status==1) && (dateValue.charAt(i)!='.'))
        {
            tmpm=tmpm+dateValue.charAt(i)
        }
        //start tianye add
        if((status==2) && (dateValue.charAt(i)==" "))//田野添加带有时间的日期类型遇到‘ ’空格时day值已经获取完成 不在结束循环
        	break;
        //end
        if ((status==2) && (dateValue.charAt(i)!='.'))
        {
            tmpd=tmpd+dateValue.charAt(i)
        }
    }
    
    year=new String (tmpy);
    month=new String (tmpm);
    day=new String (tmpd)
    if(tmpm.length==0) month=new String ('01');
    if(tmpd.length==0) //day=new String ('01')
    {
        alert(desc+"日期格式不正确，正确格式为yyyy.mm.dd！");
        return false;
    }
    //tempdate= new String (year+month+day);
    //alert(tempdate);
    if ((tmpy.length!=4) || (tmpm.length>2) || (tmpd.length>2))
    {
        alert(desc+"日期格式不正确，正确格式为yyyy.mm.dd！");
        return false;
    }
    if (!((1<=month) && (12>=month) && (31>=day) && (1<=day)) )
    {
        alert (desc+"月份或天不正确！");
        return false;
    }
    if (!((year % 4)==0) && (month==2) && (day==29))
    {
        alert (desc+"不是闰年！");
        return false;
    }
    if ((month<=7) && ((month % 2)==0) && (day>=31))
    {
        alert (desc+"此月份是小月？");
        return false;
    }
    if ((month>=8) && ((month % 2)==1) && (day>=31))
    {
        alert (desc+"此月份是小月！");
        return false;
    }
    if ((month==2) && (day==30))
    {
        alert(desc+"二月份没有此日！");
        return false;
    }
    if(dateValue.substr(0,4)<1753)
    {
        alert(desc+"输入了不合法日期！");
     	return false
    }
    
    if(timeValue){
    	var reg = new RegExp("^(\\d{1,2})[:](\\d{1,2})[:](\\d{1,2})$");
        if(!reg.exec(timeValue)){
            alert(desc+"输入了不合法时间！");
		   	datestr.focus();
		   	datestr.select();
		   	return false;
        }
        
    	var h = "", m = "", s = "";
    	var t = timeValue.split(":");
    	h = t[0];
    	if(h < 0 || h > 23){
    		alert(desc+"输入了不合法时间！");
         	return false
    	}
    	
    	if(t.length > 1){
    		m = t[1];
    		if(m < 0 || m > 59){
        		alert(desc+"输入了不合法时间！");
             	return false
        	}
    	}
    	
    	if(t.length = 3){
    		s = t[2];
    		if(s < 0 || s > 59){
        		alert(desc+"输入了不合法时间！");
             	return false
        	}
    	}
    
    }
    
    return true;
}

function checkTime(times){
	if(!times){
		return true;
	}
	
	if(times.length==10||times.length==9){
 		var result=times.match(/^(\d{1,4})(.|\/)(\d{1,2})\2(\d{1,2})$/);
 		if(result==null) {
 			return false;
 		}
 		var d= new Date(result[1], result[3]-1, result[4]);
 		return (d.getFullYear()==result[1]&&(d.getMonth()+1)==result[3]&&d.getDate());
 	}else if(times.length==7||times.length==6){
 		var result=times.match(/^(\d{1,4})(.|\/)(\d{1,2})$/);
 		if(result==null) {
 			return false;
 		}
 		var d= new Date(result[1], result[3]-1,'01');
 		return (d.getFullYear()==result[1]&&(d.getMonth()+1)==result[3]&&d.getDate());
 	}else if(times.length==4){
 		var result=times.match(/^(\d{1,4})$/);
 		if(result==null) {
 			return false;
 		}
 		if(times>2100||times<1800){
 			return false;
 		}else{
 			return true;
 		}	
 	}else if(times.length==0){
 		return true;
 	}else{
 		return false;
 	}
}
function timeCheck(obj, desc){
	if(!checkTime(obj.value)){
		var msg = INPUT_FORMAT_DATE+"！"
		if(desc) {
			msg = desc + "：" + msg;
		}
		
		alert(msg);
		obj.focus();
		obj.value='';
	}
}

function validateDate(datestr,d_value)
{
  
  if(datestr!="" && d_value.length==4)
  {
   
     var reg = new RegExp("^(\\d{4})$");
     if(!reg.exec(d_value)){
        alert("请输入正确的日期格式！（比如:YYYY或者YYYY.MM等）");
	datestr.focus();
	datestr.select();
	return false;
     }
     else
     {
     	if(d_value<1753)
     	{
          alert("输入了不合法的日期！");
     	  return false
     	}
     	else
     	return true;
     }
  }
  if(datestr!="" && (d_value.length==6 || d_value.length==7))
  {
     var reg = new RegExp("^(\\d{4})[.](\\d{1,2})$");
     if(!reg.exec(d_value)){
        alert("请输入正确的日期格式！（比如:YYYY或者YYYY.MM等）");
	datestr.focus();
	datestr.select();
	return false;
     }
     else
     {
     	var mm=d_value.substr(d_value.indexOf(".")+1,d_value.length);
     	/*月份为0的时候报错，把等于0的情况删掉    xpy 2013 4 15*/
     	if(mm>12 || mm<1)
     	{
     	  alert("请输入正确的日期格式！（比如:YYYY或者YYYY.MM等）");
	  datestr.focus();
	  datestr.select();
	  return false;	
     	}
     	if(d_value.substr(0,4)<1753)
     	{
          alert("输入了不合法日期！");
     	  return false
     	}
     	return true;
     }
  } 
 
   var lthdatestr;
   if (d_value != "")
        lthdatestr= d_value.length ;
   else
   {
   	return true;
	
        lthdatestr=0;
    }
    var tmpy="";
    var tmpm="";
    var tmpd="";
 
    var status=0;  
    for (i=0;i<lthdatestr;i++)
    {
        if (d_value.charAt(i)== '.')
        {
            status++;
        }
        if (status>2)
        {

            alert("日期格式不正确，正确格式为yyyy.mm.dd！");
            return false;
        }
        if ((status==0) && (d_value.charAt(i)!='.'))
        {
            tmpy=tmpy+d_value.charAt(i)
        }
        if ((status==1) && (d_value.charAt(i)!='.'))
        {
            tmpm=tmpm+d_value.charAt(i)
        }
        //start tianye add
        if((status==2) && (d_value.charAt(i)==" "))//田野添加带有时间的日期类型遇到‘ ’空格时day值已经获取完成 不在结束循环
        	break;
        //end
        if ((status==2) && (d_value.charAt(i)!='.'))
        {
            tmpd=tmpd+d_value.charAt(i)
        }
    }
    
    year=new String (tmpy);
    month=new String (tmpm);
    day=new String (tmpd)
    if(tmpm.length==0) month=new String ('01');
    if(tmpd.length==0) //day=new String ('01')
    {
        alert("日期格式不正确，正确格式为yyyy.mm.dd！");
        return false;
    }
    //tempdate= new String (year+month+day);
    //alert(tempdate);
    if ((tmpy.length!=4) || (tmpm.length>2) || (tmpd.length>2))
    {
        alert("日期格式不正确，正确格式为yyyy.mm.dd！");
        return false;
    }
    if (!((1<=month) && (12>=month) && (31>=day) && (1<=day)) )
    {
        alert ("月份或天不正确！");
        return false;
    }
    if (!((year % 4)==0) && (month==2) && (day==29))
    {
        alert ("不是闰年！");
        return false;
    }
    if ((month<=7) && ((month % 2)==0) && (day>=31))
    {
        alert ("此月份是小月？");
        return false;
    }
    if ((month>=8) && ((month % 2)==1) && (day>=31))
    {
        alert ("此月份是小月！");
        return false;
    }
    if ((month==2) && (day==30))
    {
        alert("二月份没有此日！");
        return false;
    }
    if(d_value.substr(0,4)<1753)
    {
        alert("输入了不合法日期！");
     	return false
    }
    return true;
}



function checkNUM1(NUM,inputDesc)
{
    var i,j,strTemp;
    //alert(NUM);
    
    var desc="";
    if(arguments.length>1)
    	desc=arguments[1];
    if(desc===""&&inputDesc&&inputDesc!==""){
    	desc=inputDesc;
    }
    strTemp="0123456789";
    if ( NUM.value.length== 0)
    {
        return true;
    }    
    for (i=0;i<NUM.value.length;i++)
    {
    	
        j=strTemp.indexOf(NUM.value.charAt(i)); 
        if (j==-1  && NUM.value.charAt(0)!='-')
        {
            //说明有字符不是数字
            alert(desc+'请输入整数！');
            return false;
        }
    }
    //说明是数字
    return true;
}

function checkNUM2(NUM,len1,len2,desc)
{
    var i,j,strTemp;
    var str1,str2;
    var n=0;
   desc = desc!=undefined?desc:"";
  
    strTemp="-0123456789.";
    if ( NUM.value.length== 0)
    {
        return true;
    }    
    if(NUM.value.indexOf("-")!=-1&&NUM.value.indexOf("-")!=0)
	{
			    	alert('格式不正确！');
			    	return false; 
	}
	 if(NUM.value.length==1&&(NUM.value.indexOf(".")==0||NUM.value.indexOf("-")==0))
	{
			    	alert('格式不正确！');
			    	return false; 
    }
    for (i=0;i<NUM.value.length;i++)
    {
        j=strTemp.indexOf(NUM.value.charAt(i)); 
        if (j==-1)
        {
            //说明有字符不是数字
            alert(desc+'请输入数字！');
            return false;
        }   
        if(NUM.value.charAt(i)==".")
        {
            n=n+1;
        }   
        if(n>1)
        {
            alert(desc+'输入数据不能有两个以上的小数点!');
            return false;
        }
    }
     //alert(NUM.value  + "dd"  + len1);
  
    if(NUM.value.indexOf(".")!=-1)
    {
     	str1 = NUM.value.substr(0,NUM.value.indexOf("."));
     	str2 = NUM.value.substr(NUM.value.indexOf(".")+1,NUM.value.length);
     	
     	if(str1.length>len1)
     	  {
     	  	alert(desc+'整数部分位数最大为'+len1);
     	  	return false;
     	  }
        if(str2.length>len2)
        {
        	alert(desc+'请保留'+len2+'位小数');
     	  	return false;
        }
    }
    else
    {
    	str1 = NUM.value;
    	if(str1.length>len1 && len2>0)
     	  {
     	  	alert(desc+'整数部分位数最大为'+len1);
     	  	return false;
     	  }
    	else if(str1.length>len1 && len2<=0)
    	{
    		alert(desc+'所录入数值长度不能超过'+len1+'位');
     	  	return false;
    	}
    }   
    //说明是数字
    return true;
}

//检查是否是整数类型
	function checkIsIntNum(value)
	{
		return /^[0-9]*[1-9][0-9]*$/.test(value);
	}

//检查是否是数字类型(正数)
function checkIsNum(value)
{
	return /^\d+(\.\d+)?$/.test(value);
}
//检测输入的数字是否是正整数
function checkIsPositiveNum(object){
	if(!checkIsIntNum(object.value) || !checkIsNum(object.value)){
		object.value = '';
	}
}
//检查是否是数字类型
function checkIsNum2(value)
{
	return /^-?\d+(\.\d+)?$/.test(value);
}

//判断密码是否一致
function checkpassword(dobj,sdobj)
{
	if(dobj.value==sdobj.value)
	{
	  return true;
	}
	else
        {
           alert('验证密码不一致');
           return false;
	}
	 
}
/*****************************************************
*                                                    *
*                     日期处理函数                   *
*                                                    *
*****************************************************/
Date.prototype.toString = function(){
	return getDateString(this);
}
	
function getDateArray(datestr){
	var arr = datestr.split("-");
	arr[1] = arr[1].replace(/^0(\d)/, "$1");
	arr[2] = arr[2].replace(/^0(\d)/, "$1");
	return [parseInt(arr[0]),parseInt(arr[1])-1,parseInt(arr[2])];
}

function getDateByStr(dstr){
	var arr = getDateArray(dstr);
	return new Date(arr[0],arr[1],arr[2]);
}

function getDateString(d,separator){
	//zxj 20150722 没传separator会导致整数+undefined报错，间接影响到command.js中的Date.prototype.toString
	if (typeof(separator) == "undefined")
		return d.getTime().toString();
	
	var dstr = d.getUTCFullYear() + separator;
	if( (d.getMonth()+1) < 10 )
		dstr += "0";
	dstr += d.getMonth()+1;
	dstr += separator;
	if( d.getDate() < 10)
		dstr += "0";
	dstr += d.getDate();
	return dstr;
}

function getPreviousDate(dstr){
	var arr = getDateArray(dstr);
	var d = new Date(arr[0],arr[1],arr[2]-1);
	return d.getYear()+"-"+(d.getMonth()+1)+"-"+d.getDate();
}

function getNextDate(dstr){
	var arr = getDateArray(dstr);
	var d = new Date(arr[0],arr[1],arr[2]+1);
	return d.getYear()+"-"+(d.getMonth()+1)+"-"+d.getDate();
}

function getCurrentDate(){
	return getDateString(new Date());
}

function getCurrentTime(){
	var d = new Date();
	var s = "";
	s += d.getHours();
	s += ":";
	if( d.getMinutes() < 10 )
		s += "0" + d.getMinutes();
	else
		s += d.getMinutes();	
	s += ":";
	if( d.getSeconds() < 10 )
		s += "0" + d.getSeconds();
	else
		s += d.getSeconds();	

	return s;
}

/*****************************************************
*                                                    *
*                     DOM functions                  *
*                                                    *
*****************************************************/
function findParentNodeByTagName(obj, tagname){
	var p = obj;
	while(p != null && p.tagName != "BODY"){
		if(p.tagName == tagname)
			return p;
		p = p.parentNode;
	}
	return null;
}

function findChildNodesByTagName(obj, name){
	return obj.getElementsByTagName(name);
}

function findChildNodeByTagName(obj, name){
	return findChildNodesByTagName(obj, name)[0];
}

function setSelectObjectByIndex(selObj, index){
	selObj.options[index].selected = true;
}

function setSelectObjectByText(selObj, text){
	var v = null;
	for(var i=0; i<selObj.options.length; i++){
		selObj.options[i].selected = false;
		if(selObj.options[i].text == text){
			selObj.options[i].selected = true;
			v = selObj.options[i].value;
		}
	}
	return v;
}

function getSelectObjectText(selObj){
	return selObj.options[selObj.selectedIndex].text;
}
function getSelectObjectValue(selObj){
	return selObj.options[selObj.selectedIndex].value;
}
/*****************************************************
*                                                    *
*                     金额处理函数                   *
*                                                    *
*****************************************************/
function getFloatValue(v){
	var ret = parseFloat((v+"").trim());
	if(ret == null || isNaN(ret) == true)
		return 0;
	return ret;
}

/* 转换String到Float,保存两位精度 */		
function getMoneyValue(str){
	return getFloatValue(str.replace(/,/g, ''));
}

/* 转换参数(可以是字符串或者数字)到逗号分隔的金额格式 */		
function formatMoneyValue(value){
	var a = String(parseFloat(String(value)).toFixed(2));
	
	var rn = /^\d{1,3}(,\d{3})*\.\d\d$/;
	if(rn.test(a))
		return a;

	var r1 = /\d{4}\./;
	if(r1.test(a))
		a = a.replace(/(\d)(\d{3}\.)/,"$1,$2");
	else
		a = a.replace(/(\d)(\d{3})$/, "$1,$2");
	var r2 = /\d{4},/;
	while(r2.test(a))
		a = a.replace(/(\d)(\d{3},)/, "$1,$2");
	return a;
}
/*****************************************************
*                                                    *
*                     form处理函数                   *
*                                                    *
*****************************************************/

function disableForm(form){
	for(var i=0; i<form.elements.length; i++)
		form.elements[i].disabled = true;
}

function enableForm(form){
	for(var i=0; i<form.elements.length; i++)
		form.elements[i].disabled = false;
}

function disableElement(obj){
	if(obj.disabled == true)
		return;
	obj.disabled = true;
	obj._do1 = true;
}

function enableElement(obj){
	if(obj._do1 != true)
		return;
	obj.disabled = false;
	obj._do1 = null;
}

function disableDocument(){
	var ret;

	ret = document.getElementsByTagName("INPUT");
	for(var i=0; i<ret.length; i++)
		disableElement(ret[i]);
	ret = document.getElementsByTagName("SELECT");
	for(var i=0; i<ret.length; i++)
		disableElement(ret[i]);
	ret = document.getElementsByTagName("BUTTON");
	for(var i=0; i<ret.length; i++)
		disableElement(ret[i]);
}

function enableDocument(){
	var ret;

	ret = document.getElementsByTagName("INPUT");
	for(var i=0; i<ret.length; i++)
		enableElement(ret[i]);
	ret = document.getElementsByTagName("SELECT");
	for(var i=0; i<ret.length; i++)
		enableElement(ret[i]);
	ret = document.getElementsByTagName("BUTTON");
	for(var i=0; i<ret.length; i++)
		enableElement(ret[i]);
}
/*****************************************************
*                                                    *
*                     杂类函数                       *
*                                                    *
*****************************************************/
function getExceptionInfo(exception){
	var ret = new Object();
	var re = /^([^:]*):\s(.*)$/;
	
	var arr = exception.match(re);
	ret.type = arr[1];
	ret.msg = arr[2];
	return ret;
}

/*****************************************************
*                                                    *
*                     扩展Array方法                      *
*                                                    *
*****************************************************/
Array.prototype.indexOf=function(o){
	for(var i=0;i<this.length;i++){
		if(this[i]==o)
			return i;
	}
	return-1;
};
Array.prototype.lastIndexOf=function(o){
	for(var i=this.length-1;i>=0;i--){
		if(this[i]==o)
			return i;
	}
	return-1;
};
Array.prototype.contains=function(o){
	return this.indexOf(o)!= -1;
};
Array.prototype.copy=function(o){
	return this.concat();
};
Array.prototype.insertAt=function(o,i){
	this.splice(i,0,o);
};
Array.prototype.insertBefore=function(o,o2){
	var i=this.indexOf(o2);
	if(i== -1)
		this.push(o);
	else 
		this.splice(i,0,o);
};
Array.prototype.removeAt=function(i){
	this.splice(i,1);
};
Array.prototype.remove=function(o){
	var i=this.indexOf(o);if(i!= -1)
		this.splice(i,1);
};
Array.prototype.previousItem = function(o){
	var i = this.indexOf(o);
	if(i > 0)
		return this[i-1];
	return null;
}
Array.prototype.nextItem = function(o){
	var i = this.indexOf(o);
	if((i+1) < this.length)
		return this[i+1];
	return null;
}
function showcodefrm(){
  innerframe.style.display="";
  innerframe.style.left=document.body.scrollLeft+event.clientX-30;
  innerframe.style.top=document.body.scrollTop+event.clientY+5;
}
function hidefrm()
{
  innerframe.style.display="none";             
}



/*功能介绍：检查是否含有非字母
  参数说明：要检查的字符串
  返回值：0：含有 1：全部为字母 	*/

function fucPWDchk(str)
{
  var strSource ="abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
  var ch;
  var i;
  var temp;
  
  for (i=0;i<=(str.length-1);i++)
  {
  
    ch = str.charAt(i);
    temp = strSource.indexOf(ch);
    if (temp==-1) 
    {
     return 0;
    }
  }
  if (strSource.indexOf(ch)==-1)
  {
    return 0;
  }
  else
  {
    return 1;
  } 
}


/*功能介绍：检查是否含有非数字
  参数说明：要检查的字符串
  返回值：0：含有 1：全部为数字 	*/

function fucNumchk(str)
{
  var strSource ="0123456789.-";
  var ch;
  var i;
  var temp;
  
  for (i=0;i<=(str.length-1);i++)
  {
  
    ch = str.charAt(i);
    temp = strSource.indexOf(ch);
    if (temp==-1) 
    {
     return 0;
    }
  }
  if (strSource.indexOf(ch)==-1)
  {
    return 0;
  }
  else
  {
    return 1;
  } 
}

/* 功能介绍：替换方法 */
function replaceAll( str, from, to ) {
	    var idx = str.indexOf( from );
	    while ( idx > -1 ) {
	        str = str.replace( from, to ); 
	        idx = str.indexOf( from );
	    }
	   
	    return str;
}



/*功能介绍：检查email是否符合规则
*/
function fucEmailchk(str)
{
	var email = /^\w+([-+.]\w+)*@\w+([-.]\w+)*\.\w+([-.]\w+)*$/;
	return email.test(str)

}


/**四舍五入
*/
function  cheng(num,n)  
{
    var  dd=1;  
	var  tempnum;  
	for(i=0;i<n;i++)  
	{  
	dd*=10;  
	}  
	tempnum=num*dd;  
	tempnum=Math.round(tempnum);  
	return(tempnum/dd);  
}  	

/**Json特殊字符转换
 * 
 */
function jsonReplace(str)
{
	
	str = replaceAll(str,">", "&gt;");  
	str = replaceAll(str,"<", "&lt;");  
	str = replaceAll(str," ", "&nbsp;");  
	str = replaceAll(str,"\"", "&quot;");  
	str = replaceAll(str,"\'", "&#39;");  
	//str = replaceAll(str,"\\", "\\\\");//对斜线的转义  	这样转会死机
	str = str.replace(/\\/g,"\\\\");//对斜线的转义  
	str = replaceAll(str,"\r\n", "<br>"); 
	return str;
}


/**
 * 检查文件名中是否有非法字符
 * @param str
 * @return
 */
function checkFilename(str) {
	var filename=str.value;
    if(filename.indexOf("/")!=-1||filename.indexOf("<")!=-1||filename.indexOf(">")!=-1||filename.indexOf(":")!=-1
    	    ||filename.indexOf("\\")!=-1||filename.indexOf("|")!=-1||filename.indexOf("*")!=-1
    	    ||filename.indexOf("\"")!=-1||filename.indexOf("?")!=-1){
        alert(TRAIN_FILENAME_EORROR);
        str.focus();
    	str.value='';
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

//验证身份证号 
var Wi = [ 7, 9, 10, 5, 8, 4, 2, 1, 6, 3, 7, 9, 10, 5, 8, 4, 2, 1 ];    // 加权因子   
var ValideCode = [ 1, 0, 10, 9, 8, 7, 6, 5, 4, 3, 2 ];            // 身份证验证位值.10代表X   
function IdCardValidate(idCard) { 
    idCard = trim(idCard.replace(/ /g, ""));               //去掉字符串头尾空格                     
    if (idCard.length == 15) {   
        return isValidityBrithBy15IdCard(idCard);       //进行15位身份证的验证    
    } else if (idCard.length == 18) {   
        var a_idCard = idCard.split("");                // 得到身份证数组   
        if(isValidityBrithBy18IdCard(idCard)&&isTrueValidateCodeBy18IdCard(a_idCard)){   //进行18位身份证的基本验证和第18位的验证
            return true;   
        }else {   
            return false;   
        }   
    } else {   
        return false;   
    }   
}   
/**  
 * 判断身份证号码为18位时最后的验证位是否正确  
 * @param a_idCard 身份证号码数组  
 * @return  
 */  
function isTrueValidateCodeBy18IdCard(a_idCard) {   
    var sum = 0;                             // 声明加权求和变量   
    if (a_idCard[17].toLowerCase() == 'x') {   
        a_idCard[17] = 10;                    // 将最后位为x的验证码替换为10方便后续操作   
    }   
    for ( var i = 0; i < 17; i++) {   
        sum += Wi[i] * a_idCard[i];            // 加权求和   
    }   
    valCodePosition = sum % 11;                // 得到验证码所位置   
    if (a_idCard[17] == ValideCode[valCodePosition]) {   
        return true;   
    } else {   
        return false;   
    }   
}   
/**  
  * 验证18位数身份证号码中的生日是否是有效生日  
  * @param idCard 18位书身份证字符串  
  * @return  
  */  
function isValidityBrithBy18IdCard(idCard18){   
    var year =  idCard18.substring(6,10);   
    var month = idCard18.substring(10,12);   
    var day = idCard18.substring(12,14);   
    var temp_date = new Date(year,parseFloat(month)-1,parseFloat(day));   
    // 这里用getFullYear()获取年份，避免千年虫问题   
    if(temp_date.getFullYear()!=parseFloat(year)   
          ||temp_date.getMonth()!=parseFloat(month)-1   
          ||temp_date.getDate()!=parseFloat(day)){   
            return false;   
    }else{   
        return true;   
    }   
}   
  /**  
   * 验证15位数身份证号码中的生日是否是有效生日  
   * @param idCard15 15位书身份证字符串  
   * @return  
   */  
  function isValidityBrithBy15IdCard(idCard15){   
	  var reg = /^\d{15}$/;
      var year =  idCard15.substring(6,8);   
      var month = idCard15.substring(8,10);   
      var day = idCard15.substring(10,12);   
      var temp_date = new Date(year,parseFloat(month)-1,parseFloat(day));   
      // 对于老身份证中的年龄则不需考虑千年虫问题而使用getYear()方法   
      if(!reg.test(idCard15)||temp_date.getYear()!=parseFloat(year)   
              ||temp_date.getMonth()!=parseFloat(month)-1   
              ||temp_date.getDate()!=parseFloat(day)){   
                return false;   
        }else{   
            return true;   
        }   
  }   
  
  /**
   * 获取浏览器端URL（从协议到端口）
   * @return
   */
  function getRequestURL() {
	  return location.protocol + "//" + location.hostname + ":" + location.port;
  }

