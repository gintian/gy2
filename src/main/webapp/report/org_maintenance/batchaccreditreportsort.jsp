<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean"%>
<%@ taglib uri="/tags/struts-html" prefix="html"%>
<%@ taglib uri="/tags/struts-logic" prefix="logic"%>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms"%>
	<script language="JavaScript">
		function add(){
			document.all.ly.style.display="block";   
	 		document.all.ly.style.height=document.body.scrollHeight>document.body.offsetHeight?document.body.scrollHeight:document.body.offsetHeight;
	 		document.getElementById('wait').style.display='block';
			var uc = "${searchReportUnitForm.rtUnitCodes}";
			 searchReportUnitForm.target="il_body";
			searchReportUnitForm.action="/report/org_maintenance/reportunitlist.do?b_batchsave=link&uc="+uc;
			searchReportUnitForm.submit();
		}
		
	</script>
	<link href="../../css/css1.css" rel="stylesheet" type="text/css">
	<hrms:themes/>
	<table width="100%" align="center" border="0" cellpadding="0" cellspacing="0">
	<tr>
		<td valign="top">

			<html:form  action="/report/org_maintenance/reportunitlist2">
			<div id="ly" style="position:absolute;left:0px;top:0px;FILTER:alpha(opacity=30);opacity:0.3;background-color:#c5c5c5;z-index:2;display:none;width:100%;
	    "></div>
		<div id='wait' style='position:absolute;top:180;left:400;display:none;z-index:1000;'>
			<table border="1" width="300" cellspacing="0" cellpadding="4" class="table_style" height="87" align="center">
				<tr>
					<td class="td_style" height=24>
						正在导入数据，请稍候...
					</td>
				</tr>
				<tr>
					<td style="font-size:12px;line-height:200%" align=center>
						<marquee class="marquee_style" direction="right" width="300" scrollamount="5" scrolldelay="10">
							<table cellspacing="1" cellpadding="0">
								<tr height=8>
									<td bgcolor=#3399FF width=8></td>
									<td></td>
									<td bgcolor=#3399FF width=8></td>
									<td></td>
									<td bgcolor=#3399FF width=8></td>
									<td></td>
									<td bgcolor=#3399FF width=8></td>
									<td></td>
								</tr>
							</table>
						</marquee>
					</td>
				</tr>
			</table>
		</div>
				<br>
				<table width="90%" border="0" cellspacing="0" align="center" cellpadding="0" class="ListTable">
					<thead>
						<tr>
							<td align="center" class="TableRow" nowrap>
								<input type="checkbox" name="selbox" onclick="batch_select(this,'reportTypeList.select');" title='<bean:message key="label.query.selectall"/>'>
							</td>
							<td align="center" class="TableRow" nowrap>
								<bean:message key="reporttypelist.tsortname" />
								&nbsp;
							</td>
							<td align="center" class="TableRow" nowrap>
								<bean:message key="reporttypelist.sortid" />
								&nbsp;
							</td>
							<td align="center" class="TableRow" nowrap>
								<bean:message key="reporttypelist.sort" />
							</td>

						</tr>
					</thead>

					<hrms:extenditerate id="element" name="searchReportUnitForm" property="reportTypeList.list" indexes="indexes" pagination="reportTypeList.pagination" pageCount="15" scope="session">
						<tr class="trShallow">
							<td align="center" class="RecordRow" nowrap>
								<hrms:checkmultibox name="searchReportUnitForm" property="reportTypeList.select" value="true" indexes="indexes" />
							</td>
							<td align="left" class="RecordRow" nowrap>
								<bean:write name="element" property="string(name)" filter="false" />
								&nbsp;
							</td>
							<td align="left" class="RecordRow" nowrap>
								<bean:write name="element" property="string(tsortid)" filter="false" />
								&nbsp;
							</td>
							<td align="center" class="RecordRow" nowrap>
								<bean:write name="element" property="string(sdes)" filter="false" />
								&nbsp;
							</td>
						</tr>
					</hrms:extenditerate>

				</table>

				<table width="90%"  class="RecordRowP"  align="center">
					<tr>
						<td valign="bottom" class="tdFontcolor">
							<bean:message key="label.page.serial" />
							<bean:write name="searchReportUnitForm" property="reportTypeList.pagination.current" filter="true" />
							<bean:message key="label.page.sum" />
							<bean:write name="searchReportUnitForm" property="reportTypeList.pagination.count" filter="true" />
							<bean:message key="label.page.row" />
							<bean:write name="searchReportUnitForm" property="reportTypeList.pagination.pages" filter="true" />
							<bean:message key="label.page.page" />
						</td>
						<td align="right" nowrap class="tdFontcolor">
							<p align="right">
								<hrms:paginationlink name="searchReportUnitForm" property="reportTypeList.pagination" nameId="reportTypeList">
								</hrms:paginationlink>
						</td>
					</tr>
				</table>

				<table width="70%" align="center">
					<tr>
						<td align="center" nowrap colspan="4">
							<input type="hidden" name="unitCodes" value="${searchReportUnitForm.rtUnitCodes}"/>
							<input type="button" name="save" value="<bean:message key='reporttypelist.confirm'/>" class="mybutton" onClick="add()">
							<input type="button"  value="<bean:message key='reporttypelist.cancel'/>" onClick="javaScript:history.back();" class="mybutton">
						</td>
					</tr>
				</table>
			</html:form>

		</td>
	</tr>
</table>
