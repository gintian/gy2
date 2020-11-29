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
	<script language="JavaScript" src="../../js/validate.js"></script>
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
		var value="";
		var value2="";
		for(var i=0;i<document.editReportAnalyseForm.elements.length;i++)
		{
			if(document.editReportAnalyseForm.elements[i].type=='checkbox'&&document.editReportAnalyseForm.elements[i].name !="selbox")
			{
				if(document.editReportAnalyseForm.elements[i].checked==false)
				{
					value+=document.editReportAnalyseForm.elements[i].value+",";
				}
				else
					value2+=document.editReportAnalyseForm.elements[i].value+",";
			}
		}
		if(value2==""){
		alert(SELECTSTATICSTATEMENT+"！");
		return;
		}
		window.returnValue=value2;
   		window.close();
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
			window.close();
		}
	</script>
<style>
	#tbl-container {
			 
		BORDER-BOTTOM: #94B6E6 1pt solid; 
		BORDER-LEFT: #94B6E6 1pt solid; 
		BORDER-RIGHT: #94B6E6 1pt solid; 
		BORDER-TOP: #94B6E6 1pt solid;
		margin-left:5px;
		overflow:auto;
		height:250px;
		width:95% 	
		
	}	
</style>
	<link href="../../css/css1.css" rel="stylesheet" type="text/css">

	<table width="90%" align="center" border="0" cellpadding="0" cellspacing="0">
	<tr>
		<td valign="top">

			<form name="editReportAnalyseForm" method="post" action="/report/org_maintenance/reporttypelist.do">
				<br>
				<div id="tbl-container">
				<table width="100%" border="0" cellspacing="0" align="center"
			cellpadding="0" class="ListTable">
					<thead>
						<tr>
							<td align="center" class="TableRow" nowrap>
								 <input type="checkbox" name="selbox" onclick="batch_select(this,'id');" title='<bean:message key="label.query.selectall"/>'>
								</td>
							
							<td align="center" class="TableRow" nowrap>
								<bean:message key="report.static.name"/>
								&nbsp;
							</td>
							
						</tr>
					</thead>

					<logic:iterate id="element" name="editReportAnalyseForm"
						property="scopelist">
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
							<td align="center" class="RecordRow" width="15%" nowrap>
										<input type='checkbox' name='id'  value='<bean:write name="element" property="dataValue" filter="false" />'  />				
							</td>
							<td align="center" class="RecordRow" nowrap>
							<bean:write name="element" property="dataName" filter="false" />
								&nbsp;
							</td>
							
						</tr>
					</logic:iterate>

				</table>
		</div>
				<table width="70%" align="center">
					<tr>
						<td align="center" nowrap colspan="4">
							<input type="button" value="<bean:message key='reporttypelist.confirm'/>" class="mybutton" onClick="add()">
							<input type="button"  value="<bean:message key='button.cancel'/>" onClick="returnPage()" class="mybutton">
						</td>
					</tr>
				</table>
			</form>

		</td>
	</tr>
</table>
