/**
 *创建一个构造函数
 *BJCA
 */
function SignObjsBjca(SignatureID,signDataid,signdivid,pagedivid,objectname,pageid,x,y,divName,isShow){
    this.SignObj = null;
    this.signDataid = signDataid;
	this.signDataname = null;
	this.pagedivid = pagedivid;
	this.DocumentID = "BJCA";
	this.pageid = pageid;
	this.SignatureID = SignatureID;
	this.signdivid = signdivid;
	this.objectname = objectname;
	this.x = x;
	this.y = y;
	this.gridno=divName.split("_")[3];
	this.isSHowSign=isShow;
}
/**
 *创建签章对象（一个签章对应一个对象）
 *BJCA
 */
SignObjsBjca.prototype.doSignBjca = function(){
	var index=1000;
	if(this.isSHowSign==false){//签章
		var fields=templateCard_me.fieldSet.fields;//如果是签章且没有写权限,直接返回false,不执行签章操作。
		for(var num=0;num<fields.length;num++){
			var field=fields[num];
			if(field.uniqueId==("fld_"+this.pageid+"_"+this.gridno)){
				if(field.rwPriv==0||field.rwPriv==1){
					return false;
				}
				break;
			}
		}
	}else{//显示签章
		var fields=templateCard_me.fieldSet.fields;//如果是签章且没有写权限,直接返回false,不执行签章操作。
		for(var num=0;num<fields.length;num++){
			var field=fields[num];
			if(field.uniqueId==("fld_"+this.pageid+"_"+this.gridno)){
				if(field.rwPriv==0){
					index=-1;
				}
				break;
			}
		}
	}
    var CLSID = '820390E5-1C07-483D-AEED-6A0EDF640AA2';
    var signDiv = document.getElementById(this.signdivid);
	var addEvent = true;
	try {
		if(checkIsIe()){
			if(window.navigator.platform == "Win32")
				signDiv.innerHTML = '<object id="' + this.objectname + '" classid="CLSID:'+ CLSID +'" codeBase="/module/template/signaturecab/bjcacab/BJCAWebSign.CAB#version=4,2,0,0" style="POSITION: absolute; TOP: '+this.y+'px; LEFT: '+this.x+'px;z-index:'+index+';"> <PARAM NAME="Visible" VALUE="true"> </object>';
			else
				signDiv.innerHTML = '<object id="' + this.objectname + '" classid="CLSID:'+ CLSID +'" codeBase="/module/template/signaturecab/bjcacab/BJCAWebSignX64.CAB#version=4,2,0,0" style="POSITION: absolute; TOP: '+this.y+'px; LEFT: '+this.x+'px;z-index:'+index+';"> <PARAM NAME="Visible" VALUE="true"> </object>';
			if (addEvent) {
				var clt = eval(this.objectname);
				if (clt.attachEvent) { 
					clt.attachEvent("OnSign", this.SignDataBjca.bind(this));
					clt.attachEvent("OnVerify", this.VerifySignBjca.bind(this));
					clt.attachEvent("OnSignRemoved", this.RemoveSignObjBjca.bind(this));
				} else {
					this.AttachForIE11Event(this.objectname, "OnSign", "SignDataBjca("+this.objectname+")");
					this.AttachForIE11Event(this.objectname, "OnVerify", "VerifySignBjca("+this.objectname+")");
					this.AttachForIE11Event(this.objectname, "OnSignRemoved", "RemoveSignObjBjca("+this.objectname+")");					
					//clt.addEventListener("OnUsbKeyChange", $OnUsbKeyChange, false);
					//clt.addEventListener("Sign", this.SignDataBjca.bind(this),false);
					//clt.addEventListener("Verify", this.VerifySignBjca.bind(this),false);
					//clt.addEventListener("SignRemoved", this.RemoveSignObjBjca.bind(this),false);
				}	
			}	
		}else {
			if (addEvent) 
				signDiv.innerHTML = '<embed id=' + this.objectname + ' type=application/x-xtx-axhost clsid={' + CLSID + '} event_OnUsbkeyChange=$OnUsbKeyChange width=0 height=0 />' ;
			else 
				signDiv.innerHTML = '<embed id=' + this.objectname + ' type=application/x-xtx-axhost clsid={' + CLSID + '} width=0 height=0 />' ;
		}
		this.SignObj = addSignatureBjca(this.objectname,this.pageid);
	} catch (e) {
		var pageDiv = document.getElementById(this.pagedivid);
		pageDiv.removeChild(signDiv);
		signDiv.innerHTML = "";
		signDiv = null;
		return false;
	}
}
/**
 *兼容ie11用到（BJCA执行签章）
 */
function SignDataBjca(objectname){
	var subsign ;
	for(var i=0;i<templateCard_me.signobjarr.length;i++){
		var map = templateCard_me.signobjarr[i];
		if(map.objectname==objectname.id){
			subsign = map.signobjs;
		}
	}
	subsign.SignDataBjca();
}
/**
 *兼容ie11用到（BJCA验证签章）
 */
function VerifySignBjca(objectname){
	var subsign ;
	for(var i=0;i<templateCard_me.signobjarr.length;i++){
		var map = templateCard_me.signobjarr[i];
		if(map.objectname==objectname.id){
			subsign = map.signobjs;
		}
	}
	subsign.VerifySignBjca();
}
/**
 *兼容ie11用到（BJCA撤销签章）
 */
function RemoveSignObjBjca(objectname){
	var subsign ;
	for(var i=0;i<templateCard_me.signobjarr.length;i++){
		var map = templateCard_me.signobjarr[i];
		if(map.objectname==objectname.id){
			subsign = map.signobjs;
		}
	}
	subsign.RemoveSignObjBjca();
}
/**
 *印章对象的执行签章的回调函数
 *BJCA
 */
SignObjsBjca.prototype.SignDataBjca = function(){
	var signatureSize=0 ;
	var map = new HashMap();	
	//设置URL地址
    var orgData = GetOrgData();
    var signature = this.SignObj.Sign(orgData);
    var orgData = GetOrgData();
	var signData = "hjsoft";
	if (signData != ""){
		ret = this.SignObj.Verify(orgData, signature);
        if(ret){
	        //Ext.showAlert("验证成功，数据有效！");
        }else{
	        Ext.showAlert("验证失败，数据无效！");
	        return;
        }
	}
    if(signature != ""){
	    document.getElementById(this.signDataid).value = signature;
	    var time = new Date();
	    var lg = time.getTime();
	    this.SignatureID = lg;
	    map.put("DocumentID",this.DocumentID);    
	    map.put("SignatureID",this.SignatureID+'');
	    map.put("signatureText",signature);
	    map.put("signatureSize",signatureSize+'');
	    map.put("flag","save");
	    map.put("signflag","1");
	    Rpc({functionId:'MB00004006',async:false,success:savesignOK,scope:this},map);
	}
}
/**
 *给签章对象添加方法（可扩展BJCA签章支持的其他方法，现只添加了基本常用到的几个方法）
 *BJCA
 */
function addSignatureBjca(ctlName,pageId){
	var o = new Object();
	
	var clt = eval(ctlName);
	o.Sign = function(plainstring) {
		return clt.Sign(plainstring);
	};
	o.SetOffsetPos = function(posRelativeElementIDString, x ,y ) {
		return clt.SetOffsetPos(posRelativeElementIDString, x,y );
	};
	o.Verify = function(plainstring, signDataString) {
		return clt.Verify(plainstring, signDataString);
	};
	
	o.IsSigned = function() {		
		return clt.IsSigned();
	};
	o.SetVisible = function(bVisible) {
		return clt.SetVisible(bVisible);
	};

	o.SetDisplayRect = function(left,top,width,height) {
		return clt.SetDisplayRect(left,top,width,height);
	};

	o.SetWebServiceURL = function(strData) {
		return clt.SetWebServiceURL(strData);
	};
	o.SetCtrlPos = function(x,y){
		return clt.SetCtrlPos(x,y);
	}
	o.GetXPos = function(){
		return clt.GetXPos();
	}
	o.GetYPos = function(){
		return clt.GetYPos();
	}
	return o;
}
/**
 *印章对象的验证签章的回调函数
 *BJCA
 */
SignObjsBjca.prototype.VerifySignBjca=function(){
	var orgData = GetOrgData();
	var signData = "hjsoft";
	if (signData != ""){
		ret = this.SignObj.Verify(orgData, document.getElementById(this.signDataid).value);
        if(ret){
	        Ext.showAlert("验证成功，数据有效！");
        }else{
	        Ext.showAlert("验证失败，数据无效！");
        }
	}
	return ret;
}
function savesignOK(form,action){
	//alert('保存签章成功！');
	var result = Ext.decode(form.responseText);
	var flag = result.flag;
	if(flag=='true'){
		if(templateCard_me.signXml!=''){
			var signXml = this.editSignaturXml(templateCard_me.signXml,1);
			templateCard_me.signXml = signXml;
		}else{
			var RecordSet = templateCard_me.getCurRecordSet();
			var fields = RecordSet.fields;
			for(var i=0;i<fields.length;i++){
				var field = fields[i];
				if(field.fldName=='signature'){
		    		var signXml = getDecodeStr(field.disValue);
		    		signXml = this.editSignaturXml(signXml,1);
		    		templateCard_me.signXml = signXml;
		   		}
			}
		}
	}else{
		Ext.showAlert("签章出错，请退出当前表单重新执行!",function(){
			this.SignObj.SetVisible(false);
		    var signdiv = document.getElementById(this.signdivid);
		    var pagediv = document.getElementById(this.pagedivid);
		    pagediv.removeChild(signdiv);
		    this.SignObj  =null;
		},this);
		//if (0 == this.SignObj.IsSigned()) {
		   
		//}
	}
}
function GetOrgData(){
	var temp = "hjsoft";
	return temp;
}
/**
 *印章对象的撤销签章的回调函数
 *BJCA
 */
SignObjsBjca.prototype.RemoveSignObjBjca=function(){
	if (0 == this.SignObj.IsSigned()) {
	   this.SignObj.SetVisible(false);
	   var signdiv = document.getElementById(this.signdivid);
	   var pagediv = document.getElementById(this.pagedivid);
	   pagediv.removeChild(signdiv);
	   this.SignObj  =null;
	}
	if(templateCard_me.signXml!=''){
		var signXml = this.editSignaturXml(templateCard_me.signXml,0);
		templateCard_me.signXml = signXml;
	}else{
		var RecordSet = templateCard_me.getCurRecordSet();
		var fields = RecordSet.fields;
		for(var i=0;i<fields.length;i++){
			var field = fields[i];
			if(field.fldName=='signature'){
	    		var signXml = getDecodeStr(field.disValue);
	    		signXml = this.editSignaturXml(signXml,0);
	    		templateCard_me.signXml = signXml;
	   		}
		}
	}
}
/**
 *进行添加撤销签章改变xml数据
 *flag=0 撤销 flag=1 添加 
 */
SignObjsBjca.prototype.editSignaturXml=function(signXml,flag){
	if(signXml==''){
		signXml += "<?xml version=\"1.0\" encoding=\"GB2312\"?>";
		signXml += "<params>";
		signXml += "</params>";
	}
	var ishave = false;
	signXml=replaceAll(signXml,"＜","<");
	signXml=replaceAll(signXml,"＞",">");
	signXml=replaceAll(signXml,"＇","'");
	signXml=replaceAll(signXml,"＂",'"');
	signXml=replaceAll(signXml,"&","");
	var XMLDoc = loadXMLString(signXml);  
	XMLDoc.async=false;
	var rootNode = XMLDoc.documentElement;
	try
	{
		if(rootNode)
		{
			var recordnode = rootNode.childNodes;
			for(var i=0;i<recordnode.length;i++){
				if(recordnode[i].getAttribute("DocuemntID")=='BJCA'){
					ishave = true;
					var itemNodes = recordnode[i].childNodes;
					if(flag==1){//添加
						var newNode = XMLDoc.createElement("item");
						newNode.setAttribute('UserName','');
						newNode.setAttribute('SignatureID',this.SignatureID);
						newNode.setAttribute('pointx',this.SignObj.GetXPos()-4-5);
						newNode.setAttribute('pointy',this.SignObj.GetYPos()-5);
						newNode.setAttribute('PageID',this.pageid);
						newNode.setAttribute('GridNO',this.gridno);
						newNode.setAttribute('node_id',""+templateCard_me.nodeId);//签章增加node_id属性
						recordnode[i].appendChild(newNode);
					}
					else {//撤销
						for(var j=0;j<itemNodes.length;j++){
							var node = itemNodes[j];
							if(node.getAttribute("SignatureID")==this.SignatureID&&node.getAttribute("PageID")==this.pageid){
								var newAtt = XMLDoc.createAttribute("delflag");
								newAtt.nodeValue ="true";
								node.setAttributeNode(newAtt);
							}
						}
					}
				}else{
					ishave = false;
				}
			}
			if(ishave==false){
				var record = XMLDoc.createElement("record");
				record.setAttribute('DocuemntID','BJCA');
				if(flag==1){//添加
					var newNode = XMLDoc.createElement("item");
					newNode.setAttribute('UserName','');
					newNode.setAttribute('SignatureID',this.SignatureID);
					newNode.setAttribute('pointx',this.SignObj.GetXPos()-4-5);
					newNode.setAttribute('pointy',this.SignObj.GetYPos()-5);
					newNode.setAttribute('PageID',this.pageid);
					newNode.setAttribute('GridNO',this.gridno);
					newNode.setAttribute('node_id',""+templateCard_me.nodeId);//签章增加node_id属性
					record.appendChild(newNode);
				}
				rootNode.appendChild(record);
			}
			signXml = XMLDoc.xml;
		}
	}
	catch(e)
	{
		//Ext.showAlert(e.message);
	}
	return signXml;
}
/**
 *IE11下注册监听函数
 *BJCA
 */
SignObjsBjca.prototype.AttachForIE11Event=function(strObjName, eventName, callbackFunName){
	var handler = document.createElement("script");
	handler.setAttribute("for", strObjName);
	handler.setAttribute("event", eventName);
	var textnote = document.createTextNode(callbackFunName);
	handler.appendChild(textnote);
	document.body.appendChild(handler);
}
/**
 * 坐标(BJCA)
 * @param x
 * @param y
 * @return
 */
function CPos(x, y){
	 this.x = x;
	 this.y = y;
}
/**
 * 得到对象的相对浏览器的坐标(BJCA)
 * @param ATarget
 * @return
 */
function GetObjPos(ATarget){
      var target = ATarget;
      var pos = new CPos(target.offsetLeft, target.offsetTop);
      var target = target.offsetParent;
      while (target){
          pos.x += target.offsetLeft;
          pos.y += target.offsetTop;
          target = target.offsetParent
      }
      return pos;
}
/**
 *加载签好的章
 */
function initSignatureing(field){
	var map = new HashMap();
	var SignatureID = field.SignatureID;
	map.put("DocumentID",field.DocuemntID);    
    map.put("SignatureID",SignatureID);
    map.put("flag","search");
    map.put("signflag","1");
    Rpc({functionId:'MB00004006',async:false,success:function(res){
    	var resultObj = Ext.decode(res.responseText);
    	var signaturetext = resultObj.signaturetext;
    	field.signaturetext = signaturetext;
    	if(signaturetext!=''&&signaturetext!=undefined)
    		showSignatureing(field);
    },scope:this},map);
}
/**
 *创建回显的签章对象
 *
 */
function showSignatureing(field){
	var pageid = field.PageID;
	var gridNo = field.GridNO;
	var nodeID = field.NodeID;
	var signDataid = null;
	var signDataname = null;
	var ctlName = 'signObj'+field.SignatureID;
	var pagedivid = 'tabdiv_tab_'+pageid;
	var signdivid = 'signObj_'+field.SignatureID;
	var pageDiv = document.getElementById(pagedivid);	
	var signDiv = document.createElement("div");
	signDiv.id = signdivid;
	pageDiv.appendChild(signDiv);
	//隐藏div存储执行签章完毕后的base64串
	var hiddendiv = document.createElement("input");
	signDataid = signDataname= "signData"+field.SignatureID;
	hiddendiv.setAttribute("id", signDataid);
	hiddendiv.setAttribute("name", signDataid);
	hiddendiv.setAttribute("type", "hidden");
	pageDiv.appendChild(hiddendiv);
	var signobjs = new SignObjsBjca(field.SignatureID,signDataid,signdivid,pagedivid,ctlName,pageid,field.pointx,field.pointy,'a_fld_'+pageid+'_'+gridNo+'_signObj',true);
	var map = new HashMap();
	map.put('objectname',ctlName);
	map.put('signobjs',signobjs);
	templateCard_me.signobjarr.push(map);
	signobjs.doSignBjca();
	signobjs.setSignBjca(field.signaturetext);
}
/**
 *设置回显签章显示
 *BJCA
 */
SignObjsBjca.prototype.setSignBjca=function(signaturetext){
	document.getElementById(this.signDataid).value = signaturetext;
    this.SignObj.Verify('hjsoft',signaturetext);
}
/**
 *检查是否是ie
 */
function checkIsIe(){
	if (!!window.ActiveXObject || 'ActiveXObject' in window) {
		return true;
	} else {
		return false;
	}
}

/**
 *加载签章(BJCA)
 */ 
function initBjcaSignatureXml(fieldvalue,pageid){
	var xmlrec = fieldvalue;
	xmlrec = replaceAll(xmlrec,"＜","<");
	xmlrec = replaceAll(xmlrec,"＞",">");
	xmlrec = replaceAll(xmlrec,"＇","'");
	xmlrec = replaceAll(xmlrec,"＂",'"');
	xmlrec = replaceAll(xmlrec,"&","");
	var XMLDoc = loadXMLString(xmlrec);
	XMLDoc.async = false;
	var rootNode = XMLDoc.documentElement;
	try{
		if(rootNode){
			var recordnode = rootNode.childNodes;
			for(var i=0;i<recordnode.length;i++){
				if(recordnode[i].getAttribute("DocuemntID")=='BJCA'){
					var itemNodes = recordnode[i].childNodes;
					for(var j=0;j<itemNodes.length;j++){
						var node = itemNodes[j];
						var pageID = node.getAttribute("PageID");
						var delflag = node.getAttribute("delflag");
						if(pageID!=pageid)
							continue;
						if(delflag=='true')
							continue;
						var field = {};
						field.UserName=node.getAttribute("UserName");
						field.SignatureID=node.getAttribute("SignatureID");
						field.pointx=parseInt(node.getAttribute("pointx"))-23;
						field.pointy=parseInt(node.getAttribute("pointy"))-70;
						field.PageID=node.getAttribute("PageID");
						field.GridNO=node.getAttribute("GridNO");
						field.NodeID=node.getAttribute("node_id");
						field.DocuemntID="BJCA";
						//加载签章
						initSignatureing(field);
					}
				}
			}
		}
	}catch(e){
		//Ext.showAlert(e.message);
	}
}