/**
 * 标准表结构页面组件
 */
Ext.define("Standard.StandardStructureDetail", {
    extend: 'Ext.panel.Panel',
    requires: ['Standard.SelectTreeFieldCombox'],
    prefix: '',// 主panel id前缀
    standStructInfor: {},//结构数据 用于调整结构时  填充数据
    viewType: '', //create 新增 struct 调整结构
    operateDA:false,
    firstLevelSelectRecord: '',//一级指标数据
    secondLevelSelectRecord: '',//二级级指标数据
    expandSecondNode: undefined,
    expandFirstNode: undefined,
    clickRecord: undefined,  //一级点击选中的记录
    border: false,
    isInit: false, //true表示调整结构第一次展现需要回填数据库数据 false表示 通过上一步下一步调用不需回填数据库数据
    initComponent: function () {
        this.callParent();
        this.setId(this.prefix + 'StandardStructureDetail');
        if (this.viewType == 'struct' && this.isInit) {
            this.echoData();
        }
        this.add(this.createMainPanel());
    },
    /**
     * 创建主panel
     */
    createMainPanel: function () {
        var StructureDetail = this;
        var mainPanel = Ext.create('Ext.panel.Panel', {
            itemId: StructureDetail.prefix + 'mainPanel',
            layout: 'hbox',
            border: false,
            items: [StructureDetail.getLevelPanel('first'), StructureDetail.getArrows(),StructureDetail.getLevelPanel('second')]
        });
        StructureDetail.add(mainPanel);
    },
    /**
     * 用于调整结构时  回显数据
     */
    echoData: function () {
        var StructureDetail = this;
        var hfactorCodeSetId = this.standStructInfor.hfactorCodeSetId;
        if (this.prefix == 'horizontal') { //回显横向指标数据
            StructureDetail.firstLevelSelectRecord = {
                codesetid: hfactorCodeSetId,
                selectHfactor: this.standStructInfor.selectHfactor
            };
            StructureDetail.secondLevelSelectRecord = {
                codesetid: StructureDetail.standStructInfor.shfactorCodeSetId,
                itemtype: StructureDetail.standStructInfor.shfactorItemtype,
                itemid: StructureDetail.standStructInfor.shfactorItemId,
                itemdesc: StructureDetail.standStructInfor.s_hfactor.split("`")[1]
            };
        } else if (this.prefix == 'vertical') { //回显纵向指标数据
            StructureDetail.firstLevelSelectRecord = {
                codesetid: StructureDetail.standStructInfor.vfactorCodeSetId,
                selectVfactor: this.standStructInfor.selectVfactor
            };
            StructureDetail.secondLevelSelectRecord = {
                codesetid: StructureDetail.standStructInfor.svfactorCodeSetId,
                itemtype: StructureDetail.standStructInfor.svfactorItemtype,
                itemid: StructureDetail.standStructInfor.svfactorItemId,
                itemdesc: StructureDetail.standStructInfor.s_vfactor.split("`")[1]
            };
        }
    },
    getArrows:function(){
    	return Ext.create('Ext.container.Container', {
    		border:false,
    		margin:'6 0 0 0',
    		items:[{
    			xtype: 'component',
        		width:27,
        		height:23,
    	    	autoEl: {
    	    		tag: 'img',
    	    		src:'/module/gz/templateset/standard/images/arrows.png'
    	    	}
    	    }]
    	})
    },
    /**
     * 创建选择指标组件
     */
    getSelectFieldCombobox: function (config) {
        var selectFiedlCombobox = Ext.create('Ext.container.Container', {
    		width:220,
        	items:[Ext.create('Standard.SelectTreeFieldCombox', config),
        		{
        		layout:'absolute',
        		width:12,
        		height:12,
        		border:false,
        		x: 215,
        		y: -24,
        		disabledCls: '.disable{opacity:1 !important}',
        	}]
        });
        return selectFiedlCombobox;
    },
    /**
     *获取代码选择treePanel
     */
    getCodeTreePanel: function (level, config) {
        var StructureDetail = this;
        var extraParams = {};
        config = config || {};
        if (config && !config.extraParams) {
            extraParams = {
                ctrltype: "0",
                nmodule: "",
                codesetid: "",
                parentid: "",
                multiple: true,
                onlySelectCodeset: true
            }
        }
        var StructureDetail = this;
        var treeStore = Ext.create('Ext.data.TreeStore', {
            storeId: StructureDetail.prefix + level + 'LevelStore',
            fields: ['text', 'id', 'codesetid', 'orgtype', 'selectable'],
            proxy: {
                type: 'transaction',
                functionId: 'ZJ100000131',
                extraParams: extraParams
            },
            autoLoad: true,
            listeners: {
                beforeload: function (store, operation, eOpts) {
                    if (level == 'first') {
                    	if(store.proxy.extraParams.isEmpty){
                    		StructureDetail.firstLevelSelectRecord.codesetid = '';
                    	}
                        store.proxy.extraParams.codesetid = StructureDetail.firstLevelSelectRecord.codesetid;
                    } else if (level == 'second') {
                    	if(store.proxy.extraParams.isEmpty){
                    		StructureDetail.secondLevelSelectRecord.codesetid = '';
                    	}
                        store.proxy.extraParams.codesetid = StructureDetail.secondLevelSelectRecord.codesetid;
                    }
                }
            }
        });
        var codeTreePanel = Ext.create('Ext.tree.Panel', {
            itemId: StructureDetail.prefix + level + "codeTreePanel",
            rootVisible: false,
            border: false,
            store: treeStore,
            scrollable:'y',
            bodyStyle: 'border-top:0px !important'
        });
        if (StructureDetail.viewType == 'struct') {
            treeStore.on('load', function (store) {
                if (StructureDetail.prefix == 'horizontal') {
                    if (level == 'first') {
                        var firstRecod = store.getById(StructureDetail.standStructInfor.selecthFactorList[0]);
                        for (var i = 0; i < StructureDetail.standStructInfor.selecthFactorList.length; i++) {
                            var itemid = StructureDetail.standStructInfor.selecthFactorList[i];
                            if (store.getById(itemid)) {
                                store.getById(itemid).set("checked", true);
                            }
                        }
                        if(StructureDetail.standStructInfor.hfactor && StructureDetail.standStructInfor.s_hfactor){
                            codeTreePanel.getSelectionModel().select(firstRecod);
                        }
                    } else if (level == 'second') {
                        StructureDetail.firstEchoSecondData();
                    }
                } else if (StructureDetail.prefix == 'vertical') {
                    if (level == 'first') {
                        var firstRecod = store.getById(StructureDetail.standStructInfor.selectvFactorList[0]);
                        for (var i = 0; i < StructureDetail.standStructInfor.selectvFactorList.length; i++) {
                            var itemid = StructureDetail.standStructInfor.selectvFactorList[i];
                            if (store.getById(itemid)) {
                                store.getById(itemid).set("checked", true);
                            }
                        }
                        if (StructureDetail.standStructInfor.vfactor && StructureDetail.standStructInfor.s_vfactor) {
                            codeTreePanel.getSelectionModel().select(firstRecod);
                        }
                    } else if (level == 'second') {
                        StructureDetail.firstEchoSecondData();
                    }

                }
            });
            codeTreePanel.on('checkchange', function (node, checked, eOpts) {
                var selectRecords = codeTreePanel.getChecked();
            	var selectRecordList = [];
            	Ext.each(selectRecords, function (record) {
            		selectRecordList.push(record.get("id"));
            	});
                if (StructureDetail.prefix == 'horizontal') {
                    var key = StructureDetail.standStructInfor.hfactor.split("`")[0] + "_" + node.id;
                    if (level == 'first') {
                        if (StructureDetail.standStructInfor.hfactor && StructureDetail.standStructInfor.s_hfactor) {
                            if (!checked) {
                                if (Ext.Array.indexOf(StructureDetail.standStructInfor.selecthFactorList, node.id) != -1) {
                                    delete StructureDetail.standStructInfor.hrelation[key];
                                }
                            } else {
                                if (Ext.Array.indexOf(StructureDetail.standStructInfor.selecthFactorList, node.id) == -1) {
                                    if (StructureDetail.standStructInfor.hfactor && StructureDetail.standStructInfor.s_hfactor) {
                                        StructureDetail.standStructInfor.hrelation[key] = [];
                                    }
                                }
                            }
                        }
                        StructureDetail.standStructInfor.selecthFactorList = selectRecordList;

                    } else if (level == 'second') {
                        //只有一级二级都选 才需一级二级联动
                        if (StructureDetail.standStructInfor.hfactor && StructureDetail.standStructInfor.s_hfactor) {
                            var id = "";
                            if (!StructureDetail.clickRecord) {
                                id = StructureDetail.standStructInfor.selecthFactorList[0];
                            } else {
                                id = StructureDetail.clickRecord.get("id");
                            }
                            //获取此时一级选中的itemid
                            var key = StructureDetail.standStructInfor.hfactor.split("`")[0] + "_" + id;
                            //获取缓存
                            var hrelation = StructureDetail.standStructInfor.hrelation;
                            hrelation[key] = selectRecordList;
                        } else if (!StructureDetail.standStructInfor.hfactor && StructureDetail.standStructInfor.s_hfactor) {
                            var selectSecondList = StructureDetail.standStructInfor.selectshFactorList;
                            StructureDetail.standStructInfor.selectshFactorList = selectRecordList;
                        }
                    }
                } else if (StructureDetail.prefix == 'vertical') {
                    if (level == 'first') {
                        var key = StructureDetail.standStructInfor.vfactor.split("`")[0] + "_" + node.id;
                        if (StructureDetail.standStructInfor.vfactor && StructureDetail.standStructInfor.s_vfactor) {
                            if (!checked) {
                                if (Ext.Array.indexOf(StructureDetail.standStructInfor.selectvFactorList, node.id) != -1) {
                                    delete StructureDetail.standStructInfor.vrelation[key];
                                }
                            } else {
                                if (Ext.Array.indexOf(StructureDetail.standStructInfor.selectvFactorList, node.id) == -1) {
                                    if (StructureDetail.standStructInfor.vfactor && StructureDetail.standStructInfor.s_vfactor) {
                                        StructureDetail.standStructInfor.vrelation[key] = [];
                                    }
                                }
                            }
                        }
                        StructureDetail.standStructInfor.selectvFactorList = selectRecordList;
                    } else if (level == 'second') {
                        //只有一级二级都选 才需一级二级联动
                        if (StructureDetail.standStructInfor.vfactor && StructureDetail.standStructInfor.s_vfactor) {
                            var id = "";
                            if (!StructureDetail.clickRecord) {
                                id = StructureDetail.standStructInfor.selectvFactorList[0];
                            } else {
                                id = StructureDetail.clickRecord.get("id");
                            }
                            //获取此时一级选中的itemid
                            var key = StructureDetail.standStructInfor.vfactor.split("`")[0] + "_" + id;
                            //获取缓存
                            var vrelation = StructureDetail.standStructInfor.vrelation;
                            vrelation[key] = selectRecordList;
                        } else if (!StructureDetail.standStructInfor.vfactor && StructureDetail.standStructInfor.s_vfactor) {
                            StructureDetail.standStructInfor.selectsvFactorList = selectRecordList;
                        }
                    }
                }
            });
            if (level == 'second') {
                // codeTreePanel.on('itemexpand', function (node, checked, eOpts) {
                //     StructureDetail.expandSecondNode = node;
                // });
            } else if (level == 'first') {
                // codeTreePanel.on('itemexpand', function (node, checked, eOpts) {
                //     StructureDetail.expandFirstNode = node;
                // });
                if (StructureDetail.standStructInfor.hfactor && StructureDetail.standStructInfor.s_hfactor||
                		StructureDetail.standStructInfor.vfactor && StructureDetail.standStructInfor.s_vfactor) {
                    codeTreePanel.on('itemclick', function (t, record, item, index, e, eOpts) {
                        //点击时  选中第二级  treepanel节点
                        StructureDetail.clickRecord = record;
                        StructureDetail.firstEchoSecondData();
                    });
                }
            }
        }
        return codeTreePanel;
    },
    firstEchoSecondData: function () {
        var StructureDetail = this;
        if (this.prefix == 'horizontal') {
            var secondStore = Ext.StoreManager.lookup('horizontalsecondLevelStore');
            if (Ext.StoreManager.lookup('horizontalsecondGridStore').data.items.length<=0) {
                Ext.each(secondStore.getData().items, function (record) {
                    record.set("checked", false);
                });
                if (StructureDetail.standStructInfor.hfactor && StructureDetail.standStructInfor.s_hfactor) {
                    //只有第一次加载的时候 赋值数据库中选中的 其余的
                    var record = {};
                    var id = "";
                    if (StructureDetail.clickRecord) {
                        record = StructureDetail.clickRecord;
                        id = record.get("id");
                    } else {
                        id = StructureDetail.standStructInfor.selecthFactorList[0];
                    }
                    var hrelation = StructureDetail.standStructInfor.hrelation;
                    var key = StructureDetail.standStructInfor.hfactor.split('`')[0] + "_" + id;
                    var selectList = hrelation[key];
                    if (Ext.isArray(selectList)) {
                        for (var i = 0; i < selectList.length; i++) {
                            var itemid = selectList[i];
                            if (secondStore.getById(itemid)) {
                                secondStore.getById(itemid).set("checked", true);
                            }
                        }
                    }
                } else if (!StructureDetail.standStructInfor.hfactor && StructureDetail.standStructInfor.s_hfactor) {
                    var selectList = StructureDetail.standStructInfor.selectshFactorList;
                    for (var i = 0; i < selectList.length; i++) {
                        var itemid = selectList[i];
                        if (secondStore.getById(itemid)) {
                            secondStore.getById(itemid).set("checked", true);
                        }
                    }
                }
            } else {
                StructureDetail.echoGridPanelSelect();
            }

        } else if (this.prefix == 'vertical') {
            var secondStore = Ext.StoreManager.lookup('verticalsecondLevelStore');
            if (Ext.StoreManager.lookup('verticalsecondGridStore').data.items.length<=0) {
                Ext.each(secondStore.getData().items, function (record) {
                    record.set("checked", false);
                });
                if (StructureDetail.standStructInfor.vfactor && StructureDetail.standStructInfor.s_vfactor) {
                    //只有第一次加载的时候 赋值数据库中选中的 其余的
                    var record = {};
                    if (StructureDetail.clickRecord) {
                        record = StructureDetail.clickRecord;
                        id = record.get("id");
                    } else {
                        id = StructureDetail.standStructInfor.selectvFactorList[0];
                    }
                    var vrelation = StructureDetail.standStructInfor.vrelation;
                    var key = StructureDetail.standStructInfor.vfactor.split('`')[0] + "_" + id;
                    var selectList = vrelation[key];
                    if (Ext.isArray(selectList)) {
                        for (var i = 0; i < selectList.length; i++) {
                            var itemid = selectList[i];
                            if (secondStore.getById(itemid)) {
                                secondStore.getById(itemid).set("checked", true);
                            }
                        }
                    }
                } else if (!StructureDetail.standStructInfor.vfactor && StructureDetail.standStructInfor.s_vfactor) {
                    var selectList = StructureDetail.standStructInfor.selectsvFactorList;
                    for (var i = 0; i < selectList.length; i++) {
                        var itemid = selectList[i];
                        if (secondStore.getById(itemid)) {
                            secondStore.getById(itemid).set("checked", true);
                        }
                    }
                }
            } else {
                StructureDetail.echoGridPanelSelect();
            }

        }
    },
    /**
     * 获取指标panel
     * level 层级 first 1级指标 second 2级指标
     */
    getLevelPanel: function (level) {
        var StructureDetail = this;
        var childContainer = Ext.create('Ext.panel.Panel', {
            itemId: StructureDetail.prefix + level + 'LevelContainer',
            height: 320,
            width: 250,
            layout: 'fit',
            border: true
        });
        var comboboxConfig = {
            codeFilter: false,
            source: 'A`B`K',
            StructureDetail:StructureDetail,
            refreshSoreIdtree:StructureDetail.prefix + level + 'LevelStore',
            refreshSoreIdgrid:StructureDetail.prefix + 'secondGridStore'
        };
        if (StructureDetail.viewType == 'struct') {
            comboboxConfig.disabled = true;
            comboboxConfig.disabledCls = '.disable{opacity:1 !important}';
        }
        comboboxConfig.itemId = StructureDetail.prefix + level + "factorCombo";
        comboboxConfig.onlySelectCodeset = true;
        if (level == 'first') { //一级指标只能选择代码型指标
            comboboxConfig.filterTypes = 'A,D,M,N';
            comboboxConfig.emptyText = '请输入一级指标';
        } else if (level == 'second') { //二级指标只能选择 代码型 日期型 数值型 指标
            comboboxConfig.filterTypes = 'A,M';
            comboboxConfig.emptyText = '请输入二级指标';
        }
        comboboxConfig.listeners = {
            select: function (t, record) {
                if (level == 'first') {
                    StructureDetail.firstLevelSelectRecord = {
                        codesetid: record.get("codesetid")
                    };
                    childContainer.add(StructureDetail.getCodeTreePanel(level));
                } else if (level == 'second') {
                    StructureDetail.secondLevelSelectRecord = {
                        itemtype: record.get("itemtype"),
                        codesetid: record.get("codesetid"),
                        itemid: record.get("itemid"),
                        itemdesc: record.get("itemdesc")
                    };
                    childContainer.removeAll(true);
                    var childDetailCmp = StructureDetail.getSecondChildCmp();
                    childContainer.add(childDetailCmp);
                }
            },
        };
        comboboxConfig.listeners.render = function (t) {
            if (StructureDetail.viewType == 'struct' && StructureDetail.isInit == true) {
                if (level == 'first') {
                	childContainer.add(StructureDetail.getCodeTreePanel(level))
                    if (StructureDetail.prefix == 'horizontal') {
                        t.setValue(StructureDetail.standStructInfor.hfactor)
                    } else if (StructureDetail.prefix == 'vertical') {
                        t.setValue(StructureDetail.standStructInfor.vfactor)
                    }
                } else if (level == 'second') {
                	childContainer.removeAll(true);
                    var childDetailCmp = StructureDetail.getSecondChildCmp();
                    childContainer.add(childDetailCmp);
                    if (StructureDetail.prefix == 'horizontal') {
                        t.setValue(StructureDetail.standStructInfor.s_hfactor)
                    } else if (StructureDetail.prefix == 'vertical') {
                        t.setValue(StructureDetail.standStructInfor.s_vfactor)
                    }
                }
            }
        };
        var levelPanel = Ext.create('Ext.panel.Panel', {
            border: false,
            layout: 'vbox',
            margin: '5 5 0 5',
            items: [
                StructureDetail.getSelectFieldCombobox(comboboxConfig),
                childContainer
            ]
        });
        return levelPanel;

    },
    /**
     * 获取二级指标具体子容器
     */
    getSecondChildCmp: function () {
        var StructureDetail = this;
        var itemtype = StructureDetail.secondLevelSelectRecord.itemtype;
        //创建日期型，数值型页面
        var selectReords = [];
        var storeData = [];
        if (itemtype == 'N' || itemtype == 'D') {
            var param = new HashMap();
            param.put("item", StructureDetail.secondLevelSelectRecord.itemid);
            Rpc({
                functionId: 'GZ00001212', success: function (res) {
                    var resData = Ext.decode(res.responseText);
                    var return_code = resData.return_code;
                    if (return_code == "success") {
                        storeData = resData.return_data;
                    }
                }, scope: this, async: false
            }, param);
        }
        var secondGridStore = Ext.create('Ext.data.Store', {
            storeId: StructureDetail.prefix + 'secondGridStore',
            fields: ['item_id', 'description'],
            data: storeData
        });
        var secondGridPanel = Ext.create('Ext.grid.Panel', {
            border: false,
            autoScroll: true,
            rowLines: true,
            columnLines: true,
            border: false,
            itemId: StructureDetail.prefix + 'secondGridPanel',
            hideHeaders: true,
            bodyStyle: 'border-width:0px !important',
            selModel: Ext.create("Ext.selection.CheckboxModel", {
                checkOnly: true,
                renderer: function (value, metaData, record, rowIndex, colIndex, store, view) {
                    if (record.get("item_id") != 'operate')
                        return '<div class="' + Ext.baseCSSPrefix + 'grid-row-checker" role="presentation">&#160;</div>';
                }
            }),
            store: secondGridStore,
            columns: [{
                dataIndex: 'item_id', hidden: true
            }, {
                dataIndex: 'description',
                sortable: false,
                flex: 1,
                renderer: function (value, metaData, record, rowIndex, colIndex) {
                    metaData.tdStyle = 'border-right:0px';
                    if(StructureDetail.viewType == 'struct'){
                    	return '<div style="float:left;height:30px;width:445px;overflow:hidden;text-overflow:ellipsis;white-space:nowrap;padding-top:8px;">' + value + '</div>' ;
                    }
                    //删除按钮显现
                    tdMouseOver = function (id) {
                        Ext.getDom(id).children[1].style.display = '';
                    };
                    //删除按钮消失
                    tdMouseout = function (id) {
                        Ext.getDom(id).children[1].style.display = 'none';
                    };
                    if (value) {
                        var deleteImghtml = '<div style="overflow: hidden;height: 30px;" id="' + record.id + '" onmouseover="tdMouseOver(\'' + record.id + '\')" onmouseout="tdMouseout(\'' + record.id + '\')"><div style="float:left;width:445px;overflow:hidden;text-overflow:ellipsis;white-space:nowrap;padding-top:8px;">' + value + '</div>' +'<div name="edit_formula" style="display: none;text-align:right;position:absolute;right:0;overflow:hidden;padding-top:3px;" >' + StructureDetail.getdeletehtml(rowIndex) + '</div></div>';
                        return deleteImghtml;
                    } else if (record.get("item_id") == 'operate') {
                        var item_id = "";
                        return '<div style="overflow: hidden;height: 30px;padding-top:8px;"  ><div name="add_img" style="float: left">' + StructureDetail.getImgHtml(rowIndex, item_id) + '</div></div>';
                    }
                }
            }],
            listeners: {
                cellclick: function (t, td, cellIndex, record, tr, rowIndex, e, eOpts) {
                	if (StructureDetail.viewType == 'struct') {
                		return;
                	}
                    if (cellIndex != 2) {
                        return;
                    }
                    if (record.get("item_id") == 'operate') {
                        return;
                    }
                    var item = record.data.item;
                    var item_id = record.data.item_id;
                    var data = new HashMap();
                    data.put("item", item);
                    data.put("item_id", item_id);
                    StructureDetail.createSelectWindow(rowIndex, item_id);
                    Rpc({
                        functionId: 'GZ00001212', success: function (result) {
                            var records = Ext.decode(result.responseText);
                            var data = records.return_data[0]
                            Ext.getCmp('selectWindow').query('#selectTextfield')[0].setValue(data.description);
                            Ext.getCmp('selectWindow').query('#firstSelectCombo')[0].setValue(data.lowerOperate);
                            Ext.getCmp('selectWindow').query('#firstSelectField')[0].setValue(data.lowerValue);
                            Ext.getCmp('selectWindow').query('#secondSelectCombo')[0].setValue(data.heightOperate);
                            Ext.getCmp('selectWindow').query('#secondSelectField')[0].setValue(data.heightValue);
                            if (data.type == "D") {
                                Ext.getCmp('selectWindow').query('#selectFieldsetCombo')[0].setValue(data.middleValue);
                            } else if (data.type == "N") {
                                Ext.getCmp('selectWindow').query('#selectFieldsetText')[0].setValue(StructureDetail.secondLevelSelectRecord.itemdesc);
                            }
                            if (data.isAccuratelyDay == "True") {
                                Ext.getCmp('selectWindow').query('#selectCheckboxfield')[0].setValue(true);
                            } else {
                                Ext.getCmp('selectWindow').query('#selectCheckboxfield')[0].setValue(false);
                            }
                        }, scope: StructureDetail
                    }, data);

                }
            }
        });
        if (StructureDetail.viewType == 'struct') {
            secondGridPanel.on('selectionchange', function (t, selected, eOpts) {
            	if(StructureDetail.operateDA){
            		return;
            	}
                if (StructureDetail.prefix == 'horizontal') {
                    if (StructureDetail.standStructInfor.hfactor && StructureDetail.standStructInfor.s_hfactor) {
                        var id = '';
                        if (!StructureDetail.clickRecord) {
                            id = StructureDetail.standStructInfor.selecthFactorList[0];
                        } else {
                            record = StructureDetail.clickRecord;
                            id = record.get("id");
                        }
                        //获取此时一级选中的itemid
                        var key = StructureDetail.standStructInfor.hfactor.split("`")[0] + "_" + id;
                        //获取缓存
                        var hrelation = StructureDetail.standStructInfor.hrelation;
                        var selectSecondList = hrelation[key];
                        selectSecondList = [];
                        for (var i = 0; i < selected.length; i++) {
                            selectSecondList.push(selected[i].data.item_id);
                        }
                        hrelation[key] = selectSecondList;

                    } else if (!StructureDetail.standStructInfor.hfactor && StructureDetail.standStructInfor.s_hfactor) {
                        var selectSecondList = [];
                        for (var i = 0; i < selected.length; i++) {
                            selectSecondList.push(selected[i].data.item_id);
                        }
                        StructureDetail.standStructInfor.selectshFactorList = selectSecondList;
                    }
                } else if (StructureDetail.prefix == 'vertical') {
                    if (StructureDetail.standStructInfor.vfactor && StructureDetail.standStructInfor.s_vfactor) {
                        var id = '';
                        if (!StructureDetail.clickRecord) {
                            id = StructureDetail.standStructInfor.selectvFactorList[0];
                        } else {
                            record = StructureDetail.clickRecord;
                            id = record.get("id");
                        }
                        //获取此时一级选中的itemid
                        var key = StructureDetail.standStructInfor.vfactor.split("`")[0] + "_" + id;
                        //获取缓存
                        var vrelation = StructureDetail.standStructInfor.vrelation;
                        var selectSecondList = vrelation[key];
                        selectSecondList = [];
                        for (var i = 0; i < selected.length; i++) {
                            selectSecondList.push(selected[i].data.item_id);
                        }
                        vrelation[key] = selectSecondList;

                    } else if (!StructureDetail.standStructInfor.vfactor && StructureDetail.standStructInfor.s_vfactor) {
                    	var selectSecondList = [];
                        for (var i = 0; i < selected.length; i++) {
                            selectSecondList.push(selected[i].data.item_id);
                        }
                        StructureDetail.standStructInfor.selectsvFactorList = selectSecondList;
                    }
                }

            });
        }
        secondGridPanel.on('render', function (t) {
            if (StructureDetail.viewType == 'struct') {
                StructureDetail.echoGridPanelSelect();
            } else {
            	var datadesc = {item_id: 'operate', description: ''};
                secondGridStore.add(datadesc);
            }
        });
        if (itemtype == 'N' || itemtype == 'D') {
            return secondGridPanel;
        } else {
            return this.getCodeTreePanel('second');
        }
    },
    echoGridPanelSelect: function () {
        var StructureDetail = this;
        var selectReords = [];
        var gridPanel = StructureDetail.query("#" + StructureDetail.prefix + "secondGridPanel")[0];
        var secondGridStore = Ext.StoreManager.lookup(StructureDetail.prefix + 'secondGridStore');
        if (StructureDetail.viewType == 'struct') {
            if (StructureDetail.prefix == 'horizontal') {
                if (StructureDetail.standStructInfor.hfactor && StructureDetail.standStructInfor.s_hfactor) {
                    var id = "";
                    if (StructureDetail.clickRecord) {
                        record = StructureDetail.clickRecord;
                        id = record.get("id");
                    } else {
                        id = StructureDetail.standStructInfor.selecthFactorList[0];
                    }
                    var hrelation = StructureDetail.standStructInfor.hrelation;
                    var key = StructureDetail.standStructInfor.hfactor.split('`')[0] + "_" + id;
                    var selectList = hrelation[key];
                    if (Ext.isArray(selectList)) {
                    	for(var j = 0;j<StructureDetail.standStructInfor.selecthFactorList.length;j++){
                    		var idempty = StructureDetail.standStructInfor.selecthFactorList[j];
                    		var keyempty = StructureDetail.standStructInfor.hfactor.split('`')[0] + "_" + idempty;
                    		var selectListempty = hrelation[keyempty];
                    		for(var i = selectListempty.length;i>=0;i--){
                    			var itemid = selectListempty[i];
                    			if(!secondGridStore.findRecord("item_id", itemid)&&selectListempty[i]){
                        			StructureDetail.standStructInfor.hrelation[keyempty].splice(i,1);
                        		}
                    		}
                    	}
                        for (var i = 0; i < selectList.length; i++) {
                        	var itemid = selectList[i];
                            if (secondGridStore.findRecord("item_id", itemid)) {
                                selectReords.push(secondGridStore.findRecord("item_id", itemid));
                            }
                        }
                    }
                    gridPanel.getSelectionModel().clearSelections();
                    gridPanel.getView().refresh();
                    gridPanel.getSelectionModel().select(selectReords);
                } else if (!StructureDetail.standStructInfor.hfactor && StructureDetail.standStructInfor.s_hfactor) {
                    var selectList = StructureDetail.standStructInfor.selectshFactorList;
                    for (var i = selectList.length-1; i >=0 ; i--) {
                        var itemid = selectList[i];
                        if (secondGridStore.findRecord("item_id", itemid)) {
                            selectReords.push(secondGridStore.findRecord("item_id", itemid));
                        } else {
                        	StructureDetail.standStructInfor.selectshFactorList.splice(i,1);
                        }
                    }
                    gridPanel.getSelectionModel().clearSelections();
                    gridPanel.getView().refresh();
                    gridPanel.getSelectionModel().select(selectReords);
                }
            } else if (StructureDetail.prefix == 'vertical') {
                if (StructureDetail.standStructInfor.vfactor && StructureDetail.standStructInfor.s_vfactor) {
                    //只有第一次加载的时候 赋值数据库中选中的 其余的
                    var id = '';
                    if (StructureDetail.clickRecord) {
                        record = StructureDetail.clickRecord;
                        id = record.get("id");
                    } else {
                        id = StructureDetail.standStructInfor.selectvFactorList[0];
                    }
                    var vrelation = StructureDetail.standStructInfor.vrelation;
                    var key = StructureDetail.standStructInfor.vfactor.split('`')[0] + "_" + id;
                    var selectList = vrelation[key];
                    if (Ext.isArray(selectList)) {
                    	for(var j = 0;j<StructureDetail.standStructInfor.selectvFactorList.length;j++){
                    		var idempty = StructureDetail.standStructInfor.selectvFactorList[j];
                    		var keyempty = StructureDetail.standStructInfor.vfactor.split('`')[0] + "_" + idempty;
                    		var selectListempty = vrelation[keyempty];
                    		for(var i = selectListempty.length; i>=0;i--){
                    			var itemid = selectListempty[i];
                    			if(!secondGridStore.findRecord("item_id", itemid)&&selectListempty[i]){
                        			StructureDetail.standStructInfor.vrelation[keyempty].splice(i,1);
                        		}
                    		}
                    	}
                        for (var i = 0; i < selectList.length; i++) {
                            var itemid = selectList[i];
                            if (secondGridStore.findRecord("item_id", itemid)) {
                                selectReords.push(secondGridStore.findRecord("item_id", itemid));
                            }
                        }
                    }
                    gridPanel.getSelectionModel().clearSelections();
                    gridPanel.getView().refresh();
                    gridPanel.getSelectionModel().select(selectReords);
                } else if (!StructureDetail.standStructInfor.vfactor && StructureDetail.standStructInfor.s_vfactor) {
                    var selectList = StructureDetail.standStructInfor.selectsvFactorList;
                    for (var i = selectList.length-1; i >=0 ; i--) {
                        var itemid = selectList[i];
                        if (secondGridStore.findRecord("item_id", itemid)) {
                            selectReords.push(secondGridStore.findRecord("item_id", itemid));
                        } else {
                        	StructureDetail.standStructInfor.selectsvFactorList.splice(i,1);
                        }
                    }
                    gridPanel.getSelectionModel().clearSelections();
                    gridPanel.getView().refresh();
                    gridPanel.getSelectionModel().select(selectReords);
                }
            }
        }

    }
    ,
//二级指标删除按钮
    getdeletehtml: function (rowIndex) {
        var StructureDetail = this;
        //二级指标删除方法
        deleteItem = function (rowIndex) {
            //获取event事件对象
            var event = window.event || arguments.callee.caller.arguments[0];
            if (event.stopPropagation) {
                // 针对 Mozilla 和 Opera
                event.stopPropagation();
            } else if (window.event) {
                // 针对 IE
                window.event.cancelBubble = true;
            }
            var store = Ext.data.StoreManager.lookup(StructureDetail.prefix + 'secondGridStore');
            var item = store.getAt(rowIndex).data.item;
            var item_id = store.getAt(rowIndex).data.item_id;
            var data = new HashMap();
            data.put("item", item);
            data.put("item_id", item_id);
            Rpc({
                functionId: 'GZ00001221', success: function (result) {
                    var msg = Ext.decode(result.responseText);
                    if (msg.return_code == "success") {
                    	StructureDetail.secondGridStoreLoad();
                    	StructureDetail.echoGridPanelSelect();
                    	StructureDetail.operateDA = false;
                    }
                }, scope: StructureDetail
            }, data);
        };
        return '<img name=' + rowIndex + ' height=21px width=21px src="/module/gz/templateset/standard/images/delete.png" onclick="deleteItem(' + rowIndex + ')" style="float:right;margin-right:5px;cursor:pointer;">';
    },

//二级指标新增按钮
    getImgHtml: function (rowIndex, item_id) {
        var StructureDetail = this;
        //二级指标新增按钮方法
        addItem = function (rowIndex, item_id) {
            var event = window.event || arguments.callee.caller.arguments[0];
            if (event.stopPropagation) {
                // 针对 Mozilla 和 Opera
                event.stopPropagation();
            } else if (window.event) {
                // 针对 IE
                window.event.cancelBubble = true;
            }
            if (!StructureDetail.secondLevelSelectRecord) {
                Ext.Msg.alert(gz.standard.tip, gz.standard.sdst.addLexpr);
            } else {
                var itemtype = StructureDetail.secondLevelSelectRecord.itemtype;
                if ((itemtype == "D" || itemtype == "N")&&!Ext.getCmp('selectWindow')) {
                    StructureDetail.createSelectWindow(rowIndex, item_id);
                } else {
                    Ext.Msg.alert(gz.standard.tip, gz.standard.sdst.addLexprtype);
                }
            }
        };
        return '<img name=' + rowIndex + ' src="/module/gz/mysalary/images/org_add.png" onclick="addItem(' + rowIndex + ',\'' + item_id + '\')" style="height: 16px;width: 16px;float:right;margin-right:5px;cursor:pointer;">';
    }
    ,
//创新新增二级指标方法
    createSelectWindow: function (rowIndex, item_id) {
        var StructureDetail = this;
        var selectFieldsetCombostore = Ext.create('Ext.data.Store', {
            fields: ['desc', 'name'],
            data: [
                {
                    "desc": "0",
                    "name": gz.standard.sdst.lexprtypeDfirst + StructureDetail.secondLevelSelectRecord.itemdesc + ")"
                },
                {
                    "desc": "1",
                    "name": gz.standard.sdst.lexprtypeDsecond + StructureDetail.secondLevelSelectRecord.itemdesc + ")"
                },
                {
                    "desc": "2",
                    "name": gz.standard.sdst.lexprtypeDthird + StructureDetail.secondLevelSelectRecord.itemdesc + ")"
                },
                {
                    "desc": "3",
                    "name": gz.standard.sdst.lexprtypeDforth + StructureDetail.secondLevelSelectRecord.itemdesc + ")"
                }
            ]
        });
        var selectFieldsetCombo = Ext.create('Ext.form.field.ComboBox', {
            width: '100%',
            height: 22,
            editable: false,
            selectOnFocus: true,
            store: selectFieldsetCombostore,
            itemId: 'selectFieldsetCombo',
            displayField: 'name',
            valueField: 'desc',
            listConfig: {minWidth: 120},
            listeners: {
                render: function (combo) {
                    combo.setValue("0");
                }
            }
        });
        var selectFieldsetText = Ext.create('Ext.form.field.Text', {
            width: '100%',
            height: 22,
            readOnly: true,
            itemId: 'selectFieldsetText'
        });
        var selectFCombostore = Ext.create('Ext.data.Store', {
            fields: ['desc', 'name'],
            data: [
                {"desc": "<"},
                {"desc": "<="},
                {"desc": gz.standard.sdst.lexprOperatestore}
            ]
        });
        var selectSCombostore = Ext.create('Ext.data.Store', {
            fields: ['desc', 'name'],
            data: [
                {"desc": "<"},
                {"desc": "<="},
                {"desc": "="},
                {"desc": gz.standard.sdst.lexprOperatestore}
            ]
        });
        var selectdataPanel = Ext.create('Ext.form.Panel', {
            layout: 'hbox',
            border: 0,
            items: [{
                width: 150,
                itemId: 'firstSelectField',
                xtype: 'textfield',
                margin: '30 0 0 30',
                height:22
            }, {
                width: 40,
                selectOnFocus: true,
                itemId: 'firstSelectCombo',
                store: selectFCombostore,
                editable: false,
                displayField: 'desc',
                valueField: 'desc',
                xtype: 'combobox',
                margin: '30 0 0 0',
                height:22,
                listConfig: {minWidth: 40},
                listeners: {
                    render: function (combo) {
                        combo.setValue("<");
                    }
                }
            }, {
                width: 120,
                border: 0,
                itemId: 'selectFieldset',
                height:22,
                xtype: 'form',
                margin: '30 10 0 10',
                items: []
            }, {
                width: 40,
                selectOnFocus: true,
                itemId: 'secondSelectCombo',
                store: selectSCombostore,
                editable: false,
                displayField: 'desc',
                valueField: 'desc',
                xtype: 'combobox',
                margin: '30 0 0 0',
                height:22,
                listConfig: {minWidth: 40},
                listeners: {
                    render: function (combo) {
                        combo.setValue("<");
                    }
                }
            }, {
                width: 150,
                itemId: 'secondSelectField',
                xtype: 'textfield',
                height:22,
                margin: '30 0 0 0'
            }]
        });
        var selectWindow = Ext.create('Ext.window.Window', {
            title: gz.standard.sdst.lexprtitle,
            width: 600,
            height: 270,
            id: 'selectWindow',
            layout: 'vbox',
            buttonAlign: 'center',
            items: [{
            	bodyStyle:'border-top:1px !important',
                width: 220,
                emptyText: gz.standard.sdst.lexpremptyText,
                itemId: 'selectTextfield',
                xtype: 'textfield',
                height:22,
                margin: '30 0 0 30'
            }, selectdataPanel, {
                xtype: 'checkboxfield',
                boxLabel: gz.standard.sdst.lexprboxLabel,
                itemId: 'selectCheckboxfield',
                margin: '30 0 0 30'
            }],
            buttons: [{
                text: gz.standard.confirm,
                margin:'0 0 3 0',
                handler: function () {
                    var itemtype = StructureDetail.secondLevelSelectRecord.itemtype;
                    var itemid = StructureDetail.secondLevelSelectRecord.itemid;
                    var middleValue = '';
                    if (itemtype == 'D') {
                        middleValue = selectWindow.query('#selectFieldsetCombo')[0].getValue();
                    }
                    var type = itemtype;
                    var description = selectWindow.query('#selectTextfield')[0].getValue();
                    var lowerOperate = selectWindow.query('#firstSelectCombo')[0].getValue();
                    var lowerValue = selectWindow.query('#firstSelectField')[0].getValue();
                    var heightOperate = selectWindow.query('#secondSelectCombo')[0].getValue();
                    var heightValue = selectWindow.query('#secondSelectField')[0].getValue();
                    var isAccuratelyDay = selectWindow.query('#selectCheckboxfield')[0].getValue();
                    //二级指标描述不能为空
                    if(!description){
                    	Ext.Msg.alert(gz.standard.tip,gz.standard.sdst.tiplexprDes);
                    	return
                    }
                    //二级指标项为空时符号为无
                    if((lowerOperate=="无"&&heightOperate=="无")){
                    	Ext.Msg.alert(gz.standard.tip,gz.standard.sdst.tiplexprValue);
                    	return
                    }
                    if((lowerOperate!="无"&&!lowerValue)){
                    	Ext.Msg.alert(gz.standard.tip,gz.standard.sdst.tiplexprLValue);
                    	return
                    }
                    if((heightOperate!="无"&&!heightValue)){
                    	Ext.Msg.alert(gz.standard.tip,gz.standard.sdst.tiplexprRValue);
                    	return
                    }
                   //日期型二级指标项为正整数，数值型为
                    if(itemtype == 'D'&&(lowerOperate!="无"&&isNaN(lowerValue)||heightOperate!="无"&&isNaN(heightValue))){
                    	Ext.Msg.alert(gz.standard.tip,gz.standard.sdst.tiplexprDNaN);
                    	return
                    } else if (itemtype == 'N'&&(lowerOperate!="无"&&isNaN(lowerValue)||heightOperate!="无"&&isNaN(heightValue))){
                    	Ext.Msg.alert(gz.standard.tip,gz.standard.sdst.tiplexprNNaN);
                    	return
                    }

                    var data = new HashMap();
                    data.put("item", itemid);
                    data.put("item_id", item_id);
                    data.put("type", type);
                    data.put("lowerOperate", lowerOperate);
                    data.put("lowerValue", lowerValue);
                    data.put("heightOperate", heightOperate);
                    data.put("heightValue", heightValue);
                    data.put("middleValue", middleValue);
                    data.put("description", description);
                    data.put("isAccuratelyDay", isAccuratelyDay);

                    Rpc({
                        functionId: 'GZ00001213', success: function (result) {
                            var msg = Ext.decode(result.responseText);
                            if (msg.return_code == "success") {
                            	StructureDetail.secondGridStoreLoad();
                            	StructureDetail.echoGridPanelSelect();
                            	StructureDetail.operateDA = false;
                                Ext.getCmp('selectWindow').destroy();
                            } else {
                                Ext.Msg.alert(gz.standard.tip, gz.standard.sdst.addLexprFail);
                            }
                        }, scope: StandardStructure
                    }, data);
                }
            }, {
                text: gz.standard.cancel,
                margin: '0 0 3 20',
                handler: function () {
                    Ext.getCmp('selectWindow').destroy()
                }
            }]
        });
        if (StructureDetail.secondLevelSelectRecord.itemtype == 'D') {
        	selectWindow.query('#selectFieldset')[0].add(selectFieldsetCombo);
        } else {
        	selectWindow.query('#selectFieldset')[0].add(selectFieldsetText);
        	selectWindow.query('#selectCheckboxfield')[0].setHidden(true);
        	selectWindow.query('#selectFieldsetText')[0].setValue(StructureDetail.secondLevelSelectRecord.itemdesc);
        }
        selectWindow.show();
    },
    secondGridStoreLoad:function(){
    	var StructureDetail = this;
    	var secondGridStore = Ext.data.StoreManager.lookup(StructureDetail.prefix + 'secondGridStore');
        var param = new HashMap();
        param.put("item", StructureDetail.secondLevelSelectRecord.itemid);
        Rpc({
            functionId: 'GZ00001212', success: function (res) {
                var resData = Ext.decode(res.responseText);
                var return_code = resData.return_code;
                if (return_code == "success") {
                	StructureDetail.operateDA = true;
                    var storeData = resData.return_data;
                    var datadesc = {item_id: 'operate', description: ''};
                    storeData.push(datadesc);
                    secondGridStore.loadData(storeData);
                }
            }, scope: this, async: false
        }, param);
    },
    /**
     * 获取此页面结果
     */
    getResult: function () {
        var StructureDetail = this;
        var result = new HashMap();
        var factorValue = "";
        var sfactorValue = "";
        var factorContent = "";
        var factorItemId = "#" + StructureDetail.prefix + "firstfactorCombo";
        var sfactorItemId = "#" + StructureDetail.prefix + "secondfactorCombo";
        var factorCmp = StructureDetail.query("#" + StructureDetail.prefix + "firstcodeTreePanel")[0];
        var sfactorCodeCmp = StructureDetail.query("#" + StructureDetail.prefix + "secondcodeTreePanel")[0];
        var sfactorNDCmp = StructureDetail.query("#" + StructureDetail.prefix + "secondGridPanel")[0];
        var isSelectFactorItem = false; //是否选择一级指标项 默认为false
        var isSelectsFactorItem = false; //是否选择二级指标项 默认为false
        if (StructureDetail.viewType == 'create') {
            var factorValue = StructureDetail.query(factorItemId)[0].getValue().split("`")[0];
            var sfactorValue = StructureDetail.query(sfactorItemId)[0].getValue().split("`")[0];
            var key = "id";
            var factorCheckRecord = [];
            if (factorCmp) {
                factorCheckRecord = factorCmp.getChecked();
            }
            var sfactorCheckRecord = [];
            if (sfactorCodeCmp) {
                sfactorCheckRecord = sfactorCodeCmp.getChecked();
                Ext.each(sfactorCheckRecord, function (record) {
                    record.data.item_id = record.data.id;
                });
            } else if (sfactorNDCmp) {
                sfactorCheckRecord = sfactorNDCmp.getSelectionModel().getSelection();
                key = "item_id";
            }
            var factorContent = '';
            if (factorValue) {
                if (factorCheckRecord.length >= 1) {
                    isSelectFactorItem = true;
                }
                if (sfactorCheckRecord.length >= 1) {
                    isSelectsFactorItem = true;
                }
                for (var i = 0; i < factorCheckRecord.length; i++) {
                    var itemid = factorCheckRecord[i].get("id");
                    factorContent += itemid + '[';
                    for (var j = 0; j < sfactorCheckRecord.length; j++) {
                        var sitemid = sfactorCheckRecord[j].get("item_id");
                        factorContent += sitemid;
                        if (j < sfactorCheckRecord.length - 1) {
                            factorContent += ",";
                        }
                    }
                    factorContent += "]";
                    if (i < factorCheckRecord.length - 1) {
                        factorContent += ";";
                    }
                }
            } else if (sfactorValue) {
                if (sfactorCheckRecord.length >= 1) {
                    isSelectsFactorItem = true;
                }
                factorContent += "[";
                for (var i = 0; i < sfactorCheckRecord.length; i++) {
                    var itemid = sfactorCheckRecord[i].get(key);
                    factorContent += itemid;
                    if (i < sfactorCheckRecord.length - 1) {
                        factorContent += ",";
                    }
                }
                factorContent += "]";
            }
        } else if (StructureDetail.viewType == 'struct') {
            var selectFactorList = [];
            var relation = "";
            if (StructureDetail.prefix == 'horizontal') {
                factorValue = StructureDetail.standStructInfor.hfactor.split("`")[0];
                sfactorValue = StructureDetail.standStructInfor.s_hfactor.split("`")[0];
                relation = StructureDetail.standStructInfor.hrelation;
                selectFactorList = StructureDetail.standStructInfor.selecthFactorList;

            } else if (StructureDetail.prefix == 'vertical') {
                factorValue = StructureDetail.standStructInfor.vfactor.split("`")[0];
                sfactorValue = StructureDetail.standStructInfor.s_vfactor.split("`")[0];
                relation = StructureDetail.standStructInfor.vrelation;
                selectFactorList = StructureDetail.standStructInfor.selectvFactorList;
            }
            if (factorValue) {
                if (selectFactorList.length >= 1) {
                    isSelectFactorItem = true;
                }
                for (var i = 0; i < selectFactorList.length; i++) {
                    var factor = selectFactorList[i];
                    var key = factorValue + "_" + factor;
                    factorContent += factor + "[";
                    if(sfactorValue){
                        var sfactorArray = relation[key];
                        for (var j = 0; j < sfactorArray.length; j++) {
                            isSelectsFactorItem = true;
                            var sfactor = sfactorArray[j];
                            factorContent += sfactor;
                            if (j < sfactorArray.length - 1) {
                                factorContent += ",";
                            }
                        }
                    }
                    factorContent += "]";
                    if (i < selectFactorList.length - 1) {
                        factorContent += ";";
                    }
                }

            } else if (sfactorValue) {
                var key = "id";
                var selectList = [];
                if (StructureDetail.prefix == 'vertical') {
                    selectList = StructureDetail.standStructInfor.selectsvFactorList;
                } else if (StructureDetail.prefix == 'horizontal') {
                    selectList = StructureDetail.standStructInfor.selectshFactorList;
                }
                if (selectList.length >= 1) {
                    isSelectsFactorItem = true;
                }
                factorContent += "[";
                for (var i = 0; i < selectList.length; i++) {
                    var itemid = selectList[i];
                    factorContent += itemid;
                    if (i < selectList.length - 1) {
                        factorContent += ",";
                    }
                }
                factorContent += "]";
            }

        }
        result.put("factorItemId", factorValue);
        result.put("sfactorItemId", sfactorValue);
        result.put("factorContent", factorContent);
        result.put("isSelectFactorItem", isSelectFactorItem);
        result.put("isSelectsFactorItem", isSelectsFactorItem);
        return result;
    }
})
;
