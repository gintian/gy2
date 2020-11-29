var formula_arr=new Array();
var itemidarr=new Array();
var fieldsetidarr=new Array();
var descarr=new Array();
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
function initArr(formulaarr,itemid){
	formula_arr=getDecodeStr(formulaarr).split("`");
	var arr = itemid.split("`");
	for(var i=0;i<arr.length;i++){
		var itemarr = arr[i].split(":");
		if(itemarr.length==3){
			fieldsetidarr[i]=itemarr[0];
			itemidarr[i]=itemarr[1];
			descarr[i]=itemarr[2];
		}
	}
	if(itemidarr.length>0)
		onSelect(itemidarr[0]+"_"+1);
}
function symbol(strexpr){
	document.getElementById("formula").focus();
	var element = document.selection;
	if (element!=null) {
		var rge = element.createRange();
		if (rge!=null)	
		rge.text=strexpr;
	}
	viewSaveButton();
}
function tr_bgcolor(nid){
	var tablevos=document.getElementsByTagName("input");
	for(var i=0;i<tablevos.length;i++){
	    if(tablevos[i].type=="text"){
	    	var cvalue = tablevos[i];
	    	var td = cvalue.parentNode.parentNode;
	    	td.style.backgroundColor = '';
		}
    }
	var c = document.getElementById(nid);
	var tr = c.parentNode.parentNode;
	if(tr.style.backgroundColor!=''){
		tr.style.backgroundColor = '' ;
	}else{
		tr.style.backgroundColor = '#FFF8D2' ;
	}
}
function function_Wizard(infor){
    var thecodeurl ="/org/autostatic/mainp/function_Wizard.do?b_query=link&infor="+infor; 
    var return_vo= window.showModalDialog(thecodeurl,"", 
              "dialogWidth:400px; dialogHeight:450px;resizable:no;center:yes;scroll:yes;status:no;");
    if(return_vo!=null){
		symbol(return_vo);
	}	
}
function change(){
    var fieldsetid=document.getElementById("fieldid").value;
    var itemid=document.getElementById("itemid").value;
    var arr = itemid.split("_");
    if(arr.length==2){
    	var i = parseInt(arr[1])-1;
    	fieldsetidarr[i]=fieldsetid;
    }
    
	var hashvo=new ParameterSet();
	hashvo.setValue("fieldsetid",fieldsetid);
	var request=new Request({method:'post',asynchronous:false,onSuccess:showList,functionId:'1010090010'},hashvo);	
}
function showList(outparamters){
	var itemlist=outparamters.getValue("itemlist");
	AjaxBind.bind(auditForm.itemid_arr,itemlist);
}
function changeField(){
	hides("viewcode");
    var fieldsetid=document.getElementById("field").value;
	var hashvo=new ParameterSet();
	hashvo.setValue("fieldsetid",fieldsetid);
	var request=new Request({method:'post',asynchronous:false,onSuccess:showFieldList,functionId:'3020050011'},hashvo);	
}
function showFieldList(outparamters){
	var itemlist=outparamters.getValue("itemlist");
	AjaxBind.bind(auditForm.itemarr,itemlist);
}
function changeCodeValue(){
    var itemid=getItemid('itemarr').split(":");
    var fieldsetid=document.getElementById("field").value;
    if(itemid.length<1){
    	return;
    }
    symbol(itemid[1]);
    var hashvo=new ParameterSet();
	hashvo.setValue("fieldsetid",fieldsetid);	
	hashvo.setValue("itemid",itemid[0]);
	var request=new Request({method:'post',asynchronous:false,onSuccess:showCodeFieldList,functionId:'3020050012'},hashvo);	
}
function getItemid(itemarr){
	var itemid="";
	var itemid_arr= document.getElementsByName(itemarr);
	var itemid_arr_vo = itemid_arr[0];
	if(itemid_arr==null){
		return "";
	}else{
		for(var i=0;i<itemid_arr_vo.options.length;i++){
			if(itemid_arr_vo.options[i].selected){
				itemid =itemid_arr_vo.options[i].text;
				continue;
			}
		}
		return itemid;
	}
}
function showCodeFieldList(outparamters){	
	var codelist=outparamters.getValue("codelist");
	if(codelist!=null&&codelist.length>1){
		toggles("viewcode");
		AjaxBind.bind(auditForm.codearr,codelist);
	}else{
		hides("viewcode");
	}	
}
function getCodesid(){
	var codeid="";
	var codesetid_arr= document.getElementsByName("codearr");
	var codesetid_arr_vo = codesetid_arr[0];
	if(codesetid_arr==null){
		return;
	}else{
		for(var i=0;i<codesetid_arr_vo.options.length;i++){
			if(codesetid_arr_vo.options[i].selected){
				codeid =codesetid_arr_vo.options[i].value;
				continue;
			}
		}
		symbol("\""+codeid+"\"");
	}
}
function onEdite(obj){
	obj.readOnly=false;
	movePoint(obj);   
}
function onLeve(obj){
	obj.readOnly=true;   
}
function onSelect(itemid){
	if(itemid.length<1)
		return;
	var item_arr = itemid.split("_");
	if(item_arr.length!=2)
		return;
	var checkitemid = document.getElementById("itemid").value;
	if(itemid==checkitemid)
		return;
	tr_bgcolor(itemid);
	document.getElementById("itemid").value=itemid;
	var n=parseInt(item_arr[1])-1;
	document.getElementById("fieldid").value=fieldsetidarr[n];
	change();
	document.getElementById("formula").value=getDecodeStr(formula_arr[n]);
	selectItem(itemidarr[n]);
}
function selectItem(id){
	var itemid_arr= document.getElementsByName("itemid_arr");
	var itemid_arr_vo = itemid_arr[0];
	if(itemid_arr==null){
		return "";
	}else{
		for(var i=0;i<itemid_arr_vo.options.length;i++){
			var itemid =itemid_arr_vo.options[i].text;
			var arr = itemid.split(":");
			if(arr.length!=2)
				continue;
			if(arr[0]==id){
				itemid_arr_vo.options[i].selected=true;
				break;
			}
		}
	}
}
function changeItem(obj){
	var itemid = obj.value;
	var id = document.getElementById("itemid").value;
	var itemarr = id.split("_");
	if(itemarr.length!=2)
		return;
	var i = parseInt(itemarr[1])-1;
	itemidarr[i]=itemid.toUpperCase();
}
function movePoint(obj){
	var png = obj.value;
	var pn = 1;
	if(png.lenth==0)
		pn=1;
	var rng = obj.createTextRange(); 
	rng.moveStart("character",png.length); 
	rng.collapse(true); 
	rng.select(); 
}
function addTable(){
	var len = itemidarr.length;
	var i=len+1;
	formula_arr[len]='';
	itemidarr[len]='';
	fieldsetidarr[len]='';
	descarr[len]='';
	var tabelstr=document.getElementById("scroll_box").innerHTML;
	tabelstr=tabelstr.replace("</table>","").replace("</TABLE>","");
	tabelstr+="<tr><td align=\"center\" class=\"RecordRow\" style=\"border-left:0px;\">"+i;
	tabelstr+="</td><td class=\"RecordRow\" style=\"border-right:none;\">";
	tabelstr+="<input type=\"text\" class=\"text4\" name=\"";
	tabelstr+="_"+i;
	tabelstr+="\"  size=\"23\"";
	tabelstr+=" onkeydown=\"viewSaveButton();\" onclick=\"onSelect('_"+i+"');\">";
	tabelstr+="</td></tr>";
	tabelstr=tabelstr.replace("TOP","HEIGHT");
	tabelstr+="</table>";
	document.getElementById("scroll_box").innerHTML=tabelstr;
}
function delItemTable(){
	var itemid=document.getElementById("itemid").value;
	if(itemid==null||itemid.length<1)
		return
	var desc=document.getElementById(itemid).value;
	if(confirm(READY_APP_FORMULA+"："+desc)){
		
		var arr = itemid.split("_");
		if(arr.length==2){
			var m=parseInt(arr[1])-1;
			delFormula(itemidarr[m]);
			formula_arr=arrRemove(formula_arr,m);
			itemidarr=arrRemove(itemidarr,m);
			fieldsetidarr=arrRemove(fieldsetidarr,m);
			descarr = arrRemove(descarr,m);
			/*
			var obj = document.getElementById("itemTr");   
			obj.deleteRow(arr[1]);
			var tabelstr = document.getElementById("scroll_box").innerHTML;
			for(var i=m;i<itemidarr.length;i++){
				alert(i)
				var id=i+"_"+itemidarr[i];
				var id_old=(i+1)+"_"+itemidarr[i];
				tabelstr=replaceAll(tabelstr,id_old,id);
				var str_new  = ">"+(i+1)+"</TD>";
				var str_old  = ">"+(i+2)+"</TD>";
				tabelstr=tabelstr.replace(str_old,str_new);
			}
			document.getElementById("scroll_box").innerHTML=tabelstr;
			*/
			document.getElementById("scroll_box").innerHTML=outTable();

			if(itemidarr.length>m){
				onSelect(itemidarr[m]+"_"+(m+1));
			}else{
				if(m>0){
					onSelect(itemidarr[m-1]+"_"+m);
				}else{
					document.getElementById("formula").value='';
					document.getElementById("fieldid").value='';
					change();		
				}
			}
		}
	}
}
function replaceAll(text,replacement,target){
    if(text==null||text==""){
    	return text;
    }
    if(replacement==null||replacement==""){ 
    	return text;
    }
    if(target==null) target="";
    var returnString="";
    var index=text.indexOf(replacement);
    while(index!=-1){
        if(index!=0) returnString+=text.substring(0,index)+target;
        text=text.substring(index+replacement.length);
        index=text.indexOf(replacement);
    }
    if(text!=""){
		returnString+=text;
	}
    return returnString;
}
function outTable(){
	var tabelstr = "<table width=\"100%\" id=\"itemTr\" border=\"0\" cellspacing=\"0\"  cellpadding=\"0\" class=\"ListTable\">";
	tabelstr += "<tr class=\"fixedHeaderTr1\"><td width=\"15%\" height=\"20\" class=\"TableRow\" nowrap>&nbsp;</td>";
	tabelstr += "<td class=\"TableRow\" align=\"center\" nowrap>"+APP_FORMULA+"</td></tr>";
	var n=1;
	for(var i=0;i<itemidarr.length;i++){
		var itemid = itemidarr[i].toUpperCase();
		tabelstr+="<tr><td align=\"center\" class=\"RecordRow\">"+n;
		tabelstr+="</td><td class=\"RecordRow\">";
		tabelstr+="<input type=\"text\" class=\"text4\" name=\"";
		tabelstr+=itemid+"_"+n;
		tabelstr+="\"  size=\"23\" value=\""+descarr[i]+"\"";
		tabelstr+=" onkeydown=\"viewSaveButton();\" onclick=\"onSelect('"+itemid+"_"+n+"');\">";
		tabelstr+="</td></tr>";
		n++;
	}
	tabelstr+="</table>";
	return tabelstr;
}
function arrRemove(arr,n){
	if(arr.length<1||n<0){
		return arr;
	}else{
		return arr.slice(0,n).concat(arr.slice(parseInt(n)+1,arr.length));
	}
}
function saveFormula(){
	var item_arr = getItemid('itemid_arr').split(":");
	if(item_arr.length!=2){
		alert(SELECT_APP_ITEM);
		return;
	}
	var itemid=item_arr[0];
	var id=document.getElementById("itemid").value;
	var arr = id.split("_");
	if(arr.length!=2){
		alert(SELECT_FORMULA_TABLE);
		return;
	}
	
	var check=0;
	for(var i=0;i<itemidarr.length-1;i++){
		if(itemidarr[i].toUpperCase()==item_arr[0].toUpperCase()){
			check+=1;
		}
	}
	if(check==2){
		if(!confirm(item_arr[1]+ALREADY_EXISTS_COVERAGE+"?")){
			return;
		}
	}
	var hashvo=new ParameterSet();
	hashvo.setValue("itemid",itemid);
	hashvo.setValue("itemdesc",item_arr[1]);
	var request=new Request({method:'post',asynchronous:false,onSuccess:checkViewFormula,functionId:'1010090009'},hashvo);
}

function checkViewFormula(outparamters){
	var info=outparamters.getValue("infor");
	var itemdesc = outparamters.getValue("itemdesc");
	var itemid = outparamters.getValue("itemid");
	if(info=='ok'){
		if(!confirm(itemdesc+ALREADY_EXISTS_COVERAGE+"?")){
			return;
		}
	}
	var id=document.getElementById("itemid").value;
	var arr = id.split("_");
	var n = parseInt(arr[1])-1;
	var formula=document.getElementById("formula").value;
	var desc=document.getElementById(id).value;
	if(desc==null||desc==undefined||desc.length<1){
		alert(APP_INFOR_NOT_NULL+"!");
		return false;
	}
		
	
	var fieldsetid=document.getElementById("fieldid").value;
	formula_arr[n]=getEncodeStr(formula);
	itemidarr[n]=itemid;
	fieldsetidarr[n]=fieldsetid;
	descarr[n]= desc;
	var hashvo=new ParameterSet();
	hashvo.setValue("formula",formula_arr[n]);
	hashvo.setValue("itemid",itemid);
	hashvo.setValue("desc",desc);
	var request=new Request({method:'post',asynchronous:false,onSuccess:showSaveResult,functionId:'1010090007'},hashvo);	
}

function delFormula(itemid){
	var hashvo=new ParameterSet();
	hashvo.setValue("formula","");
	hashvo.setValue("itemid",itemid);
	hashvo.setValue("desc","");
	var request=new Request({method:'post',asynchronous:false,onSuccess:showSaveResult,functionId:'1010090007'},hashvo);	
}
function showSaveResult(outparamters){
	var info=outparamters.getValue("info");
	if(info!='ok'){
		alert(getDecodeStr(info));
	}else{
		hideSaveButton();
	}
}
function viewSaveButton(){
	hides("hidebutton");
	toggles("viewbutton");
}
function hideSaveButton(){
	hides("viewbutton");
	toggles("hidebutton");
}
function closeFormula(){
	returnValue="aaaa";
	window.close();
}
