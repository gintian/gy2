<%@ page contentType="text/html; charset=UTF-8"%>
<%@page import="com.hjsj.hrms.businessobject.train.TrainClassBo"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean"%>
<%@ taglib uri="/tags/struts-html" prefix="html"%>
<%@ taglib uri="/tags/struts-logic" prefix="logic"%>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms"%>
<%@ page import="com.hrms.struts.valueobject.UserView"%>
<%@ page import="com.hrms.struts.constant.WebConstant"%>
<%
	    UserView userView = (UserView) session.getAttribute(WebConstant.userView);
	    String bosflag = "";
	    if (userView != null)
	    {
		bosflag = userView.getBosflag();
		bosflag = bosflag != null ? bosflag : "";
	    }
%>
<style type="text/css">
	body{
		padding-left: 5px;
	}
</style>
<script type="text/javascript">
	function returnto(){
		trainResourceForm.action="/train/resource/trainRescList.do?b_query=link&type=2&returnvalue=";
	    trainResourceForm.submit();
		}
	
</script>
<html:form action="/train/resource/trainRescList/teachlesson">
	<%int i=0; %>
	<table width="100%" border="0" cellspacing="0" align="center"
		cellpadding="0" class="ListTable">
		<tr>
			<td>
				<div >
				<table width="100%" border="0" cellspacing="0" align="center" cellpadding="0" class="common_border_color">
					<tr class="fixedHeaderTr">
							 <td align="left" class="TableRow" style="border-bottom: none;" nowrap>&nbsp;<bean:message key="conlumn.infopick.educate.teachername"/>:&nbsp;<bean:write  name="trainResourceForm" property="teachername" filter="false"/>
							 </td>
							</tr>
				</table>
					<table width="100%" border="0" cellspacing="0" align="center"
						cellpadding="0" class="ListTableF">
						<thead>
							<tr class="fixedHeaderTr">
								<logic:iterate id="element" name="trainResourceForm" property="fields">
									<logic:equal value="1" name="element" property="state">
										<td align="center" class="TableRow" nowrap>
											<bean:write  name="element" property="itemdesc" filter="true"/>
										</td>
									</logic:equal>
								</logic:iterate>
							</tr>
						</thead>
						<hrms:paginationdb id="element" name="trainResourceForm"
							sql_str="trainResourceForm.strsql" table=""
							where_str="trainResourceForm.strwhere"
							columns="trainResourceForm.columns" page_id="pagination"
							pagerows="${trainResourceForm.pagerows}"
							order_by="order by ${trainResourceForm.primaryField}">
							<%
									if (i % 2 == 0)
									{
							%>
							<tr class="trShallow">
								<%
										} else
										{
								%>
							<tr class="trDeep">
								<%
										}
										i++;
								%>
								<logic:iterate id="fielditem" name="trainResourceForm" property="fields">
									<logic:equal value="1" name="fielditem" property="state">
										<logic:equal name="fielditem" property="itemtype" value="N">
											<td align="right" class="RecordRow" nowrap>&nbsp;
										</logic:equal>
										<logic:equal name="fielditem" property="itemtype" value="D">
											<td align="center" class="RecordRow" nowrap>&nbsp;
										</logic:equal>
										<logic:notEqual name="fielditem" property="itemtype" value="N">
										<logic:notEqual name="fielditem" property="itemtype" value="D">
											<td align="left" class="RecordRow" nowrap>&nbsp;
										</logic:notEqual>
										</logic:notEqual>
											<logic:equal name="fielditem" property="codesetid" value="0">
												<logic:equal name="fielditem" property="itemid" value="r4105">
													<bean:define id="r4105" name="element" property="r4105"></bean:define>
													&nbsp;<%=TrainClassBo.getProgrammeName(r4105.toString()) %>&nbsp;
												</logic:equal>
												<logic:notEqual name="fielditem" property="itemid" value="r4105">
												&nbsp;<bean:write name="element" property="${fielditem.itemid}" filter="false"/>
												</logic:notEqual>
											</logic:equal>
											<logic:notEqual name="fielditem" property="codesetid" value="0">
												<logic:notEqual name="fielditem" property="itemid" value="r4105">
													&nbsp;<bean:write name="element" property="${fielditem.itemid}" filter="false"/>
												</logic:notEqual>
												<logic:equal name="fielditem" property="itemid" value="r4105">
													<bean:define id="r4105" name="element" property="r4105"></bean:define>
													&nbsp;<%=TrainClassBo.getProgrammeName(r4105.toString()) %>&nbsp;
												</logic:equal>
											</logic:notEqual>
										&nbsp;<script>document.write("</td>");</script>
									</logic:equal>
								</logic:iterate>
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
							<hrms:paginationtag name="trainResourceForm"
								pagerows="${trainResourceForm.pagerows}" property="pagination"
								scope="page" refresh="true"></hrms:paginationtag>
						</td>
						<td align="right" nowrap class="tdFontcolor">
							<p align="right">
								<hrms:paginationdblink name="trainResourceForm"
									property="pagination" nameId="trainResourceForm" scope="page">
								</hrms:paginationdblink>
						</td>
					</tr>
				</table>
			</td>
		</tr>
	</table>
	<table width="100%">
		<tr>
			<td align="left">
				<input type='button' class="mybutton" property="returnButton"
					onclick="returnto();"
					value='<bean:message key='reportcheck.return'/>' />
			</td>
		</tr>
	</table>
</html:form>