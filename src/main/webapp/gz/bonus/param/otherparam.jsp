<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean"%>
<%@ taglib uri="/tags/struts-html" prefix="html"%>
<%@ taglib uri="/tags/struts-logic" prefix="logic"%>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms"%>
<script language="JavaScript" src="param.js"></script>
<style>
.myfixedDiv
{ 
	overflow:auto; 
	height:expression(document.body.clientHeight-100);
	width:expression(document.body.clientWidth-30); 
	BORDER-BOTTOM: #94B6E6 1pt solid; 
    BORDER-LEFT: #94B6E6 1pt solid; 
    BORDER-RIGHT: #94B6E6 1pt solid; 
    BORDER-TOP: #94B6E6 0pt solid ; 
    margin-left: auto;
	margin-right: auto;

    
}
</style>
<hrms:themes />
	<html:form action="/gz/bonus/param/otherparam">
					<div class="myfixedDiv" align="center">
						<table width="100%" border="0" cellspacing="0" align="center"
							cellpadding="0" class="ListTable">
							<%
							int i = 0;
							%>

						<tr class="fixedHeaderTr">
								<td align="center" class="TableRow_right common_background_color common_border_color" nowrap width='40'>
									<input type="checkbox" name="selbox"
										onclick="batch_select(this, 'codeitemid');">
								</td>
								<td align="center" class="TableRow" nowrap>
									<bean:message key="conlumn.codeitemid.caption" />
								</td>
								<td align="center" class="TableRow" nowrap>
									<bean:message key="kq.item.name" />
								</td>
								<td align="center" class="TableRow_left common_background_color common_border_color" nowrap width='80'>
									<bean:message key="kq.item.edit" />
								</td>
							</tr>

							<logic:iterate id="element" name="bonusParamForm"
								property="codeDataList">
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
									<td align="center" class="RecordRow_right common_border_color" nowrap>
										<input type='checkbox' name='codeitemid'
											value='<bean:write name="element" property="codeitemid" filter="true"/>:<bean:write name="element" property="codesetid" filter="true"/>' />
									</td>
									<td align="left" class="RecordRow" nowrap>
										<bean:write name="element" property="codeitemid" filter="true" />
									</td>
									<td align="left" class="RecordRow" nowrap>
										<bean:write name="element" property="codeitemdesc" filter="true" />
									</td>
									<td align="center" class="RecordRow_left common_border_color" nowrap>
										<img src="/images/edit.gif" BORDER="0" style="cursor:hand;"
												onclick="update('<bean:write name="element" property="codeitemid" filter="true"/>')">
									</td>									
								</tr>
							</logic:iterate>
						</table>
					</div>
		<table width="100%" >
			<tr>
				<td align="center">
					<input type='button' class="mybutton" name="b_add"
						onclick='addCodeItem()'
						value='<bean:message key="kq.emp.button.add"/>' />					
					<input type='button' class="mybutton" onclick='del()'
						value='<bean:message key="button.delete"/>' />
				</td>
			</tr>
		</table>
			<html:hidden name="bonusParamForm" property="paramStr"/>
	<html:hidden name="bonusParamForm" property="menuid"/>
	</html:form>

