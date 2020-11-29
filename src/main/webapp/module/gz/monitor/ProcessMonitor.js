/**
 * 薪资应用机构-弹出框
 * sunjian 2018-8-10
 */

Ext.define('Salarybase.monitor.ProcessMonitor', {
    constructor: function (config) {
        salarymonitor_me = this;
        var map = new HashMap();
        map.put("url", config.url);
        Rpc({functionId: 'GZ00000237', async: false, success: salarymonitor_me.getTableOK}, map);
    },
    getTableOK: function (form, action) {
        var result = Ext.decode(form.responseText);

        salarymonitor_me.salaryid = result.curr_item;
        salarymonitor_me.imodule = result.imodule;
        salarymonitor_me.curr_item = result.curr_item;//当前的类别
        salarymonitor_me.curr_date = result.curr_date;//当前业务日期
        salarymonitor_me.curr_stateOfWrite = result.curr_stateOfWrite;//当前填报状态
        salarymonitor_me.enableModes = result.enableModes;//发送的方式，短信，邮箱等
        //记录下所有的数据，在切换状态的时候可以直接拿这个数据，不用再查数据库了，TableDataConfigCache由于每次都把数据塞到缓存中，如果从全部切到驳回，没问题，再从驳回切换到已报批就有问题了
        salarymonitor_me.dataListAll = result.dataListAll;
        var itemList = result.itemList;
        var DateList = result.DateList;
        salarymonitor_me.stateOfWriteList = result.stateOfWriteList;
        var conditions = result.tableConfig;
        var obj = Ext.decode(conditions);
        salarymonitor_me.monitorObj = new BuildTableObj(obj);
        salarymonitor_me.monitorObj.setSchemeViewConfig({//配置栏目设置参数
            publicPlan: result.isShowPublicPlan == '1' ? true : false,
            sum: false,
            lock: true,
            merge: false,
            pageSize: '20'
        });
        
        salarymonitor_me.itemlistStore = Ext.create('Ext.data.Store', {//类别名称
            fields: ['name', 'id'],
            data: itemList
        });

        salarymonitor_me.dateStore = Ext.create('Ext.data.Store', {//业务日期
            fields: ['name', 'id'],
            data: DateList
        });

        salarymonitor_me.stateOfWriteStore = Ext.create('Ext.data.Store', {//填报状态
            fields: ['name', 'id'],
            data: salarymonitor_me.stateOfWriteList
        });

        salarymonitor_me.toolbar = Ext.create('Ext.toolbar.Toolbar', {
            border: 0,
            id: "toolbar",
            dock: 'top',
            height: 35,
            items: [{
                xtype: 'combobox',
                id: 'itemlist',
                labelSeparator: '：',
                labelWidth: 60,
                width: 240,
                store: salarymonitor_me.itemlistStore,
                matchFieldWidth:false,//长度超出了自适应
                displayField: 'name',
                valueField: 'id',
                editable: true,
                queryMode: 'local',
                fieldLabel: gz.label.typeName,//类别名称
                labelAlign: 'left',
                value: salarymonitor_me.curr_item,
                listeners: {
                    select: function (combo, records) {
                    	var map = new HashMap();
				    	map.put("salaryid",combo.value);
				    	map.put("a00z2",salarymonitor_me.curr_date);
				    	map.put("imodule",salarymonitor_me.imodule);
				    	map.put("enter", "1");
				    	salarymonitor_me.monitorObj.getMainPanel().destroy();
					    Rpc({functionId:'GZ00000237',async:false,success:salarymonitor_me.getTableOK},map);
                    }
                }
            }, {
                xtype: 'combo',
                id: 'date',
                labelSeparator: '：',
                labelWidth: 60,
                width: 240,
                store: salarymonitor_me.dateStore,
                displayField: 'name',
                valueField: 'id',
                editable: false,
                queryMode: 'local',
                fieldLabel: gz.label.date,//业务日期
                labelAlign: 'left',
                style: 'padding-left:20px;',
                value: salarymonitor_me.curr_date,
                listeners: {
                    select: function (combo, records) {
                    	var map = new HashMap();
				    	map.put("salaryid",salarymonitor_me.curr_item);
				    	map.put("a00z2",combo.value);
				    	map.put("imodule",salarymonitor_me.imodule);
				    	salarymonitor_me.monitorObj.getMainPanel().destroy();
					    Rpc({functionId:'GZ00000237',async:false,success:salarymonitor_me.getTableOK},map);

                    }
                }
            }, {
                xtype: 'combo',
                id: 'stateOfWrite',
                labelSeparator: '：',
                labelWidth: 60,
                width: 150,
                store: salarymonitor_me.stateOfWriteStore,
                displayField: 'name',
                valueField: 'id',
                editable: false,
                queryMode: 'local',
                fieldLabel: gz.label.stateOfWrite,//填报状态
                labelAlign: 'left',
                style: 'padding-left:20px;',
                value: salarymonitor_me.curr_stateOfWrite,
                listeners: {
                    select: function (combo, records) {
                    	var map = new HashMap();
				    	map.put("salaryid",salarymonitor_me.curr_item);
				    	map.put("a00z2",salarymonitor_me.curr_date);
				    	map.put("curr_stateOfWrite",combo.value);
				    	map.put("imodule",salarymonitor_me.imodule);
				    	map.put("stateOfWriteLists",salarymonitor_me.stateOfWriteList);
				    	map.put("dataListAll",salarymonitor_me.dataListAll);
				    	salarymonitor_me.monitorObj.getMainPanel().destroy();
				    	Rpc({functionId:'GZ00000237',async:false,success:salarymonitor_me.getTableOK},map);

                    }
                }
            }]
        });
        salarymonitor_me.monitorObj.insertItem(salarymonitor_me.toolbar, 0);
    },

    operate: function (value, metaData, record) {
        var fullname = arguments[2].data.fullname;
        var username = arguments[2].data.username;
        var sp_flag = arguments[2].data.sp_flag;
        var curr_user = arguments[2].data.curr_user;
        var curr_user_fullname = arguments[2].data.curr_user_fullname;
        var sp_flag_code = arguments[2].data.sp_flag_code;
        if(sp_flag_code == "03" || sp_flag_code == "06" || sp_flag_code == "") {//已批和结束的不显示提醒
        	return "";
        }
        if(sp_flag_code == "02") {//如果是已报批的，则发给审批人
        	fullname = curr_user_fullname;
        	username = curr_user;
        }
        var html = "<div style='text-align:center;' ><img style='margin: 0 auto;cursor:pointer;width:16px;height:16px;' src='/images/warn.png' onclick='salarymonitor_me.sendMessage(\"" + fullname + "\",\"" + username + "\",\"" + sp_flag_code + "\")'/></div>";
        return html;
    },

    sendMessage: function (fullname,username,sp_flag_code) {
        Ext.require('Salarybase.monitor.SendMessageMonitor', function () {
            Ext.create("Salarybase.monitor.SendMessageMonitor", {
                salaryid: salarymonitor_me.salaryid,
                fullname: fullname,
                username: username,
                sp_flag_code: sp_flag_code,
                imodule: salarymonitor_me.imodule,
                enableModes: salarymonitor_me.enableModes
            });
        });
    },

    loadStore: function () {
        var store = salarymonitor_me.monitorObj.tablePanel.getStore();
        store.load();
    },

    schemeSetting_callBack: function () {
        salarymonitor_me.monitorObj.getMainPanel().destroy();
        var map = new HashMap();
        map.put("salaryid", salarymonitor_me.curr_item);
        map.put("a00z2", salarymonitor_me.curr_date);
        map.put("curr_stateOfWrite", salarymonitor_me.curr_stateOfWrite);
        map.put("imodule", salarymonitor_me.imodule);
        map.put("stateOfWriteLists",salarymonitor_me.stateOfWriteList);
        Rpc({functionId: 'GZ00000237', async: false, success: salarymonitor_me.getTableOK}, map);
    }
})