<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean"%>
<%@ taglib uri="/tags/struts-html" prefix="html"%>
<%@ taglib uri="/tags/struts-logic" prefix="logic"%>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms"%>
<%@ page import="com.hrms.struts.valueobject.UserView"%>
<%@ page import="com.hrms.struts.constant.WebConstant"%>
<%
	UserView userView=(UserView)session.getAttribute(WebConstant.userView);
	String bosflag="";
	if(userView != null){	  
	  	bosflag=userView.getBosflag();
	  	bosflag=bosflag!=null?bosflag:"";                
	}
%>
<script type="text/javascript">
	function search()
	{
		trainReportForm.action="/train/report/trainRepList.do?b_query=link";
		trainReportForm.submit();
	}
	//导出Excel
	function exportExcel()
	{
		var hashvo=new ParameterSet();
		hashvo.setValue("reportId",$F('reportId'));
		var request=new Request({method:'post',asynchronous:false,onSuccess:showfile,functionId:'2020030027'},hashvo);
	}
	function showfile(outparamters)
	{
		var outName=outparamters.getValue("outName");
		var name=outName.substring(0,outName.length);
		var win=open("/servlet/vfsservlet?fromjavafolder=true&fileid="+name,"excel");
	
	}
	function returnFirst(){
   		self.parent.location= "/general/tipwizard/tipwizard.do?br_train=link";
	}
</script>
<style>
.myfixedDiv2{
	BORDER-BOTTOM:#94B6E6 0pt solid; 
	BORDER-LEFT: #94B6E6 0pt solid; 
	BORDER-RIGHT: #94B6E6 0pt solid; 
	BORDER-TOP: #94B6E6 0pt solid; 
	width:100%;
	overflow: auto; 
    height:expression(document.body.clientHeight-130);
}
</style>
<html:form action="/train/report/trainRepList"> 
	<html:hidden name="trainReportForm" property="reportId" styleId="reportId"/>
		<table width="100%" border="0" cellspacing="0" align="center" cellpadding="0" class="ListTable">
			<tr style="padding-bottom: 5px;">
				<td align="left">
					&nbsp;
					<bean:message key="px.report.year" />
					<html:select name="trainReportForm" property="year" size="1"  style="width:70px" onchange="search();">
						<html:option value="">
							<bean:message key="label.all" />
						</html:option>
						<html:optionsCollection property="yearList" value="dataValue" label="dataName" />
					</html:select>
					&nbsp;
					<bean:message key="kq.wizard.quarter" />
					<html:select name="trainReportForm" property="quarter" size="1" style="width:70px"
						onchange="search();">
						<html:option value="">
							<bean:message key="label.all" />
						</html:option>
						<html:option value="01">
							<bean:message key="report.pigionhole.oneQuarter" />
						</html:option>
						<html:option value="02">
							<bean:message key="report.pigionhole.twoQuarter" />
						</html:option>
						<html:option value="03">
							<bean:message key="report.pigionhole.threeQuarter" />
						</html:option>
						<html:option value="04">
							<bean:message key="report.pigionhole.fourQuarter" />
						</html:option>
					</html:select>
				</td>
			</tr>
	<tr>
		<td align="left"  class="RecordRow" style="padding: 0;">
	<div class="myfixedDiv2">
		<table width="100%" border="0" cellspacing="0" align="center" cellpadding="0">	
			<thead>
				<tr  class="fixedHeaderTr">
					<logic:iterate id="element" name="trainReportForm" property="titles">
						<td align="center" class="TableRow"  style="border-left:none;border-top: none;"  nowrap>
							<bean:write name="element" property="title" filter="true" />
						</td>
					</logic:iterate>
				</tr>	
			</thead>
			<%int i = 0;%>
			<hrms:extenditerate id="element1" name="trainReportForm"
						property="setlistform.list" indexes="indexes"
						pagination="setlistform.pagination" pageCount="20" scope="session">
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
				<logic:equal name="trainReportForm" property="reportId" value="1">		
					<td align="left" class="RecordRow"  style="border-left:none;border-top: none;"  nowrap>
						&nbsp;<bean:write name="element1" property="b0110" filter="false" />&nbsp;
					</td>
					<td align="left" class="RecordRow" style="border-left:none;border-top: none;"  nowrap>
						&nbsp;<bean:write name="element1" property="e0122" filter="false" />&nbsp;
					</td>
					<td align="right" class="RecordRow" style="border-left:none;border-top: none;"  nowrap>
						&nbsp;<bean:write name="element1" property="usercount" filter="false" />&nbsp;
					</td>
					<td align="right" class="RecordRow" style="border-left:none;border-top: none;"  nowrap>
						&nbsp;<bean:write name="element1" property="r2506" filter="false" />&nbsp;
					</td>					
					<td align="right" class="RecordRow" style="border-left:none;border-top: none;"  nowrap>
						&nbsp;<bean:write name="element1" property="r3111" filter="false" />&nbsp;
					</td>
					
					<td align="right" class="RecordRow" style="border-left:none;border-top: none;"  nowrap>
						&nbsp;<bean:write name="element1" property="itemcount" filter="false" />&nbsp;
					</td>
					<td align="right" class="RecordRow" style="border-left:none;border-top: none;border-right: none;"  nowrap>
						&nbsp;<bean:write name="element1" property="r3110" filter="false" />&nbsp;
					</td>
				</logic:equal>
				<logic:equal name="trainReportForm" property="reportId" value="2">
					<td align="left" class="RecordRow" style="border-left:none;border-top: none;"  nowrap>
						&nbsp;<bean:write name="element1" property="b0110" filter="false" />&nbsp;
					</td>
					<td align="left" class="RecordRow" style="border-left:none;border-top: none;"  nowrap>
						&nbsp;<bean:write name="element1" property="e0122" filter="false" />&nbsp;
					</td>
					<td align="left" class="RecordRow" style="border-left:none;border-top: none;"  nowrap>
						&nbsp;<bean:write name="element1" property="r4105" filter="false" />&nbsp;
					</td>
					<td align="right" class="RecordRow" style="border-left:none;border-top: none;"  nowrap>
						&nbsp;<bean:write name="element1" property="sumrs" filter="false" />&nbsp;
					</td>			
					<td align="right" class="RecordRow" style="border-left:none;border-top: none;"  nowrap>
						&nbsp;<bean:write name="element1" property="sumks" filter="false" />&nbsp;
					</td>
					<td align="right" class="RecordRow" style="border-left:none;border-top: none;border-right: none;"  nowrap>
						&nbsp;<bean:write name="element1" property="sumfy" filter="false" />&nbsp;
					</td>
				</logic:equal>
				<logic:equal name="trainReportForm" property="reportId" value="3">
					<td align="left" class="RecordRow" style="border-left:none;border-top: none;"  nowrap>
						&nbsp;<bean:write name="element1" property="b0110" filter="false" />&nbsp;
					</td>
					<td align="left" class="RecordRow" style="border-left:none;border-top: none;"  nowrap>
						&nbsp;<bean:write name="element1" property="e0122" filter="false" />&nbsp;
					</td>			
					<td align="left" class="RecordRow" style="border-left:none;border-top: none;"  nowrap>
						&nbsp;<bean:write name="element1" property="r4002" filter="false" />&nbsp;
					</td>
					<td align="left" class="RecordRow" style="border-left:none;border-top: none;"  nowrap>
						&nbsp;<bean:write name="element1" property="r4105" filter="false" />&nbsp;
					</td>					
					<td align="right" class="RecordRow" style="border-left:none;border-top: none;"  nowrap>
						&nbsp;<bean:write name="element1" property="itemcount" filter="false" />&nbsp;
					</td>					
					<td align="right" class="RecordRow" style="border-left:none;border-top: none;"  nowrap>
						&nbsp;<bean:write name="element1" property="sumfy" filter="false" />&nbsp;
					</td>					
					<td align="right" class="RecordRow" style="border-left:none;border-top: none;border-right: none;"  nowrap>
						&nbsp;<bean:write name="element1" property="sumks" filter="false" />&nbsp;
					</td>
				</logic:equal>
				</tr>
			</hrms:extenditerate>
		</table>
			</td>
			</tr>
			</table>
	</div>
		<table  width="100%" align="center" class="RecordRowP">
		<tr>
			<td valign="bottom" align="left" class="tdFontcolor">
				第
				<bean:write name="trainReportForm"
					property="setlistform.pagination.current" filter="true" />
				页 共
				<bean:write name="trainReportForm"
					property="setlistform.pagination.count" filter="true" />
				条 共
				<bean:write name="trainReportForm"
					property="setlistform.pagination.pages" filter="true" />
				页
			</td>
			<td align="right" nowrap class="tdFontcolor">
				<p align="right">
					<hrms:paginationlink name="trainReportForm"
						property="setlistform.pagination" nameId="setlistform"
						propertyId="roleListProperty">
					</hrms:paginationlink>
			</td>
		</tr>
	</table>
	<table width="100%">
		<tr>			
			<td align="left">
				<input type='button' class="mybutton" property="b_query" onclick='exportExcel()'
					value='&nbsp;<bean:message key="goabroad.collect.educe.excel"/>&nbsp;' />
				<logic:equal value="dxt" name="trainReportForm" property="returnvalue">
				 <%if(bosflag.equals("hl")||bosflag.equals("hcm")){%>
				 <input type='button' class="mybutton" name="returnButton"
					onclick='returnFirst();'
					value='<bean:message key='reportcheck.return'/>' />
				 <%} %>
				 </logic:equal>
			</td>
		</tr>
	</table>
</html:form>