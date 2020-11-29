<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ page import="com.hjsj.hrms.actionform.gz.gz_accounting.AcountingForm" %>
<link rel="stylesheet" href="/css/css1.css" type="text/css">
<script language="JavaScript" src="/js/validate.js"></script>
<style>
<!--
.TableRow_head_locked {
	background-position : center left;
	BACKGROUND-COLOR: #f4f7f7; 
	font-size: 12px;  
	BORDER-BOTTOM: #94B6E6 0pt solid; 
	BORDER-LEFT: #94B6E6 1pt solid; 
	BORDER-RIGHT: #94B6E6 0pt solid; 
	BORDER-TOP: #94B6E6 1pt solid;
	height:22;
	font-weight: bold;	
	valign:middle;
	left: expression(document.getElementById("ss").scrollLeft-1); /*IE5+ only*/
	position: relative;
	
}
.TableRow_head_locked1{
	background-position : center left;
	BACKGROUND-COLOR: #f4f7f7; 
	font-size: 12px;  
	BORDER-BOTTOM: #94B6E6 1pt solid; 
	BORDER-LEFT: #94B6E6 1pt solid; 
	BORDER-RIGHT: #94B6E6 0pt solid; 
	BORDER-TOP: #94B6E6 1pt solid;
	height:22;
	font-weight: bold;	
	valign:middle;
	left: expression(document.getElementById("ss").scrollLeft-1); /*IE5+ only*/
	position: relative;
}
.RecordRow_locked{
	border: inset 1px #94B6E6;
	BACKGROUND-COLOR: white; 
	BORDER-BOTTOM: #94B6E6 1pt solid; 
	BORDER-LEFT: #94B6E6 1pt solid; 
	BORDER-RIGHT: #94B6E6 0pt solid; 
	BORDER-TOP: #94B6E6 0pt solid;
	font-size: 12px;
	border-collapse:collapse; 
	height:22;
	left: expression(document.getElementById("ss").scrollLeft-1); /*IE5+ only*/
	position: relative;
	z-index: 20;
}
#scroll_box {
	border: 1px solid;
}
-->
</style>
<hrms:themes />
<html:form action="/gz/gz_accounting/infochange">
<%int nums=1;%>
<div id="scroll_box" style="position:absolute;overflow:auto;width:100%;height:510px;">
<table  class="ListTable">
	<tr  class="fixedHeaderTr1">
		<td align="center" width="20" class="TableRow_right">&nbsp;</td>
		<td align="center" class="TableRow" nowrap><bean:message key='tree.unroot.undesc'/></td>
		<td align="center" class="TableRow" nowrap><bean:message key='tree.umroot.umdesc'/></td>
		<td align="center" class="TableRow" nowrap><bean:message key='hire.employActualize.name'/></td>
		<td align="center" class="TableRow" nowrap><bean:message key='gz.gz_acounting.change.info'/></td>
		<logic:iterate id="tables" name="accountingForm" property="tablenamelist" indexId="index">
		<td align="center"  class="TableRow" nowrap>
			<bean:write name="tables"/>
		</td>
		</logic:iterate>
	</tr>
	<hrms:paginationdb id="element" name="accountingForm" sql_str="accountingForm.sqlstr" table="" where_str="accountingForm.where" columns="accountingForm.column" order_by="accountingForm.orderby" pagerows="20" page_id="pagination" indexes="indexes" curpage="curpage">	
	 <%
          if(nums%2==0)
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
		<td align="center" rowspan="2" class="RecordRow_right" nowrap>
		<%String curpage=(String)pageContext.getAttribute("curpage");
		int currp=0;
		if(curpage!=null&&curpage.length()>0)
		{
		   int cur=Integer.parseInt(curpage);
		   currp=(cur-1)*20+nums;
		}			
		  out.println(currp);
			%></td>
		<td rowspan="2" class="RecordRow" nowrap>
			<hrms:codetoname codeid="UN" name="element" codevalue="b0110" codeitem="codeitem1" scope="page"/>
			<bean:write name="codeitem1" property="codename" />&nbsp;
		</td>
		<td rowspan="2" class="RecordRow" nowrap>
			<hrms:codetoname codeid="UM" name="element" codevalue="e0122" codeitem="codeitem2" scope="page"/>
			<bean:write name="codeitem2" property="codename" />&nbsp;
		</td>
		<td rowspan="2" class="RecordRow" nowrap><bean:write name="element" property="a0101"/></td>
		<td class="RecordRow" nowrap><bean:message key='gz.gz_acounting.change.before'/></td>
		<logic:iterate id="tableid1" name="accountingForm" property="tableidlist1" indexId="index">
		<td align="right" class="RecordRow" nowrap>
			<logic:notEqual name="tableid1" property="codesetid" value="0">
				<logic:equal name="tableid1" property="codesetid" value="UM">
				<hrms:codetoname codeid="${tableid1.codesetid}" name="element" codevalue="${tableid1.itemid}_1" codeitem="${tableid1.itemid}" scope="page"/>
				<bean:write name="${tableid1.itemid}" property="codename" />
				<hrms:codetoname codeid="UN" name="element" codevalue="${tableid1.itemid}_1" codeitem="${tableid1.itemid}" scope="page"/>
				<bean:write name="${tableid1.itemid}" property="codename" />
				</logic:equal>
				<logic:notEqual name="tableid1" property="codesetid" value="UM">
				<hrms:codetoname codeid="${tableid1.codesetid}" name="element" codevalue="${tableid1.itemid}_1" codeitem="${tableid1.itemid}" scope="page"/>
				<bean:write name="${tableid1.itemid}" property="codename" />
				</logic:notEqual>
				
			</logic:notEqual>
			<logic:equal name="tableid1" property="codesetid" value="0">
			  <logic:equal name="element" property="${tableid1.itemid}_1" value="0">
			  &nbsp;&nbsp;
			  </logic:equal>
			  <logic:notEqual value="0" name="element" property="${tableid1.itemid}_1">
				<bean:write name="element" property="${tableid1.itemid}_1" />
				</logic:notEqual>
			</logic:equal>
		</td>
		</logic:iterate>
	</tr>
	<tr>
		<td class="RecordRow_right" nowrap><bean:message key='gz.gz_acounting.change.affter'/></td>
		<logic:iterate id="tableid2" name="accountingForm" property="tableidlist1" indexId="index">
		<td align="right" class="RecordRow" nowrap>
			<logic:notEqual name="tableid2" property="codesetid" value="0">
				<hrms:codetoname codeid="${tableid2.codesetid}" name="element" codevalue="${tableid2.itemid}_2" codeitem="${tableid2.itemid}" scope="page"/>
				<bean:write name="${tableid2.itemid}" property="codename" />
			</logic:notEqual>
			<logic:equal name="tableid2" property="codesetid" value="0">
			  <logic:equal name="element" property="${tableid2.itemid}_2" value="0">
			  &nbsp;&nbsp;
			  </logic:equal>
			  <logic:notEqual value="0" name="element" property="${tableid2.itemid}_2">
				 <bean:write name="element" property="${tableid2.itemid}_2" />
			</logic:notEqual>
			</logic:equal>
		</td>
		</logic:iterate>
	</tr>
	<%nums++;%>
	</hrms:paginationdb>
	<tr class='trShallow'>
		<td align="center" width="20" class="RecordRow_right" nowrap>&nbsp;</td>
		<td align="center" width="100" class="RecordRow" nowrap>&nbsp;</td>
		<td align="center" width="100" class="RecordRow" nowrap>&nbsp;</td>
		<td align="left" width="80" class="RecordRow" nowrap><bean:message key='gz.gz_acounting.variance.total'/></td>
		<td align="center" width="60" class="RecordRow" nowrap>&nbsp;</td>
		<logic:iterate id="vartotal" name="accountingForm" property="varianceTotal" indexId="index">
		<td align="right" class="RecordRow" nowrap>
			<bean:write name="vartotal"/>
		</td>
		</logic:iterate>
	</tr>
	<% int count=0; %>
	<tr class='trShallow'>
		<td align="center" width="20" class="RecordRow_right" nowrap>&nbsp;</td>
		<td align="center" width="100" class="RecordRow" nowrap>&nbsp;</td>
		<td align="center" width="100" class="RecordRow" nowrap>&nbsp;</td>
		<td align="left" width="80" class="RecordRow" nowrap><bean:message key='gz.gz_acounting.total'/></td>
		<td align="center" width="60" class="RecordRow" nowrap>&nbsp;</td>
		<logic:iterate id="total" name="accountingForm" property="totallist" indexId="index">
		<td align="right" class="RecordRow" nowrap>
			<bean:write name="total"/>
			<% count++; %>
		</td>
		</logic:iterate>
	</tr>
	<tr>
	<td colspan="<%=(count+5)%>" class="RecordRow_right">
	<table  width="100%">
	<tr>
		<td valign="bottom" class="tdFontcolor">
		    <bean:message key="label.page.serial"/>
			<bean:write name="pagination" property="current" filter="true" />
			<bean:message key="label.page.sum"/>
			<bean:write name="pagination" property="count" filter="true" />
			<bean:message key="label.page.row"/>
			<bean:write name="pagination" property="pages" filter="true" />
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
<br>				
</div>
</html:form> 
<script language="javascript">
	parent.document.getElementById("changeflag").value='${accountingForm.changeflag}';
</script>