Ext.util.CSS.createStyleSheet(".down{margin:6px 2px;border-width:8px 5px 0px 5px;border-style:solid;border-color:#549fe3 #f0f0f0 #f0f0f0 #f0f0f0;}");
Ext.util.CSS.createStyleSheet(".up{margin:6px 2px;border-width:0px 5px 8px 5px;border-style:solid;border-color:#f0f0f0 #f0f0f0 #549fe3 #f0f0f0;}");
Ext.define("sendEmailUL.sendEmail",{
    extend:'Ext.window.Window',
    xtype:'emailPanel',
    me:'',
    scope:undefined,
    sub_module:undefined,//招聘环节(子模块编号)
    nModule:undefined,//模块编号(1：人员；2：薪资发放；5：薪资审批；7：招聘管理（新）)
    c0102:undefined,
    a0100s:undefined,
    a0101s:undefined,
    z0301:undefined,
    function_str:undefined,//方法名
    link_Id:undefined,//环节id
    node_Id:undefined,//状态id
    title:undefined,
    operation:true,//是否记录日志
    fuId:undefined,//是否先执行此交易类
    sendTemplate:false,//按照编辑内容发送，当模板为空时起作用
    map:undefined,//保存发邮件需要的参数
    executionMethod:undefined,
    templateSize:undefined,//邮件模板个数
    template:undefined,//模板
    initComponent:function(){
    	me = this;
    	var map = new HashMap();
    	map.put("sub_module", me.sub_module);
    	map.put("nModule", me.nModule);
    	map.put("a0100s", me.a0100s);
    	map.put("z0301", me.z0301);
    	map.put("function_str", me.function_str);
    	map.put("link_Id", me.link_Id);
    	map.put("node_Id", me.node_Id);
    	Rpc({functionId : 'ZP0000002000',success:me.emailPanel}, map);
    },
    emailPanel:function(param){
    	var title = "提示信息";
    	if(me.title)
    		title =me.title;
		var result = Ext.decode(param.responseText);
		var data = Ext.decode(result.templateList);
		me.templateSize = data.length;
		me.template = result.template;
		if("sendnotice"==me.function_str&&me.templateSize==0){
			Ext.showAlert(result.linkname+noTemplate);
			return;
		}
		if("7"==me.sub_module&&"91"==me.nModule&&me.templateSize==0){
			Ext.showAlert(SET_EMAIL_TEMPLATE);
			return;
		}
		if("7"==me.sub_module&&"82"==me.nModule&&me.templateSize==0){
			Ext.showAlert(set_center_template);
			return;
		}
		me.node_Id = result.status;
		var old = Ext.getCmp('emailNewPanelID');
		if(old)//防止重复点击
			old.destroy();
    	me.emailNewPanel = Ext.widget('window',{
									modal : true,
									title : title,
									id:'emailNewPanelID',
									layout : 'vbox',
									width : 510,
									height : 274,
									y:80,
									style:'background-color:white!important',
									border : 0,
									padding : '0 0 0 0',
									defaults:{
										margin:'0 0 0 10'
									},
									resizable : false,
									buttonAlign : 'center',
									buttons : [{
										text : "确定",
										id : "sendEmail",
										handler : me.enter
									}, {
										text : "关闭",
										handler : function() {
											me.emailNewPanel.close();
										}
									}],
									listeners : {
										'close' : function() {},
										'afterrender':function(){
											var content = result.content;
											if((content||""==content)&&me.templateSize>0){
												var ifempty=content.replace(/<\/?[^>]*>/g,'');
												ifempty=ifempty.replace(/&nbsp;/g,'');
												if(ifempty.length<=0){
													sendTemplate = true;
												}
											}
										}
									}
								}).show();
    	if(me.templateSize>0){
			me.createToolBar(result);
			me.createBodyPanel(result);
			me.bodyPanel.setDisabled(true);
		}else{
			me.emailNewPanel.setHeight(70);
		}
    	if("passChoice"==me.function_str||"changeStatus"==me.function_str)
    		me.getPaneltop(result);
    	//插入附件窗口
		if(Ext.decode(result.fileContentList).length>0)
			me.createFilepanel(result);	
		if("passChoice"==me.function_str&&me.node_Id.substring(0,2)=="05"){
			var paneltop = Ext.widget('container', {
			    width: "100%",
			    border:0,
			    height:22,
			    margin:'4 0 4 10',
			    html:'<div style="width:100%;height:22px;background:#f0f0f0;color:#333;line-height:22px;font-size:12px;display: inline-flex;vertical-align: middle;">'
					+'<font style="margin-left:10px">操作人通知设置</font>'
					+'<img id="personPanelsj" src="/module/recruitment/image/down.png";/>'
					+'<a style="cursor:pointer;margin-left:290px" onclick="me.openPicker()" onmouseout="leave(this)" onmouseover="hover(this)">添加通知对象</a>'
					+'</div>',
				listeners:{
					'render':function() {
						Ext.fly(this.el).on('click',
						function(e, t) {
							if(e.target.nodeName=="A")
								return;
							if(me.bodyPanel.isHidden()){
								me.bodyPanel.show();
								Ext.getDom('emailsj').src = "/module/recruitment/image/up.png";
								Ext.getCmp('personPanelId').hide();
								Ext.getDom('personPanelsj').src = "/module/recruitment/image/down.png";
							}else{
								me.bodyPanel.hide();
								Ext.getDom('emailsj').src = "/module/recruitment/image/down.png";
								Ext.getCmp('personPanelId').show();
								Ext.getDom('personPanelsj').src = "/module/recruitment/image/up.png";
							}
						});
					}
				}
			});
			me.emailNewPanel.insert(paneltop);
    		me.emailNewPanel.insert(me.addNoticePanel());
    		me.emailNewPanel.setHeight(me.emailNewPanel.getHeight()+30);
    	}
    	//添加意见窗口
    	if(me.operation){
    		var height = 60;
    		if (Ext.isIE)
				height += 12;
	    	var operation = me.createOperation();
	    	
	    	if(me.templateSize==0){
	    		
	    		operation.setMargin('5 0 0 10');
		    	if(Ext.decode(result.statueJson).length==0){
					me.emailNewPanel.setWidth(302);
					operation.setFieldLabel();
					operation.setWidth(286);
					operation.setMargin('5 5 0 5');
		    	}
	    	}
	    	me.emailNewPanel.insert(operation);
	    	me.emailNewPanel.setHeight(me.emailNewPanel.getHeight()+height);
    	}else{
    		if (Ext.isIE)
    			me.emailNewPanel.setHeight(me.emailNewPanel.getHeight()+6);
    	}
    },
    //生成上方工具条
    createToolBar:function(result){
    	//邮件通知选择框	
    	var emailparam = new Object();
    	emailparam.id = "sendEmailInfo";
    	emailparam.label = "邮件通知";
    	var checked = true;
    	var disabled = false;
    	
    	if("false" == result.emailFlag) {
    		checked = false;
    		disabled = true;
    	}
		emailparam.checked = false;
		emailparam.disabled = disabled;
    	var sendEmailInfo = me.createCheckbox(emailparam);
    	
    	//短信通知选择框	
    	var noticeparam = new Object();
    	noticeparam.id = "sendnoticeId";
    	noticeparam.label = "短信通知";
    	checked = true;
    	disabled = false;
    	if("false" == result.phoneFlag) {
    		checked = false;
    		disabled = true;
    	}
    	noticeparam.checked = false;
    	noticeparam.disabled = disabled;
    	var sendnotice = me.createCheckbox(noticeparam);
    	//反馈信息选择框	
    	var feedparam = new Object();
    	feedparam.id = "feedBack";
    	feedparam.label = "反馈信息";
    	feedparam.checked = false;
    	feedparam.disabled = false;
    	var feedBack = me.createCheckbox(feedparam);
    	var toolbar = Ext.widget('toolbar',{
			    		border:0,padding : '1 0 1 0',
						items: [{xtype: 'label',forId: 'myFieldId',text: '给候选人发送',padding : '0 5 0 0'},
			                  sendEmailInfo, sendnotice,feedBack]
					});
    	me.emailNewPanel.insert(0,toolbar);
    },
    //生成弹窗主要内容
    createBodyPanel:function(result){
		var combo = me.createComboBox(result);
		var htmleditor = me.createHtmlEditor(result);
    	me.bodyPanel =  Ext.widget('container',{
			id : 'bodyPanel',
			region : 'center',
			width: 480,
			border : 0,
			layout : {
				type : 'vbox',
				align : 'stretch'
			},
			items : [combo, {
				xtype : "textfield",
				fieldLabel : "通知标题",
				labelSeparator : "",
				labelWidth : 55,
				value : result.subject,
				id : "subject"},
				htmleditor]
		});
    	
    	me.emailNewPanel.add(me.bodyPanel);
    },
    //切换邮件模板
    createComboBox:function(result){
    	var data = Ext.decode(result.templateList);
		var store = new Ext.data.ArrayStore( {
			fields : ['value', 'name'],
			data:data
		});
		var combo = Ext.create('Ext.form.ComboBox', {
			id : 'combo',
			injectCheckbox : 1,
			autoSelect : true,
			editable : false,
			padding : '5 0 0 0',
			store : store,
			fieldLabel : "通知模板",
			labelSeparator : "",
			labelWidth : 55,
			anchor : "80%",
			valueField : 'value',
			displayField : 'name',// store字段中你要显示的字段，多字段必选参数，默认当mode为remote时displayField为undefine，当
			// select列表时displayField为”text”
			mode : 'local',// 因为data已经取数据到本地了，所以’local’,默认为”remote”，枚举完
			emptyText : '请选择一个模板',
			applyTo : 'combo',
			autoloader : true,
			listeners : {
				'afterRender':function(){
					if(data.length > 0)
						   value = data[0][0];
					this.setValue(value);
				},
				'select' : function(combo, record, index) {
					var map = new HashMap();
			    	map.put("sub_module", me.sub_module);
			    	map.put("nModule", me.nModule);
			    	map.put("a0100s", me.a0100s);
			    	map.put("z0301", me.z0301);
			    	map.put("function_str", me.function_str);
			    	map.put("id", combo.value);
			    	map.put("link_Id", me.link_Id);
			    	map.put("node_Id", me.node_Id);
			    	Rpc({functionId : 'ZP0000002000',success:me.getEmailBean}, map);
				}
			}
		});
		return combo;
    },
    //生成内容窗口
    createHtmlEditor:function(result){
    	Ext.util.CSS.updateRule('.x-html-editor-input','border','1px solid #b5b8c8');
    	var htmleditor = Ext.widget('htmleditor',{
			height : 120,
			/*编辑模板暂时去掉防止用户在不知情的情况下发送相同内容*/
			fieldLabel : '内容预览 <br><div id="editId" style="line-height:30px;"><a onclick="me.editTemplate()">编辑模板</a></div>',
			labelWidth : 55,
			id : "htmleditor",
			value : result.content,
			labelSeparator : "",
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
					this.getToolbar().hide();
					this.setReadOnly(true);
				}
			}
		});
    	return htmleditor;
    },
    //生成checkbox
    createCheckbox:function(obj){
    	
    	var checkBox = new Ext.form.Checkbox( {
			id : obj.id,
			checked : obj.checked,
			disabled : obj.disabled,
			padding : '0 5 0 5',
			boxLabel : obj.label,
			handler : function() {
				var checked = Ext.getCmp('sendEmailInfo').checked;
				var feedCheck = Ext.getCmp('feedBack').checked;
				var sendNotice = Ext.getCmp('sendnoticeId').checked;
				if (!checked && !feedCheck&&!sendNotice) {
					me.bodyPanel.setDisabled(true);
				} else {
					me.bodyPanel.setDisabled(false);
					if("passChoice" == me.function_str&&me.node_Id.substring(0,2)=="05"){
						me.bodyPanel.show();
						Ext.getDom('emailsj').src = "/module/recruitment/image/up.png";
						Ext.getCmp('personPanelId').hide();
						Ext.getDom('personPanelsj').src = "/module/recruitment/image/down.png";
					}
				}
			}
		});
    	return checkBox;
    },
    //生成意见窗口
    createOperation:function(){
    	var operation = Ext.widget('textareafield',{
	        id:'operationID',
	        fieldLabel:'意见',
	        width: 480,
	        labelAlign:'center',
	        emptyText:'请填写意见',
	        labelWidth : 55,
	        rows:3
	    });
    	return operation;
    },
    insertItem:function(obj,index){
    	if(Ext.isNumber(index))
  		  me.bodyPanel.insert(index,obj);
  		else
  		  me.bodyPanel.add(obj);
    },
    //生成附件窗口
    createFilepanel:function(param){
		var fileColumn = Ext.decode(param.fileColumn);
		var fileContentList = Ext.decode(param.fileContentList);
		var configs = {
			prefix : "emailFile",
			pagesize : 20,
			editable : true,
			selectable : false,
			storedata : fileContentList,
			tablecolumns : fileColumn,
			datafields : [ 'fileid', 'filename', 'extname' ]
		};
		
		var file = new BuildTableObj(configs);
		var table = file.getMainPanel();
    	var filepanel = Ext.widget('container',{
			id : 'filepanel',
			height : 100,
			border : 0,
			layout : 'hbox',
			items : [{
				xtype : 'label',
				text : '邮件附件  ',
				width : 60
				}, {
				xtype : 'container',
				id : 'emailFile',
				layout : 'fit',
				border : 0,
				height : 110,
				flex : 10,
				items : table
				}]
			});
    	
		me.emailNewPanel.setHeight(me.emailNewPanel.getHeight()+100);
		me.insertItem(filepanel);
    },
    //编辑通知内容按钮
    editTemplate:function(edit){
    	var htmleditor = Ext.getCmp("htmleditor");
    	var send = Ext.getCmp('sendEmail');
    	if(edit){
    		Ext.MessageBox.wait("", "正在生成通知内容，请稍候…");
    		htmleditor.setFieldLabel("通知内容 <br><div style='line-height:30px;'><a onclick='me.editTemplate(true)'>编辑完成</a></div>");
    		me.template = htmleditor.getValue();
    		if(me.template == null || trim(me.template.replace(/&nbsp;/g, "")).length<=0){
				Ext.Msg.alert(PROMPT_INFORMATION,NOTIFICATION_CONTENT_ISNOTEMPT);
				return;
			}
    		
    		var map = new HashMap();
        	map.put("template", me.template);
        	map.put("sub_module", me.sub_module);
        	map.put("nModule", me.nModule);
        	map.put("a0100s", me.a0100s);
        	map.put("z0301", me.z0301);
        	map.put("function_str", me.function_str);
        	map.put("link_Id", me.link_Id);
        	map.put("node_Id", me.node_Id);
        	map.put("id", Ext.getCmp("combo").getValue());//模板id
        	Rpc({functionId : 'ZP0000002000',success:function(param){
        		var result = Ext.decode(param.responseText);
        		htmleditor.setValue(result.content);
        		send.setDisabled(false);
        		htmleditor.setFieldLabel("通知内容<br><div style='line-height:30px;'><a onclick='me.editTemplate()'>编辑模板</a></div>");
        		Ext.MessageBox.close();	
        	}}, map);
    	}else{
    		Ext.showAlert("内容编辑将对本次所选的所有人员生效，请谨慎操作！")
    		htmleditor.setFieldLabel("通知内容<br><div style='line-height:30px;'><a onclick='me.editTemplate(true)'>编辑完成</a></div>");
    		me.sendTemplate = true;
    		htmleditor.setReadOnly(false);
    		htmleditor.setValue(me.template);
    		send.setDisabled(true);
    	}
    },
    //"passChoice"==function_str 面试通过时需要转环节
    getPaneltop:function(param){
    	var paneltop = Ext.widget('container', {
			    width: "100%",
			    html: '<div style="width:100%;height:22px;background:#f0f0f0;color:#333;line-height:22px;font-size:12px;"> '
				    +'<font  style="margin-left:10px">转新阶段</font>'
			   		+'</div>',
			   	border:0
			});
    	var	emailtop = Ext.widget('container', {
	    		id:'emailtopId',
	    		border:0,
				width: "100%",
				html: '<div style="width:100%;height:22px;background:#f0f0f0;color:#333;line-height:22px;font-size:12px;display: inline-flex;vertical-align: middle;"> '
					+'<font style="margin-left:10px">候选人通知设置</font>'
					+'<img id="emailsj" src="/module/recruitment/image/up.png";/>'
					+'</div>',
				listeners:{
					'render':function() {
						Ext.fly(this.el).on('click',
						function(e, t) {
							if(me.bodyPanel.isHidden()){
								me.bodyPanel.show();
								Ext.getDom('emailsj').src = "/module/recruitment/image/up.png";
								Ext.getCmp('personPanelId').hide();
								Ext.getDom('personPanelsj').src = "/module/recruitment/image/down.png";
							}
							else{
								me.bodyPanel.hide();
								Ext.getDom('emailsj').src = "/module/recruitment/image/down.png";
								Ext.getCmp('personPanelId').show();
								Ext.getDom('personPanelsj').src = "/module/recruitment/image/up.png";
							}
						});
					}
				}
			});
    	var radioGroup = me.createRadio(param);
    	var statueJson = Ext.decode(param.statueJson);
    	var stageHeight = 18;
    	if("passChoice"==me.function_str){
    		if(me.templateSize>0){
    			me.emailNewPanel.insert(0,emailtop);
    			stageHeight += 22;
    		}
	    	me.emailNewPanel.insert(0,radioGroup);
	    	me.emailNewPanel.insert(0,paneltop);
    	}else{
    		stageHeight = 0;
    		me.emailNewPanel.insert(0,radioGroup);
    	}
//    	if (Ext.isIE)
//    		stageHeight += 4;
		stageHeight += Math.ceil(statueJson.length/3)*24;
    	me.emailNewPanel.setHeight(me.emailNewPanel.getHeight()+stageHeight);
    },
    createRadio:function(param){
    	var statueJson = Ext.decode(param.statueJson);
    	var radioGroup = new Ext.form.RadioGroup( {
			xtype : 'radiogroup',
			width:'100%',
			id : 'radioGroupId',
			padding : '0 0 0 13',
			columns : 3,
			region : 'north',
			items : statueJson,
			listeners:{
				'change':function(obj, newValue){
					me.showPerson(newValue.rb.split("/")[0]);
				}
			}
		});
    	return radioGroup;
    },
    //动态选择邮件模板
    getEmailBean:function(param){
    	var result = Ext.decode(param.responseText);
    	me.template = result.template;
    	var title = result.subject;
    	var content = result.content;
    	var fileColumn = Ext.decode(result.fileColumn);
    	var fileContentList = Ext.decode(result.fileContentList);
    	Ext.getCmp("subject").setValue(title);
    	Ext.getCmp("htmleditor").setValue(content);
    	
    	if(Ext.getCmp('filepanel')){
    		Ext.getCmp('filepanel').destroy();
    		me.emailNewPanel.setHeight(me.emailNewPanel.getHeight()-100);
		}
    	if(fileContentList.length>0)
    		me.createFilepanel(result);
    	
    	var htmleditor = Ext.getCmp("htmleditor");
    	htmleditor.setFieldLabel("通知内容<br><div style='line-height:30px;'><a onclick='me.editTemplate()'>编辑模板</a></div>");
		htmleditor.setReadOnly(false);
    },
    //点击确认
    enter:function(){
    	var now_linkId = "";//转环节后的linkid
    	var now_nodeId = "";//转环节后的nodeid
    	if("passChoice"==me.function_str||"changeStatus"==me.function_str){
    		now_linkId = me.link_Id;
    		now_nodeId = me.node_Id;
    		var items = Ext.getCmp('radioGroupId').items.items;
    		for(var i=0;i<items.length;i++){
    			var item = items[i];
    			if(item.checked){
    				if("passChoice"==me.function_str){
    					me.link_Id=item.inputValue.split("/")[0];
    					me.node_Id=item.inputValue.split("/")[1];
    				}else
    					me.node_Id=item.inputValue;
    				break;
    			}
    		}
    	}
    	if(me.templateSize>0){
    		var content = Ext.getCmp("htmleditor").getValue();
			var templateId = Ext.getCmp("combo").getValue();//模板id
			var sendEmailInfo = Ext.getCmp('sendEmailInfo').checked;
			var feedCheck = Ext.getCmp('feedBack').checked;
			var noticeCheck = Ext.getCmp('sendnoticeId').checked;
			if(!templateId){//是否有模板
				Ext.getCmp('sendEmailInfo').checked=false;
				Ext.getCmp('feedBack').checked=false;
				Ext.getCmp('sendnoticeId').checked=false;
			}
			//验证内容是否为空
			var ifempty=content.replace(/<\/?[^>]*>/g,'');
			ifempty=ifempty.replace(/&nbsp;/g,'');
			if((sendEmailInfo||feedCheck||noticeCheck)&&trim(ifempty).length<=0&&templateId){
				me.sendTemplate = true;
				Ext.showAlert("通知内容为空！");
				return;
			}
    	}
		//记录日志
		var map = new HashMap();
		map.put("a0100", me.a0100s);
		map.put("node_id", me.node_Id);
		map.put("link_id", me.link_Id);
		map.put("z0301", me.z0301);
		map.put("function_str", me.function_str);
		map.put("now_linkId",now_linkId);
		map.put("now_nodeId",now_nodeId);
		//没有意见窗的不走交易类
		if(me.operation){
			map.put("description", Ext.getCmp('operationID').getValue());
			Rpc({asynchronous:true,functionId : 'ZP0000002004'},map);
			if("passChoice"==me.function_str){//面试通过，转阶段和发送邮件是一起的，所以单独记录一次转阶段日志
				var hashMap = map;
				hashMap.put("function_str", "toStage");
				Rpc({asynchronous:true,functionId : 'ZP0000002004'},map);
			}
		}
		if (me.fuId) {//执行变更状态和转阶段等操作
			map.put("status", me.node_Id);
			me.map = map;
			map.put("person",me.person);
			Rpc({functionId : me.fuId,success : me.executionSuc}, map);
		} else {//发送邮件
			map.put("sendEmail", "sendEmail");
			me.map = map;
			me.executionSuc(map);
		}
    },
    executionSuc:function(param) {
    	var result='';
    	if(param.responseText)
    		result = Ext.decode(param.responseText);
    	else
    		result = param;
		if(result.custom_name)
		   	me.map.put("custom_name", result.custom_name)//职位候选人穿透，变更状态，或转阶段后修改最上面的信息;
	    if(result.link_name)	
	    	me.map.put("link_name", result.link_name);
	    if(result.next_linkId)
	    	me.map.put("next_linkId", result.next_linkId);
	    if(result.status)
	    	me.map.put("status", result.status);
	    if(me.fuId)
    		me.map.put("z0301",result.z0301s);
	    else
	    	me.map.put("z0301",result.z0301);
	    if(result.a0100)
	    	me.map.put("a0100",result.a0100);
	    if(result.sendEmail)
			me.map.put("sendEmail",result.sendEmail);
    	if(me.templateSize==0){
    		me.emailNewPanel.close();
    		me.map.put("callback","callback");
    		Ext.callback(me.executionMethod,this,[me.map]);
			return;//没模板不发邮件
    	}
    	var templateId = Ext.getCmp("combo").getValue();
    	var sendEmailInfo = Ext.getCmp('sendEmailInfo').checked;
    	var feedCheck = Ext.getCmp('feedBack').checked;
    	var noticeCheck = Ext.getCmp('sendnoticeId').checked;
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
    		//关窗口前获取值
    		var title = Ext.getCmp("subject").getValue();
        	var content = Ext.getCmp("htmleditor").getValue();
    		me.emailNewPanel.close();
    		
    		me.map.put("title", title);
    		me.map.put("content", content);
    		me.map.put("template", me.template);
    		me.map.put("templateId", templateId);
    		me.map.put("sub_module", me.sub_module);
    		me.map.put("nModule", me.nModule);
    		me.map.put("sendTemplate", me.sendTemplate);
    		me.map.put("sendEmailInfo", sendEmail);
    		me.map.put("feedCheck", sendFeedback);
    		me.map.put("sendNotice", sendNotice);
			Rpc({functionId : 'ZP0000002001',success : function(){
				me.map.put("callback","callback");//用来判断要不要转json对象
				Ext.callback(me.executionMethod,this,[me.map]);
			}}, me.map);
    	}else{
    		me.emailNewPanel.close();
    		me.map.put("callback","callback");
    		Ext.callback(me.executionMethod,this,[me.map]);
    	}
    },
    addNoticePanel:function(){
    	return Ext.create('Ext.panel.Panel',{
    		id:'personPanelId',
    		width:'100%',
        	border:false,
        	height:184,
        	hidden:true,
        	html:'<div id="personArea" class="hj-zm-xq-two" style="overflow-y:auto;padding-left:6px;margin-top:0px;width:98%;height:180px" ></div>',
        	listeners:{
        		'afterrender':function(){
        			me.showPerson(me.link_Id);
        		}
        	}
    		
    	});
    },
    openPicker:function(){
    	me.bodyPanel.hide();
    	Ext.getDom('emailsj').src = "/module/recruitment/image/down.png";
    	Ext.getCmp('personPanelId').show();
    	Ext.getDom('personPanelsj').src = "/module/recruitment/image/up.png";
    	var picker = new PersonPicker({
    		multiple: true,
    		isPrivExpression:false,//是否启用人员范围（含高级条件）
    		validateSsLOGIN:true,
    		deprecate: me.person,
    		callback:function(c){
    			me.insertObj(c);
    		}
    	}, this);
    	picker.open();
    },
    insertObj:function(c){//新增接收人
    	for(var i=0;i<c.length;i++){
    		var el = c[i];
    		me.person.push(el.id);
    		var elem = Ext.getDom("personArea");
    		var obj = document.createElement("div");
    		obj.className="hj-nmd-dl";
    		obj.onmouseover=function(){this.getElementsByTagName('img')[0].style.visibility=''}
    		obj.onmouseleave=function(){this.getElementsByTagName('img')[0].style.visibility='hidden'}
    		var html='<img class="newDeletePic" id="'+el.id+'" onclick="me.deleteObj(this)" style="width: 14px; height: 14px;visibility:hidden;" src="/workplan/image/remove.png" />';
    		obj.innerHTML=html+'<dl><dt title="'+el.name+'"><img class="img-circle" src="'+el.photo+'" /></dt><dd class="text-ellipsis">'+el.name+'</dd></dl>';
    		elem.appendChild(obj);
    	}
    },
    deleteObj:function(el){//删除接收人
    	Ext.Array.remove(me.person,el.id);
    	var obj = el.parentNode;
    	obj.parentNode.removeChild(obj);
    },
    showPerson:function(linkId){
    	me.person = [];
    	var parentNode = Ext.getDom("personArea");
    	var childs = Ext.getDom("personArea").childNodes;
    	for(var i = childs.length;i>0; i--){
    		parentNode.removeChild(childs[0]);
    	}
    	var map = new HashMap();
    	map.put("z0301",me.z0301);
    	map.put("linkId",linkId);
        Rpc({functionId : 'ZP0000002133',success :function(response){
        	var value = response.responseText;
        	var map	 = Ext.decode(value);
        	me.insertObj(map.defPerson);
        }}, map);
    }
});