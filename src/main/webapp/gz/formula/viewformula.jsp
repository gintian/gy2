<%@ page contentType="text/html; charset=UTF-8" language="java"%>
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
    width: 282px;            
    overflow: auto;            
    margin: 1em 0;
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
  	var item=document.getElementById("itemid").value;
  	if(item==null||item==undefined||item.length<1){
  		return;
  	}
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
	var fieldsetid="";
	if((-2)==salaryid){
		fieldsetid= document.getElementById("fieldsetid").value;
	}
	var thecodeurl = "/gz/gz_accounting/iframvartemp.jsp?state="+salaryid+"&fieldsetid="+fieldsetid;
   	var return_vo= window.showModalDialog(thecodeurl,"window2",
   						"dialogWidth:900px;dialogHeight:550px;resizable:no;center:yes;scroll:yes;status:no");
    if(return_vo!=null){
    	var hashvo=new ParameterSet();
    	hashvo.setValue("salaryid",salaryid);
    	hashvo.setValue("fieldsetid",fieldsetid);
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
	standid=standid!=null&&standid.length>0?standid:"";
	document.iframe_user.location.href="/gz/formula/standard.do?b_query=init&opt=edit&standardID="+standid;
}

function addFormula(){
	var salaryid=document.getElementById("salaryid").value;
	var fieldsetid = document.getElementById("fieldsetid").value;
    var thecodeurl ="/gz/gz_accounting/addformula.do?b_query=link&salaryid="+salaryid+"&fieldsetid="+fieldsetid; 
    if(isIE6() ){
        var return_vo= window.showModalDialog(thecodeurl, "", 
              "dialogWidth:330px; dialogHeight:170px;resizable:no;center:yes;scroll:no;status:no");
    }else{
        var return_vo= window.showModalDialog(thecodeurl, "", 
              "dialogWidth:300px; dialogHeight:150px;resizable:no;center:yes;scroll:no;status:no");
    }
	if(return_vo!=null){
    	 var thecodeurl ="/gz/formula/viewformula.do?b_query=link&salaryid="+salaryid+"&itemid="+return_vo; 
    	 window.location.href=thecodeurl;
    }
}
function delProject(itemid){
	var itemid=document.getElementById("item").value;
	var salaryid = document.getElementById("salaryid").value;
	if(itemid==null||itemid.length<1||salaryid==null||salaryid.length<1){
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
	var thecodeurl="/gz/formula/sorting.do?b_query=link&salaryid="+salaryid;
	if(isIE6()){
		var return_vo= window.showModalDialog(thecodeurl, "", 
              "dialogWidth:350px; dialogHeight:400px;resizable:no;center:yes;scroll:yes;status:no");
	}else{
		var return_vo= window.showModalDialog(thecodeurl, "", 
              "dialogWidth:300px; dialogHeight:350px;resizable:no;center:yes;scroll:yes;status:no");
	}
    reflesh();
}
function setcond(){
	var salaryid=document.getElementById("salaryid").value;
	var item=document.getElementById("item").value;
	var fieldsetid = document.getElementById("fieldsetid").value;//为了解决数据采集中有指标不是来自midvariable也不是来自salaryset中  
	var itemname = document.getElementById("itemname").value;
	if(itemname==""){
		alert("请先新增公式！");
		return;
	}
  	var hashvo=new ParameterSet();
	hashvo.setValue("salaryid",salaryid);
	hashvo.setValue("item",item);
	hashvo.setValue("fieldsetid",fieldsetid);
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
	if(itemname==""){
		alert("请先新增公式！");
		return;
	}
	//if(formula == ""){
  //		alert("<bean:message key='gz.formula.formula.not.null'/>");
 // 		return ;
 // 	}else{
  		var hashvo=new ParameterSet();
	    hashvo.setValue("c_expr",getEncodeStr(formula));
	    hashvo.setValue("itemid",itemname);
	    hashvo.setValue("salaryid",salaryid);
	   	var In_paramters="flag=1"; 	
		var request=new Request({method:'post',asynchronous:false,
			parameters:In_paramters,onSuccess:resultCheckExpr,functionId:'3020060020'},hashvo);	
//  	}
}

function resultCheckExpr(outparamters){
  	var info = outparamters.getValue("info");
	if(info=="ok"){
		var formula=document.getElementById("formula").value;
		var cstate=document.getElementById("fieldsetid").value;
		var hashvo=new ParameterSet();

		var salaryid = document.getElementById("salaryid").value;
		hashvo.setValue("salaryid",salaryid);
		
		var itemid = document.getElementById("item").value;
		hashvo.setValue("item",itemid);
		
		hashvo.setValue("formula",getEncodeStr(formula));
		hashvo.setValue("cstate",cstate);
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
	var salaryid=document.getElementById("salaryid").value;
	if(salaryid==-1){
		window.close();
	}else if(salaryid==-2){
		document.location.href="/performance/data_collect/data_collect.do?b_query=link";
	}else{
		document.location.href="/gz/templateset/gz_templatelist.do?b_query=link2";
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
//如果是ie6
function ie6Style(){
	if(isIE6()){
		document.getElementById('tableId').style.cssText="margin-left:-3px;margin-top:-3px;";
	}
}
</script>
<html:form action="/gz/formula/viewformula">
<table id="tableId" width="840" height="350" border="0" align="center" style="margin-left:-3px;margin-top:-5px;">
<tr>
<td>  
<html:hidden name="formulaForm" property="salaryid"/> 
<html:hidden name="formulaForm" property="item"/> 
<html:hidden name="formulaForm" property="itemname"/> 
<html:hidden name="formulaForm" property="fieldsetid"/> 
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
          <td height="280" align="center" valign="top"> 
          <div id="scroll_box" style="margin-bottom: 5px;margin-top: 1px;"><!-- modify by xiaoyun 2014-9-13 -->
            <table width="100%" border="0" class="ListTable">
              <tr class="fixedHeaderTr1">
                <td width="15%" class="TableRow_right common_background_color common_border_color" align="center"><bean:message key="sys.export.status"/></td>
                <td width="65%" class="TableRow_left common_background_color common_border_color" align="center"><bean:message key="kq.shift.relief.name"/></td>
                <logic:notEqual name="formulaForm" property="salaryid" value="-1">
                	<td width="20%" class="TableRow_left common_background_color common_border_color" align="center"><bean:message key="gz.formula.implementation"/></td>
                </logic:notEqual>
              </tr>
              <hrms:paginationdb id="element" name="formulaForm" sql_str="formulaForm.sql" table="" where_str="formulaForm.where" columns="formulaForm.column" order_by="formulaForm.orderby" pagerows="200" page_id="pagination" indexes="indexes">	
				<bean:define id="itemid" name="element" property="itemid"/>
				<bean:define id="hzname" name="element" property="hzname"/>
				<bean:define id="itemname" name="element" property="itemname"/>
			   <tr> 
                <td class="RecordRow_right" align="center" nowrap>
                	<logic:equal name="element" property="useflag" value="1">
                		<input type="checkbox" name="${itemid}" value="1" onclick="changebox(${itemid},'','${itemname}');alertUseFlag(this);" checked/>
                	</logic:equal>
                	<logic:notEqual name="element" property="useflag" value="1">
                		<input type="checkbox" name="${itemid}" value="0" onclick="changebox(${itemid},'','${itemname}');alertUseFlag(this);"/>
                	</logic:notEqual>
                </td>
                <td class="RecordRow_left" onclick="changebox(${itemid},'','${itemname}');">${hzname}</td>
                <logic:notEqual name="formulaForm" property="salaryid" value="-1">
	                <td class="RecordRow_left" onclick="changebox(${itemid},'','${itemname}');">
	                <logic:equal name="element" property="itemtype" value="N">
	                	<logic:equal name="formulaForm" property="salaryid" value="-2">
	                		<html:select name="element" property="runflag" onchange="changeRun(this,${itemid},'${itemname}');" style="width:60px" > 
	    						<html:option value="0"><bean:message key="hmuster.label.expressions"/></html:option>
						</html:select>
	                	</logic:equal>
	                	<logic:notEqual name="formulaForm" property="salaryid" value="-2">
	                		<html:select name="element" property="runflag" onchange="changeRun(this,${itemid},'${itemname}');" style="width:60px" > 
	    					<html:option value="0"><bean:message key="hmuster.label.expressions"/></html:option>
	                		<html:option value="2"><bean:message key="gz.formula.scale"/></html:option>
						</html:select>
	                	</logic:notEqual>
                	</logic:equal>
               		<logic:equal name="element" property="itemtype" value="A">
               			<logic:notEqual name="element" property="codesetid" value="0">
		                	<logic:equal name="formulaForm" property="salaryid" value="-2">
		                		<html:select name="element" property="runflag" onchange="changeRun(this,${itemid},'${itemname}');" style="width:60px" > 
		    						<html:option value="0"><bean:message key="hmuster.label.expressions"/></html:option>
							</html:select>
		                	</logic:equal>
		                	<logic:notEqual name="formulaForm" property="salaryid" value="-2">
		                		<html:select name="element" property="runflag" onchange="changeRun(this,${itemid},'${itemname}');" style="width:60px" > 
		    					<html:option value="0"><bean:message key="hmuster.label.expressions"/></html:option>
		                		<html:option value="1"><bean:message key="gz.formula.standart"/></html:option>
		                		<html:option value="2"><bean:message key="gz.formula.scale"/></html:option>
							</html:select>
		                	</logic:notEqual>
		                </logic:notEqual>
               		</logic:equal>
                	<logic:notEqual name="element" property="itemtype" value="N">
						<logic:equal name="element" property="itemtype" value="A">
							<logic:equal name="element" property="codesetid" value="0">
		                		<logic:equal name="formulaForm" property="salaryid" value="-2">
			                		<html:select name="element" property="runflag" onchange="changeRun(this,${itemid},'${itemname}');" style="width:60px" > 
			    						<html:option value="0"><bean:message key="hmuster.label.expressions"/></html:option>
									</html:select>
				                </logic:equal>
				                <logic:notEqual name="formulaForm" property="salaryid" value="-2">
			                		<html:select name="element" property="runflag" onchange="changeRun(this,${itemid},'${itemname}');" style="width:60px" > 
				    					<html:option value="0"><bean:message key="hmuster.label.expressions"/></html:option>
									</html:select>
								</logic:notEqual>
							</logic:equal>	
						</logic:equal>	
                		<logic:notEqual name="element" property="itemtype" value="A">
	                		<logic:equal name="formulaForm" property="salaryid" value="-2">
		                		<html:select name="element" property="runflag" onchange="changeRun(this,${itemid},'${itemname}');" style="width:60px" > 
		    						<html:option value="0"><bean:message key="hmuster.label.expressions"/></html:option>
							</html:select>
		                	</logic:equal>
		                	<logic:notEqual name="formulaForm" property="salaryid" value="-2">
	                		<html:select name="element" property="runflag" onchange="changeRun(this,${itemid},'${itemname}');" style="width:60px" > 
		    					<html:option value="0"><bean:message key="hmuster.label.expressions"/></html:option>
							</html:select>
						</logic:notEqual>
                	</logic:notEqual>
                	</logic:notEqual>
	                </td>
	            </logic:notEqual>
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
    <table border="0" align="center">
    <tr><td>
    <fieldset align="center" style="width:100%;">
	<legend><bean:message key="kq.wizard.expre"/></legend> 
		<table width="100%" border="0">
        	<tr> 
          		<td colspan="2" align="center"> 
            		<html:textarea name="formulaForm" property="formula"  cols="70" rows="9" styleId="shry"></html:textarea> 
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
          		 <fieldset  align="center" style="width:96%;height=135">
				 <legend><bean:message key='org.maip.reference.projects'/></legend> 
            		<table width="100%" border="0" height="100">
            			<tr height="30">
            				<td>
            					<table width="100%"  border="0" >
              						<tr> 
                						<td height="30" align="right" width="50"><bean:message key="gz.formula.project"/>
											<hrms:optioncollection name="formulaForm" property="itemlist" collection="list"/>
											</td>
											<td>
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
            					<span id="codeview" style="display:none">
            					<table width="100%" border="0" >
              						<tr> 
                						<td height="30"  align="right" width="50"><bean:message key="conlumn.codeitemid.caption"/>
                						</td>
											<td>
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
          		<fieldset align="center" style="width:100%;height=135">
				 <legend><bean:message key="gz.formula.operational.symbol"/></legend> 
					<table width="80%" border="0">
              			<tr> 
              				<td>
              				<table width="100%" border="0">
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
      <fieldset align="center" style="width:97%;">
	  <legend><bean:message key='gz.formula.standart'/></legend> 
      <table border="0" align="center" width="100%" height="345">
    	<tr><td>
    		<table width="100%" border="0" align="center">
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
      <fieldset align="center" style="width:97%;">
	  <legend><bean:message key='gz.formula.scale'/></legend> 
      <table border="0" align="center" width="100%" height="300">
    	<tr><td valign="top">
    		<table width="100%" border="0" align="center">
    			<tr>
    				<td>
    					<iframe id="iframe_rate" name="iframe_rate" width="100%" height="300" src=""></iframe>
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
    			&nbsp;&nbsp;&nbsp;&nbsp;
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

<logic:notEqual name="formulaForm" property="salaryid" value="-1">
 		<center><input type="button"  value="<bean:message key='button.return'/>" onclick="returnup();" Class="mybutton"></center>
</logic:notEqual>
<logic:equal name="formulaForm" property="salaryid" value="-1">
 		<center><input type="button"  value="<bean:message key='button.cancel'/>" onclick="returnup();" Class="mybutton"></center>
</logic:equal>

<script language="javascript">
var itemname = '${formulaForm.itemname}';
defCheck(itemname);
ie6Style();
</script>
</html:form>
