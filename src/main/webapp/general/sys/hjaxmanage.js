/**
 * ActiveX 插件管理
 * 插件升级后需要改本js中的版本号
 * 
 */
var AxManager = {
    hrmsAXProjCls: "657DF951-2DED-4E5D-868A-66F6FA92BB92",
    hrmsAXProjVersion: "2,1,29,2",  // ocx版本号
    hrmsAXProjFile: "/cs_deploy/hrmsAXProj.cab",
    
    // 基础组件
    hjbasepkgName: "hjax.msi",
    
    // 后台业务
    busiAXpkgName: "busiAXpkg.msi",
    busiAxSrvPkgVersionParam: function(){return "srvpkgversion<"+_AxPkgs[this.busiAXpkgName].version+">";},
    busiAxLowestVersionParam: function(){return "lowestversion<"+_AxPkgs[this.busiAXpkgName].lowestVersion+">";},
    
    // 表格工具
    hjdesignerpkgName: "hjdesignerpkg.msi",
    hjdesignerSrvPkgVersionParam: function(){return "hjdesignerpkg_srvpkgversion<"+_AxPkgs[this.hjdesignerpkgName].version+">";},
    hjdesignerLowestVersionParam: function(){return "hjdesignerpkg_lowestversion<"+_AxPkgs[this.hjdesignerpkgName].lowestVersion+">";},
    
    // 登记表打印预演
    cardpkgName: "hjdesignerpkg.msi",  // this.hjdesignerpkgName未定义
    
    // 高级花名册打印预演
    musterpkgName: "hjdesignerpkg.msi",
    
    // 业务模板打印预演
    tmplpkgName: "hjdesignerpkg.msi",

    // 统计表打印预演
    tjbpkgName: "hjdesignerpkg.msi",
    
    //ie设置
    setIEName: "hrpsetiepkg.msi",
    
    workFlowPkgName: "HJWorkFlow.msi",
    kqmachPkgName: "kqmach.msi",
    omrreaderPkgName: "omrreader.msi",
    salarychartviewxPkgName: "salarychartviewx.msi",
    
    _isIE: function(){
    	if(navigator.cpuClass == "x64")
			return false;
	    var userAgent = navigator.userAgent.toLowerCase(); 
	    if(userAgent.indexOf("msie") >= 0 || userAgent.indexOf("trident") >= 0){
	         return true;
	    }
	    return false;    
    },
    
    _pkgName: "",   // 当前包名
    _timer: null,
    _callback: null,  // 重试回调函数
    _onFail: null,    // 安装失败回调函数
    _axid: "",
    _axObject: null
};

var _AxPkgs = {
	"hjax.msi": {
		clsid: "657DF951-2DED-4E5D-868A-66F6FA92BB92",
		version: "7.0.29",  // msi版本号
		lowestVersion: "65535",
		productCode: "{EF39CA26-7CA0-42F1-8571-422B4F39B15E}",  // 升级后会变化
		upgradeCode: "{50A18757-4F02-405B-989E-FEE5919AC3EB}"	// 保持不变	
	},
    "busiAXpkg.msi": { 
    	version: "7.0.76",
		lowestVersion: "65535",
		productCode: "{88B17023-FA53-4273-9BBB-59B4E62E668B}", // 升级后会变化
		upgradeCode: "{157F2829-9835-4A38-82B5-1D6F40A018E6}"		// 保持不变
    },
    "hjdesignerpkg.msi": {
    	version: "7.1.10",
		lowestVersion: "65535",
		productCode: "{4A6A1448-61D2-4E36-8562-21C47EF66FAA}",
		upgradeCode: "{6DD05539-A9DD-4C3D-85C8-DFC757A1B0FF}"		
    },
    "hrpsetiepkg.msi": {
    	version: "7.0.16",
		lowestVersion: "65535",
		productCode: "{0452C67A-B0C5-4CA1-94D0-C08B5925F87B}",
		upgradeCode: "{1B613260-1FB0-485C-A8E9-F8BF1B89056D}"		
    },
    "HJWorkFlow.msi": {
    	version: "7.1.3",
		lowestVersion: "65535",
		productCode: "{1B259101-785A-4726-936F-CBAEC6B8B741}",
		upgradeCode: "{025C2F87-9A61-430D-B58C-4953850107C4}"		
    },
    "kqmach.msi": {
    	clsid: "2A1583F4-8310-46CD-83F4-0A19AA21B0A3",
    	version: "7.0.8",
		lowestVersion: "65535",
		productCode: "{AE14E100-E9BE-4872-9B01-0C7851009630}",
		upgradeCode: "{A249EF01-F483-4348-B61A-C3F628BE2D16}"		
    },
    "omrreader.msi": {
    	clsid: "A593E0E4-4C29-45C9-B504-CB78C6E585B3",
    	version: "7.0.7", 
		lowestVersion: "65535",
		productCode: "{922228A2-0AE3-4AAF-A84F-B2D722834D0D}",
		upgradeCode: "{544FEEB0-87F4-475F-8B69-561F85117029}"		
    },
    "salarychartviewx.msi": {
    	clsid: "10C81882-13C9-43E2-AF35-2FE0279CBBDA",
    	version: "7.1.0",
		lowestVersion: "65535",
		productCode: "{8205684E-18B1-4C66-97A2-B0A47346779B}",
		upgradeCode: "{C5E88F23-BE01-4107-B4B2-5781739FBC2D}"		
    },
    "orcl11.msi": {
    	version: "7.0.10",
		lowestVersion: "65535",
		productCode: "{9320C2D6-6E14-445F-867D-41FD215C74E2}",
		upgradeCode: "{DB62DE5B-9847-4532-B736-F6344C524772}"		
    } 	
};

var MSI_STATE_NONE = 0;
var MSI_STATE_INSTALLED = 1;
var MSI_STATE_NEEDUPDATE = 2;

AxManager._isHrmsAXProjOcx = function(pkgName) {
	return !(pkgName == "kqmach.msi" || pkgName == "omrreader.msi" || pkgName == "salarychartviewx.msi");
};

AxManager._getClsId = function(pkgName) {
	return this._isHrmsAXProjOcx(pkgName) ? this.hrmsAXProjCls : _AxPkgs[pkgName].clsid;
};

/**
 * 加载插件
 * pkgName: 包名
 * autoInstall: 是否指定插件codebase
 * @see #write()
 */
AxManager.setup = function(container, axid, width, height, callback, pkgName, onFail, autoInstall, url){
    if(!this._isIE())
        return false;
    if(this._timer !== null && typeof this._timer != "undefined")
        return false;        
    if(!pkgName)
    	pkgName = "hjax.msi";
	if(!this._isHrmsAXProjOcx(pkgName)){
		if(!this._setupPkg(pkgName, url))
			return false;
    }
    var containerObj = null;
    if(!container){
	    container = "_ax_"+axid;
	    if(!document.getElementById(container)){
			var div = document.createElement("div");
			div.id = container;
			div.style.display="none";
			document.body.appendChild(div);
		}
    }
    if (typeof container == "string")
        containerObj = document.getElementById(container);
    else
        containerObj = container;
    if(!containerObj)
        return false;
    if(typeof autoInstall == 'undefined')
    	autoInstall = true;
    containerObj.innerHTML = this._getObjHtml(pkgName, axid, width, height, autoInstall);
    this._pkgName = pkgName;
    this._callback = callback;
    this._onFail = onFail;
    this._axid = axid;
    this._axObject = document.getElementById(axid);
    if(!this._axLoaded()){
    	if(!autoInstall){
    		if(this._onFail)
    			this._onFail();
    		return false;
    	}
        this._waitForLoading();
        if(!this._axLoaded())
            return false;
    }
    return true;
};

/**
 * 加载插件
 * 使用setup时，有插件界面显示有问题，用write()可以解决。
 * @see #setup()
 */
AxManager.write = function(axid, width, height, pkgName, url){
    if(!this._isIE())
        return false;
    if(!pkgName)
    	pkgName = "hjax.msi";
	if(!this._isHrmsAXProjOcx(pkgName)){
		if(!this._setupPkg(pkgName, url))
			return false;
    }

	try{
	    document.writeln("");
	}catch(e){
		// IE7/8, 薪资分析图首次安装后报错: 由于出现错误 800a03e8 而导致此项操作无法完成。
		window.location.href = window.location.href;
		return false;
	}	
	document.writeln(this._getObjHtml(pkgName, axid, width, height, true));
    this._pkgName = pkgName;
    this._callback = null;
    this._onFail = null;
    this._axid = axid;
    this._axObject = null;
    return true;
};

/**
 * 加载登记表打印插件
 * 兼容旧代码
 * @see #write()
 */
AxManager.writeCard = function(axid){
    if(!this._isIE())
        return false;
	if(!axid)
		axid = "CardPreview1";
	var pkgName = this.cardpkgName;
	document.writeln(this._getObjHtml(pkgName, axid, 0, 0, true));
    this._pkgName = pkgName;
    this._callback = null;
    this._onFail = null;
    this._axid = axid;
    this._axObject = null;
    return true;
};

AxManager._getObjHtml = function(pkgName, axid, width, height, includeCodebase){
	var codebase = "";
	if(includeCodebase && this._isHrmsAXProjOcx(pkgName))
	    codebase = "codebase='"+this.hrmsAXProjFile+"#version="+this.hrmsAXProjVersion+"'";
	var s = "<OBJECT id='"+axid+"' classid='clsid:"+this._getClsId(pkgName)+"' "+codebase+
            " width='"+width+"' height='"+height+"'"+
            " align='center' hspace='0' vspace='0'>"+
            " <PARAM NAME='PkgName' VALUE='"+pkgName+"'/>"+
            " <PARAM NAME='SrvVersion' VALUE='"+_AxPkgs[pkgName].version+"'/>"+
            " <PARAM NAME='LowestVersion' VALUE='"+_AxPkgs[pkgName].lowestVersion+"'/>"+
            " <PARAM NAME='ProductCode' VALUE='"+_AxPkgs[pkgName].productCode+"'/>"+
            " <PARAM NAME='UpgradeCode' VALUE='"+_AxPkgs[pkgName].upgradeCode+"'/>"+    
            "</OBJECT>";
    return s;
};
                 
/**
 * 通过hrmsAXProj.ocx安装或升级ocx(通过msi包)
 */
AxManager._setupPkg = function(pkgName, url){
	try{
		if(!this.setup(null, "__axbase", 0, 0, null, null, null, false))
			return false;
			
		var state = this._axObject.GetInstallState(_AxPkgs[pkgName].productCode,_AxPkgs[pkgName].upgradeCode,_AxPkgs[pkgName].version);
		if(state == MSI_STATE_INSTALLED)
			return true;	

		this._axObject.SetURL(url);
		this._setPkgParams(pkgName);
		this._axObject.SetupPkg();
		
		var state = this._axObject.GetInstallState(_AxPkgs[pkgName].productCode,_AxPkgs[pkgName].upgradeCode,_AxPkgs[pkgName].version);
		if(state == MSI_STATE_INSTALLED)
			return true;	
		return false;
	}catch(e){
		return false;
	}finally{
		this._destroyAx();
	}	
};

AxManager._setPkgParams = function(pkgName) {
	if(this._axLoaded()){
    	this._axObject.PkgName = pkgName;
    	this._axObject.SrvVersion = _AxPkgs[pkgName].version;
    	this._axObject.LowestVersion = _AxPkgs[pkgName].lowestVersion;
    	this._axObject.ProductCode = _AxPkgs[pkgName].productCode;
    	this._axObject.UpgradeCode = _AxPkgs[pkgName].upgradeCode;
	}
}

/**
 * 等待插件首次下载并安装
 */
AxManager._waitForLoading = function(){
    if(!this._callback){
        alert("应用组件加载失败，请重试。");
        return;
    }
    var trycnt = 0;
    var _this = this;
    this._timer = setInterval(function(){
    	// 外网招聘初次安装完表格工具msi后，会导致_this._timer无效，造成无法关闭Interval，原因不明。
    	if(_this._timer === null || typeof _this._timer == "undefined") // 已无效
    		return;
        trycnt++;
        _this._axObject = document.getElementById(_this._axid);  
        if(_this._axLoaded()){
            _this._timer = clearInterval(_this._timer);
            if(_this._callback){
                _this._callback();
                _this._callback = null;  // 只调用一次
            }
        }
        if(trycnt == 2){
            _this._timer = clearInterval(_this._timer);
            if(_this._onFail){
            	_this._onFail();
            	_this._onFail = null;
            }else
	            alert("应用组件加载失败，请重试。");
        }
    }, 2000);
};

/**
 * 检查并提示下载安装插件；
 * 检查并自动设置IE：IE信任站点、兼容性视图、允许弹出窗口、加载页面最新版本等设置。
 * 
 * @param domain 服务器ip或域名，如：http://127.0.0.1:8080
 */
var _axdomain = null; 
AxManager.checkBrowserSettings = function(domain, axid) {
    if(!this._isIE())
        return true;
    if(domain){
    	domain = "<URL>"+domain+"</URL><HjaxProductCode>"+_AxPkgs[this.hjbasepkgName].productCode+"</HjaxProductCode>"+
    	         "<HjaxUpgradeCode>"+_AxPkgs[this.hjbasepkgName].upgradeCode+"</HjaxUpgradeCode>"+
    	         "<HjaxVersion>"+_AxPkgs[this.hjbasepkgName].version+"</HjaxVersion>";
    	_axdomain = domain;
    }else
    	domain = _axdomain;  // 回调时domain未定义
    if(!domain)
    	return false;
    // WScript.Shell属于未标记为可安全执行脚本的ActiveX控件，应避免使用	
/*    if(!this._isProgInstalled("WScript.Shell")){  // 全部插件被禁用
        this._showDownloadDialog();  
        //alert("为确保您的最佳使用体验，请先启用ActiveX插件。");
        return;
    }*/
    /*if(!this._isProgInstalled("hrmsAXProj.hrmsAX", true)){  // 文件占用后就不能更新
        this._showDownloadDialog();
        return;
    }*/

	if(axid){
		this._axObject = document.getElementById(axid);
		if(!this._axLoaded()){
			this._showDownloadDialog();
			return false;
		}
		try{
	        this._setPkgParams(this.setIEName);
		    this._axObject.CheckBrowserSettings(domain);
		    return true;
		}catch(e){
			//this._showDownloadDialog();
			return false;
		}		
	}
	else{
	    axid = "__ax__";
	    var target = "__axcontainer__";
	    if(!document.getElementById(target)){
			var div = document.createElement("div");
			div.id = target;
			div.style.display="none";
			document.body.appendChild(div);
		}
	/*  // 去掉hrmsAXProj.cab方式, 问题：ocx文件占用后就不能更新  
		try{
			var callback = function(){};
			if(!this.setup(target, axid, 0, 0, callback, null, this._showDownloadDialog, false)){
				this._showDownloadDialog();
				return false;
			}
			
			this._axObject.SetURL(domain);
			this._setPkgParams(this.hjbasepkgName);
			this._axObject.SetupPkg();
	
			this._setPkgParams(this.setIEName);
			this._axObject.CheckBrowserSettings(domain);
		    return true;
		}catch(e){
			this._showDownloadDialog();
		}finally{
			this._destroyAx();
		}*/
	
	/*	var obj = new ActiveXObject("hrmsAXProj.hrmsAX");
		obj.SetPkgParams(this.setIEName, _AxPkgs[this.setIEName].version, _AxPkgs[this.setIEName].lowestVersion);
		obj.CheckBrowserSettings(domain);
		destroy(obj);
	*/
		
	    if(!this.setup(target, axid, 0, 0, this.checkBrowserSettings, this.setIEName, this._showDownloadDialog))
	        return false;
	    try{
		    this._axObject.CheckBrowserSettings(domain);
		    return true;
		}catch(e){
			//this._showDownloadDialog();
			return false;
		}finally{
			this._destroyAx();
		}
	}
};

var _downloadHjaxDlgId = "__downloadHjaxDlg__";
var _HjaxTitle = "基础组件";

AxManager._hideDownloadDialog = function(){
    var dlg = document.getElementById(_downloadHjaxDlgId);
    if(dlg)
	    dlg.parentNode.removeChild(dlg);
};

AxManager.showDownloadDialog = function(){
	this._showDownloadDialog();
};

AxManager._showDownloadDialog = function(){
	if(!document.getElementsByTagName("body"))
		return;

    var body = document.getElementsByTagName("body")[0];
	var div = document.getElementById(_downloadHjaxDlgId);
	if(div) 
		return;
	div = document.createElement("div");
	div.setAttribute("id", _downloadHjaxDlgId);
	var pkgName = 'hjax.msi';
	var s = '';
//	if(!this._isProgInstalled("WScript.Shell"))
//		s += '浏览器插件尚未启用，';
	s += '为确保您的最佳使用体验，请先下载【'+_HjaxTitle+'】并安装(如果已安装请进行修复)，成功后需要重启浏览器，谢谢！';	
	var html = '';
	html += '<table id="light" style="display: block;  position: absolute;  top: 30%;  left: 35%;  width: 400px;  height: 200px; ';
	html += ' padding: 10px;  border: 5px double #DDDDDD;  background-color: white;  z-index:1002;  overflow: auto;">';
	html += '<tr><td colspan="2" align="right"><a style="margin-top:25px;" href="javascript:void(0)" onclick="AxManager._hideDownloadDialog()">';
	html += '<img src="/images/del.gif" alt="关闭" border="0" align="middle"></a></td></tr>';
	html += '<tr><td style="padding-right:10px;"><img width="36px;" height="36px;" src="/images/rzfs.gif" alt="pic"></td>';
	html += '<td><span style="margin-bottom: 10px;">尊敬的用户：<span></br>';
	html += s + '</td></tr>';
	html += '<tr><td colspan="2" align="center"><input  style="margin-top:10px;width:120px;height:25px;border:1px solid #C6C5C4;';
	html += 'background-color:#FBBE55" onclick="AxManager.downloadSetupPackage(\''+pkgName+'\')" type="button" value="下载'+_HjaxTitle+'"/></td></tr>';
	html += '</table>';
	html += '<div id="fade" style="display: block;  position: absolute;  top: 0%;  left: 0%;  width: 100%;  height: 100%; ';
	html += ' background-color: #DDDDDD;  z-index:1001;  opacity:0.1; -moz-opacity: 0.1;  filter: alpha(opacity=80);">';
	html += '<!--[if IE 6]><iframe style="position:absolute;width:100%; height:100%;"></iframe><![endif]-->';
	html += '</div>';
	div.innerHTML = html;
	document.body.appendChild(div); 
};

AxManager._axLoaded = function() {
    return this._axObject && this._axObject.object;
};

AxManager._destroyAx = function(obj){
	if(!obj)
		obj = this._axObject;
	if(obj){
		try{
			obj.parentNode.removeChild(obj);
		}catch(e){
		}

		try{
			obj.free;
			obj = null;
		}catch(e){
		}
	}
};

/*function destroy(excel){
	try{
		delete excel;
		excel.free;
		excel = null; 
	}catch(e){
	}
}*/

/**
 * 插件安装包数量
 * 用于资源下载-应用组件
 */
AxManager.getAxSetupCount = function() {
    return 7;
};

/** 返回插件标题
 * pans: 插件索引
 */
AxManager.getTitle = function(pans){
    if(pans==1)                 return "基础组件"
    else if(pans==2)            return "表格工具"
    else if(pans==3)            return "后台业务"
    else if(pans==4)            return "流程设计器"
    else if(pans==5)            return "考勤机接口"
    else if(pans==6)            return "扫描及机读接口"
    else if(pans==7)            return "薪资分析图"
};

//返回插件注释
AxManager.getNote = function(pans){
    if(pans==1)                 return "基础功能，设置浏览器，读取本地IP等。"
    else if(pans==2)            return "登记表、高级花名册、报表、模板的设计及打印。"
    else if(pans==3)            return "包含员工管理表格录入、实施工具等。需要直连数据库，稳定的网络连接。"
    else if(pans==4)            return "业务流程设计工具。"
    else if(pans==5)            return "用于接收考勤机刷卡数据、设置考勤机时钟。"
    else if(pans==6)            return "通过扫描仪采集测评表数据，支持光标阅读机采集机读卡测评数据，提供生成Excel格式测评表功能。"
    else if(pans==7)            return "用于制作、生成薪资分析图表，数据来源包括人员信息数据、薪资历史数据、外部数据。"
};
//获取插件安装包名
AxManager.getSetupName = function(pans){
    if(pans==1)                 return this.hjbasepkgName;
    else if(pans==2)            return this.hjdesignerpkgName;
    else if(pans==3)            return this.busiAXpkgName;
    else if(pans==4)            return this.workFlowPkgName;
    else if(pans==5)            return this.kqmachPkgName;
    else if(pans==6)            return this.omrreaderPkgName;
    else if(pans==7)            return this.salarychartviewxPkgName;
};

/*AxManager.getAXProgState = function(axName){
    if (axName == this.hjbasepkgName){
        if(this._isProgInstalled("hrmsAXProj.hrmsAX", true)){
            return "（已安装）";
        }else{
            return "（未安装）";
        }
    }
    else {
        if(this.isPkgInstalled(axName)){
            return "（已安装）";
        }else{
            return "（未安装）";
        }
    }
};*/

AxManager._isProgInstalled = function(pid, checkReg){
	if(checkReg){
	    try {  
	        var obj = new ActiveXObject("WScript.Shell");  
	        if(typeof obj != 'undefined'){
	        	var val = obj.RegRead("HKEY_CLASSES_ROOT\\"+pid+"\\");
	        	if(val)
		            return true; 
		        else
		        	return false;
	        }else{
	            return false;
	        }  
	    } catch (e) { 
	        return false;  
	    }
	}else{
	    try {  
	        var obj = new ActiveXObject(pid);  
	        if(typeof obj != 'undefined'){ 
	            return true; 
	        }else{
	            return false;
	        }  
	    } catch (e) { 
	        return false;  
	    }
	} 
};

//var _pkgRegPath = "HKEY_LOCAL_MACHINE\\Software\\北京世纪软件股份有限公司\\";// HKEY_CURRENT_USER

//AxManager.isPkgInstalled = function(pkgName){
/*    try {  
        var obj = new ActiveXObject("WScript.Shell");  
        if(typeof obj != 'undefined'){
        	var val = obj.RegRead(_pkgRegPath+pkgName);
        	if(val)
	            return true; 
	        else
	        	return false;
        }else{
            return false;
        }  
    } catch (e) { 
        return false;  
    }*/
/*	try{
		if(!this.setup(null, "__axbase", 0, 0, null, null, null, false))
			return false;
		var state = this._axObject.GetInstallState(_AxPkgs[pkgName].productCode, _AxPkgs[pkgName].upgradeCode,
		                                           _AxPkgs[pkgName].version);
		return state != 0;
	}catch(e){
		return false;
	}finally{
		this._destroyAx();
	}	
}*/

AxManager.getPkgInstallState = function(pkgName){
	try{
		if(!this.setup(null, "__axbase", 0, 0, null, null, null, false))
			return MSI_STATE_NONE;
		var state = this._axObject.GetInstallState(_AxPkgs[pkgName].productCode, _AxPkgs[pkgName].upgradeCode,
		                                           _AxPkgs[pkgName].version);
		return state;
	}catch(e){
		return MSI_STATE_NONE;
	}finally{
		this._destroyAx();
	}	
}

//AxManager.pkgNeedUpdate = function(pkgName){
/*    try {  
        var obj = new ActiveXObject("WScript.Shell");  
        if(typeof obj != 'undefined'){
        	var val = obj.RegRead(_pkgRegPath+pkgName);
        	if(val){
        		if(this._pkgHasNewVer(_AxPkgs[pkgName].version, val))
        			return true;
        		else
        			return false; 
	        }else
	        	return false;
        }else{
            return false;
        }  
        
    } catch (e) { 
        return false;  
    }*/
/*	try{
		if(!this.setup(null, "__axbase", 0, 0, null, null, null, false))
			return false;
		var state = this._axObject.GetInstallState(_AxPkgs[pkgName].productCode, _AxPkgs[pkgName].upgradeCode,
		                                           _AxPkgs[pkgName].version);
		return state == 2;
	}catch(e){
		return false;
	}finally{
		this._destroyAx();
	}    
}*/	   

/*AxManager._pkgHasNewVer = function(serverVer, localVer){
	var serverVers = serverVer.split(".");
	while(serverVers.length < 4){
		serverVers.push("0");
	}
	var localVers = localVer.split(".");
	while(localVers.length < 4){
		localVers.push("0");
	}
	for(var i=0;i<4;i++){
		if(parseInt(serverVers[i], 10)>parseInt(localVers[i], 10))
			return true;
		else if(parseInt(serverVers[i], 10)<parseInt(localVers[i], 10))
			return false;
	}
	return false;
}*/

AxManager.downloadSetupPackage = function(axName){
    var url = "/cs_deploy/" + axName;
    return this._downloadFile(url);
};

AxManager._downloadFile = function(fileurl){
	try{
	    var xmlhttp=new ActiveXObject("Microsoft.XMLHTTP");
	    xmlhttp.open("GET",fileurl,false);
	    xmlhttp.send();
	    if(xmlhttp.readyState==4){
	        if(xmlhttp.status==200||xmlhttp.status==0){ 
	            window.location.href = fileurl;
	            return true; //存在
	        }else if(xmlhttp.status==404){ 
	            alert("资源文件未找到");
	            return false; //不存在 
	        }else{ 
	            return true; //其他状态 
	        }
	    }
	}catch(e){
		// 插件禁用或连接失败
		//if(fileurl.indexOf("hjax.msi") != -1)
		//	var win=open("/servlet/DisplayOleContent?filename=FP_AX_CAB_INSTALLER.text&fromflag=hjax", "");
		alert("下载失败。\n"+e.message);
	} 
};

AxManager._getCookie= function(cookieName){
    var arr,reg=new RegExp("(^| )"+cookieName+"=([^;]*)(;|$)");
    if(arr=document.cookie.match(reg))
      return unescape(arr[2]);
    else
      return null;
};

AxManager.getJSessionId = function() {
    return  this._getCookie("JSESSIONID");
};
