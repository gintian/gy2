Ext.define('GetZpAccountsUrl.GetZpAccounts', {
	zpAccounts : '',
	constructor : function(config) {
		zpAccounts = this;
		this.init();
	},
	// 初始化函数
	init : function() {
		var map = new HashMap();
		Rpc( {
			functionId : 'ZP0000002651',
			success : zpAccounts.createPanel
		}, map);
	},
	
	createPanel : function(response) {
		var value = response.responseText;
		var map = Ext.decode(value);
		
		if (map.succeed) {
			var nameField = Ext.create('Ext.form.field.Text', {
				id : 'nameId',
				height : 30,
				width : 400,
				border : 1,
				margin : '20 0 0 0',
				fieldLabel : '<font>' + map.nameDesc + '</font>',
				labelAlign : 'right',
				emptyText : '请输入' + map.nameDesc
			});
				
			var phoneField = Ext.create('Ext.form.field.Text', {
				id : 'phoneId',
				region : 'north',
				height : 30,
				width : 400,
				border : 1,
				margin : '20 0 0 0',
				fieldLabel : '<font>' + map.phoneFieldDesc + '</font>',
				labelAlign : 'right',
				emptyText : '请输入' + map.phoneFieldDesc
			});
				
			
			var onlyField = Ext.create('Ext.form.field.Text', {
				id : 'onlyId',
				height : 30,
				width : 400,
				border : 1,
				margin : '20 0 0 0',
				fieldLabel : '<font>' + map.onlynFieldDesc + '</font>',
				labelAlign : 'right',
				emptyText : '请输入'  + map.onlynFieldDesc
			});
			
			var code = Ext.create('Ext.form.field.Text', {
				id : 'codeId',
				height : 30,
				width : 400,
				margin : '0 0 5 0',
				border : 1,
				fieldLabel : '<font>验证码</font>',
				labelAlign : 'right',
				emptyText : '请输入验证码'
			});
			
			var vaildataCode = Ext.create('Ext.panel.Panel', {
				id: 'codePanelId',
				height : 50,
				border : false,
				margin : '20 0 0 0',
				layout : 'hbox',
				items : [code, {
					xtype : 'box',
					id : 'vaildataCode',
					width : 100,
					height : 30,
					padding : '0 0 0 10',
					autoEl : {
						tag : 'img', // 指定为img标签
						src : '/servlet/vaildataCode?out=true&channel=0&codelen=4'
					},
					listeners : {
						render : function() {
							this.getEl().on('click', zpAccounts.refresh);
						}
					}
				},{
					xtype : 'label',
					padding : '0 0 0 10',
					forId : 'myFieldId',
					cls : 'myField',
					text : '点击图片刷新验证码'
				} ]
			});
				
			var button = Ext.create('Ext.Button', {
				id : 'submit',
				border : false,
				cls : 'button',
				iconCls : 'submit-icon',
				handler : zpAccounts.checkAccounts
			});
			
			var north = Ext.create('Ext.panel.Panel', {
				id : 'northId',
				region : 'north',
				height : 100,
				border : false,
				html : '<div class="bh-wzm-all"><div class="bh-wzm-logo">找回注册帐号</div></div>'
			});
				
			var container = Ext.create("Ext.Container", {
				flex : 1,
				width : 700,
				height : 400,
				id : 'containerId',
				items : [ nameField, phoneField, onlyField,
				          vaildataCode, button ]
			});
			
			var center = Ext.create('Ext.panel.Panel', {
				region : 'center',
				border : false,
				cls : 'bh-div',
				layout : {
					align : 'middle',
					type : 'vbox'
				},
				items : [ container ]
			});
			
			Ext.create('Ext.container.Viewport', {
				layout : 'border',
				items : [ north, center ]
			});
		}
	},
	// 刷新验证码
	refresh : function() {
		var url = document.getElementById('vaildataCode').src;
		document.getElementById('vaildataCode').src = url
		+ "&id=" + Math.random();
	},

	checkAccounts : function() {
		var showMsg = Ext.getCmp("msgId");
		if(showMsg) {
			var container = Ext.getCmp("containerId");
			container.remove(showMsg);
		}
		
		var name = Ext.getCmp('nameId');
		var nameValue = name.value;
		if(!nameValue) {
			Ext.showAlert(name.fieldLabel.replace(/<[^>]+>/g,"") + "输入不能为空！");
			return;
		}
		
		var phone = Ext.getCmp('phoneId');
		var phoneValue = phone.value;
		if(!phoneValue) {
			Ext.showAlert(phone.fieldLabel.replace(/<[^>]+>/g,"") + "输入不能为空！");
			return;
		}
		
		var only = Ext.getCmp('onlyId');
		var onlyValue = only.value;
		if(!onlyValue){
			Ext.showAlert(only.fieldLabel.replace(/<[^>]+>/g,"") + "输入不能为空！");
			return;
		}
		
		var code = Ext.getCmp('codeId');
		var codeValue = code.value;
		if(!codeValue){
			Ext.showAlert(code.fieldLabel.replace(/<[^>]+>/g,"") + "输入不能为空！");
			zpAccounts.refresh();
			return;
		}

		Ext.Ajax.request({
	        url: '/servlet/AboutAccountServlet',
	        async:false,
	        method:'post',
	        params: {
	        	nameValue:nameValue,
	        	phoneValue:phoneValue,
	        	onlyValue:onlyValue,
	        	codeValue:codeValue,
	        	operate:"retrieveAccount"
	        },
	        success: zpAccounts.returnSccuss
	    });
	},
	
	returnSccuss: function (response) {
		var value = response.responseText;
		var map = Ext.decode(value);
		var flag = map.flag;
		zpAccounts.refresh();
		if("validatecode-error"==flag){
			Ext.showAlert("验证码错误！");
			return;
		}
		var showMsg = Ext.create("Ext.panel.Panel", {
			id : 'msgId',
			border: false,
			height: '60',
			width: '100%'
		}); 
		var flag = map.flag;
		var container = Ext.getCmp("containerId");
		if("true" == flag) {
			var button = Ext.getCmp("submit");
			var code = Ext.getCmp("codePanelId");
			showMsg.html="<p style='margin-top: 20px;'>您的注册帐号是："+map.msg+"，请牢记！</p>" +
					"<P style='margin-top: 20px;'>如忘记密码，可以重新设置密码，请点击<a href='###' style='font-size:18px; font-family:\"微软雅黑\";' onclick='zpAccounts.resetPassword();'>重设密码</a>。</P>";
			container.remove(code);
			container.remove(button);
			container.add(showMsg);
		} else if("false" == flag) { 
			zpAccounts.refresh();
			showMsg.html="<p style='margin-top: 20px;'>没有找到您的注册信息！请确认填写信息是否有误。</p>";
			container.insert(4,showMsg);
		} else {
			zpAccounts.refresh();
			Ext.showAlert(flag);
		}
	},
	
	resetPassword: function () {
		window.location.href = "/module/hire/resetPassword.html?flag=1";
	}

});


