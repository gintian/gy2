Ext.define("mysalary.MySalarySetting", {
    requires: ['EHR.stepview.StepView', 'EHR.extWidget.field.CodeTreeCombox', 'EHR.rolepicker.RolePicker', 'mysalary.MySalaryViewSetting'],
    constructor: function (config) {
        mySalarySetting = this;
        mySalarySetting.nameIndex = 1;
        mySalarySetting.viewType = config.viewType;
        mySalarySetting.id = config.id;

        this.wingroup = new Ext.WindowGroup();
        this.init();
    },
    init: function () {
        //用于获取薪资表数据
        var param = new HashMap();
        param.put("type", "search");
        param.put("id", mySalarySetting.id);
        Rpc({
            functionId: 'GZ00000805',
            async: false,
            success: function (data) {
                var result = Ext.decode(data.responseText);
                if (result.return_code == 'success') {
                    mySalarySetting.resultData = result.return_data;
                } else if (result.return_code == 'fail') {

                }
            }
        }, param);
        this.createBasicInformationPanel();//创建基本信息panel
        this.createSalaryStructurePanel();//创建工资结构panel
        this.createWindow();
        if (mySalarySetting.viewType == 'view') {
            this.echoData();
        }
    },
    /**
     * 创建基本信息panel
     */
    createBasicInformationPanel: function () {
        var salaryScaleComboboxStore = Ext.create('Ext.data.Store', {
            storeId: 'salaryScaleComboboxStore',
            fields: ["salary_table", "salary_table_name"],
            data: mySalarySetting.resultData.salary_table
        });
        var numberFieldStore = Ext.create('Ext.data.Store', {
            storeId: 'numberFieldStore',
            fields: ["itemid", "itemdesc"],
            data: []
        });
        // 东莞交投二开,设置应纳税所得额和个人所得税所需要的stroe数据，比numberFieldStore多个空选项
        var numberFieldStoreAddEmpty = Ext.create('Ext.data.Store', {
            storeId: 'numberFieldStoreAddEmpty',
            fields: ["itemid", "itemdesc"],
            data: []
        });
        var dateFieldStore = Ext.create('Ext.data.Store', {
            storeId: 'dateFieldStore',
            fields: ["itemid", "itemdesc"],
            data: []
        });
        // 包含字符型和数值型的指标store
        var characterAndNumberFieldStore = Ext.create('Ext.data.Store', {
            storeId: 'characterAndNumberFieldStore',
            fields: ["itemid", "itemdesc"],
            data: []
        });
        //薪资表
        var salaryScaleCombobox = Ext.widget('combobox', {
            fieldLabel: gz.label.salaryScale,
            itemId: 'salaryScale',
            queryMode: 'local',
            displayField: 'salary_table_name',
            valueField: 'salary_table',
            labelAlign: 'right',
            name: 'salary_table',
            labelPad: 15,
            store: salaryScaleComboboxStore,
            // beforeLabelTextTpl: "<font color='red'> * </font>",
            allowBlank: false,
            width: 430,
            margin: '10 0 0 5',
            editable: false,
            listeners: {
                select: function (combo, record, eOpts) {
                    mySalarySetting.clearValue();
                    var salaryTable = record.data.salary_table;//表名
                    var param = new HashMap();
                    param.put("type", 'searchSalaryViewField');
                    param.put("tableName", salaryTable);
                    Rpc({
                        functionId: 'GZ00000805',
                        async: false,
                        success: function (data) {
                            var result = Ext.decode(data.responseText);
                            if (result.return_code == 'success') {
                                var returnData = result.return_data;
                                var numerbFieldData = returnData.N;
                                var dateFieldData = returnData.D;
                                var characterAndNumberFieldData = returnData.A;
                                // numberFieldStore.setData(numerbFieldData);
                                // dateFieldStore.setData(dateFieldData);
                                // numberFieldStore.loadData(numerbFieldData.slice(1,numerbFieldData.length), false);
                                numberFieldStoreAddEmpty.loadData(numerbFieldData);
                                dateFieldStore.loadData(dateFieldData, false);
                                characterAndNumberFieldStore.loadData(characterAndNumberFieldData, false);
                                // 设置应纳税所得额和个人所得税下拉框允许为空
                                Ext.getCmp('displayWindow').query('#taxableAmount')[0].allowBlank = true;
                                Ext.getCmp('displayWindow').query('#personalIncomeTax')[0].allowBlank = true;
                                Ext.getCmp('displayWindow').query('#payable')[0].allowBlank = true;
                                Ext.getCmp('displayWindow').query('#realWage')[0].allowBlank = true;
                                // 设置应纳税所得额和个人所得税下拉框默认选中空选项
                                Ext.getCmp('displayWindow').query('#taxableAmount')[0].setValue("none");
                                Ext.getCmp('displayWindow').query('#personalIncomeTax')[0].setValue("none");
                                Ext.getCmp('displayWindow').query('#payable')[0].setValue("none");
                                Ext.getCmp('displayWindow').query('#realWage')[0].setValue("none");
                            } else if (result.return_code == 'fail') {

                            }
                        }
                    }, param);
                },
                render: function () {
                    if (mySalarySetting.viewType == 'view') {
                        var numerbFieldData = mySalarySetting.resultData.salaryViewField.N;
                        var dateFieldData = mySalarySetting.resultData.salaryViewField.D;
                        var characterAndNumberFieldData = mySalarySetting.resultData.salaryViewField.A;
                        numberFieldStore.loadData(numerbFieldData.slice(1,numerbFieldData.length), false);// .slice(1,numerbFieldData.length)
                        numberFieldStoreAddEmpty.loadData(numerbFieldData);
                        dateFieldStore.loadData(dateFieldData, false);
                        characterAndNumberFieldStore.loadData(characterAndNumberFieldData, false);
                        // 设置应纳税所得额和个人所得税下拉框默认选中空选项
                        Ext.getCmp('displayWindow').query('#taxableAmount')[0].allowBlank = true;
                        Ext.getCmp('displayWindow').query('#personalIncomeTax')[0].allowBlank = true;
                        Ext.getCmp('displayWindow').query('#payable')[0].allowBlank = true;
                        Ext.getCmp('displayWindow').query('#realWage')[0].allowBlank = true;
                    }
                }
            }

        });
        var salaryScaleContainer = Ext.create('Ext.container.Container', {
            layout: 'hbox',
            items: [
                salaryScaleCombobox,
                {
                    xtype: 'component',
                    margin: '14 0 0 3',
                    html: '<font color="red"> * </font>'
                },
                {
                    xtype: 'image',
                    src: rootPath + '/module/gz/mysalary/images/org_add.png',
                    width: 18,
                    height: 18,
                    title:gz.label.addViewTip,
                    margin: '12 0 0 5',
                    style: 'cursor:pointer;',
                    listeners: {
                        element: 'el',
                        click: function () {
                            Ext.create('mysalary.MySalaryViewSetting',{
                            	view:'',
                            	name:''
                            });
                        }
                    }
                },
                {
                	xtype: 'image',
                    src: rootPath + '/images/new_module/salaryReportEdit.png',
                    width: 18,
                    height: 18,
                    title:gz.label.updateViewTip,
                    margin: '12 0 0 8',
                    style: 'cursor:pointer;',
                    listeners: {
                        element: 'el',
                        click: function () {
                        	var view = salaryScaleCombobox.value;
                        	var name = salaryScaleCombobox.rawValue;
                            Ext.create('mysalary.MySalaryViewSetting',{
                            	view:view,
                            	name:name
                            });
                        }
                    }
                }
            ]
        });
        //时间维度指标
        var timeDimensionIndicatorCombobox = Ext.widget('combobox', {
            fieldLabel: gz.label.timeDimensionIndicator,
            queryMode: 'local',
            displayField: 'itemdesc',
            valueField: 'itemid',
            labelAlign: 'right',
            name: 'salary_date',
            labelPad: 15,
            store: dateFieldStore,
            itemId: 'timeDimensionIndicator',
            // beforeLabelTextTpl: "<font color='red'> * </font>",
            allowBlank: false,
            width: 430,
            margin: '10 0 0 5',
            editable: false,
            setValue:mySalarySetting.setStoreValue
        });
        var timeDimensionIndicatorContainer = Ext.create('Ext.container.Container', {
            layout: 'hbox',
            items: [
                timeDimensionIndicatorCombobox,
                {
                    xtype: 'component',
                    margin: '14 0 0 3',
                    html: '<font color="red"> * </font>'
                }
            ]
        });
        //应发工资
        var payableCombobox = Ext.widget('combobox', {
            fieldLabel: gz.label.payable,
            queryMode: 'local',
            displayField: 'itemdesc',
            valueField: 'itemid',
            labelAlign: 'right',
            name: 'payable',
            labelPad: 15,
            store: numberFieldStoreAddEmpty,
            itemId: 'payable',
            // beforeLabelTextTpl: "<font color='red'> * </font>",
            // allowBlank: false,
            width: 430,
            margin: '10 0 0 5',
            editable: false,
            listeners: {
                beforeselect: function (combo, record, index, eOpts) {
                    return mySalarySetting.judeIsSelect(record);
                }
            },
			setValue:mySalarySetting.setStoreValue,
            tpl:Ext.create('Ext.XTemplate',
                '<ul class="x-list-plain"><tpl for=".">',
                '<li role="option" class="x-boundlist-item" style="height: 22px">{itemdesc}</li>',
                '</tpl></ul>'
            )  //为了给combobox添加空选项
        });
        var payableContainer = Ext.create('Ext.container.Container', {
            layout: 'hbox',
            items: [
                payableCombobox/*,
                {
                    xtype: 'component',
                    margin: '14 0 0 3',
                    html: '<font color="red"> * </font>'
                }*/
            ]
        });
        //应纳税金额
        var taxableAmountCombobox = Ext.widget('combobox', {
            fieldLabel: gz.label.taxableAmount,
            queryMode: 'local',
            displayField: 'itemdesc',
            valueField: 'itemid',
            labelAlign: 'right',
            name: 'taxable',
            labelPad: 15,
            itemId: 'taxableAmount',
            store: numberFieldStoreAddEmpty,
            // beforeLabelTextTpl: "<font color='red'> * </font>",
            // allowBlank: false,
            width: 430,
            margin: '10 0 0 5',
            editable: false,
            listeners: {
                beforeselect: function (combo, record, index, eOpts) {
                    return mySalarySetting.judeIsSelect(record);
                }
            },
            setValue:mySalarySetting.setStoreValue,
            tpl:Ext.create('Ext.XTemplate',
                '<ul class="x-list-plain"><tpl for=".">',
                '<li role="option" class="x-boundlist-item" style="height: 22px">{itemdesc}</li>',
                '</tpl></ul>'
            )  //为了给combobox添加空选项
        });
        var taxableAmountContainer = Ext.create('Ext.container.Container', {
            layout: 'hbox',
            items: [
                taxableAmountCombobox/*,
                {
                    xtype: 'component',
                    margin: '14 0 0 3',
                    html: '<font color="red"> * </font>'
                }*/
            ]
        });
        //个人所得税
        var personalIncomeTaxCombobox = Ext.widget('combobox', {
            fieldLabel: gz.label.personalIncomeTax,
            queryMode: 'local',
            displayField: 'itemdesc',
            valueField: 'itemid',
            labelAlign: 'right',
            name: 'incometax',
            itemId: 'personalIncomeTax',
            labelPad: 15,
            store: numberFieldStoreAddEmpty,
            // beforeLabelTextTpl: "<font color='red'> * </font>",
            // allowBlank: false,
            width: 430,
            margin: '10 0 0 5',
            editable: false,
            listeners: {
                beforeselect: function (combo, record, index, eOpts) {
                    return mySalarySetting.judeIsSelect(record);
                }
            },
			setValue:mySalarySetting.setStoreValue,
            tpl:Ext.create('Ext.XTemplate',
                '<ul class="x-list-plain"><tpl for=".">',
                '<li role="option" class="x-boundlist-item" style="height: 22px">{itemdesc}</li>',
                '</tpl></ul>'
            )  //为了给combobox添加空选项

        });
        var personalIncomeTaxContainer = Ext.create('Ext.container.Container', {
            layout: 'hbox',
            items: [
                personalIncomeTaxCombobox/*,
                {
                    xtype: 'component',
                    margin: '14 0 0 3',
                    html: '<font color="red"> * </font>'
                }*/
            ]
        });
        //实发工资
        var realWageCombobox = Ext.widget('combobox', {
            fieldLabel: gz.label.realWage,
            queryMode: 'local',
            displayField: 'itemdesc',
            valueField: 'itemid',
            labelAlign: 'right',
            itemId: 'realWage',
            labelPad: 15,
            store: numberFieldStoreAddEmpty,
            name: 'realpay',
            // beforeLabelTextTpl: "<font color='red'> * </font>",
            // allowBlank: false,
            width: 430,
            margin: '10 0 0 5',
            editable: false,
            listeners: {
                beforeselect: function (combo, record, index, eOpts) {
                    return mySalarySetting.judeIsSelect(record);
                }
            },
			setValue:mySalarySetting.setStoreValue,
            tpl:Ext.create('Ext.XTemplate',
                '<ul class="x-list-plain"><tpl for=".">',
                '<li role="option" class="x-boundlist-item" style="height: 22px">{itemdesc}</li>',
                '</tpl></ul>'
            )  //为了给combobox添加空选项
        });
        var realWageContainer = Ext.create('Ext.container.Container', {
            layout: 'hbox',
            items: [
                realWageCombobox/*,
                {
                    xtype: 'component',
                    margin: '14 0 0 3',
                    html: '<font color="red"> * </font>'
                }*/
            ]
        });
        //所属组织   start
        var optionpanels = Ext.widget('codecomboxfield', { //实时校验有问题
            border: false,
            width: 315,
            height: 22,
            onlySelectCodeset: true,
            itemId: 'organization',
            codesetid: "UN",
            margin: "0 0 0 15",
            // emptyText: kq.scheme.chooseOrg,
            name: 'B0110',
            ctrltype: "3",
            editable: false,
            allowBlank: false,
            nmodule: "1",
            listeners: {
                afterrender: function () {
                    // this.setValue("",true); //初始化赋值
                },
                select: function (a, b) {
                    // SchemeDetails.b0110 = b.get('id') + "`" + b.get('text');
                }
            }
        });
        // 所属机构
        var organization = Ext.create('Ext.container.Container', {
            layout: {
                type: 'hbox'
            },
            width: 430,
            margin: '10 0 0 57',
            border: false,
            // id: 'org_select',
            items: [
                {
                    xtype: 'label',
                    html: gz.label.affiliatedOrganization
                }, optionpanels,
                {
                    xtype: 'component',
                    html: '<font color="red"> * </font>',
                    margin: '5 0 0 3'
                }

            ]
        });
        //所属组织   end
        //可见范围
        var visibleRange = Ext.create('Ext.container.Container', {
            layout: 'vbox',
            items: [
                {
                    xtype: 'container',
                    layout: 'hbox',
                    items: [
                        {
                            xtype: 'label',
                            html: gz.label.visibleRange,
                            margin: '10 0 0 57'
                        }, {
                            xtype: 'container',
                            // layout:'hbox',子元素浮动时不可以加布局！！否则浮动不起来
                            itemId: 'roleContainer',
                            scrollable: 'y',
                            style: 'border:1px dashed #c5c5c5;',
                            margin: '10 0 0 5',
                            // maxWidth: 330,
                            width: 330,
                            height: 94
                        }, {
                            xtype: 'label',
                            html: '<div  style="color:blue;cursor:pointer;">' + gz.label.addRoles + '</div>',
                            margin: '10 0 0 15',
                            listeners: {
                                element: 'el',
                                click: mySalarySetting.createAddRoleWindow,
                                scope: mySalarySetting
                            }
                        }
                    ]

                },
                {
                    xtype: 'hiddenfield',
                    name: 'role',
                    itemId: 'role',
                    value: ''
                }
            ]
        });
        var programNameContainer = Ext.create('Ext.container.Container', {
            layout: 'hbox',
            items: [
                {
                    xtype: 'textfield',
                    width: 430,
                    fieldLabel: gz.label.programName,//方案名称
                    maxLength: 25,
                    itemId: 'programName',
                    // beforeLabelTextTpl: "<font color='red'> * </font>",
                    allowBlank: false,
                    name: 'name',
                    margin: '0 0 0 5',
                    labelPad: 15,
                    labelAlign: 'right',
                    listeners: {
                        blur: function (t) {
                            mySalarySetting.repeatFlag = false;
                            var mainStore = Ext.StoreManager.lookup('mainStore');//获取到薪资方案store
                            for (var i = 0; i < mainStore.getCount(); i++) {
                                var salaryName = Ext.util.Format.trim(mainStore.getAt(i).data.name);
                                var salaryId = mainStore.getAt(i).data.id;
                                if (salaryName == Ext.util.Format.trim(mySalarySetting.halfFullWidthCharacterConversion(t.getValue())) && salaryId != mySalarySetting.id) {//排除自身
                                    mySalarySetting.repeatFlag = true;
                                }
                            }
                            if (mySalarySetting.repeatFlag) {
                                Ext.Msg.alert(gz.label.tips, gz.label.repeatSalaryName);
                            }
                        }
                    }
                },
                {
                    xtype: 'component',
                    margin: '3 0 0 3',
                    html: '<font color="red"> * </font>'
                }
            ]
        });
        var yearData = [];
        yearData.push({desc:gz.label.noSelect,value:0});
        var year = new Date().getFullYear();
        for(i = year -10 ; i <= year ; i++){
        	yearData.push({desc:i,value:i});
        }
        
        var yearStore = Ext.create('Ext.data.Store',{
        	fields:['desc','value'],
        	data:yearData
        });
        this.basicInformationPanel = Ext.create('Ext.panel.Panel', {
            border: false,
            layout: {
                type: 'vbox'
            },
            items: [
                programNameContainer,
                salaryScaleContainer,
                timeDimensionIndicatorContainer,
                payableContainer,
                taxableAmountContainer,
                personalIncomeTaxContainer,
                realWageContainer,
                organization,
                visibleRange,
                {
                    xtype: 'container',
                    layout: 'hbox',
                    margin: '5 0 0 115',
                    items: [
                        {
                            xtype: 'checkbox',
                            width: 10,
                            // checked:false,
                            itemId: 'zeroItemCtrl',
                            inputValue: '1',
                            name: 'zeroItemCtrl'
                        },
                        {
                            xtype: 'component',
                            // width: 130,
                            margin: '2 0 0 8',
                            html: gz.label.zeroItemCtrl
                        },
                        {
                        	xtype:'component',
                        	width:20
                        },
                        {
                        	xtype: 'component',
                        	width:46,
                        	margin: '2 0 0 8',
                        	html: gz.label.show
                        },
                        {
                        	xtype : 'combobox',
                        	width :80,
                        	name : 'year',
                            itemId: 'yearCombox',
                        	displayField:'desc',
                        	valueField:'value',
                        	editable:false,
                        	emptyText:gz.label.select,
                        	store:yearStore
                        },
                        {
                        	xtype: 'component',
                        	margin: '2 0 0 8',
                        	html: gz.label.yearSalary
                        }
                    ]
                }
            ]
        });
    },
    /**
     * 创建工资结构panel
     */
    createSalaryStructurePanel: function () {
        var salaryStructureGridStore = Ext.create('Ext.data.Store', {
            storeId: 'salaryStructureGridStore',
            fields: ['name', 'item', 'chart', 'itemname'],
            data: []
        });
        var salaryStructureGridPanel = Ext.create('Ext.grid.Panel', {
            store: salaryStructureGridStore,
            height: 375,
            width: '100%',
            itemId: 'salaryStructureGridPanel',
            enableColumnResize: false,//禁止改变列宽
            //enableColumnMove: false,//禁止拖放列
            stripeRows: false,//表格是否隔行换色
            columnLines: true,//列分割线
            viewConfig: {
                markDirty: false, //不显示编辑后的三角
                plugins: {  
		        	ptype: 'gridviewdragdrop',  
		        	ddGroup:  'DragDropGroup'
   		    	},
   		    	listeners: {
                    beforedrop: function (node, data, overModel, dropPosition, dropHandlers, eOpts) {
                        for (var i = 0; i < data.records.length; i++) {
                            if (data.records[i].data.item == 'operate') {
                                dropHandlers.cancelDrop();
                                salaryStructureGridPanel.getSelectionModel().deselect(data.records[i]);//取消拖动行的选中状态
                                return;
                            }
                        }
                        if (overModel.data.item == 'operate' && dropPosition == 'after') { //不允许拖拽行 拖拽到操作行的后面
                            dropHandlers.cancelDrop();
                            salaryStructureGridPanel.getSelectionModel().deselect(data.records);
                        } else {
                            dropHandlers.processDrop();
                        }
                    }
                }
            },
            selModel: {
                selType: 'rowmodel'
            },
            plugins: [
                Ext.create('Ext.grid.plugin.CellEditing', {
                    clicksToEdit: 2,
                    listeners: {
                        beforeedit: function (editor, context, eOpts) {
                            var record = context.record;
                            if (record.data.item == 'operate') {
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
                    text: gz.label.salaryItemCategory,
                    dataIndex: 'name',
                    sortable: false,
                    flex: 4,
                    align: 'left',
                    editor: {xtype: 'textfield', selectOnFocus: true},
                    menuDisabled: true,
                    renderer: function (value, metaData, record, rowIndex, colIndex, store, view) {
                        if (record.data.item == 'operate') {
                            metaData.tdStyle = 'border-width:0px !important'; //去除操作行的分割线
                        }
                        // return '<div style="overflow: hidden;height: 30px;padding-top:8px;" id="' + record.id + '" onmouseover="mySalarySetting.tdMouseOver(\'' + record.id + '\')" onmouseout="mySalarySetting.tdMouseout(\'' + record.id + '\')"><div style="float:left">' + value + '</div><span name="add_img" style="display: none">' + mySalarySetting.getImgHtml(rowIndex) + '</span></div>';
                        if (record.data.item === 'operate') {//操作行
                            return '<div style="overflow: hidden;height: 30px;padding-top:8px;"  ><span name="add_img" style="float: left">' + mySalarySetting.getImgHtml(rowIndex) + '</span></div>';
                        } else {
                            return value;
                        }
                    }
                },// 工资项目类别
                {
                    text: gz.label.salaryItem,
                    dataIndex: 'itemname',
                    sortable: false,
                    flex: 10,
                    align: 'left',
                    menuDisabled: true,
                    renderer: function (value, metaData, record, rowIndex, colIndex, store, view) {
                        if (record.data.item == 'operate') {
                            metaData.tdStyle = 'border-width:0px !important'; //去除操作行的分割线
                        }
                        //取出当前薪资项目选中的指标
			            var items = ','+record.data.item+',';
			            var itemnames = ','+record.data.itemname+',';
			            var characterAndNumberFieldStore = Ext.StoreManager.lookup("characterAndNumberFieldStore");
			            var name = '';
                        for(var i =0 ; i < characterAndNumberFieldStore.getCount() ; i++){
                        	var data = characterAndNumberFieldStore.getAt(i).data;
                        	if(items.indexOf(','+data.itemid+',')>-1){
                        		name +=','+data.itemdesc;
                        	}
                        }
                        if(name.length > 0 ){
                        	value = name.substring(1);
                        }
                        return value;
                    }
                },//工资项目
                {
                    text: gz.label.whetherTheLegend,
                    dataIndex: 'chart',
                    sortable: false,
                    xtype: 'checkcolumn',
                    flex: 3,
                    align: 'center',
                    menuDisabled: true,
                    renderer: function (value, metaData, record, rowIndex, colIndex, store, view) {
                        var cssPrefix = Ext.baseCSSPrefix,
                            cls = cssPrefix + 'grid-checkcolumn';
                        if (value) {
                            cls += ' ' + cssPrefix + 'grid-checkcolumn-checked';
                        }
                        var img = '<img class="' + cls + '" src="' + Ext.BLANK_IMAGE_URL + '"/>';
                        if (record.data.item == 'operate') { //去除操作行的 checkbox样式
                            metaData.tdStyle = 'border-width:0px !important'; //去除操作行的分割线
                            img = '';
                        }
                        return img;
                    }

                }, // 是否显示图例
                {
                    text: gz.label.total,
                    dataIndex: 'total',
                    sortable: false,
                    xtype: 'checkcolumn',
                    flex: 3,
                    align: 'center',
                    menuDisabled: true,
                    renderer: function (value, metaData, record, rowIndex, colIndex, store, view) {
                        var cssPrefixTotal = Ext.baseCSSPrefix,
                            clsTotal = cssPrefixTotal + 'grid-checkcolumn';
                        if (value) {
                            clsTotal += ' ' + cssPrefixTotal + 'grid-checkcolumn-checked';
                        }
                        var img = '<img class="' + clsTotal + '" src="' + Ext.BLANK_IMAGE_URL + '"/>';
                        if (record.data.item == 'operate') { //去除操作行的 checkbox样式
                            metaData.tdStyle = 'border-width:0px !important'; //去除操作行的分割线
                            img = '';
                        }
                        return img;
                    }

                },// 合计
                {
                    text: gz.label.operation,
                    sortable: false,
                    flex: 3,
                    align: 'center',
                    menuDisabled: true,
                    renderer: function (value, metaData, record, rowIndex, colIndex, store, view) {
                        if (record.data.item == 'operate') {//代表是加号操作列  不允许删除
                            metaData.tdStyle = 'border-width:0px !important'; //去除操作行的分割线
                            value = '';
                        } else {
                            value = "<a href=\"javascript:mySalarySetting.deleteSalaryStructureRecord('" + record.id + "');\" >" + gz.label.del + "<a/>";
                        }
                        return value;
                    }
                },
                // 操作
                {
                    dataIndex: 'item',
                    hidden: true
                }

            ],
            listeners: {
                cellClick: function (t, td, cellIndex, record, tr, rowIndex, e, eOpts) {
                    if (cellIndex != 1) {
                        return;
                    }
                    if (record.data.item == 'operate') {
                        return;
                    }
                    mySalarySetting.createSelectFieldWindow(record);
                },
                containerclick: function (t, e, eOpts) {//t代表this(gridPanel)表格视图对象 在IE浏览器中 blur()事件有效 wangb  26566 20170810
                    t.blur();
                },
                render: function (panel) {
                    panel.getStore().add({name: '', itemname: '', chart: '', item: 'operate'});
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
                                    //div 里面内容不允许自动换行，滚动条隐藏 27250 27734  wangb 2017-4-24
                                    tip.update("<div style='white-space:nowrap;overflow:hidden;'>" + div.innerHTML + "</div>");
                                } else
                                    return false;
                            }
                        }
                    });
                }
            }
        });
        this.salaryStructurePanel = Ext.create('Ext.panel.Panel', {
            border: false,
            layout: {
                type: 'vbox'
            },
            items: [salaryStructureGridPanel]
        })
    },
    /**
     * 创建显示窗口
     */
    createWindow: function () {
        //第一步，第二步，头部流程控件
        this.stepview = Ext.widget("stepview", {
            listeners: {
                stepchange: function (stepview, step) {
                    mySalarySetting.changeStep(stepview.currentIndex);
                }
            },
            height: 30,
            freeModel: false,
            stepData: [{name: gz.label.basicInformation}, {name: gz.label.salaryStructure}]
        });
        var formPanel = this.createFormPanel();
        
        var userAgent = navigator.userAgent; //取得浏览器的userAgent字符串  
        var isIE = userAgent.indexOf("compatible") > -1 && userAgent.indexOf("MSIE") > -1; //判断是否IE<11浏览器
        var isIE8 = false;
        if(isIE){
        	var reIE = new RegExp("MSIE (\\d+\\.\\d+);");
            reIE.test(userAgent);
            var fIEVersion = parseFloat(RegExp["$1"]);
            if(fIEVersion < 8 || fIEVersion == 10)
            	isIE8 = true;
            if(fIEVersion == 8 && userAgent.indexOf('Trident/4.0;')>-1)// 真正ie8  userAgent串为 Trident/4.0;
            	isIE8 = true;
        }
        var displayWindow = Ext.create('Ext.window.Window', {
            title: mySalarySetting.viewType == 'view' ? gz.label.edite : gz.label.create,
            height:isIE8?520:500,
            width: 590,
            id: 'displayWindow',
            resizable: false,
            scrollable: 'y',
            modal: true,
            items: [{
                xtype: 'panel',
                width: 580,
                border: false,
                margin: '3 0 0 60',
                items: [mySalarySetting.stepview]
            }, {
                xtype: 'form',
                height: isIE8?430:410,
                margin: '10 0 0 0',
                items: [formPanel],
                itemId: 'salarySettingParentForm',
                border: false,
                buttonAlign: 'center',
                buttons: [
                    {
                        text: gz.button.previousStep,//上一步
                        id: 'previousStep',
                        hidden: true,
                        height: 22,
                        handler: function () {
                            mySalarySetting.stepview.previousStep();
                        }
                    }, {
                        text: gz.button.nextStep,//下一步
                        id: 'nextStep',
                        hidden: false,
                        height: 22,
                        formBind: true,
                        handler: function () {
                            var programName = Ext.getCmp('displayWindow').query('#programName')[0].getValue();
                            if (Ext.util.Format.trim(programName) == '') {
                                Ext.Msg.alert(gz.label.tips, gz.label.salaryProgramNameIsNotNull);
                                return;
                            }
                            mySalarySetting.stepview.nextStep();
                        }
                    }, {
                        text: gz.button.ok,//确定
                        id: 'ok',
                        hidden: true,
                        height: 22,
                        handler: function () {
                            if (mySalarySetting.repeatFlag) {
                                Ext.Msg.alert(gz.label.tips, gz.label.repeatSalaryName);
                                return;
                            }
                            var parm = {};
                            //获取基本信息panel Value
                            var values = formPanel.getValues();
                            //获取工资结构panel 数据
                            var salaryStructureGridStore = Ext.StoreManager.lookup('salaryStructureGridStore');
                            var salaryStructureGridStoreData = salaryStructureGridStore.getData().items;
                            //个人所得税
                            var personalIncomeTax = values.incometax;
                            //方案名称
                            var programName = values.name;
                            if (Ext.util.Format.trim(programName) == '') {
                                Ext.Msg.alert(gz.label.tips, gz.label.salaryProgramNameIsNotNull);
                                return;
                            }
                            //实发工资
                            var realWage = values.realpay;
                            //可见范围
                            var role = values.role;
                            //薪资表
                            var salaryScale = values.salary_table;
                            //应纳税金额
                            var taxableAmount = values.taxable;
                            //时间维度指标
                            var timeDimensionIndicator = values.salary_date;
                            //应发工资
                            var payable = values.payable;
                            //所属组织
                            var organization = values.B0110;
                            if (organization) {
                                organization = "UN" + organization.split("`")[0];
                            }
                            parm.items = [];
                            parm.id = mySalarySetting.id;
                            parm.name = programName;
                            parm.salary_table = salaryScale;
                            parm.salary_date = timeDimensionIndicator;
                            parm.payable = payable;
                            parm.taxable = taxableAmount;
                            parm.incometax = personalIncomeTax;
                            parm.realpay = realWage;
                            parm.B0110 = organization;
                            parm.role_id = role;
                            parm.zeroItemCtrl = values.zeroItemCtrl;
                            if (!parm.zeroItemCtrl) {
                                parm.zeroItemCtrl = '0';
                            }
                            parm.year = values.year && !isNaN(values.year)?parseInt(values.year):0;
                            var isItemNameBlank = false;
                            var isItemNBlank = false;
                            for (var i = 0; i < salaryStructureGridStoreData.length; i++) {
                                var dataItem = salaryStructureGridStoreData[i];
                                var itemname = dataItem.data.name;
                                var item = dataItem.data.item;
                                var chart = dataItem.data.chart ? '1' : '0';
                                var total = dataItem.data.total ? '1' : '0';
                                if (!Ext.util.Format.trim(itemname) && item != 'operate') {
                                    isItemNameBlank = true;
                                }
                                if (!item && item != 'operate') {
                                    isItemNBlank = true;
                                }
                                if ((itemname && item) && item != 'operate') {//排除加号操作行
                                    parm.items.push({name: itemname, item: item, chart: chart, total: total});
                                }
                            }
                            if (isItemNameBlank) {
                                Ext.Msg.alert(gz.label.tips, gz.label.salaryItemNameIsNotNull);
                                return;
                            }
                            if (isItemNBlank) {
                                Ext.Msg.alert(gz.label.tips, gz.label.salaryItemIsNotNull);
                                return;
                            }
                            var jsonStr = JSON.stringify(parm);
                            var paramMap = new HashMap();
                            paramMap.put("type", "save");
                            paramMap.put("data", jsonStr);
                            Rpc({
                                functionId: 'GZ00000805',
                                async: false,
                                success: function (data) {
                                    var result = Ext.decode(data.responseText);
                                    if (result.return_code == 'success') {
                                        var mainStore = Ext.StoreManager.lookup('mainStore');
                                        var id = result.return_data.id;
                                        var salary_table_name = result.return_data.salary_table_name;
                                        var name = result.return_data.name;
                                        var salary_table = result.return_data.salary_table;
                                        var B0110_name = result.return_data.B0110_name;
                                        var role_name = result.return_data.role_name;
                                        if (mySalarySetting.viewType != 'view') {//只有在创建的时候才往store里面添加
                                            mainStore.add({
                                                id: id,
                                                name: name,
                                                salary_table: salary_table,
                                                B0110_name: B0110_name,
                                                role_name: role_name,
                                                salary_table_name: salary_table_name
                                            });
                                        } else {//代表是修改  执行更新数据操作
                                            var record = mainStore.getById(id);//获取到当前选择记录record
                                            record.set('name', name);
                                            record.set('salary_table', salary_table);
                                            record.set('B0110_name', B0110_name);
                                            record.set('role_name', role_name);
                                            record.set('salary_table_name', salary_table_name);
                                        }
                                        Ext.Msg.alert(gz.label.tips, gz.label.saveSuccess, function () {
                                            Ext.getCmp('displayWindow').close();
                                        });
                                    } else if (result.return_code == 'fail') {
                                        Ext.Msg.alert(gz.label.tips, result.return_msg);
                                    }
                                }
                            }, paramMap);
                        }
                    }, {
                        text: gz.button.no,//取消
                        id: 'no',
                        hidden: false,
                        height: 22,
                        // margin: '60 5 0 0',
                        handler: function () {
                            Ext.getCmp('displayWindow').close();
                        }
                    }
                ]

            }
            ]
        });
        this.wingroup.register(displayWindow);
        displayWindow.show();
    },
    /**
     * 创建formpanel
     */
    createFormPanel: function () {
        var formPanel = Ext.create('Ext.form.Panel', {
            border: false,
            layout: 'card',
            itemId: 'salarySettingPanel',
            items: []
        });
        formPanel.setActiveItem(this.basicInformationPanel);
        return formPanel;
    },
    /**
     * 步骤条步骤改变事件处理函数
     */
    changeStep: function (index) {
        var salarySettingPanel = Ext.getCmp('displayWindow').query('#salarySettingPanel')[0];
        if (index == 0) {
            Ext.getCmp("previousStep").setHidden(true);
            Ext.getCmp("nextStep").setHidden(false);
            Ext.getCmp("ok").setHidden(true);
            salarySettingPanel.setActiveItem(this.basicInformationPanel);
        } else if (index == 1) {
            Ext.getCmp("previousStep").setHidden(false);
            Ext.getCmp("nextStep").setHidden(true);
            Ext.getCmp("ok").setHidden(false);
            salarySettingPanel.setActiveItem(this.salaryStructurePanel);
        }
    },
    /**
     * 创建添加角色window
     */
    createAddRoleWindow: function () {
        Ext.create('EHR.rolepicker.RolePicker', {
            callBackFunc: mySalarySetting.getRolesList,
            multiple: true
        });
        Ext.ComponentQuery.query("window")[2].setHeight(400);
    },
    /**
     * 获取角色数据
     */
    getRolesList: function (records) {
        var roleLabel = '';
        var roleValue = '';
        var displayWindow = Ext.getCmp('displayWindow');
        if (displayWindow) {
            var roleValueObject = displayWindow.query('#role')[0];
            var roleContainer = displayWindow.query('#roleContainer')[0];
            //获取已有的角色数据
            roleValue = roleValueObject.getValue();
            var orginValue = roleValue;
            var orginRoleValueArray = [];
            if (roleValue) {
                orginRoleValueArray = roleValue.split(",");
            }
            for (var i = 0; i < records.length; i++) {
                if (Ext.Array.indexOf(orginRoleValueArray, records[i].role_id_e) != -1) {//说明该角色id已经存在于容器中
                    continue;
                }
                roleContainer.add(mySalarySetting.createRolePanel(records[i].role_name, records[i].role_id_e));
                if (orginValue) {//有数据的情况下 加，
                    roleValue += ","
                }
                roleValue += records[i].role_id_e;
                if (i < records.length - 1) {
                    roleValue += ',';
                    roleLabel += ",";
                }
            }
            roleValueObject.setValue(roleValue);
        }

    },
    /**
     * 创建选择指标window
     */
    createSelectFieldWindow: function (record, type, param) {
        var selectModels = [];
        if (type != 'add') {
            //取出当前薪资项目选中的指标
            var fieldItems = record.data.item;
            var fieldItemsArray = [];
            var characterAndNumberFieldStore = Ext.StoreManager.lookup("characterAndNumberFieldStore");
            if (fieldItems && characterAndNumberFieldStore) {
                fieldItemsArray = fieldItems.split(",");
                for (var i = 0; i < fieldItemsArray.length; i++) {
                    for (var j = 0; j < characterAndNumberFieldStore.getCount(); j++) {
                        if (fieldItemsArray[i] == characterAndNumberFieldStore.getAt(j).data.itemid) {
                            selectModels.push(characterAndNumberFieldStore.getAt(j));
                        }
                    }
                }
            }
        }
        var records = [];
        var selectFieldGridPanel = Ext.create('Ext.grid.Panel', {
            store: Ext.StoreManager.lookup("characterAndNumberFieldStore"),
            height: 420,
            width: 380,
            margin: '2 0 0 5',
            itemId: "selectFieldGridPanel",
            enableColumnResize: false,//禁止改变列宽
            enableColumnMove: false,//禁止拖放列
            stripeRows: false,//表格是否隔行换色
            columnLines: false,//列分割线
            viewConfig: {
                markDirty: false, //不显示编辑后的三角
                plugins: {  
            		ptype: 'gridviewdragdrop',  
            		ddGroup: 'DragDropGroup'//此处代表拖动的组 拖动组件与放置组件要同属一组才能实现相互拖放  
        		} 
            },
            selModel: Ext.create("Ext.selection.CheckboxModel", {
                mode: "SIMPLE",//multi,simple,single；默认为多选multi
                selType:"checkboxmodel",
                //checkOnly: true,//如果值为true，则只用点击checkbox列才能选中此条记录
                enableKeyNav: true,
                getHeaderConfig: function() {
                    var me = this,
                        showCheck = me.showHeaderCheckbox !== false;
                    return {
                        xtype: 'gridcolumn',
                        ignoreExport: true,
                        isCheckerHd: showCheck,
                        text : '&#160;',
                        clickTargetName: 'el',
                        width: me.headerWidth,
                        sortable: false,
                        draggable: false,
                        resizable: false,
                        hideable: false,
                        menuDisabled: true,
                        level:me.level,
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
                    dataIndex: 'itemdesc',
                    sortable: false,
                    flex: 1,
                    align: 'left'
                },// 指标名称
                {
                    dataIndex: 'itemid',
                    hidden: true,
                    sortable: false
                }//指标id
            ],
        });
        selectFieldGridPanel.getSelectionModel().select(selectModels);
        var selectFieldWindow = Ext.create('Ext.window.Window', {
            height: 500,
            width: 400,
            resizable: false,
            modal: true,
            minButtonWidth: 50,
            title: gz.label.selectItem,
            buttonAlign: 'center',
            buttons: [
                {
                    text: gz.button.ok,
                    height: 22,
                    handler: function () {
                        records = selectFieldGridPanel.getSelectionModel().getSelection();
                        var itemLabel = '';
                        var itemValue = '';
                        for (var i = 0; i < records.length; i++) {
                            itemValue += records[i].data.itemid;
                            itemLabel += records[i].data.itemdesc;
                            if (i < records.length - 1) {
                                itemLabel += ",";
                            }
                            if (i < records.length - 1) {
                                itemValue += ','
                            }
                        }
                        if (type == 'add') {
                            var salaryStructureGridStore = Ext.StoreManager.lookup('salaryStructureGridStore');
                            var index = param.index;
                            // var name = gz.label.salaryItemCategory + index;
                            var name = gz.label.salaryItemCategory + mySalarySetting.nameIndex;
                            mySalarySetting.nameIndex++;
                            salaryStructureGridStore.insert(index, {
                                name: name,
                                itemname: itemLabel,
                                chart: false,
                                item: itemValue
                            });
                            mySalarySetting.resetSort();
                        } else {
                            record.set("item", itemValue);
                            record.set("itemname", itemLabel);
                        }
                        selectFieldWindow.close();
                    }
                }, {
                    text: gz.button.no,
                    height: 22,
                    handler: function () {
                        selectFieldWindow.close();
                    }
                }
            ],
            items: [
                selectFieldGridPanel
            ]

        });
        selectFieldWindow.show();
    },
    /**
     * 删除工资项目
     */
    deleteSalaryStructureRecord: function (id) {
        var confim = Ext.Msg.confirm(gz.label.tips, gz.label.isDelItemsSuccess, function (t) {
            if (t == 'yes') {
                var salaryStructureGridStore = Ext.StoreManager.lookup('salaryStructureGridStore');
                var storeData = salaryStructureGridStore.getData().items;
                var index = undefined;
                for (var i = 0; i < storeData.length; i++) {
                    if (id == storeData[i].id) {
                        index = i;
                    }
                }
                if (index != undefined) {
                    salaryStructureGridStore.remove(salaryStructureGridStore.getAt(index));
                    mySalarySetting.resetSort();
                }
            }
        });
        this.wingroup.register(confim);
        this.wingroup.bringToFront(confim);
        // var length = storeData.length;
        // if (length == 0) {
        //     salaryStructureGridStore.add({name: '', itemname: '', chart: false, item: ''});
        // }

    },
    /**
     * 用于判断数值型store里面的数据是否可选
     * record 选中的记录
     * return true or false
     */
    judeIsSelect: function (record) {
        var window = Ext.getCmp('displayWindow');
        var payableCombobox = window.query('#payable')[0];
        var taxableAmountCombobox = window.query('#taxableAmount')[0];
        var personalIncomeTaxCombobox = window.query('#personalIncomeTax')[0];
        var realWageCombobox = window.query('#realWage')[0];
        if (record.data.itemid != 'none'){
            if (record.data.itemid == payableCombobox.getValue() || record.data.itemid == taxableAmountCombobox.getValue() || record.data.itemid == personalIncomeTaxCombobox.getValue() || realWageCombobox.getValue() == record.data.itemid) {
                Ext.Msg.alert(gz.label.tips, gz.label.isHaveFieldItem);
                return false;
            } else {
                return true;
            }
        }
    },
    /**
     * 回显数据
     */
    echoData: function () {
        var displayWindow = Ext.getCmp('displayWindow');
        //基本信息panel赋值
        var formPanel = displayWindow.query('#salarySettingPanel')[0];
        formPanel.getForm().setValues(mySalarySetting.resultData.salary_fields);
        //可见范围填充数据
        // var roleLabelObject = displayWindow.query('#roleLabel')[0];
        var roleValueObject = displayWindow.query('#role')[0];
        var roleIdValueArray = [];
        if (mySalarySetting.resultData.role_id) {
            roleIdValueArray = mySalarySetting.resultData.role_id.split(",");
        }
        if (mySalarySetting.resultData.role_name) {
            var roleContainer = displayWindow.query('#roleContainer')[0];
            var rolenameArray = mySalarySetting.resultData.role_name.split(",");
            for (var i = 0; i < rolenameArray.length; i++) {
                roleContainer.add(mySalarySetting.createRolePanel(rolenameArray[i], roleIdValueArray[i]));
            }
        }
        roleValueObject.setValue(mySalarySetting.resultData.role_id);
        //工资结构panel赋值
        var salaryStructureGridStore = Ext.StoreManager.lookup('salaryStructureGridStore');
        var salary_items = mySalarySetting.resultData.salary_items;
        for (var i = 0; i < salary_items.length; i++) {
            if (salary_items[i].chart == '1') {
                salary_items[i].chart = true;
            } else {
                salary_items[i].chart = false;
            }
            if (salary_items[i].total == '1') {
                salary_items[i].total = true;
            } else {
                salary_items[i].total = false;
            }
        }
        salaryStructureGridStore.loadData(salary_items);
    },
    createRolePanel: function (roleName, roleId) {
        var cutName = '';
        if (roleName.replace(/[\u4E00-\u9FA5]/g, 'aa').length > 10) {
            cutName = mySalarySetting.cut_str(roleName, 5);
        } else {
            cutName = roleName;
        }
        var rolePanel = Ext.create('Ext.panel.Panel', {
            border: false,
            itemId: roleId,
            id: roleId,
            margin: '5 0 0 2',
            width: 67,
            style: 'float:left',
            items: [
                {
                    xtype: 'container',
                    layout: {
                        type: 'vbox',
                        pack: 'center',
                        align: 'middle'
                    },
                    items: [
                        {
                            xtype: 'image',
                            height: 25,
                            width: 25,
                            id: 'image' + roleId,
                            src: rootPath + '/images/role.png'
                            // listeners: {
                            //     render: function (t) {
                            //         Ext.create('Ext.tip.ToolTip', {
                            //             target: "image" + roleId,
                            //             shadow: false,
                            //             trackMouse: true,
                            //             maxWidth: 800,//最大显示宽度设置为800，文本宽度超过800显示不全解决不了，高度超过显示的最大高度同样也显示不全解决不了  27734 wangb 20170517
                            //             renderTo: Ext.getBody(),
                            //             bodyStyle: "background-color:white;border:1px solid #c5c5c5;",
                            //             html: roleName
                            //         });
                            //     }
                            // }
                        }, {
                            xtype: 'component',
                            html: cutName
                        }
                    ]
                }
                , {
                    xtype: 'image',
                    src: '/workplan/image/remove.png',
                    style: 'cursor:pointer;position:absolute;left:40px !important;top:-2px !important',
                    width: 15,
                    height: 15,
                    id: 'del' + roleId,
                    hidden: true,
                    remove: 'yes',
                    listeners: {
                        element: 'el',
                        click: function () {
                            mySalarySetting.deleteRole(roleId);
                        }
                    }
                }
            ],
            listeners: {
                render: function (t) {
                    Ext.create('Ext.tip.ToolTip', {
                        target: roleId,
                        shadow: false,
                        trackMouse: true,
                        maxWidth: 800,//最大显示宽度设置为800，文本宽度超过800显示不全解决不了，高度超过显示的最大高度同样也显示不全解决不了  27734 wangb 20170517
                        renderTo: Ext.getBody(),
                        bodyStyle: "background-color:white;border:1px solid #c5c5c5;",
                        html: roleName
                    });
                },
                mouseover: {
                    element: 'el',
                    fn: function (e) {
                        document.getElementById('del' + roleId).style.display = 'block';
                        // rolePanel.query('image[remove=yes]')[0].setHidden(false);//用这行代码会出问题所以改成了原生js
                    }
                },
                mouseout: {
                    element: 'el',
                    fn: function (e) {
                        document.getElementById('del' + roleId).style.display = 'none';
                        // rolePanel.query('image[remove=yes]')[0].setHidden(true);
                    }
                }
            }
        });
        return rolePanel;
    },
    //截取6个字节长度的字符串
    cut_str: function (str, len) {
        var char_length = 0;
        for (var i = 0; i < str.length; i++) {
            var son_str = str.charAt(i);
            encodeURI(son_str).length > 2 ? char_length += 1 : char_length += 0.5;
            if (char_length >= len) {
                var sub_len = char_length == len ? i + 1 : i;
                return str.substr(0, sub_len);
            }
        }
    },
    /**
     * 用于切换薪资表和新增薪资表时 清空下拉框数据
     */
    clearValue: function () {
        var salaryStructureGridStore = Ext.StoreManager.lookup('salaryStructureGridStore');
        salaryStructureGridStore.loadData([]);
        var window = Ext.getCmp('displayWindow');
        var timeDimensionIndicatorCombobox = window.query('#timeDimensionIndicator')[0];
        var payableCombobox = window.query('#payable')[0];
        var taxableAmountCombobox = window.query('#taxableAmount')[0];
        var personalIncomeTaxCombobox = window.query('#personalIncomeTax')[0];
        var realWageCombobox = window.query('#realWage')[0];
        timeDimensionIndicatorCombobox.allowBlank = true;
        timeDimensionIndicatorCombobox.clearValue();
        timeDimensionIndicatorCombobox.allowBlank = false;
        payableCombobox.allowBlank = true;
        payableCombobox.clearValue();
        payableCombobox.allowBlank = false;
        taxableAmountCombobox.allowBlank = true;
        taxableAmountCombobox.clearValue();
        taxableAmountCombobox.allowBlank = false;
        personalIncomeTaxCombobox.allowBlank = true;
        personalIncomeTaxCombobox.clearValue();
        personalIncomeTaxCombobox.allowBlank = false;
        realWageCombobox.allowBlank = true;
        realWageCombobox.clearValue();
        realWageCombobox.allowBlank = false;
        Ext.getCmp('displayWindow').query('#organization')[0].setValue('');
        Ext.getCmp('displayWindow').query('#salarySettingPanel')[0].getForm().checkValidity();//手动调用验证表单
        Ext.getCmp('displayWindow').query('#salarySettingParentForm')[0].getForm().checkValidity();//手动调用验证表单
    },
    /**
     * 删除角色
     */
    deleteRole: function (roleId) {
        Ext.Msg.confirm(gz.label.tips, gz.label.isDeletRole, function (ope) {
            if (ope == 'yes') {
                var displayWindow = Ext.getCmp('displayWindow');
                var roleContainer = displayWindow.query('#roleContainer')[0];
                var deleteRole = roleContainer.query('#' + roleId)[0];
                roleContainer.remove(deleteRole);
                //roleContainer.getScrollY()  ie下获取不到值
                roleContainer.scrollTo(roleContainer.getScrollX(), roleContainer.scrollable.trackingScrollTop);//删除完后定位到当前位置。
                var roleValueObject = displayWindow.query('#role')[0];
                var roleValue = roleValueObject.getValue();
                var roleValueArray = [];
                if (roleValue) {
                    roleValueArray = roleValue.split(",");
                    Ext.Array.splice(roleValueArray, Ext.Array.indexOf(roleValueArray, roleId), 1);
                    roleValue = "";
                    for (var i = 0; i < roleValueArray.length; i++) {
                        roleValue += roleValueArray[i];
                        if (i < roleValueArray.length - 1) {
                            roleValue += ',';
                        }
                    }
                    roleValueObject.setValue(roleValue);
                }
            }
        });
    },
    /**
     * 薪资项目分类悬浮处理函数
     * @param id divid
     */
    tdMouseOver: function (id, rowindex) {
        var salaryStructureGridStore = Ext.StoreManager.lookup('salaryStructureGridStore');
        var salaryStructureGridData = salaryStructureGridStore.getData();
        var index = salaryStructureGridData.items.length - 1;
        var rowIndex = Ext.getDom(id).children[1].children[0].name;
        if (rowIndex == index) {
            Ext.getDom(id).children[1].style.display = '';
        }
    },
    /**
     * 薪资项目分类离开处理函数
     * @param id divid
     */
    tdMouseout: function (id) {
        Ext.getDom(id).children[1].style.display = 'none';
    },
    /**
     * 新增处理函数
     * @param id divid
     */
    tdClick: function (rowIndex) {
        var salaryStructureGridStore = Ext.StoreManager.lookup('salaryStructureGridStore');
        var salaryStructureGridData = salaryStructureGridStore.getData();
        var index = salaryStructureGridData.items.length - 1;
        if (salaryStructureGridData.items[index]) {
            mySalarySetting.createSelectFieldWindow('', 'add', {index: index});
        }

    },
    /**
     * 获取新增图片的html代码
     */
    getImgHtml: function (rowIndex) {
        return '<img name=' + rowIndex + ' src="/module/gz/mysalary/images/org_add.png" onclick="mySalarySetting.tdClick(' + rowIndex + ')" style="height: 16px;width: 16px;float:right;margin-right:5px;cursor:pointer;">'
    },
    /**
     * 重新对rowindex进行排序
     */
    resetSort: function () {
        var add_imgs = Ext.query("*[name=add_img]");
        Ext.each(add_imgs, function (add_img, index) {
            add_img.innerHTML = mySalarySetting.getImgHtml(index);
        });

    },
    setStoreValue:function(value){
    	var me = this;
    	var flag = false;
        if (value != null) {
        	var store = me.getStore();
        	for(var i = 0; i <store.getCount() ; i++){
        		if(value == store.getAt(i).data.itemid){
        			flag = true;
        			break;
        		}
        	}
        	if(flag){
            	return me.doSetValue(value);
        	}else{
        		return me.doSetValue('');
        	}
        }
        else {
            me.suspendEvent('select');
            me.valueCollection.beginUpdate();
            me.pickerSelectionModel.deselectAll();
            me.valueCollection.endUpdate();
            me.lastSelectedRecords = null;
            me.resumeEvent('select');
        }
    },
    // 半角全角字符转换
    halfFullWidthCharacterConversion : function (value) {
        var result = '';
        for (var i = 0; i < value.length; i++) {
            switch (value.charAt(i)) {
                case '<':
                    result = result+ "＜";
                    break;
                case '>':
                    result = result+ "＞";
                    break;
                case '"':
                    result = result+ "＂";
                    break;
                case '\'':
                    result = result+ "＇";
                    break;
                case ';':
                    result = result+ "；";
                    break;
                case '(':
                    result = result+ "〔";
                    break;
                case ')':
                    result = result+ "〕";
                    break;
                case '+':
                    result = result+ "＋";
                    break;
                case '|'://以下为增加内容　將半角轉為全角
                    result = result+ "｜";
                    break;
                case '$':
                    result = result+ "＄";
                    break;
                case '&':
                    result = result+ "＆";
                    break;
                case '%':
                    result = result+ "％";
                    break;
                case '#':
                    result = result+ "＃";
                    break;
                case '?':
                    result = result+ "？";
                    break;
                case '[':
                    result = result+ "［";
                    break;
                case ']':
                    result = result+ "］";
                    break;
                case '*':
                    result = result+ "＊";
                    break;
                case '/':
                    result = result+ "／";
                    break;
                case '=':
                    result = result+ "＝";
                    break;
                default:
                    result = result+ value.charAt(i);
                    break;
            }
        }
        result.replace('--',"－－");
        return result;
    }
});