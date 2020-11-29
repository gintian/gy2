Ext.define('HolidayManageTemplateUL.Holiday',{
	
	constructor:function(config) {
		holidayManage = this;
		holidayManage.holidayType = "";
		holidayManage.typeLength = 0;
		holidayManage.holidayYear = "";
		holidayManage.table = "";
		holidayManage.subModuleId = "";
		holidayManage.leaveTimeType = "";
		holidayManage.isRemoveAll = false;
		holidayManage.leaveActiveTime = "";
		this.init();
	},
	// 初始化函数
	init: function() {
		var map = new HashMap();
		map.put("holidayType",holidayManage.holidayType);
		map.put("isload","1");
		map.put("flag",",init,");
	    Rpc({functionId:'KQ00010001',success:holidayManage.loadeTable},map);
	},
	
	loadeTable: function(response){
		if(Ext.util.CSS.getRule(".type-selected-cls")){
			Ext.util.CSS.updateRule(".type-selected-cls","text-decoration","underline");
			Ext.util.CSS.updateRule(".type-selected-cls","margin-left","10px");
			Ext.util.CSS.updateRule(".type-selected-cls","margin-right","10px");
		}else
			Ext.util.CSS.createStyleSheet(".type-selected-cls{text-decoration:underline;margin-left:10px;margin-right:10px}","underline"); 
		
		var map	 = Ext.decode(response.responseText);
		if(map.succeed){
			var errors = map.errors;
			if(!Ext.isEmpty(errors)){
				Ext.showAlert(errors);
				return;
			}
			var conditions=map.tableConfig;
			var tableConfig = Ext.decode(conditions);
			holidayManage.subModuleId = map.subModuleId;
			if(!Ext.util.Cookies.get(tableConfig.cookiePre+"_" + holidayManage.subModuleId + "_filter"))
				Ext.util.Cookies.set(tableConfig.cookiePre+"_" + holidayManage.subModuleId + "_filter","e0122");
			
			holidayManage.table = new BuildTableObj(tableConfig);
			var tablePanel = holidayManage.table.getMainPanel();
			
			holidayManage.holidayType = map.holidayType;
			holidayManage.holidayYear = map.year;
			holidayManage.leaveTimeType = map.leaveTimeType;
			
			var holidayTypeJson = Ext.decode(map.holidayTypeJson);
			holidayManage.typeLength = holidayTypeJson.length;
			var tbar = new Ext.Toolbar({
				id:'toolbar1',
				height:25,
				padding:'0 0 0 5',
				border:false,
				items:holidayTypeJson
			}); 
			holidayManage.table.insertItem(tbar, 0);
			// 34208 若功能导航没有权限功能则不显示
			if(map.menus.length > 2){
				var menuButton = Ext.create('Ext.Button', {
				    text: '功能导航',
				    arrowAlign: 'right',
				    menu: Ext.decode(map.menus)
				});
				Ext.getCmp(holidayManage.subModuleId + "_toolbar").insert(0, menuButton);
			}
			
			var otForLeaveCycle = map.otForLeaveCycle;
			if("0" != otForLeaveCycle && "4" != otForLeaveCycle){
				var holidayYearJson = Ext.decode(map.holidayYearJson);
				var dataStore = Ext.create('Ext.data.Store', {
					fields:['name','id'],
					data: holidayYearJson,
					autoLoad: true
				});
				
				var combox = Ext.create("Ext.form.field.ComboBox", {
					id : 'codeComId',
					store: dataStore,
					width : 120,
					queryMode: 'local',
					displayField: 'name',
					valueField: 'id',
					labelSeparator: '',
					triggerAction : 'all',
					value: holidayManage.holidayYear,
					listeners: {
						select: function(txtP,newValue,oldValue) {
							holidayManage.holidayYear = newValue.data.id;
							holidayManage.holdQueryReload();
						}
					}
				});
				
				var index = 5;
				if(holidayManage.holidayType == holidayManage.leaveTimeType)
					index = 2;
				
				Ext.getCmp(holidayManage.subModuleId + "_toolbar").insert(index, combox);
			}
			
			if(holidayManage.leaveTimeType == holidayManage.holidayType){
				holidayManage.leaveActiveTime = map.leaveActiveTime;
				var leaveActiveTime = { 
					xtype: 'tbtext', 
					id:'tbText', 
					html: '调休有效范围：' + map.leaveActiveTime 
				};
				
				Ext.getCmp(holidayManage.subModuleId + "_toolbar").add(leaveActiveTime);
			}

			new Ext.Viewport( {
				id:"holidayPort",
				layout : "fit",
				items:[tablePanel]
			});
		} else
			Ext.showAlert(map.message);
			
	},
	/**
	 * 刷新时保留快速查询框的条件
	 * 
	 * **/
	holdQueryReload: function (){
		var map = new HashMap();
		map.put("holidayType", holidayManage.holidayType);
		map.put("year", holidayManage.holidayYear);
		map.put("subModuleId", holidayManage.subModuleId);
		map.put("flag",",init,holdQuery,");
	    Rpc({functionId:'KQ00010001',success:function(response){
	    	Ext.getCmp(holidayManage.subModuleId + '_tablePanel').getStore().reload();
	    	var valueMap = Ext.decode(response.responseText);
        	if(valueMap.succeed){
        		if(valueMap.leaveActiveTime){
        			holidayManage.leaveActiveTime = valueMap.leaveActiveTime;
        			Ext.getCmp('tbText').setHtml('调休有效范围：' + valueMap.leaveActiveTime); 
        		}
        		
        		var showFlag = valueMap.showFlag
        		if("false" == showFlag) {
        			var calculateButton = Ext.getCmp("calculate");
        			if(calculateButton)
        				calculateButton.hide();
        			
        			var deleteButton = Ext.getCmp("delete");
        			if(deleteButton)
        				deleteButton.hide();
        			
        			Ext.getCmp("importData").hide();
        		} else {
        			var calculateButton = Ext.getCmp("calculate");
        			if(calculateButton)
        				calculateButton.show();
        			
        			var deleteButton = Ext.getCmp("delete");
        			if(deleteButton)
        				deleteButton.show();
        			
        			Ext.getCmp("importData").show();
        		}
        	}
	    }},map);
	},
	reloadStore: function (){
		var map = new HashMap();
		map.put("holidayType", holidayManage.holidayType);
		map.put("year", holidayManage.holidayYear);
		map.put("flag",",init,");
	    Rpc({functionId:'KQ00010001',success:function(response){
	    	Ext.getCmp(holidayManage.subModuleId + '_tablePanel').getStore().reload();
	    	var valueMap = Ext.decode(response.responseText);
        	if(valueMap.succeed){
        		if(valueMap.leaveActiveTime){
        			holidayManage.leaveActiveTime = valueMap.leaveActiveTime;
        			Ext.getCmp('tbText').setHtml('调休有效范围：' + valueMap.leaveActiveTime); 
        		}
        		
        		var showFlag = valueMap.showFlag
        		if("false" == showFlag) {
        			var calculateButton = Ext.getCmp("calculate");
        			if(calculateButton)
        				calculateButton.hide();
        			
        			var deleteButton = Ext.getCmp("delete");
        			if(deleteButton)
        				deleteButton.hide();
        			
        			Ext.getCmp("importData").hide();
        		} else {
        			var calculateButton = Ext.getCmp("calculate");
        			if(calculateButton)
        				calculateButton.show();
        			
        			var deleteButton = Ext.getCmp("delete");
        			if(deleteButton)
        				deleteButton.show();
        			
        			Ext.getCmp("importData").show();
        		}
        	}
	    }},map);
	},
	
	deletePersonHoliday: function(){
		var selectRecord = Ext.getCmp(holidayManage.subModuleId + '_tablePanel').getView().getSelectionModel().getSelection();
		if(selectRecord.length<1){
			Ext.showAlert("请选择要删除的数据！");
			return;
		} 

		Ext.Msg.confirm('提示信息', '确认删除所选择的数据吗？', function(btn) {
			if (btn == 'yes') {
				var deleteDatas = new Array();
				for(var i = 0; i < selectRecord.length; i++){
					var data = selectRecord[i].data;
					deleteDatas[i] = data.primarykey_e + ":" + data.q1701 + ":" + data.q1709;
				}
				
				var map = new HashMap();
				map.put("datas", deleteDatas);
		        Rpc({functionId:'KQ00010003',success:function(response){
		        	var valueMap = Ext.decode(response.responseText);
		        	if(valueMap.succeed){
		        		Ext.showAlert(valueMap.msg);
		        		holidayManage.holdQueryReload();
		        	} else
		        		Ext.showAlert(valueMap.msg);
				}},map);
			}
		});
	},
	
	setCalculationFormula: function(){
		Ext.require('EHR.defineformula.DefineFormula',function(){
			Ext.create("EHR.defineformula.DefineFormula",{module:"4", hoildayType:holidayManage.holidayType,
				hoildayYear:holidayManage.holidayYear,infor_type:'1',dragDropFlag:1});
		})
	},
	
	calculate:function(){
		var map = new HashMap();
		map.put("holidayType", holidayManage.holidayType);
		map.put("holidayYear", holidayManage.holidayYear);
        Rpc({functionId:'KQ00010007',success:holidayManage.showCalculateWin},map);
	},
	/**
	 * 计算时上移、下移公式
	 */
	setSort:function(start){
		var formulaGrid = Ext.getCmp('formulaGrid');
        // 获得选中的行 
        var seletedGird = formulaGrid.getSelectionModel().getSelection();
        if (seletedGird.length == 0) {  
	       	 Ext.showAlert("请选中要调整顺序的行！");  
	         return;  
        } 
        var store = formulaGrid.getStore();  
        // 获得选中的行在store内的行号
        var selectedRow = seletedGird[0];
        var selectedRowIndex = store.indexOf(selectedRow);
    	var cindex = undefined;  
     	var total = store.getTotalCount();  
     	store.remove(seletedGird[0]);
     	
     	switch (start) {  
             case "up":  
                 if (selectedRowIndex != undefined && selectedRowIndex > 0) {  
                     cindex = selectedRowIndex - 1;  
                     store.insert(cindex, selectedRow);  
                 }  
                 else {  
                     cindex = 0;  
                     store.insert(cindex, selectedRow);  
                 }  
                 formulaGrid.getSelectionModel().select(cindex);  
                 break;  
             case "down":  
                 if (selectedRowIndex != undefined && selectedRowIndex < total -1) {  
                     cindex = selectedRowIndex + 1;  
                     store.insert(cindex, selectedRow);  
                 }  
                 else {  
                     cindex = total -1;  
                     store.insert(cindex, selectedRow);  
                 }  
                 formulaGrid.getSelectionModel().select(cindex);  
                 break;  
             default:  
            	 break;  
         }  
	},
	
	showCalculateWin:function(response){
		var valueMap = Ext.decode(response.responseText);
    	if(valueMap.succeed){
    		var nbaseJson = Ext.decode(valueMap.nbaseJson);
			var fieldSetStore = Ext.create('Ext.data.Store', {
				fields:['name','id'],
				data:nbaseJson
			});
		
			var dbnameCombox = Ext.create('Ext.form.ComboBox', {
			     id:'dbnaseCombobox',
			   	 fieldLabel:'人员库',
			   	 labelAlign:'right',
			   	 labelSeparator :'',//去掉后面的冒号
			   	 margin:'10 0 5 10',
			   	 store:fieldSetStore,
			   	 valueField:'id',
			   	 displayField:'name',
			   	 labelWidth:40,
			   	 width:160,
			   	 style:'margin-top:15px',
			   	 value:valueMap.dbpre,
			   	 listeners:{
			   	 	'selectclick':function(combo,ecords){}
				}
			 });
		
			var datePanel = Ext.create('Ext.form.Panel', {
			    width: '100%',
			    margin:'5 0 5 10',
			    layout:{
					type : 'hbox',
					align : 'left'
				},
				border: false,
			    items: [{
			        xtype: 'datefield',
			        fieldLabel: '从',
			        labelAlign:'right',
			        labelWidth:40,
			        id: 'fromDate',
			        format:'Y-m-d',
			        width:160,
			        value:valueMap.feastStart,
			        maxValue: new Date()  
			    }, {
			        xtype: 'datefield',
			        fieldLabel: '到',
			        labelAlign:'right',
			        labelWidth:40,
			        id: 'endDate',
			        format:'Y-m-d',
			        width:140,
			        value:valueMap.feastEnd,
			    }]
			});
			
			var fieldJson = Ext.decode(valueMap.fieldJson);
			var girdStore = Ext.create('Ext.data.Store', {
			    fields:[ 'fielditem'],
			    data: fieldJson
			});
			
			var grid = Ext.create('Ext.grid.Panel', {
				id:'formulaGrid',
			    store: girdStore,
			    hideHeaders:true,
			    columns: [{ 
			    	text: '指标名称', 
			    	dataIndex: 'fielditem', 
			    	width:'100%',
			    	menuDisabled:true,
			    	sortable: false
			    }],
			    viewConfig:{
					plugins:{
						ptype:'gridviewdragdrop',
						//拖放数据
						dragText:common.label.DragDropData
		            }
				},
				rowLines: false,
			    height: 145,
			    width: 255
			});
			
			var gridPanel = Ext.create('Ext.panel.Panel', {
				width: '100%',
				height: 145,
				border: false,
				margin:'5 0 0 0',
				layout: "border",
				items:[{
				    width: 55,
				    border: false,
				    html: '计算顺序（上下拖动调整顺序）',
				    region: 'west'
				}
				, grid,
				{
					xtype: 'panel',
					layout: {
				        type: 'vbox',
				        align: 'middle'
				    },
				    width: 57,
				    border: false,
				    items:[{
					    	xtype: 'button',
					    	text:'上移',
					    	listeners: {
						        click: {
						            element: 'el', 
						            fn: function(){ holidayManage.setSort("up"); }
						        }
					    	}
					    },{
					    	xtype: 'button',
					    	text:'下移',
					    	margin: '14 0 0 0',
					    	listeners: {
						        click: {
						            element: 'el', 
						            fn: function(){ holidayManage.setSort("down"); }
						        }
					    	}
					    }
				    ],
				    region: 'east'
				}]
			});
			
			var panel = Ext.create('Ext.panel.Panel', {
				width: '100%',
				height:'77%',
				region: 'north',
				margin:'0 0 5 0',
				border: false,
				items:[{
					xtype:'fieldset',
			        height: '100%',
			        title: '计算范围',
			        layout:{
						type : 'vbox',
						align : 'left'
					},
			        items :[dbnameCombox,datePanel,gridPanel]
				}]
			});
			
			var surplusPanel = Ext.create('Ext.form.Panel', {
			    width: '100%',
			    margin:'5 0 0 10',
			    layout:{
					type : 'hbox',
					align : 'left'
				},
				border: false,
			    items: [{
			    	xtype: 'checkboxfield',
					 boxLabel: '累计上年结余',
	                name: 'surplus',
	                inputValue: '2',
	                disabled: true,
	                id: 'balance'
			    },{
				    xtype: 'datefield',
				    id:'BalanceEndDate',
				    fieldLabel: '结余截至日期',
				    labelAlign:'right',
				    labelWidth:100,
				    name: 'from_date',
				    format:'Y-m-d',
				    width:220,
				    disabled: true
			    }]
			});
			
			var center = Ext.create('Ext.panel.Panel', {
				width: '100%',
				region: 'center',
				layout:{
					type : 'vbox',
					align : 'left'
				},
				items:[{
					 xtype: 'checkboxfield',
					 margin:'5 0 0 10',
					 boxLabel: '不享有此假期的人不生成记录！',
	                 name: 'topping',
	                 inputValue: '1',
	                 id: 'clearZoneBox',
	                 checked: true
	            },surplusPanel]
			});
			
			var existBalance = valueMap.existBalance;
			if("1" == existBalance){
				Ext.getCmp("balance").setDisabled(false);
				if("1" == valueMap.existBalanceEnd)
					Ext.getCmp("BalanceEndDate").setDisabled(false);
			}
			
			var win = Ext.create('Ext.window.Window', {
			    title: '公式计算',
			    height: 400,
			    width: 400,
			    resizable: false,
			    modal: true,
			    layout: "border",
			    items: [panel,center],
			    buttonAlign:'center',
			    buttons:[{
			    	xtype:'button',
			    	text:'确定',
			    	handler:function(){
			    		var nbase = Ext.getCmp("dbnaseCombobox").value;
			    		var startDateObj = Ext.getCmp("fromDate");
			    	    if(!holidayManage.selectDate(startDateObj)){
			    	    	 return;
							}
			    		var startDate = Ext.getCmp("fromDate").value;
			    		var countStart = Ext.util.Format.date(startDate,'Y-m-d');
			    		var endDateObj = Ext.getCmp("endDate");
			    	    if(!holidayManage.selectDate(endDateObj)){
			    	    	 return;
							}
			    		var endDate = Ext.getCmp("endDate").value;
			    		var countEnd = Ext.util.Format.date(endDate,'Y-m-d');
			    		if(countStart == "") {
			    			Ext.showAlert("请选择计算开始时间！");
			                return;
			    		} else if(countEnd == "") {
			    			Ext.showAlert("请选择计算结束时间！");
			                return;
			    		} else if(countStart > countEnd) {
			    			Ext.showAlert("起始时间不能大于终止时间!");
			    			return;
			    		} else {
			    			var fieldDatas="";
			    			var fieldData = grid.getStore().getData().items;
			    			if(fieldData.length < 1) {
			    				fieldDatas="q1703";
			    			}else {
			    				for(var i = 0; i < fieldData.length; i++) {
			    					var field = fieldData[i].data.fielditem.split(":");
			    					fieldDatas += field[0] + ",";
			    				}       
			    			}  
			    			
			    			var clearZoneBox = Ext.getCmp("clearZoneBox");
			    			var clearZone="";
			    			if(clearZoneBox){
			    				if(clearZoneBox.checked)
			    					clearZone="1";
			    				else
			    					clearZone="0";
			    			}
			    	      
			    			// 上年结余
			    			var balanceObj = Ext.getCmp("balance");
			    			var balanceValue = "0";
			    			if (balanceObj) {	    	     
			    				if (balanceObj.checked)
			    					balanceValue = "1";
			    			} 
			    	     
				    	     // 结余截止日期 
				    	     var balanceEndObj = Ext.getCmp("BalanceEndDate");
				    	     var balanceEndDate = "";
				    	     if(!holidayManage.selectDate(balanceEndObj)){
				    	    	 return;
								}
				    	     balanceEndDate = Ext.util.Format.date(balanceEndObj.value,'Y-m-d');
				    	     
				    	     Ext.MessageBox.wait("正在执行计算操作，请稍候...", "等待");
				             var map = new HashMap();
				             map.put("nbase", nbase);
				             map.put("countStart", countStart);
				             map.put("countEnd", countEnd);
				             map.put("fieldDatas", fieldDatas);
				             map.put("clearZone", clearZone);
				             map.put("balanceValue", balanceValue);
				             map.put("balanceEndDate", balanceEndDate);
				             map.put("holidayType", holidayManage.holidayType);
				             map.put("holidayYear", holidayManage.holidayYear);
				             Rpc({functionId:'KQ00010008',success:function (response){
				            	 Ext.MessageBox.close();
				            	 win.close();
				            	 var returnMap = Ext.decode(response.responseText);
				            	 if(returnMap.succeed){
				            		 var msg = returnMap.msg;
				            		 if("0" != msg)
				            			 Ext.showAlert(msg);
				            		 else {
				            			 Ext.showAlert("计算完成！");
				            			 holidayManage.reloadStore();
				            		 }
				            	 }
				             }},map);
				             
			    		}
			    	}
			    },{
			    	xtype:'button',
			    	text:'取消',
			    	handler:function(){
			    		win.close();
			    	}
			    }]
			});
			
			win.show();
    	} 
	},
		
	switchHolidayClass: function(id, typeId){
		for(var i = 0; i < holidayManage.typeLength - 1; i++){
			Ext.getCmp('label'+i).removeCls('type-selected-cls');
		}
		
		Ext.getCmp(id).addCls('type-selected-cls');
		holidayManage.holidayType = typeId;
		if(holidayManage.leaveTimeType == holidayManage.holidayType)
			holidayManage.isRemoveAll = true;
			
		if(holidayManage.isRemoveAll){
			var holidayPort = Ext.getCmp("holidayPort");
			if(holidayPort)
				holidayPort.destroy();
			
			if(holidayManage.leaveTimeType != holidayManage.holidayType)
				holidayManage.isRemoveAll = false;
			
			holidayManage.init();
		} else 
			holidayManage.reloadStore();
		
	},
	
	exportWin : function(){
		var exportWin = Ext.getCmp('exportWinid');
	    if(exportWin)
	    	exportWin.close();
		
	    exportWin = Ext.create('Ext.window.Window', {
			id: 'exportWinid',
		    title: '导入数据',
		    height: 180,
		    width: 300,
		    modal:true,
		    layout: {
		        align: 'middle',
		        pack: 'center',
		        type: 'vbox'
			},
		    items: [{
			    layout: 'column',
			    border: false,
			    margin: '-20 0 0 0',
			    width: 200,	
			    items: [{
			        columnWidth: 0.7,
			        border: false,		        		            
			        html: "1、 下载模板文件",		        
			    },{
			        columnWidth: 0.3,
			        border: false,
			        items: {
			            xtype: 'button',
			            text: '下载',		            		            
			            handler: holidayManage.exportTemplate
			        }
			    }]
		    },{
			    layout: 'column',
			    border: false,
			    margin: '30 0 0 0',
			    width: 200,	
			    items: [{
			        columnWidth: 0.7,
			        border: false,		        		            
			        html: "2、 请选择导入文件",		        
			    },{
			        columnWidth: 0.3,
			        border: false,
			        items: {
			            xtype: 'button',
			            id:'importHoliday',
			            text: '浏览',		            		            
			            listeners:{
			            	afterrender: holidayManage.selectFile
			    		}
			        }
			    }]
		    }]
		});
		
	    exportWin.show();
	},
	
	exportTemplate : function(){
		var map = new HashMap();
		map.put("holidayType", holidayManage.holidayType);
		map.put("subModuleId", holidayManage.subModuleId);
		// 传入调休范围有效日期
		map.put("leaveActiveTime", holidayManage.leaveActiveTime);
	    Rpc({functionId:'KQ00010005',success:holidayManage.exportSucc},map);
	},
	
	selectFile : function (){
		Ext.require('SYSF.FileUpLoad', function(){
			var uploadObj = Ext.create("SYSF.FileUpLoad",{
				isTempFile:true,
				VfsModules:VfsModulesEnum.KQ,
				VfsFiletype:VfsFiletypeEnum.doc,
				VfsCategory:VfsCategoryEnum.other,
				CategoryGuidKey:'',
				upLoadType:3,
				fileSizeLimit:'500MB',
				fileExt:"*.xls;*.xlsx;",
				buttonText:'',
				renderTo:"importHoliday",
				success:holidayManage.ImportData,
				isDelete:true,
				width:32,
				height:20
			});
			Ext.getDom("importHoliday").childNodes[1].style.marginTop = "-20px";
		});
	},
	
	ImportData:function(list){
		if(list.length < 0)
			return;
		
		var obj = list[0];
		if(obj){
			var map = new HashMap();
			map.put("fileid", obj.fileid); 
			if(holidayManage.leaveTimeType != holidayManage.holidayType) {
				map.put("holidayType", holidayManage.holidayType);
				Rpc( {
					functionId : 'KQ00010006',
					success : function (response){
						var valueMap = Ext.decode(response.responseText);
						if(valueMap.succeed){
							holidayManage.showMsgWin(valueMap.importMsg);
							holidayManage.reloadStore();
						} else{
							holidayManage.showMsgWin(valueMap.importMsg);
						}
					}
				}, map);
			} else {
				Rpc( {
					functionId : 'KQ00010011',
					success : function (response){
						var valueMap = Ext.decode(response.responseText);
						if(valueMap.succeed){
							var errorMessage = valueMap.errorMessage;
							if(errorMessage)
								Ext.showAlert(errorMessage);
							else {
								var msgJson = valueMap.msgJson;
								if(msgJson) {
									var gridStore = Ext.create('Ext.data.Store', {
										fields:['message'],
										data: Ext.decode(msgJson),
										autoLoad: true
									});
									
									var grid = Ext.create('Ext.grid.Panel', {
									    store: gridStore,
									    columns: [
									        { text: '', dataIndex: 'message', height:0,width:'99%' }
									    ],
									    height: 320,
									    width: "100%"
									});
									
									var win = Ext.create('Ext.window.Window', {
									    title: '导入数据',
									    height: 400,
									    width: 500,
									    modal:true,
									    items: [grid],
									    buttonAlign: 'center',
									    buttons: [{
									    	text: '关闭',
									    	handler:function(){
									    		win.close();
									    	}
									    }]
									});
									
									win.show();
								} else {
									var map = new HashMap();
									map.put("itemList", valueMap.itemList);
									map.put("mapsList", valueMap.mapsList);
									Rpc( {functionId : 'KQ00010012',
										success : function (response){
											var msgMap = Ext.decode(response.responseText);
											var count = msgMap.count;
											if(msgMap.succeed && !Ext.isEmpty(count)){
												Ext.showAlert("成功导入" + count + "条数据！");
												holidayManage.reloadStore();
											} else
												Ext.showAlert("数据导入失败！");
										}
									}, map);
								}
							}
						} else{
							holidayManage.showMsgWin(valueMap.importMsg);
						}
					}
				}, map);
			}
		}
		
		var exportWin = Ext.getCmp('exportWinid');
	    if(exportWin)
	    	exportWin.close();
	},
	/**
	 * 导出Excel
	 */
	exportData : function(){
		var selectRecord = Ext.getCmp(holidayManage.subModuleId + '_tablePanel').getView().getSelectionModel().getSelection();
		var selectDatas = new Array();
		for(var i = 0; i < selectRecord.length; i++){
			var data = selectRecord[i].data;
			// 34146 linbz 改为隐藏列参数primarykey_e，防止栏目设置不显示取不到
			if(holidayManage.leaveTimeType == holidayManage.holidayType)
				selectDatas[i] = data.primarykey_e;
			else{
				// 35329 防止data.q1709 栏目设置隐藏 改为holidayManage.holidayType
				selectDatas[i] = data.primarykey_e + ":" + data.q1701 + ":" + holidayManage.holidayType;
			}
		}
		
		var map = new HashMap();
		map.put("selectDatas", selectDatas);
		map.put("holidayType", holidayManage.holidayType);
		map.put("subModuleId", holidayManage.subModuleId);
	    Rpc({functionId:'KQ00010004',success:holidayManage.exportSucc},map);
	},
	
	showMuster:function(object){
		var urlstr = "/general/muster/hmuster/select_muster_name.do?b_next2=b_next2&changeDbpre=1&historyRecord=0&relatTableid=17" +
				"&isGetData=1&clears=1&operateMethod=direct&tabID=" + object.id + "&modelFlag=81";
		// 加载一次交易类，传高级花名册SQL时增加查询条件
		var map = new HashMap();
		map.put("holidayType",holidayManage.holidayType);
		map.put("year", holidayManage.holidayYear);
		map.put("subModuleId", holidayManage.subModuleId);
		map.put("flag",",muster,");
	    Rpc({functionId:'KQ00010001',success:function(response){
		    	var valueMap = Ext.decode(response.responseText);
	        	if(valueMap.succeed){    	
					var titleName = "高级花名册";
					if(!Ext.isEmpty(object.text))
						titleName = object.text;
					var win = new Ext.window.Window({
						title : titleName,
						renderTo : Ext.getBody(),
						maximized : true,
						border : false,
						closable : true,
						autoScroll : true,
						layout : 'fit',
						html : '<iframe src=' + urlstr + ' frameborder="0" scrolling="no" width="100%" height="99%"></iframe>',
						defaults : {
							border : false,
							padding : '10 0 0 50'
						}
					}).show();
	        	}
    	}},map);
	},
	
	showLeaveDetail: function (value, metaData, record) {
		if(!record)
			return;
		
		if(holidayManage.leaveTimeType == holidayManage.holidayType) {
			var personName = record.data.a0101;
			// 34146 linbz 增加primarykey_e为usr|00000009| 库和编号的隐藏列参数，防止栏目设置不显示库标识取不到
			var primaryKey = record.data.primarykey_e;
			return "<a style='color:#1B4A98;' onclick=\"holidayManage.showOverTimeLeaveDetailWin('" + primaryKey + "', '"
				+ personName + "')\">" + value + "</a>";
		} else {
			var startDate = record.data.q17z1;
			var endDate = record.data.q17z3;
			var personName = record.data.a0101;
			var primaryKey = record.data.primarykey_e;
			return "<a style='color:#1B4A98;' onclick=\"holidayManage.showLeaveDetailWin('" + primaryKey + "', '"
				+ startDate + "', '" + endDate + "', '" + personName + "')\">" + value + "</a>";
		}	
	},
	
	showLeaveDetailWin: function (primaryKey, startDate, endDate, personName) {
		var map = new HashMap();
		map.put("primaryKey", primaryKey);
		map.put("startDate", startDate);
		map.put("endDate", endDate);
		map.put("holidayType", holidayManage.holidayType);
        map.put("holidayYear", holidayManage.holidayYear);
        
	    Rpc({functionId:'KQ00010010',success:function (response){
	    	var map	 = Ext.decode(response.responseText);
	    	if(map.succeed){
	    		var conditions=map.tableConfig;
	    		var tableConfig = Ext.decode(conditions);
	    		var table = new BuildTableObj(tableConfig);
	    		var tablePanel = table.getMainPanel();
	    		var win = Ext.create('Ext.window.Window', {
				    title: holidayManage.holidayYear + "年" + personName + map.typeName + '明细',
				    height: 400,
				    width: 618,
				    modal:true,
				    layout:'fit',
				    items: [tablePanel]
				});
				
				win.show();
			} else
				Ext.showAlert(map.message);
	    }},map);
	},
	
	showOverTimeLeaveDetailWin: function (primaryKey, personName) {
		var map = new HashMap();
		map.put("primaryKey", primaryKey);
		// 传入调休范围有效日期
		map.put("leaveActiveTime", holidayManage.leaveActiveTime);
        
	    Rpc({functionId:'KQ00010013',success:function (response){
	    	var map	 = Ext.decode(response.responseText);
	    	if(map.succeed){
	    		var conditions=map.tableConfig;
	    		var hourlyBasis = map.hourlyBasis;
	    		var tableConfig = Ext.decode(conditions);
	    		var table = new BuildTableObj(tableConfig);
	    		var tablePanel = table.getMainPanel();
	    		var win = Ext.create('Ext.window.Window', {
				    title: personName + '调休加班明细（单位：' + hourlyBasis + '）',
				    height: 400,
				    width: 618,
				    modal:true,
				    layout:'fit',
				    items: [tablePanel]
				});
				
				win.show();
			} else
				Ext.showAlert(map.message);
	    }},map);
	},
	
	exportSucc: function (response){
		var value = response.responseText;
		var map	 = Ext.decode(value);
		if(map.succeed){
			var fieldName = getDecodeStr(map.fileName);
			window.location.target="_blank";
			window.location.href = "/servlet/vfsservlet?fileid=" + fieldName +"&fromjavafolder=true";
		}
	},
	
	editData: function (startDate, endDate) {
		return !startDate || !endDate || startDate < endDate;
	},
	
	startDate: function (value){
		var selectRecord = Ext.getCmp(holidayManage.subModuleId + '_tablePanel').getView().getSelectionModel().getSelection();
		if(selectRecord) {
			var endDate = selectRecord[0].data.q17z3;
			if(!holidayManage.editData(value, endDate)){
				return "起始时间不能大于终止时间！";
			}else
				return true;
		} else
			return false;
	},
	
	endDate: function (value){
		var selectRecord = Ext.getCmp(holidayManage.subModuleId + '_tablePanel').getView().getSelectionModel().getSelection();
		if(selectRecord) {
			var startDate = selectRecord[0].data.q17z1;
			if(!holidayManage.editData(startDate, value)){
				return "终止时间不能小于起始时间！";
			}else
				return true;
		} else
			return false;
	},
	
	holidaySum: function (value){
		var grid = Ext.getCmp(holidayManage.subModuleId + '_tablePanel');
		var dataStore = Ext.data.StoreManager.lookup(holidayManage.subModuleId + '_dataStore');
		var rowNum = grid.getStore().indexOf(grid.getView().getSelectionModel().getSelection()[0]);
		var selectRecord = dataStore.getAt(rowNum);
		if(selectRecord) {
			var q1705 = selectRecord.data.q1705;
			if(value - q1705 < 0){
				return "假期天数不能小于已修天数！";
			}else {
				selectRecord.set("q1707", value - q1705);
				return true;
			}
		} else
			return false;
	},
	
	holidayOff: function (value){
		var grid = Ext.getCmp(holidayManage.subModuleId + '_tablePanel');
		var dataStore = Ext.data.StoreManager.lookup(holidayManage.subModuleId + '_dataStore');
		var rowNum = grid.getStore().indexOf(grid.getView().getSelectionModel().getSelection()[0]);
		var selectRecord = dataStore.getAt(rowNum);
		if(selectRecord) {
			var q1703 = selectRecord.data.q1703;
			if(q1703 - value < 0){
				return "已修天数不能大于假期天数！";
			}else {
				selectRecord.set("q1707", q1703 - value);
				return true;
			}
		} else
			return false;
	},
	SwitchMonth:function(){
		var win = Ext.getCmp('win');
		if(win)
			win.close();
		
		var string = "";
		var month = document.getElementById("month").innerHTML;
		for(var i = 1; i < 13; i++){
			var mon = '';
			if(i<10)
				mon = "&nbsp;"+i;
			else
				mon = i;
			
			if(month == i)
				string+="<li ><a href='###' onclick='holidayManage.selectPeriodMonth("+i+")' style='background-color:rgb(84, 159, 227)'>"+mon+"月</a></li>";
			else
				string+="<li ><a href='###' onclick='holidayManage.selectPeriodMonth("+i+")'>"+mon+"月</a></li>";
			
		}
		
		win = Ext.create('Ext.window.Window', {
			id : 'win',
			header : false,
			resizable : false,
			x : Ext.get("month").getX() - 105,
			y : Ext.get("month").getY() + 20,
			width : 210,
			height : 115,
			html : "<div id='monthlist'  >"
				+ "<ul style='text-align:center'>"
				+ "<span style='color:#549FE3;'>"
				+ "<a dropdownName='monthbox' href='javascript:holidayManage.yearchange(-1);'>" 
				+ "<img dropdownName='monthbox' id='changeYear' src='/workplan/image/left2.gif' /></a>"
				+ "<span id='yearTitle'>"
				+ document.getElementById("year").innerHTML
				+ "</span>年  "
				+ "<a dropdownName='monthbox' href='javascript:holidayManage.yearchange(1);'>"
				+ "<img dropdownName='monthbox' id='yearChange' src='/workplan/image/right2.gif' /></a>"
				+ "</span></ul>"
				+ "<ul id='months'>"
				+ string
				+ "</ul></div>"
		});
		
		win.show();
		// 点击window外
		Ext.getBody().addListener('click', function(evt, el) {
			if (!win.hidden && "month" != el.id && "year" != el.id && "asd" != el.id && "changeYear" != el.id && "yearChange" != el.id && "xiaimg" != el.id)
				win.close();
		});
		
	},
		
	//选择年份
	yearchange:function(ch){
		 var year = Ext.getDom('yearTitle');
		 year.innerHTML = Number(year.innerHTML)+ch;
	},
	//选择月份
	selectPeriodMonth:function(month){
		var map = new HashMap();
		var year=Ext.getDom("yearTitle").innerHTML;
		Ext.getCmp('win').close();
		document.getElementById("year").innerHTML = year;
		document.getElementById("month").innerHTML = month;
		if(month < 10)
			month = "0"+month;
		holidayManage.holidayYear = year + "-" + month + "-01";
		holidayManage.reloadStore();
	},
	// 34312 增加栏目设置保存后的回调函数
	schemeSaveCallback:function(){
		var holidayPort = Ext.getCmp("holidayPort");
		if(holidayPort)
			holidayPort.removeAll();
		holidayManage.init();
	},
	//选择时间
	selectDate:function(obj){
	     if(obj){
	    	 if (obj.activeErrors) {
	    		 var errorDate=obj.activeErrors[0]
	    		 if(errorDate){
	    			 Ext.showAlert(errorDate);
	    			 Ext.MessageBox.hide();
	    			 return false;
	    		 }
			}
	     }
	     return true;
	},
	/**
	 * 导入消息提示过多故增加弹出窗口
	 */
	showMsgWin: function(values){
		
		if(((","+values).search(",成功导入") != -1) || ((","+values).search(",导入失败") != -1)){
			Ext.showAlert(values);
			return;
		}
		
		var win = Ext.getCmp('winid');
	    if(win)
	    	win.close();
	    
		win = Ext.create('Ext.window.Window', {
			id: 'winid',
		    title: kq.shift.shiftTip,
		    border: false,
		    height: 280,
		    width: 340,
		    modal: true,
		    autoScroll: true,
		    items: [{
		    	xtype:'panel',
		    	border: false,
				width:310,
				padding: 10,
				html: "<div>"+ values +"</div>"
		    }],
		    buttonAlign: 'center',
			buttons: [{
				text: kq.button.close,
				handler:function(){
					win.close();
				}
			}]
		}).show();
	}
	
});