/**
	@title 工作日志
	@author linbz
	@time 2016-11-30 11:47:54
*/
Ext.define("WorkLogURL.WorkLog",{
	
	requires:['EHR.extWidget.proxy.TransactionProxy','EHR.extWidget.field.DateTimeField','EHR.extWidget.field.BigTextField'],
	
	constructor:function(obj){
		worklog_me = this;
		worklog_me.isSelf = '0';
		worklog_me.nbase = obj?obj.nbase:"";
    	worklog_me.a0100 = obj?obj.a0100:"";
    	worklog_me.name = "";
		worklog_me.p0100 = null;
		worklog_me.p0115 = null;//审批标志
		worklog_me.flag = null;//补填标识 0:正常填写 1:补填
		worklog_me.section = null;//参数设置填写期限
		worklog_me.nowMonth = obj?obj.nowMonth:"";//切换月份
		worklog_me.datelist = null;//某个月的全部日期
		worklog_me.oneday = null;//选中的某天
		worklog_me.oneClassId = "";//选中当天的班次id
		worklog_me.sumdata = null;//汇总数据
		worklog_me.sumflag = null;//汇总标示=0月 =1周
		worklog_me.e01a1PlanTask = null;//工作任务子集
		worklog_me.nowday = new Date();//当前时间
		worklog_me.taskTime = false;//耗时相关列是否隐藏
		worklog_me.editFunc = false;//不可编辑别人的日志,并隐藏编辑工具
		worklog_me.showMap = true;//是否隐藏人力地图
		worklog_me.nodelist = false;//是否有下属
		worklog_me.isselfuser = false;//是否自助用户
		worklog_me.workplan = '';//周计划列头
		worklog_me.contentWidth = 0;
		worklog_me.cenlogWidth = 0;//中间内容
		worklog_me.employeflag = obj?obj.employeflag:"";//监控标示=1为从监控进来
		this.init();
		if(worklog_me.isselfuser)
			return;
		this.createSelfCss(); // 自定义样式
		
		if(worklog_me.employeflag != "1"){
			wpm = {};
			wpm.super_concerned_objs = [];
			this.initPlan();//初始人力地图
		}
    },
    
    createSelfCss : function() {
    	/** 分栏蓝线变成灰色 */
		if (!Ext.util.CSS.getRule('#loggrid .x-grid-group-hd')) {
			Ext.util.CSS.createStyleSheet("#loggrid .x-grid-group-hd{border-width:0 0 1px 0;border-color:#C5C5C5;}", "card_css");
		}
		if (!Ext.util.CSS.getRule('#weekgrid .x-grid-group-hd')) {
			Ext.util.CSS.createStyleSheet("#weekgrid .x-grid-group-hd{border-width:0 0 1px 0;border-color:#C5C5C5;}", "card_css");
		}
		
		//起始时间
		if (!Ext.util.CSS.getRule('#startTimeid div')) {
			Ext.util.CSS.createStyleSheet("#startTimeid div{border-color:#ffffff;}", "card_css");
		}
		if (!Ext.util.CSS.getRule('#startTimeid .x-form-text-default')) {
			Ext.util.CSS.createStyleSheet("#startTimeid .x-form-text-default{color:#000000;}", "card_css");
		}
		if (!Ext.util.CSS.getRule('#startTimeid .x-form-trigger-default')) {
			Ext.util.CSS.createStyleSheet("#startTimeid .x-form-trigger-default{background-image: url('/images/new_module/expand.png');background-position:0px 0px;}", "card_css");
		}
		if (!Ext.util.CSS.getRule('#startTimeid .x-form-trigger-over')) {
			Ext.util.CSS.createStyleSheet("#startTimeid .x-form-trigger-over{background-image: url('/images/new_module/expand.png');background-position:0px 0px;border-color:#ffffff;}", "12313");
		}
		
		if (!Ext.util.CSS.getRule('#endTimeid div')) {
			Ext.util.CSS.createStyleSheet("#endTimeid div{border-color:#ffffff;}", "endTimeid_css");
		}
		if (!Ext.util.CSS.getRule('#endTimeid .x-form-text-default')) {
			Ext.util.CSS.createStyleSheet("#endTimeid .x-form-text-default{color:#000000;}", "endTimeid_css");
		}
		if (!Ext.util.CSS.getRule('#endTimeid .x-form-trigger-default')) {
			Ext.util.CSS.createStyleSheet("#endTimeid .x-form-trigger-default{background-image: url('/images/new_module/expand.png');background-position:0px 0px;}", "endTimeid_css");
		}
		if (!Ext.util.CSS.getRule('#endTimeid .x-form-trigger-over')) {
			Ext.util.CSS.createStyleSheet("#endTimeid .x-form-trigger-over{background-image: url('/images/new_module/expand.png');background-position:0px 0px;border-color:#ffffff;}", "endTimeid_css");
		}
		
		/** 下拉框改变风格 */
		if (!Ext.util.CSS.getRule('#workTypeid div')) {
			Ext.util.CSS.createStyleSheet("#workTypeid div{border-color:#ffffff;}", "card_css");
		}
		if (!Ext.util.CSS.getRule('#workTypeid .x-form-text-default')) {
			Ext.util.CSS.createStyleSheet("#workTypeid .x-form-text-default{color:#000000;}", "card_css");
		}
		if (!Ext.util.CSS.getRule('#workTypeid .x-form-trigger-default')) {
			Ext.util.CSS.createStyleSheet("#workTypeid .x-form-trigger-default{background-image: url('/images/new_module/expand.png');background-position:0px 0px;}", "card_css");
		}
		if (!Ext.util.CSS.getRule('#workTypeid .x-form-trigger-over')) {
			Ext.util.CSS.createStyleSheet("#workTypeid .x-form-trigger-over{background-image: url('/images/new_module/expand.png');background-position:0px 0px;border-color:#ffffff;}", "12313");
		}
		if (!Ext.util.CSS.getRule('#workTypeid .x-form-trigger-focus')) {
			Ext.util.CSS.createStyleSheet("#workTypeid .x-form-trigger-focus{background-image: url('/images/new_module/expand.png');background-position:0px 0px;border-color:#ffffff;}", "12313");
		}
		if (!Ext.util.CSS.getRule('#workTypeid .x-form-text-wrap-default')) {
			Ext.util.CSS.createStyleSheet("#workTypeid .x-form-text-wrap-default{border-color:#ffffff;}", "card_css");
		}
		if (!Ext.util.CSS.getRule('#workTypeid .x-form-text-wrap-focus')) {
			Ext.util.CSS.createStyleSheet("#workTypeid .x-form-text-wrap-focus{border-color:#ffffff;}", "card_css");
		}
		if (!Ext.util.CSS.getRule('#workTypeid .x-form-trigger-default.x-form-trigger-over.x-form-trigger-focus')) {
			Ext.util.CSS.createStyleSheet("#workTypeid .x-form-trigger-default.x-form-trigger-over.x-form-trigger-focus{background-position:0px 0px;}", "card_css");
		}
		
		
		if (!Ext.util.CSS.getRule('#workContentid div')) {
			Ext.util.CSS.createStyleSheet("#workContentid div{border-color:#ffffff;}", "card_css");
		}
		//这样不是空文本的时候字体也显示成了灰色,暂时删掉  haosl delete 2018-2-6
		/*if (!Ext.util.CSS.getRule('#workContentid .x-form-text-default')) {
			Ext.util.CSS.createStyleSheet("#workContentid .x-form-text-default{color:#888;}", "card_css");
		}*/
		if (!Ext.util.CSS.getRule('#workContentid .x-form-trigger-default')) {
			Ext.util.CSS.createStyleSheet("#workContentid .x-form-trigger-default{background-image: url('/images/new_module/expand.png');background-position:0px 0px;}", "card_css");
		}
		
		
	},
	init:function(){
    	
		var map = new HashMap();
		map.put("flag","all");
//	    map.put("self","self");
		map.put("nbase",worklog_me.nbase);
		map.put("a0100",worklog_me.a0100);
		if(worklog_me.employeflag == "1"){
			map.put("nowMonth",worklog_me.nowMonth);
			map.put("employeflag","1");
		}
	    
		Rpc({functionId:'WP30000001',async:false,success:this.getWorkLogOK,scope:this},map);
    },
    
    getWorkLogOK : function(form){
    	var result = Ext.decode(form.responseText);
    	if(result.error == 1){
    		worklog_me.isselfuser = true;
    		Ext.Msg.alert('提示信息',"非自助用户不能使用该功能！");
			return;
    	}
    	if(result.power == 1){
    		worklog_me.isselfuser = true;
    		Ext.Msg.alert('提示信息',"未启用日志功能！");
			return;
    	}
    	worklog_me.section = result.section;
    	if(worklog_me.section == ''){
    		worklog_me.isselfuser = true;
    		Ext.Msg.alert('提示信息',"未启用日志功能！");
			return;
    	}
    	worklog_me.isSelf = result.isSelf;
    	worklog_me.nbase = result.nbase;
    	worklog_me.a0100 = result.a0100;
    	
    	worklog_me.p0100 = result.p0100;
    	worklog_me.p0115 = result.p0115;
    	worklog_me.flag = result.flag;
    	
    	worklog_me.nowMonth = result.nowMonth;
    	worklog_me.datelist = result.dates;
    	//监控
    	if(worklog_me.employeflag == "1"){
    		worklog_me.oneday = worklog_me.datelist[0];
		}
	    
    	worklog_me.codelist = result.codelist;
    	worklog_me.sumdata = result.sumjo;
    	worklog_me.fillstate = result.fillstate;
    	worklog_me.e01a1PlanTask = result.dataE01a1;
    	
    	if(result.weekplan == 0)
    		worklog_me.workplan = '本周无工作计划';
    	else
    		worklog_me.workplan = '计划任务';
    	
    	var taskTimeflag = result.taskTime;
    	if(taskTimeflag == '2')
    		worklog_me.taskTime = true;
    	else
    		worklog_me.taskTime = false;
    	
		var time=Ext.Date.format(worklog_me.nowday, 'Y.m.d');
		worklog_me.oneday = time;
		worklog_me.sumflag = 0;
		
		worklog_me.cenlogWidth = (Ext.getBody().getWidth()*0.75-154-15-15-3-10);
		if(worklog_me.cenlogWidth < 400)
			worklog_me.cenlogWidth = 400;
		
		worklog_me.viewPanel = Ext.create('Ext.panel.Panel', {
			id: 'panelid',
			border: false,
			layout:'hbox',//水平布局
			scrollable:true,//'y',
			autoHeight: true,//自动高度
			items: [
			    {//左
			        title: false,
			        border: false,
			        width: '25%',
			        margin: '5 0 0 5',
			        minWidth : 300,
			        items:[{
							xtype: 'panel',
							border: false,
							layout: 'vbox',
							width: '98%',
							height: '100%',
							items:[{
							   id: 'calview',
							   xtype : 'label',
							   width: '100%',
							   border: false,
							   html:worklog_me.calview()
						   },{
							   id: 'fillsum',
							   xtype : 'label',
							   width: '100%',
							   border: false,
							   margin: '8 0 0 0',
							   html:worklog_me.fillingSum()
						   },{
							   	id: 'dropdownid',
							   	xtype : 'label',
							   	width: '100%',
							   	height : 36,
							   	border: false,
							   	margin: '8 0 0 0',
							   	layout:{
							   		align:'center'
						   		},
						   		html: worklog_me.dropMonthWeek()
						   },{
							   xtype:'panel',	
							   id: 'pieid',
							   width: '100%',
							   border: false,
							   margin: '8 0 0 0',
							   items:[
							          worklog_me.createPieChart()
							          ]
						   }]
						}]
			    }, {
				    width: 5,
				    height: '100%',
				    border: false,
					xtype : 'label',
					html : "<div style='position: absolute;height:100%;width:0px;border-right:solid 1px #EBEBEB;margin:30px 0 0 0;'></div>  "
				},{//中
			    	id: 'cenlog',
			        title: false,
			        xtype: 'panel',
			        width : worklog_me.cenlogWidth,
			        minWidth : 400,
			        margin: '0 0 0 5',
			        border: false,
			        items:[{
						id : 'titlehtml',
						border: false,
	        			xtype : 'label',
	        			margin: '3 0 0 0',
	        			layout: 'fit',
    	        		html:worklog_me.logTitle('','','',worklog_me.name)
	        		},{
			        	id: 'content',
				        title: false,
				        border: false,
				        xtype: 'panel',
				        margin: '5 0 0 0',
				        items :[{
								id : 'logContents',
								title: false,
								border: false,
	    	        			xtype : 'panel',
	    	        			margin: '0 0 0 0',
	    	        			minHeight : 300,
	    	        			layout : 'fit',
	    	        			items :[
	    	        			        worklog_me.logContent()
	    	        			        ]
								},
								worklog_me.getInputBbar(),
								worklog_me.getButtons(),
								{
									border: false,
				        			xtype : 'label',
				        			margin: '5 0 0 0',
				        			layout: 'fit',
			    	        		html:worklog_me.getWeekTitle()
								},
								{
									id : 'weekPlanid',
									title: false,
									border: false,
		    	        			xtype : 'panel',
		    	        			margin: '0 0 0 0',
		    	        			layout : 'fit',
		    	        			items :[
		    	        			        worklog_me.getWeekgrid()
		    	        			        ]
									}
		    	        		]
				    }]
			    },{//右
			    	id : 'hrmap',
			        title: false,
			        border: false,
			        xtype: 'panel',
			        margin: '1 0 0 9',
			        hidden : true,
			        items:[{
						id : 'showmapimg',
						border: false,
						hidden : worklog_me.showMap,
	        			xtype : 'label',
	        			margin: '2 0 0 0',
    	        		html:'<img src="/module/system/questionnaire/images/directingleft.png" style="cursor:pointer;" width="25px;" height="25px;" onclick="worklog_me.showMapmain(0);" title="显示人力地图">'
	        		},{
						id : 'mapimg',
						width: 154,
						hidden : !worklog_me.showMap,
						border: false,
	        			xtype : 'label',
    	        		html:worklog_me.hrMapimg()
	        		},{
	        			id : 'map',
	        			width: 154,
	        			hidden : !worklog_me.showMap,
						border: false,
	        			xtype : 'component',
    	        		html:worklog_me.hrMap()
	        		}]
			    }],
			    listeners:{
	    			'resize':function(){
						if (worklog_me.nodelist) {
							if(Ext.getBody().getWidth()*0.25 < 300){
								worklog_me.cenlogWidth = (Ext.getBody().getWidth()-300-154-30-16);
							}else{
								worklog_me.cenlogWidth = (Ext.getBody().getWidth()*0.75-154-30-16);
							}
							if(worklog_me.cenlogWidth < 400)
								worklog_me.cenlogWidth = 400;
							
							if(!worklog_me.showMap){
								if(Ext.getBody().getWidth() < 850){
									worklog_me.cenlogWidth = (Ext.getBody().getWidth()-300-60);
									if(worklog_me.cenlogWidth < 400)
										worklog_me.cenlogWidth = 400;
								}else{
									worklog_me.cenlogWidth = worklog_me.cenlogWidth + 135;
								}
							}
							if(worklog_me.taskTime){
					    		worklog_me.cenlogWidth = worklog_me.cenlogWidth - 5;
					    	}
						}else{
							if(Ext.getBody().getWidth()*0.25 < 300){
								worklog_me.cenlogWidth = (Ext.getBody().getWidth()-300-154-15+132+26-22);	
							}else{
								worklog_me.cenlogWidth = (Ext.getBody().getWidth()*0.75-154-15+132+26-22);	
							}
							if(worklog_me.cenlogWidth < 400)
				    			worklog_me.cenlogWidth = 400;
						}
						Ext.getCmp('content').setWidth(worklog_me.cenlogWidth);
						Ext.getCmp('cenlog').setWidth(worklog_me.cenlogWidth);
					}
	    		},
			    renderTo: Ext.getBody()
		});
		//监控
    	if(worklog_me.employeflag == "1"){
    		worklog_me.editFunc = true;
    		Ext.getCmp('editbar').setHidden(worklog_me.editFunc);
			Ext.getCmp('editbuts').setHidden(worklog_me.editFunc);
		}else{
			worklog_me.getOperateButton();
		}
    	if(document.getElementById(worklog_me.oneday)){
    		document.getElementById(worklog_me.oneday).setAttribute("class", "border_bottom");
    	}
    	
		this.mainPanel  = Ext.widget("viewport",{
			  layout:'fit',
			  id:"mainPanel1",
			  items:worklog_me.viewPanel
			});
    },
  
    //日志内容
    logContent : function(){
    	
    	var loggrid = Ext.create('Ext.grid.Panel', {
			id:'loggrid',
			title : false,
			border : 1,
			width : '100%',
			selModel:Ext.create('Ext.selection.CheckboxModel',{mode:"SIMPLE"}),
			store : worklog_me.getLogData(),
			viewConfig: {
                enableTextSelection: true
            },
			features : worklog_me.getGridFeatures(),
			columns : [{
						header : '工作内容',
						dataIndex : 'content',
						editor : Ext.widget('bigtextfield',{}),
						menuDisabled : true,
						sortable : false,
						renderer :worklog_me.addGridCssMemo,
						flex : 3
					}, {
						header : '完成情况',
						dataIndex : 'finish_desc',
						editor : Ext.widget('bigtextfield',{}),
						menuDisabled : true,
						sortable : false,
						renderer :worklog_me.addGridCssMemo,
						flex : 2
					}, {
						header : '起始时间',
						dataIndex : 'start_time',
						menuDisabled : true,
						sortable : true,
						editor : this.getTextfield,
						hidden : worklog_me.taskTime,
//						renderer :function(value) {
//							return value.substring(11);
//						},
						flex : 1.8
					}, {
						header : '截止时间',
						dataIndex : 'end_time',
						menuDisabled : true,
						sortable : false,
						editor : this.getTextfield,
						hidden : worklog_me.taskTime,
//						renderer :function(value) {
//							return value.substring(11);
//						},
						flex : 1.8
					}, {
						header : '耗时（分钟）',
						dataIndex : 'work_time',
						editor : this.getTextfield,
						hidden : worklog_me.taskTime,
						align : 'right',
						menuDisabled : true,
						sortable : false,
						scope : this,
						flex : 1.5
					}, {
						header : '备注',
						dataIndex : 'other_desc',
						editor : Ext.widget('bigtextfield',{}),
						menuDisabled : true,
						sortable : false,
						renderer :worklog_me.addGridCssMemo,
						scope : this,
						flex : 2
					},{
						header : '行号',
						dataIndex : 'Record_num',
						hidden: true
					}],
				plugins : [{
					ptype : 'cellediting',
					clicksToEdit : 1,
					listeners : {
						beforeedit : function(editor, e) {
							if(worklog_me.p0115=='02' || worklog_me.p0115=='03' || worklog_me.editFunc){
								e.cancel = true;
								return;
							}
						},
						validateedit : function(editor, e) {//保存
							if(e.originalValue == e.value){
								return ;
							}
							//工作任务内容不能为空
							if (e.field=="content"){
                                if(Ext.isEmpty(e.value.replace(/\s*/g,""))){
                                    e.cancel = true;
                                    e.value = e.originalValue;
                                    Ext.showAlert("工作内容不能为空！");
                                }
                            }
							if(e.field == 'work_time'){//耗时只能为数字
								var reg = new RegExp("^(0|[1-9][0-9]*)$");
                                if (!reg.test(e.value)) {
                                    e.cancel = true;
                                    return;
                                }
							}
							if(e.field == 'start_time' || e.field == 'end_time'){//时间
								var reg = new RegExp("^([0-9]{2}\:[0-9]{2})$");
								if (!reg.test(e.value)) {
									e.cancel = true;
									Ext.Msg.alert('提示信息',"日期格式错误！");
									return;
								}else{
									/*var dt1 = Ext.Date.parse(e.value.replace(/-|\./g,"/"), "Y/m/d H:i");
									var d1 = dt1.getTime();
									var d2 = worklog_me.nowday.getTime();
									if((d1-d2) > 0){
										e.cancel = true;
										Ext.Msg.alert('提示信息',"日期不能超过当前时间！");
										return;
									}*/
								}
							}
							worklog_me.updateRecord(e);
						},
						scope:this
					}
				}],
			listeners:{
				'resize':function(){
		    		
		    		if(!worklog_me.taskTime){
		    			worklog_me.contentWidth = worklog_me.cenlogWidth-100-76-76-32;
			    	}else{
			    		worklog_me.contentWidth = worklog_me.cenlogWidth-100-32;
			    	}
		    		Ext.getCmp('workContentid').setWidth(worklog_me.contentWidth);//kuandu
				},
				'beforegroupclick':function(){
					Ext.getCmp('workTypeid').focus();
				},
				scope : this
			}
		});
    	return loggrid;
    },
    
    getTextfield : function() {
		return Ext.widget('textfield', {});
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
			} ]
		} ];
	},
 // 工作日志Store
	getLogData : function(p0100) {

		worklog_me.logStore = Ext.create('Ext.data.Store', {
					//			id:'storey',
					fields : ['content', 'finish_desc', 'start_time','Record_num',
							'end_time', 'work_time', 'other_desc', 'work_type'],
					groupField : 'work_type',
					proxy : {
						type : 'transaction',
						functionId : 'WP30000001',
						extraParams : {
							flag : 'onedaylog',
							datevalue : worklog_me.oneday,
							nbase : worklog_me.nbase,
							a0100 : worklog_me.a0100
						},
						reader : {
							type : 'json',
							root : 'tabledata'
						}
					},
					autoLoad : true
				});
		return worklog_me.logStore;
	},
	
	//weekplan本周工作计划
	// 获取表格标题
	getWeekTitle : function() {
		var title = '<span style="font-size:14px;color:#979797;"><b>本周工作计划</b></span><img id="weekimg" style="position:relative;margin-left:5px;cursor:pointer;" src="/images/new_module/expand.png" onclick="worklog_me.clickTitleIcon();"/>';

		return title;
	},
	// 展开图标点击事件 
	clickTitleIcon : function() {
//		var grid = Ext.getCmp("weekgrid");
//		grid.toggleCollapse();
		if(worklog_me.showWeekPlan==0){
			worklog_me.showWeekPlan=1;
			Ext.getCmp('weekgrid').setHidden(true);
			worklog_me.setIcon('down');
		}else{
			worklog_me.showWeekPlan=0;
			Ext.getCmp('weekgrid').setHidden(false);
			worklog_me.setIcon('up');
		}
	},
	//本周工作计划表格
	getWeekgrid : function() {
		var weekgrid =  Ext.create('Ext.grid.Panel', {
			id : 'weekgrid',
			title : false,
			store : worklog_me.getWeekData(),
			hidden : true,
			border : 1,
			features : worklog_me.getGridFeatures(),
			columns : [ {
				id : 'planName',
				text : worklog_me.workplan,
				dataIndex : 'p1903',
				menuDisabled : true,
				sortable : false,
				flex : 1
			}],
			listeners : {
				'beforeexpand' : function(o) {
					worklog_me.setIcon('up');// 收起按钮
				},
				'beforecollapse' : function(o) {
					worklog_me.setIcon('down');// 展开按钮
				},
				scope : this
			}
		});
		return weekgrid;
	},
	//本周工作计划收起展开事件
	setIcon:function(state){
		var img = Ext.getDom('weekimg');
		var src = '/images/new_module/expand.png';
		if(state == 'up'){
			src = '/images/new_module/collapse.png';
			
		}
		img.src = src;
	},
	//本周工作计划store
	getWeekData : function() {

		var weekstore = Ext.create('Ext.data.Store', {
					//			id:'storey',
					fields : ['p1901', 'p1903'],
					groupField : 'p1901',
					proxy : {
						type : 'transaction',
						functionId : 'WP30000001',
						extraParams : {
							flag : 'oneweek',
							datevalue : worklog_me.oneday,
							nbase : worklog_me.nbase,
							a0100 : worklog_me.a0100
						},
						reader : {
							type : 'json',
							root : 'weekdata'
						}
					},
					autoLoad : true
				});
		return weekstore;
	},
	
    
    //切换月份
    getSessiondata:function (form){
	      var mdate = document.getElementById('nowMoth').innerHTML;
	      mdate += "-01";
	      var dt = new Date(mdate.replace(/-|\./g,"/"));
//	      var curDate = new Date();
	      var nowtime=Ext.Date.format(worklog_me.nowday, 'Y-m');
	      var date = null;
	      if(form == 0){
	    	  date = Ext.Date.add(dt, Ext.Date.MONTH, -1);  
	      }else if(form == 1){
	    	  if(mdate == (nowtime+"-01"))
	    		  return;
	    	  date = Ext.Date.add(dt, Ext.Date.MONTH, 1);
	    	  
	      }
	      var time=Ext.Date.format(date, 'Y-m');
//	      document.getElementById('nowMoth').innerHTML = time;
	      
	      var map = new HashMap();
	      map.put("nowMonth",time);
	      map.put("flag","duration");
//	      map.put("self","self");
	      map.put("nbase",worklog_me.nbase);
	      map.put("a0100",worklog_me.a0100);
	      Rpc({functionId:'WP30000001',async:false,success:this.loadCalview,scope:this},map);
	  },
	
	//切换月份回调函数
	loadCalview : function (form){
		  var result = Ext.decode(form.responseText);
		  worklog_me.nowMonth = result.nowMonth;
		  worklog_me.datelist = result.dates;
		  worklog_me.sumdata = result.sumjo;
		  worklog_me.fillstate = result.fillstate;
		  worklog_me.sumflag = 0;
		  
		  Ext.getCmp('calview').setHtml(worklog_me.calview());
		  Ext.getCmp('fillsum').setHtml(worklog_me.fillingSum());
		  
		  Ext.getCmp('dropdownid').setHtml(worklog_me.dropMonthWeek());
		  Ext.getCmp('pieid').removeAll();
		  Ext.getCmp('pieid').add(worklog_me.createPieChart());
		  
	  },
	  
    //日志标头
    logTitle : function(year, month, day, name){
		  if(name==null || name.length==0 || name==undefined){
			  name = "";
		  }else{
			  name = name+"的";
		  }
		  if(year==null || year.length==0){
				var time=Ext.Date.format(worklog_me.nowday, 'Y.m.d');
				year = time.substring(0, 4);
				month = time.substring(5, 7);
				day = time.substring(8);
			}
			
    	var titlehtml = "<div align='left' style='width:100%;margin:5px 0 0 0;font-size: 15px;' ><b><span style='color:#979797;'>"+year+"年"+month+"月"+day+"日</span>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"+name+"工作日志";
		if(worklog_me.flag!=0 && (worklog_me.p0115=='02' || worklog_me.p0115=='03' )){
			titlehtml += "（补填）";
		}	
    	titlehtml += "</b></div>";
    	
    	return titlehtml;
    },
    
    
    //人力地图
    hrMap : function(){
    	var hrmaphtml = "";
    		hrmaphtml += "<div class='hj-wzm-all-right' id='rightDiv' style='margin-top: 0px;'>";
				hrmaphtml += "<dl class='hj-right-dl' style='background: #F8F8F8;' id='concerneddivx'>";
					hrmaphtml += "<dt>";
						hrmaphtml += "<a href='javascript:worklog_me.loadMyPlan()'><img class='img-circle' id='my_image' /></a>";
					hrmaphtml += "</dt>";
					hrmaphtml += "<dd>";
						hrmaphtml += "<div id='concerneddiv' style='background:#F8F8F8;width:100px; height:32px;border-bottom:0px #D5D5D5 solid;line-height:24px;text-align:center; margin-left:5px; margin-top:18px;'>";
							hrmaphtml += "<a id='concernedtitle' dropdownName='dropdownBox' style='color: #549FE3;';>团队成员</a>";
						hrmaphtml += "</div>";
					hrmaphtml += "</dd>";
				hrmaphtml += "</dl>";
				hrmaphtml += "<div id='xshangjpg' class='hj-wzm-right-xshang' align='center' style='margin:0 auto; display: none;'>";
					hrmaphtml += "<a href='javascript:worklog_me.upConcerneders()'><img src='/workplan/image/xshang.jpg' /></a>";
				hrmaphtml += "</div>";
				hrmaphtml += "<div id='backSuperDiv' style='margin-left:50px;'>";
					hrmaphtml += "<a href='javascript:worklog_me.backSuper()'>返回上级 </a>";
				hrmaphtml += "</div>";
				hrmaphtml += "<div id='concernedersdiv' class='hj-wzm-right-dllb'></div>";
				hrmaphtml += "<div class='hj-wzm-right-xxia' align='center' id='xxiajpg'>";
					hrmaphtml += "<a href='javascript:worklog_me.downConcerneders()'><img src='/workplan/image/xxia.jpg' /></a>";
				hrmaphtml += "</div>";
			hrmaphtml += "<div  style='height:10px;'></div>";
			hrmaphtml += "</div>";
			
			return hrmaphtml;
    },
    //人力地图隐藏显示图标
    hrMapimg : function(){
    	var imghtml = "";
    	imghtml += "<div align='right' style='width:100%;' >";
    	imghtml += '<img src="/module/system/questionnaire/images/directingright.png" onclick="worklog_me.showMapmain(1);" style="cursor:pointer;" width="25px;" height="25px;" title="隐藏人力地图">';
    	imghtml += "</div>";
    	
    	return imghtml;
    },
    //隐藏显示人力地图
    showMapmain : function(show){
    	if(show == 0){
    		worklog_me.showMap = true;
    		Ext.getCmp('showmapimg').setHidden(true);
    		Ext.getCmp('mapimg').setHidden(false);
    		Ext.getCmp('map').setHidden(false);
    		if(Ext.getBody().getWidth()*0.25 < 300){
    			worklog_me.cenlogWidth = (Ext.getBody().getWidth()-300-154-30-16);
    		}else{
    			worklog_me.cenlogWidth = (Ext.getBody().getWidth()*0.75-154-30-16);       
    		}
    		
    		if(worklog_me.cenlogWidth < 400)
    			worklog_me.cenlogWidth = 400;
    		Ext.getCmp('cenlog').setWidth(worklog_me.cenlogWidth);
    		Ext.getCmp('content').setWidth(worklog_me.cenlogWidth);
    		Ext.getCmp('hrmap').setWidth(154);
    		
    	}else if(show == 1){
    		worklog_me.showMap = false;
    		Ext.getCmp('showmapimg').setHidden(false);
    		Ext.getCmp('mapimg').setHidden(true);
    		Ext.getCmp('map').setHidden(true);
    		if(Ext.getBody().getWidth()*0.25 < 300){
    			worklog_me.cenlogWidth = (Ext.getBody().getWidth()-300-154-15-15+132-16);
    		}else{
    			worklog_me.cenlogWidth = (Ext.getBody().getWidth()*0.75-154-15-15+132-16);       
    		}
    		if(worklog_me.cenlogWidth < 400)
    			worklog_me.cenlogWidth = 400;
    		Ext.getCmp('cenlog').setWidth(worklog_me.cenlogWidth);
    		Ext.getCmp('content').setWidth(worklog_me.cenlogWidth);
    		Ext.getCmp('hrmap').setWidth(26);
    	}
    	
    },
    

    //编辑工具条
    getInputBbar: function(){
    	var typeStore = Ext.create('Ext.data.Store', {
    	    fields: ['codeitem', 'codename'],
    	    data : worklog_me.codelist
    	});
    	var contentStore = Ext.create('Ext.data.Store', {
    	    fields: ['itemid', 'itemdesc'],
    	    data : worklog_me.e01a1PlanTask
    	});

    	var panel = Ext.create("Ext.panel.Panel",{
    		id : 'editbar',
    		width : '100%', 
			border : 1,
    		bodyPadding : 0,
    		bodyStyle : {
				border : '1px solid #c5c5c5'
			},
			margin : '5 0 0 1',
    		height : 30,
    		layout: 'hbox',
			items:[{
			    	   	xtype: 'combo',
			    	   	width : 100,
			    	   	fieldLabel: false,
			    	   	editable : false,//不允许编辑 haosl 2018-2-6
			    	    id : 'workTypeid',
			    	    store: typeStore,
			    	    queryMode: 'local',
			    	    
			    	    displayField: 'codename',
			    	    valueField: 'codeitem',
			    	    margin : '4 0 0 0',
			    	    anchor: '100%',
			    	    listeners : {
							render : function(combo) {
								combo.setValue(combo.getStore().getAt(0));
							},
							scope : this
						}
			       },{
			    	   	xtype: 'combo',
			    	   	width : worklog_me.contentWidth,
			    	   	fieldLabel: false,
			    	    emptyText: '工作内容',
			    	    id : 'workContentid',
			    	    store: contentStore,
			    	    queryMode: 'local',
			    	    valueField: 'itemid',
				        displayField: 'itemdesc',
			    	    margin : '4 0 0 0',
			    	    labelAlign:'left',
			    	    onFieldMutation: function(e) {return;},
			    	    listeners : {//focus
							focus:function(){
								var elm = Ext.get('workContentid');
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
									y:y,
									style:'z-index:2;',
									renderTo:Ext.getBody(),
									listeners:{
										render:function(){
											
											var defaltvalue = Ext.getCmp('workContentid').getRawValue();
											this.setValue(defaltvalue);
											var task = new Ext.util.DelayedTask(function() {
												if (Ext.isIE) {// 兼容ie
													if(Ext.getDom("description-inputEl")){
														var tempRange = Ext.getDom("description-inputEl").createTextRange();// 创建文本区
														tempRange.moveStart('character', defaltvalue.length);// 设置移动起点
	//													tempRange.collapse(true);
														tempRange.select();
													}
												}
												this.focus();
											}, this);
											task.delay(200);
											
											this.mon(Ext.getDoc(), {
								                mousedown: this.hiddenIf,
								                scope: this
								            });
										}
									},
									hiddenIf: function(e) {
								        if (!this.isDestroyed && !e.within(this.bodyEl, false, true) && !this.owns(e.target)) {
								        	var value = this.getValue();//大文本值
								        	var combo = Ext.getCmp('workContentid');
								        	combo.setValue(Ext.isEmpty(value) ? null : value);
								        	combo.setRawValue(Ext.isEmpty(value) ? undefined : value);
								        	
								        	Ext.destroy(this);
								        }
								    }
								});
							},   
							scope : this
						}
			       },{
			    	   	xtype: 'timefield',
		    	        id : 'startTimeid',
		    	        format: 'H:i',
		    	        fieldLabel: false,
		    	        width : 76,
		    	        emptyText : '起始时间',
		    	        formatText: 'HH:mm',
		    	        minValue: '00:00',
		    	        maxValue: '23:59',
		    	        margin : '4 0 0 0',
		    	        hidden : worklog_me.taskTime,
		    	        invalidText: '{0}请输入正确的时间！',
		    	        altFormats : "g:ia|g:iA|g:i a|g:i A|h:i|g:i|H:i|ga|ha|gA|h a|g a|g A|gi|hi|Hi|gia|hia|g|H|gi a|hi a|giA|hiA|gi A|hi A",
		    	        increment: 5,
		    	        listConfig : {
                              maxHeight : 160
                        },
		    	        anchor: '100%'   
			       }
			       ,{
			    	   	xtype: 'timefield',
		    	        id : 'endTimeid',
		    	        format: 'H:i',
		    	        fieldLabel: false,
		    	        width : 76,
		    	        emptyText : '截止时间',
		    	        formatText: 'HH:mm',
		    	        minValue: '00:00',
		    	        maxValue: '23:59',
		    	        margin : '4 0 0 0',
		    	        hidden : worklog_me.taskTime,
		    	        invalidText: '{0}请输入正确的时间！',
		    	        altFormats : "g:ia|g:iA|g:i a|g:i A|h:i|g:i|H:i|ga|ha|gA|h a|g a|g A|gi|hi|Hi|gia|hia|g|H|gi a|hi a|giA|hiA|gi A|hi A",
		    	        increment: 5,
		    	        listConfig : {
                              maxHeight : 160
                        },
		    	        anchor: '100%'
			       }
			       ,{
						xtype:'panel',
						width:30,
						border:false,
						bodyStyle:'background-color:#f0f0f0;color:#000;',
						html:'<div style="width:100%;text-align:center;cursor:pointer;"><span style="position:relative;top:-4px;font-size:x-large;">+</span><div>',
						items:[],
						listeners : {
							click : {
								element : 'el',
								fn : function() {
			    	   				worklog_me.addTask();
								}
							},
							scope : this
						}
					}]
		});
		return panel;
    },
    
    //添加记录
    addTask : function (){
    	
    	var onedt = Ext.Date.parse(worklog_me.oneday, "Y.m.d");
    	if((onedt.getTime() - worklog_me.nowday.getTime())>0){
    		Ext.Msg.alert('提示信息',"超过当前日期的日志无法填写！");
    		return;
    	}
    	
		if(worklog_me.flag==1 && (worklog_me.p0115=='02' || worklog_me.p0115=='03')){
    		Ext.Msg.alert('提示信息',"当天补填的日志已发布，不可以添加！");
    		return;
    	}
		
    	if(worklog_me.p0115=='02' || worklog_me.p0115=='03'){
    		Ext.Msg.alert('提示信息',"当天工作日志已发布，请先撤回再编辑！");
    		return;
    	}
    	
    	var worktype = Ext.getCmp('workTypeid').getValue();
    	if(worktype==null){
    		Ext.Msg.alert('提示信息',"工作类别不能为空！");
    		return;
    	}
    	
    	var desc = Ext.getCmp('workContentid').rawValue;
    	if(Ext.isEmpty(desc.replace(/\s*/g,"")) || desc=='工作内容'){
    		Ext.Msg.alert('提示信息',"工作内容不能为空！");
    		return;
    	}
    	var startTimeValue = Ext.getCmp('startTimeid').getValue();
    	if(startTimeValue == null && !worklog_me.taskTime){
    		Ext.Msg.alert('提示信息',"起始时间不能为空！");
    		return;
    	}
    	
    	var endTimeValue = Ext.getCmp('endTimeid').getValue();
    	if(endTimeValue == null && !worklog_me.taskTime){
    		Ext.Msg.alert('提示信息',"截止时间不能为空！");
    		return;
    	}
    	var startTime = "";
		var endTime = "";
		var worktimes = "0";
		var startdt = null;
    	if(!worklog_me.taskTime){//耗时相关列
    		startTime = Ext.Date.format(startTimeValue, 'H:i');
    		endTime = Ext.Date.format(endTimeValue, 'H:i');
    		startdt = Ext.Date.parse(startTime, "H:i");
    		var enddt = Ext.Date.parse(endTime, "H:i");
    		worktimes = (enddt.getTime() - startdt.getTime())/(1000*60)+"";
    		startTime = worklog_me.oneday+" "+startTime;
    		endTime = worklog_me.oneday+" "+endTime;
    		if(worktimes <= 0){
                Ext.Msg.alert('提示信息',"起始时间应早于截止时间，请重新填写！");
                return;
            }
            //添加之前校验时间是否有交互
            var stime = replaceAll(startTime,".","-");
            var etime = replaceAll(endTime,".","-");
            var bool = false;
            worklog_me.logStore.getData().each(function(record,index){
            	if(!((stime<record.data.start_time&&etime<record.data.start_time) 
            	       || (stime>record.data.end_time&&etime>record.data.end_time))){
                	   bool = true;
                	   return;
            	}
             });
    	}else{
            startTime = worklog_me.oneday+" 00:00";
            endTime = worklog_me.oneday+" 00:00";
        }
        if(bool){
        	Ext.showAlert('起止时间与其他工作时间段存在交叉，请重新填写！');
            return;
        }
    	
	  var map = new HashMap();
      
      map.put("flag","addlog");
//  	      map.put("self","self");
      map.put("nbase",worklog_me.nbase);
      map.put("a0100",worklog_me.a0100);
      map.put("p0100",worklog_me.p0100);
      map.put("work_type",worktype);
      map.put("content",getEncodeStr(desc));
      map.put("start_time",startTime);
      map.put("end_time",endTime);
      map.put("work_time",worktimes);
      map.put("datevalue",worklog_me.oneday);
      
      Rpc({functionId:'WP30000001',async:false,
    	  success:function(response){
	      	var obj = Ext.decode(response.responseText);
			var errorcode = obj.errorcode;
			if (errorcode == '1') {
				Ext.showAlert('新增失败！');
		
			} else if (errorcode == '0') {
				var store = Ext.getCmp('loggrid').getStore();
				store.setData(obj.tabledata);
				
//  				Ext.getCmp('workTypeid').setValue(worklog_me.codelist[0].codeitem);
				Ext.getCmp('workContentid').reset();
				Ext.getCmp('startTimeid').reset();
				Ext.getCmp('endTimeid').reset();
			}
		},scope:this},map);
    	
    },
    //更改编辑
    updateRecord : function(e) {
		var field = e.field;
		var num = e.record.get('Record_num');
		var bool = false;
		//修改时间之前校验时间是否有交互
		if(field=="start_time" || field=="end_time"){
            worklog_me.logStore.getData().each(function(record,index){
            	if(num==record.data.Record_num && !bool){
            		if(field=="start_time"){
            		      if(e.value>=record.data.end_time){
            		          Ext.showAlert('起始时间大于或等于截止时间，请重新填写！');
            		          bool = true;
            		          e.cancel=true;
                              return;
            		      }
            		}else if(field=="end_time"){
                          if(e.value<=record.data.start_time){
                              Ext.showAlert('截止时间小于或等于起始时间，请重新填写！');
                              bool = true;
                              e.cancel=true;
                              return;
                          }
                    }
            	}else if(e.value>=record.data.start_time && e.value<=record.data.end_time && !bool){
                       Ext.showAlert('起止时间与其他时间存在交互，请重新填写！');
                       bool = true;
                       e.cancel=true;
                       return;
                }
          });
        }
        if(bool)
            return;
            
		var map = new HashMap();
		map.put("flag","updatelog");
//		map.put("self","self");
		map.put("nbase",worklog_me.nbase);
		map.put("a0100",worklog_me.a0100);
		map.put("p0100",worklog_me.p0100);
		map.put("record_num", num+'');
		map.put("field", field);
		if(field=="start_time" || field=="end_time"){
			//为起止时间加上年月日 haosl 2018-2-6
	        var hm=e.value;
	        var ymd = worklog_me.oneday.replace(/\./g,"-");
			map.put("value",ymd+" "+hm);
		}else{
			map.put("value",e.value);
		}
		
		Rpc({
					functionId : 'WP30000001',
					async : false,
					success : function(response) {
						var obj = Ext.decode(response.responseText);
						var errorcode = obj.errorcode;
						if (errorcode == '1') {
							Ext.showAlert('更新失败！');

						} else if (errorcode == '0') {// success
							var store = Ext.getCmp('loggrid').getStore();
							store.setData(obj.tabledata);
						}
					},
					scope : this
				}, map);
	},
    //删除记录
    deleteRecord : function() {
		if(worklog_me.flag==1 && (worklog_me.p0115=='02' || worklog_me.p0115=='03')){
    		Ext.Msg.alert('提示信息',"补填的工作日志发布后不允许删除！");
    		return;
    	}
		
		if(worklog_me.p0115=='02' || worklog_me.p0115=='03'){
    		Ext.Msg.alert('提示信息',"当天工作日志已发布，请先撤回再删除！");
    		return;
    	}
		
		var grid = Ext.getCmp('loggrid');
		if (grid.getSelectionModel().getSelection().length == 0) {
			Ext.showAlert('请选择记录再进行删除！');
			return;
		}
		Ext.showConfirm('确定要删除选中记录？', function(btn) {
			if (btn == 'yes') {
				var record_nums = new Array();

				var isHaveCantDeleteRecord = false;
				var selected = grid.getSelectionModel().getSelection();
				for (var i = 0; i < selected.length; i++) {
					var data = selected[i].data;
					var record_num = data.Record_num;
					record_nums.push(record_num+'');
				}
				var map = new HashMap();
				map.put("flag","delelog");
//				map.put("self","self");
				map.put("nbase",worklog_me.nbase);
				map.put("a0100",worklog_me.a0100);
				map.put("p0100",worklog_me.p0100);
		      	map.put("record_nums",record_nums);
				Rpc({
					functionId : 'WP30000001',
					async : false,
					success : function(response) {
						var obj = Ext.decode(response.responseText);
						var errorcode = obj.errorcode;
						if (errorcode == '1') {
							Ext.showAlert('删除失败！');

						} else if (errorcode == '0') {// success
							var store = Ext.getCmp('loggrid').getStore();
							store.setData(obj.tabledata);
						}
					},
					scope : this
				}, map);
			}
		}, this);
	},
    
	//获取编辑按钮
	getOperateButton : function(){
		
		//查看别人日志不显示编辑工具
		if(worklog_me.isSelf == '1'){
    		worklog_me.editFunc = true;
    		Ext.getCmp('editbar').setHidden(worklog_me.editFunc);
    		Ext.getCmp('editbuts').setHidden(worklog_me.editFunc);
    		return;
    	}
		
		var oneday = Ext.Date.parse(worklog_me.oneday, "Y.m.d");
		var times = (worklog_me.nowday.getTime() - oneday.getTime())/(1000*60*60*24);
		
		//补填并已发布的  || 超过当前日期的  不显示编辑工具
		if((worklog_me.flag==1 && (worklog_me.p0115=='02' || worklog_me.p0115=='03' )) || (oneday.getTime() - worklog_me.nowday.getTime())>0){
//			worklog_me.editFunc = true;
    	}else{
    		worklog_me.editFunc = false;
    	}
		
//		Ext.getCmp('editbar').setHidden(worklog_me.editFunc);
//		Ext.getCmp('editbuts').setHidden(worklog_me.editFunc);
		//加一层校验，发布后把编辑的文本框按钮全部隐藏
		if(worklog_me.flag!=1 && (worklog_me.p0115=='02' || worklog_me.p0115=='03')){
			Ext.getCmp('editbar').setHidden(true);
			Ext.getCmp('deleid').setHidden(true);//隐藏删除按钮
			Ext.getCmp('editbuts').setHidden(false);
    	}else{
    		Ext.getCmp('deleid').setHidden(false);
    		Ext.getCmp('editbar').setHidden(false);
    	}
		
		//校验是否补填
		if(worklog_me.flag!=1 && !((Number(worklog_me.section)+1)<times && times>0)){
			//未发布的只显示发布按钮，已发布显示撤回
			if(worklog_me.p0115 == '02' || worklog_me.p0115=='03'){
				Ext.getCmp('btnPublish').setHidden(true);
				Ext.getCmp('btnRecall').setHidden(false);
			}else{
				Ext.getCmp('btnPublish').setHidden(false);
				Ext.getCmp('btnRecall').setHidden(true);
			}
			Ext.getCmp('btnFill').setHidden(true);
			Ext.getCmp('editbuts').setHidden(false);
		}else{
			if(worklog_me.p0115 == '02' || worklog_me.p0115=='03'){
				Ext.getCmp('btnPublish').setHidden(true);
				Ext.getCmp('btnRecall').setHidden(true);
				Ext.getCmp('btnFill').setHidden(true);
				Ext.getCmp('deleid').setHidden(true);//隐藏删除按钮
			}else{
				Ext.getCmp('editbuts').setHidden(false);
				Ext.getCmp('btnPublish').setHidden(true);
				Ext.getCmp('btnRecall').setHidden(true);
				Ext.getCmp('btnFill').setHidden(false);
				Ext.getCmp('deleid').setHidden(false);//删除按钮
			}
		}
	},
	
    //操作按钮工具条
    getButtons: function(){

    	var panel = Ext.create("Ext.panel.Panel",{
    		id : 'editbuts',
    		width : '100%', 
			border : false,
    		bodyPadding : 0,
    		margin: '12 0 0 1',
    		height : 26,
    		layout: 'hbox',
			items:[{
				xtype:'panel',
				width:'10%',
				border:false,
				margin: '2 0 0 2',
				items:[{
                    id : 'jiaid',
                    xtype : 'image',
                    src : '/images/daochu.png',
                    style:'cursor:pointer;',
                    title:"导出",
                    width : 15,
                    height : 15,
                    listeners : {
                        click : {
                            element : 'el',
                            fn : function(e) {
                                worklog_me.exportExcel();
                            }
                        },
                        scope : this
                    }
                },{
					id : 'deleid',
					xtype : 'image',
					src : '/workplan/image/chahao.png',
					style:'cursor:pointer;',
					title:"删除",
					margin: '0 0 0 10',
					formatText: 'HH:mm',
					width : 15,
					height : 15,
					listeners : {
						click : {
							element : 'el',
							fn : function() {
								worklog_me.deleteRecord();
							}
						},
						scope : this
					}
				}]
			},{
				xtype:'panel',
				width:'90%',
				border:false,
				layout: {
				    type: 'vbox',
				    align: 'right'
				},
				items:[{
					id:'btnPublish',
					xtype:'panel',
					border:false,
					html: '<div class="hj-bt-bg"><span class="hj-bt-fonts">发布</span><div>',
					listeners : {
						click : {
							element : 'el',
							fn : function() {
		    	   				worklog_me.publishLog();
							}
						},
						scope : this
					}
				},{
					id:'btnRecall',
					xtype:'panel',
					border:false,
					html: '<div class="hj-bt-bg"><span class="hj-bt-fonts">撤回</span><div>',
					listeners : {
						click : {
							element : 'el',
							fn : function() {
		    	   				worklog_me.recallLog();
							}
						},
						scope : this
					}
				},{
					id:'btnFill',
					xtype:'panel',
					border:false,
					html: '<div class="hj-bt-bg"><span class="hj-bt-fonts">补填</span><div>',
					listeners : {
						click : {
							element : 'el',
							fn : function() {
		    	   				worklog_me.fillLog();
							}
						},
						scope : this
					}
				}]
			}]
		});
		return panel;
    },
    
    //发布日志
    publishLog : function(){
    	var store = Ext.getCmp('loggrid').getStore();
		if (store.getCount() == 0) {
			Ext.showAlert('请先填写日志再发布！');
			return;
		}
    	
    	Ext.showConfirm('确定要发布日志吗？', function(btn) {
			if (btn == 'yes') {
				var map = new HashMap();
				map.put("flag","publishlog");
//				map.put("self","self");
				map.put("nbase",worklog_me.nbase);
				map.put("a0100",worklog_me.a0100);
				map.put("p0100",worklog_me.p0100);
				map.put("classId",worklog_me.oneClassId);
				Rpc({
						functionId : 'WP30000001',
						async : false,
						success : function(response) {
								var obj = Ext.decode(response.responseText);
								var errorcode = obj.errorcode;
								if (errorcode == '1') {
									Ext.showAlert('发布失败！');
	
								} else if (errorcode == '0') {// success
									worklog_me.p0115 = "02";
									worklog_me.operateLoadView(obj);
									
								}
						},
						scope : this
					}, map);
			}
		}, this);
    	
    },
    
    //在发布、撤回、补填后刷新日历组件
    operateLoadView : function(obj){
    	var store = Ext.getCmp('loggrid').getStore();
    	store.setData(obj.tabledata);
    	var map = new HashMap();
	      map.put("nowMonth",worklog_me.nowMonth);
	      map.put("flag","duration");
//	      map.put("self","self");
	      map.put("nbase",worklog_me.nbase);
	      map.put("a0100",worklog_me.a0100);
	      Rpc({functionId:'WP30000001',async:false,success:this.loadCalview,scope:this},map);
	      
	      document.getElementById(worklog_me.oneday).setAttribute("class", "border_bottom");
	      worklog_me.getOperateButton();
    },
    
    //撤回日志
    recallLog : function(){
    	if(worklog_me.p0115 != '02'){
    		Ext.Msg.alert('提示信息',"当天日志未发布，无法撤回！");
    		return;
    	}
    	
    	Ext.showConfirm('确定要撤回日志吗？', function(btn) {
			if (btn == 'yes') {
				var map = new HashMap();
				map.put("flag","recalllog");
//				map.put("self","self");
				map.put("nbase",worklog_me.nbase);
				map.put("a0100",worklog_me.a0100);
				map.put("p0100",worklog_me.p0100);
				Rpc({
							functionId : 'WP30000001',
							async : false,
							success : function(response) {
								var obj = Ext.decode(response.responseText);
								var errorcode = obj.errorcode;
								if (errorcode == '1') {
									Ext.showAlert('撤回失败！');
		
								} else if (errorcode == '0') {// success
									worklog_me.p0115 = "01";
									worklog_me.operateLoadView(obj);
								}
							},
							scope : this
						}, map);
			}
		}, this);
    	
    },
    //fillLog补填
    fillLog : function(){
    	var store = Ext.getCmp('loggrid').getStore();
		if (store.getCount() == 0) {
			Ext.showAlert('请先填写日志再补填！');
			return;
		}
    	Ext.showConfirm('补填的日志发布后不可以修改，确定发布吗？', function(btn) {
			if (btn == 'yes') {
				var map = new HashMap();
				map.put("flag","filllog");
//				map.put("self","self");
				map.put("nbase",worklog_me.nbase);
				map.put("a0100",worklog_me.a0100);
				map.put("p0100",worklog_me.p0100);
				map.put("datevalue",worklog_me.oneday);
				map.put("classId",worklog_me.oneClassId);
				Rpc({
						functionId : 'WP30000001',
						async : false,
						success : function(response) {
								var obj = Ext.decode(response.responseText);
								var errorcode = obj.errorcode;
								if (errorcode == '1') {
									Ext.showAlert('补填失败！');
		
								} else if (errorcode == '0') {// success
									worklog_me.p0115 = "02";
									worklog_me.flag = obj.flag;
									var year = worklog_me.oneday.substring(0, 4);
									var month = worklog_me.oneday.substring(5, 7);
									var day = worklog_me.oneday.substring(8);
									//日志表格表头更新
									Ext.getCmp('titlehtml').setHtml(worklog_me.logTitle(year, month, day, worklog_me.name));
									/*
									Ext.getCmp('editbar').setHidden(true);
									Ext.getCmp('editbuts').setHidden(true);
									*/
									worklog_me.operateLoadView(obj);
								}
						},
						scope : this
					}, map);
			}
		}, this);
    	
    },
    
    //获取时间期第一天是星期几
	getFirstDay:function(date){
		var dt = new Date(date.replace(/-|\./g,"/"));
		var dayNum = dt.getDay();
		return dayNum;
	},
    
	//查看某天的日志
	getLogDairy: function(datevalue, classId){
		worklog_me.oneday = datevalue;
		worklog_me.oneClassId = classId;
		var map = new HashMap();
	      map.put("datevalue",datevalue);
	      map.put("flag","onedaylog");
//	      map.put("self","self");
	      map.put("nbase",worklog_me.nbase);
	      map.put("a0100",worklog_me.a0100);
	      
	      Rpc({functionId:'WP30000001',async:true,success:worklog_me.loadLogTable,scope:this},map);
	},
	
	loadLogTable : function(form){
		var result = Ext.decode(form.responseText);
    	worklog_me.p0100 = result.p0100;
    	worklog_me.p0115 = result.p0115;
    	worklog_me.flag = result.flag;
    	
    	var store = Ext.getCmp('loggrid').getStore();
    	store.setData(result.tabledata);
    	
		Ext.getCmp('calview').setHtml(worklog_me.calview());
		
		var year = worklog_me.oneday.substring(0, 4);
		var month = worklog_me.oneday.substring(5, 7);
		var day = worklog_me.oneday.substring(8);
		
		//选中的日期改变下标示  
		document.getElementById(worklog_me.oneday).setAttribute("class", "border_bottom");
		//日志表格表头更新
		Ext.getCmp('titlehtml').setHtml(worklog_me.logTitle(year, month, day, worklog_me.name));		
		//更新汇总饼图
		if(worklog_me.sumflag=="1")
			worklog_me.changeMW(worklog_me.sumflag);
		
    	if(worklog_me.employeflag == "1"){//监控不可编辑
    		worklog_me.editFunc = true;
			Ext.getCmp('editbar').setHidden(worklog_me.editFunc);
			Ext.getCmp('editbuts').setHidden(worklog_me.editFunc);
		}else{
			worklog_me.getOperateButton();
		}
		//切换不通日期的日志时，清掉已经在填写的工作内容  haosl 2018-02-06 add
		var workContent = Ext.getCmp("workContentid");
		if(workContent){
			workContent.setValue(null);
		}
    	//加载本周计划
    	worklog_me.getWeekPlan();
	},
	
	//根据某天查看该周的工作计划
	getWeekPlan: function(){
		var map = new HashMap();
	      map.put("datevalue",worklog_me.oneday);
	      map.put("flag","oneweek");
//	      map.put("self","self");
	      map.put("nbase",worklog_me.nbase);
	      map.put("a0100",worklog_me.a0100);
	      
	      Rpc({functionId:'WP30000001',async:false,success:worklog_me.loadWeekTable,scope:this},map);
	},
	loadWeekTable : function(form){
		var result = Ext.decode(form.responseText);
		if(result.weekplan == 0)
    		worklog_me.workplan = '本周无工作计划';
    	else
    		worklog_me.workplan = '计划任务';
    	
    	var store = Ext.getCmp('weekgrid').getStore();
    	store.setData(result.weekdata);
    	Ext.getCmp('planName').setText(worklog_me.workplan);
	},
    	
  //日志 日历
	calview:function(){
		
		var calhtml = "";
		calhtml += "<div align='center' style='width:100%;' ><p align='center' style='font-size: 22px;margin:10px 0 0 0;' ><a style='cursor:pointer;font-size: 22px;color:#979797;' onclick='worklog_me.getSessiondata(0);'> < </a>" +
				"<span id='nowMoth' style='font-size: 22px;color:#FF4474;'>"+worklog_me.nowMonth+"</span>" +
						"<a style='cursor:pointer;font-size: 22px;color:#979797;' onclick='worklog_me.getSessiondata(1);'> > </a></p>";
		calhtml += "<table  border='0' cellspacing='0'  align='center' cellpadding='0'  class='ListTable'>";
		calhtml += "<tr> ";
//		calhtml += "<td align='center' class='TableRow' style='color:#78C5FF;' >日</td>";//theFirstDay-1则每周第一天从周一开始
		calhtml += "<td align='center' class='TableRow' style='color:#78C5FF;' >一</td>";
		calhtml += "<td align='center' class='TableRow' style='color:#78C5FF;' >二</td>";
		calhtml += "<td align='center' class='TableRow' style='color:#78C5FF;' >三</td>";
		calhtml += "<td align='center' class='TableRow' style='color:#78C5FF;' >四</td>";
		calhtml += "<td align='center' class='TableRow' style='color:#78C5FF;' >五</td>";
		calhtml += "<td align='center' class='TableRow' style='color:#78C5FF;' >六</td>";
		calhtml += "<td align='center' class='TableRow' style='color:#78C5FF;' >日</td>";
		calhtml += " </tr> ";

		var time=Ext.Date.format(worklog_me.nowday, 'Y.m.d');
		var theRows = parseInt(worklog_me.datelist.length / 7);
		var mod = worklog_me.datelist.length % 7;
        if (mod > 0) {
            theRows = theRows + 1;
        }
//        var theFirstDay = worklog_me.getFirstDay(worklog_me.datelist[0].date)-1;//-1则每周第一天从周一开始
        var theFirstDay = worklog_me.getFirstDay(worklog_me.datelist[0].date);
        if(theFirstDay==0){//一个月的1号为周日时单独处理
        	theFirstDay = 7-1;
        }else{
        	theFirstDay = theFirstDay-1;
        }
        var theMonthLen = theFirstDay + worklog_me.datelist.length;
        if (7 - theFirstDay < mod)
            theRows = theRows + 1;
        if(worklog_me.datelist.length==28 && theFirstDay!=6)
        	theRows = theRows + 1;

        var n = 0;
        var day = 0;
        var day_str = "";
        var day_state = "";
        for (var i = 0; i < theRows; i++) {
        	calhtml += "<tr>";
            for (var j = 0; j < 7; j++) {
                n++;
                if (n > theFirstDay && n <= theMonthLen) {
                    day = n - theFirstDay - 1;
                    var date = worklog_me.datelist[day].date;
                    var classId = worklog_me.datelist[day].classId;
                    day_str = date.substring(8);
                	day_state = worklog_me.datelist[day].state;
                    
                	if(date == time){
                		worklog_me.oneClassId = classId;
//                    	calhtml += " <td align='center'  class='TableRow'   style='font-size:15px;background:url(../../workplan/images/okr_curdate.png) no-repeat;background-position:center; ' ><div  class='fontdiv' ><a class='nowfontacolor'  onclick='worklog_me.getLogDairy(\""+date+"\",\""+classId+"\");' >"+day_str+" </a><div id='"+date+"' class='boxbottom' ></div></div></td>";
                    }
                    if(day_state == "all"){
                    	calhtml += " <td align='center'  class='TableRow'   style='font-size:15px;background:url(../../workplan/images/okr_all.png) no-repeat;background-position:center; ' ><div  class='fontdiv'><a class='fontacolor'  onclick='worklog_me.getLogDairy(\""+date+"\",\""+classId+"\");' >"+day_str+" </a><div id='"+date+"' class='boxbottom' ></div></div></td>";
                    }else if(day_state == "dukey"){
                    	calhtml += " <td align='center'  class='TableRow' style='font-size:15px;background:url(../../workplan/images/okr_duty_key.png) no-repeat;background-position:center; ' ><div  class='fontdiv'><a class='fontacolor' onclick='worklog_me.getLogDairy(\""+date+"\",\""+classId+"\");' >"+day_str+" </a><div id='"+date+"' class='boxbottom' ></div></div></td>";
                    }else if(day_state == "duoth"){
                    	calhtml += " <td align='center'  class='TableRow' style='font-size:15px;background:url(../../workplan/images/okr_duty_oth.png) no-repeat;background-position:center; ' ><div  class='fontdiv'><a class='fontacolor' onclick='worklog_me.getLogDairy(\""+date+"\",\""+classId+"\");' >"+day_str+" </a><div id='"+date+"' class='boxbottom' ></div></div></td>";
                    }else if(day_state == "keoth"){
                    	calhtml += " <td align='center'  class='TableRow' style='font-size:15px;background:url(../../workplan/images/okr_key_oth.png) no-repeat;background-position:center; ' ><div  class='fontdiv'><a class='fontacolor' onclick='worklog_me.getLogDairy(\""+date+"\",\""+classId+"\");' >"+day_str+" </a><div id='"+date+"' class='boxbottom' ></div></div></td>";
                    }else if(day_state == "duty"){
                    	calhtml += " <td align='center'  class='TableRow' style='font-size:15px;background:url(../../workplan/images/okr_duty.png) no-repeat;background-position:center; ' ><div  class='fontdiv'><a class='fontacolor' onclick='worklog_me.getLogDairy(\""+date+"\",\""+classId+"\");' >"+day_str+" </a><div id='"+date+"' class='boxbottom' ></div></div></td>";
                    }else if(day_state == "key"){
                    	calhtml += " <td align='center'  class='TableRow' style='font-size:15px;background:url(../../workplan/images/okr_key.png) no-repeat;background-position:center; ' ><div  class='fontdiv'><a class='fontacolor' onclick='worklog_me.getLogDairy(\""+date+"\",\""+classId+"\");' >"+day_str+" </a><div id='"+date+"' class='boxbottom' ></div></div></td>";
                    }else if(day_state == "oth"){
                    	calhtml += " <td align='center'  class='TableRow' style='font-size:15px;background:url(../../workplan/images/okr_oth.png) no-repeat;background-position:center; ' ><div  class='fontdiv'><a class='fontacolor' onclick='worklog_me.getLogDairy(\""+date+"\",\""+classId+"\");' >"+day_str+" </a><div id='"+date+"' class='boxbottom' ></div></div></td>";
                    }else if(day_state == "isnull"){
                    	calhtml += " <td align='center'  class='TableRow'   style='font-size:15px;background:url(../../workplan/images/okr_curdate.png) no-repeat;background-position:center; ' ><div  class='fontdiv' ><a class='nowfontacolor'  onclick='worklog_me.getLogDairy(\""+date+"\",\""+classId+"\");' >"+day_str+" </a><div id='"+date+"' class='boxbottom' ></div></div></td>";
	                }else if(day_state == "nofill"){
	                	calhtml += " <td align='center'  class='TableRow' style='font-size:15px;' ><div class='fontdiv'><a class='nowfontacolor' onclick='worklog_me.getLogDairy(\""+date+"\",\""+classId+"\");' ><span style='color:#979797;'>"+day_str+" </span></a><div id='"+date+"' class='boxbottom' ></div></div></td>";
	                }else if(day_state == "after"){
	                	calhtml += " <td align='center'  class='TableRow' style='font-size:15px;color:#979797;' ><div class='fontdiv' >"+day_str+"<div id='"+date+"' class='boxbottom' ></div></div></td>";
	                }else if(day_state == "no"){
                    	calhtml += " <td align='center'  class='TableRow' style='font-size:15px;' ><div class='fontdiv'><a class='nowfontacolor' onclick='worklog_me.getLogDairy(\""+date+"\",\""+classId+"\");' ><span style='color:#979797;'>"+day_str+" </span></a><div id='"+date+"' class='boxbottom' ></div></div></td>";
	                }else{
                    	calhtml += " <td  align='center'  class='TableRow' style='font-size:15px;' ><div class='fontdiv' ><a class='nowfontacolor' onclick='worklog_me.getLogDairy(\""+date+"\",\""+classId+"\");' >"+day_str+" </a><div id='"+date+"' class='boxbottom' ></div></div></td>";
                    }
                } else {
//                	calhtml += " <td align='center'  class='TableRow' style='border-left:none;'  > </td> ";
                	//当一行只有一个日期时选中时行高会改变引起跳动故加一个隐藏的点‘.’使其保持原来样式
                	calhtml += " <td align='center'  class='TableRow' style='font-size:15px;border-left:none;' ><div class='fontdiv' ><a style='color:#FFFFFF;'>.</a><div class='boxbottom' ></div></div></td>";
                }
            }
            calhtml += "</tr>";
        }
		
		calhtml += "</table>";
//		calhtml += "<br>";
		calhtml += "<table width='90%' border='0' cellspacing='0'  align='center' cellpadding='0' style='margin:10px 0 0 0;font-size: 15px;'>" +
				"<tr align='center' >" +
				"<td><img alt='' src='../../workplan/images/okr_duty_small.png'>&nbsp;&nbsp;例行工作</td>" +
				"<td><img alt='' src='../../workplan/images/okr_key_small.png'>&nbsp;&nbsp;重点工作</td>" +
				"<td><img alt='' src='../../workplan/images/okr_oth_small.png'>&nbsp;&nbsp;其他工作</td>" +
				"</tr>" ;
		calhtml += "</table> </div>";
		return calhtml;
	},
	
	fillingSum : function(){
		var fillhtml = "";
		fillhtml += "<div align='center' style='width:100%;' ><table width='90%' border='0' cellspacing='0'  align='center' cellpadding='0' style='margin:10px 0 0 0;font-size: 15px;'>";
		fillhtml += "<tr align='center'><td colspan='3' style='font-size: 13px;'>";
		fillhtml += "填写情况：正常填写"+worklog_me.fillstate.normal+"天，逾期补填"+worklog_me.fillstate.overdue+"天。";
		fillhtml += "</td></tr>";
		fillhtml += "<tr><td colspan='3' align='center'>";
		fillhtml += "<div style='width:85%;height:0;border:solid 1px #EBEBEB;'></div>";
		fillhtml += "</td></tr>";
		fillhtml += "</table> </div>";
		return fillhtml;
	},

	//月份与周切换
	changeMW : function(txtid){
		worklog_me.sumflag = txtid+"";
		if(txtid == 2){
			if(!worklog_me.taskTime){
				document.getElementById("fillingId").setAttribute("class", "task-time");
				document.getElementById("weekTimeId").setAttribute("class", "task-time-no");
				document.getElementById("monthTimeId").setAttribute("class", "task-time-no");
			}
			Ext.getCmp('pieid').removeAll();
			Ext.getCmp('pieid').add(worklog_me.createPieChart());
			return;
		}else if(txtid == 1){
			document.getElementById("fillingId").setAttribute("class", "task-time-no");
			document.getElementById("weekTimeId").setAttribute("class", "task-time");
			document.getElementById("monthTimeId").setAttribute("class", "task-time-no");
		}else if(txtid == 0){
			document.getElementById("fillingId").setAttribute("class", "task-time-no");
			document.getElementById("weekTimeId").setAttribute("class", "task-time-no");
			document.getElementById("monthTimeId").setAttribute("class", "task-time");
		}
		
		var map = new HashMap();
	      map.put("flag","summary");
//	      map.put("self","self");
	      map.put("nbase",worklog_me.nbase);
	      map.put("a0100",worklog_me.a0100);
	      map.put("dropMonth",worklog_me.nowMonth);
	      map.put("dropDay",worklog_me.oneday);
	      map.put("droptype",worklog_me.sumflag);
		
		Rpc({functionId:'WP30000001',async:false,success:worklog_me.loadPie,scope:this},map);
	},
	
	loadPie : function(form){
		var result = Ext.decode(form.responseText);
		  worklog_me.sumdata = result.sumjo;
		  
		  Ext.getCmp('pieid').removeAll();
		  Ext.getCmp('pieid').add(worklog_me.createPieChart());
	},
	
	//饼图类别
	dropMonthWeek : function (){
		var drophtml = "";
		drophtml += "<div align='center' style='width:100%;'>" ;
		drophtml += "<table width='90%' border='0' cellspacing='0'  align='center' cellpadding='0' style='margin:10px 0 0 0;font-size: 13px;'>";
		drophtml += "<tr align='center' >" ;
		if(!worklog_me.taskTime){
			drophtml += "<td><div id='monthTimeId' style='cursor:pointer;width:58px;' class='task-time' onclick='worklog_me.changeMW(0);'>本月耗时</div></td>" ;
			drophtml += "<td><div id='weekTimeId' style='cursor:pointer;width:58px;' onclick='worklog_me.changeMW(1);'>本周耗时</div></td>" ;
		}
		drophtml += "<td><div id='fillingId' style='cursor:pointer;width:80px;' onclick='worklog_me.changeMW(2);'>本月填写情况</div></td>" ;
		drophtml += "</tr>" ;
		drophtml += "</table></div>";
		return drophtml;
	},
	//本月出勤饼状图 汇总数据
	/*createPieChart:function(){	
		
		var sumall = Number(worklog_me.sumdata.duty)+Number(worklog_me.sumdata.key)+Number(worklog_me.sumdata.oth);
		var sumdays = Number(worklog_me.fillstate.normal)+Number(worklog_me.fillstate.overdue)+Number(worklog_me.fillstate.isnull);

		if((sumall == 0 && !worklog_me.taskTime && worklog_me.sumflag!=2) || sumdays==0){
			var mw = "";
			if(worklog_me.sumflag==0){
				mw = "本月工作耗时暂无统计数据";
			}else if(worklog_me.sumflag==1){
				mw = "本周工作耗时暂无统计数据";
			}else if(worklog_me.sumflag==2){
				mw = "本月统计情况暂无统计数据";
			}
			
			var nodate = "<div align='center'><table  height='150px' border='0' cellspacing='0'  align='center' cellpadding='0' style='font-size: 22px;color:#979797;'>" +
			"<tr><td>"+mw+"</td></tr></table></div>"; 

			var panel = Ext.create("Ext.panel.Panel",{
				border: false,
				items:[
				       {
				    	   xtype: 'label',
				    	   layout:{
								align:'middle'
							},
						   html  : nodate
				       }]
			});
			return panel;
		}else{
		
		var store = Ext.create("Ext.data.Store",{
			fields:['dataname','datavalue'],
			data:[]
//			data:[{'dataname':'例行工作', 'datavalue':worklog_me.sumdata.duty}]
		});
//		var colors = ['#52D48C','#5FB5FF','#FFD04C'];#52DFC,#FFD04C,#FF9386
		var colors = [];
		if(worklog_me.sumflag==2 || worklog_me.taskTime){
			if(Number(worklog_me.fillstate.normal) != 0){
				store.add({'dataname':'正常', 'datavalue':worklog_me.fillstate.normal});colors.push('#00B050');
			}
			if(Number(worklog_me.fillstate.overdue) != 0){
				store.add({'dataname':'补填', 'datavalue':worklog_me.fillstate.overdue});colors.push('#FFFF00');
			}
			if(Number(worklog_me.fillstate.isnull) != 0){
				store.add({'dataname':'未填', 'datavalue':worklog_me.fillstate.isnull});colors.push('#FF0000');
			}
		}else{
			if(Number(worklog_me.sumdata.duty) != 0){
				store.add({'dataname':'例行工作', 'datavalue':worklog_me.sumdata.duty});colors.push('#52D48C');
			}
			if(Number(worklog_me.sumdata.key) != 0){
				store.add({'dataname':'重点工作', 'datavalue':worklog_me.sumdata.key});colors.push('#5FB5FF');
			}
			if(Number(worklog_me.sumdata.oth) != 0){	
				store.add({'dataname':'其他工作', 'datavalue':worklog_me.sumdata.oth});colors.push('#BFB1FF');
			}
		}
		//饼状图
		var percent = 0;
		var pieChart = Ext.create("Ext.chart.PolarChart",{
			         width: '65%',
			         height: 180,
			         insetPadding:2,
					 innerPadding:2,
			         border: false,
			         animation:true,
			         store: store,
			         colors : colors,
			         interactions: ['rotate'],
			         series: {
			             type: 'pie',
			             highlight: false,
			             angleField: 'datavalue',
			             tooltip:{
								trackMouse:true,
								shadow:false,
								renderer:function(tip, record){
								   var showType = Ext.getCmp('pie_panel').showType;
								   if(showType=="data"){
									    tip.update(record.get("dataname")+"<br>值:"+record.get("datavalue"));
								   } else {
										var total = 0;
										store.each(function(rec){
											total += Number(rec.get('datavalue'));
										});
										percent = Math.round(record.get('datavalue')/total*100);
										tip.update(record.get('dataname')+"<br>占比:"+percent+"%");
									}
								}
							}
			         }
			      });
		
		var summarydata = "<table width='140px' height='130px' border='0' cellspacing='0'  align='center' cellpadding='0' style='font-size: 15px;color:#979797;'>" ;
			summarydata += "" ;
			if(worklog_me.sumflag==2 || worklog_me.taskTime){
				summarydata += "<tr ><td><img alt='' src='../../workplan/images/okr_normal_small.png'>&nbsp;&nbsp;正常"+worklog_me.fillstate.normal+"天</td></tr>" ;
				summarydata += "<tr><td><img alt='' src='../../workplan/images/okr_fill_small.png'>&nbsp;&nbsp;补填"+worklog_me.fillstate.overdue+"天</td></tr>" ;
				summarydata += "<tr><td><img alt='' src='../../workplan/images/okr_nofill_small.png'>&nbsp;&nbsp;未填"+worklog_me.fillstate.isnull+"天</td></tr>" ;
			}else{
				summarydata += "<tr ><td><img alt='' src='../../workplan/images/okr_duty_small.png'>&nbsp;&nbsp;"+worklog_me.sumdata.duty+"分钟</td></tr>" ;
				summarydata += "<tr><td><img alt='' src='../../workplan/images/okr_key_small.png'>&nbsp;&nbsp;"+worklog_me.sumdata.key+"分钟</td></tr>" ;
				summarydata += "<tr><td><img alt='' src='../../workplan/images/okr_oth_small.png'>&nbsp;&nbsp;"+worklog_me.sumdata.oth+"分钟</td></tr>" ;
			}
			summarydata += "</table>"; 

		//有数据时
		var panel = Ext.create("Ext.panel.Panel",{
			id:'pie_panel',
			border: false,
			chartType:'pie',//统计图类型column、line、pie
			showChart:true,//是否显示统计图
			showType:'percentage',//=data提示显示数据，=percentage显示百分比
			layout:{
				type:'hbox',
				align:'center'
			},
			items:[
			       pieChart,
			       {
			    	   xtype: 'label',
					   border: false,
					   html  : summarydata
			       }]
			});															
		
		return panel;
		}
	},*/
	/**
	 * echarts组件显示饼图
	 * 
	 * haosl
	 * @return {}
	 */
	createPieChart:function(){
		var sumall = Number(worklog_me.sumdata.duty)+Number(worklog_me.sumdata.key)+Number(worklog_me.sumdata.oth);
		var sumdays = Number(worklog_me.fillstate.normal)+Number(worklog_me.fillstate.overdue)+Number(worklog_me.fillstate.isnull);
		//没有数据时不显示饼图
		if((sumall == 0 && !worklog_me.taskTime && worklog_me.sumflag!=2) || sumdays==0){
			var mw = "";
			if(worklog_me.sumflag==0){
				mw = "本月工作耗时暂无统计数据";
			}else if(worklog_me.sumflag==1){
				mw = "本周工作耗时暂无统计数据";
			}else if(worklog_me.sumflag==2){
				mw = "本月统计情况暂无统计数据";
			}
			
			var nodate = "<div align='center'><table  height='150px' border='0' cellspacing='0'  align='center' cellpadding='0' style='font-size: 22px;color:#979797;'>" +
			"<tr><td>"+mw+"</td></tr></table></div>"; 

			var panel = Ext.create("Ext.container.Container",{
				border: false,
				items:[
				       {
				    	   xtype: 'label',
				    	   layout:{
								align:'middle'
							},
						   html  : nodate
				       }]
			});
			return panel;
		}else{	
		//有数据时加载饼图
		var jsonArr = new Array();
		if(worklog_me.sumflag==2 || worklog_me.taskTime){
			if(Number(worklog_me.fillstate.normal) != 0){
				var json = {};
				json.name = "正常";
				json.value = worklog_me.fillstate.normal;
				json.itemStyle = {};
				json.itemStyle.normal={};
				json.itemStyle.normal.color = '#00B050';
				jsonArr.push(json);
			}
			if(Number(worklog_me.fillstate.overdue) != 0){
				var json = {};
				json.name = "补填";
				json.value = worklog_me.fillstate.overdue;
				json.itemStyle = {};	
				json.itemStyle.normal={};
				json.itemStyle.normal.color= '#FFFF00';
				jsonArr.push(json);
			}
			if(Number(worklog_me.fillstate.isnull) != 0){
				var json = {};
				json.name = "未填";
				json.value = worklog_me.fillstate.isnull;
				json.itemStyle = {};
				json.itemStyle.normal={};
				json.itemStyle.normal.color = '#FF0000';
				jsonArr.push(json);
			}
		}else{
			if(Number(worklog_me.sumdata.duty) != 0){
				var json = {};
				json.name = "例行工作";
				json.value = worklog_me.sumdata.duty;
				json.itemStyle = {};
				json.itemStyle.normal={};
				json.itemStyle.normal.color = '#52D48C';
				jsonArr.push(json);
			}
			if(Number(worklog_me.sumdata.key) != 0){
				var json = {};
				json.name = "重点工作";
				json.value = worklog_me.sumdata.key;
				json.itemStyle = {};
				json.itemStyle.normal={};
				json.itemStyle.normal.color = '#5FB5FF';
				jsonArr.push(json);
			}
			if(Number(worklog_me.sumdata.oth) != 0){	
				var json = {};
				json.name = "其他工作";
				json.value = worklog_me.sumdata.oth;
				json.itemStyle = {};
				json.itemStyle.normal={};
				json.itemStyle.normal.color = '#BFB1FF';
				jsonArr.push(json);
			}
		}
		var bodyWidth = Ext.getBody().getWidth()*0.25;
		var pieWidth=bodyWidth>300?bodyWidth:300;
		var pieHeight=220;
		var panel = Ext.create("Ext.container.Container",{
				id:'pie_panel',
				width:pieWidth,
				height:'100%',
				border: false,
			 	html:'<div id="echartsPie" style="width:'+pieWidth+'px;height:'+pieHeight+'px"></div>',
			 	listeners:{
			 		render:function(){
			 			var data = [];
			 			if(worklog_me.sumflag==2 || worklog_me.taskTime){
							data = ["正常","补填","未填"];
						}else{
							data = ["例行工作","重点工作","其他工作"];
						}
			 			var echartsPie = echarts.init(document.getElementById('echartsPie'));;
						var option = {
							    tooltip : {
							        trigger: 'item',
							        formatter: function(params){
							        	var str = params.name+"<br/>";
							        	str+="数值："+params.value+(worklog_me.sumflag==2 ||worklog_me.taskTime ?"天":"分钟")+"<br/>";
							        	str+="占比："+Math.round(params.percent)+"%";
							        	return str;
							        }
							    },
							    legend: {
							        orient: 'vertical',
							        right: 10,
							        bottom: 20,
							        data:data
							    },
							    calculable : true,
							    series : [
							        {
							            type:'pie',
							            radius : '80%',//饼图的半径大小
							            hoverOffset:5,
							            center: ['35%', '50%'],//饼图的位置
							            data:jsonArr,
							            label:{
							            	 normal: {
							                    show: false
							                }
							            },
							            labelLine: {
							                normal: {
							                    show: false
							                }
							            }
							        }
							    ]
							}; 
						echartsPie.setOption(option);
			 		}
			 	}
			});															
		
		return panel;
		}
	
	},
	//导出
	exportExcel : function(){
	
		var map = new HashMap();
        map.put("flag","export");
//      map.put("self","self");
        map.put("nbase",worklog_me.nbase);
        map.put("a0100",worklog_me.a0100);
        map.put("taskTime",worklog_me.taskTime?"2":"1");
        map.put("p0100",worklog_me.p0100);
        map.put("datevalue",worklog_me.oneday);
        
        Rpc({functionId:'WP30000001',async:false,success:this.exportOK,scope:this},map);
	},
	
	exportOK : function (response){
		var result = Ext.decode(response.responseText);
		if(result.succeed){
			//zhangh 2020-3-5 下载改为使用VFS
			var outName=result.fileName
			outName = decode(outName);
			var win=open("/servlet/vfsservlet?fromjavafolder=true&fileid="+outName);

		}else{
            Ext.showAlert('导出日志失败！');
            return;
        }
	},
	//人力地图
	initPlan:function () {
		var map = new HashMap();
		map.put("flag","humanmap");
//	    map.put("self","self");
		map.put("nbase",worklog_me.nbase);
		map.put("a0100",worklog_me.a0100);
	    map.put("binit",true);
	    
		Rpc({functionId:'WP30000001',async:false,success:this.init_ok,scope:this},map);
	
	},
	init_ok:function (response) {
		var obj = Ext.decode(response.responseText);
		var strinfo = obj.info;
		strinfo = getDecodeStr(strinfo);
		var planobj = Ext.decode("{"+strinfo+"}");
	
		// 我的照片
		var my_image = obj.my_image;
		var selobj = Ext.getDom("my_image");
		if(selobj && !Ext.isEmpty(my_image)){
			selobj.src = my_image;
		}
		
		// 显示人力地图
		this.displayConcerneders(planobj.human_map);
		
	},
	displayConcerneders:function (human_map) {
		var selobj = document.getElementById("backSuperDiv");
		// 是否显示人力地图下拉图片
		if (wpm.curjsp == "selfplan" && human_map.display_dropdown_img != "") {
			var display_img = human_map.display_dropdown_img;
			displayConcernedImg(display_img);
		}
		wpm.concerned_bteam = human_map.concerned_bteam;
		wpm.concerned_cur_page = human_map.concerned_cur_page;
		var concerned_title = human_map.concerned_title;
		var add_super_flag = false;
		if (wpm.concerned_bteam == "2" || wpm.concerned_bteam == "3") {
			add_super_flag = true;
			if(wpm.concerned_bteam == "2")
				wpm.planType="person";
			else
				wpm.planType="org";
			wpm.sub_object_id = human_map.concerned_objectid;
			this.add_super_concerned(wpm.sub_object_id, wpm.concerned_cur_page, human_map.isperson);
			if (wpm.concerned_bteam == "2")
				concerned_title = concerned_title;
			else
				concerned_title = concerned_title;
			concerned_title = concerned_title;
	
		} else {
			if(wpm.concerned_bteam == "1")
				wpm.planType="org";
			selobj.style.display = "none";
			concerned_title = concerned_title;
			if(wpm.concerned_bteam == "4"){
				concerned_title = "我关注的";
				wpm.planType="person";
			}
		}
		this.setConcernedTitle(concerned_title);
		var worklist = human_map.concerneders;
		//根据人员数，设定人力地图高度
		var personlength = worklist.length;
		var personMapHeight = 50+16+16+45+(personlength*100)+50;
		Ext.getCmp('hrmap').setHeight(personMapHeight);
		
		var teamPageNun = 1;
		if (personlength > 0) {
			Ext.getCmp('hrmap').setHidden(false);
			teamPageNun = parseInt(worklist[0].totalPageNum);
			worklog_me.nodelist = true;
			if(Ext.getBody().getWidth()*0.25 < 300){
				worklog_me.cenlogWidth = (Ext.getBody().getWidth()-300-154-30-16);
			}else{
				worklog_me.cenlogWidth = (Ext.getBody().getWidth()*0.75-154-30-16);
			}
			if(worklog_me.cenlogWidth < 400)
				worklog_me.cenlogWidth = 400;
			//工作内容框宽度 是否有耗时列
			if(!worklog_me.taskTime){
				worklog_me.contentWidth = worklog_me.cenlogWidth-100-76-76-32;
	    	}else{
	    		worklog_me.cenlogWidth = worklog_me.cenlogWidth - 5;
	    		worklog_me.contentWidth = worklog_me.cenlogWidth-100-32;
	    	}
			Ext.getCmp('workContentid').setWidth(worklog_me.contentWidth);
			Ext.getCmp('content').setWidth(worklog_me.cenlogWidth);
			Ext.getCmp('cenlog').setWidth(worklog_me.cenlogWidth);
		}else{
			worklog_me.nodelist = false;
			if(Ext.getBody().getWidth()*0.25 < 300){
				worklog_me.cenlogWidth = (Ext.getBody().getWidth()-300-154-15+132+26-22);
			}else{
				worklog_me.cenlogWidth = (Ext.getBody().getWidth()*0.75-154-15+132+26-22);
			}
			if(worklog_me.cenlogWidth < 400)
    			worklog_me.cenlogWidth = 400;
			if(!worklog_me.taskTime){
				worklog_me.contentWidth = worklog_me.cenlogWidth-100-76-76-32;
	    	}else{
	    		worklog_me.contentWidth = worklog_me.cenlogWidth-100-32;
	    	}
			Ext.getCmp('workContentid').setWidth(worklog_me.contentWidth);
			Ext.getCmp('content').setWidth(worklog_me.cenlogWidth);
    		Ext.getCmp('cenlog').setWidth(worklog_me.cenlogWidth);
		}
		var strhtml = "";
		var xxiaEle = document.getElementById("xxiajpg");
		if (personlength < 8 || teamPageNun == wpm.concerned_cur_page) {
			if (xxiaEle) {
				xxiaEle.style.display = "none";
			}
		} else {
			if (xxiaEle) {
				xxiaEle.style.display = "inline";
				xxiaEle.style.margin = "0 0 0 55px";
			}
		}
		//如果是第一页则隐藏向前翻页的箭头
		var xshangEle = document.getElementById("xshangjpg");
		if(wpm.concerned_cur_page == 1){
			if (xshangEle) {
				xshangEle.style.display = "none";
			}
		}
		
		var justOnce = false;
		for (var i = 0; i < personlength; i++) {
			//截取出部门名称
			var orgname = worklist[i].hintinfo.split("> ")[1];
			if (wpm.concerned_bteam == "2"){// 团队成员
				if (worklist[i].flag == "true") {
							strhtml = strhtml
							+ "<dl><dt><a "
//									"onmouseover='worklog_me.hintMsg(\""+ worklist[i].hintinfo+ "\")'  " +"onmouseout='tt_HideInit()' " +
							+ " href=\"javascript:void(0)\" onclick='worklog_me.clickTeamPeople(\""
							+ worklist[i].name + "\",\""+ worklist[i].objectid + "\",\"" + worklist[i].p0723
							+ "\")'><img class=\"img-circle\" title='"+worklist[i].name+"\n"+orgname+"'  src='"
							+ worklist[i].imagepath + "'/></a></dt>";
							strhtml = strhtml + "<dd>" + worklist[i].name + "</dd>"
					if (worklist[i].subpeople != "") {
						strhtml = strhtml
								+ "<dd>查看 <a href=\"javascript:void(0)\" onclick='worklog_me.clickSubPeople(\""
								+ worklist[i].objectid + "\",\"1\",\""
								+ worklist[i].flag + "\",\"yes\")'>"
								+ worklist[i].subpeople + "</a></dd>"
					}
				} else {
					strhtml = strhtml
							+ "<dl><dt><a "
//									"onmouseover='worklog_me.hintMsg(\"" + worklist[i].hintinfo + "\")' " +" onmouseout='tt_HideInit()' " +
							+ " href=\"javascript:void(0)\"'><img class=\"img-circle\" title='"+worklist[i].name+"\n"+orgname+"'  src='"
							+ worklist[i].imagepath + "'/></a></dt>";
					strhtml = strhtml + "<dd>" + worklist[i].name + "</dd>"
					if (worklist[i].subpeople != "") {
						strhtml = strhtml
								+ "<dd>查看 <a href=\"javascript:void(0)\" onclick='worklog_me.clickSubPeople(\""
								+ worklist[i].objectid + "\",\"1\",\""
								+ worklist[i].flag + "\")'>"
								+ worklist[i].subpeople + "</a></dd>"
					}
				} 
			} 
	
			strhtml = strhtml + "</dl>";
		}
		var selobj = document.getElementById("concernedersdiv");
		selobj.innerHTML = strhtml;
		if (worklist.length < 4){
			selobj.style.height = "400px"
			Ext.getCmp('hrmap').setHeight(550);
		}else{
			selobj.style.height = "";
		}
	},
	setConcernedTitle:function (namevalue) {
		var nameobj = document.getElementById("concernedtitle");
		if (nameobj != null)
			nameobj.innerHTML = namevalue;
	},
	add_super_concerned:function (objectid, curpage, flag) {
		var bfind = false;
		if (wpm.super_concerned_objs.length > 0) {
			for (var i = 0; i < wpm.super_concerned_objs.length; i++) {
				var obj = wpm.super_concerned_objs[i];
				if (obj.objectid == objectid) {
					obj.curpage = curpage;
					obj.flag = flag;
					bfind = true;
					break;
				}
			}
		}
		if (!bfind) {
			var obj = new Object();
			obj.objectid = objectid;
			obj.curpage = curpage;
			obj.flag = flag;
			wpm.super_concerned_objs.push(obj);
		}
		var selobj = document.getElementById("backSuperDiv");
	
		if (wpm.super_concerned_objs.length > 1) {
			selobj.style.display = "block";
		} else {
			selobj.style.display = "none";
		}
	},
	photoshow:function (obj) {
		obj.getElementsByTagName("div")[0].style.display = 'block';
	
	},

	photohide:function (obj) {
		obj.getElementsByTagName("div")[0].style.display = 'none';
	
	},
	clickTeamPeople : function (name, objectid, p0723) {
		worklog_me.name = name;
		wpm.p0723 = p0723;
		needRefresh = "no";
		worklog_me.refreshPlan(objectid);
		needRefresh = "yes";
	},
	hintMsg:function (content) {
//		var config={};
		config.FontSize = '10pt';
		config.FontColor = '#51504E';
		// config.Shadow=true;
		// config.BgImg="/workplan/image/huifu.jpg";
		config.BgColor = "#FFFFFF";
		Tip(content, STICKY, true);
	},
	refreshPlan:function(objectid){
		var map = new HashMap();
		map.put("flag","all");
//	    map.put("self","self");
	    map.put("subobjectid",objectid);
	    map.put("datevalue",worklog_me.oneday);
	    
		Rpc({functionId:'WP30000001',async:false,success:worklog_me.clickSubPeoplelog_ok,scope:this},map);
	},
	clickSubPeoplelog_ok: function(form){
    	
    	var result = Ext.decode(form.responseText);
    	if(result.error == 1){
    		worklog_me.isselfuser = true;
    		Ext.Msg.alert('提示信息',"非自助用户不能使用该功能！");
			return;
    	}
    	if(result.power == 1){
    		worklog_me.isselfuser = true;
    		Ext.Msg.alert('提示信息',"未启用日志功能！");
			return;
    	}
    	worklog_me.section = result.section;
    	if(worklog_me.section == ''){
    		worklog_me.isselfuser = true;
    		Ext.Msg.alert('提示信息',"未启用日志功能！");
			return;
    	}
    	worklog_me.isSelf = result.isSelf;
    	
    	worklog_me.nbase = result.nbase;
    	worklog_me.a0100 = result.a0100;
    	
    	worklog_me.p0100 = result.p0100;
    	worklog_me.p0115 = result.p0115;
    	worklog_me.flag = result.flag;
    	worklog_me.nowMonth = result.nowMonth;
    	worklog_me.datelist = result.dates;
    	worklog_me.codelist = result.codelist;
    	worklog_me.sumdata = result.sumjo;
    	worklog_me.fillstate = result.fillstate;
    	worklog_me.e01a1PlanTask = result.dataE01a1;
    	
    	var store = Ext.getCmp('loggrid').getStore();
    	store.setData(result.tabledata);
    	
    	var store = Ext.getCmp('weekgrid').getStore();
    	store.setData(result.weekdata);
		
		Ext.getCmp('calview').setHtml(worklog_me.calview());
		Ext.getCmp('fillsum').setHtml(worklog_me.fillingSum());
		  
		Ext.getCmp('pieid').removeAll();
		Ext.getCmp('pieid').add(worklog_me.createPieChart());
		  
		Ext.getCmp('dropdownid').setHtml(worklog_me.dropMonthWeek());
		
		var year = worklog_me.oneday.substring(0, 4);
		var month = worklog_me.oneday.substring(5, 7);
		var day = worklog_me.oneday.substring(8);
		
		//选中的日期改变下标示  border_bottom
//		document.getElementById(worklog_me.oneday).setAttribute("class", "border_bottom");
		//日志表格表头更新
		Ext.getCmp('titlehtml').setHtml(worklog_me.logTitle(year, month, day, worklog_me.name));
		
		//更新汇总饼图
		if(worklog_me.sumflag=="1")
			worklog_me.changeMW(worklog_me.sumflag);
		
		//查看别人日志不显示编辑工具
		if(worklog_me.isSelf == '0'){
    		worklog_me.editFunc = false;
    	}else{
    		worklog_me.editFunc = true;
    	}
		
		Ext.getCmp('editbar').setHidden(worklog_me.editFunc);
		Ext.getCmp('editbuts').setHidden(worklog_me.editFunc);
	},
	dropdownAttentionMenu:function () {
		
		var parentobj = Ext.getDom("concerneddivx");
		var left = worklog_me.getElementLeft(parentobj);
		var top = worklog_me.getElementTop(parentobj) + parentobj.offsetHeight;
		top = top + "px";
		left = left + "px";
		box.style.top = top;
		box.style.left = left;
		box.style.width = "153px";
	},
	getElementLeft:function (element) {
		var actualLeft = element.offsetLeft;
		var current = element.offsetParent;
		while (current !== null) {
			actualLeft += current.offsetLeft;
			current = current.offsetParent;
		}
		return actualLeft;
	},
	getElementTop:function (element) {
		var actualTop = element.offsetTop;
		var current = element.offsetParent;
		while (current !== null) {
			actualTop += current.offsetTop;
			current = current.offsetParent;
		}
		return actualTop;
	},
	loadMyPlan:function(){
		worklog_me.name = "";
		var map = new HashMap();
		map.put("flag","all");
//	    map.put("self","self");
		map.put("nbase","");
		map.put("a0100","");
		map.put("datevalue",worklog_me.oneday);
	       
		Rpc({functionId:'WP30000001',async:false,success:this.getMyWorkLogOK,scope:this},map);
	},
	getMyWorkLogOK: function(form){
    	
    	var result = Ext.decode(form.responseText);
    	if(result.error == 1){
    		worklog_me.isselfuser = true;
    		Ext.Msg.alert('提示信息',"非自助用户不能使用该功能！");
			return;
    	}
    	if(result.power == 1){
    		worklog_me.isselfuser = true;
    		Ext.Msg.alert('提示信息',"未启用日志功能！");
			return;
    	}
    	worklog_me.section = result.section;
    	if(worklog_me.section == ''){
    		worklog_me.isselfuser = true;
    		Ext.Msg.alert('提示信息',"未启用日志功能！");
			return;
    	}
    	worklog_me.isSelf = result.isSelf;
    	worklog_me.nbase = result.nbase;
    	worklog_me.a0100 = result.a0100;
    	
    	worklog_me.p0100 = result.p0100;
    	worklog_me.p0115 = result.p0115;
    	worklog_me.flag = result.flag;
    	worklog_me.nowMonth = result.nowMonth;
    	worklog_me.datelist = result.dates;
    	worklog_me.codelist = result.codelist;
    	worklog_me.sumdata = result.sumjo;
    	worklog_me.fillstate = result.fillstate;
    	worklog_me.e01a1PlanTask = result.dataE01a1;
    	
    	var store = Ext.getCmp('loggrid').getStore();
    	store.setData(result.tabledata);
    	
    	var store = Ext.getCmp('weekgrid').getStore();
    	store.setData(result.weekdata);
		
		Ext.getCmp('calview').setHtml(worklog_me.calview());
		Ext.getCmp('fillsum').setHtml(worklog_me.fillingSum());
		  
		Ext.getCmp('pieid').removeAll();
		Ext.getCmp('pieid').add(worklog_me.createPieChart());
		  
		Ext.getCmp('dropdownid').setHtml(worklog_me.dropMonthWeek());
		
		var year = worklog_me.oneday.substring(0, 4);
		var month = worklog_me.oneday.substring(5, 7);
		var day = worklog_me.oneday.substring(8);
		
		//选中的日期改变下标示  border_bottom
		document.getElementById(worklog_me.oneday).setAttribute("class", "border_bottom");
		//日志表格表头更新
		Ext.getCmp('titlehtml').setHtml(worklog_me.logTitle(year, month, day, worklog_me.name));		
		//更新汇总饼图
		if(worklog_me.sumflag=="1")
			worklog_me.changeMW(worklog_me.sumflag);
		
		worklog_me.getOperateButton();
		
	},
	clickSubPeople:function (objectid, curpage, flag, needSeeSub) {
		var map = new HashMap();
		map.put("flag","humanmap");
		map.put("mapflag","1");
	    map.put("binit",false);
	    map.put("subobjectid",objectid);
	    map.put("subpersonflag",flag);
	    map.put("concerned_cur_page",curpage);
	    map.put("needSeeSub",needSeeSub);
	    
		Rpc({functionId:'WP30000001',async:false,success:worklog_me.clickSubPeople_ok,scope:this},map);
	},
	clickSubPeople_ok:function (response) {
		var obj = Ext.decode(response.responseText);
		var strinfo = obj.info;
		strinfo = getDecodeStr(strinfo);
		var planobj = Ext.decode("{"+strinfo+"}");

		worklog_me.displayConcerneders(planobj.human_map);
		if (wpm.concerned_bteam == "1" || wpm.concerned_bteam == "2"
				|| wpm.concerned_bteam == "3") {
			if (planobj.human_map.team_plan_title != null) {
				setPlanTitle(planobj.human_map.team_plan_title);
			}
		}
	},
	downConcerneders:function () {
		var xshangEle = document.getElementById("xshangjpg");
		if (xshangEle) {
			xshangEle.style.display = "inline";
			xshangEle.style.align = "center";
			xshangEle.style.margin = "0 0 0 5px";
		}
		worklog_me.refreshConcerneders(++wpm.concerned_cur_page);
	},

	refreshConcerneders:function (cur_page) {
				
		var map = new HashMap();
		map.put("flag","humanmap");
	    map.put("mapflag","1");
	    map.put("binit",false);
	    map.put("objectId", wpm.sub_object_id);
	    map.put("subobjectid",wpm.sub_object_id);
	    map.put("subpersonflag", wpm.sub_person_flag);
	    map.put("concerned_cur_page",cur_page);
	    
		Rpc({functionId:'WP30000001',async:false,success:worklog_me.refreshConcerneders_ok,scope:this},map);
	},
	refreshConcerneders_ok:function (response) {
		var obj = Ext.decode(response.responseText);
		var strinfo = obj.info;
		strinfo = getDecodeStr(strinfo);
		var planobj = Ext.decode("{"+strinfo+"}");
		var maps = planobj.human_map;
		if ("" == maps.concerneders) {
			if (wpm.concerned_cur_page > 1) {
				--wpm.concerned_cur_page;
			}
			// return;
		}
		worklog_me.displayConcerneders(planobj.human_map);
	},
	upConcerneders:function () {
		if (wpm.concerned_cur_page <= 2) {
			var xshangEle = document.getElementById("xshangjpg");
			if (xshangEle) {
				xshangEle.style.display = "none";
			}
		}
		worklog_me.refreshConcerneders(--wpm.concerned_cur_page);
	},
	backSuper:function () {
		worklog_me.del_last_super_concerned();
		if (wpm.super_concerned_objs.length > 0) {
			var obj = wpm.super_concerned_objs[wpm.super_concerned_objs.length - 1];
			worklog_me.clickSubPeople(obj.objectid, obj.curpage, obj.flag);
			
		}
	},
	del_last_super_concerned:function (objectid, curpage) {
		if (wpm.super_concerned_objs.length > 1) {
			wpm.super_concerned_objs.pop();
		}
	},
	addGridCssMemo : function(value, cell, record, rowIndex, columnIndex, store) {
		cell.style = "font-size:14px;float:left;padding:5px;white-space: normal;word-wrap: break-word;line-height:20px;";
		if (Ext.isEmpty(value))
			return value;
		else{
			var titleVal = value;
			value = value.replace(/</g,'&lt;').replace(/\n/g,'<br>').replace(/ /g,'&nbsp;');
			return '<div title="'+titleVal+'">'+value+'</div>';
	 	}
	}
});
