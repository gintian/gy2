/**
*调用方法：

	Ext.Loader.setConfig({
			enabled: true,
			paths: {
				'EHR': '/components/'
			}
		});
		*items为页面中已存在的指标，格式为Ext.encode(json)，json中必须包含itemid、itemdesc、itemtype、fieldsetid、codesetid。如没有指标则不传入
		*items = Ext.encode([{"itemid":"A0104","itemdesc":"曾用名","itemtype":"A","fieldsetid":"A01","codesetid":"0"}]);
		
		*fieldset:'A`B`K`Y' 指标集，根据传入的'A`B`K`Y'加载指标集，如果不加载此类指标集，可以不传入fieldset这个参数
		
		*functionId:'0000002561' 是自定义的指标集，不需要可以不传入。
			*在交易类中需提供两个方法，返回list用于js中store的加载
			*一个是加载自定义的指标集：
			*格式：ArrayList list = new ArrayList();
				  HashMap hm = new HashMap();
	    		  hm.put("fieldsetid", "");
	    		  hm.put("fieldsetdesc", "");
	    		  list.add(hm);
			*一个是加载自定义指标集对应的指标：
			*格式：ArrayList list = new ArrayList();
				  HashMap hm = new HashMap();
				  hm.put("itemid", itemid);
				  hm.put("itemdesc", itemdesc);
				  hm.put("itemtype", itemtype);
				  hm.put("fieldsetid", fieldsetid);
				  hm.put("codesetid",codesetid);
				  list.add(hm);
				  
			*     this.getFormHM().put("data", list);
		*items:为页面中已存在的指标，格式为A0101`A0104，如没有指标可以不传入
		*afterfunc: 回调方法，此处传入方法名，返回值为json。
		*module:调用的模块（此参数提供有单独设置指标权限的模块按模块中设置的权限显示子集指标，目前仅招聘模块使用）
		*titlename:组件名称
		Ext.require('EHR.fielditemmultiselector.Selector', function(){
			Ext.create("EHR.fielditemmultiselector.Selector",{functionId:'0000002561',fieldset:'A`B`K`Y',items:items,afterfunc:'examhall_me.addExamHall'});
		});
**/

Ext.define("EHR.fielditemmultiselector.Selector",{
    require:["EHR.extWidget.proxy.TransactionProxy"],
    
	/**回调函数，返回json**/
	afterfunc:'',
	/**交易类编号**/
	functionId:undefined,
	/**items**/
	items:'',
	/**需要加载的指标集**/
	fieldset:undefined,
	/*自定义备选指标集*/
	customOptionalFields:undefined,
	module:'',
	titlename:'',
	align:'', /*添加按钮居中*/
	//自定义 功能按钮
	funTools:undefined,
	
	constructor : function(config) {
		config.titlename = config.titlename?config.titlename:'请选择';
		Ext.apply(this,config);
		
		if(Ext.isString(this.items))
			this.items = Ext.decode(this.items);
		this.width=600;
		this.initComponent();
	},
	
	initComponent:function(){
	     //生成唯一id，将组件注册到 ext组件管理对象中，方便后面调用
	     this.id = Ext.id(this,"ext-selector");
	     Ext.ComponentManager.register(this);
	
	      
	     this.selectedItemIds = ",";
	     if(this.items)
	     for(var i=0;i<this.items.length;i++){
	          this.selectedItemIds+=this.items[i].itemid+",";
	     }
	
	     this.fieldSetCombo =  this.createCombox();
	     this.optionalPanel = this.createOptionalPanel();
	     this.selectedPanel = this.createSelectedPanel();
	     this.funTools = this.createFunTools();
	     
	     this.showWindow();
	},
	
	
	//创建指标集选择下拉框
	createCombox:function(){
	
		var me = this,
		     selectBoxStore,extraSelectStore;
		     
	      
	      if(this.fieldset){
			  //初始化下拉框的store
		      selectBoxStore=Ext.create('Ext.data.Store', {
					fields:['fieldsetid','fieldsetdesc'],
					autoLoad:false,
					proxy:{
						type: 'transaction',
						functionId:'ZJ100000122',
					    extraParams:{
							      fieldset:this.fieldset,
							      module:this.module
						},
						reader: {
							type: 'json',
							root: 'data'         	
						}
					}
					
				});
		  }else if(this.functionId){
	           selectBoxStore =  Ext.create('Ext.data.Store', {
					fields:['fieldsetid','fieldsetdesc'],
					autoLoad:true,
					proxy:{
						type: 'transaction',
						functionId:this.functionId,
					    extraParams:{
							      module:this.module,
						      	  type:'1'
						},
						reader: {
							type: 'json',
							root: 'data'         	
						}
					}
				
				});
	      }

	      	 
	      if(!selectBoxStore)
	          return;
	      selectBoxStore.on("load",function(){
	          if(me.customOptionalFields)
	               this.add({fieldsetid:'selector.customSet',fieldsetdesc:'其他'});
	          
	          if(this.getCount()<1)
					return;
			  var value=this.getAt(0).get('fieldsetid');
				this.ownerCombo.setValue(value);
				me.refreshOptionalData(value);
	      });  
	      
	      return Ext.widget("combo",{
						displayField:'fieldsetdesc',
						valueField:'fieldsetid',
						store:selectBoxStore,
						style:'margin-top:5px',
						width:me.width*0.45-2,
						queryMode:'local',
						queryParam : 'fieldsetdesc',
						minChars:1,
						editable:true,
						listeners:{
							render:function(){
							    var combox = this;
								combox.store.ownerCombo = combox;
							    combox.store.load();
							},
							select:function(){
								me.refreshOptionalData(this.value);
							}
						}
	      });
	    
	},
	
	//创建备选指标grid
	createOptionalPanel:function(){
	
	     var me = this;
	     var storeConfig = {
	     	fields:['itemid','itemdesc','itemtype','fieldsetid','codesetid'],
	     	autoLoad:false
	     };
	     if(me.fieldSetCombo){
	        
	        storeConfig.proxy={
				type: 'transaction',
				functionId:this.fieldset?'ZJ100000121':this.functionId,
			    extraParams:{
					      value:''
				},
				reader: {
					type: 'json',
					root: 'data'         	
				}
			};
	     }else{
	        storeConfig.data = me.customOptionalFields;
	     }
	     
	     //待选指标(左侧grid)的store
		 var optionalStore = Ext.create('Ext.data.Store', storeConfig);
	   
		var optionalPanel = Ext.create('Ext.grid.Panel', {
			height:me.fieldSetCombo?290:320,
			width:me.width*0.45-2,
			margin:'5 0 0 0',
			hideHeaders:true,
			autoScroll:true,
			store:optionalStore,
			columnLines:false,
			rowLines:true,
			columns: [{ 
				text:'名称',
				dataIndex: 'itemdesc',
				id:'itemdesc',
				sortable:false,
				flex:5,
				menuDisabled:true,
				renderer: function(value, metaData, record, rowIndex, colIndex, store) { 
					//渲染
					//metaData.style="background: #fff";
					var itemid = record.data.itemid;
					var idz = "add_"+itemid;
					var id ="doc_"+itemid;
      				var flag = '添加';
      				if(me.selectedItemIds.indexOf(","+itemid+",")!=-1)
      				   flag='已添加';	
      				//if(!Ext.isEmpty(document.getElementById('docc_'+itemid)))
      				//	flag='已添加';	
      				var val = '<div id='+id+' style="float:left;height:30px;line-height:30px;" >&nbsp;&nbsp;'+value+'</div>'
      				+'<div style="float:right;height:30px;line-height:30px;"><a id='+idz+'   style="display:none" >'+flag+'</a></div>';
      				return val;
   				}
			}],
			listeners:{
				'itemclick':function(obj,record,item,index){
					me.insertIntoPanel(record);
				},
				//grid中column的鼠标悬停事件
				'itemmouseenter':function(obj,record,item,index,e){
					item.firstChild.firstChild.style.backgroundColor='#CFE6FF';
					document.getElementById('add_'+record.data.itemid).style.display='block';
				},
				'itemmouseleave':function(value,record,item,index){
					item.firstChild.firstChild.style.backgroundColor='#fff';
					document.getElementById('add_'+record.data.itemid).style.display='none';
				}
			}
		});	
		
		return optionalPanel;
	},
	
	//创建选中grid
	createSelectedPanel:function(){
	    var me = this;
	    
		//右侧已选指标的store
		 var selectedStore = Ext.create('Ext.data.Store',{
 	  		 	 fields:['itemid','itemdesc','itemtype','fieldsetid','codesetid'],  
           		 data:me,
           		 proxy: {
			        type: 'memory',
			        reader: {
			            type: 'json',
			            root:'items'
			        }
			    }
 	  		 });
 	  	  var selectedPanel = Ext.create('Ext.grid.Panel', {
			height:320,
			width:me.width*0.45-10,
			hideHeaders:true,
			autoScroll:true,
			store:selectedStore,
			id:'selectGrid',
			columnLines:false,
			rowLines:true,
			viewConfig:{
				plugins:{
					ptype:'gridviewdragdrop',
					dragText:common.label.DragDropData
				}
			},
			enableDragDrop: true,  
	        dropConfig: {  
	            appendOnly:true  
	        },  
			columns: [{ 
				header:'名称',
				dataIndex: 'itemdesc',
				sortable:false,
				flex:5,
				menuDisabled:true,
				renderer: function(value, metaData, record, rowIndex, colIndex, store) { 
					//渲染
					//metaData.style="background: #fff";
					var itemid = record.data.itemid;
					var idz = "addd_"+itemid;
					var id ="docc_"+itemid;
      				var val = '<div id='+id+' style="float:left;height:30px;line-height:30px;" >&nbsp;&nbsp;'+value+'</div>'
      				+'<div style="float:right;height:30px;line-height:30px;"><a id='+idz+' href="javaScript:Ext.getCmp(\''+me.id+'\').deleteField(\''+record.data.itemid+'\',\''+record.data.itemdesc+'\')" style="display:none" >删除</a></div>';//
      				return val;
   				}
			}],
			listeners:{
				//grid中column的鼠标悬停事件
				'itemmouseenter':function(obj,record,item,index,e){
					item.firstChild.firstChild.style.backgroundColor='#CFE6FF';
					me.onMouseover(record.data.itemid,2);
				},
				'itemmouseleave':function(value,record,item,index){
					item.firstChild.firstChild.style.backgroundColor='#fff';
					me.onMouseleave(record.data.itemid,2);
				}
			}
		});	
		
		return selectedPanel;
	
	},
	
	//创建工具按钮
	createFunTools:function(){
	        var me = this;
	        if(me.funTools)
	            return me.funTools;
	            
			var funTools = ['->' ,
			   			{
						xtype:'button',
					 	text:'确定',
					 	id:'buttonid',
					 	style:'margin-right:6px;',
					 	listeners:{
							'click':function(){
								var jsonData = Ext.encode(me.getSelectedFields()); 
								if(me.afterfunc)
									Ext.callback(eval(me.afterfunc),null,[jsonData]);
								me.window.close();
							}
					 	}
					}
	   			];
		if("center"==me.align){
			funTools.push('->');
		}
		
		return funTools;
	},
	
	showWindow:function(){
	      
	      this.optionalPanel.flex=10;
	      this.window = Ext.create('Ext.Window',{
			title:this.titlename,
			width:580,
			height:450,
			layout:'table',
			modal: true,
			resizable: false,  
			items:[{
				xtype:'container',
				layout:'vbox',
				border:false,
				style:'margin-left:10px;margin-top:10px',
				width:this.width*0.45,
				items:[{xtype:'label',text:'可选指标'},this.fieldSetCombo,this.optionalPanel]
			},{
				xtype:'panel',
				width:1,
				style:'margin-left:10px;margin-top:30px',
				bodyStyle: 'border-width:0 0 0 1px;',
				height:320
			},{
				xtype:'panel',
				border:false,
				style:'margin-left:10px;margin-top:10px;',
				items:[{
					xtype:'panel',
					border:false,
					style:'margin-bottom:5px',
					html:'<div style="float:left"><a style="color:#000000;">已选指标</a></div>'
				},this.selectedPanel]
			}],
			buttonAlign:'center', 
			bbar:this.funTools
		}).show();
	},
	
	refreshOptionalData:function(value){
	   
	   var optionalPanelStore = this.optionalPanel.store;
	   
	   if(value=='selector.customSet'){
	       
	       optionalPanelStore.removeAll();
	       optionalPanelStore.add(this.customOptionalFields);
	       return;
	   }
	
		var extraParams="";
		if(this.fieldset){
			extraParams ={value:value,module:this.module}
		}else{
			 extraParams={value:value,type:'2'}
		}
		
		optionalPanelStore.getProxy().extraParams=extraParams;
		optionalPanelStore.load();
	
	},
	
//鼠标悬停
	onMouseover:function(itemid,flag){
		if(flag==1){
			document.getElementById('add_'+itemid).style.display='block';
		}else{
			//FFF8D2
			document.getElementById('addd_'+itemid).style.display='block';
		}
	},
	onMouseleave:function(itemid,flag){
		if(flag==1){
			document.getElementById('add_'+itemid).style.display='none';
		}else{
			document.getElementById('addd_'+itemid).style.display='none';
		}
	},

	
	//将选中的数据加载到右侧grid中
	insertIntoPanel:function(record){
		var flag = document.getElementById('add_'+record.data.itemid).innerHTML;
		if(flag!='已添加'){
			var store = this.selectedPanel.getStore();
			store.add({
				itemid:record.data.itemid,
				itemdesc:record.data.itemdesc,
				itemtype:record.data.itemtype,
				fieldsetid:record.data.fieldsetid,
				codesetid:record.data.codesetid,
				formatlength:record.data.formatlength
			});
			document.getElementById('add_'+record.data.itemid).innerHTML='已添加';
		}
	},
	
	//删除选中指标
	deleteField:function(itemid,itemdesc){
	   	var store = this.selectedPanel.getStore();
			var record = store.findRecord('itemid',itemid);
			var arr = new Array();
			
			if(!Ext.isEmpty(document.getElementById('add_'+itemid)))
				document.getElementById('add_'+itemid).innerHTML='添加';
			
			if(this.selectedItemIds.indexOf(","+itemid+",")!=-1){
			     Ext.Msg.confirm("提示信息",itemdesc+"指标已经使用，确定要删除吗？",function(btn){ 
						if(btn=="yes"){ 
							store.remove(record);
						} 
					});
				 return;
			}
			
			store.remove(record);
	 },
	 
	 //获取选中指标
	 getSelectedFields:function(){
	 		return Ext.pluck(this.selectedPanel.getStore().data.items, 'data'); 
	 },
	 //用于注册组件使用
	 getId:function(){
     	return this.id;
	 }

});