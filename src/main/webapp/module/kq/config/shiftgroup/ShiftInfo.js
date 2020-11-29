Ext.define("ShiftURL.ShiftInfo", {
	extend:'Ext.container.Container',
	alias:'widget.ShiftInfo',
	constructor:function(){
		shiftInfo = this;
		shiftInfo.callParent(arguments);
		shiftInfo.dataType = shiftInfo.config.dataType ? shiftInfo.config.dataType : "shiftData";
		shiftInfo.regionSelect = shiftInfo.config.regionSelect ? true : false;
		shiftInfo.shiftPivFlag = shiftInfo.config.shiftPivFlag ? true : false;
		shiftInfo.initParam(shiftInfo.config.data, shiftInfo.config.dataIndex, shiftInfo.config.rowIdx, shiftInfo.config.colIdx);
	},
	//加载班次信息
	initParam:function(data, dataIndex, rowIdx, colIdx){
		shiftInfo.html = "";
		if(shiftInfo.regionSelect || !shiftInfo.shiftPivFlag)
			shiftInfo.html = "<div Style='height:30px;width:100%;'></div>";
			
		if(!data)
			return;
		
		var shifInfos = data.get(dataIndex);
		if(Ext.isEmpty(shifInfos))
			return;
		
		shiftInfo.html = "";
		var shifInfoData = Ext.decode(shifInfos);
		var state = "";
		if(shifInfoData && shifInfoData.length > 0)
			shiftInfo.html += "<table width='100%'>";
		
		for(var i = 0; i < shifInfoData.length; i++){
			var datas = shifInfoData[i];
			var className = datas.className;
			var classColor = datas.classColor;
			var comment = datas.comment;
			var commentColor = datas.commentColor;
			var schemeState = datas.state;
			if(schemeState){
				state = schemeState;
				shiftInfo.html += "<input type='hidden' id='hid_" + rowIdx + "_" + colIdx + "'" +
						" value='" + state + "'/>";
			}
			
			shiftInfo.html += "<tr><td id='div_" + rowIdx + "_" + colIdx + "_" + i + "' style='height:30px;width:100%;"
			 	+ "'";
			if("shiftData" == shiftInfo.dataType)
				shiftInfo.html += " oncontextmenu='shiftInfo.onRightClickMenu(\"" + dataIndex + "\"," + i + "," + rowIdx + "," + colIdx + ")'"
				 		+ " onmouseover='shiftInfo.shiftInfoButton(" + rowIdx + "," + colIdx + "," + i + ",1)'"
						+ " onmouseout='shiftInfo.shiftInfoButton(" + rowIdx + "," + colIdx + "," + i + ",0)'";
			
			shiftInfo.html += "><div id='" + rowIdx + "_" + colIdx + "_" + i + "' style='float:left;display:block;'>";
			shiftInfo.html += "<div class='shiftInfoCls' title='" + className + "' style='color:"+ classColor + "' >" + className + "</div>";
			if(comment){
				shiftInfo.html += "<span style='float:left;font-size:12px;'>(</span><div id='comment_" + rowIdx + "_" + colIdx + "_" + i + "'" +
						" class='shiftInfoCls' title='" + comment + "'";
				if(commentColor)
					shiftInfo.html += " style='color: " + commentColor + "'";
				
				shiftInfo.html += ">" + comment + "</div><span style='float:left;font-size:12px;'>)</span>";
			}
			
			shiftInfo.html += "</div>";
			if("shiftData" == shiftInfo.dataType && "04" != state && shiftInfo.shiftPivFlag) {
				var topIndex = 6 + i * 30;
				shiftInfo.html += "<div style='position: absolute;right: 8px;display: flex;align-items: center;'><img src='/module/kq/images/shift_delete1.png' id='delId_" + rowIdx + "_" + colIdx + "_" + i + "'"
					+ " style='display:none;float:right;cursor:pointer;' title='" + kq.label.del + "'"
					+ " align='absmiddle' onclick='shiftInfo.deleteShift(\"" + dataIndex + "\"," + i + "," + rowIdx + "," + colIdx + ")'/></div>";
			}
			
			shiftInfo.html += "</td></tr>";
		}
		
		if(shifInfoData && shifInfoData.length > 0)
			shiftInfo.html += "</table>";
	},
	// 班次右键菜单
	onRightClickMenu: function(dataIndex, shiftIndex, rowIdx, colIdx){
		if(shiftInfo.regionSelect || !shiftInfo.shiftPivFlag)
			return false;
		
		var e = window.event || arguments.callee.caller.arguments[0];
		var div = document.getElementById("div_" + rowIdx + "_" + colIdx + "_" + shiftIndex);
		var record = Ext.getCmp("shiftManage_0001_grid").getStore().getAt(rowIdx);
		var cellValue = record.get(dataIndex);
		var disabled = false;
		if(cellValue) {
        	var shiftData = Ext.decode(cellValue);
        	if(shiftData.length >= 3)
        		disabled = true;
        	
        }
		
		var changeDisabled = false;
		var hid = document.getElementById("hid_" + rowIdx + "_" + colIdx)
		if(hid && "04" == hid.value)
			changeDisabled = true;
		
		if(changeDisabled)
			disabled = true;
		//展现菜单
		var menu = Ext.create("Ext.menu.Menu",{
			x:e.pageX || e.clientX,
			y:e.pageY || e.clientY,
			items:[{
				text:kq.shift.changeShift,
				icon:'../../../../module/kq/images/shift_change.png',
				disabled:changeDisabled,
				handler:function(){
					shiftInfo.editShifts(dataIndex, shiftIndex, rowIdx, colIdx, "changeShift");
				}
			},{
				text:kq.shift.addShifts,
				disabled:disabled,
				icon:'../../../../module/kq/images/shift_add.png',
				handler:function(){
					shiftInfo.editShifts(dataIndex, shiftIndex, rowIdx, colIdx, "addShifts");
				}
			},{
				text:kq.shift.editRemark,
				icon:'../../../../module/kq/images/shift_editRemark.png',
				handler:function(){
					shiftInfo.editShiftInfo(dataIndex, shiftIndex, rowIdx, colIdx);
				}
			}]
		});
		
		menu.show();
	},
	/**
	 * 获取html
	 * @returns
	 */
	getHtml:function(){
		var shiftInfo = this;
		return shiftInfo.html; 
	},
	//班次后的按钮显示或隐藏
	shiftInfoButton: function (rowIdx, colIdx, id, flag) {
		var delButton = document.getElementById("delId_" + rowIdx + "_" + colIdx + "_" + id);
		var div = document.getElementById("div_" + rowIdx + "_" + colIdx + "_" + id);
		if(1 == flag) {
			if(delButton)
				delButton.style.display = "block";
			
			div.style.background = "#d6c988";
		} else {
			if(delButton)
				delButton.style.display = "none";
			
			div.style.background = "none";
		}
		
	},
	//编辑班次备注和备注颜色
	editShiftInfo: function (dataIndex, shiftIndex, rowIdx, colIdx) {
		var storeData = Ext.data.StoreManager.lookup("shiftManage_0001_store").getAt(rowIdx);
		var data = Ext.decode(storeData.get(dataIndex))[shiftIndex];
		var win = Ext.getCmp("shiftWinId");
		if(win)
			win.close();
		
		var field = Ext.create('Ext.form.field.Text', {
			id: 'shiftRemark',
	        fieldLabel: kq.shift.remark,
	        labelWidth:30,
	        maxLength: 20,
	        value: data.comment,
	        listeners:{
	        	change: function(field) {
	        		var newValue = field.getValue();
	        		if(newValue.replace(/[^\x00-\xff]/g,"**").length > 20){
						var tipMsg = kq.shift.remarkMax.replace('{0}', "10").replace('{1}', "20");
						Ext.showAlert(kq.shift.remark + tipMsg);
						field.setValue("");
					}
	        	} 
	        }
		});
		
		var colorValue = data.commentColor;
		if(!colorValue)
			colorValue = "#000000";
		
		var color = Ext.create('Ext.container.Container', {
			height:35,
			width:'100%',
			layout:'hbox',
			margin: '0 0 0 105',
			items:[{
				xtype:'label',
				text:kq.shifts.color,
				width:35,
			},{
				id:'colorValue',
				xtype:'hiddenfield',
				name:'color',
				value:colorValue
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
			},{
				xtype:'container',
				id:'colorDiv',
				style:{
					backgroundColor:colorValue,
					cursor:'pointer',
					borderStyle:'solid', 
					borderColor:'#B5B8C8', 
					borderWidth:'1px'
				},
				width:150,
				height:20,
				padding:'1 1 1 1',
				margin:'2 0 0 0',
				border:true,
				listeners: {
					click: {
						element: 'el', 
						fn: function(a, o){
							var colorPicker = Ext.getCmp("colorpicker");
							colorPicker.setHidden(!colorPicker.hidden);
						}
					},
					afterrender:function(){
						Ext.get("shiftWinId").on("click", function(e) {
							e = e || window.event;
							var target = e.target || e.srcElement;
							var colorDiv = Ext.getCmp("colorDiv");
							if(target.id.indexOf("colorDiv")<0){
								var colorPicker = Ext.getCmp("colorpicker");
								if(!colorPicker.hidden)
									colorPicker.setHidden(true);
							}
						});
					}
				}
			}]
		});
		
		win = Ext.create('Ext.window.Window', {
			id: 'shiftWinId',
			title:dataIndex + " " + data.className,
			width: 300,
			height: 200,
			modal:true,
			resizable: false,
			layout: {
		        align: 'middle',
		        pack: 'center',
		        type: 'vbox'
			},
			items: [field,color],
			buttons:[{
				text:kq.button.ok,
				handler: function (){
					var remark = Ext.getCmp("shiftRemark").value;
					if(remark.replace(/[^\x00-\xff]/g,"**").length > 20){
						var tipMsg = kq.shift.remarkMax.replace('{0}', "10").replace('{1}', "20");
						Ext.showAlert(kq.shift.remark + tipMsg);
						return false;
					}
					
					var colorValue = Ext.getCmp("colorValue").getValue();
					data.comment=remark;
					data.commentColor=colorValue;
					var grid = Ext.getCmp("shiftManage_0001_grid");
					var record = grid.getStore().getAt(rowIdx);
					var shiftInfoDatas = Ext.decode(record.get(dataIndex));
					shiftInfoDatas[shiftIndex] = data;
					record.set(dataIndex, Ext.encode(shiftInfoDatas));
					var map = new HashMap();
					map.put("type", "saveShiftInfos");
					map.put("date", dataIndex);
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
		
	},
	//删除班次
	deleteShift: function (dataIndex, shiftIndex, rowIdx, colIdx) {
		Ext.Msg.confirm(kq.shift.shiftTip, kq.shift.deleteInfo, function(button, text){  
			if(button != "yes")
				return;
			
			var grid = Ext.getCmp("shiftManage_0001_grid");
			var record = grid.getStore().getAt(rowIdx);
			var shiftInfoDatas = Ext.decode(record.get(dataIndex));
			shiftInfoDatas.remove(shiftInfoDatas[shiftIndex]);
			record.set(dataIndex, "");
			record.set(dataIndex, Ext.encode(shiftInfoDatas));
			var map = new HashMap();
			map.put("type", "saveShiftInfos");
			map.put("date", dataIndex);
			map.put("record", JSON.stringify(record.getData()));
			Rpc({functionId:'KQ00021308',async:false,success:function (response) {
				var map = Ext.decode(response.responseText);
				if(map.succeed){
					var countDataMap = map.countDataMap;
					var statDataMap = countDataMap[record.get("guidkey")];
					for(var key in statDataMap)
						record.set(key, statDataMap[key]);
					
					record.commit();
					grid.syncRowHeights();
				} else {
					Ext.showAlert(map.message);
					return false;
				}
			}},map);
		});
	},
	//新增或更换班次
	editShifts: function(dataIndex, shiftIndex, rowIdx, colIdx, shiftFlag){
		var grid = Ext.getCmp("shiftManage_0001_grid");
		var view = grid.getView();
		var record = grid.getStore().getAt(rowIdx);
		var columns = grid.columns;
		var lockedFlag = false;
		for(var index = 0; index < columns.length; index++){
			if(columns[index].dataIndex != dataIndex)
				continue;
			
			lockedFlag = columns[index].isLocked();
			break;
		}
		
		var param = {};
		if(shiftFlag) {
			//shiftFlag ="changeShift"：更换；="addShifts":新增
			param.shiftFlag = shiftFlag;
			//邮件点击的单元格所在的列是否是锁列状态
			param.locked = lockedFlag;
			//右键点击的是一天的第几个班次
			param.shiftIndex = shiftIndex;
		} else
			param = null;
		
		Ext.getCmp("shiftManage_0001_shiftGrid").editShifts(view, "", colIdx, record, "", rowIdx, param);
	}
});