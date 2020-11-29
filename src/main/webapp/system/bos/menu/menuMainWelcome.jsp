<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean"%>
<%@ taglib uri="/tags/struts-html" prefix="html"%>
<%@ taglib uri="/tags/struts-logic" prefix="logic"%>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms"%>
<%@page import="java.util.*,org.apache.commons.beanutils.LazyDynaBean"%>
<%
	int i = 0;
%>
<HTML>
	<HEAD>
		<TITLE></TITLE>
		<script LANGUAGE=javascript src="/js/function.js"></script>
		<script LANGUAGE=javascript src="/js/validate.js"></script>
		<script LANGUAGE=javascript src="/system/bos/menu/menument.js"></script>
		<script type="text/javascript">

		</script>
	</HEAD>
	
	<html:form action="/system/bos/menu/menuMain.do?b_search=query">
	<html:hidden name="menuMainForm" property="parentid"/>
		<table width="100%" border="0" cellspacing="0" align="center"
			cellpadding="0" class="ListTable complex_border_color" style="margin-top:2px;">
			<thead>
				<tr>
					<td align="center" class="TableRow" nowrap>
						<bean:message key="lable.menu.main.id" />
						&nbsp;
					</td>
					<td align="center" class="TableRow" nowrap>
						<bean:message key="lable.menu.main.name" />
						&nbsp;
					</td>
					<td align="center" class="TableRow" nowrap>
						<bean:message key="lable.menu.main.func_id" />
						&nbsp;
					</td>
					<td align="center" class="TableRow" nowrap>
						<bean:message key="lable.menu.main.url" />
						&nbsp;
					</td>
					<td align="center" class="TableRow" nowrap>
						<bean:message key="lable.menu.main.target" />
						&nbsp;
					</td>
					<td align="center" class="TableRow" nowrap>
									<bean:message key="lable.portal.panel.hide" />
								</td>
					<td align="center" class="TableRow" nowrap style="border-right:none;">
						<bean:message key="lable.menu.main.icon" />
						&nbsp;
					</td>
					<td align="center" class="TableRow" nowrap style="border-right:none;">
                        <bean:message key="lable.menu.main.addvalidate" />
                        &nbsp;
                    </td>
				</tr>
			</thead>
			<hrms:extenditerate id="element" name="menuMainForm" property="menuMainForm.list" indexes="indexes" pagination="menuMainForm.pagination" pageCount="${menuMainForm.pagerows}" scope="session">
				
				<%
					if (i % 2 == 0) {
				%>
				<tr class="trShallow">
					<%
						} else {
					%>
				
				<tr class="trDeep">
					<%
						}
									i++;
					%>
					<!--td align="center" class="RecordRow" nowrap>
						
					 <hrms:checkmultibox name="menuMainForm" property="menuMainForm.select" value="true" indexes="indexes"/>&nbsp;
					<html:hidden name="element" property="codeitemid" />
					</td -->

					<td align="center" class="RecordRow" nowrap height="100px"
						style="word-break: break-all">

						&nbsp;<bean:write name="element" property="codeitemid" filter="true" />
						&nbsp;

					</td>

					<td align="center" class="RecordRow" nowrap width="110"
						style="word-break: break-all">
						&nbsp;<bean:write name="element" property="codeitemdesc" filter="true" />
						&nbsp;

					</td>
					<td align="left" class="RecordRow" nowrap width="250"
						style="word-break: break-all">
						&nbsp;<bean:write name="element" property="codeitemfunc_id" filter="true" />
						&nbsp;

					</td>
					<td align="left" class="RecordRow" nowrap width="360"
						style="word-break: break-all">
						&nbsp;<bean:write name="element" property="codeitemurl" filter="true" />
						&nbsp;

					</td>
					<td align="center" class="RecordRow" nowrap width="70"
						style="word-break: break-all">
						&nbsp;<bean:write name="element" property="codeitemtarget" filter="true" />
						&nbsp;

					</td>
					<td align="center" class="RecordRow" nowrap width="40"
						style="word-break: break-all">
						&nbsp;<bean:write name="element" property="menuhide" filter="true" />
						&nbsp;

					</td>
					<td align="left" class="RecordRow" nowrap width="100"
						style="word-break: break-all;border-right:none;">
						&nbsp;<bean:write name="element" property="codeitemicon" filter="true" />
						&nbsp;
					</td>
                    <td align="left" class="RecordRow" nowrap width="100"
                        style="word-break: break-all;border-right:none;">
                        &nbsp;<bean:write name="element" property="validate" filter="true" />
                        &nbsp;
                    </td>
				</tr>
			</hrms:extenditerate>
			
		<tr><td colspan="8">
		<table width="100%"  align="right"  class="RecordRowP" style="border:none;">
		<tr>
			<td valign="bottom" class="tdFontcolor">
		    	<hrms:paginationtag name="menuMainForm" pagerows="${menuMainForm.pagerows}" property="menuMainForm.pagination" scope="session" refresh="true"></hrms:paginationtag>
			</td>
			<td align="right" nowrap class="tdFontcolor">
				<p align="right">
					<hrms:paginationlink name="menuMainForm" property="menuMainForm.pagination" nameId="menuMainForm" propertyId="roleListProperty">
					</hrms:paginationlink>
			</td>
		</tr>
	</table>
	</td>
	</tr>
</table>
	</html:form>
	<script>



</script>
</HTML>
