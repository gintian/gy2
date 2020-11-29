var  selectformulaId;
var  selecttabName;
var formulareadonly;
var frowcolflag;//行公式1 列公式 2
var ftab_type;//当前预算表类别
var fformula_type;//当前预算表公式类别
var fchktablecell=false;//选择单元格

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

function toggles1(targetId){
	if (document.getElementById(targetId)){
		target = document.getElementById(targetId);
		target.style.display = "inline";
	}
} 

function change_style(targetId,csstype){
	if (document.getElementById(targetId)){
		target = document.getElementById(targetId);
        target.setAttribute("class",csstype);//Mozilla设置class的方法
        target.setAttribute("className",csstype);//IE设置class的方法    
	}

 }
 
 function disable(targetId){
	if (document.getElementById(targetId)){
		target = document.getElementById(targetId);
        target.setAttribute("disabled",true);

	}

 }

 function enable(targetId){
	if (document.getElementById(targetId)){
		target = document.getElementById(targetId);
        target.setAttribute("disabled",false);

	}

 }
 function setReadonly(targetId){
	if (document.getElementById(targetId)){
		target = document.getElementById(targetId);
        target.setAttribute("readOnly",true);

	}

 }

 function clearReadonly(targetId){
	if (document.getElementById(targetId)){
		target = document.getElementById(targetId);
        target.setAttribute("readOnly",false);

	}

 }

function symbol(editor,strexpr){
    if (formulareadonly=="true")
      return;
	document.getElementById(editor).focus();
	var element = document.selection;
	if (element!=null) {
		var rge = element.createRange();
		if (rge!=null)	
		rge.text=strexpr;
	}
}
 function returnbudgetdef() {
	var thecodeurl = "/gz/gz_budget/budget_rule/definition.do?b_query=link";
	window.location.href = thecodeurl;
}

function Localsymbol(strexpr){
	document.getElementById("formuladcrp").focus();
	var element = document.selection;
	if (element!=null) {
		var rge = element.createRange();
		if (rge!=null)	
		rge.text=strexpr;
	}
}

function add_formula() {
	var target_url = "/gz/gz_budget/budget_rule/formula.do?b_add=link&mode=add";
if(isIE6()){
	var newwindow = window.showModalDialog(target_url, "", "dialogWidth:530px; dialogHeight:230px;resizable:no;center:yes;scroll:yes;status:no");

}else{
	var newwindow = window.showModalDialog(target_url, "", "dialogWidth:500px; dialogHeight:210px;resizable:no;center:yes;scroll:yes;status:no");

}
	if (newwindow != null) {
		var tab_id =budgetformulaForm.tab_id.value; 
		var flag = "add";
		budgetformulaForm.action="/gz/gz_budget/budget_rule/formula.do?b_query=link&flag="+flag+"&tab_id="+tab_id;;
		budgetformulaForm.submit();
	}
}
function insert_formula() {
	var target_url = "/gz/gz_budget/budget_rule/formula.do?b_add=link&mode=insert&curformulaid="+ selectformulaId;
	/* 预算表设置 计算公式 新增 去掉滚动条 2014-9-13 start */
	//var newwindow = window.showModalDialog(target_url, "", "dialogWidth:500px; dialogHeight:200px;resizable:no;center:yes;scroll:yes;status:no");
	var newwindow = window.showModalDialog(target_url, "", "dialogWidth:500px; dialogHeight:210px;resizable:no;center:yes;scroll:yes;status:no");
	/* 预算表设置 计算公式 新增 去掉滚动条 2014-9-13 end */
	if (newwindow != null) {
		var tab_id =budgetformulaForm.tab_id.value; 
		var flag = "add";
		budgetformulaForm.action="/gz/gz_budget/budget_rule/formula.do?b_query=link&flag="+flag+"&tab_id="+tab_id;
		budgetformulaForm.submit();
	}
}
function del_formula(tab_id) {
	var tab_id ; 
	var strIds = "";
	var dd = false;
	var index = 0;
	var obj = document.getElementsByName("ids");
	for (var i = 0; i < document.budgetformulaForm.elements.length; i++) {
		if (document.budgetformulaForm.elements[i].type == "checkbox"&&document.budgetformulaForm.elements[i].name!="quanxuan") {
			if (document.budgetformulaForm.elements[i].checked) {
				dd = true;
				strIds = strIds + obj[index].value + ",";
			}
			index++;
		}
	}
	if (!dd) {
		alert(REPORT_INFO28);
		return;
	} else {
		strIds = strIds.substring(0, strIds.length - 1) ;
		if (confirm(GZ_REPORT_CONFIRMDELETE)) {
			var hashvo = new ParameterSet();
			hashvo.setValue("ids", getEncodeStr(strIds));
			hashvo.setValue("tab_id", getEncodeStr(tab_id));
			var request = new Request({asynchronous:false, onSuccess:del, functionId:"302001020492"}, hashvo);
		}
	}
}
function del(outparamters) {
	var flag = "del";
	var formula_id1 = outparamters.getValue("formula_id1");
	var tab_id = outparamters.getValue("tab_id");
	var thecodeurl = "/gz/gz_budget/budget_rule/formula.do?b_query=link&flag="+flag+"&formula_id1="+formula_id1+"&tab_id="+tab_id;
	window.location.href = thecodeurl;
}
function moveRecord(formula_id, seq, move) {
	var tab_id=document.getElementsByName("tab_id")[0].value;
	var hashvo = new ParameterSet();
	hashvo.setValue("formula_id", getEncodeStr(formula_id));
	hashvo.setValue("seq", getEncodeStr(seq));
	hashvo.setValue("move", getEncodeStr(move));
	hashvo.setValue("tab_id", getEncodeStr(tab_id));
	var request = new Request({asynchronous:false, onSuccess:seq_ok, functionId:"302001020489"}, hashvo);
}
function seq_ok(outparamters) {
	var formula_id = outparamters.getValue("formula_id");
	var tab_id=document.getElementsByName("tab_id")[0].value;
	var flag = "seq";
	budgetformulaForm.action = "/gz/gz_budget/budget_rule/formula.do?b_query=link&tab_id="+tab_id+"&formula_id="+formula_id+"&flag="+flag;
	budgetformulaForm.submit();
}

function function_wizard(formuladcrp) {
	var thecodeurl = "/org/autostatic/mainp/function_Wizard.do?b_query=link&checktemp=ysgs&mode=ysgl";
	var return_vo = window.showModalDialog(thecodeurl, "", "dialogWidth:400px; dialogHeight:430px;resizable:no;center:yes;scroll:yes;status:no");
	if (return_vo != null) {
		symbol(formuladcrp, return_vo);
	}
}

function checkformula(formula_id){
   if (selectformulaId==formula_id) { return;}
	var hashvo=new ParameterSet();
	hashvo.setValue("formula_id",formula_id);
	selectformulaId =formula_id;
	
	hashvo.setValue("formulacontent"," ");  
	hashvo.setValue("rowcolflag"," ");  
	var request=new Request({asynchronous:false,onSuccess:getformulacontent,functionId:'302001020497'},hashvo); 	
	
	var request=new Request({asynchronous:false,onSuccess:getformulavalue,functionId:'302001020493'},hashvo);
	formula_id=formula_id+"";
	if (formula_id != null && formula_id.length > 0) {
		tr_bgcolor(formula_id);
	  }
}

function getformulavalue(outparamters){
	var tab_type = outparamters.getValue("tab_type");
	var formula_type = outparamters.getValue("formula_type");	
	var tab_name = outparamters.getValue("tab_name");	
	selecttabName=tab_name;
	
	SetRefProVisible(tab_type,formula_type,frowcolflag);

	var itemlist=outparamters.getValue("setlist");
	if ((itemlist !=null)) {
	  AjaxBind.bind(budgetformulaForm.fieldsetlist,itemlist);	
    }
	var itemlist1=outparamters.getValue("colitemlist");	
	if ((itemlist1 !=null)) {
	  AjaxBind.bind(budgetformulaForm.fieldcollist,itemlist1);
	}
}

function getformulacontent(outparamters){
		
	var formulacontent= outparamters.getValue("formulacontent");
	document.getElementById("formuladcrp").value = getDecodeStr(formulacontent);
	
	var rowcolflag = outparamters.getValue("rowcolflag");	
	setRowcolflagValue(rowcolflag);
	frowcolflag=rowcolflag;
}

function saveformulacontent(){
	var formula=document.getElementById("formuladcrp").value;
 	var hashvo=new ParameterSet();
    hashvo.setValue("flag","checkformula");
    hashvo.setValue("formulacontent",getEncodeStr(formula));
    hashvo.setValue("formula_id",selectformulaId);
	var In_paramters=""; 		
	var request=new Request({method:'post',asynchronous:false,
		parameters:In_paramters,onSuccess:sucessCheckformula,functionId:'302001020493'},hashvo);	

}

function sucessCheckformula(outparamters){
  	var info = outparamters.getValue("info");
	if(info=="true"){	
		var formula=document.getElementById("formuladcrp").value;
		var hashvo=new ParameterSet();
		hashvo.setValue("formula_id",selectformulaId);	
		hashvo.setValue("formulacontent",getEncodeStr(formula));	
		hashvo.setValue("rowcolflag",getRowcolflagValue());	
		var request=new Request({method:'post',asynchronous:false,onSuccess:sucesscheckrowcol,functionId:'302001020498'},hashvo);
	}
	else{
		var strerror = outparamters.getValue("error");
		alert(getDecodeStr(strerror));

	}
}
function sucesscheckrowcol(outparamters){
//检查行列范围是否设置了
 	var hashvo=new ParameterSet();
    hashvo.setValue("flag","checkrowcol");
    hashvo.setValue("formula_id",selectformulaId);
	var In_paramters=""; 		
	var request=new Request({method:'post',asynchronous:false,
		parameters:In_paramters,onSuccess:sucessSaveformula,functionId:'302001020493'},hashvo);	

  	
}

function sucessSaveformula(outparamters){
  	var info = outparamters.getValue("info");
	if(info=="true"){	
		alert(SAVE_FORMULA_OK);
	
	}
	else {
		var strerror = outparamters.getValue("error");
		alert(getDecodeStr(strerror));
	}

	
}


function getRowcolflagValue(){
     var arr = document.getElementsByName("rowColMode");   
     if (arr==null){return 1} 
     var checkedId ;
     var radioValue ='1'; 
     for(var i=0;i<arr.length;i++){
        if(arr[i].checked){
                radioValue = arr[i].value;
                checkedId = i;    
        }
     }
     
     return radioValue;
 }
function setRowcolflagValue(flag){
     var arr = document.getElementsByName("rowColMode");    
     if (arr==null){return 1} 
     var checkedId ;
     var radioValue ; 
     for(var i=0;i<arr.length;i++){
             if(arr[i].value==flag){
                     arr[i].checked=true;    
             }
     }
     
     return radioValue;
 }




function defCheck(formula_id){  
  checkformula(formula_id,"");
}

function tr_bgcolor(formula_id) {
	var tablevos = document.getElementsByTagName("input");
	for (var i = 0; i < tablevos.length; i++) {
		if (tablevos[i].type == "hidden") {
			var cvalue = tablevos[i];
			var td = cvalue.parentNode.parentNode;
			td.style.backgroundColor = "";
		}
	}
	var c = document.getElementById(formula_id);
	if (c!=null) {
		var tr = c.parentNode.parentNode;
		if (tr.style.backgroundColor != "") {
			tr.style.backgroundColor = "";
		} else {
			tr.style.backgroundColor = "#FFF8D2";
		}
		
	}

}
function openColumn(formula_id) {
//判断是否需要显示列范围
	if (ftab_type==2){//员工名册	
		if ((fformula_type==2) && (frowcolflag==2)) {//列公式
			return;
		}else if (fformula_type==3) {
			return;
		}else if (fformula_type==1) {
			return;
		}
	
	}
	var iframe_url = "/gz/gz_budget/budget_rule/formula.do?b_column=link&formula_id=" + formula_id;
	var return_value = window.showModalDialog(iframe_url, "", "dialogWidth:300px; dialogHeight:450px;resizable:no;center:yes;scroll:yes;status:no");
}
/*选择所有选项*/
function checkAll() {
	var tablevos = document.getElementsByTagName("input");
	for (var i = 0; i < tablevos.length; i++) {
		if (tablevos[i].type == "checkbox") {
			tablevos[i].checked = true;
		}
	}
}

function checkrowcolflag(flag) {
  frowcolflag=flag;

	var texta= document.getElementById("formuladcrp");
	if (texta!=null){
	 	texta.value='';
	}

	var hashvo=new ParameterSet();
	hashvo.setValue("formula_id",selectformulaId);	
	hashvo.setValue("formulacontent","");	
	hashvo.setValue("rowcolflag",getRowcolflagValue());	
	var request=new Request({asynchronous:false,functionId:'302001020498'},hashvo);		
    SetRefProVisible(ftab_type,fformula_type,frowcolflag);
    
    var hashvo=new ParameterSet();
	hashvo.setValue("itemid",getColvalue(0));
	hashvo.setValue("flag","getcodelist");	
	var request=new Request({method:'post',asynchronous:false,onSuccess:showCodeFieldList,functionId:'302001020493'},hashvo);	
}

function checktablecell( obj){
	if (obj.checked){	
		fchktablecell=true;
		toggles("btn_ok");
	}
	else{	
		fchktablecell=false;
		hides("btn_ok");
	}



}

function SetRefProVisible(Tab_type,formula_type,rowcolflag) {  
  frowcolflag=rowcolflag;
  ftab_type=Tab_type;
  fformula_type=formula_type;
  hides("codeview");
  enable("btn_save");
  enable("btn_wizard");
  enable("btn_calc");
  disable("btn_tj");
  
  hides("btn_tj");
  toggles1("btn_calc");
  toggles1("btn_wizard");
  toggles1("btn_save");
  
  clearReadonly("formuladcrp");  
  formulareadonly=""; 

  hides("formularowtype");    
  hides("lblformulacontent");    
  
  toggles("trReference1");  
  toggles("trReference");  
  toggles("trbtns");  
  hides("trReference2");
  
  hides("sptablecell");
  var dcrp =document.getElementById("formuladcrp");
  dcrp.style.height="180px";
 // alert(Tab_type +'   '+formula_type);
  if ((formula_type=="3") && (Tab_type=="2")) {//名册的导入项
	  toggles("setvalue1");
	  hides("setvalue2");       
	  toggles("colvalue1");
	  hides("colvalue2");  	      
	  
	  toggles("setview");
	  hides("rowview");
	  toggles("colview");
	  dcrp.style.height="210px";

   }
   else if(formula_type=="3"){//其他预算表的导入项
	  toggles("setvalue2");
	  hides("setvalue1");
	  toggles("colvalue2");
	  hides("colvalue1");    
	   	  
	  toggles("setview");
	  hides("rowview");
	  toggles("colview");
	  dcrp.style.height="210px";
  }
  else if ((formula_type=="2") && (Tab_type=="2")) {//名册的计算项
 	  toggles("formularowtype"); 
	  toggles("lblformulacontent"); 
      if (rowcolflag==2 ) {      
		  toggles("setvalue2");
		  hides("setvalue1");      
		  toggles("colvalue2");
		  hides("colvalue1"); 

		  toggles("setview");
		  hides("rowview");
		  toggles("colview");
		  
      }
      else{          
		  hides("setview");
		  hides("rowview");
		  hides("colview"); 
		  
		  enable("btn_tj");
		  toggles1("btn_tj");
		//  disable("btn_calc");		  
		//  disable("btn_wizard")
		 // disable("btn_save");
		  hides("btn_calc");
		  hides("btn_wizard");
		  hides("btn_save");
  
		  if (dcrp!=null){
		   	dcrp.value='请通过统计条件设置合计条件，列范围设置合计的指标列。';
		  }
		  setReadonly("formuladcrp");  
		  formulareadonly="true";
      }
   }
  else if ((formula_type=="2")) {//其他预算表计算项
	  toggles("setvalue2");
	  hides("setvalue1");
	  toggles("colvalue2");
	  hides("colvalue1");
	  
	  toggles("formularowtype"); 
	  toggles("lblformulacontent"); 
	  
	  toggles("setview");
	  toggles("rowview");
	  toggles("colview"); 
	  
	  toggles("sptablecell");
	  if (fchktablecell){	  
	    toggles("btn_ok");
	  } 
	  else{	  
	  	hides("btn_ok");
	  }
	  hides("btn_wizard")
	  toggles("trReference2");  
	  hides("trReference1");
    
   }  
  else {//录入项
	  hides("setview");
	  hides("rowview");
	  hides("colview"); 
	  
	  hides("trReference");  
	  //hides("trbtns");  
	  

	  hides("btn_tj");
	  hides("btn_wizard");
	  hides("btn_save");
	  toggles1("btn_calc");
	  

	  if (dcrp!=null){
	   	dcrp.value='录入项指标不需要设置公式。';
	  }
   
	  setReadonly("formuladcrp"); 
	  formulareadonly="true";
	  dcrp.style.height="390px";
  } 

}

function getfieldsetid(){
	var itemid="";
	var fieldsetlist= document.getElementsByName("fieldsetlist");
	var fieldsetlist_vo = fieldsetlist[0];
	if(fieldsetlist==null){
		return "";
	}else{
		for(var i=0;i<fieldsetlist_vo.options.length;i++){
			if(fieldsetlist_vo.options[i].selected){
				itemid =fieldsetlist_vo.options[i].value;
				continue;}
		}
		return itemid;
	}
}
function changeset(){
	hides("codeview");
    var fieldsetid=getfieldsetid().split(":");
    if(fieldsetid==null||fieldsetid==undefined||fieldsetid.length<2){
    	return;
    }
	var hashvo=new ParameterSet();
	hashvo.setValue("formula_id",selectformulaId);
	hashvo.setValue("fieldsetid",fieldsetid[0]);	
	hashvo.setValue("flag","getfldsetlist");	
	var request=new Request({method:'post',asynchronous:false,onSuccess:showItemList,functionId:'302001020493'},hashvo);	
}
function showItemList(outparamters){	
	var itemlist=outparamters.getValue("colitemlist");
	AjaxBind.bind(budgetformulaForm.fieldcollist,itemlist);	
	var itemlist1=outparamters.getValue("rowitemlist");
	AjaxBind.bind(budgetformulaForm.fieldrowlist,itemlist1);	
}


function getRowItemid(){
	var itemid="";
	var itemid_arr= document.getElementsByName("fieldrowlist");
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


function savetablecell(){

   var rowid=getRowvalue(1);
   if (rowid==""){
		return;
   }
   
   var colid=getColvalue(1);
   if (colid==""){
		return;
   }   
  
   var fieldsetid=getFieldsetvalue(1);
   if (fieldsetid==""){
		return;
   }
   
   var expr=""; 
   if(fieldsetid==selecttabName){
    	expr= getRowvalue(0)+":"+colid 
   }
   else{
   		expr= fieldsetid+"."+rowid+":"+colid   	
   }  
   
   Localsymbol("["+ expr+"]");

}

function getColvalue(flag){
   var itemid=getItemid().split(":");
    if(itemid==null||itemid==undefined||itemid.length<2){
        if ((itemid.length==1) && (itemid[0]=='*')){
        	return "*";
        }
        else{
       		return "";
        }
    }
   return itemid[flag]; 
}
function getRowvalue(flag){
   var itemid=getRowItemid().split(":");
    if(itemid==null||itemid==undefined||itemid.length<2){
        if ((itemid.length==1) && (itemid[0]=='*')){
        	return "*";
        }
        else{
       		return "";
        }
    }
   return itemid[flag]; 
}
function getFieldsetvalue(flag){
   var itemid=getfieldsetid().split(":");
    if(itemid==null||itemid==undefined||itemid.length<2){
       		return "";
    }
   return itemid[flag]; 
}

function changerow(){
   if ((ftab_type=="2")) {//员工名册
   
   }  
   else{//其他预算表   
	   if (fchktablecell  && (fformula_type=="2")) { //其他预算表 计算项 单元格选中
	   		return;
	   }
	   if ((fformula_type=="2") && (frowcolflag==2)){//其他预算表 计算项 列公式
	   		return;
	   }
   }
  
   var itemid=getRowvalue(1);
   if (itemid==""){
		return;
   }
   var fieldsetid=getFieldsetvalue(1);
   if (fieldsetid==""){
		return;
   }   
   
   var expr="";    
   if ((ftab_type=="2")) {//员工名册 导入项
	   expr=itemid;
	   if (fformula_type=="3") {//导入项
	  	    Localsymbol(expr); 	   
	   }
	   else{//计算项	   
	    	Localsymbol(expr); 
	   }
   }  
   else{//其他预算表   
	   if(fieldsetid==selecttabName){
	   	expr=getRowvalue(0)
	   }
	   else{
	     expr= fieldsetid+"."+itemid
	   }  
	    Localsymbol("["+ expr+"]");
   }
}

function getItemid(){
	var itemid="";
	var itemid_arr= document.getElementsByName("fieldcollist");
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

function changecol(){
   hides("codeview");
   
   if ((ftab_type=="2")) {//员工名册
   
   }else{//其他预算表   
	   if (fchktablecell  && (fformula_type=="2")) { //其他预算表 计算项 单元格选中
	   		return;
	   }
	   if ( (fformula_type=="2") && (frowcolflag==1)){//其他预算表 计算项 行公式
	   		return;
	   }
   }


   //alert(fchktablecell+ " "+ftab_type +" "+fformula_type);
  
   var itemid=getColvalue(1);
   if (itemid==""){
		return;
   }   
  
   var fieldsetid=getFieldsetvalue(1);
   if (fieldsetid==""){
		return;
   }
   
   var expr="";    
   if ((ftab_type=="2")) {//员工名册 导入项
	   expr=itemid;
	   if (fformula_type=="3") {//导入项
	  	    Localsymbol(expr); 	   
	   }
	   else{//计算项	   
	    	//Localsymbol("["+ expr+"]");
	    	Localsymbol( expr);
	   }
   }  
   else{//其他预算表   
    	if (fformula_type=="2") {
		   if(fieldsetid==selecttabName){
		   	expr=itemid
		   }
		   else{
		     expr= fieldsetid+"."+itemid
		   }  
    	
    	}
    	else {
    		expr=itemid
    	}

	   if (fformula_type=="2"){	   
	    	Localsymbol("["+ expr+"]");
	   } else {
   			Localsymbol(""+ expr+"");
	   }
   }
  
    var hashvo=new ParameterSet();
    var tab_id="";
	hashvo.setValue("itemid",getColvalue(0));
	hashvo.setValue("flag","getcodelist");	
	if ((itemid="itemid") ||(itemid="itemdesc")){
		tab_id=getFieldsetvalue(0)
	}else {
		tab_id="";
	}
	hashvo.setValue("tab_id",tab_id);	
	var request=new Request({method:'post',asynchronous:false,onSuccess:showCodeFieldList,functionId:'302001020493'},hashvo);	
}

function showCodeFieldList(outparamters){	
	var codelist=outparamters.getValue("codeitemlist");
	if(codelist!=null&&codelist.length>1){
		toggles("codeview");
		AjaxBind.bind(budgetformulaForm.fieldcodelist,codelist);
	}else{
		hides("codeview");
	}	
}  

function changecode(){
	var codeid="";
	var codesetid_arr= document.getElementsByName("fieldcodelist");
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
		if(codeid==null||codeid==undefined||codeid.length<1){
			return;
		}
		Localsymbol("\""+codeid+"\"");
	}
}

/*清除所有选项*/
function clearAll() {
	var tablevos = document.getElementsByTagName("input");
	for (var i = 0; i < tablevos.length; i++) {
		if (tablevos[i].type == "checkbox") {
			tablevos[i].checked = false;
		}
	}
}
function othersave() {
	var dd = false;
	var dd2=true;//判断是否选中了多条
	var k=1;
	var index = 0;
	for (var i = 0; i < document.budgetformulaForm.elements.length; i++) {
		if (document.budgetformulaForm.elements[i].type == "checkbox"&&document.budgetformulaForm.elements[i].name!="quanxuan") {
			if (document.budgetformulaForm.elements[i].checked) {
				dd = true;
				var formula_id = document.getElementsByName("ids")[index].value;
				if(k>1)
				{
					dd2=false;
					break;
				}
				k++;
			}
			index++;
		}
	}
	
	 if(!dd2)
	 {
	 	alert(GZ_BUDGET_INFO5);
	 	return;
	 } 
	if (!dd) {
		alert(SELECT_ONE_RECORD);
		return;
	} else {
		var target_url = "/gz/gz_budget/budget_rule/formula.do?b_othersave=link&formula_id=" + formula_id;
		var newwindow = window.showModalDialog(target_url, "", "dialogWidth:500px; dialogHeight:150px;resizable:no;center:yes;scroll:yes;status:no");
			if (newwindow != null) {
				var thecodeurl = "/gz/gz_budget/budget_rule/formula.do?b_query=link";
				window.location.href = thecodeurl;
	}
	}
}


function setformulatype(runvalue){
    var formulatype = runvalue.value;
	var hashvo=new ParameterSet();
	hashvo.setValue("formula_id",selectformulaId);
	hashvo.setValue("formula_type",formulatype);
	hashvo.setValue("flag","updateformulatype");
	var request=new Request({asynchronous:false,onSuccess:sucesssetformulatype,functionId:'302001020493'},hashvo);
	
	
}

function sucesssetformulatype(outparamters){
  	var formula_id = outparamters.getValue("formula_id");
	selectformulaId ='';
	checkformula(formula_id);

}




function selectformula(){
	var tab_id=document.getElementsByName("tab_id")[0].value;
	budgetformulaForm.action="/gz/gz_budget/budget_rule/formula.do?b_query=link&tab_id="+tab_id;
	budgetformulaForm.submit();
}
function saveColumn(){
	var index = 0;
	var a = "";
	var formula_id = "";
	var obj = document.getElementsByName("aaa");
	var bbb = document.getElementsByName("bbb");
	for (var i = 0; i < document.budgetformulaForm.elements.length; i++) {
		if (document.budgetformulaForm.elements[i].type == "checkbox"&&document.budgetformulaForm.elements[i].name!="quanxuan") {
			if (document.budgetformulaForm.elements[i].checked) {
				
				a = a + obj[index].value + ",";
			}
			index++;
		}
	}
	a = a.substring(0, a.length - 1);
	formula_id = formula_id+bbb[0].value;
	formula_id = formula_id.substring(0, formula_id.length);
	var hashvo=new ParameterSet();
	hashvo.setValue("formula_id",formula_id);	
	hashvo.setValue("colrange",a);	
	var request=new Request({asynchronous:false,onSuccess:window.close,functionId:'302001020498'},hashvo);	
}

function setcond(){
    var thecodeurl ="/gz/gz_budget/budget_rule/formula.do?b_condition=link&formula_id="+selectformulaId+"&tj_type=tjwhere"; 
    var return_vo= window.showModalDialog(thecodeurl, "", 
              "dialogWidth:520px; dialogHeight:430px;resizable:no;center:yes;scroll:yes;status:no");                   
    return return_vo;  			
}

function openRow(){
    var thecodeurl ="/gz/gz_budget/budget_rule/formula.do?b_condition=link&formula_id="+selectformulaId+"&tj_type=rowscope"; 
    var return_vo= window.showModalDialog(thecodeurl, "", 
              "dialogWidth:550px; dialogHeight:400px;resizable:no;center:yes;scroll:yes;status:no");      // modify by xiaoyun 2014-8-28
                 
    return return_vo; 		
}

function symbol_condition(cal){
  LocalCondsymbol(cal)	
}

function savecond(formula_id,tj_type){
	var formula=document.getElementById("cond_value").value;
	formula=formula!=null?formula:"";
	
 	var hashvo=new ParameterSet();
	if (tj_type =='tjwhere'){
	  	btj="true";
	 }else{
	  	btj="false"
  	}	
  	
    hashvo.setValue("flag","checkcondformula");
    hashvo.setValue("btj",btj);
    hashvo.setValue("formulacontent",getEncodeStr(formula));
    hashvo.setValue("formula_id",formula_id);
	var In_paramters=""; 		
	var request=new Request({method:'post',asynchronous:false,
		parameters:In_paramters,onSuccess:sucessCheckcond,functionId:'302001020493'},hashvo);	
		
}

function sucessCheckcond(outparamters){  
  	var info = outparamters.getValue("info");
	if(info=="true"){	
		var formula=document.getElementById("cond_value").value;
	  	var hashvo=new ParameterSet();  	
		hashvo.setValue("cond_value",getEncodeStr(formula));
		hashvo.setValue("formula_id",formula_id);
		if (tj_type =='tjwhere'){
		  hashvo.setValue("cType","savecond");
		 }else{
		  hashvo.setValue("cType","saverow");
	  	}		
		var request=new Request({method:'post',asynchronous:false,onSuccess:successsavecond,functionId:'302001020480'},hashvo);	
	}
	else{
		var strerror = outparamters.getValue("error");
		alert(getDecodeStr(strerror));
	}
}
function successsavecond(){  
  alert(SAVESUCCESS);
}

function defSetCondView(formula_id,tj_type){
	if (tj_type =='rowscope') {
	  hides("condsetview");	
	 var hashvo=new ParameterSet();
	 hashvo.setValue("formula_id",formula_id);
	 hashvo.setValue("cType","curTabColItem");	
	 var request=new Request({method:'post',asynchronous:false,onSuccess:showCondItemList,functionId:'302001020480'},hashvo);	  
   }			
}

function getcondfieldsetid(){
	var itemid="";
	var fieldsetlist= document.getElementsByName("cond_setlist");
	var fieldsetlist_vo = fieldsetlist;
	if(fieldsetlist==null){
		return "";
	}else{
		for(var i=0;i<fieldsetlist_vo.options.length;i++){
			if(fieldsetlist_vo.options[i].selected){
				itemid =fieldsetlist_vo.options[i].text;
				continue;}
		}
		return itemid;
	}
}

function LocalCondsymbol(strexpr){
	document.getElementById("cond_value").focus();
	var element = document.selection;
	if (element!=null) {
		var rge = element.createRange();
		if (rge!=null)	
		rge.text=strexpr;
	}
	
}

function CondChangeSet(formula_id){
	hides("condcodeview");
	
  	var item=document.getElementById("cond_setid").value;
  	var itemid = item.split(":");
  	var fieldsetid= itemid[0];
  	var fieldsetdesc= itemid[1];
    if(fieldsetid==null||fieldsetid==undefined||fieldsetid.length<2){
    	return;
    }
	var hashvo=new ParameterSet();
	hashvo.setValue("formula_id",formula_id);
	hashvo.setValue("fieldsetid",fieldsetid);	
	hashvo.setValue("cType","colitem");	
	var request=new Request({method:'post',asynchronous:false,onSuccess:showCondItemList,functionId:'302001020480'},hashvo);	
}

function showCondItemList(outparamters){	
	var itemlist=outparamters.getValue("cond_itemlist");
	AjaxBind.bind(budgetformulaForm.cond_itemlist,itemlist);	
}


function getCondItemid(){
	var itemid="";
	var itemid_arr= document.getElementsByName("cond_itemlist");
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

function CondChangeCol(formula_id,tj_type){
   hides("condcodeview");
   var itemid=getCondItemid().split(":");
    if(itemid==null||itemid==undefined||itemid.length<2){
    	return;
    }
    if (tj_type=="tjwhere") {    
	  	var fieldset=document.getElementById("cond_setid").value;
	  	var fieldsetid = fieldset.split(":");    
	  //  LocalCondsymbol(fieldsetid[1] +'.'+itemid[1]);
	    LocalCondsymbol(itemid[1]);
     }else {    
      LocalCondsymbol(itemid[1]);    
     }
	
    var hashvo=new ParameterSet();
	hashvo.setValue("itemid",itemid[0]);
	hashvo.setValue("formula_id",formula_id);		
	hashvo.setValue("cType","codeitem");		
	var request=new Request({method:'post',asynchronous:false,onSuccess:showCondCodeFieldList,functionId:'302001020480'},hashvo);	
}

function showCondCodeFieldList(outparamters){	
	var codelist=outparamters.getValue("cond_codelist");
	if(codelist!=null&&codelist.length>1){
		toggles("condcodeview");
		AjaxBind.bind(budgetformulaForm.cond_codelist,codelist);
	}
}  

function CondChangeCode(formula_id){
	var codeid="";
	var codesetid_arr= document.getElementsByName("cond_codelist");
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
		if(codeid==null||codeid==undefined||codeid.length<1){
			return;
		}
		LocalCondsymbol("\""+codeid+"\"");
	}
}
function selectzhibiao(tab,l1,l2){
  var tab_id = tab.value;
  Add_selectTab(tab_id,l1,l2);
}

function Add_selectTab(tab_id,l1,l2){
	  if(tab_id=="1") 
  {  
    hides("zhibiao");
    toggles("zhibiao1");
    document.budgetformulaForm.formulaname.value = l2;
  }else if(tab_id=="2"){
  	hides("zhibiao1");
  	toggles("zhibiao");
  	document.budgetformulaForm.formulaname.value = l1;
  }else{
  	hides("zhibiao1");
  	hides("zhibiao");
  	document.budgetformulaForm.formulaname.value = "";
  }
}

function modifyname(){
	var dd = false;
	var dd2=true;//判断是否选中了多条
	var k=1;
	var index = 0;
	for (var i = 0; i < document.budgetformulaForm.elements.length; i++) {
		if (document.budgetformulaForm.elements[i].type == "checkbox"&&document.budgetformulaForm.elements[i].name!="quanxuan") {
			if (document.budgetformulaForm.elements[i].checked) {
				dd = true;
				var formula_id = document.getElementsByName("ids")[index].value;
				if(k>1)
				{
					dd2=false;
					break;
				}
				k++;
			}
			index++;
		}
	}
	 if(!dd2)
	 {
	 	alert(GZ_BUDGET_INFO5);
	 	return;
	 }   
	if (!dd) {
		alert(SELECT_ONE_RECORD);
		return;
	} else {
			var hashvo=new ParameterSet();
			hashvo.setValue("formula_id",formula_id);
			hashvo.setValue("flag","getformulaname");	
			var request=new Request({asynchronous:false,onSuccess:updateformulaname,functionId:'302001020493'},hashvo);
	}
}


function updateformulaname(outparamters){
	var formulaname = outparamters.getValue("formula_name");	
    var thecodeurl ="formula/budget_modifyformulaname.jsp?formulaname="+$URL.encode(formulaname)+""; 
    var retvo= window.showModalDialog(thecodeurl, "", 
              "dialogWidth:400px; dialogHeight:150px;resizable:no;center:yes;scroll:yes;status:no");  
    if (retvo==null) return;                           	
	if(retvo.success=="1")	{
		var hashvo=new ParameterSet();"hrms/gz/gz_budget/budget_rule/formula/budget_formula.js"
		hashvo.setValue("formula_id",selectformulaId);
		hashvo.setValue("formula_name",retvo.formulaname);
		hashvo.setValue("flag","updateformulaname");
		var request=new Request({asynchronous:false,onSuccess:sucessupdatename,functionId:'302001020493'},hashvo);
	}
}

function sucessupdatename(outparamters){
	var formulaid = outparamters.getValue("formula_id");	
	var formulaname = outparamters.getValue("formula_name");	
	localmodifyname(formulaid,formulaname)
	
//	var thecodeurl = "/gz/gz_budget/budget_rule/formula.do?b_query=link";
	//window.location.href = thecodeurl;
}

function localmodifyname(formula_id,formula_name) {
	var c = document.getElementById(formula_id);
	if (c!=null) {
		var tr = c.parentNode.parentNode;
		tr.cells[2].innerHTML="&nbsp;";
		tr.cells[2].innerHTML+="&nbsp;";
		tr.cells[2].innerHTML+=formula_name;
	}
}
