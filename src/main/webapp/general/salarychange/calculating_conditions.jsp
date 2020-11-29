<%@ page contentType="text/html; charset=UTF-8" language="java"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean"%>
<%@ taglib uri="/tags/struts-html" prefix="html"%>
<%@ taglib uri="/tags/struts-logic" prefix="logic"%>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms"%>
<script language="JavaScript" src="/js/validate.js"></script>
<script language="JavaScript" src="/js/function.js"></script>
<%@ page import="com.hrms.struts.valueobject.UserView"%>
<%@ page import="com.hrms.struts.constant.WebConstant"%>
<%

	String bosflag ="";
	UserView userView=(UserView)session.getAttribute(WebConstant.userView);
	if(userView != null)
	{
		bosflag =userView.getBosflag();
	}
%>
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
	if(document.getElementById("conditions").pos!=null){
		if(document.getElementById("conditions").pos.text.length>0){
			document.getElementById("conditions").pos.text+=cal;
		}else{
			document.getElementById("conditions").pos.text=cal;
		}
	}else{
		document.getElementById("conditions").value +=cal;
	}
}
function changeCodeValue(){
  	var item=document.getElementById("itemid").value;
  	var itemid = item.split(":");
    symbol(itemid[1]);
	var in_paramters="itemid="+itemid[0];
    var request=new Request({method:'post',asynchronous:false,parameters:in_paramters,onSuccess:showCodeFieldList,functionId:'3020050012'});
}
function showCodeFieldList(outparamters){
	var codelist=outparamters.getValue("codelist");
	if(codelist.length>1){
		toggles("codeview");
		AjaxBind.bind(setCondForm.codesetid_arr,codelist);
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
function function_Wizard(tableid){
    var thecodeurl ="/org/autostatic/mainp/function_Wizard.do?b_search=link&tableid="+tableid; 
    var return_vo= window.showModalDialog(thecodeurl, "", 
              "dialogWidth:400px; dialogHeight:430px;resizable:no;center:yes;scroll:yes;status:no;");
    if(return_vo!=null)
		symbol(return_vo);
}
function savemula(){
	var formula=document.getElementById("conditions").value;
	formula = getEncodeStr(formula);
	if(formula== ""){
  		window.returnValue = "";
		window.close();
  	}else{
  		var hashvo=new ParameterSet();
	    hashvo.setValue("c_expr",formula);
	    hashvo.setValue("checkflag","2");
	    hashvo.setValue("tabid","${setCondForm.tableid}");
	   	var In_paramters="flag=1"; 	
		var request=new Request({method:'post',asynchronous:false,
			parameters:In_paramters,onSuccess:resultCheckExpr,functionId:'1602010217'},hashvo);				
  	}
}

function resultCheckExpr(outparamters){
  	var info = outparamters.getValue("info");
	if(info=="ok"){
		var conditions = document.getElementById("conditions").value;
		window.returnValue = getEncodeStr(conditions);
		window.close();
	}else{
		alert(getDecodeStr(info));
	}
}	
</script>
<hrms:themes></hrms:themes>
<%	   
if ("hcm".equals(bosflag)){	   
%>
<link href="/general/template/template.css" rel="stylesheet" type="text/css"/>
<%} %>
<html:form action="/general/salarychange/calculating_conditions">
<table width="511" height="350" border="0" align="center" class="formulaconditonformmragin">
<tr>
<td align="center">  
    <fieldset align="center" style="width:100%;">
	<legend><bean:message key='gz.formula.crond.formula.expression'/></legend> 
		<table width="99%" border="0">
        	<tr> 
          		<td colspan="2" align="center"> 
            		<html:textarea name="setCondForm" property="conditions"  onclick="this.pos=document.selection.createRange();"  cols="80" rows="12" styleId="shry"></html:textarea> 
            	</td>
        	</tr>
        	<tr> 
          		<td height="21" colspan="2" align="right"> 
          			<input name="wizard" type="button" id="wizard" value='<bean:message key="kq.formula.function"/>' onclick='function_Wizard("${setCondForm.tableid}");' Class="mybutton"> 
            		<input type="button" value="<bean:message key='org.maip.formula.save.cond'/>" onclick="savemula();" Class="mybutton">&nbsp; 
            	</td>
        	</tr>
        	<tr> 
          		<td width="50%" align="center"> 
          		 <fieldset  align="center" style="width:96%;height=80">
				 <legend><bean:message key='org.maip.reference.projects'/></legend> 
            		<table width="100%" border="0" height="80">
            			<tr height="30">
            				<td>
            					<table width="100%"  border="0" >
              						<tr> 
                						<td height="30"><bean:message key='gz.formula.project'/>
											<html:select name="setCondForm" property="itemid" onchange="changeCodeValue();" style="width:160">
			 									<html:optionsCollection property="itemlist" value="dataValue" label="dataName" />
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
                						<td height="30"><bean:message key='codemaintence.codeitem.id'/>
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
                				<td><input type="button"  value="0" onclick="symbol(0);" class="btn2 common_btn_bg" style="margin-top: -1px;"></td>
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
</script>
</html:form>
