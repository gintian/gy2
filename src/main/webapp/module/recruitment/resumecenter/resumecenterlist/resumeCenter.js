var Global = new Object();

Global.fromModule = "resumeCenter"; // resumeCenter:简历中心 talents:人才库
Global.oldfromModule = "resumeCenter";
Global.fromName = "简历中心"; // 简历中心、人才库
Global.pageDesc = ""; // 获取页面描述信息（current`schemeValues`pagesize连接成的字符串)
Global.schemeArr = new Array(0, 0, 0); // 应聘情况所有区域值初始化
Global.searchBarLink = null; // 获取当前选择方案链接的对象
Global.linkEvent = ""; // 记录链接对应的点击事件
Global.index = 0; // 防止超链接被连续点击的计数器
Global.flagArr = new Array(true,true,true); //当全部为true 时 全部下划横线
Global.selectA0100 = "";
Global.searchBarLink = null; // 获取当前选择方案链接的对象
Global.searchNewBarLink = null; 
Global.setSchemeArr = function(index, value) { // 设置应聘情况相应区域的值
	Global.searchBarLink = document.getElementById(index + "" + value);
	Global.linkEvent = Global.searchBarLink.getAttribute("onclick");
	document.getElementById("all").style.textDecoration = "none";
	var size = Global.searchBarLink.name;
	if (Global.schemeArr[index] == value && 
			!(index=="1"&&value=="1"&&JSON.stringify(Global.schemeArr)=="[0,1,2]")) {
		Global.flagArr[index] = true;
		Global.searchBarLink.style.color = "#1b4a98";
		Global.searchBarLink.style.textDecoration = "none";
		Global.schemeArr[index] = 0;
	} else if(index=="2"&&value=="2"){
		Global.schemeArr = new Array(0, 1, 2);
		var tagA = document.getElementById("searchbar").getElementsByTagName("a");
		for ( var i = 0; i < tagA.length; i++) {
			tagA[i].style.textDecoration = "none";
			tagA[i].style.color = "#1b4a98";
		}
		Global.searchBarLink.style.textDecoration = "underline";
	} else {
		Global.flagArr[index] = false;
		for ( var i = 1; i <= size; i++) {
			var href = document.getElementById(index + "" + i);
			if (i == value) {
				href.style.color = "green";
				href.style.textDecoration = "underline";
			} else {
				href.style.color = "#1b4a98";
				href.style.textDecoration = "none";
			}
		}
		
		if(index=="1"&&value=="1"&&JSON.stringify(Global.schemeArr)=="[0,1,2]"){
			Global.searchNewBarLink = document.getElementById("2"+ "" +"2");
			Global.searchNewBarLink.style.color = "#1b4a98";
			Global.searchNewBarLink.style.textDecoration = "none";
			Global.schemeArr[2] = 0;
		}
			
		if(Global.flagArr[1] = true){
			Global.schemeArr[1] = 0;
		}
		
		Global.schemeArr[index] = value;
	}
	//当全部为true 时 全部下划横线
	if(Global.flagArr[0]&&Global.flagArr[1]&&Global.flagArr[2])
		document.getElementById("all").style.textDecoration = "underline";
}
// 查询所有记录
Global.searchAll = function() {
	Global.schemeArr = new Array(0, 0, 0);
	var tagA = document.getElementById("searchbar").getElementsByTagName("a");
	for ( var i = 0; i < tagA.length; i++) {
		tagA[i].style.textDecoration = "none";
		tagA[i].style.color = "#1b4a98";
	}
	document.getElementById("all").style.textDecoration = "underline";
}
// 应聘情况栏查询
Global.schemeSearch = function() { // 根据schemeArr的值进行相应查询
	if (Global.searchBarLink != null)
		Global.searchBarLink.setAttribute("onclick", "");
	var schemeValues = '';
	for ( var i = 0; i < Global.schemeArr.length; i++) {
		if (i != Global.schemeArr.length - 1)
			schemeValues += Global.schemeArr[i] + ",";
		else
			schemeValues += Global.schemeArr[i];
	}
	var config = tablegrid.getTableConfig();
	var map = new HashMap();
	map.put("flag", "3");
	map.put("from", Global.fromModule);
	map.put("schemeValues", schemeValues);
	map.put("tablekey", config.tablekey);
	Rpc( {
		functionId : 'ZP0000002101',
		success : queryResult
	}, map);
}

// 搜索框查询
Global.boxSearch = function() {
	var searchBoxContent = Ext.getCmp('boxtext').getValue();
	var config = tablegrid.getTableConfig();
	var map = new HashMap();
	map.put("flag", "1");
	map.put("searchBoxContent", searchBoxContent);
	map.put("tablekey", config.tablekey);
	map.put("from", Global.fromModule);
	var schemeValues = '';
	for ( var i = 0; i < Global.schemeArr.length; i++) {
		if (i != Global.schemeArr.length - 1)
			schemeValues += Global.schemeArr[i] + ",";
		else
			schemeValues += Global.schemeArr[i];
	}
	map.put("schemeValues", schemeValues);
	Rpc( {
		functionId : 'ZP0000002101',
		success : queryResult
	}, map);
}

// 查询结果提示
queryResult = function(outparamters) {
	var datastore = Ext.data.StoreManager.lookup('tablegrid_dataStore');
	datastore.load();
	datastore.loadPage(1);
	if (Global.searchBarLink)
		Global.searchBarLink.setAttribute("onclick", Global.linkEvent);
}

Ext.Loader.setConfig({
	enabled: true,
	paths: {
		'sendEmailUL': '/module/recruitment/js/sendEmail.js'
	}
});
//接受职位申请
Global.acceptPositionApply = function(object, record, store) {
	if (record.length == 0) {
		Ext.Msg.alert('提示信息', '请选择操作数据！');
		return;
	}
	var z0301 = "";
	var a0100s = "";
	var z0101s = "";
	for ( var i = 0; i < record.length; i++) {
		if (record[i].data.status != "") {
			Ext.Msg.alert('提示信息',
					record[i].data.a0101 + '已接受过该职位申请！');
			return;
		}
		
		if (record[i].data.z0351 == "") {
			Ext.Msg.alert('提示信息',
					record[i].data.a0101 + '未申请职位！');
			return;
		}

		a0100s += record[i].data.a0100_e+",";
		z0101s +=record[i].data.a0101+",";
		z0301 += record[i].data.z0301_e+",";
	}

	var map = new HashMap();
	map.put("a0101s", z0101s);
	map.put("a0100", a0100s);
	map.put("flag", "isjoin");
	Rpc( {
		functionId : 'ZP0000002102',
		success : function (param){
			var result = Ext.decode(param.responseText);
			if("1" == result.flag) {
				Ext.showAlert(DONOT_ACCEPT_JOB_APPLICATIONS);
				return;
			}else{
				Ext.require('sendEmailUL', function(){
					Ext.create("sendEmailUL.sendEmail", {
						sub_module:"7",
						nModule:"10",
						z0301:z0301,
						a0100s:a0100s,
						a0101s:z0101s,
						title:"接受职位申请",
						fuId:"ZP0000002102",
						function_str:"acceptPositionApply",
						executionMethod:function(obj){
							Ext.data.StoreManager.lookup('tablegrid_dataStore').reload();
						}
					});
				});
			}
		}
	}, map);
	
}
//拒绝职位申请
Global.rejectPositionApply = function(object, record, store) {
	
	if (record.length == 0) {
		Ext.Msg.alert('提示信息', '请选择操作数据！');
		return;
	}
	var z0301 = "";
	var a0100s = "";
	var z0101s = "";
	for ( var i = 0; i < record.length; i++) {
		if (record[i].data.status != "") {
			Ext.Msg.alert('提示信息',
					record[i].data.a0101 + '已处于流程当中，<br/>不允许拒绝职位申请！');
			return;
		}
		
		if (record[i].data.z0351 == "") {
			Ext.Msg.alert('提示信息',
					record[i].data.a0101 + '未申请职位！');
			return;
		}
		a0100s += record[i].data.a0100_e+",";
		z0101s +=record[i].data.a0101+",";
		z0301 += record[i].data.z0301_e+",";
	}
    Ext.require('sendEmailUL', function(){
		Ext.create("sendEmailUL.sendEmail", {
			sub_module:"7",
			nModule:"11",
			z0301:z0301,
			a0100s:a0100s,
			a0101s:z0101s,
			title:"拒绝职位申请",
			fuId:"ZP0000002102",
			function_str:"rejectPositionApply",
			executionMethod:function(obj){
				Ext.data.StoreManager.lookup('tablegrid_dataStore').reload();
			}
		});
	});
}

// 移出人才库
Global.removeTalents = function(object, record, store) {
	if (record.length == 0) {
		Ext.Msg.alert('提示信息', '请选择需要进行业务操作的数据！');
		return;
	}
	Ext.Msg.confirm('提示信息', '确认将所选择的人员移出人才库？', function(btn) {
		if (btn == 'yes') {
			var array = new Array();
			for ( var i = 0; i < record.length; i++) {
				var param = new Array();
				param[0] = record[i].data.a0100_e;
				param[1] = record[i].data.z0301_e;
				param[2] = record[i].data.nbase_e;
				param[3] = record[i].data.a0101;
				array[i] = param;
			}
			var map = new HashMap();
			map.put("array", array);
			map.put("opt", "removeTalents");
			map.put("fromModule", "talents");
			Rpc( {
				functionId : 'ZP0000002103',
				success : Global.operateResult
			}, map);
		}
	});
}

// 删除人员信息
Global.deleteRecords = function(object, record, store) {
	if (record.length == 0) {
		Ext.Msg.alert('提示信息', '请选择人员信息！');
		return;
	}
	Ext.Msg.confirm('提示信息', '确认删除所选择的人员吗？', function(btn) {
		if (btn == 'yes') {
			var array = new Array();
			for ( var i = 0; i < record.length; i++) {
				var param = new Array();
				param[0] = record[i].data.a0100_e;
				param[1] = record[i].data.z0301_e;
				param[2] = record[i].data.nbase_e;
				param[3] = record[i].data.a0101;
				array[i] = param;
			}
			var map = new HashMap();
			map.put("array", array);
			map.put("opt", "delete");
			map.put("fromModule", "resumeCenter");
			Rpc( {
				functionId : 'ZP0000002103',
				success : Global.operateResult
			}, map);
		}
	});
}
// 操作结果提示
Global.operateResult = function(outparamters) {
	var result = "";
	if(outparamters.callback){
		result = outparamters;
		var obj = document.getElementById("positionstatus");
		if(obj && "1" == result.status) {
			resume_me.queryOperationList();
			if("10" == result.nModule)
				obj.innerHTML="处理中";
			else
				obj.innerHTML="已处理";
		}
	}
	else
		result = Ext.decode(outparamters.responseText);
	var warnMsg = "";
	if (result.result != true) {
		if (result.info != '')
			warnMsg = getDecodeStr(result.info);
		else
			warnMsg = '操作失败！';
	} else {
		var datastore = Ext.data.StoreManager.lookup('tablegrid_dataStore');
		datastore.reload();
		var info = getDecodeStr(result.info);
		var msg = getDecodeStr(result.msg);
		if (info)
			warnMsg = getDecodeStr(result.info);

		if (msg && msg != "true")
			warnMsg = msg;
	}
	if (!Ext.isEmpty(warnMsg)) {
		Ext.showAlert(warnMsg);
		return;
	}
	
	var datastore = Ext.data.StoreManager.lookup('tablegrid_dataStore');
	datastore.reload();
}

// 转人才库
Global.turnTalents = function(object, record, store) {
	if (record.length == 0) {
		Ext.Msg.alert('提示信息', '请选择操作数据！');
		return;
	}
	Ext.Msg.confirm('提示信息', '确认将所选择的人员转人才库？', function(btn) {
		if (btn == 'yes') {
			var array = new Array();
			for ( var i = 0; i < record.length; i++) {
				var status = record[i].data.status;
				if (status == "1003") {
					Ext.Msg.alert('提示信息', '已过滤已入职人员！');
					continue;
				}
				var param = new Array();
				param[0] = record[i].data.a0100_e;
				param[1] = record[i].data.nbase_e;
				param[2] = record[i].data.a0101;
				array[i] = param;
			}
			var map = new HashMap();
			map.put("array", array);
			Rpc( {
				functionId : 'ZP0000002104',
				success : turnResult
			}, map);
		}
	});
}

// 转人才库结果
function turnResult(outparamters) {
	var result = Ext.decode(outparamters.responseText);
	if (result.exitTrans != undefined && result.exitTrans != "") {
		Ext.showAlert(result.exitTrans + "在人才库中已经存在！");
	}// else if(outparamters.getValue("result")=='true'){
	// Ext.Msg.alert('提示信息',getDecodeStr(outparamters.getValue("info")));
	// }
	var datastore = Ext.data.StoreManager.lookup('tablegrid_dataStore');
	datastore.reload();
}
//招聘职位进入简历中心，返回方法
Global.back = function(param) {
	parent.Global.reload();
	parent.Ext.getCmp("recommendWinID").close();
}
/** ***推荐职位***** */
Global.recommendOtherPosition = function(object, record, store) {
	if (record.length == 0) {
		Ext.Msg.alert('提示信息', '请选择操作数据！');
		return;
	}
	var searchStr = Global.schemeArr.join(",");
	var a0100s = "{\"a0100\":[";
	var nbase = "";
	Global.selectA0100 = record.length;
	for ( var i = 0; i < record.length; i++) {
		if (!nbase.length > 0)
			nbase = record[i].data.nbase_e;
		a0100s += "{\"a0100\":\"" + record[i].data.a0100_e
				+ "\",\"z0301\":\"" + record[i].data.z0301_e
				+ "\",\"a0101\":\"" + record[i].data.a0101 + "\"},";
	}
	if (a0100s.length > 1)
		a0100s = a0100s.substring(0, a0100s.length - 1)
				+ "],\"nbase\":\"" + nbase + "\"}";

	var pageDescFro = store.currentPage + "`" + searchStr + "`"
			+ store.pageSize;
	var map = new HashMap();
	map.put("a0100s", a0100s);
	map.put("pageDescFro", pageDescFro);
	Rpc( {
		functionId : 'ZP0000002082',
		success : Global.recommendPosition
	}, map);
}
/*******************************************************************************
 * 通过后的人员进行入职
 * 
 * @param {}
 *            outparamters
 */
Global.recommendPosition = function(outparamters) {
	var param = Ext.decode(outparamters.responseText);
	var a0100s = param.a0100s;
	var pageDescFro = param.pageDescFro;
	if (a0100s == "") {
		Ext.Msg.alert('提示信息', '选择的人员处于已终止或已入职状态，<br/>不允许继续推荐！');
		return;
	}
	a0100temp = a0100s;
	//链接可以传的参数有限
	if(Global.selectA0100>1)
		a0100s = "";
	
	Ext.create('Ext.window.Window', {
    	id:'recommendWinID',
        border:false,
        closable : false,
        maximized : true,
        header: false,
        html:"<iframe id='recommendForm' width='100%' frameborder=0 height='100%' src='/recruitment/position/position.do?b_query=link&pageNum=1"
        	+"&pagesize=20&pageDescFro="+$URL.encode(pageDescFro)+"&from="+Global.fromModule+"&a0100s="+$URL.encode(a0100s)+"'></iframe>"
    }).show();
	//推荐职位为了少查一些东西将fromModeule置为resumeInfo，在这边重新设置回原来的值
	Global.fromModule = Global.oldfromModule;
}

/** ***推荐职位***** */
function setDisabled(id, href) {
	if(posWin)
		posWin.close();
	var posWin = Ext.create('Ext.window.Window', {
    	id:'recommendWinID',
        border:false,
        closable : false,
        maximized : true,
        header: false,
        html:"<iframe id='recommendForm' width='100%' frameborder=0 height='100%' src='"+href+"'></iframe>"
    }).show();
}

Global.reload = function(){
	Ext.data.StoreManager.lookup('tablegrid_dataStore').reload();
}
// 填写简历反馈信息
Global.FeedBack = function(object, record, store) {
	if (record.length == 0) {
		Ext.Msg.alert('提示信息', '请选择操作数据！');
		return;
	}
	var userInfos = new Array();
	for ( var i = 0; i < record.length; i++) {
		var userInfo = {
			a0101 : record[i].data.a0101,
			nbase : record[i].data.nbase_e,
			a0100 : record[i].data.a0100_e,
			zp_pos_id : record[i].data.z0301_e,
			link_id : "",
			link_name : ""
		};

		userInfos.push(userInfo);
	}
	FeedBack.write(userInfos, "tablegrid");
}

Global.exportResume = function (object, record, store) {
	Ext.getCmp('resumeWindowId').close();
	Ext.MessageBox.wait("", "正在导出请稍候……");
	var selectstore = Ext.getCmp("tablegrid_tablePanel").getSelectionModel().getSelection();
	var a0100 = "";
	Ext.each(selectstore,function(record,index){
		a0100 += record.data.a0100_e + ",";
	});
	var map = new HashMap();
	map.put("a0100", a0100);
	Rpc( {
		functionId : 'ZP0000002105',
		success : Global.exportResumeZip
	}, map);
}

Global.exportResumeZip = function (param){
	var map = Ext.decode(param.responseText);
	Ext.MessageBox.close();	
	if(map.succeed){
		var infor = map.infor;
		if("ok" == infor) {
			var zipName = map.zipname;
			window.open("/servlet/vfsservlet?fromjavafolder=true&fileid="+zipName+"&openflag=true","_blank");
		} else {
			Ext.showAlert(infor);
		}
	}else{
		if(map.message){
			Ext.showAlert(map.message);
		}
	}
}

Global.importResume = function (obj) {
	Ext.require('SYSF.FileUpLoad', function(){
		var uploadObj = Ext.create("SYSF.FileUpLoad",{
			upLoadType:3,
			fileSizeLimit:'500MB',
			fileExt:"*.zip;",
			buttonText:'',
			renderTo:obj.id,
			success:Global.uploadFile,
			isDelete:true,
			height:22,
            width:38,
            isTempFile:true,
            VfsModules:VfsModulesEnum.ZP,
            VfsFiletype:VfsFiletypeEnum.other,
            VfsCategory:VfsCategoryEnum.other
		});
	});
}

Global.showResumeWindow = function (){
	Ext.create('Ext.window.Window', {
	    title: '数据迁移',
	    id:'resumeWindowId',
	    width:250,
 		height:150,
 		modal:true,
 		layout: 'vbox',
 		padding:0,
	    items: [{
	    	xtype:'container',
	    	margin:'24 0 0 50',
	    	items:[{
		        xtype: 'label',
		        margin: '0 10 0 0',
		        text: '导出简历（zip）',
		    },{
		    	xtype: 'button',
	            text : '导出',
	            handler : Global.exportResume
		    }]
	    },{
	    	xtype:'container',
	    	margin:'10 0 0 50',
	    	items:[{
		        xtype: 'label',
		        text: '导入简历（zip）',
		        margin: '0 10 0 0'
		    },{
		    	xtype: 'button',
		    	padding:0,
		    	height:22,
	            width:38,
	            text : '浏览'
		    },{
	   	   		xtype:'box',
	   	   		border:false,
	   	   		width:38,
	   	   		height:22,
	  	   		margin: '-22 0 0 100',
	  	   		style:{
	  	   			background:'',
	  	   			borderColor:'#c5c5c5',
	  	   			borderStyle:'dashed'
	  	   		},
	  	   		listeners:{
	  	   			render:function(){
	  	   				Global.importResume(this);
	  	   			}
	  	   		}
	        }]
	    }]
	}).show();
}


Global.uploadFile = function (list) {
	if(list.length < 0)
		return;
	Ext.getCmp('resumeWindowId').close();
	Ext.MessageBox.wait("", "正在导入数据");	
	var obj = list[0];
	if(obj){
		var map = new HashMap();
		map.put("fileId",obj.fileid);
		Rpc( {
			functionId : 'ZP0000002106',
			success : Global.uploadSuccess
		}, map);
	}
}

Global.uploadSuccess = function (param){
	var map = Ext.decode(param.responseText);
	Ext.MessageBox.close();
	if(map.succeed){
		var info = map.info;
		var flag = map.flag;
		var filePath = map.filePath;
		if("true" == flag) {
			if(!Ext.isEmpty(info))
				Ext.showAlert(info);
			
			var datastore = Ext.data.StoreManager.lookup('tablegrid_dataStore');
			datastore.reload();
		} else if("error" == flag) {
			if(!Ext.isEmpty(info))
				Ext.showAlert(info);
		} else if("false" == flag) {
			Global.uploadDataAnalyze(info, filePath);
		}
	} else {
		var info = map.info;
		if(!Ext.isEmpty(info))
			Ext.showAlert(info);
	}
}

Global.uploadDataAnalyze = function (msg, filePath) {
	var data = Ext.decode(msg);
	var store = Ext.create('Ext.data.Store', {
	    fields:[ 'information'],
	    data: data
	});

	var gird = Ext.create('Ext.grid.Panel', {
	    store: store,
	    columns: [{ 
	    	text: '提示信息',
	    	dataIndex: 'information',
	    	width: '100%',
	    	height: 0,
	    	menuDisabled:true,
	    	sortable: false
	    }]
	});
	
	var importButton = Ext.create('Ext.Button', {
	    text: '导入',
	    handler: function() {
			Ext.getCmp("infoWindow").close();
			Ext.MessageBox.wait("提示信息", "正在导入数据");	
			var map = new HashMap();
			map.put("importParam", "1");
			map.put("filePath", filePath);
			Rpc( {
				functionId : 'ZP0000002106',
				success : Global.uploadSuccess
			}, map);
			
	    }
	});
	
	var closeButton = Ext.create('Ext.Button', {
	    text: '取消',
	    handler: function() {
			var map = new HashMap();
			map.put("importParam", "2");
			map.put("filePath", filePath);
			Rpc( {
				functionId : 'ZP0000002106',
				success : function (param){}
			}, map);
			
			Ext.getCmp("infoWindow").close();
	    }
	});
	
	Ext.create('Ext.window.Window', {
		title: '提示信息',
		id:'infoWindow',
	    height: 400,
	    width: 600,
	    layout: 'fit',
	    items: [gird],
	    buttonAlign: 'center',
	    buttons: [importButton, closeButton]
	}).show();
}
//保存栏目设置后的回调函数
Global.schemeSave = function(){
	var datastore = Ext.data.StoreManager.lookup('tablegrid_dataStore');
	var current = datastore.currentPage;
	var pagesize = datastore.pageSize;
	var schemeValues = '';
	 for(var i=0; i<Global.schemeArr.length; i++){
	        if(i!=Global.schemeArr.length-1)
	            schemeValues += Global.schemeArr[i] + ",";
	        else
	            schemeValues += Global.schemeArr[i]; 
	    }
	  window.location="/recruitment/resumecenter/searchresumecenter.do?b_search=link&current="+current+"&pagesize="+pagesize+"&schemeValues="+schemeValues+"&from=resumeCenter&back=true";
}

//导出简历PDF
Global.exportResumePDF = function () {
	var record = Ext.getCmp("tablegrid_tablePanel").getSelectionModel().getSelection();
	if (record.length == 0) {
		Ext.Msg.alert('提示信息', '请选择操作数据！');
		return;
	}
	Ext.MessageBox.wait("", "正在导出请稍候……");
	var nbase="", a0100="", a0101s="";
	for (var i = 0; i < record.length; i++) {
		nbase = record[i].data.nbase_e;
		a0100 += record[i].data.a0100_e + ",";
		a0101s += record[i].data.a0101 + ",";
	}
	
	var map = new HashMap();
	map.put("a0100s", a0100);
	map.put("nbase", nbase);
	map.put("a0101s", a0101s);
	Rpc( {
		functionId : 'ZP0000002107',
		success : Global.exportResumeZip
	}, map);
}

//导出简历WORD
Global.exportResumeWORD = function () {
	var record = Ext.getCmp("tablegrid_tablePanel").getSelectionModel().getSelection();
	if (record.length == 0) {
		Ext.Msg.alert('提示信息', '请选择操作数据！');
		return;
	}
	Ext.MessageBox.wait("", "正在导出请稍候……");
	var nbase="", a0100="", a0101s="";
	for (var i = 0; i < record.length; i++) {
		nbase = record[i].data.nbase_e;
		a0100 += record[i].data.a0100_e + ",";
		a0101s += record[i].data.a0101 + ",";
	}
	
	var map = new HashMap();
	map.put("a0100s", a0100);
	map.put("nbase", nbase);
	map.put("a0101s", a0101s);
	map.put("filetype", "word");
	Rpc( {
		functionId : 'ZP0000002107',
		success : Global.exportResumeZip
	}, map);
}

//打印简历
Global.printAX = function(){
	var record = Ext.getCmp("tablegrid_tablePanel").getSelectionModel().getSelection();
	if(!Ext.isIE){
		Ext.Msg.alert('提示信息', '该功能仅支持IE浏览器！');
		return;
	}
	if (record.length == 0) {
		Ext.Msg.alert('提示信息', '请选择操作数据！');
		return;
	}
	var pers=new Array();
	var a0100s="";
	for (var i = 0; i < record.length; i++) {
		var nbase = record[i].data.nbase_e;
		var a0100 = record[i].data.a0100_e;
		a0100s = nbase+"`"+a0100;
		pers[i]=a0100s;
	}
	
	var map = new HashMap();
	map.put("inforkind", "1");
	map.put("pers",pers);
	Rpc( {
		functionId : 'ZP0000002108',
		success : Global.showPrint
	}, map);
}
Global.showPrint = function (outparamters)
{
	var param = Ext.decode(outparamters.responseText);
	if(!param.succeed){
		Ext.Msg.alert('提示信息', param.message);
		return;
	}
	var personlist=param.personlist;
	var nbase = param.nbase;
	var cardid = param.cardid;
	var obj = document.getElementById('CardPreview1');
	if(obj==null)
	{
		Ext.Msg.alert('提示信息', '没有下载打印控件，请设置IE重新下载！');
		return;
	}
	try {
		   Global.initCard();
		   obj.SetCardID(cardid);
		   obj.SetDataFlag("<SUPER_USER>1</SUPER_USER>");
		   obj.SetNBASE(nbase);
		   obj.ClearObjs();   
		   if(personlist!=null&&personlist.length>0)
		   {
		     for(var i=0;i<personlist.length;i++)
		     {
		       obj.AddObjId(personlist[i].dataValue);
		     }
		   }
		   try { obj.SetJSessionId(AxManager.getJSessionId()); } catch(err) {}
	   		obj.ShowCardModal();
	}catch (e) {

	}
}

Global.exportWin = function (){
	var win = Ext.getCmp("exportWinId");
	if(win)
		win.close();
	
	var checkBox = new Ext.form.Checkbox( {
		id : "historyInfo",
		name : "checkbox",
		width: 200,
		labelSeparator : '',
		padding : '0 5 0 16',
		boxLabel : '包含子集历史记录',
		inputValue : 1
	});
	
	var attachment = new Ext.form.Checkbox( {
		id : "attachment",
		name : "checkbox",
		width: 200,
		labelSeparator : '',
		padding : '0 5 0 16',
		boxLabel : '包含简历附件',
		inputValue : 1
	});
	
	var registration = new Ext.form.Checkbox( {
		id : "registration",
		name : "checkbox",
		width: 200,
		labelSeparator : '',
		padding : '0 5 0 16',
		boxLabel : '包含简历登记表',
		inputValue : 1
	});
	
	win = Ext.create('Ext.window.Window', {
	    title: '导出Excel',
	    id:'exportWinId',
	    height: 164,
	    width: 300,
	    padding:10,
	    layout: 'vbox',
	    modal: true,
	    items:[checkBox,attachment,registration],
	    buttonAlign: 'center',
	    buttons:[{
	    	text: '确定',
	    	handler: function() {
	    		Ext.MessageBox.wait("", "正在导出请稍候…");
	    		var historyFlag = "0";
	    		var historyBox = Ext.getCmp("historyInfo");
	    		
	    		
	    		if(historyBox.checked)
	    			historyFlag = "1";
	    		
	    		var attachmentFlag = "0";
	    		var attachmentBox = Ext.getCmp("attachment");
	    		if(attachmentBox.checked)
	    			attachmentFlag = "1";
	    		
	    		var registration = "0";
	    		var registrationBox = Ext.getCmp("registration");
	    		if(registrationBox.checked)
	    			registration = "1";
	    		
	    		var selectstore = Ext.getCmp("tablegrid_tablePanel").getSelectionModel().getSelection();
	    		var a0100_es = "";
	    		var z0301_es = "";
	    		Ext.each(selectstore,function(record,index){
	    			a0100_es+=record.data.a0100_e+",";
	    			z0301_es+=record.data.z0301_e+",";
	    		});
	    		var map = new HashMap();
	    		map.put("historyFlag", historyFlag);
	    		map.put("attachmentFlag", attachmentFlag);
	    		map.put("registration", registration);
	    		map.put("a0100_es",a0100_es);
	    		map.put("z0301_es",z0301_es);
	    		Rpc( {
	    			functionId : 'ZP0000002109',
	    			success : Global.exportSucc
	    		}, map);
	    		
	    		win.close();
		    }
	    },{ 
	    	text: '取消',
	    	handler: function() {
	    		win.close();
	    	}
	    }]
	});
	
	win.show();
}

Global.exportSucc = function (response){
	var value = response.responseText;
	var map	 = Ext.decode(value);
	Ext.MessageBox.close();	
	if(map.succeed){
		if("false" == map.flag)
			return;
		
		var fieldName = getDecodeStr(map.fileName);
		window.location.target="_blank";
		window.location.href = "/servlet/vfsservlet?fromjavafolder=true&fileid="+fieldName;
	}
}

Global.selectField = function(){
	var record = Ext.getCmp("tablegrid_tablePanel").getSelectionModel().getSelection();
	if(record.length == 0){
		Ext.Msg.alert(common.button.promptmessage, "请选择操作数据！");
		return ;
	}
	// 公告维护--选择列表指标
	Ext.require('NoticePath.SelectNoticeField', function() {
		var selectField = Ext.create("NoticePath.SelectNoticeField", {
							title : '选择公示列表指标',
							items : Global.exceptItems,
							flag:true,//招聘公示内容分组指标
							jsonStr:Global.jsonStr,
							callBackFunc : 'Global.getContentHtml'
						});
	});
}

// 公告内容html
Global.getContentHtml = function(selectedItems,groupValue){
	Global.selected = selectedItems;
	var values = new Array();
	Ext.Array.each(selectedItems, function(data) {
        values.push(data.get("dataValue"));
    });
	var selectstore = Ext.getCmp("tablegrid_tablePanel").getSelectionModel().getSelection();
	var a0100_es = new Array();
	Ext.each(selectstore,function(record,index){
		a0100_es.push(record.data.a0100_e);
	});
	var map = new HashMap();
	map.put("selectedItems", values);
	map.put("a0100s", a0100_es);
	map.put("groupValue", groupValue);
	map.put("flag","searchInfo");
	Rpc( {
		functionId : 'ZP0000002113',
		success : Global.createContentHtml
	}, map);
	
}

Global.createContentHtml = function(outparamters){
	var param = Ext.decode(outparamters.responseText);
	if(!param.succeed){
		Ext.showAlert(param.message);
		return;
	}
	
	var searchInfo = param.searchInfo;
	var groupDesc = param.groupDesc;
	var groupKey = param.groupKey;
	var selectedItems = Global.selected;
	var selectData;
	var num = 0;
	var map = new HashMap();
	for(var i = 0;i<groupKey.length;i++){
		var key = groupKey[i];
		if(num==0){
			selectData = searchInfo[key];
		}
		map.put(key, Global.createTable(selectedItems,searchInfo[key]));
		num++;	
	}
	var flag = true;
	if(groupKey.length==0){
		selectData = searchInfo['noFlag'];
		flag = false;
	}
	if(!selectData){
		Ext.showAlert("所选应聘人员未申请职位，无法进行公示！");
		return;
	}
	var contentHtml=Global.createHtml(selectedItems,selectData,flag);
	var obj = new HashMap();
	obj.put("searchInfo", map);
	obj.put("groupDesc", groupDesc);
	obj.put("groupKey", groupKey);
	var notice_name = "招聘人员信息公示";
	if(groupKey.length>0&&groupDesc[groupKey[0]])
		notice_name += "（"+groupDesc[groupKey[0]]+"）";
	// 公告维护
	Ext.require('NoticePath.Notice',function(){
		var notice = Ext.create('NoticePath.Notice', {
			title : '信息公示',
			notice_name : notice_name,
			notice_content : contentHtml,
			notice_time : '5',
			notice_seq : '1',
			notice_object : '',
			notice_select : false,
			height : 465,
			flag : 13,
			isApproved : false,
			groupData : obj
		});
	});
}
//flag 增加替换标识符
Global.createHtml = function(selectedItems,selectData,flag){
	Ext.util.CSS.createStyleSheet(".noBorder div{border-color:#ffffff;}","card_css");
	//border:none 覆盖掉ckeditor 的自带样式
	var tableHtml = Global.createTable(selectedItems,selectData,flag);
	
	/** 公告内容=文字+表格 */
	var d = new Date();
	var contentHtml = '';
	contentHtml+=
		'<div >' +
			'<p>' 
			+'&nbsp; &nbsp; &nbsp; &nbsp;经学院审核，现将'+d.getFullYear()+'年招聘人员公示如下，'
			+'公示期为  年  月  日至  年  月  日，在公示期内如有异议请与人事处联系。（电话：__）<br />'
			+'附：招聘人员情况'+
			
			'</p>' +tableHtml+
			'<p style="text-align:right;">' +
					d.getFullYear()+'年'+(d.getMonth()+1)+'月'+d.getDate()+'日' +
			'</p>' +
		'</div>';
	return contentHtml;
}

Global.createTable = function(selectedItems,selectData,flag){
	var tdStyle = "border:none;text-align:center; height:42px; line-height:24px;border-bottom:1px solid #e5e6e8;"
	var thStyle = 'border:none;background:#f5f5f5;height:42px; line-height:42px;font-family:"微软雅黑"; font-size:14px;color:#666;';
	var lineStyle = 'padding: 0 20px 0;margin: 20px 0;line-height: 1px;border-left: 200px solid #ddd;border-right: 200px solid #ddd;'
		+'text-align: center;';
	var tableHtml = "";
	if(flag)
		tableHtml = "<div id = 'replaceStart' style='"+lineStyle+"'>此行开始往下请勿修改</div>";
	tableHtml+='<div class="hj-wzm-table">';
	tableHtml+='<table style="border:1px solid #e5e6e8;border-bottom:none;" width="100%" border="0" cellpadding="0" cellspacing="0">';
	
	// 表格列头
	tableHtml+='<tr>';
	tableHtml+='<th style="'+thStyle+'"; font-size:14px;color:#666;" scope="col">序号</th>';
	for(var j=0; j<selectedItems.length; j++){
		var text = selectedItems[j].get('dataName');
		tableHtml+='<th style="'+thStyle+'" scope="col">'+text+'</th>';
	}
	tableHtml+='<th style="'+thStyle+'" scope="col">详情</th>';
	tableHtml+='</tr>';
	// 表格数据
	for(var i=0; i<selectData.length; i++){
		tableHtml+='<tr>';
		tableHtml+='<td style="'+tdStyle+'">'+(i+1)+'</td>';
		var data = selectData[i];
		for(var w=0; w<selectedItems.length; w++){
			var itemid = selectedItems[w].get('dataValue');
			var text = data[itemid];
			if(typeof text == 'string' && text.indexOf('`') > -1){
				text = text.split('`')[1];
			}
			tableHtml+='<td style="'+tdStyle+'">'+text+'</td>';
			
		}
		var nbasea0100 = data.nbasea0100_e;
		var nbasea0100_1 = data.nbasea0100_1_e;
		tableHtml+="<td style='"+tdStyle+"'>";//评审材料
		if(data.infoUrl)
			tableHtml+="<a href='"+data.infoUrl+"'  target='_blank'><img src='/images/new_module/icon1.png' /></a>";
		tableHtml+="</td>";
		
		tableHtml+='</tr>';
	}
	tableHtml += '</table>';
	tableHtml += '</div >';
	if(flag)
		tableHtml += "<div id = 'replaceEnd' style='"+lineStyle+"'>此行开始往上请勿修改</div>";
	return tableHtml;
}

//发送通知
Global.sendNotice = function () {
	var record = Ext.getCmp("tablegrid_tablePanel").getSelectionModel().getSelection();
	if (record.length == 0) {
		Ext.Msg.alert('提示信息', '请选择操作数据！');
		return;
	}
	var a0100s="", a0101s="",z0301s="";
	for (var i = 0; i < record.length; i++) {
		a0100s += record[i].data.a0100_e + ",";
		a0101s += record[i].data.a0101 + ",";
		z0301s += record[i].data.z0301_e + ",";
	}
	Ext.require('sendEmailUL', function(){
		Ext.create("sendEmailUL.sendEmail", {
			sub_module:"7",
			nModule:"82",
			z0301:z0301s,
			a0100s:a0100s,
			a0101s:a0101s,
			operation:false,
			title:"发送通知"
		});
	});
}