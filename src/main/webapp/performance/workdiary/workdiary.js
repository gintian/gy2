// 主背景色（大区域）
// 通常使用明快的颜色（浅黄色等...）
	if (typeof fcolor == 'undefined') { var fcolor = "ffffff";}
	
// Border的颜色和标题栏的颜色；
// 通常的颜色深（褐色，黑色等。）
	if (typeof backcolor == 'undefined') { var backcolor = "#CDCDCD";}
	
	
// 文字的颜色
// 通常是比较深的颜色；
	if (typeof textcolor == 'undefined') { var textcolor = "#999900";}
	
// 标题的颜色
// 通常是明快的颜色；
	if (typeof capcolor == 'undefined') { var capcolor = "#FFFFFF";}
	
// "Close"的颜色
// 通常是明快的颜色；
	if (typeof closecolor == 'undefined') { var closecolor = "#9999FF";}
	
	
// 弹出的窗口的宽度；
// 100-300 pixels 合适
	if (typeof width == 'undefined') { var width = "100";}
	
// 边缘的宽度，象素。
// 1-3 pixels 合适
	if (typeof border == 'undefined') { var border = "1";}
	
	
// 弹出窗口位于鼠标左侧或者右侧的距离，象素。
// 3-12合适
	if (typeof offsetx == 'undefined') { var offsetx = 5;}
	
// 弹出窗口位于鼠标下方的距离；
// 3-12 合适
	if (typeof offsety == 'undefined') { var offsety = 5;}
	
////////////////////////////////////////////////////////////////////////////////////
// 设置结束
////////////////////////////////////////////////////////////////////////////////////

var ns4 = (document.layers)? true:false
var ie4 = (document.all)? true:false

// Microsoft Stupidity Check.
if (ie4) {
	if (navigator.userAgent.indexOf('MSIE 5')>0) {
		ie5 = true;
	} else {
		ie5 = false; }
} else {
	ie5 = false;
}

var x = 0;
var y = 0;
var snow = 0;
var sw = 0;
var cnt = 0;
var dir = 2;
var tr=1;
//  linbz  18649  'overDiv'未定义 无效参数
//if ( (ns4) || (ie4) ) {
//	if (ns4) over = document.overDiv
//	if (ie4) over = overDiv.style
//	//document.onmousemove = mouseMove
//	//document.onclick = mouseMove
//	if (ns4) document.captureEvents(Event.MOUSEMOVE)
//}

// 以下是页面中使用的公共函数；

// Simple popup right
function drs(text,titles) {
	dts(1,text,titles);
}


// Clears popups if appropriate
function nd() {
	if ( cnt >= 1 ) { sw = 0 };
	if ( (ns4) || (ie4) ) {
		if ( sw == 0 ) {
			snow = 0;
			hideObject(over);
		} else {
			cnt++;
		}
	}

}

// 非公共函数，被其它的函数调用；

// Simple popup
function dts(d,text,titles) {
    layerWrite(text,titles);
	dir = d;
	disp();
}



// Common calls
function disp() {
	if ( (ns4) || (ie4) ) {
		if (snow == 0) 	{
			if (dir == 2) { // Center
				moveTo(over,x+offsetx-(width/2),y+offsety);
			}
			if (dir == 1) { // Right
				moveTo(over,x+offsetx,y+offsety);
			}
			if (dir == 0) { // Left
				moveTo(over,x-offsetx-width,y+offsety);
			}
			showObject(over);
			snow = 1;
		}
	}
// Here you can make the text goto the statusbar.
}

// Moves the layer
function mouseMove(e) {
	if (ns4) {x=e.pageX; y=e.pageY;}
	if (ie4) {x=event.x; y=event.y;}
	if (ie5) {x=event.x+document.body.scrollLeft; y=event.y+document.body.scrollTop;}
	if (snow) {
		if (dir == 2) { // Center
			moveTo(over,x+offsetx-(width/2),y+offsety);
		}
		if (dir == 1) { // Right
			moveTo(over,x+offsetx,y+offsety);
		}
		if (dir == 0) { // Left
			moveTo(over,x-offsetx-width,y+offsety);
		}
	}
}

// The Close onMouseOver function for Sticky
function cClick() {
	hideObject(over);
	sw=0;
}

// Writes to a layer
function layerWrite(txt,titles) {
    var mouse_x=200;
    var mouse_y=60;
    if(!document.all){
        mouse_x=event.pageX;
        mouse_y=event.pageY;
  //firefox By Rlby 2007-12-28
    }else{
        mouse_x=document.documentElement.scrollLeft+event.clientX;
        mouse_y=document.documentElement.scrollTop+event.clientY;
    }
    mouse_x=mouse_x-30;
	var strTable = "<div id=\"movDiv\"";
	strTable+=" style='position:absolute;left:"+mouse_x+"px;top:"+mouse_y+"px;z-index:-1;' onmousedown='getFocus(this)'>";
	strTable+="<table width=\"400\" border=\"0\" cellspacing=\"0\" cellpadding=\"0\" class=\"ListTable\">";
    strTable+="<tr><td align=\"center\" class=\"TableRow\">";
    strTable+="<table width=\"100%\" border=\"0\"><tr><td width=\"90%\" style='cursor:move;' onmousedown=MDown(movDiv)>"+titles;
    strTable+="</td><td align=\"right\"><img src=\"/images/del.gif\" onclick=\"nd();\"></td></tr></table></td></tr>";
    strTable+="<tr><td class=\"RecordRow\"><div id=\"scroll_box\">";
    strTable+="<table width=\"100%\" border=\"0\"><tr><td style='cursor:move;word-break: break-all; word-wrap:break-word;' onmousedown=MDown(movDiv)>";
    strTable+=txt;
    strTable+="</td></tr></table></div></td></tr></table></div>";
    if (ns4) {
       var lyr = document.overDiv.document
       lyr.write(replaceAll(getDecodeStr(strTable),"\n","<br>"));
       lyr.close()
    }else if (ie4){
       document.all["overDiv"].innerHTML =replaceAll(getDecodeStr(strTable),"\n","<br>");
    }
	if (tr) {  }
}

// Make an object visible
function showObject(obj) {
        if (ns4) obj.visibility = "show"
        else if (ie4) obj.visibility = "visible"
}

// Hides an object
function hideObject(obj) {
        if (ns4) obj.visibility = "hide"
        else if (ie4) obj.visibility = "hidden"
}

// Move a layer
function moveTo(obj,xL,yL) {
        obj.left = xL
        obj.top = yL
}
function ssdel(){ 
	if (event) { 
		lObj = event.srcElement;

		while (lObj && lObj.tagName != "DIV") 
			lObj = lObj.parentElement; 
	} 
	var id=lObj.id ;
	document.getElementById(id).removeNode(true); 
} 
//-- 控制层删除End of script --> 
 
var Obj='' ;
var index=10000;//z-index; 
document.onmouseup=MUp;
document.onmousemove=MMove ;

function MDown(Object){ 
	Obj=Object.id ;
	document.all(Obj).setCapture() ;
	pX=event.x-document.all(Obj).style.pixelLeft; 
	pY=event.y-document.all(Obj).style.pixelTop; 
} 

function MMove(){ 
	if(Obj!=''){ 
		document.all(Obj).style.left=event.x-pX; 
		document.all(Obj).style.top=event.y-pY; 
		//document.all("lblX").innerText = event.y-pX;
		//document.all("lblY").innerText= event.y-pY;
	} 
} 

function MUp(){ 
	if(Obj!=''){ 
		document.all(Obj).releaseCapture(); 
		Obj=''; 
	} 
} 
//-- 控制层移动end of script --> 
//获得焦点; 
function getFocus(obj) { 
	if(obj.style.zIndex!=index) { 
		index = index + 2; 
		var idx = index; 
		obj.style.zIndex=idx;  
	} 
} 