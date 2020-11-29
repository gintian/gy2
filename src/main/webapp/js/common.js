/*显示*/
function toggles(targetId){
	if (document.getElementById(targetId)){
		target = document.getElementById(targetId);
		target.style.display = "block";
	}
}
/*隐藏*/
function hides(targetId){
	if (document.getElementById(targetId)){
		target = document.getElementById(targetId);
		target.style.display = "none";
	}
}
/*显示父页面中的对象*/
function togglesParent(targetId){
	if (parent.document.getElementById(targetId)){
		target = parent.document.getElementById(targetId);
		target.style.display = "block";
	}
} 
/*隐藏父页面中的对象*/
function hidesParent(targetId){
	if (parent.document.getElementById(targetId)){
		target = parent.document.getElementById(targetId);
		target.style.display = "none";
	}
}
/*向文本框赋值*/
function symbol(editor,strexpr){
	document.getElementById(editor).focus();
	var element = document.selection;
	if (element!=null) {
		var rge = element.createRange();
		if (rge!=null)	
		rge.text=strexpr;
	}	
}
/*点击当前行变颜色*/
function tr_bgcolor(itemid){
	var tablevos=document.getElementsByTagName("select");
	for(var i=0;i<tablevos.length;i++){
	    var cvalue = tablevos[i];
	    var td = cvalue.parentNode.parentNode;
	    td.style.backgroundColor = '';
   	}
	var c = document.getElementById(itemid);
	var tr = c.parentNode.parentNode;
	if(tr.style.backgroundColor!=''){
		tr.style.backgroundColor = '' ;
	}else{
		tr.style.backgroundColor = '#FFF8D2' ;
	}
}
/*检查是否为数字*/
function isNum(i_value){
    re=new RegExp("[^0-9]");
    var s;
    if(s=i_value.match(re)){
        return false;
     }
    return true;
}
/*检查是否为合法时间*/
function checkTime(times){
 	var result=times.match(/^(\d{1,4})(-|\/)(\d{1,2})\2(\d{1,2})$/);
 	if(result==null) return false;
 	var d= new Date(result[1], result[3]-1, result[4]);
 	return (d.getFullYear()==result[1]&&(d.getMonth()+1)==result[3]&&d.getDate());
}
/*选择所有选项*/
function checkAll(){
	var tablevos=document.getElementsByTagName("input");
	for(var i=0;i<tablevos.length;i++){
		if(tablevos[i].type=="checkbox"){
			tablevos[i].checked=true;
      	 }
   	}
}
/*清除所有选项*/
function clearAll(){
	var tablevos=document.getElementsByTagName("input");
	for(var i=0;i<tablevos.length;i++){
		if(tablevos[i].type=="checkbox"){
			tablevos[i].checked=false;
      	 }
   	}
}
/*操作所有选项*/
function checkboxAll(obj){
	if(obj.checked)
		checkAll();
	else
		clearAll();
}
/*
* 删除数组中的某一项
* arr 数组
* n 数组中第几项
*/
function arrRemove(arr,n){
	if(arr.length<1||n<0)
		return arr;
	else
		return arr.slice(0,n).concat(arr.slice(n+1,arr.length));
}
/*
* 替换字符串中所有要替换的字符串
* text  指定的文本
* replacement 指定的旧字符
* target 指定的新字符
*/
function replaceAll(text,replacement,target){
    if(text==null||text==""){
    	return text;
    }
    if(replacement==null||replacement==""){ 
    	return text;
    }
    if(target==null) target="";
    var returnString="";
    var index=text.indexOf(replacement);
    while(index!=-1){
        if(index!=0) returnString+=text.substring(0,index)+target;
        text=text.substring(index+replacement.length);
        index=text.indexOf(replacement);
    }
    if(text!=""){
		returnString+=text;
	}
    return returnString;
}
/*
*只能输入数字
*/
function IsDigit() { 
    return ((event.keyCode >= 47) && (event.keyCode <= 57)); 
} 
/**
*去掉空格
*/
function trim(s){ 
	s = s.replace(/(^\s*)/g, "");
	s = s.replace(/(\s*$)/g, "");
	return s; 
}


//swfupload组件属性设置
var swfuploadObject = {
	// 显示文件名的输入框的样式默认为class='textColorRead'定义的样式
  	inputStyle : "width:300px;",
  	// “选择文件”按钮的样式，默认样式为class='mybutton'定义的样式
  	buttonStyle : "",
  	// 单个文件的最大值，默认为10,
  	fileMaxSize : "10",
  	//单个上传文件的单位，默认为MB,还可以使用GB、KB
  	fileSizeUnit : "MB",
  	// 文件的扩展名，限定上传文件的类型,默认是任意类型（*.*）,多个文件类型用分号隔开，例如*.jpg;*.jpeg;*.gif
  	fileExt : "*.*",
  	// 需要传入的其他参数
  	post_params : {
		"user_id" : "stephen830",
		"pass_id" : "123456"
	},
	// 文件类型的描述，默认为“所有文件类型”
	file_types_desc : "所有文件类型",
	// 最多上传的文件个数，默认为100
	file_upload_limit : 100,
	type : ""
}; 
/**
*大文件上传
*divId div的id
*paramName 参数
*/
function swfupload(divId,paramName,uploadUrl,type,obj) {
	var divObject = document.getElementById(divId);
	if (obj) {
		if (obj.inputStyle) {
			swfuploadObject.inputStyle = obj.inputStyle;
		}
		if (obj.buttonStyle) {
			swfuploadObject.buttonStyle = obj.buttonStyle;
		}
		if (obj.fileMaxSize) {
			swfuploadObject.fileMaxSize = obj.fileMaxSize;
		}
		if (obj.fileSizeUnit) {
			swfuploadObject.fileSizeUnit = obj.fileSizeUnit;
		}
		if (obj.fileExt && "" != obj.fileExt) {
			swfuploadObject.fileExt = obj.fileExt;
		}
		if (obj.post_params) {
			swfuploadObject.post_params = obj.post_params;
		}
		if (obj.file_types_desc) {
			swfuploadObject.file_types_desc = obj.file_types_desc;
		}
		if (obj.file_upload_limit) {
			swfuploadObject.file_upload_limit = obj.file_upload_limit;
		}
		
	} 
	swfuploadObject.uploadUrl = uploadUrl;
	swfuploadObject.type = type;
	if (divObject) {
		if (paramName) {
			var htmlStr = "";
			htmlStr = "<input type='text' readonly='true' class='textColorRead' name='"+paramName+"_name' id='"+paramName+"_name' value='' style='"+swfuploadObject.inputStyle+"'/>";
			htmlStr = htmlStr + "<input type='hidden' name='"+paramName+"' id='"+paramName+"' value=''/>";
			htmlStr = htmlStr + "&nbsp;<input type='button' class='mybutton' name='"+paramName+"_button' value='"+SWFUPLOAD_SELECT_FILES+"' style='"+swfuploadObject.buttonStyle+"' onclick=\"selectFile('"+paramName+"')\"/>";
			divObject.innerHTML+=htmlStr;
		} else {
			alert(SWFUPLOAD_NO_PARAM);
			return;
		}
	} else {
		alert(SWFUPLOAD_NO_DIV);
		return;
	}
}

function selectFile(paramName,type) {

	if (swfuploadObject.type == "61") {
		var fileName = document.getElementById("r5103name");
		if (fileName) {
			swfuploadObject.post_params.fileName = getEncodeStr(fileName.value);
			if (swfuploadObject.post_params.fileName == "") {
				alert("请先填写课件名称！");
				return ;
			}
		}
		
		var ke = document.getElementById("selectr5105");
		if (ke) {
			swfuploadObject.post_params.fileType = ke.value;
			if ("1" == ke.value) {
				swfuploadObject.fileExt = "*.doc;*.docx;*.xls;*.xlsx;*.pdf;*.ppt;*.pptx;*.zip";
			}
		}
	}
	var url = "/general/swfupload/swfuploadFile.jsp";
	var thurl = "/gz/gz_analyse/gz_analyse_iframe.jsp?src=" + url;
	var return_vo= window.showModalDialog(thurl,swfuploadObject, 
        "dialogWidth:500px; dialogHeight:400px;resizable:no;center:yes;scroll:yes;status:no;scrollbars:yes");
    if (return_vo) {
    	document.getElementById(paramName + "_name").value=return_vo.name;
    	document.getElementById(paramName).value=return_vo.value;
    }
}

/*
*流媒体播放器
*
*/
var player = null;
var ttt = null;
function showStreamPlayer(divid,netConnectionUrl,filePath,media_server,width,height,postion) {
	if (!netConnectionUrl) {
		alert(STREAMPLAYER_NO_NETURL);
		return ;
	}
	
	if (!postion) {
		postion = 0;
	}
	
	if (!filePath) {
		alert(STREAMPLAYER_NO_FILE);
		return ;
	}
	
	if (!media_server) {
		if ("red5" != media_server.toLowerCase() && "microsoft" != media_server.toLowerCase()) {
			alert(STREAMPLAYER_PROTOCOL_ERROR);
			return ;
		}
	}
	
	if (!divid) {
		alert(STREAMPLAYER_NO_DIV);
		return ;
	}
	
	window.onload=function() { 
	
	if ("red5" == media_server.toLowerCase()) {
		var div = document.getElementById(divid);
		var htmlStr ="";
		htmlStr += "<a href='http://get.adobe.com/cn/flashplayer/download/' style='display:block;width:"+width+"px;height:"+height+"px'";  
		htmlStr += " id='"+ divid +"_player'></a>"; 
		div.innerHTML = htmlStr;
		 
		player = flowplayer(divid + "_player", "/general/flowplayer/flowplayer-3.2.7.swf",{    
			clip: {    
           	provider: 'rtmp', 
            live: false,   
            autoBuffering: true,     //是否自动缓冲视频，默认true   
            autoPlay: true, 
            //accelerated:true,
            start:0.1,
            url:filePath
            
        },    
       	plugins: { 
        	rtmp: {    
                url: '/general/flowplayer/flowplayer.rtmp-3.2.3.swf',    
                netConnectionUrl: netConnectionUrl    
            },   
            controls: {    
                url: '/general/flowplayer/flowplayer.controls-3.2.5.swf',   
                autoHide:'always',   
                play: true,    
                scrubber: true,    
                playlist: false,   
                tooltips: {    
                    buttons: true,    
                    play:'播放',   
                    fullscreen: '全屏' ,   
                    fullscreenExit:'退出全屏',   
                    pause:'暂停',   
                    mute:'静音',   
                    unmute:'取消静音'  
                }    
            }   
        }   
        
    	});
    	 player.onStart (
 			function(){
 			player.seek(postion);
 		});
 		player.onLastSecond(
 			function() {
 				//alert(123);
 			}
 		); 
	}
	
	if ("microsoft" == media_server.toLowerCase()) {
		var div = document.getElementById(divid);
		/**div.innerHTML +="<OBJECT CLASSID='clsid:6BF52A52-394A-11D3-B153-00C04F79FAA6' ID='WMP'>";
		div.innerHTML +="<PARAM NAME='Name' VALUE='WMP1'>";
		div.innerHTML +="<PARAM NAME='URL' VALUE='"+ netConnectionUrl + "/" +filePath +"'>";
		div.innerHTML +="</OBJECT>";**/
	}
	//player.seek(100);
	//setTimeout('player.seek(100)',1000);
	};
	
	 
		
} 

function swfuploadsingle(divId,paramName,uploadUrl,type,obj) {
	var divObject = document.getElementById(divId);
	if (obj) {
		if (!obj.inputStyle) {
			obj.inputStyle = swfuploadObject.inputStyle;
		}
		if (!obj.buttonStyle) {
			obj.buttonStyle = swfuploadObject.buttonStyle;
		}
		if (!obj.fileMaxSize) {
			obj.fileMaxSize = swfuploadObject.fileMaxSize;
		}
		if (!obj.fileSizeUnit) {
			obj.fileSizeUnit = swfuploadObject.fileSizeUnit;
		}
		if (!(obj.fileExt && "" != obj.fileExt)) {
			obj.fileExt = swfuploadObject.fileExt ;
		}
		if (!obj.post_params) {
			obj.post_params = swfuploadObject.post_params;
		}
		if (!obj.file_types_desc) {
			obj.file_types_desc = swfuploadObject.file_types_desc;
		}
		if (!obj.file_upload_limit) {
			obj.file_upload_limit = swfuploadObject.file_upload_limit;
		}
		
	} else {
		obj = swfuploadObject;
	}
	obj.uploadUrl = uploadUrl;
	obj.type = type;
	obj.divId=divId;
	obj.paramName = paramName;
	if (divObject) {
		if (paramName) {
			var htmlStr = "";
			htmlStr = "<table border='0' cellspacing='0'><tr><td height='15' width='70%' align='left' valign='middle'>";
			htmlStr = htmlStr +	"<div id='"+divId+"_progress_div' style='overflow:hidden;'></div></td>";
			htmlStr = htmlStr +	"<td width='5%'><div id='"+divId+"_delete_div' style='display:none;'></div></td>";
			htmlStr = htmlStr +	"<td width='25%'>&nbsp;</td></tr>";
			htmlStr = htmlStr +	"<tr><td><input type='text' readonly='true' class='textColorRead common_border_color' name='"+paramName+"_name' id='"+paramName+"_name' value='"+document.getElementById(paramName+"_old").value+"' onpropertychange='ly();' style='height:22px;margin-bottom:7px;"+obj.inputStyle+"'/>";
			htmlStr = htmlStr + "<input type='hidden' name='"+paramName+"' id='"+paramName+"' value=''/>";
			htmlStr = htmlStr + "</td><td colspan='3' valign='middle' height='30'>&nbsp;<span id='spanButtonPlaceHolder'></span>&nbsp;<input id='btnCancel' type='button' value='取消' onclick='cancelQ();' disabled='disabled' style='display:none;' class='mybutton'/></td></tr></table>";
			divObject.innerHTML=htmlStr;
		} else {
			alert("参数名称不能为空！");
			return;
		}
	} else {
		alert("未定义控件位置！");
		return;
	}

	windload(obj);
}

var swfu;
function windload(objx) {
		var lstmp1=true;//按钮是否禁用
		var buttontext="<span class='mybutton'><font color='#CCCCCC'>浏览...</font></span>";//按钮显示文字
		if (document.getElementById("r5103name")) {
			if(document.getElementById("r5103name").value!=null&&document.getElementById("r5103name").value.length>0){
				lstmp1=false;
				buttontext="<span class='mybutton'><font color='#36507E'>浏览...</font></span>";
			}
		} else {
			lstmp1=false;
			buttontext="<span class='mybutton'><font color='#36507E'>浏览...</font></span>";
		}

		//window.onload = function() {
			var settings = {
				flash_url : "/general/swfupload/swfupload_single.swf",
				flash9_url : "/general/swfupload/swfupload_fp9.swf",
				upload_url: objx.uploadUrl,
				post_params: objx.post_params,
				file_size_limit : objx.fileMaxSize + "" + objx.fileSizeUnit,
				file_types : objx.fileExt,
				file_types_description : objx.file_types_desc,
				file_upload_limit : objx.file_upload_limit,
				file_queue_limit : objx.file_queue_limit,
				custom_settings : {
					progressTarget : objx.divId + "_progress_div",
					cancelButtonId : "btnCancel",
					obj:objx
				},
				debug: false,
				
				// Button settings
				button_image_url: "",
				button_width: "55",
				button_height: "22",
				button_placeholder_id: "spanButtonPlaceHolder",
				button_text: buttontext,
				button_text_style: ".rex{font-size:12px;color:#36507E;background-color: transparent;}",
				button_text_left_padding: 10,
				button_text_top_padding: 0,
				button_disabled: lstmp1,
				
				// The event handler functions are defined in handlers.js
				swfupload_preload_handler : preLoad,
				swfupload_load_failed_handler : loadFailed,
				file_queued_handler : fileQueued,
				file_queue_error_handler : fileQueueError,
				//file_dialog_complete_handler : fileDialogComplete,
				upload_start_handler : uploadStart,
				upload_progress_handler : uploadProgress,
				upload_error_handler : uploadError,
				upload_success_handler : uploadSuccess,
				upload_complete_handler : uploadComplete,
				queue_complete_handler : queueComplete	// Queue plugin event
			};
			
			if(objx.autoUpload!="false")
				settings.file_dialog_complete_handler = fileDialogComplete;

			swfu = new SWFUpload(settings);
	     //};
}

function cancelQ(){
	swfu.cancelQueue();
	if (document.getElementById(swfu.customSettings.obj.paramName)) {
		document.getElementById(swfu.customSettings.obj.paramName).value="";
	}
	
	if (document.getElementById(swfu.customSettings.obj.paramName + "_name")) {
		document.getElementById(swfu.customSettings.obj.paramName + "_name").value="";
	}
	
	
}
function buttonDisabled(){
	if(!checkLoad())
		return false;
	
if(swfu){
	var newPath=document.getElementById("newPathId").value;
	swfu.addPostParam("newPath",newPath);
	var lstmp=document.getElementById("r5103name").value;
	swfu.addPostParam("fileName",getEncodeStr(lstmp));
	if(lstmp!=null&&lstmp.length>0){
		swfu.setButtonText("<span class='mybutton'><font color='#36507E'>浏览...</font></span>");
		swfu.setButtonDisabled(false);
	}else{
		swfu.setButtonText("<span class='mybutton'><font color='#CCCCCC'>浏览...</font></span>");
		swfu.setButtonDisabled(true);
	}
}
}
function PlayFlv(divid,filePath){
//function PlayFlv(divId,filename) {
	/**var result = "";
	result = "<object type=\"application/x-shockwave-flash\" width=\"400\" height=\"300\""; 
	result += "data=\"/general/flowplayer/flvplayer.swf?file="+filename+"\">"; 
	result += "<param name=\"movie\" value=\"/general/flowplayer/flvplayer.swf?file="+filename+"&showfsbutton=true&autostart=true\"/>"; 
	result += "<param name=\"wmode\" value=\"transparent\" />"; 
	result += "<param name=\"quality\" value=\"high\" />"; 
	result += "<param name=\"allowfullscreen\" value=\"true\" /> "; 
	result += "</object>";
	document.getElementById(divId).innerHTML=result;**/
	
	if (!filePath) {
		alert(STREAMPLAYER_NO_FILE);
		return ;
	}
	
	if (!divid) {
		alert(STREAMPLAYER_NO_DIV);
		return ;
	}

	var fileNa = filePath.substr(filePath.lastIndexOf("."));

	window.onload=function() { 
	
	if (fileNa.toLowerCase().indexOf('.mp3') != -1 || fileNa.toLowerCase().indexOf('.mp4') != -1 || fileNa.toLowerCase().indexOf('.flv') != -1 || fileNa.toLowerCase().indexOf('.f4v') != -1) {
		var div = document.getElementById(divid);
		var htmlStr ="";
		htmlStr += "<a href='http://get.adobe.com/cn/flashplayer/download/' style='display:block;width:"+520+"px;height:"+330+"px'";  
		htmlStr += " id='"+ divid +"_player'></a>"; 
		div.innerHTML = htmlStr;
		 
		if (fileNa.toLowerCase().indexOf('.mp3') != -1 )  {
			player = flowplayer(divid + "_player", "/general/flowplayer/flowplayer-3.2.7.swf",{    
				clip: {    
           		provider: 'audio', 
            	live: false,   
            	autoBuffering: true,     //是否自动缓冲视频，默认true   
            	autoPlay: true, 
            	url:filePath
        	},    
       	plugins: { 
        	audio: {
				url: '/general/flowplayer/flowplayer.audio-3.2.2.swf'
			},

            controls: {    
                url: '/general/flowplayer/flowplayer.controls-3.2.5.swf',   
                autoHide:'always',   
                play: true,    
                scrubber: true,    
                playlist: false,   
                tooltips: {    
                    buttons: true,    
                    play:'播放',   
                    fullscreen: '全屏' ,   
                    fullscreenExit:'退出全屏',   
                    pause:'暂停',   
                    mute:'静音',   
                    unmute:'取消静音'  
                }    
            }   
        }   
        
    	}); 
		} else {
		player = flowplayer(divid + "_player", "/general/flowplayer/flowplayer-3.2.7.swf",{    
			clip: {    
    //       	provider: 'audio', 
            live: false,   
            autoBuffering: true,     //是否自动缓冲视频，默认true   
            autoPlay: true, 
            url:filePath
        },    
       	plugins: { 
        	/**rtmp: {    
                url: '/general/flowplayer/flowplayer.rtmp-3.2.3.swf',    
                netConnectionUrl: netConnectionUrl    
            },   **/
            controls: {    
                url: '/general/flowplayer/flowplayer.controls-3.2.5.swf',   
                autoHide:'always',   
                play: true,    
                scrubber: true,    
                playlist: false,   
                tooltips: {    
                    buttons: true,    
                    play:'播放',   
                    fullscreen: '全屏' ,   
                    fullscreenExit:'退出全屏',   
                    pause:'暂停',   
                    mute:'静音',   
                    unmute:'取消静音'  
                }    
            }   
        }   
        
    	}); 
    	}
    	
	}else {

		var div = document.getElementById(divid);
		div.innerHTML +="<OBJECT CLASSID='clsid:6BF52A52-394A-11D3-B153-00C04F79FAA6' ID='WMP'>";
		div.innerHTML +="<PARAM NAME='Name' VALUE='WMP1'>";
		div.innerHTML +="<PARAM NAME='URL' VALUE='"+ netConnectionUrl + "/" +filePath +"'>";
		div.innerHTML +="</OBJECT>";
	}
	
	};

} 