/**
 *金格科技签章生成(添加签章)
 */
function initJgkjSignature(setname,signaturename,PageID){
	var DocumentID = "";
	var username = setname.split("_")[0];
	var GridNO = signaturename.split("S")[1];
	var fields=templateCard_me.fieldSet.fields;//如果是签章且没有写权限,直接返回false,不执行签章操作。
	for(var num=0;num<fields.length;num++){
		var field=fields[num];
		if(field.uniqueId==("fld_"+PageID+"_"+GridNO)){
			if(field.rwPriv==0||field.rwPriv==1){
				return false;
			}
			break;
		}
	}
	//检查当前人在当前页的当前区域是否签过章  暂时不启用
	if(templateCard_me.signatureMap.get(PageID+"_"+GridNO)==username){
		//Ext.showAlert("签章区域不允许同一个人重复签章！");
		//return;
	}
	var mLength=document.getElementsByName("iHtmlSignature").length;
   	var signstr ="";
    for (var a=0;a<mLength;a++){
	    var vItem=document.getElementsByName("iHtmlSignature")[a];
	    if(signstr.indexOf(""+vItem.SignatureID)<0){//去掉重复的签章
	     	signstr+=vItem.SignatureID+",";
	    }
	}
	var obj_=eval(signaturename);//多个地方签章有问题，待修改（这个是得到div对象）
	var mx=0;
	var my=0;
	if(obj_){
	   var fc= obj_.firstChild;
	   mx=fc.style.left;  //获取X坐标值
	   my=fc.style.top;   //获取Y坐标值
	}else{
	}
	if((""+mx).indexOf("px"))
		mx = mx.substring(0,mx.length-2);
	if((""+my).indexOf("px"))
		my = my.substring(0,my.length-2);
	 	
	var obj = document.getElementById('SignatureControl'); 
	if(obj){
	}else{
		initJgkjSignObject();
		obj = document.getElementById('SignatureControl'); 
	}
	if(obj.ServiceUrl!=undefined){
		obj.FieldsList="HTJB=合同级别;XYBH=协议编号;BMJH=保密级别;JF=甲方签章;YF=乙方签章;HZNR=合作内容;QLZR=权利责任;CPMC=产品名称;DGSL=订购数量;DGRQ=订购日期"       //所保护字段
		obj.UserName="wjd";                         //文件版签章用户
		obj.PositionBySignType = 0;                 //设置签章所处位置，1表示中间,0表示左上角2表示右上角
		obj.DivId= signaturename;					//放到相应的div里
		obj.SaveHistory="false";
		obj.Position(mx,my);                        //签章位置
		var time=new Date();
		var lg=time.getTime();
		DocumentID=lg;                      
		if(templateCard_me.documentrecordID==''){//金格科技
			obj.DocumentID = DocumentID;
		}else{
			obj.DocumentID = templateCard_me.documentrecordID;
		}
		obj.SIGNATUREID = DocumentID;
		if(obj.RunSignature()){//执行签章操作  
		    mLength=document.getElementsByName("iHtmlSignature").length;
		    for (var a=0;a<mLength;a++){
		       vItem=document.getElementsByName("iHtmlSignature")[a];
		       if(signstr.indexOf(""+vItem.SignatureID)<0){
		        	 vItem.style.zIndex='1000';
				     signstr+=vItem.SignatureID+",";
				     var record=templateCard_me.getCurRecordSet();
				     var id = "";
				     if(record){
						var baespre="";
						var a0100="";
						for (var i=0;i<record.getFieldCount();i++){
							var valueItem =record.fields[i];
							if(templateCard_me.templPropety.infor_type=='1'){//人员
								if (valueItem.fldName=='basepre')
									baespre = valueItem.keyValue;
								if(valueItem.fldName=='a0100')
									a0100 = valueItem.keyValue;
							}else if(templateCard_me.templPropety.infor_type=='2'){//单位
								if (valueItem.fldName=='b0110')
									a0100 = valueItem.keyValue;
							}else if(templateCard_me.templPropety.infor_type=='3'){//岗位
								if (valueItem.fldName=='e01a1')
									a0100 = valueItem.keyValue;
							}
						}
						if(templateCard_me.templPropety.infor_type=='1')
							id = baespre+"|"+a0100;
						else
							id = a0100+"|"+a0100;
					 }
				     addJgkjDocumentid(vItem.DocumentID,vItem.SignatureID,mx,my,"",PageID,GridNO,id);
				     templateCard_me.signatureMap.put(PageID+"_"+GridNO,username);
		       }
		   }
		}
	}
}
/**
 *添加签章时添加xml
 */
function addJgkjDocumentid(DocuemntID,SignatureID,pointx2,pointy2,a0100s,PageID,GridNO,id){
	var signXml = templateCard_me.signXml;
	if(templateCard_me.signXml==''){
		signXml += "<?xml version=\"1.0\" encoding=\"GB2312\"?>";
		signXml += "<params>";
		signXml += "<record id=\""+id+"\" DocuemntID=\""+DocuemntID+"\">";
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
				if(recordnode[i].getAttribute("DocuemntID")!='BJCA'){
					if(recordnode[i].getAttribute("DocuemntID")!=''){//金格科技
						templateCard_me.documentrecordID = recordnode[i].getAttribute("DocuemntID");
					}else{
						templateCard_me.documentrecordID = DocuemntID;
					}
				}
				var newNode = XMLDoc.createElement("item");
				newNode.setAttribute('UserName','');
				newNode.setAttribute('SignatureID',""+SignatureID);
				newNode.setAttribute('pointx',""+pointx2+'px');
				newNode.setAttribute('pointy',""+pointy2+'px');
				newNode.setAttribute('PageID',""+PageID);
				newNode.setAttribute('GridNO',""+GridNO);
				newNode.setAttribute('node_id',""+templateCard_me.nodeId);//签章增加node_id属性
				recordnode[i].appendChild(newNode);
				templateCard_me.showSignatureId += SignatureID+',';
			}
		}
		templateCard_me.signXml = XMLDoc.xml;
	}
	catch(e){
		//Ext.showAlert(e.message);
	}
}
/**
 *保存前修改xml
 */
function updateJgkjDocumentid(){
	var mLength=document.getElementsByName("iHtmlSignature").length;
    var signstr ="";
    //var pageid="" ;
    for (var a=0;a<mLength;a++){
	      var vItem=document.getElementsByName("iHtmlSignature")[a];
	      if(signstr.indexOf(""+vItem.SignatureID)<0&&templateCard_me.showSignatureId.indexOf(""+vItem.SignatureID)!=-1){
	     		 signstr+=vItem.SignatureID+",";
	      }
	}
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
		if(recordnode[i].getAttribute("DocuemntID")!='BJCA'){//金格科技
			var item = recordnode[i].childNodes;
			var signstr2="";
			var  m = item.length;
			for(var j=0;j<m;j++){
			     var SignatureID = item[j].getAttribute("SignatureID");
				 for (var a=0;a<mLength;a++){		//更新移动位置
				      var vItem=document.getElementsByName("iHtmlSignature")[a];
				      var  pointx = vItem.style.left;
				      var pointy = vItem.style.top;
				      var mx = vItem.style.top;
				      if((""+mx).indexOf("px"))
						 mx = mx.substring(0,mx.length-2);
				      if(vItem.SignatureID==item[j].getAttribute("SignatureID")&&mx>0) {
					     	item[j].getAttributeNode("pointx").value=pointx;
					        item[j].getAttributeNode("pointy").value=pointy;
				      }
				  }
			}
			for(var j=0;j<m;j++){
		 		//维护删除的签章
		        signstr2+=item[j].getAttribute("SignatureID")+",";
		        var signature_ = item[j].getAttribute("SignatureID")+"";
		        if(signstr.indexOf(signature_)<0&&templateCard_me.showSignatureId.indexOf(signature_)!=-1){
		           //if(pageid==item[j].getAttribute("PageID")){
		               recordnode[i].removeChild(item[j]);
		               m = m-1;
		               continue;
		           //}
		        }
		    }
		}
	}
	templateCard_me.signXml = XMLDoc.xml;
}

//作用：显示或隐藏签章
function ShowJgkjSignature(visibleValue){
	templateCard_me.notShowSignatureIDs+=',';//记录无权限的签章
	templateCard_me.readOnlySignatureIDs+=',';//记录只读权限的签章
    var flag = ShowJgkjSignature2(visibleValue);
    if(flag ==true){
    	var mLength=document.getElementsByName("iHtmlSignature").length;
	    var signstr ="";
	    var signstr2 ="";
	    for (var i=0;i<mLength;i++){
	        var vItem=document.getElementsByName("iHtmlSignature")[i];
	        vItem.style.zIndex='1000';
	        if(templateCard_me.notShowSignatureIDs.indexOf(vItem.SignatureID+",")>-1){//无权限的签章不显示，把zIndex置为-1隐藏
		            vItem.Visiabled = "0";
		            vItem.ShowHint = false;
		            vItem.style.zIndex=-1;
	        }
			else if(templateCard_me.readOnlySignatureIDs.indexOf(vItem.SignatureID+",")>-1){//只读权限的签章设置不允许移动。
		            vItem.EnableMove=false;
	        }
	    }
	    return '0';
    }else{
    	return '1';
    }
}
    
//作用：获得documentid的所有签章
function ShowJgkjSignature2(visibleValue){
    //DeleteJgkjSignature();
    var obj = document.getElementById('SignatureControl'); 
    if(templateCard_me.signXml.length>0&&obj&&obj.ServiceUrl!=undefined){
    	obj.ShowSignature(visibleValue);
    	return true;
    }else{
    	return false;
    }
}
	
//作用：删除签章
function DeleteJgkjSignature(){
   var mLength=document.getElementsByName("iHtmlSignature").length; 
   var mSigOrder = "";
   for (var i=mLength-1;i>=0;i--){
       var vItem=document.getElementsByName("iHtmlSignature")[i];
       vItem.DeleteSignature();
   }
}

//作用：生成签章图片（打印，导出pdf）
function CreateJgkjSignatureJif(signatureType,object_id,info_type,outflag,flag){
	if(signatureType==0){
		var pageId= templateCard_me.getCurrPageId();
		var obj = document.getElementById('SignatureControl');
		if(templateCard_me.signXml.length>0&&obj&&obj.ServiceUrl!=undefined){
			var imLength=document.getElementsByName("iHtmlSignature").length;
		    var signstr ="";
		    for (var a=0;a<imLength;a++){
			      var vItem=document.getElementsByName("iHtmlSignature")[a];
			      if(signstr.indexOf(""+vItem.SignatureID)<0){
			     		 signstr+=vItem.SignatureID+",";
			      }
			}
			var templPropety = templateCard_me.templPropety;
			var map = new HashMap();
			initPublicParam(map,templPropety);
		    map.put("object_id",object_id);
		    map.put("info_type",info_type);
		    map.put("outflag",outflag);
		    map.put("signflag","0");
		    map.put("modeflag",flag);
		    Rpc({functionId:'MB00004006',async:false,success:function(res){
		    	var resultObj = Ext.decode(res.responseText);
				var signatureidarr = resultObj.documentidarr;
				var signaturelist = signatureidarr.split(',');
				for(var i=0;i<signaturelist.length;i++){
					var signatureid = signaturelist[i];
					if(signstr.indexOf(signatureid.split('_')[1])<0){
						if(signatureid.split('_')[2]!=pageId){
							Ext.getCmp('templcard_tabpanel').setActiveTab(parseInt(signatureid.split('_')[2]));
						}
	    				obj.ShowSignature(signatureid.split('_')[0]);
					}
				}
				Ext.getCmp('templcard_tabpanel').setActiveTab(parseInt(pageId));
				var mLength=document.getElementsByName("iHtmlSignature").length;
			    for (var i=0;i<mLength;i++){
			         var vItem=document.getElementsByName("iHtmlSignature")[i];
			         vItem.style.zIndex='1000';
			         if(signstr.indexOf(vItem.SignatureID)<0){
			         	var anaflag = analysisJgkjXml(vItem.SignatureID);
			         	if(anaflag)
			        		vItem.Visiabled = "0";
			         }
			         var mx = vItem.style.top;
			         if((""+mx).indexOf("px"))
					    mx = mx.substring(0,mx.length-2);
			         var  signatureid  = vItem.SignatureID;
			         vItem.SaveImage= "1";//可以获取IMAGEVALUE
			         if(flag=="0") 
			            vItem.SaveSignatureAsGif(""+signatureid,1,1,false,1);//金格签章升级SaveSignatureAsGif第二个参数不能传null
			         else if(flag=="1"){
			            vItem.SaveSignatureAsGif("C:\\Windows\\temp\\"+signatureid+".gif",1,1,true,"1");
			         }
			    }
		    },scope:this},map);
		}
	}
}
/**
 *金格科技签章生成（回显签章）
 */
function initJgkjSignatureXml(signxml,pageid){
	var obj = document.getElementById('SignatureControl');
	if(signxml.length>0&&obj&&obj.ServiceUrl!=undefined){
		//DeleteJgkjSignature();
		signxml = replaceXml(signxml);
		var XMLDoc = loadXMLString(signxml);
		XMLDoc.async = false;
		var rootNode = XMLDoc.documentElement;
		var recordnode = rootNode.childNodes;
		var notShowSignatureIDs='';//记录无权限的签章
		var readOnlySignatureIDs='';//记录只读权限的签章
		for(var i=0;i<recordnode.length;i++){
			var cid = recordnode[i].getAttribute("DocuemntID");
			if(cid!=""&&cid!="BJCA"){
				templateCard_me.documentrecordID = cid;
				var item = recordnode[i].childNodes;
				var isShow = false;
				for(var j=0;j<item.length;j++){
					var isHaveRwPriv=true;
					var isReadOnly=false;
					var pid = item[j].getAttribute("PageID");
					var gridno = item[j].getAttribute("GridNO");
					var username = item[j].getAttribute("UserName");
					var SignatureID = item[j].getAttribute("SignatureID");
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
						if(isReadOnly){
							templateCard_me.readOnlySignatureIDs+=','+SignatureID;
						}
						if(!isShow){
							isShow = true;
							templateCard_me.showSignatureId += SignatureID+',';
						}
					}
				}
					ShowJgkjSignature(cid);
			}
		}
	}
}

/**
*创建金格签章对象
*/
function initJgkjSignObject(){
	var SignatureControl=document.getElementById('SignatureControl');
	if(SignatureControl){
		SignatureControl.parentNode.removeChild(SignatureControl);
		createJgkjSignObject();
	}else{
		createJgkjSignObject();
	}
}
/**
*创建金格签章对象
*/
function createJgkjSignObject(){
	var signObjdiv = document.createElement("div");
	var innerHtml = '<OBJECT id="SignatureControl" classid="clsid:D85C89BE-263C-472D-9B6B-5264CD85B36E" codebase="/iSignatureHTML/iSignatureHTML.cab#version=8,2,2,66" width=0 height=0 VIEWASTEXT>';
	innerHtml+='<param name="ServiceUrl" value="'+templateCard_me.mServerUrl+'">';//读取数据库相关信息
	innerHtml+='<param name="WebAutoSign" value="0">';//是否自动数字签名(0:不启用，1:启用)
	innerHtml+='<param name="PrintControlType" value="2">';//打印控制方式（0:不控制  1：签章服务器控制  2：开发商控制)
	innerHtml+='<param name="PrintWater" value="true">';//是否打印水印
	innerHtml+='<param name="MenuDocVerify" value="true">';//菜单验证文档
	if(templateCard_me.templPropety.approve_flag=="1"){
		innerHtml+='<param name="MenuServerVerify" value="false">';//菜单在线验证
		innerHtml+='<param name="MenuDigitalCert" value="false">';//菜单数字签名
		innerHtml+='<param name="MenuDocLocked" value=false>';//菜单文档锁定
		innerHtml+='<param name="MenuDeleteSign" value=true>';//菜单撤消签章
		innerHtml+='<param name="MenuMoveSetting" value="true">';//菜单禁止移动
	}else{
		innerHtml+='<param name="MenuServerVerify" value="false">';//菜单在线验证
		innerHtml+='<param name="MenuDigitalCert" value="false">';//菜单数字签名
		innerHtml+='<param name="MenuDocLocked" value=false>';//菜单文档锁定
		innerHtml+='<param name="MenuDeleteSign" value=false>';//菜单撤消签章
		innerHtml+='<param name="MenuMoveSetting" value="false">';//菜单禁止移动
	}
	innerHtml+='</OBJECT>';
	signObjdiv.innerHTML = innerHtml;
	document.body.appendChild(signObjdiv);
}

function analysisJgkjXml(vdocumentid){
    var signXml = templateCard_me.signXml;
	if(templateCard_me.signXml!=''){
		var flag = true;
		signXml = replaceXml(signXml);
		var XMLDoc = loadXMLString(signXml);  
		XMLDoc.async=false;
		var rootNode = XMLDoc.documentElement;
		var recordnode = rootNode.childNodes;
		for(var i=0;i<recordnode.length;i++){
			var cid = recordnode[i].getAttribute("DocuemntID");
			/*if(recordnode[i].getAttribute("DocuemntID")!='BJCA'){//金格科技
				var DocuemntID = recordnode[i].getAttribute("DocuemntID");
				if(vdocumentid==DocuemntID){
					flag = false;
					break;
				}
				else if(vdocumentid!=DocuemntID){
					flag = true;
				}
			}*/
			if(cid!=""&&cid!="BJCA"){
				var item = recordnode[i].childNodes;
				for(var j=0;j<item.length;j++){
					var SignatureID = item[j].getAttribute("SignatureID");
					if(vdocumentid==SignatureID){
						flag = false;
						break;
					}
					else if(vdocumentid!=SignatureID){
						flag = true;
					}
				}
			}
		}
		return flag;
	}
}

function replaceXml(signXml){
	signXml=replaceAll(signXml,"＜","<");
	signXml=replaceAll(signXml,"＞",">");
	signXml=replaceAll(signXml,"＇","'");
	signXml=replaceAll(signXml,"＂",'"');
	signXml=replaceAll(signXml,"&","");
	return signXml;
}