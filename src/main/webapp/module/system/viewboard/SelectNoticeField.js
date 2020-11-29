/**
 * 公告维护--选择列表指标
 * @createtime Mar 02, 2017 9:07:55 AM
 * @author chent
 * 
 * API：
 * 调用方式：		
	Ext.require('NoticePath.SelectNoticeField', function(){
		Ext.create("NoticePath.SelectNoticeField", {items:[],callBackFunc:''});
	});
 * 参数说明：
	items：备选指标（必须）。如：[{dataName:'a0100',dataValue:'员工编号',selected:'1'},{dataName:'a0101',dataValue:'姓名',selected:'0'}]。
		   dataName指标号、dataValue指标名、selected是否已选状态
	callBackFunc：回调函数（必须）
	title：标题（非必须）,默认：选择指标
	width：宽（非必须），默认：550
	height：高（非必须），默认：420
 * 
 * */
Ext.define('NoticePath.SelectNoticeField', {
	items:[],//备选指标（必须）。
	callBackFunc : '',//回调函数（必须）
	title:'选择指标',//标题（非必须）
	width:550,//宽（非必须）
	height:440,//高（非必须）
	flag:false,//招聘公示内容分组指标
	jsonStr:'',//分组指标
	nextStr:'',
	constructor : function(config) {
		this.callBackFunc = config.callBackFunc;
		this.items = Ext.isEmpty(config.preItems)?config.items:config.preItems;
		this.nextItems = config.nextItems;
		this.preItems = config.preItems;
		this.title = Ext.isEmpty(config.title)?'选择指标':config.title;
		this.alternativetitle = Ext.isEmpty(config.alternativetitle)?common.label.alternativeField:config.alternativetitle;
		this.hasSelectTitle = Ext.isEmpty(config.hasSelectTitle)?common.label.hasSelectField:config.hasSelectTitle;
		
		this.oldTitle = Ext.isEmpty(config.title)?'选择指标':config.title;
		this.oldAlternativetitle = Ext.isEmpty(config.alternativetitle)?common.label.alternativeField:config.alternativetitle;
		this.oldHasSelectTitle = Ext.isEmpty(config.hasSelectTitle)?common.label.hasSelectField:config.hasSelectTitle;
		this.width = Ext.isEmpty(config.width)?550:config.width;
		this.height = Ext.isEmpty(config.height)?420:config.height;
		this.flag = Ext.isEmpty(config.flag)?false:config.flag;
		this.jsonStr = Ext.isEmpty(config.jsonStr)?'':config.jsonStr;
		
		this.init();
	},
	init : function() {
		this.createMainWindow();
		this.initQueryBox();// 检索框
		this.initLeftPanel();// 左侧备选指标
		this.initButtonsPanel();// 中间按钮
		this.initRightPanel();// 右侧已选指标
		if(this.flag)
			this.insertItem(this.jsonStr);
	},
	// 主窗口
	createMainWindow : function() {

		this.win = Ext.widget("window", {
			title : this.oldTitle,
			width : this.width,
			height : this.height,
			bodyPadding : '3 2',
			modal : true,
			resizable : false,
			closeAction : 'destroy',
			items : [{
				xtype : 'panel',
				layout : 'hbox',
				border : false,
				items : [{
							xtype : 'container',
							border : false,
							layout : 'vbox',
							items : [{
										xtype : 'container',
										border : false,
										id : 'fieldPanelId'
									}, {
										xtype : 'container',
										border : false,
										id : 'leftPanelId'
									}]
						}, {
							xtype : 'container',
							border : false,
							id : 'buttonId'
						}, {
							xtype : 'container',
							border : false,
							id : 'rightPanelId'
						}]
			}],
			//haosl 按钮上方添加横线  20170420
			dockedItems: [{
			    xtype: 'toolbar',
			    style:"border-top:1px solid silver",
			    margin:'0 -4 2 -4',
			    dock: 'bottom',
			    ui: 'footer',
			    defaults:{
			    	minWidth:75
			    },
			    layout:{
			    	pack:'center'
			    },
			    items: [
			    	{
						text : common.label.preStep, // 上一步
						style:'margin-top:4px;',
						id:'preStep',
						hidden:true,
						handler : function() {
							this.preStep();
						},
						scope : this
					}, {
						text : common.button.ok, // 确定
						style:'margin-top:4px;',
						id:'enter',
						hidden:this.nextItems?true:false,
						handler : function() {
							this.saveItems();
						},
						scope : this
					}, {
						text : common.label.nextStep, // 下一步
						style:'margin-top:4px;',
						id:'nextStep',
						hidden:this.nextItems?false:true,
						handler : function() {
							this.next();
						},
						scope : this
					}, {
						text : common.button.cancel, // 取消
						style:'margin-top:4px;',
						handler : function() {
							this.win.close();
						},
						scope : this
					}
			    ]
			}]
		});
		this.win.show();
	},
	// 检索框
	initQueryBox : function() {

	},
	// 左侧面板
	initLeftPanel : function() {
		
		// 初始数据源
		var leftStore = new Ext.data.ArrayStore({
			storeId : 'leftStoreId',
            fields: ['dataName','dataValue']
        });
        
		if (this.items && this.items.length > 0) {
			Ext.each(this.items, function(data) {
				if(data && data.selected == 0){
					leftStore.insert(leftStore.getCount(), [{
						dataName : data.dataName,
						dataValue : data.dataValue
					}]);
				}
			})
		}
		// 创建左侧面板
		this.leftGrid = Ext.create('Ext.grid.Panel', {
					store : leftStore,
					width : this.width/2 - 40,
					height : this.height - 90,//haosl update 20170420 this.height - 80=>this.height - 90 
					border : true,
					multiSelect : true,
					forceFit : true,
					hideHeaders : true,
					columns : [{
								text : common.label.itemName,
								menuDisabled : true,
								dataIndex : 'dataName'
							},// 项目名称
							{
								text : common.label.itemName,
								menuDisabled : true,
								dataIndex : 'dataValue',
								hidden : true
							}

					],
					listeners : {
						'celldblclick' : function(grid, td, cellIndex, record,
								tr, rowIndex, e) {
							this.addMode(false);
						},
						scope:this
					}
				});
		
		// 备选指标menubar
		var menubar = Ext.create('Ext.toolbar.Toolbar', {
			height : 30,
			items : [{
				xtype : 'label',
				text : this.oldAlternativetitle
				}]
		});
		this.leftGrid.addDocked(menubar);
		
		Ext.getCmp('leftPanelId').add(this.leftGrid);
	},

	// 右侧面板
	initRightPanel : function() {
		
		// 初始数据源
		var rightStore = new Ext.data.ArrayStore({
			storeId : 'rightStoreId',
            fields: ['dataName','dataValue']
        });
        
		if (this.items && this.items.length > 0) {
			Ext.each(this.items, function(data) {
				if(data && data.selected == 1){
					rightStore.insert(rightStore.getCount(), [{
						dataName : data.dataName,
						dataValue : data.dataValue
					}]);
				}
			})
		}

		// 创建右侧面板
		this.rightGrid = Ext.create('Ext.grid.Panel', {
			store : rightStore,
			width : this.width/2 - 25,
			height : this.height - 90,//haosl update 20170420 this.height - 80=>this.height - 90 
			border : true,
			multiSelect : true,
			forceFit : true,
			hideHeaders : true,
			viewConfig : {
				plugins : {
					ptype : 'gridviewdragdrop'
				}
			},
			columns : [{
						text : common.label.itemName,
						menuDisabled : true,
						dataIndex : 'dataName'
					}, {
						text : common.label.itemName,
						menuDisabled : true,
						dataIndex : 'dataValue',
						hidden : true
					}

			],
			listeners : {
				'celldblclick' : function(grid, td, cellIndex, record,
						tr, rowIndex, e) {
					this.delMode(false);
				},
				scope:this
			}
		});

		// 已选指标menubar
		var menubar1 = Ext.create('Ext.toolbar.Toolbar', {
			items : [{
				xtype : 'label',
				text : this.oldHasSelectTitle
					// 已选指标
				}]
		});
				
		this.rightGrid.addDocked(menubar1);

		Ext.getCmp('rightPanelId').add(this.rightGrid);
	},

	// 中间按钮
	initButtonsPanel : function() {
		var butPanel = Ext.widget({
			xtype : 'panel',
			border : false,
			width : 50,
			height : this.height - 80,
			layout : {
				type : 'vbox',
				align : 'center',
				pack : 'center'
			},
			defaults : {
				margin : '5 0 0 0'
			},
			items : [{
						xtype : 'button',
						text : common.button.allselect, // 全选
						handler : function() {
							this.addMode(true);
						},
						scope:this
					},{
						xtype : 'button',
						text : common.button.addfield, // 添加
						handler : function() {
							this.addMode(false);
						},
						scope:this
					}, {
						xtype : 'button',
						text : common.button.todelete, // 删除
						handler : function() {
							this.delMode(false);
						},
						scope:this
					}, {
						xtype : 'button',
						text : common.button.allreset, // 全撤
						handler : function() {
							this.delMode(true);
						},
						scope:this
					}]
		});

		Ext.getCmp('buttonId').add(butPanel);
	},

	// 将左侧勾选的指标添加到右侧,true:全选
	addMode : function(flag) {
		// 左侧已选
		var records = new Array();
		if(flag) {
			//这么做的目的是为了记住所有的store的数据，如果直接等于，会导致在添加一个之后的第二个store的值改变了，下面方法执行错误
			var items = this.leftGrid.getStore().data.items;
			for(var i = 0; i < items.length; i++) {
				records.push(items[i]);
			}
		}else {
			records = this.leftGrid.getSelectionModel().getSelection();
		}
		if (records.length == 0) {
			Ext.showAlert(common.msg.selectAddObj);
			return;
		};
		// 插入到右侧
		Ext.Array.each(records, function(record, index, countriesItSelf) {
			var modeValue = record.get('dataValue');
			var modeName = record.get('dataName');
			// 生成要添加的model对象
			var aimMode = {
				dataName : record.get('dataName'),
				dataValue : record.get('dataValue')
			};
			// 右侧是否已有，有则无需添加
			var isAdd = true;
			this.rightGrid.getStore().each(function(item, index, count) {
				if (item.get('dataValue').toUpperCase() == modeValue.toUpperCase()) {
					isAdd = false;
					return;
				}
			});
			// 添加数据
			if (isAdd){
				if(this.preItems) {
					Ext.each(this.preItems, function(data) {
						if(modeName == data.dataName) {
							data.selected = '1';
						}
					});
				}
				if(this.nextItems) {
					Ext.each(this.nextItems, function(data) {
						if(modeName == data.dataName) {
							data.selected = '1';
						}
					});
				}
				this.rightGrid.getStore().insert(this.rightGrid.getStore().getCount(), aimMode);
				this.leftGrid.getStore().remove(record);
			}
		},this);
	},
	// 删除右侧已选指标
	delMode : function(flag) {
		var records = new Array();
		if(flag) {
			//这么做的目的是为了记住所有的store的数据，如果直接等于，会导致在添加一个之后的第二个store的值改变了，下面方法执行错误
			var items = this.rightGrid.getStore().data.items;
			for(var i = 0; i < items.length; i++) {
				records.push(items[i]);
			}
		}else {
			records = this.rightGrid.getSelectionModel().getSelection();
		}
		Ext.Array.each(records, function(record) {
			var modeValue = record.get('dataValue');
			var modeName = record.get('dataName');
			// 生成要添加的model对象
			var aimMode = {
					dataName : record.get('dataName'),
					dataValue : record.get('dataValue')
			};
			if (records.length == 0) {
				Ext.showAlert(common.msg.selectDelObj);
				return;
			}
			var modeName = record.get('dataName');
			if(this.preItems) {
				Ext.each(this.preItems, function(data) {
					if(modeName == data.dataName) {
						data.selected = '0';
					}
				});
			}
			if(this.nextItems) {
				Ext.each(this.nextItems, function(data) {
					if(modeName == data.dataName) {
						data.selected = '0';
					}
				});
			}
			this.leftGrid.getStore().insert(this.leftGrid.getStore().getCount(), aimMode);
			this.rightGrid.getStore().remove(record);
		},this);
	},
	// 确定、回传已选
	saveItems : function() {
		var array = new Array();//已选记录
		this.rightGrid.getStore().each(function(item){
			array.push(item);
		});
		if(array.length<1){
			Ext.showAlert("请选择分组");
			return;
		}
		var groupValue = '';
		if(this.flag&&Ext.getCmp('selectField').value)
			groupValue = Ext.getCmp('selectField').value;
		this.win.close();
		if(this.callBackFunc){//回传已选
			Ext.callback(eval(this.callBackFunc),null,[array,groupValue,this.nextStr]);
		}
	},
	insertItem : function(jsonStr){
		//插入分组指标
		this.win.dockedItems.items[1].insert(0,'->');
		var store = Ext.create('Ext.data.Store',{
			fields:['dataName','dataValue'],
			data:jsonStr
		});
		
		this.win.dockedItems.items[1].insert(0,{
		   				xtype:'combobox',
				     	id:'selectField',
		 	    		fieldLabel:'分组指标',
		 	    		emptyText: '--请选择--',
		 		  	    store: store,
		 	    		queryMode: 'local',
		 		  	   	displayField: 'dataName',
		 		  	    valueField: 'dataValue',
						width:236,
						labelAlign:'left',
						editable:false,
						overflowY:'scroll',
						labelWidth:60
		   		  });
	},
	next : function() {
		var next = '';
		this.rightGrid.getStore().each(function(item){
			next += "," + item.data.dataValue;
		});
		if(next == '') {
			Ext.showAlert("请选择分组");
			return;
		}
		this.nextStr = next;
		this.win.close();
		this.items = this.nextItems;
		this.oldAlternativetitle = common.label.alternativeField;
		this.oldHasSelectTitle = common.label.hasSelectField;
		this.oldTitle = "选择指标";
		this.init();
		Ext.getCmp("preStep").show();
		Ext.getCmp("enter").show();
		Ext.getCmp("nextStep").hide();
		
	},
	preStep : function() {
		this.win.close();
		this.items = this.preItems;
		this.oldAlternativetitle = this.alternativetitle;
		this.oldHasSelectTitle = this.hasSelectTitle;
		this.oldTitle = this.title;
		this.init();
		Ext.getCmp("preStep").hide();
		Ext.getCmp("enter").hide();
		Ext.getCmp("nextStep").show();
	}
})