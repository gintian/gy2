/**
 *卡片页面元素双击触发事件
 */
function DoMouseSignature(paramVar,signflag){
	var isie = Ext.isIE;
	if(!isie&&signflag!='2'&&signflag!='3'){
		Ext.Msg.alert('提示','此功能只支持IE浏览器');
		return;
	}
	var paramarr = paramVar.split('|');
	if (templateCard_me.object_id==null||templateCard_me.object_id==""){
		return;
	}
	if(signflag=='1'){//BJCA
		var divName = paramarr[0];
		var pageId = paramarr[1];
		var cellId = paramarr[2];
		var number = Math.floor(Math.random()*10000000000);
		var pageid = pageId;
		var SignatureID = 0;
		var x,y;
		var signDataid = null;
		var signDataname = null;
		var ctlName = 'signObj'+number+'';
		var pagedivid = 'tabdiv_tab_'+pageid;
		
		var signdivid = 'signObj_'+number+'';
		/*var aa = document.getElementById(signdivid);
		if (aa!=null&&aa.innerHTML!='') {
			alert("已经签过章了！");
			return;
		}*/
		var pageDiv = document.getElementById(pagedivid);
		var signDiv = document.createElement("div");
		signDiv.id = signdivid;	
		/*var pageStyle = Ext.query("div[class='pageStyle']",true,pageDiv);
		if(pageStyle.length>0){
			var cssText = pageStyle[0].style.cssText;
			signDiv.style.cssText = cssText;
		}*/
		pageDiv.appendChild(signDiv);
		//隐藏div存储执行签章完毕后的base64串
		var hiddendiv = document.createElement("input");
		signDataid = signDataname= "signData"+number+'';
		hiddendiv.setAttribute("id", signDataid);
		hiddendiv.setAttribute("name", signDataid);
		hiddendiv.setAttribute("type", "hidden");
		pageDiv.appendChild(hiddendiv);
		var cellDiv = document.getElementById(cellId);
		var pos = GetObjPos(cellDiv);
		if(templateMain_me.templPropety.card_view_type=='1')//自助
			x = pos.x;
		else
			x = pos.x-300;
		y = pos.y-200;
		var signobjs = new SignObjsBjca(SignatureID,signDataid,signdivid,pagedivid,ctlName,pageid,x,y,divName,false);
		var map = new HashMap();
		map.put('objectname',ctlName);
		map.put('signobjs',signobjs);
		templateCard_me.signobjarr.push(map);
		signobjs.doSignBjca();
	}
	else if(signflag=='0'){//金格科技
		var setname = paramarr[0];
		var signaturename = paramarr[1];
		var PageID = paramarr[2];
		initJgkjSignature(setname,signaturename,PageID);
	}
	else if(signflag=='2'){
		var setname = paramarr[0];
		var signaturename = paramarr[1];
		var PageID = paramarr[2];
		initSignature(setname,signaturename,PageID,"1");
	}
}
/**
 *加载签章
 */
function initSignRecord(pageId){
	var isie = Ext.isIE;
	if(isie&&templateCard_me.signatureType==1){//BJCA
		initBjcaSignatureXml(templateCard_me.signXml,pageId);
	}else if(isie&&templateCard_me.signatureType==0){//金格科技
		initJgkjSignObject();
		initJgkjSignatureXml(templateCard_me.signXml,pageId);
	}else if(templateCard_me.signatureType==2){
		initSignatureXml(templateCard_me.signXml,pageId);
	}else if(templateCard_me.signatureType==3){
		initJgkjSignatureXmlHtml5(templateCard_me.signXml,pageId);
	}
}
/**
 * 鼠标滑过区域显示浮动菜单
 * @param paramVar
 * @param signflag
 * @returns
 */
function DoMouseSignatureOver(paramVar,signflag){
	var paramarr = paramVar.split('|');
	var setname = paramarr[0];
	var signaturename = paramarr[1];
	var div = document.getElementById(signaturename);
	var width = parseInt(div.children[0].style.width+0);
	var PageID = paramarr[2];
	var GridNO = signaturename.split("S")[1];
	var fields=templateCard_me.fieldSet.fields;
	for(var num=0;num<fields.length;num++){
		var field=fields[num];
		if(field.uniqueId==("fld_"+PageID+"_"+GridNO)){
			if(field.rwPriv==0||field.rwPriv==1){
				return false;
			}
			break;
		}
	}
	if(signflag=='2'){
		var signatureimg_id = "fld_"+PageID+"_"+GridNO+"_signid";
		var signatureimg = document.getElementById(signatureimg_id); 
		if(signatureimg){
			return false;
		}
	}
	if(templateCard_me.object_id==''){
		return false;
	}
	var fldid = "fld_"+PageID+"_"+GridNO;
	var menuid = "fld_"+PageID+"_"+GridNO+"_menu";
	var left = width/2-50;
	if(width<100){
		left = 0;
	}
	if(!Ext.getCmp(menuid)){
		var individual = {
		        text: MB.LABEL.INDIVIDUALSIGNATURE,
		        icon:'/images/new_module/individual.png',
		        handler:function(){
		        	if(signflag=='2'){
		        		initSignature(setname,signaturename,PageID,"1");
		        	}else if(signflag=='3'){
		        		initJgkjSignatureHtml5(setname,signaturename,PageID,"1");
		        	}
			    }
		    };
		var batch = {
		        text: MB.LABEL.BATCHSIGNATURE,
		        icon:'/images/new_module/batch.png',
		        handler:function(){
		        	var tablePanel=null;
		        	if(templateMain_me.templPropety.view_type=="list"){
		        		tablePanel=templateList_me.templateListGrid.tablePanel;
		        	}else{
		    	    	tablePanel=templateCard_me.personListGrid.tablePanel;
		        	}
		    		var selectRecord = tablePanel.getSelectionModel().getSelection();
		    		if(selectRecord.length<1){
		    			var text = MB.LABEL.PERSON;
		    			if(templateMain_me.templPropety.infor_type=='2'){
		    				text = MB.LABEL.UNIT;
		    			}else if(templateMain_me.templPropety.infor_type=='3'){
		    				text = MB.LABEL.STATION;
		    			}
		    			Ext.showAlert(MB.LABEL.SELECTNEEDBATCHSIGNATURE+text+"！");
		    			return;
		    		}
		    		if(signflag=='2'){
		    			initSignature(setname,signaturename,PageID,"2");
		    		}else if(signflag=='3'){
		        		initJgkjSignatureHtml5(setname,signaturename,PageID,"2");
		        	}
			    }
		    };
		var del = {
				text: MB.LABEL.CHEXIAOSIGNATURE,
				icon:'',
				handler:function(){
					deleteSignatureHtml5(signaturename,PageID);
				}
		}
		var items = [];
		if(templateMain_me.templPropety.module_id=='9'&&templateMain_me.ins_id=='0'){
			items.push(individual);
		}else{
			items.push(individual);
			items.push(batch);
		}
		if(signflag=='3'){
			//items.push(del);
		}
		Ext.create('Ext.menu.Menu', {
			id:menuid,
		    width: 100,
		    margin: '0 0 10 0',
		    floating: false,  
		    renderTo: fldid, 
		    style:'left:'+left+'px;top:0px;position:relative;z-index:1000',
		    items: items
		});
	}
}
/**
 * 鼠标离开签章区域删除浮动菜单
 * @param pageid
 * @param gridno
 * @returns
 */
function DoMouseSignatureLeave(pageid,gridno){
	var menuid = "fld_"+pageid+"_"+gridno+"_menu";
	if(Ext.getCmp(menuid)){
		Ext.getCmp(menuid).close();
	}
}