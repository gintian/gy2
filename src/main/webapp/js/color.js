var ColorHex=new Array('00','33','66','99','CC','FF');
var SpColorHex=new Array('FF0000','00FF00','0000FF','FFFF00','00FFFF','FF00FF');
var current=null;
/*
棰滆壊鎺т欢鍏煎闈濱E娴忚鍣�  bug 35031  wangb 20180228 
*/
var userAgent = navigator.userAgent; //鍙栧緱娴忚鍣ㄧ殑userAgent瀛楃涓�  
var isFF = userAgent.indexOf("Firefox") > -1; //鍒ゆ柇鏄惁Firefox娴忚鍣�  
function intocolor()
{
var colorTable=''
for (i=0;i<2;i++)
 {
  for (j=0;j<6;j++)
   {
    colorTable=colorTable+'<tr height=12>'
    colorTable=colorTable+'<td width=11 style="background-color:#000000">'
    
    if (i==0){
    colorTable=colorTable+'<td width=11 style="background-color:#'+ColorHex[j]+ColorHex[j]+ColorHex[j]+'">'} 
    else{
    colorTable=colorTable+'<td width=11 style="background-color:#'+SpColorHex[j]+'">'} 

    
    colorTable=colorTable+'<td width=11 style="background-color:#000000">'
    for (k=0;k<3;k++)
     {
       for (l=0;l<6;l++)
       {
        colorTable=colorTable+'<td width=11 style="background-color:#'+ColorHex[k+i*3]+ColorHex[l]+ColorHex[j]+'">'
       }
     }
  }
}
colorTable='<table width=253 border="0" cellspacing="0" cellpadding="0" style="border:1px #000000 solid;border-bottom:none;border-collapse: collapse" bordercolor="000000">'
           +'<tr height=30><td colspan=21 bgcolor=#cccccc>'
           +'<table cellpadding="0" cellspacing="1" border="0" style="border-collapse: collapse">'
           +'<tr><td width="3"><td><input id="DisColorid" type="text" name="DisColor" size="6" disabled style="border:solid 1px #000000;background-color:#ffff00"></td>'
           +'<td width="3"><td><input id="HexColorid" type="text" name="HexColor" size="7" style="border:inset 1px;font-family:Arial;" value="#000000"></td></tr></table></td></table>'
           +'<table border="1" cellspacing="0" cellpadding="0" style="border-collapse: collapse" bordercolor="000000" onmouseover="doOver(event)" onmouseout="doOut(event)" onclick="doclick(event)" style="cursor:hand;">'
           +colorTable+'</table>';          
//colorpanel.innerHTML='<iframe src=\"javascript:false\"  style=\"position:absolute; visibility:inherit; top:0px; left:0px;width:253px;height:177px;z-index:3;\">'+colorTable+'</iframe>';
var colorpanel = document.getElementById('colorpanel');
colorpanel.innerHTML=colorTable;
}
//灏嗛鑹插�煎瓧姣嶅ぇ鍐�
function doOver(e) {
	var e = e || window.event;
    if(isFF){
    	if ((e.target.tagName=="TD") && (current!=e.target)) {
        	if (current!=null){current.style.backgroundColor = current._background;}     
        	e.target._background = e.target.style.backgroundColor;
        	var colorValue = e.target.style.backgroundColor;
        	if(colorValue.indexOf("rgb")!=-1 || colorValue.indexOf("RGB")!=-1){
            	colorValue = colorHexMain(colorValue);
        	}
        	document.getElementById("DisColorid").style.backgroundColor = colorValue;
        	document.getElementById("HexColorid").value = colorValue.toUpperCase();
        	e.target.style.backgroundColor = "white";
        	current = e.target;
      	}
    }else{
    	if ((e.srcElement.tagName=="TD") && (current!=e.srcElement)) {
        if (current!=null){current.style.backgroundColor = current._background;}     
        e.srcElement._background = e.srcElement.style.backgroundColor;
        var colorValue = e.srcElement.style.backgroundColor;
        if(colorValue.indexOf("rgb")!=-1 || colorValue.indexOf("RGB")!=-1){
            colorValue = colorHexMain(colorValue);
        }
        document.getElementById("DisColorid").style.backgroundColor = colorValue;
        document.getElementById("HexColorid").value = colorValue.toUpperCase();
        e.srcElement.style.backgroundColor = "white";
        current = e.srcElement;
      }
    }
}


//灏嗛鑹插�煎瓧姣嶅ぇ鍐�
function doOut(e) {
    if (current!=null) current.style.backgroundColor = current._background.toUpperCase();
}
function doclick(e)
{
	var e = e || window.event;
	if(isFF){
		if (e.target.tagName == "TD")
    	{
        	var clr = e.target._background;
        	if(clr.indexOf("rgb")!=-1 || clr.indexOf("RGB")!=-1){
            	clr = colorHexMain(clr);
        	}
        	clr = clr.toUpperCase(); //灏嗛鑹插�煎ぇ鍐�
        	if (targetElement)
        	{
            	//缁欑洰鏍囨棤浠惰缃鑹插��
            	targetElement.value = clr;
            	targetElement.style.backgroundColor=clr;
        	}
        	DisplayClrDlg(false,e);
       		return clr;
    	}
	}else{
		if (e.srcElement.tagName == "TD")
    	{
        	var clr = e.srcElement._background;
        	if(clr.indexOf("rgb")!=-1 || clr.indexOf("RGB")!=-1){
            	clr = colorHexMain(clr);
        	}
        	clr = clr.toUpperCase(); //灏嗛鑹插�煎ぇ鍐�
        	if (targetElement)
        	{
            	//缁欑洰鏍囨棤浠惰缃鑹插��
            	targetElement.value = clr;
            	targetElement.style.backgroundColor=clr;
        	}
        	DisplayClrDlg(false,e);
        	return clr;
    	}
	}
   
}
//搴旂敤棰滆壊瀵硅瘽妗嗗繀椤绘敞鎰忎袱鐐癸細
//棰滆壊瀵硅瘽妗� id : colorpanel 涓嶈兘鍙�
//瑙﹀彂棰滆壊瀵硅瘽妗嗘樉绀虹殑鏂囨湰妗嗭紙鎴栧叾瀹冿級蹇呴』鏈� alt 灞炴�э紝涓斿�间负 clrDlg锛堜笉鑳藉拷鐣ュぇ灏忓啓锛�

var targetElement = null; //鎺ユ敹棰滆壊瀵硅瘽妗嗚繑鍥炲�肩殑鍏冪礌

//褰撶偣涓嬮紶鏍囨椂锛岀‘瀹氭樉绀鸿繕鏄殣钘忛鑹插璇濇
//鐐瑰嚮棰滆壊瀵硅瘽妗嗕互澶栧叾瀹冨尯鍩熸椂锛岃瀵硅瘽妗嗛殣钘�
//鐐瑰嚮棰滆壊瀵硅瘽妗嗚壊鍖烘椂锛岀敱 doclick 鍑芥暟鏉ラ殣钘忓璇濇
function OnDocumentClick()
{
	var e;
	if(isFF)
		e = arguments[0];
	else
		e = window.event;
    var srcElement;
    if(isFF)
    	srcElement = e.target;
    else
   		srcElement = e.srcElement;
    if (srcElement.alt == "clrDlg")
    {
        //鏄剧ず棰滆壊瀵硅瘽妗�
       //targetElement = event.srcElement;
        targetElement = srcElement;
        DisplayClrDlg(true,e);
    }
    else
    {
        //鏄惁鏄湪棰滆壊瀵硅瘽妗嗕笂鐐瑰嚮鐨�
        while (srcElement && srcElement.id!="colorpanel")
        {
            srcElement = srcElement.parentElement;
        }
        if (!srcElement)
        {
            //涓嶆槸鍦ㄩ鑹插璇濇涓婄偣鍑荤殑
            DisplayClrDlg(false,e);
        }
    }
    
}

//鏄剧ず棰滆壊瀵硅瘽妗�
//display 鍐冲畾鏄剧ず杩樻槸闅愯棌
//鑷姩纭畾鏄剧ず浣嶇疆
function DisplayClrDlg(display,e)
{
	var e = e || window.event;
    var clrPanel = document.getElementById("colorpanel");
    if (display)
    {
        var left = document.body.scrollLeft + e.clientX;
        var top = document.body.scrollTop + e.clientY;
        if (e.clientX+clrPanel.style.pixelWidth > document.body.clientWidth)
        {
            //瀵硅瘽妗嗘樉绀哄湪榧犳爣鍙虫柟鏃讹紝浼氬嚭鐜伴伄鎸★紝灏嗗叾鏄剧ず鍦ㄩ紶鏍囧乏鏂�
            left -= clrPanel.style.pixelWidth;
        }
        if (e.clientY+clrPanel.style.pixelHeight > document.body.clientHeight)
        {
            //瀵硅瘽妗嗘樉绀哄湪榧犳爣涓嬫柟鏃讹紝浼氬嚭鐜伴伄鎸★紝灏嗗叾鏄剧ず鍦ㄩ紶鏍囦笂鏂�
            top -= clrPanel.style.pixelHeight;
        }
        //clrPanel.style.pixelLeft = left;
        //clrPanel.style.pixelTop = top;
        clrPanel.style.left = left+'px';
        clrPanel.style.top = top+'px';
        clrPanel.style.display = "block";
    }
    else
    {
        clrPanel.style.display = "none";
    }
}

/**
 * RGB棰滆壊杞崲涓�16杩涘埗
 * RGB(52,83,139) --> #34538b
 */
function colorHexMain(value){
 	var reg = /^#([0-9a-fA-f]{3}|[0-9a-fA-f]{6})$/;
    var that = value;
    if(/^(rgb|RGB)/.test(that)){
        var aColor = that.replace(/(?:\(|\)|rgb|RGB)*/g,"").split(",");
        var strHex = "#";
        for(var i=0; i<aColor.length; i++){
            var hex = Number(aColor[i]).toString(16);
            if(hex === "0"){
                hex += hex; 
            }
            strHex += hex;
        }
        if(strHex.length !== 7){
            strHex = that;  
        }
        return strHex;
    }else if(reg.test(that)){
        var aNum = that.replace(/#/,"").split("");
        if(aNum.length === 6){
            return that;    
        }else if(aNum.length === 3){
            var numHex = "#";
            for(var i=0; i<aNum.length; i+=1){
                numHex += (aNum[i]+aNum[i]);
            }
            return numHex;
        }
    }else{
        return that;    
    }
}

document.body.onclick = OnDocumentClick;
document.body.onload = intocolor;
