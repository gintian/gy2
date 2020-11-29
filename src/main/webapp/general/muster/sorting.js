function addfield(){    
	//var itemid = document.getElementById("itemid").value;
	var sortitem = document.getElementById("sortitem").value;
	sortitem=sortitem!='undefined'?sortitem:"";
	sortitem=sortitem!=null?sortitem:"";
	var id="";
	var vos= document.getElementsByName("sort_left_fields");
	if(vos==null)
  		return false;
  	var right_vo=vos[0];
  	for(i=right_vo.options.length-1;i>=0;i--){
  		if(right_vo.options[i].selected){
  			var itemid = right_vo.options[i].value;
  			var itemname = right_vo.options[i].text;
			sortitem+=itemid+":"+itemname+":0`";
			id = itemid;
		}
	}
	document.getElementById("sortitem").value = sortitem;
	document.getElementById("dis_sort_table").innerHTML = outTable(sortitem);
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
function outTable(sortitem){
	/* 组织机构-花名册-新建花名册-排序指标 xiaoyun 2014-7-23 start */
	//var tabelstr = "<table width=\"100%\" border=\"0\" class=\"ListTable\">";
	var tabelstr = "<table width=\"100%\" border=\"0\" cellspacing=\"0\" cellpadding=\"0\" class=\"ListTable\">";
	/* 组织机构-花名册-新建花名册-排序指标 xiaoyun 2014-7-23 end */
	tabelstr += "<tr style=\"position:relative;\">";
	tabelstr += "<td class=\"TableRow_top\" width=\"10%\" align=\"left\">&nbsp;</td>";
	tabelstr += "<td class=\"TableRow_lr\" width=\"65%\" align=\"center\">指标名称</td>";
	tabelstr += "<td class=\"TableRow_top\" width=\"25%\" align=\"center\">升降<td>";
	tabelstr += "</tr>";
	var arr = sortitem.split("`");
	if(arr.length>0){
		var n=1;
		for(var i=0;i<arr.length;i++){
			var arr_item = arr[i].split(":");
			if(arr_item.length==3){
				tabelstr+="<tr>";
				tabelstr+="<td align=\"center\" class=\"RecordRow_right\" onclick=\"tr_bgcolor('";
				tabelstr+=arr_item[0]+"')\" ondblclick=\"deletefield();\" nowrap>";
				tabelstr+=n+"</td>";
				tabelstr+="<td class=\"RecordRow\" id="+arr_item[0]+" onclick=\"tr_bgcolor('";
				tabelstr+=arr_item[0]+"')\" ondblclick=\"deletefield();\" nowrap>";
				tabelstr+=arr_item[1]+"</td>";
				tabelstr+="<td align=\"center\" class=\"RecordRow_left\" onclick=\"tr_bgcolor('";
				tabelstr+=arr_item[0]+"')\" ondblclick=\"deletefield();\" nowrap>";
				tabelstr+="<select name="+arr_item[0];
				tabelstr+=" onchange=\"viewHide(this,'"+arr_item[0]+"');\">";
				if(arr_item[2]==0){
					tabelstr+="<option value=0 selected>升序</option>";
					tabelstr+="<option value=1>降序</option>";
				}else{
					tabelstr+="<option value=0>升序</option>";
					tabelstr+="<option value=1 selected>降序</option>";
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
				var itemid=arr_item[0];
				additemright("sort_left_fields",itemid,arr_item[1]);
				n=1;
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
	var tr = c.parentNode;
	if(tr.style.backgroundColor!=''){
		tr.style.backgroundColor = '' ;
	}else{
		tr.style.backgroundColor = '#fff8d2' ;
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
  		window.close();
  	}else{
  		alert(SELECT_ITEM);
  		return false;
  	}
}
function outSubsetTable(){
	var tabelstr = "<table width=\"100%\" border=\"0\" class=\"ListTable\">";
	tabelstr += "<tr class=\"fixedHeaderTr1\">";
	tabelstr += "<td class=\"TableRow_top\" width=\"10%\" align=\"left\">&nbsp;</td>";
	tabelstr += "<td class=\"TableRow_lr\" width=\"65%\" align=\"center\">指标名称</td>";
	tabelstr += "<td class=\"TableRow_top\" width=\"25%\" align=\"center\">状态<td>";
	tabelstr += "</tr>";
	tabelstr+="</table>";	
	return tabelstr;
}