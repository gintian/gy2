
<%@ page language="java" contentType="text/html;charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean"%>
<%@ taglib uri="/tags/struts-html" prefix="html"%>
<%@ taglib uri="/tags/struts-logic" prefix="logic"%>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms"%>
<html>
	<script language="JavaScript" src="/js/validateDate.js"></script>
	<link href="/css/css1.css" rel="stylesheet" type="text/css">
	<script language="javascript">
	
	function onsave2(){
	var name = trim(document.getElementsByName("name")[0].value);
	if(name==""){
	alert("请输入名称");
	return;
	}
			alert("保存成功");
	 	reportCycleForm.action = "/report/actuarial_report/fill_cycle.do?b_editsave2=editsave";
	 	reportCycleForm.submit();
	 
 }

   </script>
   <hrms:themes/>
	<body>
		<html:form action="/report/actuarial_report/fill_cycle">
		<html:hidden styleId="reportcycle_id" name="reportCycleForm"
				property="reportcyclevo.string(id)" />
		<html:hidden styleId="reportcycle_status" name="reportCycleForm"
				property="reportcyclevo.string(status)" />
			<br>
			<br>
			<br>
			<table width="60%" border="0" cellspacing="0" align="center"
				cellpadding="0" class="ListTable">
				<thead>
					<tr>
						<td align="left" class="TableRow" colspan="2">
							<bean:message key="reportcyclelist.cycle" />
							&nbsp;
						</td>
					</tr>
				</thead>

							<tr class="trShallow">
								<td align="right" height="30" width="30%" class="RecordRow" nowrap>
									<bean:message key="reportcyclelist.name" />
									&nbsp;
								</td>
								<td class="RecordRow" nowrap>
									<html:text name="reportCycleForm" size="14" style="width:200px" styleId="name" property="reportcyclevo.string(name)" maxlength="100"   styleClass="textColorWrite" />
								</td>
							</tr>
							
			</table>

			<table width="60%" align="center">
				<tr>
					<td align="center">

						<input type="button" name="b_edit2"
							value="<bean:message key="lable.menu.main.save"/>"
							class="mybutton" onClick="onsave2()">
				<hrms:submit styleClass="mybutton" property="br_return">
						<bean:message key="button.return" />
					</hrms:submit>

					</td>
				</tr>
			</table>


		</html:form>
	</body>
</html>
