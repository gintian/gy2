Ext.define('Talentmarkets.talenthall.TalentHallMain', {
    requires: ['Talentmarkets.talenthall.TalentHallBrowse'],
    extend: 'Ext.panel.Panel',
    layout: 'fit',
    bodyPadding: '0 0 0 5',
    initParam: {},
    initComponent: function () {
        TalentHallMain = this;
        this.callParent();
        this.init();
    },
    init: function () {
        this.initData();
        this.add(this.createMainPanel());
    },
    initData: function () {
        var map = new HashMap();
        map.put("operateType", "initParam");
        Rpc({
            functionId: 'TM000000201', success: function (res) {
                var resData = Ext.decode(res.responseText);
                var return_code = resData.return_code;
                var return_msg = resData.return_msg_code;
                if (return_code == "fail") {
                    // Ext.Msg.alert(tm.contendPos.msg.title, tm.talentHall.Msg.changeAprovalStatusError);
                } else {
                    TalentHallMain.initParam = resData.return_data.initParam;
                }
            }, scope: this, async: false
        }, map);
    },
    createMainPanel: function () {
        var mainPanel = Ext.create('Ext.panel.Panel', {
            layout: 'vbox',
            border: false,
            itemId: 'mainPanel'
        });
        //创建顶部工具栏按钮
        mainPanel.add(this.createToolBar());
        mainPanel.add(this.createDataView());
        return mainPanel;
    },
    createToolBar: function () {
        var jobCategoryCombobox = Ext.create('EHR.extWidget.field.CodeTreeCombox', {
            codesetid: TalentHallMain.initParam.postTypeFieldCodesetId,
            ctrltype: '0',
            name: 'jobCategory',
            hidden: TalentHallMain.initParam.isHaveResumePostTypeField ? false : true,
            margin: '0 0 0 20',
            enableKeyEvents: true,
            emptyText: tm.talentHall.Msg.peleaseChooseJobCategory,
            afterCodeSelectFn: function (dataIndex, orgId) {
                TalentHallMain.reloadStore();
            },
            listeners: {
                keyup: {
                    fn: function (t) {
                        if (!t.getRawValue()) {
                            TalentHallMain.reloadStore();
                        }
                    }
                }
            }
        });
        var queryFormPanel = Ext.create('Ext.form.Panel', {
            layout: 'hbox',
            itemId: 'queryForm',
            margin: '0 0 1 0',
            border: false,
            items: [
                {
                    xtype: 'codecomboxfield',
                    name: 'org',
                    width: 150,
                    margin: '0 0 0 30',
                    codesetid: 'UN',
                    // nmodule: "4",//组织机构业务范围
                    ctrltype: "0",
                    onlySelectCodeset: true,
                    enableKeyEvents: true,
                    emptyText: tm.selectOrgplease,//请选择组组织机构
                    afterCodeSelectFn: function (dataIndex, orgId) {
                        TalentHallMain.reloadStore();
                    },
                    listeners: {
                        keyup: {
                            fn: function (t) {
                                if (!t.getRawValue()) {
                                    TalentHallMain.reloadStore();
                                }
                            }
                        }
                    }
                },
                jobCategoryCombobox,
                {
                    xtype: 'textfield',
                    margin: '0 0 0 30',
                    name: 'name',
                    emptyText: tm.talentHall.Msg.peleaseInputName,
                    listeners: {
                        specialkey: function (field, e) {
                            if (e.getKey() == e.ENTER) {
                                TalentHallMain.reloadStore();
                            }
                        }
                    }
                }
            ]
        });
        var toolbarContainer = Ext.create('Ext.container.Container', {
            layout: {
                type: 'hbox',
                align: 'center'
            },
            width: '100%',
            margin: '14 0 3 5',
            items: [
                /*{
                    xtype: 'component',
                    html: tm.talentHall.title
                },*/
                queryFormPanel,
                TalentHallMain.createOrderByContainer(),
                {
                    xtype: 'component',
                    flex: 1
                },
                {
                    xtype: 'component',
                    margin: '0 30 0 0',
                    hidden: TalentHallMain.initParam.browseFlag == 1 ? false : true,
                    html: '<span style="color:#1B4A98;cursor: pointer;font-size: 13px" onclick="javascript:void(0);TalentHallMain.showViewSituation()">' + tm.talentHall.Msg.viewSituation + '</span>'
                },
                {
                    xtype: 'component',
                    hidden: TalentHallMain.initParam.applyFlag == 1 && TalentHallMain.initParam.applyType == 'release' ? false : true,
                    margin: '0 30 0 0',
                    html: '<span style="color:#1B4A98;cursor: pointer;font-size: 13px" onclick="javascript:void(0);TalentHallMain.processProcessing()">' + tm.talentHall.Msg.publishResume + '</span>'
                },
                {
                    xtype: 'component',
                    hidden: TalentHallMain.initParam.applyFlag == 1 && TalentHallMain.initParam.applyType == 'cancel' ? false : true,
                    margin: '0 30 0 0',
                    html: '<span style="color:#1B4A98;cursor: pointer;font-size: 13px" onclick="javascript:void(0);TalentHallMain.processProcessing()">' + tm.talentHall.Msg.cancelResume + '</span>'
                }


            ]
        });
        return toolbarContainer;
    },
    /**
     * 通用走流程
     */
    processProcessing: function () {
        if (!TalentHallMain.initParam.tabid) {
            Ext.Msg.alert(tm.contendPos.msg.title, tm.contendPos.pelaseSetingTab);
            return;
        }
        var templateObj = new Object();
        templateObj.tab_id = TalentHallMain.initParam.tabid;
        templateObj.return_flag = "0";
        templateObj.view_type = "card";
        templateObj.module_id = "9";
        templateObj.approve_flag = "1";
        templateObj.callBack_init = "TalentHallMain.tempFunc";
        templateObj.callBack_close = "TalentHallMain.goBack";
        createTemplateForm(templateObj);

    },
    tempFunc: function () {
        TalentHallMain.query("#mainPanel")[0].hide();
        TalentHallMain.add(templateMain_me.mainPanel);
    },
    goBack: function () {
        TalentHallMain.remove(templateMain_me.mainPanel);
        TalentHallMain.query("#mainPanel")[0].show();
    },
    createOrderByContainer: function () {
        var data = [
            {type: '1', displaydesc: tm.talentHall.Msg.publishedTime},
            {type: '2', displaydesc: tm.talentHall.Msg.attention},
            {type: '3', displaydesc: tm.talentHall.Msg.likeNumber},
            {type: '4', displaydesc: tm.talentHall.Msg.viewNumber}
        ];

        if (TalentHallMain.initParam.applyFlag == 0) {
            data = [
                {type: '1', displaydesc: tm.talentHall.Msg.publishedTime},
                {type: '3', displaydesc: tm.talentHall.Msg.likeNumber},
                {type: '4', displaydesc: tm.talentHall.Msg.viewNumber}
            ];
        }
        var viewCountStore = Ext.create('Ext.data.Store', {
            fields: ['type', 'displaydesc'],
            data: data
        });
        var container = Ext.create('Ext.container.Container', {
            layout: {
                type: 'hbox',
                align: 'center'
            },
            margin: '0 0 0 20',
            items: [
                {
                    xtype: 'component',
                    html: tm.talentHall.Msg.press
                },
                {
                    xtype: 'combobox',
                    margin: '0 0 0 5',
                    queryMode: 'local',
                    itemId: 'orderCombobox',
                    store: viewCountStore,
                    editable:false,
                    // emptyText: '',
                    displayField: 'displaydesc',
                    valueField: 'type',
                    listeners: {
                        select: function (combo, record, eOpts) {
                            TalentHallMain.reloadStore();
                        },
                        render: function (t) {
                            //默认按发布时间排序
                            t.setValue('1');
                        }
                    }

                },
                {
                    xtype: 'component',
                    margin: '0 0 0 5',
                    html: tm.talentHall.Msg.orderby
                }
            ]
        });
        return container;

    },
    /**
     * 根据toolbar 条件查询数据
     */
    getQueryValues: function () {
        return TalentHallMain.query('#queryForm')[0].getValues();
    },
    /**
     * 刷新store数据
     */
    reloadStore: function () {
        Ext.StoreManager.lookup('dataView_dataStore').reload();
    },
    /**
     * 浏览情况
     */
    showViewSituation: function () {
        var talentHallBrowse = Ext.create("Talentmarkets.talenthall.TalentHallBrowse", {
            viewType: 'browseDetails',
            z8501: ''
        });
        TalentHallMain.query("#mainPanel")[0].hide();
        TalentHallMain.add(talentHallBrowse);
    },
    /**
     * 创建卡片视图
     */
    createDataView: function () {
        Ext.define('dataView_Model', {
            extend: 'Ext.data.Model',
            fields: ["b0110", "a0101", "e0122", "e01A1", "z8507", "z8509", "approval", "attention", "z8501", "id"],
            idProperty: 'record_internalId'
        });
        var dataViewStore = Ext.create('Ext.data.Store', {
            storeId: "dataView_dataStore",
            autoLoad: true,
            pageSize: 15,
            proxy: {
                type: 'transaction',
                timeout: 80000,
                extraParams: {operateType: 'queryData'},
                functionId: 'TM000000201',
                reader: {
                    type: 'json',
                    root: 'dataobjs',
                    totalProperty: 'totalCount',
                    idProperty: 'record_internalId'
                }
            },
            loadPage: function (page, options) {
                var me = this,
                    size = me.getPageSize();
                if (me.onChangePage)
                    me.onChangePage();

                me.currentPage = page;
                options = Ext.apply({
                    page: page,
                    start: (page - 1) * size,
                    limit: size,
                    addRecords: !me.getClearOnPageLoad()
                }, options);
                me.read(options);
            },
            listeners: {
                beforeload: function (store) {
                    Ext.apply(store.proxy.extraParams, {
                        queryValues: TalentHallMain.getQueryValues(),
                        orderValue: TalentHallMain.query('#orderCombobox')[0].getValue()
                    });
                },
                load: function (store) {
                }
            }
        });
        var gridPanel = Ext.create('Ext.grid.Panel', {
            store: dataViewStore,
            border: false,
            columns: [],
            // bodyBorder:false,
            // margin:'0 0 0 0',
            width: '100%',
            flex: 1,
            bbar: [{
                xtype: 'pagingtoolbar',
                width: '100%',
                border: false,
                height: 30,
                id: 'pagingtool',
                store: dataViewStore

            }],
            listeners: {
                render: function (panel) {
                    Ext.create('Ext.tip.ToolTip', {
                        target: panel.body,
                        delegate: "td > div.x-grid-cell-inner",
                        shadow: false,
                        trackMouse: true,
                        maxWidth: 800,//最大显示宽度设置为800，文本宽度超过800显示不全解决不了，高度超过显示的最大高度同样也显示不全解决不了  27734 wangb 20170517
                        renderTo: Ext.getBody(),
                        bodyStyle: "background-color:white;border:1px solid #c5c5c5;",
                        listeners: {
                            beforeshow: function updateTipBody(tip) {
                                var div = tip.triggerElement;//.childNodes[0];
                                if (Ext.isEmpty(div))
                                    return false;
                                if (div.offsetWidth < div.scrollWidth || div.offsetHeight < div.scrollHeight - 4) {
                                    //div 里面内容不允许自动换行，滚动条隐藏 27250 27734  wangb 2017-4-24
                                    tip.update("<div style='white-space:nowrap;overflow:hidden;'>" + div.innerHTML + "</div>");
                                } else
                                    return false;
                            }
                        }
                    });
                }
            }
        });
        var tpl = new Ext.XTemplate(
            '<tpl for=".">',
            '<div class="resumeCard" onmouseover="if(document.getElementById(\'{id}_deleteImage\')){document.getElementById(\'{id}_deleteImage\').style.visibility=\'visible\'};"  onmouseout="if(document.getElementById(\'{id}_deleteImage\')){document.getElementById(\'{id}_deleteImage\').style.visibility=\'hidden\'};">',
            '<div style="position:relative;top:6px;left:317px;z-index:10;width:20px;height:20px;">',
            '<tpl if ="TalentHallMain.initParam.removeFlag ==1">',
            '<img id="{id}_deleteImage" onclick="TalentHallMain.changeResumeStatus(\'{z8501}\',\'1\')" src="' + rootPath + '/module/system/questionnaire/images/deletebtn.png" width=20 height=20 style="visibility:hidden;cursor:pointer">',
            '</tpl>',
            '</div>',
            '<table  border=0 cellspacing=0 cellpadding=0 class="cardTable">',
            '<tr style="height: 130px">',
            '<td valign="top" style="width: 70px">',
            '<img src="{photo}" style="width: 80px;height: 110px;margin-left: 5px;margin-top: 5px;border-radius:3px"/>',
            '</td>',
            '<td  valign="top" style="width: 120px" >',
            '<span title=\"{a0101}\"style="width:150px;display: block;font-size: 16px;font-weight: bold;margin-top: 20px; overflow:hidden;white-space:nowrap;text-overflow:ellipsis;">{a0101}</span>',
            '<span style="display: block;font-size: 13px;margin-top: 10px">{dept}</span>',
            '<span style="display: block;font-size: 13px;margin-top: 10px">{e01a1}</span>',
            '</td>',
            '<td valign="top" style="width: 30px" >',
            '<tpl if="attention==1&&TalentHallMain.initParam.applyFlag ==1">',
            '<img onclick="TalentHallMain.changeAttentionStatus(\'{z8501}\',\'0\',\'{id}\')" src="../images/attention.png" style="width: 55px;height: 28px;margin-left: -20px;margin-top: 5px;cursor: pointer"/>',
            '</tpl>',
            '<tpl if="(attention==0 ||!attention)&&TalentHallMain.initParam.applyFlag ==1">',
            '<img onclick="TalentHallMain.changeAttentionStatus(\'{z8501}\',\'1\',\'{id}\')" src="../images/disattention.png" style="width: 55px;height: 28px;margin-left: -20px;margin-top: 5px;cursor: pointer"/>',
            '</tpl>',
            '</td>',
            '</tr>',
            '<tr>',
            '<td colspan="3" valign="top"><div style="padding: 0px 5px 0px 5px;font-size: 13px;height: 80px;overflow: hidden" title="{resumeselfintroduction}">{resumeselfintroduction}</div> </td>',
            '</tr>',
            '<tr style="height: 25px;">',
            '<td colspan="3">',
            '<div style="height: 18px;margin-left: 40px">',
            '<tpl if ="TalentHallMain.initParam.isHaveCard || !TalentHallMain.initParam.cardid">',
            '<span style="color:#1B4A98;cursor: pointer;margin-left: 145px" onclick="javascript:void(0);TalentHallMain.viewResume(\'{objectid}\',\'{z8501}\',\'{id}\')">' + tm.talentHall.Msg.viewResume + '</span>',
            '</tpl>',
            '<tpl if ="!TalentHallMain.initParam.isHaveCard &&TalentHallMain.initParam.cardid ">',
            '<span style="color:darkgray;margin-left: 145px">' + tm.talentHall.Msg.viewResume + '</span>',
            '</tpl>',
            '<img onclick="TalentHallMain.viewCount(\'{z8501}\',\'{a0101}\')" src="../images/view.png" style="width: 18px;height: 18px;cursor: pointer;margin-left: 10px;position: absolute;margin-top: -2px"/>',
            '<span style="margin-left: 34px;position: absolute;margin-top: -1px;color: darkgray;font-weight: 500;">{z8507}</span>',
            '<tpl if="approval==1">',
            '<img onclick="TalentHallMain.changeApprovalStatus(\'{z8501}\',\'0\',\'{id}\')" src="../images/like.png" style="width: 18px;height: 18px;cursor: pointer;margin-left: 50px;position: absolute;margin-top: -2px"/>',
            '</tpl>',
            '<tpl if="(approval==0 || !approval)">',
            '<img onclick="TalentHallMain.changeApprovalStatus(\'{z8501}\',\'1\',\'{id}\')" src="../images/dislike.png" style="width: 18px;height: 18px;cursor: pointer;margin-left: 50px;position: absolute;margin-top: -2px"/>',
            '</tpl>',
            '<span style="margin-left: 73px;position: absolute;margin-top: -1px;color: darkgray;font-weight: 500;">{z8509}</span>',
            '</div>',
            '</td>',
            '</tr>',
            '</table>',
            '</div>',
            '</tpl>'
        );
        var cardView = Ext.widget("dataview", {
            store: gridPanel.getStore(),
            autoScroll: true,
            style: 'float:center',
            // margin:'-15 0 0 0',
            selectedItemCls: 'mySelected',
            itemSelector: 'table.cardTable',
            tpl: tpl,
        });
        if (gridPanel.lockedGrid) {
            gridPanel.lockedGrid.hide();
            gridPanel.normalGrid.hide();
        } else {//没有锁列直接隐藏view，还得隐藏表头
            gridPanel.query("headercontainer")[0].hide();
            gridPanel.view.hide();
        }
        // gridPanel.getView().hide();
        gridPanel.add(cardView);
        return gridPanel;
    },
    /**
     *改变简历状态
     * @param guidkey 简历人员唯一标识
     * @param opt 1撤销 2发布
     */
    changeResumeStatus: function (guidkey, opt) {
        if (opt == '1') {
            Ext.Msg.confirm(tm.contendPos.msg.title, tm.talentHall.Msg.isCancelResume, function (btn) {
                if (btn == 'yes') {
                    TalentHallMain.changeResumeStatusDetail(guidkey, opt);
                }
            });
        } else {
            TalentHallMain.changeResumeStatusDetail(guidkey, opt);
        }

    },
    changeResumeStatusDetail: function (guidkey, opt) {
        var map = new HashMap();
        map.put("guidkey", guidkey);
        map.put("operateType", "changeResumeStatus");
        map.put("opt", opt);
        Rpc({
            functionId: 'TM000000201', success: function (res) {
                var resData = Ext.decode(res.responseText);
                var return_code = resData.return_code;
                var return_msg = resData.return_msg_code;
                if (return_code == "fail") {
                    var str = "";
                    if (opt == 1) {
                        str = tm.talentHall.Msg.cancel;
                    } else if (opt == 2) {
                        str = tm.talentHall.Msg.publish;
                    }
                    if (return_msg == "changeResumeStatusError") {
                        Ext.Msg.alert(tm.contendPos.msg.title, tm.talentHall.Msg.changeResumeStatusError.replace('{resume}', str));
                    }
                } else {
                    TalentHallMain.reloadStore();
                }
            }, scope: this, async: false
        }, map);
    },
    /**
     * 浏览次数（眼睛）点击事件
     */
    viewCount: function (z8501, a0101) {
        var talentHallBrowse = Ext.create("Talentmarkets.talenthall.TalentHallBrowse", {
            viewType: 'browseTimes',
            z8501: z8501,
            a0101: a0101
        });
        TalentHallMain.query("#mainPanel")[0].hide();
        TalentHallMain.add(talentHallBrowse);
    },
    /**
     * 改变关注状态
     * @param guidkey 简历人员唯一标识
     * @param attention 0 取消关注 1 关注
     * @param id 当前记录唯一标识
     */
    changeAttentionStatus: function (z8501, attention, id) {
        var map = new HashMap();
        map.put("z8501", z8501);
        map.put("operateType", "changeAttentionStatus");
        map.put("attention", attention);
        Rpc({
            functionId: 'TM000000201', success: function (res) {
                var resData = Ext.decode(res.responseText);
                var return_code = resData.return_code;
                var return_msg = resData.return_msg_code;
                if (return_code == "fail") {
                    Ext.Msg.alert(tm.contendPos.msg.title, tm.talentHall.Msg.changeAprovalStatusError);
                } else {
                    var record = Ext.StoreManager.lookup('dataView_dataStore').findRecord("id", id);
                    record.set("attention", attention);
                    if (TalentHallMain.query("#orderCombobox")[0]) {
                        if (TalentHallMain.query("#orderCombobox")[0].getValue() == 2) { //按关注排序
                            TalentHallMain.reloadStore();
                        }
                    }
                }
            }, scope: this, async: false
        }, map);
    },
    /**
     * 改变点赞状态
     * @param guidkey 简历人员唯一标识
     * @param approval 0 取消点赞 1 点赞
     * @param id 当前记录唯一标识
     */
    changeApprovalStatus: function (z8501, approval, id) {
        if (TalentHallMain.initParam.applyFlag == 0) {
            return;
        }
        var map = new HashMap();
        map.put("z8501", z8501);
        map.put("operateType", "changeApprovalStatus");
        map.put("approval", approval);
        Rpc({
            functionId: 'TM000000201', success: function (res) {
                var resData = Ext.decode(res.responseText);
                var return_code = resData.return_code;
                var return_msg = resData.return_msg_code;
                if (return_code == "fail") {
                    Ext.Msg.alert(tm.contendPos.msg.title, tm.talentHall.Msg.changeAprovalStatusError);
                } else {
                    var record = Ext.StoreManager.lookup('dataView_dataStore').findRecord("id", id);
                    record.set("approval", approval);
                    var z8509 = record.get("z8509");
                    if (approval == 1) {
                        z8509 = parseInt(z8509) + 1;
                    } else if (approval == 0) {
                        z8509 = parseInt(z8509) - 1;
                    }
                    record.set("z8509", z8509);
                    if (TalentHallMain.query("#orderCombobox")[0]) {
                        if (TalentHallMain.query("#orderCombobox")[0].getValue() == 3) { //按点赞数排序
                            TalentHallMain.reloadStore();
                        }
                    }
                }
            }, scope: this, async: false
        }, map);
    },
    /**
     * 查看简历
     */
    viewResume: function (objectid, z8501, id) {
        if(Ext.getCmp('resumeWindow')){
            return;
        }
        if (!TalentHallMain.initParam.cardid) {
            Ext.Msg.alert(tm.contendPos.msg.title, tm.talentHall.Msg.peleaseSetResumeCard);
            return;
        }
        var src = '/module/card/cardCommonSearch.jsp?inforkind=1&a0100=' + objectid + '&tabid=' + TalentHallMain.initParam.cardid + '&cardFlag=1&fieldpriv=1';
        var html = '<iframe id="iframeId" frameborder="0" width="100%" height="100%" src=' + src + '></iframe>';
        var window = Ext.create('Ext.window.Window', {
            height: document.documentElement.clientHeight * 3 / 4 > 550 ? 550 : document.documentElement.clientHeight * 3 / 4,
            width: document.documentElement.clientWidth * 2 / 3 > 950 ? 950 : document.documentElement.clientWidth * 2 / 3,
            maximizable: true,
            id:'resumeWindow',
            border: false,
            maximized: true,
            title: tm.talentHall.Msg.resumeRegistrationForm,
            layout: 'fit',
            html: html
        });
        window.show();
        //隐藏toolbar
        var interval = setInterval(function () {
            var iframeExt = document.getElementById("iframeId").contentWindow.Ext;
            if (iframeExt) {
                var cardPanel = iframeExt.getCmp('cardtabPanelId');
                if (cardPanel) {
                    var ownerCt = cardPanel.ownerCt;
                    ownerCt.setBorder(false);
                    ownerCt.getDockedItems()[0].hide();
                    clearInterval(interval);
                }
            }
        }, 500);
        //调后台  执行浏览次数加一
        if (TalentHallMain.initParam.applyFlag == 0) {
            return;
        }
        var map = new HashMap();
        map.put("operateType", "viewCount");
        map.put("z8501", z8501);
        Rpc({
            functionId: 'TM000000201', success: function (res) {
                var resData = Ext.decode(res.responseText);
                var return_code = resData.return_code;
                var return_msg = resData.return_msg_code;
                if (return_code == "fail") {
                    // Ext.Msg.alert(tm.contendPos.msg.title, tm.talentHall.Msg.changeAprovalStatusError);
                } else {
                    var record = Ext.StoreManager.lookup('dataView_dataStore').findRecord("id", id);
                    var z8507 = record.get("z8507");
                    z8507 = parseInt(z8507) + 1;
                    record.set("z8507", z8507);
                    if (TalentHallMain.query("#orderCombobox")[0]) {
                        if (TalentHallMain.query("#orderCombobox")[0].getValue() == 4) { //按浏览次数排序
                            TalentHallMain.reloadStore();
                        }
                    }
                }
            }, scope: this, async: false
        }, map);
    }
});