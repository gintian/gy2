<%@ page language="java" contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%-- <%@ page import="com.hjsj.hrms.module.recruitment.recruitprocess.actionform.ConfirmInvitationForm"%> --%>
<%@ page import="com.hjsj.hrms.actionform.hire.employNetPortal.EmployPortalForm"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>确认结果</title>
<script type="text/javascript">
function CloseWin() //这个不会提示是否关闭浏览器    
{    
window.opener=null;    
window.open("","_self");    
window.close();    
}
</script>
<style type="text/css">
.divstyle{
width:60%;
height:60%;
font:normal bold 20px/20px arial,sans-serif;
text-align:center;
padding:20% 20% 2% 20%;
border:1px;
}
.close{
text-align:center;
}
</style>
</head>
<body>
<div class="divstyle">
<logic:equal name="employPortalForm" property="message" value="1">
确认成功！
</logic:equal>
<logic:equal name="employPortalForm" property="message" value="0">
拒绝成功！
</logic:equal>
<logic:equal name="employPortalForm" property="message" value="2">
您已处理过此信息！
</logic:equal>
</div>
<div class="close">
<a href="javascript:CloseWin()">关闭窗口</a>
</div>
</body>
</html>