Ext.define('QRCard.setting.addqrcard', {
	extend : 'Ext.window.Window',
	requires:["EHR.templateSelector.TemplateSelector",'EHR.extWidget.field.CodeTreeCombox'],
	title : qr.deployMsgName,//入职登记表单配置
	width : 590,// 宽
	height : 344,// 高
	id : 'addqrcard',
	qrCardData : '',
	modal : true,
	constrain : true,
	resizable : false,//禁止缩放
	/*draggable : false,//禁止拖动
*/	initComponent : function() {
		var me = this;
		me.callParent();
		me.init();
	},
	//获取模板
	selectTemplate:function(){
		var me = this;
		/*var templateType =1;*/
		Ext.widget('window',{
			title:qr.chooseModel,
			height:400,
			width:300,
			layout:'fit',
			modal : true,
			items:{
				xtype:'templateselector',
				dataType:1,
				templateType:'01',
				childTemplateType:'0',
				listeners:{
					itemclick:function(tree,node){
						if(node.get('leaf')){
							var text = node.get('text')
							var tabName=text.split(".");  
							Ext.getCmp('tabid').setValue(tabName[1]);
							Ext.getCmp('tabid').templateId=node.get('id');
							tree.up('window').close();
						}
					}
				}
			}
		}).show();
	},
	//获取字符串长度（汉字算两个字符，字母数字算一个）
	getByteLen : function(val) {
		var charnum = 0;//字符数
		var varlength = 0;//前50字符长度
		for (var i = 0; i < val.length; i++) {
			var a = val.charAt(i);
			if (a.match(/^[\u4e00-\u9fa5_a-zA-Z0-9]+$/ig) != null) {
				charnum = charnum + 2;
				varlength = varlength + 1;
			} else {
				charnum = charnum + 1;
				varlength = varlength + 1;
			}
			if (charnum == 100) {
				return varlength;
			} else if (charnum > 100) {
				return varlength - 1;
			}
		}
	},
	// 将信息传到后台实现添加或修改
	addInformation : function() {
		var me = this;
		//获取itemId为explainPanel的组件
		var showPanel = me.query('#showPanel')[0];
		//获取name的值
		var name = showPanel.query('#name')[0].getValue();
		name = name.replace(/(\n)/g, "");    
        name = name.replace(/(\t)/g, "");    
        name = name.replace(/(\r)/g, "");    
        name = name.replace(/<\/?[^>]*>/g, "");    
        name = name.replace(/\s*/g, "");  
        if(name.length>20){
			Ext.MessageBox.alert(qr.remind,qr.tipNameDesc);	
			return;
		}
		//获取tabid的值
		/*var tabid = showPanel.query('#tabid')[0].getValue();*/
		var tabid = Ext.getCmp('tabid').templateId;
		//获取模板名称
		var templateName = Ext.getCmp('tabid').getValue();
		//获取description的值
		var description = showPanel.query('#description')[0].getValue();
		if(!description || description.length < 1){
			Ext.MessageBox.alert(qr.remind,qr.perfectMsg);	
			return;
		}
		description = description.replace(/(\n)/g, "");    
        description = description.replace(/(\t)/g, "");    
        description = description.replace(/(\r)/g, "");    
        description = description.replace(/<\/?[^>]*>/g, "");    
        description = description.replace(/\s*/g, "");  
        if(!description || description.length < 1){
			Ext.MessageBox.alert(qr.remind,qr.businessDescriptionTip);	
			return;
		}    
		if(description.length>me.getByteLen(description)){
			Ext.MessageBox.alert(qr.remind,qr.charNums);	
			return;
		}
		description = description.substring(0, me.getByteLen(description));
		//获取b0110的值
		var b0110 = showPanel.query('#organization')[0].getValue();
		var organizationName;
		if(b0110){
			organizationName = b0110.split('`')[1];
			b0110 = b0110.split('`')[0];
		}
			
		// 判断输入的是否为空
		if (tabid == ""
				|| tabid == null
				|| name == ""
				|| description == ""
				|| (b0110 == "" || b0110 == null)) {
			Ext.MessageBox.alert(qr.remind, qr.perfectMsg);//请将信息填写完整！
		} else {
			//将获取的值封装到HashMap中
			var vo = new HashMap();
			var msg = new HashMap();
			var qrid = '';
			msg.put("tabid", tabid);
			msg.put("name", name);
			msg.put("description", description);
			msg.put("b0110", b0110);
			msg.put("tabName", templateName);
			msg.put("organizationName", organizationName)
			// 判断是添加或者修改
			if ('' == (me.qrCardData)) {
				msg.put("type", "add");
				vo.put("qrCardData", msg);
				Rpc({
							functionId : 'SYS00001002',
							async : false,
							success : function(form, action) {
								var result = Ext.decode(form.responseText).result;
								if (result) {
									me.close();
									Ext.MessageBox.alert(qr.remind,qr.addSuccess);
									Ext.getCmp('searchqrcard').addPanel(msg);
								} else {
									if(Ext.decode(form.responseText).return_msg ==1)
										Ext.MessageBox.alert(qr.remind,qr.returnMsg.msg1);
									else
										Ext.MessageBox.alert(qr.remind,qr.addFailure);
								}
							},
							scope : this
						}, vo);
			} else {
				var parent = me.config.qrCardData;
				if((parent.name==msg.name)&&(parent.description==msg.description)&&(parent.tabid==msg.tabid)&&(parent.b0110==msg.b0110)){
					me.close();
					return;
				}
				qrid = parent.qrid;
				msg.put("qrid", qrid);
				msg.put("type", "update");
				vo.put("qrCardData", msg);
				Rpc({
							functionId : 'SYS00001002',
							async : false,
							success : function(form, action) {
								var result = Ext.decode(form.responseText).result;
								if (result) {
									me.close();
									Ext.getCmp('searchqrcard').updatePanel(msg,qrid);
								} else {
									if(Ext.decode(form.responseText).return_msg ==1)
										Ext.MessageBox.alert(qr.remind,qr.returnMsg.msg1);
									else
										Ext.MessageBox.alert(qr.remind,qr.settingFailure);
								}
							},
							scope : this
						}, vo);
			}
		}
	},
	// 主函数，入口
	init : function() {
		var me = this;
		//设置确定和取消按钮
		var okButton = Ext.widget('container',{
			scope:me,
			width:70,
			height:21,
			// style:'background:url(/module/system/qrcard/images/buttonground-blue.png);font-size:12px;color:white;cursor:pointer;line-height:21px;text-align:center',
			style:'background:url(/module/system/qrcard/images/buttonground-white.png);font-size:12px;color:black;cursor:pointer;line-height:21px;text-align:center;borderColor:#C5C5C5; borderStyle:solid; borderWidth:1px',
			html:qr.determine,
			listeners : {
				element:'el',
				click:function() {
					Ext.getCmp('addqrcard').addInformation();
				}
			}
		});
		var cancelButton = Ext.widget('container',{
			id:'cancelButton',
			scope:me,
			width:70,
			height:21,
			margin:'0 0 0 50',
			border:1,
			style:'background:url(/module/system/qrcard/images/buttonground-white.png);font-size:12px;color:black;cursor:pointer;line-height:21px;text-align:center;borderColor:#C5C5C5; borderStyle:solid; borderWidth:1px',
			html:qr.settingPageCancel,
			listeners : {
				element:'el',
				click:function() {
					Ext.getCmp('addqrcard').close();
				}
			}
		});
		//创建showPanel包含输入框和按钮		
		var b0110 = me.qrCardData.b0110;
		if(!b0110 && me.config.addOrganizationId && me.config.addOrganizationName)
			b0110 = me.config.addOrganizationId +'`'+ me.config.addOrganizationName;
		if(b0110 && b0110.indexOf('`') == -1)
			b0110 = me.qrCardData.b0110+'`'+me.qrCardData.organizationName;
		var showPanel = Ext.create("Ext.panel.Panel", {
			width : '100%',// 宽
			height : '100%',// 高
			itemId : 'showPanel',
			border : false,
			items : [{
				xtype : 'textfield',
				readOnlyCls : 'backgroun-color: #ffffff',
				itemId : 'name',
				fieldLabel : '<span style="margin:0px 10px 0px 0px",>'
						+ qr.businessForm + '</span>',//业务表单
				labelAlign : 'right',
				value : me.qrCardData.name,
				width : 500,
				beforeLabelTextTpl : "<font color='red'> * </font>",
				margin : '20 0 0 20'
			}, 
			{
				xtype : 'panel',
				margin : '20 0 0 20',
				width : 500,
				layout : {
					type : 'hbox'
				},
				border : false,
				height : 26,
				width : '100%',
				items : [{
					xtype : 'textfield',
					readOnly:true,
					id : 'tabid',
					width:420,
					height : 24,
					labelAlign : 'right',
					beforeLabelTextTpl : "<font color='red'> * </font>",
					fieldLabel :'<span style="margin:0px 10px 0px 0px",>'+qr.associationForm+"</span>",//关联表单
					listeners : {
						afterRender : function(combo) {
							Ext.getCmp('tabid').setValue(me.qrCardData.tabName);
							Ext.getCmp('tabid').templateId = me.qrCardData.tabid;
						}
					}
				},{
     			    xtype:'button',
     			    width:80,
     			    text: qr.chooseModel,
			    	id:'select_template',
					listeners : {
						afterRender : function(combo) {
							var textfieldHeight = Ext.getCmp('tabid').getHeight();
							Ext.getCmp('select_template').setHeight(textfieldHeight);
						},
						click : {
							element : 'el',
							fn :this.selectTemplate
						}
					}
  			    }]
				
			},{
				xtype : 'textareafield',
				itemId : 'description',
				fieldLabel : '<span style="margin:0px 10px 0px 0px">'
						+ qr.businessDescription + '</span>',
				labelAlign : 'right',
				beforeLabelTextTpl : "<font color='red'> * </font>",
				value : me.qrCardData.description,
				width : 500,
				height : 100,
				margin : '18 0 0 20'// 上右下左
			}, {
				xtype : 'panel',
				itemId : 'b0110',
				margin : '20 0 0 20',
				border : false,
				height : 45,
				width : 500,
				layout : {
					type : 'hbox'
				},
				items : [{
					xtype : 'component',
					html : '<span style="margin:0px 15px 0px 42px;line-height:22px;">'
							+ qr.affiliation + '</span>'
				},{//修改获取机构组件 wangb 20190523
					xtype:'codecomboxfield',
					border: false,
		            width: me.config.clearPriv? 346:395,
		            //height: 24,
		            onlySelectCodeset: true,
		            itemId: 'organization',
		            codesetid: "UN",
		            // emptyText: kq.scheme.chooseOrg,
		            name: 'B0110',
		            ctrltype: "3",
		            editable: false,
		            allowBlank: false,
		            nmodule: "4",
		            value:b0110,
		            listeners: {
                		afterrender: function () {
                   			// this.setValue("",true); //初始化赋值
                		},
                		select: function (a, b) {
                    		// SchemeDetails.b0110 = b.get('id') + "`" + b.get('text');
                		}
            		}
		            
				},{
					xtype : 'button',
					hidden:!me.config.clearPriv,
					text:qr.clearall,//清空
					style:'margin-left:11px',
					handler:function () {
						showPanel.query('#organization')[0].setValue(qr.dafaultvalue);
					}
				}]
			}, {
				xtype : 'panel',
				margin : '0 0 0 0',
				layout : {
					pack : 'center',
					type : 'hbox'
				},
				border : false,
				height : 41,
				width : '100%',
				items : [okButton,cancelButton]
			}]
		});
		me.add(showPanel);
	}
})