
<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ page import="com.hrms.struts.constant.SystemConfig"%>
<% 
	    String url_p=SystemConfig.getCsClientServerURL(request); 
%>
<html>
<head>
<script src="/general/sys/hjaxmanage.js"></script>
<script type="text/javascript">
    function doload()
    {
        if(AxManager.checkBrowserSettings('<%=url_p%>', 'ax')){
    		window.location.href="/system/apply_enter_forward.jsp";
    	}
    }
</script>
</head>
<body onload="doload();">
<div style="display:none;">
<script type="text/javascript">
	AxManager.write("ax", 0, 0);
</script>
</div>
</body>
</html>
