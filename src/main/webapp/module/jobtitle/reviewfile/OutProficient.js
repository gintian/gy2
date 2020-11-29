/**
 * 鉴定专家
 * chent
 */
Ext.define('OutProficientURL.OutProficient',{
	me:'',
	w0301:'',
	w0501:'',
	constructor:function(config){
		outProficient_me = this;
		this.w0501 = config.w0501;//申请人编号
		this.w0301 = config.w0301;//会议编号
     	this.init();
	},
	init:function(){
		var me = this;
		
		var map = new HashMap();
		map.put("w0501", this.w0501);
		map.put("w0301", this.w0301);
	    Rpc({functionId:'ZC00003002',async:false,success:me.getTableOK,scope:this},map);
	},
	getTableOK:function(form){
		
		var result = Ext.decode(form.responseText);
		
		// 账号启用禁用
		outproficientstate = result.outproficientstate; 
		// 知否已评
		subflag = result.subflag; 
		var jsonData = result.tableConfig;
		var obj = Ext.decode(jsonData);
		var tableObj = new BuildTableObj(obj);
		var tableComp = tableObj.getMainPanel();
	    //创建弹窗
	    Ext.create('Ext.window.Window',{
		  	title:'同行专家',
		  	id:'outProficientId',
		  	layout:'fit',
		  	minWidth:780,
		  	width:Ext.getBody().getWidth()*0.7,
			height:(window.screen.availHeight-150)*0.7,//窗口的高(不含菜单)，150：菜单高度,
		  	modal: true,
		  	border:false,
		  	bodyStyle: 'background:#ffffff;',
	       	items:[tableComp],
	        listeners:{
		         beforeclose: function(){
		        	 reviewfile_me.loadTable();
		         }
		    }
		}).show();
	},
	//添加空白行
	addComputeCond:function(){
		var obj = new Object();
		obj.username='';
		obj.content='';
		obj.pasword='';
		obj.statment='0';
		obj.desc='';
		obj.w0301_e = outProficient_me.w0301;
		obj.w0501_e = outProficient_me.w0501;
		obj.isNew='1';
		var store = Ext.data.StoreManager.lookup('reviewfile_outproficient_dataStore');
		store.insert(store.getCount(),obj);
	},
	// 新增专家
	addPerson:function(selected, action){
		var me = this;
		
		var map = new HashMap();
		map.put("w0301", outProficient_me.w0301);
		map.put("w0501", outProficient_me.w0501);
		map.put("personidList", selected);
		Rpc({functionId:'ZC00003016',async:false,success:function(form,action){
			var msg = Ext.decode(form.responseText).msg;
			if(msg != ""){
				Ext.showAlert(msg);
				return ;
			}else{
				var store = Ext.data.StoreManager.lookup('reviewfile_outproficient_dataStore');
				store.load();
			}
		},scope:this},map);
	},
	//引入专家
	importExpert:function(){
		var map = new HashMap();
		Rpc({functionId:'ZC00002208',async:false,success:function(form, action){
			Ext.require("ExpertPicker.ExpertPicker",function(){
				var re = Ext.create("ExpertPicker.ExpertPicker",{
					width:Ext.getBody().getWidth()*0.9,
					height:(window.screen.availHeight-150)*0.9,//窗口的高(不含菜单)，150：菜单高度,
					sql:Ext.decode(form.responseText).sql,//加载时sql
					orderBy:Ext.decode(form.responseText).orderBy,//排序
					searchText:'请输入单位名称、部门、姓名...',
					title:'请选择专家',
					callback:outProficient_me.addPerson
				});
			});
		},scope:this},map);
	},
	/**
	 *  校验列 账号状态为启用的不允许编辑
	 */
	checkCell:function(record){
		var state = record.data.state;
		if(state=="1")
			return false
		else
			return true;
	},
	/**
	 * 校验账号和密码只能输入字母和数字
	 */
	validfunc:function(value){
		var reg = /^[0-9a-zA-Z]*$/g;
		if(!reg.test(value))
			return "只支持输入字母和数字";
		else 
			return true;
			
	}
});