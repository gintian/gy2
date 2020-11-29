/**
 * 指标对应调用js
 * 
 * @author zhancq
 */
Ext.define('ShowThirdPartyRensumeParmURL.ItemCorrespond', {
		requires:[
	    'EHR.extWidget.field.CodeSelectField',
	    'EHR.extWidget.proxy.TransactionProxy',
		'EHR.tableFactory.TableController'
		],
		
	constructor : function(config) {
		Ext.util.CSS.createStyleSheet(".x-nbr .x-window-default{background-color : white!important;border-width:1px!important}");
		thirdPartyRensumeItem = this;
		thirdPartyRensumeItem.resumeset = config.resumeset;
		thirdPartyRensumeItem.fieldset = config.fieldset;
		thirdPartyRensumeItem.name = config.name;
		thirdPartyRensumeItem.table = "";
		this.init();
	 },
	// 初始化函数
	init : function() {
		var map = new HashMap();
		map.put("resumeset",thirdPartyRensumeItem.resumeset);
		map.put("fieldset",thirdPartyRensumeItem.fieldset);
		map.put("name",thirdPartyRensumeItem.name);
		Rpc( {
			functionId : 'ZP0000002605',
			async:false,
			success : thirdPartyRensumeItem.initItem
		}, map);
	},
	
	codeThirdPartyRensume:function(view, rowIndex, colIndex, item, e, record, row){
		 var map = new HashMap();
         map.put("resumeset",thirdPartyRensumeItem.resumeset);
		 map.put("fieldset",thirdPartyRensumeItem.fieldset);
		 map.put("from_flag","2"); 
		 map.put("resumeID","");
		 map.put("name",thirdPartyRensumeItem.name);
		 Ext.require('ShowThirdPartyRensumeParmURL.codeCorrespond', function(){
			Ext.create("ShowThirdPartyRensumeParmURL.codeCorrespond", map);
		});
	},
		 
	backDefineRensume:function(){
		 var win = Ext.getCmp("itemId");
		 if(win)
			 win.close();
	},
	
	initItem : function(param){
		var value = param.responseText;
		var map = Ext.decode(value);
		var itemColumn =Ext.decode(map.rzColumn);
	    var itemValue = Ext.decode(map.rzValue);
	    
	    var configPanel = {
	   			prefix : "thirdPartyRensumeItem",
	   			editable : true,
	   			selectable : false,
	   			storedata : itemValue,
	   			clickToEdit:2,
	   			tablecolumns : itemColumn,
	   			datafields : [ 'resumeItems', 'userItems' ]
	   			               
	   	};
	                 	  
		var RensumeItem = new BuildTableObj(configPanel);
	    var itemTable = RensumeItem.getMainPanel();
	    thirdPartyRensumeItem.table = itemTable;
	    
	   	var ItemBbar = Ext.create("Ext.Toolbar", {
	   		buttonAlign : "center",
	   		border : false,
	   		items : [{
	   			text : "保存",
	   			iconCls : '',
	   			handler : function(){
		   			var itemMap = new  HashMap();
		   			var itemlist = map.itemlist;
		   			var celldata = RensumeItem.tablePanel.getStore();
		   			var baseitems = new Array();
		   			var itemvalues = new Array();
		   			var resumefldids = new Array();
		   			
		   			for(var i = 0;i < itemlist.length;i++){
		   				var  comValue=celldata.getAt(i).data.userItems;
		   				var  ehrfldselect = "";
		   				if(comValue!=null && comValue!="" && comValue.indexOf("`")>-1) {
		   					ehrfldselect = comValue.split("`")[0];
		   				}
		   				
		   				var  resumeset = itemlist[i].resumeset;
		   				var  resumefld = itemlist[i].resumefld;		
		   				var  resumefldid = itemlist[i].resumefldid;
		   				baseitems[i]=resumefld; 
		   				itemvalues[i]=ehrfldselect;
		   				resumefldids[i]=resumefldid;     
		   			}	
		   			
		   			itemMap.put("itemvalues",itemvalues);
		   			itemMap.put("resumeset",resumeset);
		   			itemMap.put("baseitems",baseitems);
		   			itemMap.put("resumefldids",resumefldids);
		   			itemMap.put("name",thirdPartyRensumeItem.name);
		   			Rpc( {
		   				functionId : 'ZP0000002607',
		   				async:false,
		   				success : function (param){
		   					RensumeItem.tablePanel.getStore().reload();
		   					var value = param.responseText;
		   					var returnMap = Ext.decode(value);
		   					if(returnMap.succeed)
		   						Ext.showAlert("保存成功！");
		   				}
		   			}, itemMap);
		   		}
	   				
	   		},
	   		'->',
	   		{
	   			text : "代码对应",
	   			iconCls : '',
	   			handler : thirdPartyRensumeItem.codeThirdPartyRensume	   			
	   		},
	   		'->',
	   		{
	   			text : "返回",
	   			iconCls : '',
	   			handler : thirdPartyRensumeItem.backDefineRensume
	   		}] 
	   	});
	   		
	    var win = Ext.getCmp("itemId");
	    if(win)
	    	win.close();
	    
	    win = new Ext.window.Window({
	    	title : '<div style="height:37px;padding-left:10px;padding-top:8px;">指标对应</div>',
	    	renderTo : Ext.getBody(),
	    	maximized : true,
	    	border : false,
	    	id : 'itemId',
	    	closable : false,
	    	autoScroll : true,
	    	layout : 'fit',
	    	tbar:   [ItemBbar],
	    	items:[itemTable]
	    });
	    
		win.show();
	},
	
	changeItemCombo : function (value){
		return value.substring(value.lastIndexOf('`')+1,value.length);
	}

});