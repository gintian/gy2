
Ext.define('ExamHallUL.ExamineeNameList',{
	examineenamelist:'',
	examineeNameObj:'',
	constructor:function(config) {
		examineenamelist=this;
		examineeNameObj="";
		hallId = config.hall_id;
		hallName = config.hall_name;
		batchName = config.batch_name;
        examineenamelist.init();
	},
	init:function(){
		 var map = new HashMap();
		 map.put('hall_id',hallId+'');
		 map.put('hallName',hallName);
		 map.put('batchName',batchName);
		 Rpc({functionId:'ZP0000002541',success: function(form,action){
				var result = Ext.decode(form.responseText);
				if(result.succeed){
					examineenamelist.createTableOK(result,form,action);
				}else{
					Ext.MessageBox.alert('提示信息',result.message);
				}
			}},map);
	},
	createTableOK:function(result,form,action){
		Ext.util.CSS.createStyleSheet(".scheme-selected-cls{text-decoration:underline;margin-left:10px;margin-right:10px}","underline");
		var conditions=result.tableConfig;
		var obj = Ext.decode(conditions);
		examineeNameObj = new BuildTableObj(obj);
	},
	//移除考场
	removeHall:function(){
		var ids = examineenamelist.getSelect1(Ext.getCmp('examineeNameList001_tablePanel'),"a0100");
		if(Ext.isEmpty(ids)){
			Ext.Msg.alert('提示信息','请选择考生!');
			return;
		}
		
		Ext.Msg.confirm("提示信息","确定要将所选考生移出考场吗？",function(btn){ 
			if(btn=="yes"){ 
				// 确认触发，继续执行后续逻辑。 
				//selectid选中的记录
				 var map = new HashMap();
				 map.put('ids',ids);
				 map.put('hall_id',hallId+'');
				 Rpc({functionId:'ZP0000002542',success:function(form,action){Ext.getCmp('examineeNameList001_tablePanel').getStore().reload();}},map);
		 	} 
		});
	},
	//获取选中grid的列
	 getSelect1:function (grid, col) { 
		var arr=new Array();
	    for (var i = 0; i < grid.getSelectionModel().getSelection().length; i++) {
			 if(grid.getSelectionModel().getSelection()[i].get(col)!=null && grid.getSelectionModel().getSelection()[i].get(col).length>0){
			      arr[i]=grid.getSelectionModel().getSelection()[i].get(col);
			 }
		}
		return arr;
	},
	//返回
	returnBack:function(){
		examineeNameObj.getMainPanel().destroy();
		examhall_me.tableObj.getMainPanel().add(examhall_me.tableObj.bodyPanel);
		examhall_me.loadStore();
	},
	schemeSaveCallback:function(){
		examineeNameObj.getMainPanel().destroy();
		examineenamelist.init();
	}
	
	
});
