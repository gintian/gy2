<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<html>

<head>
<title>Insert title here</title>
</head>
<script type="text/javascript">
	function sureretrun(){
		window.returnValue="ok";
		window.close();
	}
	function goback(){
		window.returnValue="no";
		window.close();
	}
</script>
<body>
<html:form action="/performance/kh_system/kh_field/init_grade_template">

<table width="90%"  class="ListTable" cellpadding="0" cellspacing="0" align="center">
<tr>
<td>
	<fieldset style="width:100%;">
	<legend>
		下列机构继承了所选机构的指标，将一起被清除:
	</legend>
		<div style='height:200;width:100%; overflow: auto;'  >
			<table width="100%"  class="ListTable" cellpadding="0" cellspacing="0" align="center">
				<logic:iterate id="element" name="khFieldForm" property="unitlist">
				<tr>
					<td align="left" class="RecordRow_inside" width="100%">
						<bean:write name="element" property="itemdesc" filter="true" />
					</td>
				</tr>
				</logic:iterate>
			</table>
			</div>
	</fieldset>
</td>
</tr>
	<tr>
	<td align="center">
	<br>
	<input type='button'  class="mybutton" value='<bean:message key="lable.tz_template.enter"/>'  onclick=' sureretrun()' />
			&nbsp;
	<input type='button'  class="mybutton" value='<bean:message key="lable.tz_template.cancel"/>'  onclick='goback()' />&nbsp;&nbsp;
	</td>
	</tr>
</table>
</html:form>
</body>
</html>