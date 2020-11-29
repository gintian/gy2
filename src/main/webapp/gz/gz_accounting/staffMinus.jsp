<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<link rel="stylesheet" href="/css/css1.css" type="text/css">
<script language="JavaScript" src="/js/validate.js"></script>
<style>
#scroll_box {
	border: 1px solid;
}
</style>
<hrms:themes />
<html:form action="/gz/gz_accounting/staffMinus">
<%int i=1;%>
<div id="scroll_box" style="position:absolute;overflow:auto;width:100%;height:510px;">
<table border="0" class="ListTable">
	<tr>
		<td align="center" width="20" class="TableRow_right">&nbsp;</td>
		<td align="center" width="100" class="TableRow" nowrap><bean:message key='tree.unroot.undesc'/></td>
		<td align="center" width="100" class="TableRow" nowrap><bean:message key='tree.umroot.umdesc'/></td>
		<td align="center" width="80" class="TableRow" nowrap><bean:message key='hire.employActualize.name'/></td>
		<logic:iterate id="tables" name="accountingForm" property="tablenamelist" indexId="index">
		<td align="center" class="TableRow" nowrap>
			<bean:write name="tables"/>
		</td>
		</logic:iterate>
	</tr>
	<hrms:paginationdb id="element" name="accountingForm" sql_str="accountingForm.sqlstr" table="" where_str="accountingForm.where" columns="accountingForm.column" order_by="accountingForm.orderby" pagerows="20" page_id="pagination" indexes="indexes" curpage="curpage">	
	 <%
          if(i%2==0)
          {
          %>
          <tr class="trShallow">
          <%}
          else
          {%>
          <tr class="trDeep">
          <%
          }      
          %>  
		<td align="center"  class="RecordRow_right" nowrap>
		<%
		String curpage=(String)pageContext.getAttribute("curpage");
		int currp=0;
		if(curpage!=null&&curpage.length()>0)
		{
		   int cur=Integer.parseInt(curpage);
		   currp=(cur-1)*20+i;
		}			
		  out.println(currp);
		%></td><%i++;%>
		<td class="RecordRow" nowrap>
			<hrms:codetoname codeid="UN" name="element" codevalue="b0110" codeitem="codeitem1" scope="page"/>
			<bean:write name="codeitem1" property="codename" />&nbsp;
		</td>
		<td class="RecordRow" nowrap>
			<hrms:codetoname codeid="UM" name="element" codevalue="e0122" codeitem="codeitem2" scope="page"/>
			<bean:write name="codeitem2" property="codename" />&nbsp;
		</td>
		<td class="RecordRow" nowrap><bean:write name="element" property="a0101"/>&nbsp;</td>
		<logic:iterate id="tableid2" name="accountingForm" property="tableidlist1" indexId="index">
		<td align="right" class="RecordRow">
			<logic:notEqual name="tableid2" property="codesetid" value="0">
				
				<logic:equal name="tableid2" property="codesetid" value="UM">
					<hrms:codetoname codeid="${tableid2.codesetid}" name="element" codevalue="${tableid2.itemid}_1" codeitem="${tableid2.itemid}" scope="page"/>
					<bean:write name="${tableid2.itemid}" property="codename" />&nbsp;
					<hrms:codetoname codeid="UN" name="element" codevalue="${tableid2.itemid}_1" codeitem="${tableid2.itemid}" scope="page"/>
					<bean:write name="${tableid2.itemid}" property="codename" />&nbsp;
				</logic:equal>
				<logic:notEqual name="tableid2" property="codesetid" value="UM">
					<hrms:codetoname codeid="${tableid2.codesetid}" name="element" codevalue="${tableid2.itemid}_1" codeitem="${tableid2.itemid}" scope="page"/>
					<bean:write name="${tableid2.itemid}" property="codename" />&nbsp;
				</logic:notEqual>
			
			
			</logic:notEqual>
			<logic:equal name="tableid2" property="codesetid" value="0">
				<bean:write name="element" property="${tableid2.itemid}_1" />&nbsp;
			</logic:equal>
		</td>
		</logic:iterate>
	</tr>
	</hrms:paginationdb>
			<% int count=0; %>
	<tr class="trShallow">
		<td align="center" width="20" class="RecordRow_right" nowrap>&nbsp;</td>
		<td align="center" width="100" class="RecordRow" nowrap>&nbsp;</td>
		<td align="center" width="100" class="RecordRow" nowrap>&nbsp;</td>
		<td align="left" width="80" class="RecordRow" nowrap><bean:message key='gz.gz_acounting.total'/></td>
		<logic:iterate id="total" name="accountingForm" property="totallist" indexId="index">
		<td align="right" class="RecordRow" nowrap>
			<bean:write name="total"/>&nbsp;
			<% count++; %>
		</td>
		</logic:iterate>
	</tr>
<tr>
<td colspan="<%=(count+4)%>" class="RecordRow_right">
<table  width="100%">
	<tr>
		<td valign="bottom" class="tdFontcolor">
		    <bean:message key="label.page.serial"/>
			<bean:write name="accountingForm" property="pagination.current" filter="true" />
			<bean:message key="label.page.sum"/>
			<bean:write name="accountingForm" property="pagination.count" filter="true" />
			<bean:message key="label.page.row"/>
			<bean:write name="accountingForm" property="pagination.pages" filter="true" />
			<bean:message key="label.page.page"/>
		</td>
	    <td  align="right" nowrap class="tdFontcolor">
		    <p align="right">
		      <hrms:paginationdblink name="accountingForm" property="pagination" nameId="browseRegisterForm" scope="page">
				</hrms:paginationdblink>
		</td>
	</tr>
</table>
</td>
</tr>
</table>				
</html:form> 
<script language="javascript">
	parent.document.getElementById("changeflag").value='${accountingForm.changeflag}';
</script>