Ext.define("HRCloud.SyncSetting", {
    extend: 'Ext.panel.Panel',
    requires: ['EHR.extWidget.field.DateTimeField', 'EHR.extWidget.field.CodeTreeCombox', 'EHR.fielditemselector.FieldItemSelector'],
    config: {
        layout: {
            type: 'vbox',
            align: 'stretch'
        }
    },
    //初始化组件
    initComponent: function () {
        this.callParent();
        this.setItemPanel();
        this.initCompPage();
        this.assessFieldSet = "";
    },
    //设置主页
    setItemPanel: function () {
        var me = this;
        this.activeItem = 0;
        //标题栏
        var titlePanel = {
            xtype: 'panel',
            width: '100%',
            padding: '10 0 10 10',
            border: 0,
            height: 40,
            layout: 'hbox',
            items: [{
            	//xus 20/1/3 【57152 】V77云集成：电脑屏幕比例150%，查看同步详情界面，【返回】按钮，显示不全，详见附件！
                xtype: 'container', flex:1, html: syncSetting.hrcloudSetting
            }, {
                xtype: 'container',
                style: 'padding-right:30px;color:#0096FF;cursor:pointer',
                html: '<div style="cursor: pointer;">' + syncSetting.cleanInterfaceData + '</div>',
                listeners: {
                    afterrender: function (contain, eOpts) {
                        contain.el.on('click', function () {
                            Ext.MessageBox.confirm(syncSetting.tip, syncSetting.selection_yes + syncSetting.selection_no + syncSetting.cleanInterfaceData + "?", function (optional) {
                                if (optional == 'yes') {
                                    var map = new HashMap();
                                    map.put("transType", "cleanInterfaceData");
                                    Rpc({
                                        functionId: 'SYS00005001', async: false, success: function (res) {
                                            var resultObj = Ext.decode(res.responseText);
                                            window.location.href = "/module/system/hrcloud/SyncMapSetting.html";
                                        }
                                    }, map);
                                }
                            });
                        });
                    }
                }
            }, {
                xtype: 'container',
                style: 'padding-right:30px;color:#0096FF;cursor:pointer',
                html: '<div style="cursor: pointer;">' + syncSetting.showSyncDetail + '</div>',
                listeners: {
                    afterrender: function (contain, eOpts) {
                        contain.el.on('click', function () {
                            me.addSyncInfoPage();
                            Ext.getCmp('mainpanel').setHidden(true);
                            Ext.getCmp('showsyncinfopagepanel').setHidden(false);
                            me.items.items[0].items.items[0].setHtml(syncSetting.sync_info);
                            contain.setHidden(true);
                            contain.nextSibling().setHidden(false);
                            contain.nextSibling().nextSibling().setHidden(false);
                        })
                    }
                }
            }, {
                xtype: 'container',
                style: 'padding-right:30px;color: rgb(0, 150, 255);cursor:pointer;',
//				width:80,
                hidden: true,
                html: syncSetting.manual_operation_dosync,
                listeners: {
                    'click': {
                        element: 'el',
                        fn: function () {
                            Rpc({
                                functionId: 'SYS00005003', async: false, success: function (res) {
                                	var resultObj = Ext.decode(res.responseText);
                                	if(resultObj.flag == "false"){
                                		Ext.Msg.alert(syncSetting.tip, resultObj.errmessage);
                                	}else{
                                		Ext.Msg.alert(syncSetting.tip, syncSetting.syncTipinfo);
                                	}
                            }}, new HashMap());
                        }
                    }
                }
            }, {
                xtype: 'container',
                id: 'returnBtn',
                hidden: true,
                style: 'padding-right:30px;color:#0096FF;',
                html: '<div style="cursor: pointer;">' + syncSetting.return_btn + '</div>',
                listeners: {
                    afterrender: function (contain, eOpts) {
                        contain.el.on('click', function () {
                            Ext.getCmp('mainpanel').setHidden(false);
                            Ext.getCmp('showsyncinfopagepanel').setHidden(true);
                            me.items.items[0].items.items[0].setHtml(syncSetting.hrcloudSetting);
                            contain.previousSibling().previousSibling().setHidden(false);
                            contain.previousSibling().setHidden(true);
                            contain.setHidden(true);
                        })
                    }
                }
            }]
        };
        //选项栏
        var tabPanel = {
            xtype: 'panel',
            //height:50,
//			width:'100%',
            border: 0,
            layout: 'hbox',
            padding: '20 0 20 0',
            items: [
                //第一步
                {
                    xtype: 'container',
                    layout: 'hbox',
                    items: [
                        {
                            xtype: 'image',
                            src: 'images/1_sld.png',
                            id: 'step_button_1',
                            width: 40,
                            height: 40
                        },
                        {
                            xtype: 'container',
                            width: 80,
                            padding: '0 0 0 10',
                            cls: 'tabfont_cls',
                            html: syncSetting.paramSetting
                        }]
                    ,
                    listeners: {
                        afterrender: function (contain) {
                            contain.el.on('click', function () {
                                me.selectStep(1);
                                if (Ext.getCmp('step_panel_4')) {
                                    Ext.getCmp('acceptAssessSaveBtn').setHidden(true);
                                    Ext.getCmp('acceptAssessPrevBtn').setHidden(false);
                                    Ext.getCmp('acceptAssessNextBtn').setHidden(false);
                                }
                            })
                        }
                    }
                }, {
                    //边线（破折号）
                    xtype: 'container',
                    border: '0 0 1 0',
                    padding: '0 10 0 10',
                    width: 90,
                    height: 40,
                    html: '<div style="border-bottom:1px solid #787878;height:20px;"></div>'
                },
                //第二步
                {
                    xtype: 'container',
                    layout: 'hbox',
                    items: [
                        {
                            xtype: 'image',
                            src: 'images/02.png',
                            id: 'step_button_2',
                            width: 40,
                            height: 40
                        },
                        {
                            xtype: 'container',
                            width: 80,
                            padding: '0 0 0 10',
                            cls: 'tabfont_cls',
                            html: syncSetting.fieldItemRelated
                        }]
                }, {
                    //边线（破折号）
                    xtype: 'container',
                    border: '0 0 1 0',
                    padding: '0 10 0 10',
                    width: 90,
                    height: 40,
                    html: '<div style="border-bottom:1px solid #787878;height:20px;"></div>'
                },
                //第三步
                {
                    xtype: 'container',
                    layout: 'hbox',
                    items: [
                        {
                            xtype: 'image',
                            src: 'images/03.png',
                            id: 'step_button_3',
                            width: 40,
                            height: 40
                        },
                        {
                            xtype: 'container',
                            width: 80,
                            padding: '0 0 0 10',
                            cls: 'tabfont_cls',
                            html: syncSetting.codeItemRelated
                        }]
                }, {
                    //边线（破折号）
                    xtype: 'container',
                    border: '0 0 1 0',
                    padding: '0 10 0 10',
                    width: 90,
                    height: 40,
                    html: '<div style="border-bottom:1px solid #787878;height:20px;"></div>'
                },
                //第四步
                {
                    xtype: 'container',
                    layout: 'hbox',
                    items: [
                        {
                            xtype: 'image',
                            src: 'images/04.png',
                            id: 'step_button_4',
                            width: 40,
                            height: 40
                        },
                        {
                            xtype: 'container',
                            width: 80,
                            padding: '0 0 0 10',
                            cls: 'tabfont_cls',
                            html: syncSetting.syncSetting
                        }]
                }]
        };
        //主页面
        var mainPanel = {
            xtype: 'panel',
            id: 'mainpanel',
            flex: 1,
            width: '100%',
            border: 0,
            style: 'border-top:1px solid #c5c5c5;',
            layout: {
                type: 'vbox',
                align: 'center'
            },
            items: [tabPanel]
        }
        this.add(titlePanel);
        this.add(mainPanel);
    },
    //加载同步详情页面
    addSyncInfoPage: function () {
        var me = this;
        if (!Ext.getCmp('showsyncinfopagepanel')) {
            this.add({
                xtype: 'panel',
                id: 'showsyncinfopagepanel',
                width: '100%',
                flex: 1,
                border: 0,
                height: 500,
                style: 'border-top:1px solid #c5c5c5',
                layout: 'fit',
                items: [{
                    xtype: 'tabpanel',
                    width: '100%',
                    scrollable: true,
                    layout: 'vbox',
                    padding: '10 50 0 50',
                    activeItem: 0,
                    items: [me.showSyncLogsPanel(), {
                        xtype: 'container',
                        title: syncSetting.hcm2Cloud + syncSetting.datatext + syncSetting.logger,
                        scrollable: true,
                        id: 'dataLogPanel',
                        width: '100%',
                        padding: '10 50 10 50',
                        layout: 'vbox'
                    }, {
                        xtype: 'container',
                        title: syncSetting.cloud2Hcm + syncSetting.datatext + syncSetting.logger,
                        scrollable: true,
                        id: 'assessDataReciPanel',
                        width: '100%',
                        padding: '10 50 10 50',
                        layout: 'vbox'
                    }]
                }]
            });
            me.showSyncDataLogsPanel();
            me.showSyncAssessDataLogsPanel();
            
            //刷新操作日志数据
            me.refOpLogData();
        }
    },
    //数据日志信息详情 组件
    showSyncDataLogsPanel: function () {
        var dataLogPanel = Ext.getCmp('dataLogPanel');
        var vo = new HashMap();
        vo.put("transType", "showDataLogs");
        Rpc({
            functionId: 'SYS00005004', async: false, success: function (res) {
                var resultObj = Ext.decode(res.responseText);
                var fileList = resultObj.fileList;
                if (!fileList || fileList.length == 0) {
                    dataLogPanel.add({
                        xtype: 'container',
                        width: '100%',
                        padding: '100 0 0 0',
                        style: 'text-align:center;font-size:16px;',
                        html: syncSetting.notData
                    });
                    return;
                }
                //标题
                dataLogPanel.add({
                    xtype: 'container',
                    width: '100%',
                    layout: 'hbox',
                    style: 'border-bottom:1px solid rgb(0, 150, 255);padding:14px 0px;font-size:14px;',
                    items: [{
                        xtype: 'container',
                        flex: 0.4,
                        html: '<b>' + syncSetting.file_date + '</b>'
                    }, {
                        xtype: 'container',
                        flex: 0.6,
                        html: '<b>' + syncSetting.file_name + '</b>'
                    }, {
                        xtype: 'container',
                        width: 40,
                        html: '<b>' + syncSetting.file_operation + '</b>'
                    }]
                });
                for (var i = 0; i < fileList.length; i++) {
                    dataLogPanel.add({
                        xtype: 'container',
                        width: '100%',
                        layout: 'hbox',
                        style: i == fileList.length - 1 ? 'padding:14px 0px;font-size:14px;' : 'border-bottom:1px solid rgb(0, 150, 255);padding:14px 0px;font-size:14px;',
                        items: [{
                            xtype: 'container',
                            flex: 0.4,
                            html: '20'+fileList[i].substr(13, 2) + syncSetting.year + fileList[i].substr(15, 2) + syncSetting.month + fileList[i].substr(17, 2) + syncSetting.day
                        }, {
                            xtype: 'container',
                            flex: 0.6,
                            html: fileList[i]
                        }, {
                            xtype: 'button',
                            width: 40,
                            filename: fileList[i],
                            text: syncSetting.download,
                            listeners: {
                                click: function (btn) {
                                    var filename = btn.filename;
                                    var vo = new HashMap();
                                    vo.put("transType", "downLoadDataLog");
                                    vo.put("filename", filename);
                                    Rpc({
                                        functionId: 'SYS00005004', async: false, success: function (res) {
                                            var result = Ext.decode(res.responseText);
                                            if (result.flag == true) {
                                                window.location.target = "_blank";
                                                window.location.href = "/servlet/vfsservlet?fromjavafolder=true&fileid=" +  decode(result.fileName);
                                            } else {
                                                Ext.MessageBox.alert(syncSetting.infor_message, syncSetting.download + syncSetting.fail);
                                            }
                                        }
                                    }, vo);
                                }
                            }
                        }]
                    });
                }
            }
        }, vo);
    },
    //考核数据接收日志信息详情页
    showSyncAssessDataLogsPanel: function () {
        var assessDataReciPanel = Ext.getCmp('assessDataReciPanel');
        var vo = new HashMap();
        vo.put("transType", "showAssessDataLogs");
        Rpc({
            functionId: 'SYS00005004', async: false, success: function (res) {
                var resultObj = Ext.decode(res.responseText);
                var fileList = resultObj.fileList;
                if (!fileList || fileList.length == 0) {
                    assessDataReciPanel.add({
                        xtype: 'container',
                        width: '100%',
                        padding: '100 0 0 0',
                        style: 'text-align:center;font-size:16px;',
                        html: syncSetting.notData
                    });
                    return;
                }
                //标题
                assessDataReciPanel.add({
                    xtype: 'container',
                    width: '100%',
                    layout: 'hbox',
                    style: 'border-bottom:1px solid rgb(0, 150, 255);padding:14px 0px;font-size:14px;',
                    items: [{
                        xtype: 'container',
                        flex: 0.4,
                        html: '<b>' + syncSetting.file_date + '</b>'
                    }, {
                        xtype: 'container',
                        flex: 0.6,
                        html: '<b>' + syncSetting.file_name + '</b>'
                    }, {
                        xtype: 'container',
                        width: 40,
                        html: '<b>' + syncSetting.file_operation + '</b>'
                    }]
                });
                for (var i = 0; i < fileList.length; i++) {
                    assessDataReciPanel.add({
                        xtype: 'container',
                        width: '100%',
                        layout: 'hbox',
                        style: i == fileList.length - 1 ? 'padding:14px 0px;font-size:14px;' : 'border-bottom:1px solid rgb(0, 150, 255);padding:14px 0px;font-size:14px;',
                        items: [{
                            xtype: 'container',
                            flex: 0.4,
                            html: '20'+fileList[i].filename.substr(10, 2) + syncSetting.year + fileList[i].substr(12, 2) + syncSetting.month + fileList[i].substr(14, 2) + syncSetting.day
                        }, {
                            xtype: 'container',
                            flex: 0.6,
                            html: fileList[i].filename
                        }, {
                            xtype: 'button',
                            width: 40,
                            filename: fileList[i].filename,
                            fileid:fileList[i].fileid,
                            text: syncSetting.download,
                            listeners: {
                                click: function (btn) {
                                    var filename = btn.filename;
                                    var vo = new HashMap();
                                    vo.put("transType", "downAssessLoadDataLog");
                                    vo.put("filename", filename);
                                    Rpc({
                                        functionId: 'SYS00005004', async: false, success: function (res) {
                                            var result = Ext.decode(res.responseText);
                                            if (result.flag == true) {
                                                window.location.target = "_blank";
//                                              window.location.href = "/servlet/DisplayOleContent?filename=" + result.fileName + "&&filePath=" + result.filePath;
                                                window.location.href = "/servlet/vfsservlet?fileid=" + btn.fileid + "&islog=true";
                                            } else {
                                                Ext.MessageBox.alert(syncSetting.infor_message, syncSetting.download + syncSetting.fail);
                                            }
                                        }
                                    }, vo);
                                }
                            }
                        }]
                    });
                }
            }
        }, vo);
    },
  //操作日志信息详情 组件
    showSyncLogsPanel: function () {
    	var me = this;
    	me.oprationPage = 1;
    	me.pageSize = 30;
    	me.pageCount = 0;
    	var statusArrays = [syncSetting.fail, syncSetting.success, syncSetting.executemsg];
    	var logPanel = {
    			xtype: 'container',
    			title: syncSetting.operation + syncSetting.logger,
//    			scrollable: true,
    			width: '100%',
    			padding: '10 50 10 50',
    			layout: 'vbox',
    			items: []
    	};
    	//获取根级日志总条数
		var vo = new HashMap();
		vo.put("transType", "getOpLogParentCount");
		Rpc({
		     functionId: 'SYS00005004', async: false, success: function (res) {
		    	 var resultObj = Ext.decode(res.responseText);
		    	 me.pageCount = resultObj.pageCount;
		     }
		},vo);
		
		if(me.pageCount == 0){
			//没有操作日志
			logPanel.items.push({
                xtype: 'container',
                width: '100%',
                padding: '100 0 0 0',
                style: 'text-align:center;font-size:16px;',
                html: syncSetting.notData
            });
		}else{
			//有操作日志
			var data = [];
			for(var zz = 0; zz < me.pageCount/me.pageSize ; zz++){
				var showPageIndex = zz+1;
				data.push({"abbr":showPageIndex,"name":syncSetting.No+showPageIndex+syncSetting.page});
			}
			var states = Ext.create('Ext.data.Store', {
				fields: ['abbr', 'name'],
				data : data
			});
			//页签下拉选
			logPanel.items.push({
				xtype:'combobox',
				fieldLabel: syncSetting.opLogShowPage+":",
				style:'padding-top:10px;padding-bottom:10px;padding-left:20px;',
				store: states,
				queryMode: 'local',
				displayField: 'name',
				valueField: 'abbr',
				value:1,
				listeners:{
					change:function( comb, newValue, oldValue, eOpts ){
						me.oprationPage = newValue;
				    	me.pageSize = 30;
						me.refOpLogData();
					}
				}
			});
			//日志信息页
			logPanel.items.push({
				xtype: 'container',
				id:'opLogDataListPanel',
    			scrollable: true,
    			width: '100%',
    			style:'border-top:1px dashed #c5c5c5;',
    			flex:1,
    			margin:'0 0 0 20',
    			padding: '10 50 10 0',
    			layout: 'vbox',
    			items: []
			});
		}
        return logPanel;
    },
    //刷新操作日志信息页
    refOpLogData:function(){
    	var me = this;
    	logPanel = Ext.getCmp('opLogDataListPanel');
    	//xus 19/12/19 【56544】v77发版：ie浏览器：云集成，第一次点击查看同步详情，详情显示在界面下方，再点击查看同步详情，才显示正确的界面
    	if(!logPanel){
    		return;
    	}
    	logPanel.removeAll(true);
    	var statusArrays = [syncSetting.fail, syncSetting.success, syncSetting.executemsg];
    	 var vo = new HashMap();
         vo.put("transType", "showMainLogs");
         vo.put("page", me.oprationPage);
         vo.put("size", me.pageSize);
         Rpc({
             functionId: 'SYS00005004', async: false, success: function (res) {
                 var resultObj = Ext.decode(res.responseText);
                 var logsData = resultObj.logsData;
                 if (!logsData || logsData.length == 0) {
                     logPanel.add({
                         xtype: 'container',
                         width: '100%',
                         padding: '100 0 0 0',
                         style: 'text-align:center;font-size:16px;',
                         html: syncSetting.notData
                     });
                     return;
                 }
                 //第一级log 日志
                 for (var i = 0; i < logsData.length; i++) {

                     (function (i) {
                         var synp = "";
                         if(logsData[i].logtype==1
                             ||logsData[i].logtype==2
                             ||logsData[i].logtype==3){
                             synp = syncSetting.hcm2Cloud;
                         }else{
                             synp = syncSetting.cloud2Hcm;
                         }
                         var statusname = "";
                         if(logsData[i].status == 1){
                             statusname = '<span style="margin-left:25px;">'+statusArrays[logsData[i].status]+'</span>'
                         }else{
                             statusname = '<span style="color:red;'+(logsData[i].status==0?'margin-left:25px;':'')+'">' + statusArrays[logsData[i].status] + '</span>'
                         }
                         var log = {
                             xtype: 'container',
                             width: '100%',
                             layout: 'vbox',
                             style: i == logsData.length - 1 ? 'padding:14px 0px;' : 'border-bottom:1px solid rgb(0, 150, 255);padding:14px 0px;',
                             items: [{
                                 xtype: 'container',
                                 layout: 'hbox',
                                 width: '100%',
                                 items: [
                                     //同步对象
                                     {
                                         xtype: 'container',
                                         padding: '0 10 0 0',
                                         cls: 'fontsize_cls',
                                         flex: 0.2,
                                         html:synp
                                     },
                                     { //同步时间
                                         xtype: 'container',
                                         flex: 0.2,
                                         padding: '0 10 0 0',
                                         cls: 'fontsize_cls',
                                         html: logsData[i].syncdate
                                     }, { // 日志 信息
                                         xtype: 'container',
                                         flex: 0.2,
                                         padding: '0 10 0 0',
                                         cls: 'fontsize_cls',
                                         html: logsData[i].loginfo
                                     }, { // 同步失败日志信息
                                         xtype: 'container',
                                         flex: 0.4,
                                         cls: 'fontsize_cls',
                                         html: logsData[i].errormsg
                                     },{
                                         xtype:'container',
                                         width:100,
                                         layout:'hbox',
                                         items:[
                                             {//同步异常 显示图标
                                                 xtype: 'image',
                                                 id: 'image' + logsData[i].logid,
                                                 width: 25,
                                                 style: 'cursor:pointer',
                                                 hidden: logsData[i].status == 2 ? false : true,
                                                 src: '/system/sms/weixin/images/up.png',
                                                 listeners: {
                                                     'click': {
                                                         'element': 'el',
                                                         fn: function () {
                                                             var logPanel = Ext.getCmp('log' + logsData[i].logid);
                                                             var imagePanel = Ext.getCmp('image' + logsData[i].logid);
                                                             var src = imagePanel.getSrc();
                                                             var btn = this.component.ownerCt.child('button');
                                                             if (src.indexOf('up.png') == -1) {
                                                                 imagePanel.setSrc('/system/sms/weixin/images/up.png');
                                                                 logPanel.setHidden(true);
                                                                 btn.setHidden(true);
                                                                 return;
                                                             } else {
                                                                 imagePanel.setSrc('/system/sms/weixin/images/down.png');
                                                                 logPanel.setHidden(false);
                                                                 btn.setHidden(false);
                                                                 if (logPanel.items.items.length) {
                                                                     return;
                                                                 }
                                                             }
                                                             var vo = new HashMap();
                                                             vo.put("transType", "showChildLogs");
                                                             vo.put("logid", logsData[i].logid);
                                                             Rpc({
                                                                 functionId: 'SYS00005004',
                                                                 async: false,
                                                                 success: function (res) {
                                                                     var resultObj = Ext.decode(res.responseText);
                                                                     var childLogsData = resultObj.childLogsData;
                                                                     if (childLogsData && childLogsData.length == 0) {
                                                                         btn.setHidden(true);
                                                                     }
                                                                     for (var j = 0; j < childLogsData.length; j++) {
                                                                         if (childLogsData[j].outLimit) {
                                                                             logPanel.add({
                                                                                 xtype: 'container',
                                                                                 width: '100%',
                                                                                 style: 'margin:4px 0px 5px ;text-align:center;color:#CCCCCC;font-size:14px;',
                                                                                 html: childLogsData[j].outLimit
                                                                             });
                                                                             continue;
                                                                         }
                                                                         var childLogPanel = {
                                                                             xtype: 'container',
                                                                             layout: 'hbox',
                                                                             width: '100%',
                                                                             style: 'margin:4px 0px;',
                                                                             items: [{
                                                                                 xtype: 'container',
                                                                                 flex: 0.2,
                                                                                 cls: 'child_fontsize_cls',
                                                                                 html: childLogsData[j].name
                                                                             }, {
                                                                                 xtype: 'container',
                                                                                 flex: 0.2,
                                                                                 cls: 'child_fontsize_cls',
                                                                                 style:'padding-right:20px',
                                                                                 html: childLogsData[j].orgInfo
                                                                             }, {
                                                                                 xtype: 'container',
                                                                                 flex: 0.2,
                                                                                 cls: 'child_fontsize_cls',
                                                                                 html: syncSetting.syncFail
                                                                             }, {
                                                                                 xtype: 'container',
                                                                                 flex: 0.4,
                                                                                 cls: 'child_fontsize_cls',
                                                                                 html: childLogsData[j].errormsg
                                                                             },{xtype:'container',
                                                                                 width:93
                                                                             }]
                                                                         };
                                                                         logPanel.add(childLogPanel);
                                                                     }
                                                                 }
                                                             }, vo);
                                                         }
                                                     }
                                                 }
                                             }, {// 同步状态
                                                 xtype: 'container',
                                                 cls: 'fontsize_cls',
                                                 html:statusname
                                             },{ // 同步失败日志信息
                                                 xtype: 'button',
                                                 width: 40,
                                                 hidden: true,
                                                 style:'margin-left:5px;',
                                                 text: syncSetting.download,
                                                 listeners: {
                                                     click: function (btn) {
                                                         var img = btn.ownerCt.child('image');
                                                         var main_logid = img.id.substring(5);

                                                         var vo = new HashMap();
                                                         vo.put("transType", "downLoad");
                                                         vo.put("mainLogid", main_logid);
                                                         Rpc({
                                                             functionId: 'SYS00005004', async: false, success: function (res) {
                                                                 var result = Ext.decode(res.responseText);
                                                                 if (result.flag == true) {
                                                                     window.location.target = "_blank";
//                                                                   window.location.href = "/servlet/DisplayOleContent?filename=" + result.fileName;
                                                                     window.location.href = "/servlet/vfsservlet?fileid=" + result.fileName + "&fromjavafolder=true";
                                                                 } else {
                                                                     Ext.MessageBox.alert(syncSetting.infor_message, syncSetting.download + syncSetting.fail);
                                                                 }
                                                             }
                                                         }, vo);
                                                     }
                                                 }
                                             }]
                                     }]
                             }, {
                                 xtype: 'container',
                                 id: 'log' + logsData[i].logid,
                                 width: '100%',
                                 layout: 'vbox',
                                 hidden: true
                             }]
                         };

                         logPanel.add(log);

                     })(i);
                 }


             }
         }, vo);
    },
    //获取参数设置数据
    getParamSettingPageData: function () {
        var me = this;
        var vo = new HashMap();
        vo.put("transType", "loadAPIConfig");
        Rpc({
            functionId: 'SYS00005001', async: false, success: function (res) {
                var resultObj = Ext.decode(res.responseText);
                me.paramSettingPageData = resultObj.returnStr.return_data;
            }
        }, vo);
    },
    //初始化参数设置页面
    initParamSettingPage: function () {
        this.getParamSettingPageData();
        var me = this;
        var paramSettingPage = {
            xtype: 'container',
            id: 'step_panel_1',
            //xus 20/1/3 【57153】V77云集成：火狐浏览器，电脑屏幕比例150%，【下一步】按钮，底部缺线，详见附件！
            flex:1,
            layout: {
                type: 'vbox',
                align: 'center'
            },
            items: [{
                xtype: 'container',
                id: 'paramSettingSet',
                //height:'100%',
                width: 450,
                border: 0,
                items: [{
                    xtype: 'container',
                    margin: '20 0 20 0',
                    style: 'color:#CCCCCC;font-size:14px;',
                    html: syncSetting.cloudSettingIntru
                }, {
                    xtype: 'textfield',
                    fieldLabel: syncSetting.userIDDesc + ' (AppId)',
                    labelWidth: 150,
                    width: 410,
                    margin: '10 0 10 0',
                    id: 'user_appId',
                    name: 'user_appId',
                    value: me.paramSettingPageData.appId
                }, {
                    xtype: 'textfield',
                    fieldLabel: syncSetting.tenantIDDesc + ' (tenantID)',
                    labelWidth: 150,
                    width: 410,
                    margin: '10 0 10 0',
                    id: 'user_tenantID',
                    name: 'user_tenantID',
                    value: me.paramSettingPageData.tenantId
                }, {
                    xtype: 'textfield',
                    fieldLabel: syncSetting.userSecretKey + ' (AppSecret)',
                    labelWidth: 150,
                    width: 410,
                    margin: '10 0 10 0',
                    id: 'user_AppSecret',
                    name: 'user_AppSecret',
                    value: me.paramSettingPageData.appSecret
                }]
            }, {
                //下一步按钮
                xtype: 'button',
                margin: '20 0 0 0',
                width: 100,
                style: 'border-radius: 2px;',
                layout: {
                    align: 'center'
                },
                text: syncSetting.nextDesc,
                listeners: {
                    click: function (btn, e, eOpts) {
                        me.saveConfigValues();
                        me.selectStep(2);
                    }
                }
            }]
        };
        Ext.getCmp('mainpanel').add(paramSettingPage);
    },
    //获取指标关联页面数据
    getfieldItemRelatedPageData: function () {
        var me = this;

        var vo = new HashMap();
        Ext.getCmp('user_appId').getValue();
        vo.put("transType", "loadFieldMap");
        vo.put("appId", Ext.String.trim(Ext.getCmp('user_appId').getValue()));
        vo.put("tenantId", Ext.String.trim(Ext.getCmp('user_tenantID').getValue()));
        vo.put("appSecret", Ext.String.trim(Ext.getCmp('user_AppSecret').getValue()));
        Rpc({
            functionId: 'SYS00005001', async: false, success: function (res) {
                var resultObj = Ext.decode(res.responseText);
                me.fieldItemRelatedPageData = resultObj.returnStr.return_data;
            }
        }, vo);
    },
    //初始化指标关联页面
    initfieldItemRelatedPage: function () {
        var me = this;
        var fieldItemRelated = {
            xtype: 'container',
            id: 'step_panel_2',
            flex: 1,
            layout: {
                type: 'vbox',
                align: 'center'
            },
            items: [{
                xtype: 'tabpanel',
                id: 'tabpanel_2',
                //xus 20/1/3 【57150】V77云集成：电脑屏幕比例150%，第二步指标对应，按钮不显示，无法下一步，详见附件！
                flex:1,
                width: 770
            }, {
                xtype: 'container',
                layout: {
                    type: 'hbox',
                    align: 'middle'
                },
                height: 50,
                items: [{
                    //上一步按钮
                    xtype: 'button',
                    width: 100,
                    style: 'border-radius: 2px;',
                    layout: {
                        align: 'center'
                    },
                    text: syncSetting.prevDesc,
                    listeners: {
                        click: function (btn, e, eOpts) {
                            me.selectStep(1);
                        }
                    }
                }, {
                    //自动匹配按钮
                    xtype: 'button',
                    margin: '0 0 0 100',
                    width: 100,
                    style: 'border-radius: 2px;',
                    layout: {
                        align: 'center'
                    },
                    text: syncSetting.auto_connect_code,
                    listeners: {
                        click: function (btn, e, eOpts) {
                            me.fieldAutoConnect();
                        }
                    }
                }, {
                    //下一步按钮
                    xtype: 'button',
                    margin: '0 0 0 100',
                    width: 100,
                    style: 'border-radius: 2px;',
                    layout: {
                        align: 'center'
                    },
                    text: syncSetting.nextDesc,
                    listeners: {
                        click: function (btn, e, eOpts) {
                            me.selectStep(3);
                        }
                    }
                }]
            }]

        };
        Ext.getCmp('mainpanel').add(fieldItemRelated);
        this.getfieldItemRelatedPageData();
        this.setfieldItemTabPanel();
    },
    //自动匹配
    fieldAutoConnect: function () {
        var me = this;
        var vo = new HashMap();
        var json_str = me.getnotnullfields();
        vo.put("transType", "autoMatch");
        vo.put("json_str", Ext.encode(json_str));
        Rpc({
            functionId: 'SYS00005001', async: false, success: function (res) {
                var resultObj = Ext.decode(res.responseText);
                var matchMap = resultObj.returnStr.return_data;
                me.rePutMatchFieldPage(matchMap);
            }
        }, vo);
    },
    //回填关联指标
    rePutMatchFieldPage: function (matchMap) {
        var comboboxs = Ext.getCmp('hrTOcloud_panel').query('FieldSelectorPiker');
        var fields = matchMap.fields;
        for (var i = 0; i < comboboxs.length; i++) {
            var id = comboboxs[i].itemid;
            if (fields[id]) {
                var json = fields[id];
                comboboxs[i].setValue(json.itemdesc);
                comboboxs[i].hritemid = json.itemid;
            }
        }
        var formpanels = Ext.getCmp("fieldMappings").items.items;
        var cloudTOhr = matchMap.cloudTOhr;
        for (var i = 0; i < formpanels.length; i++) {
            var formpanel = formpanels[i];
            var fieldItems = formpanel.items.items;
            var hrSetId = fieldItems[0].value;
            var cloudset = cloudTOhr[hrSetId];
            if(!cloudset){
                continue;
            }
            for (var j = 1; j < fieldItems.length; j++) {
                var cloudid = fieldItems[j].itemid;
                var cloudf = cloudset[cloudid];
                if(cloudf){
                    fieldItems[j].setItemValue(cloudf.itemid+"`"+cloudf.itemdesc);
                }
            }
        }
    },
    //获取值不为空的指标
    getnotnullfields: function () {
        var json_str  = {};
        var fields = [];
        json_str.fields = fields;
        var me = this;
        if (me.fieldItemRelatedPageData) {
            var comboboxs = Ext.getCmp('hrTOcloud_panel').query('FieldSelectorPiker');
            for (var i = 0; i < comboboxs.length; i++) {
                if (comboboxs[i].getValue() && comboboxs[i].getValue() != '') {
                    var obj = {};
                    obj.id = comboboxs[i].itemid;
                    obj.name = comboboxs[i].fieldLabel;
                    fields.push(obj);
                }
            }
        }
        var cloudTOhr = {};
        json_str.cloudTOhr = cloudTOhr;
        var formpanels = Ext.getCmp("fieldMappings").items.items;
        var setMapping = new Array();
        cloudTOhr.setMapping = setMapping;
        for (var i = 0; i < formpanels.length; i++) {
            var formpanel = formpanels[i];
            var obj = {};
            var fieldItems = formpanel.items.items;

            if(Ext.isEmpty(fieldItems[0].getValue())){
                continue;
            }

            obj.cloudset_id = fieldItems[0].id;
            var currentSet = me.assessFieldSet.currentSetMap.get(fieldItems[0].id);
            obj.cloudset_name = currentSet.name;
            obj.hr_set = fieldItems[0].getValue();
            obj.cloud_fields=currentSet.cloud_fields;
            for (var j = 1; j < fieldItems.length; j++) {
                var cloudid = fieldItems[j].itemid;
                var hritemid = fieldItems[j].hritemid;
                var hrvalue = fieldItems[j].value;
                var copyCloudFlds = obj.cloud_fields.concat();
                if (!Ext.isEmpty(hritemid) && !Ext.isEmpty(hrvalue)) {
                    for (var m=0;m<obj.cloud_fields.length;m++){
                        if (obj.cloud_fields[m].id==cloudid){
                            //移除未配置的指标
                            copyCloudFlds.splice(m,1);
                            break;
                        }
                    }
                }
            }
            obj.cloud_fields = copyCloudFlds;
            setMapping.push(obj);
        }
        return json_str;
    },
    //设置同步指标选项卡页
    setfieldItemTabPanel: function () {
        var me = this;
        var return_data = this.fieldItemRelatedPageData;

        //HCM同步云指标
        var hrTOcloudTableTab = {
            id: 'hrTOcloud',
            title: syncSetting.hrTOcloud,
            scrollable: 'y',
            scrollable: 'y',
            items: [{
                xtype: 'fieldset',
                id: 'hrTOcloud_panel',
                scrollable: true,
                padding: '30 0 0 100',
                border: 0
            }]
        };
        Ext.getCmp('tabpanel_2').add(hrTOcloudTableTab);
        if (!return_data.hrTOcloud || !return_data.hrTOcloud.cloud_fields || return_data.hrTOcloud.cloud_fields == 0) {
            Ext.MessageBox.alert(syncSetting.infor_message, syncSetting.no_sync_field);
        } else {
            this.addFieldItemInfo('hrTOcloud', return_data.hrTOcloud.cloud_fields);
        }

        //云同步HCM指标
        var cloudTOhrTableTab = {
            id: 'cloudTOhr',
            title: syncSetting.cloudTOhr,
            layout: 'vbox',
            items: [{
                xtype: "container",
                id: 'cloudTOhr_panel',
                layout: 'vbox',
                flex:1,
                border: 0
            }]
        };
        Ext.getCmp('tabpanel_2').add(cloudTOhrTableTab);
        this.initCloudTOhrPage();

        Ext.getCmp('tabpanel_2').setActiveItem(0);
    },
    //初始化云同步HCM指标页面
    initCloudTOhrPage: function () {
        var cloudTOhr_panel = Ext.getCmp("cloudTOhr_panel");
        var cloudSets = this.fieldItemRelatedPageData.cloudTOhr.cloudset;
        var store = Ext.create('Ext.data.Store', {
            fields: ['id', 'name', 'selected'],
            data: cloudSets,
            filters: [
                function (item) {
                    return item.get("selected") != "true";
                }
            ]
        });
        //创建指标对应界面
        var assessFieldSet = Ext.create("AssessFieldSet", {
            cloudSets: cloudSets
        });
        this.assessFieldSet=assessFieldSet;
        //同步信息集下拉框
        var infosetContainer = Ext.widget("container", {
            layout: 'hbox',
            margin: '5 0 0 10',
            items: [
                {
                    xtype: 'combo',
                    id: "infosetSeletor",
                    margin: '5 0 0 0',
                    width: 230,
                    displayField: 'name',
                    valueField: 'id',
                    store: store,
                    editable: false,
                    queryMode: 'local',
                    emptyText: syncSetting.infoset_emptytext
                }, {
                    xtype: 'image',
                    src: '/images/new_module/nocycleadd.png',
                    width: 30,
                    style: 'cursor:pointer;',
                    height: 30,
                    listeners: {
                        click: {
                            element: 'el',
                            fn: function (a, o) {
                                var infosetSeletor = Ext.getCmp("infosetSeletor");
                                var selected = infosetSeletor.getValue();
                                if (Ext.isEmpty(selected)) {
                                    return;
                                }
                                var iset = store.findRecord("id", selected);
                                iset.set("selected", "true");
                                infosetSeletor.setValue(null);
                                var oldSetId = assessFieldSet.removeSelected();
                                assessFieldSet.addSetTitle(iset.data, true, false, true, oldSetId);
                            }
                        }
                    }
                }
            ]
        });
        cloudTOhr_panel.add(infosetContainer);

        cloudTOhr_panel.add(assessFieldSet.getMainContainer());
    },
    //添加指标项
    addFieldItemInfo: function (tableId, fields) {
        var tableTab = Ext.getCmp(tableId + '_panel');
        for (var i = 0; i < fields.length; i++) {
            var itemid = '';
            var itemdesc = '';
            if (fields[i].connectField && fields[i].connectField.itemid && fields[i].connectField.itemid != '') {
                itemid = fields[i].connectField.itemid;
                itemdesc = fields[i].connectField.itemdesc;
            }
            tableTab.add({
                xtype: 'FieldSelectorPiker',
                source: 'A',
                margin: '5 0 5 0',
                columnWidth: 600,
                itemid: fields[i].id,
                fieldLabel: fields[i].name,
                type: fields[i].type,
                flength: fields[i].length,
                dlength: fields[i].dlength,
                codesetid: fields[i].codesetid,
                required: fields[i].required,
                hritemid: itemid,
                value: itemdesc,
                renderTo: document.body
            });

        }
    },
    //初始化代码关联页面
    initcodeItemRelatedPage: function () {
        var me = this;
        var codeItemRelated = {
            xtype: 'container',
            id: 'step_panel_3',
//				scrollable:true,
            flex: 1,
            layout: {
                type: 'vbox',
                align: 'center'
            },
            items: [{
                xtype: 'tabpanel',
                id: 'tabpanel_3',
                //xus 20/1/7【57308】V77云集成：屏幕比例150%，第三步代码对应，按钮不显示，详见附件！
//                scrollable: true,
                minWidth: 600,
                flex:1
            }, {
                xtype: 'container',
                layout: {
                    type: 'hbox',
                    align: 'middle'
                },
                height: 50,
                items: [{
                    //上一步按钮
                    xtype: 'button',
                    width: 100,
                    style: 'border-radius: 2px;',
                    layout: {
                        align: 'center'
                    },
                    text: syncSetting.prevDesc,
                    listeners: {
                        click: function (btn, e, eOpts) {
                            me.saveCodeConfig();
                            me.selectStep(2);
                        }
                    }
                }, {
                    //清空关联按钮
                    xtype: 'button',
                    margin: '0 0 0 60',
                    width: 120,
                    style: 'border-radius: 2px;',
                    layout: {
                        align: 'center'
                    },
                    text: syncSetting.clean_current_code,
                    listeners: {
                        click: function (btn, e, eOpts) {
                            me.cleanCurrentCode();
                        }
                    }
                }, {
                    //自动关联按钮
                    xtype: 'button',
                    margin: '0 0 0 60',
                    width: 100,
                    style: 'border-radius: 2px;',
                    layout: {
                        align: 'center'
                    },
                    text: syncSetting.auto_connect_code,
                    listeners: {
                        click: function (btn, e, eOpts) {
                            me.autoConnectCode();
                        }
                    }
                }, {
                    //下一步按钮
                    xtype: 'button',
                    margin: '0 0 0 60',
                    width: 100,
                    style: 'border-radius: 2px;',
                    layout: {
                        align: 'center'
                    },
                    text: syncSetting.nextDesc,
                    listeners: {
                        click: function (btn, e, eOpts) {
                            me.saveCodeConfig();
                            me.selectStep(4);
                        }
                    }
                }]
            }]
        };
        Ext.getCmp('mainpanel').add(codeItemRelated);
    },
    //清除此页对应关系
    cleanCurrentCode: function () {
        var codeitems = Ext.getCmp('tabpanel_3').getActiveTab().query('codecomboxfield');
        for (var j = 0; j < codeitems.length; j++) {
            if (codeitems[j].getValue() && codeitems[j].getValue() != '') {
                codeitems[j].setValue('');
            }
        }
    },
    //添加代码选项卡标签
    addCodeCardTabPage: function () {
//		Ext.getCmp('tabpanel_3').removeAll(true);
        var codefields = this.getcodefields();
        if (codefields.length == 0) {
            Ext.MessageBox.alert(syncSetting.infor_message, syncSetting.no_code_field);
            this.unmask();
            return;
        }
        this.getcodeItemRelatedPageData(codefields);
//		this.setCodeTabPage();
    },
    //保存代码对应关系
    saveCodeConfig: function () {
        var fieldsets = Ext.getCmp('tabpanel_3').query('fieldset');
        if (fieldsets.length == 0) {
            return;
        }
        var configData = [];
        for (var i = 0; i < fieldsets.length; i++) {
            var fieldset = fieldsets[i];
            var cloud_codesetid = fieldset.cloud_codesetid;
            var hr_codesetid = fieldset.hr_codesetid;

            var codeitems = fieldset.query('codecomboxfield');
            var matchCode = [];
            for (var j = 0; j < codeitems.length; j++) {
                if (codeitems[j].getValue() && codeitems[j].getValue() != '') {
                    var json = {};
                    json.cloud_codeid = codeitems[j].cloud_codeid;
                    json.cloud_codename = codeitems[j].cloud_codename;
                    json.parentid = codeitems[j].cloud_parentid;
                    json.hr_codeid = codeitems[j].value;
                    json.hr_codename = codeitems[j].rawValue;
                    matchCode.push(json);
                }
            }
            if (matchCode.length > 0) {
                var cjson = {};
                cjson.cloud_codesetid = cloud_codesetid;
                cjson.hr_codesetid = hr_codesetid;
                cjson.matchCode = matchCode;
                configData.push(cjson);
            }

        }
        if (configData.length == 0) {
            return;
        }
        var vo = new HashMap();
        vo.put("transType", "saveMatch");
        vo.put("json_str", configData);
        Rpc({
            functionId: 'SYS00005002', async: false, success: function (res) {
                var resultObj = Ext.decode(res.responseText);
            }
        }, vo);
    },
    //自动关联方法
    autoConnectCode: function () {
        var me = this;
        var vo = new HashMap();
        var config = me.getCloudMatchCode();
        vo.put("transType", "autoMatch");
        vo.put("json_str", config);
        Rpc({
            functionId: 'SYS00005002', async: false, success: function (res) {
                var resultObj = Ext.decode(res.responseText);
                var returnJson = resultObj.returnStr.return_data;
                me.rePutMatchCodePage(returnJson);
            }
        }, vo);
    },
    //自动关联的值 回填到代码页
    rePutMatchCodePage: function (returnJson) {
        var fieldsets = Ext.getCmp('tabpanel_3').query('fieldset');
        for (var i = 0; i < fieldsets.length; i++) {
            var fieldset = fieldsets[i];
            var cloud_codesetid = fieldset.cloud_codesetid;
            if (!returnJson || !returnJson[cloud_codesetid]) {
                continue;
            }
            var hr_codeitems = returnJson[cloud_codesetid];
            var comboboxs = fieldset.query('codecomboxfield');
            for (var j = 0; j < comboboxs.length; j++) {
                var cloud_codeid = comboboxs[j].cloud_codeid;
                if (hr_codeitems[cloud_codeid]) {
                    comboboxs[j].setValue(hr_codeitems[cloud_codeid].hr_codeid + '`' + hr_codeitems[cloud_codeid].hr_codename);
                }
            }
        }
    },
    //获取代码页未设置的代码
    getCloudMatchCode: function () {
        var fieldsets = Ext.getCmp('tabpanel_3').query('fieldset');
        if (fieldsets.length == 0) {
            return;
        }
        var configData = [];
        for (var i = 0; i < fieldsets.length; i++) {
            var fieldset = fieldsets[i];
            var cloud_codesetid = fieldset.cloud_codesetid;
            var hr_codesetid = fieldset.hr_codesetid;

            var comboboxs = fieldset.query('codecomboxfield');
            var cloud_codeitems = [];
            for (var j = 0; j < comboboxs.length; j++) {
                if (!comboboxs[j].getValue() || comboboxs[j].getValue() == '') {
                    var json = {};
                    json.cloud_codeid = comboboxs[j].cloud_codeid;
                    json.cloud_codename = comboboxs[j].cloud_codename;
                    cloud_codeitems.push(json);
                }
            }
            if (cloud_codeitems.length > 0) {
                var cjson = {};
                cjson.cloud_codesetid = cloud_codesetid;
                cjson.hr_codesetid = hr_codesetid;
                cjson.cloud_codeitems = cloud_codeitems;
                configData.push(cjson);
            }

        }
        if (configData.length == 0) {
            return;
        }
        return configData;
    },
    //获取代码页的代码项
    getcodefields: function () {
        var me = this;
        var codefields = "";
        var list = [];
        if (this.fieldItemRelatedPageData) {
            var comboboxs = Ext.getCmp('tabpanel_2').query('FieldSelectorPiker');
            for (var i = 0; i < comboboxs.length; i++) {
                if (comboboxs[i].getValue() && comboboxs[i].getValue() != '' && comboboxs[i].codesetid != null && comboboxs[i].codesetid != '0') {
                    var map = new HashMap();
                    if(comboboxs[i].codesetid.indexOf(codefields)>-1){
                        if (codefields.length != 0) {
                            codefields += ",";
                        }
                        codefields += comboboxs[i].codesetid;
                    }

                    map.put("codesetid", comboboxs[i].codesetid);
                    map.put("fielditemid", comboboxs[i].hritemid);
                    list.push(map);
                }
            }
        }
        return list;
    },
    //获取代码关联页面数据
    getcodeItemRelatedPageData: function (codefields) {
        var me = this;
        var vo = new HashMap();
        vo.put("transType", "loadMatch");
        vo.put("appId", Ext.String.trim(Ext.getCmp('user_appId').getValue()));
        vo.put("tenantId", Ext.String.trim(Ext.getCmp('user_tenantID').getValue()));
        vo.put("appSecret", Ext.String.trim(Ext.getCmp('user_AppSecret').getValue()));
        vo.put("codefields", codefields);
        Rpc({
            functionId: 'SYS00005002', async: true, success: function (res) {
                me.unmask();
                var resultObj = Ext.decode(res.responseText);
                me.codeItemRelatedPageData = resultObj.returnStr.return_data;
                me.setCodeTabPage(me.codeItemRelatedPageData);
            }
        }, vo);
    },
    //设置代码关联选项卡页面
    setCodeTabPage: function (codes) {
        for (var i = 0; i < codes.length; i++) {
            var tableTab = {
                xtype: 'container',
                scrollable: true,
                title: codes[i].name,
                items: [{
                    xtype: 'fieldset',
                    id: codes[i].id + '_code_panel',
                    cloud_codesetid: codes[i].id,
                    hr_codesetid: codes[i].codesetid,
                    activeItem: 0,
                    padding: '30 0 0 100',
                    border: 0
                }]
            };
            Ext.getCmp('tabpanel_3').add(tableTab);
            this.addCodeItemsInfo(Ext.getCmp(codes[i].id + '_code_panel'), codes[i]);
        }
        if (codes.length > 0) {
            Ext.getCmp('tabpanel_3').setActiveItem(0);
        }
    },
    //添加代码详情页面
    addCodeItemsInfo: function (fieldset, code) {
        var codeitems = code.codeitems;
        if (!codeitems) {
            return;
        }
        for (var i = 0; i < codeitems.length; i++) {
            var value = '';
            if (codeitems[i].hr_codeid && codeitems[i].hr_codename) {
                value = codeitems[i].hr_codeid + '`' + codeitems[i].hr_codename;
            }
            fieldset.add({
                xtype: 'codecomboxfield',
                codesetid: code.codesetid,
                cloud_codeid: codeitems[i].cloud_codeid,
                cloud_codename: codeitems[i].cloud_codename,
                cloud_parentid: codeitems[i].parentid,
                fieldLabel: codeitems[i].cloud_codename,
                value: value
            });
        }
    },
    //获取同步设置页面数据
    getsyncSettingPageData: function () {
        var me = this;
        var vo = new HashMap();
        vo.put("transType", "loadSyncConfig");
        Rpc({
            functionId: 'SYS00005001', async: false, success: function (res) {
                var resultObj = Ext.decode(res.responseText);
                me.syncSettingPageData = resultObj.returnStr.return_data;
            }
        }, vo);
    },
    //初始化同步设置页面
    initsyncSettingPage: function () {
        var me = this;
        this.getsyncSettingPageData();
        //是否启用的选项
        var isUsedStates = Ext.create('Ext.data.Store', {
            fields: ['abbr', 'name'],
            data: [
                {"abbr": "0", "name": syncSetting.selection_no},
                {"abbr": "1", "name": syncSetting.selection_yes}
            ]
        });
        //同步频率的选项
        var syncFreqStates = Ext.create('Ext.data.Store', {
            fields: ['abbr', 'name'],
            data: [
                {"abbr": "day", "name": syncSetting.selection_day},
                {"abbr": "week", "name": syncSetting.selection_week}
            ]
        });
        //同步时间(周)的选项
        var syncTimeWeekStates = Ext.create('Ext.data.Store', {
            fields: ['abbr', 'name'],
            data: [
                {"abbr": "2", "name": syncSetting.selection_Monday},
                {"abbr": "3", "name": syncSetting.selection_Tuesday},
                {"abbr": "4", "name": syncSetting.selection_Wednesday},
                {"abbr": "5", "name": syncSetting.selection_Thursday},
                {"abbr": "6", "name": syncSetting.selection_Friday},
                {"abbr": "7", "name": syncSetting.selection_Saturday},
                {"abbr": "1", "name": syncSetting.selection_Sunday}
            ]
        });
        var weekTime = '1';
        var dayTime = '2:00';
        var dayTime_hour = 2;
        var dayTime_min = 0;
        if (me.syncSettingPageData.frequency && me.syncSettingPageData.frequency.type) {
            if (me.syncSettingPageData.frequency.type == 'week' && me.syncSettingPageData.frequency.week) {
                weekTime = me.syncSettingPageData.frequency.week;
            }
            dayTime = me.syncSettingPageData.frequency.time;
            var timeArray = dayTime.split(':');
            dayTime_hour = parseInt(timeArray[0]);
            dayTime_min = parseInt(timeArray[1]);
        }

        //历史选中人员库
        me.step_panel_5dblists = "";
        for (var i = 0; i < me.syncSettingPageData.dblist.length; i++) {
            if (me.syncSettingPageData.dblist[i].checked) {
                if (me.step_panel_5dblists.length > 0) {
                    me.step_panel_5dblists += ",";
                }
                me.step_panel_5dblists += me.syncSettingPageData.dblist[i].pre;
            }
        }
        var syncSettingPage = {
            xtype: 'container',
            id: 'step_panel_4',
            items: [{
                xtype: 'fieldset',
                width: 550,
                height: '100%',
                border: 0,
                items: [
                    {
                        //人员库
                        xtype: 'checkboxgroup',
                        id: 'dbname',
                        itemid: 'dbname',
                        fieldLabel: syncSetting.dbnameDesc,
                        columns: 3,
                        vertical: true,
                        labelWidth: 100,
                        labelSeparator: '',
                        width: 500,
                        margin: '10 0 10 0',
                        items: me.syncSettingPageData.dblist
                    },
                    {
                        //同步频率
                        xtype: 'combobox',
                        fieldLabel: syncSetting.syncFrenq,
                        id: 'syncfrenq',
                        itemId: 'syncfrenq',
                        store: syncFreqStates,
                        labelWidth: 100,
                        width: 350,
                        margin: '10 0 10 0',
                        queryMode: 'local',
                        displayField: 'name',
                        valueField: 'abbr',
                        value: me.syncSettingPageData.frequency && me.syncSettingPageData.frequency.type ? me.syncSettingPageData.frequency.type : 'day',
                        listeners: {
                            afterrender: function (comb, eOpts) {
                                me.changeSyncTimeComp(comb.value);
                            },
                            change: function (comb, newValue, oldValue, eOpts) {
                                me.changeSyncTimeComp(newValue);
                            }
                        }
                    },
                    {
                        //同步时间(周)
                        xtype: 'combobox',
                        id: 'synctime_week',
                        itemId: 'synctime_week',
                        padding: '0 0 0 105',
                        store: syncTimeWeekStates,
                        hidden: true,
                        width: 245,
                        margin: '10 0 10 0',
                        queryMode: 'local',
                        displayField: 'name',
                        valueField: 'abbr',
                        value: weekTime
                    },
                    {
                        //同步时间(日)
                        xtype: 'container',
                        id: 'synctime_day',
                        itemId: 'synctime_day',
                        width: '100%',
                        margin: '10 0 10 0',
                        height: 50,
                        layout: 'hbox',
                        items: [{
                            xtype: 'container',
                            width: 100,
                            padding: '5 0 0 0',
                            html: syncSetting.syncTime
                        }, {
                            xtype: 'numberfield',
                            width: 50,
                            margin: '0 0 0 5',
                            value: dayTime_hour,
                            validator: function (val) {
                                return (val > 23 || val < 0) ? syncSetting.time_out_limit + '0~23' : true;
                            },
                            listeners: {
                                spindown: function (me, eOpts) {
                                    if (me.getValue() <= 0) {
                                        me.setValue(24);
                                    }
                                },
                                spinup: function (me, eOpts) {
                                    if (me.getValue() >= 23) {
                                        me.setValue(-1);
                                    }
                                }
                            }
                        },

                            {
                                xtype: 'container',
                                margin: '5 0 0 10',
                                width: 20,
                                html: ':'
                            }
                            , {
                                xtype: 'numberfield',
                                value: dayTime_min,
                                width: 50,
                                validator: function (val) {
                                    return (val > 59 || val < 0) ? syncSetting.time_out_limit + '0~59' : true;
                                },
                                listeners: {
                                    spindown: function (me) {
                                        if (me.getValue() <= 0) {
                                            me.setValue(60);
                                        }
                                    },
                                    spinup: function (me) {
                                        if (me.getValue() >= 59) {
                                            me.setValue(-1);
                                        }
                                    },
                                }
                            }
                        ]
                    }]
            }, {
                xtype: 'container',
                width: '100%',
                items: [{
                    //上一步按钮
                    xtype: 'button',
                    width: 100,
                    style: 'border-radius: 2px;',
                    layout: {
                        align: 'center'
                    },
                    text: syncSetting.prevDesc,
                    listeners: {
                        click: function () {
                            me.selectStep(3);
                        }
                    }
                }, {
                    //启用按钮
                    xtype: 'button',
                    id: 'startpauseBtn',
                    itemId: 'startpauseBtn',
                    margin: '0 0 0 100',
                    width: 100,
                    style: 'border-radius: 2px;',
                    layout: {
                        align: 'center'
                    },
                    syncIsUsed: me.syncSettingPageData.used,
                    text: me.syncSettingPageData.used == '1' ? syncSetting.stopped_btn_text : syncSetting.used_btn_text,
                    listeners: {
                        click: function (btn, e, eOpts) {
                            if (btn.syncIsUsed == '1') {
                                btn.syncIsUsed = '0';
                            } else {
                                btn.syncIsUsed = '1';
                            }
                            var configData = me.getSaveValues();
                            var vo = new HashMap();
                            vo.put("transType", "save");
                            vo.put("isSave", false);
                            vo.put("json_str", configData);
                            Rpc({
                                functionId: 'SYS00005001', async: false, success: function (res) {
                                    var resultObj = Ext.decode(res.responseText);
                                    var flag = resultObj.returnStr.return_code;
                                    //保存成功弹窗
                                    if (flag == "success") {
                                        if (btn.syncIsUsed == '1') {
                                            //启用成功
                                            Ext.MessageBox.alert(syncSetting.infor_message, syncSetting.used_btn_text + syncSetting.success);
                                            btn.setText(syncSetting.stopped_btn_text);
                                        } else {
                                            //暂停成功
                                            Ext.MessageBox.alert(syncSetting.infor_message, syncSetting.stopped_btn_text + syncSetting.success);
                                            btn.setText(syncSetting.used_btn_text);
                                        }
                                    } else {
                                        if (btn.syncIsUsed == '1') {
                                            //暂停失败
                                            Ext.MessageBox.alert(syncSetting.infor_message, syncSetting.stopped_btn_text + syncSetting.fail);
                                            btn.syncIsUsed = '0';
                                        } else {
                                            //启用失败
                                            Ext.MessageBox.alert(syncSetting.infor_message, syncSetting.used_btn_text + syncSetting.fail);
                                            btn.syncIsUsed = '1';
                                        }
                                    }

                                }
                            }, vo);
                        }
                    }
                }, {
                    //保存按钮
                    xtype: 'button',
                    margin: '0 0 0 100',
                    width: 100,
                    style: 'border-radius: 2px;',
                    layout: {
                        align: 'center'
                    },
                    text: syncSetting.saveDesv,
                    listeners: {
                        click: function () {
                            //保存之后可以点击图片切换页数
                            me.saveSettingConfig();
                        }
                    }
                }]
            }]
        };
        Ext.getCmp('mainpanel').add(syncSettingPage);
    },
    //改变同步时间组件
    changeSyncTimeComp: function (type) {
        if (type == 'week') {
            Ext.getCmp('synctime_week').setHidden(false);
        } else {
            Ext.getCmp('synctime_week').setHidden(true);
            Ext.getCmp('synctime_day').setHidden(false);
        }
    },
    //选中选项栏
    selectStep: function (index) {
        //改变选项栏选中
        if (this.activeItem == index) {
            return;
        }
        var buttonId = 'step_button_';
        if (this.activeItem != 0) {
            Ext.getCmp(buttonId + this.activeItem).setSrc('images/0' + this.activeItem + '.png');
            Ext.getCmp(buttonId + index).setSrc('images/' + index + '_sld.png');
        }

        var hiddenItem = this.activeItem;
        this.activeItem = index;
        //改变页面主体
        this.changeBodyPage(index, hiddenItem);
    },
    //改变页面主体
    changeBodyPage: function (index, hiddenItem) {
        if (hiddenItem != 0) {
            Ext.getCmp('step_panel_' + hiddenItem).setHidden(true);
        }
        var stepPanel = Ext.getCmp('step_panel_' + index);
        if (index == 1) {
            //参数设置页面
            if (!stepPanel) {
                this.initParamSettingPage();
            } else {
                this.paramSettingPageData.appId = Ext.String.trim(Ext.getCmp('user_appId').getValue());
                this.paramSettingPageData.tenantId = Ext.String.trim(Ext.getCmp('user_tenantID').getValue());
                this.paramSettingPageData.appSecret = Ext.String.trim(Ext.getCmp('user_AppSecret').getValue());
            }
        } else if (index == 2) {
            //指标关联
            if (!stepPanel) {
                this.initfieldItemRelatedPage();
            } else {
                var appId = this.paramSettingPageData.appId;
                var tenantId = this.paramSettingPageData.tenantId;
                var appSecret = this.paramSettingPageData.appSecret;

                var user_appId = Ext.String.trim(Ext.getCmp('user_appId').getValue());
                var user_tenantID = Ext.String.trim(Ext.getCmp('user_tenantID').getValue());
                var user_AppSecret = Ext.String.trim(Ext.getCmp('user_AppSecret').getValue());
                if (appId != user_appId || tenantId != user_tenantID || appSecret != user_AppSecret) {
                    Ext.getCmp('mainpanel').remove(stepPanel);
                    this.initfieldItemRelatedPage();
                }
            }
        } else if (index == 3) {
            this.mask(syncSetting.LoadingMaskText);
            //代码关联
            if (!stepPanel) {
                this.initcodeItemRelatedPage();
            } else {
                Ext.getCmp('tabpanel_3').removeAll(true);
            }
            this.addCodeCardTabPage();
        } else if (index == 4) {
            //同步设置
            if (!stepPanel) {
                this.initsyncSettingPage();
                return;
            }
        }
        Ext.getCmp('step_panel_' + index).setVisible(true);
    },
    //初始化第一页
    initCompPage: function () {
        this.selectStep(1);
    },
    //保存设置参数
    saveSettingConfig: function () {
        var me = this;
        var configData = this.getSaveValues();
        if (configData.dbnames.length == 0) {
            Ext.MessageBox.alert(syncSetting.infor_message, syncSetting.no_nbases);
            return;
        }
        //判断是否有其他外部系统同时使用数据视图
        if (this.syncSettingPageData.isRemind == true && this.step_panel_5dblists != configData.dbnames) {
            Ext.MessageBox.confirm(syncSetting.infor_message, syncSetting.change_nbase_remaind, function (optional) {
                if (optional != 'yes') {
                    var dbnamechild = Ext.getCmp('dbname').items.items;
                    for (var i = 0; i < dbnamechild.length; i++) {
                        if (me.step_panel_5dblists.indexOf(dbnamechild[i].pre) > -1) {
                            dbnamechild[i].setValue(true);
                        } else {
                            dbnamechild[i].setValue(false);
                        }
                    }
                    configData.dbnames = me.step_panel_5dblists;

                    var vo = new HashMap();
                    vo.put("transType", "save");
                    vo.put("isSave", true);
                    vo.put("json_str", configData);
                    Rpc({
                        functionId: 'SYS00005001', async: true, success: function (res) {
                            var resultObj = Ext.decode(res.responseText);
                            //操作执行标记
                            var flag = resultObj.returnStr.return_code;
                            //保存成功弹窗
                            if (flag == "success") {
                                Ext.MessageBox.alert(syncSetting.infor_message, syncSetting.save_success);
                            } else {
                                Ext.MessageBox.alert(syncSetting.infor_message, syncSetting.save_failed);
                            }
                        }
                    }, vo);
                } else {
                    var vo = new HashMap();
                    vo.put("transType", "save");
                    vo.put("isSave", true);
                    vo.put("json_str", configData);
                    Rpc({
                        functionId: 'SYS00005001', async: true, success: function (res) {
                            var resultObj = Ext.decode(res.responseText);
                            var flag = resultObj.returnStr.return_code;
                            //保存成功弹窗
                            if (flag == "success") {
                                Ext.MessageBox.alert(syncSetting.infor_message, syncSetting.save_success);
                            } else {
                                Ext.MessageBox.alert(syncSetting.infor_message, syncSetting.save_failed);
                            }
                        }
                    }, vo);
                }

            });
        } else {
            var vo = new HashMap();
            vo.put("transType", "save");
            vo.put("isSave", true);
            vo.put("json_str", configData);
            Rpc({
                functionId: 'SYS00005001', async: true, success: function (res) {
                    var resultObj = Ext.decode(res.responseText);
                    var flag = resultObj.returnStr.return_code;
                    //保存成功弹窗
                    if (flag == "success") {
                        Ext.MessageBox.alert(syncSetting.infor_message, syncSetting.save_success);
                    } else {
                        Ext.MessageBox.alert(syncSetting.infor_message, syncSetting.save_failed);
                    }
                }
            }, vo);
        }

    },
    //获取保存参数值
    getSaveValues: function () {
        var configData = new HashMap();
        //第一步的值
        configData.put('appId', Ext.String.trim(Ext.getCmp('user_appId').getValue()));
        configData.put('tenantId', Ext.String.trim(Ext.getCmp('user_tenantID').getValue()));
        configData.put('appSecret', Ext.String.trim(Ext.getCmp('user_AppSecret').getValue()));
        //第四步的值
        var fieldset4 = Ext.getCmp('step_panel_4').items.items[0];
        var used = Ext.getCmp('step_panel_4').items.items[1].child('#startpauseBtn').syncIsUsed;
        configData.put('used', used);

        var dbname = fieldset4.child('#dbname');
        var checkfields = dbname.items.items;
        var dbnames = "";
        for (var i = 0; i < checkfields.length; i++) {
            if (checkfields[i].getValue()) {
                if (dbnames.length > 0) {
                    dbnames += ",";
                }
                dbnames += checkfields[i].pre;
            }
        }
        var type = fieldset4.child('#syncfrenq').getValue();
        var week = '';
        if (type == 'week') {
            week = fieldset4.child('#synctime_week').getValue();
        }
        var contain = fieldset4.child('#synctime_day');
        var numberfields = contain.query('numberfield');
        var hour = numberfields[0].getValue();
        var min = numberfields[1].getValue();
        time = hour + ':' + min;

        var frequency = new HashMap();
        frequency.put('type', type);
        frequency.put('week', week);
        frequency.put('time', time);
        configData.put('frequency', frequency);
        configData.put('dbnames', dbnames);
        //第二步的值
        var tables = this.getTablesSaveConfig();
        if (tables.length > 0) {
            configData.put('tables', tables);
        }
        //云同步HCM指标配置信息
        var setMapping = this.getCloud2HcmData();
        if (setMapping.length>0){
            var cloudTOhr=new HashMap();
            cloudTOhr.put("setMapping",setMapping);
            configData.put('cloudTOhr', cloudTOhr);
        }
        return configData;
    },
    //获得第二步中HCM同步云的值
    getTablesSaveConfig: function () {
        //获得HCM同步云指标
        var tables = [];
        var json = {};
        var fields = [];
        var tabpanel1 = Ext.getCmp('hrTOcloud_panel');
        var comboboxs = tabpanel1.query('FieldSelectorPiker');
        for (var j = 0; j < comboboxs.length; j++) {
            if (comboboxs[j].getValue() && comboboxs[j].getValue() != '') {
                var itemjson = {};
                var itemid = comboboxs[j].config.itemid;
                var itemname = comboboxs[j].config.fieldLabel;
                var type = comboboxs[j].config.type;
                var fieldlength = comboboxs[j].config.flength;
                var dlength = comboboxs[j].config.dlength;
                var codesetid = comboboxs[j].config.codesetid;
                var hritemid = comboboxs[j].hritemid;
                var hritemdesc = comboboxs[j].getValue();
                itemjson.id = itemid;
                itemjson.name = itemname;
                itemjson.type = type;
                itemjson.fieldlength = fieldlength;
                itemjson.dlength = dlength;
                itemjson.codesetid = codesetid;
                var connectField = {"itemid": hritemid, "itemdesc": hritemdesc};
                itemjson.connectField = connectField;
                fields.push(itemjson);
            }
        }
        if (fields.length > 0) {
            json.fields = fields;
            tables.push(json);
        }
        return tables;
    },
    //获得第二步中云同步HCM指标配置信息
    getCloud2HcmData: function () {
        var me = this;
        var setListTitle = Ext.getCmp("setListTitle").items.items;
        var setMapping = new Array();
        for (var i = 0; i < setListTitle.length; i++) {
            var setTitle = setListTitle[i];
            var obj = {};
            var setId = setTitle.id.substring(9);
            var formpanel = Ext.getCmp("formpanel"+setId);
            var fieldItems = formpanel.items.items;

            if(Ext.isEmpty(fieldItems[0].getValue())){
                continue;
            }
            var setTitlePanel = Ext.getCmp("container"+setId);
            obj.cloudset_id = fieldItems[0].id;
            obj.isChildSet = fieldItems[0].isChildSet;
            var currentSet = me.assessFieldSet.currentSetMap.get(fieldItems[0].id);
            obj.set_type=setTitlePanel.type;
            obj.cloudset_name = currentSet.name;
            obj.hr_set = fieldItems[0].getValue();
            obj.cloud_fields=currentSet.cloud_fields;
            var copyCloudFlds = obj.cloud_fields.concat();
            for (var j = 1; j < fieldItems.length; j++) {
                var cloudid = fieldItems[j].itemid;
                var hritemid = fieldItems[j].hritemid;
                var hrvalue = fieldItems[j].value;
                if (Ext.isEmpty(hritemid) || Ext.isEmpty(hrvalue)) {
                    for (var m=0;m<copyCloudFlds.length;m++){
                        if (copyCloudFlds[m].id==cloudid){
                            //移除未配置的指标
                            copyCloudFlds.splice(m,1);
                            break;
                        }
                    }
                }else{
                    for (var m=0;m<copyCloudFlds.length;m++){
                        var cfield = copyCloudFlds[m];
                        if (copyCloudFlds[m].id==cloudid){
                            cfield.connectField={};
                            cfield.connectField.itemid= hritemid;
                            cfield.connectField.itemdesc= hrvalue;
                            break;
                        }
                    }
                }
            }
            obj.cloud_fields = copyCloudFlds;
            setMapping.push(obj);
        }
        return setMapping;
    },
    //保存第一页数据
    saveConfigValues: function () {
        var configData = new HashMap();
        configData.put('appId', Ext.String.trim(Ext.getCmp('user_appId').getValue()));
        configData.put('tenantId', Ext.String.trim(Ext.getCmp('user_tenantID').getValue()));
        configData.put('appSecret', Ext.String.trim(Ext.getCmp('user_AppSecret').getValue()));
        var vo = new HashMap();
        vo.put("transType", "saveFirstPage");
        vo.put("json_str", configData);
        Rpc({
            functionId: 'SYS00005001', async: false, success: function (res) {
                var resultObj = Ext.decode(res.responseText);
            }
        }, vo);
    }
});