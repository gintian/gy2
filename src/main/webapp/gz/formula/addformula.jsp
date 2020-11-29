<%@ page contentType="text/html; charset=UTF-8" language="java"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean"%>
<%@ taglib uri="/tags/struts-html" prefix="html"%>
<%@ taglib uri="/tags/struts-logic" prefix="logic"%>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms"%>

<link rel="stylesheet" href="/css/css1.css" type="text/css">
<hrms:themes />
<script language="JavaScript" src="/js/validate.js"></script>
<script language="JavaScript" src="/js/function.js"></script>
<script language="javascript">
function saveProject(){
	var hashvo=new ParameterSet();
	var formulaitemid = document.getElementById("formulaitemid").value;
	hashvo.setValue("formulaitemid",formulaitemid);
	
	var salaryid = document.getElementById("salaryid").value;
	hashvo.setValue("salaryid",salaryid);
	
	var request=new Request({method:'post',asynchronous:false,
     	onSuccess:showFieldList,functionId:'3020060005'},hashvo);
}
function showFieldList(outparamters){
	var base=outparamters.getValue("base");
	if(base=='ok'){
		returnFormula();
	}else{
		alert("<bean:message key='gz.formula.project.add.formula.failure'/>");;
	}
}
function returnFormula(){
	var salaryid = document.getElementById("salaryid").value;
	window.location.href="/gz/formula/viewformula.do?b_query=link&salaryid="+salaryid;
}
</script>
<html:form action="/gz/formula/addformula">
<center>
<table width="50%"  border="0" align="center">
<tr><td height="100">&nbsp;</td></tr>
<tr><td>
<table width="60%"  border="0" align="center">
	<tr>
    	<td><html:hidden name="formulaForm" property="salaryid"/></td>
   </tr>
   <tr>
    	<td>
    		<fieldset style="width:100%;height:70">
    		<legend><bean:message key='gz.formula.select.crond.project'/></legend>
    			<table width="100%"  border="0" align="center">
    				<tr>
    					<td>&nbsp;</td>
    				</tr>
    				<tr>
    					<td>
    						<html:select name="formulaForm" property="formulaitemid"  style="width:230">
			 					<html:optionsCollection property="formulaitemlist" value="dataValue" label="dataName" />
							</html:select>
						</td>
    				</tr>
    			</table>
    		</fieldset>
    	</td>
    </tr>
	<tr>
		<td>
		    <table width="100%"  border="0" align="center">
    			<tr>
    				<td align="center"><input type="button" onclick="saveProject();" value="<bean:message key='options.save'/>" Class="mybutton"></td>
    				<td><input type="button" value="<bean:message key='field_result.return'/>" onclick="returnFormula();" Class="mybutton"></td>
    			</tr>
    		</table>
    	</td>
    </tr>
</table>
</td></tr>
</table>
</center>
</html:form>
