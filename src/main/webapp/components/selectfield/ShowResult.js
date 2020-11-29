/**
 * 结果显示
 * @createtime August 24, 2017 9:07:55 PM
 * @author chent
 * 
 * */
Ext.define('EHR.selectfield.ShowResult',{
	width:600,
	height:420,
	expr:"",
	checkValues:"",
	dataList:"",
	query_type:"",
	callback:Ext.emptyFn,
	title:'请选择',
	imodule:"",
	constructor:function(config) {
		this.width = config.width;
		this.height = config.height;
		this.expr = config.expr;
		this.checkValues = config.checkValues;
		this.dataList = config.dataList;
		this.query_type = config.query_type;
		this.title = config.title;
		this.callback = config.callback;
		this.imodule = config.imodule;
		this.isFilterSelectedExpert=config.isFilterSelectedExpert;//职称评审 是否过滤掉已经选择的专家 0 不过滤 1过滤
		this.init();
	},
	// 初始化函数
	init:function(url) {
		var me = this;

		//加载自定义类
		Ext.Loader.loadScript({url:'/components/tableFactory/tableFactory.js'});
		var map = new HashMap();
		map.put("expr", me.expr);
		map.put("checkValues", me.checkValues);
		map.put("dataList", me.dataList);
		map.put("query_type", me.query_type);
		map.put("imodule", me.imodule);
		map.put("isFilterSelectedExpert",me.isFilterSelectedExpert);
	    Rpc({functionId:'ZJ100000126',async:false,success:me.getTableOK,scope:this},map);
	},
	// 加载表单
	getTableOK:function(form, action){
		var me = this;
		
		var result = Ext.decode(form.responseText);
		var jsonData = result.tableConfig;
		var obj = Ext.decode(jsonData);
		var tableObj = new BuildTableObj(obj);
		me.extpert_picker_tableObj = tableObj;
		var tableComp = tableObj.getMainPanel();
		
		var pageheight = window.parent.window.document.getElementById('center_iframe').offsetHeight;
		var pagewidth = window.parent.window.document.getElementById('center_iframe').offsetWidth;
		Ext.create('Ext.window.Window', {
			id : 'picker_expert_window_id',
			modal:true,
		    title: me.title,
			modal: true,
			layout:'fit',
			width:pagewidth*0.9,
			height:pageheight*0.9,//窗口的高(不含菜单)，150：菜单高度,
			border:false,
			items:[tableComp],
			buttonAlign:'center',
			//alwaysOnTop:true,
			buttons : [{
				xtype : 'button',
				text : "上一步",
				margin:'20 10 0 0',
				handler : function() {
					this.closePicker();
					queryField_me.win.show();
				},
				scope:this
			},{
				xtype : 'button',
				text : common.button.ok,
				margin:'20 0 0 0',
				handler : function() {me.enter();}
			}]
		}).show();
	},
	//确定
	enter:function(){
		var selectedList = new Array();//人员信息集

		var selectData = this.extpert_picker_tableObj.tablePanel.getSelectionModel().getSelection();//获取数据
		for(var p in selectData){
			if(selectData.hasOwnProperty(p)){
				var nbasea0100_e = selectData[p].data.nbasea0100_e;
				selectedList.push(nbasea0100_e);
			}
		}
		/** 获取的是选择的数据 */
		if(selectedList.length == 0){//如果没选，不允许【确定】
			Ext.showAlert(common.label.selectRecord);//请勾选数据后操作
			return ;
		}
		this.callback(selectedList);
		this.closePicker();
		queryField_me.win.close();
	},
	// 关闭
	closePicker:function(){
		Ext.getCmp('picker_expert_window_id').close();
	}
});