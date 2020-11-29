/**
 * 排序管理和排班审查页面表格对象
 * 注：如果要使用表格对象中的过滤功能则表格对象所在的panel或viewport的id必须是port
 * 布局（即：layout）必须是border；同时子对象中的所属的west部分不能被占用
 */
Ext.define('ShiftURL.ShiftGrid',{
    extend:'Ext.panel.Panel',
    alias:'widget.ShiftGrid',
    requires:['ShiftURL.ShiftInfo','ShiftURL.TableController'],
    
    layout: 'fit',
    border: false,
    id: 'shiftManage_0001_shiftGrid',
	initComponent:function(){
		shiftGrid = this;
		shiftGrid.callParent(arguments);
		//表格显示的列
		shiftGrid.columns = shiftGrid.config.columns;
		//数据查询的指标id
		shiftGrid.fields = shiftGrid.config.fields;
		//查询数据的sql
		shiftGrid.dataSql = shiftGrid.config.dataSql ? shiftGrid.config.dataSql : "";
		//年份
		shiftGrid.year = shiftGrid.config.year;
		//月
		shiftGrid.month = shiftGrid.config.month;
		//一个月的第几周，=-1为全月的数据
		shiftGrid.weekIndex = shiftGrid.config.weekIndex;
		//班组编号
		shiftGrid.groupId = shiftGrid.config.groupId;
		//排班方案编号
		shiftGrid.schemeId = shiftGrid.config.schemeId;
		//显示表格的页面，=shiftData：排班页面；=shiftCheck：排班页面
		shiftGrid.dataType = shiftGrid.config.dataType ? shiftGrid.config.dataType : "shiftData";
		//第几行
		shiftGrid.rowIndex = "0";
		//第几列
		shiftGrid.cellIndex = "0";
		//选中的行
		shiftGrid.selectRowIndex;
		//是否开启区域选择
		shiftGrid.regionSelect = shiftGrid.config.regionSelect ? true : false;
		//表格中锁列的数量
		shiftGrid.lockedCount = 0;
		//表格中隐藏列的数量
		shiftGrid.hiddenCount = 0;
		//调换人员的标识， =old：被调换人员；=new：调换人员
		shiftGrid.flag;
		//编辑班次的功能标识：="":表格直接双击单元格设置班次；=changgeShift：更换班次；=addShifts：新增班次
		shiftGrid.shiftFlag = "";
		//更换班次时，被更换班次是当天的第几个班次=0：第一个；=1：第二个；=2：第三个
		shiftGrid.shiftIndex = 0;
		//增加班次时，当天已设置的班次数量
		shiftGrid.shiftCount = 0;
		//表格的id的前半部分：grid的id必须为：shiftGrid.prefix+"_grid"和store的id必须为shiftGrid.prefix+"_store"
		shiftGrid.prefix = "shiftManage_0001";
		//复制或剪切的单元格的值
		shiftGrid.cellData = null;
		//每页数据显示的条数，默认20
		shiftGrid.pageSize = shiftGrid.config.pageSize ? shiftGrid.config.pageSize : 20;
		//拖拽的数据是第几行的数据
		shiftGrid.rowNumber;
		//拖拽到第几行
		shiftGrid.modelRowNumber;
		//是否删除快速查询的条件，=0：不删除；=1：删除
		shiftGrid.removeFilter = shiftGrid.config.removeFilter ? shiftGrid.config.removeFilter : "1";
		//是否需要自动保存， =true：需要；=false：不需要
		shiftGrid.editFlag = true;
		shiftGrid.selecedOrderdItem = [];
		this.showPanel();
	},

	//展现表格数据
	showPanel: function () {
		var link = document.createElement("link");
		link.rel = "stylesheet";
		link.type = "text/css";
		link.href = "../../../../module/kq/config/shiftgroup/Shift.css";
		document.getElementsByTagName("head")[0].appendChild(link);
		var store = new Ext.create('Ext.data.Store', {
			id: "shiftManage_0001_store",
			pageSize:shiftGrid.pageSize,
			fields: this.fields,
			autoLoad: true,
			proxy:{
				type:'transaction',
				functionId:'KQ00021308',
				extraParams:{
					fields: this.fields,
					dataSql: this.dataSql,
					year: this.year,
					month: this.month,
					weekIndex:  this.weekIndex,
					groupId: this.groupId,
					dataType: this.dataType,
					removeFilter: this.removeFilter
				},
				reader:{
					type:'json',
					root:'data',
					totalProperty:'totalCount'
				}
			},
			listeners: {
				//表格中数据发生变化后自动保存数据
				update: function(store, record, operation, modifiedFieldNames, details){
					if(operation!="edit" || !modifiedFieldNames || !shiftGrid.editFlag)
						return;
					
					var flag = false;
					var copyShifts = [];
					var shift = {};
					
					for(var i = 0; i < modifiedFieldNames.length; i++){
						var field = modifiedFieldNames[i];
						var value = record.get(field);
						if(!field || field.indexOf(".") < 0 || "NaN" == value)
							continue;
						
						if(!flag)
							flag = true;
						
						shift[field] = value || "";
					}
					
					if(!flag)
						return false;
					
					shift["guidkey"] = record.get("guidkey");
					copyShifts.push(shift);
			            
					var map = new HashMap();
					map.put("type", "saveCopyShiftInfos");
					map.put("copyShifts", Ext.encode(copyShifts));
					map.put("groupId", shiftGrid.groupId);
					map.put("schemeId", shiftGrid.schemeId);
					Rpc({functionId:'KQ00021308',async:false,success:function (response) {
						var map = Ext.decode(response.responseText);
						if(map.succeed){
							var countDataMap = map.countDataMap;
							var statDataMap = countDataMap[record.get("guidkey")];
							for(var key in statDataMap)
								record.set(key, statDataMap[key]);
							
							record.commit();
							Ext.getCmp("shiftManage_0001_grid").syncRowHeights();
						} else {
							Ext.showAlert(map.message);
							return false;
						}
					}},map);
				},
				
				load: function (){
					Ext.getCmp("shiftManage_0001_grid").syncRowHeights();
				},
				
				sort: function (){
					Ext.getCmp("shiftManage_0001_grid").syncRowHeights();
				}
			}
		});
		
		var pagingToolbar = new Ext.PagingToolbar({
			id:"shiftManage_0001_pagingToolbar",
			pageSize: shiftGrid.pageSize,
			emptyMsg: kq.shift.dataEmptyMsg,
			displayInfo : true,
			height: 30,
			displayMsg : kq.shift.dataMsg,
			store : store
		});
		
		var selModel = {};
		var viewConfig = {};
		var plugins = [];
		var listeners = {};
		if("shiftData" == this.dataType){
			if(shiftGrid.regionSelect) {
				selModel = {
						type: 'spreadsheet',
						columnSelect: true,
						rowSelect:true,
						pruneRemoved: false,
						extensible: 'y'
				};
				
				plugins= [Ext.create('Ext.grid.plugin.CellEditing', {
					clicksToEdit: 1
				}),{
					ptype:'clipboard',
					system:'raw',
					formats: {
						raw: {
							get: 'getCellData',
							put: 'putCellData'
						}
					},
					//复写复制的方法
					getCellData: shiftGrid.getCellData,
					//复写粘贴的方法
					putCellData: shiftGrid.putCellData
				},'selectionreplicator'];
				listeners = {
						//鼠标双击单元格事件
						celldblclick: shiftGrid.editShifts,
						//表格添加鼠标右键事件
						cellcontextmenu : shiftGrid.rightOperationMenu,
						 edit: function(editor,e){
							 var record = e.record;
							 var field = e.field;
							 if (field=="extra_days") {//存假或存班校验数据
								 var flag=shiftGrid.regexNumeric(e.value);
								 if (!flag) {
									 Ext.showAlert(kq.shift.inputError);
									 record.set(field,e.originalValue);
									 return false;
								 }
							 }
							 if(e.value !=e.originalValue){
								 record.set(field,e.value);
								 var map = new HashMap();
								 map.put("type", "saveSchemeEmp");
								 map.put("schemeId", shiftGrid.schemeId);
								 map.put("itemId", field);
								 map.put("record", JSON.stringify(record.getData()));
								 Rpc({functionId:'KQ00021308',async:false,success:function (response) {
									 var map = Ext.decode(response.responseText);
									 if(map.succeed){
										 record.commit();
									 } else {
										 Ext.showAlert(map.message);
										 return false;
									 }
								 }},map);
							 }
						 },
						 afterrender:function(gridpanel){
							 gridpanel.view.lockedView.ownerCt.el.dom.style.zIndex=100;
							 document.onkeydown=function(e){
							 	if(!e && window.event)
							 		e = window.event;
							 	
								 var keyNum=window.event ? e.keyCode :e.which;
								 if (keyNum == Ext.event.Event.DELETE){
									 shiftGrid.deleteCellData();
								 }
							 }
						 },
						 selectionchange: function (grid, selections) {
							 var selModel = grid.getSelectionModel();
							 selModel.getSelected().eachCell(function (cellContext) {
								 var column = cellContext.column;
								 var dataIndex = column.dataIndex;
								 if(!shiftGrid.checkColumn(dataIndex) || !shiftGrid.checkselectValid("," + dataIndex + ",")){
									 selModel.deselectAll(false);
									 return false;
								 }
							 });
	                     },
	                     render: shiftGrid.renderFun
				};
			} else {
				if("-1" != shiftGrid.weekIndex) {
					viewConfig = {
							plugins:{
								ptype:'gridviewdragdrop',
								dragText: kq.shift.dragText
							},
							listeners: {
								beforedrop:shiftGrid.beforedrop,
								drop:shiftGrid.dropSort
							}
					};
				}
				
				plugins= [Ext.create('Ext.grid.plugin.CellEditing', {clicksToEdit: 1})];
				listeners = {
						//鼠标双击单元格事件
						celldblclick: shiftGrid.editShifts,
						//表格添加鼠标右键事件
						cellcontextmenu : shiftGrid.rightContextmenu,
						 edit: function(editor,e){
							 var record = e.record;
							 var field = e.field;
							 if (field=="extra_days") {//存假或存班校验数据
								 var flag=shiftGrid.regexNumeric(e.value);
								 if (!flag) {
									 Ext.showAlert(kq.shift.inputError);
									 record.set(field,e.originalValue);
									 return false;
								 }
							}
							 if(e.value !=e.originalValue){
								 record.set(field,e.value);
								 var map = new HashMap();
								 map.put("type", "saveSchemeEmp");
								 map.put("schemeId", shiftGrid.schemeId);
								 map.put("itemId", field);
								 map.put("record", JSON.stringify(record.getData()));
								 Rpc({functionId:'KQ00021308',async:false,success:function (response) {
									 var map = Ext.decode(response.responseText);
									 if(map.succeed){
										 record.commit();
									 } else {
										 Ext.showAlert(map.message);
										 return false;
									 }
								 }},map);
							 }
						 },
						 render: shiftGrid.renderFun
				};
			}
		} else {
			listeners = {render: shiftGrid.renderFun};
		}
		
		viewConfig.filterable = true;
		viewConfig.markDirty = false;
		viewConfig.operaScope = Ext.create("ShiftURL.TableController",shiftGrid);
		
		var grid = Ext.create('Ext.grid.Panel', {
			id:'shiftManage_0001_grid',
			selModel: Ext.encode(selModel) == '{}' ? 'cellmodel' : selModel,
			store: store,
			columnLines: true,
			columns: this.columns,
			height: '100%',
			width: '100%',
			bbar:pagingToolbar,
			viewConfig: viewConfig,
			plugins: plugins,
			listeners: listeners
		});
		
		this.add([grid]);
	},
	renderFun: function (grid) {
		Ext.create('Ext.tip.ToolTip', {
			target: grid.id,
			delegate:"td",
			trackMouse: true,
			renderTo: document.body,
			bodyStyle:"background-color:white;border:1px solid #c5c5c5;",
			listeners: {
				beforeshow: function updateTipBody(tip) {
					var div = tip.triggerElement.childNodes[0];
					var title = "";
					if (Ext.isEmpty(div))
						return false;
					
					if(div.offsetWidth < div.scrollWidth || div.offsetHeight < div.scrollHeight){
						var havea = div.getElementsByTagName("a");
						if(havea != null && havea.length > 0){
							title = havea[0].innerHTML;
						} else 
							title = div.innerHTML;
						
						title = replaceAll(title, "\r\n", "<br>");
						title = replaceAll(title, "\n", "<br>");
						title = trimStr(title);
						if(Ext.isEmpty(title) || "&nbsp;" == title)
							return false;
						
						tip.update("<div style='WORD-BREAK:break-all;'>"+title+"</div>");
					}else
						return false;
				}
			}
		});
	},
	//校验列是否允许复制或设置班次
    checkColumn: function (dataIndex) {
    	//校验选中列对应的指标是否包含“.”，如果包含，则认为i
    	if(dataIndex.indexOf(".") < 0)
    		return false;
    	
    	return true;
    },
    //校验选择的表格是否有效
    checkselectValid: function (columns) {
    	if((columns.indexOf(",group_name,") > -1 || columns.indexOf(",shift_comment,") > -1
    			|| columns.indexOf(",extra_days,") > -1 || columns.indexOf(",extra_hour,") > -1)
    			&& (columns.indexOf(".") > -1 || "-1" == shiftGrid.schemeId))
    		return false;
    	
    	return true;
    },
    //展现班次设置窗口
    showShiftWin: function (response) {
    	var map	 = Ext.decode(response.responseText);
    	if(map.succeed){
    		var shiftInfos = map.shiftInfoList;
    		var html = "";
    		var shifts = [];
    		shiftGrid.selecedOrderdItem = [];
    		for(var i = 0; i < shiftInfos.length; i++){
    			var clsName='unselectedCls';
    			if(shiftInfos[i].checked == 1) {
    				clsName='selectedCls';
    				shiftGrid.selecedOrderdItem.push(shiftInfos[i].classId);
    			}
    			
    			shifts.push({
					xtype:'container',
					height: 35,
	                width: 65,
	                border:true,
	                html:'<div class="' + clsName +'" id="' + shiftInfos[i].classId + '" checked="' 
	                	+ shiftInfos[i].checked + '" title="' + shiftInfos[i].name 
	                	+ '" nameColor="' + shiftInfos[i].nameColor
	                	+ '" onclick="shiftGrid.changeShiftState(this)"><div>' + shiftInfos[i].name 
	                	+ '</div><p id="'+shiftInfos[i].classId+'_xuhao" class="item_xuhao"></p><span></span></div>'
				});
    		}
    		
    		html += "";
    		var win = Ext.getCmp("shiftWin");
    		if(win)
    			win.close();
    		
    		var hiddeFlag = false;
    		if(shiftGrid.shiftFlag)
    			hiddeFlag = true;
    		
    		var checkBox = Ext.create('Ext.form.field.Checkbox', {
    			id:'checkBoxId',
    			boxLabel:kq.shift.successiveShift,
    			inputValue:'1',
    			checked:false,
    			hidden:hiddeFlag,
    			margin:'0 0 0 13'
    		});
    		
    		win = Ext.create('Ext.window.Window', {
    			id: 'shiftWin',
    			title:kq.shift.setShift,
    			width: 490,
    			height: 350,
    			modal:true,
    			scrollable:'vertical',
    			resizable: false,
    			items: [checkBox,{
					//班次
					xtype:'container',
					layout:'column',
					margin:'10 0 15 12',
					items:shifts,
					defaults:{
						margin:'0 13 10 0',
						layout:{
							align:'center',
							pack:'center',
							columnWidth:65
						}
					}
				}],
				listeners:{
	                show:function () {
	                	shiftGrid.refreshItemsOrder();
	                }
	            },
				buttons:[{
					text:kq.button.ok,
					handler: function (){
						var grid = Ext.getCmp("shiftManage_0001_grid");
						if(shiftGrid.rowIndex >= grid.getStore().getTotalCount()) {
							Ext.showAlert(kq.shift.finish);
			    			return false;
						}
						
						var column = grid.columns[shiftGrid.cellIndex];
						var columnIndex = column.dataIndex;
						var record = grid.getStore().getAt(shiftGrid.rowIndex);
						var shiftinfos = record.get(columnIndex);
						var shifts = [];
						if(!Ext.isEmpty(shiftinfos))
							shifts =  Ext.decode(shiftinfos);
						
						var num = 0;
						var newShifts = [];
						if(shifts && shifts.length > 0 && "addShifts" == shiftGrid.shiftFlag)
							newShifts = shifts;
						
						var changeShift = {};	
						for(var i = 0; i < shiftGrid.selecedOrderdItem.length; i++){
							var classId = shiftGrid.selecedOrderdItem[i];
							var selectedShifts = document.getElementById(classId);
							var shiftInfo = {};
							var isExist = false;
							if(shifts && shifts.length > 0 && !shiftGrid.shiftFlag){
								for(var m= 0; m < shifts.length; m++) {
									var id = shifts[m].classId;
									if(classId == id){
										shiftInfo = shifts[m];
										isExist = true;
										num++;
										break;
									}
									
								}
							}
							
							if(!isExist) {
								var shiftName = selectedShifts.getAttribute("title");
								var nameColor = selectedShifts.getAttribute("nameColor");
								shiftInfo.classId = classId;
								shiftInfo.className = shiftName;
								shiftInfo.classColor = nameColor;
								shiftInfo.comment = '';
								shiftInfo.commentColor = '';
							}
							
							newShifts.push(shiftInfo);
							changeShift = shiftInfo;
						}
						
						if("changeShift" == shiftGrid.shiftFlag) {
							shifts[shiftGrid.shiftIndex] = changeShift;
							newShifts = shifts;
						}
						
						if(num < 3){
							record.set(columnIndex, "");
							record.set(columnIndex, Ext.encode(newShifts));
							var map = new HashMap();
							map.put("type", "saveShiftInfos");
							map.put("date", columnIndex);
							map.put("record", JSON.stringify(record.getData()));
							Rpc({functionId:'KQ00021308',async:false,success:function (response) {
								var map = Ext.decode(response.responseText);
								if(map.succeed){
									var countDataMap = map.countDataMap;
									var statDataMap = countDataMap[record.get("guidkey")];
									for(var key in statDataMap)
										record.set(key, statDataMap[key]);
									
									grid.syncRowHeights();
								} else {
									Ext.showAlert(map.message);
									return false;
								}
							}},map);
						}
						
						if(shiftGrid.regionSelect)
							grid.getSelectionModel().selectCells([shiftGrid.cellIndex - shiftGrid.hiddenCount, shiftGrid.rowIndex], 
									[shiftGrid.cellIndex - shiftGrid.hiddenCount, shiftGrid.rowIndex], false);
						else
							grid.getSelectionModel().select({row: shiftGrid.rowIndex,column: shiftGrid.cellIndex - shiftGrid.lockedCount - shiftGrid.hiddenCount});
						
						var checkBox = Ext.getCmp("checkBoxId");
						if(checkBox.checked){
							shiftGrid.cellIndex++;
							columnIndex = grid.columns[shiftGrid.cellIndex].dataIndex;
							if(!shiftGrid.checkColumn(columnIndex)) {
								shiftGrid.rowIndex++;
								if(shiftGrid.rowIndex >= grid.getStore().getTotalCount()) {
									Ext.showAlert(kq.shift.finish);
									win.close();
					    			return false;
								}
								
								for(var i = 0; i < grid.columns.length; i++){
									var columnIndexTemp = grid.columns[i].dataIndex;
									if(columnIndexTemp.indexOf(".") > -1) {
										shiftGrid.cellIndex = i
										break;
									}
								}
							}
							
							if(shiftGrid.regionSelect)
								grid.getSelectionModel().selectCells([shiftGrid.cellIndex - shiftGrid.hiddenCount, shiftGrid.rowIndex], 
										[shiftGrid.cellIndex - shiftGrid.hiddenCount, shiftGrid.rowIndex], false);
							else
								grid.getSelectionModel().select({row: shiftGrid.rowIndex,column: shiftGrid.cellIndex - shiftGrid.lockedCount - shiftGrid.hiddenCount - 1});
							
						} else
							win.close();
					}
				},{
					text:kq.button.cancle,
					handler: function() {
						win.close();
					}
				}],
				buttonAlign:'center'
    		});
    		
    		win.show();
    	}
    },
    //更改班次的选中状态
    changeShiftState:function (obj){
    	if(0 == obj.getAttribute("checked")){
    		var selectedShifts = Ext.query(".selectedCls");
    		if(selectedShifts && (selectedShifts.length >= 3 
    				|| (shiftGrid.shiftCount + selectedShifts.length) >= 3)){
    			Ext.showAlert(kq.shift.maxSelected);
    			return false;
    		}
    		
    		obj.setAttribute("checked", 1);
    		obj.setAttribute("class", 'selectedCls');
    		shiftGrid.selecedOrderdItem.push(obj.id);
    	} else {
    		obj.setAttribute("checked", 0);
    		obj.setAttribute("class", 'unselectedCls');
    		for (var i = 0; i < shiftGrid.selecedOrderdItem.length; i++){
                var itemid = shiftGrid.selecedOrderdItem[i];
                if (obj.id == itemid){
                	shiftGrid.selecedOrderdItem.splice(i, 1);
                    break;
                }
            }
    	}
    	
    	if("changeShift" == shiftGrid.shiftFlag){
    		var selectId = obj.getAttribute("id");
    		var selectedShifts = Ext.query(".selectedCls");
    		for(var i = 0; i < selectedShifts.length; i++){
				var classId = selectedShifts[i].getAttribute("id");
				if(selectId != classId){
					selectedShifts[i].setAttribute("checked", 0);
					selectedShifts[i].setAttribute("class", 'unselectedCls');
				}
			}
    		
    		return;
    	}
    	
    	shiftGrid.refreshItemsOrder();
    },
    //设置班次编号
    setSchemeId: function (schemeId) {
    	shiftGrid.schemeId = schemeId;
    },
    beforedrop: function (node, data, overModel, dropPosition, dropHandlers){
    	var store = Ext.data.StoreManager.lookup("shiftManage_0001_store");
		shiftGrid.rowNumber = store.indexOf(data.records[0]) + 1;
		shiftGrid.modelRowNumber = store.indexOf(overModel) + 1;
    },
    //拖拽数据
    dropSort: function (node,data,overModel,dropPosition){
    	var map = new HashMap();
    	map.put("type", "saveDropSort");
    	map.put("schemeId", shiftGrid.schemeId);
    	map.put("guidKey", data.records[0].get("guidkey"));
    	map.put("modelGuidKey", overModel.get("guidkey"));
    	map.put("dropPosition", dropPosition);
    	Rpc({functionId:'KQ00021308',async:false,success:function (response) {
    		var map = Ext.decode(response.responseText);
    		if(map.succeed){
    			var store = Ext.data.StoreManager.lookup("shiftManage_0001_store");
    			if (shiftGrid.rowNumber > shiftGrid.modelRowNumber) {
    				if ("after" == dropPosition)
    					shiftGrid.modelRowNumber = shiftGrid.modelRowNumber + 1;

    				shiftGrid.changeRowNumber(store, shiftGrid.modelRowNumber, shiftGrid.rowNumber, 0);
    			} else {
    				if ("before" == dropPosition)
    					shiftGrid.modelRowNumber = shiftGrid.modelRowNumber -1;

    				shiftGrid.changeRowNumber(store, shiftGrid.rowNumber, shiftGrid.modelRowNumber, 1);
    			}
    			
    			store.commitChanges();
    		} else {
    			Ext.showAlert(map.message);
    			return false;
    		}
    	}},map);
    },
    //查询被调换人员的信息
    showPersonChange:function (guidkey) {
    	var map = new HashMap();
    	map.put("type", "searchPersonInfo");
    	map.put("groupId", shiftGrid.groupId);
    	map.put("schemeId", shiftGrid.schemeId);
    	map.put("guidKey", guidkey);
    	Rpc({functionId:'KQ00021308',async:false,success:shiftGrid.showChangeWin},map);
    },
    //展现调换人员窗口
    showChangeWin:function (response){
    	var map = Ext.decode(response.responseText);
		if(map.succeed){
			var win = Ext.getCmp("personChangeWin");
			if(win)
				win.close();
			
			var personInfo = map.personList[0];
			
			var personPanel = Ext.create('Ext.panel.Panel',{
				id:'personPanel',
				border:false,
				height:100,
				margin:'60 0 5 55',
				width:350,
				html:'<ul><li class="liCls" style="padding-left: 50px;"><div style="width:60px;margin:0 10px 0 10px;">'
					+ '<img id="oldImg" style="border-radius:30px" width=60 height=60 src="' + personInfo.photoPath + '"/>'
					+ '</div><div id="oldName" style="width:80px;text-align:center;margin-top:5px;">' + personInfo.a0101 + '</div>'
					+ '<input type="hidden" id="oldHidden" value="' + personInfo.guidkey + '">'
					+ '</li><li li class="liCls">'
					+ '<img style="margin:15px 20px 0px 20px;" width=60 height=30 src="../../../../module/kq/images/shift_change_big.png"/></li>'
					+ '<li class="liCls"><div style="width:60px;margin:0 10px 0 10px;">'
					+ '<img id="newImg" style="border-radius:30px" width=60 height=60 src="/images/photo_square.jpg"/>'
					+ '</div><div id="newName" style="width:80px;text-align:center;margin-top:5px;"></div>'
					+ '<input type="hidden" id="newHidden">'
					+ '</li></ul>'
			});
			
			var oldField = Ext.create('Ext.form.field.Text', {
				id: 'oldText',
				labelWidth:30,
				margin:'0 10 0 10',
				emptyText:kq.shift.emptyText,
				listeners: {
					change: function(oldField, newValue, oldValue){
						if(!newValue)
							return false;
						
						shiftGrid.flag = "old";
						var map = new HashMap();
				    	map.put("type", "searchPersonInfo");
				    	map.put("groupId", shiftGrid.groupId);
				    	map.put("schemeId", shiftGrid.schemeId);
				    	map.put("filterValue", newValue);
				    	Rpc({functionId:'KQ00021308',async:false,success:shiftGrid.showPerson},map);
					}
				}
			});
			
			var newField = Ext.create('Ext.form.field.Text', {
				id: 'newText',
				labelWidth:30,
				margin:'0 10 0 10',
				emptyText:kq.shift.emptyText,
				listeners: {
					change:function(oldField, newValue, oldValue){
						if(!newValue)
							return false;
						
						shiftGrid.flag = "new";
						var map = new HashMap();
				    	map.put("type", "searchPersonInfo");
				    	map.put("groupId", shiftGrid.groupId);
				    	map.put("schemeId", shiftGrid.schemeId);
				    	map.put("filterValue", newValue);
				    	Rpc({functionId:'KQ00021308',async:false,success:shiftGrid.showPerson},map);
						
					}
				}
			});
			
			var fieldPanel = Ext.create('Ext.panel.Panel',{
				id:'fieldPanel',
				border:false,
				height:22,
				width:350,
				margin:'0 0 0 65',
				layout: 'hbox',
				items:[oldField, newField]
			});
			
			var divPanel = Ext.create('Ext.panel.Panel',{
				id:'divPanel',
				border: false,
				height: 100,
				width: '150px',
				margin:'0 0 0 75',
				hidden: true,
				html:'<div id="personDiv" style="border: 1px solid #c5c5c5;height:90px;'
					+ 'width:150px;overflow-y:auto;"></div>'
			});
			
			win = Ext.create('Ext.window.Window', {
				id: 'personChangeWin',
				title:kq.shift.shiftChange,
				width: 490,
				height: 350,
				modal:true,
				resizable: false,
				layout: 'vbox',
				items: [personPanel, fieldPanel, divPanel],
				buttons:[{
					text:kq.button.ok,
					handler: shiftGrid.savePersonChange
				},{
					text:kq.button.cancle,
					handler: function() {
						win.close();
					}
				}],
				buttonAlign:'center'
			});
			
			win.show();
			
			Ext.getBody().addListener('click', function(evt, el) {
				var divPanel = Ext.getCmp("divPanel");
				if (divPanel && !divPanel.isHidden() && shiftGrid.flag && "personDiv" != el.id 
						&& (shiftGrid.flag + "Text") != el.id){
					divPanel.setHidden(true);
					Ext.getCmp(shiftGrid.flag + "Text").setValue("");
				}
			});
		} else {
			Ext.showAlert(map.message);
			return false;
		}
    },
    //查询调换人员
    showPerson:function(response){
    	var map = Ext.decode(response.responseText);
		if(map.succeed){
			Ext.getCmp("divPanel").setHidden(false);
			var personDiv = document.getElementById("personDiv");
			personDiv.innerHTML = "";
			if("new" == shiftGrid.flag)
				Ext.getCmp("divPanel").setMargin("0 0 0 245");
			else
				Ext.getCmp("divPanel").setMargin("0 0 0 75");
			
			var oldGuidKey = document.getElementById("oldHidden").value;
	    	var newGuidKey = document.getElementById("newHidden").value;
			var personList = map.personList;
			for(var i = 0; i < personList.length; i++){
				var person = personList[i];
				if("new" == shiftGrid.flag && person.guidkey == oldGuidKey)
					continue;
				else if("old" == shiftGrid.flag && person.guidkey == newGuidKey)
					continue;
				
				var div = document.createElement("div");
				div.setAttribute("id", i);
				div.setAttribute("personKey", person.guidkey);
				div.setAttribute("photoPath", person.photoPath);
				div.innerHTML = person.a0101;
				var a = document.createElement("a");
				a.setAttribute("onclick", "shiftGrid.selectPerson('" + i +"')");
				a.setAttribute("href", "###");
				a.appendChild(div);
				personDiv.appendChild(a);
			}
		} else {
			Ext.showAlert(map.message);
			return false;
		}
    },
    //选择调换的人员
    selectPerson: function(index){
    	var div = document.getElementById(index);
    	var img = document.getElementById(shiftGrid.flag + "Img");
    	var psersonName = document.getElementById(shiftGrid.flag + "Name");
    	var hidden = document.getElementById(shiftGrid.flag + "Hidden");
    	img.src = div.getAttribute("photoPath");
    	hidden.value = div.getAttribute("personKey");
    	psersonName.innerHTML = div.innerHTML;
    	Ext.getCmp("divPanel").setHidden(true);
    	Ext.getCmp(shiftGrid.flag + "Text").setValue("");
    },
    //保存人员调换信息
    savePersonChange:function (){
    	var oldGuidKey = document.getElementById("oldHidden").value;
    	var newGuidKey = document.getElementById("newHidden").value;
    	var map = new HashMap();
    	map.put("type", "savePersonChange");
    	map.put("oldGuidKey", oldGuidKey);
    	map.put("newGuidKey", newGuidKey);
    	map.put("year",shiftGrid.year);
    	map.put("month",shiftGrid.month);
    	map.put("weekIndex",shiftGrid.weekIndex);
    	map.put("schemeId",shiftGrid.schemeId);
    	Rpc({functionId:'KQ00021308',async:false,success:shiftGrid.changeSuccess},map);
    },
    
    changeSuccess: function(response){
    	var map = Ext.decode(response.responseText);
		if(map.succeed) {
			Ext.showAlert(kq.shift.changeSuccess);
			Ext.getCmp("personChangeWin").close();
			shiftGrid.remove(Ext.getCmp("shiftManage_0001_grid"));
	    	shiftGrid.showPanel();
		} else {
			Ext.showAlert(map.message);
			return false;
		}
    },
    reloadGrid: function (){
    	shiftGrid.remove(Ext.getCmp("shiftManage_0001_grid"));
    	shiftGrid.showPanel();
    },
    //设置是否开启区域选择
    setRegionSelect:function (selectFlag) {
    	shiftGrid.regionSelect = selectFlag;
    },
    //设置年
    setYear: function (year) {
    	shiftGrid.year = year;
    },
    //设置月
    setMonth: function (month) {
    	shiftGrid.month = month;
    },
    //设置第几周
    setWeekIndex: function (weekIndex) {
    	shiftGrid.weekIndex = weekIndex;
    },
    //设置表格的列头
    setColumns: function (columns) {
    	shiftGrid.columns = columns;
    },
    //设置store中查询的数据列
    setFields: function (fields) {
    	shiftGrid.fields = fields;
    },
    //设置每页数据显示的条数
    setPageSize: function (pageSize) {
    	shiftGrid.pageSize = pageSize;
    },
    //设置每页数据显示的条数
    setRemoveFilter: function (removeFilter) {
    	shiftGrid.removeFilter = removeFilter;
    },
    //拖拽数据行号自动调整
    changeRowNumber: function(store, formNumber, toNumber, flag){
    	if(1 == flag)
    		toNumber = toNumber -1;
    	else
    		formNumber = formNumber + 1;
    	
    	for(var i = formNumber; i <= toNumber; i++){
    		var data = store.getAt(i - 1);
    		var rowNumber = data.get("rownumber");
    		if(0 == flag)
    			rowNumber = rowNumber + 1;
        	else 
        		rowNumber = rowNumber -1;
    		
    		data.set("rownumber", rowNumber);
    		data.commit();
    	}
    },
    //删除选择的单元格的数据
    deleteCellData : function () {
        var grid = Ext.getCmp("shiftManage_0001_grid");
        if(!grid)
            return;
        
        var flag = false;
        var deleteShifts = [];
        var datas = {};
        shiftGrid.editFlag = false;
        var selectCells = grid.getSelectionModel().getSelected();
        var store = Ext.data.StoreManager.lookup("shiftManage_0001_store");
        selectCells.eachCell(function (cellContext) {
            var column = cellContext.column;
            var record = cellContext.record;
            var dataIndex = column.dataIndex;
            if (column.ignoreExport || !shiftGrid.checkColumn(dataIndex))
                return false;
            
            flag = true;
            var guidkey = record.get("guidkey");
            var deleteData = {}
            if(datas[guidkey])
            	deleteData = datas[guidkey];
            
            deleteData[dataIndex] = "";
            datas[guidkey] = deleteData;
        });
        
        var storeDatas = store.getData().items;
        for(var key in datas){
        	var shiftData = datas[key];
        	for(var i = 0; i < storeDatas.length; i++){
        		var data = storeDatas[i];
        		if(key == data.get("guidkey")){
        			data.set(shiftData);
        			break;
        		}
        	}
        	
        	shiftData["guidkey"] = key;
        	deleteShifts.push(shiftData);
        }
        
        var map = new HashMap();
        map.put("type", "saveCopyShiftInfos");
        map.put("copyShifts", Ext.encode(deleteShifts));
        map.put("groupId", shiftGrid.groupId);
        map.put("schemeId", shiftGrid.schemeId);
        Rpc({functionId:'KQ00021308',async:false,success:function (response) {
     	   var map = Ext.decode(response.responseText);
     	   if(map.succeed){
     		   var countDataMap = map.countDataMap;
     		   for (var i = 0; i < storeDatas.length; i++) {
     			   var statDataMap = countDataMap[storeDatas[i].get("guidkey")];
     			   for(var key in statDataMap)
     				  storeDatas[i].set(key, statDataMap[key]);
     			   
     		   }
     		   
     		   Ext.data.StoreManager.lookup("shiftManage_0001_store").commitChanges();
     		   Ext.getCmp("shiftManage_0001_grid").syncRowHeights();
     	   } else {
     		   Ext.showAlert(map.message);
     		   return false;
     	   }
     	   
     	   shiftGrid.editFlag = true;
        }},map);
        
        if (flag){
            grid.getSelectionModel().deselectAll();
        }
    },
    //开启区域操作后，右键点击可设置班次的单元格的菜单
    rightOperationMenu: function (view, td, cellIndex, record, tr, rowIndex, e){
    	if(e){
    		e.preventDefault();
    		e.stopEvent();
    	}
    	//除了日期列外右键不能弹出菜单
    	var column = view.grid.columnManager.columns[cellIndex];
    	var dataIndex = column.dataIndex;
		if(!shiftGrid.checkColumn(dataIndex))
        	return false;
		
		var selectedFlag = false;
		var grid = Ext.getCmp("shiftManage_0001_grid");
		var selectCells = grid.getSelectionModel().getSelected();
		var valuesFlag = false;
		if(selectCells) {
			//判断右键点击的单元格是否是选中的单元格
			selectCells.eachCell(function (cellContext) {
				var record = cellContext.record;
				var rowNum = grid.getStore().indexOf(record);
				if(cellContext.column.dataIndex == dataIndex && shiftGrid.checkColumn(dataIndex)
						&& rowNum == rowIndex) {
					selectedFlag = true;
					return false;
				}
			});
		}
		//如果右键点击的单元格不是选中的单元格则选中右键点击的单元格
		if(!selectedFlag) {
			var columns = grid.columns;
			shiftGrid.lockedCount = 0;
			shiftGrid.hiddenCount = 0;
			
			var index = 0;
			for(index = 0; index < columns.length; index++){
				if(columns[index].isLocked())
					shiftGrid.lockedCount++;
				
				if(columns[index].isHidden())
					shiftGrid.hiddenCount++;
				
				if(columns[index].dataIndex == dataIndex)
					break;
			}
			
			grid.getSelectionModel().selectCells([index - shiftGrid.hiddenCount, rowIndex], [index - shiftGrid.hiddenCount, rowIndex], false);
		}
		//如果单元格中没有班次，则弹出的菜单中不能复制、剪切、删除
		var disabled = true;
		var selectCells = grid.getSelectionModel().getSelected();
		selectCells.eachCell(function (cellContext) {
			var columnIndex = cellContext.column.dataIndex;
			var cellValue = cellContext.record.get(columnIndex);
			if(cellValue) {
				disabled = false;
				return false;
			}
		});
		
		var menu = Ext.getCmp("operationMenuId");
		if(menu)
			menu.close();
		
		var pasteDisabled = true;
		if(shiftGrid.cellData)
			pasteDisabled = false;
		
		//展现菜单
		menu = Ext.create("Ext.menu.Menu",{
			id:"operationMenuId",
			items:[{
				id:"copyMenuId",
				text: kq.datamx.msg.copy+"&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Ctrl+C",
                icon:'../../../../module/kq/images/shift_copy.png',
                disabled: disabled,
				handler:function(){
					shiftGrid.getCellData('raw',false);
				}
			},{
				id:"cutMenuId",
				text: kq.datamx.msg.cut+"&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Ctrl+X",
                icon:'../../../../module/kq/images/shift_cut.png',
                disabled: disabled,
				handler:function(){
					shiftGrid.getCellData('raw',true);
				}
			},{
				id:"pasteMenuId",
				text: kq.datamx.msg.paste+"&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Ctrl+V",
				icon:'../../../../module/kq/images/shift_paste.png',
				disabled: pasteDisabled,
				handler:function(){
					shiftGrid.putCellData();
				}
			},{
				id:"deleteMenuId",
				text: kq.datamx.msg.deleteFlag+"&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Delete",
                icon:'../../../../module/kq/images/shift_clear.png',
                disabled: disabled,
				handler:function(){
					shiftGrid.deleteCellData();
				}
			}]
		});
		
		menu.showAt(e.getXY());
    },
    //删除表格中的右键点击事件
    rightContextmenu: function (view, td, cellIndex, record, tr, rowIndex, e){
    	if(e){
    		e.preventDefault();
    		e.stopEvent();
    	}
    	//除了日期列外右键不能弹出菜单
		var column = view.grid.columnManager.columns[cellIndex];
		if(!shiftGrid.checkColumn(column.dataIndex))
        	return false;
		
    },
    //编辑单元格中的班次
    editShifts: function (view, td, cellIndex, record, tr, rowIndex, e){
		//禁用浏览器的右键相应事件
    	if(e && !e.shiftFlag){
    		e.preventDefault();
    		e.stopEvent();
    	}
    	// 54667 之前增加发布控制时写错对象变量引起
    	//已发布不可编辑
    	if (shiftManage.pushScheme=="true") {
			return false;
		}
    	var column;
    	if(e && e.shiftFlag){
    		if(e.locked)
    			column = view.lockedGrid.columnManager.columns[cellIndex];
    		else
    			column = view.normalGrid.columnManager.columns[cellIndex];
    	} else
    		column = view.grid.columnManager.columns[cellIndex];
    	
        if(!shiftGrid.checkColumn(column.dataIndex))
        	return false;
        
        if(e && e.shiftFlag) {
        	shiftGrid.shiftFlag = e.shiftFlag;
        	shiftGrid.shiftIndex = e.shiftIndex;
        } else {
        	shiftGrid.shiftFlag = "";
        	shiftGrid.shiftIndex = 0;
        }
        	
        var classIds = ",";
        var cellValue = record.get(column.dataIndex);
        if(cellValue) {
        	var shiftData = Ext.decode(cellValue);
        	for(var i = 0; i < shiftData.length; i++)
        		classIds += shiftData[i].classId + ",";
        	
        	if("addShifts" == shiftGrid.shiftFlag)
        		shiftGrid.shiftCount = shiftData.length;
        	else
        		shiftGrid.shiftCount = 0;
        } else
    		shiftGrid.shiftCount = 0;
        
        var columns = Ext.getCmp("shiftManage_0001_grid").columns;
        var index = 0;
        shiftGrid.lockedCount = 0;
        shiftGrid.hiddenCount = 0;
        
		for(index = 0; index < columns.length; index++){
			if(columns[index].isLocked())
				shiftGrid.lockedCount++;
			
			if(columns[index].isHidden())
				shiftGrid.hiddenCount++;
			
			var dataIndex = columns[index].dataIndex;
			if(column.dataIndex == dataIndex)
				break;
		}
		
        shiftGrid.rowIndex = rowIndex;
		shiftGrid.cellIndex = index;
		cellIndex = cellIndex - shiftGrid.hiddenCount;
		if(shiftGrid.regionSelect)
			view.grid.getSelectionModel().selectCells([index - shiftGrid.hiddenCount, rowIndex], [index - shiftGrid.hiddenCount, rowIndex], false);
		else{
			if(e && e.shiftFlag){
	    		if(e.locked)
	    			column = view.lockedGrid.getSelectionModel().select({row:rowIndex,column:cellIndex});
	    		else
	    			column = view.normalGrid.getSelectionModel().select({row:rowIndex,column:cellIndex});
	    	} else
	    		view.grid.getSelectionModel().select({row:rowIndex,column:cellIndex});
		}
		
        var map = new HashMap();
        map.put("type", "shifts");
        map.put("classIds", classIds);
        map.put("shiftFlag", shiftGrid.shiftFlag);
        map.put("groupId", shiftGrid.groupId);
	    Rpc({functionId:'KQ00021308',success:shiftGrid.showShiftWin},map);
    },
    // 复制或剪切数据:erase=true：剪切数据；=false：复制数据
    getCellData: function(format,erase){
    	if("-1" == shiftGrid.schemeId)
			return false;
	 
		var selModel = Ext.getCmp("shiftManage_0001_grid").getSelectionModel(),
        ret = [],columns = ",",
        isRaw = format === 'raw',
        data, dataIndex, lastRecord, column, record, row;
		
		selModel.getSelected().eachCell(function (cellContext) {
			column = cellContext.column,
			record = cellContext.record;

			dataIndex = column.dataIndex;
			columns += dataIndex + ","
            if(!shiftGrid.checkColumn(dataIndex) || !shiftGrid.checkselectValid(columns))
            	return false;
 
            if (lastRecord !== record) {
                lastRecord = record;
                ret.push(row = []);
            }
 
            data = record.get(dataIndex);
            row.push(data);
            if (erase && dataIndex)
                record.set(dataIndex, "");
        
		});
		
		shiftGrid.cellData = Ext.encode(ret);
		return shiftGrid.cellData;
    },
    /*
	 * 粘帖复制或剪切的数据
	 */
    putCellData: function () {
    	if(!shiftGrid.cellData || "-1" == shiftGrid.schemeId)
			 return false;
		 
		 var values = Ext.decode(shiftGrid.cellData), row,
           recCount = values.length,
           colCount = recCount ? values[0].length : 0,
           sourceRowIdx, sourceColIdx,
           view = Ext.getCmp("shiftManage_0001_grid").getView(),
           maxRowIdx = view.dataSource.getCount() - 1,
           maxColIdx = view.getVisibleColumnManager().getColumns().length - 1,
           navModel = view.getNavigationModel(),
           destination, dataIndex, destinationStartColumn;
           
       if (!destination) {
           view.getSelectionModel().getSelected().eachCell(function(c){
               destination = c;
               return false;
           });
       }
       
       shiftGrid.editFlag = false;
       if (destination)
           destination = new Ext.grid.CellContext(view).setPosition(destination.record, destination.column);
       else
           destination = new Ext.grid.CellContext(view).setPosition(0, 0);
       
       destinationStartColumn = destination.colIdx;
       var store = destination.view.dataSource;
       var copyShifts = [];
       for (sourceRowIdx = 0; sourceRowIdx < recCount; sourceRowIdx++) {
       	var dataObject = {};
       	var clearObject = {};
           row = values[sourceRowIdx];
           for (sourceColIdx = 0; sourceColIdx < colCount; sourceColIdx++) {
               dataIndex = destination.column.dataIndex;
               if(!shiftGrid.checkColumn(dataIndex))
            	   break;
               
               var cellValue = row[sourceColIdx];
               if(Ext.isEmpty(cellValue))
            	   cellValue = "[]";
               
               eval('var obj=' + cellValue);
               if (!obj || "object" != typeof(obj))
            	   break;
               
               if (dataIndex.indexOf(".") > -1 && Ext.isEmpty(row[sourceColIdx])){
            	   dataObject[dataIndex] = "";
            	   destination.setColumn(destination.colIdx + 1);
            	   continue;
               }
               
               if (dataIndex){
            	   dataObject[dataIndex] = row[sourceColIdx];
            	   clearObject[dataIndex] = "";
               }
               
               if (destination.colIdx === maxColIdx)
                   break;
               
               destination.setColumn(destination.colIdx + 1);
           }
           
           destination.record.set(clearObject);
           destination.record.set(dataObject);
           dataObject["guidkey"] = destination.record.get("guidkey");
           copyShifts.push(dataObject);
           if (destination.rowIdx === maxRowIdx)
               break;
           
           destination.setPosition(destination.rowIdx + 1, destinationStartColumn);
       }
       
       var map = new HashMap();
       map.put("type", "saveCopyShiftInfos");
       map.put("copyShifts", Ext.encode(copyShifts));
       map.put("groupId", shiftGrid.groupId);
       map.put("schemeId", shiftGrid.schemeId);
       Rpc({functionId:'KQ00021308',async:false,success:function (response) {
    	   var map = Ext.decode(response.responseText);
    	   if(map.succeed){
    		   var countDataMap = map.countDataMap;
    		   var datas = Ext.data.StoreManager.lookup("shiftManage_0001_store").getData().items;
    		   
    		   for (var i = 0; i < datas.length; i++) {
    			   var statDataMap = countDataMap[datas[i].get("guidkey")];
    			   for(var key in statDataMap)
    				   datas[i].set(key, statDataMap[key]);
    			   
    		   }
    		   
    		   Ext.data.StoreManager.lookup("shiftManage_0001_store").commitChanges();
    		   Ext.getCmp("shiftManage_0001_grid").syncRowHeights();
    	   } else {
    		   Ext.showAlert(map.message);
    		   return false;
    	   }
    	   
    	   shiftGrid.editFlag = true;
       }},map);
       // 取消掉选中状态
       view.getSelectionModel().deselectAll();
    },
    //控制调换人员图标是否显示
    changeIcon: function(rowNumnber,flag){
    	var icon = document.getElementById("img_" + rowNumnber);
    	if(1 == flag)
    		icon.style.display = "block";
    	else
    		icon.style.display = "none";
    },
    /**
     * 显示班次或项目的序号
     */
    refreshItemsOrder:function(){
       var selectedArr = Ext.query('p[id$="_xuhao"]');
        for (var i=0; i < selectedArr.length; i++){
            var temp = selectedArr[i];
            if (temp){
                temp.style.display='none';
            }
        }
        var classIndex = 1;//序号
        for (var i = 0; i < shiftGrid.selecedOrderdItem.length; i++){
            var id = this.selecedOrderdItem[i];
            var p = Ext.getDom(id+"_xuhao");
            //为班次或项目添加序号
            if (p){
                p.style.display='inline-block';
                p.innerText = classIndex;
                classIndex++;
            }
        }
    },
    //校验0.0-999999999.9区间数字
    regexNumeric: function(v){
    	var reg=/^\d{1,9}(\.\d{1})?$/;
    	var s=v.toString();
    	if (s == null||trim(s).length==0) {
    		return true;
    	}
    	if(reg.test(s))
    		return true;
    	return false;
    }
});