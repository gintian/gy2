/**
 * 声明本界面需要调用的类
 * */
Ext.Loader.setConfig({
	enabled: true,
	paths: {
		'TemplateToolBarUL': rootPath+'/module/template/templatetoolbar',
		'TemplateCardUL': rootPath+'/module/template/templatecard',
		'TemplateListUL': rootPath+'/module/template/templatelist',
		'TemplateSubsetUL': rootPath+'/module/template/templatesubset',
		'SYSF':rootPath+'/components/fileupload'
	}
});

/**
 * 模板主界面类 TemplateMain.js
 * */
Ext.define('TemplateMainUL.TemplateMain',{
	viewType:"card", //默认显示卡片   
	mainPanel:'',//主界面面板
	mainViewPort:'',//渲染主界面
	templPropety:'',//模板的所有属性 模板号，实例号等 参考html中的templateGlobalBean
	prefix:"templmain",//前缀标识：模板主界面面板元素前缀
	tableConfig:"",//临时存储后台传过来的工具栏信息
	callBack_init:null,//初始化界面后的回调函数
	callBack_close:null,//返回调用的回调函数
	isAutoLog:false,//是否记录变动日志
	autoLogColor:'#FF0000',//变动字段颜色设置
	canToSave:true,//非兼容模式下大文本超出限制长度是否可以继续保存
	//attachFlag:'',//当前模板设置了“个人附件归档至主集附件”，但是主集未设置支持附件 后台直接抛错，不传输到前台了 屏蔽此属性
	constructor:function(config) {
   		this.init(config);
	},
	init:function(config) {
		templateMain_me=this;
		var browser = templateMain_me.getBrowser();
	    var isMobileBrowser = false;
	    if (browser.versions.android||browser.versions.ucweb||browser.versions.uc7||
	    	    browser.versions.mdip||browser.versions.ios||browser.versions.winc||
	    	    browser.versions.iPhone||browser.versions.iPad||browser.versions.winm||
	    	    browser.versions.weixin) { 
	    	isMobileBrowser = true;
	    	var maskId = Ext.getCmp("maskId"); //模态遮罩覆盖控件去掉
        	if(maskId)
        		maskId.destroy();
	    	Ext.showAlert("很抱歉,此单据不支持在移动端审批");
	    	return;
        }
		var templPropetyTemp = config.templPropety;
		var globalBean={};//临时存储模板参数
		//切换模板时，先恢复默认模板参数
		Ext.apply(globalBean,templateGlobalBeanDefault);
		/*以下参数不需要 有其他方式获取，wangrd 
		if(!!!templPropetyTemp.ins_id)
			templPropetyTemp.ins_id = "0";
		//已办任务、我的申请、任务监控进入，sp_flag=2
		if(!!!templPropetyTemp.sp_flag)
			templPropetyTemp.sp_flag = "1";
		//批量审批
		if(!!!templPropetyTemp.sp_batch)
			templPropetyTemp.sp_batch = "0";
			*/
		
		Ext.apply(globalBean,templPropetyTemp);
		templPropetyTemp = globalBean;
		/*需要传送的参数： 模板号，task_id */
		var map = new HashMap();
		initPublicParam(map,templPropetyTemp);
		map.put("prefix",this.prefix);
	    if(!Ext.getCmp("maskId"))
			Ext.MessageBox.wait("正在加载数据，请稍候...", "等待");
	    this.templPropety = templPropetyTemp;
	    var isarchive = getTemplPropetyOthParam("isarchive");
	    var functionid = "MB00001001";
	    if(isarchive=='0'){
	    	functionid = "MB00008002";
	    }
	    Rpc({functionId:functionid,async:true,success:function(form,action){
	    	var result = Ext.decode(form.responseText);	
	    	templateMain_me.change_view=result.change_view;//是否显示切换按钮
	    	clearInterval(window.lockedtimer);
			if(!result.succeed){
		 		var maskId = Ext.getCmp("maskId"); //模态遮罩覆盖控件去掉
	        	if(maskId)
	        		maskId.destroy();
	        	else
	        		Ext.MessageBox.close();
	        	var callback=''
	        	Ext.apply(templateGlobalBean,templateGlobalBeanDefault);
	    		Ext.apply(templateGlobalBean,config.templPropety);
	        	if (config.templPropety.callBack_close){
	        		callback=config.templPropety.callBack_close;
	      		}
	            Ext.showAlert(result.message,function(){templateMain_me.returnBack(callback,templPropetyTemp.return_flag,templPropetyTemp.bos_flag);});
	            return;
	        }else{
	        	if(!Ext.getCmp("maskId"))
	    			Ext.MessageBox.close();
	        	//me.attachFlag = result.attachFlag; //当前模板设置了“个人附件归档至主集附件”，但是主集未设置支持附件
	        	if(result.out_pages){
	        		templateMain_me.out_pages=result.out_pages;
	        	}else{
	        		templateMain_me.out_pages="";	
	        	}
	        	
	    		this.validateOk(config,result);
	    		document.title=templateMain_me.tableConfig.title;//title改为模板名称
	    		if(templateMain_me.templPropety.task_id!='0'&&templateMain_me.templPropety.isInterval==true
	    	    		&&templateMain_me.templPropety.approve_flag=='1'){
	    	   		//每5分钟掉一次后台
	    	   		window.lockedtimer=setInterval(function(){
	    	   			templateMain_me.setEmployTime();
	    	   		},1000*60*5);
	    		}
	        }
	    },scope:this},map);
	},
	
	//验证 lis 20160615
	validateOk:function(config,result){
		//lis 20160409 start
		/*if(!!!Ext.util.CSS.getRule(".x-grid-row-selected-white .x-grid-td"))
			Ext.util.CSS.createStyleSheet(".x-grid-row-selected-white .x-grid-td{background-color: #FFFFFF !important}","white");
		if(!!!Ext.util.CSS.getRule(".x-grid-row-selected-grey .x-grid-td"))
			Ext.util.CSS.createStyleSheet(".x-grid-row-selected-grey .x-grid-td{background-color: #FAFAFA !important}","grey");
		if(!!!Ext.util.CSS.getRule(".x-grid-row-selected-gray .x-grid-td"))//子集记录只读颜色
			Ext.util.CSS.createStyleSheet(".x-grid-row-selected-gray .x-grid-td{background-color: #F3F3F3 !important}","gray");
		if(!!!Ext.util.CSS.getRule(".x-grid-row-selected-mouseenter .x-grid-td"))
			Ext.util.CSS.createStyleSheet(".x-grid-row-selected-mouseenter .x-grid-td{background-color: #FFF8D2}","mouseonId");
		if(!!!Ext.util.CSS.getRule(".x-grid-row-selected-itemclick .x-grid-td"))
			Ext.util.CSS.createStyleSheet(".x-grid-row-selected-itemclick .x-grid-td{background-color: #FFF8D2}","clickId");
		if(!!!Ext.util.CSS.getRule(".x-grid-item-selected"))
			Ext.util.CSS.createStyleSheet(".x-grid-item-selected{color:#000;background-color: #fff}");*/
		//lis 20160409 end
			
		//销毁原来的
		if(Ext.getCmp(this.prefix+"_main_panel")){
      		Ext.getCmp(this.prefix+"_main_panel").destroy();
   		 }
		
		//切换模板时，先恢复默认模板参数
		Ext.apply(templateGlobalBean,templateGlobalBeanDefault);
		/*以下参数不需要 有其他方式获取， wangrd 	
		if(!!!config.templPropety.ins_id)
			config.templPropety.ins_id = "0";
		//已办任务、我的申请、任务监控进入，sp_flag=2
		if(!!!config.templPropety.sp_flag)
			config.templPropety.sp_flag = "1";
		//批量审批
		if(!!!config.templPropety.sp_batch)
			config.templPropety.sp_batch = "0";
		*/
		Ext.apply(templateGlobalBean,config.templPropety);
		this.templPropety = templateGlobalBean;
		if (config.templPropety.callBack_init){
		  this.callBack_init= config.templPropety.callBack_init;
		}
		if (config.templPropety.callBack_close){
		  this.callBack_close= config.templPropety.callBack_close;
		}
		
		templateMain_me.initOK(result);
	},
	initOK:function(result){		
		/*返回信息，功能按钮， 模板属性等*/
		
		//模板回传的属性
		if (result.hasOwnProperty("tab_id")){
			this.templPropety.tab_id=result.tab_id;
		}
		if (result.hasOwnProperty("view_type")){
			this.templPropety.view_type=result.view_type;
		}
		if (result.hasOwnProperty("infor_type")){
			this.templPropety.infor_type=result.infor_type;
		}
		if (result.hasOwnProperty("bos_flag")){
			this.templPropety.bos_flag=result.bos_flag;
		}
		if (result.hasOwnProperty("table_name")){
			this.templPropety.table_name=result.table_name;
		}
		if (result.hasOwnProperty("visible_toolbar")){
			this.templPropety.visible_toolbar=result.visible_toolbar;
		}
		if (result.hasOwnProperty("visible_title")){
			this.templPropety.visible_title=result.visible_title;
		}
		if (result.hasOwnProperty("onlyname")){
			this.templPropety.onlyname=result.onlyname;
		}
		if (result.hasOwnProperty("orgId")){
			this.templPropety.orgId=result.orgId; //选人（选机构）控件默认显示某机构下数据  如果是职称评审，则判断是否有业务范围，如果有则走业务范围
		}
		if (result.hasOwnProperty("multimedia_maxsize")){
			this.templPropety.multimedia_maxsize=result.multimedia_maxsize;//多媒体文件上传限制大小
		}
		if (result.hasOwnProperty("rootDir")){
			this.templPropety.rootDir=result.rootDir;	//文件存放根目录
		}		
		if (result.hasOwnProperty("downmap")){
			this.templPropety.downmap=result.downmap;	//是否有下载、上传附件权限
		}
		if (result.hasOwnProperty("tasktype")){
			this.templPropety.tasktype=result.tasktype;	//是否是（起草或者驳回到起草）或者是审批状态
		}
		if (result.hasOwnProperty("ins_id")){
			templateMain_me.ins_id=result.ins_id;	//流程实例号（只打印高级花名册用）
		}
		// 按检索条件和人员范围 begin
		if (result.hasOwnProperty("isPrivExpression")){
			this.templPropety.isPrivExpression=result.isPrivExpression;	//手工选人、条件选人是否按管理范围控制
		}
		if (result.hasOwnProperty("filter_factor")){
			this.templPropety.filter_factor=result.filter_factor;	//检索条件条件
		}
		if (result.hasOwnProperty("filter_by_factor")){
			this.templPropety.filter_by_factor=result.filter_by_factor;	//检索条件条件
		}
		if (result.hasOwnProperty("factor_update_type")){
			this.templPropety.factor_update_type=result.factor_update_type;	//检索条件更新方式
		}
		// 按检索条件和人员范围 end
		if(this.templPropety.module_id=='9'&&templateMain_me.ins_id=='0'){//自助申请
			this.templPropety.card_view_type = "1";
		}
		var operation_type = "";
		if (result.hasOwnProperty("operation_type")){
			operation_type=result.operation_type;	//业务类型
		}
		if(this.templPropety.infor_type=='2'||this.templPropety.infor_type=='3'){//机构相关(合并划转)默认走卡片
			if(operation_type=='8'||operation_type=='9')
				this.templPropety.view_type = 'card';
		}
		if(this.templPropety.fillInfo=="1"&&this.templPropety.module_id=='9')//无会话提交方式默认走卡片
			this.templPropety.view_type = 'card';
		
		if(getTemplPropetyOthParam("iscombine")=='true'||this.templPropety.task_id!='0')
			this.templPropety.task_id = result.task_id;
		
		if (result.hasOwnProperty("isInterval")){
			this.templPropety.isInterval=result.isInterval;
		}
		
		if (result.hasOwnProperty("autoCompute")){
			this.templPropety.autoCompute=result.autoCompute;
		}
		if (result.hasOwnProperty("firstPageNo")){
			this.templPropety.firstPageNo=result.firstPageNo;
		}
		if (result.hasOwnProperty("isValidOnlyname")){//是否设置并启用了唯一性指标
			this.templPropety.isValidOnlyname=result.isValidOnlyname;
		}
		if (result.hasOwnProperty("nbases")){//进入时人员库设置
			this.templPropety.nbases=result.nbases;
		}
		if (result.hasOwnProperty("isAutoLog")){
			this.templPropety.isAutoLog=result.isAutoLog;	//是否记录变动日志
		}
		if (result.hasOwnProperty("autoLogColor")){
			this.templPropety.autoLogColor=result.autoLogColor;	//变动字段颜色
		}
		if (result.hasOwnProperty("sqlwhere_factor")){
			this.templPropety.sqlwhere_factor=result.sqlwhere_factor;	//单位岗位条件检索定义sqlwhere语句
		}
		//加载界面:标题栏 功能按钮 
		var tableConfig=result.tableConfig;
		tableConfig = Ext.decode(tableConfig);
		this.tableConfig= tableConfig;
		var bOtherInit=false;
		//是否需要按检索条件增加人员
		if (result.needImportMen=='true'){
			this.confimImportMen(result.hasRecord);
		}
		else {
		    //初始界面
            this.initForm(this.tableConfig);
		}
	},
    /**
     * 判断是否按自动按条件检索增加人员
     */
    confimImportMen:function(hasRecord) {
    	if(templateMain_me.templPropety.factor_update_type=='-1')
    		templateMain_me.templPropety.factor_update_type='';
		if (hasRecord=='true'){
			if(templateMain_me.templPropety.factor_update_type
					&&templateMain_me.templPropety.factor_update_type!=''){
				var flag = "1";
				if(templateMain_me.templPropety.factor_update_type=='1'){
					flag = "1";
				}else if(templateMain_me.templPropety.factor_update_type=='2'){
					flag = "2";
				}else if(templateMain_me.templPropety.factor_update_type=='3'){
					flag = "0";
				}
				if (flag!=0){
					templateMain_me.importMenByCondition(flag);
				}else{
					templateMain_me.initForm();
				}
	    	}else{
                Ext.Msg.show({
                    title:MB.MSG.IMPORTMEN_CONDITION0,
                    msg: MB.MSG.IMPORTMEN_CONDITION2,
                    buttons: Ext.Msg.YESNOCANCEL,
                    fn: function(btn){
                              var flag="1";
                              if(btn=="yes"){//是
                                  flag="1";
                              }
                              else if(btn=="no"){//否
                                  flag="2";
                              }
                              else {//取消
                                  flag="0";
                              }
                              if (flag!=0){
                                  bOtherInit=true;
                                  templateMain_me.importMenByCondition(flag);
                              }
                              else {
                                  templateMain_me.initForm();
                              }
                          }, 
                    icon: Ext.MessageBox.QUESTION
                }); 
	    	}
  		}
      else {
    	  if(templateMain_me.templPropety.factor_update_type&&templateMain_me.templPropety.factor_update_type!=''){
    		  templateMain_me.importMenByCondition("1");
    	  }else{
    		  Ext.showConfirm(MB.MSG.IMPORTMEN_CONDITION1+"",
    	              function(btn){
    	                  if(btn=="yes"){
    	                     bOtherInit=true;
    	                     templateMain_me.importMenByCondition("1");
    	                  }
    	                  else {
    	                      templateMain_me.initForm();
    	                  }
    	              } 
    	       ); 
    	  }
      }
    },
    /**
     * 自动按条件检索增加人员
     */
    importMenByCondition:function(flag) {    
        var map = new HashMap();
        initPublicParam(map,templateMain_me.templPropety);
        map.put('flag', flag);
        Rpc({functionId:'MB00001003',async:false,success:templateMain_me.importMenByConditionOK},map);
    },
    
    /**
     * 自动按条件检索增加人员后后的回调方法
     */
    importMenByConditionOK:function(form,action){  
       //初始界面
       templateMain_me.initForm();
    },
    /**
     * 初始化界面
     */
	initForm:function(){
	   var config=templateMain_me.tableConfig;
		//生成工具栏功能按钮 本来是要在TemplateToolBar中定义，但代码没有实现。只在templateToolBar定义了按钮方法
		var menuPanel= this.createMenuPanel(config);
		
		//功能按钮容器
		templateMain_me.menuContainer =  Ext.widget("panel", {
			id:"menuContainerId",
			border : 0,
			style:'border:1px;',
			tools:[this.createCoumnSetImage()],
			title:config.title,
			header:this.templPropety.visible_title=='0'?false:true,
			items:menuPanel
	   	});	
		
		//列表或卡片的容器
		templateMain_me.bodyContainer =  Ext.widget("container", {
			id:this.prefix+"_body_panel",
			border : 0,
			layout:"fit", 
			padding:'5 0 0 0',
			margin:"0 0 0 2",
			flex:1			
	   	});	
		
		//主界面
		templateMain_me.mainPanel = Ext.create("Ext.panel.Panel",{
			id:this.prefix+"_main_panel",
			border:this.templPropety.visible_toolbar=='0'?0:1,
			height:"100%",
			width:"100%",
			layout:{
				type:'vbox',
				align:'stretch'
			},
			items:[templateMain_me.menuContainer,{
				xtype:"panel",
				itemId:'lineId',
				hidden:this.templPropety.visible_toolbar=='0'?true:false,
				height:1
			},templateMain_me.bodyContainer],
			renderTo: Ext.getBody()
		});
	
		 //加载js，生成工具栏对象供页面元素调用
		Ext.require('TemplateToolBarUL.TemplateToolBar', function(){		
			 TemplateTool = Ext.create("TemplateToolBarUL.TemplateToolBar", 
			 {
			   templPropety:this.templPropety,
			   bodyPanel:templateMain_me.bodyContainer,
			   tm_prefix:this.prefix
			 });
			 if (templateMain_me.templPropety.task_id.indexOf(",")>0&&this.templPropety.view_type=="list"){// 30458 列表下批量审批隐藏审批过程
			     var viewProcessButton=Ext.getCmp('viewProcessButton');
		         if (viewProcessButton) 
		             viewProcessButton.hide();
			 } 
			 if(!Ext.isIE&&Ext.getCmp("printButton"))//非ie浏览器隐藏打印功能
				 Ext.getCmp("printButton").hide();   
		},this);
		
		if(Ext.getCmp("cardButton")&&!templateMain_me.change_view)//控制是否显示切换按钮
			Ext.getCmp("cardButton").hide();  
 		//加载卡片页面或列表页面
		if (this.templPropety.view_type=="card"){
			Ext.require('TemplateCardUL.TemplateCard', function(){		
				Ext.create("TemplateCardUL.TemplateCard", {templPropety:this.templPropety});
				var cardPanel=templateCard_me.getMainPanel();
				templateMain_me.bodyContainer.add(cardPanel);
				//linbz 26870 打印需要的div
				templateMain_me.bodyContainer.add({id:'printPreviewdiv',border:0});
				
				//生成查询框(自助申请不需要查询框)
				///农大 一个人时不显示复杂查询框
				if(templateCard_me==null||!(templateCard_me!=null&&templateCard_me.hidePersonGrid&&templateCard_me.templPropety.task_id!="0"))
					if(this.templPropety.card_view_type != "1")
						templateMain_me.createSearchBox();
				//如果左侧人员列表只有一个人，则隐藏列表按钮（审批过程中）
				if(templateCard_me.hidePersonGrid&&templateCard_me.templPropety.task_id!="0")
					if(Ext.getCmp("cardButton"))
						Ext.getCmp("cardButton").hide();        
				//生成快速选人框	
				/*		
				templateMain_me.createInsertPanel();
				*/
			    templateMain_me.updateViewState("card");
			},this);
		}
		else {
			Ext.require('TemplateListUL.TemplateList', function(){			
			    TemplateList = Ext.create("TemplateListUL.TemplateList", {templPropety:this.templPropety});
				var listPanel=TemplateList.getMainPanel();
				templateMain_me.bodyContainer.add(listPanel);
				templateMain_me.bodyContainer.add({id:'printPreviewdiv',border:0});
				
               //生成查询框
                templateMain_me.createSearchBox();
                //生成快速选人框
                /*
                templateMain_me.createInsertPanel();
                */
				templateMain_me.updateViewState("list");
				
			},this);
		}
		if(templateMain_me.callBack_init){
            Ext.callback(eval(templateMain_me.callBack_init),null,[]);
		}
		/*
		var me = this;
		if(me.attachFlag)
           	 	Ext.showAlert("当前模板设置了“个人附件归档至主集附件”，但是主集未设置支持附件，请联系管理员！");
           	 	*/
	},
    /**
     * 列表、卡片视图下需隐藏的按钮等。
     * view_type :当前的视图状态
     */
    updateViewState:function(view_type){
    	var midText = "人员";
        if(this.templPropety.infor_type=='2')
        	midText = "机构";
        if(this.templPropety.infor_type=='3')
        	midText = "岗位";
        if (view_type=="list"){         
                //生成查询框
                templateMain_me.comSet.show();
                //liuyz 2016-12-28 列表状态没有当前页概念，隐藏打印当前页pdf菜单
                if (Ext.getCmp('curOutPdf'))
	            	Ext.getCmp('curOutPdf').hide();
	            if (Ext.getCmp('curOutword'))
              		Ext.getCmp('curOutword').hide();
	            Ext.each(Ext.ComponentQuery.query("*[text^='当前"+midText+"生成PDF']"),function(child){
	  				child.hide();
	            });
                if (templateMain_me.templPropety.task_id.indexOf(",")>0){//批量审批隐藏审批过程
                    var viewProcessButton=Ext.getCmp('viewProcessButton');
                    if (viewProcessButton) 
                        viewProcessButton.hide();
                }    
                //是否隐藏导航按钮
        		templateMain_me.showOrHideNavigation(Ext.getCmp('navigationId'));
        }
        else {
                //卡片模式隐藏下载模板和导入数据两个按钮  改成都显示这两个按钮
                //if (Ext.getCmp('m_downLoad'))
                //    Ext.getCmp('m_downLoad').hide();
                //if (Ext.getCmp('m_upLoad'))    
                //    Ext.getCmp('m_upLoad').hide();
                //liuyz 2016-12-28 显示打印当前页pdf菜单
                if (Ext.getCmp('curOutPdf'))
                	Ext.getCmp('curOutPdf').show(); 
                if (Ext.getCmp('curOutword'))
              		Ext.getCmp('curOutword').show();
                Ext.each(Ext.ComponentQuery.query("*[text^='当前"+midText+"生成PDF']"),function(child){
      				child.show();
                });
                templateMain_me.comSet.hide();
                //是否隐藏导航按钮
                templateMain_me.showOrHideNavigation(Ext.getCmp('navigationId'));
        }
    
    },
	/**
	 * 渲染功能按钮
	 * @return Panel
	 */
	createMenuPanel:function(config){
		if(config.custommenus)
			return null;
		var menuPanel = Ext.create('Ext.panel.Panel',{
			    id:this.prefix+"_menuPanel",
			    bodyBorder:false,//lis 20160407
			    hidden:this.templPropety.visible_toolbar=='0'?true:false,
				border:0
			});
			
		if(config.custommenus){//menu栏
	 	   var menubar  = Ext.create('Ext.toolbar.Toolbar',{border:0,id:this.prefix+"_menubar",dock:'top',height:38});
	 	   menubar.add(config.custommenus);
	 	   menuPanel.addDocked(menubar);
	 	}
	 	
	 	var customtools = config.customtools;
		if(customtools){//tool栏
			
			for(var i=0; i < customtools.length;i++){
				//导航按钮后面生成分隔符
				if("split_navigation" == customtools[i]&& (customtools[i+1]=="-" ||customtools[i-1]=="-")){
					var tbseparator = new Object();
					tbseparator.xtype = "tbseparator";//"分割符"
					tbseparator.text = "button";
					tbseparator.id = "split_navigation";
					tbseparator.cusBtn ="cusBtn";
					customtools[i] = tbseparator;
					if(customtools[i+1]=="-")
						customtools.splice(i+1,1);
					if(customtools[i-1]=="-")
						customtools.splice(i-1,1);
					
				}else if ("split_navigation" == customtools[i]){
					var tbseparator = new Object();
					tbseparator.xtype = "tbseparator";//"分割符"
					tbseparator.text = "button";
					tbseparator.id = "split_navigation";
					tbseparator.cusBtn ="cusBtn";
					customtools[i] = tbseparator;
				}
			}
			
			   var position = "top";
			   if(config.toolPosition)
				   position = config.toolPosition;
			   var toolbar  = Ext.create('Ext.toolbar.Toolbar',{border:0,id:this.prefix+"_toolbar",dock:position,height:38});
		 	   toolbar.add(customtools);		 	  
		 	   menuPanel.addDocked(toolbar);
		}
		//为工具条按钮添加监听
		var buttons = Ext.ComponentQuery.query('#'+this.prefix+'_toolbar>button[cusBtn=cusBtn]');
		for(var i=0;i<buttons.length;i++){
			buttons[i].on('click',templateMain_me.customMenuClick,this);
		}
		var menutools = Ext.ComponentQuery.query('#'+this.prefix+'_toolbar menuitem[cusMenu=cusMenu]');
		for(var i=0;i<menutools.length;i++){
			menutools[i].on('click',templateMain_me.customMenuClick,this);
		}
		//为菜单按钮添加监听
		var menuitems = Ext.ComponentQuery.query('#'+this.prefix+'_menubar menuitem[cusMenu=cusMenu]');
		for(var i=0;i<menuitems.length;i++){
			menuitems[i].on('click',templateMain_me.customMenuClick,this);
		}
		return menuPanel;
	},
	
	 /**
     * 显示或隐藏导航按钮 lis 20160607
     * @param {} isHide
     */
    showOrHideNavigation: function(button){
        		var hiddenNavigation = true;
	        	if(button){
	        		var items =button.getMenu().items.items;
	        		for(var i = 0; i<items.length; i++){
			        		var id = items[i].id;
			        		if(id){
			        			var menu = Ext.getCmp(id);
			        			if(menu)
			        				hiddenNavigation = menu.isHidden();
			        			if(!hiddenNavigation){
			        				break;
			        			}
			        		}
			        	}
        		}

        		if(hiddenNavigation){
        			if(Ext.getCmp('navigationId'))
        				Ext.getCmp('navigationId').hide();
        			if(Ext.getCmp('split_navigation'))
        				Ext.getCmp('split_navigation').hide();
        		}
    			else{
    				if(Ext.getCmp('navigationId'))
    					Ext.getCmp('navigationId').show();
    				if(Ext.getCmp('split_navigation'))
    					Ext.getCmp('split_navigation').show();
    			}
        		return hiddenNavigation;
	},
		
	/**
	 * 渲染栏目设置按钮
	 * @return Panel
	 */
	createCoumnSetImage:function(){
		//解决栏目设置按钮自动适应问题 暂时将栏目设置设置为全局变量
		templateMain_me.comSet = Ext.widget('image',{
			id:this.prefix+"_column_set_img",
			xtype:'image',
			title:'栏目设置',
			height:17,
			width:17,
			hidden:true,
			border:0,
			src:rootPath+'/components/tableFactory/tableGrid-theme/images/Settings.png',
			listeners:{
				click:{
			        element: 'el',
			        fn:function(){
								var config = null;
								if(TemplateList)
									config = TemplateList.getObj();
					            Ext.require("EHR.tableFactory.plugins.SchemeSetting",function(){
					                 var callback = config.schemeSaveCallback?eval(config.schemeSaveCallback):function(){
					                     window.location.reload();
					                 };
					                 new EHR.tableFactory.plugins.SchemeSetting({
								    	 	 subModuleId:config.subModuleId,
								    	     schemeItemKey:config.schemeItemKey?me.config.schemeItemKey:'',
								    	     itemKeyFunctionId:config.itemKeyFunctionId?config.itemKeyFunctionId:'',
								    	     moduleId:config.moduleId,
								    	     closeAction:callback,
								    	     viewConfig:{
								    	    	 	publicPlan:config.showPublicPlan
								    	     }
								     });
									});
								}
			    }
			}
		});
		return templateMain_me.comSet;
	},

	/*//工具栏按钮 事件 监控方法
	 * 取自tablefactory.js 因为此处工具栏按钮为此页面自己生成的，所以加事件监听也需在此处做
	 * */
	customMenuClick:function(menuItem){
		if(menuItem.fntype){
			return;
		}
		
		var xtype = menuItem.xtype;
		var fn = menuItem.fn;
		if(fn.length<1)
			return;
		var menuCall = eval(fn);
		//var msg = undefined;
		//var param = undefined;
		var param = {tablekey:this.config.subModuleId,subModuleId:this.config.subModuleId,targetid:menuItem.id};
		if(xtype == 'menuitem'){
			if(menuItem.getdata && this.config.selectable){
				var selectModel = this.tablePanel.getSelectionModel(),
		    	//从选择组件中获取选中 的数据
		    	selectedItems = selectModel.getSelection(),
		    	store = this.tablePanel.getStore();
				menuCall(param,selectedItems,store,selectModel.doSelectAll);
			}else{
				menuCall(param);
			}
		}else if(xtype == 'menucheckitem'){
			if(!menuItem.group){
				//msg = "点击了checkbox菜单按钮 ，选中状态："+menuItem.checked;
				menuCall(param,menuItem.checked);
			}else{
				//msg = "单选菜单按钮，值："+menuItem.value;
				menuCall(param,menuItem.value);
			}
		}
	},
    /**
     *删除快速查询框
     *
     */
    removeInsertPanel:function(){
       //删除快速插入
       if (Ext.getCmp('m_handSelect')){
	       var toolbar = Ext.getCmp('templmain_toolbar');
	       toolbar.remove(templateMain_me.quickInsert); 
           toolbar.remove(Ext.getCmp('right_space'));
           toolbar.remove(Ext.getCmp('fill_right')); 
       }
    },
	
	//快速插入
	createInsertPanel:function(){
		//无选人权限 则无此功能
		if (!(Ext.getCmp('m_handSelect'))){
		  return;
		}
		
		var insertDataList;//下拉列表data
	    var quickSelect = new Ext.form.ComboBox({
	                name:'quickInsert',
	                id:'quickInsert',
	                height:22,//IE不兼容这个属性
	                width:180,
					//resizable: true,//可以改变大小 
	                hideTrigger: true,//隐藏按钮
	                matchFieldWidth:true,
	                margin:'-1',
	                enableKeyEvents:true,//true用于启用HTML输入表单项的按键事件代理
	                store:new Ext.data.SimpleStore({
	                    fields:['id', 'name', 'dbName'],
	                    data: insertDataList
	                }),
		            valueField:'id',
		            displayField:'name',
		            emptyText:this.templPropety.onlyname,
		            listeners: {
		            	specialkey: function(field, e){//任何与导航相关的键（方向键、Tab键、回车键、退格键等）按下时触发。你能根据 Ext.EventObject.getKey判断按下的是哪个键。                  
				            if (e.getKey() == e.ENTER) {
								var inputValue=field.getValue();
								var map = new HashMap();
								map.put('inputValue',inputValue);
								map.put('opt','1');
								Rpc({functionId:'MB000020015',async:false,success:function(form,action){
									var result=Ext.decode(form.responseText);
									insertDataList=result.insertDataList;//下拉列表数据
									var combo=Ext.getCmp("quickInsert");//获得下拉列表对象
									combo.store.insert(0,insertDataList);//初始化下拉列表
									combo.expand();//展开下拉列表
								}},map);
				            }
			            },
			            select:function(combo,record){//选中
			            	var id=record.get("id");
			            	var dbpre=id.split("|");
			            	if(dbpre[0]=="dbpre"){//为人员库时
			            		return;
			            	}
			            	var dbName=record.get("dbName");
			            	var map = new HashMap();
								map.put('id',id);
								map.put('dbName',dbName);
								map.put('opt','2');
								map.put('tabid',templateMain_me.templPropety.tab_id);
								Rpc({functionId:'MB000020015',async:false,success:function(form,action){
									var result=Ext.decode(form.responseText);
									var flag = result.flag;
									if(flag){
										templateTool_me.refreshAll();//刷新列表、卡片
										var combo=Ext.getCmp("quickInsert");//获得下拉列表对象
										combo.clearValue();//清除当前在ComboBox中设置的任何值。
									}
								}},map);
			            },
			            keyup: function(field,e){//输入监听
			            		var inputValue=field.getValue();
								var map = new HashMap();
								map.put('inputValue',inputValue);
								map.put('opt','1');
								Rpc({functionId:'MB000020015',async:false,success:function(form,action){
									var result=Ext.decode(form.responseText);
									insertDataList=result.insertDataList;//下拉列表数据
									var combo=Ext.getCmp("quickInsert");//获得下拉列表对象
									combo.store.insert(0,insertDataList);//初始化下拉列表
									combo.expand();//展开下拉列表
								}},map);
				            }
			            
		            }
		      });
		   
	    	//lis 20160406 添加“加号”图片
		   templateMain_me.quickInsert = Ext.widget({
				xtype: 'container',
				style:'border:1px solid #c5c5c5;background:url('+rootPath+'/images/new_module/nocycleaddlittle.png) no-repeat center left;background-size:20px 20px;',
				layout:'hbox',
				padding:'0 0 0 16',
				items:[quickSelect]
			})
			
		var toolbar = Ext.getCmp('templmain_toolbar');
	// 	toolbar.add("->");
	 	toolbar.add({ xtype: 'tbfill', id:'fill_right' });
		toolbar.add(templateMain_me.quickInsert);
	//	toolbar.add(" ");
		toolbar.add({ xtype: 'tbspacer', id:'right_space' ,width: 10 });
	//	console.log(toolbar.items.items[12].lastBox.height);
	},
	
    /**
     *生成快速查询框 开始进入及切换列表统一调用此方法。
     *
     */
	createSearchBox:function(){
        
		//29032 linbz 在切换卡片列表时 或者 修改栏目设置的指标名称时，不销毁快速查询框对象，更换store
        if (templateMain_me.SearchBox){  
            if(templateMain_me.SearchBox.queryField){
                templateMain_me.SearchBox.queryField.on('expand',function(store,records){
                	var fieldsArrayOK = null;
                    if(templateMain_me.templPropety.view_type=="card"){
                        fieldsArrayOK = templateCard_me.fieldsArray;
                    }else{
                        fieldsArrayOK = templateList_me.fieldsArray;
                    }
                    
                    if(!Ext.getCmp('queryCondPaneId'))
                        return;
                    if(!Ext.getCmp('queryCondPaneId').items)
                        return;
                    if(!Ext.getCmp('queryCondPaneId').items.items[0])
                        return;
                    if(!Ext.getCmp('queryCondPaneId').items.items[0].items)
                        return;
                    if(!Ext.getCmp('queryCondPaneId').items.items[0].items.items[1])
                        return;
                        
                    var lenbox = Ext.getCmp('queryCondPaneId').items.items.length;
                    var itemStore1 = Ext.create('Ext.data.Store',{
                       fields:['type','itemid','itemdesc','codesetid','format','codesource','ctrltype','nmodule','parentid','operationData','codesetValid'],
                       data:fieldsArrayOK
                    });
                    
                    templateMain_me.SearchBox.queryField.itemStore=itemStore1;
                    
                    for(var i=0;i<lenbox;i++){
                        var valueid = Ext.getCmp('queryCondPaneId').items.items[i].items.items[1].getValue();
                        Ext.getCmp('queryCondPaneId').items.items[i].items.items[1].setStore(itemStore1);
                        Ext.getCmp('queryCondPaneId').items.items[i].items.items[1].setValue(valueid);
                    }
                });
            }
            
            templateMain_me.SearchBox.show();
            return;
        }
        
        var ecmptyText = "请输入姓名";
        if(this.templPropety.infor_type == "2")
            ecmptyText = "请输入单位名称";
        else  if(this.templPropety.infor_type == "3")
            ecmptyText = "请输入岗位名称";
            
        var subModuleId = "";
        var map = new HashMap();
        map.put('tab_id',this.templPropety.tab_id);
        if (this.templPropety.view_type=="card"){
            map.put('fieldsMap',templateCard_me.fieldsMap);
            subModuleId=templateCard_me.subModuleId;
        }else{
            map.put('fieldsMap',templateList_me.fieldsMap);
            subModuleId=templateList_me.subModuleId;
        }
        
        templateMain_me.SearchBox = Ext.create("EHR.querybox.QueryBox",{
            emptyText:ecmptyText,
            subModuleId:subModuleId,
            customParams:map,
            funcId:"MB00003008",
            fieldsArray:(this.templPropety.view_type=="card")?templateCard_me.fieldsArray:templateList_me.fieldsArray,
            success:templateMain_me.searchBoxOK
        });
        
        var toolbar = Ext.getCmp('templmain_toolbar');
        toolbar.add(templateMain_me.SearchBox);
    },
    /**
     *快速查询框回调函数
     *
     */
    searchBoxOK:function(e){
        (templateMain_me.templPropety.view_type=="card")?templateCard_me.loadStore(e):templateList_me.loadStore(e);
    },
    
    /**
     *删除快速查询框
     *
     */
    removeSearchBox:function(){
            /** 删除查询控件 */
        if (templateMain_me.SearchBox){
            templateMain_me.SearchBox.hide();
        }
        return;
        var toolbar = Ext.getCmp('templmain_toolbar');
        toolbar.remove(templateMain_me.SearchBox);
    },
	//卡片加载完后的回调
	addCardPanel:function(){
	    var cardPanel=templateCard_me.getMainPanel();
		templateMain_me.bodyContainer.add(cardPanel);
	},
	
	//行颜色改变事件 lis 20160412
	rowCssChangeEvent:function(grid,scope,isRefresh){
		var lockedGrid = null;
		var lockedView = null;
		var normalView = grid.getView();
		var lockedRowNode = null;
		var normalRowNode = null;
		if(grid.enableLocking){//如果当前grid是锁列的 
			lockedGrid = grid.lockedGrid;//锁列所在的grid
			lockedView =  lockedGrid.getView();
		}
		 //鼠标进入行颜色改变 lis 20160409
        grid.on('itemmouseenter',function(view,record,el,index){
        	if(lockedGrid){
        		lockedRow = lockedView.getNode(index);
        		templateMain_me.itemMouseEnterChangeCss(lockedView,lockedRow,index);
        	}
        	normalRowNode = normalView.getNode(index);
        	templateMain_me.itemMouseEnterChangeCss(normalView,normalRowNode,index);
		},scope);
        
       //鼠标离开行颜色改变 lis 20160409
        grid.on('itemmouseleave',function(view,record,el,index){
        	if(lockedGrid){
        		lockedRow = lockedView.getNode(index);
        		templateMain_me.itemMouseLeaveChangeCss(lockedView,lockedRow,index);
        	}
        	normalRowNode = normalView.getNode(index);
        	templateMain_me.itemMouseLeaveChangeCss(normalView,normalRowNode,index);
		},scope);
        
        //鼠标点击行颜色改变 lis 20160409
		grid.on('itemclick',function(view,record,el,index){
				templateMain_me.selecRowChangeCss(grid,index);
			},scope);
		
		//键盘上下箭头，改变行
		grid.on('rowkeydown',function(view, r, tr, index, e, eOpts ){
	        	//第0列
			 var count =  grid.getStore().getCount();
			 var rowIndex = 0;
			 var refresh = false;
	  		   if(e.keyCode == 40){//down
	  			   rowIndex = index+1; 
	  			   if(rowIndex<count){
	  				   refresh = true;
	  				   templateMain_me.selecRowChangeCss(grid,rowIndex);
	  			   }
	  		   }else if(e.keyCode == 38){//up
	  			   if(index > 0){
	  				   rowIndex = index-1; 
	  				   refresh = true;
	  				   templateMain_me.selecRowChangeCss(grid,rowIndex);
	  			   }
	  		   }
	  		   
	  		   if(isRefresh && refresh){
	  			   var record =   grid.getStore().getAt(rowIndex);
	  			   var objectid = record.data.objectid_e;
	  			   var taskid = record.data.realtask_id_e;
	  			   var insid = record.data.ins_id;
	  			   scope.switchPerson(objectid,insid,taskid);
	  			   scope.personListCurRecord=record;
	  		   }
	        },scope);
	},
	
	//鼠标进入行时颜色改变 lis 20160506
	itemMouseEnterChangeCss:function(view,rowNode,index){
		var rowCss = rowNode.rows[0].className;
    	if(rowCss.indexOf('x-grid-row-selected-itemclick') < 0){
    		if(index%2 == 0)
    			view.removeRowCls(index,'x-grid-row-selected-white');
    		else
    			view.removeRowCls(index,'x-grid-row-selected-grey');
    		view.addRowCls(index,'x-grid-row-selected-mouseenter');
    	}
	},
	
	//鼠标离开行时颜色改变 lis 20160506
	itemMouseLeaveChangeCss:function(view,rowNode,index){
		var rowCss = rowNode.rows[0].className;
		if(rowCss.indexOf('x-grid-row-selected-itemclick') < 0){//不是选中行
    		view.removeRowCls(index,'x-grid-row-selected-mouseenter');
    		if(index%2 == 0)
    			view.addRowCls(index,'x-grid-row-selected-white');
    		else
    			view.addRowCls(index,'x-grid-row-selected-grey');
    	}
	},
	
	//选中时改变行样式 lis 20160506
	selecRowChangeCss:function(grid,index){
		var lockedGrid = null;
		var lockedView = null;
		var normalView = grid.getView();
		var lockedRowNode = null;
		var normalRowNode = null;
		if(grid.enableLocking){//如果当前grid是锁列的 
			lockedGrid = grid.lockedGrid;//锁列所在的grid
			lockedView =  lockedGrid.getView();
			templateMain_me.selecRowCss(lockedView,index);
		}
		templateMain_me.selecRowCss(normalView,index,grid.getStore().getCount());
	},
	
	//选中时改变行样式 lis 20160412
	selecRowCss:function(view,index,storeCount){
		//循环清除其他选中样式
		for(var i=0;i<storeCount;i++){
			var node = view.getNode(i);
			if(node!=null){
				var rowCss = node.rows[0].className;
				if(rowCss.indexOf('x-grid-row-selected-itemclick') >= 0){
					//是选中行，则清除选中样式
					view.removeRowCls(i,'x-grid-row-selected-itemclick');
					//添加原来样式
					if(i%2 == 0)
						view.addRowCls(i,'x-grid-row-selected-white');
					else
						view.addRowCls(i,'x-grid-row-selected-grey');
				}
			}
		};
		//去除掉鼠标进入和本身的样式
		view.removeRowCls(index,'x-grid-row-selected-mouseenter');
		if(index%2 == 0)
			view.removeRowCls(index,'x-grid-row-selected-white');
		else
			view.removeRowCls(index,'x-grid-row-selected-grey');
		//添加选中样式
		view.addRowCls(index,'x-grid-row-selected-itemclick');
	},
	/**
	 * 循环调用后台修改locked_time字段
	 */
    setEmployTime:function(){
    	var map = new HashMap();
    	initPublicParam(map,templateMain_me.templPropety);
    	Rpc({functionId:'MB00001005',async:false,success:function(){
        },scope:this},map);
    },
    getBrowser:function(){
    	var browser={  
        		versions:function(){  
    	           var u = navigator.userAgent.toLowerCase();  
    	           return {//移动终端浏览器版本信息  
    	        	    android: u.match(/android/i) == "android", //android
    	        	    ucweb: u.match(/ucweb/i) == "ucweb", //uc 
    	                uc7: u.match(/rv:1.2.3.4/i) == "rv:1.2.3.4", //uc7
    	                mdip: u.match(/midp/i) == "midp", //  
    	                ios: !!u.match(/\(i[^;]+;( U;)? CPU.+Mac OS X/), //ios终端  
    	                winc: u.match(/windows ce/i) == "windows ce", //
    	                iPhone: u.match(/iphone os/i) == "iphone os", //
    	                iPad: u.match(/ipad/i) == "ipad", //是否iPad  
    	                winm: u.match(/windows mobile/i) == "windows mobile", //
    	                weixin: u.match(/MicroMessenger/i)=="MicroMessenger"//是否是微信打开
    	            };  
             	}(),  
             	language:(navigator.browserLanguage || navigator.language).toLowerCase()  
    		};
    	return browser;
    },
    /**
	 * 返回
	 * @return Panel
	 */
	returnBack:function(callBack_close,return_flag,bos_flag){
	    if(callBack_close){
	    	clearInterval(window.lockedtimer);
            Ext.callback(eval(callBack_close),null,[]);
        }
        else {
            if (return_flag=="11"){//主页待办
            	Ext.destroy(parent.Ext.getCmp('serviceHallWin'));
				location.href=rootPath+"/templates/index/hcm_portal.do?b_query=link";
            }else if (return_flag=="12"){//主页待办更多列表
                location.href=rootPath+"/general/template/matterList.do?b_query=link";
            }else if (return_flag=="13"){//关闭
                window.parent.close(); 
            }else if (return_flag.indexOf("7-")==0){ //预警列表 xx:预警id
            	location.href=rootPath+"/system/warn/result_manager.do?b_query=link&warn_wid="+templateTool_me.templPropety.return_flag.substring(2);
            }else if(return_flag.indexOf("8-")==0){
            	location.href=rootPath+"/dtgh/party/person/searchbusinesslist.do?b_search=link&tabIndex=0&param="+templateTool_me.templPropety.return_flag.substring(2);
            }else if(return_flag.indexOf("-r")!=0){//证明是撤回的单据的返回
            	location.href=rootPath+"/module/template/templatenavigation/TemplateNavigation.html?b_query=link&sys_type=1&module_id="+templateTool_me.templPropety.module_id;
            }
        }
	}
});
