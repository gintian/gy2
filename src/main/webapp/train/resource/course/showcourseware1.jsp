<%@page import="java.util.Date"%>
<%@page import="com.hrms.struts.constant.WebConstant"%>
<%@page import="com.hrms.struts.valueobject.UserView"%>
<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean"%>
<%@ taglib uri="/tags/struts-html" prefix="html"%>
<%@ taglib uri="/tags/struts-logic" prefix="logic"%>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms"%>
<%
UserView userView=(UserView)session.getAttribute(WebConstant.userView);
if(userView != null){
	userView.getHm().put("fckeditorAccessTime", new Date().getTime());
}
%>
<script language="JavaScript" src="/js/validateDate.js"></script>
<script type="text/javascript" src="/fckeditor/fckeditor.js"></script>
<script language="javascript" src="/ajax/constant.js"></script>
<script language="javascript" src="/ajax/basic.js"></script>
<script language="javascript" src="/ajax/common.js"></script>
<script language="javascript" src="/ajax/command.js"></script>
<script type="text/javascript"
	src="/train/resource/course/courseTrain.js"></script>
<script language="JavaScript">
	function save(){
		coursewareForm.action="/train/resource/courseware.do?b_save1=link";
		coursewareForm.target="hidd";
		coursewareForm.submit();
		window.close();
	}
</script>
<body>
<html:form action="/train/resource/courseware"
	enctype="multipart/form-data" target="hidd">
	<center>
	<br/>
	<div style="width: 95%;">
	<html:hidden name="coursewareForm" property="r5100" />
	<html:textarea name="coursewareForm" property="r5115" styleId="content1"></html:textarea>
	<script language="JavaScript">
		var oe = new FCKeditor('content1');
		oe.ToolbarSet="My1";
		oe.Height=333;
        oe.ReplaceTextarea();
	</script>
	</div>
	<br />
	<logic:notEqual value="1" name="coursewareForm" property="isParent">
	<input type="button" value="保存" class="mybutton" onclick="save()" />&nbsp;
	</logic:notEqual>
	<input type="button" value="关闭" class="mybutton" onclick="window.close();" />
	</center>
</html:form>
<iframe id="hidd" name="hidd" style="display: none;"></iframe>
</body>
