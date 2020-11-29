<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ page import="com.hrms.struts.valueobject.UserView"%>
<%@ page import="com.hrms.struts.constant.WebConstant"%>
<%
	UserView userView=(UserView)session.getAttribute(WebConstant.userView);
	String bosflag="";
	if(userView!=null){
		bosflag = userView.getBosflag();
	}
%>
<%
	int i=0;
%>

<style id=iframeCss>
	div{
		font-size:12px;
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
		overflow-y:auto;
	}
	.trShallow {
		BORDER-RIGHT: medium none;
	}

	.trDeep {
		BORDER-RIGHT: medium none;
	}
	.ListTable2 {
		border:1px solid #C4D8EE;
		border-spacing: 0;
		border-collapse: unset !important;
		BORDER-BOTTOM: medium none;
		BORDER-LEFT: medium none;
		BORDER-TOP: medium none;
		BORDER-RIGHT: medium none;
	}
	.RecordRowx {
		position:relative;
		border: inset 1px #C4D8EE;
		BORDER-BOTTOM: #C4D8EE 1pt solid;
		BORDER-LEFT: medium none;
		BORDER-RIGHT: #C4D8EE 1pt solid;
		BORDER-TOP: #C4D8EE 1pt solid;
		font-size: 12px;
		height:22;
		font-weight: bold;
		background-color:#f4f7f7;
		valign:middle;
		border-collapse:collapse;
	}
	.RecordRowNum{

		BORDER-BOTTOM: #C4D8EE 1pt solid;
		font-size: 12px;
		height:22px;
		padding:0 5px 0 5px;
	}
	.RecordRowy {
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
	.fixedHeaderTr
	{
		position:relative;
		top:expression(this.offsetParent.scrollTop);
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

	function deletenews(did){ /*删除*/
		addr=0;
		adds=0;
		mn=0;
		addx--;
		arr =idlist.split(",");
		idlist=",";
		var fid = arr[did];
		var selectitems="";
		var seconditems="";
		for(var i=1;i<arr.length-1;i++){
			if(fid!=arr[i]){
				var selects=document.getElementsByName('selectflag'+i);
				for(var j=0;j<selects.length;j++){
					if(selects[j].type=='checkbox'&&selects[j].checked){
						selectitems+="`"+selects[j].value;//将被选中的指标组成一个新的list
					}
				}
				var secondselects=document.getElementsByName('seconditem'+i);
				for(var j=0;j<secondselects.length;j++){
					if(secondselects[j].type=='checkbox'&&secondselects[j].checked){
						seconditems+="`"+secondselects[j].value;
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
				hashvo.setValue("secondlist",seconditems);
				var request=new Request({method:'post',asynchronous:false,onSuccess:addResult,functionId:'0201001088'},hashvo);
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
			alert("信息集已存在！");
		}else{
			addx++;
			addr=0;
			adds=0;
			idlist = idlist+fieldsetid+",";
			arr =idlist.split(",");
			arrlen=arr.length-1;
			var selectitems="";
			var seconditems="";
			for(var i=1;i<arrlen;i++){
				var selects=document.getElementsByName('selectflag'+i);
				for(var j=0;j<selects.length;j++){
					if(selects[j].type=='checkbox'&&selects[j].checked){
						selectitems+="`"+selects[j].value;//将被选中的指标组成一个新的list
					}
				}
				var secondselects=document.getElementsByName('seconditem'+i);
				for(var j=0;j<secondselects.length;j++){
					if(secondselects[j].type=='checkbox'&&secondselects[j].checked){
						seconditems+="`"+secondselects[j].value;
					}
				}
			}
			for(var i=1;i<arrlen;i++){
				addr++;
				adds++;
				var hashvo=new ParameterSet();
				hashvo.setValue("fieldsetid",arr[i]);
				hashvo.setValue("selectlist",selectitems);
				hashvo.setValue("secondlist",seconditems);
				var request=new Request({method:'post',asynchronous:false,onSuccess:addResult,functionId:'0201001088'},hashvo);
			}
			if(addr==arrlen-1){
				showsetinfo("a");   //使最后一个子集展现出来，其他的收起来
			}
			if(!getBrowseVersion() || !isCompatibleIE()){//兼容非IE浏览器样式 修改   wangb  20180206  bug 34447
				var addtargetid = document.getElementById('addtargetid'); //table 高度调整
				addtargetid.style.height = (document.body.clientHeight-200) + 'px';
				addtargetid.style.position = "relative";
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
		var secondlist=outparameters.getValue("secondlist");
		fieldsetdesc=outparameters.getValue("fieldsetdesc");
		brr[adds]=fieldsetdesc;
		var addhtml="";
		var bodyhtml="";
		var isupdate=$('isupdate');
		isupdate.checked=false;
		var fieldsetid=arr[addr];
		addhtml+="<table border='0' width='100%' cellspacing='0' cellpadding='0' align='center'>";
		if(adds==1){
			addhtml+="<tr class='fixedHeaderTr'><td align=center class='TableRow_top' nowrap ><input type=checkbox name=selbox onclick=batch_select(this,'selectflag'); title=<bean:message key='label.query.selectall' /> width=35></td><td align=center class='TableRow_left' nowrap width=45><bean:message key='label.query.number'/></td><td align=center class='TableRow_left' nowrap><bean:message key='kq.wizard.target'/></td><td align=center class='TableRow_left' nowrap width=90><bean:message key='workbench.info.seconditem.lebal' /></td></tr>";
		} else
			addhtml = "<tr><td>" + addhtml;
		//targethtml+="<thead><tr class=fixedHeaderTr><td align=center class=TableRow nowrap><input type=checkbox name=selbox onclick=batch_select(this,'selectflag'); title=<bean:message key='label.query.selectall' />  width=35></td><td align=center class=TableRow width=45 nowrap><bean:message key='train.evaluationStencil.no'/></td><td align=center class=TableRow nowrap><bean:message key='kq.wizard.target'/></td><td align=center class=TableRow nowrap width=90><bean:message key='workbench.info.seconditem.lebal' /></td></tr></thead>";
		if(fieldsetid.toUpperCase()!='A01'.toUpperCase()){
			addhtml+="<tr><td align=center class='RecordRow_right' nowrap  width=35 ><input type=checkbox id='selectflags"+adds+"' name='selectflags"+adds+"' onclick=batch_select(this,'selectflag"+adds+"'); title=<bean:message key='label.query.selectall' /> ";
			if(fieldlist!=null)
				for(var i=0;i<fieldlist.length;i++){
					var obj123=fieldlist[i];
					if(selectlist.indexOf(obj123.itemid)!=-1){//判断信息集内有无被勾选指标，只要有至少一个，则信息集勾选。
						addhtml+="checked='checked'";
						break;
					}
				}
			addhtml+="></td><td colspan='3' class='RecordRow_left' nowrap >";
			addhtml+="<span style='float:left'><a align='center' href='###' onclick='showsetinfo("+adds+")' id='"+adds+"add'><img src='/images/tree_collapse.gif' border='0'>"+fieldsetdesc+"</a></span><span style='float:right'><a href='###' onclick='deletenews("+adds+")'>删除</a></span></td></tr>";
			bodyhtml+="<tr><td colspan='4' class='RecordRowNum'><div id="+adds+" style=' display: block'><table style='width:100%;' border=0 cellspacing=0  align=center cellpadding=0 class='ListTable2' style=margin-top:0>";
			if(fieldlist!=null)
				for(var i=0;i<fieldlist.length;i++){
					mn++;
					var obj=fieldlist[i];
					if(i%2==0){
						bodyhtml+="<tr class='trShallow'>";
					}else{
						bodyhtml+="<tr class='trDeep'>";
					}
					bodyhtml+="<td align='center' class='RecordRow_right' width=35  nowrap>";
					bodyhtml+="<input type='checkbox' name='selectflag"+adds+"' value='"+obj.itemid+"'" ;
					if(selectlist.indexOf(obj.itemid)!=-1){//判断初始有无选中
						bodyhtml+="checked='checked'";
					}
					bodyhtml+=" onclick='jiliangouxuan("+adds+")';>";
					bodyhtml+="</td>";
					bodyhtml+="<td align='left' class='RecordRowTop0' style='word-break:break-all;' width=45 nowrap>"+(parseInt(mn))+"</td>";
					bodyhtml+="<td align='left' class='RecordRowTop0' style='word-break:break-all;'  nowrap>"+obj.itemdesc+"</td>";
					bodyhtml+="<td align='center' class='RecordRow_left' width=90  nowrap>";
					bodyhtml+="<input type='checkbox' name='seconditem"+adds+"' value='"+obj.itemid+"'";
					if(secondlist.indexOf(obj.itemid)!=-1){//判断初始有无选中
						bodyhtml+="checked='checked'";
					}
					bodyhtml+="></td></tr>";
				}
		}else{
			addhtml+="<tr><td align=center class='RecordRow_right' nowrap  width=35 ><input type=checkbox id='selectflags"+adds+"' name='selectflags"+adds+"' onclick=batch_select(this,'selectflag"+adds+"'); title=<bean:message key='label.query.selectall' /> ";
			if(fieldlist!=null)
				for(var i=0;i<fieldlist.length;i++){
					var obj123=fieldlist[i];
					if(selectlist.indexOf(obj123.itemid)!=-1){//判断信息集内有无被勾选指标，只要有至少一个，则信息集勾选。
						addhtml+="checked='checked'";
						break;
					}
				}
			addhtml+="></td><td colspan='3' class='RecordRow_left' nowrap >";
			addhtml+="<span style='float:left'><a align='center' href='###' onclick='showsetinfo("+adds+")' id='"+adds+"add'><img src='/images/tree_collapse.gif' border='0'>"+fieldsetdesc+"</a></span><span style='float:right'><a href='###' onclick='deletenews("+adds+")'>删除</a></span></td></tr>";
			bodyhtml+="<tr><td colspan='3' class='RecordRowNum'><div id="+adds+" style=' display: block'><table style='width:100%;' border=0 cellspacing=0  align=center cellpadding=0 class='ListTable2' style=margin-top:0>";
			if(fieldlist!=null)
				for(var i=0;i<fieldlist.length;i++){
					mn++;
					var obj=fieldlist[i];
					if(i%2==0){
						bodyhtml+="<tr class='trShallow'>";
					}else{
						bodyhtml+="<tr class='trDeep'>";
					}
					bodyhtml+="<td align='center' class='RecordRow_right' width=35  nowrap>";
					bodyhtml+="<input type='checkbox' name='selectflag"+adds+"' value='"+obj.itemid+"'" ;
					if(selectlist.indexOf(obj.itemid)!=-1){//判断初始有无选中
						bodyhtml+="checked='checked'";
					}
					bodyhtml+=" onclick='jiliangouxuan("+adds+")';>";
					bodyhtml+="</td>";
					bodyhtml+="<td align='left' class='RecordRowTop0' style='word-break:break-all;' width=45 nowrap>"+(parseInt(mn))+"</td>";
					bodyhtml+="<td align='left' class='RecordRow_left' style='word-break:break-all;'  nowrap>"+obj.itemdesc+"</td></tr>";
				}
		}
		bodyhtml+="</table></div></td></tr>";
		addhtml+="</table></td></tr>";
		var tarDiv=$("addtargetid");
		if(adds==1){
			tarDiv.innerHTML="";
		}
		tarDiv.innerHTML=tarDiv.innerHTML+addhtml+bodyhtml;
	}
	function showsetinfo(id)
	{
		if(id=='a'){    //展现多表
			pdym=1;
			var obj1 = document.getElementById("addtargetid");
			var obj2 = document.getElementById("targetid");
			obj2.style.display='none';
			obj1.style.display='block';
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
			obj2.style.display='none';
			obj1.style.display='block';
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
		var request=new Request({method:'post',asynchronous:false,onSuccess:changeInforResult,functionId:'0201001088'},hashvo);
	}
	function changeInforResult(outparameters){
		var fieldlist=outparameters.getValue("fieldlist");
		var targethtml="";
		var isupdate=$('isupdate');
		isupdate.checked=false;
		var fieldsetid=$F("fieldsetid");
		targethtml+="<table style='width:100%;' style='position:absolute' border=0 cellspacing=0  align=center cellpadding=0 class=ListTable2 style=margin-top:0>";
		if(fieldsetid.toUpperCase()!='A01'.toUpperCase()){
			targethtml+="<thead><tr class=fixedHeaderTr><td align=center class=TableRow_top nowrap><input type=checkbox name=selbox onclick=batch_select(this,'selectflag'); title=<bean:message key='label.query.selectall' />  width=35></td><td align=center class=TableRow_left width=45 nowrap><bean:message key='train.evaluationStencil.no'/></td><td align=center class=TableRow_left nowrap><bean:message key='kq.wizard.target'/></td><td align=center class=TableRow_left nowrap width=90><bean:message key='workbench.info.seconditem.lebal' /></td></tr></thead>";
			if(fieldlist!=null)
				for(var i=0;i<fieldlist.length;i++){
					var obj=fieldlist[i];
					if(i%2==0){
						targethtml+="<tr class='trShallow'>";
					}else{
						targethtml+="<tr class='trDeep'>";
					}

					targethtml+="<td align='center' class='RecordRow_right' width=35  nowrap>";
					targethtml+="<input type='checkbox' name='selectflag' value='"+obj.itemid+"'";
					targethtml+=" onclick='jiliangouxuan(0)';>";
					targethtml+="</td>";
					targethtml+="<td align='left' class='RecordRowNum' style='word-break:break-all;' width=45 nowrap>"+(parseInt(i)+1)+"</td>";
					targethtml+="<td align='left' class='RecordRowNum' style='word-break:break-all;'  nowrap>"+obj.itemdesc+"</td>";
					targethtml+="<td align='center' class='RecordRow_left' width=90  nowrap>";
					targethtml+="<input type='checkbox' name='seconditem' value='"+obj.itemid+"'>";
					targethtml+="</td></tr>";
				}
		}else{
			targethtml+="<thead><tr class=fixedHeaderTr><td align=center class=TableRow_top nowrap><input type=checkbox name=selbox onclick=batch_select(this,'selectflag'); title=<bean:message key='label.query.selectall' />  width=35></td><td align=center class=TableRow_left width=45 nowrap><bean:message key='train.evaluationStencil.no'/></td><td align=center class=TableRow_left nowrap><bean:message key='kq.wizard.target'/></td></tr></thead>";
			if(fieldlist!=null)
				for(var i=0;i<fieldlist.length;i++){
					var obj=fieldlist[i];
					if(i%2==0){
						targethtml+="<tr class='trShallow'>";
					}else{
						targethtml+="<tr class='trDeep'>";
					}
					targethtml+="<td align='center' class='RecordRow_right' width=35  nowrap>";
					targethtml+="<input type='checkbox' name='selectflag' value='"+obj.itemid+"'";
					targethtml+=" onclick='jiliangouxuan(0)';>";
					targethtml+="</td>";
					targethtml+="<td align='left' class='RecordRowNum' style='word-break:break-all;' width=45 nowrap>"+(parseInt(i)+1)+"</td>";
					targethtml+="<td align='left' class='RecordRow_left' style='word-break:break-all;'  nowrap>"+obj.itemdesc+"</td></tr>";
				}
		}
		targethtml+="</table>";
		var tarDiv=$("targetid");
		tarDiv.style.height="0px";
		if('block' == tarDiv.style.display) {
			tarDiv.innerHTML="";
			tarDiv.innerHTML=targethtml;
			tarDiv.style.height= (document.body.clientHeight-200) + "px";
		}
	}
	function loaddown(){
		//zhangh 2019-11-20 获取客户选择的office版本
		var officeType = $F("office")[0];
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
		if(pdym==1){
			var obj=new Object();
			//设置office版本
			obj.officeType = officeType;
			var fieldsetid="";
			obj.fieldsetid=fieldsetid;
			var selectitems="";
			var seconditems="";//次关联指标
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
				if(arr[i]=='A01'){
					if(selectlength>255){
						alert('信息集勾选的指标超过Excel的列数，请分多次下载!');
						return false;
					}
				}else{
					if(selectlength>253){
						alert('信息集勾选的指标超过Excel的列数，请分多次下载!');
						return false;
					}
				}
				var secondselects=document.getElementsByName('seconditem'+i);
				for(var j=0;j<secondselects.length;j++){
					if(secondselects[j].type=='checkbox'&&secondselects[j].checked){
						seconditems+="`"+secondselects[j].value;
					}
				}

			}
			obj.selectitems=selectitems;
			obj.seconditems=seconditems;
			obj.codeid=codeid;
			if(obj.selectitems.length>0){
				downLoadTemp(obj);
			}else{
				alert('请选择指标，再下载模板！');
			}
		}
		if(pdym==2){
			var obj=new Object();
			//设置office版本
			obj.officeType = officeType;
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
			var seconditems="";//次关联指标
			var secondselects=document.getElementsByName('seconditem');
			for(var i=0;i<secondselects.length;i++){
				if(secondselects[i].type=='checkbox'&&secondselects[i].checked){
					seconditems+="`"+secondselects[i].value;
				}
			}
			obj.seconditems=seconditems;
			obj.codeid=codeid;
			if(obj.selectitems.length>0){
				downLoadTemp(obj);
			}else{
				alert('请选择指标，再下载模板！');
			}
		}
	}
	function fileup(){
		selfInfoForm.action="/workbench/info/showinfodata.do?b_selectfile=link"//&fieldsetid="+obj.fieldsetid+"&isupdate="+obj.isupdate;
		selfInfoForm.submit();
	}

	//导出摸板
	function downLoadTemp(obj){
		var hashvo=new ParameterSet();
		hashvo.setValue("obj",obj);
		var request=new Request({method:'post',asynchronous:false,onSuccess:showfile,functionId:'0201001089'},hashvo);
	}
	function showfile(outparamters)
	{
		var outName=outparamters.getValue("outName");
		window.location.target="_blank";
		window.location.href = "/servlet/vfsservlet?fromjavafolder=true&fileid="+outName;
		($('b_loaddown')).disabled=false;
	}

	function returnback(){
		selfInfoForm.action="/workbench/info/showinfodata.do?b_searchinfo=link&code=${selfInfoForm.code}&kind=${selfInfoForm.kind}&query=1&isAdvance=0";
		selfInfoForm.submit();
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
</script>
<hrms:themes />
<style>
	.TableRow_left{
		BORDER-TOP:0pt solid;
	}
	.RecordRow_left{
		BORDER-TOP:0pt solid;
	}
	.RecordRow_right{
		BORDER-TOP:0pt solid;
	}
</style>
<html:form action="/workbench/info/showinfodata" method="post">
	<%if("hcm".equals(bosflag)){ %>
	<table border="0"   cellspacing="0"  align="center" cellpadding="0" width="450">
	<%}else{ %>
	<table border="0"   cellspacing="0"  align="center" cellpadding="0" width="450" style="margin-top: 10px">
		<%} %>
		<tr>
			<td>
				<font-size=3><strong>注意：</strong></font-size>如需设置子集关联指标，必须在下载模板前指定，导入时无需再指定。<br/>
			</td>
		</tr>
		<tr height="5"><td></td></tr>
		<tr>
			<td align="left" nowrap="nowrap"><bean:message key="workbench.info.batchinout.lebal"/>
				<hrms:optioncollection name="selfInfoForm" property="fieldSetDataList" collection="list" />
				<html:select name="selfInfoForm" property="fieldsetid" indexed="fieldsetid" onchange="changeInfor();">
					<html:options collection="list" property="dataValue" labelProperty="dataName"/>
				</html:select>
				<a href="###" onclick="addnews();"><img src="/images/add.gif" border=0></a>
			</td>
			<td align="left" nowrap>
			</td>
		</tr>
		<tr height="5"><td></td></tr>
		<tr valign="top" style="width:auto;height:expression(document.body.clientHeight-200);">
			<td colspan="2" valign="top" abbr="left" style="width:auto;">
				<div   class="fixedDiv6" id="targetid" style="display: block">
				</div>
				<div   class="fixedDiv6" id="addtargetid" style="display: none;position:absolute;">
				</div>
			</td>
		</tr>
		<tr height="5"><td></td></tr>
		<tr>
			<td nowrap>
				<table>
					<tr>
						<td>
							<input type="checkbox" id="onlyitem" value="1" onclick="onchangeid();">
							<html:hidden name="selfInfoForm" property="lebal" styleId="text"/>
							<bean:message key="workbench.info.batchinout.only.lebal"/>
						</td>
						<td>
							<div id="cid1">
								<html:select name="selfInfoForm" property="codeid" size="0">
									<html:optionsCollection property="fielditemlist" value="dataValue" label="dataName" />
								</html:select>
							</div>
							<div id="cid2">
								<select>
									<option>请选择&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</option>
								</select>
							</div>
						</td>
						<td>
						</td>
					</tr>
					<!-- 2019-11-20 zhangh 为了兼容不同版本的office，需要在界面上选择以下版本start-->
					<tr>
						<td colspan="2">
							<label><input name="office" type="radio" value="1" checked/>Office2007以上版本 </label>
							<label><input name="office" type="radio" value="2" />Office2007及以下版本</label>
						</td>
						<td>
						</td>
					</tr>
					<!-- 2019-11-20 zhangh 为了兼容不同版本的office，需要在界面上选择以下版本end-->
				</table>
			</td>
		</tr>
		<tr height="3"><td></td></tr>
		<tr>
			<td align="center"  nowrap>
				<hrms:priv func_id="2606051,03040141,030401C1" module_id="">
					<html:button property="b_loaddown" styleClass="mybutton" onclick="loaddown();">&nbsp;<bean:message key='lable.loaddown'/>&nbsp;</html:button>
				</hrms:priv>
				<hrms:priv func_id="2606052,03040142,030401C2" module_id="">
					<html:button property="b_fileup" styleClass="mybutton" onclick="return fileup();">&nbsp;<bean:message key='menu.gz.import'/>&nbsp;</html:button>
				</hrms:priv>
				<html:button property="b_close" styleClass="mybutton" onclick="returnback();">&nbsp;<bean:message key='button.return'/>&nbsp;</html:button>
			</td>
		</tr>
	</table>
</html:form>
<script type="text/javascript">
	<!--
	changeInfor();
	onchangeid();
	//-->
	if(!getBrowseVersion() || !isCompatibleIE()){//兼容非IE浏览器样式 修改   wangb  20180206  bug 34447
		var targetid = document.getElementById('targetid'); //table 高度调整
		targetid.style.height = (document.body.clientHeight-200) + 'px';
	}
</script>
