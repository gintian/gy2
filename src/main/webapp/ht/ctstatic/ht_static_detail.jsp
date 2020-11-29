<%@ page contentType="text/html; charset=UTF-8" language="java"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ page import="org.apache.commons.beanutils.LazyDynaBean" %>
<%@ page import="com.hjsj.hrms.utils.PubFunc" %>
<script language="JavaScript" src="./ht_static.js"></script>
<script language="JavaScript" src="/module/utils/js/template.js"></script>
<style>
.td_border_none {border: 0px;}
.td_border_t0 {border-top: 0px;}
.td_border_l0 {border-left-width: 0px;}
.td_border_r0 {border-right: 0px;}
.td_border_b0 {border-bottom: 0px;}
</style>
<html:form action="/ht/ctstatic/ht_static_detail">
				<div class="fixedDiv5">
					<table width="100%" border="0" cellspacing="0" align="center"
						cellpadding="0" class="ListTableF td_border_none">
						<tr class="fixedHeaderTr1">
						    <% int firstCol = 0; %>
							<logic:iterate id="element0" name="stAnalysisForm"
								property="items" indexId="index">
								<% firstCol++; %>
								<td align="center" class="TableRow <% if (firstCol==1) { %>td_border_l0<% } else { %> td_border_r0 <% } %>" nowrap>
									<bean:write name='element0' property='itemdesc' filter='true' />
								</td>
							</logic:iterate>
						</tr>
						<%
							int i = 0;
							String cols = "";
						%>
						<hrms:extenditerate id="element" name="stAnalysisForm"
							property="setlistform.list" indexes="indexes"
							pagination="setlistform.pagination" pageCount="20"
							scope="session">
							<%if (i % 2 == 0) {%>
							<tr class="">
							<%} else {%>
							<tr class="">
							<%}i++; firstCol=0;%>
								<logic:iterate id="element1" name="stAnalysisForm"
									property="items" indexId="index">
									<%
										LazyDynaBean abean = (LazyDynaBean) pageContext.getAttribute("element1");
										String itemid = (String) abean.get("itemid");
										String align = (String) abean.get("align");
										if (i == 1)
											cols += "," + itemid;
										
										firstCol++;
									%>
									<td align="<%=align%>" class="RecordRow <% if (firstCol==1) { %>td_border_l0<% } else { %> td_border_r0 <% } %>" nowrap>
										&nbsp;<bean:write name='element' property='<%=itemid%>' filter='true' />&nbsp;
									</td>
								</logic:iterate>
							</tr>
						</hrms:extenditerate>
					</table>
				</div>
				<table width="100%" align="center" cellspacing="0" cellpadding="0" border="0">
					<tr>
						<td valign="bottom" align="left" class="tdFontcolor RecordRow td_border_r0 td_border_t0">
							第
							<bean:write name="stAnalysisForm"
								property="setlistform.pagination.current" filter="true" />
							页 共
							<bean:write name="stAnalysisForm"
								property="setlistform.pagination.count" filter="true" />
							条 共
							<bean:write name="stAnalysisForm"
								property="setlistform.pagination.pages" filter="true" />
							页
						</td>
						<td align="right" nowrap class="tdFontcolor RecordRow td_border_l0 td_border_t0">
							<p align="right">
								<hrms:paginationlink name="stAnalysisForm"
									property="setlistform.pagination" nameId="setlistform"
									propertyId="roleListProperty">
								</hrms:paginationlink>
						</td>
					</tr>
				</table>
	<div style="margin-top: 5px;" align="left">
                <input type="button"
                    value="<bean:message key='goabroad.collect.educe.excel'/>"
                    onclick="exportExcel('cols');" Class="mybutton">
                <hrms:priv func_id="330031">
                    <input type="button" value="<bean:message key='ht.param.setIndex'/>"
                        onclick="setFild('${stAnalysisForm.dbname}','${stAnalysisForm.orgcode}','${stAnalysisForm.itemid}','${stAnalysisForm.itemvalue}');"
                        Class="mybutton">
                </hrms:priv>
                <input type="button" value="<bean:message key='button.return'/>"
                    onclick="goback('${stAnalysisForm.code }');" Class="mybutton">
	</div>
	
	
	<input type="hidden" name="cols" id="cols" value="<%=PubFunc.encrypt(cols.substring(1))%>"/>
</html:form>
