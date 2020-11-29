function GUID() {
	this.date = new Date(); /* 判断是否初始化过，如果初始化过以下代码，则以下代码将不再执行，实际中只执行一次 */
	if (typeof this.newGUID != 'function') { /* 生成GUID码 */
		GUID.prototype.newGUID = function() {
			this.date = new Date();
			var guidStr = '';
			sexadecimalDate = this.hexadecimal(this.getGUIDDate(), 16);
			sexadecimalTime = this.hexadecimal(this.getGUIDTime(), 16);
			for (var i = 0; i < 9; i++) {
				guidStr += Math.floor(Math.random() * 16).toString(16);
			}
			guidStr += sexadecimalDate;
			guidStr += sexadecimalTime;
			while (guidStr.length < 32) {
				guidStr += Math.floor(Math.random() * 16).toString(16);
			}
			return guidStr.toUpperCase();
		}
		/* * 功能：获取当前日期的GUID格式，即8位数的日期：19700101 * 返回值：返回GUID日期格式的字条串 */
		GUID.prototype.getGUIDDate = function() {
			return this.date.getFullYear()
					+ this.addZero(this.date.getMonth() + 1)
					+ this.addZero(this.date.getDay());
		}
		/* * 功能：获取当前时间的GUID格式，即8位数的时间，包括毫秒，毫秒为2位数：12300933 * 返回值：返回GUID日期格式的字条串 */
		GUID.prototype.getGUIDTime = function() {
			return this.addZero(this.date.getHours())
					+ this.addZero(this.date.getMinutes())
					+ this.addZero(this.date.getSeconds())
					+ this.addZero(parseInt(this.date.getMilliseconds() / 10));
		}
		/*
		 * * 功能: 为一位数的正整数前面添加0，如果是可以转成非NaN数字的字符串也可以实现 * 参数:
		 * 参数表示准备再前面添加0的数字或可以转换成数字的字符串 * 返回值: 如果符合条件，返回添加0后的字条串类型，否则返回自身的字符串
		 */
		GUID.prototype.addZero = function(num) {
			if (Number(num).toString() != 'NaN' && num >= 0 && num < 10) {
				return '0' + Math.floor(num);
			} else {
				return num.toString();
			}
		}
		/*
		 * * 功能：将y进制的数值，转换为x进制的数值 *
		 * 参数：第1个参数表示欲转换的数值；第2个参数表示欲转换的进制；第3个参数可选，表示当前的进制数，如不写则为10 *
		 * 返回值：返回转换后的字符串
		 */
		GUID.prototype.hexadecimal = function(num, x, y) {
			if (y != undefined) {
				return parseInt(num.toString(), y).toString(x);
			} else {
				return parseInt(num.toString()).toString(x);
			}
		}
		/* * 功能：格式化32位的字符串为GUID模式的字符串 * 参数：第1个参数表示32位的字符串 * 返回值：标准GUID格式的字符串 */
		GUID.prototype.formatGUID = function(guidStr) {
			var str1 = guidStr.slice(0, 8) + '-', str2 = guidStr.slice(8, 12)
					+ '-', str3 = guidStr.slice(12, 16) + '-', str4 = guidStr
					.slice(16, 20)
					+ '-', str5 = guidStr.slice(20);
			return str1 + str2 + str3 + str4 + str5;
		}
	}
}
Ext.define('SysRegListUL.SysRegList', {
	sysRegList_me:'',
	tableObj:'',
	constructor : function(config) {
		sysRegList_me = this;
		sysRegList_me.serviceData = {};
		sysRegList_me.editServiceData = {};
		sysRegList_me.init();// 初始化界面
	},
	init : function() {
		var map = new HashMap()
		map.put('method','init');
		Rpc({functionId:'SYS00000002',async:false,success:sysRegList_me.getTableOK},map);
	},
	getTableOK:function(form,action){
		var result = Ext.decode(form.responseText);
		var conditions=result.sysRegGridTable;
		var obj = Ext.decode(conditions);
		tableObj = new BuildTableObj(obj);
	},
	insertSysReg:function(){
		var guid = new GUID();
		var uuid = guid.newGUID();
		var serviceStore = Ext.create('Ext.data.Store',{
			fields:['serviceName','dataScope'],
			proxy:{
				type: 'transaction',
				functionId:'SYS00000002',
				extraParams:{
					method:"serviceList"
				},
				reader: {
					type: 'json',
					root: 'data'         	
				}
			},
			autoLoad: true
		});
		var serviceGrid = new Ext.grid.GridPanel({
			autoScroll : true,
			store:serviceStore,
			width : 500,
	        stateful:true,
	        border:1,
	        stripeRows:true,//隔行换色
	        forceFit:true,//让每列自动填满表格，可以根据columns中设置的width按比例分配
    		enableHdMenu:false,//是否显示表头的上下文菜单，默认为true
    		enableColumnHide:false,//是否允许通过标题中的上下文菜单隐藏列，默认为true
    		enableColumnMove:false,//是否允许拖放列，默认为true
    		enableColumnResize:false,//是否允许改变列宽，默认为true
    		columnLines: true,//是否显示列分割线，默认为false
    		loadMask:true,//在store.load()完成之前是否显示遮罩效果，true会一直显示"Loading...",
    		multiSelect:false,//支持多选
    		selModel: {
    			selType: 'checkboxmodel',
            	columnSelect: true,
            	checkboxSelect: true,
            	pruneRemoved: false,
    	        mode: "SIMPLE",     //"SINGLE"/"SIMPLE"/"MULTI"
    	        checkOnly: true     //只能通过checkbox选择
    	    },
    		columns:[
    		         //自动显示行号，也可以用new Ext.grid.RowNumberer()
    		         {	
    		        	header:sysreg.column.serviceName,
         		        dataIndex:'serviceName',
    		        	sortable:false
    		         },
    	             {
    		        	header:sysreg.column.dataScope,
    		        	dataIndex:'dataScope',
    		        	sortable:false,
    		        	align : 'center',
    		        	renderer: function (value, meta, record) {
    		        			var html = '';
    		        			if(value==1){
    		        				var method = record.data.serviceMethod;
    		        				var opt = '';
    		        				if(method=='getOrg'){
    		        					opt = 'org';
    		        				}else if(method=='getPost'){
    		        					opt = 'post';
    		        				}else if(method=='getEmp'){
    		        					opt = 'emp';
    		        				}
    		        				html = "<a href=javascript:sysRegList_me.showDefine('"+opt+"','add')><img src='/images/new_module/changecompare.gif' border=0></a>";
    		        			}
	    		        		return html;
    		        		} 
    		         }]
    			});
		var bodyPanel = Ext.create('Ext.form.Panel', {
		    bodyPadding: '20',
		    margin:'auto',
			border:false,
		    layout: 'anchor',
		    defaults: {
		        anchor: '90%'
		    },
		    items:[{
			   	xtype : 'textfield',
				validator : function(value){
			   		if(value.length<1)
			   			return sysreg.msg.sysCodeMinMsg;
			   		var startWithNum = /^(?![a-zA-Z])[a-zA-Z0-9]{0,10}$/;
			   		if(startWithNum.test(value))
			   			return sysreg.msg.sysCodeStartWithNumErrorMsg;
			   		var re = /^[a-zA-Z0-9]+$/;
			   		if(!re.test(value))
			   			return sysreg.msg.sysCodeErrorMsg;
			   		return true;
			   	},
		    	id:'sysCode',
		        fieldLabel: sysreg.label.sysCode,
		        labelSeparator:'',
		        beforeLabelTextTpl:"<font color='red'> * </font>",
		        labelAlign:'right',
		        maxLength:10,//允许最大长度
		        margin:'0 0 10 0'
		    },
		    {
		    	xtype : 'textfield',
		    	validator : function(value){
			   		if(value.length<1)
			   			return sysreg.msg.sysNameMinMsg;
			   		return true;
			   	},
		    	id:'sysName',
		        fieldLabel: sysreg.label.sysName,
		        labelSeparator:'',
		        beforeLabelTextTpl:"<font color='red'> * </font>",
		        labelAlign:'right',
		        maxLength:50,
		        margin:'0 0 10 0'
		    },
		    {
		    	xtype : 'textfield',
		    	id:'sysEtoken',
		        fieldLabel: sysreg.label.sysEtoken,
		        labelSeparator:'',
		        labelAlign:'right',
		        maxLength:50,
		        readOnly:true,//只读
		        fieldStyle:'background-color:#EEEEEE;',
		        allowBlank: false,
		        margin:'0 0 10 0',
		        value:uuid
		    },
		    {
		        xtype : 'textareafield',
		        id  : 'description',
		        fieldLabel: sysreg.label.description,
		        labelSeparator:'',
		        labelAlign:'right',
		        grow : false,
		        height:80,
		        margin:'0 0 10 0'
		    },{
		    	xtype:'checkbox',
		    	boxLabel:sysreg.label.valid,
		    	id:'valid',
		    	name:'valid',
		    	margin : '0 0 0 57',
		    	checked:true,//默认选中
		    	labelAlign:"left",
		    	handler : function() {
		    		if(this.checked==true){
		    			serviceGrid.getSelectionModel().setLocked(false);//将服务选项设置为不能勾选
		    			Ext.getCmp('dynaCode').readOnly=false;//将动态认证选项设置为不能勾选
		    		}else{
		    			serviceGrid.getSelectionModel().setLocked(true);
		    			Ext.getCmp('dynaCode').readOnly=true;
		    		}
		    	}
		    },{
				xtype : 'fieldset',
				id : 'serviceSet',
				margin : '0 0 10 57',
				height : 220,
				title : sysreg.label.serviceSet,
				layout : 'fit',
				items : [ serviceGrid ]
		    },{
		    	xtype:'checkbox',
		    	boxLabel:sysreg.label.dynaCode,
		    	id:'dynaCode',
		    	name:'dynaCode',
		    	margin : '0 0 10 57',
		    	checked:false,//默认选中
		    	labelAlign:"left",
		    }
		   ],
		    buttonAlign:'center',
		    buttons: [{
		        text: sysreg.button.ok,
		        formBind:true,
		        handler: function() {
		        	var serviceList = [];
		            var sysCode = Ext.util.Format.trim(Ext.getCmp('sysCode').getValue());
		            var sysName = Ext.util.Format.trim(Ext.getCmp('sysName').getValue());
		            if(sysCode.length==0){
		            	Ext.showAlert(sysreg.msg.sysCodeMsg);
		            	return;
		            }else if(sysName.length==0){
		            	Ext.showAlert(sysreg.msg.sysNameMsg);
		            	return;
		            }
		            if(sysCode.length>10){
		            	Ext.showAlert(sysreg.msg.sysCodeMaxMsg);
		            	return;
		            }else if(sysName.length>50){
		            	Ext.showAlert(sysreg.msg.sysNameMaxMsg);
		            	return;
		            }
		            var checkMap = new HashMap();
		            checkMap.put('method','check');
		            checkMap.put('addOrEdit','add');
		            checkMap.put('checkCode',sysCode);
		            checkMap.put('checkName',sysName);
		            Rpc({
		    	    	functionId:'SYS00000002',
		    	    	async:true,
		    	    	success: function(form){
			    	    		var returnStr = Ext.decode(form.responseText);
			    	    		var reMsg = returnStr.result;
		    		    		if(reMsg != 'true'){
		    		    			if(reMsg == 'code'){
		    		    				Ext.showAlert(sysreg.msg.checkCodeMsg);
		    		    			}else if(reMsg == 'name'){
		    		    				Ext.showAlert(sysreg.msg.checkNameMsg);
		    		    			}else if(reMsg == 'false'){
		    		    				Ext.showAlert(sysreg.msg.checkErrorMsg);
		    		    			}
		    		    			return;
		    		    		}
		    		    		var etoken = Ext.getCmp('sysEtoken').getValue();
		    		    		var description = Ext.getCmp('description').getValue();
		    		    		var valid = Ext.getCmp('valid').getValue();
		    		    		var dynaCode = Ext.getCmp('dynaCode').getValue();
		    		    		var selected = serviceGrid.getSelectionModel().getSelected();
		    		    		if(selected.length>0){
		    		    			var items = selected.items;
		    		    			for(var i=0; i<selected.length ; i++){
		    		    				serviceList[i] = items[i].data.serviceMethod;
		    		    			}
		    		    		}
		    		    		var map = new HashMap();
		    		    		map.put('method','add');
		    		    		map.put("sysCode",sysCode);
		    		    		map.put("sysName",sysName);
		    		    		map.put("etoken",etoken);
		    		    		map.put("description",description);
		    		    		map.put("valid",valid);
		    		    		map.put("dynaCode",dynaCode);
		    		    		map.put("serviceList",serviceList);
		    		    		map.put("orgExper",sysRegList_me.serviceData.strOrgExpression);
		    		    		map.put("postExper",sysRegList_me.serviceData.strPostExpression);
		    		    		map.put("empExper",sysRegList_me.serviceData.strEmpExpression);
		    		    		Rpc({
		    		    			functionId:'SYS00000002',
		    		    			async:true,
		    		    			success: function(form){
		    		    				var returnStr = Ext.decode(form.responseText);
		    		    				if(returnStr.result==true){
		    		    					tableObj.dataStore.reload();
		    		    				}else{
		    		    					Ext.showAlert(sysreg.msg.sysAddErrorMsg);
		    		    				}
		    		    				sysRegList_me.serviceData = {};
		    		    				win.close();
		    		    			}
		    		    		},map);
		    	    		}
		    	    	},checkMap);
		        }
		    },{
		        text: sysreg.button.cancel,
		        handler: function() {
		        	sysRegList_me.serviceData = {};
		        	win.close();
		        }
		    }]/*,
		    renderTo: Ext.getBody()*/
		});
		var win = Ext.widget("window", {
			title : sysreg.label.winName,
			height : 600,
			width : 700,
			minButtonWidth : 40,
			layout : 'fit',
			bodyStyle : 'background:#ffffff;',
			modal : true,
			resizable : false,
			closeAction:'destroy',
			items : [ bodyPanel ]
		});
		win.show();
	},
	deleteSysReg:function(){
		var selectRecord = tableObj.tablePanel.getSelectionModel().getSelection(true);
		if(selectRecord.length<1){
			Ext.showAlert(sysreg.msg.selectDelData);
			return;
		}
		Ext.Msg.confirm(sysreg.msg.promptmessage,sysreg.msg.isDelete,function(btn){
				if(btn=='yes'){
					var arr = new Array();
					for(var i=0; i<selectRecord.length; i++){
						arr[i] = selectRecord[i].data.id;
					}
					var map = new HashMap();
					map.put('method','delete');
					map.put("deletedata",arr);
					Rpc({
				    	functionId:'SYS00000002',
				    	async:true,
				    	success: function(form){
				    		tableObj.dataStore.reload();
				    		}
				    	},map);
				}
			}
		,this);
	},
	toEditSysReg:function(){
		var selectRecord = tableObj.tablePanel.getSelectionModel().getSelection(true);
		if(selectRecord.length<1){
			Ext.showAlert(sysreg.msg.selectEditData);
			return;
		}else if(selectRecord.length>1){
			Ext.showAlert(sysreg.msg.editOneData);
			return;
		}
		var id = selectRecord[0].data.id;
		var map = new HashMap();
		map.put('method','toEdit');
		map.put("id",id);
		Rpc({
	    	functionId:'SYS00000002',
	    	async:true,
	    	success: function(form){
		    		var returnStr = Ext.decode(form.responseText);
		    		sysRegList_me.editSysReg(returnStr);
	    		}
	    	},map);
	},
	editSysReg:function(returnStr){
		if(returnStr.result==false){
			return;
		}
		var targetSystem = returnStr.targetSystem;
		var id = targetSystem.id;
		var editSysCode = targetSystem.syscode;
		var editSysName = targetSystem.sysname;
		var editSysEtoken = targetSystem.sysetoken;
		var editSysDesc = targetSystem.sysdesc;
		var editValid = targetSystem.valid=="1"?true:false;
		var editDynaCode = targetSystem.validateway=="1"?true:false;
		var serviceList = returnStr.serviceList;
		var editServiceStore = Ext.create('Ext.data.Store',{
			fields:['serviceName','dataScope'],
			data : serviceList
		});
		var editServiceGrid = new Ext.grid.GridPanel({
			autoScroll : true,
			store:editServiceStore,
			width : 500,
	        stateful:true,
	        border:1,
	        stripeRows:true,//隔行换色
	        forceFit:true,//让每列自动填满表格，可以根据columns中设置的width按比例分配
    		enableHdMenu:false,//是否显示表头的上下文菜单，默认为true
    		enableColumnHide:false,//是否允许通过标题中的上下文菜单隐藏列，默认为true
    		enableColumnMove:false,//是否允许拖放列，默认为true
    		enableColumnResize:false,//是否允许改变列宽，默认为true
    		columnLines: true,//是否显示列分割线，默认为false
    		loadMask:true,//在store.load()完成之前是否显示遮罩效果，true会一直显示"Loading...",
    		//multiSelect:false,//支持多选
    		selModel: {
    			selType: 'checkboxmodel',
            	columnSelect: true,
            	checkboxSelect: true,
            	pruneRemoved: false,
    	        mode: "SIMPLE",     //"SINGLE"/"SIMPLE"/"MULTI"
    	        checkOnly: true     //只能通过checkbox选择
    	    },
    		columns:[
    		         {	
    		        	header:sysreg.column.serviceName,
         		        dataIndex:'serviceName',
    		        	sortable:false
    		         },
    	             {
    		        	header:sysreg.column.dataScope,
    		        	dataIndex:'dataScope',
    		        	sortable:false,
    		        	align : 'center',
    		        	renderer: function (value, meta, record) {
    		        			var html = '';
    		        			var sqlscope = record.data.sqlscope;
    		        			if(value==1){
    		        				var method = record.data.serviceMethod;
    		        				var opt = '';
    		        				if(method=='getOrg'){
    		        					opt = 'org';
    		        					sysRegList_me.editServiceData.strOrgExpression = sqlscope;
    		        				}else if(method=='getPost'){
    		        					opt = 'post';
    		        					sysRegList_me.editServiceData.strPostExpression = sqlscope;
    		        				}else if(method=='getEmp'){
    		        					opt = 'emp';
    		        					sysRegList_me.editServiceData.strEmpExpression = sqlscope;
    		        				}
    		        				html = "<a href=javascript:sysRegList_me.showDefine('"+opt+"','edit')><img src='/images/new_module/changecompare.gif' border=0></a>";
    		        			}
	    		        		return html;
    		        		} 
    		         }]
    			});
		var editBodyPanel = Ext.create('Ext.form.Panel', {
		    bodyPadding: '20',
		    margin:'auto',
			border:false,
		    layout: 'anchor',
		    defaults: {
		        anchor: '90%'
		    },
		   items:[{
			   	xtype : 'textfield',
			   	/*validator : function(value){
			   		if(value.length<1)
			   			return sysreg.msg.sysCodeMinMsg;
			   		var re = /^[a-zA-Z0-9]+$/;
			   		if(!re.test(value))
			   			return sysreg.msg.sysCodeErrorMsg;
			   		return true;
			   	},*/
			   	readOnly:true,
		        fieldStyle:'background-color:#EEEEEE;',
		    	id:'editSysCode',
		        fieldLabel: sysreg.label.sysCode,
		        labelSeparator:'',
		        beforeLabelTextTpl:"<font color='red'> * </font>",
		        labelAlign:'right',
		        //maxLength:10,
		        value: editSysCode,
		        margin:'0 0 10 0'
		    },
		    {
		    	xtype : 'textfield',
		    	validator : function(value){
			   		if(value.length<1)
			   			return sysreg.msg.sysCodeMinMsg;
			   		return true;
			   	},
		    	id:'editSysName',
		        fieldLabel: sysreg.label.sysName,
		        labelSeparator:'',
		        beforeLabelTextTpl:"<font color='red'> * </font>",
		        labelAlign:'right',
		        maxLength:50,
		        value: editSysName,
		        margin:'0 0 10 0'
		    },
		    {
		    	xtype : 'textfield',
		    	id:'editSysEtoken',
		        fieldLabel: sysreg.label.sysEtoken,
		        labelSeparator:'',
		        labelAlign:'right',
		        maxLength:50,
		        readOnly:true,
		        fieldStyle:'background-color:#EEEEEE;',
		        allowBlank: false,
		        margin:'0 0 10 0',
		        value:editSysEtoken
		    },
		    {
		        xtype : 'textareafield',
		        id  : 'editDescription',
		        fieldLabel: sysreg.label.description,
		        labelSeparator:'',
		        labelAlign:'right',
		        grow : false,
		        height:80,
		        value:editSysDesc,
		        margin:'0 0 10 0'
		    },{
		    	xtype:'checkbox',
		    	boxLabel:sysreg.label.valid,
		    	id:'editValid',
		    	margin : '0 0 0 57',
		    	checked:editValid,//默认选中
		    	labelAlign:"left",
		    	handler : function() {
		    		if(this.checked==true){
		    			editServiceGrid.getSelectionModel().setLocked(false);//将服务选项设置为能勾选
		    			Ext.getCmp('editDynaCode').readOnly=false;//将动态认证选项设置为能勾选
		    		}else{
		    			editServiceGrid.getSelectionModel().setLocked(true);
		    			Ext.getCmp('editDynaCode').readOnly=true;
		    		}
		    	}
		    },{
				xtype : 'fieldset',
				id : 'editServiceSet',
				margin : '0 0 10 57',
				height : 220,
				title : sysreg.label.serviceSet,
				layout : 'fit',
				items : [ editServiceGrid ]
		    },{
		    	xtype:'checkbox',
		    	boxLabel:sysreg.label.dynaCode,
		    	id:'editDynaCode',
		    	margin : '0 0 10 57',
		    	checked:editDynaCode,
		    	labelAlign:"left",
		    }
		   ],
		    buttonAlign:'center',
		    buttons: [{
		        text: sysreg.button.ok,
		        formBind:true,
		        handler:function() {
		        	var serviceList = [];
		            var editSysCode = Ext.util.Format.trim(Ext.getCmp('editSysCode').getValue());
		            var editSysName = Ext.util.Format.trim(Ext.getCmp('editSysName').getValue());
		            var editSysEtoken = Ext.util.Format.trim(Ext.getCmp('editSysEtoken').getValue());
		            if(editSysCode.length==0){
		            	Ext.showAlert(sysreg.msg.sysCodeMsg);
		            	return;
		            }else if(editSysName.length==0){
		            	Ext.showAlert(sysreg.msg.sysNameMsg);
		            	return;
		            }
		            if(editSysCode.length>10){
		            	Ext.showAlert(sysreg.msg.sysCodeMaxMsg);
		            	return;
		            }else if(editSysName.length>50){
		            	Ext.showAlert(sysreg.msg.sysNameMaxMsg);
		            	return;
		            }
		            var checkMap = new HashMap();
		            checkMap.put('method','check');
		            checkMap.put('addOrEdit','edit');
		            checkMap.put('checkCode',editSysCode);
		            checkMap.put('checkName',editSysName);
		            checkMap.put('editSysEtoken',editSysEtoken);
		            Rpc({
		    	    	functionId:'SYS00000002',
		    	    	async:true,
		    	    	success: function(form){
			    	    		var returnStr = Ext.decode(form.responseText);
			    	    		var reMsg = returnStr.result;
		    		    		if(reMsg != 'true'){
		    		    			if(reMsg == 'code'){
		    		    				Ext.showAlert(sysreg.msg.checkCodeMsg);
		    		    			}else if(reMsg == 'name'){
		    		    				Ext.showAlert(sysreg.msg.checkNameMsg);
		    		    			}else if(reMsg == 'false'){
		    		    				Ext.showAlert(sysreg.msg.checkErrorMsg);
		    		    			}
		    		    			return;
		    		    		}
		    		    		var editDescription = Ext.getCmp('editDescription').getValue();
		    		            var editValid = Ext.getCmp('editValid').getValue();
		    		            var editDynaCode = Ext.getCmp('editDynaCode').getValue();
		    		            var selected = editServiceGrid.getSelectionModel().getSelected();
		    		            if(selected.length>0){
		    		            	var items = selected.items;
		    		            	for(var i=0; i<selected.length ; i++){
		    		            		serviceList[i] = items[i].data.serviceMethod;
		    		            	}
		    		            }
		    		            var map = new HashMap();
		    		            map.put('method','edit');
		    		    		map.put('id',id);
		    		    		map.put("sysCode",editSysCode);
		    		    		map.put("sysName",editSysName);
		    		    		map.put("description",editDescription);
		    		    		map.put("valid",editValid);
		    		    		map.put("dynaCode",editDynaCode);
		    		    		map.put("serviceList",serviceList);
		    		    		map.put("orgExper",sysRegList_me.editServiceData.strOrgExpression);
		    		    		map.put("postExper",sysRegList_me.editServiceData.strPostExpression);
		    		    		map.put("empExper",sysRegList_me.editServiceData.strEmpExpression);
		    		    		Rpc({
		    		    	    	functionId:'SYS00000002',
		    		    	    	async:true,
		    		    	    	success: function(form){
		    		    		    		var returnStr = Ext.decode(form.responseText);
		    		    		    		if(returnStr.result==true){
		    		    		    			tableObj.dataStore.reload();
		    		    		    		}else{
		    		    		    			Ext.showAlert(sysreg.msg.sysAddErrorMsg);
		    		    		    		}
		    		    		    		sysRegList_me.editServiceData = {};
		    		    		    		editWin.close();
		    		    	    		}
		    		    	    	},map);
		    	    		}
		    	    	},checkMap);
		        }
		    },{
		        text: sysreg.button.cancel,
		        handler: function() {
		        	sysRegList_me.serviceData = {};
		        	editWin.close();
		        }
		    }]/*,
		    renderTo: Ext.getBody()*/
		});
		var editWin = Ext.widget("window", {
			title : sysreg.label.editWinName,
			height : 600,
			width : 700,
			minButtonWidth : 40,
			layout : 'fit',
			bodyStyle : 'background:#ffffff;',
			modal : true,
			resizable : false,
			items : [ editBodyPanel ]
		});
		editWin.show();
		// 动态选中复选框开始
		var selected = [];
		editServiceGrid.getStore().each(function(record){
			var valid = record.data.valid;
			if(valid == '1'){
				selected.push(record);
			}
		});
		var model = editServiceGrid.getSelectionModel();
		model.select(selected);
		model.setLocked(!editValid);
		Ext.getCmp('editDynaCode').readOnly=!editValid;
		// 动态选中复选框结束
	},
	showDefine:function(opt,method){
		var exper = '';
		if(method == 'add'){
			if(opt=='org'){
				exper = sysRegList_me.serviceData.strOrgExpression;
			}else if(opt=='post'){
				exper = sysRegList_me.serviceData.strPostExpression;
			}else if(opt=='emp'){
				exper = sysRegList_me.serviceData.strEmpExpression;
			}
		}else if(method == 'edit'){
			if(opt=='org'){
				exper = sysRegList_me.editServiceData.strOrgExpression;
			}else if(opt=='post'){
				exper = sysRegList_me.editServiceData.strPostExpression;
			}else if(opt=='emp'){
				exper = sysRegList_me.editServiceData.strEmpExpression;
			}
		}
		var map = new HashMap();
		map.put("saveText", common.button.ok);//确定
		Ext.Loader.setPath("defineUL","../../../module/system/regothersys");
	 	Ext.require('defineUL.SysDefineCondition',function(){
	 		Ext.create("defineUL.SysDefineCondition",{dataMap:map,imodule:'3',method:method,opt:opt,conditions:exper,afterfunc:'sysRegList_me.getConditions'});
	 	});
	},
	getConditions:function(conditions,opt,method){
		if(method == 'add'){
			if(opt=='org'){
				sysRegList_me.serviceData.strOrgExpression = conditions;
			}else if(opt=='post'){
				sysRegList_me.serviceData.strPostExpression = conditions;
			}else if(opt=='emp'){
				sysRegList_me.serviceData.strEmpExpression = conditions;
			}
		}else if(method == 'edit'){
			if(opt=='org'){
				sysRegList_me.editServiceData.strOrgExpression = conditions;
			}else if(opt=='post'){
				sysRegList_me.editServiceData.strPostExpression = conditions;
			}else if(opt=='emp'){
				sysRegList_me.editServiceData.strEmpExpression = conditions;
			}
		}
	},
	transDownloadLog:function(value, metaData, Record){
		var name = Record.data.syscode;
		var html = "<a href=javascript:sysRegList_me.downloadLog('"+name+"');>"+sysreg.button.downLoad+"</a>";
		return html;
	},
	transState:function(value, metaData, Record){
		var state = Record.data.valid;
		if(state==1){
			return sysreg.label.valid;
			//return '<font color="green">'+sysreg.label.valid+'</font>';
		}else{
			return sysreg.label.invalid;
			//return '<font color="red">'+sysreg.label.invalid+'</font>';
		}
	},
	downloadLog:function(name){
		var map = new HashMap();
		map.put("name",name);
		map.put("method",'download');
		Rpc({
	    	functionId:'SYS00000002',
	    	async:true,
	    	success: function(form){
		    		var returnStr = Ext.decode(form.responseText);
		    		if(returnStr.result==true){
		    			var fileName = returnStr.fileName;
		    			window.location.target="_blank";
		    			window.location.href="/servlet/vfsservlet?fromjavafolder=true&fileid="+decode(fileName);
		    		}else{
		    			Ext.showAlert(sysreg.msg.logNonexistent);
		    		}
	    		}
	    	},map);
	}
});
	