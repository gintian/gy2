Ext.Loader.loadScript({url:'/components/tableFactory/ext_custom.js'});

Ext.define("EHR.tableFactory.TableBuilder",{
	requires:[
	    'EHR.extWidget.proxy.TransactionProxy',
		'EHR.tableFactory.TableController',
		'EHR.extWidget.field.DateTimeField',
		'EHR.extWidget.field.CodeTreeCombox',
		'EHR.extWidget.field.BigTextField',
		'EHR.querybox.QueryBox'
	],
	
	controller:undefined,
	
	//config中添加beforeBuildComp参数
	//保存还未生成Ext组件的json对象
	gridConfigObj:undefined,
	
	//一些其他地方要用的数据
	repertory:undefined,
	
	//保存的控制变量
	keyParams:undefined,

	//ext对象
	//gridpanel对象
	tablePanel:undefined,
	menuBar:undefined,
	toolBar:undefined,
	dataStore:undefined,
	bodyPanel:undefined,
	
	columnLevel:undefined,
    //是否限制分页最大500，true 不限制。haosl 20190921
	limitPageSize:false,
	
	constructor:function(configObj){
		var me = this;
		//如果参数是string，转成object
		if(Ext.isString(configObj)){
			me.config = Ext.decode(configObj);
		}else{
			me.config = configObj;
		}

		this.beforeBuildComp = me.config.beforeBuildComp;
		this.handlerScope = me.config.handlerScope;
		
		me.initConfig();
		
		
		//兼容以前的tablekey属性
		if(me.config.subModuleId)
			me.config.tablekey = me.config.subModuleId;
		if(!me.config.cookiePre)
			me.config.cookiePre = "";
		me.prefix = me.config.prefix;
		me.subModuleId = me.config.subModuleId;
		me.controller = Ext.create("EHR.tableFactory.TableController",me);
		me.initComponent();
		
		//表格编辑回车 当做tab键使用
		if(me.config.editable && Ext.event)
			Ext.override(Ext.event.Event,{
				getKey:function(){
				   var grid = Ext.getCmp(me.prefix+"_tablePanel");
				   if(grid && this.getKeyName( )=='ENTER' && grid.owns(this.target))
				      return this.TAB;
					return this.keyCode || this.charCode;
				}
			});
	},
	initConfig:function(){
		this.gridConfigObj={
		    tableConfig:undefined,
		    storeConfig:undefined,
		    menuConfig:undefined,
		    toolConfig:undefined
		};
		
		//一些其他地方要用的数据
		this.repertory={
		    //全部的列,包括合并列头的子列。不包括columnId为空的列
		    fullColumns:[],
		    //store 的fields
		    fields:['MVP_Data_Key']
		};
		
		//保存的控制变量
		this.keyParams={
		    // 列合计
		    doSummary:false,
		    //分组列
		    groupColumn:undefined
		};
		
		this.columnLevel = {level:1};
		
	},
	initComponent:function(){
	    if(this.config.tdMaxHeight>0){
	    	    if(Ext.util.CSS.getRule(".x-grid-cell-inner"))
	    	    		Ext.util.CSS.updateRule(".x-grid-cell-inner","max-height",this.config.tdMaxHeight+"px");
	    	    else
	    	    		Ext.util.CSS.createStyleSheet(".x-grid-cell-inner{max-height:"+this.config.tdMaxHeight+"px}");
		}
	    
		this.readCookies();
	
	    //将参数转为 ext格式的json对象
		this.convertConfigToExtObj();
		
		//调用beforeBuildComp,可以对json对象进行二次修改
		Ext.callback(this.beforeBuildComp,this.handlerScope,[this.gridConfigObj]);
		
		//将ext格式的json对象生成Ext的具体组件
		this.createExtComponents();
		
		//添加栏目设置等功能
		this.addOtherWidget();
		
		//添加事件监听
		this.addEvent();
		
		//创建最外层容器
		this.createMainContainer();
		
		this.initFilter();
		
	},

	readCookies:function(){
	   var me = this,filterConfig;
		//如果开启过滤功能
		if(this.config.isColumnFilter){
			var filterStr = Ext.util.Cookies.get(this.config.cookiePre+"_"+this.config.subModuleId+"_filter");
			if(filterStr){
			    var columnIndex = filterStr.split(":")[0];
			    filterConfig = {columnIndex:columnIndex};
			    if(filterStr.split(":").length>1){
			         var checkedCodes = filterStr.split(":")[1];
			         filterConfig.checkedCodes = checkedCodes;
			    }
			    me.filterConfig = filterConfig;
			}
		}
		
	},
	
	initFilter:function(){
		var me = this;
		if(!me.filterConfig && me.initFilterColumn)
		    me.filterConfig = {columnIndex:me.initFilterColumn}
		
		//如果开启过滤功能
		if(me.filterConfig){
			    var columnIndex = me.filterConfig.columnIndex;//filterStr.split(":")[0];
				var columns = this.tablePanel.getColumnManager().getColumns();
				for(var i=0;i<columns.length;i++){
					var col = columns[i];
					if(col.dataIndex == columnIndex && ((col.columnType=='A' && col.codesetid && col.codesetid!='0') || col.operationData)){
						this.controller.columnCodeFilter(undefined,columns[i],true);
						if(columns[i].getEl()){
							columns[i].filterMark(this.tablePanel);
						}else{
							columns[i].on("render",function(){ this.filterMark(me.tablePanel);  });
						}
						break;
					}
				}
		}
	},
	//将参数转换为Ext对象格式的json
	convertConfigToExtObj:function(){
	    var me = this;
	    if(me.subModuleId)
	       me.remoteConvert();
	    else
	       me.simpleConvert();
	       
	    //生成tool和menu参数
	    me.createToolAndMenuObj();
	    
	    me.insertPluginsOrFeatures();
	},
	
	//不需要走后台的简单表格
	simpleConvert:function(){
	   var me = this;
	   me.gridConfigObj.storeConfig={
	       storeId:me.prefix+"_dataStore",
	       fields:me.config.datafields,
	       data:me.config.storedata
	   };
	   me.gridConfigObj.tableConfig={
	       id:me.prefix+"_tablePanel",
	       store:me.prefix+"_dataStore",
	       columnLines:true,
	       columns:me.config.tablecolumns,
	       enableLocking:me.config.lockable,
	       selModel:me.config.selectable?{selType:'checkboxmodel'}:undefined,
	       viewConfig:{},
	       //ext6.0 bug: enableLocking与bufferedRenderer有冲突 guodd 2018-04-28
	       bufferedRenderer:me.config.lockable?false:true,
	       plugins:[],
	       features:[]
	   };
	   
	},
	
	//需要走后台的复杂表格
	remoteConvert:function(){
		var me = this;
		//将columns参数转为Ext grid 格式的Column json对象
	    var ccs = me.config.tablecolumns;
	    var columnArray = new Array();
	    if(me.config.showRowNumber)
	    	columnArray.push({xtype:'rownumberer',text:'序号',width:50,style:'padding-left:4px;'});
	    for(var i=0;i<ccs.length;i++){
	        var headLevel = {max:1};
	        var column = me.controller.columnConfig2Column(ccs[i],headLevel);
	        if(!column)
	           continue;
	        if(headLevel.max>me.columnLevel.level)
	        		me.columnLevel.level = headLevel.max;
	        if(!column.columns)
	        	   column.level = me.columnLevel;
	        columnArray.push(column);
	    }
	    //生成store参数
	    me.createStoreObj();
	    //生成gridpanel参数
	    me.createTableObj(columnArray);
	    
	},
	
	/**
	 *创建store json对象
	 */
	createStoreObj:function(){
	    var me = this;
	    Ext.define(me.prefix+"_TableModel",{
	    		extend: 'Ext.data.Model',
	        fields:me.repertory.fields,
	        idProperty:'record_internalId'
	    });
	    me.config.pagesize = me.config.pagesize?me.config.pagesize:25;
	    me.config.pagesize = (me.config.pagesize>500 && !me.config.limitPageSize)?500:me.config.pagesize;
		me.gridConfigObj.storeConfig = {
	    		storeId:me.prefix+"_dataStore",
			autoLoad:true,
			remoteSort:true,
			currentPage:me.config.currentPage&&me.config.currentPage>0?me.config.currentPage:1,
			groupField:me.keyParams.groupColumn,
			model:me.prefix+"_TableModel",
			pageSize:me.config.pagesize,
			onChangePage:me.config.onChangePage,
			proxy:{
			    type: 'transaction',
		        timeout:80000,
		        functionId:me.config.tableFunctionId||'ZJ100000000',
		        extraParams:{subModuleId:me.subModuleId},
		        reader: {
		            type: 'json',
		            root: 'dataobjs',
		            totalProperty:'totalCount',
		            idProperty:'record_internalId'
		        }
			},
			loadPage: function(page, options) {
		        var me = this,
		            size = me.getPageSize();
				if(me.onChangePage)
				    me.onChangePage();
				
		        me.currentPage = page;
		        options = Ext.apply({
		            page: page,
		            start: (page - 1) * size,
		            limit: size,
		            addRecords: !me.getClearOnPageLoad()
		        }, options);
		        me.read(options);
		    }
	    };
	    
	    var filterParam = me.loadFilterConfig();
	    if(filterParam)
	       me.gridConfigObj.storeConfig.proxy.extraParams.filterParam = filterParam;
	},
	loadFilterConfig:function(){
	    var filterConfig = this.filterConfig;
	    if(!this.filterConfig || !this.filterConfig.checkedCodes)
	        return false;
	        
		var filterParam = {
	        	 				field:this.filterConfig.columnIndex,
	        	 				itemtype:"C",
	        	 				expr:'or',
	        	 				factor:[]
	    };
	    var checkedCodes = this.filterConfig.checkedCodes.split(",");
	    
	    for(var i=0;i<checkedCodes.length;i++){
	         if(checkedCodes[i].length<1)
	             continue;
	        	 filterParam.factor.push("=`"+checkedCodes[i]);
	    }
	    return Ext.encode(filterParam);
	},
	/**
	 *创建grid json对象
	 */
	createTableObj:function(columnArray){
	    var me = this,
		tableConfig = {
	        id:me.prefix+"_tablePanel",
	        bodyStyle:'z-index:2',//处理放大缩小浏览器 缺线问题， wangb 20190524
	        store:me.prefix+'_dataStore',
	        topGrid:true,
	        bufferedRenderer:false,
	        columnLines:true,
	        columns:columnArray,
	        sortableColumns:me.config.sortable,
	        enableLocking:me.config.lockable,
	        dockedItems:[me.config.isPageTool?{
		        xtype: 'pagingtoolbar',
		        id:me.prefix+'_pagingtool',
		        dock: 'bottom',
                limitPageSize:me.config.limitPageSize,
                showDisplayInfo:me.config.showDisplayInfo,
		        store:me.prefix+'_dataStore',
		        simpleModel:me.config.simpleModel,
                listeners:{
                    beforechange:me.autoSave,
                    scope:me

                }
			}:{}],
			plugins:[],
			features:[],
			viewConfig:{
			   filterable:me.config.isColumnFilter,
			   analysable:me.config.fieldAnalyse,
			   operaScope:me.controller
			   /* ,
			   renderRows: function(rows, columns, viewStartIndex, out) {
			        var me = this,
			            rowValues = me.rowValues,
			            rowCount = rows.length,
			            i;
			
			        rowValues.view = me;
			        rowValues.columns = columns;
			        
			        // The roles are the same for all data rows and cells
			        rowValues.rowRole = me.rowAriaRole;
			        me.cellValues.cellRole = me.cellAriaRole;
			
			        for (i = 0; i < rowCount; i++, viewStartIndex++) {
			            rowValues.itemClasses.length = rowValues.rowClasses.length = 0;
			            me.renderRow(rows[i], viewStartIndex, out);
			        }
			
			        // Dereference objects since rowValues is a persistent on our prototype
			        rowValues.view = rowValues.columns = rowValues.record = null;
			        
			        if(this.mousePrePage){
				        Ext.callback(function(){
				        		this.scrollTo(10, Infinity);
				        },this.getScrollable(),[],100);
				        delete this.mousePrePage;
			        }
			    }
			    */
			}
	    };
	    me.gridConfigObj.tableConfig = tableConfig;
	},
	
	//创建tool json对象
	createToolAndMenuObj:function(){
		
	  if(this.config.custommenus) 
	   this.gridConfigObj.menuConfig = {
	   	  id:this.prefix+"_menubar",
	      xtype:'toolbar',
	      items:this.config.custommenus
	   };
	   
	   if(this.config.customtools || this.config.searchFuncId || this.config.isAnalyse){
	       var items = this.config.customtools?this.config.customtools:[]
		   this.gridConfigObj.toolConfig = {
		      id:this.prefix+"_toolbar",
		      xtype:'toolbar',
		      style:'border:none;',
		      defaults:{
		      	height:22
		      },
		      items:items//this.config.customtools
		   };
	   }
	},
	//初始化插件
	insertPluginsOrFeatures:function(){
	    var me = this,
	        keys = me.keyParams;
	    if(keys.doSummary)
	        me.gridConfigObj.tableConfig.features.push({ftype:'summary',remoteRoot:'summaryData',dock:'bottom'});
	    if(keys.groupColumn)    
	        me.gridConfigObj.tableConfig.features.push({ftype:'grouping',groupHeaderTpl:['{columnName}: {name:this.formatName}&nbsp;&nbsp;&nbsp;&nbsp;({children.length}条)',{formatName:function(name){if(isNaN(name) && name.indexOf('`')!=-1)return name.split('`')[1];else return name;}}]});
	    if(me.config.editable)
	        me.gridConfigObj.tableConfig.plugins.push({ptype:'cellediting',clicksToEdit:1,listeners:{beforeedit:function(e,c){
	        c.column.currentRecord = c.record;
	        }}});
	    if(me.config.selectable)
	        me.gridConfigObj.tableConfig.selModel={
	        		selType:'checkboxmodel',
	        		level:me.columnLevel,
	        		getHeaderConfig: function() {
			        var me = this,
			            showCheck = me.showHeaderCheckbox !== false;
			        return {
			            xtype: 'gridcolumn',
			            ignoreExport: true,
			            isCheckerHd: showCheck,
			            text : '&#160;',
			            clickTargetName: 'el',
			            width: me.headerWidth,
			            sortable: false,
			            draggable: false,
			            resizable: false,
			            hideable: false,
			            menuDisabled: true,
			            level:me.level,
			            dataIndex: '',
			            tdCls: me.tdCls,
			            cls: showCheck ? Ext.baseCSSPrefix + 'column-header-checkbox ' : '',
			            defaultRenderer: me.renderer.bind(me),
			            editRenderer: me.editRenderer || me.renderEmpty,
			            locked: me.hasLockedHeader(),
			            processEvent: me.processColumnEvent
			        };
			    }
	        	};
	    
	},
	
	//将参数转化为对象
	createExtComponents:function(){
	    var me = this,
	    configs = me.gridConfigObj;
	    bodyItems = new Array();
	    if(configs.menuConfig){
	    		me.menuBar = Ext.widget('toolbar',configs.menuConfig);
	    		bodyItems.push(me.menuBar);
	    	}
	    	var topToolPostion = me.config.toolPosition!='bottom'?true:false;
	    	if(configs.toolConfig){
	    		me.toolBar = Ext.widget('toolbar',configs.toolConfig);
	    	}
	    	//toolbar 位置 top
	    if(me.toolBar && topToolPostion)
	    		bodyItems.push(me.toolBar);
	    me.dataStore = Ext.create("Ext.data.Store",configs.storeConfig);
	    me.tablePanel = Ext.widget("gridpanel",configs.tableConfig);
	    me.tablePanel.region='center';
	    var tableContainer = Ext.widget('container',{
	         flex:10,
	         border:0,
	         layout:'border',
	         items:me.tablePanel
	    });
	    bodyItems.push(tableContainer);
	     
	    //toolbar 位置 bottom
	    if(me.toolBar && !topToolPostion)
	    		bodyItems.push(me.toolBar);
	    me.bodyPanel = Ext.widget("panel",{
	        width:'100%',
	        height:'100%',
	        tools:me.config.title?[{xtype:'container',itemId:'titleBar'}]:undefined,
	        title:me.config.title,
	        border:0,
	        layout:{
	          type:'vbox',
	          align:'stretch'
	        },
	        items:bodyItems
	    });
	    
	},
	
	addEvent:function(){
	    var me = this;
	    
	    //屏蔽浏览器右键菜单
		Ext.getDoc().on("contextmenu", function(e){
			e.stopEvent();
		});
	    
	    //工具按钮添加监听
		var buttons = me.bodyPanel.query('button[cusBtn=cusBtn]');
		for(var i=0;i<buttons.length;i++){
			buttons[i].on('click',this.handleClick,this);
		}
		//菜单添加监听
		var menutools = me.bodyPanel.query('menuitem[cusMenu=cusMenu]');
		for(var i=0;i<menutools.length;i++){
			menutools[i].on('click',this.handleClick,this);
		}
	    
		var gridEvent = {
			// 点击表格容器空白地方 事件 
			containerclick:function(t,e,eOpts){//t代表this(gridPanel)表格视图对象 在IE浏览器中 blur()事件有效 wangb  26566 20170810
	    			t.blur();
	    		},
			render:function(panel){
		    		Ext.create('Ext.tip.ToolTip', {
					    target: panel.body,
					    delegate:"td > div.x-grid-cell-inner",
					    shadow:false,
					    id:me.subModuleId+'_celltip',
					    trackMouse: true,
					    maxWidth:800,//最大显示宽度设置为800，文本宽度超过800显示不全解决不了，高度超过显示的最大高度同样也显示不全解决不了  27734 wangb 20170517
					    renderTo: Ext.getBody(),
					    bodyStyle:"background-color:white;border:1px solid #c5c5c5;",
					    listeners: {
					        beforeshow: function updateTipBody(tip) {
					        	    var div = tip.triggerElement;//.childNodes[0];
					        	    if (Ext.isEmpty(div))
					        	    	return false;
						        	if(div.offsetWidth < div.scrollWidth || div.offsetHeight < div.scrollHeight-4){ 
						        		//div 里面内容不允许自动换行，滚动条隐藏 27250 27734  wangb 2017-4-24 and 又换回最初状态 
						        		tip.update("<div>"+div.innerHTML+"<br/>  <br/>  </div>");
						        	}else
						        		return false;
					        }
					    }
		    		});
		    		
		    		
		    		if(!me.subModuleId)
		    			return;
		    		
		    		/*鼠标滚动翻页功能 start
		    		 * 普遍反映鼠标滚轮翻页不好用，此功能去掉 guodd 2019-06-03
		    		var view = panel.getView();
		    		var lockview;
		    		if(view.normalView){
		    			lockview = view.lockedView;
		    			view = view.normalView;
		    			
		    		}
		    		
		    		var viewevent = {
		    			mousewheel:{
		    				element:'el',
			    			fn:function(eo){
			    				
			    				var deltaY = eo.browserEvent.deltaY;
			    				if(!deltaY){
			    					deltaY = eo.browserEvent.wheelDelta;
			    					deltaY = -deltaY;
			    				}
			    				
			    				//表格控件引用ext4在ie下，eo.delegatedTarget不存在导致报错，故此处做下处理。 add hej 20180227
			    				var target = eo.delegatedTarget?eo.delegatedTarget:eo.browserEvent.srcElement;
			    				var scrollTop = target.scrollTop;
			    				if(deltaY<0 && scrollTop==0 && this.store.currentPage>1){
			    					me.autoSave();
                               this.store.previousPage();
                               //this.mousePrePage=true;
                               return;
                            }
                            
                            
                            var clientHeight = target.clientHeight;
                            var scrollHeight = target.scrollHeight;
                            var pageCount = Math.ceil(this.store.totalCount/this.store.pageSize);
                            if(deltaY>0 && scrollTop+clientHeight>=scrollHeight && this.store.currentPage<pageCount){
                            		me.autoSave();
                                this.store.nextPage();
                            }
			    			},
			    			scope:view
		    			},
		    			wheel:{
		    				element:'el',
			    			fn:function(eo){
			    				var deltaY = eo.browserEvent.deltaY;
			    				if(!deltaY){
			    					deltaY = eo.browserEvent.wheelDelta;
			    					deltaY = -deltaY;
			    				}
			    				
			    				
			    				var scrollTop = eo.delegatedTarget.scrollTop;
			    				if(deltaY<0 && scrollTop==0 && this.store.currentPage>1){
			    					me.autoSave();
                               this.store.previousPage();
                               this.mousePrePage=true;
                               return;
                            }
                            
                            
                            var clientHeight = eo.delegatedTarget.clientHeight;
                            var scrollHeight = eo.delegatedTarget.scrollHeight;
                            var pageCount = Math.ceil(this.store.totalCount/this.store.pageSize);
                            if(deltaY>0 && scrollTop+clientHeight>=scrollHeight && this.store.currentPage<pageCount){
                            		me.autoSave();
                                this.store.nextPage();
                            }
			    			},
			    			scope:view
		    			}
		    		};
		    			
		    		view.on(viewevent);
		    		if(lockview)
		    			lockview.on(viewevent);
		    		
		    		鼠标滚动翻页功能 end*/  
		    	},
		    	scope:me.controller
				
		};
		//启用栏目设置功能
		if(me.config.isSetScheme){
			gridEvent.columnresize=me.controller.saveColumnWidth;
			gridEvent.columnmove = me.controller.saveColumnMove;
			gridEvent.grouplockcolumn = me.controller.saveColumnLock;
			gridEvent.groupunlockcolumn = me.controller.saveColumnLock;
			gridEvent.columnlockmove = me.controller.saveColumnMove;
		}
	    //grid添加上下箭头监听和提示信息
		if(me.subModuleId){
			gridEvent.beforeitemkeydown=function(panel,b,c,d,e){
				var datasize = me.dataStore.getCount();
			    var currentPage = me.dataStore.currentPage;
			   
			    var pagingbar = Ext.getCmp(me.prefix+"_pagingtool");
			    if(!pagingbar)return;
			    var pagecount = pagingbar.getPageData().pageCount;
			    
			    var complateEdit = function(){
			    		var editor = me.tablePanel.findPlugin('cellediting');
			    		if(editor)
			    			editor.completeEdit( );
			    };
				if(e.keyCode == 40 && d==datasize-1 && currentPage<pagecount){
					complateEdit();
					me.dataStore.nextPage({callback:function(){
						Ext.getCmp(me.prefix+"_tablePanel").getSelectionModel().select(0); 
					}});
				}else if(e.keyCode == 38 && d==0 && currentPage>1){
					complateEdit();
					me.dataStore.previousPage({callback:function(records){
						Ext.getCmp(me.prefix+"_tablePanel").getSelectionModel().select(records.length-1); 
					}});
				}
			};
		}
		
		if(!me.config.editable && me.config.rowdbclick){
			var func = eval(me.config.rowdbclick);
			gridEvent.itemdblclick=func;
		}
		if(me.config.fieldAnalyse && !(me.config.contextAnalyse===false)){
			gridEvent.cellcontextmenu = function(grid){
				
				var lockedGrid = grid.lockedGrid;
				var normalGrid = grid.normalGrid;
				var index = arguments[2];
				if(lockedGrid && normalGrid.owns(arguments[1]))
			         index += lockedGrid.getColumnManager().getColumns().length;
				var column = grid.getColumnManager( ).getHeaderAtIndex(index);
				
				if(column.xtype!='codecolumn')
				     return;
				grid.contextmenu = grid.contextmenu || Ext.create('Ext.menu.Menu', {
				     items:{
				     	text:'',
				     	handler:me.controller.singleItemAnalyse,
				     	scope:me.controller
				     }
				});
				grid.contextmenu.items.items[0].setText('按'+column.text+'统计分析');
				grid.contextmenu.items.items[0].column = column;
				grid.contextmenu.showAt(arguments[6].getXY());
			}
		}
		
	    me.tablePanel.on(gridEvent);
	    me.dataStore.on({
	    		  load:function(s,records){
	    		  		if(this.mousePrePage){
	    		  			var view = this.tablePanel.getView();
	    		  			if(view.normalView)
	    		  				view = view.normalView;
	    		  			view.getScrollable().scrollToEnd();
	    		  			this.mousePrePage = undefined;
	    		  		}
	    		  
	    		  
			       if(!this.keyParams.doSummary)
			          return;
			       var view = this.tablePanel.getView();;
			       if(view.lockedView)
			          view = view.lockedView;
			       var summaryfeature = view.findFeature('summary');
			       if(records.length>0){
			          summaryfeature.toggleSummaryRow(true);
			          s.summaryData = summaryfeature.summaryRows[0];
			       }else{
			       	  summaryfeature.toggleSummaryRow(false);
			       	  s.summaryData = undefined;
			       }
			   },
			   scope:me
	    });
	},
	
	autoSave:function(){
		//如果页面有编辑，不翻页
		var store = this.dataStore;
		var plugin  = this.tablePanel.findPlugin('cellediting');
		if(plugin)
			plugin.completeEdit( );
		if(store.getRemovedRecords( ).length>0 || store.getUpdatedRecords( ).length>0 || store.getNewRecords( ).length>0){
			var saveBtn;
			if(this.toolBar && (saveBtn = this.toolBar.query('button[fntype=save]')).length>0){
				var saveBtn = saveBtn[0];
				this.handleGlobalFunc(saveBtn,true);
			}
		}
        return true;
	
	
	},
	handleClick:function(fireObj){
	    //内置功能性按钮
	    if(fireObj.fntype){
			this.handleGlobalFunc(fireObj);
			return;
		}
		var xtype = fireObj.xtype;
		var fn = fireObj.fn;
		//没有监听
		if(fn.length<1)
			return;
		
		var param = {tablekey:this.subModuleId,subModuleId:this.subModuleId,targetid:fireObj.id};
		Ext.apply(param,fireObj.params);
		
	    var paramArray = [];
	    paramArray.push(param);
		//fn = eval(fn);
		
		//如果是带选框的菜单按钮，特殊处理一下
		if(xtype == 'menucheckitem'){
			if(!menuItem.group){ //分组菜单
				paramArray.push(menuItem.checked);
				//fn(param,menuItem.checked);
			}else{
				//fn(param,menuItem.value);
				paramArray.push(menuItem.value);
			}
			return;
		}
		//普通按钮触发
		if(fireObj.getdata && this.config.selectable){
			//获取选中数据
			var selectModel = this.tablePanel.getSelectionModel();
			var selectedItems = selectModel.getSelection();
			//fn(param,selectedItems,this.dataStore,selectModel.doSelectAll);
			paramArray.push(selectedItems);
			paramArray.push(this.dataStore);
		}else{
			//fn(param);
		}	
		if(this.handlerScope==null){
			fn = eval(fn);
		}
		Ext.callback(fn,this.handlerScope,paramArray);	
	},
	
	handleGlobalFunc:function(obj,silence){
		var me = this;
		var hashvo = new HashMap();
		hashvo.put("tablekey",this.subModuleId);
		hashvo.put("subModuleId",this.subModuleId);
		//fntype 为功能类型
		if(obj.fntype == "analyse"){
			me.controller.planItemsAnalyse();
		}else if(obj.fntype == "scheme"){
		    me.controller.doTableScheme();
		}else if(obj.fntype == "insert"){
			//如果注册了 functionid ，则去交易类里获取数据，将返回的数据插入到表格中
			if(obj.functionid){
				Rpc({functionId:obj.functionid,success:this.insertRecord,scope:this},hashvo);
				return;
			}
			//如果没有注册 功能号，直接插入一条空数据
			this.insertRecord();
		}else if(obj.fntype == "save"){//保存操作
			//如果没有注册交易类号 停止执行	
			if(!obj.functionid){
				if(!silence)
					Ext.Msg.alert('提示信息', "未注册相关业务！");
				return;
			}
			var store = me.dataStore;
			var updates = store.getModifiedRecords();//获取修改的数据。注意：新增的数据也在这里面
			if(updates.length<1)
				return;
			
			var records = [];
			for(var i=0;i<updates.length;i++){
				var record = updates[i].data;
				if(!record.changestate)//添加修改标识，说明是修改操作。
					record.changestate='update';
				records.push(record);
			}
			
			hashvo.put("savedata",records);
			//将数据传入后台，key为‘savedata’
			Rpc({functionId:obj.functionid,scope:me,success:function(res){
				var resultObj = Ext.decode(res.responseText);
				if(resultObj.result!=undefined && !resultObj.result){
					if(!silence)
						Ext.Msg.alert('提示信息',resultObj.hinttext?resultObj.hinttext:"保存失败！");
					return;
				}
				if(!silence){
					Ext.Msg.alert('提示信息',resultObj.hinttext?resultObj.hinttext:"保存成功！");
					//this.reloadStore();
					//保存后留在当页
					this.dataStore.reload();
				}
			    
			}},hashvo);
		}else if(obj.fntype == "delete"){
			
			//获取选中行数据
			var selectRecord = this.tablePanel.getSelectionModel().getSelection();
			if(selectRecord.length<1){
				Ext.Msg.alert('提示信息',"请选择删除数据！");
				return;
			}
			
			var doDelete = function(){
				//如果没有注册交易类号 停止执行	
				if(!obj.functionid){
					Ext.Msg.alert('提示信息', "未注册相关业务！");
					return;
				}
				
				var records = [],
				     keys = me.config.primeryKeys.split(",");
				if(me.config.primeryKeys.length>0){
					for(var i=0;i<selectRecord.length;i++){
					    var data = {};
					    for(var k =0;k<keys.length;k++){
					        if(keys[k].length<1)
					           continue;
					    	data[keys[k]]=selectRecord[i].get(keys[k]);
					    }
						records.push(data);
					}
				}else{
					for(var i=0;i<selectRecord.length;i++){
						records.push(selectRecord[i].data);
					}
				
				}
				hashvo.put("deletedata",records);
				//将数据传入后台，key为‘deletedata’
				Rpc({functionId:obj.functionid,scope:this,success:function(res){
					var resultObj = Ext.decode(res.responseText);
					if(resultObj.result!=undefined && !resultObj.result){
						Ext.Msg.alert('提示信息',resultObj.hinttext?resultObj.hinttext:"删除失败！");
						return;
					}
					
		    			me.reloadStore();
				}},hashvo);
			};
			
			Ext.Msg.confirm("提示信息","确认删除吗？",function(id){if(id=='yes'){doDelete();}});
			
		}else if(obj.fntype == "export_Setting"){
			var hashvo = new HashMap();
			hashvo.put("subModuleId", this.subModuleId);
			Rpc({
				functionId: "ZJ100000009", scope: this, success: function (res) {
					var resultObj = Ext.decode(res.responseText);
					var result=JSON.parse(resultObj.settings);
					var settingsId=resultObj.settingsId;
					result.isExcel=1;
					Ext.require('EHR.exportPageSet.ExportPageSet', function () {//这块的函数向导不需要卡薪资类别，取权限内的全部子集
						Ext.create("EHR.exportPageSet.ExportPageSet", {
							result: result, callbackfn:
								function (pagesetupValue, titleValue, pageheadValue, pagetailValue, textValueValue, type, data) {
									hashvo.put("data", JSON.stringify(data));
									hashvo.put("settingsId", settingsId);
									Rpc({
										functionId: "ZJ100000010", scope: this, success: function (rep) {
											var rep_result = Ext.decode(rep.responseText);
											if(rep_result.succeed!=true){
												Ext.Msg.alert('提示信息',rep_result.message);
											}
										}
									}, hashvo);

								}
						});
					})

				}
			}, hashvo);






		}else{//export
			var myMask = new Ext.LoadMask({
			    msg    : '正在导出......',
			    target : this.bodyPanel
			}).show();
			var displaycolumns = this.tablePanel.getColumnManager().getColumns();
			var outputcolumns = new Array();
			var column;
			var level = 1;
			for(var i in displaycolumns){
				column =  displaycolumns[i];
				
				if(column.dataIndex && column.dataIndex.length>0 && !column.hidden && column.beExport){
				    var ups = [];
				    this.getUpColumnText(column,ups);
				    level = ups.length+1>level?ups.length+1:level;
					outputcolumns.push({columnid:column.dataIndex,width:column.width,operationData:column.operationData?column.operationData:[],ups:ups});
				}
			}
			hashvo.put("outputcolumns",outputcolumns);
			hashvo.put("headLevel",level);
			hashvo.put("showRowNumber",me.config.showRowNumber);
			if(me.config.selectable){
				var selectRecord = me.tablePanel.getSelectionModel().getSelection();
				if(selectRecord.length>0){
					var outputdata = new Array();
					for(var k in selectRecord){
						outputdata.push(selectRecord[k].data);
					}
					
					hashvo.put("outputdata",outputdata);
				}
			}
			hashvo.put("summaryData",me.dataStore.summaryData);
			Rpc({functionId:'9030000001',timeout:300000,scope:this,success:function(res){
			    myMask.destroy( );
				var resultObj = Ext.decode(res.responseText);
				var outName = resultObj.filename;
				//zhangh 2020-3-5 下载改为使用VFS
				outName = decode(outName);
				var win=open("/servlet/vfsservlet?fromjavafolder=true&fileid="+outName);
			}},hashvo);
		}
	
	
	},
	
	getUpColumnText:function(column,ups){
	     if(column.ownerCt.xtype=='gridcolumn'){
	         ups.push(column.ownerCt.text+'`'+column.ownerCt.id);
	         this.getUpColumnText(column.ownerCt,ups);
	     }
	},
	
	//最外层容器
	createMainContainer:function(){
		if(this.config.autoRender)
			this.mainPanel  = Ext.widget("viewport",{
			  layout:'fit',
			  id:this.prefix+"_mainPanel",
			  items:this.bodyPanel
			});
		else
			this.mainPanel = Ext.widget("container",{
			    width:'100%',
			    id:this.prefix+"_mainPanel",
			    height:'100%',
			    layout:'fit',
			    items:this.bodyPanel
			});
	},
	
	//添加栏目设置和搜索功能
	addOtherWidget:function(){
	    var me = this;
	    
	    if(me.config.isAnalyse && me.config.doAnalyse==undefined)
	 		   me.toolBar.add({xtype:'button',text:'统计分析',handler:me.controller.planItemsAnalyse,scope:me.controller});
	 		   
	 	if(me.config.searchFuncId){
	 		var fieldsArray = [];
	    		Ext.each(me.repertory.fullColumns,function(c){
	    		
	    		    if(me.config.openColumnQuery){
	    		        if(!c.queryable)
	    		            return;
	    		    }else{
	    		    		if(c.fieldsetid.length<1)
	    		    		    return;
	    		    }
	    				
	    			var obj = {
		    				type:c.columnType,
		    				itemid:c.columnId,
		    				itemdesc:c.columnDesc,
		    				codesetid:c.codesetId,
		    				format:c.format,
		    				codesource:c.codesource,
		    				ctrltype:c.ctrltype,
		    				nmodule:c.nmodule,
		    				parentid:c.parentid,
		    				codesetValid:c.codeSetValid,
		    				operationData:c.operationData
		    			};
	    			
	    			fieldsArray.push(obj);
	    		});
	    		if(me.toolBar){
		    		var queryboxid = me.prefix+"_querybox";
		    		var index = me.config.queryBoxIndex;
		    		if(!index)
		    		   index = me.toolBar.items.getCount();
		    		me.toolBar.insert(index,{
			    			xtype:'querybox',
			    			id:queryboxid,
			    			funcId:me.config.searchFuncId,
			    			subModuleId:me.subModuleId,
			    			customParams:{tableName:'myGridData'},
			    			fieldsArray:fieldsArray,
			    			hideQueryScheme:!me.config.showPlanBox,
			    			emptyText:me.config.searchText,
			    			callBackScope:me,
			    			success:me.reloadStore
			    	});
			}
	 	}  
	    
	    
	    
	    if(me.config.isSetScheme && me.config.schemePosition!='custom'){
	          var createCom = function(){
	        	  	return Ext.widget('image',{
	        	  		id:me.prefix+"_schemeBtn",
		  				xtype:'image',
		  				title:'栏目设置',
		  				height:17,
		  				width:17,
		  				border:0,
		  				style:'cursor:pointer',
		  				src:'/components/tableFactory/tableGrid-theme/images/Settings.png',
		  				listeners:{
		  				    click:{
		  				        element: 'el',
		  				        fn:function(){
		  				            me.controller.doTableScheme();
		  				           /* Ext.require("EHR.tableFactory.plugins.SchemeSetting",function(){
		  				                 var callback = me.config.schemeSaveCallback?eval(me.config.schemeSaveCallback):function(){
		  				                     window.location.reload();
		  				                 };
		  							     new EHR.tableFactory.plugins.SchemeSetting({
		  							    	 	 subModuleId:me.subModuleId,
		  							    	     schemeItemKey:me.config.schemeItemKey?me.config.schemeItemKey:'',
		  							    	     itemKeyFunctionId:me.config.itemKeyFunctionId?me.config.itemKeyFunctionId:'',
		  							    	     moduleId:me.config.moduleId,
		  							    	     closeAction:callback,
		  							    	     viewConfig:{
		  							    	    	 	publicPlan:me.config.showPublicPlan
		  							    	     }
		  							     });
		  							});
		  							*/
		  				        }
		  				    }
		  				}
		  				
		  			});
	          };

	         /**
	          * 根据后台参数设置栏目设置的位置
	          * */
	        if(me.config.schemePosition=='title'){
	           if(me.config.title)
                        me.bodyPanel.addTool(createCom());
	        	
	        }else if(me.config.schemePosition=='toolbar'){
	        	me.toolbarScheme = true;
                        me.toolBar.add(['->',createCom()]);
	        }else if(me.config.schemePosition=='menubar'){
	        	 me.menuBar.add(['->',createCom()]);
	        }else if(me.config.schemePosition!=null&&me.config.schemePosition.length>0){
	        	var cmp = Ext.getCmp(me.config.schemePosition);
	        	if(cmp)
	        	    cmp.add(['->',createCom()]);
	        }else{
	           if(me.config.title)
                        me.bodyPanel.addTool(createCom());
               else if(me.config.custommenus)
                        me.menuBar.add(['->',createCom()]);
               else if(me.config.customtools){
                        me.toolbarScheme = true;
                        me.toolBar.add(['->',createCom()]);
               }else{
                        me.config.isSetScheme=false;
               }
	        }
	    }
	    
	},
	reloadStore:function(){
	   this.dataStore.load({page:1});
	},
	// border 布局时，设置 控件 region
	setBorderLayoutRegion:function(region){
		this.mainPanel.region = region;
	},
	// 动态插入 组件
	insertItem:function(obj,index){
		if(Ext.isNumber(index))
		  this.bodyPanel.insert(index,obj);
		else
		  this.bodyPanel.add(obj);
	},
	getMainPanel:function(){
	   return this.mainPanel;
	},
	// 获取 参数
	getTableConfig:function(){
	    return this.config;
	},
	getTitleBar:function(){
		var bar = this.bodyPanel.queryById('titleBar');
		return bar;
	},
	//渲染 控件
	renderTo:function(id){
		this.mainPanel.render(id);
	},
	
	// 设置 控件 flex
	setlayoutFlex : function(flex){
		this.mainPanel.flex = flex;
	},
	cleanSearchItems : function(){
		var querybox = Ext.getCmp(this.prefix+"_querybox");
		if(querybox)
			querybox.removeAllKeys();
		
	},
	/**
	 * 单列统计 执行之前调用的方法
	 */
	beforeColumnAnalyse : function(action){
		this.listenerConfig.beforeColumnAnalyse = action;
	},
	setSchemeViewConfig:function(viewConfig){
		this.controller.schemeViewConfig = viewConfig;
	},
	doSetScheme:function(){
	    this.controller.doTableScheme();
	},
	setHandlerScope:function(scope){
		this.handlerScope = scope;
	}
});