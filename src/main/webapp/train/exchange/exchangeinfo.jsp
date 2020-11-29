<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean"%>
<%@ taglib uri="/tags/struts-html" prefix="html"%>
<%@ taglib uri="/tags/struts-logic" prefix="logic"%>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms"%>
<script language="javascript" src="/js/dict.js"></script>
<script language="JavaScript" src="/js/validateDate.js"></script>
<script type="text/javascript" src="exchange.js"></script>

<html:form action="/train/exchange/exchangeinfo">
	 <html:hidden name="exchangeForm" property="a_code" />
	<%
		int i = 0;
	%>
	<table border="0" cellpadding="0" cellspacing="0">
	<tr>
	<td height="35">
		　　<font size="4" style="font-weight: bold;">${exchangeForm.r5703 }兑换详情:</font>
	</td>
	</tr>
	<tr>
	<td>
	<div class="fixedDiv2">
	<table width="100%" border="0" cellspacing="0" align="center"
		cellpadding="0">
		<thead>
			<tr class="fixedHeaderTr">
				<td align="center" width="18%" class="TableRow" style="border-left: none;border-top: none;" nowrap>
					&nbsp;<bean:message key="b0110.label"/>&nbsp;
				</td>
				<td align="center" width="23%" class="TableRow" style="border-left: none;border-top: none;"  nowrap>
					&nbsp;<bean:message key="e0122.label"/>&nbsp;
				</td>
				<td align="center" width="18%" class="TableRow" style="border-left: none;border-top: none;"  nowrap>
					&nbsp;<bean:message key="e01a1.label"/>&nbsp;
				</td>
				<td align="center" width="13%" class="TableRow" style="border-left: none;border-top: none;"  nowrap>
					&nbsp;<bean:message key="label.title.name"/>&nbsp;
				</td>
				<td align="center" width="18%" class="TableRow" style="border-left: none;border-top: none;"  nowrap>
					&nbsp;兑换日期&nbsp;
				</td>
				<td align="center" width="13%" class="TableRow" style="border-left: none;border-top: none;border-right: none;"  nowrap>
					&nbsp;兑换数量&nbsp;
				</td>
			</tr>
		</thead>
		<hrms:paginationdb id="element" name="exchangeForm"
			sql_str="exchangeForm.strsql" table="" where_str="exchangeForm.strwhere"
			columns="exchangeForm.columns" page_id="pagination"
			pagerows="${exchangeForm.pagerows}" order_by="exchangeForm.order_by">
			<%
				if (i % 2 == 0) {
			%>
			<tr class="trShallow" onMouseOver="javascript:tr_onclick(this,'')">
				<%
					} else {
				%>
			
			<tr class="trDeep" onMouseOver="javascript:tr_onclick(this,'')">
				<%
					}
								i++;
				%>
				<td class="RecordRow" style="border-left: none;border-top: none;"  nowrap>
					&nbsp;<hrms:codetoname codeid="UN" name="element" codevalue="b0110" codeitem="codeitem" scope="page" />
					<bean:write name="codeitem" property="codename" />&nbsp;
				</td>
				<td class="RecordRow" style="border-left: none;border-top: none;"  nowrap>
					&nbsp;<hrms:codetoname codeid="UM" name="element" codevalue="e0122" codeitem="codeitem" uplevel="${exchangeForm.uplevel}" scope="page" />
					<bean:write name="codeitem" property="codename" />&nbsp;
				</td>
				<td class="RecordRow" style="border-left: none;border-top: none;"  nowrap>
					&nbsp;<hrms:codetoname codeid="@K" name="element" codevalue="e01a1" codeitem="codeitem" scope="page" />
					<bean:write name="codeitem" property="codename" />&nbsp;
				</td>
				<td class="RecordRow" style="border-left: none;border-top: none;"  nowrap>
					&nbsp;<bean:write name="element" property="a0101"/>&nbsp;
				</td>
				<td align="center" class="RecordRow" style="border-left: none;border-top: none;"  nowrap>
					&nbsp;<bean:write name="element" property="exchangedtime"/>&nbsp;
				</td>
				<td align="right" class="RecordRow" style="border-left: none;border-top: none;border-right: none;"  nowrap>
					&nbsp;<bean:write name="element" property="ncount"/>&nbsp;
				</td>
			</tr>
		</hrms:paginationdb>
	</table>
	</div>
	</td>
	</tr>
	<tr>
		<td>
			<table width="100%" class="RecordRowP" align="center">
				<tr>
					<td valign="bottom" class="tdFontcolor">
						<hrms:paginationtag name="exchangeForm"
							pagerows="${exchangeForm.pagerows}" property="pagination"
							scope="page" refresh="true"></hrms:paginationtag>
					</td>
					<td align="right" nowrap class="tdFontcolor">
							<hrms:paginationdblink name="exchangeForm"
								property="pagination" nameId="exchangeForm" scope="page">
							</hrms:paginationdblink>
					</td>
				</tr>
			</table>
		</td>
	</tr>
	<tr>
		<td style="padding-top: 8px;" align="left">
			<input type="button" class="mybutton" value="<bean:message key='button.return'/>" onClick="exchange();">
		</td>
	</tr>
</table>
</html:form>