<%@ page contentType="text/html; charset=UTF-8" language="java"%>
<%@page import="com.hrms.struts.valueobject.UserView"%>
<%@page import="com.hrms.struts.constant.WebConstant"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean"%>
<%@ taglib uri="/tags/struts-html" prefix="html"%>
<%@ taglib uri="/tags/struts-logic" prefix="logic"%>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms"%>

<link rel="stylesheet" href="/css/css1.css" type="text/css">
<script language="JavaScript" src="/js/validate.js"></script>
<script language="JavaScript" src="/js/function.js"></script>
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
function savemula(){
	var formula=document.getElementById("conditions").value;
	formula = getEncodeStr(formula)
	formula=formula!=null?formula:"";
  	var hashvo=new ParameterSet();
	hashvo.setValue("c_expr",formula);
	hashvo.setValue("itemsetid","S05");
	var In_paramters=""; 	
	var request=new Request({method:'post',asynchronous:false,
			parameters:In_paramters,onSuccess:resultCheckExpr,functionId:'3020060020'},hashvo);				
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
<html:form action="/gz/gz_accounting/piecerate/search_piecerate_formula">
<html:hidden name="pieceRateFormulaForm" property="busiid"/> 
<table width="100%" height="350" border="0" align="center">
<tr>
<td align="center">  
    <fieldset align="center" style="width:100%;">
	<legend><bean:message key='gz.formula.crond.formula.expression'/></legend> 
		<table width="99%" border="0">
        	<tr> 
          		<td colspan="2" align="center"> 
            		<html:textarea name="pieceRateFormulaForm" property="conditions"  onclick="this.pos=document.selection.createRange();"  cols="65" rows="12" styleId="shry"></html:textarea> 
            	</td>
        	</tr>
        	<tr> 
          		<td height="21" colspan="2" align="right"> 
          			<input name="wizard" type="button" id="wizard" value='<bean:message key="kq.formula.function"/>' onclick="function_Wizard();" Class="mybutton"> 
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
											<html:select name="pieceRateFormulaForm" property="itemid" onchange="changeCodeValue();" style="width:180">
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
											<select name="codesetid_arr" onchange="getCodesid();"  style="width:180;font-size:9pt">
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
          		<fieldset align="center" style="width:100%;height: 102px;">
				 <legend><bean:message key='gz.formula.operational.symbol'/></legend> 
					<table width="100%" border="0">
              			<tr> 
              				<td>
              				<table width="100%" border="0">
              				<tr>
                				<td><input type="button"  value="0" onclick="symbol(0);" class="btn2 smallbutton"></td>
                				<td><input type="button"  value="1" onclick="symbol(1);" class="btn2 smallbutton"> </td>
                				<td><input type="button"  value="2" onclick="symbol(2);" class="btn2 smallbutton"> </td>
                				<td><input type="button"  value="3" onclick="symbol(3);" class="btn2 smallbutton"> </td>
                				<td><input type="button"  value="4" onclick="symbol(4);" class="btn2 smallbutton"> </td>
                				<td><input type="button"  value="+" onclick="symbol('+');" class="btn2 smallbutton"> </td>
                				<td><input type="button"  value="*" onclick="symbol('*');" class="btn2 smallbutton"> </td>
                				<td><input type="button"  value="\" onclick="symbol('\\');" class="btn2 smallbutton"> </td>
                				<td><input type="button"  value="<bean:message key='general.mess.and'/>" onclick="symbol('<bean:message key='general.mess.and'/>');" class="btn1 smallbutton"> </td>
                				<td><input type="button"  value="<bean:message key='kq.wizard.not'/>" onclick="symbol('<bean:message key='kq.wizard.not'/>');" class="btn1 smallbutton"> </td>
              				</tr>
              				<tr> 
                				<td><input type="button"  value="5" onclick="symbol(5);" class="btn2 smallbutton"> </td>
                				<td><input type="button"  value="6" onclick="symbol(6);" class="btn2 smallbutton"> </td>
                				<td><input type="button"  value="7" onclick="symbol(7);" class="btn2 smallbutton"> </td>
                				<td><input type="button"  value="8" onclick="symbol(8);" class="btn2 smallbutton"> </td>
                				<td><input type="button"  value="9" onclick="symbol(9);" class="btn2 smallbutton"> </td>
                				<td><input type="button"  value="-" onclick="symbol('-');" class="btn2 smallbutton"> </td>
                				<td><input type="button"  value="/" onclick="symbol('/');" class="btn2 smallbutton"> </td>
                				<td><input type="button"  value="%" onclick="symbol('%');" class="btn2 smallbutton"> </td>
                				<td><input type="button"  value="<bean:message key='kq.wizard.and'/>" onclick="symbol('<bean:message key='kq.wizard.and'/>');" class="btn1 smallbutton"> </td>
                				<td><input type="button"  value="~" onclick="symbol('~');" class="btn2 smallbutton"> </td>
              				</tr>
              				<tr> 
              					<td><input type="button"  value="(" onclick="symbol('(');" class="btn2 smallbutton"> </td>
              					<td><input type="button"  value=")" onclick="symbol(')');" class="btn2 smallbutton"> </td>
               		 			<td><input type="button"  value="=" onclick="symbol('=');" class="btn2 smallbutton"> </td>
                				<td><input type="button"  value="&gt;" onclick="symbol('&gt;');" class="btn2 smallbutton"> </td>
                				<td><input type="button"  value="&lt;" onclick="symbol('&lt;');" class="btn2 smallbutton"> </td>
                				<td><input type="button"  value="&lt;&gt;" onclick="symbol('&lt;&gt;');" class="btn1 smallbutton"> </td>
                				<td><input type="button"  value="&lt;=" onclick="symbol('&lt;=');"class="btn1 smallbutton"> </td>
                				<td><input type="button"  value="&gt;=" onclick="symbol('&gt;=');"class="btn1 smallbutton"> </td>
                				<td colspan="2"><input type="button"  value="<bean:message key='kq.wizard.contain'/>" onclick="symbol('Like');" class="btn3 smallbutton"></td>
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
