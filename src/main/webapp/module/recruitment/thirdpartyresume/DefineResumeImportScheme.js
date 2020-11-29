/**
 * 子集界面调用js
 * 
 * @author zhancq
 */
var  order = "";
Ext.define('ShowThirdPartyRensumeParmURL.DefineResumeImportScheme', {
	
	requires:[
	    'EHR.extWidget.field.CodeSelectField',
	    'EHR.extWidget.proxy.TransactionProxy',
		'EHR.tableFactory.TableController'
	],
	constructor : function(config) {
		Ext.util.CSS.createStyleSheet(".x-nbr .x-window-default{background-color : white!important}");
		defineResumeScheme = this;
		defineResumeScheme.name = config.name;
		defineResumeScheme.table = "";
		this.init();
	},
	// 初始化函数
	init : function() {
		var map = new HashMap();
		map.put("name",defineResumeScheme.name);
		Rpc( {
			functionId : 'ZP0000002604',
			success : defineResumeScheme.initCombox 
		}, map);
		
	},
	
	initCombox : function(param){
		var value = param.responseText;
		var map = Ext.decode(value);
		var rzColumn =Ext.decode(map.rzColumn);
	    var rzValue = Ext.decode(map.rzValue);
	    var configs = {
	    		prefix : "rensumeFieldset",
				editable : true,
				selectable : false,
				storedata : rzValue,
				clickToEdit:2,
				tablecolumns : rzColumn,
				datafields : [ 'resumeItem', 'userItem', 'codeIndex' ]
	    };
	    
	    var rensumeFieldset = new BuildTableObj(configs);
	    var table = rensumeFieldset.getMainPanel();
	    table.setRegion("center");
	    defineResumeScheme.table = rensumeFieldset.getMainPanel();
	    var tablePanel = rensumeFieldset.tablePanel;
	    
	    var dataStore = Ext.create('Ext.data.Store', {
	    	fields:['name','id'],
	    	data: map.userList,
	    	autoLoad: true
	    });
	    
	    var itemList = Ext.create('Ext.data.Store', {
	    	fields:['name','id'],
	    	data: map.itemIDList,
	    	autoLoad: true
	    });
	    
	    var type = Ext.create('Ext.data.Store', {
	    	fields: ['id', 'name'],
	    	data : [
	    	        {"id":"1",  "name":"简历已存在时不导入，不存在时导入"},
	    	        {"id":"2", "name":"简历已存在时替换，不存在时导入"},
	    	        {"id":"3", "name":"简历已存在时追加，不存在时导入"},
	    	        {"id":"4", "name":"简历已存在时替换，不存在时不导入"}
	    	        ]
	    });	

	    if("BeiSen" != map.name){
	    	type = Ext.create('Ext.data.Store', {
		    	fields: ['id', 'name'],
		    	data : [
		    	   {"id":"1", "name":"简历已存在时不导入，不存在时导入"},
		    	   {"id":"2", "name":"简历已存在时替换，不存在时导入"},
		    	   {"id":"3", "name":"简历已存在时追加，不存在时导入"}
		    	]
		    });	
	    }
	    
	    var synchronous = Ext.create('Ext.data.Store', {
	    	fields: ['id', 'name'],
	    	data : map.synchronousList
	    });	
	    
	    var bbar = Ext.create("Ext.Toolbar", {
	    	buttonAlign : "center",
	    	border:false,
	    	items : [{
	    		text : "保存",
	    		iconCls : '',
	    		handler : function(){
	    		order = "1";
	    		var  personBase =  Ext.getCmp("personBaseId").getValue();
	    		var  mainItem = Ext.getCmp("mainItem").getValue();
	    		var secondItem = Ext.getCmp("secondItem").getValue();
	    		var impType = Ext.getCmp("impType").getValue();
	    		var combo ="";
	    		if(Ext.isEmpty(personBase)){
	    			Ext.showAlert("人员库不能为空！");
	    			return;
	    		}
	    		
	    		if(Ext.isEmpty(mainItem)) {
	    			Ext.showAlert("关键指标不能为空！");
	    			return;
	    		}
	    		
	    		if(Ext.isEmpty(secondItem))
	    			secondItem = "";
	    		
	    		if(Ext.isEmpty(impType)) {	
	    			Ext.showAlert("导入类型不能为空！");
	    			return;
	    		}
	    		
	    		var  synchronousFlag  = "";
	    		var phoneBox = Ext.getCmp("synchronousFlag");
	    		if(!phoneBox.hidden){
	    			synchronousFlag = phoneBox.getValue();
	    			if(Ext.isEmpty(synchronousFlag)) {	
	    				Ext.showAlert("同步标识不能为空！");
	    				return;
	    			}
	    		}
	    		
	    		
	    		var codelist = map.codelist;
	    		var codearray = new Array();
	    		var codearray1 = new Array();
	    		var codeMap = new  HashMap();
	    		var defineMap = new  HashMap();
	    		var celldata = rensumeFieldset.tablePanel.getStore();
	    		var selected = ""; 
	    		var resumeset = ""; 
	    		var resumesetid = "";
	    		var  selecteds  = "";
	    		
	    		for(var i = 0;i < codelist.length;i++){
	    			var   comValue=celldata.getAt(i).data.userItem;
	    			var  selected = "";
	    			if(comValue!=null && comValue!="" && comValue.indexOf("`")>-1) {
	    				selected = comValue.split("`")[0];
	    			}
	    			resumeset = codelist[i].resumeset;
	    			resumesetid = codelist[i].resumesetid;
	    			codearray[0] = selected;
	    			codearray[1] = resumeset;
	    			codearray[2] = resumesetid;
	    			var jsons = {"resumeset":resumeset,"selected":selected,"resumesetid":resumesetid};
	    			var obj2 = eval(jsons);  
	    			codearray1.push(obj2);
	    		}
	    		
	    		defineMap.put("userbase",personBase);
	    		defineMap.put("itemID",mainItem);
	    		defineMap.put("secitemID",secondItem);
	    		defineMap.put("mode",impType);
	    		defineMap.put("codearray",JSON.stringify(codearray1));
	    		defineMap.put("name",defineResumeScheme.name);
	    		defineMap.put("synchronousFlag",synchronousFlag);
	    		Rpc( {
	    			functionId : 'ZP0000002608',
	    			success : function (param){
	    			rensumeFieldset.tablePanel.getStore().reload();
	    			var value = param.responseText;
	    			var returnMap = Ext.decode(value);
	    			if(returnMap.succeed){
	    				Ext.showAlert("保存成功！");
	    			}
	    		}
	    		}, defineMap);
	    		
	    	}
	    	}, 
	    	'->', {
	    		text : "代码对应",
	    		iconCls : '',
	    		handler : defineResumeScheme.codeThirdPartyRensume
	    	},
	    	'->',
	    	{
	    		text : "返回",
	    		iconCls : '',
	    		handler : defineResumeScheme.backThirdPartyRensume
	    	}] 
	    });					
	    
	    var tbar = Ext.create("Ext.Toolbar", {
	    	buttonAlign: 'center',
	    	border:false,
	    	items : [{
	    		xtype: 'combobox',
	    		fieldLabel: '人员库',
	    		store: dataStore,
	    		style:'margin-left:5px',
	    		id: 'personBaseId',
	    		emptyText: "请选择人员库",
	    		editable:false,
	    		labelWidth: 38,
	    		queryMode: 'local',
	    		width : 170,
	    		labelSeparator:'',
	    		displayField: 'name',
	    		valueField: 'id',
	    		triggerAction : 'all',
	    	},{
	    		xtype: 'combobox',
	    		labelSeparator:'',
	    		style:'text-align:right',
	    		fieldLabel: '关键指标',
	    		labelWidth: 60,
	    		id : 'mainItem',
	    		emptyText: "请选择关键指标",
	    		store: itemList,
	    		queryMode: 'local',
	    		editable:false,
	    		width : 250,  
	    		displayField: 'name',
	    		valueField: 'id',
	    		triggerAction : 'all'
	    	}, {
	    		xtype: 'combobox',
	    		fieldLabel: '次关键指标',
	    		labelWidth: 70,
	    		emptyText: "请选择次关键指标",
	    		editable:false,
	    		labelSeparator:'',
	    		style:'text-align:right',
	    		id : 'secondItem',
	    		store: itemList,
	    		width : 240,
	    		queryMode: 'local',
	    		displayField: 'name',
	    		valueField: 'id',
	    		triggerAction : 'all'
	    	},{
	    		xtype: 'combobox',
	    		fieldLabel: '导入方式',
	    		labelWidth: 60,
	    		labelSeparator:'',
	    		style:'text-align:right',
	    		id : 'impType',
	    		editable:false,
	    		store: type,
	    		width : 320,  
	    		queryMode: 'local',
	    		displayField: 'name',
	    		valueField: 'id',
	    		triggerAction : 'all',
	    		listeners:{
	    			select: function(combo, record, eOpts) {
	    				if("4" == record.id && "BeiSen" == defineResumeScheme.name)
	    					Ext.getCmp('synchronousFlag').show();
	    				else
	    					Ext.getCmp('synchronousFlag').hide();
	    			} 
	    		}
	    	},{
	    		xtype: 'combobox',
	    		fieldLabel: '同步标识',
	    		labelWidth: 60,
	    		labelSeparator:'',
	    		style:'text-align:right',
	    		emptyText: "请选同步标识指标",
	    		id : 'synchronousFlag',
	    		editable:false,
	    		store: synchronous,
	    		width : 250,  
	    		queryMode: 'local',
	    		displayField: 'name',
	    		valueField: 'id',
	    		hidden:true,
	    		triggerAction : 'all'
	    	}]
	    });
	    
	    Ext.getCmp("mainItem").setValue(map.itemID);
	    Ext.getCmp("personBaseId").setValue(map.dbname);
	    Ext.getCmp("secondItem").setValue(map.secitemID);
	    Ext.getCmp("synchronousFlag").setValue(map.synchronousFlag);
	    if(Ext.isEmpty(map.mode))
	    	Ext.getCmp("impType").setValue(1);  
	    else
	    	Ext.getCmp("impType").setValue(map.mode);
	    
	    if("4" == map.mode && "BeiSen" == defineResumeScheme.name)
	    	Ext.getCmp("synchronousFlag").show();	    	
	    
	    var pannel = Ext.create('Ext.panel.Panel', {
	    	id:'pannel',
	    	buttonAlign: 'center',
	    	border : false,
	    	stripeRows : true,
	    	autoScroll : true,
	    	enableColumnMove : false,
	    	trackMouseOver : false,
	    	layout : 'border',
	    	tbar:[tbar],
	    	loadMask : {
	    	msg : '数据加载中...'
	    },
	    items : [table],
	    bbar:   [bbar],
	    
	    }); 
	    
	    var win = Ext.getCmp("defineScheme");
	    if(win)
	    	win.close();
	    
	    win = new Ext.window.Window({
	    	title : '定义方案',
	    	renderTo : Ext.getBody(),
			maximized : true,
			border : false,
			id : 'defineScheme',
			closable : false,
			autoScroll : true,
			items:[pannel],
			layout : 'fit',
			border : false
		});
	    
	    win.show();
	},
	    
	codeThirdPartyRensume:function(){
		var map = new HashMap();
		map.put("from_flag","1");
		map.put("resumeID","");
		map.put("name",defineResumeScheme.name);
		Ext.require('ShowThirdPartyRensumeParmURL.codeCorrespond', function(){
			Ext.create("ShowThirdPartyRensumeParmURL.codeCorrespond", map);
		});
	},
				
	changeCombo : function (value){
		return value.substring(value.indexOf('`')+1,value.length);
	},
	
	thirdPartyRensumeManagePage:function(view, rowIndex, colIndex, item, e, record, row){
		var fieldlist = record.data.userItem;
		var fieldset = "";
		if(fieldlist != null && fieldlist != "")
			fieldset = record.data.userItem.substring(0,3);
		
		var   map = new HashMap();
		map.put("resumeset",record.data.resumeItem);
		map.put("fieldset",fieldset);
		map.put("name",defineResumeScheme.name);
		Ext.require('ShowThirdPartyRensumeParmURL.ItemCorrespond', function(){
			Ext.create("ShowThirdPartyRensumeParmURL.ItemCorrespond", map);
		});
	},
	
	backThirdPartyRensume:function(){
		var win = Ext.getCmp("defineScheme");
		if(win)
			win.close();
	}
	
});
	