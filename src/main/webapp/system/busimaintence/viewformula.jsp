<%@ page contentType="text/html; charset=UTF-8" language="java"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean"%>
<%@ taglib uri="/tags/struts-html" prefix="html"%>
<%@ taglib uri="/tags/struts-logic" prefix="logic"%>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms"%>
<%
	String itemtype = request.getParameter("itemtype");
	itemtype = itemtype!=null?itemtype:"";
 %>
<link rel="stylesheet" href="/css/css1.css" type="text/css">
<script language="JavaScript" src="/js/constant.js"></script>
<script language="JavaScript" src="/js/validate.js"></script>
<script language="JavaScript" src="/js/function.js"></script>
<script language="JavaScript" src="../../../../module/utils/js/template.js"></script>
<style type="text/css">
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
}
#scroll_box {
    border: 1px solid #eee;
    height: 280px;    
    width: 270px;            
    overflow: auto;            
    margin: 1em 1;
}
</style>
<hrms:themes></hrms:themes>
<script language="javascript">
function symbol(editor,strexpr){
	// document.getElementById(editor).focus();
	// var element = document.selection;
	// if (element!=null) {
	// 	var rge = element.createRange();
	// 	if (rge!=null)
	// 	rge.text=strexpr;
	// }
	var expr_editor = document.getElementsByName(editor)[0];
	expr_editor.focus();
	var element;
	if(document.selection){
		element = document.selection;
		if (element!=null) {
			var rge = element.createRange();
			if (rge!=null)
				rge.text=strexpr;
		}
	}else{
		element = window.getSelection();
		var start =expr_editor.selectionStart;
		expr_editor.value = expr_editor.value.substring(0,start)+strexpr+expr_editor.value.substring(start,expr_editor.value.length);
		if(!strexpr.length){
			strexpr = strexpr.toString();
		}
		expr_editor.setSelectionRange(start+strexpr.length,start+strexpr.length);
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
	
	document.getElementById("formula").value=getDecodeStr(formulavalue);
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

function changeCodeValue(){
  	// var item=document.getElementById("fielditemid").value;
  	var item=document.getElementsByName("fielditemid")[0].value;
  	if(item==null||item==undefined||item.length<1){
  		return;
  	}
  	var itemid = item.split(":");
    symbol('formula',itemid[1]);
	var in_paramters="itemid="+itemid[0];
    var request=new Request({method:'post',asynchronous:false,parameters:in_paramters,onSuccess:showCodeFieldList,functionId:'3020050012'});
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
		symbol('formula',"\""+codeid+"\"");
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

function addFormula(){
	var salaryid=document.getElementById("salaryid").value;
    var thecodeurl ="/gz/gz_accounting/addformula.do?b_query=link&salaryid="+salaryid; 
    var return_vo= window.showModalDialog(thecodeurl, "", 
              "dialogWidth:400px; dialogHeight:200px;resizable:no;center:yes;scroll:yes;status:no");
	if(return_vo!=null){
    	 var thecodeurl ="/gz/formula/viewformula.do?b_query=link&salaryid="+salaryid+"&itemid="+return_vo; 
    	 window.location.href=thecodeurl;
    }
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
	// var formula=document.getElementById("formula").value;
	var formula=document.getElementsByName("formula")[0].value;
	var itemname = '${busiMaintenceForm.fielditemid}';
  		var hashvo=new ParameterSet();
	    hashvo.setValue("c_expr",getEncodeStr(formula));
	    hashvo.setValue("itemid",itemname);
	    hashvo.setValue("salaryid",'');
	    hashvo.setValue("itemtype",'<%=itemtype %>');
	    hashvo.setValue("itemsetid",'${busiMaintenceForm.itemsetid}');
	   	var In_paramters="flag=1"; 	
		var request=new Request({method:'post',asynchronous:false,
			parameters:In_paramters,onSuccess:resultCheckExpr,functionId:'3020060020'},hashvo);	
}

function resultCheckExpr(outparamters){
	var info = outparamters.getValue("info");
	if(info=="ok"){
		// var formula=document.getElementById("formula").value;
		var formula=document.getElementsByName("formula")[0].value;
		// zhangh 2020-1-17 【56118】V77系统管理：库结构，业务字典，修改指标，点公式没反应，不弹出弹框（google）
		window.returnValue=formula;
		if(parent.window.formulaReturn){
			parent.window.formulaReturn(formula);
		}
		if(parent && parent.Ext && parent.Ext.getCmp('viewformula')){
			parent.Ext.getCmp('viewformula').close();
		}else{
			parent.window.close();
		}
	}else{
		if(info.length<4){
			var formula=document.getElementById("formula").value;
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
function selectScale(){
	var salaryid=document.getElementById("salaryid").value;
	var itemid = document.getElementById("item").value;
    var thecodeurl ="/gz/formula/selectScale.do?b_query=link&salaryid="+salaryid+"&item="+itemid; 
    var return_vo= window.showModalDialog(thecodeurl, "", 
              "dialogWidth:400px; dialogHeight:240px;resizable:no;center:yes;scroll:yes;status:no");
    if(return_vo!=null){
  	 	return return_vo;
  	}else{
  		return "";
  	}
}
function returnup(){
   	document.location.href="/gz/templateset/gz_templatelist.do?b_query=link";
}
function alertUseFlag(obj){
	var salaryid=document.getElementById("salaryid").value;
	var itemid = document.getElementById("item").value;
	var useflag="0";
    if(obj.checked){
    	var useflag="1";
    }
    var hashvo=new ParameterSet();
    hashvo.setValue("salaryid",salaryid);
    hashvo.setValue("itemid",itemid);
    hashvo.setValue("useflag",useflag);
    var request=new Request({method:'post',asynchronous:false,functionId:'3020060022'},hashvo); 
}

function function_Wizard(busi,formula){
    var thecodeurl ="/org/autostatic/mainp/function_Wizard.do?b_query=link&busi="+busi; 
    // var return_vo= window.showModalDialog(thecodeurl, "",
    //           "dialogWidth:400px; dialogHeight:430px;resizable:no;center:yes;scroll:yes;status:no");
    // if(return_vo!=null)
	// 	symbol(formula,return_vo);
	var theUrl = thecodeurl;
	parent.Ext.create('Ext.window.Window', {
		id:'function_Wizard',
		height: 420,
		width: 400,
		resizable:false,
		modal:true,
		autoScroll:false,
		autoShow:true,
		html:'<iframe style="background-color:#ffffff " frameborder="0" scrolling="no" height="100%" width="100%" src="'+theUrl+'"></iframe>',
		renderTo:parent.Ext.getBody(),
		listeners: {
			'close': function () {
				if (this.return_vo) {
					symbol(formula,this.return_vo);
				}
			}
		}

	});
}
</script>
<html:form action="/system/busimaintence/showbusifield">

<table width="100%" height="350" border="0" align="center" cellpadding="0" cellspacing="0">
  <tr> 
    <td width="100%" align="center">
    <span id="expression">
    <table border="0" align="center" cellpadding="0" cellspacing="0">
    <tr><td>
    <fieldset align="center" style="padding:5px">
	<legend><bean:message key="kq.wizard.expre"/></legend> 
		<table width="100%" border="0">
        	<tr> 
          		<td colspan="2" align="center"> 
            		<html:textarea name="busiMaintenceForm" property="formula"  cols="53" rows="12" styleId="shry" style="width:410px;"></html:textarea> 
            	</td>
        	</tr>
        	<tr> 
          		<td height="21" colspan="2" align="right" style="padding-right: 8px;">
          			<input name="wizard" type="button" id="wizard" value='<bean:message key="button.wizard"/>' onclick="function_Wizard('${busiMaintenceForm.itemsetid }','formula');" Class="smallbutton"> &nbsp; 
            		<input type="button" value="<bean:message key='button.ok'/>" onclick="savemula();" Class="smallbutton" style="margin-left: -4px;">&nbsp; 
            		<input type="button" value="<bean:message key='button.cancel'/>" onclick="winclose();" Class="smallbutton">&nbsp; 
            	</td>
        	</tr>
        	<tr> 
          		<td width="52%" align="center"> 
          		 <fieldset  align="center" style="width:98%;padding:0px;height:100%">
				 <legend><bean:message key='org.maip.reference.projects'/></legend>
            		<table width="100%" border="0" height="100">
            			<tr height="30">
            				<td>
            					<table width="100%"  border="0" >
              						<tr> 
                						<td height="30"><bean:message key="gz.formula.project"/>
                							&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
											<hrms:optioncollection name="busiMaintenceForm" property="itemlist" collection="list"/>
											<html:select name="busiMaintenceForm" property="fielditemid" onchange="changeCodeValue();" style="width:130px">
			 									<html:options collection="list" property="dataValue" labelProperty="dataName" />
											</html:select>
                 						</td>
              						</tr>
            					</table>
            				</td>
            			</tr>
            			<tr height="30">
            				<td>
            					<span id="codeview" style="display:none">
            					<table width="100%" border="0" >
              						<tr> 
                						<td height="30"><bean:message key="conlumn.codeitemid.caption"/>											
											<select name="codesetid_arr" onchange="getCodesid();"  style="width:130px;font-size:9pt">
             								</select>
                 						</td>
              						</tr>
            					</table>
            					</span>
            				</td>
            			</tr>
            		</table>
            		</fieldset>
          		</td>
          		<td width="48%" align="center">
          		<fieldset align="center" style="width:98%;padding:0px;height:100%">
				 <legend><bean:message key="gz.formula.operational.symbol"/></legend> 
              				<table width="100%" border="0">
              				<tr>
                				<td align="center" valign="top"><input type="button"  value="0" onclick="symbol('formula',0);" class="btn2 common_btn_bg" style="padding:2px 5px;"></td>
                				<td align="center" valign="top"><input type="button"  value="1" onclick="symbol('formula',1);" class="btn2 common_btn_bg" style="padding:2px 5px;"> </td>
                				<td align="center" valign="top"><input type="button"  value="2" onclick="symbol('formula',2);" class="btn2 common_btn_bg" style="padding:2px 5px;"> </td>
                				<td align="center" valign="top"><input type="button"  value="3" onclick="symbol('formula',3);" class="btn2 common_btn_bg" style="padding:2px 5px;"> </td>
                				<td align="center" valign="top"><input type="button"  value="4" onclick="symbol('formula',4);" class="btn2 common_btn_bg" style="padding:2px 5px;"> </td>
                				<td align="center" valign="top"><input type="button"  value="(" onclick="symbol('formula','(');" class="btn2 common_btn_bg" style="padding:2px 5px;"> </td>
                				<td colspan="2" align="center" valign="top"><input type="button" style="padding:2px 10px;"  value="<bean:message key='gz.formula.if'/>" onclick="symbol('formula','<bean:message key='gz.formula.if'/>');" class="btn3 common_btn_bg"></td>
              				</tr>
              				<tr> 
                				<td align="center" valign="middle"><input type="button" style="padding:2px 5px;"  value="5" onclick="symbol('formula',5);" class="btn2 common_btn_bg"> </td>
                				<td align="center" valign="middle"><input type="button" style="padding:2px 5px;"  value="6" onclick="symbol('formula',6);" class="btn2 common_btn_bg"> </td>
                				<td align="center" valign="middle"><input type="button" style="padding:2px 5px;"  value="7" onclick="symbol('formula',7);" class="btn2 common_btn_bg"> </td>
                				<td align="center" valign="middle"><input type="button" style="padding:2px 5px;"  value="8" onclick="symbol('formula',8);" class="btn2 common_btn_bg"> </td>
                				<td align="center" valign="middle"><input type="button" style="padding:2px 5px;"  value="9" onclick="symbol('formula',9);" class="btn2 common_btn_bg"> </td>
                				<td align="center" valign="middle"><input type="button" style="padding:2px 5px;"  value=")" onclick="symbol('formula',')');" class="btn2 common_btn_bg"> </td>
                				<td colspan="2" align="center" valign="middle"><input type="button" style="padding:2px 10px;"  value="<bean:message key='gz.formula.else'/>" onclick="symbol('formula','<bean:message key='gz.formula.else'/>');" class="btn3 common_btn_bg"></td>
              				</tr>
              				<tr> 
                				<td align="center" valign="middle"><input type="button" style="padding:2px 5px;"  value="+" onclick="symbol('formula','+');" class="btn2 common_btn_bg"> </td>
                				<td align="center" valign="middle"><input type="button" style="padding:2px 5px;"  value="-" onclick="symbol('formula','-');" class="btn2 common_btn_bg"> </td>
                				<td align="center" valign="middle"><input type="button"  style="padding:2px 5px;" value="*" onclick="symbol('formula','*');" class="btn2 common_btn_bg"> </td>
                				<td align="center" valign="middle"><input type="button" style="padding:2px 5px;"  value="/" onclick="symbol('formula','/');" class="btn2 common_btn_bg"> </td>
                				<td align="center" valign="middle"><input type="button" style="padding:2px 5px;"  value="\" onclick="symbol('formula','\\');" class="btn2 common_btn_bg"> </td>
                				<td align="center" valign="middle"><input type="button" style="padding:2px 5px;"  value="%" onclick="symbol('formula','%');" class="btn2 common_btn_bg"> </td>
               			 		<td align="center" valign="middle"><input type="button" style="padding:2px 5px;"  value="<bean:message key='general.mess.and'/>" onclick="symbol('formula','<bean:message key='general.mess.and'/>');" class="btn1 common_btn_bg"> </td>
                				<td align="center" valign="middle"><input type="button" style="padding:2px 5px;"  value="<bean:message key='general.mess.or'/>" onclick="symbol('formula','<bean:message key='general.mess.or'/>');" class="btn1 common_btn_bg"> </td>
              				</tr>
              				<tr> 
               		 			<td align="center" valign="middle"><input type="button" style="padding:2px 5px;"  value="=" onclick="symbol('formula','=');" class="btn2 common_btn_bg"> </td>
                				<td align="center" valign="middle"><input type="button" style="padding:2px 5px;"  value="&gt;" onclick="symbol('formula','&gt;');" class="btn2 common_btn_bg"> </td>
                				<td align="center" valign="middle"><input type="button" style="padding:2px 5px;"  value="&lt;" onclick="symbol('formula','&lt;');" class="btn2 common_btn_bg"> </td>
                				<td align="center" valign="middle"><input type="button"  style="padding:2px 2px;" value="&lt;&gt;" onclick="symbol('formula','&lt;&gt;');" class="btn1 common_btn_bg"> </td>
                				<td align="center" valign="middle"><input type="button"  style="padding:2px 2px;" value="&lt;=" onclick="symbol('formula','&lt;=');"class="btn1 common_btn_bg"> </td>
                				<td align="center" valign="middle"><input type="button"  style="padding:2px 2px;" value="&gt;=" onclick="symbol('formula','&gt;=');"class="btn1 common_btn_bg"> </td>
                				<td align="center" valign="middle"><input type="button" style="padding:2px 8px;"  value="~" onclick="symbol('formula','~');" class="btn2 common_btn_bg"> </td>
                				<td align="center" valign="middle"><input type="button"  style="padding:2px 5px;" value="<bean:message key='kq.wizard.not'/>" onclick="symbol('formula','<bean:message key='kq.wizard.not'/>');" class="btn1 common_btn_bg"> </td>
              				</tr>
            			</table>
            		</fieldset>
          		</td>
        	</tr>
      </table>
      </fieldset>
      </td></tr>
      </table>
      </span>
      
    </td>
  </tr>
</table>
<html:hidden name="busiMaintenceForm" property="formula" styleId="myformula"/>
<script language="javascript">
function winclose(){
	//alert($F('myformula'));
	// window.returnValue=$F('myformula');
	// window.close();
	parent.return_vo = $F('myformula');
	if(parent && parent.Ext && parent.Ext.getCmp('viewformula')){
		parent.Ext.getCmp('viewformula').close();
	}else{
		parent.window.close();
	}
}
if(!getBrowseVersion()){
	var fieldset = document.getElementsByTagName('fieldset');
	for(var i = 0 ; i < fieldset.length ; i++){
		fieldset[i].style.width='90%';
	}
}
</script>
</html:form>
