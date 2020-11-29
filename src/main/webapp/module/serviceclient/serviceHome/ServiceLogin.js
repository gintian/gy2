Ext.define("ServiceClient.serviceHome.ServiceLogin", {
	extend : 'Ext.window.Window',
	requires:["ServiceClient.serviceHome.ServiceHome","ServiceClient.serviceHome.FirstModifyPassword"],
	width : 470,
	height :310,
	margin:'0 0 20 0',
	logonHeight:'',
	id:'ServiceLogin',
	resizable:false,
	closable:false,//隐藏关闭按钮
	header: false,//隐藏标题栏
	ip:'',
	style:'border-radius:7px 7px 7px 7px',
	constructor : function(config) {
		this.callParent();//一定要调用！！！！！！！！！！！！！！！！
		this.ip = config.ip;
		Ext.util.CSS.createStyleSheet('.radius{border-radius:0px 6px 6px 0px;border-top:1px   #c5c5c5 solid;border-right:1px   #c5c5c5 solid;border-bottom:1px   #c5c5c5 solid;} ');
		this.isValidateCode = config.isValidateCode;
		this.y=config.logonHeight;
		this.hour = config.hour;
		this.passwordTransEncrypt = config.passwordTransEncrypt;
		if(this.isValidateCode){
			this.setHeight(360);
		}
		this.init();
	},
	init : function() {
		var form = this.getMainPanel();
		this.add(form);
	},
	getMainPanel : function() {
		var me = this;
		var panel =Ext.create('Ext.form.Panel',{
			width : '100%',
			height : '100%',
			layout :{
				type:'vbox',
				align:'center'
			},
			border:false,
			items : [ {
					xtype:'container',
					width:'100%',
					items:[{
						xtype:'image',
						style:'float:right;margin-top:8px;margin-right:7px',
						src:'/module/serviceclient/images/index/winClose.png',
						width:16,
						height:16,
						listeners:{
							click:{
								element:'el',
								fn:function(){
									VirtualKeyboard.close();
									Ext.getCmp('ServiceLogin').destroy();
								},
								scope:me
							}
						}
					}]
			}, {
				xtype:'container',
				width:'90%',
				height:42,
				layout:{//组件内容居中
					type:'hbox',
					align:'middle',
					pack:'center'
				},
				margin:'34 0 0 0',
				items:[{
					xtype:'image',
					src:'/module/serviceclient/images/index/user.png',
					width:42,
					height:42
				},{
					xtype : 'textfield',
					name : 'userName',
					id:'uName',
					emptyText :sc.home.userName,//用户名
					width:'82%',
					height:42,
					inputWrapCls:Ext.baseCSSPrefix + 'form-text-wrap radius',
					fieldStyle:'font-size:16px',
					allowBlank : false,
					listeners:{
						focus:function(e){
//							window.$write = document.getElementsByName("userName")[0];
							VirtualKeyboard.toggle('uName-inputEl', 'softkey');
							$("#kb_langselector,#kb_mappingselector,#copyrights").css("display", "none");
							e.emptyText = "";
						},
						blur:function(){
							VirtualKeyboard.close();
						}
					}
				}]
				
			},{
				xtype:'container',
				width:'90%',
				height:42,
				layout:{//组件内容居中
					type:'hbox',
					align:'middle',
					pack:'center'
				},
				margin:'30 0 0 0',
				items:[{
					xtype:'image',
					src:'/module/serviceclient/images/index/password.png',
					width:42,
					height:42
				},{
					xtype : 'textfield',
					name : 'passWord',
					id:'passWord',
					emptyText :sc.home.passWord,//密码
					inputType:'password',
					inputWrapCls:Ext.baseCSSPrefix + 'form-text-wrap radius',
					fieldStyle:'font-size:16px',
					width : '82%',
					height:42,
					listeners:{
						focus:function(e){
//							window.$write = document.getElementsByName("passWord")[0];
							VirtualKeyboard.toggle('passWord-inputEl', 'softkey');
							$("#kb_langselector,#kb_mappingselector,#copyrights").css("display", "none");
							e.emptyText = "";
							VirtualKeyboard.switchLayout("US US");
						},
						blur:function(){
							VirtualKeyboard.close();
						}
					}
				}]
			}, {
				xtype:'container',
				layout:{//组件内容居中
					type:'hbox',
					align:'middle',
					pack:'center'
				},
				width:'90%',
				margin:'30 0 0 0',
				hidden:this.isValidateCode?false:true,
				items:[
					{
						xtype:'image',
						src:'/module/serviceclient/images/index/vaildataCode.png',
						width:42,
						height:42
					},
					{
						xtype : 'textfield',
						name : 'vaildata',
						id:'vaildata',
						emptyText : sc.home.vaildata,//验证码
						width : '50%',
						height:42,
						allowBlank : false,
						inputWrapCls:Ext.baseCSSPrefix + 'form-text-wrap radius',
						margin:'0 20 0 0',
						listeners:{
							focus:function(){
//								window.$write = document.getElementsByName("vaildata")[0];
								VirtualKeyboard.toggle('vaildata-inputEl', 'softkey');
								$("#kb_langselector,#kb_mappingselector,#copyrights").css("display", "none");
								VirtualKeyboard.switchLayout("US US");
							},blur:function(){
								VirtualKeyboard.close();
							}
						}
					},
					{
						xtype:'box',
						id:'vaildataCode',
						width:'30%',
						height:42,
						autoEl: {  
					        tag: 'img',//指定为img标签  
				        	src:'/servlet/vaildataCode?channel=1'
					    },
					    listeners:{
							render:function(){
							  this.getEl().on('click',me.refresh);
							}
						}
					}
				]
			},{
				xtype:'button',
				id:'serviceButton',
				width:'85%',
				height:50,
				margin:this.isValidateCode?'30 0 0 0':'50 0 0 0',
				text:sc.home.login,//登录
				style:'text-align:center;vertical-align:middle;background-color:#00A2FF;',
				listeners:{
					click:function(btn){
						VirtualKeyboard.close();
						btn.setDisabled(true);
						var value = panel.getForm().getValues();
						//密码读取设置判断是否需要MD5加密
                        if(me.passwordTransEncrypt=="true"){
							var currentHour = new Date().getHours();
                            var md5jpassword = value.passWord+currentHour;
                            var finallyPassword = "MD5`"+$.md5(md5jpassword);
                        }else{
                        	var finallyPassword = value.passWord;
                        }
                        var vo = new HashMap();
						vo.put("ip",me.ip);
						vo.put('logintype',1);
						vo.put("__type","byserviceclient");
						vo.put("username",value.userName);
                        vo.put("password",finallyPassword);
						vo.put("transType","needModifyPassword");
						vo.put("validatecode",value.vaildata);
                        Rpc({functionId:'SC000000001',async:false,success:function(res){
                            var info = Ext.decode(res.responseText);
                            var passwordrule = info.passwordrule;//密码强度
                            var passwordlength = info.passwordlength;//密码长度
                            if(info.nmpFlag){
                                Ext.getCmp("ServiceLogin").close();
                                Ext.create('ServiceClient.serviceHome.FirstModifyPassword',{
                                    ip:me.ip,
                                    passwordrule:passwordrule,
                                    logonHeight:me.y,
                                    passwordlength:passwordlength,
                                    accessType:"firstLogin"
                                }).show();
                            }else{
                                //设置参数
                                var paramMap = new HashMap();
                                paramMap.put("ip",me.ip);
                                paramMap.put('logintype',1);
                                paramMap.put("__type","byserviceclient");
                                paramMap.put("username",value.userName);
                                paramMap.put("password",finallyPassword);
                                paramMap.put("transType","serve");
                                paramMap.put("validatecode",value.vaildata);
                                
                                Rpc({functionId:'SC000000001',success:function(resp){
                                    var resultObj = Ext.decode(resp.responseText);
                                    var returnFlag = resultObj.flag;
                                    var servicesData = resultObj.serviceData;
                                    if(returnFlag == 0){
                                        Ext.Msg.alert(sc.home.tip, sc.home.notAccessFromUnRegclientTip);
                                        btn.setDisabled(false);
                                        return;
                                    }else{
                                        var servicesData = resultObj.serviceData;
                                        var error_message = resultObj.logon_error;
                                        var validatecode = value.vaildata;
                                        var username = value.userName;
                                        var log_noAcess = resultObj.log_noAcess;
                                        var flag = true;
                                        var errorTipMessage='';
                                        if(me.isValidateCode&&validatecode.trim()==''){
                                        	 errorTipMessage = sc.home.validatecodeBlankTip;
                                             //Ext.Msg.alert(sc.home.tip,sc.home.validatecodeBlankTip);//请输入验证码
                                             flag = false;
                                        }
                                        if(error_message=="account"){
                                        	errorTipMessage = sc.home.accountErrorTip;//用户名或密码错误
                                            flag = false;
                                        }else if(error_message=="thirdError") {//第三方认证失败
											errorTipMessage = sc.home.accountErrorTip;
											flag = false;
										}else if(error_message=="code"){
                                        	errorTipMessage = sc.home.validatecodeErrorTip;//校验码不正确
                                            flag = false;
                                        }
                                        if(log_noAcess){//登录被拒绝,使用的是非自助用户登录
                                        	errorTipMessage = sc.home.log_noAcessTip;//请使用自助用户登录系统
                                            flag = false;
                                        }
                                        if(!flag){//当登录不成功时 
                                        	 Ext.Msg.alert(sc.home.tip,errorTipMessage,function(){
                                                 btn.setDisabled(false);
                                                 me.refresh();
                                        	 });
                                             return;
                                        }
                                        //定时
                                        ServiceClientSecurity.start();
                                        Ext.getCmp("ServiceLogin").destroy();
                                        Ext.getDom("banner").style.display="none";
                                        Ext.widget('viewport',{
                                            layout:'fit',
                                            items:Ext.create("ServiceClient.serviceHome.ServiceHome",{
                                                servicesData:servicesData,
                                                ip:me.ip
                                            })
                                        });
                                    }
                                },method:'post'},paramMap);
                            }
                        }},vo);
					},
					disable:function(){
						Ext.getDom('serviceButton').style.background = '#a38f8f';
					},
					enable:function(){
						Ext.getDom('serviceButton').style.background = '#0099FF';
					}
				}
			}],
			listeners:{
				
				/*render:function(btn){
					document.getElementById("container").style.display="";
					
				}*/
			}
		});
		return panel
	},
	/**
	 * 声明登录验证回调方法
	 */
	refresh:function(){
		var url = document.getElementById('vaildataCode').src;
		document.getElementById('vaildataCode').src = url+"&id=" + Math.random();
	}
});
