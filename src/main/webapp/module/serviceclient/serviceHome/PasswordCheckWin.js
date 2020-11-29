Ext.define('ServiceClient.serviceHome.PasswordCheckWin', {
	extend:'Ext.window.Window',
	requires:["ServiceClient.serviceHome.ServiceHome","ServiceClient.serviceHome.RecoverPassword","ServiceClient.serviceHome.FirstModifyPassword"],
	id:'userPwCheckWin',
	title:'<div style="height:30px;font-size:17px;font-weight:400;margin-top:6px;margin-left:4px">'+sc.home.userPwCheck+'</div>',//用户密码校验
	style:'border-radius:7px 7px 7px 7px',
	closable:false,
	modal:true,
	resizable:false,
	draggable : false,
	width:503,
	height:200,
	tools:[{
		xtype:'image',src:'/module/serviceclient/images/index/winClose.png',height:16,width:16,
		style:'cursor:pointer;margin-right:7px',
		listeners:{
			element:'el',
			click:function(){
				VirtualKeyboard.close();
				Ext.getCmp("userPwCheckWin").close();
			},
			scope:this
		}
	}],
	constructor:function(config){
		var me = this;
		me.callParent();
		Ext.apply(me,config);
		me.y=me.logonHeight;
		me.init();
	},
	init:function(){
		var form = this.getMainPanel();
		this.add(form);
	},
	getMainPanel:function(){
		var me = this;
		Ext.util.CSS.createStyleSheet('.radius{border-radius:0px 6px 6px 0px;border-top:1px   #c5c5c5 solid;border-right:1px   #c5c5c5 solid;border-bottom:1px   #c5c5c5 solid;} ');
		var userPwCheckPanel = Ext.create("Ext.form.Panel",{
			border:false,
			width:'100%',
			height:'100%',
			items:[{
				xtype:'panel',
				margin:'27 0 0 18',
				border:false,
				layout:{
					type:'hbox',
					align:'center'
				},
				items:[{
					xtype:'image',
					src:'/module/serviceclient/images/index/password.png',
					width:42,
					height:42
				},{
					xtype : 'textfield',
					name : 'passWord',
					id:'passWord',
					emptyText :sc.home.writePw,//请输入密码
					inputType:'password',
					inputWrapCls:Ext.baseCSSPrefix + 'form-text-wrap radius',
					fieldStyle:'font-size:15px',
					width :me.forgetPwdFlag=="true"? 349:420,
					height:42,
					listeners:{
						focus:function(e){
							VirtualKeyboard.toggle('passWord-inputEl', 'softkey');
							e.emptyText = "";
							VirtualKeyboard.switchLayout("US US");
						},
						blur:function(){
							VirtualKeyboard.close();
						}
					}
				},{
					xtype:'component',
					hidden:me.forgetPwdFlag=="true"? false:true,
					style:'font-size:15px;color:#2c83ec;margin-left:10px;cursor:pointer',
					html:sc.home.forgetPw,//忘记密码
					listeners:{
						element:'el',
						click:function(){
							Ext.create('ServiceClient.serviceHome.RecoverPassword',{
								needCheckPwFlag:me.needCheckPwFlag,//是否需要密码校验
                                ip:me.ip,//终端机ip
                                logintype:2,
                                forgetPwdFlag:me.forgetPwdFlag,
                                logonHeight:me.logonHeight,
                                cardIdField:me.cardIdField,//证件指标
                                icCardField:me.icCardField,//工卡指标
                                accessFlag:me.accessFlag,//登录方式
                                hour:me.hour,
                                passwordTransEncrypt:me.passwordTransEncrypt,//密码是否需要MD5加密
								username:me.cardid,//证件号
								realUserName:me.cardIdValue//证件号真实值
							}).show();
							me.close();
						},
						scope:this
					}
				}]
			},{
				xtype:'button',//登录按钮
				height:42,
				width:110,
				margin:'18 0 0 192',
				style:'backgroundColor:#00A2FF',
				html:'<div style="color:white;font-weight:400;font-size:18px;height:40px;padding-top:12px">'+sc.home.indexLogin+'</div>',//登录
				listeners:{
					click:function(btn){
						VirtualKeyboard.close();
						btn.setDisabled(true);
						var value = userPwCheckPanel.getValues();
						var vo = new HashMap();
						vo.put('username',me.cardid);
						vo.put('logintype',2);
						vo.put('__type','byserviceclient');
						vo.put('transType','serve');
						vo.put('ip',me.ip);
						vo.put('accessFlag',me.accessFlag);
						vo.put('cardIdField',me.cardIdField);
						vo.put('icCardField',me.icCardField);
						vo.put('needInputPassword',me.needCheckPwFlag);
						vo.put('isInputPassword',true);
						//密码读取设置判断是否需要MD5加密
						if(me.passwordTransEncrypt=="true"){
							var currentHour = new Date().getHours();
							var md5jpassword = value.passWord + currentHour;
							vo.put("password","MD5`"+$.md5(md5jpassword));
						}else{
							vo.put('password',value.passWord);
						}
						Rpc({functionId:'SC000000001',async:false,success:function(res){
							var info = Ext.decode(res.responseText);
							var ipFlag = info.flag;//该ip是否已经注册
							var servicesData = info.serviceData;
							var error_message = info.logon_error;
							if(ipFlag == 0){
								Ext.Msg.alert(sc.home.tip,sc.home.notAccessFromUnRegclientTip);//不允许从未注册服务终端登录!
								btn.setDisabled(false);
								return;
							}
							if(error_message=="account"){
								btn.setDisabled(false);
								Ext.Msg.alert(sc.home.tip,sc.home.passWordError);//密码错误
							}else{
								var map = new HashMap();
								map.put("transType","needModifyPassword");
								Rpc({functionId:'SC000000001',async:false,success:function(res){
								    var info = Ext.decode(res.responseText);
								    var passwordrule = info.passwordrule;
								    var passwordlength = info.passwordlength;
								    if(info.nmpFlag){
								        Ext.create('ServiceClient.serviceHome.FirstModifyPassword',{
								            passwordrule:passwordrule,//密码强度
								            passwordlength:passwordlength,//密码长度
								            logonHeight:me.logonHeight,
								            ip:me.ip,//ip
								            accessType:"firstLogin"
                                        }).show();
								    }else{
								    	//定时
								    	ServiceClientSecurity.start();
								        Ext.getDom("banner").style.display="none";
                                        Ext.widget('viewport',{
                                            layout:'fit',
                                            items:Ext.create("ServiceClient.serviceHome.ServiceHome",{
                                                servicesData:servicesData,
                                                ip:me.ip
                                            })
                                        });
								    }
								}},map);
								me.close();
							}
						}},vo);
					}
				}
			}]
		});
		return userPwCheckPanel;
	}
});