var Global = new Object();
function trim(str){ //删除左右两端的空格
	if(str==null){
		return;
	}
　　  return str.replace(/(^\s*)|(\s*$)/g, "");
　}
var nextOne = "no";
var nextPageNum = 0;
var flag = "";
Global.submitArrangement=function(){
	flag = "submit";
	Global.arrangement();
}
//提交面试安排
Global.arrangement=function(){
	Ext.util.CSS.createStyleSheet(".x-mask{filter: alpha(opacity = 50)!important;opacity: .5!important;cursor: default!important}");
    var table = Ext.getDom("schedule");
    var rowNum = table.rows.length-1;
  //可以不添加面试官，只发面试通知给面试人员
    /*if(rowNum<1)
    {
        Ext.MessageBox.alert("提示信息","请添加一轮面试安排！");return;
    }*/
    var arrangeDate = Ext.getCmp("arrangeDate").value;//面试日期
    if(!Ext.isEmpty(arrangeDate)){
    	arrangeDate = Ext.util.Format.date(arrangeDate,'Y-m-d H:i');
    	dataType = /^[0-9]{4}-(0?[0-9]|1[0-2])-(0?[1-9]|[12]?[0-9]|3[01])\s+(20|21|22|23|[0-1]\d):[0-5]\d$/;
    	if (!dataType.test(arrangeDate)){
    		Ext.showAlert(AUDITION_DATE__FORMAT);
    		return;
    	}
    }
    	
    
    var arrangAddress = Ext.getDom("arrangAddress").value;//面试地点
    if(trim(arrangeDate)=="" || arrangeDate==null)
    {
         Ext.MessageBox.alert("提示信息","请选择面试时间！");
         return;
    }
    if(trim(arrangAddress)=="" || arrangAddress==null)
    {
         Ext.MessageBox.alert("提示信息","请选择面试地址！");
         return;
    }
    var a0100 = Ext.getDom("a0100").value;//被面试人员
    var z0301 = Ext.getDom("z0301").value;//职位id
    var a0101 = Ext.getDom("a0101").value;//面试人员信息
    var nbase = Ext.getDom("nbase").value;//人员库
    var z0351 = Ext.getDom("z0351").value;//岗位名称
    var z0325 = Ext.getDom("z0325").value;//需求部门
    var link_id = Ext.getDom("link_id").value;//流程环节
    var node_id = Ext.getDom("node_id").value;//环节状态
    var candidateMail = Ext.getDom("candidateMail").checked+"";//给面试人员发送邮件
    var examinerMail = Ext.getDom("examinerMail").checked+"";//给考官发送邮件
    if(Ext.getDom("candidateMail").checked)
    {
        var c0102 = Ext.getDom("c0102").value;
       if(!fucEmailchk(c0102))
       {
    	   Ext.showAlert("候选人邮箱地址不正确！");
           return;
       }
    }
    var candidateText = Ext.getDom("candidateText").checked+"";//给面试人员发送短信
    var arrangements="";
    for(var i=1;i<=rowNum;i++)
    {
    	var idd = table.rows[i].cells[0].id;
        var userNo = Ext.getDom("userNo"+idd).value;
        var c0104 = Ext.getDom("c0104"+idd).value;
        var userEmail = Ext.getDom("userEmail"+idd).value;
        var userName = Ext.getDom("userName"+idd).value;
        var beginTime = Ext.getDom("beginTime"+idd).value;
        var endTime = Ext.getDom("endTime"+idd).value;
        var address = Ext.getDom("address"+idd).value;
        
        if(trim(userNo)=="")
        {
        	//当最后一轮面试未选择面试官时，不将其保存
        	if(i>1&&i==rowNum)
            {
                continue;
            }
            Ext.MessageBox.alert("提示信息","第"+i+"轮安排中未选择面试官！");return;
        }else{
            if(trim(beginTime)==""||trim(endTime)=="")
            {
                Ext.MessageBox.alert("提示信息","第"+i+"轮安排中未选择面试时间！");return;
            }
            if(trim(address)=="")
            {
                Ext.MessageBox.alert("提示信息","第"+i+"轮安排中未选择面试地点！");return;
            }
            if(trim(beginTime)>trim(endTime))
            {
                Ext.MessageBox.alert("提示信息","第"+i+"轮安排中开始时间不能大于结束时间！");return;
            }
            //将一组人员保存为json格式
            var arrangementInfo = 
            	'{\"nbasA0100\":\"'+userNo+'\",' +
            	'\"c0104\":\"'+c0104+'\",' +
            	'\"email\":\"'+userEmail+'\",' +
            	'\"name\":\"'+userName+'\",' +
                '\"start_time\":\"'+beginTime+'\",' +
                '\"end_time\":\"'+endTime+'\",' +
                '\"address\":\"'+address+'\"}';
            }
            arrangements+=arrangementInfo+"\\/";
        }
    var map = new HashMap();
    map.put("arrangements", arrangements);
    map.put("arrangeDate", arrangeDate);
    map.put("arrangAddress", arrangAddress);
    map.put("a0100", a0100);
    map.put("a0101", a0101);
    map.put("z0301", z0301);
    map.put("z0351", z0351);
    map.put("z0325", z0325);
    map.put("nbase", nbase);
    map.put("link_id", link_id);
    map.put("node_id", node_id);
    map.put("candidateMail", candidateMail);
    map.put("examinerMail", examinerMail);
    map.put("candidateText", candidateText);
    map.put("function_str", "arrangement");
    //记录面试安排日志
    Rpc({asynchronous:true,functionId : 'ZP0000002004'},map);
    Rpc({success:Global.saveSuccess,functionId:'ZP0000002306'},map);
}
Global.saveSuccess=function(param){
	var value = param.responseText;
	var map = Ext.decode(value);
	var flg=map.flg;
	if(flg==1)
	{
	   if(Ext.getDom("candidateMail").checked||Ext.getDom("candidateText").checked||Ext.getDom("feedBack").checked)
	   {
    	   var c0102 = Ext.getDom("c0102").value;
           var title = Ext.getDom("title").value;
           if(!Ext.getCmp("content") && !Ext.getCmp("combo")){
        	   Ext.MessageBox.alert(PROMPT_INFORMATION,SET_TEMPLATE);
           }
           var content = Ext.getCmp("content").getValue();
           var templateId = Ext.getCmp("combo").getValue();
           var phoneNum = Ext.getDom("phoneNum").value;
           var a0101 = Ext.getDom("a0101").value;
           var a0100 = Ext.getDom("a0100").value;//被面试人员
           var z0301 = Ext.getDom("z0301").value;//职位id
           var nbase = Ext.getDom("nbase").value;//人员库
           
           var map = new HashMap();
           map.put("c0102",getEncodeStr(c0102));
           map.put("title",getEncodeStr(title));
           map.put("content",getEncodeStr(content));
           map.put("templateId",getEncodeStr(templateId));
           map.put("phoneNum",phoneNum);
           map.put("candidateMail",Ext.getDom("candidateMail").checked);
           map.put("candidateText",Ext.getDom("candidateText").checked);
           map.put("feedBack",Ext.getDom("feedBack").checked);
           map.put("a0101",a0101);
           map.put("a0100",a0100);
           map.put("nbase",nbase);
           map.put("z0301",z0301);
           Rpc( {success:Global.returnEmail,functionId:'ZP0000002304'},map);
	   }else{
	       if(flag=="submit")
	       { 
	    	   var linkId = Ext.getDom("link_id").value;
	    	   var nodeId = "0502";
	    	   var z0381 = Ext.getDom("z0381").value;
	    	   var page = Ext.getDom("page").value;
	    	   var z0301 = Ext.getDom("z0301").value;
	    	   window.location="/recruitment/position/position.do?b_search=link&z0301="+getEncodeStr(z0301)+"&z0381="+getEncodeStr(z0381)+"&page="+getEncodeStr(page)+"&node_id="+getEncodeStr(nodeId)+"&link_id="+getEncodeStr(linkId)+"&sign=2";
	       }else if(flag=="submitNext")
	       { 
	           window.location="/recruitment/recruitprocess/arrangement.do?b_arrangement=link&nextNum="+nextPageNum;
	       }
	   }
	}else{
	   Ext.MessageBox.alert("提示信息","面试安排提交失败！");
	}
}
//继续面试安排
Global.continueToArrange=function(nextNum){
	nextOne = "yes";
	flag= "submitNext"
	nextPageNum = nextNum;
	Global.arrangement();
    //window.location="/recruitment/recruitprocess/arrangement.do?b_arrangement=link&nextNum="+nextNum;
}
//添加面试人员
Global.addInterviewer=function(btn,id){
	//透明度样式
	Ext.util.CSS.createStyleSheet(".x-mask{filter: alpha(opacity = 50)!important;opacity: .5!important;cursor: default!important}");
	var nbaseA0100s = document.getElementById("userNo"+id).value;
	var arr = new Array();
	arr = nbaseA0100s.split(",");
	var dd="#td"+id;
	var picker = new PersonPicker({
		multiple: true,
		deprecate: arr,
		callback: function (ck) {
				for (var i = 0; i < ck.length; i++) {
					var c=ck[i];
					var html ="<dl style='float:left;'>";
					html += '<dt onmouseover="Global.onMouseover(this)" onmouseleave="Global.onMouseleave(this)"><img src="'+c.photo+'" width="32px" height="32px;"  class="img-middle"/>' +
					'<img style="display:none;width: 20px; height: 20px;float:left;" class="deletePic" onclick="Global.removePerson(this,'+id+',\''+c.id+'\',\''+c.c0104+'\',\''+c.email+'\',\''+c.name+'\')" src="/workplan/image/remove.png" >' +
					'</dt>';
					var Phone_number = c.c0104==undefined?"":c.c0104;
					html += '<dd>'+c.name+'&nbsp;'+Phone_number+'</dd><br>';
					html += '<dd>'+c.email+'</dd>';
					html += "</dl>"; 
					$(dd).append(html);
					document.getElementById("userNo"+id).value+=c.id+", ";
					document.getElementById("c0104"+id).value+=Phone_number+", ";
					document.getElementById("userEmail"+id).value+=c.email+", ";
					document.getElementById("userName"+id).value+=c.name+", ";
				}
		}
	}, btn);
	picker.open();
	
}
var trNum = 1;
//添加新一轮考官
Global.addNewTr=function(){
	var table = Ext.getDom("schedule");
	document.getElementById("examinerMail").checked = true;
	var rowNum = table.rows.length;
	if(rowNum>1)
	{	
	   var idd = table.rows[rowNum-1].cells[0].id;
	   trNum = idd+1;
	   var value=document.getElementById("userNo"+idd).value;
    	if(value.length==0)
    	{
    		return;
    	}
	}
	var gettime = Global.getTime(trNum);
	var temInput ='<tr>' +
			'<td style="border-right:none;border-top:none;height:30px;" id="'+trNum+'">'+rowNum+'</td> ' +
			'<td style="border-right:none;border-top:none;width:30%;height:30px;" id="td'+trNum+'">&nbsp;</td>' +
			'<td style="border-right:none;border-top:none;border-left:none;">' +
			'<font color="red">*</font>&nbsp;'+
			'<a  style="vertical-align: middle;cursor:pointer;" onclick="Global.addInterviewer(this,'+trNum+')">添加面试官</a>' +
			'</td>' +
			'<td style="border-right:none;border-top:none;" id="time'+trNum+'">'+gettime+'</td>' +
			'<td style="border-right:none;line-height:20px;border-top:none;cursor:pointer;"><font color="red">*</font>&nbsp;<input type="text" id="address'+trNum+'" class="hj-wzm-msap-dd"/></td>'+
            '<input type="hidden" id="userNo'+trNum+'"><input type="hidden" id="c0104'+trNum+'"><input type="hidden" id="userEmail'+trNum+'"><input type="hidden" id="userName'+trNum+'">' +
			'<td style="border-top:none;cursor:pointer;"><a onclick="Global.deleteCurrentRow(this)">删除</a></td>' +
			'</tr>'
$(schedule).append(temInput);// 页面追加元素 
    //trNum += 1;
}
//删除当前面试轮次
Global.deleteCurrentRow=function (obj){
    var tr=obj.parentNode.parentNode; 
    var tbody=tr.parentNode;
    //至少保留一行记录
    //可以不添加面试官，只发面试通知给面试人员
   /* if(tbody.rows.length==2)
    {
    	//透明度样式
    	Ext.util.CSS.createStyleSheet(".x-mask{filter: alpha(opacity = 50)!important;opacity: .5!important;cursor: default!important}");
    	Ext.MessageBox.alert("提示信息","请至少保留一轮面试信息！");
        return;
    }*/
    tbody.removeChild(tr); 
    if(tbody.rows.length == 1){
    	 document.getElementById("examinerMail").checked = false;
    }
    
    for (var i = 0; i<tbody.rows.length; i++) {
        if (i != 0)
        	tbody.rows[i].cells[0].innerHTML = i;
        //tbody.rows[i].cells[1].id = "td"+i;
    }
}
//时间选择
Global.getTime=function(num){
	   var html='<select name="beginhou" id="beginhou'+num+'" onchange="Global.onchangeTiem(this,'+num+')">';
	   for(var i=0;i<=23;i++)
	   {
    	   	if(i<10)
    	   	{	   		
    	       html+='<option value=0'+i+'>0'+i+'</option>';
    	   	}else{
    	   	   html+='<option value='+i+'>'+i+'</option>';
    	   	}
	   }
	   html+='</select>';
	   html+=':'
	   html+='<select name="beginmin" id="beginmin'+num+'" onchange="Global.onchangeTiem(this,'+num+')">';
       for(var i=0;i<=11;i++)
       {
       	   var j = 5*i;
       	   if(j<10)
            {           
               html+='<option value=0'+j+'>0'+j+'</option>';
            }else{
               html+='<option value='+j+'>'+j+'</option>';
            }
       }
       html+='</select>';
       html+='&nbsp;&nbsp;-&nbsp;&nbsp;';
       html+='<select name="endhou" id="endhou'+num+'" onchange="Global.onchangeTiem(this,'+num+')">';
       for(var i=0;i<=23;i++)
       {
           if(i<10)
            {           
               html+='<option value=0'+i+'>0'+i+'</option>';
            }else{
               html+='<option value='+i+'>'+i+'</option>';
            }
       }
       html+='</select>';
       html+=':'
       html+='<select name="endmin" id="endmin'+num+'" onchange="Global.onchangeTiem(this,'+num+')">';
       for(var i=0;i<=11;i++)
       {
           var j = 5*i;
           if(j<10)
            {           
               html+='<option value=0'+j+'>0'+j+'</option>';
            }else{
               html+='<option value='+j+'>'+j+'</option>';
            }
       }
       html+='</select><br/>';
       html+='<input type="hidden" id="beginTime'+num+'"/>'
       html+='<input type="hidden" id="endTime'+num+'"/>'
       return html;
}
//显示或隐藏右键发送面板
Global.sendEmailDiv=function(){
    var emailDiv = Ext.getDom("sendEmailDiv");
    if (Ext.getDom("candidateMail").checked || Ext.getDom("feedBack").checked
    		|| Ext.getDom("candidateText").checked){
        $("#sendEmailDiv").show();
        var textarea= document.getElementById("content"); 
		textarea.style.posHeight=textarea.scrollHeight;
		if(Ext.getDom("candidateMail").checked){
			Ext.getDom("EmailAddress").style.display = 'block';
		    Ext.getDom("Template").style.display = 'block';
		    Ext.getDom("TemplateTitle").style.display = 'block';
		    Ext.getDom("Templatebody").style.display = 'block';
		    Ext.getDom("filetr").style.display = 'block';
		}else{
			Ext.getDom("EmailAddress").style.display = 'none';
			Ext.getDom("filetr").style.display = 'none';
		}
    } else {
        $("#sendEmailDiv").hide();
    }
}
//隐藏域中时间改变
Global.onchangeTiem=function(select,num){
    document.getElementById("beginTime"+num).value=document.getElementById("beginhou"+num).value+":"+document.getElementById("beginmin"+num).value;
    document.getElementById("endTime"+num).value=document.getElementById("endhou"+num).value+":"+document.getElementById("endmin"+num).value;
}
//邮件返回方法
Global.returnEmail=function(param){
	var value = param.responseText;
	var map = Ext.decode(value);
    var msg=map.msg;
    if(msg!="")
    {
        Ext.MessageBox.alert("提示信息",msg);return;
    }else{
       if(flag=="submit")
       { 
           Global.goBack();
       }else if(flag=="submitNext")
       { 
           window.location="/recruitment/recruitprocess/arrangement.do?b_arrangement=link&nextNum="+nextPageNum;
       }
    }
}
/*功能介绍：检查email是否符合规则
*/
function fucEmailchk(str)
{
    var email = /^\w+([-+.]\w+)*@\w+([-.]\w+)*\.\w+([-.]\w+)*$/;
    return email.test(str)
}

Ext.Loader.setConfig({
	enabled: true,
	paths: {
		'ResumeTemplateUL': '/module/recruitment/resumecenter/resumecenterlist',
	}
});
Global.goBack=function(){
    var linkId = Ext.getDom("link_id").value;
    var nodeId = Ext.getDom("node_id").value;
    var nbase = Ext.getDom("nbase").value;//人员库
    var a0100 = Ext.getDom("a0100").value;//被面试人员
    var z0381 = Ext.getDom("z0381").value;
    var page = Ext.getDom("page").value;
    var pages = page.split("`");
    var z0301 = Ext.getDom("z0301").value;
    var c0102 = Ext.getDom("c0102").value;
    var pageFlag = Ext.getDom("flag").value;
    var resume_flag = Ext.getDom("resume_flag").value;
    var resume_name = Ext.getDom("resume_name").value;
    if(pageFlag !=1)
    	window.location="/recruitment/position/position.do?b_search=link&z0301="+getEncodeStr(z0301)+"&z0381="+getEncodeStr(z0381)+"&page="+getEncodeStr(page)+"&node_id="+getEncodeStr(nodeId)+"&link_id="+getEncodeStr(linkId)+"&sign=2";
    else{
    	Ext.require('ResumeTemplateUL.resumeInfoTop', function(){
    		Ext.create("ResumeTemplateUL.resumeInfoTop", {nbase:nbase,a0100:a0100,zp_pos_id:z0301,from:'process',current:pages[1],pagesize:pages[0],link_id:linkId,resume_flag:resume_flag,email:c0102,z0381:z0381,resume_name:resume_name,nextRowindex:undefined});
    	});
    }
    
}
//鼠标移入图片时显示删除图标
Global.onMouseover=function(obj){
	obj.childNodes[1].style.display="";
}
//鼠标移出图片时隐藏删除图标
Global.onMouseleave=function(obj){
    obj.childNodes[1].style.display="none";
}
//删除人员信息
Global.removePerson=function(obj,id,userNo,c0104,userEmail,userName){
	var node =obj.parentNode.parentNode;
	Ext.getDom("td"+id).removeChild(node);
    //alert(userNo+"--"+c0104+"--"+userEmail+"--"+userName);
	document.getElementById("userNo"+id).value=document.getElementById("userNo"+id).value.replace(userNo+", "," ");
    document.getElementById("c0104"+id).value=document.getElementById("c0104"+id).value.replace(c0104+", "," ");
    document.getElementById("userEmail"+id).value=document.getElementById("userEmail"+id).value.replace(userEmail+", "," ");
    document.getElementById("userName"+id).value=document.getElementById("userName"+id).value.replace(userName+", "," ");
    
    document.getElementById("userNo"+id).value=document.getElementById("userNo"+id).value.replace(userNo+","," ");
    document.getElementById("c0104"+id).value=document.getElementById("c0104"+id).value.replace(c0104+","," ");
    document.getElementById("userEmail"+id).value=document.getElementById("userEmail"+id).value.replace(userEmail+","," ");
    document.getElementById("userName"+id).value=document.getElementById("userName"+id).value.replace(userName+","," ");
    var str=document.getElementById("userNo"+id).value;
    if(trim(str)=="")
    {
    	var tr=Ext.getDom("td"+id).parentNode;
        var tbody=tr.parentNode;
        //至少保留一行记录
        if(tbody.rows.length==2)
        {
            return;
        }
        tbody.removeChild(tr); 
        for (var i = 0; i<tbody.rows.length; i++) {
            if (i != 0)
            tbody.rows[i].cells[0].innerHTML = i;
        }
    }
}
Global.emailPanel = function(param){
	var value = param.responseText;
	var map = Ext.decode(value);
	var data=Ext.decode(map.templateList);
	var title=map.subject;
	var content=map.content;
	var sub_module=map.sub_module;
	var nModule=map.nModule;
	var b0110=map.b0110;
	var c0102=map.c0102;
	var z0301=map.z0301;
	var a0100s=map.a0100s;
	var params=map.params;
	var method=map.method;
	var file = "";
	var fileColumn = Ext.decode(map.fileColumn);
	var fileContentList = Ext.decode(map.fileContentList);
	var configs={
                prefix:"emailFile",
                pagesize:20,
                editable:true,
                selectable:false,
                storedata:fileContentList,
                tablecolumns:fileColumn,
                datafields:['fileid','filename','extname']
        };
	file=new BuildTableObj(configs);
    var table = file.getMainPanel();
	
	var fileName = "";
	var store = new Ext.data.ArrayStore({
	fields: ['value', 'name'],
	data : data
	});
	var value = data[0][0];
	var combo = Ext.create('Ext.form.ComboBox', {
	id:'combo',
	injectCheckbox:1,
	padding:0,
	autoSelect:true,
	editable:false,
	store: store,
	width:500,
    anchor : "100%",
    cls:"img-middle",
    value:value,
    valueField:'value',
	displayField:'name',//store字段中你要显示的字段，多字段必选参数，默认当mode为remote时displayField为undefine，当 select列表时displayField为”text”
	mode: 'local',//因为data已经取数据到本地了，所以’local’,默认为”remote”，枚举完
	emptyText:'请选择一个模板',
	applyTo: 'combo',
	autoloader:true,
	listeners:{
		"select":function(combo,record,index){
			/**弹出邮件发送页面**/
			var map = new HashMap();
	    	map.put("sub_module", sub_module);
	    	map.put("nModule", nModule);
	    	map.put("b0110", b0110);
	    	map.put("c0102", c0102);
	    	map.put("a0100s", a0100s);
	    	map.put("z0301", z0301);
	    	map.put("params", params);
	    	map.put("method", method);
	    	map.put("id", record.get("value"));
	    	Rpc({
	    		functionId : 'ZP0000002000',
	    		success : Global.getEmailBean
	    	}, map);
		}
	}
	});
	var htmleditor = Ext.create('Ext.form.HtmlEditor', {
		 height : 300,
		 width:500,
		 border:0,
	     id:"content",
	     value: content,
	     cls:'hj-ms-yjzw',
		 labelSeparator:"  ",
	   	 enableAlignments: false,//是否启用对齐按钮，包括左中右三个按钮 
		 enableColors: false,//是否启用前景色背景色按钮，默认为true
		 enableFont: false,//是否启用字体选择按钮 默认为true
		 enableFontSize: false,//是否启用字体加大缩小按钮 
		 enableFormat: false,//是否启用加粗斜体下划线按钮
		 enableLists: false,//是否启用列表按钮
		 enableSourceEdit: false,//是否启用代码编辑按钮
		 enableLinks:false,
		 fontFamilies: ["宋体","隶书", "黑体","楷体"],
		 listeners:{
		 	afterrender:function(){
		 		this.getToolbar().hide();
				Global.replaceMethodDate(Ext.getDom('arrangeDate').value);
				Global.replaceMethodAddress(Ext.getDom('arrangAddress'));
			}
	}
	})
    Ext.getDom("title").value=title;
    Ext.getDom("c0102").value=c0102;
	htmleditor.render("contentPanel");
    combo.render("combo");
    file.renderTo("filePanel");
    if(fileContentList.length<2)
    {
    	Ext.getDom("filetr").style.display = 'none';
    }
    
    $("#sendEmailDiv").hide();
}
Global.getEmailBean = function(param)
{
	var value = param.responseText;
	var map = Ext.decode(value);
	var title=map.subject;
	var content=map.content;
	var fileColumn = Ext.decode(map.fileColumn);
	var fileContentList = Ext.decode(map.fileContentList);
	Ext.getDom("title").value=title;
	Ext.getCmp("content").setValue(content);
	var configs={
                prefix:"emailFile",
                pagesize:20,
                editable:true,
                selectable:false,
                storedata:fileContentList,
                tablecolumns:fileColumn,
                datafields:['fileid','filename','extname']
        };
	 if(fileContentList.length<1)
    {
    	Ext.getDom("filetr").style.display = 'none';
    }else{
    	Ext.getDom("filetr").style.display = 'block';
    	var file=new BuildTableObj(configs);
        Ext.getDom("filePanel").innerHTML="" ;
        file.renderTo("filePanel");
    }
	
    
}
/****
 * 替换面试日期和面试地址
 * @type String
 */
var replaceDate = "";
var replaceAddress = "";
Global.replaceMethodDate = function(obj)
{
	if(((obj=="" || typeof(obj)=='undefined') && replaceDate == "") || Ext.isEmpty(Ext.getCmp("content")))
		return;

	var content = Ext.getCmp("content").getValue();
	if(replaceDate!="") {
		if(!Ext.isEmpty(obj))
			content = content.replace(replaceDate,obj);
		else
			content = content.replace(replaceDate,"(面试日期)");
	} else
		content = content.replace("(面试日期)",obj);
	
	replaceDate = obj;
	Ext.getCmp("content").setValue(content);
}
Global.replaceMethodAddress = function(obj)
{
	if((obj.value=="" && replaceAddress == "") || Ext.isEmpty(Ext.getCmp("content")))
		return;
	
	var content = Ext.getCmp("content").getValue();
	
	if(replaceAddress!="") {
		if(!Ext.isEmpty(obj.value))
			content = content.replace(replaceAddress,obj.value);
		else
			content = content.replace(replaceAddress,"(面试地址)");
	} else
		content = content.replace("(面试地址)",obj.value);
	
	replaceAddress = obj.value;
	Ext.getCmp("content").setValue(content);
}
Global.just = function(nextPageNum){
        window.location="/recruitment/recruitprocess/arrangement.do?b_arrangement=link&nextNum="+nextPageNum;
}