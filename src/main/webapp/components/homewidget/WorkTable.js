Ext.define("EHR.homewidget.WorkTable",{
	extend:'Ext.panel.Panel',
	xtype:'worktable',
	collapsible:true,
	titleCollapse:true,
//	id:"workTableId",
	animCollapse: true, 
	//标题添加图片 wangb 20170815 30291
	title:'<div style="font-size:14px"><div style="float:left;width:16px;height:16px;margin:0px 3px 0px 1px;background:url(/images/hcm/themes/default/icon/icon_business.png) no-repeat 0 0;"></div>工作桌面</div>',
	minHeight:100,
	bodyPadding:'0 10 10 0',
	margin:'10 10 0 10',
	style:'background:white',
	menuIconUrl:'/images/worktableicon/',
	maxCount:7,
	config:{
		//settingModel: 配置模式，将禁用菜单鼠标点击事件（不跳转）
		settingModel:false,
		//objectid ： 方案所属对象
		objectid:'',
		//objecttype：对象类型
		objecttype:'',
		//menuinfo：菜单数据
		menuinfo:'',
	},
	initComponent:function(){
		this.callParent();
		this.loadLinks();
	},
	loadLinks:function(){
		var map = new HashMap();
		map.put("objectid",this.getObjectid());
		map.put("objecttype",this.getObjecttype());
		Rpc({functionId:'ZJ100000161',async:true,success:this.renderLinks,scope:this},map);
	},
	renderLinks:function(result){
	    var me = this,
	        menuArray = Ext.decode(result.responseText).menus,
	        htmlStr = "";
//	    this.userName= Ext.decode(result.responseText).userName;
	    if(Ext.decode(result.responseText).isHidden=='true'){
	    	this.destroy();
	    	return;
	    }
	        me.menuCacheCollection = {};
	    this.userArray=[];
	    for(var i=0;i<menuArray.length;i++){
	    		var menuInfo = menuArray[i];
	    		if(menuInfo.objecttype==this.getObjecttype())
	    			this.userArray.push(menuInfo);
	    		me.menuCacheCollection[menuInfo.menuid] = menuInfo;
	    		//每条记录的标识（"role"或""） 
	    		htmlStr +='<table cmptype="menu" menuid="'+menuInfo.menuid+'" objecttype="'+menuInfo.objecttype+'" border=0 width=145 height=75 cellpadding=0 cellspacing=0 style="cursor:pointer;background:#FFFFFF;float:left;margin:10px 0px 0px 10px;">'+
	            			  '<tr>'+
	            			      '<td rowspan=3 width=46 align=right><img style="width:36px;height:36px;" src="'+me.menuIconUrl+menuInfo.qicon+'"/></td>'+
	            			      '<td height=20 >'+
	            			      	'<img functype="del" src="/components/homewidget/images/del.png" style="display:none;position:relative;top:-8px;left:8px;width:18px;height:18px;float:right;">'+
	            			      '</td>'+
	            			  '</tr>'+
	            			  '<tr>'+
	            				  '<td valign=top height=36 align=left  style="padding-left:5px;font-size:14px;"><div style="width:84px;height:36px;line-height:18px;overflow:hidden;text-overflow:ellipsis;" title="'+menuInfo.name+'">'+menuInfo.name+'</div></td>'+
	            			  '</tr>'+
	            			  '<tr>'+
	            				  '<td valign=top align=left style="font-size:12px;">'+
	            				  	'<div '+(menuInfo.desc.length>0?'title='+menuInfo.desc:'')+' style="width:80px;overflow:hidden;text-overflow:ellipsis;white-space:nowrap;">'+
	            				  		menuInfo.desc+
	            				  	'</div>'+
	            				  '</td>'+
	            			  '</tr>'+
	            			'</table>';
	    }
	    var display = "block";
	    if(this.userArray.length>=me.maxCount)
	       display = "none";
	    htmlStr+='<table cmptype="add" objecttype="'+this.getObjecttype()+'" height=75 width=75 style="display:'+display+';cursor:pointer;float:left;margin:10px 0px 0px 10px;"><tr><td align=center height=75 width=75><img src="/images/new_module/nocycleadd.png"/></td></tr></table>'
	    
	    this.update(htmlStr);
	    
	    this.addListener({
	    		delegate:'table[cmptype]',
	        	element:'body',
	        	scope:me,
	        	click:function(evt,menuTable){
	        		var me = this,
	        		menuid = menuTable.getAttribute("menuid");
	        		
	        	  	if(evt.target.getAttribute('funcType')=='del'){
		            Ext.Msg.show({
                        title:"<div style='margin-left:0px'>提示信息</div>",
                        msg: "确认删除所选功能？",
                        buttons: Ext.Msg.YESNO,
                        buttonText: {
        						yes: '确定',
        						no: '取消'
    						},
                        fn: function(btn){
                           if(btn!='yes')
                               return;
                           me.deleteMenu(menuTable);        
                        },
                        icon: Ext.MessageBox.QUESTION
                    });
		            if(!Ext.isIE){
		            	var messageboxObj = document.getElementById("messagebox-1001").style="height: 128px; right: auto; left: 501px; top: 211px; width: 250px; z-index: 19001;";
			            var ext_gen = document.getElementsByClassName("x-css-shadow")[0];
			            if(ext_gen)
			            	ext_gen.style="display: none;box-shadow:none;";
		            }
		            return;
	        	  	}
	        	  	if(menuTable.getAttribute("cmptype")=='menu'){
	        	  		//settingModel 为点击跳转链接属性  
	        	  		if(this.getSettingModel())
	        	  			return;
	        	  			
	        	  		if(me.menuCacheCollection[menuid].bevalidate=='0'){
	        	  			var url = me.menuCacheCollection[menuid].url;
	        	  			window.location.href=url;
	        	  			return;
	        	  		}
	        	  		if(me.menuCacheCollection[menuid].validatetype=='1'){
	        	  			me.checkPassword(menuid);
	        	  		}else if(me.menuCacheCollection[menuid].validatetype=='2'){
	        	  			me.checkCode(menuid);
	        	  		}else if(me.menuCacheCollection[menuid].validatetype=='1,2'){
	        	  			me.checkCode(menuid,true);
	        	  		}
	        	  		return;
	        	  	}
	        	  		
	        	  	me.openMenuTree();
	        	     
	        	},
	        
	        mouseover:function(evt,menuTable){
	        		if(menuTable.getAttribute("cmptype")=='add')
	        			return;
	        		menuTable.style.background ="url(\"/images/worktableicon/worktable-bg.png\") no-repeat 0px -1px";
	        		menuTable.style.backgroundSize ="100% 100%";
	        		//删除图标权限判断
	        		if(menuTable.getAttribute("objecttype")==me.getObjecttype()||"role"==me.getObjecttype()){
	        			menuTable.getElementsByTagName('img')[1].style.display="block";
	        		}
	        },
	        mouseout:function(evt,menuTable){
	        		if(menuTable.getAttribute("cmptype")=='add')
	        			return;
	        		menuTable.style.background = "#FFFFFF";
	        		menuTable.getElementsByTagName('img')[1].style.display="none";
	        	}
	    });
	},
	checkPassword:function(menuid){
		var me = this;
    	Ext.widget("window",{
    	  			title:'安全验证',
    	  			width:300,
    	  			height:200,
    	  			modal:true,
    	  			resizable:false,
    	  			layout:'fit',
    	  			items:{
    	  				xtype:'container',
    	  				padding:'16 0 0 50',
    	  				items:[{
	    	  				xtype:'label',
	    	  				text:'请输入您的密码：'
	    	  			},{
	    	  				xtype:'textfield',margin:'10 0 0 0',width:200,inputType:'password'
	    	  			},{
	    	  				xtype:'component',margin:'10 0 0 0',width:200,
	    	  				html:'提示：请输入您的登录密码，校验认证通过以后，才能进入该模块。'
	    	  			}]
    	  			},
    	  			buttonAlign:'center',
    	  			buttons:[{text:'确定',handler:function(){
    	  				var password = this.up('window').query('textfield')[0].getValue();
    	  				var vo = new HashMap();
    	  				vo.put("password",password);
    	  				vo.put("transType","pwdcheck");
    	  				Rpc({functionId:'0202011021',success:function(response){
    	  					var backparam = Ext.decode(response.responseText);
    	  					if(backparam.result){
    	  						var url = me.menuCacheCollection[menuid].url;
	        	  				window.location.href=url;
    	  					}else{
    	  						alert("密码输入不正确！");
    	  					}
    	  					
    	  				}},vo);
    	  			}}]
    	}).show();
    },
    checkCode:function(menuid,checkPassword){
    	var me = this;
    	var phone;
    	var delaytime;
    	var vo = new HashMap();
		vo.put("transType","getPhone");
    	Rpc({functionId:'0202011021',async:false,success:function(response){
    		var backparam = Ext.decode(response.responseText);
    	  	if(backparam.result){
    	  		phone = backparam.phone;
    	  		delaytime = backparam.delaytime;
    	  	}else{
    	  		alert(backparam.msg);
    	  	}
    	}},vo);
    
    	if(!phone || phone.length<1)
    		return;
    	Ext.widget("window",{
    	  			title:'安全验证',
    	  			width:400,
    	  			height:checkPassword?350:300,
    	  			modal:true,
    	  			resizable:false,
    	  			layout:'fit',
    	  			items:{
    	  				xtype:'container',
    	  				padding:'16 0 0 20',
    	  				items:[{
	    	  				xtype:'label',
	    	  				text:'手机号码: '+phone
	    	  			},{
	    	  				xtype:'container',layout:'hbox',
	    	  				items:[{
	    	  					xtype:'textfield',itemId:'codebox',margin:'10 0 0 0',fieldLabel:'验证码',labelWidth:56,width:120,labelAlign:'right'
	    	  				},{
	    	  					xtype:'button',width:70,margin:'10 0 0 10',text:'发送验证码',
	    	  					handler:function(btn){
	    	  						//发送验证码
	    	  						var vo = new HashMap();
	    	  						vo.put("phoneNumber",getEncodeStr(phone));
	    	  						Rpc({functionId:'0202011020',success:function(response){
			    	  					var backparam = Ext.decode(response.responseText);
			    	  					//发送失败，提示
			    	  					if(backparam.error==1)
										{
											alert(backparam.content);
											return;
										}
										
										var codebox = btn.ownerCt.child('#codebox');
										codebox.realcode = backparam.content;
										//发送成功，隐藏发送按钮，显示倒计时
			    	  					btn.setText('重新发送');
			    	  					btn.setVisible(false);
			    	  					var timerBox = btn.ownerCt.child('#timer');
			    	  					timerBox.setVisible(true);
			    	  					timerBox.update(delaytime+'s');
			    	  					timerBox.delaytime = delaytime;
			    	  					//开始倒计时
			    	  					var forcode = setInterval(function(){
			    	  						timerBox.delaytime--;
			    	  						timerBox.update(timerBox.delaytime+'s');
			    	  						//当倒计时为0时，隐藏倒计时，显示重新发送按钮
			    	  						if(timerBox.delaytime==0){
			    	  							window.clearInterval(forcode);
			    	  							btn.setVisible(true);
			    	  							timerBox.setVisible(false);
			    	  						}
			    	  					},1000);
			    	  				}},vo);
	    	  					}
	    	  				},{
	    	  					xtype:'box',margin:'14 0 0 10',hidden:true,itemId:'timer'
	    	  				}]
	    	  			},
	    	  			checkPassword?{xtype:'textfield',inputType:'password',itemId:'pwdbox',margin:'10 0 0 0',fieldLabel:'密码',labelWidth:56,width:200,labelAlign:'right'}:undefined,
	    	  			{
	    	  				xtype:'component',margin:'10 0 0 0',width:360,
	    	  				html:'<table booder="0"><tr><td align="left"" Colspan="2" >提示：</td></tr>'+
					           '<tr> <td align="right">&nbsp;&nbsp;&nbsp;&nbsp;</td><td  align="left">1.请确认上述手机号码是否为接收短信验证码的正确号码。如有误，请不要点击“获取验证码”，并尽快联系您的人力资源主管进行信息更正。</td></tr>'+
					           '<tr><td align="right">&nbsp;&nbsp;&nbsp;&nbsp;</td><td align="left">2.点击获取验证码后，短信可能由于网路等原因有所延迟，如果您在'+delaytime+'秒内手机没有收到短信验证码，请重新获取。</td></tr>'+
					           (checkPassword?'<tr><td align="right">&nbsp;&nbsp;&nbsp;&nbsp;</td><td align="left">3.请输入您的登录密码</td></tr>':'')+
                               '<tr><td align="right">&nbsp;&nbsp;&nbsp;&nbsp;</td><td align="left">'+
                               (checkPassword?'4':'3')+'.验证通过后才能进入该模块。</td></tr>'+
					   		   '</table>'
	    	  			}]
    	  			
    	  			},
    	  			buttonAlign:'center',
    	  			buttons:[{text:'确定',handler:function(){
    	  				var codefield = this.up('window').queryById('codebox');
    	  				var smscode = codefield.getValue();
    	  				var realcode = codefield.realcode;
    	  				if(smscode!=realcode){
    	  					alert('验证码错误');
    	  					return;
    	  				}
    	  				
    	  				var pwdfield = this.up('window').queryById('pwdbox');
    	  				if(pwdfield){
    	  					var vo = new HashMap();
    	  					vo.put("password",pwdfield.getValue());
    	  					vo.put("transType","pwdcheck");
    	  					Rpc({functionId:'0202011021',success:function(response){
	    	  					var backparam = Ext.decode(response.responseText);
	    	  					if(backparam.result){
	    	  						var url = me.menuCacheCollection[menuid].url;
		        	  				window.location.href=url;
	    	  					}else{
	    	  						alert("密码错误！");
	    	  					}
	    	  					
	    	  				}},vo);
    	  				
    	  				}else{
    	  					var url = me.menuCacheCollection[menuid].url;
	        	  			window.location.href=url;
    	  				}
    	  				
    	  			}}]
    	}).show();
    
    },
	openMenuTree:function(){
		var me=this;
	      Ext.require("EHR.funcmenu.FuncMenuTree",function(){
	            Ext.widget('window',{
		          title:'<div style="margin-left:0px">功能选择</div>',
		          shadow:false,padding:'0 5 0 5',
		          height:400,width:300,
		          modal:true,
		          layout:'fit',
		          tools:[],
		          items:{
		             xtype:'funcmenutree',
		             //xus 角色管理页面 不控制菜单权限 参数
		             isCheckPriv:me.getObjecttype()=='role'?false:true,
		             listeners:{
		             	itemclick:function(tree,record){
		             	     if(record.get('leaf'))
		             	         Ext.getCmp('okbutton').setDisabled(false);
		             	     else
		             	         Ext.getCmp('okbutton').setDisabled(true);
		             	},
		                itemdblclick:function(o,record, item, index, e, eOpts){
		                	 if(record.get('leaf')){
		                	 	 var buttonCmp = Ext.getCmp('okbutton');//确定按钮对象
		                	 	 var dbclickflag = true;//双击事件标识
		             	         me.insertMenu(buttonCmp,dbclickflag);
		             	     }
		             	         
		                }
		             }
		          },
		          buttons:[{xtype:'tbfill',height:30},{text:'确定',id:'okbutton',disabled:true,handler:this.insertMenu,scope:this},{text:'取消',handler:function(){this.up('window').close();}},{xtype:'tbfill',height:30}]
		      }).show();
	      
	      },this);
	},
	insertMenu:function(button,dbclickflag){
	     var item = button.up('window').items.items[0].getSelectionModel().getSelection()[0];
	     var menuInfo = {};
	     menuInfo.name = item.get('name');
	     menuInfo.menuid = item.get('menuid');
	     menuInfo.funcid = item.get('funcid');
	     menuInfo.desc = item.get('desc');
	     menuInfo.qicon = item.get('qicon');
	     menuInfo.url = item.get('url');
	     menuInfo.target = item.get('target');
	     menuInfo.bevalidate = item.get('bevalidate');
	     menuInfo.validatetype = item.get('validatetype');
	     menuInfo.objecttype = this.getObjecttype();
	     
	     var has = false;
	     if(this.menuCacheCollection[menuInfo.menuid]){
	     	button.up('window').close();
	     	var buttons = {};
			Ext.MessageBox.buttonText.ok = '确认';
	     	Ext.MessageBox.alert("提示信息", "您已经添加该工作项，请重新选择！");
	     	return;
	     }
	     
	     var menuStr = '<table cmptype="menu" menuid="'+menuInfo.menuid+'" objecttype="'+this.getObjecttype()+'" border=0 width=145 height=75 cellpadding=0 cellspacing=0 style="cursor:pointer;background:#f6f6f6;float:left;margin:10px 0px 0px 10px;">'+
	            			  '<tr>'+
	            			      '<td rowspan=3 width=46 align=right><img style="width:36px;height:36px;" src="'+this.menuIconUrl+menuInfo.qicon+'"/></td>'+
	            			      '<td height=20 >'+
	            			      	'<img functype="del" src="/components/homewidget/images/del.png" style="display:none;position:relative;top:-8px;left:8px;width:18px;height:18px;float:right;">'+
	            			      '</td>'+
	            			  '</tr>'+
	            			  '<tr>'+
	            				  '<td valign=top height=34 align=left  style="padding-left:5px;font-size:14px;"><div style="width:84px;height:36px;overflow:hidden;text-overflow:ellipsis;">'+menuInfo.name+'</div></td>'+
	            			  '</tr>'+
	            			  '<tr>'+
	            				  '<td valign=top align=left style="font-size:12px;">'+
	            				  	'<div '+(menuInfo.desc.length>0?'title='+menuInfo.desc:'')+' style="width:80px;overflow:hidden;text-overflow:ellipsis;white-space:nowrap;">'+
	            				  		menuInfo.desc+
	            				  	'</div>'+
	            				  '</td>'+
	            			  '</tr>'+
	            			'</table>';
	     Ext.suspendLayouts();
	     var menuEles = this.body.query("table[cmptype]");
	     //与调用者相同权限的集合
	     var avalableTable=this.body.query("table[objecttype='"+this.getObjecttype()+"']");
	     if(avalableTable.length>=this.maxCount){
	          menuEles[menuEles.length-1].style.display="none";
	          if(button.up('window'))
	          		button.up('window').close();
	     }
	     
	     if(dbclickflag == true && avalableTable.length>this.maxCount)//双击叶子结点且工作项数量大于7时，不允许添加   
	          return;
	     Ext.DomHelper.insertHtml('beforeBegin',menuEles[menuEles.length-1],menuStr);
	     this.updateLayout();
	     Ext.resumeLayouts(true);
	     this.menuCacheCollection[menuInfo.menuid] = menuInfo;
	     this.saveMenus();
	     if(dbclickflag != true && button.up('window'))//双击添加时，不关闭弹出框；点击确定添加 关闭功能选择框
	   	  	button.up('window').close();
	},
	
	deleteMenu:function(menuTable){
		var me = this;
			menuid = menuTable.getAttribute("menuid"),
		    parentEle = menuTable.parentNode;
		    menuEles =  parentEle.getElementsByTagName("table");
		Ext.suspendLayouts();
		parentEle.removeChild(menuTable);
		var avalableTable=this.body.query("table[objecttype='"+this.getObjecttype()+"']")
		if(avalableTable.length<=me.maxCount)
		    menuEles[menuEles.length-1].style.display="block";
		me.updateLayout();
	    Ext.resumeLayouts(true);
		
		delete me.menuCacheCollection[menuid];
	    me.saveMenus();
	},
	saveMenus:function(){
		var list = [];
		var menuEles = this.body.query("table[cmptype=menu]");
		for(var i=0;i<menuEles.length;i++){
		   var menuInfo = this.menuCacheCollection[menuEles[i].getAttribute("menuid")];
		   if(menuInfo.objecttype==this.getObjecttype())
		   list.push(menuInfo);
		}
		  var map = new HashMap();
		  map.put("menus",list);
		  map.put("type",'save');
		  map.put("objectid",this.getObjectid());
		  map.put("objecttype",this.getObjecttype());
		  Rpc({functionId:'ZJ100000161',scope:this},map);
	}
});
