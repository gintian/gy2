<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%
	int i=0;
%>

<style id=iframeCss>
div{
	cursor:hand;font-size:12px;
   }
a{
text-decoration:none;color:black;font-size:12px;
}

a.a1:active {
	color: #003100;
	text-decoration: none;
}
a.a1:hover {
	color: #FFCC00;
	text-decoration: none;
}
a.a1:visited {	
	text-decoration: none;
}
a.a1:link {
	color: #003100;
	text-decoration: none;
}
.fixedDiv6 
{ 
    overflow-x:hidden ;
    width:450px!important;
    height: 370px!important;
}
.trShallow {  
	BORDER-RIGHT: medium none; 
}

.trDeep {  
	BORDER-RIGHT: medium none;
}
.ListTable2 {
	border:1px solid #C4D8EE;
	border-collapse:collapse; 
	BORDER-BOTTOM: medium none; 
    BORDER-LEFT: medium none; 
    BORDER-TOP: medium none; 
    BORDER-RIGHT: medium none;
}
.RecordRowx {
	border: inset 1px #C4D8EE;
	BORDER-BOTTOM: #C4D8EE 1pt solid; 
	BORDER-LEFT: #C4D8EE 1pt solid; 
	BORDER-RIGHT: #C4D8EE 1pt solid; 
	BORDER-TOP: #C4D8EE 1pt solid;
	font-size: 12px;
	height:22;
	font-weight: bold;
	background-color:#f4f7f7;
	valign:middle;
}
.RecordRow {
	border: inset 1px #C4D8EE;
	BORDER-BOTTOM: #C4D8EE 1pt solid; 
	BORDER-LEFT: #C4D8EE 1pt solid; 
	BORDER-RIGHT: #C4D8EE 1pt solid; 
	BORDER-TOP: medium none;
	font-size: 12px;
	border-collapse:collapse; 
	height:22;
	valign:middle;
}
.RecordRowz {
	border: inset 1px #C4D8EE;
	BORDER-BOTTOM: #C4D8EE 1pt solid; 
	BORDER-LEFT: #C4D8EE 1pt solid; 
	BORDER-RIGHT: #C4D8EE 1pt solid; 
	BORDER-TOP: medium none;
	font-size: 12px;
	border-collapse:collapse; 
	height:22;
	background-color:#f4f7f7;
}
</style>
<script language="javascript">
var mn=0;
var addr=0;
var adds=0;
var addx=0;    //信息集个数
var arr=new Array();
var brr=new Array();
var arrlen=0;
var idlist = ",";
var undeleteid=",";
var pdym=0;
var fieldsetdesc="";

function tabler31(){
	window.parent.Ext.getCmp("iframe_body2").hide();
}
<logic:notEqual name="courseTrainForm" property="student" value="student">
	tabler31();
</logic:notEqual>
function deletenews(did){ /*删除*/
	addr=0;
	adds=0;
	mn=0;
	addx--;
	arr =idlist.split(",");
	idlist=",";
	var fid = arr[did];
	var selectitems="";
	for(var i=1;i<arr.length-1;i++){
		if(fid!=arr[i]){
			var selects=document.getElementsByName('selectflag'+i);
			for(var j=0;j<selects.length;j++){
				if(selects[j].type=='checkbox'&&selects[j].checked){
					selectitems+="`"+selects[j].value;//将被选中的指标组成一个新的list
				}
			}
		}
	}
	for(var i=1;i<arr.length-1;i++){
		addr++;
		if(fid!=arr[i]){
			adds++;
			idlist = idlist+arr[i]+",";
			var hashvo=new ParameterSet();
			hashvo.setValue("fieldsetid",arr[i]);
			hashvo.setValue("selectlist",selectitems);
			var request=new Request({method:'post',asynchronous:false,onSuccess:addResult,functionId:'202003003310'},hashvo);
		}
		if(addr==arr.length-2){
			showsetinfo("a");   //使最后一个信息集展现出来，其他的收起来
		}
		if(arr.length==3){
			changeInfor();    //当最后一个信息集被删除时，页面调整
		}
	}
}
function addnews(){  //多子集，按+号
	mn=0;
	var fieldsetid=$F("fieldsetid");
	if(idlist.indexOf(fieldsetid)!=-1){
		alert(TRAIN_CALSS_FILEDEXSIT);
	}else{
		addx++;
		addr=0;
		adds=0;
		idlist = idlist+fieldsetid+",";
		arr =idlist.split(","); 
		arrlen=arr.length-1;
		var selectitems="";
		for(var i=1;i<arrlen;i++){
			var selects=document.getElementsByName('selectflag'+i);
			if(arrlen==2)
				selects=document.getElementsByName('selectflag');
			for(var j=0;j<selects.length;j++){
				if(selects[j].type=='checkbox'&&selects[j].checked){
					selectitems+="`"+selects[j].value;//将被选中的指标组成一个新的list
				}
			}
			
		}
		for(var i=1;i<arrlen;i++){
			addr++;
			adds++;
			if(addr==arrlen-1){
				showsetinfo("a");   //使最后一个子集展现出来，其他的收起来
			}
			var hashvo=new ParameterSet();
			hashvo.setValue("fieldsetid",arr[i]);
			hashvo.setValue("selectlist",selectitems);
			var request=new Request({method:'post',asynchronous:false,onSuccess:addResult,functionId:'202003003310'},hashvo);
		}
	}
}
function jiliangouxuan(id){//点击勾选框时，判断整个信息集内指标有无勾选。如果一个也没有，则信息集不勾选，否则就勾选。
	if(id==0){
		var boxArray = document.getElementsByName('selectflag');
	}else{
		var boxArray = document.getElementsByName('selectflag'+id);
	}
	var selectitems="";
	for(var j=0;j<boxArray.length;j++){
		if(boxArray[j].type=='checkbox'&&boxArray[j].checked){
			selectitems+="`"+boxArray[j].value;//将被选中的指标组成一个新的list
		}
	}
	if(selectitems.indexOf("e01a1")!=-1){
		for(var i=0;i<boxArray.length;i++){
			if(boxArray[i].value=="b0110"||boxArray[i].value=="e0122"){
				boxArray[i].checked=true;
			}
		}
	}else if(selectitems.indexOf("e0122")!=-1){
		for(var i=0;i<boxArray.length;i++){
			if(boxArray[i].value=="b0110"){
				boxArray[i].checked=true;
			}
		}
	}
	if(id==0){
	}else{
		var total = 0;
		for(var i=0;i<boxArray.length;i++){
			if(boxArray[i].checked){
				total++;
			}
		}
	    if (total > 0) {  
	        var boxArrays = document.getElementsByName('selectflags'+id);
	        boxArrays[0].checked=true;
	    }else{
	    	var boxArrays = document.getElementsByName('selectflags'+id);
	        boxArrays[0].checked=false;
	    } 
	}
}
function addResult(outparameters){
	var fieldlist=outparameters.getValue("fieldlist");
	var selectlist=outparameters.getValue("selectlist");
	fieldsetdesc=outparameters.getValue("fieldsetdesc");
	brr[adds]=fieldsetdesc;
	var addhtml="";
	var bodyhtml="";
	var isupdate=$('isupdate');
	isupdate.checked=false;
	var fieldsetid=arr[addr];
	addhtml+="<table border='0' width='100%' cellspacing='0' cellpadding='0' align='center' class='ListTable2'>";
	if(adds==1){
		addhtml+="<tr><td align=center class='TableRow' style='border-left: none;border-top: none;' nowrap><input type=checkbox name=selbox onclick=batch_select(this,'selectflag'); title=<bean:message key='label.query.selectall' />>&nbsp;</td><td align=center class='TableRow' style='border-top: none;' nowrap width='11%'><bean:message key='train.job.num'/>&nbsp;</td><td align=center class='TableRow noright' style='border-top: none;' nowrap><bean:message key='train.job.target'/>&nbsp;</td></tr>";
	}

	if(fieldsetid.toUpperCase()!='R31'.toUpperCase()){
		addhtml+="<tr><td align=center class='TableRow' nowrap style='border-left: none;border-top: none; ' width=35 ><input type=checkbox id='selectflags"+adds+"' name='selectflags"+adds+"' onclick=batch_select(this,'selectflag"+adds+"'); title=<bean:message key='label.query.selectall' /> ";
		if(fieldlist!=null)
		for(var i=0;i<fieldlist.length;i++){
			var obj123=fieldlist[i];
			if(selectlist.indexOf(obj123.itemid)!=-1){//判断信息集内有无被勾选指标，只要有至少一个，则信息集勾选。
				addhtml+="checked='checked'";
				break;
			}
		}
		addhtml+=">&nbsp;</td><td colspan='2' class='TableRow noright' style='border-top: none;' nowrap >";
		addhtml+="<span style='float:left'><a align='center' href='###' onclick='showsetinfo("+adds+")' id='"+adds+"add'><img src='/images/tree_collapse.gif' border='0'>"+fieldsetdesc+"</a></span><span style='float:right'><a href='###' onclick='deletenews("+adds+")'>删除</a>&nbsp;&nbsp;&nbsp;</span></td></tr>";
		bodyhtml+="<tr><td colspan='2' class='RecordRow'><div id="+adds+" style=' display: block'><table style='width:100%;' border=0 cellspacing=0  align=center cellpadding=0 class='ListTable2' style=margin-top:0>";
		if(fieldlist!=null)
		for(var i=0;i<fieldlist.length;i++){
			mn++;
			var obj=fieldlist[i];
			if(i%2==0){
				bodyhtml+="<tr class='trShallow'>";
			}else{
				bodyhtml+="<tr class='trDeep'>";
			}
			bodyhtml+="<td align='center' class='RecordRow' width=35 style='border-left: none;border-top: none;' nowrap>";
			bodyhtml+="<input type='checkbox' name='selectflag"+adds+"' value='"+obj.itemid+"'" ;
			if(selectlist.indexOf(obj.itemid)!=-1 || obj.reserveitem==1){//判断初始有无选中
				bodyhtml+="checked='checked'";
			}
			if(obj.reserveitem==1)
				bodyhtml+="disabled='disabled'";
			bodyhtml+=" onclick='jiliangouxuan("+adds+")';>&nbsp;";
			bodyhtml+="</td>";
			bodyhtml+="<td align='left' class='RecordRow' style='word-break:break-all;border-top: none;' width=45 nowrap>&nbsp;"+(parseInt(mn))+"</td>";
			bodyhtml+="<td align='left' class='RecordRow noright' style='word-break:break-all;border-top: none;'  nowrap>&nbsp;"+obj.itemdesc+"</td>";
			bodyhtml+="</tr>";
		}
	}else{
		addhtml+="<tr><td align=center class='TableRow' nowrap style='border-left: none;border-top: none;' width=35 ><input type=checkbox id='selectflags"+adds+"' name='selectflags"+adds+"' onclick=batch_select(this,'selectflag"+adds+"'); title=<bean:message key='label.query.selectall' /> ";
		if(fieldlist!=null)
		for(var i=0;i<fieldlist.length;i++){
			var obj123=fieldlist[i];
			if(selectlist.indexOf(obj123.itemid)!=-1){//判断信息集内有无被勾选指标，只要有至少一个，则信息集勾选。
				addhtml+="checked='checked'";
				break;
			}
		}
		addhtml+=">&nbsp;</td><td colspan='2' class='TableRow noright' nowrap >";
		addhtml+="<span style='float:left'><a align='center' href='###' onclick='showsetinfo("+adds+")' id='"+adds+"add'><img src='/images/tree_collapse.gif' border='0'>"+fieldsetdesc+"</a></span><span style='float:right'><a href='###' onclick='deletenews("+adds+")'>删除</a>&nbsp;&nbsp;&nbsp;</span></td></tr>";
		bodyhtml+="<tr><td colspan='2' class='TableRow'><div id="+adds+" style=' display: block'><table style='width:100%;'";
		if(arr[addr]=='R31'){
			if(addx==adds){
				bodyhtml+="style='position:absolute'";
			}
		}
		bodyhtml+=" border=0 cellspacing=0  align=center cellpadding=0 class='ListTable2' style=margin-top:0>";
		if(fieldlist!=null)
		for(var i=0;i<fieldlist.length;i++){
			mn++;
			var obj=fieldlist[i];
			if(i%2==0){
				bodyhtml+="<tr class='trShallow'>";
			}else{
				bodyhtml+="<tr class='trDeep'>";
			}
			bodyhtml+="<td align='center' class='RecordRow' style='border-top:none;' width=35 style='border-left: none;' nowrap>";
			bodyhtml+="<input type='checkbox' name='selectflag"+adds+"' value='"+obj.itemid+"'" ;
			if(selectlist.indexOf(obj.itemid)!=-1 || obj.reserveitem==1){//判断初始有无选中
				bodyhtml+="checked='checked'";
			}
			if(obj.reserveitem==1)
				bodyhtml+="disabled='disabled'";
			bodyhtml+=" onclick='jiliangouxuan("+adds+")';>&nbsp;";
			bodyhtml+="</td>";
			bodyhtml+="<td align='left' class='RecordRow' style='word-break:break-all;border-top:none;' width=45 nowrap>&nbsp;"+(parseInt(mn))+"</td>";
			bodyhtml+="<td align='left' class='RecordRow noright' style='word-break:break-all;border-top:none;'  nowrap>&nbsp;"+obj.itemdesc+"</td></tr>";
		}
	}
	bodyhtml+="</table></div></td></tr>";
	addhtml+="</table>";
	var tarDiv=$("addtargetid");
	if(adds==1){
		tarDiv.innerHTML="";
	}
	tarDiv.innerHTML=tarDiv.innerHTML+addhtml+bodyhtml;
	/*
	alert(tarDiv.innerHTML);
	*/
}
function showsetinfo(id)
{
	if(id=='a'){    //展现多表
		pdym=1;
		var obj1 = document.getElementById("addtargetid");
		var obj2 = document.getElementById("targetid");
		obj1.style.display='block';
		obj2.style.display='none';
		for(var i=1;i<=addx;i++){        //只展开最后一个信息集
			if(i<addx){
				var obj3 = document.getElementById(i);
				var obj4=document.getElementById(i+"add");
				if(obj3!=null&&obj4!=null){
					obj3.style.display='none';
					obj4.innerHTML='<img src=\"/images/tree_expand.gif\" border=\"0\">'+brr[i];
				}
			}
			if(i==addx){
				var obj3 = document.getElementById(i);
				var obj4=document.getElementById(i+"add");
				if(obj3!=null&&obj4!=null){
					obj3.style.display='block';
					obj4.innerHTML='<img src=\"/images/tree_collapse.gif\" border=\"0\">'+brr[i];
				}
			}
		}
	}else if(id=='b'){    //展现单表
		pdym=2;
		var obj1 = document.getElementById("targetid");
		var obj2 = document.getElementById("addtargetid");
		obj1.style.display='block';
		obj2.style.display='none';
	}else{   //单个信息集的展现与收缩
		var obj = document.getElementById(id);
		var obj1=document.getElementById(id+"add");
		if(obj.style.display=='none'){
			obj.style.display='block';
			obj1.innerHTML='<img src=\"/images/tree_collapse.gif\" border=\"0\">'+brr[id];
		}else{
			obj.style.display='none';
			obj1.innerHTML='<img src=\"/images/tree_expand.gif\" border=\"0\">'+brr[id];
		}
	}
}
function changeInfor(){
	if(addx==0){
		showsetinfo("b");
	}
	var fieldsetid=$F("fieldsetid");
	var hashvo=new ParameterSet();
	hashvo.setValue("fieldsetid",fieldsetid);
	var request=new Request({method:'post',asynchronous:false,onSuccess:changeInforResult,functionId:'202003003310'},hashvo);
}
function changeInforResult(outparameters){
	var fieldlist=outparameters.getValue("fieldlist");
	var targethtml="";
	var isupdate=$('isupdate');
	isupdate.checked=false;
	var fieldsetid=$F("fieldsetid");
	targethtml+="<table style='width:100%;' style='position:absolute' border=0 cellspacing=0  align=center cellpadding=0 class=ListTable style=margin-top:0>";
	if(fieldsetid.toUpperCase()!='R31'.toUpperCase()){
		targethtml+="<thead><tr class=fixedHeaderTr><td align=center class='TableRow noright' style='border-top:none;border-left:none;' nowrap><input type=checkbox name=selbox onclick=batch_select(this,'selectflag'); title=<bean:message key='label.query.selectall' />>&nbsp;</td><td align=center class=TableRow style='border-top:none;' width='10%' nowrap><bean:message key='train.job.num'/>&nbsp;</td><td align=center class='TableRow noright' style='border-top:none;' nowrap><bean:message key='train.job.target'/>&nbsp;</td></tr></thead>";
		if(fieldlist!=null)
		for(var i=0;i<fieldlist.length;i++){
			var obj=fieldlist[i];
			if(i%2==0){
				targethtml+="<tr class='trShallow'>";
			}else{
				targethtml+="<tr class='trDeep'>";
			}
			
			targethtml+="<td align='center' class='RecordRow' style='border-left:none;border-top:none;' width=35  nowrap>";
			targethtml+="<input type='checkbox' name='selectflag' value='"+obj.itemid+"'";
			if(obj.reserveitem==1)
				targethtml+="checked='checked' disabled='disabled'";
			targethtml+=" onclick='jiliangouxuan(0)'>&nbsp;";
			targethtml+="</td>";
			targethtml+="<td align='left' class='RecordRow' style='word-break:break-all;border-top:none;' width=45 nowrap>&nbsp;"+(parseInt(i)+1)+"</td>";
			targethtml+="<td align='left' class='RecordRow noright' style='word-break:break-all;border-top:none;'  nowrap>&nbsp;"+obj.itemdesc+"</td>";
			targethtml+="</tr>";
		}
	}else{
		targethtml+="<thead><tr class=fixedHeaderTr><td align=center class=TableRow style='border-top:none;border-left:none;' nowrap><input type=checkbox name=selbox onclick=batch_select(this,'selectflag'); title=<bean:message key='label.query.selectall' />>&nbsp;</td><td align=center class=TableRow style='border-top:none;' width='10%' nowrap><bean:message key='train.job.num'/>&nbsp;</td><td align=center class='TableRow noright' style='border-top:none;' nowrap><bean:message key='train.job.target'/>&nbsp;</td></tr></thead>";
		if(fieldlist!=null)
		for(var i=0;i<fieldlist.length;i++){
			var obj=fieldlist[i];
			if(i%2==0){
				targethtml+="<tr class='trShallow'>";
			}else{
				targethtml+="<tr class='trDeep'>";
			}
			targethtml+="<td align='center' class='RecordRow' width=35 style='border-left:none;border-top:none;' nowrap>";
			targethtml+="<input type='checkbox' name='selectflag' value='"+obj.itemid+"'";
			if(obj.reserveitem==1)
				targethtml+="checked='checked' disabled='disabled'";
			targethtml+=" onclick='jiliangouxuan(0)'>&nbsp;";
			targethtml+="</td>";
			targethtml+="<td align='left' class='RecordRow' style='word-break:break-all;border-top:none;' width=45 nowrap>&nbsp;"+(parseInt(i)+1)+"</td>";
			targethtml+="<td align='left' class='RecordRow noright' style='word-break:break-all;border-top:none;'  nowrap>&nbsp;"+obj.itemdesc+"</td></tr>";
		}
	}
	targethtml+="</table>";
	//alert(targethtml);
	var tarDiv=$("targetid");
	tarDiv.innerHTML="";
	tarDiv.innerHTML=targethtml;
}
function loaddown(student){
	
	if(pdym==1){
		var obj=new Object();
		var fieldsetid="";
		obj.fieldsetid=fieldsetid;
		var selectitems="";
		for(var i=1;i<=adds;i++){
			arr =idlist.split(",");
			var selectlength=0;
			var selects=document.getElementsByName('selectflag'+i);
			for(var j=0;j<selects.length;j++){
				if(selects[j].type=='checkbox'&&selects[j].checked){
					selectlength++;
					if(selectlength==1){
						selectitems+="#"+arr[i]+":";
					}
					selectitems+="`"+selects[j].value;
				}
			}
			if(arr[i]=='R31'){
				if(selectlength>255){
					alert(TRAIN_CALSS_SELECTMOREITEMS);
					return false;
				}
			}else{
				if(selectlength>254){
					alert(TRAIN_CALSS_SELECTMOREITEMS);
					return false;
				}
			}
		}
		obj.selectitems=selectitems;
		if(obj.selectitems.length>0){
	        downLoadTemp(obj);
	    }else{
	        alert(TRAIN_CALSS_SELECTITEMS);
	    }
	}
	if(pdym==2){
		var obj=new Object();
		var fieldsetid=$F("fieldsetid");
		obj.fieldsetid=fieldsetid;
		var selects=document.getElementsByName('selectflag');
		var selectitems="";
		for(var i=0;i<selects.length;i++){
			if(selects[i].type=='checkbox'&&selects[i].checked){
				selectitems+="`"+selects[i].value;
			}
		}
		obj.selectitems=selectitems;
		obj.codeid=codeid;
		if(obj.selectitems.length>0){
			if(student=="student"){
				var onlyld = document.getElementById('onlyitem');
				var codeid = "";
				if(onlyld.checked==true){
					codeid=$F("codeid");
					if(codeid=='请选择'){
						codeid="";
					}
				}else{
					codeid="";
				}
				obj.codeid=codeid;
				outTemplete(obj);
			}else
	        	downLoadTemp(obj);
	    }else{
	        alert(TRAIN_CALSS_SELECTITEMS);
	    }
	}
}
function outTemplete(obj){
	var hashvo=new ParameterSet();	
	hashvo.setValue("model","1");
	hashvo.setValue("r3101",'${courseTrainForm.r3101}');
	hashvo.setValue("obj",obj);
	var request=new Request({method:'post',asynchronous:false,onSuccess:showfile,functionId:'2020040019'},hashvo);
}
//导出摸板
function downLoadTemp(obj)
{	
	
	var hashvo=new ParameterSet();	
	hashvo.setValue("obj",obj);
	var request=new Request({method:'post',asynchronous:false,onSuccess:showfile,functionId:'202003003311'},hashvo);
}
function showfile(outparamters)
{
	var outName=outparamters.getValue("outName");
	var student=outparamters.getValue("student");
	student=getDecodeStr(student);
	if(student=="student") {
		window.returnValue = outName;
		top.close();
	} else {
		window.location.target="_blank";
		window.location.href = "/servlet/vfsservlet?fromjavafolder=true&fileid="+outName;
		($('b_loaddown')).disabled=false;
	}
}

function returnback(){
	courseTrainForm.action="/train/request/trainsData.do?b_query=link&model=1&a_code=${courseTrainForm.a_code}";
	courseTrainForm.submit();
}
function onchangeid(){
	var onlyld = document.getElementById('onlyitem');
	if(onlyld.checked==true){
		Element.hide('cid2');
		Element.show('cid1');
	}else{
		Element.hide('cid1');
		Element.show('cid2');
	}
}
function wclose(){
	
	window.close();
}
function diaplay(student){
	if(student=="student")
		document.getElementById('studentid').style.display="none";
}
</script>
<hrms:themes></hrms:themes>
<html:form action="/train/request/trainsData" method="post">
<bean:define id="student" name="courseTrainForm" property="student"/>
<table border="0" cellspacing="0"  align="center" cellpadding="0">
<tr>
	<td align="left" nowrap="nowrap">
		<div id="studentid" style="padding-top: 10px;padding-bottom: 10px;">
		<br>
		<bean:message key="workbench.info.batchinout.lebal"/>
		<hrms:optioncollection name="courseTrainForm" property="fieldSetDataList" collection="list" />
		<html:select name="courseTrainForm" property="fieldsetid" indexed="fieldsetid" onchange="changeInfor();">
			<html:options collection="list" property="dataValue" labelProperty="dataName"/>
		</html:select>
		<a href="###" onclick="addnews();"><img src="/images/add.gif" border=0></a>
		</div>
	</td>
	<td align="left" nowrap>
	</td>
	<td align="right"><div id="updatecheck" style="display: none"><input type="checkbox" id="isupdate" onclick="changeInfor();" />&nbsp;<bean:message key="workbench.info.recordupdate.lebal"/></div></td>
</tr>
<tr valign="top" style="width:auto">
<td colspan="2" valign="top" abbr="left" style="width:auto">
<div   class="fixedDiv6" id="targetid" style="display: block"> 
</div>
<div   class="fixedDiv6" id="addtargetid" style="display: none"> 
</div>
</td>
</tr>
<logic:equal name="courseTrainForm" property="student" value="student">
<tr><td>&nbsp;</td></tr>
<tr>
	<td nowrap>
		<table>
      		<tr>
	      		<td style="padding-left: 6px;">
					<input type="checkbox" id="onlyitem" value="1" onclick="onchangeid();">
					<bean:message key="workbench.info.batchinout.only.lebal"/>&nbsp;
				</td>
				<td>
					<div id="cid1">
				    	<html:select name="courseTrainForm" property="codeid" size="0">
				    		<html:optionsCollection property="fielditemlist" value="dataValue" label="dataName" /> 
				        </html:select>
			        </div>
			        <div id="cid2">
				    	<select>
							<option><bean:message key="label.select"/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</option>
				        </select>
			        </div>   
				</td>
				<td>
			    </td>
		    </tr>
      	</table>
    </td>
</tr>
</logic:equal>
<logic:notEqual name="courseTrainForm" property="student" value="student">
<input type="hidden" id="onlyitem"/>
</logic:notEqual>
</table>
	<table width="100%" border="0" cellspacing="0"  align="center" cellpadding="0">
		<tr height="10"><td>&nbsp;</td></tr>
		<tr>
	        <td align="center"  nowrap>
	        	<logic:notEqual name="courseTrainForm" property="student" value="student">
		        	 &nbsp;<html:button property="b_loaddown" styleClass="mybutton" onclick="loaddown('${student}');">&nbsp;<bean:message key='lable.loaddown'/>&nbsp;</html:button>
		        	 &nbsp;<html:button property="b_close" styleClass="mybutton" onclick="returnback();">&nbsp;<bean:message key='button.return'/>&nbsp;</html:button>
	        	</logic:notEqual>
	        	<logic:equal name="courseTrainForm" property="student" value="student">
	        		 &nbsp;<html:button property="b_loaddown" styleClass="mybutton" onclick="loaddown('${student}');">&nbsp;<bean:message key='lable.loaddown'/>&nbsp;</html:button>
		        	 &nbsp;<html:button property="b_close" styleClass="mybutton" onclick="wclose();">&nbsp;<bean:message key='button.cancel'/>&nbsp;</html:button>
	        	</logic:equal>
	        </td>
	   </tr>
	</table>

</html:form>
<script type="text/javascript">
<!--
	changeInfor();
    onchangeid();
    diaplay("${student}");
//-->
</script>
