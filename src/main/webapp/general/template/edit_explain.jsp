<%@page import="java.util.Date"%>
<%@page import="com.hrms.struts.constant.WebConstant"%>
<%@page import="com.hrms.struts.valueobject.UserView"%>
<%@ page contentType="text/html; charset=UTF-8"%>

<%@ taglib uri="/tags/struts-bean" prefix="bean"%>
<%@ taglib uri="/tags/struts-html" prefix="html"%>
<%@ taglib uri="/tags/struts-logic" prefix="logic"%>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms"%>
<%@ taglib uri="/WEB-INF/tlds/FCKeditor.tld" prefix="fck"%>
<%
UserView userView=(UserView)session.getAttribute(WebConstant.userView);
if(userView != null){
	userView.getHm().put("fckeditorAccessTime", new Date().getTime());
}
%>
<script language="javascript">

    function showView() {
       var oEditor = FCKeditorAPI.GetInstance('FCKeditor1');
       var oldInputs = document.getElementsByName('content');
       if(oEditor.EditorDocument.body.innerText=="")
       {oldInputs[0].value="";}
       else
       oldInputs[0].value = oEditor.GetXHTML(true);
    }   
</script>
<script type="text/javascript" src="/fckeditor/fckeditor.js"></script>
<html:form action="/general/template/search_module">
	<table width="100%" border="0" cellpadding="0" cellspacing="0" align="center" class="ListTableF">	
		<tr height="30">
			<td align="center" class="TableRow" >
				<bean:message key="template.operation.explain" />
			</td>
		</tr>
		<tr>
			<td class="framestyle">
				<br>
				<table border="0" cellpmoding="0" cellspacing="0" class="DetailTable" cellpadding="0" width="100%">
					<tr>
						<td height="10"></td>
					</tr>
					<tr class="list3">				
						<td align="left"  nowrap>
                            <html:textarea name="templateForm" property="content" cols="80" rows="20" style="display:none;" />
                            <!-- 
							<script type="text/javascript">
					              var oldInputs = document.getElementsByName('content'); 					              
					              var oFCKeditor = new FCKeditor( 'FCKeditor1' ) ;
					              oFCKeditor.BasePath	= '/fckeditor/';
					              oFCKeditor.Height	= 500 ;
					              oFCKeditor.ToolbarSet='Middle';					             
					              oFCKeditor.Value	= oldInputs[0].value;
					              oFCKeditor.Create() ;
                            </script>
                            -->
			<fck:editor id="FCKeditor1" basePath="${contextPath}/fckeditor/"
				toolbarSet="Middle"
				imageBrowserURL="${contextPath}/fckeditor/editor/filemanager/browser/default/browser.html?Type=Image&Connector=${contextPath}/fckeditor/editor/filemanager/browser/default/connectors/jsp/connector"
				linkBrowserURL="${contextPath}/fckeditor/editor/filemanager/browser/default/browser.html?Connector=${contextPath}/fckeditor/editor/filemanager/browser/default/connectors/jsp/connector"
				flashBrowserURL="${contextPath}/fckeditor/editor/filemanager/browser/default/browser.html?Type=Flash&Connector=${contextPath}/fckeditor/editor/filemanager/browser/default/connectors/jsp/connector"
				imageUploadURL="${contextPath}/fckeditor/editor/filemanager/uploa/simpleuploader?Type=Image"
				linkUploadURL="${contextPath}/fckeditor/editor/filemanager/upload/simpleuploader?Type=File"
				flashUploadURL="${contextPath}/fckeditor/editor/filemanager/upload/simpleuploader?Type=Flash"
				autoDetectLanguage="false"
				defaultLanguage="zh-cn" height="500">
				
                <bean:write name="templateForm" property="content" filter="false"/>
			</fck:editor>                            
						</td>
					</tr>
					<tr>
						<td height="10"></td>
					</tr>					
					<tr class="list3">
					<tr>
						<td height="10"> <html:hidden name="templateForm" property="tabid" styleClass="text"/>  </td>	
					<tr>
						<td height="10"></td>
					</tr>
				</table>
			</td>
		</tr>
	</table>
	<div style="margin-top:5px;" align="center">
		<hrms:submit styleClass="mybutton" property="b_save" onclick="showView();">
         		<bean:message key="button.save"/></hrms:submit>			 			
      	<hrms:submit styleClass="mybutton" property="br_return">
         		<bean:message key="button.return"/></hrms:submit>		
	</div>
</html:form>
