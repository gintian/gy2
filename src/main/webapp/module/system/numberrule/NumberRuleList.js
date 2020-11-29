function GUID() {
    this.date = new Date(); /* 判断是否初始化过，如果初始化过以下代码，则以下代码将不再执行，实际中只执行一次 */
    if (typeof this.newGUID != 'function') { /* 生成GUID码 */
        GUID.prototype.newGUID = function () {
            this.date = new Date();
            var guidStr = '';
            sexadecimalDate = this.hexadecimal(this.getGUIDDate(), 16);
            sexadecimalTime = this.hexadecimal(this.getGUIDTime(), 16);
            for (var i = 0; i < 9; i++) {
                guidStr += Math.floor(Math.random() * 16).toString(16);
            }
            guidStr += sexadecimalDate;
            guidStr += sexadecimalTime;
            while (guidStr.length < 32) {
                guidStr += Math.floor(Math.random() * 16).toString(16);
            }
            return guidStr.toUpperCase();
        }
        /* * 功能：获取当前日期的GUID格式，即8位数的日期：19700101 * 返回值：返回GUID日期格式的字条串 */
        GUID.prototype.getGUIDDate = function () {
            return this.date.getFullYear()
                + this.addZero(this.date.getMonth() + 1)
                + this.addZero(this.date.getDay());
        }
        /* * 功能：获取当前时间的GUID格式，即8位数的时间，包括毫秒，毫秒为2位数：12300933 * 返回值：返回GUID日期格式的字条串 */
        GUID.prototype.getGUIDTime = function () {
            return this.addZero(this.date.getHours())
                + this.addZero(this.date.getMinutes())
                + this.addZero(this.date.getSeconds())
                + this.addZero(parseInt(this.date.getMilliseconds() / 10));
        }
        /*
         * * 功能: 为一位数的正整数前面添加0，如果是可以转成非NaN数字的字符串也可以实现 * 参数:
         * 参数表示准备再前面添加0的数字或可以转换成数字的字符串 * 返回值: 如果符合条件，返回添加0后的字条串类型，否则返回自身的字符串
         */
        GUID.prototype.addZero = function (num) {
            if (Number(num).toString() != 'NaN' && num >= 0 && num < 10) {
                return '0' + Math.floor(num);
            } else {
                return num.toString();
            }
        }
        /*
         * * 功能：将y进制的数值，转换为x进制的数值 *
         * 参数：第1个参数表示欲转换的数值；第2个参数表示欲转换的进制；第3个参数可选，表示当前的进制数，如不写则为10 *
         * 返回值：返回转换后的字符串
         */
        GUID.prototype.hexadecimal = function (num, x, y) {
            if (y != undefined) {
                return parseInt(num.toString(), y).toString(x);
            } else {
                return parseInt(num.toString()).toString(x);
            }
        }
        /* * 功能：格式化32位的字符串为GUID模式的字符串 * 参数：第1个参数表示32位的字符串 * 返回值：标准GUID格式的字符串 */
        GUID.prototype.formatGUID = function (guidStr) {
            var str1 = guidStr.slice(0, 8) + '-', str2 = guidStr.slice(8, 12)
                + '-', str3 = guidStr.slice(12, 16) + '-', str4 = guidStr
                    .slice(16, 20)
                + '-', str5 = guidStr.slice(20);
            return str1 + str2 + str3 + str4 + str5;
        }
    }
}

Ext.define('NumberRuleListUL.NumberRuleList', {
    numberRule: '',
    tableObj: '',
    constructor: function (config) {
        numberRule = this;
        numberRule.fieldsMap = {};
        numberRule.init();// 初始化界面
    },
    init: function (url) {
        var map = new HashMap();
        map.put('method', 'init');
        Rpc({functionId: 'SYS20200520', async: false, success: numberRule.getTableOK}, map);
    },
    getTableOK: function (form, action) {
        var result = Ext.decode(form.responseText);
        var conditions = result.numberRuleTable;
        numberRule.fieldsMap = result.fieldsMap;
        var obj = Ext.decode(conditions);
        tableObj = new BuildTableObj(obj);
        numberRule.createSearchPanel();
    },
    // 查询控件
    createSearchPanel: function () {
        var me = this;
        var map = new HashMap();
        //map.put("fieldsMap",numberRule.fieldsMap);
        me.SearchBox = Ext.create("EHR.querybox.QueryBox", {
            hideQueryScheme: true,
            emptyText: '查询: 申请者/手机号/系统名称',
            subModuleId: "gz_numberRule_query",
            customParams: map,
            funcId: "SYS20200520",
            success: numberRule.loadTable//重新加载数据列表
        });
        var toolBar = Ext.getCmp("numberRuleTable_toolbar");
        toolBar.add(me.SearchBox);
    },

    //刷新页面
    loadTable: function (form) {
        var store = Ext.data.StoreManager.lookup('numberRuleTable_dataStore');
        store.load();
    },
    toRegister: function () {
        var map = new HashMap();
        map.put('method', 'toRegister');
        Rpc({
            functionId: 'SYS20200520',
            async: true,
            success: function (form) {
                var returnStr = Ext.decode(form.responseText);

                numberRule.registerSystem(returnStr);
            }
        }, map);
    },
    registerSystem: function (result) {
        var regMobile = /^1[3456789]\d{9}$/;
        var bodyPanel = Ext.create('Ext.form.Panel', {
            bodyPadding: '20',
            margin: 'auto',
            border: false,
            layout: 'anchor',
            defaults: {
                anchor: '90%'
            },
            items: [{
                xtype: 'textfield',
                id: 'applicant',
                fieldLabel: "申请者",
                labelSeparator: '',
                labelAlign: 'right',
                maxLength: 30,//允许最大长度
                margin: '0 0 10 0',
                value: result.currentUsrName
            }, {
                xtype: 'textfield',
                id: 'mobile',
                fieldLabel: "手机号",
                labelSeparator: '',
                labelAlign: 'right',
                maxLength: 50,
                margin: '0 0 10 0',
                value: result.currentUsrMobile
            }, {
                xtype: 'textfield',
                id: 'systemName',
                validator: function (value) {
                    if (value.length < 1)
                        return "系统名称必填";
                    return true;
                },
                beforeLabelTextTpl: "<font color='red'> * </font>",
                fieldLabel: "系统名称(中文)",
                labelSeparator: '',
                labelAlign: 'right',
                grow: false,
                maxLength: 100,
                margin: '0 0 10 0'
            }, {
                xtype: 'textfield',
                id: 'systemCode',
                validator: function (value) {
                    if (value.length < 1)
                        return "系统简称必填";
                    return true;
                },
                beforeLabelTextTpl: "<font color='red'> * </font>",
                fieldLabel: "系统简称(英文)",
                labelSeparator: '',
                labelAlign: 'right',
                grow: false,
                maxLength: 24,
                margin: '0 0 10 0'
            }
            ],
            buttonAlign: 'center',
            buttons: [{
                text: "确定",
                formBind: true,
                handler: function () {
                    var applicant = Ext.util.Format.trim(Ext.getCmp('applicant').getValue());
                    var mobile = Ext.util.Format.trim(Ext.getCmp('mobile').getValue());

                    if (mobile.length != 0) {
                        if (!regMobile.test(mobile)) {
                            Ext.showAlert("手机号格式不正确");
                            return;
                        }
                    }

                    var systemName = Ext.getCmp('systemName').getValue();
                    var systemCode = Ext.getCmp('systemCode').getValue();

                    var map = new HashMap();
                    map.put('method', 'registerSystem');
                    map.put("applicant", applicant);
                    map.put("mobile", mobile);
                    map.put("systemName", systemName);
                    map.put("systemCode", systemCode);

                    Rpc({
                        functionId: 'SYS20200520',
                        async: true,
                        success: function (form) {
                            var returnStr = Ext.decode(form.responseText);
                            if (returnStr.result == true) {
                                tableObj.dataStore.reload();
                            } else {
                                Ext.showAlert("登记出错了[" + returnStr.desc + "].");
                            }
                            win.close();
                        }
                    }, map);
                }
            }, {
                text: "取消",
                handler: function () {
                    win.close();
                }
            }]
        });
        var win = Ext.widget("window", {
            title: "登记系统",
            height: 260,
            width: 400,
            minButtonWidth: 40,
            layout: 'fit',
            bodyStyle: 'background:#ffffff;',
            modal: true,
            resizable: false,
            closeAction: 'destroy',
            items: [bodyPanel]
        });
        win.show();
    },
    toApply: function () {
        var map = new HashMap();
        map.put('method', 'toApply');
        Rpc({
            functionId: 'SYS20200520',
            async: true,
            success: function (form) {
                var returnStr = Ext.decode(form.responseText);

                numberRule.addNumberRule(returnStr);
            }
        }, map);
    },
    addNumberRule: function (result) {
        var regMobile = /^1[3456789]\d{9}$/;
        var regCount = /^([1-9][0-9]?|100)$/;

        var tableItems = [];
        var systemList = result.systemList;
        var len = systemList.length;
        if (len == 0) {

        }

        for (var i = 0; i < len; i++) {
            systemList[i].strName = "(" + systemList[i].systemCode + ")" + systemList[i].systemName;
        }

        var sysSelect = Ext.create('Ext.data.Store', {
            fields: ['systemCode', 'systemName', 'strName'],
            data: systemList
        });

        var systemName = '';
        var chooseSysCode = Ext.create('Ext.form.ComboBox', {
            margin: '20 0 5 5',
            store: sysSelect,
            repeatTriggerClick: true,
            labelSeparator: '',
            labelAlign: 'right',
            labelWidth: 140,
            height: 24,
            width: 400,
            emptyText: "请选择系统",
            fieldLabel: '系统简称（英文）',
            beforeLabelTextTpl: "<font color='red'> * </font>",
            displayField: 'strName',
            valueField: 'systemCode',
            id: 'systemCode',
            matchFieldWidth: false,
            editable: false,
            fieldStyle: 'height:20px;',
            listeners: {
                select: function (combo, records) {
                    systemName = records.data.systemName;
                }
            }
        });

        var applicant = Ext.widget('container', {
            margin: '20 0 5 5',
            layout: 'hbox',
            items: [{
                xtype: 'textfield',
                fieldLabel: '申请者',
                id: 'applicant',
                validator: function (value) {
                    if (value.length < 1)
                        return "申请者必填";
                    return true;
                },
                maxLength: 30,//允许最大长度
                labelAlign: 'right',
                beforeLabelTextTpl: "<font color='red'> * </font>",
                labelWidth: 140,
                fieldStyle: 'height:20px;width:250px;',
                value: result.currentUsrName
            }]
        });

        var mobile = Ext.widget('container', {
            margin: '20 0 5 5',
            layout: 'hbox',
            items: [{
                xtype: 'textfield',
                fieldLabel: '手机号',
                id: 'mobile',
                validator: function (value) {
                    if (value.length < 1)
                        return "手机号必填";

                    if (!regMobile.test(value))
                        return "手机号格式不正确";
                    return true;
                },
                maxLength: 32,//允许最大长度
                labelAlign: 'right',
                beforeLabelTextTpl: "<font color='red'> * </font>",
                labelWidth: 140,
                fieldStyle: 'height:20px;width:250px;',
                value: result.currentUsrMobile
            }]
        });

        var count = Ext.widget('container', {
            margin: '20 0 5 5',
            layout: 'hbox',
            items: [{
                xtype: 'textfield',
                fieldLabel: '编号个数[1~100]',
                id: 'count',
                validator: function (value) {
                    if (value.length < 1)
                        return "编号个数必填";

                    if (!regCount.test(value))
                        return "请输入1~100的整数";
                    return true;
                },
                maxLength: 4,//允许最大长度
                labelAlign: 'right',
                beforeLabelTextTpl: "<font color='red'> * </font>",
                labelWidth: 140,
                fieldStyle: 'height:20px;width:250px;',
            }]
        });

        var remark = Ext.widget('container', {
            margin: '20 0 5 5',
            layout: 'hbox',
            items: [{
                xtype: 'textareafield',
                fieldLabel: '申请说明',
                id: 'remark',
                maxLength: 200,//允许最大长度
                labelAlign: 'right',
                labelWidth: 140,
                fieldStyle: 'height:20px;width:330px;',
                height: 60,
                margin: '0 0 10 0'
            }]
        });

        tableItems.push(chooseSysCode);
        tableItems.push(applicant);
        tableItems.push(mobile);
        tableItems.push(count);
        tableItems.push(remark);

        var win = Ext.widget("window", {
            title: "申请编号",
            height: 410,
            width: 600,
            minButtonWidth: 40,
            layout: 'fit',
            bodyStyle: 'background:#ffffff;',
            modal: true,
            resizable: false,
            closeAction: 'destroy',
            items: [{xtype: 'container', width: 500, items: tableItems}],
            buttonAlign: 'center',
            buttons: [{
                text: "确定",
                formBind: true,
                handler: function () {
                    var applicant = Ext.util.Format.trim(Ext.getCmp('applicant').getValue());
                    var mobile = Ext.util.Format.trim(Ext.getCmp('mobile').getValue());
                    var count = Ext.util.Format.trim(Ext.getCmp('count').getValue());
                    var systemCode = Ext.getCmp('systemCode').getValue();

                    if (systemCode.length == 0) {
                        Ext.showAlert("系统简称必选");
                        return;
                    }

                    if (applicant.length == 0) {
                        Ext.showAlert("申请者必填");
                        return;
                    }
                    if (mobile.length == 0) {
                        Ext.showAlert("手机号必填");
                        return;
                    }

                    if (!regMobile.test(mobile)) {
                        Ext.showAlert("手机号格式不正确");
                        return;
                    }

                    if (count.length == 0) {
                        Ext.showAlert("编号个数必填");
                        return;
                    }

                    if (!regCount.test(count)) {
                        Ext.showAlert("请输入1~100的整数");
                        return;
                    }

                    var remark = Ext.getCmp('remark').getValue();


                    var map = new HashMap();
                    map.put('method', 'add');
                    map.put("applicant", applicant);
                    map.put("mobile", mobile);
                    map.put("count", count);
                    map.put("remark", remark);
                    map.put("systemName", systemName);
                    map.put("systemCode", systemCode);

                    Rpc({
                        functionId: 'SYS20200520',
                        async: true,
                        success: function (form) {
                            var returnStr = Ext.decode(form.responseText);
                            if (returnStr.result == true) {
                                tableObj.dataStore.reload();
                            } else {
                                Ext.showAlert("申请编号失败，" + returnStr.desc);
                            }
                            win.close();
                        }
                    }, map);
                }
            }, {
                text: "取消",
                handler: function () {
                    win.close();
                }
            }]
        });
        win.show();
    },
    deleteNumberRule: function () {
        var selectRecord = tableObj.tablePanel.getSelectionModel().getSelection(true);
        if (selectRecord.length < 1) {
            Ext.showAlert("请选择要删除的数据！");
            return;
        }
        Ext.Msg.confirm("提示信息", "是否确定删除？", function (btn) {
                if (btn == 'yes') {
                    var arr = new Array();
                    for (var i = 0; i < selectRecord.length; i++) {
                        arr[i] = selectRecord[i].data.id;
                    }
                    var map = new HashMap();
                    map.put('method', 'delete');
                    map.put("deleteIds", arr);
                    Rpc({
                        functionId: 'SYS20200520',
                        async: true,
                        success: function (form) {
                            tableObj.dataStore.reload();
                        }
                    }, map);
                }
            }
            , this);
    },
    numberDetailBtn: function (value, metaData, Record) {
        var numberlist = Record.data.numberlist;
        var remark = Record.data.remark;
        var html = "";
        var count = Record.data.count;
        if (count > 0) {
            html = "<a href=javascript:numberRule.viewNumberDetail('" + numberlist + "','" + remark + "');><img src='/images/view.gif'/>查看</a>";
        } else {
            html = "<a href=javascript:></a>";
        }
        return html;
    },
    downNumberDetailBtn: function (value, metaData, Record) {
        var id = Record.data.id;
        var count = Record.data.count;
        var html = "";
        if (count > 0) {
            html = "<a href=javascript:numberRule.downNumberDetail('" + id + "');><img src='/images/down.png'/>下载编号</a>";
        } else {
            html = "<a href=javascript:></a>";
        }
        return html;
    },
    downNumberDetail: function (id) {
        var map = new HashMap();
        map.put("id", id);
        map.put("method", 'downloadNumberList');
        Rpc({
            functionId: 'SYS20200520',
            async: true,
            success: function (result) {
                var returnStr = Ext.decode(result.responseText);
                if (returnStr.result == true) {
                    var fileName = returnStr.fileName;
                    window.location.target = "_blank";
                    window.location.href = "/servlet/vfsservlet?fromjavafolder=true&fileid=" + fileName;
                } else {
                    console.log("error:" + JSON.stringify(result));
                    Ext.showAlert("下载失败，请联系管理员");
                }
            }
        }, map);
    },
    viewNumberDetail: function (numberlist, remark) {
        var htmlText = "";
        if (numberlist == undefined || numberlist == "undefined")
            return "no data";

        var arr = numberlist.split(",");

        htmlText += "编号列表：<br/>";
        for (var i = 0; i < arr.length; i++) {
            if (i > 0 && (i % 10 == 0))
                htmlText += "<br/>";

            htmlText += arr[i] + ",";
        }
        //# 除去最后一个逗号，如果有
        htmlText = htmlText.substring(htmlText.length - 1 == ',') ? htmlText.substring(0, htmlText.length - 1) : htmlText;
        htmlText += "<p>&nbsp;</p>";

        htmlText += "<p>&nbsp;</p>"
        htmlText += "申请说明：<br/>";
        if (remark == undefined || remark == "undefined" || remark.length == 0) {

        } else {
            var tempArr = [];
            var len = remark.length;
            var num = 60;
            for (var j = 0; j < len / num; j++) {
                var str = remark.slice((num * j), (num * (j + 1)));
                tempArr.push(str);
            }
            var templen = tempArr.length;
            for (var x = 0; x < templen; x++) {
                htmlText += (tempArr[x] + "<br/>")
            }
            htmlText += "<br/>";
        }

        var myMsg = Ext.create('Ext.window.MessageBox', {alwaysOnTop: true}).show({
            title: '详情',
            message: Ext.isIE ? "<div style='margin:0px 0px 14px 0px;'>" + htmlText + "</div>" : htmlText,
            buttons: Ext.Msg.OK,
            icon: Ext.Msg.INFO,
            closeAction: 'destroy'
        });
    }
});
	