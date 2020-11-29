/**
 * 工作总结-周总结
 * 
 * @createtime dec 05, 2016 9:07:55 AM
 * @author chent
 * 
 */
Ext.define('OKR.WeeklySummary', {
	requires:['EHR.extWidget.proxy.TransactionProxy','EHR.extWidget.field.DateTimeField','EHR.extWidget.field.BigTextField'],
	cycle : '',
	year : '',
	month : '',
	week : '',
	p0100 : '',
	isself : '',
	p0115 : '',
	zhouzjpx : '',// 周总结-培训需求字段，空 表示没启用
	nbase:'',
	a0100:'',
	editFunc : false,
	isopentasktime:false,//是否启用计时
	contentWidth : Ext.get('zhouzjdiv').getWidth()-2,
	constructor : function(config) {
		weeklysummary_me = this;

		this.loadParam(config); // 初始化参数
		this.setEditFunc();// 编辑权限
		
		this.createSelfCss(); // 自定义样式
		this.init(); // 初始化页面
		this.bindEvent();// 绑定事件，页面resize等。。 
	},
	// 初始化函数
	init : function() {
		var weeklySummary = this.getWeeklySummary();// 本周工作总结
		var diaryContent = this.getDiaryContent();// 本周工作日志
		var nextWorkPlan = this.getNextWorkPlan();// 下期工作计划

		weeklysummary_me.mainpanel = Ext.widget('panel', {
			id : 'mainpanel',
			renderTo : 'zhouzjdiv',
			width:"100%",
			minHeight : 300,
			border : 0,
			items : [{
						border: false,
						xtype : 'label',
						padding: '5 0 0 1',
						layout: 'fit',
						html:weeklysummary_me.getGridTitle(1)
					},
			          weeklySummary,
			          {
							border: false,
		        			xtype : 'label',
		        			padding: '5 0 0 1',
		        			layout: 'fit',
	    	        		html:weeklysummary_me.getGridTitle(2)
						},
					diaryContent,
					{
						border: false,
	        			xtype : 'label',
	        			padding: '5 0 0 1',
	        			layout: 'fit',
    	        		html:weeklysummary_me.getGridTitle(3)
					},
					nextWorkPlan
//					,contentArea 
					],
					listeners:{
						'resize':function(){
								var contentWidth = this.getZjWidth()-2;
								Ext.getCmp('grid1').setWidth(contentWidth);
								Ext.getCmp('grid2').setWidth(contentWidth);
								Ext.getCmp('grid3').setWidth(contentWidth);
								var textfield1Width = contentWidth - 34 - 24 - 100 - 24 - 10 - 20;
								Ext.getCmp('textfield1').setWidth(textfield1Width);
								var textfield3Width = contentWidth - 34 - 100 - 24 - 10 - 20;
								Ext.getCmp('textfield3').setWidth(textfield3Width);
							},
							scope:this
					}
		});
		
		if (!Ext.isEmpty(this.zhouzjpx)) {
			this.getContentArea();
			this.initContentValue();// 培训需求等赋值
		}
	},
	// 本期工作总结
	getWeeklySummary : function() {
		var columns = [];
		columns.push({
			header : '计划任务',
			dataIndex : 'p1903',
			flex : 2,
			editor : Ext.widget('bigtextfield',{}),
			menuDisabled : true,
			sortable : false,
			renderer :weeklysummary_me.addGridCssMemo
		});
		
		if(this.isopentasktime){//启用计时
			var editor = Ext.widget('textfield', {});
			
			columns.push({
				header : '耗时（分钟）',
				dataIndex : 'p1905',
				flex : 1,
				editor:editor,
				menuDisabled : true,
				sortable : false,
				align : 'right',
				scope : this
			});
		}
		
		columns.push({
			header : '对照总结',
			dataIndex : 'p1907',
			flex : 2,
			editor : Ext.widget('bigtextfield',{}),
			menuDisabled : true,
			sortable : false,
			renderer :weeklysummary_me.addGridCssMemo
		});
		
		return Ext.create('Ext.grid.Panel', {
			id : 'grid1',
			title : false,
			store : this.getWeeklySummaryData_1(),
			border : 1,
			width : this.contentWidth,
			features : this.getGridFeatures(),
			selModel : Ext.create('Ext.selection.CheckboxModel', {
				mode : "SIMPLE"
			}),
			//表格复制属性
			viewConfig: {
                enableTextSelection: true
            },
			columns : columns,
			plugins : [ {
				ptype : 'cellediting',
				clicksToEdit : 1,
				listeners : {
					beforeedit : function(editor, e) {// 校验

						//1、没有权限不许修改 2、来自计划和日志的总结的【任务】【耗时】不允许修改
						var p1919 = e.record.get('p1919');
						if (!this.editFunc || ((p1919 == '1' || p1919 == '2') && (e.field == 'p1903' || e.field == 'p1905'))) {
							e.cancel = true;
							return;
						}
					},
					validateedit : function(editor, e) {// 保存
						if (e.originalValue == e.value) {
							return;
						}
						if (e.field == 'p1903' && (Ext.isEmpty(e.value) || Ext.isEmpty(e.value.replace(/(^\s*)|(\s*$)/g, "")))){
                            Ext.showAlert('任务名称不能为空！');
                            e.cancel = true;
                            return;
                        }
						if (e.field == 'p1905') {// 耗时只能为数字
							var reg = new RegExp("^(0|[1-9][0-9]*)$");
							if (!reg.test(e.value)) {
								e.cancel = true;
								return;
							}
						}
						this.updateRecord(1, e);
						editSummary();
					},
					scope : this
				}
			} ],
			bbar : this.getInputBbar(1),
			listeners : {
				'beforeexpand' : function(o) {
					this.setIcon(1, 'up');// 收起按钮
				},
				'beforecollapse' : function(o) {
					this.setIcon(1, 'down');// 展开按钮
				},
				'beforegroupclick':function(){
					Ext.getDom('savebutton').focus();
				},
				'resize':function(){
					this.contentWidth = Ext.get('zhouzjdiv').getWidth()-2;
				},
				scope : this
			}
		});
	},
	// 本期工作日志
	getDiaryContent : function() {
		var columns = [];
		columns.push({
			header : '工作内容',
			dataIndex : 'content',
			menuDisabled : true,
			sortable : false,
			flex : 3,
			renderer : function(value) {
				return '<div title="'+value+'" style="height:30px;line-height:30px;">'+value+'</div>';
			}
		});
		
		columns.push({
			header : '完成情况',
			dataIndex : 'finish_desc',
			menuDisabled : true,
			sortable : false,
			flex : 2,
			renderer : function(value) {
				return '<div title="'+value+'" style="height:30px;line-height:30px;">'+value+'</div>';
			}
		});
		if(this.isopentasktime){//启用计时
			columns.push({
				header : '起始时间',
				dataIndex : 'start_time',
				menuDisabled : true,
				sortable : false,
				flex : 2
			});
			
			columns.push({
				header : '截止时间',
				dataIndex : 'end_time',
				menuDisabled : true,
				sortable : false,
				flex : 2
			});
			
			columns.push({
				header : '耗时（分钟）',
				dataIndex : 'work_time',
				menuDisabled : true,
				sortable : false,
				align : 'right',
				flex : 1
			});
		}

		columns.push({
			header : '备注',
			dataIndex : 'other_desc',
			menuDisabled : true,
			sortable : false,
			flex : 2,
			renderer : function(value) {
				return '<div title="'+value+'" style="height:30px;line-height:30px;">'+value+'</div>';
			}
		});
		
		return Ext.create('Ext.grid.Panel', {
			id : 'grid2',
			title : false,
			store : this.getWeeklySummaryData_2(),
			features : this.getGridFeatures(),
			border : 1,
//			collapsed : true,
			hidden : true,
//			margin:'0 0 0 -1',
			width : this.contentWidth,
			viewConfig: {
                enableTextSelection: true
            },
			columns : columns,
			listeners : {
				'beforeexpand' : function(o) {
					this.setIcon(2, 'up');// 收起按钮
				},
				'beforecollapse' : function(o) {
					this.setIcon(2, 'down');// 展开按钮
				},
				scope : this
			}
		});
	},
	// 下期工作计划
	getNextWorkPlan : function() {
		return Ext.create('Ext.grid.Panel', {
			id : 'grid3',
			title : false,
//			collapsed : true,
			hidden : true,
//			margin:'0 0 -1 -1',
			border : 1,
			store : this.getWeeklySummaryData_3(),
			width : this.contentWidth,
			features : this.getGridFeatures(),
			selModel : Ext.create('Ext.selection.CheckboxModel', {
				mode : "SIMPLE"
			}),
			viewConfig: {
                enableTextSelection: true
            },
			columns : [ {
				header : '计划任务',
				dataIndex : 'p1903',
				flex : 2,
				editor : Ext.widget('bigtextfield',{}),
				menuDisabled : true,
				sortable : false,
				scope : this,
				renderer :weeklysummary_me.addGridCssMemo
			} ],
			plugins : [ {
				ptype : 'cellediting',
				clicksToEdit : 1,
				listeners : {
					beforeedit : function(editor, e) {// 校验

						// 1、没有权限不许修改
						var p1919 = e.record.get('p1919');
						if (!this.editFunc) {
							e.cancel = true;
							return;
						}
					},
					validateedit : function(editor, e) {// 保存
						if (e.originalValue == e.value) {
							e.cancel = true;
							return;
						}
                        if (e.field == 'p1903' && ((Ext.isEmpty(e.value) || Ext.isEmpty(e.value.replace(/(^\s*)|(\s*$)/g, ""))))){
                            Ext.showAlert('任务名称不能为空！');
                            e.cancel = true;
                            return;
                        }
						this.updateRecord(3, e);
						editSummary();
					},
					scope : this
				}
			} ],
			bbar : this.getInputBbar(3),
			listeners : {
				'beforeexpand' : function(o) {
					this.setIcon(3, 'up');// 收起按钮
				},
				'beforecollapse' : function(o) {
					this.setIcon(3, 'down');// 展开按钮
				},
				'beforegroupclick':function(){
					Ext.getDom('savebutton').focus();
				},
				scope : this
			}
		});
	},
	// 培训需求区域
	getContentArea : function() {
//		var container = Ext.widget('container', {//
//			border:false,
//			layout:{
//				type:'anchor'
//			},
//			items:[]
//		});
		
		var objArray = this.zhouzjpx.split(',');
		for(var i=0; i<objArray.length; i++){
			var obj = objArray[i];
			var itemArray = obj.split(':');
			var itemId = itemArray[0];
			var itemDesc = itemArray[1];
			
			var id = i+4;//前面有三个列表，分别是grid1、2、3，这里就从4开始。
			var panel = Ext.widget('panel', {
				id : 'grid'+id,
				title : false,
				border : false,
				layout : 'anchor',
//				collapsed : true,
				hidden : true,
				items : [ {
					xtype : 'textareafield',
					id:'content_'+id,
					grow : true,
					height : 80,
					padding : '0 10 0 10',
					anchor : '100%'
				} ],
				listeners : {
					'beforeexpand' : function(o) {
						var index = o.id.charAt(o.id.length - 1);
						this.setIcon(index, 'up');// 收起按钮
					},
					'beforecollapse' : function(o) {
						var index = o.id.charAt(o.id.length - 1);
						this.setIcon(index, 'down');// 展开按钮
					},
					scope : this
				}
			});
			
			weeklysummary_me.mainpanel.add({
				border: false,
    			xtype : 'label',
    			padding: '5 0 0 1',
    			layout: 'fit',
        		html:weeklysummary_me.getGridTitle(id, itemDesc)
			});
			weeklysummary_me.mainpanel.add(panel);
		}
//		return container;
	},
	// 本期工作总结数据源
	getWeeklySummaryData_1 : function(p0100) {

		return Ext.create('Ext.data.Store', {
			// id:'weeklysummary',
			// p1900,p0100,p1901,p1903,p1905,p1907
			fields : [ 'p1900', 'p0100', 'p1901', 'p1903', 'p1905', 'p1907', 'p1919' ],
			groupField : 'p1901',
			proxy : {
				type : 'transaction',
				functionId : 'WP20000004',
				extraParams : {
					type : '1',
					option : '1',
					p0100 : this.p0100
				},
				reader : {
					type : 'json',
					root : 'data'
				}
			},
			autoLoad : true
		});
	},
	// 本周工作日志数据源
	getWeeklySummaryData_2 : function(p0100) {

		return Ext.create('Ext.data.Store', {
			// id:'weeklysummary',
			fields : [ 'content', 'finish_desc', 'start_time', 'end_time', 'work_time', 'other_desc', 'work_type' ],
			groupField : 'work_type',
			proxy : {
				type : 'transaction',
				functionId : 'WP20000004',
				extraParams : {
					type : '2',
					option : '1',
					p0100 : this.p0100
				},
				reader : {
					type : 'json',
					root : 'data'
				}
			},
			autoLoad : true
		});
	},
	// 下期工作计划数据源
	getWeeklySummaryData_3 : function(p0100) {

		return Ext.create('Ext.data.Store', {
			// id:'weeklysummary',
			fields : [ 'p1900', 'p0100', 'p1901', 'p1903', 'p1905', 'p1907' ],
			groupField : 'p1901',
			proxy : {
				type : 'transaction',
				functionId : 'WP20000004',
				extraParams : {
					type : '3',
					option : '1',
					p0100 : this.p0100
				},
				reader : {
					type : 'json',
					root : 'data'
				}
			},
			autoLoad : true
		});
	},
	// 文本框
	getTextfield : function() {
		return Ext.widget('textfield', {});
	},
	// 新增记录 index：1本期工作总结 3下期工作计划
	addRecord : function(index) {
		var p1901 = '';// 任务类别
		var p1903 = '';// 任务名称
		var combo = Ext.getCmp('combo' + index);
		if (combo) {
			if (!combo.isValid()) {
				return;
			}
			p1901 = combo.getValue();
		}
		var textfield = Ext.getCmp('textfield' + index);
		if (textfield) {
			p1903 = textfield.rawValue;
		}

		if (Ext.isEmpty(p1903) || Ext.isEmpty(p1903.replace(/(^\s*)|(\s*$)/g, ""))) {
			Ext.showAlert('任务名称不能为空！');
			return ;
		}
		
		if(p1903 == '创建任务，也可在右侧选择岗位职责进行添加'){
			return ;
		}
		// 校验任务名是否重复
		var flag = false;
		var store = Ext.getCmp('grid' + index).getStore();
		var records = store.data.items;
		for(var i=0; i<records.length; i++){
			var record = records[i];
			var _p1903 = record.data.p1903;
			
			if(_p1903 == p1903){//重复
				flag = true;
				break;
			}
		}
		if(flag){
			Ext.showAlert('已存在同名任务，任务名称不能重复！');
			return ;
		}
		
		var map = new HashMap();
		map.put("type", index+'');
		map.put("option", '2');
		map.put("p0100", this.p0100);
		map.put("p1901", p1901);
		map.put("p1903", p1903);
		Rpc({
			functionId : 'WP20000004',
			async : false,
			success : function(response) {
				var obj = Ext.decode(response.responseText);
				var errorcode = obj.errorcode;
				if (errorcode == '1') {
					Ext.showAlert('新增失败！');

				} else if (errorcode == '0') {// success
					var p1900 = obj.p1900;
                    var store = Ext.getCmp('grid' + index).getStore();
                    var strRecord = "{p0100:'" + this.p0100 + "',p1900:'" + p1900 + "',p1901:'" + p1901 + "',p1903:'" + p1903.replace(/\n/g,'\\n') + "',p1919:'3',p1907:''}";
                    var record = Ext.decode(strRecord);
                    store.insert(store.getCount(), record);
//					combo.reset();
					textfield.reset();
				}

			},
			scope : this
		}, map);
	},
	// 更新记录 index：1本期工作总结 3下期工作计划
	updateRecord : function(index, e) {
		var field = e.field;
		var map = new HashMap();
		map.put("type", index+'');
		map.put("option", '4');
		map.put("p1900", e.record.get('p1900'));
		map.put("field", field);
		map.put("value", e.value);
		Rpc({
			functionId : 'WP20000004',
			async : false,
			success : function(response) {
				var obj = Ext.decode(response.responseText);
				var errorcode = obj.errorcode;
				if (errorcode == '1') {
					Ext.showAlert('更新失败！');

				} else if (errorcode == '0') {// success
					var store = Ext.getCmp('grid' + index).getStore();
					store.load();
				}
			},
			scope : this
		}, map);
	},
	// 删除记录 index：1本期工作总结 3下期工作计划
	deleteRecord : function(index) {
		var grid = Ext.getCmp('grid' + index);

		if (grid.getSelectionModel().getSelection().length == 0) {
			Ext.showAlert('请选择记录再进行删除！');
			return;
		}

		Ext.showConfirm('确定要删除选中记录？', function(btn) {
			if (btn == 'yes') {
				var p1900s = new Array();

				var isHaveCantDeleteRecord = false;
				var selected = grid.getSelectionModel().getSelection();
				for (var i = 0; i < selected.length; i++) {
					var data = selected[i].data;
					var p1919 = data.p1919;
					if (p1919 == '1' || p1919 == '2') {
						isHaveCantDeleteRecord = true;
						continue;
					}

					var p1900 = data.p1900;
					p1900s.push(p1900);
				}

				var map = new HashMap();
				map.put("type", index+'');
				map.put("option", '3');
				map.put("p1900s", p1900s);
				Rpc({
					functionId : 'WP20000004',
					async : false,
					success : function(response) {
						var obj = Ext.decode(response.responseText);
						var errorcode = obj.errorcode;
						if (errorcode == '1') {
							Ext.showAlert('更新失败！');

						} else if (errorcode == '0') {// success
							var store = Ext.getCmp('grid' + index).getStore();
							store.load();

							if (isHaveCantDeleteRecord) {
								Ext.showAlert('删除的记录中有部分来自上周计划和本周日志，没有被删除！');
							}
						}
					},
					scope : this
				}, map);
			}
		}, this);
	},
	// 汇总
	collectRecord : function() {
		var map = new HashMap();
		map.put("type", '4');
		map.put("option", '0');
		map.put("p0100", this.p0100);
		map.put("cycle", this.cycle);
		map.put("year", this.year);
		map.put("month", this.month);
		map.put("week", this.week);
		Rpc({
			functionId : 'WP20000004',
			async : false,
			success : function(response) {
				var obj = Ext.decode(response.responseText);
				var errorcode = obj.errorcode;
				if (errorcode == '1') {
					return;

				} else if (errorcode == '0') {// success
					var store = Ext.getCmp('grid1').getStore();
					store.load();
				}
			},
			scope : this
		}, map);
	},
	// 展开图标点击事件 index：1本期工作总结 2下期工作计划3、下期工作计划 4、培训需求
	clickTitleIcon : function(index) {
//		var grid = Ext.getCmp("grid" + index);
//		grid.toggleCollapse();
		
		if(weeklysummary_me.showWeekPlan==0){
			weeklysummary_me.showWeekPlan=1;
			Ext.getCmp("grid" + index).setHidden(true);
			weeklysummary_me.setIcon(index, 'down');
		}else{
			weeklysummary_me.showWeekPlan=0;
			Ext.getCmp("grid" + index).setHidden(false);
			weeklysummary_me.setIcon(index, 'up');
		}
	},
	// 点击图标后重设图片路径 index：1本期工作总结 2下期工作计划3、下期工作计划 4、培训需求
	setIcon : function(index, state) {
		var img = Ext.getDom('img' + index);
		var src = '/images/new_module/expend_blue.png';
		if (state == 'up') {
			src = '/images/new_module/collapse_blue.png';

		}
		img.src = src;
	},
	// 重置页面：刷新数据、权限控制等。。
	reload : function(config) {
		if(typeof config != 'undefined'){
			this.loadParam(config);
		}
		this.setEditFunc();
		
		var extraParams1 = {
			type : '1',
			option : '1',
			p0100 : this.p0100
		}
		var store1 = Ext.getCmp("grid1").getStore();
		store1.getProxy().extraParams = extraParams1;
		store1.load();

		var extraParams2 = {
			type : '2',
			option : '1',
			p0100 : this.p0100,
			nbase  : this.nbase,
			a0100  : this.a0100
		}
		var store2 = Ext.getCmp("grid2").getStore();
		store2.getProxy().extraParams = extraParams2;
		store2.load();

		var extraParams3 = {
			type : '3',
			option : '1',
			p0100 : this.p0100
		}
		var store3 = Ext.getCmp("grid3").getStore();
		store3.getProxy().extraParams = extraParams3;
		store3.load();
		
		if (!Ext.isEmpty(this.zhouzjpx)) {
			this.initContentValue();
		}
		
		Ext.getCmp('container1').setHidden(!this.editFunc);
		Ext.getCmp('container3').setHidden(!this.editFunc);
	},
	// 设置/重置参数属性
	loadParam : function(config) {
		this.p0100 = config.p0100;
		this.cycle = config.cycle;
		this.year = config.year;
		this.month = config.month;
		this.week = config.week;
		this.isself = config.isself;
		this.p0115 = config.p0115;
		this.zhouzjpx = config.zhouzjpx;
		this.nbase = config.nbase;
		this.a0100 = config.a0100;
		this.isopentasktime = this.getIsOpentasktime();
	},
	// 编辑权限
	setEditFunc:function(){
		if (this.isself == 'me' && /*this.p0115 != '02' && */this.p0115 != '03') {// 1、是自己  2、不是报批/已批 。chent 20170314 报批时也可编辑，不然报批给领导后也没批准就没法再编辑了。 
			this.editFunc = true;
		} else {
			this.editFunc = false;
		}
	},
	// 自定义样式
	createSelfCss : function() {
		/** 文本框去除边框 */
//		if (!Ext.util.CSS.getRule('#textfield1-inputEl')) {
//			Ext.util.CSS.createStyleSheet("#textfield1-inputEl{color:#BBB1C6;}", "card_left_css");
//		}
//		if (!Ext.util.CSS.getRule('#textfield3-inputEl')) {
//			Ext.util.CSS.createStyleSheet("#textfield3-inputEl{color:#BBB1C6;}", "card_left_css");
//		}
		/** 下拉框改变风格 */
		if (!Ext.util.CSS.getRule('.abc div')) {
			Ext.util.CSS.createStyleSheet(".abc div{border-color:#ffffff;}", "card_css");
		}
		if (!Ext.util.CSS.getRule('.abc .x-form-text-default')) {
			Ext.util.CSS.createStyleSheet(".abc .x-form-text-default{color:#000000;}", "card_css");
		}
		if (!Ext.util.CSS.getRule('.abc .x-form-trigger-default')) {
			Ext.util.CSS.createStyleSheet(".abc .x-form-trigger-default{background-image: url('/images/new_module/expand.png');background-position:0px 0px;}", "card_css");
		}
		if (!Ext.util.CSS.getRule('#textfield1 .x-form-trigger-over')) {
			Ext.util.CSS.createStyleSheet("#textfield1 .x-form-trigger-over{background-image: url('/images/new_module/expand.png');background-position:0px 0px;border-color:#ffffff;}", "qweqwe");
		}
		if (!Ext.util.CSS.getRule('#textfield1 .x-form-trigger-focus')) {
			Ext.util.CSS.createStyleSheet("#textfield1 .x-form-trigger-focus{background-image: url('/images/new_module/expand.png');background-position:0px 0px;border-color:#ffffff;}", "qweqwe");
		}
		if (!Ext.util.CSS.getRule('.abc .x-form-text-wrap-default')) {
			Ext.util.CSS.createStyleSheet(".abc .x-form-text-wrap-default{border-color:#ffffff;}", "card_css");
		}
		if (!Ext.util.CSS.getRule('.abc .x-form-text-wrap-focus')) {
			Ext.util.CSS.createStyleSheet(".abc .x-form-text-wrap-focus{border-color:#ffffff;}", "card_css");
		}
		if (!Ext.util.CSS.getRule('.abc .x-form-trigger-default.x-form-trigger-over.x-form-trigger-focus')) {
			Ext.util.CSS.createStyleSheet(".abc .x-form-trigger-default.x-form-trigger-over.x-form-trigger-focus{background-position:0px 0px;}", "card_css");
		}
		if (!Ext.util.CSS.getRule('#combo1 div')) {
			Ext.util.CSS.createStyleSheet("#combo1 div{border-color:#ffffff;}", "card_css");
		}
		if (!Ext.util.CSS.getRule('#combo3 div')) {
			Ext.util.CSS.createStyleSheet("#combo3 div{border-color:#ffffff;}", "card_css");
		}
		if (!Ext.util.CSS.getRule('#textfield1 div')) {
			Ext.util.CSS.createStyleSheet("#textfield1 div{border-color:#ffffff;}", "card_css");
		}
		if (!Ext.util.CSS.getRule('#textfield3 div')) {
			Ext.util.CSS.createStyleSheet("#textfield3 div{border-color:#ffffff;}", "card_css");
		}
//		if (!Ext.util.CSS.getRule('#textfield1 .x-form-text-default')) {
//			Ext.util.CSS.createStyleSheet("#textfield1 .x-form-text-default{color:#BBB1C6;}", "card_css");
//		}
//		if (!Ext.util.CSS.getRule('#textfield1 .x-form-trigger-default')) {
//			Ext.util.CSS.createStyleSheet("#textfield1 .x-form-trigger-default{background-image: url('/images/new_module/expand.png');background-position:0px 0px;}", "card_css");
//		}
//		if (!Ext.util.CSS.getRule('#textfield1 .x-form-trigger-over')) {
//			Ext.util.CSS.createStyleSheet("#textfield1 .x-form-trigger-over{background-image: url('/images/new_module/expand.png');background-position:0px 0px;border-color:#ffffff;}", "12313");
//		}
//		if (!Ext.util.CSS.getRule('#textfield1 .x-form-trigger-focus')) {
//			Ext.util.CSS.createStyleSheet("#textfield1 .x-form-trigger-focus{background-image: url('/images/new_module/expand.png');background-position:0px 0px;border-color:#ffffff;}", "12313");
//		}
//		if (!Ext.util.CSS.getRule('#textfield1 .x-form-text-wrap-default')) {
//			Ext.util.CSS.createStyleSheet("#textfield1 .x-form-text-wrap-default{border-color:#ffffff;}", "card_css");
//		}
//		if (!Ext.util.CSS.getRule('#textfield1 .x-form-text-wrap-focus')) {
//			Ext.util.CSS.createStyleSheet("#textfield1 .x-form-text-wrap-focus{border-color:#ffffff;}", "card_css");
//		}
//		if (!Ext.util.CSS.getRule('#textfield1 .x-form-trigger-default.x-form-trigger-over.x-form-trigger-focus')) {
//			Ext.util.CSS.createStyleSheet("#textfield1 .x-form-trigger-default.x-form-trigger-over.x-form-trigger-focus{background-position:0px 0px;}", "card_css");
//		}
		
//		if (!Ext.util.CSS.getRule('.abc div')) {
//			Ext.util.CSS.createStyleSheet(".abc div{border-color:#ffffff;}", "card_css");
//		}
//		if (!Ext.util.CSS.getRule('.abc .x-form-text-default')) {
//			Ext.util.CSS.createStyleSheet(".abc .x-form-text-default{color:#BBB1C6;}", "card_css");
//		}
//		if (!Ext.util.CSS.getRule('.abc .x-form-trigger-default')) {
//			Ext.util.CSS.createStyleSheet(".abc .x-form-trigger-default{background-image: url('/images/new_module/expand.png');background-position:0px 0px;}", "card_css");
//		}
//		if (!Ext.util.CSS.getRule('.abc .x-form-trigger-over')) {
//			Ext.util.CSS.createStyleSheet(".abc .x-form-trigger-over{background-image: url('/images/new_module/expand.png');background-position:0px 0px;border-color:#ffffff;}", "12313");
//		}
//		if (!Ext.util.CSS.getRule('.abc .x-form-trigger-focus')) {
//			Ext.util.CSS.createStyleSheet(".abc .x-form-trigger-focus{background-image: url('/images/new_module/expand.png');background-position:0px 0px;border-color:#ffffff;}", "12313");
//		}
//		if (!Ext.util.CSS.getRule('.abc .x-form-text-wrap-default')) {
//			Ext.util.CSS.createStyleSheet(".abc .x-form-text-wrap-default{border-color:#ffffff;}", "card_css");
//		}
//		if (!Ext.util.CSS.getRule('.abc .x-form-text-wrap-focus')) {
//			Ext.util.CSS.createStyleSheet(".abc .x-form-text-wrap-focus{border-color:#ffffff;}", "card_css");
//		}
//		if (!Ext.util.CSS.getRule('.abc .x-form-trigger-default.x-form-trigger-over.x-form-trigger-focus')) {
//			Ext.util.CSS.createStyleSheet(".abc .x-form-trigger-default.x-form-trigger-over.x-form-trigger-focus{background-position:0px 0px;}", "card_css");
//		}
		
		
//		
//		if (!Ext.util.CSS.getRule('#textfield3 div')) {
//			Ext.util.CSS.createStyleSheet("#textfield3 div{border-color:#ffffff;}", "card_css");
//		}
//		if (!Ext.util.CSS.getRule('#textfield3 .x-form-text-default')) {
//			Ext.util.CSS.createStyleSheet("#textfield3 .x-form-text-default{color:#BBB1C6;}", "card_css");
//		}
//		if (!Ext.util.CSS.getRule('#textfield3 .x-form-trigger-default')) {
//			Ext.util.CSS.createStyleSheet("#textfield3 .x-form-trigger-default{background-image: url('/images/new_module/expand.png');background-position:0px 0px;}", "card_css");
//		}
		if (!Ext.util.CSS.getRule('#textfield3 .x-form-trigger-over')) {
			Ext.util.CSS.createStyleSheet("#textfield3 .x-form-trigger-over{background-image: url('/images/new_module/expand.png');background-position:0px 0px;border-color:#ffffff;}", "12313");
		}
		if (!Ext.util.CSS.getRule('#textfield3 .x-form-trigger-focus')) {
			Ext.util.CSS.createStyleSheet("#textfield3 .x-form-trigger-focus{background-image: url('/images/new_module/expand.png');background-position:0px 0px;border-color:#ffffff;}", "12313");
		}
//		if (!Ext.util.CSS.getRule('#textfield3 .x-form-text-wrap-default')) {
//			Ext.util.CSS.createStyleSheet("#textfield3 .x-form-text-wrap-default{border-color:#ffffff;}", "card_css");
//		}
//		if (!Ext.util.CSS.getRule('#textfield3 .x-form-text-wrap-focus')) {
//			Ext.util.CSS.createStyleSheet("#textfield3 .x-form-text-wrap-focus{border-color:#ffffff;}", "card_css");
//		}
//		if (!Ext.util.CSS.getRule('#textfield3 .x-form-trigger-default.x-form-trigger-over.x-form-trigger-focus')) {
//			Ext.util.CSS.createStyleSheet("#textfield3 .x-form-trigger-default.x-form-trigger-over.x-form-trigger-focus{background-position:0px 0px;}", "card_css");
//		}
		
		
		if (!Ext.util.CSS.getRule('#combo1 .x-form-trigger-over')) {
			Ext.util.CSS.createStyleSheet("#combo1 .x-form-trigger-over,#combo1 .x-form-trigger-focus,#combo3"+
										" .x-form-trigger-over,#combo3 .x-form-trigger-focus"+
										"{background-image: url('/images/new_module/expand.png');background-position:0px 0px;border-color:#ffffff;}", "qweqwe");
		}
		/** 空文本提示颜色被Ext样式覆盖掉了，需要还原 */
		if(!Ext.util.CSS.getRule('.emptyTextCls')){
			Ext.util.CSS.createStyleSheet(".emptyTextCls{color:#666666 !important}", "");
		}
		
		/** 分栏蓝线变成灰色 */
		if (!Ext.util.CSS.getRule('#grid1 .x-grid-group-hd')) {
			try{
				Ext.util.CSS.createStyleSheet("#grid1 .x-grid-group-hd{border-color:#C5C5C5;}", "aaa");
			}catch(e){
			}
		}
		if (!Ext.util.CSS.getRule('#grid2 .x-grid-group-hd')) {
			try{
				Ext.util.CSS.createStyleSheet("#grid2 .x-grid-group-hd{border-color:#C5C5C5;}", "bbb");
			}catch(e){
			}
		}
		if (!Ext.util.CSS.getRule('#grid3 .x-grid-group-hd')) {
			try{
				Ext.util.CSS.createStyleSheet("#grid3 .x-grid-group-hd{border-color:#C5C5C5;}", "ccc");
			}catch(e){
			}
		}
		
	},
	// 绑定事件。页面resize等。。
	bindEvent : function() {
		Ext.EventManager.onWindowResize(function() {
			if(weeklysummary_me.mainpanel){
				var contentWidth = this.getZjWidth();
				weeklysummary_me.mainpanel.setWidth(contentWidth);
			}
			
		}, this);
	},
	// 获取表格标题
	getGridTitle : function(index, itemDesc) {
		var text = itemDesc;// 标题文本
		if (index == 1) {
			text = '本期工作总结';
		} else if (index == 2) {
			text = '本期工作日志';
		} else if (index == 3) {
			text = '下期工作计划';
		}
		var src = '/images/new_module/expend_blue.png';// 图片路径
		if (index == 1) {
			src = '/images/new_module/collapse_blue.png';
		}
		//蓝#6CAFED   首钢颜色绿#CCE9AD;  --border-bottom:2px #6CAFED solid background:#F7f7f9;灰E2E2E2
		//return title = '<div style="cursor:pointer;height:26px;line-height:26px;background:#E2E2E2;" onclick="weeklysummary_me.clickTitleIcon(' + index + ');"><span style="line-height:15px;font-size:15px;">&nbsp;&nbsp;' + text + '</span><img id="img' + index + '" style="position:relative;top:-2px;margin-left:5px;cursor:pointer;background-color:#E2E2E2;" src="' + src + '" /></div>';
		//陈总提，修改标题样式
		return title = '<div style="cursor:pointer;height:26px;line-height:26px;" onclick="weeklysummary_me.clickTitleIcon(' + index + ');"><span style="line-height:15px;font-size:15px;color:#1B4A98;">&nbsp;&nbsp;' + text + '</span><img id="img' + index + '" style="width:23px;height:23px;position:relative;top:-2px;cursor:pointer;" src="' + src + '" /></div>';
	},
	// 获取表格分栏属性
	getGridFeatures : function() {
		return [ {
			ftype : 'grouping',
			groupHeaderTpl : [ '{name:this.formatName}', {
				formatName : function(name) {
					if (name == '01')
						return '例行工作';
					else if (name == '02')
						return '重点工作';
					else if (name == '03')
						return '其他工作';
				}
			}]
		} ];
	},
	// 获取表格下方的添加框 index:1本期工作总结 3下期工作计划
	getInputBbar:function(index){
		var comboStore = new Ext.data.ArrayStore({
			fields : [ 'myId', 'displayText' ],
			data : [ [ '01', '例行工作' ], [ '02', '重点工作' ], [ '03', '其他工作' ] ]
		});
		
		return [ {
			xtype : 'panel',
			id : 'container'+index,
			bodyStyle : {
				border : '1px solid #529FE5'
			},
			layout : 'hbox',
			hidden : !this.editFunc,
			border : true,
			height : 30,
			items : [ index==1?{
				xtype : 'image',
				src : '/images/refresh.png',
				style:'cursor:pointer;',
				margin : '6 0 0 10',
				width : 14,
				height : 14,
				listeners : {
					click : {
						element : 'el',
						fn : function() {
							this.collectRecord();
						}
					},
					scope : this
				}
			}:undefined, {
				xtype : 'image',
				src : '/workplan/image/chahao.png',
				style:'cursor:pointer;',
				margin : '7 10 0 10',
				width : 14,
				height : 14,
				listeners : {
					click : {
						element : 'el',
						fn : function() {
							this.deleteRecord(index);
							editSummary();
						}
					},
					scope : this
				}
			}, {
				xtype : 'container',
				layout : 'hbox',
				margin : '0 10 0 0',
				items : [ {
					xtype : 'combo',
					id : 'combo'+index,
					cls:'abc',
					fieldLabel : '',
					labelSeparator : '',
					store : comboStore,
					forceSelection : true,
					valueField : 'myId',
					displayField : 'displayText',
					editable : false,
					labelAlign : 'right',
					width : 100,
					margin : '3 0 0 0',
					allowBlank : false,
					listeners : {
						render : function(combo) {
							combo.setValue(combo.getStore().getAt(0));
						},
						scope : this
					}
				}, {
					xtype:'combo',
					id : 'textfield'+index,
					cls:'abc',
					fieldLabel : '',
					labelSeparator : '',
			    	store:this.getE01a1PlanTask(),
			        valueField: 'itemid',
			        displayField: 'itemdesc',
			        emptyCls:"emptyTextCls",	
			        emptyText:'创建任务，也可在右侧选择岗位职责进行添加',
			        width : index==1?(this.contentWidth - 34 - 24 - 100 - 24 - 10 - 20 - 20):(this.contentWidth - 34 - 100 - 24 - 10 - 20 - 20),//本期工作总结中少一个按钮，少减一个24
			        labelAlign:'left',
			        margin:'3 0 5 0',
			        onFieldMutation: function(e) {return;},
					listeners : {
//						'specialkey' : function(field, e) {// 回车添加
//							if (e.getKey() == Ext.event.Event.ENTER || e.getKey() == Ext.event.Event.TAB) {
//								var index = field.id.charAt(field.id.length - 1);
//								
//								var combo = Ext.getCmp('textfield'+index);
//								if(combo){
//									combo.blur();
//									this.addRecord(index);
//								}
//							}
//						},
						focus:function(){
							var elm = Ext.get('textfield'+index);
							var x = elm.getX();
							var y = elm.getY();
							var w = elm.getWidth();
							Ext.widget('textareafield', {
								id  : 'description',
						        fieldLabel: '',
						        labelSeparator:'',
						        labelAlign:'right',
						        floating:true,
						        //grow : false,
						        width:w,
						        height:100,
						        x:x,
								y:y-4,
								style:'z-index:2;',
								renderTo:Ext.getBody(),
								listeners:{
									render:function(){
										var defaltvalue = Ext.getCmp('textfield'+index).getRawValue();
										
										this.setValue(defaltvalue);
										this.focus(true,10);//haosl	update 20170412 文本域聚焦
										this.mon(Ext.getDoc(), {
							                mousedown: this.hiddenIf,
							                scope: this
							            });
									}
								},
								hiddenIf: function(e) {
							        if (!this.isDestroyed && !e.within(this.bodyEl, false, true) && !this.owns(e.target)) {
							        	var value = this.getValue();//大文本值
							        	
							        	var combo = Ext.getCmp('textfield'+index);
                                        combo.setValue(Ext.isEmpty(value)?undefined:value);
                                        combo.setRawValue(Ext.isEmpty(value)?undefined:value);
                                        
							        	Ext.destroy(this);
							        }
							    }
							});
						},
						scope : this
					}
				}
//				{
//					xtype : 'textfield',
//					id : 'textfield'+index,
//					width : index==1?(this.contentWidth - 34 - 24 - 100 - 24 - 10 - 20 - 20):(this.contentWidth - 34 - 100 - 24 - 10 - 20 - 20),//本期工作总结中少一个按钮，少减一个24
//					maxLength : 50,
//					height : 26,
//					maxLengthText : '最大50位',
//					emptyText : '创建任务',
//					margin : '1 10 0 0',
//					listeners : {
//						'specialkey' : function(field, e) {// 回车添加
//							if (e.getKey() == Ext.EventObject.ENTER) {
//								var index = field.id.charAt(field.id.length - 1);
//								this.addRecord(index);
//							}
//						},
//						scope : this
//					}
//				} 
				]
			}, {
				xtype:'panel',
				width:30,
				border:false,
				bodyStyle:'background-color:#529FE5;color:#ffffff;',
				html:'<div style="width:100%;text-align:center;cursor:pointer;"><span style="font-family:\'微软雅黑\' !important; position:relative;top:-4px;font-size:x-large;">+</span><div>',
				items:[],
				listeners : {
					click : {
						element : 'el',
						fn : function() {
							this.addRecord(index);
							editSummary();
						}
					},
					scope : this
				}
			} ]
		} ]
	},
	// 工作任务子集
	getE01a1PlanTask : function() {

		return Ext.create('Ext.data.Store', {
			fields:['itemid', 'itemdesc'],
			proxy : {
				type : 'transaction',
				functionId : 'WP20000004',
				extraParams : {
					type : '6',
					option : '1',
					nbase  : this.nbase,
					a0100  : this.a0100
				},
				reader : {
					type : 'json',
					root : 'data'
				}
			},
			autoLoad : true
		});
	},
	// 获取培训需求
	initContentValue:function(){
		var objArray = this.zhouzjpx.split(',');
		for(var i=0; i<objArray.length; i++){
			var obj = objArray[i];
			var itemArray = obj.split(':');
			var itemId = itemArray[0];
			
			var id = i+4;//前面有三个列表，分别是grid1、2、3，这里就从4开始。
			
			var map = new HashMap();
			map.put("type", '5');
			map.put("option", '1');
			map.put("field", itemId);
			map.put("p0100", this.p0100);
			Rpc({
				functionId : 'WP20000004',
				async : false,
				success : function(response) {
					var obj = Ext.decode(response.responseText);
					
					var textareafield = Ext.getCmp('content_'+id);
					if(textareafield){
						textareafield.setValue(obj.data);
						textareafield.setDisabled(!this.editFunc);
					}
				},
				scope : this
			}, map);
		}
	},
	// 更新培训需求
	updateContentValue:function(){
		var objArray = this.zhouzjpx.split(',');
		for(var i=0; i<objArray.length; i++){
			var obj = objArray[i];
			var itemArray = obj.split(':');
			var itemId = itemArray[0];
			if(Ext.isEmpty(itemId)){
				continue ;
			}
			var id = i+4;//前面有三个列表，分别是grid1、2、3，这里就从4开始。
			
			var value = '';
			var textareafield = Ext.getCmp('content_'+id);
			if(textareafield){
				value = textareafield.getValue();
			}
			
			var map = new HashMap();
			map.put("type", '5');
			map.put("option", '2');
			map.put("p0100", this.p0100);
			map.put("field", itemId);
			map.put("value", value);
			Rpc({
				functionId : 'WP20000004',
				async : false,
				success : function(response) {},
				scope : this
			}, map);
		}
	},
	// 是否启用计时
	getIsOpentasktime : function(){
		var isopentasktime = 'false';
		
		var map = new HashMap();
		map.put("type", '2');
		map.put("option", '2');
		map.put("nbase", this.nbase);
		map.put("a0100", this.a0100);
		Rpc({
			functionId : 'WP20000004',
			async : false,
			success : function(response) {
				var obj = Ext.decode(response.responseText);
				isopentasktime = obj.isopentasktime;
			},
			scope : this
		}, map);
		
		return isopentasktime;
	},
	//内容全部展开
	addGridCssMemo : function(value, cell, record, rowIndex, columnIndex, store) {
        cell.style = "font-size:14px;float:left;padding:5px;white-space: normal;word-wrap: break-word;line-height:20px;";
        if (Ext.isEmpty(value))
            return value;
        else{
            var titleVal = value;
            value = value.replace(/</g,'&lt;').replace(/\n/g,'<br>').replace(/ /g,'&nbsp;');
            return '<div title="'+titleVal+'">'+value+'</div>';
        }
    },
    //计算总结表格的宽度
    getZjWidth : function(){
    	var display = Ext.getDom("rightDiv").style.display;
    	var bodyWidth = Ext.getBody().getWidth()*0.98;
    	if(display == "inline"){
    		return bodyWidth-164;
    	}else{
    		return bodyWidth-20;
    	}
    }
});