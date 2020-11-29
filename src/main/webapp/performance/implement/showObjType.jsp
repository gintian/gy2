<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ page import="java.util.*,
				 com.hjsj.hrms.actionform.performance.implement.ImplementForm,
				 com.hrms.struts.taglib.CommonData" %>
				 
<%
	ImplementForm implementForm=(ImplementForm)session.getAttribute("implementForm");				 
	ArrayList 	objectTypeList = implementForm.getObjectTypeList();
%>		
<script>
	function selObjType()
	{
		var bodyid = document.getElementById('body_id').value;
		var thevo=new Object();
       	thevo.flag="true";
       	thevo.objTypeId=bodyid;
       	if (window.showModalDialog){
            parent.window.returnValue=thevo;
        }else{
			parent.opener.batchSetObjType_ok(thevo);
        }
        parent.window.close();
		
	}
</script>		 
<html:form action="/performance/implement/performanceImplement">

	<table border="0" cellspacing="0" align="center" cellpadding="2">

			<tr>
						<td align="center" nowrap >
							<fieldset align="center" style="width:360px;">
							<legend>
									<bean:message key='jx.implement.selObjType' />
							</legend>
							<table border="0" cellspacing="0" align="center" cellpadding="2">
								<tr>
						<tr>
						<td height='10' nowrap>
							&nbsp;
						</td>
						</tr>
									<td style="height:35px" nowrap>
							<select id="body_id" size="1" style="width:250px">
							<%for(int i=0;i<objectTypeList.size();i++){ 
								CommonData data = (CommonData)objectTypeList.get(i);
								String dataValue = data.getDataValue();
								String dataName = data.getDataName();					
							%>
								<option value="<%= dataValue%>">
									<%= dataName%>
								</option>
							<%} %>
							</select>
						</td>
					</tr>
						<tr>
						<td height='10' nowrap>
							&nbsp;
						</td>
						</tr>
				</table>
			</fieldset>
						</td>
			</tr>
			</table>			
			
			<table width="100%">
				<tr>
					<td align="center">
						
						<input type="button" class="mybutton"
							value="<bean:message key='button.ok' />" onClick="selObjType();" />
					
						<input type="button" class="mybutton"
							value="<bean:message key='button.cancel' />"
							onClick="parent.window.close();">
					</td>
				</tr>
			</table>
</html:form>