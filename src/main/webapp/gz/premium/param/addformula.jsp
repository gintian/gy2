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
	
	var setid = document.getElementById("setid").value;
	var fmode = "${premiumParamForm.fmode}";
	hashvo.setValue("setid",setid);
	hashvo.setValue("fmode",fmode);
	
	var request=new Request({method:'post',asynchronous:false,
     	onSuccess:showFieldList,functionId:'3020131016'},hashvo);
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
<html:form action="/gz/premium/param/addformula">
<br>
<center>
<table width="90%"  border="0" align="center">
	<tr>
    	<td><html:hidden name="premiumParamForm" property="setid"/></td>
   </tr>
   <tr>
    	<td align="center">
    		<fieldset style="width:80%;height:70">
    		<legend><bean:message key='gz.formula.select.crond.project1'/></legend>
    			<table width="80%"  border="0" align="center">  	
    				<tr></tr>						
    				<tr>
    					<td>
    						<html:select name="premiumParamForm" property="formulaitemid"  style="width:230;margin-top:4px;">
			 					<html:optionsCollection property="formulaitemlist" value="dataValue" label="dataName" />
							</html:select>
						</td>
    				</tr>
    				<tr></tr>
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
