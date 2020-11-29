var fieldObject = {};
function addfield(){    
	//var itemid = document.getElementById("itemid").value;
	var sortitem = document.getElementById("sortitem").value;
	sortitem=sortitem!='undefined'?sortitem:"";
	sortitem=sortitem!=null?sortitem:"";
	var id="";
	
	var vos= document.getElementsByName("itemid");
	if(vos==null)
  		return false;
  	var right_vo=vos[0];
  	for(i=right_vo.options.length-1;i>=0;i--){
  		if(right_vo.options[i].selected){
  			var itemid = right_vo.options[i].value;
			sortitem+=itemid+":1`";
			id = itemid;
		}
	}
	document.getElementById("sortitem").value = sortitem;
	if(sortitem==""){
		alert("请选择需要添加的指标！");
		return false;
	}
	document.getElementById("dis_sort_table").innerHTML = outTable(sortitem);
	var arr = id.split(":");
	if(arr.length==2)
	{
		tr_bgcolor(arr[0]);
	}
	return true;
}
function addfield1(){    
	//var itemid = document.getElementById("itemid").value;
	var sortitem = document.getElementById("sortitem").value;
	sortitem=sortitem!='undefined'?sortitem:"";
	sortitem=sortitem!=null?sortitem:"";
	var id="";
	
	var vos= document.getElementsByName("orderid");
	if(vos==null)
  		return false;
  	var right_vo=vos[0];
  	for(i=right_vo.options.length-1;i>=0;i--){
  		if(right_vo.options[i].selected){
  			var itemid = right_vo.options[i].value;
			sortitem+=itemid+":1`";
			id = itemid;
		}
	}
	document.getElementById("sortitem").value = sortitem;
	document.getElementById("dis_sort_table").innerHTML = outTable1(sortitem);
	var arr = id.split(":");
		if(arr.length==2)
			tr_bgcolor(arr[0]);
}
function defField(){
	var sortitem = document.getElementById("sortitem").value;
	sortitem=sortitem!='undefined'?sortitem:"";
	sortitem=sortitem!=null?sortitem:"";
	document.getElementById("dis_sort_table").innerHTML = outTable(sortitem);
	var arr = sortitem.split("`");
	if(arr!=null&&arr.length>0){
		var arr_item = arr[0].split(":");
		if(arr_item.length==3){
			tr_bgcolor(arr_item[0]);
		}
	}
}
function defField1(){
	var sortitem = document.getElementById("sortitem").value;
	sortitem=sortitem!='undefined'?sortitem:"";
	sortitem=sortitem!=null?sortitem:"";
	document.getElementById("dis_sort_table").innerHTML = outTable1(sortitem);
	var arr = sortitem.split("`");
	if(arr!=null&&arr.length>0){
		var arr_item = arr[0].split(":");
		if(arr_item.length==3){
			tr_bgcolor(arr_item[0]);
		}
	}
}
function outTable(sortitem){
	/* 组织机构高级花名册-重新取数-选择排序指标页面样式修改 xiaoyun 2014-7-24 start */
	//var tabelstr = "<table width=\"100%\" class=\"ListTable1\" style=\"border-right:0\">";
	var tabelstr = "<table width=\"100%\" class=\"ListTable1\" style=\"border-right:0\" cellspacing=\"0\"  align=\"center\" cellpadding=\"0\">";
	/* 组织机构高级花名册-重新取数-选择排序指标页面样式修改 xiaoyun 2014-7-24 end */
	tabelstr += "<tr class=\"fixedHeaderTr1\">";
	tabelstr += "<td class=\"TableRow\" style=\"border-left:0px;border-top:none !important;\" width=\"10%\" align=\"left\">&nbsp;</td>";
	tabelstr += "<td class=\"TableRow\" width=\"65%\" style=\"border-top:none !important;\" align=\"center\">指标名称</td>";
	tabelstr += "<td class=\"TableRow\" style=\"border-right:0px;border-top:none !important;\" width=\"25%\" align=\"center\" >升降<td>";
	tabelstr += "</tr>";
	var arr = sortitem.split("`");
	if(arr.length>0){
		var n=1;
		for(var i=0;i<arr.length;i++){
			var arr_item = arr[i].split(":");
			if(arr_item.length==3){
				tabelstr+="<tr>";
				tabelstr+="<td align=\"center\"  style=\"border-left:0px;\" class=\"RecordRow\" onclick=\"tr_bgcolor('";
				tabelstr+=arr_item[0]+"')\" ondblclick=\"deletefield();\" nowrap>";
				tabelstr+=n+"</td>";
				tabelstr+="<td class=\"RecordRow\" onclick=\"tr_bgcolor('";
				tabelstr+=arr_item[0]+"')\" ondblclick=\"deletefield();\" nowrap>";
				tabelstr+=arr_item[1]+"</td>";
				tabelstr+="<td align=\"center\" class=\"RecordRow\" style=\"border-right:0px;\" onclick=\"tr_bgcolor('";
				tabelstr+=arr_item[0]+"')\" ondblclick=\"deletefield();\" nowrap>";
				tabelstr+="<select id="+arr_item[0]+" name="+arr_item[0];
				tabelstr+=" onchange=\"viewHide(document.getElementById('"+arr_item[0]+"'),'"+arr_item[0]+"');\">";
				if(arr_item[2]==1){
					tabelstr+="<option value=1 selected>升序</option>";
					tabelstr+="<option value=0>降序</option>";
				}else{
					tabelstr+="<option value=1>升序</option>";
					tabelstr+="<option value=0 selected>降序</option>";
				}
				tabelstr+="</select>";
				tabelstr+="</td></tr>";
				n++;
			}
		}
	}	
	tabelstr+="</table>";	
	return tabelstr;
}
function outTable1(sortitem){
	var tabelstr = "<table width=\"100%\" border=\"0\" class=\"ListTable1\">";
	tabelstr += "<tr class=\"fixedHeaderTr1\">";
	tabelstr += "<td class=\"TableRow\" width=\"10%\" align=\"left\">&nbsp;</td>";
	tabelstr += "<td class=\"TableRow\" width=\"65%\" align=\"center\">指标名称</td>";
	tabelstr += "<td class=\"TableRow\" width=\"25%\" align=\"center\">升降<td>";
	tabelstr += "</tr>";
	var arr = sortitem.split("`");
	if(arr.length>0){
		var n=1;
		for(var i=0;i<arr.length;i++){
			var arr_item = arr[i].split(":");
			if(arr_item.length==3){
				tabelstr+="<tr>";
				tabelstr+="<td align=\"center\" class=\"RecordRow\" onclick=\"tr_bgcolor('";
				tabelstr+=arr_item[0]+"')\" ondblclick=\"deletefield1();\" nowrap>";
				tabelstr+=n+"</td>";
				tabelstr+="<td class=\"RecordRow\" onclick=\"tr_bgcolor('";
				tabelstr+=arr_item[0]+"')\" ondblclick=\"deletefield1();\" nowrap>";
				tabelstr+=arr_item[1]+"</td>";
				tabelstr+="<td align=\"center\" class=\"RecordRow\" onclick=\"tr_bgcolor('";
				tabelstr+=arr_item[0]+"')\" ondblclick=\"deletefield1();\" nowrap>";
				tabelstr+="<select name="+arr_item[0];
				tabelstr+=" onchange=\"viewHide(this,'"+arr_item[0]+"');\">";
				if(arr_item[2]==1){
					tabelstr+="<option value=1 selected>升序</option>";
					tabelstr+="<option value=0>降序</option>";
				}else{
					tabelstr+="<option value=1>升序</option>";
					tabelstr+="<option value=0 selected>降序</option>";
				}
				tabelstr+="</select>";
				tabelstr+="</td></tr>";
				n++;
			}
		}
	}	
	tabelstr+="</table>";	
	return tabelstr;
}
function deletefield(){
	var sortitem = document.getElementById("sortitem").value;
	var sortitemid = document.getElementById("sortitemid").value;
	var arr = sortitem.split("`");
	var item="";
	var id="";
	var n=0;
	var bid="";
	var fieldSetId = undefined;
	if(document.getElementById("fieldid")) {
		fieldSetId = document.getElementById("fieldid").value;
	}
	for(var i=0;i<arr.length;i++){
		var arr_item = arr[i].split(":");
		if(arr_item.length==3){
			if(arr_item[0].toLowerCase()!=sortitemid.toLowerCase()){
				item+=arr[i]+"`";
				bid = arr_item[0];
				if(n==1){
					id=arr_item[0];
					n=0;
				}
			}else{
				var itemid=arr_item[0]+":"+arr_item[1];
				eval("var fieldid = fieldObject." + arr_item[0]);
				if(fieldSetId == fieldid || (!fieldSetId && !fieldid)) {
					delete fieldObject[arr_item[0]];
					additemright("itemid",itemid,arr_item[1]);
					n=1;
				}
			}
		}
	}
	if(id==null||id.length<1)
		id = bid;
	document.getElementById("sortitemid").value=id;
	item=item!=null?item:"";
	document.getElementById("sortitem").value = item;
	document.getElementById("dis_sort_table").innerHTML = outTable(item);
	if(id!=null&&id.length>0)
		tr_bgcolor(id);
}
function deletefield1(){
	var sortitem = document.getElementById("sortitem").value;
	var sortitemid = document.getElementById("sortitemid").value;
	var arr = sortitem.split("`");
	var item="";
	var id="";
	var n=0;
	var bid="";
	for(var i=0;i<arr.length;i++){
		var arr_item = arr[i].split(":");
		if(arr_item.length==3){
			if(arr_item[0].toLowerCase()!=sortitemid.toLowerCase()){
				item+=arr[i]+"`";
				bid = arr_item[0];
				if(n==1){
					id=arr_item[0];
					n=0;
				}
			}else{
				var itemid=arr_item[0]+":"+arr_item[1];
				additemright("orderid",itemid,arr_item[1]);
				n=1;
			}
		}
	}
	if(id==null||id.length<1)
		id = bid;
	document.getElementById("sortitemid").value=id;
	item=item!=null?item:"";
	document.getElementById("sortitem").value = item;
	document.getElementById("dis_sort_table").innerHTML = outTable1(item);
	if(id!=null&&id.length>0)
		tr_bgcolor(id);
}
function additemright(sourcebox_id,itemid,itemdesc){
	var left_vo,vos,i;
	vos= document.getElementsByName(sourcebox_id);
	if(vos==null)
		return false;
	left_vo=vos[0];
	var no = new Option();
	no.value=itemid;
	no.text=itemdesc;
	left_vo.options[left_vo.options.length]=no;
}
function tr_bgcolor(itemid){
	var tablevos=document.getElementsByTagName("select");
	for(var i=0;i<tablevos.length;i++){
	    var cvalue = tablevos[i];
	    var td = cvalue.parentNode.parentNode;
	    td.style.backgroundColor = '';
   	}
	var c = document.getElementById(itemid);
	if(c!=null){
    	var tr = c.parentNode.parentNode;
    	if(tr.style.backgroundColor!=''){
    		tr.style.backgroundColor = '' ;
    	}else{
    		tr.style.backgroundColor = '#fff8d2' ;
    	}
    }
	
	document.getElementById("sortitemid").value=itemid;
}
function upSort(){
	var sortitem = document.getElementById("sortitem").value;
	var sortitemid = document.getElementById("sortitemid").value;
	if(sortitemid==null||sortitemid.length<1)
		return false;
	
	var arr = sortitem.split("`");
	var item="";
	var n=0;
	for(var i=0;i<arr.length;i++){
		var arr_item = arr[i].split(":");
		if(arr_item.length==3){
			if(arr_item[0].toLowerCase()==sortitemid.toLowerCase()){
				n=i;
				break;
			}
		}
	}
	if(n>0){
		var sortitem = arr[n];
		arr[n]=arr[n-1];
		arr[n-1]=sortitem;
	}
	for(var i=0;i<arr.length;i++){
		if(arr[i].length>0)
			item+=arr[i]+"`";
	}
	item=item!=null?item:"";
	document.getElementById("sortitem").value = item;
	document.getElementById("dis_sort_table").innerHTML = outTable(item);
	tr_bgcolor(sortitemid);
}
function upSort1(){
	var sortitem = document.getElementById("sortitem").value;
	var sortitemid = document.getElementById("sortitemid").value;
	if(sortitemid==null||sortitemid.length<1)
		return false;
	
	var arr = sortitem.split("`");
	var item="";
	var n=0;
	for(var i=0;i<arr.length;i++){
		var arr_item = arr[i].split(":");
		if(arr_item.length==3){
			if(arr_item[0].toLowerCase()==sortitemid.toLowerCase()){
				n=i;
				break;
			}
		}
	}
	if(n>0){
		var sortitem = arr[n];
		arr[n]=arr[n-1];
		arr[n-1]=sortitem;
	}
	for(var i=0;i<arr.length;i++){
		if(arr[i].length>0)
			item+=arr[i]+"`";
	}
	item=item!=null?item:"";
	document.getElementById("sortitem").value = item;
	document.getElementById("dis_sort_table").innerHTML = outTable1(item);
	tr_bgcolor(sortitemid);
}
function downSort(){
	var sortitem = document.getElementById("sortitem").value;
	var sortitemid = document.getElementById("sortitemid").value;
	if(sortitemid==null||sortitemid.length<1)
		return false;
	var arr = sortitem.split("`");
	var item="";
	var n=-1;
	for(var i=0;i<arr.length;i++){
		var arr_item = arr[i].split(":");
		if(arr_item.length==3){
			if(arr_item[0].toLowerCase()==sortitemid.toLowerCase()){
				n=i;
				break;
			}
		}
	}
	if(n>=0&&n<arr.length-1){
		var sortitem = arr[n];
		arr[n]=arr[n+1];
		arr[n+1]=sortitem;
	}
	for(var i=0;i<arr.length;i++){
		if(arr[i].length>0)
			item+=arr[i]+"`";
	}
	item=item!=null?item:"";
	document.getElementById("sortitem").value = item;
	document.getElementById("dis_sort_table").innerHTML = outTable(item);
	tr_bgcolor(sortitemid);
}
function downSort1(){
	var sortitem = document.getElementById("sortitem").value;
	var sortitemid = document.getElementById("sortitemid").value;
	if(sortitemid==null||sortitemid.length<1)
		return false;
	var arr = sortitem.split("`");
	var item="";
	var n=-1;
	for(var i=0;i<arr.length;i++){
		var arr_item = arr[i].split(":");
		if(arr_item.length==3){
			if(arr_item[0].toLowerCase()==sortitemid.toLowerCase()){
				n=i;
				break;
			}
		}
	}
	if(n>=0&&n<arr.length-1){
		var sortitem = arr[n];
		arr[n]=arr[n+1];
		arr[n+1]=sortitem;
	}
	for(var i=0;i<arr.length;i++){
		if(arr[i].length>0)
			item+=arr[i]+"`";
	}
	item=item!=null?item:"";
	document.getElementById("sortitem").value = item;
	document.getElementById("dis_sort_table").innerHTML = outTable1(item);
	tr_bgcolor(sortitemid);
}
function viewHide(obj,itemid){
	var sortitem = document.getElementById("sortitem").value;
	var arr = sortitem.split("`");
	var item="";
	for(var i=0;i<arr.length;i++){
		var arr_item = arr[i].split(":");
		if(arr_item.length==3){
			if(arr_item[0].toLowerCase()==itemid.toLowerCase()){
				item+=arr_item[0]+":"+arr_item[1]+":"+obj.value+"`";
			}else{
				item+=arr[i]+"`";
			}
		}
	}
	document.getElementById("sortitem").value = item;
}
function changeField(){
	var fieldid = document.getElementById("fieldid").value;	
	var sortitem = document.getElementById("sortitem").value;	
	var hashVo = new ParameterSet();
	hashVo.setValue("fieldid",fieldid);
	hashVo.setValue("sortitem",sortitem);
	var request=new Request({method:'post',asynchronous:false,onSuccess:changeItem,functionId:'3020110063'},hashVo);
}
function changeItem(outparamters){
	var itemlist = outparamters.getValue("itemlist");
	AjaxBind.bind(sortForm.itemid,itemlist);
	var flag=document.getElementById("flag").value
	if(flag=="2")
	{
		document.getElementById("sortitem").value = "";
		document.getElementById("dis_sort_table").innerHTML = outSubsetTable();
	}
}
function sub(){
	var sortitem = document.getElementById("sortitem").value;
	if(sortitem!=null&&sortitem.length>0){
		window.returnValue=sortitem;
  	}else{
  		window.returnValue="not";
  	}
	if(window.parent.window.opener)//非ie浏览器
		window.parent.window.opener.setPrevParamValues(returnValue,6);
	window.close();
}
function outSubsetTable(){
	var tabelstr = "<table width=\"100%\" border=\"0\" class=\"ListTable\">";
	tabelstr += "<tr class=\"fixedHeaderTr1\">";
	tabelstr += "<td class=\"TableRow\" width=\"10%\" align=\"left\">&nbsp;</td>";
	tabelstr += "<td class=\"TableRow\" width=\"65%\" align=\"center\">指标名称</td>";
	tabelstr += "<td class=\"TableRow\" width=\"25%\" align=\"center\">状态<td>";
	tabelstr += "</tr>";
	tabelstr+="</table>";	
	return tabelstr;
}
function defOrder(){
	var sortitem = document.getElementById("sortitem").value;
	if(!(sortitem!=null&&sortitem.length>0))
		if(window.confirm("确实要清除默认排序？")){
			
		}else{
			return;
		}
	//if(sortitem!=null&&sortitem.length>0){//xuj 2009-10-9改动，改动为可以清空默认排序字段，也就是不选择指标就点击默认排序
		var salaryid = document.getElementById("salaryid").value;
		//alert(sortitem+"  "+salaryid);
		var hashVo = new ParameterSet();
		hashVo.setValue("sortitem",sortitem);
		hashVo.setValue("salaryid",salaryid);
		var request=new Request({method:'post',asynchronous:false,onSuccess:defOrderSuccess,functionId:'3020110066'},hashVo);
  	//}else{
  		//alert('\u8bf7\u9009\u62e9\u6392\u5e8f\u6307\u6807\uff01');
  	//}
}
function defOrderSuccess(outparamters){
	var msg = outparamters.getValue("msg");
	alert(msg);
}