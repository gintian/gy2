<%@ page contentType="text/html; charset=UTF-8" language="java"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean"%>
<%@ taglib uri="/tags/struts-html" prefix="html"%>
<%@ taglib uri="/tags/struts-logic" prefix="logic"%>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms"%>

<link rel="stylesheet" href="/css/css1.css" type="text/css">
<script language="JavaScript" src="/js/validate.js"></script>
<script language="JavaScript" src="/js/function.js"></script>
<script language="JavaScript" src="/gz/salary.js"></script>
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
function changeRun(runvalue,itemid,itemname){
	var runflag = runvalue.value;
	if(runflag==1){
		var standid = selectStandard(itemname);
		linkIframe(standid);
	}else if(runflag==2){
		var taxid = selectScale();
		if(taxid!=null&&taxid.length>0){
			linkScale(taxid);
		}else{
			runvalue.value=0;
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
	if(checkvalue!=null&&checkvalue>=0){	
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
  	var item=document.getElementById("itemid").value;
  	if(item=='')
  	    return;
  	if(item=='newcreate'){
  		settemp();
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
	var return_vo= window.showModalDialog(thecodeurl,"windows2", 
              "dialogWidth:900px; dialogHeight:540px;resizable:no;center:yes;scroll:yes;status:no");
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
	if(codelist.length>1){
		toggles("codeview");
		AjaxBind.bind(formulaForm.codesetid_arr,codelist);
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
	if(c!=null)
	{
		var tr = c.parentNode.parentNode;
		if(tr.style.backgroundColor!=''){
			tr.style.backgroundColor = '' ;
		}else{
			tr.style.backgroundColor = '#FFF8D2' ;
		}
	}
}
function defCheck(itemname){
	var itemid = "${formulaForm.itemid}";
	if(itemid==null||itemid.length<1){
		var tablevos=document.getElementsByTagName("input");
		for(var i=0;i<tablevos.length;i++){
	    	if(tablevos[i].type=="checkbox"){
	    		itemid=tablevos[i].name;
	    		break;
			}
    	}
    }
    changebox(itemid,'',itemname);
}
function linkScale(taxid){
	var salaryid=document.getElementById("salaryid").value;
	var itemid=document.getElementById("item").value;
	document.iframe_rate.location.href="/gz/templateset/tax_table/initTaxDetailTable.do?b_init=init&taxid="+taxid+"&salaryid="+salaryid+"&itemid="+itemid;
}
function linkIframe(standid){
	document.iframe_user.location.href="/gz/formula/standard.do?b_query=init&opt=edit&standardID="+standid;
}

function addFormula(){
	var salaryid=document.getElementById("salaryid").value;
    var thecodeurl ="/gz/gz_accounting/addformula.do?b_query=link&salaryid="+salaryid; 
    var return_vo= window.showModalDialog(thecodeurl, "", 
              "dialogWidth:300px; dialogHeight:150px;resizable:no;center:yes;scroll:no;status:no");
	if(return_vo!=null){
    	document.formulaForm.action="/gz/gz_accounting/viewformula.do?b_query=link&salaryid="+salaryid+"&itemid="+return_vo; 
   		document.formulaForm.submit();
    }
}
function delProject(){
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
   document.formulaForm.action="/gz/gz_accounting/viewformula.do?b_query=link&salaryid="+salaryid; 
   document.formulaForm.submit();
} 
function sorting(){
	var salaryid=document.getElementById("salaryid").value;
	var thecodeurl="/gz/gz_accounting/sorting.do?b_query=link&salaryid="+salaryid;
	if(isIE6()){
		var return_vo= window.showModalDialog(thecodeurl, "", 
	              "dialogWidth:430px; dialogHeight:460px;resizable:no;center:yes;scroll:yes;status:no");
	}else{
		var return_vo= window.showModalDialog(thecodeurl, "", 
	              "dialogWidth:400px; dialogHeight:430px;resizable:no;center:yes;scroll:yes;status:no");
	}
    if(return_vo!=null){
    	reflesh();
    }
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
	var formula=document.getElementById("formula").value;
	var itemname = document.getElementById("itemname").value;
	var salaryid = document.getElementById("salaryid").value;

  	var hashvo=new ParameterSet();
	hashvo.setValue("c_expr",getEncodeStr(formula));
	hashvo.setValue("itemid",itemname);
	hashvo.setValue("salaryid",salaryid);
	var In_paramters="flag=1"; 	
	var request=new Request({method:'post',asynchronous:false,
			parameters:In_paramters,onSuccess:resultCheckExpr,functionId:'3020060020'},hashvo);
}

function resultCheckExpr(outparamters){
  	var info = outparamters.getValue("info");
	if(info=="ok"){
		var formula=document.getElementById("formula").value;
		var hashvo=new ParameterSet();

		var salaryid = document.getElementById("salaryid").value;
		hashvo.setValue("salaryid",salaryid);
		
		var itemid = document.getElementById("item").value;
		hashvo.setValue("item",itemid);
		
		hashvo.setValue("formula",getEncodeStr(formula));
	
		var request=new Request({method:'post',asynchronous:false,functionId:'3020060011'},hashvo);
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
function selectStandard(itemname){
	document.getElementById("itemname").value=itemname;
	var salaryid=document.getElementById("salaryid").value;
	var itemid = document.getElementById("item").value;
    var thecodeurl ="/gz/formula/selectstandard.do?b_query=link&itemname="+itemname+"&salaryid="+salaryid+"&item="+itemid; 
    var return_vo= window.showModalDialog(thecodeurl, "", 
              "dialogWidth:400px; dialogHeight:230px;resizable:no;center:yes;scroll:yes;status:no");
    if(return_vo!=null){
  	 	return return_vo;
  	}else{
  		return "";
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
</script>
<base target="_self"> 
<html:form action="/gz/gz_accounting/viewformula">
<table width="100%" height="400" border="0" align="center">
<tr>
<td>  
<html:hidden name="formulaForm" property="salaryid"/> 
<html:hidden name="formulaForm" property="item"/> 
<html:hidden name="formulaForm" property="itemname"/> 
<bean:define id="salaryid" name='formulaForm' property='salaryid'/>
<fieldset align="center" style="width:100%;">
<legend><bean:message key="kq.item.count"/></legend>
<table width="100%" height="350" border="0" align="center">
  <tr> 
    <td width="40%" height="350" align="center">
    <fieldset align="center" style="width:100%;height: 371px;">
	<legend><bean:message key="gz.formula.list.table"/></legend> 
      <table width="100%" height="345" border="0">
        <tr > 
          <td height="310" align="center" valign="top"> 
          <div id="scroll_box" style="margin-top: 1px;height: 90%;margin-bottom: 5px;">
            <table width="100%" border="0" cellspacing="0">
              <tr class="fixedHeaderTr1"> 
                <td width="15%" class="TableRow" align="center" style="border-left:none;border-top:none;"><bean:message key="sys.export.status"/></td>
                <td width="65%" class="TableRow" align="center" style="border-left:none;border-top:none;"><bean:message key="kq.shift.relief.name"/></td>
                <td width="20%" class="TableRow" align="center" style="border-left:none;border-top:none;border-right:none;"><bean:message key="gz.formula.implementation"/></td>
              </tr>
              <hrms:paginationdb id="element" name="formulaForm" sql_str="formulaForm.sql" table="" where_str="formulaForm.where" columns="formulaForm.column" order_by="formulaForm.orderby" pagerows="200" page_id="pagination" indexes="indexes">	
				<bean:define id="itemid" name="element" property="itemid"/>
				<bean:define id="hzname" name="element" property="hzname"/>
				<bean:define id="itemname" name="element" property="itemname"/>
			   <tr> 
                <td class="RecordRow" align="center" nowrap style="border-left:none;border-top:none;">
                	<logic:equal name="element" property="useflag" value="1">
                		<input type="checkbox" name="${itemid}" value="1" onclick="changebox(${itemid},'','${itemname}');alertUseFlag(this);" checked/>
                	</logic:equal>
                	<logic:notEqual name="element" property="useflag" value="1">
                		<input type="checkbox" name="${itemid}" value="0" onclick="changebox(${itemid},'','${itemname}');alertUseFlag(this);"/>
                	</logic:notEqual>
                </td>
                <td class="RecordRow" style="border-left:none;border-top:none;" onclick="changebox(${itemid},'','${itemname}');" nowrap>${hzname}</td>
                <td class="RecordRow" style="border-left:none;border-top:none;border-right:none;" onclick="changebox(${itemid},'','${itemname}');" nowrap>
                	<html:select name="element" property="runflag" onchange="changeRun(this,${itemid},'${itemname}');"> 
    					<html:option value="0"><bean:message key="hmuster.label.expressions"/></html:option>
                		<html:option value="1"><bean:message key="gz.formula.standart"/></html:option>
                		<html:option value="2"><bean:message key="gz.formula.scale"/></html:option>
					</html:select>
                </td>
              </tr>
			  </hrms:paginationdb>
            </table>
            </div>
            <input  type="button"  value="<bean:message key='button.new.add'/>" onclick="addFormula();" Class="mybutton"> 
            <input type="button"  value="<bean:message key='button.delete'/>" onclick="delProject();" Class="mybutton">
            <input  type="button"  value="<bean:message key='kq.item.change'/>" onclick="sorting();" Class="mybutton">
          </td>
        </tr>
        
      </table> 
      </fieldset>
    </td>
    <td width="60%" align="center">
    <span id="expression">
    <table border="0" align="center" width="100%" height="345">
    <tr><td>
    <fieldset align="center" style="width:100%;height: 360px;">
	<legend><bean:message key="kq.wizard.expre"/></legend> 
		<table width="100%" border="0">
        	<tr> 
          		<td colspan="2" align="center"> 
            		<html:textarea name="formulaForm" property="formula"  cols="75" rows="9" styleId="shry"></html:textarea> 
            	</td>
        	</tr>
        	<tr> 
          		<td height="21" colspan="2" align="right">
          			<input name="wizard" type="button" id="wizard" value='<bean:message key="kq.formula.function"/>' onclick="function_Wizard('${formulaForm.salaryid}','formula');" Class="mybutton"> 
            		<input type="button" value="<bean:message key='gz.formula.calculation.conditions'/>" onclick="setcond();" Class="mybutton"> 
            		<input type="button" value="<bean:message key='org.maip.formula.preservation'/>" onclick="savemula();" Class="mybutton">&nbsp; 
            	</td>
        	</tr>
        	<tr> 
          		<td width="52%" align="center"> 
          		 <fieldset  align="center" style="width:96%;height:120">
				 <legend><bean:message key='org.maip.reference.projects'/></legend> 
            		<table width="100%" border="0" height="100">
            			<tr height="30">
            				<td>
            					<table width="100%"  border="0" >
              						<tr> 
                						<td height="30"><bean:message key="gz.formula.project"/>
                							<hrms:optioncollection name="formulaForm" property="itemlist" collection="list"/>
											<html:select name="formulaForm" property="itemid" onchange="changeCodeValue();" style="width:140">
			 									<html:options collection="list" property="dataValue" labelProperty="dataName" />
											</html:select>
                 						</td>
              						</tr>
            					</table>
            				</td>
            			</tr>
            			<tr height="30">
            				<td>
            					<span id="codeview">
            					<table width="100%" border="0" >
              						<tr> 
                						<td height="30"><bean:message key="codemaintence.codeitem.id"/>
											<select name="codesetid_arr" onchange="getCodesid();"  style="width:140;font-size:9pt">
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
          		<td width="48%">
          		<fieldset align="center" style="width:100%;">
				 <legend><bean:message key="gz.formula.operational.symbol"/></legend> 
					<table width="100%" border="0">
              			<tr> 
              				<td align="center">
              				<table width="100%" align="center" border="0">
              				<tr>
                				<td><input type="button"  value="0" onclick="symbol('formula',0);" class="btn2 common_btn_bg"></td>
                				<td><input type="button"  value="1" onclick="symbol('formula',1);" class="btn2 common_btn_bg"> </td>
                				<td><input type="button"  value="2" onclick="symbol('formula',2);" class="btn2 common_btn_bg"> </td>
                				<td><input type="button"  value="3" onclick="symbol('formula',3);" class="btn2 common_btn_bg"> </td>
                				<td><input type="button"  value="4" onclick="symbol('formula',4);" class="btn2 common_btn_bg"> </td>
                				<td><input type="button"  value="(" onclick="symbol('formula','(');" class="btn2 common_btn_bg"> </td>
                				<td colspan="2"><input type="button"  value="<bean:message key='gz.formula.if'/>" onclick="symbol('formula','<bean:message key='gz.formula.if'/>');" class="btn3 common_btn_bg"></td>
              				</tr>
              				<tr> 
                				<td><input type="button"  value="5" onclick="symbol('formula',5);" class="btn2 common_btn_bg"> </td>
                				<td><input type="button"  value="6" onclick="symbol('formula',6);" class="btn2 common_btn_bg"> </td>
                				<td><input type="button"  value="7" onclick="symbol('formula',7);" class="btn2 common_btn_bg"> </td>
                				<td><input type="button"  value="8" onclick="symbol('formula',8);" class="btn2 common_btn_bg"> </td>
                				<td><input type="button"  value="9" onclick="symbol('formula',9);" class="btn2 common_btn_bg"> </td>
                				<td><input type="button"  value=")" onclick="symbol('formula',')');" class="btn2 common_btn_bg"> </td>
                				<td colspan="2"><input type="button"  value="<bean:message key='gz.formula.else'/>" onclick="symbol('formula','<bean:message key='gz.formula.else'/>');" class="btn3 common_btn_bg"></td>
              				</tr>
              				<tr> 
                				<td><input type="button"  value="+" onclick="symbol('formula','+');" class="btn2 common_btn_bg"> </td>
                				<td><input type="button"  value="-" onclick="symbol('formula','-');" class="btn2 common_btn_bg"> </td>
                				<td><input type="button"  value="*" onclick="symbol('formula','*');" class="btn2 common_btn_bg"> </td>
                				<td><input type="button"  value="/" onclick="symbol('formula','/');" class="btn2 common_btn_bg"> </td>
                				<td><input type="button"  value="\" onclick="symbol('formula','\\');" class="btn2 common_btn_bg"> </td>
                				<td><input type="button"  value="%" onclick="symbol('formula','%');" class="btn2 common_btn_bg"> </td>
               			 		<td><input type="button"  value="<bean:message key='general.mess.and'/>" onclick="symbol('formula','<bean:message key='general.mess.and'/>');" class="btn1 common_btn_bg"> </td>
                				<td><input type="button"  value="<bean:message key='general.mess.or'/>" onclick="symbol('formula','<bean:message key='general.mess.or'/>');" class="btn1 common_btn_bg"> </td>
              				</tr>
              				<tr> 
               		 			<td><input type="button"  value="=" onclick="symbol('formula','=');" class="btn2 common_btn_bg"> </td>
                				<td><input type="button"  value="&gt;" onclick="symbol('formula','&gt;');" class="btn2 common_btn_bg"> </td>
                				<td><input type="button"  value="&lt;" onclick="symbol('formula','&lt;');" class="btn2 common_btn_bg"> </td>
                				<td><input type="button"  value="&lt;&gt;" onclick="symbol('formula','&lt;&gt;');" class="btn1 common_btn_bg"> </td>
                				<td><input type="button"  value="&lt;=" onclick="symbol('formula','&lt;=');"class="btn1 common_btn_bg"> </td>
                				<td><input type="button"  value="&gt;=" onclick="symbol('formula','&gt;=');"class="btn1 common_btn_bg"> </td>
                				<td><input type="button"  value="~" onclick="symbol('formula','~');" class="btn2 common_btn_bg"> </td>
                				<td><input type="button"  value="<bean:message key='kq.wizard.not'/>" onclick="symbol('formula','<bean:message key='kq.wizard.not'/>');" class="btn1 common_btn_bg"> </td>
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
      </td></tr>
      </table>
      </span>
      <span id="standard">
      <fieldset align="center" style="width:97%;height: 371px;">
	  <legend><bean:message key='gz.formula.standart'/></legend> 
      <table border="0" align="center" width="100%" height="345">
    	<tr><td>
    		<table width="100%" border="0" align="center" >
    			<tr>
    				<td>
    					<iframe id="iframe_user" name="iframe_user" width="100%" height="330" src="#"></iframe>
    				</td>
    			</tr>
    		</table>
    	</td></tr>
      </table>
      </fieldset>
     </span>

     <span id="ratetable">
      <fieldset align="center" style="width:97%;height=290">
	  <legend><bean:message key='gz.formula.scale'/></legend> 
      <table border="0" align="center" width="100%" height="310">
    	<tr><td valign="top">
    		<table width="100%" border="0" align="center">
    			<tr>
    				<td>
    					<iframe id="iframe_rate" name="iframe_rate" width="100%" height="290" src=""></iframe>
    				</td>
    			</tr>
    		</table>
    	</td></tr>
      </table>
      </fieldset>
       <table border="0" align="center" width="100%">
      	<tr>
    		<td align="right" height="30">
    			<input type="button" value="<bean:message key='gz.formula.calculation.conditions'/>" onclick="setcond();" Class="mybutton"> 
    		</td>
    	</tr>
      </table>
     </span>
    </td>
  </tr>
</table>
</fieldset>
</td>
</tr>
</table>
<center><input type="button"  value="<bean:message key='button.close'/>" onclick="window.close();" Class="mybutton"></center>
<script language="javascript">
hides("codeview");
var itemname = '${formulaForm.itemname}';
defCheck(itemname);
</script>
</html:form>
