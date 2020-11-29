<%@ page contentType="text/html; charset=UTF-8" language="java"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<html:form action="/performance/kh_result/kh_result_interview">
	<table width="550"  border="0" cellpadding="0" cellspacing="0" align="center" style="height:390px;">
		<tr>
			<td align="left" width="570">
				<%--<table width="570" border="0" cellpadding="0" cellspacing="0" align="center">--%>
					<%--<tr>--%>
						<%--<td class="framestyle">--%>

							<table width="100%" border="0" cellpmoding="0" cellspacing="0" class="DetailTable"
								   cellpadding="0">
								<tr>
									<td width="90%" align='center'>
										<br><!-- 【6441】绩效管理：员工在查看团队考核计划的时候，查看面谈记录，显示了两个滚动条  jingq upd 2015.01.05 -->
										<html:textarea property="interview" name="khResultForm" rows='30' cols='75'
													   style="height:370px;"></html:textarea>
									</td>
								</tr>
							</table>
						<%--</td>--%>
						<%----%>
					<%--</tr>--%>
				<%--</table>--%>
			</td>
		</tr>
		<tr>
			<td align="center" style="height:35px;">
				<input type="button" class="mybutton" name="oo" value="<bean:message key="button.close"/>"
					   onclick="parent.parent.window.closeWindow();"/>
			</td>
		</tr>
	</table>
</html:form>