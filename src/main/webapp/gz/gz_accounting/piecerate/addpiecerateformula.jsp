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
	var salaryid = document.getElementById("busiid").value;
	hashvo.setValue("busiid",salaryid);	
	hashvo.setValue("model","saveformula");	
	var request=new Request({method:'post',asynchronous:false,
     	onSuccess:showFieldList,functionId:'3020091050'},hashvo);
}
function showFieldList(outparamters){
	var base=outparamters.getValue("strResult");
	var formulaid=outparamters.getValue("formulaid");
	if(base=='ok'){
		var objlist=new Array();     	
	  	objlist.push(formulaid);
	  	window.returnValue=objlist;
	 	window.close();

	}else{
	if (base=='exists')
	  {
	  	alert('此指标已定义公式，不能重复定义');
	  }
		
	}
}
function returnFormula(){
	window.close();
}
</script>
<hrms:themes />
<html:form action="/gz/gz_accounting/piecerate/search_piecerate_formula">
<center>
<table width="50%"  border="0" align="center">
<tr><td>
<table width="60%"  border="0" align="center">
	<tr>
    	<td><html:hidden name="pieceRateFormulaForm" property="busiid"/></td>
   </tr>
   <tr>
    	<td>
    		<fieldset style="width:100%;height:70">
    		<legend><bean:message key='gz.formula.select.crond.project'/></legend>
    			<table width="100%"  border="0" align="center" height="100%">  				
    				<tr>
    					<td height="100%" valign="middle">
    						<html:select name="pieceRateFormulaForm" property="formulaitemid"  style="width:230">
			 					<html:optionsCollection property="formulaitemlist" value="dataValue" label="dataName" />
							</html:select>
						</td>
    				</tr>
    			</table>
    		</fieldset>
    	</td>
    </tr>
    <tr>
	<tr>
		<td>
		    <table width="100%"  border="0" align="center">
    			<tr>
    				<td align="center">
	    				<input type="button" onclick="saveProject();" value="<bean:message key='options.save'/>" Class="mybutton">
	    				<input type="button" value="<bean:message key='field_result.return'/>" onclick="returnFormula();" Class="mybutton">
    				</td>
    			</tr>
    		</table>
    	</td>
    </tr>
</table>
</td></tr>
</table>
</center>
</html:form>
