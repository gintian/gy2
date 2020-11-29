<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean"%>
<%@ taglib uri="/tags/struts-html" prefix="html"%>
<%@ taglib uri="/tags/struts-logic" prefix="logic"%>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms"%>

<%
	int i=0;
	session.setAttribute("dmltab","report_sort");
%>
<script type="text/javascript" src="/general/tipwizard/returnT.js"></script>
<link href="../../css/css1.css" rel="stylesheet" type="text/css">
<hrms:themes />
<body style="margin:10 0 0 5;" >
	<table width="90%" align="center" border="0" cellpadding="0" cellspacing="0">
	<tr>
		<td valign="top">

			<form name="reportStateSortForm" method="post" action="" style="margin-top: 8px;">
				<table width="100%" border="0" cellspacing="0" align="center" cellpadding="0" class="ListTable">
					<thead>
						<tr>
							<td align="center" class="TableRow" nowrap>
								<bean:message key="reporttypelist.tsortname" />
								&nbsp;
							</td>
							<td align="center" class="TableRow" nowrap>
								<bean:message key="reporttypelist.sortid" />
								&nbsp;
							</td>
							<td align="center" class="TableRow" nowrap>
								<bean:message key="reporttypelist.sort" />
							</td>
							
							<td align="center" class="TableRow" nowrap>
								<bean:message key="column.sys.status" />
							</td>
							
						</tr>
					</thead>

					<hrms:extenditerate id="element" name="reportStateSortForm" property="reportTypeList.list" indexes="indexes" pagination="reportTypeList.pagination" pageCount="15" scope="session">
					          <%
					          if(i%2==0)
					          {
					          %>
					          <tr class="trShallow" onclick='tr_onclick(this,"#F3F5FC");' >
					          <%}
					          else
					          {%>
					          <tr class="trDeep" onclick='tr_onclick(this,"#E4F2FC");'  >
					          <%
					          }
					          i++;          
					          %>  
							<td align="left" class="RecordRow" nowrap>
								<bean:write name="element" property="string(name)" filter="false" />
								&nbsp;
							</td>
							<td align="left" class="RecordRow" nowrap>
								<bean:write name="element" property="string(tsortid)" filter="false" />
								&nbsp;
							</td>
							<td align="left" class="RecordRow" nowrap>
								<bean:write name="element" property="string(fontname)" filter="false" />
								&nbsp;
							</td>
							<td align="left" class="RecordRow" nowrap>
								<bean:write name="element" property="string(cbase)" filter="false" />
								&nbsp;
							</td>
						</tr>
					</hrms:extenditerate>

				</table>

				<table width="100%"   class='RecordRowP' align="center">
					<tr>
						<td valign="bottom" class="tdFontcolor">
							<bean:message key="label.page.serial"/>
							<bean:write name="reportStateSortForm" property="reportTypeList.pagination.current" filter="true" />
							<bean:message key="label.page.sum"/>
							<bean:write name="reportStateSortForm" property="reportTypeList.pagination.count" filter="true" />
							<bean:message key="label.page.row"/>
							<bean:write name="reportStateSortForm" property="reportTypeList.pagination.pages" filter="true" />
							<bean:message key="label.page.page"/>
						</td>
						<td align="right" nowrap class="tdFontcolor">
							<p align="right">
								<hrms:paginationlink name="reportStateSortForm" property="reportTypeList.pagination" nameId="reportTypeList">
								</hrms:paginationlink>
						</td>
					</tr>
				</table>
				
				<table width="100%"  align="center" >
				<Tr><td width='100%' align='center' >
				<hrms:tipwizardbutton flag="report" target="3" formname="reportStateSortForm"/>
				</td></tr>
				</table>
			</form>

		</td>
	</tr>
</table>
</body>