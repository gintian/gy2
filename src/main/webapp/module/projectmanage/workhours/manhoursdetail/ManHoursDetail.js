/**
 * 员工项目工时汇总
 * ly 2015-12-03
 * 
 * */
Ext.define('ManHoursDetailUL.ManHoursDetail',{
	manhoursdetaillistObj:'',
	constructor:function(config) {
		manhoursdetail_me = this;
		//用作页面公式格式化
		DecimalWidth1319= new Object();
		DecimalWidth1321= new Object();
		
		manhoursdetail_me.P1101 = config.projectId;
		manhoursdetail_me.title = config.name; 
		manhoursdetail_me.init();
	},
	// 初始化函数
	init:function() {
		Ext.util.CSS.removeStyleSheet('treegridImg');
		Ext.util.CSS.removeStyleSheet('gridCell');
		var map = new HashMap();
		map.put("projectId",manhoursdetail_me.P1101);
		map.put("title",manhoursdetail_me.title+"");
	    Rpc({functionId:'PM00000102',success:manhoursdetail_me.getTableOK},map);
	},
	// 加载表单
	getTableOK:function(form,action){
		var result = Ext.decode(form.responseText);
		var conditions=result.tableConfig;
		var obj = Ext.decode(conditions);
		manhoursdetaillistObj = new BuildTableObj(obj);
		
		//获取员工项目工时详细信息
		var manhoursdetailItem = manhoursdetaillistObj.getMainPanel();
		Ext.getCmp("manhoursdetail_tablePanel").on("validateedit", manhoursdetail_me.validDateValue)
		var window = Ext.getCmp('manHoursid');
		if(window)
			window.close();
		//创建window对象
		window = new Ext.window.Window({
			maximized : true,
			header: false,
			padding:'0 1 1 0',
			border : false,
			id : 'manHoursid',
			closable : false,
			autoScroll : true,
			items:[manhoursdetailItem],
			layout :'fit',
			listeners: {
			}
		});
		window.show();
	},
	toMenDetailPage:function(value,c,record){

		var html = "<a href=javascript:manhoursdetail_me.openEditPage('"+record.data.p1301_e+"','"+manhoursdetail_me.title+"','"+manhoursdetail_me.P1101+"');>"+value+"</a>"; 
		return html;
	},
	
	isManager:function(record){
		if(record.data.p1311 =='01`项目经理'){
			return false;
		}
	},
	save:function(){
		var store = Ext.getCmp("manhoursdetail_tablePanel").getStore();
		var updateList = store.getModifiedRecords();
		if(updateList.length==0)
			return;
		var map = new HashMap();
		map.put("P1101", manhoursdetail_me.P1101);
		var dataList = new Array();
		for(var i = 0;i<updateList.length;i++){
			var record = updateList[i];
			var dateAddMap = new HashMap();
			dateAddMap.put("P1301",record.data.p1301_e);
			dateAddMap.put("P1307",record.data.p1307);
			dateAddMap.put("P1309",record.data.p1309);
			dateAddMap.put("P1311",record.data.p1311);
			dateAddMap.put("P1313",record.data.p1313);
			dateAddMap.put("P1315",record.data.p1315);
			dateAddMap.put("P1317",record.data.p1317);
			dataList.push(dateAddMap);
		}
		map.put("dateList", dataList);
		Rpc({functionId:'PM00000101',async:false,success:function(form,action){
			var result = Ext.decode(form.responseText);
			var base = result.base;
			var tip = result.tip;
			if("1"==tip){
				  store.load();
				 // reviewfile_me.refreshTable();
		  }else{
				  alert("修改失败");
		  }
		}},map);
	},
	openEditPage:function(manId,projectName,projectId){
		//manhoursdetaillistObj.getMainPanel().destroy();
		var map = new HashMap();
		map.put("type", 2);
		map.put("manId", manId);
		map.put("projectName", projectName);
		map.put("projectId", projectId);
		Ext.require('ManHoursSumUL.ManHoursSum', function(){
			Ext.create("ManHoursSumUL.ManHoursSum", map);
		});
	},
	returnToMainPage:function(){
		//manhoursdetaillistObj.getMainPanel().destroy();
		//projectManage.init();
		var window = Ext.getCmp('manHoursid');
		if(window)
			window.close();
	},
	schemeSaveCallback:function(){
		manhoursdetaillistObj.getMainPanel().destroy();
		manhoursdetail_me.init();
	},
	
	validDateValue: function (e, context){
		var field = context.field;
		if("p1315" == field || "p1317" == field){
			var oldValue = context.originalValue;
			var newValue = context.value;
			
			if(newValue == oldValue)
				return false;
			
			var columnText = "";
			var indexField = "p1315";
			if("p1315" == field) 
				indexField = "p1317";
				
			var columns = Ext.getCmp("manhoursdetail_tablePanel").columns;
			for (var i = 0; i < columns.length; i++){
				if(indexField == columns[i].dataIndex) {
					columnText = columns[i].text;
					break;
				}
					
			}
			
			if("p1315" == field) {
				var value = context.record.data.p1317;
				if(newValue > value) {
					var text = context.column.text;
					Ext.showAlert(text + '不能大于' + columnText + '！');
					return false;
				}
			} else {
				var value = context.record.data.p1315;
				if(newValue < value) {
					var text = context.column.text;
					Ext.showAlert(text + '不能小于' + columnText + '！');
					return false;
				}
			}
		}
	}
	
});
