/**
 * 薪资发放、薪资审批、薪资上报
 * @createtime July 02, 2015 9:07:55 PM
 * @author chent
 * 
 * */
Ext.define('SalaryTemplateUL.SalaryTemplate',{
	salarypay_me:'',
	cookie:'',
	tableObj:'',
	dbname:'',
	constructor:function(config) {
		cookie = Ext.create('Ext.state.CookieProvider', {
		    path: "/module/gz/",//作用路径
		    expires: new Date(new Date().getTime()+(1000*60*60*24*30)) //30天
		});
		salarypay_me = this;
		this.init(config.url);
	},
	// 初始化函数
	init:function(url) {
		var map = new HashMap();
		map.put("url",url);
	    Rpc({functionId:'GZ00000201',async:false,success:salarypay_me.getTableOK},map);
	},
	// 加载表单
	getTableOK:function(form,action){
		var result = Ext.decode(form.responseText);
		var conditions=result.tableConfig;
		var obj = Ext.decode(conditions);
		//配置上下移动
		obj.beforeBuildComp=function(config){
		    config.tableConfig.sortableColumns=false;
		    config.tableConfig.enableColumnMove=false;
            var columns = config.tableConfig.columns;
            //47882 薪资发放界面，去掉设置列是否可以显示功能，见附件。
            for(var i in columns){
                var c = columns[i];
                c.menuDisabled = true;
            }
		};
		
		tableObj = new BuildTableObj(obj);
		salarypay_me.createSearchPanel();
	},
	// 薪资发放界面【薪资类别】按钮渲染
	cnameForPay:function(value, metaData, Record){
		var salaryid = Record.data.salaryid_safe;
		var appdate = Record.data.appdate_safe;
		var count = Record.data.count_safe;
		var imodule = Record.data.imodule_safe;
		var viewtype = Record.data.viewtype_safe;
		var manager = Record.data.manager;
		var tip = Record.data.tip;
		var canComing = Record.data.can_coming;
		var html = "";
		if(canComing == "true") {
			html += "<a href=javascript:salarypay_me.openSalaryPayPage('"+salaryid+"','"+appdate+"','"+count+"','"+imodule+"','"+viewtype+"','"+manager+"');>" + value + "</a>" +
				"&nbsp;&nbsp;<span style='color:gray;'>" + tip + "</span>"; 
		}else {
			html += "<span style='color:gray;'>" + value + "&nbsp;&nbsp;" + tip + "</span>"
		}
		return html;
	},
	openSalaryPayPage:function(salaryid, appdate, count, imodule, viewtype,manager){
		if(manager=="1"&&appdate.length==0){
			Ext.Msg.alert(common.button.promptmessage, "该类别管理员还未开始业务处理！");
			return;
		}
		var me = this;
/**		var obj = new Object();
		obj.salaryid = salaryid;
		obj.appdate = appdate;
		obj.count = count;
		obj.imodule = imodule;
		obj.viewtype = viewtype;
		obj.returnflag = "menu";
		Ext.accounting = obj;
		tableObj.getMainPanel().removeAll(false);
		me.SearchBox.removeAllKeys();
		Ext.require('SalaryUL.SalaryAccounting', function(){
			GzGlobal = Ext.create("SalaryUL.SalaryAccounting",Ext.accounting);
		});*/
		var store= tableObj.tablePanel.getStore();
		var currentPage=store.currentPage;//当前页码 amount为0时页码不变
		window.location.href="/module/gz/salaryaccounting/SalaryAccounting.html?salaryid="+salaryid+"&appdate="+appdate+"&count="+count+"&imodule="+imodule+"&viewtype="+viewtype+"&currentPage="+currentPage+"&returnflag=menu";
	},
	// 返回薪资发放
	returnSalaryAccounting:function(){
		salaryObj.getMainPanel().destroy();
		Ext.require('SalaryUL.SalaryAccounting', function(){
			GzGlobal = Ext.create("SalaryUL.SalaryAccounting", Ext.accounting);
		});
	},
	// 返回薪资审批
	returnSalarySp:function(salaryid){
		salarypay_me.gotoSalaryTable(salaryid);
	},
	// 薪资发放界面【变动比对】按钮渲染
	compareForPay:function(value, metaData, Record){
		var salaryid = Record.data.salaryid_safe;
		var imodule = Record.data.imodule_safe;
		var appdate = Record.data.appdate_safe;
		var count = Record.data.count_safe;
		var royalty_valid = Record.data.royalty_valid;// 提成工资是否启用
		// 提成工资启用时不显示
		if(royalty_valid == "1"){
			return "";
		}
		var html = "<a href=javascript:salarypay_me.checkChangeCompare('"+salaryid+"','"+imodule+"','"+appdate+"','"+count+"');><img src='/images/new_module/changecompare.gif' border=0></a>"; 
		
		return html;
	},
	// 检查是否存在变动比对表，没有则显示提示。
	checkChangeCompare:function(salaryid, imodule, appdate, count){
		
		//弹出等待
		var mainPanel = tableObj.getMainPanel();
		var myMask = new Ext.LoadMask({
		    target : mainPanel
		});
		myMask.show();
		
		var map = new HashMap();
		map.put("salaryid",salaryid);
		map.put("imodule",imodule);
		map.put("appdate",appdate);
		map.put("count",count);
	    Rpc({functionId:'GZ00000203',success:function(data) {
	    	myMask.hide();
	    	var checkResult = Ext.decode(data.responseText); // 查询结果
	    	var flag=checkResult.succeed;
			if(flag==true){
		    	if (checkResult.isExistAdd === "0" 
		    		&& checkResult.isExistDel === "0" 
		    		&& checkResult.isExistInfo === "0" 
		    		&& checkResult.isExistStop === "0") {
		    		var salaryRs = checkResult.salaryid;
		    		var cnameRs = checkResult.cname;
		    		Ext.Msg.alert(common.button.promptmessage, salaryRs+"."+cnameRs+gz.msg.nothaveperson);
		    		return;
				}
		    	salarypay_me.openChangeComparePage(salaryid, imodule, checkResult);
		    }else{
		      	Ext.showAlert(checkResult.message);
		    }
	    },scope:this},map);
	},
	// 打开变动比对页面
	openChangeComparePage:function(salaryid, imodule, checkResult){
		
		var obj = new Object();
		obj.salaryid = salaryid;
		obj.imodule = imodule;
		obj.isExistAdd = checkResult.isExistAdd;//新增  1:有0:无
		obj.isExistDel = checkResult.isExistDel;//减少  1:有0:无
		obj.isExistInfo = checkResult.isExistInfo;//信息变动  1:有0:无
		obj.isExistStop = checkResult.isExistStop;//停发  1:有0:无
		obj.dbname = checkResult.dbname;//dbname转化usr=>在职人员库
		obj.pathform=0;//标示来源页面，0来自类别列表
		obj.currentPage=tableObj.tablePanel.getStore().currentPage;//当前页码 amount为0时页码不变
		tableObj.getMainPanel().removeAll(false);
		Ext.require('SalaryUL.ComparisonWithFile', function(){
			ChangeCompareGlobal = Ext.create("SalaryUL.ComparisonWithFile", obj);
		});
	},
	// 薪资发放界面【业务处理】按钮渲染
	dealtoForPay:function(value, metaData, Record){
		var salaryid = Record.data.salaryid_safe;
		var appdate = Record.data.appdate_safe;
		var count = Record.data.count_safe;
		var imodule = Record.data.imodule_safe;
		var viewtype = Record.data.viewtype_safe;
		var manager = Record.data.manager;
		var html = "<a href=javascript:salarypay_me.openSalaryPayPage('"+salaryid+"','"+appdate+"','"+count+"','"+imodule+"','"+viewtype+"','"+manager+"');><img src='/images/new_module/dealto.gif' border=0></a>"; 
		
		return html;
	},
	// 薪资审批界面【薪资类别】按钮渲染
	cnameForApprov:function(value, metaData, Record){
		var collectPoint = Record.data.collectpoint;
		var salaryid = Record.data.salaryid_safe;
		var imodule = Record.data.imodule_safe;
		//var appdate = Record.data.appdate_safe;
		//var count = Record.data.count_safe;
		//var html = "<a href='" + collectPoint + "?b_query=link&ori=0&zjjt=1&salaryid=" + salaryid + "&gz_module=" + imodule + "'>" + value + "</a>"; 
		var html = "<a href=javascript:salarypay_me.gotoSalaryTable('"+salaryid+"','"+imodule+"');>" + value + "</a>"; 
		return html;
	},
	// 薪资审批界面【操作】按钮渲染
	operationForApprov:function(value, metaData, Record){
		var collectPoint = Record.data.collectpoint;
		var isCurr_user = Record.data.iscurr_user;
		var salaryid = Record.data.salaryid_safe;
		var imodule = Record.data.imodule_safe;
		//var appdate = Record.data.appdate_safe;
		//var count = Record.data.count_safe;
		//var html = "<a href='" + collectPoint + "?b_query=link&ori=0&zjjt=1&salaryid=" + salaryid + "&gz_module=" + imodule + "'>" + isCurr_user + "</a>";  
		var html = "<a href=javascript:salarypay_me.gotoSalaryTable('"+salaryid+"','"+imodule+"');>" + isCurr_user + "</a>"; 
		return html;
	},
	gotoSalaryTable:function(salaryid,imodule){
//		tableObj.getMainPanel().removeAll(false);
		this.SearchBox.removeAllKeys();
		
		var store= tableObj.tablePanel.getStore();
		var currentPage=store.currentPage;//当前页码 amount为0时页码不变
		window.location.href="/module/gz/salaryspcollect/SalarySpCollect.html?salaryid="+salaryid+"&imodule="+imodule+"&currentPage="+currentPage+"&returnflag=menu";
/**		Ext.require('Salarybase.salaryspcollect.SalarySpCollect', function(){
			SalarySpCollect = Ext.create("Salarybase.salaryspcollect.SalarySpCollect",{salaryid:salaryid,returnflag:"menu"});
		});*/
	},
	// 数据上报界面【薪资类别】按钮渲染
	cnameForUp:function(value, metaData, Record){
		var salaryid = Record.data.salaryid_safe;
		var appdate = Record.data.appdate_safe;
		var count = Record.data.count_safe;
		var imodule = Record.data.imodule_safe;
		var viewtype = Record.data.viewtype_safe;
		var manager = Record.data.manager;
		var tip = Record.data.tip;
		var can_coming = Record.data.can_coming;
		var html = "";
		if(can_coming=='true') {
			html += "<a href=javascript:salarypay_me.openSalaryPayPage('"+salaryid+"','"+appdate+"','"+count+"','"+imodule+"','"+viewtype+"','"+manager+"');>" + value + "</a>" +
			"&nbsp;&nbsp;<span style='color:gray;'>" + tip + "</span>"; 
		}else {
			html += "<span style='color:gray;'>" + value + "&nbsp;&nbsp;" + tip + "</span>"
		}
		return html;
	},
	// 数据上报界面【业务处理】按钮渲染
	dealtoForUp:function(value, metaData, Record){
		var salaryid = Record.data.salaryid_safe;
		var appdate = Record.data.appdate_safe;
		var count = Record.data.count_safe;
		var imodule = Record.data.imodule_safe;
		var viewtype = Record.data.viewtype_safe;
		var manager = Record.data.manager;
		var html = "<a href=javascript:salarypay_me.openSalaryPayPage('"+salaryid+"','"+appdate+"','"+count+"','"+imodule+"','"+viewtype+"','"+manager+"');><img src='/images/new_module/dealto.gif' border=0></a>"; 
		
		return html;
	},
	// 查询控件
	createSearchPanel:function(){
		var me = this;
		var map = new HashMap();
		map.put("url",url);
		me.SearchBox = Ext.create("EHR.querybox.QueryBox",{
			hideQueryScheme:true,
			emptyText:gz.msg.searchmsg,
			subModuleId:"gz_salaryTemplate_00000001",
			customParams:map,
			funcId:"GZ00000201",
			success:salarypay_me.loadTable//重新加载数据列表
		});
		var toolBar = Ext.getCmp("salarypay_toolbar");
		toolBar.add(me.SearchBox);
	},
	
	//刷新页面
	loadTable:function(form){
		var store = Ext.data.StoreManager.lookup('salarypay_dataStore');
		store.currentPage=1;
		store.load();
	},
	historyViewForApprov:function (value, metaData, Record) {
        var salaryid = Record.data.salaryid_safe;
        var appdate = Record.data.appdate_safe;
        var count = Record.data.count_safe;
        var imodule = Record.data.imodule_safe;
        var viewtype = Record.data.viewtype_safe;
        var manager = Record.data.manager;
        var html = "<a href=javascript:salarypay_me.openHistoryView('"+salaryid+"','"+appdate+"','"+count+"','"+imodule+"','"+viewtype+"','"+manager+"');><img src='/images/new_module/search_blue.gif' border=0></a>";

        return html;
    },
	openHistoryView:function(salaryid, appdate, count, imodule, viewtype,manager){
        var obj = new Object();
        obj.salaryid = salaryid;
        obj.appdate = appdate;
        obj.count = count;
        obj.imodule = imodule;
        obj.viewtype =viewtype;
        obj.manager = manager;
        Ext.require('SalaryTemplateUL.ApprovalSituation', function(){
            Ext.create("SalaryTemplateUL.ApprovalSituation", obj);
        });
	},
	rowdbclick:function(value, record){
		var salaryid = record.data.salaryid_safe;
		var appdate = record.data.appdate_safe;
		var count = record.data.count_safe;
		var imodule = record.data.imodule_safe;
		var viewtype = record.data.viewtype_safe;
		var manager = record.data.manager;
		salarypay_me.openSalaryPayPage(salaryid,appdate,count,imodule,viewtype,manager);
		
	}
});
