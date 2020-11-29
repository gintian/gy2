/**
 * 公告维护
 * 
 * @createtime Mar 02, 2017 9:07:55 AM
 * @author chent
 * 
 * API：
 * 调用方式：		
	Ext.require('NoticePath.Notice',function(){
		Ext.create('NoticePath.Notice', {
			title : '',notice_name : '',notice_content : '',notice_time : '5',notice_seq : '1',notice_object : ''
		});
	});
 * 参数说明：
	title：弹出框标题,默认:公告维护
	notice_name：公告主题
	notice_content：内容
	notice_time：公告期
	notice_seq：优先级
	notice_object: 通知对象
	Approved : 是否直接批准。默认：否
	default_notice_object：默认通知对象。array里装hashmap。map：id（人员id/角色id/机构id）、name（显示文字）、type（person/role/org）、orgpre(机构时传此参数。UN/UM/@K)
 * 
 * */
Ext.define('NoticePath.Notice', {
	title : '公告维护',
	notice_name : '',
	notice_content : '',
	notice_time : '',
	notice_seq : '',
	notice_object : '',
	isApproved : false,
	default_notice_object:[],
	//selected_notice_object:[],
	width:800,
	height:500,
	notice_select : true,//是否显示通知对象
	flag : 1,//通知类型 1 ehr系统公告栏 2 招聘首页公告 3 社会招聘公告 4 校园招聘公告 11 培训新闻13外网公示信息
	groupData : '',
	constructor : function(config) {
		Notice_global = this;
		
		this.title = Ext.isEmpty(config.title) ? '选择指标' : config.title;
		this.notice_name = Ext.isEmpty(config.notice_name) ? '' : config.notice_name;
		this.notice_content = Ext.isEmpty(config.notice_content) ? '' : config.notice_content;
		this.notice_time = Ext.isEmpty(config.notice_time) ? '' : config.notice_time;
		this.notice_seq = Ext.isEmpty(config.notice_seq) ? '' : config.notice_seq;
		this.notice_object = Ext.isEmpty(config.notice_object) ? '' : config.notice_object;
		this.isApproved = !!config.isApproved;
		this.default_notice_object = Ext.isEmpty(config.default_notice_object) ? [] : config.default_notice_object;
		this.height = Ext.isEmpty(config.height) ? 500 : config.height;
		this.notice_select = Ext.isEmpty(config.notice_select) ? true : config.notice_select;
		this.flag = Ext.isEmpty(config.flag) ? 1 : config.flag;
		this.groupData = Ext.isEmpty(config.groupData) ? '' : config.groupData;
		this.selected_notice_object = new Array();
		this.unitIds = Ext.isEmpty(config.unitIds) ? "" : config.unitIds;	//机构范围控制（按照“业务范围>操作单位>人员范围”）
		this.saveAfterCallBack=config.saveAfterCallBack,//保存成功后回调函数
		this.init();
	},
	// 初始化
	init : function() {
		var noticePanel = this.getFormPanel();
		if(this.notice_select)
			this.addNotice(noticePanel);
		
		Ext.create('Ext.window.Window', {
					id : 'NoticeWin',
					modal : true,
					title : this.title,
					modal : true,
					layout : 'fit',
					width : this.width,
					height : this.height,
					border : false,
					resizable : false,
					items : [noticePanel],
					listeners:{
						beforeshow:function(){//默认通知对象
							var newValue = "";
							var len = this.default_notice_object.length
							if(len > 0){
								for(var i=0; i<len; i++){
									var object = this.default_notice_object[i];
									var id = object.id;
									var name = object.name;
									var type = object.type;
									var orgpre = object.orgpre;
									newValue = this.addNoticeObject(id, name, type, orgpre);
								}
								Notice_global.notic_object_orgin_value = newValue;
								Notice_global.notic_object_orgin_object = Notice_global.selected_notice_object;
							}
						},
						scope:this
					}
				}).show();

	},
	// 创建表单
	getFormPanel : function() {

		var CKEditor = Ext.create("EHR.ckEditor.CKEditor", {
					id : 'ckeditorid',
					ckEditorConfig:{
						toolbarGroups:[
							{ name: 'document', groups: [ 'mode', 'document', 'doctools' ] },
							{ name: 'clipboard', groups: [ 'clipboard', 'undo' ] },
							{ name: 'editing', groups: [ 'find', 'selection', 'spellchecker', 'editing' ] },
							{ name: 'forms', groups: [ 'forms' ] },
							{ name: 'basicstyles', groups: [ 'basicstyles', 'cleanup' ] },
							{ name: 'paragraph', groups: [ 'list', 'indent', 'blocks', 'align', 'bidi', 'paragraph' ] },
							{ name: 'links', groups: [ 'links' ] },
							{ name: 'insert', groups: [ 'insert' ] },
							{ name: 'styles', groups: [ 'styles' ] },
							{ name: 'colors', groups: [ 'colors' ] },
							{ name: 'tools', groups: [ 'tools' ] },
							{ name: 'others', groups: [ 'others' ] },
							{ name: 'about', groups: [ 'about'] }
						],
						removeButtons:'Templates,Save,NewPage,DocProps,Preview,Print,Cut,Copy,Paste,PasteText,PasteFromWord,Scayt,Form,Checkbox,Radio,TextField,Textarea,Select,Button,ImageButton,HiddenField,SelectAll,Anchor,About,SpecialChar,Flash,Iframe,Language'
					},
					value : this.notice_content
				});

		var labelWidth = 80;
		var textfieldWidth = 300;
		// 编辑窗口
		var formPanel = Ext.create('Ext.form.Panel', {
			id : 'formpanel',
			bodyPadding : '0 20 0 20',
			border : false,
			layout : {
				type : 'table',
				columns : 2
			},
			items : [{
				xtype : 'container',
				width : labelWidth,
				items : [{
							xtype : 'label',
							html : '<font style="color:red; width:100px;"> * </font>主题'
						}]
			}, {
				xtype : 'textfield',
				id : 'notice_name',
				labelSeparator : '',
				maxLength : 50,
				allowBlank : false,
				width : textfieldWidth,
				value : this.notice_name
			}, {
				xtype : 'container',
				width : labelWidth,
				items : [{
							xtype : 'label',
							html : '<font style="color:red; width:100px;"> * </font>内容'
						}]
			}, {
				xtype : 'panel',
				id:'ckeditorpanel',
				layout : 'fit',
				width : this.width - 150,
				height : 300,
				border : true,
				margin : '0 0 10 0',
				items : [CKEditor]
			}, {
				xtype : 'container',
				width : labelWidth,
				items : [{
					xtype : 'label',
					html : '<font style="color:red; width:100px;"> * </font>公告期(天)'
				}]
			}, {
				xtype : 'textfield',
				id : 'notice_time',
				labelSeparator : '',
				maxLength : 50,
				allowBlank : false,
				width : textfieldWidth,
				value : this.notice_time,
				regex : /^([1-9]\d*|[0]{1,1})$/,
				regexText : '必须为整数'
			}, {
				xtype : 'container',
				width : labelWidth,
				items : [{
					xtype : 'label',
					html : '<font style="color:red; width:100px;"> * </font>优先级'
				}]
			}, {
				xtype : 'container',
				width : 650,
				layout : 'hbox',
				margin : '0 0 10 0',
				items : [{
							xtype : 'textfield',
							id : 'notice_seq',
							labelSeparator : '',
							maxLength : 50,
							allowBlank : false,
							width : textfieldWidth,
							value : this.notice_seq,
							regex : /^[1-9]\d*$/,
							regexText : '必须为正整数'
						}, {
							xtype : 'label',
							margin: '2 0 0 5',
							text : '优先级越小，排列顺序越靠前'
						}]
			}],
			buttonAlign : 'center',
			buttons : [{
						text : '保存',
						handler : function() {
							var form = Ext.getCmp('formpanel').getForm();
							var cktext = Ext.getCmp('ckeditorid').getText().replace(/\n/g,"");
							
							if(form.isValid() && !Ext.isEmpty(cktext)){
								
								Ext.showConfirm('您确定保存并发布通知？', function(btn){
									if(btn == 'yes'){
										if(Ext.isEmpty(this.groupData)){
											var values = form.getValues();
											
											var map = new HashMap();
											map.put('notice_name', values['notice_name-inputEl']);//主题
											//加密后的的特殊字符，使用前需要解密。解决 websphere环境报错的问题  郝树林 2017-06-24 update
											map.put('notice_content',getEncodeStr(Ext.getCmp('ckeditorid').getHtml()));//内容
											map.put('notice_time', values['notice_time-inputEl']);//公告期
											map.put('notice_seq', values['notice_seq-inputEl']);//优先级
											map.put('notice_object', this.selected_notice_object);//通知对象
											map.put('isApproved', this.isApproved);//是否直批
											map.put('flag', this.flag);//通知类型
											Rpc({functionId:'ZJ100000153',async:false,success:function(response) {
												var result = Ext.decode(response.responseText);
												var errorcode = result.errorcode;
												if (errorcode == '1') {
													Ext.showAlert('保存失败！');
												} else if (errorcode == '0') {//根据提出的缺陷，不需要提示该保存成功，显得繁琐
													//Ext.showAlert('保存成功！', function(){
													Ext.getCmp('NoticeWin').close();
													if(Notice_global.saveAfterCallBack){
														Ext.callback(eval(Notice_global.saveAfterCallBack), null, [map]);
													}
													//});
													
												}
											},scope:this},map);
										}else{
											var values = form.getValues();
											var searchInfo = this.groupData.searchInfo;
											var groupDesc = this.groupData.groupDesc;
											var groupKey = this.groupData.groupKey;
											var titleTemp = values['notice_name-inputEl'];
											if(titleTemp.indexOf('（'+groupDesc[groupKey[0]]+'）')>0)
												titleTemp = titleTemp.substring(0,titleTemp.indexOf('（'+groupDesc[groupKey[0]]+'）'));
											var tempContent = Ext.getCmp('ckeditorid').getHtml();
											var contentStart =  tempContent.substring(0,tempContent.indexOf('<div id="replaceStart"'));
											var contentEnd = tempContent.substring(tempContent.indexOf('<div id="replaceEnd"'));
											contentEnd = contentEnd.substring(contentEnd.indexOf('</div>'));
											for(var i = 0;i<groupKey.length;i++){
												var key = groupKey[i];
												var map = new HashMap();
												var title = titleTemp;
												if(groupDesc[key])
													title +="（"+groupDesc[key]+"）";
												map.put('notice_name', title);//主题
												//加密后的的特殊字符，使用前需要解密。解决 websphere环境报错的问题  郝树林 2017-06-24 update
//												map.put('notice_content',getEncodeStr(Ext.getCmp('ckeditorid').getHtml()));//内容
												map.put('notice_time', values['notice_time-inputEl']);//公告期
												map.put('notice_seq', values['notice_seq-inputEl']);//优先级
												map.put('notice_object', new Array());//通知对象
												map.put('isApproved', this.isApproved);//是否直批
												map.put('flag', this.flag);//通知类型
												map.put('notice_content', getEncodeStr(contentStart+searchInfo[key])+contentEnd);
												Rpc({functionId:'ZJ100000153',async:false,success:function(response) {
													var result = Ext.decode(response.responseText);
													var errorcode = result.errorcode;
													if (errorcode == '1') {
														Ext.showAlert('保存失败！');
													}else{
														if(Notice_global.saveAfterCallBack){
															Ext.callback(eval(Notice_global.saveAfterCallBack), null, []);
														}
													}
												},scope:this},map);
											}
											if(groupKey.length==0){
												var map = new HashMap();
												map.put('notice_name', values['notice_name-inputEl']);//主题
												//加密后的的特殊字符，使用前需要解密。解决 websphere环境报错的问题  郝树林 2017-06-24 update
												map.put('notice_content',getEncodeStr(Ext.getCmp('ckeditorid').getHtml()));//内容
												map.put('notice_time', values['notice_time-inputEl']);//公告期
												map.put('notice_seq', values['notice_seq-inputEl']);//优先级
												map.put('notice_object', new Array());//通知对象
												map.put('isApproved', this.isApproved);//是否直批
												map.put('flag', this.flag);//通知类型
												Rpc({functionId:'ZJ100000153',async:false,success:function(response) {
													var result = Ext.decode(response.responseText);
													var errorcode = result.errorcode;
													if (errorcode == '1') {
														Ext.showAlert('保存失败！');
													}else{
														if(Notice_global.saveAfterCallBack){
															Ext.callback(eval(Notice_global.saveAfterCallBack), null, []);
														}
													}
												},scope:this},map);
											}
											Ext.getCmp('NoticeWin').close();
										}
									}
								}, this);
							}
						},
						scope : this
					}, {
						text : '重置',
						handler : function() {
							Ext.getCmp('formpanel').getForm().reset();
							Ext.getCmp('ckeditorid').setValue(this.notice_content);
							//重置的时候对应的通知对象不能给置空了，而应该是刚进来的赋值的
							if(Notice_global.notic_object_orgin_value) {
								Ext.getCmp('notice_object_text').setValue(Notice_global.notic_object_orgin_value);
								Notice_global.selected_notice_object = Notice_global.notic_object_orgin_object;
							}
						},
						scope : this
					}]
		});

		return formPanel;
	},
	addNotice:function(panel){
		var labelWidth = 80;
		var textfieldWidth = 300;
		panel.add({
				xtype : 'container',
				width : labelWidth,
				items : [{
					xtype : 'label',
					html : '<font style="color:red; width:100px;"> &nbsp; </font>通知对象'
				}]
			});
		panel.add({
			xtype : 'container',
			width : 650,
			layout : 'hbox',
			margin : '0 0 10 0',
			items : [this.getNoticeObjectCombo(), {
						xtype : 'textfield',
						id : 'notice_object_text',
						labelSeparator : '',
						width : textfieldWidth,
						margin:'0 0 0 5',
						readOnly : true,
						value : ''
					} , {
						xtype:'image',
						src: "/images/del.gif",
						style:"cursor:pointer;",
						margin:'2 0 0 5',
//						width:20,
//						height:20,
						listeners: {
					        click: {
					            element: 'el', 
					            fn: function(a, o){ 
					            	Ext.getCmp('notice_object_text').reset(); 
					            	this.selected_notice_object = new Array();
				            	}
					        },
					        scope : this
						}
					}]
		});
	},
	// 通知对象
	getNoticeObjectCombo:function(){
		var store = new Ext.data.ArrayStore({
            fields: ['codeitem','codename'],
            data: [[1, '人员'], [2, '角色'], [3, '机构']]
        });
		
		return Ext.create('Ext.form.field.ComboBox', {
					fieldLabel : '',
					labelSeparator : '',
//					height : 22,
					width : 100,
					name : 'roleName',
					id : 'roleId',
					store : store,
					valueField : 'codeitem',
					displayField : 'codename',
					typeAhead : true,
					editable : false,
					queryMode : 'local',
					emptyText : '请选择',
					listeners : {
						'select' : function() {
							if (this.getValue() == '1') { // =1为人员；=2为角色；=3为组织单位
								var p = new PersonPicker({
									multiple : true,
									orgid:Notice_global.unitIds,
									isPrivExpression : false,
									callback : function(persons) {
										for (var i = 0; i < persons.length; i++) {
											var person= persons[i];
											var id = person.id;
											var name = person.name;
											Notice_global.addNoticeObject(id, name, 'person');
										}
									}
								}, this);
								p.open();
							} else if (this.getValue() == '2') {// 角色 单选
								Ext.require('EHR.rolepicker.RolePicker', function(){          
									Ext.create('EHR.rolepicker.RolePicker',{callBackFunc:function(selectRecords){
										for(var i=0; i<selectRecords.length; i++){
											var record = selectRecords[i];
											var id = record.role_id_e;
											var name = record.role_name;
											Notice_global.addNoticeObject(id, name, 'role');
										}
									},multiple:true});
								},this);
							} else if (this.getValue() == '3') {// 组织单元 单选
								var picker = new PersonPicker({
									text: "选择机构",
									multiple: true,
									addunit:true, //是否可以添加单位
									adddepartment:true, //是否可以添加部门
									orgid:Notice_global.unitIds, //haosl 20170920  bug 29788 
									isPrivExpression : false,
									callback: function (units) {
										for (var i = 0; i < units.length; i++) {
											var unit= units[i];
											var id = unit.id;
											var name = unit.name;
											var orgpre = unit.rawType;//UN、UM..
											Notice_global.addNoticeObject(id, name, 'org', orgpre);
										}
									}
								}, this);
								picker.open();
							}
						},
						'expand': function() {
							if(Ext.getCmp("roleId"))
								Ext.getCmp("roleId").setValue("");
						}
					}
				});
	},
	// 新增通知对象
	addNoticeObject:function(id, name, type, orgpre){
		// 文本显示
		var textfield = Ext.getCmp('notice_object_text');
		var oldValue = textfield.getValue();
		var newValue = oldValue += (name + ',');
		textfield.setValue(newValue);
		// 隐藏值记录
		var map = new HashMap();
		map.put('id', id);
		map.put('name', name);
		map.put('type', type);
		if(!!orgpre){
			map.put('orgpre', orgpre);
		}
		Notice_global.selected_notice_object.push(map);
		return newValue;
	}
});