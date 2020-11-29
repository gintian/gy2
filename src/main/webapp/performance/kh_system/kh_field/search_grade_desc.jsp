<%@ page contentType="text/html; charset=UTF-8" language="java"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>

<script type="text/javascript">
<!--
function save()
{
   khFieldForm.action="/performance/kh_system/kh_field/search_field_grade.do?b_save=save&close=1";
   khFieldForm.submit();
}
<%if(request.getParameter("close")!=null&&request.getParameter("close").equals("1")){%>
   var obj = new Object();
   obj.refresh="1";
parent.window.returnValue=obj;
if(window.showModalDialog) {
    parent.window.close();;
}else{
    parent.window.opener.window.searchDesc_ok(obj);
    window.open("about:blank","_top").close();
}
<%}%>
//-->
</script>

<html:form action="/performance/kh_system/kh_field/search_field_grade">

	<html:hidden name="khFieldForm" property="gradeid"/>
	
	<table width="100%" border="0" cellspacing="0"  align="center" cellpadding="0" style="margin-top:7px;">
		<tr>
			<td class="TableRow_lrt" align="left" nowrap>
				标度内容
			</td>
		</tr>
		<tr>
			<td align="center" class="RecordRow_lrt">
				<html:textarea property="gradeContent" name="khFieldForm" rows="16" cols="65"></html:textarea>
			</td>
		</tr>
		<tr>
			<td class='RecordRow' align="center" style="height:35px">   
				<input type="button" name="sa" class="mybutton" value="<bean:message key="button.save"/>" onclick="save();"/>
				<input type="button" name="cl" class="mybutton" value="<bean:message key="button.close"/>" onclick="parent.window.close();"/>
			</td>
		</tr>
	</table>
</html:form>