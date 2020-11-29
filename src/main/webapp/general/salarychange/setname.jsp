<%@ page contentType="text/html; charset=UTF-8" language="java"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ page import="com.hrms.struts.valueobject.UserView"%>
<%@ page import="com.hrms.struts.constant.WebConstant"%>
<%
    // 在标题栏显示当前用户和日期 2004-5-10 
    String userName = null;
    String css_url="/css/css1.css";
	UserView userView=(UserView)session.getAttribute(WebConstant.userView);
	if(userView != null)
	{
	  userName = userView.getUserFullName();
	  css_url=userView.getCssurl();
	  if(css_url==null||css_url.equals(""))
	  	css_url="/css/css1.css";
	}
	//String date = DateStyle.getSystemDate().getDateString();
%>

<script LANGUAGE=javascript src="/js/xtree.js"></script> 
<script LANGUAGE=javascript src="/js/constant.js"></script> 
<hrms:themes></hrms:themes>
<link href="/css/xtree.css" rel="stylesheet" type="text/css">
<html>
  <head>
  <title><bean:message key='hire.zp_persondb.username'/>：<%=userName%></title>
  </head>
  <body>
  <br>
    <table border="0" align="center">
    	<tr> 
    		<td><bean:message key='workdiary.message.input.formula.group.name'/></td>
    	</tr>
    	<tr> 
    		<td><input type="text" class="text4" name="name" size="35"></td>
    	</tr>
    	<tr> 
    		<td align="center">
    			<input type="button" value="<bean:message key='reporttypelist.confirm'/>" onclick="getName();" Class="mybutton">&nbsp;&nbsp;&nbsp;&nbsp;
    			<input type="button" value="<bean:message key='kq.register.kqduration.cancel'/>" onclick="window.close();" Class="mybutton">
    		</td>
    	</tr>
    </table>
  </body>
</html>
<script language="javascript">
function getName(){
	var name = document.getElementById("name").value;
	if(name!=null&&name.length>0){
		window.returnValue=name;
		window.close();
	}else{
		window.close();
	}
} 
</script>
