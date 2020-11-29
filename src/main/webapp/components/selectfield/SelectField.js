/**
 * 显示备选指标和已选指标
 * lis
 * 2015-12-18
 * 
 *	调用方法：		
			Ext.require('EHR.selectfield.SelectField',function(){
				Ext.create("EHR.selectfield.SelectField",{imodule:imodule});
			})
 *	参数说明：
			imodule:模块号为空时为公共简单查询方案；0:薪资类别,1:人事异动,2:所得税管理结构设置,9:职称
			type：同一个模块不同位置；
			dataMap:不同模块调用时传入的参数；
			comBoxDataInfoMap：(可选，如果有子集下拉框，则左侧备选指标leftDataList显示下拉框第一个子集中的指标)下拉框数据信息，
				key是1：A：信息主集和子集，B：组织机构主集和子集，K：职位信息主集和子集，Y:党组织，V：团组织，W：工会组织，h基准岗位，tableName是指标集表名，以逗号分隔；
				key是2：排除的指标集，参考1；
				key是3：数组，下拉框的数据集合，数组中包含为{dataValue:value,dataName:name}格式数据
			leftDataList:左侧备选指标数据，,数据集合中是CommonData对象，如果存在子集下拉框，则显示第一个子集指标；
			rightDataList:右侧已选指标数据,数据集合中是CommonData对象；
			saveCallbackfunc：回调函数，把条件返还给调用页面并触发一个自定义的方法，用于保存条件；
			queryCallbackfunc:回调函数，把条件返还给调用页面并触发一个自定义的方法，用于查询； 
	lis 2015-12-16
**/
Ext.define('EHR.selectfield.SelectField',{
		requires:["EHR.extWidget.proxy.TransactionProxy"],
	
	   /**
	   * 构造方法
	   * config 构造参数
	   */
        constructor:function(config){
			selectField_me = this;
			selectField_me.selectFieldConfig = config;
			selectField_me.imodule = config.imodule;//模块号 0:薪资类别,1:人事异动 ,2:所得税管理结构设置,9:职称
			if(config.type)
				selectField_me.type = config.type;//同一模块不同操作
			else selectField_me.type = "0";//“0”是没有下一步，“1”是只有下一步
			selectField_me.saveCallbackfunc = config.saveCallbackfunc;//保存回调函数
			selectField_me.queryCallbackfunc = config.queryCallbackfunc;//查询回调函数
			selectField_me.salaryid = null;//薪资类别id
			selectField_me.title = config.title;//窗口标题
			selectField_me.queryType = config.queryType;//"1"是通用查询，"0"是简单条件,"2"是简单查询
			selectField_me.leftDataList = config.leftDataList;//左侧备选指标数据
			selectField_me.rightDataList = config.rightDataList;//右侧已选指标数据
			selectField_me.isQuery = false;//是否是查询，如果是查询，则有“下一步”按钮
			selectField_me.flag = config.flag;//""：默认添加的时候去掉左边的，"1"：添加的时候不去掉左边的，左边指标仍存在 2017-06-09sunjian
			selectField_me.isShowResult = config.isShowResult;
			
			
			var dataMap = config.dataMap;
			if(dataMap){
                /**薪资发放-所得税管理 start */
                if(selectField_me.imodule==2){
                    selectField_me.isComputeDep=dataMap.isComputeDep;//所得税管理支持按归属部门计算 0 false 1 true
				}
                /**薪资发放-所得税管理 end */
                selectField_me.nbases=dataMap.nbases;  // 人员库范围 
				/**薪资类别-薪资属性-简单条件 start */
				selectField_me.salaryid = dataMap.salaryid;
				if(dataMap.cexpr && dataMap.condStr)
					selectField_me.expr = dataMap.cexpr+"|"+dataMap.condStr;
				/**人事异动-简单条件,gaohy**/
				selectField_me.priv=dataMap.priv;
				selectField_me.filter_factor=dataMap.filter_factor;// 按检索条件
				selectField_me.path=dataMap.path;
				/**薪资类别-薪资属性-简单条件 end */
				
				/**通用查询 start */
				selectField_me.buttonText = dataMap.buttonText;
				selectField_me.expression = dataMap.expression;
				
				/**通用查询 end */


				/** 职称评审 start **/
                selectField_me.isFilterSelectedExpert = dataMap.isFilterSelectedExpert;// 是否过滤掉已选专家 0不过滤 1过滤
				/** 职称评审 end **/

			}
			selectField_me.dataList = "";
			selectField_me.fieldSetStr = "";//包含指标集
			selectField_me.excludeFieldSetStr = "";//排除指标集
			this.panelWidth = 242;
        	this.panelHeight = 333;
        	
			var comBoxDataInfoMap = config.comBoxDataInfoMap;//子集下拉框
			if(comBoxDataInfoMap){
				selectField_me.isShowComBox = 1;
				for(var key in comBoxDataInfoMap){
					if(key == 1){
						selectField_me.fieldSetStr = comBoxDataInfoMap[key];
					}else if(key == 2){
						selectField_me.excludeFieldSetStr = comBoxDataInfoMap[key];
					}else if(key!='get'&&key!='put'){
						selectField_me.dataList = comBoxDataInfoMap[key];
					}
				}  
			}	
			else 
				selectField_me.isShowComBox = 0;
			       
            this.createSalary();//初始化弹出框
            this.initLeftPanel();//初始化左侧备选指标
            this.initButtonsPanel();//初始化按钮
            
			if(selectField_me.expr){//如果有条件表达式则解析
				 //右侧已选指标数据清空
				 selectField_me.rightDataList=null;
				 var map = new HashMap();
		    	 map.put("imodule",selectField_me.imodule);//0:首次进入 1：查询进入
				 map.put("opt","3");
				 map.put("expr",selectField_me.expr);
				 Rpc({functionId:'ZJ100000124',async:false,success:function(form,action){
				    	var result = Ext.decode(form.responseText);
						if(result.succeed){
							selectField_me.initRightPanel(result.selectedlist);//初始化右侧已选指标
				  		}else{
							Ext.MessageBox.alert(common.button.promptmessage,result.message);
						}
				    }},map);
			}else{
				this.initRightPanel();//初始化右侧已选指标
			}
            
            this.initComBox();//初始化下拉框
        },
		 createSalary:function()  
		 {
        	var isHideSaveButton = false;//是否隐藏保存按钮
        	var isHideCancelButton = false;//是否隐藏取消按钮
        	//薪资类别
        	if(selectField_me.imodule=='0'||selectField_me.imodule=='1'||selectField_me.imodule=='9'){//增加人事异动,gaohy 9:职称
        		if(selectField_me.type=='1'||selectField_me.type=='3'){//薪资属性-简单条件
        			isHideSaveButton = true;
        			isHideCancelButton = true;
        			selectField_me.isQuery = true;
            	}
        	}
        	//imodule为空时为公共简单查询方案
        	if(selectField_me.imodule==''){
        	      isHideSaveButton = true;
                    isHideCancelButton = true;
                    selectField_me.isQuery = true;
        	}
        	
	 		//生成弹出得window
			selectField_me.win=Ext.widget("window",{
	   		  title : selectField_me.title,
	   		  width: 550,
	   		  height: selectField_me.imodule==2?445:420,
	   		  bodyPadding:'3 2',
	   		  minButtonWidth:45,
			  modal:true,
			  resizable:false,
			  closeAction:'destroy',
			  items: [{
				  xtype:'panel',
				  layout: 'hbox',
				  border:false,
				  items:[{
					    xtype: 'container',
					    border:false,
					    layout:'vbox',
					    items:[{
						    	xtype: 'container',
							    border:false,
							    id:'fieldPanelId' 
							  },{
								xtype: 'container',
							    border:false,
							    id:'leftPanelId' 
						  }, {
                            xtype: 'container',
							hidden:selectField_me.imodule==2?false:true,//所得税管理时显示
                            margin:'5 0 0 0',
                            border: false,
                            items: [{
                                xtype: 'checkbox',
                                id:'leftcheckboxId',
								checked:selectField_me.isComputeDep=="1"?true:false,
                               // boxLabel: gz.label.supportUnit, 通用组件不能引入工资的变量，否则报错
                                boxLabel: "支持按归属单位进行所得税管理",
                                labelWidth: 20
							}]
                        }]
				  }, {
					  xtype: 'container',
					    border:false,
					    id:'buttonId' 
				  },{
					  xtype: 'container',
					    border:false,
					    id:'rightPanelId'  
				  }]
			  }],
	          bbar:[
	                     {xtype:'tbfill'},
			          		{
	                    	 	type: 'button',
			          			text:common.button.ok, //确定
			          			hidden:isHideSaveButton,
			          			style:'margin-right:5px',
			          			handler:function(){
	                    	 		selectField_me.saveItems();
	                     		}
			          		},
			          		{
			          			type: 'button',
			          			text:common.button.cancel, //取消
			          			hidden:isHideCancelButton,
			          			handler:function(){
			          				selectField_me.win.close();
			          			}
			          		},
			          		{
			          			type: 'button',
			          			text:common.label.nextStep, //下一步
			          			hidden:!selectField_me.isQuery,
			          			handler:function(){
			          				selectField_me.nextStep();
			          			}
			          		},{xtype:'tbfill'}
			           ]     
	   		});                               
			selectField_me.win.show(); 
		 },
		 //初始化下拉框
		 initComBox:function(){
				var info_type = "";
		        if(selectField_me.selectFieldConfig.dataMap && selectField_me.selectFieldConfig.dataMap.info_type)
		        	info_type = selectField_me.selectFieldConfig.dataMap.info_type;
			 	//子集数据store
	        	var comBoxStore = Ext.create('Ext.data.Store',
	        			{
	        				fields:['dataName','dataValue'],
	        				proxy:{
						    	type: 'transaction',
						        functionId:'ZJ100000124',
						        extraParams:{
						        		imodule:selectField_me.imodule,
						        		type:selectField_me.type,
						        		info_type:info_type,
						        		fieldSetStr:selectField_me.fieldSetStr,
						        		excludeFieldSetStr:selectField_me.excludeFieldSetStr,
						        		priv:selectField_me.priv,//手工选人、条件选人不按管理范围过滤, 0按管理范围过滤(默认值),1不按,gaohy
						        		path:selectField_me.path,//path=值为功能号（便于查哪个功能）
						        		opt:'1'
						        },
						        reader: {
						            type: 'json',
						            root: 'list'         	
						        }
						}
	        			});
	        	//子集列表数据store
	        	var comBox = Ext.widget('combo',{
					store:comBoxStore,
					width: this.panelWidth,
				    queryMode: 'local',
				    hidden:selectField_me.isShowComBox==0?true:false,
				    repeatTriggerClick : true,
				    editable: false,
				    forceSelection: true,
				    displayField: 'dataName',//显示的值
				    valueField: 'dataValue',//隐藏的值
				    listeners:{
		   				select:function(combo,records){
							selectField_me.changeSelect(combo.getValue());
						}
	        		}
	        	});
	        	//显示下拉框时执行
				if(selectField_me.isShowComBox == 1)
					comBoxStore.load();
				//下拉框初始化显示第一个
		   		comBoxStore.on('load',function(store,records,options){
					if(selectField_me.dataList != null&&selectField_me.dataList!=''){
	        		Ext.each(selectField_me.dataList,function(obj,index){
	            		comBoxStore.insert(index,[{dataName: obj.dataName,dataValue: obj.dataValue}]);
		            	});
		            	comBox.select(selectField_me.dataList[0].dataValue);
		   				selectField_me.changeSelect(selectField_me.dataList[0].dataValue);
		        	}
		   			else if(records.length>1){
		   				comBox.select(records[0].data.dataValue);
		   				selectField_me.changeSelect(records[0].data.dataValue);
		   			}
				});
		   		
	        	Ext.getCmp('fieldPanelId').add(comBox);
		 },
		 
		 //初始化左侧面板数据
		 initLeftPanel:function(){
			 var leftStore = Ext.create('Ext.data.Store', {
				 	storeId: 'leftStoreId',
					fields:['dataName','dataValue'],
					proxy:{
				    	type: 'transaction',
				        functionId:'ZJ100000124',
				        extraParams:{
				        		imodule:selectField_me.imodule,
				        		type:selectField_me.type,
				        		priv:selectField_me.priv,//手工选人、条件选人不按管理范围过滤, 0按管理范围过滤(默认值),1不按,gaohy
						        path:selectField_me.path,//path=值为功能号（便于查哪个功能）
				        		opt:'2'
				        },
				        reader: {
				            type: 'json',
				            root: 'list'         	
				        }
				}
			});	
			
			if(selectField_me.leftDataList){
				Ext.each(selectField_me.leftDataList,function(record,index){
					leftStore.insert(index,[{dataName: record.dataName,dataValue: record.dataValue}]);
				})
			}
			
			//左侧面板
			selectField_me.leftGrid = Ext.create('Ext.grid.Panel', {
				store:leftStore,
				width:this.panelWidth,
			 	height: selectField_me.isShowComBox==0?this.panelHeight:305,
			 	border:true,
			 	scrollable:"y",
			 	bufferedRenderer:false,
			 	multiSelect:true,
			 	forceFit:true,
			 	hideHeaders:true,
				columns: [
				    { text: common.label.itemName,menuDisabled:true, dataIndex: 'dataName'},//项目名称
				    { text: common.label.itemName,menuDisabled:true, dataIndex: 'dataValue',hidden:true}
					
				],
				listeners:{
		 	    	'celldblclick':function(grid, td,cellIndex,record,tr,rowIndex,e){
							selectField_me.addMode();
		     		}
		     },
				renderTo:Ext.getBody()
			});
			
			if(selectField_me.isShowComBox==0){
				var menubar  = Ext.create('Ext.toolbar.Toolbar',{height:30,
					items:[{
						xtype:'label',
						text:common.label.alternativeField//备选指标
					}]});
				selectField_me.leftGrid.addDocked(menubar);
			}
			Ext.getCmp('leftPanelId').add(selectField_me.leftGrid);
		 },
		 
		 //初始化右侧面板
		 initRightPanel:function(selectedlist){
			//目标数据store
				var rightStore = Ext.create('Ext.data.Store', {
					storeId: 'rightStoreId',
					fields:['dataName','dataValue']
				});	
				
				//解析因式表达式得到的数据
				if(selectedlist){
					Ext.each(selectedlist,function(record,index){
						rightStore.insert(index,[{dataName: record.dataName,dataValue: record.dataValue}]);
					})
				}
				
				if(selectField_me.rightDataList){
					Ext.each(selectField_me.rightDataList,function(record,index){
						rightStore.insert(index,[{dataName: record.dataName,dataValue: record.dataValue}]);
					})
				}
	        	
				//右侧面板
				selectField_me.rightGrid = Ext.create('Ext.grid.Panel', {
					store:rightStore,
					width:this.panelWidth,
			    	height: this.panelHeight,
			    	bufferedRenderer:false,
			    	border:true,
			    	multiSelect:true,
			    	forceFit:true,
			    	hideHeaders:true,
			        viewConfig: {
		    	        plugins: {
		    	            ptype: 'gridviewdragdrop'
		    	        }
		    	    },
					columns: [
					    { text: common.label.itemName,menuDisabled:true, dataIndex: 'dataName'},//项目名称
						{ text: common.label.itemName,menuDisabled:true, dataIndex: 'dataValue',hidden:true}
						
					],
					listeners:{
	        	    	'celldblclick':function(grid, td,cellIndex,record,tr,rowIndex,e){
							selectField_me.delMode();
	            		}
	            },
					renderTo:Ext.getBody()
				});
				
				var menubar1  = Ext.create('Ext.toolbar.Toolbar',{
					items:[{
						xtype:'label',
						text:common.label.hasSelectField//已选指标
					}]});
				selectField_me.rightGrid.addDocked(menubar1);
				
				Ext.getCmp('rightPanelId').add(selectField_me.rightGrid);
		 },
		 
		 //初始化中间按钮
		 initButtonsPanel:function(){
			 var butPanel = Ext.widget({
				    xtype: 'panel',
				    border:false,
				    width: 40,
				    margin:'140 5 0 5',
			    	items:[
						{
							xtype:'button',
		          			text:common.button.addfield, //添加
		          			margin:'0 0 5 0',
		          			handler:function(){
								selectField_me.addMode();
							}
						},
						{
							xtype:'button',
		          			text:common.button.todelete, //删除
		          			handler:function(){
								selectField_me.delMode();
							}
						}
			          	]
			    });
			 
			 Ext.getCmp('buttonId').add(butPanel);
		 },
		 
		 //将左侧勾选的子集指标添加到右侧panel
		 addMode:function(){
			 var records = selectField_me.leftGrid.getSelectionModel().getSelection();
			 if(records.length == 0){
				 Ext.showAlert(common.msg.selectAddObj);
				 return;
			 };
			 Ext.Array.each(records, function(record, index, countriesItSelf) {
				 var modeValue = record.get('dataValue');
				 //生成要插入的model对象
				 var aimMode = {
						 dataName:record.get('dataName'), 
						 dataValue:record.get('dataValue')
				 };
				 var rowlength = selectField_me.rightGrid.getStore().data.length;
				 var isAdd = true;
				 if(!selectField_me.isQuery){//如果不是查询则不校验右侧是否有重复指标
					 selectField_me.rightGrid.getStore().each(function(item,index,count){ //遍历右侧每一条数据
						 if(item.get('dataValue').toUpperCase().indexOf(modeValue.toUpperCase())!="-1"){
							 isAdd = false;
							 return;
						 }
					 })
				 }
				 if(isAdd) {
					 selectField_me.rightGrid.getStore().insert(rowlength, aimMode);//将选中对象数据插入到指定位置
					 //如果flag=1则在添加的时候左边指标仍存在
					 if(selectField_me.flag != 1)
						 selectField_me.leftGrid.getStore().remove(record);//将选中对象数据插入到指定位置
				 }
			 });
		 },
		 
		 //删除右侧已选指标
		 delMode:function(){
			 var records = selectField_me.rightGrid.getSelectionModel().getSelection();
				if(records.length == 0){
					Ext.showAlert(common.msg.selectDelObj);
					return;
				}
				Ext.Array.each(records, function(record) {
					var aimMode = {
							 dataName:record.get('dataName'), 
							 dataValue:record.get('dataValue')
					 };
					var rowlength = selectField_me.leftGrid.getStore().data.length;
					selectField_me.rightGrid.getStore().remove(record);
					if(selectField_me.flag != 1)//不配置flag参数 删除右边选择指标不插入左侧面板中
						selectField_me.leftGrid.getStore().insert(rowlength, aimMode);//将选中对象数据插入到指定位置
    			}); 
		 },
		 
		 //保存右侧已经选好的薪资项目
		 saveItems:function(){
			 var salarySetIDs = ""
				 selectField_me.rightGrid.getStore().each(function(item,index,count){ //遍历每一条数据
					 salarySetIDs = salarySetIDs + "/" + item.get('dataValue');
				 });

			 var isComputeDep="";
			 if(Ext.getCmp("leftcheckboxId")!=undefined)//所得税管理 是否支持按归属部门计算
                 isComputeDep=Ext.getCmp("leftcheckboxId").getValue()==true?"1":"0"
			 selectField_me.win.close();
			 if(selectField_me.saveCallbackfunc) {
			 	if(selectField_me.imodule==2){//所得税管理
                    var map = new HashMap();
                    map.put("salarySetIDs",salarySetIDs);
                    map.put("isComputeDep",isComputeDep);
                    Ext.callback(eval(selectField_me.saveCallbackfunc), null, [map]);
				}else
                 	Ext.callback(eval(selectField_me.saveCallbackfunc), null, [salarySetIDs]);
             }
		 },
		 
		 //改变下拉框值
		 changeSelect:function(fieldSetid){
			 //获得薪资项目数据store
			 selectField_me.leftGrid.getStore().load({
				 params:{
				 salaryid:selectField_me.salaryid,
				 fieldSetid:fieldSetid,
				 opt:'2'
			 }
			 }); 
		 },
		 
		 //下一步
		 nextStep:function(){
			   var right_fields = new Array();
			   //var right_field_objects = new Array();
			   var rightStore = Ext.data.StoreManager.lookup('rightStoreId');
			   rightStore.each(function(item,index,count){ //遍历每一条数据
				   right_fields.push(item.get('dataValue'));
				   //right_field_objects.push(item.data);
			   });
			   if(right_fields.length == 0){
			   		return;
			   }
			   //selectField_me.selectFieldConfig.rightDataList = right_field_objects;
			   
				var map = new HashMap();
				map.put("info_type", selectField_me.selectFieldConfig.dataMap.info_type);
				map.put("expr", selectField_me.expr);
				//map.put("expression", selectField_me.expression);//liuyz bug26539 解决重新选择指标后因子表达式未实现更新
				map.put("right_fields",right_fields);
				map.put("buttonText",selectField_me.buttonText);
				if(selectField_me.queryType)
					map.put("queryType",selectField_me.queryType);
				else
					map.put("queryType","0");
				map.put("priv",selectField_me.priv);
				map.put("filter_factor",selectField_me.filter_factor);
				map.put("isFilterSelectedExpert",selectField_me.isFilterSelectedExpert );
				//配置参数
				var configObj = new Object();
				configObj.selectFieldConfig = selectField_me.selectFieldConfig;
				configObj.imodule = selectField_me.imodule;
				configObj.type = selectField_me.type;
				configObj.dataMap = map;
				configObj.saveCallbackfunc = selectField_me.saveCallbackfunc;
				configObj.queryCallbackfunc = selectField_me.queryCallbackfunc;
				configObj.isShowResult = selectField_me.isShowResult; 
				configObj.nbases =selectField_me.nbases;
	         	Ext.require('EHR.selectfield.QueryFieldSet', function(){
	         		var scopePanel = Ext.create("EHR.selectfield.QueryFieldSet",configObj);
	         		selectField_me.win.close();
	     		});
		 }
 })