<%@ page contentType="text/html; charset=UTF-8" language="java"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean"%>
<%@ taglib uri="/tags/struts-html" prefix="html"%>
<%@ taglib uri="/tags/struts-logic" prefix="logic"%>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms"%>
<%@ page import="com.hjsj.hrms.actionform.gz.premium.PremiumParamForm,
				org.apache.commons.beanutils.LazyDynaBean,
				java.util.*" %>
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
    height: 215px;    
    width: 270px;            
    overflow: auto;            
    margin: 1em 1;
}
</style>
<hrms:themes />
<script language="javascript">
 function function_Wizard(setid,formula){
   var fmode ="${premiumParamForm.fmode}";
    var thecodeurl ="/org/autostatic/mainp/function_Wizard.do?b_query=link&infor=2&tableid=&setid="+setid+"&fmode="+fmode; 
        var return_vo= window.showModalDialog(thecodeurl, "", 
              "dialogWidth:400px; dialogHeight:430px;resizable:no;center:yes;scroll:yes;status:no");
    if(return_vo!=null)
		symbol(formula,return_vo);
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

function changebox(checkvalue,smode,itemname){
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
	var setid = document.getElementById("setid").value
	var hashvo=new ParameterSet();
	hashvo.setValue("setid",setid);
	hashvo.setValue("itemid",checkvalue);
	hashvo.setValue("useflag",useflag);
	hashvo.setValue("smode",smode);
	
	
	var request=new Request({asynchronous:false,onSuccess:getformulavalue,functionId:'3020131008'},hashvo);
	checkvalue=checkvalue+"";
	if(checkvalue!=null&&checkvalue.length>0){
		tr_bgcolor(checkvalue);
	}
}
function getformulavalue(outparamters){
	var formulavalue= outparamters.getValue("formulavalue");
	var runflag = outparamters.getValue("runflag");
	var standid = outparamters.getValue("standid");
	var smode = outparamters.getValue("smode");
	
		document.getElementById("formula").value=formulavalue;
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
    var request=new Request({method:'post',asynchronous:false,parameters:in_paramters,onSuccess:showCodeFieldList,functionId:'3020131009'});
}
function settemp(){
	var setid=document.getElementById("setid").value;
	var thecodeurl = "/gz/gz_accounting/iframvartemp.jsp?state="+setid;
   	var return_vo= window.showModalDialog(thecodeurl,"window2",
   						"dialogWidth:750px;dialogHeight:530px;resizable:no;center:yes;scroll:yes;status:no");
    if(return_vo!=null){
    	var hashvo=new ParameterSet();
    	hashvo.setValue("setid",setid);
    	hashvo.setValue("itemid",return_vo);
    	var request=new Request({method:'post',asynchronous:false,
     		onSuccess:setItemList,functionId:'3020131011'},hashvo);
    }
}
function setItemList(outparamters){
	var itemlist=outparamters.getValue("itemlist");
	var itemid = outparamters.getValue("itemid");
	if(itemlist.length>0){
		AjaxBind.bind(premiumParamForm.itemid,itemlist);
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
		AjaxBind.bind(premiumParamForm.codesetid_arr,codelist);
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
function defCheck(itemname){
var item = "${premiumParamForm.item}";
	//var smode = "${premiumParamForm.smode}";
	//if(item==null||item.length<1){
	//	var tablevos=document.getElementsByTagName("input");
	//	for(var i=0;i<tablevos.length;i++){
	//    	if(tablevos[i].type=="checkbox"){
	 //   		item=tablevos[i].name;
	 //   		break;
	//		}
    //	}
   // }
    changebox(item,'',itemname);
}
function linkScale(taxid){
	var setid=document.getElementById("setid").value;
	var itemid=document.getElementById("item").value;
	document.iframe_rate.location.href="/gz/templateset/tax_table/initTaxDetailTable.do?b_init=init&taxid="+taxid+"&setid="+setid+"&itemid="+itemid;
}
function linkIframe(standid){
	standid=standid!=null&&standid.length>0?standid:"";
	document.iframe_user.location.href="/gz/formula/standard.do?b_query=init&opt=edit&standardID="+standid;
}

function addFormula(){
	var setid=document.getElementById("setid").value;
    var thecodeurl ="/gz/premium/param/addformula.do?b_query=link&setid="+setid; 
    var return_vo= window.showModalDialog(thecodeurl, "", 
              "dialogWidth:400px; dialogHeight:200px;resizable:no;center:yes;scroll:yes;status:no");
	if(return_vo!=null){
		var fmode ="${premiumParamForm.fmode}";
		var thecodeurl ="/gz/premium/param/formula.do?b_count=link&fmode="+fmode+"&itemid="+return_vo; 
    	// var thecodeurl ="/gz/formula/viewformula.do?b_query=link&setid="+setid+"&itemid="+return_vo; 
    	 window.location.href=thecodeurl;
    }
}
function checkfieldid(){

	var fielditemid='';
	var a_IDs=eval("document.forms[0].IDs");	
	var b=0;
			for(var i=0;i<document.forms[0].elements.length;i++)
			{			
				if(document.forms[0].elements[i].type=='checkbox'&&document.forms[0].elements[i].name!='selbox')
		   		{		   			
		   		if(document.forms[0].elements[i].name!='selbox'&&document.forms[0].elements[i].checked==true)
			   			{
			   				if(a_IDs.length!=undefined){
			   				fielditemid+=a_IDs[b].value+",";	
			   			
			   				}else{
			   				if(document.getElementById("item").value!='')
			   				fielditemid=document.getElementById("item").value;
			   				break;
			   				}	
			   							
						}
							b++;	
		   		}
			}
	
		if(fielditemid!=null){
			return fielditemid.toUpperCase();
		}else{
			return;
		}
	}

function delProject(){
	//var itemid=checkfieldid();
	var itemid = document.getElementById("item").value;
	var setid = document.getElementById("setid").value;
	if(itemid==null||itemid==''||itemid.length<1||setid==null||setid.length<1){
		return;
	}
	 if(!ifdel()){
    	return ;
    }
	var hashvo=new ParameterSet();
	hashvo.setValue("setid",setid);
	hashvo.setValue("itemid",itemid);
	hashvo.setValue("fmode","${premiumParamForm.fmode}");
	var request=new Request({method:'post',asynchronous:false,
     	onSuccess:del,functionId:'3020131014'},hashvo);
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
	var fmode ="${premiumParamForm.fmode}";
    var thecodeurl ="/gz/premium/param/formula.do?b_count=link&fmode="+fmode; 
    window.location.href=thecodeurl;
} 
function sorting(){
	var setid=document.getElementById("setid").value;
	document.location.href="/gz/premium/param/sortformula.do?b_query=link&setid="+setid;
}
function setcond(){
	var setid=document.getElementById("setid").value;
	var item=document.getElementById("item").value;
  	var hashvo=new ParameterSet();
	hashvo.setValue("setid",setid);
	hashvo.setValue("item",item);
	//hashvo.setValue("fmode","${premiumParamForm.fmode}");
	var In_paramters="flag=1"; 	
	var request=new Request({method:'post',asynchronous:false,
		parameters:In_paramters,onSuccess:getcond,functionId:'3020131012'},hashvo);				
}
function getcond(outparamters){
	var conditions = outparamters.getValue("conditions");
	var setid=document.getElementById("setid").value;
	var cond = condiTions(conditions,setid);
	if(cond!=null){
		savecond(cond);
	}
}
function condiTions(formula,id){
    var thecodeurl ="/gz/premium/param/calculating_conditions.do?b_query=link&conditions="+formula+"&id="+id;
    /* modify by xiaoyun 2014-8-30 start */  
    /*
    var return_vo= window.showModalDialog(thecodeurl, "", 
              "dialogWidth:520px; dialogHeight:440px;resizable:no;center:yes;scroll:yes;status:no");*/ 
    var return_vo= window.showModalDialog(thecodeurl, "", 
              "dialogWidth:520px; dialogHeight:440px;resizable:no;center:yes;scroll:yes;status:no");
    /* modify by xiaoyun 2014-8-30 end */     
    return return_vo;  
}
function savecond(cond){
	var hashvo=new ParameterSet();
	var setid = document.getElementById("setid").value;
	hashvo.setValue("setid",setid);
		
	var itemid = document.getElementById("item").value;
	hashvo.setValue("item",itemid);
		
	hashvo.setValue("conditions",cond);
	var request=new Request({method:'post',asynchronous:false,functionId:'3020131013'},hashvo);
}
function savemula(){
	var formula=trim(document.getElementById("formula").value);
	var itemid = document.getElementById("item").value;
	if(itemid==null||itemid==""){
	alert("请选择指标项");
	return;
	}
	if(formula==""){
	alert("表达式不能为空!");
	return;
	}
	var itemname = document.getElementById("itemname").value;
	var setid = document.getElementById("setid").value;
	//if(formula == ""){
  //		alert("<bean:message key='gz.formula.formula.not.null'/>");
 // 		return ;
 // 	}else{
  		var hashvo=new ParameterSet();
	    hashvo.setValue("c_expr",getEncodeStr(formula));
	    hashvo.setValue("itemid",itemname);
	    hashvo.setValue("setid",setid);
	   	var In_paramters="flag=1"; 	
		var request=new Request({method:'post',asynchronous:false,
			parameters:In_paramters,onSuccess:resultCheckExpr,functionId:'3020131005'},hashvo);	
//  	}
}

function resultCheckExpr(outparamters){
  	var info = outparamters.getValue("info");
	if(info=="ok"){
		var formula=document.getElementById("formula").value;

		var hashvo=new ParameterSet();

		var setid = document.getElementById("setid").value;
		hashvo.setValue("setid",setid);
		hashvo.setValue("fmode","${premiumParamForm.fmode}");
		var itemid = document.getElementById("item").value;
		hashvo.setValue("item",itemid);
		
		hashvo.setValue("formula",getEncodeStr(formula));
	
		var request=new Request({method:'post',asynchronous:false,functionId:'3020131006'},hashvo);
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
	var setid=document.getElementById("setid").value;
	var itemid = document.getElementById("item").value;
    var thecodeurl ="/gz/formula/selectstandard.do?b_query=link&itemname="+itemname+"&setid="+setid+"&item="+itemid; 
    var return_vo= window.showModalDialog(thecodeurl, "", 
              "dialogWidth:400px; dialogHeight:230px;resizable:no;center:yes;scroll:yes;status:no");
    if(return_vo!=null){
  	 	return return_vo;
  	}
}
function selectScale(){
	var setid=document.getElementById("setid").value;
	var itemid = document.getElementById("item").value;
    var thecodeurl ="/gz/formula/selectScale.do?b_query=link&setid="+setid+"&item="+itemid; 
    var return_vo= window.showModalDialog(thecodeurl, "", 
              "dialogWidth:400px; dialogHeight:240px;resizable:no;center:yes;scroll:yes;status:no");
    if(return_vo!=null){
  	 	return return_vo;
  	}else{
  		return "";
  	}
}
function returnup(){
 parent.location.href="/gz/premium/param.do?b_query=link";
}
function alertUseFlag(obj){
	var setid=document.getElementById("setid").value;
	var itemid = document.getElementById("item").value;
	var useflag="0";
    if(obj.checked){
    	var useflag="1";
    }
    var hashvo=new ParameterSet();
    hashvo.setValue("setid",setid);
    hashvo.setValue("itemid",itemid);
    hashvo.setValue("useflag",useflag);
    var request=new Request({method:'post',asynchronous:false,functionId:'3020131007'},hashvo); 
}
</script>
<html:form action="/gz/premium/param/formula">
<table width="750" height="250" border="0" align="center">
<tr>
<td>  
<html:hidden name="premiumParamForm" property="setid"/>
<html:hidden name="premiumParamForm" property="item"/> 
<html:hidden name="premiumParamForm" property="itemname"/> 
<table width="100%" height="250" border="0" align="center">
  <tr> 
    <td width="40%" height="200" align="center">
    <fieldset align="center" style="width:100%;height: 333px;">
	<legend><bean:message key="gz.formula.list.table"/></legend> 
      <table width="100%" height="200" border="0">
        <tr > 
          <td height="200" align="center" valign="top"> 
          <div id="scroll_box" style="margin-bottom: 5px;">
            <table width="100%" border="0" class="" cellspacing="0">
              <tr class="fixedHeaderTr"> 
                <td width="15%" class="TableRow" align="center" style="border-left: none;border-top: none;"><bean:message key="sys.export.status"/></td>
                <td width="65%" class="TableRow" align="center" style="border-left:none;border-right: none;border-top: none;"><bean:message key="kq.shift.relief.name"/></td>
               
              </tr>
              <hrms:paginationdb id="element" name="premiumParamForm" sql_str="premiumParamForm.sql" table="" where_str="premiumParamForm.where" columns="premiumParamForm.column" order_by="premiumParamForm.orderby" pagerows="200" page_id="pagination" indexes="indexes">	
				<bean:define id="itemid" name="element" property="itemid"/>
				<bean:define id="hzname" name="element" property="hzname"/>
				<bean:define id="itemname" name="element" property="itemname"/>
				<bean:define id="smode" name="element" property="smode"/>
			   <tr> 
                <td class="RecordRow" style="border-left:none;border-top: none;" align="center" nowrap>
                	<logic:equal name="element" property="useflag" value="1">
                		<input type="checkbox" name="${itemid}" value="1" onclick="changebox(${itemid},'','${itemname}');alertUseFlag(this);" checked/>
                	</logic:equal>
                	<logic:notEqual name="element" property="useflag" value="1">
                		<input type="checkbox" name="${itemid}" value="0" onclick="changebox(${itemid},'','${itemname}');alertUseFlag(this);"/>
                	</logic:notEqual>
                </td>
                <td class="RecordRow" style="border-left:none;border-right: none;border-top: none;" onclick="changebox(${itemid},'','${itemname}');" nowrap>${hzname}
                  <input type="hidden" name="IDs" value="${itemid}" />
                </td>
          
              </tr>
			  </hrms:paginationdb>
            </table>
            </div>
            <span style="margin-top: 10px;">
            <input  type="button"  value="<bean:message key='button.new.add'/>" onclick="addFormula();" Class="mybutton">
            <input type="button"  value="<bean:message key='button.delete'/>" onclick="delProject();" Class="mybutton">
            <input  type="button"  value="<bean:message key='kq.item.change'/>" onclick="sorting();" Class="mybutton">
            </span>
          </td>
        </tr>
        <tr>
          <td height="10" align="center"> 
          </td>
        </tr>
      </table> 
      </fieldset>
    </td>
    <td width="60%" align="center">
    <span id="expression">
    <table border="0" align="center">
    <tr><td>
    <fieldset align="center" style="width:100%;height: 333px;">
	<legend><bean:message key="kq.wizard.expre"/></legend> 
		<table width="100%" border="0">
        	<tr> 
          		<td colspan="2" align="center"> 
            		<html:textarea name="premiumParamForm" property="formula"  cols="55" rows="7" styleId="shry"></html:textarea> 
            	</td>
        	</tr>
        	<tr> 
          		<td height="21" colspan="2" align="right">
          			<input name="wizard" type="button" id="wizard" value='<bean:message key="kq.formula.function"/>' onclick="function_Wizard('${premiumParamForm.setid}','formula');" Class="mybutton"> 
            		<input type="button" value="<bean:message key='gz.formula.calculation.conditions'/>" onclick="setcond();" Class="mybutton"> 
            		<input type="button" value="<bean:message key='org.maip.formula.preservation'/>" onclick="savemula();" Class="mybutton">&nbsp; 
            	</td>
        	</tr>
        	<tr> 
         <td width="52%" align="center"> 
          		 <fieldset  align="center" style="width:96%;height=125">
				 <legend><bean:message key='org.maip.reference.projects'/></legend> 
            		<table width="100%" border="0" height="100">
            			<tr height="30">
            				<td>
            					<table width="100%"  border="0" >
              						<tr> 
                						<td height="30">&nbsp;&nbsp;项&nbsp;&nbsp;目&nbsp;&nbsp;
											<hrms:optioncollection name="premiumParamForm" property="itemlist" collection="list"/>
											<html:select name="premiumParamForm" property="itemid" onchange="changeCodeValue();" style="width:140">
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
                						<td height="30">&nbsp;&nbsp;代&nbsp;&nbsp;码&nbsp;&nbsp;
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
                				<td><input type="button"  value="<bean:message key='general.mess.or'/>" onclick="symbol('formula','<bean:message key='general.mess.or'/>');" class="btn1 common_btn_bg "> </td>
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
    </td>
  </tr>
</table>
</td>
</tr>
</table>
<center><input type="button"  value="<bean:message key='button.return'/>" onclick="returnup();" Class="mybutton" style="margin-top: -5px;"></center>
<script language="javascript">
var itemname = '${premiumParamForm.itemname}';
defCheck(itemname);
</script>
 
   

</html:form>
