Ext.define('Declare.DeclareList', {
    requires: ['SYSF.FileUpLoad', 'Declare.DeclareInfor', 'Declare.DeclareRelationship', 'EHR.stepview.StepView'],
    extend: 'Ext.panel.Panel',
    id: 'declarelist',
    //title:gz.label.zxDeclareTitle,//个税专项附加申报
    declareList: '',
    globalDeclareType: '-1',//默认的专项申请类型为查询全部
    globalApproveState: '-1',//默认的审核状态全部
    approveStateArray: [],//用于存放选中数据的审批状态  to 对按钮操作的数据进行限制
    //layout:{type:'vbox',align:'stretch'},
    flex: 1,
    layout: 'fit',
    bodyPadding: '0 0 0 5',
    initComponent: function () {
        //初始化框架参数
        declareList = this;
        this.callParent();
        declareList.loadData();
    },
    loadData: function () {
        var vo = new HashMap();
        vo.put("operateType", "search");
        vo.put("declareType", this.globalDeclareType);
        vo.put("approveState", this.globalApproveState);
        Rpc({functionId: 'GZ00000701', success: this.rendData, scope: this}, vo);
    },
    rendData: function (response) {
        Ext.util.CSS.createStyleSheet(".scheme-selected-cls{text-decoration:underline;margin-left:10px;margin-right:10px;}", "underline");
        var me = this;
        var resData = Ext.decode(response.responseText);
        var return_code = resData.return_code;
        if (return_code == 'fail') {//失败的话弹出提示信息,不再往下执行
            var return_msg = resData.return_msg;
            Ext.Msg.alert(gz.msg.zxDeclareTitle, return_msg);
            return;
        }
        var tableConfig = resData.return_data.tableconfig;
        var configObj = Ext.decode(tableConfig);
        configObj.openColumnQuery = true;
        var customtools = Ext.widget('image', {
            src: '/module/serviceclient/images/setting.png',
            height: 22,
            width: 22,
            id: 'declareSetting',
            style: 'cursor:pointer',
            listeners: {
                element: 'el',
                click: function (e) {
                    Ext.create('Declare.DeclareRelationship').show();
                },
                mouseover: function (e) {
                    Ext.QuickTips.init();
                    Ext.QuickTips.register({
                        target: 'declareSetting',
                        text: gz.label.zxdeclare.settingText
                    })
                }
            }
        })

        var tableObj = new BuildTableObj(configObj);
        var toolbar = Ext.create('Ext.toolbar.Toolbar', {
            border: 0,
            id: 'toolbar',
            dock: 'top',
            items: [{
                xtype: 'label',
                text: gz.label.zxDeclareSearch,//查询方案
                style: 'margin-left:5px'
            }, {
                xtype: 'label',//全部分类
                html: '<a href="javascript:declareList.searchDeclareType(declareList.zxDeclareType.all)">' + gz.label.zxDeclareTypeAll + '</a>',
                id: 'typeAll',
                style: 'margin-left:5px;margin-right:10px;',
                cls: 'scheme-selected-cls'
            }, {
                xtype: 'label',//子女教育
                id: 'type01',
                html: '<a href="javascript:declareList.searchDeclareType(declareList.zxDeclareType.child_edu)">' + gz.label.zxDeclareTypeChildEdu + '</a>',
                style: 'margin-left:5px;margin-right:5px;'
            }, {
                xtype: 'label',//继续教育
                id: 'type02',
                html: '<a href="javascript:declareList.searchDeclareType(declareList.zxDeclareType.continu_edu)">' + gz.label.zxDeclareTypeContinuEdu + '</a>',
                style: 'margin-left:5px;margin-right:5px;'
            }, {
                xtype: 'label',//住房租金
                html: '<a href="javascript:declareList.searchDeclareType(declareList.zxDeclareType.housing_rent)">' + gz.label.zxDeclareTypeHouseRent + '</a>',
                id: 'type03',
                style: 'margin-left:5px;margin-right:5px;'
            }, {
                xtype: 'label',//房贷利息
                id: 'type04',
                html: '<a href="javascript:declareList.searchDeclareType(declareList.zxDeclareType.interest_expense)">' + gz.label.zxDeclareTypeInterestExpense + '</a>',
                style: 'margin-left:5px;margin-right:5px;'
            }, {
                xtype: 'label',//大病医疗
                id: 'type05',
                html: '<a href="javascript:declareList.searchDeclareType(declareList.zxDeclareType.illness_medicalcare)">' + gz.label.zxDeclareTypeIllnessMedicalcare + '</a>',
                style: 'margin-left:5px;margin-right:5px;',
                hidden: true
            }, {
                xtype: 'label',//赡养父母
                id: 'type06',
                html: '<a href="javascript:declareList.searchDeclareType(declareList.zxDeclareType.support_elderly)">' + gz.label.zxDeclareTypeSupportElderly + '</a>',
                style: 'margin-left:5px;margin-right:5px;'
            }, '-', {
                xtype: 'label',//全部状态
                id: 'stateAll',
                html: '<a href="javascript:declareList.searchApproveState(declareList.approveState.all)">' + gz.label.approveStateAll + '</a>',
                style: 'margin-left:5px;margin-right:5px;',
                cls: 'scheme-selected-cls'
            }, {
                xtype: 'label',//审核中
                id: 'state02',
                html: '<a href="javascript:declareList.searchApproveState(declareList.approveState.inaudit)">' + gz.label.approveStateInaudit + '</a>',
            }, {
                xtype: 'label',//通过
                id: 'state03',
                html: '<a href="javascript:declareList.searchApproveState(declareList.approveState.adopt)">' + gz.label.approveStateAdopt + '</a>',
                style: 'margin-left:5px;margin-right:5px;'
            }, {
                xtype: 'label',//未通过
                id: 'state04',
                html: '<a href="javascript:declareList.searchApproveState(declareList.approveState.notpass)">' + gz.label.approveStateNotPass + '</a>',
                style: 'margin-left:5px;margin-right:5px;'
            }, {
                xtype: 'label',//已归档
                id: 'state05',
                html: '<a href="javascript:declareList.searchApproveState(declareList.approveState.filed)">' + gz.label.approveStateFiled + '</a>',
                style: 'margin-left:5px;margin-right:5px;'
            }
            ]
        });
        tableObj.insertItem(toolbar, 0);
        // tableObj.getMainPanel().child('panel').addTool(customtools);//添加对应关系图标
        me.add(tableObj.getMainPanel());
    },
    /**
     * 根据选中的专项申报类型查询数据
     * @param declareType 传递的专项申报类型
     */
    searchDeclareType: function (declareType) {
        for (var i = 1; i <= 6; i++) {
            var getid = 'type0' + i;
            Ext.getCmp(getid).removeCls('scheme-selected-cls');
        }
        Ext.getCmp('typeAll').removeCls('scheme-selected-cls');//先移除全部的选中状态
        if (this.globalDeclareType == declareType) {//当前传递过来的专项审批类型等于全局的,那么将globalDeclareType==-1
            this.globalDeclareType = '';//置空如果不选中专项分类中列出的几项则查询不到数据
        } else {
            this.globalDeclareType = declareType;
            if (declareType == "-1") {
                Ext.getCmp('typeAll').addCls('scheme-selected-cls');
            } else {
                Ext.getCmp('type' + declareType).addCls('scheme-selected-cls');
            }
        }
        this.searchData();
    },
    /**
     * 根据选中的审批状态查询数据,如果都没选中则认为是
     * @param approveState
     */
    searchApproveState: function (approveState) {
        Ext.getCmp('stateAll').removeCls('scheme-selected-cls');//先移除全部的选中状态
        for (var i = 2; i <= 5; i++) {
            Ext.getCmp('state0' + i).removeCls('scheme-selected-cls');
        }

        if (this.globalApproveState == approveState) {//当前传递过来的专项审批类型等于全局的,那么将globalDeclareType==-1
            this.globalApproveState = '';//如果不选中的话查询数据为空
        } else {
            this.globalApproveState = approveState;
            if (approveState == '-1') {
                Ext.getCmp('stateAll').addCls('scheme-selected-cls');
            } else {
                Ext.getCmp('state' + approveState).addCls('scheme-selected-cls');
            }
        }
        this.searchData();
    },
    searchData: function () {
        var vo = new HashMap();
        vo.put("operateType", "search");
        vo.put("declareType", this.globalDeclareType);
        vo.put("approveState", this.globalApproveState);
        vo.put("refsFlag", "true");//是否是只刷新表格数据
        Rpc({functionId: 'GZ00000701', success: this.refsData, scope: this}, vo);
    },
    refsData: function (response) {
        var resData = Ext.decode(response.responseText);
        var return_code = resData.return_code;
        if (return_code == 'fail') {//失败的话弹出提示信息,不再往下执行
            var return_msg = resData.return_msg;
            Ext.Msg.alert(gz.msg.zxDeclareTitle, return_msg);
            return;
        }
        //刷新数据成功
        var store = Ext.data.StoreManager.lookup('declareListTable_dataStore');
        store.reload();
    },
    agree: function () {
        var isSelect = declareList.getSelectedParams();
        if (!isSelect) {
            return;
        }
        //同意按钮只能操作审核中的数据
        var check = Ext.Array.filter(declareList.approveStateArray, function (item) {
            return item != '02'
        })
        if (check.length > 0) {
            Ext.Msg.alert(gz.msg.zxDeclareTitle, gz.label.zxdeclare.agreeErrorTip);
            Ext.getCmp("declareListTable_tablePanel").getSelectionModel().deselectAll();
            return;
        }
        // 提示信息  确定要同意选中的数据？
        Ext.Msg.confirm(gz.msg.zxDeclareTitle, gz.msg.zxDeclareAgree, function (res) {
            if (res == "yes") {
                var map = new HashMap();
                map.put("operateType", "approve");
                map.put("ids", selectIds);
                Rpc({functionId: 'GZ00000701', success: declareList.refsData}, map);
            }
        });
    },
    reject: function () {
        var isSelect = declareList.getSelectedParams();
        if (!isSelect) {
            return;
        }
        //退回按钮只能操作审核中的数据
        var check = Ext.Array.filter(declareList.approveStateArray, function (item) {
            return item != '02'
        })
        if (check.length > 0) {
            Ext.Msg.alert(gz.msg.zxDeclareTitle, gz.label.zxdeclare.rejectErrorTip);
            Ext.getCmp("declareListTable_tablePanel").getSelectionModel().deselectAll();
            return;
        }
        // 提示信息  确定要退回选中的数据？
        Ext.Msg.confirm(gz.msg.zxDeclareTitle, gz.msg.zxDeclareReject, function (res) {
            if (res == "yes") {
                var map = new HashMap();
                map.put("operateType", "reject");
                map.put("ids", selectIds);
                Rpc({functionId: 'GZ00000701', success: declareList.refsData}, map);
            }
        });
    },
    deleteFunc: function () {
        var isSelect = declareList.getSelectedParams();
        if (!isSelect) {
            return;
        }
        //退回按钮只能操作审核中的数据
        var check = Ext.Array.filter(declareList.approveStateArray, function (item) {
            return item != '02' && item != '04';
        })
        if (check.length > 0) {
            Ext.Msg.alert(gz.msg.zxDeclareTitle, gz.label.zxdeclare.deleteErrorTip);
            Ext.getCmp("declareListTable_tablePanel").getSelectionModel().deselectAll();
            return;
        }
        // 提示信息  确定要删除选中的数据？
        Ext.Msg.confirm(gz.msg.zxDeclareTitle, gz.msg.zxDeclareDelete, function (res) {
            if (res == "yes") {
                var map = new HashMap();
                map.put("operateType", "delete");
                map.put("ids", selectIds);
                Rpc({functionId: 'GZ00000701', success: declareList.refsData}, map);
            }
        });
    },
    /**
     *
     */
    importFunc: function () {
        var vo = new HashMap();
        vo.put('type', 'import');
        Rpc({
            functionId: 'GZ00000705', success: function (resp) {
                var resData = Ext.decode(resp.responseText);
                var return_code = resData.return_code;
                if (return_code == 'fail') {//失败的话弹出提示信息,不再往下执行
                    var return_msg = resData.return_msg;
                    Ext.Msg.alert(gz.msg.zxDeclareTitle, return_msg);
                    return;
                }
                var savepath = resData.return_data.savepath;
                var fileUpLoad = Ext.create("SYSF.FileUpLoad", {
                    margin: '10 0 0 22',
                    upLoadType: 1,//指定单文件上传
                    emptyText: gz.label.zxdeclare.importFileText,
                    readInputWidth: 302,
                    VfsFiletype:VfsFiletypeEnum.other,
                    VfsModules:VfsModulesEnum.GZ,
                    VfsCategory:VfsCategoryEnum.other,
                    CategoryGuidKey: '',
                    isTempFile:true,
                    fileExt: "*.zip",
                    // savePath: savepath,
                    success: function (files) {
                        var file = files[0];
                        var param = new HashMap();
                        param.put('type', 'importData');
                        param.put('file', file);
                        Ext.Msg.wait(gz.label.zxdeclare.importWaitTip, gz.msg.zxDeclareTitle);//正在导入数据
                        Rpc({
                            functionId: 'GZ00000705', success: function (resp) {
                                var result = Ext.decode(resp.responseText);
                                var eFileName = result.eFileName;
                                if (result.return_code == 'fail') {//失败的话弹出提示信息,不再往下执行
                                    var return_msg = result.return_msg;
                                    Ext.Msg.hide();
                                    Ext.Msg.alert(gz.msg.zxDeclareTitle, return_msg, function (opterType) {
                                        if (opterType == 'ok') {
                                            Ext.getCmp('importDataWindow').close();
                                        }
                                    });
                                    return;
                                } else {
                                    // if(result.return_code == 'false'){
                                    //     Ext.Msg.hide();
                                    //     Ext.Msg.alert(gz.msg.zxDeclareTitle,gz.label.zxdeclare.importPartPersonFailTip, function (opterType) {
                                    //         if (opterType == 'ok') {
                                    //             Ext.getCmp('importDataWindow').close();
                                    //         }
                                    //     });
                                    //     return;
                                    // }
                                    Ext.Msg.hide();
                                    var tips = "";
                                    if (result.successCount > 0 && result.errorCount < 1) {
                                        tips = gz.label.zxdeclare.successImportTemplate.replace('{count}', result.successCount);
                                    } else {
                                        tips = gz.label.zxdeclare.failImportTemplate.replace('{successCount}', result.successCount).replace('{errorCount}', result.errorCount);
                                        if (eFileName) {
                                            var url = "/servlet/vfsservlet?fromjavafolder=true&fileid="+eFileName;
                                            window.open(url, "_blank");
                                        }
                                    }
                                    Ext.Msg.alert(gz.msg.zxDeclareTitle, tips, function (opterType) {
                                        if (opterType == 'ok') {
                                            Ext.getCmp('importDataWindow').close();
                                        }
                                    });
                                    // window.location.reload();
                                    var store = Ext.data.StoreManager.lookup('declareListTable_dataStore');
                                    store.reload();
                                }
                            }
                        }, param);
                    },
                });
                var win = Ext.create('Ext.window.Window', {
                    title: gz.label.zxdeclare.importText,
                    layout: 'vbox',
                    width: 410,
                    height: 140,
                    id: 'importDataWindow',
                    modal: true,
                    items: [{
                        xtype: 'tbtext',
                        margin: '10 0 0 10',
                        html: gz.label.zxdeclare.importFileTip
                    }, fileUpLoad]
                });
                win.show();
            }
        }, vo);
    },
    /**
     * 获取选中的数据
     */
    getSelectedParams: function () {
        var me = this;
        selectIds = "";
        var selectRecords = Ext.getCmp("declareListTable_tablePanel").getSelectionModel().getSelection();
        if (selectRecords.length <= 0) {
            // 提示信息  请选中要操作的记录？
            Ext.Msg.alert(gz.msg.zxDeclareTitle, gz.msg.zxDeclarePleaseSelect);
            return false;
        }
        this.approveStateArray = [];//下一次选择时  将上一次的置空
        Ext.each(selectRecords, function (rec, index) {
            me.approveStateArray.push(rec.data.approve_state);
            if (index == selectRecords.length - 1) {
                selectIds += rec.data.id_e;
            } else {
                selectIds += rec.data.id_e + ",";
            }
        });
        return true;
    },
    declareInfor: function (value, metaData, record) {
        var id = record.get("id_e");
        var declare_type = record.get("declare_type");
        return "<a href='javascript:declareList.declareInforView(\"" + id + "\",\"" + declare_type + "\");' style='width:100%;height:100%;'>" + value + "</a>";
    },
    declareInforView: function (id, declare_type) {
        var me = this;
        var win = Ext.create('Ext.window.Window', {
            title: me.tranlateZxdeclareType(declare_type),
            width: document.body.offsetWidth,
            height: document.body.offsetHeight,
            id: 'declareInfoWindow',
            layout: 'fit',
            items: [{
                xtype: 'declareinfor',
                id: id
            }]
        });
        items:win.show();
    },
    zxDeclareType: {
        all: "-1",
        child_edu: "01",
        continu_edu: "02",
        housing_rent: "03",
        interest_expense: "04",
        illness_medicalcare: "05",
        support_elderly: "06",
    },
    approveState: {
        all: "-1",//全部
        inaudit: "02",//审核中
        adopt: "03",//通过
        notpass: "04",//未通过
        filed: "05"//已归档
    },
    /**
     * 将专项申报类型转换为文字
     * @param value
     * @returns {string}
     */
    tranlateZxdeclareType: function (value) {
        var text;
        if (value == '01') {
            text = gz.label.zxDeclareTypeChildEdu + gz.label.zxDeclareTypeInfo;
        } else if (value == '02') {
            text = gz.label.zxDeclareTypeContinuEdu + gz.label.zxDeclareTypeInfo;
        } else if (value == "03") {
            text = gz.label.zxDeclareTypeHouseRent + gz.label.zxDeclareTypeInfo;
        } else if (value == "04") {
            text = gz.label.zxDeclareTypeInterestExpense + gz.label.zxDeclareTypeInfo;
        } else if (value == "05") {
            text = gz.label.zxDeclareTypeIllnessMedicalcare + gz.label.zxDeclareTypeInfo;
        } else if (value == "06") {
            text = gz.label.zxDeclareTypeSupportElderly + gz.label.zxDeclareTypeInfo;
        }
        return text;
    },
    // exportTemplateExcel: function () {
    //     var filePath;
    //     var settingWindow = Ext.create('Ext.window.Window', {
    //         width: 300,
    //         height: 180,
    //         resizable: false,
    //         title: gz.label.zxdeclare.exportTemplateTitle,
    //         layout: {
    //             align: 'middle',
    //             pack: 'center',
    //             type: 'vbox'
    //         },
    //         items: [{
    //             layout: 'column',
    //             border: false,
    //             width:210,
    //             margin: '0 10 20 0',
    //             padding: '-30 0 10 0',
    //             items: [{
    //                 columnWidth: 0.7,
    //                 border: false,
    //                 xtype: 'label',
    //                 text: '1、' + gz.label.zxdeclare.selectTemplateFile,
    //             }
    //                 , {
    //                     columnWidth: 0.3,
    //                     border: false,
    //                     margin:'0 0 0 15',
    //                     items: {
    //                         xtype: 'button',
    //                         text: gz.label.zxdeclare.browse,
    //                         id: 'importResumeId',
    //                         padding: 0,
    //                         height: 22,
    //                         width: 38,
    //                         handler: function () {
    //                             //上传控件
    //                             var uploadObj = Ext.create("SYSF.FileUpLoad", {
    //                                 upLoadType: 1,
    //                                 fileExt: "*.xls;",
    //                                 emptyText: gz.label.zxdeclare.importFileText,
    //                                 buttonText: gz.label.zxdeclare.browse + "...",
    //                                 height: 30,
    //                                 //回调方法，失败
    //                                 error: function () {
    //                                     Ext.showAlert(common.msg.uploadFailed + "！");
    //                                 },
    //                                 success: function (list) {
    //                                     filePath = list[0].fullpath;
    //                                     var map = new HashMap();
    //                                     map.put("operateType", "saveTemplateFile");
    //                                     map.put("filePath", filePath);
    //                                     Rpc({
    //                                         functionId: 'GZ00000701', success: function (form) {
    //                                             var return_data = Ext.decode(form.responseText).return_data;
    //                                             var d = new Ext.util.DelayedTask(function () {
    //                                                 win.close();
    //                                             });
    //                                             d.delay(2000);
    //                                             if (return_data.msg == "isNotTemplateFile") {
    //                                                 Ext.Msg.alert(gz.msg.zxDeclareTitle, gz.label.zxdeclare.noTemplateFileErrorTip);
    //                                                 return;
    //                                             }
    //                                         }
    //                                     }, map);
    //                                 }
    //                             });
    //
    //                             //上传导入弹出框
    //                             var win = Ext.widget("window", {
    //                                 title: gz.label.zxdeclare.selectImportFile,
    //                                 modal: true,
    //                                 border: false,
    //                                 width: 395,
    //                                 height: 140,
    //                                 layout: {
    //                                     type: 'vbox',
    //                                     align: 'stretch',
    //                                     pack: 'center'
    //                                 },
    //                                 defaults: {
    //                                     margin: '0 0 10 10'
    //                                 },
    //                                 items: [{
    //                                     xtype: 'tbtext',
    //                                     text: gz.label.zxdeclare.importComment
    //                                 }, {
    //                                     xtype: 'panel',
    //                                     border: false,
    //                                     layout: {
    //                                         type: 'vbox',
    //                                         padding: '0 0 0 30',
    //                                         pack: 'center',
    //                                         align: 'middle'
    //                                     },
    //                                     items: [uploadObj]
    //                                 }]
    //                             });
    //                             win.show();
    //                         }
    //                     }
    //                 }]
    //         }, {
    //             layout: 'column',
    //             border: false,
    //             padding: '-30 0 10 0',
    //             width: 210,
    //             items: [{
    //                 columnWidth: 0.7,
    //                 border: false,
    //                 html: "<font >2、"+ gz.label.zxdeclare.exportDeclareData + "</font>",
    //             },
    //                 {
    //                     columnWidth: 0.3,
    //                     border: false,
    //                     margin:'0 0 0 15',
    //                     items: {
    //                         xtype: 'button',
    //                         text: gz.label.zxdeclare.exportText,
    //                         handler: function () {
    //                             var map = new HashMap();
    //                             map.put("operateType", "exportTemplateExcel");
    //                             Rpc({
    //                                 functionId: 'GZ00000701', success: function (form) {
    //                                     var return_data = Ext.decode(form.responseText).return_data;
    //                                     var exportTemplateExcelParam = return_data.exportTemplateExcelParam;
    //                                     if (exportTemplateExcelParam.isExitsFile == "false") {
    //                                         Ext.Msg.alert(gz.msg.zxDeclareTitle, gz.label.zxdeclare.noTemplateFileTip);
    //                                         return;
    //                                     }
    //                                     if (exportTemplateExcelParam.isNotTemplateFile) {
    //                                         Ext.Msg.alert(gz.msg.zxDeclareTitle, gz.label.zxdeclare.noTemplateFileErrorTip);
    //                                         return;
    //                                     }
    //                                     window.location.target = "_blank";exportTemplateExcelParam.zipFileName
    //                                     settingWindow.close();
    //                                     Ext.Msg.alert(gz.msg.zxDeclareTitle, exportTemplateExcelParam.msg);
    //                                 }
    //                             }, map);
    //                         }
    //                     }
    //                 }]
    //         }]
    //     }).show();
    // },
    exportTemplateExcel: function () {
        var currentIndex = "0";
        var map = new HashMap();
        map.put("operateType", "getCurrentIndex");
        Rpc({
            functionId: 'GZ00000701', success: function (form) {
                currentIndex = Ext.decode(form.responseText).return_data.currentIndex;
            }, async: false
        }, map);
        if (!currentIndex) {//防止后台错误
            currentIndex = "0";
        }
        var settingPanel = Ext.create('Ext.panel.Panel', {
            width: '100%',
            layout: 'card',
            itemId: 'settingPanel',
            border: false,
            items: []
        });
        var uploadField = Ext.create("SYSF.FileUpLoad", {
            upLoadType: 1,
            fileExt: "*.xls;",
            emptyText: gz.label.zxdeclare.importFileText,
            buttonText: gz.label.zxdeclare.browse + "...",
            VfsFiletype:VfsFiletypeEnum.other,
            VfsModules:VfsModulesEnum.GZ,
            VfsCategory:VfsCategoryEnum.other,
            CategoryGuidKey: '',
            height: 30,
            //回调方法，失败
            error: function () {
                Ext.showAlert(common.msg.uploadFailed + "！");
            },
            success: function (list) {
                //临时缓存 可能后续有问题
                fileid = list[0].fileid;
                var map = new HashMap();
                map.put("operateType", "saveTemplateFile");
                map.put("fileid", fileid);
                Rpc({
                    functionId: 'GZ00000701', success: function (form) {
                        var return_data = Ext.decode(form.responseText).return_data;
                        if (return_data.msg == "isNotTemplateFile") {
                            Ext.Msg.alert(gz.msg.zxDeclareTitle, gz.label.zxdeclare.noTemplateFileErrorTip);
                            return;
                        }
                    }
                }, map);
            }
        });
        declareList.firstPanel = Ext.create('Ext.panel.Panel', {
            border: false,
            width: '100%',
            layout: {
                type: 'vbox',
                // align: 'center'
            },
            items: [{
                xtype: 'tbtext',
                html: '1、' + gz.label.zxdeclare.importComment,
                margin: '45 0 0 66'
            }, {
                xtype: 'tbtext',
                html: '2、' + gz.label.zxdeclare.importCommentAfter,
                margin: '15 0 0 66'
            }, {
                xtype: 'panel',
                width: '90%',
                border: false,
                items: uploadField,
                margin: '15 0 0 70'

            }]
        });
        declareList.secondPanel = declareList.createRelationshipPanel();
        declareList.thirdPanel = Ext.create('Ext.panel.Panel', {
            border: false,
            width: '100%',
            items: [{
                xtype: 'tbtext',
                html: '1、' + gz.label.zxdeclare.thirdPanelTips1,
                margin: '45 0 0 122'
            }, {
                xtype: 'tbtext',
                html: '2、' + gz.label.zxdeclare.thirdPanelTips2,
                margin: '45 0 0 122'
            }]
        });
        if (currentIndex != '0') {
            declareList.firstPanel.insert(2, Ext.create('Ext.toolbar.TextItem', {
                margin: '15 0 0 66',
                html: '3、' + gz.label.zxdeclare.exitsTemplateFile
            }));
        }
        if (currentIndex == '0') {
            settingPanel.setActiveItem(declareList.firstPanel);
        } else if (currentIndex == '1') {
            settingPanel.setActiveItem(declareList.secondPanel);
        } else if (currentIndex == '2') {
            settingPanel.setActiveItem(declareList.thirdPanel);
        }
        var stepview = Ext.widget("stepview", {
            listeners: {
                stepchange: function (stepview, step) {
                    declareList.changeStep(stepview.currentIndex);
                }
            },
            height: 45,
            freeModel: false,
            currentIndex: currentIndex,
            stepData: [{name: gz.label.zxdeclare.selectTemplateFile}, {name: gz.label.zxdeclare.declareRelationshipTitle}, {name: gz.label.zxdeclare.exportTemplateTitle}]
        });
        //显示window
        var showWin = Ext.create('Ext.window.Window', {
            width: 540,
            height: 450,
            title: gz.label.zxdeclare.exportDeclareData,
            resizable: false,
            id: 'exportWindow',
            items: [{
                xtype: 'panel',
                width: 580,
                border: false,
                margin: '10 0 0 14',
                items: [stepview]
            }, settingPanel],
            buttonAlign: 'center',
            buttons: [
                {
                    text: gz.label.zxdeclare.previousStep,//上一步
                    id: 'previousStep',
                    margin: '0 5 0 0',
                    hidden: true,
                    height: 22,
                    handler: function () {
                        stepview.previousStep();
                    }
                }, {
                    text: gz.label.zxdeclare.nextStep,//下一步
                    id: 'nextStep',
                    hidden: false,
                    height: 22,
                    margin: '0 5 0 0',
                    handler: function () {
                        var isFirstToSecond = true;
                        if (stepview.currentIndex == '0') {
                            var map = new HashMap();
                            map.put("operateType", "checkFile");
                            Rpc({
                                functionId: 'GZ00000701', success: function (form) {
                                    var return_data = Ext.decode(form.responseText).return_data;
                                    if (return_data.isExitsFile == "false") {
                                        isFirstToSecond = false;
                                        Ext.Msg.alert(gz.msg.zxDeclareTitle, gz.label.zxdeclare.noTemplateFileTip);
                                        return;
                                    }
                                }, async: false
                            }, map);
                            if (isFirstToSecond) {
                                stepview.nextStep();
                            }
                            return;
                        }
                        var secondToThird = true;
                        if (stepview.currentIndex == '1') {
                            var dataArray = [];
                            var gridStore = Ext.StoreManager.lookup('relationStore');
                            var count = gridStore.getCount();
                            var alertTip = "";
                            if (!Ext.util.Format.trim(gridStore.getAt(0).data.itemid)) {
                                secondToThird = false;
                                alertTip += gz.label.zxdeclare.taxpayerIDType + ',';
                                // Ext.Msg.alert(gz.msg.zxDeclareTitle, gz.label.zxdeclare.taxpayerIDTypeIsNull);
                                // return;
                            }
                            if (!Ext.util.Format.trim(gridStore.getAt(1).data.itemid)) {
                                secondToThird = false;
                                alertTip += gz.label.zxdeclare.taxpayerIDNumber + ',';
                                // Ext.Msg.alert(gz.msg.zxDeclareTitle, gz.label.zxdeclare.taxpayerIDNumberIsNull);
                                // return;
                            }
                            if (!Ext.util.Format.trim(gridStore.getAt(4).data.itemid)) {
                                secondToThird = false;
                                alertTip += gz.label.zxdeclare.spouseSituation + ',';
                                // Ext.Msg.alert(gz.msg.zxDeclareTitle, gz.label.zxdeclare.spouseSituationIsNull);
                                // return;
                            }
                            alertTip = Ext.util.Format.substr(alertTip,0, alertTip.length -1);
                            // alertTip = alertTip.substring(0, alertTip.length - 1);
                            alertTip += gz.label.zxdeclare.isNot;
                            alertTip += '空 ';//因为在ie浏览器下如果把空字写到资源文件里  会不显示所以没有资源国际化
                            if (!secondToThird) {
                                Ext.Msg.alert(gz.msg.zxDeclareTitle, alertTip);
                                return;
                            }
                            for (var i = 0; i < count; i++) {
                                var fieldData = gridStore.getAt(i).data;
                                var fieldObject = {};
                                fieldObject.fieldsetid = fieldData.fieldsetid ? fieldData.fieldsetid : '';
                                fieldObject.itemid = fieldData.itemid ? fieldData.itemid : '';
                                fieldObject.sourceField = fieldData.sourceField ? fieldData.sourceField : '';
                                fieldObject.fielditemidesc = fieldData.fielditemidesc ? fieldData.fielditemidesc : '';
                                dataArray.push(fieldObject);
                            }
                            var map = new HashMap();
                            map.put("operateType", "saveRelation");
                            map.put("fieldsArray", dataArray);
                            // console.log(dataArray);
                            Rpc({
                                functionId: 'GZ00000701', success: function (form) {
                                    var return_data = Ext.decode(form.responseText).return_data;
                                    if (return_data.saveFlag == "success") {
                                        // Ext.Msg.alert(gz.msg.zxDeclareTitle, gz.label.zxdeclare.saveSuccess, function () {
                                        // });
                                    } else {
                                        secondToThird = false;
                                        Ext.Msg.alert(gz.msg.zxDeclareTitle, gz.label.zxdeclare.saveFail, function () {
                                        });
                                    }
                                }, async: false
                            }, map);
                            if (secondToThird) {
                                stepview.nextStep();
                                return;
                            }
                        }
                    }
                }, {
                    text: gz.label.zxdeclare.exportText,//导出
                    id: 'export',
                    hidden: true,
                    height: 22,
                    margin: '0 5 0 0',
                    handler: function () {
                        Ext.Msg.wait(gz.label.zxdeclare.exportWaitTip, gz.msg.zxDeclareTitle);//正在导入数据
                        var map = new HashMap();
                        map.put("fileid",fileid)
                        map.put("operateType", "exportTemplateExcel");
                        Rpc({
                            functionId: 'GZ00000701', success: function (form) {
                                var return_data = Ext.decode(form.responseText).return_data;
                                var exportTemplateExcelParam = return_data.exportTemplateExcelParam;
                                if (exportTemplateExcelParam.isExitsFile == "false") {
                                    Ext.Msg.alert(gz.msg.zxDeclareTitle, gz.label.zxdeclare.noTemplateFileTip);
                                    return;
                                }
                                if (exportTemplateExcelParam.isNotTemplateFile) {
                                    Ext.Msg.alert(gz.msg.zxDeclareTitle, gz.label.zxdeclare.noTemplateFileErrorTip);
                                    return;
                                }
                                var fieldName = getDecodeStr(exportTemplateExcelParam.zipFileName);
                                window.location.target = "_blank";
                                window.location.href = "/servlet/vfsservlet?fileid="+fieldName+"&fromjavafolder=true";
                                showWin.close();
                                Ext.Msg.alert(gz.msg.zxDeclareTitle, exportTemplateExcelParam.msg);
                            }
                        }, map);
                    }
                }
            ]
        }).show();
        if (currentIndex == '0') {
            Ext.getCmp("previousStep").setHidden(true);
            Ext.getCmp("nextStep").setHidden(false);
            Ext.getCmp("export").setHidden(true);
        } else if (currentIndex == '1') {
            Ext.getCmp("previousStep").setHidden(false);
            Ext.getCmp("nextStep").setHidden(false);
            Ext.getCmp("export").setHidden(true);
        } else if (currentIndex == '2') {
            Ext.getCmp("previousStep").setHidden(false);
            Ext.getCmp("nextStep").setHidden(true);
            Ext.getCmp("export").setHidden(false);
        }
    },
    renderLableDescription: function (value) {
        return value.replace(/\n/g, '<br/>').replace(/\s/g, '&nbsp;');
    }
    ,
    /**
     * 步骤条步骤改变事件处理函数
     */
    changeStep: function (index) {
        var settingPanel = Ext.getCmp('exportWindow').query('#settingPanel')[0];
        if (index == 0) {
            Ext.getCmp("previousStep").setHidden(true);
            Ext.getCmp("nextStep").setHidden(false);
            Ext.getCmp("export").setHidden(true);
            settingPanel.setActiveItem(declareList.firstPanel);
        } else if (index == 1) {
            Ext.getCmp("previousStep").setHidden(false);
            Ext.getCmp("nextStep").setHidden(false);
            Ext.getCmp("export").setHidden(true);
            settingPanel.setActiveItem(declareList.secondPanel);
        } else if (index == 2) {
            Ext.getCmp("previousStep").setHidden(false);
            Ext.getCmp("nextStep").setHidden(true);
            Ext.getCmp("export").setHidden(false);
            settingPanel.setActiveItem(declareList.thirdPanel);
        }
    }
    ,
    createRelationshipPanel: function () {
        var me = this;
        if (!declareList.selectMap) {
            declareList.selectMap = new HashMap();
        }
        var map = new HashMap();
        map.put("operateType", "getRelation");
        Rpc({
            functionId: 'GZ00000701', success: function (form) {
                declareList.relationData = Ext.decode(form.responseText).return_data;
            }, async: false
        }, map);
        var gridColumns = [
            {
                text: gz.label.zxdeclare.relationshipName,
                align: 'left',
                dataIndex: 'displayname',
                flex: 6,
                sortable: false
            },
            {
                text: gz.label.zxdeclare.hrFiledName,
                align: 'left',
                dataIndex: 'fielditemidesc',
                flex: 4,
                sortable: false,
                editor: this.getObjectiveCombo()
            }
        ];
        var gridData = [
            {
                displayname: '<span style="color: red;font-weight:bold">*</span>' + gz.label.zxdeclare.taxpayerIDType,
                // fielditemidesc: gz.label.zxdeclare.select,
                fielditemidesc: '',
                fieldsetid: '',
                itemid: '',
                sourceField: 'taxpayerIDType'
            },
            {
                displayname: '<span style="color: red;font-weight:bold">*</span>' + gz.label.zxdeclare.taxpayerIDNumber,
                // fielditemidesc: gz.label.zxdeclare.select,
                fielditemidesc: '',
                fieldsetid: '',
                itemid: '',
                sourceField: 'taxpayerIDNumber'
            },
            {
                displayname: '<div style="margin-left: 6px">' + gz.label.zxdeclare.taxpayerIdentificationNumber + '</div>',
                // fielditemidesc: gz.label.zxdeclare.select,
                fielditemidesc: '',
                fieldsetid: '',
                itemid: '',
                sourceField: 'taxpayerIdentificationNumber'
            },
            {
                displayname: '<div style="margin-left: 6px">' + gz.label.zxdeclare.contactAddress + '</div>',
                // fielditemidesc: gz.label.zxdeclare.select,
                fielditemidesc: '',
                fieldsetid: '',
                itemid: '',
                sourceField: 'contactAddress'
            },
            {
                displayname: '<span style="color: red;font-weight:bold">*</span>' + gz.label.zxdeclare.spouseSituation,
                // fielditemidesc: gz.label.zxdeclare.select,
                fielditemidesc: '',
                fieldsetid: '',
                itemid: '',
                sourceField: 'spouseSituation'
            },
            {
                displayname: '<div style="margin-left: 6px">' + gz.label.zxdeclare.spouseName + '</div>',
                // fielditemidesc: gz.label.zxdeclare.select,
                fielditemidesc: '',
                fieldsetid: '',
                itemid: '',
                sourceField: 'spouseName'
            },
            {
                displayname: '<div style="margin-left: 6px">' + gz.label.zxdeclare.spouseIdType + '</div>',
                // fielditemidesc: gz.label.zxdeclare.select,
                fielditemidesc: '',
                fieldsetid: '',
                itemid: '',
                sourceField: 'spouseIdType'
            },
            {
                displayname: '<div style="margin-left: 6px">' + gz.label.zxdeclare.spouseIdNumber + '</div>',
                // fielditemidesc: gz.label.zxdeclare.select,
                fielditemidesc: '',
                fieldsetid: '',
                itemid: '',
                sourceField: 'spouseIdNumber'
            }
        ];
        var data = declareList.relationData.relation;
        if (data) {
            data = Ext.decode(declareList.relationData.relation);
            for (var i = 0; i < data.length; i++) {
                var columnObject = data[i];
                for (var s = 0; s < gridData.length; s++) {
                    var gridObject = gridData[s];
                    if (gridObject) {//加此判断是因为ie在兼容模式下会把protype原型也算在数组长度里
                        if (columnObject.sourceField == gridObject.sourceField) {
                            gridObject.fieldsetid = columnObject.fieldSetId;
                            gridObject.itemid = columnObject.itemId;
                            gridObject.fielditemidesc = columnObject.fielditemidesc;
                        }
                    }
                }
            }
        }
        //在ie兼容模式下js代码一定不要有多余的逗号！！！！！否则会解析出错
        Ext.define('relationModel', {
            extend: 'Ext.data.Model',
            fields: ['displayname', 'fielditemidesc', 'fieldsetid', 'itemid', 'sourceField']
        });
        var gridStore = Ext.create('Ext.data.Store', {
            storeId: 'relationStore',
            model: 'relationModel',
            data: gridData
        });
        return Ext.create('Ext.grid.Panel', {
            store: gridStore,
            columnLines: true,
            columns: gridColumns,
            plugins: [{ptype: 'cellediting', clicksToEdit: 1}],
            viewConfig: {
                markDirty: false //不显示编辑后的三角
            },
            dockedItems: [{
                xtype: 'toolbar',
                dock: 'top',
                width: '40%',
                border: false,
                items: [
                    declareList.getArchiveCombo()
                ]
            }]
        });
    }
    ,
    getObjectiveCombo: function () {
        var objectiveStore = Ext.create('Ext.data.Store', {
            storeId: 'objectiveStore',
            fields: ['valueitemid', 'valuedesc'],
            data: [],
            proxy: {
                type: 'transaction',
                functionId: 'GZ00000701',
                extraParams: {
                    fieldsetid: '',
                    operateType: 'getField'
                },
                reader: {
                    type: 'json',
                    root: 'fieldList'
                }
            },
            autoLoad: true
        });
        return Ext.create('Ext.form.field.ComboBox', {
            labelWidth: 70,
            store: objectiveStore,
            valueField: 'valueitemid',
            displayField: 'valuedesc',
            itemId: 'fieldItemCombox',
            editable: false,
            tpl: Ext.create('Ext.XTemplate',
                '<ul class="x-list-plain"><tpl for=".">',
                '<li role="option" class="x-boundlist-item" style="height: 22px">{valuedesc}</li>',
                '</tpl></ul>'
            ),  //为了给combobox添加空选项
            listeners: {
                select: function (combo, record, eOpts) {
                    var grid = combo.up('grid');
                    var gridRecord = grid.getSelectionModel().getSelection()[0];
                    var fieldsetid = grid.query('#fieldSetCombox')[0].getValue();
                    gridRecord.set('itemid', record.data.valueitemid);
                    gridRecord.set('fieldsetid', fieldsetid);
                    declareList.selectMap.put(gridRecord.data.sourceField, record.data.valueitemid);
                },
                beforeselect: function (combo, record, index, eOpts) {//限制选择项  选择过的数据不允许再次选择
                    var flag = true;
                    for (var key in declareList.selectMap) {
                        if (declareList.selectMap[key] == record.data.valueitemid && Ext.util.Format.trim(declareList.selectMap[key])) {
                            flag = false;
                            break;
                        }
                    }
                    return flag;
                }
            },
            //重写getvalue方法  为了gridpanel 第二列显示指标描述
            getValue: function () {
                if (!this.value)
                    return "";
                return this.rawValue;
            }

        });
    }
    ,
    getArchiveCombo: function () {
        var archiveComboStore = Ext.create('Ext.data.Store', {
            storeId: 'archiveComboStore',
            fields: ['fieldsetid', 'fieldsetdesc'],
            data: declareList.relationData.fieldsetlist
        });
        return Ext.create('Ext.form.field.ComboBox', {
            fieldLabel: gz.label.zxdeclare.selectFieldSet,
            labelWidth: 70,
            store: archiveComboStore,
            valueField: 'fieldsetid',
            displayField: 'fieldsetdesc',
            itemId: 'fieldSetCombox',
            editable: false,
            listeners: {
                select: function (combo, record, eOpts) {
                    var fieldsetid = record.data.fieldsetid;
                    // 刷新子集指标字段数据源
                    var store = Ext.data.StoreManager.lookup('objectiveStore');
                    var extraParams = {
                        fieldsetid: fieldsetid,
                        operateType: 'getField'
                    }
                    store.getProxy().extraParams = extraParams;
                    store.load();
                },
                afterRender: function () {
                    var archiveComboStore = Ext.data.StoreManager.lookup('archiveComboStore');
                    var fieldsetid = archiveComboStore.getData().items[0].data.fieldsetid;
                    this.setValue(fieldsetid);
                    var objectiveStore = Ext.data.StoreManager.lookup('objectiveStore');
                    var extraParams = {
                        fieldsetid: fieldsetid,
                        operateType: 'getField'
                    }
                    objectiveStore.getProxy().extraParams = extraParams;
                    objectiveStore.load();
                }
            }
        });
    }

})
