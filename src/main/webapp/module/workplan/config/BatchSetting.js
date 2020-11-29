/**
 * 批量设置
 * 基于快速查询的人员批量设置人员方案
 */
Ext.define('WorkPlanConfigUL.BatchSetting',{
	title: '批量设置',
	extend:'Ext.window.Window',
	id:'batchSettingWin',
	layout: 'fit',
    height: 490,
	width: 400,
	shadow:true,
	modal:true,
	constructor:function(){
		wp_bantchSetting = this;
		wp_bantchSetting.callParent(arguments);
		wp_bantchSetting.init();
	},
	init:function(){
		var gridpanel = wp_bantchSetting.createPanel();
		wp_bantchSetting.add(gridpanel);
	},
	/** 获得方案数据*/
	getStore:function(){
		var map = new HashMap();
		map.put('opt','batchSetting');//获取页面显示需要的方案数据
		var dataArray=new Array();
		Rpc({functionId:'WP20000003',async:false,success:function(form,action){
			var data = Ext.decode(form.responseText).data;
			for(var i in data){
				var obj = data[i];
				dataArray.push({id:obj.id,planSummy:obj.planSummy,isOpen:''});
			}
		}},map);
		return Ext.create('Ext.data.Store', {
			    id: 'planSummyStore',
			    fields:['id','planSummy','isOpen'],
			    data: dataArray
			});
	},
	createPanel:function(){
		var store = wp_bantchSetting.getStore();
		return Ext.create('Ext.grid.Panel', {
		    store:store,
		    style:'margin:0 30 20 30',
		    columnLines:true,
		    columns: [
		        { text: '计划与总结类型', dataIndex: 'planSummy',flex:0.5},
		        { text: '启用', dataIndex: 'isopen',flex:0.5,renderer:workPlanConfig.batchsettingRenderColumn}
		    ],
		    buttonAlign:'center',
			fbar: [
			  	{ type: 'button', text: '确定', handler:function(){workPlanConfig.updataPersonFunc()}},
			  	{ type: 'button', text: '取消', handler:function(){wp_bantchSetting.close()}}
			],
		});
	}
})