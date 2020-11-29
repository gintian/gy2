var idArr=new Array();
var descArr=new Array();
var logicArr=new Array();
var eqArr=new Array();
var typeArr=new Array();
var codeArr=new Array();
var fieldSetArr=new Array();
function change(){
 	var fieldid=document.getElementById("fieldSetId").value;
	var in_paramters="tablename="+fieldid;
    var request=new Request({method:'post',asynchronous:false,parameters:in_paramters,onSuccess:showFieldList,functionId:'3020110073'});
}
function showFieldList(outparamters){
	var fieldlist=outparamters.getValue("fieldlist");
	AjaxBind.bind(aboutForm.item_field,fieldlist);
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
function searchSetCond(){
	var condstr='',condlogic='';desc="";
	for(var i=0;i<this.logicArr.length;i++){
		var itemid = this.idArr[i];
		var itemdesc = this.descArr[i];
		if(condstr.length<1){
			condstr+=(i+1);
		}else
			condstr+=this.logicArr[i]+(i+1);
		var itemvalue = document.getElementById(i+"_"+itemid+".value").value;
		var itemdescobj = document.getElementsByName(i+"_"+itemid+".hzvalue")[0];
		var itemdescvalue = "";
		if (itemdescobj) {
			itemdescvalue = itemdescobj.value;
		} else {
			itemdescvalue = itemvalue;
		}
		condlogic+=itemid+this.eqArr[i]+itemvalue+"`";
		desc +=itemdesc+this.eqArr[i]+	itemdescvalue+",";
	}
	if(condlogic==null||condlogic.length<1){
		alert("请定义查询条件!");
		return false;
	}
	
	// 模糊查询
	var obj = document.getElementsByName("like")[0];
	var like="0";
	if(obj)
	{
	  if(obj.checked)
		 like="1";
	}

	// 人员库
	var preObj = document.getElementsByName("pre")[0];
	if (preObj) {
		//hashvo.setValue("tablename",preObj.value);
	}
	
    //var hashvo=new ParameterSet();
	//hashvo.setValue("sexpr",condstr);
	//hashvo.setValue("sfactor",getEncodeStr(condlogic));
	//hashvo.setValue("like",like);
	//var request=new Request({method:'post',asynchronous:false,onSuccess:searchSave,functionId:'3020110075'},hashvo);
	var obj2 = new Object();
	    obj2.expr=condstr;
	    obj2.factor=condlogic;
	    obj2.like=like;
	    obj2.desc=desc;
	    obj2.pre=preObj.value;
	    
	    if(window.showModalDialog){
	 	   top.returnValue=obj2;
	 	}else{
	 		parent.parent.getselectcond_callbackfunc(obj2);
	 	}
	    windowClose();
	    /*
	    window.returnValue=obj2;
		window.close();
		*/
}
function windowClose(){
	if(window.showModalDialog){
		parent.window.close();
	}else{
		//parent.parent.Ext.getCmp('getselectcond_showModalDialogs').close();
		parent.parent.closeExtWin();
	}
}
function searchSave(outparamters){
	var check=outparamters.getValue("check");
	if(check=='ok'){
		var wheresql=outparamters.getValue("wheresql");
		wheresql=wheresql!=null&&wheresql.length>1?wheresql:"ok";
		var sexpr=outparamters.getValue("sexpr");
		var sfactor=outparamters.getValue("sfactor");
		var fieldSetId=outparamters.getValue("fieldSetId");
		
		var obj = new Object();
	    obj.expr=sexpr;
	    obj.factor=sfactor;
	    obj.sql=wheresql;
	    window.returnValue=obj;
		window.close();
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
   document.getElementById("strTable").innerHTML=tabelstr; 
   checkMaintItem();
}
function addTable(itemid,desc,type,codesetid){
	var n=idArr.length-1;
	var id=n+"_"+itemid;
	var tabelstr="<tr onclick=\"onSelects('"+id+"');\">";
	tabelstr+="<td class=\"RecordRow noleft\" style=\"border-top: none;border-left:none;\" align=\"center\" height=\"30\">";
	if(n>0){
		tabelstr+="<select name=\""+id+"_logic\" onchange=\"logicChange(";
		tabelstr+=n+",this);\"><option value=\"*\">且</option>";
		tabelstr+="<option value=\"+\">或</option></select>";
	}else{
		tabelstr+="&nbsp;";
	}
	tabelstr+="</td><td class=\"RecordRow noleft\" style=\"border-top: none;\" height=\"30\">";
	tabelstr+=desc;
	tabelstr+="</td><td class=\"RecordRow noleft\" style=\"border-top: none;\" align=\"center\" height=\"30\">";
	tabelstr+="<select id=\""+id+"_eq\" name=\""+id+"_eq\" onchange=\"eqChange(";
	tabelstr+="this);\"><option value=\"=\">&nbsp;=</option>";
	tabelstr+="<option value=\"&gt;\">&nbsp;&gt;</option>";
	tabelstr+="<option value=\"&gt;=\">&gt;=</option>";
	tabelstr+="<option value=\"&lt;\">&nbsp;&lt;</option>";
	tabelstr+="<option value=\"&lt;=\">&lt;=</option>";
	tabelstr+="<option value=\"&lt;&gt;\">&lt;&gt;</option></select>";
	tabelstr+="</td><td nowrap class=\"RecordRow noleft NORIGHT\"  style=\"border-top: none;border-right:none;\">";
	if(type=='N'){
		tabelstr+="<input type=\"text\" id=\""+id+".value\" name=\""+id+".value\"";
		tabelstr+=" onkeypress=\"event.returnValue=IsDigit();\" style=\"width:120px\" class=\"text4\">";
	}else if(type=='D'){
		tabelstr+="<input type=\"text\" class=\"text4\" id=\""+id+".value\" name=\""+id+".value\"";
		tabelstr+=" ondblclick=\"showDateSelectBox(this);\" ";
		//tabelstr+=" extra=\"editor\"";
		//tabelstr+=" onblur=\"timeCheck(this);\"";
		tabelstr+=" style=\"width:120px;\"";
		//tabelstr+=" dropDown=\"dropDownDate\"";
		tabelstr+=">";
	}else if(type=='A'){
		if(codesetid=="0"){
			tabelstr+="<input type=\"text\" id=\""+id+".value\" name=\""+id+".value\" style=\"width:120px\" class=\"text4\">";
		}else{
			tabelstr+="<input type=\"text\" onblur=\"hzvalueTovalue('"+id+"');\" id=\""+id+".hzvalue\" name=\""+id+".hzvalue\" style=\"width:120px\" class=\"text4\">";
			tabelstr+="<input type=\"hidden\" id=\""+id+".value\" name=\""+id+".value\">";
			if("UM,UN,@K".indexOf(codesetid)==-1)
			{
				tabelstr+="&nbsp;<img  src=\"/images/code.gif\" align=\"absmiddle\" style=\"margin-left:5px;\" onclick=\"javascript:openCondCodeDialog('";
				tabelstr+=codesetid+"','"+id+".hzvalue');\">";
			}else
			{//按照单位或部门进行查询时组织机构树的显示请按权限过滤
				tabelstr+="&nbsp;<img  src=\"/images/code.gif\" align=\"absmiddle\" style=\"margin-left:5px;\" onclick=\"javascript:showDia('";
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
	var upCaseTable = tabelstr.toUpperCase();
	if(this.idArr.length>0&&n==0){
		str = tabelstr.substring(0,upCaseTable.indexOf("<SELECT"));
		tabelstr=tabelstr.substring(upCaseTable.indexOf("</SELECT>")+9,tabelstr.length);
		tabelstr=str+"&nbsp;"+tabelstr;
	}
	tabelstr=replaceAll(tabelstr,"<TBODY>","");
	tabelstr=replaceAll(tabelstr,"<tbody>","");
	tabelstr=replaceAll(tabelstr,"</TBODY>","");
	tabelstr=replaceAll(tabelstr,"</tbody>","");
//	tabelstr=tabelstr.replace("TOP","HEIGHT");
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
//19/3/20 xus 浏览器兼容 发送短信 选择机构页面 单独拿出来
function showDia(codeid,mytarget,managerstr,flag){
	if(isNotPC()){
		 gPopupMask = document.getElementById("popupMask");
		 gPopupContainer = document.getElementById("popupContainer");
		 gPopFrame = document.getElementById("popupFrame");	
		 loadcssfile("/js/subModal-1.6/style.css" ,"css");
		 loadcssfile("/js/subModal-1.6/subModal.css" ,"css");
		insertJS("/js/subModal-1.6/common.js","js" ,function(){
			insertJS("/js/subModal-1.6/subModal.js","js",function(){
					 gPopupIsShown = false;
					 gDefaultPage = "/js/subModal-1.6/loading.html";
					 gHideSelects = false;
					 gTabIndexes = new Array();
					 gTabbableTags = new Array("A","BUTTON","TEXTAREA","INPUT","IFRAME");	
					if (!document.all) {
						document.onkeypress = keyDownHandler;
					}
					if(null==gPopupContainer&&null==gPopupMask&&null==gPopFrame)
					initPopUp();
					thecodeurl="/system/newcodeselectposinputpos.jsp?codesetid="+codeid+"&codeitemid="+managerstr+"&isfirstnode=" + flag+"&isAccord=1&mytarget="+mytarget;
					showPopWin(thecodeurl, 400, 400, returnRefresh);//tianye update 兼容ipad产品
				});
		});
		return; 
	 }
	 
   var codevalue,thecodeurl,target_name,hidden_name,hiddenobj;
   if(mytarget==null)
     return;
   var oldInputs=document.getElementsByName(mytarget);
   oldobj=oldInputs[0];
   //根据代码显示的对象名称查找代码值名称	
   target_name=oldobj.name;
   hidden_name=target_name.replace(".viewvalue",".value"); 
   hidden_name=hidden_name.replace(".hzvalue",".value");
   hidden_name=hidden_name.replace("name1","namevalue");   
   var hiddenInputs=document.getElementsByName(hidden_name);
   if(hiddenInputs!=null&&hiddenInputs.length>0)
   {
   	hiddenobj=hiddenInputs[0];
   	codevalue=managerstr;
   }else{
   	hiddenobj=document.getElementById(hidden_name);
   	codevalue=managerstr;
   }
    var theArr=new Array(codeid,codevalue,oldobj,hiddenobj,flag); 
   thecodeurl="/system/codeselectposinputpos.jsp?codesetid="+codeid+"&codeitemid=&isfirstnode=" + flag; //xuj update 2011-5-11 兼容firefox、chrome
   var dw=300,dh=400,dl=(screen.width-dw)/2;dt=(screen.height-dh)/2;
	var dialogWidth="300px";
	var dialogHeight="400px";
   if (checkBrowser().indexOf("MSIE|6") != -1) {
   	dialogWidth="320px";
   	dialogHeight="440px";
   } 
   if(getBrowseVersion()){
	   var popwin= window.showModalDialog(thecodeurl, theArr, 
		    	 "dialogLeft:"+dl+"px;dialogTop:"+dt+"px;dialogWidth:"+dialogWidth+"; dialogHeight:"+dialogHeight+";resizable:no;center:yes;scroll:yes;status:no");
   }else{
	   var win = Ext.create('Ext.window.Window',{
		   id:'select_code',
		   title:'选择角色',
		   width:dw+20,
		   height:dh+20,
		   resizable:'no',
		   modal:true,
		   autoScoll:false,
		   autoShow:true,
		   autoDestroy:true,
		   html:'<iframe style="background-color:#fff" frameborder="0" SCROLLING=NO height="100%" width="100%" src="'+thecodeurl+'"></iframe>',
		   renderTo:Ext.getBody()
	   }); 
	   win.dialogArguments = theArr;
   }
}