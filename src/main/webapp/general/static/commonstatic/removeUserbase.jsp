<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@page import="com.hjsj.hrms.actionform.stat.StatForm"%>
<%@ page import="com.hrms.struts.valueobject.UserView"%>
<%@ page import="com.hrms.struts.constant.WebConstant"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
  <head>   
  	<%
  	StatForm statForm = (StatForm)session.getAttribute("statForm");
  	statForm.setUserbase("");
  	UserView userView=(UserView)session.getAttribute(WebConstant.userView);
    String url="";
    if(userView != null)
    {
       url=userView.getBosflag();
    }
  	%>
  	<hrms:themes />
	<script type="text/javascript">
		function send(){
			var form = document.getElementsByTagName("form")[0];
			<%if("bi".equals(url)){%>
         		form.target = '_self';
		    <%}else if("hl".equals(url)){%>
		        form.target = 'il_body';
		    <%}else if("hcm".equals(url)){%>
		        form.target = 'il_body';
		    <%}%>
			form.submit();
		}
	</script>
  </head>
  
  <body onload="send();">
    <!-- liuy 2014-11-20 将bi里的的统计项配置方法配到hr或者hcm里面，会出现返回错误 start-->
    <%if("bi".equals(url)){%>
         <html:form action="/templates/index/bi_portal.do?b_query=link"></html:form>
    <%}else if("hl".equals(url)){%>
    	 <html:form action="/templates/index/portal.do?b_query=link"></html:form>
    <%}else if("hcm".equals(url)){%>
    	 <html:form action="/templates/index/hcm_portal.do?b_query=link"></html:form>
    <%}%>
  	<!-- liuy 2014-11-20 end -->
  </body>
</html>
