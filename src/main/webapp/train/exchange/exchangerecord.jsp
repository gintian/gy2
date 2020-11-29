<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean"%>
<%@ taglib uri="/tags/struts-html" prefix="html"%>
<%@ taglib uri="/tags/struts-logic" prefix="logic"%>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms"%>
<script language="javascript" src="/js/dict.js"></script>
<script language="JavaScript" src="/js/validateDate.js"></script>
<script language="JavaScript" src="/js/wz_tooltip.js"></script>
<script type="text/javascript" src="exchange.js"></script>
<style>
body{text-align: center;overflow:hidden;}
.myfixedDiv
{  
	overflow:auto; 
	height:expression(document.body.clientHeight-120);
	width:expression(document.body.clientWidth-10); 
	border-collapse: collapse;
}
.fixedHeaderTr{
 	border-bottom:1px solid #C4D8EE;
 	border-right:1px solid #C4D8EE;
 	border-left:1px solid #C4D8EE;
 	border-top:1px solid #C4D8EE;
}
</style>
<html:form action="/train/exchange/exchangerecord">
	 <html:hidden name="exchangeForm" property="a_code" />
	<%
		int i = 0;
	%>
	<table style="width:100%;border:0;padding:0 4px;" cellpadding="0" cellspacing="0" >
		<tr>
		<td style="padding-bottom: 5px;">
		<span style="vertical-align: middle;">
			<logic:notEqual value="3" name="exchangeForm" property="model">
				 	姓名&nbsp;<html:text name="exchangeForm" property="a0101" styleClass="text4"></html:text>&nbsp;&nbsp;
			</logic:notEqual>
				 	奖品名称&nbsp;<html:text name="exchangeForm" styleClass="text4" property="searchstr"></html:text>&nbsp;&nbsp;
					兑换日期&nbsp;<input type="text" name="startdate" extra="editor" class="text4" dropDown="dropDownDate" style="width: 110px;height: 22px;"  value="${exchangeForm.startdate}" onchange=" if(!validate(this,'日期')) {this.focus(); this.value=''; }" />&nbsp;
					至&nbsp;<input type="text" name="enddate" extra="editor" class="text4" dropDown="dropDownDate" style="width: 110px;height: 22px;" value="${exchangeForm.enddate}" onchange=" if(!validate(this,'日期')) {this.focus(); this.value=''; }" />&nbsp;
		</span>		 	
		<span style="vertical-align: middle;">
			<input type="button" value="查询" class="mybutton" onclick="record();"/>
		</span>		
	</td>
	</tr>
	<tr>
	<td >
	<table align="center" style="margin:0px;width:100%;border:0;border-collapse: collapse;" cellpadding="0" cellspacing="0" >
		<thead>
			<tr class="fixedHeaderTr">
				<logic:notEqual value="3" name="exchangeForm" property="model">
				<td align="center" width="13%" class="TableRow" nowrap>
					&nbsp;<bean:message key="b0110.label"/>&nbsp;
				</td>
				<td align="center" width="16%" class="TableRow" style="border-left:none;" nowrap>
					&nbsp;<bean:message key="e0122.label"/>&nbsp;
				</td>
				<td align="center" width="13%" class="TableRow" style="border-left:none;" nowrap>
					&nbsp;<bean:message key="e01a1.label"/>&nbsp;
				</td>
				<td align="center" width="8%" class="TableRow" style="border-left:none;" nowrap>
					&nbsp;<bean:message key="label.title.name"/>&nbsp;
				</td>
				</logic:notEqual>
				<td align="center" width="22%" class="TableRow" style="border-left: none;" nowrap>
					&nbsp;奖品名称&nbsp;
				</td>
				<td align="center" width="16%" class="TableRow" style="border-left: none;" nowrap>
					&nbsp;兑换日期&nbsp;
				</td>
				<td align="center" width="6%" class="TableRow" style="border-left: none;" nowrap>
					&nbsp;兑换数量&nbsp;
				</td>
				<td align="center" width="6%" class="TableRow" style="border-left: none;" nowrap>
					&nbsp;积分支出&nbsp;
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
			<tr class="trShallow" style="border-left: 1px solid #c4d8ee;border-right: 1px solid #c4d8ee;" onMouseOver="javascript:tr_onclick(this,'')">
				<%
					} else {
				%>
			
			<tr class="trDeep" style="border-left: 1px solid #c4d8ee;border-right: 1px solid #c4d8ee;" onMouseOver="javascript:tr_onclick(this,'')">
				<%
					}
								i++;
				%>
				<logic:notEqual value="3" name="exchangeForm" property="model">
				<td class="RecordRow" nowrap>
					&nbsp;<hrms:codetoname codeid="UN" name="element" codevalue="b0110" codeitem="codeitem" scope="page" />
					<bean:write name="codeitem" property="codename" />&nbsp;
				</td>
				<td class="RecordRow" nowrap>
					&nbsp;<hrms:codetoname codeid="UM" name="element" codevalue="e0122" codeitem="codeitem" uplevel="${exchangeForm.uplevel}" scope="page" />
					<bean:write name="codeitem" property="codename" />&nbsp;
				</td>
				<td class="RecordRow" nowrap>
					&nbsp;<hrms:codetoname codeid="@K" name="element" codevalue="e01a1" codeitem="codeitem" scope="page" />
					<bean:write name="codeitem" property="codename" />&nbsp;
				</td>
				<td class="RecordRow" nowrap>
					&nbsp;<bean:write name="element" property="a0101"/>&nbsp;
				</td>
				</logic:notEqual>
				<td class="RecordRow"  style="border-left: none;" onmouseout="UnTip();" onmouseover="outContent('<bean:write name="element" property="r5701"/>','r5711');" nowrap>
					&nbsp;<bean:write name="element" property="r5703"/>&nbsp;
				</td>
				<td align="center" class="RecordRow" nowrap>
					&nbsp;<bean:write name="element" property="exchangedtime"/>&nbsp;
				</td>
				<td align="right" class="RecordRow" nowrap>
					&nbsp;<bean:write name="element" property="ncount"/>&nbsp;
				</td>
				<td align="right" class="RecordRow" nowrap>
					&nbsp;<bean:write name="element" property="npoint"/>&nbsp;
				</td>
			</tr>
		</hrms:paginationdb>
		<logic:notEqual value="0" name="exchangeForm" property="npoint">
			<tr style="border-left: 1px solid #c4d8ee;border-right: 1px solid #c4d8ee;">
				<logic:equal value="3" name="exchangeForm" property="model">
					<td colspan="2" align="right" class="RecordRow">&nbsp;合计&nbsp;</td>
				</logic:equal>
				<logic:notEqual value="3" name="exchangeForm" property="model">
					<td colspan="6" align="right" class="RecordRow">&nbsp;合计&nbsp;</td>
				</logic:notEqual>
				<td align="right" class="RecordRow">&nbsp;<bean:write name="exchangeForm" property="ncount"/>&nbsp;</td>
				<td align="right" class="RecordRow">&nbsp;<bean:write name="exchangeForm" property="npoint"/>&nbsp;</td>
			</tr>
		</logic:notEqual>
	</table>
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
	</table>
</html:form>