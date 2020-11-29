<%@ page contentType="text/xml; charset=UTF-8"%>
<%@ page language="java" %>

<%@ page import="com.hjsj.hrms.interfaces.hire.OrganizationByXml"%>
<%@ page import="com.hrms.struts.exception.*" %>
<%@ page import="com.hrms.struts.valueobject.UserView"%>
<%@ page import="com.hrms.struts.constant.WebConstant"%>
<%

	UserView userView=(UserView)session.getAttribute(WebConstant.userView);
	String flag=(String)userView.getHm().get("gradeFashion");   //1:下拉框方式  2：平铺方式
	//request.getRequestDispatcher("/selfservice/performance/batchGrade.do?b_query=link" );
%>

<script language='javascript'>
	var flag=<%=flag%>
	if(flag==1)
		document.location='/selfservice/performance/batchGrade.do?b_query=link';
	else
	{
		
		document.location='/selfservice/performance/batchGrade.do?b_tileFrame=link';

	}
</script>