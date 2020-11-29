/**
 * 服务须知窗口
 */
Ext.define("ServiceClient.serviceHome.ServiceNoticeWindow",{
	extend: 'Ext.window.Window',
	requires:["ServiceClient.serviceHome.PrintService"],
	id: 'serviceNotice',
	layout: 'vbox',
    title: sc.home.serviceGuideline,
    width: 730,
    modal: true,
    resizable: false,
    draggable: false,
	constructor : function(config) {
		this.callParent();
		this.init(config);
	},
	init : function(config) {
		this.ip = config.ip;
		this.serviceConfig = config.serviceConfig;
		this.serviceParamData = config.serviceParamData;
		var guidePanel;
		var noticePanel;
		var ifCheck = this.serviceConfig.notice_enable;
		var config_input_enable = this.serviceConfig.config_input_enable;
		noticePanel = this.createNoticePanel(this.serviceParamData,config_input_enable,ifCheck); //字段项
		if(ifCheck == "1"){ //服务须知
			guidePanel = this.createGuidePanel(this.serviceConfig);
			noticePanel.add(guidePanel);
		}
		this.add(noticePanel);
	},
	listeners: {
		'close':function(){
			VirtualKeyboard.close(); //关闭软键盘
		 }
    },
	createNoticePanel : function(serviceParamData,config_input_enable,ifCheck){
		var me = this;
		var noticePanel = Ext.create('Ext.form.Panel',{
			width:"100%",
			border:false,
			itemId:'noticePanel',
			bodyPadding:'10 0 0 30',
			buttonAlign:'center',
			buttons: [{
		        text: sc.setting.ok,
		        itemId: "agreeGuidelines",
		        formBind: true,
		        handler: function (btn) {
		        	VirtualKeyboard.close(); //关闭软键盘
			        Ext.getCmp('serviceNotice').query('#agreeGuidelines')[0].setDisabled(true); //防止重复点击
		        	var serviceGuidelines = Ext.getCmp('serviceNotice');
		        	var serviceConfig = serviceGuidelines.serviceConfig;
					var inputEnable = serviceConfig.config_input_enable;
					if(inputEnable == "1"){
					    //执行数据保存
					    var fieldData = serviceGuidelines.query('#noticePanel')[0].getValues(); //获取指标值
					    delete fieldData.hiddenField;
					    delete fieldData.agreeServiceNotice;
					    var map = new HashMap();
					    map.put("fieldData", fieldData);
				    	Rpc({functionId:'SC000000007',async:false,success:function(res){
				    		var info = Ext.decode(res.responseText);
				    		var serviceParamData = info.serviceParamData;
						},scope:this},map);
					}
		        	var serviceMainBox = Ext.getCmp('serviceMainBox');
		            if (ifCheck == "1" || inputEnable == "1") {
    		        	serviceMainBox.removeAll(false);
		            	var ip = serviceGuidelines.ip;
		            	Ext.MessageBox.show({  
		                    title:sc.home.tip,   
		                    msg:sc.home.loadTip, //正在加载,请稍候...  
		                    progress:true,   
		                    width:300,   
		                    wait:true,   
		                    waitConfig:{interval:600},   
		                    closable:true 
		                });
		                this.up('window').destroy();
		                var printService = Ext.create("ServiceClient.serviceHome.PrintService", {
		                    serviceId: serviceConfig.serviceId,
		                    ins_id: serviceConfig.ins_id,
		                    task_id: serviceConfig.task_id,
		                    ifCheck: serviceConfig.notice_enable,//是否启用服务须知
		                    templateId: serviceConfig.templateId,
		                    ip: ip,
		                    templateType: serviceConfig.templateType
		                });
		                serviceMainBox.add(printService);
		            } else {
		                this.up('window').destroy();
		            }
		        }
		    },{
		    	text: sc.setting.cancel, //取消
		    	margin:'0 30',
		    	handler:function(){
		    		VirtualKeyboard.close(); //关闭软键盘
		    		this.up('window').destroy();
		    	}
		    }]
		});
		if(config_input_enable == "1"){
			me.createFieldPanel(serviceParamData,noticePanel);
		}
		if(ifCheck == "1"){
			noticePanel.add({xtype:'textfield',name:'hiddenField',itemId:'hiddenField',hidden:true,allowBlank:false});
		}
		return noticePanel;
	},
	createGuidePanel : function(serviceConfig){
		var guidePanel = Ext.create('Ext.Panel',{
			layout: {
				type: 'vbox'
			},
			width:620,
			border:false,
			margin:'20px',
			items: [{
                xtype: 'panel',
                scrollable:'y',
                height: 270,
                width: 620,
                html: serviceConfig.description
            }, {
                xtype: 'checkboxgroup',
                id: 'agreeServiceNotice',
                margin: '0 0 0 -4',
                items: [{
                    boxLabel: sc.home.serviceGuidelines,
                    inputValue: '1',
                    checked: false
                }],
                listeners:{
                	element:'el',
                    click:function(){
                        var isCheck = Ext.getCmp('agreeServiceNotice').getValue().agreeServiceNotice;//是否勾选服务须知
                        var hiddenField = Ext.getCmp('serviceNotice').query('#hiddenField')[0];
                        if(!isCheck){
                        	hiddenField.setValue("");
                        }else{
                        	hiddenField.setValue("true");
                        }
                    },
                    scope:this
                }
            }]
		});
		return guidePanel;
	},
	createFieldPanel:function(serviceParamData,noticePanel){
		var me = this;
		var titleArea = Ext.create('Ext.Panel',{
            border:false,
			html:sc.setting.inputInfo,//填写信息
			margin:'0 302 10'
	    });
		var fieldArea = Ext.create('Ext.Panel',{
			layout: {
				type: 'table',
				columns: 3
			},
            width:650, //宽度650
            itemId:"areaPanel",
            margin:'0 10',
            border:false,
            listeners: {
		        afterrender: function() {
		            Ext.each(this.items.items, function (field) {
		            	if(!field.disabled){
		                	Ext.create('Ext.tip.ToolTip', Ext.applyIf(field.tooltip, {target: field.getEl()}));
		            	}
		            });
		        }
		    }
	    });
	    var remarkArea = Ext.create('Ext.Panel',{
			layout: {
				type: 'vbox'
			},
            width:650,
            itemId:"remarkPanel",
            margin:'0 10',
            border:false
	    });
		for(var i = 0; i < serviceParamData.length; i++){
			var field = serviceParamData[i];
			var fieldCmp;
			var i9999;
			if("i9999" in field)
				i9999 = field.i9999;
			var setId = field.setId;
			var itemId = field.itemId;
			var isWrite = field.isWrite;
			var itemDesc = field.itemDesc;
			var itemType = field.itemType;
			var codeSetId = field.codeSetId;
			var codeItemId = field.codeItemId;
			var fieldValue = field.fieldValue;
			fieldCmp = me.createFieldCmp(setId,codeSetId,itemType,itemDesc,itemId,isWrite,codeItemId,fieldValue,i9999);
			if("M" == itemType)
				remarkArea.add(fieldCmp);
			else
				fieldArea.add(fieldCmp);
		}
		noticePanel.add(titleArea);
		noticePanel.add(fieldArea);
		noticePanel.add(remarkArea);
    },
	createFieldCmp:function(setId,codeSetId,itemType,itemDesc,itemId,isWrite,codeItemId,fieldValue,count){
		var me = this;
    	var fieldCmp;
    	var fieldId = "A01" == setId ?setId+'`'+itemId : setId+'`'+itemId+'`'+count;
    	if("A" == itemType){
			if("0" == codeSetId){ //字符
				fieldCmp = {
					xtype:'textfield',
					id:fieldId,
					name:fieldId,
					disabled:isWrite == "1"? false : true,
					width:210,
					labelWidth:60,
					allowBlank:false,
					value:fieldValue,
					fieldLabel:'<span style="font-family:"微软雅黑";">'+ itemDesc +'</span>',
					afterLabelTextTpl:isWrite == "1" ? "<font color='red'> * </font>" : "",
					style:'margin:10px 0;text-align:right;',
					tooltip:{
		                anchor: 'bottom',
		                trackMouse: true,
		                anchorOffset: 25,
		                items: [{
		                    xtype: 'container',
		                    width: 120,
		                    height: 20,
		                    html: itemDesc,
		                    style: 'overflow:hidden;'
		                }]
		            },
		            listeners: {
				        focus:function(e){
							VirtualKeyboard.toggle(fieldId+'-inputEl', 'softkey');
							VirtualKeyboard.switchLayout("US US");
							e.emptyText = "";
						},
						blur:function(){
							VirtualKeyboard.close();
						},
						focusleave:function(){
							var fieldName = this.name;
							var map = new HashMap();
						    map.put("fieldName", fieldName);
					    	Rpc({functionId:'SC000000008',async:false,success:function(res){
					    		var info = Ext.decode(res.responseText);
					    		var itemLen = parseInt(info.itemLen);
					    		var bytenum = 0; //字节数
		                        var varlength = 0; //字符长度
		                        for (var i = 0; i < this.value.length; i++) {
		                            var a = this.value.charAt(i);
		                            if (a.match(/[^\x00-\xff]/ig) != null) { //汉字
		                                bytenum = bytenum+2; //一个汉字占两个字节
		                                varlength = varlength+1;
		                            }else {
		                                bytenum = bytenum+1; //字母数字等占一个字节
		                                varlength = varlength+1;
		                            }
		                            if(bytenum==itemLen+1||bytenum==itemLen+2){
		                            	var afterWarn = sc.setting.overlength.substring(5);
		                            	var pleaseEnter = sc.setting.entertypename.substring(0,3);
		                                Ext.MessageBox.alert(sc.setting.promptmessage, pleaseEnter+itemLen+afterWarn);//提示信息
		                                this.setValue(this.value.substring(0,varlength-1));
		                                return;
		                            }
		                        }
							},scope:this},map);
						}
				    }
				}
			}else{ //代码
				fieldCmp = {
					xtype:'codecomboxfield',
					name:fieldId,
					disabled:isWrite == "1"? false : true,
					codesetid:codeSetId,
					width:210,
					ctrltype:'1',
					labelWidth:60,
					editable:false,
					value:codeItemId+"`"+fieldValue, //codeitemid`codeitemdesc
					inputable:true,
					allowBlank:false,
					fieldLabel:'<span style="font-family:"微软雅黑";">'+ itemDesc +'</span>',
					afterLabelTextTpl:isWrite == "1" ? "<font color='red'> * </font>" : "",
					style:'margin:10px 0;text-align:right;'
				}
			}
		}else if("D" == itemType){
			fieldCmp = {
				xtype:'datetimefield',
				name:fieldId,
				format:'Y-m-d',
				disabled:isWrite == "1"? false : true,
				width:210,
				labelWidth:60,
				editable:false, //设置只能选择
				useStrict:false,
				allowBlank:false,
				value:fieldValue,
				fieldLabel:'<span style="font-family:"微软雅黑";">'+ itemDesc +'</span>',
				afterLabelTextTpl:isWrite == "1" ? "<font color='red'> * </font>" : "",
				style:'margin:10px 0;text-align:right;'
			}
		}else if("N" == itemType){
			fieldCmp = {
				xtype:'numberfield',
				name:fieldId,
				inputId:fieldId,
				disabled:isWrite == "1"? false : true,
				width:210,
				labelWidth:60,
				allowBlank:false,
				hideTrigger:false,
//				decimalPrecision:field.formatlength, //设置精度
				value:fieldValue,
				fieldLabel:'<span style="font-family:"微软雅黑";">'+ itemDesc +'</span>',
				afterLabelTextTpl:isWrite == "1" ? "<font color='red'> * </font>" : "",
				style:'margin:10px 0;text-align:right;',
				listeners: {
			        focus:function(e){
						VirtualKeyboard.toggle(fieldId, 'softkey');
						VirtualKeyboard.switchLayout("US US");
						e.emptyText = "";
					},
					blur:function(){
						VirtualKeyboard.close();
					}
				}
			}
		}else if("M" == itemType){
			fieldCmp = {
				xtype:'textfield',
				id:fieldId,
				name:fieldId,
				disabled:isWrite == "1"? false : true,
				width:630,
				labelWidth:60,
				allowBlank:false,
				value:fieldValue,
				fieldLabel:'<span style="font-family:"微软雅黑";">'+ itemDesc +'</span>',
				afterLabelTextTpl:isWrite == "1" ? "<font color='red'> * </font>" : "",
				style:'margin:10px 0;text-align:right;',
		        listeners: {
			        focus:function(e){
						VirtualKeyboard.toggle(fieldId+'-inputEl', 'softkey');
						e.emptyText = "";
					},
					blur:function(){
						VirtualKeyboard.close();
					}
			    }
			}
		}
		return fieldCmp;
    }
});