/**
 * 薪资发放-历史填报数据页面
 * 2018-08-28 zhanghua
 */

Ext.define('SalaryTemplateUL.ApprovalSituation', {
    constructor: function (config) {
        ApprovalSituation = this;
        ApprovalSituation.imodule = config.imodule;//薪资和保险区分标识  1：保险  否则是薪资
        ApprovalSituation.viewtype = config.viewtype;// 页面区分 0:薪资发放  1:审批  2:上报
        ApprovalSituation.salaryid = config.salaryid;
        ApprovalSituation.appdate = config.appdate;//业务日期
        ApprovalSituation.count = config.count;//次数

        ApprovalSituation.manager = config.manager;// 是否共享用户   0：否或者管理员 1：是
        Ext.MessageBox.wait("正在打开，请稍候...", "等待");
        var map = new HashMap();
        map.put("salaryid", ApprovalSituation.salaryid);
        map.put("imodule", ApprovalSituation.imodule);
        map.put("appdate", ApprovalSituation.appdate);
        map.put("viewtype", ApprovalSituation.viewtype);
        map.put("count", ApprovalSituation.count);
        map.put("manager", ApprovalSituation.manager);
        map.put("optType", "init");
        ApprovalSituation.initMap = map;
        Rpc({
            functionId: 'GZ00000236', success: function (form, action) {
                Ext.MessageBox.close();//关闭遮罩
                var result = Ext.decode(form.responseText);
                if (result.succeed) {
                    ApprovalSituation.init(result);
                } else {
                    Ext.showAlert(result.message);
                }
            }
        }, map);
    },
    init: function (result) {

        var conditions = result.tableConfig;
        var obj = Ext.decode(conditions);
        ApprovalSituation.isNeedSalaryarchive = result.isNeedSalaryarchive;
        //生成弹出得window
        var win = Ext.widget("window", {
            id: "ApprovalSituationWin",
            maximized: true,//大小适应屏幕
            border: false,
            title: gz.label.reportedInformation,//"填报信息",
            frame: false,
            resizable: false,
            tools: [{id: 'ApprovalSituation_schemeSetting', xtype: 'toolbar', border: false}],
            draggable: false,
            layout: 'fit',
            modal: true,
            closeAction: 'destroy'
        });
        win.show();

        ApprovalSituation.tableObj = new BuildTableObj(obj);
        ApprovalSituation.tableObj.setSchemeViewConfig({//配置栏目设置参数
            publicPlan: result.isShowPublicPlan == '1' ? true : false,
            sum: false,
            lock: true,
            merge: false,
            pageSize: '20'
        });

        //年份store
        var yearListStore = Ext.create("Ext.data.Store", {
            fields: ["dataName", "dataValue"],
            data: result.yearList
        });
        //应用机构store
        var AgencyListStore = Ext.create("Ext.data.Store", {
            fields: ["dataName", "dataValue"],
            data: result.AgencyList
        });

        var toolbar = Ext.create('Ext.toolbar.Toolbar', {
            border: 0,
            id: "toolbar",
            dock: 'top',
            height: 31,
            padding: '2px 0px 5px 0px',
            items: [{
                xtype: 'button',
                text: gz.label.outExcel,//'导出excel'
                margin: '0 2 0 0',
                listeners: {
                    click: function () {
                        ApprovalSituation.ExportExcel();
                    }
                }

            }, {
                xtype: 'button',
                text: gz.label.detailData,//'个人明细数据',
                margin: '0 3 0 0',
                listeners: {
                    click: function () {
                        ApprovalSituation.openQueryPerson();
                    }
                }

            }, {
                xtype: "combobox",//年份下拉
                width: 100,
                id: 'yearListBox',
                store: yearListStore,
                editable: false,
                displayField: "dataName",
                valueField: "dataValue",
                queryMode: "local"
            }, {
                xtype: 'label',
                text: gz.label.year,//'年',
                margin: '0 4',
                width: 20
            }, {
                xtype: "combobox",//应用机构下拉
                id: 'AgencyListBox',
                store: AgencyListStore,
                editable: false,
                hidden: result.AgencyList.length <= 1 ? true : false,//没有可选择的应用机构时 不显示下拉
                width: 130,
                displayField: "dataName",
                valueField: "dataValue",
                queryMode: "local"
            }]
        });
        var yearListBox = Ext.getCmp('yearListBox');
        yearListBox.setValue(yearListStore.getAt(0).get('dataValue'));
        if (result.AgencyList.length > 1) {
            var AgencyListBox = Ext.getCmp('AgencyListBox');
            AgencyListBox.setValue(AgencyListStore.getAt(0).get('dataValue'));
        }

        //下拉列表选择事件
        var filterFun = function (e, value) {
            var map = new HashMap();
            map.put("salaryid", ApprovalSituation.salaryid);
            map.put("imodule", ApprovalSituation.imodule);
            map.put("appdate", ApprovalSituation.appdate);
            map.put("count", ApprovalSituation.count);
            map.put("manager", ApprovalSituation.manager);
            map.put("filterYear", Ext.getCmp('yearListBox').getValue());
            map.put("filterAgency",  Ext.getCmp('AgencyListBox').getValue()==null?"":Ext.getCmp('AgencyListBox').getValue());
            map.put("optType", "filter");
            Rpc({
                functionId: 'GZ00000236', success: function (form, action) {
                    var result = Ext.decode(form.responseText);
                    if (result.succeed) {
                        ApprovalSituation.isNeedSalaryarchive = result.isNeedSalaryarchive;
                        ApprovalSituation.reloadTable();
                    } else {
                        Ext.showAlert(result.message);
                    }
                }
            }, map);
        };
        //添加事件
        yearListBox.on('change', filterFun);
        if (result.AgencyList.length > 1) {
            AgencyListBox.on('change', filterFun);
        }


        ApprovalSituation.tableObj.insertItem(toolbar, 0);
        win.add(ApprovalSituation.tableObj.getMainPanel());
    },
    reloadTable: function () {
        var store = Ext.data.StoreManager.lookup('ApprovalSituation_dataStore');
        store.load();
    },

    closeSettingWindow: function () {
        Ext.getCmp('ApprovalSituationWin').destroy();
        var map = ApprovalSituation.initMap;
        ApprovalSituation = null;
        Ext.require('SalaryTemplateUL.ApprovalSituation', function () {
            Ext.create("SalaryTemplateUL.ApprovalSituation", map);
        });

    },
    //详情按钮渲染
    buildDetails: function (value, metaData, Record) {
        var a00z2 = Record.data.a00z2;
        var a00z3 = Record.data.a00z3;
        var agencyId = Record.data.agencyid;
        var html = "<a href=javascript:ApprovalSituation.openHistoryDetails('" + a00z2 + "','" + a00z3 + "','" + agencyId + "');><img src='/images/new_module/search_blue.gif' border=0></a>";
        return html;
    },
    //打开详情页面
    openHistoryDetails: function (a00z2, a00z3, agencyId) {
        var obj = new Object();
        obj.salaryid = ApprovalSituation.salaryid;
        obj.appdate = a00z2;
        obj.count = a00z3;
        obj.imodule = ApprovalSituation.imodule;
        obj.viewtype = ApprovalSituation.viewtype;
        obj.manager = ApprovalSituation.manager;
        obj.isNeedSalaryarchive = ApprovalSituation.isNeedSalaryarchive;
        if ('undefined' == agencyId || undefined == agencyId) {
            obj.agencyFilter = Ext.getCmp('AgencyListBox').getValue();
        }
        else {
            obj.agencyFilter = agencyId;
        }
        obj.optType = "detail";
        Ext.require('SalaryTemplateUL.SalaryDetails', function () {
            Ext.create("SalaryTemplateUL.SalaryDetails", obj);
        });
    },
    //行双击事件
    rowclick: function (e, record) {
        var a00z2 = record.data.a00z2 + "";
        var a00z3 = record.data.a00z3 + "";
        var agencyId = record.data.agencyid;
        ApprovalSituation.openHistoryDetails(a00z2, a00z3, agencyId);
    },
    //打开人员筛选页面
    openQueryPerson: function () {
        var obj = new Object();
        obj.salaryid = ApprovalSituation.salaryid;
        obj.imodule = ApprovalSituation.imodule;
        obj.viewtype = ApprovalSituation.viewtype;
        obj.manager = ApprovalSituation.manager;
        obj.isNeedSalaryarchive = ApprovalSituation.isNeedSalaryarchive;
        obj.appdate = ApprovalSituation.appdate;//业务日期
        obj.count = ApprovalSituation.count;//次数
        obj.filterYear = Ext.getCmp('yearListBox').getValue();//年份过滤
        obj.optType = "queryPerson";
        Ext.require('SalaryTemplateUL.SalaryDetails', function () {
            Ext.create("SalaryTemplateUL.SalaryDetails", obj);
        });
    },


    //导出excel
    ExportExcel: function () {
        Ext.MessageBox.wait("正在导出，请稍候...", "等待");

        var map = new HashMap();
        map.put("salaryid", ApprovalSituation.salaryid);
        map.put("imodule", ApprovalSituation.imodule);
        map.put("appdate", ApprovalSituation.appdate);
        map.put("viewtype", ApprovalSituation.viewtype);
        map.put("count", ApprovalSituation.count);
        map.put("manager", ApprovalSituation.manager);
        map.put("isNeedSalaryarchive", ApprovalSituation.isNeedSalaryarchive);
        map.put("filterYear", Ext.getCmp('yearListBox').getValue());
        map.put("filterAgency", Ext.getCmp('AgencyListBox').getValue()==null?"":Ext.getCmp('AgencyListBox').getValue());
        map.put("optType", "ExportExcel");
        Rpc({
            functionId: 'GZ00000236', success: function (form, action) {
                Ext.MessageBox.close();
                var result = Ext.decode(form.responseText);
                if (result.succeed) {
                	var fieldName = getDecodeStr(result.fileName);
                    window.location.target = "_blank";
                    window.location.href = "/servlet/vfsservlet?fileid=" + fieldName +"&fromjavafolder=true";
                } else {
                    Ext.showAlert(result.message);
                }

            }
        }, map);
    }

});