var idArr=new Array();
var descArr=new Array();
var logicArr=new Array();
var eqArr=new Array();
var typeArr=new Array();
var codeArr=new Array();
var fieldSetArr=new Array();
var isPriv="0";
function change(){
 	var fieldid=document.getElementById("fieldid").value;
	var in_paramters="tablename="+fieldid;
    var request=new Request({method:'post',asynchronous:false,parameters:in_paramters,onSuccess:showFieldList,functionId:'3020110073'});
}
//获取链接中的参数
function GetRequest() { 
	   var url = location.search; //获取url中"?"符后的字串 
	   var theRequest = new Object(); 
	   if (url.indexOf("?") != -1) { 
	      var str = url.substr(1); 
	      strs = str.split("&"); 
	      for(var i = 0; i < strs.length; i ++) { 
	         theRequest[strs[i].split("=")[0]]=unescape(strs[i].split("=")[1]); 
	      } 
	   } 
	   return theRequest; 
	} 


function showFieldList(outparamters){
	var fieldlist=outparamters.getValue("fieldlist");
	AjaxBind.bind(searchInformForm.item_field,fieldlist);
}
function IsDigit(e){ 
    e=e?e:(window.event?window.event:null);
    var key = window.event?e.keyCode:e.which;
	key=parseInt(key);
	if(((key >= 48) && (key <= 57))||key==8||key==46){
		return true;
	}else{
		return false;
	}
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
/*显示*/
function toggles(targetId){
	if (document.getElementById(targetId)){
		target = document.getElementById(targetId);
		target.style.display = "block";
	}
}
/*隐藏*/
function hides(targetId){
	if (document.getElementById(targetId)){
		target = document.getElementById(targetId);
		target.style.display = "none";
	}
}
function onSelects(itemid){
	document.getElementById("itemid").value=itemid;
	tr_bgcolor(itemid+".value");
}
function searchSetCond(a_code,tablename,type){
	var condstr='',condlogic='';
	
	for(var i=0;i<this.logicArr.length;i++){
		var itemid = this.idArr[i];
		
		if(typeof(itemid)!='undefined'){
			condstr+=this.logicArr[i]+(i+1);
			var itemvalue = document.getElementById(i+"_"+itemid+".value").value;
			condlogic+=itemid+replaceKeyWord(this.eqArr[i])+itemvalue+"`";
		}
	}
	if(condlogic==null||condlogic.length<1){
		alert(DEFINITION_SEARCH_CONDITIONS+"!");
		return false;
	}
	 var hashvo=new ParameterSet();
	hashvo.setValue("sexpr",getEncodeStr(condstr));
	hashvo.setValue("sfactor",getEncodeStr(condlogic));
	hashvo.setValue("fieldSetId",tablename);
	hashvo.setValue("type","P");
	var request=new Request({method:'post',asynchronous:false,onSuccess:searchSave,functionId:'3020110075'},hashvo);
	function searchSave(outparamters){//xuj update 2011-5-11 兼容firefox、chrome
		var check=outparamters.getValue("check");
		if(check=='ok'){
		var obj = document.getElementById("like");
		var like="0";
		if(obj.checked)
			like="1";
			
			if(navigator.appName.indexOf("Microsoft")!= -1){
				if(parent.opener){
					parent.opener.returnFun(condstr+'::'+condlogic+"::"+like);
				}else{
					window.returnValue=condstr+'::'+condlogic+'::'+like;
				}
				window.close();
			}else{
				if(moduleFlag=="hmuster" || moduleFlag=="batchDelete"){
					parent.opener.returnValue(condstr+'::'+condlogic+"::"+like);
				}else{
					if(parent.opener.returnFun)
						parent.opener.returnFun(condstr+'::'+condlogic+"::"+like);
					else
						top.returnValue=condstr+'::'+condlogic+"::"+like;
				}
				top.close();
			}
		}
	}
}
function searchSave(outparamters){
	var check=outparamters.getValue("check");
	if(check=='ok'){
		window.returnValue='ok';
		window.close();
	}
}
function additemtr(sourcebox_id){
  var left_vo,vos,i;
  vos= document.getElementsByName(sourcebox_id);
  if(vos==null)
  	return false;
  left_vo=vos[0];
  updateInputValue();
  var tabelstr=document.getElementById("strTable").innerHTML;
  	tabelstr=tabelstr.replace("</table>","").replace("</TABLE>","");
  for(i=0;i<left_vo.options.length;i++){
  		if(left_vo.options[i].selected){
        	var itemid=left_vo.options[i].value;
    		var desc=left_vo.options[i].text;
    		if(itemid!=null&&itemid.length>0){
    			var arr = itemid.split(":");
    			if(arr.length==4){
    				var n=idArr.length;
    				this.idArr[n]=arr[0];
    				if(n==0){
    					this.logicArr[n]="";
    				}else{
    					this.logicArr[n]="*";
    				}
    				this.eqArr[n]="=";
    				this.descArr[n]=desc;
    				this.typeArr[n]=arr[1].toUpperCase();
    				this.codeArr[n]=arr[2];
    				this.fieldSetArr[n]=arr[3];
    				tabelstr+=addTable(arr[0],desc,arr[1].toUpperCase(),arr[2]); 
    			}
    		}
    	}
   }
   tabelstr+="</table>";
   document.getElementById("strTable").innerHTML=tabelstr; 
   checkMaintItem();
}

function updateInputValue(){
	var inputs=document.getElementsByTagName("input");
	//ie8 不支持getElementsByClassName()方法
//	var inputs=document.getElementsByClassName("text4");
	var i=0;
	for(;i<inputs.length;i++){
		if(inputs[i]&&inputs[i].type=='text'){
			inputs[i].setAttribute("value", inputs[i].value);
		}
	}
	var selects=document.getElementsByTagName("option");
	for(var j=0;j<selects.length;j++){
		if(selects[j].selected){
			selects[j].setAttribute("selected",selects[j].selected);
		}
	}
}

function addTable(itemid,desc,type,codesetid){
	var n=idArr.length-1;
	var id=n+"_"+itemid;
	var tabelstr="<tr onclick=\"onSelects('"+id+"');\">";

	var Request = new Object();
	Request = GetRequest();
	var msg = Request['msg'];
	tabelstr+="<td class=\"RecordRow noleft\" align=\"center\" height=\"30\" style=\"border-left: 0px;border-top:none;\">";
	if(n>0){
		tabelstr+="<select name=\""+id+"_logic\" id=\""+id+"_logic\" onchange=\"logicChange(";
		tabelstr+=n+",this);\"><option value=\"*\">"+GENERAL_AND+"</option>";
		tabelstr+="<option value=\"+\">"+GENERAL_OR+"</option></select>";
	}else{
		tabelstr+="&nbsp;";
	}
	tabelstr+="</td><td class=\"RecordRow\"  height=\"30\" style=\"border-left: 0px;border-top:none;\">";
	tabelstr+=desc;
	tabelstr+="</td><td class=\"RecordRow\" align=\"center\" height=\"30\" style=\"border-left: 0px;border-top:none;\">";
	tabelstr+="<select name=\""+id+"_eq\" id=\""+id+"_eq\" onchange=\"eqChange(";
	tabelstr+=+n+",this);\"><option value=\"=\">&nbsp;=</option>";
	tabelstr+="<option value=\"&gt;\">&nbsp;&gt;</option>";
	tabelstr+="<option value=\"&gt;=\">&gt;=</option>";
	tabelstr+="<option value=\"&lt;\">&nbsp;&lt;</option>";
	tabelstr+="<option value=\"&lt;=\">&lt;=</option>";
	tabelstr+="<option value=\"&lt;&gt;\">&lt;&gt;</option></select>";
	tabelstr+="</td><td align=\"left\" class=\"RecordRow noright\" style=\"border-left: 0px;border-top:none;\">";
	if(type=='N'){
		tabelstr+="<input type=\"text\" name=\""+id+".value\" id=\""+id+".value\"";
		tabelstr+=" onkeypress=\"return IsDigit(event);\" class=\"text4\" style=\"width:110px\">";
	}else if(type=='D'){
		tabelstr+="<input type=\"text\" name=\""+id+".value\" id=\""+id+".value\"";
		tabelstr+=" ondblclick=\"showDateSelectBox(this);\" class=\"text4\"";
		tabelstr+=" style=\"width:110px;\"";
		tabelstr+=">";
	}else if(type=='A'){
		if(moduleFlag=="hmuster"){//高级花名册链接传递参数，针对代码性指标调用codeselect.js组件 其他调用不动
			if(codesetid=="0"){
				tabelstr+="<input type=\"text\" name=\""+id+".value\" id=\""+id+".value\" class=\"text4\" style=\"width:110px\">";
			}else if((codesetid=='UN'||codesetid=='UM'||codesetid=='@K')&&isPriv=='1'){
			     tabelstr+="<input type=\"text\" onblur=\"hzvalueTovalue('"+id+"');\" class=\"text4\" name=\""+id+".hzvalue\"  id=\""+id+".hzvalue\" style=\"width:110px\">";
				 tabelstr+="<input type=\"hidden\" name=\""+id+".value\" id=\""+id+".value\">";
				 tabelstr+="&nbsp;<img src=\"../../../images/code.gif\" style=\"vertical-align:middle;\" id=\""+id+".hzvalue_0\" onlySelectCodeset=\"true\" plugin=\"codetree\" codesetid=\""+codesetid+"\" inputname=\""+id+".hzvalue\" valuename=\""+id+".value\"   multiple =\"false\" onclick=\"codeClick(['"+id+".hzvalue_0'])\"/>";
			}else{
				tabelstr+="<input type=\"text\" class=\"text4\" onblur=\"hzvalueTovalue('"+id+"');\" name=\""+id+".hzvalue\"  id=\""+id+".hzvalue\" style=\"width:110px\">";
				tabelstr+="<input type=\"hidden\" name=\""+id+".value\" id=\""+id+".value\">";
				tabelstr+="&nbsp;<img src=\"../../../images/code.gif\" style=\"vertical-align:middle;\" id=\""+id+".hzvalue_0\" plugin=\"codetree\" codesetid=\""+codesetid+"\" inputname=\""+id+".hzvalue\" valuename=\""+id+".value\"   multiple =\"false\" onclick=\"codeClick(['"+id+".hzvalue_0'])\"/>";
			}
		}else{
			if(codesetid=="0"){
				tabelstr+="<input type=\"text\" name=\""+id+".value\" id=\""+id+".value\" class=\"text4\" style=\"width:110px\">";
			}else if((codesetid=='UN'||codesetid=='UM'||codesetid=='@K')&&isPriv=='1'){
			     tabelstr+="<input type=\"text\" onblur=\"hzvalueTovalue('"+id+"');\" class=\"text4\" name=\""+id+".hzvalue\" style=\"width:110px\">";
				 tabelstr+="<input type=\"hidden\" name=\""+id+".value\" id=\""+id+".value\">";
				 if("1"==msg){//判断跳转后的界面中点击根节点是否为公共资源
					 tabelstr+="&nbsp;<img  src=\"/images/code.gif\" align=\"absmiddle\" onclick=\"javascript:openInputCodeDialogOrgInputPos00('";
				 }else{
					 tabelstr+="&nbsp;<img  src=\"/images/code.gif\" align=\"absmiddle\" onclick=\"javascript:openInputCodeDialogOrgInputPos('";
				 }
				 tabelstr+=codesetid+"','"+id+".hzvalue','"+manageCode+"',1);\">";
			}else{
				tabelstr+="<input type=\"text\" class=\"text4\" onblur=\"hzvalueTovalue('"+id+"');\" name=\""+id+".hzvalue\" style=\"width:110px\">";
				tabelstr+="<input type=\"hidden\" name=\""+id+".value\" id=\""+id+".value\">";
				if("54"==codesetid){
					tabelstr+="&nbsp;<img  src=\"/images/code.gif\" align=\"absmiddle\" onclick=\"javascript:openCondCodeDialogsx('";
					tabelstr+=codesetid+"','"+id+".hzvalue');\">";
				}else{
					tabelstr+="&nbsp;<img  src=\"/images/code.gif\" align=\"absmiddle\" onclick=\"javascript:openCondCodeDialog('";
					tabelstr+=codesetid+"','"+id+".hzvalue');\">";
				}
			}
		}
		
	}
	tabelstr+="</td></tr>";
	return tabelstr;
}

function codeClick(eleId){
	setEleConnect(eleId);
}

function hzvalueTovalue(id){
	var namevlue = id+".value";
	var namehzvlue = id+".hzvalue";
	var values = document.getElementById(namehzvlue).value;
	if(values.indexOf("*")!=-1){
		document.getElementById(namevlue).value=values;
	}
}
function delTableStr(){
	var itemid = document.getElementById("itemid").value;
	if(itemid==null||itemid.length<1){
		return;
	}
	var arr = itemid.split("_");
	if(arr.length<2){
		return;
	}
	var n=parseInt(arr[0]);
	this.idArr=arrRemove(this.idArr,n);
	this.descArr=arrRemove(this.descArr,n);
	this.logicArr=arrRemove(this.logicArr,n);
	this.eqArr=arrRemove(this.eqArr,n);
	this.typeArr=arrRemove(this.typeArr,n);
	this.codeArr=arrRemove(this.codeArr,n);
	this.fieldSetArr=arrRemove(this.fieldSetArr,n);
	
	var obj = document.getElementById("tablestr");
	obj.deleteRow(n+1);
	updateInputValue();
	var tabelstr=document.getElementById("strTable").innerHTML;
	for(var i=n;i<this.idArr.length;i++){
		var id=i+"_"+this.idArr[i];
		var id_old=(i+1)+"_"+this.idArr[i];
		tabelstr=replaceAll(tabelstr,id_old,id,i);
		
	}
	if(this.idArr.length>0&&n==0){
		this.logicArr[n]="";
	}
	if(this.idArr.length>0&&n==0){
		str = tabelstr.substring(0,tabelstr.indexOf("<select"));
		tabelstr=tabelstr.substring(tabelstr.indexOf("</select>")+9,tabelstr.length);
		tabelstr=str + "&nbsp;" + tabelstr;
	}
	tabelstr=replaceAll(tabelstr,"<TBODY>","","");
	tabelstr=replaceAll(tabelstr,"</TBODY>","","");
	document.getElementById("strTable").innerHTML=tabelstr;
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
	checkMaintItem();
}
function replaceAll(text,replacement,target,num){
	
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
    
    if(num!=null&& num!=""&&returnString!=null&&returnString.length > 0){
    	text = returnString;
    	returnString = "";
         
         if(num>0){
	    	object = "logicChange("+num+",this);";
	    	index=text.indexOf("logicChange("+(num+1)+",this);");
	    	
	    	if(index > 0) 
	    		returnString+=text.substring(0,index)+object;
	        text=text.substring(index+object.length);
         }
         
         object = "eqChange("+num+",this);";
     	 index=text.indexOf("eqChange("+(num+1)+",this);");
     	    	
     	if(index > 0) 
     	    returnString+=text.substring(0,index)+object;
     	
     	text=text.substring(index+object.length);
        
        if(text!=""){
    		returnString+=text;
    	}
    }
    return returnString;
}
function checkMaintItem(){
    var checkMain = "0";
    for(var i=0;i<this.fieldSetArr.length;i++){
    	if(this.fieldSetArr[i]!=null&&this.fieldSetArr[i]!=undefined&&this.fieldSetArr[i].length>0){
    		if(this.fieldSetArr[i].toLowerCase()=='a01')
    			continue;
    		else if(this.fieldSetArr[i].toLowerCase()=='b01')
    			continue;
    		else if(this.fieldSetArr[i].toLowerCase()=='k01')
    			continue;
    		else{
    			checkMain="1";
    			break;
    		}
    	}
    }
    if(checkMain=="1"||this.fieldSetArr.length==0){
    	toggles("viewHistory");
    }else{
    	document.getElementById("history").checked=false;
    	hides("viewHistory");
    } 
}
function logicChange(n,obj){
    this.logicArr[n]=obj.value;
}
function eqChange(n,obj){
    this.eqArr[n]=obj.value;
}
var date_desc;
function showDateSelectBox(srcobj){
	date_desc=srcobj;
	Element.show('date_panel');   
	var pos=getAbsPosition(date_desc);
	with($('date_panel')){
		style.position="absolute";
		if(navigator.appName.indexOf("Microsoft")!= -1){
			style.posLeft=pos[0]-1;
			style.posTop=pos[1]-1+srcobj.offsetHeight;
			style.width=(srcobj.offsetWidth<150)?150:srcobj.offsetWidth+1;
		}else{
			style.left=pos[0]+5;
			style.top=pos[1]+srcobj.offsetHeight;
			style.width=(srcobj.offsetWidth<150)?150:srcobj.offsetWidth+1;
		}     
	}
}
function setSelectValue(){
	if(date_desc){
		date_desc.value=$F('date_box');
       	Element.hide('date_panel'); 
	}
}