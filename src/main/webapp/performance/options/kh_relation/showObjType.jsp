<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ page import="java.util.*,
				 com.hjsj.hrms.actionform.performance.options.PerRelationForm,
				 com.hrms.struts.taglib.CommonData" %>
				 
<%
	PerRelationForm perRelationForm=(PerRelationForm)session.getAttribute("perRelationForm");				 
	ArrayList 	objectTypeList = perRelationForm.getObjectTypeList();
%>		
<script>
	function selObjType()
	{
		var bodyid = document.getElementById('body_id').value;
		var thevo=new Object();
       	thevo.flag="true";
       	thevo.objTypeId=bodyid;       	
		if(window.showModalDialog){
            parent.window.returnValue=thevo;
		}else {
			parent.parent.batchSetObjType_ok(thevo);
		}
		closeWin_show();
	}
	
	function closeWin_show(){
		if(window.showModalDialog){
			parent.window.close();
		}else {
			var win = parent.parent.Ext.getCmp('batchSetObjType_win');
	   		if(win) {
	    		win.close();
	   		}
		}
		
	}
</script>		 
<html:form action="/performance/options/kh_relation">

	<table border="0" cellspacing="0" align="center" cellpadding="2">
			<tr>
						<td height='10' nowrap>
							&nbsp;
						</td>
			</tr>
			<tr>
						<td align="center" nowrap >
							<fieldset align="center" style="width:300;">
							<legend>
									<bean:message key='jx.implement.selObjType' />
							</legend>
							<table border="0" cellspacing="0" align="center" cellpadding="2">
								<tr>
									<td style="height:35px" nowrap>
							<select id="body_id" size="1" style="width:250px">
							<%for(int i=0;i<objectTypeList.size();i++){ 
								CommonData data = (CommonData)objectTypeList.get(i);
								String dataValue = data.getDataValue();
								String dataName = data.getDataName();					
								if(!dataValue.equalsIgnoreCase("null")){
							%>
								<option value="<%= dataValue%>">
									<%= dataName%>
								</option>
							<%} }%>
							</select>
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
							onClick="closeWin_show();">
					</td>
				</tr>
			</table>
</html:form>