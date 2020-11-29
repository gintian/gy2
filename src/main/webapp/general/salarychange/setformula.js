var idArr=new Array();
var formulaArr=new Array();
var chzArr=new Array();
var itemid2 =null;
function initArr(){
	var item=document.getElementById("item").value;
	var chz=document.getElementById("chz_arr").value;
	if(item==null||item.length<1||chz==null||chz.length<1)
		return;
	var arr = item.split("`");
	var chz_arr = chz.split(",");
	var n=0;
	for(var i=0;i<arr.length;i++){
		if(arr[i].length>1){
			this.idArr[n] = arr[i].substring(0,arr[i].indexOf("="));
			this.formulaArr[n] = arr[i].substring(arr[i].indexOf("=")+1,arr[i].length);
			this.chzArr[n]=chz_arr[i];
			n++;
		}
	}
}
initArr();
function onSelects(itemid){
	
	var formula='';
	if(itemid==null||itemid.length<1)
		return;
	var arr = itemid.split("_");
	var selectid="new";
	if(arr.length==3){
		var n=parseInt(arr[0]);
		formula=this.formulaArr[n];
		selectid=arr[1].toUpperCase(); 
		tr_bgcolor(itemid);
		document.getElementById("formula").value=formula;
		document.getElementById("itemids").value=itemid;
		var affteritem_arr=document.getElementById("affteritem_arr").value;
		document.getElementById("div"+n+"__2").innerHTML=affteritem_arr;
		document.getElementById("selectid").value=selectid;
		document.getElementById("selectid").focus();
		if(this.itemid2!=itemid){
			onLeave2(this.itemid2,selectid);	
			this.itemid2 =	itemid.toUpperCase();
		}
	}else if(arr.length==4){
		var n=parseInt(arr[0]);
		formula=this.formulaArr[n];
		selectid=arr[1].toUpperCase()+"_"+arr[2].toUpperCase(); 
		tr_bgcolor(itemid);
		document.getElementById("formula").value=formula;
		document.getElementById("itemids").value=itemid;
		var affteritem_arr=document.getElementById("affteritem_arr").value;
		document.getElementById("div"+n+"__2").innerHTML=affteritem_arr;
		document.getElementById("selectid").value=selectid;
		document.getElementById("selectid").focus();
		if(this.itemid2!=itemid){
			onLeave2(this.itemid2,selectid);	
			this.itemid2 =	itemid.toUpperCase();
		}
	}
	this.itemid2 =	itemid.toUpperCase();
}
function onLeave2(itemid2,selectid){
	if(itemid2!=null){
		var arr = itemid2.split("_");
		if(arr.length==3){
			var n=parseInt(arr[0]);
			if(idArr[n]!=null){
			var id=idArr[n].toUpperCase();
			var tabelstr="<input type=\"text\" name=\""+id+"\" value=\""+chzArr[n]+"\" onclick=\"";
			tabelstr+="onSelects('"+id+"');\" style=\"width:200px;text-align:right\">";
			document.getElementById("div"+n+"__2").innerHTML=tabelstr;
			document.getElementById("div"+n+"__2").parentNode.style.backgroundColor ='#FFFFFF';
//			var affteritem_arr=document.getElementById("affteritem_arr").value;//bug号：16631，计算公式-计算项目焦点失去
//			document.getElementById("div"+n+"__2").innerHTML=affteritem_arr;
//			document.getElementById("selectid").value=selectid;
			}
		}
	}	
}
function onLeave(){
	document.getElementById("selectid").focus();
//	document.getElementById("selectid").onblur=function(){
		var itemid=document.getElementById("itemids").value;
		var arr = itemid.split("_");
		if(arr.length==3){
			var n=parseInt(arr[0]);
			var id=idArr[n].toUpperCase();
			var tabelstr="<input type=\"text\" name=\""+id+"\" value=\""+chzArr[n]+"\" onclick=\"";
			tabelstr+="onSelects('"+id+"');\" style=\"width:200px;text-align:right\">";
			document.getElementById("div"+n+"__2").innerHTML=tabelstr;
		}
//	}
}
function  changeSelect(){
	var obj=document.getElementById("selectid");
	var itemid=document.getElementById("itemids").value;
	var arr = itemid.split("_");
	var id='';
	if(arr.length==3){
		var n=parseInt(arr[0]);
		var selectid=obj.value;
		if(selectid!="new"){
			id = n+"_"+selectid+"_2";
			this.idArr[n]=id;
			this.formulaArr[n]='';
			this.chzArr[n]=obj.options[obj.selectedIndex].text;
		}else{
			id=n+"__2";
			this.idArr[n]=id;
			this.formulaArr[n]='';
			this.chzArr[n]='';
		}
		document.getElementById("itemids").value=id;
	}
	document.getElementById("formula").value='';
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
function outTable(){
	var tabelstr = "<table width=\"100%\" border=\"0\" align=\"center\" class=\"ListTable\">";
	tabelstr += "<tr class=\"fixedHeaderTr1\">";
	tabelstr += "<td align=\"center\" class=\"TableRow\" nowrap>"+FORMULA_PROJECT+"</td>";
	tabelstr += "</tr>";
	var n=0;
	for(var i=0;i<this.idArr.length;i++){
		var itemid = this.idArr[i].toUpperCase();
		tabelstr+="<tr>";
		tabelstr+="<td class=\"RecordRow\"  height=\"30\" nowrap>";
		tabelstr+="<div id=\"div"+i+"__2\">";
		tabelstr+="<input type=\"text\" name=\""+itemid+"\" value=\""+this.chzArr[i]+"\" onclick=\"";
		tabelstr+="onSelects('"+itemid+"');\" style=\"width:200px;text-align:right\">";
		tabelstr+="</div>";
		tabelstr+="</td></tr>";
		n++;
	}
	tabelstr+="</table>";
	return tabelstr;
}
function addTable(){
	var len = this.idArr.length;
	var itemid = len+"__2";
	this.idArr[len]=itemid;
	this.formulaArr[len]='';
	this.chzArr[len]='';
	var tabelstr=document.getElementById("itemtable").innerHTML;
	tabelstr=tabelstr.replace("</table>","").replace("</TABLE>","");
	tabelstr+="<tr>";
	tabelstr+="<td class=\"RecordRow\" align='center' style='border-left:0px;border-right:0px;' height=\"30\" nowrap>";
	tabelstr+="<div id=\"div"+itemid+"\">";
	tabelstr+="<input type=\"text\" name=\""+itemid+"\" value=\"\" onclick=\"";
	tabelstr+="onSelects('"+itemid+"');\" style=\"width:200px;text-align:right\" class='TEXT4'>";
	tabelstr+="</div>";
	tabelstr+="</td></tr>";
	tabelstr+="</table>";
	tabelstr=tabelstr.replace("TOP","HEIGHT");
	document.getElementById("itemtable").innerHTML=tabelstr;
}
function delItemTable(){
	var itemid=document.getElementById("itemids").value;
	var arr = itemid.split("_");
	var info =PLEASE_DELL;
	for(var i=0;i<this.idArr.length;i++){
		var itemid2 = this.idArr[i].toUpperCase();
		if(itemid2==itemid.toUpperCase()){
		info+=this.chzArr[i];
		}
	}
    if(confirm(info+"?"))
	 {
		if(arr.length==3){
			this.idArr=arrRemove(this.idArr,arr[0]);
			var n=0;
			for(var i=0;i<this.idArr.length;i++){
				var id = this.idArr[i];
				var id_arr = id.split("_");
				if(id_arr.length==3){
					this.idArr[i]=n+"_"+id_arr[1]+"_"+id_arr[2];
					n++;
				}
			}
			this.formulaArr =arrRemove(this.formulaArr,arr[0]);
			this.chzArr=arrRemove(this.chzArr,arr[0]);
			document.getElementById("itemtable").innerHTML=outTable();
			document.getElementById("formula").value=''; 
			
			var pre_number=arr[0]-1;
			if(pre_number<0)
				pre_number=0; 
			if(this.idArr.length>pre_number)
			{ 
				onSelects(this.idArr[pre_number]);
			}
			
		}
	}
}
function arrRemove(arr,n){
	if(arr.length<1||n<0){
		return arr;
	}else{
		return arr.slice(0,n).concat(arr.slice(parseInt(n)+1,arr.length));
	}
}
function upSort(){
	var sortitemid = document.getElementById("itemids").value;
	if(sortitemid==null||sortitemid<1){
		return;
	}
	var arr = sortitemid.split("_");
	if(this.idArr.length>1&&arr.length==3){
		var n=parseInt(arr[0]);
		if(n>0){
			var id=this.idArr[n-1];
			var id_arr = id.split("_");
			if(id_arr.length==3){
				sortitemid=(n-1)+"_"+arr[1]+"_2";
				this.idArr[n]=n+"_"+id_arr[1]+"_2";
				this.idArr[n-1]=sortitemid;
				var formula = this.formulaArr[n];
				this.formulaArr[n]=this.formulaArr[n-1];
				this.formulaArr[n-1]=formula;
				var chz = this.chzArr[n];
				this.chzArr[n]=this.chzArr[n-1];
				this.chzArr[n-1]=chz;
			}
		}
		document.getElementById("itemtable").innerHTML=outTable();
		onSelects(sortitemid);
	}else if(this.idArr.length>1&&arr.length==4){
		var n=parseInt(arr[0]);
		if(n>0){
			var id=this.idArr[n-1];
			var id_arr = id.split("_");
			if(id_arr.length==3){
				sortitemid=(n-1)+"_"+arr[1]+"_"+arr[2]+"_2";
				this.idArr[n]=n+"_"+id_arr[1]+"_2";
				this.idArr[n-1]=sortitemid;
				var formula = this.formulaArr[n];
				this.formulaArr[n]=this.formulaArr[n-1];
				this.formulaArr[n-1]=formula;
				var chz = this.chzArr[n];
				this.chzArr[n]=this.chzArr[n-1];
				this.chzArr[n-1]=chz;
			}
		}
		document.getElementById("itemtable").innerHTML=outTable();
		onSelects(sortitemid);
	}
	
}
function downSort(){
	var sortitemid = document.getElementById("itemids").value;
	if(sortitemid==null||sortitemid<1){
		return;
	}
	var arr = sortitemid.split("_");
	if(this.idArr.length>1&&arr.length==3){
		var n=parseInt(arr[0]);
		if(n<this.idArr.length-1){
			var id=this.idArr[n+1];
			var id_arr = id.split("_");
			if(id_arr.length==3){
				sortitemid=(n+1)+"_"+arr[1]+"_"+arr[2];
				this.idArr[n]=n+"_"+id_arr[1]+"_"+id_arr[2];
				this.idArr[n+1]=sortitemid;
				var formula = this.formulaArr[n];
				this.formulaArr[n]=this.formulaArr[n+1];
				this.formulaArr[n+1]=formula;
				var chz = this.chzArr[n];
				this.chzArr[n]=this.chzArr[n+1];
				this.chzArr[n+1]=chz;
			}
		}
		document.getElementById("itemtable").innerHTML=outTable();
		onSelects(sortitemid);
	}else if(this.idArr.length>1&&arr.length==4){
		var n=parseInt(arr[0]);
		if(n<this.idArr.length-1){
			var id=this.idArr[n+1];
			var id_arr = id.split("_");
			if(id_arr.length==3){
				sortitemid=(n+1)+"_"+arr[1]+"_"+arr[2]+"_"+arr[3];
				this.idArr[n]=n+"_"+id_arr[1]+"_"+id_arr[2];
				this.idArr[n+1]=sortitemid;
				var formula = this.formulaArr[n];
				this.formulaArr[n]=this.formulaArr[n+1];
				this.formulaArr[n+1]=formula;
				var chz = this.chzArr[n];
				this.chzArr[n]=this.chzArr[n+1];
				this.chzArr[n+1]=chz;
			}
		}
		document.getElementById("itemtable").innerHTML=outTable();
		onSelects(sortitemid);
	}
	
}
function changeCodeValue(formula){
  	var item=document.getElementById("itemid").value;
  	var itemid = item.split(":");
	var in_paramters="itemid="+itemid[0];
    var request=new Request({method:'post',asynchronous:false,parameters:in_paramters,onSuccess:showCodeFieldList,functionId:'3020050012'});
	symbol(formula,itemid[1]);
}
function showCodeFieldList(outparamters){
	var codelist=outparamters.getValue("codelist");
	if(codelist.length>1){
		toggles("codeview");
		AjaxBind.bind(setFormulaForm.codesetid_arr,codelist);
	}else{
		hides("codeview");
	}	
}
function function_Wizard(tableid,formula){
    var thecodeurl ="/org/autostatic/mainp/function_Wizard.do?b_search=link&salaryid=&tableid="+tableid+ "&mode=rsyd_jsgs"; 
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
    var conditions = document.getElementById("cfactor").value;
    var hashvo=new ParameterSet();
    hashvo.setValue("type","setConditionsInfo");
    hashvo.setValue("conditions",conditions);
    var request=new Request({method:'post',asynchronous:false,onSuccess:condiTions_ok,functionId:'3020110056'},hashvo);       
}

function condiTions_ok(){
	var tableid = document.getElementById("tableid").value;
	//var conditions = document.getElementById("cfactor").value;
    var thecodeurl ="/general/salarychange/calculating_conditions.do?b_query=link&tableid="+tableid;//+"&conditions="+conditions; 
    var dialogWidth="520px";
    var dialogHeight="440px";
    if (isIE6()){
   		dialogWidth="570px";
   		dialogHeight="470px";
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
function alertFormula(){
	var itemids = document.getElementById("itemids").value;
	var formula=document.getElementById("formula").value;
	if(itemids!=null&&itemids.length>1){
		var arr = itemids.split("_");
		if(arr.length==3){
			var n=parseInt(arr[0]);
			this.formulaArr[n]=formula;
		}
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
  		else if(arr.length==4&&arr[1].length>1){//start_date 
			var item = arr[1]+"_"+arr[2]+"_2="+formula+"`";
  			var hashvo=new ParameterSet();
	    	hashvo.setValue("item",getEncodeStr(item));
	    	hashvo.setValue("check",check);
	   	 	hashvo.setValue("tableid",tableid);
			var request=new Request({method:'post',asynchronous:false,onSuccess:resultCheckExpr,functionId:'3020110060'},hashvo);		
  		}
  	}else{
  		alert(PROJECT_NOT_NULL);
  		return ;
  	}
}
function checkCurFormulaOK(check){
    alertFormula();
	var item="";
	for(var i=0;i<idArr.length;i++){
		if(idArr[i]==null)
			continue;
		var arr = idArr[i].split("_");
		if(arr.length==3&&arr[1]!=null&&arr[1].length>0)
			item+=arr[1]+"_"+arr[2]+"="+formulaArr[i]+"`"
		else if	(arr.length==4&&arr[1]!=null&&arr[1].length>0&&arr[2]!=null&&arr[2].length>0) //start_date
			item+=arr[1]+"_"+arr[2]+"_"+arr[3]+"="+formulaArr[i]+"`"
		
	}
	var tableid=document.getElementById("tableid").value;
	if(item!=null&&item.length>4){
		if(confirm("请确认是否需要对所有计算项进行公式检查?"))
		{
			var hashvo=new ParameterSet();
		    hashvo.setValue("item",getEncodeStr(item));
		    hashvo.setValue("check",check);
		   	hashvo.setValue("tableid",tableid);
			var request=new Request({method:'post',asynchronous:false,onSuccess:resultCheckExpr,functionId:'3020110060'},hashvo);	
		}
		else
		{
			if(check=='save'){
				setName();
			}else if(check=='alert'){
				alertName();
			}
		}
		
  	}else{
  		alert(PROJECT_NOT_NULL);
  		return ;
  	}
}
function checkFormula(check){
	if(this.idArr!=null&&this.idArr.length>0){
		if(check=='save'){
			setName();
		}else if(check=='alert'){
			alertName();
		}
	}
}
function itemStr(){
	var item='';
	if(this.idArr!=null&&this.idArr.length>0){
		for(var i=0;i<this.idArr.length;i++){
			var itemid = this.idArr[i];
			var arr = itemid.split("_");
			if(arr.length==3){
				if(arr[1].length>1){
					item+=arr[1]+"_2=";
					item+=this.formulaArr[i]+"`";
				}
			}else if(arr.length==4){
				if(arr[1].length>1&&arr[2].length>1){
					item+=arr[1]+"_"+arr[2]+"_2=";
					item+=this.formulaArr[i]+"`";
				}
			}
		}
  	}
  	return item;
}
function resultCheckExpr(outparamters){
  	var info = outparamters.getValue("info");
  	var check = outparamters.getValue("check");
	if(info!='ok'){
		alert(getDecodeStr(info));
	}else{
		if(check=='save'){
			setName();
		}else if(check=='alert'){
			alertName();
		}else if(check=='check'){
			alertFormula();
			alert(FORMULA_OK+"!");
		}
	}
}
function alertName(){
    var cfactor=document.getElementById("cfactor").value;
	var tableid=document.getElementById("tableid").value;
	var id=document.getElementById("id").value;
	var hashvo=new ParameterSet();
	hashvo.setValue("tableid",tableid);
	hashvo.setValue("id",id);
	hashvo.setValue("item",getEncodeStr(itemStr()));
	hashvo.setValue("cfactor",cfactor);
	hashvo.setValue("flag","alert");
	var request=new Request({method:'post',asynchronous:true,onSuccess:checkSaveName,functionId:'3020110057'},hashvo); 
}
function setName(){
    var thecodeurl ="/general/salarychange/setname.jsp"; 
    var return_vo= window.showModalDialog(thecodeurl, "", 
             "dialogWidth:300px; dialogHeight:150px;resizable:no;center:yes;scroll:yes;status:no;");
    if(return_vo!=null){
    	var item=itemStr();
    	if(item!=null&&item.length>1){
			var cfactor=document.getElementById("cfactor").value;
			var tableid=document.getElementById("tableid").value;
			var hashvo=new ParameterSet();
			hashvo.setValue("tableid",tableid);
			hashvo.setValue("item",getEncodeStr(item));
			hashvo.setValue("cfactor",cfactor);
			hashvo.setValue("name",return_vo);
			hashvo.setValue("flag","save");
			var request=new Request({method:'post',asynchronous:true,onSuccess:checkSaveName,functionId:'3020110057'},hashvo); 
		}
	}   
}
function checkSaveName(outparamters){
  	var info = outparamters.getValue("info");
	if(info=='ok'){
		var id = outparamters.getValue("id");
		window.returnValue=id;
		window.close();
	}else if(info=='alert'){
		window.close();
	}
}

