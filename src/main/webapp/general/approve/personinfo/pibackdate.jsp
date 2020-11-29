<%@ page contentType="text/html; charset=UTF-8" language="java"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ page import="com.hrms.struts.valueobject.UserView"%>
<%@ page import="com.hrms.struts.constant.WebConstant,java.text.SimpleDateFormat,java.util.Date"%>
<%
    // 在标题栏显示当前用户和日期 2004-5-10 
    String css_url="/css/css1.css";
	UserView userView=(UserView)session.getAttribute(WebConstant.userView);
	if(userView != null){
	  css_url=userView.getCssurl();
	  if(css_url==null||css_url.equals("")) 
	  	 css_url="/css/css1.css";
          //out.println("<link href='"+css_url+"' rel='stylesheet' type='text/css' >");  	 
	}
%>
<hrms:themes></hrms:themes>
<script language="JavaScript" src="/js/function.js"></script>
<script type="text/javascript" src="/js/validateDate.js"></script>
<script type="text/javascript">

function clickok(){//update by xiegh on date20171125 修改自助服务-员工信息-信息审核：浏览器兼容问题：批准按钮
	var contentObj=document.getElementById("content");
	var  arrays = new Array();
	arrays.push('<%=request.getParameter("flag")%>');
	arrays.push('<%=request.getParameter("boxstr")%>');
	arrays.push(contentObj.value);
	parent.window.opener['<%=request.getParameter("callback")%>'](arrays);
	parent.window.close(); 
}
</script>
<html:form action="/org/orginfo/searchorgtree"> 
  <table width="455" border="0" cellspacing="0"  align="center" cellpadding="0"  > 
  <tr><td colspan="2">批示：</td></tr>
  <tr>
  	<td align="left"><textarea rows="19" cols="62" style="width: 394px;" id="content" ></textarea> </td>
  	<td align="center" valign="top">
  		<input name="ok" type="button" style="margin-bottom: 20px;" class="mybutton" value="<bean:message key="button.ok"/>" onclick="clickok()"/>
  		<input name="cancel" type="button" class="mybutton" value="<bean:message key="button.cancel"/>" onclick="parent.window.close();"/>
  	</td>
  </tr>    
   </table>
</html:form>
