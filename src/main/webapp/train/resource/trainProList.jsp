<%@ page contentType="text/html; charset=UTF-8"%>
<%@page import="org.apache.commons.beanutils.LazyDynaBean"%>
<%@page import="com.hrms.frame.codec.SafeCode"%>
<%@page import="com.hjsj.hrms.utils.PubFunc"%>
<%@page import="com.hjsj.hrms.businessobject.train.resource.TrainResourceBo"%>
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
<script language="javascript" src="/train/resource/trainResc.js"></script>
<script type="text/javascript" src="../../js/hjsjUrlEncode.js"></script>
<script language="javascript" src="/js/validate.js"></script>

<style>
body{text-align: center;}
.myfixedDiv
{ 
	overflow:auto; 
	height:expression(document.body.clientHeight-150);
	width:expression(document.body.clientWidth-10); 
	border-collapse: collapse;
	border-bottom: #99BBE8 1pt solid; 
	border-left: #99BBE8 1pt solid; 
	border-right: #99BBE8 1pt solid; 
	border-top: #99BBE8 1pt solid;
}

.fixedHeaderTr{
 	border-bottom:1px solid #C4D8EE;
 	border-right:1px solid #C4D8EE;
 	border-left:1px solid #C4D8EE;
 	border-top:1px solid #C4D8EE;
}
</style>
<html:form action="/train/resource/trainProList">
	<html:hidden name="trainProjectForm" property="strParam"
		styleId="strParam" />
	<%
	int i = 0;
	%>
	<html:hidden name="trainProjectForm" property="code" styleId="code" />
	<table width="100%" border="0" cellspacing="0" align="center" cellpadding="0" class="ListTable">
		<tr>
			<td>
				<div class="myfixedDiv complex_border_color" style="padding: 0;">
					<table width="100%" border="0"  cellspacing="0" align="center"
						cellpadding="1">
						<thead>
							<tr class="fixedHeaderTr">
								<td align="center" class="TableRow" style="border-left: none; border-top: none;" nowrap>
									<input type="checkbox" name="selbox"
										onclick="batch_select(this,'pagination.select');"
										title='<bean:message key="label.query.selectall"/>'>&nbsp;
								</td>

								<logic:iterate id="element" name="trainProjectForm"
									property="fields">
									<logic:notEqual value="0" name="element" property="state">
									<td align="center" class="TableRow" style="border-left: none; border-top: none;" nowrap>
										&nbsp;<bean:write name="element" property="itemdesc" filter="true" />&nbsp;
									</td>
									</logic:notEqual>
								</logic:iterate>
								<hrms:priv func_id="3230002" module_id="">
								<td align="center" class="TableRow" style="border-left: none; border-top: none;" nowrap>
									&nbsp;<bean:message key="system.infor.oper" />&nbsp;
								</td>
								</hrms:priv>
<%--								<td align="center" class="TableRow" style="border-left: none; border-top: none;border-right: none;" nowrap>
									&nbsp;评估&nbsp;
								</td>--%>
							</tr>
						</thead>
						<hrms:paginationdb id="element2" name="trainProjectForm"
							sql_str="trainProjectForm.strsql" table=""
							where_str="trainProjectForm.strwhere"
							columns="trainProjectForm.columns" page_id="pagination"
							pagerows="${trainProjectForm.pagerows}" order_by="order by r1301">
							
							<bean:define id="id" name="element2" property="r1301"></bean:define>

							<% String r1301 = SafeCode.encode(PubFunc.encrypt(id.toString()));
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
								<td align="center" class="RecordRow" style="border-left: none; border-top: none;" nowrap>&nbsp;
									<hrms:checkmultibox name="trainProjectForm"
										property="pagination.select" value="true" indexes="indexes" />
									<Input type='hidden'
										value='<%=r1301 %>' />
								&nbsp;</td>


								<logic:iterate id="element1" name="trainProjectForm"
									property="fields">
									<logic:notEqual value="0" name="element1" property="state">
									<bean:define id="nid" name="element1" property="itemid" />

									<logic:equal name="element1" property="itemtype" value="M">
										<bean:define id="beizhu" name="element2" property="${nid}"/>
										<%
											String remark = TrainResourceBo.substr(beizhu.toString());
										%>
										<td align="left" class="RecordRow" style="border-left: none; border-top: none;" nowrap>&nbsp;
											<a
												href='javascript:editMemoFild2("<%=r1301 %>","${nid}");'>
												<%=remark %>
											</a>
										&nbsp;</td>
									</logic:equal>
									<logic:notEqual name="element1" property="itemtype" value="M">
										<logic:equal name="element1" property="itemtype" value="N">
											<td align="right" class="RecordRow" style="border-left: none; border-top: none;" nowrap>&nbsp;
										</logic:equal>
										<logic:equal name="element1" property="itemtype" value="D">
											<td align="right" class="RecordRow" style="border-left: none; border-top: none;" nowrap>&nbsp;
										</logic:equal>
										<logic:equal name="element1" property="itemtype" value="A">
											<td align="left" class="RecordRow" style="border-left: none; border-top: none;" nowrap>&nbsp;
										</logic:equal>

										<logic:equal name="element1" property="itemtype" value="A">
											<logic:equal name="element1" property="codesetid" value="0">
												<bean:define id="strvalue" name="element2" property="${nid}" type="java.lang.String" />
												<logic:notEqual value="r1302" name="element1" property="itemid">
													<%=strvalue.length()>30?strvalue.substring(0,30)+"...":strvalue %>
												</logic:notEqual>
												<logic:equal value="r1302" name="element1" property="itemid">
													<a href="javascript:edit2('<%=r1301 %>');"><%=strvalue.length()>30?strvalue.substring(0,30)+"...":strvalue %></a>
												</logic:equal>
											</logic:equal>
											<logic:notEqual name="element1" property="codesetid"
												value="0">
												<logic:notEqual name="element1" property="codesetid"
													value="UN">
													<bean:define id="codesetid" name="element1"
														property="codesetid" />
													<hrms:codetoname codeid="${codesetid}" name="element2"
														codevalue="${nid}" codeitem="codeitem" scope="page" />
													<bean:write name="codeitem" property="codename" />

												</logic:notEqual>
												<logic:equal name="element1" property="codesetid" value="UN">
													<logic:notEqual name="element2" property="${nid}"
														value="HJSJ">
														<hrms:codetoname codeid="UN" name="element2"
															codevalue="${nid}" codeitem="codeitem" scope="page" />
														<bean:write name="codeitem" property="codename" />
													</logic:notEqual>
													<logic:equal name="element2" property="${nid}" value="HJSJ">
														<bean:message key="jx.khplan.hjsj" />
													</logic:equal>
												</logic:equal>
											</logic:notEqual>
										</logic:equal>
										<logic:notEqual name="element1" property="itemtype" value="A">
											<bean:write name="element2" property="${nid}" filter="false" />
										</logic:notEqual>
										&nbsp;<script>document.write("</td>");</script>
									</logic:notEqual>
									</logic:notEqual>
								</logic:iterate>
								<hrms:priv func_id="3230002" module_id="">
								<td align="center" class="RecordRow" style="border-left: none; border-top: none;" nowrap>
									<img src="/images/edit.gif" BORDER="0" style="cursor:hand;"
										onclick="edit2('<%=r1301 %>')">
								</td>
								</hrms:priv>
<%--								<td align="center" class="RecordRow" style="border-left: none; border-top: none;border-right: none;" nowrap>
									<img src="/images/view.gif" BORDER="0" style="cursor:hand;" onclick="javascript:showAssess('r13','<%=r1301 %>');" alt="评估结果"/>
								</td>--%>
							</tr>
						</hrms:paginationdb>
					</table>
				</div>
			</td>
		</tr>
		<tr>
			<td>
				<table width="100%" align="center" class="RecordRowP">
					<tr>
						<td valign="middle" class="tdFontcolor">
							<hrms:paginationtag name="trainProjectForm"
								pagerows="${trainProjectForm.pagerows}" property="pagination"
								scope="page" refresh="true"></hrms:paginationtag>
						</td>
						<td align="right" nowrap class="tdFontcolor">
							<p align="right">
								<hrms:paginationdblink name="trainProjectForm"
									property="pagination" nameId="trainProjectForm" scope="page">
								</hrms:paginationdblink>
						</td>
					</tr>
				</table>
			</td>
		</tr>
	</table>
	<table width="100%">
		<tr>
			<td align="left" style="padding-left: 0;padding-top: 5px;">
			<hrms:priv func_id="3230001" module_id="">	
				<input type='button' class="mybutton" property="b_add"
					onclick='add2()' value='<bean:message key="button.insert"/>' />
		    </hrms:priv>
		    <hrms:priv func_id="3230003" module_id="">
				<input type='button' class="mybutton" property="b_delete"
					onclick='del2()' value='<bean:message key="button.delete"/>' />
			</hrms:priv>
			<hrms:priv func_id="3230004">
				<input type='button' class="mybutton" property="b_query"
					onclick='search2("1")' value='<bean:message key="infor.menu.query"/>' />
			</hrms:priv>
			<hrms:priv func_id="3230005">
				<input type='button' class="mybutton" property="b_export"
					onclick='exportExcel2()'
					value='<bean:message key="goabroad.collect.educe.excel"/>' />
			</hrms:priv>
				<logic:equal value="dxt" name="trainProjectForm" property="returnvalue">
				<%
					    if (bosflag.equals("hl")||bosflag.equals("hcm"))
					    {
				%>
				<input type='button' class="mybutton" name="returnButton"
					onclick='returnFirst();'
					value='<bean:message key='reportcheck.return'/>' />
				<%
				}
				%>
				</logic:equal>
			</td>
		</tr>
	</table>
</html:form>
