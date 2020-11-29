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
	ArrayList 	itemList = implementForm.getItemList();
	
%>		
<script>
	function selObjType()
	{
		var itemid = document.getElementById('item_id_add').value;
		var thevo=new Object();
       	thevo.flag="true";
       	thevo.item_id=itemid;
       	if (window.showModalDialog){
            window.returnValue=thevo;
        }else{
       	    window.opener.additem_ok(thevo);
        }
		parent.window.close();
	}
</script>		 
<html:form action="/performance/implement/kh_object/dynaitem">

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
									<bean:message key='conlumn.investigate.additem' />
							</legend>
							<table border="0" cellspacing="0" align="center" cellpadding="2">
								<tr>
									<td style="height:35px" nowrap>
							<select id="item_id_add" size="1" style="width:250px">
							<%for(int i=0;i<itemList.size();i++){ 
								CommonData data = (CommonData)itemList.get(i);
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
							onClick="parent.window.close();">
					</td>
				</tr>
			</table>
</html:form>