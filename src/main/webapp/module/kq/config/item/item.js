//考勤项目主页面
Ext.define('kqConfigItem.item', {
    constructor: function (config) {
        kqConfigItem = this;
        var jsonStr = {type: 'list'};
        var map = new HashMap();
        map.put("jsonStr", jsonStr);
        Rpc({functionId: 'KQ00020401', async: false, success: kqConfigItem.init}, map);
    },
    init: function (form) {
        var result = Ext.decode(form.responseText);
        var returnStr = eval(result.returnStr);

        var return_data = returnStr.return_data;
        var data = return_data.list;
        kqConfigItem.FieldItemList=new Array();
        var q35List = return_data.q35list;
        var i=0;
        Ext.each(q35List,function (record) {
            if(record.id!=''&&record.id!=undefined&&record.name!=""&&record.name!=undefined) {
                kqConfigItem.FieldItemList[i] = record.id + ":" + record.name;
                i++;
            }
        });

        q35List.unshift({id:'-1',name:''});
        kqConfigItem.comboboxstore = Ext.create('Ext.data.Store', {
            fields: ['id', 'name'],
            data: [
                {"id": "0", "name": kq.label.no},
                {"id": "1", "name": kq.label.yes}
            ]
        });

        kqConfigItem.q35store = Ext.create('Ext.data.Store', {
            fields: ['id', 'name'],
            data: q35List
        });
        kqConfigItem.unitComboboxStore = Ext.create('Ext.data.Store', {
            fields: ['id', 'name'],
            data: [
                {"id": "01", "name": kq.label.hour},
                {"id": "02", "name": kq.label.day},
                {"id": "03", "name": kq.label.minute},
                {"id": "04", "name": kq.label.count}
            ]
        });

        kqConfigItem.symbolstore = Ext.create('Ext.data.Store', {
            fields: ['id', 'name'],
            data: [
                {"id": "α", "name": "α"},
                {"id": "β", "name": "β"},
                {"id": "Φ", "name": "Φ"},
                {"id": "Τ", "name": "Τ"},
                {"id": "⊙", "name": "⊙"},
                {"id": "√", "name": "√"},
                {"id": "×", "name": "×"},
                {"id": "＋", "name": "＋"},
                {"id": "－", "name": "－"},
                {"id": "★", "name": "★"},
                {"id": "☆", "name": "☆"},
                {"id": "▼", "name": "▼"},
                {"id": "▽", "name": "▽"},
                {"id": "◆", "name": "◆"},
                {"id": "◇", "name": "◇"},
                {"id": "●", "name": "●"},
                {"id": "○", "name": "○"},
                {"id": "■", "name": "■"},
                {"id": "□", "name": "□"},
                {"id": "▲", "name": "▲"},
                {"id": "△", "name": "△"}
            ]
        });
        var columns = [
            //主键
            {text: 'id', hidden: true, dataIndex: 'item_id', hideable: false},
            {
                text: common.label.name,
                width: 200, dataIndex: 'item_name', locked: true,
                renderer: function (value, meta, record) {
                    return value;
                }
            }, {
                text: kq.label.hasHoliday,
                sortable: false, width: 70, dataIndex: 'has_feast',
                renderer: function (value, meta, record) {
                    if (value == '1') {
                        value = kq.label.yes;
                    } else {
                        value = kq.label.no;
                    }
                    return value;
                },
                editor: {
                    xtype: "combobox",
                    store: kqConfigItem.comboboxstore,
                    editable: false,
                    queryMode: 'local',
                    displayField: 'name',
                    valueField: 'id'

                }
            }, {
                text: kq.label.hasPublicHoliday,
                sortable: false, width: 70, dataIndex: 'has_rest',
                renderer: function (value, meta, record) {
                    if (value == '1') {
                        value = kq.label.yes;
                    } else {
                        value = kq.label.no;
                    }
                    return value;
                },
                editor: {
                    xtype: "combobox",
                    store: kqConfigItem.comboboxstore,
                    editable: false,
                    queryMode: 'local',
                    displayField: 'name',
                    valueField: 'id'
                }
            }, {
                text:kq.label.calUnit,
                sortable: false, width: 70, dataIndex: 'item_unit',
                renderer: function (value, meta, record) {
                    if (value == '01') {
                        value = kq.label.hour;
                    } else if (value == '02') {
                        value = kq.label.day;
                    } else if (value == '03') {
                        value = kq.label.minute;
                    } else if (value == '04') {
                        value = kq.label.count;
                    }
                    return value;
                },
                editor: {
                    xtype: "combobox",
                    store: kqConfigItem.unitComboboxStore,
                    editable: false,
                    queryMode: 'local',
                    displayField: 'name',
                    valueField: 'id'
                }
            }, {
                text: kq.label.symbol,
                sortable: false, width: 70, dataIndex: 'item_symbol', align: 'center',
                editor: {
                    xtype: "combobox",
                    store: kqConfigItem.symbolstore,
                    maxLength:10,
                    queryMode: 'local',
                    displayField: 'name',
                    valueField: 'id'
                }
            }, {
                text: kq.label.color,
                sortable: false, width: 100, dataIndex: 'item_color',
                renderer: function (value, meta, record) {
                    var id = record.data.item_id;
                    if (value.substring(0, 1) != '#') {
                        value = '#' + value;
                    }

                    return "<div style='width: 100%;height:100%;background-color:" + value + "' >&nbsp</div>";
                },
                editor: {
                    xtype: 'colorPickField',
                    editable: false
                }
            },  {
                text: kq.label.resultField,
                sortable: false, width: 100, dataIndex: 'fielditemid',
                renderer: function (value, meta, record) {
                    Ext.each(q35List, function (t) {
                        if (t.id == value) {
                            value = t.name;
                        }
                    });
                    return value;
                },
                editor: {
                    xtype: "combobox",
                    store: kqConfigItem.q35store,
                    editable: false,
                    queryMode: 'local',
                    displayField: 'name',
                    valueField: 'id',
                    tpl: Ext.create('Ext.XTemplate',
                        '<ul class="x-list-plain"><tpl for=".">',
                        '<li role="option" class="x-boundlist-item" style="height: 22px">{name}</li>',
                        '</tpl></ul>')
                }
            }, {// 是否统计项
                text: kq.label.enableModify,
                sortable: false, width: 80, dataIndex: 'item_type',
                renderer: function (value, meta, record) {
                	var item_id = record.data.item_id;
                	var checked = '';
             		if('1' == value){
             			checked = 'checked=checked';
             		}
             		return '<div style="text-align:center;">'
             				+ '<input name="item_type'+item_id+'" style="cursor:pointer;" type="checkbox" '+checked
             				+ ' onclick=javascript:kqConfigItem.updataItemTypeFunc(this,"'+item_id+'"); /></div>';
                }
            }, {
                text: kq.label.sort,
                sortable: false, width: 70, dataIndex: 'computerorder',
                renderer: function (value, meta, record) {
                    return value;
                },
                editor: {
                    xtype: 'numberfield',
                    maxValue:999999999,
                    minValue:-999999999,
                    mouseWheelEnabled: false,//去掉鼠标滑轮增减
                }
            }, {
                text: kq.label.rules,
                sortable: false, width: 70, dataIndex: 'c_expr', align: 'center',
                renderer: function (value, meta, record) {
                    var item_id = record.data.item_id;
                    // 61649 计算公式内容转码问题
                    var c_expr = record.data.c_expr;
                    var fielditem_type = record.data.fielditem_type;
                    var html = '<a href="####" onclick="kqConfigItem.showFormulaWindow(\'' + item_id + '\',\'' + c_expr + '\',\''+fielditem_type+'\')">';
                    if (Ext.isEmpty(value)) {
                        html += '<img id=img_' + item_id + ' src="/images/add.gif" border=0></a>';
                    } else {
                        html += '<img id=img_' + item_id + ' src="/images/new_module/formula.gif" border=0></a>';
                    }
                    return html;
                }
            },{
                text: kq.label.importItem,
                sortable: false, 
                width: 70, 
                dataIndex: 'other_param', 
                align: 'center',
                renderer: function (value, meta, record) {
                    var item_id = record.data.item_id;
                    var fielditemid = record.data.fielditemid;
                    var display = fielditemid ? "" : "none";
                    var html = '<a id="a_' + item_id + '" href="####" style="display:' + display + ';"';
                    html += ' onclick="kqConfigItem.importField(\'' + item_id + '\',\'' + fielditemid + '\')">';
                    if(Ext.isEmpty(value))
                    	html += '<img id=img_import_' + item_id + ' src="/images/add.gif" border=0></a>';
                    else
                    	html += '<img id=img_import_' + item_id + ' src="/images/new_module/formula.gif" border=0></a>';                    	
                    return html;
                }
            }
        ];
        var store = Ext.create('Ext.data.Store', {
            storeId: 'itemMainStore',
            fields: ['item_id', 'item_name', 'has_feast', 'has_rest',
                'item_symbol', 'item_color', 'item_type', 'c_expr', 'computerorder'],
            data: data
        });
        var dataPanel = Ext.create('Ext.grid.Panel', {
            id: 'itemMainPanel',
            title: kq.label.kqItem,
            store: store,
            bodyStyle: 'z-index:2;',
            margin:'0 0 30 0',
            enableLocking: true,
            columnLines: true,
            rowLines: true,
            sortableColumns: false,
            bufferedRenderer: false,
            columns: columns,
            viewConfig: {
                plugins: {
                    ptype: 'gridviewdragdrop',
                    dragText: kq.shifts.sort.title
                },
                listeners: {
                    'drop': function (node, data, model, dropPosition) {
                        var panel = Ext.getCmp("itemMainPanel");
                        if (data.records.length > 1) {
                            Ext.showAlert(gz.msg.forbidMultiMove);
                            panel.getStore().load();
                        } else {
                            var ori_id = data.records[0].get("item_id");
                            var ori_seq = data.records[0].get('displayorder');
                            var to_id = model.get('item_id');
                            var to_seq = model.get('displayorder');
                            var jsonStr = {
                                type: 'move',
                                ori_id: ori_id,
                                ori_seq: ori_seq,
                                to_id: to_id,
                                to_seq: to_seq,
                                dropPosition: dropPosition,
                            };
                            var map = new HashMap();
                            map.put("jsonStr", jsonStr);
                            Rpc({
                                functionId: 'KQ00020401', success: function (response, action) {
                                    var result = Ext.decode(response.responseText);
                                    if (!result.succeed) {
                                        panel.getStore().load();
                                        Ext.showAlert(gz.msg.moveFalse);
                                    }
                                    else {
                                        var result = Ext.decode(form.responseText);
                                        var returnStr = eval(result.returnStr);

                                        var return_data = returnStr.return_data;
                                        var data = return_data.list;

                                        var store = panel.getStore();
                                        for (var i = 0; i < data.length; i++) {
                                            store.findRecord('item_id', data[i].item_id).set('displayorder', data[i].displayorder);
                                        }
                                        store.commitChanges();
                                    }
                                }
                            }, map);
                        }
                    }
                }
            },
            plugins: [
                Ext.create('Ext.grid.plugin.CellEditing', {
                    clicksToEdit: 1,
                    listeners: {
                        'edit': function (editor, context, eOpts) {
                            var recordData = context.record.data;
                            if(context.originalValue!=context.value) {
                                kqConfigItem.saveData(recordData.item_id, context.field, context.value,context.originalValue);
                                var item_id = context.record.data.item_id;
                                var fielditemid = context.record.data.fielditemid;
                                var a_import = document.getElementById("a_" + item_id);
                                if(a_import) {
                                	if(fielditemid && "-1" != fielditemid)
                                		a_import.style.display = "";
                                	else
                                		a_import.style.display = "none";
                                }
                            }
                        }
                    }
                })
            ]
            // ,
            // tbar: [
            //     {
            //         xtype: 'button',
            //         text: '新增',
            //         style: 'margin-right:5px',
            //         handler: function () {
            //
            //         }
            //     }, {
            //         xtype: 'button',
            //         text: '删除',
            //         style: 'margin-right:5px',
            //         handler: function () {
            //             kqConfigItem.deleteItems();
            //         }
            //     }
            //
            // ]
        });


        var mainPanel = Ext.create('Ext.container.Viewport', {
            layout: 'fit',
            items: [dataPanel]
        });

    },
    saveData: function (kq_itemid, column_id, value,originalValue) {
        var jsonStr = {type: 'save', kq_itemid: kq_itemid, column_id: column_id, value: value};
        var map = new HashMap();
        var store=Ext.StoreMgr.get('itemMainStore');
        var flag=false;
        map.put("jsonStr", jsonStr);
        Rpc({
            functionId: 'KQ00020401', async: false, success: function (form) {
                var result = Ext.decode(form.responseText);
                var returnStr = eval(result.returnStr);
                if(returnStr.return_code=='success') {
                    Ext.StoreMgr.get('itemMainStore').commitChanges();
                    flag=true;
                }else{
                	flag=false;
                	alert(returnStr.return_msg);
                }

            }
        }, map);
        if (!flag) {
        	Ext.each(store.data.items, function (e) {
        		if (e.data.item_id == kq_itemid) {
        			e.set("fielditemid",originalValue);
        		}
        	});
		}        	
    },
    /**
     * 计算规则
     */
    showFormulaWindow: function (item_id, c_expr, fielditem_type) {
        var map = new HashMap();
        map.put("item_id", item_id);
        // 61649 计算公式内容转码问题
        c_expr = decode(c_expr);
        
        Ext.require('EHR.complexcondition.ComplexCondition', function () {
            Ext.create("EHR.complexcondition.ComplexCondition", {
                dataMap: map,
                imodule: "11",
                opt: "0",
                itemtype: fielditem_type,
                title: kq.label.computeFormula,
                callBackfn: "kqConfigItem.saveFormula",
                fieldItem_List:kqConfigItem.FieldItemList,
                formula: c_expr
            });
        });
    },
    saveFormula: function (c_expr, heapFlag, initflag, fieldid, dataMap) {
        var item_id = dataMap.item_id;
        if (Ext.isEmpty(c_expr)) {
            document.getElementById("img_" + item_id).src = "/images/add.gif";
        } else {
            document.getElementById("img_" + item_id).src = "/images/new_module/formula.gif";
        }
        
        kqConfigItem.saveData(item_id, "c_expr", c_expr,'');
    },
    /**
     * 导入指标
     */
    importField: function (kq_itemid,fielditemid){
    	var map = new HashMap();
        var jsonStr = {type: 'searchImportField', kq_itemid: kq_itemid};
        var map = new HashMap();
        map.put("jsonStr", jsonStr);
        Rpc({functionId: 'KQ00020401', async: false, success: kqConfigItem.showImportWin}, map);
    },    
    /**
     * 导入指标窗口
     */
    showImportWin: function (response){
    	var result = Ext.decode(response.responseText);
    	var returnStr = result.returnStr;
    	if("fail" == returnStr.return_code) {
    		Ext.showAlert(returnStr.return_msg);
    		return;
    	}
    	
    	var countItemId = returnStr.countItemId;
    	var win = Ext.getCmp("importWin");
    	if(win)
    		win.close();
    	
    	var kq_itemid = returnStr.kq_itemid;
    	var fieldSetComboBoxStore = Ext.create('Ext.data.Store', {
            id: 'fieldSetListStore',
            fields: ['fieldSetId', 'fieldSetDesc'],
            data: returnStr.fieldSetList
        });
		
		var fieldSetComboBox = Ext.create('Ext.form.field.ComboBox', {
			id:'fieldSetComboId',
            fieldLabel: kq.label.importSub,
            labelSeparator: '',
            labelAlign:'right',
            labelWidth:60,
            store: fieldSetComboBoxStore,
            width: 300,
            forceSelection: true,
            valueField: 'fieldSetId',
            displayField: 'fieldSetDesc',
            shadow: false,
            editable: false,
            allowBlank: false,
            value:returnStr.fieldSetId,
            cls: 'comboxStyle',
            listeners: {
            	select:function (combo, record) {
            		var map = new HashMap();
                    var jsonStr = {type: 'searchImportItems', fieldSetId: record.data.fieldSetId,fieldItemId:countItemId};
                    var map = new HashMap();
                    map.put("jsonStr", jsonStr);
                    Rpc({functionId: 'KQ00020401', async: false, success:function (from){
                    	var re = Ext.decode(from.responseText);
                    	var reStr = re.returnStr;
                    	if("fail" == reStr.return_code) {
                    		Ext.showAlert(reStr.return_msg);
                    		return;
                    	}
                    	
                    	var fieldItemStore = Ext.create('Ext.data.Store', {
                            id: 'fieldListStore',
                            fields: ['itemId', 'itemDesc'],
                            data: reStr.fieldItemList
                        });
                    	
                    	Ext.getCmp("fieldComboBoxId").setStore(fieldItemStore);
                    	Ext.getCmp("fieldComboBoxId").setValue("#");
                    	var formDateStore = Ext.create('Ext.data.Store', {
                            id: 'startDateStore',
                            fields: ['itemId', 'itemDesc'],
                            data: reStr.dateItemList
                        });
                    	
                    	Ext.getCmp("fromDate").setStore(formDateStore);
                    	Ext.getCmp("fromDate").setValue("#");
                    	
                    	var endStore = Ext.create('Ext.data.Store', {
                            id: 'endDateStore',
                            fields: ['itemId', 'itemDesc'],
                            data: reStr.dateItemList
                        });
                    	
                    	Ext.getCmp("endDate").setStore(endStore);
                    	Ext.getCmp("endDate").setValue("#");
                    }}, map);
            	}
            }
        });
    	
		var fieldComboBoxStore = Ext.create('Ext.data.Store', {
            id: 'fieldListStore',
            fields: ['itemId', 'itemDesc'],
            data: returnStr.fieldItemList
        });
		
		var fieldComboBox = Ext.create('Ext.form.field.ComboBox', {
			id:'fieldComboBoxId',
            fieldLabel: kq.label.importFieldItem,
            labelSeparator: '',
            labelAlign:'right',
            labelWidth:60,
            store: fieldComboBoxStore,
            width: 300,
            forceSelection: true,
            valueField: 'itemId',
            displayField: 'itemDesc',
            shadow: false,
            editable: false,
            allowBlank: false,
            value:returnStr.fieldItemId,
            cls: 'comboxStyle'
        });
		
		var startDateStore = Ext.create('Ext.data.Store', {
            id: 'startDateStore',
            fields: ['itemId', 'itemDesc'],
            data: returnStr.dateItemList
        });
		
		var startDate = Ext.create('Ext.form.field.ComboBox', {
			id: 'fromDate',
			fieldLabel: kq.label.startDate,
			labelSeparator: '',
            labelAlign:'right',
            labelWidth:60,
            store: startDateStore,
            width: 300,
            forceSelection: true,
            valueField: 'itemId',
            displayField: 'itemDesc',
            shadow: false,
            editable: false,
            allowBlank: false,
            value:returnStr.beginDate,
            cls: 'comboxStyle'
		});
		
		var endDateStore = Ext.create('Ext.data.Store', {
            id: 'endDateStore',
            fields: ['itemId', 'itemDesc'],
            data: returnStr.dateItemList
        });
		
		var endDate = Ext.create('Ext.form.field.ComboBox', {
	        id: 'endDate',
	        fieldLabel: kq.label.endDate,
	        labelSeparator: '',
            labelAlign:'right',
            labelWidth:60,
            store: endDateStore,
            width: 300,
            forceSelection: true,
            valueField: 'itemId',
            displayField: 'itemDesc',
            shadow: false,
            editable: false,
            allowBlank: false,
            value:returnStr.endDate,
            cls: 'comboxStyle'
		});
		var redStyle ={
				xtype:'label',
				width:10,
				margin:'5 0 0 5',
				html:'<div style="color:red;">*</div>'
			};
		var nullStyle ={
				xtype:'label',
				width:10,
				margin:'5 0 0 5',
				html:''
			};
    	win = Ext.create('Ext.window.Window', {
    		title: kq.label.importItem,
    		height: 250,
    		width: 400,
    		modal: true,
    		layout: {
    			align: 'middle',
		        pack: 'center',
		        type: 'vbox'
    		},
    		items: [{
	    			border: false,
			        layout: 'hbox',
	    			items: [
	    				fieldSetComboBox,
	    				redStyle
	    				]
    			},{
	    			border: false,
			        layout: 'hbox',
			        margin:'5 0 0 0',
	    			items: [
	    				fieldComboBox,
						redStyle
					]
    			},{
        			border: false,
    		        layout: 'hbox',
    		        margin:'5 0 0 0',
        			items: [
        				startDate,
        				nullStyle
        				]
    			},{
        			border: false,
    		        layout: 'hbox',
    		        margin:'5 0 0 0',
        			items: [
        				endDate,
        				nullStyle
    					]
        			}
    			],
    		buttonAlign:'center',
    		buttons: [{ 
    			text: kq.button.ok,
		    	handler:function(){
		    		var fieldSetId = Ext.getCmp("fieldSetComboId").getValue();
		    		var itemId = Ext.getCmp("fieldComboBoxId").getValue();
		    		var startDate = Ext.getCmp("fromDate").getValue();
		    		var endDate = Ext.getCmp("endDate").getValue();
		    		
		    		if(!fieldSetId) {
		    			Ext.showAlert(kq.importMsg.fieldSetIsEmptyText);
		    			return;
		    		}
		    		
		    		if(fieldSetId && fieldSetId != "#" && (!itemId || itemId == "#")) {
		    			Ext.showAlert(kq.importMsg.fieldItemIsEmptyText);
		    			return;
		    		}
		    		
		    		var map = new HashMap();
                    var jsonStr = {
                    		type: 'saveImportParam',
                    		fieldSetId: fieldSetId,
                    		itemId: itemId,
                    		startDate: startDate,
                    		endDate: endDate,
                    		kq_itemid:kq_itemid
                    };
                    var map = new HashMap();
                    map.put("jsonStr", jsonStr);
                    Rpc({functionId: 'KQ00020401', async: false, success:function (from){
                    	var re = Ext.decode(from.responseText);
                    	var reStr = re.returnStr;
                    	if("fail" == reStr.return_code) {
                    		Ext.showAlert(returnStr.return_msg);
                    		return;
                    	} else 
                    		Ext.showAlert(kq.label.saveSuccess);
                    	
                    	win.close();
                    	// 更新标识图片
                    	if(Ext.isEmpty(fieldSetId) || "#" == fieldSetId){
                    		document.getElementById("img_import_" + kq_itemid).src = "/images/add.gif";
                        } else {
                        	document.getElementById("img_import_" + kq_itemid).src = "/images/new_module/formula.gif";
                    	}
                    }}, map);
		    	}
    		},{
    			text: kq.button.cancle,
		    	handler:function(){
		    		win.close();
			    }	
    		}]
    	});
    	
    	win.show();
    },
    // 更改是否统计
    updataItemTypeFunc:function(input, item_id){
    	var value = '0';
    	if(input.checked)
			value = '1';
    	kqConfigItem.saveData(item_id, "item_type", value,'');
 	}
    //添加 删除方法，本版不需要，暂时屏蔽
    // ,
    // deleteItems: function () {
    //     var selection = Ext.getCmp('itemMainPanel').getSelection();
    //     var itemid = '';
    //     Ext.each(selection, function (record) {
    //         itemid += record.item_id + ",";
    //     });
    //
    //     var jsonStr = {type: 'deleteItem', kq_itemid: itemid};
    //     var map = new HashMap();
    //     map.put("jsonStr", jsonStr);
    //     Rpc({
    //         functionId: 'KQ00020401', async: false, success: function (form) {
    //             var result = Ext.decode(form.responseText);
    //             var returnStr = eval(result.returnStr);
    //             Ext.StoreMgr.get('itemMainStore').reload();
    //
    //         }
    //     }, map);
    // },
    // showAddWindow: function () {
    //
    //     var dataPanel = Ext.create('Ext.form.Panel', {
    //         id: 'formp',
    //         padding: '10 10 0 10',
    //         border: false,
    //         items: [
    //             {
    //                 xtype: 'container',
    //                 width: '100%',
    //                 height: 35,
    //                 layout: 'hbox',
    //                 items: [
    //                     {
    //                         xtype: 'hiddenfield',
    //                         name: 'encrypt_item_id',//班次编号加密
    //                         value: ''
    //                     },
    //                     {
    //                         xtype: 'textfield',
    //                         name: 'name',
    //                         fieldLabel: '名称<font style="color:#FF3330;margin-top:3px;">*</font>',
    //                         emptyText: '请输入项目名称',
    //                         maxLength: 50,
    //                         labelWidth: 40,
    //                         width: 280,
    //                         margin: '0 20 0 0',
    //                         allowBlank: false
    //                     },
    //                     {
    //                         xtype: 'textfield',
    //                         name: 'abbreviation',
    //                         fieldLabel: '简称<font style="color:#FF3330;margin-top:3px;">*</font>',
    //                         emptyText: '请输入班次简称',
    //                         maxLength: 50,
    //                         labelAlign: 'right',
    //                         allowBlank: false,
    //                         width: 300,
    //                         labelWidth: 60
    //                     }
    //                 ]
    //             }, {
    //                 xtype: 'container',
    //                 height: 35,
    //                 width: '100%',
    //                 layout: 'hbox',
    //                 items: [
    //                     {
    //                         xtype: 'label',
    //                         text: "颜色",
    //                         width: 40,
    //                         margin: '3 5 0 0'
    //                     }, {
    //                         id: 'colorValue',
    //                         xtype: 'hiddenfield',
    //                         name: 'color',
    //                         value: '#FFCC00'
    //                     }, {
    //                         xtype: 'colorpicker',
    //                         id: 'colorpicker',
    //                         floating: true,
    //                         hidden: true,
    //                         x: 45,
    //                         y: 22,
    //                         listeners: {
    //                             select: function (picker, color) {
    //                                 var colorDiv = Ext.getDom("colorDiv");
    //                                 colorDiv.style.backgroundColor = "#" + color;
    //                                 picker.setHidden(true);
    //                                 var colorValue = Ext.getCmp("colorValue");
    //                                 if (colorValue)
    //                                     colorValue.setValue("#" + color);
    //                             }
    //                         }
    //                     },
    //                     {
    //                         xtype: 'container',
    //                         id: 'colorDiv',
    //                         style: 'background-color:#FFCC00;cursor:pointer;border-style:solid;border-color:#B5B8C8;',
    //                         width: 60,
    //                         height: 20,
    //                         padding: '1 1 1 1',
    //                         margin: '2 0 0 0',
    //                         border: true,
    //                         listeners: {
    //                             click: {
    //                                 element: 'el',
    //                                 //打开选色板
    //                                 fn: function (a, o) {
    //                                     var colorPicker = Ext.getCmp("colorpicker");
    //                                     colorPicker.setHidden(!colorPicker.hidden);
    //                                 }
    //                             }, afterrender: function () {
    //                                 Ext.get("addShitWin").on("click", function (e) {
    //                                     e = e || window.event;
    //                                     var target = e.target || e.srcElement;
    //                                     var colorDiv = Ext.getCmp("colorDiv");
    //                                     if (target.id.indexOf("colorDiv") < 0) {
    //                                         var colorPicker = Ext.getCmp("colorpicker");
    //                                         if (!colorPicker.hidden) {
    //                                             colorPicker.setHidden(true);
    //                                         }
    //                                     }
    //                                 });
    //                             }
    //                         }
    //                     }, {
    //                         xtype: 'combo',
    //                         fieldLabel: '符号',
    //                         name: 'symbol',
    //                         editable: true,
    //                         margin: '0 0 0 5',
    //                         labelWidth: 60,
    //                         maxLength: 3,
    //                         labelAlign: 'right',
    //                         width: 170,
    //                         valueField: "displayVal",
    //                         displayField: "displayVal",
    //                         store: Ext.data.StoreManager.lookup("symbolStore")
    //                     },
    //                     {
    //                         xtype: 'codecomboxfield',
    //                         fieldLabel: '统计属性',
    //                         codesetid: '85',
    //                         name: 'statistics_type',
    //                         margin: '0 0 0 20',
    //                         inputable: false,
    //                         labelWidth: 60,
    //                         labelAlign: 'right',
    //                         width: 300
    //                     }
    //                 ]
    //             }, {
    //                 xtype: 'container',
    //                 height: 35,
    //                 width: '100%',
    //                 layout: 'hbox',
    //                 items: [
    //                     {
    //                         xtype: 'combo',
    //                         id: 'domain_count',
    //                         fieldLabel: '一天上下班次数',
    //                         name: 'domain_count',
    //                         editable: false,
    //                         labelWidth: 100,
    //                         width: 280,
    //                         valueField: "value",
    //                         displayField: "name",
    //                         store: Ext.data.StoreManager.lookup("domainCountStore"),
    //                         listeners: {
    //                             afterrender: function (combo) {
    //                                 var store = combo.getStore();
    //                                 if (store.getCount() > 0) {
    //                                     var record = store.getAt(0);
    //                                     combo.setValue(record.get("value"))
    //                                     combo.fireEvent("select", combo, record);
    //                                 }
    //                             },
    //                             select: function (combo, record) {
    //                                 var ondutys = Ext.getCmp("ondutys");
    //                                 if (ondutys.hidden) {
    //                                     ondutys.setHidden(false);
    //                                 }
    //                                 var value = record.get("value");
    //                                 if (value == '1') {
    //                                     ondutys.setHeight(64);
    //                                 } else if (value == "2") {
    //                                     ondutys.setHeight(95);
    //                                 } else if (value == '3') {
    //                                     ondutys.setHeight(126);
    //                                 }
    //                                 var store = Ext.data.StoreManager.lookup("timeStore" + value);
    //                                 ondutys.setStore(store);
    //                             }
    //                         }
    //                     },
    //                     {
    //                         xtype: 'textfield',
    //                         name: 'work_hours',
    //                         fieldLabel: '时长',
    //                         margin: '0 0 0 20',
    //                         labelWidth: 60,
    //                         emptyText: '请输入工作时长（小时）',
    //                         labelAlign: 'right',
    //                         width: 300,
    //                         validator: function (value) {
    //                             if (value.length > 0) {
    //                                 var fval = parseFloat(value);
    //                                 if (isNaN(fval)) {
    //                                     return "请输入数值!";//0到24之间
    //                                 }
    //                                 if (0 > fval || fval > 24) {
    //                                     return "请输入0到24之间的数值!";
    //                                 }
    //                             }
    //                             return true;
    //                         }
    //                     }
    //                 ]
    //             }, {
    //                 xtype: 'grid',
    //                 id: 'ondutys',
    //                 store: Ext.data.StoreManager.lookup("timeStore"),
    //                 disableSelection: true,
    //                 sortableColumns: false,
    //                 width: 176,
    //                 hidden: true,
    //                 scroll: false,
    //                 margin: '0 0 0 105',
    //                 columns: [
    //                     {
    //                         text: '起始时间', dataIndex: 'startTime', width: 88, menuDisabled: true,
    //                         align: 'center',
    //                         editor: {
    //                             xtype: 'timefield',
    //                             format: "H:i",
    //                             pickerMaxHeight: 150,
    //                             minValue: '00:00',
    //                             maxValue: '23:59',
    //                             invalidText: '时间格式错误：{0}',
    //                             increment: 5
    //                         }
    //                     },
    //                     {
    //                         text: '结束时间', dataIndex: 'endTime', width: 88, menuDisabled: true,
    //                         align: 'center',
    //                         editor: {
    //                             xtype: 'timefield',
    //                             format: "H:i",
    //                             pickerMaxHeight: 150,
    //                             invalidText: '时间格式错误：{0}',
    //                             minValue: '00:00',
    //                             maxValue: '23:59',
    //                             increment: 5
    //                         }
    //                     }
    //                 ],
    //                 plugins: [Ext.create('Ext.grid.plugin.CellEditing', {
    //                     clicksToEdit: 1,
    //                     listeners: {
    //                         edit: function (editor, e) {
    //                             if (!e.value || e.value.length == 0) {
    //                                 e.cancel = true;
    //                                 return;
    //                             }
    //                             var time = Ext.Date.format(e.value, "H:i");
    //                             e.record.set(e.field, time);
    //                             // e.record.commit();
    //                         }
    //                     }
    //                 })],
    //             }, {
    //                 xtype: 'codecomboxfield',
    //                 size: 50,
    //                 width: 280,
    //                 labelWidth: 55,
    //                 margin: "10 0 0 0",
    //                 name: 'b0110',
    //                 onlySelectCodeset: false,
    //                 codesetid: "UM",
    //                 emptytext: "请选择所属机构",
    //                 ctrltype: '3',
    //                 nmodule: "11",
    //                 fieldLabel: "所属机构<font style='color:#FF3330;margin-top:3px;'>*</font>",
    //                 allowBlank: false,
    //                 editable: false,
    //                 listeners: {
    //                     afterrender: function (combo) {
    //                         var map = new HashMap();
    //                         var json = {};
    //                         json.type = "getPriv";
    //                         map.put("jsonStr", Ext.encode(json));
    //                         Rpc({
    //                             functionId: 'KQ00020401', async: false, success: function (form) {
    //                                 var res = Ext.decode(form.responseText);
    //                                 var data = Ext.decode(res.returnStr);
    //                                 combo.setValue(data.b0110);
    //                             }, scope: this
    //                         }, map);
    //                     }
    //                 }
    //             }, {
    //                 xtype: 'textarea',
    //                 fieldLabel: '备注',
    //                 labelWidth: 50,
    //                 name: 'remarks',
    //                 emptyText: '请输入班次描述信息',
    //                 width: 600,
    //                 height: 70,
    //                 margin: '10 0 0 0'
    //             }, {
    //                 xtype: 'hiddenfield',
    //                 id: 'validateFld',
    //                 name: 'is_validate',
    //                 value: 1
    //             },
    //             {
    //                 xtype: 'container',
    //                 width: '100%',
    //                 border: false,
    //                 margin: '5 0 0 0',
    //                 items: [{
    //                     xtype: 'label',
    //                     text: '启用'
    //                 }, {
    //                     xtype: 'image',
    //                     src: '../../../../module/kq/images/kq_on.png',
    //                     id: 'validate_1',
    //                     style: 'position:relative;top:4px;margin-left:29px;cursor:pointer;',
    //                     width: 50,
    //                     listeners: {
    //                         click: {
    //                             element: 'el',
    //                             //停用
    //                             fn: function (a, o) {
    //                                 var validate_1 = Ext.getCmp("validate_1");
    //                                 if (validate_1) {
    //                                     validate_1.setHidden(true);
    //                                 }
    //                                 var validate_0 = Ext.getCmp("validate_0");
    //                                 if (validate_0) {
    //                                     validate_0.setHidden(false);
    //                                 }
    //                                 var validateFld = Ext.getCmp("validateFld");
    //                                 if (validateFld) {
    //                                     validateFld.setValue(0);
    //                                 }
    //
    //                             }
    //                         }
    //                     }
    //                 }, {
    //                     xtype: 'image',
    //                     hidden: true,
    //                     id: 'validate_0',
    //                     src: '../../../../module/kq/images/kq_off.png',
    //                     style: 'position:relative;top:4px;margin-left:29px;cursor:pointer;',
    //                     width: 50,
    //                     listeners: {
    //                         click: {
    //                             element: 'el',
    //                             //启用
    //                             fn: function (a, o) {
    //                                 var validate_1 = Ext.getCmp("validate_1");
    //                                 if (validate_1) {
    //                                     validate_1.setHidden(false);
    //                                 }
    //                                 var validate_0 = Ext.getCmp("validate_0");
    //                                 if (validate_0) {
    //                                     validate_0.setHidden(true);
    //                                 }
    //                                 var validateFld = Ext.getCmp("validateFld");
    //                                 if (validateFld) {
    //                                     validateFld.setValue(1);
    //                                 }
    //                             }
    //                         }
    //                     }
    //                 }]
    //             }
    //
    //         ]
    //
    //
    //     });
    // }
});