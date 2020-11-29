
<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean"%>
<%@ taglib uri="/tags/struts-html" prefix="html"%>
<%@ taglib uri="/tags/struts-logic" prefix="logic"%>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms"%>

<script language="javascript">
function next()
{
	var items = document.perRelationForm.right_fields.options; 
	if(items.length==0)
	{
		alert(GENERAL_SELECT_ITEMNAME+"!");
		return;
	}
	
  	setselectitem('right_fields');
  	
	perRelationForm.action='/performance/options/kh_relation/query.do?b_next=link';
	perRelationForm.submit();
}
</script>
<html:form action="/performance/options/kh_relation/query">
	<br>
	<br>
	<br>
	<table width="90%" border="0" cellspacing="0" align="center"
		cellpadding="0" class="ListTable">
		<thead>
			<tr>
				<td align="center" class="TableRow" nowrap>
					<bean:message key="label.query.selectfield" />
					&nbsp;&nbsp;
				</td>
			</tr>
		</thead>
		<tr>
			<td width="100%" align="center" class="RecordRow" nowrap>
				<table>
					<tr>
						<td align="center" width="46%">
							<table align="center" width="100%">
								<tr>
									<td align="left">
										<bean:message key="selfservice.query.queryfield" />
										&nbsp;&nbsp;
									</td>
								</tr>
								<tr>
									<td align="center">
										<html:select name="perRelationForm" property="left_fields"
											multiple="true" style="height:230px;width:100%;font-size:9pt"
											ondblclick="additem2('left_fields','right_fields');">
											<html:optionsCollection property="leftlist" value="dataValue"
												label="dataName" />
										</html:select>
									</td>
								</tr>
							</table>
						</td>

						<td width="8%" align="center">
							<html:button styleClass="mybutton" property="b_addfield"
								onclick="additem2('left_fields','right_fields');">
								<bean:message key="button.setfield.addfield" />
							</html:button>
							<br>
							<br>
							<html:button styleClass="mybutton" property="b_delfield"
								onclick="removeitem('right_fields');">
								<bean:message key="button.setfield.delfield" />
							</html:button>
						</td>


						<td width="46%" align="center">
							<table width="100%">
								<tr>
									<td width="100%" align="left">
										<bean:message key="selfservice.query.queryfieldselected" />
										&nbsp;&nbsp;
									</td>
								</tr>
								<tr>
									<td width="100%" align="left">
										<html:select name="perRelationForm" property="right_fields"
											size="10" multiple="true"
											style="height:230px;width:100%;font-size:9pt"
											ondblclick="removeitem('right_fields');">
										</html:select>
									</td>
								</tr>
							</table>
						</td>
					</tr>
				</table>
			</td>
		</tr>
		<tr>
			<td align="center" class="RecordRow" nowrap>
				<input type='button' class="mybutton"
					value='<bean:message key="button.query.next"/>' onclick='next()' />
			</td>
		</tr>
	</table>
</html:form>

