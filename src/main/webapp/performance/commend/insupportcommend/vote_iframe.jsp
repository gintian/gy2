<%@ page contentType="text/html; charset=UTF-8"%>
<%@page import="com.hjsj.hrms.utils.PubFunc"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%String sSourceURL = (String)request.getParameter("src");
//url特殊字符还原处理 xiaoyun 2014-9-5 start
sSourceURL = PubFunc.hireKeyWord_filter_reback(sSourceURL);
//url特殊字符还原处理 xiaoyun 2014-9-5 end
sSourceURL=sSourceURL.replaceAll("`","&");
%>
<html>
<body>
<iframe name="childFrame" id="childFrame" height="100%" width="100%" src="<%=sSourceURL%>"></iframe>
</body>
</html> 