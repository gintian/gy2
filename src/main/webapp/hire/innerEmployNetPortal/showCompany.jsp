 <%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ page import="com.hrms.struts.valueobject.UserView"%>
<%@ page import="com.hrms.struts.constant.WebConstant"%>
<html>
<head>

</head>
<body>
<LINK href="/css/hireNetStyle.css" type=text/css rel=stylesheet>
<html:form action="/hire/hireNetPortal/search_zp_position"> 
<div style="padding: 10px;">
<table class="ListTable">
    <tr class="">
        <td class="tableRow">单位介绍</td>
    </tr>
    <tr>
        <td class="RecordRow">
        <div style="width: 970px;height: 400px;overflow: auto;padding-top: 5px;">
	        ${employPortalForm.info}
        </div>
        </td>
    </tr>
    <tr>
        <td align="center" class="RecordRow" style="padding-top: 5px;padding-bottom: 5px;"><input type="button" onclick="javascript:window.close();" value="关闭" class="mybutton"/></td>
    </tr>
</table></div>
</html:form>    
</body>

</html>
