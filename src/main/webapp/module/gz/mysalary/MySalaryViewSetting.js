/**
 * 薪资视图配置界面 caoqy 2019-2-14 11:17:17
 */
Ext.define("mysalary.MySalaryViewSetting", {
    fieldsets: [],//年月变化子集
    nbases: [],//人员库数组
    isSalaryTabelRepeat: false,//视图表名是否重复
    requires: ['EHR.fielditemselector.FieldItemSelector'],
    constructor: function (config) {
        viewSetting_me = this;
        viewSetting_me.isEdit = false;//修改视图
        if(config.view){
        	viewSetting_me.isEdit = true;
        	viewSetting_me.name = config.name;
        	viewSetting_me.view = config.view;
        }
        
        viewSetting_me.wingroup = new Ext.WindowGroup();
        var map = new HashMap();
        var json = {type:'search',view:config.view};
        map.put("jsonStr", json);
        Rpc({
            functionId: 'GZ00000804',
            async: false,
            success: function (data) {
                var result = Ext.decode(data.responseText);
                if(result.return_code == 'fail'){
                	if(result.return_msg == 'gz.mysalary.scheme.error.getViewField'){
                		Ext.Msg.alert(gz.label.tips,gz.label.getViewFieldError.replace('{view}',config.view));
                	}else{
                		Ext.Msg.alert(gz.label.tips, result.return_msg);
                	}
                	viewSetting_me.isStop = true;
                	return;
                }
               
                viewSetting_me.fieldsets = result.return_data.fieldsets;
                viewSetting_me.nbases = result.return_data.nbases;
                viewSetting_me.mainsource = result.return_data.mainsource;
                viewSetting_me.mainsourceList = result.return_data.mainsourceList;
                viewSetting_me.fieldList = result.return_data.fieldList? result.return_data.fieldList:[];
                // 选中的人员库
                viewSetting_me.nbasesChecked = result.return_data.nbasesChecked ?result.return_data.nbasesChecked:[] ;

                viewSetting_me.judeArray=[];
                if(viewSetting_me.mainsourceList){
	                for(var i =0; i < viewSetting_me.mainsourceList.length; i++){
	                	viewSetting_me.judeArray.push(viewSetting_me.mainsourceList[i].fieldsetid);
	                }
                }
            }
        }, map);
        
        if(viewSetting_me.isStop){
        	return;
        }
        if (!viewSetting_me.nbases || viewSetting_me.nbases.length == 0){
        	if(viewSetting_me.isEdit){
        		Ext.Msg.alert(gz.label.tips, gz.label.viewNbasesUpdateError);
        	}else{
        		Ext.Msg.alert(gz.label.tips, gz.label.viewNbasesCreateError);
        	}
        	return;
        }
        if (viewSetting_me.fieldsets.length != 0 && viewSetting_me.nbases.length != 0) {
            this.init();
        } else {
        	if(viewSetting_me.isEdit){
        		Ext.Msg.alert(gz.label.tips, gz.label.viewSettingUpdateError);
        	}else{
        		Ext.Msg.alert(gz.label.tips, gz.label.viewSettingCreateError);
        	}
        }
    },
    init: function () {
        this.createWindow();
    },
    createWindow: function () {
        //主窗口
        var win = Ext.create('Ext.window.Window', {
            title: viewSetting_me.isEdit? gz.label.update + gz.label.view :gz.label.create + gz.label.view,
            height: 500,
            width: 560,
            id: 'addview_win',
            scrollable: 'y',
            // resizable: false,
            modal: true,
            layout: 'vbox',
            buttonAlign: 'center',
            buttons: [
                {
                    xtype: "button",
                    text: gz.button.ok,
                    scope: this,
                    width: 45,
                    handler: function () {
                        viewSetting_me.saveConfig();
                    }
                }, {
                    xtype: "button",
                    text: gz.button.no,
                    // flex:1,
                    width: 45,
                    scope: this,
                    handler: function () {
                        win.close();
                    }
                }
            ]
        });
        
        viewSetting_me.wingroup.register(win);
        //主数据来源combo的store
        var comboStore = Ext.create("Ext.data.Store", {
            storeId: 'mainsourceStore',
            fields: ['fieldsetdesc', 'fieldsetid'],
            data: viewSetting_me.mainsourceList? viewSetting_me.mainsourceList:[]
        });
        var view_name ="";
        if(viewSetting_me.view){
        	if(viewSetting_me.view.indexOf('V_EMP_') != -1){
        		view_name = viewSetting_me.view.substring(6);
        	}else if( viewSetting_me.view.indexOf('V_MY_GZ_') != -1){
        		view_name = viewSetting_me.view.substring(8);
        	}else{
        		view_name = viewSetting_me.view; 
        	}
        }
        //基本参数配置form
        var configform = Ext.create('Ext.form.Panel', {
            border: false,
            id: "view_configform",
            width: '100%',
            layout: "vbox",
            items: [/*{
                xtype: "container",
                html: '<p style="font-size: 16px;font-weight:bold">' + gz.label.basicParamSetting + '</p>',
                margin: '-12 0 0 10'
            },*/ {
                xtype: 'container',
                margin: '5 0 0 22',
                layout: 'hbox',
                items: [
                    {
                        xtype: "textfield",
                        fieldLabel: gz.label.viewName,//视图名称
                        name: 'salary_table_name',
                        labelAlign: 'right',
                        emptyText: gz.label.pleaseSetViewName.replace('！', "..."),
                        labelWidth: 70,
                        width: 220,
                        allowBlank: false,
                        readOnly:viewSetting_me.name? true:false,
                        value:viewSetting_me.name? viewSetting_me.name:'',
                        listeners:{
                        	blur: function (thistext, event, eOpts) {
                            	if(viewSetting_me.isEdit){
                            		return;
                            	}
                                var formPanel = Ext.getCmp("view_configform");
                                var values = formPanel.getValues();
                                var salary_table_name = values.salary_table_name;//视图名
                                var map = new HashMap();
                                map.put("salary_table_name", salary_table_name);
                                Rpc({
                                    functionId: 'GZ00000802',
                                    async: false,
                                    success: function (data) {
                                        var result = Ext.decode(data.responseText);
                                        if (result.return_code == 'success') {
                                            viewSetting_me.isSalaryNameRepeat = false;
                                        } else if (result.return_code == 'fail') {
                                            viewSetting_me.isSalaryNameRepeat = true;
                                            viewSetting_me.isSalaryNameRepeatMsg = result.return_msg+"！";
                                            Ext.Msg.alert(gz.label.tips, result.return_msg+"！");
                                        }
                                    }
                                }, map);
                            }
                        }
                        // beforeLabelTextTpl: "<font color='red'> * </font>"
                    },
                    /* {
                         xtype: 'component',
                         html: '<font color="red">*</font>',
                         margin: '3 0 0 3'
                     },*/
                    {
                        xtype: 'container',
                        layout: 'hbox',
                        items: [
                            {
                                xtype: "textfield",
                                // fieldLabel: gz.label.viewTable,//视图表
                                name: 'salary_table',
                                labelAlign: 'right',
                                margin: '0 0 0 10',
                                allowBlank: false,
                                maxLength: 8,
                                emptyText: gz.label.pleaseSetViewTable.replace('！', "..."),
                                // beforeLabelTextTpl: "<font color='red'> * </font>",
                                labelWidth: 70,
                                width: 150,
                                readOnly:viewSetting_me.view? true:false,
                                value:view_name,
                                listeners: {
                                    blur: function (thistext, event, eOpts) {
                                    	if(viewSetting_me.isEdit){
                                    		return;
                                    	}
                                        var viewTableName = "V_MY_GZ_"+thistext.value;
                                        if (!viewTableName || Ext.util.Format.trim(viewTableName) == '') {
                                            // Ext.Msg.alert(gz.label.tips, gz.label.viewErrorBydigitalNull);
                                            return;
                                        }
                                        var patt1 = /^[a-zA-Z]([_a-zA-Z0-9])*$/;
                                        if (!patt1.test(viewTableName)) {
                                            Ext.Msg.alert(gz.label.tips, gz.label.viewErrorBydigital);
                                            return;
                                        }
                                        var map = new HashMap();
                                        map.put("salary_table", viewTableName);
                                        Rpc({
                                            functionId: 'GZ00000802',
                                            async: false,
                                            success: function (data) {
                                                var result = Ext.decode(data.responseText);
                                                if (result.return_code == 'success') {
                                                    viewSetting_me.isSalaryTabelRepeat = false;
                                                } else if (result.return_code == 'fail') {
                                                    viewSetting_me.isSalaryTabelRepeat = true;
                                                    viewSetting_me.isSalaryTabelRepeatMsg = result.return_msg+"！";
                                                    Ext.Msg.alert(gz.label.tips, result.return_msg+"！");
                                                }
                                            }
                                        }, map);
                                    }
                                }
                            },
                            {
                                xtype: 'component',
                                html: '<font color="red">*</font>',
                                margin: '3 0 0 3'
                            }
                        ]
                    }
                ]
            }]
        });
        //人员库container
        var fieldcontainer = Ext.widget('fieldcontainer', {
            labelWidth: 66,
            fieldLabel: gz.label.daName,
            labelSeparator: '',
            margin: '11 0 0 15',
            width: '100%',
            itemId: 'fieldcontainer',
            labelAlign: 'right',
            // scrollable: 'y', // 解决Bug [62607] 暂不控制人员库区域出现滚动条
            // height: 65,
            height: '100%',// 解决Bug [62607] 控制人员库区域高度自适应
            // defaultType : 'checkboxfield',
            layout: 'vbox',
            items: [{
                xtype: 'container',
                layout: 'hbox',
                items: []
            }]
        });
        //动态生成人员库checkbox
        for (var i = 0; i < viewSetting_me.nbases.length; i++) {
            var tempNbaseObj = viewSetting_me.nbases[i];
            var tempNbase = tempNbaseObj.nbase;
            var preName = tempNbaseObj.dbname;
            // 控制人员库是否选中
            var checked = false;
            // 遍历选中的人员库，将选中的人员库设置为选中
            for (var j = 0; j < viewSetting_me.nbasesChecked.length; j++) {
                if (tempNbase == viewSetting_me.nbasesChecked[j]){
                    checked =true;
                }
            }
            for (var s = 0; s < fieldcontainer.items.items.length; s++) {
                if (fieldcontainer.items.items[s].items.items.length < 4) {
                    fieldcontainer.items.items[s].add(Ext.widget("checkboxfield", {
                        boxLabel: preName,
                        name: 'nbase',
                        width: 90,
                        inputValue: tempNbase,
                        margin: '0 0 0 4',
                        checked: checked
                    }));
                }
            }
            if ((i + 1) % 4 == 0) {
                fieldcontainer.add(Ext.create('Ext.container.Container', {
                    layout: 'hbox',
                    items: []
                }));
            }
            // fieldcontainer.add(Ext.widget("checkboxfield",{
            // 	boxLabel : preName,
            // 	name : 'nbase',
            // 	inputValue : tempNbase,
            // 	margin: '0 0 0 4'
            // }));
        }
        configform.add(fieldcontainer);
        //指标配置formPanel
        var indexform = Ext.create('Ext.form.FieldSet', {
            // border: false,
            // flex:13,
            title: gz.label.fieldSetting,
            width: '94%',
            margin: '0 0 1 30',
            padding: '5 10 5 10',
            layout: "vbox",
            items: [/*{
                xtype: "container",
                html: '<p style="font-size: 16px;font-weight:bold">' + gz.label.fieldSetting + '</p>',
                margin: '-10 0 0 10'
            }*/]
        });
        //添加按钮
        var addBtn = Ext.create('Ext.button.Button', {
            text: gz.label.add,
            scope: this,
            width: 40,
            handler: function () {
                var fieldsetarr = [];
                for (var i = 0; i < viewSetting_me.fieldsets.length; i++) {
                    fieldsetarr.push(viewSetting_me.fieldsets[i].fieldsetid);
                }
                var source = fieldsetarr.join("`");
                //过滤已选择的指标
                var grid = Ext.getCmp("viewset_indexGrid");
                var gridStore = grid.getStore();
                var filterItems = '';
                for (var i = 0; i < gridStore.getCount(); i++) {
                    filterItems += gridStore.getAt(i).data.itemid;
                    if (i < gridStore.getCount() - 1) {
                        filterItems += ","
                    }
                }
                var fieldItemSelecorWin = Ext.widget('window', {
                    header: false,
                    height: 400,
                    width: 300,
                    resizable: false,
                    layout: 'fit',
                    modal: true,
                    items: {
                        xtype: 'fielditemselecor',
                        source: source, // 子集拼接
                        filterTypes: 'A,D,M',// 过滤的指标类型
                        multiple: true,
                        title: gz.label.selectField,// 选择指标
                        okBtnText: gz.button.ok,
                        cancelBtnText: gz.button.no,
                        filterItems: filterItems,
                        searchEmptyText: gz.label.searchEmptyText,// '输入指标名称或指标代码查询...'
                        listeners: {
                            selectend: function (selectorCmp, fields) {// 确定按钮触发事件
                                viewSetting_me.addIndex(fields);
                                fieldItemSelecorWin.close();
                            },
                            cancel: function (selectorCmp) {// 取消按钮触发事件
                                fieldItemSelecorWin.close();
                            }
                        }
                    }
                });
                fieldItemSelecorWin.show();
            }
        });
        // 删除按钮
        var delBtn = Ext.create('Ext.button.Button', {
            text: gz.label.del,
            scope: this,
            width: 40,
            margin: '0 0 0 5',
            handler: function () {
                var grid = Ext.getCmp('viewset_indexGrid');
                var records = grid.getSelectionModel().getSelection();
                var mainsourceStore = Ext.StoreManager.lookup('mainsourceStore');
                var fieldArray = [];//主数据来源
                var judeArray = [];
                if (records.length < 1) {
                    Ext.Msg.alert(gz.label.tips, gz.label.selectOneField);
                }
                for (var i = 0, len = records.length; i < len; i++) {
                    grid.getStore().remove(records[i]);
                }
                for (var i = 0; i < grid.getStore().getCount(); i++) {
                    var fieldsetid = grid.getStore().getAt(i).data.fieldsetid;
                    var fieldsetdesc = grid.getStore().getAt(i).data.fieldsetdesc;
                    if (Ext.Array.indexOf(judeArray, fieldsetid) == -1) {
                        fieldArray.push({
                            id: fieldsetid,
                            fieldsetid: fieldsetid,
                            fieldsetdesc: fieldsetdesc
                        })
                    }
                    viewSetting_me.judeArray.push(fieldsetid);
                }
                var mainsourceValue = Ext.getCmp('mainsourceCombox').getValue();
                mainsourceStore.loadData(fieldArray, false);
                if (!mainsourceStore.getById(mainsourceValue)) {
                    Ext.getCmp('mainsourceCombox').reset();
                }
            }
        });
        //toolbar工具栏
        var toolbar = Ext.create('Ext.toolbar.Toolbar', {
            border: 0,
            items: [addBtn, delBtn]
        });
        //grid面板 的Store
        var indexStore = Ext.create('Ext.data.Store', {
            fields: ['fieldsetdesc', 'itemdesc', 'itemid', 'fieldsetid','itemtype','calcformat'],
            data: viewSetting_me.fieldList? viewSetting_me.fieldList:[]
        });
        //指标grid 
        viewSetting_me.fieldList.push({itemid:'operate'});
        var fieldStore = Ext.create("Ext.data.Store", {
            storeId: 'fieldStore',
            fields: ['fieldsetdesc', 'fieldsetid','itemid','itemdesc','itemtype'],
            data: viewSetting_me.fieldList? viewSetting_me.fieldList:[]
        });
        var indexGrid = Ext.create("Ext.grid.Panel", {
            store: indexStore,
            enableColumnResize: false,//改变列宽
            enableColumnMove: false,//拖放列
            stripeRows: false,//表格是否隔行换色
            columnLines: true,//列分割线
            id: "viewset_indexGrid",
            height: 257,
            width: '100%',
            // tbar: toolbar,
            // margin: "0 0 0 10",
            viewConfig: {
                markDirty: false //不显示编辑后的三角
            },
            store:fieldStore,
            plugins: [
                Ext.create('Ext.grid.plugin.CellEditing', {
                    clicksToEdit: 2
                })
            ],
            selModel: Ext.create("Ext.selection.CheckboxModel", {
                mode: "multi",//multi,simple,single；默认为多选multi
                checkOnly: true,//如果值为true，则只用点击checkbox列才能选中此条记录
                enableKeyNav: true,
                getHeaderConfig: function () {
                    var me = this,
                        showCheck = me.showHeaderCheckbox !== false;
                    return {
                        xtype: 'gridcolumn',
                        ignoreExport: true,
                        isCheckerHd: showCheck,
                        text: '&#160;',
                        clickTargetName: 'el',
                        width: me.headerWidth,
                        sortable: false,
                        draggable: false,
                        resizable: false,
                        hideable: false,
                        menuDisabled: true,
                        level: me.level,
                        dataIndex: '',
                        // tdCls: me.tdCls,//去除checkbox 选中样式
                        cls: Ext.baseCSSPrefix + 'column-header-checkbox ',
                        defaultRenderer: me.renderer.bind(me),
                        editRenderer: me.editRenderer || me.renderEmpty,
                        locked: me.hasLockedHeader(),
                        processEvent: me.processColumnEvent
                    };
                },
                renderer: function (value, metaData, record, rowIndex, colIndex, store, view) {
                    if (record.get("itemid") != 'operate')
                        return '<div class="' + Ext.baseCSSPrefix + 'grid-row-checker" role="presentation">&#160;</div>';
                }
            }),
            columns: [
                {
                    text: gz.label.fieldSetName,
                    dataIndex: 'fieldsetdesc',
                    sortable: false,
                    hideable: false,
                    align: 'left',
                    flex: 1,
                    renderer: function (value, metaData, record, rowIndex, colIndex, store, view) {
                        if (record.data.itemid == 'operate') {
                            metaData.tdStyle = 'border-width:0px !important'; //去除操作行的分割线
                        }
                        if (record.data.itemid === 'operate') {//操作行
                            return '<div style="overflow: hidden;height: 30px;padding-top:8px;"  ><span name="add_img" style="float: left">' + viewSetting_me.getImgHtml(rowIndex) + '</span></div>';
                        } else {
                            var html = '<div style="overflow: hidden;height: 30px;padding-top:8px" id="' + record.get('itemid') + '" onmouseover="viewSetting_me.tdMouseOver(\'' + record.get('itemid') + '\')" onmouseout="viewSetting_me.tdMouseout(\'' + record.get('itemid') + '\')"><div style="float: left">' + value + '</div>' +
                                '<span name="remove_img" style="display: none">';
                            if(record.data.itemtype === 'N'){
                                html += '<img src="/module/gz/mytax/images/org_del.png" onclick="viewSetting_me.removeField(\'' + record.get('itemid') + '\')" style="height: 16px;width: 16px;float:right;margin-right:5px;cursor:pointer;"/><img src="/module/gz/mytax/images/remove.png" onclick="viewSetting_me.removeSelectField()" style="height: 16px;width: 16px;float:right;margin-right:5px;cursor:pointer;"/>' +
                                    '<img src="/module/gz/mysalary/images/jsgs.png" onclick="viewSetting_me.editFieldItemFormula(\''
                                    + record.get('itemid')+ ':'+record.get('itemdesc') + '\')" style="height: 16px;width: 16px;float:right;margin-right:5px;cursor:pointer;"/></span></div>';
                            }else {
                                html += '<img src="/module/gz/mytax/images/org_del.png" onclick="viewSetting_me.removeField(\'' + record.get('itemid') + '\')" style="height: 16px;width: 16px;float:right;margin-right:5px;cursor:pointer;"/><img src="/module/gz/mytax/images/remove.png" onclick="viewSetting_me.removeSelectField()" style="height: 16px;width: 16px;float:right;margin-right:5px;cursor:pointer;"/></span></div>';
                            }
                            return html;
                        }
                    }
                },// 指标名称
                {
                    text: gz.label.FieldName,
                    dataIndex: 'itemdesc',
                    sortable: false,
                    hideable: false,
                    align: 'left',
                    flex: 1,
                    editor: {xtype: 'textfield', selectOnFocus: true},
                    renderer: function (value, metaData, record, rowIndex, colIndex, store, view) {
                        if (record.data.itemid == 'operate') {
                            metaData.tdStyle = 'border-width:0px !important'; //去除操作行的分割线
                        }
                        return value;
                    }
                }//薪资表
            ],
            listeners: {
                render: function () {
                    indexStore.add({fieldsetdesc: '', itemdesc: '', itemid: 'operate', fieldsetid: ''});
                }
            }

        });
        indexform.add(indexGrid);
        var mainsourceCombox = Ext.create('Ext.container.Container', {
            layout: 'hbox',
            items: [
                {
                    xtype: "combobox",
                    fieldLabel: gz.label.mainDataSource,
                    queryMode: 'local',
                    id: 'mainsourceCombox',
                    displayField: 'fieldsetdesc',
                    valueField: 'fieldsetid',
                    name: "fieldsetid",
                    labelAlign: 'right',
                    editable: false,
                    width: 390,
                    store: comboStore,
                    value:viewSetting_me.mainsource? viewSetting_me.mainsource:'',
                    emptyText: gz.label.select,
                    // beforeLabelTextTpl: "<font color='red'> * </font>",
                    margin: '5 0 0 5',
                    labelWidth: 50
                },
                {
                    xtype: 'component',
                    html: '<font color="red">*</font>',
                    margin: '10 0 0 3'
                }
            ]
        });
        indexform.add(mainsourceCombox);
        configform.add(indexform);
        win.add(configform);
        // win.add(indexform);
        win.show();
    },
    /**
     * 添加指标
     * @param {} fields
     */
    addIndex: function (fields) {
        var grid = Ext.getCmp("viewset_indexGrid");
        var gridStore = grid.getStore();
        var index = gridStore.getCount() - 1;
        var mainsourceStore = Ext.StoreManager.lookup('mainsourceStore');
        var dataArr = [];
        var fieldArray = [];//主数据来源
        //var judeArray = [];
        //循环添加数据并判断是否重复
        for (var i = 0; i < fields.length; i++) {
            var tempfieldsetid = fields[i].data.fieldsetid;
            var tempfieldsetdesc = fields[i].data.fieldsetdesc;
            var tempitemid = fields[i].data.id;
            var tempitemdesc = fields[i].data.itemdesc;
            var tempitemtype = fields[i].data.itemtype;
            var tempcalcformat = fields[i].data.itemdesc;
            //判断store中是否已有此数据
            for (var j = 0; j < gridStore.getCount(); j++) {
                var record = gridStore.getAt(j);
                if (tempitemid == record.get('itemid')) {
                    Ext.Msg.alert(gz.label.tips, gz.label.selectViewFieldExist.replace('{item1}',tempfieldsetdesc).replace('{item2}',tempitemdesc));
                    return;
                }
            }
            if (Ext.Array.indexOf(viewSetting_me.judeArray, tempfieldsetid) == -1) {
                fieldArray.push({
                    id: tempfieldsetid,
                    fieldsetid: tempfieldsetid,
                    fieldsetdesc: tempfieldsetdesc,
                });
            	viewSetting_me.judeArray.push(tempfieldsetid);
            }
            dataArr.push({
                id: tempitemid,
                fieldsetid: tempfieldsetid,
                fieldsetdesc: tempfieldsetdesc,
                itemid: tempitemid,
                itemdesc: tempitemdesc,
                itemtype: tempitemtype,
                calcformat: tempcalcformat
            });
        }
        //store添加数据
        // for (var k = 0; k < dataArr.length; k++) {
        //     gridStore.add(dataArr[k]);
        // }
        gridStore.insert(index, dataArr);
        mainsourceStore.loadData(fieldArray, true);

    },
    /**
     * 保存配置
     */
    saveConfig: function () {
        var grid = Ext.getCmp("viewset_indexGrid");
        var gridStore = grid.getStore();
        var formPanel = Ext.getCmp("view_configform");
        var values = formPanel.getValues();
        var salary_table_name = values.salary_table_name;//视图名
        salary_table_name = salary_table_name.replace(/^\s+|\s+$/g,"")
        //校验数据完整性
        if (values.salary_table_name == '') {
            Ext.Msg.alert(gz.label.tips, gz.label.pleaseSetViewName);
            return;
        }
        if (salary_table_name == '') {
            Ext.Msg.alert(gz.label.tips, gz.label.pleaseSetViewNameNotBlank);
            return;
        }
        if (values.salary_table == '') {
            Ext.Msg.alert(gz.label.tips, gz.label.pleaseSetViewTable);
            return;
        }
        if (!values.fieldsetid || values.fieldsetid == '' || values.fieldsetid == gz.label.select) {
            Ext.Msg.alert(gz.label.tips, gz.label.pleaseSetMainDataSource);
            return;
        }
        if (!values.hasOwnProperty("nbase")) {
            Ext.Msg.alert(gz.label.tips, gz.label.pleaseSetDBName);
            return;
        }
        if (gridStore.getCount() == 1) {//内置一条新增图标数据
            Ext.Msg.alert(gz.label.tips, gz.label.pleaseSetField);
            return;
        }
        if (viewSetting_me.isSalaryNameRepeat) {
            Ext.Msg.alert(gz.label.tips, viewSetting_me.isSalaryNameRepeatMsg);
            return;
        }
        if (viewSetting_me.isSalaryTabelRepeat) {
            Ext.Msg.alert(gz.label.tips, viewSetting_me.isSalaryTabelRepeatMsg);
            return;
        }
        if (values.salary_table) {
            if (values.salary_table.length > 8) {
                Ext.Msg.alert(gz.label.tips, gz.label.viewCodeMaxLength);
                return;
            }
        }
        //封装数据
        var patt1 = /^[a-zA-Z]([_a-zA-Z0-9])*$/;
        if (!patt1.test(values.salary_table)) {
            Ext.Msg.alert(gz.label.tips, gz.label.viewErrorBydigital);
            return;
        }
        var salary_table;
        if(viewSetting_me.isEdit){//修改视图
        	salary_table = viewSetting_me.view;
        }else{
        	salary_table = "V_MY_GZ_" + values.salary_table;//视图表名  默认加前缀V_MY_GZ_ 不与 人员视图前缀重复 
        }
        
        var fieldsetid = values.fieldsetid;//主数据来源
        if (typeof (values.nbase) == 'string') {
            var nbase = values.nbase
        } else {
            var nbase = values.nbase.join(",");//人员库
        }
        var fieldsets = [];
        var fielditems = [];
        for (var i = 0; i < gridStore.getCount(); i++) {
            var fielditemsMap = new HashMap();
            var record = gridStore.getAt(i);
            var tempfieldsetid = record.get('fieldsetid');
            var tempfieldsetdesc = record.get('fieldsetdesc');
            var tempitemid = record.get('itemid');
            var tempitemdesc = record.get('itemdesc');
            if (tempitemdesc == ''){
                Ext.Msg.alert(gz.label.tips, gz.label.itemDescIsEmpty);
                return;
            }
            var tempitemtype = record.get('itemtype');
            var tempcalcformat = record.get('calcformat');
            if (!tempcalcformat){
                tempcalcformat = '';
            }

            if(tempitemid == 'operate'){
                continue;
            }
            if (Ext.Array.indexOf(fieldsets, tempfieldsetid) == -1) {
                fieldsets.push(tempfieldsetid);
            }
            fielditemsMap.put('fieldsetid',tempfieldsetid);
            fielditemsMap.put('itemid',tempitemid);
            fielditemsMap.put('itemdesc',tempitemdesc);
            fielditemsMap.put('itemtype',tempitemtype);
            fielditemsMap.put('calcformat',tempcalcformat);
            fielditems.push(fielditemsMap);
        }
        // 判断是否选择了视图主表的日期型指标
        // 定义一个变量记录主表日期型个数
        var dateCount = 0;
        for (var i = 0; i < fielditems.length; i++) {
            // 判断是否是主表的日期型指标
            if (fielditems[i].get('itemtype') == 'D' && fieldsetid.toLowerCase() == fielditems[i].get('itemid').slice(0,3)){
                dateCount++;
            }
        }
        if (dateCount == 0){
            var A58Z0= new HashMap();
            A58Z0.put('fieldsetid','A58');
            A58Z0.put('itemid','a58z0');
            A58Z0.put('itemdesc','年月标识');
            A58Z0.put('itemtype','D');
            A58Z0.put('calcformat','');
            fielditems.push(A58Z0);
        }
        //后台交互
        var map = new HashMap();
        var json = {
            type: viewSetting_me.isEdit? 'update':'add',
            salary_table_name: salary_table_name,
            salary_table: salary_table,
            fieldsetid: fieldsetid,
            nbase: nbase,
            items: {
                fieldsets: fieldsets,
                fielditems: fielditems
            }
        };
        map.put("jsonStr", json);
        Rpc({
            functionId: 'GZ00000804',
            async: false,
            success: function (data) {
                var result = Ext.decode(data.responseText);
                if (result.return_code == 'success') {
                    var return_data = result.return_data;
                    var salaryScaleComboboxStore = Ext.StoreManager.lookup('salaryScaleComboboxStore');
                    var numberFieldStore = Ext.StoreManager.lookup('numberFieldStore');
                    var dateFieldStore = Ext.StoreManager.lookup('dateFieldStore');
                    var characterAndNumberFieldStore = Ext.StoreManager.lookup('characterAndNumberFieldStore');
                    // 根据storeId获取到含有空选项的数据
                    var numberFieldStoreAddEmpty = Ext.StoreManager.lookup('numberFieldStoreAddEmpty');
                    if (return_data.salary_table && return_data.salary_table_name && !viewSetting_me.isEdit) {
                        salaryScaleComboboxStore.add({
                            salary_table: return_data.salary_table,
                            salary_table_name: return_data.salary_table_name
                        });
                        // 给不包含应纳税所得额和个人所得税的下拉菜单设置数据为截掉空选项之后的数据
	                    numberFieldStore.loadData(return_data.items.N.slice(1,return_data.items.N.length), false);
                        numberFieldStoreAddEmpty.loadData(return_data.items.N);
	                    dateFieldStore.loadData(return_data.items.D, false);
                        characterAndNumberFieldStore.loadData(return_data.items.A, false);
	                    Ext.getCmp('displayWindow').query('#salaryScale')[0].setValue(return_data.salary_table);
	                    mySalarySetting.clearValue();
	                    // 设置应纳税所得额和个人所得税下拉菜单允许为空（为了防止全局变量冲突，不采用定义变量优化此代码）
                        Ext.getCmp('displayWindow').query('#taxableAmount')[0].allowBlank = true;
                        Ext.getCmp('displayWindow').query('#personalIncomeTax')[0].allowBlank = true;
                        Ext.getCmp('displayWindow').query('#payable')[0].allowBlank = true;
                        Ext.getCmp('displayWindow').query('#realWage')[0].allowBlank = true;
                        // 设置应纳税所得额和个人所得税下拉菜单默认值为none
                        Ext.getCmp('displayWindow').query('#taxableAmount')[0].setValue("none");
                        Ext.getCmp('displayWindow').query('#personalIncomeTax')[0].setValue("none");
                        Ext.getCmp('displayWindow').query('#payable')[0].setValue("none");
                        Ext.getCmp('displayWindow').query('#realWage')[0].setValue("none");
	                    Ext.getCmp('displayWindow').query('#salarySettingPanel')[0].getForm().checkValidity();//手动调用验证表单
                    } else {
                        viewSetting_me.cacheBasicData();
                    }
                    Ext.getCmp('addview_win').close();
                } else if (result.return_code == 'fail') {
                    Ext.Msg.alert(gz.label.tips, result.return_msg);
                }
            }
        }, map);
    },
    /**
     * 问题：创建薪酬方案是，点击编辑视图，将基本信息清空
     * 方法：将基本信息缓存再回显
     */
    cacheBasicData : function(){
        var cacheDisplayWindow = Ext.getCmp('displayWindow');
        // 先拿到方案名称
        var cacheProgramName = cacheDisplayWindow.query('#programName')[0].getValue();
        // 拿到选中的薪酬表
        var cacheSalaryScale = cacheDisplayWindow.query('#salaryScale')[0].getValue();
        var cacheSalaryScale1Combobox = cacheDisplayWindow.query('#salaryScale')[0];
        // 拿到时间维度指标
        var cacheTimeDimensionIndicator = cacheDisplayWindow.query('#timeDimensionIndicator')[0].getValue();
        // 拿到应发工资
        var cachePayable = cacheDisplayWindow.query('#payable')[0].getValue();
        // 拿到应纳税所得额
        var cacheTaxableAmount = cacheDisplayWindow.query('#taxableAmount')[0].getValue();
        // 拿到个人所得税
        var cachePersonalIncomeTax = cacheDisplayWindow.query('#personalIncomeTax')[0].getValue();
        // 拿到实发工资
        var cacheRealWage = cacheDisplayWindow.query('#realWage')[0].getValue();
        // 拿到所属单位
        var cacheOrganization = cacheDisplayWindow.query('#organization')[0].getValue();
        // 拿到可见范围
        var cacheRoleId = cacheDisplayWindow.query('#role')[0].getValue();
        // 拿到角色id数组
        if (cacheRoleId == ""){
            cacheRoleId = [];
        }else {
            cacheRoleId = cacheRoleId.split(",");
        }
        var cacheRoleName = [];
        // 拿到角色名称数组
        for (var i=0; i< cacheRoleId.length;i++) {
            var roleId = '#'+cacheRoleId[i];
            cacheRoleName[i] = cacheDisplayWindow.query(roleId)[0].config.items[0].items[1].html;
        }
        // 拿到为零项不显示
        var cacheZeroItemCtrl = cacheDisplayWindow.query('#zeroItemCtrl')[0].checked;
        // 拿到只显示某年以后的薪酬
        var cacheYearCombox = cacheDisplayWindow.query('#yearCombox')[0].getValue();
        // }
        // 关掉窗口
        cacheDisplayWindow.close();
        // 重新加载页面
        mySalarySetting.init();
        // 数据回显
        var displayWindow = Ext.getCmp('displayWindow');
        displayWindow.query('#programName')[0].setValue(cacheProgramName);
        displayWindow.query('#salaryScale')[0].setValue(cacheSalaryScale);
        // 回显并触发薪酬表的选中事件
        displayWindow.query('#salaryScale')[0].fireEvent('select', cacheSalaryScale1Combobox, cacheSalaryScale1Combobox.selection);
        displayWindow.query('#salaryScale')[0].select(cacheSalaryScale);
        displayWindow.query('#timeDimensionIndicator')[0].setValue(cacheTimeDimensionIndicator);
        displayWindow.query('#payable')[0].setValue(cachePayable);
        displayWindow.query('#taxableAmount')[0].setValue(cacheTaxableAmount);
        displayWindow.query('#personalIncomeTax')[0].setValue(cachePersonalIncomeTax);
        displayWindow.query('#realWage')[0].setValue(cacheRealWage);
        displayWindow.query('#organization')[0].setValue(cacheOrganization);
        // 遍历添加角色
        if (mySalarySetting.viewType !== 'create') {
            // 如果不是创建流程那么先将roleContainer回显的数据清空再添加
            displayWindow.query('#roleContainer')[0].removeAll();
        }
        for (var i = 0; i < cacheRoleName.length; i++) {
            displayWindow.query('#roleContainer')[0].add(mySalarySetting.createRolePanel(cacheRoleName[i], cacheRoleId[i]));
        }
        displayWindow.query('#role')[0].setValue(cacheRoleId);
        displayWindow.query('#zeroItemCtrl')[0].setValue(cacheZeroItemCtrl);
        displayWindow.query('#yearCombox')[0].setValue(cacheYearCombox);
    },
    /**
     * 获取新增图片的html代码
     */
    getImgHtml: function (rowIndex) {
        return '<img name=' + rowIndex + ' src="/module/gz/mysalary/images/org_add.png" onclick="viewSetting_me.addField(' + rowIndex + ')" style="height: 16px;width: 16px;float:right;margin-right:5px;cursor:pointer;">'
    },
    /**
     * 添加视图指标
     */
    addField: function (rowIndex) {
        var fieldsetarr = [];
        for (var i = 0; i < viewSetting_me.fieldsets.length; i++) {
            fieldsetarr.push(viewSetting_me.fieldsets[i].fieldsetid);
        }
        var source = fieldsetarr.join("`");
        //过滤已选择的指标
        var grid = Ext.getCmp("viewset_indexGrid");
        var gridStore = grid.getStore();
        var filterItems = '';
        for (var i = 0; i < gridStore.getCount(); i++) {
            filterItems += gridStore.getAt(i).data.itemid.toLowerCase() ;
            if (i < gridStore.getCount() - 1) {
                filterItems += ","
            }
        }
        var fieldItemSelecorWin = Ext.widget('window', {
            header: false,
            height: 400,
            width: 300,
            resizable: false,
            layout: 'fit',
            modal: true,
            items: {
                xtype: 'fielditemselecor',
                source: source, // 子集拼接
                // filterTypes: 'A,D,M',// 过滤的指标类型
                filterTypes: 'M',// 过滤的指标类型
                multiple: true,
                title: gz.label.selectField,// 选择指标
                okBtnText: gz.button.ok,
                cancelBtnText: gz.button.no,
                filterItems: filterItems,
                searchEmptyText: gz.label.searchEmptyText,// '输入指标名称或指标代码查询...'
                listeners: {
                    selectend: function (selectorCmp, fields) {// 确定按钮触发事件
                        viewSetting_me.addIndex(fields);
                        fieldItemSelecorWin.close();
                    },
                    cancel: function (selectorCmp) {// 取消按钮触发事件
                        fieldItemSelecorWin.close();
                    }
                }
            }
        });
        fieldItemSelecorWin.show();
    },
    /**
     * 视图指标行悬浮处理函数
     * @param id
     */
    tdMouseOver: function (id) {
        Ext.getDom(id).children[1].style.display = '';
    },
    tdMouseout: function (id) {
        Ext.getDom(id).children[1].style.display = 'none';
    },
    /**
     * 删除视图指标
     * @param itemid
     */
    removeField: function (itemid) {
        var grid = Ext.getCmp('viewset_indexGrid');
        var record = grid.getStore().getById(itemid);
        grid.getStore().remove(record);
        viewSetting_me.operateMainSourceStore();
    },
    /**
     * 根据视图指标的子集动态更新视图主表的store数据
     */
    operateMainSourceStore: function () {
        var grid = Ext.getCmp('viewset_indexGrid');
        var fieldArray = [];//主数据来源
        var judeArray = [];
        viewSetting_me.judeArray = [];
        for (var i = 0; i < grid.getStore().getCount(); i++) {
            var fieldsetid = grid.getStore().getAt(i).data.fieldsetid;
            var fieldsetdesc = grid.getStore().getAt(i).data.fieldsetdesc;
            if (Ext.Array.indexOf(judeArray, fieldsetid) == -1) {
                fieldArray.push({
                    id: fieldsetid,
                    fieldsetid: fieldsetid,
                    fieldsetdesc: fieldsetdesc
                })
            	viewSetting_me.judeArray.push(fieldsetid);
            }
        }
        var mainsourceStore = Ext.StoreManager.lookup('mainsourceStore');
        var mainsourceValue = Ext.getCmp('mainsourceCombox').getValue();
        mainsourceStore.loadData(fieldArray, false);
        if (!mainsourceStore.getById(mainsourceValue)) {
            Ext.getCmp('mainsourceCombox').reset();
            Ext.getCmp('mainsourceCombox').setValue('');
        }
    },
    /**
     * 批量删除视图指标函数
     */
    removeSelectField: function () {
        var grid = Ext.getCmp('viewset_indexGrid');
        var records = grid.getSelectionModel().getSelection();
        if (records.length == 1) {
            if (records[0].get('itemid') == 'operate') {
                var alert = Ext.Msg.alert(gz.label.tips, gz.label.selectOneField);
                viewSetting_me.wingroup.register(alert);
                viewSetting_me.wingroup.bringToFront(alert);
            }
        }
        if (records.length < 1) {
            var alert = Ext.Msg.alert(gz.label.tips, gz.label.selectOneField);
            viewSetting_me.wingroup.register(alert);
            viewSetting_me.wingroup.bringToFront(alert);

        }
        for (var i = 0, len = records.length; i < len; i++) {
            if (records[i].get('itemid') != 'operate') {
                grid.getStore().remove(records[i]);
            } else {
                grid.getSelectionModel().deselect(records[i]);
            }
        }
        viewSetting_me.operateMainSourceStore();
    },
    /**
     * 编辑指标的计算公式
     */
    editFieldItemFormula:function (record) {
        var arr = record.split(':');
        // 取得当前指标所在子集的数值型指标
        var map = new HashMap();
        var json = {
            type: 'searchNumberFieldItem',
            fieldsetid: arr[0].slice(0,3),

        };
        // 计算公式项目数据
        var numberFieldItem = null;
        map.put("jsonStr", json);
        Rpc({
                functionId: 'GZ00000804',
                async: false,
                success: function (data) {
                   var data = Ext.decode(data.responseText);
                   numberFieldItem = data.return_data.numberFieldItem;
                }
            }
            , map
        );
        // 找出itemid对应的计算公式
        var grid = Ext.getCmp("viewset_indexGrid");
        var gridStore = grid.getStore();
        // 将计算公式添加给store
        for (var i = 0; i < gridStore.getCount(); i++) {
            if (arr[0] == gridStore.getAt(i).data.itemid.toLowerCase()){
                var calcFormat =  gridStore.getAt(i).get('calcformat');
                if (!calcFormat){
                    // 如果计算公式为空，则默认显示，未修改之前的指标名称
                    for (var j = 0; j < numberFieldItem.length; j++) {
                        var split = numberFieldItem[i].name.split(':');
                        if (split[0] == arr[0].toUpperCase()){
                            calcFormat = split[1];
                            break;
                        }
                    }
                }
            }
        }
        Ext.create("mysalary.MySalaryFormula", {
            itemdata: numberFieldItem,
            itemType: "N",
            calcFormat: calcFormat,
            fieldSetId:arr[0].slice(0,3),
            itemId:arr[0],
            callbackfunc: viewSetting_me.saveFieldItemFormula
        });
    },
    saveFieldItemFormula: function (c_expr,itemId) {
        // 将计算公式保存
        var grid = Ext.getCmp("viewset_indexGrid");
        var gridStore = grid.getStore();
        // 将计算公式添加给store
        for (var i = 0; i < gridStore.getCount(); i++) {
            if (itemId == gridStore.getAt(i).data.itemid.toLowerCase()){
                gridStore.getAt(i).set('calcformat',c_expr);
            }
        }
    }
});