<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ page import="com.hrms.struts.valueobject.UserView"%>
<%@ page import="com.hrms.struts.constant.WebConstant"%>
<%
	int i=0;
	String userName = null;
	String css_url="/css/css1.css";
	UserView userView=(UserView)session.getAttribute(WebConstant.userView);
	if(userView != null){
		css_url=userView.getCssurl();
		if(css_url==null||css_url.equals(""))
		css_url="/css/css1.css";
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

//屏蔽右键,实在没有办法的采用此办法,解决重复提交问题
/*
function document.oncontextmenu() 
{ 
  	return false; 
} 
*/
</script>
   <link href="../../css/css1.css" rel="stylesheet" type="text/css">
<hrms:themes/>
<table width="100%" align="center" border="0" cellpadding="0" cellspacing="0">
<tr>  
<td valign="top">
<form name="reportUserForm" method="post" action="/report/org_maintenance/reportuserlist.do">
	<br>
	<table width="70%" border="0" cellspacing="1"  align="center" cellpadding="1" class="ListTable">
		<thead>
			<tr>
				<td align="center" class="TableRow" nowrap>
				<bean:message key="userlist.select"/>&nbsp;
				</td>           
				<td align="center" class="TableRow" nowrap>
				<bean:message key="userlist.username"/>&nbsp;
				</td>
			</tr>
		</thead>
		<hrms:extenditerate id="element" name="reportUserForm" property="reportUserListForm.list"   
		indexes="indexes"  pagination="reportUserListForm.pagination" pageCount="15" scope="session">
			<tr class="trShallow">
				<td align="center" class="RecordRow" nowrap>
					<logic:equal name="element" property="string(userflag)" value="1">
						<hrms:checkmultibox name="reportUserForm" property="reportUserListForm.select"  value="false" indexes="indexes"/>&nbsp;
					</logic:equal>  
					<logic:equal name="element" property="string(userflag)" value="0">
						<hrms:checkmultibox name="reportUserForm" property="reportUserListForm.select"  value="true" indexes="indexes"/>&nbsp;
					</logic:equal> 			   
				</td>            
				<td align="left" class="RecordRow" nowrap>
					<bean:write name="element" property="string(username)" filter="false"/>&nbsp;
				</td>
			</tr>
		</hrms:extenditerate>
	</table>

	<table width="60%" align="center">
		<tr>
			<td valign="bottom" class="tdFontcolor">
				<bean:message key="label.page.serial" />
				<bean:write name="reportUserForm" property="reportUserListForm.pagination.current" filter="true" />
				<bean:message key="label.page.sum" />
				<bean:write name="reportUserForm" property="reportUserListForm.pagination.count" filter="true" />
				<bean:message key="label.page.row" />
				<bean:write name="reportUserForm" property="reportUserListForm.pagination.pages" filter="true" />
				<bean:message key="label.page.page" />
			</td>
			<td align="right" nowrap class="tdFontcolor">
				<p align="right">
					<hrms:paginationlink name="reportUserForm" property="reportUserListForm.pagination" nameId="reportUserListForm">
					</hrms:paginationlink>
			</td>
		</tr>
	</table>
	
	<table  width="70%" align="center">
		<tr>
			<td align="center">		
				<input type="submit" name="b_save_user" value="<bean:message key='userlist.save'/>" class="mybutton">                
				<input type="submit" name="b_return_user" value="<bean:message key='userlist.cancel'/>" class="mybutton">
			</td>
		</tr>          
	</table>
</form>
</td>
</tr>
</table>
