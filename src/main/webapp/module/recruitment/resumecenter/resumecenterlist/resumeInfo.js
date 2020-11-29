var Global = new Object();

Global.position = "";// 职位信息，用于邮件发送
Global.resumeid = "";// 人员id
Global.nbase = "";  // 人员库
Global.username = "";
Global.email = "";   
Global.index = 0;  // 防止超链接被连续点击的计数器
Global.functionName = "";
Global.now_linkId = "";//转阶段前
Global.node_id_str = "";
Global.a0100_str = "";
Global.name_str = "";
Global.c0102_str = "";
Global.z0301_str = "";
Global.nbase_str = "";
function setGlobalPos(){
    var lastPos = document.getElementById("lastPosition");
    if(lastPos)
        Global.position = lastPos.value;
}


// 操作结果提示
Global.operateResult = function(outparamters){
	var result = Ext.decode(outparamters.responseText);
	var z0301s = result.z0301s;
	var c0102s = result.c0102s;
	var nModule = result.nModule;
    var status = result.status;
    
    if(result.info)
        Ext.Msg.alert('提示信息',getDecodeStr(result.info));
    else
    {
    	var acceptPA = document.getElementById("acceptPA");
		if(acceptPA!=null)
		{
			if(status=="1"||status=="2")
			{
				acceptPA.setAttribute("onclick","");
				acceptPA.setAttribute("style","color:gray");
			}else{
				acceptPA.setAttribute("style","color:#1b4a98");
				acceptPA.setAttribute("onclick","Global.acceptPositionApply()");
			}
		}
		var rejectPA = document.getElementById("rejectPA");
		if(rejectPA!=null)
		{
			if(status=="1"||status=="2")
			{
				rejectPA.setAttribute("onclick","");
				rejectPA.setAttribute("style","color:gray");
			}else{
				rejectPA.setAttribute("style","color:#1b4a98");
				rejectPA.setAttribute("onclick","Global.rejectPositionApply()");
			}
		}
    }

    Global.parentFn();
}

// 职位申请信息
Global.applyPositionInfo = function(zp_pos_id,position){
    var posInfo = document.getElementsByName("posInfo");
    for(var i=0; i<posInfo.length; i++){
        if(posInfo[i].getAttribute("disabled"))
            posInfo[i].removeAttribute("disabled");
    }
    var radio = document.getElementById(zp_pos_id);
    radio.setAttribute("disabled","disabled");
    document.getElementById("zp_pos_id").value = zp_pos_id;
    Global.position = position;
    var map = new HashMap();
    map.put("resumeid",Global.resumeid);
    map.put("nbase", Global.nbase);
    map.put("zp_pos_id", zp_pos_id);
    map.put("headInfo", "true");
    Rpc( {
				functionId : 'ZP0000002111',
				async:false,
				success : freshHead
		    }, map);
}

// 查询结果提示
freshHead = function(outparamters){
	var result = Ext.decode(outparamters.responseText);
	
    var username = document.getElementById("username");
    username.value = result.username;
    var zp_pos_id = result.zp_pos_id;
    document.getElementById("zp_pos_id").value = zp_pos_id;
    var recdate = document.getElementById("recdate");
    var acceptPA = document.getElementById("acceptPA");
    var rejectPA = document.getElementById("rejectPA");
    if(recdate!=null)
        recdate.innerText = result.recdate;
    if(acceptPA==null||rejectPA==null)
    {
    	return;
    }
    var status = result.status;
    if(status=="1"||status=="2")
	{
		acceptPA.setAttribute("onclick","");
		acceptPA.setAttribute("style","color:gray");
		rejectPA.setAttribute("onclick","");
		rejectPA.setAttribute("style","color:gray");
	}else{
		acceptPA.setAttribute("style","color:#1b4a98");
		acceptPA.setAttribute("onclick","Global.acceptPositionApply()");
		rejectPA.setAttribute("style","color:#1b4a98");
		rejectPA.setAttribute("onclick","Global.rejectPositionApply()");
	}
    
}

// 显示或隐藏
Global.showOrCloseArea = function(id){
    var area = document.getElementById(id);
    if(area.style.display==="block")
        area.style.display="none";
    else if(area.style.display==="none")
    	area.style.display="block";
    var img = document.getElementById(id+"img");
    if(img.src.indexOf("jianhao.png")!=-1)
        img.src = img.src.replace("jianhao.png","jiahao.png");
    else if(img.src.indexOf("jiahao.png")!=-1)
        img.src = img.src.replace("jiahao.png","jianhao.png");
}

// 转入人才库
Global.turnTalents = function(){
    Ext.Msg.confirm('提示信息', '确认将该人员转人才库？', function(btn){
	    if (btn == 'yes'){
		    var array = new Array();
	        var param = new Array();
	        param[0] = Global.resumeid;
	        param[1] = Global.nbase;
	        param[2] = Global.a0101;
	        array[0] = param;
		    var map = new HashMap();
		    map.put("array", array);
		    Rpc( {
				functionId : 'ZP0000002104',
				async:false,
				success : turnResult
		    }, map);
	    }
    });
}

//移出人才库
Global.removeTalents = function() {
	Ext.Msg.confirm('提示信息', '确认将该人员移出人才库？', function(btn) {
		if (btn == 'yes') {
			var array = new Array();
			var param = new Array();
			param[0] = Global.resumeid;
			param[1] = "";
			param[2] = Global.nbase;
			param[3] = Global.a0101;
			array[0] = param;
			var map = new HashMap();
			map.put("array", array);
			map.put("opt", "removeTalents");
			map.put("fromModule", "talents");
			Rpc( {
				functionId : 'ZP0000002103',
				success : removeResult
			}, map);
		}
	});
}


// 转人才库结果
function turnResult(outparamters){
	var result = Ext.decode(outparamters.responseText);
    if(result.result==true){
    	if(window.parent.document.getElementById("removeId"))
    		window.parent.document.getElementById("removeId").style.display="";
    	if(window.parent.document.getElementById("addId"))
    		window.parent.document.getElementById("addId").style.display="none";
    }else{
    	Ext.Msg.alert('提示信息',"在人才库中已存在！");
    }
}
// 移出人才库结果
function removeResult(outparamters){
	var result = Ext.decode(outparamters.responseText);
	var warnMsg = "";
	if (result.result != true) {
		if (result.info != '')
			warnMsg = getDecodeStr(result.info);
		else
			warnMsg = '操作失败！';
	} else {
		var info = getDecodeStr(result.info);
		var msg = getDecodeStr(result.msg);
		if (info){
			warnMsg = getDecodeStr(result.info);
		}else{
			if(window.parent.document.getElementById("removeId"))
				window.parent.document.getElementById("removeId").style.display="none";
			if(window.parent.document.getElementById("addId"))
				window.parent.document.getElementById("addId").style.display="";
		}

		if (msg && msg != "true")
			warnMsg = msg;
	}
	if (!Ext.isEmpty(warnMsg)) {
		Ext.Msg.alert('提示信息', warnMsg);
		return;
	}
}

function setDisabled(id, href){
    if(Global.index===0)
        window.location.href = href;
    Global.index++;
}

/*******************************************************************************
 * 添加评语
 */
Global.addEvaluation = function(){
	var content = Ext.getDom("addContent").value;
	var score = Ext.getDom('score').value;
	if(score==null||score==""||score==-1)
	{
		Ext.MessageBox.alert("提示信息","请选择评价星数！");return;
	}
	var map = new HashMap();
	map.put("content",content);
	map.put("score",score);
	map.put("a0100",Global.resumeid);
	map.put("nbase",Global.nbase);
	Rpc( {
		functionId : 'ZP0000002450',
		success :Global.returnEvaluation
	}, map);
}
/*******************************************************************************
 * 返回提示信息
 * 
 * @param {}
 *            param
 */
Global.returnEvaluation = function(param)
{
	var result = Ext.decode(param.responseText);
	var flg=result.flg;
	if(flg!=true)
	{
		Ext.MessageBox.alert("提示信息","当前用户评价失败！");return;
	}
	var content = result.content;
	content = content.replace(/\n/g,"<BR>");
	content = content.replace(/ /g,"&nbsp;");
	Ext.getDom("content_r").innerHTML=content;
	Ext.getDom("div_r").style.display="block";
	Ext.getDom("div_w").style.display="none";
	Ext.getDom("textMsg").style.display="block";
	initstar('starlist');
}

/*******************************************************************************
 * 重新评价
 */
Global.ReEvaluation = function()
{
	var content = Ext.getDom("content_r").innerHTML;
	content = content.replace(/ /g,"");
	content = content.replace(/\r/g,"");
	content = content.replace(/\t/g,"");
	content = content.replace(/\n/g,"");
	content = content.replace(/<br>/g,"<BR>");
	content = content.replace(/&nbsp;/g," ");
	content = content.replace(/<BR>/g,"\r");
	Ext.getDom('score').value="-1";
	Ext.getDom("div_r").style.display="none";
	Ext.getDom("div_w").style.display="block";
	Ext.getDom("textMsg").style.display="none";
	Ext.getDom("addContent").value=content;
	initstar('starlist');
}
/*******************************************************************************
 * 发送提示
 */
Global.remind= function(obj,nbase,a0100,nbase_object,a0100_object,z0301){
	obj.disabled=true;
	obj.style.opacity="0.2";
	var map = new HashMap();
	map.put("a0100_object",a0100_object);
	map.put("nbase_object",nbase_object);
	map.put("z0301",z0301);
	map.put("a0100",a0100);
	map.put("nbase",nbase);
	Rpc( {
		functionId : 'ZP0000002452',
		success :Global.remindSuccess
	}, map);
}
/**
 * 提示成功
 */
Global.remindSuccess = function(outparamters){
	var value = outparamters.responseText;
	var param = Ext.decode(value);
	var msg = param.msg;
	if(msg!="")
		Ext.MessageBox.alert("提示信息",msg);return;
}
/*******************************************************************************
 * 刷新界面
 */
Global.reload = function()
{
	location=location;
}
/*******************************************************************************
 * 调用父页面刷新按钮
 */
Global.parentFn = function()
{
	window.parent.resume_me.queryOperationList();
}
/*******************************************************************************
 * 获取下一个简历信息
 */
Ext.Loader.setConfig({
	enabled: true,
	paths: {
		'ResumeTemplateUL': '/module/recruitment/resumecenter/resumecenterlist',
		'ResumeUL': '/module/recruitment/resumecenter/resumecenterlist',
		'SYSF.FileUpLoad':'/components/fileupload',
		'OperationLogUL': '/module/recruitment/js/operationLog.js'
	}
});
Ext.util.CSS.swapStyleSheet("theme1","/module/recruitment/css/style.css");
Global.nextResume = function(){
	var nextResumeid = Ext.getDom("nextResumeid").value;
	var nextNbase = Ext.getDom("nextNbase").value;
	var nextZp_pos_id = Ext.getDom("nextZp_pos_id").value;
	var nextCurrent = Ext.getDom("nextCurrent").value;
	var nextPagesize = Ext.getDom("nextPagesize").value;
	var nextRowindex = Ext.getDom("nextRowindex").value;
	Global.turnPage(nextResumeid,nextNbase,nextZp_pos_id,nextCurrent,nextPagesize,nextRowindex);
}
Global.lastResume = function(){
	var nextResumeid = Ext.getDom("lastResumeid").value;
	var nextNbase = Ext.getDom("lastNbase").value;
	var nextZp_pos_id = Ext.getDom("lastZp_pos_id").value;
	var nextCurrent = Ext.getDom("lastCurrent").value;
	var nextPagesize = Ext.getDom("lastPagesize").value;
	var nextRowindex = Ext.getDom("lastRowindex").value;
	Global.turnPage(nextResumeid,nextNbase,nextZp_pos_id,nextCurrent,nextPagesize,nextRowindex);
}
	
Global.turnPage = function(nextResumeid,nextNbase,nextZp_pos_id,nextCurrent,nextPagesize,nextRowindex){
	var schemeValues = Global.schemeValues;
	Ext.require('ResumeTemplateUL.resumeInfoTop', function(){
		Ext.create("ResumeTemplateUL.resumeInfoTop", {nbase:nextNbase,a0100:nextResumeid,zp_pos_id:nextZp_pos_id,from:Global.from,current:nextCurrent,pagesize:nextPagesize,rowindex:nextRowindex,schemeValues:schemeValues});
	});
}

//查询操作日志
Global.searchOperationLog = function(map){
	Rpc({asynchronous:true,functionId : 'ZP0000002004',success:function(out){
		var result = Ext.decode(out.responseText);
		var searchLog = result.searchLog;
		var html="<table style ='border-collapse:separate; border-spacing:8px;' width='90%'>";
		Ext.Array.each(searchLog, function(obj, index) {
			if(index!=0){
				html+="<td colspan='3'><div style='border-bottom:1px #c5c5c5 dashed;'></div></td>"
			}
			html += "<tr>" +
					"<td nowrap='nowrap' style='vertical-align: middle;padding-top: 5px' align='left' width='50px'>" +
					"<span>"+obj.create_fullname+"</span></td>" +
					"<td align='left' style='padding-top: 5px;padding-left: 15px' >" +
					"<div style='float: left;width: 145px;'>"+obj.Create_time+"</div></td>" +
					"<td align='left' style='padding-top: 5px;word-break: break-all' width='80%'>" +
					"<div style='float: left;font-weight:bold;'>"+obj.link_name+"</div>" +
					"<div style='float: left;padding-left:10px;'>"+obj.Log_info+"</div></td>" +
					"</tr>";
			if(obj.Description)
				html += "<tr>" +
						"<td style='vertical-align: middle;padding-top: 10px' align='left' width='100px'></td>" +
						"<td align='left' style='padding-left: 15px;word-break: break-all' width='80%' colspan='2'>" +
						"<div style='float: left;'>"+obj.Description+"</div>" +
						"</td></tr>";
		});
		html += "</table>";
		Ext.getDom("logdiv").innerHTML=html;
	}},map);
}