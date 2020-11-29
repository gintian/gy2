<%@ page contentType="text/html; charset=UTF-8" language="java"%>
<%@page import="com.hrms.struts.valueobject.UserView"%>
<%@page import="com.hrms.struts.constant.WebConstant"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean"%>
<%@ taglib uri="/tags/struts-html" prefix="html"%>
<%@ taglib uri="/tags/struts-logic" prefix="logic"%>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms"%>

<link rel="stylesheet" href="/css/css1.css" type="text/css">
<script language="JavaScript" src="/js/constant.js"></script>
<script language="JavaScript" src="/js/validate.js"></script>
<script language="JavaScript" src="/js/function.js"></script>
<script language="JavaScript" src="/gz/salary.js"></script>
<style type="text/css">
<%
UserView userView = (UserView)session.getAttribute(WebConstant.userView);
String bosflag = userView.getBosflag();
if(bosflag.equals("hcm")) {
%>
.btn1 {
	width: 18px;
	height: 17px;
}

.btn2 {
	width: 18px;
	height: 17px;
}

.btn3 {
	width: 40px;
	height: 17px;
}	
<%
}else{%>
.btn1 {
	width: 22px;
	height: 20px;
}

.btn2 {
	width: 22px;
	height: 20px;
}

.btn3 {
	width: 40px;
	height: 20px;
}
<%
}
%>
.scroll_box {
	border: 1px solid #eee;
	height: 325px;
	width: 270px;
	overflow: auto;
	margin: 1em 1;
}
</style>
<hrms:themes />
<script language="javascript">
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

function changebox(checkvalue,runf,itemid){
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
	document.getElementById("formulaid").value=checkvalue;
	itemid=itemid!=null?itemid:'';
	document.getElementById("itemid").value=itemid;
	var busiid = document.getElementById("busiid").value
	var hashvo=new ParameterSet();
	hashvo.setValue("formulaid",checkvalue);
	hashvo.setValue("model","getformulacontent");
	
	var request=new Request({asynchronous:false,onSuccess:getformulavalue,functionId:'3020091050'},hashvo);
	checkvalue=checkvalue+"";
	if(checkvalue!=null&&checkvalue.length>0){
		tr_bgcolor(checkvalue);
	}
}
function getformulavalue(outparamters){
	var formulavalue= outparamters.getValue("formulavalue");	
	document.getElementById("formula").value=getDecodeStr(formulavalue);
}

function changeCodeValue(){
  	var item=document.getElementById("fielditemid").value;
  	if(item==null||item==undefined||item.length<1){
  		return;
  	}
  	var itemid = item.split(":");
    symbol('formula',itemid[1]);
	var in_paramters="itemid="+itemid[0];
    var request=new Request({method:'post',asynchronous:false,parameters:in_paramters,onSuccess:showCodeFieldList,functionId:'3020050012'});
}


function showCodeFieldList(outparamters){
	var codelist=outparamters.getValue("codelist");
	if(codelist!=null&&codelist.length>1){
		toggles("codeview");
		AjaxBind.bind(pieceRateFormulaForm.codesetid_arr,codelist);
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
	    //	alert(tablevos[i].name);
	    	if(tablevos[i].checked){
	    		checks=1
	    	}
	    	break;
		}
    }
    return checks;
}
function batch_select1(obj){
  var checks=false;
  	if(obj.checked)
  	  checks=true;

	var tablevos=document.getElementsByTagName("input");
	for(var i=0;i<tablevos.length;i++){
	    if(tablevos[i].type=="checkbox"){
	      tablevos[i].checked =checks;
		}
    }
    return checks;
}
function defCheck(itemid){
	var formulaid = "${pieceRateFormulaForm.formulaid}";
	if(formulaid==null||formulaid.length<1){
		var tablevos=document.getElementsByTagName("input");
		for(var i=0;i<tablevos.length;i++){
	    	if((tablevos[i].type=="checkbox")&&(tablevos[i].name!="selbox")){
	    		formulaid=tablevos[i].name;
	    		break;
			}
    	}
    }
    changebox(formulaid,'',itemid);
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
	var busiid=document.getElementById("busiid").value;
    var thecodeurl ="/gz/gz_accounting/piecerate/search_piecerate_formula.do?b_addformula=link&busiid="+busiid; 
    var return_vo= window.showModalDialog(thecodeurl, "", 
              "dialogWidth:400px; dialogHeight:200px;resizable:no;center:yes;scroll:yes;status:no");
	if(return_vo!=null)
	{		
		if(return_vo.length>0)
		{ 
	      var itemid = return_vo[0];  
	   	  var thecodeurl ="/gz/gz_accounting/piecerate/search_piecerate_formula.do?b_query=link&busiid="+pieceRateFormulaForm.busiid.value
	   	           +"&formulaid="+itemid;      
	   	 document.pieceRateFormulaForm.action= thecodeurl;
	   	 document.pieceRateFormulaForm.submit();
	    }
    }
}

function getselobjs(){
    var formulaid="";
	var tablevos=document.getElementsByTagName("input");
	for(var i=0;i<tablevos.length;i++)
	{
    	if((tablevos[i].type=="checkbox")&&(tablevos[i].name!="selbox")&&(tablevos[i].checked==true))
    	{
    		formulaid=formulaid+","+tablevos[i].name;
		}
   	}
    return formulaid;
}


function delProject(itemid){
    var formulaids = getselobjs();
    var busiid=document.getElementById("busiid").value;
	if(formulaids==null||formulaids.length<2||busiid==null||busiid.length<1)
	{
		alert(SEL_RECORDS_DEL);
		return;
	}
	if(!ifdel()) {
    	return ;
    }
    
	var hashvo=new ParameterSet();
	hashvo.setValue("busiid",busiid);
	hashvo.setValue("formulaids",formulaids);
	hashvo.setValue("model","delformula");
	var request=new Request({method:'post',asynchronous:false,
     	onSuccess:del,functionId:'3020091050'},hashvo);
}

function del(outparamters){
	var base=outparamters.getValue("strResult");
	if(base=='ok'){
		reflesh();
	}else{
	//	alert(base);
	}
}
function reflesh(){
	var busiid=document.getElementById("busiid").value;
    var thecodeurl ="/gz/gz_accounting/piecerate/search_piecerate_formula.do?b_query=link&busiid="+busiid; 
    window.location.href=thecodeurl;
} 

function sorting(){
	var busiid=document.getElementById("busiid").value;
	var thecodeurl ="/gz/gz_accounting/piecerate/search_piecerate_formula.do?b_sortformula=link&busiid="+busiid;
	var return_vo= window.showModalDialog(thecodeurl, "", 
              "dialogWidth:400px; dialogHeight:430px;resizable:no;center:yes;scroll:yes;status:no");
    if(return_vo!=null)
    {
    	reflesh();
    }
}

function function_Wizard(salaryid,formula){
    var thecodeurl ="/org/autostatic/mainp/function_Wizard.do?b_query=link&busi=S05";
    var return_vo= window.showModalDialog(thecodeurl, "", 
              "dialogWidth:400px; dialogHeight:430px;resizable:no;center:yes;scroll:yes;status:no");
    if(return_vo!=null)
		symbol(formula,return_vo);
}

function setcond(){
	var formulaid=document.getElementById("formulaid").value;
    var thecodeurl ="/gz/gz_accounting/piecerate/search_piecerate_formula.do?b_calccond=link&formulaid="+formulaid; 
    var return_vo= window.showModalDialog(thecodeurl, "", 
              "dialogWidth:520px; dialogHeight:460px;resizable:no;center:yes;scroll:yes;status:no");     // modify by xiaoyun 2014-9-1 (计算公式-计算条件-去掉滚动条 dialogHeight:430px->460px;)
	if(return_vo!=null){
		savecond(return_vo);
	}	
}

function savecond(cond){
	var hashvo=new ParameterSet();		
	var itemid = document.getElementById("formulaid").value;
	hashvo.setValue("formulaid",itemid);		
	hashvo.setValue("conditions",cond);
	hashvo.setValue("model","savecond");	
	var request=new Request({method:'post',asynchronous:false,functionId:'3020091050'},hashvo);
}

function saveformula(){
	var formula=document.getElementById("formula").value;
	var itemid=document.getElementById("itemid").value;

 	var hashvo=new ParameterSet();
    hashvo.setValue("c_expr",getEncodeStr(formula));
	hashvo.setValue("itemsetid","S05");
	hashvo.setValue("itemid",itemid);
	var In_paramters=""; 		
	var request=new Request({method:'post',asynchronous:false,
		parameters:In_paramters,onSuccess:resultCheckExpr,functionId:'3020060020'},hashvo);	
}

function resultCheckExpr(outparamters){
  	var info = outparamters.getValue("info");
	if(info=="ok"){
		var formula=document.getElementById("formula").value;
		var formulaid = document.getElementById("formulaid").value;
		var hashvo=new ParameterSet();
		hashvo.setValue("formulaid",formulaid);		
		hashvo.setValue("model","saveformulacontent");	
		hashvo.setValue("formula",getEncodeStr(formula));
	
		var request=new Request({method:'post',asynchronous:false,functionId:'3020091050'},hashvo);
		alert(SAVE_FORMULA_OK+"!");
	}else{
		if(info.length<4){
			var formula=document.getElementById("formula").value;
			alert(formula+" "+SYNTAX_ERROR+"!");
		}else{
			alert(getDecodeStr(info));
		}
	}
}

function returnup(){
	window.close();

}
function alertUseFlag(obj){
	var useflag="0";
    if(obj.checked){
    	var useflag="1";
    }

}
</script>
<base target="_self"> 
<html:form action="/gz/gz_accounting/piecerate/search_piecerate_formula">
	<table width="600" height="400" border="0" align="center">
		<tr>
			<td>
			    <html:hidden name="pieceRateFormulaForm" property="busiid" />
				<html:hidden name="pieceRateFormulaForm" property="itemid" />
				<html:hidden name="pieceRateFormulaForm" property="formulaid" />
				<bean:define id="busiid" name='pieceRateFormulaForm' property='busiid' />
				
				<fieldset style="width: 80%;">
					<legend>
						<bean:message key="kq.item.count" />
					</legend>
					<table width="100%" border="0"> 
						<tr>
							<td width="40%" align="center" valign="top">
								<fieldset align="left" valign="top" style="width: 100%; height: 399px"><!-- 计件薪资-作业人员-计算公式样式调整 modify by xiaoyun 2014-10-13 -->
									<legend>
										<bean:message key="gz.formula.list.table" />
									</legend>
									<table width="100%" border="0">
										<tr>
											<td align="center" valign="top">
												<div id="scroll_box" class="scroll_box" style="margin-bottom: 5px;"><!-- 计件薪资-作业人员-计算公式样式调整 modify by xiaoyun 2014-10-13 -->
													<table width="100%" border="0" cellpadding="0" cellspacing="0">
														<tr class="fixedHeaderTr">
											            	<td width="10%" class="TableRow" align="left" style="border-left: none;border-top:none;">
																<input type="checkbox" name="selbox" onclick="batch_select1(this);" 
																      title='<bean:message key="label.query.selectall"/>'>&nbsp;
												    		</td>      
															<td width="60%" class="TableRow" style="border-top:none;border-left:none;border-right:none;" align="left">
																<bean:message key="kq.shift.relief.name" />
															</td>
														</tr>
														<hrms:paginationdb id="element"
															name="pieceRateFormulaForm"
															sql_str="pieceRateFormulaForm.sql" table=""
															where_str="pieceRateFormulaForm.where"	columns="pieceRateFormulaForm.column" order_by="pieceRateFormulaForm.orderby" pagerows="200"
														 	page_id="pagination" indexes="indexes">
															<bean:define id="formulaid" name="element" property="formulaid" />
															<bean:define id="itemid" name="element" property="itemid" />
															<bean:define id="itemname" name="element" property="itemname" />
															<tr>
																<td width="10%" class="RecordRow" style="border-top:none;border-left:none;"align="left" nowrap>
																	<input type="checkbox" name="${formulaid}" value="0"
																		onclick="changebox('${formulaid}','','${itemid}');alertUseFlag(this);" />
																</td>
																<td width="60%" class="RecordRow" style="border-top:none;border-left:none;border-right:none;"
																	onclick="changebox('${formulaid}','','${itemid}');" nowrap>
																	${itemname}
																</td>
															</tr>
														</hrms:paginationdb>
													</table>
												</div>
												<input type="button" value="<bean:message key='button.new.add'/>" onclick="addFormula();" Class="mybutton">
												<input type="button" value="<bean:message key='button.delete'/>" onclick="delProject();" Class="mybutton">
												<input type="button" value="<bean:message key='kq.item.change'/>" onclick="sorting();" Class="mybutton">
											</td>
											</td>
										</tr>										
									</table>
								</fieldset>
							</td>
							<td width="60%" valign="top" align="left">
								<table border="0" cellpadding="0" width="100%">
									<tr valign="top">
										<td>
											<fieldset style="width: 100%; height: 200px">
												<legend>
													<bean:message key="kq.wizard.expre" />
												</legend>
												<table width="100%" border="0">
													<tr>
														<td colspan="2">
															<html:textarea name="pieceRateFormulaForm"
																property="formula" cols="56" rows="10" styleId="shry"></html:textarea>
														</td>
													</tr>
													<tr>
														<td height="21" colspan="2" align="right">
															<input name="wizard" type="button" id="wizard"
																value='<bean:message key="kq.formula.function"/>'
																onclick="function_Wizard('','formula');"
																Class="mybutton">
															<input type="button"
																value="<bean:message key='gz.formula.calculation.conditions'/>"
																onclick="setcond();" Class="mybutton">
															<input type="button"
																value="<bean:message key='org.maip.formula.preservation'/>"
																onclick="saveformula();" Class="mybutton">
															&nbsp;
														</td>
													</tr>

												</table>

											</fieldset>
										</td>
									</tr>

									<tr>
										<td>
											<table border="0" width="100%" align="left">
												<tr>
													<td width="60%" align="left">
														<fieldset style="width: 100%; height: 120px">
															<legend>
																<bean:message key='org.maip.reference.projects' />
															</legend>
															<table width="100%" border="0">
																<tr height="30">
																	<td>

																		<bean:message key="field_result.fielditem" />
																		<hrms:optioncollection name="pieceRateFormulaForm"
																			property="itemlist" collection="list" />
																		<html:select name="pieceRateFormulaForm"
																			property="fielditemid" onchange="changeCodeValue();"
																			style="width:160">
																			<html:options collection="list" property="dataValue"
																				labelProperty="dataName" />
																		</html:select>

																	</td>
																</tr>
																<tr height="30">
																	<td>
																		<span id="codeview" style="display: none"> <bean:message
																				key="gz.acount.code.project" /> <select
																				name="codesetid_arr" onchange="getCodesid();"
																				style="width: 160; font-size: 9pt">
																			</select> </span>
																	</td>
																</tr>



															</table>
														</fieldset>
													</td>

													<td width="40%" align="left">
														<fieldset style="width: 100%; height: 120px">
															<legend>
																<bean:message key="gz.formula.operational.symbol" />
															</legend>
															<table width="100%" border="0">
																<tr>
																	<td>
																		<table width="100%" border="0">
																			<tr>
																				<td>
																					<input type="button" value="0"
																						onclick="symbol('formula',0);" class="btn2 smallbutton">
																				</td>
																				<td>
																					<input type="button" value="1"
																						onclick="symbol('formula',1);" class="btn2 smallbutton">
																				</td>
																				<td>
																					<input type="button" value="2"
																						onclick="symbol('formula',2);" class="btn2 smallbutton">
																				</td>
																				<td>
																					<input type="button" value="3"
																						onclick="symbol('formula',3);" class="btn2 smallbutton">
																				</td>
																				<td>
																					<input type="button" value="4"
																						onclick="symbol('formula',4);" class="btn2 smallbutton">
																				</td>
																				<td>
																					<input type="button" value="("
																						onclick="symbol('formula','(');" class="btn2 smallbutton">
																				</td>
																				<td colspan="2">
																					<input type="button"
																						value="<bean:message key='gz.formula.if'/>"
																						onclick="symbol('formula','<bean:message key='gz.formula.if'/>');"
																						class="btn3 smallbutton">
																				</td>
																			</tr>
																			<tr>
																				<td>
																					<input type="button" value="5"
																						onclick="symbol('formula',5);" class="btn2 smallbutton">
																				</td>
																				<td>
																					<input type="button" value="6"
																						onclick="symbol('formula',6);" class="btn2 smallbutton">
																				</td>
																				<td>
																					<input type="button" value="7"
																						onclick="symbol('formula',7);" class="btn2 smallbutton">
																				</td>
																				<td>
																					<input type="button" value="8"
																						onclick="symbol('formula',8);" class="btn2 smallbutton">
																				</td>
																				<td>
																					<input type="button" value="9"
																						onclick="symbol('formula',9);" class="btn2 smallbutton">
																				</td>
																				<td>
																					<input type="button" value=")"
																						onclick="symbol('formula',')');" class="btn2 smallbutton">
																				</td>
																				<td colspan="2">
																					<input type="button"
																						value="<bean:message key='gz.formula.else'/>"
																						onclick="symbol('formula','<bean:message key='gz.formula.else'/>');"
																						class="btn3 smallbutton">
																				</td>
																			</tr>
																			<tr>
																				<td>
																					<input type="button" value="+"
																						onclick="symbol('formula','+');" class="btn2 smallbutton">
																				</td>
																				<td>
																					<input type="button" value="-"
																						onclick="symbol('formula','-');" class="btn2 smallbutton">
																				</td>
																				<td>
																					<input type="button" value="*"
																						onclick="symbol('formula','*');" class="btn2 smallbutton">
																				</td>
																				<td>
																					<input type="button" value="/"
																						onclick="symbol('formula','/');" class="btn2 smallbutton">
																				</td>
																				<td>
																					<input type="button" value="\"
																						onclick="symbol('formula','\\');" class="btn2 smallbutton">
																				</td>
																				<td>
																					<input type="button" value="%"
																						onclick="symbol('formula','%');" class="btn2 smallbutton">
																				</td>
																				<td>
																					<input type="button"
																						value="<bean:message key='general.mess.and'/>"
																						onclick="symbol('formula','<bean:message key='general.mess.and'/>');"
																						class="btn1 smallbutton">
																				</td>
																				<td>
																					<input type="button"
																						value="<bean:message key='general.mess.or'/>"
																						onclick="symbol('formula','<bean:message key='general.mess.or'/>');"
																						class="btn1 smallbutton">
																				</td>
																			</tr>
																			<tr>
																				<td>
																					<input type="button" value="="
																						onclick="symbol('formula','=');" class="btn2 smallbutton">
																				</td>
																				<td>
																					<input type="button" value="&gt;"
																						onclick="symbol('formula','&gt;');" class="btn2 smallbutton">
																				</td>
																				<td>
																					<input type="button" value="&lt;"
																						onclick="symbol('formula','&lt;');" class="btn2 smallbutton">
																				</td>
																				<td>
																					<input type="button" value="&lt;&gt;"
																						onclick="symbol('formula','&lt;&gt;');"
																						class="btn1 smallbutton">
																				</td>
																				<td>
																					<input type="button" value="&lt;="
																						onclick="symbol('formula','&lt;=');" class="btn1 smallbutton">
																				</td>
																				<td>
																					<input type="button" value="&gt;="
																						onclick="symbol('formula','&gt;=');" class="btn1 smallbutton">
																				</td>
																				<td>
																					<input type="button" value="~"
																						onclick="symbol('formula','~');" class="btn2 smallbutton">
																				</td>
																				<td>
																					<input type="button"
																						value="<bean:message key='kq.wizard.not'/>"
																						onclick="symbol('formula','<bean:message key='kq.wizard.not'/>');"
																						class="btn1 smallbutton">
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
								</table>
							</td>
						</tr>
					</table>
				</fieldset>
			</td>
		</tr>
	</table>
	<center>
		<input type="button" value="<bean:message key='button.close'/>"
			onclick="returnup();" Class="mybutton">
	</center>
	<script language="javascript">
  var itemid = '${pieceRateFormulaForm.itemid}';
  defCheck(itemid);
</script>
</html:form>
