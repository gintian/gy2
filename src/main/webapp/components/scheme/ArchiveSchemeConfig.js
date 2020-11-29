/**
 * 归档方案配置组件   wangbo 20181105 
 *
 * sourceFieldData  源指标数据集合 
 * 	参数如下：
 *  	fieldsetid, itemid , itemdesc , itemlength, itemtype, codesetid,  valueitemid 配置关联指标,valuedesc 配置关联指标描述  等
 * SubSetData:  子集集合
 *	参数如下：
 *  	fieldsetid, fieldsetdesc 
 * showSubSet:  当前显示归档子集  map
 *  参数如下：
 *  	fieldsetid, fieldsetdesc
 * callbackFn: 回调方法
 *      返回参数：sourceFieldData,SubSetData,showSubSet
 *
 * 示例：
 * Ext.create('Ext.scheme.ArchiveSchemeConfig',{
 * 		sourceFieldData:xxx,
 *		SubSetData:xxx,
 *		showSubSet:xxx,
 *		callbackFn:xxx
 * });
 */

Ext.define('Ext.scheme.ArchiveSchemeConfig',{
	requires:['EHR.extWidget.proxy.TransactionProxy'],
	sourceFieldData:undefined, // 源指标 数据集合
	SubSetData:undefined, //子集集合
	showSubSet:undefined, //当前显示的归档子集
	callbackFn:undefined, //回调方法
    filterItem:[],//用于过滤掉归档子集的指定指标
    messages:"", //返回信息
	constructor:function(config) {
		this.config.sourceFieldData=config.sourceFieldData;
		this.config.SubSetData=config.SubSetData;
		this.config.showSubSet=config.showSubSet;
		this.config.objectiveData = this.getObjectiveData(this.config.showSubSet.fieldsetid);
		this.config.callbackFn = config.callbackFn;
		this.config.messages = config.messages;
		//要排除的子集指标
	    this.filterItem = config.filterItem || [];
		this.archiveData = Ext.create('Ext.data.Store',{
			storeId:'archiveStore',
			fields:['fieldsetid','fieldsetdesc'],
			data:this.config.SubSetData
		});
		this.fieldsetid = this.config.showSubSet.fieldsetid;	
		
		this.setSelectMap();
		
		this.getConfigWin();
		//自适应窗口大小 
		Ext.EventManager.onWindowResize(function(){
			var mainWin =  Ext.getCmp('ResultsArchivingMainWin');
			if(mainWin){
				mainWin.setWidth(Ext.getBody().getWidth()*0.6);
				mainWin.setHeight(Ext.getBody().getHeight()*0.8);	
			}
		});
		// 54240 设置归档指标的返回信息
		if(!Ext.isEmpty(this.config.messages)){
			Ext.showAlert(this.config.messages);
		}
	},
	/**获取参数配置*/
	getConfigWin:function(){
		Ext.create('Ext.window.Window', {
			id:'ArchiveSchemeConfigWin',
			modal:true,
		    title: '归档方案配置',
			modal: true,
			layout:'fit',
			width:Ext.getBody().getWidth()*0.6,
			height:Ext.getBody().getHeight()*0.8,
			border:false,
			buttonAlign:'center',
		    buttons: [{
		        text: '保存',
		        handler: function() {
		        	for(var i=0; i < this.config.SubSetData.length; i++){
		        		if(this.config.SubSetData[i].fieldsetid != this.fieldsetid)
		        			continue;
		        		this.config.showSubSet.fieldsetid=this.fieldsetid;
		        		this.config.showSubSet.fieldsetdesc=this.config.SubSetData[i].fieldsetdesc;
		        	}
		        	this.config.callbackFn(this.config.sourceFieldData,this.config.SubSetData,this.config.showSubSet);
		        	Ext.getCmp('ArchiveSchemeConfigWin').close();
		        },
		        scope:this
		    },{
		        text: '取消',
		        handler: function() {
		            this.up('window').close();
		        }
		    }],
			items:[this.getTablePanel()]
		}).show();
	},
	/**获取归档配置页面*/
	getTablePanel:function(){
		return Ext.create('Ext.grid.Panel', {
			id:'ArchiveSchemegridPanel',
			store: this.getGridTableStore(),
			tbar:this.getArchiveCombo(),
			columnLines:true,
			columns: [
				{	header: '源指标',  dataIndex: 'metaField', flex:2 },
				{	header: '类型', dataIndex: 'typeName', flex: 1 },
				{	header: '代码对应', dataIndex: 'codesetid',flex:1 },
				{	header: '目标指标', 
					dataIndex: 'valuedesc',
					flex:2 ,
					editor: this.getObjectiveCombo(),
					renderer:function(value,record,store){
						value = value == undefined? '':value;
						if(value && value != '　' && value.split('`').length<2)
							return value;
						if(!value || value =='　'){
							for(var i = 0 ; i <this.config.sourceFieldData.length ; i++){
								if(this.config.sourceFieldData[i].itemid == store.data.itemid){
									this.config.sourceFieldData[i].valuedesc = '';
									this.config.sourceFieldData[i].valueitemid = '';
									break;
								}
							}						
							return "";
						}
						
						for(var i = 0 ; i <this.config.sourceFieldData.length ; i++){
							if(this.config.sourceFieldData[i].itemid == store.data.itemid){
								this.config.sourceFieldData[i].valuedesc = value.split('`')[1];
								this.config.sourceFieldData[i].valueitemid = value.split('`')[0];
								break;
							}
						}
						return value.split('`')[1];
					},
					scope:this
				}
			],
			plugins:[{ptype:'cellediting',clicksToEdit:1}]
		});
	},
	/**加载表格数据*/
	getGridTableStore:function(){
		var dataArray = new Array();
		
		if(!this.config.sourceFieldData)
			this.config.sourceFieldData = [];
		for(var i=0;i<this.config.sourceFieldData.length;i++){
			var obj = {};
			var fieldItem = this.config.sourceFieldData[i];
			obj.itemtype = fieldItem.itemtype;
			obj.codesetid = fieldItem.codesetid=='0'?'':fieldItem.codesetid;
			var typeName = "";
			if("A"==obj.itemtype){
				if(""==obj.codesetid)
					typeName="字符型";
				else
					typeName="代码型"
			}else if("D"==obj.itemtype){
				typeName="日期型";
			}else if("N"==obj.itemtype){
				typeName="数值型";
			}else if("M"==obj.itemtype){
				typeName="备注型";
			}
			obj.metaField = fieldItem.itemdesc;
			obj.typeName = typeName;
			obj.itemid = fieldItem.itemid;
			obj.itemlength = fieldItem.itemlength;
			obj.valueitemid = fieldItem.valueitemid;
			obj.valuedesc = fieldItem.valuedesc;
			dataArray.push(obj);
		}
		var store =  Ext.create('Ext.data.Store', {
			storeId:'gridStoreData',
			fields:['metaField', 'typeName', 'codesetid','itemid','itemlength','valueitemid','valuedesc'],
			data:dataArray
		});
		
		return store;
	},
	/**归档子集数据加载*/
	getArchiveCombo:function(){
		var me = this;
		return Ext.widget('combo',{
	    	fieldLabel: '结果归档子集',
	    	labelSeparator:'',
	    	store:me.archiveData,
	        valueField: 'fieldsetid',
	        displayField: 'fieldsetdesc',
	    	maxWidth:300,
	    	labelWidth:80,
//	        forceSelection :true,
	        editable:false,
	        labelAlign:'left',
	        allowBlank: false,
	        margin:'3 0 5 0',
	        listeners: {  
				afterrender: function(combo) {//赋初始值
					combo.setValue(me.fieldsetid);
	            },
	            select:function(combo){// 用change事件会导致初始化就重置了目的指标
	            	// 更新配置信息
	            	me.fieldsetid = combo.getValue();
	            	/*
	            	Ext.Array.each(this.configArrayName, function(name, index, countriesItSelf) {
	            		if(name!='fieldset'){
	            			this.configMap.replace(name, '');
	            		}
					},this);
					*/
	            	// 重置目的指标
	            	var records = combo.up('grid').getStore().data.items;
					for(var i=0; i<records.length; i++){
						records[i].set("valuedesc", "");
					}
	            	
					// 刷新目的指标数据源
	            	var store = Ext.data.StoreManager.lookup('objectiveStore');
	            	var extraParams = {
						fieldsetid : me.fieldsetid
					}
	            	store.getProxy().extraParams = extraParams;
	            	store.load();
	            	// 刷新表格，避免出现红色三角
	            	Ext.getCmp('ArchiveSchemegridPanel').getStore().load();
	            	this.selectMap = new HashMap();
	            },
	            scope:this
	        }
		});
	},
	/** 加载归档配置指标*/
	getObjectiveCombo:function(){
		var archiveMe = this;
		return objectiveCombo = Ext.create('Ext.form.field.ComboBox',{
	   		store:this.config.objectiveData,
	        valueField: 'valueitemid',
	        displayField: 'valuedesc',
	        editable:false,
	        labelAlign:'left',
	        margin:'3 0 5 0',
            tpl: Ext.create('Ext.XTemplate',
                '<ul class="x-list-plain"><tpl for=".">',
                '<li role="option" class="x-boundlist-item" style="height: 22px">{valuedesc}</li>',
                '</tpl></ul>'
            ),
	        listeners: {  
	            expand:function(combo){
	            	var grid = combo.up('grid');
	            	var itemtype = grid.getSelectionModel().getSelection()[0].data.itemtype;
	            	var codesetid = grid.getSelectionModel().getSelection()[0].data.codesetid;
	            	if(itemtype != "A"){
	            		codesetid = 0;
	            	}
	            	var store = combo.getStore();
				    store.filter({
                        filterFn: function(node) { 
                        	var nodesCodesetId = node.data.codesetid;
                        	if(node.data.codesetid == '0'){
                        		nodesCodesetId = "";
                        	}
                        	var visible = true;
                        	//目标指标加空选项，以便删除已选指标  haosl  2017-9-29
                        	if(node.data.valueitemid.length==1){
                        		return visible;
                        	}
                        	if(node.data.itemtype == itemtype && nodesCodesetId==codesetid){//匹配指标类型、代码型
                        		visible = true;
			            		var configMap = this.selectMap;
                        		for(var p in configMap){
                        			var obj = configMap[p];
	                        		if(node.data.valueitemid == obj.valueitemid){// 选择过的指标不显示
	                        			visible = false;
	                        		}
	                        		if(archiveMe.filterItem.length>0){
                                        var filterStr = ","+archiveMe.filterItem.join(",").toLowerCase()+",";
                                        if (filterStr.indexOf(","+node.data.valueitemid.toLowerCase()+",")>-1){
                                            visible = false;
                                        }
                                    }
                        		}
                        	}else{
                        		visible = false;
                        	}
                        	return visible;
                        },
                        scope:this,
                        id: 'valueFilter'
                    });
                    store.load();
	            },
	            select:function(combo){
	            	var selectValue = combo.value;
	            	var grid = combo.up('grid');
	            	var varName = grid.getSelectionModel().getSelection()[0].data.itemid;
	            	if(!this.selectMap)
	            		this.selectMap = new HashMap();
	            	var obj = {};
	            	obj.valueitemid = selectValue;
	            	obj.valuedesc = combo.rawValue;
	            	this.selectMap.put(varName,obj);
	            },
	            scope:this
	        },
	        setValue:function(value){
	            var me = this;
				if(value &&  typeof(value)=="string" && value.indexOf("`")>-1){
					me.value = value.split("`")[0];
					me.setRawValue(value.split("`")[1]);
					value = me.value;
				}
		
		        // Value needs matching and record(s) need selecting.
		        if (value != null) {
		            return me.doSetValue(value);
		        }
		        // Clearing is a special, simpler case.
		        else {
		            me.suspendEvent('select');
		            me.valueCollection.beginUpdate();
		            me.pickerSelectionModel.deselectAll();
		            me.valueCollection.endUpdate();
		            me.lastSelectedRecords = null;
		            me.resumeEvent('select');
		        }
	        },
	        getValue:function(){
		        if(!this.value)
	    			return "";
	    		if(this.value == '　')
	    			return this.value;
	        	return this.value+"`"+this.rawValue;
	        },
	        doSetValue: function(value /* private for use by addValue */, add) {
		        var me = this,
		            store = me.getStore(),
		            Model = store.getModel(),
		            matchedRecords = [],
		            valueArray = [],
		            autoLoadOnValue = me.autoLoadOnValue,
		            isLoaded = store.getCount() > 0 || store.isLoaded(),
		            pendingLoad = store.hasPendingLoad(),
		            unloaded = autoLoadOnValue && !isLoaded && !pendingLoad,
		            forceSelection = me.forceSelection,
		            selModel = me.pickerSelectionModel,
		            displayIsValue = me.displayField === me.valueField,
		            isEmptyStore = store.isEmptyStore,
		            lastSelection = me.lastSelection,
		            i, len, record, dataObj,
		            valueChanged, key;
		        if (add && !me.multiSelect) {
		            Ext.raise('Cannot add values to non multiSelect ComboBox');
		        }
		        if (pendingLoad || unloaded || !isLoaded || isEmptyStore) {
		            if (!value.isModel) {
		                if (add) {
		                    me.value = Ext.Array.from(me.value).concat(value);
		                } else {
		                    me.value = value;
		                }
		                me.setHiddenValue(me.value);
		                me.setRawValue(displayIsValue ? value : '');
		                if (displayIsValue && !Ext.isEmpty(value) && me.inputEl && me.emptyText) {
		                    me.inputEl.removeCls(me.emptyUICls);
		                }
		            }
		 
		            if (unloaded && !isEmptyStore) {
		                store.load();
		            }
		 
		            if (!value.isModel || isEmptyStore) {
		                return me;
		            }
		        }
		 
		        value = add ? Ext.Array.from(me.value).concat(value) : Ext.Array.from(value);
		 
		        for (i = 0, len = value.length; i < len; i++) {
		            record = value[i];
		            if (!record || !record.isModel) {
		                record = me.findRecordByValue(key = record);
		                if (!record) {
		                    record = me.valueCollection.find(me.valueField, key);
		                }
		            }
		            if (!record) {
		                if (!forceSelection) {
		                    
		                    if (!record && value[i]) {
		                        dataObj = {};
		                        dataObj[me.displayField] = value[i];
		                        if (me.valueField && me.displayField !== me.valueField) {
		                            dataObj[me.valueField] = value[i];
		                        }
		                        record = new Model(dataObj);
		                    }
		                }
		                else if (me.valueNotFoundRecord) {
		                    record = me.valueNotFoundRecord;
		                }
		            }
		            if (record) {
		            	for(var p in archiveMe.selectMap){
			            	var obj = archiveMe.selectMap[p];
			            	if(value[i] == obj.valueitemid){
			            		record.set('valuedesc',obj.valuedesc);
			            		break;
			            	}
			            }
		                matchedRecords.push(record);
		                valueArray.push(record.get(me.valueField));
		            }
		        }
		 
		        if (lastSelection) {
		            len = lastSelection.length;
		            if (len === matchedRecords.length) {
		                for (i = 0; !valueChanged && i < len; i++) {
		                    if (Ext.Array.indexOf(me.lastSelection, matchedRecords[i]) === -1) {
		                        valueChanged = true;
		                    }
		                }
		            } else {
		                valueChanged = true;
		            }
		        } else {
		            valueChanged = matchedRecords.length;
		        }
		 
		        if (valueChanged) {
		            me.suspendEvent('select');
		            me.valueCollection.beginUpdate();
		            if (matchedRecords.length) {
		                selModel.select(matchedRecords, false);
		            } else {
		                selModel.deselectAll();
		            }
		            me.valueCollection.endUpdate();
		            me.resumeEvent('select');
		        } else {
		            me.updateValue();
		        }
		        
		        return me;
		    }
		});
	},
	// 获取目的指标数据源
	getObjectiveData:function(fieldsetid){
		return Ext.create('Ext.data.Store', {
			id:'objectiveStore',
			fields:['valueitemid', 'valuedesc', 'itemtype', 'codesetid'],
			proxy:{
				type: 'transaction',
		        functionId : 'ZJ100000302',
				extraParams:{
			        fieldsetid:fieldsetid
				},
				 reader: {
					  type : 'json',
					  root : 'objectivedata'         	
				}
			},
			autoLoad: true
		});
	},
	/** 已选指标    key 归档指标     value  子集指标*/
	setSelectMap:function(){
		if(!this.config.sourceFieldData)
			this.config.sourceFieldData=[];
		if(!this.selectMap)
			this.selectMap = new HashMap();
		for(var i = 0 ; i < this.config.sourceFieldData.length;i++){
			if(this.config.sourceFieldData[i].valueitemid){
				var obj = {};
				obj.valueitemid=this.config.sourceFieldData[i].valueitemid;
				obj.valuedesc=		this.config.sourceFieldData[i].valuedesc;	
				this.selectMap.put(this.config.sourceFieldData[i].itemid,obj);
			}
		}
	}
});