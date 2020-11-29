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
function tr_bgcolor(nid){
	var tablevos=document.getElementsByTagName("input");
	for(var i=0;i<tablevos.length;i++){
	    var cvalue = tablevos[i];
	    var td = cvalue.parentNode.parentNode;
	    td.style.backgroundColor = '';
    }
	var c = document.getElementById(nid);
	var tr = c.parentNode.parentNode;
	if(tr.style.backgroundColor!=''){
		tr.style.backgroundColor = '' ;
	}else{
		tr.style.backgroundColor = '#FFF8D2' ;
	}
}
function setId(nid){
	document.getElementById("id").value=nid;
}
function defaultSelect(nid){
	nid=nid!=null&&nid.length>0?nid:getNid();
    if(nid!=null&&nid.length>0){
    	tr_bgcolor(nid);
    	setId(nid);
    }else{
    	document.getElementById("id").value='';
    }
}
function getNid(){
	var tablevos=document.getElementsByTagName("input");
	var nid = "";
	for(var i=0;i<tablevos.length;i++){
	    if(tablevos[i].type=='checkbox'){
	    	nid=tablevos[i].name;
	    	break;
	    }
    }
    return nid;
}
function getEndNid(){
	var tablevos=document.getElementsByTagName("input");
	var nid = "";
	for(var i=0;i<tablevos.length;i++){
	    if(tablevos[i].type=='checkbox'){
	    	nid=tablevos[i].name;
	    }
    }
    return nid;
}
function alertName(nid){
	hides("view_"+nid);
	toggles("hide_"+nid);
	document.getElementById("value_"+nid).focus();
}
function onLeave(nid,obj){
	toggles("view_"+nid);
	hides("hide_"+nid);
	var chz=obj.value;
	document.getElementById("view_"+nid).innerHTML=chz;
	var tableid=document.getElementById("tableid").value;
	var hashvo=new ParameterSet();
    hashvo.setValue("id",nid);
	hashvo.setValue("tableid",tableid);  
	hashvo.setValue("chz",chz);    
   	var request=new Request({method:'post',asynchronous:true,functionId:'3020110064'},hashvo); 
}
function delTemp(){
	if(!confirm(DELETE_SELECT_FORMULA_GROUP+'？')){
    	return ;
    }
	var id=document.getElementById("id").value;
	if(id==null||id.length<1){
		alert(SELECT_FORMULA_GROUP+"!");
		return false;
	}
	var tableid=document.getElementById("tableid").value;
	var hashvo=new ParameterSet();
    hashvo.setValue("id",id);
	hashvo.setValue("tableid",tableid);   
   	var request=new Request({method:'post',asynchronous:true,onSuccess:outputTemp,functionId:'3020110052'},hashvo); 
}
function alertFlag(id,obj){
	var tableid=document.getElementById("tableid").value;
	var flag="0";
	if(obj.checked){
		flag="1";
	}
	var hashvo=new ParameterSet();
    hashvo.setValue("id",id);
	hashvo.setValue("tableid",tableid); 
	hashvo.setValue("flag",flag);     
   	var request=new Request({method:'post',asynchronous:true,functionId:'3020110061'},hashvo); 
}
function outputTemp(outparamters){
	var tableid=outparamters.getValue("tableid");
	document.getElementById("id").value='';
	reflesh(tableid);
}
function setSorting(tableid){
 	var thecodeurl ="/general/salarychange/sort.do?b_query=link&tabid="+tableid
    var dialogWidth="360px";
    var dialogHeight="400px";
    if (isIE6()){
   		dialogWidth="420px";
   		dialogHeight="460px";
    } 
    var return_vo= window.showModalDialog(thecodeurl, "", 
              "dialogWidth:"+dialogWidth+"; dialogHeight:"+dialogHeight+";resizable:no;center:yes;scroll:yes;status:no;");
    if(return_vo!=null){
    	reflesh(tableid);
	}
}
function addFormula(){
	var tableid=document.getElementById("tableid").value;
 	var thecodeurl ="/general/salarychange/setformula.do?b_query=link&tableid="+tableid+"&flag=add"
    var dialogWidth="750px";
    var dialogHeight="600px";
    if (isIE6()){
    	dialogWidth="800px";
    	dialogHeight="630px";
    } 
    var return_vo= window.showModalDialog(thecodeurl, "", 
              "dialogWidth:"+dialogWidth+"; dialogHeight:"+dialogHeight+";resizable:no;center:yes;scroll:yes;status:no;");
    if(return_vo!=null){
    	var tableid=document.getElementById("tableid").value;
    	document.getElementById("id").value=return_vo;
    	reflesh(tableid);
	}
}
function alertModeFormula(){ 
	var tableid=document.getElementById("tableid").value;
	var id=document.getElementById("id").value;
	if(id==null||id.length<1){
		alert(SELECT_FORMULA_GROUP+"!");
		return;
	}
 	var thecodeurl ="/general/salarychange/setformula.do?b_query=link&tableid="+tableid+"&id="+id+"&flag=alert";
    var dialogWidth="750px";
    var dialogHeight="600px";
    if (isIE6()){
    	dialogWidth="800px";
    	dialogHeight="630px";
    } 
    var return_vo= window.showModalDialog(thecodeurl, "", 
              "dialogWidth:"+dialogWidth+"; dialogHeight:"+dialogHeight+";resizable:no;center:yes;scroll:yes;status:no;"); 
    if(return_vo!=null){
    	var tableid=document.getElementById("tableid").value;
    	reflesh(tableid);
	}
}
function reflesh(tableid){
	var id=document.getElementById("id").value;
	document.setFormulaForm.action="/general/salarychange/fomulatemplate.do?b_query=link&tableid="+tableid+"&id="+id;
	document.setFormulaForm.submit();
}