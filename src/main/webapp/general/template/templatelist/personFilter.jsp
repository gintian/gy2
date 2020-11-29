<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ page import="com.hrms.struts.valueobject.UserView"%>
<%@ page import="com.hrms.struts.constant.WebConstant"%>
<script language="JavaScript" src="/js/popcalendar.js"></script>
<script language="JavaScript" src="/js/function.js"></script>
<script language="JavaScript" src="/js/validate.js"></script>
<script language="JavaScript" src="/js/constant.js"></script>
<SCRIPT Language="JavaScript">dateFormat='yyyy.mm.dd'</SCRIPT>
<link href="/css/css1.css" rel="stylesheet" type="text/css">
<script language="javascript" src="/general/template/templatelist/templatelist.js"></script> 
<%

    UserView userView = (UserView) session.getAttribute(WebConstant.userView);
    String bosflag = "";
    if (userView != null) {
        bosflag = userView.getBosflag();
    }
%>

<base id="mybase" target="_self">
<hrms:themes></hrms:themes>

<html:form action="/general/template/personFilter">
 <%
     if (!"hcm".equals(bosflag)) {
 %>
<br>
<%
    }
%>
	<table width="590"  border="0" cellspacing="0" align="center" cellpadding="0"  >
		<THEAD>
			<tr>
				<td class="TableRow" style="margin-left: 100px">
					<logic:equal name="templateListForm" property="infor_type"
						value="1">
						<bean:message key="gz.bankdisk.personfilter" />
					</logic:equal>
					<logic:equal name="templateListForm" property="infor_type"
						value="2">
						<bean:message key="label.gz.zzfilter" />
					</logic:equal>
					<logic:equal name="templateListForm" property="infor_type"
						value="3">
						<bean:message key="label.gz.gwfilter" />
					</logic:equal>
				</td>
			</tr>
		</THEAD>
		<tr>
			<td>
				<table width="100%" border="0" cellspacing="0" align="center"cellpadding="0" class="ListTable">
					<tr>
						<td class="RecordRow_lr">
							<table>
								<tr>
									<td width="20%">
										<table width="100%" border="0" cellspacing="0" align="center"
											cellpadding="0">
											<tr>
												<td align="left">
													<bean:message key="gz.bankdisk.preparefield" />
												</td>
											</tr>
											<tr>
												<td align="center">
													<hrms:optioncollection name="templateListForm"
														property="allList" collection="list" />
													<html:select name="templateListForm" size="10"
														property="left_fields" multiple="multiple"
														ondblclick="additem('left_fields','right_fields');"
														style="height:250px;width:100%;font-size:9pt">
														<html:options collection="list" property="dataValue"
															labelProperty="dataName" />
													</html:select>
												</td>
											</tr>
										</table>
									</td>
									<td width="5%" align="center">
										<html:button styleClass="mybutton" property="b_addfield"
											onclick="additem('left_fields','right_fields');">
											<bean:message key="button.setfield.addfield" />
										</html:button>
										<br>
										<br>
										<html:button styleClass="mybutton" property="b_delfield"
											onclick="removeitem('right_fields');">
											<bean:message key="button.setfield.delfield" />
										</html:button>
									</td>
									<td width="20%">
										<table width="100%" border="0" cellspacing="0" align="center"
											cellpadding="0">
											<tr>
												<td width="100%" align="left">
													<bean:message key="gz.bankdisk.selectedfield" />
													&nbsp;&nbsp;
												</td>
											</tr>
											<tr>
												<td width="100%" align="left">
													<hrms:optioncollection name="templateListForm"
														property="selectedFieldList" collection="list" />
													<html:select name="templateListForm" size="10"
														property="right_fields" multiple="multiple"
														ondblclick="removeitem('right_fields');"
														style="height:250px;width:100%;font-size:9pt">
														<html:options collection="list" property="dataValue"
															labelProperty="dataName" />
													</html:select>
												</td>
											</tr>
										</table>
									</td>
								</tr>
							</table>
						</td>
					</tr>
				</table>
			</td>
		</tr>
		<tr >
			<td class="RecordRow" nowrap align="center"  style="height: 35px;" valign="middle">
				<input type="button" name="query" class="mybutton"
					value="<bean:message key="gz.bankdisk.nextstep"/>"
					onclick="bankdisk_choose('<bean:message key="gz.bankdisk.noreselect"/>');">
				<input type="button" name="cancel"
					value="<bean:message key="button.close"/>" class="mybutton"
					onclick="window.close()" />
				<input type="hidden" name="rightFields" value="">
				<input type="hidden" name="tabid" value="${templateListForm.tabid}" />

			</td>
		</tr>
	</table>
</html:form>