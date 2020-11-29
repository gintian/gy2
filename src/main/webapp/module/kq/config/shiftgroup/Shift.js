Ext.define('ShiftURL.Shift',{
	requires:['EHR.attendanceMonth.AttendanceMonthComp','ShiftURL.ShiftGrid'],
	constructor:function(config) {
		shiftManage = this;
		flag = false;
		shiftManage.prefix="shiftManage_0001";
		shiftManage.year='';
		shiftManage.month='';
		shiftManage.weekIndex = '1';
		shiftManage.groupId = config.groupId;
		shiftManage.orgId = config.orgId;
		shiftManage.schemeId = '';
		shiftManage.selectArray = [];
		shiftManage.addArray = [];
		shiftManage.removeArray = [];
		shiftManage.dateJson;
		shiftManage.lastWeekScope;
		shiftManage.weekScope;
		shiftManage.operation = config.operation;
		shiftManage.pushScheme = config.pushScheme;
		shiftManage.renderPerNumid = config.renderPerNumid;
		if("groupPerson" == config.operation){
			this.getShiftEmpTableConfig("0");
		} else
			this.init();
	},
	
	init: function () {
		Ext.Loader.loadScript({url:'/components/extWidget/proxy/TransactionProxy.js'});
		Ext.Loader.loadScript({url:'/ext/ext6/ext-additional.js'});
		var map = new HashMap();
		map.put('groupId', shiftManage.groupId);
		map.put('firstFlag', "1");
	    Rpc({functionId:'KQ00021303',success:shiftManage.showPanel},map);
	},
	//展现页面布局
	showPanel: function (response) {
		var map	 = Ext.decode(response.responseText);
		if(map.succeed){
			var columns = Ext.decode(getDecodeStr(map.column));
			var columnJson = Ext.decode(getDecodeStr(map.columnJson));
			shiftManage.schemeId = map.schemeId;
			shiftManage.year = map.year;
			shiftManage.month = map.month;
			shiftManage.weekIndex = map.weekIndex;
			shiftManage.weekList = map.weekList;
			shiftManage.lastWeekScope = map.lastWeekScope;
			shiftManage.weekScope = map.weekScope;
			shiftManage.dateJson = Ext.decode(map.dateJson);
			shiftManage.otherParam = map.otherParam;
			var comboBoxStore = Ext.create('Ext.data.Store', {
	            id: 'schemeListStore',
	            fields: ['id', 'name'],
	            autoLoad : true,
	            data: map.weekList
	        });
			
			var comboBox = Ext.create('Ext.form.field.ComboBox', {
				id:'comboBoxId',
	            fieldLabel: '',
	            labelSeparator: '',
	            store: comboBoxStore,
	            width: 210,
	            forceSelection: true,
	            valueField: 'id',
	            displayField: 'name',
	            shadow: false,
	            editable: false,
	            allowBlank: false,
	            value:shiftManage.weekIndex,
	            cls: 'comboxStyle',
	            listeners: {
	                select:function (combo, record) {
	                	shiftManage.weekIndex = record.data.id;
	                    shiftManage.reloadTable();
	                }
	            }
	        });
			
			var tbar1 = new Ext.Toolbar({
				id:'toolbar1',
				height:25,
				padding:'0 0 0 5',
				margin:'0 0 2 0',
				border:false,
				items:[{
					xtype:"label",
					text:kq.label.searchScheme
				},{
					xtype:"label",
					width:75,
					html:"<a href='javascript:shiftManage.selectMonth()' id='date' >" 
						+ shiftManage.year + kq.shift.year + shiftManage.month + kq.shift.month + "</a>"
						+ "<img src='/workplan/image/jiantou.png' id='dateImg' style='cursor:pointer' onclick='shiftManage.selectMonth();'/> "
				},"-", comboBox]
			}); 
			
			shiftManage.pushScheme = map.pushScheme;
			var text = kq.label.publish;
			var disabled = false;
			if("true" == shiftManage.pushScheme) {
				disabled = true;
				text = kq.group.edit;
			}
			
			var buttons = Ext.decode(map.buttonJson);
			var tbar2 = new Ext.Toolbar({
				id:'toolbar2',
				height:25,
				padding:'0px 10px 10px 5px',
				border:false,
				items:buttons
			}); 
			
			if(Ext.getCmp("publishButtonId"))
				Ext.getCmp("publishButtonId").setText(text);
			
			if(Ext.getCmp("personId"))
				Ext.getCmp("personId").setDisabled(disabled);
			
			if(Ext.getCmp("shiftId"))
				Ext.getCmp("shiftId").setDisabled(disabled);
			
			if(Ext.getCmp("checkboxId"))
				Ext.getCmp("checkboxId").setDisabled(disabled);
			
			var titleStr = '<ul><li style="margin-top: -5px;float: left;">' + map.groupName + kq.shift.shiftManage+ "</li>";
			// 栏目设置权限控制
			if("1" == shiftManage.otherParam.scheme_priv){
				titleStr += "<li style='margin-top: -5px;float: right;'><img onclick='shiftManage.schemeSetting()'" 
						+ "src='/components/tableFactory/tableGrid-theme/images/Settings.png' style='cursor:pointer' title='" + kq.label.schemeSetDesc + "'/>"
					+ "</li>";
			}
			titleStr += "</ul>";
			var north = new Ext.Panel({
				xtype : 'panel',
				title: titleStr,
				id : 'north',
				height:93,
				items:[tbar1, tbar2],
				region : 'north',
				border : false			
			});
			
			var grid = Ext.create('ShiftURL.ShiftGrid', {
				columns: columnJson,
				fields: columns,
				year:shiftManage.year,
				month:shiftManage.month,
				weekIndex: shiftManage.weekIndex,
				groupId:shiftManage.groupId,
				pushScheme:true,
				pageSize:map.pageRows,
				schemeId:shiftManage.schemeId,
				removeFilter:"1"
			 });
			
			var center = new Ext.Panel( {
				xtype : 'panel',
				id : 'center',
				region : 'center',
				layout : 'fit',
				width: '100%',
				height: '100%',
				style:'background-color: #FFFFFF;',
				border : false,
				items:[grid]
			});
			
			new Ext.Viewport({
				id:"port",
				layout : "border",
				items:[north,center]
			});
			
			var fieldArray = Ext.decode(map.fieldArray);
			shiftManage.createSearchPanel(fieldArray);
		}
	},
	//选择年月
    selectMonth: function () {
    	var win = Ext.getCmp('win');
		if(win) {
			win.close();
		}
		
		var SetItemGloble = Ext.create("EHR.attendanceMonth.AttendanceMonthComp",{
			totalData:shiftManage.dateJson,
			border: false,
			currentYear: shiftManage.year,
			currentMonth: shiftManage.month,
			onMonthSelected: function (value) {
				document.getElementById("date").innerText = value.year + kq.shift.year + value.desc;
				shiftManage.year = value.year;
				shiftManage.month = value.monthOrder + "";
				shiftManage.weekIndex = '1';
				var date = new Date;
				var nowYear = date.getFullYear(); 
				var nowMonth = date.getMonth() + 1;
				if(shiftManage.year == nowYear && shiftManage.month == nowMonth)
					shiftManage.weekIndex = "0";
					
				shiftManage.reloadTable();
				win.hide();
			}
		});
		
		win = Ext.create('Ext.window.Window', {
			id: 'win',
			header: false,
			x: Ext.get("date").getX() - 50,
			y: Ext.get("date").getY() + 15,
			width: 258,
			height: 170,
			items: [SetItemGloble],
			listeners: {
				render: function () {
		            document.getElementById("win").onmouseout = function () {
		                var s = event.toElement || event.relatedTarget;
		                if (s == undefined || !this.contains(s))
		                	win.hide();
		            };
		        }
			}
		});
		
		win.show();
		
		// 点击window外
		shiftManage.hidWindow();
    },
    
    // 查询控件
	createSearchPanel: function(fieldsArray){
		var map = new HashMap();
		shiftManage.SearchBox = Ext.create("EHR.querybox.QueryBox",{
			id:'queryBox',
			hideQueryScheme:false,
			emptyText:kq.shift.queryScheme,
			subModuleId:'shiftManage_0001',
			customParams:map,
			funcId:"KQ00021303",
			hideQueryScheme: true,
			fieldsArray:fieldsArray,
			success:shiftManage.reloadTable
		});
		
		var items = Ext.getCmp('toolbar2').items;
		Ext.getCmp('toolbar2').insert(items.length - 2, shiftManage.SearchBox);
	},
	//刷新表格
	reloadTable: function () {
		var map = new HashMap();
		map.put('groupId', shiftManage.groupId);
		map.put('year', shiftManage.year);
		map.put('month', shiftManage.month);
		map.put('weekIndex', shiftManage.weekIndex);
	    Rpc({functionId:'KQ00021303',async:true,success:shiftManage.reloadGrid},map);
	},
	//重新加载表格数据
	reloadGrid: function (response) {
		var map	 = Ext.decode(response.responseText);
		if(map.succeed){
			shiftManage.weekList = map.weekList;
			var comboBoxStore = Ext.create('Ext.data.Store', {
	            id: 'schemeListStore',
	            fields: ['id', 'name'],
	            autoLoad : true,
	            data: map.weekList
	        });
			
			Ext.getCmp("comboBoxId").setStore(comboBoxStore);
			Ext.getCmp('comboBoxId').setValue(map.weekIndex);
			
			var columns = Ext.decode(getDecodeStr(map.column));
			var columnJson = Ext.decode(getDecodeStr(map.columnJson));
			shiftManage.schemeId = map.schemeId;
			shiftManage.lastWeekScope = map.lastWeekScope;
			shiftManage.weekScope = map.weekScope;
			shiftManage.weekIndex = map.weekIndex;
			Ext.getCmp('shiftManage_0001_shiftGrid').setSchemeId(shiftManage.schemeId);
			Ext.getCmp('shiftManage_0001_shiftGrid').setYear(shiftManage.year);
			Ext.getCmp('shiftManage_0001_shiftGrid').setMonth(shiftManage.month);
			Ext.getCmp('shiftManage_0001_shiftGrid').setWeekIndex(shiftManage.weekIndex);
			Ext.getCmp('shiftManage_0001_shiftGrid').setColumns(columnJson);
			Ext.getCmp('shiftManage_0001_shiftGrid').setFields(columns);
			Ext.getCmp('shiftManage_0001_shiftGrid').setPageSize(map.pageRows);
			Ext.getCmp('shiftManage_0001_shiftGrid').setRemoveFilter("0");
			Ext.getCmp('shiftManage_0001_shiftGrid').setRegionSelect(Ext.getCmp("checkboxId").checked);
			Ext.getCmp('shiftManage_0001_shiftGrid').reloadGrid();
			
			var text = kq.label.publish;
			var disabled = false;
			if("true" == map.pushScheme) {
				disabled = true;
				text = kq.group.edit;
			}
			
			shiftManage.pushScheme = map.pushScheme;
			if(Ext.getCmp("publishButtonId"))
				Ext.getCmp("publishButtonId").setText(text);
			
			if(Ext.getCmp("personId"))
				Ext.getCmp("personId").setDisabled(disabled);
			
			if(Ext.getCmp("shiftId"))
				Ext.getCmp("shiftId").setDisabled(disabled);
			
			if(Ext.getCmp("checkboxId"))
				Ext.getCmp("checkboxId").setDisabled(disabled);
			
			if("-1" == shiftManage.weekIndex) {
				Ext.getCmp("publishButtonId").setDisabled(true);
				Ext.getCmp("shiftCommentId").setHidden(true);
			} else {
				Ext.getCmp("publishButtonId").setDisabled(false);
				Ext.getCmp("shiftCommentId").setHidden(false);
			}
		}
	},
	
	/**
	 * selectedflag =0已选人员标签；=1  未选人员标签 标识
	 */
	getShiftEmpTableConfig:function(selectedflag){
		// 51206 固定班制过滤失败问题
		if("groupPerson" == shiftManage.operation)
			shiftGrid = undefined;
		var tableConfigStr = "";
		var json = {};
		json.type = "init";
		json.group_id = shiftManage.groupId;
		json.scheme_id = shiftManage.schemeId;
		json.org_id = shiftManage.orgId;
		json.selectedflag = selectedflag;
		var map = new HashMap();
		map.put("jsonStr", JSON.stringify(json));
		
		Rpc({functionId:'KQ00021305',success:function(form, action){
	    	var result = Ext.decode(form.responseText);
			if(result.succeed == true) {
				tableConfigStr = result.tableConfig;
				shiftManage.shiftEmpWin(selectedflag, tableConfigStr, result.untableConfig);
			}else {
				Ext.showAlert(result.message);
				return;
			}
		}},map);
		
		return tableConfigStr;
	},
	/**
	 * 班组人员窗口
	 */
	shiftEmpWin:function(selectedflag, tableConfigStr, untableConfig){
		
		if(Ext.isEmpty(tableConfigStr)){
			Ext.showAlert(kq.shift.emplistfail);
			return;
		}
		shiftManage.selectArray = [];
		shiftManage.addArray = [];
		shiftManage.removeArray = [];
		// 增加查询框下拉指标
		tableConfigStr.openColumnQuery = true;
		var table = new BuildTableObj(tableConfigStr);
		var datas = table.tablePanel;
		datas.getStore().on('load',function(store,records){
		    for(var i=0;i<records.length;i++){
		    	shiftManage.selectArray.push(records[i].data.guidkey_e);
		    }
		},datas); 
		var tablePanel = table.getMainPanel();
		// 增加查询框下拉指标
		untableConfig.openColumnQuery = true;
		var untable = new BuildTableObj(untableConfig);
		var untablePanel = untable.getMainPanel();
		
		var selectPersonWin = Ext.getCmp("selectPersonWinid");
		if(selectPersonWin)
			selectPersonWin.close();
		// 已选 待选 页签
		var buttons = Ext.create('Ext.panel.Panel', {
			width: 180,
			height: 40,
			border: 0,
			layout: {
		        align: 'center',
		        pack: 'center',
		        type: 'hbox'
			},
			items: [{
						xtype: 'label',
						id: 'selectedid',
						border: false,
						width: 90,
						html  : '<div class="button-group-inner button-group-inner-select" onclick="shiftManage.selectiveFunc(\'selectedid\')">'
					   		+'<font class="button-group-font button-group-font-select">'+kq.shift.selected+'</font></div>'
			     },{
			    	 	xtype: 'label',
			    	 	id: 'unSelectedid',
			    	 	border: false,
			    	 	width: 90,
			    	 	html  : '<div class="button-group-inner" onclick="shiftManage.selectiveFunc(\'unSelectedid\')">'
					   		+'<font class="button-group-font">'+kq.shift.unselected+'</font></div>' 
			     }]
		});
		
		selectPersonWin = Ext.create('Ext.window.Window', {
			id: 'selectPersonWinid',
		    title: kq.shift.groupEmpDistribution,
		    height: 480,
		    width: 600,
		    modal: true,
//		    resizable: false,
		    layout: {
		    	align: 'center',
		        type: 'vbox'
			},
			items:[
				buttons
				,{
					xtype: 'panel',
					id: 'shiftEmpTableid',
					height: 350,
				    width: 570,
				    border: 0,
				    hidden: false,
				    layout: {
				        type: 'fit'
					},
				    items:[tablePanel]
				} 
				,{
					xtype: 'panel',
					id: 'unshiftEmpTableid',
					height: 350,
				    width: 570,
				    border: 0,
				    hidden: true,
				    layout: {
				        type: 'fit'
					},
				    items:[untablePanel]
				} 
			],
			listeners: {
				// 监听表格随窗口大小改变
				'resize':function(e, width, height, eOpts){
					// 已选表格
					if(Ext.getCmp("shiftEmpTableid")){
						Ext.getCmp("shiftEmpTableid").setWidth(width-32);
						Ext.getCmp("shiftEmpTableid").setHeight(height-130);
					}
					// 待选表格
					if(Ext.getCmp("unshiftEmpTableid")){
						Ext.getCmp("unshiftEmpTableid").setWidth(width-32);
						Ext.getCmp("unshiftEmpTableid").setHeight(height-130);
					}
				}
			},
			buttonAlign: 'center',
			buttons: [{
		    	text: kq.button.ok,
		    	handler:function(){
		    		
		    		// 处理新增人员数组
		    		for(i=0;i<shiftManage.addArray.length;i++){
		    			var guidkey_e = shiftManage.addArray[i];
		    			if(Ext.Array.contains(shiftManage.selectArray, guidkey_e)){
		    				var index = Ext.Array.indexOf(shiftManage.addArray, guidkey_e, 0);
		    				if(index != -1){
		    					Ext.Array.remove(shiftManage.addArray, guidkey_e);
		    					i--;
		    				}
		    			}
		    		}
		    		// 处理取消人员数组
		    		for(i=0;i<shiftManage.removeArray.length;i++){
		    			var guidkey_e = shiftManage.removeArray[i];
		    			if(!Ext.Array.contains(shiftManage.selectArray, guidkey_e)){
		    				var index = Ext.Array.indexOf(shiftManage.removeArray, guidkey_e, 0);
		    				if(index != -1){
		    					Ext.Array.remove(shiftManage.removeArray, guidkey_e);
		    					i--;
		    				}
		    			}
		    		}
		    		// 如果没有变动直接关闭窗口
		    		if(0==shiftManage.addArray.length && 0==shiftManage.removeArray.length){
		    			selectPersonWin.close();
		    			return;
		    		}
		    		// 固定班制人员维护
		    		if("groupPerson" == shiftManage.operation){
		    			// 选择日期
		    			shiftManage.showChangeDateFunc();
		    		}// 排班制人员维护
		    		else{
		    			shiftManage.dateEmpJson = shiftManage.dateJson;
		    			shiftManage.showChangeDateWin(shiftManage.weekList, shiftManage.year, shiftManage.month, shiftManage.weekIndex);
		    		}
		    	}
			},{
		    	text: kq.button.cancle,
		    	handler:function(){
		    		selectPersonWin.close();
			    }
			}]
		});
		selectPersonWin.show();
	},
	/**
	 * 选择 已选 待选事件
	 */
	selectiveFunc:function(id){
		// 已选标识
		var selectbool = ("selectedid" == id);
		// 已选
		var selehtml = '<div class="button-group-inner'+ (selectbool ? ' button-group-inner-select' : '') 
					+'" onclick="shiftManage.selectiveFunc(\'selectedid\')">'
					+'<font class="button-group-font '+ (selectbool ? ' button-group-font-select' : '') +'">'+kq.shift.selected+'</font></div>';
		// 待选 
		var unselehtml = '<div class="button-group-inner'+ (selectbool ? '' : ' button-group-inner-select') 
					+'" onclick="shiftManage.selectiveFunc(\'unSelectedid\')">'
					+'<font class="button-group-font'+ (selectbool ? '' : ' button-group-font-select') +'">'+kq.shift.unselected+'</font></div>';
	   	// 重新为已选待选panel赋值
		Ext.getCmp("selectedid").setHtml(selehtml);
		Ext.getCmp("unSelectedid").setHtml(unselehtml);
		// 通过id校验该隐藏已选还是待选
		Ext.getCmp("shiftEmpTableid").setHidden(!selectbool);
		Ext.getCmp("unshiftEmpTableid").setHidden(selectbool);
	},
	/**
	 * 展现选择日期窗口请求后台数据
	 */
	showChangeDateFunc:function(){
		var json = {};
		json.type = "change_emp_data";
		json.year = '';
		json.month = '';
		json.weekIndex = '';
		var map = new HashMap();
		map.put("jsonStr", JSON.stringify(json));
		
		Rpc({functionId:'KQ00021305',success:function(form, action){
	    	var result = Ext.decode(form.responseText);
			if(result.succeed == true) {
				shiftManage.dateEmpJson = Ext.decode(result.dateJson);
				// 展现窗口
				shiftManage.showChangeDateWin(result.weekList, result.year, result.month, "1");
			}else {
				Ext.showAlert(result.message);
				return;
			}
		}},map);
	},
	// 班组维护选择年月
    groupSelectMonth: function (id, year, month, weekIndex) {
    	var win = Ext.getCmp('groupwin'+id);
		if(win) {
			win.close();
		}
		
		var SetItemGloble = Ext.create("EHR.attendanceMonth.AttendanceMonthComp",{
			totalData: shiftManage.dateEmpJson,
			border: false,
			currentYear: year,
			currentMonth: month,
			onMonthSelected: function (value) {
				document.getElementById(id).innerText = value.year + kq.shift.year + value.desc;
				shiftManage.refreshGroupWeekList(id, value.year, value.monthOrder, '1');
				win.hide();
			}
		});
		
		win = Ext.create('Ext.window.Window', {
			id: 'groupwin'+id,
			header: false,
			x: Ext.get(id).getX() - 50,
			y: Ext.get(id).getY() + 15,
			width: 258,
			height: 170,
			items: [SetItemGloble],
			listeners: {
				"render": function () {
		            document.getElementById("groupwin"+id).onmouseout = function () {
		                var s = event.toElement || event.relatedTarget;
		                if (s == undefined || !this.contains(s))
		                    win.hide();
		            };
		        }
			}
		});
		win.show();
		// 点击window外
		shiftManage.hidWindow();
    },
    /**
     * 班组人员选择调入调出日期后更新周下拉列表
     */
    refreshGroupWeekList:function(id, year, month, weekIndex){
    	
    	var json = {};
		json.type = "change_emp_data";
		json.year = year;
		json.month = month;
		json.weekIndex = weekIndex;
		var map = new HashMap();
		map.put("jsonStr", JSON.stringify(json));
		
		Rpc({functionId:'KQ00021305',success:function(form, action){
	    	var result = Ext.decode(form.responseText);
			if(result.succeed == true) {
				
				var comboBoxStore = Ext.create('Ext.data.Store', {
		            fields: ['id', 'name'],
		            autoLoad : true,
		            data: result.weekList
		        });
				
				Ext.getCmp('cob_'+id).setStore(comboBoxStore);
				Ext.getCmp('cob_'+id).setValue("1");
				
			}else {
				Ext.showAlert(result.message);
				return;
			}
		}},map);
    	
    },
    /**
     * 班组人员维护获取选择日期范围对象
     */
    getGroupDateObj:function(id, weekList, year, month, weekIndex){
    	
    	// 班组维护周下拉数据
		var comboBoxStore = Ext.create('Ext.data.Store', {
            fields: ['id', 'name'],
            autoLoad : true,
            data: weekList
        });
		
		var hiddenflag = true;
		if((shiftManage.addArray.length > 0 && "addDateId"==id) || (shiftManage.removeArray.length > 0 && "removeDateId"==id))
			hiddenflag = false;
		var comboBox = Ext.create('Ext.form.field.ComboBox', {
			id:'cob_'+id,
            fieldLabel: '',
            labelSeparator: '',
            hidden: hiddenflag,
            store: comboBoxStore,
            width: 210,
            forceSelection: true,
            valueField: 'id',
            displayField: 'name',
            shadow: false,
            editable: false,
            allowBlank: false,
            value: weekIndex,
            cls: 'comboxStyle'
        });
		var textname = kq.label.callin + kq.shift.date;
		if("removeDateId" == id)
			textname = kq.label.callout + kq.shift.date;
		var widthValue = 350;
		if("groupPerson" == shiftManage.operation)
			widthValue = 130;
		
		var tbar = new Ext.Toolbar({
			height:25,
			width:widthValue,
			padding:'0 0 0 5',
			border:false,
			items:[{
				xtype: "label",
				text: textname,
				hidden: hiddenflag
			},{
				xtype:"label",
				width:75,
				hidden: hiddenflag,
				html:"<a href='javascript:shiftManage.groupSelectMonth(\""+id+ "\",\""+year+"\",\""+month+"\",\""+weekIndex+"\");' id='" +id+ "' >"  
					+ year + kq.shift.year + month + kq.shift.month + "</a>"
					+ "<img src='/workplan/image/jiantou.png' style='cursor:pointer' id='" + id +"Img'" 
						+ "onclick='shiftManage.groupSelectMonth(\""+id+ "\",\""+year+"\",\""+month+"\",\""+weekIndex+"\")'>"  
			}]
		});
		// 排班制需要选择周
		if(!("groupPerson" == shiftManage.operation) && !hiddenflag){
			tbar.add("-");
			tbar.add(comboBox);
		}
		return tbar;
    },
    /**
     * 展现 维护班组人员选择日期窗口
     */
	showChangeDateWin:function(weekList, year, month, weekIndex){
		
		var addtbar = shiftManage.getGroupDateObj("addDateId", weekList, year, month, weekIndex);
		var removetbar = shiftManage.getGroupDateObj("removeDateId", weekList, year, month, weekIndex);
		
		var changeDateWin = Ext.getCmp("changeDateWinid");
		if(changeDateWin)
			changeDateWin.close();
		
		var widthValue = 500;
		if("groupPerson" == shiftManage.operation)
			widthValue = 400;
		
		changeDateWin = Ext.create('Ext.window.Window', {
			id: 'changeDateWinid',
		    title: kq.shift.groupEmpDistribution,
		    height: 180,
		    width: widthValue,
		    modal: true,
		    resizable: false,
		    layout: {
		    	align: 'center',
		        type: 'vbox'
			},
			items:[{
					xtype: 'panel',
					layout: {
				        align: 'center',
				        pack: 'center',
				        type: 'hbox'
					},
					border: false,
					margin: '10 0 0 0',
					items:[{
						xtype: 'label',//'调入班组成员多少人'
						html: kq.label.callin + kq.shift.person + shiftManage.addArray.length+ kq.label.people
					}
					,addtbar
					]
				} 
				,{
					xtype: 'panel',
					layout: {
				        align: 'center',
				        pack: 'center',
				        type: 'hbox'
					},
					border: false,
					margin: '20 0 0 0',
					items:[{
						xtype: 'label',
						margin: '2 0 0 0',//'调出班组成员多少人'
						html: kq.label.callout + kq.shift.person + shiftManage.removeArray.length+ kq.label.people
					}
					,removetbar
					]
				} 
				],
			buttonAlign: 'center',
			buttons: [{
		    	text: kq.button.ok,
		    	handler:function(){
		    		
		    		var addDateValue = document.getElementById("addDateId").innerText;
		    		var addDateMap = shiftManage.getFormatValue(addDateValue);
		    		var addweek = Ext.getCmp('cob_addDateId').getValue();
		    		addDateMap.put("weekIndex", addweek);
		    		var removeDateValue = document.getElementById("removeDateId").innerText;
		    		var removeDateMap = shiftManage.getFormatValue(removeDateValue);
		    		var removeweek = Ext.getCmp('cob_removeDateId').getValue();
		    		removeDateMap.put("weekIndex", removeweek);
		    		// 保存人员调入调出
		    		shiftManage.saveGroupEmp(addDateMap, removeDateMap);
		    	}
			},{
		    	text: kq.button.cancle,
		    	handler:function(){
		    		changeDateWin.close();
			    }
			}]
		});
		changeDateWin.show();
		
	},
	/**
	 * 格式化年月 2019年1月
	 */
	getFormatValue:function(innerText){
		var yearValue = innerText.split("年")[0];
		var monthText = innerText.split("年")[1];
		var monthValue = monthText.split("月")[0];
		var map = new HashMap();
		map.put("year", yearValue);
		map.put("month", monthValue);
		return map;
	},
	/**
	 * 保存班组人员维护
	 */
	saveGroupEmp:function(addDateMap, removeDateMap){
		
		var selectLen = shiftManage.selectArray.length + shiftManage.addArray.length - shiftManage.removeArray.length;
		var json = {};
		json.type = "change_emp";
		json.group_id = shiftManage.groupId;
		json.scheme_id = shiftManage.schemeId;
		json.org_id = shiftManage.orgId;
		json.selectLen = selectLen;
		json.addArray = shiftManage.addArray;
		json.removeArray = shiftManage.removeArray;
		json.addyear = addDateMap.year;
		json.addmonth = addDateMap.month;
		json.addweekIndex = addDateMap.weekIndex;
		json.removeyear = removeDateMap.year;
		json.removemonth = removeDateMap.month;
		json.removeweekIndex = removeDateMap.weekIndex;
		var map = new HashMap();
		map.put("jsonStr", JSON.stringify(json));
		
		Rpc({functionId:'KQ00021305',success:function(form, action){
	    	var result = Ext.decode(form.responseText);
			if(result.succeed == true) {
				var selectPersonWin = Ext.getCmp("selectPersonWinid");
				if(selectPersonWin)
					selectPersonWin.close();
				// 关闭调入调出日期窗口
				var changeDateWin = Ext.getCmp("changeDateWinid");
				if(changeDateWin)
					changeDateWin.close();
				// 如有返回信息则提示
				if(!Ext.isEmpty(result.returnStr)){
					Ext.showAlert(result.returnStr);
					return;
				}
				// groupPerson 班组界面调用班组成员维护窗口回调函数
				if("groupPerson" == shiftManage.operation){
					shiftGroup.init(shiftGroup.validityflag);
				} else{
					// 回显刷新排班信息
					if(!Ext.isEmpty(shiftManage.schemeId) && !Ext.isEmpty(shiftManage.year)
							&& !Ext.isEmpty(shiftManage.month) && !Ext.isEmpty(shiftManage.weekIndex))
						shiftManage.reloadTable();
				}
				
			}else {
				Ext.showAlert(result.message);
				return;
			}
		}},map);
		shiftManage.addArray = [];
		shiftManage.removeArray = [];
		
	},
	/**
	 * 渲染是否排班
	 */
	renderValidate:function(value, metaData, record){
		// 校验如果在加载后进行筛选查询仍保持之前勾选
		var guidkey_e = record.data.guidkey_e;
		// 已选记录
		if(Ext.Array.contains(shiftManage.addArray, guidkey_e))
			value = "1";
		// 待选记录
		if(Ext.Array.contains(shiftManage.removeArray, guidkey_e))
			value = "0";
		
		var src = "../../../../module/kq/images/kq_on.png";
		var temp = kq.shift.cancleEmp;
		if(value != "1"){
			src = "../../../../module/kq/images/kq_off.png";
			temp = kq.shift.addEmp;
		}
		var id = "validate_"+record.data.guidkey_e;
		return "<img id='"+id+"' title='"+kq.shifts.click+temp+"' onclick='shiftManage.validteClickFn(\""+value+"\",\""+id+"\",\""
				+record.data.guidkey_e+"\")' style='width:40px;cursor:pointer' src='"+src+"'/>";
	},
	/**
	 * 保存添加|取消人员
	 * @returns
	 */
	validteClickFn:function(value, imgId, guidkey_e){
		var newValue = "0";
		var src = "../../../../module/kq/images/kq_off.png";
		var title=kq.shifts.click + kq.shift.addEmp;
		if(value != "1"){
			src = "../../../../module/kq/images/kq_on.png";
			newValue="1";
			title=kq.shifts.click + kq.shift.cancleEmp;
		}
		var img = document.getElementById(imgId);
		if(img){
			img.src=src;
			img.title = title;
			img.onclick = function(){
				shiftManage.validteClickFn(newValue,imgId,guidkey_e);
			}
		}
		// 操作数据库
		shiftManage.editValidate(newValue, guidkey_e);
	},
	
	editValidate:function(value, guidkey_e){
		// =0未选取消  =1选中添加
		if(value == "1"){
			shiftManage.handleArray(shiftManage.addArray, shiftManage.removeArray, guidkey_e);
		}else{
			shiftManage.handleArray(shiftManage.removeArray, shiftManage.addArray, guidkey_e);
		}
	},
	
	handleArray:function(addArray, removeArray, guidkey_e){
		// 不包含增加
		if(!Ext.Array.contains(addArray, guidkey_e))
			addArray.push(guidkey_e);
		// 包含了移除
		if(Ext.Array.contains(removeArray, guidkey_e)){
			var index = Ext.Array.indexOf(removeArray, guidkey_e, 0);
			if(index != -1)
				Ext.Array.remove(removeArray, guidkey_e);
			
		}
	},
	
	deleteShiftInfo:function(){
		var deleteMsg = kq.shift.clearShiftMsg.replace("{0}", shiftManage.weekScope);
		Ext.Msg.confirm(kq.shift.shiftTip, deleteMsg,
				function(button, text){  
			if(button != "yes")
				return;
			
			var map = new HashMap();
			map.put("operation", "deleteShiftInfos");
			map.put("groupId", shiftManage.groupId);
			if(-1 == shiftManage.weekIndex)
				map.put("schemeId", "-1");
			else
				map.put("schemeId", shiftManage.schemeId);
			
			map.put("weekScope", shiftManage.weekScope);
			Rpc({functionId:'KQ00021303',async:false,success:function (response) {
				var map = Ext.decode(response.responseText);
				if(map.succeed){
					Ext.data.StoreManager.lookup("shiftManage_0001_store").reload();
				} else {
					Ext.showAlert(map.message);
				}
			}},map);
		});
	},
	
	searchRemark: function (){
		if(-1 == shiftManage.weekIndex)
			return false;
		
		var map = new HashMap();
		map.put("operation", "searchRemark");
		map.put("groupId", shiftManage.groupId);
		map.put("schemeId", shiftManage.schemeId);
		Rpc({functionId:'KQ00021303',async:false,success:shiftManage.showRemark},map);
	},
	
	showRemark: function (response) {
		var map	 = Ext.decode(response.responseText);
		if(map.succeed){
			var win = Ext.getCmp("remarkWin");
			if(win)
				win.close();
			
			var shiftComment = Ext.create('Ext.form.field.TextArea',{
				id:'shiftComment',
		        grow: true,
		        fieldLabel: kq.shift.shiftComment,
		        labelAlign:'right',
		        width:400,
		        height:80,
		        anchor: '100%',
		        value: map.remarkMap.shiftComment
		    });
			
			var empComment = Ext.create('Ext.form.field.TextArea',{
				id:'empComment',
				grow: true,
				fieldLabel: kq.shift.empComment,
				labelAlign:'right',
				width:400,
				height:80,
				anchor: '100%',
				value: map.remarkMap.empComment
			});
			
			var trainComment = Ext.create('Ext.form.field.TextArea',{
				id:'trainComment',
				grow: true,
				fieldLabel: kq.shift.trainComment,
				labelAlign:'right',
				width:400,
				height:80,
				anchor: '100%',
				value: map.remarkMap.trainComment
			});
			
			win = Ext.create('Ext.window.Window', {
    			id: 'remarkWin',
    			title:kq.shift.weekComment,
    			layout:'vbox',
    			width: 490,
    			height: 350,
    			modal:true,
    			resizable: false,
    			items: [shiftComment, empComment, trainComment],
				buttons:[{
					text:kq.button.ok,
					handler: function (){
						var shiftComment = Ext.getCmp("shiftComment").getValue();
						var empComment = Ext.getCmp("empComment").getValue();
						var trainComment = Ext.getCmp("trainComment").getValue();
						var tipMsg = kq.shift.remarkMax.replace('{0}', "1000").replace('{1}', "20000");
						if(shiftComment.replace(/[^\x00-\xff]/g,"**").length > 2000){
							Ext.showAlert(kq.shift.shiftComment + tipMsg);
							return false;
						}
						
						if(empComment.replace(/[^\x00-\xff]/g,"**").length > 2000){
							Ext.showAlert(kq.shift.empComment + tipMsg);
							return false;
						}
						
						if(trainComment.replace(/[^\x00-\xff]/g,"**").length > 2000){
							Ext.showAlert(kq.shift.trainComment + tipMsg);
							return false;
						}
						
						var map = new HashMap();
						map.put("operation", "saveRemark");
						map.put("groupId", shiftManage.groupId);
						map.put("schemeId", shiftManage.schemeId);
						map.put("shiftComment", shiftComment);
						map.put("empComment", empComment);
						map.put("trainComment", trainComment);
						Rpc({functionId:'KQ00021303',async:false,success:function(){
							var map	 = Ext.decode(response.responseText);
							if(map.succeed){
								Ext.showAlert(kq.label.saveSuccess);
								win.close();
							} else {
								Ext.showAlert(map.message);
								return false;
							}
						}},map);
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
		} else {
			Ext.showAlert(map.message);
			return false;
		}
	},
	
	copyLastWeekShiftInfo:function(){
		var win = Ext.getCmp("copyShiftWin");
		if(win)
			win.close();
		
		var copyMsg = kq.shift.copyShiftMsg.replace("{0}", shiftManage.lastWeekScope).replace("{1}", shiftManage.weekScope);
		var label = Ext.create('Ext.form.Label', {
			width:'100%',
			margin:'0 20 10 20',
	        text: copyMsg
		});
		
		var radioGroup = Ext.create('Ext.form.RadioGroup', {
			id:'radioGroupId',
	        columns: 2,
	        vertical: true,
	        width:'100%',
	        margin:'0 20 0 20',
	        items: [{
	        	boxLabel: kq.shift.coverShiftNull,
	        	name: 'rb', 
	        	inputValue: '1', 
	        	checked: true
	        },{ 
	        	boxLabel: kq.shift.coverShiftAll, 
	        	name: 'rb', 
	        	inputValue: '2'
	        }]
	    });
		
		win = Ext.create('Ext.window.Window', {
			id: 'copyShiftWin',
			title: kq.shift.copyLastWeekShift,
			layout:'vbox',
			width: 350,
			height: 150,
			modal:true,
			resizable: false,
			items: [label, radioGroup],
			buttons:[{
				text:kq.button.ok,
				handler: function (){
					if("-1" == shiftManage.weekIndex)
						return false;
					
					var copyType = Ext.getCmp("radioGroupId").getValue().rb;
					var map = new HashMap();
					map.put("operation", "copyShiftInfo");
					map.put("groupId", shiftManage.groupId);
					map.put("schemeId", shiftManage.schemeId);
					map.put("weekScope", shiftManage.weekScope);
					map.put("lastWeekScope", shiftManage.lastWeekScope);
					map.put("copyType", copyType);
					Rpc({functionId:'KQ00021303',async:false,success:function (response) {
						var map = Ext.decode(response.responseText);
						if(map.succeed){
							if(map.shiftMsg)
								Ext.showAlert(map.shiftMsg);
							else {
								Ext.showAlert(kq.shift.copySuccess);
								shiftManage.reloadTable();
								win.close();
							}
						} else {
							Ext.showAlert(map.message);
						}
					}},map);
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
	
	autoShift: function (){
		var win = Ext.getCmp("autoShiftWin");
		if(win)
			win.close();
		var day = new Date(shiftManage.year, shiftManage.month, 0); 
		//获取天数
		var maxDay = day.getDate();
		var dateLabel = Ext.create('Ext.form.Label', {
			width:'100%',
			margin:'0 20 10 20',
	        text: kq.shift.dateScope
		});
		
		var datePanel = Ext.create('Ext.panel.Panel', {
			width: '100%',
			height:30,
			layout:'hbox',
			border:false,
			padding:'0 0 0 50',
			items: [{
				id:"fromDate",
		        xtype: 'datefield',
		        anchor: '100%',
		        width: 100,
		        format:'Y.m.d',
		        value:new Date(shiftManage.year, shiftManage.month - 1, 1)
		    },{
		        xtype: 'label',
		        text:kq.shift.to,
		        margin:"0 5 0 5"
		    },{
		    	xtype: 'datefield',
		    	id:"toDate",
		    	anchor: '100%',
		    	width: 100,
		        format:'Y.m.d',
		        value:new Date(shiftManage.year, shiftManage.month - 1, maxDay)
		    }]
		});
		
		var tpyeLabel = Ext.create('Ext.form.Label', {
			width:'100%',
			margin:'0 20 10 20',
			text: kq.shift.shiftOption
		});
		
		var radioGroup = Ext.create('Ext.form.RadioGroup', {
			id:'autoRadioGroupId',
	        columns: 2,
	        vertical: true,
	        width:'100%',
	        margin:'0 20 0 50',
	        items: [{
	        	boxLabel: kq.shift.autoShiftNull,
	        	name: 'autoRb', 
	        	inputValue: '1', 
	        	checked: true
	        },{ 
	        	boxLabel: kq.shift.coverShiftAll, 
	        	name: 'autoRb', 
	        	inputValue: '2'
	        }]
	    });
		
		win = Ext.create('Ext.window.Window', {
			id:'autoShiftWin',
			title:kq.label.autoShift,
			width: 350,
			height: 200,
			layout:'vbox',
			modal:true,
			resizable: false,
			items:[dateLabel, datePanel, tpyeLabel, radioGroup],
			buttons:[{
				text:kq.button.ok,
				handler: function (){
					var shfitType = Ext.getCmp("autoRadioGroupId").getValue().autoRb;
					var fromDate = Ext.Date.format(Ext.getCmp("fromDate").getValue(),"Y.m.d");
					var toDate = Ext.Date.format(Ext.getCmp("toDate").getValue(),"Y.m.d");
					if(Ext.isEmpty(fromDate)){
						Ext.showAlert(kq.shift.fromDateEmpty);
						return;
					}
					
					if(Ext.isEmpty(toDate)){
						Ext.showAlert(kq.shift.endDateEmpty);
						return;
					}
					
					if(fromDate > toDate) {
						Ext.showAlert(kq.shift.errorDate);
						return;
					}
					
					Ext.MessageBox.wait("", kq.label.autoShiftWait);	
					var map = new HashMap();
					map.put("operation", "autoShift");
					map.put("groupId", shiftManage.groupId);
					map.put("fromDate", fromDate);
					map.put("toDate", toDate);
					map.put("shfitType", shfitType);
					Rpc({functionId:'KQ00021303',async:true,success:function (response) {
						var map = Ext.decode(response.responseText);
						Ext.MessageBox.close();
						if(map.succeed){
							Ext.showAlert(kq.shift.autoSuccess);
							shiftManage.dateJson = Ext.decode(map.dateJson);
							shiftManage.reloadTable();
							win.close();
						} else {
							Ext.showAlert(map.message);
						}
					}},map);
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
	//xus 导出功能 
	exportWorkingTable:function(flag){
		//flag:0 导出周排班表 ； 1 导出月排班汇总表
		var json = {};
		if(flag=='0'){
			json.type = "week";
		}else if(flag=='1'){
			json.type = "group";
		}
		json.year = this.year;
		json.month = this.month;
		json.weekIndex = this.weekIndex;
		json.groupId = this.groupId;
		json.schemeId = this.schemeId;
		json.dataSql = Ext.getCmp('shiftManage_0001_shiftGrid').dataSql;
		
		var map = new HashMap();
		map.put("jsonStr", JSON.stringify(json));
		map.put("params","");
		Rpc({functionId : 'KQ00021307',async : false,success : function(form){
			var result = Ext.decode(form.responseText);	
			if(result.msg==null||result.msg==""){
				if(result.succeed){
					window.location.target="_blank";
					window.location.href = "/servlet/vfsservlet?fileid="+result.fileName+"&fromjavafolder=true";
				}else{
					Ext.MessageBox.show({  
						title : zc.label.remind,  
						msg : result.message, 
						icon: Ext.MessageBox.INFO  
					})
				}
	     	}else{
	     	     Ext.showAlert(result.msg);
	     	}
		}},map);
	},
	
	pushShift: function(state){
		if(-1 == shiftManage.weekIndex)
			return false;
		
		var tipMsg = kq.shift.pushTip;
		var alertMsg = kq.shift.publishSuccess;
		if("edit" == state){
			tipMsg = kq.shift.revokeTip;
			alertMsg = kq.shift.revokeSuccess;
		}
			
		
		Ext.Msg.confirm(kq.shift.shiftTip, tipMsg,
				function(button, text){
			if(button != "yes")
				return;
			
			var map = new HashMap();
			map.put("operation", "pushShiftScheme");
			map.put("groupId", shiftManage.groupId);
			map.put("schemeId", shiftManage.schemeId);
			map.put("state", state);
			Rpc({functionId:'KQ00021303',async:false,success:function (response) {
				var map = Ext.decode(response.responseText);
				if(map.succeed){
					Ext.showAlert(alertMsg);
					shiftManage.reloadTable();
				} else {
					Ext.showAlert(map.message);
				}
			}},map);
		});
	},
	//添加隐藏年月选择的窗口的监听
	hidWindow: function () {
		var win = Ext.getCmp('win');
		var addDateIdWin = Ext.getCmp('groupwinaddDateId');
		var removeDateId = Ext.getCmp('groupwinremoveDateId');
		// 点击window外
        Ext.getBody().addListener('click', function(evt, el) {
        	var html = el.outerHTML;
        	var innerHTML = el.innerHTML;
        	if ("date" == el.id || "dateImg" == el.id || "addDateId" == el.id || "removeDateId" == el.id
        			 || "addDateIdImg" == el.id || "removeDateIdImg" == el.id|| (html && html.length > 5 
        					&& (html.length - 5) == html.lastIndexOf(kq.shift.month + "</b>"))
        			 || !innerHTML)
        		return false;
        	//隐藏排班管理页面的年月选择窗口
        	if(win)				
        		win.hide();
        	//隐藏添加人员的年月选择窗口
        	if(addDateIdWin)				
        		addDateIdWin.hide();
        	//隐藏调出人员的年月选择窗口
        	if(removeDateId)				
        		removeDateId.hide();
        });
	},
	
	schemeSetting: function(showPublicPlan) {
		Ext.require("EHR.tableFactory.plugins.SchemeSetting",function(){
			var window = new EHR.tableFactory.plugins.SchemeSetting({
					subModuleId:'shiftManage_0001',
					showPublicPlan:showPublicPlan,
					schemeItemKey:'A',
					itemKeyFunctionId:'',
					showPageSize:true,
					viewConfig: {
						publicPlan: true,
						order: false,
						merge: false,
						sum: false
					},
					closeAction:shiftManage.closeSettingWindow
			});
		});
	},
	
	closeSettingWindow: function (){
		var map = new HashMap();
		map.put("dataType", "shiftData");
		map.put("operation", "changeSubmoudleId");
	    Rpc({functionId:'KQ00021303',success:shiftManage.searchrTableSucc},map);
	},
	
	searchrTableSucc: function (response) {
		var map = Ext.decode(response.responseText);
		if(map.succeed){
			shiftManage.reloadTable();
		} else {
			Ext.showAlert(map.message);
		}
	},
	/**
	 * 返回 排班后返回班组需定位到当前页 故重新渲染人数列
	 * linbz
	 */
	calBackFunc:function (){
    	var count = Ext.getCmp("shiftManage_0001_grid").getStore().getTotalCount();
    	parent.document.getElementById(shiftManage.renderPerNumid).innerHTML = count;
    	parent.Ext.getCmp('shiftWin').close();
    },
    /**
     * 班组选人增加 全选按钮
     * flag =0全部加入；=1全部取消
     */
    selectAllPerson:function(flag){
    	// 当前页面是哪个表格 =0已选  =1未选
    	var isSelect = 0;
    	// 已选表格
		if(Ext.getCmp("shiftEmpTableid")){
			if(Ext.getCmp("shiftEmpTableid").isHidden())
				isSelect = 1;
		}
		// 待选表格
		if(Ext.getCmp("unshiftEmpTableid")){
			if(Ext.getCmp("unshiftEmpTableid").isHidden())
				isSelect = 0;
		}
		var grid = null;
		if(isSelect == 0){
			grid = Ext.getCmp('kqshiftemp_01_tablePanel');
		}else if(isSelect == 1){
			grid = Ext.getCmp('kqshiftemp_02_tablePanel');
		}else
			return;
		
		var dataList = grid.getStore().getData().items;
		for(var i = 0; i < dataList.length; i++) {
			var data = dataList[i].data;
			var guidkey_e = data.guidkey_e
			var imgId = "validate_" + guidkey_e;
			if("0" == flag){
				shiftManage.validteClickFn("0", imgId, guidkey_e);
			}else if("1" == flag){
				shiftManage.validteClickFn("1", imgId, guidkey_e);
			}
		}
		
    }
});