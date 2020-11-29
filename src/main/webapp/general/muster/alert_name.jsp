<%@ page contentType="text/html; charset=UTF-8" language="java"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ page import="com.hrms.struts.valueobject.UserView"%>
<%@ page import="com.hrms.struts.constant.WebConstant"%>
<%@ page import="com.hrms.frame.codec.SafeCode"%>
<%
    // 在标题栏显示当前用户和日期 2004-5-10 
    String css_url="/css/css1.css";
	UserView userView=(UserView)session.getAttribute(WebConstant.userView);
	if(userView != null){
	  css_url=userView.getCssurl();
	  if(css_url==null||css_url.equals(""))
	  	 css_url="/css/css1.css";
	}
	String musgername = (String)request.getParameter("mustername");
	musgername=musgername!=null?SafeCode.decode(musgername):"";
%> 
<html>
<head>
<hrms:themes></hrms:themes>
<link href="/css/xtree.css" rel="stylesheet" type="text/css">
</head>
<SCRIPT LANGUAGE="javascript">
function reMusterOk(){
	var mustername = document.getElementById("musername").value;
	window.returnValue=mustername;
	window.close();
}
</SCRIPT>
<body>
<table border="0" align="center" width="100%">
<tr><td>&nbsp;</td></tr>
<tr><td>
 <fieldset  align="center" style="width:96%;height=100">
	 <legend><bean:message key="button.rename"/></legend> 
		<table width="100%" border="0">
        	<tr height="30">
            	<td align="center"><bean:message key="column.name"/>:<input type="text" size="25" maxlength="30" name="musername" value="<%=musgername%>"></td>
            </tr>
            <tr height="30">
            	<td align="center"><input type="button" value="<bean:message key='lable.tz_template.enter'/>" onclick="reMusterOk();" Class="mybutton">&nbsp;&nbsp;&nbsp;&nbsp;
            		<input type="button" value="<bean:message key='lable.tz_template.cancel'/>" onclick="window.close();" Class="mybutton">
            	</td>
            </tr>
         </table>
	</fieldset>
</td></tr>
</table>
</body>
</html>

