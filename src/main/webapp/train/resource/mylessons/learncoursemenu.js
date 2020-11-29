function searchCourseMenu(lessonid, classes, filepath) {
	var map = new HashMap();
	map.put("lessonid", lessonid);
	map.put("classes", classes);
	map.put("filepath", filepath);
	map.put("opt", opt);

	Rpc( {
		functionId : '2020030200',
		success : succSearch
	}, map);
}

function succSearch(response) {
	var map = Ext.decode(response.responseText);
	var list = map.courselist;
	var isdown = map.isdown;
	var classes = map.classes;
	var lessonid = map.lessonid;
	var finishpiv = map.finishpiv;

	document.getElementById("coursemeun").innerHTML="";
	for ( var i = 0; i < list.length; i++) {
		var msg = list[i];
		addDiv(msg, isdown, classes, lessonid, finishpiv);
	}

}

function addDiv(msg, isdown, classes, lessonid, finishpiv) {
	if (msg.length < 6 || msg.length > 9)
		return;

	var courseid = msg[0];// 课件编号
	var coursename = msg[1];// 课件名称
	var coursetype = msg[2];// 课件类别
	var coursepath = msg[3];// 课件路径
	var coursetime = msg[4];// 课件总时长
	var coursecount = msg[5];// 课件播放次数
	var coursestate = "";// 课件状态
	var courselpr = "";// 课件学习进度
	var ext = "";
	if("me" == opt){
	    coursestate = msg[6];// 课件状态
	    courselpr = msg[7];// 课件学习进度
	    ext = msg[8];// 课件后缀名
	} else {
		ext = msg[6];
	}

	var dh = Ext.DomHelper;
	var cssText = "";
	if("me" == opt) {
			cssText = "margin:5px 5px 0 5px;width:290px;height:62px;padding-bottom:10px;border-bottom:1px #D5D5D5 dashed; overflow:hidden;";
	} else {
			cssText = "margin:5px 5px 0 5px;width:290px;height:52px;padding-bottom:10px;border-bottom:1px #D5D5D5 dashed; overflow:hidden;";
	}
	
	if(courseid == courseId)
		cssText += 'background-color:#ffedbf;';
	
	var divparam = {
		    tag: 'div',
		    name: 'divf'
	};
	
	var div = dh.createDom(divparam);
	dh.applyStyles(div,cssText);
	
	var div_left = dh.createDom({tag: 'div'});
	dh.applyStyles(div_left,"float:left; height:48px;");

	var div_right = dh.createDom({tag: 'div'});
	dh.applyStyles(div_right,"text-align:left;margin-left:56px;");
	
	var divclear = dh.createDom({tag: 'div'});
	dh.applyStyles(divclear,"clear:both");
	div.appendChild(divclear);
	
	var leftHTML = "";
	var herf_onclick = "href=\"/train/resource/mylessons/learncoursebyextjs.jsp?opt=" + opt + "&classes="
			+ classes + "&lesson=" + lessonid + "&course=" + courseid + "&state=" + state
			+ "&lessonState=" + lessonState + "\" onclick=\"javascirpt:if (Ext.getDom('WMP')) {pos=Ext.getDom('WMP').controls.currentPosition;}\"";
	if ("1" == coursetype) { 
		if("doc" == ext || "docx" == ext)
			leftHTML = "<a " + herf_onclick + "><img height='48px' width='48px' src='/images/word.png'/></a>";
		if("ppt" == ext || "pptx" == ext)
			leftHTML = "<a " + herf_onclick + "><img height='48px' width='48px' src='/images/ppt.png'/></a>";
		if("xls" == ext || "xlsx" == ext)
			leftHTML = "<a " + herf_onclick + "><img height='48px' width='48px' src='/images/excell.png'/></a>";
		if("pdf" == ext)
			leftHTML = "<a " + herf_onclick + "><img height='48px' width='48px' src='/images/PDF.png'/></a>";
		
	} else if ("2" == coursetype) 
		leftHTML = "<a " + herf_onclick + "><img height='48px' width='48px' src='/images/txt.png'/></a>";
	else if ("3" == coursetype) 
		leftHTML = "<a " + herf_onclick + "><img height='48px' width='48px' src='/images/shipin.png'/></a>";
	else if ("4" == coursetype) 
		leftHTML = "<a " + herf_onclick + "><img height='48px' width='48px' src='/images/scrom.png'/></a>";
	else if ("5" == coursetype) 
		leftHTML = "<a " + herf_onclick + "><img height='48px' width='48px' src='/images/aicc.png'/></a>";
	
	dh.insertHtml('afterBegin', div_left, leftHTML);
	div.appendChild(div_left);

	var html = "<table width='230px' cellspacing='0' cellpadding='0'><tr><td colspan='4' valign='top'  class='fontstyle' style='height: 30px;padding:0px;margin:-2px 0px 0px 0px;'>";
	html += "<a " + herf_onclick + ">" + coursename + "</a>&nbsp;"
			
	html += "</td></tr>";

	if ("3" == coursetype) {
		html += "<tr><td style='width:65px'><img align=\"absmiddle\" height='15px' width='15px' src='/images/bofang.png' title='播放次数'/>"
				+ coursecount + "次</td>";
		html += "<td style='width:65px'><img align=\"absmiddle\" height='15px' width='15px' src='/images/time.png' title='课件总时长'/>"
				+ coursetime + "分</td>";
	} else {
		html += "<tr><td style='width:65px'>&nbsp;</td>";
		html += "<td style='width:65px'>&nbsp;</td>";
	}
	
	if ("1" == isdown && "2" != coursetype)
		html += "<td style='width:20px' align='right'><a href='/servlet/vfsservlet?fileid="
				+ coursepath
				+ "'><img align=\"absmiddle\" height='15px' width='15px' src='/images/down.png' title='下载'/></a></td>";
	else
		html += "<td style='width:20px' align='right'>&nbsp;</td>";

	if ("me" == opt && "2" != coursestate && "0" == isLearned && courseid == courseId) {
		html += "<td style='width:80px' align='right' width='60px' style='padding-right:5px;'>";
		if("3" == coursetype || "4" == coursetype) {
			if("1" == finishpiv)
				html +=	"<a id='learned_" + courseid + "' href='###' onclick=\"learnedCourse('" + courseid + "',"+coursetype+");\">学习完毕</a>";
		} else
			html +=	"<a id='learned_" + courseid + "' href='###' onclick=\"learnedCourse('" + courseid + "',1);\">学习完毕</a>";
		
		html +=	"&nbsp;</td>";
	}else
		html += "<td style='width:80px' align='right' width='60px' style='padding-right:5px;'>&nbsp;</td>";

	html += "</tr></table>";
	
	var spec = {
		    tag: 'div',
		    html: html
		};
	
	dh.append(div_right,  spec);
	
	div.appendChild(div_right);
	
	if("me" == opt){
		var Bottom = {
			    tag: 'div',
			    id: 'courseid_' + courseid
		};
		
		var divBottom = dh.createDom(Bottom);
		dh.applyStyles(divBottom,"margin-top: 5px;left:0px;paddding:0px;text-align: left");
		div.appendChild(divBottom);
	}
	
	
	Ext.getDom("coursemeun").appendChild(div);
	
	if("me" == opt)
		silder(courselpr, courseid);
}
// 生成目录中的进度条
function silder(courselpr, courseid) {
	Ext.getDom('courseid_' + courseid).innerHTML="";
	courselpr = courselpr / 100;
	Ext.create('Ext.ProgressBar', {
		renderTo : "courseid_" + courseid,
		height : 5,
		width : 280,
		value : courselpr
	});
}

function learnedCourseSucc(response) {
	var map = Ext.decode(response.responseText);
	var flag = map.flag;
	var r5100 = map.id;
	if(flag != "false"){
		silder(100, r5100);
		Ext.getDom("learned_" + r5100).style.display = "none";
		checklp(r5100);
	} else {
		alert(ERROR_TRAIN_LEARN_COURSE);
	}
}

// 点击学习完毕后检测是否关联考试：关联了自动跳转到考试界面
function checklp(courseId){
	var map = new HashMap();
	map.put("R5100",courseId);
	Rpc( {functionId:'202003017201',success:isSearch},map);
}

function isSearch(response) {
	var map = Ext.decode(response.responseText);
	var temp = map.check;
	var te = map.che;
	var r5300 = map.r5300;
	var r5400 = map.r5400;
	if("yes" == temp){
		if(!confirm("学习完毕是否要进行考试？")){
			return;
		}
		var type = map.type;
		if (type == 1) {// 整版考试
			url = "/train/trainexam/paper/preview/paperspreview.do?b_query=link&r5300="+r5300+"&exam_type=2&flag=2&returnId=2&paperState=0&plan_id="+r5400+"&msg=1";
		} else {// 单题考试
			url = "/train/trainexam/paper/preview/paperspreview.do?b_single=link&r5300="+r5300+"&current=1&exam_type=2&flag=2&returnId=2&paperState=0&paper_id="+r5400+"&msg=1";
		}
		try {
			this.location.href=url;
			var WsShell = new ActiveXObject('WScript.Shell'); 
			WsShell.SendKeys('{F11}');
			}catch (e) {
		    }
		   
    } else if((!temp || "no" == temp)&&"yes"==te){
	    var modle = map.modle;
		var r5000 = map.r5000;
		if(!confirm("学习完毕是否要进行自测考试？")){
			return;
		}
		selfexam(r5300,r5000);
	}
}

function selfexam(r5300,r5000) {
	var map = new HashMap();
	map.put("r5300",r5300);
	map.put("r5000",r5000);
	Rpc({functionId:'2020030185',success:examSucc},map);
	
}

function examSucc(response) {
	var map = Ext.decode(response.responseText);
	var flag = map.biaozhi;
	var r5300 = map.r5300;
	var r5000 = map.r5000;
	var paper_id = map.paper_id;
	var modelType = map.modelType;
	if (flag == "ok") {
		var url = "";
		if ("1" == modelType) {
			url = "/train/trainexam/paper/preview/paperspreview.do?b_query=link&r5300="+r5300+"&exam_type=1&flag=5&returnId=2&r5000="+r5000+"&paper_id="+paper_id;
		} else {
			url = "/train/trainexam/paper/preview/paperspreview.do?b_single=link&r5300="+r5300+"&current=1&exam_type=1&flag=5&returnId=2&r5000="+r5000+"&paper_id="+paper_id;
		}
		 try {
				window.top.opener.location.href=url;
				window.top.close();
				}catch (e) {
				}
	} else {
		alert(getDecodeStr(flag));
	}
}
