/**
 * 职称评审_随机抽选/撤选专家选择控件
 * @createtime August 24, 2015 9:07:55 PM
 * @author chent
 * 
 * */
Ext.define('ExpertPicker.RandomSelection',{
	requires:['EHR.extWidget.proxy.TransactionProxy'],
	addCallback:Ext.emptyFn,//抽选回调函数
	rollBackCallback:Ext.emptyFn,//撤选回调函数
	currentState:'',//当前状态  1：抽选 2：抽调
	moduleType:'1',//模块区分 1：评委会 2：学科组
	currentId:'',
	//bakRecords:[],
	needRefresh:true,//是否需要刷新  true：重新抽取  false：在现有数据基础上继续抽取
	configSchemeNum : 0,//配置方案数量
	configIdArray:[],
	seq:0,//每次抽选/撤选时的编号
	width : window.parent.window.document.getElementById('center_iframe').offsetWidth,
	height : window.parent.window.document.getElementById('center_iframe').offsetHeight,
	constructor:function(config) {
		this.addCallback = config.addCallback;
		this.rollBackCallback = config.rollBackCallback;
		this.currentId = config.committeeId;
		if(Ext.isEmpty(this.rollBackCallback)){
			this.moduleType = '2';
		}
		this.init();
		this.bindResizeEvent();
	},
	/**
	 * 绑定窗口resize
	 * haosl add 窗口自适应 2017-9-16
	 */
	bindResizeEvent : function(){
		Ext.EventManager.onWindowResize(function() {
			this.width = window.parent.window.document.getElementById('center_iframe').offsetWidth;
			this.height = window.parent.window.document.getElementById('center_iframe').offsetHeight;
			var selWin = Ext.getCmp("random_selection_window_id");
			if(selWin){
				selWin.setWidth(this.width);
				selWin.setHeight(this.height);
			}
		}, this);
	},
	// 初始化函数
	init:function(url) {
		
		if(!Ext.util.CSS.getRule('.scheme-selected-cls')){
			Ext.util.CSS.createStyleSheet(".scheme-selected-cls{text-decoration:underline;}","underline");
		}
		
		var map = new HashMap();
		map.put('id', this.currentId);
		map.put('moduleType', this.moduleType);
	    Rpc({functionId:'ZC00002112',async:false,success:this.getTableOK,scope:this},map);
	},
	// 加载表单
	getTableOK:function(form, action){
		
		var obj = Ext.decode(Ext.decode(form.responseText).tableConfig);
		var tableObj = new BuildTableObj(obj);
		var tableComp = tableObj.getMainPanel();//表格控件
		
		var addImg = Ext.create('Ext.Img', {//添加条件
			src:'/images/add.gif',
			style:'cursor:pointer;',
			width:16,
			height:16,
			margin:'0 0 10 5',
			listeners: {
		        click: {
		            element: 'el', 
		            scope:this,
		            fn: function(a, o){this.addSchemeItem(true, 0);}
		        }
			}
		});
		
		var configLabel = undefined;//规则定义
		configLabel = Ext.widget('label',{
			xtype:'label',
			text:'规则定义',
			style:'cursor:pointer;color:#22549b;',
			margin:'0 0 10 5',
			listeners:{
				click: {
					element:'el',
		            fn: function(a, o){
		            	Ext.Loader.setConfig({
    						enabled:true,
    						paths: {
    							//筛选条件页面
    							'FilterConditionsURL':'/module/jobtitle/expertpicker'
    						}
    					});
    					var obj = new Object();
						obj.subModuleId = this.getSubModuleId();
    					Ext.require("FilterConditionsURL.FilterConditions",function(){
    						Ext.create('FilterConditionsURL.FilterConditions', obj);
    					});
		            }
		        },
		        scope:this
			}
		});
		
		var addConfigLabel = undefined;//添加方案
		addConfigLabel = Ext.widget('label',{
			xtype:'label',
			text:'添加方案',
			style:'cursor:pointer;color:#22549b;',
			margin:'0 0 10 5',
			listeners:{
				click: {
					element:'el',
		            fn: function(a, o){
		            	this.addConfig();
		            }
		        },
		        scope:this
			}
		});
		
		var rollbackExpertLabel = undefined;
		if(this.moduleType == '1'){//评委会时才显示【专家撤选】按钮
			rollbackExpertLabel = Ext.widget('label',{
				id:'rollbackExpertLabel',
				text : '专家撤选',
				margin:'0 0 0 20',
				style:'cursor:pointer;color:#22549b;',
				listeners : {
					click:{
						element:'el',
						fn:function(F, obj) {
							if(this.validateSchemeConfig()){
								
								
//								if(this.currentState == '1'){
									this.needRefresh = true;
									this.seq = 0;
//								} else {
//									this.needRefresh = false;
//								}
								
								this.currentState = '2';
								var addExpertLabel = Ext.getCmp('addExpertLabel');
								var rollbackExpertLabel = Ext.getCmp('rollbackExpertLabel');
								if(rollbackExpertLabel){
									rollbackExpertLabel.addCls('scheme-selected-cls');
								}
								if(addExpertLabel){
									addExpertLabel.removeCls('scheme-selected-cls');
								}
								
								var len = this.configIdArray.length;
								var randomIndex = Ext.Number.randomInt(0, len-1);
								var num = this.configIdArray[randomIndex];
								
								var configMap = this.getSchemeConfig(num);
								this.selectSchemeLoadTable(configMap.map);
								
								this.selectedScheme(num);
							}
						}
					},
			        scope:this
				}
			});
		}
		
		var addExpertLabel = undefined;//专家抽选
		addExpertLabel = Ext.widget('label',{
			xtype : 'label',
			id:'addExpertLabel',
			text : '专家抽选',
			margin:'0 0 0 20',
			style:'cursor:pointer;color:#22549b;',
			listeners : {
				click:{
					element:'el',
					fn:function() {
						if(this.validateSchemeConfig()){
							if(this.currentState == '2' || this.configIdArray.length > 1){
								this.needRefresh = true;
								this.seq = 0;
							} else {
								this.needRefresh = false;
								this.seq ++ ;
							}
							
							this.currentState = '1';
							var addExpertLabel = Ext.getCmp('addExpertLabel');
							var rollbackExpertLabel = Ext.getCmp('rollbackExpertLabel');
							if(addExpertLabel){
								addExpertLabel.addCls('scheme-selected-cls');
							}
							if(rollbackExpertLabel){
								rollbackExpertLabel.removeCls('scheme-selected-cls');
							}
							
							var len = this.configIdArray.length;
							var randomIndex = Ext.Number.randomInt(0, len-1);
							var num = this.configIdArray[randomIndex];
							
							
							var configMap = this.getSchemeConfig(num);
							this.selectSchemeLoadTable(configMap.map);
							
							this.selectedScheme(num);
						}
					}
				},
		        scope:this
			}
		});
		var itemsNum = Math.round(this.width/(320+20));//筛选条件和排列个数
		
		// 所属组织
		var container = Ext.widget('container', {
			id:'maincontainer',
			width:this.width-20,
			maxHeight:310,
			scrollable:'y',
		    layout: {
		        type: 'vbox'
		    },
		    items : [{
				xtype : 'container',
				layout : 'hbox',
				items : [{
					xtype : 'box',
					margin:'0 0 0 0',
					width:18,
					html : '<img style="float:left" src="/components/querybox/images/wuxing.png"/>'
				},{
					xtype:'label',
					margin:'0 5 10 0',
					text:'筛选规则'
				}, configLabel, addConfigLabel/*, addImg*/]
			}/*,{
				xtype:'container',
				id:'schemeItemsContainer_0',
				layout: {
			        type: 'table',
			        columns: itemsNum
				},
				width:this.width-20,
				maxHeight:108,
				scrollable:'y',
				padding:'0 0 0 20',
				items : []
		    }*/,{
				xtype:'container',
				layout: 'hbox',
				items:[
					rollbackExpertLabel, 
					addExpertLabel
				]
		    }]
		});
		
		var toolBar = Ext.getCmp("random_selection_toolbar");
		toolBar.add(container);
		
		Ext.create('Ext.window.Window', {
			id : 'random_selection_window_id',
		    title: '专家抽取',
			layout:'fit',
			modal:true,
			width:this.width,
			height:this.height,
			border:false,
			resizable:false,
			items:[tableComp],
			buttonAlign:'center',
			buttons : [{
					xtype : 'button',
					text : '删除',
					margin:'0 10 0 0',
					handler : function() {this.deletePerson();},
					scope:this
				},{
					xtype : 'button',
					text : common.button.ok,
					margin:'0 10 0 0',
					handler : function() {this.enter();},
					scope:this
				},{
					xtype : 'button',
					text : "取消",
					margin:'0 0 0 0',
					handler : function() {this.closePicker();},
					scope:this
			}],
			listeners: {
				beforeclose:function(){
					this.configIdArray.length=0;
				},
				resize:function(){
					//haosl add 窗口自适应 2017-9-16
					tableComp.setWidth(this.width);
					tableComp.setHeight(this.height);
					var maincontainer = Ext.getCmp("maincontainer");
					if(maincontainer)
						maincontainer.setWidth(this.width-20);
					for(var num=1; num<=this.configSchemeNum; num++){
						var schemeItemsContainer = Ext.getCmp('schemeItemsContainer_'+num)
						if(schemeItemsContainer)
							schemeItemsContainer.setWidth(this.width-20);
						var panel = Ext.getCmp('panel_'+num);
						if(panel)
							panel.setWidth(this.width-20);
						var displayfield = Ext.getCmp('displayfield_'+num);
						if(displayfield)
							displayfield.setWidth(this.width-100);
					}
				},
				scope:this
	        }
		}).show();
		
		//this.addSchemeItem(false, 0);//初始化时，先增加一个条件，第一个不可以删除
		this.addConfig(true);//true表示是第一个方案，不能删除
	},
	//确定
	enter:function(){
		if(!this.validateSchemeConfig()){//校验
			return ;
		}
		
		var store = Ext.data.StoreManager.lookup('random_selection_dataStore');

		// 校验
		var msg = undefined;
		var selectData = Ext.getCmp('random_selection_tablePanel').getSelectionModel().getSelection();//获取选中数据
		if(this.currentState == ''){
			if(this.moduleType == '1'){
				msg = "没有进行筛选，选择点击【专家抽选】或【专家撤选】按钮进行筛选！";
			} else if(this.moduleType == '2') {
				msg = "没有进行筛选，选择点击【专家抽选】按钮进行筛选！";
			}
		} else if(selectData.length == 0){
			msg = "没有选中记录！";
		}
		if(!Ext.isEmpty(msg)){
			Ext.showAlert(msg);
			return ;
		}
		
		// 抽选
		var text = "抽选";
		if(this.currentState == '2'){
			text = "撤选";
		}
		Ext.showConfirm('确认'+text+'该'+selectData.length+'名专家？',function(btn){ 
			if(btn=='yes'){
				var arrayList = new Array();
				for(var i=0; i<selectData.length; i++){
					var w0101_e = selectData[i].data.w0101_e;
					arrayList.push(w0101_e);
				}
				if(this.currentState == '1'){
					this.addCallback(arrayList);
				} else if(this.currentState == '2'){
					this.rollBackCallback(arrayList);
				}
				Ext.getCmp('random_selection_window_id').close();
			}
		},this);
		
	},
	// 删除
	deletePerson:function(){
		
		var selectData = Ext.getCmp('random_selection_tablePanel').getSelectionModel().getSelection();//获取选中数据
		if(selectData.length == 0){
			Ext.showAlert('没有选中记录！');
			return ;
		}
		Ext.showConfirm('是否删除选中记录？',function(btn){ 
			 if(btn=='yes'){
				var store = Ext.data.StoreManager.lookup('random_selection_dataStore');
				store.remove(selectData);//删除
			 }
		});
		
	},
	// 关闭
	closePicker:function(){
		Ext.getCmp('random_selection_window_id').close();
	},
	// 获取数据集
    getComBoStore : function() {
    	var store = Ext.create('Ext.data.Store', {
			fields:['key', 'value'],
			proxy:{
				type: 'transaction',
		        functionId:'ZC00002115',
				extraParams:{
			       subModuleId: this.getSubModuleId()
				},
				 reader: {
					  type: 'json',
					  root: 'randomScheme'         	
				}
			},
			autoLoad: true
		});
		
		return store;
	},
	// 查询
	selectSchemeLoadTable:function(configMap){
		
		var w0101List = new Array();
		if(!this.needRefresh){
			var store = Ext.data.StoreManager.lookup('random_selection_dataStore');
			var records = store.data.items;
			for(var i=0; i<records.length; i++){
				var w0101_e = records[i].data.w0101_e;
				var schemeid = records[i].data.schemeid;
				var seq = records[i].data.seq;
				
				var map = new HashMap();
				map.put("w0101", w0101_e);
				map.put("schemeid", schemeid);
				map.put("seq", seq);
				w0101List.push(map);
				//w0101List.push(w0101_e);
				//w0101List.push(schemeid);
			}
		}
		var map = new HashMap();
		map.put("schemeConfig", configMap);
		map.put("state", this.currentState);//当前状态  1：抽选 2：抽调
		map.put("w0101List", w0101List);
		map.put("seq", this.seq);
		Rpc({functionId:'ZC00002113',async:false,success:function(form,action){
			this.loadTable();
		},scope:this},map);
	},
	// 重新加载数据列表
	loadTable:function(form){
		var store = Ext.data.StoreManager.lookup('random_selection_dataStore');
//		// 备份当前数据
//		var records = store.data.items;
//		this.bakRecords = [];
//		for(var i=0; i<records.length; i++){
//			var record = records[i];
//			this.bakRecords.push(record);
//		}
//
//		store.on('load', function(){
//			if(!this.needRefresh){
//				store.insert(store.getCount(), this.bakRecords);//追加备份数据
//			}
//			
//		},this);

		// 重载
		store.currentPage=1;
		store.load();
		
	},
	// 增加条件定义项   value：true是第一个   num：方案号
	addSchemeItem:function(value, num){
		
		var isFirst = false;// 是第一个
		if(!value){
			isFirst = true;
		}
		
		var store = this.getComBoStore();
		var comBo = Ext.create('Ext.form.ComboBox', {
		    store: store,
		    labelAlign:'right',
		    queryMode: 'local',
		    displayField: 'value',
		    valueField: 'key',
		  	editable:false,
		  	allowBlank: false,
	        listeners: {  
				afterRender: function(combo) {
					store.on('load', function(){
						//var firstValue = store.data.items[0];
						//combo.setValue(firstValue);
					});
	            },
	            expand:function(combo){
	            	var store = combo.getStore();
				    
					var num = combo.up('container').up('container').id.substring(21);
	            	store.filter({
                        filterFn: function(node) {
			            	var configMap = this.getSchemeConfig(num);
                        	var visible = true;

                        	var nodesKey = node.data.key;
                        	if(configMap.get(nodesKey) != undefined) {//排除已选项
                        		visible = false;
                        	}
                        	
                        	return visible;
                        },
                        scope:this
                    });
                    store.load();
	            },
	            change:function(){
	            	//var store = Ext.data.StoreManager.lookup('random_selection_dataStore');
	            	//store.removeAll();
	            },
	            scope:this
	        }
		});
		
		var randomNum = Ext.create('Ext.form.field.Text', {
			fieldLabel:'人数',
	        labelAlign:'right',
	        allowBlank: false,
	        width:100,
	        labelWidth:40,
	        //value:randomNumValue,
	        value:'',
	        regex:/^([1-9]\d*|[0]{1,1})$/,
	        regexText:'必须为正整数',
	        margin:'0 0 0 10'
		});
		
		var delImg = undefined;
		if(!isFirst){//第一个不可删除
			
			delImg = Ext.create('Ext.Img', {
				src:'/images/del.gif',
				style:'cursor:pointer;',
				width:16,
				height:16,
				margin:'2 0 0 2',
				listeners: {
			        click: {
			            element: 'el', 
			            scope:this,
			            fn: function(F, obj){
			            	// 删除组件
			            	var currentContainer = Ext.getCmp(obj.id).up('container');
			            	var upContainer = currentContainer.up('container');//父组件
			            	upContainer.remove(currentContainer);
			            	this.resizeMainContainer();
			            }
			        }
				}
			});
		}
		
		
		var container = Ext.widget('container', {
			layout : 'hbox',
			style:"display:inline-block;",
			width:320,
			padding:'0 40 5 0',
			items : [comBo, randomNum, delImg]
		});
		
		var schemeItemsContainer = Ext.getCmp('schemeItemsContainer_'+num);
		if(schemeItemsContainer){
			schemeItemsContainer.add(container);
		}
		
		this.resizeMainContainer();
	},
	// 校验抽选规则信息
	validateSchemeConfig:function(){
		var flag = true;
		
		for(var num=1; num<=this.configSchemeNum; num++){
			var schemeItemsContainer = Ext.getCmp('schemeItemsContainer_'+num);
			if(schemeItemsContainer){
				var configArray = schemeItemsContainer.query('container');
				
				for(var i=0; i<configArray.length; i++){
					if(!configArray[i].items.items[0].isValid() | !configArray[i].items.items[1].isValid()) {//校验
						flag = false;
					}
				}
			}
		}
		
		return flag;
	},
	// 获取抽选规则信息
	getSchemeConfig:function(num){
		
		var schemeConfig = new Ext.util.HashMap()
		
		var schemeItemsContainer = Ext.getCmp('schemeItemsContainer_'+num);
		if(schemeItemsContainer){
			var configArray = schemeItemsContainer.query('container');
			
			for(var i=0; i<configArray.length; i++){
				var key = configArray[i].items.items[0].getValue();
				var value = configArray[i].items.items[1].getValue();//用户输入的值
				var sum = 0;// 已经存在记录的数量
				
				var store = Ext.data.StoreManager.lookup('random_selection_dataStore');
				var records = store.data.items;
	
				if(records.length > 0 && !this.needRefresh){
					for(var j=0; j<records.length; j++){
						var schemeid = records[j].data.schemeid;
						if(key == schemeid){
							sum++;
						}
					}
					
					if(sum > 0){
						if(value - sum > 0){
							value = value - sum ;
						} else {
							value = 0;
						}
					}
				}
				if(!Ext.isEmpty(key)){
					schemeConfig.add(key, value);
				}
			}
		}
		
		return schemeConfig;
	},
	// 添加方案  isfirst:true表示是第一个方案，不能删除
	addConfig:function(isfirst){
		
    	this.configSchemeNum++;

    	this.configIdArray.push(this.configSchemeNum);
    	
    	var maincontainer = Ext.getCmp('maincontainer');
    	
    	var addImg = Ext.create('Ext.Img', {//添加条件
    		id:'addImg_'+this.configSchemeNum,
			src:'/images/add.gif',
			style:'cursor:pointer;',
			width:16,
			height:16,
			margin:'3 0 3 40',
			listeners: {
		        click: {
		            element: 'el', 
		            scope:this,
		            fn: function(a, o){
		            	var num = o.id.substring(7);
		            	this.addSchemeItem(true, num);
	            	}
		        }
			}
		});
    	
		var selImg = Ext.create('Ext.Img', {
    		id:'selschemeimg_'+this.configSchemeNum,
			src:'',
			width:16,
			height:16
		});
		
    	var line = Ext.widget('displayfield', { 
    		id:"displayfield_"+this.configSchemeNum,
    		width:this.width-100,
        	value: '<hr style="width:100%;border-top:0px dashed #C5C5C5; border-bottom:1px dashed #C5C5C5;; border-left:0px;" />',
        	height:2
    	});
    	
    	var delImg = Ext.create('Ext.Img', {
    		id:'delschemeimg_'+this.configSchemeNum,
			src:'/images/del.gif',
			style:'cursor:pointer;',
			width:16,
			height:16,
			listeners: {
		        click: {
		            element: 'el', 
		            scope:this,
		            fn: function(F, obj){
		            	// 删除组件
		            	var num = obj.id.substring(13);
		            	maincontainer.remove('schemeItemsContainer_'+num);
		            	maincontainer.remove('panel_'+num);
		            	maincontainer.remove('addImg_'+num);
		            	
		            	var removeNum = Number(num);
		            	Ext.Array.remove(this.configIdArray, removeNum);
		            	
		            	
		            	for(var i=0; i<this.configIdArray.length; i++){//更新序号
							var label = Ext.getCmp('number_'+this.configIdArray[i]);
							if(label){
								label.setText((i+1)+'、');
							}
								
						}
		            	this.resizeMainContainer();
		            }
		        }
			}
		});
		
		if(isfirst){
			delImg = undefined;
		}
    	
    	var itemsNum = Math.round(this.width/(320+20));//筛选条件和排列个数
    	var schemeItemsContainer = Ext.widget('container', {
    		id:'schemeItemsContainer_'+this.configSchemeNum,
			width:this.width-20,
			maxHeight:108,
			scrollable:'y',
			padding:'0 0 0 40',
			items : []
    	});
    	
    	var panel = Ext.widget('panel', {
    		id:'panel_'+this.configSchemeNum,
    		border:false,
    		layout:{
    			type:'hbox'
    		},
    		items:[selImg,{
    				xtype:'label', id:'number_'+this.configSchemeNum, text:this.configIdArray.length+'、',margin:'0 5 0 5'
    			}, line, delImg
			]
    	});
    	var itemsLength = maincontainer.items.items.length;
    	maincontainer.insert(itemsLength-1, addImg);
    	maincontainer.insert(itemsLength-1, schemeItemsContainer);
    	maincontainer.insert(itemsLength-1, panel);
    	
    	this.addSchemeItem(false, this.configSchemeNum);//初始化时，先增加一个条件，第一个不可以删除
		            
	},
	// 方案选中后效果
	selectedScheme:function(num){
		//移除选中效果
		for(var i=0; i<this.configIdArray.length; i++){
			var img = Ext.getCmp('selschemeimg_'+this.configIdArray[i]);
			img.setSrc('');
		}
		
		// 增加选中效果
		var img = Ext.getCmp('selschemeimg_'+num);
		if(img){
			img.setSrc('/images/new_module/icon_tgsx.gif');
		}
	},
	// 规则定义的唯一标识
	getSubModuleId :function(){
		var subModuleId = '';
		
		if(this.moduleType == '1'){//评委会
			subModuleId = 'zc_com_'+this.currentId;
		} else if(this.moduleType == '2') {//学科组
			subModuleId = 'zc_sub_'+this.currentId;
		}
		
		return subModuleId;
	},
	//筛选规则自适应设置大小
	resizeMainContainer:function(){
		var height = 0;
		var items = Ext.getCmp("maincontainer").items.items;
		for(var i = 0; i < items.length; i++) {
			height += items[i].lastBox.height;
		}
		if(height < 310)
			Ext.getCmp("maincontainer").setHeight(height+20);
	}
});