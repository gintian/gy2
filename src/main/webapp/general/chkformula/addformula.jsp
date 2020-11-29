<%@ page contentType="text/html; charset=UTF-8" language="java"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean"%>
<%@ taglib uri="/tags/struts-html" prefix="html"%>
<%@ taglib uri="/tags/struts-logic" prefix="logic"%>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms"%>

<hrms:themes></hrms:themes>
<script language="JavaScript" src="/js/validate.js"></script>
<script language="JavaScript" src="/js/function.js"></script>
<script language="javascript">
function saveFormula(chkflag){
	var tabid = '${chkFormulaForm.tabid}';
	var flag = '${chkFormulaForm.flag}';
	var name = document.getElementById("name").value;
	if(name==null||name.length<1){
		alert(INPUT_FORMULA_NAME+"！");
		return false;
	}
	name=getEncodeStr(name);
	var information = document.getElementById("information").value;
	information=getEncodeStr(information);
	
	var hashvo=new ParameterSet();
	if(chkflag=='alert'){
		var chkid = '${chkFormulaForm.chkid}';
		hashvo.setValue("chkid",chkid);
	}
	
	hashvo.setValue("chkflag",chkflag);
	hashvo.setValue("tabid",tabid);
	hashvo.setValue("flag",flag);
	hashvo.setValue("name",name);
	hashvo.setValue("information",information);
	var request=new Request({method:'post',asynchronous:false,onSuccess:resultCheckExpr,functionId:'1010092013'},hashvo);
} 
function resultCheckExpr(outparamters){
	var info = outparamters.getValue("infor");
	var chkflag = outparamters.getValue("chkflag");
	var chkid = outparamters.getValue("chkid");
	if(info=='ok'){
		if(chkflag=='add'||chkflag=='alert'){
			window.returnValue=chkid;
			window.close();
		}else{
			document.getElementById("information").value="";
			document.getElementById("name").value="";
		}
	}
}
function setTPinput(){
    var InputObject=document.getElementsByTagName("input");
    for(var i=0;i<InputObject.length;i++){
        var InputType=InputObject[i].getAttribute("type");
        if(InputType!=null&&(InputType=="text"||InputType=="password")){
            InputObject[i].className=" "+"TEXT4";
        }
    }
}
	  		 		
</script>
<body onload="setTPinput()">
<html:form action="/general/chkformula/setformula">
<center>
<table width="490px" border="0" align="center">
  <tr> 
    <td valign='top'>
    	<fieldset style="width:100%;height:220">
    	<legend><bean:message key='workdiary.message.check.formula'/></legend>
    	<table width="100%"  border="0" align="center">
    	<tr>
    		<td>
    			<table width="100%"  border="0" align="center">
    				<tr>
    					<td width="70" align="right">&nbsp;</td>
    					<td>&nbsp;</td>
    				</tr>
    				<tr> 
    					<td width="70" align="right"><bean:message key='workdiary.message.formula.name'/>&nbsp;&nbsp;</td>
    					<td><html:text name="chkFormulaForm" property="name" style="width:298px;" /></td>
    				</tr>
    				<tr>
    					<td align="right"><bean:message key='workdiary.message.message'/>&nbsp;&nbsp;</td>
    					<td>
    						<html:textarea name="chkFormulaForm" property="information" cols="46" rows="6"></html:textarea> 
    					</td>
    				</tr>
    			</table>
    		</td>
    	</tr>
		 
    	</table>
    	 </fieldset>
    </td>
  </tr>
  <tr><td>
  
  <table width="100%"  border="0" align="center">
    				 
    				<tr>
    					<td align="center">
    						<logic:equal name="chkFormulaForm" property="chkid" value="no">
    							<input type="button" value="<bean:message key='button.save'/>" onclick="saveFormula('add');" Class="mybutton">
    							<input type="button" value="<bean:message key='button.save'/>&<bean:message key='edit_report.continue'/>" onclick="saveFormula('addmore')" Class="mybutton">
    						</logic:equal>
    						<logic:notEqual name="chkFormulaForm" property="chkid" value="no">
    							<input type="button" value="<bean:message key='button.save'/>" onclick="saveFormula('alert');" Class="mybutton">
    						</logic:notEqual>
    						<input type="button" value="<bean:message key='button.close'/>" onclick="window.close();" Class="mybutton">
    					</td>
    				</tr>
    			</table>
  
  </td></tr>
  
</table>
</center>
</html:form>
</body>