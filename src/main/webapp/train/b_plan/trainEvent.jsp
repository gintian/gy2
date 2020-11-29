<%@page import="java.util.Date"%>
<%@page import="com.hrms.struts.constant.WebConstant"%>
<%@page import="com.hrms.struts.valueobject.UserView"%>
<%@ page contentType="text/html; charset=UTF-8" language="java"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%
UserView userView=(UserView)session.getAttribute(WebConstant.userView);
if(userView != null){
	userView.getHm().put("fckeditorAccessTime", new Date().getTime());
}
%>
<script type="text/javascript" src="/fckeditor/fckeditor.js"></script>
<html:form action="/train/traincourse/traindata">
<table width="100%" border="0" cellspacing="0"  align="center" cellpadding="0">
<tr><td>
	<html:textarea name="trainCourseForm" styleId="contentEv"
		property='contentEv' rows="3" cols="60" style="display=none" />&nbsp;&nbsp;&nbsp;&nbsp; 
   <script type="text/javascript">
		var oldInputs = document.getElementById('contentEv'); 
					                                         
		var oFCKeditor = new FCKeditor('FCKeditor1') ;		             
		oFCKeditor.BasePath	= '/fckeditor/';
        oFCKeditor.Height	= 400 ;			
		oFCKeditor.Width	="100%";			            
		oFCKeditor.ToolbarSet="Default";
		oFCKeditor.Value	= oldInputs.value;
		oFCKeditor.Create() ;			            
   </script>
</td></tr>
<tr><td align="center" height="40">
	<input type="button" class="mybutton" value="<bean:message key="button.close"/>" onclick="window.top.close();"/>
</td></tr>
</table>
</html:form>
