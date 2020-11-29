Ext.define('Talentmarkets.competition.CompetitionJobs', {
    requires: ['SYSF.FileUpLoad', 'Talentmarkets.competition.InterviewArrange','Talentmarkets.competition.AddPositions'],
    extend: 'Ext.panel.Panel',
    layout: 'fit',
    bodyPadding: '0 0 0 5',
    status: {
        drafting: '01',
        application: '02',
        approved: '03',
        published: '04',
        suspend: '05',
        end: '06',
        publicized: '07',
        approvalFailed:'08',
        refuse:'10'
    },
    from: 'jobs',
    initComponent: function () {
        competitionJobs = this;
        Ext.util.CSS.createStyleSheet(".scheme-selected-cls{text-decoration:underline;margin-left:10px;margin-right:10px;}", "underline");
        this.callParent();
        this.init();
    },
    init: function () {
        var fromValue = "";
        var statusValue = "";
        if (location.search) {
            var searchParams = location.search.substring(1);
            var paramsArr = searchParams.split("&");
            fromValue = paramsArr[0].split("=")[1];
            statusValue = paramsArr[1].split("=")[1];
            ////点击门户页面图标进来，修改gridpanel的title
            competitionJobs.gridTitle = $URL.decode(paramsArr[2]).split("=")[1];
        }
        if (fromValue) {
            competitionJobs.from = fromValue;
        }
        var vo = new HashMap();
        vo.put("operateType", "search");
        vo.put("init", "1");//1 代表初始化  0 代表查询
        vo.put("from", fromValue);//连接从哪来
        vo.put("status", statusValue);//查询当前||历史
        Rpc({functionId: 'TM000000002', success: this.rendData, scope: this, async: false}, vo);
    },
    rendData: function (response) {
        var resData = Ext.decode(response.responseText);
        var return_code = resData.return_code;
        if (return_code == 'fail') {//失败的话弹出提示信息,不再往下执行
            var return_msg = resData.return_msg;
            Ext.Msg.alert(tm.contendPos.msg.title, eval(return_msg));
            return;
        }
        var tableConfig = resData.return_data.gridconfig;
        competitionJobs.postDetailRnameId = resData.return_data.postDetailRnameId;
        competitionJobs.quickApprove = resData.return_data.quickApprove;
        competitionJobs.alternativeItems = Ext.decode(resData.return_data.alternativeItems);
        competitionJobs.groupItems = Ext.decode(resData.return_data.groupItems);
        //是否含有岗位详情登记表权限
        competitionJobs.isHavePosCardId = Ext.decode(resData.return_data.isHavePosCardId);
        var psnOrPosPrivMap = resData.return_data.psnOrPosPrivMap;
        competitionJobs.psnPriv = psnOrPosPrivMap.psnPriv;
        var configObj = Ext.decode(tableConfig);
        configObj.title = competitionJobs.gridTitle ? competitionJobs.gridTitle : configObj.title;
        //翻页自动保存
        competitionJobs.notShowAlertFlag = false;
        configObj.onChangePage = function () {
            competitionJobs.notShowAlertFlag = true;
            competitionJobs.saveFunc();
            competitionJobs.notShowAlertFlag = false;
        };
        configObj.openColumnQuery = true;
        configObj.beforeBuildComp = function (grid) {
            grid.tableConfig.selModel={selType:'checkboxmodel',checkOnly:true};
        };
        competitionJobs.tableObj = new BuildTableObj(configObj);
        //只有从竞聘岗位进来时 才显示查询方案
        if (competitionJobs.from == 'jobs') {
            competitionJobs.tableObj.insertItem(this.createQueryPlan(), 0);
        }
        competitionJobs.add(competitionJobs.tableObj.getMainPanel());
        competitionJobs.addEvent();

    },
    createQueryPlan: function () {
        var itemMargin = '0 15 0 0';
        var planContainer = Ext.create('Ext.container.Container', {
            layout: 'hbox',
            margin: '5 0 5 0',
            items: [
                {
                    xtype: 'component',
                    html: tm.contendPos.queryPlan + ":",
                    margin: itemMargin
                },
                {
                    xtype: 'component',
                    html: '<span style="color:#1B4A98;cursor: pointer" onclick="javascript:void(0);competitionJobs.schemeClick(\'all\')">' + tm.contendPos.status.all + '</span>',//全部
                    cls: 'scheme-selected-cls',
                    schemeId: 'all',
                    itemId: 'scheme_all',
                    margin: itemMargin
                },{
                    xtype: 'component',
                    html: '<span style="color:#1B4A98;cursor: pointer" onclick="javascript:void(0);competitionJobs.schemeClick(\'drafting\')">' + tm.contendPos.status.drafting + '</span>',//起草
                    schemeId: 'drafting',
                    itemId: 'scheme_drafting',
                    margin: itemMargin
                }, {
                    xtype: 'component',
                    html: '<span style="color:#1B4A98;cursor: pointer" onclick="javascript:void(0);competitionJobs.schemeClick(\'application\')">' + tm.contendPos.status.application + '</span>',//申请中
                    schemeId: 'application',
                    itemId: 'scheme_application',
                    margin: itemMargin
                }, {
                    xtype: 'component',
                    itemId: 'scheme_approved',
                    html: '<span style="color:#1B4A98;cursor: pointer" onclick="javascript:void(0);competitionJobs.schemeClick(\'approved\')">' + tm.contendPos.status.approved + '</span>',//已批准
                    schemeId: 'approved',
                    margin: itemMargin
                },{
                    xtype: 'component',
                    itemId: 'scheme_refuse',
                    html: '<span style="color:#1B4A98;cursor: pointer" onclick="javascript:void(0);competitionJobs.schemeClick(\'refuse\')">已' + tm.contendPos.status.refuse + '</span>',//已退回
                    schemeId: 'refuse',
                    margin: itemMargin
                }, {
                    xtype: 'component',
                    html: '<span style="color:#1B4A98;cursor: pointer" onclick="javascript:void(0);competitionJobs.schemeClick(\'published\')">' + tm.contendPos.status.published + '</span>',//已发布
                    schemeId: 'published',
                    itemId: 'scheme_published',
                    margin: itemMargin
                }, {
                    xtype: 'component',
                    html: '<span style="color:#1B4A98;cursor: pointer" onclick="javascript:void(0);competitionJobs.schemeClick(\'suspend\')">' + tm.contendPos.status.suspend + '</span>',//暂停
                    schemeId: 'suspend',
                    itemId: 'scheme_suspend',
                    margin: itemMargin
                }, {
                    xtype: 'component',
                    itemId: 'scheme_end',
                    html: '<span style="color:#1B4A98;cursor: pointer" onclick="javascript:void(0);competitionJobs.schemeClick(\'end\')">' + tm.contendPos.status.end + '</span>',//结束
                    schemeId: 'end',
                    margin: itemMargin
                }, {
                    xtype: 'component',
                    itemId: 'scheme_publicized',
                    html: '<span style="color:#1B4A98;cursor: pointer" onclick="javascript:void(0);competitionJobs.schemeClick(\'publicized\')">' + tm.contendPos.status.publicized + '</span>',//已公示
                    schemeId: 'publicized',
                    margin: itemMargin
                }, {
                    xtype: 'component',
                    itemId: 'scheme_publicizend',
                    html: '<span style="color:#1B4A98;cursor: pointer" onclick="javascript:void(0);competitionJobs.schemeClick(\'publicizend\')">' + tm.contendPos.status.publicizend + '</span>',//已公示
                    schemeId: 'publicizend',
                    margin: itemMargin
                }, {
                    xtype: 'component',
                    itemId: 'scheme_approvalFailed',
                    html: '<span style="color:#1B4A98;cursor: pointer" onclick="javascript:void(0);competitionJobs.schemeClick(\'approvalFailed\')">' + tm.contendPos.status.approvalFailed + '</span>',//已公示
                    schemeId: 'approvalFailed'
                }

            ]

        });
        return planContainer;

    },
    schemeClick: function (schemeId) {
        var selectComponent = Ext.ComponentQuery.query('component[cls=scheme-selected-cls]')[0];
        if (selectComponent.schemeId != schemeId) {//说明当前点击的和有选中样式的不是同一个
            selectComponent.removeCls('scheme-selected-cls');
            selectComponent.cls = '';
            var itemid = '#' + 'scheme_' + schemeId;
            var schemeLabel = Ext.ComponentQuery.query(itemid)[0];
            schemeLabel.addCls('scheme-selected-cls');
            schemeLabel.cls = "scheme-selected-cls";
            this.query(schemeId)
        }
    },
    query: function (schemeId) {
        var vo = new HashMap();
        vo.put("operateType", "search");
        vo.put("init", "0");//1 代表初始化  0 代表查询
        vo.put("queryMethod", schemeId);
        Rpc({
            functionId: 'TM000000002', success: function () {
                competitionJobs.tableObj.reloadStore();
            }, scope: this, async: false
        }, vo);
    },
    /**
     * 添加事件
     */
    addEvent: function () {
        competitionJobs.tableObj.tablePanel.on('beforeedit', function (e, c) {
            if (competitionJobs.from != 'jobs') {
                return false;
            }
            var status = c.record.data.z8103.split("`")[0];
            //只有暂停状态和已批准状态和起草状态可以修改数据
            // if (status == competitionJobs.status.suspend || status == competitionJobs.status.approved
            //     && status == competitionJobs.status.drafting && status == competitionJobs.status.refuse) {
            //     return false;
            // }
            if (c.value) {
                if (c.field == 'z8115' && c.value.split('`').length < 2) {

                    c.value = c.record.data.z8115 + "`" + c.record.data.orgdesc;
                }
            }
            if ( status == competitionJobs.status.drafting || status == competitionJobs.status.refuse) {//起草和退回
                return true;
            }
            if(status == competitionJobs.status.suspend || status == competitionJobs.status.approved){//暂停和批准
                if(c.field == 'z8105' || c.field == 'z8107'){//拟招聘人数和缺编人数不允许修改
                    return false;
                }else{
                    return true;
                }
            }else{
                return false;
            }
            c.column.currentRecord = c.record;
        });
        competitionJobs.tableObj.tablePanel.on('edit', function (editor, e) {
            if (!e.record.data.z8105) {
                e.record.set("z8105", 0);
            }
            if (!e.record.data.z8107) {
                e.record.set("z8107", 0);
            }

        });
        competitionJobs.tableObj.tablePanel.on('validateedit', function(editor, context) {
            //只有编辑结束时间时校验
            if(context.field =='z8113'){
                if (Ext.Date.parse(context.value, "Y-m-d H:i") < new Date()) {
                    Ext.Msg.alert(tm.contendPos.msg.title, tm.contendPos.msg.notAllowEndDatelowNowDate);
                    context.cancel = true;
                    return false;
                }
            }
        });
        competitionJobs.tableObj.tablePanel.on('cellClick', function (t, td, cellIndex, record, tr, rowIndex, e, eOpts) {
                // //给竞聘岗位列添加点击事件，考虑到栏目设置可以设置列是否显示，所以cellindex不能写死 为动态获取  （改用codetreecombox 封掉）
                // var columns = competitionJobs.tableObj.tablePanel.getColumnManager().getColumns();
                // var competitiveScopeColumn;
                // Ext.each(columns, function (item) {
                //     if (item.dataIndex == 'z8115') {
                //         competitiveScopeColumn = item;
                //     }
                // });
                // var competitiveScopeCellIndex;
                // if (competitiveScopeColumn) {
                //     competitiveScopeCellIndex = competitiveScopeColumn.getIndex();
                // }
                // if (competitiveScopeCellIndex != cellIndex) {
                //     return;
                // }
                // if (record.data.z8103.split("`")[0] == competitionJobs.status.suspend) { //只有暂停岗位可以进行维护信息
                //     competitionJobs.showOrgWin(record);
                // }
            }
        );
        competitionJobs.tableObj.tablePanel.on('render', function (t) {
            var z8115Column = t.getColumnManager().getHeaderByDataIndex("z8115");
            if (z8115Column) {
                var eidtior = {
                    xtype: "codecomboxfield", codesetid: "UM", onlySelectCodeset: false,
                    ctrltype: "3", nmodule: "4", multiple: true
                };
                z8115Column.setEditor(eidtior);
            }
            var z8105Column = t.getColumnManager().getHeaderByDataIndex("z8105");
            if (z8105Column) {
                z8105Column.getEditor().setMinValue(0);
            }
            var z8107Column = t.getColumnManager().getHeaderByDataIndex("z8107");
            if (z8107Column) {
                z8107Column.getEditor().setMinValue(0);
            }
        });
        competitionJobs.tableObj.dataStore.on("datachanged", function () {
            //竞聘范围map
            competitionJobs.z8115Map = {};
            //竞聘岗位map
            competitionJobs.e01a1Map = {};
            //当前页数据
            var currentRecords = competitionJobs.tableObj.dataStore.getData().items;
            for(var index in currentRecords){
                var record = currentRecords[index];
                if (record.data.z8115) {
                    competitionJobs.z8115Map[record.internalId] = record.data.z8115;
                }
                competitionJobs.e01a1Map[record.internalId] = record.data.e01a1.split("`")[0];
            }
            var vo = new HashMap();
            vo.put("operateType", "getCompetitiveScopeDesc");
            vo.put("z8115Map", competitionJobs.z8115Map);
            vo.put("e01a1Map", competitionJobs.e01a1Map);
            Rpc({
                functionId: 'TM000000002', success: function (res) {
                    //提交更改记录
                    var resData = Ext.decode(res.responseText);
                    var return_code = resData.return_code;
                    if (return_code == "success") {
                        competitionJobs.z8115Map = resData.return_data.z8115Map;
                        competitionJobs.e01a1Map = resData.return_data.e01a1Map;
                    } else {
                        //拼装数据出错，重置数据map
                        competitionJobs.z8115Map = {};
                        competitionJobs.e01a1Map = {};
                        Ext.Msg.alert(tm.tip, tm.contendPos.assembleDescAndEncryptError);
                    }
                }, scope: this, async: false
            }, vo);
        });
    },
    /**
     * 岗位编制按钮处理函数
     */
    jobPreparationFunc: function () {
        window.location.href = "JobPreparation.html";
    },
    /**
     * 新建按钮处理函数
     */
    createFunc: function () {
        var addPositionsPanel = Ext.create("Talentmarkets.competition.AddPositions", {

        });
        Ext.getCmp("competitionJobsTable_mainPanel").hide();
        competitionJobs.add(addPositionsPanel);
    },
    /**
     * 导入按钮处理函数
     */
    importFunc: function () {
        var win = new Ext.create('Ext.window.Window', {
            title: '导入岗位',
            id: 'importDataExcelId',
            width: 300,
            height: 180,
            modal: true,
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
                            handler: function () {
                                var outName = '';//下载文件
                                var flag = '0';//下载程序是否执行完成
                                var succeed = false;//下载程序是否执行成功
                                var map = new HashMap();
                                map.put("operateType", "importInit");
                                Rpc({
                                    functionId: 'TM000000002',
                                    timeout: 10000000,
                                    async: true,
                                    success: function (form, action) {
                                        var result = Ext.decode(form.responseText);
                                        flag = '1';//1表示下载程序执行完成
                                        succeed = result.succeed;
                                        if (succeed) {
                                            outName = result.return_data.templateUrl;
                                        }
                                    }
                                }, map);

                                var msgBox = Ext.create('Ext.window.MessageBox', {alwaysOnTop: true}).show({
                                    title: common.button.promptmessage,
                                    msg: '动态更新进度条和信息文字',
                                    modal: true,
                                    width: 300,
                                    progress: true
                                });
                                var progressText = '';//进度条信息
                                var task = {
                                    run: function () {
                                        //进度条信息
                                        progressText = '下载中...';
                                        //更新信息提示对话框
                                        msgBox.updateProgress('', progressText, '当前时间：' + Ext.util.Format.date(new Date(), 'Y-m-d g:i:s A'));
                                        //下载文件成功，关闭更新信息提示对话框
                                        if (flag == '1') {
                                            Ext.TaskManager.stop(task);
                                            msgBox.hide();
                                            if (succeed) {
                                                window.open("/servlet/vfsservlet?fileid=" + outName + "&fromjavafolder=true");
                                            } else {
                                                Ext.showAlert("导出失败！错误信息：" + result.message)
                                            }
                                        }
                                    },
                                    interval: 1000//时间间隔
                                };
                                Ext.TaskManager.start(task);
                            }
                        }
                    }]
            }, {
                layout: 'column',
                border: false,
                padding: '-30 0 10 0',
                width: 200,
                items: [{
                    columnWidth: 0.7,
                    border: false,
                    xtype: 'label',
                    text: '2、 请选择导入文件',
                }
                    , {
                        columnWidth: 0.3,
                        border: false,
                        items: {
                            xtype: 'button',
                            text: '浏览',
                            id: 'importResumeId',
                            padding: 0,
                            height: 22,
                            width: 38,
                        }
                    }]
            }, {
                xtype: 'box',
                border: false,
                width: 40,
                height: 22,
                margin: '-22 0 0 150',
                listeners: {
                    afterrender: function () {
                        Ext.require('SYSF.FileUpLoad', function () {
                            Ext.create("SYSF.FileUpLoad", {
                                upLoadType: 3,
                                isDelete: true,
                                height: 22,
                                width: 38,
                                renderTo: 'importResumeId',
                                fileSizeLimit: '20MB',
                                fileExt: "*.xls;*.xlsx",
                                buttonText: '',
                                isTempFile: true,
                                VfsFiletype:VfsFiletypeEnum.doc,
                                VfsModules:VfsModulesEnum.JP,
                                VfsCategory: VfsCategoryEnum.other,
                                CategoryGuidKey: '',
                                success: function (list) {
                                    var success = false;//是否上传成功
                                    var flag = '0';//上传程序是否执行完成
                                    var message = '';//报错提示信息
                                    var fileId = list[0].fileid;
                                    var map = new HashMap();
                                    map.put("operateType", "importData");
                                    map.put("fileId", fileId);
                                    map.put("flag", "1");
                                    Rpc({
                                        functionId: 'TM000000002', async: true, success: function (response, action) {
                                            var result = Ext.decode(response.responseText);
                                            flag = '1';
                                            success = result.success;
                                            if (result.return_code == "success") {
                                                Ext.getCmp("importDataExcelId").close();
                                                competitionJobs.tableObj.dataStore.reload();
                                                Ext.Msg.alert(tm.tip, tm.importSuccess);
                                            } else {
                                                Ext.getCmp("importDataExcelId").close();
                                                if (result.return_msg_code) {
                                                    Ext.Msg.alert(tm.tip,result.return_msg_code);
                                                } else {
                                                    var fileName = result.return_data.templateUrl;
                                                    Ext.Msg.alert(tm.tip, tm.importFail);
                                                    window.open("/servlet/vfsservlet?fileid=" + fileName + "&fromjavafolder=true");
                                                }
                                            }
                                        }
                                    }, map);

                                    var msgBox = Ext.create('Ext.window.MessageBox', {alwaysOnTop: true}).show({
                                        title: common.button.promptmessage,
                                        modal: true,
                                        width: 300,
                                        progress: true
                                    });

                                    var progressText = '正在导入数据,请稍等...';//进度条信息
                                    var task = {
                                        run: function () {
                                            //进度条信息
                                            //更新信息提示对话框
                                            msgBox.updateProgress('', progressText, '');
                                            //完成上传文件，关闭更新信息提示对话框
                                            if (flag == '1') {
                                                Ext.TaskManager.stop(task);
                                                msgBox.hide();
                                            }
                                        },
                                        interval: 500//时间间隔
                                    };
                                    Ext.TaskManager.start(task);
                                }
                            });
                            Ext.getDom("importResumeId").childNodes[1].style.marginTop = "-22px";
                        });
                    }
                }
            }]
        }).show();
    },
    /**
     * 删除按钮处理函数
     */
    deleteFunc: function () {
        var selectRecords = competitionJobs.tableObj.tablePanel.getSelectionModel().getSelection();
        if (selectRecords.length < 1) {
            Ext.Msg.alert(tm.contendPos.msg.title, tm.contendPos.msg.pleaseSelectRecord);
            return;
        }
        var isDelete = true;//可以删除
        for (var i = 0; i < selectRecords.length; i++) {
            var record = selectRecords[i];
            if (record.data.z8103.split("`")[0] == competitionJobs.status.application) {//申请中
                isDelete = false;
                break;
            }
        }
        if (!isDelete) {
            Ext.Msg.alert(tm.contendPos.msg.title, tm.contendPos.msg.noArrowDelAppliction);
            return;
        }
        Ext.Msg.confirm(tm.contendPos.msg.title, tm.contendPos.msg.isDeleteJobs, function (btn) {
            if (btn == 'yes') {
                var delIds = "";
                Ext.each(selectRecords, function (record) {
                    delIds += record.data.z8101_e + ",";
                });
                delIds = Ext.util.Format.substr(delIds, 0, delIds.length - 1);
                var map = new HashMap();
                map.put('jobIds', delIds);
                map.put("operateType", "delete");
                map.put("isConfim", "0");
                Rpc({
                    functionId: 'TM000000002', success: function (res) {
                        var resData = Ext.decode(res.responseText);
                        var return_code = resData.return_code;
                        if (return_code == "success") {
                            if (resData.return_data.needConfim == "1") {
                                Ext.Msg.confirm(tm.contendPos.msg.title, tm.contendPos.msg.delJobsHavePerson, function (btn) {
                                    if (btn == 'yes') {
                                        map.put("isConfim", "1");
                                        Rpc({
                                            functionId: 'TM000000002', success: function (res) {
                                                var resData = Ext.decode(res.responseText);
                                                var return_code = resData.return_code;
                                                if (return_code == "success") {
                                                    Ext.Msg.alert(tm.contendPos.msg.title, tm.contendPos.msg.delSuccess, function () {
                                                        competitionJobs.tableObj.dataStore.remove(selectRecords);
                                                    });
                                                } else {
                                                    Ext.Msg.alert(tm.contendPos.msg.title, tm.contendPos.msg.delRecordSql);
                                                }
                                            }, scope: this, async: false
                                        }, map);
                                    }
                                })
                            } else {
                                Ext.Msg.alert(tm.contendPos.msg.title, tm.contendPos.msg.delSuccess, function () {
                                    competitionJobs.tableObj.dataStore.remove(selectRecords);
                                });
                            }
                        } else {
                            Ext.Msg.alert(tm.contendPos.msg.title, tm.contendPos.msg.delRecordSql);
                        }
                    }, scope: this, async: false
                }, map);
            }
        });


    },
    /**
     * 暂停按钮处理函数
     */
    suspendFunc: function () {
        competitionJobs.changeStatus(competitionJobs.status.suspend);
    },
    /**
     * 发布按钮处理函数
     */
    publishFunc: function () {
        competitionJobs.changeStatus(competitionJobs.status.published);
    },
    /**
     * 结束按钮处理函数
     */
    endFunc: function () {
        var selectRecords = competitionJobs.tableObj.tablePanel.getSelectionModel().getSelection();
        if (selectRecords.length == 0) {
            Ext.Msg.alert(tm.contendPos.msg.title, tm.contendPos.msg.pleaseSelectRecord);
            return;
        }
        Ext.Msg.confirm(tm.contendPos.msg.title, tm.contendPos.msg.isEndJobs, function (btn) {
            if (btn == 'yes') {
                var excuteFlag = true;
                Ext.each(selectRecords, function (record) {
                    var z8103 = record.data.z8103.split("`")[0];
                    if (z8103 != competitionJobs.status.published && z8103 != competitionJobs.status.suspend) {
                        excuteFlag = false;
                        return;
                    }
                });
                if (!excuteFlag) {
                    Ext.Msg.alert(tm.contendPos.msg.title, tm.contendPos.msg.onlyEndPublishOrSuspend);
                    return;
                }
                var ids ="";
                Ext.each(selectRecords, function (record) {
                    ids += record.data.z8101_e + ",";
                });
                var map = new HashMap();
                map.put("operateType", "checkIngPersonInPost");
                map.put("state", competitionJobs.status.end);
                map.put("ids", ids);
                map.put("statusType", "2");
                //查询选择的岗位下是否有报名通过中、面试安排中、面试通过和拟录用审批中单据
                Rpc({
                    functionId: 'TM000000002', success: function (res) {
                        var resData = Ext.decode(res.responseText);
                        var return_code = resData.return_code;
                        var isHavePostList = resData.return_data.isHavePostList;
                        if (return_code == 'success') {
                        	if (isHavePostList && isHavePostList.length > 0) {
                        		var post_top = '';
                        		for(var i = 0 ; i < isHavePostList.length;i++){
                        			post_top += '，'+isHavePostList[i];
                        		}
                                Ext.Msg.alert(tm.contendPos.msg.title, tm.contendPos.ingPersonInThePostTip.replace('{post}',post_top.substring(1)));
                                return;
                            }
                            map.put("statusType", "1");
                            Rpc({
                                functionId: 'TM000000002', success: function (res) {
                                    //提交更改记录
                                    var resData = Ext.decode(res.responseText);
                                    var return_code = resData.return_code;
                                    var isHavePostList = resData.return_data.isHavePostList;
                                    if (isHavePostList && isHavePostList.length > 0 ) {
                                    	var post_top = '';
                                    	for(var i = 0 ; i < isHavePostList.length;i++){
                                    		post_top += '，'+isHavePostList[i];
                                    	}
                                        Ext.Msg.confirm(tm.contendPos.msg.title, tm.contendPos.msg.IncompleteProcessPersonnel.replace('{post}',post_top.substring(1)), function (btn) {
                                            if (btn == 'yes') {
                                                var map = new HashMap();
                                                map.put("isContinue",true);
                                                competitionJobs.changeStatus(competitionJobs.status.end,map);
                                            }
                                        });
                                    } else {
                                        competitionJobs.changeStatus(competitionJobs.status.end);
                                    }
                                }
                            },map);
                        }
                    }
                },map);
            }
        });
    },
    changeStatus: function (status,map) {
        var selectRecords = competitionJobs.tableObj.tablePanel.getSelectionModel().getSelection();
        if (selectRecords.length == 0) {
            Ext.Msg.alert(tm.contendPos.msg.title, tm.contendPos.msg.pleaseSelectRecord);
            return;
        }
        var excuteFlag = true;
        Ext.each(selectRecords, function (record) {
            var z8103 = record.data.z8103.split("`")[0];
            if (status == competitionJobs.status.suspend) {//暂停按钮
                if (z8103 != competitionJobs.status.published) {//不等于发布状态
                    excuteFlag = false;
                    return;
                }
            } else if (status == competitionJobs.status.published) {//发布按钮  只能发布已批准03  暂停04 起草 01
                var flag = z8103 != competitionJobs.status.approved && z8103 != competitionJobs.status.suspend && z8103 != competitionJobs.status.drafting
                if(competitionJobs.quickApprove){//开启二级审批 起草记录不允许直接发布
                    flag = z8103 != competitionJobs.status.approved && z8103 != competitionJobs.status.suspend;
                }
                if (flag) {
                    excuteFlag = false;
                    return;
                }
            }else if(status == competitionJobs.status.application){//报批按钮 只能报批 起草01、退回10 ===》申请中02
                if (z8103 != competitionJobs.status.drafting && z8103 != competitionJobs.status.refuse) {
                    excuteFlag = false;
                    return;
                }
            }else if(status == competitionJobs.status.approved){//批准按钮 只能批准 申请中02 ===》已批准03
                if (z8103 != competitionJobs.status.application) {
                    excuteFlag = false;
                    return;
                }
            }else if(status == competitionJobs.status.refuse){//退回按钮 只能退回 申请中02 ===》退回10
                if (z8103 != competitionJobs.status.application) {
                    excuteFlag = false;
                    return;
                }
            }
        });
        if (!excuteFlag) {
            if (status == competitionJobs.status.suspend) {
                Ext.Msg.alert(tm.contendPos.msg.title, tm.contendPos.msg.onlySuspendPublish);
            } else if (status == competitionJobs.status.published) {
                var str = tm.contendPos.msg.onlyPublishApplactionOrSuspendOrDraft;
                if(competitionJobs.quickApprove){
                    str = tm.contendPos.msg.onlyPublishApplactionOrSuspend;
                }
                Ext.Msg.alert(tm.contendPos.msg.title, str);
            }else if(status == competitionJobs.status.application){
                Ext.Msg.alert(tm.contendPos.msg.title, tm.contendPos.msg.onlyReportDraftingOrRefuse);
            }else if(status == competitionJobs.status.approved){
                Ext.Msg.alert(tm.contendPos.msg.title, tm.contendPos.msg.onlyApproveApplication);
            }else if(status == competitionJobs.status.refuse){
                Ext.Msg.alert(tm.contendPos.msg.title, tm.contendPos.msg.onlyRefuseApplication);
            }
            return;
        } else {
            var modifiedRecords = competitionJobs.tableObj.dataStore.getModifiedRecords();
            var isLegitimateDate = true;
            var ids = "";
            var isHaveNotSave =false;
            Ext.each(selectRecords, function (record) {
                ids += record.data.z8101_e + ",";
                if(Ext.Array.indexOf(modifiedRecords,record) != -1){ //说明发布的记录中有未保存的记录
                    isHaveNotSave = true;
                    return;
                }
                //发布时判断下开始时间和结束时间（是否为空 结束时间是否大于开始时间）
                if (status == competitionJobs.status.published) {
                    if (!record.get("z8111")||!record.get("z8113")) {
                        isLegitimateDate =false;
                        return;
                    }else{
                        if(Ext.Date.parse(record.get("z8111"),"Y-m-d H:i")>Ext.Date.parse(record.get("z8113"),"Y-m-d H:i")){
                            isLegitimateDate =false;
                        }
                    }
                }
            });
            if (isHaveNotSave) {
                Ext.Msg.alert(tm.contendPos.msg.title, tm.contendPos.msg.peleaseSaveData);
                return;
            }
            if(!isLegitimateDate){
                Ext.Msg.alert(tm.contendPos.msg.title, tm.contendPos.msg.notLegitimateDate);
                return;
            }
            Ext.util.Format.substr(ids, 0, ids.length - 1);
            var vo = new HashMap();
            vo.put("operateType", "changeState");
            vo.put("state", status);
            vo.put("ids", ids);
            if(status == competitionJobs.status.publicized){
                if(map){//公示组件传过来的数据  获取公告期 用于维护公告开始时期和公告结束日期
                    var notice_time = map.notice_time;
                    vo.put("notice_time",notice_time);
                }
            }
            if(status == competitionJobs.status.end){
                var isContinue = false;
                if(map){
                    isContinue = map.get("isContinue");
                }
                vo.put("isContinue", isContinue);
            }
            Rpc({
                functionId: 'TM000000002', success: function (res) {
                    //提交更改记录
                    var resData = Ext.decode(res.responseText);
                    var return_code = resData.return_code;
                    var statusDesc = "";
                    if (status == competitionJobs.status.published) {
                        statusDesc = Ext.util.Format.substr(tm.contendPos.status.published, 1, tm.contendPos.status.published.length);
                    } else if (status == competitionJobs.status.suspend) {
                        statusDesc = tm.contendPos.status.suspend;
                    } else if (status == competitionJobs.status.end) {
                        statusDesc = tm.contendPos.status.end;
                    } else if (status == competitionJobs.status.publicized) {
                        statusDesc = Ext.util.Format.substr(tm.contendPos.status.publicized, 0, tm.contendPos.status.publicized.length-1);
                    } else if (status == competitionJobs.status.application) {
                        statusDesc = tm.contendPos.status.report;
                    }else if (status == competitionJobs.status.approved) {
                        statusDesc = tm.contendPos.status.approve;
                    }else if (status == competitionJobs.status.refuse) {
                        statusDesc = tm.contendPos.status.refuse;
                    }
                    var msg = "";
                    if (return_code == "success") {
                        msg = tm.contendPos.msg.changeStateSuccess.replace("{status}", statusDesc);
                        Ext.Msg.alert(tm.contendPos.msg.title, msg, function () {
                            competitionJobs.tableObj.reloadStore();
                        });
                        if (status == competitionJobs.status.published) {
                            var noticeFlag = true;
                            var selectArr = [];
                            var postArr = [];
                            Ext.each(selectRecords, function (record) {
                                if (record.data.z8103.split("`")[0] == competitionJobs.status.suspend) {
                                    noticeFlag = false;
                                }
                                selectArr.push(record.data.z8115);
                                var desc = "";
                                if(record.data.b0110.split("`")[1]){
                                    desc +=record.data.b0110.split("`")[1];
                                }
                                if(record.data.e0122.split("`")[1]){
                                    desc += record.data.e0122.split("`")[1];
                                }
                                desc += " ";
                                if(record.data.e01a1.split("`")[1]){
                                    desc += record.data.e01a1.split("`")[1];
                                }
                                desc +=tm.contendPos.context;
                                // postArr.push(record.data.b0110.split("`")[1] + record.data.e0122.split("`")[1] + " " + record.data.e01a1.split("`")[1] + tm.contendPos.context);
                                postArr.push(desc);
                            });
                            if (!noticeFlag) {//从暂停转为发布不发送通知
                                return;
                            }
                            var param = new HashMap();
                            param.put("operateType", "savePublishedNotice");
                            param.put("selectArr", selectArr);
                            param.put("postArr", postArr);
                            param.put("topic", tm.contendPos.topic);
                            Rpc({
                                functionId: 'TM000000002', success: function (res) {
                                    var resData = Ext.decode(res.responseText);
                                    var return_code = resData.return_code;
                                    if (return_code == "fail") {
                                        Ext.Msg.alert(tm.contendPos.msg.title, tm.contendPos.msg.noticeFail);
                                    }
                                }, scope: this, async: false
                            }, param);
                        }
                        if(status == competitionJobs.status.publicized){
                            //公示后修改竞聘人员状态
                            var param = new HashMap();
                            param.put("operateType","changePersonnelStatus");
                            param.put("ids",ids);
                            Rpc({
                                functionId: 'TM000000002', success: function (res) {
                                    var resData = Ext.decode(res.responseText);
                                    var return_code = resData.return_code;
                                    var return_msg = resData.return_msg_code;
                                    if(return_code == "fail"){
                                        Ext.Msg.alert(tm.contendPos.msg.title, eval(return_msg));
                                    }
                                }, scope: this, async: false
                            },param);
                        }
                    } else {
                        var return_msg = resData.return_msg_code;
                        msg = eval(return_msg).replace("{status}", statusDesc);
                        Ext.Msg.alert(tm.contendPos.msg.title, msg);
                    }
                }, scope: this, async: false
            }, vo);

        }

    },

    /**
     * 保存按钮处理函数
     */
    saveFunc: function () {
        var modifiedRecords = competitionJobs.tableObj.dataStore.getModifiedRecords();
        if (modifiedRecords.length == 0) {
            return;
        }
        var recordList = [];
        for (var i = 0; i < modifiedRecords.length; i++) {
            var modifiedRecord = modifiedRecords[i];
            recordList.push(modifiedRecord.data);
        }
        var vo = new HashMap();
        vo.put("operateType", "save");
        vo.put("modifyDatas", recordList);
        Rpc({
            functionId: 'TM000000002', success: function (res) {
                //提交更改记录
                var resData = Ext.decode(res.responseText);
                var return_code = resData.return_code;
                if (return_code == "success") {
                    competitionJobs.tableObj.dataStore.commitChanges();
                    //保存成功  翻页自动保存时不提示保存成功
                    if (!competitionJobs.notShowAlertFlag) {
                        Ext.Msg.alert(tm.contendPos.msg.title, tm.contendPos.msg.saveSuccess);
                    }
                } else {
                    var return_msg = resData.return_msg_code;
                    Ext.Msg.alert(tm.contendPos.msg.title, eval(return_msg));
                }
            }, scope: this, async: false
        }, vo);
    },
    /**
     * 组织单元窗口
     */
    showOrgWin: function (record) {
        competitionJobs.currentSelectRecord = record;// 当前点击的单元格的数据
        var map = new HashMap();
        map.put('codesetidstr', 'UN,UM,@K');
        map.put('codesource', '');
        map.put('nmodule', '4');
        map.put('ctrltype', '3');//0： 不控制 ；1：管理范围； 2：操作单位； 3：业务范围
        map.put('parentid', '');
        map.put('searchtext', encodeURI(""));
        map.put('multiple', true);
        map.put('isencrypt', false);
        map.put('confirmtype', '1');
        map.put('title', '组织单元');
        map.put('height', 330);
        map.put('width', 350);
        map.put('callbackfunc', competitionJobs.getOrgList);
        Ext.require('EHR.orgTreePicker.OrgTreePicker', function () {
            Ext.create('EHR.orgTreePicker.OrgTreePicker', {map: map});
        }, this);
    },
    getOrgList: function (orgRecord) {
        var value = "";
        Ext.each(orgRecord, function (item) {
            value += item.codesetid + item.id + ",";
        });
        value = Ext.util.Format.substr(value, 0, value.length - 1);
        competitionJobs.currentSelectRecord.set("z8115", value);
    },
    /**
     * 竞聘范围列渲染拦截器
     * @param value
     * @param metaData
     * @param record
     * @param rowIndex
     * @param colIndex
     * @param store
     * @param view
     * @returns {*}
     */
    competitiveScopeRenderFunc: function (value, metaData, record, rowIndex, colIndex, store, view) {
        if (value) {
            //有"`"说明是在前台组件中后台选的值如：A`总部
            if (value.indexOf("`") > -1) {
                value = value.split("`")[1];
            } else {
                value = competitionJobs.z8115Map[record.internalId] ? competitionJobs.z8115Map[record.internalId] : '';
            }
            record.data.orgdesc = value;
            return value;
        } else {
            return "";
        }
    },
    /**
     * 公示按钮处理函数
     */
    publicityFunc: function () {
        var selectRecords = competitionJobs.tableObj.tablePanel.getSelectionModel().getSelection();
        if (selectRecords == 0) {
            Ext.Msg.alert(tm.contendPos.msg.title, tm.contendPos.msg.selectPublicityRecord);
            return;
        }
        var isEndFlag = true;
        Ext.each(selectRecords, function (record) {
            if (record.get('z8103').split("`")[0] != competitionJobs.status.end) {
                isEndFlag = false;
                return;
            }
        });
        if (!isEndFlag) {
            Ext.Msg.alert(tm.contendPos.msg.title, tm.contendPos.msg.onlyPublicityEndRecord);
            return;
        }
        var ids = "";
        Ext.each(selectRecords, function (record) {
            ids += record.get("z8101_e") + ",";
        });
        Ext.util.Format.substr(ids, ids.length - 1);

        var vo = new HashMap();
        vo.put("operateType", "publicity");
        vo.put("ids", ids);
        Rpc({
            functionId: 'TM000000002', success: function (res) {
                //提交更改记录
                var resData = Ext.decode(res.responseText);
                var return_code = resData.return_code;
                if (return_code == "success") {
                    var selectData = resData.return_data.selectData;
                    var notice_name = '内部竞聘人员信息公示';
                    var contentHtml = competitionJobs.createHtml(competitionJobs.alternativeItems, selectData, false);
                    Ext.require('NoticePath.Notice', function () {
                        var notice = Ext.create('NoticePath.Notice', {
                            title: '信息公示',
                            notice_name: notice_name,
                            notice_content: contentHtml,
                            notice_time: '5',
                            notice_seq: '1',
                            notice_object: '',
                            notice_select: true,
                            height: 500,
                            flag: 1,
                            isApproved: true,
                            // saveAfterCallBack: Ext.Function.bind(competitionJobs.changeStatus, competitionJobs, [competitionJobs.status.publicized])
                            saveAfterCallBack: function(map){
                                competitionJobs.changeStatus(competitionJobs.status.publicized,map);
                            }
                        });
                    });
                } else {
                    Ext.Msg.alert(tm.contendPos.msg.title, tm.contendPos.msg.publicitySql);
                }
            }, scope: this, async: false
        }, vo);
        /*
        var map = new HashMap();
        map.put("operateType", "checkIngPersonInPost");
        map.put("ids", ids);
        map.put("statusType", "2");
        //查询选择的岗位下是否有报名审核中单据
        Rpc({
            functionId: 'TM000000002', success: function (res) {
                //提交更改记录
                var resData = Ext.decode(res.responseText);
                var return_code = resData.return_code;
                var isHave = resData.return_data.isHave;
                if (return_code == 'success') {
                    if (isHave) {
                        Ext.Msg.alert(tm.contendPos.msg.title, tm.contendPos.ingPersonInThePostTip);
                        return;
                    } else {}
                }
            }
        },map);
		*/


    },
    createHtml: function (selectedItems, selectData, flag) {
        Ext.util.CSS.createStyleSheet(".noBorder div{border-color:#ffffff;}", "card_css");
        //border:none 覆盖掉ckeditor 的自带样式
        var tableHtml = competitionJobs.createTable(selectedItems, selectData, flag);

        /** 公告内容=文字+表格 */
        var d = new Date();
        var contentHtml = '';
        contentHtml +=
            '<div >' +
            '<p>'
            + '&nbsp; &nbsp; &nbsp; &nbsp;经学院审核，现将' + d.getFullYear() + '年招聘人员公示如下，'
            + '公示期为  年  月  日至  年  月  日，在公示期内如有异议请与人事处联系。（电话：__）<br />'
            + '附：招聘人员情况' +

            '</p>' + tableHtml +
            '<p style="text-align:right;">' +
            d.getFullYear() + '年' + (d.getMonth() + 1) + '月' + d.getDate() + '日' +
            '</p>' +
            '</div>';
        return contentHtml;
    },
    createTable: function (selectedItems, selectData, flag) {
        var tdStyle = "border:none;text-align:center; height:42px; line-height:24px;border-bottom:1px solid #e5e6e8;"
        var thStyle = 'border:none;background:#f5f5f5;height:42px; line-height:42px;font-family:"微软雅黑"; font-size:14px;color:#666;';
        var lineStyle = 'padding: 0 20px 0;margin: 20px 0;line-height: 1px;border-left: 200px solid #ddd;border-right: 200px solid #ddd;'
            + 'text-align: center;';
        var tableHtml = "";
        if (flag)
            tableHtml = "<div id = 'replaceStart' style='" + lineStyle + "'>此行开始往下请勿修改</div>";
        tableHtml += '<div class="hj-wzm-table">';
        tableHtml += '<table style="border:1px solid #e5e6e8;border-bottom:none;" width="100%" border="0" cellpadding="0" cellspacing="0">';

        // 表格列头
        tableHtml += '<tr>';
        tableHtml += '<th style="' + thStyle + '"; font-size:14px;color:#666;" scope="col">序号</th>';
        for (var j = 0; j < selectedItems.length; j++) {
            var text = selectedItems[j].dataName;
            tableHtml += '<th style="' + thStyle + '" scope="col">' + text + '</th>';
        }
        // tableHtml+='<th style="'+thStyle+'" scope="col">详情</th>';
        tableHtml += '</tr>';
        // 表格数据
        for (var i = 0; i < selectData.length; i++) {
            tableHtml += '<tr>';
            tableHtml += '<td style="' + tdStyle + '">' + (i + 1) + '</td>';
            var data = selectData[i];
            for (var w = 0; w < selectedItems.length; w++) {
                var itemid = selectedItems[w].dataValue;
                var text = data[itemid];
                if (typeof text == 'string' && text.indexOf('`') > -1) {
                    text = text.split('`')[1];
                }
                if(!text){
                    text = "";
                }
                tableHtml += '<td style="' + tdStyle + '">' + text + '</td>';

            }
            tableHtml += "<td style='" + tdStyle + "'>";//评审材料
            if (data.infoUrl)
                tableHtml += "<a href='" + data.infoUrl + "'  target='_blank'><img src='/images/new_module/icon1.png' /></a>";
            tableHtml += "</td>";

            tableHtml += '</tr>';
        }
        tableHtml += '</table>';
        tableHtml += '</div >';
        if (flag)
            tableHtml += "<div id = 'replaceEnd' style='" + lineStyle + "'>此行开始往上请勿修改</div>";
        return tableHtml;
    },

    /**
     * 面试安排列渲染拦截器
     */
    interviewArrangementRenderFunc: function (value, metaData, record, rowIndex, colIndex, store, view) {
        var z8101_e = record.get('z8101_e');
        var b0110 = record.get('b0110');
        var e0122 = record.get('e0122');
        var e01a1 = record.get('e01a1');
        var z8103 = record.get('z8103');
        var html = '<img src="../images/unInterview.png" style="width:28px;height:28px;"/>';
        if (z8103.split("`")[0] == competitionJobs.status.published) {
            html = '<img src="../images/interview.png" style="cursor:pointer;width:28px;height: 28px" ' +
                'onclick="competitionJobs.interviewArrangementClick(\'' + z8101_e + ',' + b0110 + ',' + e0122 + ',' + e01a1 + ',' + z8103 + '\')"/>';
        }
        return html;
    },
    /**
     * 面试安排按钮处理函数
     * @param record 跳转页面需要的参数
     */
    interviewArrangementClick: function (params) {
        var paramsArr = params.split(",");
        var z8103 = paramsArr[4].split("`")[0];
        if (z8103 == "04") {//已发布的岗位才能进行面试安排
            var z8101_e = paramsArr[0];//加密的竞聘岗位编号
            var b0110 = paramsArr[1].split("`")[1];
            var e0122 = paramsArr[2].split("`")[1];
            var e01a1 = paramsArr[3].split("`")[1];

            var title = b0110 + "/";
            if (e0122) {//部门有可能为空，因为单位下可直接挂岗位
                title = title + e0122 + "/";
            }
            title = title + e01a1;
            var interviewArrangePanel = Ext.create("Talentmarkets.competition.InterviewArrange", {
                posNum: z8101_e,
                title: title
            });
            Ext.getCmp("competitionJobsTable_mainPanel").hide();
            competitionJobs.add(interviewArrangePanel);
        }
    },
    returnHomePage: function () {
        window.location.href = '/module/talentmarkets/portaldashboard/PortalDashboard.html';
    },
    approvalProcessRenderFunc: function (value, metaData, record, rowIndex, colIndex, store, view) {//审批过程渲染拦截器
        var html = '';
        if(!value){
            html = '<img style="cursor: pointer;width:28px;height:28px;" src="../images/noApprove.png" ' +
                'onclick="competitionJobs.approvalProcessClick(\'' + rowIndex + '\')"/>';
        }else{
            html = '<img style="cursor: pointer;width:28px;height:28px;" src="../images/approve.png" ' +
                'onclick="competitionJobs.approvalProcessClick(\'' + rowIndex + '\')"/>';
        }
        return html;
    },
    approvalProcessClick: function (params) {//审批过程点击事件
        var rowIndex = params;
        var record = competitionJobs.tableObj.tablePanel.getStore().getData().getAt(rowIndex);
        var z8117 = record.get('z8117');
        var vo = new HashMap();
        vo.put('approvalValue', z8117);
        vo.put('operateType', 'approvalFormat');
        var value = [];
        Rpc({
            functionId: 'TM000000002', success: function (res) {
                //获取格式化后的审批过程信息
                var resData = Ext.decode(res.responseText);
                var return_code = resData.return_code;
                if (return_code == "success") {
                    value = resData.return_data.approvalValueList;
                } else {
                    Ext.Msg.alert(tm.contendPos.msg.title, tm.contendPos.getApproveDataFail);
                }
            }, scope: this, async: false
        }, vo);
        var html = "";
        //展示审批信息容器
        var fieldCmp = Ext.create("Ext.container.Container", {
            width: 460,
            height: 520,
            layout: {
                type: 'vbox',
                align: 'center'
            },
            scrollable: true,
            bodyStyle: 'overflow-y:auto;overflow-x:hidden;'
        });

        //判断是否存在审批数据
        if (value.length == 0) {//没有数据
            html = "<p style='font-size: 14px;color: #5c5c5c;'>" + tm.contendPos.noApproveData + "</p>";
            var image = Ext.create("Ext.Img", {
                width: 142,
                height: 142,
                src: '../images/notconfig.png'
            });
            var component = Ext.create("Ext.Component", {
                html: html
            });
            fieldCmp.add(image);
            fieldCmp.add(component);
        } else {
            //存在数据，组装审批过程html
            html = competitionJobs.getApproveHtml(value);
            var component = Ext.create("Ext.Component", {
                html: html
            });
            fieldCmp.add(component);
        }
        //审批过程弹窗
        var approvalProcessWin = Ext.create("Ext.window.Window", {
            title: tm.contendPos.competitivePosition,
            height: 520,
            //autoHeight:true,
            width: 460,
            modal: true,
            resizable: false
        });


        approvalProcessWin.add(fieldCmp);
        approvalProcessWin.show();
    },
    getApproveHtml: function (value) {//组装审批过程html
        var html = "<div style='overflow-y:auto;height:480px;width:430px;'><table border='0' cellspacing='0' cellpadding='0' width=100% class='workflow-tuli'>";
        for (var z = 0; z < value.length; z++) {
            var map = value[z];
            var unit = map.approverUnit == '' ? '' : (map.approverUnit + '/');
            var dept = map.approverDepartment == '' ? '' : map.approverDepartment;
            var img = "";
            if (map.approverOpinion != '') {
                if (map.approverOpinion == tm.contendPos.approverAgree)
                    img = "<image width='40px' height='40px' src='../images/agree.png' />";
                else
                    img = "<image width='40px' height='40px' src='../images/disagree.png' />";
            }
            //var firstdesc=unit+dept+'<br/>';
            var seconddesc = map.approverName + '&nbsp;&nbsp;' + map.approvalTime + '<br/>';
            var thirddesc = map.approverAnnotation;

            html += "<tr><td width='80px'></td><td width='12px' class='workflow-timeLine-shortline'></td><td></td></tr>"
                + "<tr><td width='45px;'><div class='workflow-approver'>";

            if (z == 0) {
                html += tm.contendPos.applicantFills;
            } else {
                html += map.approverName;
            }

            html += "</div></td>" +
                "<td width='12px' class='workflow-timeLine-longline'>" +
                //"<table width='12px'><tr><td><div class='workflow-timeLine-line'/></td></tr><tr><td><div class='workflow-timeLine-point'/></td></tr><tr><td><div class='workflow-timeLine-line' /></td></tr></table>" +
                //"<div style='width:2px; background-color:#35baf6; position:fixed; top:0px; left:50%; height:100%;'></div>"+
                "<div class='workflow-timeLine-point'></div>" +
                "</td>" +
                "<td style='padding-left:10px;padding-right:20px;'>" +
                "<div class='workflow-timeline-textarr'>" +
                "<div class='arrow'><em></em><span></span></div><table width='100%' style='font-size:1em;word-break:break-all; word-wrap:break-word;'><tr><td valign='top' width='80px' style='padding-top: 6px;'>" +
                map.approverName + "</td><td valign='top' style='margin-left:5px;padding-top: 6px;'>" + map.approvalTime + "</td>" +
                "<td width='30px' valign='middle' rowspan='2'>" + img + "</td></tr><tr><td valign='top' colspan='2'>" + map.approverAnnotation + "</td></tr></table>" +
                "</div>" +
                "</td></tr>"
                + "<tr><td ></td><td width='12px' class='workflow-timeLine-shortline'></td><td></td></tr>";
        }

        html += "</table></div>";
        return html;
    },
    //岗位名称列渲染拦截器
    renderJobsColumnFunc:function (value, metaData, record, rowIndex, colIndex, store, view) {
        var e01a1_e = competitionJobs.e01a1Map[record.internalId];
        var displayValue = "";
        if(value){
            displayValue = value.split("`")[1];
        }
        var html = displayValue;
        if(competitionJobs.isHavePosCardId){
            html = "<a href=javascript:competitionJobs.jobsColumnClick('" + e01a1_e + "','" + displayValue + "');>" + displayValue + "</a>";
        }
        return html;
    },
    /**
     * 岗位名称点击事件处理函数  用于展现岗位说明书
     */
    jobsColumnClick:function (e01a1_e, e01a1Desc) {
        if(!competitionJobs.postDetailRnameId){
            //'请先配置应聘简历登记表！';
            Ext.Msg.alert(tm.tip,tm.contendPos.msg.notSetPostDetailRname);
            return;
        }
        var src = '/module/card/cardCommonSearch.jsp?inforkind=6&a0100=' + e01a1_e + '&tabid=' + competitionJobs.postDetailRnameId;
        var registrationFormWin = Ext.create('Ext.window.Window', {
            width: '100%',
            height: Ext.getBody().getViewSize().height,
            title: e01a1Desc,//岗位详情登记表
            modal: true,//遮罩
            resizable: false,//禁止拉伸
            draggable: false,//紧张拖拽
            html: '<iframe id="iframeId" frameborder="0" width="100%" height="100%" src="' + src + '"></iframe>',
            listeners: {
                resize: function () {
                    //浏览器放缩时，重新计算高度，否则没有滚动条
                    if (registrationFormWin) {
                        registrationFormWin.setHeight(Ext.getBody().getViewSize().height);
                    }
                }
            }
        }).show();

        //隐藏toolbar
        var interval = setInterval(function(){
            var iframeExt = document.getElementById("iframeId").contentWindow.Ext;
            if(iframeExt){
                var cardPanel = iframeExt.getCmp('cardtabPanelId');
                if (cardPanel) {
                    var ownerCt = cardPanel.ownerCt;
                    ownerCt.setBorder(false);
                    ownerCt.getDockedItems()[0].hide();
                    clearInterval(interval);
                }
            }
        },500);
    },
    /**
     * 导出面试名单
     */
    exportInterviewList:function () {
        var selectRecords = competitionJobs.tableObj.tablePanel.getSelectionModel().getSelection();;
        if(selectRecords.length<1){
            Ext.Msg.alert(tm.contendPos.msg.title, tm.contendPos.msg.pleaseSelectRecord);
            return;
        }
        var z8101_e = "";
        Ext.each(selectRecords,function (record) {
            z8101_e += record.get("z8101_e");
            z8101_e += ",";
        });
        z8101_e = Ext.util.Format.substr(z8101_e, 0, z8101_e.length - 1);
        var vo = new HashMap();
        vo.put("operateType","exportInterviewList");
        vo.put("z8101_e",z8101_e);
        Rpc({
            functionId: 'TM000000002', success: function (res) {
                //获取格式化后的审批过程信息
                var resData = Ext.decode(res.responseText);
                var return_code = resData.return_code;
                var return_msg = resData.return_msg_code;
                if (return_code == "success") {
                    var fileName = resData.return_data.fileName;
                    if (fileName) {
                        window.open("/servlet/vfsservlet?fileid=" + fileName + "&fromjavafolder=true");
                    }

                } else {
                    Ext.Msg.alert(tm.contendPos.msg.title, tm.contendPos.msg.exportInterviewListError);
                }
            }, scope: this, async: false
        }, vo);
    },
    /**
     * 应聘人数自定义渲染
     */
    applyPsnCountRenderFunc: function (value, metaData, record) {
        var html = "";
        var z8103 = record.data.z8103.split("`")[0];//岗位状态
        var z8101_e = record.data.z8101_e;//加密pos编号

        if (value) {
            html = "<a href=javascript:competitionJobs.applyPsnCountClickFunc('" + z8103 + "','" + z8101_e + "');>" + value + "</a>";
        }
        return html;
    },
    /**
     * 应聘人数点击事件
     * @param z8103 竞聘岗位状态
     * @param z8101_e 加密竞聘岗位编号
     */
    applyPsnCountClickFunc: function (z8103,z8101_e) {
        if(!competitionJobs.psnPriv) {
            Ext.Msg.alert(tm.tip, tm.contendPos.noPsnPriv);//'您没有查看竞聘人员的权限！'
            return;
        }
        var searchParams = location.search.substring(1);
        var statusValue = "";
        if (searchParams) {
            var paramsArr = searchParams.split("&");
            statusValue = paramsArr[1].split("=")[1];
        }

        //默认岗位是已经结束或暂停的数据
        var status = "history";
        if (z8103 == "04") {//已发布
            status = "apply";
        }
        var targetHref = '/module/talentmarkets/competition/Competitors.html?from=pos&status=' + status + '&pos=' + z8101_e;

        //当前岗位页面是什么状态的数据，穿透应聘人数，人员列表页面返回按钮用
        if (statusValue) {
            targetHref = targetHref + '&firstStatus=' + statusValue;
        }
        window.location.href = targetHref;
    },
    /**
     * 报批按钮处理函数
     */
    reportFunc:function () {
        competitionJobs.changeStatus(competitionJobs.status.application);
    },
    /**
     * 批准按钮处理函数
     */
    approveFunc:function () {
        competitionJobs.changeStatus(competitionJobs.status.approved);
    },
    /**
     * 退回按钮处理函数
     */
    refuseFunc:function () {
        competitionJobs.changeStatus(competitionJobs.status.refuse);
    },
    /**
     * 表格组件导出excel 二开支持导出竞聘范围
     */
    exportFunc:function () {
        var hashvo = new HashMap();
        hashvo.put("tablekey",competitionJobs.tableObj.subModuleId);
        hashvo.put("subModuleId",competitionJobs.tableObj.subModuleId);
        var myMask = new Ext.LoadMask({
            msg    : '正在导出......',
            target : competitionJobs.tableObj.bodyPanel
        }).show();
        var displaycolumns = competitionJobs.tableObj.tablePanel.getColumnManager().getColumns();
        var outputcolumns = new Array();
        var column;
        var level = 1;
        for(var i in displaycolumns){
            column =  displaycolumns[i];

            if(column.dataIndex && column.dataIndex.length>0 && !column.hidden && column.beExport){
                var ups = [];
                competitionJobs.tableObj.getUpColumnText(column,ups);
                level = ups.length+1>level?ups.length+1:level;
                outputcolumns.push({columnid:column.dataIndex,width:column.width,operationData:column.operationData?column.operationData:[],ups:ups});
            }
        }
        hashvo.put("outputcolumns",outputcolumns);
        hashvo.put("headLevel",level);
        if(competitionJobs.tableObj.config.selectable){
            var selectRecord = competitionJobs.tableObj.tablePanel.getSelectionModel().getSelection();
            if(selectRecord.length>0){
                var outputdata = new Array();
                for(var k in selectRecord){
                    outputdata.push(selectRecord[k].data);
                }

                hashvo.put("outputdata",outputdata);
            }
        }
        hashvo.put("summaryData",competitionJobs.tableObj.dataStore.summaryData);
        Rpc({functionId:'TM000000015',timeout:300000,scope:this,success:function(res){
                myMask.destroy( );
                var resultObj = Ext.decode(res.responseText);
                var filename = resultObj.filename;
                window.open("/servlet/vfsservlet?fileid=" + filename + "&fromjavafolder=true");
            }},hashvo);
    }
});
