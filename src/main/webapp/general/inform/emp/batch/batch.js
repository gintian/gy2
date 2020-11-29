function toggles(targetId){
	if (document.getElementById(targetId)){
		target = document.getElementById(targetId);
		target.style.display = "";
	}
} 
function hides(targetId){
	if (document.getElementById(targetId)){
		target = document.getElementById(targetId);
		target.style.display = "none";
	}
}
function IsDigit() { 
    return ((event.keyCode >= 47) && (event.keyCode <= 57)); 
}
function tr_bgcolor(nid){
	var tablevos=document.getElementsByTagName("input");
	for(var i=0;i<tablevos.length;i++){
	    var cvalue = tablevos[i];
	    var td = cvalue.parentNode.parentNode;
	    td.style.backgroundColor = '';
    }
	var c = document.getElementById(nid);
	if(c==null||c==undefined)
		return;
	var tr = c.parentNode.parentNode;
	if(tr==null||tr==undefined)
		return;
	if(tr.style.backgroundColor!=''){
		tr.style.backgroundColor = '' ;
	}else{
		tr.style.backgroundColor = '#FFF8D2' ;
	}
	document.getElementById("id").value=nid;
}
function formulaView(){
	toggles("viewSave");
	hides("hideSave");
}
function symbol(strexpr){
	var expr_editor=document.getElementById("formula");
	if(expr_editor)
		expr_editor.focus();
	
	var element = document.selection;
	if (element!=null) {
		var rge = element.createRange();
		if (rge!=null)	
		rge.text=strexpr;
	}else{
				var word = expr_editor.value;
				var _length=strexpr.length;
				var startP = expr_editor.selectionStart;
				var endP = expr_editor.selectionEnd;
				var ddd=word.substring(0,startP)+strexpr+word.substring(endP);
		    	expr_editor.value=ddd;
        		expr_editor.setSelectionRange(startP+_length,startP+_length); 
	}
}
function setCheck(obj,str){
	var sortitem = document.getElementById(str).value;
	var check="0";
	var name = obj.name;
	if(obj.checked){
		check="1";
	}
	var item = "";
	var arr = sortitem.split("`");
	if(arr.length>0){
		for(var i=0;i<arr.length;i++){
			var arr_item = arr[i].split("::");
			if(arr_item.length==3){
				if(arr_item[0]==name){
					item+=arr_item[0]+"::"+arr_item[1]+"::"+check+"`";
				}else{
					item+=arr[i]+"`";
				}
			}
		}
	}
	var item_arr = name.split("_");
	if(item_arr.length!=2){
		return;
	}
	var unit_type = document.getElementById("unit_type").value;
	var hashvo=new ParameterSet();
	hashvo.setValue("id",item_arr[0]);
	hashvo.setValue("unit_type",unit_type);
	hashvo.setValue("check",check);
	hashvo.setValue("flag","update");
	var request=new Request({method:'post',asynchronous:false,functionId:'1010092010'},hashvo);	
	document.getElementById(str).value=item;
}
function defaultSelect(unit_type){
	var tablevos=document.getElementsByTagName("input");
	var nid = "";
	for(var i=0;i<tablevos.length;i++){
	    if(tablevos[i].type=='checkbox'){
	    	nid=tablevos[i].name;
	    	break;
	    }
    }
    if(nid=='results')
    	return;
    if(nid=='history')
    	return;
    if(nid!=null&&nid.length>0){
    	//tr_bgcolor(nid,unit_type); //modify by xiaoyun 2014-10-14 参数设置/基础数据维护 去掉背景色
    }
}
function outTable(sortitem){
	var tabelstr = "<table width=\"100%\" border=\"0\">";
	var arr = sortitem.split("`");
	if(arr.length>0){
		for(var i=0;i<arr.length;i++){
			var arr_item = arr[i].split("::");
			if(arr_item.length==3){
				tabelstr+="<tr>";
				tabelstr+="<td align=\"left\" onclick=\"tr_bgcolor('";
				tabelstr+=arr_item[0]+"')\">";
				tabelstr+="<input type=\"checkbox\" onclick=\"setCheck(document.getElementById('"+arr_item[0]+"'),'sortstr');\" value=\"1\" id=\""+arr_item[0]+"\"  name=\""+arr_item[0]+"\"";
				if(arr_item[2]==1){
					tabelstr+="checked";
				}
				tabelstr+=">";
				tabelstr+=arr_item[1];
				tabelstr+="</td></tr>";
			}
		}
	}	
	tabelstr+="</table>";	
	return tabelstr;
}
function upSort(){
	var sortitem = document.getElementById("sortstr").value;
	if(sortitem==null||sortitem==undefined||sortitem.length<1)
		return;
	if(sortitem=='aaa')
		return;
	var sortitemid = document.getElementById("id").value;
	if(sortitemid==null||sortitemid==undefined||sortitemid.length<1)
		return;
	var arr = sortitem.split("`");
	var item="";
	var n=0;
	for(var i=0;i<arr.length;i++){
		var arr_item = arr[i].split("::");
		if(arr_item.length==3){
			if(arr_item[0].toLowerCase()==sortitemid.toLowerCase()){
				n=i;
				break;
			}
		}
	}
	if(n>0){
		var sortitem = arr[n];
		arr[n]=arr[n-1];
		arr[n-1]=sortitem;
	}
	for(var i=0;i<arr.length;i++){
		if(arr[i].length>0)
			item+=arr[i]+"`";
	}
	item=item!=null?item:"";
	document.getElementById("sortstr").value = item;
	document.getElementById("scroll_box").innerHTML = outTable(item);
	tr_bgcolor(sortitemid);
}
function downSort(){
	var sortitem = document.getElementById("sortstr").value;
	if(sortitem==null||sortitem==undefined||sortitem.length<1)
		return;
	if(sortitem=='aaa')
		return;
	var sortitemid = document.getElementById("id").value;
	if(sortitemid==null||sortitemid==undefined||sortitemid.length<1)
		return;
	var arr = sortitem.split("`");
	var item="";
	var n=-1;
	for(var i=0;i<arr.length;i++){
		var arr_item = arr[i].split("::");
		if(arr_item.length==3){
			if(arr_item[0].toLowerCase()==sortitemid.toLowerCase()){
				n=i;
				break;
			}
		}
	}
	if(n>=0&&n<arr.length-1){
		var sortitem = arr[n];
		arr[n]=arr[n+1];
		arr[n+1]=sortitem;
	}
	for(var i=0;i<arr.length;i++){
		if(arr[i].length>0)
			item+=arr[i]+"`";
	}
	item=item!=null?item:"";
	document.getElementById("sortstr").value = item;
	document.getElementById("scroll_box").innerHTML = outTable(item);
	tr_bgcolor(sortitemid);
}
function setFormula(unit_type,infor){ 
 	var thecodeurl ="/general/inform/emp/batch/setformula.do?b_query=link`unit_type="+unit_type+"`infor="+infor;
 	var iframe_url ='/general/query/common/iframe_query.jsp?src='+$URL.encode(thecodeurl);
 	var dw=720,dh=450,dl=(screen.width-dw)/2;dt=(screen.height-dh)/2;
 	/*
    if(getBrowseVersion()){
    	var return_vo= window.showModalDialog(iframe_url,"", 
              "dialogWidth:640px; dialogHeight:450px;resizable:no;center:yes;scroll:no;status:no"); // modify by xiaoyun 2014-10-14
    	if(return_vo!=null&&return_vo.length>1){
    		document.getElementById("sortstr").value = return_vo;
			document.getElementById("scroll_box").innerHTML = outTable(return_vo);    
		}
    }else{//非IE浏览器改用open弹窗 wangb 20180206 bug 34418
    	var iTop = (window.screen.availHeight - 30 - dh) / 2;  //获得窗口的垂直位置
		var iLeft = (window.screen.availWidth - 10 - dw) / 2; //获得窗口的水平位置 
		window.open(iframe_url,"","width="+dw+"px,height="+dh+"px,resizable=no,scrollbars=no,status=no,left="+iLeft+",top="+iTop);
    }*/
    //Ie浏览器弹窗 改为 Ext.window  wangb 20180307
    var obj = parent.parent;
    if(obj.Ext){
	    var win = obj.Ext.create('Ext.window.Window',{
				id:'gongshi',
				title:'公式',
				width:dw,
				height:dh,
				resizable:'no',
				autoScoll:false,
				autoShow:true,
				autoDestroy:true,
				html:'<iframe style="background-color:#fff" frameborder="0" SCROLLING=NO height="100%" width="100%" src="'+iframe_url+'"></iframe>',
				renderTo:obj.Ext.getBody(),
				listeners:{
					'close':function(){
						if(this.return_vo!=null&&this.return_vo.length>1){
	    					document.getElementById("sortstr").value = this.return_vo;
							document.getElementById("scroll_box").innerHTML = outTable(this.return_vo);    
						}
					}
				}	
		});
	}else if(getBrowseVersion()){
		var return_vo= window.showModalDialog(iframe_url,"", 
              "dialogWidth:640px; dialogHeight:460px;resizable:no;center:yes;scroll:no;status:no"); // modify by xiaoyun 2014-10-14
    	if(return_vo!=null&&return_vo.length>1){
    		document.getElementById("sortstr").value = return_vo;
			document.getElementById("scroll_box").innerHTML = outTable(return_vo);    
		}
	}	
}

function change(infor){
    //var fieldsetid=document.getElementById("fieldsetid").value;
    var fieldsetid=document.getElementsByName("fieldsetid")[0].value;
	var hashvo=new ParameterSet();
	hashvo.setValue("fieldsetid",fieldsetid);
	hashvo.setValue("flag","0");
	var request=new Request({method:'post',asynchronous:false,onSuccess:showFieldList,functionId:'3020050011'},hashvo);	
}
function addchange(infor){
    //var fieldsetid=document.getElementById("fieldsetid").value;
    var fieldsetid=document.getElementsByName("fieldsetid")[0].value;
	var hashvo=new ParameterSet();
	hashvo.setValue("fieldsetid",fieldsetid);
	hashvo.setValue("flag","2");
	hashvo.setValue("infor",infor);
	var request=new Request({method:'post',asynchronous:false,onSuccess:showFieldList,functionId:'3020060023'},hashvo);	
}
function showFieldList(outparamters){
    hides("codeview");
	var itemlist=outparamters.getValue("itemlist");
	AjaxBind.bind(indBatchHandForm.itemid_arr,itemlist);
}
function getItemid(name){
	var itemid="";
	var itemid_arr= document.getElementsByName(name);
	var itemid_arr_vo = itemid_arr[0];
	if(itemid_arr==null){
		return;
	}else{
		for(var i=0;i<itemid_arr_vo.options.length;i++){
			if(itemid_arr_vo.options[i].selected){
				itemid =itemid_arr_vo.options[i].text;
				continue;
			}
		}
		var arr = itemid.split(":");
		if(arr.length==2){
			 if(name=='itemid_arr'){
			   if(arr[0].toUpperCase().indexOf("Z0")!=-1||arr[0].toUpperCase().indexOf("Z1")!=-1)
			     symbol(arr[0]);
			   else
				 symbol(arr[1]);
			 }else{
			 	symbol('"'+arr[0]+'"');
			 }
			 if(name=='itemid_arr'){
				var in_paramters="itemid="+arr[0];
			    var request=new Request({method:'post',asynchronous:false,parameters:in_paramters,onSuccess:showCodeFieldList,functionId:'3020050012'});
			 }
		}
	}
}
function showCodeFieldList(outparamters){
	var codelist=outparamters.getValue("codelist");
	if(codelist.length>1){
		toggles("codeview");
		AjaxBind.bind(indBatchHandForm.itemid_value_arr,codelist);
	}else{
		hides("codeview");
	}	
}
function function_Wizard(infor){
    var thecodeurl ="/org/autostatic/mainp/function_Wizard.do?b_query=link&infor="+infor+"&salaryid=&tableid=&salarytemp="; 
    var iframe_url="/general/query/common/iframe_query.jsp?src="+$URL.encode(thecodeurl);
   	iframe_url = iframe_url.replace(/&/g,"`");
    if(getBrowseVersion()){
    	var return_vo= window.showModalDialog(iframe_url, "", 
              "dialogWidth:400px; dialogHeight:400px;resizable:no;center:yes;scroll:yes;status:no");
    	if(return_vo!=null){
  	 		symbol(return_vo);
  		}
    }else{
    	//兼容非IE浏览器 弹窗替换用 open弹窗  wangb 20180209 bug 34787
		var iTop = (window.screen.availHeight - 30 - 400) / 2;  //获得窗口的垂直位置
		var iLeft = (window.screen.availWidth - 10 - 400) / 2; //获得窗口的水平位置 
		window.open(iframe_url,"","width=400px,height=400px,resizable=no,scrollbars=no,status=no,left="+iLeft+",top="+iTop);
    }
}
//非IE浏览器 弹窗 返回数据  wangb 20180209 bug 34787
function openReturn(return_vo){
	if(return_vo!=null){
  	 	symbol(return_vo);
  	}
}

function getFormula(id,unit_type){
	if(id.length<1){
		return;
	}
	var arr = id.split(":");
	if(arr.length==2){
		document.getElementById("formula").value='';
		return;
	}
	var hashvo=new ParameterSet();
	hashvo.setValue("id",id);	
	hashvo.setValue("unit_type",unit_type);	
	var request=new Request({method:'post',asynchronous:false,onSuccess:showFormula,functionId:'1010092008'},hashvo);
	formulaView();
}
function defaultSelectFormula(unit_type){
	var tablevos=document.getElementsByTagName("input");
	var nid = "";
	for(var i=0;i<tablevos.length;i++){
	    if(tablevos[i].type=='checkbox'){
	    	nid=tablevos[i].name;
	    	break;
	    }
    }
    if(nid!=null&&nid.length>0){
    	tr_bgcolor(nid);
    	var arr = nid.split("_");
    	if(arr.length==2)
    		getFormula(arr[0],unit_type);
    }
}
function showFormula(outparamters){
	var formula=outparamters.getValue("formula");
	document.getElementById("formula").value=getDecodeStr(formula);
}
function addFormula(infor,setname,issetid){
	var thecodeurl ="/general/inform/emp/batch/addformula.do?b_query=link`infor="+infor+"`setid="+setname+"`issetid="+issetid; 
	var iframe_url ="/general/query/common/iframe_query.jsp?src="+$URL.encode(thecodeurl);
	var dw=500,dh=400,dl=(screen.width-dw)/2;dt=(screen.height-dh)/2;
    if(getBrowseVersion()){
    	var return_vo= window.showModalDialog(iframe_url, "", 
              "dialogLeft:"+dl+"px;dialogTop:"+dt+"px;dialogWidth:405px; dialogHeight:400px;resizable:no;center:yes;scroll:yes;status:no;");
    	if(return_vo!=null){
    		var itemid = return_vo;
    		var item = document.getElementById("formulastr").value;
    		var itemarr = item.split("`");
			var arr=itemid.split(":");
			var fidid = "";
			if(arr.length==2){
				fidid="new:"+itemarr.length+"_"+arr[0];
				item=item+fidid+"::"+arr[1]+"::1"+"`";
			}
			if(fidid==null&&fidid.length<1){
				return;
			}
			document.getElementById("formulastr").value = item;
			document.getElementById("formula").value = "";
			//document.getElementById("itemtable").innerHTML = outFormulaTable(item);
			//tr_bgcolor(fidid);
			document.getElementById("id").value=fidid;
			formulaView();
			resultCheckExpr();
    	}
    }else{//非IE浏览器 使用open弹窗打开  wangb 20180206
    	var iTop = (window.screen.availHeight - 30 - dh) / 2;  //获得窗口的垂直位置
		var iLeft = (window.screen.availWidth - 10 - dw) / 2; //获得窗口的水平位置 
		window.open(iframe_url,"","width="+dw+"px,height="+dh+"px,resizable=no,scrollbars=no,status=no,left="+iLeft+",top="+iTop);
    }
  
}
//非IE浏览器 弹窗回调方法  wangb 20180206
function saveReturn(return_vo){
	if(return_vo!=null){
    	var itemid = return_vo;
    	var item = document.getElementById("formulastr").value;
    	var itemarr = item.split("`");
		var arr=itemid.split(":");
		var fidid = "";
		if(arr.length==2){
			fidid="new:"+itemarr.length+"_"+arr[0];
			item=item+fidid+"::"+arr[1]+"::1"+"`";
		}
		if(fidid==null&&fidid.length<1){
			return;
		}
		document.getElementById("formulastr").value = item;
		document.getElementById("formula").value = "";
		//document.getElementById("itemtable").innerHTML = outFormulaTable(item);
		//tr_bgcolor(fidid);
		document.getElementById("id").value=fidid;
		formulaView();
		resultCheckExpr();
    }
}

function delFormula(){
	var id = document.getElementById("id").value;
    var item = document.getElementById("formulastr").value;
    var itemarr = item.split("`");
    var desc="";
    var name=""; 
    var n=0;
	for(var i=0;i<itemarr.length;i++){
		var arr = itemarr[i].split("::");
		if(arr.length==3){
			if(id!=arr[0]){
				desc +=itemarr[i]+"`";
			}else{
				name=arr[1];
				n=i;
			}
		}
	}
	if(!confirm('确定删除['+name+"]?")){
    	return ;
    }
	document.getElementById("formulastr").value = desc;
	document.getElementById("itemtable1").innerHTML = outFormulaTable(desc);
	var itemtable = document.getElementById('itemtable');
	var div = document.getElementById('divId');
	var fs = document.getElementById('fsId');
	var fieldset = document.getElementById('fieldId');
	var text = document.getElementsByName('formula')[0];
	if(getBrowseVersion()){
		if(isCompatibleIE()) {
			itemtable.style.left='5px';
			itemtable.style.width='190px';
			itemtable.style.height='300px';
			div.style.height='310px';
			text.style.width='340px';
			fs.style.paddingLeft="5px";
		} else {
			text.style.width='320px';
			fs.style.width='320px';
			fieldset.style.width='300px';
			itemtable.style.height='310px';
			div.style.height='320px';
		}
	}
	/*员工管理-计算-设置公式页面删除公式后页面变宽，因此去掉这部分改变样式的代码
	if(getBrowseVersion() && getBrowseVersion() !=10){//IE兼容模式浏览器 公式指标列表样式 问题   wangb 20180206
				var fieldset1 = document.getElementsByTagName('fieldset')[0];
				fieldset1.style.width = '220px';
				var itemtable = document.getElementById('itemtable');
				itemtable.style.left='1px';
			}else if(getBrowseVersion() ==10){//ie11 非兼容模式 样式修改 wangb 20190307
				var itemtable = document.getElementById('itemtable');
				itemtable.style.border="";
				itemtable.style.top="";
				itemtable.style.width='210px';
			}else{//非IE浏览器 公式指标列表样式 问题   wangb 20180206
				var itemtable = document.getElementById('itemtable');
				itemtable.style.border="";
				itemtable.style.top="";
			}
	//19/3/15 xus 浏览器兼容：点击删除按钮 计算项目框内样式错乱  ------------end
	*/
	var unit_type = document.getElementById("unit_type").value;
	if(n>=itemarr.length-2){
		if(itemarr[n-1]!=null&&itemarr[n-1]!=undefined){
			var arr_item = itemarr[n-1].split("::");
			if(arr_item.length==3){
				var itemid=arr_item[0];
				var fid_arr = itemid.split("_");
				var fid = "";
				if(fid_arr.length==2){
					fid = fid_arr[0];
				}
				tr_bgcolor(itemid);
				getFormula(fid,unit_type);
			}
		}else{
			document.getElementById("id").value='';
			document.getElementById("formula").value='';
		}
	}else{
		var arr_item = itemarr[n+1].split("::");
		if(arr_item.length==3){
			var itemid=arr_item[0];
			var fid_arr = itemid.split("_");
			var fid = "";
			if(fid_arr.length==2){
				fid = fid_arr[0];
			}
			tr_bgcolor(itemid);
			getFormula(fid,unit_type);
		}
	}
	
	var item_arr = id.split("_");
	if(item_arr.length!=2){
		return;
	}
	var fid_arr = item_arr[0].split(":");
	if(fid_arr.length==2){
		return;
	}
	var hashvo=new ParameterSet();
	hashvo.setValue("id",item_arr[0]);
	hashvo.setValue("unit_type",unit_type);
	hashvo.setValue("flag","del");
	var request=new Request({method:'post',asynchronous:false,functionId:'1010092010'},hashvo);
}
function resultCheckExpr(){
	var id = document.getElementById("id").value;
	var formula = document.getElementById("formula").value;
    if(id==null&&id.length<1){
    	return;
    }
	var arr = id.split("_");
	if(arr.length!=2){
		return;
	}
	
    var hashvo=new ParameterSet();
    hashvo.setValue("c_expr",getEncodeStr(formula));
	hashvo.setValue("itemid",arr[1]);
	var In_paramters="flag=1"; 	
	var request=new Request({method:'post',asynchronous:false,
			parameters:In_paramters,onSuccess:saveFormula,functionId:'1602010217'},hashvo);
		
}
function saveFormula(outparamters){
	var info = outparamters.getValue("info");
	var formula = document.getElementById("formula").value;
	if(info=='ok'){
		var id = document.getElementById("id").value;
		var item = document.getElementById("formulastr").value;
		var infor = document.getElementById("infor").value;
		var unit_type = document.getElementById("unit_type").value;
		var setname=document.getElementById("setname").value;
		var isSetId=document.getElementById("isSetId").value;
		var hashvo=new ParameterSet();
		hashvo.setValue("formulastr",getEncodeStr(item));
		hashvo.setValue("formula",getEncodeStr(formula));
		hashvo.setValue("id",id);
		hashvo.setValue("flag","save");
		hashvo.setValue("infor",infor);
		hashvo.setValue("unit_type",unit_type);
		hashvo.setValue("setname",setname);
		hashvo.setValue("isSetId",isSetId);
		var request=new Request({method:'post',asynchronous:false,onSuccess:setFormulastr,functionId:'1010092010'},hashvo);
	}else{
		if(info.length<2){
			alert(formula+" "+SYNTAX_ERROR+"!");
		}else{
			alert(getDecodeStr(info));
		}
		return false;
	}
}
function setFormulastr(outparamters){
	var id = outparamters.getValue("id");
	var formulastr = outparamters.getValue("formulastr");
	document.getElementById("formulastr").value=formulastr;
	document.getElementById("itemtable1").innerHTML = outFormulaTable(formulastr);
	tr_bgcolor(id);
	//新增计算公式后，样式调整  wangb 20180206
	if(getBrowseVersion() && getBrowseVersion() !=10){//IE兼容模式浏览器 公式指标列表样式 问题   wangb 20180206
		var itemtable = document.getElementById('itemtable');
		itemtable.style.left='5px';
		itemtable.style.width='190px';
		itemtable.style.height='295px';
	}else if(getBrowseVersion() ==10){//ie11 非兼容模式 样式修改 wangb 20190307
		var itemtable = document.getElementById('itemtable');
		itemtable.style.border="";
		itemtable.style.top="";
		itemtable.style.width='210px';
	}else{//非IE浏览器 公式指标列表样式 问题   wangb 20180206
		var itemtable = document.getElementById('itemtable');
		itemtable.style.border="";
		itemtable.style.top="";
		
	}
}
function outFormulaTable(sortitem){
	var unit_type = document.getElementById("unit_type").value;
	/* 薪资管理-基础数据维护-计算-公式 xiaoyun 2014-10-16 start */
	//var tabelstr = "<div id=\"itemtable\" class=\"complex_border_color\"><table width=\"100%\" cellspacing=\"0px\" border=\"0\" align=\"center\">";
	var tabelstr = "<div id=\"itemtable\" class=\"complex_border_color\" style=\" margin-bottom:5px; height:315px;width:210px;border:1px solid;position: absolute;left: 10px;top:10px;\"><table width=\"100%\" cellspacing=\"0px\" border=\"0\" align=\"center\">";
	/* 薪资管理-基础数据维护-计算-公式 xiaoyun 2014-10-16 end */
	tabelstr += "<tr class=\"fixedHeaderTr1\">";
	tabelstr += "<td width=\"20%\" align=\"left\" class=\"TableRow\" style=\"border-left:none;\" nowrap>"+GENERAL_EFFECTIVE+"</td>";
	tabelstr += "<td width=\"80%\" align=\"left\" class=\"TableRow\" style=\"border-left:none;border-right:none;\" nowrap>"+GENERAL_ITEMNAME+"</td></tr>";
	var arr = sortitem.split("`");
	if(arr.length>0){
		for(var i=0;i<arr.length;i++){
			var arr_item = arr[i].split("::");
			if(arr_item.length==3){
				var id=arr_item[0];
				var fid_arr = id.split("_");
				var fid = "";
				if(fid_arr.length==2){
					fid = fid_arr[0];
				}
				tabelstr+="<tr><td class=\"RecordRow\" align=\"left\" style=\"border-top:none;border-left:none;\" onclick=\"tr_bgcolor('";
				tabelstr+=id;
				tabelstr+="');getFormula('";
				tabelstr+=fid;
				tabelstr+="','";
				tabelstr+=unit_type;
				tabelstr+="');\">";
				tabelstr+="<input type=\"checkbox\" id=\""+id+"\" name=\"";
				tabelstr+=id;
				tabelstr+="\" value=\"1\" onclick=\"setCheck(document.getElementById('"+id+"'),'formulastr');\"";
				tabelstr+=id;
				if(arr_item[2]==1){
					tabelstr+=" checked";
				}
				tabelstr+=">";
				tabelstr+="</td><td class=\"RecordRow\" style=\"border-top:none;border-left:none;border-right:none;\" onclick=\"tr_bgcolor('";
				tabelstr+=id;
				tabelstr+="');getFormula('";
				tabelstr+=fid;
				tabelstr+="','";
				tabelstr+=unit_type;
				tabelstr+="');\">";
				tabelstr+=arr_item[1];
				tabelstr+="</td></tr>";
			}
		}
	}	
	tabelstr+="</table></div>";	
	return tabelstr;
}
function addItemOk(){
	var itemid="";
	var itemid_arr= document.getElementsByName("itemid_arr");
	var itemid_arr_vo = itemid_arr[0];
	if(itemid_arr==null){
		return;
	}else{
		for(var i=0;i<itemid_arr_vo.options.length;i++){
			if(itemid_arr_vo.options[i].selected){
				itemid =itemid_arr_vo.options[i].text;
				continue;
			}
		}
		if(itemid==null||itemid==undefined||itemid==''){
			alert(GENERAL_SELECT_ITEMNAME+"!");
			return false;
		}
		if(navigator.appName.indexOf("Microsoft")!= -1){
			if(getBrowseVersion()){
				parent.window.returnValue=itemid;
				parent.window.close();
			}else{
				parent.opener.saveReturn(itemid);
				parent.window.close();
			}
			//window.returnValue=itemid;
			//window.close();
		}else{
			if(getBrowseVersion()){
				top.returnValue=itemid;
				top.close();
			}else{
				parent.opener.saveReturn(itemid);
				parent.window.close();
			}
			//top.returnValue=itemid;
			//top.close();
		}
	}
}
function closeOk(){
	var formulastr=document.getElementById("formulastr").value;
	formulastr=formulastr!=null&&formulastr!=''?formulastr:"aaa";
	/*
	if(getBrowseVersion()){
		parent.window.returnValue=formulastr;
	}else{//非IE浏览器弹窗改用open弹窗  wangb 20180206
		parent.opener.returnFormula(formulastr);
	}
	parent.window.close();
	*/
	//Ext 弹窗返回数据    wangb 20190307
	
	if(parent.parent.Ext){
		var win = parent.parent.Ext.getCmp('gongshi');
		win.return_vo = formulastr;
		win.close();
	}else if(getBrowseVersion()){
		parent.window.returnValue=formulastr;
		parent.window.close();
	}
}

function saveSort(){
		var sortitem = document.getElementById("sortstr").value;
    	var hashvo=new ParameterSet();
		hashvo.setValue("sortstr",sortitem);
		var request=new Request({method:'post',asynchronous:false,onSuccess:null,functionId:'1010092014'},hashvo);
}
function colFormulaOk(dbname,setname,a_code,viewsearch,infor){
	var tablevos=document.getElementsByTagName("input");
	var checkflag = false;
	for(var i=0;i<tablevos.length;i++){
		if(tablevos[i].type=="checkbox"&&tablevos[i].name!="results"&&tablevos[i].name!="history"){
			if(tablevos[i].checked && tablevos[i].id!="computeScope"){
				checkflag = true;
				break;
			}
      	 }
   	}
	if(!checkflag){
		alert("请选择计算公式后，再进行计算!");
		return false;
	}
	var unit_type = document.getElementById("unit_type").value;
	
	var results="0";
	var history="1";
	
	if(unit_type!=2){
		var count = document.getElementById("count").value;
		var warningMess = "总共将计算"+count+"条记录,是否继续?";
		var hashvo=new ParameterSet();
		hashvo.setValue("computeScope","0");
		if(unit_type == 3){
			var computeScope = document.getElementById("computeScope");
			if(computeScope.checked==true){
				hashvo.setValue("computeScope","1");
				warningMess = "计算所有下级,是否继续?";
			}
				
			
		}
			if(!confirm(warningMess)){
		    	return ;
		    }
		
		var button = document.getElementById("submitId");
		if(button)
			button.disabled=true;
		
		button = document.getElementById("returnId");
		if(button)
			button.disabled=true;
	    
		var sortitem = document.getElementById("sortstr").value;
	    var entranceFlag=document.getElementById("entranceFlag").value;
    	
		hashvo.setValue("flag","updatecol");	
		hashvo.setValue("dbname",dbname);	
		hashvo.setValue("sortstr",sortitem);
		hashvo.setValue("setname",setname);	
		hashvo.setValue("a_code",a_code);	
		hashvo.setValue("results",results);	
		hashvo.setValue("infor",infor);	
		hashvo.setValue("history",history);	
		hashvo.setValue("viewsearch",viewsearch);
		hashvo.setValue("entranceFlag",entranceFlag);
		var waitInfo=eval("wait1");
		waitInfo.style.display="block";
		var request=new Request({method:'post',asynchronous:true,onSuccess:indCheck,functionId:'1010092006'},hashvo);
	}
	
	if(unit_type==2){
		if(document.getElementById("results")&&document.getElementById("results").checked){
			results="1";
		}
		if(document.getElementById("history")&&document.getElementById("history").checked){
			history="0";
		}
		var hashvo1=new ParameterSet();
		hashvo1.setValue("flag","updatecol");
		hashvo1.setValue("results",results);
		hashvo1.setValue("history",history);
		hashvo1.setValue("setname",setname);
		hashvo1.setValue("dbname",dbname);
		var request=new Request({method:'post',asynchronous:false,onSuccess:showcount,functionId:'1010092020'},hashvo1);
		function showcount(outparamters){
			var count = outparamters.getValue("count");
			if(!confirm("总共将计算"+count+"条记录,是否继续?")){
		    	return ;
		    }
			
			var button = document.getElementById("submitId");
			if(button)
				button.disabled=true;
			
			button = document.getElementById("returnId");
			if(button)
				button.disabled=true;
			
		    var entranceFlag=document.getElementById("entranceFlag").value;
			var sortitem = document.getElementById("sortstr").value;
		    var hashvo=new ParameterSet();
			hashvo.setValue("flag","updatecol");	
			hashvo.setValue("dbname",dbname);	
			hashvo.setValue("sortstr",sortitem);
			hashvo.setValue("setname",setname);	
			hashvo.setValue("a_code",a_code);	
			hashvo.setValue("results",results);	
			hashvo.setValue("infor",infor);	
			hashvo.setValue("history",history);	
			hashvo.setValue("viewsearch",viewsearch);
			hashvo.setValue("entranceFlag",entranceFlag);
			var waitInfo=eval("wait1");
			waitInfo.style.display="block";
			var request=new Request({method:'post',asynchronous:true,onSuccess:indCheck,functionId:'1010092006'},hashvo);
		}
	}
	
}
function indCheck(outparamters){
	var succeed = outparamters.getValue("succeed");
	if("false"==succeed){
		var waitInfo=eval("wait1");
		waitInfo.style.display="none";
		return;
	}
	var check = outparamters.getValue("check");
		//检查编制
	var scanInfo = outparamters.getValue("scanformation");
	if( (scanInfo!=null) && (scanInfo!='')){
      alert(scanInfo);
	}
	
	if(check=='ok'){
		//编制管理暂时不支持浏览器兼容，所以添加参数和其他页面做区分
		if(flag && "ie" == flag){
			window.returnValue="ok";
			window.close();
		}else{//非IE浏览器 wangb 20180127
			// 不一定是非IE浏览器 批量计算是公共模块，某些调用的地方flag也可能没定义 zxj 20200102 jazz56493
			if (parent.parent.openReturn)
				parent.parent.openReturn("ok");
			else {
				window.returnValue="ok";
				window.close();
			}
		}
	}else if(check=='msg'){
		//if(getBrowseVersion()){
		//	window.returnValue="为已报批或已批审核状态的数据未能参与计算!";
		//	window.close();
		//}else{//非IE浏览器 wangb 20180127
			parent.parent.openReturn("为已报批或已批审核状态的数据未能参与计算!");
		//}
	}else{
		var waitInfo=eval("wait1");
		waitInfo.style.display="none";
		alert(FORMULA_ERROR_CHECK_COND_DATA);
		
	}
}
