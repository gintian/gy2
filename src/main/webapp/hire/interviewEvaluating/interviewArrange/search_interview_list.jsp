<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean"%>
<%@ taglib uri="/tags/struts-html" prefix="html"%>
<%@ taglib uri="/tags/struts-logic" prefix="logic"%>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms"%>
<script type="text/javascript">
<!--
function changeState(obj)
{
var codeid="${interviewArrangeForm.codeID}";
  interviewArrangeForm.action="/hire/interviewEvaluating/interviewRevert.do?b_interview=interview&type=2&code="+codeid;
  interviewArrangeForm.submit();
}
function queryRecord()
{
   var codeid="${interviewArrangeForm.codeID}";
  interviewArrangeForm.action="/hire/interviewEvaluating/interviewRevert.do?b_interview=interview&type=2&code="+codeid;
  interviewArrangeForm.submit();
}
//-->
</script>
<SCRIPT Language="JavaScript">dateFormat='yyyy.mm.dd'</SCRIPT>
<script language="JavaScript" src="/js/popcalendar.js"></script>
<script language="JavaScript" src="/js/function.js"></script>
<script language="JavaScript" src="/js/validate.js"></script>
<script language="JavaScript" src="/js/constant.js"></script>
<html:form action="/hire/interviewEvaluating/interviewRevert">
  <base id="mybase" target="_self">
<table width="90%" border="0" cellspacing="0" align="center" cellpadding="0" class="ListTable">
<br>
<tr>
<td colspan="6" align="left">
<bean:message key="hire.revert.result"/>：<hrms:optioncollection name="interviewArrangeForm" property="interviewingRevertItemCodeList"  collection="list" />
						 <html:select name="interviewArrangeForm" property="interviewingCodeValue" onchange="changeState(this);" size="1" style="width:130px;">
				             <html:options collection="list" property="dataValue" labelProperty="dataName"/>
		  </html:select></td>
</tr>
<tr><td colspan="6" align="left">
		    
		按面试时间从： <input type="text" name="start_date" size="14"   value='<bean:write name="interviewArrangeForm" property="start_date"/>' style="BACKGROUND-COLOR:#F8F8F8;border: 1pt solid #A8C4EC;width:150px"  extra="editor"  id='startdate' class="complex_border_color" dropDown="dropDownDate"/>至： <input type="text" name="end_date" size="14"   value='<bean:write name="interviewArrangeForm" property="end_date" />' style="BACKGROUND-COLOR:#F8F8F8;border: 1pt solid #A8C4EC;width:150px"  extra="editor"  id='startdate' class="common_border_color" dropDown="dropDownDate"/>
		&nbsp;&nbsp;<input type="button" name="query" class="mybutton" onclick="queryRecord();" value="<bean:message key="infor.menu.query"/>"/>		 

</td>
</tr>
</table>
<table width="90%" border="0" cellspacing="0" align="center" cellpadding="0" class="ListTable" style="margin-top:5px;">
<thead>
<tr>
<td class="TableRow" align="center"><bean:message key="hire.employActualize.name"/>
</td>
<td class="TableRow" align="center"><bean:message key="hire.employActualize.resumeState"/>
</td>
<td class="TableRow" align="center"><bean:message key="hire.interviewExamine.interviewUnit"/>
</td>
<td class="TableRow" align="center"><bean:message key="hire.interviewExamine.interviewDepartment"/>
</td>
<td class="TableRow" align="center"><bean:message key="hire.interviewExamine.interviewPosition"/>
</td>
<td class="TableRow" align="center"><bean:message key="hire.revert.result"/>
</td>
</tr>
</thead>
<hrms:paginationdb id="element" name="interviewArrangeForm" sql_str="${interviewArrangeForm.select_sql}" fromdict="1" where_str="${interviewArrangeForm.where_sql}" columns="${interviewArrangeForm.cloumns}" order_by="${interviewArrangeForm.order_sql}" page_id="pagination" pagerows="10" indexes="indexes">
<tr>
<td class="RecordRow" align="center">
<bean:write name="element" property="a0101"/>
</td>
<td class="RecordRow" align="center">
 <hrms:codetoname codeid="36" name="element" codeitem="codeitem" codevalue="state" scope="page"/>
         &nbsp;<bean:write name="codeitem" property="codename"/>&nbsp;
</td>
<td class="RecordRow" align="center">
<bean:write name="element" property="unit"/>
</td>
<td class="RecordRow" align="center">
<bean:write name="element" property="departid"/>
</td>
<td class="RecordRow" align="center">
<bean:write name="element" property="codeitemdesc"/>
</td>
<td class="RecordRow" align="center">
<hrms:codetoname codeid="${interviewArrangeForm.codesetid}" name="element" codeitem="codeitem" codevalue="interview" scope="page"/>
         &nbsp;<bean:write name="codeitem" property="codename"/>&nbsp;
</td>
</tr>
</hrms:paginationdb>
<tr>
<td colspan="6" align="center">
<table  width="100%"  class='RecordRowP'  align='center' >
		<tr>
			  <td valign="bottom" class="tdFontcolor">
				<bean:message key="hmuster.label.d"/>
				<bean:write name="pagination" property="current" filter="true" />
				<bean:message key="hmuster.label.paper"/>
				<bean:message key="hmuster.label.total"/>
			<bean:write name="pagination" property="count" filter="true" />
				<bean:message key="label.every.row"/>
				<bean:message key="hmuster.label.total"/>
				<bean:write name="pagination" property="pages" filter="true" />
				<bean:message key="hmuster.label.paper"/>
			</td>
			<td align="right" nowrap class="tdFontcolor">
				<p align="right">
					<hrms:paginationdblink name="interviewArrangeForm" property="pagination" nameId="interviewArrangeForm" scope="page">
				</hrms:paginationdblink>
			</td>
		</tr>
	</table>	
</td>
</tr>
<tr>
<td colspan="6" align="center" height="35px;">
<input type="button" value="<bean:message key="button.close"/>" class="mybutton" onclick="window.close();"/>
</td>
</tr>
</table>
</html:form>