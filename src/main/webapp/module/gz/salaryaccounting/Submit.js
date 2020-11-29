//薪资提交“显示提交方式窗口”时候弹窗
Ext.define('SalaryUL.Submit', {
    constructor: function (config) {
        submitScope = this;
        submitScope.isTotalControl = config.isTotalControl;
        submitScope.verify_ctrl = config.verify_ctrl;
        submitScope.ctrlType = config.ctrlType;
        submitScope.subNoShowUpdateFashion = config.subNoShowUpdateFashion;
        submitScope.salaryid = config.salaryid;
        submitScope.viewtype = config.viewtype;
        submitScope.appdate = config.appdate;
        submitScope.count = config.count;
        submitScope.imodule = config.imodule;//薪资和保险区分标识  1：保险  否则是薪资
        submitScope.flag = config.flag;//ff:薪资发放 sp:薪资审批
        submitScope.collectPoint = config.collectPoint;
        submitScope.cound = config.cound;
        if (config.selectID)//如果没传这个值，则赋值下，防止后面的substring报错
            submitScope.selectID = config.selectID;
        else
            submitScope.selectID = "`";
        submitScope.win = "";
        this.init();
    },
    init: function () {
        var optionStore = Ext.create('Ext.data.Store', {
            fields: ['type', 'name'],
            data: [
                {"type": "0", "name": "更新当前记录"},
                {"type": "1", "name": "新增记录"},
                {"type": "2", "name": "当前记录不变"}
            ]
        });
        var box = Ext.create('Ext.form.ComboBox', {//执行列所用的下拉框
            store: optionStore,
            queryMode: 'local',
            repeatTriggerClick: true,
            displayField: 'name',
            valueField: 'type'
        });
        var store = Ext.create('Ext.data.Store', {
            fields: ['setid', 'name', 'type'],
            proxy: {
                type: 'transaction',
                functionId: 'GZ00000020',
                extraParams: {
                    salaryid: submitScope.salaryid
                },
                reader: {
                    type: 'json',
                    root: 'data'
                }
            },
            autoLoad: true
        });
        store.on('load', function (store, rds) {
            for (var i = 0; i < rds.length; i++) {
                if (rds[i].get("type") == "0") {
                    Ext.getCmp('advance').show();
                    break;
                }
            }
        });
        var panel = Ext.create('Ext.grid.Panel', {
            store: store,
            width: 390,
            height: 380,
            columnLines: true,
            rowLines: true,
            columns: [
                {text: '子集名称', menuDisabled: true, dataIndex: 'name', flex: 70},
                {
                    text: '操作方式', menuDisabled: true, dataIndex: 'type', flex: 30, editor: box, renderer: function (v) {
                    if (v == '0') return "更新当前记录";
                    else if (v == '1') return "新增记录";
                    else return "当前记录不变";
                }
                }
            ],
            plugins: [
                Ext.create('Ext.grid.plugin.CellEditing', {
                    clicksToEdit: 1
                })
            ],
            listeners: {
                'edit': function (combo, ecords) {
                    var flag = false;
                    store.each(function (record) {
                        if (record.get('type') == "0") {
                            flag = true;
                        }
                    });
                    if (flag) {
                        Ext.getCmp('advance').show();
                    } else {
                        Ext.getCmp('advance').hide();
                    }
                }
            }
        });
        submitScope.win = Ext.widget("window", {
            title: '请选择数据操作方式',
            height: 460,
            width: 400,
            layout: 'fit',
            modal: true,
            closeAction: 'destroy',
            items: [{
                xtype: 'panel',
                border: false,
                items: [panel],
                buttons: [
                    {xtype: 'tbfill'},
                    {
                        text: "高级...",
                        id: 'advance',
                        hidden: true,
                        handler: function () {
                            var sets = "";
                            if (store.count() > 0) {
                                for (var i = 0; i < store.count(); i++) {
                                    var record = store.getAt(i);
                                    var setid = record.get("setid");
                                    if (record.get('type') == "0") {
                                        sets += "/" + setid;
                                    }
                                }
                            }
                            submitScope.open_sumbmit_advance(sets);
                        }
                    },
                    {
                        text: common.button.ok,
                        handler: function () {
                            submitScope.setid = "";
                            submitScope.type = "";
                            if (store.count() > 0) {
                                for (var i = 0; i < store.count(); i++) {
                                    var record = store.getAt(i);
                                    var setid = record.get("setid");
                                    var type = record.get("type");
                                    submitScope.setid += "/" + setid;
                                    submitScope.type += "/" + type;
                                }
                            }
                            Ext.showConfirm("确认提交数据吗？", function (id) {
                                    if (id == 'yes') {
                                        submitScope.verify();
                                    }
                                }
                            );
                        }
                    },
                    {
                        text: common.button.cancel,
                        handler: function () {
                            submitScope.win.close();
                        }
                    },
                    {xtype: 'tbfill'}
                ]
            }]
        });
        submitScope.win.show();
    },
    //提交页面高级按钮弹窗
    open_sumbmit_advance: function (sets) {
        var optionStore = Ext.create('Ext.data.Store', {
            fields: ['flag', 'name'],
            data: [
                {"flag": "0", "name": "累加更新"},
                {"flag": "1", "name": "替换更新"}
            ]
        });
        var box = Ext.create('Ext.form.ComboBox', {//执行列所用的下拉框
            store: optionStore,
            queryMode: 'local',
            repeatTriggerClick: true,
            displayField: 'name',
            valueField: 'flag'
        });
        if (!!!submitScope.updateObj)
            submitScope.updateObj = new Array();

        var store = Ext.create('Ext.data.Store', {
            fields: ['itemid', 'itemdesc', 'flag'],
            proxy: {
                type: 'transaction',
                functionId: 'GZ00000021',
                extraParams: {
                    salaryid: submitScope.salaryid,
                    sets: sets,
                    updateObj: submitScope.updateObj
                },
                reader: {
                    type: 'json',
                    root: 'data'
                }
            },
            autoLoad: true
        });

        var panel = Ext.create('Ext.grid.Panel', {
            store: store,
            width: 350,
            height: 350,
            columnLines: true,
            rowLines: true,
            columns: [
                {text: '指标名称', menuDisabled: true, dataIndex: 'itemdesc', flex: 70},
                {
                    text: '更新方式', menuDisabled: true, dataIndex: 'flag', flex: 30, editor: box, renderer: function (v) {
                    if (v == '0') return "累加更新";
                    else if (v == '1') return "替换更新";
                }
                }
            ],
            plugins: [
                Ext.create('Ext.grid.plugin.CellEditing', {
                    clicksToEdit: 1
                })
            ]
        });
        var win = Ext.widget("window", {
            title: '更新高级设置',
            height: 430,
            width: 360,
            layout: 'fit',
            modal: true,
            closeAction: 'destroy',
            items: [{
                xtype: 'panel',
                border: false,
                items: [panel],
                buttons: [
                    {xtype: 'tbfill'},
                    {
                        text: common.button.ok,
                        handler: function () {
                            submitScope.items = "";
                            submitScope.uptypes = "";
                            if (store.count() > 0) {
                                var updateObj = new Array();
                                for (var i = 0; i < store.count(); i++) {
                                    var record = store.getAt(i);
                                    var itemid = record.get("itemid");
                                    var flag = record.get("flag");
                                    var map = new HashMap();
                                    map.put("itemid", record.get('itemid'));
                                    map.put("itemdesc", record.get('itemdesc'));
                                    map.put("flag", record.get('flag'));
                                    updateObj[i] = map;
                                    submitScope.items += "/" + itemid;
                                    submitScope.uptypes += "/" + flag;
                                }
                                submitScope.updateObj = updateObj;
                            }
                            win.close();
                        }
                    },
                    {
                        text: common.button.cancel,
                        handler: function () {
                            win.close();
                        }
                    },
                    {xtype: 'tbfill'}
                ]
            }]
        });
        win.show();
    },
    //审核
    verify: function () {
        if (submitScope.verify_ctrl != '1') {//如果没设置审核那么直接去执行总额校验
            if (submitScope.isTotalControl == '1')
                Ext.MessageBox.wait("正在总额校验，请稍候...", "等待");
            else
                Ext.MessageBox.wait("正在提交，请稍候...", "等待");
            submitScope.totalControl();
        } else {
            Ext.MessageBox.wait("正在数据审核，请稍候...", "等待");
            var map = new HashMap();
            map.put("salaryid", submitScope.salaryid);
            map.put("appdate", submitScope.appdate);
            map.put("count", submitScope.count);
            map.put("imodule", submitScope.imodule);
            map.put("viewtype", submitScope.viewtype);
            map.put("collectPoint", submitScope.collectPoint);
            map.put("selectID", submitScope.selectID);
            Rpc({
                functionId: 'GZ00000016', success: function (form, action) {

                    var result = Ext.decode(form.responseText);
                    var flag = result.succeed;
                    if (flag == true) {
                        if (result.msg == 'yes') {
                            submitScope.totalControl();
                        } else if (result.fileName.length > 0) {
                            Ext.MessageBox.close();
                            Ext.showAlert('审核不通过！');
                            var fieldName = getDecodeStr(result.fileName);
                            window.location.target = "_blank";
                            window.location.href = "/servlet/vfsservlet?fileid="+fieldName+"&fromjavafolder=true";
                        } else {
                            Ext.MessageBox.close();
                            Ext.showAlert('审核不通过！');
                        }
                    } else {
                        Ext.MessageBox.close();
                        Ext.showAlert(result.message);
                    }
                }
            }, map);
        }
    },
    //提交必走的数据验证方法 包括总额校验、可操作数据条数验证
    totalControl: function () {
        var map = new HashMap();
        map.put("salaryid", submitScope.salaryid);
        map.put("viewtype", submitScope.viewtype);
        map.put("selectID", submitScope.selectID.substring(1));
        map.put("collectPoint", submitScope.collectPoint);
        map.put("appdate", submitScope.appdate);
        map.put("count", submitScope.count);
        map.put("cound", submitScope.cound);
        map.put("type", '2');//1:报批 2：提交
        Rpc({
            functionId: 'GZ00000018', success: function (form, action) {
                var result = Ext.decode(form.responseText);
                Ext.MessageBox.close();
                if (result.succeed == true) {
                    if(submitScope.flag == "ff") {
                        var dataCount = result.dataCount;
                        if (dataCount == 0) {
                            Ext.showAlert(gz.msg.notExistsData.replace("{0}", gz.label.submit));
                        } else {
                            if (result.info == 'success') {//通过总额控制
                                Ext.Msg.confirm(common.button.promptmessage, gz.msg.canUseData.replace("{0}", gz.label.submit).replace("{1}", dataCount),
                                    function (s) {
                                        if (s == "yes") {
                                            submitScope.submit();
                                        }
                                    }
                                );
                            } else {//未通过
                                if (result.ctrlType == "0") {//总额校验不强行控制
                                    Ext.Msg.confirm(common.button.promptmessage, result.info+"&nbsp",
                                        function (id) {
                                            if (id == 'yes') {
                                                Ext.Msg.confirm(common.button.promptmessage, gz.msg.canUseData.replace("{0}", gz.label.submit).replace("{1}", dataCount),
                                                    function (s) {
                                                        if (s == "yes") {
                                                            submitScope.submit();
                                                        }
                                                    }
                                                );
                                            }
                                        }
                                    );
                                } else {
                                    Ext.showAlert(result.info);
                                }
                            }

                        }
                    }else{
                        if (result.info == 'success') {//通过总额控制
                            submitScope.submit();
                        } else {//未通过
                            if (result.ctrlType == "0") {//总额校验不强行控制
                                Ext.Msg.confirm(common.button.promptmessage, result.info,
                                    function (id) {
                                        if (id == 'yes') {
                                            submitScope.submit();//提交数据
                                        }
                                    }
                                );
                            } else {
                                Ext.showAlert(result.info);
                            }
                        }
                    }
                } else {
                    Ext.showAlert(result.message);
                }
            }
        }, map);
    },
    //具体提交操作
    submit: function () {
        var map = new HashMap();
        map.put("salaryid", submitScope.salaryid);
        map.put("setid", submitScope.setid);
        map.put("type", submitScope.type);
        map.put("items", submitScope.items == undefined ? "" : submitScope.items);
        map.put("uptypes", submitScope.uptypes == undefined ? "" : submitScope.uptypes);
        map.put("appdate", submitScope.appdate);
        map.put("count", submitScope.count);
        map.put("subNoShowUpdateFashion", submitScope.subNoShowUpdateFashion);
        Ext.MessageBox.wait("正在提交，请稍候...", "等待");
        if (submitScope.flag == "ff") {
            LRpc({
                functionId: 'GZ00000022', success: function (form, action) {
                    Ext.MessageBox.close();
                    var result = Ext.decode(form.responseText);
                    var flag = result.succeed;
                    if (flag) {
                        submitScope.win.close();
                        GzGlobal.loadStore();
                        //提交改为不再刷新整个页面 zhanghua 2018-06-21
                        var sp_flag=result.sp_flag;
                        if(sp_flag=="06"){
                            GzGlobal.isSubed=true;
                            //如果状态有修改，则将右上角状态修改为最新状态
                            document.getElementById("sp_flagname").innerHTML = gz.label.end
                        }
                    } else {
                        Ext.showAlert(result.message);
                    }
                }
            }, map);
        } else if (submitScope.flag == "sp") {
            map.put("selectID", submitScope.selectID.substring(1));
            map.put("cound", submitScope.cound);
            map.put("collectPoint", submitScope.collectPoint);
            map.put("fromPending",SalarySpCollect.returnflag=='menu'?'0':'1');
            LRpc({
                functionId: 'GZ00000440', success: function (form, action) {
                    Ext.MessageBox.close();
                    var result = Ext.decode(form.responseText);
                    var flag = result.succeed;
                    if (flag) {
                        submitScope.win.close();
                        //剩余可批条数
                        var listNumber=result.lastNumber;
                        //从待办进来，且剩余可批条数为0，那么跳回待办页面
                        if(listNumber==0&&SalarySpCollect.returnflag!='menu'){
                            SalarySpCollect.back();
                        }else {
                            SalarySpCollect.reload();
                        }
                    } else {
                        Ext.showAlert(result.message);
                    }
                }
            }, map);
        }
    }
});