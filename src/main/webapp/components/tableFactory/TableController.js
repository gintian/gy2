/**
 * 表格控件业务控制类
 * guodd 2016-01-06
 */
Ext.define("EHR.tableFactory.TableController",{
	
	//TableBuilder 对象
	ownerBuilder:undefined,
	
	/**
	 * 构造方法
	 * @param builder TableBuilder 对象
	 */
	constructor:function(builder){
	  this.ownerBuilder = builder;
	},
	//column 参数 转 Ext column 对象
	columnConfig2Column:function(cc,headLevel){
	       var me = this.ownerBuilder;
	       var co = {};
	       
	      /* //自定义的列，columnsInfo中定义一个变量customColumn，支持完全自定义列
	       if(cc.customColumn){
	           if(cc.customColumn.dataIndex){
	               me.repertory.fields.push(cc.customColumn.dataIndex);
	               me.repertory.fullColumns.push(cc);
	           }
	           return cc.customColumn;
	       }*/
	       co.text = cc.columnDesc + (cc.descSuffix == null ? '' : cc.descSuffix);//xiegh 20170401  26659 
	       cc.hintText = cc.hintText&&cc.hintText.length>0?cc.hintText:cc.columnDesc;
	       co.tooltip=cc.hintText;
	       co.nowrap = me.config.columnNowrap;
	       co.tooltipType='title';
	       co.width = cc.columnWidth;
	       co.align = cc.textAlign;
	       co.locked = cc.locked;
	       co.beExport = cc.beExport;
	       /*
	       var fn = eval(cc.rendererFunc);
	       if(cc.rendererFunc.length>0 && !fn)
	            fn = new Function("return "+cc.rendererFunc)();
	       co.renderer = Ext.isFunction(fn)?fn:undefined;
	       */
	       if(cc.rendererFunc && cc.rendererFunc.length>0){
	       		if(this.ownerBuilder.handlerScope && this.ownerBuilder.handlerScope[cc.rendererFunc]){
	       			co.renderer = cc.rendererFunc;
	       			co.scope = this.ownerBuilder.handlerScope;
	       		}else{
					var func = eval(cc.rendererFunc);
					co.renderer = func;
				}
	       }
	       
	       //合并列
	       
	       if(cc.childColumns.length>0){
	       	  headLevel.max++;
	          var childc = [];
	            for(var i =0;i<cc.childColumns.length;i++){
	                 childc.push(this.columnConfig2Column(cc.childColumns[i],headLevel));
	            }
	          co.columns = childc;
	          co.menuDisabled=true;
	          return co;
	       }
	       
	       if(!cc.columnId){
	       		co.menuDisabled=true;
	            return co;
	       }
	       
	       		
	       if(cc.encrypted){//加密列
	                me.repertory.fields.push(cc.columnId+"_e");
	                return undefined;
	       }else if(cc.loadtype==2){//等于2 ，隐藏
	          		co.hidden=true;
	       }else if(cc.loadtype==3 || cc.loadtype==6){//等于3 ，不显示，但是加载数据
	       		    me.repertory.fields.push(cc.columnId);
	       		    return undefined;
	       }else if(cc.loadtype==4)//不加载数据
	       		    return undefined;
	       		    
	       me.repertory.fields.push(cc.columnId);
	       me.repertory.fullColumns.push(cc);   
	       
	       co.dataIndex=cc.columnId;
	       co.fieldsetid = cc.fieldsetid;
	       co.codesetid = cc.codesetId;
	       co.columnType = cc.columnType;
	       co.filterable = cc.filterable;
	       if(cc.filterable && cc.doFilterOnLoad &&((cc.codesetId && cc.codesetId.length>1)||cc.operationData))
	           me.initFilterColumn = cc.columnId;
	       co.sortable = cc.sortable;
	       co.showSortType = cc.showSortType;
	       co.imppeople=cc.imppeople;//是否启用选人组件
	       if(cc.columnType=='M')
	    	   co.limitlength=cc.limitlength;//大文本限制长度
	       cc.operationData = cc.operationData && cc.operationData.length>0?cc.operationData:undefined;
	       //编辑对象
	       var editor = undefined;
	       if(cc.operationData){
	       	   co.xtype="operationcolumn";
	       	  if(Ext.isString(cc.operationData)){
	       		 cc.operationData = eval(cc.operationData);
	          }
	       	  co.operationData = cc.operationData;
	          editor={
	                 xtype:"combo",
	                 editable:false,
	                 store:{field:["dataValue","dataName"],data:cc.operationData},valueField:'dataValue',displayField:'dataName'
	          };
	             
	       }else if(cc.columnType=='A' && cc.codesetId && cc.codesetId.length>1){
	          co.xtype="codecolumn";
	          editor={
	             	xtype:"codecomboxfield",codesetid:cc.codesetId,onlySelectCodeset:cc.codeSetValid,vorg:cc.vorg,parentidFn:cc.parentidFn,
	             	ctrltype:cc.ctrltype,nmodule:cc.nmodule,codesource:cc.codesource,selectValidator:eval(cc.validFunc)
	          };
	          co.ctrltype = cc.ctrltype;
	          co.nmodule = cc.nmodule;
	          co.codesource = cc.codesource;
	          co.fatherRelationField=cc.fatherRelationField;//父代码指标标识
	          co.childRelationField=cc.childRelationField;//孩子代码指标
	          
	       }else if(cc.columnType=='N'){
	    	  // linbz 数值增加可编辑函数
	    	  var validator = eval(cc.validFunc);
	          co.xtype='numbercolumn';
	          var formatpattern = "00000000000000000000000000000";
	          var format = formatpattern.substr(0,cc.columnLength);
	          var displayFormat = "0,000";
	          if(cc.decimalWidth>0){
	                 format+="."+formatpattern.substr(0,cc.decimalWidth);
	                 displayFormat+="."+formatpattern.substr(0,cc.decimalWidth);
	          }
	          co.format = displayFormat; 
	          cc.format=format;//changxy 20160526  
	          var limit = format.replace(/0/g,9);
	          editor={
	             	xtype:"numberfield",minValue:"-"+limit,allowDecimals:cc.decimalWidth>0,selectOnFocus:true,
	             	decimalPrecision:cc.decimalWidth,maxValue:limit,hideTrigger:true,step:0,validator:validator
	          };
	       }else if(cc.columnType=='D'){
	          var format = 'Y-m-d';
	          if(cc.columnLength==4)
	          	format = 'Y';
	          else if(cc.columnLength==7)
	          	format = 'Y-m';
	          else if(cc.columnLength==10)
	          	format = 'Y-m-d';
	          else if(cc.columnLength==16)
	          	format = 'Y-m-d H:i';
	          else if(cc.columnLength==18)
	            format = 'Y-m-d H:i:s';
	          co.format = format;
	          cc.format = format;
	          co.operationFormat = format;
	          if(cc.ignoreTime){
	          	co.operationFormat = format.indexOf('H:')?'Y-m-d':format;
	          }
	          editor={xtype:'datetimefield',
	                  format:format,selectOnFocus:true,
	                  formatText:'预期的日期格式:'+format
	                  };
	          
	       }else if(cc.columnType=='M' ){
	          co.xtype='bigtextcolumn';
	          co.sortable=false;
	          editor = {xtype:'bigtextfield'};
	       }else{
	       	  var validator = eval(cc.validFunc);
	       	  if(!validator){
	       	  	validator=function(value){
	       		   if(Ext.getStringByteLength(value)>this.maxSize)
	       			   return "该输入项的最大长度是"+this.maxSize+"个字符！";
	       		   else 
	       			   return true;
	       	  	};
	       	  }
	       	  editor = {xtype:'textfield',maxSize:cc.columnLength,selectOnFocus:true,validator:validator};
	       	  
	       	  if(!co.renderer)
	          	co.renderer=this.doSpecialCharater;
	       }
	       
	       //开放编辑功能 并且 列编辑功能开启
	       if(me.config.editable && cc.editableValidFunc!='false'){
	          editor.allowBlank = Ext.isEmpty(cc.allowBlank)?true:cc.allowBlank;
	          co.editor = editor;
	          co.editableValidFunc = cc.editableValidFunc;
	       }else 
	          delete editor;
	       
	       if(cc.summaryType>0 && cc.columnType=='N'){
	          co.summaryType = 'remote';
	          co.summaryRenderer = cc.summaryRendererFunc.length>0?eval(cc.summaryRendererFunc):undefined;
	          me.keyParams.doSummary = true;
	       }
	       
	       //分组指标
	       if(cc.group){
	           me.keyParams.groupColumn = cc.columnId;
	       }
	       return co;
	},
	doSpecialCharater:function(value){//add by xiegh on date20171212 处理特殊字符 < > 不能正常显示
		if(value==undefined){//value是undefined 后面报错。
			value='';
		}
		return '<label>'+value.replace(/</g,'&lt;').replace(/>/g,'&gt;')+'</label>';
	},
	doTableScheme:function(){
	    var config = this.ownerBuilder.config;
        var viewConfig = this.schemeViewConfig||{publicPlan:config.showPublicPlan};
		Ext.require("EHR.tableFactory.plugins.SchemeSetting",function(){
		  	var callback = config.schemeSaveCallback?eval(config.schemeSaveCallback):function(){
		  		 window.location.reload();
		  	};
		  	new EHR.tableFactory.plugins.SchemeSetting({
		  		subModuleId:config.subModuleId,
		  		schemeItemKey:config.schemeItemKey?config.schemeItemKey:'',
		  		itemKeyFunctionId:config.itemKeyFunctionId?config.itemKeyFunctionId:'',
		  		moduleId:config.moduleId,
		  		closeAction:callback,
		  		viewConfig:viewConfig
		  		/*{
		  			publicPlan:config.showPublicPlan
		  		}*/
		  	});
		});
	},
	/**
	 * 单指标统计
	 * 当前对象的this是TableBuilder
	 */
	singleItemAnalyse:function(obj){
		var me = this;
		Ext.require("EHR.tableFactory.plugins.SingleItemAnalyse",function(){
			var columns = this.ownerBuilder.repertory.fullColumns;
			//if(this.listenerConfig.beforeColumnAnalyse)
			//	columns = this.listenerConfig.beforeColumnAnalyse(columns);
			
			var analyse = new EHR.tableFactory.plugins.SingleItemAnalyse(obj.column,this.ownerBuilder.subModuleId,this.ownerBuilder.moduleId,columns);
			var tree = analyse.getTree();
			var anal = analyse.getAnalyse();
			Ext.widget("window",{
				title:"按"+obj.column.text+'统计',
				modal:true,
				layout:'border',
				height:600,
				width:870,
				resizable : false,//禁止缩放
				items:[tree,anal]
			}).show();
		},me);
	},
	/**
	 * 方案统计分析
	 */
	planItemsAnalyse:function(){
		var builer = this.ownerBuilder;
		builer.mainPanel.removeAll(false);
		Ext.require("EHR.tableFactory.plugins.PlanItemsAnalyse",function(){
			var analyse = Ext.create("EHR.tableFactory.plugins.PlanItemsAnalyse",{
				subModuleId:builer.subModuleId,
				analyseBusiId:builer.moduleId,
				columns:builer.repertory.fullColumns,
				title:'多维统计分析',
				tools:[{
					xtype:'button',text:'返回',scope:builer,
					handler:function(button){
					    		   this.mainPanel.remove(button.toolOwner,true);
					    		   this.mainPanel.add(this.bodyPanel);
					}
				}]
			});
			builer.mainPanel.add(analyse);
		});
	},
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
		
	    var codefilter = Ext.getCmp(builer.prefix+"_codeFilter");
		var store = Ext.data.StoreManager.lookup(builer.prefix+'_dataStore');
	/*	if(column.id==me.FiltercolumnId)  //如果是同一个代码类过滤 不执行，直接返回 
		  return;*/
		//delete store.getProxy().extraParams.filterParam;//清除过滤参数    changxy 
		//store.reload();//刷新数据
		
		
		//如果column有operationData 或者 是代码 指标，树形方式过滤
	   if(column.operationData|| (itemtype=='A' && codesetid && codesetid!='0') ){
		  this.columnCodeFilter(codefilter,column);
		  column.filterMark(builer.tablePanel);
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
	      this.columnNumberFilter(windowConfig,column);
	   else if(itemtype=='D')//日期型
		   this.columnDateFilter(windowConfig,column);
	   else//字符型和文本型
		   this.columnStringFilter(windowConfig,column.filterParam);
	   //创建过滤window
	   Ext.widget('window',windowConfig).show();
	
	},
	//移除 代码过滤树
	removeCodeFilter:function(codeFilter){
		if(codeFilter){
				codeFilter.column.cancelMark();
			   codeFilter.ownerCt.remove(codeFilter,true);
			   //清除cookie
			   Ext.util.Cookies.clear(this.ownerBuilder.config.cookiePre+"_"+this.ownerBuilder.config.subModuleId+"_filter");
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
		var store = Ext.data.StoreManager.lookup(this.ownerBuilder.prefix+'_dataStore');
		//delete store.getProxy().extraParams.filterParam;//清除过滤参数
		store.getProxy().extraParams.filterParam = undefined;
		store.reload();//刷新数据
		column.cancelMark();//清除过滤图标
		this.FiltercolumnId = undefined;
		Ext.util.Cookies.clear(this.ownerBuilder.config.cookiePre+"_"+this.ownerBuilder.config.subModuleId+"_filter");
	},
	
	//代码型过滤窗口
    columnCodeFilter:function(codeFilter,column,notKeepCookies){
	  	var me = this,
	  	    builder = me.ownerBuilder;
	  	if(!notKeepCookies)
		  	//保存过滤信息到cookie中，下次进入本界面时自动打开过滤panel
		  	Ext.util.Cookies.set(builder.config.cookiePre+"_"+builder.subModuleId+"_filter",column.dataIndex);
	  	me.FiltercolumnId=column.id;//changxy  
	  	var tablePanel = builder.tablePanel;
	  	
	  	if(codeFilter ){
	  	    if(codeFilter.column === column)
	  	        return;
	  	        
	  		codeFilter.setTitle(column.text);
	  		codeFilter.column = column;
	  		if(column.operationData){
	  			  var childrens = new Array();
		  	  	  Ext.each(column.operationData,function(data){
		  	  		 childrens.push({id:data.dataValue,text:data.dataName,leaf:true,checked:false}); 
		  	  	  });
		  	  	codeFilter.getStore().setRootNode({
		  			expanded: true,
		  	        children:childrens
		  		});
	  		}else{
	  			codeFilter.getStore().setProxy({//update by xiegh on date 20180118 bug33971
	  				 type:'transaction',
					 extraParams:{
		    				codesetid:column.codesetid,
		    				nmodule:column.nmodule,
		    				ctrltype:column.ctrltype,
		    				codesource:column.codesource,
		    				multiple:true
				  	},
					functionId:'ZJ100000131'
	  			
	  			});
	  			codeFilter.getStore().load();
	  		}
	  		
	  		return;
	  	}
	  	
	  	var store = undefined;
	  	var checkedCodes = builder.filterConfig && builder.filterConfig.checkedCodes?","+builder.filterConfig.checkedCodes:"";
	  	if(column.operationData){
	  		  var childrens = new Array();
		  	  Ext.each(column.operationData,function(data){
		  	     var item = {id:data.dataValue,text:data.dataName,leaf:true,checked:false};
		  	     if(checkedCodes.indexOf(","+data.dataValue+",")>-1)
		  	          item.checked = true;
		  		 childrens.push(item); 
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
					    },
					    listeners:{
					       load:function(self,records){
					       		for(var k=0;k<records.length;k++){
					       		    if(checkedCodes.indexOf(","+records[k].get("id")+",")>-1)
					       		        records[k].set("checked",true);
					       		}
					       }
					    }
			  	  	  
			  	  	  });
	  	}
	  	tablePanel.ownerCt.add({
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
			expandToolText:false,
			collapseToolText:false,
			store:store,
	         listeners:{
	        	 	checkchange:function(){
	        	 		builder.autoSave();
	        	 		var checked = this.getChecked();
	        	 		var filterParam = {
	        	 				field:this.column.dataIndex,
	        	 				itemtype:"C",
	        	 				expr:'or',
	        	 				factor:[]
	        	 		};
	        	 		var ids = "";
	        	 		Ext.each(checked,function(record){
	        	 				 filterParam.factor.push("=`"+record.get("id"));
	        	 				 ids+=record.get("id")+",";
	        	 		});
	        	 		var store = Ext.data.StoreManager.lookup(builder.prefix+'_dataStore'); 
	        	 		//18/7/20 xus 原来方法会覆盖掉sortType 属性 
	        	 		store.getProxy().extraParams.subModuleId=builder.subModuleId;
	        	 		store.getProxy().extraParams.filterParam=Ext.encode(filterParam);
	        	 	    store.loadPage(1);
	        	 	    Ext.util.Cookies.set(builder.subModuleId+"_filter",this.column.dataIndex+":"+ids);
	        	 	}
	        	 	
	         }
	  	});
  		
	},
	//数值型过滤窗口
	columnNumberFilter:function(windowConfig,column){
		   windowConfig.width=400;
		   windowConfig.height=200;
		   windowConfig.resizable=false;
		   var maxValue = column.maxValue; 
		   
		    var operator1="=",operator2,exprvalue1,exprvalue2,expr="or";
		   if(column.filterParam){
		   		var filterParam = column.filterParam;
		    	if(filterParam.factor[0]){
		    		operator1 = filterParam.factor[0].split("`")[0];//运算符
		    		exprvalue1 = decodeURI(decodeURI(filterParam.factor[0].split("`")[1]));//运算值
		   		}
		    	if(filterParam.factor[1]){
			    	operator2 = filterParam.factor[1].split("`")[0];//运算符
			    	exprvalue2 = decodeURI(decodeURI(filterParam.factor[1].split("`")[1]));//运算值
		    	}
		    	expr = filterParam.expr;
		   }
		   
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
				  	   		  value:operator1
			   			  },{
			                  xtype: 'numberfield',  
			                  maxValue:maxValue,
			                  hideTrigger:true,
	                          flex: 1,
	                          itemId : 'fText',
	                          margin:'1 0 0 10',
	                          value:exprvalue1
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
				   					  expr:expr,
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
					  	   		  itemId:'sCombox',
					  	   		  value:operator2
				   			}]
			              },{
			            	  	xtype: 'numberfield',  
			                  maxValue:maxValue,
			                  hideTrigger:true,
	                          flex: 1,
	                          itemId : 'sText',
	                          margin:'1 0 0 10',
	                          value:exprvalue2
			              }]
		   			  }]
		   	  };	
	},
	//字符型过滤窗口
	columnStringFilter:function(windowConfig,filterParam ){
	       windowConfig.width=400;
		   windowConfig.height=200;
		   windowConfig.resizable=false;
		   
		   var operator1="=",operator2,exprvalue1,exprvalue2,expr="or";
		   if(filterParam){
		   		if(filterParam.factor[0]){
		    		operator1 = filterParam.factor[0].split("`")[0];//运算符
		    		exprvalue1 = decodeURI(decodeURI(filterParam.factor[0].split("`")[1]));//运算值
		   		}
		    	if(filterParam.factor[1]){
			    	operator2 = filterParam.factor[1].split("`")[0];//运算符
			    	exprvalue2 = decodeURI(decodeURI(filterParam.factor[1].split("`")[1]));//运算值
		    	}
		    	expr = filterParam.expr;
		   }
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
				  	   		  value:operator1
			   			  },{
			                  xtype: 'textfield', 
	                          flex: 1,
	                          itemId: 'fText',
	                          margin:'1 0 0 10',
	                          value:exprvalue1
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
				   					  expr:expr,
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
						  	   	store:states,
						  	   	value:operator2
					   		}]
			              },{
			                  xtype: 'textfield',  
	                          flex: 1,
	                          itemId: 'sText',
	                          margin:'1 0 0 10',
	                          value:exprvalue2
			              }]
		   			  }]
		   	  };
	},
	//日期型过滤窗口
	columnDateFilter:function(windowConfig,column){
		   var format = column.format;
		   var planValue="",operator1,operator2,exprvalue1,exprvalue2,expr="or";
		   if(column.filterParam){
		   		var filterParam = column.filterParam;
		    	if(filterParam.factor[0]){
		    		operator1 = filterParam.factor[0].split("`")[0];//运算符
		    		exprvalue1 = decodeURI(decodeURI(filterParam.factor[0].split("`")[1]));//运算值
		    		
		    		if(operator1.length>2){
		    			planValue = operator1;
		    			operator1 = "";
		    			exprvalue1 = "";
		    		}
		    			
		   		}
		    	if(filterParam.factor[1]){
			    	operator2 = filterParam.factor[1].split("`")[0];//运算符
			    	exprvalue2 = decodeURI(decodeURI(filterParam.factor[1].split("`")[1]));//运算值
		    	}
		    	expr = filterParam.expr;
		   }
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
			  	   		  value:planValue,
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
				  	   		  value:operator1
			   			  },{
			   				  xtype:'datetimefield',
			   				  itemId:'fValue',
			   				  format:format,
			   				  margin:'1 0 0 10',
			   				  value:exprvalue1
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
			   					  expr:expr,
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
					  	   		  value:operator2,
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
			   				  format:format,
			   				  margin:'1 0 0 10',
			   				  value:exprvalue2
			   			  }]
		   			  }]
		   	  };
	},
	//执行过滤
	filterHandler:function(window,column,subModuleId){
		var store = Ext.data.StoreManager.lookup(this.ownerBuilder.prefix+'_dataStore');
		var itemid = column.dataIndex,
		    itemtype = column.columnType,
	        codesetid = column.codesetid;
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
	        	filterParam.factor.push(fCombox+'`'+encodeURI(fText));
	        	if(sCombox)
	        		filterParam.factor.push(sCombox+'`'+encodeURI(sText));
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
	        filterParam:Ext.encode(filterParam)
	    };
	    column.filterParam = filterParam;//add by xiegh on date 20180207 bug:33773 记录过滤条件
	    store.loadPage(1);//执行过滤，刷新数据
	    window.close();
		column.filterMark(this.ownerBuilder.tablePanel);//列上添加过滤图标
	},
	
	/**
	 * 保存列新宽度到栏目设置
	 * 当前对象的this是TableBuilder
	 * @param header
	 * @param column 拖动的列
	 * @param width  新宽度
	 */
	saveColumnWidth:function(header,column,width){
		 if(!column.dataIndex)
	    	    return;
	     var para = new HashMap();
		Ext.apply(para,{
			subModuleId:this.ownerBuilder.subModuleId,
		    updateType:'widthUpdate',
		    itemid:column.dataIndex,
		    width:width,
			actionName:'update'
		});
		Rpc({
			functionId:'ZJ100000001'
		},para);
	},
	/**
	 * 列拖动调换位置时保存到栏目设置
	 * @param header  
	 * @param column  拖动的列
	 */
	saveColumnMove:function(header,column){
		if(!column.dataIndex)
		   	 return;
		var tablePanel = this.ownerBuilder.tablePanel;
		var is_lock = column.isLocked()?'1':'0';
		var index = tablePanel.getColumnManager().getHeaderIndex(column);
		var nextcolumn = tablePanel.getColumnManager().getHeaderAtIndex(index+1);
		var nextid = "-1";
		if(nextcolumn && nextcolumn.dataIndex)
			nextid = nextcolumn.dataIndex;
			
		var para = new HashMap();
		Ext.apply(para,{
			subModuleId:this.ownerBuilder.subModuleId,
			updateType:'positionUptate',
			itemid:column.dataIndex,
			is_lock:is_lock,
			nextid:nextid,
			actionName:'update'
		});
		Rpc({
			functionId:'ZJ100000001'
		},para);
	},
	/**
	 * 列锁定或解锁的时候保存到栏目设置
	 * @param panel
	 * @param column 操作列
	 * @param opt 锁定状态 lock/unlock
	 */
	saveColumnLock:function(panel,column,opt){
		if(!column.dataIndex)
		   	 return;
		   	 
		var para = new HashMap();
		Ext.apply(para,{
			subModuleId:this.ownerBuilder.subModuleId,
		   	updateType:'lockUpdate',
		   	itemid:column.dataIndex,
		   	lockstate:opt,
			actionName:'update'
		});
		Rpc({
			functionId:'ZJ100000001'
		},para);
	}

    
});