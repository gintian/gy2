// 保存当前工作总结或工作计划的编号
var toObjectId = "";
// 删除的对象
var deleteObject = null;


/*
 * 点击，发送信息或附件
 * 
 * id 书写沟通信息文本域的id
 * to_name 回复对象名称
 * fileId 上传文件的编号名称
 * spanId 显示上传文件名<span>的id
 * type 消息来源
 * 
 */

function sendMessage(showId,id,to_name,fileId,spanId,type,msgflag){
	//文件未上传完成,发表按钮失效
	document.getElementById("gtBtn").disabled=true;
	//点击发表后,隐藏不能点击的文件/图片(wusy)
	 Ext.getDom("title"+msgflag).style.display = "none";
	 
	 var msg = Ext.get(id).getValue();
	 if(!Ext.isEmpty(msg)){
        msg = keyWord_filter(msg);
     }
	 var placeHolder=document.getElementById("msgContent").getAttribute("placeHolder");
	 if(trim(msg)==trim(placeHolder)){
		 msg="";
	 }
	 
	 var count=0;
	 for(var i=1;i<=5;i++){
		 var file = Ext.get(fileId+msgflag+i).getValue(); 
		 if(file == ""){
			 count++;
		 }
	 }
	 if(trim(msg) == "" && count==5){
		 document.getElementById("gtBtn").disabled=false;
		 var position = getMsgPosition();
		 //confirm弹出前，计算当前滚动条位置，放置alert弹出后滚动条位置改变
		 var scrollTop = parent.window.Ext.getBody().getScroll().top;
		 Ext.Msg.alert("提示信息","请输入信息或选择附件！").alignTo(Ext.getBody(),"tl",[position.x,position.y]);
		 //恢复confirm弹出前的滚动条位置 兼容IE、firefox、chrome、safari chent 20160319
		 if(Ext.isIE || Ext.firefoxVersion != 0) {//IE、firefox
			 window.parent.document.documentElement.scrollTop = scrollTop;
		 }else {//chrome、safari
			 parent.window.Ext.getBody().setScrollTop(scrollTop);
		 }
		return;
	 }
	 
	 // 向工作总结p01添加一条记录 
	 if(type == "3" && toObjectId == ""){
		 //没有记录，和未发布
		 toObjectId = Ext.get("p0100").getValue();
		 if(toObjectId == ""){
	 		 var position = getMsgPosition();
	 		 //confirm弹出前，计算当前滚动条位置，放置alert弹出后滚动条位置改变
			var scrollTop = parent.window.Ext.getBody().getScroll().top;
			 Ext.Msg.alert("提示信息","总结还未发布，不能沟通！").alignTo(Ext.getBody(),"tl",[position.x,position.y]);
			 //恢复confirm弹出前的滚动条位置 兼容IE、firefox、chrome、safari chent 20160319
			 if(Ext.isIE || Ext.firefoxVersion != 0) {//IE、firefox
				 window.parent.document.documentElement.scrollTop = scrollTop;
			 }else {//chrome、safari
				 parent.window.Ext.getBody().setScrollTop(scrollTop);
			 }
			 document.getElementById("gtBtn").disabled=false;
			 return;
		 }
	 }
	 
	 if(type == "1" && toObjectId == ""){
	 	 toObjectId = wpm.p0700;	//更新计划标识 haosl 20160923
	 	 if(toObjectId ==""){
			 document.getElementById("gtBtn").disabled=false;
		     var position = getMsgPosition();
		     //confirm弹出前，计算当前滚动条位置，放置alert弹出后滚动条位置改变
			 var scrollTop = parent.window.Ext.getBody().getScroll().top;
			 Ext.Msg.alert("提示信息","计划还未制订,不能发表沟通信息！").alignTo(Ext.getBody(),"tl",[position.x,position.y]);
			 //恢复confirm弹出前的滚动条位置 兼容IE、firefox、chrome、safari chent 20160319
			 if(Ext.isIE || Ext.firefoxVersion != 0) {//IE、firefox
				 window.parent.document.documentElement.scrollTop = scrollTop;
			 }else {//chrome、safari
				 parent.window.Ext.getBody().setScrollTop(scrollTop);
			 }
	         return;
     	}
	 }
     
	 
	 var p0800Node = document.getElementById("param.p0800");
	 var p0835Node = document.getElementById("progressBar");
	 var p0800 = p0800Node ? p0800Node.value : ""; // 任务id
	 var p0835 = (p0835Node ? p0835Node.getAttribute("fieldValue") : "") || ""; // 任务进度
	 //总结沟通发布消息用  haosl 2017-8-25
	 var month = "";
	 var year = "";
	 if(type == "3"){
		 month = document.getElementById("month").value;
		 year = document.getElementById("year").value;
	 }

     Ext.Ajax.request({ 
          url : '/servlet/workplan/UpLoadFileServlet?objectid='+toObjectId+"&to_name="+to_name+"&fileId="+fileId+"&type="+type+"&p0835="+p0835+"&p0800="+p0800+"&id="+id+"&month="+month+"&year="+year,
          isUpload : true, 
          form : form_id, 
          success : function(response) {
    	 //附件上传完成后,发表按钮需要显示
    	 document.getElementById("gtBtn").disabled=false;
          	var datevalue = response.responseText;
          	if(""==datevalue){
          		return;
          	}
          	var map = Ext.JSON.decode(datevalue);
          	
          	if(map.result == "fileSizeOver"){
			     var position = getMsgPosition();
			     //confirm弹出前，计算当前滚动条位置，放置alert弹出后滚动条位置改变
				 var scrollTop = parent.window.Ext.getBody().getScroll().top;
				 Ext.Msg.alert("提示信息","文件大小不超过20M！").alignTo(Ext.getBody(),"tl",[position.x,position.y]);
				 //恢复confirm弹出前的滚动条位置 兼容IE、firefox、chrome、safari chent 20160319
				 if(Ext.isIE || Ext.firefoxVersion != 0) {//IE、firefox
					 window.parent.document.documentElement.scrollTop = scrollTop;
				 }else {//chrome、safari
					 parent.window.Ext.getBody().setScrollTop(scrollTop);
				 }          		
          		return;
          	}else if(map.result == "saveMsgError"){
 			     var position = getMsgPosition();
 			     //confirm弹出前，计算当前滚动条位置，放置alert弹出后滚动条位置改变
				 var scrollTop = parent.window.Ext.getBody().getScroll().top;
				 Ext.Msg.alert("提示信息","沟通信息保存失败！").alignTo(Ext.getBody(),"tl",[position.x,position.y]);
				 //恢复confirm弹出前的滚动条位置 兼容IE、firefox、chrome、safari chent 20160319
				 if(Ext.isIE || Ext.firefoxVersion != 0) {//IE、firefox
					 window.parent.document.documentElement.scrollTop = scrollTop;
				 }else {//chrome、safari
					 parent.window.Ext.getBody().setScrollTop(scrollTop);
				 }            		
          	}else if(map.result == "success"){
          		var fileName = map.fName;
          		var filePath = map.path;
          		
              	var arry = new Array(); 
              	arry[0] = map.name;
              	arry[1] = msg;
              	arry[2] = map.date;
              	arry[3] = map.msgId;
              	arry[4] = to_name;
              	arry[5] = map.photoUrl; 
              	arry[6] = type;
              	// 判断消息是否是自己发送 ,1:自己，0：别人
              	arry[7] = "1";
              	// 添加刚发布的消息 
              	addDiv(arry,true);

              	// 添加文件图片
             	// 添加文件图片
              	var pathArray= new Array(); 
            	var nameArray= new Array(); 
            	pathArray=filePath.split(",");
            	nameArray=fileName.split(",");
            	for(i=0;i<pathArray.length-1;i++){
            		if(nameArray[i] != "error") 
                  		showUploadPhoto(map.msgId,'',nameArray[i],pathArray[i]);
            	}
            	
            	if(type == "2"){//任务详情页面
	            	//修改任务进度后刷新任务列表 lis 20160319
	            	var p0700 = document.getElementById("param.p0700").value;
					var p0800 = document.getElementById("param.p0800").value;
					var p0723 = document.getElementById("param.p0723").value;
					var recordId = document.getElementById("param.recordId").value;
					isNeedToRefresh(p0700,p0800,p0723,recordId);
					
					basic.biz.tmp.p0835 = p0835;//发表后，把任务完成进度全局变量同时更新 chent 20160321
            	}
              	
          	}else{
          	    return;
            }
        	// 清空附件(span和input都要清除)
            var inputIds="summaryFile"+msgflag+"1,summaryFile"+msgflag+"2,summaryFile"+msgflag+"3,summaryFile"+msgflag+"4,summaryFile"+msgflag+"5";//五个文件输入框。
        	var spanIds="fNameId"+msgflag+"1,fNameId"+msgflag+"2,fNameId"+msgflag+"3,fNameId"+msgflag+"4,fNameId"+msgflag+"5";//五个给 span 标签赋值的标签
        	var inputArray= new Array(); 
        	var spanArray= new Array(); 
        	inputArray=inputIds.split(",");
        	spanArray=spanIds.split(",");
            for(i=0;i<spanArray.length;i++){
            	if(Ext.getDom(spanArray[i]).innerHTML != "")
            	  clearFileInput(inputArray[i],spanArray[i]);
            }
            
            //初始化input框
            initInput(msgflag);
            
            // 重置文本域
            Ext.getDom(id).value = "";
            if(showId != '')//回复成功后隐藏回复框
            	dispalyWritebackDiv(showId);
//            autoTextareas(id);
//            adapt.adaptTextareaHeight();
          	//window.parent.window.isNeedToRefresh();//发表计划沟通时会报错,不知道为什么加该行代码,感觉不用刷新,先注释掉
          }  
      });  
}
/*
增加沟通记录

bhr:是否是hr查看,
*/

function addDiv(msg,bhr){
	var sender = msg[0]; // 发送信息者
	var content = msg[1]; // 发送信息内容
	var submintDate = msg[2]; // 发送信息时间
	var id = msg[3]; // 发送信息编号
	var to_name = msg[4]; // 回复信息者
	var photo = msg[5]; // 发送者图片
	var type = msg[6]; // 消息来源
	// 判断消息是否是自己发送 ,1:自己，0：别人
	var sign = msg[7];

	var div = document.createElement("div"); 
	div.className = "hj-wzm-six-bottom-er1";
	div.style.cssText = "margin:10px 10px 0 10px;width:99%;padding-bottom:10px;border-bottom:1px #D5D5D5 dashed; overflow:hidden;";
	

	var div_top = document.createElement("div");
	div_top.style.cssText = "float:left; width:32px;";
	
	var div_bottom = document.createElement("div");
	div_bottom.style .cssText = "text-align:left;margin-left:40px;";
	
	var div_bottom_top = document.createElement("div"); 
	div_bottom_top.style.cssText = "float:top;";
	
	//修改样式问题:如果要展示的文件不够在一行展示,会破坏下面div的样式,设置父级div高度随子内容自动增加(wusy)
	var divclear = document.createElement("div");
	divclear.style.cssText = "clear:both";
	div.appendChild(divclear);
	
	var showId = "writeBackId"+id;
//	var writeBackId = "write"+id;
	
	div_top.innerHTML = "<a ><img class='img-circle' height='32px' width='32px' src='" + photo + "'/></a>"; 
	div.appendChild(div_top);

	var p = document.createElement("p"); 
	p.innerHTML = "<span class='hj-wzm-six-dd2' >"+sender+"</span>&nbsp;&nbsp;"
	if((to_name!="") && (to_name !=null)){
		p.innerHTML += "回复 <span class='hj-wzm-six-dd2'>"+to_name+"</span>&nbsp;&nbsp;"
	}
	p.innerHTML += "<span class='hj-wzm-six-dd1' style='color:#c3c8c4;'>"+submintDate+"</span>";
	div_bottom_top.appendChild(p);
	
	div_bottom_top.innerHTML +=  "<div id='showUpLoadPhotoId"+id+"'></div>"
	if ((content!='') && (content!=null))
	   div_bottom_top.innerHTML +=  "<textarea name='div_bottom_content' readonly='readonly' style='resize:none;font-family:\"微软雅黑\";font-size:12px;width:95%;height:100%;overflow-x:hidden;overflow-y:hidden;border:0;background:#FAFAFA;'>"
	   +content+"</textarea>";
	
	if(sign == "1"){
		div_bottom_top.innerHTML +=  "<span  onclick='deleteMySelfMsg(this,\""+id+"\")' style='float:right;cursor:pointer;color:#549FE3;'>删除</span>"; // IE下a:hover文本域会错位 lium
		if (content=='')
			div_bottom_top.innerHTML +="</br></br>";
	}// showWriteBackDiv
	else{
	   if (bhr!="true")
		div_bottom_top.innerHTML +=  "<a href=\"javascript:addReplyDiv('"+showId+"','"+sender+"','"+type+"')\" style='float:right;'>回复</a>";  // showWriteBackDiv
	    if (content=='')
			div_bottom_top.innerHTML +="</br></br>";
	}

	div_bottom_top.innerHTML +=  " <div id='writeBackId"+id+"' name='writeBackName' style='overflow:auto;height:auto!important;height:100px;'>"
				  			 + 	 " </div>";
	
	div_bottom.appendChild(div_bottom_top);
	div.appendChild(div_bottom);
	
	// 将所有沟通信息添加到页面 id = addDivMsg 的div中			
	document.getElementById("addDivMsg").appendChild(div);
	
	
	
	
	var reforeNode = Ext.query('#addDivMsg div')[0]; 
	document.getElementById("addDivMsg").insertBefore(div,reforeNode);
	var contents = Ext.query("[name=div_bottom_content]");
	for ( var i = 0; i < contents.length; i++) {
		contents[i].style.height = 'auto';
		contents[i].style.height = contents[i].scrollTop + contents[i].scrollHeight+'px';
	}
	
}

/**
 * 删除自己沟通信息
 * 
 * @param obj 删除按钮的对象
 * @param msgId 这条沟通信息的编号
 * @return
 */
function deleteMySelfMsg(obj,msgId){
	/** 弹出信息位置重新计算 chent 20160319 start */
	var position = getMsgPosition();
	
	//confirm弹出前，计算当前滚动条位置，放置alert弹出后滚动条位置改变
	var scrollTop = parent.window.Ext.getBody().getScroll().top;
	
	var a = Ext.Msg.confirm("提示信息","您确定要删除此消息吗？",function(e){ 
		if(e == "yes"){
			deleteObject = obj.parentNode.parentNode.parentNode;
			var hashvo = new HashMap();
			hashvo.put("msgId",msgId);
			Rpc( {functionId : '9028000806',success: deleteMsgOK}, hashvo);
		}else{
			return;
		}
	}).alignTo(Ext.getBody(),"tl",[position.x,position.y]);
	
	//恢复confirm弹出前的滚动条位置 兼容IE、firefox、chrome、safari chent 20160319
	if(Ext.isIE || Ext.firefoxVersion != 0) {//IE、firefox
		window.parent.document.documentElement.scrollTop = scrollTop;
	}else {//chrome、safari
		parent.window.Ext.getBody().setScrollTop(scrollTop);
	}
	/** 弹出信息位置重新计算 chent 20160319 end */
}
// 任务详情页面，弹出信息框时计算弹出位置
function getMsgPosition(){
	var position = {y:0, x:0};
	
	var scrh = getScrollTop();//获取滚动条高度
	var screenh = window.screen.availHeight-130;//窗口的高(130:菜单的高度)
	if(Ext.getBody().getScroll().top == 0){//说明是任务详情页面
		position.y = scrh + screenh / 2 - 150;//150:提示框的高
	} else{//工作计划页面
		position.y = screenh / 2 - 150;//150:提示框的高
	}
	var screenWidth = Ext.getBody().getWidth();//窗口宽度
	position.x = screenWidth/2 - 150;//x = 宽口宽度 /2 - 提示框的宽
	
	return position;
}
function deleteMsgOK(response){
	var map = Ext.JSON.decode(response.responseText);
	if(map.deleteResult == "true"){
		// 移除数据 
		deleteObject.parentNode.removeChild(deleteObject);
	}else
		Ext.Msg.alert("提示信息","删除失败");
	
}

var autoTextareas = function(elem, extra, maxHeight) {
	extra = extra || 0;
	var isFirefox = !!document.getBoxObjectFor || 'mozInnerScreenX' in window
	var isOpera = !!window.opera && !!window.opera.toString().indexOf('Opera')
	var addEvent = function(type, callback) {
		elem.addEventListener ? elem.addEventListener(type, callback, false)
				: elem.attachEvent('on' + type, callback);
	}
	getStyle = elem.currentStyle ? function(name) {
		var val = elem.currentStyle[name];
		return val;
	} : function(name) {
		return getComputedStyle(elem, null)[name];
	};

	var minHeight = parseFloat(getStyle('height'));
	// 此处有ie不兼容问题（原因未知），故人为做特殊处理
	if (minHeight <= 0) {
		minHeight = 84;
	}

	elem.style.resize = 'none';
	var change = function() {
		var scrollTop, height, padding = 0, style = elem.style;
		if (elem._length === elem.value.length)
			return;
		elem._length = elem.value.length;
		if (!isFirefox && !isOpera) {
			padding = parseInt(getStyle('paddingTop'))
					+ parseInt(getStyle('paddingBottom'));
		}
		;
		scrollTop = document.body.scrollTop
				|| document.documentElement.scrollTop;

		if (elem.scrollHeight > 84) {
			if (maxHeight && elem.scrollHeight > maxHeight) {
				height = maxHeight - padding;
				style.overflowY = 'auto';
			} else {
				height = elem.scrollHeight - padding;
				style.overflowY = 'hidden';
			}
			;
			style.height = height + extra + 'px';
			scrollTop += parseInt(style.height) - elem.currHeight;
			document.body.scrollTop = scrollTop;
			document.documentElement.scrollTop = scrollTop;
			elem.currHeight = parseInt(style.height);
			elem.parentNode.style.height = parseInt(style.height);// 当textarea的大小发生变化的时候，其父节点tr也要随着其相应的变化
		} else {
			elem.style.height = "84px";
			elem.parentNode.style.height = elem.style.height;
		}
		;
	};
	addEvent('propertychange', change);
	addEvent('input', change);
	addEvent('focus', change);
	// addEvent('click', change);
	addEvent('keyup', change);
	addEvent('mouseout', change);// 当鼠标离开某对象范围时触发此事件
	change();

};

/**
 * 点击回复，动态添加回复框及相应按钮
 * 
 * @param showId 回复框显示位置的div的id
 * @param sender 回复者
 * @param type 回复的是总结，还是计划 
 * @return
 */
function addReplyDiv(showId,sender,type){
	var saveShowId = Ext.getDom('saveShowId');
	
	if (saveShowId == null)
		document.getElementById(showId).innerHTML = "";
	else{
		var value = Ext.get('saveShowId').getValue();
		document.getElementById(value).innerHTML = "";
	}
	
	var div = document.createElement("div"); 
	div.style.width="99%";
    div.innerHTML = " 	<textarea name='msgContent1' id='write2' class='hj-wzm-six-fabu' rows='' cols='' contentEditable='true' style='width:99%;height:100%;margin-left:0px;word-break:break-all;overflow: auto;margin-bottom:10px;'></textarea>"
				  + " 	<div >"
				  + "        <a href='###' id='upfile_r1' style='display:inline-block; position:relative; overflow:hidden;float:left;'><img src='/workplan/image/biez.jpg' /> 文件/图片<input type='file' id='summaryFile_r1' name='summaryFile' onchange=\"getCheckedFileName('summaryFile_r1','fNameId_r1','upfile_r1','2','_r');\" style='position:absolute; right:0; top:0; opacity:0; filter:alpha(opacity=0);'></a>"
				  + "        <a href='###' id='upfile_r2' style='display:none; position:relative; overflow:hidden;float:left;'><img src='/workplan/image/biez.jpg' /> 文件/图片<input type='file' id='summaryFile_r2' name='summaryFile' onchange=\"getCheckedFileName('summaryFile_r2','fNameId_r2','upfile_r2','3','_r');\" style='position:absolute; right:0; top:0; opacity:0; filter:alpha(opacity=0);'></a>"
				  + "        <a href='###' id='upfile_r3' style='display:none; position:relative; overflow:hidden;float:left;'><img src='/workplan/image/biez.jpg' /> 文件/图片<input type='file' id='summaryFile_r3' name='summaryFile' onchange=\"getCheckedFileName('summaryFile_r3','fNameId_r3','upfile_r3','4','_r');\" style='position:absolute; right:0; top:0; opacity:0; filter:alpha(opacity=0);'></a>"
				  + "        <a href='###' id='upfile_r4' style='display:none; position:relative; overflow:hidden;float:left;'><img src='/workplan/image/biez.jpg' /> 文件/图片<input type='file' id='summaryFile_r4' name='summaryFile' onchange=\"getCheckedFileName('summaryFile_r4','fNameId_r4','upfile_r4','5','_r');\" style='position:absolute; right:0; top:0; opacity:0; filter:alpha(opacity=0);'></a>"
				  + "        <a href='###' id='upfile_r5' style='display:none; position:relative; overflow:hidden;float:left;'><img src='/workplan/image/biez.jpg' /> 文件/图片<input type='file' id='summaryFile_r5' name='summaryFile' onchange=\"getCheckedFileName('summaryFile_r5','fNameId_r5','upfile_r5','','_r');\" style='position:absolute; right:0; top:0; opacity:0; filter:alpha(opacity=0);'></a>"
				  + "        <div id='title_r'  style='display:none; position:relative; overflow:hidden;float:left;'><img src='/workplan/image/biez.jpg' />&nbsp;文件/图片</div> "
				  + "		 <div><span id='fNameId_r1' class='fNameClass' style='float:left;margin-left:10px;'></span>"
				  + "        <span id='fNameId_r2' class='fNameClass' style='float:left;margin-left:10px;'></span>"
				  + "        <span id='fNameId_r3' class='fNameClass' style='float:left;margin-left:10px;'></span>"
				  + "        <span id='fNameId_r4' class='fNameClass' style='float:left;margin-left:10px;'></span>"
				  + "        <span id='fNameId_r5' class='fNameClass' style='float:left;margin-left:10px;'></span></div>"
				  + "        <a href=\"javascript:sendMessage('"+showId+"','write2','"+sender+"','summaryFile','fNameId','"+type+"','_r')\" style='float:right;margin-left:10px;'>确定</a>"
				  + "        <a href=\"javascript:dispalyWritebackDiv('"+showId+"')\" style='float:right;'>取消</a>"
				  + "		 <input type='hidden' id='saveShowId' value='"+showId+"'/> "
				  + "   </div>";
	Ext.getDom(showId).appendChild(div);
	var write2 = document.getElementById("write2");
	autoTextareas(write2);
	//adapt.adaptTextareaHeight();
}

/*
 * 加载沟通信息的内容
 * 
 * type 消息来源 (1、计划|项目      2、任务      3、工作总结)
 * objectId  项目|任务|总结ID
 * 
 */
function initMessageContentlist(type,objectId)
{
	try{
		if(typeof strHaveCommuctionPri != 'undefined'){
			if(strHaveCommuctionPri == 'false'){//没有计划沟通权限 chent 20160413
				return ;
			}
		}
	}catch(e){}
	var hashvo = new HashMap();
	hashvo.put("type",type);
	hashvo.put("objectId",objectId);
	Rpc( {functionId : '9028000806',success: showCommunicationInfo}, hashvo);
}
//显示沟通信息
function showCommunicationInfo(response)
{
	var map = Ext.JSON.decode(response.responseText);
	var list = map.list;
	var fileList = map.fileList;
	var bhr = map.bhr;
	var _type = map.type; 
	toObjectId = map.objectId;
	var _str="<div class='hj-wzm-six-bottom-yi' id='publicMsg' style='height:90px;text-align:center;'>"; 
	if (bhr!="true"){
		_str+="<textarea name='msgContent' id='msgContent' class='hj-wzm-six-fabu' rows='' cols='' ";
		_str+=" style='font-size:12px; width:100%;height:60px;margin-left:0px;word-break:break-all;overflow: auto;margin-bottom:10px;margin-top:10px;'></textarea> ";
		_str+="<input type='button' id='gtBtn' class='hj-wzm-five-fabu' value='发表' onclick=\"sendMessage('','msgContent','','summaryFile','fNameId','"+_type+"','_f');\" style='margin-top:-2px;margin-bottom:30px;cursor:pointer;'/> ";
		_str+="<span id='upfile_f1' href='###' style='color:#549FE3;display:inline-block; position:relative; overflow:hidden;float:left;'> "; // IE下a:hover文本域会错位 lium
		_str+="<img src='/workplan/image/biez.jpg' />&nbsp;文件/图片";
		_str+="<input type='file' name='summaryFile' id='summaryFile_f1' onchange=\"getCheckedFileName('summaryFile_f1','fNameId_f1','upfile_f1','2','_f');\" ";
		_str+="  style='position:absolute; right:0; top:0; opacity:0; filter:alpha(opacity=0);cursor:pointer;'> ";
		_str+="</span> ";
		_str+="<a id='upfile_f2' href='###' style='display:none; position:relative; overflow:hidden;float:left;'> ";
		_str+="<img src='/workplan/image/biez.jpg' />&nbsp;文件/图片";
		_str+="<input type='file' name='summaryFile' id='summaryFile_f2' onchange=\"getCheckedFileName('summaryFile_f2','fNameId_f2','upfile_f2','3','_f');\" ";
		_str+="  style='position:absolute; right:0; top:0; opacity:0; filter:alpha(opacity=0);cursor:pointer;'> ";
		_str+="</a> ";
		_str+="<a id='upfile_f3' href='###' style='display:none; position:relative; overflow:hidden;float:left;'> ";
		_str+="<img src='/workplan/image/biez.jpg' />&nbsp;文件/图片";
		_str+="<input type='file' name='summaryFile' id='summaryFile_f3' onchange=\"getCheckedFileName('summaryFile_f3','fNameId_f3','upfile_f3','4','_f');\" ";
		_str+="  style='position:absolute; right:0; top:0; opacity:0; filter:alpha(opacity=0);cursor:pointer;'> ";
		_str+="</a> ";
		_str+="<a id='upfile_f4' href='###' style='display:none; position:relative; overflow:hidden;float:left;'> ";
		_str+="<img src='/workplan/image/biez.jpg' />&nbsp;文件/图片";
		_str+="<input type='file' name='summaryFile' id='summaryFile_f4' onchange=\"getCheckedFileName('summaryFile_f4','fNameId_f4','upfile_f4','5','_f');\" ";
		_str+="  style='position:absolute; right:0; top:0; opacity:0; filter:alpha(opacity=0);cursor:pointer;'> ";
		_str+="</a> ";
		_str+="<a id='upfile_f5' href='###' style='display:none; position:relative; overflow:hidden;float:left;'> ";
		_str+="<img src='/workplan/image/biez.jpg' />&nbsp;文件/图片";
		_str+="<input type='file' name='summaryFile' id='summaryFile_f5' onchange=\"getCheckedFileName('summaryFile_f5','fNameId_f5','upfile_f5','','_f');\" ";
		_str+="  style='position:absolute; right:0; top:0; opacity:0; filter:alpha(opacity=0);cursor:pointer;'> ";
		_str+="</a> ";
		_str+="<div id='title_f'  style='display:none; position:relative; overflow:hidden;float:left;'><img src='/workplan/image/biez.jpg' />&nbsp;文件/图片</div> ";
	}
	
	_str+="<div style='overflow:hidden;  float:left;'><span id='fNameId_f1' class='fNameClass' style='float:left;margin-left:10px;'></span>";
	_str+="<span id='fNameId_f2' class='fNameClass' style='float:left;margin-left:10px;'></span>";
	_str+="<span id='fNameId_f3' class='fNameClass' style='float:left;margin-left:10px;'></span>";
	_str+="<span id='fNameId_f4' class='fNameClass' style='float:left;margin-left:10px;'></span>";
	_str+="<span id='fNameId_f5' class='fNameClass' style='float:left;margin-left:10px;'></span></div> </div>";
	//_str+="<div style='clear:right;'></div><div  id='addDivMsg'  style='margin-top:50px;'> </div>";
	_str+="<div  id='addDivMsg'  style='margin-top:50px;'> </div>";

	document.getElementById("tongzhi").innerHTML=_str;
	if (bhr!="true"){
		var msgContent=document.getElementById("msgContent");
		adapt.listen(msgContent, 80);
		 msgContent.style.display = "inline";//inline:CSS1　内联对象的默认值。用该值将从对象中删除行  
		 msgContent.setAttribute("placeHolder", "填写对工作的意见和要求，或者汇报任务的执行情况。");
		 //让当前文本框支持PlaceHolder属性
		 basic.global.compatPlaceHolder(msgContent);
	}
	else {
	   document.getElementById("publicMsg").style.height = "0px";
	}
  
	// 移除数据 
	var we = Ext.query("#addDivMsg div"); 
	for ( var i = 0; i < we.length; i++) {
		we[i].removeNode(true);
	}
	
	// 加载沟通信息内容
	if(list.length == 0)
		return;
	
	for(var i = 0 ; i < list.length;i++){
		var msg = list[i];
		addDiv(msg,bhr);
	}
	
	
	// 加载文件图片 
	if(fileList.length == 0)
		return;
	
	// 加载文件图片 
	if(fileList.length == 0)
		return;
	for(var i = 0 ; i < fileList.length ; i++){
		colarr = fileList[i];
		for(var j = 0 ; j < colarr.length; j++){
			arr=colarr[j];
			showUploadPhoto(arr[0],'',arr[1],arr[3]);
		}
	}
}
/**
 * 调用的方法  wusy
 * @param msg
 * @param bhr
 * @return
 */
function addDiv1(msg,bhr,docFrmg){
	var sender = msg[0]; // 发送信息者
	var content = msg[1]; // 日志内容
	var submintDate = msg[2]; // 修改时间
	var id = msg[3]; // 任务id
	var photo = msg[4]; // 照片
	var type = msg[5]; // 消息来源

	var div = document.createElement("div"); 
	div.className = "hj-wzm-six-bottom-er1";
	div.style.cssText = "margin:10px 10px 0 10px;width:99%;padding-bottom:10px;border-bottom:1px #D5D5D5 dashed; overflow:hidden;";
	

	var div_top = document.createElement("div");
	div_top.style.cssText = "float:left; width:32px;";
	
	var div_bottom = document.createElement("div");
	div_bottom.style .cssText = "text-align:left;margin-left:40px;";
	
	var div_bottom_top = document.createElement("div"); 
	div_bottom_top.style.cssText = "float:top;";
	
	//修改样式问题:如果要展示的文件不够在一行展示,会破坏下面div的样式,设置父级div高度随子内容自动增加(wusy)
	var divclear = document.createElement("div");
	divclear.style.cssText = "clear:both";
	div.appendChild(divclear);
	
	div_top.innerHTML = "<a ><img class='img-circle' height='32px' width='32px' src='" + photo + "'/></a>"; 
	div.appendChild(div_top);

	var p = document.createElement("p"); 
	p.innerHTML = "<span class='hj-wzm-six-dd2' >"+sender+"</span>&nbsp;&nbsp;"
	p.innerHTML += "<span class='hj-wzm-six-dd1' style='color:#c3c8c4;'>"+submintDate+"</span>";
	div_bottom_top.appendChild(p);
	
	if (content!='')
	   div_bottom_top.innerHTML +=  "<textarea name='div_bottom_content' readonly='readonly' style='resize:none;font-family:\"微软雅黑\";font-size:12px;width:95%;height:100%;overflow-x:hidden;overflow-y:hidden;border:0;background:#FAFAFA;'>"
	   +content+"</textarea>";
	
	 if (content=='')
		 div_bottom_top.innerHTML +="</br></br>";

	div_bottom_top.innerHTML +=  " <div id='writeBackId"+id+"' name='writeBackName' style='overflow:auto;height:auto!important;height:100px;'>"
				  			 + 	 " </div>";
	
	div_bottom.appendChild(div_bottom_top);
	div.appendChild(div_bottom);
	
	// 将所有沟通信息添加到页面 id = addDivMsg1 的div中			
	docFrmg.appendChild(div);
	var reforeNode = Ext.query('#addDivMsg1 div')[0]; 
}


function showLogHistoryInfo(response){
	var map = Ext.JSON.decode(response.responseText);
	var list = map.loglist;
	var bhr = map.logbhr;
	var _type = map.type; 
	
	var _str="<div class='hj-wzm-six-bottom-yi' id='publicMsg1' style='text-align:center;'>"; 
	_str+="<div style='clear:right;'></div><div  id='addDivMsg1' > </div>";
	document.getElementById("tongzhi").innerHTML=_str;
//	if (bhr!="true"){
//		
//	}
//	else {
//	   document.getElementById("publicMsg1").style.height = "0px";
//	}
	var docFrmg = document.createDocumentFragment();//创建文档碎片（作为临时容器）,用于避免频繁操作dom文档 haosl20160907
	// 加载沟通信息内容
	if(list.length == 0)
		return;
	for(var i = 0 ; i < list.length;i++){
		var msg = list[i];
		addDiv1(msg,bhr,docFrmg);
	}
	document.getElementById("addDivMsg1").appendChild(docFrmg);
	var contents = Ext.query("[name=div_bottom_content]");
	for ( var i = 0; i < contents.length; i++) {
		contents[i].style.height = 'auto';
		contents[i].style.height = contents[i].scrollTop + contents[i].scrollHeight+'px';
	}
}

/**
 * 加载日志信息  wusy
 *  type 消息来源 (1、计划|项目      2、任务      3、工作总结)
 * objectId  项目|任务|总结ID
 * 
 * */
function initLogHistoryList(type, objectId){
	var hashvo = new HashMap();
	hashvo.put("type",type);
	hashvo.put("objectId",objectId);
	Rpc( {functionId : '9028000812',success: showLogHistoryInfo}, hashvo);
}


// 展开回复 (点击回复，展开回复框)
function showWriteBackDiv(id){
	 var writeBackClass = Ext.query('[name=writeBackName]');
	 for(var i=0; i< writeBackClass.length;i++){
		 writeBackClass[i].style.display="none";
	 }
	 var dp= document.getElementById(id).style.display;
	 if(dp=="none"){
		 document.getElementById(id).style.display="block";}
	 else{
		 document.getElementById(id).style.display="none";}
}

/**
 * 点击取消   
 * 
 * @param id 点击回复展开div的id
 * @return
 */ 
function dispalyWritebackDiv(id){
	
	document.getElementById(id).innerHTML = "";
}

/**
 * 上传附件，显示图标
 * 
 * @param id 附件显示位置div 的id
 * @param photoName 显示附件的图片
 * @param fileName 附件名称
 * @param path 附件所在路径
 * @return
 */
function showUploadPhoto(id,photoName,fileName,path){
	photoName = "file.jpg";
	//fileName = "文件夹名称.doc";
	var span = document.createElement("span"); 
	span.className="hj-wzm-gzjh-spanstyle";
	var subfileName=""
		if(fileName.length>18){
			subfileName=fileName.substring(0,18)+"...";
		}else{
			subfileName=fileName;
		}
	//zhangh 2020-3-5 下载改为使用VFS
	span.innerHTML = "<a href='/servlet/vfsservlet?fileid="+path+"'><img align='left' width='32px;' height='32px;' src='/workplan/image/"+photoName+"'/></a>"
				+ "<font title='"+fileName+"'>"+subfileName+"</font>" + "<br>"
				+ "<a href='/servlet/vfsservlet?fileid="+path+"'>下载</a>";
	var ids = "showUpLoadPhotoId"+id;
	document.getElementById(ids).appendChild(span);
}

/**
 * 获取选择的文件名
 * 
 * @param fName 附件path+附件名称
 * @param spanId 显示附件名称<span>的id
 * @param msgflag 标志从哪个入口发表沟通信息 f:直接发布 r:回复入口发布
 * @return
 */
function getCheckedFileName(fName,spanId,curaId,nextaIdIndex,msgflag){
	var path = Ext.getDom(fName).value;
	if(path == "")
		return;
	
	if (!checkFileSize(Ext.getDom(fName))) {
		clearFileInput(fName, spanId);
		return;
	}
	var subfileName=""
	var fileName =path.substr(path.lastIndexOf('\\') + 1);
	if(fileName.length>18){
		subfileName=fileName.substring(0,18)+"...";
	}else{
		subfileName=fileName;
	}
	var suffix = /\.[^\.]+/.exec(fileName);
	if(".bat"==suffix || ".exe"==suffix){
		//提示前需要将非法文件置空
		var objFile=Ext.getDom(fName);
		objFile.outerHTML=objFile.outerHTML.replace(/(value=\").+\"/i,"$1\"");
		Ext.Msg.alert("提示信息","不能上传.exe或.bat类型文件!");
		return;
	}
	if(fileName.length > 50){
		//提示前需要将文件置空
		var objFile=Ext.getDom(fName);
		objFile.outerHTML=objFile.outerHTML.replace(/(value=\").+\"/i,"$1\"");
		Ext.showAlert('文件名不能超过50个字符！');
		return ;
	}
	// 赋值给 span 标签 
//    Ext.getDom(spanId).title = "上传文件名： " + fileName;
	Ext.getDom(spanId).innerHTML = "<font title='"+fileName+"'>"+subfileName+"</font>&nbsp;&nbsp;<a href=\"javascript:deleteFileInput('"+fName+"','"+spanId+"','"+curaId+"','"+nextaIdIndex+"','"+msgflag+"');\">删除</a>";
	Ext.getDom(curaId).style.display="none";
	if(nextaIdIndex!=""){
		if(Ext.getDom("summaryFile"+msgflag+nextaIdIndex).value==""){//下一个上传框的value值为空，则说明还未上传
			Ext.getDom("upfile"+msgflag+nextaIdIndex).style.display="inline-block";//显示上传按钮
		}else{
			for(var i=1;i<=5;i++){//在删除过程中，要重新排列上传按钮的顺序
				if(Ext.getDom("summaryFile"+msgflag+i).value==""){
					 Ext.getDom("upfile"+msgflag+i).style.display="inline-block";
					 break;
				}
			}
	   }
	}else{//有两种情况会执行此处代码，1是一直上传文件，从未删除，直到上传最后一个时，2是经过删除操作以后，删除了最后一个后开始上传文件
		if(isHaveEmpty(msgflag))//当五个input框的vaule值都不为空
		{
			for(var i=1;i<=5;i++){//在删除过程中，要重新排列上传按钮的顺序
				if(Ext.getDom("summaryFile"+msgflag+i).value==""){
					Ext.getDom("upfile"+msgflag+i).style.display="inline-block";
					break;
				}
			}
		}
	}
	//加验证，//当五个input框的vaule只要有一个为空就隐藏灰色的上传文件按钮， wusy
	isHaveEmpty(msgflag);
}

/* @param msgflag 标志从哪个入口发表沟通信息 f:直接发布 r:回复入口发布*/
function deleteFileInput(fName,spanId,curaId,nextaIdIndex,msgflag){
	// 清空 file框 
	var objFile=Ext.getDom(fName); 
	objFile.outerHTML=objFile.outerHTML.replace(/(value=\").+\"/i,"$1\""); 
	// 清空 span
	Ext.getDom(spanId).innerHTML = "";
	
	//当前span为空，则其对应的input显示
	Ext.getDom(curaId).style.display="inline-block";
	if(nextaIdIndex!=""){
		for(var i=1;i<=5;i++){
			if(i!=nextaIdIndex-1){
				Ext.getDom("upfile"+msgflag+i).style.display="none";
			}else{
				Ext.getDom("upfile"+msgflag+nextaIdIndex).style.display="inline-block";
			}
		}
	}
		//修正一直删除第二个文件出现的bug(其实就是加上nextaIdIndex=""的情况下的判定)wusy
	else{
		Ext.getDom("upfile"+msgflag+1).style.display="none";
		Ext.getDom("upfile"+msgflag+2).style.display="none";
		Ext.getDom("upfile"+msgflag+3).style.display="none";
		Ext.getDom("upfile"+msgflag+4).style.display="none";
		Ext.getDom("title_f").style.display="none";
		Ext.getDom("upfile"+msgflag+5).style.display="inline-block";
	}
	
	isAllEmpty(msgflag);
	isHaveEmpty(msgflag);
	
}

//点击删除
function clearFileInput(fName,spanId){
	// 清空 file框 
	var objFile=Ext.getDom(fName); 
	objFile.outerHTML=objFile.outerHTML.replace(/(value=\").+\"/i,"$1\""); 
	// 清空 span
	Ext.getDom(spanId).innerHTML = "";
}

//当五个input框的vaule全为空时置为初始化状态
function isAllEmpty(msgflag){
	var path =Ext.getDom("summaryFile"+msgflag+"1").value;
	var path1=Ext.getDom("summaryFile"+msgflag+"2").value;
	var path2=Ext.getDom("summaryFile"+msgflag+"3").value;
	var path3=Ext.getDom("summaryFile"+msgflag+"4").value;
	var path4=Ext.getDom("summaryFile"+msgflag+"5").value;
	if(path==""&&path1==""&&path2==""&&path3==""&&path4=="")
       initInput(msgflag);
}
//当五个input框的vaule只要有一个为空就隐藏灰色的上传文件按钮
function isHaveEmpty(msgflag){
	var path =Ext.getDom("summaryFile"+msgflag+"1").value;
	var path1=Ext.getDom("summaryFile"+msgflag+"2").value;
	var path2=Ext.getDom("summaryFile"+msgflag+"3").value;
	var path3=Ext.getDom("summaryFile"+msgflag+"4").value;
	var path4=Ext.getDom("summaryFile"+msgflag+"5").value;
	
	//添加如果5个input框都不是空的,灰色上传文件按钮应为隐藏wusy
	if (path != "" && path1 != "" && path2 != "" && path3 != "" && path4 != ""){
		Ext.getDom("title" + msgflag).style.display = "inline-block";
		return false;
	}else{//更正只要五个input框有一个的vaule为空就隐藏灰色的上传文件按钮wusy
		Ext.getDom("title" + msgflag).style.display = "none";
		return true;
	}
}

//初始化五个input文件输入框
function initInput(msgflag){
	Ext.getDom("upfile"+msgflag+"1").style.display="inline-block";
	Ext.getDom("upfile"+msgflag+"2").style.display="none";
	Ext.getDom("upfile"+msgflag+"3").style.display="none";
	Ext.getDom("upfile"+msgflag+"4").style.display="none";
	Ext.getDom("upfile"+msgflag+"5").style.display="none";
	Ext.fly('msgContent').setHeight(120, false);//解决发布沟通内容较长时发表后输入框没有还原 chent 20150917
}

     
function checkFileSize(target) {     
  var isIE = /msie/i.test(navigator.userAgent) && !window.opera;    
  var fileSize = 0;          
  if (isIE && !target.files) {  
	  try {
          var filePath = target.value;      
          var fileSystem = new ActiveXObject("Scripting.FileSystemObject");         
          var file = fileSystem.GetFile (filePath);      
          fileSize = file.Size;
	  } catch(e) {
		  fileSize = 10;
	  }
  } else {     
   fileSize = target.files[0].size;      
  } 
  
  var size = fileSize / 2048;     
  if(size>10000){   
	  Ext.Msg.alert("提示信息","上传文件不能大于20M！");  
     return false;
  } else {
	  return true;
  }
     
}    
function getScrollTop() {  
        var scrollPos;
        if (window.pageYOffset) {
        	scrollPos = window.pageYOffset;
        } else if(window.parent.pageYOffset){
        	scrollPos = window.parent.pageYOffset;
        } else if (document.compatMode && document.compatMode != 'BackCompat') { 
        	scrollPos = document.documentElement.scrollTop; 
        	
        	if(scrollPos==0) {//为0时继续取父页面滚动条，兼容工作计划——任务详情页面
        		var isIE = /msie/i.test(navigator.userAgent) && !window.opera;    
	    		if(isIE) {
	    			scrollPos = window.parent.document.documentElement.scrollTop;
	    		} else {
	    			scrollPos = window.parent.document.body.scrollTop;
	    		}
	    	}
        }
        return scrollPos;   
	}
/** ################################### 从总结页面移至js文件中 ######################################### */
/** 文本域高度自适应 */
var adapt = adapt || {
	adaptTextareaHeight: function(t, minHeight) { // 文本域高度自适应
		//文本设置高度是，加500(移动端延时1秒)毫秒的延时，防止，高度设置的不准确，导致内容显示不全  haosl
		var delayTime = 500;
		if(/Android|webOS|iPhone|iPod|BlackBerry/i.test(navigator.userAgent)) {
			delayTime = 1000;
		}
		setTimeout(function(){
			var min = minHeight || 120;
			var areas = [];

			if (t) { // 指定对某一个文本域自适应
				areas[0] = t;
			} else { // 对所有的文本域自适应
				areas = document.getElementsByTagName("textarea");
			}

			for (var i = 0; i < areas.length; i++) {
				//沟通信息默认高度不走这里的设置
				if(areas[i].name=='div_bottom_content' && !areas[i].className)
					continue;
				var btw = adapt.style(areas[i]).borderTopWidth;
				var bbw = adapt.style(areas[i]).borderBottomWidth;

				var iBtw = parseInt(btw.substring(0, btw.length - 2)) || 0;
				var iBbw = parseInt(bbw.substring(0, bbw.length - 2)) || 0;

				areas[i].style.height = min + "px";

				var adaptHeight = areas[i].scrollHeight + iBtw + iBbw;
				if(/Android|webOS|iPhone|iPod|BlackBerry/i.test(navigator.userAgent)) {
					if(adaptHeight>=min) {
						adaptHeight += 150;

					}
				}
				adaptHeight = adaptHeight < min ? min : adaptHeight;

				areas[i].style.height = adaptHeight + "px";
			}
		},delayTime);
	},
	listen: function (t, minHeight) {
		var min = minHeight || 120;
		
		if ("oninput" in t) { // W3C标准浏览器
			t.oninput = adapt.bind(adapt.adaptTextareaHeight, null, t, min);
		} else { // IE
			t.onpropertychange = adapt.bind(adapt.adapt4IE, null, t, min);
			t.onkeyup = adapt.bind(adapt.adapt4IE, null, t, min);
		}
	},
	adapt4IE: function (t, min) {
		t.style.height = (t.scrollHeight > min ? t.scrollHeight : min) + "px";
	},
	style: function(elmt) { // 获取元素计算后的样式
		if (elmt.currentStyle) {
			return elmt.currentStyle;
		} else {
			return window.getComputedStyle(elmt);
		}
	},
	bind: function(fn, thisObj) { // 创建闭包环境,用于参数传递
		if (!fn || typeof fn !== "function") {return null;}
		
		var args = [];
		if (arguments[2]) {
			for (var i = 2; i < arguments.length; i++) {
				args[args.length] = arguments[i];
			}
		}
		
		return (function() {
			fn.apply(thisObj, args);
		});
	}
	
};

/** 给所有的文本域添加事件，让其能够根据内容自适应 */
Ext.onReady(function() {
	var areas = document.getElementsByTagName("textarea");
	
	for (var i = 0; i < areas.length; i++) {
		adapt.listen(areas[i]);
	}

	adapt.adaptTextareaHeight();
});
