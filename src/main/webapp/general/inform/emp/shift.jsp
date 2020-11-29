<%@ page contentType="text/html; charset=UTF-8" language="java"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean"%>
<%@ taglib uri="/tags/struts-html" prefix="html"%>
<%@ taglib uri="/tags/struts-logic" prefix="logic"%>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms"%>

<hrms:themes></hrms:themes>
<script language="JavaScript" src="/js/validate.js"></script>
<script language="JavaScript" src="/general/inform/inform.js"></script>
<script language="javascript">
function inOk(){
	var dbname = document.getElementById("dbname").value;
	if(!dbname)
		return;
	var tablevos=document.getElementsByTagName("input");
	var check="1";
	for(var i=0;i<tablevos.length;i++){
		if(tablevos[i].type=="checkbox"){
	     	if(tablevos[i].checked){
	     		check="1";
	     	}else{
	     		check="0";
	     	}
		}
    }
	window.returnValue=dbname+","+check;
	window.close();
}
</script>
<html:form action="/general/inform/emp/shift">
<table  border="0" align="center">
	<tr>
		<td align="center" width="50%"> 
			<fieldset style="width:100%;height:60">
     		<legend><bean:message key='org.autostatic.mainp.treasury.objectives'/></legend> 
			<table  border="0" align="center">
				<tr>
					<td align="center" height="30">
						<html:select name="shiftLibraryForm" property="dbname" style="width:130">
    						<html:optionsCollection property="dblist" value="dataValue" label="dataName" />
 						</html:select>
					</td>
				</tr>
			</table>
			</fieldset>
		</td>
		<td align="center" width="50%">
			<fieldset style="width:100%;height:60">
     		<legend><bean:message key='kq.set.card.explain'/></legend>   
			<table  border="0" align="center" height="40">
				<tr>
					<td><bean:message key='org.autostatic.mainp.shift.person'/></td>
				</tr>
			</table>
			</fieldset>
		</td>
	</tr>
	<tr>
		<td colspan="2">
			<input type="checkbox" name="dellibrary" value="1" checked>
			<bean:message key='org.autostatic.mainp.shift.person.del'/>
		</td>
	</tr>
	<tr>
		<td align="center">
			<input type="button" value="<bean:message key='kq.formula.true'/>" onclick="inOk();" Class="mybutton">
		</td>
		<td>
			<input type="button" value="<bean:message key='kq.register.kqduration.cancel'/>" onclick="window.close();" Class="mybutton">
		</td>
	</tr>
</table>

</html:form>

