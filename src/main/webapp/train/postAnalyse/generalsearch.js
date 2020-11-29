var idArr=new Array();
var descArr=new Array();
var logicArr=new Array();
var eqArr=new Array();
var typeArr=new Array();
var codeArr=new Array();
var fieldSetArr=new Array();
function change(){
 	var fieldid=document.getElementById("fieldid").value;
	var in_paramters="tablename="+fieldid;
    var request=new Request({method:'post',asynchronous:false,parameters:in_paramters,onSuccess:showFieldList,functionId:'2020051007'});
}
function showFieldList(outparamters){
	var fieldlist=outparamters.getValue("fieldlist");
	AjaxBind.bind(trainStationForm.item_field,fieldlist);
}
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
function searchSetCond(a_code,tablename,type,fieldSetId){
	var condstr='',condlogic='';
	var obj = document.getElementById("like");
	var like="0";
	if(obj)
	{
	  if(obj.checked)
		 like="1";
	}
	for(var i=0;i<this.logicArr.length;i++){
		var itemid = this.idArr[i];
		if(condstr.length<1){
			condstr+=(i+1);
		}else
			condstr+=this.logicArr[i]+(i+1);
		var itemvalue = document.getElementById(i+"_"+itemid+".value").value;
		if(this.typeArr[i].toUpperCase()=='A')
		{
		   if(codeArr[i]!='0')
		   {
	    	 var itemhzvalue=document.getElementById(i+"_"+itemid+".hzvalue").value;
		     if(trim(itemhzvalue).length>0&&(itemhzvalue.indexOf("?")!=-1||itemhzvalue.indexOf("*")!=-1))
		     {
		        condlogic+=itemid+this.eqArr[i];	
		        condlogic+=itemhzvalue
		        if(like=='1')
		          condlogic+="*";
		        condlogic+="`";
		    }else{
	    	   condlogic+=itemid+this.eqArr[i]+itemvalue;
	    	    if(like=='1')
		          condlogic+="*";
		        condlogic+="`";	
	        }
	      }else
	      {
	          condlogic+=itemid+this.eqArr[i];//字符型，前后都模糊
	          if(like=='1')
		          condlogic+="*";	
		        condlogic+=itemvalue
		        if(like=='1')
		          condlogic+="*";
		        condlogic+="`";
	      }
	   } else{
	        condlogic+=itemid+this.eqArr[i]+itemvalue+"`";	
	    }
	}
	if(condlogic==null||condlogic.length<1){
		alert("请定义查询条件!");
		return false;
	}
	
	var fieldid=document.getElementById("fieldid").value;
	obj = document.getElementById("history");
	var history="0";
	if(obj)
	{
  	   if(obj.checked)
		  history="1";
    }
	var unite = '2';
	if(type=='2'){
		var tempobj=document.getElementsByName("unite");
		if(tempobj)
		{
	    	for (i=0;i<tempobj.length;i++){  
		     	if(tempobj[i].checked){
		 	    	unite=tempobj[i].value;
		    	}
		    }
	 	}
	}
    var hashvo=new ParameterSet();
	hashvo.setValue("sexpr",getEncodeStr(condstr));
	hashvo.setValue("sfactor",getEncodeStr(condlogic));
	hashvo.setValue("like",like);
	hashvo.setValue("history",history);
	hashvo.setValue("a_code",a_code);
	hashvo.setValue("tablename",tablename);
	hashvo.setValue("query_type","1");
	hashvo.setValue("type",type);
	hashvo.setValue("unite",unite);
	hashvo.setValue("fieldid",fieldid);
	hashvo.setValue("fieldSetId",fieldSetId);
	var request=new Request({method:'post',asynchronous:false,onSuccess:searchSave,functionId:'3020110075'},hashvo);
}
function searchSave(outparamters){//xuj update 2011-5-11 兼容firefox、chrome
	var check=outparamters.getValue("check");
	if(check=='ok'){
		var wheresql=outparamters.getValue("wheresql");
		wheresql=wheresql!=null&&wheresql.length>1?wheresql:"ok";
		var sexpr=outparamters.getValue("sexpr");
		var sfactor=outparamters.getValue("sfactor");
		var fieldSetId=outparamters.getValue("fieldSetId");
		if(fieldSetId=='0'){
			if(navigator.appName.indexOf("Microsoft")!= -1)
		     	window.returnValue=wheresql;
		    else
	         	top.returnValue=wheresql;
		}else
	    {
	         var obj = new Object();
	         obj.expr=sexpr;
	         obj.factor=sfactor;
	         obj.likeflag=outparamters.getValue("likeflag");
	         obj.history=outparamters.getValue("history");
	         if(navigator.appName.indexOf("Microsoft")!= -1)
	         	window.returnValue=obj;
	         else
	         	top.returnValue=obj;	
	    }
		top.close();
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
   tabelstr=tabelstr.replace("TOP","HEIGHT");
   document.getElementById("strTable").innerHTML=tabelstr; 
   checkMaintItem();
}
function addTable(itemid,desc,type,codesetid){//xuj update 2011-5-11 兼容firefox、chrome
	var n=idArr.length-1;
	var id=n+"_"+itemid;
	var tabelstr="<tr onclick=\"onSelects('"+id+"');\">";
	tabelstr+="<td class=\"RecordRow\" align=\"center\" height=\"30\">";
	if(n>0){
		tabelstr+="<select id=\""+id+"_logic\" name=\""+id+"_logic\" onchange=\"logicChange(";
		tabelstr+=n+",this);\"><option value=\"*\">且</option>";
		tabelstr+="<option value=\"+\">或</option></select>";
	}else{
		tabelstr+="&nbsp;";
	}
	tabelstr+="</td><td class=\"RecordRow\"  height=\"30\">";
	tabelstr+=desc;
	tabelstr+="</td><td class=\"RecordRow\" align=\"center\" height=\"30\">";
	tabelstr+="<select id=\""+id+"_eq\" name=\""+id+"_eq\" onchange=\"eqChange(";
	tabelstr+="this);\"><option value=\"=\">&nbsp;=</option>";
	tabelstr+="<option value=\"&gt;\">&nbsp;&gt;</option>";
	tabelstr+="<option value=\"&gt;=\">&gt;=</option>";
	tabelstr+="<option value=\"&lt;\">&nbsp;&lt;</option>";
	tabelstr+="<option value=\"&lt;=\">&lt;=</option>";
	tabelstr+="<option value=\"&lt;&gt;\">&lt;&gt;</option></select>";
	tabelstr+="</td><td class=\"RecordRow\">";
	if(type=='N'){
		tabelstr+="<input type=\"text\" name=\""+id+".value\" id=\""+id+".value\"";
		tabelstr+=" onkeypress=\"event.returnValue=IsDigit();\" style=\"width:120px\" class=\"text4\">";
	}else if(type=='D'){
		tabelstr+="<input type=\"text\" name=\""+id+".value\" id=\""+id+".value\"";
		tabelstr+=" ondblclick=\"showDateSelectBox(this);\" ";
		//tabelstr+=" extra=\"editor\"";
		//tabelstr+=" onblur=\"timeCheck(this);\"";
		tabelstr+=" style=\"width:120px;\"";
		//tabelstr+=" dropDown=\"dropDownDate\"";
		tabelstr+=" class=\"text4\">";
	}else if(type=='A'){
		if(codesetid=="0"){
			tabelstr+="<input type=\"text\" name=\""+id+".value\" id=\""+id+".value\" style=\"width:120px\" class=\"text4\">";
		}else{
			tabelstr+="<input type=\"text\" onblur=\"hzvalueTovalue('"+id+"');\" name=\""+id+".hzvalue\" id=\""+id+".hzvalue\" readonly=\"readonly\" style=\"width:120px\" class=\"text4\">";
			tabelstr+="<input type=\"hidden\" name=\""+id+".value\" id=\""+id+".value\">";
			if("UM,UN,@K".indexOf(codesetid)==-1)
			{
				tabelstr+="&nbsp;<img  src=\"/images/code.gif\" align=\"absmiddle\" onclick=\"javascript:openCondCodeDialog('";
				tabelstr+=codesetid+"','"+id+".hzvalue');\">";
			}else
			{//按照单位或部门进行查询时组织机构树的显示请按权限过滤
				tabelstr+="&nbsp;<img  src=\"/images/code.gif\" align=\"absmiddle\" onclick=\"javascript:openInputCodeDialogOrgInputPos('";
				tabelstr+=codesetid+"','"+id+".hzvalue','"+manageCode+"',1);\">";
			}
		}
	}
	tabelstr+="</td></tr>";
	return tabelstr;
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
	if(arr.length!=2){
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
	}
	if(this.idArr.length>0&&n==0){
		str = tabelstr.substring(0,tabelstr.indexOf("<SELECT"));
		tabelstr=tabelstr.substring(tabelstr.indexOf("</SELECT>")+9,tabelstr.length);
		tabelstr=str+"&nbsp;"+tabelstr;
	}
	tabelstr=replaceAll(tabelstr,"<TBODY>","");
	tabelstr=replaceAll(tabelstr,"</TBODY>","");
	tabelstr=tabelstr.replace("TOP","HEIGHT");
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
    if(checkMain=="1"){
    	toggles("viewHistory");
    }else{
    	if(document.getElementById("history"))
    	{
    	   document.getElementById("history").checked=false;
    	   hides("viewHistory");
    	}
    } 
}
function logicChange(n,obj){
    this.logicArr[n]=obj.value;
}
function eqChange(obj){
	var name = obj.name;
	if(name==null||name.length<5)
		return;
	var arr = name.split("_");
	if(arr.length!=3)
		return;
	var n = parseInt(arr[0]);
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
		}else{
			style.left=(pos[0]+7)+"px";
			style.top=(pos[1]+srcobj.offsetHeight)+"px";
		}
		style.width=(srcobj.offsetWidth<150)?150:srcobj.offsetWidth+1;
	}                 
}
function setSelectValue(){
	if(date_desc){
		date_desc.value=$F('date_box');
       	Element.hide('date_panel'); 
	}
}
function alertLert(obj,id)
{
   var trElement = document.getElementById(id);
   if(obj.checked)
       trElement.style.display="block";
   else
        trElement.style.display="none";
}