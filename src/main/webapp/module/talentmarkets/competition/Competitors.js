/**
 * 竞聘人员列表页面
 * wangbs 2019-7-29
 */
Ext.define('Talentmarkets.competition.Competitors',{
    extend:'Ext.Panel',
    id: 'competitorsPage',
    layout: 'fit',
    fromValue : "",
    statusValue : "",
    posValue : "",
    initComponent:function(){
        CompetitionScope = this;
        CompetitionScope.showAlertFlag = true;
        this.callParent();
        this.loadData();
    },
    /**
     * 加载数据
     */
    loadData: function () {
        //获取连接参数
        if (location.search) {
            //截取需要的参数
            var searchParams = location.search.substring(1);
            var paramsArr = searchParams.split("&");
            CompetitionScope.fromValue = paramsArr[0].split("=")[1];
            CompetitionScope.statusValue = paramsArr[1].split("=")[1];
            if (paramsArr[2]) {
                CompetitionScope.posValue = paramsArr[2].split("=")[1];
            }
            if (paramsArr[3]) {
                CompetitionScope.firstStatus = paramsArr[3].split("=")[1];
            }
            if (paramsArr[4]) {//点击门户页面图标进来，修改gridpanel的title
                CompetitionScope.gridTitle = $URL.decode(paramsArr[4]).split("=")[1];
            }
        }
        var map = new HashMap();
        map.put("operateType", "search");
        map.put("from", CompetitionScope.fromValue);//连接从哪来
        map.put("status", CompetitionScope.statusValue);//查询当前||历史
        map.put("posId", CompetitionScope.posValue);//点击柱子查询某岗位的申报人列表
        Rpc({functionId: 'TM000000004', success: this.createPageLayout, scope: this}, map);
    },
    /**
     * 创建页面布局
     */
    createPageLayout:function (res) {
        var resultData = Ext.decode(res.responseText);
        //是否开启面试环节
        CompetitionScope.interviewOn = resultData.openInterview;
        //人员登记表id
        CompetitionScope.tabid = resultData.tabid;
        //有无配置的登记表权限
        CompetitionScope.tablePriv = resultData.tablePriv;
        if(resultData.return_code=='success') {
            var tableConfig = Ext.decode(resultData.tableConfig);
            tableConfig.title = CompetitionScope.gridTitle ? CompetitionScope.gridTitle : tableConfig.title;
            tableConfig.onChangePage = function (){
                CompetitionScope.showAlertFlag = false;
                CompetitionScope.savePsnData([], "");
            };
            //开启查询框（点击搜索框弹出的组件中下拉框数据拼装）
            tableConfig.openColumnQuery = true;

            CompetitionScope.tableObj = new BuildTableObj(tableConfig);
            CompetitionScope.tableObj.tablePanel.on('beforeedit', function (e, c) {
                if (c.field == "z8307") {//对面试分数进行能否编辑的过滤
                    var status = c.record.data.z8303.split("`")[0];
                    //状态不为04 || 从门户页面进来的  不能修改分数
                    if (status != "04" || CompetitionScope.fromValue=="portal") {
                        return false;
                    }
                }
            });
            CompetitionScope.add(CompetitionScope.tableObj.getMainPanel());
        }else{
            Ext.Msg.alert(tm.tip, resultData.return_msg);
        }
    },
    /**
     * 返回按钮点击事件
     * @param returnTarget 返回哪一页
     */
    returnHomePage: function (returnTarget) {
        if (returnTarget == "pos") {
            var tempHref = '/module/talentmarkets/competition/CompetitionJobs.html';
            if (CompetitionScope.firstStatus == "current") {
                tempHref = tempHref + '?from=portal&status=current';
            } else if (CompetitionScope.firstStatus == "history") {
                tempHref = tempHref + '?from=portal&status=history';
            }
            window.location.href = tempHref;
        } else {
            window.location.href = '/module/talentmarkets/portaldashboard/PortalDashboard.html';
        }
    },
    /**
     * 自定义渲染姓名列
     * @param value
     * @param metaData
     * @param record
     */
    renderNameColumn: function (value, metaData, record) {
        //没有登记表权限  不给超链接
        if (CompetitionScope.tablePriv == "false") {
            return value;
        }

        var nbase = record.data.nbase_e;
        var a0100 = record.data.a0100_e;
        var nbaseA0100 = nbase +"`"+ a0100;
        var html = "<a href=javascript:CompetitionScope.nameColumnClick('" + nbaseA0100 + "');>" + value + "</a>";
        return html;
    },
    /**
     * 姓名点击事件
     * @param nbaseA0100
     */
    nameColumnClick: function (nbaseA0100) {
        if(!CompetitionScope.tabid){
            //'请先前往配置页面配置应聘简历登记表！';
            Ext.Msg.alert(tm.tip, tm.contendPsn.selectApplyRegistrationForm);
            return;
        }
        var map = new HashMap();
        map.put("operateType", "encrypt");
        map.put("nbaseA0100", nbaseA0100);
        Rpc({
            functionId: 'TM000000004', async: false, success: function (res) {
                var returnMap = Ext.decode(res.responseText);
                nbaseA0100 = returnMap.nbaseA0100;
            }, scope: this
        }, map);

        var src = '/module/card/cardCommonSearch.jsp?fieldpriv=1&inforkind=1&a0100=' + nbaseA0100 + '&tabid=' + CompetitionScope.tabid;
        var registrationFormWin = Ext.create('Ext.window.Window', {
            width: '100%',
            height: Ext.getBody().getViewSize().height,
            title: tm.contendPsn.applyRegistrationForm,//应聘简历登记表
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

        //隐藏toolbar和边框线
        var interval = setInterval(function(){
            var iframeExt = document.getElementById("iframeId").contentWindow.Ext;
            if(iframeExt){
                var cardPanel = iframeExt.getCmp('cardtabPanelId');
                if (cardPanel) {
                    var ownerCt = cardPanel.ownerCt;
                    ownerCt.getDockedItems()[0].hide();
                    ownerCt.setBodyStyle({
                        border: 'none'
                    });
                    clearInterval(interval);
                }
            }
        },1000);
    },

    /**
     * 编辑分数后的校验
     */
    gradeValidate: function (value) {
        var errMsg = "";
        if (value) {
            if (value < 0) {
                errMsg = tm.contendPsn.noLessZore;//"面试分数不能低于0分"
            }
        }
        return errMsg ? errMsg : true;
    },
    /**
     * 导入成绩
     */
    importScore:function () {
        Ext.getBody().mask();
        var win = new Ext.create('Ext.window.Window', {
            title: '导入成绩',
            id: 'importDataExcelId',
            width: 300,
            height: 180,
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
                                map.put("from", CompetitionScope.fromValue);//连接从哪来
                                map.put("status", CompetitionScope.statusValue);//查询当前||历史
                                map.put("posId", CompetitionScope.posValue);//点击柱子查询某岗位的申报人列表
                                Rpc({
                                    functionId: 'TM000000004',
                                    timeout: 10000000,
                                    async: true,
                                    success: function (form, action) {
                                        var result = Ext.decode(form.responseText);
                                        flag = '1';//1表示下载程序执行完成
                                        succeed = result.return_code;
                                        if (succeed=="success") {
                                            outName = result.return_data.templateUrl;
                                        }else {
                                            Ext.Msg.alert(tm.contendPos.msg.title,result.return_msg);
                                            Ext.TaskManager.stop(task);
                                            msgBox.hide();
                                            win.close();
                                            return;
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
                                }
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
                                    map.put("fileId", fileId);
                                    map.put("operateType", "importData");
                                    Rpc({
                                        functionId: 'TM000000004', async: true, success: function (response, action) {
                                            var result = Ext.decode(response.responseText);
                                            flag = '1';
                                            if (result.return_code == 'success') {
                                                Ext.getCmp('importDataExcelId').close();
                                                var tableObj = Ext.getCmp("competitionPsn_tablePanel");
                                                tableObj.getStore().reload();
                                                Ext.Msg.alert(tm.tip, tm.contendPsn.importSuccess);
                                            } else {
                                                if (result.return_msg) {
                                                    msgBox.hide();
                                                    Ext.Msg.alert(tm.tip, result.return_msg);
                                                } else {
                                                    Ext.getCmp('importDataExcelId').close();
                                                    Ext.Msg.alert(tm.tip, tm.contendPsn.importFail);
                                                    var fileName = result.return_data.templateUrl;
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
        win.on("close", function () {
            Ext.getBody().unmask();
        });
    },
    /**
     * 面试通过按钮：将某记录竞聘状态更新为面试通过
     */
    interviewPass: function (obj, selectedData) {
        CompetitionScope.updateCompetionStatus(selectedData, "05");
    },
    /**
     * 面试未通过按钮：将某记录竞聘状态更新为面试未通过
     */
    interviewNotPass: function (obj, selectedData) {
        CompetitionScope.updateCompetionStatus(selectedData, "06");
    },
    /**
     * 更新记录状态
     * @param selectedData 勾选的数据
     * @param compeStatus 竞聘状态
     */
    updateCompetionStatus: function (selectedData, compeStatus) {
        if (selectedData.length == 0) {
            Ext.Msg.alert(tm.tip, tm.contendPsn.firstSelectData);//请先勾选要操作的数据
            return;
        }
        var onlyFlagArr = [];
        for (var i = 0; i < selectedData.length; i++) {
            var oneData = selectedData[i].data;
            var z8101 = oneData.z8101_e;//岗位编号
            var z8301 = oneData.z8301;//guidkey
            var status = oneData.z8303.split("`")[0];//竞聘状态
            var grade = oneData.z8307;//分数
            if (!(status == "04")) {
                Ext.Msg.alert(tm.tip, tm.contendPsn.interviewPlaning);//只能修改状态为安排面试中的数据
                return;
            }
            if (grade != 0 && !grade) {
                Ext.Msg.alert(tm.tip, tm.contendPsn.fillGrade);//请先填写面试分数
                return;
            }
            var onlyFlag = z8101 + "`" + z8301;
            onlyFlagArr.push(onlyFlag);
        }
        CompetitionScope.savePsnData(onlyFlagArr, compeStatus);
    },

    /**
     * 保存人员数据
     * @param onlyFlagArr 点击面试通过或面试未通过时有值
     * @param compeStatus 竞聘状态
     */
    savePsnData: function (onlyFlagArr, compeStatus) {
        var changeFlag = false;//默认没有修改数据
        var modifyDataList = [];
        var modifiedRecords = CompetitionScope.tableObj.dataStore.getModifiedRecords();
        for (var i = 0; i < modifiedRecords.length; i++) {
            var oneRecord = modifiedRecords[i];

            var oneData = oneRecord.data;//该行所有数据
            var z8101 = oneData.z8101_e;//岗位编号
            var z8301 = oneData.z8301;//guidkey
            var tempOnlyFlag = z8101 + "`" + z8301;

            var tempData = {};
            //联合主键更新数据用
            tempData.z8101 = z8101;
            tempData.z8301 = z8301;

            var oneDataModified = oneRecord.modified;//该记录被修改的字段
            for (var key in oneDataModified) {
                tempData[key] = oneData[key];
                changeFlag = true;
            }

            for (var j = 0; j < onlyFlagArr.length; j++) {
                if (onlyFlagArr[j] === tempOnlyFlag) {
                    tempData.z8303 = compeStatus;
                    onlyFlagArr.splice(j, 1);
                    break;
                }
            }
            modifyDataList.push(tempData);
        }

        //只改变数据状态
        if (onlyFlagArr.length > 0) {
            changeFlag = true;
            for (var i = 0; i < onlyFlagArr.length; i++) {
                var oneOnlyFlagArr = onlyFlagArr[i].split("`");
                var tempData = {};
                //联合主键更新数据用
                tempData.z8101 = oneOnlyFlagArr[0];
                tempData.z8301 = oneOnlyFlagArr[1];
                tempData.z8303 = compeStatus;
                modifyDataList.push(tempData);
            }
        }
        if (!changeFlag) {
            //假如面试安排中的数据在第二页，第一页未进行修改翻页后该标志位为false，再修改面试分数点击保存，alert出不来，翻页前数据未修改时此处置为true
            CompetitionScope.showAlertFlag = true;
            return;
        }
        var map = new HashMap();
        map.put("operateType", "saveGridData");
        map.put("modifyDataList", modifyDataList);
        Rpc({
            functionId: 'TM000000004', success: function (res) {
                var resultData = Ext.decode(res.responseText);
                if (resultData.return_code == 'success') {
                    CompetitionScope.tableObj.dataStore.commitChanges();
                    CompetitionScope.tableObj.dataStore.reload();

                    //保存成功  点击保存按钮时提示成功、翻页自动保存时不提示保存成功
                    if (CompetitionScope.showAlertFlag) {
                        Ext.Msg.alert(tm.tip, tm.saveSuccess);//保存成功
                    }
                } else {
                    Ext.Msg.alert(tm.tip, eval(resultData.return_msg));
                }
                CompetitionScope.showAlertFlag = true;
            }, scope: this
        }, map);
    },
    /**
     * 保存人员数据
     */
    savePsnBtnData: function () {
        CompetitionScope.savePsnData([], "");
    },
    /**
     * 导出简历PDF
     */
    exportPDF : function () {
        var tableObj = Ext.getCmp("competitionPsn_tablePanel");
        var items = tableObj.getSelectionModel().getSelection();
        if (items == null || items.length == 0) {
            Ext.Msg.alert(tm.tip, tm.contendPsn.exportPdfSelectError);
            return;
        }
        var selectIdArr = [];
        for (var i = 0; i < items.length; i++) {
            var tempGuid = items[i].data.z8301;
            selectIdArr.push(tempGuid);
        }
        var selectIds = selectIdArr.join(",");
        var map = new HashMap();
        map.put("operateType", "exportPdf");
        map.put("selectIds", selectIds);
        Rpc({
            functionId: 'TM000000004', success: function (res) {
                var resultData = Ext.decode(res.responseText);
                if (resultData.return_code == 'success') {
                    var url = resultData.return_data.url;
                    window.open("/servlet/vfsservlet?fileid=" + url + "&fromjavafolder=true");
                } else {
                    if (resultData.return_code == "saveGridDataError") {
                        Ext.Msg.alert(tm.tip, tm.contendPsn.exportPdfError);
                    }
                }
                CompetitionScope.showAlertFlag = true;
            }, scope: this
        }, map);
    },
    //拟录用审批按钮处理函数
    draftEmployApprovalFunc:function () {
        var tableObj = Ext.getCmp("competitionPsn_tablePanel");
        var selectDataArray = new Array();
        for (var i = 0; i < tableObj.getSelectionModel().getSelection().length; i++) {
            selectDataArray.push(tableObj.getSelectionModel().getSelection()[i].data);
        }
        if (selectDataArray.length < 1) {
            Ext.Msg.alert(tm.contendPos.msg.title, tm.contendPos.msg.pleaseSelectRecord);
            return;
        }
        var isNotInterview = false;
        Ext.each(selectDataArray, function (item) {
            if (CompetitionScope.interviewOn) {//开启了面试环节只能录用面试通过的人员
                if (item.z8303.split("`")[0] != '05') {
                    isNotInterview = true;
                    return;
                }
            } else {//未开启面试环节可以录用报名通过、安排面试中、面试通过三种状态的人员
                var ruleStr = "02,04,05";
                if (ruleStr.indexOf(item.z8303.split("`")[0]) == -1) {
                    isNotInterview = true;
                    return;
                }
            }
        });
        if(isNotInterview){
            var tipContent = tm.contendPsn.appealInterview;
            if (!CompetitionScope.interviewOn) {
                tipContent = tm.contendPsn.appealInterview1;
            }
            Ext.Msg.alert(tm.contendPos.msg.title, tipContent);
            return;
        }
        var personMap = new HashMap();
        var doublePersonFlag = false;
        Ext.each(selectDataArray, function (item) {
            var nbase = item.nbase_e;
            var a0100 = item.a0100_e;
            var key = nbase+a0100;
            if(!personMap.get(key)){
                personMap.put(key,1);
            }else{
                var count = personMap.get(key);
                count = count +1;
                if(count >1){
                    doublePersonFlag = true;
                    return;
                }
            }
        });
        if(doublePersonFlag){
            Ext.Msg.alert(tm.contendPos.msg.title, tm.contendPsn.onePeopleDoublePostTip);
            return;
        }
        var map = new HashMap();
        map.put("records", selectDataArray);
        map.put("templateType", "hireTemplate");
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
                    templateObj.callBack_init = "CompetitionScope.tempFunc";
                    templateObj.callBack_close = "CompetitionScope.goBack";
                    Ext.require('TemplateMainUL.TemplateMain', function () {
                        Ext.create("TemplateMainUL.TemplateMain", {templPropety: templateObj});
                    });
                } else {
                    if (return_msg_code == 'noSetingData') {
                        Ext.Msg.alert(tm.contendPos.msg.title, tm.contendPos.pelaseSeting);
                    } else if (return_msg_code == 'notSetHireTemplateRelation') {
                        Ext.Msg.alert(tm.contendPos.msg.title, tm.contendPos.competitorsNoSetPlan);
                    } else if (return_msg_code == 'initTempTemplateTableError') {
                        Ext.Msg.alert(tm.contendPos.msg.title, tm.contendPos.initTempTemplateTableError);
                    }else{
                        Ext.Msg.alert(tm.contendPos.msg.title,return_msg_code);
                    }

                }
            }, async: false
        }, map);
    },
    tempFunc: function () {
        CompetitionScope.removeAll();
        CompetitionScope.add(templateMain_me.mainPanel);
    },
    goBack: function () {
        window.location.reload();
    },
    approvalProcessRenderFunc:function (value, metaData, record, rowIndex, colIndex, store, view) {//审批过程渲染拦截器
        var html = '';
        if(!value){
            html = '<img style="cursor: pointer;width:28px;height:28px;" src="../images/noApprove.png" ' +
                'onclick="CompetitionScope.approvalProcessClick(\'' + rowIndex + '\')"/>';
        }else{
            html = '<img style="cursor: pointer;width:28px;height:28px;" src="../images/approve.png" ' +
                'onclick="CompetitionScope.approvalProcessClick(\'' + rowIndex + '\')"/>';
        }
        return html;
    },
    approvalProcessClick:function (params) {//审批过程点击事件
        var rowIndex = params;
        var record = CompetitionScope.tableObj.tablePanel.getStore().getData().getAt(rowIndex);
        var z8309 = record.get('z8309');
        var vo = new HashMap();
        vo.put('approvalValue',z8309);
        vo.put('operateType','approvalFormatData');
        var value = [];
        Rpc({
            functionId: 'TM000000004', success: function (res) {
                //获取格式化后的审批过程信息
                var resData = Ext.decode(res.responseText);
                var return_code = resData.return_code;
                if (return_code == "success") {
                    value = resData.approvalValueList;
                }else{
                    Ext.Msg.alert(tm.contendPos.msg.title, tm.contendPos.getApproveDataFail);
                }
            }, scope: this, async: false
        }, vo);
        var html = "";
        //展示审批信息容器
        var fieldCmp = Ext.create("Ext.container.Container",{
            width:460,
            height:520,
            layout:{
                type:'vbox',
                align:'center'
            },
            scrollable:true,
            bodyStyle:'overflow-y:auto;overflow-x:hidden;'
        });

        //判断是否存在审批数据
        if(value.length == 0){//没有数据
            html = "<p style='font-size: 14px;color: #5c5c5c;'>" + tm.contendPos.noApproveData +"</p>";
            var image = Ext.create("Ext.Img",{
                width:142,
                height:142,
                src:'../images/notconfig.png'
            });
            var component = Ext.create("Ext.Component",{
                html:html
            });
            fieldCmp.add(image);
            fieldCmp.add(component);
        }else{
            //存在数据，组装审批过程html
            html = CompetitionScope.getApproveHtml(value);
            var component = Ext.create("Ext.Component",{
                style:'overflow:auto;',
                html:html
            });
            fieldCmp.add(component);
        }
        //审批过程弹窗
        var approvalProcessWin = Ext.create("Ext.window.Window",{
            title:tm.contendPos.competitivePosition,
            height:520,
            //autoHeight:true,
            width:460,
            modal:true,
            resizable:false
        });


        approvalProcessWin.add(fieldCmp);
        approvalProcessWin.show();
    },
    getApproveHtml:function (value) {//组装审批过程html
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

            if(z == 0){
                html += tm.contendPos.applicantFills;
            }else{
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
    }
});
