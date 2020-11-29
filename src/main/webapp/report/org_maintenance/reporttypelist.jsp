<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean"%>
<%@ taglib uri="/tags/struts-html" prefix="html"%>
<%@ taglib uri="/tags/struts-logic" prefix="logic"%>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms"%>
<%@ page import="com.hrms.struts.valueobject.UserView"%>
<%@ page import="com.hrms.struts.constant.WebConstant"%>

<%
	int i = 0;
	String userName = null;
	String css_url = "/css/css1.css";
	UserView userView = (UserView) session
			.getAttribute(WebConstant.userView);
	if (userView != null) {
		css_url = userView.getCssurl();
		if (css_url == null || css_url.equals(""))
			css_url = "/css/css1.css";
		//out.println("<link href='"+css_url+"' rel='stylesheet' type='text/css' >");  	 
	}

		
%>
	<SCRIPT LANGUAGE=javascript src="../../../js/validate.js"></SCRIPT>
	<script language="JavaScript"src="../../../module/utils/js/template.js"></script>
	<script language="JavaScript">
		function pf_ChangeFocus() 
		{
		   key = window.event.keyCode;
		   if ( key==0xD && event.srcElement.tagName!='TEXTAREA') /*0xD*/
		   {
		   	window.event.keyCode=9;
		   }
		   //按F5刷新问题,重复提交问题,右键菜单也设法去掉
		   if ( key==116)
		   {
		   	window.event.keyCode=0;	
			window.event.returnValue=false;
		   }   
		   if ((window.event.ctrlKey)&&(key==82))//屏蔽 Ctrl+R  
		   {    
		        window.event.keyCode=0;	
			window.event.returnValue=false;
		   } 
		}
		
		
		function add(){
			var uc = "${searchReportUnitForm.rtUnitCode}";
			searchReportUnitForm.action="/report/org_maintenance/reporttypelist.do?b_addtype=add&rtunitcode="+uc;
			searchReportUnitForm.submit();
		}
		
		function setReports(tsortid){
			var config = {
				width:490,
				height:500,
				title:'',
				theurl:"/report/org_maintenance/reportunitlist.do?b_reportList=query&tsortid="+tsortid+"&unitcode=${searchReportUnitForm.rtUnitCode}",
				id:'setReportWin'
			}
			openWin(config);
	   }
		function openWin(config){
		    Ext.create("Ext.window.Window",{
		    	id:config.id,
		    	width:config.width,
		    	height:config.height,
		    	title:config.title,
		    	resizable:false,
		    	autoScroll:false,
		    	modal:true,
		    	renderTo:Ext.getBody(),
		    	html:"<iframe style='background-color:#ffffff' frameborder='0' SCROLLING=NO height='100%' width='100%' src='"+config.theurl+"'></iframe>"
	 	    }).show();	
		}
		
		//屏蔽右键,实在没有办法的采用此办法,解决重复提交问题
		/*
		function document.oncontextmenu() 
		{ 
		  	return false; 
		} 
		*/
		
		
		function returnPage()
		{
			searchReportUnitForm.action="/report/org_maintenance/reportunitlist.do?b_query=link";
			searchReportUnitForm.submit();
		}
	</script>
	<link href="../../css/css1.css" rel="stylesheet" type="text/css">
	<hrms:themes/>
	<table width="100%" align="center" border="0" cellpadding="0" cellspacing="0">
	<tr>
		<td valign="top">

			<form name="searchReportUnitForm" method="post" action="/report/org_maintenance/reporttypelist.do">
				<br>
				<table width="90%" border="0" cellspacing="0" align="center" cellpadding="0" class="ListTable">
					<thead>
						<tr>
							<td align="center" class="TableRow" nowrap>
								 <input type="checkbox" name="selbox" onclick="batch_select(this,'reportTypeList.select');" title='<bean:message key="label.query.selectall"/>'>
								
							</td>
							<td align="center" class="TableRow" nowrap>
								&nbsp;<bean:message key="reporttypelist.sortid" />
								&nbsp;
							</td>
							<td align="center" class="TableRow" nowrap>
								<bean:message key="reporttypelist.tsortname" />
								&nbsp;
							</td>
							<td align="center" class="TableRow" nowrap>
								&nbsp;<bean:message key="edit_report.status.bj" />&nbsp;
							</td>
							<td align="center" class="TableRow" nowrap>
								<bean:message key="reporttypelist.sort" />
							</td>
							
						</tr>
					</thead>

					<hrms:extenditerate id="element" name="searchReportUnitForm" property="reportTypeList.list" indexes="indexes" pagination="reportTypeList.pagination" pageCount="25" scope="session">
						<%
			          if(i%2==0)
			          {
			          %>
			          <tr class="trShallow">
			          <%}
			          else
			          {%>
			          <tr class="trDeep">
			          <%
			          }
			          i++;          
          %>  
							<td align="center" class="RecordRow" nowrap>
								<logic:equal name="element" property="string(sid)" value="1">
									<hrms:checkmultibox name="searchReportUnitForm" property="reportTypeList.select" value="false" indexes="indexes" />
				</logic:equal>
								<logic:equal name="element" property="string(sid)" value="0">
									<hrms:checkmultibox name="searchReportUnitForm" property="reportTypeList.select" value="true" indexes="indexes" />
				</logic:equal>
							</td>
							<td align="left" class="RecordRow" nowrap>
								&nbsp;<bean:write name="element" property="string(tsortid)" filter="false" />
								&nbsp;
							</td>
							<td align="left" class="RecordRow" nowrap>
							&nbsp;<bean:write name="element" property="string(name)" filter="false" />
								&nbsp;
							</td>
							<td align="center" class="RecordRow" nowrap>
							<logic:equal name="element" property="string(sid)" value="1">
								<a href='javascript:setReports("<bean:write name="element" property="string(tsortid)" filter="false" />")'>	
									<img src="../../images/edit.gif" width="11" height="17" border=0>	
								</a>			
							</logic:equal>
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
							<input type="button" value="<bean:message key='reporttypelist.confirm'/>" class="mybutton" onClick="add()">
							<input type="button"  value="<bean:message key='reporttypelist.cancel'/>" onClick="returnPage()" class="mybutton">
						</td>
					</tr>
				</table>
			</form>

		</td>
	</tr>
</table>
