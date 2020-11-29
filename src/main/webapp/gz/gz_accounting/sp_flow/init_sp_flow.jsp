<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<script type="text/javascript">
<!--
function changedata(binit,cinit)
{
   gzSpFlowForm.action="/gz/gz_accounting/sp_flow/init_sp_flow.do?b_init=link&init=init&binit="+binit+"&cinit="+cinit;
   gzSpFlowForm.submit();
}
//-->
</script>
<html:form action="/gz/gz_accounting/sp_flow/init_sp_flow">

<logic:equal value="1" name="gzSpFlowForm" property='hasData'>
<p align='center'><strong><bean:message key='lable.gz.nosalaryset'/></strong></p>
</logic:equal>
<logic:equal value="0" name="gzSpFlowForm" property='hasData'>
<table width="80%" border="0" cellspacing="0"  align="center" cellpadding="0" class="ListTable" style="margin-top:10px;">
<tr>
<td align="left" width="100%" style="padding-bottom: 3px;">
<table width="100%" border="0" cellspacing="0"  align="center" cellpadding="0" class="ListTable">
<tr align="center">
<td valign="middle" width="30%" nowrap>
<logic:equal value="0" name="gzSpFlowForm" property='gz_module'>
   <bean:message key="sys.res.gzset"/>&nbsp;
</logic:equal>
<logic:notEqual value="0" name="gzSpFlowForm" property='gz_module'>
   <bean:message key="sys.res.xzset"/>&nbsp;
</logic:notEqual>
<html:select name="gzSpFlowForm" styleId="sl" property="salaryid" size="1" onchange="changedata('first','first');" style="vertical-align:middle;">
   <html:optionsCollection property="salaryList" value="dataValue" label="dataName"/>
</html:select> 
</td>
<td valign="middle" width="18%" nowrap>
<bean:message key="label.gz.appdate"/>
<html:select name="gzSpFlowForm" styleId="bdl" property="busiDate" size="1" onchange="changedata('init','first');" style="vertical-align:middle;">
   <html:optionsCollection property="busiDateList" value="dataValue" label="dataName"/>
</html:select> 
</td>
<td valign="middle" width="18%" nowrap>
审批状态&nbsp;
<html:select name="gzSpFlowForm" styleId="sp" property="spFlag" size="1" onchange="changedata('init','first');" style="vertical-align:middle;">
   <html:optionsCollection property="spFlagList" value="dataValue" label="dataName"/>
</html:select> 

</td>
<td valign="middle" width="15%" nowrap>
薪资员&nbsp;
<html:select name="gzSpFlowForm" styleId="usr" property="usrName" size="1" onchange="changedata('init','first');" style="vertical-align:middle;">
   <html:optionsCollection property="usrNameList" value="dataValue" label="dataName"/>
</html:select> 

</td>
<td valign="middle" width="20%" nowrap>
当前操作员&nbsp;
<html:select name="gzSpFlowForm" styleId="usr" property="curr" size="1" onchange="changedata('init','first');" style="vertical-align:middle;">
   <html:optionsCollection property="currList" value="dataValue" label="dataName"/>
</html:select> 
</td>
</tr>
</table>
</td>
</tr>
<tr>
<td align="center" width="100%">

<table width="100%" border="0" cellspacing="0"  align="center" cellpadding="0" class="ListTable">
<tr>
<td align="center" class="TableRow" nowrap>
<logic:equal value="0" name="gzSpFlowForm" property='gz_module'>
   <bean:message key="sys.res.gzset"/>
</logic:equal>
<logic:notEqual value="0" name="gzSpFlowForm" property='gz_module'>
   <bean:message key="sys.res.xzset"/>
</logic:notEqual>
</td>
<td align="center" class="TableRow" nowrap>
<bean:message key="label.gz.appdate"/>
</td>
<td align="center" class="TableRow" nowrap>
<bean:message key='hmuster.label.counts'/>
</td>
<td align="center" class="TableRow" nowrap>
<bean:message key='label.gz.sp'/>
</td>
<td align="center" class="TableRow" nowrap>
<bean:message key='label.gz.salaryadmin'/>
</td>
<td align="center" class="TableRow" nowrap>
<bean:message key="lable.gz.curr_user"/>
</td>
</tr>
<%int i=0; %>
<hrms:extenditerate id="element" name="gzSpFlowForm" property="spDataListform.list" indexes="indexes"  pagination="spDataListform.pagination" pageCount="15" scope="session">
<%if(i%2==0){%>
<tr class="trShallow" onmouseout="changTRColor(this,'');" onmouseover="changTRColor(this,'#FFF8D2');">
<%}else{ %>
<tr class="trDeep" onmouseout="changTRColor(this,'');" onmouseover="changTRColor(this,'#FFF8D2');" >
<%} %>
<td align='left' class="RecordRow" nowrap>
&nbsp;<bean:write name="element" property="cname"/>&nbsp;
</td>
<td align='right' class="RecordRow" nowrap>
&nbsp;<bean:write name="element" property="busidate"/>&nbsp;
</td>
<td align='right' class="RecordRow" nowrap>
&nbsp;<bean:write name="element" property="count"/>&nbsp;
</td>
<td align='left' class="RecordRow" nowrap>
&nbsp;<bean:write name="element" property="sp_flag"/>&nbsp;
</td>
<td align='left' class="RecordRow" nowrap>
&nbsp;<bean:write name="element" property="admin"/>&nbsp;
</td>
<td align='left' class="RecordRow" nowrap>
&nbsp;<bean:write name="element" property="curr_oper"/>&nbsp;
</td>
<%i++;%>
</tr>
</hrms:extenditerate>
</table>
</td>
<html:hidden property="gz_module" name="gzSpFlowForm"/>
</tr>
<tr>
<td class='RecordRow' style="border-top:0;">
<table  width="100%" align="center">
		<tr>
		   <td valign="bottom" class="tdFontolor" nowrap>第
		   <bean:write name="gzSpFlowForm" property="spDataListform.pagination.current" filter="true"/>
		   页
		   共
		   <bean:write name="gzSpFlowForm" property="spDataListform.pagination.count" filter="true"/>
		   条
		   共
		   <bean:write name="gzSpFlowForm" property="spDataListform.pagination.pages" filter="true"/>
		   页
		   </td>
		   <td align="right" class="tdFontcolor" nowrap>
		   <p align="right">
		   <hrms:paginationlink name="gzSpFlowForm" property="spDataListform.pagination" nameId="spDataListform" propertyId="spDataListProperty">
		   </hrms:paginationlink>
		   </td>
		</tr> 
</table>
</td>
</tr>
</table>
</logic:equal>
</html:form>