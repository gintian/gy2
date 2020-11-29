/**
 * 报批 弹框，用于写审批意见
 * 将页面中显示的组件分离，根据条件自由组装，可实现自动流转和手动流转的不同情况 
 * 该页面仅仅用于显示，而不需要进行报批操作
 */ 
	/**
	 * 参数配置
	 *  页面参数配置
	 *   邮件抄送 isReport 1显示 0不显示 
	 *   报送对象 isApply 1显示 0不显示 
	 *   优先级 isPri 1显示 0不显示 
	 *   审批意见 isApplyContent 1显示 0不显示 
	 *  按钮参数配置
	 *   同意按钮 btnAgree 1显示 0不显示
	 *   继续报批按钮 btnContinueApply 1显示 0不显示
	 *   驳回按钮 btnReject 1显示 0不显示
	 *   批准按钮 btnApprove 1显示 0不显示
	 *   //关闭按钮 btnCancel 1显示 0不显示
	 *   确定按钮 btnOk 1显示 0不显示
	 *   报批页面标题 winTitle 
	 *   
	 *   展现邮件抄送人员的window需要点击win之外的区域关闭win
	 */
Ext.Loader.setPath("EHR","/components");
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
Ext.define('TemplateApplyUL.TemplateSubmit',{
	afterfunc:'',//回调函数
	x:'',//邮件抄送对像过多显示窗体位置控制
	y:'',
	actorId:'',//报送对象id
	actorType:'',//报送对象类型
	reportObjectId:'',//邮件抄送对象id
	reportObjectName:'',//邮件抄送对象名称
	constructor:function(config){
		templateSubmit_me=this;
		this.pagemap=config.map;
		this.afterfunc=config.map.callBackFunc;
		this.init();
	},
	init:function(){
		var map = new HashMap();
		map.put("tab_id",templatePrepare_me.templPropety.tab_id);
	    Rpc({functionId:'MB00005003',async:false,success:this.createWin,scope:this},map);
	},
	/*
	 * 创建主窗体
	 */
	createWin:function(form,action){
		var result = Ext.decode(form.responseText);
		var signLogo = result.signLogo;//
	    var emergencylist=result.emergencylist;  //优先级 ：高优先级、正常、低优先级
	    var rolelist=result.rolelist;    //包含 人员、角色、用户、组织机构
	    templateSubmit_me.x='';
	    templateSubmit_me.y='';
	    
		//优先级
		var priStore=Ext.create('Ext.data.Store', {
			 fields:['codeitem', 'codename'],
	         data:emergencylist
		});
		
		var priComboBox=Ext.create('Ext.form.ComboBox', {
		    fieldLabel:'优先级',
		    padding: '10 0 10 10',
		    labelWidth: 47,
		    width:167,
	        height:22,
	        labelAlign:'left',
	        name:'priorityName',
	        id:'priorityId',
		    store: priStore,
		    valueField:'codeitem',
            displayField:'codename',
            typeAhead:true,
            editable : false, 
            queryMode: 'local',
            value:'',//默认值,要设置为提交给后台的值，不要设置为显示文本,可选
            emptyText:'请选择',
            listeners : {
			      afterRender : function(combo) {
			      	if(priStore.data.items.length > 0){//判断优先级是否存在，lis 20160803
				         var firstValue = priStore.data.items[1].data.codeitem;
				         combo.setValue(firstValue);//同时下拉框会将与name为firstValue值对应的 text显示
			      	}
			      }
            }
		});
		
		//选择要报送的对象(单选)
		var roleStore=Ext.create('Ext.data.Store', {
			 fields:(signLogo!=null&&signLogo=='hand')?['dataValue','dataName']:['codeitem', 'codename'],
	         data:rolelist
		});
		
		templateSubmit_me.actorId='';
		templateSubmit_me.actorType='';  //报送对象类型  =1,具体审批人,=2角色 =3组织单位 
		var roleComboBox = undefined;
		if(signLogo!=null&&signLogo=='hand'){
			roleComboBox=Ext.create('templatecombo', {
			    fieldLabel:'报送对象',
			    labelWidth: 60,
		        height:22,
		        width:180,
		        labelAlign:'left',
		        name:'roleName',
		        id:'roleId',
			    store: roleStore,
			    valueField:'dataValue',
	            displayField:'dataName',
	            typeAhead:true,
	            editable : false, 
	            queryMode: 'local',
	            emptyText:'请选择',
	            matchFieldWidth:false,
	            listeners : {
	            	'selectclick':function(){
	            		templateSubmit_me.actorId = this.getValue().split('`')[0];
	            		var selectRole=Ext.getCmp('selectRoleId');
						var text = selectRole.getEl().query('input');
	      	            text[0].value=this.getValue().split('`')[1];
	      	            templateSubmit_me.actorType = this.getValue().split('`')[2];;
	            	}
	            }
	         });
		}else{
			roleComboBox=Ext.create('templatecombo', {
			    fieldLabel:'报送对象',
			    labelWidth: 60,
		        height:22,
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
	            		if(this.getValue()=='1'){  //=1为人员；=2为角色；=4为用户；=3为组织单位
		            		var p = new PersonPicker({
								multiple: false,  //暂时无法选择单个人员，有bug
								isSelfUser:true,//是否选择自助用户
								isMiddle:true,//是否居中显示
								isPrivExpression:false,//是否启用人员范围（含高级条件）
								deprecate:templatePrepare_me.deprecate,//不显示的人员
								text: "确定",
								callback: function (c) { 
									var staffname = "";
									templateSubmit_me.actorType = '1';
									staffname = c.name;
									templateSubmit_me.actorId =c.id;//这里的id是使用pubfunc加密的，在后台需要进行解密操作
									var selectRole=Ext.getCmp('selectRoleId');
									var text = selectRole.getEl().query('input');
				      	            text[0].value=staffname;
								}
							},this);
							p.open();
		               }else if(this.getValue()=='4'){
		              	   var p = new PersonPicker({
								multiple: false,
								isSelfUser:false,//是否选择自助用户
								isMiddle:true,//是否居中显示
								isPrivExpression:false,//是否启用人员范围（含高级条件）
								text: "确定",
								callback: function (c) {
									var staffname = "";
									templateSubmit_me.actorType = '4';
									staffname = c.name;
									templateSubmit_me.actorId =c.id;//这里的id是使用pubfunc加密的，在后台需要进行解密操作
									var selectRole=Ext.getCmp('selectRoleId');
									var text = selectRole.getEl().query('input');
				      	            text[0].value=staffname;
								}
							},this);
							p.open();
		               }else if(this.getValue()=='2'){//角色 单选
		            	   templateSubmit_me.showroleWin('1');
		               }else if(this.getValue()=='3'){//组织单元 单选
		            	   templateSubmit_me.showorgWin();
		               }
	                }
	             }
			});
		}
		var RoleShow=Ext.widget('container', {
		    title: '',
		    border:0,
		    padding:'5 0 0 10',
		    items: [{
		        xtype: 'box',
		        name: 'selectRoleName',
		        id:'selectRoleId',
		        height:23,
		        width:210,
		        html:'<input id="sunmitid" style="border:0;width:200px;border-bottom:#c5c5c5 1px solid;background:none;font: normal 12px 微软雅黑;">'
		    }]
		});
		
		var roleContainer=Ext.widget('container',{ 
			padding: '10 0 10 10',
			layout: {
		        type: 'hbox',
		        align:'middle'
		    },
		    items:[roleComboBox,RoleShow]//,deleteRole]
		});
		
		//邮件抄送，暂时将下列列表和显示框和删除图标放在了一起，可以分开写
		templateSubmit_me.reportObjectId='';
		templateSubmit_me.reportObjectName='';
		var EmailContainer=Ext.widget('container',{
			padding: '10 0 10 10',
			height:50,
			layout: {
		        type: 'hbox',
		        align:'middle'
		    },
		    items:[{
		                height:22,
		                fieldLabel: '抄送对象',
		                labelWidth: 60,
		                width:180,
		                labelAlign:'left',
		                xtype:'templatecombo',
		                name:'EmailSendName',
		                id:'EmailSendId',
		                store:new Ext.data.Store({
		                    fields:['abbr', 'name'],
		                    data:[{'abbr':'people','name':'人员'},
		                    	  {'abbr':'role','name':'角色'},
		                    	  {'abbr':'user','name':'用户'}]
		                }),
			            valueField:'abbr',
			            displayField:'name',
			            typeAhead:true,
			            queryMode: 'local',
			            emptyText:'请选择',
			            listeners : {
			            	'selectclick':function(record){
			            		if(this.getValue()=='user'){//用户
				            		var p = new PersonPicker({
										multiple: true,
										isSelfUser:false,//是否选择自助用户
										isMiddle:true,//是否居中显示
										isPrivExpression:false,//是否启用人员范围（含高级条件）
										text: "确定",
										callback: function (c) {
											var staffname = "";
											var staffid = "";
											var selectEmail=Ext.ComponentQuery.query("panel[selectEmailId=selectEmailId]")[0];
											//if(selectEmail.items.length>0){
											//	selectEmail.removeAll(true);
											//}
											for (var i = 0; i < c.length; i++) {
												staffname = c[i].name;
												staffid =c[i].id;//这里的id是使用pubfunc加密的，在后台需要进行解密操作
												if(templateSubmit_me.reportObjectName.indexOf(staffname)==-1){
													templateSubmit_me.reportObjectId += "4:"+staffid +",";
													templateSubmit_me.reportObjectName +="4:"+staffname +",";
													selectEmail.add(templateSubmit_me.createKeyItem('4',staffname,staffid,'0'));
												}
												
											}
										}
									},this);
									p.open();
				              }else if(this.getValue()=='people'){//人员
				              	  var p = new PersonPicker({
										multiple: true,
										isSelfUser:true,//是否选择自助用户
										isMiddle:true,//是否居中显示
										isPrivExpression:false,//是否启用人员范围（含高级条件）
										text: "确定",
										callback: function (c) {
											var staffname = "";
											var staffid = "";
											var selectEmail=Ext.ComponentQuery.query("panel[selectEmailId=selectEmailId]")[0];
											//if(selectEmail.items.length>0){
											//	selectEmail.removeAll(true);
											//}
											for (var i = 0; i < c.length; i++) {
												staffname = c[i].name;
												staffid =c[i].id;
												if(templateSubmit_me.reportObjectName.indexOf(staffname)==-1){
													templateSubmit_me.reportObjectId += "1:"+staffid +",";//这里的id是使用pubfunc加密的，在后台需要进行解密操作
													templateSubmit_me.reportObjectName +="1:"+staffname +",";
													selectEmail.add(templateSubmit_me.createKeyItem('1',staffname,staffid,'0'));
												}
											}
										}
									},this);
									p.open();
				              }else if(this.getValue()=='role'){
				            	  templateSubmit_me.showroleWin('0');
				              }
			               }
			            }
		            },
//		            {xtype: 'textfield',padding:'0 0 0 10',name: 'selectEmailName',id:'selectEmailId',value:'',width:395,
//				        //html:'<input type="text" name="username" value="" size="12" style="border:0px;border-bottom:#000000 1px solid;">',
//				       // style:'background:none; border-right: 0px solid;border-top: 0px solid;border-left: 0px solid;border-bottom: #000000 1px solid;',
//				        /*Style:'padding:0px 0px 1px',*/
//				        allowBlank: true  // 表单项非空
//				    },
		            {xtype:'panel',selectEmailId:'selectEmailId',border:1,height : 22,width : 355,padding:'0 10 0 15',
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
		   		        	var selectEmail=Ext.ComponentQuery.query("panel[selectEmailId=selectEmailId]")[0];
		   		        	if(selectEmail.items.length>5){
		   		        		if(templateSubmit_me.x==''&&templateSubmit_me.y==''){
		   		        			templateSubmit_me.showEmailWin(templateSubmit_me.win.getX(),templateSubmit_me.win.getY(),selectEmail.items);
			   		        	}
		   		        		else{
		   		        			templateSubmit_me.showEmailWin(templateSubmit_me.x,templateSubmit_me.y,selectEmail.items);
		   		        		}
		   		        	}
		   		         },
		   		         lostFocus:function(){
		   		          }
		   			},
				    {
				    	/*
				    	xtype : 'image',
					    src : rootPath+'/images/del.gif',
					    style : {cursor : 'pointer'},
					    title:'清空抄送人员',
					    listeners : {
					    	el:{
						        click : function(){
						        	var selectEmail=Ext.ComponentQuery.query("panel[selectEmailId=selectEmailId]")[0];
						        	if(selectEmail.items.length>0){
						        		selectEmail.removeAll(true);
						        		templateSubmit_me.reportObjectId='';
						        		templateSubmit_me.reportObjectName='';
						        	}
						        }
					    	}
					    }
					    */
					    xtype : 'button',margin:'0 0 0 5',
                        text:'清空',
                        handler : function() {
	                        var selectEmail=Ext.ComponentQuery.query("panel[selectEmailId=selectEmailId]")[0];
	                        if(selectEmail.items.length>0){
	                            selectEmail.removeAll(true);
	                            templateSubmit_me.reportObjectId='';
	                            templateSubmit_me.reportObjectName='';
	                        }
                        }
				    }
		         ]
		});
		
	    var contentPanel=Ext.widget('container', {
		    title: '',
		    padding: '10 0 10 10',
		    border:0,
		    items: [{
            	labelAlign:'right',
            	fieldLabel:'',
            	xtype:'textarea',
            	name:'contentName',
            	id:'contentId',
            	width:579,
				fieldStyle:'height:240px;width:576px;'
		    }]
		});

	    //表单
		var formPanel=new Ext.form.Panel({
	        frame:false,
	        bodyStyle:'background:#FFFFFF;',//白色
	        border:false,
	        layout:'vbox'
		});
		
		//需要确定参数、、、、、、
		if(this.pagemap.isApply=='1'&&this.pagemap.isPri=='1'){
			priComboBox.setMargin('0 0 0 10');
			formPanel.add({xtype:'container',layout:{type:'hbox',align:'middle'},items:[roleContainer,priComboBox]}); //手工流转包含 报送对象
		}
		if(this.pagemap.isApply=='0'&&this.pagemap.isPri=='1'){
			priComboBox.setMargin('0 0 0 23');
			formPanel.add(priComboBox);
		}
		if(this.pagemap.isReport=='1'){
			formPanel.add(EmailContainer);
		}
		if(this.pagemap.isApplyContent=='1'){
			formPanel.add(contentPanel);//内容
		}
		if(this.pagemap.isApply=='1'&&this.pagemap.isPri=='1'&&this.pagemap.isReport=='1'&&this.pagemap.isApplyContent=='0'){
			priComboBox.setMargin('20 0 10 23');
			formPanel.add(priComboBox);
			formPanel.add(roleContainer);
			EmailContainer.setMargin('0 0 50 0');
			formPanel.add(EmailContainer);
		}
		if(this.pagemap.isApply=='0'&&this.pagemap.isPri=='1'&&this.pagemap.isReport=='1'&&this.pagemap.isApplyContent=='0'){
			priComboBox.setMargin('20 0 10 23');
			formPanel.add(priComboBox);
			//formPanel.add(roleContainer);
			EmailContainer.setMargin('0 0 50 0');
			formPanel.add(EmailContainer);
		}
		if(this.pagemap.isApply=='1'&&this.pagemap.isPri=='1'&&this.pagemap.isReport=='0'&&this.pagemap.isApplyContent=='0'){
			priComboBox.setMargin('20 0 10 23');
			formPanel.add(priComboBox);
			roleContainer.setMargin('0 0 50 0');
			formPanel.add(roleContainer);
			//formPanel.add(EmailContainer);
		}
		//没有抄送权限，不显示抄送选择框。
		if(this.pagemap.isApply=='0'&&this.pagemap.isPri=='1'&&this.pagemap.isReport=='0'&&this.pagemap.isApplyContent=='0'){
			priComboBox.setMargin('20 0 10 23');
			formPanel.add(priComboBox);
			//formPanel.add(roleContainer);
			//EmailContainer.setMargin('0 0 50 0');
			//formPanel.add(EmailContainer);
		}
		templateSubmit_me.createButtons();
		var title = '';
		if(this.pagemap.winTitle==''){
			title='报批'; 
		}else{
			title = this.pagemap.winTitle;
		};
		
		//弹出窗口
		templateSubmit_me.win=Ext.widget("window",{
	          title:title,  
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
				buttons:templateSubmit_me.buttons}
			  ],
			  listeners:{
				move:function(obj,x,y){
					templateSubmit_me.x=x;
					templateSubmit_me.y=y;
				},
				close:function(){//判断选人控件存在，如果存在则关闭 lis 20160825
					var win = Ext.getCmp('person_picker_single_view');
					templateTool_me.enabledButton("template_rejectButton");
					templateTool_me.enabledButton("template_applyButton");
					templateTool_me.enabledButton("template_submitButton");
					if(win)
						win.close();
				}
			 }
	    });
	    templateSubmit_me.win.show(); 
	},
	/**
	 * 创建button按钮
	 */
	createButtons:function(){
		var buttons =[{xtype:'tbfill'}];
		if(this.pagemap.btnAgree=='1'){
			buttons.push({text:'同意',id:'submit_Agree',handler:function(){
				templateSubmit_me.submit_DisableButton();
				templateSubmit_me.getPanelValue('4');
			}});
		}
		if(this.pagemap.btnContinueApply=='1'){
			buttons.push({text:'继续报批',id:'submit_GoApply',handler:function(){
				templateSubmit_me.submit_DisableButton();
				templateSubmit_me.getPanelValue('1');				
			}});
		}
		if(this.pagemap.btnReject=='1'){
			buttons.push({text:'驳回',id:'submit_Reject',handler:function(){
				templateSubmit_me.submit_DisableButton();
				templateSubmit_me.getPanelValue('2');
			}});
		}
		if(this.pagemap.btnApprove=='1'){
			buttons.push({text:'批准',id:'submit_Apply',handler:function(){
				templateSubmit_me.submit_DisableButton();
				templateSubmit_me.getPanelValue('3');
			}});
		}
		if(this.pagemap.btnOk=='1'){
			buttons.push({text:'确定',id:'submit_Confirm',handler:function(){
				templateSubmit_me.submit_DisableButton();
				templateSubmit_me.getPanelValue('1');
			}});
		}
		if(this.pagemap.btnCancel=='1'){
			buttons.push({text:'取消',id:'submit_Cancel',handler:function(){
				templateSubmit_me.submit_DisableButton();
				var emailWin = Ext.getCmp('emailWin');
				if(emailWin!=undefined){
					emailWin.close();
				}
				templateSubmit_me.win.close();
			}});
		}
		buttons.push({xtype:'tbfill'});
		templateSubmit_me.buttons = buttons;
	},
	/**
	 * 邮件抄送选择后的显示
	 * @param staffname
	 * @param reportObjectId
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
	          				var selectEmail=Ext.ComponentQuery.query("panel[selectEmailId=selectEmailId]")[0];
	          				var emailWin = Ext.getCmp('emailWin');
	          				if(flag=='0'){
	          					if(emailWin!=undefined){
	          						emailWin.remove(emailcon,true);
	          					}
	          				}else{
	          					var a = selectEmail.query("container[staffid="+staffid+"]")[0];
	          					selectEmail.remove(a,true);
	          				}
	          				templateSubmit_me.removeQueryKey(this.ownerCt);
	          				var b = entertype+':'+staffid+',';
	          				templateSubmit_me.reportObjectId =  templateSubmit_me.reportObjectId.replace(b,"");
          					templateSubmit_me.reportObjectName = templateSubmit_me.reportObjectName.replace(entertype+':'+staffname+',',"");
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
	 * 生成角色表格
	 * @param form
	 * @param action
	 */
	getRoleTableOK:function(form,action){
		var result = Ext.decode(form.responseText);
		var conditions=result.tableConfig;
		var obj = Ext.decode(conditions);
		templateSubmit_me.tableObj = new BuildTableObj(obj);
	},
	/**
	 * 生成角色表格win
	 * @param flag
	 * @param id
	 */
	showroleWin:function(flag){
		templateSubmit_me.actormultiple = false;
		if(flag=='1')
			templateSubmit_me.actormultiple = false;
		else
			templateSubmit_me.actormultiple = true;
		Ext.require('EHR.rolepicker.RolePicker', function(){          
				Ext.create('EHR.rolepicker.RolePicker',{callBackFunc:templateSubmit_me.getRolesList,multiple:templateSubmit_me.actormultiple});
			},this);
	},
	getRolesList:function(records){
	    if(templateSubmit_me.actormultiple==false){//报送 单选
       		templateSubmit_me.actorType = '2';
   			var record =  records[0];
   			var role_id = record.role_id_e;
   			var role_name = record.role_name;
   			templateSubmit_me.actorId =role_id;
           	var selectRole=Ext.getCmp('selectRoleId');
           	var text = selectRole.getEl().query('input');
           	text[0].value=role_name;
       	}else{//邮件抄送 多选
      		var staffname = "";
      		var staffid = "";
			var selectEmail=Ext.ComponentQuery.query("panel[selectEmailId=selectEmailId]")[0];
			for (var i = 0; i < records.length; i++) {
				var record =  records[i];
				staffname = record.role_name;
				staffid = record.role_id_e;
				//28908 linbz 角色名称相同时，应全部显示出来，取消过滤 
//				if(templateSubmit_me.reportObjectName.indexOf(staffname)==-1){
					templateSubmit_me.reportObjectId += "2:"+staffid +",";//这里的id是使用pubfunc加密的，在后台需要进行解密操作
					templateSubmit_me.reportObjectName +="2:"+staffname +",";
					selectEmail.add(templateSubmit_me.createKeyItem('2',staffname,staffid,'0'));
//				}
			}
		}
	},
	/**
	 * 组织单元窗口
	 */
	showorgWin:function(id){
		var map = new HashMap();
	    map.put('codesetidstr','UN,UM,@K');
		map.put('codesource','');
		map.put('nmodule','4');
		map.put('ctrltype','0');//0： 不控制 ；1：管理范围； 2：操作单位； 3：业务范围
		map.put('parentid','');
		map.put('searchtext',encodeURI(""));
		map.put('multiple',false);
		map.put('isencrypt',true);
		map.put('confirmtype','0');
		map.put('title','组织单元');
		map.put('height',330);
		map.put('width',350);
		map.put('callbackfunc',templateSubmit_me.getOrgList);
		Ext.require('EHR.orgTreePicker.OrgTreePicker', function(){          
			Ext.create('EHR.orgTreePicker.OrgTreePicker',{map:map});
		},this);
	},
	getOrgList:function(record){
        	var name = "";
			templateSubmit_me.actorType = '3';
			name  = record[0].text;
			var id = record[0].id;
			var codesetid=record[0].codesetid;
			templateSubmit_me.actorId = codesetid+id;
			var selectRole=Ext.getCmp('selectRoleId');
			var text = selectRole.getEl().query('input');
			text[0].value=name;
    },
	/**
	 * 获取页面值
	 * @param buttontype
	 */
	getPanelValue:function(buttontype){
		var actorId = '';
		var actorType = '';
		var actorName = '';
		var reportObjectId = '';
		var reportObjectName = '';
		var pri = '';
		var content = '';
		var buttonFlag = '';
		if(buttontype=='1'||buttontype=='0'){//报批
			if(this.pagemap.isApply=='1'&&templateSubmit_me.actorId==''){
				templateSubmit_me.submit_EnableButton();
				Ext.showAlert('请选择报送对象！');
				return;
			}
		}
		if(buttontype=='2'){
			if(this.pagemap.isApplyContent=='1'&&Ext.getCmp('contentId').getValue()==''){
				templateSubmit_me.submit_EnableButton();
				Ext.showAlert('请填写驳回原因！');
				return;
			}
		}
		if(this.pagemap.isApply=='1'&&this.pagemap.isPri=='1'){ //报送对象与优先级
			pri=Ext.getCmp('priorityId').getValue();            //优先级
			actorId=templateSubmit_me.actorId;  				//报送对象Id
		    var showRole=Ext.getCmp('selectRoleId');     
		    var text = showRole.getEl().query('input');
		    actorName = text[0].value;                          //报送对象名称
		    if(actorName==''){
		    	actorId = '';
		    }
		    actorType = templateSubmit_me.actorType;
		}
		if(this.pagemap.isApply=='0'&&this.pagemap.isPri=='1'){//只有优先级
			pri=Ext.getCmp('priorityId').getValue();
		}
		if(this.pagemap.isReport=='1'){//抄送对象
			reportObjectId = templateSubmit_me.reportObjectId;          	//邮件抄送Id
			reportObjectName = templateSubmit_me.reportObjectName;
		}
		if(this.pagemap.isApplyContent=='1'){
			content=Ext.getCmp('contentId').getValue();           	//填写意见
		}
	    if(buttontype=='3'){//批准
	        Ext.showConfirm(MB.MSG.SUBMIT_APPLY_INFO,function(value){
	            if(value=="yes"){
		            var map = new HashMap();
		            map.put('actorId',actorId);//报送对象
		            map.put('actorType',actorType);
		            map.put('actorName',actorName);
		            map.put('reportObjectId',reportObjectId);//抄送对象
		            map.put('reportObjectName',reportObjectName);
		            map.put('pri',pri);//优先级
		            map.put('content',content);//意见内容
		            map.put('buttonFlag',buttontype);
		            Ext.callback(eval(templateSubmit_me.afterfunc),null,[map]); 
	            }else{
	            	templateSubmit_me.submit_EnableButton();//bug 34096确认框点击否按钮没有还原 
	            }
	        }); 
        }
	    else if(buttontype=='4'){//同意
	        Ext.showConfirm('此信息将转入下一审批环节，是否确认提交？',function(value){
	            if(value=="yes"){
		            var map = new HashMap();
		            //map.put('actorId',actorId);//报送对象
		           // map.put('actorType',actorType);
		           // map.put('actorName',actorName);
		            map.put('reportObjectId',reportObjectId);//抄送对象
		            map.put('reportObjectName',reportObjectName);
		            map.put('pri',pri);//优先级
		            map.put('content',content);//意见内容
		            map.put('buttonFlag',buttontype);
		            Ext.callback(eval(templateSubmit_me.afterfunc),null,[map]); 
	            }else{
	            	templateSubmit_me.submit_EnableButton();//bug 34096确认框点击否按钮没有还原 
	            }
	        }); 
        }
        else {
	        var map = new HashMap();
	        map.put('actorId',actorId);//报送对象
	        map.put('actorType',actorType);
	        map.put('actorName',actorName);
	        map.put('reportObjectId',reportObjectId);//抄送对象
	        map.put('reportObjectName',reportObjectName);
	        map.put('pri',pri);//优先级
	        map.put('content',content);//意见内容
	        map.put('buttonFlag',buttontype);
	        Ext.callback(eval(templateSubmit_me.afterfunc),null,[map]); 
        }

	    
	},
	/**
	 * 展现选择的邮件抄送人员
	 */
	showEmailWin:function(x,y,items){
		var xx = 0;
		var yy = 0; 
		if(this.pagemap.isApply=='1'&&this.pagemap.isPri=='1'&&this.pagemap.isReport=='1'&&this.pagemap.isApplyContent=='0'){
			xx = x+204;
			yy = y+173;
		}
		else if(this.pagemap.isApply=='0'&&this.pagemap.isPri=='1'&&this.pagemap.isReport=='1'&&this.pagemap.isApplyContent=='0'){
			xx = x+204;
			yy = y+125;
		}
		else if(this.pagemap.isApply=='0'&&this.pagemap.isPri=='1'&&this.pagemap.isReport=='1'&&this.pagemap.isApplyContent=='1'){
			xx = x+204;
			yy = y+95;
		}
		else {
			xx = x+204;
			yy = y+121;
		}
		var emailWin = Ext.getCmp('emailWin');
		if(emailWin==undefined){
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
		}else{
			emailWin.setPagePosition(xx,yy);
		}
		emailWin.removeAll(true);
		items.each(function(r){
			emailWin.add(templateSubmit_me.createKeyItem(r.entertype,r.staffname, r.staffid,'1'));
		});
		emailWin.show();
	},
	//关闭报批窗口
	closeWin:function(){
		var emailWin = Ext.getCmp('emailWin');
		if(emailWin!=undefined){
			emailWin.hide();
		}
		templateSubmit_me.win.close();
		
	},
	//bug 32691 点击报批、继续报批、驳回、同意、取消、确定按钮后将按钮置灰，防止用户重复提交。
	submit_DisableButton:function(){
		var agreeButton=Ext.getCmp("submit_Agree");
		var goApplyButton=Ext.getCmp("submit_GoApply");
		var rejectButton=Ext.getCmp("submit_Reject");
		var applyButton=Ext.getCmp("submit_Apply");
		var confirmButton=Ext.getCmp("submit_Confirm");
		var cancelButton=Ext.getCmp("submit_Cancel");
		if(agreeButton){
			agreeButton.disable();
		}
		if(goApplyButton){
			goApplyButton.disable();
		}
		if(rejectButton){
			rejectButton.disable();
		}
		if(applyButton){
			applyButton.disable();
		}
		if(confirmButton){
			confirmButton.disable();
		}
		if(cancelButton){
			cancelButton.disable();
		}
	},
	submit_EnableButton:function(){
		var agreeButton=Ext.getCmp("submit_Agree");
		var goApplyButton=Ext.getCmp("submit_GoApply");
		var rejectButton=Ext.getCmp("submit_Reject");
		var applyButton=Ext.getCmp("submit_Apply");
		var confirmButton=Ext.getCmp("submit_Confirm");
		var cancelButton=Ext.getCmp("submit_Cancel");
		if(agreeButton){
			agreeButton.enable();
		}
		if(goApplyButton){
			goApplyButton.enable();
		}
		if(rejectButton){
			rejectButton.enable();
		}
		if(applyButton){
			applyButton.enable();
		}
		if(confirmButton){
			confirmButton.enable();
		}
		if(cancelButton){
			cancelButton.enable();
		}
	}
});
