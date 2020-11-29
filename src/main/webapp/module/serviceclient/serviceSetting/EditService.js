Ext.define('ServiceClient.serviceSetting.EditService', {
	requires:["EHR.templateSelector.TemplateSelector","EHR.fielditemselector.FieldItemSelector"],
	groupId:'',
	handler:Ext.emptyFn,
	scope:undefined,
	extend:'Ext.window.Window',
	info:'',
	inputItemArray:[],//保存配置录入项
	templateType:undefined,
	templateId:undefined,
	id:'editServiceWin',
    layout:'fit',
    width:650,
    height:425,
    modal : true,//遮罩
    resizable:false,//禁止拉伸
    closable:true,//允许关闭按钮
    title:this.selectType==1?sc.setting.createservice:sc.setting.edit,
	constructor:function(config){
		this.callParent();
		var me = this;
		EditServiceScope = this;
		Ext.apply(me,config);
		this.init();
	},
	init:function(){
		var me = this;
		if(me.inputItemArray.length>0){//清空数组元素
    		Ext.Array.erase(me.inputItemArray,0,me.inputItemArray.length);
		}
		var form = this.getMainPanel();
		this.add(form);
		var configInputPanel = form.query('#configInput')[0];//配置项标签页
		if(me.templateType==1){
            configInputPanel.close();
		}
		if(this.selectType==2){
			this.echoForm(form);
			//回显服务须知勾选框状态
            var useServiceCheckValue = Ext.getCmp('useService');
            if(me.info.notice_enable=="1"){
                useServiceCheckValue.setValue(true);
            }
            configInputPanel = form.query('#configInput')[0];
            //给gridpanel添加行数据
            if(configInputPanel){
                var addType = "echo";
                me.addConfigInputData(me.info.configInputsList,addType);
            }
            //配置录入项勾选框状态
            var useConfigInputCheckValue = Ext.getCmp('useConfigInput');
            if(me.info.config_input_enable=="1"){
                useConfigInputCheckValue.setValue(true);
            }
		}
		this.show();
		Ext.getCmp('editServiceWin_header').setStyle({
			borderStyle:'hidden hidden solid hidden'
		});
		Ext.getCmp('name').focus();
	},
	/**
	 * 页面编辑时回显数据
	 */
	echoForm:function(form){
		var me = this;
		var map = new HashMap();
		map.put("saveType",'enformService');
		map.put('serviceId',me.serviceId);
		map.put("templateType",me.templateType);
		Rpc({functionId:'SC000000002',async:false,success:function(data){
			me.info = Ext.decode(data.responseText).infos;
    		var store =Ext.StoreMgr.get('typeStore');//获取store
    		form.query('#serviceTemplatePage')[0].getForm().setValues(me.info);
    		if(me.info.type==1){
    			Ext.getCmp('type').setValue(store.data.items[0]);
    			Ext.getCmp('templateId').setValue(me.info.viewName);
    			me.templateId = me.info.templateId;
    		}else{
    			Ext.getCmp('type').setValue(store.data.items[1]);
    			Ext.getCmp('print_panel').setDisabled(true);
    			Ext.getCmp('print_panel').setVisible(false);
    		}
		},scope:this},map);
	},
	getMainPanel:function(){
		var me = this;
		var formPanel = Ext.create("Ext.tab.Panel",{
			id:'mainPanel',
			defaults:{xtype:'form'},
            items:[{
                title:sc.setting.defineServiceTemplate,//定义服务模板
                itemId:'defineServiceTemplate',
                showTime:1,
                items:me.createServiceTemplatePage()
            },{
                title:sc.setting.serviceGuidelines,//服务须知
                itemId:'serviceGuidelines',
                items:me.createCkEdit(),
                buttonAlign:'center',
                buttons:[{
                    text:sc.setting.previousStep,//上一步
                    itemId:'GuidelinesPreviousStep',
                    handler:function(btn){
                        var mainPanel = Ext.getCmp('mainPanel');//tabPanel
                        var defineServiceTemplatePanel = mainPanel.query('#defineServiceTemplate')[0];//服务模板页签
                        mainPanel.setActiveTab(defineServiceTemplatePanel);
                    }
                },{
                    text:sc.setting.nextStep,//下一步
                    itemId:'secondNextStep',
                    handler:function(btn){
                        var mainPanel = Ext.getCmp('mainPanel');//tabPanel
                        var configInputPanel = mainPanel.query('#configInput')[0];//配置录入项页签
                        mainPanel.setActiveTab(configInputPanel);
                    }
                },{
                    text:me.selectType==1?sc.setting.ok:sc.setting.save,//确定Or保存
                    itemId:'GuidelinesConfirm',
                    handler:function(btn){
                    	me.saveServiceData(btn);
                    }
                },{
                    text:sc.setting.cancel,//取消
                    itemId:'GuidelinesCancel',
                    handler:function(){
                        Ext.getCmp('editServiceWin').close();
                    }
                }]
            },me.createConfigInputPanel()],//配置录入项页签
            listeners:{
                afterRender:function(){
                	if(Ext.getCmp('type').getValue()==2){
                	    var configInputPanel = formPanel.query('#configInput')[0];//配置项标签页
                        configInputPanel.close();
                	}
                	var ckedit = Ext.getCmp('ckeditorid');
                    var defineServiceTemplatePanel = formPanel.query('#defineServiceTemplate')[0];//服务模板页签
                    if(me.selectType==2){
                    	//回显服务须知内容
                        var serviceGuidelinesPanel = formPanel.query('#serviceGuidelines')[0];//服务须知页签
                    	formPanel.setActiveTab(serviceGuidelinesPanel);//首次加载触发页签切换功能,让ckedit能进行赋值
                    	formPanel.setActiveTab(defineServiceTemplatePanel);//切换回第一个页签
                        if(me.templateType==2){
                            Ext.getCmp('box3').setDisabled(true);
                            Ext.getCmp('box3').setVisible(false);
                        }
                    }
                    ckedit.showType='afterRender';//已渲染ckedit
                    defineServiceTemplatePanel.showTime = 2;
                },
                tabchange:function(tabPanel, tab){
                    var defineServiceTemplatePanel = formPanel.query('#defineServiceTemplate')[0];//服务模板页签
                    var serviceGuidelinesPanel = formPanel.query('#serviceGuidelines')[0];//服务须知页签
                    var configInputPanel = formPanel.query('#configInput')[0];//配置录入项panel
                    if(defineServiceTemplatePanel.showTime==1){
                        var ckedit = Ext.getCmp('ckeditorid');
                        if(me.info.description){
                            ckedit.setValue(me.info.description);
                        }
                        return;
                    }
                    
                	if(!defineServiceTemplatePanel.isValid()){
                        formPanel.setActiveTab(defineServiceTemplatePanel);
                        Ext.MessageBox.alert(sc.setting.promptmessage,sc.setting.completeCurrentPage);//提示信息  请先完成当前页的配置
                	    return;
                	}
                	if(formPanel.activeTab==serviceGuidelinesPanel||formPanel.activeTab==configInputPanel){
                        var nameValue = Ext.getCmp('name').value;
                        var nameLength = Ext.util.Format.trim(nameValue).length;//字符串去头尾空格后的长度
                        var iconBox = formPanel.query('#iconBox')[0];
                        if(nameLength==0){
                            formPanel.setActiveTab(defineServiceTemplatePanel);
                            Ext.MessageBox.alert(sc.setting.promptmessage,sc.setting.writename);//提示信息  请填写服务名称
                            return;
                        }else if(iconBox.query('container')[0] == undefined){
                            formPanel.setActiveTab(defineServiceTemplatePanel);
                            Ext.MessageBox.alert(sc.setting.promptmessage,sc.setting.mustselecticon);//提示信息   请选择服务图标！
                            return;
                        }
                	}
                	var itemId = tab.getItemId();//活动页签的itemid
                	var editServiceWin = Ext.getCmp('editServiceWin');//编辑服务window
                    if(configInputPanel){
                        if(itemId=="serviceGuidelines"){
                    		//解决IE无法给editor赋值的问题,只能重新给ckedit.setValue()
                    		var ckedit = Ext.getCmp('ckeditorid');
                    		var nowValue = ckedit.getHtml();
                    		ckedit.editor.focus();
                    		ckedit.setValue(nowValue);
                        	editServiceWin.query("#GuidelinesConfirm")[0].hide();
                        	editServiceWin.query("#GuidelinesCancel")[0].hide();
                            editServiceWin.query("#secondNextStep")[0].show();
                        }else{
                            var returnFalg = me.validateCKEditorBlank();
                            if(returnFalg){
                            	var guidelinesCheckbox = Ext.getCmp('useService');//启用服务须知checkbox
                            	guidelinesCheckbox.setValue(false);
                            }
                        }
                    }else{
                        if(itemId=="serviceGuidelines"){
                        	//解决IE无法给editor赋值的问题,只能重新给ckedit.setValue()
                        	var ckedit = Ext.getCmp('ckeditorid');
                            var nowValue = ckedit.getHtml();
                    		ckedit.editor.focus();
                            ckedit.setValue(nowValue);
                            editServiceWin.query("#secondNextStep")[0].hide();
                        	editServiceWin.query("#GuidelinesConfirm")[0].show();
                        	editServiceWin.query("#GuidelinesCancel")[0].show();
                        }else{
                            var returnFalg = me.validateCKEditorBlank();
                            if(returnFalg){
                            	var guidelinesCheckbox = Ext.getCmp('useService');//启用服务须知checkbox
                            	guidelinesCheckbox.setValue(false);
                            }
                        }
                    }
                }
            }
		});
		return formPanel;
	},
	/**
	 * 创建录入配置项panel
	 */
	createConfigInputPanel:function(){
		var me = this;
        var configInputPanel = Ext.create("Ext.form.Panel",{
            title:sc.setting.configInput,//配置录入项
            itemId:'configInput',
            items:[me.createConfigInputGrid(),{
                xtype:'checkbox',
                margin:'0 0 0 5',
                id:'useConfigInput',
                boxLabel : sc.setting.enableConfigInput,//启用配置录入项
                name : 'useConfigInputNotice',
                inputValue:'1',
                checked : false,
                listeners:{
                    change:function(checkbox){
                    	var useConfigInputFlag = checkbox.getValue();
        	            var ConfigInputTableStore = Ext.getCmp('configInputGrid').getStore().data;
                    	if(useConfigInputFlag&&ConfigInputTableStore.length==0){
                            Ext.Msg.alert(sc.setting.promptmessage,sc.setting.mustAddInputItem);//提示信息   启用前请先添加录入项
                            checkbox.setValue(false);
                		}
                    }
                }
            }],
            buttonAlign:'center',
            buttons:[{
                text:sc.setting.previousStep,//上一步
                itemId:'configInputPreviousStep',
                handler:function(btn){
                    var mainPanel = Ext.getCmp('mainPanel');//tabPanel
                    var serviceGuidelinesPanel = mainPanel.query('#serviceGuidelines')[0];//服务须知页签
                    mainPanel.setActiveTab(serviceGuidelinesPanel);
                }
            },{
                text:me.selectType==1?sc.setting.ok:sc.setting.save,//确定Or保存
                itemId:'configInputConfirm',
                handler:function(btn){
                	me.saveServiceData(btn);
                }
            },{
                text:sc.setting.cancel,//取消
                itemId:'configInputCancel',
                handler:function(){
                    Ext.getCmp('editServiceWin').close();
                }
            }]
        });
        return configInputPanel;
	},
	createConfigInputGrid:function(){
		var me = this;
		Ext.define("inputItemModel",{//定义数据模型
            extend:'Ext.data.Model',
            fields:["subsetName","itemName","readOrWrite"]
        });
        var tbar = [];//顶部按钮
        tbar.push({xtype:'button',text:sc.setting.addConfigInput,itemId:'addConfigInputBtn',margin:'0 10 0 0',handler:me.addConfigInput,scope:me});//添加
        tbar.push({xtype:'button',text:sc.setting.deleteConfigInput,handler:me.deleteConfigInput,scope:me});//删除
	    
        var configStore = new Ext.data.ArrayStore({
            storeId:'readOrWriteStore',
            fields:['id','displayText'],
            data:[[sc.setting.read,sc.setting.read],[sc.setting.write,sc.setting.write]]//读  写
        });
        var readOrWriteCombo = Ext.widget("combobox",{
            id:'readOrWriteType',
            store:configStore,
            valueField:'id',
            displayField:'displayText',
            editable:false,
            listeners:{
                afterRender:function(){
                    readOrWriteCombo.setValue(sc.setting.read);
                },
                select:function(){
                    var configInputGrid = Ext.getCmp('configInputGrid');//配置项表格
                    configInputGrid.getSelectionModel().deselectAll();//清空getSelectionModel中已选的数据，防止保存时records数据顺序混乱
                }
            }
        });
	    var configInputGridPanel = Ext.widget("gridpanel",{
	    	id:'configInputGrid',
	    	margin:'3 0 0 5',
	    	height:290,
	    	width:628,
            stripeRows:true,//表格是否隔行换色
            columnLines:true,
            enableColumnResize:false,//禁止改变列宽
            enableColumnMove:false,//禁止拖放列
            selModel: {
                injectCheckbox: 0,//复选框在那一索引列  默认第一列
                mode: "MULTI",    //"SINGLE"单选/"MULTI"多选
                checkOnly: false  //只能通过checkbox选择
            },
            selType: "checkboxmodel",//复选框实现多选
            tbar:tbar,
            store:{
               storeId:"planStore",
               model:'inputItemModel'
            },
            columns:[{ text: sc.setting.subsetName, sortable:false,dataIndex: 'subsetName',menuDisabled:true,flex:4},//menuDisabled:true, 禁止列右侧菜单
                    { text: sc.setting.itemName, sortable:false,dataIndex: 'itemName',menuDisabled:true,flex:4},
                    { text: sc.setting.readOrWrite, sortable:false,dataIndex: 'readOrWrite',menuDisabled:true,flex:2,editor:readOrWriteCombo}],
            plugins:{
                ptype:'cellediting',
                clicksToEdit: 1//单击编辑
            },
            viewConfig: {
                markDirty:false,
                plugins: {//拖拽
                    ptype: 'gridviewdragdrop',
                    dragText: sc.setting.adjustTheOrder//调整顺序
                },
                listeners:{
                    drop:function(node,dragData,overModel,position){
                        me.dropRestructData(dragData,overModel,position);
                    }
                }
            }
        });
        //对于编辑后的单元格，会在左上角出现一个红色的标识，说明该数据是编辑过的，要想去掉这个红色箭头，需要调用record的commit()方法。
        configInputGridPanel.on('edit', function (editor, e) {
            e.record.commit();
        });
        return configInputGridPanel;
	},
	/**
	 * 拖拽排序gridpanel中数据顺序的重塑
	 * param dragData
	 * param overModel
	 * param position 上移还是下移标识
	 */
	dropRestructData:function(dragData,overModel,position){
		var me = this;
	    var tempArray = [];//记录中转
	    var configInputGridPanel = Ext.getCmp("configInputGrid");
        var ConfigInputTableStore = configInputGridPanel.getStore();
        var records = dragData.records;//被拖动的数据
        for(var i=0;i<records.length;i++){
            var recordItemName = records[i].data.itemName;
            for(var j=0;j<me.inputItemArray.length;j++){
                var inputItemId = me.inputItemArray[j].itemId;
                var index = recordItemName.indexOf(inputItemId);
                if(index>-1){
                    tempArray.push(me.inputItemArray[j]);
                    Ext.Array.remove(me.inputItemArray,me.inputItemArray[j]);
                }
            }
        }
        ConfigInputTableStore.remove(records);
        var overItemName = overModel.data.itemName;//被超越的记录的指标名称
        var inputItems = ConfigInputTableStore.data.items;//配置项
        for(var i=0;i<inputItems.length;i++){
            var itemName = inputItems[i].data.itemName;
            if(overItemName==itemName){
                for(var j=0;j<records.length;j++){
                	var insertIndex = i;//数据重新装入的位置
                	if(position=="after"){
                	    insertIndex = i+1;
                	}
                    ConfigInputTableStore.insert(insertIndex,records[j]);//拖动数据的重新装入
                    me.inputItemArray.splice(insertIndex,0,tempArray[j]);
                    i=i+1;
                }
                break;
            }
        }
	},
	/**
	 * 配置及修改后，保存服务数据
	 */
	saveServiceData:function(btn){
		var me = this;
        var formPanel = Ext.getCmp('mainPanel');//tabpanel
        var iconBox = formPanel.query('#iconBox')[0];
        //第一个页签
        var defineServiceTemplatePanel = formPanel.query('#defineServiceTemplate')[0];
        //第一个页签内的formpanel
        var serviceTemplatePagePanel = defineServiceTemplatePanel.query('#serviceTemplatePage')[0];
        var value =serviceTemplatePagePanel.getValues();
        
        var description = Ext.getCmp('ckeditorid').getHtml();//获取服务须知html文本内容
        var iconBoxContainer = iconBox.query('container')[0];
        var iconSrc = iconBoxContainer.query('image')[0].src;
        var iconName = iconSrc.substring(sc.setting.iconurl.length);//服务图标
        
        var map = new HashMap();
        //是否启用服务须知 1:启用 0:未启用
        var guidelinesCheck = "0";
        var guidelinesCheckbox = Ext.getCmp('useService');//启用服务须知checkbox
        var guidelinesCheckFlag = guidelinesCheckbox.getValue();
        if(guidelinesCheckFlag){
            guidelinesCheck = "1";
        }
        //是否启用配置录入项 1:启用 0:未启用 2:没有配置录入项页签
        var configInputCheck = "0";
        var configInputCheckBox = Ext.getCmp('useConfigInput');//启用配置录入项checkbox
        if(configInputCheckBox){
            var configInputCheckFlag = configInputCheckBox.getValue();
            if(configInputCheckFlag){
                var configInputCheck = "1";
            }
            var configInputGrid = Ext.getCmp('configInputGrid');//配置项表格
            //清空getSelectionModel中已选的数据，防止保存时records数据顺序混乱
            configInputGrid.getSelectionModel().deselectAll();
            configInputGrid.getSelectionModel().selectAll();//全选
            var records = configInputGrid.getSelectionModel().getSelection();
            for(var i=0;i<records.length;i++){
                var readOrWriteFlag = records[i].data.readOrWrite;
                if(readOrWriteFlag==sc.setting.read){
                    readOrWriteFlag = "0";
                }else{
                    readOrWriteFlag = "1";
                }
                me.inputItemArray[i].put("isWrite",readOrWriteFlag);
            }
        }else{
            configInputCheck = "2"
        }
        if(me.selectType==2){
            map.put('serviceId',me.serviceId);
        }
        map.put('inputItemArray',me.inputItemArray);//配置录入项
        map.put('guidelinesCheck',guidelinesCheck);//服务须知是否勾选
        map.put('configInputCheck',configInputCheck);//配置录入项是否勾选
        map.put('groupId',me.groupId);
        map.put('name',value['name-inputEl']);
        map.put('type',value['type-inputEl']);//打印、其他
        map.put('icon',iconName);
        map.put('effectiveDate',value['effectiveDate-inputEl']);
        map.put('url',value['url-inputEl']);
        map.put('printPrice',value['printPrice-inputEl']);
        map.put('description',description);
        map.put('freePrintCount',value['freePrintCount-inputEl']);
        map.put('templateType',me.templateType);//登记表、业务模版
        map.put('templateId',me.templateId);//templateType 为业务模版时,模版id
        btn.up('window').close();
        if(me.selectType==1){
            Ext.callback(me.handler,me.scope,[map,me.ownerGroupBoxId]);
        }else{
        	Ext.callback(me.handler,me.scope,[map,me.serviceCmp]);
        }
	},
    /**
     * 创建url文本框
     */
    createUrlTextfield:function(){
        var urlTextfield = Ext.widget("textfield",{
            id:'url',
            beforeLabelTextTpl:"<font color='red'> * </font>",
            fieldLabel: "<span>"+sc.setting.serviceurl+"</span>",//服务地址
            emptyText:sc.setting.enterserviceurl,//请输入服务链接地址。。。
            labelAlign:'right',
            allowBlank: false,
            width:'90%',
            height:22,
            margin:'10 0 20 0'
        });
        return urlTextfield;
    },
    /**
     * 定义服务模板页panel
     */
    createServiceTemplatePage:function(){
    	var me = this;
    	var printPanel = me.createPrintConfigPanel();
    	var store = new Ext.data.ArrayStore({
            storeId:'typeStore',
            fields:['id','displayText'],
            data:[[1,sc.setting.print],[2,sc.setting.other]]//1:打印  2:其他
        });
        var urlTextfield = me.createUrlTextfield();
    	var templatePanel = Ext.create("Ext.form.Panel",{
    		itemId:'serviceTemplatePage',
    		border:false,
    		height:355,
            items:[{
                xtype:'combo',
                id:'type',
                fieldLabel: "<span>"+sc.setting.servicetype+"</span>",//服务类别
                fieldStyle:this.selectType==1?'background-color:#ffffff':'background-color:#d9d9d9',//输入框样式
                beforeLabelTextTpl:"<font color='red'> * </font>",
                labelAlign:'right',
                store:store,
                width:'90%',
                valueField:'id',
                displayField:'displayText',
                margin:'13 0 15 0',
                editable:false,
                height:22,
                allowBlank: false,
                readOnly:this.selectType==1?false:true,
                listeners:{
                    afterRender:function(combo){
                        if(me.selectType==1){//新增
                            var value = store.data.items[0];
                            combo.setValue(value);
                        }
                        var value = Ext.getCmp('type').getValue();
                        if(value==2){
                        	templatePanel.add(urlTextfield);
                        	urlTextfield.setValue(me.info.url);
                        }
                    },
                    select:function(){
                    	var configInputPanel = Ext.getCmp('mainPanel').query('#configInput')[0];
                        var value = Ext.getCmp('type').getValue();
                        if(value==2){//其他服务
                            if(configInputPanel){
                                Ext.getCmp('mainPanel').query('#configInput')[0].close();
                            }
                            templatePanel.remove(Ext.getCmp("print_panel"));//移除打印服务特有配置
                            var urlTextfield = me.createUrlTextfield();//url文本框
                            templatePanel.add(urlTextfield);
                        }else{
                            if(!configInputPanel){
                                var configInputPanel = me.createConfigInputPanel();
                                Ext.getCmp('mainPanel').add(configInputPanel);
                            }
                            Ext.getCmp('url').setValue("1");
                            templatePanel.remove(Ext.getCmp('url'));//移除url文本框
                            var printPanel = me.createPrintConfigPanel();
                            templatePanel.add(printPanel);//添加打印特有配置
                        }
                    }
                }
            },{
                xtype:'textfield',
                id:'name',
                height:22,
                width:'90%',
                allowBlank:false,
                beforeLabelTextTpl:"<font color='red'> * </font>",
                fieldLabel:"<span>"+sc.setting.servicename+"</span>",//服务名称
                labelAlign:'right',
                margin:'0 0 15 0',
                listeners:{
                    focusleave:function(){
                        var charnum = 0;//字节数
                        var varlength = 0;//字符长度
                        var formPanel = Ext.getCmp('mainPanel');
                        for (var i = 0; i < this.value.length; i++) {
                            var a = this.value.charAt(i);
                            if (a.match(/[^\x00-\xff]/ig) != null) {//如果是汉字
                                charnum = charnum+2;//一个汉字占两个字节
                                varlength = varlength+1;
                            }else {
                                charnum =charnum+1;//字母数字等占一个字节
                                varlength = varlength+1;
                            }
                            if(charnum==51||charnum==52){
                                Ext.MessageBox.alert(sc.setting.promptmessage,sc.setting.overlength);//提示信息   请输入50个以内的字节（一个汉字占两个字节）
                                this.setValue(this.value.substring(0,varlength-1));
                            }
                        }
                        var iconBoxArray = formPanel.query('#iconBox')[0].query('container');
                        if(iconBoxArray.length!=0){
                            iconBoxArray[0].query('panel')[0].setHtml("<div style='word-break:break-all;color:white;font-size:13px;height:34px;'>"+this.value+"</div>");
                        }
                     }
                  }
              },{
                  xtype:'panel',
                  layout:'hbox',
                  border:false,
                  width:'100%',
                  height:76,
                  items:[{
                      xtype : 'component',
                      id  : 'icon',
                      html: "<div><span style=color:red;>* </span>"+sc.setting.serviceicon+"</div>",//服务图标
                      margin:'5 0 0 43'
                  },{
                      xtype:'panel',
                      border:false,
                      itemId:'selecticon',
                      hidden:this.selectType==1?false:true,
                      layout:'hbox',
                      width:70,
                      height:30,
                      items:[{
                          xtype : 'component',
                          html: sc.setting.selecticon,//选择图标
                          style:"cursor:pointer;color:#0099cc",
                          margin:5
                      }],
                      listeners:{
                          element:'el',
                          click:function(){
                              var formPanel = Ext.getCmp('mainPanel');
                              me.listener(formPanel);
                          }
                      }
                  },{
                      xtype:'container',
                      hidden:true,
                      width:145,
                      height:75,
                      itemId:'iconBox',
                      margin:'0 5 0 5',
                      listeners:{
                          render:function(){
                              if(me.selectType==2){
                                  var imageContainer = Ext.create('Ext.panel.Panel',{
                                      border:false,
                                      items:[{
                                          xtype:'image',
                                          itemId:me.info.sicon,
                                          width:145,
                                          height:75,
                                          src:sc.setting.iconurl+me.info.sicon
                                      },{
                                          xtype:'panel',
                                          border:false,
                                          html:"<div style='word-break:break-all;color:white;font-size:13px;height:37px;'>"+me.info.name+"</div>",
                                          bodyStyle:'background:transparent;',
                                          style:'width:80px;position:absolute;right:2px;top:22px;'
                                      }]
                                  }) 
                                  this.add(imageContainer);
                                  this.show();
                              }
                          }
                      }
                  },{
                      xtype:'panel',
                      itemId:'changeicon',
                      border:false,   
                      hidden:this.selectType==1?true:false,
                      layout:'hbox',
                      width:70,
                      height:30,
                      margin:'0 0 10 5',
                      items:[{
                          xtype : 'component',
                          html: sc.setting.changeicon,//更换图标
                          style:"cursor:pointer;color:#0099cc",
                          margin:5,
                          listeners:{
                              element:'el',
                              click:function(){
                                  var formPanel = Ext.getCmp('mainPanel');
                                  me.listener(formPanel);
                              }
                          }
                      }]
                  }]
              },printPanel],
              buttonAlign:'center',
              buttons:[{
                text:sc.setting.nextStep,//下一步
                formBind:true,
                itemId:'firstNextStep',
                handler:function(btn){
                	var formPanel = Ext.getCmp('mainPanel');
                	var nameValue = Ext.getCmp('name').value;
                    var nameLength = Ext.util.Format.trim(nameValue).length;//字符串去头尾空格后的长度
                    var iconBox = formPanel.query('#iconBox')[0];
                    if(nameLength==0){
                        Ext.MessageBox.alert(sc.setting.promptmessage,sc.setting.writename);//提示信息  请填写服务名称
                        return;
                    }else if(iconBox.query('container')[0] == undefined){
                        Ext.MessageBox.alert(sc.setting.promptmessage,sc.setting.mustselecticon);//提示信息   请选择服务图标！
                        return;
                    }
                	
                    var mainPanel = Ext.getCmp('mainPanel');
                    var editServiceWin = Ext.getCmp('editServiceWin');
                    var serviceGuidelinesPanel = mainPanel.query('#serviceGuidelines')[0];
                    var configInputPanel = mainPanel.query('#configInput')[0];//配置录入项页签
                    
                    if(configInputPanel){
                        editServiceWin.query('#GuidelinesConfirm')[0].hide();
                        editServiceWin.query('#GuidelinesCancel')[0].hide();
                        editServiceWin.query('#secondNextStep')[0].show();
                    }else{
                        editServiceWin.query('#GuidelinesConfirm')[0].show();
                        editServiceWin.query('#GuidelinesCancel')[0].show();
                        editServiceWin.query('#secondNextStep')[0].hide();
                    }
                    mainPanel.setActiveTab(serviceGuidelinesPanel);
                }
            }]
    	});
    	return templatePanel;
    },
    /**
     * 服务须知编辑器
     * @return {}
     */
    createCkEdit : function() {
        var me = this;
        var config = {};
        config.height = 58;
        config.language = "zh-cn";
        config.resize_enabled = false;// 是否使用“拖动以改变大小”功能
        config.removePlugins = "elementspath";// 去掉底部路径显示栏
        config.image_previewText = ' ';// 图片预览区域显示内容
        config.baseFloatZIndex = 19900;// 保证弹出菜单在最上层，不会被Ext菜单遮住
        config.toolbar = [
               {name : 'document',items : ['Source']}, 
               {name : 'styles',items : ['Font', 'FontSize']}, 
               {name : 'basicstyles',items : ['Bold', 'Italic','Underline']}, 
               {name : 'paragraph',items : ['JustifyLeft', 'JustifyCenter','JustifyRight', 'JustifyBlock']}, 
               {name : 'colors',items : ['TextColor', 'BGColor']},
               {name : 'tools',items : ['Maximize']}
        ];
        var CKEditor = Ext.create("EHR.ckEditor.CKEditor", {
            id : 'ckeditorid',
            width : '100%',
            height: 288,
            margin:'5 5 0 5',
            style:{
                border:'1px solid #B5B8C8'
            },
            ckEditorConfig : config
        });
        var ckeditAndUseRidoContainer = Ext.create('Ext.Container',{
            layout:{
                type:'vbox'
            }
        });
        ckeditAndUseRidoContainer.add(CKEditor);
        var useRido = Ext.widget('checkbox', {
                margin:'0 0 0 5',
                id:'useService',
                boxLabel : sc.setting.serviceEnablementGuideline,//启用服务须知
                name : 'useServiceNotice',
                inputValue:'1',
                checked : false,
                listeners:{
                    change:function(guidelinesCheckbox){
                    	var returnFlag = me.validateCKEditorBlank();
                    	if(returnFlag){
                    	    Ext.Msg.alert(sc.setting.promptmessage,sc.setting.mustEditGuidelines);//提示信息   启用前请先编辑内容
                    	    guidelinesCheckbox.setValue(false);
                    	}
                    }
                }
            });
        ckeditAndUseRidoContainer.add(useRido);
        return ckeditAndUseRidoContainer;
    },
    /**
     * 校验服务须知内容是否为空
     */
	validateCKEditorBlank:function(){
		var returnFlag = false;
        var CKEditor = Ext.getCmp('ckeditorid');//编辑器
	    var guidelinesCheckbox = Ext.getCmp('useService');//启用服务须知checkbox
        var checkFlag = guidelinesCheckbox.getValue();//true:启用服务须知
        
        if(CKEditor.showType=='afterRender'&&checkFlag){
                var CKEditorText = CKEditor.getHtml();//编辑器读的空格  &nbsp;
                var textLength = Ext.util.Format.trim(CKEditorText).length;
                var textChar = CKEditorText.split("&nbsp;");
                var blankFlag = true;
                for(var index in textChar){
                    var charLength = Ext.util.Format.trim(textChar[index]).length;
                    if(charLength>0){
                        blankFlag = false;
                        break;
                    }
                }
                if(checkFlag&&textLength==0 || blankFlag){
                    returnFlag = true;
                    return returnFlag;
                }else{
                    return returnFlag;
                }
        }else{
            return returnFlag;
        }
    },
    /**
     * 打印特有配置
     */
    createPrintConfigPanel:function(){
        var printPanel = new Ext.panel.Panel({
            id:'print_panel',
            border:false,
            items:[{
                      layout:'hbox',
                      border:false,
                      id:'box',
                      margin:'15 0 15 0',
                      items: [{
                        xtype : 'textfield',
                        id  : 'templateId',
                        beforeLabelTextTpl:"<font color='red'> * </font>",
                        fieldLabel: "<span>"+sc.setting.servicetemplate+"</span>",//服务模板
                        readOnly:true,
                        labelAlign:'right',
                        width:'80%',
                        height:22,
                        allowBlank: false,
                        fieldStyle:'background-color:#d9d9d9'//输入框样式
                    },{
                        xtype:'button',
                        width:'10%',
                        text: sc.setting.select,
                            id:'select_template',
                            margin:'0 0 0 20',
                            disabled:this.selectType==1?false:true,
                            menu:{
                                items: [{
                                  text: sc.setting.businessform,
                                  templateType:1,
                                  handler:this.selectTemplate,
                                  scope:this
                                },{
                                  text: sc.setting.registryform,
                                  templateType:2,
                                  handler:this.selectTemplate,
                                  scope:this
                                }]
                            }
                    }]
                  },{
                       layout:"hbox",
                       id:"box1",
                       border:false,
                       items:[{
                            xtype : 'numberfield',
                            id:'freePrintCount',
                            beforeLabelTextTpl:"<font color='red'> * </font>",
                            fieldLabel: "<span>"+sc.setting.freeprint+"</span>",//免费打印
                            maxValue:100,
                            minValue:1,
                            allowDecimals:false,//禁用小数
                            labelAlign:'right',
                            allowBlank: false,
                            width:190,
                            height:22,
                            margin:'0 5 15 0'
                       },{
                            xtype: "displayfield",
                            value: "<div>"+sc.setting.part+"</div>"//份
                         }]
                    },{ 
                       layout:"hbox",
                       id:"box2",
                       border:false,
                       items:[{
                        xtype : 'numberfield',
                        id:'printPrice',
                        beforeLabelTextTpl:"<font color='red'> * </font>",
                        fieldLabel: "<span>"+sc.setting.each+"</span>",//每份
                        minValue:0,
                        decimalPrecision:2,
                        step:0.01,
                        width:190,
                        height:22,
                        labelAlign:'right',
                        allowBlank: false,
                        margin:'0 5 15 0'
                    },{
                          xtype: "displayfield", 
                          value: "<div>"+sc.setting.RMB+"</div>"//元
                        }]
                    },{
                       layout:"hbox",
                       id:"box3",
                       border:false,
                       items:[{
                        xtype : 'numberfield',
                        id:'effectiveDate',
                        beforeLabelTextTpl:"<font color='red'> * </font>",
                        fieldLabel: "<span>"+sc.setting.effectivedate+"</span>",//有效期
                        minValue:1,
                        allowDecimals:false,
                        labelAlign:'right',
                        width:190,
                        height:22,
                        allowBlank: false,
                        margin:'0 5 15 0'
                     },{
                          xtype: "displayfield",
                          value: "<div>"+sc.setting.month+"</div>"//月
                       }]
                    }]
        });
        return printPanel;
    },
    /**
     * 选择服务模板的点击事件
     * @param {} menuitem
     */
    selectTemplate:function(menuitem){
        var me = this;
        var templateType = menuitem.templateType;
        Ext.widget('window',{
            title:sc.setting.select,
            height:400,
            width:300,
            layout:'fit',
            modal : true,
            items:{
                xtype:'templateselector',
                dataType:templateType,
                listeners:{
                    itemclick:function(tree,node){
                        var configInputPanel = Ext.getCmp('mainPanel').query('#configInput')[0];
                        if(node.get('leaf')){
                            Ext.getCmp('templateId').setValue(node.get('text'));
                            me.templateType = templateType;
                            me.templateId = node.get('id');
                            if(templateType==2){
                                Ext.getCmp('box3').setDisabled(true);
                                Ext.getCmp('box3').setVisible(false);
                                if(!configInputPanel){
                                    var configInputPanel = me.createConfigInputPanel();
                                    Ext.getCmp('mainPanel').add(configInputPanel);
                                }
                            }else{
                                if(configInputPanel){
                                    configInputPanel.close();
                                }
                                Ext.getCmp('box3').setDisabled(false);
                                Ext.getCmp('box3').setVisible(true);
                            }
                            tree.up('window').close();
                        }
                    }
                }
            }
        }).show();
    },
    /**
     * 选择和更换图标的点击事件
     */
    listener:function(formPanel){
        var me = this;
        var selectWin = Ext.widget('window',{
            title:sc.setting.selecticon,//选择服务
            id:'selectSerWin',
            resizable:false,//禁止拉伸
            modal : true,//遮罩
            bodyPadding:20,
            height:157,
            width:730,
            layout:'fit'
        });
        var iconArray = ['bxzm.png','ckzm.png','spzm.png','srzm.png','zczm.png','zzzm.png'];
        var iconBox = formPanel.query('#iconBox')[0];
        if(iconBox.query('image')[0]){
            var itemid = iconBox.query('image')[0].itemId;
            for(var i=0;i<iconArray.length;i++){
                if(iconArray[i]==itemid){
                    Ext.Array.removeAt(iconArray,i);
                }
            }
        }
        var iconlength = iconArray.length;
        var panelCount = parseInt(iconlength/4);
        var iconremiander = iconlength%4;
        if(iconremiander>0){
              panelCount=panelCount+1;
        }
        var panelArray = [];
        for(var i=0;i<panelCount;i++){
            var bigPanel = Ext.create('Ext.Panel',{
                 layout:'hbox',
                 border:false
            });
            me.createContainer(bigPanel,i,formPanel,iconArray,selectWin);
            panelArray.push(bigPanel);
        }
        var carousel = Ext.create('EHR.carousel.Carousel',{
            items:panelArray
        });
        selectWin.add(carousel);
        selectWin.show();
    },
    createContainer:function(bigPanel,i,formPanel,iconArray,selectWin){
        var beginIdex = i*4;
        var endIdex = (i+1)*4;
        if(endIdex>iconArray.length){
            endIdex =iconArray.length;
        }
        for(beginIdex;beginIdex<endIdex;beginIdex++){
            var iconBox = formPanel.query('#iconBox')[0];
            var icon = iconArray[beginIdex];
            var container = Ext.create('Ext.Container',{
                margin:'0 6 0 0',
                width:145,
                height:75,
                items:[{
                    xtype:'image',
                    itemId:icon,
                    src:sc.setting.iconurl+icon
                },{
                    xtype:'panel',
                    border:false,
                    html:"<div style='word-break:break-all;color:white;font-size:13px;height:37px;'>"+formPanel.query('textfield')[1].value+"</div>",
                    bodyStyle:'background:transparent;',
                    style:'width:80px;position:relative;right:-63px;top:-55px;'
                }],
                listeners:{
                    element:'el',
                    click:function(){
                        var owner = Ext.getCmp(this.id);
                        if(owner.ownerCt.ownerCt.xtype == 'carousel'){
                            formPanel.query('#changeicon')[0].show();
                            formPanel.query('#iconBox')[0].show();
                            formPanel.query('#selecticon')[0].hide();
                            iconBox.removeAll();
                            iconBox.add(owner); 
                            selectWin.close();
                        }
                    }
                }
            });
            bigPanel.add(container);
        }
    },
    /**
     * 添加录入配置项按钮弹窗
     */
    addConfigInput:function(){
        var fieldItemSelecorWin = Ext.widget('window',{
    		header:false,
            height:400,
            width:300,
            resizable:false,
            layout:'fit',
            modal:true,
            items:{
                xtype:'fielditemselecor',
                source:'A',
                multiple:true,
                title:sc.setting.selectFieldItem,//选择指标
                okBtnText:sc.setting.ok,
                cancelBtnText:sc.setting.cancel,
                searchEmptyText:sc.setting.inputItemNameOrItemCodeSearch,//'输入指标名称或指标代码查询...'
                listeners:{
                    selectend:function(selectorCmp,fields){
                    	var addType = "add";
                        EditServiceScope.addConfigInputData(fields,addType);
                        fieldItemSelecorWin.close();
                    },
                    cancel:function(selectorCmp){
                        selectorCmp.close();
                        fieldItemSelecorWin.close();
                    }
                }
            }
        });
        fieldItemSelecorWin.show();
    },
    /**
     * 增加配置项
     * param fields 选中的指标
     * param addType 新增还是回显服务
     */
    addConfigInputData:function(inputItems,addType){
    	var me = this;
        var breakFlag = false;
		var repeatItemArray = [];//重复项数组
    	var inputItemsLength = inputItems.length;//录入项个数
    	var subsetName = "";//子集名称加编码
    	var itemName = "";//指标名称加编码
        var ConfigInputTableStore = Ext.getCmp('configInputGrid').getStore();//所有数据
        
    	for(var i=0;i<inputItemsLength;i++){
    		var inputItemMap = new HashMap();
    		if(addType=="echo"){
        		var listName = inputItems[i].fieldSetDesc;//主集或子集名称
        		var setId = inputItems[i].setId;//主集或子集编码
        		var fieldName = inputItems[i].itemdesc;//指标名称
        		var itemId = inputItems[i].itemId;//指标编码
        		var isWrite = inputItems[i].isWrite;//读写标识
        		if(isWrite=="1"){
        		    isWrite = sc.setting.write;
        		}else{
        		    isWrite = sc.setting.read;
        		}
    		}else{
                var existingInputItems = ConfigInputTableStore.data.items;//已存在的配置项
        		var listName = inputItems[i].data.fieldsetdesc;//主集或子集名称
        		var setId = inputItems[i].data.fieldsetid;//主集或子集编码
        		var fieldName = inputItems[i].data.itemdesc;//指标名称
        		var itemId = inputItems[i].data.id.toUpperCase();//指标编码
                for(var j=0;j<existingInputItems.length;j++){
                    var existingItemName = existingInputItems[j].data.itemName;
                    if(existingItemName.indexOf(itemId)>-1){
                        breakFlag = true;
                        repeatItemArray.push(fieldName);
                        break;
                    }else{
                        breakFlag = false;
                    }
                }
                if(breakFlag){//若该指标已添加 则跳出此次循环 不重复添加
                    continue;
                }
    		}
    		subsetName = listName+"["+setId+"]";
    	    itemName = fieldName+"["+itemId+"]";
            ConfigInputTableStore.add({
                subsetName: subsetName, 
                itemName: itemName, 
                readOrWrite: addType=="echo"?isWrite:sc.setting.read //读
            });
            inputItemMap.put("setId",setId);
            inputItemMap.put("itemId",itemId);
            me.inputItemArray.push(inputItemMap);
    	}
    	if(repeatItemArray.length>0){
    		Ext.Msg.show({
                title: sc.setting.promptmessage,//提示信息
                msg: sc.setting.stopConfigRepeatItem+repeatItemArray,//不允许配置重复项:
                buttons: Ext.MessageBox.OK,
                width:350,
                modal:true,
                closable: true,
                icon:Ext.Msg.INFO
            });
    	}
    },
    /**
     * 删除配置项
     */
    deleteConfigInput:function(){
    	var me = this;
        Ext.Msg.confirm(sc.setting.tip,sc.setting.deleteConfirm,function(btn){
            if(btn == 'yes'){
                var useConfigInputCheckBox = Ext.getCmp("useConfigInput");//配置录入项启用框
                var configInputGrid = Ext.getCmp('configInputGrid');
                var ConfigInputTableStore = configInputGrid.getStore();
                var records = configInputGrid.getSelectionModel().getSelection();
                for(var i=0;i<records.length;i++){
                    var itemNameArray = records[i].data.itemName.split("[");
                    var itemId = itemNameArray[1].substring(0,itemNameArray[1].length-1);
                    for(var j=0;j<me.inputItemArray.length;j++){
                        if(me.inputItemArray[j].itemId==itemId){
                            Ext.Array.remove(me.inputItemArray,me.inputItemArray[j]);
                        }
                    }
                }
                ConfigInputTableStore.remove(records);
                if(ConfigInputTableStore.data.length==0){
                    useConfigInputCheckBox.setValue(false);
                }
            }
        });
    }
});
