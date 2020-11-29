/**
 * 班次管理
 */
Ext.define('ConfigShiftsURL.Shifts',{
	requires:['EHR.extWidget.field.CodeTreeCombox'],
	constructor:function(){
		config_shifts_me = this;
		this.getListView();
	},
	/**
	 * 初始化数据
	 */
	initData:function(){
		var symbolStore = Ext.create('Ext.data.Store', {
			id:'symbolStore',
		    fields: ['displayVal'],
		    data : [
                {"displayVal": "α"},
                {"displayVal": "β"},
                {"displayVal": "Φ"},
                {"displayVal": "Τ"},
                {"displayVal": "⊙"},
                {"displayVal": "√"},
                {"displayVal": "×"},
                {"displayVal": "＋"},
                {"displayVal": "－"},
                {"displayVal": "★"},
                {"displayVal": "☆"},
                {"displayVal": "▼"},
                {"displayVal": "▽"},
                {"displayVal": "◆"},
                {"displayVal": "◇"},
                {"displayVal": "●"},
                {"displayVal": "○"},
                {"displayVal": "■"},
                {"displayVal": "□"},
                {"displayVal": "▲"},
                {"displayVal": "△"}
		    ]
		});
		//上下班次数store
		Ext.create("Ext.data.Store",{
			id:'domainCountStore',
			fields: ['name', 'value'],
			data:[{'name':kq.shifts.once,'value':'1'},{'name':kq.shifts.twice,'value':'2'},{'name':kq.shifts.threetimes,'value':'3'}]
		});
		//班次时间段1
		Ext.create("Ext.data.Store",{
			 id:'timeStore1',
			 fields: ['startTime', 'endTime'],
            //陈总提，班次时段为1 时增加默认时段
			 data:[{'startTime':'08:30','endTime':'17:30'}]
		});
		//班次时间段2
		Ext.create("Ext.data.Store",{
			 id:'timeStore2',
			 fields: ['startTime', 'endTime'],
			 data:[{'startTime':'','endTime':''},{'startTime':'','endTime':''}]
		});
		//班次时间段3
		Ext.create("Ext.data.Store",{
			 id:'timeStore3',
			 fields: ['startTime', 'endTime'],
			 data:[{'startTime':'','endTime':''},{'startTime':'','endTime':''},{'startTime':'','endTime':''}]
		});
	},
	/**
	 * 班次列表
	 */
	getListView:function(){
		var map = new HashMap();
		var json = {"type":"pclist"};
		map.put("jsonStr", Ext.encode(json));
		Rpc({functionId:'KQ00020101',async:false,success:function(form){
			var res = Ext.decode(form.responseText);
			var obj = Ext.decode(res.tableConfig);
			obj.openColumnQuery = true;//haosl 2017-07-31 方案查询可以查询自定义指标
			obj.beforeBuildComp = function(grid){
                //复写表格默认行高的样式,因为创建了两个表格，只在最后一个创建的表格钱覆盖样式即可
				//【54768】此样式不知道什么地方用了，样式覆写后导致班次管理页面中表格内容过大的时候不会自动截断显示了，所有暂时注释掉
//                Ext.util.CSS.createStyleSheet(
//                    "#shifts_tablePanel .x-grid-cell-inner{white-space:normal;max-height:1000px;"//+kqDataMx_me.lineHeight
//                    +" !important;","shiftsLineHeight");

				grid.tableConfig.viewConfig.plugins={
					ptype : 'gridviewdragdrop',
					dragText : kq.shifts.sort.title
				};

                grid.tableConfig.listeners={
                    celldblclick:function(view,td,cellIndex,record ,tr,rowIndex,e){
                        var columnId = view.grid.columnManager.columns[cellIndex].dataIndex;
                        if(columnId == "name"){
                            config_shifts_me.showClassInfo("-1");
                        }
                    }
                }

				//添加拖拽事件
				grid.tableConfig.viewConfig.listeners={
					beforedrop : config_shifts_me.dropRecord,
					select : function(row,record,index){
						var view = config_shifts_me.tableObj.tablePanel.getView();
						config_shifts_me.rowCssChange(view,record,index,true);
					},
					deselect : function(row,record,index){
						var view = config_shifts_me.tableObj.tablePanel.getView();
						config_shifts_me.rowCssChange(view,record,index,false);
					}
				}
				grid.tableConfig.viewConfig.getRowClass=function(record, index, rowParams, store){
	                 return record.get('higlevel')=="true"?"row-gray":"row-white";
                }
			}
			this.tableObj  = new BuildTableObj(obj);
			this.mainPanel = this.tableObj.getMainPanel();
		},scope:this},map);
	},
	dropRecord : function(node,data,model,dropPosition,dropHandlers){
		var higlevel = data.records[0].get("higlevel");
		//拖拽班次是上级不允许拖拽
		if(higlevel=="true"){
			return false;
		}
		var ori_record = data.records[0];
		var ori_seq = ori_record.get("seq");
		var to_seq = model.get('seq');
		if(Ext.isEmpty(ori_seq) || Ext.isEmpty(to_seq)){
			return false;
		}
		//拖拽到的目标是上级班次怎取消拖拽
		var higlevel2 = model.get("higlevel");
		if(higlevel2=="true")
			dropHandlers.cancelDrop();
		//保存拖拽后的排序
		var map = new HashMap();
		var from_id = ori_record.get("encrypt_class_id_e");
		var to_id = model.get("encrypt_class_id_e");
		var json = {"type":"adjust_seq","from_id":from_id,"to_id":to_id,"to_seq":to_seq,"ori_seq":ori_seq};
		map.put("jsonStr",Ext.encode(json));
		Rpc({functionId:'KQ00020101',async:false,success:function(r){
			var response = Ext.decode(r.responseText);
			var jsonObj = Ext.decode(response.returnStr);
			if(jsonObj.return_code=="success"){
				var store = Ext.data.StoreManager.lookup("shifts_dataStore");
				store.load();
			}else{
				Ext.showAlert(jsonObj.return_msg);
				return;
			}
		},scope:this},map);
		
	},
	/**
	 * 保存新建|编辑班次
	 */
	saveShift:function(){
		var form = Ext.getCmp("formp").getForm();
		//表单无效
		if(!form.isValid())
			return
		//获得表单的值	
		var valueObj = form.getFieldValues();
		if(valueObj.domain_count){
			var store = Ext.data.StoreManager.lookup("timeStore"+valueObj.domain_count);
			for(i=0;i<store.getCount();i++){
				var record = store.getAt(i);
				var startTime = record.get("startTime");
				var endTime = record.get("endTime");
				if(record.get("class_id")!="0" &&( Ext.isEmpty(startTime)||Ext.isEmpty(endTime))){
					Ext.showAlert(kq.shifts.error.timenoCompletemsg);
					return;
				}

				//陈总提： 班次需要支持跨天，所以不需要校验了
				var index = i+1;
				valueObj["onduty_"+index]=startTime;
				valueObj["offduty_"+index]=endTime;
			}
		}
		var map = new HashMap();
		var json = {"type":"save","info":valueObj};
		map.put("jsonStr",Ext.encode(json));
		Rpc({functionId:'KQ00020101',async:false,success:function(r){
			var response = Ext.decode(r.responseText);
			var jsonObj = Ext.decode(response.returnStr);
			if(jsonObj.return_code=="success"){
				Ext.getCmp("addShitWin").destroy();
			}else{
				Ext.showAlert(jsonObj.return_msg);
				return;
			}
			var store = Ext.data.StoreManager.lookup("shifts_dataStore");
			if(store)
				store.load();
			
		},scope:this},map);
		
	},
	/**
	 * 新建班次
	 */
	addShitView:function(opt,readOnly,record){
		config_shifts_me.initData();
		var title = opt=="edit"?kq.shifts.modify:kq.shifts.create;
		readOnly = !!readOnly;
		if(readOnly){
			title=kq.shifts.read;
		}
		//休息班次 的名称 简称 时段数，时间不允许修改
		var readOnly2 = readOnly?true:record && record.get("class_id")=="0"
		Ext.create("Ext.window.Window",{
			id:'addShitWin',
			title:title,
			modal:true,
			resizable :false,
			width:650,
			height:470,
			layout:'fit',
			bbar: [
				   '->',
				  { xtype: 'button',disabled : readOnly, text: kq.button.ok,width:75,height:22,handler:function(){
					  config_shifts_me.saveShift();
				  } },
				  { xtype: 'button', text: kq.button.cancle,width:75,height:22,handler:function(){
					  var addShitWin = Ext.getCmp("addShitWin");
					  if(addShitWin){
						  addShitWin.destroy();
					  }
				  }},
				  '->'
				],
			items:[
				{
					xtype:'form',
					id:'formp',
					padding:'10 10 0 10',
					border:false,
					items:[
						{
							xtype:'container',
							width:'100%',
							height:35,
							layout:'hbox',
							items:[
								{
									xtype:'hiddenfield',
									name:'encrypt_class_id',//班次编号加密
									value:''
								},
								{
									xtype:'textfield',
									name:'name',
									readOnly:readOnly2,
                                    fieldStyle:readOnly2?'background-color:#e1e1e1':'',
									fieldLabel:kq.label.name+'<font style="color:#FF3330;margin-top:3px;">*</font>',
									emptyText:kq.shifts.confirm.input+kq.shifts.name,
									maxLength:50,
									labelWidth:55,
									labelAlign:'right',
									width:280,
									margin:'0 20 0 0',
									allowBlank:readOnly2
								},
								{
									xtype:'textfield',
									readOnly:readOnly2,
                                    fieldStyle:readOnly2?'background-color:#e1e1e1':'',
									name:'abbreviation',
									labelAlign:'right',
									fieldLabel:'简称<font style="color:#FF3330;margin-top:3px;">*</font>',
									emptyText:kq.shifts.confirm.input+kq.shifts.sname,
									maxLength:4,
									labelAlign:'right',
									allowBlank:readOnly2,
									width:300,
									labelWidth:60
								}
							]
						},{
							xtype:'container',
							height:35,
							width:'100%',
							layout:'hbox',
							items:[
								{
									xtype:'label',
									text:kq.shifts.color,
									width:35,
									margin:'3 0 0 25'
								},{
									id:'colorValue',
									xtype:'hiddenfield',
									name:'color',
									value:'#FFCC00'
								},{
									xtype:'colorpicker',
									id:'colorpicker',
									floating:true,
									hidden:true,
									x:45,
									y:22,
									listeners:{
										select:function(picker,color){
											var colorDiv = Ext.getDom("colorDiv");
											colorDiv.style.backgroundColor="#"+color;
											picker.setHidden(true);
											var colorValue = Ext.getCmp("colorValue");
											if(colorValue)
												colorValue.setValue("#"+color);
										}
									}
								},
								{
									xtype:'container',
									id:'colorDiv',
									style:'background-color:#FFCC00;border-style:solid;border-color:#B5B8C8;'+(!!readOnly?'':'cursor:pointer;'),
									width:60,
									height:20,
									padding:'1 1 1 1',
									margin:'2 0 0 0',
									border:true,
									listeners: {
										click: {
								            element: 'el', 
								            //打开选色板
								            fn: function(a, o){
								            	if(readOnly)
								            		return;
								            	var colorPicker = Ext.getCmp("colorpicker");
								            	colorPicker.setHidden(!colorPicker.hidden);
								            }
								        },afterrender:function(){
								        	  Ext.get("addShitWin").on("click", function(e) {
								        		 e = e || window.event;
								       	         var target = e.target || e.srcElement;
								       	         var colorDiv = Ext.getCmp("colorDiv");
								       	         if(target.id.indexOf("colorDiv")<0){
								       	        	 var colorPicker = Ext.getCmp("colorpicker");
									       	         if(!colorPicker.hidden) {
									        			 colorPicker.setHidden(true);
									        		 }
								       	         }
								        	  });
								        }
									}
								},{
									xtype:'combo',
									fieldLabel:kq.shifts.symbol,
									name:'symbol',
									editable:true,
									margin:'0 0 0 5',
									labelWidth:	50,
									readOnly:readOnly,
                                    fieldStyle:readOnly?'background-color:#e1e1e1':'',
									maxLength:3,
									labelAlign:'right',
									width:155,
									valueField:"displayVal",
									displayField:"displayVal",
									store:Ext.data.StoreManager.lookup("symbolStore")
								},
								{
									xtype:'codecomboxfield',
									fieldLabel:kq.shifts.statype,
                                    //43971 考勤管理（医院、高校）：【陈总提】班次管理中，班次的统计属性恢复原来的设计，详见附件！
									codesetid:'85',
									readOnly:readOnly,
                                    fieldStyle:readOnly?'background-color:#e1e1e1':'',
									name:'statistics_type',
                                    onlySelectCodeset:true,
									margin:'0 0 0 20',
									inputable:false,
									labelWidth:60,
									labelAlign:'right',
									width:300,
									editable:readOnly?false:true,
									listeners:{
										//对于editable：false的，如果选中汉字再输入会搜索，导致不对，现在对于不让修改的直接光标出去
										focus:function(){
						        			readOnly?this.blur():this.focus();
										}
									}
								}
							]
						},{
							xtype:'container',
							height:35,
							width:'100%',
							layout:'hbox',
							items:[
								{
									xtype:'combo',
									id:'domain_count',
									fieldLabel:kq.shifts.dayoftimes+"<font style='color:#FF3330;margin-top:3px;'>*</font>",
									name:'domain_count',
									editable:false,
									readOnly:readOnly2,
                                    fieldStyle:readOnly2?'background-color:#e1e1e1':'',
									allowBlank:readOnly2,
									labelWidth:100,
									width:280,
									valueField:"value",
									displayField:"name",
									store:Ext.data.StoreManager.lookup("domainCountStore"),
									listeners:{
										afterrender:function(combo){
											if(opt=="edit")
												return;
											var store = combo.getStore();
											if(store.getCount()>0){
												var record = store.getAt(0);
												combo.setValue(record.get("value"))
												combo.fireEvent("select",combo,record);
											}
										},
										select:function(combo,record){
											var ondutys = Ext.getCmp("ondutys");
											if(ondutys.hidden){
												ondutys.setHidden(false);
											}
											var value = record.get("value");
											if(value=='1'){
												ondutys.setHeight(64);
											}else if(value=="2"){
												ondutys.setHeight(95);
											}else if(value=='3'){
												ondutys.setHeight(126);
											}
											var store = Ext.data.StoreManager.lookup("timeStore"+value);
											ondutys.setStore(store);
										}
									}
								},
								{
									xtype:'numberfield',
									name:'work_hours',
									fieldLabel:kq.shifts.hours,
									margin:'0 0 0 20',
									minValue:0,
									readOnly:readOnly2,
                                    fieldStyle:readOnly2?'background-color:#e1e1e1':'',
									labelWidth:60,
									emptyText:kq.shifts.confirm.input+kq.shifts.workhours,
									labelAlign:'right',
									width:300
								}
							]
						},{
							xtype:'grid',
							id:'ondutys',
							store:Ext.data.StoreManager.lookup("timeStore"),
							disableSelection:true,
							sortableColumns:false,
							width:176,
							hidden:true,
							scroll:false,
							margin:'0 0 0 105',
							columnLines:true,
						    columns: [
						        { 
						        	text: kq.shifts.starttime, dataIndex: 'startTime',width:88,menuDisabled:true,
						        	align:'center',
						        	editor:{
						        		xtype:'timefield',
					        			format:"H:i",
					        			pickerMaxHeight:150,
					        			formatText: 'HH:mm',
					        			minValue: '00:00',
						    	        maxValue: '23:59',
						    	        invalidText:kq.shifts.error.tformat+'{0}',
					        			increment:10
						        	}
						        },
						        { 
						        	text: kq.shifts.endtime, dataIndex: 'endTime',width:88,menuDisabled:true,
						        	align:'center',
						        	editor:{
						        		xtype:'timefield',
					        			format:"H:i",
					        			pickerMaxHeight:150,
					        			formatText: 'HH:mm',
					        			invalidText:kq.shifts.error.tformat+'{0}',
					        			minValue: '00:00',
						    	        maxValue: '23:59',
						    	        increment:10
						        	}
						        }
						    ],
						    plugins : [Ext.create('Ext.grid.plugin.CellEditing', {
						    	clicksToEdit:1,
								listeners : {
									beforeedit:function(editor,e){
										if(readOnly2)
											return false;
										return true;
									},
									edit:function(editor,e){
										if(!e.value || e.value.length==0){
											e.cancel = true;
											return;
										}
										var time = Ext.Date.format(e.value,"H:i");
										e.record.set(e.field,time);
									}
								}
							})],
						},{
				        	xtype:'codecomboxfield',
				        	size:50,
				        	width:280,
				        	labelWidth:55,
				        	margin:"10 0 0 0",
				        	name:'org_id',
				        	onlySelectCodeset:false,
				        	codesetid:"UM",
				        	ctrltype:'3',
				        	nmodule:"11",
				        	readOnly:readOnly2,
                                fieldStyle:readOnly2?'background-color:#e1e1e1':'',
				        	fieldLabel:kq.scheme.organization+"<font style='color:#FF3330;margin-top:3px;'>*</font>",
				        	allowBlank:readOnly2,
				        	editable:readOnly2?false:true,
				        	listeners:{
				        		afterrender:function(combo){
				        			var map = new HashMap();
				        			var json = {};
				        			json.type = "getPriv";
				        			map.put("jsonStr",Ext.encode(json));
				        			Rpc({functionId:'KQ00020101',async:false,success:function(form){
				        				var res = Ext.decode(form.responseText);	
				        				var data = Ext.decode(res.returnStr);
				        				combo.setValue(data.org_id);
				        			},scope:this},map);
				        		},
				        		focus:function(input_){
				        			readOnly2?this.blur():this.focus();
								}
				        	}
				        },{
							xtype:'textarea',
							fieldLabel:kq.scheme.remark,
							labelWidth:55,
							name:'remarks',
							readOnly:readOnly,
                            fieldStyle:readOnly?'background-color:#e1e1e1':'',
							width:600,
							height:70,
							margin:'10 0 0 0'
						},{
							xtype:'hiddenfield',
							id:'validateFld',
							name:'is_validate',
							value:1
						},
						{
							xtype:'container',
							width:'100%',
							border:false,
							margin:'5 0 0 0',
							items:[{
								xtype:'label',
								text:kq.shifts.on
							},{
								xtype:'image',
								src:'../../../../module/kq/images/kq_on.png',
								id:'validate_1',
								style:'position:relative;top:4px;margin-left:29px;'+(readOnly2?'':'cursor:pointer;'),
								width:50,
								listeners:{
									click:{
										element: 'el', 
							            //停用
							            fn: function(a, o){
							            	if(readOnly2)
							            		return;
							            	var validate_1 = Ext.getCmp("validate_1");
							            	if(validate_1){
							            		validate_1.setHidden(true);
							            	}
							            	var validate_0 = Ext.getCmp("validate_0");
							            	if(validate_0){
							            		validate_0.setHidden(false);
							            	}
							            	var validateFld = Ext.getCmp("validateFld");
							            	if(validateFld){
							            		validateFld.setValue(0);
							            	}
							            	
							            }
									}
								}
							},{
								xtype:'image',
								hidden:true,
								id:'validate_0',
								src:'../../../../module/kq/images/kq_off.png',
								style:'position:relative;top:4px;margin-left:29px;'+(readOnly2?'':'cursor:pointer;'),
								width:50,
								listeners:{
									 click:{
										element: 'el', 
							            //启用
							            fn: function(a, o){
							            	if(readOnly2)
							            		return;
							            	var validate_1 = Ext.getCmp("validate_1");
							            	if(validate_1){
							            		validate_1.setHidden(false);
							            	}
							            	var validate_0 = Ext.getCmp("validate_0");
							            	if(validate_0){
							            		validate_0.setHidden(true);
							            	}
							            	var validateFld = Ext.getCmp("validateFld");
							            	if(validateFld){
							            		validateFld.setValue(1);
							            	}
							            }
									 }
								}
							}]
						}
						
					]
				}
				
			]
		}).show();
	},
	deleteShifts:function(){
		var selectData = config_shifts_me.tableObj.tablePanel.getSelectionModel().getSelection();
		if(selectData.length == 0){
			Ext.showAlert(kq.shifts.confirm.change2del);
			return ;
		}
		var ids = [];
		for(var i in selectData){
			if(selectData[i].get("higlevel")=="true"){
				Ext.showAlert(kq.shifts.shangjibanci+"【"+selectData[i].get("name")+"】"+kq.shifts.confirm.nodel);
				return;
			}
			if (selectData[i].get("class_id") == "0"){
                Ext.showAlert("【"+selectData[i].get("name")+"】"+kq.shifts.confirm.class0+","+kq.shifts.confirm.nodel);
                return;
            }
			ids.push(selectData[i].data.encrypt_class_id_e);
		}
		Ext.showConfirm(kq.shifts.confirm.askdel,function(flag){
			if(flag!="yes")
				return;
			var map = new HashMap();
			var json = {"type":"delete","ids":ids.join(",")};
			map.put("jsonStr",Ext.encode(json));
			Rpc({functionId:'KQ00020101',async:false,success:function(form){
				var response = Ext.decode(form.responseText);
				var returnStr = Ext.decode(response.returnStr);
				if(returnStr.return_code == "fail"){
					Ext.showAlert(returnStr.return_msg);
				}else{
					//刷新store
					var store = Ext.data.StoreManager.lookup("shifts_dataStore");
					if(store)
						store.load();
				}
			}},map);
		})
	},
	/**
	 * 渲染颜色
	 */
	renderColor:function(color){
		if(color && color.length>0){
			return "<div style='background-color:"+color+"'>&nbsp;</div>";
		}
	},
	/**
	 * 渲染启用列
	 */
	renderValidate:function(value,metaData,record){
		var src = "../../../../module/kq/images/kq_on.png";
		var temp = kq.shifts.off;
		if(value!="1"){
			src = "../../../../module/kq/images/kq_off.png";
			temp = kq.shifts.on;
		}
		if(record.get("higlevel")=="true"
            ||record.get("class_id")=="0"){
			return value=="1"?kq.shifts.on:kq.shifts.off;
		}
		var id = "validate_"+record.data.encrypt_class_id_e;
		return "<img id='"+id+"' title='"+kq.shifts.click+temp+"' onclick='config_shifts_me.validteClickFn(\""+value+"\",\""+id+"\",\""+record.get("encrypt_class_id_e")+"\")' style='width:40px;cursor:pointer' src='"+src+"'/>";
	},
	/**
	 * 保存启用|停用
	 * @returns
	 */
	validteClickFn:function(value,imgId,class_id){
		var newValue = "0";
		var src = "../../../../module/kq/images/kq_off.png";
		var title=kq.shifts.click+kq.shifts.on;
		if(value!="1"){
			src = "../../../../module/kq/images/kq_on.png";
			newValue="1";
			title=kq.shifts.click+kq.shifts.off;
			config_shifts_me.selectImg(value,imgId,class_id,newValue,src,title);
		}else{
			var map = new HashMap();
			var jsonStr = {};
			jsonStr.type = "checkValidate";
			jsonStr.id = class_id;
			map.put("jsonStr",Ext.encode(jsonStr));
			Rpc({functionId:'KQ00020101',async:false,success:function(form){
				var res = Ext.decode(form.responseText);	
				var data = Ext.decode(res.returnStr);
				if(data.return_code == "fail"){
					Ext.showConfirm(kq.shifts.confirm.checkValidate,function(flag){
						if(flag=="yes"){
							config_shifts_me.selectImg(value,imgId,class_id,newValue,src,title);
						}
					});
				}else{
					config_shifts_me.selectImg(value,imgId,class_id,newValue,src,title);
				}
				
			},scope:this},map);
		}
		
	},
	/**
	 * 启用|停用图标切换
	 */
	selectImg:function(value,imgId,class_id,newValue,src,title){
		var img = document.getElementById(imgId);
		if(img){
			img.src=src;
			img.title = title;
			img.onclick = function(){
				config_shifts_me.validteClickFn(newValue,imgId,class_id);
			}
			config_shifts_me.editValidate(value,class_id);
		}
	},
	/**
	 * 校验开始结束时间
	 */
	validateTime:function(stratTime,endTime){
        var stratTimes = stratTime.split(":");
        var endTime = endTime.split(":");
        //开始时间小时
        var startH = stratTimes[0].replace(/\b(0+)/gi,"");
        if(Ext.isEmpty(startH)){
            startH = "0";
        }
        var startM = stratTimes[1].replace(/\b(0+)/gi,"");
        if(Ext.isEmpty(startM)){
            startM = "0";
        }
        var endH = endTime[0].replace(/\b(0+)/gi,"");
        if(Ext.isEmpty(endH)){
            endH = "0";
        }
        var endM = endTime[1].replace(/\b(0+)/gi,"");
        if(Ext.isEmpty(endM)){
            endM = "0";
        }
        if(parseInt(startH)>parseInt(endH)){
            return false;
        }else if(parseInt(startH)==parseInt(endH)
            && parseInt(startM)>=parseInt(endM)){
            return false;
        }
        return true;
	},
	/**
	 * 修改班次
	 */
	showClassInfo:function(class_id_e){
		var selectData = [];
		/**
		 *  如果取不到则校验 class_id_e
		 */
		if("-1" == class_id_e){
			selectData = config_shifts_me.tableObj.tablePanel.getSelectionModel().getSelection();
		}else if(!Ext.isEmpty(class_id_e)){
			config_shifts_me.tableObj.tablePanel.getStore().getData().each(function(record, index){
				if(record.data.encrypt_class_id_e == class_id_e){
					selectData.push(record);
					return false;
				}
			});
		}
		
		if(selectData.length == 0){
			Ext.showAlert(kq.shifts.confirm.change2edit);
			return ;
		}
		
		if(selectData.length > 1){
			Ext.showAlert(kq.shifts.confirm.change2edit2);
			return ;
		}
		var readOnly = selectData[0].get("higlevel")=="true";
		config_shifts_me.addShitView("edit",readOnly,selectData[0]);
		var map = new HashMap();
		var classId_e = selectData[selectData.length-1].get("encrypt_class_id_e");
		var json = {"type":"get_info","id":classId_e};
		map.put("jsonStr", Ext.encode(json));
		Rpc({functionId:'KQ00020101',async:false,success:function(form){
			var res = Ext.decode(form.responseText);	
			var data = Ext.decode(res.returnStr);
			Ext.getCmp("formp").getForm().loadRecord(new Ext.data.Model(data.return_data));
			//是否启用
			var valid = data.return_data.is_validate;
			if(valid==0){
				Ext.getCmp("validate_0").setHidden(false);
				Ext.getCmp("validate_1").setHidden(true);
			}else{
				Ext.getCmp("validate_1").setHidden(false);
				Ext.getCmp("validate_0").setHidden(true);
			}
			
			//设置颜色
			var colorDiv = Ext.getDom("colorDiv");
			if(colorDiv){
				colorDiv.style.backgroundColor=data.return_data.color;
			}
			var combo = Ext.getCmp("domain_count");
			var value = combo.getValue();
			var record = combo.findRecordByValue(value);
			if(!Ext.isEmpty(value)){
				combo.fireEvent("select",combo,record);
			}
			//回显时间段
			var onduty_1 = data.return_data.onduty_1||"";
			var onduty_2 = data.return_data.onduty_2||"";
			var onduty_3 = data.return_data.onduty_3||"";
			var offduty_1 = data.return_data.offduty_1||"";
			var offduty_2 = data.return_data.offduty_2||"";
			var offduty_3 = data.return_data.offduty_3||"";
			var store = Ext.data.StoreManager.lookup("timeStore"+value);
			var arr = [];
			if(value==1){
				arr[0]={'startTime':onduty_1, 'endTime':offduty_1};
			}
			if(value==2){
				arr.push({'startTime':onduty_1, 'endTime':offduty_1});
				arr.push({'startTime':onduty_2, 'endTime':offduty_2});
			}
			if(value==3){
				arr.push({'startTime':onduty_1, 'endTime':offduty_1});
				arr.push({'startTime':onduty_2, 'endTime':offduty_2});
				arr.push({'startTime':onduty_3, 'endTime':offduty_3});
			}
			if(arr.length>0){
				store.loadData(arr);
			}
		},scope:this},map);
		
	},
	editValidate:function(value,class_id){
		var map = new HashMap();
		var jsonStr = {};
		jsonStr.type = "validate";
		jsonStr.id = class_id;
		if(value!=1){
			jsonStr.validate = "1";
		}else{
			jsonStr.validate = "0";
		}
		map.put("jsonStr",Ext.encode(jsonStr));
		Rpc({functionId:'KQ00020101',async:false,success:function(form){
			var res = Ext.decode(form.responseText);	
			var data = Ext.decode(res.returnStr);
			if(data.return_code == "fail"){
				Ext.showAlert(data.return_msg);
			}
		},scope:this},map);
	},
	domainScopeRender:function(val,mateData,record){
		var onduty_1 = record.get("onduty_1");
		var onduty_2 = record.get("onduty_2");
		var onduty_3 = record.get("onduty_3");
		var offduty_1 = record.get("offduty_1");
		var offduty_2 = record.get("offduty_2");
		var offduty_3 = record.get("offduty_3");
		var domainScope = "";
		if(!Ext.isEmpty(onduty_1) && !Ext.isEmpty(offduty_1)){
			domainScope+=onduty_1+"~"+offduty_1;
            if(!config_shifts_me.validateTime(onduty_1,offduty_1)){
                domainScope+=kq.shifts.tommr;
            }
            domainScope+=",\n";
		}
		if(!Ext.isEmpty(onduty_2) && !Ext.isEmpty(offduty_2)){
			domainScope+=onduty_2+"~"+offduty_2;
            if(!config_shifts_me.validateTime(onduty_2,offduty_2)){
                domainScope+=kq.shifts.tommr;
            }
            domainScope+=",\n";
		}
		if(!Ext.isEmpty(onduty_3) && !Ext.isEmpty(offduty_3)){
			domainScope+=onduty_3+"~"+offduty_3;
            if(!config_shifts_me.validateTime(onduty_3,offduty_3)){
                domainScope+=kq.shifts.tommr;
            }
            domainScope+=",\n";
		}
		
		return domainScope.substring(0,domainScope.length-2);
	},
	click2editShift:function(val,mateData,record){
		return "<div style='width: 100%;cursor: pointer'><a href='javascript:void(0);' onclick='config_shifts_me.showClassInfo(\""+record.get("encrypt_class_id_e")+"\")'>"+val+"</a></div>"
	},
	/**
	 * 改变行样式
	 */
	rowCssChange:function(view,record,index,selected){
		var higlevel = record.get("higlevel");
		var node = view.getNode(index);
		var rowCss = node.rows[0].className;
		if(selected){
			if(higlevel == "true"){
				if(rowCss.indexOf("row-gray")>-1){
					view.removeRowCls(index,"row-gray");
				}
			}else{
				if(rowCss.indexOf("row-white")>-1){
					view.removeRowCls(index,"row-white");
				}
			}
			if(rowCss.indexOf("row-selected")==-1){
				view.addRowCls(index,'row-selected');
			}
		}else{
			if(rowCss.indexOf("row-selected")>-1){
				view.removeRowCls(index,'row-selected');
			}
			if(higlevel == "true"){
				if(rowCss.indexOf("row-gray")==-1){
					view.addRowCls(index,"row-gray");
				}
			}else{
				if(rowCss.indexOf("row-white")==-1){
					view.addRowCls(index,"row-white");
				}
			}
		}
	}
});