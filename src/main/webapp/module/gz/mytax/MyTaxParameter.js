Ext.define("mytax.MyTaxParameter", {
    extend: 'Ext.panel.Panel',
    requires: ['mytax.MyTaxFormula'],
    layout: 'vbox',
    // title: gz.label.myTax,
    header: {
        xtype: 'header',
        title: gz.label.myTax,
        style: 'borderStyle:hidden hidden hidden solid'//解决火狐浏览器缩放150 线条没有的 问题
    },
    constructor: function (config) {
        mytaxParameter = this;
        this.callParent();
        this.init();
        this.convertFormulaStoreData();
        this.add(this.getGridPanel());
    },
    init: function () {
        var param = new HashMap();
        param.put("type", "main");
        Rpc({
            functionId: 'GZ00000901',
            async: false,
            success: function (data) {
                var result = Ext.decode(data.responseText);
                if (result.return_code == 'success') {
                    mytaxParameter.returndata = result.return_data;
                    var removeItemList = mytaxParameter.returndata.removeItemList;
                    var tips = mytaxParameter.returndata.tips;
                    var warnMsg = '';
                    for (var i = 0; i < removeItemList.length; i++) {
                        warnMsg = warnMsg + removeItemList[i] + ',';
                    }
                    var alerttips = "";
                    if (warnMsg) {
                        warnMsg = warnMsg.substr(0, warnMsg.length - 1);
                        if (tips) {
                            alerttips = gz.label.dueToItemChange + warnMsg + gz.label.peleaseCheckFormula + tips;
                        } else {
                            alerttips = gz.label.dueToItemChange + warnMsg;
                        }
                    } else {
                        if (tips) {
                            alerttips = gz.label.peleaseCheckFormula + tips;
                        }
                    }
                    if (warnMsg || tips) {
                        Ext.Msg.alert(gz.label.tips, alerttips);
                    }
                    // console.log(removeItemList);
                } else if (result.return_code == 'fail') {

                }
            }
        }, param);

    },
    /**
     * 获取gridPanel
     */
    getGridPanel: function () {
        Ext.util.CSS.updateRule(".x-grid-cell-inner", "max-height", "30px");
        var myTaxSettingGridStore = Ext.create('Ext.data.Store', {
            storeId: 'myTaxSettingGridStore',
            fields: ['itemid', 'name', 'itemType', 'calcFormat', 'yearCollect'],
            data: mytaxParameter.returndata.items
        });
        var myTaxSettingGridPanel = Ext.create('Ext.grid.Panel', {
                store: myTaxSettingGridStore,
                flex: 1,
                width: '100%',
                // height: '80%',
                itemId: 'myTaxSettingGridPanel',
                bodyStyle: "margin-top:1px",//解决火狐放大到150%导致最顶部的边线缺失
                // enableColumnResize: false,//禁止改变列宽
                enableColumnMove: false,//禁止拖放列
                stripeRows: false,//表格是否隔行换色
                columnLines: true,//列分割线
                viewConfig: {
                    markDirty: false, //不显示编辑后的三角
                    plugins: {
                        ptype: 'gridviewdragdrop',
                        dragText: common.label.DragDropData//拖放数据
                    },
                    listeners: {
                        beforedrop: function (node, data, overModel, dropPosition, dropHandlers, eOpts) {
                            // dropHandlers.wait = true;
                            for (var i = 0; i < data.records.length; i++) {
                                if (data.records[i].data.yearCollect == 'operate') {
                                    dropHandlers.cancelDrop();
                                    myTaxSettingGridPanel.getSelectionModel().deselect(data.records[i]);//取消拖动行的选中状态
                                    return;
                                }
                            }
                            if (overModel.data.yearCollect == 'operate' && dropPosition == 'after') { //不允许拖拽行 拖拽到操作行的后面
                                dropHandlers.cancelDrop();
                                myTaxSettingGridPanel.getSelectionModel().deselect(data.records);
                            } else {
                                dropHandlers.processDrop();
                            }
                        },
                        drop: mytaxParameter.dragColumn//拖拽进行排序时会重新渲染拖拽列
                    }
                },
                selModel: Ext.create("Ext.selection.CheckboxModel", {
                    mode: "simple",//multi,simple,single；默认为多选multi
                    checkOnly: true,//如果值为true，则只用点击checkbox列才能选中此条记录
                    enableKeyNav: true,
                    renderer: function (value, metaData, record, rowIndex, colIndex, store, view) {
                        if (record.get("yearCollect") != 'operate')
                            return '<div class="' + Ext.baseCSSPrefix + 'grid-row-checker" role="presentation">&#160;</div>';
                    },
                    listeners: {
                        deselect: function (model, record, index) {//取消选中时产生的事件
                            
                        },
                        select: function (model, record, index) {//record被选中时产生的事件
                            if (model.store.getCount() == 1) {//如果store数据行数为1说明是操作行 不能将删除按钮使能
                                return;
                            }
                            var delButton = myTaxSettingGridPanel.query("#delButton")[0];
                            delButton.setDisabled(false);
                        },
                        selectionchange: function (t, selected, eOpts) {
                            if (selected.length == 1) {
                                if (selected[0].get('yearCollect') == 'operate') {
                                    myTaxSettingGridPanel.getSelectionModel().deselect(selected[0]);
                                    var delButton = myTaxSettingGridPanel.query("#delButton")[0];
                                    delButton.setDisabled(true);
                                }
                            }
                        }
                    },
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
                            // tdCls: me.tdCls,
                            cls: Ext.baseCSSPrefix + 'column-header-checkbox ',
                            defaultRenderer: me.renderer.bind(me),
                            editRenderer: me.editRenderer || me.renderEmpty,
                            locked: me.hasLockedHeader(),
                            processEvent: me.processColumnEvent
                        };
                    }
                }),
                plugins: [
                    Ext.create('Ext.grid.plugin.CellEditing', {
                        clicksToEdit: 2,
                        listeners: {
                            beforeedit: function (editor, context, eOpts) {
                                var record = context.record;
                                if (record.data.yearCollect == 'operate') {  //禁止操作行的编辑操作
                                    return false;
                                } else {
                                    return true;
                                }
                            }
                        }
                    })
                ],
                columns: [
                    {
                        text: gz.label.itemName,
                        dataIndex: 'name',
                        sortable: false,
                        // flex: 5,
                        width: 200,
                        align: 'left',
                        editor: {
                            xtype: 'textfield',
                            allowBlank: false,
                            // maxLength: 40,//项目名称最多40个字符
                        },
                        renderer: function (value, metaData, record, rowIndex, colIndex) {
                            if (record.data.yearCollect == 'operate') {
                                return '<div style="overflow: hidden;height: 30px;padding-top:8px;"  ><span name="add_img" style="float: left">' + mytaxParameter.getImgHtml(rowIndex) + '</span></div>';
                            } else {
                                return value;
                            }
                        }
                    },// 项目名称
                    {
                        text: gz.label.calculationRules,
                        dataIndex: 'calcFormat',
                        sortable: false,
                        // flex: 14,
                        width: 500,
                        align: 'left',
                        renderer: function (value, metaData, record, rowIndex, colIndex) {
                            if (record.data.yearCollect == 'operate') {
                                return '';
                            }
                            // if (value) {
                            //     var zxfjkc = gz.label.zxfjkc.split(",");
                            //     if (Ext.Array.indexOf(zxfjkc, value) != -1) {
                            //         value = "[" + value + "]";
                            //         record.set('calcFormat',value);
                            //     }
                            // }
                            var html = '<div style="overflow: hidden;height: 30px;padding-top:8px" id="' + record.id + '" onmouseover="mytaxParameter.tdMouseOver(\'' + record.id + '\')" onmouseout="mytaxParameter.tdMouseout(\'' + record.id + '\')"><div style="float:left;width:445px;overflow:hidden;text-overflow:ellipsis;white-space:nowrap;">' + value + '</div>' +
                                '<span name="edit_formula" style="display: none;text-align:right;position:absolute;right:0;overflow:hidden;" ><div onclick="mytaxParameter.tdClick(' + rowIndex + ')" ><a style="color:#2792d0;cursor:pointer">' + gz.label.editFormula + '</a></div></span></div>';
                            return html;
                        }
                    },// 计算规则
                    {
                        text: gz.label.summaryByYear,
                        dataIndex: 'yearCollect',
                        sortable: false,
                        // flex:1,
                        width: 94,
                        align: 'center',
                        renderer: function (value, metaData, record, rowIndex, colIndex) {
                            var itemType = record.data.itemType;
                            var itemid = record.data.itemid;
                            var canUseYearSum = itemType === 'N' && itemid !== 'ljsde' && itemid !== 'ljse' && itemid !== 'lj_basedata' && itemid !== 'Sskcs' && itemid !== 'Basedata' && itemid !== 'Sl';
                            if (!canUseYearSum)
                                return '';
                            var src = '';
                            if (value == '0') {
                                src = '/module/gz/mytax/images/close.png'
                            } else if (value == '1') {
                                src = '/module/gz/mytax/images/open.png'
                            }
                            var html = '<img onclick="mytaxParameter.switchPicture(this,\'' + record.id + '\')" width="46" height="24" src ="' + src + '"/>';
                            return html;
                        }
                    }// 按年汇总
                ],
                dockedItems: [{
                    xtype: 'toolbar',
                    dock: 'top',
                    width: '40%',
                    border: false,
                    margin: '1 1 1 1',
                    items: [
                        {
                            xtype: 'button',
                            text: gz.button.creation,
                            handler: function () {
                                mytaxParameter.createSelectFieldWindow();
                            }
                        },
                        {
                            xtype: 'button',
                            text: gz.label.del,
                            //disabled: true,
                            itemId: 'delButton',
                            handler: function () {
                            	//获取是否还有被选中的数据，然后根据数据情况决定删除按钮是否可以用
                                var removedRecords = myTaxSettingGridPanel.getSelectionModel().getSelection();
                                if (removedRecords.length === 0) {
                                    var delButton = myTaxSettingGridPanel.query("#delButton")[0];
                                    Ext.Msg.alert(gz.label.tips,gz.label.notDelItem);
                                    return;
                                }
                                Ext.Msg.confirm(gz.label.tips, gz.label.isSureDeleteSelectData, function (t) {
                                    if (t === 'yes') {
                                        Ext.each(removedRecords, function (r) {
                                            if (r.get("yearCollect") != 'operate')
                                                myTaxSettingGridStore.remove(r);
                                        });
                                        // myTaxSettingGridStore.remove(removedRecords);
                                        mytaxParameter.resetSort();
                                        var itemList = mytaxParameter.getStoreValues();
                                        mytaxParameter.saveItemObject(itemList, 'delete');
                                    }
                                });
                            }
                        }
                    ]
                }],
                listeners: {
                    render: function (panel) {
                        myTaxSettingGridStore.add({name: '', calcFormat: '', yearCollect: 'operate'});
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
                                    if (div.parentNode.cellIndex == 2) {//说明是计算公式列
                                        if (div.children[0]) {
                                            var childDiv = div.children[0].children[0];
                                            if (childDiv.scrollWidth > 445) {
                                                tip.update("<div style='white-space:nowrap;overflow:hidden;'>" + childDiv.innerHTML + "</div>");
                                            } else {
                                                return false;
                                            }
                                        } else {
                                            return false;
                                        }
                                    } else if (div.offsetWidth < div.scrollWidth || div.offsetHeight < div.scrollHeight - 4) {
                                        //div 里面内容不允许自动换行，滚动条隐藏 27250 27734  wangb 2017-4-24
                                        tip.update("<div style='white-space:nowrap;overflow:hidden;'>" + div.innerHTML + "</div>");
                                    } else
                                        return false;
                                }
                            }
                        });
                    },
                    beforeitemmousedown: function (t, record, item, index, e, eOpts) { //避免点击最后一行 页面异常问题
                        if (record.data.yearCollect == 'operate') {
                            return false;
                        }
                    },
                    celldblclick: function (t, td, cellIndex, record, tr, rowIndex, e, eOpts) {
                        if (cellIndex != 2) {
                            return;
                        }
                        if (record.data.yearCollect == 'operate') {
                            return;
                        }
                        //修改计算规则显示数据
                        Ext.create("mytax.MyTaxFormula", {
                            itemdata: mytaxParameter.formulaItemData,
                            itemType: record.data.itemType,
                            calcFormat: record.data.calcFormat ? record.data.calcFormat : '',
                            callbackfunc: mytaxParameter.getCexpr
                        });
                        mytaxParameter.selectRecordIndex = rowIndex;
                        mytaxParameter.id = record.data.id;
                    }
                }
            })
        ;
        myTaxSettingGridPanel.on('edit', function (editor, e) {
            e.record.commit();
            if (Ext.util.Format.trim(editor.activeRecord.data.name) != '') {//只有项目名称不为空时 才保存
                if (editor.activeRecord.data.name.length > 40) {
                    e.record.set('name', Ext.util.Format.substr(Ext.util.Format.trim(editor.activeRecord.data.name), 0, 40));
                } else {
                    e.record.set('name', Ext.util.Format.substr(Ext.util.Format.trim(editor.activeRecord.data.name)));
                }
                var itemList = mytaxParameter.getStoreValues();
                mytaxParameter.saveItemObject(itemList, 'update');
            } else {
                e.record.set('name', Ext.util.Format.trim(e.originalValue));// 如果值trim后 为空 则显示为原来值
            }
        });
        return myTaxSettingGridPanel;
    },
    /**
     * 计算规则列悬浮处理函数
     * @param id divid
     */
    tdMouseOver: function (id) {
        Ext.getDom(id).children[1].style.display = '';
    },
    /**
     * 计算规则列悬浮处理函数
     * @param id divid
     */
    tdMouseout: function (id) {
        Ext.getDom(id).children[1].style.display = 'none';
    },
    /**
     * 计算规则点击请选择处理函数
     * @param id divid
     */
    tdClick: function (rowIndex) {
        //这里完全依靠rowindex从store取值所以必须保证rowindex的正确性
        var myTaxSettingGridStore = Ext.StoreManager.lookup('myTaxSettingGridStore');
        var record = myTaxSettingGridStore.getAt(rowIndex);
        var itemType = record.data.itemType;
        var calcFormat = record.data.calcFormat;
        var id = record.data.id;
        //修改计算规则显示数据
        Ext.create("mytax.MyTaxFormula", {
            itemdata: mytaxParameter.formulaItemData,
            itemType: itemType,
            calcFormat: calcFormat ? calcFormat : '',
            callbackfunc: mytaxParameter.getCexpr
        });
        mytaxParameter.selectRecordIndex = rowIndex;
        mytaxParameter.id = id;
    },
    /**
     * 创建选择指标面板
     */
    createSelectFieldWindow: function () {
        if (Ext.getCmp('selectFieldWindow')) {
            return;
        }
        var selectFieldWindow = Ext.create('Ext.window.Window', {
            width: 350,
            height: 400,
            title: gz.label.selectField,
            id: 'selectFieldWindow',
            resizable: false,
            layout: 'fit',
            items: []
        });
        var selectFieldGridStore = Ext.create('Ext.data.Store', {
            storeId: 'selectFieldGridStore',
            fields: ['itemName', 'itemid', 'itemType'],
            data: mytaxParameter.returndata.gz_items
        });
        var selectFieldGridPanel = Ext.create('Ext.grid.Panel', {
            store: selectFieldGridStore,
            flex: 1,
            itemId: 'selectFieldGridPanel',
            enableColumnResize: false,//禁止改变列宽
            enableColumnMove: false,//禁止拖放列
            stripeRows: false,//表格是否隔行换色
            columnLines: true,//列分割线
            viewConfig: {
                markDirty: false //不显示编辑后的三角
            },
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
                        // tdCls: me.tdCls,
                        cls: Ext.baseCSSPrefix + 'column-header-checkbox ',
                        defaultRenderer: me.renderer.bind(me),
                        editRenderer: me.editRenderer || me.renderEmpty,
                        locked: me.hasLockedHeader(),
                        processEvent: me.processColumnEvent
                    };
                }
            }),
            columns: [
                {
                    text: gz.label.itemName,
                    dataIndex: 'itemName',
                    sortable: false,
                    flex: 1,
                    align: 'left'
                }

            ],
            dockedItems: [{
                xtype: 'toolbar',
                dock: 'bottom',
                width: '40%',
                border: false,
                items: [
                    {xtype: 'tbfill'},
                    {
                        xtype: 'button',
                        text: gz.button.ok,
                        margin: '0 5 0 0',
                        handler: function () {
                            var selectRecords = selectFieldGridPanel.getSelectionModel().getSelection();
                            if (selectRecords.length == 0) {
                                Ext.Msg.alert(gz.label.tips, gz.label.atLestSelectOneField);
                                return;
                            }
                            var itemList = [];
                            for (var i = 0; i < selectRecords.length; i++) {
                                var record = selectRecords[i];
                                var name = record.data.itemName;
                                var calcFormat = name;
                                var zxfjkc = gz.label.zxfjkc.split(",");
                                if (Ext.Array.indexOf(zxfjkc,name) != -1) {
                                    calcFormat = "[" + calcFormat + "]";
                                }
                                var itemid = record.data.itemid;//防止itemid重复
                                var itemType = record.data.itemType;
                                var itemObject = {
                                    itemid: itemid,
                                    name: name,
                                    itemType: itemType,
                                    calcFormat: calcFormat,
                                    yearCollect: '0'
                                };
                                itemList.push(itemObject);
                            }
                            mytaxParameter.saveItemObject(itemList, 'add');
                            selectFieldWindow.close();
                        }
                    },
                    {
                        xtype: 'button',
                        text: gz.button.no,
                        margin: '0 0 5 0',
                        handler: function () {
                            selectFieldWindow.close();
                        }
                    },
                    {xtype: 'tbfill'}
                ]
            }],
            listeners: {
                render: function () {
                    mytaxParameter.filterSelectField();
                }
            }
        });
        selectFieldWindow.add(selectFieldGridPanel);
        selectFieldWindow.show();
    },
    /**
     * 新增项目
     */
    saveItemObject: function (itemList, type) {
        var data = [];
        var param = new HashMap();
        if (type == 'add') {
            var storeList = mytaxParameter.getStoreValues();
            for (var i = 0; i < itemList.length; i++) {
                storeList.push(itemList[i]);
            }
            data = storeList;
        } else if (type == 'update' || type == 'delete') {
            data = itemList;
        }
        param.put("type", "add");
        param.put("data", data);
        Rpc({
            functionId: 'GZ00000901',
            async: false,
            success: function (data) {
                var result = Ext.decode(data.responseText);
                if (result.return_code == 'success') {
                    if (itemList && type == 'add') {
                        var myTaxSettingGridStore = Ext.StoreManager.lookup('myTaxSettingGridStore');
                        var index = myTaxSettingGridStore.getCount() - 1;
                        // myTaxSettingGridStore.add(itemList);
                        myTaxSettingGridStore.insert(index, itemList);
                        mytaxParameter.resetSort();
                        Ext.Msg.alert(gz.label.tips, gz.label.saveSuccess);
                    } else if (itemList && type == 'delete') {
                        Ext.Msg.alert(gz.label.tips, gz.label.delSuccess);
                    }
                } else if (result.return_code == 'fail') {

                }
            }
        }, param);
    },
    /**
     * 获取gridpanelstore里值并封装数据
     */
    getStoreValues: function () {
        var myTaxSettingGridStore = Ext.StoreManager.lookup('myTaxSettingGridStore');
        var itemList = [];
        var items = myTaxSettingGridStore.getData().items;
        for (var i = 0; i < items.length; i++) {
            var itemData = items[i].data;
            var itemObject = {};
            itemObject.calcFormat = itemData.calcFormat;
            itemObject.itemid = itemData.itemid;
            itemObject.name = itemData.name;
            itemObject.itemType = itemData.itemType;
            itemObject.yearCollect = itemData.yearCollect;
            if (itemData.yearCollect == 'operate') {
                continue; //不存储操作行
            }
            itemList.push(itemObject);
        }
        // console.log(itemList);
        return itemList;
    },
    switchPicture: function (t, recordId) {
        var itemList = [];
        var yearCollect = "0";
        if (t.src.indexOf('open.png') != -1) {
            t.src = '/module/gz/mytax/images/close.png'
            yearCollect = "0";
        } else if (t.src.indexOf('close.png') != -1) {
            t.src = '/module/gz/mytax/images/open.png'
            yearCollect = "1";
        }
        var myTaxSettingGridStore = Ext.StoreManager.lookup('myTaxSettingGridStore');
        var items = myTaxSettingGridStore.getData().items;
        for (var i = 0; i < items.length; i++) {
            var itemData = items[i].data;
            if (items[i].id == recordId) {
                itemData.yearCollect = yearCollect;
            }
            var itemObject = {};
            itemObject.calcFormat = itemData.calcFormat;
            itemObject.itemid = itemData.itemid;
            itemObject.name = itemData.name;
            itemObject.yearCollect = itemData.yearCollect;
            if (itemObject.yearCollect == 'operate') {
                continue;
            }
            itemObject.itemType = itemData.itemType;
            itemList.push(itemObject);
        }
        mytaxParameter.saveItemObject(itemList, 'update');
    },
    /**
     * 将gz_itms数据转换成计算公式所需数据格式data
     */
    convertFormulaStoreData: function () {
        this.formulaItemData = [];
        Ext.Array.forEach(mytaxParameter.returndata.gz_items, function (item, index) {
            var object = {};
            object.id = item.itemid + ":" + item.itemName;
            object.name = item.itemid + ":" + item.itemName;
            mytaxParameter.formulaItemData.push(object);

        });
    },
    /**
     * 获取计算公式计算条件
     */
    getCexpr: function (c_expr) {
        //修改显示的html
        Ext.getDom(mytaxParameter.id).children[0].innerHTML = c_expr;
        //修改store里数据
        var myTaxSettingGridStore = Ext.StoreManager.lookup('myTaxSettingGridStore');
        var record = myTaxSettingGridStore.getData().items[mytaxParameter.selectRecordIndex];
        record.set('calcFormat', c_expr);
        var items = mytaxParameter.getStoreValues();
        mytaxParameter.saveItemObject(items, 'update');
    },
    getAddImage: function () {
        var addImage = Ext.create('Ext.Img', {
            src: rootPath + '/images/new_module/add.png',
            width: 25,
            height: 25,
            margin: '13 0 0 5',
            style: 'cursor:pointer;visibility:hidden;',
            listeners: {
                element: 'el',
                click: function () {
//                    var myTaxSettingGridStore = Ext.StoreManager.lookup('myTaxSettingGridStore');
//                    myTaxSettingGridStore.add({itemid: 'tax'+new Date().getTime(), name: '', calcFormat: '', yearCollect: '0'});

                }
            }
        });
        var imageContainer = Ext.create('Ext.container.Container', {
            width: '100%',
            layout: {
                type: 'vbox',
                align: 'center'
            },
            items: [addImage]
        });
        return imageContainer;
    },
    //拖拽排序
    dragColumn: function (node, data, overModel, dropPosition, eOpts) {
        var myTaxSettingGridStore = Ext.StoreManager.lookup('myTaxSettingGridStore');
        var myTaxSettingGridPanel = mytaxParameter.query('#myTaxSettingGridPanel')[0];
        var storeValues = mytaxParameter.getStoreValues();
        mytaxParameter.saveItemObject(storeValues, 'update');
        myTaxSettingGridPanel.getSelectionModel().deselect(data.records);//取消拖拽排序的选中效果
        //todo 要对rowindex重新排序
        mytaxParameter.resetSort();

    },
    /**
     * 获取新增图片的html代码
     */
    getImgHtml: function (rowIndex) {
        return '<img name=' + rowIndex + ' src="/module/gz/mysalary/images/org_add.png" onclick="mytaxParameter.addItem(' + rowIndex + ')" style="height: 16px;width: 16px;float:right;margin-right:5px;cursor:pointer;">'
    },
    addItem: function (rowIndex) {
        mytaxParameter.createSelectFieldWindow();
    },
    /**
     * 重新对rowindex进行排序
     */
    resetSort: function () {
        var edit_formulas = Ext.query("*[name=edit_formula]");
        Ext.each(edit_formulas, function (edit_formula, index) {
            edit_formula.innerHTML = '<div onclick="mytaxParameter.tdClick(' + index + ')" style="text-align:right;float:right;"><a style="color:#2792d0;cursor:pointer">' + gz.label.editFormula + '</a></div>';
        });

    },
    /**
     * 过滤项目名称已选指标
     */
    filterSelectField: function () {
        var myTaxSettingGridStore = Ext.StoreManager.lookup('myTaxSettingGridStore');
        var selectFieldGridStore = Ext.StoreManager.lookup('selectFieldGridStore');
        var filterItems = [];
        for (var i = 0; i < myTaxSettingGridStore.getCount(); i++) {
            filterItems.push(myTaxSettingGridStore.getAt(i).data.itemid);
        }
        selectFieldGridStore.filterBy(function (record, index) {
            // console.log(record);
            return Ext.Array.indexOf(filterItems, record.data.itemid) == -1;
        });
    }

});