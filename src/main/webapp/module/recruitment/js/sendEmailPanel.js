var Global = new Object();
var returnPanel = "open";
var eamilMap = new HashMap();
var executionMethod = "";
var emailNewPanel;
var c0102 = "";
var tempMap = new Object();
var sendTemplate = "";
var functionStr = "";
/*******************************************************************************
 * 弹出邮件发送面板
 * 
 * @param {}
 *            param
 */
emailPanel = function(param) {
	Ext.util.CSS.swapStyleSheet("emailCss","/ext/ext6/resources/ext-theme.css");
	Ext.util.CSS.createStyleSheet(".x-mask{filter: alpha(opacity = 50)!important;opacity: .5!important;cursor: default!important}");
	var value = param.responseText;
	var map = Ext.decode(value);
	tempMap = map;
	var info = map.info;
	var hasPosPriv = map.hasPosPriv;
	if (!Ext.isEmpty(hasPosPriv)) {
		Ext.Msg.alert('提示信息', hasPosPriv);
		return;
	}
	if (info && !Ext.isEmpty(info)) {
		Ext.Msg.alert('提示信息', info);
		return;
	}
	var data = Ext.decode(map.templateList);
	if("sendnotice"==map.functionStr&&data.length==0){
		Ext.showAlert(map.linkname+"环节未设置或未启用通知模板");
		return;
	}
	var subject = map.subject;
	var content = map.content;
	var template = map.template;
	var sub_module = map.sub_module;
	var nModule = map.nModule;
	var b0110 = map.b0110;
	c0102 = map.c0102;
	var z0301 = map.z0301;
	var a0100s = map.a0100s;
	var params = map.params;
	var method = map.method;
	var statueJson = Ext.decode(map.statueJson);
	functionStr = map.functionStr;
	var emailParam = map.emailParam;
	var renParam = map.renParam;
	var array = map.array;
	var fuId = map.fuId;
	executionMethod = map.executionMethod;
	var status = map.status;
	var otherfun = map.otherfun;//是否为接受或拒绝职位申请
	var title = map.title;
	
	if(data.length>0?true:("接受职位申请"!=title&&"拒绝职位申请"!=title)){
		var sendcontent = false;//发送内容sendcontent 为true给所有人发送的内容都是邮件面板里的内容
		if (title == null || title == "") {
			title = "邮件通知";
		}
	
		var file = "";
		var fileColumn = Ext.decode(map.fileColumn);
		var fileContentList = Ext.decode(map.fileContentList);
		var configs = {
			prefix : "emailFile",
			pagesize : 20,
			editable : true,
			selectable : false,
			storedata : fileContentList,
			tablecolumns : fileColumn,
			datafields : [ 'fileid', 'filename', 'extname' ]
		};
		
		file = new BuildTableObj(configs);
		var table = file.getMainPanel();
		var fileName = "";
		var store = new Ext.data.ArrayStore( {
			fields : [ 'value', 'name' ],
			data : data
		});
		var value = "";
		if(data.length > 0)
		   value = data[0][0];
		
		var combo = Ext.create('Ext.form.ComboBox', {
			id : 'combo',
			injectCheckbox : 1,
			autoSelect : true,
			editable : false,
			padding : '5 0 0 0',
			store : store,
			fieldLabel : "通知模板",
			labelSeparator : "  ",
			labelWidth : 55,
			anchor : "80%",
			value : value,
			valueField : 'value',
			displayField : 'name',// store字段中你要显示的字段，多字段必选参数，默认当mode为remote时displayField为undefine，当
			// select列表时displayField为”text”
			mode : 'local',// 因为data已经取数据到本地了，所以’local’,默认为”remote”，枚举完
			emptyText : '请选择一个模板',
			applyTo : 'combo',
			autoloader : true,
			listeners : {
				"select" : function(combo, record, index) {
					/** 弹出邮件发送页面* */
					var map = new HashMap();
					map.put("sub_module", sub_module);
					map.put("nModule", nModule);
					map.put("b0110", b0110);
					map.put("c0102", c0102);
					map.put("a0100s", a0100s);
					map.put("z0301", z0301);
					map.put("params", params);
					map.put("method", method);
					map.put("id", record.data.value);
	
					Rpc( {
						functionId : 'ZP0000002000',
						success : getEmailBean
					}, map);
				}
			}
		});
	
		var radioGroup = new Ext.form.RadioGroup( {
			xtype : 'radiogroup',
			width:'100%',
			id : 'radioGroupId',
			padding : '0 0 0 13',
			columns : 3,
			region : 'north',
			items : statueJson
			
		});
		var emailChecked = true;
		var emailDisabled = false;
		if("true" == map.emailFlag) {
			emailChecked = false;
			emailDisabled = true;
		}
		
		var sendEmailInfo = new Ext.form.Checkbox( {
			id : "sendEmailInfo",
			name : "checkbox",
			checked : emailChecked,
			disabled : emailDisabled,
			labelSeparator : '',
			padding : '0 5 0 5',
			boxLabel : '邮件通知',
			inputValue : 1,
			handler : function() {
				var checked = Ext.getCmp('sendEmailInfo').checked;
				var feedCheck = Ext.getCmp('feedBack').checked;
				var sendNotice = Ext.getCmp('sendnotice').checked;
				if (!checked && !feedCheck&&!sendNotice) {
					Ext.getCmp('bodyPanel').setDisabled(true);
				} else {
					Ext.getCmp('bodyPanel').setDisabled(false);
				}
			}
		});
	
		var feedBack = new Ext.form.Checkbox( {
			id : "feedBack",
			name : "checkbox",
			checked : true,
			labelSeparator : '',
			padding : '0 5 0 5',
			boxLabel : '反馈信息',
			inputValue : 1,
			handler : function() {
				var checked = Ext.getCmp('sendEmailInfo').checked;
				var feedCheck = Ext.getCmp('feedBack').checked;
				var sendNotice = Ext.getCmp('sendnotice').checked;
				if (!checked && !feedCheck&&!sendNotice) {
					Ext.getCmp('bodyPanel').setDisabled(true);
				} else {
					Ext.getCmp('bodyPanel').setDisabled(false);
				}
			}
		});
		
		var phoneChecked = true;
		var phoneDisabled = false;
		if("true" == map.phoneFlag) {
			phoneChecked = false;
			phoneDisabled = true;
		}
		
		var sendnotice = new Ext.form.Checkbox( {
			id : "sendnotice",
			name : "checkbox",
			checked : phoneChecked,
			disabled : phoneDisabled,
			labelSeparator : '',
			padding : '0 5 0 5',
			boxLabel : '短信通知',
			inputValue : 1,
			handler : function() {
				var checked = Ext.getCmp('sendEmailInfo').checked;
				var feedCheck = Ext.getCmp('feedBack').checked;
				var sendNotice = Ext.getCmp('sendnotice').checked;
				if (!checked && !feedCheck&&!sendNotice) {
					Ext.getCmp('bodyPanel').setDisabled(true);
				} else {
					Ext.getCmp('bodyPanel').setDisabled(false);
				}
			}
		});
	
		var bodyPanel = new Ext.Panel( {
			id : 'bodyPanel',
			region : 'center',
			width: 480,
			layout : {
				type : 'vbox',
				align : 'stretch'
			},
			border : 0,
			items : [ combo, {
				xtype : "textfield",
				fieldLabel : "通知标题",
				labelSeparator : "  ",
				labelWidth : 55,
				value : subject,
				id : "title"
			}, {
				xtype : "htmleditor",
				height : 95,
				fieldLabel : "通知内容预览",
				labelWidth : 55,
				id : "content",
				value : content,
				labelSeparator : "  ",
				enableAlignments : false,// 是否启用对齐按钮，包括左中右三个按钮
				enableColors : false,// 是否启用前景色背景色按钮，默认为true
				enableFont : false,// 是否启用字体选择按钮 默认为true
				enableFontSize : false,// 是否启用字体加大缩小按钮
				enableFormat : false,// 是否启用加粗斜体下划线按钮
				enableLists : false,// 是否启用列表按钮
				enableSourceEdit : false,// 是否启用代码编辑按钮
				enableLinks : false,
				fontFamilies : [ "宋体", "隶书", "黑体", "楷体" ],
				listeners:{
					'beforerender':function(){
						this.setReadOnly(true);
					}
				}
			},{
				xtype : "htmleditor",
				height : 210,
				fieldLabel : "通知模板编辑",
				labelWidth : 55,
				id : "contentTemplate",
				value : template,
				labelSeparator : "  ",
				enableAlignments : false,// 是否启用对齐按钮，包括左中右三个按钮
				enableColors : false,// 是否启用前景色背景色按钮，默认为true
				enableFont : false,// 是否启用字体选择按钮 默认为true
				enableFontSize : false,// 是否启用字体加大缩小按钮
				enableFormat : false,// 是否启用加粗斜体下划线按钮
				enableLists : false,// 是否启用列表按钮
				enableSourceEdit : false,// 是否启用代码编辑按钮
				enableLinks : false,
				hidden:true,
				fontFamilies : [ "宋体", "隶书", "黑体", "楷体" ]
			}, {
				xtpe : 'panel',
				id : 'filepanel',
				height : 110,
				border : 0,
				layout : 'hbox',
				disabledCls : 'disableCss',
				items : [ {
					xtype : 'label',
					text : '邮件附件  ',
					width : 60
				}, {
					xtype : 'panel',
					id : 'emailFile',
					layout : 'fit',
					border : 0,
					height : 110,
					flex : 10,
					items : [ table ]
				} ]
			} ]
	
		});
		
		var stageHeight = 410;
		if(statueJson.length > 0)
			stageHeight = 490 + Math.ceil((statueJson.length-9)/3)*40;
		
		if(data.length==0)
			stageHeight = Math.ceil((statueJson.length-9)/3)*40+160;
		//new Object();会显示一个黑点
		var paneltop=Ext.create('Ext.panel.Panel', {id:'aa',border:0,hide:true});
		var emailtop=Ext.create('Ext.panel.Panel', {id:'bb',border:0,hide:true});
		if("passChoice"==functionStr){
			paneltop = Ext.create('Ext.panel.Panel', {
			    width: "100%",
			    html: '<div style="width:100%;height:25px;background:#f0f0f0;color:#333;line-height:25px;font-size:12px;"> '
				    +'<font  style="margin-left:10px">转新阶段</font>'
			   		+'</div>',
			   	border:0
			});
			emailtop = Ext.create('Ext.panel.Panel', {
				width: "100%",
				id:'emailtopId',
				html: '<div style="width:100%;height:25px;background:#f0f0f0;color:#333;line-height:25px;font-size:12px;"> '
					+'<font  style="margin-left:10px">通知设置</font>'
					+'</div>',
					border:0
			});
			if(data.length>0)
				stageHeight+=35;
		}
		emailNewPanel = Ext.widget(
				"window",
				{
					modal : true,
					title : title,
					layout : 'vbox',
					height : stageHeight,
					style:'background-color:white!important',
					width : 510,
					border : 0,
					items : [paneltop,
					    radioGroup, 
					    emailtop,
					    {
						id:'emailid',
						layout : {
							type : 'vbox',
							align : 'stretch'
						},
						border : 0,
						padding : '0 10 0 10',
						tbar : [{xtype: 'label',forId: 'myFieldId',text: '给候选人发送',padding : '0 5 0 0'}, sendEmailInfo, sendnotice,feedBack],
						items : [ bodyPanel,
						          {xtype:'panel',
									id:'editTemplateId',
									border:false,
									style:'margin-left:60px',
									html:'<a onclick="editTemplate()">编辑模板</a>'} ]
					} ],
					resizable : false,
					buttonAlign : 'center',
					buttons : [ {
						text : "确定",
						id : "sendEmail",
						handler : function() {
							if(Ext.getDom('positionstatus')!=null&&Ext.getDom('positionstatus')!=undefined)
								Ext.getDom('positionstatus').innerHTML="处理中";
							
							var title = Ext.getCmp("title").getValue();
							var content = Ext.getCmp("content").getValue();
							var templateId = Ext.getCmp("combo").getValue();
							
							if(statueJson.length > 0) {
								var items = Ext.getCmp('radioGroupId').items.items;
								for(var i=0;i<items.length;i++){
									var item = items[i];
									if(item.checked){
										emailParam[1]=item.inputValue;
										break;
									}
								}
							}
							var sendEmailInfo = Ext.getCmp('sendEmailInfo').checked;
							var feedCheck = Ext.getCmp('feedBack').checked;
							var noticeCheck = Ext.getCmp('sendnotice').checked;
							var sendEmail = "0";
							var sendFeedBack = "0";
							var sendNotice = "0";
							if(data.length>0){
								if (sendEmailInfo&&data.length > 0)
									sendEmail = "1";
								
								if (feedCheck&&data.length > 0)
									sendFeedBack = "1";
								
								if (noticeCheck&&data.length > 0)
									sendNotice = "1";
							}else{
								Ext.getCmp('sendEmailInfo').checked=false;
								Ext.getCmp('feedBack').checked=false;
								Ext.getCmp('sendnotice').checked=false;
							}
							
							eamilMap = new HashMap();
							var ifsend = "";//验证内容是否为空
							ifsend=content.replace(/<\/?[^>]*>/g,'');
							ifsend=ifsend.replace(/&nbsp;/g,'');
							if((sendEmailInfo||feedCheck||noticeCheck)&&ifsend.length<=0&&templateId){
								sendcontent = true;
								Ext.showAlert("通知内容为空！");
								return;
							}
							eamilMap.put("sendcontent", sendcontent);
							eamilMap.put("sendEmailInfo", sendEmail);
							eamilMap.put("feedCheck", sendFeedBack);
							eamilMap.put("sendNotice", sendNotice);
							eamilMap.put("c0102", c0102);
							eamilMap.put("z0301", z0301);
							eamilMap.put("title", title);
							eamilMap.put("content", content);
							eamilMap.put("templateId", templateId);
							eamilMap.put("sub_module", sub_module);
							eamilMap.put("nModule", nModule);
							eamilMap.put("b0110", b0110);
							eamilMap.put("a0100s", a0100s);
							eamilMap.put("params", params);
							eamilMap.put("method", method);
							eamilMap.put("array", array);
							eamilMap.put("sendTemplate", sendTemplate); //按照修改后的模板发送
							if(sendTemplate)
								eamilMap.put("template",Ext.getCmp('contentTemplate').getValue());
							if (fuId && fuId != "") {
								var hashMap = new HashMap();
								hashMap.put("status", status);
								hashMap.put("array", array);
								hashMap.put("emailParam", emailParam);
								hashMap.put("renParam", renParam);
	
								Rpc( {
									functionId : fuId,
									success : executionSuc
								}, hashMap);
	
							} else {
								Rpc({functionId : 'ZP0000002001',success : sendeEmailMsg}, eamilMap);
							}
						}
					}, {
						text : "关闭",
						handler : function() {
							emailNewPanel.close();
						}
					} ],
					listeners : {
						'close' : function() {
							if (params != null && method != null && method != ""&& params != "") {
								method = eval(method);
								method(params);
							}
						},
						'afterrender':function(){
							var content = Ext.getCmp("content").getValue();
							var templateId = Ext.getCmp("combo").getValue();
							if((content||""==content)&&templateId){
								var ifsend = "";
								ifsend=content.replace(/<\/?[^>]*>/g,'');
								ifsend=ifsend.replace(/&nbsp;/g,'');
								if(ifsend.length<=0){
									sendcontent = true;
								}
							}
						}
					}
				}).show();
		
		if (fileContentList.length < 1) {
			Ext.getCmp("filepanel").hide(true);
			Ext.getCmp("content").setHeight(210);
		}
		
		if(statueJson.length < 1)
			Ext.getCmp("radioGroupId").hide();
		
		if(data.length==0) {
			if(Ext.getCmp("emailtopId"))
				Ext.getCmp("emailtopId").hide();
			Ext.getCmp("emailid").hide();
			Ext.getCmp('sendEmailInfo').checked = false;
			Ext.getCmp('feedBack').checked = false;
		}
		if("true" == map.phoneFlag) {
			Ext.getCmp("sendnotice").hide();
		}
		if("true" == map.emailFlag) {
			Ext.getCmp("sendEmailInfo").hide();
		}
	}else{
		Ext.showConfirm("确认"+title+"吗？",function(obj){
			if("yes"==obj){
				var hashMap = new HashMap();
				hashMap.put("status", status);
				hashMap.put("array", array);
				hashMap.put("emailParam", emailParam);
				hashMap.put("renParam", renParam);
				hashMap.put("noemail", 1);
				Rpc({
					functionId : fuId,
					success:sendeEmailMsg
				}, hashMap);
			}
		});
	}
}
/*******************************************************************************
 * 动态选择邮件模板
 * 
 * @param {}
 *            param
 */
getEmailBean = function(param) {
	var value = param.responseText;
	var map = Ext.decode(value);
	var title = map.subject;
	var content = map.content;
	var template = map.template;
	var fileColumn = Ext.decode(map.fileColumn);
	var fileContentList = Ext.decode(map.fileContentList);
	var height = Ext.getCmp("content").getHeight();
	var store = Ext.getCmp("emailFile_tablePanel").getStore();
	store.loadData(fileContentList);
	Ext.getCmp("title").setValue(title);
	Ext.getCmp("content").setValue(content);
	Ext.getCmp("contentTemplate").setValue(template);

	if (fileContentList.length < 1 && height != 210) {
		Ext.getCmp("content").setHeight(210);
		Ext.getCmp("filepanel").hide();
	} else if (fileContentList.length > 0 && height != 95) {
		Ext.getCmp("content").setHeight(95);
		Ext.getCmp("filepanel").setHeight(110);
		Ext.getCmp("filepanel").show();
	}

}
/*******************************************************************************
 * 邮件发送返回方法
 * 
 * @param {}
 *            param
 */
sendeEmailMsg = function(param) {
	Ext.util.CSS.createStyleSheet(".x-nbr .x-window-header-default-top{background-color:white !important}");
	Ext.util.CSS.createStyleSheet(".x-toolbar-footer{background-color:white !important;margin-top:0px}");
	var value = param.responseText;
	var map = Ext.decode(value);
	var msg = map.msg;
	var params = map.params;
	var method = map.method;
	if(emailNewPanel)
		emailNewPanel.close();
	//屏蔽 表格设置了合计，但是看不到表格的时候刷新store的错误信息 ，需要调试时最好去掉
	window.onerror=function(){return true;} 
	if (msg == 1) {
		//发送邮件成功不提示成功信息
		if (Ext.isEmpty(executionMethod)){
			var datastore = Ext.data.StoreManager.lookup('tablegrid_dataStore');
			datastore.reload();
		}
	} else {//发送邮件不成功也要刷新表格
		if (Ext.isEmpty(msg)){
			if (Ext.isEmpty(executionMethod)){
				var datastore = Ext.data.StoreManager.lookup('tablegrid_dataStore');
				datastore.reload();
			}
		}
		else {
			if("sendnotice"==functionStr)
					eval(executionMethod + "(param);");
			Ext.MessageBox.alert("提示信息", msg, function(but) {
				if (Ext.isEmpty(executionMethod)){
					var datastore = Ext.data.StoreManager.lookup('tablegrid_dataStore');
					datastore.reload();
				}
			});
		}
	}
	
}

executionSuc = function(param) {
	
	var result = Ext.decode(param.responseText);
	var warnMsg = "";
	if (result.result != true) {
		if (result.info != '')
			warnMsg = getDecodeStr(result.info);
		else
			warnMsg = '操作失败！';

		if (!Ext.isEmpty(warnMsg))
			Ext.Msg.alert('提示信息', warnMsg);
		return;
	}

	var info = getDecodeStr(result.info);
	var msg = getDecodeStr(result.msg);
	if (info)
		warnMsg = getDecodeStr(result.info);

	if (msg && msg != "true")
		warnMsg = msg;

	if (!Ext.isEmpty(warnMsg))
		Ext.Msg.alert('提示信息', warnMsg);

	var sendEmailInfo = Ext.getCmp('sendEmailInfo').checked;
	var feedCheck = Ext.getCmp('feedBack').checked;
	var noticeCheck = Ext.getCmp('sendnotice').checked;
	if(emailNewPanel)
		emailNewPanel.close();
	if (!Ext.isEmpty(executionMethod)) {
		eval(executionMethod + "(param);");
	} else if (!sendEmailInfo && !feedCheck) {
		if (Ext.isEmpty(executionMethod)) {
			var datastore = Ext.data.StoreManager.lookup('tablegrid_dataStore');
			datastore.reload();
		}
	}
	
	var sendFeedback="0";
	if (sendEmailInfo || feedCheck||noticeCheck) {
		var sendEmail = "0";
		if (sendEmailInfo)
			sendEmail = "1";

		if (feedCheck)
			sendFeedback = "1";
		
		var sendNotice = "0";
		if (noticeCheck)
			sendNotice = "1";

		var preA0100s = result.preA0100s;
		var z0301s = result.z0301s;
		var c0102s = result.c0102s;
		if(eamilMap.get("templateId")==null||eamilMap.get("templateId")==undefined){
			return;//没模板不发邮件
		}
		eamilMap.put("c0102", c0102s);
		eamilMap.put("z0301", z0301s);
		eamilMap.put("a0100s", preA0100s);
		eamilMap.put("sendEmailInfo", sendEmail);
		eamilMap.put("feedCheck", sendFeedback);
		eamilMap.put("sendNotice", sendNotice);

		Rpc( {
			functionId : 'ZP0000002001',
			success : sendeEmailMsg
		}, eamilMap);
	}

	//招聘简历是否处理
	var obj = window.parent.document.getElementById("positionstatus");
	if(obj && "7" == eamilMap.sub_module) {
		if("10" == eamilMap.nModule)
			obj.innerHTML="处理中";
		else if("11" == eamilMap.nModule)
			obj.innerHTML="已处理";
	}
	
	var img = window.parent.document.getElementById("imgId");
	if(!img)
		img = document.getElementById("imgId");
	
	if(img && "1" == sendFeedback)
		img.style.display="";
	else if(img && "1" != sendFeedback)
		img.style.display="none";
	
	if (!sendEmailInfo && !feedCheck)
		emailNewPanel.close();
}
//编辑模板内容
editTemplate=function(edit){
	if(edit){
		sendTemplate="true";
		var subject = tempMap.subject;
		var content = tempMap.content;
		var template = tempMap.template;
		var sub_module = tempMap.sub_module;
		var nModule = tempMap.nModule;
		var b0110 = tempMap.b0110;
		c0102 = tempMap.c0102;
		var z0301 = tempMap.z0301;
		var a0100s = tempMap.a0100s;
		var params = tempMap.params;
		var method = tempMap.method;
		Ext.getCmp('content').setHidden(false);
		Ext.getCmp('contentTemplate').setHidden(true);
		Ext.getCmp('editTemplateId').setHtml('<a onclick="editTemplate()">编辑模板</a>');
		Ext.getCmp('sendEmail').setDisabled(false);
		var map = new HashMap();
		map.put("sub_module", sub_module);
		map.put("nModule", nModule);
		map.put("b0110", b0110);
		map.put("c0102", c0102);
		map.put("a0100s", a0100s);
		map.put("z0301", z0301);
		map.put("params", params);
		map.put("method", method);
		map.put("template",Ext.getCmp('contentTemplate').getValue());
		Rpc( {
			functionId : 'ZP0000002000',
			success : getEmailBean
		}, map);
	}else{
		Ext.getCmp('content').setHidden(true);
		Ext.getCmp('contentTemplate').setHidden(false);
		Ext.getCmp('sendEmail').setDisabled(true);
		Ext.getCmp('editTemplateId').setHtml('<a onclick="editTemplate('+"true"+')">编辑完成</a>');
	}
}


