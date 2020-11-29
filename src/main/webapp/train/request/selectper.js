function selectPerson(){
	var currnode=Global.selectedItem;
	if(currnode==null)
    	return false;
	var id = currnode.uid;
	if(id==null)
		return false;
	var itemarr = id.split("::");
	if(itemarr.length==5){
		var nid = parent.document.getElementById("idarr").value;
		nid=nid!=null&&nid.length>0?nid:"";
		if(nid.indexOf(id)!=-1){
			return false;
		}
		addTable(itemarr);
		nid+=id+"`";
		parent.document.getElementById("idarr").value=nid;
		currnode.remove();
	}
}
function addSelectPerson(){
	var currnode=window.selectiframe.Global.selectedItem;
	if(currnode==null)
    	return false;
	var id = currnode.uid;
	if(id==null||id.length<1)
		return false;
	var itemarr = id.split("::");
	if(itemarr.length==5){
		var nid = document.getElementById("idarr").value;
		nid=nid!=null&&nid.length>0?nid:"";
		if(nid.indexOf(id)!=-1){
			return false;
		}
		addTableStr(itemarr);
		nid+=id+"`";
		document.getElementById("idarr").value=nid;
		currnode.remove();
	}
}
function tr_bgcolor(nid){
	var id = document.getElementById("idarr").value;
	var idArr = id.split("`");
	for(var i=0;i<idArr.length;i++){
		if(idArr[i]==null||idArr[i].length<1)
			continue;	
	    var cvalue = document.getElementById(idArr[i]);
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
function parent_tr_bgcolor(nid){
	var tablevos=document.all("namestr");
	for(var i=0;i<tablevos.rows.length;i++){
	    var tr = tablevos.rows[i];
	    if(tr.id==nid)
	    	tr.style.backgroundColor = '#FFF8D2' ;
	    else
	    	tr.style.backgroundColor = '';
    }
}
function addTableStr(itemarr){
	if(itemarr.length!=5)
   		return false;
	var tab=document.getElementById("person");
	var i=tab.rows.length;
   	var newRow=tab.insertRow(tab.rows.length-1);
   	var nid = itemarr[0]+"::"+itemarr[1]+"::"+itemarr[2]+"::"+itemarr[3]+"::"+itemarr[4];
   	newRow.id="cc"+i;
   	myNewCell=newRow.insertCell(0);
   	var tablestr = "<img src=\"/images/man.gif\" style=\"cursor:hand;\" onclick=\"selPerColor('";
   	tablestr+=nid;
   	tablestr+="');\"";
   	tablestr+=" ondblclick=\"deleterow()\">";
   	myNewCell.innerHTML =tablestr;
   	myNewCell=newRow.insertCell(1);
   	tablestr = "<div style=\"cursor:hand;\" id=\"";
   	tablestr+=nid;
   	tablestr+="\" onclick=\"selPerColor('";
   	tablestr+=nid;
   	tablestr+="');\"";
   	tablestr+=" ondblclick=\"deleterow()\">";
   	tablestr+=itemarr[1];
   	tablestr+="</div>";
   	myNewCell.innerHTML=tablestr;
}
function selPerColor(nid){
	tr_bgcolor(nid);
	document.getElementById("personid").value=nid;
}
function addTable(itemarr){
	if(itemarr.length!=5)
   		return false;
	var tab=parent.document.getElementById("person");
	var i=tab.rows.length;
   	var newRow=tab.insertRow(tab.rows.length-1);
   	var nid = itemarr[0]+"::"+itemarr[1]+"::"+itemarr[2]+"::"+itemarr[3]+"::"+itemarr[4];
   	newRow.id="cc"+i;
   	myNewCell=newRow.insertCell(0);
   	var tablestr = "<img src=\"/images/man.gif\" style=\"cursor:hand;\" onclick=\"selPerColor('";
   	tablestr+=nid;
   	tablestr+="');\"";
   	tablestr+=" ondblclick=\"deleterow()\">";
   	myNewCell.innerHTML =tablestr;
   	myNewCell=newRow.insertCell(1);
   	tablestr = "<div style=\"cursor:hand;\" id=\"";
   	tablestr+=nid;
   	tablestr+="\" onclick=\"selPerColor('";
   	tablestr+=nid;
   	tablestr+="');\"";
   	tablestr+=" ondblclick=\"deleterow()\">";
   	tablestr+=itemarr[1];
   	tablestr+="</div>";
   	myNewCell.innerHTML=tablestr;
}
function deleterow(flag){
	var nid = document.getElementById("personid").value;
	var id = document.getElementById("idarr").value;
	if(nid==null||nid.length<1)
		return false;
	if(id==null||id.length<1)
		return false;
	var idArr = id.split("`");
	var ids="";
	var n=0;
	for(var i=0;i<idArr.length;i++){
		if(idArr[i]==null||idArr[i].length<1)
			continue;
		if(nid==idArr[i]){
			n=i;
			break;	
		}
	}
	if(idArr.length>1){
		if(n<idArr.length-1){
			if(idArr[n+1]!=null&&idArr[n+1].length>0){
				ids=idArr[n+1];
			}else{
				ids=idArr[n-1];
			}
		}else if(n<idArr.length-1){
			ids=idArr[n-1];
		}
	}
	var tab=document.getElementById("person");
	tab.deleteRow(n);
	document.getElementById("idarr").value=id.replace(nid+"`","");
	var flag=document.getElementById("flag").value;
	flag=flag!=null&&flag.length>0?flag:1;
	if(flag==1||flag=='1'){
		addTreePerson(nid);
	}else{
		addNamePerson(nid);
	}
	if(ids!=null&&ids.length>0){
		selPerColor(ids);
	}
}
function addTreePerson(itemid){
	var itemarr=itemid.split("::");
	if(itemarr.length!=5)
		return false;
	var currnode=window.selectiframe.Global.all;
	var globObj;
	var flag = 0;
    for(var i=1;i<currnode.length;i++){
    	var obj=currnode[i];
    	if(obj!=null){
    		var id = obj.uid;
    		if(id!=null&&id==itemarr[2]){
    			globObj=obj;
    		}
    		if(id!=null&&id==itemid){
    			flag=1;
    		}
    	}	
    }
    if(flag==0){
    	var tmp = new window.selectiframe.xtreeItem(itemid,itemarr[1],"","",itemarr[1],"/images/man.gif","");
    	globObj.add(tmp); 
    }
}
function searchSelect(nbase,itemkey,preflag) {
	var cancelQuery=document.getElementById("cancelQuery");
	toggles("b_addfield1");
	toggles("b_delfield1");
	hides("b_addfield");
	hides("b_delfield");
	var name = document.getElementById("a_name").value;
	cancelQuery.style.display='';
	if(name==null||name.length<1){
		//alert(INPUTNAME_SEARCH+"!");
		cancelQuery.style.display='none';
		cancelSelect(nbase,itemkey,preflag);
		return false;
	}
		
	var urlstr = "/train/request/selectpre.do?b_pre=link&name=";
	urlstr+=$URL.encode(getEncodeStr(name))+"&nbase="+nbase+"&itemkey="+itemkey+"&preflag="+preflag;
	
	document.getElementById("flag").value='2';
	document.selectiframe.location.href=urlstr;
}
function cancelSelect(nbase,itemkey,preflag) {
	document.getElementById("cancelQuery").style.display='none';
	toggles("b_addfield");
	toggles("b_delfield");
	hides("b_addfield1");
	hides("b_delfield1");
	document.getElementById("flag").value='1';
	document.selectiframe.location.href="/train/request/selectifram.jsp?nbase="+nbase+"&itemkey="+itemkey+"&preflag="+preflag;
}
function selectName(id){
	parent_tr_bgcolor(id);
	document.getElementById("nameid").value=id;
}
function selectNamePer(id){
	if(id==null)
		return false;
	var itemarr = id.split("::");
	if(itemarr.length==5){
		var nid = parent.document.getElementById("idarr").value;
		nid=nid!=null&&nid.length>0?nid:"";
		if(nid.indexOf(id)!=-1){
			return false;
		}
		addTable(itemarr);
		nid+=id+"`";
		parent.document.getElementById("idarr").value=nid;
		document.getElementById("nameid").value='';
		deleteNameRow(id,1);
	}
}
function addSelectNamePer(){
	var id = window.selectiframe.document.getElementById("nameid").value;
	var itemarr = id.split("::");
	if(itemarr.length==5){
		var nid = document.getElementById("idarr").value;
		nid=nid!=null&&nid.length>0?nid:"";
		if(nid.indexOf(id)!=-1){
			return false;
		}
		addTableStr(itemarr);
		nid+=id+"`";
		document.getElementById("idarr").value=nid;
		window.selectiframe.document.getElementById("nameid").value='';
		deleteNameRow(id,2);
	}
}
function deleteNameRow(nid,flag){
	var tablevos ;
	if(flag==1){
		tablevos = document.getElementById("namestr");
	}else{
		tablevos = window.selectiframe.document.getElementById("namestr");
	}
	for(var i=0;i<tablevos.rows.length;i++){
	    var tr = tablevos.rows[i];
	    if(tr.id==nid){
	    	tr.removeNode(true);
	    	break
	    }
    }
}
function addNamePerson(nid){
	var idArr = nid.split("::");
	if(idArr.length!=5)
		return false;
	var tab = window.selectiframe.document.getElementById("namestr");
	var str = tab.innerHTML;
	if(str.indexOf(nid)!=-1){
		return false;
	}
	var newRow=tab.insertRow(tab.rows.length);
	newRow.id=nid;
	newRow.ondblclick="selectNamePer('"+nid+"')";
	newRow.Style="CURSOR:hand";
	newRow.onclick="selectName('"+nid+"')";
   	myNewCell=newRow.insertCell(0);
   	myNewCell.Class="RecordRow";
   	myNewCell.innerHTML = idArr[1];
   	myNewCell=newRow.insertCell(1);
   	myNewCell.Class="RecordRow";
   	myNewCell.innerHTML=idArr[3];
   	var tablestr = tab.innerHTML;
   	tablestr = replaceAll(tablestr,"Class","class");
   	tablestr = replaceAll(tablestr,"Style","style");
   	tablestr = replaceAll(tablestr,"\"RecordRow\"","RecordRow");
   	tablestr="<table id=\"namestr\" width=\"240\" border=\"0\" cellspacing=\"0\""
   			+"align=\"left\" cellpadding=\"0\" class=\"ListTable1\">"
   			+tablestr+"</table>";
   	window.selectiframe.document.getElementById("namePerson").innerHTML=tablestr;
}
function setOk(){
	var nid = document.getElementById("idarr").value;
	window.returnValue=nid;
	window.close();
}
