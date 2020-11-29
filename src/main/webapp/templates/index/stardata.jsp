<%@ page contentType="text/html; charset=UTF-8"%>
<%@taglib uri="/tags/struts-bean" prefix="bean"%>
<%@taglib uri="/tags/struts-html" prefix="html"%>
<%@taglib uri="/tags/struts-logic" prefix="logic"%>
<%@taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms"%>
<%@page import="com.hrms.struts.valueobject.UserView"%>
<%@page import="com.hrms.struts.constant.WebConstant"%>
<script type="text/javascript" src="/js/wz_tooltip.js"></script>
<link rel="stylesheet" href="/css/css1.css" type="text/css">
<%
	int i = 0;
	UserView userView=(UserView)session.getAttribute(WebConstant.userView);
	String bosflag="";
	if(userView!=null){
	 	bosflag = userView.getBosflag();
	}
%>
<hrms:themes />
<style>
.fixedDiv2 {
	margin-right:0px!important;
	<%if("hl".equals(bosflag)){%>
		border-color:#C4D8EE
	<%}%>
}

.no_t {
	border-top: 0pt solid;
}
</style>
<html:form action="/templates/index/star_data">
	<table width="99%" border="0" cellspacing="0" align="center"
		cellpadding="0" id='aa' style=''>
		<tr>
			<td width="100%" nowrap>
				<div class="fixedDiv2">
					<table width="100%" border="0" cellspacing="0" align="center"
						cellpadding="0" class="ListTable">
						<tr class="fixedHeaderTr">
							<logic:iterate id="info" name="homeForm" property="starfields"
								indexId="index">
								<logic:equal name="index" value="0">
									<logic:equal name="info" property="visible" value="true">
										<td align="center" class="TableRow_top" nowrap>
											<bean:write name="info" property="itemdesc" filter="true" />
										</td>
									</logic:equal>
								</logic:equal>
								<logic:notEqual name="index" value="0">
									<logic:equal name="info" property="visible" value="true">
										<td align="center" class="TableRow_left no_t" nowrap>
											<bean:write name="info" property="itemdesc" filter="true" />
										</td>
									</logic:equal>
								</logic:notEqual>
							</logic:iterate>
						</tr>
						<hrms:paginationdb id="element" allmemo="1" name="homeForm"
							sql_str="homeForm.strsql" columns="homeForm.columns"
							order_by="homeForm.order" page_id="pagination"
							pagerows="${homeForm.pagerows}">
							<%
								if (i % 2 == 0) {
							%>
							<tr class="trShallow"
								onMouseOver="javascript:tr_onclick(this,'')">
								<%
									} else {
								%>
							
							<tr class="trDeep"
								onMouseOver="javascript:tr_onclick(this,'DDEAFE')">
								<%
									}
												i++;
								%>
								<bean:define id="a0100" name="element" property="a0100" />
								<logic:iterate id="info" name="homeForm" property="starfields" indexId="index">
									<%String style=""; %>
									<logic:equal name="index" value="0">
										<%style="RecordRow_right"; %>
									</logic:equal>
									<logic:notEqual name="index" value="0">
										<%style="RecordRow_left"; %>
									</logic:notEqual>
									<logic:equal name="info" property="visible" value="true">
										<logic:equal name="info" property="itemtype" value="M">
											<td align="left" style="word-break: break-all;"
												class="<%=style %>" onmouseout='UnTip();'
												onmouseover="Tip('<bean:write  name="element" property="${info.itemid}" filter="false"/>',STICKY ,true);"
												nowrap>
										</logic:equal>
										<logic:equal name="info" property="itemtype" value="A">
											<td align="left" class="<%=style %>" nowrap>
										</logic:equal>
										<logic:equal name="info" property="itemtype" value="D">
											<td align="left" class="<%=style %>" nowrap>
										</logic:equal>
										<logic:equal name="info" property="itemtype" value="N">
											<td align="left" class="<%=style %>" nowrap>
										</logic:equal>
										<logic:equal name="info" property="codesetid" value="0">
											<logic:notEqual name="info" property="itemid" value="a0101">
												<logic:equal name="info" property="itemtype" value="M">
													<span
														style="width: 200px; height: 15px; overflow: hidden; text-overflow: ellipsis;"><bean:write
															name="element" property="${info.itemid}" filter="false" />
													</span>
												</logic:equal>
												<logic:notEqual name="info" property="itemtype" value="M">
													<bean:write name="element" property="${info.itemid}"
														filter="false" />
												</logic:notEqual>
											</logic:notEqual>
											<logic:equal name="info" property="itemid" value="a0101">
												<bean:write name="element" property="a0101" filter="true" />
											</logic:equal>
										</logic:equal>
										<logic:notEqual name="info" property="codesetid" value="0">
											<logic:notEqual name="info" property="itemid" value="e01a1">
												<logic:notEqual name="info" property="itemid" value="a0101">
													<logic:equal name="info" property="codesetid" value="UM">
														<hrms:codetoname codeid="UM" name="element"
															codevalue="${info.itemid}" codeitem="codeitem"
															scope="page" />
														<logic:notEqual name="codeitem" property="codename"
															value="">
															<bean:write name="codeitem" property="codename" />
														</logic:notEqual>
														<logic:equal name="codeitem" property="codename" value="">
															<hrms:codetoname codeid="UN" name="element"
																codevalue="${info.itemid}" codeitem="codeitem"
																scope="page" />
															<bean:write name="codeitem" property="codename" />
														</logic:equal>
													</logic:equal>
													<logic:notEqual name="info" property="codesetid" value="UM">
														<hrms:codetoname codeid="${info.codesetid}" name="element"
															codevalue="${info.itemid}" codeitem="codeitem"
															scope="page" />
														<bean:write name="codeitem" property="codename" />
													</logic:notEqual>
												</logic:notEqual>
											</logic:notEqual>
											<logic:equal name="info" property="itemid" value="e01a1">
												<hrms:codetoname codeid="@K" name="element"
													codevalue="e01a1" codeitem="codeitem" scope="page" />
												<bean:write name="codeitem" property="codename" />
											</logic:equal>
										</logic:notEqual>
										</td>
									</logic:equal>
								</logic:iterate>
							</tr>
						</hrms:paginationdb>
					</table>
				</div>
				<table width="100%" align="center" class="RecordRowP">
					<tr>
						<td valign="bottom" class="tdFontcolor">
							<hrms:paginationtag name="homeForm"
								pagerows="${homeForm.pagerows}" property="pagination"
								scope="page" refresh="true"></hrms:paginationtag>
						</td>
						<td align="right" nowrap class="tdFontcolor">
							<p align="right">
								<hrms:paginationdblink name="homeForm" property="pagination"
									nameId="homeForm" scope="page">
								</hrms:paginationdblink>
							</p>
						</td>
					</tr>
				</table>
				<table align="center">
					<tr>
						<td align="left">
							<!-- <input type="button" name="addbutton"  value="<bean:message key="button.query.viewphoto"/>" class="mybutton" onclick='viewPhoto();' >
	       		 -->
							<input type="button" name="addbutton" value="关闭" class="mybutton"
								onclick="returnQ('');">
						</td>
					</tr>
				</table>
			</td>
		</tr>
	</table>
</html:form>
<script language="javascript">
	function returnQ()
{
     window.close();
     return ;
}
/*
function viewPhoto()
{
	homeForm.action="/templates/index/star_employees.do?b_view_photo=link";
	homeForm.target="_self";
	homeForm.submit()
}*/
</script>