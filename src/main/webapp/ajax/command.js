/*************************************************************************
 *command
 *
 *************************************************************************/
 /****************************
 *根据不同的浏览器取得相对应XMLHttpRequest
 ****************************/
function getXMLHttpRequestByBrowser()
{
   var req;
   if (window.XMLHttpRequest) 
   {
       req = new XMLHttpRequest();
   } 
   else if (window.ActiveXObject) 
   {
       req = new ActiveXObject("Microsoft.XMLHTTP");
   }
   return req
}
/******************************************************
 *xml解释器
 ******************************************************/

function XmlDocument() {}
XmlDocument.create = function () {
	try {
	

			
		if (document.implementation && document.implementation.createDocument) {
			var doc = document.implementation.createDocument("", "", null);
			
			if (doc.readyState == null) {
				doc.readyState = 1;
				doc.addEventListener("load", function () {
					doc.readyState = 4;
					if (typeof doc.onreadystatechange == "function")
						doc.onreadystatechange();
				}, false);
			}else if(doc.readyState=='uninitialized'){
				if (window.ActiveXObject)
					return new ActiveXObject("Msxml.DomDocument");
			}
			 
			return doc;
		}	else if (window.ActiveXObject)
			return new ActiveXObject("Msxml.DomDocument");
	}
	catch (ex) {}
	throw new Error("Your browser does not support XmlDocument objects");
};

if (window.DOMParser &&
	window.XMLSerializer &&
	window.Node && Node.prototype && Node.prototype.__defineGetter__) {
	Document.prototype.loadXML = function (s) {
		
		var doc2 = (new DOMParser()).parseFromString(s, "text/xml");
		
		while (this.hasChildNodes())
			this.removeChild(this.lastChild);
		for (var i = 0; i < doc2.childNodes.length; i++) {
			this.appendChild(this.importNode(doc2.childNodes[i], true));
		}
	};
	
	Document.prototype.__defineGetter__("xml", function () {
		return (new XMLSerializer()).serializeToString(this);
	});
}

function __remoteCall(action, type, xml, showAgent) 
{
	var result; 
	if (showAgent) 
	{
		var rpcInfo = new Object();
		rpcInfo.action = action;
		rpcInfo.type = type;
		rpcInfo.xml = xml;
		rpcInfo.viewInstanceId = __viewInstanceId;
		result = showModalDialog(_application_root + "/ajax/showRPCAgent.html", rpcInfo,
			"dialogHeight: 60px; dialogWidth: 260px; center: Yes; help: No; resizable: yes; status: No");
	}
	else 
	{
		xml=encodeURIComponent(xml); 
		var qs = "__rpc=true&__type=" + type + "&__xml=" + xml + "&__viewInstanceId=" + __viewInstanceId;
 
		var XMLHTTP =getXMLHttpRequestByBrowser(); /*new ActiveXObject("Microsoft.XMLHTTP")*/;
		XMLHTTP.open("POST", action, false);
    	XMLHTTP.setRequestHeader("Content-Type","application/x-www-form-urlencoded");

        XMLHTTP.setRequestHeader("If-Modified-Since","0");
	    XMLHTTP.setRequestHeader("Cache-Control","no-cache");  
	    /*
    	if(webserver==2)//websphere6.0
    	{
    		 qs=encodeURI(qs);
	 		 qs=encodeURI(qs);  
    	}
    	*/
		XMLHTTP.send(qs);
		result = XMLHTTP.responseText;
	}
	/*
	var XMLDoc=new ActiveXObject("Msxml.DOMDocument");
	if (XMLDoc.loadXML(result)) 
	{
		return XMLDoc;
	}
	else 
	{
		return null;
	}
	*/
	/**其它浏览器中xml文档解释器*/
	if (window.DOMParser) { // all browsers, except IE before version 9  xuj 2013-10-25 add 解决ie10解析xml
                    var parser = new DOMParser();
                    try {
                        var XmlItem = parser.parseFromString (result, "text/xml");
                        return XmlItem;
                    } catch (e) {
                            // if text is not well-formed, 
                            // it raises an exception in IE from version 9
                    };
    }else{
    	var XMLDoc = XmlDocument.create();
    	XMLDoc.async=false;
    	if(XMLDoc.loadXML(result))
    	{
    		return XMLDoc;
    	}
    	else
    		return null;
    }
}



//-------------------------------------------------------

Date.prototype.toString = function() 
{
	return this.getTime().toString();
}

//-------------------------------------------------------

// Command
function Command(id) {
	this._id = id;
}

Command.prototype._fireBeforeExecute = function() 
{
	var eventName = this._id + "_beforeExecute";
	if (isUserEventDefined(eventName))
	{
		var result=fireUserEvent(eventName, [this]);
		if (result) throw result;
	}
}

Command.prototype._fireOnSuccess = function() {
	var eventName = this._id + "_onSuccess";

	if (isUserEventDefined(eventName)){
		return fireUserEvent(eventName, [this]);
	}
	else {
		return true;
	}
}

Command.prototype._fireOnFailure = function() {
	var eventName = this._id + "_onFailure";
	if (isUserEventDefined(eventName)){
		return fireUserEvent(eventName, [this]);
	}
	else {
		return true;
	}
}

Command.prototype.execute = function() {}

Command.prototype.getId = function() {
	return this._id;
}

//-------------------------------------------------------

// CommandQueue
function CommandQueue(id) 
{
	this._id = id;
	this._queue = new Array();
}

CommandQueue.prototype = new Command();

// Methods
CommandQueue.prototype.addCommand = function(command) 
{
	var queue = this._queue;
	queue[queue.length] = command;
}

CommandQueue.prototype.execute = function() 
{
	try 
	{
		this._fireBeforeExecute();

		var queue = this._queue;
		for (var i=0; i<queue.length; i++) 
		{
			var command = queue[i];
			command.execute();
		}

		this._fireOnSuccess();
	}
	catch(e) 
	{
		if (this._fireOnFailure()) 
		{
			processException(e);
		}
	}
}

//-------------------------------------------------------

// Constants
var __Frame_SELF = "_self";
var __Frame_BLANK = "_blank";
var __Frame_PARENT = "_parent";
var __Frame_TOP = "_top";
var __Frame_MODAL_DIALOG = "_modal_dialog";
var __Frame_MODELESS_DIALOG = "_modeless_dialog";

// __Frame
function __Frame() 
{
	this._Target = __Frame_SELF;
	this._left = 0;
	this._top = 0;
	this._width = 0;
	this._height = 0;
	this._center = true;
	this._resizable = false;
	this._statusbar = false;
}

__Frame.prototype.getTarget = function() 
{
	return this._Target;
}

__Frame.prototype.setTarget = function(Target) 
{
	this._Target = Target;
}

__Frame.prototype.getLeft = function() {
	return this._left;
}

__Frame.prototype.setLeft = function(left) 
{
	this._left = left;
}

__Frame.prototype.getTop = function() 
{
	return this._top;
}

__Frame.prototype.setTop = function(top) 
{
	this._top = top;
}

__Frame.prototype.getWidth = function() {
	return this._width;
}

__Frame.prototype.setWidth = function(width) {
	this._width = width;
}

__Frame.prototype.getHeight = function() {
	return this._height;
}

__Frame.prototype.setHeight = function(height) {
	this._height = height;
}

__Frame.prototype.isCenter = function() {
	return this._center;
}

__Frame.prototype.setCenter = function(center) {
	this._center = center;
}

__Frame.prototype.isResizable = function() {
	return this._resizable;
}

__Frame.prototype.setResizable = function(resizable) {
	this._resizable = resizable;
}

__Frame.prototype.isStatusbar = function() {
	return this._statusbar;
}

__Frame.prototype.setStatusbar = function(statusbar) {
	this._statusbar = statusbar;
}

function __open(path, frame) {
	var top = frame.getTop();
	var left = frame.getLeft();
	var width = frame.getWidth();
	var height = frame.getHeight();

	if (frame.isCenter()) {
		left = (window.screen.width - width) / 2;
		top = (window.screen.height - height) / 2;
	}

	var features = "";
	features += "resizable=" + frame.isResizable() + ",";
	features += "status=" + frame.isStatusbar() + ",";

	if (left > 0) {
		features += "left=" + left + ",";
	}

	if (top > 0) {
		features += "top=" + top + ",";
	}

	if (width > 0) {
		features += "width=" + width + ",";
	}

	if (height > 0) {
		features += "height=" + height + ",";
	}

	window.open(path, frame.getTarget(), features);
}

function __openDialog(path, frame, arg) {
	var features = "";
	features += "resizable:" + ((frame.isResizable())?"1":"0") + ";";
	features += "status:" + ((frame.isStatusbar())?"1":"0") + ";";
	features += "center:" + ((frame.isCenter())?"1":"0") + ";";

	var top = frame.getTop();
	var left = frame.getLeft();
	var width = frame.getWidth();
	var height = frame.getHeight();

	if (left > 0) {
		features += "dialogLeft:" + left + "px;";
	}

	if (top > 0) {
		features += "dialogTop:" + top + "px;";
	}

	if (width > 0) {
		features += "dialogWidth:" + width + "px;";
	}

	if (height > 0) {
		features += "dialogHeight:" + height + "px;";
	}

	if (frame.getTarget()=="__Frame_MODAL_DIALOG"){
		return window.showModalDialog(path, arg, features);
	}
	else {
		window.showModelessDialog(path, arg, features);
		return true;
	}
}

//-------------------------------------------------------

// RequestCommand
function RequestCommand(id) {
	this._id = id;
	this._path = null;
	this._dataset = null;
	this._paramFields = null;
	this._frame = new __Frame();
}

RequestCommand.prototype = new Command();

RequestCommand.prototype.getPath = function() {
	return this._path;
}

RequestCommand.prototype.setPath = function(path) {
	this._path = path;
}

RequestCommand.prototype.getDataset = function() {
	return this._dataset;
}

RequestCommand.prototype.setDataset = function(dataset) {
	this._dataset = dataset;
}

RequestCommand.prototype.getParamFields = function() {
	return this._paramFields;
}

RequestCommand.prototype.setParamFields = function(paramFields) {
	this._paramFields = paramFields;
}

RequestCommand.prototype.frame = function() {
	return this._frame;
}

// Methods
RequestCommand.prototype.execute = function(arg) {
	try {
		this._fireBeforeExecute();

		var path = this._path;
		var i = path.indexOf("?");
		if (i < 0) {
			path += "?";
		}
		else if (i < (path.length - 1)) {
			path += "&";
		}

		var dataset = this._dataset;
		if (typeof(dataset) == "object") {
			var paramFields = this._paramFields;
			if (paramFields != null) {
				var fields = paramFields.split(",");
				for (var i=0; i<fields.length; i++)	{
					path += fields[i] + "=" + dataset.getValue(fields[i]) + "&";
				}
			}
		}

		var frame = this._frame;
		var target = frame.getTarget();
		if (target != __Frame_MODAL_DIALOG && target != __Frame_MODELESS_DIALOG)	{
			__open(path, frame);
		}
		else {
			return __openDialog(path, frame, arg);
		}

		this._fireOnSuccess();
	}
	catch(e) {
		if (this._fireOnFailure()) {
			processException(e);
		}
	}
}

//-------------------------------------------------------

// RPCCommand
function RPCCommand(id) {
	this._id = id;
	this._action = null;
	this._parameters = new ParameterSet();
	this._outParameters = new ParameterSet();
	/*功能号*/
	this._functionId="";
	/**后台交易类*/
	this._transclass="";	
	this._action = "/ajax/ajaxService";
	this._transactionMode = __TRANSACTION_REQUIRED;
	this._method = "";	
}

RPCCommand.prototype = new Command();

RPCCommand.prototype.getAction = function() {
	return this._action;
}

RPCCommand.prototype.setAction = function(action) {
	this._action = action;
}

RPCCommand.prototype.isShowDialog = function() {
	return this._showDialog;
}

RPCCommand.prototype.setShowDialog = function(showDialog) {
	this._showDialog = showDialog;
}

// Methods
RPCCommand.prototype.parameters = function() {
	return this._parameters;
}

RPCCommand.prototype.outParameters = function() {
	return this._outParameters;
}

RPCCommand.prototype.execute = function() {
	try {
		this._fireBeforeExecute();
		
		var xml = __populateRPCXml(this);
		var result = __remoteCall(_application_root+this._action, "base", xml, this._showDialog);
		if (result) {
			var rootNode = result.documentElement;
			if (rootNode) {
				var succeed = isTrue(rootNode.getAttribute("succeed"));
				var message = rootNode.selectSingleNode("message");	
				__parseRPCOutParameters(this._outParameters, rootNode);
				__parseRPCViewProperties(rootNode);
				__parseRPCOutJavaScript(rootNode);			
				if (succeed) 
				{					
					if (this._fireOnSuccess()) 
					{
						if (message.text) 
						{
							alert(getDecodeStr(message.text));
						}
					}
				}
				else 
				{
					if (this._fireOnFailure()) 
					{					
						alert(constErrUpdateFailed + "\n" + getDecodeStr(message.text));
					}				
				}				
				return succeed;
			}
		}
	}
	catch(e) 
	{
		processException(e);
	}
	return false;
}

function __populateRPCXml(command) {
	var parameters = command._parameters;
	var xml = "";
	xml += "<?xml version=\"1.0\" encoding=\"UTF-8\"?>";
	xml += "<rpc>";
	xml += "<parameters>" + __populateRPCParamXML(parameters) + "</parameters>";
	xml += "<viewProperties>" + __populateRPCViewPropertiesXML() + "</viewProperties>";
	xml += "</rpc>";
	return xml;
}

function __populateRPCParamXML(parameters) {
	var xml = "";

	for(var i=0; i<parameters.count(); i++){
		xml += "<parameter";
		xml += " name=\"" + parameters.indexToName(i) + "\"";
		xml += " dataType=\"" + getEncodeStr(parameters.getDataType(i)) + "\"";
		xml += " value=\"" + getEncodeStr(parameters.getValue(i)) + "\"";
		xml += "/>";
	}
	return xml;
}

function __populateAjaxParamXML(parameters) {
	var xml = "";
	var temp;
	var vv="";
	for(var i=0; i<parameters.count(); i++){
		xml += "<parameter";
		xml += " name=\"" + parameters.indexToName(i) + "\"";
		xml += " dataType=\"" + getEncodeStr(parameters.getDataType(i)) + "\"";
		temp=parameters.getValue(i);
		if(temp instanceof Array)
		{
			vv=doArray(temp);
		}
		else if(temp instanceof Date || temp instanceof String ||temp instanceof Number||temp instanceof Boolean)
		{
		  vv=doBaseType(temp,parameters.getDataType(i));
		}
		else if(temp instanceof Object)
		{
		  vv=doObject(temp);
		}
		else
		{
		  vv=doBaseType(temp,parameters.getDataType(i));
		}
		//alert(vv);
		xml += " value=\"" + getEncodeStr(vv) + "\"";
		xml += "/>";
	}
	return xml;
}

function __populateRPCViewPropertiesXML() {
	var xml = "";
	for(var i=0; i<ViewProperties.count(); i++){
		xml += "<property";
		xml += " name=\"" + ViewProperties.indexToName(i) + "\"";
		xml += " dataType=\"" + getEncodeStr(ViewProperties.getDataType(i)) + "\"";
		xml += " value=\"" + getEncodeStr(ViewProperties.getValue(i)) + "\"";
		//xml += " dataType=\"" + ViewProperties.getDataType(i) + "\"";
		//xml += " value=\"" + ViewProperties.getValue(i) + "\"";

		xml += "/>";
	}
	return xml;
}

function __parseRPCOutJavaScript(xmlRoot)
{
   var outJavascript = xmlRoot.selectSingleNode("javascript");
   if(outJavascript.text)
   {
     var jscode=outJavascript.text;
     eval(jscode/*getDecodeStr(jscode)*/);
   }
}

function __parseRPCOutParameters(parameters, xmlRoot) {
	var outParameters = xmlRoot.selectSingleNode("outParameters");
	var propertyNodes= outParameters.childNodes;
	for (var i=0; i<propertyNodes.length; i++) {
		var propertyNode = propertyNodes.item(i);
		var name = propertyNode.getAttribute("name");
		parameters.setValue(name, getDecodeStr(propertyNode.getAttribute("value")));
		parameters.setDataType(name, propertyNode.getAttribute("dataType"));
	}
}

function __parseRPCViewProperties(xmlRoot) 
{
	var viewProperties = xmlRoot.selectSingleNode("viewProperties");
	//var propertyNodes= viewProperties.childNodes;
	var propertyNodes= viewProperties.getElementsByTagName('property');//xuj 2011.5.11 update 兼容ff、chrome
	for (var i=0; i<propertyNodes.length; i++) 
	{
		var propertyNode = propertyNodes.item(i);
		var name = propertyNode.getAttribute("name");
		ViewProperties.setValue(name, getDecodeStr(propertyNode.getAttribute("value")));
		ViewProperties.setDataType(name, propertyNode.getAttribute("dataType"));
	}
}

//---------------------Java Object -->JavaScript Object
function __parseRequestOutParameters(parameters, xmlRoot) {
	var rootchildNodes=xmlRoot.childNodes;//xuj 2011.5.11 update 兼容ff、chrome下面注解的是以前的
	for(var n=0;n<rootchildNodes.length;n++){
		var rootchildNode=rootchildNodes[n];
		if(rootchildNode.nodeName=='outParameters'){
			var outParameters =rootchildNode;
			var propertyNodes= outParameters.childNodes;
			var tempvalue;
			//alert(propertyNodes.length);
			for (var i=0; i<propertyNodes.length; i++) {
				var propertyNode = propertyNodes.item(i);
				//alert(i+propertyNode.nodeName);
				if(propertyNode.nodeName=="property"){
					var name = propertyNode.getAttribute("name");
					//tempvalue="var xvalue="+getDecodeStr(propertyNode.getAttribute("value"))+";";
					tempvalue="var xvalue="+propertyNode.getAttribute("value")+";";
					eval(tempvalue);
					parameters.setValue(name,/*getDecodeStr(propertyNode.getAttribute("value"))*/ xvalue);
					parameters.setDataType(name, propertyNode.getAttribute("dataType"));
				}
			}
			break;
		}
		
	}
	//var outParameters = xmlRoot.selectSingleNode("outParameters");
	//alert(outParameters.nodeName);
	//var propertyNodes= outParameters.childNodes;
	//var propertyNodes= outParameters.getElementsByTagName('property');//xuj 2011.5.11 update 兼容ff、chrome
	//var tempvalue;
	//alert(propertyNodes.length);
	//for (var i=0; i<propertyNodes.length; i++) {
		//var propertyNode = propertyNodes.item(i);
		//alert(propertyNode.nodeName);
		//var name = propertyNode.getAttribute("name");
		//tempvalue="var xvalue="+getDecodeStr(propertyNode.getAttribute("value"))+";";
		//tempvalue="var xvalue="+propertyNode.getAttribute("value")+";";
		//eval(tempvalue);
		//parameters.setValue(name,/*getDecodeStr(propertyNode.getAttribute("value"))*/ xvalue);
	//	parameters.setDataType(name, propertyNode.getAttribute("dataType"));
	//}
}

function __parseRequestViewProperties(xmlRoot) 
{
	/*var viewProperties = xmlRoot.selectSingleNode("viewProperties");
	var propertyNodes= viewProperties.childNodes;
	var tempvalue;
	for (var i=0; i<propertyNodes.length; i++) 
	{
		var propertyNode = propertyNodes.item(i);
		var name = propertyNode.getAttribute("name");
		//tempvalue="var xvalue="+getDecodeStr(propertyNode.getAttribute("value"))+";";
		tempvalue="var xvalue="+propertyNode.getAttribute("value")+";";
		eval(tempvalue);		
		ViewProperties.setValue(name,xvalue);
		ViewProperties.setDataType(name, propertyNode.getAttribute("dataType"));
	}*/
	var rootchildNodes=xmlRoot.childNodes;//xuj 2011.5.11 update 兼容ff、chrome上面注解的是以前的
	//alert(rootchildNodes);
	for(var n=0;n<rootchildNodes.length;n++){
		var rootchildNode=rootchildNodes[n];
		if(rootchildNode.nodeName=="viewProperties"){
			var viewProperties =rootchildNode;
			var propertyNodes= viewProperties.childNodes;
			var tempvalue;
			for (var i=0; i<propertyNodes.length; i++) 
			{
				var propertyNode = propertyNodes.item(i);
				if(propertyNode.nodeNamde=="property"){
					var name = propertyNode.getAttribute("name");
					//tempvalue="var xvalue="+getDecodeStr(propertyNode.getAttribute("value"))+";";
					tempvalue="var xvalue="+propertyNode.getAttribute("value")+";";
					eval(tempvalue);		
					ViewProperties.setValue(name,xvalue);
					ViewProperties.setDataType(name, propertyNode.getAttribute("dataType"));
				}
			}
			break;
		}
	}
}

//-------------------------------------------------------
// Constants
var __DatasetInfo_ALL = "all";
var __DatasetInfo_ALL_CHANGE = "all-change";
var __DatasetInfo_CURRENT = "current";
var __DatasetInfo_SELECTED = "selected";

// DatasetInfo
function __DatasetInfo() {
	this._dataset = null;
	this._submitScope = __DatasetInfo_CURRENT;
	this._flushDataOnSuccess = false;
	this._clearSelectionOnSuccess = false;
}

__DatasetInfo.prototype.getDataset = function() {
	return this._dataset;
}

__DatasetInfo.prototype.setDataset = function(dataset) {
	this._dataset = dataset;
}

__DatasetInfo.prototype.getSubmitScope = function() {
	return this._submitScope;
}

__DatasetInfo.prototype.setSubmitScope = function(submitScope) {
	this._submitScope = submitScope;
}

__DatasetInfo.prototype.isFlushDataOnSuccess = function() {
	return this._flushDataOnSuccess;
}

__DatasetInfo.prototype.setFlushDataOnSuccess = function(flushDataOnSuccess) {
	this._flushDataOnSuccess = flushDataOnSuccess;
}

__DatasetInfo.prototype.isClearSelectionOnSuccess = function() {
	return this._clearSelectionOnSuccess;
}

__DatasetInfo.prototype.setClearSelectionOnSuccess = function(clearSelectionOnSuccess) {
	this._clearSelectionOnSuccess = clearSelectionOnSuccess;
}

__DatasetInfo.prototype.isDeleteSelectionOnSuccess = function() {
	return this._deleteSelectionOnSuccess;
}

__DatasetInfo.prototype.setDeleteSelectionOnSuccess = function(clearSelectionOnSuccess) {
	this._deleteSelectionOnSuccess = clearSelectionOnSuccess;
}

//-------------------------------------------------------

// Constants
var __TRANSACTION_NOT_SUPPORTED = 1;
var __TRANSACTION_SUPPORTS = 5;
var __TRANSACTION_REQUIRED = 10;
var __TRANSACTION_REQUIRES_NEW = 20;

// UpdateCommand
function UpdateCommand(id) {
	this._id = id;
	/*后台执行AjaxService*/
	this._action = "/ajax/ajaxService";
	this._transactionMode = __TRANSACTION_REQUIRED;
	this._method = null;
	this._alwaysPerform = false;
	/*数据集*/
	this._datasetInfos = new Array();
	/*需传到后台的参数*/
	this._parameters = new ParameterSet();

}

UpdateCommand.prototype = new RPCCommand();

UpdateCommand.prototype.getTransactionMode = function() {
	return this._transactionMode;
}

UpdateCommand.prototype.setTransactionMode = function(transactionMode) {
	this._transactionMode = transactionMode;
}

UpdateCommand.prototype.getMethod = function() {
	return this._method;
}

UpdateCommand.prototype.setMethod = function(method) {
	this._method = method;
}

UpdateCommand.prototype.isAlwaysPerform = function() {
	return this._alwaysPerform;
}

UpdateCommand.prototype.setAlwaysPerform = function(alwaysPerform) 
{
	this._alwaysPerform = alwaysPerform;
}

UpdateCommand.prototype.setFunctionId = function(functionId) 
{
	this._functionId = functionId;
}

UpdateCommand.prototype.getFunctionId = function() 
{
	return this._functionId ;
}

UpdateCommand.prototype.setHint = function(hints) 
{
	this._hints = hints;
}

UpdateCommand.prototype.getHint = function() 
{
	return this._hints ;
}

UpdateCommand.prototype.setTransClass = function(transclass) 
{
	this._transclass = transclass;
}

UpdateCommand.prototype.getTransClass = function() 
{
	return this._transclass ;
}


// Methods
UpdateCommand.prototype.addDatasetInfo = function(dataset, submitScope) 
{
	var datasetInfo = new __DatasetInfo();

	datasetInfo.setDataset(dataset);
	datasetInfo.setSubmitScope(submitScope);

	var infos = this._datasetInfos;
	infos[infos.length] = datasetInfo;
	return datasetInfo;
}
/***********************************************
 *数据是否已被修改过
 ***********************************************/
UpdateCommand.prototype.isDirty = function() 
{
	var datasetInfos = this._datasetInfos;

	for (var i=0; i<datasetInfos.length; i++) 
	{
		var dataset = datasetInfos[i].getDataset();
		if (dataset != null) 
		{
			var submitScope = datasetInfos[i].getSubmitScope();
			switch (submitScope)
			{
			case __DatasetInfo_ALL:
				return true;
			case __DatasetInfo_ALL_CHANGE:
				var record=dataset.firstUnit;
				if(typeof(signxml)!="undefined"&&signxml.length>0){ //人事异动保存兼容电子签章的保存 xieguiquan 2011.06.22
				savesignature('1');
				
				}
				while (record) 
				{
					if (record.recordState == "insert" ||
						record.recordState == "modify" ||
						record.recordState == "delete") {
						return true;
					}
					record=record.nextUnit;
				}
				break;
			case __DatasetInfo_CURRENT:
				if (dataset.getCurrent() != null) {
					return true;
				}
				break;
			case __DatasetInfo_SELECTED:
				var record=dataset.getFirstRecord();
				while (record) {
					if (record.getValue("select")) {
						return true;
					}
					record=record.getNextRecord();
				}
				break;
			}
		}
	}
	return false;
}

UpdateCommand.prototype.execute = function() 
{

	if (!__postDatasets(this)) 
	{
		return false;
	}

	if (!this.isAlwaysPerform() && !this.isDirty()) //删除
	{
		return false;
	}
	try 
	{
		this._fireBeforeExecute();

		var xml = __populateUpdateXml(this);


		var result = __remoteCall(_application_root+this._action, "updateData", xml, false);

		if (result) 
		{
			var rootNode = result.documentElement;
			if (rootNode) 
			{
				var succeed = isTrue(rootNode.getAttribute("succeed"));
				var message = rootNode.selectSingleNode("message");				
				var requeryPath = rootNode.selectSingleNode("requeryPath");				
				__parseRPCViewProperties(rootNode);

				
				if (succeed) 
				{
					var datasetInfos = this._datasetInfos;
					for (var i=0; i<datasetInfos.length; i++) 
					{
						var datasetInfo = datasetInfos[i];
						var dataset = datasetInfo.getDataset();
						if (dataset != null) 
						{
							if (datasetInfo.isFlushDataOnSuccess()) 
							{
								if(typeof(template_refresh)!="undefined" && template_refresh&&template_refresh!=null&&template_refresh=="true") //模板的动态加载有问题，子集的XML描述需动态加上 宽度、权限属性，动态加载不会添加
								{ 
									 window.location=window.location;
								}
								else
									dataset.flushData();
							}
							else {
								if (datasetInfo.isDeleteSelectionOnSuccess() &&	dataset.getField("select") != null) 
								{
									var current = dataset.getCurrent();
									dataset.disableControls();
									try	
									{
										dataset.moveFirst();
										while (!dataset.isLast()) 
										{
											if (dataset.getValue("select")) 
											{									
												dataset.deleteRecord();
											}
											else 
											{
												dataset.moveNext();
											}
										}
									}
									finally 
									{
										dataset.setCurrent(current);
										dataset.enableControls();
									}
								}
								else if (datasetInfo.isClearSelectionOnSuccess() &&
									dataset.getField("select") != null) {
									var current = dataset.getCurrent();
									dataset.disableControls();
									try	{
										dataset.moveFirst();
										while (!dataset.isLast()) {
											dataset.setValue("select", false);
											dataset.moveNext();
										}
									}
									finally {
										dataset.setCurrent(current);
										dataset.enableControls();
									}
								}
							}
						}
					}
			
					var datasetInfos = this._datasetInfos;
					for (var i=0; i<datasetInfos.length; i++) {
						var datasetInfo = datasetInfos[i];
						var dataset = datasetInfo.getDataset();
						if (dataset != null) {
							var record=dataset.firstUnit;
							while (record) {
								_resetRecordState(record);
								record=record.nextUnit;
							}
						}
					}
					
					if (this._fireOnSuccess()) {
						if (message.text) {
							alert(getDecodeStr(message.text));
						}
					}
				}
				else {
					if (this._fireOnFailure()) {	
						 alert(constErrUpdateFailed + "\n" + getDecodeStr(message.text));
					}
				}
				
				if (succeed&&requeryPath.text) {
					window.open(requeryPath.text, "_self");
				}
				//22923 changxy  员工管理--花名册--花名册，12号花名册，删除一条数据后，页码处没有自动刷新 20161112
				var dataset=this._datasetInfos[this._datasetInfos.length-1]._dataset;
				var datapilotm=document.getElementById('pilot'+dataset.id);
				if(datapilotm){//50368 兼容60锁人事异动保存操作
					dataset.moveToPage(dataset.pageIndex);//删除后重新跳转到当前页面
					var title=" <span style='padding-top:5px;height:20px' > 第 "+dataset.pageIndex+  " 页 共 "+dataset.getRowCount()+" 条 "+" 共 "+dataset.getPageCount()+" 页"+" </span>";
					if(datapilotm&&datapilotm.celllabel)
						datapilotm.celllabel.innerHTML=title;
				}
				return succeed;
			}
		}
	}
	catch(e) 
	{
		processException(e);	
	}
	return false;
}



/*
 *模板保存数据方法 邓灿(2010-5-26)
 */
UpdateCommand.prototype.autosave = function() 
{

	if (!__postDatasets(this))
	{
		return true;
	}

	if (!this.isAlwaysPerform() && !this.isDirty()) 
	{
		return true;
	}
	try 
	{
		this._fireBeforeExecute();
		var xml = __populateUpdateXml(this);
		var result = __remoteCall(_application_root+this._action, "updateData", xml, false);
		
		if (result)  //20140925 报批报错时需有提示
		{
			var rootNode = result.documentElement;
			if (rootNode) 
			{
				var succeed = isTrue(rootNode.getAttribute("succeed"));
				var message = rootNode.selectSingleNode("message");				
				var requeryPath = rootNode.selectSingleNode("requeryPath");				
				__parseRPCViewProperties(rootNode);

				
				if (succeed) 
				{ 
					
				}
				else {
					if (this._fireOnFailure()) {	
						 alert(constErrUpdateFailed + "\n" + getDecodeStr(message.text));
						 return false;
					}
				}
				return succeed;
			}
		}
		
	}
	catch(e) 
	{
		processException(e);	
	}
	return true;
}















function __postDatasets(command) 
{
	var datasetInfos = command._datasetInfos;
	for (var i=0; i<datasetInfos.length; i++) 
	{
		var dataset = datasetInfos[i].getDataset();
		if (dataset != null) 
		{
			if (!dataset.postRecord()) 
			{
				return false;
			}
		}
	}
	return true;
}


/********************************************
 *组装更新字符串
 ********************************************/
function __populateUpdateXml(command) 
{
	var datasetInfos = command._datasetInfos;
	var parameters = command._parameters;

	var defXML = "";
	var dataXML = "";

	for (var i=0; i<datasetInfos.length; i++) 
	{
		var dataset = datasetInfos[i].getDataset();
		if (dataset != null) 
		{
			// pupulate definition information
			var fields = new Array();
			var fieldsText = "";
			var count = dataset.fields.fieldCount;
			for (var j=0; j<count; j++)	{
				fields[j] = dataset.getField(j).name;
				fieldsText += fields[j] + ",";
			}

			defXML += "<dataset";
			defXML += " type=\"" + dataset.type + "\"";
			defXML += " id=\"" + dataset.id + "\"";
			defXML += " keyFields=\"" + fieldsText + "\"";
			defXML += " updateFields=\"" + fieldsText + "\"";
			defXML += ">";

			for (var j=0; j<count; j++)	
			{
				var field = dataset.getField(j);
				defXML += "<field";
				defXML += " name=\"" + field.name + "\"";
				defXML += " dataType=\"" + field.dataType + "\"";
				defXML += "/>";
			}

			defXML += "</dataset>";

			// pupulate data information
			var submitScope = datasetInfos[i].getSubmitScope();
			switch (submitScope)
			{
			case __DatasetInfo_ALL:
				var record=dataset.firstUnit;
				while (record) {
					dataXML += __populateUpdateRecordXML(fields, record);
					record=record.nextUnit;
				}
				break;
			case __DatasetInfo_ALL_CHANGE:
				var record=dataset.firstUnit;
				while (record) {
					if (record.recordState == "insert" ||
						record.recordState == "modify" ||
						record.recordState == "delete") {
						dataXML += __populateUpdateRecordXML(fields, record);
					}
					record=record.nextUnit;
				}
				break;
			case __DatasetInfo_CURRENT:
				if (dataset.record) {
					dataXML += __populateUpdateRecordXML(fields, dataset.record);
				}
				break;
			case __DatasetInfo_SELECTED:
				var record=dataset.firstUnit;
				while (record) {
					if (isTrue(record.getValue("select")) && dataset_isRecordValid(record)) {
						dataXML += __populateUpdateRecordXML(fields, record);
					}
					record=record.nextUnit;
				}
				break;
			}
		}
	}

	var xml = "";
	xml += "<?xml version=\"1.0\" encoding=\"UTF-8\"?>";
	xml += "<rpc>";
	xml += "<transactionMode>" + command._transactionMode + "</transactionMode>";
	xml += "<method>" + command._method + "</method>";
	xml +="<function_id>"+command._functionId+"</function_id>";
	xml +="<transclass>"+command._transclass+"</transclass>";	
	xml += "<datasets>" + defXML + "</datasets>";
	xml += "<records>" + dataXML + "</records>";
	xml += "<parameters>" + __populateRPCParamXML(parameters) + "</parameters>";
	xml += "<viewProperties>" + __populateRPCViewPropertiesXML() + "</viewProperties>";
	xml += "</rpc>";
	return xml;
}

function __populateUpdateRecordXML(fields, record) {
	var xml = "";
	xml += "<record";
	xml += " dataset=\"" + record.dataset.id + "\"";
	xml += " type=\"" + record.recordState + "\"";
	xml += ">";

	xml += "<old>";
	for (var i=0; i<fields.length; i++)	
	{
		//xml += getEncodeStr(record.getValue("_old_" + fields[i])) + ",";
		xml += filter_xml(record.getValue("_old_" + fields[i])) + ",";

	}
	xml += "</old>";

	xml += "<new>";
	for (var i=0; i<fields.length; i++)	{
		//xml += getEncodeStr(record.getValue(fields[i])) + ",";
		var value=record.getValue(fields[i]);		
		value=filter_xml(value);		
		xml += value + ",";
	}
	xml += "</new>";

	xml += "</record>";
	return xml;
}

function __populateRequestXml(request)
{
	var xml = "";
	xml += "<?xml version=\"1.0\" encoding=\"GB2312\"?>";
	xml += "<rpc>";
	xml += "<transactionMode>" + request._transactionMode + "</transactionMode>";
	xml += "<method>" + request._method + "</method>";
	xml +="<function_id>"+request._functionId+"</function_id>";
	xml +="<transclass>"+request._transclass+"</transclass>";	
	xml += "<parameters>" + __populateAjaxParamXML(request._parameters) + "</parameters>";
	xml += "<viewProperties>" + __populateRPCViewPropertiesXML() + "</viewProperties>";
	xml += "</rpc>";
	return xml;

}
//-------------------------------------------------------
// SubmitFormDatasetCommand
function SubmitFormDatasetCommand(id) {
	this._id = id;
	this._action = "";
	this._method = "get";
	this._target = "_self";
	this._dataset = null;
}

SubmitFormDatasetCommand.prototype = new Command();

SubmitFormDatasetCommand.prototype.getAction = function() {
	return this._action;
}

SubmitFormDatasetCommand.prototype.setAction = function(action) {
	this._action = action;
}

SubmitFormDatasetCommand.prototype.getMethod = function() {
	return this._method;
}

SubmitFormDatasetCommand.prototype.setMethod = function(method) {
	this._method = method;
}

SubmitFormDatasetCommand.prototype.getTarget = function() {
	return this._target;
}

SubmitFormDatasetCommand.prototype.setTarget = function(target) {
	this._target = target;
}

SubmitFormDatasetCommand.prototype.getDataset = function() {
	return this._dataset;
}

SubmitFormDatasetCommand.prototype.setDataset = function(dataset) {
	this._dataset = dataset;
}

// Methods
SubmitFormDatasetCommand.prototype.execute = function() 
{
	try 
	{
		this._fireBeforeExecute();
		var _dataset = this.getDataset();
		var _action = this.getAction();
		var _method = this.getMethod();
		var _target = this.getTarget();

		if (_dataset) {
			if (_dataset.getCurrent() && _dataset.getCurrent().getState()=="new") {
				_dataset.modified=true;
			}
			
			if (!_dataset.postRecord()) {
				return false;
			}
			
			var form = document.createElement(
				"<form method=\"" + _method + "\" action=\"" + _action + "\" " +
				"style=\"visibility: hidden\"></form>");
			var count = _dataset.fieldSet().count();
			for (var i=0; i<count; i++){
				form.insertAdjacentHTML("beforeEnd", "<input name=\"" + _dataset.getField(i).name + "\" >");
			}
		}
		document.body.appendChild(form);
		for (var i=0; i<count; i++)
		{
			form[_dataset.getField(i).name].value = _dataset.getValue(i);
		}

		form.submit();

		this._fireOnSuccess();
		return true;
	}
	catch(e) 
	{
		if (this._fireOnFailure()) 
		{
			processException(e);
		}
		return false;
	}
}
//--------------------------------------------------------------
//author chenmengqing added.
//2005.11.30

//--------------Ajax--->RPCCommand
Ajax.prototype=new RPCCommand();

function Ajax() {
}
Object.extend = function(destination, source) {
  for (property in source) {
    destination[property] = source[property];
  }
  return destination;
}
Ajax.prototype.setOptions=function(options)
{
     this.options=Object.extend({method:'post',asynchronous: false,parameters:'',action:'/ajax/ajaxService',functionId:'',transclass:'',pop_information:false,submit_form:false},(options||{}));
     this._functionId=this.options['functionId'];
     /**后台交易类*/
     this._transclass=this.options['transclass'];	
     this._action =this.options['action'];     
}

Ajax.prototype.responseIsSuccess=function()
{
    return this.transport.status == undefined
        || this.transport.status == 0 
        || (this.transport.status >= 200 && this.transport.status < 300);
}

Ajax.prototype.responseIsFailure=function()
{
    return !this.responseIsSuccess();
}

//---------------Request-->Ajax
Request.prototype =new Ajax();
//*************************0,1,2,3,4
Request.Events = ['Uninitialized', 'Loading', 'Loaded', 'Interactive', 'Complete'];
/***************************************************
 *forms:表单名串'aaa,bbb'
 ***************************************************/
function Request(options,parameters)
{
    //this.forms=forms;
    /**收集表单中参数*/
    this.transport = getXMLHttpRequestByBrowser();
    this.setOptions(options);
    if(this.options.submit_form&&(parameters instanceof Array))
      this.combineValueFromAllForms(parameters); 

    if(parameters instanceof ParameterSet)
    {
	for(var i=0; i<parameters.count(); i++){
		this._parameters.setValue(parameters.indexToName(i),parameters.getValue(i));
		this._parameters.setDataType(parameters.indexToName(i),parameters.getDataType(i));
	}    	
    }
    this.request();
}

Request.prototype.combineValueFromAllForms=function(forms)
{
    var elements;
    if(typeof(forms)=="undefined"||forms==null)
      return;
    for(var i=0;i<forms.length;i++)
    {
       elements=Form.getElements(forms[i]);    	
       if(elements==null)
          continue;
       for(var j=0;j<elements.length;j++)
       {
	  this._parameters.setValue(elements[j].name,$F(elements[j].name));
       }

    }
}

Request.prototype.onStateChange=function()
{
    var readyState = this.transport.readyState;
    if (readyState != 1)
      this.respondToReadyState(this.transport.readyState);
}

Request.prototype.parseXmlResult=function()
{
	var result =this.transport.responseText;
	var XMLDoc;
	if (window.DOMParser) { // all browsers, except IE before version 9 xuj 2013-10-25 add 解决ie10解析xml
                    var parser = new DOMParser();
                    try {
                        XMLDoc = parser.parseFromString (result, "text/xml");
                    } catch (e) {
                            // if text is not well-formed, 
                            // it raises an exception in IE from version 9
                    };
    }else{
    	XMLDoc = XmlDocument.create();
    	//alert(result);
    	XMLDoc.async=false;
    	XMLDoc.loadXML(result);
    }
	this.response(XMLDoc);//xuj 2011.5.11 update 兼容ff、chrome
}

Request.prototype.respondToReadyState=function(readyState)
{
    var event = Request.Events[readyState];
    if (event == 'Complete')
    {
       this.parseXmlResult();
      (this.options['on' + this.transport.status] || this.options['on' + (this.responseIsSuccess() ? 'Success' : 'Failure')]|| Prototype.emptyFunction)(this._outParameters);
    }

    (this.options['on' + event] || Prototype.emptyFunction)(this._outParameters);

    /* Avoid memory leak in MSIE: clean up the oncomplete event handler */
    if (event == 'Complete')
      this.transport.onreadystatechange = Prototype.emptyFunction;
}

Request.prototype.setRequestHeaders=function()
{
    var requestHeaders = ['X-Requested-With', 'XMLHttpRequest'];
	
    if (this.options.method == 'post') 
    {
      requestHeaders.push('Content-type', 
        'application/x-www-form-urlencoded');
      /* Force "Connection: close" for Mozilla browsers to work around
       * a bug where XMLHttpReqeuest sends an incorrect Content-length
       * header. See Mozilla Bugzilla #246651. 
       */
      if (this.transport.overrideMimeType&&navigator.userAgent.indexOf("Firefox") > -1)
        requestHeaders.push('Connection', 'close');
    }

    if (this.options.requestHeaders)
      requestHeaders.push.apply(requestHeaders, this.options.requestHeaders);
    for (var i = 0; i < requestHeaders.length; i += 2)
      this.transport.setRequestHeader(requestHeaders[i], requestHeaders[i+1]);

}

Request.prototype.request=function()
{
    var parameters = this.options.parameters || '';
	
    var xml = __populateRequestXml(this);
    //alert(xml);
    //cmq added at 20121104 for security
    xml=encode_v1(xml);
    //end.
    parameters = parameters + "&__type=exeTrans&__xml=" + xml;
    //alert(parameters);
	paramters  =encodeURIComponent(parameters);
    //alert(parameters);	
    try 
    {
      if (this.options.method == 'get')
        this.options.action += '?' + parameters;
	
      this.transport.open(this.options.method, this.options.action, this.options.asynchronous);
      if (this.options.asynchronous) 
      {
        this.transport.onreadystatechange = this.onStateChange.bindObj(this);
        setTimeout((function() {this.respondToReadyState(1)}).bindObj(this), 10);
      }

      this.setRequestHeaders();
      var body = this.options.postBody ? this.options.postBody : parameters;
      
      this.transport.send(this.options.method == 'post' ? body : null);

      /*同步*/
      if(!this.options.asynchronous)
      {

	this.parseXmlResult();
	this._parameters.clearAll();
      }
    } 
    catch (e) {
    	processException(e);
    }
}



Request.prototype.response=function(result)
{
	//alert(result);
    try 
    {		
		if (result) 
		{
			//alert(result);
			var rootNode = result.documentElement;
			//var rootNode = result.selectSingleNode("result");
			//alert(rootNode.nodeName);
			if (rootNode) 
			{
				var succeed = isTrue(rootNode.getAttribute("succeed"));
				
				var message ="";
				var rootchildNodes=rootNode.childNodes;//xuj 2011.5.11 update 兼容ff、chrome下面注解的是以前的
				for(var n=0;n<rootchildNodes.length;n++){
					var rootchildNode=rootchildNodes[n];
					if(rootchildNode.nodeName=='message'){
						message=rootchildNode;
						break;
					}
				}
				//var message =rootNode.selectSingleNode("message");

				__parseRequestOutParameters(this._outParameters, rootNode);

				__parseRequestViewProperties(rootNode);
				if (succeed) 
				{					
					if (this._fireOnSuccess()) 
					{
   						if(!this.options.asynchronous)//chenmengqing changed at 20060729
   						  (this.options['onSuccess']|| Prototype.emptyFunction)(this._outParameters);
						if (message.text&&this.options.pop_information) 
						{
						   //alert(message.text);
						   if(message.text){
							   alert(getDecodeStr(message.text));
	   						}else{
	   							alert(getDecodeStr(message.textContent));
							}
						}
						this._parameters.clearAll();						
					}
				}
				else 
				{
					if (this._fireOnFailure()) 
					{		
   						if(!this.options.asynchronous)					
      						(this.options['onFailure']|| Prototype.emptyFunction)(this._outParameters);	
   						if(message.text){
   							alert(/*constErrUpdateFailed + "\n" +*/ getDecodeStr(message.text));
   						}else{
							alert(/*constErrUpdateFailed + "\n" +*/ getDecodeStr(message.textContent));
						}
					}				
				}				
				return succeed;
			}
		}
    	
    }
    catch (e) {
    	processException(e);
    }    	
}
