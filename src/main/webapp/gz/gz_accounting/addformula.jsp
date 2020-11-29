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
	if(formulaitemid==null||formulaitemid==undefined||formulaitemid==''){
		alert(SELECT_COND_PROJECT+"!");
		return false;
	}
	
	hashvo.setValue("formulaitemid",formulaitemid);
	hashvo.setValue("fieldsetid","${formulaForm.fieldsetid}");
	var salaryid = document.getElementById("salaryid").value;
	hashvo.setValue("salaryid",salaryid);
	
	var request=new Request({method:'post',asynchronous:false,
     	onSuccess:showFieldList,functionId:'3020060005'},hashvo);
}
function showFieldList(outparamters){
	var base=outparamters.getValue("base");
	if(base!='no'){
		window.returnValue=base;
		window.close();
	}else{
		alert("<bean:message key='gz.formula.project.add.formula.failure'/>");;
	}
}
</script>
<html:form action="/gz/gz_accounting/addformula">
<center>
<table width="290px;"  border="0" align="center" style="margin-left:-3px;">
<html:hidden name="formulaForm" property="salaryid"/>

   <tr>
    	<td align="center">
    		<fieldset style="width:290px;height:90px">
    		<legend><bean:message key='gz.formula.select.crond.project1'/></legend>
    			<table width="80%"  border="0" align="center">
<br>
    				<tr>
    					<td>
    						<html:select name="formulaForm" property="formulaitemid"  style="width:250">
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
    				<td align="right"><input type="button" onclick="saveProject();" value="<bean:message key='options.save'/>" Class="mybutton"></td>
    				<td align="left"><input type="button" value="<bean:message key='button.close'/>" onclick="window.close();" Class="mybutton"></td>
    			</tr>
    		</table>
    	</td>
    </tr>
</table>
</center>
</html:form>
