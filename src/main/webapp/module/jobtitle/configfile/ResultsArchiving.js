/**
 * 资格评审_配置_结果归档
 * @createtime August 24, 2015 9:07:55 PM
 * @author chent
 * 
 * */
Ext.define('ConfigFileURL.ResultsArchiving',{
	requires:['EHR.extWidget.proxy.TransactionProxy'],
	configMap:new Ext.util.HashMap(),//配置信息
	descinfo:new Ext.util.HashMap(),// 配置信息对应名称
	archiveData:'',//归档子集数据源
	objectiveData:'',//目标指标数据源
	configArrayName: '',// 参数名称集，没有归档子集
	srcList:'',		//需要归档的源指标
	constructor:function(config) {
		this.configMap = this.getConfigArray();
		this.descinfo.map = this.configMap.get('descinfo');
		this.configArrayName = this.configMap.get('configArrayName');
		this.srcList = this.configMap.get('srcList');
		this.archiveData = this.getArchiveData();
		this.objectiveData = this.getObjectiveData(this.configMap.get('fieldset'));
		
		this.getMainWin();
		//自适应窗口大小 
		Ext.EventManager.onWindowResize(function(){
			var mainWin =  Ext.getCmp('ResultsArchivingMainWin');
			if(mainWin){
				mainWin.setWidth(Ext.getBody().getWidth()*0.6);
				mainWin.setHeight(Ext.getBody().getHeight()*0.8);	
			}
		});
	},
	// 获取配置参数
	getConfigArray:function(){
		
		var configMap = new Ext.util.HashMap();

		var map = new HashMap();
		map.put("type", "3");//获取配置信息
		Rpc({functionId:'ZC00004003',async:false,success:function(form,action){
			configMap.map = Ext.decode(form.responseText).configmap;
		}},map);
		return configMap;
	},
	// 获取归档配置页面
	getMainWin:function(){
		
		Ext.create('Ext.window.Window', {
			id:'ResultsArchivingMainWin',
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
		            var map = new HashMap();
		            var obj = this.configMap.map;
		            //校验会议名称和起始时间是否配置，如未配置则不允许保存
		            var W0303 = obj.W0303.replace(/\s+/g,"");
		            var W0309 = obj.W0309.replace(/\s+/g,"");
		           if(Ext.isEmpty(W0303) && Ext.isEmpty(W0309)){
		           		Ext.showAlert("【"+zc.configFile.meetinName+"】、【"+zc.configFile.startDate+"】"+zc.configFile.errorMsg);
		           		return;
		           }else if(Ext.isEmpty(W0303)){
		           		Ext.showAlert("【"+zc.configFile.meetinName+"】"+zc.configFile.errorMsg);
		           		return;
		           }else if(Ext.isEmpty(W0309)){
		          	 	Ext.showAlert("【"+zc.configFile.startDate+"】"+zc.configFile.errorMsg);
		          	 	return;
		           }
		            obj.descinfo = "";
					map.put('configInfo', obj);//配置信息
					Rpc({functionId:'ZC00004004',async:false,success:function(form,action){
						var msg = Ext.decode(form.responseText).msg;
						if(!Ext.isEmpty(msg)){
							Ext.showAlert(msg,function(){  
					           Ext.getCmp('ResultsArchivingMainWin').close();
					        }); 
						}
						// 刷新表格，避免出现红色三角
						Ext.getCmp('gridPanel').getStore().load();
					}},map);
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
	// 获取列表
	getTablePanel:function(){
		
		return Ext.create('Ext.grid.Panel', {
			id:'gridPanel',
			store: this.getGridTableStore(),
			tbar:this.getArchiveCombo(),
			columnLines:true,
			columns: [
				{	header: '源指标',  dataIndex: 'metaField', flex:2 },
				{	header: '类型', dataIndex: 'typeName', flex: 1 },
				{	header: '代码对应', dataIndex: 'codesetid',flex:1 },
				{	header: '指标', 
					dataIndex: 'objectivedata',
					flex:2 ,
					editor: this.getObjectiveCombo(),
					renderer:function(value){
						var html = "";
						if(Ext.isEmpty(value)){
							return html;
						}
						if(this.descinfo.map == undefined){
							this.descinfo.map = new HashMap();
						}
						if(this.descinfo.get(value) != undefined){
							html = this.descinfo.get(value);
							
						}
						var record = this.objectiveData.findRecord('itemid', value);
						if(record){
							html = record.data.itemdesc;
							if(this.descinfo.get(value) == undefined){
								this.descinfo.add(value, html);
							}
						}
						
						return html;
						
					},
					scope:this
				}
			],
			plugins:[{ptype:'cellediting',clicksToEdit:1}]
		});
		
	},
	// 获取表格数据
	getGridTableStore:function(){
		var dataArray = new Array();
		var configName = this.configArrayName;
		for(var j=0;j<this.srcList.length;j++){
			var obj = new Object();
			var fieldItem = this.srcList[j];	//通过数据字典查出来的指标项
			if(fieldItem.itemid == "w0531"||fieldItem.itemid == "w0529"||fieldItem.itemid == "w0527"){
				obj.metaField = fieldItem.itemdesc+"（"+zc.label.exExpert+"）";
			}else if(fieldItem.itemid == "w0547"||fieldItem.itemid == "w0543"||fieldItem.itemid == "w0545"){
				obj.metaField = fieldItem.itemdesc+"（"+zc.label.inExpert+"）";
			}else if(fieldItem.itemid == "w0553"||fieldItem.itemid == "w0549"||fieldItem.itemid == "w0551"){
				obj.metaField = fieldItem.itemdesc+"（"+zc.label.inReview+"）";
			}else if(fieldItem.itemid == "w0567"||fieldItem.itemid == "w0563"||fieldItem.itemid == "w0565"){
				obj.metaField = fieldItem.itemdesc+"（"+zc.label.inOther+"）";	//haosl 20160830  增加阶段标识
			}else if(fieldItem.itemid == "w0533"){
				obj.metaField = "评审结果（"+zc.label.exExpert+"）";
			}else if(fieldItem.itemid == "w0557"){
				obj.metaField = "评审结果（"+zc.label.inExpert+"）";
			}else if(fieldItem.itemid == "w0559"){
				obj.metaField = "评审结果（"+zc.label.inReview+"）";//评委会
			}else if(fieldItem.itemid == "w0569"){
				obj.metaField = "评审结果（"+zc.label.inOther+"）";//haosl 20160830  增加阶段标识
			}else if(fieldItem.itemid == "w0517"){//总人数
				obj.metaField = "总人数（"+zc.label.inReview+"）";
			}else if(fieldItem.itemid == "w0521"){
				obj.metaField = "总人数（"+zc.label.inExpert+"）";
			}else if(fieldItem.itemid == "w0523"){
				obj.metaField = "总人数（"+zc.label.exExpert+"）";
			}else if(fieldItem.itemid == "w0571"){
				obj.metaField = "总人数（"+zc.label.inOther+"）";
			}else if(fieldItem.itemid == "attendance_1"){//参会人数
				obj.metaField = "参会人数（"+zc.label.inReview+"）";
			}else if(fieldItem.itemid == "attendance_2"){
				obj.metaField = "参会人数（"+zc.label.inExpert+"）";
			}else if(fieldItem.itemid == "attendance_3"){
				obj.metaField = "参会人数（"+zc.label.exExpert+"）";
			}else if(fieldItem.itemid == "attendance_4"){
				obj.metaField = "参会人数（"+zc.label.inOther+"）";
			}else{
				obj.metaField = fieldItem.itemdesc;
			}
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
			obj.typeName = typeName;
			obj.objectivedata =  this.configMap.get(fieldItem.itemid.toUpperCase());
			obj.varName = fieldItem.itemid.toUpperCase();
			dataArray.push(obj);
		}
		return Ext.create('Ext.data.Store', {
			fields:['metaField', 'typeName', 'codesetid', 'objectivedata'],
			data:{'items':dataArray},
			proxy: {
				type: 'memory',
				reader: {
					type: 'json',
					root: 'items'
				}
			}
		});
	},
	// 获取归档指标
	getArchiveCombo:function(){
		
		return Ext.widget('combo',{
	    	fieldLabel: '结果归档子集',
	    	labelSeparator:'',
	    	store:this.archiveData,
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
	            	this.archiveData.on("load", function(){
						var record = this.archiveData.findRecord('fieldsetid', this.configMap.get('fieldset'));
						combo.setValue(record);
					},this,{single: true});
	            },
	            select:function(combo){// 用change事件会导致初始化就重置了目的指标
	            	
	            	// 更新配置信息
	            	var fieldsetid = combo.getValue();
	            	this.configMap.replace('fieldset', fieldsetid);
	            	Ext.Array.each(this.configArrayName, function(name, index, countriesItSelf) {
	            		if(name!='fieldset'){
	            			this.configMap.replace(name, '');
	            		}
					},this);
	            	// 重置目的指标
	            	var records = combo.up('grid').getStore().data.items;
					for(var i=0; i<records.length; i++){
						records[i].set("objectivedata", "");
					}
	            	
					// 刷新目的指标数据源
	            	var store = Ext.data.StoreManager.lookup('objectiveStore');
	            	var extraParams = {
								type : '2',
								fieldsetid : fieldsetid
							}
	            	store.getProxy().extraParams = extraParams;
	            	store.load();
	            	// 刷新表格，避免出现红色三角
	            	Ext.getCmp('gridPanel').getStore().load();
	            },
	            scope:this
	        }
		});
	},
	// 获取目标指标
	getObjectiveCombo:function(){
		
		return objectiveCombo = Ext.widget('combo',{
	    	store:this.objectiveData,
	        valueField: 'itemid',
	        displayField: 'itemdesc',
//	    	maxWidth:200,
//	        forceSelection :true,
	        editable:false,
	        labelAlign:'left',
	        margin:'3 0 5 0',
	        listeners: {  
	            expand:function(combo){
	            	var grid = combo.up('grid');
	            	var itemtype = grid.getSelectionModel().getSelection()[0].data.itemtype;
	            	var codesetid = grid.getSelectionModel().getSelection()[0].data.codesetid;
	            	if(itemtype != "A"){
	            		codesetid = 0;
	            	}
	            	var store = combo.getStore();
//	            	store.filter(
//            		{ 
//            			property : 'itemtype', 
//            			value : itemtype
//            		}, { 
//            			property : 'codesetid',
//            			value: codesetid
//            		}, { 
//            			property : 'name',
//            			value:  /^\+?[1-9]*$/
//            		});
//				    store.load();
				    
				    store.filter({
                        filterFn: function(node) { 
                        	var nodesCodesetId = node.data.codesetid;
                        	if(node.data.codesetid == '0'){
                        		nodesCodesetId = "";
                        	}
                        	var visible = true;
                        	//目标指标加空选项，以便删除已选指标  haosl  2017-9-29
                        	if(node.data.itemid=="　")
                        		return visible;
                        	if(node.data.itemtype == itemtype && nodesCodesetId==codesetid){//匹配指标类型、代码型
                        		visible = true;
			            		var configMap = this.configMap.map;
                        		for(var p in configMap){
                        			var itemid = configMap[p];
	                        		if(node.data.itemid == itemid){// 选择过的指标不显示
	                        			visible = false;
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
	            	var varName = grid.getSelectionModel().getSelection()[0].data.varName;
	            	this.configMap.replace(varName, selectValue);
	            },
	            scope:this
	        }
		});
	},
	// 获取归档子集数据源
	getArchiveData:function(){
		
		return Ext.create('Ext.data.Store', {
			id:'archiveDataStore',
			fields:['fieldsetid', 'fieldsetdesc'],
			proxy:{
				type: 'transaction',
		        functionId : 'ZC00004003',
				extraParams:{
			        type:'1'
				},
				 reader: {
					  type : 'json',
					  root : 'archivedata'         	
				}
			},
			autoLoad: true
		});
	},
	// 获取目的指标数据源
	getObjectiveData:function(fieldsetid){
		
		return Ext.create('Ext.data.Store', {
			id:'objectiveStore',
			fields:['itemid', 'itemdesc', 'itemtype', 'codesetid'],
			proxy:{
				type: 'transaction',
		        functionId : 'ZC00004003',
				extraParams:{
			        type : '2',
			        fieldsetid:fieldsetid
				},
				 reader: {
					  type : 'json',
					  root : 'objectivedata'         	
				}
			},
			autoLoad: true
		});
	}
});