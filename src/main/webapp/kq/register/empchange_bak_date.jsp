<%@ page contentType="text/html; charset=UTF-8" language="java"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean"%>
<%@ taglib uri="/tags/struts-html" prefix="html"%>
<%@ taglib uri="/tags/struts-logic" prefix="logic"%>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms"%>
<link rel="stylesheet" href="/css/css1.css" type="text/css"><hrms:themes /> <!-- 7.0css -->
<SCRIPT language=JavaScript>
function pickdate()
{
   var vos= document.getElementsByName("curdate");
   if(vos==null)
  	return false;
   var vo=vos[0];
   var datestr="";
   for(var i=0;i<vo.options.length;i++)
   {
    if(vo.options[i].selected)
      datestr=vo.options[i].value;
   }   
   window.returnValue=datestr;
   window.close();
}
</SCRIPT>
<html:form action="/kq/register/empchange">
	<br>
	<br>
	<table align="center" width="100%" style="margin-left: 5px;">
		<tr>
			<td align="center" width="100%">
				<fieldset style="width: 95%; text-align: center; vertical-align: middle;">
					<legend>
						批量修改时间
					</legend>
					<table width="200" border="0" cellpadding="0" cellspacing="0"
						align="center">
						<tr>
							<td align="center" colspan="3">
								&nbsp;
							</td>
						</tr>
						<tr class="list3">
							<td width="10%">
							</td>
							<td>
								${empChangeForm.workcalendar}
							</td>
							<td width="10%">
							</td>
						</tr>
						<tr class="list3">
							<td align="center" colspan="3">
								&nbsp;
							</td>
						</tr>
					</table>
				</fieldset>
			</td>
		</tr>
		<tr>
			<td align="center">
				<br />
			</td>
		</tr>
		<tr>
			<td align="center">
				<input type="button" name="br_return"
					value="<bean:message key="button.ok"/>" class="mybutton"
					onclick="pickdate();">
				<input type="button" name="br_return"
					value="<bean:message key="button.close"/>" class="mybutton"
					onclick="window.close();">
			</td>
		</tr>
	</table>
</html:form>