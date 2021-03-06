<%@ page contentType="text/html; charset=UTF-8" language="java"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean"%>
<%@ taglib uri="/tags/struts-html" prefix="html"%>
<%@ taglib uri="/tags/struts-logic" prefix="logic"%>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms"%>

<link rel="stylesheet" href="/css/css1.css" type="text/css">
<script language="JavaScript" src="/js/validate.js"></script>
<script language="JavaScript" src="/js/function.js"></script>
<style type="text/css">
.btn3 {
 BORDER-RIGHT: #C0C0C0 1px solid;
 BORDER-TOP: #C0C0C0 1px solid; 
 PADDING-LEFT: 0px; FONT-SIZE: 12px; 
 BORDER-LEFT: #C0C0C0 1px solid; 
 COLOR: #808080; 
 PADDING-TOP: 0px; 
 PADDING-BOTTOM: 0px;
 BORDER-BOTTOM: #C0C0C0 1px solid
}
</style>
<hrms:themes />
<script language="javascript">
function saveSelect(){
	var standardid = document.getElementById("taxid").value;
	if(standardid==' '||standardid=='0'){
		return ;
	}
	
	var hashvo=new ParameterSet();

	var salaryid = document.getElementById("salaryid").value;
	hashvo.setValue("salaryid",salaryid);
	
	var item = document.getElementById("item").value;
	hashvo.setValue("item",item);
	
	hashvo.setValue("standardid",standardid);
	
	hashvo.setValue("runflag","2");
	
	hashvo.setValue("operating","alert");
	
	var request=new Request({method:'post',asynchronous:false,
     	onSuccess:checkSelect,functionId:'3020060014'},hashvo);
}
function checkSelect(outparamters){
	var info = outparamters.getValue("info");
	var operating = outparamters.getValue("operating");
	if(info=='ok'){
		var taxid = document.getElementById("taxid").value;
		window.returnValue = taxid;
		window.close();
	}else{
		window.close();
	}
}
</script>
<html:form action="/gz/formula/selectScale">
<center>
<table width="60%"  border="0" align="center">
<tr><td>&nbsp;</td></tr>
<tr><td>
<table width="70%"  border="0" align="center">
	<tr>
    	<td>
			<html:hidden name="formulaForm" property="salaryid"/>
			<html:hidden name="formulaForm" property="item"/>
		</td>
   </tr>
   <tr>
    	<td>
    		<fieldset style="width:100%;height=90">
    		<legend><bean:message key='gz.formula.select.scale.table'/></legend>
    			<table width="100%"  border="0" align="center">
    				<tr>
    					<td>&nbsp;</td>
    				</tr>
    				<tr>
    					<td>
    						<html:select name="formulaForm" property="taxid"  style="width:230">
			 					<html:optionsCollection property="taxlist" value="dataValue" label="dataName" />
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
    				<td align="center">
    					<input type="button" value="<bean:message key='button.ok'/>" onclick="saveSelect();" Class="mybutton">
    					<input type="button" value="<bean:message key='button.cancel'/>" onclick="window.close();" Class="mybutton">
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
