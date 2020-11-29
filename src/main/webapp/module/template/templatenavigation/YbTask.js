/**
* 已办任务 页签生成js
* zhaoxg 2016-3-8
*/
Ext.define('TemplateNavigation.YbTask',{
   constructor:function(config){
   		if(Ext.getCmp("ybtask1_toolbar")){//由于点击页签重新加载表格工具，而页签的removeAll（true）未能销毁之前曾经加载过的，导致对象冲突，故有此判断
    		Ext.getCmp("ybtask1_toolbar").destroy();
    	}
    	YbTaskScope = this;
    	YbTaskScope.callBackFunc = config.callBackFunc;//回调函数（itemid，panel）  可用于该组件之渲染
    	YbTaskScope.itemid = config.itemid;//配合回调函数把该组件渲染到的位置
    	YbTaskScope.clienth = config.clienth;
    	YbTaskScope.module_id = config.module_id;
    	YbTaskScope.bs_flag = "";
    	YbTaskScope.query_type = "";
    	YbTaskScope.days = config.days;
    	var map = new HashMap();
    	map.put("flag","0");//0:首次进入 1：查询进入
    	map.put("module_id",YbTaskScope.module_id);
		map.put("bs_flag","1");
		map.put("query_type","1");
		map.put("days",YbTaskScope.days); 
	    Rpc({functionId:'MB00006003',async:false,success:function(form,action){
	    	var result = Ext.decode(form.responseText);
	    	var flag=result.succeed;
			if(flag==true){
				var templatejson=result.templatejson;
				YbTaskScope.templatejson = templatejson;
		    	var conditions=result.tableConfig;
				var obj = Ext.decode(conditions);
				var templateObj = new BuildTableObj(obj);
	  		  	YbTaskScope.init(templateObj);
	  		}else{
				Ext.showAlert(result.message);
			}
	    }},map);
    },
   	init:function(templateObj){
  		var taskStore = Ext.create('Ext.data.Store', {
		    fields: ['flag', 'name'],
		    data : [
		        {"flag":"1", "name":"报批任务"},
		        {"flag":"3", "name":"报备任务"}
		    ]
		});
		var taskType = Ext.create('Ext.form.ComboBox', {//任务类别所用的下拉框
		    store: taskStore,
		    queryMode: 'local',
		    repeatTriggerClick : true,
		    fieldLabel:'任务类型',
		    labelSeparator: '', 
		    labelWidth:48,
		    displayField: 'name',
		    valueField: 'flag',
		    matchFieldWidth:false,
		    editable:false,
		    width:140,
		    fieldStyle:'height:20px;',
			listeners:{
				afterrender:function(combo){
					combo.setValue(taskStore.getAt(0).get('flag'));
					YbTaskScope.bs_flag = combo.getValue();
	     		},
   				select:function(combo,ecords){
					YbTaskScope.bs_flag = combo.getValue();
					YbTaskScope.query();
				}
			}
		});
   	  	var templateNameStore = Ext.create('Ext.data.Store', {
			fields:['name','id'],
			data:YbTaskScope.templatejson
		});
		var templateName = Ext.create('Ext.form.ComboBox', {//模板名称下拉框
			id:'template_yb_select',
		    store: templateNameStore,
		    queryMode: 'local',
		    repeatTriggerClick : true,
		    fieldLabel:'模板名称',
		    labelSeparator: '', 
		    margin:'0 10 0 0',
		    labelAlign:'right',
		    labelWidth:60,
		    displayField: 'name',
		    valueField: 'id',
		    matchFieldWidth:false,
		    editable:false,
		    width:250,
		    fieldStyle:'height:20px;',
			listeners:{
				afterrender:function(combo){
					combo.setValue(templateNameStore.getAt(0).get('id'));
					YbTaskScope.tabid = combo.getValue();
	     		},
   				select:function(combo,ecords){
					YbTaskScope.tabid = combo.getValue();
					YbTaskScope.query();
				}
			}
		});
		var selectStore = Ext.create('Ext.data.Store', {
		    fields: ['flag', 'name'],
		    data : [
		        {"flag":"1", "name":"按日期最近"},
		        {"flag":"2", "name":"按时间段"}
		    ]
		});
		var selectPanel = Ext.create('Ext.form.ComboBox', {//日期下拉框
		    store: selectStore,
		    queryMode: 'local',
		    repeatTriggerClick : true,
		    //fieldLabel:'过滤条件',
		    margin:'0 5 0 10',
		    labelAlign:'right',
		    labelWidth:60,
		    displayField: 'name',
		    valueField: 'flag',
		    matchFieldWidth:false,
		    editable:false,
		    width:100,
		    fieldStyle:'height:20px;',
			listeners:{
				afterrender:function(combo){
					combo.setValue(selectStore.getAt(0).get('flag'));
					YbTaskScope.query_type = combo.getValue();
	     		},
   				select:function(combo,ecords){
					YbTaskScope.query_type = combo.getValue();
   					if(YbTaskScope.query_type=="2"){
   						Ext.getCmp('ybdays').hide(); 
   						Ext.getCmp('ybfrom').show(); 
   						Ext.getCmp('ybto').show(); 
   					}else{
   						Ext.getCmp('ybdays').show(); 
   						Ext.getCmp('ybfrom').hide(); 
   						Ext.getCmp('ybto').hide(); 
   					}
				}
			}
		});
		
		var radioPanel = Ext.create('Ext.panel.Panel', {
			border : false,
			layout: 'hbox',
           	items: [
           			{
        		    	xtype:"container",
        		    	layout: 'hbox',
        		    	id:'ybdays',
        		    	items:[
        						{
        							xtype:"numberfield",
        						    maxValue: 9999, // 最大值
        						    minValue: 0, // 最小值 
        						    value:YbTaskScope.days,  
        						    width:60,
        						    id:'ybday',
        						    fieldStyle:'height:20px;',
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
			        id:'ybfrom',
			        name: 'ybfrom',
			        labelAlign:'right',
			        labelWidth:20,
			        hidden:true,
			        format: 'Y-m-d',
			        formatText:'',//提示信息
			        labelSeparator:'',
			        fieldStyle:'height:20px;',
			        fieldLabel: common.label.from//从
			    },{
			        xtype: 'datefield',
			        id:'ybto',
			        name: 'ybto',
			        labelAlign:'right',
			        labelWidth:20,
			        hidden:true,
			        format: 'Y-m-d',
			        formatText:'',//提示信息
			        labelSeparator:'',
			        fieldStyle:'height:20px;',
			        fieldLabel: common.label.to//到
			    }
            ]
		})
		var YbTaskPanel = Ext.create('Ext.panel.Panel', {
			id:'ybtaskId',		
			border : false,
			height:YbTaskScope.clienth,
			autoScroll:true,
			margin:"0 0 0 2",//lis 20160513
			layout:'fit',
			items:[templateObj.getMainPanel()]
		});
		
		//查询按钮，lis add 20160607
		var queryBut = Ext.create('Ext.Button', {
		    text: common.button.query,
		    handler: function() {
		        YbTaskScope.query();
		    }
		});
		var toolBar = Ext.getCmp("ybtask1_toolbar");
		toolBar.insert(taskType);
		toolBar.insert(selectPanel);
		toolBar.insert(radioPanel);
		toolBar.insert(templateName);
		toolBar.insert(queryBut);
		
		if(YbTaskScope.callBackFunc){
            Ext.callback(eval(YbTaskScope.callBackFunc),null,[YbTaskScope.itemid,YbTaskPanel]);
		}
   	},
	getSploop:function(value, metaData, Record){
	   	var tabid = Record.data.tabid;
		var ins_id = Record.data.ins_id;
		var task_id = Record.data.task_id;
		var name = Record.data.task_topic;
		name = getEncodeStr(name);
		var flag = Record.data.flag;
		flag = getEncodeStr(flag);
		return "<a href='javascript:void(0);' onclick=YbTaskScope.showCard('"+tabid+"','"+ins_id+"','"+task_id+"','"+name+"','"+flag+"')><img src='"+rootPath+"/images/view.gif' width='16' height='16' border='0'></a>";
	},
	showCard:function(tabid,ins_id,task_id,name,flag){
		var obj = new Object();
	
		obj.tabid=tabid; 
	    obj.task_id=task_id;
	    if(YbTaskScope.module_id=='7')
    	  obj.infor_type="2";
	    else if(YbTaskScope.module_id=='8')
    	  obj.infor_type="3";
	    else
    	  obj.infor_type="1"; 
		obj.return_flag="2";  
		var isDelete = false;
		name = getDecodeStr(name);
		flag = getDecodeStr(flag);
		//syl 已办任务中 穿透进去表单
		//bug 58116 V77包：人事异动：审批人在首页待办任务进入表单，点“撤销”操作后，单据在“已办任务”页面不显示。
		if(name.indexOf(MB.MSG.bydelete)!=-1&&flag == "终止"){
			isDelete = true;
			obj.return_flag="4";
			obj.isDelete=isDelete;
		}
		Ext.Loader.setConfig({
			enabled: true,
			paths: {
				'TemplateViewProcessUL':rootPath+'/module/template/templatetoolbar/viewprocess'
			}
		});
	   	Ext.require('TemplateViewProcessUL.TemplateViewProcess',function(){
			Ext.create("TemplateViewProcessUL.TemplateViewProcess",obj);
		});
	},
	getBrowsePrint:function(value, metaData, Record){
		var tabid = Record.data.tabid;
		var ins_id = Record.data.ins_id;
		var task_id = Record.data.task_id;
		//syl 已办任务中 穿透进去表单
		//bug 58116 V77包：人事异动：审批人在首页待办任务进入表单，点“撤销”操作后，单据在“已办任务”页面不显示。
		var name = Record.data.task_topic;
		name = getEncodeStr(name);
		var flag = Record.data.flag;
		flag = getEncodeStr(flag);
		return "<a href='javascript:void(0);' onclick=YbTaskScope.showPrint('"+tabid+"','"+ins_id+"','"+task_id+"','"+name+"','"+flag+"')><img src='"+rootPath+"/images/new_module/row_view.png' width='16' height='16' border='0'></a>";
	},
	query:function(flag){
	    var map = new HashMap();
	    map.put("flag","1");//0:首次进入 1：查询进入
	    map.put("tabid",YbTaskScope.tabid+"");
    	map.put("module_id",YbTaskScope.module_id);
		map.put("bs_flag",YbTaskScope.bs_flag);
		map.put("query_type",YbTaskScope.query_type);
		var day = Ext.getCmp("ybday").getValue();
		if(day >= 0 && day <=9999)
			map.put("days",day+"");
		else{
			Ext.showAlert(MB.MSG.laterDateError);//"最近日期输入有误，请重新输入！"
			return;
		};
		
		var fromDate = Ext.getCmp("ybfrom").getValue();
		var toDate = Ext.getCmp("ybto").getValue();
		if(YbTaskScope.query_type == "2" && flag!="1" && fromDate && toDate && (fromDate>toDate)){
			Ext.showAlert("开始日期不能大于结束日期！");
			return;
		};
		var from = Ext.Date.format(fromDate, "Y-m-d");
		var to = Ext.Date.format(toDate, "Y-m-d");
		map.put("start_date",from);
		map.put("end_date",to); 
	    Rpc({functionId:'MB00006003',async:false,success:function(form,action){
	    	var result = Ext.decode(form.responseText);
	    	var flag=result.succeed;
			if(flag==true){
		    	YbTaskScope.loadTable();
		    	
		    	var templatejson=result.templatejson; 
				var tempSelectStore=Ext.getCmp('template_yb_select').store; 
	  		    tempSelectStore.removeAll();
				tempSelectStore.add(templatejson);   
		    	
	  		}else{
				Ext.showAlert(result.message);
			}
	    }},map);
	},
	loadTable:function(){
		var store = Ext.data.StoreManager.lookup('ybtask1_dataStore');
		store.currentPage=1;
		store.load();
	},
	getTopic:function(value, metaData, Record){//主题
   		var tabid = Record.data.tabid;
		var ins_id = Record.data.ins_id;
		var task_id = Record.data.task_id;
		//syl 已办任务中 穿透进去表单
		//bug 58116 V77包：人事异动：审批人在首页待办任务进入表单，点“撤销”操作后，单据在“已办任务”页面不显示。
		var name = Record.data.task_topic;
		name = getEncodeStr(name);
		var flag = Record.data.flag;
		flag = getEncodeStr(flag);
		var html="<a href=\"javascript:YbTaskScope.showPrint('"+tabid+"','"+ins_id+"','"+task_id+"','"+name+"','"+flag+"');\" >"+value+"<a/>";
		return html;
	},
	
	showPrint:function(tabid,ins_id,task_id,name,flag){
		YbTaskScope.myMask = Ext.getCmp("maskId");
      	if(!!!YbTaskScope.myMask){
	      	YbTaskScope.myMask = new Ext.LoadMask({
	      		id:"maskId",
			    target : Ext.getCmp("template")
			});
		}
		YbTaskScope.myMask.show();
       	Ext.Loader.setConfig({
			enabled: true,
			paths: {
				'TemplateMainUL': rootPath+'/module/template/templatemain'
			}
		});
		var templateObj = new Object();
		templateObj.sys_type="1";
		templateObj.tab_id=tabid;
		templateObj.return_flag="2";
		templateObj.module_id=YbTaskScope.module_id;
		templateObj.approve_flag="0";
		templateObj.task_id=task_id;
		templateObj.sp_flag="2";
		templateObj.ins_id=ins_id;
		templateObj.callBack_init="YbTaskScope.tempFunc";
		templateObj.callBack_close="YbTaskScope.goBack";
		var isDelete = false;
		name = getDecodeStr(name);
		flag = getDecodeStr(flag);
		//syl 已办任务中 穿透进去表单
		//bug 58116 V77包：人事异动：审批人在首页待办任务进入表单，点“撤销”操作后，单据在“已办任务”页面不显示。
		if(name.indexOf(MB.MSG.bydelete)!=-1&&flag == "终止"){
			isDelete = true;
			templateObj.return_flag="4";
			templateObj.other_param="isDelete="+isDelete+"";
		}
  		Ext.require('TemplateMainUL.TemplateMain', function(){
			TemplateMainGlobal = Ext.create("TemplateMainUL.TemplateMain", {templPropety:templateObj});
		});
	},
	tempFunc:function(){
		YbTaskScope.myMask.hide();
		Ext.getCmp("template").removeAll(false);
		Ext.getCmp("template").add(templateMain_me.mainPanel);
	},
	goBack:function(){
		Ext.getCmp("template").removeAll(true);
		Ext.getCmp("template").add(templatenavigation.tabs);
		YbTaskScope.query("1");
		//YbTaskScope.loadTable(); ie8刷新出现表格列错乱问题
	},
	//显示审批人角色详细
    getRoleInfo:function(value, metaData, Record){
		var tabid = Record.data.tabid;
		var actorname = Record.data.sp_info;//.split(",");
		var task_id = Record.data.task_id;
		var actor_type = Record.data.actor_type;//.split(",");
		var ins_id = Record.data.ins_id;
		/*var actor_name = "";
		for(var i=0;i<actor_type.length;i++){
			if(actor_type[i]=='2')
				actor_name+= "<a href='javascript:void(0);' onclick=YbTaskScope.showRoleInfo(this,\""+tabid+"\",\""+task_id+"\",\""+ins_id+"\")>"+actorname[i]+"<a/>";
			else
				actor_name+=actorname[i];
			if(i!=actor_type.length-1)
				actor_name+=",";
		}*/
		return actorname;
	},
	showRoleInfo:function(e,tabid,task_id,ins_id){
	    Ext.Loader.setConfig({
			enabled: true,
			paths: {
				'TemplateNavigationOther': rootPath+'/module/template/templatenavigation/other'
			}
		});
		Ext.require('TemplateNavigationOther.DisplayRoleInfo',function(){
			Ext.create("TemplateNavigationOther.DisplayRoleInfo",{element:e,tabid:tabid,task_id:task_id,flag:'1',ins_id:ins_id});
		});
	}
  }
)