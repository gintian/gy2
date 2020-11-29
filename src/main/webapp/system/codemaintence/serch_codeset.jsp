<%@ page contentType="text/html; charset=UTF-8" language="java"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean"%>
<%@ taglib uri="/tags/struts-html" prefix="html"%>
<%@ taglib uri="/tags/struts-logic" prefix="logic"%>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms"%>
<script language="JavaScript" src="/js/validate.js"></script>
<script language="JavaScript" src="/js/function.js"></script>
<%
int i=0;
%>
<hrms:themes></hrms:themes>
<style id=iframeCss>
.fixedDiv2 
{ 
    overflow:auto; 
    height:230px;
    width:490px;  
}
</style>
<script language="JavaScript">
	function winClose() {
		if(parent.Ext.getCmp('sercodeset')){
            parent.Ext.getCmp('sercodeset').close();
		}
    }
</script>
<html:form action="/system/codemaintence/serch_codeset">
<table width="100%" border="0" cellspacing="0" align="center" cellpadding="0">
<tr>
<td align="center" nowrap>
<div class="fixedDiv2"> 
	<table width="100%" border="0" cellspacing="0" align="center" cellpadding="0" class="ListTable" style="margin-top:-1;margin-bottom:0;">
		<THEAD>
			<tr class="fixedHeaderTr">

				<td align="center" class="TableRow" nowrap style="border-left:none;">
					<bean:message key="menu.table" />
				</td>
				<td align="center" class="TableRow" nowrap>
					<bean:message key="codemaintance.codeset.itemid" />
				</td>
				<td align="center" class="TableRow" nowrap>
					<bean:message key="field.label" />
				</td>
				<td align="center" class="TableRow" nowrap style="border-right:none;">
					<bean:message key="field_result.useflag" />
				</td>
			</TR>
		</THEAD>
		<hrms:paginationdb id="element" name="codeSetForm" sql_str="codeSetForm.sql" table="" where_str="codeSetForm.where" columns="codeSetForm.column" order_by="" pagerows="1000" page_id="pagination" indexes="indexes">
			 <%if(i%2==0){ %>
	     <tr class="trShallow">
          <%} else { %>
	     <tr class="trDeep">
	      <% }
	      %>
			<td align="left" class="RecordRow" nowrap style="border-left:none;">
				&nbsp;<bean:write name="element" property="fieldsetdesc" />
			</td>
			<td align="left" class="RecordRow" nowrap>
				&nbsp;<bean:write name="element" property="itemid" />
			</td>
			<td align="left" class="RecordRow" nowrap>
				&nbsp;<bean:write name="element" property="itemdesc" />
			</td>
			<logic:equal name="element" property="useflag" value="0">
				<td align="center" class="RecordRow" nowrap style="border-right:none;">
					<bean:message key="datesytle.no" />
				</td>
			</logic:equal>
			<logic:equal name="element" property="useflag" value="1">
				<td align="center" class="RecordRow" nowrap style="border-right:none;">
					<bean:message key="datestyle.yes" />
				</td>
			</logic:equal>
			</tr>
			<%i++;%>
		</hrms:paginationdb>
	</table>
</div>
	</td>
	</tr>
	<tr>
</tr>
<tr style="height: 35px">
<td align="center" nowrap>
	<%--<input type="button" name="close" value="<bean:message key="button.close"/>" class="mybutton" onclick="window.close()"/>--%>
	<input type="button" name="close" value="<bean:message key="button.close"/>" class="mybutton" onclick="winClose()"/>
</table>
</html:form>
