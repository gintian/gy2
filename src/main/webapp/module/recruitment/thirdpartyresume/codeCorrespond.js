/**
 * 代码对应调用js
 * 
 * @author zhancq
 */
Ext.define('ShowThirdPartyRensumeParmURL.codeCorrespond', {
	requires:[
	    'EHR.extWidget.field.CodeSelectField',
	    'EHR.extWidget.proxy.TransactionProxy',
		'EHR.tableFactory.TableController'
	],
		
	constructor : function(config) {
		Ext.util.CSS.createStyleSheet(".x-nbr .x-window-default{background-color : white!important}");
		thirdPartyRensumeCodeItem = this;
	    thirdPartyRensumeCodeItem.from_flag = config.from_flag;
		thirdPartyRensumeCodeItem.resumeID = config.resumeID;
		thirdPartyRensumeCodeItem.resumeset = config.resumeset;
		thirdPartyRensumeCodeItem.fieldset = config.fieldset;
		thirdPartyRensumeCodeItem.name = config.name;
		thirdPartyRensumeCodeItem.table = "";
		thirdPartyRensumeCodeItem.selectId = "";
		this.init();
	 },
	// 初始化函数
	init : function() {
		var map = new HashMap();
		map.put("resumeID",thirdPartyRensumeCodeItem.resumeID);
		map.put("from_flag",thirdPartyRensumeCodeItem.from_flag);
		map.put("name",thirdPartyRensumeCodeItem.name);
		Rpc( {
			functionId: 'ZP0000002606',
			success: thirdPartyRensumeCodeItem.initCombox
		}, map);
	},
	
	initCombox : function(param){
		var codeBbar  = "";
		var value = param.responseText;
		var map = Ext.decode(value);
		var rzColumn =Ext.decode(map.rzColumn);
		var rzValue = Ext.decode(map.rzValue);
		var configs = {
				prefix : "codeItem",
				editable : true,
				selectable : false,
				storedata : rzValue,
				clickToEdit:2,
				tablecolumns : rzColumn,
				datafields : [ 'resumeInfo', 'userItemId','userItemName' ]
		};
		
		var codeItemtable = new BuildTableObj(configs);
		var codeItemTable = codeItemtable.getMainPanel();
		thirdPartyRensumeCodeItem.table = codeItemTable;
		codeBbar = Ext.create("Ext.Toolbar", {
			border : false,
			items : [{
				text : "保存",
				iconCls : '',
				handler : function(){  
					var codeMap = new  HashMap();
					var celldata = codeItemtable.tablePanel.getStore();
					var resumelist = map.resumeList;
					var list="";
					for(var i=0;i<resumelist.length;i++){
						var   responseText=celldata.getAt(i).data.resumeInfo;
						var   userItemId=celldata.getAt(i).data.userItemId;
						var text=userItemId+"="+responseText+"|";
						list=list+text
					}
					
					codeMap.put("list",list);
					codeMap.put("resumeID",map.resumeID);
					codeMap.put("name",thirdPartyRensumeCodeItem.name);
					Rpc( {
						functionId : 'ZP0000002609',
						success : function (param){
							var value = param.responseText;
							var returnMap = Ext.decode(value);
							if(returnMap.succeed)
								Ext.showAlert("保存成功");
						}
					}, codeMap);
				}
			},
			'->',
			{
				text : "返回",
				iconCls : '',
				handler : thirdPartyRensumeCodeItem.backRensume
			}] 
		});
		
		var autoCodeButton = Ext.create("Ext.Toolbar", {
			border : false,
			items : [{
				text : "自动对应",
				iconCls : '',
				handler : function(){
					var codeMap = new  HashMap();
					var commonvalue = map.commonvalue; 
					thirdPartyRensumeCodeItem.resumeID = map.resumeID;
					codeMap.put("name",thirdPartyRensumeCodeItem.name);
					codeMap.put("commonvalue",commonvalue);
					codeMap.put("resumeID",map.resumeID);
					Rpc( {
						functionId : 'ZP0000002610',
						success : thirdPartyRensumeCodeItem.init
					}, codeMap);
				}
			}] 
		});
		
		var dataStore = Ext.create('Ext.data.Store', {
			fields:['name','id'],
			data: map.clist,
			autoLoad: true
		});
		
		if(!thirdPartyRensumeCodeItem.selectId)
			thirdPartyRensumeCodeItem.selectId = map.itemCom;
		var tbar = Ext.create("Ext.Toolbar", {
			border : false,
			items : [{
				xtype: 'combobox',
				fieldLabel: '代码对应',
				id : 'codeComId',
				store: dataStore,
				labelWidth:60,
				width : 300,
				queryMode: 'local',
				displayField: 'name',
				valueField: 'id',
				labelSeparator: '',
				triggerAction : 'all',
				listeners: {
					select: function(txtP,newValue,oldValue) {
						var resumeID = newValue.data.id;
						thirdPartyRensumeCodeItem.selectId = newValue.data.name;
						var map = new HashMap();
						map.put("resumeID",resumeID);
						map.put("from_flag",thirdPartyRensumeCodeItem.from_flag);
						map.put("name",thirdPartyRensumeCodeItem.name);
						Rpc( {
							functionId : 'ZP0000002606',
							success : thirdPartyRensumeCodeItem.initCombox
						}, map);
					},
				}
			}] 
		});
		
		Ext.getCmp("codeComId").setValue(thirdPartyRensumeCodeItem.selectId);
		
		var resumeInfo = new Ext.form.TextArea({
			width : 600,
			height : 100,
			margin: '5 0 0 0',
			name: 'resumeInfo',
			readOnly:true,
			fieldLabel: '简历信息参考',
			labelSeparator: ''
		});
		
		resumeInfo.setValue(map.commonvalue);
		
		var win = Ext.getCmp("codeItemId");
	    if(win)
	    	win.close();
	    
	    win = new Ext.window.Window({
	    	title : '<div style="height:37px;padding-left:10px;padding-top:8px;">代码对应</div>',
	    	renderTo : Ext.getBody(),
	    	maximized : true,
	    	border : false,
	    	id : 'codeItemId',
	    	closable : false,
	    	autoScroll : true,
	    	layout : 'fit',
	    	tbar: [tbar,autoCodeButton,codeBbar],
	    	bbar: [resumeInfo],
	    	items: [codeItemTable],
	    	loadMask : {
				msg : '数据加载中...'
			}
	    });
	    
		win.show();
	},
		
	backRensume:function(){
		var win = Ext.getCmp("codeItemId");
	    if(win)
	    	win.close();
	},
	
	changeCombo : function (value){
		return value.substring(value.indexOf('`')+1,value.length);
	}
		
});