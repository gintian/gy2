/**
 * 出勤异常显示
 */
Ext.define('KqDataURL.KqExceptInfo',{
	tableObj:'',
	constructor: function(config){
		KqExceptInfo=this;
		KqExceptInfo.prefix="KqExcept_001";
		KqExceptInfo.name=config.name;
		KqExceptInfo.guidkey=config.guidkey;
		KqExceptInfo.startDate=config.startDate;
		KqExceptInfo.endDate=config.endDate;
		this.selecedOrderdItem = [];
		this.init();
	},
	//初始化
	init: function(){
		var map = new HashMap();
		map.put('guidkey', KqExceptInfo.guidkey);
		map.put('startDate', KqExceptInfo.startDate);
		map.put('endDate', KqExceptInfo.endDate);
	    Rpc({functionId:'KQ00021206',success:KqExceptInfo.showPanel},map);
	}, 
	showPanel: function(response){
		var result	 = Ext.decode(response.responseText);
		if(result.succeed == true) {
			tableConfigStr = result.tableConfig;
			tableObj = new BuildTableObj(tableConfigStr);
			var tablePanel = tableObj.tablePanel;
			tablePanel.initialConfig.columns.unshift({xtype:'rownumberer',text:kq.label.rowNumberer,width:40});
			var tbar2 = new Ext.Toolbar({
				id: 'toolbar2',
				height: 25,
				padding: '0 5 0 0',
				border: false,
				hidden: false,
				items: [{
					xtype:"button",
					text: kq.label.exportDesc,
					arrowAlign: 'left',
					handler: function() {
						KqExceptInfo.exportTemplate()
					}
				}]
			}); 
			var north = new Ext.Toolbar({
				title: "出勤异常情况"+KqExceptInfo.name,
				id : 'north',
				items:[{
						xtype : 'panel',
						layout: 'vbox',
						border : false,
						items:[ tbar2]
					},
					],
				region : 'north',
				border : false			
			});
			var center = new Ext.Panel({
				xtype : 'panel',
				id : 'center',
				region : 'center',
				layout : 'fit',
				width: '100%',
				height: '100%',
				style:'background-color: #FFFFFF;',
				border : false,
				items:[
					tablePanel
					]
			});
//			new Ext.Viewport({
//				layout : "fit",
//				items:[north,center]
//			});
			var kqExcepWin = Ext.getCmp("KqExceptid");
			if(kqExcepWin)
				kqExcepWin.close();
			kqExcepWin = Ext.create('Ext.window.Window', {
//				id: 'KqExceptid',
			    title: "出勤异常情况-"+KqExceptInfo.name,
			    maximized:true,
//			    modal: true,
//			    resizable: false,
	            border : false,
				width: '100%',
				height: '100%',
				layout : "border",
	            items:[north,center]
//				listeners: {
//					
//				},
				
			});
			kqExcepWin.show();
		}
	},
	/**
	 * 导出表
	 */
	exportTemplate : function(){
		var json = {};
		json.name=KqExceptInfo.name;
		json.guidkey=KqExceptInfo.guidkey;
		json.startDate=KqExceptInfo.startDate;
		json.endDate=KqExceptInfo.endDate;
		var map = new HashMap();
		map.put("jsonStr", JSON.stringify(json));
	    Rpc({functionId:'KQ00021207',success:KqExceptInfo.exportSucc},map);
	},
	exportSucc: function (response){
		var value = response.responseText;
		var map	 = Ext.decode(value);
		if(map.succeed){
			var fieldName = getDecodeStr(map.fileName);
			window.location.target="_blank";
			window.location.href = "/servlet/vfsservlet?fileid="+fieldName+"&fromjavafolder=true";
		}
	},
	
});