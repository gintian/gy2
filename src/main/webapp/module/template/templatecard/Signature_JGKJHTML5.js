/**
 *金格科技签章生成(添加签章)
 */
function initJgkjSignatureHtml5(setname,signaturename,PageID,flag){
	var GridNO = signaturename.split("S")[1];
	var positionid = "fld_"+PageID+"_"+GridNO;
	for(var key in templateCard_me.signature_fldid){
	  if(templateCard_me.signature_fldid[key]==positionid){
	  }
	}
	if(templateCard_me.keysn==''){
		Ext.showAlert("业务用户必须绑定自助用户，并设置签章密钥key，才可进行签章！");
		return;
	}
	var username = setname.split("_")[0];
	if(templateCard_me.documentid!=""){
		DocumentID = templateCard_me.documentid;
	}else{
		var time=new Date();
		var lg=time.getTime();
		var DocumentID=lg;
		templateCard_me.documentid = DocumentID;
	}

	if(!templateCard_me.signatureInit){
		templateCard_me.Signature = Signature;
		templateCard_me.Signature.init({//初始化属性
		  keysn:templateCard_me.keysn,
		  delCallBack: delCB,
		  imgtag: 0, //签章类型：0：无; 1:公章; 2:私章; 3:法人章; 4:法人签名; 5:手写签名
		  moveable: true,
		  timestamp: true,  //获取签章服务器时间
		  signdate : {ischeck: true, fontFormat:'yyyy/MM/dd hh:mm:ss',fontFamily:'楷体',fontSize:12,fontColor:'#000000',position:'居中'},
		  moveable_self : true,//true:是否判断只有自己加盖的印章才可以移动  缺省不做判断
		  valid : false,    //签章和证书有效期判断， 缺省不做判断
	      icon_move : true, //移动签章按钮隐藏显示，缺省显示
	      icon_remove : true, //撤销签章按钮隐藏显示，缺省显示
	      icon_sign : true, //数字签名按钮隐藏显示，缺省显示
	      icon_signverify : true, //签名验证按钮隐藏显示，缺省显示
	      icon_sealinfo : true, //签章验证按钮隐藏显示，缺省显示
	      certType : 'server',//设置证书在签章服务器
	      sealType : 'server',//设置印章从签章服务器取
	      serverUrl : templateCard_me.serverUrlForHtml5,//
	      documentid:DocumentID,//设置文档ID
	      documentname:DocumentID,//设置文档名称
	      pw_timeout:'s1800', //s：秒；h:小时；d:天
	      cache_path:'/app/netarb/cietacweb_war.ear/cietacweb.war',
		  scaleImage: 1.0 //签章图片的缩放比例
		})
		templateCard_me.signatureInit = true;
	}
	
	var mx=0;
	var my=0;	
	var signatureCreator = templateCard_me.Signature.create();
		var that = this;
		var protectedItems = signatureCreator.run({
			protectedItems : [],//设置定位页面DOM的id，自动查找ID，自动获取保护DOM的kg-desc属性作为保护项描述，value属性为保护数据。不设置，表示不保护数据，签章永远有效。
			position : positionid,//设置盖章定位dom的ID，必须设置
			autoCert : false,
			okCall : function(fn, image) {//点击确定后的回调方法，this为签章对象 ,签章数据撤销时，将回调此方法，需要实现签章数据持久化（保存数据到后台数据库）,保存成功后必须回调fn(true/false)渲染签章到页面上
				fn(true);
				var signatureId = this.getSignatureid();
				var signatureData = this.getSignatureData();
				//将签章数据保存到数据库表中
				saveSignatureToServer(DocumentID,signatureId,signatureData,"add");
				//将数据添加到xml
				var record=templateCard_me.getCurRecordSet();
			    var image = document.getElementById("kg-img-"+signatureId);
			    var imgheight = parseInt(image.style.height+0);
			    var imgwidth = parseInt(image.style.width+0);
				addJgkjDocumentid(DocumentID,signatureId,mx,my,"",PageID,GridNO,imgheight,imgwidth,flag);
				templateCard_me.signatureMap.put(PageID+"_"+GridNO,username);
				signatureCreator.saveSignature(DocumentID, signatureId, signatureData);
				//将签章保存成图片到本地
				var base64 = signatureCreator.toBase64Img(this.signatureData.seal);//转base64
				base64toImage(base64,signatureId,DocumentID);
				templateCard_me.signature_fldid.put(signatureId,positionid);
				var menuid = "fld_"+PageID+"_"+GridNO+"_menu";
				if(Ext.getCmp(menuid)){
					Ext.getCmp(menuid).close();
				}
				if(flag=='2'){
					batchSignatureHtml5(DocumentID,signatureId);
				}
			},
			cancelCall : function() {//点击取消后的回调方法
				//console.log("取消！")
			}
		});
		templateCard_me.Signature.bind({
			remove:function(fn){//签章数据撤销时，将回调此方法，需要实现签章数据持久化（保存数据到后台数据库）,
				fn(true);//保存成功后必须回调fn(true/false)传入true/false分别表示保存成功和失败
			},
			update:function(fn){//签章数据有变动时，将回调此方法，需要实现签章数据持久化（保存数据到后台数据库）,执行后必须回调fn(true/false)，传入true/false分别表示保存成功和失败
				fn(true);
				saveSignatureToServer(templateCard_me.documentid,this.getSignatureid(),this.getSignatureData(),"update");
				signatureCreator.saveSignature(templateCard_me.documentid, this.getSignatureid(), this.getSignatureData());
				var marginLeft = this.signatureData.position[positionid].marginLeft;
				var marginTop = this.signatureData.position[positionid].marginTop;
				updateXml(templateCard_me.documentid,this.getSignatureid(),marginLeft,marginTop);
			}
		});
}
/**
 * 批量签章
 * @param DocumentID
 * @param signatureId
 * @returns
 */
function batchSignatureHtml5(DocumentID,signatureId){
	var map = new HashMap();
	map.put("object_id" , templateCard_me.object_id);
	map.put("cur_task_id" , templateCard_me.cur_task_id);
	map.put("cur_ins_id" , templateCard_me.cur_ins_id);
	map.put("tab_id",templateMain_me.templPropety.tab_id); 
	map.put("task_id",templateMain_me.templPropety.task_id);
	map.put("infor_type",templateMain_me.templPropety.infor_type);
	map.put("module_id",templateMain_me.templPropety.module_id);
	map.put("allNum",templateTool_me.getTotalCount());
	map.put("signxml",templateCard_me.signXml);
	map.put("DocumentID",DocumentID+"");
	map.put("signatureId",signatureId);
	Rpc({functionId:'MB00007005',async:false,success:function(form,action){
		var result = Ext.decode(form.responseText);
	}},map);
}
/**
 * 将签章保存成图片
 * @param base64
 * @param signatureId
 * @param DocumentID
 * @returns
 */
function base64toImage(base64,signatureId,DocumentID){
	var templPropety = templateCard_me.templPropety;
	var map = new HashMap();
	initPublicParam(map,templPropety);
    map.put("base64",base64);
    map.put("DocumentID",DocumentID+"");
    map.put("signatureId",signatureId);
    map.put("signflag","0");
    map.put("flag","0");
    map.put("solveflag","image");
    Rpc({functionId:'MB00004006',async:false,success:function(res){},scope:this},map);
}
/**
 * 更新xml
 * @param DocumentID
 * @param signatureId
 * @param x
 * @param y
 * @returns
 */
function updateXml(DocumentID,signatureId,x,y){
    var signXml = templateCard_me.signXml;
	if(templateCard_me.signXml==''){
		signXml += "<?xml version=\"1.0\" encoding=\"GB2312\"?>";
		signXml += "<params>";
		signXml += "</params>";
	}
	signXml = replaceXml(signXml);
	var XMLDoc = loadXMLString(signXml);  
	XMLDoc.async=false;
	var rootNode = XMLDoc.documentElement;
	var recordnode = rootNode.childNodes;
	for(var i=0;i<recordnode.length;i++){
		if(recordnode[i].nodeType==1){
			if(recordnode[i].getAttribute("DocuemntID")==DocumentID){
				var item = recordnode[i].childNodes;
				var  m = item.length;
				for(var j=0;j<m;j++){
					if(item[j].nodeType==1){
						var SignatureID = item[j].getAttribute("SignatureID");
					     if(signatureId==item[j].getAttribute("SignatureID")) {
						     item[j].getAttributeNode("pointx").value=x;
						     item[j].getAttributeNode("pointy").value=y;
					     }
					}
				}
			}
		}
	}
	templateCard_me.signXml = XMLtoString(XMLDoc);
}
/**
 * 撤销签章
 * @param signatureid
 * @param signatureData
 * @returns
 */
function delCB(signatureid, signatureData) {
	for (var key in templateCard_me.Signature.list) {
		if (signatureid == key && Signature.list[signatureid].keysn == signatureData.keysn) {
			var signatureCreator = templateCard_me.Signature.create();
			delJgkjDocumentid(signatureData.documentid,signatureid,signatureCreator,signatureData,'0');
			templateCard_me.signature_fldid.put(signatureid,'');
			break;
		}
	}
	return true;
}
/**
 * 清除签章
 * @returns
 */
function HideSignatureHtml5(){
	var signXml = templateCard_me.signXml;
	if(templateCard_me.signXml==''){
		signXml += "<?xml version=\"1.0\" encoding=\"GB2312\"?>";
		signXml += "<params>";
		signXml += "</params>";
	}
	signXml = replaceXml(signXml);
	var XMLDoc = loadXMLString(signXml);  
	XMLDoc.async=false;
	var rootNode = XMLDoc.documentElement;
	var recordnode = rootNode.childNodes;
	for(var i=0;i<recordnode.length;i++){
		if(recordnode[i].nodeType==1){
			if(recordnode[i].getAttribute("DocuemntID")==templateCard_me.documentid){
				var item = recordnode[i].childNodes;
				var  m = item.length;
				for(var j=0;j<m;j++){
					if(item[j].nodeType==1){
						var SignatureID = item[j].getAttribute("SignatureID");
						if(templateCard_me.Signature){
							for(var key in templateCard_me.Signature.list){
								if(SignatureID==key){
									var signature =  templateCard_me.Signature.list[key];
									if(signature)
										signature.hide();
								}
							}
						}
					}
				}
			}
		}
	}
}
/**
 * 撤销签章-更新xml
 * @param documentid
 * @param signatureid
 * @returns
 */
function delJgkjDocumentid(documentid,signatureid,signatureCreator,signatureData,flag,GridNO,PageID){
	var signXml = templateCard_me.signXml==undefined?"":templateCard_me.signXml;
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
				if(recordnode[i].nodeType==1){
					if(recordnode[i].getAttribute("DocuemntID")==documentid){
						var itemNodes = recordnode[i].childNodes;
						for(var j=0;j<itemNodes.length;j++){
							if(itemNodes[j].nodeType==1){
								var node = itemNodes[j];
								var UserName = node.getAttribute("UserName");
								var pageID = node.getAttribute("PageID");
								var gridNO = node.getAttribute("GridNO");
								if(node.getAttribute("SignatureID")==signatureid){
									if((flag=='1'&&pageID+"_"+gridNO==PageID+"_"+GridNO&&UserName==templateCard_me.currentUsername)||flag=='0'){
										var batch_flag = node.getAttribute("batch_flag");
										if(batch_flag=='true'){//批量的签章，只打标记不删除数据
											
										}else{
											signatureCreator.removeSignature(signatureData.documentid, signatureid);
											saveSignatureToServer(signatureData.documentid,signatureid,"","del");
										}
										var newAtt = XMLDoc.createAttribute("delflag");
										newAtt.nodeValue ="true";
										node.setAttributeNode(newAtt);
									}
								}
							}
						}
					}
				}	
			}
		}
		templateCard_me.signXml = XMLtoString(XMLDoc);
	}catch(e){
		
	}
}
/**
 * 将签章信息保存到表中
 * @param DocumentID
 * @param signatureId
 * @param signatureData
 * @param solveflag
 * @returns
 */
function saveSignatureToServer(DocumentID,signatureId,signatureData,solveflag){
	var templPropety = templateCard_me.templPropety;
	var map = new HashMap();
	initPublicParam(map,templPropety);
    map.put("DocumentID",DocumentID+"");
    map.put("signatureId",signatureId);
    map.put("signatureData",signatureData);
    map.put("signflag","0");
    map.put("flag","0");
    map.put("solveflag",solveflag);
    Rpc({functionId:'MB00004006',async:false,success:function(res){},scope:this},map);
}

/**
 *添加签章时添加xml
 */
function addJgkjDocumentid(DocuemntID,SignatureID,pointx2,pointy2,a0100s,PageID,GridNO,imgheight,imgwidth,flag){
	var signXml = templateCard_me.signXml==undefined?"":templateCard_me.signXml;
	if(templateCard_me.signXml==''){
		signXml += "<?xml version=\"1.0\" encoding=\"GB2312\"?>";
		signXml += "<params>";
		signXml += "<record DocuemntID=\""+DocuemntID+"\">";
		signXml += " </record>";
		signXml += " </params>";
	}
	signXml = replaceXml(signXml);
	var XMLDoc = loadXMLString(signXml);  
	XMLDoc.async=false;
	var rootNode = XMLDoc.documentElement;
	try{
		if(rootNode){
			var recordnode = rootNode.childNodes;
			for(var i=0;i<recordnode.length;i++){
				if(recordnode[i].nodeType==1){
					if(recordnode[i].getAttribute("DocuemntID")==DocuemntID){
						var newNode = XMLDoc.createElement("item");
						newNode.setAttribute('UserName',templateCard_me.currentUsername);
						newNode.setAttribute('SignatureID',""+SignatureID);
						newNode.setAttribute('pointx',""+pointx2+'px');
						newNode.setAttribute('pointy',""+pointy2+'px');
						newNode.setAttribute('PageID',""+PageID);
						newNode.setAttribute('GridNO',""+GridNO);
						newNode.setAttribute('height',""+imgheight);
						newNode.setAttribute('width',""+imgwidth);
						newNode.setAttribute('node_id',""+templateCard_me.nodeId);
						newNode.setAttribute('batch_flag',flag=='2'?'true':'false');
						recordnode[i].appendChild(newNode);
					}else{
						var newRecord = XMLDoc.createElement("record");
						newRecord.setAttribute('id',id);
						newRecord.setAttribute('DocuemntID',DocuemntID);
						var newNode = XMLDoc.createElement("item");
						newNode.setAttribute('UserName',templateCard_me.currentUsername);
						newNode.setAttribute('SignatureID',""+SignatureID);
						newNode.setAttribute('pointx',""+pointx2+'px');
						newNode.setAttribute('pointy',""+pointy2+'px');
						newNode.setAttribute('PageID',""+PageID);
						newNode.setAttribute('GridNO',""+GridNO);
						newNode.setAttribute('height',""+imgheight);
						newNode.setAttribute('width',""+imgwidth);
						newNode.setAttribute('node_id',""+templateCard_me.nodeId);
						newNode.setAttribute('batch_flag',flag=='2'?'true':'false');
						newRecord.appendChild(newNode);
						rootNode.appendChild(newRecord);
					}
				}
			}
		}
		templateCard_me.signXml = XMLtoString(XMLDoc);
	}
	catch(e){
		
	}
}
/**
 *金格科技签章生成（回显签章）
 */
function initJgkjSignatureXmlHtml5(signxml,pageid){
	if(signxml.length>0){
		signxml = replaceXml(signxml);
		var XMLDoc = loadXMLString(signxml);
		XMLDoc.async = false;
		var rootNode = XMLDoc.documentElement;
		var recordnode = rootNode.childNodes;
		var notShowSignatureIDs='';
		var readOnlySignatureIDs='';
		for(var i=0;i<recordnode.length;i++){
			if(recordnode[i].nodeType==1){
				var cid = recordnode[i].getAttribute("DocuemntID");
				if(cid!=""&&cid!="BJCA"){
					templateCard_me.documentrecordID = cid;
					var page_id,grid_no;
					var item = recordnode[i].childNodes;
					for(var j=0;j<item.length;j++){
						if(item[j].nodeType==1){
							var isHaveRwPriv=true;
							var isReadOnly=false;
							var pid = item[j].getAttribute("PageID");
							var gridno = item[j].getAttribute("GridNO");
							var username = item[j].getAttribute("UserName");
							var SignatureID = item[j].getAttribute("SignatureID");
							var delflag = item[j].getAttribute("delflag");
							if(delflag=='true'){
								
							}else{
								var fields=templateCard_me.fieldSet.fields;
								for(var num=0;num<fields.length;num++){
									var field=fields[num];
									if(field.uniqueId==("fld_"+pid+"_"+gridno)){
										if(field.rwPriv==0)
											isHaveRwPriv=false;
										else if(field.rwPriv==1)
											isReadOnly=true;
										break;
									}
								}
								if(templateCard_me.signatureMap.get(pid+"_"+gridno)){}
								else
									templateCard_me.signatureMap.put(pid+"_"+gridno,username);
								if(pageid==pid){
									if(!isHaveRwPriv){
										templateCard_me.notShowSignatureIDs+=','+SignatureID;
									}
									else if(isReadOnly){
										templateCard_me.readOnlySignatureIDs+=','+SignatureID;
									}
									page_id=pid;
									grid_no=gridno;
									showSignatureWithPriv(cid,page_id,grid_no,SignatureID);
								}
							}
						}
					}
				}
			}
		}
	}
}
function showSignatureWithPriv(cid,page_id,grid_no,SignatureID){
	templateCard_me.notShowSignatureIDs+=',';
	templateCard_me.readOnlySignatureIDs+=',';
    if(templateCard_me.notShowSignatureIDs.indexOf(SignatureID+",")>-1){
            
    }
	else if(templateCard_me.readOnlySignatureIDs.indexOf(SignatureID+",")>-1){
		showSignature(cid,page_id,grid_no,SignatureID,false);
    }else{
    	showSignature(cid,page_id,grid_no,SignatureID,true);
    }
}
/**
 * 回显签章
 * @param cid
 * @param page_id
 * @param grid_no
 * @param SignatureID
 * @returns
 */
function showSignature(cid,page_id,grid_no,SignatureID,moveflag) {
	if(templateCard_me.documentid==""){
		templateCard_me.documentid = cid;
	}
	if(!templateCard_me.signatureInit){
		templateCard_me.Signature = Signature;
		templateCard_me.Signature.init({//初始化属性
		  keysn:templateCard_me.keysn,
		  delCallBack: delCB,
		  imgtag: 0, //签章类型：0：无; 1:公章; 2:私章; 3:法人章; 4:法人签名; 5:手写签名
		  moveable: true,
		  timestamp: true,  //获取签章服务器时间
		  signdate : {ischeck: true, fontFormat:'yyyy/MM/dd hh:mm:ss',fontFamily:'楷体',fontSize:12,fontColor:'#000000',position:'居中'},
		  moveable_self : true,//true:是否判断只有自己加盖的印章才可以移动  缺省不做判断
		  valid : false,    //签章和证书有效期判断， 缺省不做判断
	      icon_move : true, //移动签章按钮隐藏显示，缺省显示
	      icon_remove : true, //撤销签章按钮隐藏显示，缺省显示
	      icon_sign : true, //数字签名按钮隐藏显示，缺省显示
	      icon_signverify : true, //签名验证按钮隐藏显示，缺省显示
	      icon_sealinfo : true, //签章验证按钮隐藏显示，缺省显示
	      certType : 'server',//设置证书在签章服务器
	      sealType : 'server',//设置印章从签章服务器取
	      serverUrl : templateCard_me.serverUrlForHtml5,//
	      documentid:templateCard_me.documentid,//设置文档ID
	      documentname:templateCard_me.documentid,//设置文档名称
	      pw_timeout:'s1800', //s：秒；h:小时；d:天
	      cache_path:'/app/netarb/cietacweb_war.ear/cietacweb.war',
		  scaleImage: 1.0 //签章图片的缩放比例
		})
		templateCard_me.signatureInit = true;
	}
	var signatureCreator = templateCard_me.Signature.create();
	//查询documentid 对应的签章数据
    var templPropety = templateCard_me.templPropety;
	var map = new HashMap();
	initPublicParam(map,templPropety);
    map.put("DocumentID",templateCard_me.documentid+"");
    map.put("flag","1");
    map.put("signflag","0");
    map.put("signatureId",SignatureID);
    Rpc({functionId:'MB00004006',async:false,success:function(res){
    	var resultObj = Ext.decode(res.responseText);
    	var signaturetext = resultObj.signaturetext;
    	var signatureid = resultObj.signatureid;
    	var signdata = new Array();
    	var map = {};
    	var extra = {};
    	var positionid = "fld_"+page_id+"_"+grid_no;
    	templateCard_me.signature_fldid.put(signatureid,positionid);
    	if(!moveflag){
    		extra.icon_move = function(){return false;};
        	map.extra = extra;
    	}
        map.signatureid = signatureid;
        map.signatureData = signaturetext;
        signdata.push(map);
        templateCard_me.Signature.loadSignature(signatureid,signaturetext);
        var signature =  templateCard_me.Signature.list[signatureid];
        if(signature)
        	signature.show();
    },scope:this},map);
    templateCard_me.Signature.bind({
		remove:function(fn){//签章数据撤销时，将回调此方法，需要实现签章数据持久化（保存数据到后台数据库）,
			fn(true);//保存成功后必须回调fn(true/false)传入true/false分别表示保存成功和失败
		},
		update:function(fn){//签章数据有变动时，将回调此方法，需要实现签章数据持久化（保存数据到后台数据库）,执行后必须回调fn(true/false)，传入true/false分别表示保存成功和失败
			fn(true);
			saveSignatureToServer(templateCard_me.documentid,this.getSignatureid(),this.getSignatureData(),"update");
			signatureCreator.saveSignature(templateCard_me.documentid, this.getSignatureid(), this.getSignatureData());
			var positionid = templateCard_me.signature_fldid.get(this.getSignatureid());
			var marginLeft = this.signatureData.position[positionid].marginLeft;
			var marginTop = this.signatureData.position[positionid].marginTop;
			updateXml(templateCard_me.documentid,SignatureID,marginLeft,marginTop);
		}
	});
}

function replaceXml(signXml){
	signXml=replaceAll(signXml,"＜","<");
	signXml=replaceAll(signXml,"＞",">");
	signXml=replaceAll(signXml,"＇","'");
	signXml=replaceAll(signXml,"＂",'"');
	signXml=replaceAll(signXml,"&","");
	return signXml;
}

function deleteSignatureHtml5(signaturename,PageID){
	var GridNO = signaturename.split("S")[1];
	for (var key in templateCard_me.Signature.list) {
		if (Signature.list[key].keysn == templateCard_me.keysn) {
			var signatureCreator = templateCard_me.Signature.create();
			delJgkjDocumentid(templateCard_me.documentid,key,signatureCreator,Signature.list[key].signatureData,'1',GridNO,PageID);
			templateCard_me.signature_fldid.put(key,'');
		}
	}
}