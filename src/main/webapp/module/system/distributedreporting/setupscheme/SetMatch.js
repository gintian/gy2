/**
 * 创建导入方案
 */
Ext.define('SetupschemeUL.SetMatch',{
	schemeid:"",//id
	rowindx:"",//行号
	unitcodeid:"",
	constructor:function(config) {
		setMatch_me = this;
		setMatch_me.schemeid = config.schemeid;
		setMatch_me.rowindx = config.rowindx;
		setMatch_me.unitcodeid = config.unitcodeid;
		this.init(config);
	},
	init:function(config){
		var schemeid = config.schemeid;
		var rowindx = config.rowindx;
		var unitcodeid = config.unitcodeid;
		var map = new HashMap();
		map.put("schemeid", schemeid);
		map.put("unitcodeid", unitcodeid);
		Rpc({
			functionId : 'SYS0000003035',
			async : false,
			success : function(form, action) {
				var result = Ext.decode(form.responseText);
				if (result.sflag) {
					setMatch_me.setMatchWin();
				} else {
					Ext.Msg.alert(setmatch_tips, setmatch_before_tips);// 请先定义数据标准
				}
			}
		}, map);
	},
	/**
	 * 设置对应关系窗口
	 */
	setMatchWin : function(){
		var currtsetid ="";//保存过滤方案时选中的子集指标
		var currtdesc = "";//保存过滤方案时选中的子集名称
		var unitcodeid = setMatch_me.unitcodeid;//选中的单位编码
		//主window窗口
		var win = Ext.create("Ext.window.Window", {
					title : '<p style="font-size: 15px;">'+setmatch_title+'</p>',//设置对应关系
					height : 540,
					resizable : false,// 禁止缩放
					width : 540,
					id:"matchwin",
					modal : true,
					layout : "vbox",
					fieldStoreMap:{},//window全局储存对应指标store的对象，格式[e0122:store，a0107:store,...]指标名:store对象
					fieldMatchArr:[],
					thisFieldMatchArr:[]
				});
		var stepview = Ext.widget("stepview", {
			margin:"0 0 0 10",
			listeners : {
				stepchange : function(stepview, step) {
					if(stepview.currentIndex==1){//下一步
						//隐藏取消下一步，显示上一步确定
						quitButton.setHidden(true);
						nextButton.setHidden(true);
						lastButton.setHidden(false);
						sureButton.setHidden(false);
						//显示代码对应容器，隐藏子集列表容器
						oprationPanel.setHidden(true);
						codeMatchPanel.setHidden(false);
					}else if(stepview.currentIndex==0){//上一步
						// 显示取消下一步，隐藏上一步确定
						quitButton.setHidden(false);
						nextButton.setHidden(false);
						lastButton.setHidden(true);
						sureButton.setHidden(true);
						// 显示代码对应容器，隐藏子集列表容器
						oprationPanel.setHidden(false);
						codeMatchPanel.setHidden(true);
					}
				}
			},
			renderTo : Ext.getBody(),
			// currentIndex:1,
			// freeModel:true,
			stepData : [{
				name : setmatch_firststeptips
			}, {
				name : setmatch_secondsteptips
			}]
		});
		//====================第一步-子集信息form容器=======================
		var oprationPanel = Ext.create("Ext.form.Panel",{
			flex:5,
			border:0,
			margin:"10 0 0 0"
		});
		 //子集列表store
		 var fieldsetStore = Ext.create('Ext.data.Store', {
			fields : ['setid', 'desc'],
			// 数据代理服务
			proxy : {
				type : 'transaction',
				functionId : 'SYS0000003005',
				reader : {
					type : 'json',
					root: 'list'
				}
			},
			// 自动加载
			autoLoad : true
				// 每页显示多少条
			}
		);
		// 子集列表grid
		var fieldsetGridPanel = Ext.create('Ext.grid.Panel', {
					store : fieldsetStore,
					margin:"0 0 0 13",
					itemId:"fieldsetGridPanel",
					stripeRows:true,//表格是否隔行换色
					enableColumnResize:false,//禁止改变列宽
            		enableColumnMove:false,//禁止拖放列
            		columnLines:true,//列分割线
            		currtsetid:"",//选中的fieldsetid
            		currtdesc:"",//选中的fielditemid
            		unitcodeid:"",//选中的单位id
					columns : [
						{ text : setmatch_fieldset,dataIndex : 'setid', sortable:false,flex : 1,hideable: false,sortable:false  },// 子集代码
						{ text : setmatch_fieldsetindex,dataIndex : 'desc', sortable:false,flex : 2,hideable: false ,sortable:false},// 子集名称
						{ text : setmatch_filtercondition,// 过滤记录条件
							xtype : 'actioncolumn',
							align : "center",
							width : 100,
							hideable: false ,
							sortable:false,
							items : [{
								tooltip : setmatch_setfiltercondition, //设置过滤记录条件公式
								icon : rootPath+'/images/new_module/dealto_green.gif',
								handler : function(grid, rowIndex, colIndex) {
									var thisGridPanel = Ext.getCmp("matchwin").query("#fieldsetGridPanel")[0];
									thisGridPanel.currtsetid = grid.getStore().getAt(rowIndex).data.setid;// 子集指标
									thisGridPanel.currtdesc = grid.getStore().getAt(rowIndex).data.desc;// 子集指标
									var maingrid = Ext.getCmp('setupscheme001_tablePanel');
									var record=maingrid.getSelectionModel().getSelection();
									thisGridPanel.unitcodeid = (record[0].data.unitcode).split("`")[0];//选中的单位编码
									var map = new HashMap();
									map.put("type", "open");
									map.put("setid", thisGridPanel.currtsetid);
									map.put("unitcodeid", thisGridPanel.unitcodeid);
									var currentexpress = "";//已选中的子集的过滤条件
									Rpc({
												functionId : 'SYS0000003022',
												async : false,
												success : function(form, action) {
													var result = Ext.decode(form.responseText);
													if (result.succeed) {
														currentexpress = getDecodeStr(result.express);
													} else {
														Ext.Msg.alert(setmatch_error, setmatch_save_fail);// 保存失败
													}
												}
											}, map);
									var dataMap = new HashMap();
									dataMap.put("express",currentexpress);//默认公式
									dataMap.put("sdzgitemsetid",thisGridPanel.currtsetid);//fieldsetid
									dataMap.put("initflag",'1');//fieldsetid
									Ext.require('EHR.complexcondition.ComplexCondition',function(){
						         		var formulaWin = Ext.create("EHR.complexcondition.ComplexCondition",{dataMap:dataMap,imodule:"3",opt:"1",title:setmatch_setfiltercondition,callBackfn:setMatch_me.saveFieldSetFormula});
						         		var fieldSetCombo = Ext.getCmp("fieldItem_id");
						         		var fieldSetComboStore = fieldSetCombo.getStore();
						         		fieldSetComboStore.load({
											params:{
												value:thisGridPanel.currtsetid,
												imodule:"3",
												opt:"1",
												flag:'1'
											},
											callback: function(record, option, succes){
												fieldItemCom.setValue("");
												Ext.getCmp('codeItem_id').hide(); 
											}
										});
						         	});
							}
						}]
					,sortable:false}],
					height : 415,
					width : 504,
					renderTo : Ext.getBody()
				});
		oprationPanel.add(fieldsetGridPanel);
		//==================第二步-代码对应form容器============
		var codeMatchPanel = Ext.create("Ext.form.Panel",{
			flex:5,
			border:0,
			hidden:true,
			layout:"vbox",
			margin:"10 0 0 0"
		});
		//子集checkbox及新增删除自动对应按钮的容器
		var functionContainer = Ext.create("Ext.Container",{
			flex:1,
			margin:"0 0 0 13",
			layout:"hbox"
		});
		// 子集指标选择combobox的store
		var fielditemComboBoxStore = Ext.create('Ext.data.Store', {
			fields : ['itemid', 'itemdesc','itemiddesc','setid'],
			// 数据代理服务
			proxy : {
				type : 'transaction',
				functionId : 'SYS0000003023',
				reader : {
					type : 'json',
					root : 'list'
				}
			},
			// 自动加载
			autoLoad : true
				// 每页显示多少条
			}
		);
		//指标选择combobox
		var fielditemComboBox = Ext.create("Ext.form.ComboBox",{
			xtype : "combobox",
			store : fielditemComboBoxStore,
			height : 25,
			editable : false,
			displayField : "itemiddesc",
			valueField : "itemid",
			emptyText : setmatch_codeitemtips,// 请选择代码类指标
			listeners: {  
				select:function(combo,record,opts){
					var me = this;
					//获取子集指标setid
					var setid = record.get("setid");
					var itemid = record.get("itemid");
					//获取选择的单位unitcodeid
					var unitcodeid = setMatch_me.unitcodeid;//选中的单位编码
					//加载选择指标的需要对应的数据
					var matcheWin = Ext.getCmp("matchwin");
					var matchGridStore = matcheWin.query("#matchgird")[0].getStore();//grid的Store
					var fieldcomboStore = Ext.getCmp("fieldcombo").getStore();//combobox的Store
					var fieldStoreMap = matcheWin.fieldStoreMap;//获取全局的store的arr
					matchGridStore.proxy.extraParams.itemid = itemid;
					matchGridStore.proxy.extraParams.unitcode = unitcodeid;
					var fieldMatchArr = matcheWin.fieldMatchArr;//获取全局的匹配用的指标项obj
					var thisFieldMatchArr = matcheWin.thisFieldMatchArr;//获取当前选中全局的匹配用的指标项obj
					var map = null;
					map = new HashMap();
					map.put("itemid",itemid);
					Rpc({
						functionId : 'SYS0000003025',
						async : false,
						success : function(form, action) {
							var matcheWin = Ext.getCmp("matchwin");
							var result = Ext.decode(form.responseText);
							if (result.succeed) {
								matcheWin.thisFieldMatchArr = result.list;
							} else {
							}
						}
					}, map);
					if(!fieldStoreMap.hasOwnProperty(itemid)){//如果没有加载过此Store
						if(matchGridStore.getCount()!=0){
							var temparr = [];
							//封装此界面Store数据
							var oldItemid = matchGridStore.proxy.extraParams.beforeitemid;
							map = new HashMap();
							map.put("itemid",oldItemid);
							Rpc({
								functionId : 'SYS0000003025',
								async : false,
								success : function(form, action) {
									var result = Ext.decode(form.responseText);
									if (result.succeed) {
										fieldMatchArr = result.list;
									} else {
									}
								}
							}, map);
							for (var i = 0; i < matchGridStore.getCount(); i++) {// 循环此Store遍历所有数据
								map = new Object();
								var record = matchGridStore.getAt(i);
								map.midcodedesc = record.get('midcodedesc');
								map.codedesc = record.get('codedesc');
								for(var k = 0; k < fieldMatchArr.length; k++){
									var tempcodeobj = fieldMatchArr[k];
									var tempcodeitemid = tempcodeobj.codeitemid;
									var tempcodedesc = record.get('codedesc');
									if(tempcodeitemid == tempcodedesc){
										map.codedesc = tempcodeobj.codeitemdesc;
										continue;
									}
								}
								map.codesetid = record.get('codesetid');
								temparr.push(map);
							}
							fieldStoreMap[oldItemid] = temparr;// 将此Store的数据添加进全局对象中
						}
						// =========加载GridStore===========
						matchGridStore.proxy.extraParams.setid = setid;
						var schemeid = schemeid;//已选中的数据id号数组
						matchGridStore.proxy.extraParams.schemeid = setMatch_me.schemeid;
						matchGridStore.load();
					}else{//加载过此Store
						//封装此界面Store数据
						var map = null;
						var oldItemid = matchGridStore.proxy.extraParams.beforeitemid;
						map = new HashMap();
						map.put("itemid",oldItemid);
						Rpc({
							functionId : 'SYS0000003025',
							async : false,
							success : function(form, action) {
								var result = Ext.decode(form.responseText);
								if (result.succeed) {
									fieldMatchArr = result.list;
								} else {
								}
							}
						}, map);
						var temparr = [];
						for (var i = 0; i < matchGridStore.getCount(); i++) {// 循环此Store遍历所有数据
							map = new Object();
							var record = matchGridStore.getAt(i);
							map.midcodedesc = record.get('midcodedesc');
							map.codedesc = record.get('codedesc');
							for(var k = 0; k < fieldMatchArr.length; k++){
								var tempcodeobj = fieldMatchArr[k];
								var tempcodeitemid = tempcodeobj.codeitemid;
								var tempcodedesc = record.get('codedesc');
								if(tempcodeitemid == tempcodedesc){
									map.codedesc = tempcodeobj.codeitemdesc;
									continue;
								}
							}
							map.codesetid = record.get('codesetid');
							temparr.push(map);
						}
						fieldStoreMap[oldItemid] = temparr;// 将此Store的数据添加进全局对象中
						//	加载数据
						matchGridStore.removeAll();//将store里边的内容清空
						var thisStore = fieldStoreMap[itemid];//获取当前选中的Store
						matchGridStore.loadData(thisStore);
					}
					// =========加载comboboxStore=======
					fieldcomboStore.removeAll();//将store里边的内容清空
					fieldcomboStore.proxy.extraParams.itemid = itemid;
					fieldcomboStore.reload();
				},
				change : function(thisCombo, newValue ,oldValue,eOpts ){
					if(oldValue == null){
						return;
					}
					var matcheWin = Ext.getCmp("matchwin");
					var matchGridStore = matcheWin.query("#matchgird")[0].getStore();//grid的Store
					matchGridStore.proxy.extraParams.beforeitemid = oldValue;
				}
		    }
		});
		// 新增对应按钮
		var addMatchButton = Ext.create("Ext.Button", {
			buttonAlign : 'center',
			text : setmatch_add,// 新增
			height : 25,
			width:50,
			margin : "0 0 0 5",
			handler : function() {
				var comboValue = fielditemComboBox.getValue();
				if(!comboValue){//没有选择不允许新增 bug【47604】cqy
					return;
				}
				var matchGrid = Ext.getCmp('matchwin').query("#matchgird")[0];
				var matchGridStore = matchGrid.getStore();
				var newRecord = {midcodedesc:"",codedesc:"",codesetid:""}; 
				matchGridStore.add(newRecord); 
			}
		});
		// 删除对应按钮
		var deleteMatchButton = Ext.create("Ext.Button", {
			buttonAlign : 'center',
			text : setmatch_delete,// 删除
			height : 25,
			width:50,
			margin : "0 0 0 5",
			handler : function() {
				var grid = Ext.getCmp('matchwin').query("#matchgird")[0];
				var records = grid.getSelectionModel().getSelection();
				if(records.length==0){
					Ext.Msg.alert(setmatch_tips,setmatch_delete_none);//请选择需要删除的对应关系
					return;
				}
				Ext.Msg.confirm(setmatch_tips,setmatch_delete_confirm, function(id) {// 确认删除？
					if ("no" == id) {// 点否返回
						return;
					}
					for (var i = 0, len = records.length; i < len; i++) {
						grid.getStore().remove(records[i]);
					}
				});

			}
		});
		// 自动对应按钮
		var autoMatchButton = Ext.create("Ext.Button", {
			buttonAlign : 'center',
			text : setmatch_automatch,// 自动对应
			height : 25,
			width:80,
			margin : "0 0 0 5",
			handler : function() {
				var matchWin = Ext.getCmp("matchwin");
				var thisFieldMatchArr = matchWin.thisFieldMatchArr;//指标项对应的数组
				var matchGridStore = matchWin.query("#matchgird")[0].getStore();//grid的Store
				var map = null;
				var temparr = [];
				for (var i = 0; i < matchGridStore.getCount(); i++) {// 循环此Store遍历所有数据
					map = new Object();
					var record = matchGridStore.getAt(i);
					if(record.get('midcodedesc')==""||record.get('midcodedesc')==null){
						continue;
					}
					map.midcodedesc = record.get('midcodedesc');
					map.codedesc = record.get('codedesc');
					map.codesetid = record.get('codesetid');
					for(var k = 0 ; k < thisFieldMatchArr.length ; k++){
						var tempArr = thisFieldMatchArr[k];
						var tempCodeItemDesc = tempArr.codeitemdesc;
						if((''+tempCodeItemDesc)==(record.get('midcodedesc')+'')){
							map.codedesc = tempArr.codeitemdesc;
							map.codesetid = tempArr.codeitemid;
							break;
						}else if((''+tempCodeItemDesc).indexOf(record.get('midcodedesc')+'') != -1 ||(record.get('midcodedesc')+'').indexOf(tempCodeItemDesc+'') != -1){
							map.codedesc = tempArr.codeitemdesc;
							map.codesetid = tempArr.codeitemid;
						}
					}
					temparr.push(map);
				}
				//排序，将未对应的指标项置顶
				for (var k = 0; k < temparr.length; k++) {
					var tempmap = temparr[k];
					if (tempmap.codesetid == "" || tempmap.codesetid == null) {
						var cloneMatchArr = JSON.parse(JSON.stringify(tempmap));
						temparr.splice(k, 1);// 删除未对应的数组
						temparr.unshift(cloneMatchArr);// 把未对应的数组添加到最前
					}
				}
				matchGridStore.removeAll();
				matchGridStore.loadData(temparr);
			}
		});
		functionContainer.add(fielditemComboBox);
		functionContainer.add(addMatchButton);
		functionContainer.add(deleteMatchButton);
		functionContainer.add(autoMatchButton);
		//store需要的参数子集指标setid
		var extraParams = {
			setid:"",
			itemid:"",
			schemeid:"",
			beforeitemid:"",
			unitcode:""
		};
		//指标对应grid面板 的Store
		var matchGridStore = Ext.create('Ext.data.Store', {
			fields : ['midcodedesc', 'codedesc','codesetid'],
			id:"matchgridstore",
			// 数据代理服务
			proxy : {
				type : 'transaction',
				functionId : 'SYS0000003024',
				extraParams:extraParams,
				reader : {
					type : 'json',
					root : 'middesclist'
				}
			},
			sortInfo : {
				field : 'codesetid',
				direction : 'ASC' //升序排列
			},
			remoteSort : true, //开启远程排序
			// 自动加载
			autoLoad : true,
				// 每页显示多少条
			listeners : {
				load : function(store, records, eOpts) {
					var itemid = store.proxy.extraParams.itemid;
					if(itemid == ""){
						return;
					}
					var matcheWin = Ext.getCmp("matchwin");
					var fieldStoreMap = matcheWin.fieldStoreMap;// 获取全局的store的arr
					if (!fieldStoreMap.hasOwnProperty(itemid)) {// 如果没有加载过此Store
						var map = null;
						var temparr = [];
						for (var i = 0; i < store.getCount(); i++) {// 循环此Store遍历所有数据
							map = new Object();
							var record = store.getAt(i);
							map.midcodedesc = record.get('midcodedesc');
							map.codedesc = record.get('codedesc');
							map.codesetid = record.get('codesetid');
							temparr.push(map);
						}
						fieldStoreMap[itemid] = temparr;// 将此Store的数据添加进全局对象中
					}
				}
			}
		});
		//指标项内容的combobox的store的额外参数
		var codeextraParams = {
			itemid:""
		};
		//指标项内容的combobox的store
		var svcMedTypeStore = Ext.create('Ext.data.Store', {
				fields : ['codeitemdesc', 'codeitemid'],
				// 数据代理服务
				proxy : {
					type : 'transaction',
					functionId : 'SYS0000003025',
					extraParams:codeextraParams,
					reader : {
						type : 'json',
						root : 'list'
					}
				},
				// 自动加载
				autoLoad : true
			});
		//指标项内容的combobox
		var svcMedTypeCombo = new Ext.form.ComboBox({
				id:"fieldcombo",
				store : svcMedTypeStore,
				valueField : 'codeitemid',
				displayField : 'codeitemdesc',
				mode : 'local',
//				forceSelection : true,
				editable : true,
				triggerAction : 'all',//默认显示全部
				typeAhead:true,//模糊匹配
				editRowIndex:"",
				listeners : {
					select : function(editor, e, eOpts) {
						// 选择时把数据同步到上级代码项——itemid一列
						var rowIdx = editor.editRowIndex;
						var codeitemid = e.get("codeitemid");
						var gridStore = Ext.getCmp('matchwin').query("#matchgird")[0]
								.getStore();
						var rowIdxStore = gridStore.getAt(rowIdx);
						rowIdxStore.set("codesetid", codeitemid);
						editor.getStore().clearFilter();
					},
					beforequery : function(e) {//任意字符模糊匹配
						var combo = e.combo;
						combo.getStore().clearFilter();
						if (!e.forceAll) {
							var value = e.query;
							combo.store.filterBy(function(record, id) {
								var text = record.get(combo.displayField);
								return (text.indexOf(value) != -1);
							});
							combo.expand();
							return false;
						}
					},
					blur:function(c,t,e){
						c.getStore().clearFilter();
					}
				}
			});
		// 指标对应grid面板
		var matchGridPanel = Ext.create('Ext.grid.Panel', {
					flex:9,
					store : matchGridStore,
					itemId:"matchgird",
					margin:'-3 0 10 13',
					stripeRows:true,//表格是否隔行换色
					enableColumnResize:false,//禁止改变列宽
            		enableColumnMove:false,//禁止拖放列
            		columnLines:true,
            		viewConfig: {　　 
						markDirty: false //不显示编辑后的三角
					},
            		plugins:[  
	                 	Ext.create('Ext.grid.plugin.CellEditing',{  
				                     clicksToEdit:1 //设置单击单元格编辑  
				                 })  
				    ],
            		selModel: Ext.create("Ext.selection.CheckboxModel", {
					    mode: "multi",//multi,simple,single；默认为多选multi
					    checkOnly: true,//如果值为true，则只用点击checkbox列才能选中此条记录
					    enableKeyNav: true
					}),
					columns : [
						{ text : setmatch_midcodeindex, dataIndex : 'midcodedesc', sortable:false , editor:{ xtype:"textfield" , allowBlank: false } , flex : 2 , hideable: false },// 中间库代码名称
						{
							header : setmatch_upcodeindex, // 上级代码名称
							dataIndex : 'codedesc',
							editor : svcMedTypeCombo,
							flex:2,
							hideable: false ,
							sortable:false,
							renderer : function(value, cellmeta, record) {
								var index = svcMedTypeStore.find(
										svcMedTypeCombo.valueField, value
								);
								var ehrRecord = svcMedTypeStore.getAt(index);
								var returnvalue = "";
								if (ehrRecord) {
									returnvalue = ehrRecord.get('codeitemdesc');
								}
								if(record.data.codedesc!="" && record.data.codedesc!=record.data.codesetid){//如果有显示值且不为数字的话返回此值
									returnvalue = record.data.codedesc;
								}
								return returnvalue;
							}
						},
						{ text : setmatch_upcodeset, dataIndex : 'codesetid', sortable:false, flex : 2, hideable: false }// 上级代码项
						],
					listeners : {
						beforeedit : function(editor,e,eOpts) {
							//编辑后把行号保存在combobox中
							var rowIdx = e.rowIdx;
							var combo = Ext.getCmp("fieldcombo");
							combo.editRowIndex = rowIdx;
						}
					},
					height : 395,
					width : 504,
					renderTo : Ext.getBody()
				});
		codeMatchPanel.add(functionContainer);
		codeMatchPanel.add(matchGridPanel);
		//=================底部按钮Container容器=============
		var buttonPanel = Ext.create("Ext.Container",{
			flex:0.4,
			layout:"hbox"
		});
		//取消按钮
		var quitButton = Ext.create("Ext.Button", {
				buttonAlign : 'center',
				text : setmatch_quit,// 取消
				height : 25,
				width:60,
				margin : "0 0 0 190",
				handler : function() {
					win.close();
				}
			});
		//下一步按钮
		var nextButton = Ext.create("Ext.Button", {
			buttonAlign : 'center',
			text : setmatch_next,// 下一步
			height : 25,
			width:60,
			margin : "0 0 0 30",
			handler : function() {
				stepview.nextStep();
			}
		});
		//上一步按钮
		var lastButton = Ext.create("Ext.Button", {
			buttonAlign : 'center',
			text : setmatch_last,// 上一步
			height : 25,
			width:60,
			hidden:true,
			margin : "0 0 0 190",
			handler : function() {
				stepview.previousStep();
			}
		});
		//确定按钮
		var sureButton = Ext.create("Ext.Button", {
			buttonAlign : 'center',
			text : setmatch_sure,// 确定
			height : 25,
			width:60,
			hidden:true,
			margin : "0 0 0 30",
			handler : function() {
				var matchWin = Ext.getCmp("matchwin");
				var matchGridStore = matchWin.query("#matchgird")[0].getStore();//grid的Store
				var fieldStoreMap = matchWin.fieldStoreMap;//获取全局的store的arr
				var itemid = matchGridStore.proxy.extraParams.itemid;
				// 把当前界面未保存的数据存到后台
				var temparr = [];
				for (var i = 0; i < matchGridStore.getCount(); i++) {// 循环此Store遍历所有数据
					map = new Object();
					var record = matchGridStore.getAt(i);
					map.midcodedesc = record.get('midcodedesc');
					map.codedesc = record.get('codedesc');
					map.codesetid = record.get('codesetid');
					temparr.push(map);
				}
				fieldStoreMap[itemid] = temparr;// 将此Store的数据添加进全局对象中
				
				// 校验是否有未对应的指标
				var needMatch = false; 
				for(tempItemid in fieldStoreMap){
					var tempMatchArr = fieldStoreMap[tempItemid];
					for(var k = 0;k < tempMatchArr.length; k++){
						var tempMatch = tempMatchArr[k];
						var cloneMatchArr = JSON.parse(JSON.stringify(tempMatch));
						if(tempMatch.codesetid == "" || tempMatch.codesetid == null){
							tempMatchArr.splice(k,1);//删除未对应的数组
							tempMatchArr.unshift(cloneMatchArr);//把未对应的数组添加到最前
							needMatch = true;
						}
					}
				}
				matchGridStore.loadData(temparr);//重新加载当前界面数据
				if(needMatch){
					Ext.Msg.alert("提示","请完成所有指标项的对应");// 请完成所有指标项的对应
					return;
				}
				
				//封装所有指标代码
				var matchField = [];
				for(index in fieldStoreMap){
					matchField.push(index);
				}
				// 获取选中的单位编码
				var unitcodeid = setMatch_me.unitcodeid;
				// 封装数据
				var map = new HashMap();
				map.put("unitcode",unitcodeid);
				map.put("matchmap",fieldStoreMap);
				map.put("matchfield",matchField);
				Rpc({
					functionId : 'SYS0000003026',
					async : false,
					success : function(form, action) {
						var result = Ext.decode(form.responseText);
						if (result.succeed) {
							Ext.Msg.alert("提示", "保存成功");// 保存成功
							var matchWin = Ext.getCmp("matchwin");
							matchWin.close();
						} else {
							Ext.Msg.alert("错误", "保存失败");// 保存失败
						}
					}
				}, map);				
				
			}
		});
		buttonPanel.add(quitButton);//取消
		buttonPanel.add(nextButton);//下一步
		buttonPanel.add(lastButton);//上一步
		buttonPanel.add(sureButton);//确定
		
//		win.add(navigationBar);//步骤导航栏
		win.add(stepview);//步骤导航栏——新
		win.add(oprationPanel);//第一步-子集代码过滤条件panel
		win.add(codeMatchPanel);//第二步-代码对应panel
		win.add(buttonPanel);//第二步-代码对应panel
		win.show();
	},
	/**
	 * 保存过滤条件
	 * @param {} c_expr
	 */
		saveFieldSetFormula : function (c_expr){
			var gridpanel = Ext.getCmp("matchwin").query("#fieldsetGridPanel")[0];
			var map = new HashMap();
			map.put("type","save");
			map.put("c_expr",c_expr);
			map.put("setid",gridpanel.currtsetid);
			map.put("desc",gridpanel.currtdesc);
			map.put("unitcodeid",gridpanel.unitcodeid);
			Rpc({
				functionId : 'SYS0000003022',
				async : false,
				success : function(form, action) {
					var result = Ext.decode(form.responseText);
					if (result.succeed) {
					} else {
						Ext.Msg.alert(setmatch_error, setmatch_save_fail);// 保存失败
					}
				}
			}, map);
	}
});
