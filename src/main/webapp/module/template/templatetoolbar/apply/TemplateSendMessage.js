/**
*不走审批 发送邮件短信弹出窗口
*add by hej 2016-09-13
*
**/
Ext.define('templatecombo',{
	extend:'Ext.form.field.ComboBox',
	xtype: 'templatecombo',
	createPicker:function(){
		var me = this;
		var picker = this.callParent(arguments);
		picker.on('itemclick',function(e,record){
			me.fireEvent('selectclick',me,record);
		});
		return picker;
	}
});
Ext.define('TemplateApplyUL.TemplateSendMessage',{
	afterfunc:'',//回调函数
	x:'',
	y:'',
	pickupname:'',//收件对象名称
	pickupid:'',//收件人对象id
	constructor:function(config){
		templateSendMessage_me=this;
		this.pagemap=config.map;
		this.afterfunc=config.map.callBackFunc;
		this.init();
	},
	init:function(){
		var map = new HashMap();
		map.put("tab_id",templatePrepare_me.templPropety.tab_id);
	    Rpc({functionId:'MB00005011',async:false,success:this.createMsgWin,scope:this},map);
	},
	createMsgWin:function(form,action){
		var result = Ext.decode(form.responseText);
		var mailTempletList = result.mailTempletList;//邮件模板
		var mailTempletID = result.mailTempletID;
		this.email_staff = result.email_staff;//邮件通知到本人
		var template_staff = result.template_staff;//员工本人的邮件模板
		var template_bos = result.template_bos;//业务办理人员的邮件模板
		var user_ = result.user_;
		var user_h = result.user_h;
		var title = result.title;//标题
		var context = result.context;//内容
		
	    templateSendMessage_me.x='';
	    templateSendMessage_me.y='';
	    
	    var readOnlyValue="false";
		if(mailTempletList.length==2)
			readOnlyValue="true";
		if(mailTempletList.length==1&&mailTempletList[0].dataValue==template_bos)
			readOnlyValue="true";
			
	    var mailTemplate =undefined;
	    if(mailTempletList.length==2){
	    	var mailStore=Ext.create('Ext.data.Store', {
				 fields:['dataValue', 'dataName'],
		         data:mailTempletList
			});
	    	mailTemplate=Ext.create('templatecombo', {
			    fieldLabel:'邮件模板',
			    labelWidth: 60,
		        height:22,
		        width:180,
		        labelAlign:'left',
		        padding: '10 0 0 10',
		        name:'mailName',
		        id:'mailId',
			    store: mailStore,
			    valueField:'dataValue',
	            displayField:'dataName',
	            typeAhead:true,
	            editable : false, 
	            queryMode: 'local',
	            emptyText:'请选择',
	            listeners : {
	            	'selectclick':function(){
	            		var emailid = this.getValue();
	            		var map = new HashMap();
	            		map.put('id',emailid);
	            		Rpc({functionId:'MB00005012',async:false,success:function(form,action){
	            			var result = Ext.decode(form.responseText);
	            			var subject=getDecodeStr(result.subject);
							var content=getDecodeStr(result.content);
							Ext.getCmp('context').setValue(content);
	    					Ext.getCmp('title').setValue(subject);
	            		},scope:this},map);
	            	}
	            }
	        });
	    }
	    var title = Ext.widget('textfield',{
	    	fieldLabel: '标题',
	    	id:'title',
	    	labelWidth: 60,
	    	width:578,
	    	height:22,
	    	value:title,
	    	readOnly:readOnlyValue=='true'?true:false,
	    	padding: '10 0 0 10'
	    });
	    
	    var textarea = Ext.widget('textarea',{
	    	fieldLabel: '内容',
	    	id:'context',
	    	//height:300,
	    	labelWidth: 60,
            //width:578,
            value:context,
            readOnly:readOnlyValue=='true'?true:false,
            padding: '10 0 0 10',
            fieldStyle:'height:240px;width:511px;',//邮件内容框高度IE兼容模式下不正常
            labelStyle:'padding:0px;'//bug 38793 窗口位置和ie不同
	    });
	    
	    var roleStore=Ext.create('Ext.data.Store', {
			 fields:['codeitem', 'codename'],
	         data:[{'codeitem':'1','codename':'人员'},
                	  {'codeitem':'2','codename':'角色'},
                	  {'codeitem':'4','codename':'用户'}]
		});
		
	    var roleComboBox=Ext.create('templatecombo', {
			    fieldLabel:'收件对象',
			    labelWidth: 60,
		        //height:22,
		        width:180,
		        labelAlign:'left',
		        name:'roleName',
		        id:'roleId',
			    store: roleStore,
			    valueField:'codeitem',
	            displayField:'codename',
	            typeAhead:true,
	            editable : false, 
	            queryMode: 'local',
	            emptyText:'请选择',
	            listeners : {
	            	'selectclick':function(){
	            		if(this.getValue()=='1'){  //=1为人员；=2为角色；=4为用户
		            		var p = new PersonPicker({
								multiple: true,  //
								isSelfUser:true,//是否选择自助用户
								isMiddle:true,//是否居中显示
								isPrivExpression:false,//是否启用人员范围（含高级条件）
								text: "确定",
								callback: function (c) {
				      	            var staffname = "";
									var staffid = "";
									var selectEmail=Ext.ComponentQuery.query("panel[user_=user_]")[0];
									for (var i = 0; i < c.length; i++) {
										staffname = c[i].name;
										staffid =c[i].id;
										if(templateSendMessage_me.pickupname.indexOf(staffname)==-1){
											templateSendMessage_me.pickupid += "1:"+staffid +",";//这里的id是使用pubfunc加密的，在后台需要进行解密操作
											templateSendMessage_me.pickupname +="1:"+staffname +",";
											selectEmail.add(templateSendMessage_me.createKeyItem('1',staffname,staffid,'0'));
										}
									}
								}
							},this);
							p.open();
		               }else if(this.getValue()=='4'){
		              	   var p = new PersonPicker({
								multiple: true,
								isSelfUser:false,//是否选择自助用户
								isMiddle:true,//是否居中显示
								isPrivExpression:false,//是否启用人员范围（含高级条件）
								text: "确定",
								callback: function (c) {
									var staffname = "";
									var staffid = "";
									var selectEmail=Ext.ComponentQuery.query("panel[user_=user_]")[0];
									for (var i = 0; i < c.length; i++) {
										staffname = c[i].name;
										staffid =c[i].id;
										if(templateSendMessage_me.pickupname.indexOf(staffname)==-1){
											templateSendMessage_me.pickupid += "4:"+staffid +",";//这里的id是使用pubfunc加密的，在后台需要进行解密操作
											templateSendMessage_me.pickupname +="4:"+staffname +",";
											selectEmail.add(templateSendMessage_me.createKeyItem('4',staffname,staffid,'0'));
										}
									}
								}
							},this);
							p.open();
		               }else if(this.getValue()=='2'){//角色
		            	   templateSendMessage_me.showroleWin('0');
		               }
	                }
	             }
			});
			
		var email_staff_value = undefined;
		if(this.email_staff=='true'){
			email_staff_value = Ext.widget('checkbox',{
				name : 'email_staff_value',
				inputValue : '1',
				uncheckedValue:'0',
				id : 'email_staff_value',
				boxLabel  : '通知本人',
				padding:'10 0 30 75',
				checked:true
			});
		}
		
		var roleContainer=Ext.widget('container',{ 
			padding: this.email_staff=='true'?'10 0 10 10':'10 0 30 10',
			height:60,
			layout: {
		        type: 'hbox',
		        align:'middle'
		    },
		    items:[roleComboBox,{xtype:'panel',user_:'user_',border:1,height : 22,width : 355,padding:'0 10 0 15',
					    front:true,
					    bodyStyle:"border-top:none;border-left:none;border-right:none;",
		   				floating:false,
		   				shadow:false,
		   				scrollFlags:{overflowX:'',overflowY:''},
		   				layout:{
		   					type:"column"
		   				},
		   				listeners:{
		   	             render:function(){
		   	                this.mon(this.getEl(),{
		   	                   mouseover:this.hadFocus,
		   	                   mouseout:this.lostFocus,
		   	                   scope:this
		   	                });
		   	             }
		   		         },
		   		         hadFocus:function(){
		   		        	var selectEmail=Ext.ComponentQuery.query("panel[user_=user_]")[0];
		   		        	if(selectEmail.items.length>5){
		   		        		if(templateSendMessage_me.x==''&&templateSendMessage_me.y==''){
		   		        			templateSendMessage_me.showEmailWin(templateSendMessage_me.win.getX(),templateSendMessage_me.win.getY(),selectEmail.items);
			   		        	}
		   		        		else{
		   		        			templateSendMessage_me.showEmailWin(templateSendMessage_me.x,templateSendMessage_me.y,selectEmail.items);
		   		        		}
		   		        	}
		   		         },
		   		         lostFocus:function(){
		   		          }
		   			},{
					    xtype : 'button',margin:'0 0 0 5',
                        text:'清空',
                        handler : function() {
	                        var selectEmail=Ext.ComponentQuery.query("panel[user_=user_]")[0];
	                        if(selectEmail.items.length>0){
	                            selectEmail.removeAll(true);
	                            templateSendMessage_me.pickupid='';
	                            templateSendMessage_me.pickupname='';
	                        }
                        }
				    }]
		});
		
		//表单
		var formPanel=new Ext.form.Panel({
	        frame:false,
	        bodyStyle:'background:#FFFFFF;',//白色
	        border:false,
	        layout:'vbox',
	        items:[mailTempletList.length==2?mailTemplate:{html:''},
			       title,textarea,roleContainer,
			       this.email_staff=='false'?{html:''}:email_staff_value]
		});
		
		templateSendMessage_me.win=Ext.widget("window",{
	          title:'邮件发送',  
	          width:610,
	          autoHeight:true,
	          layout:'fit',
	          resizable:false,
			  modal:true,
			  closeAction:'destroy',
			  border: false,
			  plain:true,
         	  items: [{
         		xtype:'panel',
         		border:false,
				items:[formPanel],
				buttons:[
						{xtype:'tbfill'},
						{text:'确定',handler:function(){
							templateSendMessage_me.getSelectedData();
						}},
						{text:'关闭',handler:function(){
							templateSendMessage_me.closeWin();
						 }},
						{xtype:'tbfill'}]}
			  	],
			  listeners:{
				move:function(obj,x,y){
					templateSendMessage_me.x=x;
					templateSendMessage_me.y=y;
				},
				close:function(){
					var win = Ext.getCmp('person_picker_single_view');
					templateTool_me.enabledButton("template_rejectButton");
					templateTool_me.enabledButton("template_applyButton");
					templateTool_me.enabledButton("template_submitButton");
					if(win)
						win.close();
				}
			 }
	    });
	    templateSendMessage_me.win.show();
	},
	/**
	 * 生成角色表格win
	 * @param flag
	 * @param id
	 */
	showroleWin:function(flag){
		templateSendMessage_me.actormultiple = false;
		if(flag=='1')
			templateSendMessage_me.actormultiple = false;
		else
			templateSendMessage_me.actormultiple = true;
		Ext.require('EHR.rolepicker.RolePicker', function(){          
				Ext.create('EHR.rolepicker.RolePicker',{callBackFunc:templateSendMessage_me.getRolesList,multiple:templateSendMessage_me.actormultiple});
			},this);
	},
	getRolesList:function(records){
	    if(templateSendMessage_me.actormultiple==true){// 多选
      		var staffname = "";
      		var staffid = "";
			var selectEmail=Ext.ComponentQuery.query("panel[user_=user_]")[0];
			for (var i = 0; i < records.length; i++) {
				var record =  records[i];
				staffname = record.role_name;
				staffid = record.role_id_e;
				if(templateSendMessage_me.pickupname.indexOf(staffname)==-1){
					templateSendMessage_me.pickupid += "2:"+staffid +",";//这里的id是使用pubfunc加密的，在后台需要进行解密操作
					templateSendMessage_me.pickupname +="2:"+staffname +",";
					selectEmail.add(templateSendMessage_me.createKeyItem('2',staffname,staffid,'0'));
				}
			}
		}
	},
	/**
	 * 邮件抄送选择后的显示
	 * @param staffname
	 * @param pickupid
	 * @returns
	 */
	createKeyItem:function(entertype,staffname,staffid,flag){
	       var con = Ext.widget('container',{
	          style:{border:"solid #c5c5c5 0px",backgroundColor:'#f8f8f8'},
	          margin:1,
	          width:80,
	          height:16,
	          value:staffname,
	          entertype:entertype,
	          staffname:staffname,
	          staffid:staffid,
	          layout:'hbox',
	          items:[{xtype:'label',padding:'0 0 0 5',text:staffname,flex:10},{
	          	xtype:'image',src: rootPath+'/components/querybox/images/hongcha.png',hidden:true,
	          	width:16,height:16,margin:0,style:'cursor:pointer;',
	          	listeners:{
	          		render:function(hh){
	          			this.getEl().on('click',function(){
	          				var staffid =  this.ownerCt.staffid;
	          				var staffname =  this.ownerCt.staffname;
	          				var entertype = this.ownerCt.entertype;
	          				var emailcon = Ext.ComponentQuery.query("container[staffid="+this.staffid+"]")[0];
	          				var selectEmail=Ext.ComponentQuery.query("panel[uesr_=uesr_]")[0];
	          				var emailWin = Ext.getCmp('emailWin');
	          				if(flag=='0'){
	          					if(emailWin!=undefined){
	          						emailWin.remove(emailcon,true);
	          					}
	          				}else{
	          					var a = selectEmail.query("container[staffid="+staffid+"]")[0];
	          					selectEmail.remove(a,true);
	          				}
	          				templateSendMessage_me.removeQueryKey(this.ownerCt);
	          				var b = entertype+':'+staffid+',';
	          				templateSendMessage_me.pickupid =  templateSendMessage_me.pickupid.replace(b,"");
          					templateSendMessage_me.pickupname = templateSendMessage_me.pickupname.replace(entertype+':'+staffname+',',"");
	          			},this);
	          		}
	          	}
	          }],
	          listeners:{
	             render:function(){
	                this.mon(this.getEl(),{
	                   mouseover:this.hadFocus,
	                   mouseout:this.lostFocus,
	                   scope:this
	                });
	             },
	             afterrender: function(hh) {
		             var tip = Ext.create('Ext.tip.ToolTip', {
						target: hh.getEl(),
					    html: staffname,
					    trackMouse: true,
					    bodyStyle:"background-color:white;border:1px solid #c5c5c5",
					    border:true
					});
		         }
	          },
	          hadFocus:function(){
	             this.child('image').setVisible(true);
			     this.setStyle({
			     	border:"solid #ff8c26 0px",
			     	backgroundColor:'#feefe5'
			     });
	          },
	          lostFocus:function(){
	          	this.child('image').setVisible(false);
	          	this.setStyle({
			     	border:"solid #c5c5c5 0px",
			     	backgroundColor:'#f8f8f8'
			     });
	          }
	          
	       });
	       return con;
	},
	removeQueryKey:function(keyItem){
		 keyItem.destroy();
	},
	/**
	 * 展现选择的邮件抄送人员
	 */
	showEmailWin:function(x,y,items){
		var xx = 0;
		var yy = 0; 
		xx = x+205;
		yy = y+364;
		var emailWin = Ext.getCmp('emailWin');
		if(emailWin){
			emailWin.setPagePosition(xx,yy);
		}else{
			emailWin = Ext.widget('window',{
				width:340,
				id:'emailWin',
				x:xx,
				y:yy,
				header:false,
				resizable : false,
				closeAction:'hide',
				layout:{type:"column"},
				items:[],
				listeners:{
					render:function(){
						this.mon(Ext.getDoc(), {
							mouseover: this.showIf,
			                mousedown: this.hiddenIf,
			                scope: this
			            });
					}
				},
				hiddenIf: function(e) {
					var me = this;
					if (!me.isDestroyed && !e.within(me.bodyEl, false, true) && !me.owns(e.target)) {
						me.hide();
			        }
			    },
			    showIf:function(e){
			    	
			    }
			});
		}
		emailWin.removeAll(true);
		items.each(function(r){
			emailWin.add(templateSendMessage_me.createKeyItem(r.entertype,r.staffname, r.staffid,'1'));
		});
		emailWin.show();
	},
	//关闭报批窗口
	closeWin:function(){
		var emailWin = Ext.getCmp('emailWin');
		if(emailWin!=undefined){
			emailWin.hide();
		}
		templateSendMessage_me.win.close();
		
	},
	/**
	 * 生成角色表格
	 * @param form
	 * @param action
	 */
	getRoleTableOK:function(form,action){
		var result = Ext.decode(form.responseText);
		var conditions=result.tableConfig;
		var obj = Ext.decode(conditions);
		templateSendMessage_me.tableObj = new BuildTableObj(obj);
	},
	getSelectedData:function(){
		var topic=Ext.getCmp('context').getValue();
	    var title=Ext.getCmp('title').getValue();
	   
	    if(topic.length==0){
	   		Ext.showAlert("请填写消息内容");
	    	return;
	    }
	    var pickupid = templateSendMessage_me.pickupid;  
	    var hashvo=new HashMap();
       	hashvo.put("context",topic); 
       	hashvo.put("title",title); 
       	hashvo.put("sendid",pickupid); 
       	
        if(templateSendMessage_me.email_staff=='true'){
	       	if(Ext.getCmp("email_staff_value").getValue())
	       			hashvo.put("email_staff_value","1");
	       	else
	       		    hashvo.put("email_staff_value","0");
        }
        Ext.callback(eval(templateSendMessage_me.afterfunc),null,[hashvo]); 
	}
})