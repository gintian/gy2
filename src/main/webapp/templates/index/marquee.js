/**
 * Marquee 替代，改为无缝滚动 
 * @author xiaoyun 2014-7-28
 */
/**
 * Marquee 调用方法
 * new Marquee({obj, name, mode, speed, autoStart, movePause});
 * obj：Object滚动对象或者滚动对象id (*必须)
 * name：String实例名 (*可选，默认随机)
 * mode：String滚动模式(x=水平, y=垂直) (*可选,默认为x)
 * speed：Number滚动速度，越小速度越快 (*可选，默认10)
 * autoStart：Boolean自动开始 (*可选，默认True)
 * movePause：Boolean鼠标经过是否暂停 (*可选，默认True)
 */
var rand = function(n, u){
 var tmStr = "abcdefghijklmnopqrstuvwxyz0123456789";
 var Len = tmStr.length;
 var Str = "";
 for(i=1;i<n+1;i++){
  Str += tmStr.charAt(Math.random()*Len);
 }
 return (u ? Str.toUpperCase() : Str);
};

var getObj = function(element) {
	return typeof(element) == 'object' ? element : document.getElementById(element);
}

var MyMarquees = new Array();
// 获取检测实例名
function getMyMQName(mName) {
 var name = mName==undefined ? rand(5) : mName;
 var myNames = ','+ MyMarquees.join(',') +',';
 
 while(myNames.indexOf(','+ name +',')!=-1) {
  name = rand(5);
 }
 return name;
}
function Marquee(inits) {
 var _o = this;
 var _i = inits;
 
 if(_i.obj==undefined) return;
 _o.mode    = _i.mode==undefined ? 'x' : _i.mode;   // 滚动模式(x:横向, y:纵向)
 _o.mName = getMyMQName(_i.name);       // 实例名
 _o.mObj  = getObj(_i.obj);         // 滚动对象
 if(!_o.mObj)
	 return;
 _o.speed = _i.speed==undefined ? 10 : _i.speed;   // 滚动速度
 _o.autoStart= _i.autoStart==undefined ? true : _i.autoStart;// 自动开始
 _o.movePause= _i.movePause==undefined ? true : _i.movePause;// 鼠标经过是否暂停
 
 /* 解决无缝滚动记录少时滚动不灵的问题 新加属性：系统版本 add by xiaoyun 2014-9-19 start */
 _o.bosflag = _i.bosflag;
 /* 解决无缝滚动记录少时滚动不灵的问题 新加属性：系统版本 add by xiaoyun 2014-9-19 end */
 
 _o.mDo  = null;           // 计时器
 _o.pause = false;          // 暂停状态
 
 // 无间滚动初始化
 _o.init = function() {
  if((_o.mObj.scrollWidth<=_o.mObj.offsetWidth && _o.mode=='x') && (_o.mObj.scrollHeight<=_o.mObj.offsetHeight && _o.mode=='y')) return;
 /* 解决无缝滚动记录少时滚动不灵的问题 xiaoyun 2014-9-18 start */
 var divHeight,childHeight; 
 if(_o.mode == 'y') {  	
  	divHeight = _o.mObj.offsetHeight;
	if(_o.mObj.scrollHeight <= divHeight) {
		// 获取单元格高度
		var height = _o.mObj.getElementsByTagName('td')[0].style.height;
		// 给table动态增加缺少的行数
		var length = _o.mObj.getElementsByTagName('tr').length;
		/* 将字串小写会引起参数加密错误 xiaoyun 2014-9-29 start */
		//var tab = _o.mObj.innerHTML.toLocaleLowerCase();
		var tab = _o.mObj.innerHTML;
		//var tab1 = tab.substr(0, tab.lastIndexOf('</td>')+5);
		var tab1;
		/* 兼容火狐和chrome xiaoyun 2014-10-8 start */
		if(window.ActiveXObject) {
			tab1 = tab.substr(0, tab.lastIndexOf('</TD>')+5);
		}else {
			tab1 = tab.substr(0, tab.lastIndexOf('</td>')+5)
		}
		/* 兼容火狐和chrome xiaoyun 2014-10-8 end */
		/* 将字串小写会引起参数加密错误 xiaoyun 2014-9-29 end */
		// 缺少的行数
		var other;
		if(_o.bosflag == 'hcm') {
			other = 5-length;
			for(var i = 0; i < other;i++) {
				tab1 += "<tr><td height='"+height+"'>&nbsp;</td></tr>";	
			}			
		}else {
			other = 7-length;
			for(var i = 0; i < other;i++) {
				tab1 += "<tr><td height='"+height+"'>&nbsp;</td></tr>";	
			}			
		}
		/* 将字串小写会引起参数加密错误 xiaoyun 2014-9-29 start */
		//var tab2 = tab.substr(tab.lastIndexOf('</tr>')+5);
		/* 兼容火狐和chrome xiaoyun 2014-10-8 start */
		var tab2;
		if (window.ActiveXObject) {
			tab2 = tab.substr(tab.lastIndexOf('</TD>')+5);
		}else {
			tab2 = tab.substr(tab.lastIndexOf('</td>')+5);
		}
		/* 兼容火狐和chrome xiaoyun 2014-10-8 end */
		/* 将字串小写会引起参数加密错误 xiaoyun 2014-9-29 end */
		tab =  tab1+tab2;
		_o.mObj.innerHTML = tab;
	}
	childHeight = _o.mObj.scrollHeight > divHeight? _o.mObj.scrollHeight : 160;
 }
 /* 解决无缝滚动记录少时滚动不灵的问题 xiaoyun 2014-9-18 end */
  MyMarquees.push(_o.mName);
  // 克隆滚动内容
  _o.mObj.innerHTML = _o.mode=='x' ? (
   '<table width="100%" border="0" align="left" cellpadding="0" cellspace="0">'+
   ' <tr>'+
   '  <td id="MYMQ_'+ _o.mName +'_1">'+ _o.mObj.innerHTML +'</td>'+
   '  <td id="MYMQ_'+ _o.mName +'_2">'+ _o.mObj.innerHTML +'</td>'+
   ' </tr>'+
   '</table>'
  ) : (
   '<div id="MYMQ_'+ _o.mName +'_1" style="height:'+childHeight+';margin-top:0px;margin-bottom:0px;">'+ _o.mObj.innerHTML +'</div>'+
   '<div id="MYMQ_'+ _o.mName +'_2" style="height:'+childHeight+';margin-top:0px;margin-bottom:0px;">'+ _o.mObj.innerHTML +'</div>'
  );
  // 获取对象、高宽
  _o.mObj1 = getObj('MYMQ_'+ _o.mName +'_1');
  _o.mObj2 = getObj('MYMQ_'+ _o.mName +'_2');
  _o.mo1Width = _o.mObj1.scrollWidth;
  _o.mo1Height = _o.mObj1.scrollHeight;
  
  // 初始滚动
  if (_o.autoStart) {
	  _o.start();
  }
 };
  
 // 开始滚动
 _o.start = function() {
  _o.mDo = setInterval((_o.mode=='x' ? _o.moveX : _o.moveY), _o.speed);
  if(_o.movePause) {
   _o.mObj.onmouseover = function() {_o.pause = true;} 
   _o.mObj.onmouseout = function() {_o.pause = false;}
  }
 }
 
 // 停止滚动
 _o.stop = function() {
  clearInterval(_o.mDo)
  _o.mObj.onmouseover = function() {} 
  _o.mObj.onmouseout = function() {}
 }
 
 // 水平滚动
 _o.moveX = function() {
  if(_o.pause) return;
  var left = _o.mObj.scrollLeft;
  if(left==_o.mo1Width){ 
   _o.mObj.scrollLeft = 0 ;
  }else if(left>_o.mo1Width) {
   _o.mObj.scrollLeft = left-_o.mo1Width;
  }else{ 
   _o.mObj.scrollLeft++;
  }
 };
 
 // 垂直滚动
 _o.moveY = function() {
  if(_o.pause) return;
  var top = _o.mObj.scrollTop;
  if(top ==_o.mo1Height){ 
   _o.mObj.scrollTop = 0 ; 
  }else if(top>_o.mo1Height) {
   _o.mObj.scrollTop = top-_o.mo1Height;
  }else{
   _o.mObj.scrollTop++;
  }
 };
 
 _o.init();
}