/**
* 待办任务 页签生成js
* zhaoxg 2016-3-8
*/
Ext.define('TemplateNavigation.DbTask',{
    constructor:function(config){
    	if(Ext.getCmp("dbtask1_toolbar")){//由于点击页签重新加载表格工具，而页签的removeAll（true）未能销毁之前曾经加载过的，导致对象冲突，故有此判断
    		Ext.getCmp("dbtask1_toolbar").destroy();
    	}
    	DbTaskScope = this;
    	DbTaskScope.callBackFunc = config.callBackFunc;//回调函数（itemid，panel）  可用于该组件之渲染
    	DbTaskScope.itemid = config.itemid;//配合回调函数把该组件渲染到的位置
    	DbTaskScope.clienth = config.clienth;
    	DbTaskScope.module_id = config.module_id;
    	DbTaskScope.bs_flag = "";
    	DbTaskScope.query_type = "";
    	DbTaskScope.days = config.days;
    	var map = new HashMap();
    	map.put("module_id",DbTaskScope.module_id);
		map.put("bs_flag","1");
		map.put("query_type","1");
		map.put("days",DbTaskScope.days); 
	    Rpc({functionId:'MB00006002',async:false,success:function(form,action){
	    	var result = Ext.decode(form.responseText);
	    	var flag=result.succeed;
			if(flag==true){
				var templatejson=result.templatejson;
				DbTaskScope.templatejson = templatejson;
		    	var conditions=result.tableConfig;
				var obj = Ext.decode(conditions);
				//配置多选框
				obj.beforeBuildComp=function(config){
					config.tableConfig.selModel={
							selType:'checkboxmodel',
							renderer:function(value,metaData,record){//渲染每行是否显示多选框
									var task_id = record.data.task_id;
									if(task_id != "0")
										 return '<div class="' + Ext.baseCSSPrefix + 'grid-row-checker" role="button" tabIndex="0">&#160;</div>';
								    else
									 	return "";
								}
					}
				};
				DbTaskScope.templateObj = new BuildTableObj(obj);
	  		  	DbTaskScope.init(DbTaskScope.templateObj);
	  		}else{
				Ext.showAlert(result.message);
			}
	    }},map);
    },
   	init:function(templateObj){
  		var taskStore = Ext.create('Ext.data.Store', {
		    fields: ['flag', 'name'],
		    data : [
		        {"flag":"1", "name":MB.LABLE.bpTask},//报批任务
		        {"flag":"3", "name":MB.LABLE.bbTask}//报备任务
		    ]
		});
		var taskType = Ext.create('Ext.form.ComboBox', {//任务类别所用的下拉框
		    store: taskStore,
		    queryMode: 'local',
		    repeatTriggerClick : true,
		    fieldLabel:MB.LABLE.taskType,//'任务类型'
		    labelSeparator: '',
		    //emptyText:"请选择任务类型",//请选择任务类型
		    labelAlign:'right',
		    labelWidth:60,
		    height:24,
		    displayField: 'name',
		    valueField: 'flag',
		    matchFieldWidth:false,
		    editable:false,
		    width:150,
		    fieldStyle:'height:20px;',
			listeners:{
				afterrender:function(combo){
					combo.setValue(taskStore.getAt(0).get('flag'));
					DbTaskScope.bs_flag = combo.getValue();
	     		},
   				select:function(combo,ecords){
					DbTaskScope.bs_flag = combo.getValue();
					if(DbTaskScope.bs_flag=='3')
						Ext.getCmp("batchApproveId").hide();
					else
						Ext.getCmp("batchApproveId").show();
					DbTaskScope.query();
				}
			}
		});
   	  	var templateNameStore = Ext.create('Ext.data.Store', {
			fields:['name','id'],
			data:DbTaskScope.templatejson
		});
		var templateName = Ext.create('Ext.form.ComboBox', {//模板名称下拉框
			id:'template_db_select',
		    store: templateNameStore,
		    queryMode: 'local',
		    margin:'0 10 0 0',
		    repeatTriggerClick : true,
		    fieldLabel:MB.LABLE.templateName,//模板名称
		    labelSeparator: '',
		    //emptyText:"请选择任务类型",//请选择任务类型
		    labelAlign:'right',
		    labelWidth:60,
		    height:24,
		    displayField: 'name',
		    valueField: 'id',
		    matchFieldWidth:false,
		    editable:false,
		    width:250,
		    fieldStyle:'height:20px;',
			listeners:{
				afterrender:function(combo){
					combo.setValue(templateNameStore.getAt(0).get('id'));
					DbTaskScope.tabid = combo.getValue();
	     		},
   				select:function(combo,ecords){
					DbTaskScope.tabid = combo.getValue();
					DbTaskScope.query();
				}
			}
		});
		
		var selectStore = Ext.create('Ext.data.Store', {
		    fields: ['flag', 'name'],
		    data : [
		        {"flag":"1", "name":MB.LABLE.byDate},//"按日期最近"
		        {"flag":"2", "name":MB.LABLE.byTimeDomain}//按时间段
		    ]
		});
		var selectPanel = Ext.create('Ext.form.ComboBox', {//日期下拉框
		    store: selectStore,
		    queryMode: 'local',
		    repeatTriggerClick : true,
		    //fieldLabel:'过滤条件',
		    margin:'0 5 0 10',
		    //emptyText:"请选择过滤方式",
		    labelAlign:'right',
		    labelWidth:60,
		    height:24,
		    displayField: 'name',
		    valueField: 'flag',
		    matchFieldWidth:false,
		    editable:false,
		    width:100,
		    fieldStyle:'height:20px;',
			listeners:{
				afterrender:function(combo){
					combo.setValue(selectStore.getAt(0).get('flag'));
					DbTaskScope.query_type = combo.getValue();
	     		},
   				select:function(combo,ecords){
					DbTaskScope.query_type = combo.getValue();
   					if(DbTaskScope.query_type=="2"){
   						Ext.getCmp('days').hide(); 
   						Ext.getCmp('from').show(); 
   						Ext.getCmp('to').show(); 
   					}else{
   						Ext.getCmp('days').show(); 
   						Ext.getCmp('from').hide(); 
   						Ext.getCmp('to').hide(); 
   					}
				}
			}
		});
		
		var radioPanel = Ext.create('Ext.panel.Panel', {
			border : false,
			layout: 'hbox',
			id:'aaa',
			items:[        	
				{
        			xtype      : 'fieldcontainer',
		            defaultType: 'radiofield',
		            layout: 'hbox',
		            defaults:{flex:1},
	            	items: [
             			 {
				    	   xtype:"container",
				    	   layout: 'hbox',
				    	   id:'days',
				    	   height:24,
				    	   items:[
								{
									xtype:"numberfield",
								    maxValue: 9999, // 最大值
								    minValue: 0, // 最小值 
								    value:DbTaskScope.days,  
								    width:60,
								    fieldStyle:'height:20px;',
								    id:'day',
								    name:'days'
								},
				    	        {
				                	xtype:'label',
				                	margin:'2 0 0 5',
				                	text:'天'
				    	        }
				    	      ]
		                 },{
					        xtype: 'datefield',
					        id:'from',
					        name: 'from',
					        labelAlign:'right',
					        labelWidth:20,
					        hidden:true,
					        width:130,
					        format: 'Y-m-d',
					        formatText:'',//提示信息
					        labelSeparator:'',
					        fieldStyle:'height:20px;',
					        fieldLabel: common.label.from//从
					    },{
					        xtype: 'datefield',
					        id:'to',
					        name: 'to',
					        labelAlign:'right',
					        labelWidth:20,
					        hidden:true,
					        width:130,
					        format: 'Y-m-d',
					        formatText:'',//提示信息
					        labelSeparator:'',
					        fieldStyle:'height:20px;',
					        fieldLabel: common.label.to//到
					    }
		            ]
		         }
		     ]
		})
		var DbTaskPanel = Ext.create('Ext.panel.Panel', {
			id:'dbtaskId',		
			border : false,
			height:DbTaskScope.clienth,
			autoScroll:true,
			margin:"0 0 0 2",//lis 20160513
			layout:'fit',
			items:[templateObj.getMainPanel()]
		})
		
		//查询按钮，lis add 20160607
		var queryBut = Ext.create('Ext.Button', {
		    text: common.button.query,
		    handler: function() {
		        DbTaskScope.query();
		    }
		});
		var toolBar = Ext.getCmp("dbtask1_toolbar");
		toolBar.insert(2,taskType);
		toolBar.insert(3,selectPanel);
		toolBar.insert(4,radioPanel);
		toolBar.insert(5,templateName);
		toolBar.insert(queryBut);
		
		if(DbTaskScope.callBackFunc){
            Ext.callback(eval(DbTaskScope.callBackFunc),null,[DbTaskScope.itemid,DbTaskPanel]);
		}
   	},
   	getTopic:function(value, metaData, Record){//主题
   		var tabid = Record.data.tabid;
		var ins_id = Record.data.ins_id;
		var task_id = Record.data.task_id;
		var ismessage=Record.data.ismessage; //liuyz ismessage是否是通知单起草任务，1是，0不是
		var html="<a href='javascript:DbTaskScope.showCard(\""+tabid+"\",\""+ins_id+"\",\""+task_id+"\",\""+ismessage+"\");'>"+value+"<a/>";
		return html;
	},
	getPri:function(value, metaData, Record){
		var html = "";
		if(value=="1"){
			html = "<img src='"+rootPath+"/images/imail.gif' width='5' height='13'>";
		}else if(value=="0"){
			html = "<img src='"+rootPath+"/images/imailr.gif' width='5' height='13'>";
		}
		return html;
	},
	getBread:function(value, metaData, Record){
		var html = "";
		if(value=="1"){
			html = "<img src='"+rootPath+"/images/mail1.gif' width='18' height='16' title='已阅读'>";
		}else if(value=="0"){
			html = "<img src='"+rootPath+"/images/mail0.gif' width='18' height='16' title='未阅读'>";
		}
		return html;
	},
	getBfile:function(value, metaData, Record){
		var html = "";
		if(value=="1"){
			html = "<img src='"+rootPath+"/images/cc1.gif' width='16' height='16' title='有附件'>";
		}
		return html;
	},
	
	/*
	 * 查询
	 * flag:1是从模板返回时不校验时间
	 */
	query:function(flag){
	    var map = new HashMap();
	    map.put("tabid",DbTaskScope.tabid+"");
    	map.put("module_id",DbTaskScope.module_id);
		map.put("bs_flag",DbTaskScope.bs_flag);
		map.put("query_type",DbTaskScope.query_type);
		
		var day = Ext.getCmp("day").getValue();
		if(day >= 0 && day <=9999)
			map.put("days",day+"");
		else{
			Ext.showAlert(MB.MSG.laterDateError);//"最近日期输入有误，请重新输入！"
			return;
		};
		var fromDate = Ext.getCmp("from").getValue();
		var toDate = Ext.getCmp("to").getValue();
		if(DbTaskScope.query_type == "2" && flag!="1"){
			if(!!!fromDate && !!!toDate){
				Ext.showAlert("开始日期、结束日期不能同时为空！");
				return;
			}
			if(fromDate && toDate && (fromDate>toDate)){
				Ext.showAlert("开始日期不能大于结束日期！");
				return;
			}
		};
		var from = Ext.Date.format(fromDate, "Y-m-d");
		var to = Ext.Date.format(toDate, "Y-m-d");
		map.put("start_date",from);
		map.put("end_date",to); 
	    Rpc({functionId:'MB00006002',async:false,success:function(form,action){
	    	var result = Ext.decode(form.responseText);
	    	var flag=result.succeed;
			if(flag==true){
		    	DbTaskScope.loadTable();
		    	 
				var templatejson=result.templatejson; 
				var tempSelectStore=Ext.getCmp('template_db_select').store; 
				if(DbTaskScope.tabid==-1){
					tempSelectStore.removeAll();
					tempSelectStore.add(templatejson);
				}
		    	
	  		}else{
				Ext.showAlert(result.message);
			}
	    }},map);
	},
	loadTable:function(){ 
		var store = Ext.data.StoreManager.lookup('dbtask1_dataStore'); 
		store.currentPage=1;
		store.load();
	},
	
	//进入任务页面
	showCard:function(tabid,ins_id,task_id,ismessage){
		DbTaskScope.myMask = Ext.getCmp("maskId");
		if(!!!DbTaskScope.myMask){
	      	DbTaskScope.myMask = new Ext.LoadMask({
	      		id:"maskId",
			    target : Ext.getCmp("template")
			});
		}
		DbTaskScope.myMask.show();
       	Ext.Loader.setConfig({
			enabled: true,
			paths: {
				'TemplateMainUL': rootPath+'/module/template/templatemain'
			}
		});
		//liuyz 如果是自助又是通知单起草任务，将module_id置为1（人事异动）走业务流程。
		var module_id=null;
		if(DbTaskScope.module_id=="9"&&ismessage=="1")
    	{
    		module_id="1";
    	}
		var templateObj = new Object();
		templateObj.sys_type="1";
		templateObj.tab_id=tabid;
		templateObj.return_flag="1";
		templateObj.module_id=module_id==null?DbTaskScope.module_id:module_id;
		templateObj.approve_flag=DbTaskScope.bs_flag;
		templateObj.task_id=task_id;
		templateObj.ins_id=ins_id;
		templateObj.callBack_init="DbTaskScope.tempFunc";
		templateObj.callBack_close="DbTaskScope.goBack";
  		Ext.require('TemplateMainUL.TemplateMain', function(){
			TemplateMainGlobal = Ext.create("TemplateMainUL.TemplateMain", {templPropety:templateObj});
		});
	},
	tempFunc:function(){
		DbTaskScope.myMask.hide();
		Ext.getCmp("template").removeAll(false);
		Ext.getCmp("template").add(templateMain_me.mainPanel);
	},
	goBack:function(){
		Ext.getCmp("template").removeAll(true);
		Ext.getCmp("template").add(templatenavigation.tabs);
		DbTaskScope.query("1");
//		DbTaskScope.loadTable();
	},
	
	/**
	 * 删除 lis 20160419
	 */
	deleteTask:function(){
		//获取选中行数据
		var selectRecord = DbTaskScope.templateObj.tablePanel.getSelectionModel().getSelection(true);
		var records = [];
				for(var i=0;i<selectRecord.length;i++){
					var data = selectRecord[i].data;
					var task_id = data.task_id;
					if(task_id != "0")
						records.push(data);
				}
		if(records.length < 1){
			Ext.showAlert("请选择要删除的记录！");
		}else{
			Ext.showConfirm("确认删除吗？",function(id){if(id=='yes'){
				var hashvo = new HashMap();
				hashvo.put("tablekey","dbtask");
				hashvo.put("deletedata",records);
				//将数据传入后台，key为‘deletedata’
				Rpc({functionId:"MB00006008",scope:this,success:function(res){
					var resultObj = Ext.decode(res.responseText);
					if(resultObj.hinttext){
						Ext.showAlert(resultObj.hinttext);
						return;
					}
					DbTaskScope.query();
				}},hashvo);
			}});
		}
	},
	
	/**
	 * 批量审批 lis 20160419
	 */
	batchApprove:function(){
		if(DbTaskScope.bs_flag =="3"){
			Ext.showAlert("报备任务不支持批量审批功能！");
			return;
		}
		//获取选中行数据
		var selectRecord = DbTaskScope.templateObj.tablePanel.getSelectionModel().getSelection();
		//var isSelectAll = DbTaskScope.templateObj.tablePanel.getSelectionModel().doSelectAll;
		//var selectRecordCount = DbTaskScope.templateObj.tablePanel.getStore().getCount();
		var records = [];
		for(var i=0;i<selectRecord.length;i++){
			var data = selectRecord[i].data;
			var task_id = data.task_id;
			if(task_id != "0")
				records.push(data);
		}
		if(records.length == 0){
			Ext.showAlert("未选择审批任务！");
		}else{
			var hashvo = new HashMap();
			hashvo.put("selectdata",records);
			//hashvo.put("doSelectAll",isSelectAll);
			//将数据传入后台，key为‘deletedata’
			Rpc({functionId:"MB00006009",scope:this,success:function(res){
				var result = Ext.decode(res.responseText);
				if(result.succeed){
					DbTaskScope.myMask = new Ext.LoadMask({
					    target : Ext.getCmp("template")
					});
					DbTaskScope.myMask.show();
			       	Ext.Loader.setConfig({
						enabled: true,
						paths: {
							'TemplateMainUL': rootPath+'/module/template/templatemain'
						}
					});
					var templateObj = new Object();
					templateObj.sys_type="1";
					templateObj.tab_id=result.tab_id;
					templateObj.return_flag="1";
					templateObj.module_id=DbTaskScope.module_id;
					templateObj.approve_flag="1";
					templateObj.task_id=result.taskIds;
			//		templateObj.sp_batch="1"; //20160905 dengcan 无用了，ins_id表示当前选中记录的单号
					templateObj.callBack_init="DbTaskScope.tempFunc";
					templateObj.callBack_close="DbTaskScope.goBack";
			  		Ext.require('TemplateMainUL.TemplateMain', function(){
						TemplateMainGlobal = Ext.create("TemplateMainUL.TemplateMain", {templPropety:templateObj});
					});
				}else{
					Ext.showAlert(result.message);
				}
				DbTaskScope.loadTable();	
			}},hashvo);
		}
	}
  }
)