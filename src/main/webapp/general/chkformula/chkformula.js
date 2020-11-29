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

function setCvalue(chkid){
	chkid=chkid!=null&&chkid.length>0?chkid:getchkId();
	if(chkid==null||chkid.length<1){
		return false;
	}
	document.getElementById("chkid").value=chkid;
	var pars="chkid="+chkid;  
    var request=new Request({method:'post',asynchronous:false,parameters:pars,onSuccess:getCvalue,functionId:'1010092015'});
    tr_bgcolor(chkid);
}
function getCvalue(outparamters){
	var formula=outparamters.getValue("formula");
	document.getElementById("formula").value=getDecodeStr(formula);
}
function changeCodeValue(formula){
  	var item=document.getElementById("itemid").value;
  	var itemid = item.split(":");
  	if (itemid.length <2) return;
	var in_paramters="itemid="+itemid[0];
    var request=new Request({method:'post',asynchronous:false,parameters:in_paramters,onSuccess:showCodeFieldList,functionId:'3020050012'});
	symbol(formula,itemid[1]);
}
function showCodeFieldList(outparamters){
	var codelist=outparamters.getValue("codelist");
	if(codelist.length>1){
		toggles("codeview");
		AjaxBind.bind(chkFormulaForm.codesetid_arr,codelist);
	}else{
		hides("codeview");
	}	
}
function function_Wizard(tableid,formula){
    var thecodeurl ="/org/autostatic/mainp/function_Wizard.do?b_search=link&salaryid=&tableid="+tableid; 
    var return_vo= window.showModalDialog(thecodeurl,"", 
              "dialogWidth:400px; dialogHeight:450px;resizable:no;center:yes;scroll:yes;status:no;");
    if(return_vo!=null){
		symbol(formula,return_vo);
	}	
}
function symbol(editor,strexpr){
	document.getElementById(editor).focus();
	var element = document.selection;
	if (element!=null) {
		var rge = element.createRange();
		if (rge!=null)	
		rge.text=strexpr;
	}
}
function condiTions(){
	var tableid = document.getElementById("tabid").value;
	var conditions = document.getElementById("cfactor").value;
    var thecodeurl ="/general/salarychange/calculating_conditions.do?b_query=link&tableid="+tableid+"&conditions="+conditions; 
    var dialogWidth="520px";
    var dialogHeight="430px";
    if (isIE6()){
   		dialogWidth="550px";
   		dialogHeight="450px";
    } 
    var return_vo= window.showModalDialog(thecodeurl, "", 
              "dialogWidth:"+dialogWidth+"; dialogHeight:"+dialogHeight+";resizable:no;center:yes;scroll:yes;status:no;");
    if(return_vo!=null){
		document.getElementById("cfactor").value=return_vo;   
	}    
}
function getCodesid(formula){
	var codeid="";
	var codesetid_arr= document.getElementsByName("codesetid_arr");
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
		symbol(formula,"\""+codeid+"\"");
	}
}
function onDefSelects(){
	if(this.idArr!=null&&this.idArr.length>0){
		var itemid=this.idArr[0];
		document.getElementById("formula").value=this.formulaArr[0];
		document.getElementById("itemids").value=itemid;
		tr_bgcolor(itemid);
	}
}
function checkCurFormula(check){
	var itemids=document.getElementById("itemids").value;
	var formula=document.getElementById("formula").value;
	var tableid=document.getElementById("tableid").value;
	if(itemids!=null&&itemids.length>4){
		var arr = itemids.split("_");
		if(arr.length==3&&arr[1].length>1){
			var item = arr[1]+"_2="+formula+"`";
  			var hashvo=new ParameterSet();
	    	hashvo.setValue("item",getEncodeStr(item));
	    	hashvo.setValue("check",check);
	   	 	hashvo.setValue("tableid",tableid);
			var request=new Request({method:'post',asynchronous:false,onSuccess:resultCheckExpr,functionId:'3020110060'},hashvo);		
  		}
  	}else{
  		return ;
  	}
}
function saveFormula(tabid,flag){
	var formula=document.getElementById("formula").value;
	var chkid=document.getElementById("chkid").value;
	var view=document.getElementById("savebutton");
	view.disabled = true;
	if(tabid!=null&&tabid.length>0){
  		var hashvo=new ParameterSet();
	    hashvo.setValue("c_expr",getEncodeStr(formula));
	    hashvo.setValue("tabid",tabid);
	   	hashvo.setValue("flag",flag);
	   	hashvo.setValue("chkid",chkid);
		var request=new Request({method:'post',asynchronous:false,onSuccess:checkFormula,functionId:'1010092017'},hashvo);		
  	}else{
  		view.disabled = false;
  		return ;
  	}
}
function checkFormula(outparamters){
	var chkflag = outparamters.getValue("chkflag");
	if(chkflag!='ok'){
		var view=document.getElementById("savebutton");
		view.disabled = false;
		alert(getDecodeStr(chkflag));
		return false;
	}else{
		alert("公式保存成功!");
		var view=document.getElementById("savebutton");
		view.disabled = false;
		return false;
	}
}
function addName(tabid,flag,chkid){
    var thecodeurl ="/general/chkformula/setformula.do?b_add=link&tableid="+tabid+"&flag="+flag+"&chkid="+chkid; 
    var dialogWidth="500px";
    var dialogHeight="300px";
    if (isIE6()){
   		dialogWidth="550px";
   		dialogHeight="350px";
    } 
    var return_vo= window.showModalDialog(thecodeurl, "", 
             "dialogWidth:"+dialogWidth+"; dialogHeight:"+dialogHeight+";resizable:no;center:yes;scroll:yes;status:no;");
    if(return_vo!=null)
   	 	reflesh(tabid,flag,return_vo);   
   	 else
   	 	reflesh(tabid,flag,"");   
}
function setSorting(tabid,flag){
    var thecodeurl ="/general/chkformula/setformula.do?b_sort=link&tabid="+tabid+"&flag="+flag; 
    var dialogWidth="360px";
    var dialogHeight="400px";
    if (isIE6()){
   		dialogWidth="400px";
   		dialogHeight="420px";
    } 
    var return_vo= window.showModalDialog(thecodeurl, "", 
             "dialogWidth:"+dialogWidth+"; dialogHeight:"+dialogHeight+";resizable:no;center:yes;scroll:yes;status:no;");
    if(return_vo!=null)
   	 	reflesh(tabid,flag,'');     
}
function delVariables(tabid,flag){
	var chkid="";
	var tablevos=document.getElementsByTagName("input");
	for(var i=0;i<tablevos.length;i++){
	    if(tablevos[i].type=="checkbox"){
	     	if(tablevos[i].checked){
	     		chkid+=tablevos[i].value+",";
	     	}
		}
    }
    if(chkid==null){
    	alert(SELECT_CHECK_PROJECT+"!");
    	return;
    }
    if(!ifdel()){
    	return ;
    }
     
	var hashvo=new ParameterSet();
	hashvo.setValue("chkid",chkid);
	var request=new Request({asynchronous:false,functionId:'1010092016'},hashvo);
	reflesh(tabid,flag,"");
}
function reflesh(tabid,flag,chkid){
	chkid=chkid!=null&&chkid.length>0?chkid:"";
	document.chkFormulaForm.action="/general/chkformula/setformula.do?b_query=link&tableid="+tabid+"&flag="+flag+"&chkid="+chkid;
    document.chkFormulaForm.submit();   
} 
function getchkId(){
	var chkid = "";
	var tablevos=document.getElementsByTagName("input");
	for(var i=0;i<tablevos.length;i++){
	    if(tablevos[i].type=="checkbox"){
	     	chkid=tablevos[i].value;
	     	break;
		}
    }
    return chkid;
}

