/**
*	调用方法：		
		Ext.require('EHR.defineformula.DefineTempVar',function(){
			Ext.create("EHR.defineformula.DefineTempVar",{type：type,id:id,formulaType:formulaType});
		})
*	参数说明：type：入口标识  1：薪资  2：薪资总额  3：人事异动  4...其他
			id：主键标识   薪资则为薪资类别号；  人事异动为公式组号；  其他。。。根据各自模块自行设置   在交易类中区分即可
**/
Ext.define('EHR.defineformula.DefineTempVar',{
		requires:['EHR.extWidget.proxy.TransactionProxy'],
        constructor:function(config){
			tempVar = this;
			tempVar.id = config.id;//薪资类别id
			tempVar.module = config.module;//模块号,1是薪资
			tempVar.type = config.type;//同一模块下不同入口，1:是薪资类别
			tempVar.callBackfn = config.callBackfn;//回调函数
			tempVar.rowNum = 0;//当前选中的行数
			tempVar.nid = '';
			tempVar.nflag=config.nflag;//人事异动默认都是0，gaohy,2016-1-6
			tempVar.selectionStart = 0;//光标起选中始位置infor_type
			tempVar.selectionEnd = 0;//光标选中结束位置
			tempVar.selectionIndex = 0;//光标位置
			tempVar.tempVarNameIsNum = false;//临时变量是否都是数字
			tempVar.tempFields = new Array();　//创建一个数组infor_type
			tempVar.infor_type = config.infor_type;  //模板类型
			if(tempVar.module == "1"){//薪资模块
				if(tempVar.type == '1'||tempVar.type == '3'){//临时变量列
					tempVar.tempFields[0] = "nid";//临时变量id
					tempVar.tempFields[1] = "chz";//临时变量汉化名称
					tempVar.tempFields[2] = "ntype";//临时变量数据类型
					tempVar.tempFields[3] = "fldlen";//临时变量长度
					tempVar.tempFields[4] = "flddec";//临时变量小数点位数
					tempVar.tempFields[5] = "codesetid";//临时变量代码类id
					tempVar.tempFields[6] = "cstate";//临时变量是否是共享，如果不是则是当前薪资类别id，否则为空
					tempVar.tempFields[7] = "sorting";//临时变量排序号
				}
			}
			tempVar.initTempColumns();//对列对象的初始化
			tempVar.initTemp();
        },
        initTempColumns:function(){
        	//数据ntype类型的store
        	var states = Ext.create('Ext.data.Store', {
				fields: ['id', 'name'],
				data : [
				    {"id":"1", "name":common.label.countn},
				    {"id":"2", "name":common.label.charat},
				    {"id":"3", "name":common.label.date},
				    {"id":"4", "name":common.label.codeType}
				    ]
				});

			//代码类数据store，列中的编辑器
			var tempCodeStore = Ext.create('Ext.data.Store',
					{
						fields:['dataName','dataValue'],
						proxy:{
						    	type: 'transaction',
						    	functionId:'ZJ100000073',
						        extraParams:{
					        		module:tempVar.module,
					        		type:tempVar.type,//模块号
					        		flag:'5'
						        },
						        reader: {
						            type: 'json',
						            root: 'codelist'         	
						        }
						},
						autoLoad: true
					});
			
			//数据类型的下拉框对象
			var comBoxNType = Ext.create('Ext.form.ComboBox', {
					id:'ntype_id',
					store:states,
					value:common.label.countn,
					editable:false,
					//hideTrigger:true,
					displayField:'name',
					valueField:'id',
					queryMode:'local',
					listeners:{
						'select':function(combox,records){//选择数据类型时清空代码类
							var record = tempVar.tempVarPanel.getStore().getAt(tempVar.rowNum);
							if(combox.getValue() != 1){//数值型
								record.set('flddec',"0");
							}else{
								record.set('flddec',"2");
							}
							if(combox.getValue() == 2){//字符型
								record.set('fldlen',"30");
							}else{
								record.set('fldlen',"10");
							}
							record.set('codesetid',"");
							record.commit();
						}
					}
			});
			
			//代码类下拉框对象，列中的编辑器
			tempVar.comBoxCode = Ext.create('Ext.form.ComboBox', {
					store:tempCodeStore,
					id:'codesetid',
					editable:false,
					//hideTrigger:true,
					displayField:'dataName',
					valueField:'dataValue',
					queryMode:'local',
					matchFieldWidth:false,
					width:250,
					listeners:{
						'focus':function(box){//代码类获得焦点时显示第一个数据
							var record = tempVar.tempVarPanel.getStore().getAt(tempVar.rowNum);
							if(tempCodeStore.getAt(0) != null && (record.data.codesetid == null || record.data.codesetid == ''))
								box.select(tempCodeStore.getAt(0).data.dataValue);
						}
					}
			});

			//编辑状态：数字类型输入框
			var lenField =  Ext.widget('numberfield',{
				hideTrigger:true,
				allowDecimals:false,
				maxLength:4,
				maxValue:9999,
				minValue:0
			});
			//编辑状态：数字类型输入框
			var lenFieldshort =  Ext.widget('numberfield',{
				hideTrigger:true,
				allowDecimals:false,
				maxLength:2,
				maxValue:38,
				minValue:0,
				//根据类型动态更改验证规则
				listeners:{
					focus:function(){
						var record = tempVar.tempVarPanel.getStore().getAt(tempVar.rowNum);
						switch(record.data.ntype){
							case '2': this.maxLength=3;     //字符最大200，3位。
							          this.setMaxValue(200);
							break;
							default: this.maxLength=2;    //数值、日期、代码最大38，2位。
							         this.setMaxValue(38);
							break;
						}
					}
				}
			});
			
			//编辑状态：数字类型输入框
			var decField =  Ext.widget({
				xtype: 'numberfield',
				hideTrigger:true,
				allowDecimals:false,
				maxLength:2,
				maxValue:99,
				minValue:0
			});
			
			//编辑状态：文本框
			var textField = Ext.widget({
				xtype: 'textfield',
		        name: 'name'
		       /* validator:function(value){
					if(/^\d+$/.test(value)){
						tempVar.tempVarNameIsNum = true;
						return common.error.notAllNum;
					}else{
						return true;
					}
				}*/
		        //allowBlank: false  // 表单项非空
			});
			
        	tempVar.tempColumns = new Array();//列对象
			
     		for(var i=0;i<tempVar.tempFields.length;i++){
				var obj = new Object();
				if(tempVar.tempFields[i]=='nid'){
					obj.header=common.label.select;
					obj.flex=20;
					obj.hidden=true;
					obj.dataIndex='nid';
					obj.menuDisabled=true;
					obj.renderer=function(value) {
	    				return '<input name="nid" type="checkbox" id=\"'+value+'\"/>';
					};
					tempVar.tempColumns[i]=obj;
				}else if(tempVar.tempFields[i]=='chz'){
					obj.text=common.label.name;
					obj.flex=150;
					obj.menuDisabled=true;
					obj.dataIndex='chz';
					obj.editor=textField;
					tempVar.tempColumns[i]=obj;
				}else if(tempVar.tempFields[i]=='ntype'){
					obj.text=common.label.ntype;
					obj.flex=80;
					obj.menuDisabled=true;
					obj.dataIndex='ntype';
					obj.renderer=function(v) {
	    				if(v=='1')return common.label.countn;
	    				else if(v=='2')return common.label.charat;
	    				else if(v=='3')return common.label.date;
	    				else return common.label.codeType;
					};
					obj.editor = comBoxNType;
					tempVar.tempColumns[i]=obj;
				}else if(tempVar.tempFields[i]=='fldlen'){
					obj.text=common.label.fldlen;
					obj.flex=50;
					obj.menuDisabled=true;
					obj.editor=lenFieldshort;
					obj.dataIndex='fldlen';
					tempVar.tempColumns[i]=obj;
				}else if(tempVar.tempFields[i]=='flddec'){
					obj.text=common.label.flddec;
					obj.flex=50;
					obj.menuDisabled=true;
					obj.dataIndex='flddec';
					obj.editor=decField;
					tempVar.tempColumns[i]=obj;
				}else if(tempVar.tempFields[i]=='codesetid'){
					obj.text=common.label.codesetid;
					obj.flex=60;
					obj.menuDisabled=true;
					obj.dataIndex='codesetid';
					obj.editor = tempVar.comBoxCode;
					
					tempVar.tempColumns[i]=obj;
				}else if(tempVar.tempFields[i]=='cstate'){
					obj.text=common.label.share;
					obj.flex=20;
					obj.menuDisabled=true;
					obj.dataIndex='cstate';
					obj.renderer=function(value, cell, record) {
						var htm = '';
						var nid = "";
						if(record.get('nid'))
						 	nid = record.get('nid');
						if(tempVar.type=="3"){//人事异动 lis 20160711
							if(value == "1")//共享
								htm = '<input id="share_' + nid + '" name="share" type="checkbox" onclick="tempVar.shareFlag(this,\''+nid+'\')" checked />'
							else{
								if(nid)
									htm = '<input id="share_' + nid + '" name="share" type="checkbox" onclick="tempVar.shareFlag(this,\''+nid+'\')" />'
								else
									htm = '<input id="share_' + nid + '" name="share" type="checkbox" disabled=true onclick="tempVar.shareFlag(this,\''+nid+'\')" />'
							}
						}else{
							if(record.get('cstate') == tempVar.id)
								htm = '<input id="share_' + nid + '" name="share" type="checkbox" onclick="tempVar.shareFlag(this,\''+nid+'\')" />'
							else{
								if(nid)
									htm = '<input id="share_' + nid + '" name="share" type="checkbox" onclick="tempVar.shareFlag(this,\''+nid+'\')" checked />'
								else
									htm = '<input id="share_' + nid + '" name="share" type="checkbox"  disabled=true onclick="tempVar.shareFlag(this,\''+nid+'\')" checked />'
							}
						}
	    				return htm;
					};
					
					tempVar.tempColumns[i]=obj;
				}
			}
        },
        
        //初始化临时变量
        initTemp:function()
		{
        	//临时变量的数据store
        	tempVar.store = Ext.create('Ext.data.Store', {
				fields:tempVar.tempFields,
				proxy:{
				    	type: 'transaction',
				        functionId:'ZJ100000073',
				        extraParams:{
			        		id:tempVar.id,
			        		module:tempVar.module,
			        		type:tempVar.type,
			        		flag:'1'
				        },
				        reader: {
				            type: 'json',
				            root: 'data'         	
				        }
				},
				autoLoad: true
			});

        	//临时变量数据加载时选中行数
        	tempVar.store.on('load',function(){
			    var sel = tempVar.tempVarPanel.getSelectionModel();
				sel.select(0, false);
			});
			
        	//编辑器是单元格编辑
        	tempVar.cellEditing = Ext.create('Ext.grid.plugin.CellEditing', {
	            clicksToEdit: 2//双击编辑 liuyz 原为单击编辑
	        });
        	
			//临时变量数据显示panel
        	tempVar.tempVarPanel = Ext.create('Ext.grid.Panel', {
					store: tempVar.store,
					border:false,
					width: 450,
			    	height: 400,
			    	columnLines:true,
					rowLines:true,
					bufferedRenderer:false,//一起把数据去拿过来，不用假分页模式
		   			selModel:{
		   				selType: 'checkboxmodel',
		            	allowDeselect: false,//如果值true，并且mode值为单选（single）时，可以通过点击checkbox取消对其的选择
		            	pruneRemoved: true,//从存储的选项中删除时删除记录
		    	        mode: "MULTI",     //"SINGLE"/"SIMPLE"/"MULTI"
		    	        checkOnly: false,     //只能通过checkbox选择
		    	        enableKeyNav: true//开启/关闭在网格内的键盘导航。
		   	    	},
					columns: tempVar.tempColumns,
					viewConfig:{
						plugins:{
							ptype:'gridviewdragdrop',
							dragText:common.label.DragDropData
						},
						listeners: {
			                beforedrop:tempVar.moveRecord
			            }
					},
					dockedItems:[
						{
							xtype:'toolbar',
							dock:'bottom',
							border : false,
							items:[{xtype:'tbfill'},{
									text:common.button.insert,//添加
									handler:function(){
										tempVar.addTemp(tempVar.store);
									}
								},{
									text:common.button.todelete,//删除
									handler:function(){
										tempVar.deleteTempVar();
									}
								},{
									text:common.label.relatedReference,//相关引用
									id:'relateReferenceButton',
									handler:function(){
										tempVar.relateReference();
									}
								},{
									text:"调整顺序",
									handler:function(){
										var records = []; 
										tempVar.store.each(function(r){ records.push(r.copy()); }); 
										if(Ext.isEmpty(records)){
											Ext.showAlert(common.msg.emptyalert);
											return;
										}else {
											var orderByStore = new Ext.data.Store({ recordType: tempVar.store.recordType }); 
											orderByStore.add(records);
											var colObj = new Object();
											colObj.text=common.label.name;
											colObj.menuDisabled=true;
											colObj.flex=100;
											colObj.dataIndex='chz';
											var panel = Ext.create('Ext.grid.Panel', {
												store: orderByStore,
												layout:'fit',
												border:true,
										    	columnLines:true,
												rowLines:true,
												bufferedRenderer:false,//一起把数据去拿过来，不用假分页模式
												selModel: {
												    selection: "rowmodel",
												    mode: "MULTI"
												},
												columns:[colObj]
											})
											var win=Ext.widget("window",{
										          title:"调整顺序",  
										          height:400,  
										          width:300,
										          layout:'fit',
										          bodyStyle: 'background:#ffffff;',
												  modal:true,
												  resizable:false,
												  bodyBorder:false,
												  closeAction:'destroy',
												  items:[panel],
										          bbar:[
										         		{xtype:'tbfill'},
										         		{
										         			text:"确定",
										         			handler:function(){
						                                        var nid = "";
						                                        orderByStore.each(function(item,index,count){//遍历每一条数据
						                                        	nid+=item.get('nid')+",";
																});
																var map = new HashMap();
																map.put("sorting",nid);
															    Rpc({functionId:'ZJ100000090',async:false,success:function(form,action){
															    	win.close();
															    	tempVar.store.reload();
															    }},map);
										         			}
										         		},{
										         			text:"上移",
										         			handler:function(){
																var records = panel.getSelectionModel().getSelection();
																for(var i=0;i<records.length;i++){
																	for(var j=i+1;j<records.length;j++){
																		if(orderByStore.indexOf(records[i])<orderByStore.indexOf(records[j])){
																			var temp = records[i];
																			records[i] = records[j];
																			records[j] = temp;
																		}
																	}
																}
																var num = orderByStore.getCount();
																for(var i in records){
																	var record = records[i];
						                                            var index = orderByStore.indexOf(record);
						                                            if(index<num){
						                                            	num = index;
						                                            }
																}
																for(var i in records){
						                                            var record = records[i];
						                                            var index = orderByStore.indexOf(record);
						                                            if(num>0){
						                                                orderByStore.removeAt(index);
						                                                orderByStore.insert(num-1, record);
						                                                panel.getView().refresh();
						                                                panel.getSelectionModel().select(num-1, true);
						                                            }else{
						                                                continue;
						                                            }
						                                        }
										         			}
										         		},{
										         			text:"下移",
										         			handler:function(){
																var records = panel.getSelectionModel().getSelection();
																for(var i=0;i<records.length;i++){
																	for(var j=0;j<i;j++){
																		if(orderByStore.indexOf(records[j+1])<orderByStore.indexOf(records[j])){
																			var temp = records[j];
																			records[j] = records[j+1];
																			records[j+1] = temp;
																		}
																	}
																}
																var num = 0;
																for(var i in records){
																	var record = records[i];
						                                            var index = orderByStore.indexOf(record);
						                                            if(index>num){
						                                            	num = index;
						                                            }
																}
																for(var i in records){
						                                            var record = records[i];
						                                            var index = orderByStore.indexOf(record); 
						                                            if(num<orderByStore.getCount()-1){
						                                                orderByStore.removeAt(index);
						                                                orderByStore.insert(num+1, record);
						                                                panel.getView().refresh();
						                                                panel.getSelectionModel().select(num+1, true);
						                                            }else{
						                                                continue;
						                                            }
						                                        }
										         			}
										         		},
										         		{
										         			text:common.button.close,//关闭
										         			handler:function(){
																win.close();
																//tempVar.store.load();
										         			}
										         		},
										         		{xtype:'tbfill'}
										          ]
										    });
										    win.show();
										    panel.getSelectionModel().selectRange(0,0); 
										}
									}
								},{xtype:'tbfill'}]
						}
					],
				    plugins: [tempVar.cellEditing],
				    listeners:{
				    	'beforeedit':function(editor, e,eOpts){//只有是数据类型是代码类时才可以编辑代码类列
			    			var nid = e.record.data.nid;
			    			var ntype = e.record.data.ntype;
			    			
					    	/*if(!!!nid){//判断临时变量名称是否全是数字
					    		Ext.showAlert(common.error.notAllNum+"！");
					    	}
				    		tempVar.tempVarNameIsNum = false;*/
				    		if(!!!e.record.get('chz'))//解决为空时显示提示
				    			e.record.set('chz',"");
			    			if((ntype != 1 && e.column.dataIndex == 'flddec') || (ntype != 4 && e.column.dataIndex == 'codesetid')||(!nid && e.column.dataIndex != 'chz')){
				    			e.cancel = true;
				    			return;
				    		}
				    	},
				    	'validateedit':function(editor, e, eOpts){
			    			if(e.column.dataIndex == 'chz'){//校验临时变量名称为数字 lis 20160719
			    				var value_ = e.value;
			    				if(value_.length>1)
			    					value_ = value_.substring(0,1);
				    			if(/^\d+$/.test(value_)){
						    		Ext.showAlert(common.error.notFirstNum);
						    		e.cancel = true;
									return;
								}
								//28957 linbz 名称过滤特殊字符
								if(/[   `~!@#$%^&*()+={}':;',.<>?~！@#￥%……&*（）——+{}【】‘；：”“’。，、？\|\[\]]/.test(e.value)){
                                    Ext.showAlert(common.error.notSpecialChar);
                                    e.cancel = true;
                                    return;
                                }
			    			}
			    			if(e.value==null){
			    				e.cancel = true;
			    				Ext.showAlert(common.msg.valueNotNull);
			    				return;
			    			}
				    		if(e.column.dataIndex == 'flddec'){//是数值型时可以编辑小数点
					    		var ntype = e.record.data.ntype;
			    				if(ntype == 1){
					    			var fldlen = e.record.data.fldlen;//长度
					    			var flddec = e.value;//小数点位数
					    			if(parseInt(flddec) > parseInt(fldlen)){
					    				e.cancel = true;
					    				Ext.showAlert(common.msg.flddecNoGreaterfldlen);
					    				return;
					    			}
			    				}
			    			}
				    	},
				    	'select':function(rowmode,record,rowIndex){//选中时显示当前临时变量的公式
				    		tempVar.rowNum = rowIndex;
				    		tempVar.nid = record.get("nid");
				    		tempVar.cellClicked(tempVar.nid);
				    		
				    		var tip = Ext.getCmp('content_tip');
				    		if(tempVar.nid){
				    			if(tip){
				    				tip.destroy();
				    			}
							}else{
								var rulearea = Ext.getCmp('formulaVal');
								if(rulearea){
									rulearea.setValue("");
									tempVar.expressionTemp.disable();
									Ext.getCmp('relateReferenceButton').disable();
									if(tip){
									}else{
										tip = Ext.create('Ext.tip.ToolTip', {
											id:"content_tip",
											shadow:false,
											trackMouse: true,
											bodyStyle:"background-color:white;border:1px solid #c5c5c5;",
											target: "expressionTemp",
											html: "请编辑左侧临时变量名称！"
										});
									}
								}
							}
				    	},
				    	'edit':function(editor, e,eOpts ){
				    		tempVar.saveData(e.record);
				    	}
				    }
				});

			//输入公式panel
			var tempFormula = Ext.create('Ext.panel.Panel', {
					border:false,
					width:330,
					
					minButtonWidth:55,
					buttonAlign:'right',
			        items:[{
			         	border:false,
						xtype:'textareafield',
						name:'formula',
						id:'formulaVal',
						fieldStyle:'height:220px;',
						width:328,
						height:195,
						enableKeyEvents:true,
						listeners:{
			        		afterrender:function(textarea){
			        			if(document.selection){//ie 下绑定mouseleave事件
			        				textarea.getEl().on("mouseleave",function(){
			        					//tempVar.getCursorPosition();//获得光标位置
			        				})
			        			}
			        		},
			        		change:function(){
			        			if(document.selection){
			        				//tempVar.getCursorPosition();//获得光标位置
			        			}
			        		},
			        		keyup:function(textarea,e){
			        			if(document.selection){
			        				tempVar.getCursorPosition();//获得光标位置     lis update2016-7-5
			        			}
			        		},
			        		 click: {
			                    element: 'el',
			                    fn: function(){ 
				        			if(document.selection){
				        				tempVar.getCursorPosition(); //获得光标位置
				        			}
			        			}
			                }
			        	}
			        }],
					buttons:[
						{xtype:'button',text:common.button.functionGuide,//函数向导
							handler:function(){
							tempVar.functionWizard();
						  }
						},
						{xtype:'button',text:common.button.formulaSave,//公式保存
						  handler:function(){
							tempVar.saveFormula();
						  }
						}
					]
			   	});
			
				//子集下拉框数据store
				var tempItemStore = Ext.create('Ext.data.Store',
				{
					fields:['name','id'],
					proxy:{
					    	type: 'transaction',
					    	functionId:'ZJ100000073',
					        extraParams:{
				        		id:tempVar.id,
				        		module:tempVar.module,
				        		type:tempVar.type,
				        		nflag:tempVar.nflag,//人事需要，gaohy,2016-1-7
				        		infor_type:tempVar.infor_type,
				        		flag:'3'
					        },
					        reader: {
					            type: 'json',
					            root: 'fieldsetlist'         	
					        }
					},
					autoLoad: true
				});
				
				//指标集合下拉框数据store
				var tempFieldStore = Ext.create('Ext.data.Store',
				{
					fields:['name','id'],
					proxy:{
					    	type: 'transaction',
					    	functionId:'ZJ100000073',
					        extraParams:{
				        		id:tempVar.id,
				        		module:tempVar.module,
				        		type:tempVar.type,
				        		flag:'4'
					        },
					        reader: {
					            type: 'json',
					            root: 'itemlist'         	
					        }
					}
				});
				
				//指标集合下拉框数据store
				var tempCodeStore = Ext.create('Ext.data.Store',
				{
					fields:['dataValue','dataName'],
					proxy:{
					    	type: 'transaction',
					    	functionId:'ZJ100000073',
					        extraParams:{
				        		module:tempVar.module,
				        		type:tempVar.type,
				        		flag:'6'
					        },
					        reader: {
					            type: 'json',
					            root: 'codeItemlist'         	
					        }
					}
				});
				
				var comboxWidth = 300;
				//下拉框panel
				var tempItemPanel = Ext.create('Ext.panel.Panel', {
					border:true,
					width:328,
					height:130,
					margin:'10 0 0 0',
					layout: {
	        	        type: 'vbox',
	        	        align: 'center',
	        	        pack :'center'
	        	    },
					items:[{
			   			xtype:'combobox',
			   			fieldLabel:common.label.fieldset,
			   			labelSeparator :'',//去掉后面的冒号
			   			store:tempItemStore,
			   			emptyText:common.label.selectFieldset,
			   			editable:false,
			   			displayField:'name',
			   			valueField:'id',
			   			queryMode:'local',
			   			labelAlign:'right',
			   			labelWidth:35,
			   			width:comboxWidth,
			   			listeners:{
			   				select:function(combo,ecords){
								Ext.getCmp('tempField_combobox').reset();
								tempFieldStore.load({
									params:{
										fieldsetid:combo.value
									},
									callback: function(record, option, succes){
										
									}
								});
							}
						}
			   		},{
			   			id:'tempField_combobox',
			   			xtype:'combobox',
			   			fieldLabel:common.label.item,
			   			labelSeparator :'',//去掉后面的冒号
			   			store:tempFieldStore,
			   			emptyText:common.label.selectItem,
			   			editable:false,
			   			displayField:'name',
			   			valueField:'id',
			   			queryMode:'local',
			   			labelWidth:35,
			   			width:comboxWidth,
			   			labelAlign:'right',
			   			style:'margin-top:10px',
			   			listeners:{
			   				select:function(combo,records){ 
								var name = records.data.name;
								//将选择的指标添加到公式框
			    				tempVar.symbol('formulaVal',name.substring(name.indexOf(":")+1,name.length));
			    				Ext.getCmp('tempCode_combobox').reset();
								tempCodeStore.load({
									params:{
										codeid:combo.value
									},
									callback: function(record, option, succes){
										if(record.length>1){
											Ext.getCmp('tempCode_combobox').show(); 
										}else{
											Ext.getCmp('tempCode_combobox').hide(); 
										}
									}
								});
							}
						}
			   		},{

			   			id:'tempCode_combobox',
			   			xtype:'combobox',
			   			fieldLabel:common.label.code,//代码
			   			labelSeparator :'',//去掉后面的冒号
			   			store:tempCodeStore,
			   			emptyText:common.label.selectItem,
			   			editable:false,
			   			displayField:'dataName',
			   			valueField:'dataValue',
			   			queryMode:'local',
			   			labelWidth:35,
			   			width:comboxWidth,
			   			labelAlign:'right',
			   			style:'margin-top:10px',
			   			hidden:true,
			   			listeners:{
			   				select:function(combo,records){ 
								var value = records.data.dataValue;
								//将选择的指标添加到公式框
			    					tempVar.symbol('formulaVal','"'+value+'"');
							}
						}
			   		}]
				   	});

				   	var tempPanel = null;
				   	tempPanel = tempVar.tempVarPanel;
				   	
				   	//存放临时变量列表的panel
				    var varPanel = Ext.create('Ext.panel.Panel', {
						border : false,
						items:[{
					 	   	xtype:'fieldset',
					        title:common.label.tempVar,//临时变量
					        layout:'column',
							width: 472,
					    	height: 430,
					   		items:[{
					   			xtype:'panel',
					   			width: 450,
						    	height: 400,
					   			items:tempPanel
					   		}]
				   		}]
				   	});	
					
					//存放表达式的panel
				    tempVar.expressionTemp = Ext.create('Ext.panel.Panel', {
						border : false,
						//height: 390,
						items:[{
					 	   	xtype:'fieldset',
					        title:common.label.expression2,//"表达式"
					   		items:[{
					   			xtype:'panel',
					   			width: 360,
								height: 412,
								layout: 'border',
								border:false,
								bodyStyle: 'background:#ffffff;',
								items: [
								      //下拉框
						             { region: "west",border:false, items:tempItemPanel},
						             //表达式
						             { region: "north",border:false,items:tempFormula,style: {
										marginTop: '5px'
						        	 }}
								]
					   		}]
				   		}]
				   	});
			
				    //存放临时变量和计算公式的panel
					var bodyPanelTemp = Ext.create('Ext.panel.Panel', {
						height: 445,
						layout: 'border',
						border:false,
						bodyStyle: 'background:#ffffff;',
						items: [
				              { region: "west",border:false, width:490,items:varPanel},
				              { region: "center",id:'expressionTemp',border:false,items:[tempVar.expressionTemp]}
						]
					});
					var titleTemp = "";
					titleTemp = common.label.tempVar;
					
					//弹出的临时变量窗口
				    var winTemp=Ext.widget("window",{
				          title:titleTemp,  
				          height:520,  
				          width:850,
				          layout:'fit',
				          bodyStyle: 'background:#ffffff;',
						  modal:true,
						  resizable:false,
						  closeAction:'destroy',
						   //复写beginDrag方法，解决下拉框弹出时拖动造成页面混乱
					      beginDrag:function(){
					      		   tempVar.cellEditing.completeEdit( );
					        	   Ext.each(tempItemPanel.query('combobox'),function(combox,index){
					        	   		combox.collapse();
					        	   });
					  	  },
						  items: [{
					  		xtype:'panel',
			         		border:false,
							items:[bodyPanelTemp]
				          }],
				          buttons:[
				         			{xtype:'tbfill'},
					         		{
					         			text:common.button.close,//关闭
					         			handler:function(){
					         				winTemp.close();
					         				if(tempVar.callBackfn)//关闭的时候回调
												Ext.callback(eval(tempVar.callBackfn),null,[]);
					         			}
					         		},
					         		{xtype:'tbfill'}
					          	]
				    });             
				    winTemp.show(); 
				    winTemp.on("close",function(){
				    	if(tempVar.callBackfn)//关闭的时候回调
							Ext.callback(eval(tempVar.callBackfn),null,[]);
				    });
		},
		
		//单击单元格时执行的动作
		cellClicked:function(nid){
			tempVar.expressionTemp.enable();
			Ext.getCmp('relateReferenceButton').enable();
			var tip = Ext.getCmp('content_tip');
			if(tip){
				tip.destroy();
			}
			var map = new HashMap();
			map.put("nid",nid==undefined?"":nid+"");
			map.put("type",'1');
			map.put("flag",'2');
			map.put("module",tempVar.module);
		    Rpc({functionId:'ZJ100000073',async:false,success:tempVar.cellclickOK},map);
		},
		
		//单击单元格时显示公式
        cellclickOK:function(form,action){
			var result = Ext.decode(form.responseText);
			var formulaValue = result.cValue;
			var formula = Ext.getCmp("formulaVal");
			formula.setValue(formulaValue);
			var rows=tempVar.tempVarPanel.getSelectionModel().getSelection();
			if(rows.length == 0)
				tempVar.tempVarPanel.getView().focusRow(rows[0]);
		},
		
		//将选择的指标添加到公式框
		symbol:function(exprId,strexpr){
			var rulearea = Ext.getCmp(exprId);
			var myField = rulearea.inputEl.dom;
			var startPos = 0;//光标选中内容起始位置
			var endPos = 0;//光标选中内容结束位置
			var selectionIndex = 0;//光标选中内容结束位置
			
			//五项专项附加扣除指标当名称和系统内置得名称一致时，定义公式用到这5个指标时，程序自动加上中括号。
			if(strexpr == common.label.znjy || strexpr == common.label.jxjy || strexpr == common.label.zfzj ||
					strexpr == common.label.zfdk || strexpr == common.label.sylr) {
				strexpr = "[" + strexpr + "]"
			}
			//IE support
			if (document.selection) {
				var sel = null;
				startPos = tempVar.selectionStart;
				endPos = tempVar.selectionEnd;
				selectionIndex = tempVar.selectionIndex;
				myField.focus();
				//写入选中内容
				rulearea.setValue(myField.value.substring(0, startPos) + strexpr + myField.value.substring(endPos, myField.value.length));
				
				var index = selectionIndex + strexpr.length;
				var range = myField.createTextRange();
				range.move("character", index);//移动光标
				range.select();//选中
				tempVar.selectionIndex = index;
				tempVar.selectionStart = startPos + strexpr.length;
				tempVar.selectionEnd = endPos + strexpr.length;
			}
			//MOZILLA/NETSCAPE support 
			else if (myField.selectionStart || myField.selectionStart == '0') {
				startPos = myField.selectionStart;
				endPos = myField.selectionEnd;
				// 保存scrollTop，为了换行
				var restoreTop = myField.scrollTop;
				//写入选中内容
				rulearea.setValue(myField.value.substring(0, startPos) + strexpr + myField.value.substring(endPos, myField.value.length));
				
				if (restoreTop > 0) {//换行
					myField.scrollTop = restoreTop;
				}
				myField.focus();
				myField.selectionStart = startPos + strexpr.length;
				myField.selectionEnd = startPos + strexpr.length;
			}else {
				myField.value += strexpr;
		    }
		},
		
		//获得光标位置
		getCursorPosition:function () { 
			var rulearea = Ext.getCmp('formulaVal');
	   		var el = rulearea.inputEl.dom;//得到当前textarea对象
	   		if(document.selection){
	   		    el.focus();
	   		    var r = document.selection.createRange(); //返回当前网页中的选中内容的TextRange对象
	   		    if (r == null) { 
	   		    	tempVar.selectionStart = 0; 
	   		    } 
	   		    var re = el.createTextRange(), //选中内容
	   		        rc = re.duplicate(); //所有内容
	   		        
	   		    try{
		   		    //定位到指定位置
		   		    re.moveToBookmark(r.getBookmark());    		   
	   		    	//【为了保持选区】rc的开始端不动，rc的结尾放到re的开始
	   		    	rc.setEndPoint('EndToStart', re); 
	   		    }catch(e){
	   		    	//表格控件点击刷新页面按钮后，此时鼠标焦点拿不到 lis 20160704
	   		    }

	   		    var text = rc.text;
	   		    text = text.replace(/\r/g,'');//替换回车符 lis 20160701
	   		    tempVar.selectionIndex = text.length; //光标位置
	   		    tempVar.selectionStart = rc.text.length; 
	   		    tempVar.selectionEnd = tempVar.selectionStart + re.text.length;
	   		  } 
		},
		
		//分享按钮勾选框，勾选操作
		shareFlag:function(obj,nid){
			if(nid){
				var map = new HashMap();
				map.put("nid",nid);
				map.put("type",tempVar.type);
				map.put("salaryid",tempVar.id);
				if(obj.checked){//分享
				    Rpc({functionId:'ZJ100000079',success:function(form,action){
				    	tempVar.clickShareOk(obj,form,false);
					}},map);
				}else{//取消分享
				    Rpc({functionId:'ZJ100000080',success:function(form,action){
						tempVar.clickShareOk(obj,form,true);
					}},map);
				}
			}
		},
		
		//单击单元格后触发的方法
		clickShareOk:function(obj,form,flag){
			var result = Ext.decode(form.responseText);
			var succeed=result.succeed;
			if(succeed){
				var base=result.base;
				if(base != 'ok'){
					Ext.showAlert(base);
					obj.checked = flag;
				}
	        }else{
	        	Ext.showAlert(result.message);
	        	obj.checked = flag;
	        }
		},
		
		//拖拽公式，改变排列位置
		moveRecord:function(node,data,model,dropPosition,dropHandlers){
			var ori_nid=data.records[0].get("nid");
			var ori_seq=data.records[0].get('sorting');
			var to_nid=model.get('nid');
			var to_seq=model.get('sorting');
		    var map = new HashMap();
			map.put("cstate",tempVar.id);
			map.put("type",tempVar.type);
			map.put("ori_nid",ori_nid);
			map.put("ori_seq",ori_seq);
			map.put("to_nid",to_nid);
			map.put("to_seq",to_seq);
		    Rpc({functionId:'ZJ100000081',success:function(form,action){
		    	var result = Ext.decode(form.responseText);
				var succeed=result.succeed;
				if(succeed){
					tempVar.rowNum = model.index;
					tempVar.store.load();
		        }
			}},map);
		},
		
		//新增计算公式
		addTemp:function(store){						
			//tempVar.tempVarPanel.setScrollY(100,100, true);
			//var d = tempVar.tempVarPanel.body.dom;
			//d.scrollTop = 100;
			
			var obj = new Object();
			if(tempVar.type=="3")//人事异动 lis 20160711
				obj.cstate = "";
			else
				obj.cstate = ""+tempVar.id;
			obj.chz = '';
			obj.ntype = '1';
			obj.fldlen = 10;
			obj.flddec = 2;
			var count = store.count();
			store.insert(store.count(),obj);
			tempVar.rowNum = store.count()-1;
			var sel = tempVar.tempVarPanel.getSelectionModel();
			sel.select(tempVar.rowNum, false);//定位最后一行
			tempVar.cellEditing.cancelEdit();
			tempVar.cellEditing.startEditByPosition({row:tempVar.rowNum,column:1});
		},
		
		//保存临时变量组装数据
		saveTempVar:function(){
			//修改的的记录
			var modifyModels = tempVar.store.getModifiedRecords();
			Ext.Array.each(modifyModels,function(modifyModel,index){
				if(modifyModel.data.chz==null||modifyModel.data.chz==""){//如果名称为空，则不能新增成功。gaohy,2016-1-7
					tempVar.store.removeAt(tempVar.store.count()-1);
					tempVar.rowNum=tempVar.store.count()-1;
					return;
				}
				tempVar.saveData(modifyModel);
			});
			tempVar.store.load();
		},
		
		//保存临时变量
		saveData:function(model){
		var chz = model.data.chz;
			if(!!!trim(chz)){//临时变量名称为必填 lis add 20160608
				Ext.showAlert(common.error.tempVarNotNull);//临时变量名称不能为空
				//tempVar.store.reload();
				return;
			}
			if(trim(chz).length>20){
				Ext.showAlert("临时变量名称长度不能超过20个字符！");
				//tempVar.store.reload();
				return;
			}
			var map = new HashMap();
			map.put("tempvarname",model.data.chz);
			map.put("ntype",model.data.ntype);
			map.put("fidlen",model.data.fldlen+"");
			map.put("fiddec",model.data.flddec+"");
			map.put("codesetid",model.data.codesetid);
			map.put("nid",model.data.nid==undefined?"":model.data.nid+"");
			map.put("cstate",tempVar.id);
			map.put("type",tempVar.type);
			Rpc({functionId:'ZJ100000074',async:false,success: function(form,action){
				var result = Ext.decode(form.responseText);
				var base = result.base;
				if('' == base || base == null){
					var tempsel = tempVar.store.getAt(tempVar.rowNum);
					for(var key in result.storeList[0]){
						tempsel.set(key,result.storeList[0][key]);
					}
					tempVar.nid = tempsel.get('nid');
					var sel = tempVar.tempVarPanel.getSelectionModel();
					sel.select(tempVar.rowNum, true);
					model.commit();
					
					tempVar.expressionTemp.enable();//右侧可编辑
					Ext.getCmp('relateReferenceButton').enable();//相关引用按钮可以点击
					if(tempVar.nid)
						Ext.getDom('share_' + tempVar.nid).disabled = false;
					var tip = Ext.getCmp('content_tip');
					if(tip){
						tip.destroy();
					}
				}else{
					Ext.showAlert(result.base);
	               tempVar.store.reload();
				}
		    }},map);
		},
		
		//删除公式
		deleteTempVar:function(){
			var rows=tempVar.tempVarPanel.getSelectionModel().getSelection();
			var nids_array = [];
			var name = "";//要删除的临时变量名称
			var num=0;
   			Ext.each(rows ,function(record,index){ 
              		nids_array.push(record.data.nid);
              		if(index < 5)
              			name = name + "，" +  record.data.chz
              		num=index;
           	});
   			if(num >= 5)
   				name = name + "...";
			if(nids_array.length<1){
				Ext.showAlert(common.msg.pleseSelectVar);
				return;
			}
			//liuyz  bug26376  29147
			Ext.showConfirm(common.label.isDeleteSelected+common.label.tempVar+" " + name.substring(1) +"？",
				function(v){
					if(v=='yes'){
						var map = new HashMap();
						map.put("nids",nids_array.toString());
						map.put("type",tempVar.type);
						map.put("cstate",tempVar.id)
					    Rpc({functionId:'ZJ100000075',async:false,success: function(form,action){
								var result = Ext.decode(form.responseText);
								var base = result.base;
								if(base == 'ok'){
									Rpc({functionId:'ZJ100000076',async:false,success: function(form,action){
										var result = Ext.decode(form.responseText);
										var succeed=result.succeed;
										if(succeed){
											//tempVar.store.load({
												//callback: function(record, option, succes){
													Ext.showAlert(common.msg.deleteSuccess+"！");//liuyz  bug26376
													//去除删除后选中第一行
													//var sel = tempVar.tempVarPanel.getSelectionModel();
													//tempVar.tempVarPanel.getView().focusRow(rows[0]);
													//sel.select(0, false);
													Ext.getCmp("formulaVal").setValue("");//临时变量，全部删除后，右侧公式面板还有数据，应该同时清空
													tempVar.store.reload();
												//}
											//});
										}else{
											Ext.showAlert(result.message);
										}
									}},map);
								}else{
									Ext.showAlert(result.base);
								}
						}},map);
					}else{
						return;
					}				
				}
			);
		},
		
		//保存计算公式表达式
		saveFormula:function(){
			var models = tempVar.tempVarPanel.getSelectionModel().getSelection();
			if(models.length==0){
				Ext.showAlert(common.msg.selectTempVar);
				return;
			}
			var c_expr = Ext.getCmp("formulaVal").getValue();
			if((c_expr == '' || c_expr == null) && (tempVar.type!=3)){//人事异动允许临时变量为空的情况 wangrd 
				Ext.showAlert(common.msg.expressionaIsNull);
				return;
			}
			
			var model = models[0];
			var map = new HashMap();
			map.put("ntype",model.data.ntype);
			map.put("nid",model.data.nid+'');
			map.put("c_expr",encode(c_expr));
			map.put("tabid",tempVar.id);
			map.put("type",tempVar.type);
			map.put("module",tempVar.module);
			Rpc({functionId:'ZJ100000077',async:false,success: function(form,action){
				var result = Ext.decode(form.responseText);
				var base = result.base;
				if(base == 'ok'){
					Rpc({functionId:'ZJ100000078',async:false,success: function(form,action){
						var result = Ext.decode(form.responseText);
						var succeed = result.succeed;
						if(succeed){
							Ext.showAlert(common.msg.saveSucess+"！");
						}else{
							Ext.showAlert(result.message);
						}
				    }},map);
				}else{
					Ext.showAlert(getDecodeStr(result.base));
				}
		    }},map);
		},
		
		//相关引用，该变量被引用
		relateReference:function(){
			var flag = 0;
			var rows=tempVar.tempVarPanel.getSelectionModel().getSelection();
   			Ext.each(rows ,function(record){ 
   					flag = flag+1;
              	});
			if(tempVar.nid == '' || flag==0){
				Ext.showAlert(common.msg.pleseSelectVar);
				return;
			}
			if(!!!tempVar.nid)
				return;
			var map = new HashMap();
			map.put("nid",tempVar.nid+"");
			map.put("type",tempVar.type);
			map.put("cstate",tempVar.id);
			Rpc({functionId:'ZJ100000082',async:false,success: function(form,action){
				var result = Ext.decode(form.responseText);
				var succeed = result.succeed;
				if(succeed){
					var isok = result.isok;
					if(isok==0)
					{
//						var filename = result.filename;
//						open("/servlet/DisplayOleContent?filename="+filename,"txt");
						var textValue = result.textValue;
						var win=Ext.widget("window",{
					          title:"相关引用说明",  
					          height:500,  
					          width:400,
					          minButtonWidth:40,
					          layout:'fit',
					          bodyStyle: 'background:#ffffff;',
							  modal:true,
							  resizable:false,
							  closeAction:'destroy',
							  items: [{
					         	border:false,
								xtype:'textareafield',
								height:425,
								value:textValue,
								style:'margin-bottom:10px;',
								id:'relateReference'
						      }],
					          buttons:[
						         		{xtype:'tbfill'},
						         		{
						         			text:common.button.close,//关闭
						         			handler:function(){
						         				win.close();
						         			}
						         		},
						         		{xtype:'tbfill'}
						          	]
					    });             
					    win.show(); 
					}else if(isok==1)
					{
						Ext.showAlert(common.msg.noReference);
					}
				}else{
					Ext.showAlert(common.button.promptmessage);
				}
		    }},map);
		},
		
		//函数向导
		functionWizard:function(){
			Ext.require('EHR.functionWizard.FunctionWizard',function(){
				Ext.create("EHR.functionWizard.FunctionWizard",{keyid:tempVar.id,opt:"5",checktemp:'salary',mode:'xzgl_jsgs',type:tempVar.type,callbackfunc:'tempVar.getfunctionWizard'});
			});
		},
		
		//函数向导回调函数，用来接收返回值
		getfunctionWizard:function(obj){
			tempVar.symbol('formulaVal',obj);
		}
 });