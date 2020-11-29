<%@ page contentType="text/html; charset=UTF-8"%>
<%@page
	import="com.hjsj.hrms.actionform.general.impev.ImportantEvForm,
	com.hrms.struts.constant.WebConstant,
	com.hrms.struts.valueobject.UserView,
	java.util.List,com.hrms.frame.dao.RecordVo"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean"%>
<%@ taglib uri="/tags/struts-html" prefix="html"%>
<%@ taglib uri="/tags/struts-logic" prefix="logic"%>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms"%>
<%
	UserView userView=(UserView)session.getAttribute(WebConstant.userView);
	String hcmflag="";
	if(userView != null){
	  hcmflag=userView.getBosflag();
	}
	
%>
<script type="text/javascript" src="/general/impev/important.js"></script>

<html:form action="/general/impev/importantev">
<%if("hl".equals(hcmflag)){ %>
<br><br>
<table align="center" width="600" border="0" cellpadding="0" cellspacing="0">
<%}else{ %>
<table align="center" width="700" border="0" cellpadding="0" cellspacing="0" style="margin-top:60px">
<%} %>
			<tr height="10">
				<!--  <td width=10 valign="top" class="tableft"></td>
				<td width=130 align=center class="tabcenter">
					&nbsp;报告内容&nbsp;
				</td>
				<td width=10 valign="top" class="tabright"></td>
				<td valign="top" class="tabremain" width="660"></td>-->
				<td  align=center class="TableRow">
					&nbsp;报告内容&nbsp;
				</td>
			</tr>
			<tr>
				<td class="framestyle9" 
					align="center" valign="top" width="600" height="300">
					<br>
					<table>
						<tr>
							<td width="600">
								<bean:write name="importantEvForm" property="content" filter="false"/>
							</td>
						</tr>			
					</table>
				</td>				
			</tr>
			<tr><td align="center" style="height:35px;"><INPUT type="button" value="<bean:message key="button.leave" />"
					Class="mybutton" onclick="back1();"></td></tr>
		</table>
</html:form>