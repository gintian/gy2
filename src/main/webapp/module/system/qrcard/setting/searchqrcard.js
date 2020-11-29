Ext.define("QRCard.setting.searchqrcard", {
	requires:["QRCard.setting.qrcardSetting","QRCard.setting.addqrcard"],
	extend : "Ext.panel.Panel",
	id : 'searchqrcard',
	title :qr.funcName, //员工信息登记二维码
	//上右下左
	bodyPadding : '20 0 0 0',
	bodyStyle : 'overflow-x:hidden; overflow-y:scroll',
	/*autoScroll: true,*/
	listeners : {
		render:function(){
			Ext.getCmp('searchqrcard_header').setStyle({borderStyle:'hidden hidden solid hidden'});
		}},
	fillBrowser : function(obj){
		if(obj != null){
			obj.setWidth(document.body.clientWidth);
		}
	},
	//初始化组件并初始化框架参数
	initComponent : function() {
		var me = this;
		me.formInfo="";//用于设置页面回调信息
		me.detail_description="";//用于打印二维码页面信息回调
		me.flag="";//用于设置页面信息和二维码打印页面信息实时更新标识
		me.qrCardData = "";//用于获取后台所需要的数据
		me.addPriv = "";//用于获取添加权限
		me.deletePriv = "";//用于获取删除权限
		me.clearPriv = "";//用于获取清除权限
		me.addOrganizationId = "";//用于新建表单的机构id
		me.addOrganizationName = "";//用于新建表单的机构名称
		me.PerTransferTablePriv = "";//用于判断是否拥有人员调入表单资源权限
		me.callParent();//继承父类的对应的初始化函数
		me.loadData();//执行此方法请求后台数据
		me.initFrameConfig();//执行主函数
		Ext.EventManager.onWindowResize(function(){ 
			me.fillBrowser(Ext.getCmp('formContainer'));
		});
	},
	//请求数据
	loadData : function() {
		var me = this;
		var vo = new HashMap();
		Rpc({
			functionId : 'SYS00001001',
			async : false,
			success : function(form, action) {	
				var result_obj = Ext.decode(form.responseText);
				me.qrCardData = result_obj.qrCardData;//接收后台基本数据，此数据是一个list，list里面的元素是map
				me.addPriv = result_obj.addPriv;//获取是否有添加权限
				me.deletePriv = result_obj.deletePriv;//判断是否有删除权限
				me.clearPriv = result_obj.clearPriv;//判断是否有修改权限
				me.addOrganizationId = result_obj.addOrganizationId;
				me.addOrganizationName = result_obj.addOrganizationName;
				me.PerTransferTablePriv = result_obj.PerTransferTablePriv;
				var return_code = result_obj.return_code;
				if(return_code && return_code == 'fail'){
					var return_msg = result_obj.return_msg;
					Ext.MessageBox.alert(qr.remind,qr.error[return_msg]);
					return;
				}
				if(!me.PerTransferTablePriv){
					Ext.MessageBox.alert(qr.remind,qr.tablePriv);
				}
			},
			scope : me
		}, vo);
	},
	//回调函数添加表单
	addPanel:function(formInfo){
		var me = this;
		//重新获取后台数据
		me.loadData();
		//执行formFrame方法生成新的表单框
		var temporaryFormFrame = me.formFrame(me.qrCardData[me.qrCardData.length-1]);
		//给新生成的表单框的itemid赋值
		temporaryFormFrame.itemId = me.qrCardData[me.qrCardData.length-1].qrid;
		Ext.getCmp('formContainer').insert(Ext.getCmp('formContainer').items.items.length-1,temporaryFormFrame);
		//原来是移除再添加，现在不需要做那么多工作了浪费性能
		//1.移除添加表单框
		//Ext.getCmp('formContainer').remove(Ext.getCmp('addcontainer'),{destroy:true});
		//2.添加新将建的表单框
		// //3.创建新的添加表单框
		// var createForm = me.createFormFrame();
		// if(!me.addPriv){
		// 	createForm.hide();
		// }
		// Ext.getCmp('formContainer').add(createForm);

	},
	//回调函数更新表单
	updatePanel:function(formInfo,itemId){
		var me = this;
		//更新数据
		me.formInfo=formInfo;
		//设置标识表示数据已更新
		me.flag = itemId;
		//获取当前操作的panel
		var container = me.query('#'+itemId)[0];
		//将当前panel的name值进行改变
		container.query('#name')[0].setHtml(formInfo.name);
		//将当前panel的description值进行改变
		container.query('#description')[0].setHtml(formInfo.description);
	},
	//回调函数更新表单内容描述
	updateDetailDescription:function(detail_description,itemId){
		var me = this;
		//将改变后的detail_description赋值给me.detail_description
		me.detail_description = detail_description;
		//改变标识表示数据已经进行更新
		me.flag = itemId;
	},
	//主函数，入口
	initFrameConfig : function() {
		var me = this;
		var explainPanel = Ext.create("Ext.panel.Panel", {
			width : 450,
			height : 130,
			border : false,
			html : "<div style='text-indent:1.5em;font-size:20px;color:#ff9224;'>"+qr.explain+"</div><br>"//说明
					+ "<div style='text-indent:4em;font-size:15px;'>"+qr.explainContentOne+"</div><br>"//1.您可以打印二维码，当员工入职时，显示员工扫码
					+ "<div style='text-indent:4em;font-size:15px;'>"+qr.explainContentTwo+"</div><br>"//2.针对不同的岗位和部门招聘入职可定义不同信息登记表
		})
		//将标头文字添加到window
		me.add(explainPanel);
		//执行createFormContainer进行各个表单框的加载
		me.createFormContainer(me.qrCardData.length);
	},
	//表单框
	formFrame : function(msg) {
		var me = this;
		var printButton = Ext.widget('container',{
			scope:me,
			width:100,
			height:40,
			margin:'0 0 0 50',
			style:'background:url(/module/system/qrcard/images/buttonground-blue.png);font-size:16px;color:white;cursor:pointer;line-height:40px;text-align:center',
			html:qr.printQrcard,
			listeners : {
				element:'el',
				click:function() {
					//判断是否是同一个panel
					if(me.flag==''){
					   me.flag = msg.qrid;	
					}
					//实现打印二维码内容的更新
					if(me.detail_description !='' && me.flag == msg.qrid){
						msg.detail_description = me.detail_description;
					}
					//实现打印二维码表名的更新
					if(me.formInfo !='' && me.flag == msg.qrid){
						msg.name = me.formInfo.name;
					}
					//调用qrcardSetting.js
					me.destroy();
					window.location.href = "qrcardSetting.html?qrid="+msg.qrid;
				}
			}
		});
		var setButton = Ext.widget('container',{
			scope:me,
			width:100,
			height:40,
			border:1,
			style:'background:url(/module/system/qrcard/images/buttonground-white.png);font-size:16px;color:black;cursor:pointer;line-height:40px;text-align:center;borderColor:#C5C5C5; borderStyle:solid; borderWidth:1px',
			html:qr.setting,
			listeners : {
				element:'el',
				click:function() {
					if(!me.PerTransferTablePriv){
						Ext.Msg.alert(qr.remind,qr.notAddTemplate);
						return;
					}
					//判断是否是同一个panel
					if(me.flag==''){
					   me.flag = msg.qrid;	
					}
					//实现设置内容和显示内容同步
					if(me.formInfo!=''&&me.flag ==msg.qrid){
						msg.tabid = me.formInfo.tabid;
						msg.name = me.formInfo.name;
						msg.description = me.formInfo.description;
						msg.b0110 = me.formInfo.b0110;
						msg.organizationName=me.formInfo.organizationName;
						msg.tabName = me.formInfo.tabName;
					}
					//调用addqrcard.js
					Ext.create("QRCard.setting.addqrcard",{qrCardData:msg,clearPriv:me.clearPriv}).show();
				}
			}
		});
		//表单框的设置按钮和打印二维码按钮的配置
		var buttonsPanel = Ext.create("Ext.panel.Panel", {
			layout : {
			pack: 'center',
			type : 'hbox'
			},
			border : false,
			margin:'9 0 0 0',
			height:40,
			width :'100%',
			items:[setButton,printButton]
			});
		//表单框内容
		if(msg.name){
			msg.name = msg.name.replace(/(\n)/g, "");    
    	    msg.name = msg.name.replace(/(\t)/g, "");    
        	msg.name = msg.name.replace(/(\r)/g, "");    
        	msg.name = msg.name.replace(/<\/?[^>]*>/g, "");    
        	msg.name = msg.name.replace(/\s*/g, "");  
		}
        if(msg.description){
			msg.description = msg.description.replace(/(\n)/g, "");    
	        msg.description = msg.description.replace(/(\t)/g, "");    
	        msg.description = msg.description.replace(/(\r)/g, "");    
	        msg.description = msg.description.replace(/<\/?[^>]*>/g, "");    
	        msg.description = msg.description.replace(/\s*/g, "");  
        }
		var formContentPanel = Ext.create("Ext.panel.Panel", {
			//上右下左
			margin : '10 0 0 14',
			width : 350,
			height : 260,
			border : false,
			items : [{
						xtype : 'image',
						style : 'width:80px; height:80px;',
						margin : '0 0 2 138',
						//编辑图片的加载
						src : '/module/system/qrcard/images/search_table.png'
					},
					{
						xtype:'component',
						itemId:'name',
						margin : '0 0 10 0',
						html:msg.name,
						style:'font-size:18px;text-align:center'
					},
					{
						xtype:'component',
						height : 50,
						itemId:'description',
						html:msg.description,
						style:'font-size:13px;text-align:left'
					},buttonsPanel
			]
		});
		//删除图片配置
		var delImg = Ext.create('Ext.Img', {
			style:'cursor:pointer;position:absolute;top:1px;right:1px;',
			hidden:true,
			width:20,
			height:20,
			//加载删除图片
			src : '/components/homewidget/images/del.png',
			listeners : {
				//设置当光标在当前这个panel时显示删除图标，光标不在这个panel时光标消失
				render:function(){
					if(me.deletePriv){
					this.ownerCt.getEl().on('mouseover',function(){
						this.show();
					},this);
					this.ownerCt.getEl().on('mouseout',function(){
						this.hide();
					},this);
					}else{
					this.hide();
					}
				},
				//点击事件，当点击删除图标时执行删除操作
				click : {
					element : 'el',
					fn : function() {
						//点击删除时进行提示框判断防止误删的情况
						Ext.MessageBox.confirm(qr.remind, qr.whetherDelete,function(btn){
							if(btn=="yes"){
							//将后台需要的数据进行组装
							var qrid = msg.qrid;
							var vo = new HashMap();
							var uploadMsg = new HashMap();
							uploadMsg.put("qrid", qrid);
							uploadMsg.put("type", "delete");
							vo.put("qrCardData",uploadMsg);
							//调用后台方法
							Rpc({
								functionId : 'SYS00001002',
								async : false,
								success : function(form, action) {
									//获取后台返回的数据判断是否成功，并进行提示
									var result = Ext.decode(form.responseText).result;
									if (result) {
										 me.query('#formContainer')[0].remove(containerPanel);
										Ext.Msg.alert(qr.remind,qr.deleteSuccess);
									}else{
										Ext.Msg.alert(qr.remind,qr.deleteFailure);
									}
								},
								scope : this
							}, vo);
							}
						})
					}
				}
			}
		});
		//生成表单框panel
		var formContentContainer = Ext.create('Ext.panel.Panel', {
			width : 380,
			height : 260,
			border:1,
			margin:'10 0 0 0',
			items : [formContentPanel]
		});
		//此panel包含表单框panel和删除图标
		var containerPanel = Ext.widget('container',{
			//上右下左
			margin : '0 0 30 30',
			width : 390,
			height : 270,
			style:'float:left',
			border:false,
			items:[formContentContainer,delImg]
		});
		return containerPanel;
	},
	//创建新建功能表单框
	createFormFrame : function() {
		var me = this;
		//表单框内容
		var createFormPanel = Ext.create("Ext.panel.Panel", {
			//上右下左
			/*margin : '30 0 0 15',*/
			width : 380,
			height : 260,
			border : false,
			style : 'cursor:pointer',
			items : [{
						xtype : 'image',
						style : 'width:75px; height:75px',
						margin : '45 0 5 157',
						src : '/module/system/qrcard/images/search_buildform.png',
					}, {
						html : "<div style='text-align:center;font-size:18px;font-weight:bold;'>"+qr.newForm+"</div><br>"//新建表单
								+ "<div style='text-align:center;font-size:13px;'>"+qr.newFormIntroduce+"</div><br>",//可以选择入职业务表单，自定义扫码欢迎页
						border : false
					}
			],
			listeners : {
				click : {
					element : 'el',
					fn : function() {
						//调用addqrcard.js并将tableJsonArray（关联表单选择列表）传过去
						if(!me.PerTransferTablePriv){
							Ext.Msg.alert(qr.remind,qr.notAddTemplate);
							return;
						}
						Ext.create("QRCard.setting.addqrcard",{addOrganizationName:me.addOrganizationName,addOrganizationId:me.addOrganizationId,clearPriv:me.clearPriv}).show();
					}
				}
			}
		});
		//表单框最外层panel
		var createContainer = Ext.create('Ext.panel.Panel', {
					id:'addcontainer',
					//上右下左
					margin : '10 0 30 30',
					width : 380,
					height : 260,
					border:1,
					style:'float:left;',
					items : [createFormPanel]
				});
		return createContainer;
	},
	//创建表单的容器
	createFormContainer : function(num) {
		var me = this;
		var order = 0;
		var num = num;
		var formContainer = Ext.create('Ext.panel.Panel', {
					border : false,
					id:'formContainer',
					itemId:'formContainer',
					width:'100%'
				});
		//循环添加多个表单
		while (num > 0) {
			//创建各个panel
			var temporaryFormFrame = me.formFrame(me.qrCardData[order]);
			//给各个panel的tiemId进行赋值
			temporaryFormFrame.itemId = me.qrCardData[order].qrid;
			//将创建的各个panel添加的容器中
			formContainer.add(temporaryFormFrame);
			num--;
			order++;
		}
		//新建表单框的矿建
		var createForm = me.createFormFrame();
		if(!me.addPriv){
			createForm.hide();
		}
		formContainer.add(createForm);
		me.add(formContainer);
	}
});