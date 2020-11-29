<%@page import="org.apache.commons.lang.StringUtils"%>
<%@page import="com.hjsj.hrms.actionform.general.approve.personinfo.ApprovePersonForm"%>
<%@ page contentType="text/html; charset=UTF-8" language="java"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>

<script language="JavaScript" src="/js/validate.js"></script>
<script language="JavaScript" src="/js/function.js"></script>
<%
ApprovePersonForm approvePersonForm = (ApprovePersonForm)session.getAttribute("approvePersonForm");
String returnflag = request.getParameter("returnflag");
//如果链接中此参数为空则把form中此参数清空
if(StringUtils.isEmpty(returnflag))
	approvePersonForm.setReturnflag("");
%>
<SCRIPT language=JavaScript>
function test(name) {
   
   if(name!=null&&name!=""&&name!="undefined"&&name.length>0) {	
      var obj=$('aaa');
      obj.setSelectedTab(name);
   }
} 
</SCRIPT>
<hrms:themes />
<html:form action="/general/approve/personinfo/sum">
<!-- 单位，职位信息维护暂时去掉b,k -->
<div id="tablesetdiv" >
	<logic:notEqual name="approvePersonForm" property="inputchinfor" value="1">
	<hrms:tabset   height="100%" name="aaa" type="true">
		<hrms:tab name="a" label="按状态审核" visible="true"  url="/general/approve/personinfo/orgstats.do">     
		</hrms:tab>
		<hrms:tab name="b"  label="按子集审核" visible="true"  url="/general/approve/personinfo/sumre.do?b_query=link&abkflag=a">     
		</hrms:tab>
	</hrms:tabset>
	</logic:notEqual>
	<logic:equal name="approvePersonForm" property="inputchinfor" value="1">
		<SCRIPT language=JavaScript> 
			window.location.href="/general/approve/personinfo/showapprove.do?b_query=link&action=approve.do&target=mil_body&nmodule=4";
		</SCRIPT>
	</logic:equal>
</div>
</html:form>
<script>
  

	
</script>

    