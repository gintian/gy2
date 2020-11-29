/**
 * 表格控件业务控制类
 * guodd 2016-01-06
 */
Ext.define("ProjectManageTemplateUL.TreeTableController",{
	extend: 'EHR.tableFactory.TableController',
	/**
	 * 列菜单过滤
	 * @param column
	 */
	doColumnFilter:function(column){
	    var me = this;
		var subModuleId = me.ownerBuilder.subModuleId,
	    itemtype = column.columnType,
	    codesetid = column.codesetid,
	    builer = me.ownerBuilder;
	
	    var store = Ext.data.StoreManager.lookup(builer.prefix+'_pageStore');
		delete store.getProxy().extraParams.filterParam;//清除过滤参数
		store.reload();//刷新数据
		
		var codefilter = Ext.getCmp(builer.prefix+"_codeFilter");
		//如果column有operationData 或者 是代码 指标，树形方式过滤
	   if(column.operationData|| (itemtype=='A' && codesetid && codesetid!='0') ){
		  this.columnCodeFilter(codefilter,column);
		  column.filterMark();
		  return;
	   }
	   
	   //先移除树形过滤
	   this.removeCodeFilter(codefilter);
	   
	   var windowConfig = {
		      title:column.text,
		      modal:true,
		      layout:'fit',
		      buttonAlign:'center',
		      //拖动之前先获取一下焦点，防止window内的组件正在编辑状态时拖动丢失元素
		      beginDrag:function(){
		    	     this.focus();
		    	     this.callParent(arguments);
		      },
		      buttons:[{
			    	text:'确定',
			    	handler:function(){
			    		me.filterHandler(this.ownerCt.ownerCt,column,subModuleId);
			    	}
			  },{
				    	text:'取消',
				    	handler:function(){
				    		this.ownerCt.ownerCt.close();
				    	}
			  }]
		};
	   //数值型
	   if(itemtype=='N')
	      this.columnNumberFilter(windowConfig,column.maxValue);
	   else if(itemtype=='D')//日期型
		   this.columnDateFilter(windowConfig,column.format);
	   else//字符型和文本型
		   this.columnStringFilter(windowConfig);
	   //创建过滤window
	   Ext.widget('window',windowConfig).show();
	
	},
	//移除 代码过滤树
	removeCodeFilter:function(codeFilter){
		if(codeFilter){
			codeFilter.column.cancelMark();
			codeFilter.ownerCt.remove(codeFilter,true);
			//清除cookie
			Ext.util.Cookies.clear(this.ownerBuilder.config.subModuleId+"_filter");
		}
	},
	/**
	 * 取消过滤 
	 * @param column grid的column对象（xtype:gridcolumn）
	 */
	cancelColumnFilter:function(column){
		var codefilter = Ext.getCmp(this.ownerBuilder.prefix+"_codeFilter");
		if(codefilter)//代码型过滤清除选择树
			codefilter.ownerCt.remove(codefilter,true);
		var store = Ext.data.StoreManager.lookup(this.ownerBuilder.prefix+'_pageStore');
		delete store.getProxy().extraParams.filterParam;//清除过滤参数
		column.cancelMark();//清除过滤图标
		store.reload();//刷新数据
		Ext.util.Cookies.clear(this.ownerBuilder.config.subModuleId+"_filter");
	},
	
	//代码型过滤窗口
    columnCodeFilter:function(codeFilter,column){
	  	var me = this,
	  	    builder = me.ownerBuilder;
	  	
	  	//保存过滤信息到cookie中，下次进入本界面时自动打开过滤panel
	  	Ext.util.Cookies.set(builder.subModuleId+"_filter",column.dataIndex);
	  	
	  	var tablePanel = Ext.getCmp("port");
	  	if(codeFilter ){
	  	    if(codeFilter.column === column)
	  	        return;
	  	    
	  		codeFilter.setTitle(column.text);
	  		codeFilter.column = column;
	  		if(column.operationData && column.operationData.length>0){
	  			  var childrens = new Array();
		  	  	  Ext.each(column.operationData,function(data){
		  	  		 childrens.push({id:data.dataValue,text:data.dataName,leaf:true,checked:false}); 
		  	  	  });
		  	  	codeFilter.getStore().setRootNode({
		  			expanded: true,
		  	        children:childrens
		  		});
	  		}else{
	  		   codeFilter.getStore().getProxy().extraParams ={
		        				codesetid:column.codesetid,
		        				nmodule:column.nmodule,
		        				ctrltype:column.ctrltype,
		        				codesource:column.codesource,
		        				multiple:true
	  			}
	  			codeFilter.getStore().load();
	  		}
	  		
	  		return;
	  	}
	  	
	  	var store = undefined;
	  	if(column.operationData){
	  		  var childrens = new Array();
		  	  Ext.each(column.operationData,function(data){
		  		 childrens.push({id:data.dataValue,text:data.dataName,leaf:true,checked:false}); 
		  	  });
		  	  store = Ext.create("Ext.data.TreeStore",{
				root: {
				       expanded: true,
				      children:childrens
				}
		  	  });
	  	}else{
	  	  	store = Ext.create("Ext.data.TreeStore",{
	  	  		 proxy:{   
	  	  		  	type:'transaction',
					extraParams:{
				  		codesetid:column.codesetid,
				  		nmodule:column.nmodule,
				  		ctrltype:column.ctrltype,
				  		codesource:column.codesource,
				  		multiple:true
				  	},
				  	functionId:'ZJ100000131'
			    }
			  	  	  
			});
	  	}
	  	tablePanel.add({
	  		xtype:'treepanel',
	  		region:'west',
	  		width:200,
	  		bodyStyle:'border-right:none;',
	  		id:builder.prefix+"_codeFilter",
	  		title:column.text,
	  		column:column,
	  		header:{
	  		   title:column.text,
	  		   height:31,
	  		   padding:'0 10 0 10',
	  		   style:'border-right:0px;background-color:rgb(240, 240, 240) !important;'
	  		   
	  		},
	  		animCollapse:false,
	  		collapseMode:'header',
	  		collapsible:true,
	  		collapseDirection:'left',
			rootVisible:false,
			store:store,
	         listeners:{
	        	 	checkchange:function(){
	        	 		var checked = this.getChecked();
	        	 		filterParam = {
	        	 				field:this.column.dataIndex,
	        	 				itemtype:"C",
	        	 				expr:'or',
	        	 				factor:[]
	        	 		};
	        	 		Ext.each(checked,function(record){
	        	 				 filterParam.factor.push("=`"+record.get("id"));
	        	 		});
	        	 		var store = Ext.data.StoreManager.lookup(builder.prefix+'_pageStore'); 
	        	 		store.getProxy().extraParams = {
	        	 	 		subModuleId:builder.subModuleId, 
	        	 	        filterParam:Ext.encode(filterParam),
	        	 	        flag:"1"
	        	 	    };
	        	 	    store.reload();
	        	 	}
	        	 	
	         }
	  	});
  		
	},
	//数值型过滤窗口
	columnNumberFilter:function(windowConfig,maxValue){
		   windowConfig.width=400;
		   windowConfig.height=200;
		   windowConfig.resizable=false;
		   var states1 = Ext.create('Ext.data.Store', {
	           fields: ['name', 'value'],
	           data : [{"name":"等于", "value":"="},
	                   {"name":"大于", "value":">"},
	                   {"name":"大于等于", "value":">="},
					    {"name":"小于", "value":"<"},
	                   {"name":"小于等于", "value":"<="},
	                   {"name":"不等于", "value":"<>"}]
	          });
	          windowConfig.items={
		   			  xtype:'panel',
		   			  bodyStyle:"border-width:0px 0px 1px 0px;padding:20px 0px 0px 0px;",
		   			  //style:'margin-left:50px', //changxy
		   			  items:[{
		   				  xtype:'panel',
		   				  border:0,
		   				  padding:'0 0 0 70', //changxy 
		   				  layout:{
		   					  type:'table',
		   					  columns:2
		   				  },
		   				  items:[{
			   				  xtype:'combo',
			   				  itemId:'fCombox',
			   				  width:80,
				  	   		  margin:'5 0 5 25',
			   				  editable:false,
				  	   		  displayField:'name',
				  	   		  valueField:'value',
				  	   		  store:states1,
				  	   		  value:'='
			   			  },{
			                  xtype: 'numberfield',  
			                  maxValue:maxValue,
			                  hideTrigger:true,
	                          flex: 1,
	                          itemId : 'fText',
	                          margin:'1 0 0 10'
			              },{
			            	     xtype:'panel',
			            	     border:false,
			            	     layout:'hbox',
			            	     items:[{
				   					  xtype:'label',
				   					  text:'或',
				   					  padding:'0 10 0 2',
				   					  margin:'3 1 0 0',
				   					  border:1,
				   					  itemId:'expr',
				   					  expr:'or',
				   					  listeners:{
				   						  render:function(){
				   							this.getEl().setStyle('cursor',"pointer");
				   							this.getEl().setStyle('background',"url(/components/tableFactory/tableGrid-theme/images/grid/dirty.gif) no-repeat");
				   							  this.getEl().on('click',function(){
				   								if(this.expr=='or'){
				   									  this.setText('且');
						  	   						  this.expr = 'and';
						  	   					}else{
						  	   						  this.setText("或");
						  	   						  this.expr = 'or';
						  	   					}
				   							  },this) ;
				   						  }
				   					  }
				   			},{
				   				  xtype:'combo',
				   				  width:80,
				   				  editable:false,
					  	   		  displayField:'name',
					  	   		  valueField:'value',
					  	   		  store:states1,
					  	   		  itemId:'sCombox'  
				   			}]
			              },{
			            	  	xtype: 'numberfield',  
			                  maxValue:maxValue,
			                  hideTrigger:true,
	                          flex: 1,
	                          itemId : 'sText',
	                          margin:'1 0 0 10'
			              }]
		   			  }]
		   	  };	
	},
	//字符型过滤窗口
	columnStringFilter:function(windowConfig){
	       windowConfig.width=400;
		   windowConfig.height=200;
		   windowConfig.resizable=false;
		   var states = Ext.create('Ext.data.Store', {
	         fields: ['name', 'value'],
	         data : [{"name":"等于", "value":"="},
	                 {"name":"大于", "value":">"},
	                 {"name":"大于等于", "value":">="},
					 {"name":"小于", "value":"<"},
	                 {"name":"小于等于", "value":"<="},
	                 {"name":"不等于", "value":"<>"},
	                 {"name":"开头是", "value":"sta"},
	                 {"name":"开头不是", "value":"stano"},
	                 {"name":"结尾是", "value":"end"},
	                 {"name":"结尾不是", "value":"endno"},
	                 {"name":"包含", "value":"cont"},
	                 {"name":"不包含", "value":"contno"}]
	             });
	       windowConfig.items={
		   			  xtype:'panel',
		   			  bodyStyle:"border-width:0px 0px 1px 0px;padding:20px 0px 0px 0px;",
		   			    //style:'margin-left:50px',
		   			  items:[{
		   				  xtype:'panel',
		   				  border:0,
		   				  padding:'0 0 0 70',
		   				  layout:{
		   					  type:'table',
		   					  columns:2
		   				  },
		   				  items:[{
			   				  xtype:'combo',
			   				  itemId:'fCombox',
			   				  width:80,
				  	   		  margin:'5 0 5 25',
			   				  editable:false,
				  	   		  displayField:'name',
				  	   		  valueField:'value',
				  	   		  store:states,
				  	   		  value:'='
			   			  },{
			                  xtype: 'textfield', 
	                          flex: 1,
	                          itemId: 'fText',
	                          margin:'1 0 0 10'
			              },{
			            	      xtype:'panel',
			            	      border:0,
			            	      layout:'hbox',
			            	      items:[{
				   					  xtype:'label',
				   					  text:'或',
				   					  padding:'0 10 0 2',
				   					  margin:'3 1 0 0',
				   					  border:1,
				   					  itemId:'expr',
				   					  expr:'or',
				   					  listeners:{
				   						  render:function(){
				   							this.getEl().setStyle('cursor',"pointer");
				   							this.getEl().setStyle('background',"url(/components/tableFactory/tableGrid-theme/images/grid/dirty.gif) no-repeat");
				   							  this.getEl().on('click',function(){
				   								if(this.expr=='or'){
				   									  this.setText('且');
						  	   						  this.expr = 'and';
						  	   					}else{
						  	   						  this.setText("或");
						  	   						  this.expr = 'or';
						  	   					}
				   							  },this) ;
				   						  }
				   					  }
				   			},{
					   			xtype:'combo',
					   			width:80,
					   			editable:false,
						  	   	displayField:'name',
						  	   	valueField:'value',
						  	   	itemId:'sCombox',
						  	   	store:states
					   		}]
			              },{
			                  xtype: 'textfield',  
	                          flex: 1,
	                          itemId: 'sText',
	                          margin:'1 0 0 10'
			              }]
		   			  }]
		   	  };
	},
	//日期型过滤窗口
	columnDateFilter:function(windowConfig,format){
		windowConfig.width=400;
		windowConfig.height=200;
		windowConfig.resizable=false;//禁止调整大小 changxy20160527
		   var store = Ext.create('Ext.data.Store',{
			   fields:["name","value"],
			   data:[{name:'等于',value:'='},
				        {name:'大于',value:'>'},
		   			    {name:'大于等于',value:'>='},
		   			    {name:'小于',value:'<'},
		   			    {name:'小于等于',value:'<='},
		   			    {name:'不等于',value:'<>'}]
		   });
		   
		   windowConfig.items={
		   			  xtype:'panel',
		   			  bodyStyle:"border-width:0px 0px 1px 0px;padding:20px 0px 0px 0px;",
		   			 // style:'margin-left:50px', //设置居中
		   			  items:[{
		   				  xtype:'combo',
			  	   		  width:275,
			  	   		  colspan:2,
			  	   		  itemId:'planCombo',
			  	   		  fieldLabel:'方案',
			  	   		  labelWidth:30,
			  	   		  labelSeparator:'',
			  	   		  margin:'5 0 5 60',
			  	   		  editable:false,
			  	   		  displayField:'name',
			  	   		  valueField:'value',
			  	   		  store:{
			  	   			  fields:["name","value"],
			  	   			  data:[
			  	   			        {name:'下月',value:'nextMonth'},
			  		   			    {name:'本月',value:'thisMonth'},
			  		   			    {name:'上月',value:'lastMonth'},
			  		   			    {name:'下季度',value:'nextSeason'},
			  		   			    {name:'本季度',value:'thisSeason'},
			  		   			    {name:'上季度',value:'lastSeason'},
			  		   			    {name:'明年',value:'nextYear'},
			  		   			    {name:'今年',value:'thisYear'},
			  		   			    {name:'去年',value:'lastYear'},
			  		   			    {name:'自定义',value:'custom'}]
			  	   		  },
			  	   		  listeners:{
			  	   			  change:function(selector,value){
			  	   				  if(value=='custom'){
			  	   					  this.ownerCt.items.items[1].setVisible(true);
			  	   				  }else{
			  	   					  this.ownerCt.items.items[1].setVisible(false);
			  	   				  }
			  	   			  }
			  	   		  }
		   			  },{
		   				  xtype:'panel',
		   				  border:0,
		   				  padding:'0 0 0 70',
		   				  layout:{
		   					  type:'table',
		   					  columns:2
		   				  },
		   				  hidden:true,
		   				  items:[{
			   				  xtype:'combo',
			   				  width:80,
			   				  itemId:'fSymbol',
				  	   		  margin:'5 0 5 25',
			   				  editable:false,
				  	   		  displayField:'name',
				  	   		  valueField:'value',
				  	   		  store:store,
				  	   		  value:'='
			   			  },{
			   				  xtype:'datetimefield',
			   				  itemId:'fValue',
			   				  format:'Y-m-d',
			   				  margin:'1 0 0 10'
			   			  },{
			   				  xtype:'panel',
			   				  layout:'hbox',
			   				  border:false,
			   				  items:[{
			   					  xtype:'label',
			   					  text:'或',
			   					  padding:'0 10 0 2',
			   					  margin:'3 1 0 0',
			   					  border:1,
			   					  itemId:'expr',
			   					  expr:'or',
			   					  listeners:{
			   						  render:function(){
			   							this.getEl().setStyle('cursor',"pointer");
			   							this.getEl().setStyle('background',"url(/components/tableFactory/tableGrid-theme/images/grid/dirty.gif) no-repeat");
			   							  this.getEl().on('click',function(){
			   								if(this.expr=='or'){
			   									  this.setText('且');
					  	   						  this.expr = 'and';
					  	   					}else{
					  	   						  this.setText("或");
					  	   						  this.expr = 'or';
					  	   					}
			   							  },this) ;
			   						  }
			   					  }
			   				  },{
				   				  xtype:'combo',
				   				  itemId:'sSymbol',
				   				  expr:'or',
				   				  width:80,
				   				  editable:false,
					  	   		  displayField:'name',
					  	   		  valueField:'value',
					  	   		  store:store,
					  	   		  listeners:{
					  	   			  render:function(){
					  	   				  this.labelEl.on('click',function(){
					  	   					  
					  	   				  },this);
					  	   			  }
					  	   		  }
				   			  }]
			   			  },{
			   				  xtype:'datetimefield',
			   				  itemId:'sValue',
			   				  format:'Y-m-d',
			   				  margin:'1 0 0 10'
			   			  }]
		   			  }]
		   	  };
	},
	//执行过滤
	filterHandler:function(window,column,subModuleId){
		var store = Ext.data.StoreManager.lookup(this.ownerBuilder.prefix+'_pageStore'); 
		var itemid = column.dataIndex,
		itemtype = column.columnType,
	    codesetid = column.codesetid,
	    filterParam = {
			field:itemid,
			itemtype:itemtype,
			expr:'or',
			factor:[]
		};
		//组合过滤参数
		if((itemtype=='A' && codesetid && codesetid!='0') ||  column.operationData){
			var records = window.child('treepanel').getChecked();
			Ext.each(records,function(record){
				 filterParam.factor.push("=`"+record.get("id"));
			});
			
			filterParam.itemtype="C";
	    }else if(itemtype=='N' || itemtype=='A' || itemtype=='M'){
	    	var fCombox = window.query("#fCombox")[0].getValue();
	        var fText = window.query("#fText")[0].getValue();
	        var sCombox = window.query("#sCombox")[0].getValue();
	        var sText = window.query("#sText")[0].getValue();
	        filterParam.factor.push(fCombox+'`'+encodeURI(encodeURI(fText)));
	        if(sCombox)
	        	filterParam.factor.push(sCombox+'`'+encodeURI(encodeURI(sText)));
	        	
	    }else if(itemtype=='D'){
	    	    var planValue = window.query("#planCombo")[0].getValue();
	    	    if(!planValue){
	    	    	Ext.Msg.alert("提示信息","请选择查询方案！");
	    	    	return;
	    	    }
	    	    
	    	    filterParam.plan=""; 
	    	    if(planValue=='custom'){
	    	    	   var fSymbol = window.query("#fSymbol")[0].getValue();
	    	    	   var fValue = window.query("#fValue")[0].getValue();
	    	    	   var sSymbol = window.query("#sSymbol")[0].getValue();
	    	    	   var sValue = window.query("#sValue")[0].getValue();
	    	    	   if(fValue!=null && fValue!='')
	    	    		   filterParam.factor.push(fSymbol+"`"+fValue);
	    	    	   if(sSymbol && sValue!=null && sValue!='')
	    	    		   filterParam.factor.push(sSymbol+"`"+sValue);
	    	    	   filterParam.plan = planValue;
	    	    }else{
	    	    		filterParam.factor.push(planValue+"`");
	    	    }
	    }
		
		var exprComp = window.query("#expr");
		if(exprComp.length>0)
			filterParam.expr = exprComp[0].expr;
		
		if(filterParam.factor.length<1){
			Ext.Msg.alert("提示信息","请输入查询值!");
			return;
		}
		
		store.getProxy().extraParams = {
	 		subModuleId:subModuleId, 
	        filterParam:Ext.encode(filterParam),
	        flag:'1'
	    };
		
	    store.reload();//执行过滤，刷新数据
	    window.close();
		column.filterMark();//列上添加过滤图标
	}
	
});

/**
 * 为column 添加 特定功能支持
 */
Ext.define("Ext.tree.grid.column.Column",{
	extend: 'Ext.grid.column.Column',
	alias: 'widget.treegridcolumn',
    filtered:false,
	cancelMark:function(){
    		if(!this.filtered)
	    	   return;
		var columnDom = this.getEl().child('.x-column-header-inner').dom;
			columnDom.removeChild(columnDom.getElementsByTagName("img")[0]);
			this.filtered = false;
	},
	
	filterMark:function(){
		var grid = Ext.getCmp("projectmanage_0001_treegrid");
  	    if(this.filtered)
  	    	return;
  	    
  	    var header = this.getEl().child('.x-column-header-inner');
  	    var height = header.getHeight()/2-6;
  	    header.insertFirst({tag:'img',flag:'filter',style:'position:absolute;left:2px;top:'+height+'px',src:'/components/tableFactory/tableGrid-theme/images/filter2.png'});
  	    var filteredColumn = grid.query("gridcolumn[filtered=true]")[0];
  	    if(filteredColumn){
  	    	var columnDom = filteredColumn.getEl().child('.x-column-header-inner').dom;
  	    	columnDom.removeChild(columnDom.getElementsByTagName("img")[0]);
  	    	filteredColumn.filtered = false;
  	    }
  	    
  	    this.filtered=true;
    }
});