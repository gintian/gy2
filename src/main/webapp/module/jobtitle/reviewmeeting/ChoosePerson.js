/**
 * liuy add 2015/12/09
 * 评审会议-选择参会人员
 */
Ext.define("ReviewMeetingURL.ChoosePerson",{
	tableObj:undefined,
	columns:'',
	store:'',
	idlist:'',
	orgid:'',
	typeCommittee:4,	//默认二级单位 =1 评委会   =4二级单位
	constructor:function(config) {
		choose_me = this;
		choose_me.title = config.title;
		choose_me.w0301 = config.w0301;
		choose_me.w0321 = config.w0321;
		choose_me.typeCommittee = config.typeCommittee;
		choose_me.committee_id = config.committee_id;
		this.init();
	},
	//初始加载页面
	init:function(url) {
		var map = new HashMap();
		map.put("w0301", choose_me.w0301);
		map.put("w0321", choose_me.w0321);
		map.put("typeCommittee",choose_me.typeCommittee);
		map.put("committee_id", choose_me.committee_id);
	    Rpc({functionId:'ZC00002306',async:false,success:this.getTableOK,scope:this},map);
	},
	//加载表单
	getTableOK:function(form,action){
		var result = Ext.decode(form.responseText);
		choose_me.idlist = result.idlist;
		var conditions=result.tableConfig;
		var obj = Ext.decode(conditions);
		var tableObj = new BuildTableObj(obj);
		choose_me.tableObj = tableObj;
		choose_me.columns=choose_me.tableObj.tablePanel.columns;
		var tableComp = choose_me.tableObj.getMainPanel();
		Ext.create('Ext.window.Window', {
			id : 'meeting_window_id',
			modal:true,
			html:'<div id="meeting_window_content"></div>',
		    title: choose_me.title,
			modal: true,
			layout:'fit',
			width:800,
			height:500,
			border:false,
			items:[tableComp],
			/*buttonAlign:'center',
			buttons : [{
				xtype : 'button',
				text : "关闭",
				margin:'20 0 0 0',
				handler : function() {choose_me.closePicker("1");}
			}],*/
			listeners: {
		        close: {
		            fn: function(a, o){
		            	choose_me.closePicker();
		            }
		        }
			}
		}).show();
	},
	//新增专家，打开选择专家页面
	openAddStaff:function(){
		var me = this;
		var map = new HashMap();
		map.put("w0301", choose_me.w0301);
		map.put("typeCommittee",choose_me.typeCommittee);
		map.put("committee_id", choose_me.committee_id);
		Rpc({functionId:'ZC00002307',async:false,success:function(form, action){
			Ext.require("ExpertPicker.ExpertPicker",function(){
				var re = Ext.create("ExpertPicker.ExpertPicker",{
					width:'800',
					height:'500',
					sql:Ext.decode(form.responseText).sql,//加载时sql
					orderBy:Ext.decode(form.responseText).orderBy,//排序
					searchText:'请输入单位名称、部门、姓名...',
					title:'请选择专家',
					callback:choose_me.addStaff
				});
			});
		},scope:this},map);
	},
	//新增专家
	addStaff:function(selected){
		var me = this;
		var map = new HashMap();
		map.put("type", "1");//新增
		map.put("typeCommittee",choose_me.typeCommittee);
		map.put("w0301", choose_me.w0301);
		map.put("personidList", selected);
		Rpc({functionId:'ZC00002309',async:false,success:function(form,action){
			var msg = Ext.decode(form.responseText).msg;
			if(msg != ""){
				Ext.showAlert(msg);
				return false;
			}else{
				var storeid="choose_dataStore";
	        	var store=Ext.data.StoreManager.lookup(storeid);
	        	store.reload();
			}
		},scope:this},map);
	},
	//撤销专家
	deleteStaff:function(){
		var tablePanel=choose_me.tableObj.tablePanel;
		var records=tablePanel.getSelectionModel().getSelection();
		if(records.length<1){
			Ext.showAlert('请选择要撤销的参会人员！');
			return;
		}
		var list = [];
		for(var i=0;i<records.length;i++){
			var w0101 =  records[i].data.w0101;
			list.push(w0101);
		}
		Ext.showConfirm("确定要撤销选中参会人员吗？",function(btn){
			if(btn == 'yes'){
				var me = this;
				var map = new HashMap();
				map.put("type", "2");//删除
				map.put("w0301", choose_me.w0301);
				map.put("typeCommittee", choose_me.typeCommittee);
				map.put("w0101List", Ext.encode(list));
				Rpc({functionId:'ZC00002309',async:false,success:function(form,action){
					var msg = Ext.decode(form.responseText).msg;
					if(msg != ""){
						Ext.showAlert(msg);
						return false;
					}else{
						var storeid="choose_dataStore";
			        	var store=Ext.data.StoreManager.lookup(storeid);
			        	store.reload();
					}
				},scope:this},map);
			}
		});
	},
	//随机生成账号密码
	randomCreate:function(){
		var tablePanel=choose_me.tableObj.tablePanel;
		var records=tablePanel.getSelectionModel().getSelection();
		if(records.length<1){
			Ext.showAlert('请选择要生成账号密码的参会人员！');
			return;
		}
		var list  =[];
		for(var i=0;i<records.length;i++){
			var w0101 =  records[i].data.w0101;
			list.push(w0101);
		}
		var me = this;
		var map = new HashMap();
		map.put("type", "3");//随机生成账号密码
		map.put("w0301", choose_me.w0301);
		map.put("typeCommittee", choose_me.typeCommittee);
		map.put("w0101List", Ext.encode(list));
		Rpc({functionId:'ZC00002309',async:false,success:function(form,action){
			var msg = Ext.decode(form.responseText).msg;
			if(msg != ""){
				Ext.showAlert(msg);
				return false;
			}else{
				var storeid="choose_dataStore";
	        	var store=Ext.data.StoreManager.lookup(storeid);
	        	store.reload();
			}
		},scope:this},map);
	}/*,
	//保存修改信息
	saveStaff:function(){
		var storeid="choose_dataStore";
	    var store=Ext.data.StoreManager.lookup(storeid);
		var updateList = store.getModifiedRecords();//修改过的数据
		if(updateList.length==0)
    	   return;
	    var updaterecord = [];
    	if(updateList.length>0){
    		for(var i=0;i<updateList.length;i++){
				var record = updateList[i].data;
				updaterecord.push(record);
			}
    	}
		var map = new HashMap();
    	map.put("type", "4");//修改
        map.put("updaterecord",updaterecord);
        Rpc({functionId:'ZC00002309',async:false,success:function(form,action){
			var msg = Ext.decode(form.responseText).msg;
			if(msg != ""){
				Ext.showAlert(msg);
				var store=Ext.data.StoreManager.lookup("choose_dataStore");//刷新store haosl 20161014
	        	store.reload();
				return false;
			}else{
				var storeid="choose_dataStore";
	        	var store=Ext.data.StoreManager.lookup(storeid);
	        	store.reload();
			}
		},scope:this},map);
	}*/,
	//确定
	enter:function(){
		this.saveStaff();
		this.closePicker("1");
	},
	//取消
	closePicker:function(isclose){
		if(isclose == "1"){
			Ext.getCmp('meeting_window_id').close();
		}
		var map = new HashMap();
		map.put("w0301", choose_me.w0301);
		map.put("typeCommittee",choose_me.typeCommittee)//=1评委会 =4二级单位
	    Rpc({functionId:'ZC00002308',async:false,success:function(form,action){},scope:this},map);
		var storeid="meeting_dataStore";
       	var store=Ext.data.StoreManager.lookup(storeid);
       	store.reload();
	}
});