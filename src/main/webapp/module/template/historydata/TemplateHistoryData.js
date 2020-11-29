/*
 * 历史数据
 */
Ext.define('TemplateHistoryDataUL.TemplateHistoryData',{
	constructor:function(config) {
		TemplateHistoryData = this;
   		this.init(config);
	},
	init:function(config) {
		var map = new HashMap();
		TemplateHistoryData.module_id = config.templateHistory.module_id;
  		map.put('module_id',TemplateHistoryData.module_id);
  		map.put("isarchive",'0');
  		map.put('history','true');
	    Rpc({functionId:'MB00006001',success: function(form,action){
			var result = Ext.decode(form.responseText);
			TemplateHistoryData.data=result.data;
			TemplateHistoryData.timeslot = "all";
			TemplateHistoryData.tabid = "-1";
			TemplateHistoryData.createPanel();
	    }},map);
	},
	createPanel:function(){
		var text = MB.CARD.PersonnelBusiness;//人事业务
   		if(TemplateHistoryData.module_id=="7"){
   			text = MB.CARD.UNITCHANGE;//组织机构变动
   		}else if(TemplateHistoryData.module_id=="8"){
   			text = MB.CARD.STATIONCHANGE;//岗位变动
   		}else if(TemplateHistoryData.module_id=="3"){
   			text = MB.CARD.CONTRACTMANAGEMENT;//劳动合同
   		}else if(TemplateHistoryData.module_id=="5"){
   			text = MB.CARD.GOABROAD;//出国办理
   		}else if(TemplateHistoryData.module_id=="4"){
   			text = MB.CARD.INSURANCECHANGE;//保险管理
   		}else if(TemplateHistoryData.module_id=="2"){
   			text = MB.CARD.SALARYCHANGE;//薪资
   		}else if(TemplateHistoryData.module_id=="10"){
   			text = MB.CARD.BUSINESSMANAGEMENT;//考勤
   		}else if(TemplateHistoryData.module_id=="6"){
   			text = MB.CARD.ZGPS;//资格评审
   		}else if(TemplateHistoryData.module_id=="12"){
   			text = MB.CARD.ZSGL;//证照管理
   		}
		var treeStore = Ext.create('Ext.data.TreeStore', {
			root: {
				id:'root',				
				text:text,
				expanded: true,
				icon:rootPath+'/images/add_all.gif',
				children:TemplateHistoryData.data
			}
		});
		TemplateHistoryData.tree = Ext.create('Ext.tree.Panel', {
			    id:'treepanel',
				useArrows: false,
				store: treeStore,
				rootVisible: true,
	            width: 170,
	            region: "west",
	            split:true,
	      		collapseMode:'mini',                 
	            border:true,
	            collapsible: true, 
	            style:'backgroundColor:white', 
	            bodyStyle:"border-left:none;",
	            header:false,
	            listeners: {
	       			itemclick:function(view,record,item,index){
	       			    if (record.get("isCategory") != null && record.get("isCategory") == "1"){
	       			         return;
	       			    }
	       				if(record.get("id") == "root"){
	       					TemplateHistoryData.tabid = "-1";
	       				}else{
	       					TemplateHistoryData.tabid = record.get("id");
	       				}
				       	//跳转历史数据详情页面
	       			    TemplateHistoryData.queryDataList();
	       			},
	       			afterrender:function(e,node){
	       				//选中第一个节点
	       				/*var id = this.getRootNode().firstChild.firstChild.data.id;
	       				TemplateHistoryData.tabid = id;
	       				this.getRootNode().firstChild.expand();
	       				var selected = [];
	       				this.getStore().each(function(r){
	       				   if(id==r.get('id'))
	       					   selected.push(r);
	       				});
	       				var model = this.getSelectionModel();
	       				model.select(selected);*/
	       				TemplateHistoryData.tabid = "-1";
	       				var map = new HashMap();
	       				map.put("transType",'0');
	       		  		map.put('tabid',TemplateHistoryData.tabid);
	       		  	    map.put('module_id',TemplateHistoryData.module_id);
	       			    Rpc({functionId:'MB00008001',success: function(form,action){
	       					var result = Ext.decode(form.responseText);
	       					var conditions = result.tableConfig;
	       					TemplateHistoryData.fieldsMap = result.fieldsMap;
	       					TemplateHistoryData.fieldsArray = result.fieldsArray;
	       					var obj = Ext.decode(conditions);
	       					TemplateHistoryData.templateObj = new BuildTableObj(obj);
	       					Ext.getCmp('templatehistory').add(TemplateHistoryData.templateObj.getMainPanel());
	       					var toolBar = Ext.getCmp("templatehistorydata1_toolbar");
	       					var selectStore = Ext.create('Ext.data.Store', {
	       						id:'selectStore',
	       						fields:['name','id'],
	       						data:[
	       					        {"name":"全部", "id":"all"},
	       					        {"name":"本年", "id":"toyear"},
	       					        {"name":"本季", "id":"toquarter"},
	       					        {"name":"本月", "id":"tomonth"},
	       					        {"name":"时间范围", "id":"timeframe"}
	       					    ]
	       					});
	       					var selectPanel = Ext.create('Ext.form.ComboBox', {
	       					    store: selectStore,
	       					    fieldLabel:'按时间段',
	       					    id:'selectPanel',
	       					    queryMode: 'local',
	       					    repeatTriggerClick : true,
	       					    margin:'0 5 0 0',
	       					    labelAlign:'right',
	       					    labelWidth:50,
	       					    displayField: 'name',
	       					    valueField: 'id',
	       					    editable:false,
	       					    width:130,
	       					    fieldStyle:'height:20px;',
	       						listeners:{
	       							afterrender:function(combo){
	       								var count = selectStore.getCount();
	       								if(count>0){
	       									var id = selectStore.getAt(0).get('id');
	       									if(id)
	       										combo.setValue(id);
	       									else
	       										combo.setValue(TemplateHistoryData.tYear);
	       								}
	       				     		},
	       			   				select:function(combo,records){
	       			   					var value = combo.getValue();
	       			                	if(value=='timeframe'){
	       			                		Ext.getCmp('hisfrom').show(); 
	       			   						Ext.getCmp('histo').show();
	       			   						Ext.getCmp('hisbtn').show();
	       			   						TemplateHistoryData.timeslot = "timeframe";
	       			                	}else{
	       			                		Ext.getCmp('hisfrom').hide(); 
	       			   						Ext.getCmp('histo').hide();
	       			   						Ext.getCmp('hisbtn').hide();
	       			   						if(value=='all'){
	       			   							TemplateHistoryData.tabid = "-1";
	       			   						    TemplateHistoryData.timeslot = "all";
	       			   						}else if(value=='toyear'){
	       			   							TemplateHistoryData.timeslot = "toyear";
	       			   						}else if(value=='toquarter'){
	       			   							TemplateHistoryData.timeslot = "toquarter";
	       			   						}else if(value=='tomonth'){
	       			   							TemplateHistoryData.timeslot = "tomonth";
	       			   						}
	       			   						TemplateHistoryData.queryDataList();
	       			                	}
	       							}
	       						}
	       					});
	       					var _selectPanel = Ext.create('Ext.panel.Panel', {
	       						border : false,
	       						layout: 'hbox',
	       			           	items: [
	       			           		{
	       						        xtype: 'datefield',
	       						        id:'hisfrom',
	       						        name: 'hisfrom',
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
	       						        id:'histo',
	       						        name: 'histo',
	       						        labelAlign:'right',
	       						        labelWidth:20,
	       						        hidden:true,
	       						        width:130,
	       						        format: 'Y-m-d',
	       						        formatText:'',//提示信息
	       						        labelSeparator:'',
	       						        fieldStyle:'height:20px;',
	       						        fieldLabel: common.label.to//至
	       						    },{
	       						    	xtype:'button',
	       						    	id:'hisbtn',
	       						    	text:'查询',
	       						    	margin:'0 3 0 5',
	       						    	hidden:true,
	       						    	handler:function(){
	       						    		var start = Ext.getCmp('hisfrom').getValue();
	       						    		var end = Ext.getCmp('histo').getValue();
	       						    		if(start && end && (start>end)){
	       						    			Ext.showAlert("开始日期不能大于结束日期！");
	       						    			return;
	       						    		}
	       						    		TemplateHistoryData.start = Ext.Date.format(start, "Y-m-d");
	       						    		TemplateHistoryData.end = Ext.Date.format(end, "Y-m-d");
	       						    		TemplateHistoryData.queryDataList();
	       						    	}
	       						    }
	       			            ]
	       					});
	       					var map = new HashMap();
	       					map.put('fieldsMap',TemplateHistoryData.fieldsMap);
	       					TemplateHistoryData.SearchBox = Ext.create("EHR.querybox.QueryBox",{
	       			            emptyText:MB.PROCESSARCHIVING.SEARCHTEXT,
	       			            subModuleId:"templatehistorydata",
	       			            customParams:map,
	       			            funcId:"MB00008001",
	       			            queryBoxWidth:240,
	       			            fieldsArray:TemplateHistoryData.fieldsArray,
	       			            success:TemplateHistoryData.searchEmployOK
	       			        });
	       					
	       					toolBar.insert(selectPanel);
	       					toolBar.insert(_selectPanel);
	       					toolBar.insert(TemplateHistoryData.SearchBox);
	       			    }},map);
	       			}
			    }
			});

       	var tab = Ext.create('Ext.panel.Panel', {
			region: "center",
			style:'backgroundColor:white',
			layout: 'fit',
			border:false,
			id:'templatehistory',
			items: [
				
			]
		})
		var bodyPanel = Ext.create('Ext.panel.Panel', {
	            id:"port",
	            border : 0,
	            layout:'border',
	            style:'backgroundColor:white',             
	            autoScroll:false,
	            items:[TemplateHistoryData.tree,tab]      
	    });
       	Ext.create('Ext.container.Viewport',{
			style:'backgroundColor:white',
			id:"archiveport",
			layout:'fit',
			autoScroll:false,
			items:[bodyPanel]
		});
	},
	searchEmployOK:function(){
		TemplateHistoryData.loadTable();
	},
	showPrint:function(value, metaData, Record){
		var record_id = Record.data.record_id;
		var year = Record.data.year;
		var tabid = Record.data.tabid;
		var task_id = Record.data.task_id_e;
		var ins_id = Record.data.ins_id;
		var archive_id = Record.data.archive_id;
		return "<a href='javascript:void(0);' onclick=TemplateHistoryData.showPrintData('"+record_id+"','"+year+"','"+tabid+"','"+task_id+"','"+ins_id+"','"+archive_id+"')><img src='"+rootPath+"/images/new_module/row_view.png' width='16' height='16' border='0'></a>";
	},
	showPrintData:function(record_id,year,tabid,task_id,ins_id,archive_id){
		var map = new HashMap();
  		var archive_year = year;
       	Ext.Loader.setConfig({
			enabled: true,
			paths: {
				'TemplateMainUL': rootPath+'/module/template/templatemain'
			}
		});
		var templateObj = new Object();
		templateObj.sys_type="1";
		templateObj.tab_id=tabid;
		templateObj.return_flag="14";
		templateObj.module_id=TemplateHistoryData.module_id;
		templateObj.approve_flag="0";
		templateObj.task_id=task_id;
		templateObj.sp_flag="2";
		templateObj.ins_id=ins_id;
		templateObj.view_type='card';
		templateObj.callBack_init="TemplateHistoryData.tempFunc";
		templateObj.callBack_close="TemplateHistoryData.goBack";
		templateObj.other_param="record_id="+record_id+"`archive_id="+archive_id+"`archive_year="+archive_year+"`visible_title=0`isarchive=0";
  		Ext.require('TemplateMainUL.TemplateMain', function(){
			TemplateMainGlobal = Ext.create("TemplateMainUL.TemplateMain", {templPropety:templateObj});
		});
	},
   	tempFunc:function(){
   		var win = Ext.widget('window',{
   			id:'historyWin',
   			title:templateMain_me.tableConfig.title,
   			height:Ext.getBody().getViewSize().height,
   			width:Ext.getBody().getViewSize().width,
   			layout:'fit',
   			resizable : false,
            border : false,
            items : [templateMain_me.mainPanel]
   		});
   		win.show();
	},
	goBack:function(){
		if(Ext.getCmp('historyWin'))
			Ext.getCmp('historyWin').close();
		TemplateHistoryData.loadTable();
	},
	queryDataList:function(){
		var map = new HashMap();
  		map.put('tabid',TemplateHistoryData.tabid);
  		map.put('timeslot',TemplateHistoryData.timeslot);
  		map.put('start',TemplateHistoryData.start);
  		map.put('end',TemplateHistoryData.end);
  		map.put('module_id',TemplateHistoryData.module_id);
  		map.put("transType",'0');
  		map.put('queryType','1');
	    Rpc({functionId:'MB00008001',success: function(form,action){
	    	TemplateHistoryData.loadTable();
	    }},map);
	},
	loadTable:function(){
		var store = Ext.data.StoreManager.lookup('templatehistorydata1_dataStore');
		store.load({page:1});
	},
	export_his:function(flag){
		var selectRecords = TemplateHistoryData.templateObj.tablePanel.getSelectionModel().getSelection();
		if(flag==1&&selectRecords.length<1){
    		Ext.showAlert(MB.PROCESSARCHIVING.SELECTEXPORTDATA);
			return;
    	}
    	var records = [];
    	for(var i=0;i<selectRecords.length;i++){
			records.push(selectRecords[i].data);
		}
    	var map = new HashMap();
    	map.put("selectRecords",records);
    	map.put("flag",flag+"");
    	map.put("transType",'1');
    	Ext.MessageBox.wait(MB.PROCESSARCHIVING.WAITMESSAGE, MB.PROCESSARCHIVING.WAIT);
    	Rpc({functionId:'MB00008001',async:true,success:TemplateHistoryData.outOk,scope:TemplateHistoryData},map);
	},
	outOk:function(form,action){
		Ext.MessageBox.close();
		var result = Ext.decode(form.responseText);
	    if (result.succeed) {
	    	var filename=result.fileName; 
			window.location.target="_blank";
			window.location.href="/servlet/vfsservlet?fromjavafolder=true&fileid="+filename;;
	    }else{
	    	Ext.showAlert(result.message);
	    }
	},
	processArchiving:function(){
		Ext.Loader.setConfig({
			enabled: true,
			paths: {
				'TemplateNavigation': rootPath+'/module/template/templatenavigation'
			}
		});
		Ext.require('TemplateNavigation.TemplateProcessArchiving',function(){
			Ext.create("TemplateNavigation.TemplateProcessArchiving",{callBackFunc:TemplateHistoryData.callBack});
		});
	},
	callBack:function(){
		TemplateHistoryData.queryDataList();
		if(Ext.getCmp("archivewin")){
			Ext.getCmp("archivewin").close();
		}
	}
});