Ext.Loader.loadScript({url:'/components/fileupload/FileUpLoad.js',scope:this});
Ext.Loader.loadScript({url:rootPath+'/components/personPicker/PersonPicker.js'});
//xus 必填项样式 后边加红色的*
Ext.override(Ext.form.field.Base,{
    initComponent:function(){
        if(this.required!==undefined && this.required){
            if(this.fieldLabel){
                this.fieldLabel += '<font color=red>*</font>';
            }
        }
        this.callParent(arguments);
    }
});
Ext.define('EHR.subsetview.SetItemEditor',{
	requires:['EHR.extWidget.field.CodeTreeCombox','EHR.extWidget.field.DateTimeField','EHR.carousel.Carousel','EHR.extWidget.proxy.TransactionProxy'],
	extend:'Ext.panel.Panel',
	xtype:'setitemeditor',
	config:{
//		setName:'A01',
//		nbase:'Usr',
		setName:undefined,
		nbase:undefined,
		currentObject:undefined,
//		currentObject:'qLGNqr8lOKBCzb2rfuiJDAPAATTP3HJDPAATTPPAATTP3HJDPAATTP',
		dataIndex:undefined,
		ctrltype:'1',//权限控制方式 0：不控制 1：人员范围 2：操作单位 3：业务范围（必须设置nmodule值，否则没数据）(机构需要，人员不用)
		nmodule:'0',
		personPickerNbase:undefined, //人员选择组件Nbase
		pickerIsPrivExpression:true, //人员选择组件是否受权限控制
		guidkey:'',// vfs改造上传用
		border:0
	},
	layout:'fit',
	items:[{
		xtype:'form',
		id:'setitemeditormainpanel',
		border:0,
//		cls:'.x-field-required .x-form-label:after{content:"*";display:inline;color:red}',
		layout:{
			type:'vbox',
			align:'stretch'
		},
		buttonAlign:'center',
		items:[{
			xtype:'panel',
			height:30,
			id:'toppanel',
			layout:'hbox',
			padding:'0 0 0 20',
			border:0
//			bodyStyle:'border-width: 0 0 1px 0;border-style: solid;border-color: #c0c0c0;',
		},{
			xtype:'panel',
			id:'fieldItemsPanel',
			flex:1,
//			border:0,
			bodyStyle:'border-width:1px 0 0 0;border-style: solid;border-color: #c0c0c0;',
			scrollable:true,
			defaults:{
				width:250,
				labelWidth:100
			},
			layout:{
				type:'table',
				columns:2,
				tdAttrs:{
					align:'left'
				},
				tableAttrs:{
					width:'100%'
				}
			}
		},{
			xtype:'panel',
			id:'attachment',
			height:35,
			margin:'0 0 0 0',
			border:0,
			layout:{
				type:'vbox',
				align:'stretch'
			},
			items:[{
					xtype:'container',
					style:'border-width: 0 0 1px 0;border-style: solid;border-color: #c0c0c0;',
					padding:'0 0 2 0',
					isShowAttachment:false,
					html:'<b>附件</b><image src="/images/new_module/expand.png" style="position:absolute;bottom:3px;margin-left:5px;cursor:pointer;">',
					listeners : {
		            	el:{
		                	click:function(){
		                		if(this.component.config.isShowAttachment){
		                			arguments[1].src="/images/new_module/expand.png";
		                			this.component.config.isShowAttachment=false;
		                			var attachmentcont=Ext.getCmp('attachmentcont').setHidden(true);
		                			Ext.getCmp('attachment').setHeight(35);
		                		}else{
		                			arguments[1].src="/images/new_module/collapse.png";
		                			this.component.config.isShowAttachment=true;
		                			var attachmentcont=Ext.getCmp('attachmentcont').setHidden(false);
		                			Ext.getCmp('attachment').setHeight(120);
		                		}
		                	}
		            	}
		        	}
				},
//				{
//					//附件列收缩按钮
//					xtype:'image',
//					id:'contractImg',
//					width:85,
//					height:12,
//					style:'border-width:1px 0 0 0;border-style: solid;border-color: #c0c0c0;cursor:pointer;background:url(/components/querybox/images/downbig.png) no-repeat center center;',
//					listeners : {
//		            	el:{
//		                	click:function(){
//		                		var attachmentcont=Ext.getCmp('attachmentcont').setHidden(true);
//		                		Ext.getCmp('contractImg').setHidden(true);
//		                		Ext.getCmp('expandImg').setHidden(false);
//		                		Ext.getCmp('attachment').setHeight(35);
//		                	}
//		            	}
//		        	}
//				},{
//					//附件列展开按钮
//					xtype:'image',
//					id:'expandImg',
//					width:85,
//					height:12,
//					hidden:true,
//					style:'border-width:1px 0 0 0;border-style: solid;border-color: #c0c0c0;cursor:pointer;background:url(/components/querybox/images/upbig.png) no-repeat center center;',
//					listeners : {
//		            	el:{
//		            		click:function(){
//		                		Ext.getCmp('attachmentcont').setHidden(false);
//		                		Ext.getCmp('contractImg').setHidden(false);
//		                		Ext.getCmp('expandImg').setHidden(true);
//		                		Ext.getCmp('attachment').setHeight(120);
//		                	}
//		            	}
//		        	}
//				},
				{
					xtype:'container',
					autoScroll: true,
					hidden:true,
					bodyStyle:'overflow-y:auto;overflow-x:hidden',
					padding:'5 0 0 5',
					id:'attachmentcont',
					layout:'hbox'
				}]
		}],
		buttons:[{
			xtype:'button',
			width:80,
			text:'保存',
			id:'save',
			listeners:{
				click:function(me,e, eOpts ){
					if(me.ownerCt.ownerCt.ownerCt.doSaveValues())
						me.ownerCt.ownerCt.ownerCt.oncomplete('ok');
				}
			}
		},{
			xtype:'button',
			margin:'0 0 0 20',
			id:'continue',
			width:80,
			text:'保存并继续',
			listeners:{
				click:function(me,e, eOpts ){
					if(me.ownerCt.ownerCt.ownerCt.doSaveValues()){
//						me.ownerCt.ownerCt.ownerCt.setCurrentObject('');
						me.ownerCt.ownerCt.ownerCt.resetPage();
						me.ownerCt.ownerCt.ownerCt.initComponent();
						me.ownerCt.ownerCt.ownerCt.oncomplete('continue');
					}	
				}
			}
		},{
			xtype:'button',
			margin:'0 0 0 20',
			width:80,
			text:'取消',
			listeners:{
				click:function(me,e, eOpts ){
					me.ownerCt.ownerCt.ownerCt.oncomplete('cancel');
				}
			}
		}]
	}
		],
	initComponent:function(){
		this.callParent();
		this.initSaveBtn();
		this.initData();
	},
	initSaveBtn:function(){
		//如果是修改，隐藏‘保存并继续按钮’
		if(this.getCurrentObject()&&this.getDataIndex()){
			Ext.getCmp('continue').setHidden(true);
		}else{
			Ext.getCmp('continue').setHidden(false);
		}
	},
	//初始化数据
	initData:function(){
		var me=this;
		var map=new HashMap();
		map.put('setName',this.getSetName());
		map.put('nbase',this.getNbase());
		map.put('currentObject',this.getCurrentObject());
		map.put('dataIndex',this.getDataIndex());
		map.put('personPickerNbase',this.getPersonPickerNbase());
		Rpc({
			functionId : 'ZJ100000251',
			async : false,
			success:function(res){
				var respon = Ext.decode(res.responseText);
				if(respon.flag=="true"){
					if(respon.showfile=='1'){
						Ext.getCmp('attachment').setHidden(false);
					}else{
						Ext.getCmp('attachment').setHidden(true);
					}
					me.setTopPanelItems(respon);
					me.setFieldItemsPanel(respon);
					me.setAttachmentPanel(respon);
				}
			}
		}, map);
		this.doJudgeBtnShow();
	},
	setTopPanelItems:function(respon){
		var me=this;
		var toppanel=Ext.getCmp('toppanel');
		if(this.getCurrentObject()&&this.getCurrentObject()!=''&&this.getDataIndex()&&this.getDataIndex()!=''){
			//修改
			toppanel.add({
				xtype:'container',
				html:respon.itemInfo,
				padding:'5 0 5 0'
			});
		}else{
			//新增
			var title="请选择";
			var settype=me.getSetName().substr(0,1);
			if(settype=='A'){
				title+="人员";
			}else if(settype=='B'){
				title+="机构";
			}else if(settype=='K'){
				title+="岗位";
			}
			
			
//			Ext.define('User', {
//			     extend: 'Ext.data.Model',
//			     fields: [
//			         {name: 'itemid', type: 'string'},
//			         {name: 'itemdesc',  type: 'string'},
//			     ]
//			 });
//			var store=Ext.create('Ext.data.Store', {
////			     model: 'User',
//			     fields: [
//			         {name: 'itemid', type: 'string'},
//			         {name: 'itemdesc',  type: 'string'},
//			     ]
//			 });
			
			if(toppanel.items.items.length<1)
				toppanel.add({
					xtype:'combobox',
					name:'toptextf',
					id:'toptextf',
					width:'50%',
					height:22,
					label:title,
					queryMode: 'local',
					displayField: 'itemdesc',
					emptyText:'请输入姓名',
					autoComplete:true,
					queryCaching:false,
//					queryDelay:1000,
			        valueField: 'itemid',
			        hideTrigger:true,
			        store:{
					     fields: ['itemid','itemdesc']
					 },
			        listeners:{
			        	change:function ( e, newValue, oldValue, eOpts ) {
			        		if(newValue==null)
			        			newValue='';
			        		var map=new HashMap();
			        		map.put('setName',me.getSetName());
			        		map.put('personPickerNbase',me.getPersonPickerNbase());
			        		map.put('cond',newValue);
			        		Rpc({
			        			functionId : 'ZJ100000253',
			        			async : false,
			        			success:function(res){
			        				var respon = Ext.decode(res.responseText);
			        				if(respon.selectionList.length>0){
			        					e.getStore().removeAll(true)
			        					e.getStore().setData(respon.selectionList);
//				        				e.setStore(store);
				        				e.expand();
			        				}
			        			}
			        		}, map);
			        	},
			        	select:function ( e, newValue, oldValue, eOpts ){
			        		me.setNbase(newValue.data.nbase);
			        		me.setCurrentObject(newValue.data.itemid);
			        		me.setGuidkey(newValue.data.guidkey);
			        		// 重新渲染 上传组件
							me.againImgCompFunc();
			        	} 
			        }
				},{
					xtype:'button',
					html:'选择',
					margin:'0 0 0 10',
					height:24,
					listeners:{
						click:function( btn, e, eOpts ) {
							var textfield = Ext.getCmp('toptextf');
							
							var settype=me.getSetName().substr(0,1);
							var addunit=false;
							var adddepartment=false;
							var addpost=false;
							if(settype=='A'){
								var addunit=false;
								var adddepartment=false; 
								var addpost=false;
							}else if(settype=='B'){
								var addunit=false;
								var adddepartment=true; 
								var addpost=false;
							}else if(settype=='K'){
								var addunit=false;
								var adddepartment=false; 
								var addpost=true;
							}
							if(!me.personPicker)
								me.personPicker = new PersonPicker({
									isZoom:true,
									multiple: false,
									addunit:addunit, //是否可以添加单位。Ps：该参数启用时不能选人
									adddepartment:adddepartment, //是否可以添加部门。Ps：该参数启用时不能选人
									addpost:addpost, //是否可以添加岗位。Ps：该参数启用时不能选人
									nbases:me.getPersonPickerNbase(),
									pickerIsPrivExpression: me.getPickerIsPrivExpression(),//是否受权限控制
									callback: function (c) {
										var unit="";
										if(c.unit&&c.unit.length>0){
											unit=c.unit+'/';
										}
										var post="";
										if(c.post&&c.post.length>0){
											post=c.post+'/';
										}
										textfield.setRawValue(unit+post+c.name);
										//xus 19/7/22 【50647】v76.1：证照管理/档案管理，新建，点击选择按钮引人，引入的人员姓名显示为灰色，没有高亮显示，与手动输入后选人的样式不一致
										textfield.inputEl.focus();
										me.setCurrentObject(c.id);
										me.setNbase(c.nbase);
										me.setGuidkey(c.guidkey_str);
										// 重新渲染 上传组件
										me.againImgCompFunc();
									}
								},textfield.el.dom);
							me.personPicker.open();
						}
					}
				});
			
			
//			
//			Ext.define('User', {
//			     extend: 'Ext.data.Model',
//			     fields: [
//			         {name: 'itemid', type: 'string'},
//			         {name: 'itemdesc',  type: 'string'},
//			     ]
//			 });
//			var store=Ext.create('Ext.data.Store', {
//			     model: 'User',
//			     autoLoad:true,
//			     async : false,
//			    // data :[]
//			     proxy:{
//			    	 
//			    	 type:'transaction',
//			    	 functionId:'ZJ100000253',
//			    	 reader:{
//			               type:'json',
//			               root:'selectionList'
//			            },
//			         extraParams:{
//			    		 setName:me.getSetName(),
//			    		 personPickerNbase:me.getPersonPickerNbase(),
////			    		 setName:me.getSetName(),
//			    	 }
//			     }
//			   //  data :respon.selectionList
//			 });
//			
//			if(toppanel.items.items.length<1)
//				toppanel.add({
//					xtype:'combobox',
//					name:'toptextf',
//					id:'toptextf',
//					width:'50%',
//					label:title,
//					queryMode: 'remote',
////					queryMode: 'local',
//					displayField: 'itemdesc',
//					autoComplete:true,
//					queryCaching:false,
//					queryDelay:1000,
//			        valueField: 'itemid',
//			        hideTrigger:true,
//			        store:store,
//			        listeners:{
////			        	change:function ( e, newValue, oldValue, eOpts ) {
////			        		e.getStore().load();
////			        		e.expand();
////			        	},
////			        	change:function ( e, newValue, oldValue, eOpts ) {
////			        		var map=new HashMap();
////			        		map.put('setName',me.getSetName());
////			        		map.put('personPickerNbase',me.getPersonPickerNbase());
////			        		map.put('cond',newValue);
////			        		Rpc({
////			        			functionId : 'ZJ100000253',
////			        			async : false,
////			        			success:function(res){
////			        				var respon = Ext.decode(res.responseText);
////			        				store.setData(respon.selectionList);
////			        				e.setStore(store);
//////			        				e.expand();
////			        			}
////			        		}, map);
////			        	},
//			        	select:function ( e, newValue, oldValue, eOpts ){
//			        		console.log(e);
//			        		me.setCurrentObject(newValue.data.itemid);
//			        	} 
//			        }
//				},{
//					xtype:'button',
//					html:'选择',
//					margin:'0 0 0 10',
//					listeners:{
//						click:function( btn, e, eOpts ) {
//							var textfield = Ext.getCmp('toptextf');
//							
//							var settype=me.getSetName().substr(0,1);
//							var addunit=false;
//							var adddepartment=false;
//							var addpost=false;
//							if(settype=='A'){
//								var addunit=false;
//								var adddepartment=false; 
//								var addpost=false;
//							}else if(settype=='B'){
//								var addunit=false;
//								var adddepartment=true; 
//								var addpost=false;
//							}else if(settype=='K'){
//								var addunit=false;
//								var adddepartment=false; 
//								var addpost=true;
//							}
//							if(!me.personPicker)
//								me.personPicker = new PersonPicker({
//									isZoom:true,
//									multiple: false,
//									addunit:addunit, //是否可以添加单位。Ps：该参数启用时不能选人
//									adddepartment:adddepartment, //是否可以添加部门。Ps：该参数启用时不能选人
//									addpost:addpost, //是否可以添加岗位。Ps：该参数启用时不能选人
//									nbases:me.getPersonPickerNbase(),
//									pickerIsPrivExpression: me.getPickerIsPrivExpression(),//是否受权限控制
//									callback: function (c) {
//										var unit="";
//										if(c.unit&&c.unit.length>0){
//											unit=c.unit+'/';
//										}
//										var post="";
//										if(c.post&&c.post.length>0){
//											post=c.post+'/';
//										}
//										textfield.setValue(unit+post+c.name);
//										me.setCurrentObject(c.id);
//										me.setNbase(c.nbase);
//									}
//								},textfield.el.dom);
//							me.personPicker.open();
//						}
//					}
//				});

//			if(toppanel.items.items.length<1)
//				toppanel.add({
//					xtype:'textfield',
//					name:'toptextf',
//					id:'toptextf',
//					width:'50%',
//					emptyText:title
//				},{
//					xtype:'button',
//					html:'查询',
//					margin:'0 0 0 10',
//					listeners:{
//						click:function( btn, e, eOpts ) {
//							var textfield = Ext.getCmp('toptextf');
//							
//							var settype=me.getSetName().substr(0,1);
//							var addunit=false;
//							var adddepartment=false;
//							var addpost=false;
//							if(settype=='A'){
//								var addunit=false;
//								var adddepartment=false; 
//								var addpost=false;
//							}else if(settype=='B'){
//								var addunit=false;
//								var adddepartment=true; 
//								var addpost=false;
//							}else if(settype=='K'){
//								var addunit=false;
//								var adddepartment=false; 
//								var addpost=true;
//							}
//							if(!me.personPicker)
//								me.personPicker = new PersonPicker({
//									isZoom:true,
//									multiple: false,
//									addunit:addunit, //是否可以添加单位。Ps：该参数启用时不能选人
//									adddepartment:adddepartment, //是否可以添加部门。Ps：该参数启用时不能选人
//									addpost:addpost, //是否可以添加岗位。Ps：该参数启用时不能选人
//									nbases:me.getPersonPickerNbase(),
//									pickerIsPrivExpression: me.getPickerIsPrivExpression(),//是否受权限控制
//									callback: function (c) {
//										var unit="";
//										if(c.unit&&c.unit.length>0){
//											unit=c.unit+'/';
//										}
//										var post="";
//										if(c.post&&c.post.length>0){
//											post=c.post+'/';
//										}
//										textfield.setValue(unit+post+c.name);
//										me.setCurrentObject(c.id);
//										me.setNbase(c.nbase);
//									}
//								},textfield.el.dom);
//							me.personPicker.open();
//							var input = $(".PersonPicker-Main-Keyword input")[0];
//							var arr=textfield.getValue().split('/');
//							input.value = arr[arr.length-1];
//						}
//					}
//				});
			
			//原代码为 超链接，现改为输入框，代码保留
//			var title="<font style='color:blue;cursor:pointer;'>请选择";
//			var settype=me.getSetName().substr(0,1);
//			if(settype=='A'){
//				title+="人员";
//			}else if(settype=='B'){
//				title+="机构";
//			}else if(settype=='K'){
//				title+="岗位";
//			}
//			title+="</font>";
//			toppanel.add({
//				xtype:'container',
//				name:'toptextf',
//				id:'toptextf',
//				width:'40%',
//				html:title,
//				padding:'2 0 2 0',
//				listeners:{
//					'afterrender': function(text) {
//						text.el.on('click',function( e, event, eOpts ) {
//							var settype=me.getSetName().substr(0,1);
//							var addunit=false;
//							var adddepartment=false;
//							var addpost=false;
//							if(settype=='A'){
//								var addunit=false;
//								var adddepartment=false; 
//								var addpost=false;
//							}else if(settype=='B'){
//								var addunit=false;
//								var adddepartment=true; 
//								var addpost=false;
//							}else if(settype=='K'){
//								var addunit=false;
//								var adddepartment=false; 
//								var addpost=true;
//							}
//							me.personPicker = new PersonPicker({
//								isZoom:true,
//								multiple: false,
//								addunit:addunit, //是否可以添加单位。Ps：该参数启用时不能选人
//								adddepartment:adddepartment, //是否可以添加部门。Ps：该参数启用时不能选人
//								addpost:addpost, //是否可以添加岗位。Ps：该参数启用时不能选人
//								nbases:me.getPersonPickerNbase(),
//								pickerIsPrivExpression: me.getPickerIsPrivExpression(),//是否受权限控制
//								callback: function (c) {
//									var unit="";
//									if(c.unit&&c.unit.length>0){
//										unit=c.unit+'/';
//									}
//									var post="";
//									if(c.post&&c.post.length>0){
//										post=c.post+'/';
//									}
//									text.setHtml("<font style='color:blue;cursor:pointer;'>"+unit+post+c.name+"</font>");
//									me.setCurrentObject(c.id);
//									me.setNbase(c.nbase);
//								}
//							}, e.target);
//							me.personPicker.open();
//						});
//					}
//				}
//			});
		}
	},
	setFieldItemsPanel:function(respon){
		this.oldValue = new HashMap();
		
		var fieldItemsPanel=Ext.getCmp('fieldItemsPanel');
		fieldItemsPanel.removeAll ( true );
		//判断是否是同一行第一个子集
		var isFirstItem=false;
		var linePanel;
		this.respon=respon;
		for(var i=0;i<respon.fieldList.length;i++){
			
			var field=respon.fieldList[i];
			this.oldValue.put(field.itemid,field.value);
			var margin="5 0 5 0";
			isFirstItem=!isFirstItem;
			if(field.itemtype=="A"){
				if(field.codesetid=="0"){
					fieldItemsPanel.add({
						xtype:'textfield',
						margin:margin,
						name:field.itemid,
						maxLength:field.itemlength,
//						allowBlank:field.allowblank,
						required:field.allowblank,
						fieldLabel:field.itemdesc,
						border:0,
						labelAlign:'right',
						labelWrap:true,
						clearIcon:true,
						value:field.value,
						validateValue: function(value){
							if(Ext.getStringByteLength(value) > this.maxLength){
								this.setActiveError( '该输入项的最大长度是'+this.maxLength+'个字符' );
							}else{
								this.unsetActiveError( );
							}
							return Ext.getStringByteLength(value) <= this.maxLength;
						},
						listeners:{
							change:function( me, newValue, oldValue, eOpts ) {
								me.ownerCt.ownerCt.ownerCt.doJudgeBtnShow();
							}
						}
					});
				}else{
					fieldItemsPanel.add({
						xtype:'codecomboxfield',
						margin:margin,
						name:field.itemid,
						required:field.allowblank,
						ctrltype:this.getCtrltype(),
						nmodule:this.getNmodule(),
//						allowBlank : false, 
						fieldLabel:field.itemdesc,
						codesetid:field.codesetid,
						onlySelectCodeset:false,
						border:0,
						labelAlign:'right',
						labelWrap:true,
						clearIcon:true,
						value:field.value,
						listeners:{
							change:function( me, newValue, oldValue, eOpts ) {
								me.ownerCt.ownerCt.ownerCt.doJudgeBtnShow();
							}
						}
					});
				}
			}else if(field.itemtype=="M"){
				if(!isFirstItem){
					fieldItemsPanel.add({
						xtype:'container'
					})
				};
				fieldItemsPanel.add({
					xtype:'textarea',
					name:field.itemid,
					colspan:2,
					style:'',
					required:field.allowblank,
					maxLength:field.itemlength==10?Number.MAX_VALUE:field.itemlength,
//					allowBlank:field.allowblank,
					fieldLabel:field.itemdesc,
					msgTarget :'under',
					border:0,
					width:'90%',
					labelAlign:'right',
					labelWrap:true,
					clearIcon:true,
					value:field.value,
					listeners:{
						change:function( me, newValue, oldValue, eOpts ) {
							me.ownerCt.ownerCt.ownerCt.doJudgeBtnShow();
						}
					}
				});
				isFirstItem=false;
			}else if(field.itemtype=="N"){
				fieldItemsPanel.add({
					xtype:'numberfield',
					margin:margin,
					name:field.itemid,
					maxLength:field.itemlength,
//					allowBlank:field.allowblank,
					required:field.allowblank,
					fieldLabel:field.itemdesc,
					decimalPrecision:field.demicallength,
					border:0,
					labelAlign:'right',
					labelWrap:true,
					clearIcon:true,
					value:field.value,
					listeners:{
						change:function( me, newValue, oldValue, eOpts ) {
							me.ownerCt.ownerCt.ownerCt.doJudgeBtnShow();
						}
					}
				});
			}else if(field.itemtype=="D"){
				var format='Y';
				if(field.itemlength=='4'){
					format='Y';
				}else if(field.itemlength=='7'){
					format='Y-m';
				}else if(field.itemlength=='10'){
					format='Y-m-d';
				}else if(field.itemlength=='16'){
		          	format = 'Y-m-d H:i';
				}else if(field.itemlength=='18'){
		            format = 'Y-m-d H:i:s';
				}
				fieldItemsPanel.add({
					xtype:'datetimefield',
					margin:margin,
					name:field.itemid,
//					allowBlank:field.allowblank,
					required:field.allowblank,
					fieldLabel:field.itemdesc,
					format:format,
					border:0,
					labelAlign:'right',
					labelWrap:true,
					clearIcon:true,
					value:field.value,
					listeners:{
						change:function( me, newValue, oldValue, eOpts ) {
							me.ownerCt.ownerCt.ownerCt.doJudgeBtnShow();
						}
					}
				});
			}else{
				fieldItemsPanel.add({
					xtype:'textfield',
					margin:margin,
					name:field.itemid,
					maxLength:field.itemlength,
//					allowBlank:field.allowblank,
					required:field.allowblank,
					fieldLabel:field.itemdesc,
					border:0,
					labelAlign:'right',
					labelWrap:true,
					clearIcon:true,
					value:field.value,
					validateValue: function(value){
						if(Ext.getStringByteLength(value) > this.maxLength){
							this.setActiveError( '该输入项的最大长度是'+this.maxLength+'个字符' );
						}else{
							this.unsetActiveError( );
						}
						return Ext.getStringByteLength(value) <= this.maxLength;
					},
					listeners:{
						change:function( me, newValue, oldValue, eOpts ) {
							me.ownerCt.ownerCt.ownerCt.doJudgeBtnShow();
						}
					}
				});
			}
		}
	},
	//加载附件
	setAttachmentPanel:function(respon){
		var me = this;
		var attachment=Ext.getCmp('attachmentcont');
		var html='';
		var showName='';
		me.addList=[];
		me.delList=[];
		me.fileList=[];
		me.guidkey = respon.guidkey;
		this.addSingleDev(respon.fileList);
	},
	addSingleDev:function(fileList){
		var me=this;
		var attachment=Ext.getCmp('attachmentcont');
		if(attachment.items.items.length>0){
			attachment.remove(attachment.items.items[attachment.items.items.length-1]);
		}
		for(var i=0;i<fileList.length;i++){
			var srcfilename = fileList[i].srcfilename;
			var showName = srcfilename;
			if(showName.length>6){
				showName=showName.substr(0,6)+'...';
			}
			var src='';
			
			if(fileList[i].fileext=='.jpg'||fileList[i].fileext=='.JPG'||fileList[i].fileext=='.jpeg'||fileList[i].fileext=='.png'||fileList[i].fileext=='.bmp'){
				src="/images/img.png";
			}else if(fileList[i].fileext=='.doc'||fileList[i].fileext=='.docx'){
				src = "/images/word.png";
			}else if(fileList[i].fileext=='.xls'||fileList[i].fileext=='.xlsx'){
				src = "/images/excell.png";
			}else if(fileList[i].fileext=='.ppt'||fileList[i].fileext=='.pptx'){
				src = "/images/ppt.png";
			}else if(fileList[i].fileext=='.pdf'){
				src = "/images/PDF.png";
			}else if(fileList[i].fileext == ".zip" || fileList[i].fileext == ".rar")
             	src = "/images/zip.png";
			else if(fileList[i].fileext == ".txt")
             	src = "/images/txt.png";
			else{
				src='/images/othertype.png';
			}
			attachment.add({
				xtype:'panel',
				height:80,
				width:80,
				dataInfo:fileList[i],
				srcfilename:srcfilename,
				layout:{
					type:'vbox',
					align:'middle'
				},
				border:0,
				html:'<img functype="del" src="/components/homewidget/images/del.png" style="display:none;position:relative;top:0px;left:-5px;width:18px;height:18px;float:right;">',
				items:[{
					xtype:'image',
					width:50,
					height:50,
					src:src
				},{
					xtype:'component',
					html:'<span style="cursor:default;" title="'+srcfilename+'">'+showName+'</span>'
				}],
				listeners:{
					'afterrender': function(panel) {
						//失焦隐藏删除图标
						panel.el.on('mouseleave',function( e, event, eOpts ) {
							event.getElementsByTagName('img')[1].style.display="none";
						});
						//聚焦显示删除图标
						panel.el.on('mouseover',function( e, event, eOpts ) {
							if(event.getElementsByTagName('img').length==0)
								return;
							event.getElementsByTagName('img')[1].style.display="block";
						});
						panel.el.on('click', function(evt,menuTable) {
							if(evt.target.getAttribute('funcType')=='del'){
								Ext.Msg.confirm('提示信息','确认要删除附件吗？',function(op){
									if(op == 'yes'){
										panel.dataInfo.action='delete';
										var existFlag=false;
										for(var i=0;i<me.fileList.length;i++){
											if(me.fileList[i].filename==panel.dataInfo.filename&&me.fileList[i].fileid==panel.dataInfo.fileid){
												me.fileList.splice(i,1);;
												existFlag=true;
												break;
											}
										}
										me.delList.push(panel.dataInfo);
										if(!existFlag)
											me.fileList.push(panel.dataInfo);
										panel.ownerCt.remove(panel);
									}else{
										return;
									}
								});
							}else{
								//下载
								Ext.Msg.confirm('提示信息','确认下载此文件？',function(op){
									if(op == 'yes'){
										var win=open("/servlet/vfsservlet?fileid="+panel.dataInfo.fileid+"&fromjavafolder=true");
//										var existFlag=false;
//										for(var i=0;i<me.fileList.length;i++){
//											if(me.fileList[i].filename==panel.dataInfo.filename&&me.fileList[i].filepath==panel.dataInfo.filepath){
//												me.fileList.splice(i,1);;
//												existFlag=true;
//												break;
//											}
//										}
//										if(existFlag)
//											var win=open("/servlet/DisplayOleContent?openflag=true&filename="+panel.dataInfo.filename);
//										else
//											var win=open("/servlet/DisplayOleContent?openflag=true&filePath="+panel.dataInfo.filepath);
									}else{
										return;
									}
								});
							}
						});
//						//悬停显示附件全名功能
//						var tip = Ext.create('Ext.tip.ToolTip', {
//						    target: panel.el,
//						    html:panel.srcfilename
//						});
					},
					scope:this
				}
			})
		}
		// 上传 加号图片 渲染方法
		attachment.add(me.getFileUpLoadObj());
	},
	doSaveValues:function(){
		var me = this;
		var flag=false;
		if(this.getCurrentObject()==null||this.getCurrentObject()==''){
			if(this.getSetName().substr(0,1)=='A')
				Ext.MessageBox.alert('提示信息','请选中人员');
			else if(this.getSetName().substr(0,1)=='B')
				Ext.MessageBox.alert('提示信息','请选中机构');
			else if(this.getSetName().substr(0,1)=='K')
				Ext.MessageBox.alert('提示信息','请选中岗位');
			return flag;
		}
		
		var saveData = Ext.getCmp('setitemeditormainpanel').getValues();
		delete saveData['toptextf'];
		
		var returnValue = this.fireEvent("beforesave",me,saveData,this.oldValue);
		if(returnValue===false)
			return flag;
		
		
		var map=new HashMap();
		map.put('setName',this.getSetName());
		map.put('nbase',this.getNbase());
		map.put('currentObject',this.getCurrentObject());
		map.put('saveData',saveData);
		map.put('dataIndex',this.getDataIndex());
		map.put('fileList',this.fileList);
		Rpc({
			functionId : 'ZJ100000252',
			async : false,
			success:function(res){
				var respon = Ext.decode(res.responseText);
				if(respon.flag=="true")
					flag=true;
				else
					Ext.MessageBox.alert('提示信息',respon.message);
			}
		}, map);
		return flag;
	},
	doJudgeBtnShow:function(){
		if(Ext.getCmp('setitemeditormainpanel').form.isValid()){
			if(!this.getCurrentObject()||!this.getDataIndex())
				Ext.getCmp('continue').setDisabled(false);
			Ext.getCmp('save').setDisabled(false);
        }else{
        	if(!this.getCurrentObject()||!this.getDataIndex())
				Ext.getCmp('continue').setDisabled(true);
        	Ext.getCmp('save').setDisabled(true);
        }
	},
	//回调方法
	oncomplete:function(action){
	      if(action=='ok')
	    	  Ext.MessageBox.alert('提示信息','点击保存按钮');
	      else if(action=='cancel')
	    	  Ext.MessageBox.alert('提示信息','点击取消按钮');
	      else if(action=='continue')
	    	  Ext.MessageBox.alert('提示信息','点击保存并继续');
	},
	//校验方法
	onValidate:function(savedata){
		return undefined;
	},
	//保存并继续清空页面值
	resetPage:function(){
		//原代码为 超链接，现改为输入框，代码保留
//		var title="<font style='color:blue;cursor:pointer;'>请选择";
//		var settype=this.getSetName().substr(0,1);
//		if(settype=='A'){
//			title+="人员";
//		}else if(settype=='B'){
//			title+="机构";
//		}else if(settype=='K'){
//			title+="岗位";
//		}
//		title+="</font>";
//		Ext.getCmp('toptextf').setHtml(title);
		Ext.getCmp('toptextf').clearValue();
		var attachment=Ext.getCmp('attachmentcont');
		if(attachment)
			attachment.removeAll(true);
	},
	/**
	 * 获取上传组件
	 */
	getFileUpLoadObj:function(){
		var me = this;
		var imgComp = Ext.create("Ext.container.Container",{
			id: 'imgCompid',
			html:'<div style="width=80px;height=60px;margin:10px 0px 0px 0px;" ><img src="/images/new_module/nocycleadd.png" style="width=50px;height=50px;"/></span><div>',
			height:60,
			width:80,
			listeners:{
				click:{
					element:'el',
					fn:function(){
						//上传控件
					   	var uploadObj = Ext.create("SYSF.FileUpLoad",{
					   		isTempFile:false,
							VfsModules:VfsModulesEnum.YG,
							VfsFiletype:VfsFiletypeEnum.multimedia,
							VfsCategory:VfsCategoryEnum.personnel,
							CategoryGuidKey:me.guidkey,
								upLoadType:1,
//								fileExt:"*.xls;*.xlsx;*.doc;*.docx;*.ppt;*.pptx;*.pdf;*.txt;*.jpg;*.jpeg;*.png;*.bmp;",
								cls:'',
								height: 30,
								//回调方法，失败
								error:function(){
									Ext.showAlert(common.msg.uploadFailed+"！");
								},
								success:function(list){
									if(list[0].successed==false)
										return;
					                var filename = list[0].filename;
//					                var filePath= list[0].path;
					                var fileid= list[0].fileid;
					                var srcfilename=list[0].localname;
					                var splitarray=srcfilename.split(".");
					                var ext="."+splitarray[splitarray.length-1];
					                var singeladddata={"filename":filename,"fileid":fileid,"fileext":ext,"action":"add","srcfilename":srcfilename};
					                me.addSingleDev([singeladddata]);
					                me.addList.push(singeladddata);
					                me.fileList.push(singeladddata);
					                win.close();
								}
							});

						var win=Ext.widget("window",{
				   			title: "上传附件",
				            modal:true,
				            border:false,
				        	width:380,
				   			height: 120,
							bodyStyle : 'background-color:#FFFFFF',
				            closeAction:'destroy',
				            items:[{
				                xtype: 'panel',
				                border:false,
				         		layout:{  
					             	type:'vbox',  
					             	padding:'15 0 0 35', //上，左，下，右 
					             	pack:'center',  
					              	align:'middle'  
					            },
				                items:[uploadObj]
				            }]
				    	}); 
						win.show();
					}
				}
			}
		});
		
		return imgComp;
	},
	/**
	 * 选人后重新渲染 上传组件
	 */
	againImgCompFunc:function(){
		var me = this;
		var imgComp = Ext.getCmp('imgCompid');
		if(imgComp){
			imgComp.destroy();
		}
		var attachment=Ext.getCmp('attachmentcont');
		if(attachment){
			attachment.add(me.getFileUpLoadObj());
		}
	}
})
