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
<script language="javascript">
function save(){
   var oEditor = FCKeditorAPI.GetInstance('FCKeditor1');
   var oldInputs = document.getElementById("contentEv");
   var tmpvalue=oEditor.GetXHTML(true);
	while(tmpvalue.indexOf("&nbsp;")!=-1)
		tmpvalue=tmpvalue.replace("&nbsp;","?");
   oldInputs.value = tmpvalue;
   var urlstr = "/train/traincourse/traindata.do?b_event=link&id=${trainCourseForm.contentid}"; 
   urlstr +="&wherestr=${trainCourseForm.wheresql}&flag=save&tablename=${trainCourseForm.tablename}&classid=${trainCourseForm.classid}";
   trainCourseForm.action=urlstr;
   trainCourseForm.submit();
}
</script>
<html:form action="/train/traincourse/traindata">
<logic:equal name="trainCourseForm" property="readonly" value="1">
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

	<input type="button" class="mybutton" value="<bean:message key="button.save"/>" onclick="save();"/>
	<input type="button" class="mybutton" value="<bean:message key="button.cancel"/>" onclick="window.top.close();"/>
</td></tr>
</table>
</logic:equal>
<logic:notEqual name="trainCourseForm" property="readonly" value="1">
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
</logic:notEqual>
</html:form>
