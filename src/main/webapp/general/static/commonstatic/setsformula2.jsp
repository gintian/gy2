<%@ page contentType="text/html; charset=UTF-8" language="java"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean"%>
<%@ taglib uri="/tags/struts-html" prefix="html"%>
<%@ taglib uri="/tags/struts-logic" prefix="logic"%>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms"%>
<%@ page import="com.hrms.struts.valueobject.UserView"%>
<%@ page import="com.hrms.struts.constant.WebConstant"%>
<%@ page import="com.hjsj.hrms.actionform.stat.InfoSetupForm"%>
<%
	// 在标题栏显示当前用户和日期 2004-5-10 
	String css_url = "/css/css1.css";
	UserView userView = (UserView) session
			.getAttribute(WebConstant.userView);
	if (userView != null) {
		css_url = userView.getCssurl();
		if (css_url == null || css_url.equals(""))
			css_url = "/css/css1.css";
		//out.println("<link href='"+css_url+"' rel='stylesheet' type='text/css' >");  	 
	}
	String bosflag="";
	if(userView != null)
	{
		bosflag=userView.getBosflag();
	}
	String infokind = request.getParameter("infokind");
%>
<script language="JavaScript" src="/js/function.js"></script>
<script type="text/javascript">
<!--

function getSbase(){
	var sbases = document.getElementsByName("sbase");
	var sbase="";
	for(var i=0;i<sbases.length;i++){
		if(sbases[i].checked){
			sbase+=","+sbases[i].value;
		}
	}
	if(sbase.length>1)
		sbase=sbase.substring(1);
	return sbase;
}

function function_Wizard(busi,formula){
    var thecodeurl ="/org/autostatic/mainp/function_Wizard.do?b_query=link&infor=<%=infokind %>&busi="+busi; 
    var return_vo= window.showModalDialog(thecodeurl, "", 
              "dialogWidth:400px; dialogHeight:430px;resizable:no;center:yes;scroll:yes;status:no");
    if(return_vo!=null)
		symbol(formula,return_vo);
}



function symbol(editor,strexpr){
	var expr_editor=document.getElementById(editor);
	expr_editor.focus();
	var element = document.selection;
	if (element!=null) {
		var rge = element.createRange();
		if (rge!=null)	
		rge.text=strexpr;
	}else{
		var word = expr_editor.value;
				var _length=strexpr.length;
				var startP = expr_editor.selectionStart;
				var endP = expr_editor.selectionEnd;
				var ddd=word.substring(0,startP)+strexpr+word.substring(endP);
		    	expr_editor.value=ddd;
        		expr_editor.setSelectionRange(startP+_length,startP+_length);
	}
}

function toggles(targetId){
	if (document.getElementById(targetId)){
		target = document.getElementById(targetId);
		target.style.display = "block";
	}
} 
function hides(targetId){
	if (document.getElementById(targetId)){
		target = document.getElementById(targetId);
		target.style.display = "none";
	}
}
function changeSetid(){
    var fieldsetid=document.getElementById("setid").value;
	var in_paramters="fieldsetid="+fieldsetid;
    var request=new Request({method:'post',asynchronous:false,parameters:in_paramters,onSuccess:showFieldSetList,functionId:'3020050011'});
}
function changeRun(runvalue,itemid,itemname){
	var runflag = runvalue.value;
	if(runflag==1){
		var standid = selectStandard(itemname);
		standid=standid!=null?standid:"";
		linkIframe(standid);
	}else if(runflag==2){
		var taxid = selectScale();
		if(taxid!=null&&taxid.length>0){
			linkScale(taxid);
		}else{
			runvalue.value=0;
			runflag=0;
		}
	}else{
		changebox(itemid,runflag,itemname);
	}
	runFlagCheck(runflag);
}
function changebox(checkvalue,runf,itemname){
	var useflag = '0';
	var usef = document.getElementById(checkvalue);
	if(usef!=null&&usef.length>0){
		if(usef.checked){
			useflag='1';
		}else{
			useflag='0';
		}
	}else{
		useflag = '';
	}
	if(checkvalue==null&&checkvalue.length<1){
		checkvalue="";	
	}
	document.getElementById("item").value=checkvalue;
	itemname=itemname!=null?itemname:'';
	document.getElementById("itemname").value=itemname;
	var salaryid = document.getElementById("salaryid").value
	var hashvo=new ParameterSet();
	hashvo.setValue("salaryid",salaryid);
	hashvo.setValue("itemid",checkvalue);
	hashvo.setValue("useflag",useflag);
	hashvo.setValue("runflag",runf);
	
	
	var request=new Request({asynchronous:false,onSuccess:getformulavalue,functionId:'3020060002'},hashvo);
	checkvalue=checkvalue+"";
	if(checkvalue!=null&&checkvalue.length>0){
		tr_bgcolor(checkvalue);
	}
}
function getformulavalue(outparamters){
	var formulavalue= outparamters.getValue("formulavalue");
	var runflag = outparamters.getValue("runflag");
	var standid = outparamters.getValue("standid");
	if(runflag==1){
		linkIframe(standid);
	}else if(runflag==2){
		linkScale(standid);
	}
	runFlagCheck(runflag);
	
	document.getElementById("sformula").value=getDecodeStr(formulavalue);
}
function runFlagCheck(runflag){
	if(runflag==0){
		toggles("expression");
		hides("standard");
		hides("ratetable");
	}else if(runflag==1){
		hides("expression");
		toggles("standard");
		hides("ratetable");
	}else if(runflag==2){
		toggles("ratetable");
		hides("standard");
		hides("expression");
	}else{
		toggles("expression");
		hides("standard");
		hides("ratetable");
	}
}
function getItemList(val){
	var in_paramters="stype"+val;
    var request=new Request({method:'post',asynchronous:false,parameters:in_paramters,onSuccess:showCodeFieldList,functionId:'3020050012'});
}

function changeCodeValue(){
  	var item=$F("fielditemid");
  	if(item==null||item==undefined||item.length<1){
  		return;
  	}
  	var itemid = item.split(":");
    symbol('sformula',itemid[1]);
	//var in_paramters="itemid="+itemid[0];
    //var request=new Request({method:'post',asynchronous:false,parameters:in_paramters,onSuccess:showCodeFieldList,functionId:'3020050012'});
}
function settemp(){
	var salaryid=document.getElementById("salaryid").value;
	var thecodeurl = "/gz/gz_accounting/iframvartemp.jsp?state="+salaryid;
   	var return_vo= window.showModalDialog(thecodeurl,"window2",
   						"dialogWidth:900px;dialogHeight:550px;resizable:no;center:yes;scroll:yes;status:no");
    if(return_vo!=null){
    	var hashvo=new ParameterSet();
    	hashvo.setValue("salaryid",salaryid);
    	hashvo.setValue("itemid",return_vo);
    	var request=new Request({method:'post',asynchronous:false,
     		onSuccess:setItemList,functionId:'3020060021'},hashvo);
    }
}
function setItemList(outparamters){
	var itemlist=outparamters.getValue("itemlist");
	var itemid = outparamters.getValue("itemid");
	if(itemlist.length>0){
		AjaxBind.bind(formulaForm.itemid,itemlist);
		document.getElementById("itemid").value=itemid;
		var arr = itemid.split(":");
		if(arr.length==2){
			symbol('formula',arr[1]);
		}
	}
}
function showCodeFieldList(outparamters){
	var codelist=outparamters.getValue("codelist");
	if(codelist!=null&&codelist.length>1){
		toggles("codeview");
		AjaxBind.bind(busiMaintenceForm.codesetid_arr,codelist);
	}else{
		hides("codeview");
	}	
} 
function getCodesid(){
	var codeid="";
	var codesetid_arr= document.getElementsByName("codesetid_arr");
	var codesetid_arr_vo = codesetid_arr[0];
	if(codesetid_arr==null){
		return;
	}else{
		for(var i=0;i<codesetid_arr_vo.options.length;i++){
			if(codesetid_arr_vo.options[i].selected){
				codeid =codesetid_arr_vo.options[i].value;
				continue;
			}
		}
		if(codeid==null||codeid==undefined||codeid.length<1){
  			return;
  		}
		symbol('sformula',"\""+codeid+"\"");
	}
} 
function tr_bgcolor(nid){
	var tablevos=document.getElementsByTagName("input");
	for(var i=0;i<tablevos.length;i++){
	    if(tablevos[i].type=="checkbox"){
	    	var cvalue = tablevos[i];
	    	var td = cvalue.parentNode.parentNode;
	    	td.style.backgroundColor = '';
		}
    }
	var c = document.getElementById(nid);
	var tr = c.parentNode.parentNode;
	if(tr.style.backgroundColor!=''){
		tr.style.backgroundColor = '' ;
	}else{
		tr.style.backgroundColor = '#FFF8D2' ;
	}
}
function getCheck(){
	var checks = 0;
	var tablevos=document.getElementsByTagName("input");
	for(var i=0;i<tablevos.length;i++){
	    if(tablevos[i].type=="checkbox"){
	    	alert(tablevos[i].name);
	    	if(tablevos[i].checked){
	    		checks=1
	    	}
	    	break;
		}
    }
    return checks;
}

function linkScale(taxid){
	var salaryid=document.getElementById("salaryid").value;
	var itemid=document.getElementById("item").value;
	document.iframe_rate.location.href="/gz/templateset/tax_table/initTaxDetailTable.do?b_init=init&taxid="+taxid+"&salaryid="+salaryid+"&itemid="+itemid;
}
function linkIframe(standid){
	standid=standid!=null&&standid.length>0?standid:"";
	document.iframe_user.location.href="/gz/formula/standard.do?b_query=init&opt=edit&standardID="+standid;
}

function delProject(itemid){
	var itemid=document.getElementById("item").value;
	var salaryid = document.getElementById("salaryid").value;
	if(itemid==null&&itemid.length<1&&salaryid==null&&salaryid.length<1){
		return;
	}
	 if(!ifdel()){
    	return ;
    }
	var hashvo=new ParameterSet();
	hashvo.setValue("salaryid",salaryid);
	hashvo.setValue("itemid",itemid);
	
	var request=new Request({method:'post',asynchronous:false,
     	onSuccess:del,functionId:'3020060006'},hashvo);
}
function del(outparamters){
	var base=outparamters.getValue("base");
	if(base=='ok'){
		reflesh();
	}else{
		alert("<bean:message key='gz.formula.del.project.failure'/>");;
	}
}
function reflesh(){
	var salaryid=document.getElementById("salaryid").value;
    var thecodeurl ="/gz/formula/viewformula.do?b_query=link&salaryid="+salaryid; 
    window.location.href=thecodeurl;
} 
function sorting(){
	var salaryid=document.getElementById("salaryid").value;
	document.location.href="/gz/formula/sorting.do?b_query=link&salaryid="+salaryid;
}
function setcond(){
	var salaryid=document.getElementById("salaryid").value;
	var item=document.getElementById("item").value;
  	var hashvo=new ParameterSet();
	hashvo.setValue("salaryid",salaryid);
	hashvo.setValue("item",item);
	var In_paramters="flag=1"; 	
	var request=new Request({method:'post',asynchronous:false,
		parameters:In_paramters,onSuccess:getcond,functionId:'3020060019'},hashvo);				
}
function getcond(outparamters){
	var conditions = outparamters.getValue("conditions");
	var salaryid=document.getElementById("salaryid").value;
	var cond = condiTions(conditions,salaryid);
	if(cond!=null){
		savecond(cond);
	}
}
function savecond(cond){
	var hashvo=new ParameterSet();
	var salaryid = document.getElementById("salaryid").value;
	hashvo.setValue("salaryid",salaryid);
		
	var itemid = document.getElementById("item").value;
	hashvo.setValue("item",itemid);
		
	hashvo.setValue("conditions",cond);
	var request=new Request({method:'post',asynchronous:false,functionId:'3020060010'},hashvo);
}
function savemula(){
	var formula=document.getElementById("sformula").value;
	var itemname = '${busiMaintenceForm.fielditemid}';
  		var hashvo=new ParameterSet();
	    hashvo.setValue("c_expr",getEncodeStr(formula));
	    hashvo.setValue("itemid",itemname);
	    hashvo.setValue("salaryid",'');
	    hashvo.setValue("itemtype",'');
	   	var In_paramters="flag=1"; 	
		var request=new Request({method:'post',asynchronous:false,
			parameters:In_paramters,onSuccess:resultCheckExpr,functionId:'3020060020'},hashvo);	
}

function resultCheckExpr(outparamters){
  	var info = outparamters.getValue("info");
	if(info=="ok"){
		var formula=document.getElementById("sformula").value;
		//formula = getEncodeStr(formula);
		window.returnValue=formula;
		window.close();
	}else{
		if(info.length<4){
			var formula=document.getElementById("sformula").value;
			alert(formula+" "+SYNTAX_ERROR+"!");
		}else{
			alert(getDecodeStr(info));
		}
	}
}
function selectStandard(itemname){
	document.getElementById("itemname").value=itemname;
	var salaryid=document.getElementById("salaryid").value;
	var itemid = document.getElementById("item").value;
    var thecodeurl ="/gz/formula/selectstandard.do?b_query=link&itemname="+itemname+"&salaryid="+salaryid+"&item="+itemid; 
    var return_vo= window.showModalDialog(thecodeurl, "", 
              "dialogWidth:400px; dialogHeight:230px;resizable:no;center:yes;scroll:yes;status:no");
    if(return_vo!=null){
  	 	return return_vo;
  	}
}

function changeshade(val){
	if(val==0){
		document.all.ly.style.display="block";   
		document.all.ly.style.width=document.body.clientWidth;  
		if(navigator.appName.indexOf("Microsoft")!= -1) 
			document.all.ly.style.height=document.body.clientHeight-300;
		else
			document.all.ly.style.height=document.body.clientHeight-315;
		$('wizard').disabled="disabled";
		$('sformula').value="";
		$("fielditemid").value="";
	}else{
		document.all.ly.style.display="none";  
		$('wizard').disabled="";
	}
}

function showFieldSetList(outparamters){
	var itemlist=outparamters.getValue("itemlist");
	AjaxBind.bind(infoSetupForm.itemid_arr,itemlist);
	changeCode();
}
function changeCode(){
   var itemid=getItemid().split(":");
   var in_paramters="itemid="+itemid[0];
   var request=new Request({method:'post',asynchronous:false,parameters:in_paramters,onSuccess:showCodeFieldList,functionId:'3020050012'});
}
function changeCodeValue(){
   var itemid=getItemid().split(":");
   symbol('sformula',itemid[1]);
   var in_paramters="itemid="+itemid[0];
   var request=new Request({method:'post',asynchronous:false,parameters:in_paramters,onSuccess:showCodeFieldList,functionId:'3020050012'});
}
function showCodeFieldList(outparamters){
	var codelist=outparamters.getValue("codelist");
	if(codelist!=null&&codelist.length>1){
		toggles("viewcode");
		AjaxBind.bind(infoSetupForm.codesetid_arr,codelist);
	}else{
		hides("viewcode");
	}
}
function getItemid(){
	var itemid="";
	var itemid_arr= document.getElementsByName("itemid_arr");
	var itemid_arr_vo = itemid_arr[0];
	if(itemid_arr==null){
		return "";
	}else{
		for(var i=0;i<itemid_arr_vo.options.length;i++){
			if(itemid_arr_vo.options[i].selected){
				itemid =itemid_arr_vo.options[i].text;
				continue;
			}
		}
		return itemid;
	}
}
function getCodesid(){
	var codeid="";
	var codesetid_arr= document.getElementsByName("codesetid_arr");
	if(codesetid_arr==null){
		return;
	}else{
		var codesetid_arr_vo = codesetid_arr[0];
		for(var i=0;i<codesetid_arr_vo.options.length;i++){
			if(codesetid_arr_vo.options[i].selected){
				codeid =codesetid_arr_vo.options[i].value;
				continue;
			}
		}
		symbol('sformula',"\""+codeid+"\"");
	}
}

function addFormula(){
	var title=$F('title');
	if(title.length==0){
		alert("名称不能为空");
	}else{
		var itemidselect=$('itemid');
		var value=itemidselect.options[itemidselect.selectedIndex].value;
		var text=itemidselect.options[itemidselect.selectedIndex].text;
		//itemidselect.remove(index);
		var unitselect=$('unit');
		var bb=false;
		for(var i=0;i<unitselect.options.length;i++){
			if(unitselect.options[i].text==title){
				alert("名称不能相同");
				bb=true;
			}
		}
		if(bb)
			return;
		var flag='';
		/*for(var i=0;i<unitselect.options.length;i++){
			if(unitselect.options[i].value==value){
				flag=unitselect.options[i].text;
				if(flag.length==0){
					flag=" ";
				}
				break;
			}
		}*/
		if(flag.length==0){
			for(var i=0;i<unitselect.options.length;i++){
				unitselect.options[i].selected=false;
			}
			itemidselect.disabled=true;
			$('sformula').value="";
			$('add').style.display='none';
			$('add1').style.display='';
			var opp = new Option(title,"");  //parseInt(unitselect.options[unitselect.options.length-1].value,10)+1
			unitselect.add(opp);
			unitselect.options[unitselect.options.length-1].selected=true;
			$('ly').style.display='none';
		}else{
			alert('按“'+text+'”的统计方式“'+flag+'”已存在');
		}
	}
}
function clearselected(obj){
	var flag = false;
	for(var i=0;i<obj.options.length;i++){
		if(obj.options[i].selected){
			obj.options[i].selected=false;
			flag=true;	
		}
	}
	if(flag)
		obj.fireEvent("onchange");
}

function editeunit(obj){
	var v=obj.value;
	var unitselect=$('unit');
	if(unitselect.selectedIndex!=-1){
		if(v.length==0){
			alert("名称不能为空!");
			for(var i=0;i<unitselect.options.length;i++){
				if(unitselect.options[i].selected){
					obj.value=unitselect.options[i].text;
					break;	
				}
			}
			return;
		}
		for(var i=0;i<unitselect.options.length;i++){
			if(unitselect.options[i].selected){
				unitselect.options[i].text=v;
				editsformula();	
				break;	
			}
		}
	}
}

function editsformula(){
	$('saveid1').style.display='none';
	$('saveid').style.display='';	
}

function savefavalue(){
		var favalue =$F('sformula');
		window.returnValue=favalue;
		window.close();
}

function selectedunit(obj){
	var v=obj.value;
	var index=obj.options.length-1;
	if(obj.options[index].value.length==0){
		obj.remove(index);
	}
	//alert(v);
	if(v.length>0){
		var hashvo=new ParameterSet();  
		hashvo.setValue("id",v);   
		hashvo.setValue("opflag","get"); 
		hashvo.setValue("statid","<%=request.getParameter("id")%>");      
		var request=new Request({onSuccess:submitRe,functionId:'11080204091'},hashvo); 
		function submitRe(outparamters){
				  var opflag=outparamters.getValue("opflag"); 
				  if(opflag=="true")
				  {
				  	$('ly').style.display='none';
				  	$('title').value=outparamters.getValue("title");
				  	$('decimalwidth').value=outparamters.getValue("decimalwidth");
				  	$("unit").options[$("unit").selectedIndex].text=$('title').value;
				  	$('sformula').value=getDecodeStr(outparamters.getValue("sformula"));
				  	var type=outparamters.getValue("type");
				  	var itemselect=$('itemid');
				  	for(var i=0;i<itemselect.options.length;i++){
				  		if(itemselect.options[i].value==type){
				  			itemselect.options[i].selected=true;
				  			break;
				  		}
				  	}
					//itemselect.disabled=true;
					$('add').style.display='none';
					$('add1').style.display='';
					$('saveid').style.display='';
					$('saveid1').style.display='none';
				  }
		}
	}else{
		$('title').value="";
		$('sformula').value="";
		var itemselect=$('itemid');
		itemselect.disabled='';
		$('add').style.display='';
		$('add1').style.display='none';
		$('ly').style.display="";
		$('saveid').style.display='none';
		$('saveid1').style.display='';
		$('decimalwidth').value=2;
	}
}

function delFormula(){
	var unitselect=$('unit');
	var ids=",";//初始长度就为1
	var count = 0;
	for(var i=0;i<unitselect.options.length;i++){
		if(unitselect.options[i].selected){
			count ++;
			ids+=unitselect.options[i].value+",";
		}
	}
	
	if (unitselect.options.length > 0 && count == unitselect.options.length) {
		alert('统计方式不能全部删除！');
		return;
	}
	
	if(ids.length>1){//田野修改，初始长度为1，因判断是否大于1
		if(confirm('确认要删除?')){
			var hashvo=new ParameterSet();  
			hashvo.setValue("id",ids);   
			hashvo.setValue("opflag","delete"); 
			hashvo.setValue("statid","<%=request.getParameter("id")%>");      
			var request=new Request({onSuccess:submitRe,functionId:'11080204091'},hashvo); 
			function submitRe(outparamters){
				  var opflag=outparamters.getValue("opflag"); 
				  if(opflag=="true")
				  {
				  	for(var i=0;i<unitselect.options.length;i++){
						if(unitselect.options[i].selected){
							unitselect.remove(i);
							--i;
						}
					}
					$('title').value="";
					$('sformula').value="";
					var itemselect=$('itemid');
					itemselect.disabled='';
					$('add').style.display='';
					$('add1').style.display='none';
					$('ly').style.display="";
					$('saveid').style.display='none';
					$('saveid1').style.display='';
					window.returnValue="aaa";
				  }
			}
		}
	}else{
		alert('未选择要删除统计方式');
	}
}
function checkInt(e){
    	e=e?e:(window.event?window.event:null);
		var key = window.event?e.keyCode:e.which;
		return ((48<=key&&key<=57)||key==45||key==43);
    }
    //员工管理，统计分析，常用统计，统计方式设置页面，遮罩层和按钮样式  jingq add 2014.11.14
    function setLY(){
    }
//-->
</script>
<hrms:themes />
<style>
<!--
.div_table {
	border-width: 1px;
	BORDER-BOTTOM: #aeac9f 1pt solid;
	BORDER-LEFT: #aeac9f 1pt solid;
	BORDER-RIGHT: #aeac9f 1pt solid;
	BORDER-TOP: #aeac9f 1pt solid;
}

.tdFontcolor {
	text-decoration: none;
	Font-family: ????;
	font-size: 12px;
	height
	=20px;
	align
	="center"
}

/*****************************公式鍵盤按鈕顏色*****************************************/
	/*
.btn1 {
 BORDER-RIGHT: #7b9ebd 1px solid;
 PADDING-RIGHT: 0px;
 BORDER-TOP: #7b9ebd 1px solid; 
 PADDING-LEFT: 0px; FONT-SIZE: 12px; 
 BORDER-LEFT: #7b9ebd 1px solid; 
 CURSOR: hand; 
 COLOR: black; 
 PADDING-TOP: 1px; 
 PADDING-BOTTOM: 0px;
 BORDER-BOTTOM: #7b9ebd 1px solid;
 border:#0042A0 1px solid;
 background-image:url(/images/button.jpg);	
}
.btn2 {
 BORDER-RIGHT: #7b9ebd 1px solid;
 PADDING-RIGHT: 3px; 
 BORDER-TOP: #7b9ebd 1px solid; 
 PADDING-LEFT: 3px; FONT-SIZE: 12px; 
 BORDER-LEFT: #7b9ebd 1px solid; 
 CURSOR: hand; 
 COLOR: black; 
 PADDING-TOP: 1px; 
 PADDING-BOTTOM: 0px;
 BORDER-BOTTOM: #7b9ebd 1px solid;
 border:#0042A0 1px solid;
 background-image:url(/images/button.jpg);	
}
.btn3 {
BORDER-RIGHT: #7b9ebd 1px solid;
 PADDING-RIGHT: 2px;
 BORDER-TOP: #7b9ebd 1px solid; 
 PADDING-LEFT: 2px; FONT-SIZE: 12px; 
 BORDER-LEFT: #7b9ebd 1px solid; 
 CURSOR: hand; 
 COLOR: black; 
 PADDING-TOP: 1px; 
 PADDING-BOTTOM: 0px;
 BORDER-BOTTOM: #7b9ebd 1px solid;
 border:#0042A0 1px solid;
 background-image:url(/images/button.jpg);	
}*/
.btn {
	height: 23px;
	width: 18px;
}

#scroll_box {
	border: 1px solid #eee;
	height: 280px;
	width: 270px;
	overflow: auto;
	margin: 1em 1;
}

.ListTablex {
	border: 1px solid #8EC2E6;
	border-collapse: collapse;
	BORDER-BOTTOM: medium none;
	BORDER-LEFT: medium none;
	BORDER-TOP: #94B6E6 1pt solid;
	margin-top: 5px;
}

.setsformulaTable {
	width: expression(document . body . clientWidth-10);
	margin-left: -4px;
	<%if("hl".equalsIgnoreCase(bosflag)){ %>
	margin-top:10px;
	<%}%>
}
-->

.lyclass{
	top:125px;
	height:335px;
}
<!-- 员工管理，统计分析，常用统计，统计方式设置页面，遮罩层和按钮样式  jingq add 2014.11.14 -->
/* Chrome */
@media screen and (-webkit-min-device-pixel-ratio:0){.lyclass{top:150px;height:280px;}}
/* FireFox */
@-moz-document url-prefix() {.lyclass{top:133px;height:310px;}}
</style>
<body onclick="" onload="setLY();">
	<html:form action="/general/static/commonstatic/statshowsetup">
		<table width="100%" border="0" cellspacing="0" align="center"
			cellpadding="0" class="setsformulaTable">
			<tr>
				<td colspan="2" class="framestyle1">
					<table width="100%" border="0" cellspacing="0" align="center"
						cellpadding="0">
						<tr>
							<td>
								&nbsp;
							</td>
							<td align="left">
								<fieldset align="left" style="width: 395px">
									<legend>
										<bean:message key="hmuster.label.expressions" />
									</legend>
									<table width="90%" border="0" height="100">
										<tr>
											<td colspan="2" align="left">
												<table width="100%" border="0" align="left" cellpadding="0"
													cellspacing="0">
													<tr>
														<td>
															<html:textarea name="infoSetupForm" property="sformula"
																cols="56" rows="5" styleId="sformula"
																onfocus="editsformula();"
																style="font-size:13px;height:60px!important;height:;"></html:textarea>
																<input type="button" value="函数" style="vertical-align: top;margin-top=6px" class="mybutton" 
																onclick="function_Wizard('','sformula')"></span>
														</td>
														
													</tr>
												</table>
											</td>
										</tr>

										<tr>
											<td width="52%" align="center">
												<fieldset align="center" style="width: 200px;height: 100%;">
													<legend>
														<bean:message key='org.maip.reference.projects' />
													</legend>
													<table width="98%" height="50" border="0">
														<tr>
															<td height="35" align="center">
																<bean:message key="menu.table" />
																<html:select name="infoSetupForm" property="setid"
																	onchange="changeSetid();"
																	style="width:140;font-size:9pt">
																	<html:optionsCollection property="fieldsetlist"
																		value="dataValue" label="dataName" />
																</html:select>
															</td>
														</tr>
														<tr>
															<td height="35" align="center">
																<bean:message key="menu.field" />
																<select name="itemid_arr" onchange="changeCodeValue();"
																	style="width: 140; font-size: 9pt">
																</select>
															</td>
														</tr>
														<tr>
															<td height="25" align="center">
																<span id="viewcode" style="text-align: center;">
																	<table width="100%" border="0" align="center"
																		cellpadding="0" cellspacing="0">
																		<tr>
																			<td align="center">
																				代码
																				<select name="codesetid_arr"
																					onchange="getCodesid();"
																					style="width: 140; font-size: 9pt">
																				</select>
																			</td>
																		</tr>
																	</table> </span>
															</td>
														</tr>
													</table>
												</fieldset>
											</td>
											<td width="48%">
												<fieldset align="center" style="width: auto;">
													<legend>
														<bean:message key="gz.formula.operational.symbol" />
													</legend>
													<table width="80%" border="0">
														<tr>
															<td>
																<table width="100%" border="0">
																	<tr>
																		<td>
																			<input type="button" value="0"
																				onclick="symbol('sformula',0);"
																				class="smallbutton btn">
																		</td>
																		<td>
																			<input type="button" value="1"
																				onclick="symbol('sformula',1);"
																				class="smallbutton btn">
																		</td>
																		<td>
																			<input type="button" value="2"
																				onclick="symbol('sformula',2);"
																				class="smallbutton btn">
																		</td>
																		<td>
																			<input type="button" value="3"
																				onclick="symbol('sformula',3);"
																				class="smallbutton btn">
																		</td>
																		<td>
																			<input type="button" value="4"
																				onclick="symbol('sformula',4);"
																				class="smallbutton btn">
																		</td>
																		<td>
																			<input type="button" value="("
																				onclick="symbol('sformula','(');"
																				class="smallbutton btn">
																		</td>
																		<td colspan="2">
																			<input type="button"
																				value="<bean:message key='gz.formula.if'/>"
																				onclick="symbol('sformula','<bean:message key='gz.formula.if'/>');"
																				class="smallbutton"
																				style="height: 23px; width: 40px;">
																		</td>
																	</tr>
																	<tr>
																		<td>
																			<input type="button" value="5"
																				onclick="symbol('sformula',5);"
																				class="smallbutton btn">
																		</td>
																		<td>
																			<input type="button" value="6"
																				onclick="symbol('sformula',6);"
																				class="smallbutton btn">
																		</td>
																		<td>
																			<input type="button" value="7"
																				onclick="symbol('sformula',7);"
																				class="smallbutton btn">
																		</td>
																		<td>
																			<input type="button" value="8"
																				onclick="symbol('sformula',8);"
																				class="smallbutton btn">
																		</td>
																		<td>
																			<input type="button" value="9"
																				onclick="symbol('sformula',9);"
																				class="smallbutton btn">
																		</td>
																		<td>
																			<input type="button" value=")"
																				onclick="symbol('sformula',')');"
																				class="smallbutton btn">
																		</td>
																		<td colspan="2">
																			<input type="button"
																				value="<bean:message key='gz.formula.else'/>"
																				onclick="symbol('sformula','<bean:message key='gz.formula.else'/>');"
																				class="smallbutton"
																				style="height: 23px; width: 40px;">
																		</td>
																	</tr>
																	<tr>
																		<td>
																			<input type="button" value="+"
																				onclick="symbol('sformula','+');"
																				class="smallbutton btn">
																		</td>
																		<td>
																			<input type="button" value="-"
																				onclick="symbol('sformula','-');"
																				class="smallbutton btn">
																		</td>
																		<td>
																			<input type="button" value="*"
																				onclick="symbol('sformula','*');"
																				class="smallbutton btn">
																		</td>
																		<td>
																			<input type="button" value="/"
																				onclick="symbol('sformula','/');"
																				class="smallbutton btn">
																		</td>
																		<td>
																			<input type="button" value="\"
																				onclick="symbol('sformula','\\');"
																				class="smallbutton btn">
																		</td>
																		<td>
																			<input type="button" value="%"
																				onclick="symbol('sformula','%');"
																				class="smallbutton btn">
																		</td>
																		<td>
																			<input type="button"
																				value="<bean:message key='general.mess.and'/>"
																				onclick="symbol('sformula','<bean:message key='general.mess.and'/>');"
																				class="smallbutton btn">
																		</td>
																		<td>
																			<input type="button"
																				value="<bean:message key='general.mess.or'/>"
																				onclick="symbol('sformula','<bean:message key='general.mess.or'/>');"
																				class="smallbutton btn">
																		</td>
																	</tr>
																	<tr>
																		<td>
																			<input type="button" value="="
																				onclick="symbol('sformula','=');"
																				class="smallbutton btn">
																		</td>
																		<td>
																			<input type="button" value="&gt;"
																				onclick="symbol('sformula','&gt;');"
																				class="smallbutton btn">
																		</td>
																		<td>
																			<input type="button" value="&lt;"
																				onclick="symbol('sformula','&lt;');"
																				class="smallbutton btn">
																		</td>
																		<td>
																			<input type="button" value="&lt;&gt;"
																				onclick="symbol('sformula','&lt;&gt;');"
																				class="smallbutton btn" style="font-size:7pt;">
																		</td>
																		<td>
																			<input type="button" value="&lt;="
																				onclick="symbol('sformula','&lt;=');"
																				class="smallbutton btn" style="font-size:7pt;">
																		</td>
																		<td>
																			<input type="button" value="&gt;="
																				onclick="symbol('sformula','&gt;=');"
																				class="smallbutton btn" style="font-size:7pt;">
																		</td>
																		<td>
																			<input type="button" value="~"
																				onclick="symbol('sformula','~');"
																				class="smallbutton btn">
																		</td>
																		<td>
																			<input type="button"
																				value="<bean:message key='kq.wizard.not'/>"
																				onclick="symbol('sformula','<bean:message key='kq.wizard.not'/>');"
																				class="smallbutton btn">
																		</td>
																	</tr>
																</table>
															</td>
														</tr>
													</table>
												</fieldset>
											</td>
										</tr>
									</table>
								</fieldset>
							</td>
						</tr>
					</table>
				</td>
			</tr>
		</table>
		<table width="100%" border="0" cellspacing="0" align="center"
			cellpadding="0">
			<tr>
				<td colspan="2" height="5"></td>
			</tr>
			<tr>
				<td align="right">
					<span id='saveid' style="display: none"> <input
							type="button" value="确定" class="mybutton"
							onclick="savefavalue();"> </span>
					<span id='saveid1'> <input type="button" value="保存"
							class="mybutton" disabled="disabled"> </span>
				</td>
				<td align="left">
					<html:button styleClass="mybutton" property="" value="关闭"
						onclick="window.close();">
					</html:button>
				</td>
			</tr>
		</table>
	</html:form></body>
<script>
changeSetid();
</script>