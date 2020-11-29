var idArr=new Array();
var descArr=new Array();
var logicArr=new Array();
var eqArr=new Array();
var typeArr=new Array();
var codeArr=new Array();
var fieldSetArr=new Array();
//用于区分是否是编制管理参数设置页面调用的
var bzsearch="";
function change(){
    var userAgent = navigator.userAgent; //取得浏览器的userAgent字符串
	var isOpera = userAgent.indexOf("Opera") > -1;
	var fieldid=fieldid=document.getElementsByName("fieldid")[0].value;
	var in_paramters="tablename="+fieldid;
    var request=new Request({method:'post',asynchronous:false,parameters:in_paramters,onSuccess:showFieldList,functionId:'3020110073'});
}
function change(oper){
	var hashvo=new ParameterSet();
	hashvo.setValue("oper",oper);
	var fieldid='';
	fieldid=document.getElementsByName("fieldid")[0].value;
	var in_paramters="tablename="+fieldid;
    var request=new Request({method:'post',asynchronous:false,parameters:in_paramters,onSuccess:showFieldList,functionId:'3020110073'},hashvo);
}
function showFieldList(outparamters){
	var fieldlist=outparamters.getValue("fieldlist");
	AjaxBind.bind(searchInformForm.item_field,fieldlist);
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
function symbol(editor,strexpr){
	document.getElementsByName(editor)[0].focus();
	var element = document.selection;
	if(!element){
		element=window.getSelection();//非IE下获取
	}
	if (element!=null) {
		try{
			var rge = element.createRange();
			if (rge!=null){
				rge.text=strexpr;
			}
		}catch(e){//兼容非IE获取光标位置 在光标后插入内容
			var textArea=document.getElementsByName(editor)[0];
			var startPost=textArea.selectionStart;
			var endPost=textArea.selectionEnd;
			var scrollTop=textArea.scrollTop;
			textArea.value=textArea.value.substring(0, startPost) + strexpr + textArea.value.substring(endPost, textArea.value.length);
			textArea.selectionStart = startPost + strexpr.length;
			textArea.selectionEnd = endPost + strexpr.length;
			textArea.scrollTop = scrollTop;
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
/*??*/
function toggles(targetId){
	if (document.getElementById(targetId)){
		target = document.getElementById(targetId);
		target.style.display = "block";
	}
}
/*??*/
function hides(targetId){
	if (document.getElementById(targetId)){
		target = document.getElementById(targetId);
		target.style.display = "none";
	}
}
function onSelects(itemid){
	document.getElementsByName("itemid")[0].value=itemid;
	tr_bgcolor(itemid+".value");
}
function searchSetCond(a_code,tablename,type,no_manager_priv){
	var condstr='',condlogic='';
	var obj = document.getElementById("like");
	var like="0";
	if(obj)
	{
	if(obj.checked)
		like="1";
	}
	for(var i=0;i<this.idArr.length;i++){
		var itemid = this.idArr[i];
		condstr+=this.logicArr[i]+(i+1);
		var itemvalue = document.getElementById(i+"_"+itemid+".value").value;
		if(this.typeArr[i].toUpperCase()=='A')
		{
		   if(codeArr[i]!='0')
		   {
	    	 var itemhzvalue=document.getElementById(i+"_"+itemid+".hzvalue").value;
		     if(trim(itemhzvalue).length>0&&(itemhzvalue.indexOf("?")!=-1||itemhzvalue.indexOf("*")!=-1))
		     {
		        condlogic+=itemid+replaceKeyWord(this.eqArr[i]);	
		        condlogic+=itemhzvalue
		        if(like=='1')
		          condlogic+="*";
		        condlogic+="`";
		    }else{
	    	   condlogic+=itemid+replaceKeyWord(this.eqArr[i])+itemvalue;
	    	    if(like=='1')
		          condlogic+="*";
		        condlogic+="`";	
	        }
	      }else
	      {
	          condlogic+=itemid+replaceKeyWord(this.eqArr[i]);//字符型，前后都模糊
	          if(like=='1')
		          condlogic+="*";	
		        condlogic+=itemvalue
		        if(like=='1')
		          condlogic+="*";
		        condlogic+="`";
	      }
	   } else{
	        condlogic+=itemid+replaceKeyWord(this.eqArr[i])+itemvalue+"`";	
	    }
	}
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
		for (i=0;i<tempobj.length;i++){  
		 	if(tempobj[i].checked){
		 		unite=tempobj[i].value;
		 	}
	 	}
	}
	var expression = document.getElementById("cond").value
    var hashvo=new ParameterSet();
	hashvo.setValue("sexpr",getEncodeStr(expression));
	hashvo.setValue("sfactor",getEncodeStr(condlogic));
	hashvo.setValue("like",like);
	hashvo.setValue("history",history);
	hashvo.setValue("a_code",a_code);
	hashvo.setValue("tablename",tablename);
	hashvo.setValue("query_type","2");
	hashvo.setValue("type",type);
	hashvo.setValue("unite",unite);
	var fieldSetId='0';
	if(document.getElementById("fsid"))
	{
	  fieldSetId=document.getElementById("fsid").value;
	}
	hashvo.setValue("fieldSetId",fieldSetId);
	if(!no_manager_priv)
		no_manager_priv = "false";
	hashvo.setValue("no_manager_priv",no_manager_priv);
	var request=new Request({method:'post',asynchronous:false,onSuccess:searchSave,functionId:'3020110075'},hashvo);
}
function searchSave(outparamters){
	var check=outparamters.getValue("check");
	if(check=='ok'){
		var wheresql=outparamters.getValue("wheresql");
		wheresql=wheresql!=null&&wheresql.length>1?wheresql:"ok";
		var sexpr=outparamters.getValue("sexpr");
		var sfactor=outparamters.getValue("sfactor");
		var fieldSetId=outparamters.getValue("fieldSetId");
		if(fieldSetId=='0')
	     	window.returnValue=wheresql;
	    else
	    {
	         var obj = new Object();
	         obj.expr=sexpr;
	         obj.factor=sfactor;
	         window.returnValue=obj;
	    }
		window.close();
	}
}
function additemtr(sourcebox_id){
  var left_vo,vos,i;
  vos= document.getElementsByName(sourcebox_id);
  if(vos==null)
  	return false;
  left_vo=vos[0];
  this.updateInputValue();
  var tabelstr=document.getElementById("strTable").innerHTML;
  tabelstr=tabelstr.replace("</table>","").replace("</TABLE>","");
  tabelstr = tabelstr.replace(/style= onclick/g,'style="" onclick');
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
    				this.fieldSetArr[n]=arr[3];
    				tabelstr+=addTable(arr[0],desc,arr[1].toUpperCase(),arr[2],"=",'',''); 
    			}
    		}
    	}
   }
   tabelstr+="</table>";
//   tabelstr=tabelstr.replace("TOP","HEIGHT");
   document.getElementById("strTable").innerHTML=tabelstr;
   addCond();
   checkMaintItem();
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
   document.getElementsByName("cond")[0].value=cond;
}

function updateInputValue(){
		var inputs=document.getElementsByTagName("input");
//		var inputs=document.getElementsByClassName("text4");//ie8不支持此方法获取
		var i=0;
		for(;i<inputs.length;i++){
			if(inputs[i]&&inputs[i].type=='text'&&inputs[i].className=='text4'){
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

function addTable(itemid,desc,type,codesetid,eq,values){
	var n=idArr.length-1;
	var id=n+"_"+itemid;
	var tabelstr="<tr onclick=\"onSelects('"+id+"');\">";
	tabelstr+="<td class=\"RecordRowTop0\" align=\"center\"  style=\"border-left: 0px;\" height=\"30\">";
	tabelstr+=(n+1);
	tabelstr+="</td><td class=\"RecordRowTop0\"  height=\"30\">";
	tabelstr+=desc;
	tabelstr+="</td><td class=\"RecordRowTop0\" height=\"30\" align=\"center\">";
	tabelstr+="<select name=\""+id+"_eq\" onchange=\"eqChange(this);\">";
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
	tabelstr+="</td><td class=\"RecordRowTop0\" align=\"left\" style=\"border-right: 0px;\">";//bug 号：36911 ie8展现有问题修改
	if(type=='N'){
		tabelstr+="<input type=\"text\" name=\""+id+".value\" id=\""+id+".value\" value=\""+values+"\"";
		tabelstr+=" onkeypress=\"event.returnValue=IsDigit();\" style=\"width:110px\" class=\"text4\">";
	}else if(type=='D'){
		tabelstr+="<input type=\"text\" name=\""+id+".value\" id=\""+id+".value\" value=\""+values+"\"";
		tabelstr+=" ondblclick=\"showDateSelectBox(this);\" ";
		tabelstr+=" style=\"width:110px;\"";
		tabelstr+=" class=\"text4\">";
	}else if(type=='A'){
		if(codesetid=="0"){
			tabelstr+="<input type=\"text\" name=\""+id+".value\" id=\""+id+".value\"  value=\""+values+"\" style=\"width:110px\" class=\"text4\">";
		}else{
			var valuearr = values.split(",");
			if(valuearr.length!=2){
				valuearr[0]='';
				valuearr[1]='';
			}
			if(moduleFlag=='hmuster'){
				tabelstr+="<input type=\"text\" name=\""+id+".hzvalue\" id=\""+id+".hzvalue\" onblur=\"hzvalueTovalue('"+id+"');\" value=\""+valuearr[1]+"\" style=\"width:110px;\" class=\"text4\">";
				tabelstr+="<input type=\"hidden\" name=\""+id+".value\" id=\""+id+".value\" value=\""+valuearr[0]+"\">";
				if("UN,@K".indexOf(codesetid)==-1&&itemid!="e0122")//修改支持部门选择单位
				{
					tabelstr+="&nbsp;<img src=\"../../../images/code.gif\" style=\"vertical-align:middle;\"  id=\""+id+".hzvalue_0\" plugin=\"codetree\" codesetid=\""+codesetid+"\" inputname=\""+id+".hzvalue\" valuename=\""+id+".value\"   multiple =\"false\" onclick=\"codeClick(['"+id+".hzvalue_0'])\"/>";
				}else
				{//按照单位或部门进行查询时组织机构树的显示请按权限过滤
				    var pflag=document.getElementById("pflag").value;
					 tabelstr+="&nbsp;<img src=\"../../../images/code.gif\" style=\"vertical-align:middle;\"  id=\""+id+".hzvalue_0\" onlySelectCodeset=\"true\" plugin=\"codetree\" codesetid=\""+codesetid+"\" inputname=\""+id+".hzvalue\" valuename=\""+id+".value\"   multiple =\"false\" onclick=\"codeClick(['"+id+".hzvalue_0'])\"/>";
				}
			}else{
				tabelstr+="<input type=\"text\" name=\""+id+".hzvalue\" id=\""+id+".hzvalue\"  onblur=\"hzvalueTovalue('"+id+"');\" value=\""+valuearr[1]+"\" style=\"width:110px\" class=\"text4\">";
				tabelstr+="<input type=\"hidden\" name=\""+id+".value\" id=\""+id+".value\" value=\""+valuearr[0]+"\">";
				
				//if("UM,UN,@K".indexOf(codesetid)==-1)
				if("UN,@K".indexOf(codesetid)==-1&&itemid!="e0122")//修改支持部门选择单位
				{
					tabelstr+="&nbsp;<img  src=\"/images/code.gif\" align=\"absmiddle\" onclick=\"javascript:openCondCodeDialog('";
					tabelstr+=codesetid+"','"+id+".hzvalue');\">";
				}else
				{//按照单位或部门进行查询时组织机构树的显示请按权限过滤
				    var pflag=document.getElementById("pflag").value;
					tabelstr+="&nbsp;<img  src=\"/images/code.gif\" align=\"absmiddle\" onclick=\"javascript:openCodeDialog('";
					if(pflag=='1')
					    manageCode='';
					tabelstr+=codesetid+"','"+id+".hzvalue','"+manageCode+"',1);\">";
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
function editTable(tablearr){
  if(tablearr==null||tablearr.length<1){
  	return;
  }
  var lexarr = tablearr.split("||");
  if(lexarr.length!=4){
  	return;
  }
  var obj = document.getElementById("like");
  if(obj)
  {
  if(lexarr[3]=="1")
  	obj.checked=true;
  }
  obj = document.getElementById("history");
  if(obj)
  {
  if(lexarr[2]=="1")
  	obj.checked=true;
  }
  var arr = lexarr[0].split("`");
  document.getElementById("cond").value=lexarr[1];
  var tabelstr=document.getElementById("strTable").innerHTML;
  tabelstr=tabelstr.replace("</table>","").replace("</TABLE>","");
  tabelstr = tabelstr.replace(/style= onclick/g,'style="" onclick');
  var n=0;
  for(var i=0;i<arr.length;i++){
  	if(arr[i]!=null&&arr[i].length>1){
  		var itemarr = arr[i].split(":");
  		if(itemarr.length==7){
			this.idArr[n]=itemarr[0];
    		this.eqArr[n]=itemarr[4];
    		this.descArr[n]=itemarr[1];
    		this.typeArr[n]=itemarr[3];
    		this.codeArr[n]=itemarr[2];
    		this.fieldSetArr[n]=itemarr[6];
    		tabelstr+=addTable(itemarr[0],itemarr[1],itemarr[3].toUpperCase(),itemarr[2],itemarr[4],itemarr[5]); 
    		n++;
    	}
	  }
   }
   tabelstr+="</table>";
//   tabelstr=tabelstr.replace("TOP","HEIGHT");
   document.getElementById("strTable").innerHTML=tabelstr;
   setLogicArr();
   checkMaintItem();
}
function delTableStr(){
	var itemid = document.getElementsByName("itemid")[0].value;
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
	this.fieldSetArr=arrRemove(this.fieldSetArr,n);
	
	var obj = document.getElementById("tablestr");
	obj.deleteRow(n+1);
	this.updateInputValue();
	var tabelstr=document.getElementById("strTable").innerHTML;
	tabelstr = tabelstr.replace(/style= onclick/g,'style="" onclick');
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
		var str_new  = ">"+(i+1)+"</TD>";
		var str_old  = ">"+(i+2)+"</TD>";
		tabelstr=tabelstr.replace(str_old,str_new);
		str_new  = ">"+(i+1)+"</td>";
		str_old  = ">"+(i+2)+"</td>";
		tabelstr=tabelstr.replace(str_old,str_new);
		
		str_new = "height=\"30\" align=\"center\">"+(i+1)+"</TD>";
		str_old = "height=\"30\" align=\"center\">"+(i+2)+"</TD>";
		tabelstr=tabelstr.replace(str_old,str_new);
		//zxj:IE8下center变成了middle,原因不明,也许是ie8的bug,6/7/9/10均无问题
		str_new = "height=30 align=middle>"+(i+1)+"</TD>";
		str_old = "height=30 align=middle>"+(i+2)+"</TD>";
		tabelstr=replaceAll(tabelstr,str_old,str_new);
	}
	tabelstr=replaceAll(tabelstr,"<TBODY>","");
	tabelstr=replaceAll(tabelstr,"</TBODY>","");
	tabelstr=replaceAll(tabelstr,"<tbody>","");
	tabelstr=replaceAll(tabelstr,"</tbody>","");//38156	花名册：高级花名册取数查询，删除查询指标再添加，格线消失（火狐浏览器)
	document.getElementById("strTable").innerHTML=tabelstr;
	addCond();
	checkMaintItem();
	
	if(n<idArr.length){
		var selid = n+"_"+idArr[n];
		onSelects(selid);
		document.getElementsByName("itemid")[0].value=selid;
	}else{
		var selid = (n-1)+"_"+idArr[n-1];
		if(n!=0){
			onSelects(selid);
			document.getElementsByName("itemid")[0].value=selid;
		}else{
			document.getElementsByName("itemid")[0].value="";
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
function checkCond(flag,a_code,tablename,type,checkflag,no_manager_priv){
	var expression = document.getElementsByName("cond")[0].value;
	if(expression==null||expression.length<1){
		return;
	}
	
	if(!no_manager_priv)
		no_manager_priv = "false";
    var hashvo=new ParameterSet();
	hashvo.setValue("expression",getDecodeStr(expression));
	hashvo.setValue("factorlist",this.idArr);
	hashvo.setValue("a_code",a_code);
	hashvo.setValue("tablename",tablename);
	hashvo.setValue("type",type);
	hashvo.setValue("flag",flag);
	hashvo.setValue("checkflag",checkflag);
	hashvo.setValue("no_manager_priv",no_manager_priv);
	var request=new Request({method:'post',asynchronous:false,onSuccess:searchAndSave,functionId:'3020110074'},hashvo);	
}

var va_code;
var vtablename;
var vType;
function searchAndSave(outparamters){
	var check = outparamters.getValue("check");
	if(check=='ok'){
		var flag = outparamters.getValue("flag");
		var type = outparamters.getValue("type");
		var checkflag = outparamters.getValue("checkflag");
		if(flag==1){
			if(checkflag=='3'){
					var obj = document.getElementById("like");
					var like="0";
					if(obj)
					{
					if(obj.checked)
						like="1";
					}
					var condlogic='';
					for(var i=0;i<this.idArr.length;i++){
						var itemid = this.idArr[i];
						var itemvalue = document.getElementById(i+"_"+itemid+".value").value;
						if(this.typeArr[i].toUpperCase()=='A')
				{
				   if(codeArr[i]!='0')
				   {
			    	 var itemhzvalue=document.getElementById(i+"_"+itemid+".hzvalue").value;
				     if(trim(itemhzvalue).length>0&&(itemhzvalue.indexOf("?")!=-1||itemhzvalue.indexOf("*")!=-1))
				     {
				        condlogic+=itemid+replaceKeyWord(this.eqArr[i]);	
				        condlogic+=itemhzvalue
				        if(like=='1')
				          condlogic+="*";
				        condlogic+="`";
				    }else{
			    	   condlogic+=itemid+replaceKeyWord(this.eqArr[i])+itemvalue;
			    	    if(like=='1')
				          condlogic+="*";
				        condlogic+="`";	
			        }
			      }else
			      {
			          condlogic+=itemid+replaceKeyWord((this.eqArr[i]));// 字符型，前后都模糊
			          if(like=='1')
				          condlogic+="*";	
				        condlogic+=itemvalue
				        if(like=='1')
				          condlogic+="*";
				        condlogic+="`";
			      }
			   } else{
			        condlogic+=itemid+replaceKeyWord(this.eqArr[i])+itemvalue+"`";	
			    }
					}
					obj = document.getElementById("history");
					var history="0";
					if(obj)
					{
					if(obj.checked)
						history="1";
					}
			
				var expression = getDecodeStr(outparamters.getValue("expression"));
				var urlstr="/general/inform/search/gmsearcher.do?b_search=link&like="+like+"&sexpr="+encode_v1(expression)+"&sfactor="+$URL.encode(condlogic);
				document.getElementById("divid").style.display='none';
				document.getElementById("iframeid").style.display='';
				if(!getBrowseVersion())//非ie浏览器  样式修改   wangb 20190319
					document.getElementById("iframeid").style.width='99%';
				document.getElementById("iframeid").src=urlstr;
				
			}else{
				var a_code = outparamters.getValue("a_code");
				var tablename = outparamters.getValue("tablename");
				var no_manager_priv = outparamters.getValue("no_manager_priv");
				searchSetCond(a_code,tablename,type,no_manager_priv);
			}
		}else if(flag==2){
		    var obj = document.getElementById("like");
			var like="0";
			if(obj)
			{
			if(obj.checked)
				like="1";
			}
			var condlogic='';
			for(var i=0;i<this.idArr.length;i++){
				var itemid = this.idArr[i];
				var itemvalue = document.getElementById(i+"_"+itemid+".value").value;
				if(this.typeArr[i].toUpperCase()=='A')
		{
		   if(codeArr[i]!='0')
		   {
	    	 var itemhzvalue=document.getElementById(i+"_"+itemid+".hzvalue").value;
		     if(trim(itemhzvalue).length>0&&(itemhzvalue.indexOf("?")!=-1||itemhzvalue.indexOf("*")!=-1))
		     {
		        condlogic+=itemid+replaceKeyWord(this.eqArr[i]);	
		        condlogic+=itemhzvalue
		        if(like=='1')
		          condlogic+="*";
		        condlogic+="`";
		    }else{
	    	   condlogic+=itemid+replaceKeyWord(this.eqArr[i])+itemvalue;
	    	    if(like=='1')
		          condlogic+="*";
		        condlogic+="`";	
	        }
	      }else
	      {
	          condlogic+=itemid+replaceKeyWord((this.eqArr[i]));//字符型，前后都模糊
	          if(like=='1')
		          condlogic+="*";	
		        condlogic+=itemvalue
		        if(like=='1')
		          condlogic+="*";
		        condlogic+="`";
	      }
	   } else{
	        condlogic+=itemid+replaceKeyWord(this.eqArr[i])+itemvalue+"`";	
	    }
			}
			obj = document.getElementById("history");
			var history="0";
			if(obj)
			{
			if(obj.checked)
				history="1";
			}
			var expression = getDecodeStr(outparamters.getValue("expression"));
			var theArr=new Array(expression,condlogic,type,like,history);
			var userAgent = window.navigator.userAgent; //取得浏览器的userAgent字符串  
			var isOpera = userAgent.indexOf("Opera") > -1;
			var isIE = (userAgent.indexOf("compatible") > -1 && userAgent.indexOf("MSIE") > -1 && !isOpera); 
			
			var thecodeurl ="/general/inform/search/common.do?b_query=link&type="+type+"&flag=insert"; 
			var return_vo=null;
			if(isIE){
				return_vo= window.showModalDialog(thecodeurl,theArr,
				"dialogWidth:400px; dialogHeight:350px;resizable:no;center:yes;scroll:no;status:no;");
			}else{
				var dt=(window.screen.availHeight - 30 - 460) / 2;  //获得窗口的垂直位置
				var dl=(window.screen.availWidth - 10 - 790) / 2; //获得窗口的水平位置 
				window.theArr=theArr;
			      window.open(thecodeurl,"_blank",'toolbar=no,location=no,directories=no,status=no,menubar=no,scrollbars=no,resizable=no,top='+dt+'px,left='+dl+'px,width=400px,height=350px');
			      va_code=outparamters.getValue("a_code");
			      vtablename=outparamters.getValue("tablename");
			      vType=type;
			}
			
            if(return_vo!=null&&return_vo.length>0){
            	if("0" == bzsearch) {
            		parent.Ext.getCmp("ps_parameter").return_vo=return_vo;
            	} else {
            		var a_code = outparamters.getValue("a_code");
            		var tablename = outparamters.getValue("tablename");
            		searchSetCond(a_code,tablename,type);
            	}
            	
            }
		}
	}else{
		alert(check);
	}
}
function returnVo(return_vo){
	  if(return_vo!=null&&return_vo.length>0){
		  if("0" == bzsearch) {
			  parent.Ext.getCmp("ps_parameter").return_vo=return_vo;
      	  } else {
      		  //参数变量改为全局变量，非IE回调
      		  searchSetCond(va_code,vtablename,vType);
      	  }
      }
}

function setLogicArr(){
	var expression = document.getElementsByName("cond")[0].value;
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
	//Element.show('date_panel');   
	document.getElementById('date_panel').style.display='block';
	var pos=getAbsPosition(date_desc);
	with($('date_panel')){
		style.position="absolute";
		//19/3/23 xus 浏览器兼容 高级花名册 ie双击日期型指标 弹窗样式错乱
		if(navigator.appName.indexOf("Microsoft")!= -1){
            style.posLeft=pos[0]-1;
            style.posTop=pos[1]-1+srcobj.offsetHeight;
        }else{
            style.left=pos[0]+"px";
            style.top=pos[1]+srcobj.offsetHeight+"px";
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
function ps_close()
{
	//window.returnValue = "2";
	if(parent.Ext && parent.Ext.getCmp('ps_parameter')){//浏览器兼容改用Ext弹窗，关闭 wangb 20190425
		parent.Ext.getCmp('ps_parameter').close();
		return;
	}
	window.close();
}
function alertLert(obj,id)
{
   var trElement = document.getElementById(id);
   if(obj.checked)
       trElement.style.display="block";
   else
        trElement.style.display="none";
}
