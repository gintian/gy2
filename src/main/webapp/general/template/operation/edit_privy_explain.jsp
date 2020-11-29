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
       oldInputs[0].value = oEditor.GetXHTML(true);
    }   
</script>
<script type="text/javascript" src="/fckeditor/fckeditor.js"></script>
<html:form action="/general/template/operation/privy_explain">
	
	<table width="100%" border="0" cellpadding="0" cellspacing="0" align="center">		
		<tr height="20">
			<td align="left" class="TableRow">
				<bean:message key="template.operation.privy_explain.out" />
			</td>
		</tr>
					<tr >				
						<td align="left"  nowrap>
                            <html:textarea name="abroadForm" property="content" cols="80" rows="20" style="display:none;" />
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
			<fck:editor id="FCKeditor1" basePath="/fckeditor/"
				toolbarSet="Middle"
				imageBrowserURL="/fckeditor/editor/filemanager/browser/default/browser.html?Type=Image&Connector=connectors/jsp/connector"
				linkBrowserURL="/fckeditor/editor/filemanager/browser/default/browser.html?Connector=connectors/jsp/connector"
				flashBrowserURL="/fckeditor/editor/filemanager/browser/default/browser.html?Type=Flash&Connector=connectors/jsp/connector"
				imageUploadURL="/fckeditor/editor/filemanager/upload/simpleuploader?Type=Image"
				linkUploadURL="/fckeditor/editor/filemanager/upload/simpleuploader?Type=File"
				flashUploadURL="/fckeditor/editor/filemanager/upload/simpleuploader?Type=Flash" height="500">
				
                <bean:write name="abroadForm" property="content" filter="false"/>
			</fck:editor>                            
						</td>
					</tr>
					<tr>
						<td height="10"> <html:hidden name="abroadForm" property="tabid" styleClass="text"/>  </td>	
					</tr>

		<tr class="list3">
			<td align="center">	
         	 	<hrms:submit styleClass="mybutton" property="b_save" onclick="showView();">
            		<bean:message key="button.save"/>
	 		 	</hrms:submit>			 			
         	 	<hrms:submit styleClass="mybutton" property="br_return">
            		<bean:message key="button.return"/>
	 		 	</hrms:submit>			 			
			</td>
		</tr>
	</table>
</html:form>
