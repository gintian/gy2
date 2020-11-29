/**
 * 项目信息管理主界面调用js
 * 
 * @author chenxg
 */
Ext.Loader.loadScript({url: rootPath + '/components/tableFactory/ext_custom.js'});

Ext.define('ProjectManageTemplateUL.projectmanage',{
	requires:[
	  	    'ProjectManageTemplateUL.TreeTableController'
	  	],
	projectManage:'',
	tableObj:'',
	deletedProjectIds:'',
	deleteMilestoneIds:'',
	// 选中记录的编号
	projectIds:'',
	constructor:function(config) {
		projectManage = this;
		itemLength = 0;
		itemValue = "";
		limit = 20;
		flag = false;
		projectManage.prefix="projectmanage_0001";
		projectManage.newData={};
		projectManage.controller = Ext.create("ProjectManageTemplateUL.TreeTableController",projectManage);
		this.init();
	},
	// 初始化函数
	init: function() {
		//加载自定义类
		Ext.Loader.loadScript({url:'/components/extWidget/proxy/TransactionProxy.js'});
		Ext.Loader.loadScript({url:'/ext/ext6/ext-additional.js'});
		var map = new HashMap();
	    Rpc({functionId:'PM00000001',success:projectManage.loadTable},map);
	},
	
	loadTable: function(response){
		var value = response.responseText;
		var map	 = Ext.decode(value);
		if(map.succeed){
		    var fields = Ext.decode(getDecodeStr(map.dataModel));
		    var columns = Ext.decode(getDecodeStr(map.panelColumns));
		    var buttons = Ext.decode(getDecodeStr(map.buttons));
		    var projectStage = Ext.decode(getDecodeStr(map.projectStage));
		    var fieldsArray = Ext.decode(getDecodeStr(map.fieldsArray));
		    var scheme = getDecodeStr(map.scheme);
		    var pageRows = getDecodeStr(map.pageRows);
		    projectManage.showPanel(fields,columns,pageRows,projectStage,buttons,fieldsArray,scheme);
		} else
			Ext.Msg.alert("提示信息", map.message);
			
	},
	
	showPanel: function(fields,columns,pageRows,projectStage,buttons,fieldsArray,scheme){
		projectManage.itemLength = projectStage.length;
		
		if(Ext.util.CSS.getRule(".scheme-selected-cls")){
			Ext.util.CSS.updateRule(".scheme-selected-cls","text-decoration","underline");
			Ext.util.CSS.updateRule(".scheme-selected-cls","margin-left","10px");
			Ext.util.CSS.updateRule(".scheme-selected-cls","margin-right","10px");
		}else
			Ext.util.CSS.createStyleSheet(".scheme-selected-cls{text-decoration:underline;margin-left:10px;margin-right:10px}","underline"); 
		
		Ext.util.CSS.createStyleSheet(".x-grid-cell-inner{max-height:30px;padding: 2px 6px 3px 6px;}"); 
		
		var tbar1 = new Ext.Toolbar({
			id:'toolbar1',
			height:25,
			padding:'0 0 0 5',
			border:false,
			items:projectStage
		}); 
		
		var tbar2 = new Ext.Toolbar({
			id:'toolbar2',
			height:25,
			padding:'0 0 0 5',
			border:false,
			items:buttons
		}); 
		
	    var north = new Ext.Panel( {
			xtype : 'panel',
			title:'<ul><li style="float: left;padding-top:5px;">项目管理</li><li style="float: right;">' + scheme + '</li></ul>',
			id : 'north',
			height:93,
			items:[tbar1, tbar2],
			region : 'north',
			border : false			
		});

		var center = new Ext.Panel( {
			xtype : 'panel',
			id : 'center',
			region : 'center',
			layout : 'fit',
			width: '100%',
	        height: '100%',
			style:'background-color: #FFFFFF;',
			border : false
		});
		
		new Ext.Viewport( {
			id:"port",
			layout : "border",
			items:[north,center],
			listeners:{
				remove:{
					fn:function(port){
						projectManage.removeStyle();
					}
				}
			}
		});
		
		projectManage.flag = false;
	    projectManage.showTreeTable(fields,columns,pageRows);
	    projectManage.createSearchPanel(fieldsArray);
	},
	
	showTreeTable: function(fields,columns,pageRows){
	    Ext.define('Task', {
	        extend: 'Ext.data.TreeModel',
	        id:'projectmanage_0001_model',
	        fields: fields
	    });

	    var store = Ext.create('Ext.data.TreeStore', {
	    	id:'projectmanage_0001_dataStore',
	        model: 'Task',
	        proxy:{
	    	    type:'transaction',
			    functionId:'PM00000001',
			    extraParams:{
					itemId:projectManage.itemValue,
					flag:'1',
					page:pageNum + "",
					limit:pageRows + ""
				},
				reader:{
				   type:'json',
	               root:'data',
	               totalProperty:'totalCount'
				}
			},
			listeners: {
				nodebeforeexpand:function (node, eOpts){
            		store.on("beforeload",function(){
	    				Ext.apply(store.proxy.extraParams,{p1101 : node.data.p1101});
	    			});
				}
			}
	    });
	    
	    var pageStore = new Ext.create('Ext.data.Store', {
		    id: "projectmanage_0001_pageStore",
		    pageSize:pageRows,
		    currentPage:pageNum +"",
		    fields:[],
		    autoLoad : true,
		    proxy:{
				type:'transaction',
				functionId:'PM00000001',
				extraParams:{
					itemId:projectManage.itemValue,flag:'1',
					filterParam:''
				},
				reader:{
				   type:'json',
	               root:'data',
	               totalProperty:'totalCount'
				}
			}
		});
	    
	    var pagingToolbar = new Ext.PagingToolbar( {
	    	id:"projectmanage_0001_pagingToolbar",
	    	pageSize:pageRows + "",
			emptyMsg : "没有数据",
			displayInfo : true,
			height: 30,
			displayMsg : "显示{0}-{1}条，共{2}条",
			store : pageStore,
			listeners : {
				change : function(obj, pdata, options) {
	    			store.on("beforeload",function(){
	    				var page = '1';
	    				if(typeof(pdata) != "undefined" ) {
	    					page = pdata.currentPage + "";
	    					pageNum = pdata.currentPage + "";
	    					pageRows = obj.child('#inputCount').getValue() + "";
	    				}
	    				
	    				Ext.apply(store.proxy.extraParams,{
	    					page : page + "",
	    					limit : obj.child('#inputCount').getValue() + "",
	    					itemId: projectManage.itemValue,
	    					filterParam: pageStore.proxy.extraParams.filterParam,
	    					p1101 : ''
	    				});
	    			});
	    			
	    			if(projectManage.flag)
	    				store.load();
	    			else
	    				projectManage.flag = true;

				}

			}
		});
	    
	    Ext.define("EHR.my.selection.CheckboxModel",{
	    	extend:'Ext.selection.CheckboxModel',
	    	renderer: function(value, metaData, record, rowIndex, colIndex, store, view) {
					if(''==record.get("p1101") && ""==record.get("p1201"))
						return '';
					else
						return '<div class="' + Ext.baseCSSPrefix + 'grid-row-checker" role="presentation">&#160;</div>';
				}
	    });
	    
	    var checkBox = Ext.create('EHR.my.selection.CheckboxModel'); 
	    var gtree = Ext.create('Ext.tree.Panel', {
	        id:'projectmanage_0001_treegrid', 
	        width: '100%',
	        height: '100%',
	        collapsible: false,
	        useArrows: true,
	        rootVisible: false,
	        selModel:checkBox,
	        store: store,
	        multiSelect: false, 
	        rowLines: true,
	        columnLines: true,
	        stripeRows:true,
	        columns:columns,
	        bbar: pagingToolbar,
	        viewConfig:{
			    filterable:true,
			    markDirty: false,
			    operaScope:projectManage.controller
			},
	        listeners : {  
	    		render : function(tree){
	    			
			    	Ext.create('Ext.tip.ToolTip', {
			    		target: tree.id,
					    delegate:"td",
					    trackMouse: true,
					    renderTo: document.body,
					    bodyStyle:"background-color:white;border:1px solid #c5c5c5;",
			    	    listeners: {
						    beforeshow: function updateTipBody(tip) {
					            var div = tip.triggerElement.childNodes[0];
					            var title = "";
					            if (Ext.isEmpty(div))
					            	return false;
					        	    
						       	if(div.offsetWidth < div.scrollWidth || div.offsetHeight < div.scrollHeight){
						       		var havea = div.getElementsByTagName("a");
						            if(havea != null && havea.length > 0){
						            	title = havea[0].innerHTML;
						            } else 
						            	title = div.innerHTML;
						       		
						       		title = trimStr(title);
						       		if(Ext.isEmpty(title))
						       			return false;
						       		
						       		tip.update("<div style='WORD-BREAK:break-all;'>"+title+"</div>");
						       	}else
						       		return false;
					        }
					    }
			    	});
			    	
            	},
            	//保存列宽
            	columnresize: function (treeGrid,column,width){
            		if(!column.dataIndex)
        	    	    return;
            		
            		var para = new HashMap();
	        		Ext.apply(para,{
	        			subModuleId:'projectmanage_0001',
	        		    updateType:'widthUpdate',
	        		    itemid:column.dataIndex,
	        		    width:width,
	        			actionName:'update'
	        		});
	        		
	        		Rpc({
	        			functionId:'ZJ100000001'
	        		},para);
            	},
            	//保存移动后的列的位置
            	columnmove: function(treeGrid,column){
            		if(!column.dataIndex)
           		   	 return;
            		
	           		var is_lock = column.isLocked()?'1':'0';
	           		var index = treeGrid.columnManager.getHeaderIndex(column);
	           		var nextcolumn = treeGrid.columnManager.getHeaderAtIndex(index+1);
	           		var nextid = "-1";
	           		if(nextcolumn && nextcolumn.dataIndex)
	           			nextid = nextcolumn.dataIndex;
	           			
	           		var para = new HashMap();
	           		Ext.apply(para,{
	           			subModuleId:'projectmanage_0001',
	           			updateType:'positionUptate',
	           			itemid:column.dataIndex,
	           			is_lock:is_lock,
	           			nextid:nextid,
	           			actionName:'update'
	           		});
	           		Rpc({
	           			functionId:'ZJ100000001'
	           		},para);
            	}
        	}  
	    });
	    Ext.getCmp('center').add(gtree);
	    pagingToolbar.moveFirst();
	},
	// 查询控件
	createSearchPanel: function(fieldsArray){
		var me = this;
		var map = new HashMap();
		
		me.SearchBox = Ext.create("EHR.querybox.QueryBox",{
			id:'queryBox',
			hideQueryScheme:false,
			emptyText:'请输入名称、描述...',
			subModuleId:'projectmanage_0001',
			customParams:map,
			funcId:"PM00000002",
			fieldsArray:fieldsArray,
			success:projectManage.reloadTable
		});
		
		Ext.getCmp('toolbar2').add(me.SearchBox);
	},
	
	showRemarksData: function(value,c,record){
		return value;
	},
	
	addLinkToEditProjectManagePage:function(value,c,record){
		var p1201 = record.data.p1201;
		if( p1201 != "")
			return "<a href=\"###\" onclick=projectManage.editLandMark('"+record.data.p1101+"','"+record.data.p1201+"')>"+value+"</a>";
		else
			return "<a href=\"###\" onclick=projectManage.editProjectManagePage('"+record.data.p1101+"')>"+value+"</a>";
	},
	
	menHoursDetailPage: function(id,name){	
	/*	Ext.getCmp("port").removeAll();
		Ext.getCmp("port").remove();*/
		var map = new HashMap();
		map.put("projectId", id);
		map.put("name", name);
		Ext.require('ManHoursDetailUL.ManHoursDetail', function(){
			Ext.create("ManHoursDetailUL.ManHoursDetail", map);
		});
	},
	//移除样式
	removeStyle:function(){
		if(Ext.util.CSS.getRule(".scheme-selected-cls"))
			Ext.util.CSS.updateRule(".scheme-selected-cls","text-decoration","none");
		
		if(Ext.util.CSS.getRule(".x-grid-cell")){
			Ext.util.CSS.updateRule(".x-grid-cell","border-bottom","0px solid #ededed");
			Ext.util.CSS.updateRule(".x-grid-cell","border-right","0px solid #d0d0d0");
		}
		
		Ext.util.CSS.removeStyleSheet("underline");
		Ext.util.CSS.removeStyleSheet("gridCell");
		Ext.util.CSS.refreshCache();
	},
	
	editProjectManagePage: function(id){
		projectManage.removeStyle();
		
		var map = new HashMap();
		map.put("projectId", id);
		map.put("type", "edit");
		Ext.require('ProjectManageTemplateUL.projectmanageAdd', function(){
			Ext.create("ProjectManageTemplateUL.projectmanageAdd", map);
		});
		
	},
	
	addLandMark: function(){
		var tablePanel=Ext.getCmp('projectmanage_0001_treegrid');
		var selectRecord = tablePanel.getView().getSelectionModel().getSelection();
		if(selectRecord.length<1){
			Ext.Msg.alert('提示信息',"请选择需要新增里程碑的项目！");
			return;
		}else{
			if(selectRecord.length>1){
				Ext.Msg.alert('提示信息',"请选择一个项目新增里程碑！");
				return;
			}
			
		}

		if("" == selectRecord[0].data.p1101 && "" == selectRecord[0].data.p1201){
			Ext.Msg.alert('提示信息',"请选择需要新增里程碑的项目！");
			return;
		}
		
		var map = new HashMap();
		map.put("projectId", selectRecord[0].data.p1101);
		map.put("type", "landMarkAdd");
		Ext.require('ProjectManageTemplateUL.projectmanageAdd', function(){
			Ext.create("ProjectManageTemplateUL.projectmanageAdd", map);
		});
		
		projectManage.reloadTableData("0");
	},
	
	editLandMark:function(projectId,landMarkId){
		var map = new HashMap();
		map.put("landMarkId", landMarkId+'');
		map.put("projectId", projectId);
		map.put("type", "landMarkEdit");
		Ext.require('ProjectManageTemplateUL.projectmanageAdd', function(){
			Ext.create("ProjectManageTemplateUL.projectmanageAdd", map);
		});
	}
	,
	deleteProjectOrlandMark: function(){
		deletedProjectIds='';
		deleteMilestoneIds='';
		var tablePanel=Ext.getCmp('projectmanage_0001_treegrid');
		var selectRecord = tablePanel.getView().getSelectionModel().getSelection();
		if(selectRecord.length<1){
			  Ext.Msg.alert("提示信息","请选择要删除的项目或里程碑！");
			return;
		}
		
		projectManage.setDeletedIds(selectRecord);
		if(deletedProjectIds.length<1 && deleteMilestoneIds.length<1){
			  Ext.Msg.alert("提示信息","请选择要删除的项目或里程碑！");
			return;
		}
		
		var map = new HashMap();
		map.put("projectIds", deletedProjectIds);
		map.put("milestoneIds", deleteMilestoneIds);
		Ext.Msg.confirm("提示信息", "确认要删除所选数据吗？", function(button, text){  
			if(button != "yes")
				return;
			
			Rpc({functionId : 'PM00000010', async : false,
					success : function(form,action) {
						var result = Ext.decode(form.responseText);
						if(result.msg!='success'){
							Ext.Msg.show({
							     title:'提示信息',
							     msg: '删除失败！',
							     buttons: Ext.Msg.OK,
							     icon: Ext.Msg.WARNING
							});
							return;
						}
						projectManage.reloadTableData("0");
					}
	    	}, map);

		});
	},
	
	menHoursSumPage:function(projectId,projectName,milestone){
		var map = new HashMap();
		map.put("type", 1);
		map.put("projectName", projectName);
		map.put("projectId", projectId);
		map.put("milestone", milestone);
		Ext.require('ManHoursSumUL.ManHoursSum', function(){
			Ext.create("ManHoursSumUL.ManHoursSum", map);
		});
	},
	
	reloadTable: function(response){
		
		if(response.succeed){
			projectManage.reloadTableData("0");
		}
	},
	
	schemeSetting: function(showPublicPlan) {
		Ext.require("EHR.tableFactory.plugins.SchemeSetting",function(){
			var window = new EHR.tableFactory.plugins.SchemeSetting({
					subModuleId:'projectmanage_0001',
					showPublicPlan:showPublicPlan,
					schemeItemKey:'',
					itemKeyFunctionId:'',
					showPageSize:true,
					viewConfig: {
						publicPlan: true,
						order: false,
						merge: false,
						sum: true
					},
					closeAction:projectManage.closeSettingWindow
			});
		});
	},

	closeSettingWindow: function (){
		var map = new HashMap();
		map.put("itemId", projectManage.itemValue);
		map.put("flag", "2");
	    Rpc({functionId:'PM00000001',success:projectManage.searchrTableSucc},map);
	}, 
	
	searchrProjectStage: function(id,itemId){
		for(var i = 0; i < projectManage.itemLength - 2; i++){
			Ext.getCmp('label'+i).removeCls('scheme-selected-cls');
		}
		
		Ext.getCmp(id).addCls('scheme-selected-cls');
		projectManage.itemValue = itemId;
		Ext.getCmp("queryBox").removeAllKeys();
		projectManage.reloadTableData("1");
		
	},
	
	searchrSucc: function(response){
		var value = response.responseText;
		var map	 = Ext.decode(value);
		projectManage.reloadTable(map);
	},
	
	searchrTableSucc: function(response){
		var value = response.responseText;
		var map	 = Ext.decode(value);
		if(map.succeed){
			projectManage.flag = false;
			var fields = Ext.decode(getDecodeStr(map.dataModel));
			var columns = Ext.decode(getDecodeStr(map.panelColumns));
			var pageRows = getDecodeStr(map.pageRows);
			var fieldsArray = Ext.decode(getDecodeStr(map.fieldsArray));
			if(Ext.getCmp('projectmanage_0001_pagingToolbar'))
				Ext.getCmp('projectmanage_0001_treegrid').remove(Ext.getCmp('projectmanage_0001_pagingToolbar'));
			
			Ext.getCmp('center').remove(Ext.getCmp('projectmanage_0001_treegrid'));
			projectManage.showTreeTable(fields,columns,pageRows);
			Ext.getCmp('toolbar2').remove("queryBox", true);
			projectManage.createSearchPanel(fieldsArray);
		}
	},
	
	setDeletedIds:function (selectRecord){ 
		for (var i=0;i<selectRecord.length;i++){
			if('' == selectRecord[i].data.p1101 && '' == selectRecord[i].data.p1201)
				continue;
			
			if (selectRecord[i].data.p1201 == 0)
				deletedProjectIds += selectRecord[i].data.p1101 + ',';
			else
				deleteMilestoneIds += selectRecord[i].data.p1201+',';
		   }
		},
		
	sumHours:function (){
		deletedProjectIds='';
		deleteMilestoneIds='';
		var tablePanel=Ext.getCmp('projectmanage_0001_treegrid');
		var selectRecord = tablePanel.getView().getSelectionModel().getSelection();
		if(selectRecord.length<1){
			   Ext.Msg.alert("提示信息","请选择需要统计的项目！");
			return;
		}
		
		projectManage.setDeletedIds(selectRecord);
		if(deletedProjectIds.length<1 && deleteMilestoneIds.length<1){
			   Ext.Msg.alert("提示信息","请选择需要统计的项目！");
			return;
		}
		
		var map = new HashMap();
		map.put("projectIds", deletedProjectIds);
		map.put("milestoneIds", deleteMilestoneIds);
		Rpc({
			functionId : 'PM00000098',
			async : false,
			success : function(form,action) {
				var result = Ext.decode(form.responseText);
				if(result.msg!='success'){
					Ext.Msg.show({
					     title:'提示信息',
					     msg: '统计项目不成功 ！',
					     buttons: Ext.Msg.OK,
					     icon: Ext.Msg.WARNING
					});
					return;
				}
				projectManage.reloadTableData("0");
			}
	    }, map);
	},
	
	reloadTableData:function (deleteWhere){
		projectManage.flag = true;
		var store = Ext.data.StoreManager.lookup('projectmanage_0001_pageStore');
		store.on("beforeload",function(){
			Ext.apply(store.proxy.extraParams,{
				deleteWhere: deleteWhere,
				itemId: projectManage.itemValue
			});
		}); 
		
		store.reload();
	},
	
	exportExcle:function (){
		deletedProjectIds = "";
		deleteMilestoneIds = "";
		var tablePanel=Ext.getCmp('projectmanage_0001_treegrid');
		var selectRecord = tablePanel.getView().getSelectionModel().getSelection();
		if(selectRecord.length > 0){
			projectManage.setDeletedIds(selectRecord);
		}
		
		var map = new HashMap();
		map.put("itemId", projectManage.itemValue);
		map.put("projectIds", deletedProjectIds);
		map.put("milestoneIds", deleteMilestoneIds);
	    Rpc({functionId:'PM00000003',success:projectManage.exportSucc},map);
	},
	
	exportSucc: function (response){
		var value = response.responseText;
		var map	 = Ext.decode(value);
		if(map.succeed){
			var fieldName = getDecodeStr(map.fileName);
			window.location.target="_blank";
			window.location.href = "/servlet/vfsservlet?fromjavafolder=true&fileid="+fieldName;
		}
	},
	
	updateStore: function (tip, type) {
		if("1" != tip)
			return false;
		
		var dataStore = Ext.data.StoreManager.lookup('projectmanage_0001_dataStore');
		var pageStore = Ext.data.StoreManager.lookup('projectmanage_0001_pageStore');
		
		if("landMarkAdd" == type){
			var id = projectManage.newData[0].pnodeId;
			
			if(dataStore) {
				var data = dataStore.getNodeById(id);
				
				if (Ext.isEmpty(id)) //如果没有父节点，则pnode为根节点  
					data = dataStore.getRootNode();  
				
				var newDatas = projectManage.newData;
				for(var i = 0; i < newDatas.length; i++){
					delete newDatas[i].pnodeId;
					data.appendChild(newDatas[i]);
				}

				data.set("leaf", false);
				
				if(!data.isExpanded())
					data.expand();
			}
			
//			if("add" == type && pageStore) {
//				pageStore.add(projectManage.newData);
//				Ext.getCmp("projectmanage_0001_pagingToolbar").updateInfo();
//			}
			
		} else if("add" == type && pageStore) {
			projectManage.reloadTableData("0");
		} else if("edit" == type || "landMarkEdit" == type){
			var id = (Ext.decode(projectManage.newData)).id;
			var newdata = Ext.decode(projectManage.newData);
			var column = Ext.getCmp('projectmanage_0001_treegrid').getColumns();
			if(dataStore) {
				var lastRecord = dataStore.getAt(dataStore.getCount() - 1);
				var flag = false;
				if(!lastRecord.get('p1101') && "edit" == type)
					flag = true;
				
				var data = dataStore.getNodeById(id);
				for(var key in newdata){
					var newValue = newdata[key];
					if(flag) {
						var countValue = lastRecord.get(key);
						if(countValue) {
							var oldValue = data.get(key);
							var length = 0;
							if(countValue.indexOf(".") > -1) {
								length = countValue.substring(countValue.indexOf(".") + 1).length;
								countValue = (parseFloat(countValue) + parseFloat(newValue) - parseFloat(oldValue));
							} else
								countValue = (parseInt(countValue) + parseInt(newValue) - parseInt(oldValue)) + "";
							
							lastRecord.set(key, countValue.toFixed(length));
						}
					}
					
					data.set(key, newValue);
				}
			}
		}
	}
});