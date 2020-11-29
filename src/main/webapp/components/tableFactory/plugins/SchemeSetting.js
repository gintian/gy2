/**
 * 栏目设置类
 * @author guodd
 * 2015-05-28
 */
Ext.define("EHR.tableFactory.plugins.SchemeSetting",{
    require:['EHR.extWidget.proxy.TransactionProxy'],
	subModuleId:undefined,
	
	/**
	 * 添加指标时指标来源，比如A01..
	 */
	schemeItemKey:undefined,
	itemKeyFunctionId:undefined,
	moduleId:undefined,
	
	/**
	 * 功能显示参数
	 */
	viewConfig:undefined,
	
	
	// 栏目设置window对象
	schemeWindow:undefined,
	
	schemeStore:undefined,
    schemeTable:undefined,
    closeAction:undefined,
    scope:undefined,
    width:680,
    height:500,
    
    
    constructor:function(config){
    		var me = this;
		me.viewConfig={
		    sum:true,//显示汇总设置列
		    order:true,   //显示排序设置列
		    merge:true,  //显示合并设置列
		    lock:true,   //显示锁列设置列
		    publicPlan:false, //显示共有方案设置
		    pageSize:true,  //显示每页条数设置
		    autoSavePublic:false//=true //haosl me.viewConfig.autoSavePublic 为true 是自动保存为公有方案 （高校考勤需求） 2018年11月24日 start
		};
		var viewConfig = config.viewConfig;
		delete config.viewConfig;
		Ext.apply(me,config);
		Ext.apply(me.viewConfig,viewConfig);
		Ext.define("SchemeItemModel",{
			extend:'Ext.data.Model',
			fields:["itemid","displaydesc","is_display","displaywidth","align","is_order","is_sum","itemdesc","mergedesc","is_lock","itemtype","fielsetid","is_removable"],
			idProperty:'itemid'
		});
		
		this.initSchemeWindow();
		
		this.initData();
    },
    
    initSchemeWindow:function(){
    	var me = this;
	    me.schemeTable = me.createPlanGrid();
	    me.schemeWindow = Ext.widget("window",{
	        layout:'fit',
	        modal:true,
			height:me.height,
			title:'栏目设置',
			resizable:true,
			width:me.width,
			y:50,
	        items:me.schemeTable,
	        //复写beginDrag方法，解决表格正在编辑时拖动造成页面混乱
	        beginDrag:function(){
	        	   this.child('gridpanel').findPlugin('cellediting').completeEdit( );
	  	           this.callParent(arguments);
	  	    },
	  	    close:function(){
	  	      if(me.unsave)
	  	        Ext.showConfirm("您已还原为默认方案，是否放弃保存？",function(buttonId){
	  	        		 if(buttonId=='yes')
	  	        		     this.doClose();
	  	        },this);
	  	       else
	  	         this.doClose();
	  	    },
	        listeners:{
               click:{
                   element:'body',
                   fn:me.windowClick,
                   scope:me
               },
               destroy:function(){
            	   if(this.itemSelector){
            		   this.itemSelector.destroy();
            	   }
               },
               scope:me
	        }
	    }).show();
    
    },
    
    createPlanGrid:function(){
         var me = this;
         var selModel = null;
         
		   var tbar  = new Array();
		   tbar.push({text:'保存',handler:me.saveConfig,scope:me,margin:'0 10 0 0'});
		   if(me.schemeItemKey.length>0 ||me.itemKeyFunctionId.length>0){
			   selModel =Ext.create('Ext.selection.CheckboxModel',{
		        	    renderer: function(value, metaData, record, rowIndex, colIndex, store, view) {
		        	    	   if(record.get("is_removable")=='1')
		        	         return '<div class="' + Ext.baseCSSPrefix + 'grid-row-checker" role="presentation">&#160;</div>';
		        	    }
		        });
			   tbar.push({text:'添加',itemId:'addBtn',margin:'0 10 0 0',handler:me.addRecord,scope:me});
			   tbar.push({xtype:'button',text:'删除',margin:'0 10 0 0',handler:me.deleteItems,scope:me});
		   }
		   	tbar.push({text:'恢复默认方案',itemId:'setbackbutton',handler:me.setDefault,scope:me,margin:'0 10 0 0'});
		   	if(me.viewConfig.publicPlan)//有公有权限则显示
		     tbar.push({text:'保存为默认方案',itemId:'savedefaultbtn',handler:me.setSaveDefault,scope:me,margin:'0 5 0 0'});	 //添加保存默认按钮 changxy
		   	   
		  var bbar = undefined;
		  if(me.viewConfig.pageSize){
			  bbar = [];
			  bbar.push({
		        	xtype:'numberfield',itemId:'pageRows',fieldLabel:'每页条数',minValue:1,hideTrigger:true,allowDecimals:false,
		        	labelWidth:60,width:100,value:me.pageRows,maxValue:500,
		        	hidden:!me.viewConfig.pageSize,
		        	listeners:{
		        		blur:function(c){
		        			if(c.value<1){
		        				
		        				if(!me.planData || me.planData.pageRows<1)
		        					c.setValue(20);
		        				else//显示后台配置的默认pagerows   haosl 2017-07-21
		        					c.setValue(me.planData.pageRows);
		        			}
		        			if(c.value>500)
		        				c.setValue(500);
		        		}
		        	}
	        	});
		  }
		  return Ext.widget("gridpanel",{ 
		        forceFit:true,
		        selModel:selModel,
		        dockedItems:{xtype:'toolbar',border:false,dock:'top',items:tbar},
		        bbar:bbar,
		        store:{
		           storeId:"planStore",
	        		   model:'SchemeItemModel'
		        },
		        columnLines:true,
		        columns:[{
		            text:'显示名称',
		            menuDisabled:true,
		            sortable:false,
		            dataIndex:'displaydesc',
		            editor:{allowBlank:false,validator:function(value){if(Ext.getStringByteLength(value)>60)return '长度超出限制！'; else return true;}}
		         },{
		            text:'显示',
		            menuDisabled:true,
		            sortable:false,
		            align:'center',
		            width:40,
		            xtype:'widgetcolumn',
		            widget: {
			           xtype: 'checkbox',
			           style:'margin:0 auto',
			           width:13
			        },
		            dataIndex:'is_display'
		         },{
		            text:'列宽',
		            menuDisabled:true,
		            sortable:false,
		            align:'right',
		            width:50, //changxy 20160625
		            dataIndex:'displaywidth',
		            editor:{xtype:'numberfield',step:10,minValue:10,maxValue:1000,allowDecimals:false,allowBlank:false}
		         },{
		            text:'对齐方式',
		            xtype:'',
		            menuDisabled:true,
		            width:65,
		            sortable:false,
		            dataIndex:'align',
		            editor:{
		               xtype:'combobox',
		               displayField: 'desc',
		               editable:false,
		 			  valueField: 'value',
		               store:{
		                  fields:['value','desc'],
		                  data:[{
		                     value:1,
		                     desc:'左对齐'
		                  },{
		                     value:2,
		                     desc:'居中'
		                  },{
		                     value:3,
		                     desc:'右对齐'
		                  }]
		               }
		            },
		            renderer:function(value){
		               if(value=='1')
		                  return "左对齐";
		               else if(value=='2')
		               	 return "居中";
		               else
		               	 return "右对齐";
		            }
		         },{
		            text:'排序',
		            xtype:'',
		            menuDisabled:true,
		            sortable:false,
		            hidden:!me.viewConfig.order,
		            width:50,
		            dataIndex:'is_order',
		            editor:{
		               xtype:'combobox',
		               displayField: 'desc',
		               editable:false,
		 			   valueField: 'value',
		               store:{
		                  fields:['value','desc'],
		                  data:[{
		                     value:0,
		                     desc:'无'
		                  },{
		                     value:1,
		                     desc:'正序'
		                  },{
		                     value:2,
		                     desc:'倒序'
		                  }]
		               }
		            },
		            renderer:function(value,b,record){
		            	if(record.data.itemtype == 'M'){
		            		return "";
		            	}else{
		            		if(value=='0')
	                  		return "无";
		              		else if(value=='1')
		               	 		return "正序";
		               		else
		               	 		return "倒序";
		            	}
		            }
		         },{
		            text:'合计',
		             menuDisabled:true,
		            sortable:false,
		            hidden:!me.viewConfig.sum,
		            align:'center',
		            width:40,
		            dataIndex:'is_sum',
		            xtype:'widgetcolumn',
		            widget: {
			           xtype: 'checkbox',
			           style:'margin:0 auto',
			           width:13
			        },
			        onWidgetAttach:function(c,w,r){
			               if(r.get('itemtype')!='N')
			                   w.setVisible(false);
			               else
			                   w.setVisible(true);
			        } 
		         },{
		            text:'指标名称',
		             menuDisabled:true,
		            sortable:false,
		            dataIndex:'itemdesc'
		         },{
		            text:'分组合并',
		             menuDisabled:true,
		            sortable:false,
		            hidden:!me.viewConfig.merge,
		            dataIndex:'mergedesc',
		            editor:{allowBlank:true,validator:function(value){if(Ext.getStringByteLength(value)>60)return '长度超出限制！'; else return true;}}
		         },{
		        	 text:'锁列',
		             menuDisabled:true,
		             sortable:false,
		             hidden:!me.viewConfig.lock,
		             width:40,
		             align:"center",
		             dataIndex:'is_lock',
		             renderer:function(value){
		            		 if(value == '1')
		            			 return '<img src="/components/tableFactory/tableGrid-theme/images/locked.gif" />';
		            		 else
		            			 return '&nbsp;';
		            	 
		             }
		         }],
		         plugins:{
		                ptype:'cellediting',
		                clicksToEdit: 1,
		                listeners:{
		                   beforeedit:function( editor, context){
		                       if(context.column.dataIndex=='is_order' && context.record.get('itemtype')=='M')
		                          return false;
		                   	   return true;
		                   }
		                }
		         },
		         viewConfig: {
		            markDirty:false,
					plugins: {
		    			ptype: 'gridviewdragdrop',
		    			dragText: '调整顺序'
					},
					listeners:{
						drop:function(node,dragData,overModel,position){
							if(position == "after"){
								if(overModel.data.is_lock!="1")
								  dragData.records[0].set("is_lock","0");
								else
								  dragData.records[0].set("is_lock","1");
							}else if(position == "before"){
								if(overModel.data.is_lock=="1")
								  dragData.records[0].set("is_lock","1");
								else
									dragData.records[0].set("is_lock","0");
							}
							
						}
					}
				},
				listeners:{
					cellclick:function(a,b,index,record){
						var column = this.getColumnManager().getHeaderAtIndex(index); 
						if(column.dataIndex=='is_display'){
							var is_display = record.get("is_display")=='1'?'0':'1';
							record.set("is_display",is_display);
						}
						else if(column.dataIndex=='is_sum' && record.get("itemtype")=='N'){
							var is_sum = record.get("is_sum")=='1'?'0':'1';
							record.set("is_sum",is_sum);
						}else if(column.dataIndex=='is_lock'){
							me.setLockColumn(record);
						}
					}
				}
		  });

	},
    
    setLockColumn:function (currentRecord){
		var me = this;
		var store = me.schemeTable.store;
		var is_lock = currentRecord.data.is_lock;
		var lockstate = false;
		if(is_lock=='1'){
			Ext.each(store.data.items,function(record){
				if(lockstate)
					record.set("is_lock","0");
				if(record == currentRecord){
					record.set("is_lock","0");
					lockstate = true;
				}
			});
		}else{
			Ext.each(store.data.items,function(record){
				if(!lockstate)
				     record.set("is_lock","1");
				if(record == currentRecord){
					record.set("is_lock","1");
					lockstate = true;
				}
			});
		}
	},
    
    
    initData:function(){
    		this.loadSchemeData(0);
    },
    
    loadSchemeData:function(isShare,sync){
        var me = this;
    		var para = new HashMap();
		Ext.apply(para,{
				subModuleId :me.subModuleId,
				isShare : isShare,
				actionName:'queryScheme'
		});
		Rpc({
			functionId:'ZJ100000001',
			success:function(res){
			    var originId = this.planData?this.planData.schemeId:undefined;
				this.planData = Ext.decode(res.responseText).schemeData;
				if(originId)
				    this.planData.schemeId = originId;
	    		    this.schemeTable.store.setData(this.planData.columnsConfigs);
	    		    if(this.viewConfig.pageSize){
			      var pageRowsObj = this.schemeTable.queryById('pageRows');
				  pageRowsObj.setValue(this.planData.pageRows);
				}
			},
			async:sync?false:true,
			scope:me
		},para);
    
    },
    setDefault:function(){
    		this.loadSchemeData(1,true);
    		this.unsave = true;
    },
	setSaveDefault:function(){
		var me = this;
		var store = this.schemeTable.store;
		if(me.viewConfig.pageSize)
			me.planData.pageRows = me.schemeTable.queryById('pageRows').value; 
	    me.planData.columnsConfigs = Ext.Array.pluck(me.schemeTable.store.data.items,"data");

		var para = new HashMap();
		Ext.apply(para,{
			   subModuleId :me.subModuleId,
			   publicPlan:me.planData,
			   actionName:'save'
		});
		Rpc({
			functionId:'ZJ100000001',
			success:function(){
				Ext.showAlert("保存成功！");
				me.unsave = false;	
			}
		},para);
	},
	saveConfig:function(btn){
	    	btn.setDisabled(true);
		var me = this;
		var store = this.schemeTable.store;
		if(me.viewConfig.pageSize)
			me.planData.pageRows = me.schemeTable.queryById('pageRows').value; 
	    me.planData.columnsConfigs = Ext.Array.pluck(me.schemeTable.store.data.items,"data");

		var para = new HashMap();
		
		//haosl me.viewConfig.autoSavePublic 为true 是自动保存为公有方案 （高校考勤需求） 2018年11月24日 start
		var param = {};
		param.actionName="save";
		param.subModuleId=me.subModuleId;
		if(me.viewConfig.autoSavePublic){
			param.publicPlan=me.planData;
		}else{
			param.personalPlan=me.planData;
		}
		//haosl me.viewConfig.autoSavePublic 为true 是自动保存为公有方案 （高校考勤需求） 2018年11月24日  end
		Ext.apply(para,param);
		Rpc({
			functionId:'ZJ100000001',
			success:function(){
			    me.unsave = false;
				me.schemeWindow.close();
				if(!me.closeAction)
					return;
				Ext.callback(me.closeAction,me.scope);
			}
		},para);
	},
	addRecord:function(b){
		var me = this;
		var filterItems = '';
		me.schemeTable.store.each(function(record){
			filterItems+=record.get("itemid")+",";
		});
		if(me.itemSelector){
			me.itemSelector.store.proxy.extraParams.filterItems = filterItems;
			me.itemSelector.store.reload();
			me.itemSelector.show();
			return;
		}
		var store="";
		var schemelist=me.schemeTable.store.data.items;//获取显示的指标集合 wangb 20170715 28107
 		var items  = new Array();
		for( var i = 0 ; i < schemelist.length ; i++){
			items.push(schemelist[i].data.itemid);//获取显示指标的 itemid集合 wangb 20170715 28107
		}
		if(me.itemKeyFunctionId.length>0){
			store = Ext.create('Ext.data.TreeStore', {
				fields: ['text','id','fieldItemId','fieldItemType','fieldSetId'], 
				autoLoad:true,
				proxy:{
					type:'transaction',
					extraParams:{
						fieldItemId:me.schemeItemKey,
						isCheckBox:true,
						filterItems:filterItems,
						moduleId:me.moduleId,
						schemeList:items //获取显示指标的 itemid集合 传入后台 wangb 20170715 28107
					},
					functionId:me.itemKeyFunctionId,
					reader:{
						idProperty:'id'
					}
				}
			});
		}else{
			store = Ext.create('Ext.data.TreeStore', {
				fields: ['text','id','fieldItemId','fieldItemType','fieldSetId'], 
				autoLoad:true,
				proxy:{
					type:'ajax',
					extraParams:{
						fieldItemId:me.schemeItemKey,
						isCheckBox:true,
						filterItems:filterItems,
						moduleId:me.moduleId,
						scheme:items.toString() //获取显示指标的 itemid集合转成String类型 传入后台 wangb 20170725 29936
					},
					url:'/servlet/gridtable/GetFieldItemServlet',
					reader:{
						idProperty:'id'
					}
				}
			});
		}
		me.itemSelector = Ext.widget('treepanel',{
    		    title:'指标',
            float: true,
            width:250,height:300,
            x:54,
   	        y:62,
   	        style:'position:absolute;z-index:inherit',
            valueField: 'id',  
            displayField: 'text',
            rootVisible:false,
            useArrows:false,
            store:store,
            bbar:['->',{xtype:'button',text:'确定',handler:me.addItems,margin:'0 10 0 0',scope:me},{xtype:'button',text:'取消',handler:me.closeWindow,scope:me},{xtype:'tbfill'}],
            renderTo:me.schemeWindow.getEl()
        });
	},
	closeWindow:function(){
		this.itemSelector.hide();
	},
	addItems:function(){
		var me = this;
		var checkItems = new Array();
		var records = me.itemSelector.getChecked();
	    var record;
    	for(var i=0;i<records.length;i++){
    		var alignValue = 1;
    		record = records[i];
            if("N" == record.get("fieldItemType"))
            	alignValue = 3;
    		//record.set("checked",false);
    		checkItems.push({
    			itemid:record.get("fieldItemId").toLowerCase(),
    			displaydesc:record.get('text'),
    			is_display:'1',
    			displaywidth:100,
    			align:alignValue,
    			is_order:'0',
    			is_sum:'0',
    			itemdesc:record.get("text"),
    			mergedesc:'',
    			is_lock:'0',
    			itemtype:record.get("fieldItemType"),
    			fieldsetid:record.get("fieldSetId"),
    			is_removable:'1'
    		});
    		//record.remove();
    	}
		var mess = '';
		var store = me.schemeTable.store;
		for(var i=0;i<checkItems.length;i++){
			if(store.getById(checkItems[i].itemid.toLowerCase())){
				mess+='<'+checkItems[i].itemdesc+'> ';
				continue;
			}
			store.add(checkItems[i]);
		}
		
		/*添加到栏目设置后，删除指标  wangb 20170715 28107
		for(var i=0;i<records.length;i++){
			records[i].remove();
		}*/
		me.closeWindow();
		if(mess.length>1){
			Ext.Msg.alert('提示','已存在 '+mess);
			return;
		}
		me.schemeWindow.items.getAt(0).getView().focusRow(store.getCount()-1);
		
	},
	windowClick:function(ev,el){
		var me = this;
		//如果选择控件不存在或者隐藏，return
		if(!me.itemSelector || !me.itemSelector.isVisible())
			return;
		//如果触发click的元素是选择控件或者属于添加按钮子元素，return
		if(me.itemSelector.owns(el) || me.schemeWindow.query('button#addBtn')[0].owns(el))
			return;
		//如果点击的是树展开收缩图片或者树checkbox，return
		if(el.className.indexOf('x-tree-checkbox')!=-1 || el.className.indexOf('x-tree-elbow-img')!=-1)
			return;
		//上面都不成立说明点击的是其他不相关的元素，隐藏选择控件
	   	me.itemSelector.hide();
	},
	deleteItems:function(){
    	    var me = this;
       	var select = me.schemeTable.getSelectionModel().getSelection();
       	Ext.each(select,function(r){
       		if(r.get("is_removable")=='1')
       		   me.schemeTable.store.remove(r);
       		   if(me.itemSelector){ 
       		   		var seStore = me.itemSelector.store;
       		   		//栏目删除指标 往添加按钮 里  新增栏目删除的的指标  wangb 20170715 28107
       		   		//'text','id','fieldItemId','fieldItemType','fieldSetId'
       		   		var record = {};
       		   		record.id = r.data.itemid;
       		   		record.text = r.data.itemdesc;
       		   		record.fieldItemId = r.data.itemid;
       		   		record.fieldItemType = r.data.itemtype;
       		   		record.fieldSetId = r.data.fieldsetid;
       		   		record.leaf=true;
       		   		record.checked=false;
       		   		seStore.getRoot().appendChild(record);
				}
       	});
    }
	
});