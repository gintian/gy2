var Global = new Object();
Global.positionPlan =new Array(3);
Global.positionPlan = ["0","0","1"];
Global.publishWithApprove = "false";



Global.elemForClick=null;

Global.pageLode = function(){
	location=location;
}

var positionid='';
//方案查询
//type 代表的是在数组Global.positionPlan 里的位置 而strobj代表的是在数组哪个位置的值 
Global.tosearch=function(strobj,type,name){
	var elem = document.getElementsByName(name);
	var elemT = document.getElementById(name+strobj);
	var config = tableObj.getTableConfig();
	var hashvo=new HashMap();
//	if(Global.positionPlan[type]!=strobj){ 
	Global.positionPlan[type]=strobj;
	if(document.getElementById("all"))
		document.getElementById("all").style.textDecoration="none";
	
	//增加操作标识，当前按钮是否被按过
	var flag = true;
    //如果当前按钮再次被点，应清除下划线，并清除strobj
	if(elemT && elemT.style.textDecoration=="underline" && type != 2){
		flag = false;
		elemT.style.textDecoration="none";
		Global.positionPlan[type]="0";
	}
	for ( var i = 1; i < elem.length+1; i++) {
		if(elemT==elem[i-1]&&flag){
			elem[i-1].style.textDecoration="underline";
			elem[i-1].style.color = "green";
			elem[i-1].disabled=true;
			Global.elemForClick=elem[i-1];
		}else{
			elem[i-1].style.textDecoration="none";
			elem[i-1].style.color = "#1b4a98";
		}
	}
	hashvo.put("queryArray",Global.positionPlan);
    hashvo.put("tablekey",config.tablekey);
    Rpc({functionId:'ZP0000002071',async:false,success:Global.toLoad},hashvo);
};
//回调函数
Global.toLoad = function(outparamters){
	var result = Ext.decode(outparamters.responseText);
	if(result)
		publishWithApprove =result.publishWithApprove;
	if(Global.elemForClick!=null)
		Global.elemForClick.disabled=false;
	var store = Ext.data.StoreManager.lookup('tableObj_dataStore');
	//store.currentPage=1;
	store.reload();
	store.loadPage(1);
};

Global.toDisable= function(even,html){
	even.disabled=true;
	Ext.create('Ext.window.Window', {
    	id:'recommendWinID',
        border:false,
        closable : false,
        maximized : true,
        header: false,
        html:"<iframe id='recommendForm' width='100%' frameborder=0 height='100%' src='"+html+"'></iframe>"
    }).show();
};

Global.reload = function(){
	Ext.data.StoreManager.lookup('tableObj_dataStore').load();
}


//用来给职位列生成链接
Global.toPositionDetail = function(value,metaData,Record){
	var searchStr = Global.positionPlan.join(",");
	var z0301 = Record.data.z0301_e;
	var z0319str = Record.data.z0319;
	var z0319="";
	if(z0319str!=null){
		z0319=z0319str.substring(0, 2);
	}
	var z0381 = Record.data.z0381_e;
	var store = Ext.data.StoreManager.lookup('tableObj_dataStore');
	var html = "/recruitment/position/position.do?b_search=link&z0301="+z0301+"&z0381="+z0381+"&pageNum="+store.currentPage+"&searchStr="+searchStr+"&pagesize="+store.pageSize+"&sign=1&from=position";
	if(from=="")
		return "<a onclick='Global.toDisable(this,\""+html+"\")' href='javascript:void(0);' >"+value.replace(/</g,"&lt;")+"</a>";
	else
		return value.replace(/</g,"&lt;");
	
};

//显示新简历
Global.showNewResume = function(value,metaData,Record){
	var z0301 = Record.data.z0301_e;
	value = !value?0:value;
	var html1="/recruitment/resumecenter/searchresumecenter.do?b_search=link&pagesize=20&current=1&from=resumeCenter&zp_pos_id="+z0301+"&schemeValues=0,1,0";
	hml1 = "<a onclick='Global.toDisable(this,\""+html1+"\")' href='javascript:void(0);' >"+value+"</a>";
	return hml1;
};
//显示所有简历
Global.showAllResume = function(value,metaData,Record){
	var z0301 = Record.data.z0301_e;
	value = !value?0:value;
	var html2="/recruitment/resumecenter/searchresumecenter.do?b_search=link&pagesize=20&current=1&from=resumeCenter&zp_pos_id="+z0301+"&schemeValues=0,0,0";
	hml2="<a onclick='Global.toDisable(this,\""+html2+"\")' href='javascript:void(0);' >"+value+"</a>";
	return hml2;
};


//跳转候选人
Global.toRecruitprocess=function(value,metaData,Record){
	var searchStr = Global.positionPlan.join(",");
	var store = Ext.data.StoreManager.lookup('tableObj_dataStore');
	var z0301 = Record.data.z0301_e;
	var z0319str = Record.data.z0319;
	var z0319="";
	if(z0319str!=null){
		z0319=z0319str.substring(0, 2);
	}

	var z0381 = Record.data.z0381_e;
	var html = "/recruitment/position/position.do?b_search=link&z0301="+z0301+"&z0381="+z0381+"&pageNum="+store.currentPage+"&searchStr="+searchStr+"&pagesize="+store.pageSize+"&sign=2&link_id=&node_id=&from=position";
	return "<a onclick='Global.toDisable(this,\""+html+"\")' href='javascript:void(0);' >"+value+"</a>";
};

//新增
Global.insertPosition = function(param){
	Ext.getCmp(param.targetid).setDisabled(true);
    window.location.href='/recruitment/position/position.do?b_toadd=link';
};
//导入职位
Global.importData = function(){	
	Ext.getBody().mask();
    var win=new Ext.create('Ext.window.Window', {
        title: '导入职位',
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
                        var succeed = false;//下载程序是否执行成功                
                        var map = new HashMap();
                         map.put("id","1");                                       
                            Rpc({functionId:'ZP0000002088',timeout:10000000,async:true,success:function(form,action){
                                var result = Ext.decode(form.responseText);
                                flag = '1';//1表示下载程序执行完成
                                succeed=result.succeed;
                                if(succeed){
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
                                        if(succeed){
                                            window.location.target="_blank";
                                            window.location.href = "/servlet/vfsservlet?fromjavafolder=true&fileid="+outName;
                                        }else{
                                            Ext.showAlert("导出失败！错误信息："+result.message) 
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
                                    map.put("flag","1");                              
                                    Rpc({functionId:'ZP0000002087',async:true,success:function(response,action){
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
                                                map.put("flag", "2");
                                                Rpc({
                                                    functionId: 'ZP0000002087',
                                                    async: false,
                                                    success: function(response, action) {
                                                        msgBoxss.close();
                                                        var result = Ext.decode(response.responseText);
                                                        succNumber = result.succNumber;
                                                        Ext.Msg.alert("提示信息", succNumber,function(){ 
                                                            Ext.getCmp('importDataExcelId').close();
                                                          });
                                                        var store = Ext.data.StoreManager.lookup('tableObj_dataStore');
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
                                        
                                        gridpanel.getView().on('render', function(view) {
                                            view.tip = Ext.create('Ext.tip.ToolTip', {
                                                // 所有的目标元素
                                                target: view.el,
                                                // 每个网格行导致其自己单独的显示和隐藏。
                                                delegate: view.itemSelector,
                                                // 在行上移动不能隐藏提示框
                                                trackMouse: true,
                                                // 立即呈现，tip.body可参照首秀前。
                                                renderTo: Ext.getBody(),
                                                listeners: {
                                                    // 当元素被显示时动态改变内容.
                                                	 beforeshow: function updateTipBody(tip) {
 											            var div = tip.triggerElement.childNodes[0];
 											            var title = "";
 											            if (Ext.isEmpty(div))
 											            	return false;
 											        	    
											       		var havea = div.getElementsByTagName("a");
											            if(havea != null && havea.length > 0){
											            	title = havea[0].innerHTML;
											            } else 
											            	title = div.innerHTML;
											       		
											       		title = trimStr(title);
											       		if(Ext.isEmpty(title))
											       			return false;
											       		
											       		tip.update("<div style='WORD-BREAK:break-all;'>"+title+"</div>");
 											        }
                                                }
                                            });
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
                                             var store = Ext.data.StoreManager.lookup('tableObj_dataStore');
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


//快速查询
Global.fastSearch = function(){
	var elem = Ext.getCmp('boxtext');
	var config = tableObj.getTableConfig();
	var hashvo=new HashMap();
	hashvo.put("queryStr",elem.getValue());
	hashvo.put("queryArray",Global.positionPlan);
	hashvo.put("tablekey",config.tablekey);
	hashvo.put("from",from);
	 Rpc({functionId:'ZP0000002071',async:false,success:Global.toLoad2},hashvo);
};

//回调函数
Global.toLoad2 = function(outparamters){
	var store = Ext.data.StoreManager.lookup('tableObj_dataStore');
	store.currentPage=1;
	store.load();
};

//退回
Global.returnPosition = function(pram,record){
    if(record.length<=0){
        Ext.showAlert(POS_RETURN_HINT);
        return;
    }
    Ext.Msg.confirm(PROMPT_INFORMATION,POS_RETURN_WHETHER,function(btn){ 
    	// 确认触发，继续执行后续逻辑。 
    	if(btn=="yes"){ 
            Global.functionOfPosition(pram,record,"return","退回");
        } 
    });
    
};

//批准
Global.approvePosition = function(pram,record){
    if(record.length<=0){
        Ext.showAlert(POS_APPROVE_HINT);
        return;
    }
    Ext.Msg.confirm(PROMPT_INFORMATION,POS_APPROVE_WHETHER,function(btn){ 
        // 确认触发，继续执行后续逻辑。 
        if(btn=="yes"){ 
            Global.functionOfPosition(pram,record,"approve","批准");
        } 
    });
    
};

//撤回
Global.revokePosition = function(pram,record){
    if(record.length<=0){
        Ext.showAlert(POS_REVOKE_HINT);
        return;
    }
    Ext.Msg.confirm(PROMPT_INFORMATION,POS_REVOKE_WHETHER,function(btn){ 
    	 // 确认触发，继续执行后续逻辑。 
    	if(btn=="yes"){ 
            Global.functionOfPosition(pram,record,"revoke","报批");
        } 
    });
    
};

//报批
Global.reportPosition = function(pram,record){
    if(record.length<=0){
        Ext.showAlert(POS_REPORT_HINT);
        return;
    }
    Ext.Msg.confirm(PROMPT_INFORMATION,POS_REPORT_WHETHER,function(btn){ 
    	// 确认触发，继续执行后续逻辑。 
    	if(btn=="yes"){ 
            Global.functionOfPosition(pram,record,"report","报批");
        } 
    });
    
};

//删除
Global.deletePosition = function(pram,record){
	if(record.length<=0){
		Ext.showAlert("请选择需要进行业务操作的数据！");
		return;
	}
	Ext.Msg.confirm("提示信息","确认要删除职位吗？",function(btn){ 
		if(btn=="yes"){ 
		// 确认触发，继续执行后续逻辑。 
			Global.functionOfPosition(pram,record,"delete","删除职位");
		} 
	});
	
};
//发布
Global.publishPosition= function(pram,record){
	if(record.length<=0){
		Ext.showAlert("请选择需要进行业务操作的数据！");
		return;
	}
	Ext.Msg.confirm("提示信息","确认要发布职位吗？",function(btn){ 
		if(btn=="yes"){ 
		// 确认触发，继续执行后续逻辑。 
			Global.functionOfPosition(pram,record,"publish","发布职位");
		} 
	});
};

//暂停
Global.stopPosition= function(pram,record){
	if(record.length<=0){
		Ext.showAlert("请选择需要进行业务操作的数据！");
		return;
	}
	Ext.Msg.confirm("提示信息","确认要暂停职位吗？",function(btn){ 
		if(btn=="yes"){ 
		// 确认触发，继续执行后续逻辑。 
			Global.functionOfPosition(pram,record,"stop");
		} 
	}); 
	
	

};

//结束
Global.toEndPosition= function(pram,record){
	if(Ext.isEmpty(record))
		record = Ext.getCmp("tableObj_tablePanel").getSelectionModel().getSelection();
	if(record.length<=0){
		Ext.showAlert("请选择需要进行业务操作的数据！");
		return;
	}
	Ext.Msg.confirm("提示信息","确认要结束职位吗？",function(btn){ 
		if(btn=="yes"){ 
		// 确认触发，继续执行后续逻辑。 
			Global.functionOfPosition(pram,record,"end");
		} 
	});
};

//  发布和暂停还有结束 只用改一个状态所以可以走一个交易类
Global.functionOfPosition =function(pram,record,act){
	var hashvo=new ParameterSet();
	var z0301s = "";
	for ( var int = 0; int < record.length; int++) {
		var temp =record[int].data;
		z0301s+=temp.z0301_e+",";
		var z0319 = temp.z0319.substring(0,2);
		if(act=="publish"){
			if(z0319=="04"){
				Ext.MessageBox.alert(PROMPT_INFORMATION,temp.position.replace("`"," - ")+"<br/>"+IS_PUBLISH_STATE);
				return;
			}
			if(publishWithApprove != "true"){
				if(z0319!="03" && z0319!="09" ){
					Ext.MessageBox.alert(PROMPT_INFORMATION,temp.position.replace("`"," - ")+"<br/>"+STATE_CANNOT_PUBLISH);
					return;
				}
			}else{
				if(z0319!="03" && z0319!="09" && z0319!="02" ){
					Ext.MessageBox.alert(PROMPT_INFORMATION,temp.position.replace("`"," - ")+"<br/>"+STATE_CANNOT_PUBLISHWITHAPPROVE);
					return;
				}
			}
			
		}else if(act=="stop"){
			if(z0319=="09"){
				Ext.showAlert(temp.position.replace("`"," - ")+"<br/>"+IS_STOP_STATE);
				return;
			}
			
			if(z0319!="04"){
				Ext.showAlert(temp.position.replace("`"," - ")+"<br/>"+STATE_CANNOT_STOP);
				return;
			}
			
		}else if(act=="end"){
			if(z0319=="06"){
				Ext.showAlert(temp.position.replace("`"," - ")+"<br/>"+IS_END_STATE);
				return;
			}
			
			if(z0319!="04"){
				Ext.showAlert(temp.position.replace("`"," - ")+"<br/>"+STATE_CANNOT_END);
				return;
			}
			
		}else if(act=="delete"){
			if(z0319=="04" || z0319=="03" || z0319=="02"){
				Ext.showAlert(CANNOT_DELETE_POSITION);
				return;
			}
		}else if(act=="report"){
            if(z0319!="01" && z0319!="07"){
                Ext.showAlert(temp.position.replace("`"," - ")+"<br/>"+STATE_CANNOT_REPORT);
                return;
            }
        }else if(act=="revoke"){
            if(z0319!="02"){
                Ext.showAlert(temp.position.replace("`"," - ")+"<br/>"+STATE_CANNOT_REVOKE);
                return;
            }
        }else if(act=="approve"){
            if(z0319!="02" && z0319!="07"){
                Ext.showAlert(temp.position.replace("`"," - ")+"<br/>"+STATE_CANNOT_APPROVE);
                return;
            }
        }else if(act=="return"){
            if(z0319!="02" && z0319!="03"){
                Ext.showAlert(temp.position.replace("`"," - ")+"<br/>"+STATE_CANNOT_RETURN);
                return;
            }
        }
	}
	
	if (act == "return") {
	    var win = Ext.create("Ext.window.Window", {
	        id: "myWin",
	        title: "退回意见",
	        width: 300,
	        height: 300,
	        modal : true,
	        buttonAlign :"center",
	        layout: "fit",
	        items: [{
	            xtype: 'textareafield',
	            grow: true,
	            id: 'message',
	            anchor: '100%'
	        }],

	        buttons: [{
	            xtype: "button",
	            text: "确定",
	            handler: function() {
	                var value = Ext.getCmp("message").value
	                if (value.replace(/(^\s*)|(\s*$)/g, "") == "") {
	                    Ext.Msg.alert('提示', Fill_RETURN_OPINION);
	                    return;
	                }
	                var hashvo=new HashMap();
	                hashvo.put("opinion", value);
	                hashvo.put("act", act);
	                hashvo.put("z0301s", z0301s);
	                Rpc({functionId:'ZP0000002074',async:false,success:Global.toReload},hashvo);
	                this.up("window").close();
	            }
	        },
	        {
	            xtype: "button",
	            text: "取消",
	            handler: function() {
	                this.up("window").close();
	            }
	        }]
	    });
	    win.show();
	}else{
    	    hashvo.setValue("act",act);
    	    hashvo.setValue("z0301s", z0301s);
    	    if(act=="delete")
    	        var request=new Request({asynchronous:false,onSuccess:Global.positionExamineeInfo ,functionId:'ZP0000002084'},hashvo); 
    	    else
    	        var request=new Request({asynchronous:false,onSuccess:Global.toReload ,functionId:'ZP0000002074'},hashvo); 
	}
};

//查询职位下考生后的返回信息
Global.positionExamineeInfo = function(outparamters){
	var msg = outparamters.getValue("msg");
	var hashvo=new ParameterSet();
	var z0301s = outparamters.getValue("z0301s");
	hashvo.setValue("z0301s", z0301s);
	if(msg!="")
	{
		Ext.showAlert(msg);
		/*Ext.Msg.confirm('提示信息', msg, function(btn, text){
		if (btn == 'yes'){
				var request=new Request({asynchronous:false,onSuccess:Global.toLoad ,functionId:'ZP0000002073'},hashvo); 
		    }
		});*/
	}else{
		var request=new Request({asynchronous:false,onSuccess:Global.toLoad ,functionId:'ZP0000002073'},hashvo); 
	}
}

Global.toReload = function(outparamters){
//	var msg = outparamters.getValue("msg");
	var store = Ext.data.StoreManager.lookup('tableObj_dataStore');
	store.reload();
	
};
/********推荐职位start***********/
//推荐职位成功后确定
Global.z0301s = "";
Global.successRecommend = function(pram,record){
	if(record.length<=0){
		Ext.showAlert("请选择需要进行业务操作的数据！");
		return;
	}
	
	var hashvo=new HashMap();
	var z0301s = "";
	var obj;
	if(!a0100s||a0100s=="")
		obj = eval("("+parent.a0100temp+")");
	else
		obj = eval("("+a0100s+")");
	var objArr = obj.a0100;
	var a0100str = "";
	var nbase = obj.nbase;
	
	for ( var int2 = 0; int2 < objArr.length; int2++) {
			a0100str+=objArr[int2].a0100+",";
			for ( var int = 0; int < record.length; int++) {
				var temp =record[int].data;
				if(int2==0)
					z0301s+=temp.z0301_e+",";
				if(temp.z0301_e==objArr[int2].z0301){
					var position = temp.position.split("-")[0];
					var address = temp.position.split("-")[1];
					address=Ext.isEmpty(address)?"":address;
					if(temp.position.split("-")[0]==null)
					{
						position = "未知职位";
					}
					Ext.showAlert(objArr[int2].a0101+"已经申请过“"+position.replace(" ","")+"-"+address.replace(" ","")+"”职位！");
					return;
				}
				
			}
	}
	if(a0100str.length>0)
		a0100str= a0100str.substring(0,a0100str.length-1)+"`"+nbase;
	
	
	var msg = Global.judgeIsOrNoToRecommend(a0100str,z0301s);
	if(msg!=null&&msg.length>0){
		Ext.showAlert(msg);
		return;
	}
	
	 Ext.require('OperationLogUL', function(){
			Ext.create("OperationLogUL.operationLog", 
					{a0100s:a0100str,position_id:z0301s,function_str:"Global.recommendOtherPosition",msgType:"1",
				fn:function(flag){
					hashvo.put("z0301s", z0301s);
					Global.z0301s=z0301s;
					hashvo.put("a0100s", a0100str);
					hashvo.put("from",from);
					Rpc({functionId:'ZP0000002079',async:false,success:Global.returnFun},hashvo);
				}
			});
		});
	
};

Global.returnFun=function(outparam){
	var value = outparam.responseText;
	var map = Ext.decode(value);
	var msg = map.msg;
	if(msg!=null&&msg.length>0){
		Ext.Msg.alert("提示信息",msg,function(btn){ 
			Global.returnBack();
		});
		
	}else{
		Global.returnBack(map);
	}
	
};

Global.returnBack=function(map){
	var arrPage=pageDesc.split("`");
	if(from=="resumeCenter"||from=="talents"||"resumeInfo"==from){
		var resumeInfo = map.resumeInfo;
		//设置resumeInfoTop信息
		if(parent.Ext.getCmp("firstposition")&&resumeInfo){
			parent.Ext.get("positionstatus").setHtml(resumeInfo.first.status);
			parent.parametersInfo.zp_pos_id = resumeInfo.first.zp_pos_id;
			parent.resume_me.displayPosition(resumeInfo);
		}
		if(parent.Global.fromModule)
			parent.Global.fromModule = from;
		//职位候选人穿透推荐职位关闭,先进行数据刷新
		parent.Ext.data.StoreManager.lookup('tablegrid_dataStore').reload();
		parent.Ext.getCmp("recommendWinID").close();
	}else if(from=="process"){
		window.location.href="/recruitment/position/position.do?b_search=link&z0301="+arrPage[5]+"&z0381="+arrPage[4]+"&sign=2&node_id="+arrPage[3]+"&link_id="+arrPage[2]+"&page="+$URL.encode(arrPage[1]+"`"+arrPage[0])+"&from=process";
	}else if(from.lastIndexOf("resumeInfo")>0){
		//职位候选人穿透推荐职位关闭
		parent.Ext.getCmp("recommendWinID").close();
	}
	
};
Ext.Loader.setConfig({
	enabled: true,
	paths: {
		'SYSF.FileUpLoad':'/components/fileupload',
		'OperationLogUL': '/module/recruitment/js/operationLog.js'
	}
});

//查询简历职位
Global.qureyResume1 = function(nbase,a0100,zp_pos_id,from,current,pagesize,rowindex,schemeValues)
{
    if(zp_pos_id=="2iIeo7kAcbU@3HJD@")
    {
    	zp_pos_id = "";
    }
//    Ext.getBody().setHtml("");
    Ext.getCmp("tableObj").destroy();
    Ext.util.CSS.swapStyleSheet("theme1","/module/recruitment/css/style.css");
    Ext.util.CSS.swapStyleSheet("theme2","/components/tableFactory/tableGrid-theme/tableGrid-theme-all.css");
    Ext.Loader.setConfig({
    	enabled: true,
    	paths: {
    		'ResumeTemplateUL': '/module/recruitment/resumecenter/resumecenterlist',
    		'SYSF.FileUpLoad':'/components/fileupload'
    	}
    });
	
    Ext.require('ResumeTemplateUL.resumeInfoTop', function(){
		Ext.create("ResumeTemplateUL.resumeInfoTop", {nbase:nbase,a0100:a0100,zp_pos_id:zp_pos_id,from:from,current:current,pagesize:pagesize,rowindex:rowindex,schemeValues:schemeValues});
	});
}

Global.judgeIsOrNoToRecommend = function(a0100str,z0301s){
	var msg = "";
	var hashvo=new HashMap();
	hashvo.put("z0301s", z0301s);
	hashvo.put("a0100s", a0100str);
	Rpc({functionId:'ZP0000002080',async:false,success:successJudge},hashvo);
	function successJudge(outParameter){
		var value = outParameter.responseText;
		var map = Ext.decode(value);
		msg = map.msg;
	};
	return msg;
};
/********推荐职位end***********/
//查询全部记录
Global.searchAll=function(){
    Global.positionPlan = ["0","0","0"];
    var plan1 = document.getElementsByName("A");
    for(var i=0;i<plan1.length;i++)
    {
        plan1[i].style.textDecoration="none";
        plan1[i].style.color = "#1b4a98";
    }
    var plan2 = document.getElementsByName("B");
    for(var i=0;i<plan2.length;i++)
    {
        plan2[i].style.textDecoration="none";
        plan2[i].style.color = "#1b4a98";
    }
    var plan3 = document.getElementsByName("C");
    for(var i=0;i<plan3.length;i++)
    {
        plan3[i].style.textDecoration="none";
        plan3[i].style.color = "#1b4a98";
    }
    if(document.getElementById("all"))
    	document.getElementById("all").style.textDecoration="underline";
    var hashvo=new HashMap();
    var config = tableObj.getTableConfig();
    hashvo.put("queryArray",Global.positionPlan);
    hashvo.put("tablekey",config.tablekey);
    Rpc({functionId:'ZP0000002071',async:false,success:Global.toLoad},hashvo);
};
// 人岗匹配
Global.matchPersonnel = function() {
	var zp_gridpanel = Ext.getCmp("tableObj_tablePanel");
	var colSel = zp_gridpanel.getSelectionModel().getSelection();
	if(colSel.length>1){
		Ext.showAlert("只允许选择一个职位进行人岗匹配！");
		return;
	} 
	var z0319str = colSel[0].data.z0319;
	var z0319="";
	if(z0319str!=null){
		z0319=z0319str.substring(0, 2);
	}
	
	if(z0319 !="04"){
		Ext.showAlert(ONLY_CHOOSE_POSTSTATUS_POSITION);
		return;
	}
	 
	if (colSel && colSel[0]) {
		var position = colSel[0].data.position;// z0301_e
		positionid = colSel[0].data.z0301_e;
		var hashvo = new HashMap();
		hashvo.put("position", position);
		hashvo.put("positionid", positionid);
		Rpc({
					functionId : 'ZP0000002089',
					async : false,
					success : Global.initPersonPanel
				}, hashvo);
	} else {
		Ext.Msg.alert("提示信息", "请选择需要匹配的职位！");
		return;
	}
};
Global.initPersonPanel = function(form, action) {

	var result = Ext.decode(form.responseText);
	var flag = result.succeed;
	var templateObj;
	if (flag == true) {
		if(result.countNumber==0){
			Ext.showAlert("没有找到符合本岗位要求的人员！");
			return;
		}
		var conditions = result.tableConfig;
		var obj = Ext.decode(conditions);
		Global.templateObj = new BuildTableObj(obj);
	} else {
		Ext.showAlert(result.message);
	}
	// 人岗推荐的主界面
	var viewport = Ext.ComponentQuery.query('viewport')[0];
	viewport.removeAll(false);
	viewport.add(Global.templateObj.getMainPanel());
	
};
// 返回
Global.backPreview = function() {
	var viewport = Ext.ComponentQuery.query('viewport')[0];
	viewport.removeAll(true);
	viewport.add(tableObj.getMainPanel());
	//tableObj.tablePanel.getStore().load();
};
// 推荐按钮
Global.recommend = function() {
	Ext.Loader.setConfig({
				enabled : true,
				paths : {
					'sendEmailUL' : '../../../module/recruitment/js/sendEmail.js'
				}
			});
	var recommendpanel = Ext.getCmp('recommend_tablePanel');
	var colSel = recommendpanel.getSelectionModel().getSelection();
	var a0100s = '';
	var a0101s = '';
	if (colSel && colSel[0]) {
		for (var i = 0; i < colSel.length; i++) {
			a0100s += "," + colSel[i].data.a0100_e;
			a0101s += "," + colSel[i].data.a0101;
		}
		var map = new HashMap();
		//map.put('position', '教授');
		Ext.require('sendEmailUL', function() {
					Ext.create("sendEmailUL.sendEmail", {
								sub_module : "7",
								nModule : "91",
								a0100s : a0100s.substring(1),
								a0101s : a0101s.substring(1),
								z0301 : positionid,
								operation : false,
								map : map,
								title : '职位推荐模板',
								// fuId: 'ZP0000002079',
								// function_str:'Global.recommendOtherPosition',
								executionMethod : function(param) {
								}
							});
				});
	} else {
		Ext.Msg.alert("提示信息", "请选择推荐候选人！");
	}
};
Global.saveCallBack = function() {
	Global.matchPersonnel();
};

//回调函数
Global.createFinish = function(outparamters){
	var store = Ext.data.StoreManager.lookup('tableObj_dataStore');
	store.reload();
 	Ext.showAlert(NEW_POSITION_INITIATE);
};


Global.showWin = function(outParameter){
	var result = Ext.decode(outParameter.responseText);
	var positionInfo = result.positionInfo;
	var startFormat = Utils.getFormat(positionInfo[1].itemlength);
	var endFormat = Utils.getFormat(positionInfo[2].itemlength);
	var z0319 = positionInfo[4].value;
	if(z0319 != "06"){
		Ext.showAlert(END_STATE_CREATION);
		return;
	}
	
	if(positionInfo[0].value == null || positionInfo[0].value ==""){
		Ext.showAlert(UNKNOW_POSITION_CANNOT);
		return;
	}
		
	
	Ext.util.CSS.createStyleSheet(".x-form-field-my{border:0; width:100%;}","underline");
	newRoundWin = Ext.create('Ext.window.Window', {
	    title: CREATE_NEW_POSITION,
	    height: 230,
	    width: 400,
	    modal:true,
	    layout: "anchor", 
	    items: [
	    	{
	    		xtype:'textfield',
	    		padding:'10 10 0 10',
	    		id:'name',
	    		labelStyle: 'text-align:right;',
	    		fieldLabel:positionInfo[0].itemdesc,
	    		inputWrapCls: 'x-form-field-my inwidth',
				readOnly:true,
	    		
	    	},
	    	{
		    	xtype : 'datetimefield',
				labelStyle: 'text-align:right;',
				fieldLabel: positionInfo[1].itemdesc, 
				padding:'10 10 0 10',
				id:'startTime',
				format: startFormat,
				fieldStyle:'text-align:left',
				editable:false,
			},
	    	{
		    	xtype : 'datetimefield',
				labelStyle: 'text-align:right;',
				fieldLabel: positionInfo[2].itemdesc, 
				padding:'10 10 0 10',
				id:'endTime',
				format: endFormat,
				fieldStyle:'text-align:left',
				editable:false,
				
			},
			{
	    		xtype:'textfield',
	    		id:'number',
	    		labelStyle: 'text-align:right;',
	    		padding:'10 10 20 10',
	    		fieldLabel:positionInfo[3].itemdesc,
	    	},{
	    		buttonAlign : 'center',
	    		buttons : [{
	    			xtype: "button",
	 	            text: "确定",
	 	            margin:'7 0 0 0',
	 	            handler: function() {
	 	            	var number = Ext.getCmp('number').value;
	 	            	var isFillable = positionInfo[3].itemFillable;
	 	            	if((isFillable == true && number =="") || Ext.getCmp('startTime').getRawValue() =="" 
		 	            	   || Ext.getCmp('endTime').getRawValue() =="" ){
		 	            	    Ext.showAlert(ALL_INPUT_FINISH);
		 	           		    return;
		 	            }
	 	            	
	 	            	if(!/^\d{0,3}?$/g.test(number)){ 
	 	            	     Ext.showAlert(PEOPLE_NUMBER_LIMIT);
	 	            	     Ext.getCmp('number').setValue("");
	 	            	     return;
	 	            	}
	 	            	
	 	            	if( isFillable == true && number < 1){ 
	 	            		Ext.showAlert(RECRUITS_NUMBER_MINNIMUM);
	 	            	    Ext.getCmp('number').setValue("");
	 	            	    return;
	 	            	}
	 	            	
	 	            	if(Ext.getDom("startTime")){
		 	           		var z0329 =Ext.getCmp('startTime').getRawValue();
		 	           		if(z0329.length == 10){
		 	           			z0329 =  z0329 + " 00:00:00";
		 	           		}
		 	           		var z0329Time = new Date(z0329.replace(/-/g,"/"))
		 	           	}
	 	           	
		 	           	if(Ext.getDom("endTime")){
		 	           		var z0331 =Ext.getCmp('endTime').getRawValue();
			 	           	if(z0331.length == 10){
		 	           			z0331 =  z0331 + " 23:59:59";
		 	           		}
			 	            var z0331Time = new Date(z0331.replace(/-/g,"/"))
		 	           	}
	 	           	
		 	            var nowTime=new Date().getTime();
	 	                if(z0329Time.getTime() > z0331Time.getTime()){
	 	            	    Ext.showAlert(STARDATE_NOTBEFORE_ENDDATE);
	 	           		    return;
	 	                }
		 	            
		 	           if(nowTime > z0331Time.getTime()){
	 	            	    Ext.showAlert(ENDTIME_NOTLESS_NOWTIME);
	 	           		    return;
	 	                }
	 	                
	 	            	 
	 	            	var hashvo=new HashMap();
		 	            hashvo.put("z0301", result.z0301);
		 	            hashvo.put("z0331", z0331);
		 	            hashvo.put("z0329", z0329);
		 	            hashvo.put("z0315", Ext.getCmp('number').value);
		 	            hashvo.put("z0351", positionInfo[0].value);
		 	            hashvo.put("flag", "1"); 
		 	            Rpc({functionId:'ZP0000002091',async:false,success:Global.createFinish},hashvo);
		 	            newRoundWin.close();
	 	            }
            }],
	        }
	    ]
	}).show();
	
	Ext.getCmp('name').setValue(positionInfo[0].value);
	if(positionInfo[3].value == null || positionInfo[3].value =="")
		Ext.getCmp('number').setValue(0);
	else
		Ext.getCmp('number').setValue(positionInfo[3].value);
};