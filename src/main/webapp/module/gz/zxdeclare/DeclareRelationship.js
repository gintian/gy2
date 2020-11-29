Ext.define('Declare.DeclareRelationship', {
    extend: 'Ext.window.Window',
    width: 440,
    height: 400,
    resizable: false,
    requires: ['EHR.fielditemselector.FieldItemSelector'],
    title: gz.label.zxdeclare.declareRelationshipTitle,
    initComponent: function () {
        this.callParent();
        this.init();//创建gridpanel对象
    },
    init: function () {
        var me = this;
        var map = new HashMap();
        map.put("operateType", "getRelation");
        Rpc({
            functionId: 'GZ00000701', success: function (form) {
                var return_data = Ext.decode(form.responseText).return_data;
                me.creteGridPanel(return_data);
                me.createButton();
            }
        }, map);
    },
    createButton: function () {
        var me = this;
        var saveButton = Ext.create('Ext.button.Button', {
            text: gz.button.savaRelation,
            height: 30,
            width: 100,
            margin: '20 0 0 150',
            listeners: {
                click: function () {
                    var dataArray = [];
                    var gridStore = this.ownerCt.query('gridpanel')[0].getStore();
                    var count = gridStore.getCount();
                    for (var i = 0; i < count; i++) {
                        var fieldData = gridStore.getAt(i).data;
                        var fieldObject = {};
                        fieldObject.fieldsetid = fieldData.fieldsetid ? fieldData.fieldsetid : '';
                        fieldObject.itemid = fieldData.itemid ? fieldData.itemid : '';
                        fieldObject.sourceField = fieldData.sourceField ? fieldData.sourceField : '';
                        fieldObject.fielditemidesc = fieldData.fielditemidesc ? fieldData.fielditemidesc : '';
                        dataArray.push(fieldObject);
                    }
                    var map = new HashMap();
                    map.put("operateType", "saveRelation");
                    map.put("fieldsArray", dataArray);
                    Rpc({
                        functionId: 'GZ00000701', success: function (form) {
                            var return_data = Ext.decode(form.responseText).return_data;
                            if (return_data.saveFlag == "success") {
                                Ext.Msg.alert(gz.msg.zxDeclareTitle, gz.label.zxdeclare.saveSuccess, function () {
                                    me.close();
                                });
                            } else {
                                Ext.Msg.alert(gz.msg.zxDeclareTitle, gz.label.zxdeclare.saveFail, function () {
                                    me.close();
                                });
                            }
                        }
                    }, map);

                }
            }
        });
        this.add(saveButton);
    },
    creteGridPanel: function (return_data) {
        var gridData = [
            {
                displayname: gz.label.zxdeclare.taxpayerIDType,
                fielditemidesc: gz.label.zxdeclare.select,
                fieldsetid: '',
                itemid: '',
                sourceField: 'taxpayerIDType'
            },
            {
                displayname: gz.label.zxdeclare.taxpayerIDNumber,
                fielditemidesc: gz.label.zxdeclare.select,
                fieldsetid: '',
                itemid: '',
                sourceField: 'taxpayerIDNumber'
            },
            {
                displayname: gz.label.zxdeclare.taxpayerIdentificationNumber,
                fielditemidesc: gz.label.zxdeclare.select,
                fieldsetid: '',
                itemid: '',
                sourceField: 'taxpayerIdentificationNumber'
            },
            {
                displayname: gz.label.zxdeclare.contactAddress,
                fielditemidesc: gz.label.zxdeclare.select,
                fieldsetid: '',
                itemid: '',
                sourceField: 'contactAddress'
            },
            {
                displayname: gz.label.zxdeclare.spouseSituation,
                fielditemidesc: gz.label.zxdeclare.select,
                fieldsetid: '',
                itemid: '',
                sourceField: 'spouseSituation'
            },
            {
                displayname: gz.label.zxdeclare.spouseName,
                fielditemidesc: gz.label.zxdeclare.select,
                fieldsetid: '',
                itemid: '',
                sourceField: 'spouseName'
            },
            {
                displayname: gz.label.zxdeclare.spouseIdType,
                fielditemidesc: gz.label.zxdeclare.select,
                fieldsetid: '',
                itemid: '',
                sourceField: 'spouseIdType'
            },
            {
                displayname: gz.label.zxdeclare.spouseIdNumber,
                fielditemidesc: gz.label.zxdeclare.select,
                fieldsetid: '',
                itemid: '',
                sourceField: 'spouseIdNumber'
            }
        ];
        var data;
        if (return_data.relation) {
            data = Ext.decode(return_data.relation);
            for (var i = 0; i < data.length; i++) {
                var columnObject = data[i];
                for (var s = 0; s < gridData.length; s++) {
                    var gridObject = gridData[s];
                    if (gridObject) {//加此判断是因为ie在兼容模式下会把protype原型也算在数组长度里
                        if (columnObject.sourceField == gridObject.sourceField) {
                            gridObject.fieldsetid = columnObject.fieldSetId;
                            gridObject.itemid = columnObject.itemId;
                            gridObject.fielditemidesc = columnObject.fielditemidesc;
                        }
                    }
                }
            }
        }
        //在ie兼容模式下js代码一定不要有多余的逗号！！！！！否则会解析出错
        Ext.define('relationModel', {
            extend: 'Ext.data.Model',
            fields: ['displayname', 'fielditemidesc', 'fieldsetid', 'itemid', 'sourceField']
        });
        var gridStore = Ext.create('Ext.data.Store', {
            model: 'relationModel',
            data: gridData
        });
        var gridColumns = [
            {text: gz.label.zxdeclare.relationshipName, align: 'left', dataIndex: 'displayname', flex: 6},
            {
                text: gz.label.selectRelationData, align: 'left', dataIndex: 'fielditemidesc', flex: 4,
                renderer: function (value, cellmeta, record, rowIndex, columnIndex, store) {
                    if (value == gz.label.zxdeclare.select) {
                        value = '<a style="color:blue" >' + gz.label.zxdeclare.select + '</a>';
                    }
                    return value;
                }
            },{
                text:gz.label.operation,align: 'left',flex:2,
                renderer: function (value, cellmeta, record, rowIndex, columnIndex, store) {
                    value = '<a style="color:blue" >' + gz.label.clear+ '</a>';
                    return value;
                }
            }
        ];
        var gridPanel = Ext.create('Ext.grid.Panel', {
            store: gridStore,
            columns: gridColumns,
            width: '100%',
            height: '100%',
            margin: '20 0 0 0',
            columnLines: true,
            listeners: {
                cellclick: function (grid, td, cellIndex, record, tr, rowIndex, e, eOpts) {
                    if (cellIndex == '1') {//如果不是关联指标列跳过
                        var fieldItemSelecorWin = Ext.widget('window', {
                            header: false,
                            height: 400,
                            width: 300,
                            resizable: false,
                            layout: 'fit',
                            modal: true,
                            items: {
                                xtype: 'fielditemselecor',
                                source: 'A',
                                multiple: false,
                                title: gz.label.selectField,//选择指标
                                okBtnText: gz.label.ok,
                                cancelBtnText: gz.label.cancel,
                                searchEmptyText: gz.label.zxdeclare.inputItemNameOrItemCodeSearch,//'输入指标名称或指标代码查询...'
                                listeners: {
                                    selectend: function (selectorCmp, fields) {
                                        var fieldData = fields[0].data;
                                        var fieldsetid = fieldData.fieldsetid;
                                        var itemid = fieldData.itemid;
                                        var fieldsetdesc = fieldData.itemdesc;
                                        record.set('fielditemidesc', fieldsetdesc);
                                        record.set('fieldsetid', fieldsetid);
                                        record.set('itemid', itemid);
                                        //去掉表格的修改标记
                                        record.dirty = false;
                                        record.commit();
                                        fieldItemSelecorWin.close();
                                    },
                                    cancel: function (selectorCmp) {
                                        fieldItemSelecorWin.close();
                                    }
                                }
                            }
                        });
                        fieldItemSelecorWin.show();
                    }
                    if(cellIndex == '2'){
                        record.set('fielditemidesc', '<a style="color:blue" >' + gz.label.zxdeclare.select + '</a>');
                        record.set('fieldsetid', "");
                        record.set('itemid', "");
                        //去掉表格的修改标记
                        record.dirty = false;
                        record.commit();
                    }
                }
    }

    });
        this.add(gridPanel);
    }

});