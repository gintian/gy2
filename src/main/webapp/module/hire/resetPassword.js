Ext.define('ResetPasswordUL.resetPassword',{
	me:'',
	imagePrefix:rootPath+'/module/hire/',
	constructor:function(config) {
		me = this;
		me.emailname = config.emailName;
		me.flag = config.flag;
		me.guidkey = "";
		this.init();
	},

	// 初始化函数
	init:function() {
		var item=me.returnItems();
		var leftPanel = Ext.create("Ext.Container",{
    		flex:1,
    		width:990,
    		height:600,
    		id:'leftPanel',
    		cls:'bh-div',
    		renderTo: Ext.get('aa'),
    		items:[{xtype:'container',
    			layout:{
    				type:'hbox',
    				align:'middle'
    			},
        		padding:'0 0 5 10',
        		width:900,
        		height:70,
        		defaults:{
        			autoEl:'div'
        		},
        		items:[{xtype:'box',id:'id1',width:41,height:41,cls:'select-background',html:'<div id="div1" class="select">1</div>'},
        		       {xtype:'image',border:2,width:220,src:me.imagePrefix+'images/pic1.png'},
        		       {xtype:'box',id:'id2',width:41,height:41,cls:'unselect-background',html:'<div id="div2" class="unselect">2</div>'},
               			{xtype:'image',width:220,src:me.imagePrefix+'images/pic1.png'},
        		       {xtype:'box',id:'id3',width:41,height:41,cls:'unselect-background',html:'<div id="div3" class="unselect">3</div>'},
               			{xtype:'image',width:220,src:me.imagePrefix+'images/pic1.png'},
        		       {xtype:'box',id:'id4',width:41,height:41,cls:'unselect-background',html:'<div id="div4" class="unselect">4</div>'}]
        	},{xtype:'container',
    			layout:{
    				type:'hbox',
    				align:'middle'
    			},
    			defaults:{
    				xtype:'label',
    				cls:'info'    				
    			},
    			items:[{margin:'0 0 0 10',text:'填写账户名'},
    			       {margin:'0 0 0 205',text:'验证身份'},
    			       {margin:'0 0 0 210',text:'设置新密码'},
    			       {margin:'0 0 0 220',text:'完成'}]
    		},item]
    	});
		
		if("1" == me.flag) {
			Ext.Ajax.request({
				url: '/servlet/AboutAccountServlet',
				async:false,
				method:'post',
				success: me.authentication
			});
		}
			
	},
	returnItems:function(){
		var item = "";
		var url = window.location.href;
		if(url.toLowerCase().indexOf("emailname")!=-1 && url.toLowerCase().indexOf("active")!=-1){
			var email = url.split("&emailName=")[1];
			me.emailname = email.split("&active=")[0];
			var active = url.split("&active=")[1];
			var map = new HashMap();
			map.put("emailName",me.emailname);
			map.put("active",active);
			Rpc({functionId:'ZP0000002655',async:false,
				success:function(data){
				var info = Ext.decode(data.responseText).info;
				if(info == "ok"){
					me.guidkey = Ext.decode(data.responseText).guidkey;
					item = me.setNewPwd();
				}else{
					Ext.Msg.alert('提示信息',info+"！");
					item = me.editEmailAddress();
				}
			}},map);
		}else{
			item = me.editEmailAddress();
		}
		return item;
	},
	editEmailAddress:function(){
		var item = Ext.widget('container',{
			width:'90%',
			id:'containerId',
			layout:'vbox',
			items:[
				{xtype:'textfield',
					fieldLabel:'<font class="email-code-font">邮箱名称</font>',
					labelSeparator:null,
					labelWidth:80,
					width:400,
					emptyText:'请输入邮箱',
					id:'emailId',
					name:'emailId',
					height:30,
					cls:'email-input'
				},{xtype:'container',
		    		width:700,
		    		height:50,
		    		layout:'hbox',
		    		cls:'code-input',
		    		items:[{
		    			xtype:'textfield',
		    			fieldLabel:'<font class="email-code-font">验&nbsp;&nbsp;证&nbsp;&nbsp;码</font>',
		    			id:'codeId',
		    			labelSeparator:null,
		    			labelWidth:80,
		    			width:400,
		    			emptyText:'请输入验证码',
		    			name:'codeId',
		    			height:30,
		    			margin:'0 0 15 0'
					},{
						xtype:'box',
						margin:'5 0 0 10',
						id:'vaildataCode',
						width:100,
						height:30,
						autoEl: {  
					        tag: 'img',    //指定为img标签  
				        	src:'/servlet/vaildataCode?out=true&channel=0&codelen=4'
					    },
					    listeners:{
							render:function(){
							  this.getEl().on('click',me.refresh);
							}
						}
					},{
						xtype: 'label',
				        forId: 'myFieldId',
				        text: '点击图片刷新验证码',
				        cls:'myField'
					}]
				},{xtype:'button',
					id:'submit',
					border:false,
					cls:'button',
					iconCls: 'submit-icon',
					handler:me.sendEmail
				}]
		});
		return item;
	},
	//发送邮件
	sendEmail:function(){
		var emailName = Ext.getCmp('emailId').value;
		var codeId = Ext.getCmp('codeId').value;
		var data = "";
		if(trim(emailName).length<=0)
		{
			Ext.showAlert("注册邮箱输入不能为空！");
			me.refresh();
			return;
		}
		var mm=/^([a-zA-Z0-9]+[_|\_|\.]?)*[a-zA-Z0-9]+@([a-zA-Z0-9]+[_|\_|\.]?)*[a-zA-Z0-9]+\.[a-zA-Z]{2,3}$/;
		if(!mm.test(emailName))
		{
			Ext.showAlert("请输入正确的邮箱地址！");
			me.refresh();
			return;
		}
		
		if(trim(codeId).length<=0)
		{
			Ext.showAlert("验证码不能为空！");
			me.refresh();
			return;
		}
		
		Ext.Ajax.request({
	        url: '/servlet/AboutAccountServlet',
	        async:false,
	        method:'post',
	        params: {
	        	emailName:emailName,
	        	codeValue:codeId
	        },
	        success: me.authentication
	    });
	},
	//发送邮件完成
	authentication:function(obj){
		me.refresh();
		var result = Ext.decode(obj.responseText);
		var flag = result.flag;
		if("success"!=flag){
			if("validatecode-error"==flag){
				Ext.showAlert("验证码错误！");
			}else if("no_account"==flag){
				Ext.showAlert("用户名不存在！");
			}else{
				Ext.showAlert("服务器异常，请联系管理员！");
			}
			return;
		}else{
			Ext.getCmp('id1').removeCls('select-background');
			Ext.getCmp('id1').addCls('unselect-background');
			Ext.getDom('div1').className='unselect';
			Ext.getCmp('id2').removeCls('unselect-background');
			Ext.getCmp('id2').addCls('select-background');
			Ext.getDom('div2').className='select';
			Ext.getCmp('leftPanel').remove(Ext.getCmp('containerId'));
			Ext.getCmp('leftPanel').add(me.goEmail(result.address));
		}
	},
	//刷新验证码
	refresh:function(){
		var url = document.getElementById('vaildataCode').src;
		document.getElementById('vaildataCode').src = url+"&id=" + Math.random();
	},
	goEmail:function(address){
		var item = Ext.widget('container',{
			id:'goemail',
			width:900,
			cls:'goemail',
			layout: 'absolute',
			items:[{xtype:'label',x:100,y:25,
	            	html:'<font class="bh-wzm-yzlj">验证链接已发送到您的邮箱，请点击链接，设置新密码</font>'
				}]
		});
		if(address!=""){
			var goButton = Ext.widget('container',{
					id:'goemail',
					width:800,
					cls:'goButton',
					x:50,y:100,
					layout: 'absolute',
					items:[{xtype:'button',
					id:'button',
					x:160,
					y:60,
					border:false,
					iconCls: 'gomail-icon',
					handler:function(){window.location.href=address;}
					}]
				});
			Ext.getCmp('leftPanel').add(goButton);
		}
		return item;
	},
	//设置新密码
	setNewPwd:function(){
		var item = Ext.widget('container',{
			id:'setnewpwd',
			width:800,
			//height:400,
			layout: 'absolute',
			items:[
				{xtype:'textfield',
					fieldLabel:'<font class="email-code-font">新登录密码&nbsp;&nbsp;</font>',
					labelSeparator:null,
					labelWidth:95,
					width:400,
					name:'pw0',
					height:52,
					id:'pw0',
					x:-30,
					y:30,
					cls:'email-input',
					inputType:"password"
				},{xtype:'textfield',
					fieldLabel:'<font class="email-code-font">确认新密码&nbsp;&nbsp;</font>',
					labelSeparator:null,
					labelWidth:95,
					width:400,
					name:'pw1',
					height:52,
					id:'pw1',
					x:-30,
					y:140,
					cls:'code-input',
					inputType:"password"
				},{xtype:'button',
					id:'setpwdbutton',
					border:false,
					cls:'button',
					x:-30,
					y:230,
					iconCls: 'submit-icon',
					style:'margin:30px 0 0 240px',
					listeners: {
						click:{
							element: 'el', 
				            fn: function(){
										var pw0 = Ext.getCmp('pw0').value;
										var pw1 = Ext.getCmp('pw1').value;
										if(pw0=="" || pw1=="" || pw0==null || pw1==null){
											Ext.Msg.alert('提示信息',"输入密码不能为空，请重新输入！");
											return;
										}
										if(pw0 != pw1){
											Ext.Msg.alert('提示信息',"输入密码不一致，请重新输入！");
											return;
										}
										var map = new HashMap();
										map.put("pw0",pw0);
										map.put("pw1",pw1);
										map.put("email", me.emailname);
										map.put("guidkey", me.guidkey);
										Rpc({functionId:'ZP0000002654',async:false,
											success:function(data){
											msg = Ext.decode(data.responseText).info;
											if(msg == "ok"){
												Ext.getCmp('id3').removeCls('select-background');
												Ext.getCmp('id3').addCls('unselect-background');
												Ext.getDom('div3').className='unselect';
												Ext.getCmp('id4').removeCls('unselect-background');
												Ext.getCmp('id4').addCls('select-background');
												Ext.getDom('div4').className='select';
												Ext.getCmp('leftPanel').remove(Ext.getCmp('setnewpwd'));
												Ext.getCmp('leftPanel').add(me.finish());
											}else{
												Ext.Msg.alert('提示信息',msg+"！");
												return;
											}
										}},map);
								}
						}
					}
				}],
				listeners:{
	            	   afterrender:function(){
							Ext.getCmp('id1').removeCls('select-background');
							Ext.getCmp('id1').addCls('unselect-background');
							Ext.getDom('div1').className='unselect';
							Ext.getCmp('id3').removeCls('unselect-background');
							Ext.getCmp('id3').addCls('select-background');
							Ext.getDom('div3').className='select';
            		}
            	}
		});
		return item;
	},
	//新密码设置完成
	finish:function(){
		var item = Ext.widget('container',{
			id:'finish',
			width:800,
			padding:'145 0 0 155',
			layout:{
				type:'hbox',
				align:'middle'
			},
			items:[
			       {xtype:'image',width:50,src:this.imagePrefix+'images/pic5.png'},
			       {xtype:'panel',
			    	   border:0,
			    	   layout: {
				           type: 'vbox',
				           align: 'left'
				       },
				       items: [{
				    	   		xtype:'label',
				            	html:'<font class="finish-text">新密码设置成功！</font>',
				               },{
					           xtype:'label',
					           html:'<font class="finish-info">请牢记新密码，系统将在</font>'
					        	   +'<span id="ss" class="finish-secs">5</span>'
					        	   +'<font class="finish-info">秒后返回首页！</font>'
					        	   +'<a id="isreturn" href=""><font class="finish-retu">立即返回</font></a>&nbsp;&nbsp;'
				             }]
			       }],
		       listeners:{
	            	   afterrender:function(){
							Ext.get('isreturn').dom.href="/hire/hireNetPortal/search_zp_position.do?b_query=link&operate=init";
	            	   		me.countDown(5,"/hire/hireNetPortal/search_zp_position.do?b_query=link&operate=init");
               		}
               	}
		});
		return item;
	},
	//5秒倒计时
	countDown:function countDown(secs,surl){    
		 Ext.get('ss').dom.innerHTML=secs;
		 if(--secs>0){     
			 setTimeout("me.countDown("+secs+",'"+surl+"')",1000);
		     }     
		 else{       
		     location.href=surl;     
		     }
	}
});
