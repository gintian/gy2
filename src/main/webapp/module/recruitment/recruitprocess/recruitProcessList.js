var Global = new Object();
var linkIds = "";// 用于流程样式调整
var projectIds = "";// 用于查询方案样式调整
var globalA0100 = "";// 用于上传文件
var globalNbase = "";// 用于上传文件
var clickflag = true; //是否可以点击
Global.functionName="";
Global.linkId="";
Global.nodeId="";
Global.a0100No="";
Global.z0301="";
Global.c0102_str="";
Global.nbase_str = "";
Global.name_str = "";
Global.emailItemId = "";//邮件地址指标
Global.from = "";//调用位置
Global.person = []//转阶段已选待办接收人;
Global.bususer = []
Global.pageLode = function(){
	location=location;
}
// 搜索框模糊查询
Global.boxSearch=function(str){ 
	var linkId = Ext.getDom("linkId").value;
	var nodeId = Ext.getDom("nodeId").value;
     var config = tablelist.getTableConfig();
     var map = new HashMap();
     map.put("projectId",getEncodeStr(linkId));
     map.put("status",getEncodeStr(nodeId));
     map.put("tablekey",getEncodeStr(config.tablekey));
     map.put("z0301",Global.z0301);
     map.put("z0381",Ext.getDom("z0381").value);
     map.put("queryStr",getEncodeStr(str));
     Rpc( {
		functionId : 'ZP0000002301',
		success :Global.loadList
	}, map);
}

//导入推荐排名
Global.importRank = function(){	
	Ext.getBody().mask();
    var win=new Ext.create('Ext.window.Window', {
        title: '导入推荐排名',
        id:'importDataExcelId',
        width:300,
        height:180, 
        layout: {
            align: 'middle',
            pack: 'center',
            type: 'vbox'
        },        
        items: [{
            layout: 'column',
            border: false,
            margin: '0 10 20 0',
            padding: '-30 0 10 0',
            width: 200, 
            items: [{
                columnWidth: 0.7,
                border: false,                                  
                html: "<font >1、 下载模板文件</font>",                
            },
            {
                columnWidth: 0.3,
                border: false,
                items: {
                    xtype: 'button',
                    text: '下载',                                     
                    handler: function() { 
                        var outName='';//下载文件
                        var flag = '0';//下载程序是否执行完成
                        var success = false;//下载程序是否执行成功  
                        var messages = '';//下载程序是否执行成功                
                        var map = new HashMap();
                        map.put("Check","1");
                        var records = Ext.getCmp('tablelist_tablePanel').getSelectionModel().getSelection();
                    	if(records.length==0)
                    	{
                    		map.put("Check","0");
                    	}
                    	var a0100="";
                    	for(var i=0;i<records.length;i++) {
                    		a0100 = a0100+records[i].get('a0100_e')+",";
                    	}
                        
                    	if(a0100 ==""){
                    		 Ext.showAlert(EXPORT_FAILED_MESSAGE) 
                    		 return;
                    	}
                    	
                        var link_id= Ext.getDom("linkId").value;
                        map.put("a0100",a0100);
                        map.put("z0301",Global.z0301);
                        map.put("link_id", link_id);
                        map.put("flag","1"); 
                            Rpc({functionId:'ZP0000002131',timeout:10000000,async:true,success:function(form,action){
                                var result = Ext.decode(form.responseText);
                                flag = '1';//1表示下载程序执行完成
                                success= result.success;
                                messages = result.messages;
                                if(success){
                                    outName=result.outName;
                                }                           
                            }},map);
                            var msgBox = Ext.create('Ext.window.MessageBox',{alwaysOnTop:true}).show({
                                title: common.button.promptmessage,
                                msg:'动态更新进度条和信息文字',
                                modal:true,
                                width:300,
                                progress:true
                           });
                            var progressText='';//进度条信息
                            var task = {
                                run:function(){
                                    //进度条信息
                                    progressText = '下载中...';
                                    //更新信息提示对话框
                                    msgBox.updateProgress('',progressText,'当前时间：'+Ext.util.Format.date(new Date(),'Y-m-d g:i:s A'));
                                    //下载文件成功，关闭更新信息提示对话框
                                    if(flag=='1'){
                                        Ext.TaskManager.stop(task);
                                        msgBox.hide();
                                        if(success){
                                            window.location.target="_blank";
                                            window.location.href = "/servlet/vfsservlet?fromjavafolder=true&fileid="+outName;
                                        }else{
                                            Ext.showAlert("导出失败！错误信息："+messages) 
                                        }
                                    }
                                },
                                interval:1000//时间间隔
                            }
                            Ext.TaskManager.start(task);                           
                    }
                }
            }]
        },{
            layout: 'column',
            border: false,
            padding: '-30 0 10 0',
            width: 200, 
            items:[{
                columnWidth: 0.7,
                border: false,
                xtype: 'label',
                text: '2、 请选择导入文件',
                }
            ,{
                columnWidth: 0.3,
                border: false,
                items: {
                    xtype: 'button',
                    text : '浏览', 
                    id:'importResumeId',
                    padding:0,
                    height:22,
                    width:38,
                }
            }]}, {
            xtype:'box',
            border:false,
            width:40,
            height:22,
            margin: '-22 0 0 150',
            listeners:{
                afterrender:function() {
                    Ext.require('SYSF.FileUpLoad', function(){
                    Ext.create("SYSF.FileUpLoad",{
                        upLoadType:3,
                        isDelete:true,
                        height:22,
                        width:38,
                        renderTo:'importResumeId',
                        fileSizeLimit:'20MB',
                        fileExt:"*.xls;*.xlsx",
                        buttonText:'',
                        isTempFile:true,
                        VfsModules:VfsModulesEnum.ZP,
                        VfsFiletype:VfsFiletypeEnum.other,
                        VfsCategory:VfsCategoryEnum.other,
                        success:function(list){                                       
                                    var success = false;//是否上传成功                                    
                                    var flag = '0';//上传程序是否执行完成
                                    var message = '';//报错提示信息
                                    var fileId = list[0].fileid;
                                    var map = new HashMap();                                         
                                    map.put("fileId",fileId);
                                    map.put("z0301",Global.z0301);
                                    map.put("flag","2");                              
                                    Rpc({functionId:'ZP0000002131',async:true,success:function(response,action){
                                        var result = Ext.decode(response.responseText);                                         
                                        flag = '1';
                                        success = result.success;                                   
                                        msg = result.msg;   
                                        dataNumber = result.dataNumber;   
                                        var stoteData = Ext.decode(msg);
                                        if(msg !="false"){
                                        var gridstore = Ext.create('Ext.data.Store', {                                                                          
                                            fields:['data'],
                                            data:stoteData
                                        });                                     
                                        
                                        var gridpanel = Ext.create('Ext.grid.Panel', {
                                            store:gridstore,
                                            columns: [
                                                { header: '',  dataIndex: 'data' , flex: 1}                                                
                                            ],
                                            height: 500,
                                            width: 600,
                                        bbar: //Grid操作工具栏
                                         ['->', {
                                            text: "继续导入",
                                            id:'carryOn',
                                            margin: '0 20 0 0',
                                            handler: function() {
                                            	map.put("z0301",Global.z0301);
                                                map.put("flag", "3");
                                                Rpc({
                                                    functionId: 'ZP0000002131',
                                                    async: false,
                                                    success: function(response, action) {
                                                        msgBoxss.close();
                                                        var result = Ext.decode(response.responseText);
                                                        succNumber = result.succNumber;
                                                        Ext.Msg.alert("提示信息", succNumber,function(){ 
                                                            Ext.getCmp('importDataExcelId').close();
                                                          });
                                                        var store = Ext.data.StoreManager.lookup('tablelist_dataStore');
                                                        store.reload();
                                                    }
                                                },map);
                                            }
                                        },
                                        {
                                            text: "返回",
                                            handler: function() {
                                                msgBoxss.close();
                                            }
                                        },
                                        '->']
                                        });
                                        
                                        if(dataNumber==0)
                                            Ext.getCmp('carryOn').setHidden(true);
                                       
                                        
                                    var msgBoxss = Ext.create('Ext.window.Window', {
                                                title: '导入报告',
                                                height: 550,
                                                width: 610,
                                                items: gridpanel
                                            }).show();                                  
                                        } else {                                         
                                             succNumber = result.succNumber;
                                             Ext.showAlert(succNumber, function(){ 
                                                 Ext.getCmp('importDataExcelId').close();
                                               });
                                             var store = Ext.data.StoreManager.lookup('tablelist_dataStore');
                                             store.reload();
                                            
                                        }
                                    }},map);
                                                            
                                    var msgBox = Ext.create('Ext.window.MessageBox',{alwaysOnTop:true}).show({
                                        title: common.button.promptmessage,
                                        modal:true,
                                        width:300,
                                        progress:true
                                    });

                                    var progressText='正在导入数据,请稍等...';//进度条信息
                                    var win = Ext.getCmp('importDataId');
                                    var task = {
                                        run:function(){
                                            //进度条信息
                                            //更新信息提示对话框
                                            msgBox.updateProgress('',progressText,'');
                                            //完成上传文件，关闭更新信息提示对话框
                                            if(flag =='1'){
                                                Ext.TaskManager.stop(task);
                                                msgBox.hide();                                                  
                                            }
                                        },
                                        interval:500//时间间隔
                                    }
                                    Ext.TaskManager.start(task);
                            }
                    });
                    Ext.getDom("importResumeId").childNodes[1].style.marginTop = "-22px";
                    });
                }
            }
        }]
    }).show();
	win.on("close",function(){	     
		Ext.getBody().unmask();
	});
};

// 流程查询
Global.queryStageList=function(link_id,node_id,priv){
	//防止在页面刷新的时候点击，造成取不到流程Id
	if(!clickflag){
		return;
	}
	clickflag = false;
	 document.recruitProcessForm.action="/recruitment/recruitprocess/recruitprocesslist.do?b_query=link&link_id="+link_id+"&node_id="+node_id;
	  document.recruitProcessForm.submit();
}
// 方案查询
Global.queryProjectList=function(projectId,status){
	var projectA = Ext.getDom(status);
     if(projectA!=null){
    	 //增加非空判断
    	if(Ext.getDom("nodeId").value != "")
        var project = document.getElementById(Ext.getDom("nodeId").value);// 得到上一次点击的对象
        if(project!=null){
        	project.style.color = "#1b4a98";
            project.style.textDecoration="none";
            
        }
        if(projectA.style.textDecoration=="none"||projectA.style.textDecoration=="")
        { 
        	var allbuttomDom =Ext.getDom(status.substring(0,2));
        	if(Ext.getDom("nodeId").value==status)//
        	{
        		projectA.style.color = "#1b4a98";
        		projectA.style.textDecoration="none";
        		projectIds=status;
                status="";
                //当点击有下划线的按钮时，对全部按钮做处理
                allbuttomDom.style.textDecoration="underline";
                allbuttomDom.style.color = "ba2636";
        	}else{
        		if(status!="0"){        		
                    projectA.style.color = "#ba2636";
                    projectA.style.textDecoration="underline";
                    if(status.length!=2)
                    	allbuttomDom.style.textDecoration="none";
                    
                    allbuttomDom.style.color = "#1b4a98";
        		}
                projectIds=status;
        	}
        }else{
        	projectA.style.color = "#1b4a98";
            projectA.style.textDecoration="none";
            projectIds=status;
        }
     }
     var config = tablelist.getTableConfig();
     if(status=="0")
     {
     	Ext.getCmp('tablelist_tablePanel').getSelectionModel().selected.clear();
     }
     var map = new HashMap();
     map.put("projectId",getEncodeStr(projectId));
     map.put("status",getEncodeStr(status));
     map.put("tablekey",getEncodeStr(config.tablekey));
     map.put("z0301",Global.z0301);
     map.put("z0381",Ext.getDom("z0381").value);
     Ext.getDom("nodeId").value=status;
     Rpc( {
		functionId : 'ZP0000002301',
		success :Global.loadList
	}, map);
     nodeId=status;
}
// 查询招聘流程中人员数量信息
Global.queryProjectNum=function(projectId,status,priv){
	 var linkId = Ext.getDom("linkId").value;
	 if(linkId!=projectId)
	 {
	   document.recruitProcessForm.action="/recruitment/recruitprocess/recruitprocesslist.do?b_query=link&link_id="+projectId+"&node_id="+$URL.encode(status);
       document.recruitProcessForm.submit();
       return;
	 }
	 var project = Ext.getDom(Ext.getDom("nodeId").value);// 得到上一次点击的对象
    if(project!=null){
        project.style.textDecoration="none";
    }
     var config = tablelist.getTableConfig();
     var map = new HashMap();
     map.put("projectId",getEncodeStr(projectId));
     map.put("status",getEncodeStr(status));
     map.put("tablekey",getEncodeStr(config.tablekey));
     map.put("z0301",Global.z0301);
     map.put("z0381",Ext.getDom("z0381").value);
     Rpc( {
		functionId : 'ZP0000002301',
		success :Global.loadList
	}, map);
	 linkIds=projectId;
	 nodeId=status;
}
// 查询成功后回调函数
Global.loadList=function(outparamters){
	var value = outparamters.responseText;
	var map = Ext.decode(value);
	var nodeId = map.nodeId;
	if(typeof nodeId != "undefined" && nodeId != null && nodeId != ""){
	 Ext.getDom("nodeId").value=nodeId;
	}
	
    var store = Ext.getCmp("tablelist_tablePanel").getStore();
    store.load();
}
// 对列表中的姓名加上连接，查看简历
Global.queryResume=function(value,metaData,Record){
     var a0100 = Record.data.a0100_e;
     var nbase = Record.data.nbase_e;
     var resume_flag = Record.data.resume_flag;
     var resume_name = Record.data.resume_flag1;
     var c0102 = Record.data.c0102;
     return "<a href='javascript:void(0);' onclick='javascript:Global.queryPosition(\""+a0100+"\",\""+nbase+"\",\""+resume_flag+"\",\""+c0102+"\",\""+resume_name+"\");'>"+value+"</a>";
}
// 简历评价图片渲染方法
Global.evaluation=function(value,metaData,Record){
	var sum = Record.data.score;
    var num = Record.data.num;
    var score = parseInt(sum);
    var html = "";
    var str = "";
    if(isNaN(sum/num))
    {
    	str = "未评价";
    }else{
    	str = "平均分："+score/2+"星";
    }
    if(score>=6)
    {
    	html = "<div style='width:30px;height:30px;text-align:center;margin-left:auto; margin-right:auto;'><img  title='"+str+"' src='/module/recruitment/image/recruit/passChoice.png'></div>";
    }else if(isNaN(sum/num)){
    	html = "<label title='"+str+"'>&nbsp;</label>";
    }else{
    	html = "<div style='width:30px;height:30px;text-align:center;margin-left:auto; margin-right:auto;'><img  title='"+str+"' src='/module/recruitment/image/recruit/obsolete.png'></div>";
    }
    return html;
}
// 查询人员简历信息
Global.queryPosition=function(a0100,nbase,resume_flag,c0102,resume_name){
    var linkId = Ext.getDom("linkId").value;
    var nodeId = Ext.getDom("nodeId").value;
    var z0381 = Ext.getDom("z0381").value;
    var store = Ext.data.StoreManager.lookup('tablelist_dataStore');
    Global.qureyResume(nbase,a0100,Global.z0301,'process',store.currentPage,store.pageSize,linkId,resume_flag,c0102,z0381,resume_name);
}
Ext.Loader.setConfig({
	enabled: true,
	paths: {
		'ResumeTemplateUL': '/module/recruitment/resumecenter/resumecenterlist',
		'ResumeUL': '/module/recruitment/resumecenter/resumecenterlist',
		'SYSF.FileUpLoad':'/components/fileupload',
		'sendEmailUL': '/module/recruitment/js/sendEmail.js',
		'OperationLogUL': '/module/recruitment/js/operationLog.js'
	}
});
Ext.util.CSS.swapStyleSheet("theme1","/module/recruitment/css/style.css");
Global.qureyResume=function(nbase,a0100,zp_pos_id,from,current,pagesize,link_id,resume_flag,email,z0381,resume_name,nextRowindex)
{
//	Ext.util.CSS.swapStyleSheet("theme2","/components/tableFactory/tableGrid-theme/tableGrid-theme-all.css");
	var pagehtmltop = Ext.getCmp("pagehtmltop");
	if(pagehtmltop!=undefined)
		pagehtmltop.destroy();
    if(zp_pos_id=="2iIeo7kAcbU@3HJD@")
    {
    	zp_pos_id = "";
    }
    var northpanel = window.parent.Ext.getCmp("northpanel");
    var tablelist_viewport = Ext.getCmp("tablelist_viewport");
    if(northpanel!=null)
    	northpanel.destroy();
    if(tablelist_viewport!=null)
    	tablelist_viewport.destroy();
//    Ext.getBody().setHtml("");  //会引起样式问题，暂时没办法解决
    //会把样式清空，重新引用
//    Ext.util.CSS.swapStyleSheet("extCss","/ext/ext6/resources/ext-theme.css");
//    Ext.util.CSS.createStyleSheet(".x-mask{filter: alpha(opacity = 50)!important;opacity: .5!important;cursor: default!important}");
//    Ext.util.CSS.createStyleSheet(".x-nbr .x-window-header-default-top{background-color:white !important}");
//	Ext.util.CSS.createStyleSheet(".x-toolbar-footer{background-color:white !important;margin-top:0px}");
    Ext.require('ResumeTemplateUL.resumeInfoTop', function(){
		Ext.create("ResumeTemplateUL.resumeInfoTop", {nbase:nbase,a0100:a0100,zp_pos_id:zp_pos_id,from:from,current:current,pagesize:pagesize,link_id:link_id,resume_flag:resume_flag,email:email,z0381:z0381,resume_name:resume_name,nextRowindex:nextRowindex});
	});
}
// 对人员进行操作
Global.operation=function(param){
	var function_str=param.functions;
	var custom_name=param.custom_name;
	var link_id= Ext.getDom("linkId").value;
	var node_id= Ext.getDom("nodeId").value;
	var records = Ext.getCmp('tablelist_tablePanel').getSelectionModel().getSelection();
	if(records.length==0)
	{
		Ext.showAlert("请选择操作数据！");
		return;
	}
	var a0100="";
	var resume_flag=""
	var nodeId="";
	var nodeStr="";
	var name="";
	var nbase="";
	var c0102="";
	var statusId = records[0].get('resume_flag');
	
	for(var i=0;i<records.length;i++) {
		a0100 = a0100+records[i].get('a0100_e')+",";
		nbase = nbase+records[i].get('nbase_e')+",";
		c0102 = c0102+records[i].get(Global.emailItemId)+",";
		nodeId =nodeId+records[i].get('resume_flag').split("`")[0]+",";
		nodeStr =nodeStr+records[i].get('resume_flag').split("`")[1]+",";
		name =name+records[i].get('a0101')+",";
		resume_flag = resume_flag+records[i].get('resume_flag')+",";
	}
	
	
	if(node_id==""){
		node_id=projectIds;
	}
	
	if(a0100){
		a0100 = a0100.substr(0,a0100.lastIndexOf(","));
	}
	if(nbase){
		nbase = nbase.substr(0,nbase.lastIndexOf(","));
	}
	if(c0102){
		c0102 = c0102.substr(0,c0102.lastIndexOf(","));
	}
	if(nodeId){
		nodeId = nodeId.substr(0,nodeId.lastIndexOf(","));
	}
	if(nodeStr){
		nodeStr = nodeStr.substr(0,nodeStr.lastIndexOf(","));
	}
	if(name){
		name = name.substr(0,name.lastIndexOf(","));
	}
	Global.active(function_str,link_id,node_id,nodeId,nodeStr,Global.z0301,a0100,nbase,name,c0102,custom_name);
}

Global.active = function(function_str,link_id,node_id,nodeId,nodeStr,z0301,a0100,nbase,name,c0102,custom_name,from){
	Global.from = from;
	/*
	 * 当前数组全为改变状态值操作方法，当传入方法满足其中时进行修改操作
	 * toStage:转新阶段,changeStatus：改变状态,passChoice：进行时,reserve：备选,obsolete：淘汰,arrangement:安排面试
	 * sendOffer:offer通知,acceptOffer:接受offer,refuseOffer:拒绝offer,rzRegister:入职办理,refuseRz:拒绝入职
	 */
     var nId = nodeId.split(",");
     var nStr = nodeStr.split(",");
     var a0100s = a0100.split(",");
     var nbases = nbase.split(",");
     var names = name.split(",");
	 var status = ['toStage','changeStatus','sendOffer','invitationEvaluation','Global.recommendOtherPosition'];
     var stopNode = ['0106','0206','0308','0408','0508','0604','0704','0806','1003','1005'];
    
     for(var i=0;i<status.length;i++)
     {
     	if(function_str==status[i])
        {
        	for(var j=0;j<nId.length-1;j++)
            {	
        		var nIdTemp = nId[j];
        		var nameTemp = names[j];
				if(Ext.Array.contains(stopNode,nIdTemp)&&function_str=="Global.recommendOtherPosition"){
            		Ext.showAlert(nameTemp+"为“"+nStr[j]+"”状态,\n不允许推荐职位！");
            		return;
            	}
            	else if(Ext.Array.contains(stopNode,nIdTemp)&&function_str=="toStage"){
            		Ext.showAlert(nameTemp+"为“"+nStr[j]+"”状态,\n不允许转新阶段！");
            		return;
            	}
            	else if(Ext.Array.contains(stopNode,nIdTemp)&&function_str=="changeStatus"){
            		Ext.showAlert(nameTemp+"为“"+nStr[j]+"”状态,\n不允许更改状态！");
            		return;
            	}
            	else if(Ext.Array.contains(stopNode,nIdTemp)&&function_str=="sendOffer"){
            		Ext.showAlert(nameTemp+"为“"+nStr[j]+"”状态,\n不允许发送offer！");
            		return;
            	}
            	else if(Ext.Array.contains(stopNode,nIdTemp)&&function_str=="invitationEvaluation"){
            		Ext.showAlert(nameTemp+"为“"+nStr[j]+"”状态,\n不允许邀请评价！");
            		return;
            	}
            }
        }
     }
     
	var function_operation = ['passChoice','reserve','obsolete','acceptOffer','refuseOffer','refuseRz','rzRegister','sendOffer'];
	for(var i=0;i<function_operation.length;i++)
	{
		if(function_str==function_operation[i])
		{
			for(var j=0;j<nId.length-1;j++)
			{// 进行中状态可操作"0102"!=nIdTemp&&"0202"!=nIdTemp&&"0403"!=nIdTemp&&"0503"!=nIdTemp&&"0601"!=nIdTemp&&"0701"!=nIdTemp&&"0802"!=nIdTemp&&"1002"!=nIdTemp;
				var nIdTemp = nId[j];
				var nameTemp = names[j];
				if(function_str=="passChoice"){// 通过
					if("0101"!=nIdTemp&&"0104"!=nIdTemp&&"0201"!=nIdTemp&&"0204"!=nIdTemp&&"0501"!=nIdTemp
					&&"0301"!=nIdTemp&&"0302"!=nIdTemp&&"0305"!=nIdTemp&&"0303"!=nIdTemp
					&&"0502"!=nIdTemp&&"0505"!=nIdTemp&&"0701"!=nIdTemp&&"0601"!=nIdTemp&&"0603"!=nIdTemp
					&&"0102"!=nIdTemp&&"0202"!=nIdTemp&&"0403"!=nIdTemp&&"0503"!=nIdTemp&&"0601"!=nIdTemp
					&&"0701"!=nIdTemp&&"0802"!=nIdTemp&&"1002"!=nIdTemp)
					{
						Ext.showAlert(nameTemp+"为“"+nStr[j]+"”状态,\n不允许通过！");
						return;
					}
				}else if(function_str=="reserve"){// 备选
					if("0101"!=nIdTemp&&"0103"!=nIdTemp&&"0104"!=nIdTemp&&"0201"!=nIdTemp&&"0203"!=nIdTemp&&"0204"!=nIdTemp
					&&"0301"!=nIdTemp&&"0302"!=nIdTemp&&"0305"!=nIdTemp&&"0303"!=nIdTemp
					&&"0501"!=nIdTemp&&"0502"!=nIdTemp&&"0505"!=nIdTemp&&"0701"!=nIdTemp&&"0801"!=nIdTemp&&"0803"!=nIdTemp
					&&"0601"!=nIdTemp&&"0603"!=nIdTemp&&"0102"!=nIdTemp&&"0202"!=nIdTemp&&"0403"!=nIdTemp&&"0503"!=nIdTemp
					&&"0601"!=nIdTemp&&"0701"!=nIdTemp&&"0802"!=nIdTemp&&"1002"!=nIdTemp)
					{
						Ext.showAlert(nameTemp+"为“"+nStr[j]+"”状态,\n不允许备选！");
						return;
					}
				}else if(function_str=="obsolete"){// 淘汰
					if("0101"!=nIdTemp&&"0103"!=nIdTemp&&"0104"!=nIdTemp&&"0201"!=nIdTemp&&"0203"!=nIdTemp&&"0204"!=nIdTemp
					&&"0301"!=nIdTemp&&"0302"!=nIdTemp&&"0305"!=nIdTemp&&"0303"!=nIdTemp
					&&"0501"!=nIdTemp&&"0502"!=nIdTemp&&"0505"!=nIdTemp&&"0701"!=nIdTemp&&"0801"!=nIdTemp&&"0803"!=nIdTemp
					&&"0601"!=nIdTemp&&"0102"!=nIdTemp&&"0202"!=nIdTemp&&"0403"!=nIdTemp&&"0503"!=nIdTemp
					&&"0601"!=nIdTemp&&"0701"!=nIdTemp&&"0802"!=nIdTemp&&"1002"!=nIdTemp)
					{
						Ext.showAlert(nameTemp+"为“"+nStr[j]+"”状态,\n不允许淘汰！");
						return;
					}
				}else if(function_str=="acceptOffer"){// 接受offer
					if("0801"!=nIdTemp&&"0803"!=nIdTemp&&"0802"!=nIdTemp)
					{
						Ext.showAlert(nameTemp+"为“"+nStr[j]+"”状态,\n不允许接受offer！");
						return;
					}
				}else if(function_str=="refuseOffer"){// 拒绝offer
					if("0801"!=nIdTemp&&"0803"!=nIdTemp&&"0802"!=nIdTemp)
					{
						Ext.showAlert(nameTemp+"为“"+nStr[j]+"”状态,\n不允许拒绝offer！");
						return;
					}
				}else if(function_str=="refuseRz"){// 拒绝入职
					if(("1001"!=nIdTemp&&"1002"!=nIdTemp)||"1003"==nIdTemp||"1005"==nIdTemp)
					{
						Ext.showAlert(nameTemp+"为“"+nStr[j]+"”状态,\n不允许拒绝入职！");
						return;
					}
				}else if(function_str=="rzRegister"){
					if(("1001"!=nIdTemp&&"1002"!=nIdTemp)||"1003"==nIdTemp||"1005"==nIdTemp){
						Ext.showAlert(nameTemp+"为“"+nStr[j]+"”状态,\n不允许登记入职！");
						return;
					}
				}else if(function_str=="toStage"){// 转新阶段
					if("1003"==nIdTemp){
						Ext.showAlert(nameTemp+"为“"+nStr[j]+"”状态,\n不允许转新阶段！");
						return;
					}
				}else if(function_str=="changeStatus"){// 更改状态
					if("1003"==nIdTemp){
						Ext.showAlert(nameTemp+"为“"+nStr[j]+"”状态,\n不允许更改状态！");
						return;
					}
				}else if(function_str=="toTalents"){// 转人才库
					if("1003"==nIdTemp){
						Ext.showAlert(nameTemp+"为“"+nStr[j]+"”状态,\n不允许转入人才库！");
						return;
					}
				}
			}
		}
	}
	//发送offer、面试环节通过和淘汰、 更改状态、发送通知
	if("sendOffer" == function_str||function_str=="changeStatus"||function_str=="sendnotice"||(("obsolete" == function_str||"passChoice" == function_str)&&node_id.substring(0,2)=="05")) {
		var nModule = "40";
		if("obsolete" == function_str)
			nModule = "50";
		else if(function_str=="changeStatus"||function_str=="sendnotice")
			nModule = "90";
		else if(function_str=="sendOffer")
			nModule = "60";
		if(!(function_str=="changeStatus"||function_str=="sendnotice"))
			node_id=node_id.substring(0,2);
 	   Ext.require('sendEmailUL', function(){
   			Ext.create("sendEmailUL.sendEmail", {
   				sub_module:"7",
   				nModule:nModule,
   				a0100s:a0100,
   				a0101s:name,
   				z0301:z0301,
   				link_Id:link_id,
   				node_Id:node_id,
   				title:custom_name,
   				fuId: 'ZP0000002302',
   				function_str:function_str,
   				executionMethod:function(param){
   					Global.returnOperations(param);
   				}
   			});
   		});
	} else if(function_str=="rzRegister")// 入职登记
	{
	 	if(node_id=="")
	 	{
	 	   node_id=projectIds.substring(0,2);
	 	}else{
	 	   node_id=node_id.substring(0,2);
	 	}
		Global.functionName=function_str;
	     Global.linkId=link_id;
	     Global.nodeId=node_id;
	     Global.a0100No=a0100;
	     Global.nbase_str = nbases;
	     Global.c0102_str = c0102;
	     Global.name_str = name;
	     Global.rzRegister(a0100,names,nbase);
	}else if(function_str=="toStage"){// 转新阶段
	
		toStage(function_str,link_id,node_id,a0100,name,c0102,"toStage");
	}else if(function_str=="toTalents")// 转人才库
	 {
	   	 Ext.require('OperationLogUL', function(){
			Ext.create("OperationLogUL.operationLog", 
					{a0100s:a0100,link_id:link_id,status:node_id,position_id:z0301,function_str:function_str,
				fn:function(flag){
					Global.toTalents(a0100,nbase,name);
				}
			});
		});
	 }else if(function_str=="arrangement"){// 面试安排
	     Global.arrangement(a0100s,nbases,link_id,custom_name);
	  }else if(function_str=="uploadingResult"){// 上传背景调查文件
	 	 globalA0100 = a0100;
	 	 globalNbase = nbase;
	  	Global.uploadFile(a0100,nbase,node_id,link_id);
	  }else if(function_str=="invitationEvaluation"){// 邀请面试评价
	  	var map = new HashMap();
		map.put("nModule", '7');
	  	map.put("sub_module", '80');
	 	map.put("a0100",a0100);
	 	map.put("nbase",nbase);
	 	map.put("z0301",z0301);
	 	map.put("node_id",node_id);
	 	map.put("id","1007");
	 	Rpc( {
	 		functionId : 'ZP0000002400',
	 		success :invitationEvaluation
	 	}, map);
	  }else if(function_str=="forwardResume"){// 转发简历
	   	var map = new HashMap();
		map.put("nModule", '7'); //模块编号
	   	map.put("sub_module", '81');//子模块编号
	  	map.put("a0100",a0100);
	  	map.put("nbase",nbase);
	  	map.put("z0301",z0301);
	  	map.put("node_id",node_id);
	  	Rpc( {
	  		functionId : 'ZP0000002402',
	  		success :invitationEvaluation
	  	}, map);
	   }else if(Ext.Array.contains(function_operation,function_str)){// 当传入方法都不满足条件时进行此方法
	  	if(node_id=="")
	 	{
	 	   node_id=projectIds.substring(0,2);
	 	}else{
	 	   node_id=node_id.substring(0,2);
	 	}
	 	
		Global.updateOperation(function_str,link_id,node_id,a0100,name,c0102);
	}else{
		function_str = eval(function_str);
		var param = new HashMap();
		param.put("a0100", a0100);
		param.put("nbase",nbase);
		param.put("z0301",z0301);
		param.put("node_id",node_id);
		param.put("link_id",link_id);
		param.put("c0102",c0102);
		param.put("custom_name",custom_name);
     	function_str(param);
	}
}
/*******************************************************************************
 * 上传背景调查文件
 * 
 * @param {}
 *            nbase
 * @param {}
 *            link_id
 * @param {}
 *            node_id
 * @param {}
 *            a0100
 */
Global.uploadFile = function(a0100,nbase,node_id,link_id){
	var a0100s = a0100.substring(0,a0100.length-1).split(",");
	if(a0100s.length>1)
	{
    	Ext.showAlert("只允许选择一条记录上传面试评价记录！");return false;
	}
	var map = new HashMap();
	map.put("nbase",nbase);
	map.put("node_id",node_id);
	map.put("link_id",link_id);
	map.put("a0100",a0100);
	Rpc( {
		functionId : 'ZP0000002364',
		success : Global.searchFileSuccess
	}, map);
}
Global.searchFileSuccess = function(response){
	var result = Ext.decode(response.responseText);
	var node_id = result.nodeId;
	var list = result.list;
	var isDownload = result.download;
	var isShowOrEdit = result.del;
	var title = "";// 弹窗title名称
	if(node_id=="01"||node_id=="02")
		title = "上传简历筛选附件";
	else if(node_id=="03")
		title = "上传测评结果";
    else if(node_id=="05")
    	title = "上传面试评价记录";
    else if(node_id=="06")
    	title = "上传背景调查资料";
    else if(node_id=="07")
    	title = "上传录用审批附件";
    else if(node_id=="08")
    	title = "上传Offer附件";
    else if(node_id=="09")
    	title = "上传体检结果";
    else if(node_id=="10")
    	title = "上传入职附件";
	/**
	 * 调用上传文件插件
	 */
	/*
	 * Ext.widget("window",{ modal:true, title:title, id:'fileUp',
	 * region:'center', shadow:false, resizable:false, layout:'fit',
	 * buttonAlign: 'center', collapsible:false, titleCollapse:true,
	 * renderTo:Ext.getBody(), bodyStyle:'background-color:white', width:400,
	 * height:120, border:0, frame:true,
	 * floating:true,//当设置floating为true时x,y项才有效 draggable:true, html:'<div
	 * id="bb" style="margin-left:45px;margin-top:20px"></div>' }).show();
	 */
	
	if(list.length>0){// 已有上传文件
		// var jsonStr = Ext.encode(list);
		Ext.create("SYSF.FileUpLoad",{
			renderTo:Ext.getBody(),
			emptyText:"请输入文件路径或选择文件",
			upLoadType:2,
			isDownload:isDownload,//是否可以下载
			isShowOrEdit:isShowOrEdit,
			fileList:list,
			buttonText:'上传',
			success:Global.uploadSuccess,
			fileSizeLimit:'20MB',
			isTempFile:false,
            VfsModules:VfsModulesEnum.ZP,
            VfsFiletype:VfsFiletypeEnum.other,
            VfsCategory:VfsCategoryEnum.other,
			fileExt:"*.doc;*.docx;*.xlsx;*.xls;*.rar;*.zip;*.ppt;*.jpg;*.jpeg;*.png;*.bmp;*.txt;*.wps;*.pptx;*.pdf;",
			isDelete:true
		});    	
	}else{
		Ext.create("SYSF.FileUpLoad",{
			renderTo:Ext.getBody(),
			emptyText:"请输入文件路径或选择文件",
			upLoadType:2,
			isDownload:true,
			buttonText:'上传',
			success:Global.uploadSuccess,
			fileSizeLimit:'20MB',
			isTempFile:false,
            VfsModules:VfsModulesEnum.ZP,
            VfsFiletype:VfsFiletypeEnum.other,
            VfsCategory:VfsCategoryEnum.other,
			fileExt:"*.doc;*.docx;*.xlsx;*.xls;*.rar;*.zip;*.ppt;*.jpg;*.jpeg;*.png;*.bmp;*.txt;*.wps;*.pptx;*.pdf;",
			isDelete:true
		});
	}
}
Global.uploadSuccess = function(list){
		var linkid = Ext.getDom("linkId").value;
	 	var file_list = new Array();
		Ext.each(list,function(obj,index){
			file_list.push({"fileid":!!obj.fileid?obj.fileid:obj.path,"filename":obj.filename,"localname":obj.localname});
		});
		var map = new HashMap();
		map.put("linkid",linkid);
		map.put("a0100",globalA0100);
		map.put("nbase",globalNbase);
		map.put("file_list",file_list);
		
		Rpc( {
			functionId : 'ZP0000002363',
			async:false,
			success : function(response){
				var result = Ext.decode(response.responseText);
			}
		}, map);
	}
// 对面试人员简历进行操作
Global.updateOperation=function(function_str,link_id,node_id,a0100,name,c0102){
	if("toStage"==function_str||"rzRegister"==function_str){
		if("toStage"==function_str){//转阶段直接写日志
			var hashMap = new HashMap();
			hashMap.put("a0100", a0100);
			hashMap.put("node_id", node_id);
			hashMap.put("link_id", link_id);
			hashMap.put("z0301", Global.z0301);
			hashMap.put("description", Ext.getCmp('operationID').getValue());
			hashMap.put("function_str", function_str);
			hashMap.put("now_linkId", Ext.getDom("linkId").value);
			  Global.addOperationLog(hashMap);
		}
		 var map = new HashMap();
	     map.put("a0100",getEncodeStr(a0100));
	     map.put("link_id",getEncodeStr(link_id));
	     map.put("node_id",getEncodeStr(node_id));
	     map.put("function_str",getEncodeStr(function_str));
	     map.put("z0301",getEncodeStr(Global.z0301));
	     map.put("name",getEncodeStr(name));
	     map.put("c0102",getEncodeStr(c0102));
	     map.put("person",Global.person);
	     map.put("bususer",Global.bususer);
	     map.put("now_linkId", Ext.getDom("linkId").value);
	     Rpc( {
			functionId : 'ZP0000002302',
			success :Global.returnOperations
		}, map);
	}else{
		//非转阶段、非入职先写日志回调方法继续执行下一步操作
		Ext.require('OperationLogUL', function(){
			Ext.create("OperationLogUL.operationLog", 
					{a0100s:a0100,link_id:link_id,status:node_id,position_id:Global.z0301,function_str:function_str,
					fn:function(flag){
					     var map = new HashMap();
					     map.put("a0100",getEncodeStr(a0100));
					     map.put("link_id",getEncodeStr(link_id));
					     map.put("node_id",getEncodeStr(node_id));
					     map.put("function_str",getEncodeStr(function_str));
					     map.put("z0301",getEncodeStr(Global.z0301));
					     map.put("name",getEncodeStr(name));
					     map.put("c0102",getEncodeStr(c0102));
					     map.put("z0381",Ext.getDom("z0381").value);
					     Rpc( {
							functionId : 'ZP0000002302',
							success :Global.returnOperations
						}, map);
					}
				});
			});
	}
}
Global.index=0;
// 当前操作返回操作值
Global.returnOperations=function(outparamter){
	var result = "";
	if(outparamter.callback)
		result = outparamter;
	else
		result = Ext.decode(outparamter.responseText);
	var link_id=result.link_id;
	var node_id=result.node_id;
	var status_id=result.status;
	var a0100=result.a0100;
	var name=result.name;
	var c0102=result.c0102;
	//执行通过操作
	if("passChoice"==getDecodeStr(result.function_str)&&!outparamter.callback&&"1"!=result.skipFlag){
		toStage("toStage",getDecodeStr(link_id)
				,status_id,getDecodeStr(a0100)
				,getDecodeStr(name),getDecodeStr(c0102),"passChoice");
	}else if(Global.from!="resume"){
		document.recruitProcessForm.action="/recruitment/recruitprocess/recruitprocesslist.do?b_query=link&link_id="+link_id+"&node_id="+status_id;
		document.recruitProcessForm.submit();
	}else{
		var custom_name=result.custom_name;
		var link_name=result.link_name;
		var str = link_name+"&nbsp;(&nbsp;"+custom_name+"&nbsp;)";
		resume_me.setStatus(str);
		parametersInfo.link_id = result.link_id;
		parametersInfo.old_link_id = window.frames['ifra'].Ext.getDom("link_id").value;
		parametersInfo.resume_name = custom_name;
		resume_me.queryOperationList();
		window.frames['ifra'].Ext.getDom("link_id").value = result.link_id;
		window.frames['ifra'].Ext.getDom("nextLinkId").value=result.next_linkId;
		//在修改阶段时，状态默认为该阶段开始状态'??01'
		window.frames['ifra'].Ext.getDom("node_id").value=result.status;
		var map = new HashMap();
		map.put("a0100", result.a0100);
		map.put("z0301", result.z0301);
		map.put("select","select");
		resume_me.searchOperationLog(map);
	}
	var datastore = Ext.data.StoreManager.lookup('tablelist_dataStore');
	datastore.reload();
}
/*******************************************************************************
 * 邮件返回方法
 * 
 * @param {}
 *            params
 */
Global.returnFn=function(params)
{
	var param = params.split(",");
	var link_id=param[0];
	var node_id=param[1];
	document.recruitProcessForm.action="/recruitment/recruitprocess/recruitprocesslist.do?b_query=link&link_id="+link_id+"&node_id="+node_id;
	document.recruitProcessForm.submit();
}
/*******************************************************************************
 * 通过邮件返回方法
 * 
 * @param {}
 *            params
 */
Global.returnPassChoiceFn=function(params)
{
	var param = params.split("`");
	var fnName = param[0];
	var link_id = param[1];
	var node_id = param[2];
	var a0100 = param[3];
	var name = param[4];
	var c0102 = param[5];
	var passChoice = param[6];
	toStage(fnName,link_id,node_id,a0100,name,c0102,passChoice);
}
// 转入人才库
Global.toTalents=function(a0100,nbase,name){
	var a0100s=a0100.split(",");
	var nbases=nbase.split(",");
	var names = name.split(",");
	var array = new Array();
	for(var i=0; i<a0100s.length; i++){
		if(a0100s[i]=="")
			continue;
	    var param = new Array();
	    param[0] = a0100s[i];
	    param[1] = nbases[i];
	    param[2]=names[i];
	    array[i] = param;
	}
	var map = new HashMap();
	map.put("array", array);
	Rpc({functionId : 'ZP0000002104',success :Global.operateResult}, map);
}
Global.operateResult=function(params){
	
	var value = params.responseText;
	var outparamters = Ext.decode(value);
	if(outparamters.exitTrans!=undefined&&outparamters.exitTrans!=""){
		Ext.Msg.alert('提示信息',outparamters.res+"人已成功转入人才库!</br>"+outparamters.exitTrans+"在人才库中已经存在！");
	}else if(outparamters.result=='true'){
		Ext.Msg.alert('提示信息',getDecodeStr(outparamters.info));
	}
	var datastore = Ext.data.StoreManager.lookup('tablelist_dataStore');
	 datastore.reload();
}
// 返回按钮
Global.goBack=function(param){
	 var pageNum  = param.pageNum;
	 var searchStr = param.searchStr;
	 var pagesize = param.pagesize;
	 parent.window.location="/recruitment/position/position.do?b_query=link&pageNum="+pageNum+"&searchStr="+searchStr+"&pagesize="+pagesize;
}
// 转到面试安排界面
Global.arrangement=function(a0100s,nbases,link_id,custom_name){
    var z0381 = Ext.getDom("z0381").value;
    var nodeId = Ext.getDom("nodeId").value;
    var store = Ext.data.StoreManager.lookup('tablelist_dataStore');
    if(Global.from!="resume")
    	parent.window.location="/recruitment/recruitprocess/arrangement.do?b_arrangement=link&a0100s="+a0100s+"&nbases="+nbases+"&z0381="+z0381+"&z0301="+Global.z0301+"&link_id="+link_id+"&node_id="+nodeId+"&nextNum=0&page="+$URL.encode(store.pageSize+"`"+store.currentPage);
    else
    	parent.window.location="/recruitment/recruitprocess/arrangement.do?b_arrangement=link&a0100s="+a0100s+"&nbases="+nbases+"&z0381="+z0381+"&z0301="+Global.z0301+"&link_id="+link_id+"&node_id="+nodeId+"&nextNum=0&page="+$URL.encode(store.pageSize+"`"+store.currentPage)+"&flag=1&resume_flag="+$URL.encode(nodeId+"`"+custom_name)+"&resume_name="+$URL.encode(custom_name);
}
// 返回
Global.returnPos=function(pageNum,searchStr,pagesize){
     parent.window.location="/recruitment/position/position.do?b_query=link&pageNum="+pageNum+"&searchStr="+searchStr+"&pagesize="+pagesize;
}
// 入职登记
Global.rzRegister = function(a0100s,names,nbases){
    var map = new HashMap();
    map.put("a0100s",a0100s);
    map.put("z0301",Global.z0301);
    map.put("nbases",nbases);
    if(nbases =="" || nbases == null){
    	Ext.showAlert(PARAMETER_SETTING_ENTRY_PERSONNELLIBRARY);
    	return;
    }
    
    Rpc( {
		functionId : 'ZP0000002307',
		success :Global.returnRzRegister
	}, map);
}
Global.rzRegisterPanel = null;
// 入职登记回调函数
Global.returnRzRegister=function(outparamters){
	var value = outparamters.responseText;
	var param = Ext.decode(value);
	var rzColumn = Ext.decode(param.rzColumn);
	var rzRegister = "";
	var rzValue = Ext.decode(param.rzValue);
	var configs={
                prefix:"rzRegister",
                pagesize:20,
                editable:true,
                selectable:true,
                storedata:rzValue,
                tablecolumns:rzColumn,
                datafields:['a0100','a0101','z0321','z0325','job']
        };
	rzRegister=new BuildTableObj(configs);
	rzRegister.insertItem(Ext.widget("textarea",{
		id:'operationID',
		margin:'5 0 0 0',
		width:'100%',
		rows:3,
		emptyText:'请填写意见'
	}));
    var table = rzRegister.getMainPanel();
    Global.rzRegisterPanel = Ext.widget("window",{
    	   modal:true,
    	   title:'入职登记',
           layout:'fit',
           height:350,
           width:500,
           items:table,
           resizable:false,
           buttonAlign: 'center',
           buttons:[
                     {text:"确定",
                     id:"saveRZ",
                     handler:function(){Global.saveRz();}},
                     {text:"关闭",handler:function(){Global.rzRegisterPanel.close()}}
                   ]
    }).show();
}
// 保存入职信息
Global.saveRz = function(){
    var store = Ext.data.StoreManager.lookup('rzRegister_dataStore');
    var records = store.data.items;
    var info = new Array();
    for(var i=0;i<records.length;i++)
    {
    	var a0101 = records[i].get("a0101");
    	var z0321 = records[i].get("z0321").split("`")[0];
    	var z0325 = records[i].get("z0325").split("`")[0];
    	var job = records[i].get("job").split("`")[0];
    	if(z0321.length==0)
        {
            Ext.showAlert(a0101+"未选择单位！");return;
        }
    }
    Ext.each(records,function(records){
         info.push(records.data);
    });
	var hashMap = new HashMap();
    hashMap.put("a0100", Global.a0100No);
    hashMap.put("node_id", Global.nodeId);
    hashMap.put("link_id", Global.linkId);
    hashMap.put("z0301", Global.z0301);
    hashMap.put("description", Ext.getCmp('operationID').getValue());
    hashMap.put("function_str", Global.functionName);
    Global.addOperationLog(hashMap);
    var map = new HashMap();
    map.put("info",info);
    Global.rzRegisterPanel.close();
    Rpc({functionId : 'ZP0000002308',success :Global.returnRz}, map);
}
// 回调函数，当入职信息保存成功后再进行更改状态操作
Global.returnRz = function(){
    Global.updateOperation(Global.functionName,Global.linkId,Global.nodeId, Global.a0100No,Global.name_str,Global.c0102_str);return;
}

/** ***推荐职位***** */
Global.recommendOtherPosition = function(param){
	var a0100s = "";
	var pageDescFro = "";
	if(Global.from!="resume"){
		var record = Ext.getCmp("tablelist_tablePanel").getSelectionModel().getSelection();
		var store = Ext.data.StoreManager.lookup("tablelist_dataStore");
		 if(record.length == 0){
		        Ext.Msg.alert('提示信息','请选择操作数据');
		        return;
		    }
	    
	    var nbase = "";
	    a0100s = "{\"a0100\":[";
	    for(var i=0; i<record.length; i++){
	    	if(!nbase.length>0)
	    		nbase = record[i].data.nbase_e;
	    	a0100s+="{\"a0100\":\""+record[i].data.a0100_e+"\"," +
	    			"\"z0301\":\""+Global.z0301+"\"," +
					"\"a0101\":\""+record[i].data.a0101+"\"},";
	    }
	    if(a0100s.length>1)
	    	a0100s=a0100s.substring(0, a0100s.length-1)+"],\"nbase\":\""+nbase+"\"}";
	    pageDescFro = store.currentPage+"`"+store.pageSize+"`"+Ext.getDom('linkId').value+"`"+Ext.getDom('nodeId').value+"`"+Ext.getDom('z0381').value+"`"+Global.z0301;
	}else{
		var node_flag = window.frames['ifra'].document.getElementById("node_flag").value;
		if(node_flag=="1")
		{
			Ext.Msg.alert('提示信息',"当前人员处于已终止或已入职状态，不允许推荐职位！");return;
		}
	    a0100s+="{\"a0100\":[{\"a0100\":\""+param.a0100+"\",\"z0301\":\"\",\"a0101\":\"\"}],\"nbase\":\""+param.nbase+"\"}";
	    pageDescFro = param.a0100+"`"+param.nbase+"`"+param.c0102+"`"+param.z0301+"`"+window.frames['ifra'].Ext.getDom("page").value.replace("`",",")+"`"+param.link_id+"`"+Ext.getDom('z0381').value+"`"+param.node_id;
	}
    var map = new HashMap();
    map.put("a0100s",a0100s);
    map.put("pageDescFro",pageDescFro);
    Rpc( {
		functionId : 'ZP0000002082',
		success :Global.recommendPosition
	}, map);	
	
	
}
/*******************************************************************************
 * 推荐职位
 * 
 * @param {}
 *            outparamters
 */
Global.recommendPosition = function(outparamters){
	var param = Ext.decode(outparamters.responseText);
	var a0100s = param.a0100s;
	var pageDescFro =$URL.encode(param.pageDescFro);
	
	if(a0100s==""){
		 Ext.Msg.alert('提示信息','选择的人员处于已终止或已入职状态，<br/>不允许继续推荐！');
	        return;
	}
	var ele = parent.Ext.getDom("border");
    var divel = document.createElement("div");
    ele.appendChild(divel);
    var fromModule = "process";
    if(Global.from=="resume")
    	fromModule = $URL.encode("process`resumeInfo");
    
    if(Global.from!="resume"){
	    divel.innerHTML = "<form id='recommendForm' method='post' action='/recruitment/position/position.do?b_query=link&pageNum=1&pagesize=20&pageDescFro="+pageDescFro+"&from="+fromModule+"' >" +
	    		"<input type='hidden' name='a0100s' value='"+a0100s+"' /></form>";
	    parent.recommendForm.submit();
    }else{
	    Ext.create('Ext.window.Window', {
	    	id:'recommendWinID',
	        border:false,
	        closable : false,
	        maximized : true,
	        header: false,
	        html:"<iframe id='recommendForm' width='100%' frameborder=0 height='100%' src='/recruitment/position/position.do?b_query=link&pageNum=1"
	        	+"&pagesize=20&pageDescFro="+pageDescFro+"&from="+fromModule+"&a0100s="+$URL.encode(a0100s)+"'></iframe>"
	    }).show();
	    
    }
}
/** ***推荐职位***** */
function getUNParentid(itemid)
{
    var records = Ext.getCmp("rzRegister_tablePanel").getSelectionModel().getSelection();
    records[0].set("z0325","`");
    records[0].set("job","`");
    return "";
}
// 部门选择
function getUMParentid(itemid){
	var records = Ext.getCmp("rzRegister_tablePanel").getSelectionModel().getSelection();
	var parentid = records[0].data.z0321.split('`')[0];
//	records[0].set("job","`");
	if(parentid!="")
	{
    	return parentid;
	}
}

function setJobNull(){
	var records = Ext.getCmp("rzRegister_tablePanel").getSelectionModel().getSelection();
	records[0].set("job","`");
}

function setJobNull(){
	var records = Ext.getCmp("rzRegister_tablePanel").getSelectionModel().getSelection();
	records[0].set("job","`");
}
// 职位选择
function getKParentid(itemid){
    var records = Ext.getCmp("rzRegister_tablePanel").getSelectionModel().getSelection();
    var parentidUM = records[0].data.z0325.split('`')[0];
    var parentidUN = records[0].data.z0321.split('`')[0];// alert(parentidUM+"\n"+parentidUN);
    if(parentidUM!="")
    {
        return parentidUM;
    }else if(parentidUN!="")
    {
        return parentidUN;
    }
}
// ajax进行动态查询单位部门
Global.setValue=function(itemId,value){
	var codeitemId = value;
    var map = new HashMap();
    map.put("codeitemId",codeitemId);
    Rpc( {
		functionId : 'ZP0000002309',
		success :Global.returnValue
	}, map);
}
// 通过传回的值进行动态填充
Global.returnValue=function(outparamters){
	
	var value = outparamters.responseText;
	var param = Ext.decode(value);
    var records = Ext.getCmp("rzRegister_tablePanel").getSelectionModel().getSelection();
    var codeSet = param.getCodeSet;
    if(codeSet=="UM")
    {
        records[0].set("z0321",param.codeUNName);
    }else if(codeSet=="@K")
    {
    	if(param.codeSet=="UM")
    	{
           records[0].set("z0325",param.codeUMName);
    	}
    	records[0].set("z0321",param.codeUNName);
    }
}

Global.feedBackShow = function (value,c,record){
	if("0" == record.get('description'))
		return value;
	
	var a0100 = record.get('a0100_e');
	var nbase = record.get('nbase_e');
	return "<img id='img"+ a0100 +"' width='12px' height='12px' src='/module/recruitment/image/feedback.png' onmouseover='Global.showFeedBack(\""+a0100+"\", \""+nbase+"\")'/>" + value;
}

Global.showFeedBack = function (a0100, nbase){
	Ext.require('EHR.ToolTipUL.ToolTip.feedback', function(){
		Ext.create("EHR.ToolTipUL.ToolTip.feedback",{nbase:nbase,a0100:a0100,zp_pos_id:Global.z0301,tipId:'img'+a0100,fn:function(obj){
			if(navigator.userAgent.indexOf("Edge") > -1){//edge浏览器特殊处理
				ev = event || window.event;
				obj.mouseOffset=[ev.clientX + document.body.scrollLeft - document.body.clientLeft,ev.clientY + document.body.scrollTop - document.body.clientTop];
				obj.show();
			}
		}});
	});
}
//导出简历PDF
Global.exportResumePDF = function () {
	var record = Ext.getCmp("tablelist_tablePanel").getSelectionModel().getSelection();
	if (record.length == 0) {
		Ext.Msg.alert('提示信息', '请选择操作数据！');
		return;
	}
	
	Ext.MessageBox.wait(PROMPT_INFORMATION, RESUME_EXPORT_WORD);
	var nbase="", a0100="", a0101s="";
	for (var i = 0; i < record.length; i++) {
		nbase = record[i].data.nbase_e;
		a0100 += record[i].data.a0100_e + ",";
		a0101s += record[i].data.a0101 + ",";
	}
	
	var map = new HashMap();
	map.put("a0100s", a0100);
	map.put("nbase", nbase);
	map.put("a0101s", a0101s);
	map.put("z0301", Global.z0301);
	Rpc( {
		functionId : 'ZP0000002107',
		success : Global.exportResumeZip
	}, map);
}

//导出简历WORD
Global.exportResumeWORD = function () {
	var record = Ext.getCmp("tablelist_tablePanel").getSelectionModel().getSelection();
	if (record.length == 0) {
		Ext.Msg.alert('提示信息', '请选择操作数据！');
		return;
	}
	
	Ext.MessageBox.wait(PROMPT_INFORMATION, RESUME_EXPORT_PDF);
	var nbase="", a0100="", a0101s="";
	for (var i = 0; i < record.length; i++) {
		nbase = record[i].data.nbase_e;
		a0100 += record[i].data.a0100_e + ",";
		a0101s += record[i].data.a0101 + ",";
	}
	
	var map = new HashMap();
	map.put("a0100s", a0100);
	map.put("nbase", nbase);
	map.put("a0101s", a0101s);
	map.put("z0301", Global.z0301);
	map.put("filetype", "word");
	Rpc( {
		functionId : 'ZP0000002107',
		success : Global.exportResumeZip
	}, map);
}

Global.exportResumeZip = function (param){
	var map = Ext.decode(param.responseText);
	Ext.MessageBox.close();
	if(map.succeed){
		var infor = map.infor;
		if("ok" == infor) {
			var zipName = map.zipname;
			window.open("/servlet/vfsservlet?fromjavafolder=true&fileid="+zipName,"_blank");
		} else {
			Ext.showAlert(infor);
		}
	}else{
		if(map.message){
			Ext.showAlert(map.message);
		}
	}
}
//打印简历
Global.printAX = function(){
	var record = Ext.getCmp("tablelist_tablePanel").getSelectionModel().getSelection();
	if(!Ext.isIE){
		Ext.Msg.alert('提示信息', '该功能仅支持IE浏览器！');
		return;
	}
	if (record.length == 0) {
		Ext.Msg.alert('提示信息', '请选择操作数据！');
		return;
	}
	var pers=new Array();
	var a0100s="";
	for (var i = 0; i < record.length; i++) {
		var nbase = record[i].data.nbase_e;
		var a0100 = record[i].data.a0100_e;
		a0100s = nbase+"`"+a0100;
		pers[i]=a0100s;
	}
	
	var map = new HashMap();
	map.put("inforkind", "1");
	map.put("z0301", Global.z0301);
	map.put("pers",pers);
	Rpc( {
		functionId : 'ZP0000002108',
		success : Global.showPrint
	}, map);
}
Global.showPrint = function (outparamters)
{
	var param = Ext.decode(outparamters.responseText);
	if(!param.succeed){
		Ext.Msg.alert('提示信息', param.message);
		return;
	}
	var personlist=param.personlist;
	var nbase = param.nbase;
	var cardid = param.cardid;
	var obj = document.getElementById('CardPreview1');
	if(obj==null)
	{
		Ext.Msg.alert('提示信息', '没有下载打印控件，请设置IE重新下载！');
		return;
	}
	try {
		   Global.initCard();
		   obj.SetCardID(cardid);
		   obj.SetDataFlag("<SUPER_USER>1</SUPER_USER>");
		   obj.SetNBASE(nbase);
		   obj.ClearObjs();   
		   if(personlist!=null&&personlist.length>0)
		   {
		     for(var i=0;i<personlist.length;i++)
		     {
		       obj.AddObjId(personlist[i].dataValue);
		     }
		   }
		   try { obj.SetJSessionId(AxManager.getJSessionId()); } catch(err) {}
	   		obj.ShowCardModal();
	}catch (e) {

	}
}
//添加日志
Global.addOperationLog = function(map){
    Rpc({asynchronous:true,functionId : 'ZP0000002004'},map);
}

Global.exportWin = function (){
	var win = Ext.getCmp("exportWinId");
	if(win)
		win.close();
	
	var checkBox = new Ext.form.Checkbox( {
		id : "historyInfo",
		name : "checkbox",
		width: 200,
		labelSeparator : '',
		padding : '0 5 0 16',
		boxLabel : '包含子集历史记录',
		inputValue : 1
	});
	
	var attachment = new Ext.form.Checkbox( {
		id : "attachment",
		name : "checkbox",
		width: 200,
		labelSeparator : '',
		padding : '0 5 0 16',
		boxLabel : '包含简历附件',
		inputValue : 1
	});
	
	var registration = new Ext.form.Checkbox( {
		id : "registration",
		name : "checkbox",
		width: 200,
		labelSeparator : '',
		padding : '0 5 0 16',
		boxLabel : '包含简历登记表',
		inputValue : 1
	});
	
	win = Ext.create('Ext.window.Window', {
	    title: '导出Excel',
	    id:'exportWinId',
	    height: 150,
	    width: 300,
	    layout: 'vbox',
	    modal: true,
	    items:[checkBox,attachment,registration],
	    buttonAlign: 'center',
	    buttons:[{
	    	text: '确定',
	    	handler: function() {
	    		Ext.MessageBox.wait("", "正在导出请稍候……");
	    		var exportFlag = '1';
	    		
	    		var historyFlag = "0";
	    		var historyBox = Ext.getCmp("historyInfo");
	    		
	    		if(historyBox.checked)
	    			historyFlag = "1";
	    		
	    		var attachmentFlag = "0";
	    		var attachmentBox = Ext.getCmp("attachment");
	    		if(attachmentBox.checked)
	    			attachmentFlag = "1";
	    		
	    		var registration = "0";
	    		var registrationBox = Ext.getCmp("registration");
	    		if(registrationBox.checked)
	    			registration = "1";
	    		
	    		var selectstore = Ext.getCmp("tablelist_tablePanel").getSelectionModel().getSelection();
	    		var a0100_es = "";
	    		Ext.each(selectstore,function(record,index){
	    			a0100_es+=record.data.a0100_e+",";
	    		});
	    		var map = new HashMap();
	    		map.put("from", "process")
	    		map.put("exportFlag", exportFlag);
	    		map.put("historyFlag", historyFlag);
	    		map.put("attachmentFlag", attachmentFlag);
	    		map.put("registration", registration);
	    		map.put("a0100_es",a0100_es);
	    		Rpc( {
	    			functionId : 'ZP0000002109',
	    			success : Global.exportSucc
	    		}, map);
	    		
	    		win.close();
		    }
	    },{ 
	    	text: '取消',
	    	handler: function() {
	    		win.close();
	    	}
	    }]
	});
	
	win.show();
}

Global.exportSucc = function (response){
	var value = response.responseText;
	var map	 = Ext.decode(value);
	Ext.MessageBox.close();	
	if(map.succeed){
		if("false" == map.flag)
			return;
		
		var fieldName = getDecodeStr(map.fileName);
		window.location.target="_blank";
		window.location.href = "/servlet/vfsservlet?fromjavafolder=true&fileid="+fieldName;
	}
}
Global.editable = function(){
	return false;
}
/*
 * 推荐排名为0的时候显示""
 */
Global.showNum = function(rank_num){
	if(rank_num == 999999)
		rank_num = "";
	return rank_num;
}
/*
 * 保存推荐排名
 */
Global.saveRank_num = function(){
	//获取有修改信息的store
	var editstore = Ext.getCmp("tablelist_tablePanel").getStore().getModifiedRecords();
	var a0100_es = "";
	var rank_nums = "";
	if(editstore.length<0)
		return;
	Ext.each(editstore,function(record,index){
		a0100_es+=record.data.a0100_e+",";
		rank_nums+=(record.data.rank_num?record.data.rank_num:"0")+",";
	});
	var map = new HashMap();
	map.put("z0301",Global.z0301);
    map.put("a0100_es",a0100_es);
    map.put("rank_nums",rank_nums);
    Rpc({functionId : 'ZP0000002131',success :Global.loadList}, map);
}

//menu菜单-转人才库
Global.toTalentFunc = function(){
	var map = new Object();
	map.functions="toTalents";
	map.custom_name="转人才库";
	Global.operation(map);
}
//menu菜单-邀请评价
Global.invitationEvaluation = function(){
	var map = new Object();
	map.functions="invitationEvaluation";
	map.custom_name="邀请评价";
	Global.operation(map);
}
//menu菜单-推荐职位
Global.recommendOtherPos = function(){
	var map = new Object();
	map.functions="Global.recommendOtherPosition";
	map.custom_name="推荐职位";
	Global.operation(map);
}

Global.openPicker = function(isSelfUser){
	var deprecate = isSelfUser?Global.person:Global.bususer;
	var picker = new PersonPicker({
		multiple: true,
		isSelfUser:isSelfUser,
		isPrivExpression:false,//是否启用人员范围（含高级条件）
		validateSsLOGIN:true,
		deprecate: deprecate,
		callback:function(c){
			Global.insertObj(c,isSelfUser);
		}
	}, this);
	picker.open();
}

Global.insertObj = function(c,isSelfUser){//新增接收人
	for(var i=0;i<c.length;i++){
		var el = c[i];
		if(!isSelfUser)
			Global.bususer.push(el.id);
		else
			Global.person.push(el.id);
		var elem = Ext.getDom("personArea");
		var obj = document.createElement("div");
		obj.className="hj-nmd-dl";
		obj.onmouseover=function(){this.getElementsByTagName('img')[0].style.visibility=''}
		obj.onmouseleave=function(){this.getElementsByTagName('img')[0].style.visibility='hidden'}
		var html='<img class="newDeletePic" id="'+el.id+'" onclick="Global.deleteObj(this)" style="width: 14px; height: 14px;visibility:hidden;" src="/workplan/image/remove.png" />';
		obj.innerHTML=html+'<dl><dt title="'+el.name+'"><img class="img-circle" src="'+el.photo+'" /></dt><dd class="text-ellipsis">'+el.name+'</dd></dl>';
		elem.appendChild(obj);
	}
}
Global.deleteObj = function(el){//删除接收人
	Ext.Array.remove(Global.person,el.id);
	Ext.Array.remove(Global.bususer,el.id);
	var obj = el.parentNode;
	obj.parentNode.removeChild(obj);
}
Global.showPerson = function(linkId){
	Global.person = [];
	var parentNode = Ext.getDom("personArea");
	var childs = Ext.getDom("personArea").childNodes;
	for(var i = childs.length;i>0; i--){
		parentNode.removeChild(childs[0]);
	}
	var map = new HashMap();
	map.put("z0301",Global.z0301);
	map.put("linkId",linkId);
    Rpc({functionId : 'ZP0000002133',success :function(response){
    	var value = response.responseText;
    	var map	 = Ext.decode(value);
    	Global.insertObj(map.defPerson,true);
    	Global.insertObj(map.busUsers,false);
    }}, map);
}