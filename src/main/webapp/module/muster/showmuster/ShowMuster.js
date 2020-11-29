Ext.define('SetupschemeUL.ShowMuster',{
	showMuster:'',
	moduleID : '0',//模块号，=0：员工管理；=1：组织机构；参照t_hr_subsys中内容，如果t_hr_subsys没有则按顺序添加；默认为0。
    musterType : '1',//花名册类型；=1：人员花名册；=2：单位花名册；=3：岗位花名册；=4：基准岗位花名册；默认为“1”。
    tabid :'',
	tableObj:undefined,
	source:undefined,
	currentPage:'1',
	musterManageStyleid:undefined,//花名册分类id
	constructor:function(config) {
		showMuster=this;
		musterManageStyleid=config.musterManageStyleid;
		moduleID=config.moduleID==undefined?'0':config.moduleID;
		musterType=config.musterType==undefined?'1':config.musterType;
		currentPage=config.currentPage==undefined?'1':config.currentPage;
		tabid=config.tabid;
		source=config.source;
		showMuster.init();
	},
	init:function(){
		 var map = new HashMap();
		 map.put("moduleID",moduleID);
		 map.put("musterType",musterType);
		 map.put("tabid",tabid);
		 Rpc({functionId:'MM01020001',success: function(form,action){
				var result = Ext.decode(form.responseText);
				if(result.succeed){
					showMuster.createTableOK(result,form,action);
				}else{
					Ext.showAlert(result.message);
				}
		 }},map);
	},
	createTableOK:function(result,form,action){
		Ext.util.CSS.createStyleSheet(".scheme-selected-cls{text-decoration:underline;margin-left:10px;margin-right:10px;}","underline");
		var conditions=result.tableConfig;
		var obj = Ext.decode(conditions);
		obj.openColumnQuery = true;
		tableObj = new BuildTableObj(obj);
		// 给querubox添加自定义值
		var params = new Object();
		params.tabid=tabid;
		Ext.getCmp("showMuster"+tabid+"_querybox").setCustomParams(params);
	},
	loadStore:function(){
		Ext.getCmp("showMuster"+tabid+"_tablePanel").getStore().reload({page:1});
	},
	reloadStore:function(){
		window.location.href="/module/muster/ShowMuster.html?musterType="+musterType+"&moduleID="+moduleID+"&tabid="+tabid+"";
	},
	//打开页面设置
	pageSetup:function(){
		var map = new HashMap();
		map.put('tabid', tabid);
		map.put('opt','1');//opt等于"1",回显页面数据
		Rpc({functionId : 'MM01020003',success: function(form,action){
			var result = Ext.decode(form.responseText);
			Ext.require('SetupschemeUL.PageSetupMuster', function(){
				Ext.create("SetupschemeUL.PageSetupMuster", {tabid:tabid,result:result,callbackfn:'showMuster.savePageSet'});
			}); 
		}}, map);
	},
	//栏目设置
	schemeSetting:function(){
		Ext.require("EHR.tableFactory.plugins.SchemeSetting",function(){
		  	new EHR.tableFactory.plugins.SchemeSetting({
		  		subModuleId:'showMuster'+tabid,
		  		schemeItemKey:'',
		  		itemKeyFunctionId:'',
		  		moduleId:moduleID,
		  		viewConfig:{publicPlan: true},
		  		closeAction:function(){
			  		 window.location.reload();
			  	},
			  	scope:showMuster
		  	});
		});
	},
	//保存页面设置
	savePageSet:function(pagesetupValue,titleValue,pageheadValue,pagetailValue,textValueValue) {
    	var map = new HashMap();
		map.put("tabid", tabid);	
		map.put("pagesetupValue", pagesetupValue);
		map.put("titleValue", titleValue);//标题
		map.put("pageheadValue", pageheadValue);//页头
		map.put("pagetailidValue", pagetailValue);//页尾
		map.put("textValueValue", textValueValue);//正文
		map.put("opt",'2');//opt=2,保存页面设置数据
    	Rpc({functionId : 'MM01020003',success: function(result){
    		var result = Ext.decode(result.responseText);
    		var code = result.code;
    		if(code != "2") {
	    		Ext.showAlert(hint_file);
    		}
    	}}, map);
    },
	//导出excel或PDF
	exportExcelOrPdf:function(flag){
		var totalCount = tableObj.dataStore.totalCount;
		if(totalCount>200000){
			Ext.showAlert(totalCounttomore);
			return;
		}
		var columns = tableObj.tablePanel.columns;
		var columnStore = new Array();
		for(var i = 0;i<columns.length;i++){
			if(columns[i].xtype!="rownumberer"){
				columnStore.push(columns[i]);
			}
		}
		Ext.require('SetupschemeUL.ExportData',function(){
			Ext.create("SetupschemeUL.ExportData",{
				totalCount:totalCount,
				moduleID:moduleID,
				musterType:musterType,
				flag:flag,
				tabid:tabid,
				columns:columnStore
			});
		})
	},
	//数据范围
	dataRangeFunc:function(){
		Ext.require('SetupschemeUL.DataRange', function(){
				Ext.create("SetupschemeUL.DataRange", {tabid:tabid,musterType:musterType,moduleID:moduleID,callbackfn:'showMuster.showFilterMuster'});
		});
	},
	//数据范围回写函数
	showFilterMuster:function(map){
		//显示过滤
		Rpc({functionId:'MM01020001',success: function(form,action){
			var result = Ext.decode(form.responseText);
			if(result.succeed){
				showMuster.loadStore();
			}else{
				Ext.MessageBox.alert(dataRangeRec.text.promptInformation,result.message);
			}
		}},map);
	},
	//返回
	returnMusterManage:function(){
		if("homepage"==source){
			addrss="/templates/index/hcm_portal.do?b_query=link";
			window.location.href=addrss;
		}else if("homepage2"==source){
			addrss="/general/muster/emp_muster.do?b_query=link";
			window.location.href=addrss;
		}else {
			var address ="/module/muster/mustermanage/MusterManage.html?musterType="+musterType+"&moduleID="+moduleID+"";
			if(musterManageStyleid!=undefined){
				address+="&musterManageStyleid="+musterManageStyleid+"";
			}
			address+="&currentPage="+currentPage;
			window.location.href=address;
		}
	}
});
