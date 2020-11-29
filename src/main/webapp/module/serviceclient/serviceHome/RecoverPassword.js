Ext.define('ServiceClient.serviceHome.RecoverPassword',{
	extend:'Ext.window.Window',
	width:400,
	height:270,
	title:sc.home.recoverpassword,
	closable:false,
	modal:true,
	resizable:false,
	draggable : false,
	style:'border-radius:6px 6px 6px 6px;',
	initComponent:function(){
		this.callParent();
		Ext.util.CSS.createStyleSheet('.radius{border-radius:0px 6px 6px 0px;border-top:1px   #c5c5c5 solid;border-right:1px   #c5c5c5 solid;border-bottom:1px   #c5c5c5 solid;} ');
		this.createFormPanel();
	},
	createFormPanel:function(){
		var me = this;
		var formPanel = Ext.create('Ext.form.Panel',{
			width:'100%',
			border:false,
			layout:{
				type:'vbox',
				align:'center',
				pack:'center'
			},
			items:[
				{
					xtype:'container',
					width:'80%',
					height:40,
					layout:{
						type:'hbox',
						pack:'center',
						align:'middle'
					},
					items:[{
						xtype:'component',
						width:'10%',
						html:sc.home.theway,
						style:'float:left',
						margin:'0 0 0 10'
					},{
						xtype:'component',
						width:'5%',
						html:'|',
						style:'float:left',
						margin:'0 0 0 6'
					},{
						xtype:'component',
						width:'85%',
						style:'float:left',
						html:sc.home.iphonesms
					}]
				},{
					xtype:'container',
					width:'80%',
					height:42,
					layout:{//组件内容居中
						type:'hbox',
						align:'middle',
						pack:'center'
					},
					margin:'10 0 0 0',
					items:[{
						xtype:'image',
						src:'/module/serviceclient/images/index/user.png',
						width:42,
						height:42
					},{
						xtype : 'textfield',
						emptyText :sc.home.userName,//用户名
						width:'82%',
						height:42,
						value:this.realUserName,
						inputWrapCls:Ext.baseCSSPrefix + 'form-text-wrap radius',
						fieldStyle:'font-size:16px;background-color:#d9d9d9',
						readOnly:true,
						allowBlank : false
						
					},{
						xtype: 'hiddenfield',
				        name : 'Uname',
				        value: this.username
					}]
					
				},{
					xtype:'container',
					width:'80%',
					layout:{
						type:'hbox',
						align:'middle',
						pack:'center'
					},
					margin:'20 0 0 0',
					items:[
						{
							xtype:'image',
							src:'/module/serviceclient/images/index/vaildataCode.png',
							width:42,
							height:42
						},
						{
						xtype:'textfield',
						width:'51%',
						height:42,
						name:'vaildata',
						id:'vaildata',
						emptyText : sc.home.vaildata,//验证码
						inputWrapCls:Ext.baseCSSPrefix + 'form-text-wrap radius',
						itemId:'vaildata',
						allowBlank : false,
						listeners:{
							focus:function(e){
								VirtualKeyboard.toggle('vaildata-inputEl', 'softkey');
                                VirtualKeyboard.switchLayout("US US");
                            },
							blur:function(t){
								VirtualKeyboard.close();
								if(!t.getValue()){
									me.blankFlag = true
								}else{
									me.blankFlag = false;
								}
							},
							render:function(t){
								t.focus();
							}
						}
					},{
						xtype:'box',
						width:'30%',
						id:'vaildataCode',
						height:30,
						autoEl: {  
					        tag: 'img',    //指定为img标签  
				        	src:'/servlet/vaildataCode?channel=1&codelen=6'
					    },
					    listeners:{
							render:function(){
							  this.getEl().on('click',me.refresh);
							}
						}
					}]
				},{
					xtype:'container',
					width:'100%',
					margin:'20 0 0 0',
					layout:{
						type:'hbox',
						align:'middle',
						pack:'center'
					},
					items:[{
						xtype:'button',
						width:100,
						height:40,
						margin:'0 10 0 0',
						text:sc.home.send,//发送
						id:'sendButton',
						style:'text-align:center;vertical-align:middle;background-color:#00A2FF;font-size:18px',
						listeners:{
							click:function(){
								VirtualKeyboard.close();
								var values = formPanel.getValues();
								var ze = values['Uname'];
								var vaildataCode = values['vaildata'];
								var refreshFlag = false;
								if(me.blankFlag || !vaildataCode){
									Ext.Msg.alert(sc.home.tip,sc.home.vaildataBlank);
									me.refresh();
									return;
								}
								Ext.Ajax.request({
									url :'/servlet/ServiceClientGetPasswordServlet',
								    method:'Post',
								    params:{
								    	logintype: getEncodeStr('2'),     //用户登录平台 1 业务 2自助
										type: getEncodeStr('1'),         //修改密码方式  固定 1短信
										ZE: getEncodeStr(ze),         //手机号
										validatecode: getEncodeStr(vaildataCode), // 图片验证码
										validateFlag: getEncodeStr('1')   // 固定员工登录界面找回密码

								    },
									success: function(response, opts) {
								         var obj = Ext.decode(response.responseText);
								         var msg = obj.msg;
								         if(msg == "0"){//发送成功
								        	me.close();
								        	Ext.Msg.alert(sc.home.tip,sc.home.sendSucceess,function(){//密码已成功发送到您的手机,请输入密码进行登录
								        	   var PasswordCheckWin = me.createPasswordCheckWin();
								        	   PasswordCheckWin.show();
								        	});
								         }else if(msg == "2"){
								        	 Ext.Msg.alert(sc.home.tip,sc.home.smsservertip);//短信服务器配置不成功,未能成功发送短信!
								        	 refreshFlag = true;
								         }else if(msg == "10"){//验证码错误
								        	 Ext.Msg.alert(sc.home.tip,sc.home.validatecodeErrorTip);
								        	 refreshFlag = true;
								         }else if(msg=="3"){//系统未设置移动电话指标，无法找回密码!
								        	 Ext.Msg.alert(sc.home.tip,sc.home.noMobilePhoneNumField);
								        	 refreshFlag = true;
								         }else if(msg=="5"){//系统中无您的移动电话号码，无法找回密码!
								         	Ext.Msg.alert(sc.home.tip,sc.home.noMobilePhoneNum);
								         	refreshFlag = true;
								         }else if(msg=="6"){
								         	Ext.Msg.alert(sc.home.tip,sc.home.mobilePhoneNumError);
								         	refreshFlag = true;
								         }
										 if(refreshFlag){
											me.refresh();
										 }
								     }
//								     failure: function(response, opts) {
//								         console.log('server-side failure with status code ' + response.status);
//								     }
								 });
								
							}
						}
					},{
						xtype:'button',
						width:100,
						height:40,
						text:sc.home.close,//关闭
						id:'closeBUtton',
						margin:'0 0 0 10',
						style:'text-align:center;vertical-align:middle;background-color:#D1D1D1;',
						listeners:{
							click:function(t){
								VirtualKeyboard.close();
								me.close();
							}
						}
					}]
				}
			]

			
		});
		me.add(formPanel);
	},
	refresh:function(){
		var url = document.getElementById('vaildataCode').src;
		document.getElementById('vaildataCode').src = url+"&id=" + Math.random();
	},
	/**
	 * 重置密码后校验密码
	 * @return {}校验密码窗口
	 */
	createPasswordCheckWin:function(){
		var me= this;
	    var PasswordCheckWin = Ext.create('ServiceClient.serviceHome.PasswordCheckWin',{
            needCheckPwFlag:me.needCheckPwFlag,//是否需要密码校验
            ip:me.ip,//终端机ip
            cardid:me.username,//证件号
            logintype:2,
            forgetPwdFlag:me.forgetPwdFlag,
            logonHeight:me.logonHeight,
            cardIdValue:me.realUserName,//证件号真实值
            cardIdField:me.cardIdField,//证件指标
            icCardField:me.icCardField,//工卡指标
            accessFlag:me.accessFlag,//登录方式
            hour:me.hour,
            passwordTransEncrypt:me.passwordTransEncrypt//密码是否需要MD5加密
        });
        return PasswordCheckWin;
	}
});