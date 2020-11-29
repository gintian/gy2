/**
 * 员工项目工时明细
 * ly 2015-12-03
 * 
 * */
Ext.define('ManHoursSumUL.ManHoursSum',{
	constructor:function(config) {
		ManHoursSum_me = this;
		//type = 1 主页穿透  type = 2 汇总页穿透
		ManHoursSum_me.type = config.type;
		ManHoursSum_me.projectId = config.projectId;
		ManHoursSum_me.manDetailId = '';
		ManHoursSum_me.temId = '';
		if('2'==ManHoursSum_me.type){
			ManHoursSum_me.manDetailId = config.manId;
		}else if('1'==ManHoursSum_me.type){
			ManHoursSum_me.milestone = config.milestone;
		}
		ManHoursSum_me.title = config.projectName; 
		ManHoursSum_me.init();
	},
	// 初始化函数
	init:function() {
		var map = new HashMap();
		map.put("projectId",ManHoursSum_me.projectId);
		map.put("title",ManHoursSum_me.title+'');
		map.put("manDetailId",ManHoursSum_me.manDetailId+'');
		map.put("type",ManHoursSum_me.type+'');
		map.put('dateRange',"00");
		if('1'==ManHoursSum_me.type){
			map.put('milestone',ManHoursSum_me.milestone);
		}
	    Rpc({functionId:'PM00000201',success:ManHoursSum_me.getTableOK},map);
	},
	// 加载表单
	getTableOK:function(form,action){
		//Ext.util.CSS.createStyleSheet(".scheme-selected-cls{text-decoration:underline;margin-left:10px;margin-right:10px}","underline");
		if(Ext.util.CSS.getRule(".scheme-selected-cls")){
			Ext.util.CSS.updateRule(".scheme-selected-cls","text-decoration","underline");
			Ext.util.CSS.updateRule(".scheme-selected-cls","margin-left","10px");
			Ext.util.CSS.updateRule(".scheme-selected-cls","margin-right","10px");
		}else
			Ext.util.CSS.createStyleSheet(".scheme-selected-cls{text-decoration:underline;margin-left:10px;margin-right:10px}","underline");
		
		var result = Ext.decode(form.responseText);
		var conditions=result.tableConfig;
		var obj = Ext.decode(conditions);
		templatelistObj = new BuildTableObj(obj);
		var templatelistItem = templatelistObj.getMainPanel();

		currentName = "";
		 var toolbar  = Ext.create('Ext.toolbar.Toolbar',{
				border:0,
				id:"toolbar",
				dock:'top',
				items:[{
						xtype:'label',
						text: '查询方案：',
				     	style:'margin-right:10px'
					},{
						xtype:'label',
						html:'<a href="javascript:ManHoursSum_me.searchStatus(labelAll.id);">全部</a> ',
						id:'labelAll',
						style:'margin-left:10px;margin-right:10px;',
						cls:'scheme-selected-cls'
					},'-',{
						xtype:'label',
						id:'labelWeek',
						html:'<a href="javascript:ManHoursSum_me.searchStatus(labelWeek.id);">本周</a> ',
						style:'margin-left:10px;margin-right:10px;'
					},{
						xtype:'label',
						id:'labelMonth',
						html:'<a href="javascript:ManHoursSum_me.searchStatus(labelMonth.id);">本月</a> ',
						style:'margin-left:10px;margin-right:10px;'
					},{
						xtype:'label',
						id:'labelSeason',
						html:'<a href="javascript:ManHoursSum_me.searchStatus(labelSeason.id);">本季</a> ',
						style:'margin-left:10px;margin-right:10px;'
					},{
						xtype:'label',
						id:'labelYear',
						html:'<a href="javascript:ManHoursSum_me.searchStatus(labelYear.id);">本年</a> ',
						style:'margin-left:10px;margin-right:10px;'
					}]
			});
			templatelistObj.insertItem(toolbar,0);
			Ext.getCmp("manhoursSum_tablePanel").on("remove",function(port){
				Ext.util.CSS.removeStyleSheet("underline");
				Ext.util.CSS.removeStyleSheet("treegridImg");
				Ext.util.CSS.removeStyleSheet("gridCell");
				Ext.util.CSS.refreshCache();
			});
			
			var window = Ext.getCmp('hoursSumid');
			if(window)
				window.close();
			//创建window对象
			window = new Ext.window.Window({
				maximized : true,
				header: false,
				padding:'0 1 1 0',
				border : false,
				id : 'hoursSumid',
				closable : false,
				autoScroll : true,
				items:[templatelistItem],
				layout :'fit'
			});
			window.show();
	
	},
	searchStatus:function(id){
		Ext.getCmp('labelAll').removeCls('scheme-selected-cls');
		Ext.getCmp('labelWeek').removeCls('scheme-selected-cls');
		Ext.getCmp('labelMonth').removeCls('scheme-selected-cls');
		Ext.getCmp('labelSeason').removeCls('scheme-selected-cls');
		Ext.getCmp('labelYear').removeCls('scheme-selected-cls');
		Ext.getCmp(id).addCls('scheme-selected-cls');
		ManHoursSum_me.labelflag = id;
		 var map = new HashMap();
		 var value = '00';
		 if(id=='labelAll'){
		 	value='00';
		 }
		 if(id=='labelWeek'){
		 	value='01';
		 }
		 if(id=='labelMonth'){
		 	value='02';
		 }
		 if(id=='labelSeason'){
		 	value='03';
		 }
		 if(id=='labelYear'){
			value='04';
		 }
		 
		 Ext.getCmp("manhoursSum_querybox").removeAllKeys();
		 map.put("projectId",ManHoursSum_me.projectId);
		 map.put("title",ManHoursSum_me.title+'');
		 map.put("manDetailId",ManHoursSum_me.manDetailId+'');
		 map.put("type",ManHoursSum_me.type+'');
		 map.put('dateRange',value);
		 Rpc({functionId:'PM00000201',success:function(form,action){Ext.getCmp('manhoursSum_tablePanel').getStore().loadPage(1);}},map);
	},
	accede:function(){
		var tablePanel=Ext.getCmp('manhoursSum_tablePanel');
		var selectRecord = tablePanel.getSelectionModel().getSelection();
		if(selectRecord.length<1){
			Ext.Msg.alert('提示信息',"请选择数据！");
			return;
		}else{
			
			var map = new HashMap();
			var manSumIdStrs = '';
			var manDetailStrs = '';
			var landMarkStrs = '';
			for(var i=0;i<selectRecord.length;i++){
				if (selectRecord[i].data.p1519!=0){
					var tip = "已退回的数据不允许同意"
					if(selectRecord[i].data.p1519==1)
						tip = "已同意的数据不允许再次同意";
					Ext.Msg.alert('提示信息',tip);
					return;
				}
				var p1501 = selectRecord[i].data.p1501;
				var p1301 = selectRecord[i].data.p1301;
				var p1201 = selectRecord[i].data.p1201;
				manSumIdStrs+= p1501+',';
				if(""!=p1301&&manDetailStrs.indexOf(p1301)==-1){
					manDetailStrs+=p1301+',';
				}
				if(""!=p1201&&landMarkStrs.indexOf(p1201)==-1)
				landMarkStrs+= p1201+',';
			}
			var map = new HashMap();
			map.put("manSumIdStrs", manSumIdStrs);
			map.put("manDetailStrs", manDetailStrs);
			map.put("landMarkStrs", landMarkStrs);
			map.put("projectId",ManHoursSum_me.projectId+'');
			map.put("type",'accede');
			ManHoursSum_me.showRefuseOrAccedeTipWin(map,'工作安排',"同意");
		}
	},
	refuse:function(){
		var tablePanel=Ext.getCmp('manhoursSum_tablePanel');
		var selectRecord = tablePanel.getSelectionModel().getSelection();
		if(selectRecord.length<1){
			Ext.Msg.alert('提示信息',"请选择数据！");
			return;
		}else{
			var map = new HashMap();
			var manSumIdStrs = '';
			var manDetailStrs = '';
			for(var i=0;i<selectRecord.length;i++){
				if (selectRecord[i].data.p1519!=0){
					var tip = "已退回的数据不允许再次退回"
						if(selectRecord[i].data.p1519==1)
							tip = "已同意的数据不允许退回";
						Ext.Msg.alert('提示信息',tip);
						return;
				}
				var p1501 = selectRecord[i].data.p1501;
				var p1201 = selectRecord[i].data.p1201;
				manSumIdStrs+= p1501+',';
			}
			var map = new HashMap();
			map.put("manSumIdStrs", manSumIdStrs);
			map.put("type","refuse");
			ManHoursSum_me.showRefuseOrAccedeTipWin(map,'退回原因','退回');
		}
	},
	dele:function(){
		var tablePanel=Ext.getCmp('manhoursSum_tablePanel');
		var selectRecord = tablePanel.getSelectionModel().getSelection();
		if(selectRecord.length<1){
			Ext.Msg.alert('提示信息',"请选择数据！");
			return;
		}else{
			Ext.Msg.confirm("提示信息", "确认要删除所选数据吗？", function(button, text){  
				if(button != "yes")
					return;
				var map = new HashMap();
				var manSumIdStrs = '';
				var manDetailStrs = '';
				var landMarkStrs = '';
				for(var i=0;i<selectRecord.length;i++){
					var p1501 = selectRecord[i].data.p1501;
					var p1301 = selectRecord[i].data.p1301;
					var p1201 = selectRecord[i].data.p1201;
					manSumIdStrs+= p1501+',';
					if(""!=p1301&&manDetailStrs.indexOf(p1301)==-1){
						manDetailStrs+=p1301+',';
					}
					if(""!=p1201&&landMarkStrs.indexOf(p1201)==-1)
					landMarkStrs+= p1201+',';
				}
				var map = new HashMap();
				map.put("manSumIdStrs", manSumIdStrs);
				map.put("manDetailStrs", manDetailStrs);
				map.put("landMarkStrs", landMarkStrs);
				map.put("projectId",ManHoursSum_me.projectId+'');
				map.put("type",'dele');
				
				Rpc({functionId:'PM00000202',async:false,success:function(form,action){
					Ext.getCmp("manhoursSum_tablePanel").getStore().load();
				}},map);
			}
		)}
	},
	returnToDetail:function(){
		/*templatelistObj.getMainPanel().destroy();*/
		ManHoursSum_me.resetCheckBox();
		var map = new HashMap();
		map.put("projectId", ManHoursSum_me.projectId);
		map.put("name", ManHoursSum_me.title);
		Ext.require('ManHoursDetailUL.ManHoursDetail', function(){
			Ext.create("ManHoursDetailUL.ManHoursDetail", map);
		});
		var window = Ext.getCmp('hoursSumid');
		if(window)
			window.close();
	},
	//还原checkbox
	resetCheckBox:function(){
		Ext.override(Ext.selection.CheckboxModel,{
			renderer: function(value, metaData, record, rowIndex, colIndex, store, view) {
				var baseCSSPrefix = Ext.baseCSSPrefix;
		        metaData.tdCls = baseCSSPrefix + 'grid-cell-special ' + baseCSSPrefix + 'grid-cell-row-checker';
		        return '<div class="' + baseCSSPrefix + 'grid-row-checker">&#160;</div>';
			}
		})
	},
	returnToMainPage:function(){
		ManHoursSum_me.resetCheckBox();
		var window = Ext.getCmp('hoursSumid');
		if(window)
			window.close();
	},
	
	//将分钟数转为小时  7小时35分钟
	infactTime:function(value,c,record){
		var html="";
		if(value!=""&&value!=null){
		if (value<0)
			html = "-";

		var intPart = Math.floor(Math.abs(value/60));
		var ysPart = Math.abs(value%60);
		
		if(intPart>0){
			html += intPart + "小时"; 
		}
		
		if (ysPart>0){
			html += ysPart + "分钟";
		}
		}
		return html;
	},
	schemeSaveCallback:function(){
		templatelistObj.getMainPanel().destroy();
		ManHoursSum_me.init();
	},
	
	showRefuseOrAccedeTipWin:function(map,title,buttonName){
		var textArea = Ext.create('Ext.form.field.TextArea',
				{
					id : 'textAreaId',
					labelSeparator : null,
					width:240
				});
		var button = Ext.create('Ext.Button', 
				{
					text: buttonName,
					handler: function() {
						var text = Ext.getCmp('textAreaId').getValue();
						map.put("text",text);
						Rpc({functionId:'PM00000202',async:false,success:function(form,action){
							Ext.getCmp("manhoursSum_tablePanel").getStore().load();
							Ext.getCmp("showRefuseTipWinId").close();
						}},map);
					}
				});		
		var win = new Ext.window.Window(
				{  
					title : title,
					renderTo : Ext.getBody(),
					border : false,
					id : 'showRefuseTipWinId',
					width:250,
					layout : {
						type : "vbox",
						align : "center"
					},
					items:[textArea,button]
				}).show();
		
		
	}
});
