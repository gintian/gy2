/**
 * zhaoxg,lis
 * 2016-2-3
 * 税率表
 */
Ext.define('EHR.defineformula.TaxDetail',{
	   requires:['EHR.extWidget.proxy.TransactionProxy'],
       constructor:function(config){
			taxDetail_me = this;
			taxDetail_me.id = config.id;
			taxDetail_me.taxid = config.taxid;
			taxDetail_me.itemid = config.itemid;
			taxDetail_me.init();
	    },
	    
	    init:function(){
	    	taxDetail_me.taxid = taxDetail_me.taxid==null||taxDetail_me.taxid==""?"00":taxDetail_me.taxid;
			var records = '';
			var sm = Ext.create('Ext.selection.CheckboxModel',{
			injectCheckbox:0,//checkbox位于哪一列，默认值为0
			mode:'multi',//multi,simple,single；默认为多选multi
			checkOnly:true,//如果值为true，则只用点击checkbox列才能选中此条记录
			allowDeselect:true,//如果值true，并且mode值为单选（single）时，可以通过点击checkbox取消对其的选择
			enableKeyNav:false,
			stopSelection: true,
			selType : 'rowmodel',  //选行模式
			listeners:{
				selectionchange: function(model,selected){//选择有改变时产生的事件
					records=model.getSelection();
				}
			}});
			var store = Ext.create('Ext.data.Store', {
				fields:['ynse_down','ynse_up','sl','flag','sskcs','kc_base','description','taxitem'],
				proxy:{
				    	type: 'transaction',
				    	functionId:'ZJ100000083',
				        extraParams:{
			        		taxid:taxDetail_me.taxid,
			        		salaryid:taxDetail_me.id,
			        		opt:"1"
				        },
				        reader: {
				            type: 'json',
				            root: 'data'         	
				        }
				},
				autoLoad: true
			});
			var TaxTypeStore = Ext.create('Ext.data.Store', {
				fields:['id', 'name'],
				proxy:{
				    	type: 'transaction',
				    	functionId:'ZJ100000083',
				        extraParams:{
			        		taxid:taxDetail_me.taxid,
			        		salaryid:taxDetail_me.id,
			        		opt:"2"
				        },
				        reader: {
				            type: 'json',
				            root: 'data'         	
				        }
				},
				autoLoad: true
			});
			var IncomeStore = Ext.create('Ext.data.Store', {
				fields:['id', 'name'],
				proxy:{
				    	type: 'transaction',
				    	functionId:'ZJ100000083',
				        extraParams:{
			        		taxid:taxDetail_me.taxid,
			        		salaryid:taxDetail_me.id,
			        		opt:"3"
				        },
				        reader: {
				            type: 'json',
				            root: 'data'         	
				        }
				},
				autoLoad: true
			});
			taxDetail_me.TaxrateStore = Ext.create('Ext.data.Store', {
				fields:['id', 'name'],
				proxy:{
				    	type: 'transaction',
				    	functionId:'ZJ100000083',
				        extraParams:{
			        		taxid:taxDetail_me.taxid,
			        		salaryid:taxDetail_me.id,
			        		opt:"4"
				        },
				        reader: {
				            type: 'json',
				            root: 'data'         	
				        }
				},
				autoLoad: true
			});
			var optionStore = Ext.create('Ext.data.Store', {
			    fields: ['flag', 'name'],
			    data : [
			        {"flag":"0", "name":"上限闭合"},
			        {"flag":"1", "name":"下限闭合"}
			    ]
			});
			var box = Ext.create('Ext.form.ComboBox', {//执行列所用的下拉框
			    store: optionStore,
			    queryMode: 'local',
			    repeatTriggerClick : true,
			    displayField: 'name',
			    valueField: 'flag',
			    editable:false,
				listeners:{
	   				select:function(combo,ecords){
						var runflag = combo.getValue();
						
					}
				}
			});
			
			var taxRateTable = Ext.create('Ext.panel.Panel', {
					id:'taxDetailId',		
					border : false,
					items:[{
				 	   	xtype:'fieldset',
				        title:common.label.taxRateTable,//税率表
				        layout:'column',
				        width:500,
				        height:420,	
				   		items:[{
			         		xtype:'panel',
			         		border:false,
			         		width:490,
				       		height:400,	
							items:[{
								xtype:'panel',
				          		border:false,
				          		layout:'column',
				          		items:[
					          		{
										xtype:'panel',
						          		border:false,
						          		columnWidth:0.59,
						          		items:[
						          			{
										    	xtype:"combobox",
										    	id:'taxdetail',
							                    width:170,
											    store: taxDetail_me.TaxrateStore,
											    fieldLabel:"税率表",
											    labelWidth:50,
											    queryMode: 'local',
											    repeatTriggerClick : true,
											    displayField: 'name',
											    valueField: 'id',
												listeners:{
									   				select:function(combo,ecords){
						          						taxDetail_me.taxid = combo.getValue();
														var map = new HashMap();
														map.put("taxid",taxDetail_me.taxid);
														map.put("salaryid",taxDetail_me.id);
														map.put("itemid",taxDetail_me.itemid);
														map.put("opt","4");//1：新增税率表 2：修改税率表 3：修改基数 4：修改税率表 5：修改计税方式
													    Rpc({functionId:'ZJ100000085',success:function(form,action){
													    	var result = Ext.decode(form.responseText);
															var succeed=result.succeed;
															if(succeed){
																taxDetail_me.initTaxtable(taxDetail_me.taxid,taxDetail_me.itemid);
																store.load({params:{
																	taxid1:taxDetail_me.taxid
																}});
													        }else{
													        	Ext.MessageBox.alert('提示信息',result.message);
													        }
														}},map);
													}
												}
							                }
						          		]
						          	},{
										xtype:'panel',
						          		border:false,
						          		columnWidth:0.3,
						          		items:[
								          	{
										    	xtype:"numberfield",
										    	id:'k_base',
										    	fieldLabel:'基数',
										    	labelWidth:40,
							                    maxValue: 99999, // 最大值
							                    minValue: 0, // 最小值   
							                    width:110,
							                    name:'k_base'
							                }
						          		]
						          	},{
										xtype:'panel',
						          		border:false,
						          		layout:{type:'hbox',align:'right'},
						          		items:[
						          			{
						          			    xtype      : 'fieldcontainer',
									            defaultType: 'radiofield',
									            layout: 'hbox',
									            items: [
									                {
									                    boxLabel  : '正算',
									                    name      : 'mode',
									                    inputValue: '0',
									                    width     :  50,
									                    id        : 'radio0'
									                },{
									                    boxLabel  : '反算',
									                    name      : 'mode',
									                    inputValue: '1',
									                    id        : 'radio1',
											            listeners:{
											            	'change':function(th,newvalue){
											            		var mode = "0";
											   					if(newvalue){
											   						mode = "1";
											   					}else{
											   						mode = "0";
											   					}
											   					var income = Ext.getCmp("income").getValue();
											   					if(!income){
											   						return;
											   					}
											            		var map = new HashMap();
																map.put("mode",mode);
																map.put("income",income);
																map.put("salaryid",taxDetail_me.id);
																map.put("itemid",taxDetail_me.itemid);
																map.put("opt","5");//1：新增税率表 2：修改税率表 3：修改基数 4：修改税率表 5：修改所得额，正算or反算 6：计税方式 
															    Rpc({functionId:'ZJ100000085',success:function(form,action){
															    	var result = Ext.decode(form.responseText);
																	var succeed=result.succeed;
																	if(succeed){
																		
															        }else{
															        	Ext.MessageBox.alert('提示信息',result.message);
															        }
																}},map);
											            	}
											            }
									                }
									            ]
						          			}
						          		]
						          	}
				          		]
							},{
								xtype:'panel',
				          		border:false,
				          		layout:'column',
				          		items:[
				          			{
										xtype:'panel',
						          		border:false,
						          		columnWidth:0.48,
						          		items:[
						          			{
										    	xtype:"combobox",
										    	id:'taxtype',
							                    width:170,
											    store: TaxTypeStore,
											    fieldLabel:"计税方式",
											    labelWidth:50,
											    queryMode: 'local',
											    repeatTriggerClick : true,
											    displayField: 'name',
											    valueField: 'id',
												listeners:{
									   				select:function(combo,ecords){
														var param = combo.getValue();
														var map = new HashMap();
														map.put("param",param);
														map.put("taxid",taxDetail_me.taxid);
														map.put("opt","6");//1：新增税率表 2：修改税率表 3：修改基数 4：修改税率表 5：修改所得额 6：计税方式
													    Rpc({functionId:'ZJ100000085',success:function(form,action){
													    	var result = Ext.decode(form.responseText);
															var succeed=result.succeed;
															if(succeed){
																
													        }else{
													        	Ext.MessageBox.alert('提示信息',result.message);
													        }
														}},map);
													}
												}
							                }
						          		]
						          	},{
										xtype:'panel',
						          		border:false,
						          		columnWidth:0.4,
						          		items:[
						          			{
							                	xtype:"combobox",
							                	id:'income',
							                    width:170,
											    store: IncomeStore,
											    fieldLabel:"收入额",
											    labelWidth:40,
											    queryMode: 'local',
											    repeatTriggerClick : true,
											    displayField: 'name',
											    valueField: 'id',
												listeners:{
									   				select:function(combo,ecords){
									   					var mode = "0";
									   					if(Ext.getCmp("radio0").getValue()){
									   						mode = "0";
									   					}else if(Ext.getCmp("radio1").getValue()){
									   						mode = "1";
									   					}
														var income = combo.getValue();
														var map = new HashMap();
														map.put("income",income);
														map.put("salaryid",taxDetail_me.id);
														map.put("itemid",taxDetail_me.itemid);
														map.put("mode",mode);
														map.put("opt","5");//1：新增税率表 2：修改税率表 3：修改基数 4：修改税率表 5：修改所得额
													    Rpc({functionId:'ZJ100000085',success:function(form,action){
													    	var result = Ext.decode(form.responseText);
															var succeed=result.succeed;
															if(succeed){
																
													        }else{
													        	Ext.MessageBox.alert('提示信息',result.message);
													        }
														}},map);
													}
												}
							                }
						          		]
						          	}
				          		]
							},{
						    	xtype:"grid",
						    	id:'taxtable',
								store: store,
				         		width:475,
					       		height:345,	
						    	selModel:sm,
						    	columnLines:true,
								rowLines:true,
								sortableColumns:false,//不让进行排序了，没意义[36784]
								columns: [
							        { header: '应纳税所得额下限', dataIndex: 'ynse_down',editor:{xtype:'numberfield'},align : 'right' },
							        { header: '应纳税所得额上限', dataIndex: 'ynse_up',editor:{xtype:'numberfield'},align : 'right' },
							        { header: '税率', dataIndex: 'sl',editor:{xtype:'numberfield'},align : 'right' },
							        { header: '封闭标志', dataIndex: 'flag',editor:box, align : 'right',renderer:function(v) {
										if(v=="0"){
											return "上限闭合";
										}else if(v=="1"){
											return "下限闭合";
										}else{
											return "上限闭合";
										}
									} },
							        { header: '速算扣除数', dataIndex: 'sskcs',editor:{xtype:'numberfield'},align : 'right' },
							        { header: '扣除基数',     dataIndex: 'kc_base',editor:{xtype:'numberfield'},align : 'right' },
							        { header: '说明', dataIndex: 'description',editor:{xtype:'bigtextfield'},align : 'right' }
								],
								dockedItems:[
									{
										xtype:'toolbar',
										dock:'bottom',
										border : false,
										items:[{xtype:'tbfill'},
							          		{
							          			text:'增加',
							          			handler:function(){
							          				var map = new HashMap();
													map.put("salaryid",taxDetail_me.id);
													map.put("taxid",taxDetail_me.taxid);
													map.put("opt","1");//1：新增税率表 2：修改税率表 
												    Rpc({functionId:'ZJ100000085',success:function(form,action){
												    	var result = Ext.decode(form.responseText);
														var succeed=result.succeed;
														if(succeed){
															var obj = new Object();
															obj.taxitem=result.taxitem;
															store.insert(store.count(),obj);
															var sel = Ext.getCmp("taxtable").getSelectionModel();
															sel.select(store.count()-1, true);//定位最后一行	
												        }
													}},map);
							          			}
							          		},          		
							          		{
							          			text:'删除',
							          			handler:function(){
								          			if(records.length==0)
								          	  		{
								          				Ext.MessageBox.show({  
								          					title : common.button.promptmessage,  
								          					msg : "请选择要删除记录！", //"请选择要删除的薪资类别！"
								          					buttons: Ext.Msg.OK,
								          					icon: Ext.MessageBox.INFO  
								          				});  
								          	  		    return;
								          	  		}
								          			Ext.MessageBox.confirm(
								          					common.button.promptmessage,
								          					"确认删除当前选择记录？",//您真的希望删除选中的薪资类别
								          					function(but){
								          						if(but == 'yes'){
								          							var selectID = '';
											          				for(var i=0;i<records.length;i++){
																		selectID+=","+records[i].get('taxitem');
																	}
																	var map = new HashMap();
																	map.put("salaryid",taxDetail_me.id);
																	map.put("taxid",taxDetail_me.taxid);
																	map.put("taxitem",selectID);
																    Rpc({functionId:'ZJ100000086',success:function(form,action){
																    	var result = Ext.decode(form.responseText);
																		var succeed=result.succeed;
																		if(succeed){
																			store.load({params:{
																					taxid1:taxDetail_me.taxid
																				}});
																        }else{
																        	Ext.MessageBox.alert('提示信息',result.message);
																        }
																	}},map);
								          						}
								          					},
								          					taxDetail_me
							          				);
							          			}
							          		},
							          		{
							          			text:'计算条件',
							          			handler:function(){
													thisScope.createComputeCond();
							          			}
							          		},
							          		{xtype:'tbfill'}]
									}
								],
								plugins: [
							        Ext.create('Ext.grid.plugin.CellEditing', {
							            clicksToEdit: 2
							        })
							    ]
			                }
							]}
			           ]
			   		}]
			   	});
				taxDetail_me.TaxrateStore.on('load',function(){
			   		taxDetail_me.initTaxtable(taxDetail_me.taxid,taxDetail_me.itemid);
			   	});
			   	Ext.getCmp("taxtable").on('edit',function(editor,e){
					var map = new HashMap();
					map.put("salaryid",taxDetail_me.id);
					map.put("taxid",taxDetail_me.taxid);
					map.put("taxitem",e.record.get("taxitem")+"");
					map.put("value",e.value+"");
					map.put("field",e.field);
					map.put("opt","2");//1：新增税率表 2：修改税率表  3：修改基数
				    Rpc({functionId:'ZJ100000085',success:function(form,action){
				    	var result = Ext.decode(form.responseText);
						var succeed=result.succeed;
						if(succeed){
							e.record.commit();
				        }else{
				        	Ext.MessageBox.alert('提示信息',result.message);
				        }
					}},map);
			   	});
			   	Ext.getCmp("k_base").on('blur',function(a,e){
			   		var map = new HashMap();
					map.put("taxid",taxDetail_me.taxid);
					map.put("value",a.getValue()+"");
					map.put("field","k_base");
					map.put("opt","3");//1：新增税率表 2：修改税率表 3：修改基数
				    Rpc({functionId:'ZJ100000085',success:function(form,action){
				    	var result = Ext.decode(form.responseText);
						var succeed=result.succeed;
						if(succeed){
						
				        }else{
				        	Ext.MessageBox.alert('提示信息',result.message);
				        }
					}},map);
			   	});
			   	var centerPanel = Ext.getCmp('centerPanel');
				centerPanel.add(taxRateTable);
	    },
	    
	    initTaxtable:function(taxid,itemid){
	    	var flag = false;
	    	//如果税率表被删除了，不显示了
	    	var item = taxDetail_me.TaxrateStore.data.items;
	    	for(var i = 0; i < item.length; i++) {
	    		if(taxid == item[i].id) {
	    			flag = true;
	    			break;
	    		}
	    	}
			if(!flag || taxid=='00'){//没选择税率表的时候不初始化
				return;
			}
   			var map = new HashMap();
			map.put("salaryid",taxDetail_me.id);
			map.put("taxid",taxid);
			map.put("itemid",itemid);
		    Rpc({functionId:'ZJ100000084',success:function(form,action){
		    	var result = Ext.decode(form.responseText);
				var succeed=result.succeed;
				if(succeed){
					var taxdetail = Ext.getCmp("taxdetail");
					if(taxdetail)
						taxdetail.setValue(taxid);
					if(result.mode=="1"){
						var radio1 = Ext.getCmp("radio1");
						if(radio1)
							Ext.getCmp("radio1").setValue(true);
					}else{
						var radio0 = Ext.getCmp("radio0");
						if(radio0)
							Ext.getCmp("radio0").setValue(true);
					}
					var taxtype = Ext.getCmp("taxtype");
					var k_base = Ext.getCmp("k_base");
					var income = Ext.getCmp("income");
					if(taxtype)
						taxtype.setValue(result.param);
					if(k_base)
						k_base.setValue(result.k_base);
					if(income)
						income.setValue(result.income);
		        }
			}},map);
		}
       
});