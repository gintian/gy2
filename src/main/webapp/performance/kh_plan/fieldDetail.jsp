<%@ page contentType="text/html; charset=UTF-8" language="java"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean"%>
<%@ taglib uri="/tags/struts-html" prefix="html"%>
<%@ taglib uri="/tags/struts-logic" prefix="logic"%>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms"%>

<%
	String planID = request.getParameter("planID");
	String fieldName = request.getParameter("fieldName");
	String status = request.getParameter("status");

%>
<script language="javascript">

function save()
{	 
	var thevo=new Object();
    thevo.flag="true";
    thevo.bigField=$F('bigField')
    thevo.planID='<%=planID%>';
    thevo.fieldName='<%=fieldName%>';
    thevo.status='<%=status%>';
    parent.window.returnValue=thevo;
    if(window.showModalDialog) {
        parent.window.close();
    }else{
        parent.parent.updateBigField_window_ok(thevo);
    }
}
function closewindow()
{
    if(window.showModalDialog) {
        parent.window.close();
    }else{
        parent.parent.updateBigField_window_ok("");
    }
}
</script>

<body>
	<html:form action="/performance/kh_plan/fieldDetail">
		<table border="0" cellspacing="0" align="center" cellpadding="0" style="margin-top:3px;margin-left:3px;">
			<tr>

				<td align="center" nowrap>
					<html:textarea name="examPlanForm" styleId="bigField"
						property="bigField" cols="45" rows="13"></html:textarea>
				</td>
			</tr>
		</table>
		<table border="0" cellspacing="0" align="center" cellpadding="2" width="50%">
			<tr>
				<td align="center" style="padding-top:5px;">
					<input type="button" class="mybutton" id="okButton" 
						value="&nbsp;<bean:message key='button.ok' />&nbsp;"
						onClick="save();" />				
					<input type="button" class="mybutton"
						value="&nbsp;<bean:message key='button.cancel' />&nbsp;"
						onClick="closewindow();">
				</td>
			</tr>
		</table>
	</html:form>
	<script>
		if(("${param.status}"!="0") && ("${param.status}"!="5"))
		{
			var btn = $('okButton');
			btn.disabled='disabled';
		}
	</script>
</body>

