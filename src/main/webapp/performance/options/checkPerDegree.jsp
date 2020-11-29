<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<html:form action="/performance/options/perDegreeList">
<br>
<center>
	<fieldset style="width:90%;">
		<legend>
			<bean:message key="jx.param.checkPerDegree" />
		</legend>
	<table border="0" cellspacing="0" width="100%" 	cellpadding="2" >
		<tr>
			<td>
				<html:textarea name="perDegreeForm" styleId="checkResult" property="checkResult" style="width:320px;height:250px;"
								styleClass="textboxMul"></html:textarea>
			</td>
		</tr>
	</table>
	</fieldset>
	</center>

	<table border="0" cellspacing="0" align="center" cellpadding="2" width="50%">
			<tr>
				<td align="center">
					<input type="button" class="mybutton" id="okButton" 
						value="&nbsp;<bean:message key='button.ok' />&nbsp;"
							onClick="close_checkPer();" />
				</td>
			</tr>
		</table>		
</html:form>

<script>
function close_checkPer(){
	if(window.showModalDialog){
		parent.window.close();
	}else if (parent.parent.close_check){
 		parent.parent.close_check();
	}else{
        parent.window.close();
    }
}
</script>