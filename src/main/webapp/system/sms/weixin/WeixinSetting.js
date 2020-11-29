Ext.define("EHR.weixin.WeixinSetting", {
	extend : 'Ext.panel.Panel',
	requires:["SYSF.FileUpLoad"],
	flex : 1,
	servicelist : [],// 服务号列表容器
	menus : [],// 菜单数据
	serverid : 0,// 当前数据的serverid
	recruitFuncList : [],//
	maxMenuOrder : 0,// 最大的一级菜单顺序值
	maxFucOrder : 0,// 最大的二级菜单顺序值
	nameIndex:0,
	labelLength:0,//comBoBox的label宽度
	variableSet:new HashMap(),
	layout : {
		type : 'vbox'
	},
	scrollable:'y',
	initComponent : function() {
		this.callParent();
		this.initForEach();
		this.getConfig();
		this.titleInit();
		this.serviceConfig();
		this.addNoticeTemplate();
		this.funsConfig();
		this.initData = this.service.getValues();//初始化时，获取初始数据
	},
	/**
	 * ie下或其他情况不支持forEach循环时自建forEach函数
	 */
	initForEach : function() {
		if (!Array.prototype.forEach) {
			Array.prototype.forEach = function forEach(callback, thisArg) {
				var T, k;
				if (this == null) {
					throw new TypeError("this is null or not defined");
				}
				var O = Object(this);
				var len = O.length >>> 0;
				if (typeof callback !== "function") {
					throw new TypeError(callback + " is not a function");
				}
				if (arguments.length > 1) {
					T = thisArg;
				}
				k = 0;
				while (k < len) {

					var kValue;
					if (k in O) {
						kValue = O[k];
						callback.call(T, kValue, k, O);
					}
					k++;
				}
			};
		}
	},
	/**
	 * 初始化获取数据
	 */
	getConfig : function() {
		var me = this;
		var vo = new HashMap();
		vo.put("type","service");
		Rpc({
			  functionId : 'SYS0000001001',
			  async : false,
			  success : function(data) {
				  var result = Ext.decode(data.responseText);
				  me.servicelist = result.paramInfo.servers;// 服务号列表
				  me.noticeMap = result.paramInfo.noticeMap;// 菜单数据
				  me.menus = result.paramInfo.params;// 菜单数据
				  me.funcList =result.paramInfo.funcList;
				  me.funcList.push({'itemid':'myResume','itemdesc':weixin.setting.personalResume});
				  me.funcList.push({'itemid':'applyPost','itemdesc':weixin.setting.applyForPosition});
				  me.funcList.push({'itemid':'collectPost','itemdesc':weixin.setting.collectionPosition});
				  me.funcList.splice(0,0,{'itemid':'','itemdesc':weixin.setting.pleaseSelect});
				  me.serverid = result.paramInfo.serverid;// 初始化时数据对应的serverid
				  me.recruitFuncList = result.recruitFuncList;// 我要应聘 2级功能菜单功能
				  if(!me.servicelist){
					  me.servicelist = new Array();
				  }
				  if(!me.menus){
					  me.menus = new Array();
					  return;
				  }
				  me.menus.forEach(function(menu, index) {// 获取二级菜单最大的order（顺序值）
					  menu.functions.forEach(function(tempfunc, tempindex) {
						  if (parseInt(tempfunc.order) > me.maxFucOrder) {
							  me.maxFucOrder = parseInt(tempfunc.order);
						  }
					  });
				  }
				  );
			  },
			  scope : this
		  }, vo);
	},
	/**
	 * 顶端服务号、公众号选择图标
	 * 
	 */
	titleInit : function() {
		var me = this;
		var title = Ext.create('Ext.panel.Panel', {
			  width : '100%',
			  border : 0,
			  layout : {
				  type : 'hbox',
				  pack : 'center'
			  },
			  style:'margin-top:4px;margin-bottom:4px',
			  items : [{
				  xtype : "panel",
				  border : 0,
				  layout : {
					  type : "vbox"
				  },
				  style:'cursor:pointer',
				  items : [{
					  xtype : 'image',
					  height : 60,
					  width : 60,
					  src : '/system/sms/weixin/images/fwh.png'
				  }, {
					  xtype : "component",
					  border : 0,
					  html : weixin.setting.serviceNumber,//服务号
					  margin : "4 0 0 11",
					  itemId :'service_title',
					  style : 'color:#1B4A98;',
					  layout : {
						  pack : 'center'
					  }
				  }],
				  listeners : {
					  click : {
						  element : 'el',
						  fn : function() {
						 	  title.query('#service_title')[0].setStyle({color:'#1D8EFF'});
						 	  title.query('#enterprise_title')[0].setStyle({color:''});
						      if(Ext.getCmp("service")){
                                  Ext.getCmp("service").show();
						      }
						      if(Ext.getCmp("menuconfig")){
	                              Ext.getCmp("menuconfig").show();
						      }
						      if(Ext.getCmp("noticeTemplatePanel")){
                                  Ext.getCmp("noticeTemplatePanel").show();
                              }
                              var enterprisePanel = me.query('#enterprise')[0];//企业号页面
                              if(enterprisePanel){
	                              enterprisePanel.hide();
                              }
						  }
					  }
				  }
			  }, {
				  xtype : "component",// 增加服务号公众号图标间距
				  border : 0,
				  width : 20
			  }, {
				  xtype : "panel",
				  border : 0,
				  layout : {
					  type : "vbox"
				  },
				  style:'cursor:pointer',
				  items : [{
					  xtype : 'image',
					  height : 60,
					  width : 60,
					  src : '/system/sms/weixin/images/dyh.png'
				  }, {
					  xtype : "component",
					  border : 0,
					  html : weixin.setting.enterprise,//企业号
					  itemId :'enterprise_title',
					  margin : "4 0 0 6",
					  layout : {
						  pack : 'center'
					  }
				  }],
				  listeners : {
					  click : {
						  element : 'el',
						  fn : function() {
						      title.query('#service_title')[0].setStyle({color:''});
						 	  title.query('#enterprise_title')[0].setStyle({color:'#1D8EFF'});
						      if(Ext.getCmp("service")){
	                              Ext.getCmp("service").hide();
						      }
						      if(Ext.getCmp("menuconfig")){
	                              Ext.getCmp("menuconfig").hide();
						      }
						      if(Ext.getCmp("noticeTemplatePanel")){
                                  Ext.getCmp("noticeTemplatePanel").hide();
                              }
                              var bodyPanel = me.query('#enterprise')[0];
                              if(bodyPanel){
                                  bodyPanel.show();
                                  return;
                              }
                              var vo = new HashMap();
                              vo.put("type","enterprise");
                              Rpc({functionId:'SYS0000001001',async:false,success:function(res){
                                  var result = Ext.decode(res.responseText);
                                  var paramInfo = result.paramInfo;//app列表信息、参数配置信息
                                  me.savePath = paramInfo.savePath;//logo的存储路径
                                  me.createFirmDisplay(paramInfo);//企业号界面实现
                              },scope:this},vo);
						  }
					  }
				  }
			  }]
		  });
		this.add(title);
	},
	/**
	 * 服务号配置界面
	 */
	serviceConfig : function() {
		var me = this;
		var service = Ext.create("Ext.panel.Panel", {
			  id : "service",
			  width : "100%",
			  border : 0,
			  layout:'vbox',
			  style:'margin-bottom:2px',
			  items : [{
				  xtype : "panel",
				  width : "100%",
				  height : 40,
				  layout : 'hbox',
				  border : 0,
				  bodyStyle : {
					  background : "#e4e4e4"
				  },
				  items : [{
					  xtype : "panel",
					  width:200,
					  html : "<font size=4px>"+weixin.setting.serviceNumberSetting+"</font>",//服务号设置
					  margin : "6 0 0 10",
					  border : 0,
					  bodyStyle : {
						  background : "#e4e4e4"
					  }
				  },{
				  	  xtype:'component',
				  	  flex:1
				  },{
				  	  xtype:'image',
				  	  src:'/system/sms/weixin/images/down.png',
				  	  itemId:'serviceDown',
				  	  width:32,
				  	  height:32,
				  	  style:'margin-top:4px;margin-right:20px;cursor:pointer;',
				  	  listeners:{
				  	  	  click:{
				  	  	      element:'el',
				  	  	      fn:function(){
				  	  	      	  service.query('#serviceUp')[0].setHidden(false);
				  	  	          service.query('#serviceDown')[0].setHidden(true);
				  	  	          Ext.getCmp('serviceParam').setHidden(true);
				  	  	      }
				  	  	  }
				  	  }
				  },{
				  	  xtype:'image',
				  	  src:'/system/sms/weixin/images/up.png',
				  	  itemId:'serviceUp',
				  	  width:32,
				  	  height:32,
				  	  hidden:true,
				  	  style:'margin-top:4px;margin-right:20px;cursor:pointer;',
				  	  listeners:{
				  	  	  click:{
				  	  	      element:'el',
				  	  	      fn:function(){
				  	  	          service.query('#serviceUp')[0].setHidden(true);
				  	  	          service.query('#serviceDown')[0].setHidden(false);
				  	  	          Ext.getCmp('serviceParam').setHidden(false);
				  	  	      }
				  	  	  }
				  	  }
				  }]
			  }]
		  });
		service.add(me.serviceParamPanel());
		me.add(service);
	},
    /**
     * 添加通知模板，用于服务号推送消息的相关配置
     */
	addNoticeTemplate:function(){
		var me = this;
		var noticeTemplate = Ext.create("Ext.panel.Panel", {
              id : "noticeTemplatePanel",
              width : "100%",
              border : 0,
              layout:'vbox',
              style:'margin-bottom:2px',
              items : [{
                  xtype : "panel",
                  width : "100%",
                  height : 40,
                  layout : 'hbox',
                  border : 0,
                  bodyStyle : {
                      background : "#e4e4e4"
                  },
                  items : [{
                      xtype : "panel",
                      width:200,
                      html : "<font size=4px>"+weixin.setting.noticeTemplate+"</font>",//通知模板
                      margin : "6 0 0 10",
                      border : 0,
                      bodyStyle : {
                          background : "#e4e4e4"
                      }
                  },{
                      xtype:'component',
                      flex:1
                  },{
                      xtype:'image',
                      src:'/system/sms/weixin/images/down.png',
                      hidden:true,
                      itemId:'noticeDown',
                      width:32,
                      height:32,
                      style:'margin-top:4px;margin-right:20px;cursor:pointer;',
                      listeners:{
                          click:{
                              element:'el',
                              fn:function(){
                                  noticeTemplate.query('#noticeUp')[0].setHidden(false);
                                  noticeTemplate.query('#noticeDown')[0].setHidden(true);
                                  Ext.getCmp('templateConfigId').setHidden(true);
                              }
                          }
                      }
                  },{
                      xtype:'image',
                      src:'/system/sms/weixin/images/up.png',
                      itemId:'noticeUp',
                      width:32,
                      height:32,
                      style:'margin-top:4px;margin-right:20px;cursor:pointer;',
                      listeners:{
                          click:{
                              element:'el',
                              fn:function(){
                                  noticeTemplate.query('#noticeUp')[0].setHidden(true);
                                  noticeTemplate.query('#noticeDown')[0].setHidden(false);
                                  Ext.getCmp('templateConfigId').setHidden(false);
                              }
                          }
                      }
                  }]
              }]
          });
          noticeTemplate.add(me.createNoticeTemplateConfig());
          me.add(noticeTemplate);
	},
	/**
	 * 通知模板配置项
	 * @return {}
	 */
    createNoticeTemplateConfig:function(){
    	var me = this;
    	var paramData = [];
    	paramData.push(["infoTitle",weixin.setting.infoTitle]);//msg_title
    	paramData.push(["posName",weixin.setting.posName]);//职位名称
    	paramData.push(["resumeState",weixin.setting.resumeState]);//简历筛选状态
    	paramData.push(["operateTime",weixin.setting.operateTime]);//操作时间
    	paramData.push(["infoDetail",weixin.setting.infoDetail]);//msg_detail
    	var paramStore = Ext.create('Ext.data.ArrayStore',{
    		storeId:"paramStore",
    	    fields:['id','displayText'],
            data:paramData
    	});
        var pattern = /\{{2}.+\.DATA\}{2}/g;//匹配{{。。。.DATA}}
    	if(me.noticeMap.templateContent){
            var variableArr = me.noticeMap.templateContent.match(pattern);
    	}
    	var templateConfigPanel = Ext.create('Ext.form.Panel',{//通知模板配置panel
            border:false,
            hidden:true,//默认隐藏    不然屏幕展示不全
            id:'templateConfigId',
            style:'margin-left:40px;',
            defaults:{
                xtype:'textfield',
                margin:'20 0',
                minWidth:450
            },
            items : [{
                fieldLabel : weixin.setting.infoTemplateId,//消息模板id
                value : me.noticeMap.templateId,
                //beforeLabelTextTpl:"<font color='red'> * </font>",
                labelAlign : 'left',
                style:'margin-top:20px',
                id : 'infoTemplateId',
                allowBlank : false,
                cls:'field-input'//更改边框样式
            }, {
            	xtype:"textareafield",
                fieldLabel : weixin.setting.templateContent,//模板内容
                value : me.noticeMap.templateContent,
                //beforeLabelTextTpl:"<font color='red'> * </font>",
                labelAlign : 'left',
                style:'margin-top:20px',
                height:150,
                id : 'templateContent',
                allowBlank : false,
                listeners:{
                	change:function(owner,newValue){
                		var templateVariable = templateConfigPanel.query('#templateVariable')[0];
                		templateVariable.removeAll();
                		me.variableSet = new HashMap();
                		me.labelLength = 0;
                		me.noticeMap.templateContent = newValue;
                        if(newValue){
                            var variableArray = newValue.match(pattern);
                            if(variableArray){
                            	me.computedLabelWidth(variableArray);
                                var variableCount = variableArray.length;
                                for(var i=0;i<variableCount;i++){
                            		var variableCombobox = me.addVariable(newValue,variableArray[i],paramStore,i);
                        			templateVariable.add(variableCombobox);
                                }
                            }
                        }
                	}
                }
            },{
            	xtype:'panel',
                layout:'hbox',
                border:false,
            	items:[{
            		xtype:'component',
            		html:weixin.setting.variableMap//模板变量对应
            	},{
            	    xtype:'panel',
                    itemId:'templateVariable',
                    border:false,
                    margin:"0 0 0 30",
                    listeners:{
                        render:function(){
                        	if(variableArr){
                        	    me.computedLabelWidth(variableArr);
                        		for(var i=0;i<variableArr.length;i++){
                                    var variableCombobox = me.addVariable(me.noticeMap.templateContent,variableArr[i],paramStore,i,true);
                                    this.add(variableCombobox);
                        		}
                        	}
                        }
                    }
            	}]
            }]  
        });
    	return templateConfigPanel;
    },
    /**
     * 计算变量的labelwidth
     * @param {} variableArr
     */
    computedLabelWidth:function(variableArr){
    	var me = this;
        var format = /[\u4e00-\u9fa5]/g;//匹配汉字
        var chineseCount = 0;
        for(var i=0;i<variableArr.length;i++){
            if(variableArr[i].match(format)){
                chineseCount = variableArr[i].match(format).length;
            }
            var tempLength = chineseCount*12+(variableArr[i].length-chineseCount)*7+10;
            if(tempLength>me.labelLength){//计算labelWidth应该是多少
                me.labelLength = tempLength;
            }
        }
    },
    /**
     * 动态创建变量对应下拉框
     * @param {} newValue 大文本的新值
     * @param {} variable 某个模板变量
     * @param {} paramStore 数据源
     * @param {} flag 是初始化则value读取数据库配置，否则value清空
     * @return {} 变量下拉框
     */
    addVariable:function(newValue,variable,paramStore,i,flag){
    	var me = this;
    	var index = variable.indexOf(".");
    	var textId = variable.substring(2,index);
    	var variableComboBox = Ext.create("Ext.form.field.ComboBox",{
    	    fieldLabel :variable,
            id : "variable"+i+textId,
            value:flag?me.noticeMap[textId]:"",
            labelWidth:me.labelLength,
            store:paramStore,
            valueField:'id',
            displayField:'displayText',
            allowBlank : false,
            editable:false,
            emptyText:weixin.setting.pleaseSelect,//请选择
            beforeLabelTextTpl:"<font color='red'> * </font>",
            listeners:{
                change:function(){
                	me.variableSet[this.id.substring(9)] = this.getValue();
                }
            }
    	});
    	me.variableSet.put(textId,variableComboBox.getValue());
    	return variableComboBox;
    },
	/**
	 * 菜单配置界面
	 * 
	 * 
	 */
	funsConfig : function() {
		var me = this;
		me.service = Ext.create("Ext.form.Panel", {
			  id : "menuconfig",
			  width : "100%",
			  //flex:1,
			  border : 0,
			  layout : {
				  type : "vbox"
			  },
			  items : [{
				  xtype : "panel",
				  width : "100%",
				  height : 40,
				  layout : {
					  type : 'hbox',
					  align : "middle"
				  },
				  border : 0,
				  bodyStyle : {
					  background : "#e4e4e4"
				  },
				  items : [{
					  xtype : "panel",
					  html : "<font size=4px>"+weixin.setting.customMenu+"</font>",//自定义菜单
					  margin : "6 0 0 10",
					  flex:1,
					  border : 0,
					  bodyStyle : {
						  background : "#e4e4e4"
					  }
				  }, {
					  xtype : 'button',
					  height:30,
					  width:60,
					  html : '<div style="font-size:14px;">'+weixin.setting.save+'</div>',//保存
					  listeners : {
						  click : function() {
							  if(!Ext.getCmp('APPID').getValue()){
							   	  Ext.Msg.alert(weixin.setting.promptmessage,weixin.setting.appidfaildesc);
							   	  return;
							  }
							  if(!Ext.getCmp('AppSecret').getValue()){
							   	  Ext.Msg.alert(weixin.setting.promptmessage,weixin.setting.appsecritfaildesc);
							   	  return;
							  }
							  if(!Ext.getCmp('app_url').getValue()){
							   	  Ext.Msg.alert(weixin.setting.promptmessage,weixin.setting.appurlfaildesc);
							   	  return;
							  }
							  /*
							  if(!Ext.getCmp('infoTemplateId').getValue()){
							   	  Ext.Msg.alert(weixin.setting.promptmessage,weixin.setting.stopTemplateIdNull);
							   	  return;
							  }
							  if(!Ext.getCmp('templateContent').getValue()){
							   	  Ext.Msg.alert(weixin.setting.promptmessage,weixin.setting.stopTemplateContentNull);
							   	  return;
							  }
							  var templateVariable = Ext.getCmp('templateConfigId').query('#templateVariable')[0];
							  var variableCombos = templateVariable.items.items;
							  for(var i=0;i<variableCombos.length;i++){
							      if(!variableCombos[i].getValue()){
    							   	  Ext.Msg.alert(weixin.setting.promptmessage,weixin.setting.stopTemplateVariableNull);
    							   	  return;
							      }
							  }
							  */
							  var vo = me.menusData('save');
                              if(!vo){//获取数据过程中发现数据不正确,就不调用保存了
                              	return;
                              }
							  var serviceConfig = new HashMap();
							  serviceConfig.put('appid',Ext.getCmp('APPID').getValue());
							  serviceConfig.put('appSecret',Ext.getCmp('AppSecret').getValue());
							  serviceConfig.put('url',Ext.getCmp('app_url').getValue());
							  serviceConfig.put('infoTemplateId',Ext.getCmp('infoTemplateId').getValue());
                              serviceConfig.put('templateContent',Ext.getCmp('templateContent').getValue());
                              vo.put('serviceConfig',serviceConfig);//参数配置
                              vo.put('variableSet',me.variableSet);//变量对应

							  Rpc({functionId : 'SYS0000001006',async : false,success : function(data){
								  var resp = Ext.decode(data.responseText);
								  if(resp.result){
									  Ext.Msg.alert(weixin.setting.promptmessage,weixin.setting.saveSuccess);//保存成功
								  }else{
									  Ext.Msg.alert(weixin.setting.promptmessage,weixin.setting.saveFail);//保存失败
								  }
							  }}, vo);
						  }
					  }
				  }, {
					  xtype : "button",
					  height:30,
					  width:60,
					  style:'margin-right:10px;margin-left:10px;font-size:14px;',
					  html:'<div style="font-size:14px;">'+weixin.setting.publish+'</div>',//发布
					  listeners:{
						  click:function(){
						  	  if(!Ext.getCmp('APPID').getValue()){
							   	  Ext.Msg.alert(weixin.setting.promptmessage,weixin.setting.appidfaildesc);
							   	  return;
							  }
							  if(!Ext.getCmp('AppSecret').getValue()){
							      Ext.Msg.alert(weixin.setting.promptmessage,weixin.setting.appsecritfaildesc);
							      return;
							  }
							  if(!Ext.getCmp('app_url').getValue()){
							   	  Ext.Msg.alert(weixin.setting.promptmessage,weixin.setting.appurlfaildesc);
							   	  return;
							  }
							  /*
							  if(!Ext.getCmp('infoTemplateId').getValue()){
                                  Ext.Msg.alert(weixin.setting.promptmessage,weixin.setting.stopTemplateIdNull);
                                  return;
                              }
                              if(!Ext.getCmp('templateContent').getValue()){
                                  Ext.Msg.alert(weixin.setting.promptmessage,weixin.setting.stopTemplateContentNull);
                                  return;
                              }
                              var templateVariable = Ext.getCmp('templateConfigId').query('#templateVariable')[0];
                              var variableCombos = templateVariable.items.items;
                              for(var i=0;i<variableCombos.length;i++){
                                  if(!variableCombos[i].getValue()){
                                      Ext.Msg.alert(weixin.setting.promptmessage,weixin.setting.stopTemplateVariableNull);
                                      return;
                                  }
                              }
                              */
							  var vo = me.menusData('release');
							  if(!vo){//获取数据过程中发现数据不正确,就不调用发布了
                                  return;
                              }
							  var serviceConfig = new HashMap();
							  serviceConfig.put('appid',Ext.getCmp('APPID').getValue());
							  serviceConfig.put('appSecret',Ext.getCmp('AppSecret').getValue());
							  serviceConfig.put('url',Ext.getCmp('app_url').getValue());
                              serviceConfig.put('infoTemplateId',Ext.getCmp('infoTemplateId').getValue());
                              serviceConfig.put('templateContent',Ext.getCmp('templateContent').getValue());
							  vo.put('serviceConfig',serviceConfig);//参数配置
                              vo.put('variableSet',me.variableSet);//变量对应
							  
							  Rpc({functionId:'SYS0000001007',success:function(resp){
								  var data = Ext.decode(resp.responseText);
								  if(data.result == 1){
									  Ext.Msg.alert(weixin.setting.promptmessage,weixin.setting.publishSuccess);
								  }else if(data.result == 2){
									  Ext.Msg.alert(weixin.setting.promptmessage,weixin.setting.saveFail);
								  }else if(data.result == 3){
								     var errcode = data.errcode;
								     if(errcode){
								     	var errorMsg = weixin.setting.publishFail;//默认提示这个
								     	if(errcode=='40013'){
								     	  errorMsg = weixin.setting.invalidAppid;
								     	}else if(errcode=='-1'){
								     	  errorMsg = weixin.setting.weixinSystembusy;
								     	}else if(errcode=='40001'){
								     		errorMsg = weixin.setting.invalidAppid;
								     	}
								     	Ext.Msg.alert(weixin.setting.promptmessage,errorMsg);
								     }else{
    								  	 Ext.Msg.alert(weixin.setting.promptmessage,weixin.setting.publishFail);
								     }
								  }
							  }},vo);
						  }
					  }
				  }]
			  },{
				  xtype:'container',
				  id:'serverContainer',
				  width:'100%',
				  //scrollable:'y',
			  	  //flex:1,
				  items:[]
			  }]
		  });
		me.dealMenu(me.menus);// 添加一级菜单
		me.add(me.service);

	},
	/**
	 * 自定义菜单 保存&发布功能
	 * flag  sava  保存   release 发布
	 */
	menusData:function(flag){
		  // 解析并封装菜单数据到后台保存
		  var me = this;
		  var values = me.service.getValues();
		  var params = new Array();
		  var allMenuMap = new HashMap();
		  var firstNoValueMap = new HashMap();//存储一级菜单没有值的Map,一级菜单没有value也可以但是必须得有二级菜单,否则需要给给予提示不允许保存
		  for (var key in values) {
			  var temps = key.split('-');// 所有数据的键
			  var name = temps[0];// 开头的值,[firstname,firsturl/*firsttype*/,secondname,secondfuncmenu,secondfuncurl/*secondtype*/]只能从这几个里面选一个
			  if ("firstname" == name) {// 一级菜单只有两位
				  var menuMap;
				  if (allMenuMap.get(key)) {
					  menuMap = allMenuMap.get(key);
				  } else {
					  menuMap = new HashMap();
				  }
				  var nameValue = values[key];
				  var firstorder = temps[1];
				  var urlkey = 'firsturl-' + firstorder;
				  var firsturl = values[urlkey];
				  if(firsturl==""){//如果firsturl为空,则先存起来
				  	firstNoValueMap.put(name+"-"+firstorder,nameValue);
				  }
				  menuMap.put('menuname', nameValue);
				  menuMap.put('menuurl', firsturl);
				  menuMap.put('firstorder', firstorder);
				  allMenuMap.put(key, menuMap);
			  } else if ("secondname" == name) {
				  var nameValue = values[key];
				  var firstorder = temps[1];
				  var secondorder = temps[2];
				  var firstname = "firstname-" + firstorder;
				  var funcmenukey = "secondfuncmenu-" + firstorder + "-" + secondorder;
				  var funcmenu = values[funcmenukey];
				  var funcurlkey = "secondfuncurl-" + firstorder + "-" + secondorder;
				  var funcurl = values[funcurlkey];
				  if(funcurl==""){//二级菜单不为空
				  	Ext.Msg.alert(weixin.setting.promptmessage,weixin.setting.pleaseSelectFunction+nameValue+weixin.setting.functionsOrInput);
				  	return;
				  }
				  if(firstNoValueMap.get(firstname)){//如果有值代表链接为空
    				  firstNoValueMap.put(firstname,"hasSecond");
				  }
				  var functionMap = new HashMap();
				  functionMap.put("functionname", nameValue);
				  functionMap.put("functionmenu", funcmenu);
				  functionMap.put("functionurl", funcurl);
				  functionMap.put("secondorder", secondorder);
				  var menuMap = allMenuMap.get(firstname);
				  if (!menuMap) {
					  menuMap = new HashMap();
				  }
				  var functions = menuMap.get("functions");
				  if (!functions) {
					  functions = new Array();// 指定5个
				  }
				  functions.push(functionMap);
				  menuMap.put("functions", functions);
				  allMenuMap.put(firstname, menuMap);
			  }
		  }
          for(var key in firstNoValueMap){
               if(key=="put" || key =="get"){//对象默认有的这俩跳过
                 continue;
               }
               /**
                * 首发集团需求 不配置二级菜单也可以发布、保存
                */
//               var urlValue = firstNoValueMap.get(key);
//               if(urlValue!='hasSecond'){
//               	    Ext.Msg.alert(weixin.setting.promptmessage,weixin.setting.pleaseInput+urlValue+weixin.setting.urlOrAddSecondFunction);
//                    return;
//               }
          }
		  var menuArray = new Array();
		  for (var key in allMenuMap) {
			  if (allMenuMap[key]) {
				  menuArray.push(allMenuMap[key]);
			  }
		  }
		  var vo = new HashMap();
		  var map = new HashMap();
		  map.put("servertype", "recruit");
		  map.put("params", menuArray);
		  vo.put('menuData', map);
		  vo.put("serverid", me.serverid);
		  vo.put("flag", flag);
		  return vo;
	},
	/**
	 * 一级菜单处理
	 * @param menus 一级菜单数据
	 */
	dealMenu : function(menus) {
		var me = this;
		var length = menus.length;
		if(!menus)
			return;
		menus.forEach(function(param, index) {
			me.addMenu(param, param.order);
		});
	},
	/**
	 * 创建一级菜单
	 * 
	 * @param {}
	 *         param 一级菜单数据
	 * @param {}
	 *         firstorder 一级菜单顺序标识
	 */
	addMenu : function(param, firstorder) {
		var me = this;
		var firstorder = param.order;
		var id = "first-" + firstorder;
		var menuName = "firstname-" + firstorder;
		var menutypeName = "firsttype-" + firstorder;
		var menu = Ext.create("Ext.form.Panel", {
			  firstmenutype : "recruit",// 一级菜单类型
			  id : id,
			  width : '100%',
			  order : "",
			  border:0,
			  style:'border-bottom:1px solid #d5d5d5;padding-top:8px;padding-bottom:8px;',
			  layout : {
				  type : "vbox"
			  },
			  items : [{
				  // 一级菜单名和功能
				  xtype : "panel",
				  border : 0,
				  itemId : "1stfunction" + firstorder,
				  width : "100%",
				  layout : {
					  type : 'hbox',
					  pack:'center',
				  	  align:'center'
				  },
				  style:'margin-top:8px;',
				  items : [{
					  xtype : 'panel',
					  itemId : "panel-" + firstorder,
					  border : 0,
					  width : 90,
					  style:'margin-left:40px;cursor:pointer;color:#1B4A98;',
					  html : '<font style="font-size:16px;margin-left:2px;line-height:26px;color:#1B4A98;"/>'+param.menuname+'</font>',
					  listeners : {
						  click : {
							  element : 'el',
							  fn : function() {
							  	  var img =menu.query("#image-" + firstorder)[0];
							 	  img.setHidden(true);
								  var text = menu.query('#text-' + firstorder)[0];
								  var textPanel = menu.query("#panel-" + firstorder)[0];
								  if (textPanel && !textPanel.isHidden()) {
									  textPanel.setHidden(true);
									  text.setHidden(false);
									  text.focus();
								  }
							  }
						  },
						  mouseover:{
						  	 element : 'el',
							 fn : function() {
							 	var img = menu.query("#image-" + firstorder)[0];
							 	img.setStyle({display:'block'});
							 }
						  },
						  mouseout:{
						  	 element : 'el',
							 fn : function() {
							 	var img =menu.query("#image-" + firstorder)[0];
							 	img.setStyle({display:'none'});
							 }
						  }
					  }
				  }, {
					  xtype : 'textfield',
					  itemId : "text-" + firstorder,
					  name : menuName,
					  style:'margin-left:38px;',
					  fieldStyle:'font-size:16px;',
					  hidden : true,
					  value : param.menuname,
					  listeners:{
						  blur:function(t){
							  t.setHidden(true);
							  var charnum = 0;//字节数
				        	  var varlength = 0;//字符长度
				        	  var realValue =this.value;
			        		  for (var i = 0; i < this.value.length; i++) {
			        		      if (this.value.charCodeAt(i)>255) {//如果是汉字
			        		          charnum = charnum+2;//一个汉字占两个字节
			        		          varlength = varlength+1;
			        		       }
			        		       else {
			        		          charnum =charnum+1;//字母数字等占一个字节
			        		          varlength = varlength+1;
			        		        }
			        		        if(charnum>8){
			        		        	Ext.MessageBox.alert(weixin.setting.tip,weixin.setting.maxWord8);//提示信息   请输入8个以内的字节（一个汉字占两个字节）
			        		        	realValue = this.value.substring(0,varlength-1);
				    					this.setValue(realValue);
			        		        }
			        		    }
			        		  if(!realValue){
			        			  realValue = weixin.setting.firstMenuName
			        		  }
							  var textPanel = menu.query("#panel-" + firstorder)[0];
							  textPanel.setHtml('<font style="font-size:16px;margin-left:2px;line-height:26px;color:#1B4A98;"/>'+realValue+'</font>');
							  textPanel.setHidden(false);
						  }
					  }
				  }, {
					  xtype:'hiddenfield',
					  value:param.menutype,
					  name:'firsttype-'+firstorder,
					  itemId:'text-hidden'+firstorder
				  },{
					  xtype : "image",
					  itemId : "image-" + firstorder,
					  src : "/system/sms/weixin/images/edit.png",
					  style:'margin-left:-10px;cursor:pointer;display:none;',
					  //hidden:true,
					  width : 20,
					  height : 20,
					  listeners : {
						  click : {
							  element : 'el',
							  fn : function() {
							  	  var img =menu.query("#image-" + firstorder)[0];
							 	  img.setHidden(true);
								  var text = menu.query('#text-' + firstorder)[0];
								  var textPanel = menu.query("#panel-" + firstorder)[0];
								  if (textPanel && !textPanel.isHidden()) {
									  textPanel.setHidden(true);
									  text.setHidden(false);
									  text.focus();
								  }
							  }
						  },
						  mouseover:{
						  	 element : 'el',
							 fn : function() {
							 	var img = menu.query("#image-" + firstorder)[0];
							 	img.setStyle({display:'block'});
							 }
						  },
						  mouseout:{
						  	 element : 'el',
							 fn : function() {
							 	var img =menu.query("#image-" + firstorder)[0];
							 	img.setStyle({display:'none'});
							 }
						  }
					  }
				  },{
					  xtype:'component',
					  flex:1
				  },{
				      xtype:'hidden',
				      value:param.menuurl,
				      itemId:'url-'+firstorder,
					  name:'firsturl-'+firstorder
				  },{
				  	  xtype:'panel',
				  	  border:0,
				  	  height:32,
				  	  hidden:!param.menuurl? false:true,
				  	  itemId:'textlink-'+firstorder,
				  	  style:'margin-right:80px;',
				  	  html:'<div style="color:#1B4A98;font-size:14px;cursor:pointer;margin-top:6px;">'+weixin.setting.urlDescText+'</div>',
				  	  listeners:{
				  	  	  click:{
				  	  	  	  element:'el',
				  	  	  	  fn:function(){
				  	  	  	  	  var menuurl = menu.query('#url-'+firstorder)[0];
				  	  	  	  	  var textPanel = menu.query('#textlink-'+firstorder)[0];
				  	  	  	  	  var imgPanel = menu.query('#imglink-'+firstorder)[0];
				  	  	  	  	  me.urlConfig(menuurl,textPanel,imgPanel,'first');
				  	  	  	  }
				  	  	  }
				  	  }
				  },{
				  	xtype:'image',
				  	src:'/system/sms/weixin/images/link.png',
				  	width:32,
				  	height:32,
				  	style:'margin-right:104px;cursor:pointer',
				  	itemId:'imglink-'+firstorder,
				  	hidden:param.menuurl? false:true,
				  	listeners:{
				  		click:{
				  			element:'el',
				  			fn:function(){
				  				var menuurl = menu.query('#url-'+firstorder)[0];
				  	  	  	  	var textPanel = menu.query('#textlink-'+firstorder)[0];
				  	  	  	  	var imgPanel = menu.query('#imglink-'+firstorder)[0];
				  	  	  	  	me.urlConfig(menuurl,textPanel,imgPanel,'first');
				  			}
				  		}
				  	}
				  }]
			  }]
		  });
		var serverContainer = Ext.getCmp('serverContainer');
		serverContainer.insert(serverContainer.items.keys.length,menu);
		me.secondMenu(menu, param.functions, firstorder);
	},
	/**
	 *  配置链接地址
	 *  urlPanel  用来保存链接地址
	 *  textPanel 文字链接
	 *  imgPanel  图片链接
	 *  level 层级  first 一级菜单  second 二级菜单
	 *  funcMenuPanel 子菜单功能号  （子菜单功能使用）
	 *  funcMenuPanel 服务地址 （子菜单功能使用）
	 */
	urlConfig:function(urlPanel,textPanel,imgPanel,level,funcMenuPanel,serviceUrl){
		var me = this;
		var setStore = Ext.create('Ext.data.Store',{
            	fields:['itemid','itemdesc'],
            	data:me.funcList
        });
        var disabled = false;
        
        if(level !='first' &&( funcMenuPanel && funcMenuPanel.getValue() != '')){
        	disabled = true;
        }
        var appid = Ext.getCmp('APPID').getValue();
        var url = 'https://open.weixin.qq.com/connect/oauth2/authorize?appid=APPID&redirect_uri=REDIRECT_URI&response_type=code&scope=snsapi_base&state=STATE#wechat_redirect';
        url = url.replace('APPID',appid);
        var funcUrl = serviceUrl+'/recruitservice/oauthservlet';
        url = url.replace('REDIRECT_URI',encodeURIComponent(funcUrl));
        var showUrl = disabled?url.replace('STATE',encodeURIComponent(urlPanel.getValue().split('?')[1])):urlPanel.getValue();
        var urlPanelValue='';
		var win = Ext.create('Ext.window.Window',{
			title:weixin.setting.urlConfigTitle,
			width:500,
			modal:true,
			layout:{
				type:'vbox'
			},
			items:[{
				xtype:'combobox',
				itemId:'func',
				style:'margin-top:10px;font-size:14px;',
				labelWidth:54,
				fieldLabel:weixin.setting.secondMenuFunction,
				hidden:level=='first'? true:false,
				store:setStore,
				displayField: 'itemdesc',
    			valueField: 'itemid',
    			value:level == 'first'? '':funcMenuPanel.getValue(),
    			emptyText:weixin.setting.pleaseSelect,
    			listeners:{
    				change:function(t,value){
    					var win_url = win.query('#url')[0];
    					if(!value || value == '' ){
    						win_url.setValue('');
    						win_url.setReadOnly(false);
    						return;
    					}
    					win_url.setReadOnly(true);
    					
		    			var type = '';
		    			if(value == 'myResume' || value == 'applyPost' || value == 'collectPost'){
		    			    type = 'selfCode';
		    			}else{
		    				type = 'recruit'
		    			}
		    			var state = 'type='+type+'&value='+value;
		    			var replaceBeforeUrl = url;
		    			url = url.replace('STATE',encodeURIComponent(state));
		    			urlPanelValue= serviceUrl + '/recruitservice/module/recruitplatform/index.jsp?type='+type+'&value='+value;
						win_url.setValue(url);
						url = replaceBeforeUrl;
    				}
    			}
			},{
				xtype:'textareafield',
				width:490,
				height:60,
				itemId:'url',
				readOnly:disabled,
				emptyText:level=='first'? weixin.setting.inputAccessAddress:weixin.setting.inputAndSelectAddress,
				style:'margin-top:10px;margin-bottom:4px;font-size:14px;',
				value: showUrl //disabled? serviceUrl+urlPanel.getValue() :urlPanel.getValue()  //level == 'first'? urlPanel.getValue():(!funcMenuPanel || funcMenuPanel.getValue()=='')?urlPanel.getValue():serviceUrl+urlPanel.getValue()
			}],
			buttonAlign : 'center',
			buttons : [{
				xtype:'button',
				width:60,
				height:30,
				html:'<font style="font-size:14px">'+weixin.setting.ok+'</font>',
				listeners:{
					click:function(){
						var y = me.getScrollable().getPosition().y;
						var value = win.query('#url')[0].getValue();
						var funcValue = win.query('#func')[0].getValue();
						if(level != 'first'){
							funcMenuPanel.setValue(funcValue);
						}
						//解决自定义链接保存不上问题
						if(funcValue){
							urlPanel.setValue(urlPanelValue);
						}else{
							urlPanel.setValue(value);
						}
						if(!value || value.length==''){
							textPanel.setHidden(false);
							imgPanel.setHidden(true);							
						}
						else{
							textPanel.setHidden(true);
							imgPanel.setHidden(false);
						}
						me.getScrollable().scrollTo(0,y);
						win.close();
					}
				}
			},{
				xtype:'component',
				width:10
			},{
				xtype:'button',
				width:60,
				height:30,
				html:'<font style="font-size:14px">'+weixin.setting.cancel+'</font>',
				listeners:{
					click:function(){
						win.close();
					}
				}
			}]
		});
		
		win.show();
	},
	/**
	 * 二级菜单配置
	 * 
	 * @param {}
	 *         menu 一级菜单对象
	 * @param {}
	 *         functions 同一一级菜单下所有二级菜单的数据
	 * @param {}
	 *         firstorder 一级菜单顺序标识
	 */
	secondMenu : function(menu, functions, firstorder) {
		var me = this;
		var itemId = menu.id + "-second"
		var secondMenu = Ext.create('Ext.panel.Panel', {
			  border : 0,
			  itemId : itemId,
			  width : '100%',
			  layout : 'vbox',
			  style:'margin-left:40px',
			  items : []
		  });
		menu.add(secondMenu);
		me.dealSencondAddMenu(secondMenu, firstorder);
		functions.forEach(function(functions, i) {
			me.addSecondMenu(secondMenu, firstorder, functions, functions.order);
		});
		var length = secondMenu.items.keys.length;
		if (length > 5) {
			secondMenu.query('#addsecondmenu')[0].setHidden(true);
		}
	},
	/**
	 * 处理二级菜单下的新增按钮
	 * 
	 * @param {}
	 *         secondMenu 二级菜单对象
	 * @param {}
	 *         firstorder 一级菜单顺序标识
	 */
	dealSencondAddMenu : function(secondMenu, firstorder) {
		var me = this;
		var sencondAddMenuPanel = Ext.create('Ext.panel.Panel', {
			  border : 0,
			  itemId : "addsecondmenu",
			  layout : {
				  type : "hbox",
				  pack : "center"
			  },
			  style:'margin-top:8px;margin-bottom:8px;',
			  items : [{
				  xtype : "image",
				  height : 32,
				  width : 32,
				  src : "/system/sms/weixin/images/add.png",
				  style:'margin-left:40px;margin-right:4px;cursor:pointer;',
				  listeners : {
					  click : {
						  element : "el",
						  fn : function() {// 新建二级菜单
						  	  var y = me.getScrollable().getPosition().y;
							  var list = [];
							  list.push({
								    functionname : weixin.setting.secondMenuName,//二级菜单名称
								    order : 1
							    });
							  var newsecondorder = me.maxFucOrder + 1;
							  me.maxFucOrder = newsecondorder;
							  list[0].order = me.maxFucOrder;
							  me.addSecondMenu(secondMenu, firstorder, list[0], newsecondorder);
							  // 删了再加id重复
							  me.limitSecondMenu(secondMenu);
							  me.getScrollable().scrollTo(0,y);
						  }
					  }
				  }
			  }, {
				  xtype : "component",
				  style:'margin-top:5px',
				  html : '<font style="font-size:16px;">'+weixin.setting.addsecondMenu+'</font>'//添加二级菜单
			  }]
		  });
		secondMenu.add(sencondAddMenuPanel);
	},

	/**
	 * 添加二级菜单
	 * 
	 * @param {}
	 *         secondMenu 二级菜单对象
	 * @param {}
	 *         firstorder 一级菜单顺序标识
	 * @param {}
	 *         functions 当前二级菜单数据
	 * @param {}
	 *         secondorder 二级菜单顺序标识
	 */
	addSecondMenu : function(secondMenu, firstorder, functions, secondorder) {
		var me = this;
		if (functions.functionname == undefined) {
			return;
		}
		if (!secondorder) {// 没有order,取最大order+1
			secondorder = me.maxFucOrder + 1;
			me.maxFucOrder = secondorder;
			functions.order = me.maxFucOrder;
		}
		var menuPanelItemid = secondMenu.id + "-" + secondorder
		var secondmenu = Ext.create("Ext.form.Panel", {
			  border : 0,
			  itemId : menuPanelItemid,
			  layout : {
				  type : "hbox",
				  pack:'center',
				  align:'center'
			  },
			  width : "100%",
			  style:'margin-top:8px;',
			  items : [{
				  // 二级菜单名
				  xtype : "panel",
				  border:0,
				  width:154,
				  style:'margin-left:40px;cursor:pointer;',
				  itemId : "secondpanel-" + secondorder,
				  html : '<font style="font-size:16px;margin-left:2px;line-height:26px;color:#1B4A98;">' + functions.functionname +'</font>',
				  listeners : {
					  click : {
						  element : 'el',
						  fn : function() {
							  var y = me.getScrollable().getPosition().y;
						  	  var img = secondmenu.query("#image-" + secondorder)[0];
							  img.setHidden(true);
							  var text = secondmenu.query('#secondtext-' + secondorder)[0];
							  if(text.getValue() == weixin.setting.secondMenuName){
							  	text.setValue('');
							  }
							  var textPanel = secondmenu.query("#secondpanel-" + secondorder)[0];
							  if (textPanel && !textPanel.isHidden()) {
								  textPanel.setHidden(true);
								  text.setHidden(false);
								  text.focus();
							  }
							  me.getScrollable().scrollTo(0,y);
						  }
					  },
					  mouseover:{
						  element : 'el',
						  fn : function() {
							  var img = secondmenu.query("#image-" + secondorder)[0];
							  img.setStyle({display:'block'});
						  }
					  },
					  mouseout:{
						  element : 'el',
						  fn : function() {
							   var img = secondmenu.query("#image-" + secondorder)[0];
							   img.setStyle({display:'none'});
						  }
					  }
				  }
			  }, {
				  xtype : 'textfield',
				  name : "secondname-" + firstorder + "-" + secondorder,
				  itemId : "secondtext-" + secondorder,
				  style:'margin-left:38px;',
				  fieldStyle:'font-size:16px;',
				  width : 154,
				  hidden : true,
				  value : functions.functionname,
				  listeners:{
					  blur:function(t){
						  var y = me.getScrollable().getPosition().y;
						  t.setHidden(true);
						  var charnum = 0;//字节数
			        	  var varlength = 0;//字符长度
			        	  var realValue =this.value;
		        		  for (var i = 0; i < this.value.length; i++) {
		        		      if (this.value.charCodeAt(i)>255) {//如果是汉字
		        		          charnum = charnum+2;//一个汉字占两个字节
		        		          varlength = varlength+1;
		        		       }
		        		       else {
		        		          charnum =charnum+1;//字母数字等占一个字节
		        		          varlength = varlength+1;
		        		        }
		        		        if(charnum>16){
		        		        	Ext.MessageBox.alert(weixin.setting.tip,weixin.setting.maxWord16);//提示信息   请输入16个字节以内的字节（一个汉字占两个字节）
		        		        	realValue = this.value.substring(0,varlength-1);
			    					this.setValue(realValue);
		        		        }
		        		    }
		        		  //如果没有值的情况下 赋值二级菜单名称
		        		  if(!realValue){
		        			  realValue = weixin.setting.secondMenuName;
		        		  }
						  var textPanel = secondmenu.query("#secondpanel-" + secondorder)[0];
						  textPanel.setHtml('<font style="font-size:16px;margin-left:2px;line-height:26px;color:#1B4A98;">' +realValue+ '</font>');
						  textPanel.setHidden(false);
						  me.getScrollable().scrollTo(0,y);
					  }
				  }
			  }, {
				  xtype : "image",// 编辑二级菜单
				  src : "/system/sms/weixin/images/edit.png",
				  itemId:"image-" + secondorder,
				  //hidden:true,
				  style:'cursor:pointer;display:none;',
				  width : 20,
				  height : 20,
				  listeners : {
					  click : {
						  element : 'el',
						  fn : function() {
							  var img = secondmenu.query("#image-" + secondorder)[0];
							  img.setHidden(true);
							  var text = secondmenu.query('#secondtext-' + secondorder)[0];
							  var textPanel = secondmenu.query("#secondpanel-" + secondorder)[0];
							  if(text.getValue() == weixin.setting.secondMenuName){
							  	text.setValue('');
							  }
							  if (textPanel && !textPanel.isHidden()) {
								  textPanel.setHidden(true);
								  text.setHidden(false);
								  text.focus();
							  }
						  }
					  },
					  mouseover:{
					  	  element　: 'el',
					  	  fn : function(){
					  	  	  var img = secondmenu.query("#image-" + secondorder)[0];
							  img.setStyle({display:'block'});
					  	  }
					  },
					  mouseout:{
						  element : 'el',
						  fn : function() {
							   var img = secondmenu.query("#image-" + secondorder)[0];
							   img.setStyle({display:'none'});
						  }
					  }
				  }
			  },{
				  xtype:'component',
				  width:200
			  },{
				  xtype:'component',
				  flex:1
			  },{
			  	xtype:'hidden',
			  	value:functions.functionmenu,
			  	itemId:'funcmenu-'+ firstorder + "-" + secondorder,
			  	name:'secondfuncmenu-'+ firstorder + "-" + secondorder
			  },{
				  xtype:'hidden',
				  value:functions.functionurl,
				  itemId:'funcurl-'+ firstorder + "-" + secondorder,
				  name:'secondfuncurl-'+ firstorder + "-" + secondorder
			  },{
				  xtype:'panel',
				  border:0,
				  height:32,
				  itemId:'functextlink-'+ firstorder + "-" + secondorder,
				  hidden:!functions.functionurl? false:true,
				  style:'margin-right:28px;',
				  html:'<div style="color:#1B4A98;font-size:14px;cursor:pointer;margin-top:6px;">'+weixin.setting.urlDescText+'</div>',
				  listeners:{
				  	  click:{
				  	  	  element:'el',
				  	  	  fn:function(){
				  	  	  	  var secondmenuurl = secondmenu.query('#funcurl-'+ firstorder + "-" + secondorder)[0];
				  	  	  	  var textPanel = secondmenu.query('#functextlink-'+ firstorder + "-" + secondorder)[0];
				  	  	  	  var imgPanel = secondmenu.query('#funcimglink-'+ firstorder + "-" + secondorder)[0];
				  	  	  	  var funcmenu = secondmenu.query('#funcmenu-'+ firstorder + "-" + secondorder)[0];
				  	  	  	  var serviceUrl = Ext.getCmp('app_url').getValue();
				  	  	  	  me.urlConfig(secondmenuurl,textPanel,imgPanel,'second',funcmenu,serviceUrl);
				  	  	  }
				  	  }
				  }
			  },{
				  xtype:'image',
				  src:'/system/sms/weixin/images/link.png',
				  width:32,
				  height:32,
				  style:'margin-right:52px;cursor:pointer',
				  itemId:'funcimglink-'+ firstorder + "-" + secondorder,
				  hidden:functions.functionurl? false:true,
				  listeners:{
				  	click:{
				  		element:'el',
				  		fn:function(){
				  			  var secondmenuurl = secondmenu.query('#funcurl-'+ firstorder + "-" + secondorder)[0];
				  	  	  	  var textPanel = secondmenu.query('#functextlink-'+ firstorder + "-" + secondorder)[0];
				  	  	  	  var imgPanel = secondmenu.query('#funcimglink-'+ firstorder + "-" + secondorder)[0];
				  	  	  	  var funcmenu = secondmenu.query('#funcmenu-'+ firstorder + "-" + secondorder)[0];
				  	  	  	  var serviceUrl = Ext.getCmp('app_url').getValue();
				  	  	  	  me.urlConfig(secondmenuurl,textPanel,imgPanel,'second',funcmenu,serviceUrl);
				  		}
				  	}
				  }
			  },{
				  // 删除二级菜单
				  xtype : "image",
				  src : "/system/sms/weixin/images/del.png",
				  width : 32,
				  height : 32,
				  style:'margin-right:20px;cursor:pointer;',
				  listeners : {
					  click : {// 删除二级菜单
						  element : 'el',
						  fn : function() {
						  	  var text= secondmenu.query('#'+"secondtext-" + secondorder)[0];//实时获取二级菜单名称    wangb 20190327
							  Ext.Msg.confirm(weixin.setting.tip,weixin.setting.delBefore+text.getValue()+weixin.setting.delAfter,function(opt){
								  if(opt=='yes'){
									  secondmenu.destroy();
							  		  me.limitSecondMenu(secondMenu);
							   	  }
							  });
							 
						  }
					  }
				  }
			  }]
		  });
		secondMenu.insert(secondMenu.items.length-1, secondmenu);
	},
	/**
	 * 添加菜单功能按钮
	 * 
	 * @param {}
	 *         firstorder
	 * @param {}
	 *         secondorder
	 * @param {}
	 *         secondmenu
	 * @param {}
	 *         functiontype
	 */
	dealAddSecondFunction : function(firstorder, secondorder, secondmenu,
	                                 functiontype) {
		var me = this;
		var length = secondmenu.items.keys.length;
		if (length > 5) {// 限制二级菜单功能个数
		}
	},
	/**
	 * 处理二级菜单功能
	 * 
	 * @param {}
	 *         firstorder 一级菜单顺序标识
	 * @param {}
	 *         secondorder 二级菜单顺序标识
	 * @param {}
	 *         secondmenu 二级菜单对象
	 * @param {}
	 *         functions 当前二级菜单数据
	 */
	dealSecondFunction : function(firstorder, secondorder, secondmenu, functions) {
		var me = this;
		var store = undefined;// 二级菜单数据
		var firstMenuType = Ext.getCmp("first-" + firstorder).query('#text-hidden'+firstorder)[0].getValue();
		if (firstMenuType == "recruit") {// 我要应聘
		} else if (firstMenuType == "selfCode") {// 个人中心
			store = Ext.create('Ext.data.Store', {
				  fields : ['type', 'val'],
				  data : [{
					  type : weixin.setting.personalResume,//个人简历
					  val : "myResume"
				  }, {
					  type : weixin.setting.applyForPosition,//应聘职位
					  val : "applyPost"
				  }, {
					  type : weixin.setting.collectionPosition,//收藏职位
					  val : "collectPost"
				  }, {
					  type : weixin.setting.modifyApplyForIdentity,//修改应聘身份
					  val : "myidentitymodify"
				  }]
			  });
		}else if(firstMenuType == "other" ){
			var secondFunctionCmp = Ext.create('Ext.form.field.Text',{
				width:200,
				emptyText:weixin.setting.inputAccessAddress,//请输入访问地址...
				style:'margin-left:4px',
				itemId:'test'+secondorder,
				name : "secondtype-" + firstorder + "-" + secondorder
			});
			if(functions.functiontype)
				secondFunctionCmp.setValue(functions.functiontype);
			secondmenu.query('#secondfuncname_'+secondorder)[0].setHtml('<font style="font-size:16px;margin-left:4px;">'+weixin.setting.secondMenuAddress+'</font>');//二级菜单地址
			// 获取二级菜单的menuPanel,将创建的组件加入进去
			secondmenu.insert(secondmenu.items.keys.length - 2, secondFunctionCmp);
			return;
		}
		
		if(!functions.functiontype){
			me.addSecondFunction(firstorder, secondorder, secondmenu, undefined,store,firstMenuType);
			return;
		}
		if(firstMenuType =='recruit'){
			me.addSecondFunction(firstorder, secondorder, secondmenu, functions.functiontype,store,firstMenuType);
		}else{
			var funclist = functions.functiontype.split(",");
			funclist.forEach(function(functiontype, index) {
				me.addSecondFunction(firstorder, secondorder, secondmenu, functiontype,store,firstMenuType);
			});
		}
		
	},
	/**
	 * 添加二级菜单功能
	 * 
	 * @param {}
	 *         firstorder 一级菜单顺序标识
	 * @param {}
	 *         secondorder 二级菜单顺序标识
	 * @param {}
	 *         secondmenu 二级菜单对象
	 * @param {}
	 *         functions 二级菜单功能
	 * @param {}
	 * 		   firstmenutype 一级菜单功能
	 * @param {}
	 *         addfuncFlag 我要应聘recruit 二级菜单添加多个功能标识
	 */
	addSecondFunction : function(firstorder, secondorder, secondmenu, functiontype,store,firstmenutype,addfuncFlag) {
		// 根据一级菜单功能决定二级菜单功能
		var me = this;
		var secondfunction = Ext.create("Ext.form.ComboBox", {
			  xtype : 'combobox',
			  editable : false,
			  style:'margin-left:4px',
			  name : "secondtype-" + firstorder + "-" + secondorder,
			  labelAlign : 'right',
			  width : 136,
			  height : 20,
			  emptyText:weixin.setting.pleaseSelect,//请选择
			  queryMode : 'local',
			  valueField : 'val',
			  displayField : 'type',
			  store : store
		  })
		if(functiontype){
			secondfunction.setValue(functiontype);
		}
		////////////////////////////
		var id = "secondtype-" + firstorder + "-" + secondorder;
		var funvalue ='';
		var funtype = '';
		if(firstmenutype =='recruit'){
			if(functiontype){
				var funArray = functiontype.split("`");
				if(funArray.length>0){
					funvalue = funArray[1];
					funtype = funArray[0];
				}
			}
			
			secondfunction = Ext.create('Ext.container.Container',{
				margin:'0 0 0 5',
				layout:{
					type:'hbox',
					pack:'center',
					align:'center'
				},
				items:[
					{
						xtype:'textfield',
						name:"secondtype-" + firstorder + "-" + secondorder+"-viewvalue",
						value:funtype?funtype:'',
					    width:130
					},{
						xtype:'hiddenfield',
						name:"secondtype-" + firstorder + "-" + secondorder,
						value:funvalue?funvalue:''
					},{
						xtype:'component',
						width:20,
						height:20,
						margin:'0 0 0 3',
						html:'<img id= '+id+' src="/images/code.gif" style=\"vertical-align:middle;\" align=\"absmiddle\" plugin="codeselector" codesetid="35" inputname='+"secondtype-" + firstorder + "-" + secondorder+"-viewvalue"+' valuename='+"secondtype-" + firstorder + "-" + secondorder+'   multiple="true" onlyselectcodeset="false"   />',
						listeners:{
							element:'el',
							click:function(){
								me.codeClick(["secondtype-" + firstorder + "-" + secondorder]);
							}
						}
					}
				]
			});
		}
		///////////////////////////
		if(firstmenutype == 'selfCode'){
			// 获取二级菜单的menuPanel,将创建的组件加入进去
			secondmenu.insert(secondmenu.items.keys.length - 2, secondfunction);
		}else if(addfuncFlag && addfuncFlag == 'add'){
			//添加多个2级功能
			secondmenu.insert(secondmenu.items.length - 3, secondfunction);
		}else{
			// 获取二级菜单的menuPanel,将创建的组件加入进去
			secondmenu.insert(secondmenu.items.length - 2, secondfunction);
		}
		
	},
	/**
	*服务号设置参数配置界面
	*/
	serviceParamPanel:function(){
		var me = this;
		var serviceForm =  Ext.create("Ext.form.Panel",{
			width:500,
			border:false,
			id:'serviceParam',
			style:'margin-left:40px;',
			defaults:{
				xtype:'textfield',
				margin:'20 0',
				width:500,
				cls:'field-input'//更改边框样式
			},
			items : [{
				fieldLabel : weixin.setting.appAPPID,//"应用APPID"
				value : me.servicelist[0].APPID,
				beforeLabelTextTpl:"<font color='red'> * </font>",
				labelAlign : 'left',
				style:'margin-top:20px',
				width : '80%',
				name : 'APPID',
				id : 'APPID',
				allowBlank : false
			}, {
				fieldLabel : weixin.setting.appAppSecrit,//"应用AppSecrit"
				value : me.servicelist[0].AppSecret,
				beforeLabelTextTpl:"<font color='red'> * </font>",
				labelAlign : 'left',
				style:'margin-top:20px',
				width : '80%',
				name : 'AppSecret',
				id : 'AppSecret',
				allowBlank : false
			}, {
				fieldLabel : weixin.setting.appServiceAddress,//"应用服务地址"
				value : me.servicelist[0].url,
				beforeLabelTextTpl:"<font color='red'> * </font>",
				labelAlign : 'left',
				style:'margin-top:20px',
				width : '80%',
				id:'app_url',
				name : 'url',
				allowBlank : false
			}]	
		});
		return serviceForm;
	},
	/**
	 * 新建服务号窗口
	 * 
	 * @param {}
	 *         servicemessage 服务号配置信息
	 * @param {}
	 *         type
	 */
	addServerwin : function(servicemessage, type) {
		var me = this;
		var win = Ext.create("Ext.window.Window", {
			width : 500,
			height : 250,
			id : "addwin",
			title : weixin.setting.serviceNumberConfig,//服务号配置
			optiontype : type,
			modal : true,
			constrain : true, // 限制窗口不超出浏览器边界
			constrainHeader : true, // 不允许该窗体标题超出浏览器边界
			items : [{
				xtype : "form",
				defaultType :'textfield',
				layout:{
					type:'vbox',
					pack:'center',
					align:'center'
				},
				border:0,
				items : [{
					fieldLabel : weixin.setting.appAPPID,//"应用APPID"
					value : servicemessage.APPID,
					beforeLabelTextTpl:"<font color='red'> * </font>",
					labelAlign : 'left',
					style:'margin-top:20px',
					width : '80%',
					height : 25,
					name : 'APPID',
					allowBlank : false
				}, {
					fieldLabel : weixin.setting.appAppSecrit,//"应用AppSecrit"
					value : servicemessage.AppSecret,
					beforeLabelTextTpl:"<font color='red'> * </font>",
					labelAlign : 'left',
					style:'margin-top:20px',
					width : '80%',
					height : 25,
					name : 'AppSecret',
					allowBlank : false
				}, {
					fieldLabel : weixin.setting.appServiceAddress,//"应用服务地址"
					value : servicemessage.url,
					beforeLabelTextTpl:"<font color='red'> * </font>",
					labelAlign : 'left',
					style:'margin-top:20px',
					width : '80%',
					height : 25,
					name : 'url',
					allowBlank : false
				}],
				buttonAlign : 'center',
				buttons : [{
					margin:'30 20 0 0',
					text : weixin.setting.ok,//确定
					formBind:true,//验证通过，则确定亮起
					handler : function() {
						var form = win.child("form").getValues();
						var vo = new HashMap();
						var map = new HashMap();
						var name ='微招聘';
						map.put("wxsetid", "server");
						map.put("name",name);
						map.put("serverid", servicemessage.serverid);
						map.put("APPID", form["APPID"]);
						map.put("AppSecret", form["AppSecret"]);
						map.put("url", form["url"]);
						vo.put('serverParam', map);
						vo.put('service',type);
						Rpc({
							  functionId : 'SYS0000001003',
							  async : false,
							  success : function(data) {
								  var result = Ext.decode(data.responseText);
								  Ext.getCmp("addwin").close();
								  
								  if (type == "add") {// 新建操作新建panel
									  me.servicelist.push({// 新建后前台菜单容器添加进新添加的数据
										  serverid : result.serverid.serverid,
										  servername :name
									  }
									  );
									  me.addServer(me.servicelist[me.servicelist.length - 1].serverid, me,
									    me.servicelist[me.servicelist.length - 1]
									  );
									  if(!me.serverid){
										  me.serverid = result.serverid.serverid;
										  me.funsConfig();
									  }
								  } else {// 编辑操作更新panel
									  Ext.getCmp("servername" + servicemessage.serverid)
									    .setHtml(name);
								  }

							  },
							  scope : this
						  }, vo);
					}
				}, {
					text : weixin.setting.cancel,//"取消"
					handler : function() {
						win.close();
					}
				}]
				
			}]
			
		}
		).show();
	},

	/**
	 * 添加服务号配置panel组件
	 * 
	 * @param {}
	 *         i serverid
	 * @param {}
	 *         me 全局this
	 * @param {}
	 *         servicelist 组件的数据容器
	 */
	addServer : function(i, me, servicelist) {
		var servicecomponent = Ext.create("Ext.panel.Panel", {
			  width : 60,
			  border : 0,
			  serviceid : servicelist.serverid,
			  id : 'service' + i,
			  style:'position:relative;margin-left:10px;',
			  listeners : {// 点击服务号触发编辑服务号页面
				  click : {
					  element : "el",
					  fn : function() {
						  me.serverid = servicelist.serverid;
						  var vo = new HashMap();
						  var map = new HashMap();
						  map.put("serverid", servicelist.serverid);// 待修改
						  vo.put('serverParam', map);
						  Rpc({
							    functionId : 'SYS0000001005',
							    async : false,
							    success : function(data) {
								    var result = Ext.decode(data.responseText);
								    me.serverid = servicelist.serverid;
								    me.servertype = result.menuData.servertype;
								    me.menus = result.menuData.params;
								    if(!me.menus)
								    	me.menus = new Array();
								    if(Ext.getCmp("menuconfig"))
								    	Ext.getCmp("menuconfig").destroy();
								    me.funsConfig();
							    },
							    scope : this
						    }, vo);
					  }
				  }
			  },

			  items : [{
				  xtype : "panel",
				  border : 0,
				  width:60,
				  layout : {
					  type : "vbox"
				  },
				  items : [{
					  xtype : "image",
					  width : 50,
					  height : 50,
					  style:'cursor:pointer',
					  src : "images/fwh.png"
				  }, {
					  xtype : "component",
					  width:50,
					  style:'text-align:center',
					  id : "servername" + i,
					  style:'text-align:center;word-break:break-all',
					  html:servicelist.servername
				  }],
				  listeners : {
					  click:{
						  element : "el",
						  fn:function(){
							  var vo = new HashMap();
							  var map = new HashMap();
							  map.put("serverid", servicelist.serverid);
							  vo.put('serverParam', map);
							  Rpc({
								    functionId : 'SYS0000001002',
								    async : false,
								    success : function(data) {
									    var result = Ext.decode(data.responseText);
									    var servicemessage = result.serverParam.servers;
									    me.addServerwin(servicemessage[0], "update");
								    },
								    scope : this
							    }, vo);
						  }
					  }
				  }
			  }]
		  });
		Ext.getCmp("services").insert(Ext.getCmp("services").items.keys.length - 1,
		  servicecomponent
		);// 插入新创建的服务号配置
	},

	/**
	 * 删除服务号
	 * 
	 * @param {}
	 *         serverid 服务号id
	 */
	deleteServer : function(serverid,servicecomponent) {
		var me = this;
		var type = "delete";
		var vo = new HashMap();
		var map = new HashMap();
		map.put("serverid", serverid);// 待修改
		vo.put('serverParam', map);
		Rpc({
			  functionId : 'SYS0000001004',
			  async : false,
			  success : function() {
				  Ext.Msg.alert(weixin.setting.promptmessage, weixin.setting.deleteMenuSuccess);//删除成功
				  servicecomponent.destroy();
				  if(me.serverid == serverid){
					  me.serverid = undefined;
					  Ext.getCmp("serverContainer").destroy();
				  }
			  },
			  scope : this
		  }, vo);
	},
	/**
	 * 限定最多三个一级菜单
	 */
	limitFirstMenu : function() {
		var me = this;
		if (me.service.items.keys.length > 5) {// 最多存在三个一级菜单
			me.service.query("#addfirstmenu")[0].setHidden(true);
		} else {
			me.service.query("#addfirstmenu")[0].setHidden(false);
		}
	},

	/**
	 * 限定最多五个二级菜单
	 * 
	 * @param {}
	 *         secondmenu 二级菜单对象
	 */
	limitSecondMenu : function(secondmenu) {
		var length = secondmenu.items.keys.length;
		if (length > 5) {
			secondmenu.query("#addsecondmenu")[0].setHidden(true);
		} else {
			secondmenu.query("#addsecondmenu")[0].setHidden(false);
		}
	},
	/**
	 * 限定二级菜单最多五个功能
	 * 
	 * @param {}
	 *         secondmenu 二级菜单对象
	 */
	limitSecondfunction : function(secondmenu) {
		if (secondmenu.items.keys.length > 5) {// 控制二级菜单功能个数不超过五个
			secondmenu.query("#addsecondfunction")[0].setHidden(true);
		} else {
			secondmenu.query("#addsecondfunction")[0].setHidden(false);
		}
    },
	/**
	* 企业号页面显示
	* @param paramInfo app列表信息、参数配置信息
	*/
	createFirmDisplay:function(paramInfo){
		var me = this;
		var params = paramInfo.param;//配置参数
		var items = paramInfo.items;//所有app
		var bodyPanel = Ext.create("Ext.Panel",{//企业号图标以下的大panel
			itemId:'enterprise',
			border:false,
			width:'100%'
			//flex:1,
			//scrollable:'y'
		});
		var customMenuPanel = Ext.create("Ext.Panel",{//参数配置panel
			width:'100%',
			border:false,
			style:'margin-bottom:2px;',
			items:[{
				xype:'panel',//titlePanel
				border:false,
				bodyStyle:'background:#e4e4e4;',
				height:40,
				layout:{
					type:'hbox',
					align:'center'
				},
				items:[{
					xtype:'component',
					margin:'0 0 0 10',
					style:'font-size:18px',
					flex:1,
					html:weixin.setting.configSetting//参数配置
				},{
					xtype:'button',
					itemId:'saveBtn',
					flag:1,//初始化先保存再发布标识   1：已保存
					height:30,
					width:60,
					html:'<div style="font-size:14px;">'+weixin.setting.save+'</div>',//保存
					listeners:{
						click:function(btn){
							var values = interfaceParamsPanel.getValues();//参数配置信息
							var set = Ext.getCmp('func_set');
  	  					    var funcs = set.query('textfield[menuid]');
  	  					      var func_secret = [];
  	  					    for(var i=0;i<funcs.length;i++){
  	  					    	func_secret.push({menuid:funcs[i].menuid,secret:funcs[i].value,desc:funcs[i].fieldLabel});
  	  					    }
							var vo = new HashMap();
							var paramInfo = new HashMap();
							paramInfo.put("corpid",values['corpid']);
							paramInfo.put("corpsecret",values['corpsecret']);
							paramInfo.put("agentid",values['agentid']);
							paramInfo.put("msgsecret",values['msgsecret']);
							paramInfo.put("token",values['token']);
							paramInfo.put("encodingaeskey",values['encodingaeskey']);
							paramInfo.put("w_selfservice_address",values['w_selfservice_address']);
							paramInfo.put("mchid",values['mchid']);
							paramInfo.put("mchkey",values['mchkey']);
							paramInfo.put("url",values['url']);
							paramInfo.put("func_secret",func_secret);
							vo.put("paramInfo",paramInfo)
							Rpc({functionId:'SYS0000001011',async:false,success:function(res){
								var info = Ext.decode(res.responseText);
								if(info.result){
									btn.config.flag = 1;//已保存
									Ext.Msg.alert(weixin.setting.promptmessage,weixin.setting.saveSuccess);//提示：保存成功
								}else{
									Ext.Msg.alert(weixin.setting.promptmessage,weixin.setting.saveFail);//提示：保存失败
								}
							}},vo);
						}
					}
				},{
					xtype:'component',
					width:10
				},{
				  	  xtype:'image',
				  	  src:'/system/sms/weixin/images/down.png',
				  	  itemId:'serviceDown',
				  	  width:32,
				  	  height:32,
				  	  style:'margin-top:4px;margin-right:20px;cursor:pointer;',
				  	  listeners:{
				  	  	  click:{
				  	  	      element:'el',
				  	  	      fn:function(){
				  	  	      	  customMenuPanel.query('#serviceUp')[0].setHidden(false);
				  	  	          customMenuPanel.query('#serviceDown')[0].setHidden(true);
				  	  	          interfaceParamsPanel.setHidden(true);
				  	  	      }
				  	  	  }
				  	  }
				  },{
				  	  xtype:'image',
				  	  src:'/system/sms/weixin/images/up.png',
				  	  itemId:'serviceUp',
				  	  width:32,
				  	  height:32,
				  	  hidden:true,
				  	  style:'margin-top:4px;margin-right:20px;cursor:pointer;',
				  	  listeners:{
				  	  	  click:{
				  	  	      element:'el',
				  	  	      fn:function(){
				  	  	          customMenuPanel.query('#serviceUp')[0].setHidden(true);
				  	  	          customMenuPanel.query('#serviceDown')[0].setHidden(false);
				  	  	          interfaceParamsPanel.setHidden(false);
				  	  	      }
				  	  	  }
				  	  }
				  }]
			}]
		});
		//接口参数panel
		var interfaceParamsPanel = me.createInterfaceParamsPanel(params);
		customMenuPanel.add(interfaceParamsPanel);
		bodyPanel.add(customMenuPanel);
		me.add(bodyPanel);
	},
	/**
	* 创建接口参数panel
	* @param params 参数配置信息
	*/
	createInterfaceParamsPanel:function(params){
		params = params? params:{};
		var me = this;
		var interfaceParamsPanel = Ext.create("Ext.form.Panel",{
			width:500,
			border:false,
			style:'margin-left:40px;',
			defaults:{
				xtype:'textfield',
				margin:'0 0 10 0',
				width:500,
				cls:'field-input'//更改边框样式
			},
			items:[{
				fieldLabel:weixin.setting.appCorpID,
				margin:'10 0 10 0',
				id:'corpid',
				name:'corpid',
				value:params.corpid? params.corpid:''
			},{
				fieldLabel:weixin.setting.addressBookSecret,//通讯录Secret
				name:'corpsecret',
				value:params.corpsecret? params.corpsecret:''
			},{
				fieldLabel:weixin.setting.assistantAgentID,//企业小助手AgentID
				name:'agentid',
				value:params.agentid?params.agentid:''
			},{
				fieldLabel:weixin.setting.assistantSecret,//企业小助手Secret
				name:'msgsecret',
				value:params.msgsecret? params.msgsecret:''
			},{
				fieldLabel:'Token',
				name:'token',
				value:params.token? params.token:''
			},{
				fieldLabel:'Encodingaeskey',
				name:'encodingaeskey',
				value:params.encodingaeskey? params.encodingaeskey:''
			},{
				fieldLabel:'url',
				name:'url',
				value:params.url? params.url:'',
				hidden:true
			},{
				fieldLabel:weixin.setting.wxServiceAddress,//微信服务地址
				name:'w_selfservice_address',
				id:'w_selfservice_address',
				value:params.w_selfservice_address? params.w_selfservice_address:'',
				listeners: {
					blur:function (){
						if(this.value.charAt(this.value.length-1)=='/'){
							this.setValue(this.value.substr(0,this.value.length-1));
						}
					}
				}
			},{
				fieldLabel:weixin.setting.wxmerchants,//微信商户号
				name:'mchid',
				value:params.mchid? params.mchid:''
			},{
				fieldLabel:weixin.setting.credentialKey,//证书key
				name:'mchkey',
				value:params.mchkey? params.mchkey:''
			},{
			    	xtype:'container',
	  			    	items:{
	  			    		xtype:'button',
	  			    		text:'发送测试消息',
	  			    		listeners:{
	  			    			click:function(me){
	  			    				var window = Ext.create('Ext.window.Window', {
	  			    				    title: '测试发送消息',
	  			    				    height: 140,
	  			    				    width: 400,
	  			    				    layout: 'fit',
	  			    				  	modal:true,
	  			    				    items: {  // Let's put an empty grid in just to illustrate fit layout
	  			    				        xtype: 'form',
	  			    				        border: false,
		  			    				    defaultType: 'textfield',
		  			    				    items: [{
		  			    				        fieldLabel: '接收人userid',
		  			    				        name: 'userid',
		  			    				        allowBlank: false
		  			    				    },{
		  			    				    	xtype:'container',
		  			    				    	style:'text-align:center',
		  			    				    	margin:30,
		  			    				    	items:{
		  			    				    		xtype:'button',
		  			    				    		text:'发送',
		  			    				    		listeners:{
		  			    				    			click:function(){
		  			    				    				var form = this.up('form');
		  			    				    				var userid = form.getValues().userid;
		  			    	  			    				var vo = new HashMap();
		  			    	  			    				vo.put('userid',userid);
		  			    	  			    				vo.put('transType','TestSendWeixinMsg');
		  			    	  			    				Rpc({functionId:'1010020225',async:false,success:function(res){
		  			    	  			    					var resultObj = Ext.decode(res.responseText);
		  			    	  			    					if(resultObj.sendFlag){
		  			    	  			    						Ext.MessageBox.alert("提示","发送成功！");
		  			    	  			    					}else{
		  			    	  			    						Ext.MessageBox.alert("提示","发送失败！");
		  			    	  			    					}
		  			    	  			    				}}, vo);
		  			    				    			}
		  			    				    		}
		  			    				    	}
		  			    				    }]
	  			    				    }
	  			    				}).show();
	  			    			}
	  			    		}
	  			    	}
	  			    },{
  					xtype:'fieldset',id:'func_set',title:weixin.setting.appEnterpriseFuncTitle,layout:{xtype:'vbox',align:'stretch'},defaults:{margin:'5 0 5 0'},
  					items:[{
  						xtype:'container',layout:'hbox',margin:'10 0 10 0',
  						items:[{
  							xtype:'combo',
  							fieldLabel:weixin.setting.appEnterpriseFuncSelect,
  							id:'wx_comb',
  							displayField:'name',
  							valueField:'value',
  							isFormField:false,
  							editable:false,
  							store:{
  								fields:['name','menuid','url'],
  								autoLoad: true,
  								proxy:{
  									type: 'ajax',
  							        url: './weixinfuncs.json',
  							        reader: {
  							            type: 'json',
  							            rootProperty: 'WXFunction'
  							        }
  								}
  							}
  						},{
  							xtype:'button',
  							text:weixin.setting.add,margin:'0 0 0 10',
  							handler:function(){
  								var text = this.ownerCt.items.items[0].getRawValue();
  								if(text==null||text==""){
  									Ext.Msg.alert(weixin.setting.promptmessage,weixin.setting.appEnterpriseFuncDesc1);
			  						return;			
  								}
  								var itemList=this.ownerCt.ownerCt.items.items;
  								for(var i = 1;i<itemList.length;i++){
	  								if(text==itemList[i].items.items[0].fieldLabel){
	  									Ext.Msg.alert(weixin.setting.promptmessage,weixin.setting.appEnterpriseFuncDesc2);
	  									return;
	  								}
  								}
  								var jsonlist=this.ownerCt.items.items[0].store.data.items;
  								var menuid ="";
  					  			for(var i=0;i<jsonlist.length;i++){
  					  				if(text==jsonlist[i].data.name){
  					  					menuid=jsonlist[i].data.menuid;
  					  				}
  					  			}
  								this.ownerCt.ownerCt.add({
  									xtype:'container',layout:'hbox',
  									items:[{xtype:'textfield',menuid:menuid,flex:.8,fieldLabel:text,isFormField:false},
  									       {
  												xtype:'image',
  												width:16,height:16,title:weixin.setting.appEnterpriseFuncLink,src:'/images/link.gif',
												listeners: {
	  									         	click: {
		  									            element: 'el', 
		  									            fn: function(){   me.getMenuUrl(me,menuid); }
	  									        	}
	 									        }
  									       },
  									       {
  									    		xtype:'image',
  									  			width:16,
  									   			height:16,
  									    		title:weixin.setting.deleteMenu,
  												src:'/images/delete.gif',
  									  			listeners: {
	  									         	click: {
		  									            element: 'el', 
		  									            fn: function(){ this.component.ownerCt.ownerCt.remove(this.component.ownerCt);}
	  									        	}
  									           }
  									    }
  									       ]
  								});
  							}
  						}]
  					}]
			}]
		});
		for(var p in params){
	  		if(p.substr(0,4)=='menu')
	  			me.addMenuList(p.substr(5),params[p],params[p.substr(5)]);
  		}
		return interfaceParamsPanel;
	},
	getMenuUrl:function(me,menuid){
  			var corpid = Ext.getCmp('corpid').getValue();
  			var domain = Ext.getCmp('w_selfservice_address').getValue();
  			if(corpid.length==0||corpid.indexOf("w")!=0){
  				Ext.Msg.alert(weixin.setting.tip,weixin.setting.appEnterpriseCorpId);
  				return;
  			}
  			if(domain.length==0){
  				Ext.Msg.alert(weixin.setting.tip,weixin.setting.appEnterpriseAddress);
  				return;
  			}
  			//添加对微信服务地址请求协议校验  wangb 20190522 bug 48194
  			if(domain.indexOf('http://')==-1 && domain.indexOf('https://') == -1){
  				Ext.Msg.alert(weixin.setting.tip,weixin.setting.appEnterpriseAddressTip);
  				return;
  			}
  			//xus 18/12/5 社保、养老金查询 支持根据配置的url跳转链接
  			var combstore = Ext.getCmp('wx_comb').getStore().data.items;
  			var wxMenuUrl="";
  			for(var i = 0;i<combstore.length;i++){
  				if(combstore[i].data.menuid==menuid)
  					wxMenuUrl = combstore[i].data.url;
  			}
  			var menuurl = "https://open.weixin.qq.com/connect/oauth2/authorize?appid=";
  			menuurl+=corpid+"&redirect_uri=";
  			if(menuid=='home'){
  				var redirect_uri=encodeURIComponent(domain+"/w_selfservice/oauthservlet?dest="+domain+"/w_selfservice/module/selfservice/home.jsp");
  				var state=getEncodeStr("menuid=home&etoken=ETOKEN");
  				menuurl+=redirect_uri+"&response_type=code&scope=snsapi_base&state="+state+"#wechat_redirect";
  			}else{
	  			var redirect_uri=encodeURIComponent(domain+"/w_selfservice/oauthservlet?dest="+domain+"/w_selfservice/module/selfservice/index.jsp");
	  			var state=getEncodeStr("menuid="+menuid+"&etoken=ETOKEN");
	  			if(wxMenuUrl&&wxMenuUrl!=""){
  					var index = Ext.Array.indexOf(wxMenuUrl,'?');
		  			var wxurl = wxMenuUrl.substring(0,index);
		  			var linkParams = wxMenuUrl.substring(index+1);
		  			if(linkParams.length>0)
		  				linkParams = '&'+linkParams;
  					redirect_uri=encodeURIComponent(domain+"/w_selfservice/oauthservlet?dest="+domain+wxurl);
  		  			state=getEncodeStr("menuid="+menuid+"&etoken=ETOKEN"+linkParams);
  				}
  				menuurl+=redirect_uri+"&response_type=code&scope=snsapi_base&state="+state+"#wechat_redirect";
  			}
  			var window = Ext.create('Ext.window.Window', {
			    title: weixin.setting.appEnterpriseFuncLinkAddress,
			    height: 200,
			    width: 400,
			    layout: 'fit',
			    y:100,
			    items:{
			    	xtype:'textarea',
			    	value: menuurl,
			    	readOnly :true
			    }
			}).show();
	},
	addMenuList:function(menuid,secret,desc){
		var me = this;
	  		Ext.ComponentQuery.query('fieldset')[0].add({
			xtype:'container',layout:'hbox',
			items:[{xtype:'textfield',menuid:menuid,flex:.8,fieldLabel:desc,value:secret,isFormField:false},
			       {
						xtype:'image',
						width:16,height:16,title:'生成链接',src:'/images/link.gif',
					listeners: {
				         	click: {
					            element: 'el', 
					            fn: function(){   me.getMenuUrl(this,menuid); }
				        	}
				        }
			       },
			       {
			    		xtype:'image',
			  			width:16,
			   			height:16,
			    		title:'删除',
						src:'/images/delete.gif',
			  			listeners: {
				         	click: {
					            element: 'el', 
					            fn: function(){ this.component.ownerCt.ownerCt.remove(this.component.ownerCt);}
				        	}
			           }
			    }]
		});
  	},
	/**
	 * 编辑应用配置（新增、回显）window
	 * @param {} itemId 当传递了itemid则是编辑已有服务
	 */
	editAppConfigWin:function(itemId){
		var me = this;
		var paramInfo;
		if(itemId>-1){
			paramInfo = me.echoInfo(appConfigWin,itemId);//应用信息回显
			me.file = paramInfo.logo;
		}
		var appConfigWin = Ext.create("Ext.Window",{//编辑应用配置window
			layout:'fit',
			width:600,
			height:440,
			modal : true,
			resizable:false,
			closable:true,
			title:weixin.setting.enterpriseConfig//企业号配置
		});
		var appConfigPanel = me.createAppConfigPanel(appConfigWin,itemId);
		
		var appHandConfigPanel = me.createAppHandConfigPanel(appConfigWin,itemId);
		var appConfigTab =  Ext.create('Ext.tab.Panel',{
			items:[appConfigPanel,appHandConfigPanel],
			listeners:{
				tabchange:function(t,newPanel,oldPanel){
					if(newPanel.config.itemId == "appHandConfigPanel")
						Ext.getCmp('uploadBox2').add(fileUpLoad);
					else
						Ext.getCmp('uploadBox').add(fileUpLoad);
				}
			}
		});
		
		var fileUpLoad = Ext.create("SYSF.FileUpLoad",{
			margin:'-8 0 0 4',
			upLoadType:1,//指定单文件上传
			readInputWidth:302,
			
			fileExt:"*.jpg;*.jpeg;*.gif;*.bmp;*.png",
			savePath:me.savePath,
			success:function(files){
				me.file = files[0];
			},
			//回调方法scope对象
			callBackScope:appConfigPanel
		});
		appConfigPanel.getForm().setValues(paramInfo);
		appHandConfigPanel.getForm().setValues(paramInfo);
		Ext.getCmp('uploadBox').add(fileUpLoad);
		appConfigWin.add(appConfigTab);
		appConfigWin.show();
	},
	/**
	 * 回显应用信息
	 * @param {} appConfigWin app配置window
	 * @param {} itemId 应用编号
	 */
	echoInfo:function(appConfigWin,itemId){
		var me = this;
		var vo = new HashMap();
		vo.put("itemid",itemId);
		var paramInfo;
		Rpc({functionId:'SYS0000001008',async:false,success:function(res){
			var info = Ext.decode(res.responseText);
			paramInfo = info.paramInfo;
			if(paramInfo.type){
				paramInfo.menutype = paramInfo.type;
				paramInfo.type = 'main';
			}
			else{
				paramInfo.menutype = "";
				paramInfo.type = 'menu';
			}
		}},vo);
		return paramInfo;
	},
	
	createAppHandConfigPanel:function(appConfigWin,itemId){
		var me = this;
		var corpid = Ext.getCmp('corpid').getValue();
		corpid = corpid? corpid:'';
		var domain = Ext.getCmp('w_selfservice_address').getValue();
		domain = domain? domain:paramInfo.url;
		
		var appConfigPanel = Ext.create("Ext.form.Panel",{
			title:weixin.setting.appTitle2,
			itemId:'appHandConfigPanel',
			border:false,
			items:[{
				xtype:'textfield',
				margin:'10 0 15 40',
				height:22,
				name:'name',
				itemId:'name',
				fieldLabel:weixin.setting.appName,//应用名称
				labelAlign:'right',
				width:'80%',
				allowBlank: false,
				beforeLabelTextTpl:"<font color='red'> * </font>"
			},{
				xtype:'textfield',
				height:22,
				margin:'0 0 15 40',
				name:'secret',
				itemId:'secret',
				fieldLabel:weixin.setting.appSecret,
				labelAlign:'right',
				width:'80%',
				allowBlank: false,
				beforeLabelTextTpl:"<font color='red'> * </font>"
			},{
				xtype:'panel',//上传应用logo
				border:false,
				id:'uploadBox2',
				margin:'0 0 10 0',
				width:'100%',
				layout:'hbox',
				items:[{
					xtype:'component',
					margin:'0 0 0 80',
					border:1,
					html:"<font color='red'> * </font>"+weixin.setting.appLogo//应用logo
				}]
			},{
				xtype:'combo',
				height:22,
				margin:'0 0 15 40',
				itemId:'menutype',
				name:'menutype',
				fieldLabel:weixin.setting.funcType,//应用类型
				beforeLabelTextTpl:"<font color='red'> * </font>",
				allowBlank: false,
				labelAlign:'right',
				width:'80%',
				editable:false,
				displayField:'name',
				valueField:'menuid',
				store:{
					fields:['name','menuid'],
					autoLoad: true,
					proxy:{
						type: 'ajax',
				        url: './weixinfuncs.json',
				        reader: {
				            type: 'json',
				            rootProperty: 'WXFunction'
				        }
					}
				},
				listeners:{
					change:function(t,value){
						var secret = appConfigPanel.query('#secret')[0];
						if(!secret.getValue() || secret.getValue().length == 0 ){
							Ext.Msg.alert(weixin.setting.promptmessage,weixin.setting.appsecritfaildesc);
							t.setValue('');
							return;
						}
						var menuurl = "https://open.weixin.qq.com/connect/oauth2/authorize?appid="+corpid+"&redirect_uri=";
    					if(value=='home'){
			  				var redirect_uri=encodeURIComponent(domain+"/w_selfservice/oauthservlet?dest="+domain+"/w_selfservice/module/selfservice/home.jsp");
			  				var state=getEncodeStr("menuid=home&etoken=ETOKEN");
			  				menuurl+=redirect_uri+"&response_type=code&scope=snsapi_base&state="+state+"#wechat_redirect";
			  			}else{
				  			var redirect_uri=encodeURIComponent(domain+"/w_selfservice/oauthservlet?dest="+domain+"/w_selfservice/module/selfservice/index.jsp");
				  			var state=getEncodeStr("menuid="+value+"&etoken=ETOKEN");
			  				menuurl+=redirect_uri+"&response_type=code&scope=snsapi_base&state="+state+"#wechat_redirect";
			  			}
			  			var showurl = appConfigPanel.query('#showurl')[0];
			  			showurl.setHtml(menuurl);
			  			showurl.setHidden(false);
					}
				},
				emptyText: weixin.setting.pleaseSelect//请选择。。。
			},{
				xtype:'panel',
				width:366,
				height:140,
				hidden:true,
				style:'font-size:14px;margin-top:10px;margin-left:145px;margin-bottom:10px;word-break:break-all;',
				itemId:'showurl'
			}],
			buttonAlign:'center',
			buttons: [{
				hidden:appConfigWin? false:true,
				text: weixin.setting.ok,
				formBind:true,//验证通过，则确定亮起
				handler: function() { 
					var values = appConfigPanel.getValues();
					if(!me.file && itemId<=-1){
						Ext.Msg.alert(weixin.setting.promptmessage,weixin.setting.mustSelectFile);//提示：请选择应用logo
						return;
					}
					var vo = new HashMap();
					var paramInfo = new HashMap();
					var logo = new HashMap();
					logo.put("name",me.file.localname);
					logo.put("path",me.file.fullpath);
					logo.put("filename",me.file.filename);
					var fullpath = me.file.fullpath;//前台新增图标用
					if(itemId>-1){
						vo.put("itemid",itemId);
						vo.put("type","update");
					}else{
						vo.put("type","add");
					}
					paramInfo.put("type",values['menutype']);
					paramInfo.put("name",values['name']);
					paramInfo.put("secret",values['secret']);
					paramInfo.put("logo",logo);
					vo.put("paramInfo",paramInfo);
					Rpc({functionId:'SYS0000001009',async:false,success:function(res){
						me.file = "";//logo信息置空
						var info = Ext.decode(res.responseText);
						if(info.result == '1'){//编辑应用
							if(itemId>-1){
								var eachAppContainer = me.query('#'+itemId)[0];
								eachAppContainer.query('#appName')[0].setHtml(values['name']);
								eachAppContainer.query('image')[0].setSrc('/servlet/DisplayOleContent?filePath='+fullpath);
								//通过应用主页类型 自定义菜单逻辑 处理
								var enterpriseParam = me.query('#enterpriseParam');
								if(values['type'] == 'main' && enterpriseParam.length == 0){

								}else if(enterpriseParam.length > 0 && values['type'] == 'main'){
									enterpriseParam[0].destroy();
								}else if(enterpriseParam.length > 0){
									enterpriseParam[0].destroy();
									paramInfo.customMenu = info.customMenu;
									me.enterpriseParam(paramInfo,itemId);
								}else{
									paramInfo.customMenu = info.customMenu;
									me.enterpriseParam(paramInfo,itemId);
								}
							}else{//新增应用
								var wxItemid = info.wxitemid;
								//创建一个app容器
								var eachAppContainer = me.createEachAppContainer(wxItemid,values['name'],fullpath);
								var allAppPanel = me.query('#allApp')[0];//所有app的容器
								var appCount = allAppPanel.items.length;//父容器allAppPanel中子组件数量
								var insertIndex = appCount-1;//每个app容器插入的索引
								allAppPanel.insert(insertIndex,eachAppContainer);
								//通过应用主页类型 自定义菜单逻辑 处理
								var enterpriseParam = me.query('#enterpriseParam');
								if(values['type'] == 'main' && enterpriseParam.length == 0){

								}else if(enterpriseParam.length > 0 && values['type'] == 'main'){
									enterpriseParam[0].destroy();
								}else if(enterpriseParam.length > 0){
									enterpriseParam[0].destroy();
									paramInfo.customMenu = info.customMenu;
									me.enterpriseParam(paramInfo,itemId);
								}else{
									paramInfo.customMenu = info.customMenu;
									me.enterpriseParam(paramInfo,itemId);
								}
								if(me.enterpriseItemId)
									me.query('#'+me.enterpriseItemId)[0].query('#appName')[0].setStyle({color:''});
								me.query('#'+wxItemid)[0].query('#appName')[0].setStyle({color:'#1D8EFF'});
								me.enterpriseItemId = wxItemid;
							}
							Ext.Msg.alert(weixin.setting.promptmessage,weixin.setting.saveSuccess);//提示：保存成功
						}else{
							Ext.Msg.alert(weixin.setting.promptmessage,weixin.setting.saveFail);//提示：保存失败
						}
					}},vo);
					appConfigWin.close();
				},
				scope:this
			},{
				text: weixin.setting.cancel,
				hidden:appConfigWin? false:true,
				handler: function() {
					appConfigWin.close();
				},
				scope:this
			}]
		});
		return appConfigPanel;
	}, 
	/**
	* 创建app配置formpanel
	* @param appConfigWin 编辑应用配置window
	* @param itemId 更新应用则有itemid传过来
	* @return 
	*/
	createAppConfigPanel:function(appConfigWin,itemId){
		var me = this;
		var appConfigPanel = Ext.create("Ext.form.Panel",{
			title:weixin.setting.appTitle1,
			itemId:'appConfigPanel',
			border:false,
			items:[{
				xtype:'textfield',
				margin:'10 0 15 40',
				height:22,
				name:'name',
				id:'name',
				fieldLabel:weixin.setting.appName,//应用名称
				labelAlign:'right',
				width:'80%',
				allowBlank: false,
				beforeLabelTextTpl:"<font color='red'> * </font>"
			},{
				xtype:'textfield',
				height:22,
				margin:'0 0 15 40',
				name:'agentid',
				id:'agentid',
				fieldLabel:weixin.setting.appAgentId,
				labelAlign:'right',
				width:'80%',
				allowBlank: false,
				beforeLabelTextTpl:"<font color='red'> * </font>"
			},{
				xtype:'textfield',
				height:22,
				margin:'0 0 15 40',
				name:'secret',
				id:'secret',
				fieldLabel:weixin.setting.appSecret,
				labelAlign:'right',
				width:'80%',
				allowBlank: false,
				beforeLabelTextTpl:"<font color='red'> * </font>"
			},{
				xtype:'textfield',
				height:22,
				margin:'0 0 15 40',
				name:'url',
				id:'url',
				fieldLabel:weixin.setting.trustDomainName,//信任域名
				labelAlign:'right',
				width:'80%',
				allowBlank: false,
				beforeLabelTextTpl:"<font color='red'> * </font>"
			},{
				xtype:'panel',//上传应用logo
				border:false,
				id:'uploadBox',
				margin:'0 0 10 0',
				width:'100%',
				layout:'hbox',
				items:[{
					xtype:'component',
					margin:'0 0 0 80',
					html:"<font color='red'> * </font>"+weixin.setting.appLogo//应用logo
				}]
			},{
				xtype:'combo',
				height:22,
				margin:'0 0 15 40',
				name:'type',
				id:'type',
				fieldLabel:weixin.setting.appType,//应用类型
				labelAlign:'right',
				width:'80%',
				editable:false,
				displayField:'name',
				valueField:'type',
				emptyText: weixin.setting.pleaseSelect,//请选择。。。
				allowBlank: false,
				beforeLabelTextTpl:"<font color='red'> * </font>",
				store:{
					fields:['name','type'],
					data:[{name:weixin.setting.appTypeMain,type:'main'},{name:weixin.setting.appTypeMenu,type:'menu'}]
				},
				listeners:{
					change:function(t,value){
						if(value == 'main'){
						    appConfigPanel.query('#menutype')[0].setHidden(false);
						    appConfigPanel.query('#publishBtn')[0].setHidden(false);
						}
						else{
						    appConfigPanel.query('#menutype')[0].setHidden(true);
						    appConfigPanel.query('#publishBtn')[0].setHidden(true);
						}
					}
				}
			},{
				xtype:'combo',
				height:22,
				margin:'0 0 15 40',
				id:'menutype',
				name:'menutype',
				hidden:true,
				fieldLabel:weixin.setting.funcType,//应用类型
				labelAlign:'right',
				width:'80%',
				editable:false,
				displayField:'name',
				valueField:'menuid',
				store:{
					fields:['name','menuid'],
					autoLoad: true,
					proxy:{
						type: 'ajax',
				        url: './weixinfuncs.json',
				        reader: {
				            type: 'json',
				            rootProperty: 'WXFunction'
				        }
					}
				},
				emptyText: weixin.setting.pleaseSelect//请选择。。。
			},{
				xtype:'textareafield',
				height:22,
				margin:'0 0 15 40',
				name:'description',
				id:'description',
				fieldLabel:weixin.setting.appDescription,//应用简介
				labelAlign:'right',
				width:'80%',
				allowBlank: false,
				beforeLabelTextTpl:"<font color='red'> * </font>",
				emptyText:weixin.setting.appDescInfo
			}],
			buttonAlign:'center',
			buttons: [{
				hidden:appConfigWin? false:true,
				text: weixin.setting.ok,
				formBind:true,//验证通过，则确定亮起
				handler: function() { 
					var values = appConfigPanel.getValues();
					if(!me.file && itemId<=-1){
						Ext.Msg.alert(weixin.setting.promptmessage,weixin.setting.mustSelectFile);//提示：请选择应用logo
						return;
					}
					var descLength = 0;
					for(var i = 0; i < values['description'].length; i++) {
		        		var a = values['description'].charAt(i);
		        		if (a.match(/[^\x00-\xff]/ig) != null) {//如果是汉字
		        		    descLength = descLength+2;//一个汉字占两个字节
		        		}else {
		        		    descLength =descLength+1;//字母数字等占一个字节
		        		}
		        		if(descLength > 8)
		        			break;
		        	}
		        	if(descLength < 8){
		        		Ext.Msg.alert(weixin.setting.promptmessage,weixin.setting.appDescTip);//提示：应用简历格式不对
						return;
		        	}
					var vo = new HashMap();
					var paramInfo = new HashMap();
					var logo = new HashMap();
					logo.put("name",me.file.localname);
					logo.put("path",me.file.fullpath);
					logo.put("filename",me.file.filename);
					var fullpath = me.file.fullpath;//前台新增图标用
					if(itemId>-1){
						vo.put("itemid",itemId);
						vo.put("type","update");
					}else{
						vo.put("type","add");
					}
					if(values['type'] == 'main')
						paramInfo.put("type",values['menutype']);
					else
						paramInfo.put("type",'');
					paramInfo.put("name",values['name']);
					paramInfo.put("agentid",values['agentid']);
					paramInfo.put("secret",values['secret']);
					paramInfo.put("url",values['url']);
					paramInfo.put("description",values['description']);
					paramInfo.put("logo",logo);
					vo.put("paramInfo",paramInfo);
					Rpc({functionId:'SYS0000001009',async:false,success:function(res){
						me.file = "";//logo信息置空
						var info = Ext.decode(res.responseText);
						if(info.result == '1'){//编辑应用
							if(itemId>-1){
								var eachAppContainer = me.query('#'+itemId)[0];
								eachAppContainer.query('#appName')[0].setHtml(values['name']);
								eachAppContainer.query('image')[0].setSrc('/servlet/DisplayOleContent?filePath='+fullpath);
								//通过应用主页类型 自定义菜单逻辑 处理
								var enterpriseParam = me.query('#enterpriseParam');
								if(values['type'] == 'main' && enterpriseParam.length == 0){

								}else if(enterpriseParam.length > 0 && values['type'] == 'main'){
									enterpriseParam[0].destroy();
								}else if(enterpriseParam.length > 0){
									enterpriseParam[0].destroy();
									paramInfo.customMenu = info.customMenu;
									me.enterpriseParam(paramInfo,itemId);
								}else{
									paramInfo.customMenu = info.customMenu;
									me.enterpriseParam(paramInfo,itemId);
								}
							}else{//新增应用
								var wxItemid = info.wxitemid;
								//创建一个app容器
								var eachAppContainer = me.createEachAppContainer(wxItemid,values['name'],fullpath);
								var allAppPanel = me.query('#allApp')[0];//所有app的容器
								var appCount = allAppPanel.items.length;//父容器allAppPanel中子组件数量
								var insertIndex = appCount-1;//每个app容器插入的索引
								allAppPanel.insert(insertIndex,eachAppContainer);
								//通过应用主页类型 自定义菜单逻辑 处理
								var enterpriseParam = me.query('#enterpriseParam');
								if(values['type'] == 'main' && enterpriseParam.length == 0){

								}else if(enterpriseParam.length > 0 && values['type'] == 'main'){
									enterpriseParam[0].destroy();
								}else if(enterpriseParam.length > 0){
									enterpriseParam[0].destroy();
									paramInfo.customMenu = info.customMenu;
									me.enterpriseParam(paramInfo,itemId);
								}else{
									paramInfo.customMenu = info.customMenu;
									me.enterpriseParam(paramInfo,itemId);
								}
								if(me.enterpriseItemId)
									me.query('#'+me.enterpriseItemId)[0].query('#appName')[0].setStyle({color:''});
								me.query('#'+wxItemid)[0].query('#appName')[0].setStyle({color:'#1D8EFF'});
								me.enterpriseItemId = wxItemid;
							}
							Ext.Msg.alert(weixin.setting.promptmessage,weixin.setting.saveSuccess);//提示：保存成功
						}else{
							Ext.Msg.alert(weixin.setting.promptmessage,weixin.setting.saveFail);//提示：保存失败
						}
					}},vo);
					appConfigWin.close();
				},
				scope:this
			},{
				xtype:'button',
				itemId:'publishBtn',
				html:weixin.setting.publish.replace(' ',''),//发布
				formBind:true,//验证通过，则确定亮起
				listeners:{
					click:function(btn){// 先保存  再发布
						var values = appConfigPanel.getValues();
						if(!me.file && itemId<=-1){
							Ext.Msg.alert(weixin.setting.promptmessage,weixin.setting.mustSelectFile);//提示：请选择应用logo
							return;
						}
						var descLength = 0;
						for(var i = 0; i < values['description'].length; i++) {
			        		var a = values['description'].charAt(i);
			        		if (a.match(/[^\x00-\xff]/ig) != null) {//如果是汉字
			        		    descLength = descLength+2;//一个汉字占两个字节
			        		}else {
			        		    descLength =descLength+1;//字母数字等占一个字节
			        		}
			        		if(descLength > 8)
			        			break;
			        	}
			        	if(descLength < 8){
			        		Ext.Msg.alert(weixin.setting.promptmessage,weixin.setting.appDescTip);//提示：应用简历格式不对
							return;
			        	}
						var vo = new HashMap();
						var paramInfo = new HashMap();
						var logo = new HashMap();
						logo.put("name",me.file.localname);
						logo.put("path",me.file.fullpath);
						logo.put("filename",me.file.filename);
						var fullpath = me.file.fullpath;//前台新增图标用
						if(itemId>-1){
							vo.put("itemid",itemId);
							vo.put("type","update");
						}else{
							vo.put("type","add");
						}
						if(values['type'] == 'main')
							paramInfo.put("type",values['menutype']);
						else
							paramInfo.put("type",'');
						paramInfo.put("name",values['name']);
						paramInfo.put("agentid",values['agentid']);
						paramInfo.put("secret",values['secret']);
						paramInfo.put("url",values['url']);
						paramInfo.put("description",values['description']);
						paramInfo.put("logo",logo);
						vo.put("paramInfo",paramInfo);
						vo.put("release","release"); //发布操作
						Rpc({functionId:'SYS0000001012',async:false,success:function(res){
							/**
							 * 1.发布成功  参数值 1 
							 * 3.保存数据失败   参数值 2
							 * 3.发布失败   3
							 */
							me.file = "";//logo信息置空
							var info = Ext.decode(res.responseText);
							if(info.result == 1){//编辑 发布
								if(itemId>-1){
									var eachAppContainer = me.query('#'+itemId)[0];
									eachAppContainer.query('#appName')[0].setHtml(values['name']);
									eachAppContainer.query('image')[0].setSrc('/servlet/DisplayOleContent?filePath='+fullpath);
									//通过应用主页类型 自定义菜单逻辑 处理
									var enterpriseParam = me.query('#enterpriseParam');
									if(enterpriseParam.length > 0 && values['type'] == 'main'){
										enterpriseParam[0].destroy();
									}
									if(enterpriseParam.length == 0 && values['type'] == 'menu'){
										me.enterpriseParam(paramInfo,itemId);
									}
								}else{//新增发布
									var wxItemid = info.wxitemid;
									//创建一个app容器
									var eachAppContainer = me.createEachAppContainer(wxItemid,values['name'],fullpath);
									var allAppPanel = me.query('#allApp')[0];//所有app的容器
									var appCount = allAppPanel.items.length;//父容器allAppPanel中子组件数量
									var insertIndex = appCount-1;//每个app容器插入的索引
									allAppPanel.insert(insertIndex,eachAppContainer);
									//通过应用主页类型 自定义菜单逻辑 处理
									var enterpriseParam = me.query('#enterpriseParam');
									if(enterpriseParam.length > 0 && values['type'] == 'main'){
										enterpriseParam[0].destroy();
									}
									if(enterpriseParam.length == 0 && values['type'] == 'menu'){
										me.enterpriseParam(paramInfo,wxItemid);
									}
									if(me.enterpriseItemId)
										me.query('#'+me.enterpriseItemId)[0].query('#appName')[0].setStyle({color:''});
									me.query('#'+wxItemid)[0].query('#appName')[0].setStyle({color:'#1D8EFF'});
									me.enterpriseItemId = wxItemid;
								}
								Ext.Msg.alert(weixin.setting.promptmessage,weixin.setting.publishSuccess);//提示： 发布成功
							}else if(info.result == 2){
								Ext.Msg.alert(weixin.setting.promptmessage,weixin.setting.saveFail);//提示：保存失败
							}else if(info.result == 3){
								Ext.Msg.alert(weixin.setting.promptmessage,info.error_msg);//提示： 发布失败
							}
						}},vo);
						appConfigWin.close();
					}
				}
			},{
				text: weixin.setting.cancel,
				hidden:appConfigWin? false:true,
				handler: function() {
					appConfigWin.close();
				},
				scope:this
			}]
		});
		return appConfigPanel;
	},
	/**
	* 创建每个app的container
	* @param itemId 应用编号
	* @param itemName 应用名称
	* @param fullPath logo加密路径
	*/
	createEachAppContainer:function(itemId,itemName,fullPath){
		var me = this;
		var eachAppContainer = Ext.create("Ext.Container",{
			itemId:itemId,
			margin:'5 0 5 5',
			items:[{
				xtype:'image',
				height:60,
				width:60,
				style:'border-radius:50px',
				src:'/servlet/DisplayOleContent?filePath='+fullPath
			},{
				xtype:'image',
				itemId:'appDel',
				height:15,
				width:15,
				hidden:true,
				margin:'0 0 40 -15',
				src:'/workplan/image/remove.png',
				listeners:{
					element:'el',
					click:function(event){
						event.stopPropagation();//阻止大panel的点击事件
						Ext.Msg.show({
							title:weixin.setting.promptmessage,//提示信息
							msg:weixin.setting.realDeleteApp,//确定删除该应用吗？
							buttons: Ext.Msg.YESNO,
							buttonText:{
								yes:weixin.setting.ok,//确定
								no:weixin.setting.cancel//取消
							},
							fn:function(btn){
								if(btn=='yes'){
									var vo = new HashMap();
									vo.put("itemid",eachAppContainer.config.itemId);
									if(me.enterpriseItemId == eachAppContainer.config.itemId){//删除应用是选中的应用
										me.enterpriseItemId = '';
										if(me.query('#enterpriseParam').length > 0)
											me.query('#enterpriseParam')[0].destroy();
									}
									Rpc({functionId:'SYS0000001010',async:false,success:function(res){
										var info = Ext.decode(res.responseText);
										if(info.result){
											var allAppPanel = me.query('#allApp')[0];
											allAppPanel.remove(eachAppContainer);
											Ext.Msg.alert(weixin.setting.promptmessage,weixin.setting.deleteMenuSuccess);//提示：删除成功
										}else{
											Ext.Msg.alert(weixin.setting.promptmessage,weixin.setting.deleteMenuFail);//提示：删除失败
										}
									}},vo);
								}
							},
							icon: Ext.MessageBox.QUESTION
						});
					}
				}
			},{
				xtype:'component',
				itemId:'appName',
				style:'text-align:center;word-break:break-all;width:60px',
				html:itemName
			}],
			listeners:{
				element:'el',
				mouseover:function(){
					eachAppContainer.query('#appDel')[0].show();
				},
				mouseout:function(){
					eachAppContainer.query('#appDel')[0].hide();
				},
				dblclick:function(){
					me.editAppConfigWin(itemId);//编辑应用
				},
				click:function(){
					var itemId = eachAppContainer.config.itemId;
					if(me.enterpriseItemId && me.enterpriseItemId == itemId)
						return;
					if(me.enterpriseItemId && me.enterpriseItemId != itemId)
						me.query('#'+me.enterpriseItemId)[0].query('#appName')[0].setStyle({color:''});
					eachAppContainer.query('#appName')[0].setStyle({color:'#1D8EFF'});
					me.enterpriseItemId = itemId;
					var paramInfo = me.echoInfo('',itemId);
					if(paramInfo.menutype){//工作台应用主页
						if(me.query('#enterpriseParam').length > 0)
							me.query('#enterpriseParam')[0].destroy();
						me.editAppConfigWin(itemId);//编辑应用
						return;
					}
					if(me.query('#enterpriseParam').length)
						me.query('#enterpriseParam')[0].destroy();
					me.enterpriseParam(paramInfo,itemId);
										
				}
			}
		});
		return eachAppContainer;
	},
	/**
	 * 企业号 应用 自定义菜单配置  
	 * param 企业号相关参数
	 * itemId 应用编号
	 */
	enterpriseParam:function(param,itemId){
		var me = this;
		me.enterpriseConfigParam(param,itemId);
	},
	/**
	 * 企业号 应用参数显示
	 * param 企业号相关参数
	 * itemId 应用编号
	 */
	enterpriseConfigParam:function(param,itemid){
		var me = this;
		
		var funcParamPanel = Ext.create('Ext.form.Panel',{
			itemId:'enterpriseParam',
			border:false,
			width:'100%',
			flex:1,
			items:[{
				xype:'panel',//titlePanel
				border:false,
				width:'100%',
				bodyStyle:'background:#e4e4e4;',
				height:40,
				layout:{
					type:'hbox',
					align:'center'
				},
				items:[{
					xtype:'component',
					margin:'0 0 0 10',
					style:'font-size:18px',
					flex:1,
					html:weixin.setting.enterpriseFuncCustomMenu //应用自定义菜单配置
				},{
					xtype:'button',
					itemId:'saveBtn',
					flag:1,//初始化先保存再发布标识   1：已保存
					height:30,
					width:60,
					html:'<div style="font-size:14px;">'+weixin.setting.save+'</div>',//保存
					listeners:{
						click:function(btn){
							var vo = me.menusEnterpriseData(funcParamPanel,itemid);
							Rpc({functionId:'SYS0000001013',success:function(resp){
								var info = Ext.decode(resp.responseText);
								if(info.result == 1){
									Ext.Msg.alert(weixin.setting.promptmessage,weixin.setting.saveSuccess);//提示：保存成功
								}else if(info.result == 2){
									Ext.Msg.alert(weixin.setting.promptmessage,weixin.setting.saveFail);//提示：保存失败
								}
							}},vo);
						}
					}
				},{
					xtype:'button',
					itemId:'publishBtn',
					flag:0,//0:未发布
					height:30,
					width:60,
					margin:'0 10 0 10',
					html:'<div style="font-size:14px;">'+weixin.setting.publish+'</div>',//发布
					listeners:{
						click:function(btn){
							var vo = me.menusEnterpriseData(funcParamPanel,itemid);
							vo.put('release','release');//发布标识
							Rpc({functionId:'SYS0000001014',success:function(resp){
								var info = Ext.decode(resp.responseText);
								if(info.result == 1){
									Ext.Msg.alert(weixin.setting.promptmessage,weixin.setting.publishSuccess);//提示：发布成功
								}else if(info.result == 2){
									Ext.Msg.alert(weixin.setting.promptmessage,weixin.setting.saveFail);//提示：发布失败
								}else if(info.result == 3){
									Ext.Msg.alert(weixin.setting.promptmessage,info.error_msg);//提示：保存失败
								}
							}},vo);
						}
					}
				}]
			},{
				xtype:'container',
				border:0,
				width:'100%',
				flex:1,
				itemId:'enterpriseParamMenu'
			}]
		});
		var enterprise = me.query('#enterprise')[0];
		enterprise.add(funcParamPanel);
		var enterpriseConfigPanel = me.query('#enterpriseParam')[0];
		enterpriseConfigPanel.setHidden(false);
		me.createEnterpriseFuncParam(param);
	},
	/**
	 * 企业号 应用自定义菜单
	 * itemId 应用编号
	 */
	createEnterpriseFuncParam:function(paramInfo){
		var me = this;
		if(!paramInfo.customMenu)
			return;
		me.maxEnterpriseFucOrder = 0 ;	
		paramInfo.customMenu.forEach(function(menu, index) {// 获取二级菜单最大的order（顺序值）
			if(menu.functions){
				menu.functions.forEach(function(tempfunc, tempindex) {
					if(parseInt(tempfunc.order) > me.maxEnterpriseFucOrder) {
						me.maxEnterpriseFucOrder = parseInt(tempfunc.order);
					}
				});
			}else{
				me.maxEnterpriseFucOrder = 0;
			}
		});
		paramInfo.customMenu.forEach(function(param, index) {
			me.addEnterpriseFuncMenu(param,paramInfo);
		});
	},
	/**
	 * 企业号 应用 添加一级菜单
	 *
	 */
	addEnterpriseFuncMenu:function(param,paramInfo){
		var me = this;
		var firstorder = param.order;
		var id = "first-" + firstorder;
		var menuName = "firstname-" + firstorder;
		var menutypeName = "firsttype-" + firstorder;
		var menu = Ext.create("Ext.form.Panel", {
			  firstmenutype : "recruit",// 一级菜单类型
			  itemId : id,
			  width : '100%',
			  order : "",
			  border:0,
			  style:'border-bottom:1px solid #d5d5d5;padding-top:8px;padding-bottom:8px;',
			  layout : {
				  type : "vbox"
			  },
			  items : [{
				  // 一级菜单名和功能
				  xtype : "panel",
				  border : 0,
				  itemId : "1stfunction" + firstorder,
				  width : "100%",
				  layout : {
					  type : 'hbox',
					  pack:'center',
				  	  align:'center'
				  },
				  style:'margin-top:8px;',
				  items : [{
					  xtype : 'panel',
					  itemId : "panel-" + firstorder,
					  border : 0,
					  width : 90,
					  style:'margin-left:40px;cursor:pointer;color:#1B4A98;',
					  html : '<font style="font-size:16px;margin-left:2px;line-height:26px;color:#1B4A98;"/>'+param.menuname+'</font>',
					  listeners : {
						  click : {
							  element : 'el',
							  fn : function() {
							  	  var img =menu.query("#image-" + firstorder)[0];
							 	  img.setHidden(true);
								  var text = menu.query('#text-' + firstorder)[0];
								  var textPanel = menu.query("#panel-" + firstorder)[0];
								  if (textPanel && !textPanel.isHidden()) {
									  textPanel.setHidden(true);
									  text.setHidden(false);
									  text.focus();
								  }
							  }
						  },
						  mouseover:{
						  	 element : 'el',
							 fn : function() {
							 	var img = menu.query("#image-" + firstorder)[0];
							 	img.setHidden(false);
							 }
						  },
						  mouseout:{
						  	 element : 'el',
							 fn : function() {
							 	var img =menu.query("#image-" + firstorder)[0];
							 	img.setHidden(true);
							 }
						  }
					  }
				  }, {
					  xtype : 'textfield',
					  itemId : "text-" + firstorder,
					  name : menuName,
					  style:'margin-left:38px;',
					  fieldStyle:'font-size:16px;',
					  hidden : true,
					  value : param.menuname,
					  listeners:{
						  blur:function(t){
							  t.setHidden(true);
							  var charnum = 0;//字节数
				        	  var varlength = 0;//字符长度
				        	  var realValue =this.value;
			        		  for (var i = 0; i < this.value.length; i++) {
			        		      if (this.value.charCodeAt(i)>255) {//如果是汉字
			        		          charnum = charnum+2;//一个汉字占两个字节
			        		          varlength = varlength+1;
			        		       }
			        		       else {
			        		          charnum =charnum+1;//字母数字等占一个字节
			        		          varlength = varlength+1;
			        		        }
			        		        if(charnum>8){
			        		        	Ext.MessageBox.alert(weixin.setting.tip,weixin.setting.maxWord8);//提示信息   请输入8个以内的字节（一个汉字占两个字节）
			        		        	realValue = this.value.substring(0,varlength-1);
				    					this.setValue(realValue);
			        		        }
			        		    }
			        		  if(!realValue){
			        			  realValue = weixin.setting.firstMenuName
			        		  }
							  var textPanel = menu.query("#panel-" + firstorder)[0];
							  textPanel.setHtml('<font style="font-size:16px;margin-left:2px;line-height:26px;color:#1B4A98;"/>'+realValue+'</font>');
							  textPanel.setHidden(false);
						  }
					  }
				  }, {
					  xtype:'hiddenfield',
					  value:param.menutype,
					  name:'firsttype-'+firstorder,
					  itemId:'text-hidden'+firstorder
				  },{
					  xtype : "image",
					  itemId : "image-" + firstorder,
					  src : "/system/sms/weixin/images/edit.png",
					  style:'margin-left:-20px;cursor:pointer;',
					  hidden:true,
					  width : 20,
					  height : 20,
					  listeners : {
						  click : {
							  element : 'el',
							  fn : function() {
							  	  var img =menu.query("#image-" + firstorder)[0];
							 	  img.setHidden(true);
								  var text = menu.query('#text-' + firstorder)[0];
								  var textPanel = menu.query("#panel-" + firstorder)[0];
								  if (textPanel && !textPanel.isHidden()) {
									  textPanel.setHidden(true);
									  text.setHidden(false);
									  text.focus();
								  }
							  }
						  },
						  mouseover:{
						  	 element : 'el',
							 fn : function() {
							 	var img = menu.query("#image-" + firstorder)[0];
							 	img.setHidden(false);
							 }
						  },
						  mouseout:{
						  	 element : 'el',
							 fn : function() {
							 	var img =menu.query("#image-" + firstorder)[0];
							 	img.setHidden(true);
							 }
						  }
					  }
				  },{
					  xtype:'component',
					  flex:1
				  },{
				      xtype:'hidden',
				      value:param.menuurl,
				      itemId:'url-'+firstorder,
					  name:'firsturl-'+firstorder
				  },{
				  	  xtype:'panel',
				  	  border:0,
				  	  height:32,
				  	  hidden:!param.menuurl? false:true,
				  	  itemId:'textlink-'+firstorder,
				  	  style:'margin-right:80px;',
				  	  html:'<div style="color:#1B4A98;font-size:14px;cursor:pointer;margin-top:6px;">'+weixin.setting.urlDescText+'</div>',
				  	  listeners:{
				  	  	  click:{
				  	  	  	  element:'el',
				  	  	  	  fn:function(){
				  	  	  	  	  var menuurl = menu.query('#url-'+firstorder)[0];
				  	  	  	  	  var textPanel = menu.query('#textlink-'+firstorder)[0];
				  	  	  	  	  var imgPanel = menu.query('#imglink-'+firstorder)[0];
				  	  	  	  	  me.urlEnterpriseConfig(menuurl,textPanel,imgPanel,'first','','',paramInfo);
				  	  	  	  }
				  	  	  }
				  	  }
				  },{
				  	xtype:'image',
				  	src:'/system/sms/weixin/images/link.png',
				  	width:32,
				  	height:32,
				  	style:'margin-right:104px;cursor:pointer',
				  	itemId:'imglink-'+firstorder,
				  	hidden:param.menuurl? false:true,
				  	listeners:{
				  		click:{
				  			element:'el',
				  			fn:function(){
				  				var menuurl = menu.query('#url-'+firstorder)[0];
				  	  	  	  	var textPanel = menu.query('#textlink-'+firstorder)[0];
				  	  	  	  	var imgPanel = menu.query('#imglink-'+firstorder)[0];
				  	  	  	  	me.urlEnterpriseConfig(menuurl,textPanel,imgPanel,'first','','',paramInfo);
				  			}
				  		}
				  	}
				  }]
			  }]
		  });
		me.addEnterpriseFuncSecondMenu(menu, param.functions, firstorder, paramInfo);
		me.query('#enterpriseParamMenu')[0].add(menu);
	},
	/**
	 * 企业号 应用添加二级菜单
	 *
	 */
	addEnterpriseFuncSecondMenu:function(menu,functions,firstorder,paramInfo){
		var me = this;
		var itemId = menu.config.itemId + "-second"
		var secondMenu = Ext.create('Ext.panel.Panel', {
			  border : 0,
			  itemId : itemId,
			  width : '100%',
			  layout : 'vbox',
			  style:'margin-left:40px',
			  items : []
		  });
		menu.add(secondMenu);
		me.dealEnterpriseSencondAddMenu(secondMenu, firstorder,paramInfo);
		if(functions){
			functions.forEach(function(functions, i) {
				me.addEnterpriseSecondMenu(secondMenu, firstorder, functions, functions.order, paramInfo);
			});
		}
		var length = secondMenu.items.keys.length;
		if (length > 5) {
			secondMenu.query('#addsecondmenu')[0].setHidden(true);
		}
	},
	/**
	 * 企业号  应用 处理二级菜单下的新增按钮
	 * 
	 * @param {}
	 *         secondMenu 二级菜单对象
	 * @param {}
	 *         firstorder 一级菜单顺序标识
	 */
	dealEnterpriseSencondAddMenu : function(secondMenu, firstorder,paramInfo) {
		var me = this;
		var sencondAddMenuPanel = Ext.create('Ext.panel.Panel', {
			  border : 0,
			  itemId : "addsecondmenu",
			  layout : {
				  type : "hbox",
				  pack : "center"
			  },
			  style:'margin-top:8px;margin-bottom:8px;',
			  items : [{
				  xtype : "image",
				  height : 32,
				  width : 32,
				  src : "/system/sms/weixin/images/add.png",
				  style:'margin-left:40px;margin-right:4px;cursor:pointer;',
				  listeners : {
					  click : {
						  element : "el",
						  fn : function() {// 新建二级菜单
							  var list = [];
							  list.push({
								    functionname : weixin.setting.secondMenuName,//二级菜单名称
								    order : 1
							    });
							  var newsecondorder = me.maxEnterpriseFucOrder + 1;
							  me.maxEnterpriseFucOrder = newsecondorder;
							  list[0].order = me.maxEnterpriseFucOrder;
							  me.addEnterpriseSecondMenu(secondMenu, firstorder, list[0], newsecondorder,paramInfo);
							  // 删了再加id重复
							  me.limitSecondMenu(secondMenu)
						  }
					  }
				  }
			  }, {
				  xtype : "component",
				  style:'margin-top:5px',
				  html : '<font style="font-size:16px;">'+weixin.setting.addsecondMenu+'</font>'//添加二级菜单
			  }]
		  });
		secondMenu.add(sencondAddMenuPanel);
	},
	/**
	 * 企业号 应用 添加二级菜单
	 * 
	 * @param {}
	 *         secondMenu 二级菜单对象
	 * @param {}
	 *         firstorder 一级菜单顺序标识
	 * @param {}
	 *         functions 当前二级菜单数据
	 * @param {}
	 *         secondorder 二级菜单顺序标识
	 */
	addEnterpriseSecondMenu : function(secondMenu, firstorder, functions, secondorder, paramInfo) {
		var me = this;
		if (functions.functionname == undefined) {
			return;
		}
		if (!secondorder) {// 没有order,取最大order+1
			secondorder = me.maxEnterpriseFucOrder + 1;
			me.maxEnterpriseFucOrder = secondorder;
			functions.order = me.maxEnterpriseFucOrder;
		}
		var menuPanelItemid = secondMenu.id + "-" + secondorder
		var secondmenu = Ext.create("Ext.form.Panel", {
			  border : 0,
			  itemId : menuPanelItemid,
			  layout : {
				  type : "hbox",
				  pack:'center',
				  align:'center'
			  },
			  width : "100%",
			  style:'margin-top:8px;',
			  items : [{
				  // 二级菜单名
				  xtype : "panel",
				  border:0,
				  width:154,
				  style:'margin-left:40px;cursor:pointer;',
				  itemId : "secondpanel-" + secondorder,
				  html : '<font style="font-size:16px;margin-left:2px;line-height:26px;color:#1B4A98;">' + functions.functionname +'</font>',
				  listeners : {
					  click : {
						  element : 'el',
						  fn : function() {
						  	  var img = secondmenu.query("#image-" + secondorder)[0];
							  img.setHidden(true);
							  var text = secondmenu.query('#secondtext-' + secondorder)[0];
							  var textPanel = secondmenu.query("#secondpanel-" + secondorder)[0];
							  if (textPanel && !textPanel.isHidden()) {
								  textPanel.setHidden(true);
								  text.setHidden(false);
								  text.focus();
							  }
						  }
					  },
					  mouseover:{
						  element : 'el',
						  fn : function() {
							  var img = secondmenu.query("#image-" + secondorder)[0];
							  img.setHidden(false);
						  }
					  },
					  mouseout:{
						  element : 'el',
						  fn : function() {
							   var img = secondmenu.query("#image-" + secondorder)[0];
							   img.setHidden(true);
						  }
					  }
				  }
			  }, {
				  xtype : 'textfield',
				  name : "secondname-" + firstorder + "-" + secondorder,
				  itemId : "secondtext-" + secondorder,
				  style:'margin-left:38px;',
				  fieldStyle:'font-size:16px;',
				  width : 134,
				  hidden : true,
				  value : functions.functionname,
				  listeners:{
					  blur:function(t){
						  t.setHidden(true);
						  var charnum = 0;//字节数
			        	  var varlength = 0;//字符长度
			        	  var realValue =this.value;
		        		  for (var i = 0; i < this.value.length; i++) {
		        		      if (this.value.charCodeAt(i)>255) {//如果是汉字
		        		          charnum = charnum+2;//一个汉字占两个字节
		        		          varlength = varlength+1;
		        		       }
		        		       else {
		        		          charnum =charnum+1;//字母数字等占一个字节
		        		          varlength = varlength+1;
		        		        }
		        		        if(charnum>16){
		        		        	Ext.MessageBox.alert(weixin.setting.tip,weixin.setting.maxWord16);//提示信息   请输入16个字节以内的字节（一个汉字占两个字节）
		        		        	realValue = this.value.substring(0,varlength-1);
			    					this.setValue(realValue);
		        		        }
		        		    }
		        		  //如果没有值的情况下 赋值二级菜单名称
		        		  if(!realValue){
		        			  realValue = weixin.setting.secondMenuName;
		        		  }
						  var textPanel = secondmenu.query("#secondpanel-" + secondorder)[0];
						  textPanel.setHtml('<font style="font-size:16px;margin-left:2px;line-height:26px;color:#1B4A98;">' +realValue+ '</font>');
						  textPanel.setHidden(false);
					  }
				  }
			  }, {
				  xtype : "image",// 编辑二级菜单
				  src : "/system/sms/weixin/images/edit.png",
				  itemId:"image-" + secondorder,
				  hidden:true,
				  style:'cursor:pointer;margin-left:-20px;',
				  width : 20,
				  height : 20,
				  listeners : {
					  click : {
						  element : 'el',
						  fn : function() {
							  var img = secondmenu.query("#image-" + secondorder)[0];
							  img.setHidden(true);
							  var text = secondmenu.query('#secondtext-' + secondorder)[0];
							  var textPanel = secondmenu.query("#secondpanel-" + secondorder)[0];
							  if (textPanel && !textPanel.isHidden()) {
								  textPanel.setHidden(true);
								  text.setHidden(false);
								  text.focus();
							  }
						  }
					  },
					  mouseover:{
					  	  element　: 'el',
					  	  fn : function(){
					  	  	  var img = secondmenu.query("#image-" + secondorder)[0];
							  img.setHidden(false);
					  	  }
					  },
					  mouseout:{
						  element : 'el',
						  fn : function() {
							   var img = secondmenu.query("#image-" + secondorder)[0];
							   img.setHidden(true);
						  }
					  }
				  }
			  },{
				  xtype:'component',
				  width:200
			  },{
				  xtype:'component',
				  flex:1
			  },{
			  	xtype:'hidden',
			  	value:functions.functionmenu,
			  	itemId:'funcmenu-'+ firstorder + "-" + secondorder,
			  	name:'secondfuncmenu-'+ firstorder + "-" + secondorder
			  },{
				  xtype:'hidden',
				  value:functions.functionurl,
				  itemId:'funcurl-'+ firstorder + "-" + secondorder,
				  name:'secondfuncurl-'+ firstorder + "-" + secondorder
			  },{
				  xtype:'panel',
				  border:0,
				  height:32,
				  itemId:'functextlink-'+ firstorder + "-" + secondorder,
				  hidden:!functions.functionurl? false:true,
				  style:'margin-right:28px;',
				  html:'<div style="color:#1B4A98;font-size:14px;cursor:pointer;margin-top:6px;">'+weixin.setting.urlDescText+'</div>',
				  listeners:{
				  	  click:{
				  	  	  element:'el',
				  	  	  fn:function(){
				  	  	  	  var secondmenuurl = secondmenu.query('#funcurl-'+ firstorder + "-" + secondorder)[0];
				  	  	  	  var textPanel = secondmenu.query('#functextlink-'+ firstorder + "-" + secondorder)[0];
				  	  	  	  var imgPanel = secondmenu.query('#funcimglink-'+ firstorder + "-" + secondorder)[0];
				  	  	  	  var funcmenu = secondmenu.query('#funcmenu-'+ firstorder + "-" + secondorder)[0];
				  	  	  	  var serviceUrl = Ext.getCmp('app_url').getValue();
				  	  	  	  me.urlEnterpriseConfig(secondmenuurl,textPanel,imgPanel,'second',funcmenu,serviceUrl,paramInfo);
				  	  	  }
				  	  }
				  }
			  },{
				  xtype:'image',
				  src:'/system/sms/weixin/images/link.png',
				  width:32,
				  height:32,
				  style:'margin-right:52px;cursor:pointer',
				  itemId:'funcimglink-'+ firstorder + "-" + secondorder,
				  hidden:functions.functionurl? false:true,
				  listeners:{
				  	click:{
				  		element:'el',
				  		fn:function(){
				  			  var secondmenuurl = secondmenu.query('#funcurl-'+ firstorder + "-" + secondorder)[0];
				  	  	  	  var textPanel = secondmenu.query('#functextlink-'+ firstorder + "-" + secondorder)[0];
				  	  	  	  var imgPanel = secondmenu.query('#funcimglink-'+ firstorder + "-" + secondorder)[0];
				  	  	  	  var funcmenu = secondmenu.query('#funcmenu-'+ firstorder + "-" + secondorder)[0];
				  	  	  	  var serviceUrl = Ext.getCmp('app_url').getValue();
				  	  	  	  me.urlEnterpriseConfig(secondmenuurl,textPanel,imgPanel,'second',funcmenu,serviceUrl,paramInfo);
				  		}
				  	}
				  }
			  },{
				  // 删除二级菜单
				  xtype : "image",
				  src : "/system/sms/weixin/images/del.png",
				  width : 32,
				  height : 32,
				  style:'margin-right:20px;cursor:pointer;',
				  listeners : {
					  click : {// 删除二级菜单
						  element : 'el',
						  fn : function() {
							  Ext.Msg.confirm(weixin.setting.tip,weixin.setting.delBefore+functions.functionname+weixin.setting.delAfter,function(opt){
								  if(opt=='yes'){
									  secondmenu.destroy();
							  		  me.limitSecondMenu(secondMenu);
							   	  }
							  });
							 
						  }
					  }
				  }
			  }]
		  });
		secondMenu.insert(secondMenu.items.length-1, secondmenu);
	},
	/**
	 *  企业号应用配置链接地址
	 *  urlPanel  用来保存链接地址
	 *  textPanel 文字链接
	 *  imgPanel  图片链接
	 *  level 层级  first 一级菜单  second 二级菜单
	 *  funcMenuPanel 子菜单功能号  （子菜单功能使用）
	 *  serviceUrl 企业应用信任地址 （子菜单功能使用）
	 *  paramInfo 应用参数
	 */
	urlEnterpriseConfig:function(urlPanel,textPanel,imgPanel,level,funcMenuPanel,serviceUrl,paramInfo){
		var me = this;
		var corpid = Ext.getCmp('corpid').getValue();
		corpid = corpid? corpid:'';
		var domain = Ext.getCmp('w_selfservice_address').getValue();
		domain = domain? domain:paramInfo.url;
		var showurl = urlPanel.getValue().replace('${CORPID}',corpid);
		var store= Ext.create('Ext.data.Store',{
					fields:['name','menuid'],
					autoLoad: true,
					proxy:{
						type: 'ajax',
				        url: './weixinfuncs.json',
				        reader: {
				            type: 'json',
				            rootProperty: 'WXFunction'
				        }
					},
					listeners:{
						load:function(){
							store.add({name:weixin.setting.pleaseSelect,menuid:'-1'})
						}
					}
				});
		var win = Ext.create('Ext.window.Window',{
			title:weixin.setting.urlConfigTitle,
			width:500,
			modal:true,
			layout:{
				type:'vbox'
			},
			items:[{
				xtype:'combobox',
				itemId:'func',
				style:'margin-top:10px;font-size:14px;',
				labelWidth:54,
				fieldLabel:weixin.setting.secondMenuFunction,
				hidden:level=='first'? true:false,
				displayField: 'name',
    			valueField: 'menuid',
    			value:level == 'first'? '':funcMenuPanel.getValue()? funcMenuPanel.getValue():'-1',
    			emptyText:weixin.setting.pleaseSelect,
				store:store,
    			listeners:{
    				change:function(t,value){
    					var win_url = win.query('#url')[0];
    					var show_url = win.query('#showurl')[0];
    					if(!value || value == weixin.setting.pleaseSelect){
    						win_url.setValue('');
    						show_url.setValue('');
    						return;
    					}
    					if(level == 'first'){
    						win_url.setHidden(false);
    						show_url.setHidden(true);
    						return;
    					}
    					if(level != 'first' && (!value || value == '-1')){
    						win_url.setValue('');
    						win_url.setHidden(false);
    						show_url.setHidden(true);
    						return;
    					}
    					win_url.setHidden(true);
    					show_url.setHidden(false);
    					var menuurl = "https://open.weixin.qq.com/connect/oauth2/authorize?appid=${CORPID}&redirect_uri=";
    					if(value=='home'){
			  				var redirect_uri=encodeURIComponent(domain+"/w_selfservice/oauthservlet?dest="+domain+"/w_selfservice/module/selfservice/home.jsp");
			  				var state=getEncodeStr("menuid=home&etoken=ETOKEN");
			  				menuurl+=redirect_uri+"&response_type=code&scope=snsapi_base&state="+state+"#wechat_redirect";
			  			}else{
				  			var redirect_uri=encodeURIComponent(domain+"/w_selfservice/oauthservlet?dest="+domain+"/w_selfservice/module/selfservice/index.jsp");
				  			var state=getEncodeStr("menuid="+value+"&etoken=ETOKEN");
			  				menuurl+=redirect_uri+"&response_type=code&scope=snsapi_base&state="+state+"#wechat_redirect";
			  			}
			  			win_url.setValue(menuurl);
			  			menuurl = menuurl.replace('${CORPID}',corpid);
			  			show_url.setHtml(menuurl);
    				}
    			}
			},{
				xtype:'textareafield',
				width:490,
				height:120,
				itemId:'url',
				hidden:true,
				emptyText:level=='first'? weixin.setting.inputAccessAddress:weixin.setting.inputAndSelectAddress,
				style:'margin-top:10px;margin-bottom:10px;font-size:14px;',
				value:showurl
			},{
				xtype:'panel',
				width:490,
				height:120,
				hidden:true,
				style:'font-size:14px;margin-top:10px;margin-bottom:10px;word-break:break-all;',
				itemId:'showurl',
				html:showurl
			}],
			buttonAlign : 'center',
			buttons : [{
				xtype:'button',
				width:60,
				height:30,
				html:'<font style="font-size:14px">'+weixin.setting.ok+'</font>',
				listeners:{
					click:function(){
						var value = win.query('#url')[0].getValue();
						var funcValue = win.query('#func')[0].getValue();
						if(level != 'first'){
							funcMenuPanel.setValue(funcValue);
						}
						urlPanel.setValue(value);
						if(!value || value.length==''){
							textPanel.setHidden(false);
							imgPanel.setHidden(true);							
						}
						else{
							textPanel.setHidden(true);
							imgPanel.setHidden(false);
						}
						win.close();
					}
				}
			},{
				xtype:'component',
				width:10
			},{
				xtype:'button',
				width:60,
				height:30,
				html:'<font style="font-size:14px">'+weixin.setting.cancel+'</font>',
				listeners:{
					click:function(){
						win.close();
					}
				}
			}]
		});
		if(level != 'first' && funcMenuPanel.getValue() != ''){
			win.query('#url')[0].setHidden(true);
    		win.query('#showurl')[0].setHidden(false);
		}else if(level != 'first' && funcMenuPanel.getValue() == ''){
			win.query('#url')[0].setHidden(false);
    		win.query('#showurl')[0].setHidden(true);
		}
		win.show();
	},
	/**
	 * 自定义菜单 保存&发布功能
	 * 
	 */
	menusEnterpriseData:function(funcParamPanel,itemid){
		  // 解析并封装菜单数据到后台保存
		  var me = this;
		  var values = funcParamPanel.getValues();
		  var params = new Array();
		  var allMenuMap = new HashMap();
		  for (var key in values) {
			  var temps = key.split('-');// 所有数据的键
			  var name = temps[0];// 开头的值,[firstname,firsturl/*firsttype*/,secondname,secondfuncmenu,secondfuncurl/*secondtype*/]只能从这几个里面选一个
			  if ("firstname" == name) {// 一级菜单只有两位
				  var menuMap;
				  if (allMenuMap.get(key)) {
					  menuMap = allMenuMap.get(key);
				  } else {
					  menuMap = new HashMap();
				  }
				  var nameValue = values[key];
				  var firstorder = temps[1];
				  var urlkey = 'firsturl-' + firstorder;
				  var firsturl = values[urlkey];
				  menuMap.put('menuname', nameValue);
				  menuMap.put('menuurl', firsturl);
				  menuMap.put('firstorder', firstorder);
				  allMenuMap.put(key, menuMap);
			  } else if ("secondname" == name) {
				  var nameValue = values[key];
				  var firstorder = temps[1];
				  var secondorder = temps[2];
				  var firstname = "firstname-" + firstorder;
				  var funcmenukey = "secondfuncmenu-" + firstorder + "-" + secondorder;
				  var funcmenu = values[funcmenukey];
				  var funcurlkey = "secondfuncurl-" + firstorder + "-" + secondorder;
				  var funcurl = values[funcurlkey];
				  var functionMap = new HashMap();
				  functionMap.put("functionname", nameValue);
				  functionMap.put("functionmenu", funcmenu);
				  functionMap.put("functionurl", funcurl);
				  functionMap.put("secondorder", secondorder);
				  var menuMap = allMenuMap.get(firstname);
				  if (!menuMap) {
					  menuMap = new HashMap();
				  }
				  var functions = menuMap.get("functions");
				  if (!functions) {
					  functions = new Array();// 指定5个
				  }
				  functions.push(functionMap);
				  menuMap.put("functions", functions);
				  allMenuMap.put(firstname, menuMap);
			  }
		  }

		  var menuArray = new Array();
		  for (var key in allMenuMap) {
			  if (allMenuMap[key]) {
				  menuArray.push(allMenuMap[key]);
			  }
		  }
		  var vo = new HashMap();
		  var map = new HashMap();
		  map.put("servertype", "enterprise");
		  map.put("params", menuArray);
		  vo.put('menuData', map);
		  vo.put("serverid", itemid);
		  return vo;
	},
	codeClick:function(eleId){
		setEleConnect(eleId);
	}
});