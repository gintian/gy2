/**
 * 职称评审	筛选条件
 *@author haosl
 *@date	20160719 
 */
Ext.Loader.setConfig({
    enabled: true,
    paths:{
	'EHR.extWidget.field':'/components/extWidget/field'
  }
});
 Ext.define('FilterConditionsURL.FilterConditions',{
 	requires:["EHR.extWidget.field.DateTimeField"],
 	panelConfig:undefined,	//组装panel
 	planStore:undefined,	//方案名称
 	condStore:undefined,	//专家指标条件
 	subModuleId:undefined,	//取自标签hrms:tableFactory属性submoduleid值
 	itemStore:undefined,	//查询指标store
 	columuList:undefined,	//指标列,用于将显示指标的名称（根据指标id显示指标名称）
 	constructor:function(config){
 		me_conditons = this;
 		this.subModuleId=config.subModuleId;
 		me_conditons.init();
 		me_conditons.panelConfig = me_conditons.createPanel();
 		me_conditons.createMainWin();
 	},
 	init:function(){
 		me_conditons.itemStore = me_conditons.getItemStore();	//获得查询指标的store
 	},
 	 	/**
 	 * 创建布局方案名称panel
 	 * 返回左右两侧的布局panel
 	 */
 	createPanel:function(){
		//方案名称store
 		var planStore = me_conditons.createPlanStore();
 		//左侧方案名称grid
 		var gridPanel_plan = Ext.create('Ext.grid.Panel',{
 			id:'planNameId',
 			store:planStore,
			selModel:'checkboxmodel',
 			scrollable:true,
 			plugins: {
 						ptype: 'cellediting',
 						clicksToEdit: 2,
 						listeners:{
 							edit:function(editor, e){
								var record = e.record;
								//去掉修改标志   haosl 20160822
								record.dirty=false; 
								record.commit(); 
							}
 						}
 					 },
 			columns:[{
       					header: '名称',
       					dataIndex: 'planName',
       					flex: 1, 
       					editor:{
       						xtype:'textfield',
       						emptyText:'请填写名称'
       					}
					}],
			listeners:{
				//方案名称panel的行单击事件处理
			   'select':me_conditons.generateCondStore,
			    'afterrender':function(panel){
			    	var store = panel.getStore();
			    	if(store.getCount()==0)
			    		return;
			    	panel.getSelectionModel().select(0);
			    }
		    }
	
 		})
 		// 布局grid的panel
 		var panel = Ext.create('Ext.panel.Panel',{
			border:false,
			layout:'fit',
			margin:'8,8,8,0',
			items:gridPanel_plan,
			buttonAlign:'center',
			fbar:[{
					text:'新增',
					minWidth:20,
				    handler:me_conditons.addPlan
				  },
				  {
				    text:'删除',
				    minWidth:20,
					handler:me_conditons.deletePlan
		          }]
		     
 		})
 		var left_panel = Ext.create('Ext.panel.Panel',{
 			layout:'fit',
 			border:1,
 			width:'35%',
 			height:'100%',
 			margin:'7 5 5 5',
 			items:panel
 			
 		})
 		//关系符号store
 		var exprStore = Ext.create('Ext.data.Store',{
           fields:['cha'],
           data:[{cha:'='},{cha:'<>'},{cha:'>'},{cha:'>='},{cha:'<'},{cha:'<='}]
        });
 		//专家指标条件grid
 		var gridPanel_conditons = Ext.create('Ext.grid.Panel',{
 			id:'conditonsId', 
 			border:false,
 			enableColumnMove:false,
 			sortableColumns:false,
 			columnLines:true,//增加表格线	haosl20160822
 			scrollable:true,
 			selModel:new Ext.create('Ext.selection.RowModel',{mode:'SINGLE'}),
 			//列监听器，监听列单击事件
 			listeners:{
 				cellclick:function(){
					var cellIndex = arguments[2];
					if(cellIndex==0){
						var record = arguments[3];
						var columnVlue = record.get('operator');
						if(columnVlue=='或'){
							record.set("operator","且"); 
						}else if(columnVlue=='且'){
							record.set("operator","或"); 
						}
						//去掉表格的修改标记  haosl 20160822
						record.dirty=false; 
						record.commit();
					}else if(cellIndex==4){//第五列 触发删除筛选条件的事件
						me_conditons.deleteCond();
					}
 				}
 				
 			},
 			plugins: {ptype: 'cellediting', 
 					  clicksToEdit: 1,
 					  listeners : {
 					  			//编辑’查询值‘之前检查指标是不是代码型的，是则修改编辑器为代码型
								beforeedit : me_conditons.beforeEdit,
								edit:function(editor, e){
									var record = e.record;
									//去掉修改标志   haosl 20160822
									record.dirty=false; 
									record.commit(); 
								}
							}
 					},
 			columns:[
 						{text: '逻辑符',align:'center', dataIndex: 'operator',width:'14.9%'},
 						{text: '查询指标',align:'center', dataIndex: 'itemdesc',width:'29.9%',
 						 editor:{
 						 	xtype:'combo',
 						 	store:me_conditons.itemStore,
 						 	selectOnTab:true,
 						 	displayField:'itemdesc',
 						 	valueField:'itemdesc',
 						 	editable:false,
 						 	allowBlank:false,
 						 	listeners:{
 						 	 	//根据指标的不同类型修改查询值的编辑器
		        		  		'select':function(combo,record,index){
    			        			var records = Ext.getCmp('conditonsId').getSelectionModel().getSelection();
    			        			var rec = records[0];
    			        			var index = Ext.getCmp('conditonsId').getStore().indexOf(record);	//获得所选记录的行号
    			        			rec.set('selectValue','');
    			        			rec.set('itemid',record.get('itemid'))
		        	  			}
		        	       }
 						 }
 						},
 						{text: '关系符',align:'center', dataIndex: 'cha',width:'14.9%',
 						 editor:{
 						 	 	xtype:'combo',
				        	    store :exprStore,//数据源   
				        	    selectOnTab:true,
				        	    displayField : 'cha',//显示下拉列表数据值   
				        	    valueField : 'cha',//提交时下拉列表框的数据值   
				        	    editable:false,
				        	    allowBlank:false
		        	     	}
 						 },
 						{text: '查询值',align:'center', dataIndex: 'selectValue',width:'29.9%',
 						 editor:'textfield',
 						 renderer :function(value){
 						 	//如果值是代码类型的格式"key'value",需要处理
					 			if(value==null){
					 				return "";
					 			}
								if(value!=''){
									var arr = value.split('`');
									if(arr.length==2){
										text = arr[1];//根据指标的查询
										return '<span title='+text+'>'+text+'</sapn>';//提示被隐藏的内容 haosl20160823
									}else{
										return '<span title='+value+'>'+value+'</span>';//提示被隐藏的内容 haosl20160823
									}
								}else{
									return value;
								}
					 		}
						},
						{text: '',align:'center',dataIndex: 'del',width:'9.6%'}
 				   	]
 		})
 		//存放条件的gridpanel haosl 20160822
 		var conditonsPanel = Ext.create('Ext.panel.Panel',{
			margin:'8,8,8,0',
			layout:'fit',
			items:gridPanel_conditons,
			buttonAlign:'center',
			fbar:[
 					{
						text:'增加',
						minWidth:20,
					    handler:me_conditons.addCond
				 	},
 					{
						text:'保存',
						minWidth:20,
					    handler:me_conditons.saveCond
				  	}
				 ]
 		});
 		var right_panel =  Ext.create('Ext.panel.Panel',{
 			id:'condPanel',
 			width:'64.5%',
 			margin:'7,5,0,0',
 			height:'100%',
 			layout:'fit',
 			items:conditonsPanel
 		})
 		return [left_panel,right_panel];
 	},
 	/**
 	 * 创建主窗口
 	 */
 	createMainWin:function(){
 		Ext.create('Ext.window.Window',{
 			id:'filterConditionsWin',
 			modal:true,
 			title:'筛选条件',
 			width:840,
			height:460,
			border:false,
			buttonAlign:'center',
			layout:'hbox',
			items:me_conditons.panelConfig
 		}).show();
 	},
 	/**
 	 * 获得查询指标数据
 	 */
 	getItemStore:function(){
 		var map = new HashMap();
 		map.put('type','5');
 		map.put('subModuleId',me_conditons.subModuleId);
 		var dataArr = new Array();
 		Rpc({functionId:'ZC00005005',async:false,success:function(responseValue){
 				var value=responseValue.responseText;
				var data=Ext.decode(value).data;
				for(var i=0;i<data.length;i++){
					var map = data[i];					
					var obj = new Object();
					obj.itemid = map.itemId;
					obj.itemdesc = map.itemDesc;
					obj.itemtype = map.itemType;
					obj.codesetid = map.codeSetId;
					dataArr.push(obj);
				}
 			}
 		},map);
 		return Ext.create('Ext.data.Store',{
							id:'planNameStore',
							fields:['itemid','itemdesc','itemtype','codesetid'],
							data:{'items':dataArr},
							proxy: {
								type: 'memory',
								reader: {
									type: 'json',
									root: 'items'
								}
							}
					  });
 	},
 	/**
 	 * 创建方案名称store
 	 * @return store
 	 */
 	createPlanStore:function(){
 		var	planDataArr = new Array()	//方案名称数据
 		var map = new HashMap();
		map.put("type","1");//1为查询，2为保存,3为删除
		map.put("subModuleId",me_conditons.subModuleId);
		Rpc({functionId:"ZC00005005",async:false,success:function(responseValue){
				var value=responseValue.responseText;
				var map=Ext.decode(value);
				var planDatas = map.querySchemeData;
				for(var i=0;i<planDatas.length;i++){
					var plan = planDatas[i];
					//填充方案名称数据
					var obj = new Object();
					obj.planId = plan.id;
					obj.planName = plan.name;
					planDataArr.push(obj);
				}
			}
		},map);
		return Ext.create('Ext.data.Store',{
							id:'planNameStore',
							fields:['planId','planName'],
							data:{'items':planDataArr},
							proxy: {
								type: 'memory',
								reader: {
									type: 'json',
									root: 'items'
								}
							}
					  });
 	},
 	/**
 	 * 新增方案
 	 */
 	addPlan:function(){
 		var planGrid = Ext.getCmp('planNameId');
		var store = planGrid.getStore();
		var count = store.getCount();
		var cellediting = planGrid.findPlugin('cellediting');//获得编辑器组件
		if(count>0){
			var record = store.getAt(count-1);//获得最后一条记录
			var planName = record.get("planName");
			if(planName==undefined || planName==""){
				cellediting.startEditByPosition({ row: count-1, column: 1});
				return;
			}
		}
		var selectRecords = store.insert(count,{planName:''});
		if(selectRecords){
			planGrid.getSelectionModel().select(selectRecords);	//选中最后新增的记录
			cellediting.startEditByPosition({ row: count, column: 1});
		}
 	},
 	/**
 	 * 删除方案
 	 */
 	deletePlan:function(){
 		if(Ext.getCmp('planNameId').getSelectionModel().getSelection().length==0){//没有选中的记录
			 Ext.showAlert('请选择记录！');
			 return;
		}
		Ext.showConfirm('您确定要删除所选条件吗？',function(bool){
			if(bool=='yes'){
				var records = Ext.getCmp('planNameId').getSelectionModel().getSelection();
    	     	var map = new HashMap();
    	     	var ids = '';
    	     	for(var i =0;records && i<records.length;i++){
    	     		//删除多条
    	     			var planId = records[i].get("planId")
    	     		if(planId){
	    	     		if(i==0){
	    	     			ids = planId;
	    	     		}else{
		    	     		ids += "`"+planId;
	    	     		}
    	     		}
    	     	}
    	    	map.put("ids",ids);
    	     	map.put("type",'3');
    	     	map.put("subModuleId",me_conditons.subModuleId);
    	    	Rpc({functionId:"ZC00005005",success:function(){
    	    		var store = Ext.getCmp('planNameId').getStore();
    	    	    store.remove(records);
    	    	    var count =store.getCount();
    	    	    if(count==0){
    	    	    	//左边没有方案时  清空右边的数据
	    	    		var store = Ext.create('Ext.data.Store',{
							fields:['operator','itemdesc','itemid','cha','selectValue','del']
					  	});
    	    	    	Ext.getCmp('conditonsId').reconfigure(store);
    	    	    }else{
    	    	    	//删除完成后默认选择第一条记录 haosl 20160822
    	    	    	Ext.getCmp('planNameId').getSelectionModel().select(0);
    	    	    }
    	    	},scope:this},map);
			}
		});
 	},
 	/**
 	 * 组装右侧筛选条件的store数据
 	 * @param {} me
 	 * @param {} record
 	 */
 	generateCondStore:function(me,record){
 		var condDataArr = new Array();
		var id = record.get("id");	//方案id
		
		//重新设置右侧panel的store
		var condStore_temp = Ext.data.StoreManager.lookup('condStore'+record.get("id"));
		if(condStore_temp){
			//condStore_temp有值，
			Ext.getCmp('conditonsId').setStore(condStore_temp);
		}else{
			var map = new HashMap();
			var planId = record.get("planId");
			map.put("type","1");//1为查询，2为保存,3为删除
			map.put("subModuleId",me_conditons.subModuleId);
			Rpc({functionId:"ZC00005005",async:false,success:function(responseValue){
					var value=responseValue.responseText;
					var map=Ext.decode(value);
					var planDatas = map.querySchemeData;
					if(!planDatas){
						return;
					}
					for(var i=0;i<planDatas.length;i++){
						var plan = planDatas[i];
						if(plan.id==planId){
							var conds = plan.cond.split('`');
							for(var j = 0;j<conds.length;j++){
							    if(conds[j].length<1){
	    	    	      		   continue;
							    }
				    	      	var cha = '=';
				    	        if(conds[j].indexOf('<>')>-1)
				    	    	        cha = "<>";
				    	        else if(conds[j].indexOf('>=')>-1)
				    	    	   		cha = ">=";
				    	        else if(conds[j].indexOf('<=')>-1)
				    	    	   		cha = "<=";
				    	        else if(conds[j].indexOf('>')>-1)
				    	    	   		cha = ">";
				    	        else if(conds[j].indexOf('<')>-1)
				    	    	   		cha = "<";
				    	        var itemid = conds[j].split(cha)[0];
				    	        var itemdesc = me_conditons.getitemdesc(itemid);
				    	        var value = conds[j].split(cha)[1];
				    	        var expr = plan.exp;
				    	        //截取公式运算符
				    	        var operator =expr.substring(2*j-1,2*j); 
				    	        if(operator=='+'){
				    	        	operator = '或';
				    	        }else if(operator=='*'){
				    	        	operator = '且';
				    	        }
								var html_del = "<img title='删除条件' style='width:16px;height:16px;cursor:pointer' src='/images/del.gif'>";
								var condConfig = new Object();	//存放右侧数据
								condConfig.operator = operator;
								condConfig.planId = planId;
								condConfig.itemdesc = itemdesc;
								condConfig.itemid = itemid;
								condConfig.cha = cha;	//关系符
								var comboEditor = Ext.getCmp('conditonsId').getColumns()[1].getEditor();	//获得查询指标的列编辑器
								var itemstore = comboEditor.getStore();	//获得指标下拉框的store
								for(var x = 0;x<itemstore.getCount();x++){
									var itemrecord = itemstore.getAt(x);
									if(itemid == itemrecord.get('itemid') ){//获得选中的指标的record
										var itemtype = itemrecord.get('itemtype');
										var codesetid = itemrecord.get('codesetid');
										//代码型的将值拼接成“ 代码id`代码名称” 类型的值
										if(itemtype=='A'&&codesetid != '0'){
											var map = new HashMap();
											map.put("type","4");//1为查询，2为保存,3为删除
											map.put("subModuleId",me_conditons.subModuleId);
											map.put("objs",[{codeset:codesetid,value:value,cond:'key'}]);
											Rpc({functionId:"ZC00005005",async:false,success:function(resp){
												condConfig.selectValue=Ext.decode(resp.responseText).map.key;
											}},map);
										}else{
											//不是代码设置原值 
											condConfig.selectValue = value;
										}
									}
								}
								condConfig.del = html_del;
								condDataArr.push(condConfig);
							}
						}
					}
					//condStore_temp没有值证明数据需要从数据库中拿
					var condStore = Ext.create('Ext.data.Store',{
									storeId:'condStore'+record.get("id"),
									fields:['operator','itemdesc','itemid','cha','selectValue','del'],
									data:{'items':condDataArr},
									proxy: {
										type: 'memory',
										reader: {
											type: 'json',
											root: 'items'
										}
									}
							  });
				   Ext.getCmp('conditonsId').setStore(condStore);
				}
			},map);
		}
 	},
 	/**
 	 * 根据itemid获得指标名称
 	 */
 	getitemdesc:function(itemid){
 		var store = me_conditons.itemStore;
 		for(var i = 0;i<store.getCount();i++){
 			if(store.getAt(i).get('itemid')==itemid){
 				var itemdesc = store.getAt(i).get('itemdesc');
 				return itemdesc;
 			}
 		}
 		return itemid;
 	},
	/**
	 *添加筛选条件
	 */
	 addCond:function(){
	 	var store = Ext.getCmp('conditonsId').getStore();
	 	var planStore = Ext.getCmp('planNameId').getStore();
	 	if(!planStore || planStore.getCount()==0){
	 		Ext.showAlert('请在左侧添加方案!');
	 		return;
	 	}
	 	var records = Ext.getCmp('planNameId').getSelectionModel().getSelection();
	 	var planName = records[records.length-1].get('planName');
	 	if(!planName || planName===''){
	 		Ext.showAlert('请输入左侧方案名称!');
 			//方案名称不能为空
	      	var cellediting = Ext.getCmp('planNameId').findPlugin('cellediting');
	      	cellediting.startEditByPosition({ row: count, column: 1});
	      	return;
	 	}
	 	var count = store.getCount();
	 	var html_del = "<img style='width:16px;height:16px;cursor:pointer' src='/images/del.gif'>";
	 	var record = {'operator':'且','itemdesc':'','itemid':'','cha':'=','selectValue':'','del':html_del};
	 	if(count==0){
	 		var record = {'operator':'','itemdesc':'','itemid':'','cha':'=','selectValue':'','del':html_del};
	 	}
	 	
	 	var cellediting = Ext.getCmp('conditonsId').findPlugin ('cellediting');
	 	//没有选择指标时 提示选择指标
	 	//取得条件列表的最后一条数据,判断是否选择指标
	 	if(count>0){
		 	var rec = store.getAt(count-1);
		 	if(rec.get("itemid")==""){
		 		cellediting.startEditByPosition({ row: count-1, column: 1});
		 		return;
		 	}
	 	}
	 	store.insert(count,new Ext.create('Ext.data.Model',record));
	 	cellediting.startEditByPosition({ row: count, column: 1});
	 },
	 /**
	  * 删除筛选条件
	  */
 	 deleteCond: function(){
		var store = Ext.getCmp('conditonsId').getStore();
		var record = Ext.getCmp('conditonsId').getSelectionModel().getSelection()[0];
		store.remove(record);
		if(store.getCount()>0){
			store.getAt(0).set('operator','');
		}
 	 },
	 /**
	  * 添加筛选条件
	  */
 	 beforeEdit:function(editor , context){
 		//不是查询值列就不执行
 		if(context.colIdx!='3'){
 			return;
 		}
 		var column = context.column;
 		var comboEditor = Ext.getCmp('conditonsId').getColumns()[1].getEditor();	//获得查询指标的列编辑器
 		var itemdesc = context.record.get('itemdesc');
 		if(itemdesc == null || itemdesc == ""){
 			Ext.showAlert('请选择查询指标');
 			return false;
 		}
		var itemrecord = comboEditor.findRecordByValue(itemdesc);	//获得查询指标选中的记录
		var codesetid = itemrecord.get('codesetid');
		var itemtype =itemrecord.get('itemtype');
		if(codesetid == '0'){
			if(itemtype == 'D'){
				//设置日期编辑器
				column.setEditor({xtype:'datetimefield',editable:false,format:'Y-m-d'});
			}else{
				//设置普通文本编辑器
				column.setEditor({xtype:'textfield'}); 
			}
			
		}else{
			//设置选择代码编辑器
			Ext.require(['EHR.extWidget.field.CodeTreeCombox']);
			var codeTreeCombo = Ext.widget('codecomboxfield',{
				border : false,
				width:250,
				codesetid : codesetid,
				ctrltype : '3',
				nmodule : '9',
				onlySelectCodeset:false,
				inputable:true//代码控件支持手动输入查询 （例如   ??1*这样的模糊查询）     haosl 20161116
			});
			column.setEditor(codeTreeCombo);
		}
 	},
 /**
  * 保存筛选条件
  * haosl
  */
   saveCond:function(){
      var planStore = Ext.getCmp('planNameId').getStore();	//获得左侧方案的store
      var unSavePlan = '';
      var status = true;	//保存状态 成功或者失败
   	  for(var i = 0;i<planStore.getCount();i++){
   	  	var planName = planStore.getAt(i).get('planName'); 
   	  	var id = planStore.getAt(i).get('id'); 
   	  	var condStore = Ext.data.StoreManager.lookup('condStore'+id);
   	  	if(!condStore){//condStore==undefined时，证明map中没有该条数据，不用保存
   	  		continue;
   	  	}
   	  	if(Ext.isEmpty(planName)){
   	  		continue;
   	  	}
	     if(condStore.getCount()==0){
	     	unSavePlan+= planName+'、';
    	  	continue;
     	 }
     	 var data = me_conditons.getQueryData(planStore.getAt(i));
     	 if(data==null){
	     	return;
	     }
	     var map = new HashMap();
	      map.put("type","2");//2为保存，1为查询,3为删除
	      map.put("subModuleId",me_conditons.subModuleId);
	      map.put("name",planName);
	      map.put("planId",planStore.getAt(i).get('planId'));
	      
	      map.put('exp',data.expr);
	      map.put('cond',data.conds);
	      Rpc({functionId:"ZC00005005",async:false,success:function(response){
			  var data = Ext.decode(response.responseText);
			  //为保存的方案设置planId haosl 20160823
			  var planDatas = data.querySchemeData;
			  for(var i=0;i<planDatas.length;i++){
					var plan = planDatas[i];
					//填充方案名称数据
					planStore.getAt(i).set("planId",plan.id);
					planStore.getAt(i).set("planName",plan.name);
				}
			  if(data.status=='0'){
				  status = false;
			  }
	      }},map);
   	  }
   	  if(unSavePlan!=''){
	   	  Ext.showAlert("【"+ unSavePlan.substring(0,unSavePlan.length-1) +"】等方案未添加查询条件，未保存！！");
	   	  return;
   	  }
   	  if(status){
   	  	 Ext.showAlert("保存成功！");
   	  }else{
   	  	 Ext.showAlert("保存失败！");
   	  }
   },
   /**
    * 获得筛选条件的数据用以保存
    */
  getQueryData:function(planRecord){
	  var id = planRecord.get("id")
  	  var store = Ext.data.StoreManager.lookup('condStore'+id);
  	  if(!store){
  	  	return null;
  	  }
   	  var conds = "";
   	  var expr = "";
   	  for(var i=0;i<store.getCount();i++){
   	  	//查询指标id
   	  	var rec = store.getAt(i);
   	  	var itemid = rec.get('itemid');
   	  	if(itemid==null || itemid.length==0){
      	  Ext.showAlert('请为方案【'+planRecord.get('planName')+'】设置查询指标！');
      	  return null;
        }
   	  	var cha = rec.get('cha');
   	  	var arr = rec.get('selectValue')==undefined?[]:rec.get('selectValue').split('`');
   	  	var selectValue = arr.length>0?arr[0]:rec.get('');
   	  	conds += itemid+cha+selectValue+'`'
   	  	if(i==0){
   	  		expr = '1';
   	  		continue;
   	  	}
   	  	var operator = store.getAt(i).get('operator');
   	  	if(operator =='或'){
   	  		expr+='+'+(i+1);
   	  	}else if(operator=='且'){
   	  		expr+='*'+(i+1);
   	  	}
   	  }
   	  return {expr:expr,conds:conds};
  }
})