<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>

<html:form action="/gz/gz_accounting/changesmore">
<html:hidden name="accountingForm" property="changeflag"/>
<logic:equal name="accountingForm" property="isVisible" value="1">
<hrms:tabset name="pageset" width="99%" height="550" type="true" align="center"> 
	  <hrms:tab name="tab1" label="gz.info.change" visible="true" url="/gz/gz_accounting/infochange.do?b_query=link&salaryid=${accountingForm.salaryid}">
      </hrms:tab>	
	  <hrms:tab name="tab2" label="gz.gz_acounting.add.staff" visible="true" url="/gz/gz_accounting/addStaff.do?b_query=link&salaryid=${accountingForm.salaryid}">
      </hrms:tab>	
	  <hrms:tab name="tab3" label="gz.gz_acounting.minus.staff" visible="true" url="/gz/gz_accounting/staffMinus.do?b_query=link&salaryid=${accountingForm.salaryid}">
      </hrms:tab>	
</hrms:tabset>
</logic:equal>
<logic:equal name="accountingForm" property="isVisible" value="0">
<hrms:tabset name="pageset" width="99%" height="550" type="true" align="center"> 
	  <hrms:tab name="tab1" label="gz.info.change" visible="true" url="/gz/gz_accounting/infochange.do?b_query=link&salaryid=${accountingForm.salaryid}">
      </hrms:tab>	
</hrms:tabset>
</logic:equal>
<table width="99%" border="0" align="center">
	<tr>
		<td align="center" >
			<input type="button"  value="<bean:message key='button.export'/>" onclick="outExcel();" Class="mybutton"><input type="button"  value="<bean:message key='button.close'/>" onclick="window.close();" Class="mybutton">
		</td>
		
	</tr>
</table>
</html:form> 
<script language="javascript">
function outExcel(){
	var flag = document.getElementById("changeflag").value;
	var hashvo=new ParameterSet();
	hashvo.setValue("flag",flag);
	hashvo.setValue("salaryid",'${accountingForm.salaryid}');
	var request=new Request({method:'post',asynchronous:false,onSuccess:showfile,functionId:'3020110045'},hashvo);
		
}
function showfile(outparamters){
	var outName=outparamters.getValue("outName");
	var fieldName = getDecodeStr(outName);
	var win=open("/servlet/vfsservlet?fileid="+fieldName+"&fromjavafolder=true","excel");
}
</script>