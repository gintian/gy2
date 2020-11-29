/**
 * 竞聘人员列表-面试安排页面
 * wangbs 2019-7-29
 */
Ext.define('Talentmarkets.competition.InterviewArrange',{
    extend:'Ext.Panel',
    posNum: "",//加密的竞聘岗位编号
    layout : {
        type : 'vbox'
    },
    bodyStyle:'z-index:2',
    flex : 1,
    scrollable: 'y',
    initComponent:function(){
        InterviewArrange = this;
        this.callParent();
        this.loadData();
    },
    /**
     * 加载数据
     */
    loadData:function(){
        var me = this;
        //创建遮罩  '数据加载中，请稍候...';
        me.customMyMask = me.createMask(tm.contendPos.loadDataWaiting, competitionJobs);
        var map = new HashMap();
        map.put("operateType", "interviewInit");
        map.put("selectIds", this.posNum);
        Rpc({
            functionId: 'TM000000004', success: function (res) {
                var returnMap = Ext.decode(res.responseText);
                var return_code = returnMap.return_code;
                if (return_code == "success") {
                    var returnData = returnMap.return_data;
                    me.createPageLayout(returnData);
                    if (me.customMyMask) {
                        me.customMyMask.destroy();
                    }
                } else {
                    Ext.Msg.alert(tm.tip, eval(returnMap.return_msg));
                }
            }, scope: this
        }, map);
    },
    /**
     * 创建页面布局
     * @param returnData
     */
    createPageLayout: function (returnData) {
        var me = this;
        //后台传回的候选人信息
        me.orgIdStr = returnData.privOrgIdStr;
        //面试官是否是继承上次标识
        me.extendFlag = returnData.extendFlag;
        var candidatesTableConfig = returnData.candidatesTableInfo.candidatesTableConfig;
        me.candidatesDataList = returnData.candidatesTableInfo.candidatesDataList;
        me.noticeInterviewerArr = returnData.interviewers;
        me.addTools();
        //面试官panel
        me.createUpPanel(returnData.interviewers);
        //候选人panel
        me.createCenterPanel(candidatesTableConfig);
        //面试通知panel
        me.createDownPanel();
    },
    /**
     * 获取错误信息
     * @param errorMsgList
     * @returns {string}
     */
    getSendErrorMsg:function(errorMsgList){
        var sendErrorMsg = "";
        for (var i = 0; i < errorMsgList.length; i++) {
            sendErrorMsg = sendErrorMsg + errorMsgList[i];
        }
        return sendErrorMsg;
    },
    /**
     * 过滤掉信息不全的候选人，信息不全的不予发通知
     * @returns {string}
     */
    handlerCandidatesDataList: function () {
        var me = this;
        var errorMsg = "";
        var mailCheck = me.query("#mailCheck")[0].checked;
        var noteCheck = me.query("#noteCheck")[0].checked;
        var noticeTitle = me.query("#noticeTitle")[0].getValue();

        if (me.candidatesDataList.length > 0) {
            if (!mailCheck && !noteCheck) {//通知方式必须选
                errorMsg = "tm.contendPsn.selectNoticeWay";//'请选择通知方式!';
                return errorMsg;
            }
        }

        if (mailCheck && !Ext.String.trim(noticeTitle)) {//选了邮件 标题必填
            errorMsg = "tm.contendPsn.fillTitle";//'请填写通知标题!';
            return errorMsg;
        }

        for (var i = 0; i < me.candidatesDataList.length; i++) {
            var oneData = me.candidatesDataList[i];
            oneData.z8303 = "04";
        }
        return errorMsg;
    },
    /**
     * 添加工具栏按钮
     */
    addTools: function () {
        var me = this;
        me.tools = [{
            xtype: 'component',
            style:'font-weight:bold;color:#1B4A98;cursor:pointer',
            html: tm.config.msg.save,//保存
            listeners:{
                element:'el',
                click:function(){
                    var candidatesInfoFlag = me.getCandidatesData(true);
                    if (candidatesInfoFlag == "dateMustFill") {
                        //请先填写面试日期！
                        Ext.Msg.alert(tm.tip, tm.contendPsn.dateMustFill);
                        return;
                    }

                    //获取面试官信息
                    var interviewerChangeFlag = me.getInterviewers();
                    if (!interviewerChangeFlag && candidatesInfoFlag == "noSubmit") {
                        //'您未修改数据，无需保存！';
                        Ext.Msg.alert(tm.tip, tm.contendPsn.notChangeNotSave);
                        return;
                    }

                    var interviewPlan = {};
                    interviewPlan.delInterviewers = me.finallyInterviewersGuidkey;//该竞聘岗位的面试官 根据这个数组删除库中的面试官
                    interviewPlan.addInterviewers = me.newInterviewerArr;//新增的面试官
                    interviewPlan.candidatesData = [];//复用后台方法传个空数组：候选人信息

                    var map = new HashMap();
                    map.put("operateType", "saveInterviewersData");
                    map.put("compePosNum", me.posNum);//竞聘岗位编号
                    map.put("interviewPlan", interviewPlan);//面试安排页面信息
                    map.put("extendFlag", me.extendFlag);
                    Rpc({
                        functionId: 'TM000000004', success: function (res) {
                            var returnMap = Ext.decode(res.responseText);
                            var return_code = returnMap.return_code;
                            if (return_code == "success") {
                                //数据成功入库修改前台数据，否则校验失败
                                me.newInterviewerArr = [];
                                me.oldInterviewersGuidkey = me.finallyInterviewersGuidkey.slice(0);
                                if (candidatesInfoFlag == "noSubmit") {
                                    Ext.Msg.alert(tm.tip, tm.contendPsn.dateSaveSuccess);//数据保存成功！
                                    return;
                                }
                                InterviewArrange.saveCandidatesData(true);
                            } else {
                                Ext.Msg.alert(tm.tip, eval(returnMap.return_msg));
                            }
                        }, scope: this
                    }, map);
                }
            }
        },{
            xtype: 'component',
            html: "&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp"
        },{
            xtype: 'component',
            style:'font-weight:bold;color:#1B4A98;cursor:pointer',
            html: tm.contendPsn.submit,//提交
            listeners:{
                element: 'el',
                click: function () {

                    //要通知的候选人是否已勾选，信息是否填写完整，要继续吗？
                    Ext.Msg.confirm(tm.tip, tm.contendPsn.selectAndFillcandidates, function (btn) {
                        if (btn == 'yes') {
                            //至少选择一个面试官
                            if (me.deprecateArr.length == 0) {
                                Ext.Msg.alert(tm.tip, tm.contendPsn.mustSelectInterviewer);
                                return;
                            }

                            var selectRecords = InterviewArrange.tableObj.tablePanel.getSelectionModel().getSelection();
                            if (selectRecords.length == 0) {
                                //请勾选要通知的候选人，并确认其信息是否填写完整！
                                Ext.Msg.alert(tm.tip, tm.contendPsn.mustSelectCandidate);
                                return;
                            }

                            //获取面试官信息
                            var interviewerChangeFlag = me.getInterviewers();

                            //获取候选人信息
                            var candidatesInfoFlag = me.getCandidatesData();
                            if (candidatesInfoFlag == "dateMustFill") {
                                //请先填写面试日期！
                                Ext.Msg.alert(tm.tip, tm.contendPsn.dateMustFill);
                                return;
                            }

                            me.changeCompeStatusArr = me.handlerCandidatesData(selectRecords);

                            var errorMsg = me.handlerCandidatesDataList();
                            if (errorMsg) {
                                Ext.Msg.alert(tm.tip, eval(errorMsg));
                                return;
                            }
                            var mailCheck = me.query("#mailCheck")[0].checked;
                            var noteCheck = me.query("#noteCheck")[0].checked;
                            if (!me.extendFlag && !interviewerChangeFlag && candidatesInfoFlag == "noSubmit" && me.changeCompeStatusArr.length == 0 && !mailCheck && !noteCheck) {
                                //您未修改数据，无需提交
                                Ext.Msg.alert(tm.tip, tm.contendPsn.notChangeNotSubmit);
                                return;
                            }
                            var interviewPlan = {};
                            interviewPlan.delInterviewers = me.finallyInterviewersGuidkey;//该竞聘岗位的面试官 根据这个数组删除库中的面试官
                            interviewPlan.addInterviewers = me.newInterviewerArr;//新增的面试官
                            interviewPlan.candidatesData = me.candidatesData;//候选人信息


                            var noticeTitle = me.query("#noticeTitle")[0].getValue();
                            var noticeContent = me.query("#noticeContent")[0].getValue();

                            var map = new HashMap();
                            map.put("operateType", "interviewSave");
                            map.put("posDesc", me.title);
                            map.put("compePosNum", me.posNum);//竞聘岗位编号
                            map.put("interviewPlan", interviewPlan);//面试安排页面信息
                            map.put("changeCompeStatusArr", me.changeCompeStatusArr);//改变竞聘状态数组
                            map.put("sendCandidatesNoticeList", me.candidatesDataList);//发送通知所需数据
                            map.put("noticeInterviewerArr", me.noticeInterviewerArr);//发送通知的面试官信息
                            map.put("extendFlag", me.extendFlag);
                            map.put("noticeTitle", noticeTitle);
                            map.put("noticeContent", noticeContent);
                            map.put("noticeWay", mailCheck + "`" + noteCheck);

                            var pagingToolBar = InterviewArrange.query("pagingtoolbar")[0];
                            InterviewArrange.pageSize = pagingToolBar.store.pageSize;
                            InterviewArrange.targetPage = InterviewArrange.targetPage ? InterviewArrange.targetPage : 1;

                            map.put("customPageSize", InterviewArrange.pageSize);//一页几条数据
                            map.put("targetPage", InterviewArrange.targetPage);//跳到哪一页

                            Rpc({
                                functionId: 'TM000000004', success: function (res) {
                                    var returnMap = Ext.decode(res.responseText);
                                    var return_code = returnMap.return_code;
                                    if (return_code == "success") {
                                        competitionJobs.remove(me);
                                        me.destroy();
                                        Ext.getCmp("competitionJobsTable_mainPanel").show();
                                    } else {
                                        InterviewArrange.query("#interviewTable")[0].remove(InterviewArrange.tableObj.getMainPanel());
                                        InterviewArrange.candidatesDataList = returnMap.candidatesTableInfo.candidatesDataList;

                                        var tableConfig = Ext.decode(returnMap.candidatesTableInfo.candidatesTableConfig);
                                        InterviewArrange.customAddListener(tableConfig);
                                        InterviewArrange.query("#interviewTable")[0].add(InterviewArrange.tableObj.getMainPanel());

                                        var pagingtoolbar = InterviewArrange.query("pagingtoolbar")[0];
                                        pagingtoolbar.on("beforechange", function (owner, targetPage) {
                                            InterviewArrange.targetPage = targetPage;
                                        }, InterviewArrange);

                                        var checkErrorList = returnMap.checkErrorList;
                                        var errorMsgList = returnMap.errorMsgList;
                                        if (checkErrorList) {//通知配置未配
                                            var sendErrorMsg = "";
                                            for (var i = 0; i < checkErrorList.length; i++) {
                                                sendErrorMsg = sendErrorMsg + eval(checkErrorList[i]);
                                            }
                                            // 都勾选了但是有其中一个未配置'是否继续发送！';
                                            if (mailCheck && noteCheck && checkErrorList.length == 1) {
                                                Ext.Msg.confirm(tm.tip, sendErrorMsg + tm.contendPsn.continueSend, function (btn) {
                                                    if (btn == 'yes') {
                                                        var vo = new HashMap();
                                                        vo.put("operateType", "sendNotice");
                                                        vo.put("posDesc", me.title);
                                                        vo.put("compePosNum", me.posNum);//竞聘岗位编号
                                                        vo.put("changeCompeStatusArr", me.changeCompeStatusArr);//改变竞聘状态数组
                                                        vo.put("sendCandidatesNoticeList", me.candidatesDataList);//发送通知所需数据
                                                        vo.put("noticeInterviewerArr", me.noticeInterviewerArr);//发送通知的面试官信息
                                                        vo.put("noticeTitle", noticeTitle);
                                                        vo.put("noticeContent", noticeContent);
                                                        vo.put("noticeWay", mailCheck + "`" + noteCheck);
                                                        Rpc({
                                                            functionId: 'TM000000004', success: function (response) {
                                                                var returnData = Ext.decode(response.responseText);
                                                                var return_code = returnData.return_code;
                                                                var errorMsgList = returnData.errorMsgList;

                                                                if (return_code == "success") {
                                                                    competitionJobs.remove(me);
                                                                    me.destroy();
                                                                    Ext.getCmp("competitionJobsTable_mainPanel").show();
                                                                } else {
                                                                    var sendErrorMsg = me.getSendErrorMsg(errorMsgList);
                                                                    Ext.Msg.alert(tm.tip, sendErrorMsg);
                                                                }
                                                            }
                                                        }, vo);
                                                    }
                                                });
                                            } else {
                                                Ext.Msg.alert(tm.tip, sendErrorMsg);
                                            }
                                        } else if (errorMsgList) {//发送过程中出错或更新候选人状态出错
                                            var sendErrorMsg = me.getSendErrorMsg(errorMsgList);
                                            Ext.Msg.alert(tm.tip, sendErrorMsg);
                                        } else {
                                            Ext.Msg.alert(tm.tip, eval(returnMap.return_msg));
                                        }
                                    }
                                }, scope: this
                            }, map);
                        }
                    });

                }
            }
        },{
            xtype: 'component',
            html: "&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp"
        },{
            type: 'close',
            handler: function () {
                competitionJobs.remove(me);
                me.destroy();
                Ext.getCmp("competitionJobsTable_mainPanel").show();
            }
        }];
    },

    /**
     * 翻页时保存数据
     * @param clickFlag 点击了保存按钮为true
     */
    saveCandidatesData: function (clickFlag) {
        //获取候选人信息
        var candidatesInfoFlag = InterviewArrange.getCandidatesData();
        if (candidatesInfoFlag == "noSubmit") {
            return;
        }

        var pagingToolBar = InterviewArrange.query("pagingtoolbar")[0];
        InterviewArrange.pageSize = pagingToolBar.store.pageSize;
        InterviewArrange.targetPage = InterviewArrange.targetPage ? InterviewArrange.targetPage : 1;

        var map = new HashMap();
        map.put("operateType", "saveCandidatesData");
        map.put("compePosNum", InterviewArrange.posNum);//竞聘岗位编号
        map.put("customPageSize", InterviewArrange.pageSize);//一页几条数据
        map.put("targetPage", InterviewArrange.targetPage);//跳到哪一页
        map.put("candidatesData", InterviewArrange.candidatesData);//候选人信息
        Rpc({
            functionId: 'TM000000004', success: function (res) {
                var returnMap = Ext.decode(res.responseText);
                var return_code = returnMap.return_code;
                if (return_code == "success") {
                    InterviewArrange.query("#interviewTable")[0].remove(InterviewArrange.tableObj.getMainPanel());
                    InterviewArrange.candidatesDataList = returnMap.candidatesTableInfo.candidatesDataList;

                    var tableConfig = Ext.decode(returnMap.candidatesTableInfo.candidatesTableConfig);
                    InterviewArrange.customAddListener(tableConfig);
                    InterviewArrange.query("#interviewTable")[0].add(InterviewArrange.tableObj.getMainPanel());

                    var pagingtoolbar = InterviewArrange.query("pagingtoolbar")[0];
                    pagingtoolbar.on("beforechange", function (owner, targetPage) {
                        InterviewArrange.targetPage = targetPage;
                    }, InterviewArrange);

                    if (clickFlag) {//点击的右上角的保存按钮才有提示
                        Ext.Msg.alert(tm.tip, tm.contendPsn.dateSaveSuccess);//数据保存成功！
                    }
                } else {
                    Ext.Msg.alert(tm.tip, eval(returnMap.return_msg));
                }
            }, scope: InterviewArrange
        }, map);
    },
    /**
     * 获取报名通过的候选人信息
     * @returns {string}
     */
    getCandidatesData: function (noChangeOldTimeFlag) {
        var me = this;
        var noticeContent = me.query("#noticeContent")[0].getValue();
        var mailCheck = me.query("#mailCheck")[0].checked;
        var noteCheck = me.query("#noteCheck")[0].checked;

        me.candidatesData = [];
        var candidatesInfoFlag = 'noSubmit';//默认候选人信息不需要提交（未修改）
        var modifiedRecords = InterviewArrange.tableObj.dataStore.getModifiedRecords();

        var candidatesGrid = Ext.getCmp('interviewPlan_tablePanel');
        var candidatesData = candidatesGrid.getStore().data.items;
        for (var i = 0; i < candidatesData.length; i++) {
            var candidateData = candidatesData[i].data;
            var internalId = candidatesData[i].internalId;
            var modifyFlag = false;//默认未修改
            for (var j = 0; j < modifiedRecords.length; j++) {
                var modifyInternalId = modifiedRecords[j].internalId;
                if (modifyInternalId == internalId) {
                    var modifiedInfo = modifiedRecords[j].modified;
                    //有对象说明修改了
                    for (var field in modifiedInfo) {
                        modifyFlag = true;
                        break;
                    }
                    break;
                }
            }
            var status = candidateData.z8303.split("`")[0];
            if (status == "02") {//状态为报名通过的
                var interviewDate = candidateData.interviewdate;
                var interviewAddress = candidateData.z0503;
                var z8301 = candidateData.z8301;
                var oldInterviewTime = candidateData.interviewtime;//修改前的面试时间

                var beginHour = document.getElementById("bh-" + z8301).value;
                var beginMinute = document.getElementById("bm-" + z8301).value;
                var endHour = document.getElementById("eh-" + z8301).value;
                var endMinute = document.getElementById("em-" + z8301).value;

                var timeChange = false;//默认面试时间未修改
                var interviewTime = beginHour + ":" + beginMinute + "-" + endHour + ":" + endMinute;

                if (oldInterviewTime != interviewTime) {
                    timeChange = true;
                }

                // || status == "02" && submitFlag
                if (timeChange || modifyFlag) {
                    //校验面试日期   /*||地点必填*/
                    if (!interviewDate /*|| !interviewAddress*/) {
                        candidatesInfoFlag = "dateMustFill";
                        return candidatesInfoFlag;
                    }
                    if (timeChange && !noChangeOldTimeFlag) {
                        candidateData.interviewtime = interviewTime;
                    }
                    var candidateInfoList = [];
                    var startTime = interviewDate ? interviewDate + " " + beginHour + ":" + beginMinute : "";
                    var endTime = interviewDate ? interviewDate + " " + endHour + ":" + endMinute : "";

                    candidateInfoList.push(candidateData.changeflag);
                    candidateInfoList.push(startTime);
                    candidateInfoList.push(endTime);
                    candidateInfoList.push(interviewAddress);
                    candidateInfoList.push(mailCheck || noteCheck ? "1" : "0");
                    candidateInfoList.push(noticeContent);
                    candidateInfoList.push(mailCheck || noteCheck ? "1" : "0");
                    candidateInfoList.push(candidateData.z8301);

                    me.candidatesData.push(candidateInfoList);
                }
            }else {
                //数据都排过序，到此处说明报名通过的信息都已经录入，跳出循环
                break;
            }
        }
        if (me.candidatesData.length > 0) {
            candidatesInfoFlag = "submit";
        }
        return candidatesInfoFlag;
    },
    /**
     * 处理提交页信息，得到需更新状态的人员
     * @param selectRecords 勾选的记录
     * @returns {Array}
     */
    handlerCandidatesData: function (selectRecords) {
        var me = this;
        //需要改变状态的人员数组
        var changeCompeStatusArr = [];
        //当前页修改的数据
        for (var i = 0; i < me.candidatesData.length; i++) {
            var oneCandidateInfoList = me.candidatesData[i];
            var z8301 = oneCandidateInfoList[7];//guidkey
            var interviewDate = oneCandidateInfoList[1].substring(0, 10);
            var interviewTime = oneCandidateInfoList[1].substring(11) + "-" + oneCandidateInfoList[2].substring(11);

            //更新后台返回的所有数据
            for (var j = 0; j < me.candidatesDataList.length; j++) {
                var tempCandidateData = me.candidatesDataList[j];
                var tempZ8301 = tempCandidateData.z8301;
                if (z8301 == tempZ8301) {
                    tempCandidateData.interviewdate = interviewDate;
                    tempCandidateData.interviewtime = interviewTime;
                    tempCandidateData.z0503 = oneCandidateInfoList[3];
                    break;
                }
            }
        }

        //把信息填写完毕的人员放到数组中，把数据库中的状态改掉
        for (var i = 0; i < me.candidatesDataList.length; i++) {
            var oneData = me.candidatesDataList[i];
            var interviewdate = oneData.interviewdate;
            var interviewtime = oneData.interviewtime;
            var z0503 = oneData.z0503;
            var z8303 = oneData.z8303;
            var changeZ8301 = oneData.z8301;

            var needSendMsgFlag = false;//默认当前候选人不需要发通知
            for (var j = 0; j < selectRecords.length; j++) {
                var oneSelectData = selectRecords[j].data;
                var tempZ8301 = oneSelectData.z8301;

                //勾选的数据才发通知并更新状态
                if (changeZ8301 == tempZ8301) {
                    needSendMsgFlag = true;
                    break;
                }
            }
            //勾选了&&报名通过状态下&&信息填写完毕的才更新状态，否则移除数据
            if (needSendMsgFlag && z0503 && interviewtime && interviewdate && z8303 == "02") {
                var tempArr = [changeZ8301];
                changeCompeStatusArr.push(tempArr);
            } else {
                Ext.Array.remove(me.candidatesDataList, oneData);
                i--;
            }
        }
        return changeCompeStatusArr;
    },
    /**
     * 获取面试官信息
     */
    getInterviewers: function () {
        var me = this;
        var interviewerChangeFlag = true;//默认修改了面试官
        var finallyInterviewersGuidkey = me.finallyInterviewersGuidkey.sort().toString();
        var oldInterviewersGuidkey = me.oldInterviewersGuidkey.sort().toString();
        //最终的面试官跟修改之前的面试官一致，即未修改面试官，则置为false
        if (oldInterviewersGuidkey == finallyInterviewersGuidkey) {
            interviewerChangeFlag = false;
            return interviewerChangeFlag;
        } else {
            for (var i = 0; i < me.newInterviewerArr.length; i++) {
                if (oldInterviewersGuidkey.indexOf(me.newInterviewerArr[i]) > -1) {
                    Ext.Array.remove(me.newInterviewerArr, me.newInterviewerArr[i]);
                    i--;
                }
            }
        }
        return interviewerChangeFlag;
    },
    /**
     * 初始化之前存在的面试官
     * @param interviewers 面试官信息
     * @returns {Array}
     */
    initOldInterviewerArr: function (interviewers) {
        var me = this;
        //原有的面试官的nbaseA0100(不在选人组件里显示的人员)
        me.deprecateArr = [];
        //原有的面试官guidkey
        me.oldInterviewersGuidkey = [];
        //通过组件选择的面试官guidkey
        me.newInterviewerArr = [];

        for (var i = 0; i < interviewers.length; i++) {
            me.deprecateArr.push(interviewers[i].nbaseA0100);
            me.oldInterviewersGuidkey.push(interviewers[i].interviewerGuidkey);
        }
        me.finallyInterviewersGuidkey = me.oldInterviewersGuidkey.slice(0);
    },
    /**
     * 创建遮罩
     * @param msg
     * @param target
     * @returns {*|void}
     */
    createMask: function (msg,target) {
        var myMask = new Ext.LoadMask({
            msg: msg,
            target: target
        }).show();
        return myMask;
    },
    /**
     * 创建存放面试官的顶部panel
     * @param interviewers 面试官信息
     */
    createUpPanel: function (interviewers) {
        var me = this;
        //获取之前存在的面试官
        me.initOldInterviewerArr(interviewers);

        var upPanel = Ext.create("Ext.Panel", {
            margin: '10 55 0 55',
            width: '100%',
            border: false,
            items: [me.createTitlePanel(tm.contendPsn.interviewer, "top")//面试官
                , {
                    xtype: 'panel',
                    border: false,
                    itemId: 'upContentPanel'
                }
            ]
        });
        me.add(upPanel);

        var upContentPanel = upPanel.query("#upContentPanel")[0];
        for (var i = 0; i < interviewers.length; i++) {
            var interviewer = interviewers[i];
            var interviewerPhotoPath = interviewer.interviewerPhotoPath;//头像路径
            var interviewerName = interviewer.interviewerName;//面试官姓名
            var nbaseA0100 = interviewer.nbaseA0100;
            var guidkey = interviewer.interviewerGuidkey;

            var interviewerPanel = me.getInterviewerPanel(i, interviewerPhotoPath, interviewerName, nbaseA0100, guidkey);
            upContentPanel.add(interviewerPanel);
        }

        //添加面试官按钮
        var addPersonIcon = Ext.create("Ext.Img", {
            height: 48,
            width: 48,
            margin: interviewers.length > 0 ? '10 0 0 0' : '0 0 0 0',
            itemId: 'addPersonIcon',
            style: 'float:left;cursor:pointer',
            src: '../images/addPerson.png',
            listeners: {
                element: 'el',
                click: function () {
                    if (me.orgIdStr == "no") {
                        Ext.Msg.alert(tm.tip, tm.contendPsn.noPsnSelect);//'您权限范围内没有可选择的人员！';
                    }else {
                        me.createPersonPicker(interviewers);
                    }
                }
            }
        });
        upContentPanel.add(addPersonIcon);

        //me.add(upPanel)之后再创建tooltip 否则target找不到
        for (var i = 0; i < interviewers.length; i++) {
            var interviewer = interviewers[i];
            var interviewerPhone = interviewer.interviewerPhone;//面试官电话
            var interviewerEmail = interviewer.interviewerEmail;//面试官邮箱
            me.createToolTip(i, interviewerPhone, interviewerEmail);
        }
    },

    /**
     * 创建候选人panel
     * @param candidatesTableConfig 候选人tableconfig
     */
    createCenterPanel: function (candidatesTableConfig) {
        var me = this;
        var tableConfig = Ext.decode(candidatesTableConfig);
        me.customAddListener(tableConfig);
        var centerPanel = Ext.create("Ext.Panel", {
            margin: '10 55 0 55',
            width: '100%',
            border: false,
            items: [me.createTitlePanel(tm.contendPsn.candidates, "center")//候选人
                , {//存放面试人信息的tablebuilder
                    xtype: 'panel',
                    margin: '10 0 0 0',
                    border: false,
                    itemId: 'interviewTable',
                    height: 500,
                    layout: 'fit'
                }
            ]
        });
        centerPanel.query("#interviewTable")[0].add(InterviewArrange.tableObj.getMainPanel());
        me.add(centerPanel);

        var pagingtoolbar = me.query("pagingtoolbar")[0];
        pagingtoolbar.on("beforechange", function(owner,targetPage){
            me.targetPage = targetPage;
        }, me);
    },
    /**
     * 添加监听
     * @param tableConfig
     */
    customAddListener: function (tableConfig) {
        var me = this;
        tableConfig.onChangePage = function (){
            //翻页保存候选人信息
            InterviewArrange.saveCandidatesData();
        };
        InterviewArrange.tableObj = new BuildTableObj(tableConfig);
        me.addEvent();
    },
    /**
     * tablebuilder添加监听事件
     */
    addEvent: function () {
        InterviewArrange.tableObj.tablePanel.on('beforeedit', function (e, c) {
            var currentData = c.record.data;
            var status = currentData.z8303.split("`")[0];//竞聘状态
            if (status == "02") {//报名通过的状态 才可以编辑
                return true;
            } else {
                return false;
            }
        });
        //修改列编辑器
        InterviewArrange.tableObj.tablePanel.on('render', function (t) {
                var column = t.getColumnManager().getHeaderByDataIndex("interviewdate");
                var eidtior = {
                    xtype: "datetimefield", minValue: new Date(), editable: false, format: 'Y-m-d'
                };
                column.setEditor(eidtior);
            }
        );
        InterviewArrange.tableObj.dataStore.on('endupdate', function (t) {
            var hourData = [["00", "00"], ["01", "01"], ["02", "02"], ["03", "03"], ["04", "04"],
                ["05", "05"], ["06", "06"], ["07", "07"], ["08", "08"], ["09", "09"],
                ["10", "10"], ["11", "11"], ["12", "12"], ["13", "13"], ["14", "14"],
                ["15", "15"], ["16", "16"], ["17", "17"], ["18", "18"], ["19", "19"],
                ["20", "20"], ["21", "21"], ["22", "22"], ["23", "23"]];
            var minuteData = [["00", "00"], ["05", "05"], ["10", "10"], ["15", "15"], ["20", "20"], ["25", "25"],
                ["30", "30"], ["35", "35"], ["40", "40"], ["45", "45"], ["50", "50"], ["55", "55"]];
            var hourStore = new Ext.data.ArrayStore({
                fields: ['id', 'displayText'],
                data: hourData
            });
            var minuteStore = new Ext.data.ArrayStore({
                fields: ['id', 'displayText'],
                data: minuteData
            });
            var currentPageData = InterviewArrange.tableObj.tablePanel.getStore().data.items;
            for (var i = 0; i < currentPageData.length; i++) {
                var oneData = currentPageData[i].data;
                var z8301 = oneData.z8301;

                //不在currentPageData中取数而是从实时更新的input隐藏域中取值
                var beginHour = document.getElementById("bh-" + z8301).value;
                var beginMinute = document.getElementById("bm-" + z8301).value;
                var endHour = document.getElementById("eh-" + z8301).value;
                var endMinute = document.getElementById("em-" + z8301).value;

                //多次刷新后导致interviewTimeDiv中有多个相同的下面创建的panel，造成边线重合，此处先清掉，再重新创建panel
                var interviewTimeDiv = document.getElementById(z8301);
                if(interviewTimeDiv.innerHTML){
                    interviewTimeDiv.innerHTML='';
                }
                Ext.widget("panel",{
                    border: false,
                    width: 250,
                    layout: {
                        type: 'hbox',
                        align: 'center'
                    },
                    renderTo: z8301,
                    items: [
                        InterviewArrange.createCombo('bh-' + z8301, hourStore, beginHour),
                        InterviewArrange.createComponent(':'),
                        InterviewArrange.createCombo('bm-' + z8301, minuteStore, beginMinute),
                        InterviewArrange.createComponent('-'),
                        InterviewArrange.createCombo('eh-' + z8301, hourStore, endHour),
                        InterviewArrange.createComponent(':'),
                        InterviewArrange.createCombo('em-' + z8301, minuteStore, endMinute)
                    ]
                });
            }
        });
    },
    /**
     * 创建选择面试时间combo
     * @param inputId input隐藏域id
     * @param store 数据源
     * @param value 值
     * @returns {*}
     */
    createCombo: function (inputId, store, value) {
        var combo = Ext.widget('combo', {
            width: 50,
            margin: '0 5 0 5',
            store: store,
            editable: false,
            value: value,
            valueField: 'id',
            displayField: 'displayText',
            listeners: {
                select: function (owner, selectData) {
                    document.getElementById(inputId).value = selectData.id;
                }
            }
        });
        return combo;
    },
    createComponent: function (value) {
        var label = Ext.widget('component', {
            html: value
        });
        return label;
    },
    /**
     * 面试通知panel
     */
    createDownPanel: function () {
        var me = this;
        var downPanel = Ext.create("Ext.Panel", {
            margin: '10 55 0 55',
            width: '100%',
            border: false,
            items: [me.createTitlePanel(tm.contendPsn.interviewMsg, "bottom")//面试通知
                , {
                    xtype: 'panel',
                    itemId: 'noticePanel',
                    border: false,
                    margin: '10 0 0 30',
                    items: [{
                        xtype: 'textfield',
                        itemId: 'noticeTitle',
                        width: 500,
                        fieldLabel: '<span style="font-size: 14px">' + tm.contendPsn.msgTitle + '</span>',//标题
                        labelWidth: 50,
                        labelAlign: 'left',
                        listeners: {
                            focusleave: function () {
                                var charnum = 0;//字节数
                                var varlength = 0;//字符长度
                                for (var i = 0; i < this.value.length; i++) {
                                    var a = this.value.charAt(i);
                                    if (a.match(/[^\x00-\xff]/ig) != null) {//如果是汉字
                                        charnum = charnum + 2;//一个汉字占两个字节
                                        varlength = varlength + 1;
                                    } else {
                                        charnum = charnum + 1;//字母数字等占一个字节
                                        varlength = varlength + 1;
                                    }
                                    if (charnum == 51 || charnum == 52) {
                                        Ext.MessageBox.alert(tm.tip, tm.contendPsn.overlength);//提示信息   请输入50个以内的字节（一个汉字占两个字节）
                                        this.setValue(this.value.substring(0, varlength - 1));
                                    }
                                }
                            }
                        }
                    }, {
                        xtype: 'textareafield',
                        itemId: 'noticeContent',
                        width: 500,
                        height: 150,
                        labelWidth: 50,
                        labelAlign: 'left',
                        margin: '10 auto',
                        fieldLabel: '<span style="font-size: 14px">' + tm.contendPsn.msgContent + '</span>'//内容
                    }, {
                        xtype: 'panel',
                        border: false,
                        margin: '10 0 50 55',
                        layout: 'hbox',
                        items: [{
                            xtype: 'checkbox',
                            itemId: 'mailCheck',
                            boxLabel: tm.contendPsn.mail,//邮件
                            checked: false
                        }, {
                            xtype: 'checkbox',
                            itemId: 'noteCheck',
                            margin: '0 0 0 10',
                            boxLabel: tm.contendPsn.note,//短信
                            checked: false
                        }]
                    }]
                }
            ]
        });
        me.add(downPanel);
    },
    /**
     * 创建titlepanel
     * @param title 标题
     * @param position 哪一块panel
     * @returns {Ext.Panel}
     */
    createTitlePanel: function (title, position) {
        var me = this;
        var titlePanel = Ext.create("Ext.Panel", {
            height: 35,
            border: false,
            layout: {
                type: 'hbox',
                align: 'center'
            },
            bodyStyle: 'backgroundColor:#F0F0F0',
            items: [{
                xtype: 'component',
                margin: '0 0 0 5',
                html: '<span style="font-size: 14px">' + title + '</span>'
            }, {
                xtype: 'component',
                flex: 1
            }, {
                xtype: 'image',
                itemId: 'downIcon',
                height: 32,
                width: 32,
                style: 'margin-right:10px;cursor:pointer',
                src: '../images/down.png',
                listeners: {
                    element: 'el',
                    click: function () {
                        var upContentPanel = me.query("#upContentPanel")[0];
                        var interviewTable = me.query("#interviewTable")[0];
                        var noticePanel = me.query("#noticePanel")[0];

                        titlePanel.query("#downIcon")[0].hide();
                        titlePanel.query("#upIcon")[0].show();
                        if (position == "top") {
                            upContentPanel.hide();
                        }else if (position == "center") {
                            interviewTable.hide();
                        }else if (position == "bottom") {
                            noticePanel.hide();
                        }
                    }
                }
            }, {
                xtype: 'image',
                itemId: 'upIcon',
                hidden: true,
                height: 32,
                width: 32,
                style: 'margin-right:10px;cursor:pointer',
                src: '../images/up.png',
                listeners: {
                    element: 'el',
                    click: function () {
                        var upContentPanel = me.query("#upContentPanel")[0];
                        var interviewTable = me.query("#interviewTable")[0];
                        var noticePanel = me.query("#noticePanel")[0];

                        titlePanel.query("#downIcon")[0].show();
                        titlePanel.query("#upIcon")[0].hide();
                        if (position == "top") {
                            upContentPanel.show();
                        }else if (position == "center") {
                            interviewTable.show();
                        }else if (position == "bottom") {
                            noticePanel.show();
                        }
                    }
                }

            }]
        });
        return titlePanel;
    },
    /**
     * 创建tooltip组件
     * @param suffix target后缀
     * @param interviewerPhone 电话
     * @param interviewerEmail 邮箱
     */
    createToolTip: function (suffix, interviewerPhone, interviewerEmail) {
        Ext.create("Ext.tip.ToolTip", {
            target: 'psn_' + suffix,
            minWidth: 200,
            trackMouse: true,//跟随鼠标移动
            html: tm.contendPsn.phone + interviewerPhone + '<br>' + tm.contendPsn.email + interviewerEmail
        });
    },
    /**
     * 获得一个面试官panel
     * @param suffix id后缀
     * @param interviewerPhotoPath 头像路径
     * @param interviewerName 面试官姓名
     * @param nbaseA0100
     * @param guidkey
     */
    getInterviewerPanel: function (suffix, interviewerPhotoPath, interviewerName, nbaseA0100, guidkey) {
        var me = this;
        var version = getBrowseVersion();
        var delIconMargin = "-69px 0 0 36px";
        if (version < 10 && version != 0) {
            delIconMargin = "-75px 0 0 40px";
        }
        var upContentPanel = me.query("#upContentPanel")[0];
        var interviewerPanel = Ext.create("Ext.Panel", {
            id: 'psn_' + suffix,//tooltip的目标
            border: false,
            style: 'float:left',
            width: 60,
            items: [{
                xtype: 'image',
                width: 48,
                height: 48,
                margin: '8 0 0 0',
                style: 'border-radius:100%',
                src: interviewerPhotoPath
            }, {
                xtype: 'panel',
                width: 48,
                border: false,
                layout: {
                    type: 'vbox',
                    align: 'center'
                },
                items: [{
                    xtype: 'component',
                    maxWidth: 54,
                    style: 'overflow:hidden;text-overflow:ellipsis;white-space:nowrap;',
                    html: '<span style="font-size: 14px">' + interviewerName + '</span>'
                }]
            }, {
                xtype: 'image',
                itemId: 'delIcon',
                psnFlag: nbaseA0100,//删除面试官时用
                psnGuidFlag: guidkey,//删除面试官时用
                src: '/components/homewidget/images/del.png',
                style: 'visibility:hidden;position:absolute;cursor:pointer;margin:' + delIconMargin,
                listeners: {
                    element: 'el',
                    click: function () {
                        var delIcon = interviewerPanel.query("#delIcon")[0];
                        upContentPanel.remove(interviewerPanel);
                        Ext.Array.remove(me.deprecateArr, delIcon.psnFlag);
                        Ext.Array.remove(me.newInterviewerArr, delIcon.psnGuidFlag);
                        Ext.Array.remove(me.finallyInterviewersGuidkey, delIcon.psnGuidFlag);

                        if (me.deprecateArr.length == 0) {
                            me.query("#addPersonIcon")[0].setMargin("0 0 0 0");
                        }
                        for (var j = 0; j < me.noticeInterviewerArr.length; j++) {
                            var tempData = me.noticeInterviewerArr[j];
                            var tempGuidkey = tempData.interviewerGuidkey;
                            if (delIcon.psnGuidFlag == tempGuidkey) {
                                Ext.Array.remove(me.noticeInterviewerArr, me.noticeInterviewerArr[j]);
                                break;
                            }
                        }
                    }
                }
            }],
            listeners: {
                element: 'el',
                mouseover: function () {
                    var delIcon = interviewerPanel.query("#delIcon")[0];
                    delIcon.setStyle({
                        visibility: 'visible'
                    });
                },
                mouseout: function () {
                    var delIcon = interviewerPanel.query("#delIcon")[0];
                    delIcon.setStyle({
                        visibility: 'hidden'
                    });
                }
            }
        });
        return interviewerPanel;
    },
    /**
     * 获取选人组件不应显示的人员包含候选人(面试者不能做面试官)
     */
    getDeprecateList: function () {
        var me = this;
        var deprecateList = me.deprecateArr.slice(0, me.deprecateArr.length);
        for (var i = 0; i < me.candidatesDataList.length; i++) {
            var oneCandidateInfo = me.candidatesDataList[i];
            deprecateList.push(oneCandidateInfo.nbaseA0100);
        }
        return deprecateList;
    },
    /**
     * 创建选人控件
     */
    createPersonPicker: function () {
        var me = this;
        //获取选人组件不应显示的人员
        var deprecateList = me.getDeprecateList();

        var upContentPanel = me.query("#upContentPanel")[0];
        var picker = new PersonPicker({
            multiple: true,
            orgid: me.orgIdStr,//组织机构业务范围
            isPrivExpression: false,//不控制人员范围
            deprecate: deprecateList,
            callback: function (people) {
                if (people.length > 0) {
                    //创建遮罩  '数据加载中，请稍候...';
                    me.customMyMask = me.createMask(tm.contendPos.loadDataWaiting, competitionJobs);
                    setTimeout(function () {
                        for (var i = 0; i < people.length; i++) {
                            var person = people[i];
                            var nbaseA0100 = person.id;
                            var guidkey = person.guidkey;
                            var name = person.name;//姓名
                            var photoPath = person.photo;//照片路径
                            var email = person.email ? person.email : "";//邮箱
                            var phoneNumber = person.c0104 ? person.c0104 : "";//电话

                            var suffix = 0;//id后缀
                            if (upContentPanel.items.length > 1) {//存在面试官，取到最后一个面试官的组件id后缀+1
                                var lastPsnId = upContentPanel.items.items[upContentPanel.items.length - 2].id;
                                suffix = parseInt(lastPsnId.split("_")[1]) + 1;
                            }

                            var haveFlag = false;//默认没有面试官
                            for (var j = 0; j < me.noticeInterviewerArr.length; j++) {
                                var tempData = me.noticeInterviewerArr[j];
                                var tempGuidkey = tempData.interviewerGuidkey;
                                if (guidkey == tempGuidkey) {
                                    haveFlag = true;
                                    break;
                                }
                            }

                            if (!haveFlag) {
                                me.noticeInterviewerArr.push({
                                    interviewerName: name,
                                    interviewerGuidkey: guidkey,
                                    interviewerEmail: email,
                                    interviewerPhone: phoneNumber,
                                    nbaseA0100: nbaseA0100
                                });
                            }

                            me.deprecateArr.push(nbaseA0100);//已选择的不再显示
                            me.finallyInterviewersGuidkey.push(guidkey);//最后还剩哪些面试官
                            //不添加重复的
                            var newAddFlag = true;
                            for (var j = 0; j < me.newInterviewerArr.length; j++) {
                                var oneGuid = me.newInterviewerArr[j];
                                if (oneGuid == guidkey) {
                                    newAddFlag = false;
                                    break;
                                }
                            }
                            if (newAddFlag) {
                                me.newInterviewerArr.push(guidkey);//通过组件选择的面试官
                            }
                            var interviewerPanel = me.getInterviewerPanel(suffix, photoPath, name, nbaseA0100, guidkey);
                            var insertIndex = upContentPanel.items.length - 1;
                            upContentPanel.insert(insertIndex, interviewerPanel);

                            me.createToolTip(suffix, phoneNumber, email);
                        }
                        if (me.customMyMask) {
                            me.customMyMask.destroy();
                        }
                        me.query("#addPersonIcon")[0].setMargin("10 0 0 0");
                    }, 1);
                }
            }
        }, Ext.getBody());
        picker.open();
    },

    /**
     * 自定义渲染面试时间编辑器
     * @param value 值
     * @param metaData
     * @param record 一行记录
     * @returns {*}
     */
    interviewTimeRenderFunc: function (value, metaData, record, rowIndex) {
        var z8303 = record.data.z8303;
        var z8301 = record.data.z8301;
        var interviewtime = record.data.interviewtime;
        var bh = interviewtime.split("-")[0].split(":")[0];
        var bm = interviewtime.split("-")[0].split(":")[1];
        var eh = interviewtime.split("-")[1].split(":")[0];
        var em = interviewtime.split("-")[1].split(":")[1];

        //渲染前把修改过的值拿过来防止覆盖
        if (document.getElementById('bh-' + z8301)) {
            bh= document.getElementById("bh-" + z8301).value;
            bm= document.getElementById("bm-" + z8301).value;
            eh= document.getElementById("eh-" + z8301).value;
            em= document.getElementById("em-" + z8301).value;
        }

        var status = z8303.split("`")[0];//竞聘状态
        if (status == "02") {//只有报名通过状态的数据，才能修改
            //创建时间隐藏域便于取值防止combo值被源数据覆盖
            return "<div style='width: 255px;height:22px;' id='" + z8301 + "'></div>" +
                "<input id='bh-" + z8301 + "' type='hidden' value='" + bh + "'>" +
                "<input id='bm-" + z8301 + "' type='hidden' value='" + bm + "'>" +
                "<input id='eh-" + z8301 + "' type='hidden' value='" + eh + "'>" +
                "<input id='em-" + z8301 + "' type='hidden' value='" + em + "'>";
        }
        return value;
    },

    /**
     * 拼接selecthtml
     * @param dataArr 数据源
     * @param value 数据库原数据
     * @param id select的id
     */
    customSelectTimeHtml: function (dataArr, value, id) {
        var customHtml = '<select id="' + id + '" onclick="InterviewArrange.selectClick(event,this)">';
        for (var i = 0; i < dataArr.length; i++) {
            var currentHour = dataArr[i];
            if (currentHour == value) {
                customHtml += '<option selected="selected" value="' + currentHour + '">' + currentHour + '</option>';
            }else {
                customHtml += '<option value="' + currentHour + '">' + currentHour + '</option>';
            }
        }
        return customHtml+'</select>';
    },
    /**
     * 解决ie浏览器有时选不了值的问题
     * @param e 事件对象
     */
    selectClick:function (e) {
        window.focus();
        window.event? window.event.cancelBubble = true : e.stopPropagation();
    }
});