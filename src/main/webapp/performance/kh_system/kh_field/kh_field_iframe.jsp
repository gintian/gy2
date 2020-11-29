<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ page import="com.hrms.struts.valueobject.UserView"%>
<%@ page import="com.hrms.struts.constant.WebConstant"%>
<%@ page import="com.hrms.frame.utility.DateStyle"%>
<%
    // 在标题栏显示当前用户和日期 2004-5-10 
    String userName = null;
    String css_url="/css/css1.css";    
	UserView userView=(UserView)session.getAttribute(WebConstant.userView);
	if(userView != null){
	  userName = userView.getUserFullName();
    if(css_url==null||css_url.equals(""))
 	  css_url="/css/css1.css";
  
	}
	String date = DateStyle.getSystemDate().getDateString();
%>
<html>
<head>
<title>　用户名：<%=userName%>　当前日期：<%=date%></title>
<hrms:themes />
   <script language="javascript" src="/js/validate.js"></script>   
   <script language="javascript" src="/js/dict.js"></script>   
  <script type="text/javascript">
  </script>
</head>
<html:form action="/performance/kh_system/kh_field/init_kh_field">
<frameset rows="*,150" border="1" frameborder="yes" name="ril_body" scrolling="auto">
  <frame src="/performance/kh_system/kh_field/init_kh_field.do?b_query=link&pointsetid=${khFieldForm.pointsetid}&subsys_id=${khFieldForm.subsys_id}" frameborder="1" name="i_top" scrolling="auto">
  <frame src="/performance/kh_system/kh_field/init_kh_field.do?b_query=link&pointsetid=${khFieldForm.pointsetid}&subsys_id=${khFieldForm.subsys_id}" frameborder="1" name="i_body" scrolling="auto">
</frameset>
</html:form>
</html>
