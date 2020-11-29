/**
 * 我的问卷
 * 
 **/
Ext.define('QuestionnairePlan.MyQuestionnaire',{
	container:undefined,
	tableObj:undefined,
	cardViewButton:undefined,
	tableViewButton:undefined,
	dbname:'',
	status:undefined,//状态，用于查询方案
	constructor:function(config) {
		this.container = config.container;
		this.init(config.url);
	},
	//初始化函数
	init:function(url) {
	    Rpc({functionId:'QN10000002',async:false,success:this.getTableOK,scope:this},new HashMap());
	},
	// 加载表单
	getTableOK:function(form,action){
		var me = this;
		var result = Ext.decode(form.responseText);
		var conditions=result.tableConfig;
		var obj = Ext.decode(conditions);
		me.status=result.status;
		QN_global.funcpriv = result.funcpriv;
		QN_global.Renderconfig=result.Renderconfig;//操作栏权限集合		
		me.tableObj = new BuildTableObj(obj);
		//插入快速查询
		me.tableObj.insertItem({
			xtype:'box',
			border:0,
			padding:'10 0 0 3',
			html:'<span>查询方案&nbsp;&nbsp;:&nbsp;&nbsp;</span>'+
				'<a class="myselect" name="-1" href="javascript:QN_global.searchQuestionnaireStatus(-1)">全部</a>&nbsp;&nbsp;'+
				'<a class="myselect" name="0" href="javascript:QN_global.searchQuestionnaireStatus(0)">未发布</a>&nbsp;&nbsp;'+
				'<a class="myselect" name="1" href="javascript:QN_global.searchQuestionnaireStatus(1)">进行中</a>&nbsp;&nbsp;'+
				'<a class="myselect" name="2" href="javascript:QN_global.searchQuestionnaireStatus(2)">暂停</a>&nbsp;&nbsp;'+
				'<a class="myselect" name="3" href="javascript:QN_global.searchQuestionnaireStatus(3)">已结束</a>&nbsp;&nbsp;'
		},0);
		//卡片视图按钮
		me.cardViewButton = Ext.create('Ext.Button', {
			icon: rootPath+'/module/system/questionnaire/images/cardview.png',
		    scale: 'small',
		    handler:me.toCardView,
		    scope:me
		});
		//表格视图按钮
		me.tableViewButton = Ext.create('Ext.Button', {
			icon: rootPath+'/module/system/questionnaire/images/listview.png',
		    scale: 'small',
		    handler:me.toTableView,
		    scope:me
		});
		me.container.add(me.tableObj.getMainPanel());
		me.tableObj.toolBar.add(['->',me.cardViewButton,me.tableViewButton]);
		me.toCardView();
		//【57914】点进问卷调查，列表方式下或卡片方式下默认显示全部的问卷，查询方案中的全部应该用下划线标出
		//zhangh 2020-2-12 初始化时默认展示全部，将查询方案中的全部加下划线标识
		Ext.query(".myselect")[0].style.textDecoration = "underline";
		//卡片模式 隐藏删除按钮
		/*if(Ext.getCmp('deletePlan')!=undefined){//防止功能授权由于没有权限报错
        Ext.getCmp('deletePlan').hide();
        }*/
		return;
			var arr=Ext.query("font");
			for(var i=0;i<arr.length;i++){
				if(status==(i-1+"")){
					var str = new String();
					str=Ext.query("font")[i].innerHTML;
					Ext.query("font")[i].innerHTML="<u>"+str+"</u>";
					Ext.query("font")[i].color='green';
				}
			}
		
		return;
		
	},
	// 查询方案查询
	searchStatus:function(state){
		var map = new HashMap();
		map.put("url",url);
		status = state.split("_")[1];
		map.put("status",status);
	    Rpc({functionId:'QN10000002',async:false,success:questionnaire.getTableOK},map);
	},
	
	// 查询控件
	createSearchPanel:function(){
		var me = this;
		var map = new HashMap();
		map.put("url",url);
		me.SearchBox = Ext.create("SYSQ.QueryBox",{
			renderTo : "fastsearch",
			width:250,
			hideQueryScheme:true,
			emptyText:'请输入编号或名称',
			subModuleId:"QN_questionnaire_00000001",
			customParams:map,
			funcId:"QN10000002",
			success:me.loadTable//重新加载数据列表
		});
	},
	loadTable:function(form){
		var store = Ext.data.StoreManager.lookup('questionnaire_dataStore');
		store.currentPage=1;
		store.load();
	},
	
	// 转换 表格显示模式
	toTableView:function(){
		
		var grid = this.tableObj.tablePanel;
		/*if(Ext.getCmp('deletePlan')!=undefined){
		Ext.getCmp('deletePlan').show();
		Ext.getCmp('cleanPlan').show();
        }*/
		 //隐藏卡片模式
		  grid.cardView.hide();
		  //如果有锁列表格，显示锁列和正常表格
		  if(grid.lockedGrid){
			  grid.lockedGrid.show();
			  grid.normalGrid.show();
		  }else{//没有锁列表格，直接显示view ，并且显示 表头
			  grid.view.show();
			  grid.query("headercontainer")[0].show();
		  }
		  this.cardViewButton.setDisabled(false);
		  this.tableViewButton.setDisabled(true);
	},
	// 转换 卡片 显示模式
	toCardView:function(){
		var grid = this.tableObj.tablePanel;
		/*if(Ext.getCmp('deletePlan')!=undefined){
		Ext.getCmp('deletePlan').hide();
		}*/
	//	Ext.getCmp('cleanPlan').hide();
		//如果有锁列，隐藏锁列表与正常表
		  if(grid.lockedGrid){
			  grid.lockedGrid.hide();
			  grid.normalGrid.hide();
		  }else{//没有锁列直接隐藏view，还得隐藏表头
			  grid.query("headercontainer")[0].hide();
			  grid.view.hide();
		  }
		  
		  //如果存在 卡片 view ,直接显示
		  if(grid.cardView)
			  grid.cardView.show();
		  else{//不存在，创建卡片view
			  var cardView = Ext.widget("dataview",{
				 store:grid.getStore(),
				 autoScroll:true,
				 style:'float:center',
				 selectedItemCls:'mySelected',
				// itemSelector:'div.questionnaireCard',
				 itemSelector:'table.cardTable',
				 tpl:[
				      '<tpl for=".">',
                      '<div class="questionnaireCard" onmouseover="Ext.getDom(\'{planid}_deleteImage\').style.display=\'block\';" onmouseout="Ext.getDom(\'{planid}_deleteImage\').style.display=\'none\';">',
                         '<div style="position:relative;top:0px;left:303px;z-index:10;width:20px;height:20px;">',
                         '<tpl if="QN_global.Renderconfig.deletePlan==1">',//增加删除权限校验
                         '<img id="{planid}_deleteImage" onclick="QN_global.doDeleteOrCleanCard(\'delete\',\'{planid}\',\'{status}\')" src="'+rootPath+'/module/system/questionnaire/images/deletebtn.png" width=20 height=20 style="display:none;cursor:pointer">',
                         '</tpl>',
                         '<tpl if="QN_global.Renderconfig.deletePlan==0">',
                         '<input id="{planid}_deleteImage" width=20 height=20 style="display:none;cursor:pointer;" type="hidden">',
                         '</tpl>',
                         '</div>',
                         '<table  border=0 cellspacing=0 cellpadding=0 class="cardTable">',
                         '<tr><td valign="top" class="planTd"  title="{planname}">',
                         '<tpl if="status==0">',
                         '<tpl if="QN_global.Renderconfig.designTemplate==1">',//有设计权限
                         '<a style="font-size:18px !important;" href="javascript:QN_global.designTemplate(\'{planid}\',\'{qnid}\');">{planname}</a>',
                         '</tpl>',
                          '<tpl if="QN_global.Renderconfig.designTemplate==0">',//无设计权限
                         '<label style="font-size:18px !important;">{planname}</label>',
                         '</tpl>',
                         '</tpl>',
                         
                         '<tpl if="status==1">',
                          '<tpl if="QN_global.Renderconfig.recoverycount==1">',//收集配置
                         '<a style="font-size:18px !important;" href="javascript:QN_global.setRecoveryConfig(\'{planid}\',\'{qnid}\',\'{recoverycount}\')">{planname}</a>',
                         '</tpl>',
                         '<tpl if="QN_global.Renderconfig.recoverycount==0">',
                         '<label style="font-size:18px !important;">{planname}</label>',
                         '</tpl>',
                         '</tpl>',
                         
                         '<tpl if="status==2">',
                         '<tpl if="QN_global.Renderconfig.designTemplate==1">',//设计权限
                         '<a style="font-size:18px !important;" href="javascript:QN_global.designTemplate(\'{planid}\',\'{qnid}\');">{planname}</a>',
                         '</tpl>',
                          '<tpl if="QN_global.Renderconfig.designTemplate==0">',
                         '<label style="font-size:18px !important;">{planname}</label>',
                         '</tpl>',
                         '</tpl>',
                         
                         '<tpl if="status==3">',
                          '<tpl if="QN_global.Renderconfig.analysisPlanData==1">',//分析权限
                         '<a style="font-size:18px !important;" href="javascript:QN_global.analysisPlanData(\'{planid}\',\'{qnid}\')">{planname}</a>',
                         '</tpl>',
                          '<tpl if="QN_global.Renderconfig.analysisPlanData==0">',
                         '<label style="font-size:18px !important;">{planname}</label>',
                         '</tpl>',
                         '</tpl>',
                         '</td></tr>',
                         
                         '<tr><td height="30" style="color:#c5c5c5;padding:5px 10px 5px 10px;">{createuser}创建于{qn_createtime}</td></tr>',
                         '<tr><td height="30" valign="middle" style="border-top:#c5c5c5 solid 1px;">',
                         '<table width=100% height=100%  style="font-size:12px"><tr>',
                        
                         '<tpl if="QN_global.Renderconfig.starts==1">',//开始权限
                         '<tpl if="status==0">',
                         '<td width=20 height="30"><img src="'+rootPath+'/module/system/questionnaire/images/start.png" style="cursor:pointer" onclick="QN_global.changePlanStatus(\'start\',\'{planid}\',\'{qnid}\');"></td><td style="color:#c5c5c5">未发布</td>',
                         '</tpl>',
                         '</tpl>',
                         '<tpl if="QN_global.Renderconfig.starts==0">',//开始权限
                         '<tpl if="status==0">',
                         '<td width=20 height="30"></td><td style="color:#c5c5c5">未发布</td>',
                         '</tpl>',
                         '</tpl>',
                         
                         '<tpl if="QN_global.Renderconfig.stops==1">',//增加停止权限校验
                         '<tpl if="status==1">',
                         '<td width=20 height="30"><img src="'+rootPath+'/module/system/questionnaire/images/stop.png" style="cursor:pointer" onclick="QN_global.changePlanStatus(\'stop\',\'{planid}\',\'{qnid}\');"></td><td style="color:#c5c5c5">运行中</td><td>发布于{pubtime}</td>',
                         '</tpl>',
                         '</tpl>',
                         '<tpl if="QN_global.Renderconfig.stops==0">',
                         '<tpl if="status==1">',
                         '<td width=20 height="30"></td><td style="color:#c5c5c5">运行中</td><td>发布于{pubtime}</td>',
                         '</tpl>',
                         '</tpl>',
                         '<tpl if="status==2">',//暂停状态下增加开始权限控制
                         '<tpl if="QN_global.Renderconfig.starts==1">',
                         '<td width=20 height="30"><img src="'+rootPath+'/module/system/questionnaire/images/restart.png" style="cursor:pointer" onclick="QN_global.changePlanStatus(\'start\',\'{planid}\',\'{qnid}\');"></td><td style="color:#c5c5c5">暂停</td>',
                         '</tpl>',
                         '<tpl if="QN_global.Renderconfig.starts==0">',
                         '<td width=20 height="30"></td><td style="color:#c5c5c5">暂停</td>',
                         '</tpl>',
                         '</tpl>',
                         '<tpl if="status==3">',//结束时取消开始按钮
                         '<td width=20 height="30" ></td><td style="color:#c5c5c5">结束</td>',
                         '</tpl>',
                         '<td align="right" style="color:#c5c5c5">',
                         '<span style="float:right;">已投人数:{recoverycount}</span>',
                         '</td></tr></table>',
                         '</td></tr>',
                         '</table>',
                      '</div>',
                      '</tpl>'
				      ]
			  });
			  grid.cardView = cardView;//卡片 view 保存在grid中，方便以后使用
			  grid.add(cardView);//显示卡片 模式
		  }
		  
		  this.cardViewButton.setDisabled(true);
		  this.tableViewButton.setDisabled(false);
	}

});