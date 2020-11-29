Ext.define('sdparameter.SDParameter',{
	extend:'Ext.panel.Panel',
	id:"sdparameter",
	xtyle:'sdparameter',
	layout:'fit',
	initComponent:function(){
		SDParameter = this;
		this.callParent();
		this.loadData();
	},
	loadData:function(){
		var vo = new HashMap();
		vo.put('type','main');
		Rpc({functionId:'SYS00004001',success:this.getTableOK,scope:this},vo);
	},
	getTableOK:function(form,action){
		var result = Ext.decode(form.responseText);
		var return_code = result.returnStr.return_code;
		if(return_code == 'fail'){
			Ext.Msg.alert(sd_parameter.remind,result.returnStr.return_msg);
			return;
		}
		var return_data = result.returnStr.return_data;
		var obj = Ext.decode(return_data.gridconfig);
		obj.beforeBuildComp = function (grid) {
			grid.tableConfig.selModel={selType:'checkboxmodel',checkOnly:true};
		};
		SDParameter.tableObj = new BuildTableObj(obj);
		SDParameter.add(this.tableObj.getMainPanel());
		SDParameter.gridPanel = this.tableObj.tablePanel;
		SDParameter.gridStore = this.gridPanel.getStore();
	},
	validNameFunc:function(value){
		if(!value || value.length == 0)
			return sd_parameter.checkNotName;
		if(value.replace(/^\s\s*/, '' ).replace(/\s\s*$/, '' ).length ==0)
			return sd_parameter.checkNotName;
		if(Ext.getStringByteLength(value)>this.maxSize)
			return sd_parameter.checkNameLength.replace('{length}',this.maxSize);
		return true;
	},
	//新增
	addFunc:function(){
		var gridStore = SDParameter.gridStore;
		var map = new HashMap();
		var pageSize = gridStore.pageSize;
		map.put('type','add');
		map.put('pageSize',pageSize);
		Rpc({
			functionId:'SYS00004001',
			success:SDParameter.addOk,scope:this
		},map)
	},
	addOk:function(result,action){
		var result = Ext.decode(result.responseText);
		var showPage = result.returnStr.showPage;
		SDParameter.gridStore.currentPage = showPage;
		SDParameter.gridStore.load();
	},
	//删除
	deletFunc:function(){
		var selectedItems = SDParameter.gridPanel.getSelectionModel().selected.items;
		if(selectedItems.length == 0){
			Ext.Msg.alert(sd_parameter.remind,sd_parameter.noneSelected);
			return;
		}
		Ext.Msg.confirm(sd_parameter.remind,sd_parameter.makeSureDelPre + selectedItems.length + sd_parameter.makeSureDelSuf,function(btn){
			if(btn == 'yes'){
				var constants = '';
				for(var i = 0; i < selectedItems.length;i++){
					constants += selectedItems[i].data.constant +",";
				}
				if(constants == ''){
					SDParameter.gridStore.remove(selectedItems);
					Ext.Msg.alert(sd_parameter.remind,sd_parameter.deleteOK);
					return;
				}
				constants = constants.substring(0,constants.length-1);
				var vo = new HashMap();
				vo.put('type','delete');
				vo.put('constants',constants);
				Rpc({functionId:'SYS00004001',success:SDParameter.deleteOK,scope:this},vo);
			}
		});
	},
	deleteOK:function(form,action){
		var result = Ext.decode(form.responseText);
		var return_code = result.returnStr.return_code;
		var selectedItems = SDParameter.gridPanel.getSelectionModel().selected.items;
		if(return_code == 'success'){
			SDParameter.gridStore.remove(selectedItems);
			Ext.Msg.alert(sd_parameter.remind,sd_parameter.deleteOK);
		}else{
			var return_msg = result.returnStr.return_msg;
			Ext.Msg.alert(sd_parameter.remind,return_msg);
		}
	},
	//保存
	saveFunc:function(store){
		var vo = new HashMap();
		var parameterList = new Array();
		var parameterMap;
		var gridStore = SDParameter.gridStore;
		var count = SDParameter.gridStore.count();
		var checkData = {};
		for(var i = 0; i < count ; i++){
			var record = SDParameter.gridStore.getAt(i);
			if(checkData[record.get('constant').replace(/^\s\s*/, '' ).replace(/\s\s*$/, '' )]){
				Ext.Msg.alert(sd_parameter.remind,sd_parameter.saveRepeatTip.replace('{name}',record.get('constant')));
				return;
			}
			checkData[record.get('constant').replace(/^\s\s*/, '' ).replace(/\s\s*$/, '' )]=1;
		}
		
		//获取修改后的记录
		var modifiedRecords = gridStore.getModifiedRecords();
		/*
		if(modifiedRecords.length == 0){
			Ext.Msg.alert(sd_parameter.remind,sd_parameter.saveNone);
			return;
		}
		*/
		for(var i = 0; i < modifiedRecords.length;i++){
			parameterMap = {};
			parameterMap['id'] = parseInt(modifiedRecords[i].data.id);
			parameterMap['constant'] = modifiedRecords[i].data.constant;
			parameterMap['str_value'] = modifiedRecords[i].data.str_value;
			parameterMap['describe'] = modifiedRecords[i].data.describe;
			parameterList.push(parameterMap);
		}
		vo.put('type','save');
		vo.put('paramter',parameterList);
		Rpc({functionId:'SYS00004001',success:SDParameter.saveOK,scope:this},vo);
	},
	saveOK:function(form,action){
		var result = Ext.decode(form.responseText);
		var return_code = result.returnStr.return_code;
		if(return_code == 'success'){
			Ext.Msg.alert(sd_parameter.remind,sd_parameter.saveOK);
			SDParameter.gridStore.load();
		}else{
			var return_msg = result.returnStr.return_msg;
			Ext.Msg.alert(sd_parameter.remind,return_msg);
		}
	}

});