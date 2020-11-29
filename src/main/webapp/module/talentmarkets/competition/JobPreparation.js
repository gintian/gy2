Ext.define("Talentmarkets.competition.JobPreparation", {
    extend: "Ext.panel.Panel",
    layout: 'fit',
    border: false,
    initComponent: function () {
        this.callParent();
        jobPreparation = this;
        Ext.util.CSS.createStyleSheet(".scheme-selected-cls{text-decoration:underline;margin-left:10px;margin-right:10px;}", "underline");
        this.init();
    },
    init: function () {
        //alert("123");
        var map = new HashMap();
        map.put("operateType", "jobs");
        map.put("queryMethod", "-1");
        Rpc({functionId: 'TM000000003', success: jobPreparation.renderData, scope: jobPreparation, async: false}, map);
    },
    renderData: function (response) {
        var result = Ext.decode(response.responseText);
        if (result.return_code == 'success') {
            var gridConfig = result.gridConfig;
            var configObj = Ext.decode(gridConfig);
            configObj.openColumnQuery = true;
            jobPreparation.tableObj = new BuildTableObj(configObj);
            var toolbar = jobPreparation.queryToolbar();
            jobPreparation.tableObj.insertItem(toolbar, 0);
            jobPreparation.add(jobPreparation.tableObj.getMainPanel());
            //jobPreparation.addEvent();
        } else {
            var return_msg = eval(result.return_msg);
            var errorContainer = jobPreparation.getErrorContainer();
            var tipsComponent = errorContainer.query('#errorComponent')[0];
            tipsComponent.setHtml('<span style="font-size: 14px;color: #5c5c5c">' + return_msg + '</span>');
            jobPreparation.add(errorContainer);
        }
    },
    toBack: function () {//返回
        window.location.href = "CompetitionJobs.html";
    },
    doTableScheme: function () {//栏目设置
        var tableObj = jobPreparation.tableObj;
        tableObj.controller.doTableScheme();
    },
    queryToolbar: function () {//查询方案工具条
        var toolbar = Ext.create('Ext.toolbar.Toolbar', {
            border: 0,
            id: "toolbar",
            dock: 'top',
            items: [{
                xtype: 'component',
                html: tm.contendPos.queryPlan + "：",
                style: 'margin-left:5px',
            }, {
                xtype: 'component',
                html: '<span style="color:#1B4A98;cursor: pointer" onclick="javascript:void(0);jobPreparation.queryPlan(\'all\')">' + tm.contendPos.status.all + '</span>',
                itemId: 'all',
                style: 'margin-left:5px;margin-right:5px;',
                cls: 'scheme-selected-cls'
            }, {
                xtype: 'component',
                itemId: 'miss',
                html: '<span style="color:#1B4A98;cursor: pointer" onclick="javascript:void(0);jobPreparation.queryPlan(\'miss\')">' + tm.contendPos.status.missEdit + '</span>',
                style: 'margin-left:5px;margin-right:5px;'
            }]
        });
        return toolbar;
    },
    queryPlan: function (value) {//查询方案
        var selectComponent = Ext.ComponentQuery.query('component[cls=scheme-selected-cls]')[0];
        if (selectComponent.itemId != value) {
            selectComponent.removeCls('scheme-selected-cls');
            selectComponent.cls = '';
            var schemeLabel = Ext.ComponentQuery.query('#' + value)[0];
            schemeLabel.addCls('scheme-selected-cls');
            schemeLabel.cls = "scheme-selected-cls";
        }
        var map = new HashMap();
        if (value == 'all') {//全部
            map.put("queryMethod", "-1");
        } else {
            map.put("queryMethod", "0");//缺编
        }
        map.put("operateType", "search");
        Rpc({
            functionId: 'TM000000003', success: function (form, action) {
                var result = Ext.decode(form.responseText);
                if (result.return_code == 'success') {
                    Ext.getCmp('jobPreparationTable_tablePanel').getStore().reload();
                } else {
                    Ext.showAlert(result.return_msg);
                }
            }
        }, map);
    },
    publishApplication: function () {//发布申请
        var grid = Ext.getCmp('jobPreparationTable_tablePanel');
        if (grid.getSelectionModel().getSelection().length <= 0) {
            Ext.showAlert(tm.contendPos.msg.selectPost);
            return;
        }
        /*//验证岗位发布情况
        var e01a1Arr = new Array();
        for (var i = 0; i < grid.getSelectionModel().getSelection().length; i++) {
            e01a1Arr.push(grid.getSelectionModel().getSelection()[i].data.e01a1.split("`")[0]);
        }
        var map = new HashMap();
        map.put("operateType","checkPostStatus");
        map.put("e01a1Arr",e01a1Arr);
        var returnCode = '';
        var msg = '';
        Rpc({
            functionId: 'TM000000003', success: function (form, action) {
                var result = Ext.decode(form.responseText);
                returnCode = result.return_code;
                if(returnCode == 'fail'){
                    msg = eval(result.return_msg);
                }
            }, scope: this, async: false
        }, map);
        if(returnCode == 'fail'){
            Ext.Msg.alert(tm.contendPos.msg.title,msg);
            return;
        }*/

        jobPreparation.publishBySelect(grid);
    },
    publishBySelect: function (grid) {
        var selectDataArray = new Array();
        for (var i = 0; i < grid.getSelectionModel().getSelection().length; i++) {
            selectDataArray.push(grid.getSelectionModel().getSelection()[i].data);
        }
        Ext.Msg.confirm(tm.contendPos.msg.title, tm.contendPos.msg.postApplication, function (btn) {
            if (btn == "yes") {
                var map = new HashMap();
                map.put("records", selectDataArray);
                map.put("templateType", "releasePostTemplate");
                Rpc({
                    functionId: 'TM000000009', success: function (form) {
                        var result = Ext.decode(form.responseText);
                        var return_msg_code = result.return_msg_code;
                        var return_data = result.return_data;
                        if (result.return_code == 'success') {
                            var tabid = return_data.tabid;
                            var templateObj = new Object();
                            templateObj.tab_id = tabid;
                            // templateObj.tab_id = tabid;
                            templateObj.return_flag = "0";
                            templateObj.module_id = "1";
                            templateObj.approve_flag = "1";
                            templateObj.callBack_init = "jobPreparation.tempFunc";
                            templateObj.callBack_close = "jobPreparation.goBack";
                            Ext.require('TemplateMainUL.TemplateMain', function () {
                                Ext.create("TemplateMainUL.TemplateMain", {templPropety: templateObj});
                            });
                        } else {
                            if (return_msg_code == 'noSetingData') {
                                Ext.Msg.alert(tm.contendPos.msg.title, tm.contendPos.pelaseSeting);
                            } else if (return_msg_code == 'notSetReleasePostTemplatePlan') {
                                Ext.Msg.alert(tm.contendPos.msg.title, tm.contendPos.jobNoSetPlan);
                            } else if (return_msg_code == 'initTempTemplateTableError') {
                                Ext.Msg.alert(tm.contendPos.msg.title, tm.contendPos.initTempTemplateTableError);
                            }else {
                                Ext.Msg.alert(tm.contendPos.msg.title, return_msg_code);
                            }

                        }
                    }, async: false
                }, map);
            }
        });
    },
    tempFunc: function () {
        jobPreparation.removeAll();
        jobPreparation.add(templateMain_me.mainPanel);
    },
    goBack: function () {
        window.location.reload();
    },
    getErrorContainer: function () {//未配置时显示页面
        var errorContainer = Ext.create('Ext.container.Container', {
            //hidden: true,
            itemId: 'errorContainer',
            width: '100%',
            margin: '25 0 0 0',
            layout: {
                type: 'vbox',
                align: 'center'
            },
            items: [
                {
                    xtype: 'image',
                    width: 142,
                    height: 142,
                    src: '../images/notconfig.png'
                }, {
                    xtype: 'component',
                    itemId: 'errorComponent',
                    html: ''
                }
            ]
        });
        return errorContainer;
    }

});