function toggles(targetId){
	if (document.getElementById(targetId)){
		target = document.getElementById(targetId);
		target.style.display = "block";
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
	document.getElementById("formula").focus();
	var element = document.selection;
	if (element!=null) {
		var rge = element.createRange();
		if (rge!=null)	
		rge.text=strexpr;
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
    	tr_bgcolor(nid,unit_type);
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
				tabelstr+="<td onclick=\"tr_bgcolor('";
				tabelstr+=arr_item[0]+"')\">";
				tabelstr+="<input type=\"checkbox\" onclick=\"setCheck(this,'sortstr');\" value=\""+arr_item[0].split("_")[0]+"\" name=\""+arr_item[0]+"\"";
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
function setFormula(unit_type,infor,setid){ 
 	var thecodeurl ="/general/inform/emp/batch/setformula.do?b_query=link&setId=1&unit_type="+unit_type+"&infor="+infor+"&setname="+setid;
    var return_vo= window.showModalDialog(thecodeurl,"", 
              "dialogWidth:720px; dialogHeight:450px;resizable:no;center:yes;scroll:no;status:no");
    if(return_vo!=null&&return_vo.length>1){
    	document.getElementById("sortstr").value = return_vo;
		document.getElementById("scroll_box").innerHTML = outTable(return_vo);    
	}
}
function change(){
    var fieldsetid=document.getElementById("fieldsetid").value;
	var hashvo=new ParameterSet();
	hashvo.setValue("fieldsetid",fieldsetid);
	hashvo.setValue("flag","0");
	var request=new Request({method:'post',asynchronous:false,onSuccess:showFieldList,functionId:'3020050011'},hashvo);	
}
function addchange(){
    var fieldsetid=document.getElementById("fieldsetid").value;
	var hashvo=new ParameterSet();
	hashvo.setValue("fieldsetid",fieldsetid);
	hashvo.setValue("flag","2");
	var request=new Request({method:'post',asynchronous:false,onSuccess:showFieldList,functionId:'3020060023'},hashvo);	
}
function showFieldList(outparamters){
	var itemlist=outparamters.getValue("itemlist");
	AjaxBind.bind(indBatchHandForm.itemid_arr,itemlist);
}
function getItemid(){
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
		var arr = itemid.split(":");
		if(arr.length==2){
			symbol(arr[1]);
		}
	}
}
function function_Wizard(infor){
    var thecodeurl ="/org/autostatic/mainp/function_Wizard.do?b_query=link&infor="+infor+"&salaryid=&tableid=&salarytemp="; 
    var return_vo= window.showModalDialog(thecodeurl, "", 
              "dialogWidth:400px; dialogHeight:400px;resizable:no;center:yes;scroll:yes;status:no");
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
function addFormula(infor){
	var thecodeurl ="/general/inform/emp/batch/addformula.do?b_query=link&infor="+infor; 
    var return_vo= window.showModalDialog(thecodeurl, "", 
              "dialogWidth:400px; dialogHeight:400px;resizable:no;center:yes;scroll:yes;status:no");
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
	document.getElementById("itemtable").innerHTML = outFormulaTable(desc);
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
		var hashvo=new ParameterSet();
		hashvo.setValue("formulastr",getEncodeStr(item));
		hashvo.setValue("formula",getEncodeStr(formula));
		hashvo.setValue("id",id);
		hashvo.setValue("flag","save");
		hashvo.setValue("infor",infor);
		hashvo.setValue("unit_type",unit_type);
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
	document.getElementById("itemtable").innerHTML = outFormulaTable(formulastr);
	tr_bgcolor(id);
}
function outFormulaTable(sortitem){
	var unit_type = document.getElementById("unit_type").value;
	var tabelstr = "<table width=\"100%\" border=\"0\" align=\"center\" class=\"ListTable1\">";
	tabelstr += "<tr class=\"fixedHeaderTr1\">";
	tabelstr += "<td width=\"20%\" align=\"center\" class=\"TableRow\" nowrap>"+GENERAL_EFFECTIVE+"</td>";
	tabelstr += "<td width=\"80%\" align=\"center\" class=\"TableRow\" nowrap>"+GENERAL_ITEMNAME+"</td></tr>";
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
				tabelstr+="<tr><td class=\"RecordRow\" align=\"center\" onclick=\"tr_bgcolor('";
				tabelstr+=id;
				tabelstr+="');getFormula('";
				tabelstr+=fid;
				tabelstr+="','";
				tabelstr+=unit_type;
				tabelstr+="');\">";
				tabelstr+="<input type=\"checkbox\" name=\"";
				tabelstr+=id;
				tabelstr+="\" value=\"1\" onclick=\"setCheck(this,'formulastr');\"";
				tabelstr+=id;
				if(arr_item[2]==1){
					tabelstr+=" checked";
				}
				tabelstr+=">";
				tabelstr+="</td><td class=\"RecordRow\" onclick=\"tr_bgcolor('";
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
	tabelstr+="</table>";	
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
		
		window.returnValue=itemid;
		window.close();
	}
}
function closeOk(){
	var formulastr=document.getElementById("formulastr").value;
	formulastr=formulastr!=null&&formulastr!=''?formulastr:"aaa";
	window.returnValue=formulastr;
	window.close();
}
function colFormulaOk(unit_type,setid,year){
	var tablevos=document.getElementsByTagName("input");
	var checkflag = false;
	var ids="";
	for(var i=0;i<tablevos.length;i++){
		if(tablevos[i].type=="checkbox"&&tablevos[i].name!="results"&&tablevos[i].name!="history"){
			if(tablevos[i].checked){
				checkflag = true;
				ids+=","+tablevos[i].value;
			}
      	 }
   	}
	if(!checkflag){
		alert("请选择计算公式后，再进行计算!");
		return false;
	}
	if(confirm("工资总额参数中设置的实发项目，剩余项目和年月标识指标不能计算\r\n并且只能计算起草，暂停和驳回的记录，是否继续？"))
	{
    	var sortitem = document.getElementById("sortstr").value;
        var hashvo=new ParameterSet();
	    hashvo.setValue("sortstr",sortitem);
	    hashvo.setValue("setid",setid);	
	    hashvo.setValue("unit_type",unit_type);	
        hashvo.setValue("year",year);	
        hashvo.setValue("ids",ids.substring(1));
	    var request=new Request({method:'post',asynchronous:false,onSuccess:indCheck,functionId:'3020080021'},hashvo);	
	}else
	{
	   return;
	}
}
function indCheck(outparamters){
	var check = outparamters.getValue("msg");
	if(check=='1'){
		window.returnValue="ok";
		window.close();
	}else{
		alert(FORMULA_ERROR_CHECK_COND);
	}
}