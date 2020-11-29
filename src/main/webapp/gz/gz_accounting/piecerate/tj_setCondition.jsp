<%@ page contentType="text/html; charset=UTF-8" language="java"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean"%>
<%@ taglib uri="/tags/struts-html" prefix="html"%>
<%@ taglib uri="/tags/struts-logic" prefix="logic"%>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms"%>

<link rel="stylesheet" href="/css/css1.css" type="text/css">
<script language="JavaScript" src="/js/validate.js"></script>
<script language="JavaScript" src="/js/function.js"></script>
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
function symbol(cal){
	if(document.getElementById("tjWhere").pos!=null){
		if(document.getElementById("tjWhere").pos.text.length>0){
			document.getElementById("tjWhere").pos.text+=cal;
		}else{
			document.getElementById("tjWhere").pos.text=cal;
		}
	}else{
		document.getElementById("tjWhere").value +=cal;
	}
}

function getCondItemid(){
	var itemid="";
	var itemid_arr= document.getElementsByName("cond_itemList");
	var itemid_arr_vo = itemid_arr[0];
	if(itemid_arr==null){
		return "";
	}else{
		for(var i=0;i<itemid_arr_vo.options.length;i++){
			if(itemid_arr_vo.options[i].selected){
				itemid =itemid_arr_vo.options[i].value;
				continue;
		     }
	    }
     return itemid;
   }
}


function changeCodeValue(){
   var itemid=getCondItemid().split(":");
    if(itemid==null||itemid==undefined||itemid.length<2){
    	return;
    }
    symbol(itemid[1]);
	var in_paramters="itemid="+itemid[0];
    var request=new Request({method:'post',asynchronous:false,parameters:in_paramters,onSuccess:showCodeFieldList,functionId:'3020050012'});
}
function showCodeFieldList(outparamters){	
	var codelist=outparamters.getValue("codelist");
	if(codelist.length>1){
		toggles("codeview");
		AjaxBind.bind(pieceRateTjDefineForm.codesetid_arr,codelist);
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
		symbol("\""+codeid+"\"");
	}
} 
function function_Wizard(){
    var thecodeurl ="/org/autostatic/mainp/function_Wizard.do?b_query=link"; 
    var return_vo= window.showModalDialog(thecodeurl, "", 
              "dialogWidth:400px; dialogHeight:400px;resizable:no;center:yes;scroll:yes;status:no");
    if(return_vo!=null){
  	 	symbol(return_vo);
  	}else{
  		return ;
  	}
}

function CondChangeSet(formula_id){
	hides("codeview");
	
  	var item=document.getElementById("cond_setId").value;
  	var itemid = item.split(":");
  	var fieldsetid= itemid[0];
    if(fieldsetid==null||fieldsetid==undefined||fieldsetid.length<2){
    	return;
    }
	var hashvo=new ParameterSet();
	hashvo.setValue("setId",fieldsetid);	
	hashvo.setValue("flag","getFieldItemList");	
	
	var request=new Request({method:'post',asynchronous:false,onSuccess:showCondItemList,functionId:'3020091066'},hashvo);	
}

function showCondItemList(outparamters){	
	var itemlist=outparamters.getValue("cond_itemList");
	AjaxBind.bind(pieceRateTjDefineForm.cond_itemList,itemlist);	
}

function savecond(){
	var cond=document.getElementById("tjWhere").value;
	cond = getEncodeStr(cond);
	cond=cond!=null?cond:"";
  	var hashvo=new ParameterSet();
	hashvo.setValue("cond",cond);
	hashvo.setValue("flag","checkTjWhere");
	var In_paramters=""; 	
	var request=new Request({method:'post',asynchronous:false,
			parameters:In_paramters,onSuccess:resultCheckExpr,functionId:'3020091066'},hashvo);				
}

function resultCheckExpr(outparamters){
  	var info = outparamters.getValue("info");
  	var error = outparamters.getValue("error");
	if(info=="true"){
		/* 安全问题 sql-in-url 计件薪资-报表-统计条件 xiaoyun 2014-9-18 start */
		//var conditions = document.getElementById("tjWhere").value;
		var conditions = outparamters.getValue("tjWhere");
		/* 安全问题 sql-in-url 计件薪资-报表-统计条件 xiaoyun 2014-9-18 end */
		window.returnValue = conditions;
		window.close();
	}else{
		alert(getDecodeStr(error));
	}
}	
//如果是ie6
function ie6Style(){
	if(isIE6()){
		document.getElementById('tableId').style.cssText="margin-top:-3px;";
	}
}
</script>
<html:form action="/gz/gz_accounting/piecerate/piecerate_tj_def">
<table id="tableId" width="98%" height="100%" border="0" align="center" style="margin-top: -5px;">
<tr>
<td valign="top">  
    <fieldset align="center" style="width:100%;">
    	<legend>统计条件</legend> 
		<table width="99%" border="0">
        	<tr> 
          		<td colspan="2" align="center"> 
            		<html:textarea name="pieceRateTjDefineForm" property="tjWhere"  onclick="this.pos=document.selection.createRange();"  style="width:500px;" rows="6" styleId="shry"></html:textarea> 
            	</td>
        	</tr>
        	<tr> 
          		<td height="20" colspan="2" align="right"> 
          			<input name="wizard" type="button" id="wizard" value='<bean:message key="kq.formula.function"/>' onclick="function_Wizard();" Class="mybutton"> 
            		<input type="button" onclick="savecond();" value="<bean:message key='button.ok'/>" Class="mybutton">
    				<input type="button" value="<bean:message key='button.cancel'/>" onclick="window.close();" Class="mybutton">
            	</td>
        	</tr>
        	<tr height=100> 
          		<td width="50%" align="center"> 
          		 <fieldset  align="center" style="width:96%;height=80">
				 <legend><bean:message key='org.maip.reference.projects'/></legend> 
            		<table width="100%" border="0" height="80">
            		      <tr height="25">
            				<td>
            			     <span id="condsetview" >
            					<table width="100%"  border="0" >
              						<tr> 
                						<td ><bean:message key='field_result.fieldset'/>
											<html:select name="pieceRateTjDefineForm" property="cond_setId" onchange="CondChangeSet();" style="width:160">
			 									<html:optionsCollection property="cond_setList" value="dataValue" label="dataName" />
											</html:select>
                 						</td>
              						</tr>
            					</table>
            				  </span>	
            				</td>
            			</tr>
            			<tr height="25">
            				<td>
            					<table width="100%"  border="0" >
              						<tr> 
                						<td><bean:message key='field_result.fielditem'/>
										   <select name="cond_itemList" onchange="changeCodeValue();"  style="width:160;font-size:9pt">
             								</select>
                 						</td>
              						</tr>
            					</table>
            				</td>
            			</tr>
            			<tr height="25">
            				<td>
            					<span id="codeview">
            					<table width="100%" border="0" >
              						<tr> 
                						<td><bean:message key='gz.acount.code.project'/>
											<select name="codesetid_arr" onchange="getCodesid();"  style="width:160;font-size:9pt">
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
          		<td width="50%">
          		<fieldset align="center" style="width:100%;">
				 <legend><bean:message key='gz.formula.operational.symbol'/></legend> 
					<table width="100%" border="0">
              			<tr> 
              				<td>
              				<table width="100%" border="0">
              				<tr>
                				<td><input type="button"  value="0" onclick="symbol(0);" class="btn2 common_btn_bg"></td>
                				<td><input type="button"  value="1" onclick="symbol(1);" class="btn2 common_btn_bg"> </td>
                				<td><input type="button"  value="2" onclick="symbol(2);" class="btn2 common_btn_bg"> </td>
                				<td><input type="button"  value="3" onclick="symbol(3);" class="btn2 common_btn_bg"> </td>
                				<td><input type="button"  value="4" onclick="symbol(4);" class="btn2 common_btn_bg"> </td>
                				<td><input type="button"  value="+" onclick="symbol('+');" class="btn2 common_btn_bg"> </td>
                				<td><input type="button"  value="*" onclick="symbol('*');" class="btn2 common_btn_bg"> </td>
                				<td><input type="button"  value="\" onclick="symbol('\\');" class="btn2 common_btn_bg"> </td>
                				<td><input type="button"  value="<bean:message key='general.mess.and'/>" onclick="symbol('<bean:message key='general.mess.and'/>');" class="btn1 common_btn_bg"> </td>
                				<td><input type="button"  value="<bean:message key='kq.wizard.not'/>" onclick="symbol('<bean:message key='kq.wizard.not'/>');" class="btn1 common_btn_bg"> </td>
              				</tr>
              				<tr> 
                				<td><input type="button"  value="5" onclick="symbol(5);" class="btn2 common_btn_bg"> </td>
                				<td><input type="button"  value="6" onclick="symbol(6);" class="btn2 common_btn_bg"> </td>
                				<td><input type="button"  value="7" onclick="symbol(7);" class="btn2 common_btn_bg"> </td>
                				<td><input type="button"  value="8" onclick="symbol(8);" class="btn2 common_btn_bg"> </td>
                				<td><input type="button"  value="9" onclick="symbol(9);" class="btn2 common_btn_bg"> </td>
                				<td><input type="button"  value="-" onclick="symbol('-');" class="btn2 common_btn_bg"> </td>
                				<td><input type="button"  value="/" onclick="symbol('/');" class="btn2 common_btn_bg"> </td>
                				<td><input type="button"  value="%" onclick="symbol('%');" class="btn2 common_btn_bg"> </td>
                				<td><input type="button"  value="<bean:message key='kq.wizard.and'/>" onclick="symbol('<bean:message key='kq.wizard.and'/>');" class="btn1 common_btn_bg"> </td>
                				<td><input type="button"  value="~" onclick="symbol('~');" class="btn2 common_btn_bg"> </td>
              				</tr>
              				<tr> 
              					<td><input type="button"  value="(" onclick="symbol('(');" class="btn2 common_btn_bg"> </td>
              					<td><input type="button"  value=")" onclick="symbol(')');" class="btn2 common_btn_bg"> </td>
               		 			<td><input type="button"  value="=" onclick="symbol('=');" class="btn2 common_btn_bg"> </td>
                				<td><input type="button"  value="&gt;" onclick="symbol('&gt;');" class="btn2 common_btn_bg"> </td>
                				<td><input type="button"  value="&lt;" onclick="symbol('&lt;');" class="btn2 common_btn_bg"> </td>
                				<td><input type="button"  value="&lt;&gt;" onclick="symbol('&lt;&gt;');" class="btn1 common_btn_bg"> </td>
                				<td><input type="button"  value="&lt;=" onclick="symbol('&lt;=');"class="btn1 common_btn_bg"> </td>
                				<td><input type="button"  value="&gt;=" onclick="symbol('&gt;=');"class="btn1 common_btn_bg"> </td>
                				<td colspan="2"><input type="button"  value="<bean:message key='kq.wizard.contain'/>" onclick="symbol('Like');" class="btn3 common_btn_bg"></td>
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
<script language="javascript">
hides("codeview");
ie6Style();
</script>
</html:form>
