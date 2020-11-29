<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean"%>
<%@ taglib uri="/tags/struts-html" prefix="html"%>
<%@ taglib uri="/tags/struts-logic" prefix="logic"%>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms"%>
<hrms:themes></hrms:themes>
<SCRIPT LANGUAGE=javascript src="/js/validate.js"></SCRIPT>
<script type="text/javascript" language="javascript">
<!--
function getitemtype(){
	
}
//-->
</script>
<html:form action="/system/busimaintence/editbusitable">
	<table width="50%" border="0" cellspacing="0" align="center"
		cellpadding="0">
		<tr height="20">
			<!--  <td width=10 valign="top" class="tableft"></td>
			<td width=130 align=center class="tabcenter">
				<bean:message key='kjg.title.xgzbj' />
			</td>
			<td width=10 valign="top" class="tabright"></td>
			<td valign="top" class="tabremain" width="500"></td>-->
			<td align=left class="TableRow">
				<bean:message key='kjg.title.xgzbj' />
			</td>
		</tr>
		<tr>
			<td  class="framestyle9">
				<table border="0" cellpmoding="0" cellspacing="0"
					class="DetailTable" cellpadding="0" width="100%">
					<tr class="list3" style="height:30px;">
						<td align="right" nowrap width="25%" style="padding-right:5px;">
							<bean:message key='kjg.title.zbjdh' />
						</td>
						<td align="left" nowrap>
							<html:text styleId="itemid" name="busiMaintenceForm"
								property="busiTableVo.string(fieldsetid)" disabled="true" styleClass="text4" style="width:300px;"></html:text>
						</td>
					</tr>
					<tr class="list3" style="height:30px;">
						<td align="right" nowrap style="padding-right:5px;">
							<bean:message key='kjg.title.frontname'/>
						</td>
						<td align="left"nowrap>
							<html:text name="busiMaintenceForm" property="busiTableVo.string(fieldsetdesc)" styleClass="text4" style="width:300px;"></html:text>
						</td>
					</tr>
					<tr class="list3" style="height:30px;">
						<td align="right" nowrap style="padding-right:5px;">
							<bean:message key='kjg.title.backname'/>
						</td>
						<td align="left" nowrap>
							<html:text name="busiMaintenceForm" property="busiTableVo.string(customdesc)" styleClass="text4" style="width:300px;"></html:text>
						</td>
					</tr>
					<tr class="list3" style="height:30px;">
						<td align="center" colspan='2' nowrap>
							<logic:equal name="busiMaintenceForm" property="busiTableVo.string(useflag)" value="1">
								<html:radio name="busiMaintenceForm" property="busiTableVo.string(changeflag)" value="0" disabled="true"><bean:message key='kjg.title.ybzj'/></html:radio>
								<html:radio name="busiMaintenceForm" property="busiTableVo.string(changeflag)" value="1" disabled="true"><bean:message key='kjg.title.aybh'/></html:radio>
								<html:radio name="busiMaintenceForm" property="busiTableVo.string(changeflag)" value="2" disabled="true"><bean:message key='kjg.title.anbh'/></html:radio>
							</logic:equal>
							<logic:equal name="busiMaintenceForm" property="busiTableVo.string(useflag)" value="0">
								<logic:equal name="busiMaintenceForm" property="userType" value="0">
									<logic:equal name="busiMaintenceForm" property="busiTableVo.string(ownflag)" value="1">
										<html:radio name="busiMaintenceForm" property="busiTableVo.string(changeflag)" value="0" disabled="true"><bean:message key='kjg.title.ybzj'/></html:radio>
										<html:radio name="busiMaintenceForm" property="busiTableVo.string(changeflag)" value="1" disabled="true"><bean:message key='kjg.title.aybh'/></html:radio>
										<html:radio name="busiMaintenceForm" property="busiTableVo.string(changeflag)" value="2" disabled="true"><bean:message key='kjg.title.anbh'/></html:radio>
									</logic:equal>
								</logic:equal>
							</logic:equal>
							<logic:equal name="busiMaintenceForm" property="busiTableVo.string(useflag)" value="0">
								<logic:equal name="busiMaintenceForm" property="userType" value="0">
									<logic:equal name="busiMaintenceForm" property="busiTableVo.string(ownflag)" value="0">
										<logic:equal name="busiMaintenceForm" property="busiTableVo.string(id)" value="30">
											<html:radio name="busiMaintenceForm" property="busiTableVo.string(changeflag)" value="0" disabled="true"><bean:message key='kjg.title.ybzj'/></html:radio>
											<html:radio name="busiMaintenceForm" property="busiTableVo.string(changeflag)" value="1" disabled="true"><bean:message key='kjg.title.aybh'/></html:radio>
											<html:radio name="busiMaintenceForm" property="busiTableVo.string(changeflag)" value="2" disabled="true"><bean:message key='kjg.title.anbh'/></html:radio>
										</logic:equal>
										<logic:notEqual name="busiMaintenceForm" property="busiTableVo.string(id)" value="30">
											<logic:equal name="busiMaintenceForm" property="busiTableVo.string(id)" value="35">
												<html:radio name="busiMaintenceForm" property="busiTableVo.string(changeflag)" value="0" disabled="true"><bean:message key='kjg.title.ybzj'/></html:radio>
												<html:radio name="busiMaintenceForm" property="busiTableVo.string(changeflag)" value="1" disabled="fals"><bean:message key='kjg.title.aybh'/></html:radio>
												<html:radio name="busiMaintenceForm" property="busiTableVo.string(changeflag)" value="2" disabled="fals"><bean:message key='kjg.title.anbh'/></html:radio>
											</logic:equal>
											<logic:notEqual name="busiMaintenceForm" property="busiTableVo.string(id)" value="35">
												<html:radio name="busiMaintenceForm" property="busiTableVo.string(changeflag)" value="0" disabled="true"><bean:message key='kjg.title.ybzj'/></html:radio>
												<html:radio name="busiMaintenceForm" property="busiTableVo.string(changeflag)" value="1" disabled="fals"><bean:message key='kjg.title.aybh'/></html:radio>
												<html:radio name="busiMaintenceForm" property="busiTableVo.string(changeflag)" value="2" disabled="fals"><bean:message key='kjg.title.anbh'/></html:radio>
											</logic:notEqual>
										</logic:notEqual>
									</logic:equal>
								</logic:equal>
							</logic:equal>
							<logic:equal name="busiMaintenceForm" property="busiTableVo.string(useflag)" value="0">
								<logic:equal name="busiMaintenceForm" property="userType" value="1">
									<logic:equal name="busiMaintenceForm" property="busiTableVo.string(id)" value="30">
										<html:radio name="busiMaintenceForm" property="busiTableVo.string(changeflag)" value="0" disabled="true"><bean:message key='kjg.title.ybzj'/></html:radio>
										<html:radio name="busiMaintenceForm" property="busiTableVo.string(changeflag)" value="1" disabled="true"><bean:message key='kjg.title.aybh'/></html:radio>
										<html:radio name="busiMaintenceForm" property="busiTableVo.string(changeflag)" value="2" disabled="true"><bean:message key='kjg.title.anbh'/></html:radio>
									</logic:equal>
									<logic:notEqual name="busiMaintenceForm" property="busiTableVo.string(id)" value="30">
										<logic:equal name="busiMaintenceForm" property="busiTableVo.string(id)" value="35">
											<html:radio name="busiMaintenceForm" property="busiTableVo.string(changeflag)" value="0" disabled="true"><bean:message key='kjg.title.ybzj'/></html:radio>
											<html:radio name="busiMaintenceForm" property="busiTableVo.string(changeflag)" value="1" disabled="fals"><bean:message key='kjg.title.aybh'/></html:radio>
											<html:radio name="busiMaintenceForm" property="busiTableVo.string(changeflag)" value="2" disabled="fals"><bean:message key='kjg.title.anbh'/></html:radio>
										</logic:equal>
										<logic:notEqual name="busiMaintenceForm" property="busiTableVo.string(id)" value="35">
											<html:radio name="busiMaintenceForm" property="busiTableVo.string(changeflag)" value="0" disabled="true"><bean:message key='kjg.title.ybzj'/></html:radio>
											<html:radio name="busiMaintenceForm" property="busiTableVo.string(changeflag)" value="1" disabled="fals"><bean:message key='kjg.title.aybh'/></html:radio>
											<html:radio name="busiMaintenceForm" property="busiTableVo.string(changeflag)" value="2" disabled="fals"><bean:message key='kjg.title.anbh'/></html:radio>
										</logic:notEqual>
									</logic:notEqual>
								</logic:equal>
							</logic:equal>
						</td>
					</tr>
	</table>
	</td>
	</tr>
	</table>
	<table width="50%" align="center">
		<tr>
			<td align="center" style="height:35px;">
					<hrms:submit styleClass="mybutton" property="b_update">
						<bean:message key="button.ok" />
					</hrms:submit>
					<logic:equal name="busiMaintenceForm" property="returnvalue1" value="ssbs">
					<hrms:submit styleClass="mybutton" property="br_return1">
						<bean:message key="button.return" />
					</hrms:submit>
					</logic:equal>
					<logic:notEqual name="busiMaintenceForm" property="returnvalue1" value="ssbs">
					<hrms:submit styleClass="mybutton" property="br_returnssb">
						<bean:message key="button.return" />
					</hrms:submit>
					</logic:notEqual>
			</td>
		</tr>
	</table>

</html:form>


