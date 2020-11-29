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
function newStandard(){
	var standardid=document.getElementById("standardid").value;
	if(standardid!=' '&&standardid=='0'){
		toggles("viewok");
		hides("hideok");
		var itemname=document.getElementById("itemname").value;
    	var thecodeurl ="/gz/formula/gz_newStandard.do?b_query=link`opt=new`itemname="+itemname; 
    	var iframe_url="/general/query/common/iframe_query.jsp?src="+thecodeurl;
    	var return_vo= window.showModalDialog(iframe_url, "", 
              "dialogWidth:400px; dialogHeight:420px;resizable:no;center:yes;scroll:yes;status:no");
    	if(return_vo!=null){
  	 		refleshSelect();
  		}else{
  			return ;
  		}
  	}else if(standardid!=' '){
  		toggles("viewok");
		hides("hideok");
  	}else{
  		toggles("hideok");
		hides("viewok");
  	}
}
function refleshSelect(){
	var hashvo=new ParameterSet();

	var salaryid = document.getElementById("salaryid").value;
	hashvo.setValue("salaryid",salaryid);
	
	var item = document.getElementById("item").value;
	hashvo.setValue("item",item);
	
	var itemname = document.getElementById("itemname").value;
	hashvo.setValue("itemname",itemname);
	
	var request=new Request({method:'post',asynchronous:false,
     	onSuccess:refleshList,functionId:'3020060017'},hashvo);
}
function refleshList(outparamters){
	var standardlist = outparamters.getValue("standardlist");
	AjaxBind.bind(formulaForm.standardid,standardlist);
}
function saveSelect(operat){
	if(operat=='del'){
		if(!confirm("<bean:message key='gz.formula.del.salary.standart'/>")){
			return;
		}
	}
	var standardid = document.getElementById("standardid").value;
	if(standardid==' '||standardid=='0'){
		return ;
	}
	
	var hashvo=new ParameterSet();

	var salaryid = document.getElementById("salaryid").value;
	hashvo.setValue("salaryid",salaryid);
	
	var item = document.getElementById("item").value;
	hashvo.setValue("item",item);
	
	hashvo.setValue("standardid",standardid);
	
	hashvo.setValue("runflag","1");
	
	hashvo.setValue("operating",operat);
	
	var request=new Request({method:'post',asynchronous:false,
     	onSuccess:checkSelect,functionId:'3020060014'},hashvo);
}
function checkSelect(outparamters){
	var info = outparamters.getValue("info");
	var operating = outparamters.getValue("operating");
	if(info=='ok'){
		if(operating=='del'){
			refleshSelect();
		}else{
			var standardid = document.getElementById("standardid").value;
			window.returnValue = standardid;
			window.close();
		}
	}else{
		window.close();
	}
}
</script>
<html:form action="/gz/formula/selectstandard">
<center>
<table width="60%"  border="0" align="center">
<tr><td>&nbsp;</td></tr>
<tr><td>
<table width="70%"  border="0" align="center">
	<tr>
    	<td>
			<html:hidden name="formulaForm" property="salaryid"/>
			<html:hidden name="formulaForm" property="itemname"/>
			<html:hidden name="formulaForm" property="item"/>
		</td>
   </tr>
   <tr>
    	<td>
    		<fieldset style="width:100%;height=90">
    		<legend><bean:message key='gz.formula.select.standart.table'/></legend>
    			<table width="100%"  border="0" align="center">
    				<tr>
    					<td>&nbsp;</td>
    				</tr>
    				<tr>
    					<td>
    						<html:select name="formulaForm" property="standardid" onchange="newStandard();"  style="width:230">
			 					<html:optionsCollection property="standardlist" value="dataValue" label="dataName" />
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
    				<td align="center"><input type="button" value="<bean:message key='menu.gz.delete'/>" onclick="saveSelect('del');" Class="mybutton">

    						<input type="button" value="<bean:message key='button.ok'/>" onclick="saveSelect('alert');" Class="mybutton">

    				<input type="button" value="<bean:message key='button.cancel'/>" onclick="window.close();" Class="mybutton"></td>
    			</tr>
    		</table>
    	</td>
    </tr>
</table>
</td></tr>
</table>
</center>
<script language="javascript">
toggles("hideok");
hides("viewok");
</script>
</html:form>
