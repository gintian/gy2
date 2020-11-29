var idArr=new Array();
var descArr=new Array();
var logicArr=new Array();
var eqArr=new Array();
var typeArr=new Array();
var codeArr=new Array();
function IsDigit(){ 
    return ((event.keyCode >= 47) && (event.keyCode <= 57)); 
} 
function checkTime(times){
 	var result=times.match(/^(\d{1,4})(-|\/)(\d{1,2})\2(\d{1,2})$/);
 	if(result==null) return false;
 	var d= new Date(result[1], result[3]-1, result[4]);
 	return (d.getFullYear()==result[1]&&(d.getMonth()+1)==result[3]&&d.getDate());
}
function timeCheck(obj){
	if(!checkTime(obj.value)){
		obj.value='';
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
function arrRemove(arr,n){
	if(arr.length<1||n<0){
		return arr;
	}else{
		return arr.slice(0,n).concat(arr.slice(parseInt(n)+1,arr.length));
	}
}
function tr_bgcolor(nid){
	var tablevos=document.getElementsByTagName("input");
	for(var i=0;i<tablevos.length;i++){
	    var cvalue = tablevos[i];
	    var td = cvalue.parentNode;
	    td.style.backgroundColor = '';
    }
	var c = document.getElementsByName(nid);
	var tr = c[0].parentNode;
	if(tr.style.backgroundColor!=''){
		tr.style.backgroundColor = '' ;
	}else{
		tr.style.backgroundColor = '#FFF8D2' ;
	}
}
function onSelects(itemid){
	document.getElementById("itemid").value=itemid;
	tr_bgcolor(itemid+".value");
}

var condstr_all = "";
var factor_all = "";
var tabid_all = "";
var seiveid_all = "";
var flag_all = "";

function searchSetCond(flag,tabid,seiveid,name){
	var condstr='';
	var factor = document.getElementById("cond").value;
	if(idArr.length<1)
		return;
	var namevalue='';
	for(var i=0;i<this.idArr.length;i++){
		var itemid = this.idArr[i];
		var itemvalue = document.getElementsByName(i+"_"+itemid+".value")[0].value;
		condstr+=itemid+replaceKeyWord(this.eqArr[i])+itemvalue+"`";	
		if(namevalue!=null&&namevalue.length>1)
			continue;
		else{
			var codeid = codeArr[i];
			if(codeid!='0'){
				namevalue=document.getElementsByName(i+"_"+itemid+".hzvalue")[0].value;
			}else{
				namevalue=itemvalue;
			}
		}
	}
	condstr_all = condstr;
	factor_all = factor;
	tabid_all = tabid;
	seiveid_all = seiveid;
	flag_all = flag;
	if(flag=='add'){
		name = getEncodeStr(this.descArr[0]+this.eqArr[0]+namevalue);	
   		var thecodeurl ="/gz/gz_analyse/setfiltername.jsp?setname="+name;
   		var iframe_url="/general/query/common/iframe_query.jsp?src="+$URL.encode(thecodeurl); 
		var config = {
		    width:400,
		    height:200,
		    type:'2',
		    id:'searchSetCond_win'
		}
		modalDialog.showModalDialogs(iframe_url,"searchSetCond_win",config,save_);
   		/*var return_vo= window.showModalDialog(thecodeurl, "", 
             "dialogWidth:300px; dialogHeight:150px;resizable:no;center:yes;scroll:yes;status:no");*/
		
	}else {
		save_(name);
	}
}

function save_(name) {
	if(name) {
		var hashvo=new ParameterSet();
		hashvo.setValue("expr",condstr_all);
		hashvo.setValue("factor",factor_all );
		hashvo.setValue("tabid",tabid_all);
		hashvo.setValue("seiveid",seiveid_all);
		hashvo.setValue("flag",flag_all);
		hashvo.setValue("name",replaceKeyWord(name));
		var request=new Request({method:'post',asynchronous:false,onSuccess:searchSave,functionId:'0521010016'},hashvo);
	}
}
function searchSave(outparamters){
	var tabid=outparamters.getValue("tabid");
	if(tabid!=null&&tabid.length>0){
		if(window.showModalDialog){
			window.returnValue=tabid;
		}else {
	 		parent.window.opener.setSelectCond('add');
		}
		parent.window.close();
	}
}
function additemtr(sourcebox_id){
  var left_vo,vos,i;
  vos= document.getElementsByName(sourcebox_id);
  if(vos==null)
  	return false;
  left_vo=vos[0];
  var tabelstr=document.getElementById("strTable").innerHTML;
  	tabelstr=tabelstr.replace("</table>","").replace("</TABLE>","");
  for(i=0;i<left_vo.options.length;i++){
  		if(left_vo.options[i].selected){
  			
        	var itemid=left_vo.options[i].value;
    		var desc=left_vo.options[i].text;
    		if(itemid!=null&&itemid.length>0){
    			var arr = itemid.split(":");
    			if(arr.length==3){
    				var n=idArr.length;
    				this.idArr[n]=arr[0];
    				if(n==0){
    					this.logicArr[0]='';
    				}else{
    					if(this.logicArr[n]!=null&&this.logicArr[n].length>0){
    						this.logicArr[n]+="*";
    					}else{
    						this.logicArr[n]="*";
    					}
    				}
    				this.eqArr[n]="=";
    				this.descArr[n]=desc;
    				this.typeArr[n]=arr[1].toUpperCase();
    				this.codeArr[n]=arr[2];
    				tabelstr+=addTable(arr[0],desc,arr[1].toUpperCase(),arr[2],"=",'',''); 
    			}
    		}
    	}
   }
   tabelstr+="</table>";
   document.getElementById("strTable").innerHTML=tabelstr;
   addCond();
}
function addCond(){
   var cond="";
   for(var j=1;j<=this.idArr.length;j++){
   		var lo = this.logicArr[j-1];
   		if(lo!=null&&lo.length>0){
   			cond+=lo+j;
   		}else{
   			cond+=j;
   		}
   } 
   if(this.idArr.length<this.logicArr.length){
   		var lo = this.logicArr[this.idArr.length];
   		if(lo!=null&&lo.length>0){
   			var logic = replaceAll(lo,"(","");
			logic=replaceAll(logic,"!","");
			logic=replaceAll(logic,"+","");
			logic=replaceAll(logic,"*","");
   			cond+=logic;
   		}
   }
   document.getElementById("cond").value=cond;
}
function addTable(itemid,desc,type,codesetid,eq,values){
	var n=idArr.length-1;
	var id=n+"_"+itemid;
	var tabelstr="<tr onclick=\"onSelects('"+id+"');\">";
	tabelstr+="<td class=\"RecordRow\" align=\"center\" height=\"30\" style=\"border-left: 0px;\">";
	tabelstr+=(n+1);
	tabelstr+="</td><td class=\"RecordRow\"  height=\"30\">";
	tabelstr+=desc;
	tabelstr+="</td><td class=\"RecordRow\" align=\"center\" height=\"30\">";
	tabelstr+="<select name=\""+id+"_eq\" onchange=\"eqChange("+n+",this);\">";
	if(eq=='=')
		tabelstr+="<option value=\"=\" selected>&nbsp;=</option>";
	else
		tabelstr+="<option value=\"=\">&nbsp;=</option>";
	if(eq=='>')
		tabelstr+="<option value=\"&gt;\" selected>&gt;</option>";
	else
		tabelstr+="<option value=\"&gt;\">&gt;</option>";
	if(eq=='>=')
		tabelstr+="<option value=\"&gt;=\" selected>&gt;=</option>";
	else
		tabelstr+="<option value=\"&gt;=\">&gt;=</option>";
	if(eq=='<')
		tabelstr+="<option value=\"&lt;\" selected>&lt;</option>";
	else 
		tabelstr+="<option value=\"&lt;\">&lt;</option>";
	if(eq=='<=')
		tabelstr+="<option value=\"&lt;=\" selected>&lt;=</option>";
	else
		tabelstr+="<option value=\"&lt;=\">&lt;=</option>";
	if(eq=='<>')
		tabelstr+="<option value=\"&lt;&gt;\" selected>&lt;&gt;</option></select>";
	else
		tabelstr+="<option value=\"&lt;&gt;\">&lt;&gt;</option></select>";
	tabelstr+="</td><td class=\"RecordRow\">";
	if(type=='N'){
		tabelstr+="<input type=\"text\" name=\""+id+".value\" value=\""+values+"\"";
		tabelstr+=" onkeypress=\"event.returnValue=IsDigit();\" style=\"width:120px\">";
	}else if(type=='D'){
		tabelstr+="<input type=\"text\" name=\""+id+".value\" value=\""+values+"\"";
		tabelstr+=" ondblclick=\"showDateSelectBox(this);\" ";
		tabelstr+=" style=\"width:120px;\"";
		tabelstr+=">";
	}else if(type=='A'){
		if(codesetid=="0"){
			tabelstr+="<input type=\"text\" name=\""+id+".value\" value=\""+values+"\" style=\"width:120px\">";
		}else{
			var valuearr = values.split(",");
			if(valuearr.length!=2){
				valuearr[0]='';
				valuearr[1]='';
			}
			if(valuearr[1].length==0){
				valuearr[1] = valuearr[0];
			}
			tabelstr+="<input type=\"text\" name=\""+id+".hzvalue\" value=\""+valuearr[1]+"\" onChange=\"changeHidden(this,'"+id+"')\" style=\"width:120px\">";
			tabelstr+="<input type=\"hidden\" name=\""+id+".value\" value=\""+valuearr[0]+"\">";
			tabelstr+="<img  src=\"/images/code.gif\" style=\"position:absolute;padding:2px 0 0 2px;CURSOR:point;\" onclick=\"javascript:openCondCodeDialog('";
			tabelstr+=codesetid+"','"+id+".hzvalue');\">";
		}
	}
	tabelstr+="</td></tr>";
	return tabelstr;
}
function changeHidden(obj,id){
	var hidden = document.getElementsByName(id+".value")[0];
	hidden.value = obj.value;
}
function editTable(tablearr){
  if(tablearr==null||tablearr.length<1){
  	return;
  }
  var arr = tablearr.split("`");
  var tabelstr=document.getElementById("strTable").innerHTML;
  tabelstr=tabelstr.replace("</table>","").replace("</TABLE>","");
  var n=0;
  for(var i=0;i<arr.length;i++){
  	if(arr[i]!=null&&arr[i].length>1){
  		var itemarr = arr[i].split(":");
  		if(itemarr.length==6){
			this.idArr[n]=itemarr[0];
    		this.eqArr[n]=itemarr[4];
    		this.descArr[n]=itemarr[1];
    		this.typeArr[n]=itemarr[3];
    		this.codeArr[n]=itemarr[2];
    		tabelstr+=addTable(itemarr[0],itemarr[1],itemarr[3].toUpperCase(),itemarr[2],itemarr[4],itemarr[5]); 
    		n++;
    	}
	  }
   }
   tabelstr+="</table>";
   document.getElementById("strTable").innerHTML=tabelstr;
   setLogicArr();
}
function delTableStr(){
	var itemid = document.getElementById("itemid").value;
	if(itemid==null||itemid.length<1){
		return;
	}
	var arr = itemid.split("_");
	if(arr.length!=2){
		return;
	}
	var n=parseInt(arr[0]);
	this.idArr=arrRemove(this.idArr,n);
	this.descArr=arrRemove(this.descArr,n);
	if(n==0){
		this.logicArr=arrRemove(this.logicArr,0);
		this.logicArr[0]=replaceAll(this.logicArr[0],"*","");
		this.logicArr[0]=replaceAll(this.logicArr[0],"+","");
		this.logicArr[0]=replaceAll(this.logicArr[0],")","");
	}else{
		var logic = replaceAll(this.logicArr[n],"!*","");
		logic=replaceAll(logic,"!+","");
		logic=replaceAll(logic,"+","");
		logic=replaceAll(logic,"*","");
		var right=replaceAll(logic,"(","");
		var left=replaceAll(logic,")","");
		this.logicArr=arrRemove(this.logicArr,n);
		if(right!=null&&right.length>0)
			this.logicArr[n]=right+this.logicArr[n];
		if(left!=null&&left.length>0)
			this.logicArr[n]=this.logicArr[n]+left;
	}
	this.eqArr=arrRemove(this.eqArr,n);
	this.typeArr=arrRemove(this.typeArr,n);
	this.codeArr=arrRemove(this.codeArr,n);
	
	var obj = document.getElementById("tablestr");
	obj.deleteRow(n+1);
	
	var tabelstr=document.getElementById("strTable").innerHTML;
	/*
	var m = tabelstr.indexOf("<TR style=\"BACKGROUND-COLOR: #add6a6\" onclick=\"onSelects('"+itemid+"');\">");
	var str = tabelstr.substring(m);
	tabelstr=tabelstr.substring(0,m);
	tabelstr+=str.substring(str.indexOf("</TR>")+5);
	*/
	for(var i=n;i<this.idArr.length;i++){
		var id=i+"_"+this.idArr[i];
		var id_old=(i+1)+"_"+this.idArr[i];
		tabelstr=replaceAll(tabelstr,id_old,id);
		var str_new  = "height=30>"+(i+1)+"</TD>";
		var str_old  = "height=30>"+(i+2)+"</TD>";
		tabelstr=tabelstr.replace(str_old,str_new);
	}
	tabelstr=replaceAll(tabelstr,"<TBODY>","");
	tabelstr=replaceAll(tabelstr,"</TBODY>","");
	document.getElementById("strTable").innerHTML=tabelstr;
	addCond();
	
	if(n<idArr.length){
		var selid = n+"_"+idArr[n];
		onSelects(selid);
		document.getElementById("itemid").value=selid;
	}else{
		var selid = (n-1)+"_"+idArr[n-1];
		if(n!=0){
			onSelects(selid);
			document.getElementById("itemid").value=selid;
		}else{
			document.getElementById("itemid").value="";
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
function logicChange(n,obj){
    this.logicArr[n]=obj.value;
}
function eqChange(n,obj){
    this.eqArr[n]=obj.value;
}
function checkCond(flag,a_code,tablename,type){
	var expression = document.getElementById("cond").value
	if(expression==null||expression.length<1){
		return;
	}
    var hashvo=new ParameterSet();
	hashvo.setValue("expression",expression);
	hashvo.setValue("factorlist",this.idArr);
	hashvo.setValue("a_code",a_code);
	hashvo.setValue("tablename",tablename);
	hashvo.setValue("type",type);
	hashvo.setValue("flag",flag);
	var request=new Request({method:'post',asynchronous:false,onSuccess:searchAndSave,functionId:'3020110074'},hashvo);	
}
function searchAndSave(outparamters){
	var check = outparamters.getValue("check");
	if(check=='ok'){
		var flag = outparamters.getValue("flag");
		var type = outparamters.getValue("type");
		if(flag==1){
			var a_code = outparamters.getValue("a_code");
			var tablename = outparamters.getValue("tablename");
			searchSetCond(a_code,tablename,type);
		}else if(flag==2){
			var condlogic='';
			for(var i=0;i<this.logicArr.length;i++){
				var itemid = this.idArr[i];
				var itemvalue = document.getElementById(i+"_"+itemid+".value").value;
				condlogic+=itemid+this.eqArr[i]+itemvalue+"`";	
			}
			var obj = document.getElementById("like");
			var like="0";
			if(obj.checked)
				like="1";
			obj = document.getElementById("history");
			var history="0";
			if(obj.checked)
				history="1";
			var expression = outparamters.getValue("expression");
			var theArr=new Array(expression,condlogic,type,like,history); 
			var thecodeurl ="/general/inform/search/common.do?b_query=link&type="+type+"&flag=insert"; 
			return_vo= window.showModalDialog(thecodeurl,theArr,
              "dialogWidth:400px; dialogHeight:320px;resizable:no;center:yes;scroll:yes;status:no");
		}
	}else{
		alert(check);
	}
}

function setLogicArr(){
	var expression = document.getElementById("cond").value;
	var logic='';
	var n=0;
	for(var i=0;i<expression.length;i++){
    	var cond = expression.charAt(i);
    	if((cond>=0)&&(cond<=9)){
    		if(i<expression.length-1){
    			var ex = expression.charAt(i+1);
    			if(ex>=0&&ex<=9){
    				i++;
    			}
    		}
    		n++;
    		logic='';
    	}else{
    		logic+=cond;
    		this.logicArr[n]=logic;
    	}
	}
}
var date_desc;
function showDateSelectBox(srcobj){
	date_desc=srcobj;
	Element.show('date_panel');   
	var pos=getAbsPosition(date_desc);
	with($('date_panel')){
		style.position="absolute";
		style.posLeft=pos[0]-1;
		style.posTop=pos[1]-1+srcobj.offsetHeight;
		style.width=(srcobj.offsetWidth<150)?150:srcobj.offsetWidth+1;
	}                 
}
function setSelectValue(){
	if(date_desc){
		date_desc.value=$F('date_box');
       	Element.hide('date_panel'); 
	}
}
