/**
*业务模板导航入口
*zhaoxg 2016-3-7
*/
Ext.define('TemplateNavigation.TemplateNavigation',{
	/**业务模板导航参数格式
	*{  return_flag:'1',
		sys_type:'1',
		tab_ids:'1,2,3',//可选
		href:'',
		module_id:'1',
		issearchdb:true,//其他模块调用人事异动走业务分类-业务处理按钮是否查询待办标识（可选）
	 }
	*/
	templateNav:'',//业务模板导航参数
    constructor:function(config){
    	templatenavigation = this;
		templatenavigation.templateNav = config.templateNav;
		templatenavigation.itemid = "";//页签的id，用来刷新store用的
		templatenavigation.issearchdb = config.templateNav.issearchdb;//走业务模板是否查询待办
		templatenavigation.showleft = config.templateNav.showleft;
  		var map = new HashMap();
  		map.put('return_flag',config.templateNav.return_flag);
  		map.put('sys_type',config.templateNav.sys_type);
  		map.put('tab_ids',config.templateNav.tab_ids);
  		map.put('href',config.templateNav.href);
  		map.put('module_id',config.templateNav.module_id);
	    Rpc({functionId:'MB00006001',success: function(form,action){
			var result = Ext.decode(form.responseText);
			if(result.succeed){
				templatenavigation.themes=Ext.util.JSON.decode(form.responseText).themes;
				templatenavigation.headerColor='';
		  	    templatenavigation.menuColor='';
				if(templatenavigation.themes=='gray'){//灰色
					templatenavigation.headerColor = '#BDBDBD';
					templatenavigation.menuColor = '#f9f9f9'
		  		}else if(templatenavigation.themes =='green'){
		  			templatenavigation.headerColor = '#74C528';
					templatenavigation.menuColor = '#d8ffea';
		  		}else if(templatenavigation.themes =='red'){
		  			templatenavigation.menuColor = '#ed5959';
		  		}else if(templatenavigation.themes =='lightBlue'){
		  			templatenavigation.menuColor = '#83d3ff';
		  		}else if(templatenavigation.themes =='lightGreen'){
		  			templatenavigation.menuColor = '#99ddb9';
		  		}else if(templatenavigation.themes =='cyanineBlue'){
		  			templatenavigation.menuColor = '#deeaff';
				}else{
					templatenavigation.themes = 'default';
					templatenavigation.headerColor = '#5190D1';
					templatenavigation.menuColor = '#deeaff';
				}
				templatenavigation.data=result.data;
				templatenavigation.module_id=result.module_id;
				templatenavigation.return_flag=result.return_flag;
				templatenavigation.sys_type=result.sys_type;
				templatenavigation.moduledata=result.moduledata;
				templatenavigation.bostype=result.bostype;
				templatenavigation.tab_ids=result.tab_ids;
				templatenavigation.myapply=result.myapply;
				templatenavigation.ctrltask=result.ctrltask;
				templatenavigation.businessapply=result.businessapply;
				if (templatenavigation.tab_ids!=""){
					templatenavigation.initNoLeft(); 
				}else{
					templatenavigation.init(); 
				}
				
			}else{//捕捉后台抛出的异常 throw new GeneralException(ResourceFactory.getProperty("xxxxx"));
				Ext.showAlert(result.message);
			}
	    }},map); 
    },
   	init:function(){
   		var text = MB.CARD.PersonnelBusiness;//人事业务
   		if(templatenavigation.module_id=="7"){
   			text =MB.CARD.UNITCHANGE;//组织机构变动
   		}else if(templatenavigation.module_id=="8"){
   			text =MB.CARD.STATIONCHANGE;//岗位变动
   		}else if(templatenavigation.module_id=="3"){
   			text =MB.CARD.CONTRACTMANAGEMENT;//劳动合同
   		}else if(templatenavigation.module_id=="5"){
   			text =MB.CARD.GOABROAD;//出国办理
   		}else if(templatenavigation.module_id=="4"){
   			text =MB.CARD.INSURANCECHANGE;//保险管理
   		}else if(templatenavigation.module_id=="2"){
   			text =MB.CARD.SALARYCHANGE;//薪资
   		}else if(templatenavigation.module_id=="10"){
   			text =MB.CARD.BUSINESSMANAGEMENT;//考勤
   		}
   		else if(templatenavigation.module_id=="6"){
   			text =MB.CARD.ZGPS;//资格评审
   		}
   		else if(templatenavigation.module_id=="12"){
   			text =MB.CARD.ZSGL;//证照管理
   		}
   		var module = undefined;
   		var tree = undefined;
   		if(templatenavigation.bostype=='true'){//走业务分类模板展示
   			var moduleitems = []; 
   		    var topmodule = Ext.widget('panel',{
   		    	width:'100%',
   		    	//minWidth:100,
   		        border:0,
  		   		bodyStyle: 'background-color:'+templatenavigation.menuColor,
   		    	height:37,layout:{type:'vbox',align:'center',pack:'center'},
   		    	items:[{xtype:'label',text:text,style:'font-size:14px;'}]
   		    });
   		    moduleitems.push(topmodule);
   			for(var k=0;k<templatenavigation.moduledata.length;k++){
   				var mdata = templatenavigation.moduledata[k];
				var index = mdata.indexOf('`');
				var name = mdata.substring(0,index);
				var params = mdata.substring(index+1,mdata.length);
   				var images = Ext.widget('container',{
   					padding:'10 0 10 0',
   					//width:100,
   					minHeight:75,
   					layout:{type:'vbox',align:'center',pack:'center'},
   					items:[{xtype:'image',src:rootPath+'/images/apply.gif',
		   					width:32,height:32,
		   					params:params,
		   					style : {cursor : 'pointer'}
		   					,listeners : {
		   						render : function() {
		   						    var hi = this;
									this.getEl().on("click", function() {
										clearInterval(window.lockedtimer);
										templatenavigation.turnMoudle(hi.params);
									})}}},
   						{xtype:'box',maxWidth:148,html:'<a href="javascript:void(0);">'+name+'</a>',params:params,
   							style:'cursor:pointer;text-align:center;',
   							listeners : {
		   						render : function() {
		   							var hi = this;
									this.getEl().on("click", function() {
										clearInterval(window.lockedtimer);
										templatenavigation.turnMoudle(hi.params);
									})}}}]
   				});
   				moduleitems.push(images);
   			}
   			module = Ext.widget('panel',{
				width:182,
				height:500,
				region:'west',
				collapsible:true,
				resizable:false,
				autoScroll:true,
				header:false,
				collapseMode:'mini',
				split:true,
				bodyStyle:"border:0px solid rgb(172,172,172);border-left:none;border-top:none;",//border-color:"+templatenavigation.headerColor,
				height:'100%',
				style:{//解决火狐150%有滚动条
					border:'1px solid rgb(172,172,172);border-left:none;border-top:none;'
				},
	    		layout:{type:'vbox',align:'center'},
	    		items:moduleitems
   			});
   		}else{
   			var treeStore = Ext.create('Ext.data.TreeStore', {
			root: {
				// 根节点的文本
				id:'root',				
				text:text,
				expanded: true,
				icon:rootPath+'/images/add_all.gif',
				children:templatenavigation.data
			}
			});
			tree = Ext.create('Ext.tree.Panel', {
				// 不使用Vista风格的箭头代表节点的展开/折叠状态
				useArrows: false,
				store: treeStore, // 指定该树所使用的TreeStore
				rootVisible: true, // 指定根节点可见
	            width: 170,
	            region: "west",
	            split:true,
	      		collapseMode:'mini',                 
	            border:true,
	            collapsible: true, 
	            style:'backgroundColor:white', 
	            bodyStyle:"border-left:none;",//border-color:"+templatenavigation.headerColor,
	            hidden: templatenavigation.module_id=="9"?true:false,
	            header:false,
	            listeners: {
	       			'itemclick':function(view,record,item,index){
	       				if(record.get("id") == "root")
	       					return;
	       			    if (record.get("isCategory")!=null && record.get("isCategory")=="1"){
	       			         return;
	       			    }
	       			    templatenavigation.myMask = Ext.getCmp("maskId");
			        	if(!!!templatenavigation.myMask){
							templatenavigation.myMask = new Ext.LoadMask({
								id:"maskId",
							    target : Ext.getCmp("template")
							});
							templatenavigation.myMask.show();
			        	}
				       	Ext.Loader.setConfig({
							enabled: true,
							paths: {
								'TemplateMainUL': rootPath+'/module/template/templatemain'
							}
						});
						var templateObj = new Object();
						templateObj.sys_type=templatenavigation.sys_type;
						templateObj.tab_id=record.get("id");
						templateObj.return_flag="0";
						templateObj.module_id="1";
						templateObj.approve_flag="1";
						templateObj.callBack_init="templatenavigation.tempFunc";	
						templateObj.callBack_close="templatenavigation.goBack";					
	     				Ext.require('TemplateMainUL.TemplateMain', function(){
							Ext.create("TemplateMainUL.TemplateMain", {templPropety:templateObj});
						});
	       			}
			    }
			});
   		}
		templatenavigation.tabs = Ext.create('Ext.tab.Panel', {
//			region: "center",
			layout:'auto',
			border:true,
			height:500,
			margin:'10 0 0 0',
			bodyStyle:"border-top:none;",
			padding:'0 1 0 1',//解决边框线粗不一致
			id:'templatetab',
			tabBar:{//解决谷歌150%选项卡下有线
				margin:-1
			},
       	    items: [
       	        {
       	            title: MB.CARD.DBTASK,//待办任务
       	            itemId: 'dbtask',
       	            layout:'fit',
       	            listeners: { activate: templatenavigation.addTabPage }
       	        },
       	        {
       	        	title: MB.CARD.YBTASK,//已办任务
       	        	itemId: 'ybtask',
       	        	layout:'fit',
       	        	listeners: { activate: templatenavigation.addTabPage }
       	        },
       	        {
       	        	title: MB.CARD.MYAPPLY,//我的消息
       	        	hidden: templatenavigation.myapply?false:true,
       	        	itemId: 'myapply',
       	        	layout:'fit',
       	        	listeners: { activate: templatenavigation.addTabPage }
       	        },
       	        {
       	        	title: MB.CARD.CTRLTASK,//任务监控
       	        	hidden: templatenavigation.ctrltask?false:true,//9、业务申请（自助）   不显示任务监控 显示业务申请页签
       	        	itemId: 'ctrltask',
       	        	layout:'fit',
       	        	listeners: { activate: templatenavigation.addTabPage }
       	        },
       	        {
       	        	title: MB.CARD.BUSINESSAPPLY,//业务申请
       	        	hidden: templatenavigation.businessapply?false:true,
       	        	itemId: 'businessapply',
       	        	layout:'fit',
       	        	listeners: { activate: templatenavigation.addTabPage }
       	        }
       	    ],
			listeners:{
       	    	tabchange:function( tabPanel, newCard, oldCard, eOpts ){
       	    		oldCard.removeAll(true);
       	    	}
       	    }
       	});
       	var tab = Ext.create('Ext.panel.Panel', {
			region: "center",
			style:'backgroundColor:white',
			layout: 'fit',
			border:false,
			id:'template',
			items: [
				templatenavigation.tabs
			]
		})
		var bodyPanel = Ext.create('Ext.panel.Panel', {
	            id:"port",
	            border : 0,
	            layout:'border',
	            style:'backgroundColor:white',             
	            autoScroll:false//lis 20160426
	            //items:[tree,tab]      
	        });
	    if(templatenavigation.module_id=="11"){//职称评审
	    	bodyPanel.add([tab]);
	    }else{
	    	if(templatenavigation.showleft=='0'){
	    		bodyPanel.add([tab]);
	    	}else{
	    		if(templatenavigation.bostype=='true'){//走模板展示
					bodyPanel.add([module,tab]);
				}else{
					bodyPanel.add([tree,tab]);
				}
	    	}
	    }
			
  		Ext.create('Ext.container.Viewport',{
			style:'backgroundColor:white',
			layout:'fit',
			autoScroll:false,
			items:[bodyPanel]
		});
		templatenavigation.height = templatenavigation.tabs.getHeight() - templatenavigation.tabs.tabBar.getHeight();  
	},
	addTabPage:function(a){
		templatenavigation.itemid = a.itemId;
		if(a.itemId=="dbtask"){
			Ext.require('TemplateNavigation.DbTask', function(){
				Ext.create("TemplateNavigation.DbTask", {days:'360',clienth:templatenavigation.height,module_id:templatenavigation.module_id,callBackFunc:templatenavigation.callBackFn,itemid:a.itemId});
			});
		}else if(a.itemId=="ybtask"){
			Ext.require('TemplateNavigation.YbTask', function(){
				Ext.create("TemplateNavigation.YbTask", {days:'30',clienth:templatenavigation.height,module_id:templatenavigation.module_id,callBackFunc:templatenavigation.callBackFn,itemid:a.itemId});
			});
		}else if(a.itemId=="ctrltask"){
			Ext.require('TemplateNavigation.CtrlTask', function(){
				Ext.create("TemplateNavigation.CtrlTask", {days:'30',clienth:templatenavigation.height,module_id:templatenavigation.module_id,callBackFunc:templatenavigation.callBackFn,itemid:a.itemId});
			});
		}else if(a.itemId=="myapply"){
			Ext.require('TemplateNavigation.MyApply', function(){
				Ext.create("TemplateNavigation.MyApply", {clienth:templatenavigation.height,module_id:templatenavigation.module_id,callBackFunc:templatenavigation.callBackFn,itemid:a.itemId});
			});
		}else if(a.itemId=="businessapply"){
			Ext.require('TemplateNavigation.BusinessApply', function(){
				Ext.create("TemplateNavigation.BusinessApply", {clienth:templatenavigation.height,module_id:templatenavigation.module_id,callBackFunc:templatenavigation.callBackFn,itemid:a.itemId});
			});
		}
	},
	callBackFn:function(itemid,TaskPanel){
		var templatetab = Ext.getCmp('templatetab');
		templatetab.child('#'+itemid).removeAll(true);
		templatetab.child('#'+itemid).add(TaskPanel);
	},
	tempFunc:function(){
		templatenavigation.myMask.hide();
		Ext.getCmp("template").removeAll(false);
		Ext.getCmp("template").add(templateMain_me.mainPanel);
	},
	turnMoudle:function(params){
		var template = Ext.getCmp('template');
		Ext.require('TemplateNavigation.SearchMoudle', function(){
			Ext.create("TemplateNavigation.SearchMoudle", {moduleparams:params,
			module_id:templatenavigation.module_id,callBackFunc:templatenavigation.callBackModuleFn,
			main_panel:template,sys_type:templatenavigation.sys_type});
		});
	},
	callBackModuleFn:function(panel){
		var template = Ext.getCmp('template');
		template.removeAll(false);
		template.add(panel);
	},
	goBack:function(){//去卡片或者列表页面的“返回”按钮调用
		Ext.getCmp("template").removeAll(false);
		Ext.getCmp("template").add(templatenavigation.tabs);
		
		var store = Ext.data.StoreManager.lookup(templatenavigation.itemid+"1_dataStore");
		store.currentPage=1;
		store.load();
	},
	initNoLeft:function(){
      /* var tab = Ext.create('Ext.panel.Panel', {
			region: "center",
			style:'backgroundColor:white',
			layout: 'fit',
			border:false,
			id:'template'
		});
		Ext.create('Ext.container.Viewport',{
			style:'backgroundColor:white',
			layout:'fit',
			autoScroll:false,
			items:[tab]
		});*/
		var template = Ext.getCmp('templateform');
		Ext.require('TemplateNavigation.SearchMoudle', function(){
			Ext.create("TemplateNavigation.SearchMoudle", {tab_ids:templatenavigation.tab_ids,issearchdb:templatenavigation.issearchdb,
			module_id:templatenavigation.module_id,callBackFunc:templatenavigation.callBackModuleFnNoLeft,
			main_panel:template,sys_type:templatenavigation.sys_type});
		});
	},
	callBackModuleFnNoLeft:function(panel){
		var template = Ext.getCmp('templateform');
		template.removeAll(false);
		template.add(panel);
	}
	
})