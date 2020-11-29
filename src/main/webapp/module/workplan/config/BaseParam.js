/**
 * workplan 其他参数页面
 */
 Ext.define('WorkPlanConfigUL.BaseParam',{
 	extend:'Ext.form.Panel',
 	id:'form',
	title:wp.param.otherconf,
	scrollable:'y',
	layout:'vbox',
	border:false,
	bodyPadding:'20 0 10 20',
	constructor:function(){
		wp_baseParam = this;
		wp_baseParam.callParent(arguments);
	 	wp_baseParam.on('beforerender',this.showData,this);//回显数据
	 	wp_baseParam.createPage();
 	},
 	/**
 	 * 创建其他参数页面
 	 */
 	createPage:function(){
 	 	var width = 850;//分割线的宽度，默认850
 		var bodyWidth = Ext.getBody().getWidth();
 		var menuWidth = Ext.getCmp('menuTree').getWidth();
 		width = bodyWidth - menuWidth-140;//120:横线前面label的宽度
 		var hrHtml = '<hr style="width:'+width+'px;border-top:1px solid #C5C5C5; border-bottom:0px; border-left:0px;" />';
 		var hrHtml2 = '<hr style="width:'+(width+24)+'px;border-top:1px solid #C5C5C5; border-bottom:0px; border-left:0px;" />';
 		
 		//是否在version.xml中启用“我的协作任务”功能，如果没有启用，则在参数设置中将不显示协作任务设置。
 		var items = undefined;
 		if(workPlanConfig.isOpenCooperationTaskVersion){
 			items = [{
 				xtype:'container',
 				border:false,
 				layout:'hbox',
 				items:[{
		        	xtype:'label',
		        	text:'协作任务处理模式',
		        	margin:'0 5 0 0'
		        },{ 
		        	xtype: 'displayfield', 
		        	value: hrHtml
	        	}]
 			},{
 			        xtype:'radiogroup',
 			        id:'cooperative_task',
 			        layout:'vbox',
 			        items:
 			        [
 						 { 
 							boxLabel: '发布计划协作任务自动进入协办人工作计划',//发布计划（默认流程）
 							name: 'cooperative_task', 
 							inputValue: '1',
 							checked: true
 						 },
 						 {
 							 boxLabel: '上级审批下属计划，协作任务需协办人上级批准才能进入工作计划', //协作任务申请
 							 name: 'cooperative_task',
 							 inputValue: '2' 
 						 }
 					]
		    }];
 		}
 		
 		var container1 = Ext.widget('container', {
 			border:false,
 			layout:'vbox',
 			items:items
 		});
 		var container2 = Ext.widget('container', {
 			border:false,
 			layout:'vbox',
 			items:[{
 				xtype:'container',
 				border:false,
 				layout:'hbox',
 				
 				items:[{
		        	xtype:'label',
		        	text:'工作计划权重控制',
		        	margin:'0 5 0 0'
		        },{ 
		        	xtype: 'displayfield', 
		        	value: hrHtml
	        	}]
 			},{
 				xtype:'container',
 				border:false,
 				layout:'column',
 				items:[{
		           xtype:'radiogroup',
		           id:'plan_weight',
		           layout:'vbox',
		           listeners:{
						'change':function(){
		        	      //如果选择，不控制权重输入框为不可编辑状态
				          if(this.getValue().plan_weight=='2'){
				        	  Ext.getCmp('from').disable();
				        	  Ext.getCmp('to').disable();
				        	  Ext.getCmp('suf').disable();
				          }else{
				        	 Ext.getCmp('from').enable();
				        	 Ext.getCmp('to').enable(); 
				        	 Ext.getCmp('suf').enable(); 
				          }
						}
			        },
		           items:[
					 { boxLabel: '权重范围', name: 'plan_weight', inputValue: '1',checked: true},//权重控制
					 { boxLabel: '不控制权重范围', name: 'plan_weight', inputValue: '2' }//不控制权重
				   ]
				},{
		        	xtype:'container',
		        	border:false,
		        	layout:'hbox',
		        	items:[
		        	   {
		        		   id:'from',
		        		   fieldLabel:'从',
		        		   labelWidth:12,
		        		   labelSeparator:'',
		        		   validateOnChange:true,
		        		   allowBlank:false,
		        		   validator:wp_baseParam.checkWeightValue,
		        		   name:'from',
		        		   xtype:'textfield',
		        		   labelAlign:'right',
		        		   width:73,
		        		   height:22,
		        		   value:100
		        	   },
		        	   {
		        		   id:'to',
		        		   name:'to',
		        		   fieldLabel:'% 至',
		        		   labelWidth:30,
		        		   labelSeparator:'',
		        		   labelAlign:'right',
		        		   allowBlank:false,
		        		   validateOnChange:true,
		        		   validator:wp_baseParam.checkWeightValue,
		        		   xtype:'textfield',
		        		   height:22,
		        		   width:90,
		        		   value:100
		        	   },
		        	   {
		        		   id:'suf',
		        		   xtype:'textfield',
		        		   style:'margin-left:5px',
		        		   fieldLabel:'%',
		        		   width:10
		        	   }]
			      }]
 			}]
 		});
 		
 		var container3 = Ext.widget('container', {
 			border:false,
 			layout:'vbox',
 			id:'taskSource',
 			items:[{
 				xtype:'container',
 				border:false,
 				layout:'hbox',
 				items:[{
		        	xtype:'label',
		        	text:'工作任务来源',
		        	margin:'0 5 0 0'
		        },{ 
		        	xtype: 'displayfield', 
		        	value: hrHtml2
	        	}]
 			},{
 				xtype:'container',
 				border:false,
 				layout:'hbox',
 				items:[
 					{
 						xtype:'combo',
 						id:"taskSet",
 						queryMode: 'local',  // 解决下拉框首次不能加载store的问题
 						store:wp_baseParam.getTaskData(),
 						labelWidth:75,
 						displayField:'fieldsetdesc',
 						valueField:'fieldsetid',
 						margin:'0 30 20 0',
 						name:'taskSet',
 						editable:false,
 						fieldLabel:'工作任务子集',
 						forceSelection :true,
 						listeners:{
 							render:function(combo){
 								//默认选择第一个
 					         	var store =combo.getStore();
				         		if(!combo.getValue()&& store.getCount()>0)
 					         		combo.setValue(store.getAt(0)).fireEvent('select',combo,store.getAt(0));
				         	},
 							select: function(combo,data){
 					         	if(typeof(data) == 'undefined'){
 					         		return ;
 					         	}
 					         	var fieldsetid = data.data.fieldsetid;
 					         	var taskItemCombo = Ext.getCmp("taskItem");
				         		var taskItemStore = wp_baseParam.searchTaskItem(fieldsetid);
				         		taskItemCombo.setStore(taskItemStore);
				         		if(!taskItemCombo.getValue() && taskItemStore.getCount()>0)
		         					taskItemCombo.setValue(taskItemStore.getAt(0));
 					         }
 						}
 					},{
 						id:'taskItem',
 						queryMode: 'local',  // 解决下拉框首次不能加载store的问题
 						xtype:'combo',
 						labelWidth:75,
 						margin:'0 0 20 0',
 						displayField:'itemdesc',
 						forceSelection :true,
 						name:'taskItem',
 						editable:false,
 						valueField:'itemid',
 						fieldLabel:'工作任务指标',
 						listeners:{
 							render:function(combo){
 								var store = combo.getStore();
 								if(!combo.getValue())
 									combo.setValue(store.getAt(0));
 							}
 						}
 					}
 				]
		    }]
 		});
 		var container4 = Ext.widget('container', {
 			border:false,
 			layout:'vbox',
 			items:[{
 				xtype:'container',
 				border:false,
 				layout:'hbox',
 				items:[{
		        	xtype:'label',
		        	text:'工作任务性质 ',
		        	margin:'0 5 0 0'
		        },{ 
		        	xtype: 'displayfield', 
		        	value: hrHtml2
	        	}]
 			},{
				xtype:'container',
				border:false,
				layout:'hbox',
				items:[
					{
						xtype:'combo',
						queryMode: 'local',  // 解决下拉框首次不能加载store的问题
						labelWidth:125,
						id:'taskTimeSign',
						store:wp_baseParam.searchTaskTimeStore(),
						margin:'0 0 20 0',
						editable:false,
						displayField:'itemdesc',
						forceSelection :true,
						name:'taskTimeSign',
						valueField:'itemid',
						fieldLabel:'工作任务是否计时标识',
						listeners:{
							render:function(combo){
								var store = combo.getStore();
								if(!combo.getValue() && store.getCount()>0)
									combo.setValue(store.getAt(0));
							}
						}
					},
					{
						xtype:'label',
						html:'（*K01岗位信息集代码型指标，关联代码类45）'
					}
				]
			}]
 		});
 		var container5 = Ext.widget('container', {
 			border:false,
 			layout:'vbox',
 			items:[{
 				xtype:'container',
 				border:false,
 				layout:'hbox',
 				items:[{
		        	xtype:'label',
		        	text:'总结填写模式 ',
		        	margin:'0 5 0 0'
		        },{ 
		        	xtype: 'displayfield', 
		        	value: hrHtml2
	        	}]
 			},{
		           xtype:'radiogroup',
		           id:'fillModel',
		           layout:'column',
		           items:[
					 { boxLabel: '文本', width:80, name: 'fillModel', inputValue: '1',checked: true,
					 	listeners:{
					 		change:function(rbox,newVal){
					 			var show_task = Ext.getCmp("show_task");
					 			if(show_task){
					 				if(newVal){
					 					show_task.setHidden(false);
					 				}else{
					 					show_task.setHidden(true);
					 				}
					 			
					 			}
					 			
					 		}
					 	}
					 },
					 { boxLabel: '表格', width:80, name: 'fillModel', inputValue: '2' }
				   ]
				},{
                   xtype : 'checkboxfield',
                   id : 'show_task',
                   boxLabel : '总结显示工作任务',
                   margin:'0 0 0 4',
                   name : 'show_task',
                   inputValue : true
                   
                   }]
 		});
		wp_baseParam.add([container1,container2,container3,container4,container5]);
		var buttons = Ext.widget("container",{
			width:'100%',
			layout:'center',
			margin:'30 0 0 0',
			items:{
					xtype:'container',
					items:[{
						xtype:'button',
			    		text:'保存',
			    		width:80,
			    		margin:'0 10 0 0',
			    		listeners : {
			    	    	click:{
								element:'el',
								fn:function(){
									wp_baseParam.save();
								}
							}
			    	    }
				   },{
					   xtype:'button',
					   width:80,
					   text: '重置',
					   listeners:{
					       click:{
								element:'el',
								fn:function() {
									wp_baseParam.cancelopt()
								}
					       }
				   	   }
				   }]
			}
		});
		wp_baseParam.add(buttons);
 	 },
 	 //回显数据
 	 showData:function(){	
 	   var map = new HashMap();
	   map.put('opt','select'); //=update 保存更新 =select查询数据
       Rpc({functionId:'WP20000001',async:false,success:function(response){
    	   var data = Ext.util.JSON.decode(response.responseText).data;
    	   var cooperative_task = Ext.getCmp("cooperative_task");
    	   if(cooperative_task){
    	   	cooperative_task.setValue({cooperative_task:data.cooperative_task});
    	   }
     	   Ext.getCmp("plan_weight").setValue({plan_weight:data.plan_weight});
     	   if(data.from && data.to){
	     	   Ext.getCmp("from").setValue(data.from); 
	    	   Ext.getCmp("to").setValue(data.to);
     	   }
     	   Ext.getCmp("taskSet").setValue(data.taskSet);
     	   var taskItem = Ext.getCmp("taskItem");
     	   if(data.taskSet){
     		   var taskItemStore = wp_baseParam.searchTaskItem(data.taskSet);
     		   taskItem.setStore(taskItemStore);
     		   if(data.taskItem)//数据库有数据 回显 ，没有则默认显示第一条
     			   taskItem.setValue(data.taskItem);
     	   }
    	   Ext.getCmp("fillModel").setValue({fillModel:data.fillModel});
    	   Ext.getCmp("taskTimeSign").setValue(data.taskTimeSign);
    	   Ext.getCmp("show_task").setValue(data.show_task);
       }},map);
 	},
 	 //处理保存单击事件
 	save:function(event, toolEl, panelHeader) {
		   var form = wp_baseParam.getForm(); 
		   if(!form.isValid()){
			 return;
		   }
		   var map = new HashMap();
		   var formMap = form.getFieldValues();
    	   map.put('opt','save'); //=save 保存更新 =select查询数据
    	   map.put('formMap',formMap);
    	   Rpc({functionId:'WP20000001',async:false,success:function(){
    		   Ext.Msg.show({
    			   title:'提示消息',
    			   msg:'保存成功！',
    			   buttons: Ext.Msg.OK,
    			   icon:Ext.Msg.INFO
    		   });
    	   },failure:function(){
	    	   Ext.Msg.show({
	    		   title:'出错了！！',
	    		   msg:'保存失败！ 请与管理员联系...',
	    		   buttons: Ext.Msg.OK,
	    		   icon:Ext.Msg.ERROR
	    	   });
       }},map);
    },
    //校验输入权重范围的值	
 	checkWeightValue:function(value){
 		var fieldId = this.id;	//此处this代表产生点击事件的组件textfield
 		var num = parseInt(value,10);
 		var regx = /^[1-9]+[0-9]*]*$/;
 		if(!regx.test(num)){
 			return '请输入正整数';
 		}
 		if(fieldId == 'from'){
 			var toValue = parseInt(Ext.getCmp('to').getValue(),10);//获得范围的边界的值
 			if(num>toValue){
 				return '起始值不能大于结束值';
 			}
 		}else if(fieldId == 'to'){
 			var fromValue = parseInt(Ext.getCmp('from').getValue(),10);//获得范围的起始值
 			if(num<fromValue){
 				return '起始值不能大于结束值';
 			}
 		}
 		return true;
 		
 	},
 	// 获得工作任务子集数据源
 	getTaskData:function(){
 		var map = new HashMap();
		map.put("opt","taskSource");
		var dataArr = new Array();
		Rpc({functionId:'WP20000001',async:false,success:function(response){
			var data = Ext.decode(response.responseText).taskData;
			for(var i in data){
				dataArr.push([data[i].fieldsetid,data[i].fieldsetdesc]);
			}
		}},map);
		return Ext.create('Ext.data.Store', {
			id:'taskTimeStore',
			fields:['fieldsetid', 'fieldsetdesc'],
			data:dataArr
		});
	},
	/**
	 * 查询指定工作任务子集的任务指标
	 */
	searchTaskItem:function(fieldsetid){
		var map = new HashMap();
		map.put("opt","taskItem");
		map.put("fieldsetid",fieldsetid)
		var dataArr = new Array();
		Rpc({functionId:'WP20000001',async:false,success:function(response){
			var data = Ext.decode(response.responseText).taskItemData;
			for(var i in data){
				dataArr.push([data[i].itemid,data[i].itemdesc]);
			}
		}},map);
		return Ext.create('Ext.data.Store', {
			id:'taskItemStore',
			fields:['itemid', 'itemdesc'],
			data:dataArr
		});
	},
	/**
	 * 查询工作任务是否计时标识下拉框数据
	 */
	searchTaskTimeStore:function(){
		var map = new HashMap();
		map.put("opt","taskTimeSign");
		var dataArr = new Array();
		Rpc({functionId:'WP20000001',async:false,success:function(response){
			var data = Ext.decode(response.responseText).taskTimeData;
			//加入空选项，方便取消设置计时指标   haosl 2018-2-5
			if(data.length>0){
				dataArr.push(["","　"]);
			}
			for(var i in data){
				dataArr.push([data[i].itemid,data[i].itemdesc]);
			}
		}},map);
		return Ext.create('Ext.data.Store', {
			id:'taskTimeStore',
			fields:['itemid', 'itemdesc'],
			data:dataArr
		});
	},
	/**
	 * 重置
	 */
	cancelopt:function(){
	   var cooperative_task = Ext.getCmp("cooperative_task");
		  if(cooperative_task)
			 cooperative_task.setValue({cooperative_task:'1'});
 	   Ext.getCmp("from").setValue('100');
 	   Ext.getCmp("to").setValue('100');
 	   Ext.getCmp("plan_weight").setValue({plan_weight:'1'});
 	   Ext.getCmp("show_task").setValue(false);
 	   var taskSet = Ext.getCmp("taskSet");
 	   var taskItem = Ext.getCmp("taskItem");
 	   var store = taskSet.getStore();
 	   if(store.getCount()>0){
 		   taskSet.setValue(store.getAt(0));
 		   var fieldsetid =store.getAt(0).data.fieldsetid;
 		   var itemStore =wp_baseParam.searchTaskItem(fieldsetid);
 		   if(itemStore.getCount()>0)
 			   taskItem.setValue(itemStore.getAt(0));
 	   }
 	   Ext.getCmp("fillModel").setValue({fillModel:'1'});
 	   
 	   var taskTimeSign = Ext.getCmp("taskTimeSign");
 	   var taskTimeStore = taskTimeSign.getStore();
 	   if(taskTimeStore.getCount()>0)
 		   taskTimeSign.setValue(taskTimeStore.getAt(0));
		
	}
 })