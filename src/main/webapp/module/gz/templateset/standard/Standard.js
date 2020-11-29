Ext.define('Standard.Standard', {
    extend: 'Ext.panel.Panel',
    requires: ['Standard.TabularView', 'SYSF.FileUpLoad'],
    layout: 'vbox',
    editType:'',//当前页面操作类型 create 代表是新建  edit 表示编辑
    pkg_id: '',//标准表历史沿革账套id
    stand_id: '',//标准表id
    border: false,
    standStructInfor: undefined,
    isHaveEdit: false,
    viewType: '',//edit 编辑  create 新建
    initComponent: function () {
        Standard = this;
        this.callParent();
        Standard.editType = Standard.viewType;
        //回车 相当于table键
        Ext.override(Ext.event.Event, {
            getKey: function () {
                var grid = Ext.ComponentQuery.query("#stanardGridPanel")[0];
                if (grid && this.getKeyName() == 'ENTER' && grid.owns(this.target))
                    return this.TAB;
                return this.keyCode || this.charCode;
            }
        });
        Standard.initData(Standard.viewType, Standard.standStructInfor);

    },
    /**
     * 加载数据
     */
    initData: function (operateType, standStructInfor) {
        var vo = new HashMap();
        vo.put("initType", operateType);
        vo.put("pkg_id", this.pkg_id);
        vo.put("stand_id", this.stand_id);
        vo.put("paramsInfor", standStructInfor);
        Rpc({functionId: 'GZ00001214', success: this.rendData, scope: this, async: false}, vo);
    },
    rendData: function (response) {
        var resData = Ext.decode(response.responseText);
        var returnCode = resData.returnCode;
        var returnMsgCode = resData.returnMsgCode;
        if (returnCode == "success") {
            var standData = resData.returnData.standData;
            var stanardName = standData.stanardName;
            Standard.isHaveEdit = standData.isHaveEdit;
            this.add(this.getTitleCmp(stanardName));
            this.add(this.getButtons(Standard.isHaveEdit));
            this.createGridPanel(standData);
        } else {
            var msg = "";
            if (returnMsgCode == 'getStandStructInforError') { //获取标准表结构出错
                msg = gz.standard.getStandStructInforError;
            } else if (returnMsgCode == 'getStandTableItemDataError') {//获取标准表单元格数据出错
                msg = gz.standard.getStandTableItemDataError;
            } else if (returnMsgCode == 'verifFieldItemError'){
                msg = resData.returnData.returnMsgCode
            } else {
                msg = gz.standard.getStandDataError;
            }
            Ext.Msg.alert(gz.label.tips, msg);
            StandardList.gridPanel.getStore().reload();
            Ext.getCmp('StandardList_mainPanel').setHidden(false);
        }
    },
    getTitleCmp: function (stanardName, isHaveEdit) {
        return Ext.create('Ext.panel.Panel', {
            title: stanardName,
            width: '100%',
            height: 39,
            border: false
        })
    },
    /**
     * 增加列编辑器
     * @param columns
     * @param resultFieldType
     */
    addColumnsEditor: function (columns, resultFieldData) {
        var resultFieldType = resultFieldData.resultFieldType;
        var resultFieldCodesetid = resultFieldData.resultFieldCodesetid;
        var editor = {};
        var formatpattern = "00000000000000000000000000000";
        var displayFormat = "0,000";
        if (resultFieldData.resultFieldDecimalwidth > 0) {
            displayFormat += "." + formatpattern.substr(0, resultFieldData.resultFieldDecimalwidth);
        }
        var textFieldEdit = {
            xtype: 'textfield',
            selectOnFocus: true,
            listeners: {
                change: function (t) {
                    // var record = t.getWidgetRecord();
                    // record.set(t.dataIndex, t.getValue());
                }
            }
        };
        var numberEditor = {
            xtype: 'numberfield',
            hideTrigger: true,
            selectOnFocus: true,
            allowDecimals: resultFieldData.resultFieldDecimalwidth > 0,
            decimalPrecision: resultFieldData.resultFieldDecimalwidth
        };
        var codeFieldEdit = {
            xtype: "codecomboxfield", codesetid: resultFieldCodesetid, onlySelectCodeset: true,
            ctrltype: "0", nmodule: "", multiple: false, selectOnFocus: true,
            listeners: {
                select: function (t, selectRecord) {
                    // var record = t.getWidgetRecord();
                    // record.set(t.dataIndex,t.getValue());
                }
            }
        };
        //结果指标为代码型
        if (resultFieldType == 'code') {
            editor = codeFieldEdit;
        } else if (resultFieldType == 'text') {//字符型
            editor = textFieldEdit;
        } else if (resultFieldType == 'number') {//数值型
            editor = numberEditor;
        }
        for (var i = 0; i < columns.length; i++) {
            var column = columns[i];
            if (column.dataIndex == 'vfactor_desc' || column.dataIndex == 's_vfactor_desc' || column.dataIndex == 'result_desc') {
                continue;
            }
            if (column.columns) {
                for (var j = 0; j < column.columns.length; j++) {
                    var childColumns = column.columns[j];
                    // childColumns.xtype = 'widgetcolumn';
                    // childColumns.widget = editor;
                    childColumns.editor = editor;
                    if (resultFieldType == 'code') {
                        childColumns.defaultRenderer = function (value) {
                            if (value) {
                                return value.split('`')[1];
                            }
                        }
                    } else {
                        childColumns.align = 'right';
                        childColumns.format = displayFormat;
                    }
                }
            } else {
                // columns[i].xtype = 'widgetcolumn';
                // columns[i].widget = editor;
                columns[i].editor = editor;
                if (resultFieldType == 'code') {
                    columns[i].defaultRenderer = function (value) {
                        if (value) {
                            return value.split('`')[1];
                        }
                    }
                } else {
                    columns[i].align = 'right';
                    columns[i].format = displayFormat;
                }
            }
        }
    },
    /**
     * 创建表格
     */
    createGridPanel: function (standData) {
    	var stanard = this;
        var columns = standData.columns;
        var storeData = standData.storeData;
        var resultFieldData = standData.resultFieldData;
        var mergeColumn = standData.mergeColumn;
        var viewConfig = {};
        if (mergeColumn) {
            viewConfig.mergeColumns = mergeColumn;
        }
        if (Standard.isHaveEdit == 1 || Standard.editType  == 'create') {
            this.addColumnsEditor(columns, resultFieldData);
        }
        var store = Ext.create('Ext.data.Store', {
            storeId: 'stanardStore',
            data: storeData
        });
        var girdPanel = Ext.create('Ext.grid.Panel', {
            store: store,
            width: '100%',
            bodyStyle: 'z-index:2',
            flex: 1,
            itemId: 'stanardGridPanel',
            rowLines: true,
            columnLines: true,
            disableSelection: true,
            // viewType: 'tabularview',
            lockedGridConfig:{
                viewType: 'tabularview'
            },
            normalGridConfig:{
                viewType: 'tabularview'
            },
            viewConfig: viewConfig,
            enableLocking: true,
            plugins: {
                ptype: 'cellediting', clicksToEdit: 1
            },
            columns: columns,
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
                                    //div 里面内容不允许自动换行，滚动条隐藏 27250 27734  wangb 2017-4-24 and 又换回最初状态  允许换行
                                    tip.update("<div>" + div.innerHTML + "<br/></div>");
                                } else
                                    return false;
                            }
                        }
                    });
                }
            }
        });
        this.add(girdPanel);
    },
    /**
     * 获取功能按钮
     */
    getButtons: function (isHaveEdit) {
        var stanard = this;
        var exportButton = {
            text: gz.label.zxdeclare.exportText,
            margin: '0 5 0 0',
            height: 22,
            handler: function () {
                if(stanard.stand_id.length<1){
                    Ext.Msg.alert(gz.standard.tip,gz.standard.exportFailDesc);
                    return;
                }
                var vo = new HashMap();
                vo.put('pkg_id', stanard.pkg_id);
                vo.put('stand_ids', stanard.stand_id);
                Rpc({
                    functionId: 'GZ00001216', success: function (form) {
                        var result = Ext.decode(form.responseText);
                        var return_code = result.return_code;
                        if (return_code == "fail") {
                            Ext.Msg.alert(gz.standard.tip, result.return_msg);
                            return;
                        }
                        var return_data = result.return_data;
                        var file_Name = return_data.file_Name;
                        window.open("/servlet/vfsservlet?fileid=" + file_Name + "&fromjavafolder=true");
                    }, async: false
                }, vo);
            }
        };
        var backButton = {
            text: OptAnalysisTable.back,
            margin: '0 5 0 0',
            height: 22,
            handler: function () {
                StandardList.remove(Standard);
                StandardList.gridPanel.getStore().reload();
                Ext.getCmp('StandardList_mainPanel').setHidden(false);
            }
        };
        var revisionStructureButton = {
            text: gz.standard.revisionStructure,
            margin: '0 5 0 0',
            height: 22,
            handler: function () {
                var operateType = 'init';
                if (Standard.standStructInfor) {
                    operateType = 'transform';
                }
                Ext.create('Standard.StandardStructure', {
                    afterEnterFunc: Standard.refreshPage,
                    viewType: 'struct',
                    operateType: operateType,
                    pkg_id: Standard.pkg_id,
                    stand_id: Standard.stand_id,
                    standStructInfor: Standard.standStructInfor
                });
            }
        };
        var saveButton = {
            text: gz.button.save,
            margin: '0 5 0 0',
            height: 22,
            handler: function () {
                var vo = new HashMap();
                vo.put("data", stanard.parseStoreData());
                vo.put('pkg_id', stanard.pkg_id);
                vo.put('stanard_id', stanard.stand_id);
                vo.put('standInfor', stanard.standStructInfor);
                vo.put('saveType', stanard.viewType);
                Rpc({
                    functionId: 'GZ00001215', success: function (response) {
                        var resData = Ext.decode(response.responseText);
                        var returnCode = resData.returnCode;
                        if (returnCode == "success") {
                            Ext.Msg.alert(gz.label.tips, gz.label.saveSuccess, function () {
                                Ext.StoreManager.lookup('stanardStore').commitChanges();
                                stanard.stand_id = resData.stanard_id;
                                stanard.viewType = 'edit';
                                stanard.isHaveEdit = true;
                                stanard.remove(Ext.ComponentQuery.query("#buttontoolbar")[0]);
                                stanard.insert(1, stanard.getButtons(1));
                            });
                        } else {
                            Ext.Msg.alert(gz.label.tips, gz.standard.saveFail);
                        }
                    }, async: false
                }, vo);
            }
        };
        var buttons = Ext.create('Ext.toolbar.Toolbar', {
            width: '100%',
            height: 30,
            border: false,
            margin: '0 0 3 0',
            itemId: 'buttontoolbar',
            items: [revisionStructureButton
                , saveButton
                , exportButton, {
                    text: gz.standard.importText,
                    margin: '0 5 0 0',
                    height: 22,
                    handler: function () {
                        stanard.importStandardDataExcel();
                    }
                }, backButton
            ]
        });
        if (stanard.viewType == 'edit') {
            if (isHaveEdit == 0) {
                buttons.removeAll(true);
                buttons.add(exportButton);
                buttons.add(backButton);
            } 
        }
        if (stanard.viewType == 'struct') {
            if (isHaveEdit == 0) {
                buttons.removeAll(true);
                buttons.add(revisionStructureButton);
                buttons.add(saveButton);
            }
        }
        if (Standard.viewType == 'create') {
            buttons.removeAll(true);
            buttons.add(revisionStructureButton);
            buttons.add(saveButton);
        }
        return buttons;
    },
    refreshPage: function (standStructInfor) {
        Standard.removeAll(true);
        Standard.viewType = "struct";
        Standard.standStructInfor = standStructInfor;
        Standard.initData(Standard.viewType, standStructInfor);
    },
    /**
     * 解析store数据
     */
    parseStoreData: function () {
        var recordList = [];
        var dataList = Ext.StoreManager.lookup('stanardStore').getData().items;
        if (Ext.isArray(dataList)) {
            for (var i = 0; i < dataList.length; i++) {
                var data = dataList[i].data;
                if (data.hasOwnProperty("result")) {
                    var record = new HashMap();
                    var hvalue = "";
                    var shvalue = "";
                    record.put("shvalue", shvalue);
                    record.put("hvalue", hvalue);
                    if (data.hasOwnProperty("vfactor_itemid")) {
                        record.put("vvalue", data.vfactor_itemid);
                    } else {
                        record.put("vvalue", "");
                    }
                    if (data.hasOwnProperty("s_vfactor_itemid")) {
                        record.put("svvalue", data.s_vfactor_itemid);
                    } else {
                        record.put("svvalue", "");
                    }
                    if(!data.result){
                        data.result = "";
                    }
                    record.put("value", data.result + "");
                    recordList.push(record);
                } else {
                    for (var key in data) {
                        if (key.indexOf("data`") != -1) {
                            var record = new HashMap();
                            var values = key.split("`")[1];
                            var hvalue = values.split("_")[0];
                            var shvalue = values.split("_")[1];
                            if (hvalue == '#') {
                                hvalue = "";
                            }
                            if (shvalue == '#') {
                                shvalue = "";
                            }
                            record.put("shvalue", shvalue);
                            record.put("hvalue", hvalue);
                            if (data.hasOwnProperty("vfactor_itemid")) {
                                record.put("vvalue", data.vfactor_itemid);
                            } else {
                                record.put("vvalue", "");
                            }
                            if (data.hasOwnProperty("s_vfactor_itemid")) {
                                record.put("svvalue", data.s_vfactor_itemid);
                            } else {
                                record.put("svvalue", "");
                            }
                            if(!data[key]){
                                data[key] = "";
                            }
                            record.put("value", data[key] + "");
                            recordList.push(record);
                        }
                    }
                }


            }
        }
        return recordList;
    },
    /**
     * 导入标准表数据
     * */
    importStandardDataExcel: function () {
        var stanard = this;
        var uploadObj = Ext.create("SYSF.FileUpLoad", {
            upLoadType: 1,
            fileExt: "*.xls",
            height: 30,
            buttonText: gz.standard.pkg.importbuttonText,//浏览
            emptyText: gz.standard.importfileTypesDesc,//选择导入文件
            isTempFile: true,
            VfsModules: VfsModulesEnum.GZ,
            VfsFiletype: VfsFiletypeEnum.other,
            VfsCategory: VfsCategoryEnum.other,
            CategoryGuidKey: '',
            success: function (list) {
                var fileInfo = list[0];
                var map = new HashMap();
                map.put("pkg_id", stanard.pkg_id);
                map.put("stand_id", stanard.stand_id);
                map.put("fileInfo", fileInfo);
                Rpc({
                    functionId: 'GZ00001217', success: function (res) {
                        var result = Ext.decode(res.responseText);
                        var return_code = result.return_code;
                        if (return_code === 'fail') {
                            var errorlog_path = result.return_data.errorlog_path;
                            if (errorlog_path) {//文件本身错误
                                window.open("/servlet/vfsservlet?fileid=" + errorlog_path + "&fromjavafolder=true");
                            } else {
                                Ext.Msg.alert(gz.standard.tip, result.return_msg);
                            }
                        } else {
                            Ext.Msg.alert(gz.standard.tip, gz.standard.importSuccess, function () {
                                Standard.removeAll();
                                Standard.initData("edit");
                            });
                        }
                        Ext.getCmp("mainWin").close();
                    }
                }, map);
            }
        });
        var win = Ext.create('Ext.window.Window', {
            height: 120,
            resizable: false,
            id: 'mainWin',
            modal:true,
            title: gz.standard.importText,//导入
            width: 400,
            layout: {
                type: 'vbox',
                align: 'stretch',
                pack: 'center'
            },
            items: [{
                xtype: 'tbtext',
                padding: '0 0 0 32', //上，左，下，右
                text: gz.label.importComment + '！ '
            }, {
                xtype: 'panel',
                border: false,
                style: 'padding: 0 0 5px 30px;',
                items: [uploadObj]
            }]
        });
        win.show();
    }
});