<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ page import="com.hrms.struts.valueobject.UserView"%>
<%@ page import="com.hrms.struts.constant.WebConstant"%>
 <%
	UserView userView=(UserView)session.getAttribute(WebConstant.userView);
	String hcmflag="";
	if(userView != null){
	  hcmflag=userView.getBosflag();
	}
%>
<script language="Javascript" src="/gz/salary.js"></script>
<html:form action="/gz/gz_accountingt/bankdisk/selectFileType">
<%if("hl".equals(hcmflag)){ %>
<br>
<%}%>
<table width='290px;' border="0" cellspacing="0"  align="center" cellpadding="0" class="listTable">
<tr>
<td>
<fieldset>
<legend><bean:message key="gz.bankdisk.updisk"/></legend>
<table width='100%' border="0" cellspacing="0"  align="center" cellpadding="0" class="listTable">
<tr><td align="left">
<input type="radio" name="fileType" value="0"><font size="2"><bean:message key="gz.bankdisk.tablefile"/></font>
</td></tr>
<tr><td align="left">
<input type="radio" name="fileType" value="1"><font size="2"><bean:message key="gz.bankdisk.blankfile"/></font>
</td></tr>
<tr><td align="left">
<input type="radio" name="fileType" value="2" checked><font size="2"><bean:message key="gz.bankdisk.noseparatorfile"/></font>
</td></tr>
<tr><td align="left"> 
<input type="radio" name="fileType" value="4"><font size="2"><bean:message key="gz.bankdisk.shufile"/></font>
</td></tr>
<tr><td align="left"> 
<input type="radio" name="fileType" value="5"><font size="2"><bean:message key="gz.bankdisk.doufile"/></font>
</td></tr>
<tr><td align="left"> 
<input type="radio" name="fileType" value="3"><font size="2"><bean:message key="gz.bankdisk.excelfile"/></font>
</td></tr>
</table>
</fieldset>
</td>
</tr>
</table>

<table width='290px;' border="0" cellspacing="1"  align="center" cellpadding="1">
<tr>
<td align="center">
<input type="button" name="ok" value="<bean:message key="button.ok"/>" class="mybutton" onclick="bankdisk_selectFileType();">
<input type="button" name="cancel" value="<bean:message key="button.cancel"/>" class="mybutton" onclick="window.close();">

</td>
</tr>
</table>
</html:form>